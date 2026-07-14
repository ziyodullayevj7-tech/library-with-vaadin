package com.example.view.book;

import com.example.dto.book.BookDto;
import com.example.dto.book.BookUpdateDto;
import com.example.service.BookService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;

import java.util.List;
import java.util.Optional;

@Route("/book/create-update")
@PageTitle("Book Create Update")
public class BookFormView extends VerticalLayout implements BeforeEnterObserver {
    private final BookService bookService;

    private TextField titleText = new TextField("Title");
    private TextField authorText = new TextField("Author");
    private TextField publishedYearText = new TextField("Published Year");

    private Button addBtn = new Button("Add Book");
    private Button cancelBtn = new Button("Cancel");

    private BookDto selectedBook = null;

    public BookFormView(BookService bookService) {
        this.bookService = bookService;
        addForm();
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        QueryParameters queryParameters =
                event.getLocation().getQueryParameters();

        String idParam = queryParameters
                .getParameters()
                .getOrDefault("id", List.of(""))
                .getFirst();

        if (idParam.isBlank()) {
            return;
        }

        Integer id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            Notification.show("Invalid book id in URL");
            return;
        }
        Optional<BookDto> optional = bookService.getById(id);
        optional.ifPresent(dto -> {
            selectedBook = dto;

            titleText.setValue(dto.getTitle());
            authorText.setValue(dto.getAuthor());
            publishedYearText.setValue(dto.getPublishedYear());
            addBtn.setText("Update Book");
            addBtn.setThemeVariants(ButtonVariant.LUMO_WARNING);
            cancelBtn.setVisible(true);
        });
    }

    public void addForm() {
        FormLayout form = new FormLayout();
        addBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        addBtn.addClickListener(event -> {
            String title = titleText.getValue();
            String author = authorText.getValue();
            String publishedYear = publishedYearText.getValue();
            if (selectedBook == null) { // CREATE
                bookService.createBook(new BookDto(title, author, publishedYear));
                Notification.show("Book Created").setPosition(Notification.Position.TOP_CENTER);
            } else { // UPDATE
                selectedBook.setTitle(title);
                selectedBook.setAuthor(author);
                selectedBook.setPublishedYear(publishedYear);
                bookService.update(new BookUpdateDto(selectedBook.getId(), selectedBook.getTitle(), selectedBook.getAuthor(), selectedBook.getPublishedYear()));
                Notification.show("Book Updated").setPosition(Notification.Position.TOP_CENTER);
            }
            clearForm();
            UI.getCurrent().navigate(BookView.class);
        });

        cancelBtn.setVisible(false);
        cancelBtn.addClickListener(event -> clearForm());

        form.setAutoResponsive(true);
        form.addFormRow(titleText, authorText, publishedYearText);
        form.add(addBtn, cancelBtn);
        add(form);
    }

    public void clearForm() {
        addBtn.setText("Add Book");
        addBtn.removeThemeVariants(ButtonVariant.LUMO_WARNING);
        addBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        titleText.clear();
        authorText.clear();
        publishedYearText.clear();
        cancelBtn.setVisible(false);
        selectedBook = null;
    }
}
