package com.example.todoapp.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface TodoJpaRepository extends JpaRepository<TodoJpaEntity, UUID> {
    List<TodoJpaEntity> findAllByOrderByCreatedAtAsc();
}
