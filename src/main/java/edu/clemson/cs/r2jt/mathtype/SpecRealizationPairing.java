package edu.clemson.cs.r2jt.mathtype;

public class SpecRealizationPairing {

    private final ModuleParameterization mySpec;
    private final ModuleParameterization myRealization;

    public SpecRealizationPairing(ModuleParameterization spec) {
        this(spec, null);
    }

    public SpecRealizationPairing(ModuleParameterization spec,
            ModuleParameterization realization) {

        if (spec == null) {
            throw new IllegalArgumentException("Null spec!");
        }

        mySpec = spec;
        myRealization = realization;
    }

    public ModuleParameterization getSpecification() {
        return mySpec;
    }

    public ModuleParameterization getRealization() throws NoneProvidedException {

        if (myRealization == null) {
            throw new NoneProvidedException();
        }

        return myRealization;
    }
}
