package site.book.project.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Notices;
import site.book.project.repository.NoticeRepository;
import site.book.project.service.NoticeService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NoticeController {
    
    private final NoticeService noticeService;
    private final NoticeRepository noticeRepository;
    
    @GetMapping("/notice/delete")
    public void deleteNotice(Integer noticeId) {
        log.info("노티스삭제(noticeId={})", noticeId);
        
        Notices n = noticeService.read(noticeId);
        
        noticeService.delete(noticeId);
        noticeRepository.deleteById(noticeId);
       // attrs.addFlashAttribute("deletedNoticeId", noticeId);
        log.info("노티스삭제 끝(noticeId={})", noticeId);
        
       //return "redirect:/post/detail?postId=" + n.getPostId()+"&bookId="+ n.getBookId();
    }

}
