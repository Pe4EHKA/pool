package test.ufanet.reservation.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import test.ufanet.dto.reservation.ReservationDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReservationDtoTest {

    private final JacksonTester<ReservationDto> json;

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("Сериализация ReservationDto")
    void shouldSerialize() throws Exception {

        ReservationDto dto = ReservationDto.builder()
                .time(now.toString())
                .count(9)
                .build();

        JsonContent<ReservationDto> jsonContent = json.write(dto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.time", now.toString());
        assertThat(jsonContent).extractingJsonPathNumberValue("$.count", 9);
    }

    @Test
    @DisplayName("Десериализация ReservationDto")
    void shouldDeserialize() throws Exception {
        String content = "{\"time\":\"" + now.toString() + "\",\"count\":9}";

        ReservationDto dto = json.parseObject(content);

        assertThat(dto.getTime()).isEqualTo(now.toString());
        assertThat(dto.getCount()).isEqualTo(9);
    }
}