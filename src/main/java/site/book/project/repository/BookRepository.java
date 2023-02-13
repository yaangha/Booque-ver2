package site.book.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import site.book.project.domain.Book;
import site.book.project.domain.UsedBook;

public interface BookRepository extends JpaRepository<Book, Integer> {

    // (하은) 작가의 다른 책 정보 찾기
    List<Book> findAllByAuthor(String author);


    List<Book> findByOrderByBookIdDesc();

    // (홍찬) 메인홈에서 필요한 전체 별점 리스트
    List<Book> findTop4ByOrderByBookScoreDesc();
    
    // (홍찬) 메인홈에서 필요한 전체 리뷰 많은 순 리스트
    List<Book> findTop4ByOrderByPostCountDesc();


    // (예진) 부끄장터 제목에 검색 키워드 포함된 책 리스트중 4개만
    List<Book> findTop4ByBookNameIgnoreCaseContaining(String Keyword);



}
