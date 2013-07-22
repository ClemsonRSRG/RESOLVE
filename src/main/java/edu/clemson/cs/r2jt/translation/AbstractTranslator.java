/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.absyn.CallStmt;
import edu.clemson.cs.r2jt.absyn.FacilityModuleDec;
import edu.clemson.cs.r2jt.absyn.FacilityOperationDec;
import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.absyn.NameTy;
import edu.clemson.cs.r2jt.absyn.OperationDec;
import edu.clemson.cs.r2jt.absyn.ParameterVarDec;
import edu.clemson.cs.r2jt.absyn.ProcedureDec;
import edu.clemson.cs.r2jt.absyn.ProgramExp;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.absyn.VarDec;
import edu.clemson.cs.r2jt.absyn.VariableDotExp;
import edu.clemson.cs.r2jt.archiving.Archiver;
import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.translation.bookkeeping.Bookkeeper;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.EntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.typeandpopulate.NoSuchSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramQualifiedEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.RepresentationTypeEntry;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.OperationQuery;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author
 * Mark
 * T
 */
public abstract class AbstractTranslator extends TreeWalkerStackVisitor {

    protected static final boolean PRINT_DEBUG = true;
    protected final CompileEnvironment myEnv;
    protected final ModuleScope myScope;
    protected Bookkeeper myBookkeeper;
    protected ErrorHandler err;

    //Symbol described by language to indicate qualification (etc: Java's Stack_Fac.Pop vs C's Stack_Fac->Pop)
    protected String qualifyingSymbol;

    public AbstractTranslator(CompileEnvironment env, ModuleScope scope,
            ModuleDec dec, ErrorHandler err) {
        this.err = err;
        myEnv = env;
        myScope = scope;
    }

    //-------------------------------------------------------------------
    //   Visitor methods
    //-------------------------------------------------------------------

    /* Visit Statements */

    @Override
    public void preCallStmt(CallStmt stmt) {

        String callQualifier;
        String callString;
        AbstractTranslator.emitDebug("Encountered call: " + stmt.getName());

        if (stmt.getQualifier() != null) {
            callQualifier = stmt.getQualifier().getName();
            callString = callQualifier + qualifyingSymbol + stmt.toString();
        }
        else {
            callString = stmt.toString();
        }
        myBookkeeper.fxnAppendTo(callString + "(");
    }

    @Override
    public void midCallStmtArguments(CallStmt node, ProgramExp previous,
            ProgramExp next) {
        if (next != null && previous != null) {
            myBookkeeper.fxnAppendTo(", ");
        }
    }

    /* End Visit Statements */

    /* Visit Declarations */

    @Override
    public void preFacilityOperationDec(FacilityOperationDec dec) {
        String operName = dec.getName().getName();
        String returnType = "void";
        String facName, qualRetType;
        if (dec.getReturnTy() != null) {
            returnType = ((NameTy) dec.getReturnTy()).toString();
            facName = getTypeFacility((NameTy) dec.getReturnTy());
            qualRetType = facName + qualifyingSymbol + returnType;
        }
        else {
            qualRetType = returnType;
        }
        myBookkeeper.fxnAdd(qualRetType, operName);
    }

    @Override
    public void preProcedureDec(ProcedureDec dec) {
        String operName = dec.getName().getName();
        String returnType = "void";
        String facName, qualRetType;
        if (dec.getReturnTy() != null) {
            returnType = ((NameTy) dec.getReturnTy()).toString();
            facName = getTypeFacility((NameTy) dec.getReturnTy());
            qualRetType = facName + qualifyingSymbol + returnType;
        }
        else {
            qualRetType = returnType;
        }
        myBookkeeper.fxnAdd(qualRetType, operName);
    }

    @Override
    public void preVarDec(VarDec dec) {
        throw new UnsupportedOperationException(dec.getClass().getName()
                + "not yet supported");
    }

    @Override
    public void preParameterVarDec(ParameterVarDec dec) {
        throw new UnsupportedOperationException(dec.getClass().getName()
                + "not yet supported");
    }

    /* End Visit Declaration */
    //-------------------------------------------------------------------
    //   Helper methods
    //-------------------------------------------------------------------

    protected String getTypeFacility(NameTy type) {
        if (type.getTempQualifier() == null) {
            Logger.getLogger("GENERIC TYPE NOT YET IMPLEMENTED").log(
                    Level.SEVERE, null,
                    new UnsupportedOperationException("Not supported yet."));
            return "<GENERICTYPE>";
        }
        return type.getTempQualifier().getName();
    }

    protected String getTypeConceptName(String facilityName) {
        try {
            FacilityEntry fac =
                    myScope
                            .queryForOne(
                                    new NameQuery(
                                            null,
                                            facilityName,
                                            MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                                            MathSymbolTable.FacilityStrategy.FACILITY_IGNORE,
                                            true)).toFacilityEntry(null);
            return fac.getName();
        }
        catch (NoSuchSymbolException ex) {
            Logger.getLogger(CTranslator.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        catch (DuplicateSymbolException ex) {
            Logger.getLogger(CTranslator.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return null;
    }

    public static final void setUpFlags() {}

    public static void emitDebug(String msg) {
        if (PRINT_DEBUG) {
            System.out.println(msg);
        }
    }

    public void outputCode(File outputFile) {
        if (!myEnv.flags.isFlagSet(ResolveCompiler.FLAG_WEB)
                || myEnv.flags.isFlagSet(Archiver.FLAG_ARCHIVE)) {

            //   String code = Formatter.formatCode(myBookkeeper.output());
            //   System.out.println(code);
            // System.out.println(myBookkeeper.output());
        }
        else {
            outputToReport(myBookkeeper.output().toString());
        }
    }

    private void outputToReport(String fileContents) {
        String b = "Just want to breakpoint here.";
        CompileReport report = myEnv.getCompileReport();
        report.setTranslateSuccess();
        report.setOutput(fileContents);
    }
}
