package test.ufanet.client.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import test.ufanet.dto.client.CreateClientDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CreateClientDtoTest {

    private final JacksonTester<CreateClientDto> json;

    @Test
    @DisplayName("Сериализация CreateClientDto")
    void shouldSerialize() throws Exception {
        CreateClientDto dto = CreateClientDto.builder()
                .name("test")
                .phone("123456789")
                .email("test@mail.com")
                .build();

        JsonContent<CreateClientDto> jsonContent = json.write(dto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("test");
        assertThat(jsonContent).extractingJsonPathStringValue("$.phone").isEqualTo("123456789");
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo("test@mail.com");
    }

    @Test
    @DisplayName("Десериализация CreateClientDto")
    void shouldDeserialize() throws Exception {
        String content = "{\"name\":\"test\",\"phone\":\"123456789\",\"email\":\"test@email.com\"}";

        CreateClientDto dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("test");
        assertThat(dto.getPhone()).isEqualTo("123456789");
        assertThat(dto.getEmail()).isEqualTo("test@email.com");
    }

}