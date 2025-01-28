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
import edu.clemson.rsrg.parsing.data.Location;
import org.junit.Test;

import java.io.PrintWriter;
import java.util.List;

import static org.mockito.Mockito.*;

public class WriterStatusHandlerTest {
    @Test
    public void testRetrieveFaultCount_givenNoFaults_returnZero() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));
        assert (testHandler.retrieveFaultCount() == 0);
    }

    @Test
    public void testRetrieveFaultCount_givenSingleFault_returnOne() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));

        ResolveFile rsFile = mock(ResolveFile.class);
        when(rsFile.getModuleType()).thenReturn(ModuleType.THEORY);

        Location warningLocation = new Location(rsFile, 12, 12);
        Fault fault = new Fault(FaultType.GENERIC_FAULT, warningLocation, "test single error", false);

        testHandler.registerFault(fault);

        assert (testHandler.retrieveFaultCount() == 1);
    }

    @Test
    public void testRetrieveFaultCount_givenMultipleFaults_returns5() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));

        ResolveFile rsFile = mock(ResolveFile.class);
        when(rsFile.getModuleType()).thenReturn(ModuleType.THEORY);

        Location warningLocation = new Location(rsFile, 12, 12);
        Fault fault = new Fault(FaultType.GENERIC_FAULT, warningLocation, "test single error", false);

        for (int i = 0; i < 5; i++) {
            testHandler.registerFault(fault);
        }
        assert (testHandler.retrieveFaultCount() == 5);
    }

    @Test
    public void testGetFaults_expectEmptyList_returnEmptyList() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));

        List<Fault> faults = testHandler.getFaults();
        assert (faults.isEmpty());
    }

    @Test
    public void testGetFaults_expectSingletonList_returnSingletonList() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));

        Fault testFault = mock(Fault.class);
        ResolveFile rsFile = mock(ResolveFile.class);
        Location testLocation = new Location(rsFile, 12, 12);

        when(rsFile.getModuleType()).thenReturn(ModuleType.THEORY);

        when(testFault.isType(FaultType.GENERIC_FAULT)).thenReturn(true);
        when(testFault.getMessage()).thenReturn("test single error");
        when(testFault.getLocation()).thenReturn(testLocation);

        testHandler.registerFault(testFault);
        List<Fault> faults = testHandler.getFaults();

        assert (faults.size() == 1);
        assert (faults.get(0).isType(FaultType.GENERIC_FAULT));
        assert (faults.get(0).getMessage().equals("test single error"));
        assert (faults.get(0).getLocation() == testLocation);
    }

    @Test
    public void testGetAndRegisterFaults_give5Fault_assertExpectedValues() {
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), new PrintWriter(System.err));

        ResolveFile mockedFile = mock(ResolveFile.class);
        Location testLocationA = new Location(mockedFile, 1, 1);
        Location testLocationB = new Location(mockedFile, 2, 2);

        Fault faultA = mock(Fault.class);
        when(faultA.isType(FaultType.GENERIC_FAULT)).thenReturn(true);
        when(faultA.getMessage()).thenReturn("test generic warning");
        when(faultA.getLocation()).thenReturn(testLocationA);

        Fault faultB = mock(Fault.class);
        when(faultB.isType(FaultType.INCORRECT_PARAMETER_MODE_USAGE)).thenReturn(true);
        when(faultB.getMessage()).thenReturn("test incorrect parameter mode warning");
        when(faultB.getLocation()).thenReturn(testLocationB);

        for (int i = 0; i < 4; i++) {
            testHandler.registerFault(faultA);
        }
        testHandler.registerFault(faultB);

        List<Fault> faults = testHandler.getFaults();

        assert (testHandler.getFaults().size() == 5);

        for (int i = 0; i < 4; i++) {
            assert (faults.get(i).isType(FaultType.GENERIC_FAULT));
            assert (faults.get(i).getMessage().equals("test generic warning"));
            assert (faults.get(i).getLocation() == testLocationA);
        }

        assert (faults.get(4).isType(FaultType.INCORRECT_PARAMETER_MODE_USAGE));
        assert (faults.get(4).getMessage().equals("test incorrect parameter mode warning"));
        assert (faults.get(4).getLocation() == testLocationB);
    }

    @Test
    public void testStreamAllFaults_givenNoFaults_expectNothingToBeStreamed() {
        PrintWriter mockWriter = mock(PrintWriter.class);
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), mockWriter);

        testHandler.streamAllFaults();

        verify(mockWriter, never()).write(anyString());
        verify(mockWriter, never()).flush();
    }

    @Test
    public void testStreamAllFaults_givenFaults_expectFaultsToBeStreamed() {
        PrintWriter mockWriter = mock(PrintWriter.class);
        StatusHandler testHandler = new WriterStatusHandler(new PrintWriter(System.out), mockWriter);

        ResolveFile mockedFile = mock(ResolveFile.class);
        when(mockedFile.getModuleType()).thenReturn(ModuleType.THEORY);
        Location testLocationA = new Location(mockedFile, 1, 1);
        testHandler.registerFault(new Fault(FaultType.GENERIC_FAULT, testLocationA, "", false));

        testHandler.streamAllFaults();

        verify(mockWriter, times(1)).write(anyString());
        verify(mockWriter, times(1)).flush();
    }
}
