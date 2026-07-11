package com.example.repository;

import com.example.entity.StudentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends CrudRepository<StudentEntity, Integer> {
    Optional<List<StudentEntity>> getAllByVisibleIsTrue(Boolean visible);

    @Query("FROM StudentEntity WHERE id =:id AND visible IS TRUE ")
    Optional<StudentEntity> getById(Integer id);
}
