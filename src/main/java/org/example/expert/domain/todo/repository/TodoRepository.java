package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoCustomRepository {

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE t.modifiedAt BETWEEN :startDate AND :endDate ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE t.weather LIKE :weather ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByWeatherWithUser(@Param("weather") String weather, Pageable pageable);

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE t.weather LIKE :weather AND t.modifiedAt BETWEEN :startDate AND :endDate ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByWeatherAndDateRange(@Param("weather") String weather, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
}
