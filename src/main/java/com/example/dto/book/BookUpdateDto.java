package com.example.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookUpdateDto {
    private Integer id;
    private String newTitle;
    private String newAuthor;
    private String newPublishedYear;
}
