package com.example.view;

import com.example.dto.book.BookDto;
import com.example.service.BookService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;



@Route("/book")
@PageTitle("Book CRUD")
public class BookView extends VerticalLayout {
    private final BookService bookService;

    private Grid<BookDto> grid = new Grid<>(BookDto.class, false);


    public BookView(BookService bookService) {
        this.bookService = bookService;
        add(new H1("Book List"));
        addForm();
        addGrid();
    }
    public void addForm(){
        TextField titleField = new TextField("Title");
        TextField authorField = new TextField("Author");
        TextField publishedYearField = new TextField("Published Year");

        Button addBtn = new Button("Add Book");
        addBtn.addClickListener(listener -> {
            String title = titleField.getValue();
            String author = authorField.getValue();
            String publishedYear = publishedYearField.getValue();

            bookService.createBook(new BookDto(title, author, publishedYear));

            grid.setItems(bookService.getAll());

            Notification.show("New Book Created").setPosition(Notification.Position.TOP_CENTER);

            titleField.clear();
            authorField.clear();
            publishedYearField.clear();
        });

        add(titleField);
        add(authorField);
        add(publishedYearField);
        add(addBtn);
    }

    public void addGrid(){
        grid.setItems(bookService.getAll());

        grid.addColumn(BookDto::getTitle).setHeader("Title");
        grid.addColumn(BookDto::getAuthor).setHeader("Author");
        grid.addColumn(BookDto::getPublishedYear).setHeader("Published Year");
        add(grid);
    }
}
