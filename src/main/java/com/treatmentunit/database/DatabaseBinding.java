package com.treatmentunit.database;
import javax.xml.crypto.Data;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;

// DATABASE BINDING WITH "MYDB" ONLY !!

public class DatabaseBinding {

    private static Connection con;
    private ResultSet result;
    private boolean requested;
    private static Statement statement;

    public static void connectToSqlSocket() {
        try {
            String host = System.getenv("DB_HOST");
            if(host == null) host = "localhost";
            System.out.println(host);
            String env_port = System.getenv("DB_PORT");
            int port = 6033;
            if(env_port != null) port = Integer.parseInt(env_port);
            String db_name = "mydb";
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[*] Tentative de connexion à la base de données...");
            con = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+db_name+"?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", "root", "root");
            statement = con.createStatement();
            System.out.println("[*] Connexion réussie à la base de données.");

            //boolean let = statement.execute("INSERT INTO calendar_dates VALUES (0, 21/02/2022, 'test')");

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("[!] Erreur de connexion à la base de données.");
            throw new RuntimeException(e);
        }
    }

    public void requestInsert(String query) {
        try {
            requested = statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[!] Erreur lors de l'insertion dans la base de données.");
            System.out.println("[!] Requête en cause : " + query);
        }
    }

    /*
    public ArrayList<String> requestFetchSingleValue(String query) throws SQLException, InterruptedException {
        ArrayList<String> fetched = new ArrayList<>();
            try {
                result = statement.executeQuery(query);
                if(result.next()) {
                    ResultSetMetaData metadata = result.getMetaData();
                    int col_count = metadata.getColumnCount();
                    for (int i = 1; i <= col_count; i++) {
                        fetched.add(result.getString(i));
                    }
                }
            } catch (SQLException e) {
                Thread.sleep(12);
            }

        return fetched;
    }
     */


    public String requestFetchSingleValue(String query) throws SQLException, InterruptedException {
        ArrayList<String> fetched = new ArrayList<>();
        try {
            result = statement.executeQuery(query);
            result.next();
            ResultSetMetaData metadata = result.getMetaData();
            int col_count = metadata.getColumnCount();
        } catch (SQLException e) {
            Thread.sleep(12);
        }

        return result.getString(0);
    }

}