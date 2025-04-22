package test.ufanet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.ufanet.model.ScheduleException;

import java.time.LocalDate;

public interface ScheduleExceptionRepository extends JpaRepository<ScheduleException, LocalDate> {
}
