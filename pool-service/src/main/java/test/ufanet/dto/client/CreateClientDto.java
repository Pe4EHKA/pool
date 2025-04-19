package test.ufanet.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class CreateClientDto {

    @NotBlank
    @Length(min = 1, max = 256)
    private String name;

    @NotBlank
    @Length(min = 1, max = 50)
    private String phone;

    @Email
    @NotBlank
    @Length(min = 1, max = 256)
    private String email;
}
