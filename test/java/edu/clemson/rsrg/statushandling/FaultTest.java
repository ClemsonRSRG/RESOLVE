/*
 * FaultTest.java
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FaultTest {
    @org.junit.Test
    public void testFaultIsType_givenGenericType_returnTrue() {
        ResolveFile rsFile = mock(ResolveFile.class);
        when(rsFile.getModuleType()).thenReturn(ModuleType.THEORY);

        Location warningLocation = new Location(rsFile, 12, 12);
        Fault fault = new Fault(FaultType.GENERIC_FAULT, warningLocation, "", false);

        assert (fault.isType(FaultType.GENERIC_FAULT));
    }

    @org.junit.Test
    public void testFaultIsType_givenIncorrectParameterModeUsage_returnTrue() {
        ResolveFile rsFile = mock(ResolveFile.class);
        when(rsFile.getModuleType()).thenReturn(ModuleType.THEORY);

        Location warningLocation = new Location(rsFile, 12, 12);
        Fault fault = new Fault(FaultType.INCORRECT_PARAMETER_MODE_USAGE, warningLocation, "", false);

        assert (fault.isType(FaultType.INCORRECT_PARAMETER_MODE_USAGE));
    }

    @org.junit.Test
    public void testFaultGetMessage_givenGenericString_returnsGenericString() {
        ResolveFile rsFile = mock(ResolveFile.class);
        when(rsFile.getModuleType()).thenReturn(ModuleType.THEORY);

        Location warningLocation = new Location(rsFile, 12, 12);
        Fault fault = new Fault(FaultType.GENERIC_FAULT, warningLocation, "abcd", false);

        assert (fault.getMessage().equals("abcd"));
    }

    @org.junit.Test
    public void testFaultGetLocation_givenGenericLocation_returnsGenericLocation() {
        ResolveFile rsFile = mock(ResolveFile.class);
        when(rsFile.getModuleType()).thenReturn(ModuleType.THEORY);

        Location warningLocation = new Location(rsFile, 12, 12);

        Fault fault = new Fault(FaultType.GENERIC_FAULT, warningLocation, "abcd", false);

        assert (fault.getLocation() == warningLocation);
    }

    @org.junit.Test
    public void testFaultConstructor_givenGenericTypeAndGenericString_assertCorrectValues() {
        ResolveFile rsFile = mock(ResolveFile.class);
        when(rsFile.getModuleType()).thenReturn(ModuleType.THEORY);

        Location warningLocation = new Location(rsFile, 12, 12);
        Fault fault = new Fault(FaultType.GENERIC_FAULT, warningLocation, "generic_string", false);

        assert (fault.getMessage().equals("generic_string"));
        assert (fault.isType(FaultType.GENERIC_FAULT));
    }

    @org.junit.Test
    public void testDisplayString_givenFault_expectExactResult() {
        Location testLoc = mock(Location.class);
        when(testLoc.toString()).thenReturn("LOCATION_SECTION");

        Fault fault = new Fault(FaultType.GENERIC_FAULT, testLoc, "Basic Warning Message", false);

        StringBuilder sb = new StringBuilder();
        sb.append("\nFault: ");
        sb.append(FaultType.GENERIC_FAULT.toString());
        sb.append("\nat ");
        sb.append("LOCATION_SECTION");
        sb.append("\n");
        sb.append("Basic Warning Message");
        sb.append("\n");

        assert (fault.toString().contentEquals(sb));
    }

    @Test
    public void testIsCritical_notCritical_returnsFalse() {
        Location testLoc = mock(Location.class);
        when(testLoc.toString()).thenReturn("LOCATION_SECTION");
        Fault fault = new Fault(FaultType.GENERIC_FAULT, testLoc, "Basic Warning Message", false);
        assert !fault.isCritical();
    }

    @Test
    public void testIsCritical_critical_returnsTrue() {
        Location testLoc = mock(Location.class);
        when(testLoc.toString()).thenReturn("LOCATION_SECTION");
        Fault fault = new Fault(FaultType.GENERIC_FAULT, testLoc, "Basic Warning Message", true);
        assert fault.isCritical();
    }

    @org.junit.Test
    public void testDisplayString_givenCriticalFault_expectExactResult() {
        Location testLoc = mock(Location.class);
        when(testLoc.toString()).thenReturn("LOCATION_SECTION");

        Fault fault = new Fault(FaultType.GENERIC_FAULT, testLoc, "Basic Warning Message", true);

        StringBuilder sb = new StringBuilder();
        sb.append("\nCRITICAL Fault: ");
        sb.append(FaultType.GENERIC_FAULT.toString());
        sb.append("\nat ");
        sb.append("LOCATION_SECTION");
        sb.append("\n");
        sb.append("Basic Warning Message");
        sb.append("\n");

        assert (fault.toString().contentEquals(sb));
    }

}
