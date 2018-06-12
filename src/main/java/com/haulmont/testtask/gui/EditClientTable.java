package com.haulmont.testtask.gui;

import com.haulmont.testtask.dao.ClientDao;
import com.haulmont.testtask.dao.Database;

import com.haulmont.testtask.entity.Client;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;


import java.util.List;

public class EditClientTable {


    public Window addClient(Grid grid) {

        final Window window = new Window("Добавить нового клиента");
        window.setModal(true);
        window.center();
        window.setWidth("400");
        window.setHeight("300");
        window.setClosable(false);
        window.setResizable(false);

        final FormLayout addClient = new FormLayout();
        addClient.setMargin(true);
        addClient.setSizeFull();

        final TextField surname = new TextField("Фамилия", "");
        surname.setSizeFull();
        surname.setRequired(true);
        surname.setMaxLength(50);
        surname.setNullSettingAllowed(true);
        surname.addValidator(new RegexpValidator("[' 'а-яА-Яa-z-A-Z]{1,50}", true, "Данные введены некорректно"));

        final TextField firstName = new TextField("Имя", "");
        firstName.setSizeFull();
        firstName.setRequired(true);
        firstName.setMaxLength(50);
        firstName.addValidator(new RegexpValidator("[' 'а-яА-Яa-z-A-Z]{1,50}", true, "Данные введены некорректно"));

        final TextField patronymic = new TextField("Отчество", "");
        patronymic.setSizeFull();
        patronymic.setMaxLength(50);
        patronymic.addValidator(new RegexpValidator("[' 'а-яА-Яa-z-A-Z]{0,50}", true, "Данные введены некорректно"));

        final TextField number = new TextField("Номер телефона", "");
        number.setSizeFull();
        number.setRequired(true);
        number.setMaxLength(50);
        number.addValidator(new RegexpValidator("[' '\\-()0-9]{1,50}", true, "Данные введены некорректно"));

        final Button saveClient = new Button("Добавить");
        saveClient.setStyleName(ValoTheme.BUTTON_SMALL);

        saveClient.addClickListener(clickEvent -> {

            try {
                surname.validate();
                firstName.validate();
                number.validate();

                Database.startDatabase();
                Client client = new Client(surname.getValue().trim(), firstName.getValue().trim(), patronymic.getValue().trim(), number.getValue().trim() );
                ClientDao.addClient(client);
                List<Client> clients = ClientDao.getAllClient();

                int index = clients.size();
                grid.addRow(clients.get(index-1).getId(), clients.get(index-1).getSurname(), clients.get(index-1).getFirstName(),
                            clients.get(index-1).getPatronymic(), clients.get(index-1).getNumber());

                Database.closeDatabase();
                window.close();
            } catch (Validator.InvalidValueException e) {

                surname.setRequiredError("Введите фамилию");
                firstName.setRequiredError("Введите имя");
                number.setRequiredError("Укажите телефон");
                Notification.show("Заполните данные", Notification.TYPE_WARNING_MESSAGE);
            }
        });


        final Button cancel = new Button("Отмена");
        cancel.setStyleName(ValoTheme.BUTTON_SMALL);
        cancel.addClickListener(clickEvent -> window.close());

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.addComponent(saveClient);
        buttons.addComponent(cancel);

        addClient.addComponent(surname);
        addClient.addComponent(firstName);
        addClient.addComponent(patronymic);
        addClient.addComponent(number);
        addClient.addComponent(buttons);

        window.setContent(addClient);
        return window;
    }

    public Window updateClient(Grid grid) {

        final Window window = new Window("Изменить данные клиента");
        window.setModal(true);
        window.center();
        window.setWidth("400");
        window.setHeight("300");
        window.setClosable(false);
        window.setResizable(false);

        final FormLayout addClient = new FormLayout();
        addClient.setMargin(true);
        addClient.setSizeFull();

        final TextField surname = new TextField("Фамилия", grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Фамилия").getValue().toString());
        surname.setSizeFull();
        surname.setMaxLength(50);
        surname.setRequired(true);
        surname.setRequiredError("Введите фамилию");
        surname.setNullSettingAllowed(true);
        surname.addValidator(new RegexpValidator("[' 'а-яА-Яa-z-A-Z]{1,50}", true, "Данные введены некорректно"));

        final TextField firstName = new TextField("Имя", grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Имя").getValue().toString());
        firstName.setSizeFull();
        firstName.setRequired(true);
        firstName.setRequiredError("Введите имя");
        firstName.setMaxLength(50);
        firstName.addValidator(new RegexpValidator("[' 'а-яА-Яa-z-A-Z]{1,50}", true, "Данные введены некорректно"));

        final TextField patronymic = new TextField("Отчество", grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Отчество").getValue().toString());
        patronymic.setSizeFull();
        patronymic.setMaxLength(50);
        patronymic.addValidator(new RegexpValidator("[' 'а-яА-Яa-z-A-Z]{0,50}", true, "Данные введены некорректно"));

        final TextField number = new TextField("Номер телефона", grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Номер телефона").getValue().toString());
        number.setSizeFull();
        number.setRequired(true);
        number.setRequiredError("Укажите телефон");
        number.setMaxLength(50);
        number.addValidator(new RegexpValidator("[' '\\-()0-9]{1,50}", true, "Данные введены некорректно"));

        final Button saveClient = new Button("Применить");
        saveClient.setStyleName(ValoTheme.BUTTON_SMALL);


        saveClient.addClickListener(clickEvent -> {

            try {
                surname.validate();
                firstName.validate();
                number.validate();

                Database.startDatabase();
                Long id = (Long) grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("ID").getValue();
                Client client = new Client(surname.getValue().trim(), firstName.getValue().trim(), patronymic.getValue().trim(), number.getValue().trim());
                client.setId(id);
                ClientDao.updateClient(client);
                Database.closeDatabase();

                //Перезаписываем данного клиента в таблице
                grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Фамилия").setValue(surname.getValue().trim());
                grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Имя").setValue(firstName.getValue().trim());
                grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Отчество").setValue(patronymic.getValue().trim());
                grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Номер телефона").setValue(number.getValue().trim());
                window.close();
            } catch (Validator.InvalidValueException e) {

                Notification.show("Заполните данные", Notification.TYPE_WARNING_MESSAGE);
            }
        });

        final Button cancel = new Button("Отмена");
        cancel.setStyleName(ValoTheme.BUTTON_SMALL);
        cancel.addClickListener(clickEvent -> window.close());

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponent(saveClient);
        buttons.addComponent(cancel);
        buttons.setSpacing(true);

        addClient.addComponent(surname);
        addClient.addComponent(firstName);
        addClient.addComponent(patronymic);
        addClient.addComponent(number);
        addClient.addComponent(buttons);

        window.setContent(addClient);
        return window;
    }

    public void deleteClient(Grid grid){

        if (grid.getSelectedRow() == null) return;

        Database.startDatabase();

        Long id = (Long) grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("ID").getValue();
        Database.startDatabase();
        int result = ClientDao.deleteClient(id);
        Database.closeDatabase();


        if (result == 0) {
            grid.getContainerDataSource().removeItem(grid.getSelectedRow());
            Notification.show("Удален клиент с ID = " + id.toString());
        } else if (result == 1) {

            Notification.show("Для клиента с ID = " + id.toString() + " существует заказ!", Notification.TYPE_ERROR_MESSAGE);
        } else Notification.show("Ошибка базы данных!",Notification.TYPE_ERROR_MESSAGE);
    }

}
