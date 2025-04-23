package test.ufanet.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class UpdateClientDto {

    @NotNull(message = "Id can't be null or empty")
    private Long id;

    @NotBlank(message = "Name can't be null or empty")
    @Length(min = 1, max = 256)
    private String name;

    @NotBlank(message = "Phone can't be null or empty")
    @Length(min = 1, max = 50)
    private String phone;

    @Email
    @NotBlank(message = "Email can't be null or empty")
    @Length(min = 1, max = 256)
    private String email;
}
