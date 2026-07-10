package com.example.repository;

import com.example.entity.BookEntity;
import org.springframework.data.repository.CrudRepository;

public interface StudentBookRepository extends CrudRepository<BookEntity, Integer> {
}
