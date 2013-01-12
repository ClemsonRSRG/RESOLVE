package edu.clemson.cs.r2jt.typeandpopulate;

@SuppressWarnings("serial")
public class NoSuchModuleException extends SymbolTableException {

    public final ModuleIdentifier sourceModule;
    public final ModuleIdentifier requestedModule;

    public NoSuchModuleException(ModuleIdentifier source,
            ModuleIdentifier requested) {

        sourceModule = source;
        requestedModule = requested;
    }
}
