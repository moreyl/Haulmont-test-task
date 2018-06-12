package com.haulmont.testtask.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    public static Connection connection;
    public static Statement  statement;

    static public void startDatabase(){
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Database massage: Не удалось загрузить драйвер HSQLDB!");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            connection = DriverManager.getConnection(
                    "jdbc:hsqldb:file:src/main/database/haulmont", "SA", "");

            statement  = connection.createStatement();
        } catch (SQLException e) {
            System.err.println("Database massage: Не удалось подключиться к базе данных!");
            e.printStackTrace();
            System.exit(2);
        }
    }

   static  public void closeDatabase(){
        try {
            String query = "SHUTDOWN";
            statement.execute(query);
        } catch (SQLException e) {
            System.out.println("Database massage: База данных не закрыта!");
        }
    }

}
