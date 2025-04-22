package test.ufanet.mapper;

import lombok.experimental.UtilityClass;
import test.ufanet.dto.reservation.ReservationDto;
import test.ufanet.dto.reservation.ReservationResponseDto;
import test.ufanet.model.Reservation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class ReservationMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ReservationDto toReservationDto(LocalDateTime dateTime, int count) {
        return ReservationDto.builder()
                .time(dateTime.format(DATE_TIME_FORMATTER))
                .count(count)
                .build();
    }

    public ReservationResponseDto toReservationResponseDto(Reservation reservation) {
        return ReservationResponseDto.builder()
                .orderId(reservation.getOrderId())
                .build();
    }
}
