package com.haulmont.testtask.entity;

import java.sql.Date;

public class Order {

    private long id;
    private String description;
    private long clientID;
    private Date dataOfCreation;
    private Date dataOfCompletion;
    private double price;
    private StatusDescription statusDescription;
    private enum StatusDescription {Запланирован, Выполнен, Принят_клиентом}

    public Order(String newDescription, long newClientID, Date newDataOfCreation, Date newDataOfCompletion, double newPrice, String statusDescription){
        this.description = newDescription;
        this.clientID = newClientID;
        this.dataOfCreation = newDataOfCreation;
        this.dataOfCompletion = newDataOfCompletion;
        this.price = newPrice;
        try {
            if (statusDescription.equals("Принят клиентом"))
                statusDescription = "Принят_клиентом";
            this.statusDescription = StatusDescription.valueOf(statusDescription);
        } catch (IllegalArgumentException e) {
            System.out.println("Exception: Некорректный статус заказа!");
        }
    }

    public Order(Order order){
        try {
            this.id = order.getId();
            this.description = order.getDescription();
            this.clientID = order.getClientID();
            this.dataOfCreation = order.getDataOfCreation();
            this.dataOfCompletion = order.getDataOfCompletion();
            this.price = order.getPrice();
            if (order.getStatusDescription().equals("Принят клиентом")) this.statusDescription = StatusDescription.valueOf("Принят_клиентом");
            else this.statusDescription = StatusDescription.valueOf(order.getStatusDescription());
        } catch (IllegalArgumentException e) {
            System.out.println("Exception: Некорректный статус заказа!");
        }

    }

    public Order(){
    }

    public long getId() {
        return id;
    }

    public String getDescription(){
        return this.description;
    }

    public long getClientID(){
        return this.clientID;
    }

    public Date getDataOfCreation(){
        return this.dataOfCreation;
    }

    public Date getDataOfCompletion(){
        return this.dataOfCompletion;
    }

    public double getPrice(){
        return  this.price;
    }

    public String getStatusDescription(){
        if (this.statusDescription.toString() == "Принят_клиентом") return "Принят клиентом";
         else return this.statusDescription.toString();

    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDescription(String newDescription){
        this.description = newDescription;
    }

    public void setClientID(long newClientID) {
        this.clientID = newClientID;
    }

    public void setDataOfCreation(Date newDataOfCreation) {
        this.dataOfCreation = newDataOfCreation;
    }

    public void setDataOfCompletion(Date newDataOfCompertion) {
        this.dataOfCompletion = newDataOfCompertion;
    }

    public void setPrice(double newPrice) {
        this.price = newPrice;
    }

    public void setStatusDescription(String statusDescription) {
        try {
            if (statusDescription.equals("Принят клиентом")) this.statusDescription = StatusDescription.valueOf("Принят_клиентом");
             else this.statusDescription = StatusDescription.valueOf(statusDescription);
        } catch (IllegalArgumentException e) {
            System.out.println("Exception: Некорректный статус заказа!");
        }
    }

}


