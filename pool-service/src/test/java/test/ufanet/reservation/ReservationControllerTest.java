package test.ufanet.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import test.ufanet.controller.ReservationController;
import test.ufanet.dto.reservation.*;
import test.ufanet.model.Reservation;
import test.ufanet.service.ReservationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("Должен получить занятые записи в конкретный день")
    void shouldGetBusyHours() throws Exception {
        Mockito.when(reservationService.getBusyHours(LocalDate.of(2025, 5, 1)))
                .thenReturn(List.of(ReservationDto.builder()
                        .time(LocalDateTime.of(LocalDate.of(2025, 5, 1),
                                LocalTime.of(12, 0)).toString())
                        .count(5)
                        .build()));

        mvc.perform(MockMvcRequestBuilders.get("/api/v0/pool/timetable/all")
                        .param("date", "2025-05-01"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].time").value("2025-05-01T12:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].count").value(5));
    }

    @Test
    @DisplayName("Должен получить доступное время для записи в конкретный день")
    void shouldGetAvailableHours() throws Exception {
        Mockito.when(reservationService.getAvailableHours(LocalDate.of(2025, 5, 1)))
                .thenReturn(List.of(ReservationDto.builder()
                        .time(LocalDateTime.of(LocalDate.of(2025, 5, 1),
                                LocalTime.of(12, 0)).toString())
                        .count(7)
                        .build()));

        mvc.perform(MockMvcRequestBuilders.get("/api/v0/pool/timetable/available")
                        .param("date", "2025-05-01"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].time").value("2025-05-01T12:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].count").value(7));
    }

    @Test
    @DisplayName("Должен найти по имени клиента или дате запись в бассейн")
    void shouldSearchForReservations() throws Exception {
        UUID orderId = UUID.randomUUID();

        Mockito.when(reservationService.search(Mockito.any(String.class), Mockito.any(OffsetDateTime.class)))
                .thenReturn(List.of(ReservationResponseDto.builder()
                        .orderId(orderId)
                        .build()));

        mvc.perform(MockMvcRequestBuilders.get("/api/v0/pool/timetable/search")
                        .param("name", "Ivan")
                        .param("date", OffsetDateTime.now().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].orderId").value(orderId.toString()));
    }

    @Test
    @DisplayName("Должен зарезервировать время")
    void shouldReserve() throws Exception {
        CreateReservationDto createDto = CreateReservationDto.builder()
                .clientId(1L)
                .dateTime(OffsetDateTime.parse("2025-05-01T12:00:00Z"))
                .hours(1)
                .build();

        ReservationResponseDto responseDto = ReservationResponseDto.builder()
                .orderId(UUID.randomUUID())
                .build();

        Mockito.when(reservationService.reserve(createDto)).thenReturn(responseDto);

        mvc.perform(MockMvcRequestBuilders.post("/api/v0/pool/timetable/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.orderId").value(responseDto.getOrderId().toString()));
    }

    @Test
    @DisplayName("Должен удалить запись клиента")
    void shouldDeleteReservation() throws Exception {
        CancelReservationDto cancelDto = CancelReservationDto.builder()
                .clientId(1L)
                .orderId(UUID.randomUUID())
                .build();

        mvc.perform(MockMvcRequestBuilders.delete("/api/v0/pool/timetable/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cancelDto)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}