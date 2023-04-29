package com.treatmentunit.database;
import javax.xml.crypto.Data;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;

// DATABASE BINDING WITH "MYDB" ONLY !!

/**
 * Classe DatabaseBinding sert à la manipulation de la base de données.
 */
public class DatabaseBinding {



    private static Connection con;
    private ResultSet result;
    private boolean requested;
    private static Statement statement;
    private static boolean connected_to_db_server = false;


    /**
     * La méthode connectToSqlSocket nous connect à la base de données
     */
    public static void connectToSqlSocket() {

        while(!connected_to_db_server) {
            try {
                String host = System.getenv("DB_HOST");
                if(host == null) host = "localhost";
                System.out.println(host);
                String env_port = System.getenv("DB_PORT");
                int port = 6033;
                if(env_port != null) port = Integer.parseInt(env_port);
                String db_name = "mydb";
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("[*] Tentative de connexion à la base de données..");
                con = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+db_name+"?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", "root", "root");
                statement = con.createStatement();
                System.out.println("[*] Connexion réussie à la base de données.");
                connected_to_db_server = true;

            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("[!] Connexion impossible.");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e2) {
                    System.out.println("[!] InterruptedException occured.");
                }
            }
        }
    }

    /**
     * La méthode requestInsert envoie une requête d'insertion à la base de données
     * @param query est une requête sql d'insertion
     */
    public void requestInsert(String query) {
        try {
            requested = statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[!] ERREUR LORS DE L'INSERTION DANS LA BDD.");

            //System.out.println("[!] Requête en cause : " + query);
        }
    }

    /**
     * La méthode requestFetchNColumns envoie une requête à la base de données et renvoie la réponse sous forme
     *      * de tableau de tableaux de chaine de caractère
     * @param query est une requête Sql
     * @return ArrayList< ArrayList < String>>  représentant les colonnes et les lignes en sortie de la requête
     * @throws SQLException
     * @throws InterruptedException
     */
    public ArrayList<ArrayList<String>> requestFetchNColumns(String query) throws SQLException, InterruptedException {
        ArrayList<ArrayList<String>> fetched = new ArrayList<>();
        synchronized (fetched) {
            try {

                ResultSet result;
                statement = con.createStatement();
                result = statement.executeQuery(query);
                ResultSetMetaData metadata = result.getMetaData();
                while (result.next()) {
                    ArrayList<String> entry = new ArrayList<>();
                    for (int i = 1; i <= metadata.getColumnCount(); i++) {
                        entry.add(result.getString(i));
                    }
                    fetched.add(entry);
                }

                return fetched;
            } catch (SQLException e) {
                Thread.sleep(12);
                throw new SQLException();
            }

        }
    }

    /**
     * la méthode requestFetchSingleValue envoie une requête à la base de données et renvoie une chaine de caractère contenant l'élément dans
     * la première ligne et première colonne  de la réponse
     * @param query une requête sql
     * @return String contenant la valeur dans la première ligne et première colonne de la réponse
     * @throws SQLException
     * @throws InterruptedException
     */
    public String requestFetchSingleValue(String query) throws SQLException, InterruptedException {
        ArrayList<String> fetched = new ArrayList<>();
        String val = "";
        synchronized (fetched) {
            try {
                ResultSet result;
                statement = con.createStatement();
                result = statement.executeQuery(query);
                result.next();
                ResultSetMetaData metadata = result.getMetaData();
                int col_count = metadata.getColumnCount();
                val = result.getString(1);
                statement.close();
            } catch (SQLException e) {
                Thread.sleep(12);
                throw new SQLException();
            }
        }

        return val;
    }

    public static Connection getCon() {
        return con;
    }

    public ResultSet getResult() {
        return result;
    }

    public boolean isRequested() {
        return requested;
    }

    public static Statement getStatement() {
        return statement;
    }

    public static boolean isConnected_to_db_server() {
        return connected_to_db_server;
    }

    public static void setCon(Connection con) {
        DatabaseBinding.con = con;
    }

    public void setResult(ResultSet result) {
        this.result = result;
    }

    public void setRequested(boolean requested) {
        this.requested = requested;
    }

    public static void setStatement(Statement statement) {
        DatabaseBinding.statement = statement;
    }

    public static void setConnected_to_db_server(boolean connected_to_db_server) {
        DatabaseBinding.connected_to_db_server = connected_to_db_server;
    }

}