package edu.clemson.rsrg.statushandling;

import org.junit.Test;

import java.io.PrintWriter;
import java.io.Writer;

public class WriterStatusHandlerTest {
    @Test
    public void testRetrieveWarningCount_givenNoWarnings_returnZero() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));
        assert(testHandler.retrieveWarningCount() == 0);
    }
}
