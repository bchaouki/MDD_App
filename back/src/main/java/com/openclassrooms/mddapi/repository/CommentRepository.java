package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.dto.response.CommentDto;
import com.openclassrooms.mddapi.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

   @Query("Select c from Comment c join fetch c.author  join fetch c.article a  where c.article.id=:articleId ")
    List<Comment> findArticleId(Long articleId);
}
