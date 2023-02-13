package site.book.project.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Notices;
import site.book.project.domain.User;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.NoticeRepository;
import site.book.project.repository.UserRepository;
import site.book.project.service.NoticeService;
import site.book.project.service.UserService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NoticeController {
    
    private final NoticeService noticeService;
    private final NoticeRepository noticeRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    
//    @GetMapping("/notice/delete")
//    public void deleteNotice(Integer noticeId) {
//        log.info("노티스삭제(noticeId={})", noticeId);
//        
//        Notices n = noticeService.read(noticeId);
//        
//        noticeService.delete(noticeId);
//        noticeRepository.deleteById(noticeId);
//       // attrs.addFlashAttribute("deletedNoticeId", noticeId);
//        log.info("노티스삭제 끝(noticeId={})", noticeId);
//        
//       //return "redirect:/post/detail?postId=" + n.getPostId()+"&bookId="+ n.getBookId();
//    }

   
    
    // (예진) 알림받을 BookId 등록
    @GetMapping("/register/notice")
    public String registerBookId(Integer bookId, @AuthenticationPrincipal UserSecurityDto dto) {
        
       User user = userService.read(dto.getId());
       user.setNoticeBookId(bookId);
       userRepository.save(user);
        
       return  "redirect:/market/main";
    }
    
}
