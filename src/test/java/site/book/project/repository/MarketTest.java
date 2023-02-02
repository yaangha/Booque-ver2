package site.book.project.repository;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.UsedBook;

@Slf4j
@SpringBootTest
public class MarketTest {
    
    @Autowired
    private UsedBookRepository usedBookRepository;

    @Autowired
    private UsedBookPostRepository postRepository;
    
    @Autowired
    private UsedBookImageRepository imageRe;
    
    @Test
    public void test() {
        Assertions.assertNotNull(usedBookRepository);
        
        List<UsedBook> list = usedBookRepository.searchM("", "회복");
        
        for(UsedBook u : list) {
            
            log.info(" 함 읽어보자 ~!{}", u);
            
        }
        
        log.info("읽어온 리스트의 길이 {}", list.size());
        
    }
    
    
    
}
