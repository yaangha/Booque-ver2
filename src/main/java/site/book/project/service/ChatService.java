package site.book.project.service;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Chat;
import site.book.project.domain.UsedBook;
import site.book.project.domain.UsedBookImage;
import site.book.project.domain.User;
import site.book.project.dto.ChatListDto;
import site.book.project.dto.ChatReadDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.ChatRepository;
import site.book.project.repository.UsedBookImageRepository;
import site.book.project.repository.UsedBookRepository;
import site.book.project.repository.UserRepository;
import site.book.project.web.ChatController;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ChatService {
    
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final UsedBookRepository usedBookRepository;
    private final UsedBookImageRepository usedBookImageRepository;
    
    @Value("${file.upload-dir}")
    String fileUploadPath; 
    
    // DB의 Chat 테이블에 새 데이터 행 생성
    // fileName 컬럼은 일단 null로 비워 둠
    public Integer createChat(Integer usedBookId, Integer sellerId, Integer buyerId) throws IOException {
        
        Chat chat = Chat.builder().usedBookId(usedBookId).sellerId(sellerId).buyerId(buyerId)
                    .createdTime(LocalDateTime.now()).modifiedTime(LocalDateTime.now())
                    .build();

        chatRepository.save(chat);
        createFile(chat.getChatRoomId(), usedBookId);
        
        Chat dto = chatRepository.findByUsedBookIdAndBuyerId(usedBookId, buyerId);
        // return dto;
        return chat.getChatRoomId();
    }
    
    // 로컬 저장소(D:\\study\\chat-txt/)에 txt 파일 생성
    // 파일 경로:  D:\\study\\chat-txt/중고판매글id_채팅방id.txt    (예: D:\\study\\chat-txt/2_1.txt)
    public void createFile(Integer chatRoomId, Integer usedBookId) throws IOException {
        
        String fileName = usedBookId+ "_" + chatRoomId + ".txt";
        String pathName = fileUploadPath + fileName;
        
        // Java의 File 클래스와 createNewFile() 메서드 이용해 파일 생성
        File chatFile = new File(pathName);
        chatFile.createNewFile();
        updateChat(chatRoomId, fileName);
    }
    
    @Transactional
    public void updateChat(Integer chatRoomId, String fileName) {
        // 파일이 생성되면 chat 테이블의 fileName 컬럼에 위의 파일명을 업데이트
        
        Chat entity = chatRepository.findById(chatRoomId).get();
        entity.update(fileName);
        
    }
    
    
    
    // (이미 채팅 기록이 있을 경우) 기록 불러 오기
    public List<ChatReadDto> readChatHistory(Chat chat) throws IOException {
        
        List<ChatReadDto> chatHistory = new ArrayList<ChatReadDto>();
        
        // fileName 컬럼을 통해 파일의 경로 찾기, 파일 읽기
        String pathName = fileUploadPath + chat.getFileName();
        FileReader fr = new FileReader(pathName);
        BufferedReader br = new BufferedReader(fr);
        
        ChatReadDto chatLines = new ChatReadDto();
        String chatLine;
        int index = 1;   // 채팅 메시지블럭 하나의 줄(row) 인덱스
        
        while ((chatLine = br.readLine()) != null) {
            
            //1개 메시지는 3줄(보낸사람,메시지내용,보낸시간)로 구성돼있음
            int answer = index % 3;
            if (answer == 1) {
                //보낸사람 항목에 출력 후 다음 줄로
                chatLines.setSender(chatLine);
                index++;
            } else if (answer == 2) {
                //메시지내용 항목에 출력 후 다음 줄로
                chatLines.setMessage(chatLine);
                index++;
            } else {
                //보낸시간 항목에 출력
                chatLines.setSendTime(chatLine);
                // 채팅 기록 List에 저장
                chatHistory.add(chatLines);
                //객체 초기화, 줄(row)인덱스 초기화
                chatLines = new ChatReadDto();
                index = 1;
            }            
        }
        
        br.close();
        fr.close();
        
        return chatHistory;
    }
    
    
    
    // txt 파일에 새로운 채팅 기록 추가(append)
    public void appendMessage(Integer chatRoomId, ChatReadDto dto) throws IOException {
        
//        Integer chatRoomId = dto.getChatRoomId();
        
        Chat chatAppend = chatRepository.findByChatRoomId(chatRoomId);
                
        String pathName = fileUploadPath + chatAppend.getFileName();
        
        // 파일에 쓰기(append)
        FileOutputStream fos = new FileOutputStream(pathName, true);
        String message = dto.getMessage();
        String sender = dto.getSender();
        String sendTime = dto.getSendTime();
        System.out.println("print:" + message);
        
        String writeContent = sender + "\n" + message + "\n" + "[" +  sendTime + "]" + "\n";
        
        byte[] b = writeContent.getBytes();
        
        fos.write(b);
        
        chatAppend.setModifiedTime(LocalDateTime.now());
        
        fos.close();
        
        // 읽음 여부 표시 기능 (TODO)
        /*
        if (senderId.equals(chat.getSellerId())) {
            updateChatReadBuy(chat.getId(), 0);
        } else {
            updateChatReadSell(chat.getId(), 0);
        } */
        
    }
    
    // (홍찬) 마지막 채팅 가져오기
    public String readLastThreeLines (Chat chat) throws IOException {
        // ChatReadDto recentChat = new ChatReadDto();
        String recentMessage = "";
        
        // fileName 컬럼을 통해 파일의 경로 찾기, 파일 읽기
        String pathName = fileUploadPath + chat.getFileName();
        
        // 1. RandomAcessFile, 마지막 라인을 담을 String, 읽을 라인 수 "r" 읽기모드
        RandomAccessFile chatResourceFile = new RandomAccessFile(pathName, "r");
        StringBuilder lastLine = new StringBuilder();
        int lineCount = 2; // 마지막 2줄을 읽겠다는 내용!

        // 2. 전체 파일 길이
        long fileLength = chatResourceFile.length();

        // 3. 포인터를 이용하여 뒤에서부터 앞으로 데이터를 읽는다.
        for (long pointer = fileLength - 2; pointer >= 0; pointer--) {

            // 3.1. pointer를 읽을 글자 앞으로 옮긴다.
            chatResourceFile.seek(pointer);

            // 3.2. pointer 위치의 글자를 읽는다.
            char c = (char) chatResourceFile.read();
            
            // 3.3. 줄바꿈이 2번(lineCount) 나타나면 더 이상 글자를 읽지 않는다.
            if (c == '\n') {
                lineCount--;
                if (lineCount == 0) {
                    // 원하는 pointer가 위치해 있을 때 "UTF-8"이라는 변환을 한 후 그 줄의 readline으로 읽어옴.
                    recentMessage = new String(chatResourceFile.readLine().getBytes("ISO-8859-1"),"UTF-8");
                    break;
                }
            }
            
            // 3.4. 결과 문자열의 앞에 읽어온 글자(c)를 붙여준다.
            lastLine.insert(0, c);
        }
        chatResourceFile.close();
        // 4. 결과 출력
        //System.out.println(lastLine);
        return recentMessage;
    }

    private String toConvert (String Unicodestr) throws UnsupportedEncodingException {
        return new String (Unicodestr.getBytes("8859_1"),"KSC5601");
        }
    
    
    // 내가 (판매자 혹은 구매자로) 포함된 모든 채팅방 불러와 dto에 데이터 추가
    public List<ChatListDto> loadChatList (Integer loginUserId) throws IOException {
        
        List<ChatListDto> list = new ArrayList<>();
        User chatWith = new User();
        
        // 내 유저id로 내가 포함되어 있는 모든 채팅방 찾기
        List<Chat> myChats = chatRepository.findByBuyerIdOrSellerIdOrderByModifiedTimeDesc(loginUserId, loginUserId);
        
        for (Chat chat : myChats) {
            
            // 채팅 상대 정보 불러 오기
            log.info("loginUserId={}, sellerID={}", loginUserId, chat.getSellerId());
            
            if (loginUserId.equals(chat.getSellerId())) {    // 내가 판매자면
                chatWith = userRepository.findById(chat.getBuyerId()).get();
                log.info("나는 판매자임 {}");
            } else {   // 내가 구매자면
                chatWith = userRepository.findById(chat.getSellerId()).get();
                log.info("나는 구매자 {}, {}", loginUserId, chat.getSellerId());
            }
            
            log.info("누구랑 채팅하나?? {}",chatWith);
            // 중고판매글 정보 불러 오기
            Integer usedBookId = chat.getUsedBookId();
            UsedBook usedBook = usedBookRepository.findById(usedBookId).get();
            List<UsedBookImage> imgList = usedBookImageRepository.findByUsedBookId(usedBookId);
            
            ChatListDto dto = ChatListDto.builder()
                    .chatRoomId(chat.getChatRoomId()).modifiedTime(ChatController.convertTime(chat.getModifiedTime()))
                    .usedBookId(usedBookId).usedBookImage(imgList.get(0).getFileName()).usedTitle(usedBook.getTitle()).price(usedBook.getPrice())
                    .status(usedBook.getStatus())
                    .chatWithName(chatWith.getNickName()).chatWithImage(chatWith.getUserImage()).chatWithLevel(chatWith.getBooqueLevel())
                    .usedTitle(usedBook.getTitle())
                    .build();
            
            list.add(dto);
            
        }
        return list;
        
    }
    
    
}
