package site.book.project.service;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.UsedBook;
import site.book.project.domain.UsedBookPost;
import site.book.project.domain.UsedBookWish;
import site.book.project.domain.User;
import site.book.project.dto.MarketCreateDto;
import site.book.project.repository.UsedBookImageRepository;
import site.book.project.repository.UsedBookPostRepository;
import site.book.project.repository.UsedBookRepository;
import site.book.project.repository.UsedBookWishRepository;
import site.book.project.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsedBookService {
	// 부끄마켓 글 하나는 세개의 테이블로 되어 있음.
	// service를 하나로 뭉쳐서 만들거임
	
	private final UsedBookRepository usedBookRepository;
	private final UsedBookPostRepository postRepository;
	private final UsedBookImageRepository imgRepository;
	private final UsedBookWishRepository usedBookWishRepository;
	
	/**(은정)
	 * 책 검색 후 바로 UsedBook테이블에 저장하여 UsedBookPost와 UsedBookImage에 연결할 수 있는
	 * FK를 리턴해줌. 잘하면 임시저장할 수 있지 않을까?
	 * @param bookId 판매할 책의 PK 
	 * @param userId 판매하는 사용자의 PK
	 * @return 생성된 UsedBook의 PK
	 */
	public Integer create(Integer bookId, Integer userId) {
		
		UsedBook usedBook = usedBookRepository.save(UsedBook.builder().userId(userId).bookId(bookId).build());
		
		// (하은) UsedBookPost에도 같이 저장 - 임시저장용
		postRepository.save(UsedBookPost.builder().usedBookId(usedBook.getId()).build());
		
		return usedBook.getId();
	}
	
	/**(은정)
	 * UsedBook는 Transactional를 통한 update
	 * UsedBookContent는 객체 생성 후 save
	 * @param usedBookId UsedBook의 PK
	 * @param dto market/create에서 받은 데이터(사진 DB제외)
	 */
	@Transactional
	public void create(Integer usedBookId, MarketCreateDto dto ) {
		
		// UsedBook 디비에 저장
		UsedBook entity = usedBookRepository.findById(usedBookId).get();
		entity.update(dto);
		
		// UsedBookPost에 저장
		UsedBookPost content = postRepository.findByUsedBookId(usedBookId);
		
		if(content != null) {
		    content.update(dto.getContents(), dto.getStorage());
		}else {
		    postRepository.save(UsedBookPost.builder().usedBookId(usedBookId).content(dto.getContents()).storage(dto.getStorage()).build());
		    
		}
		
		
	}

	// (하은) 코드 수정 필요!! 지금 실패함!! 찜하기 누르면 usedBookWish DB에 create
	public Integer addUsedBookWish(Integer usedBookId, Integer userId) {
	// 사용자 정보가 있으면 값을 바꾸고, 없으면 만들기 
	    UsedBookWish wish = usedBookWishRepository.findByUserIdAndUsedBookId(userId, usedBookId);
	    Integer result = 0;
	    
	    if(wish == null) {
	        usedBookWishRepository.save(UsedBookWish.builder().usedBookId(usedBookId).userId(userId).build());
	        result = 1;
	    } else {
            usedBookWishRepository.delete(wish);
            result = 0;
        }
	    
	    // 0은 삭제 1은 존재
	    return result;
	    
	}
	
	@Transactional
	public Integer addWishCount(Integer usedBookId) {
	    
	    UsedBook usedBook = usedBookRepository.findById(usedBookId).get();
	    Integer a = usedBook.getWishCount() + 1;
	    usedBook = usedBook.updateWishCount(a);
	    usedBookRepository.save(usedBook);
	    return a;
	}
	
	@Transactional
	public Integer minusWishCount(Integer usedBookId) {
	    UsedBook usedBook = usedBookRepository.findById(usedBookId).get();
	    
	    Integer a = usedBook.getWishCount() - 1;
	    
	    usedBook = usedBook.updateWishCount(a);
	    usedBookRepository.save(usedBook);
	    
	    return a;
	}
	
	

	// (하은) bookId가 동일한 다른 중고책 리스트 만들기
    public List<UsedBook> readOtherUsedBook(Integer bookId) {
        log.info("하은 중고책의 책 정보를 가진 아이디는? = {}", bookId);
        
        List<UsedBook> otherUsedBookList = usedBookRepository.findByBookId(bookId);
        
        return otherUsedBookList;
    }

    // (하은) 중고판매글 조회수
    /**
     * 
     * @param id 조회수를 증가시킬 id(=> UsedBook의 PK)
     * @param request
     * @param response
     * @return
     */
    public void updateHits(Integer usedBookId, HttpServletRequest request, HttpServletResponse response) {
        UsedBook usedBook = usedBookRepository.findById(usedBookId).get();
        //log.info("조회수 확인할 usedBookId = {}", usedBookId);
        
        Cookie oldCookie = null;
        Cookie[] cookies = request.getCookies(); // 현재 쿠키들 배열
                
        if (cookies != null) { // 이미 쿠키들이 있을 때 usedBookView 쿠키가 존재유무 체크
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("usedBookView")) {
                    oldCookie = cookie;
                }
            }
        }
        
        if (oldCookie != null) {
            if (!oldCookie.getValue().contains("[" + usedBookId.toString() + "]")) { // 해당 쿠키가 없을시
                usedBook = usedBook.updateHits();                
                usedBookRepository.save(usedBook);
                
                oldCookie.setValue(oldCookie.getValue() + "[" + usedBookId + "]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 30);
                response.addCookie(oldCookie);
            }
        } else {
            usedBook = usedBook.updateHits(); // usedBookView가 없으면 조회수 1 증가             
            usedBookRepository.save(usedBook);
            Cookie newCookie = new Cookie("usedBookView", "[" + usedBookId + "]"); // 쿠키 생성
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 30);
            response.addCookie(newCookie);
        }
        
    }
    
    
}
