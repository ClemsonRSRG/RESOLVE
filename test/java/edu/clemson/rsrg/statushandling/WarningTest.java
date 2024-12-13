package edu.clemson.rsrg.statushandling;

import edu.clemson.rsrg.init.file.ModuleType;
import edu.clemson.rsrg.init.file.ResolveFile;
import edu.clemson.rsrg.parsing.data.Location;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WarningTest {
    @Test
    public void testWarningIsType_givenGenericType_returnTrue() {
        Warning warning = new Warning(WarningType.GENERIC_WARNING, "");

        assert(warning.isType(WarningType.GENERIC_WARNING));
    }

    @Test
    public void testWarningIsType_givenIncorrectParameterModeUsage_returnTrue() {
        Warning warning = new Warning(WarningType.INCORRECT_PARAMETER_MODE_USAGE, "");

        assert(warning.isType(WarningType.INCORRECT_PARAMETER_MODE_USAGE));
    }

    @Test
    public void testWarningGetMessage_givenGenericString_returnsGenericString() {
        Warning warning = new Warning(WarningType.GENERIC_WARNING, "abcd");

        assert(warning.getMessage().equals("abcd"));
    }

    @Test
    public void testWarningGetLocation_givenGenericLocation_returnsGenericLocation() {
        ResolveFile rsFile = mock(ResolveFile.class);
        when(rsFile.getModuleType()).thenReturn(ModuleType.THEORY);

        Location warningLocation = new Location(rsFile, 12, 12);

        Warning warning = new Warning(WarningType.GENERIC_WARNING, warningLocation, "abcd");

        assert(warning.getLocation() == warningLocation);
    }

    @Test
    public void testWarningConstructor_givenGenericTypeAndGenericString_assertCorrectValues() {
        Warning warning = new Warning(WarningType.GENERIC_WARNING, "generic_string");

        assert(warning.getMessage().equals("generic_string"));
        assert(warning.isType(WarningType.GENERIC_WARNING));
    }
}
