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
        Warning warning = new Warning(WarningType.GENERIC_WARNING, warningLocation, "test single error");

        testHandler.registerWarning(warning);

        assert (testHandler.retrieveWarningCount() == 1);
    }

    @Test
    public void testRetrieveWarningCount_givenMultipleWarnings_returns5() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));

        ResolveFile rsFile = mock(ResolveFile.class);
        when(rsFile.getModuleType()).thenReturn(ModuleType.THEORY);

        Location warningLocation = new Location(rsFile, 12, 12);
        Warning warning = new Warning(WarningType.GENERIC_WARNING, warningLocation, "test single error");

        for (int i = 0;i < 5;i ++) {
            testHandler.registerWarning(warning);
        }
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

    @Test
    public void testGetAndRegisterWarnings_give5Warnings_assertExpectedValues() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));

        ResolveFile mockedFile = mock(ResolveFile.class);
        Location testLocationA = new Location(mockedFile, 1, 1);
        Location testLocationB = new Location(mockedFile, 2, 2);

        Warning warningA = mock(Warning.class);
        when(warningA.isType(WarningType.GENERIC_WARNING)).thenReturn(true);
        when(warningA.getMessage()).thenReturn("test generic warning");
        when(warningA.getLocation()).thenReturn(testLocationA);

        Warning warningB = mock(Warning.class);
        when(warningB.isType(WarningType.INCORRECT_PARAMETER_MODE_USAGE)).thenReturn(true);
        when(warningB.getMessage()).thenReturn("test incorrect parameter mode warning");
        when(warningB.getLocation()).thenReturn(testLocationB);

        for (int i = 0;i < 4;i ++) {
            testHandler.registerWarning(warningA);
        }
        testHandler.registerWarning(warningB);

        List<Warning> warnings = testHandler.getWarnings();

        assert (testHandler.getWarnings().size() == 5);

        for (int i = 0;i < 4; i ++) {
            assert (warnings.get(i).isType(WarningType.GENERIC_WARNING));
            assert (warnings.get(i).getMessage().equals("test generic warning"));
            assert (warnings.get(i).getLocation() == testLocationA);
        }

        assert (warnings.get(4).isType(WarningType.INCORRECT_PARAMETER_MODE_USAGE));
        assert (warnings.get(4).getMessage().equals("test incorrect parameter mode warning"));
        assert (warnings.get(4).getLocation() == testLocationB);
    }

    @Test
    public void testStreamAllWarnings_givenNoWarnings_expectNothingToBeStreamed() {
        PrintWriter mockWriter = mock(PrintWriter.class);
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), mockWriter);

        testHandler.streamAllWarnings();

        verify(mockWriter, never()).write(anyString());
        verify(mockWriter, never()).flush();
    }

    @Test
    public void testStreamAllWarnings_givenWarnings_expectWarningsToBeStreamed() {
        PrintWriter mockWriter = mock(PrintWriter.class);
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), mockWriter);

        ResolveFile mockedFile = mock(ResolveFile.class);
        when(mockedFile.getModuleType()).thenReturn(ModuleType.THEORY);
        Location testLocationA = new Location(mockedFile, 1, 1);
        testHandler.registerWarning(new Warning(WarningType.GENERIC_WARNING, testLocationA, ""));

        testHandler.streamAllWarnings();

        verify(mockWriter, times(1)).write(anyString());
        verify(mockWriter, times(1)).flush();
    }
}
