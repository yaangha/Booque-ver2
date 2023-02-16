package site.book.project.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Chat;

@Slf4j
@SpringBootTest
public class ChatRepositoryTest {
    
    @Autowired
    private ChatRepository chatRepository;
    
    @Test
    public void testSave() {
        
        Chat chat1 = Chat.builder().usedBookId(1).sellerId(1).buyerId(2).build();
        
        log.info("save 전 {} | {} | {}", chat1, chat1.getCreatedTime(), chat1.getModifiedTime());
        
        chatRepository.save(chat1);
        
        log.info("save 후 {} | {} | {}", chat1, chat1.getCreatedTime(), chat1.getModifiedTime());
        
        Assertions.assertNotNull(chatRepository);
        
    }

}
