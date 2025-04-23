package test.ufanet.reservation.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import test.ufanet.dto.reservation.ReservationDto;
import test.ufanet.dto.reservation.ReservationResponseDto;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReservationResponseDtoTest {

    private final JacksonTester<ReservationResponseDto> json;

    private final UUID orderId = UUID.randomUUID();

    @Test
    @DisplayName("Сериализация ReservationResponseDto")
    void shouldSerialize() throws Exception {

        ReservationResponseDto dto = ReservationResponseDto.builder()
                .orderId(orderId)
                .build();

        JsonContent<ReservationResponseDto> jsonContent = json.write(dto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.orderId", orderId);
    }

    @Test
    @DisplayName("Десериализация ReservationResponseDto")
    void shouldDeserialize() throws Exception {
        String content = "{\"orderId\":\"" + orderId + "\"}";

        ReservationResponseDto dto = json.parseObject(content);

        assertThat(dto.getOrderId()).isEqualTo(orderId);
    }

}