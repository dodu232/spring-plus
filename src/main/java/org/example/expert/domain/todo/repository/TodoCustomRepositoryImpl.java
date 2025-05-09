package org.example.expert.domain.todo.repository;

import static org.example.expert.domain.todo.entity.QTodo.todo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
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

        if (result == null){
            throw new EntityNotFoundException("Todo not found");
        }

        return result;
    }
}
