package site.book.project.web;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
    
    
    // (예진) 알림받을 BookId 등록
    @GetMapping("/register/notice")
    public String registerBookId(Integer bookId, @AuthenticationPrincipal UserSecurityDto dto) {
        
       User user = userService.read(dto.getId());
       user.setNoticeBookId(bookId);
       userRepository.save(user);
        
       return "/market/main";
    }
    
}
