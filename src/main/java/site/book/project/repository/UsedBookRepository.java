package site.book.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import site.book.project.domain.UsedBook;

public interface UsedBookRepository extends JpaRepository<UsedBook, Integer> {

    // (하은) bookId로 동일한 다른 중고책 리스트 찾기
    List<UsedBook> findByBookId(Integer bookId);
    
    // (하은) 중고판매글 조회수 증감
    /*
    @Modifying // @Query로 작성된 변경, 삭제 쿼리 메서드 사용할 때 필용
    @Query("update UsedBook u set u.hits = u.hits + 1 where u.id = :id")
    int updateHits(Integer id); // id => UsedBook의 PK
    */
}
