package test.ufanet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedule_exception")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ScheduleException {

    @Id
    @Column(name = "exception_date", nullable = false)
    private LocalDate exceptionDate;

    @Column(name = "is_working", nullable = false)
    @Builder.Default
    private Boolean isWorking = false;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
}
