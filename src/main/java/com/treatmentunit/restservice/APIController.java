package com.treatmentunit.restservice;

import com.treatmentunit.database.DatabaseBinding;
import com.treatmentunit.simulation.OptimisationAndFormating;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class APIController {

    private static final String template = "Hello %s !";
    private static final AtomicLong counter = new AtomicLong();
    static DatabaseBinding databaseBinding = new DatabaseBinding();

    static OptimisationAndFormating optAndForm = new OptimisationAndFormating();

    @GetMapping("/optimizedpath")
    public static String parcours(@RequestParam(value = "line") String line, @RequestParam(value = "sens") String sens) throws IOException, SQLException {
        // 0 Aller
        // 1 Retour
        try {
            String REQ = "SELECT DISTINCT tab_coordonnes FROM parcours_geo s, parcours_lignes_bus_star e where s.parcours_lignes_bus_star_id = e.parcours_lignes_bus_star_id and nomcourtligne = '" + line + "' and sens = '" + sens + "\'" + " and type=\'Principal\'";
            String single_val = databaseBinding.requestFetchSingleValue(REQ);
            String returned = optAndForm.getOutput(single_val, "");
            String[] val = returned.split(";");
            return val[0];

        } catch (SQLException | InterruptedException sqlException) {
            System.out.println("[!] Erreur SQL !");
            sqlException.printStackTrace();
            return "Erreur SQL ! ";
        }
    }

    @GetMapping("/pathsforarray")
    public static String parcoursArray(@RequestParam(value = "line") String line) {
        String REQ = "";

        return REQ;
    }

}