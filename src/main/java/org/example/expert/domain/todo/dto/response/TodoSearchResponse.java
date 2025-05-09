package org.example.expert.domain.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TodoSearchResponse {
    private String title;
    private int totalManager;
    private int totalComment;
}
