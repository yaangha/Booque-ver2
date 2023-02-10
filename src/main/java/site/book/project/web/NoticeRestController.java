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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Notices;
import site.book.project.domain.PostReply;
import site.book.project.domain.User;
import site.book.project.dto.CheckDto;
import site.book.project.dto.NoticeDto;
import site.book.project.dto.ReplyReadDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.service.NoticeService;
import site.book.project.service.ReplyService;
import site.book.project.service.UserService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class NoticeRestController {

    private final NoticeService noticeService;
    private final UserService userService;
    
    // (예진) 어떤 포스트 글에 새 댓글이 달릴 때 알림(notice) 만들어짐
    // notice create
    @PostMapping("/notice")
    public ResponseEntity<Integer> newNotice(@RequestBody NoticeDto dto){
        log.info("registe노티스={}", dto);
        
       Integer noticeId = noticeService.create(dto);
       
       return ResponseEntity.ok(noticeId);
    }
    
    // (예진) userId(postWriter) 기준 알림 리스트(notice list) 불러오기
    @GetMapping("/showNotice/{userId}")
    public ResponseEntity<List<NoticeDto>> showAllNotices(@PathVariable Integer userId) {
        log.info("쇼All노티스(userId={})", userId);
        
        List<NoticeDto> list =noticeService.readNotices(userId);
       
        return ResponseEntity.ok(list);
    }
    
//    노티스 삭제 컨트롤러 포스트 컨트롤러에..  
//    @DeleteMapping("/deleteNotice/{noticeId}")
//    public ResponseEntity<Integer> deleteNotice(@PathVariable Integer noticeId){
//        log.info("노티스삭제(noticeId={})", noticeId);
//        
//        Integer result = noticeService.delete(noticeId);
//        return ResponseEntity.ok(result);     
//    }
    
    @PostMapping("/notice/check")
    public ResponseEntity<Integer> checkContainingSubsKeyword(@RequestBody CheckDto checkDto){
        log.info("체크디티오={}",checkDto);
        
        List<User> users = userService.read();
        List<String> subs = new ArrayList<>();
        String kw = null;
        for (User u : users) {
            kw = u.getSubsKeyword();     
        } subs.add(kw);
        
        for (String s: subs) {
            
        }
         
        
        return ResponseEntity.ok(1);
    }
   
    
}