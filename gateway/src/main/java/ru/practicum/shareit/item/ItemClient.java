package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.BlankFieldException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(ItemDto itemDto, Long ownerId) {
        if (ownerId == null) {
            throw new BlankFieldException("Заголовок X-Sharer-User-Id не должен быть пустым");
        }
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long id, ItemDto itemDto, Long ownerId) {
        if (ownerId == null) {
            throw new BlankFieldException("Заголовок X-Sharer-User-Id не должен быть пустым");
        }
        return patch("/" + id, ownerId, itemDto);
    }

    public ResponseEntity<Object> getItemById(Long id, Long ownerId) {
        if (ownerId == null) {
            throw new BlankFieldException("Заголовок X-Sharer-User-Id не должен быть пустым");
        }
        return get("/" + id, ownerId);
    }

    public ResponseEntity<Object> listItemsOfUser(Long ownerId, int from, int size) {
        if (ownerId == null) {
            throw new BlankFieldException("Заголовок X-Sharer-User-Id не должен быть пустым");
        }
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> searchItem(Long userId, String text, int from, int size) {
        if (userId == null) {
            throw new BlankFieldException("Заголовок X-Sharer-User-Id не должен быть пустым");
        }
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(CommentDto comment, Long userId, Long itemId) {
        if (userId == null) {
            throw new BlankFieldException("Заголовок X-Sharer-User-Id не должен быть пустым");
        }
        return post("/" + itemId + "/comment", userId, comment);
    }
}
