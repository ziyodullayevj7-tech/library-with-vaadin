package com.example.view.student;

import com.example.dto.student.StudentDto;
import com.example.service.StudentService;
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


@Route("/student")
@PageTitle("Student CRUD")
public class StudentView extends VerticalLayout {
    private final StudentService studentService;

    private Grid<StudentDto> grid = new Grid<>(StudentDto.class, false);

    public StudentView(StudentService studentService) {
        this.studentService = studentService;
        add(new H1("Student List"));

        Button goToStudentFormPageBtn = new Button("");
        goToStudentFormPageBtn.setIcon(VaadinIcon.PLUS.create());
        goToStudentFormPageBtn.setThemeVariants(ButtonVariant.LUMO_LARGE,  ButtonVariant.LUMO_PRIMARY,   ButtonVariant.LUMO_SUCCESS);
        goToStudentFormPageBtn.addClickListener(e -> UI.getCurrent().navigate(StudentFormView.class));

        HorizontalLayout buttonLayout = new HorizontalLayout(goToStudentFormPageBtn);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        add(buttonLayout);
        addGrid();
    }

    public void addGrid() {
        refreshGrid();

        if (studentService.getAll().isPresent()) {
            grid.setItems(studentService.getAll().get());
            grid.addColumn(StudentDto::getName).setHeader("Name");
            grid.addColumn(StudentDto::getSurname).setHeader("Surname");
            grid.addColumn(StudentDto::getPhoneNumber).setHeader("Phone");
            grid.addComponentColumn(s -> {
                //UPDATE
                Button updateBtn = new Button("Edit");
                updateBtn.addThemeVariants(ButtonVariant.LUMO_WARNING);
                updateBtn.setIcon(VaadinIcon.PLUS.create());
                updateBtn.addClickListener(event -> UI.getCurrent().navigate(StudentFormView.class, new QueryParameters(Map.of("id", List.of(String.valueOf(s.getId()))))));

                //DELETE
                Button deleteBtn = new Button("Delete");
                deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
                deleteBtn.addClickListener(event -> {
                    ConfirmDialog confirmDialog = new ConfirmDialog();
                    confirmDialog.setHeader("Delete Student");
                    confirmDialog.setText("Are you sure you want to delete this?");

                    confirmDialog.setCancelable(true);
                    confirmDialog.setCancelText("Cancel");

                    confirmDialog.addConfirmListener(e -> {
                        Boolean delete = studentService.delete(s.getId());
                        if (!delete) {
                            Notification.show("Delete Failed").setPosition(Notification.Position.TOP_CENTER);
                        } else {
                            refreshGrid();
                            Notification.show("Student Deleted Successfully").setPosition(Notification.Position.TOP_CENTER);
                        }
                    });
                    confirmDialog.open();
                });
                return new HorizontalLayout(updateBtn, deleteBtn);
            }).setHeader("#");
        }
        add(grid);
    }

    public void getByIdForm() {
        Button getBtn = new Button("Get Student");
        getBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getBtn.addClickListener(event -> {
            ConfirmDialog confirmDialog = new ConfirmDialog();
            confirmDialog.setHeader("Get any student with id");

            TextField idField = new TextField("Id");
            confirmDialog.add(idField);
            confirmDialog.addConfirmListener(e -> {
                Optional<StudentDto> optional = studentService.getById(Integer.parseInt(idField.getValue()));
                if (optional.isEmpty()) {
                    Notification.show("Student Not Found").setPosition(Notification.Position.TOP_CENTER);
                } else {
                    StudentDto dto = optional.get();
                    Notification.show(String.format(
                            "Id: %d, Name: %s, Surname: %s, Phone: %s",
                            dto.getId(), dto.getName(), dto.getSurname(), dto.getPhoneNumber()
                    ));
                }
            });
            confirmDialog.open();
        });
        add(getBtn);
    }

    public void refreshGrid(){
        Optional<List<StudentDto>> optional = studentService.getAll();
        if (optional.isEmpty()){
            return;
        }
        grid.setItems(optional.get());
    }
}
