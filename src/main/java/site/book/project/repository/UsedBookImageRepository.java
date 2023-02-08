package site.book.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import site.book.project.domain.UsedBookImage;

public interface UsedBookImageRepository extends JpaRepository<UsedBookImage, Integer> {

    List<UsedBookImage> findByUsedBookId(Integer usedBookId);

}
