package test.ufanet.client.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import test.ufanet.dto.client.FullClientDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FullClientDtoTest {

    private final JacksonTester<FullClientDto> json;

    @Test
    @DisplayName("Сериализация FullClientDto")
    void shouldSerialize() throws Exception {
        FullClientDto dto = FullClientDto.builder()
                .id(1L)
                .name("test")
                .phone("123456789")
                .email("test@mail.com")
                .build();

        JsonContent<FullClientDto> jsonContent = json.write(dto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("test");
        assertThat(jsonContent).extractingJsonPathStringValue("$.phone").isEqualTo("123456789");
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo("test@mail.com");
    }

    @Test
    @DisplayName("Десериализация FullClientDto")
    void shouldDeserialize() throws Exception {
        String content = "{\"id\":1,\"name\":\"test\",\"phone\":\"123456789\",\"email\":\"test@email.com\"}";

        FullClientDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("test");
        assertThat(dto.getPhone()).isEqualTo("123456789");
        assertThat(dto.getEmail()).isEqualTo("test@email.com");
    }
}