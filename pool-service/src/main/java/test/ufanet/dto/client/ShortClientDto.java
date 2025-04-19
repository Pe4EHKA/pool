package test.ufanet.dto.client;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortClientDto {

    private Long id;

    private String name;
}
