package kz.ne.railways.tezcustoms.service.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kz.ne.railways.tezcustoms.service.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    private UserDto user;
}
