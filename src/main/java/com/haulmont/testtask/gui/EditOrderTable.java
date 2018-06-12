package com.haulmont.testtask.gui;

import com.haulmont.testtask.dao.ClientDao;
import com.haulmont.testtask.dao.Database;
import com.haulmont.testtask.dao.OrderDao;
import com.haulmont.testtask.entity.Client;
import com.haulmont.testtask.entity.Order;
import com.vaadin.data.Validator;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.validator.*;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EditOrderTable {

    public Window addOrder(Grid grid, TextArea fullDescription) {

        final Window window = new Window("Добавить новый заказ");
        window.setModal(true);
        window.center();
        window.setWidth("450");
        window.setHeight("400");
        window.setClosable(false);
        window.setResizable(false);

        final FormLayout addOrder = new FormLayout();
        addOrder.setMargin(true);
        addOrder.setSizeFull();

        final TextField description = new TextField("Описание", "");
        description.setSizeFull();
        description.setRequired(true);
        description.setMaxLength(500);

        final DateField dataOfCreation = new DateField("Дата создания");
        Date date =  new Date();
        dataOfCreation.setValue(date);
        dataOfCreation.setSizeFull();
        dataOfCreation.setRequired(true);
        dataOfCreation.addValidator(new DateRangeValidator("Заказ должен быть принят не более одного месяца назад и не позднее сегодняшнего дня",
                                    new Date(date.getYear(), date.getMonth()-1, date.getDate()),
                                    new java.sql.Date(date.getTime()), Resolution.YEAR));


        final DateField dataOfCompletion = new DateField("Дата окончания");
        dataOfCompletion.setValue(new Date(date.getYear(), date.getMonth(), date.getDate()+1));
        dataOfCompletion.setRequired(true);
        dataOfCompletion.setSizeFull();
        dataOfCompletion.addValidator(new DateRangeValidator("Заказ может быть завершен не раньше дня создания заказа и не позднее трех месяцев с текущего дня", new Date(date.getTime()),
                                                             new Date(date.getYear(), date.getMonth()+3, date.getDate()), Resolution.YEAR));

        dataOfCreation.addValueChangeListener(event -> {
            try {
                dataOfCompletion.removeAllValidators();
                Date updateDateOfCreation = dataOfCreation.getValue();
                dataOfCompletion.addValidator(new DateRangeValidator("Заказ может быть завершен не раньше дня создания заказа и не позднее трех месяцев с текущего дня",
                                                                     new Date(updateDateOfCreation.getYear(), updateDateOfCreation.getMonth(), updateDateOfCreation.getDate()),
                                                                     new Date(date.getYear(), date.getMonth()+3, date.getDate()), Resolution.YEAR));
            } catch (NullPointerException e) {
            }

        } );


        final TextField price = new TextField("Стоимость");
        price.setRequired(true);
        price.setSizeFull();
        price.addValidator(new RegexpValidator("[0-9]{1,6}(\\.?\\,?[0-9]{1,2}){0,1}", true, "Стоимость не может быть отрицательной или быть равной 1 000 000 и более"));

        List<Long> allClientID = new ArrayList();
        Database.startDatabase();
        List<Client> clients = ClientDao.getAllClient();
        int index = clients.size();
        for (int i=0; i<index; i++) {
            allClientID.add(clients.get(i).getId());
        }
        ComboBox clientID = new ComboBox("ID Клиента", allClientID);
        clientID.setSizeFull();
        clientID.setRequired(true);
        clientID.setInputPrompt("Выберите клиента по его ID");
        clientID.setNullSelectionAllowed(false);

        List<String> allStatus = new ArrayList<>();
        allStatus.add("Запланирован");
        allStatus.add("Выполнен");
        allStatus.add("Принят клиентом");
        ComboBox status = new ComboBox("Статус", allStatus);
        status.setSizeFull();
        status.setRequired(true);
        status.setInputPrompt("Выберите статус");
        status.setNullSelectionAllowed(false);

        final Button saveOrder = new Button("Добавить");
        saveOrder.setStyleName(ValoTheme.BUTTON_SMALL);
        final Button cancel = new Button("Отмена");
        cancel.setStyleName(ValoTheme.BUTTON_SMALL);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);

        buttons.addComponent(saveOrder);
        buttons.addComponent(cancel);

        saveOrder.addClickListener(clickEvent -> {

            try {
                description.validate();
                clientID.validate();
                dataOfCreation.validate();
                dataOfCompletion.validate();
                price.validate();
                status.validate();

                Database.startDatabase();
                Order order = new Order(description.getValue().trim(), Long.parseLong(clientID.getValue().toString()), new java.sql.Date(dataOfCreation.getValue().getTime()),
                                        new java.sql.Date(dataOfCompletion.getValue().getTime()), Double.parseDouble(price.getValue().replace(',', '.')),
                                        status.getValue().toString() );
                OrderDao.addOrder(order);
                List<Order> orders = OrderDao.getAllOrder();

                int size = orders.size();
                grid.addRow(orders.get(size-1).getId(), orders.get(size-1).getDescription(), orders.get(size-1).getClientID(), orders.get(size-1).getDataOfCreation().toString(),
                            orders.get(size-1).getDataOfCompletion().toString(), orders.get(size-1).getPrice(), orders.get(size-1).getStatusDescription());
                Database.closeDatabase();

                if (description.getValue().length() > grid.getColumn("Описание").getWidth() / 10) {
                    fullDescription.setValue("Полное описание заказа: " + grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Описание").getValue().toString());
                    fullDescription.setVisible(true);
                } else fullDescription.setVisible(false);
                window.close();

            } catch (Validator.InvalidValueException e) {

                description.setRequiredError("Введите описание заказа");
                clientID.setRequiredError("Укажите клиента");
                price.setRequiredError("Введите стоимость заказа");
                status.setRequiredError("Укажите статус заказа");
                Notification.show("Заполните данные", Notification.TYPE_WARNING_MESSAGE);
            }
        });

        cancel.addClickListener(clickEvent -> window.close());

        addOrder.addComponent(description);
        addOrder.addComponent(clientID);
        addOrder.addComponent(dataOfCreation);
        addOrder.addComponent(dataOfCompletion);
        addOrder.addComponent(price);
        addOrder.addComponent(status);
        addOrder.addComponent(buttons);

        window.setContent(addOrder);
        return window;
    }

    public Window updateOrder(Grid grid, TextArea fullDescription) {

        final Window window = new Window("Изменение заказа");
        window.setModal(true);
        window.center();
        window.setWidth("450");
        window.setHeight("400");
        window.setClosable(false);
        window.setResizable(false);

        final FormLayout addOrder = new FormLayout();
        addOrder.setMargin(true);
        addOrder.setSizeFull();

        final TextField description = new TextField("Описание", grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Описание").getValue().toString());
        description.setSizeFull();
        description.setRequired(true);
        description.setRequiredError("Опишите выполняемую работу");
        description.setMaxLength(500);

        SimpleDateFormat dateFotmat = new SimpleDateFormat("yyyy-MM-dd");

        final DateField dataOfCreation = new DateField("Дата создания");
        Date date =  new Date();
        try {
            dataOfCreation.setValue(dateFotmat.parse(grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Дата создания").getValue().toString()));
        } catch(Exception e) {
            System.out.println("Order Table: Ошибка в считывании Даты создания");
        }
        dataOfCreation.setSizeFull();
        dataOfCreation.setRequired(true);

        final DateField dataOfCompletion = new DateField("Дата окончания");
        try {
            dataOfCompletion.setValue(dateFotmat.parse(grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Дата окончания работ").getValue().toString()));
        } catch(Exception e) {
            System.out.println("Order Table: Ошибка в считывании Даты окончания работ");
        }
        dataOfCompletion.setSizeFull();
        dataOfCompletion.setRequired(true);
        dataOfCompletion.setRequiredError("Необходимо указать дату завершения работ");


        dataOfCompletion.setRequiredError("Необходимо указать дату завершения работ");
        dataOfCompletion.addValidator(new DateRangeValidator("Заказ может быть завершен не раньше дня создания заказа и не позднее трех месяцев с текущего дня",
                                      new Date(dataOfCreation.getValue().getTime()), new Date(date.getYear(), date.getMonth()+3, date.getDate()), Resolution.YEAR));

        dataOfCreation.addValueChangeListener(event -> {
            dataOfCreation.setRequiredError("Необходимо указать дату создания заказа");
            dataOfCreation.removeAllValidators();
            dataOfCreation.addValidator(new DateRangeValidator("Заказ должен быть принят не более одного месяца назад и не позднее сегодняшнего дня",
                                        new Date(date.getYear(), date.getMonth()-1, date.getDate()),
                                        new java.sql.Date(date.getTime()), Resolution.YEAR));

            try {
                dataOfCompletion.removeAllValidators();
                Date updateDateOfCreation = dataOfCreation.getValue();
                dataOfCompletion.addValidator(new DateRangeValidator("Заказ может быть завершен не раньше дня создания заказа и не позднее трех месяцев с текущего дня!",
                                              new Date(updateDateOfCreation.getYear(), updateDateOfCreation.getMonth(), updateDateOfCreation.getDate()),
                                              new Date(date.getYear(), date.getMonth()+3, date.getDate()), Resolution.YEAR));
            } catch (NullPointerException e) {
                Notification.show("Ошибка введенных дат");
            }
        } );

        final TextField price = new TextField("Стоимость",  grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Стоимость").getValue().toString());
        price.setSizeFull();
        price.setRequired(true);
        price.setRequiredError("укажите стоимость");

        price.addValidator(new RegexpValidator("[0-9]{1,6}(\\.?\\,?[0-9]{1,2}){0,1}", true, "Стоимость не может быть отрицательной или быть равной 1 000 000 и более"));

        List<Long> allClientID = new ArrayList();
        Database.startDatabase();
        List<Client> clients = ClientDao.getAllClient();
        int index = clients.size();
        for (int i=0; i<index; i++) {
            allClientID.add(clients.get(i).getId());
        }

        ComboBox clientID = new ComboBox("ID Клиента", allClientID);
        clientID.setSizeFull();
        clientID.setRequired(false);
        clientID.setValue(Long.parseLong(grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("ID Клиента").getValue().toString()));
        clientID.setNullSelectionAllowed(false);

        List<String> allStatus = new ArrayList<>();
        allStatus.add("Запланирован");
        allStatus.add("Выполнен");
        allStatus.add("Принят клиентом");

        ComboBox status = new ComboBox("Статус", allStatus);
        status.setSizeFull();
        status.setValue(grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Статус").getValue().toString());
        status.setNullSelectionAllowed(false);
        status.setRequired(false);

        final Button updateOrder = new Button("Изменить");
        updateOrder.setStyleName(ValoTheme.BUTTON_SMALL);
        final Button cancel = new Button("Отмена");
        cancel.setStyleName(ValoTheme.BUTTON_SMALL);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponent(updateOrder);
        buttons.addComponent(cancel);
        buttons.setSpacing(true);

        updateOrder.addClickListener(clickEvent -> {
            try {
                description.validate();
                clientID.validate();
                dataOfCreation.validate();
                dataOfCompletion.validate();
                price.validate();
                status.validate();

                Database.startDatabase();
                Long id = (Long) grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("ID").getValue();
                Order order = new Order(description.getValue().trim(), Long.parseLong(clientID.getValue().toString()), new java.sql.Date(dataOfCreation.getValue().getTime()),
                                        new java.sql.Date(dataOfCompletion.getValue().getTime()), Double.parseDouble(price.getValue().replace(',', '.')), status.getValue().toString());
                order.setId(id);
                OrderDao.updateOrder(order);
                Database.closeDatabase();

                grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Описание").setValue(description.getValue());
                grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("ID Клиента").setValue(clientID.getValue());
                grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Дата создания").setValue(new java.sql.Date(dataOfCreation.getValue().getTime()).toString());
                grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Дата окончания работ").setValue(new java.sql.Date(dataOfCompletion.getValue().getTime()).toString());
                grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Стоимость").setValue(Double.parseDouble(price.getValue().replace(',', '.')));
                grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Статус").setValue(status.getValue());

                if (description.getValue().length() > grid.getColumn("Описание").getWidth() / 10) {
                    fullDescription.setValue("Полное описание заказа: " + grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Описание").getValue().toString());
                    fullDescription.setVisible(true);
                } else fullDescription.setVisible(false);

                window.close();
            } catch (Validator.InvalidValueException e) {

                Notification.show("Заполните данные", Notification.TYPE_WARNING_MESSAGE);
            }
         });

        cancel.addClickListener(clickEvent -> {
            window.close();
        });

        addOrder.addComponent(description);
        addOrder.addComponent(clientID);
        addOrder.addComponent(dataOfCreation);
        addOrder.addComponent(dataOfCompletion);
        addOrder.addComponent(price);
        addOrder.addComponent(status);
        addOrder.addComponent(buttons);


        window.setContent(addOrder);
        return window;
    }

    public  void deleteOrder(Grid grid){

        if (grid.getSelectedRow() == null) return;

        Long id = (Long) grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("ID").getValue();
        Database.startDatabase();
        OrderDao.deleteOrder(id);
        Database.closeDatabase();

        grid.getContainerDataSource().removeItem(grid.getSelectedRow());
        Notification.show("Удален заказ с ID = "+id.toString());
    }

    public FormLayout filter(Grid grid){

        Label textFilter = new Label("Фильтр");
        textFilter.setStyleName(ValoTheme.LABEL_H3);

        TextField clientID = new TextField("ID Клиента", "");
        clientID.setWidth("200");
        TextField status = new TextField("Статус", "");
        status.setWidth("200");
        TextField description = new TextField("Описание", "");
        description.setWidth("200");
        description.setMaxLength(500);

        Button apply = new Button("Применить");
        apply.setStyleName(ValoTheme.BUTTON_SMALL);

        apply.addClickListener(event-> {

            Filterable filterable = (Filterable) grid.getContainerDataSource();
            filterable.removeAllContainerFilters();

            Filter filterClientID = new SimpleStringFilter("ID Клиента", clientID.getValue().toString(),
                    true, false);
            Filter filterStatus = new SimpleStringFilter("Статус", status.getValue().toString(),
                    true, false);
            Filter filterDescription = new SimpleStringFilter("Описание", description.getValue().toString(),
                    true, false);

            filterable.addContainerFilter(filterClientID);
            filterable.addContainerFilter(filterStatus);
            filterable.addContainerFilter(filterDescription);
        });

        FormLayout filterLayout = new FormLayout();
        filterLayout.addStyleName(ValoTheme.TABLE_BORDERLESS);
        filterLayout.setWidth("295");

        filterLayout.addComponent(textFilter);
        filterLayout.setComponentAlignment(textFilter, Alignment.TOP_LEFT);
        filterLayout.addComponent(clientID);
        filterLayout.addComponent(status);
        filterLayout.addComponent(description);
        filterLayout.addComponent(apply);
        return filterLayout;
    }
}
