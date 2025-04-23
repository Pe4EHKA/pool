package test.ufanet.client.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import test.ufanet.common.exception.ConflictException;
import test.ufanet.dto.client.CreateClientDto;
import test.ufanet.dto.client.FullClientDto;
import test.ufanet.dto.client.ShortClientDto;
import test.ufanet.dto.client.UpdateClientDto;
import test.ufanet.model.Client;
import test.ufanet.repository.ClientRepository;
import test.ufanet.service.ClientServiceImpl;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;

    @BeforeEach
    void setUp() {
        client = Client.builder()
                .id(1L)
                .name("test")
                .phone("+79990001122")
                .email("test@email.com")
                .build();
    }

    @Test
    @DisplayName("Должен вернуть список всех клиентов бассейна")
    void shouldGetAllClients() {
        Mockito.when(clientRepository.findAll()).thenReturn(List.of(client));

        List<ShortClientDto> shortClientDtos = clientService.getAllClients();

        Assertions.assertThat(shortClientDtos).hasSize(1);
        Mockito.verify(clientRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Должен получить клиента по Id")
    void shouldGetClientById() {
        Mockito.when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        FullClientDto dto = clientService.getClientById(client.getId());

        Assertions.assertThat(dto.getId()).isEqualTo(1L);
        Mockito.verify(clientRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Должен выбросить исключение если клиент не найден")
    void shouldThrowNotFoundExceptionWhenClientNotFound() {
        Mockito.when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> clientService.getClientById(1L));
    }

    @Test
    @DisplayName("Должен создать клиента")
    void shouldCreateClient() {
        CreateClientDto createClientDto = CreateClientDto.builder()
                .name("test")
                .phone("+79990001123")
                .email("test@email.com")
                .build();

        Mockito.when(clientRepository.save(Mockito.any(Client.class))).thenAnswer(inv -> {
            Client saved = inv.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        FullClientDto createdClient = clientService.createClient(createClientDto);

        Assertions.assertThat(createdClient.getId()).isEqualTo(2L);
        Assertions.assertThat(createdClient.getName()).isEqualTo("test");
        Assertions.assertThat(createdClient.getPhone()).isEqualTo("+79990001123");
        Assertions.assertThat(createdClient.getEmail()).isEqualTo("test@email.com");

        Mockito.verify(clientRepository, Mockito.times(1)).save(Mockito.any(Client.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение если клиент уже был создан и/или поля не уникальные")
    void shouldThrowConflictExceptionWhenClientAlreadyExists() {
        Mockito.when(clientRepository.save(Mockito.any())).thenThrow(ConflictException.class);

        Assertions.assertThatThrownBy(() ->
                        clientService.createClient(CreateClientDto.builder().build()))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("Должен обновить клиента")
    void shouldUpdateClient() {
        Mockito.when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        Mockito.when(clientRepository.save(Mockito.any(Client.class))).thenReturn(client);

        UpdateClientDto updateClientDto = UpdateClientDto.builder()
                .id(1L)
                .name("Ivan")
                .phone("+999000999")
                .email("Ivan@email.com")
                .build();

        FullClientDto updatedClient = clientService.updateClient(updateClientDto);

        Assertions.assertThat(updatedClient.getId()).isEqualTo(1L);
        Assertions.assertThat(updatedClient.getName()).isEqualTo("Ivan");
        Assertions.assertThat(updatedClient.getPhone()).isEqualTo("+999000999");
        Assertions.assertThat(updatedClient.getEmail()).isEqualTo("Ivan@email.com");
        Mockito.verify(clientRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(clientRepository, Mockito.times(1)).save(Mockito.any(Client.class));
    }
}