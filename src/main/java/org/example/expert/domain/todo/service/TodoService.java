package org.example.expert.domain.todo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.config.CustomUserDetails;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    @Transactional
    public TodoSaveResponse saveTodo(CustomUserDetails userDetails, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(userDetails);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
            todoSaveRequest.getTitle(),
            todoSaveRequest.getContents(),
            weather,
            user
        );

        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
            savedTodo.getId(),
            savedTodo.getTitle(),
            savedTodo.getContents(),
            weather,
            new UserResponse(user.getId(), user.getNickname(), user.getEmail())
        );
    }

    @Transactional(readOnly = true)
    public Page<TodoResponse> getTodos(Pageable pageable, String weather, String startDate,
        String endDate) {

        Page<Todo> todos;
        if (endDate.isBlank()) {
            endDate = LocalDate.now().toString();
        }

        todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);

        if (weather.isBlank() && !startDate.isBlank()) {
            todos = todoRepository.findAllByDateRange(toLocalDateTime(startDate), toLocalDateTime(endDate), pageable);
        }

        if (!weather.isBlank() && startDate.isBlank()) {
            todos = todoRepository.findAllByWeatherWithUser(weather, pageable);
        }

        if (!weather.isBlank() && !startDate.isBlank()) {
            todos = todoRepository.findAllByWeatherAndDateRange(weather, toLocalDateTime(startDate), toLocalDateTime(endDate), pageable);
        }

        return todos.map(todo -> new TodoResponse(
            todo.getId(),
            todo.getTitle(),
            todo.getContents(),
            todo.getWeather(),
            new UserResponse(todo.getUser().getId(), todo.getUser().getNickname(),
                todo.getUser().getEmail()),
            todo.getCreatedAt(),
            todo.getModifiedAt()
        ));
    }

    @Transactional(readOnly = true)
    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId);

        User user = todo.getUser();

        return new TodoResponse(
            todo.getId(),
            todo.getTitle(),
            todo.getContents(),
            todo.getWeather(),
            new UserResponse(user.getId(), user.getNickname(), user.getEmail()),
            todo.getCreatedAt(),
            todo.getModifiedAt()
        );
    }

    private static LocalDateTime toLocalDateTime(String time){
        return LocalDateTime.parse(time + "T23:59:59");
    }
}
