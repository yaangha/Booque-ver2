package site.book.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Book;
import site.book.project.domain.Notices;
import site.book.project.domain.PostReply;
import site.book.project.domain.UsedBook;
import site.book.project.domain.User;
import site.book.project.dto.NoticeDto;
import site.book.project.repository.NoticeRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ReplyService replyService;
    private final UsedBookService usedBookService;
    private final UserService userService;
    private final BookService bookService;
    
    // 새 댓글 등록 노티스
    public Integer create(NoticeDto dto) {
        
        Notices notice = null;
        if(dto.getUsedBookId() == null) {
             notice = Notices.builder()
                .userId(dto.getUserId())
                .bookId(dto.getBookId())
                .postId(dto.getPostId())
                .replyId(dto.getReplyId()).build();
        } else {
            notice = Notices.builder()
            .userId(dto.getUserId())
            .bookId(dto.getBookId())
            .usedBookId(dto.getUsedBookId()).build();
        }
           
        noticeRepository.save(notice);
        return notice.getNoticeId();
    }   
    
    public List<NoticeDto> readNotices(Integer userId) {
        log.info("read노티스(유저Id={})", userId);  // 알림 받을 userId
        
        List<Notices> list = noticeRepository.findByUserIdOrderByNoticeIdDesc(userId);
        List<NoticeDto> noticeList = new ArrayList<>();
        PostReply r = null;
        NoticeDto dto = null;
        UsedBook ub = null;
        
        
        for (Notices n : list) {
            
            if(n.getUsedBookId() == null) {
                r = replyService.readRep(n.getReplyId());
           
                dto= NoticeDto.builder()
                              .noticeId(n.getNoticeId())
                              .postId(n.getPostId())
                              .bookId(n.getBookId())
                              .userId(n.getUserId())
                              .replyId(n.getReplyId())
                              .userImage(r.getUser().getUserImage())
                               .nickName(r.getUser().getNickName())
                               .build();           
            
                noticeList.add(dto);
                
            } else {
                    ub = usedBookService.read(n.getUsedBookId());
                    Book b = bookService.read(n.getBookId());
                
                    dto= NoticeDto.builder()
                        .noticeId(n.getNoticeId())
                        .bookId(n.getBookId())
                        .userId(n.getUserId())
                        .usedBookId(n.getUsedBookId())
                        .title(ub.getTitle())
                        .bookName(b.getBookName())
                        .bookImage(b.getBookImage())
                        .build();
                
                    noticeList.add(dto);
            }
            
        }
        
        log.info("노티스디티오리스트={}",noticeList);
        
        return noticeList;
    }

    
    public Integer delete(Integer noticeId) {
     
        noticeRepository.deleteById(noticeId);
        
        return noticeId;
    }


    
    public Notices read(Integer noticeId) {
         Notices notice = noticeRepository.findById(noticeId).get();
        return notice;
    }





  

}