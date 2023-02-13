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
import site.book.project.domain.UsedBookPost;
import site.book.project.domain.User;

import site.book.project.dto.NoticeDto;
import site.book.project.dto.ReplyReadDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.NoticeRepository;
import site.book.project.repository.UsedBookPostRepository;
import site.book.project.service.NoticeService;
import site.book.project.service.ReplyService;

import site.book.project.service.UserService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class NoticeRestController {

    private final NoticeService noticeService;
    private final NoticeRepository noticeRepository;
    private final UserService userService;
    private final UsedBookPostRepository usedBookPostRepository;
 
    
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
      
         List<NoticeDto> list =noticeService.readNotices(userId);
       
       
        return ResponseEntity.ok(list);
    }
    
   
    @DeleteMapping("/notice/delete/{noticeId}")
    public void deleteNotice(@PathVariable Integer noticeId){
        log.info("노티스삭제(noticeId={})", noticeId);
        
        noticeService.delete(noticeId);
        noticeRepository.deleteById(noticeId);    
            
    }
    
//    @GetMapping("/notice/check")
//    public ResponseEntity<List<String>> checkContainingSubsKeyword(@RequestBody CheckDto dto){
//        log.info("체크디티오={}", dto.getUsedBookId());
//        
//        List<User> users = userService.read();
//
//        List<String> subsList = new ArrayList<>();
//            String kw = null;
//            for (User u : users) {
//                kw = u.getSubsKeyword();    
//            } subsList.add(kw); // 유저들의 알람 등록 된 키워드 리스트
//            
//        List<UsedBookPost> usedList =  usedBookPostRepository.findByUsedBookIdOrderByIdDesc(dto.getUsedBookId());
//        UsedBookPost up = usedList.get(0);  // 아이디 내림차순 정렬 -> 인덱스 0번 -> Create된 포스트
//        
//        UsedBookPost p =null;
//        for (String s : subsList) {
//           p = usedBookPostService.searchSubsKeyword(s);
//           if(p != null) {
//               subsList.add(s);
//           }
//        }
//       
//        
//        return ResponseEntity.ok(subsList);
//    }
   
    
}