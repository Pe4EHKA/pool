package test.ufanet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.ufanet.model.Client;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findClientByNameEqualsIgnoreCase(String name);
}
