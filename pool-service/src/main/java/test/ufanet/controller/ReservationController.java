package test.ufanet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.ufanet.dto.reservation.*;
import test.ufanet.service.ReservationService;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v0/pool/timetable")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/all")
    public ResponseEntity<List<ReservationDto>> getBusyHours(@RequestParam(name = "date")
                                                             @DateTimeFormat(iso = DateTimeFormat
                                                                     .ISO.DATE) LocalDate date) {
        log.info("Get busy hours for {}", date);
        return ResponseEntity.ok(reservationService.getBusyHours(date));
    }

    @GetMapping("/available")
    public ResponseEntity<List<ReservationDto>> getAvailableHours(@RequestParam(name = "date")
                                                                  @DateTimeFormat(iso = DateTimeFormat
                                                                          .ISO.DATE) LocalDate date) {
        log.info("Get available hours for {}", date);
        return ResponseEntity.ok(reservationService.getAvailableHours(date));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ReservationResponseDto>> getReservations(@RequestParam(name = "name") String name,
                                                                        @RequestParam(name = "date")
                                                                        OffsetDateTime date) {
        log.info("Get search reservations for {} | {}", name, date);
        return ResponseEntity.ok(reservationService.search(name, date));
    }

    @PostMapping("/reserve")
    public ResponseEntity<ReservationResponseDto> reserve(@RequestBody @Valid CreateReservationDto dto) {
        log.info("Reserve {}", dto);
        return new ResponseEntity<>(reservationService.reserve(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@RequestBody @Valid CancelReservationDto dto) {
        log.info("Delete reservation {}", dto);
        reservationService.deleteReservation(dto);
    }
}
