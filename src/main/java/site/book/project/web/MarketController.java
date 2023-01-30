package site.book.project.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Book;
import site.book.project.domain.UsedBook;
import site.book.project.domain.UsedBookPost;
import site.book.project.domain.User;
import site.book.project.dto.MarketCreateDto;
import site.book.project.dto.UserSecurityDto;
import site.book.project.repository.BookRepository;
import site.book.project.repository.UsedBookPostRepository;
import site.book.project.repository.UsedBookRepository;
import site.book.project.repository.UserRepository;
import site.book.project.service.SearchService;
import site.book.project.service.UsedBookService;

@Slf4j
@Controller
@RequestMapping("/market")
@RequiredArgsConstructor
public class MarketController {
    
    private final SearchService searchService;
    private final UsedBookService usedBookService;
    private final BookRepository bookRepository;
    private final UsedBookRepository usedBookRepository;
    private final UsedBookPostRepository usedBookPostRepository;
    private final UserRepository userRepository;
    
    @GetMapping("/main") // /market/main 부끄마켓 메인 페이지 이동
    public void main() {
        
    }
    
    @GetMapping("/create") // /market/create 중고판매글 작성 페이지 이동
    public void create(@AuthenticationPrincipal UserSecurityDto u , Integer bookId, Model model) {
    	log.info("책 번호 {}", bookId);
    	Book book = bookRepository.findById(bookId).get();
    	
    	Integer usedBookId = usedBookService.create(bookId, u.getId());
    	log.info("책 검색을 하고 버튼을 누르면 디비단에 저장이 되게 해야해. {}, {}, {}", bookId, u.getId(), usedBookId);
    	model.addAttribute("book", book);
    	model.addAttribute("usedBookId", usedBookId);
    }
    
    @PostMapping("/create")
    public String createPost( @AuthenticationPrincipal UserSecurityDto userDto, MarketCreateDto dto,
    							Integer usedBookId) {
    	// 총 세개의 테이블을 크리에이트 해야함
    	
    	dto.setUserId(userDto.getId());
    	
    	log.info("자자자 함 봐야지!! {}", dto);
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
        
        return "/market/search";
    }
    
    @GetMapping("/detail") // /market/detail 중고판매글 상세 페이지 이동
    public void detail(Integer usedBookId, Model model) {
        // 책 정보 불러오기(bookId) -> postId로 bookId 찾기
        // 판매글 정보 불러오기
        // 판매글제목 & 가격 & 수정시간 & 지역 & 본문 & 판매여부 & 책상태 & 이미지
        UsedBook usedBook = usedBookRepository.findById(usedBookId).get();        
        UsedBookPost usedBookPost = usedBookPostRepository.findByUsedBookId(usedBookId);
        User user = userRepository.findById(usedBook.getUserId()).get(); // 작성자의 정보
        Book book = bookRepository.findById(usedBook.getBookId()).get();
        
        String content = usedBookPost.getContent();
        
        // 판매하는 책과 동일한 책(다른 중고책) 리스트
        List<UsedBook> otherUsedBookList = usedBookService.readOtherUsedBook(usedBook.getBookId());
        List<UsedBook> otherUsedBookListFinal = new ArrayList<>();
        
        for (UsedBook u : otherUsedBookList) {
            if(usedBookId != u.getId()) {
                otherUsedBookListFinal.add(u);
            }
        }
        
        
        model.addAttribute("book", book);
        model.addAttribute("user", user); // userName만 보낼 수 있게 수정(?)
        model.addAttribute("content", content);
        model.addAttribute("usedBookPost", usedBookPost);
        model.addAttribute("usedBook", usedBook);
        model.addAttribute("otherUsedBookListFinal", otherUsedBookListFinal);
            
    }
    
    /**
     * 
     * @param id 마이페이지에 표시될 user 
     * @param model
     */
    @GetMapping("/mypage") // /market/mypage 판매글작성자&마이페이지 이동
    public void mypage(Integer id, Model model) {
        User user = userRepository.findById(id).get();
        
        model.addAttribute("user", user);
    }
    

}
