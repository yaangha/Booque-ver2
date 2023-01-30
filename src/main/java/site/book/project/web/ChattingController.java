package site.book.project.web;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Chatting;
import site.book.project.domain.UserChatLog;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChattingController {
    
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @MessageMapping("/chat/{to}")
    public void sendMessage (@DestinationVariable String to, Chatting chat) {
        log.info("sendMessage(to={}, chat={})", to, chat);
        simpMessagingTemplate.convertAndSend("/topic/chat" + to, chat);
    }
    
    // 채팅 로그 사용자 등록
    @GetMapping("/registration/{userName}")
    public ResponseEntity<Void> register(@PathVariable String userName){
        log.info("register(username={})", userName);
        try {
            UserChatLog.getInstance().setUser(userName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
    
    // 채팅창 가져오기
    @GetMapping("/fetchAllUsers")
    public Set<String> fetchAll(){
        return UserChatLog.getInstance().getUsers();
    }
}
