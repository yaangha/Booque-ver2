package site.book.project.service;

import java.util.List;

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
		postRepository.save(UsedBookPost.builder().usedBookId(usedBookId).content(dto.getContents()).build());
		
		log.info("컨텐트를 찾아서~~~~!{},, {}", dto, dto.getContents());
		
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


	
    
    
}
