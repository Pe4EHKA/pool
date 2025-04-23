package test.ufanet.reservation.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import test.ufanet.dto.reservation.CreateReservationDto;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CreateReservationDtoTest {

    private final JacksonTester<CreateReservationDto> json;

    private final OffsetDateTime now = OffsetDateTime.now();

    @Test
    @DisplayName("Сериализация CreateReservationDto")
    void shouldSerialize() throws Exception {

        CreateReservationDto dto = CreateReservationDto.builder()
                .clientId(1L)
                .dateTime(now)
                .hours(1)
                .build();

        JsonContent<CreateReservationDto> jsonContent = json.write(dto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.clientId", 1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.dateTime", now);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.hours", 1);
    }

    @Test
    @DisplayName("Десериализация CreateReservationDto")
    void shouldDeserialize() throws Exception {
        String content = "{\"clientId\":1,\"dateTime\":\"" + now + "\",\"hours\":1}";

        CreateReservationDto dto = json.parseObject(content);

        assertThat(dto.getClientId()).isEqualTo(1L);
        assertThat(dto.getDateTime()).isEqualTo(now);
        assertThat(dto.getHours()).isEqualTo(1);
    }
}