package test.ufanet.dto.reservation;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ReservationResponseDto {

    private UUID orderId;

}
