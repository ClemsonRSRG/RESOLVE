/**
 * SyntacticSugarConverter.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.parsing.utilities;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.ProcedureDec;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramFunctionExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramVariableArrayExp;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.items.programitems.IfConditionItem;
import edu.clemson.cs.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.cs.rsrg.absyn.statements.*;
import edu.clemson.cs.rsrg.errorhandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerVisitor;
import java.util.*;

/**
 * <p>This class performs the various different syntactic sugar conversions
 * using part of the RESOLVE abstract syntax tree. This visitor logic is
 * implemented as a {@link TreeWalkerVisitor}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class SyntacticSugarConverter extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>This map provides a mapping between the newly declared array name types
     * to the types of elements in the array.</p>
     */
    private final Map<NameTy, NameTy> myArrayNameTyToInnerTyMap;

    /**
     * <p>Once we are done walking the tree, the top most node will create
     * the new element.</p>
     */
    private ResolveConceptualElement myFinalProcessedElement;

    /**
     * <p>This stores the new elements created by current statement
     * we are visiting.</p>
     */
    private NewElementsContainer myNewElementContainer;

    /**
     * <p>Since we don't have symbol table, we really don't know if
     * we are generating a new object with the same name. In order to avoid
     * problems, all of our objects will have a name that starts with "_" and
     * end the current new element counter. This number increases by 1 each
     * time we create a new element.</p>
     */
    private int myNewElementCounter;

    /**
     * <p>This is a map from the original (unmodified) {@link ProgramExp} to
     * the new copy of the potentially modified replacing {@link ProgramExp}.</p>
     */
    private final Map<ProgramExp, ProgramExp> myProgramExpMap;

    /**
     * <p>Once we are done walking an element that could have a syntactic
     * sugar conversion, we add that element into this collector.</p>
     *
     * <p>When we reach the top-most node, it will create a new instance
     * of the object with these elements.</p>
     */
    private ResolveConceptualElementCollector myResolveElementCollector;

    // ===========================================================
    // Constructors
    // ===========================================================

    public SyntacticSugarConverter(Map<NameTy, NameTy> arrayNameTyToInnerTyMap,
            int newElementCounter) {
        myArrayNameTyToInnerTyMap = arrayNameTyToInnerTyMap;
        myFinalProcessedElement = null;
        myNewElementCounter = newElementCounter;
        myProgramExpMap = new HashMap<>();
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Parent Nodes
    // -----------------------------------------------------------

    /**
     * <p>This should be the top-most node that we start with
     * when processing syntactic sugar conversions inside a
     * {@link ProcedureDec}.</p>
     *
     * @param e Current {@link ProcedureDec} we are visiting.
     */
    @Override
    public void preProcedureDec(ProcedureDec e) {
        // Deep copy is expensive, but we will be creating a
        // completely new object when we are done walking the tree,
        // so we don't want any aliasing going around.
        myResolveElementCollector =
                new ResolveConceptualElementCollector(copyFacDecs(e
                        .getFacilities()));
    }

    /**
     * <p>This should be the last element we walk. All syntactic
     * sugar should have been performed and we can safely create
     * the new {@link ProcedureDec} to be returned.</p>
     *
     * @param e Current {@link ProcedureDec} we are visiting.
     */
    @Override
    public void postProcedureDec(ProcedureDec e) {
        //myFinalProcessedElement = new ProcedureDec();

        // Just in case
        myResolveElementCollector = null;
    }

    // -----------------------------------------------------------
    // Statement Nodes
    // -----------------------------------------------------------

    @Override
    public void postCallStmt(CallStmt e) {
        super.postCallStmt(e);
    }

    /**
     * <p>This statement doesn't need to do any syntactic sugar conversions,
     * therefore we create a new {@link ConfirmStmt} and add it to our
     * {@link ResolveConceptualElementCollector} instance.</p>
     *
     * @param e Current {@link ConfirmStmt} we are visiting.
     */
    @Override
    public void postConfirmStmt(ConfirmStmt e) {
        myResolveElementCollector.stmts.add(e.clone());
    }

    @Override
    public void postFuncAssignStmt(FuncAssignStmt e) {
        super.postFuncAssignStmt(e);
    }

    @Override
    public void preIfStmt(IfStmt e) {
        super.preIfStmt(e);
    }

    @Override
    public void postIfStmt(IfStmt e) {
        super.postIfStmt(e);
    }

    /**
     * <p>This statement doesn't need to do any syntactic sugar conversions,
     * therefore we create a new {@link MemoryStmt} and add it to our
     * {@link ResolveConceptualElementCollector} instance.</p>
     *
     * @param e Current {@link MemoryStmt} we are visiting.
     */
    @Override
    public void postMemoryStmt(MemoryStmt e) {
        myResolveElementCollector.stmts.add(e.clone());
    }

    /**
     * <p>This statement doesn't need to do any syntactic sugar conversions,
     * therefore we create a new {@link PresumeStmt} and add it to our
     * {@link ResolveConceptualElementCollector} instance.</p>
     *
     * @param e Current {@link PresumeStmt} we are visiting.
     */
    @Override
    public void postPresumeStmt(PresumeStmt e) {
        myResolveElementCollector.stmts.add(e.clone());
    }

    @Override
    public void postSwapStmt(SwapStmt e) {
        super.postSwapStmt(e);
    }

    @Override
    public void preWhileStmt(WhileStmt e) {
        super.preWhileStmt(e);
    }

    @Override
    public void midWhileStmt(WhileStmt e, ResolveConceptualElement previous,
            ResolveConceptualElement next) {
        super.midWhileStmt(e, previous, next);
    }

    @Override
    public void postWhileStmt(WhileStmt e) {
        super.postWhileStmt(e);
    }

    // -----------------------------------------------------------
    // Item Nodes
    // -----------------------------------------------------------

    @Override
    public void midIfConditionItem(IfConditionItem e,
            ResolveConceptualElement previous, ResolveConceptualElement next) {
        super.midIfConditionItem(e, previous, next);
    }

    // -----------------------------------------------------------
    // Program Expression Nodes
    // -----------------------------------------------------------

    /**
     * <p>This replaces the {@link ProgramVariableArrayExp} inside
     * the calling arguments with appropriate swapping operation from
     * {@code Static_Array_Template}.</p>
     *
     * <p>Any new statements generated will be added to a
     * {@link NewElementsContainer} instance.</p>
     *
     * @param e Current {@link ProgramFunctionExp} we are visiting.
     */
    @Override
    public void postProgramFunctionExp(ProgramFunctionExp e) {
    // TODO: Check each of the args to see if we have ProgramVariableArrayExp.
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method returns the current integer value for the
     * new element counter.</p>
     *
     * @return An integer.
     */
    public final int getNewElementCounter() {
        return myNewElementCounter;
    }

    /**
     * <p>This method returns the new (and potentially modified) element
     * that includes the syntactic sugar conversions.</p>
     *
     * @return A {@link ResolveConceptualElement} object.
     */
    public final ResolveConceptualElement getProcessedElement() {
        if (myFinalProcessedElement == null) {
            throw new MiscErrorException(
                    "The new element didn't get constructed appropriately!",
                    new NullPointerException());
        }

        return myFinalProcessedElement;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method to create a new list of {@link FacilityDec}s
     * that is a deep copy of the one passed in.</p>
     *
     * @param facilityDecs The original list of {@link FacilityDec}s.
     *
     * @return A list of {@link FacilityDec}s.
     */
    private List<FacilityDec> copyFacDecs(List<FacilityDec> facilityDecs) {
        List<FacilityDec> copyFacilityDecs = new ArrayList<>();
        for (FacilityDec facilityDec : facilityDecs) {
            copyFacilityDecs.add((FacilityDec) facilityDec.clone());
        }

        return copyFacilityDecs;
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>As we walk though the various different nodes, each
     * {@link ResolveConceptualElement} we are done processing will
     * be added to this class. Once we are done walking all the nodes
     * and we are back to the top-most node, we will use the elements
     * stored in this class to create the new object.</p>
     */
    private class ResolveConceptualElementCollector {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>List of facility declaration objects.</p>
         *
         * <p><strong>Note:</strong> Currently, none of the syntactic sugar
         * conversions create any {@link FacilityDec}, so this list should
         * remain the same.</p>
         */
        final List<FacilityDec> facilityDecs;

        /**
         * <p>List of variable declaration objects.</p>
         */
        final List<VarDec> varDecs;

        /**
         * <p>List of statement objects.</p>
         */
        final List<Statement> stmts;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This constructs a temporary structure to store all the elements
         * in the RESOLVE AST we are walking.</p>
         */
        ResolveConceptualElementCollector(List<FacilityDec> facDecs) {
            facilityDecs = facDecs;
            varDecs = new ArrayList<>();
            stmts = new ArrayList<>();
        }
    }

    /**
     * <p>This holds new items related to syntactic sugar conversions for
     * {@link ProgramVariableArrayExp}.</p>
     */
    private class NewElementsContainer {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>List of new variable declaration objects.</p>
         *
         * <p><strong>Note:</strong> The only variables generated at the moment are
         * new integer variables to store the indexes resulting from program array
         * conversions and any variables used to swap elements out of the array.</p>
         */
        final List<VarDec> newVarDecs;

        /**
         * <p>A stack of new statements that needs to be inserted before the code
         * that contains a program array expression.</p>
         *
         * <p><strong>Note:</strong> The only statements generated at the moment are
         * either new function assignment statements from indexes in
         * program array expressions or call statements to swap elements in the array(s).</p>
         */
        final Stack<Statement> newPreStmts;

        /**
         * <p>A queue of new statements that needs to be inserted after the code
         * that contains a program array expression.</p>
         *
         * <p><strong>Note:</strong> The only statements generated at the moment are
         * call statements to swap elements in the array(s).</p>
         */
        final Queue<Statement> newPostStmts;

        /**
         * <p>When walking an {@link IfStmt}, there might be syntactic sugar
         * for statements inside the if-statements that cause us to generate a new
         * {@link IfConditionItem}</p>
         */
        IfConditionItem newIfConditionItem;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This constructs a temporary structure to store all the new variable
         * declaration and statements that resulted from syntactic sugar conversions
         * for {@link ProgramVariableArrayExp}.</p>
         */
        NewElementsContainer() {
            newVarDecs = new ArrayList<>();
            newPreStmts = new Stack<>();
            newPostStmts = new ArrayDeque<>();
            newIfConditionItem = null;
        }
    }
}