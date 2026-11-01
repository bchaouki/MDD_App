package com.openclassrooms.mddapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable=false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name="created_at", nullable=false, updatable=false)
    private LocalDateTime createdAt;

    @ManyToOne (fetch = FetchType.LAZY,
            optional = false)
    @JoinColumn(name="article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch=FetchType.LAZY, optional = false)
    @JoinColumn(name="author_id", nullable = false)
    private User author;


}
