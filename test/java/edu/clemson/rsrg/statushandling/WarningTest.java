package edu.clemson.rsrg.statushandling;

import org.junit.Test;

public class WarningTest {
    @Test
    public void testWarningIsType_givenGenericType_returnTrue() {
        Warning warning = new Warning(WarningType.GENERIC_WARNING);

        assert(warning.isType(WarningType.GENERIC_WARNING));
    }

    @Test
    public void testWarningIsType_givenIncorrectParameterModeUsage_returnTrue() {
        Warning warning = new Warning(WarningType.INCORRECT_PARAMETER_MODE_USAGE);

        assert(warning.isType(WarningType.INCORRECT_PARAMETER_MODE_USAGE));
    }

    @Test
    public void testWarningGetMessage_givenGenericString_returnsGenericString() {
        Warning warning = new Warning(WarningType.GENERIC_WARNING, "abcd");

        assert(warning.getMessage().equals("abcd"));
    }
}
