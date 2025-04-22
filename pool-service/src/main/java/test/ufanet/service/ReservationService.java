package test.ufanet.service;

import test.ufanet.dto.reservation.CancelReservationDto;
import test.ufanet.dto.reservation.CreateReservationDto;
import test.ufanet.dto.reservation.ReservationDto;
import test.ufanet.dto.reservation.ReservationResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {

    ReservationResponseDto reserve(CreateReservationDto dto);

    void deleteReservation(CancelReservationDto dto);

    List<ReservationDto> getBusyHours(LocalDate day);

    List<ReservationDto> getAvailableHours(LocalDate day);
}
