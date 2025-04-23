package test.ufanet.client.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import test.ufanet.dto.client.ShortClientDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShortClientDtoTest {

    private final JacksonTester<ShortClientDto> json;

    @Test
    @DisplayName("Сериализация ShortClientDto")
    void shouldSerialize() throws Exception {
        ShortClientDto dto = ShortClientDto.builder()
                .id(1L)
                .name("name")
                .build();

        JsonContent<ShortClientDto> jsonContent = json.write(dto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("name");
    }

    @Test
    @DisplayName("Десериализация ShortClientDto")
    void shouldDeserialize() throws Exception {
        String content = "{\"id\":1,\"name\":\"name\"}";

        ShortClientDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("name");
    }
}