package edu.clemson.cs.r2jt.typeandpopulate2.entry;

import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import edu.clemson.cs.r2jt.misc.SrcErrorException;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import org.antlr.v4.runtime.Token;

public abstract class AbstractProgramEntry extends SymbolTableEntry {

    public AbstractProgramEntry(String name, ResolveAST definingElement,
                                ModuleIdentifier sourceModule) {
        super(name, definingElement, sourceModule);
    }

    public abstract PTType getProgramType();


}
