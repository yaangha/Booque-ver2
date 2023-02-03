package site.book.project.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import site.book.project.domain.Book;
import site.book.project.domain.Chat;
import site.book.project.domain.Post;
import site.book.project.domain.UsedBook;
import site.book.project.domain.User;

@AllArgsConstructor
@Builder
@Getter
@ToString
@Setter
public class ChatCreateDto {
    
    private Integer chatRoomId;
    private Integer UsedBookId;
    private Integer sellerId;
    private Integer buyerId;
    private LocalDateTime createdTime;
    private String fileName;
    
    // 이 클래스는 필요 없으면 삭제??

}
