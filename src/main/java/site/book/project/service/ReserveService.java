package site.book.project.service;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.book.project.domain.Reserved;
import site.book.project.repository.ReservedRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ReserveService {
    
    private final ReservedRepository reservedRepository;
    
    // (지혜) 중고책 예약 정보 생성
    public void newReservation(Integer usedBookId, Integer userId) {
        
        Reserved reserved = Reserved.builder().usedBookId(usedBookId).userId(userId).build();
        reservedRepository.save(reserved);
    }
    
    // (지혜) 예약 정보 삭제
    public void deleteReservation(Integer usedBookId) {
        Reserved reserved = reservedRepository.findByUsedBookId(usedBookId);
        reservedRepository.delete(reserved);
    }
    
    
}
