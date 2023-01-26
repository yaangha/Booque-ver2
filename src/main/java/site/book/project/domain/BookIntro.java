package site.book.project.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Entity(name = "BOOKSINTRO")
@SequenceGenerator(name = "BOOKSINTRO_SEQ_GEN", sequenceName = "BOOKSINTRO_SEQ", initialValue = 1, allocationSize = 1)
public class BookIntro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOOKSINTRO_SEQ_GEN")
    private Integer id; // PK

    @Column(nullable = false)
    private long isbn;
    
    @Column(nullable = false)
    private String bookIntro;
    
    @Column
    private String bookIntroImage;
    

    
    
}
