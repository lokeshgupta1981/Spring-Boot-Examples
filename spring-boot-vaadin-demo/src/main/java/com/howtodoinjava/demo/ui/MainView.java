package com.howtodoinjava.demo.ui;

import com.howtodoinjava.demo.dao.PersonRepository;
import com.howtodoinjava.demo.model.Person;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
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

    deleteBtn.setEnabled(false);

    newBtn.setWidth(BUTTON_WIDTH);
    deleteBtn.setWidth(BUTTON_WIDTH);
    saveBtn.setWidth(BUTTON_WIDTH);

    btnLayout.add(newBtn, deleteBtn, saveBtn);
    btnLayout.setMaxWidth(MAX_WIDTH);
    fieldsLayout.add(name, lastName);

    add(btnLayout);
    add(fieldsLayout);
    add(grid);
    refreshTableData();
    addButtonsActionListeners();
  }

  private void addButtonsActionListeners() {

    grid.addSelectionListener(selected -> {
      if (selected.getAllSelectedItems().isEmpty()) {
        deleteBtn.setEnabled(false);
        clearInputFields();
      } else {
        deleteBtn.setEnabled(true);
        Person selectedCustomer = selected.getFirstSelectedItem().get();
        name.setValue(selectedCustomer.getFirstName());
        lastName.setValue(selectedCustomer.getLastName());
        idField.setValue(selectedCustomer.getId());
      }
    });

    newBtn.addClickListener(click -> {
      clearInputFields();
      grid.select(null);
    });

    deleteBtn.addClickListener(click -> {
      repo.delete(grid.getSelectedItems().stream().toList().get(0));
      refreshTableData();
      clearInputFields();
    });

    saveBtn.addClickListener(click -> {
      Person customer = new Person();
      customer.setFirstName(name.getValue());
      customer.setLastName(lastName.getValue());
      customer.setId(idField.getValue());
      repo.save(customer);
      clearInputFields();
      refreshTableData();
    });
  }

  private void refreshTableData() {
    grid.setItems(repo.findAll());
  }

  private void clearInputFields() {
    name.clear();
    lastName.clear();
    idField.clear();
  }
}
