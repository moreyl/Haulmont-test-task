package com.haulmont.testtask.entity;

public class Client {

    private String surname;
    private String firstName;
    private String patronymic;
    private String number;
    private long   id;

    public Client(String newSurname, String newFirstName, String newPatronymic, String newNumber){
        this.surname = newSurname;
        this.firstName = newFirstName;
        this.patronymic = newPatronymic;
        this.number = newNumber;
    }

    public Client(){
    }

    public Client (Client client){
        this.surname = client.getSurname();
        this.firstName = client.getFirstName();
        this.patronymic = client.getPatronymic();
        this.number = client.getNumber();
        this.id = client.getId();
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getSurname(){
        return this.surname;
    }

    public String getPatronymic(){
        return this.patronymic;
    }

    public String getNumber(){
        return this.number;
    }

    public long getId(){
        return this.id;
    }

    public void setFirstName(String newFirstName){
        this.firstName = newFirstName;
    }

    public void setSurname(String newSurname){
        this.surname = newSurname;
    }

    public void setPatronymic(String newPatronymic){
        this.patronymic = newPatronymic;
    }

    public void setNumber(String newNumber){
        this.number = newNumber;
    }

    public void setId(long newId){
        this.id = newId;
    }
}
