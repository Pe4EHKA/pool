package test.ufanet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import test.ufanet.model.WorkingSchedule;

import java.util.Optional;

public interface WorkingScheduleRepository extends JpaRepository<WorkingSchedule, Short> {

    @Query("""
            SELECT ws
            FROM WorkingSchedule ws
            WHERE ws.id = :dow
            """)
    Optional<WorkingSchedule> findByDow(int dow);
}
