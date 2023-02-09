package site.book.project.repository;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.UsedBook;
import site.book.project.domain.UsedBookImage;

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
        Assertions.assertNotNull(imageRe);
        
        List<UsedBookImage> img = imageRe.findByUsedBookId(76);
        
        for(UsedBookImage i :img) {
            log.info("보여줘~! {}",i);
            
            
        }
    }
    
    
    
}
