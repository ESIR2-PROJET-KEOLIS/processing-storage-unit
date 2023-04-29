package com.treatmentunit.simulation;
import com.beust.ah.A;
import com.treatmentunit.simulation.OptimisationAndFormating;
import org.junit.Assert.*;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.treatmentunit.simulation.OptimisationAndFormating.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testng.Reporter.getOutput;

public class OptimisationAndFormatingTest {

    @Test
    public void distanceViaLatEtLongTest() {
        /*
        Source oracles et coords -> Google maps
         */
        int marge_erreur = 12; // 12 mètres

        // 48.12443614630945,
        // 48.122373546983475, -1.6736530006409531
        assertEquals(543.37, distanceViaLatEtLong(48.12443614630945, 48.122373546983475, -1.6804121676419894, -1.6736530006409531), marge_erreur);

        // 48.13243838513884, -1.6861146662743531
        //48.11632053416394, -1.6500580163852006
        assertEquals(3210, distanceViaLatEtLong(48.13243838513884, 48.11632053416394, -1.6861146662743531, -1.6500580163852006), marge_erreur);

        // 48.11620396609113, -1.6755895230973683
        // 48.11581328641408, -1.6556246014560367
        assertEquals(1480, distanceViaLatEtLong(48.11620396609113, 48.11581328641408, -1.6755895230973683, -1.6556246014560367), marge_erreur);
    }

    @Test
    public void haversineTest() {
        /*
        Source oracles et coords -> Google maps
         */
        double marge_erreur = 0.05; // 50 mètres

        // 48.11944128932609, -1.6925657473421045
        // 48.12113157749242, -1.667202777833453
        assertEquals(1.89, haversine(48.11944128932609, -1.6925657473421045, 48.12113157749242, -1.667202777833453), marge_erreur);

        // 48.12247803843689, -1.698745557171624
        // 48.115057740559976, -1.6529548829487282
        assertEquals(3.49, haversine(48.12247803843689, -1.698745557171624, 48.115057740559976, -1.6529548829487282), marge_erreur);
    }

    @Test
    public void testFormatingAndInversing() {
        String input = "[[40.741895,-73.989308],[40.741804,-73.989378],[40.741925,-73.989527],[40.742068,-73.989424]]";
        CopyOnWriteArrayList<String> result = FormatingAndInversing(input);
        assertEquals(3, result.size()); // 3 points  > DELTA de 10
        assertEquals("-73.989308,40.741895", result.get(0));
        assertEquals("-73.989527,40.741925", result.get(1));

    }

    @Test
    public void getOutputTest() {
        OptimisationAndFormating optimisationAndFormating = new OptimisationAndFormating();
        String input = "[[48.8566,2.3522],[51.5074,-0.1278],[40.7128,-74.0060]]";
        String current_bus_pos = "[2.3522,48.8590]";
        String expectedOutput = "[[2.3522,48.8566],[-0.1278,51.5074],[-74.0060,40.7128]];1";
        String actualOutput = optimisationAndFormating.getOutput(input, current_bus_pos);
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void generateTimestampsTest() {
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(9, 30);
        int n = 9;
        List<LocalTime> timestamps = generateTimestamps(start, end, n);
        assertEquals(n, timestamps.size());
        assertEquals(LocalTime.of(8, 9), timestamps.get(0));
        assertEquals(LocalTime.of(8, 18), timestamps.get(1));
        assertEquals(LocalTime.of(8, 27), timestamps.get(2));
        assertEquals(LocalTime.of(8, 36), timestamps.get(3));
        assertEquals(LocalTime.of(8, 45), timestamps.get(4));
        assertEquals(LocalTime.of(8, 54), timestamps.get(5));
        assertEquals(LocalTime.of(9, 3), timestamps.get(6));
        assertEquals(LocalTime.of(9, 12), timestamps.get(7));
        assertEquals(LocalTime.of(9, 21), timestamps.get(8));
    }

}