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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oracle.net.aso.l;
import site.book.project.domain.Chat;
import site.book.project.domain.ChatAssist;
import site.book.project.domain.Chatting;
import site.book.project.domain.Reserved;
import site.book.project.domain.UsedBook;
import site.book.project.domain.UserChatLog;
import site.book.project.dto.ChatReadDto;
import site.book.project.dto.UserRegisterDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.ChatAssistRepository;
import site.book.project.repository.ChatRepository;
import site.book.project.repository.UserRepository;
import site.book.project.dto.UsedBookReserveDto;
import site.book.project.repository.ReservedRepository;
import site.book.project.repository.UsedBookRepository;
import site.book.project.service.ChatService;
import site.book.project.service.UserService;
import site.book.project.service.ReserveService;

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
    @Autowired
    private ReserveService reserveService;

    @Autowired
    private UsedBookRepository usedBookRepository;

    @Autowired
    private ReservedRepository reservedRepository;

    // (지혜)
    @MessageMapping("/chat/{chatRoomId}")
    public void send(@DestinationVariable Integer chatRoomId, ChatReadDto dto) throws IOException {
        //append message to txtFile
        chatService.appendMessage(chatRoomId, dto);
        // log.info("send{},{}",chatRoomId,dto);
        log.info("텍스트파일에 메시지 내용 추가: " + dto);
        String url = "/user/" + chatRoomId + "/queue/messages";
        simpMessagingTemplate.convertAndSend(url, new ChatReadDto(dto.getSender(), dto.getMessage(), dto.getSendTime())); 

    }
    
    // (홍찬) 메세지 알림을 위해 읽음 푸시
    @MessageMapping("/chat/read/{chatRoomId}")
    public void notification(@DestinationVariable Integer chatRoomId, ChatReadDto dto) throws IOException {
        String nickName = dto.getSender();
        String url2 = "/user/" + chatRoomId + "/queue/notification/" + nickName;
        simpMessagingTemplate.convertAndSend(url2, nickName);
    }
    
    // (지혜) 거래 예약
    @PostMapping("/chat/reserve")
    public void reserve(@RequestBody UsedBookReserveDto dto) {
        Integer usedBookId = dto.getUsedBookId();
        Integer userId = dto.getUserId();
        
        // usedBook의 판매상태(status)를 '예약중'으로 바꿔 주기
        UsedBook usedBook = usedBookRepository.findById(usedBookId).get();
        usedBook = usedBook.updateStauts("예약중");
        usedBookRepository.save(usedBook);
        
        // 예약 정보 생성
        reserveService.newReservation(usedBookId, userId);
    }
    
    // (지혜) 거래 취소=예약 취소
    @PostMapping("/chat/cancel")
    public void cancel(Integer usedBookId) {
        log.info("책!!!!!!!: "+usedBookId);
        // usedBook의 판매상태(status)를 '판매중'으로 바꿔 주기
        UsedBook usedBook = usedBookRepository.findById(usedBookId).get();
        usedBook = usedBook.updateStauts("판매중");
        usedBookRepository.save(usedBook);
        
        // 예약 정보 삭제
        reserveService.deleteReservation(usedBookId);
    }
    
    // (지혜) 거래 완료=판매 완료
    @PostMapping("/chat/sold")
    public void sold(Integer usedBookId) {
        // usedBook의 판매상태(status)를 '판매완료'로 바꿔 주기
        UsedBook usedBook = usedBookRepository.findById(usedBookId).get();
        usedBook = usedBook.updateStauts("판매완료");
        usedBookRepository.save(usedBook);
        
        // 예약 정보 삭제
        reserveService.deleteReservation(usedBookId);
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
            log.info("상대방{} 상태: online입니다.", nickName);
            return 3; // TODO
        } else {
            log.info("상대방{} 상태: offline입니다.", nickName);
            return chatService.updateReadChat(nickName, chatRoomId, 0);
        }
    }
}
