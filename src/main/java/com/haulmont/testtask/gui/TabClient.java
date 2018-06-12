package com.haulmont.testtask.gui;

import com.haulmont.testtask.dao.ClientDao;
import com.haulmont.testtask.dao.Database;
import com.haulmont.testtask.entity.Client;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.util.List;

public class TabClient {

    public VerticalLayout tabClient() {

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.setHeight("70");

        Button addClient = new Button("Добавить");
        addClient.addStyleName(ValoTheme.BUTTON_SMALL);
        addClient.addStyleName(ValoTheme.BUTTON_PRIMARY);
        Button updateClient = new Button("Изменить");
        Button deleteClient = new Button("Удалить");
        updateClient.setStyleName(ValoTheme.BUTTON_SMALL);
        deleteClient.setStyleName(ValoTheme.BUTTON_SMALL);

        HorizontalLayout allButtons = new HorizontalLayout();
        allButtons.addComponent(addClient);
        allButtons.addComponent(updateClient);
        allButtons.addComponent(deleteClient);
        allButtons.setSpacing(true);

        buttons.addComponent(allButtons);
        buttons.setComponentAlignment(allButtons, Alignment.BOTTOM_LEFT);

        Grid grid = new Grid();
        grid.addColumn("ID", Long.class).setWidth(60);
        grid.addColumn("Фамилия", String.class);
        grid.addColumn("Имя", String.class);
        grid.addColumn("Отчество", String.class);
        grid.addColumn("Номер телефона", String.class);
        grid.setWidth("65%");
        grid.setHeight("100%");
        grid.setEditorEnabled(false);
        grid.setStyleName(ValoTheme.TABLE_COMPACT);

        Database.startDatabase();
        List<Client> clients = ClientDao.getAllClient();
        int index = clients.size();
        for (int i = 0; i < index; i++) {
            grid.addRow(clients.get(i).getId(), clients.get(i).getSurname(), clients.get(i).getFirstName(),
                        clients.get(i).getPatronymic(), clients.get(i).getNumber());
        }
        Database.closeDatabase();

        addClient.addClickListener(event -> buttons.getUI().getUI().addWindow(new EditClientTable().addClient(grid)));

        updateClient.addClickListener(event -> {
            if (grid.getSelectedRow() != null)
                buttons.getUI().getUI().addWindow(new EditClientTable().updateClient(grid));
        });

        deleteClient.addClickListener(event -> new EditClientTable().deleteClient(grid));

        VerticalLayout verticalLayoutClient = new VerticalLayout();
        verticalLayoutClient.setHeight("100%");
        verticalLayoutClient.addComponent(buttons);
        verticalLayoutClient.setComponentAlignment(buttons, Alignment.TOP_LEFT);
        verticalLayoutClient.addComponent(grid);
        verticalLayoutClient.setComponentAlignment(grid, Alignment.MIDDLE_LEFT);
        verticalLayoutClient.setSpacing(true);
        verticalLayoutClient.setSizeFull();

        return verticalLayoutClient;
    }

}
