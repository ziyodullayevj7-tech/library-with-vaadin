package com.example.view;

import com.example.dto.book.BookDto;
import com.example.dto.book.BookUpdateDto;
import com.example.service.BookService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Optional;


@Route("/book")
@PageTitle("Book CRUD")
public class BookView extends VerticalLayout {
    private final BookService bookService;

    private Grid<BookDto> grid = new Grid<>(BookDto.class, false);


    public BookView(BookService bookService) {
        this.bookService = bookService;
        add(new H1("Book List"));
        addForm();
        getByIdForm();
        addGrid();
    }

    public void addForm() {
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

    public void getByIdForm() {
        Button getBtn = new Button("Get Book");
        getBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getBtn.addClickListener(listener -> {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Get any book with id");

            TextField idField = new TextField("Id");
            confirmDialog.add(idField);
            confirmDialog.addConfirmListener(event -> {
                Optional<BookDto> optional = bookService.getById(Integer.parseInt(idField.getValue()));
                if (optional.isEmpty()) {
                    Notification.show("Book Not Found");
                } else {
                    BookDto dto = optional.get();
                    Notification.show(String.format(
                            "Id: %d, Title: %s, Author: %s, Year: %s",
                            dto.getId(), dto.getTitle(), dto.getAuthor(), dto.getPublishedYear()
                    ));
                }
            });
            confirmDialog.open();
        });
        add(getBtn);
    }

    public void addGrid() {
        grid.setItems(bookService.getAll());

        grid.addColumn(BookDto::getTitle).setHeader("Title");
        grid.addColumn(BookDto::getAuthor).setHeader("Author");
        grid.addColumn(BookDto::getPublishedYear).setHeader("Published Year");
        grid.addComponentColumn(s -> {

            // UPDATE
            Button updateBtn = new Button("Edit Book");
            updateBtn.addThemeVariants(ButtonVariant.LUMO_WARNING);
            updateBtn.addClickListener(listener -> {
                ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.setHeader("Write Fields Here");

                TextField editTitle = new TextField("Title");
                TextField editAuthor = new TextField("Author");
                TextField editPublishedYear = new TextField("Published Year");

                editAuthor.setValue(s.getAuthor());
                editPublishedYear.setValue(s.getPublishedYear());
                editTitle.setValue(s.getTitle());
                confirmDialog.add(editTitle, editAuthor, editPublishedYear);
                confirmDialog.addConfirmListener(e -> {
                    Boolean update = bookService.update(new BookUpdateDto(s.getId(), editTitle.getValue(), editAuthor.getValue(), editPublishedYear.getValue()));
                    if (!update) {
                        Notification.show("Update Failed").setPosition(Notification.Position.TOP_CENTER);
                    } else {
                        grid.setItems(bookService.getAll());
                        Notification.show("Book Edited Successfully").setPosition(Notification.Position.TOP_CENTER);
                    }
                });
                confirmDialog.open();
            });

            // DELETE
            Button deleteBtn = new Button("Delete Book");
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(listener -> {
                ConfirmDialog confirmDialog = new ConfirmDialog();
                confirmDialog.setHeader("Delete Book");
                confirmDialog.setText("Are you sure you want to delete this book?");

                confirmDialog.setCancelable(true);
                confirmDialog.setCancelText("Cancel");

                confirmDialog.addConfirmListener(e -> {
                    Boolean delete = bookService.delete(s.getId());
                    if (!delete) {
                        Notification.show("Delete Failed").setPosition(Notification.Position.TOP_CENTER);
                    } else {
                        grid.setItems(bookService.getAll());
                        Notification.show("Book Deleted Successfully").setPosition(Notification.Position.TOP_CENTER);
                    }
                });
                confirmDialog.open();
            });
            HorizontalLayout btnLayout = new HorizontalLayout(updateBtn, deleteBtn);

            return btnLayout;
        }).setHeader("#");
        add(grid);
    }
}
