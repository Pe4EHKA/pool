package test.ufanet.client;

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
import test.ufanet.controller.ClientController;
import test.ufanet.dto.client.CreateClientDto;
import test.ufanet.dto.client.FullClientDto;
import test.ufanet.dto.client.ShortClientDto;
import test.ufanet.dto.client.UpdateClientDto;
import test.ufanet.service.ClientService;

import java.util.List;

@WebMvcTest(controllers = ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DisplayName("Должен получить список клиентов")
    void shouldGetClients() throws Exception {
        Mockito.when(clientService.getAllClients()).thenReturn(List.of(ShortClientDto.builder()
                .id(1L)
                .name("Ivan")
                .build()));

        mvc.perform(MockMvcRequestBuilders.get("/api/v0/pool/client/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Ivan"));
    }

    @Test
    @DisplayName("Должен получить клиента")
    void shouldGetClient() throws Exception {
        Mockito.when(clientService.getClientById(1L)).thenReturn(FullClientDto.builder()
                .id(1L)
                .name("Ivan")
                .phone("123456789")
                .email("ivan@gmail.com")
                .build());

        mvc.perform(MockMvcRequestBuilders.get("/api/v0/pool/client/get").param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("Ivan"))
                .andExpect(MockMvcResultMatchers.jsonPath("phone").value("123456789"))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value("ivan@gmail.com"));
    }

    @Test
    @DisplayName("Должен добавить клиента")
    void shouldAddClient() throws Exception {
        CreateClientDto dto = CreateClientDto.builder()
                .name("Ivan")
                .phone("123456789")
                .email("ivan@gmail.com")
                .build();
        FullClientDto fullDto = FullClientDto.builder()
                .id(1L)
                .name("Ivan")
                .phone("123456789")
                .email("ivan@gmail.com")
                .build();

        Mockito.when(clientService.createClient(Mockito.any(CreateClientDto.class))).thenReturn(fullDto);

        mvc.perform(MockMvcRequestBuilders.post("/api/v0/pool/client/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("Ivan"))
                .andExpect(MockMvcResultMatchers.jsonPath("phone").value("123456789"))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value("ivan@gmail.com"));
    }

    @Test
    @DisplayName("Должен обновить клиента")
    void shouldUpdateClient() throws Exception {
        UpdateClientDto dto = UpdateClientDto.builder()
                .id(1L)
                .name("IvanUpdate")
                .phone("987654321")
                .email("ivanUpdate@gmail.com")
                .build();
        FullClientDto response = FullClientDto.builder()
                .id(1L)
                .name("IvanUpdate")
                .phone("987654321")
                .email("ivanUpdate@gmail.com")
                .build();

        Mockito.when(clientService.updateClient(Mockito.any(UpdateClientDto.class))).thenReturn(response);

        mvc.perform(MockMvcRequestBuilders.put("/api/v0/pool/client/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("id").value(1L))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("name").value("IvanUpdate"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("phone").value("987654321"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("email").value("ivanUpdate@gmail.com"));
    }
}