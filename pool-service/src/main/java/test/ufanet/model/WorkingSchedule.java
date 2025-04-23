package test.ufanet.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "working_schedule")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WorkingSchedule {

    @Id
    private int id;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
}
