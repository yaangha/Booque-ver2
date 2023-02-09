package site.book.project.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Chat;
import site.book.project.domain.UsedBook;
import site.book.project.domain.User;
import site.book.project.dto.ChatReadDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.ChatRepository;
import site.book.project.repository.UsedBookRepository;
import site.book.project.repository.UserRepository;
import site.book.project.service.ChatService;

@Slf4j
@Controller
public class ChatController {
    
    // 채팅 view 접속하기
//    @GetMapping("/chat")
//    public String onChatting(String withChatUsername, Model model) {
//        log.info("withChatUsername={}", withChatUsername);
//        model.addAttribute("firstConnectUser", withChatUsername);
//        return "chat";
//    }
    
    // 
//    @GetMapping("message")
//    @SendTo("/chat/message")
//    public String getMessage(String message) {
//        return message;
//    }
    
    // (지혜)
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UsedBookRepository usedBookRepository;
 
    // 중고판매글에서 '채팅하기' 버튼 클릭시
    @PostMapping("/chat")
    @ResponseBody
    public String getWebSocketWithSockJs(@AuthenticationPrincipal UserSecurityDto userDto, Integer usedBookId, Integer sellerId) throws IOException {
        
        Integer buyerId = userDto.getId();
        
        log.info("채팅 존재여부 확인: usedBookId={}, sellerId={}", usedBookId, sellerId);
        
        //이미 chat이 만들어져있는지 확인
        Chat chatExistsOrNot = chatRepository.findByUsedBookIdAndBuyerId(usedBookId, buyerId);
        
        if (chatExistsOrNot != null) {
            // 이미 채팅을 하고 있다면
            log.info("이미 채팅 중입니다!");
            return "/chat?chatRoomId="+chatExistsOrNot.getChatRoomId();
            
        } else {
            // 새로운 채팅 시작이라면
            log.info("새 채팅을 시작합니다!");
            // chat 생성 (+ txt 파일 생성)       
            Integer newChatRoomId = chatService.createChat(usedBookId, sellerId, buyerId);       
            return "/chat?chatRoomId="+newChatRoomId;
        }
    }
    
    
    @GetMapping("/chat")
    public void showChatWindow(@AuthenticationPrincipal UserSecurityDto userDto, Integer chatRoomId, Model model) throws IOException {
        log.info("챗창 오픈 Get Mapping");
        
        // TODO: chatRoomId를 통해 건너 건너 찾는 거 말고, 더 효율적으로 찾을 수 없나??
        // 필요한 model 정보: 내(현재 로그인되어 있는) User 객체, 중고판매글 UsedBook 객체, 채팅상대 User 객체
        
        // chatRoomId로 Chat 찾기
        Chat chat = chatRepository.findByChatRoomId(chatRoomId);
        model.addAttribute("chatInfo", chat);
        
        // chatHistory 불러 오기
        List<ChatReadDto> chatHistory = chatService.readChatHistory(chat);
        //chatHistory Model에 저장해 View로 전달
        model.addAttribute("chatHistory", chatHistory);
        
        // 중고판매글 정보 불러 오기
        UsedBook usedBook = usedBookRepository.findById(chat.getUsedBookId()).get();
        model.addAttribute("usedBook", usedBook);
        
        // 내 정보 불러 오기
        User loginUser = userRepository.findById(userDto.getId()).get();
        model.addAttribute("loginUser", loginUser);
        
        // 채팅 상대 정보 불러 오기
        if (userDto.getId() == chat.getSellerId()) {    // 내가 판매자면
            User chatWith = userRepository.findById(chat.getBuyerId()).get();
            model.addAttribute("chatWith", chatWith);
            
        } else {   // 내가 구매자면?
            User chatWith = userRepository.findById(chat.getSellerId()).get();
            model.addAttribute("chatWith", chatWith);
            
        }
    }
    
    // (홍찬) 내 대화 목록 불러오기
    @GetMapping("/chat/list")
    public String openMyChatList(@AuthenticationPrincipal UserSecurityDto userDto, Model model) throws IOException {
        Integer loginUserId = userDto.getId();

        log.info("잘 도착햇나{}",loginUserId);
        // 최근에 업데이트된 날짜 순으로 받아온 내가 대화중인 대화들
        List<Chat> chat = chatRepository.findByBuyerIdOrSellerIdOrderByModifiedTimeDesc(loginUserId, loginUserId);
        List<String> cl = new ArrayList<>();
        for (Chat c : chat) {
            log.info("방번호{}",c.getChatRoomId());
            cl.add(chatService.readLastThreeLines(c));
        }
        
        model.addAttribute("myChatList" ,cl);
        return "";
    }
}