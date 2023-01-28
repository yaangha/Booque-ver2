package site.book.project.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Book;
import site.book.project.repository.BookRepository;
import site.book.project.service.SearchService;

@Slf4j
@Controller
@RequestMapping("/market")
@RequiredArgsConstructor
public class MarketController {
    
    private final SearchService searchService;
    private final BookRepository bookRepository;
    
    @GetMapping("/main") // /market/main 부끄마켓 메인 페이지 이동
    public void main() {
        
    }
    
    @GetMapping("/create") // /market/create 중고판매글 작성 페이지 이동
    public void create(Integer bookId, Model model) {
    	log.info("책 번호 {}", bookId);
        // 책 정보 넘기고 여기서 저장을 먼저 하는게 나을까?  아님 한번에 저장을 하는게 나을까?
    	// 책 정보를 우선 넘기자.
    	Book book = bookRepository.findById(bookId).get();
    	
    	
    	model.addAttribute("book", book);
    	
    	
    }
    
    
    /**
     * 책 검색 후 디비 판매글로 넘어가기
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
        
        return "/market/search";
    }
    
}
