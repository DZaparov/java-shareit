package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;
    final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Test
    void testItemDto() throws Exception {
        LocalDateTime created = LocalDateTime.now();

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "Нужен шпатель",
                null,
                created);


        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужен шпатель");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.format(formatter));
    }
}
