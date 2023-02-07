package site.book.project.dto;

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
public class ChatReadDto {


//        private Integer chatRoomId;
    //    private Integer UsedBookId;
    private String sender;
    private String message;
    private String sendTime;
    
    public ChatReadDto() {
    }
    
//    public ChatReadDto(String sender, String message, String sendTime) {
//        this.sender = sender;
//        this.message = message;
//        this.sendTime = sendTime;
//    }
}
