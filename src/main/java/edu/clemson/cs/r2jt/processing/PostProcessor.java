package edu.clemson.cs.r2jt.processing;

// Libraries
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.Mode;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;

/**
 * TODO: Write a description of this module
 */
public class PostProcessor extends TreeWalkerStackVisitor {

    // ===========================================================
    // Global Variables
    // ===========================================================

    /**
     * <p>Utilities class that contains methods that are used
     * in both pre and post Processors.</p>
     */
    private Utilities myUtilities;

    // ===========================================================
    // Constructors
    // ===========================================================

    public PostProcessor() {
        myUtilities = new Utilities();
    }

    // ===========================================================
    // TreeWalker Methods
    // ===========================================================

    // -----------------------------------------------------------
    // CallStmt
    // -----------------------------------------------------------

    @Override
    public void preCallStmt(CallStmt stmt) {
        // Variables
        Location loc = stmt.getLocation();
        PosSymbol name = stmt.getName();
        List<ProgramExp> argList = stmt.getArguments();
        boolean localOper = myUtilities.isLocalOper(name.getName());

        // Check if the called operation is a local operation
        if (localOper) {
            List<ParameterVarDec> parameterList =
                    myUtilities.retrieveParameterList(name.getName());

            // Make sure that we have the right operation
            if (parameterList.size() == argList.size()) {
                // Replaces the modified argument list
                stmt.setArguments(applyReplica(parameterList, argList));
            }
            else {
                // Found an operation with the same name,
                // but different argument size
                wrongOperation(loc, name, parameterList.size(), argList.size());
            }
        }
        else {
            /** TODO: Invoke Hampton's new symbol table to look for the
             * external operation.
             */
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Error Handling
    // -----------------------------------------------------------

    public void wrongOperation(Location location, PosSymbol name,
            int paramSize, int argSize) {
        String message =
                "Expecting an operation with the name " + name + " with "
                        + argSize + " arguments.\n"
                        + "Found an operation with same name, but with "
                        + paramSize + " arguments.";
        throw new SourceErrorException(message, location);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Applies the Replica call to any <code>VariableExp</code>
     * where the evaluates mode is specified.</p>
     *
     * @param parameterList The list of <code>ParameterVarDec</code>,
     *                      which contains the modes.
     * @param argList The list of arguments being used to invoke the
     *                current operation/procedure.
     *
     * @return The modified argument list
     */
    private List<ProgramExp> applyReplica(List<ParameterVarDec> parameterList,
            List<ProgramExp> argList) {
        List<ProgramExp> modifiedArgList = new List<ProgramExp>();

        // Loop
        Iterator<ParameterVarDec> parameterIt = parameterList.iterator();
        Iterator<ProgramExp> argListIt = argList.iterator();
        while (argListIt.hasNext() && parameterIt.hasNext()) {
            // Temp Variables
            ProgramExp tempExp = argListIt.next();
            ParameterVarDec tempParam = parameterIt.next();

            if (tempParam.getMode() == Mode.EVALUATES) {
                // Check if it is a VariableNameExp or VariableDotExp
                if (tempExp instanceof VariableNameExp
                        || tempExp instanceof VariableDotExp) {
                    // Creates a call to replica and modifies the original Exp
                    tempExp = createReplicaExp(tempExp);
                }
            }

            // Add it to the modified list
            modifiedArgList.add(tempExp);
        }

        return modifiedArgList;
    }

    /**
     * <p>Creates a <code>ProgramParamExp</code> for Replica with the
     * <code>ProgramExp</code> passed in.</p>
     *
     * @param oldExp Expression to be replicated.
     *
     * @return A call to Replica with the oldExp as its argument.
     */
    private ProgramParamExp createReplicaExp(ProgramExp oldExp) {
        // Parameter list for Replica
        List<ProgramExp> params = new List<ProgramExp>();
        params.add(oldExp);

        // Create new right hand side with the Replica operation
        return new ProgramParamExp(oldExp.getLocation(), new PosSymbol(oldExp
                .getLocation(), Symbol.symbol("Replica")), params, null);
    }
}
