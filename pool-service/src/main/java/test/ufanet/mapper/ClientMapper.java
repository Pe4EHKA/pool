package test.ufanet.mapper;

import lombok.experimental.UtilityClass;
import test.ufanet.dto.client.CreateClientDto;
import test.ufanet.dto.client.FullClientDto;
import test.ufanet.dto.client.ShortClientDto;
import test.ufanet.dto.client.UpdateClientDto;
import test.ufanet.model.Client;

import java.util.List;

@UtilityClass
public class ClientMapper {

    public FullClientDto toFullClientDto(Client client) {
        return FullClientDto.builder()
                .id(client.getId())
                .name(client.getName())
                .phone(client.getPhone())
                .email(client.getEmail())
                .build();
    }

    public Client toClient(CreateClientDto createClientDto) {
        return Client.builder()
                .name(createClientDto.getName())
                .phone(createClientDto.getPhone())
                .email(createClientDto.getEmail())
                .build();
    }

    public Client toClient(UpdateClientDto updateClientDto) {
        return Client.builder()
                .id(updateClientDto.getId())
                .name(updateClientDto.getName())
                .phone(updateClientDto.getPhone())
                .email(updateClientDto.getEmail())
                .build();
    }

    public ShortClientDto toShortClientDto(Client client) {
        return ShortClientDto.builder()
                .id(client.getId())
                .name(client.getName())
                .build();
    }

    public List<ShortClientDto> toShortClientDtoList(List<Client> clients) {
        return clients.stream()
                .map(ClientMapper::toShortClientDto)
                .toList();
    }
}
