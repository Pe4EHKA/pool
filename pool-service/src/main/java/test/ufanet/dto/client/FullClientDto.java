package test.ufanet.dto.client;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FullClientDto {

    private Long id;

    private String name;

    private String phone;

    private String email;

}
