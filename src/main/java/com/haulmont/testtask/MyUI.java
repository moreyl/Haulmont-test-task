package com.haulmont.testtask;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import com.haulmont.testtask.gui.*;


@Theme(ValoTheme.THEME_NAME)
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest request) {

        Label workshop = new Label("");
        workshop.setStyleName(ValoTheme.LABEL_H1);
        workshop.setHeight("1");
        workshop.setWidth(10, Unit.PICAS);

        HorizontalLayout text = new HorizontalLayout();
        text.setWidth(98, Unit.PICAS);
        text.addComponent(workshop);
        text.setComponentAlignment(workshop, Alignment.MIDDLE_CENTER);

        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidth(98.0f, Unit.PICAS);
        tabSheet.setHeight(98.0f, Unit.PERCENTAGE);
        tabSheet.addTab(new TabClient().tabClient(), "   Клиенты   ");
        tabSheet.addTab(new TabOrder().tabOrder(), "   Заказы   ");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.addComponent(text);
        mainLayout.setComponentAlignment(text, Alignment.TOP_CENTER);
        mainLayout.addComponent(tabSheet);
        mainLayout.setComponentAlignment(tabSheet, Alignment.TOP_CENTER);
        setContent(mainLayout);

    }
}