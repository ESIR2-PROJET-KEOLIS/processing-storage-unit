package com.treatmentunit.database;

import javax.xml.crypto.Data;
import java.sql.*;

public class DatabaseBinding {

    private static Connection con;
    private static ResultSet result;
    private static Statement statement;

    public void connectToSqlSocket() {
        try {
            Class.forName("com.mysql.jbdc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://TODO", "USER", "PASSWD");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        DatabaseBinding databaseBinding = new DatabaseBinding();
        databaseBinding.connectToSqlSocket();
    }

    public static String request(String query) {
        try {
            result = statement.executeQuery(query);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("/!\\ REQUÊTE SQL INCORRECTE /!\\ -> DatabaseBinding.java");
        }
        return null;
    }

    public void close() {

    }

}
