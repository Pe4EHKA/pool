package test.ufanet.service;

import test.ufanet.dto.client.CreateClientDto;
import test.ufanet.dto.client.FullClientDto;
import test.ufanet.dto.client.ShortClientDto;
import test.ufanet.dto.client.UpdateClientDto;

import java.util.List;

public interface ClientService {

    public List<ShortClientDto> getAllClients();

    public FullClientDto getClientById(Long id);

    public FullClientDto createClient(CreateClientDto dto);

    public FullClientDto updateClient(UpdateClientDto dto);
}
