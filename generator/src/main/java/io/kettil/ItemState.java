package io.kettil;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Value;

import java.time.LocalDateTime;

@Value
class ItemState {
    String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    LocalDateTime timestamp;
    int value;

    @JsonIgnore
    LocalDateTime nextUpdate;
}
