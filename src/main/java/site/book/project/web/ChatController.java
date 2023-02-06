package site.book.project.web;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ChatController {
    
    // 채팅 view 접속하기
    @GetMapping("/chat")
    public String onChatting(String withChatUsername, Model model) {
        log.info("withChatUsername={}", withChatUsername);
        model.addAttribute("firstConnectUser", withChatUsername);
        return "chat";
    }
    
    // 
    @GetMapping("message")
    @SendTo("/chat/message")
    public String getMessage(String message) {
        return message;
    }
}
