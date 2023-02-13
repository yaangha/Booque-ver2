package site.book.project.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Notices;
import site.book.project.domain.PostReply;
import site.book.project.domain.UsedBook;
import site.book.project.domain.UsedBookPost;
import site.book.project.domain.User;

import site.book.project.dto.NoticeDto;
import site.book.project.dto.ReplyReadDto;

import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.NoticeRepository;
import site.book.project.repository.UsedBookPostRepository;
import site.book.project.service.NoticeService;
import site.book.project.service.ReplyService;
import site.book.project.service.UsedBookService;
import site.book.project.service.UserService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class NoticeRestController {

    private final NoticeService noticeService;
    private final NoticeRepository noticeRepository;
    private final UserService userService;
  
    
    // (예진) 포스트에 새 댓글이 달리면 알림(notice) 만들어짐
    // notice create
    @PostMapping("/notice")
    public ResponseEntity<Integer> newNotice(@RequestBody NoticeDto dto){
        log.info("registe노티스={}", dto);
        
       Integer noticeId = noticeService.create(dto);
       
       return ResponseEntity.ok(noticeId);
    }
    
    // (예진) usedBook 포스트 등록 될 때 해당 북아이디 알림 받기 설정한 유저가 있는지 체크한 후
    // 있다면 노티스 생성  
    @PostMapping("/notice/check")
    public ResponseEntity<Integer> checkContainBookId(@RequestBody NoticeDto noticeDto){
        log.info("체크데이터={}:{}", noticeDto.getBookId(), noticeDto.getUsedBookId());
        
        Integer bookId = noticeDto.getBookId();
        Integer usedBookId = noticeDto.getUsedBookId();
        
        List<User> users = userService.read();
            
        for (User u : users) {
            if(u.getNoticeBookId() == bookId) {
               Integer uId = u.getId();  
            
               NoticeDto dto = NoticeDto.builder().userId(uId).bookId(bookId).usedBookId(usedBookId).build();
               Integer noticeId = noticeService.create(dto);
            
               return ResponseEntity.ok(noticeId);
            } 
        } 
          
        return ResponseEntity.ok(1);
      
    }
   
    
    
    // (예진) userId(postWriter/subscribedBookId) 기준 새 댓글 알림 리스트(notice list) 불러오기
    @GetMapping("/showNotice/{userId}")
    public ResponseEntity<List<NoticeDto>> showAllNotices(@PathVariable Integer userId) {
      
         List<NoticeDto> list =noticeService.readNotices(userId);
       
        return ResponseEntity.ok(list);
    }
    
   
    @DeleteMapping("/notice/delete/{noticeId}")
    public void deleteNotice(@PathVariable Integer noticeId){
        log.info("노티스삭제(noticeId={})", noticeId);
        
        noticeService.delete(noticeId);
        noticeRepository.deleteById(noticeId);    
            
    }
    
  

    
}