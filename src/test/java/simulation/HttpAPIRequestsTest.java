package com.treatmentunit.simulation;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class HttpAPIRequestsTest {

    @Test
    public void requestToAIAPIforFlowSimulationTest() throws IOException {
        HttpAPIRequests httpAPIRequests = new HttpAPIRequests();
        String response = httpAPIRequests.requestToAIAPIforFlowSimulation(
                "C1",
                "0",
                25.064926,
                131.833328,
                6.0,
                13219.599609,
                1.0);

        assertEquals(response, "{\"maxFreqH\":0.23999997973442078,\"maxFreqL\":0.6399999856948853,\"maxFreqM\":0.11999999731779099}");

        response = httpAPIRequests.requestToAIAPIforFlowSimulation(
                "C1",
                "0",
                95.5415,
                58.484512,
                19.0,
                10975.41592,
                0.0);

        assertEquals(response, "{\"maxFreqH\":0.34999993443489075,\"maxFreqL\":0.46599990129470825,\"maxFreqM\":0.18399998545646667}");
    }
}
