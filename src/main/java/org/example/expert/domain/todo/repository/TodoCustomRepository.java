package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoCustomRepository {

    Todo findByIdWithUser(Long id);

    Page<TodoSearchResponse> findByTitleAndManagerAndRange(TodoSearchRequest dto, Pageable pageable);
}
