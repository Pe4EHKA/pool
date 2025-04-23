package test.ufanet.dto.reservation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationDto {

    private String time;

    private Integer count;

}
