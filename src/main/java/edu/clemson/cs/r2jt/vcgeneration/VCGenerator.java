/**
 * VCGenerator.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.vcgeneration;

/*
 * Libraries
 */
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.scope.*;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.r2jt.type.BooleanType;
import edu.clemson.cs.r2jt.type.ConcType;
import edu.clemson.cs.r2jt.type.NameType;
import edu.clemson.cs.r2jt.type.Type;
import edu.clemson.cs.r2jt.typeandpopulate.*;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TODO: Write a description of this module
 */
public class VCGenerator extends TreeWalkerVisitor {

    // ===========================================================
    // Global Variables 
    // ===========================================================

    // Symbol table related items
    private final MathSymbolTableBuilder mySymbolTable;
    private final TypeGraph myTypeGraph;
    private final MTType BOOLEAN;
    private final MTType MTYPE;
    private final MTType Z;

    // Compile Environment
    private CompileEnvironment myInstanceEnvironment;

 // Assertive Code
    private AssertiveCode myAssertion;

    // ===========================================================
    // Flag Strings
    // ===========================================================

    private static final String FLAG_ALTSECTION_NAME = "GenerateVCs";
    private static final String FLAG_DESC_ATLVERIFY_VC = "Generate VCs.";
    private static final String FLAG_DESC_ATTLISTVCS_VC = "";

    // ===========================================================
    // Flags
    // ===========================================================

    public static final Flag FLAG_ALTVERIFY_VC =
            new Flag(FLAG_ALTSECTION_NAME, "altVCs", FLAG_DESC_ATLVERIFY_VC);

    public static final Flag FLAG_ALTLISTVCS_VC =
            new Flag(FLAG_ALTSECTION_NAME, "altListVCs",
                    FLAG_DESC_ATTLISTVCS_VC, Flag.Type.HIDDEN);

    public static final void setUpFlags() {
        FlagDependencies.addImplies(FLAG_ALTVERIFY_VC, FLAG_ALTLISTVCS_VC);
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    public VCGenerator(ScopeRepository table, final CompileEnvironment env) {
        // Symbol table items
        mySymbolTable = (MathSymbolTableBuilder) table;
        myTypeGraph = mySymbolTable.getTypeGraph();
        BOOLEAN = myTypeGraph.BOOLEAN;
        MTYPE = myTypeGraph.MTYPE;
        Z = myTypeGraph.Z;

        myInstanceEnvironment = env;
        myAssertion = null;
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // EnhancementBodyModuleDec
    // -----------------------------------------------------------

    @Override
    public void preEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {

    }

    @Override
    public void postEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {

    }

    // -----------------------------------------------------------
    // FacilityOperationDec
    // -----------------------------------------------------------

    @Override
    public void postFacilityOperationDec(FacilityOperationDec dec) {
        // Create assertive code
        myAssertion = new AssertiveCode(myInstanceEnvironment);

        // Obtain the id for the module we are in.
        ModuleIdentifier id = mySymbolTable.getScope(dec).getRootModule();
        try {
            // Obtain the module dec and use it to obtain the global requires clause
            ModuleDec mDec =
                    mySymbolTable.getModuleScope(id).getDefiningElement();
            Exp gRequires = getRequiresClause(mDec);

            // Obtains items from the current operation
            Exp ensures =
                    modifyEnsuresClause(dec.getEnsures(), dec.getLocation(),
                            dec.getName());
            List<Statement> statementList = dec.getStatements();

            // Apply the procedure declaration rule
            applyProcedureDeclRule(gRequires, ensures, statementList);

            // Apply proof rules
            applyEBRules();

            System.out.println(myAssertion.getFinalConfirm());
        }
        catch (NoSuchSymbolException nsse) {
            System.err.println("Module " + id
                    + " does not exist or is not in scope.");
            noSuchModule(dec.getLocation());
        }

        myAssertion = null;
    }

    // -----------------------------------------------------------
    // ProcedureDec
    // -----------------------------------------------------------

    @Override
    public void postProcedureDec(ProcedureDec dec) {
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Error Handling
    // -----------------------------------------------------------

    public void noSuchModule(Location location) {
        throw new SourceErrorException(
                "Module does not exist or is not in scope.", location);
    }

    public void noSuchModule(PosSymbol qualifier) {
        throw new SourceErrorException(
                "Module does not exist or is not in scope.", qualifier);
    }

    public void noSuchSymbol(PosSymbol qualifier, PosSymbol symbol) {
        noSuchSymbol(qualifier, symbol.getName(), symbol.getLocation());
    }

    public void noSuchSymbol(PosSymbol qualifier, String symbolName, Location l) {

        String message;

        if (qualifier == null) {
            message = "No such symbol: " + symbolName;
        }
        else {
            message =
                    "No such symbol in module: " + qualifier.getName() + "."
                            + symbolName;
        }

        throw new SourceErrorException(message, l);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Returns a newly created <code>PosSymbol</code>
     * with the string provided.</p>
     *
     * @param name String of the new <code>PosSymbol</code>.
     *
     * @return The new <code>PosSymbol</code>.
     */
    private PosSymbol createPosSymbol(String name) {
        // Create the PosSymbol
        PosSymbol posSym = new PosSymbol();
        posSym.setSymbol(Symbol.symbol(name));
        return posSym;
    }

    /**
     * <p>Returns the requires clause for the current <code>Dec</code>.</p>
     *
     * @param dec The corresponding <code>Dec</code>.
     *
     * @return The requires clause <code>Exp</code>.
     */
    private Exp getRequiresClause(Dec dec) {
        PosSymbol name = dec.getName();
        Exp retExp = null;

        // Check for each kind of ModuleDec possible
        if (dec instanceof FacilityOperationDec) {
            retExp = ((FacilityOperationDec) dec).getRequires();
        }
        else if (dec instanceof EnhancementBodyModuleDec) {
            retExp = ((EnhancementBodyModuleDec) dec).getRequires();
        }
        else if (dec instanceof FacilityModuleDec) {
            retExp = ((FacilityModuleDec) dec).getRequirement();
        }

        // Fill in the details of this location
        if (retExp != null) {
            if (retExp.getLocation() != null) {
                Location myLoc = retExp.getLocation();
                myLoc.setDetails("Requires Clause for " + name);
                setLocation(retExp, myLoc);
            }
        }

        return retExp;
    }

    //
    /**
     * <p>Get the <code>PosSymbol</code> associated with the
     * <code>VariableExp</code> left.</p>
     *
     * @param left The variable expression.
     *
     * @return The <code>PosSymbol</code> of left.
     */
    private PosSymbol getVarName(VariableExp left) {
        // Return value
        PosSymbol name;

        // Variable Name Expression
        if (left instanceof VariableNameExp) {
            name = ((VariableNameExp) left).getName();
        }
        // Variable Dot Expression
        else if (left instanceof VariableDotExp) {
            VariableRecordExp varRecExp =
                    (VariableRecordExp) ((VariableDotExp) left)
                            .getSemanticExp();
            name = varRecExp.getName();
        }
        // Variable Record Expression
        else if (left instanceof VariableRecordExp) {
            VariableRecordExp varRecExp = (VariableRecordExp) left;
            name = varRecExp.getName();
        }
        // Variable Array Expression
        // Note (YS): We shouldn't see this by now. Handled by Preprocessor
        else if (left instanceof VariableArrayExp) {
            name = ((VariableArrayExp) left).getName();
        }
        // Creates an expression with "false" as its name
        else {
            name = createPosSymbol("false");
        }

        return name;
    }

    /**
     * <p>Returns the requires clause for the current <code>Dec</code>.</p>
     *
     * @param ensures The <code>Exp</code> containing the ensures clause.
     * @param loc Location of the <code>Operation</code>
     * @param name Name of the <code>Operation</code>
     *
     * @return The ensures clause <code>Exp</code>.
     */
    private Exp modifyEnsuresClause(Exp ensures, Location loc, PosSymbol name) {
        // Obtain the right location
        Location ensuresLoc;
        if (ensures == null) {
            ensuresLoc = (Location) loc.clone();
        }
        else {
            ensuresLoc = (Location) (ensures.getLocation().clone());
        }

        // Modify the details
        if (ensuresLoc != null) {
            ensuresLoc.setDetails("Ensures Clause of " + name);
            setLocation(ensures, ensuresLoc);
        }

        return ensures;
    }

    /**
     * <p>Returns the requires clause for the current
     * <code>Dec</code>.</p>
     *
     * @param exp The <code>Exp</code> to be replaced.
     * @param old The old sub-expression of <code>exp</code>.
     * @param repl The new sub-expression of <code>exp</code>.
     *
     * @return The new <code>Exp</code>.
     */
    private Exp replace(Exp exp, Exp old, Exp repl) {
        // Clone old and repl and use the Exp replace to do all its work
        Exp tmp = Exp.replace(exp, (Exp) Exp.clone(old), (Exp) Exp.clone(repl));

        // Return the corresponding Exp
        if (tmp != null)
            return tmp;
        else
            return exp;
    }

    /**
     * <p>Changes the <code>Exp</code> with the new
     * <code>Location</code>.</p>
     *
     * @param exp The <code>Exp</code> that needs to be modified.
     * @param loc The new <code>Location</code>.
     */
    private void setLocation(Exp exp, Location loc) {
        // Special handling for InfixExp
        if (exp instanceof InfixExp) {
            ((InfixExp) exp).setAllLocations(loc);
        }
        else {
            exp.setLocation(loc);
        }
    }

    // -----------------------------------------------------------
    // Proof Rules
    // -----------------------------------------------------------

    /**
     * <p>Applies the assume rule.</p>
     *
     * @param assume The assume clause
     */
    private void applyAssumeRule(VerificationStatement assume) {
        // Obtain the current final confirm clause
        Exp conf = myAssertion.getFinalConfirm();

        // Create a new expression
        InfixExp newConf = new InfixExp();

        // Create an implies symbol
        PosSymbol opName = new PosSymbol();
        opName.setSymbol(Symbol.symbol("implies"));
        newConf.setType(BooleanType.INSTANCE);
        newConf.setMathType(BOOLEAN);

        // Convert the assume a confirm b into
        // confirm a implies b.
        newConf.setLeft(((Exp) assume.getAssertion()));
        newConf.setOpName(opName);
        newConf.setRight(conf);

        // Set this new expression as the new final confirm
        myAssertion.setFinalConfirm(newConf);
    }

    /**
     * <p>Applies different rules to code statements.</p>
     *
     * @param statement The different statements.
     */
    private void applyCodeRules(Statement statement) {
        // Apply each statement rule here.
        if (statement instanceof SwapStmt) {
            applyEBSwapStmtRule((SwapStmt) statement);
        }
    }

    /**
     * <p>Applies the confirm rule.</p>
     *
     * @param confirm The confirm clause
     */
    private void applyConfirmRule(VerificationStatement confirm) {
        // Obtain the current final confirm clause
        Exp conf = myAssertion.getFinalConfirm();

        // Create a new expression
        InfixExp newConf = new InfixExp();

        // Create an and symbol
        PosSymbol opName = new PosSymbol();
        opName.setSymbol(Symbol.symbol("and"));
        newConf.setType(BooleanType.INSTANCE);
        newConf.setMathType(BOOLEAN);

        // Convert the confirm a confirm b into
        // confirm a and b.
        newConf.setLeft((Exp) confirm.getAssertion());
        newConf.setOpName(opName);
        newConf.setRight(conf);

        // Set this new expression as the new final confirm
        myAssertion.setFinalConfirm(newConf);
    }

    /**
     * <p>Applies each of the proof rules. This <code>AssertiveCode</code> will be
     * stored for later use and therefore should be considered immutable after
     * a call to this method.</p>
     */
    private void applyEBRules() {
        // Apply a proof rule to each of the assertions
        while (myAssertion.hasAnotherAssertion()) {
            // Work our way from the last assertion
            VerificationStatement curAssertion = myAssertion.getLastAssertion();

            // Assume Assertion
            if (curAssertion.getType() == VerificationStatement.ASSUME) {
                applyAssumeRule(curAssertion);
            }
            // Confirm Assertion
            else if (curAssertion.getType() == VerificationStatement.CONFIRM) {
                applyConfirmRule(curAssertion);
            }
            // Code
            else if (curAssertion.getType() == VerificationStatement.CODE) {
                applyCodeRules((Statement) curAssertion.getAssertion());
                if ((Statement) curAssertion.getAssertion() instanceof WhileStmt
                        || (Statement) curAssertion.getAssertion() instanceof IfStmt) {
                    return;
                }
            }
            // Remember Assertion
            else if (curAssertion.getType() == VerificationStatement.REMEMBER) {
                applyRememberRule();
            }
        }
    }

    /**
     * <p>Applies the swap statement rule to the
     * <code>Statement</code>.</p>
     *
     * @param stmt Our current <code>SwapStmt</code>.
     */
    private void applyEBSwapStmtRule(SwapStmt stmt) {
        // Obtain the current final confirm clause
        Exp conf = myAssertion.getFinalConfirm();

        // Create a copy of the left and right hand side
        VariableExp left = (VariableExp) Exp.copy(stmt.getLeft());
        VariableExp right = (VariableExp) Exp.copy(stmt.getRight());

        // Append a _ to the front of the left variable
        String lftStr = getVarName(left).toString();
        String lftTmp = "_";
        lftTmp = lftTmp.concat(lftStr);

        // New left and right
        Exp leftV;
        Exp rightV;

        // VariableDotExp
        if (left instanceof VariableDotExp) {
            // Make a copy of left into leftV
            leftV = new DotExp();
            ((DotExp) leftV).setSemanticExp(((VariableDotExp) left)
                    .getSemanticExp());
            edu.clemson.cs.r2jt.collections.List<Exp> myList =
                    new edu.clemson.cs.r2jt.collections.List<Exp>();
            for (int i = 0; i < ((VariableDotExp) left).getSegments().size(); i++) {
                VariableExp varExp =
                        ((VariableDotExp) left).getSegments().get(i);
                varExp.setType(left.getType());
                varExp.setMathType(left.getMathType());
                varExp.setMathTypeValue(left.getMathTypeValue());
                myList.add(i, varExp);
            }
            ((DotExp) leftV).setSegments(myList);
        }
        // VariableNameExp
        else {
            leftV = new VarExp();
            ((VarExp) leftV).setName(getVarName(left));
        }

        // Copy the math type information into leftV
        leftV.setType(left.getType());
        leftV.setMathType(left.getMathType());
        leftV.setMathTypeValue(left.getMathTypeValue());

        // VariableDotExp
        if (right instanceof VariableDotExp) {
            // Make a copy of right into rightV
            rightV = new DotExp();
            ((DotExp) rightV).setSemanticExp(((VariableDotExp) right)
                    .getSemanticExp());
            edu.clemson.cs.r2jt.collections.List<Exp> myList =
                    new edu.clemson.cs.r2jt.collections.List<Exp>();
            for (int i = 0; i < ((VariableDotExp) right).getSegments().size(); i++) {
                VariableExp varExp =
                        ((VariableDotExp) right).getSegments().get(i);
                varExp.setType(right.getType());
                varExp.setMathType(right.getMathType());
                varExp.setMathTypeValue(right.getMathTypeValue());
                myList.add(i, varExp);
            }
            ((DotExp) rightV).setSegments(myList);
        }
        // VariableNameExp
        else {
            rightV = new VarExp();
            ((VarExp) rightV).setName(getVarName(right));
            rightV.setMathType(right.getMathType());
            rightV.setMathTypeValue(right.getMathTypeValue());
        }

        // Need to Set Exp for rightV and leftV
        List lst = conf.getSubExpressions();
        for (int i = 0; i < lst.size(); i++) {
            if (lst.get(i) instanceof VarExp) {
                VarExp thisExp = (VarExp) lst.get(i);
                if (rightV instanceof VarExp) {
                    if (thisExp.getName().toString().equals(
                            ((VarExp) rightV).getName().toString())) {
                        rightV.setType(thisExp.getType());
                        rightV.setMathType(thisExp.getMathType());
                        rightV.setMathTypeValue(thisExp.getMathTypeValue());
                    }
                }
                if (leftV instanceof VarExp) {
                    if (thisExp.getName().toString().equals(
                            ((VarExp) leftV).getName().toString())) {
                        leftV.setType(thisExp.getType());
                        leftV.setMathType(thisExp.getMathType());
                        leftV.setMathTypeValue(thisExp.getMathTypeValue());
                    }
                }
            }
        }

        // Temp variable
        VarExp tmp = new VarExp();
        tmp.setName(createPosSymbol(lftTmp));
        tmp.setType(left.getType());
        tmp.setMathType(left.getMathType());
        tmp.setMathTypeValue(left.getMathTypeValue());

        // Replace according to the swap rule
        conf = replace(conf, rightV, tmp);
        conf = replace(conf, leftV, rightV);
        conf = replace(conf, tmp, leftV);

        // Set this new expression as the new final confirm
        myAssertion.setFinalConfirm(conf);
    }

    /**
     * <p>Applies the procedure declaration rule</p>
     *
     * @param gRequires Global requires clause
     * @param ensures Ensures clause
     * @param statementList List of statements for this procedure
     */
    private void applyProcedureDeclRule(Exp gRequires, Exp ensures,
            List<Statement> statementList) {
        // Add the global requires clause
        if (gRequires != null) {
            myAssertion.addAssume(gRequires);
        }

        // Add the remember rule
        myAssertion.addRemember();

        // Add the list of statements
        myAssertion.addStatements(statementList);

        // Add the final confirms clause
        myAssertion.setFinalConfirm(ensures);
    }

    /**
     * <p>Applies the Proof rule for Remember.</p>
     */
    private void applyRememberRule() {
        // Obtain the final confirm and apply the remember method for Exp
        Exp conf = myAssertion.getFinalConfirm();
        conf = conf.remember();
        myAssertion.setFinalConfirm(conf);
    }
}