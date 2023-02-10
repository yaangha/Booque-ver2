package site.book.project.repository;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Book;
import site.book.project.domain.BookComment;
import site.book.project.domain.BookWish;
import site.book.project.domain.Post;
import site.book.project.dto.BookCommentReadDto;
import site.book.project.dto.BookCommentRegisterDto;
import site.book.project.service.BookCommentService;
import site.book.project.service.BookService;
import site.book.project.service.CategoryService;
import site.book.project.service.PostService;

@Slf4j
@SpringBootTest
public class BookRepositoryTests {
	
	@Autowired
	private  UsedBookWishRepository uRepository;

	

	
	@Test
	public void test() {
	    Assertions.assertNotNull(uRepository);
	  //  List<BookComment> list = bookCommentRepository.findByUserIdOrderByCreatedTimeDesc(21);
 	  //  List<BookComment> list =bookCommentService.readByUserId(21);
//	    for(BookComment c : list) {
//	        
//	        log.info("버자 보자아아ㅏ아ㅏ아아앙 리스트트트ㅡ트 {}", c );
//	    }

	    log.info(" 됐을걸~~~~ {} ", uRepository.findByUserId(1));
	    
	}
	
	
	
	

}
