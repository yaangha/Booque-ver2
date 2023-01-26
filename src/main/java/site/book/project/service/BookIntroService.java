package site.book.project.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.book.project.domain.BookIntro;
import site.book.project.repository.BookIntroRepository;

@RequiredArgsConstructor
@Service
public class BookIntroService {
	
	private final BookIntroRepository bookIntroRepository;
	
	public BookIntro introByIsbn(Long isbn) {
		
		return bookIntroRepository.findByIsbn(isbn);
	}

}