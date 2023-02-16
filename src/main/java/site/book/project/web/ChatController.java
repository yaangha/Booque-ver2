package site.book.project.web;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Chat;
import site.book.project.domain.ChatAssist;
import site.book.project.domain.UsedBook;
import site.book.project.domain.UsedBookImage;
import site.book.project.domain.User;
import site.book.project.dto.ChatListDto;
import site.book.project.dto.ChatReadDto;
import site.book.project.dto.UnreadDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.ChatAssistRepository;
import site.book.project.repository.ChatRepository;
import site.book.project.repository.ReservedRepository;
import site.book.project.repository.UsedBookImageRepository;
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
    @Autowired
    private UsedBookImageRepository usedBookImageRepository;
    @Autowired
    private ChatAssistRepository chatAssistRepository;
    @Autowired
    private ReservedRepository reservedRepository;
 
    // 중고판매글에서 '채팅하기' 버튼 클릭시
    @PostMapping("/chat")
    @ResponseBody
    public String getWebSocketWithSockJs(@AuthenticationPrincipal UserSecurityDto userDto, Integer usedBookId, Integer sellerId, Model model) throws IOException {
        
        Integer buyerId = userDto.getId();
        
        log.info("채팅 존재여부 확인: usedBookId={}, sellerId={}", usedBookId, sellerId);
        
        //이미 chat이 만들어져있는지 확인
        Chat chatExistsOrNot = chatRepository.findByUsedBookIdAndBuyerId(usedBookId, buyerId);
        
        if (chatExistsOrNot != null) {
            // 이미 채팅을 하고 있다면
            log.info("이미 채팅 중입니다!");
            User dto = userRepository.findById(sellerId).get();
            Integer whenRead = chatService.updateReadChat(dto.getNickName(), chatExistsOrNot.getChatRoomId(), 1);
            model.addAttribute("read", whenRead);
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
        
        Integer loginUserId = userDto.getId();
        User loginUser = userRepository.findById(loginUserId).get();
        String loginNickName = loginUser.getNickName();
        // 뷰에 보여 줄 내 정보
        model.addAttribute("loginUser", loginUser);
        
        List<ChatListDto> list = chatService.loadChatList(loginUserId);
        
        // 뷰에 보여 줄 채팅방 정보들(리스트)
        
        List<Chat> myChats = chatRepository.findByBuyerIdOrSellerIdOrderByModifiedTimeDesc(loginUserId, loginUserId);

        
        
        // 최신 메세지 내용 불러 오기
        List<String> cl = new ArrayList<>();
        for (Chat c : myChats) {
            log.info("방번호{}",c.getChatRoomId());
            cl.add(chatService.readLastThreeLines(c));
        }
            model.addAttribute("recentMessage" ,cl);
            
            // 최신 메세지 내용 불러 오기  Dto에 넣어서 보냄
        for(int i=0; i<list.size(); i++) {
            String last = chatService.readLastThreeLines(myChats.get(i));
            list.get(i).setLastMessage(last);
            
        }
        
        model.addAttribute("data", list);
        
        List<ChatReadDto> chatHistory = null;
        if(chatRoomId==null) {  // 판매자가 상단바의 채팅 아이콘을 클릭해 들어갔을 때 보이는 첫 창
             chatHistory = chatService.readChatHistory(myChats.get(0));

             ChatListDto usedbook = list.get(0);
             model.addAttribute("usedBook", usedbook);
             // chatListDto를 같
             model.addAttribute("chatWith", usedbook);
             
             
          // 예약자 정보
             if (reservedRepository.findByUsedBookId(usedbook.getUsedBookId()) != null) {
                 Integer reservedId = reservedRepository.findByUsedBookId(usedbook.getUsedBookId()).getUserId();
                 String reservedName = userRepository.findById(reservedId).get().getNickName();
                 model.addAttribute("reservedId", reservedId);
                 model.addAttribute("reservedName", reservedName);
             }
             
             
             // 안읽은 메세지 개수
             ChatAssist CA = chatAssistRepository.findByChatRoomId(myChats.get(0).getChatRoomId());
             Integer unreadCount = CA.getReadCount();
             String withName = CA.getNickName();
             if(unreadCount != 0) {
                 if (loginNickName.equals(withName)) {
                     unreadCount = chatService.updateReadChat(withName, myChats.get(0).getChatRoomId(), 1);
                     model.addAttribute("rcount", unreadCount);
                 } else {
                 model.addAttribute("rcount", unreadCount);
                 }
                 } else {
                     model.addAttribute("rcount", 0);
                 }
        } else {    // 구매자가 '채팅하기'를 눌러 들어갔거나,  판매자가 상단바 채팅아이콘 클릭 후 어느 특정 방을 클릭했을 때

            Chat chatById = chatRepository.findByChatRoomId(chatRoomId);
            chatHistory = chatService.readChatHistory(chatById);

            // 책 정보
            UsedBook u = usedBookRepository.findById(chatById.getUsedBookId()).get();
            UsedBookImage img = usedBookImageRepository.findByUsedBookId(u.getId()).get(0);
            
            // 예약자 정보
            if (reservedRepository.findByUsedBookId(u.getId()) != null) {
                Integer reservedId = reservedRepository.findByUsedBookId(u.getId()).getUserId();
                String reservedName = userRepository.findById(reservedId).get().getNickName();
                model.addAttribute("reservedId", reservedId);
                model.addAttribute("reservedName", reservedName);
            }
            
            //  채팅 상대 정보



            ChatListDto usedbook = ChatListDto.builder().usedBookImage(img.getFileName())
                                                .price(u.getPrice()).status(u.getStatus())
                                                .usedTitle(u.getTitle())
                                                .usedBookTitle(u.getBookTitle()).userId(u.getUserId())
                                                .chatRoomId(chatRoomId)
                                                .price(u.getPrice()).status(u.getStatus()).usedTitle(u.getTitle())
                                                .chatRoomId(chatRoomId).usedBookId(u.getId())
                                                .build();


            ChatListDto chatPerson = null;
            if(loginUserId.equals(chatById.getSellerId())) {
                User chatWith = userRepository.findById(chatById.getBuyerId()).get();

                chatPerson = ChatListDto.builder().chatWithImage(chatWith.getUserImage()).chatWithLevel(chatWith.getBooqueLevel())
                                .chatWithName(chatWith.getNickName()).chatWithId(chatWith.getId()).build();
            } else {
                User chatWith = userRepository.findById(chatById.getSellerId()).get();

                chatPerson = ChatListDto.builder().chatWithImage(chatWith.getUserImage()).chatWithLevel(chatWith.getBooqueLevel())
                .chatWithName(chatWith.getNickName()).chatWithId(chatWith.getId()).build();

            }


            model.addAttribute("usedBook", usedbook);
            model.addAttribute("chatWith", chatPerson);
            // 안읽은 메세지 개수
            ChatAssist CA = chatAssistRepository.findByChatRoomId(chatRoomId);
            Integer unreadCount = CA.getReadCount();
            String withName = CA.getNickName();
            if(unreadCount != 0) {
                if (loginNickName.equals(withName)) {
                    unreadCount = chatService.updateReadChat(withName, chatRoomId, 1);
                    model.addAttribute("rcount", unreadCount);
                } else {
                model.addAttribute("rcount", unreadCount);
                }
            } else {
                model.addAttribute("rcount", 0);
            }
            
        }

            // chatHistory 불러 오기
            //chatHistory Model에 저장해 View로 전달
            model.addAttribute("chatHistory", chatHistory);
            
            // 안읽음 리스트를 위해 전달.
            List<UnreadDto> unreadInfo = new ArrayList<>();
            Integer unread = 0;
            String unreadNickName = "";
            for (Chat c : myChats) {
                unread = chatAssistRepository.findByChatRoomId(c.getChatRoomId()).getReadCount();
                unreadNickName = chatAssistRepository.findByChatRoomId(c.getChatRoomId()).getNickName();
                UnreadDto dto = UnreadDto.builder().chatRoomId(c.getChatRoomId()).unread(unread).unreadNickName(unreadNickName).build();
                unreadInfo.add(dto);
            }
            model.addAttribute("unreadInfo", unreadInfo);
            
    }
    
//    // (홍찬) 내 대화 목록 불러오기
//    @GetMapping("/chat/list")
//    public String openMyChatList(@AuthenticationPrincipal UserSecurityDto userDto, Model model) throws IOException {
//        Integer loginUserId = userDto.getId();
//
//        log.info("잘 도착햇나{}",loginUserId);
//        // 최근에 업데이트된 날짜 순으로 받아온 내가 대화중인 대화들
//        List<Chat> chat = chatRepository.findByBuyerIdOrSellerIdOrderByModifiedTimeDesc(loginUserId, loginUserId);
//        List<String> cl = new ArrayList<>();
//        for (Chat c : chat) {
//            log.info("방번호{}",c.getChatRoomId());
//            cl.add(chatService.readLastThreeLines(c));
//        }
//        
//        model.addAttribute("myChatList" ,cl);
//        return "chat";
//    }
    
    // (지혜) 최신 업데이트시간을 ㅇ초 전, ㅇ분 전, ㅇ시간 전, ㅇ일 전 식으로 바꿔 출력하기
    public static String convertTime(LocalDateTime modifiedTime) {
        
        // 현재 시간
        LocalDateTime now = LocalDateTime.now();
        
        // 시간차 구하기(초)
        long diffTime = ChronoUnit.SECONDS.between(modifiedTime, now);
        
        String ago;
        
        if (diffTime == 0) {
            ago = "방금 전";
        } else if (diffTime < 60) {
            ago = diffTime + "초 전";
        } else if (diffTime < 60 * 60) {
            ago = (diffTime/60) + "분 전";
        } else if (diffTime < 60 * 60 * 24) {
            ago = (diffTime/60/60) + "시간 전";
        } else if (diffTime < 60 * 60 * 24 * 10) {
            ago = (diffTime/60/60/24) + "일 전";
        } else {  // 10일보다 오래된 채팅은 날짜를 표시
            ago = modifiedTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        }
        
        return ago;
        
    }
    
    
    @GetMapping("/chat/api/list")
    @ResponseBody
    public List<ChatListDto> chatlist(Integer userId) throws IOException{
        log.info("채팅창 리스트 바꿔야지ㅣ이ㅣ잉{}", userId);
        List<ChatListDto> list = chatService.loadChatList(userId);
        // 뷰에 보여 줄 채팅방 정보들(리스트)
        
        List<Chat> myChats = chatRepository.findByBuyerIdOrSellerIdOrderByModifiedTimeDesc(userId, userId);
        

        // 최신 메세지 내용 불러 오기  Dto에 넣어서 보냄
        for(int i=0; i<list.size(); i++) {
            String last = chatService.readLastThreeLines(myChats.get(i));
            list.get(i).setLastMessage(last);
        
        }
        
        log.info("채팅창 리스트 바꿔야지ㅣ이ㅣ잉{}",list);
        
        return list;
    }
}