
package site.book.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.book.project.domain.BookIntro;

public interface BookIntroRepository extends JpaRepository<BookIntro, Integer> {

    BookIntro findByIsbn(Long isbn);
}