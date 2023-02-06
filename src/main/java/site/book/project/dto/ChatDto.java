package site.book.project.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
@Setter
public class ChatDto {
    
    private Integer chatRoomId;
    private Integer UsedBookId;
    private String sender;
    private String message;
    private LocalDateTime sendTime;

}
