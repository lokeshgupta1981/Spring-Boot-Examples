package com.howto.howto;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout {

    private final PersonRepository repo;
    final Grid<Person> grid;

    private Button newBtn = new Button("New");
    private Button deleteBtn = new Button("Delete");
    private Button saveBtn = new Button("Save");
    private HorizontalLayout btnLayout = new HorizontalLayout();
    private HorizontalLayout fieldsLayout = new HorizontalLayout();

    private TextField name = new TextField("First Name");
    private TextField lastName = new TextField("Last Name");
    private IntegerField idField = new IntegerField("ID");

    private String MAX_WIDTH = "400px";
    private String BUTTON_WIDTH = "123px";

    public MainView(PersonRepository repo) {
        this.repo = repo;
        this.grid = new Grid<>(Person.class, false);
        grid.addColumn(Person::getId).setHeader("ID").setSortable(true).setWidth("20px");
        grid.addColumn(Person::getFirstName).setHeader("First name").setSortable(true);
        grid.addColumn(Person::getLastName).setHeader("Last name").setSortable(true);
        grid.setMaxWidth(MAX_WIDTH);

        updateTableList();

        deleteBtn.setEnabled(false);

        grid.addSelectionListener(selected -> {
            if (selected.getAllSelectedItems().isEmpty()) {
                deleteBtn.setEnabled(false);
                clearFields();
            } else {
                deleteBtn.setEnabled(true);
                Person selectedCustomer = selected.getFirstSelectedItem().get();
                name.setValue(selectedCustomer.getFirstName());
                lastName.setValue(selectedCustomer.getLastName());
                idField.setValue(selectedCustomer.getId());
            }
        });

        newBtn.setWidth(BUTTON_WIDTH);
        deleteBtn.setWidth(BUTTON_WIDTH);
        saveBtn.setWidth(BUTTON_WIDTH);

        btnLayout.add(newBtn, deleteBtn, saveBtn);
        btnLayout.setMaxWidth(MAX_WIDTH);
        fieldsLayout.add(name, lastName);

        add(btnLayout);
        add(fieldsLayout);
        add(grid);
        updateTableList();
        addButtonsActionListeners();

    }

    private void addButtonsActionListeners() {
        newBtn.addClickListener(click -> {
            clearFields();
            grid.select(null);
        });
        deleteBtn.addClickListener(click -> {
            repo.delete(grid.getSelectedItems().stream().toList().get(0));
            updateTableList();
            clearFields();
        });
        saveBtn.addClickListener(click -> {
            Person customer = new Person();
            customer.setFirstName(name.getValue());
            customer.setLastName(lastName.getValue());
            customer.setId(idField.getValue());
            repo.save(customer);
            clearFields();
            updateTableList();
        });
    }

    private void updateTableList() {
        grid.setItems(repo.findAll());
    }

    private void clearFields() {
        name.clear();
        lastName.clear();
        idField.clear();
    }
}
