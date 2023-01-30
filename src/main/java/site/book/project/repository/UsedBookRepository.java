package site.book.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import site.book.project.domain.UsedBook;

public interface UsedBookRepository extends JpaRepository<UsedBook, Integer> {

    // (하은) bookId로 동일한 다른 중고책 리스트 찾기
    List<UsedBook> findByBookId(Integer bookId);
    
}
