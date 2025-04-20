package test.ufanet.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.ufanet.dto.client.CreateClientDto;
import test.ufanet.dto.client.FullClientDto;
import test.ufanet.dto.client.ShortClientDto;
import test.ufanet.dto.client.UpdateClientDto;
import test.ufanet.service.ClientService;

import java.util.List;

@RestController
@RequestMapping("/api/v0/pool/client")
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/all")
    public ResponseEntity<List<ShortClientDto>> getClients() {
        log.info("GET /api/v0/pool/client/all called");
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/get")
    public ResponseEntity<FullClientDto> getClient(@RequestParam(name = "id") Long id) {
        log.info("GET /api/v0/pool/client/get?id={} called", id);
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<FullClientDto> addClient(@RequestBody CreateClientDto dto) {
        log.info("POST /api/v0/pool/client/add, dto={}", dto);
        return new ResponseEntity<>(clientService.createClient(dto), HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<FullClientDto> updateClient(@RequestBody UpdateClientDto dto) {
        log.info("POST /api/v0/pool/client/update, dto={}", dto);
        return ResponseEntity.ok(clientService.updateClient(dto));
    }
}
