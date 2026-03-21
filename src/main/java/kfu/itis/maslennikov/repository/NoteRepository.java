package kfu.itis.maslennikov.repository;

import kfu.itis.maslennikov.model.Note;
import kfu.itis.maslennikov.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByAuthor(User author);

    List<Note> findByIsPublicTrue();

    @Query("""
            select n from Note n
            where lower(n.title) like lower(concat('%', :query, '%'))
               or lower(n.content) like lower(concat('%', :query, '%'))
            order by n.createdAt desc
            """)
    List<Note> search(@Param("query") String query);
}
