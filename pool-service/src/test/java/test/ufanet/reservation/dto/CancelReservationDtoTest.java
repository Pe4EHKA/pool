package test.ufanet.reservation.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import test.ufanet.dto.reservation.CancelReservationDto;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CancelReservationDtoTest {

    private final JacksonTester<CancelReservationDto> json;

    private final UUID orderId = UUID.randomUUID();

    @Test
    @DisplayName("Сериализация CancelReservationDto")
    void shouldSerialize() throws Exception {

        CancelReservationDto dto = CancelReservationDto.builder()
                .clientId(1L)
                .orderId(orderId)
                .build();

        JsonContent<CancelReservationDto> jsonContent = json.write(dto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.clientId", 1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.orderId", orderId);
    }

    @Test
    @DisplayName("Десериализация CancelReservationDto")
    void shouldDeserialize() throws Exception {
        String content = "{\"clientId\":1,\"orderId\":\"" + orderId.toString() + "\"}";

        CancelReservationDto dto = json.parseObject(content);

        assertThat(dto.getClientId()).isEqualTo(1);
        assertThat(dto.getOrderId()).isEqualTo(orderId);
    }

}