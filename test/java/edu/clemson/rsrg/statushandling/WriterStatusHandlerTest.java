package edu.clemson.rsrg.statushandling;

import edu.clemson.rsrg.parsing.data.Location;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.Writer;

public class WriterStatusHandlerTest {
    @Test
    public void testRetrieveWarningCount_givenNoWarnings_returnZero() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));
        assert(testHandler.retrieveWarningCount() == 0);
    }

    @Test
    public void testRetrieveWarningCount_givenSingleWarning_returnOne() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));
        testHandler.warning(new Location(null, 12 ,12), "test single error");
        assert(testHandler.retrieveWarningCount() == 1);
    }

    @Test
    public void testRetrieveWarningCount_givenMultipleWarnings_returns5() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));
        testHandler.warning(new Location(null, 12 ,12), "test single error");
        testHandler.warning(new Location(null, 12 ,12), "test single error");
        testHandler.warning(new Location(null, 12 ,12), "test single error");
        testHandler.warning(new Location(null, 12 ,12), "test single error");
        testHandler.warning(new Location(null, 12 ,12), "test single error");
        assert(testHandler.retrieveWarningCount() == 5);
    }
}
