package site.book.project.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Data
public class ChatListDto {

    private Integer chatRoomId;
    private LocalDateTime modifiedTime;  // 채팅 최신 업뎃시간
    
    private Integer usedBookId;    // 중고판매글 id
    private String usedBookImage;  // 상품 이미지(List[0])
    private String usedBookTitle;  // 판매글 제목
    private String usedTitle;  // 판매글 제목
    private Integer price;          // 중고 판매 가격
    private String status;         // 판매 상태
    
    private String chatWithName;    // 채팅 상대 닉네임
    private String chatWithImage;   // 채팅 상대 프사
    private String chatWithLevel;   // 채팅 상대 부끄레벨
    
}
