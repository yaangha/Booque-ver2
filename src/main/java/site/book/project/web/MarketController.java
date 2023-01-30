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
    
    
    /**
     * 책 검색 후 디비 판매글로 넘어가기
     * ajax를 이용해서 추천 검색어처럼 만들수 있지 않을까? 
     * 은정
     */
    @GetMapping("/search") // /market/create 중고판매글 작성 페이지 이동
    public void searchUsed(Model model) {
    	Page<Book> searchList = null;
    	model.addAttribute("searchList", searchList);
        
    }
    // 은정
    @GetMapping("/searchResult") // 검색후 페이지?
    public String search(String typeUsed, String keywordUsed, Model model, @PageableDefault(size=10) Pageable pageable) {
        
        Page<Book> searchList = searchService.search(typeUsed, keywordUsed, "0", pageable);

        // 페이징 - 시작페이지, 끝 페이지 (신상품순, 최저가순, 최고가순)
        int startPage = Math.max(1, searchList.getPageable().getPageNumber() - 3);
        int endPage = Math.min(searchList.getTotalPages(), searchList.getPageable().getPageNumber() + 3);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("searchList", searchList);
        model.addAttribute("reKeywordUsed", keywordUsed);
        model.addAttribute("reTypeUsed", typeUsed);
        
        return "/market/create";
    }
    
    
    
    
    @GetMapping("/detail") // /market/detail 중고판매글 상세 페이지 이동
    public void detail() {
    	
    }
    
    
    
    
    @GetMapping("/searchT")
    @ResponseBody
    public ResponseEntity<List<Book>> bookList(String keyword){
        log.info("확인 해야지 키워드가 잘 넘어갔는지{}   ",keyword);
        
        List<Book> searhList = searchRepository.unifiedSearchByKeyword(keyword);
        return ResponseEntity.ok(searhList);
    }
    
    @GetMapping("/createM")
    @ResponseBody
    public Map<String, Object>  createUsedBook(@AuthenticationPrincipal UserSecurityDto u, Integer bookId, Model mdoel) {
        log.info("잘 넘어 왔니!! 유저{} ,챡번호 {}", u, bookId);
        // 저장
        
        Map<String, Object> maps = new HashMap<>();
        
        Integer usedBookId = usedBookService.create(bookId, u.getId());
        Book book = bookRepository.findById(bookId).get();
        
        maps.put("book", book);
        maps.put("usedBookId", usedBookId);
       return maps; 
    }
    

}
