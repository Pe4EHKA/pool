package test.ufanet.service;

import test.ufanet.dto.reservation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public interface ReservationService {

    ReservationResponseDto reserve(CreateReservationDto dto);

    List<ReservationResponseDto> search(String name, OffsetDateTime date);

    void deleteReservation(CancelReservationDto dto);

    List<ReservationDto> getBusyHours(LocalDate day);

    List<ReservationDto> getAvailableHours(LocalDate day);
}
