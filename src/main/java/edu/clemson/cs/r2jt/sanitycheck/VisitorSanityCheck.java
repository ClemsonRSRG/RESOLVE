package edu.clemson.cs.r2jt.sanitycheck;

import java.util.Collection;

import edu.clemson.cs.r2jt.absyn.AffectsItem;
import edu.clemson.cs.r2jt.absyn.CallStmt;
import edu.clemson.cs.r2jt.absyn.ConceptBodyModuleDec;
import edu.clemson.cs.r2jt.absyn.ConceptModuleDec;
import edu.clemson.cs.r2jt.absyn.Dec;
import edu.clemson.cs.r2jt.absyn.EnhancementBodyItem;
import edu.clemson.cs.r2jt.absyn.EnhancementBodyModuleDec;
import edu.clemson.cs.r2jt.absyn.EnhancementModuleDec;
import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.FacilityDec;
import edu.clemson.cs.r2jt.absyn.FacilityModuleDec;
import edu.clemson.cs.r2jt.absyn.FacilityOperationDec;
import edu.clemson.cs.r2jt.absyn.FuncAssignStmt;
import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.absyn.OperationDec;
import edu.clemson.cs.r2jt.absyn.ParameterVarDec;
import edu.clemson.cs.r2jt.absyn.ProcedureDec;
import edu.clemson.cs.r2jt.absyn.ProgramExp;
import edu.clemson.cs.r2jt.absyn.ProgramParamExp;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.absyn.SwapStmt;
import edu.clemson.cs.r2jt.absyn.VariableArrayExp;
import edu.clemson.cs.r2jt.absyn.VariableDotExp;
import edu.clemson.cs.r2jt.absyn.VariableExp;
import edu.clemson.cs.r2jt.absyn.VariableNameExp;
import edu.clemson.cs.r2jt.absyn.WhileStmt;
import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.Mode;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.entry.OperationEntry;
import edu.clemson.cs.r2jt.entry.VarEntry;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.init.ModuleRecord;
import edu.clemson.cs.r2jt.scope.ModuleScope;
import edu.clemson.cs.r2jt.scope.OldSymbolTable;
import edu.clemson.cs.r2jt.treewalk.TreeWalker;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;

public class VisitorSanityCheck extends TreeWalkerStackVisitor {

    /* Private Variables */

    private ErrorHandler err;

    private CompileEnvironment myCompileEnvironment;

    List<OldSymbolTable> mySTList = null;

    // HwS - Filthy hack. These global variables keeps a pointer to the symbol
    // table of the concept associated with a realization that is currently
    // being parsed. The name associated with the concept is also stored so
    // that the table can be retrieved lazily.
    private OldSymbolTable myAssociatedConceptSymbolTable;
    private ConceptBodyModuleDec myCurrentConceptBodyModuleDec = null;

    // HwS - When we encounter a while statement, we pass execution to each of
    // its statements in turn, which then call US back depending on what kind of
    // statement they are. This means we forget that we're in a while statement
    // and there's no way to pass that info in. As a result, information about
    // a while statement is stored here whenever we're inside one. It's null
    // when we are not.
    private WhileStmt myCurWhileStatement = null;
    private ProcedureDec myCurProcedureDec = null;
    private List<String> myEncounteredProcedures;

    /* Constructors */

    public VisitorSanityCheck(CompileEnvironment env) {
        myCompileEnvironment = env;
        mySTList = myCompileEnvironment.getSymbolTables();
        myEncounteredProcedures = new List<String>();
        this.err = env.getErrorHandler();
    }

    /* pre-post Any Methods */

    @Override
    public void preAnyStack(ResolveConceptualElement data) {
        //Ensure this is the first element of the dec
        if (this.getParent() == null) {
            //			 System.out.println("Resetting encounteredProcedures");
            myCompileEnvironment.encounteredProcedures =
                    new List<ProcedureDec>();
        }
    }

    @Override
    public void postAnyStack(ResolveConceptualElement data) {

    // fails if an operation from a facility with the same name is used

    //Ensure this is the final element of the dec
    /*if(this.getParent() == null) {
    //			 System.out.println("ProcedureDecs: " + myCompileEnvironment.encounteredProcedures.toString());
    	 
    	 Iterator<ProcedureDec> i = myCompileEnvironment.encounteredProcedures.iterator();
    	 while (i.hasNext()) {
    		 ProcedureDec dec = i.next();
    		 VisitorRecursiveCheck vrc = new VisitorRecursiveCheck(dec, myCompileEnvironment);
    		 TreeWalker tw = new TreeWalker(vrc);
    		 tw.visit(dec);
    //				 System.out.println(dec.getName().getName() + " isRecursive: " + vrc.isRecursive() +"\n\n");
    //				 if(dec.getRecursive() != vrc.isRecursive()) {
    			 //Incorrectly labeled as recursive/not recursive
    		 if(!dec.getRecursive() && vrc.isRecursive()) {
    			 err.error(dec.getName().getLocation(), "Cannot implement procedure " + dec.getName().getName() + " as recursive: " + vrc.isRecursive() + ", when it is declared as recursive: " + dec.getRecursive() + ".");
    		 }
    	}
    	 myCompileEnvironment.encounteredProcedures = null;
    }*/
    }

    /* pre-post Dec Methods */

    @Override
    public void preConceptModuleDec(ConceptModuleDec dec) {}

    @Override
    public void postConceptModuleDec(ConceptModuleDec dec) {}

    @Override
    public void preFacilityOperationDec(FacilityOperationDec dec) {
        List<ParameterVarDec> parameters = dec.getParameters();
        List<AffectsItem> affectedItems = dec.getStateVars();
        sanityCheckRequires(dec.getRequires(), parameters, affectedItems);
        sanityCheckEnsures(dec.getEnsures(), parameters, affectedItems);
    }

    @Override
    public void preOperationDec(OperationDec dec) {
        List<ParameterVarDec> parameters = dec.getParameters();
        List<AffectsItem> affectedItems = dec.getStateVars();
        sanityCheckRequires(dec.getRequires(), parameters, affectedItems);
        sanityCheckEnsures(dec.getEnsures(), parameters, affectedItems);
    }

    // -----------------------------------------------------------
    // FacilityDec
    // -----------------------------------------------------------

    @Override
    public void postFacilityDec(FacilityDec dec) {
        PosSymbol realizProfileName = dec.getProfileName();
        PosSymbol conSym = dec.getConceptName();
        if (realizProfileName != null) {
            PosSymbol realizSym = dec.getBodyName();
            ModuleID realizID = ModuleID.createConceptBodyID(realizSym, conSym);
            ConceptBodyModuleDec bodyDec =
                    (ConceptBodyModuleDec) myCompileEnvironment
                            .getModuleDec(realizID);
            sanityCheckPerformanceProfiles(realizProfileName, bodyDec);
        }
        Iterator<EnhancementBodyItem> it =
                dec.getEnhancementBodies().iterator();
        while (it.hasNext()) {
            EnhancementBodyItem itemDec = it.next();
            PosSymbol itemProfileName = itemDec.getProfileName();
            if (itemProfileName != null) {
                PosSymbol enhSym = itemDec.getName();
                PosSymbol enhRealizSym = itemDec.getBodyName();
                ModuleID realizID =
                        ModuleID.createEnhancementBodyID(enhRealizSym, enhSym,
                                conSym);
                EnhancementBodyModuleDec bodyDec =
                        (EnhancementBodyModuleDec) myCompileEnvironment
                                .getModuleDec(realizID);
                sanityCheckPerformanceProfiles(itemProfileName, bodyDec);
            }
        }
    }

    @Override
    public void preConceptBodyModuleDec(ConceptBodyModuleDec dec) {
        ModuleID id = ModuleID.createConceptID(dec.getConceptName());
        myAssociatedConceptSymbolTable =
                myCompileEnvironment.getSymbolTable(id);
        myCurrentConceptBodyModuleDec = dec;

        // Check to make sure realization implements the necessary procedures
        sanityCheckImplementedProcedures(dec.getName().getName(), dec
                .getLocalProcedureNames(), myAssociatedConceptSymbolTable);
    }

    @Override
    public void postConceptBodyModuleDec(ConceptBodyModuleDec dec) {
        myCurrentConceptBodyModuleDec = null;
    }

    @Override
    public void preEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {
        ModuleID id =
                ModuleID.createEnhancementID(dec.getEnhancementName(), dec
                        .getConceptName());
        myAssociatedConceptSymbolTable =
                myCompileEnvironment.getSymbolTable(id);

        // Check to make sure realization implements the necessary procedures
        sanityCheckImplementedProcedures(dec.getName().getName(), dec
                .getLocalProcedureNames(), myAssociatedConceptSymbolTable);
    }

    @Override
    public void preFacilityModuleDec(FacilityModuleDec dec) {
    //ModuleID id = ModuleID.createFacilityID(dec.getName());
    //myAssociatedConceptSymbolTable = myCompileEnvironment.getSymbolTable(id);
    //System.out.println(myAssociatedConceptSymbolTable);
    //myCurrentConceptBodyModuleDec = dec;
    }

    @Override
    public void preProcedureDec(ProcedureDec dec) {

        myCompileEnvironment.encounteredProcedures.add(dec);

        // Get the name of the procedure as a Symbol
        Symbol procedureSymbol = dec.getName().getSymbol();

        myCurProcedureDec = dec;
        myEncounteredProcedures.add(procedureSymbol.getName());

        //TODO: check this works
        OperationDec associatedOperationDec = null;
        ModuleID conceptModuleId = myAssociatedConceptSymbolTable.getModuleID();
        Dec conceptDec = myCompileEnvironment.getModuleDec(conceptModuleId);
        if (conceptDec instanceof EnhancementModuleDec
                || conceptDec instanceof ConceptModuleDec) {
            List<Dec> decs;
            if (conceptDec instanceof EnhancementModuleDec) {
                decs = ((EnhancementModuleDec) conceptDec).getDecs();
            }
            else {
                decs = ((ConceptModuleDec) conceptDec).getDecs();
            }
            Iterator<Dec> i = decs.iterator();
            while (i.hasNext()) {
                Dec x = i.next();
                if (x instanceof OperationDec) {
                    if (x.getName().getName().equals(dec.getName().getName())) {
                        associatedOperationDec = (OperationDec) x;
                        break;
                    }
                }
            }
        }
        if (associatedOperationDec != null
                && associatedOperationDec.getEnsures() != null) {
            //Check to make sure procedure has a decreasing if it is recursive
            if (dec.getRecursive() && dec.getDecreasing() == null) {
                err
                        .error(
                                dec.getName().getLocation(),
                                "Cannot leave out the Decreasing clause of a recursive procedure when specified with an Ensures clause.");
            }
        }

        try {

            // Get the operation of the same name from the concept
            OperationEntry operation = getConceptOperation(procedureSymbol);

            // Make sure they have the same number and types of arguments
            if (operation != null) {
                // Check the parameters for correct number
                boolean correctNumberOfParameters;

                // Get operation parameters
                Iterator<VarEntry> opParam = operation.getParameters();

                // Determine size of opParam
                int sizeOfOpParam = 0;
                while (opParam.hasNext()) {
                    opParam.next();
                    sizeOfOpParam++;
                }

                // Get procedure parameters
                List<ParameterVarDec> procParam = dec.getParameters();

                // Get the mode for each operation parameter
                List<Mode> operationParameterModes =
                        getParameterVarEntryModes(operation.getParameters());

                // Get the mode for each procedure parameter
                List<Mode> procedureParameterModes =
                        getParameterVarDecModes(dec.getParameters());

                // Make sure that the number of arguments is the same
                correctNumberOfParameters = sizeOfOpParam == procParam.size();

                if (correctNumberOfParameters) {
                    sanityCheckProcedureParameterModes(operationParameterModes,
                            procedureParameterModes, operation, dec);
                }
                else {
                    // Parameter count mismatch
                    String iName =
                            myAssociatedConceptSymbolTable.getModuleID()
                                    .getName().toString();
                    err
                            .error(
                                    dec.getName().getLocation(),
                                    operation.getName().getLocation(),
                                    procedureParameterCountDoesNotMatchOperation(iName));
                }
            }
        }
        catch (SanityCheckException e) {
            // We couldn't find an operation corresponding to the procedure
            // under consideration
            err.error(dec.getName().getLocation(), e.getMessage());
        }

    }

    @Override
    public void postProcedureDec(ProcedureDec dec) {
        myCurProcedureDec = null;
    }

    /* pre-post Stmt Methods */

    @Override
    public void preWhileStmt(WhileStmt stmt) {
        if (stmt.getChanging() == null) {
            //System.out.println("NULL CHANGING");
            //			stmt.setChanging(myCurProcedureDec.getAllVariables());
        }
        myCurWhileStatement = stmt;
    }

    @Override
    public void postWhileStmt(WhileStmt stmt) {
        myCurWhileStatement = null;
    }

    @Override
    public void preFuncAssignStmt(FuncAssignStmt stmt) {
        // When in a while statement check that the FuncAssignStmt isnt changing
        // non-indicated variables
        if (myCurWhileStatement != null) {
            try {
                sanityCheckChanging(stmt.getVar());
            }
            catch (ClassCastException e) {
                System.out.println("FIXME: Assigning to non-variable?");
                System.out.println("       Analyzer.visitFuncAssignStmt");
            }
        }
    }

    @Override
    public void preSwapStmt(SwapStmt stmt) {
        sanityCheckSwapArguments(stmt);
        // When in a while statment check that the SwapStmt isnt changing
        // non-indicated variables
        if (myCurWhileStatement != null) {

            try {
                sanityCheckChanging(stmt.getLeft());
                sanityCheckChanging(stmt.getRight());
            }
            catch (ClassCastException e) {
                // XXX : is it possible to swap non-named things???
                // XXX : It appears this is the case -- need to work out
                // other sorts of things
            }
        }
    }

    @Override
    public void preCallStmt(CallStmt stmt) {

        //sanityCheckPrimaryOperation(stmt.getName().getSymbol(), stmt.getName()
        //.getLocation());
        sanityCheckParamModeChanging(stmt.getName().getSymbol(), stmt
                .getArguments());

    }

    /* pre-post Exp Methods */

    @Override
    public void preProgramParamExp(ProgramParamExp exp) {
        sanityCheckParamModeChanging(exp.getName().getSymbol(), exp
                .getArguments());
        //sanityCheckPrimaryOperation(exp.getName().getSymbol(), exp
        //.getLocation());
    }

    /* SanityCheck Functions */

    /**
     * This function is called in <code>preProgramParamExp()</code> and
     * <code>preCallStmt()</code>. It compares the called function's parameter's
     * modes with <code>myCurWhileStmt<code>'s changing clause. If a function uses
     * a parameter mode other than <code>Preserves</code> or <code>Restores</code> and the passed
     * variable does not appear in the changing clause, an error is thrown.
     * 
     * @param symb
     *            the symbol of the stmt/exp which is being checked
     * @param args
     *            the iterator for the stmt/exp's arg list
     */
    private void sanityCheckParamModeChanging(Symbol symb, List<ProgramExp> args) {
        // Compare specification parameter modes for Changing clause violations
        if (myCurWhileStatement != null) {
            boolean foundSym = false;
            Iterator<OldSymbolTable> it = mySTList.iterator();
            OldSymbolTable currST = null;
            OperationEntry operation = null;
            // First we look through the symbol table for the proper operation
            while (it.hasNext()) {
                currST = it.next();
                ModuleScope scope = currST.getModuleScope();
                Symbol sym = symb;
                if (scope.containsOperation(sym)) {
                    operation = currST.getModuleScope().getOperation(symb);
                    if (operation.getParamNum() == args.size()) {
                        /*Iterator<VarEntry> i = operation.getParameters();
                        Iterator<ProgramExp> j = args.iterator();
                        while (i.hasNext() && j.hasNext()) {
                        	VarEntry tempEntry = i.next();
                        	ProgramExp targetExp = j.next();
                        	//if(targetExp.getType().)
                        }*/
                        foundSym = true;
                        break;
                    }
                }
            }
            if (foundSym) {

                Iterator<VarEntry> i = operation.getParameters();
                Iterator<ProgramExp> j = args.iterator();
                while (i.hasNext() && j.hasNext()) {
                    ProgramExp tempExp = j.next();
                    Mode mode = i.next().getMode();
                    if (tempExp instanceof VariableExp) {
                        // Only if the arg will NOT be preserved, restored, or
                        // evaluated should we check for changing
                        if (!Mode.equals(mode, Mode.PRESERVES)
                                && !Mode.equals(mode, Mode.RESTORES)
                                && !Mode.equals(mode, Mode.EVALUATES)) {
                            sanityCheckChanging((VariableExp) tempExp);
                        }
                    }
                }
            }

            /*ModuleScope scope = myAssociatedConceptSymbolTable.getModuleScope();
            Symbol sym = symb;
            if (scope.containsOperation(sym)) {
            	OperationEntry operation = scope.getOperation(sym);
            	Iterator<VarEntry> i = operation.getParameters();
            	Iterator<ProgramExp> j = args;
            	while (i.hasNext() && j.hasNext()) {
            		ProgramExp tempExp = j.next();
            		Mode mode = i.next().getMode();
            		// Only if the arg will NOT be preserved, restored, or
            		// evaluated should we check for changing
            		if (!Mode.equals(mode, Mode.PRESERVES)
            				&& !Mode.equals(mode, Mode.RESTORES)
            				&& !Mode.equals(mode, Mode.EVALUATES)) {
            			sanityCheckChanging((VariableExp)tempExp);
            		}
            	}
            }*/
        }
    }

    /**
     * This function is called in <code>preProgramParamExp()</code> and
     * <code>preCallStmt()</code>. It checks the current stmt/exp's function's
     * name against the operation names declared in
     * <code>myAssociatedConceptSymbolTable</code>. If the current function call
     * is one of the primary operations of the associated concept then an error
     * is thrown.
     * <br><br>
     * This has the added functionality of disallowing "public" methods to call
     * themselves recursively for concept realizations, but does not hinder this
     * in enhancements.
     * 
     * @param name
     *            the name of the function being checked
     * @param loc
     *            the <code>Location</code> of the function being checked
     */
    private void sanityCheckPrimaryOperation(Symbol sym, Location loc) {
        // A check to see if this was a primary operation call in another
        // primary operation (for concept/realiz)
        // This should be hit by If and While condition statements
        if (myCurrentConceptBodyModuleDec != null) {
            List<Symbol> operationList =
                    myAssociatedConceptSymbolTable.getModuleScope()
                            .getLocalOperationNames();

            if (operationList.contains(sym)) {
                // fails if an operation from a facility with the same name is used
                //err.error(loc, illegalPrimaryOpCall(sym.getName()));
            }
        }
    }

    /**
     * The top level sanity check for 'requires' clauses. Simply evokes any
     * errors using <code>err</code>. Checks that any variables referred to in
     * the requires clause are appropriate given their parameter mode or the
     * contents of the 'affects' clause of the operation.
     * 
     * @param ensures
     *            The <code>exp</code> that represents the 'requires' clause.
     * @param parameters
     *            The list of <code>ParameterVarDec</code>s representing the
     *            formal parameters of the operation for which
     *            <code>requires</code> is provided.
     * @param affectedItems
     *            The list of <code>AffectsItem</code>s representing those
     *            global variables given in the 'affects' clause of the
     *            operation for which <code>requires</code> is provided.
     */
    private void sanityCheckRequires(Exp requires,
            List<ParameterVarDec> parameters, List<AffectsItem> affectedItems) {

        // If no requires clause was given, don't bother
        if (requires != null) {
            List<PosSymbol> initialVariables, finalVariables;
            initialVariables = new List<PosSymbol>();
            finalVariables = new List<PosSymbol>();

            // Build lists of the variables whose old and new variables are
            // referred to in the requires clause's expression.
            getContainedVariables(requires, finalVariables, initialVariables);

            // Referring to initial values in the requires clause is not
            // permitted, so output an error for every one in the list
            for (PosSymbol curVariable : initialVariables) {
                err.error(curVariable.getLocation(),
                        noOldVariablesInRequiresMessage());
            }

            // Make sure all the referrences to final values are kosher
            sanityCheckFinalRequiresVariables(finalVariables, parameters);
        }
    }

    /**
     * Sanity checks a list of variables whose new values are referenced in a
     * requires clause to make sure that they are in the appropriate mode, if
     * they refer to parameters.
     * 
     * @param initialVariables
     *            A <code>List</code> of the <code>PosSymbol</code>s whose final
     *            values are referenced in the requires clause.
     * @param parameters
     *            A <code>List</code> of the parameters to the operation
     *            corresponding to this requires clause.
     */
    private void sanityCheckFinalRequiresVariables(
            List<PosSymbol> finalVariables, List<ParameterVarDec> parameters) {

    //This logic is wrong.  In particular, just because a variable is not
    //introduced by a parameter does not mean it is global--it could have
    //been introduced by a quantifier.  See also getContainedVariables()

    /*ParameterVarDec wrkParameter;
    Mode wrkMode;
    for (PosSymbol curVariable : finalVariables) {
    	wrkParameter = getParameterByName(parameters, curVariable
    			.getSymbol());

    	if (wrkParameter == null) {
    		// If it wasn't a parameter, it must be a global. Currently,
    		// there are no restrictions on globals in this context.
    	} else {
    		wrkMode = wrkParameter.getMode();
    		if (wrkMode == Mode.REPLACES) {
    			err.error(curVariable.getLocation(),
    					invalidModeForRequires(wrkMode));
    		}
    	}
    }*/
    }

    /**
     * Sanity checks a list of variables whose old values are are referenced in
     * an ensures clause to make sure that they are in the appropriate mode, if
     * they refer to parameters, or that they are listed in the ensures clause
     * if they refer to globals.
     * 
     * @param initialVariables
     *            A <code>List</code> of the <code>PosSymbol</code>s whose
     *            initial values are referenced in the ensures clause.
     * @param parameters
     *            A <code>List</code> of the parameters to the operation
     *            corresponding to this ensures clause.
     * @param affectedItems
     *            The 'affects' claus of the operation corresponding to this
     *            ensures clause.
     */
    private void sanityCheckInitialEnsuresVariables(
            List<PosSymbol> initialVariables, List<ParameterVarDec> parameters,
            List<AffectsItem> affectedItems) {
        ParameterVarDec wrkParameter;
        Mode wrkMode;
        for (PosSymbol curVariable : initialVariables) {
            wrkParameter =
                    getParameterByName(parameters, curVariable.getSymbol());

            if (wrkParameter == null) {
                // Globals must be listed in the affects clause
                if (!affectedItemsIncludes(affectedItems, curVariable
                        .getSymbol())) {
                    err.error(curVariable.getLocation(),
                            cannotChangeValueOfUnAffectedVariableMessage());
                }
            }
            else {
                wrkMode = wrkParameter.getMode();
                if (!validInitialMode(wrkMode)) {
                    err.error(curVariable.getLocation(),
                            invalidModeForInitialEnsures(wrkMode));
                }
            }
        }
    }

    /**
     * Sanity checks a list of variables whose new values are referenced in an
     * ensures clause to make sure that they are in the appropriate mode, if
     * they refer to parameters.
     * 
     * @param initialVariables
     *            A <code>List</code> of the <code>PosSymbol</code>s whose final
     *            values are referenced in the ensures clause.
     * @param parameters
     *            A <code>List</code> of the parameters to the operation
     *            corresponding to this ensures clause.
     */
    private void sanityCheckFinalEnsuresVariables(
            List<PosSymbol> finalVariables, List<ParameterVarDec> parameters) {
        ParameterVarDec wrkParameter;
        Mode wrkMode;
        for (PosSymbol curVariable : finalVariables) {
            wrkParameter =
                    getParameterByName(parameters, curVariable.getSymbol());

            if (wrkParameter == null) {
                // If it wasn't a parameter, it must be a global. Currently,
                // there are no restrictions on globals in this context.
            }
            else {
                wrkMode = wrkParameter.getMode();
                if (!validFinalMode(wrkMode)) {
                    err.error(curVariable.getLocation(),
                            invalidModeForFinalEnsures(wrkMode));
                }
            }
        }
    }

    /**
     * The top level sanity check for 'ensures' clauses. Simply evokes any
     * errors using <code>err</code>. Checks that any variables referred to in
     * the ensures clause are appropriate given their parameter mode or the
     * contents of the 'affects' clause of the operation.
     * 
     * @param ensures
     *            The <code>exp</code> that represents the 'ensures' clause.
     * @param parameters
     *            The list of <code>ParameterVarDec</code>s representing the
     *            formal parameters of the operation for which
     *            <code>ensures</code> is provided.
     * @param affectedItems
     *            The list of <code>AffectsItem</code>s representing those
     *            global variables given in the 'affects' clause of the
     *            operation for which <code>ensures</code> is provided.
     */
    private void sanityCheckEnsures(Exp ensures,
            List<ParameterVarDec> parameters, List<AffectsItem> affectedItems) {

        // Don't bother if there's no ensures clause
        if (ensures != null) {
            List<PosSymbol> initialVariables, finalVariables;
            initialVariables = new List<PosSymbol>();
            finalVariables = new List<PosSymbol>();

            // Build lists of the variables whose old and new variables are
            // referred to in the ensures clause's expression.
            getContainedVariables(ensures, finalVariables, initialVariables);

            // Make sure all the referrences to old values are kosher
            sanityCheckInitialEnsuresVariables(initialVariables, parameters,
                    affectedItems);

            // Make sure all the referrences to new values are kosher
            sanityCheckFinalEnsuresVariables(finalVariables, parameters);
        }
    }

    /**
     * This method compares the list of operations specified in the concept or
     * enhancement to those actually implemented in the realization. This check
     * ensures that the user has implemented all the required procedures.
     * (Chuck)
     * 
     * @param name
     *            Name of the realization being checked
     * @param procedureList
     *            List of of procedures implemented in the realization
     * @param st
     *            Symbol table of the corresponding concept/enhancement (used to
     *            retrieve the list of operations required to be implemented)
     * @throw SanityCheckException Thrown if the missingList List is not empty
     */
    private void sanityCheckImplementedProcedures(String name,
            List<Symbol> procedureList, OldSymbolTable st) {
        boolean isMatch = false;
        List<OperationEntry> missingList = new List<OperationEntry>();
        List<Symbol> operationList =
                st.getModuleScope().getLocalOperationNames();
        ModuleScope scope = st.getModuleScope();
        String iName = scope.getScopeID().getModuleID().getName().toString();
        try {
            Symbol operationNameSymbol;
            String operationName;
            String procedureName;
            Iterator<Symbol> oper = operationList.iterator();
            while (oper.hasNext()) {
                operationNameSymbol = oper.next();
                operationName = operationNameSymbol.toString();
                Iterator<Symbol> proc = procedureList.iterator();
                while (proc.hasNext()) {
                    procedureName = proc.next().toString();
                    if (operationName.compareTo(procedureName) == 0) {
                        isMatch = true;
                        break;
                    }
                }
                if (!isMatch) {
                    missingList.add(scope.getOperation(operationNameSymbol));
                }
                isMatch = false;
            }
            if (missingList.size() != 0) {
                throw new SanityCheckException(foundMissingProceduresMessage(
                        name, iName, missingList));
            }
            /*
             * while(it.hasNext()){ Symbol operation = it.next();
             * System.out.println("Operation: "+operation.toString()); //if() }
             */
        }
        catch (SanityCheckException e) {
            // err.error(argumentName.getLocation(),
            err.error(e.getMessage());
        }
    }

    /*
     * This method simply throws a <code>SanityCheckException</code> if the
     * provided parameter modes are not compatible in the sense that the given
     * procedureMode is a valid mode for implementing the given operationMode.
     * 
     * @param operationMode The mode of the operation parameter that is being
     * implemented.
     * 
     * @param procedureMode The mode of the procedure parameter that is
     * attempting to implement the parameter from <code>operationMode</code>.
     * 
     * @throws SanityCheckException If the <code>procedureMode</code> is not a
     * valid implementation of the <code>operationMode</code>.
     */
    private void sanityCheckParameterModeStrength(Mode operationMode,
            Mode procedureMode) throws SanityCheckException {

        if (!Mode.implementsCompatible(procedureMode, operationMode)) {
            String iName =
                    myAssociatedConceptSymbolTable.getModuleID().getName()
                            .toString();
            throw new SanityCheckException(incompatibleParameterModes(iName,
                    operationMode, procedureMode));
            // incompatibleParameterModes(operationMode, procedureMode));
        }
    }

    private void sanityCheckProcedureParameterModes(
            List<Mode> operationParameterModes,
            List<Mode> procedureParameterModes, OperationEntry operation,
            ProcedureDec procedure) {

        // Check each parameter against its sister, report any errors
        Mode curOperationMode, curProcedureMode;
        for (int curArgumentIndex = 0; curArgumentIndex < operationParameterModes
                .size(); curArgumentIndex++) {

            curOperationMode = operationParameterModes.get(curArgumentIndex);
            curProcedureMode = procedureParameterModes.get(curArgumentIndex);

            try {
                // sanityCheckParameterMode
                // (curOperationMode, curProcedureMode, procedure);
                sanityCheckParameterModeStrength(curOperationMode,
                        curProcedureMode);
            }
            catch (SanityCheckException e) {
                // If we got here, the types did not match up correctly
                String iName =
                        myAssociatedConceptSymbolTable.getModuleID().getName()
                                .toString();
                Location l1 =
                        procedure.getParameters().get(curArgumentIndex)
                                .getName().getLocation();
                Iterator<VarEntry> it = operation.getParameters();
                for (int i = 0; i < curArgumentIndex; i++)
                    it.next();
                Location l2 = it.next().getName().getLocation();

                err.error(l1, l2, incompatibleParameterModes(iName,
                        curOperationMode, curProcedureMode));
            }
        }
    }

    /**
     * A sanity check to make sure that changing the provided
     * <code>ProgramExp</code> is alright within a while statement (that is,
     * that the same program expression appears in the changing clause of that
     * while statement). If there's an error, outputs it through
     * <code>err</code>.
     * 
     * ASSUMES: curWhileStatement != null
     * 
     * @param exp
     *            The expression that we're trying to change.
     */
    private void sanityCheckChanging(VariableExp varExp) {
        List<VariableExp> changingExpressions =
                myCurWhileStatement.getChanging();

        VariableNameExp exp;

        if (varExp instanceof VariableDotExp) {
            exp =
                    (VariableNameExp) ((VariableDotExp) varExp).getSegments()
                            .get(0);
        }
        else if (varExp instanceof VariableArrayExp) {
            VariableArrayExp arrayExp = (VariableArrayExp) varExp;
            exp =
                    new VariableNameExp(arrayExp.getLocation(), arrayExp
                            .getQualifier(), arrayExp.getName());
        }
        else {
            exp = (VariableNameExp) varExp;
        }

        boolean changeOk = false;
        VariableNameExp curChangingExpression;

        if (changingExpressions != null) {
            Iterator<VariableExp> changingExpressionsIterator =
                    changingExpressions.iterator();
            while (changingExpressionsIterator.hasNext() && !changeOk) {

                try {
                    curChangingExpression =
                            (VariableNameExp) changingExpressionsIterator
                                    .next();

                    if (exp.getName().getSymbol() == curChangingExpression
                            .getName().getSymbol()) {

                        changeOk = true;
                    }
                }
                catch (ClassCastException e) {
                    //System.out.println("FIXME: Changing has non-named variable?");
                    //System.out.println("       Analyzer.sanityCheckChanging()");
                }
            }
        }

        if (changeOk == false) {
            err.error(exp.getLocation(), changeNotPermitted(exp.toString(0)));
        }
    }

    /*
     * This method simply throws a <code>SanityCheckException</code> if the
     * arguments to the swap are not matching types or if swapping two
     * entries from different arrays
     * 
     * @param stmt The swap statement being checked
     * 
     * @throws SanityCheckException If the <code>stmt</code> contains non-matching
     * types or if entries from 2 different arrays are being swapped.
     */
    private void sanityCheckSwapArguments(SwapStmt stmt) {

        VariableExp lhs = stmt.getLeft();
        VariableExp rhs = stmt.getRight();
        if (!(lhs instanceof VariableArrayExp)
                || !(rhs instanceof VariableArrayExp)) {
            if (lhs instanceof VariableArrayExp) {

            }
            else if (rhs instanceof VariableArrayExp) {

            }
            else {

            }
        }
        else {
            // Case: A[1] :=: A[2] - check to make sure the array names are the same
            String lhsName = ((VariableArrayExp) lhs).getName().getName();
            String rhsName = ((VariableArrayExp) rhs).getName().getName();
            if (!lhsName.equals(rhsName)) {
                err.error(rhs.getLocation(), errorArrayNamesMismatch(lhsName,
                        rhsName));
            }
        }

    }

    /*
     * This method simply throws a <code>SanityCheckException</code> if the
     * performace profile names listed in the Dec is the same as the given PosSymbol.
     * 
     * @param profile The PosSymbol of the desired performance profile
     * @param dec The Dec to check
     * 
     * @throws SanityCheckException If the <code>Dec</code> does not contain
     * the given performance profiles.
     */
    private void sanityCheckPerformanceProfiles(PosSymbol profile, Dec dec) {
        PosSymbol decProfile = null;
        PosSymbol realizName = null;
        if (dec instanceof ConceptBodyModuleDec) {
            decProfile = ((ConceptBodyModuleDec) dec).getProfileName();
            realizName = ((ConceptBodyModuleDec) dec).getName();
        }
        else if (dec instanceof EnhancementBodyModuleDec) {
            decProfile = ((EnhancementBodyModuleDec) dec).getProfileName();
            realizName = ((EnhancementBodyModuleDec) dec).getName();
        }
        if (decProfile == null) {
            err.error(profile.getLocation(), performanceProfileMismatch(
                    profile, realizName));
        }
        else if (!decProfile.getName().equals(profile.getName())) {
            err.error(profile.getLocation(), performanceProfileMismatch(
                    profile, realizName));
        }
    }

    /* SanityCheck Helper Functions */

    /**
     * Simple helper method to check if the given <code>Symbol</code> identifies
     * an <code>AffectsItem</code> in a <code>List</code> of
     * <code>AffectsItem</code>s.
     * 
     * @param affectedItems
     *            The <code>List</code> to check.
     * @param name
     *            The <code>Symbol</code> to check for.
     * 
     * @return True iff one of the <code>AffectsItem</code>s is named
     *         <code>name</code>.
     */
    private boolean affectedItemsIncludes(List<AffectsItem> affectedItems,
            Symbol name) {

        boolean retval = false;

        int curIndex = 0;
        AffectsItem curAffectsItem;
        while (curIndex < affectedItems.size() && !retval) {
            curAffectsItem = affectedItems.get(curIndex);

            if (curAffectsItem.getName().getSymbol().equals(name)) {
                retval = true;
            }

            curIndex++;
        }

        return retval;
    }

    /**
     * Searches a <code>List</code> of <code>ParameterVarDec</code>s for a
     * parameter with the given name. Returns it as a
     * <code>ParameterVarDec</code> or <code>null</code> if no such parameter
     * exists.
     * 
     * @param parameters
     *            The <code>List</code> to search.
     * @param name
     *            The parameter name to search for.
     * 
     * @return The parameter as a <code>ParameterVarDec</code>, or
     *         <code>null</code> if there is no such parameter.
     */
    private ParameterVarDec getParameterByName(
            List<ParameterVarDec> parameters, Symbol name) {

        ParameterVarDec retval = null;

        int curIndex = 0;
        ParameterVarDec curParameter;
        while (curIndex < parameters.size() && retval == null) {
            curParameter = parameters.get(curIndex);

            if (curParameter.getName().getSymbol().equals(name)) {
                retval = curParameter;
            }

            curIndex++;
        }

        return retval;
    }

    /**
     * Returns true iff the provided mode permits an ensures or requires clause
     * to refer to its associated variable's initial state. That is, can ensures
     * refer to #X if parameter X is in mode m?
     * 
     * @param m
     *            The mode in question.
     * 
     * @return True iff an ensures/requires clause can refer to the initial
     *         state of a variable in the given mode.
     */
    private boolean validInitialMode(Mode m) {
        return (m == Mode.UPDATES || m == Mode.ALTERS || m == Mode.CLEARS || m == Mode.REASSIGNS);
    }

    /**
     * Returns true iff the provided mode permits an ensures or requires clause
     * to refer to its associated variable's final state. That is, can ensures
     * refer to X if parameter X is in mode m?
     * 
     * @param m
     *            The mode in question.
     * 
     * @return True iff an ensures/requires clause can refer to the final state
     *         of a variable in the given mode.
     */
    private boolean validFinalMode(Mode m) {
        return (m == Mode.UPDATES || m == Mode.REPLACES || m == Mode.PRESERVES
                || m == Mode.RESTORES || m == Mode.EVALUATES
                || m == Mode.CLEARS || m == Mode.REASSIGNS);
    }

    /**
     * Recursively descends into an expression to build two lists of variables
     * that are referred to in the expression: one list of variables referred to
     * for their final value (e.g., "X"), and one of variables referred to for
     * their initial value (e.g., "#X").
     * 
     * @param expression
     *            The expression in question.
     * @param initial
     *            Whether or not variables we've found should be considered for
     *            their initial values. That is, whether or not we've already
     *            descended down through an <code>OldExp</code>.
     * @param finalVariables
     *            The <code>List</code> into which to accumulate variables that
     *            are referred to for their final value.
     * @param initialVariables
     *            The <code>List</code> into which to accumulate variables that
     *            are referred to for their initial value.
     */
    private void getContainedVariables(Exp expression, boolean initial,
            List<PosSymbol> finalVariables, List<PosSymbol> initialVariables) {

    //This logic is wrong.  In particular, an OldExp around a function
    //call should apply to the functio name and not the parameters.

    /*
    if (expression instanceof VarExp) {
    	// "VarExp"s are what we are looking for. Add it to the appropriate
    	// list depending on whether or not we have descended through an
    	// "OldExp" yet.
    	if (initial) {
    		initialVariables.add(((VarExp) expression).getName());
    	} else {
    		finalVariables.add(((VarExp) expression).getName());
    	}
    } else if (expression instanceof OldExp) {
    	// An OldExp indicates that any children VarExps will be added to
    	// the "initial" list, rather than the "final" list
    	getContainedVariables(((OldExp) expression).getExp(), true,
    			finalVariables, initialVariables);
    } else {
    	// For anything else, recursively descend into the expression
    	for (Exp subexpression : expression.getSubExpressions()) {
    		getContainedVariables(subexpression, initial, finalVariables,
    				initialVariables);
    	}
    }*/
    }

    /**
     * A convenience method for the initial call to the recursive
     * <code>getContainedVariables</code> above. Simply sets
     * <code>initial</code> to false as a default. See the comment for the
     * recursive method.
     */
    private void getContainedVariables(Exp expression,
            List<PosSymbol> finalVariables, List<PosSymbol> initialVariables) {
        getContainedVariables(expression, false, finalVariables,
                initialVariables);
    }

    private String incompatibleParameterModes(String iName, Mode operationMode,
            Mode procedureMode) {

        return "Incorrect parameter mode \"" + procedureMode.toString()
                + "\" in procedure, is not " + "compatible with mode \""
                + operationMode.toString() + "\" in operation of " + iName
                + ":";
    }

    /*
     * Returns a well-formatted error message indicating that a parameter of a
     * procedure is defined using a mode incompatible with the corresponding
     * parameter of the corresponding operation definition.
     * 
     * Assumes <code>myCurrentConceptName</code> has been set appropriately.
     * 
     * @param operationMode The mode of the corresponding parameter in the
     * corresponding operation.
     * 
     * @param procedureMode The mode of the procedure parameter whose mode is
     * incompatible.
     * 
     * @return The error message.
     */
    private String incompatibleParameterModes(Mode operationMode,
            Mode procedureMode) {

        return "Corresponding parameter in "
                + myCurrentConceptBodyModuleDec.getConceptName().getName()
                + " is in mode '" + operationMode + "'.  Here, this parameter "
                + "is implemented with mode '" + procedureMode + "'.  This is "
                + "not allowed.";
    }

    /*
     * Returns the parameter modes associated with a series of parameters stored
     * in a Collection of ParameterVarDecs.
     * 
     * @param parameters A Collection of ParameterVarDecs representing each of
     * the parameters in order.
     * 
     * @return A <code>List</code> of <code>Mode</code>s wherein the first
     * element corresponds to the mode of the first parameter in
     * <code>parameters</code>, the second to the second, and so forth.
     */
    private List<Mode> getParameterVarDecModes(
            Collection<ParameterVarDec> parameters) {

        List<Mode> parameterModes = new List<Mode>();

        for (ParameterVarDec p : parameters) {
            parameterModes.add(p.getMode());
        }

        return parameterModes;
    }

    /*
     * Returns the parameter modes associated with a series of parameters stored
     * in an <code>Iterator</code> of <code>VarEntry</code>s.
     * 
     * @param parameters A <code>Collection</code> of <code>VarEntry</code>s
     * representing each of the parameters in order.
     * 
     * @return A <code>List</code> of <code>Mode</code>s wherein the first
     * element corresponds to the type of the first parameter in
     * <code>parameters</code>, the second to the second, and so forth.
     */
    private List<Mode> getParameterVarEntryModes(Iterator<VarEntry> parameters) {

        List<Mode> parameterModes = new List<Mode>();

        VarEntry curParameter;
        while (parameters.hasNext()) {
            curParameter = parameters.next();
            parameterModes.add(curParameter.getMode());
        }

        return parameterModes;
    }

    /*
     * This method retrieves an operation as an <code>OperationEntry</code> from
     * the current associated concept symbol table (which means that
     * makeAssociatedConceptSymbolTableAvailable MUST be called before calling
     * this method.) Throws a SanityCheckException if the named operation is not
     * found in the symbol table.
     * 
     * @param name The name of the operation to retrieve, as a
     * <code>Symbol</code>.
     * 
     * @return The OperationEntry associated with <code>name</code> in the
     * symbol table of the current associated concept.
     * 
     * @throws SanityCheckException If an operation with the given name cannot
     * be found.
     */
    private OperationEntry getConceptOperation(Symbol name)
            throws SanityCheckException {

        ModuleScope conceptModuleScope =
                myAssociatedConceptSymbolTable.getModuleScope();

        OperationEntry operation = null;

        if (conceptModuleScope.containsOperation(name)) {
            operation = conceptModuleScope.getOperation(name);
        }
        else {
            // throw new SanityCheckException(noMatchingOperation(name));
            return null;
        }

        return operation;
    }

    /* Error Message Methods */

    /**
     * Returns a well-formatted error message indicating that an ensures clause
     * makes a reference to a global variable's initial value, when that
     * variable was not listed in the affects clause, which is not allowed.
     * 
     * @return The error message.
     */
    private String cannotChangeValueOfUnAffectedVariableMessage() {
        return "Referring to initial value of a global variable is not "
                + "permitted when that variable is not listed in the 'affects' "
                + "clause.";
    }

    /**
     * Returns a well-formatted error message indicating that an ensures clause
     * attempted to access the initial value of a variable in a mode for which
     * this is not allowed.
     * 
     * @param m
     *            The mode of the variable.
     * 
     * @return The error message.
     */
    private String invalidModeForInitialEnsures(Mode m) {
        return "Ensures clause cannot use the old value of a variable in '" + m
                + "' mode.";
    }

    /**
     * Returns a well-formatted error message indicating that an ensures clause
     * attempted to access the final value of a variable in a mode for which
     * this is not allowed.
     * 
     * @param m
     *            The mode of the variable.
     * 
     * @return The error message.
     */
    private String invalidModeForFinalEnsures(Mode m) {
        return "Ensures clause cannot use the final value of a variable in '"
                + m + "' mode.";
    }

    /**
     * Returns a well-formatted error message indicating that a requires clause
     * attempted to refer to the initial value of a variable, which is not
     * allowed.
     * 
     * @return The error message.
     */
    private String noOldVariablesInRequiresMessage() {
        return "References to old values are not permitted in a requires "
                + "clause.";
    }

    /**
     * Returns a well-formatted error message indicating that a requires clause
     * attempted to refer to the final value of a variable that is in a mode for
     * which this is not allowed.
     * 
     * @param m
     *            The mode of the variable.
     * 
     * @return The error message.
     */
    private String invalidModeForRequires(Mode m) {
        return "Requires clause cannot restrict variables in '" + m + "' mode.";
    }

    private String illegalPrimaryOpCall(String operationName) {
        return "The primary operation " + operationName
                + " cannot be used in another's implementation procedure.";
    }

    private String changeNotPermitted(String varName) {
        return varName + " does not appear in the changing clause and "
                + "therefore cannot be modified.";
    }

    private String errorArrayNamesMismatch(String lhs, String rhs) {
        return "Invalid swap operation, " + lhs + " and " + rhs + " are not "
                + "the same array";
    }

    /**
     * This method creates an error message made up of the name of the
     * realization and the prototype(s) of the missing procedure(s).
     * 
     * @param name
     *            Name of realization with missing procedure(s)
     * @param iName
     *            Name of the concept/enhancement this realization is associated
     *            with
     * @param syms
     *            List of missing procedures
     * @return
     */
    private String foundMissingProceduresMessage(String name, String iName,
            List<OperationEntry> syms) {
        String msg = "\n" + name;
        Boolean plural = (syms.size() != 1);
        if (!plural)
            msg += " is missing a required procedure; ";
        else
            msg += " is missing required procedures; ";
        if (!plural)
            msg += iName + " also requires an implementation of:\n";
        else
            msg += iName + " also requires implementations of:\n";
        OperationEntry entry;
        Iterator<OperationEntry> it = syms.iterator();
        while (it.hasNext()) {
            entry = it.next();
            msg +=
                    err.printErrorLine(entry.getLocation().getFile(), entry
                            .getLocation().getPos());
        }
        return msg;
    }

    /**
     * Returns a well-formatted error message indicating that the number of
     * parameters given for a procedure does not match the number of parameters
     * given for its corresponding operation.
     * 
     * @return The error message.
     */
    private String procedureParameterCountDoesNotMatchOperation(String iName) {
        return "The number of arguments in this procedure does not match those in "
                + "the corresponding operation in " + iName + ":";
        /*
         * return "Corresponding operation in " + myCurrentConceptName +
         * " does not have the same number of " +
         * "arguments as this procedure.";
         */
    }

    private String performanceProfileMismatch(PosSymbol profile,
            PosSymbol realizName) {
        return "The module " + realizName.getName() + " does not contain the "
                + "performance profile " + profile.getName() + ":";
    }
}
