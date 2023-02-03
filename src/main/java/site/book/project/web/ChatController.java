package site.book.project.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Chat;
import site.book.project.domain.User;
import site.book.project.dto.ChatReadDto;
import site.book.project.repository.ChatRepository;
import site.book.project.service.ChatService;

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
    
    
    
    
    // (지혜) 아래 구현 중
    /*
    
    @Autowired
    private SimpMessagingTemplate simpMessage;
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private ChatRepository chatRepository;
 
    //채팅으로 거래하기(productInfo 화면)
    @GetMapping("/chatMessage")
    public String getWebSocketWithSockJs(Model model, HttpSession session, 
            @ModelAttribute("chat") Chat chat) throws IOException {
        
        //중고판매글 detail 화면에서 Chat화면에 전달해줄 parameter
        User buyer = (User) session.getAttribute("login");
        Integer buyerId = buyer.getId();
        chat.setBuyerId(buyerId);
        
        
        //이미 chat이 만들어져있는지 확인
        if (chatRepository.findByUsedBookIdAndBuyerId(usedBookId, buyerId))
        
        
        if (chatService.countByChatId(chat.getPr_id(), chat.getBuyerId()) > 0) {
            //get ChatRoomInfo
            Chat chatTemp = chatService.findByChatId(chat.getPr_id(), chat.getBuyerId());
            //load existing chat history
            List<ChatReadDto> chatHistory = chatService.readChatHistory(chatTemp);
            //transfer chatHistory Model to View
            model.addAttribute("chatHistory", chatHistory);
            
        } else {
            //chat 생성            
            chatService.createChat(chat);            
            //text file 생성
            chatService.createFile(chat.getPr_id(), chatService.getId(chat.getPr_id(), chat.getBuyerId()));                                
        }
 
            //chat Add 시 생성될 chatId
            chat.setId(chatService.getId(chat.getPr_id(), chat.getBuyerId()));
            
            //chatRoom 객체 Model에 저장해 view로 전달
            model.addAttribute("chatRoomInfo", chat);
        
        return "chatBroadcastProduct";
    
    */
    
    
}
