package test.ufanet.reservation.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import test.ufanet.common.exception.BadRequestException;
import test.ufanet.common.exception.ConflictException;
import test.ufanet.common.exception.NotFoundException;
import test.ufanet.dto.client.CreateClientDto;
import test.ufanet.dto.reservation.CancelReservationDto;
import test.ufanet.dto.reservation.CreateReservationDto;
import test.ufanet.dto.reservation.ReservationDto;
import test.ufanet.dto.reservation.ReservationResponseDto;
import test.ufanet.model.Client;
import test.ufanet.model.Reservation;
import test.ufanet.model.WorkingSchedule;
import test.ufanet.repository.ClientRepository;
import test.ufanet.repository.ReservationRepository;
import test.ufanet.repository.ScheduleExceptionRepository;
import test.ufanet.repository.WorkingScheduleRepository;
import test.ufanet.service.ReservationServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ScheduleExceptionRepository scheduleExceptionRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private WorkingScheduleRepository workingScheduleRepository;

    @InjectMocks
    private ReservationServiceImpl reservationServiceImpl;

    private Client client;
    private LocalDate date;
    private OffsetDateTime startTime;

    @BeforeEach
    void setUp() {
        client = Client.builder().id(1L).build();
        date = LocalDate.of(2025, 1, 1);
        startTime = date.atTime(10, 0).atOffset(ZoneOffset.UTC);
    }

    @Test
    @DisplayName("Должен зарезервировать время в бассейне")
    void shouldReserve() {
        WorkingSchedule workingSchedule = WorkingSchedule.builder()
                .id(3)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(21, 0))
                .build();

        Mockito.when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        Mockito.when(workingScheduleRepository.findByDow(3)).thenReturn(Optional.of(workingSchedule));
        Mockito.when(reservationRepository.existsForClientAndDay(client, date)).thenReturn(Boolean.FALSE);
        Mockito.when(reservationRepository.countByStartTime(startTime)).thenReturn(0);
        Mockito.when(reservationRepository.save(Mockito.any(Reservation.class))).thenAnswer(inv ->
                inv.getArgument(0));

        ReservationResponseDto reservation = reservationServiceImpl.reserve(CreateReservationDto.builder()
                .clientId(1L)
                .dateTime(startTime)
                .hours(2)
                .build());

        Assertions.assertThat(reservation).isNotNull();
        Assertions.assertThat(reservation.getOrderId()).isNotNull();
        Mockito.verify(reservationRepository,
                Mockito.times(2)).countByStartTime(Mockito.any());
        Mockito.verify(reservationRepository,
                Mockito.times(1)).save(Mockito.any(Reservation.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение, когда запись выходит за рабочее время")
    void shouldThrowBadRequestExceptionWhenOutsideWorkingTime() {
        WorkingSchedule workingSchedule = WorkingSchedule.builder()
                .id(3)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(18, 0))
                .build();

        Mockito.when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        Mockito.when(workingScheduleRepository.findByDow(3)).thenReturn(Optional.of(workingSchedule));

        Assertions.assertThatThrownBy(() -> reservationServiceImpl.reserve(CreateReservationDto.builder()
                        .clientId(1L)
                        .dateTime(startTime)
                        .hours(2)
                        .build()))
                .isInstanceOf(BadRequestException.class);
        Mockito.verify(clientRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(workingScheduleRepository, Mockito.times(1)).findByDow(3);
    }

    @Test
    @DisplayName("Должен выбросить исключение, когда клиент не найден")
    void shouldThrowNotFoundExceptionWhenClientNotFound() {
        Mockito.when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> reservationServiceImpl.reserve(CreateReservationDto.builder()
                        .clientId(1L)
                        .dateTime(startTime)
                        .hours(2)
                        .build()))
                .isInstanceOf(NotFoundException.class);
        Mockito.verify(clientRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Должен выбросить исключение, когда запись для клиента уже есть в этот день")
    void shouldThrowConflictExceptionWhenReservationForClientAlreadyExists() {
        Mockito.when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        Mockito.when(reservationRepository.existsForClientAndDay(client, date)).thenReturn(Boolean.TRUE);

        Assertions.assertThatThrownBy(() -> reservationServiceImpl.reserve(CreateReservationDto.builder()
                        .clientId(1L)
                        .dateTime(startTime)
                        .hours(2)
                        .build()))
                .isInstanceOf(ConflictException.class);
        Mockito.verify(clientRepository,
                Mockito.times(1)).findById(1L);
        Mockito.verify(reservationRepository,
                Mockito.times(1)).existsForClientAndDay(client, date);
    }

    @Test
    @DisplayName("")
    void shouldThrowConflictExceptionWhenReservationCapacityFull() {
        WorkingSchedule workingSchedule = WorkingSchedule.builder()
                .id(3)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(21, 0))
                .build();

        Mockito.when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        Mockito.when(workingScheduleRepository.findByDow(3)).thenReturn(Optional.of(workingSchedule));
        Mockito.when(reservationRepository.existsForClientAndDay(client, date)).thenReturn(Boolean.FALSE);
        Mockito.when(reservationRepository.countByStartTime(startTime)).thenReturn(10);

        Assertions.assertThatThrownBy(() -> reservationServiceImpl.reserve(CreateReservationDto.builder()
                        .clientId(1L)
                        .dateTime(startTime)
                        .hours(2)
                        .build()))
                .isInstanceOf(ConflictException.class);
        Mockito.verify(clientRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(workingScheduleRepository, Mockito.times(1)).findByDow(3);
        Mockito.verify(reservationRepository,
                Mockito.times(1)).existsForClientAndDay(client, date);
        Mockito.verify(reservationRepository, Mockito.times(1)).countByStartTime(startTime);
    }


    @Test
    @DisplayName("Должен удалить запись в бассейн")
    void shouldDeleteReservation() {
        UUID orderId = UUID.randomUUID();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .client(client)
                .startTime(startTime)
                .endTime(startTime.plusHours(1))
                .orderId(orderId)
                .createdAt(OffsetDateTime.now())
                .build();

        Mockito.when(reservationRepository
                .findByClient_IdAndOrderId(1L, orderId)).thenReturn(Optional.of(reservation));

        CancelReservationDto dto = CancelReservationDto.builder()
                .clientId(client.getId())
                .orderId(orderId)
                .build();

        reservationServiceImpl.deleteReservation(dto);

        Mockito.verify(reservationRepository, Mockito.times(1)).delete(reservation);
    }

    @Test
    @DisplayName("Должен получить занятые записи на определенный день в бассейне")
    void shouldGetBusyHours() {
        WorkingSchedule workingSchedule = WorkingSchedule.builder()
                .id(3)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(12, 0))
                .build();

        Mockito.when(workingScheduleRepository.findByDow(3)).thenReturn(Optional.of(workingSchedule));
        Mockito.when(reservationRepository.countByStartTime(Mockito.any()))
                .thenReturn(9)
                .thenReturn(2)
                .thenReturn(1)
                .thenReturn(0);

        List<ReservationDto> busyHours = reservationServiceImpl.getBusyHours(date);

        Assertions.assertThat(busyHours).hasSize(4);
        Assertions.assertThat(busyHours.get(0).getCount()).isEqualTo(9);
        Assertions.assertThat(busyHours.get(1).getCount()).isEqualTo(2);
        Assertions.assertThat(busyHours.get(2).getCount()).isEqualTo(1);
        Assertions.assertThat(busyHours.get(3).getCount()).isEqualTo(0);
        Mockito.verify(workingScheduleRepository,
                Mockito.times(1)).findByDow(Mockito.any(int.class));
        Mockito.verify(reservationRepository,
                Mockito.times(4)).countByStartTime(Mockito.any());
    }

    @Test
    @DisplayName("Должен получить все доступные записи на определенный день в бассейне")
    void shouldGetAvailableHours() {
        WorkingSchedule workingSchedule = WorkingSchedule.builder()
                .id(3)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(13, 0))
                .build();

        Mockito.when(workingScheduleRepository.findByDow(3)).thenReturn(Optional.of(workingSchedule));
        Mockito.when(reservationRepository.countByStartTime(Mockito.any()))
                .thenReturn(9)
                .thenReturn(2)
                .thenReturn(1)
                .thenReturn(10)
                .thenReturn(8);

        List<ReservationDto> availableHours = reservationServiceImpl.getAvailableHours(date);

        Assertions.assertThat(availableHours).hasSize(4);
        Assertions.assertThat(availableHours.get(0).getCount()).isEqualTo(1);
        Assertions.assertThat(availableHours.get(1).getCount()).isEqualTo(8);
        Assertions.assertThat(availableHours.get(2).getCount()).isEqualTo(9);
        Assertions.assertThat(availableHours.get(3).getCount()).isEqualTo(2);
        Mockito.verify(workingScheduleRepository,
                Mockito.times(1)).findByDow(Mockito.any(int.class));
        Mockito.verify(reservationRepository,
                Mockito.times(5)).countByStartTime(Mockito.any());

    }
}