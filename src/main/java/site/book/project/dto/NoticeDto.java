package site.book.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Book;
import site.book.project.domain.Notices;
import site.book.project.domain.PostReply;
import site.book.project.service.ReplyService;

@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Data
public class NoticeDto {

   
    
    private Integer noticeId;
    private Integer userId;  // postWriter id;
    private Integer bookId;
    
    
    private Integer postId;
    private PostReply reply;
    private Integer replyId;
    private String replyWriter;
    private String nickName;  // replyWriter의 닉네임
    private String userImage; // replyWriter의 유저이미지
    
    private Integer usedBookId;

    private String noticeBookId;
    private String bookName;
    private String bookImage;
    private String title;
    

 
    

}