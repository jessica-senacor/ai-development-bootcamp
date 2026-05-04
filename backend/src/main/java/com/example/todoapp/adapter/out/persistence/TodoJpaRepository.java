package com.example.todoapp.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface TodoJpaRepository extends JpaRepository<TodoJpaEntity, UUID> {}
