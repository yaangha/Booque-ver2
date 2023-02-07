package site.book.project.web;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Chat;
import site.book.project.dto.ChatReadDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.ChatRepository;
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
 
    // 중고판매글에서 '채팅하기' 버튼 클릭시
    @GetMapping("/chat")
    public String getWebSocketWithSockJs(@AuthenticationPrincipal UserSecurityDto userDto, Integer sellerId, Integer usedBookId, Model model, 
            @ModelAttribute("chat") Chat chat) throws IOException {
        //중고판매글 detail 화면에서 Chat화면에 전달해줄 parameter

        Integer buyerId = userDto.getId();
        //이미 chat이 만들어져있는지 확인
        Chat chatExistsOrNot = chatRepository.findByUsedBookIdAndBuyerId(usedBookId, buyerId);
        if (chatExistsOrNot != null) {
            // 이미 채팅을 하고 있다면
            log.info("이미 채팅 중입니다!");
            // chatHistory 불러 오기
            List<ChatReadDto> chatHistory = chatService.readChatHistory(chatExistsOrNot);
            //chatHistory Model -> View
            model.addAttribute("chatHistory", chatHistory);
            chat.setChatRoomId(chatExistsOrNot.getChatRoomId());
        } else {
            // 새로운 채팅 시작이라면
            log.info("새 채팅을 시작합니다!");
            // chat 생성 (+ txt 파일 생성)
            Integer newChatRoomId = chatService.createChat(usedBookId, sellerId, buyerId);       
            chat.setChatRoomId(newChatRoomId);
        }
        
        
      //중고판매글 detail 화면에서 Chat화면에 전달해줄 parameter
        
        chat.setBuyerId(buyerId);
        chat.setSellerId(sellerId);
        chat.setUsedBookId(usedBookId);
            
        

            // Chat 객체 Model에 저장해 view로 전달
            model.addAttribute("chatInfo", chat);
        
        return "chat";
        
    }
}
