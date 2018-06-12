package com.haulmont.testtask.gui;

import com.haulmont.testtask.dao.Database;
import com.haulmont.testtask.dao.OrderDao;
import com.haulmont.testtask.entity.Order;
import com.vaadin.event.SelectionEvent;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import java.util.List;
public class TabOrder {

    public VerticalLayout tabOrder(){

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.setHeight("70");

        Button addOrder = new Button("Добавить");
        addOrder.addStyleName(ValoTheme.BUTTON_SMALL);
        addOrder.addStyleName(ValoTheme.BUTTON_PRIMARY);
        Button updateOrder = new Button("Изменить");
        Button deleteOrder = new Button("Удалить");
        updateOrder.setStyleName(ValoTheme.BUTTON_SMALL);
        deleteOrder.setStyleName(ValoTheme.BUTTON_SMALL);

        HorizontalLayout allButtons = new HorizontalLayout();
        allButtons.addComponent(addOrder);
        allButtons.addComponent(updateOrder);
        allButtons.addComponent(deleteOrder);
        allButtons.setSpacing(true);

        buttons.addComponent(allButtons);
        buttons.setComponentAlignment(allButtons, Alignment.BOTTOM_LEFT);

        Grid grid = new Grid();
        grid.addColumn("ID", Long.class).setWidth(60);
        grid.addColumn("Описание", String.class).setWidth(300);
        grid.addColumn("ID Клиента", Long.class).setWidth(130);
        grid.addColumn("Дата создания", String.class);
        grid.addColumn("Дата окончания работ", String.class);
        grid.addColumn("Стоимость", Double.class);
        grid.addColumn("Статус", String.class);
        grid.setSizeFull();
        grid.setEditorEnabled(false);
        grid.setStyleName(ValoTheme.TABLE_COMPACT);

        Database.startDatabase();
        List<Order> orders = OrderDao.getAllOrder();
        int index = orders.size();
        for (int i=0; i<index; i++) {
            grid.addRow(orders.get(i).getId(), orders.get(i).getDescription(), orders.get(i).getClientID(), orders.get(i).getDataOfCreation().toString(),
                    orders.get(i).getDataOfCompletion().toString(), orders.get(i).getPrice(), orders.get(i).getStatusDescription());
        }
        Database.closeDatabase();

        TextArea fullDescription = new TextArea();
        fullDescription.setEnabled(false);
        fullDescription.setWidth("64.7%");
        fullDescription.setHeight("40");
        fullDescription.setStyleName(ValoTheme.TEXTAREA_LARGE);
        fullDescription.setVisible(false);

        grid.addSelectionListener(new SelectionEvent.SelectionListener() {
            @Override
            public void select(SelectionEvent event) {

                if (grid.isSelected(grid.getSelectedRow()))
                    if (grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Описание").getValue().toString().length() > grid.getColumn("Описание").getWidth() / 10) {
                        fullDescription.setValue("Полное описание заказа: " + grid.getContainerDataSource().getItem(grid.getSelectedRow()).getItemProperty("Описание").getValue().toString());
                        fullDescription.setVisible(true);
                        return;
                    }
                fullDescription.setVisible(false);
            }
        });

        addOrder.addClickListener(event -> grid.getUI().getUI().addWindow(new EditOrderTable().addOrder(grid, fullDescription)));

        updateOrder.addClickListener(event -> {

            if (grid.getSelectedRow() != null)
                grid.getUI().getUI().addWindow(new EditOrderTable().updateOrder(grid, fullDescription));
        });

        deleteOrder.addClickListener(event -> new EditOrderTable().deleteOrder(grid));
        FormLayout filter = new FormLayout();
        filter.addComponent(new EditOrderTable().filter(grid));

        HorizontalLayout horizontalLayoutTop = new HorizontalLayout();
        horizontalLayoutTop.addComponent(buttons);
        horizontalLayoutTop.setComponentAlignment(buttons, Alignment.TOP_LEFT);
        horizontalLayoutTop.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);

        HorizontalLayout horizontalLayoutBottom = new HorizontalLayout();
        horizontalLayoutBottom.setSpacing(true);
        horizontalLayoutBottom.setWidth(130.0f, Sizeable.Unit.PERCENTAGE);
        horizontalLayoutBottom.setHeight("100%");
        horizontalLayoutBottom.addComponent(grid);
        horizontalLayoutBottom.setComponentAlignment(grid, Alignment.TOP_LEFT);
        horizontalLayoutBottom.addComponent(filter);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setHeightUndefined();
        verticalLayout.setSpacing(true);

        verticalLayout.addComponent(horizontalLayoutTop);
        verticalLayout.addComponent(horizontalLayoutBottom);
        verticalLayout.addComponent(fullDescription);

        return  verticalLayout;
    }

}

