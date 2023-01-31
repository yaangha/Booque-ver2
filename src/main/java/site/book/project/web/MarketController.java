package site.book.project.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import groovy.transform.AutoExternalize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Book;
import site.book.project.domain.UsedBook;
import site.book.project.dto.MarketCreateDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.BookRepository;
import site.book.project.repository.SearchRepository;
import site.book.project.service.SearchService;
import site.book.project.service.UsedBookService;

@Slf4j
@Controller
@RequestMapping("/market")
@RequiredArgsConstructor
public class MarketController {
    
    private final SearchService searchService;
    private final SearchRepository searchRepository;
    private final UsedBookService usedBookService;
    private final BookRepository bookRepository;
    
    
    @GetMapping("/main") // /market/main 부끄마켓 메인 페이지 이동
    public void main() {
        
    }
    
//    @GetMapping("/create") // /market/create 중고판매글 작성 페이지 이동
//    public void create(@AuthenticationPrincipal UserSecurityDto u , Integer bookId, Model model) {
//    	log.info("책 번호 {}", bookId);
//    	Book book = bookRepository.findById(bookId).get();
//    	
//    	Integer usedBookId = usedBookService.create(bookId, u.getId());
//    	log.info("책 검색을 하고 버튼을 누르면 디비단에 저장이 되게 해야해. {}, {}, {}", bookId, u.getId(), usedBookId);
//    	model.addAttribute("book", book);
//    	model.addAttribute("usedBookId", usedBookId);
//    }
    @GetMapping("/create") // /market/create 중고판매글 작성 페이지 이동
    public void create(Model model) {

    }
    
    @PostMapping("/create")
    public String createPost( @AuthenticationPrincipal UserSecurityDto userDto, MarketCreateDto dto,
    							Integer usedBookId) {
    	// 총 세개의 테이블을 크리에이트 해야함
    	
    	dto.setUserId(userDto.getId());
    	
    	usedBookService.create( usedBookId, dto );
    	
    	return "redirect:/market/main";
    }
    
    

    
    
    
    
    @GetMapping("/detail") // /market/detail 중고판매글 상세 페이지 이동
    public void detail() {
    	
    }
    
    
    
    /**
     * ajax를 이용해서 키워드 받고 검색하기
     * @param keyword 검색할 단어(isbn은 아직은 제외됨)
     * @return
     */
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<Book>> bookList(String keyword){
        log.info("확인 해야지 키워드가 잘 넘어갔는지{}   ",keyword);
        
        List<Book> searhList = searchRepository.unifiedSearchByKeyword(keyword);
        return ResponseEntity.ok(searhList);
    }
    
    /**
     * UsedBook 테이블에 userId, bookId 먼저 저장하기
     * @param u user 정보
     * @param bookId  선택한 책의 PK
     * @return  Map타입을 통해 Book과 usedBookId(PK)를 넘김
     */
    @GetMapping("/createUsed")
    @ResponseBody
    public Map<String, Object>  createUsedBook(@AuthenticationPrincipal UserSecurityDto u, Integer bookId) {
        // 저장
        
        Map<String, Object> maps = new HashMap<>();
        
        Integer usedBookId = usedBookService.create(bookId, u.getId());
        Book book = bookRepository.findById(bookId).get();
        
        maps.put("book", book);
        maps.put("usedBookId", usedBookId);
       return maps; 
    }
    

}
