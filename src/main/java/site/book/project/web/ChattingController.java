package site.book.project.web;

import java.io.IOException;
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
import site.book.project.domain.Chat;
import site.book.project.domain.Chatting;
import site.book.project.domain.UserChatLog;
import site.book.project.dto.ChatReadDto;
import site.book.project.service.ChatService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChattingController {
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private ChatService chatService;
    
    // 메시지 컨트롤러
//    @MessageMapping("/chat/{userName}")
//    public void sendMessage (@DestinationVariable String userName, Chatting chat) {
//        log.info("sendMessage(to={}, chat={})", userName, chat);
//        boolean isExists = UserChatLog.getInstance().getUsers().contains(userName);
//        if (isExists) {
//            simpMessagingTemplate.convertAndSend("/topic/messages/" + userName, chat);
//            
//        }
//    }
    
    // (지혜)
    @MessageMapping("/chat/{chatRoomId}")
    public void send(@DestinationVariable Integer chatRoomId, ChatReadDto dto) throws IOException {
 
        //append message to txtFile
        chatService.appendMessage(chatRoomId, dto);
        log.info("텍스트파일에 챗기록 저장: chatService.appendMessage");
        
//        Integer chatRoomId = dto.getChatRoomId();
        String url = "/user/" + chatRoomId + "/queue/messages";
        simpMessagingTemplate.convertAndSend(url, new ChatReadDto(dto.getSender(), dto.getMessage(), dto.getSendTime())); 
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
