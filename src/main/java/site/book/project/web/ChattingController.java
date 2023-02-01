package site.book.project.web;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oracle.net.aso.l;
import site.book.project.domain.Chatting;
import site.book.project.domain.UserChatLog;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChattingController {
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    // 메시지 컨트롤러
    @MessageMapping("/chat/{userName}")
    public void sendMessage (@DestinationVariable String userName, Chatting chat) {
        log.info("sendMessage(to={}, chat={})", userName, chat);
        boolean isExists = UserChatLog.getInstance().getUsers().contains(userName);
        if (isExists) {
            log.info("존재하는지 안하는지 알고싶다!!, {}", isExists);
            simpMessagingTemplate.convertAndSend("/topic/messages/" + userName, chat);
            log.info("짜증나,, {}, {}", userName, chat);
            
        }
    }
    
    // 채팅 로그 사용자 등록
    @CrossOrigin
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
    @CrossOrigin
    @GetMapping("/fetchAllUsers")
    public Set<String> fetchAll(){
        return UserChatLog.getInstance().getUsers();
    }
}
