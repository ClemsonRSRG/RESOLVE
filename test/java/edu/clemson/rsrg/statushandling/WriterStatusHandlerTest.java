/*
 * WriterStatusHandlerTest.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.statushandling;

import edu.clemson.rsrg.init.file.ModuleType;
import edu.clemson.rsrg.init.file.ResolveFile;
import edu.clemson.rsrg.init.file.ResolveFileBasicInfo;
import edu.clemson.rsrg.parsing.data.Location;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import static org.mockito.Mockito.*;

public class WriterStatusHandlerTest {
    @Test
    public void testRetrieveWarningCount_givenNoWarnings_returnZero() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));
        assert (testHandler.retrieveWarningCount() == 0);
    }

    @Test
    public void testRetrieveWarningCount_givenSingleWarning_returnOne() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));

        ResolveFile rsFile = mock(ResolveFile.class);
        when(rsFile.getModuleType()).thenReturn(ModuleType.THEORY);

        Location warningLocation = new Location(rsFile, 12, 12);

        testHandler.warning(warningLocation, "test single error");
        assert (testHandler.retrieveWarningCount() == 1);
    }

    @Test
    public void testRetrieveWarningCount_givenMultipleWarnings_returns5() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));

        ResolveFile rsFile = mock(ResolveFile.class);
        when(rsFile.getModuleType()).thenReturn(ModuleType.THEORY);

        Location warningLocation = new Location(rsFile, 12, 12);

        testHandler.warning(warningLocation, "test single error");
        testHandler.warning(warningLocation, "test single error");
        testHandler.warning(warningLocation, "test single error");
        testHandler.warning(warningLocation, "test single error");
        testHandler.warning(warningLocation, "test single error");
        assert (testHandler.retrieveWarningCount() == 5);
    }

    @Test
    public void testGetWarnings_expectEmptyList_returnEmptyList() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));

        List<Warning> warnings = testHandler.getWarnings();
        assert(warnings.isEmpty());
    }

    @Test
    public void testGetWarnings_expectSingletonList_returnSingletonList() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));

        Warning testWarning = mock(Warning.class);
        ResolveFile rsFile = mock(ResolveFile.class);
        Location testLocation = new Location(rsFile, 12, 12);

        when(rsFile.getModuleType()).thenReturn(ModuleType.THEORY);

        when(testWarning.isType(WarningType.GENERIC_WARNING)).thenReturn(true);
        when(testWarning.getMessage()).thenReturn("test single error");
        when(testWarning.getLocation()).thenReturn(testLocation);

        testHandler.registerWarning(testWarning);
        List<Warning> warnings = testHandler.getWarnings();

        assert (warnings.size() == 1);
        assert (warnings.get(0).isType(WarningType.GENERIC_WARNING));
        assert (warnings.get(0).getMessage().equals("test single error"));
        assert (warnings.get(0).getLocation() == testLocation);
    }
}
