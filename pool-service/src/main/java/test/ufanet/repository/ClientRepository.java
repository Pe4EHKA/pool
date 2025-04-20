package test.ufanet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.ufanet.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
