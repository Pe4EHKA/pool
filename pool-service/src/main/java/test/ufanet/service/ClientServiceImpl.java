package test.ufanet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.ufanet.common.exception.ConflictException;
import test.ufanet.common.exception.NotFoundException;
import test.ufanet.common.util.PropertyMerger;
import test.ufanet.dto.client.CreateClientDto;
import test.ufanet.dto.client.FullClientDto;
import test.ufanet.dto.client.ShortClientDto;
import test.ufanet.dto.client.UpdateClientDto;
import test.ufanet.mapper.ClientMapper;
import test.ufanet.model.Client;
import test.ufanet.repository.ClientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ShortClientDto> getAllClients() {
        log.info("Getting all clients");
        List<Client> clients = clientRepository.findAll();
        log.info("Found clients: {}, size: {}", clients, clients.size());
        return ClientMapper.toShortClientDtoList(clients);
    }

    @Override
    @Transactional(readOnly = true)
    public FullClientDto getClientById(Long id) {
        log.info("Getting client by id: {}", id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Client with id %d not found", id)));
        log.info("Found client: {}", client);
        return ClientMapper.toFullClientDto(client);
    }

    @Override
    @Transactional
    public FullClientDto createClient(CreateClientDto dto) {
        log.info("Creating client: {}", dto);
        Client client = ClientMapper.toClient(dto);

        try {
            client = clientRepository.save(client);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }
        log.info("Created client: {}", client);
        return ClientMapper.toFullClientDto(client);
    }

    @Override
    @Transactional
    public FullClientDto updateClient(UpdateClientDto dto) {
        log.info("Updating client: {}", dto);
        Client existingClient = clientRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException(String.format("Client with id %d not found", dto.getId())));

        PropertyMerger.mergeProperties(ClientMapper.toClient(dto), existingClient);

        try {
            existingClient = clientRepository.save(existingClient);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }
        log.info("Updated client: {}", existingClient);
        return ClientMapper.toFullClientDto(existingClient);
    }
}
