package edu.clemson.cs.r2jt.absynnew.decl;

/**
 * <p>A <code>ModuleParameterDecl</code> can be anything ranging from a
 * generic type, to an operation, constant, or definition. This class simply
 * wraps the specific, base <code>DeclAST</code> representation
 * of each.</p>
 */
public class ModuleParameterAST extends DeclAST {

    private final DeclAST myPayload;

    public <T extends DeclAST> ModuleParameterAST(T payload) {
        super(payload.getStart(), payload.getStop(), payload.getName());
        myPayload = payload;
    }

    public DeclAST getPayload() {
        return myPayload;
    }
}
