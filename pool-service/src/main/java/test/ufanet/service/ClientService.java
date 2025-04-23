package test.ufanet.service;

import test.ufanet.dto.client.CreateClientDto;
import test.ufanet.dto.client.FullClientDto;
import test.ufanet.dto.client.ShortClientDto;
import test.ufanet.dto.client.UpdateClientDto;

import java.util.List;

public interface ClientService {

    List<ShortClientDto> getAllClients();

    FullClientDto getClientById(Long id);

    FullClientDto createClient(CreateClientDto dto);

    FullClientDto updateClient(UpdateClientDto dto);
}
