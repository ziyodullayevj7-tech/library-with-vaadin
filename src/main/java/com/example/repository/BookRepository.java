package com.example.repository;

import com.example.entity.BookEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends CrudRepository<BookEntity, Integer> {
    Optional<List<BookEntity>> getAllByVisibleIsTrue(Boolean visible);

    @Query("FROM BookEntity WHERE visible IS TRUE AND id =:id")
    Optional<BookEntity> getById(Integer id);
}
