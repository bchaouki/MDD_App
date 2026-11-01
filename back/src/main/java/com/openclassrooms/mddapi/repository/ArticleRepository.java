package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.Article;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /** Articles des thèmes suivis par l'utilisateur ; auteur et thème chargés dans la même requête (pas de N+1). */
    @Query("""
            select a from Article a
            join fetch a.author
            join fetch a.topic t
            where t.id in (select s.id from User u join u.topics s where u.id = :userId)
            """)
    List<Article> findFeed(@Param("userId") Long userId, Sort sort);

    @Query("select a from Article a join fetch a.author join fetch a.topic where a.id = :id")
    Optional<Article> findDetailedById(@Param("id") Long id);
}
