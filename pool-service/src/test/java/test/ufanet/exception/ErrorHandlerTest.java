package test.ufanet.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import test.ufanet.common.exception.BadRequestException;
import test.ufanet.common.exception.ConflictException;
import test.ufanet.common.exception.NotFoundException;
import test.ufanet.controller.ClientController;
import test.ufanet.controller.ReservationController;
import test.ufanet.dto.reservation.CreateReservationDto;
import test.ufanet.service.ClientService;
import test.ufanet.service.ReservationService;

import java.time.OffsetDateTime;

@WebMvcTest(controllers = {ClientController.class, ReservationController.class})
public class ErrorHandlerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ClientService clientService;

    @MockitoBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper mapper;


    @Test
    @DisplayName("Должен вернуть исключение, если пользователь не найден")
    void shouldReturnNotFoundExceptionWhenUserNotFoundException() throws Exception {
        Mockito.when(clientService.getClientById(Mockito.anyLong()))
                .thenThrow(new NotFoundException("Client not found"));

        mvc.perform(MockMvcRequestBuilders.get("/api/v0/pool/client/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Client not found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reason")
                        .value("Object not found."));
    }

    @Test
    @DisplayName("Должен вернуть исключение, если запись клиента уже существует в этот день")
    void shouldReturnConflictExceptionWhenReservationAlreadyExists() throws Exception {
        CreateReservationDto createDto = CreateReservationDto.builder()
                .clientId(1L)
                .dateTime(OffsetDateTime.parse("2025-05-01T12:00:00Z"))
                .hours(1)
                .build();

        Mockito.when(reservationService.reserve(Mockito.any(CreateReservationDto.class)))
                .thenThrow(new ConflictException("Reservation already exists for this client"));

        mvc.perform(MockMvcRequestBuilders.post("/api/v0/pool/timetable/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Reservation already exists for this client"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reason")
                        .value("Constraint violated."));
    }

    @Test
    @DisplayName("")
    void shouldReturnBadRequestException() throws Exception {
        CreateReservationDto createDto = CreateReservationDto.builder()
                .clientId(1L)
                .dateTime(OffsetDateTime.parse("2025-05-01T12:01:01Z"))
                .hours(1)
                .build();

        Mockito.when(reservationService.reserve(Mockito.any(CreateReservationDto.class)))
                .thenThrow(new BadRequestException("Time must start at full hour"));

        mvc.perform(MockMvcRequestBuilders.post("/api/v0/pool/timetable/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Time must start at full hour"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reason")
                        .value("Invalid request parameters."));
    }
}
