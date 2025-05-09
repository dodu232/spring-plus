package org.example.expert.domain.todo.repository;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TodoCustomRepositoryImpl implements TodoCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Todo findByIdWithUser(Long id) {
        Todo result = jpaQueryFactory.selectFrom(todo)
            .where(todo.id.eq(id))
            .leftJoin(todo.user)
            .fetchJoin()
            .fetchOne();

        if (result == null) {
            throw new EntityNotFoundException("Todo not found");
        }

        return result;
    }

    @Override
    public Page<TodoSearchResponse> findByTitleAndManagerAndRange(TodoSearchRequest dto,
        Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            builder.and(todo.title.contains(dto.getTitle()));
        }

        if (dto.getNickname() != null && !dto.getNickname().isBlank()) {
            builder.and(todo.managers.any().user.nickname.contains(dto.getNickname()));
        }

        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            builder.and(todo.createdAt.between(dto.getStartDate().atStartOfDay(),
                dto.getEndDate().atTime(23, 59, 59)));
        }

        List<Todo> todos = jpaQueryFactory
            .selectFrom(todo)
            .leftJoin(todo.managers, manager).fetchJoin()
            .leftJoin(manager.user, user).fetchJoin()
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(todo.createdAt.desc())
            .fetch();

        Long total = jpaQueryFactory
            .select(todo.count())
            .from(todo)
            .where(builder)
            .fetchOne();

        List<TodoSearchResponse> responses = todos.stream()
            .map(t -> new TodoSearchResponse(
                t.getTitle(),
                t.getManagers().size(),
                t.getComments().size()
            ))
            .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, total != null ? total : 0L);
    }
}
