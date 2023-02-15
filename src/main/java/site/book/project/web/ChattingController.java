package site.book.project.web;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oracle.net.aso.l;
import site.book.project.domain.Chat;
import site.book.project.domain.Chatting;
import site.book.project.domain.Reserved;
import site.book.project.domain.UsedBook;
import site.book.project.domain.UserChatLog;
import site.book.project.dto.ChatListDto;
import site.book.project.dto.ChatReadDto;
import site.book.project.dto.UsedBookReserveDto;
import site.book.project.repository.ReservedRepository;
import site.book.project.repository.UsedBookRepository;
import site.book.project.service.ChatService;
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
    private ReserveService reserveService;

    @Autowired
    private UsedBookRepository usedBookRepository;

    @Autowired
    private ReservedRepository reservedRepository;
    
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
