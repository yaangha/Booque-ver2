package site.book.project.web;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oracle.net.aso.l;
import site.book.project.domain.Chat;
import site.book.project.domain.ChatAssist;
import site.book.project.domain.Chatting;
import site.book.project.domain.UserChatLog;
import site.book.project.dto.ChatReadDto;
import site.book.project.dto.UserRegisterDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.ChatAssistRepository;
import site.book.project.repository.ChatRepository;
import site.book.project.repository.UserRepository;
import site.book.project.service.ChatService;
import site.book.project.service.UserService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChattingController {
    
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChatAssistRepository chatAssistRepository;
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
        // log.info("send{},{}",chatRoomId,dto);
        log.info("텍스트파일에 메시지 내용 추가: " + dto);
//        Integer chatRoomId = dto.getChatRoomId();
        String url = "/user/" + chatRoomId + "/queue/messages";
        simpMessagingTemplate.convertAndSend(url, new ChatReadDto(dto.getSender(), dto.getMessage(), dto.getSendTime())); 

    }
    
    // (홍찬) 메세지 알림을 위해 읽음 푸시
    @MessageMapping("/chat/read/{chatRoomId}")
    public void notification(@DestinationVariable Integer chatRoomId, ChatReadDto dto) throws IOException {
        String nickName = dto.getSender();
//        ChatAssist CA = chatAssistRepository.findByChatRoomId(chatRoomId);
//        Integer unreadCount = CA.getReadCount();
//        String nickNameCmp = CA.getNickName();
//        log.info("@@@@@@{},{},{}",chatRoomId,nickName, nickNameCmp);
//        if (unreadCount != 0 ) {
//            if (nickName.equals(nickNameCmp)) {
//                chatService.updateReadChat(nickNameCmp, chatRoomId, 1); // 상대방의 글을 읽고 내 글을 보낼 때
//            } else {
//                chatService.updateReadChat(nickNameCmp, chatRoomId, 0); // 상대방이 내 글을 읽지 않고 안읽음 추가
//            }   
//        } 
        String url2 = "/user/" + chatRoomId + "/queue/notification/" + nickName;
        simpMessagingTemplate.convertAndSend(url2, nickName);
    }
    
    // 채팅 로그 사용자 등록
    @CrossOrigin
    @GetMapping("/registration/{nickName}")
    public ResponseEntity<Void> register(@PathVariable String nickName){
        log.info("register(username={})", nickName);
        try {
            UserChatLog.getInstance().setUser(nickName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
    
    // 채팅 로그 사용자 등록 취소
    @CrossOrigin
    @GetMapping("/unregistration/{nickName}")
    public ResponseEntity<Void> unregister(@PathVariable String nickName){
        log.info("unregister(username={})", nickName);
        UserChatLog.getInstance().unsetUser(nickName);
        return ResponseEntity.ok().build();
    }
    
    // 채팅하고 있는 상대가 웹소켓에 연결중인지 확인
    @PostMapping("/onlineChk")
    public Integer isExists(String nickName, Integer chatRoomId) {
        log.info("isExists{},{}",nickName,chatRoomId);
        boolean isexist = UserChatLog.getInstance().getUsers().contains(nickName);
        if (isexist) {
            log.info("상대방 상태: online입니다.");
            return 3; // TODO
        } else {
            log.info("상대방 상태: offline입니다.");
            return chatService.updateReadChat(nickName, chatRoomId, 0);
        }
    }
//    // 채팅창 가져오기
//    @CrossOrigin
//    @GetMapping("/fetchAllUsers")
//    public Set<String> fetchAll(){
//        return UserChatLog.getInstance().getUsers();
//    }
}
