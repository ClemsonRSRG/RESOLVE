package edu.clemson.cs.r2jt.processing;

// Libraries
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Map;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;

import java.util.Iterator;

/**
 * TODO: Write a description of this module
 */
public class PreProcessor extends TreeWalkerStackVisitor {

    // ===========================================================
    // Global Variables
    // ===========================================================

    /**
     * <p>Map of all the local array types encountered.</p>
     */
    private Map<String, NameTy> myArrayFacilityMap;

    /**
     * <p>A counter used to keep track the number of things
     * created by the PreProcessor.</p>
     */
    private int myCounter;

    /**
     * <p>A list of all <code>FacilityDec</code> created
     * by the PreProcessor.</p>
     */
    private List<FacilityDec> myCreatedFacDecList;

    /**
     * <p>A list of all <code>Statements</code> created
     * by the PreProcessor.</p>
     */
    private List<Statement> myCreatedStmtList;

    /**
     * <p>A list of all <code>CallStmt</code> to array
     * swap operation created by the PreProcessor.</p>
     */
    private List<CallStmt> mySwapCallList;

    /**
     * <p>Utilities class that contains methods that are used
     * in both pre and post Processors.</p>
     */
    private Utilities myUtilities;

    // TODO: Get rid of this hack!
    private String[] myUsesItems =
            { "Location_Linking_Template_1", "Static_Array_Template" };

    // ===========================================================
    // Constructors
    // ===========================================================

    public PreProcessor() {
        myArrayFacilityMap = new Map<String, NameTy>();
        myCounter = 1;
        myCreatedFacDecList = new List<FacilityDec>();
        myCreatedStmtList = new List<Statement>();
        mySwapCallList = new List<CallStmt>();
        myUtilities = new Utilities();
    }

    // ===========================================================
    // TreeWalker Methods
    // ===========================================================

    // -----------------------------------------------------------
    // ArrayTy
    // -----------------------------------------------------------

    @Override
    public void postArrayTy(ArrayTy ty) {
        // Variables
        Location location = ty.getLocation();
        NameTy oldTy = (NameTy) ty.getEntryType();
        ResolveConceptualElement parent = this.getAncestor(1);
        String arrayName = null;

        // Check if we have a FacilityTypeDec, RepresentationDec or VarDec
        if (parent instanceof FacilityTypeDec) {
            arrayName = ((FacilityTypeDec) parent).getName().getName();
        }
        else if (parent instanceof RepresentationDec) {
            arrayName = ((RepresentationDec) parent).getName().getName();
        }
        else if (parent instanceof VarDec) {
            arrayName = ((VarDec) parent).getName().getName();
        }

        // Check for not null
        if (arrayName != null) {
            // Create name in the format of "_(Name of Variable)_Array_Fac_(myCounter)"
            String newArrayName = "";
            newArrayName += ("_" + arrayName + "_Array_Fac_" + myCounter++);

            // Create newTy
            NameTy newTy =
                    new NameTy(new PosSymbol(location, Symbol
                            .symbol(newArrayName)), new PosSymbol(location,
                            Symbol.symbol("Static_Array")));

            // Check if we have a FacilityTypeDec, RepresentationDec or VarDec
            // and set the Ty of the parent node.
            if (parent instanceof FacilityTypeDec) {
                ((FacilityTypeDec) parent).setRepresentation(newTy);
            }
            else if (parent instanceof RepresentationDec) {
                ((RepresentationDec) parent).setRepresentation(newTy);
            }
            else if (parent instanceof VarDec) {
                ((VarDec) parent).setTy(newTy);
            }

            // Create a list of arguments for the new FacilityDec
            List<ModuleArgumentItem> listItem = new List<ModuleArgumentItem>();
            String typeName = oldTy.getName().getName();

            // Add the type, Low and High for Arrays
            listItem.add(new ModuleArgumentItem(null, new PosSymbol(location,
                    Symbol.symbol(typeName)), null));
            listItem.add(new ModuleArgumentItem(null, null, ty.getLo()));
            listItem.add(new ModuleArgumentItem(null, null, ty.getHi()));

            // Call method to createFacilityDec
            FacilityDec arrayFacilityDec =
                    createFacilityDec(location, newArrayName,
                            "Static_Array_Template", "Std_Array_Realiz",
                            listItem, new List<ModuleArgumentItem>(),
                            new List<EnhancementItem>(),
                            new List<EnhancementBodyItem>());

            // Add the newly created array facility to our list
            myCreatedFacDecList.add(arrayFacilityDec);

            // Save the Ty of this array for future use
            myArrayFacilityMap.put(newArrayName, oldTy);
        }
        else {
            notHandledArrayTyParent(ty.getLocation(), ty, parent);
        }
    }

    // -----------------------------------------------------------
    // CallStmt
    // -----------------------------------------------------------

    @Override
    public void postCallStmt(CallStmt stmt) {
        // Variables
        Location nLocation = stmt.getName().getLocation();
        List<ProgramExp> argList = stmt.getArguments();

        // Change any instances of A[i] and S.A[i] to actual
        // calls to operations in Static_Array_Template
        argList = arrayExpConversion(argList);

        // Add any created statements into the AST
        if (!myCreatedStmtList.isEmpty()) {
            // Ancestor Iterator
            Iterator<ResolveConceptualElement> it = this.getAncestorInterator();

            // Add it to the AST
            addStatements(nLocation, it);

            // Clear the list of FuncAssignStmt
            myCreatedStmtList.clear();
        }

        // Add any array swap calls into the AST
        // Note: The reason we do this in two steps is because
        // when we swap something, we need to swap the result
        // back to the variable when we are done calling the
        // operation.
        if (!mySwapCallList.isEmpty()) {
            // Ancestor Iterator
            Iterator<ResolveConceptualElement> it = this.getAncestorInterator();

            // Add it to the AST
            addSwapCalls(nLocation, it);

            // Clear the list of FuncAssignStmt
            mySwapCallList.clear();
        }

        // Replace the original argument list with the one
        // returned by the conversion method.
        stmt.setArguments(argList);
    }

    // -----------------------------------------------------------
    // ConceptBodyModuleDec
    // -----------------------------------------------------------

    @Override
    public void preConceptBodyModuleDec(ConceptBodyModuleDec dec) {
        // Store all global variables, facility declarations,
        // records and local operations.
        myUtilities.initModuleDec(dec.getDecs());
    }

    @Override
    public void postConceptBodyModuleDec(ConceptBodyModuleDec dec) {
        // Check to see if we created any new FacilityDecs
        // and add those to the list. When we are done, we
        // clear our list of created facility declarations.
        if (!myCreatedFacDecList.isEmpty()) {
            dec.setDecs(modifyFacDecList(dec.getDecs()));
            myCreatedFacDecList.clear();
        }

        // Clean up myUtilities
        myUtilities.finalModuleDec();
    }

    // -----------------------------------------------------------
    // EnhancementBodyModuleDec
    // -----------------------------------------------------------

    @Override
    public void preEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {
        // Store all global variables, facility declarations,
        // records and local operations.
        myUtilities.initModuleDec(dec.getDecs());
    }

    @Override
    public void postEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {
        // Check to see if we created any new FacilityDecs
        // and add those to the list. When we are done, we
        // clear our list of created facility declarations.
        if (!myCreatedFacDecList.isEmpty()) {
            dec.setDecs(modifyFacDecList(dec.getDecs()));
            myCreatedFacDecList.clear();
        }

        // Clean up myUtilities
        myUtilities.finalModuleDec();
    }

    // -----------------------------------------------------------
    // FacilityModuleDec
    // -----------------------------------------------------------

    @Override
    public void preFacilityModuleDec(FacilityModuleDec dec) {
        // Store all global variables, facility declarations,
        // records and local operations.
        myUtilities.initModuleDec(dec.getDecs());
    }

    @Override
    public void postFacilityModuleDec(FacilityModuleDec dec) {
        // Check to see if we created any new FacilityDecs
        // and add those to the list. When we are done, we
        // clear our list of created facility declarations.
        if (!myCreatedFacDecList.isEmpty()) {
            dec.setDecs(modifyFacDecList(dec.getDecs()));
            myCreatedFacDecList.clear();
        }

        // TODO: Get rid of this hack, should be handled by the new import module.
        List<UsesItem> usesList = dec.getUsesItems();
        Location location = usesList.get(0).getName().getLocation();

        for (String s : myUsesItems) {
            usesList.addUnique(new UsesItem(new PosSymbol(location, Symbol
                    .symbol(s))));
        }
        dec.setUsesItems(usesList);

        // Clean up myUtilities
        myUtilities.finalModuleDec();
    }

    // -----------------------------------------------------------
    // FacilityOperationDec
    // -----------------------------------------------------------

    @Override
    public void preFacilityOperationDec(FacilityOperationDec dec) {
        // Store all parameter and local variables
        myUtilities.initOperationDec(dec.getParameters(), dec.getVariables());
    }

    @Override
    public void postFacilityOperationDec(FacilityOperationDec dec) {
        // Check to see if we created any new FacilityDecs
        // and add those to the list. When we are done, we
        // clear our list of created facility declarations.
        if (!myCreatedFacDecList.isEmpty()) {
            dec.setFacilities(modifyFacDecListForOps(dec.getFacilities()));
            myCreatedFacDecList.clear();
        }

        // Store the local variable list
        dec.setVariables(myUtilities.getLocalVarList());

        // Clean up myUtilities
        myUtilities.finalOperationDec();
    }

    // -----------------------------------------------------------
    // ProcedureDec
    // -----------------------------------------------------------

    @Override
    public void preProcedureDec(ProcedureDec dec) {
        // Store all parameter and local variables
        myUtilities.initOperationDec(dec.getParameters(), dec.getVariables());
    }

    @Override
    public void postProcedureDec(ProcedureDec dec) {
        // Check to see if we created any new FacilityDecs
        // and add those to the list. When we are done, we
        // clear our list of created facility declarations.
        if (!myCreatedFacDecList.isEmpty()) {
            dec.setFacilities(modifyFacDecListForOps(dec.getFacilities()));
            myCreatedFacDecList.clear();
        }

        // Store the local variable list
        dec.setVariables(myUtilities.getLocalVarList());

        // Clean up myUtilities
        myUtilities.finalOperationDec();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Error Handling
    // -----------------------------------------------------------

    public void notHandledArrayTyParent(Location location, ArrayTy ty,
            ResolveConceptualElement parent) {
        String message =
                "ArrayTy "
                        + ty.toString()
                        + "'s parent is "
                        + parent.toString()
                        + ". This type of parent is not handled in the PreProcessor.";
        throw new SourceErrorException(message, location);
    }

    public void recordNotFound(Location location, PosSymbol name) {
        String message =
                "Cannot find a record with the name: " + name.getName();
        throw new SourceErrorException(message, location);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Adds the newly created <code>Statements</code> to the
     * right place in our AST.</p>
     *
     * @param location The location in our AST where the statements
     *                 needs to be added to.
     * @param it The ancestor iterator.
     */
    private void addStatements(Location location,
            Iterator<ResolveConceptualElement> it) {
        // Loop
        while (it.hasNext()) {
            // Obtain a temp from it
            ResolveConceptualElement temp = it.next();

            // Our parent is a FacilityOperationDec
            if (temp instanceof FacilityOperationDec) {
                List<Statement> statements =
                        ((FacilityOperationDec) temp).getStatements();

                // Update our list of statements
                statements = modifyStatementList(location, statements);

                // Update the FacilityOperationDec statement list
                ((FacilityOperationDec) temp).setStatements(statements);
                break;
            }
            // Our parent is a ProcedureDec
            else if (temp instanceof ProcedureDec) {
                List<Statement> statements =
                        ((ProcedureDec) temp).getStatements();

                // Update our list of statements
                statements = modifyStatementList(location, statements);

                // Update the FacilityOperationDec statement list
                ((ProcedureDec) temp).setStatements(statements);
                break;
            }
        }
    }

    /**
     * <p>Adds the created <code>CallStmt</code> for swapping array
     * entries to right places in our AST.</p>
     *
     * @param location The location in our AST where the statements
     *                 needs to be added to.
     * @param it The ancestor iterator.
     */
    private void addSwapCalls(Location location, Iterator<ResolveConceptualElement> it) {
        // Loop
        while (it.hasNext()) {
            // Obtain a temp from it
            ResolveConceptualElement temp = it.next();

            // Our parent is a FacilityOperationDec
            if (temp instanceof FacilityOperationDec) {
                List<Statement> statements =
                        ((FacilityOperationDec) temp).getStatements();

                // Update our list of statements
                statements = modifyStatementListForSwapCalls(location, statements);

                // Update the FacilityOperationDec statement list
                ((FacilityOperationDec) temp).setStatements(statements);
                break;
            }
            // Our parent is a ProcedureDec
            else if (temp instanceof ProcedureDec) {
                List<Statement> statements =
                        ((ProcedureDec) temp).getStatements();

                // Update our list of statements
                statements = modifyStatementListForSwapCalls(location, statements);

                // Update the FacilityOperationDec statement list
                ((ProcedureDec) temp).setStatements(statements);
                break;
            }
        }
    }

    /**
     * <p>Converts any <code>VariableArrayExp</code> to a <code>VariableNameExp</code>
     * by creating a new <code>Exp</code> and applying swap calls provided by the
     * Static_Array_Template.</p>
     *
     * @param argList The list of arguments being used to invoke the current
     *                operation.
     *
     * @return The modified argument list
     */
    private List<ProgramExp> arrayExpConversion(List<ProgramExp> argList) {
        // Iterate through the argument list
        for (int i = 0; i < argList.size(); i++) {
            ProgramExp current = argList.get(i);

            // Check if it is a VariableArrayExp
            if (current instanceof VariableArrayExp) {
                // Things to be used frequently
                Location location = current.getLocation();
                PosSymbol name = ((VariableArrayExp) current).getName();

                // Create a new variable name expression for the entire
                // array expression
                VariableNameExp newExp =
                        createVariableNameExp(location, "_ArrayExp_", name
                                .getName(), "_" + myCounter++);

                // Create a new variable name expression for the index
                // of the array expression
                VariableNameExp newIndexExp =
                        createVariableNameExp(location, "_ArrayIndex_", name
                                .getName(), "_" + myCounter++);

                // Locate the type of the array.
                VarDec arrayVarDec = myUtilities.searchVarDecLists(name);
                NameTy facilityTy = (NameTy) arrayVarDec.getTy();
                NameTy arrayTy =
                        myArrayFacilityMap.get(facilityTy.getQualifier()
                                .getName());

                // Create new variables for these two new variable
                // expressions and add these to our list of local
                // variables.
                VarDec expVarDec = new VarDec(newExp.getName(), arrayTy);
                VarDec indexVarDec =
                        new VarDec(newIndexExp.getName(),
                                createIntegerTy(location));
                myUtilities.addNewLocalVariable(expVarDec);
                myUtilities.addNewLocalVariable(indexVarDec);

                // Store the index of the array inside "newIndexExp" by
                // creating a <code>FunctionAssignStmt</code> and add it
                // to the list of statements to be inserted later.
                FuncAssignStmt funcAssignStmt =
                        new FuncAssignStmt(location, newIndexExp,
                                ((VariableArrayExp) current).getArgument());
                myCreatedStmtList.add(funcAssignStmt);

                // Create a call to Swap_Entry
                List<ProgramExp> callArgList = new List<ProgramExp>();
                callArgList.add(createVariableNameExp(location, "", name
                        .getName(), ""));
                callArgList.add(newExp);
                callArgList.add(newIndexExp);

                CallStmt swapEntryStmt =
                        new CallStmt(null, new PosSymbol(location, Symbol
                                .symbol("Swap_Entry")), callArgList);
                mySwapCallList.add(swapEntryStmt);

                // Replace current with the newExp
                argList.set(i, newExp);
            }
            else if (current instanceof VariableDotExp) {
                // Get list of segments
                List<VariableExp> segList =
                        ((VariableDotExp) current).getSegments();
                VariableNameExp first = (VariableNameExp) segList.get(0);
                VariableExp last = segList.get(segList.size() - 1);

                // Check to see if our dot expression contains an
                // array expression as its last segment. Ex: S.A[i]
                if (last instanceof VariableArrayExp) {
                    // Things to be used frequently
                    Location location = current.getLocation();
                    PosSymbol name = ((VariableArrayExp) last).getName();

                    // Locate the array declaration inside the record
                    VarDec recordVarDec =
                            myUtilities.searchVarDecLists(first.getName());

                    if (recordVarDec != null) {
                        // Create a new variable name expression for the entire
                        // array expression
                        VariableNameExp newExp =
                                createVariableNameExp(location, "_ArrayExp_",
                                        name.getName(), "_" + myCounter++);

                        // Create a new variable name expression for the index
                        // of the array expression
                        VariableNameExp newIndexExp =
                                createVariableNameExp(location, "_ArrayIndex_",
                                        name.getName(), "_" + myCounter++);

                        // Locate the type of the array inside a record
                        NameTy recordTy = (NameTy) recordVarDec.getTy();
                        VarDec arrayVarDec =
                                myUtilities.searchRecords(recordTy.getName(),
                                        name);
                        NameTy facilityTy = (NameTy) arrayVarDec.getTy();
                        NameTy arrayTy =
                                myArrayFacilityMap.get(facilityTy
                                        .getQualifier().getName());

                        // Create new variables for these two new variable
                        // expressions and add these to our list of local
                        // variables.
                        VarDec expVarDec =
                                new VarDec(newExp.getName(), arrayTy);
                        VarDec indexVarDec =
                                new VarDec(newIndexExp.getName(),
                                        createIntegerTy(location));
                        myUtilities.addNewLocalVariable(expVarDec);
                        myUtilities.addNewLocalVariable(indexVarDec);

                        // Store the index of the array inside "newIndexExp" by
                        // creating a <code>FunctionAssignStmt</code> and add it
                        // to the list of statements to be inserted later.
                        FuncAssignStmt funcAssignStmt =
                                new FuncAssignStmt(location, newIndexExp,
                                        ((VariableArrayExp) current)
                                                .getArgument());
                        myCreatedStmtList.add(funcAssignStmt);

                        // Create a call to Swap_Entry
                        List<ProgramExp> callArgList = new List<ProgramExp>();
                        callArgList.add(createVariableNameExp(location, "",
                                name.getName(), ""));
                        callArgList.add(newExp);
                        callArgList.add(newIndexExp);

                        CallStmt swapEntryStmt =
                                new CallStmt(null, new PosSymbol(location,
                                        Symbol.symbol("Swap_Entry")),
                                        callArgList);
                        mySwapCallList.add(swapEntryStmt);

                        // Replace current with the newExp
                        argList.set(i, newExp);
                    }
                    else {
                        recordNotFound(location, first.getName());
                    }
                }
            }
        }

        return argList;
    }

    /**
     * <p>Creates a new <code>FacilityDec</code>.</p>
     *
     * @param location The location where the <code>FacilityDec</code> is created
     * @param name The name of the new <code>FacilityDec</code>.
     * @param conceptName The name of the Concept of this <code>FacilityDec</code>.
     * @param conceptRealizationName The name of the Concept Realization of this
     *                               <code>FacilityDec</code>.
     * @param conceptParam The list of parameters for the Concept.
     * @param conceptBodiesParam The list of parameters for the Concept
     *                           Realization.
     * @param enhancementParam The list of parameters for the Enhancement.
     * @param enhancementBodiesParam The list of parameters for the Enhancement
     *                               Realization.
     *
     * @return Newly created <code>FacilityDec</code>
     */
    private FacilityDec createFacilityDec(Location location, String name,
            String conceptName, String conceptRealizationName,
            List<ModuleArgumentItem> conceptParam,
            List<ModuleArgumentItem> conceptBodiesParam,
            List<EnhancementItem> enhancementParam,
            List<EnhancementBodyItem> enhancementBodiesParam) {
        // Create a FacilityDec
        FacilityDec newFacilityDec = new FacilityDec();

            // Set the name
            newFacilityDec
                    .setName(new PosSymbol(location, Symbol.symbol(name)));

            // Set the Concept
            newFacilityDec.setConceptName(new PosSymbol(location, Symbol
                    .symbol(conceptName)));
            newFacilityDec.setConceptParams(conceptParam);

            // Set the Concept Realization
            newFacilityDec.setBodyName(new PosSymbol(location, Symbol
                    .symbol(conceptRealizationName)));
            newFacilityDec.setBodyParams(conceptBodiesParam);

            // Set the Enhancement and Enhancement Realization list
            newFacilityDec.setEnhancements(enhancementParam);
            newFacilityDec.setEnhancementBodies(enhancementBodiesParam);

        return newFacilityDec;
    }

    /**
     * <p>Creates a <code>Ty</code> for Integers.</p>
     *
     * @param location A given location in the AST.
     *
     * @return The <code>Ty</code> form for Integers.
     */
    private Ty createIntegerTy(Location location) {
        return new NameTy(null, new PosSymbol(location, Symbol
                .symbol("Integer")));
    }

    /**
     * <p>Creates a new <code>VariableNameExp</code> given a prefix and
     * the old variable name.</p>
     *
     * @param location Location of the array variable.
     * @param prefix Prefix for the new variable expression.
     * @param name Name of the old variable expression.
     * @param suffix Suffix for the new variable expression.
     *
     * @return A <code>VariableNameExp</code> of the form
     *         "prefix_(name)_suffix".
     */
    private VariableNameExp createVariableNameExp(Location location,
            String prefix, String name, String suffix) {
        // Create a new name
        String newNameStr = prefix + name + suffix;
        PosSymbol newName = new PosSymbol(location, Symbol.symbol(newNameStr));

        return new VariableNameExp(location, null, newName);
    }

    /**
     * <p>Modifies the list of <code>Decs</code> passed in by
     * adding the facilities created by the PreProcessor to the
     * front of the list.</p>
     *
     * @param decList List of <code>Decs</code> to be modified.
     *
     * @return Modified list of <code>Decs</code>.
     */
    private List<Dec> modifyFacDecList(List<Dec> decList) {
        // Loop through the list
        for (int i = myCreatedFacDecList.size()-1; i >= 0; i--) {
            // Add to the front of the list
            decList.add(0, myCreatedFacDecList.get(i));
        }

        return decList;
    }

    /**
     * <p>Modifies the list of <code>FacilityDecs</code> passed in by
     * adding the facilities created by the PreProcessor to the
     * front of the list.</p>
     *
     * @param decList List of <code>FacilityDecs</code> to be modified.
     *
     * @return Modified list of <code>FacilityDecs</code>.
     */
    private List<FacilityDec> modifyFacDecListForOps(List<FacilityDec> decList) {
        // Loop through the list
        for (int i = myCreatedFacDecList.size()-1; i >= 0; i--) {
            // Add to the front of the list
            decList.add(0, myCreatedFacDecList.get(i));
        }

        return decList;
    }

    /**
     * <p>Modifies the statement list passed in by adding
     * the statements created by the PreProcessor in the right
     * location in our AST.</p>
     *
     * @param location Location of the array variable.
     * @param stmtList List of statements to be modified.
     *
     * @return Modified statement list.
     */
    private List<Statement> modifyStatementList(Location location,
            List<Statement> stmtList) {
        // Loop through the list
        for (int i = 0; i < stmtList.size(); i++) {
            Statement current = stmtList.get(i);

            // The CallStmt or FuncAssignStmt specified by the location
            if ((current instanceof CallStmt && ((CallStmt) current).getName()
                    .getLocation().equals(location))
                    || (current instanceof FuncAssignStmt && current
                            .getLocation().equals(location))) {
                // Add all created statements before current
                for (Statement s : myCreatedStmtList) {
                    stmtList.add(i, s);
                }
                break;
            }
        }

        return stmtList;
    }

    /**
     * <p>Modifies the statement list passed in by adding
     * the swap call statements created by the PreProcessor
     * before and after the specified location in our AST.</p>
     *
     * @param location Location of the array variable.
     * @param stmtList List of statements to be modified.
     *
     * @return Modified statement list.
     */
    private List<Statement> modifyStatementListForSwapCalls(Location location, List<Statement> stmtList) {
        // Loop through the list
        for (int i = 0; i < stmtList.size(); i++) {
            Statement current = stmtList.get(i);

            // The CallStmt or FuncAssignStmt specified by the location
            if ((current instanceof CallStmt && ((CallStmt) current).getName()
                    .getLocation().equals(location))
                    || (current instanceof FuncAssignStmt && current
                    .getLocation().equals(location))) {
                // Add all created statements after current
                for (int j = 0; j < mySwapCallList.size(); j++) {
                    stmtList.add(i+1, mySwapCallList.get(j));
                }

                // Add all created statements before current
                for (int j = mySwapCallList.size()-1; j >= 0; j--) {
                    stmtList.add(i, mySwapCallList.get(j));
                }
                break;
            }
        }

        return stmtList;
    }
}