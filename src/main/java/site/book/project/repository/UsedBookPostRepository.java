package site.book.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import site.book.project.domain.UsedBookPost;

public interface UsedBookPostRepository extends JpaRepository<UsedBookPost, Integer>{

    UsedBookPost findByUsedBookId(Integer usedBookId);
    
}
