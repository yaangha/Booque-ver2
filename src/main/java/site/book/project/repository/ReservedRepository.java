package site.book.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.book.project.domain.Reserved;

public interface ReservedRepository extends JpaRepository<Reserved, Integer> {

    // (지혜) usedBookId로 예약 정보 찾기
    Reserved findByUsedBookId(Integer usedBookId);
    
}

