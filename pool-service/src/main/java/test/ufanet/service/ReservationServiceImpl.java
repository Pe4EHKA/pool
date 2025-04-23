package test.ufanet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.ufanet.common.exception.BadRequestException;
import test.ufanet.common.exception.ConflictException;
import test.ufanet.common.exception.NotFoundException;
import test.ufanet.dto.reservation.CancelReservationDto;
import test.ufanet.dto.reservation.CreateReservationDto;
import test.ufanet.dto.reservation.ReservationDto;
import test.ufanet.dto.reservation.ReservationResponseDto;
import test.ufanet.mapper.ReservationMapper;
import test.ufanet.model.Client;
import test.ufanet.model.Reservation;
import test.ufanet.model.ScheduleException;
import test.ufanet.model.WorkingSchedule;
import test.ufanet.repository.ClientRepository;
import test.ufanet.repository.ReservationRepository;
import test.ufanet.repository.ScheduleExceptionRepository;
import test.ufanet.repository.WorkingScheduleRepository;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    @Value("${spring.max-capacity}")
    private static final short MAX_CAPACITY = 10;

    private final ReservationRepository reservationRepository;
    private final ScheduleExceptionRepository scheduleExceptionRepository;
    private final WorkingScheduleRepository workingScheduleRepository;
    private final ClientRepository clientRepository;


    @Override
    @Transactional
    public ReservationResponseDto reserve(CreateReservationDto dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new NotFoundException("Client with id " + dto.getClientId() + " not found"));

        OffsetDateTime startTime = dto.getDateTime();
        if (startTime.getMinute() != 0 || startTime.getSecond() != 0) {
            throw new BadRequestException("Time must start at full hour");
        }
        if (dto.getHours() < 1) {
            throw new BadRequestException("Hours must be greater than 0");
        }

        LocalDate day = startTime.toLocalDate();

        if (reservationRepository.existsForClientAndDay(client, day)) {
            throw new ConflictException("Client with id: " + client.getId() +
                    " already has reservation for that day: " + day);
        }

        LocalTime[] interval = getWorkingTimes(day);
        LocalTime workStart = interval[0];
        LocalTime workEnd = interval[1];

        LocalTime reservationStart = startTime.toLocalTime();
        LocalTime reservationEnd = startTime.toLocalTime().plusHours(dto.getHours());

        if (reservationStart.isBefore(workStart) || reservationEnd.isAfter(workEnd)) {
            throw new BadRequestException("Requested time is outside working hours");
        }

        List<OffsetDateTime> preReservations = buildHoursSchedule(startTime, dto.getHours());
        for (OffsetDateTime preReservation : preReservations) {
            if (reservationRepository.countByStartTime(preReservation) >= MAX_CAPACITY) {
                throw new ConflictException("No free places at time " + preReservation.toLocalDateTime());
            }
        }

        Reservation reservation = Reservation.builder()
                .client(client)
                .startTime(startTime)
                .endTime(startTime.plusHours(dto.getHours()))
                .build();

        return ReservationMapper.toReservationResponseDto(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public void deleteReservation(CancelReservationDto dto) {
        Reservation reservation = reservationRepository.findByClient_IdAndOrderId(dto.getClientId(), dto.getOrderId())
                .orElseThrow(() -> new NotFoundException("Reservation with client id " + dto.getClientId() +
                        " and order id " + dto.getOrderId() + " not found"));
        reservationRepository.delete(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getBusyHours(LocalDate day) {
        LocalTime[] interval = getWorkingTimes(day);

        List<OffsetDateTime> hours = buildHoursSchedule(day.atTime(interval[0]).atOffset(ZoneOffset.UTC),
                (int) Duration.between(interval[0], interval[1]).toHours());

        return hours.stream()
                .map(reservation -> ReservationMapper.toReservationDto(
                        reservation.toLocalTime().atDate(day),
                        reservationRepository.countByStartTime(reservation)
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationDto> getAvailableHours(LocalDate day) {
        return getBusyHours(day).stream()
                .map(dto -> ReservationDto.builder()
                        .time(dto.getTime())
                        .count(MAX_CAPACITY - dto.getCount())
                        .build())
                .filter(dto -> dto.getCount() > 0)
                .toList();
    }


    private LocalTime[] getWorkingTimes(LocalDate day) {
        Optional<ScheduleException> scheduleExceptionOpt = scheduleExceptionRepository.findById(day);
        if (scheduleExceptionOpt.isPresent()) {
            ScheduleException scheduleException = scheduleExceptionOpt.get();
            if (!scheduleException.getIsWorking()) {
                throw new BadRequestException("Day " + day + " is not working. Pool is closed");
            }
            return new LocalTime[]{scheduleException.getStartTime(), scheduleException.getEndTime()};
        }

        int dow = day.getDayOfWeek().getValue();
        WorkingSchedule workingSchedule = workingScheduleRepository.findByDow(dow)
                .orElseThrow(() -> new NotFoundException("Working schedule not configured"));
        return new LocalTime[]{workingSchedule.getStartTime(), workingSchedule.getEndTime()};
    }

    private List<OffsetDateTime> buildHoursSchedule(OffsetDateTime start, int hours) {
        return IntStream.range(0, hours)
                .mapToObj(start::plusHours)
                .toList();
    }
}

