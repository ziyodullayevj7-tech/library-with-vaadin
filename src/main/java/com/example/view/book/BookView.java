package com.example.view.book;

import com.example.dto.book.BookDto;
import com.example.service.BookService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Route("/book")
@PageTitle("Book CRUD")
public class BookView extends VerticalLayout {
    private final BookService bookService;

    private Grid<BookDto> grid = new Grid<>(BookDto.class, false);

    public BookView(BookService bookService) {
        this.bookService = bookService;
        add(new H1("Book List"));

        Button goToBookFormPageBtn = new Button("");
        goToBookFormPageBtn.setIcon(VaadinIcon.PLUS.create());
        goToBookFormPageBtn.setThemeVariants(ButtonVariant.LUMO_LARGE,  ButtonVariant.LUMO_PRIMARY,   ButtonVariant.LUMO_SUCCESS);
        goToBookFormPageBtn.addClickListener(e -> UI.getCurrent().navigate(BookFormView.class));
        HorizontalLayout buttonLayout = new HorizontalLayout(goToBookFormPageBtn);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        add(buttonLayout);
        addGrid();
    }

    public void refreshGrid() {
        grid.setItems(bookService.getAll());
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
        refreshGrid();

        grid.addColumn(BookDto::getTitle).setHeader("Title");
        grid.addColumn(BookDto::getAuthor).setHeader("Author");
        grid.addColumn(BookDto::getPublishedYear).setHeader("Published Year");
        grid.addComponentColumn(s -> {

            // UPDATE
            Button updateBtn = new Button("Edit Book");
            updateBtn.addThemeVariants(ButtonVariant.LUMO_WARNING);
            updateBtn.setIcon(VaadinIcon.PENCIL.create());
            updateBtn.addClickListener(listener -> UI.getCurrent().navigate(BookFormView.class, new QueryParameters(Map.of("id", List.of(String.valueOf(s.getId()))))));

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
                        refreshGrid();
                        Notification.show("Book Deleted Successfully").setPosition(Notification.Position.TOP_CENTER);
                    }
                });
                confirmDialog.open();
            });
            return new HorizontalLayout(updateBtn, deleteBtn);
        }).setHeader("#");
        add(grid);
    }
}
