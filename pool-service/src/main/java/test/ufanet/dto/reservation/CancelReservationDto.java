package test.ufanet.dto.reservation;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CancelReservationDto {

    @NotNull(message = "Client Id can't be null")
    private Long clientId;

    @NotNull(message = "Order Id can't be null")
    private UUID orderId;
}
