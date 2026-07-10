package com.example.service;

import com.example.dto.book.BookDto;
import com.example.entity.BookEntity;
import com.example.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public void createBook(BookDto dto) {
        BookEntity entity = new BookEntity();
        entity.setTitle(dto.getTitle());
        entity.setAuthor(dto.getAuthor());
        entity.setPublishYear(dto.getPublishedYear());
        bookRepository.save(entity);
        System.out.println("Book created");
    }

    public List<BookDto> getAll() {
        List<BookDto> response = new ArrayList<>();
        Optional<List<BookEntity>> optional = bookRepository.getAllByVisibleIsTrue(Boolean.TRUE);
        if (optional.isEmpty()){
            System.out.println("No books found");
        }

        List<BookEntity> entities = optional.get();
        entities.forEach(entity -> {
            response.add(new BookDto(entity.getId(), entity.getTitle(), entity.getAuthor(), entity.getPublishYear()));
        });
        return response;
    }
}
