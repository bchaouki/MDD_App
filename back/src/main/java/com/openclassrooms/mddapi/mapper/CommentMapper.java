package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.request.CreateCommentRequest;
import com.openclassrooms.mddapi.dto.response.CommentDto;
import com.openclassrooms.mddapi.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target="authorUsername", source="author.username")
    CommentDto toDto(Comment comment) ;

    List<CommentDto> toDtos(List<Comment> comments);

    Comment toEntity(CreateCommentRequest request);
}
