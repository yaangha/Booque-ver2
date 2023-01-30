package site.book.project.web;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ChatController {

    @GetMapping("/chat")
    public String onChatting() {
        return "chat";
    }
    
    @GetMapping("message")
    @SendTo("/chat/message")
    public String getMessage(String message) {
        return message;
    }
}
