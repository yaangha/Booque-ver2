package site.book.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.book.project.domain.UsedBook;

public interface UsedBookRepository extends JpaRepository<UsedBook, Integer> {

    
    
}
