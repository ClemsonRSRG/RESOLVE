package edu.clemson.cs.r2jt.processing;

// Libraries
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Map;

/**
 * TODO: Write a description of this module
 */
public class Utilities {

    // ===========================================================
    // Global Variables
    // ===========================================================

    /**
     * <p>Map of all local operations and their associated
     * parameter variables.</p>
     */
    private Map<String, List<ParameterVarDec>> myLocalOperMap;

    /**
     * <p>List of statements to be added to the
     * <code>ModuleDec</code>.</p>
     */
    private List<Statement> myStatementList;

    // ===========================================================
    // Constructors
    // ===========================================================

    public Utilities() {
        myLocalOperMap = new Map<String, List<ParameterVarDec>>();
        myStatementList = new List<Statement>();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Error Handling
    // -----------------------------------------------------------

    // -----------------------------------------------------------
    // Methods to handle local operations
    // -----------------------------------------------------------

    /**
     * <p>Checks our map of operations to see if an operation with
     * string name passed is a local operation.</p>
     *
     * @param opName The string containing the name of the operation.
     *
     * @return true/false depending if it is in our map or not
     */
    public boolean isLocalOper(String opName) {
        return myLocalOperMap.containsKey(opName);
    }

    /**
     * <p>Keeps a map reference to all local operations, by iterating
     * through list of <code>Dec</code> looking for any
     * <code>ProcedureDec</code> or <code>FacilityOperationDec</code>.</p>
     *
     * @param decList The list containing all the <code>Dec</code>.
     */
    public void retrieveLocalProc(List<Dec> decList) {
        Iterator<Dec> it = decList.iterator();
        while (it.hasNext()) {
            // Temporary holder for the current Dec
            Dec current = it.next();

            // Check if it is a ProcedureDec or not
            if (current instanceof ProcedureDec) {
                // Type cast to ProcedureDec
                ProcedureDec currentProcDec = (ProcedureDec) current;

                // Update the map
                myLocalOperMap.put(currentProcDec.getName().getName(),
                        currentProcDec.getParameters());
            }
            // Check if it is a FacilityOperationDec or not
            else if (current instanceof FacilityOperationDec) {
                // Type cast to FacilityOperationDec
                FacilityOperationDec currentProcDec =
                        (FacilityOperationDec) current;

                // Update the map
                myLocalOperMap.put(currentProcDec.getName().getName(),
                        currentProcDec.getParameters());
            }
        }
    }

    /**
     * <p>Returns list of parameters associated with the local
     * operation in the operation map.</p>
     *
     * @param opName <code>PosSymbol</code> of the operation
     *
     * @return A list of <code>ParameterVarDec</code>
     */
    public List<ParameterVarDec> retrieveParameterList(String opName) {
        return myLocalOperMap.get(opName);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================
}
