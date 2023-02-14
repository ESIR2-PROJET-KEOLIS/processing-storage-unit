package com.treatmentunit.restservice;

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
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class APIController {

    private static final String template = "Hello %s !";
    private static final AtomicLong counter = new AtomicLong();

    OptimisationAndFormating optAndForm = new OptimisationAndFormating();

    @GetMapping("/test")
    public String greetin(@RequestParam(value = "id") int id) throws IOException {

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = Files.newBufferedReader(Paths.get("C:\\Users\\1234Y\\OneDrive\\Documents\\DossierEtudes2\\PROJ-SI\\rest-service\\rest-service\\src\\main\\java\\com\\treatmentunit\\restservice\\coords_to_test.txt"));

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        //OptimisationAndFormating.FormatingAndInversing(sb.toString());
        return optAndForm.getOutput(sb.toString());
        //return optAndForm.getOutput(sb.toString());
    }

    /*
    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @GetMapping("/greeting2")
    public Greeting greeting2(@RequestParam(value = "name") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
    */
}