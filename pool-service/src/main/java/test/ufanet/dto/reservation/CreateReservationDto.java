package test.ufanet.dto.reservation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class CreateReservationDto {

    @NotNull(message = "Client Id can't be null")
    private Long clientId;

    @NotNull
    private OffsetDateTime dateTime;

    @Min(1)
    @Builder.Default
    private Integer hours = 1;

}
