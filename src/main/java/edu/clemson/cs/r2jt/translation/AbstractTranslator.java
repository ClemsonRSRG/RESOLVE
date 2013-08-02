package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.archiving.Archiver;
import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.translation.bookkeeping.Bookkeeper;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.typeandpopulate.NoSuchSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameQuery;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark T
 */
public abstract class AbstractTranslator extends TreeWalkerStackVisitor {

    protected static final boolean PRINT_DEBUG = true;
    protected final CompileEnvironment myEnv;
    protected final ModuleScope myScope;
    protected Bookkeeper myBookkeeper;
    protected ErrorHandler err;

    /**
     * <p>Indicates a language-specific qualification style.
     * For example, StackFac.Pop vs StackFac->Pop.</p>
     */
    protected String myQualSymbol;

    protected String myCurrentVarDeclaration;

    public AbstractTranslator(CompileEnvironment env, ModuleScope scope,
            ModuleDec dec, ErrorHandler err) {
        this.err = err;
        myEnv = env;
        myScope = scope;
    }

    // -----------------------------------------------------------
    //   Visitor methods
    // -----------------------------------------------------------

    @Override
    public void preCallStmt(CallStmt stmt) {

        String callName = stmt.getName().getName();
        String callQual;

        if (stmt.getQualifier() != null) {
            callQual = stmt.getQualifier().getName();
            callName = callQual + myQualSymbol + callName;
        }
        myBookkeeper.fxnAppendTo(callName);
		
		AbstractTranslator.emitDebug("Encountered call: '" + stmt.getName());

    }

    @Override
    public void midCallStmtArguments(CallStmt node, ProgramExp previous,
            ProgramExp next) {
        if (next != null && previous != null) {
            myBookkeeper.fxnAppendTo(", ");
        }
    }

    @Override
    public void postCallStmt(CallStmt stmt) {
        myBookkeeper.fxnAppendTo(");");
    }

    @Override
    public void preIfStmt(IfStmt stmt) {
        myBookkeeper.fxnAppendTo("if(");
    }

    @Override
    public void preIfStmtThenclause(IfStmt stmt) {
        myBookkeeper.fxnAppendTo(") {");
    }

    @Override
    public void postIfStmtThenclause(IfStmt node) {
        myBookkeeper.fxnAppendTo("}");
    }

    @Override
    public void preWhileStmt(WhileStmt stmt) {
        myBookkeeper.fxnAppendTo("while(");
    }

    @Override
    public void preWhileStmtStatements(WhileStmt stmt) {
        myBookkeeper.fxnAppendTo(") {");
    }

    @Override
    public void postWhileStmt(WhileStmt stmt) {
        myBookkeeper.fxnAppendTo("}");
    }

    /*     @Override
         public void preFacilityDec(FacilityDec dec) {
             myBookkeeper.facAdd(dec.getName().getName(), dec.getConceptName()
                     .getName(), dec.getBodyName().getName());
         }

         @Override
         public void preEnhancementBodyItem(EnhancementBodyItem item) {
             myBookkeeper.facAddEnhance(item.getName().getName(), item.getBodyName()
                     .getName());
         }

         @Override
         public void preModuleArgumentItem(ModuleArgumentItem item) {
             String param = "CONSTANTTYPENEEDSLANGUAGESPECIFIC";
             if (item.getName() != null) {
                 if (item.getQualifier() != null) {
                     param = item.getQualifier() + " . " + item.getName().getName(); //qual
                 }
                 else {
                     param = "UNQUALIFIED." + item.getName().getName();
                 }
             }
             if (myBookkeeper.facEnhanceIsOpen()) {
                 myBookkeeper.facAddEnhanceParam(param);
             }
             else {
                 myBookkeeper.facAddParam(param);
             }
         }

         @Override
         public void postEnhancementBodyItem(EnhancementBodyItem item) {
             myBookkeeper.facEnhanceEnd();
         }

         @Override
         public void postFacilityDec(FacilityDec dec) {
             myBookkeeper.facEnd();
         }
     */

    // TODO : MAKE IT SO YOU CAN ADD IN A POSSYMBOL OR A STRING.. IT JUST
    // CONVERTS THE POSSYMBOL BEHIND THE SCENES... 

    @Override
    public void preFacilityOperationDec(FacilityOperationDec dec) {

        String returnType = "void";
        String returnSpec;

        if (dec.getReturnTy() != null) {
            NameTy ty = (NameTy) dec.getReturnTy();
            returnSpec =
                    getFacilitySpecification(ty.getTempQualifier().toString());
            returnType = returnSpec + myQualSymbol + ty.getName().getName();
        }

        myBookkeeper.fxnAdd(returnType, dec.getName().getName());
    }

    /*   @Override
       public void preProcedureDec(ProcedureDec dec) {
           String operName = dec.getName().getName();
    	  String returnTypeName = ((NameTy) dec.getTy()).getName().getName();
    	  String returnTypeSpec = getSpecification(varQual);
      
    	  
    	  String typeSpec = getSpecification(varQual);
           String returnType = "void";
           String facName, qualRetType;
           if (dec.getReturnTy() != null) {
               returnType = ((NameTy) dec.getReturnTy()).toString();
               facName = getFacilitySpecification((NameTy) dec.getReturnTy());
               qualRetType = facName + myQualSymbol + returnType;
           }
           else {
               qualRetType = returnType;
           }
           myBookkeeper.fxnAdd(qualRetType, operName);
       }*/

    @Override
    public void preVarDec(VarDec dec) {

        String varName = dec.getName().getName();
        String varQual = ((NameTy) dec.getTy()).getTempQualifier().toString();
        String typeName = ((NameTy) dec.getTy()).getName().getName();
        String typeSpec = getFacilitySpecification(varQual);

        String lhs = typeSpec + myQualSymbol + typeName + " " + varName;
        String rhs = " = " + varQual + myQualSymbol + "create" + typeName;

        // Ignore preprocessor variables (_Integer, _Boolean, etc)
        if (!varName.startsWith("_")) {

            AbstractTranslator.emitDebug("\tAdding " + typeName + ": '"
                    + dec.getName().getName() + "' with qualifier: '" + varQual
                    + "' and specification: '" + typeSpec
                    + "' to the Bookkeeper.");

            myBookkeeper.fxnAddVariableDeclaration(lhs + rhs + "();");
        }
    }

    @Override
    public void preParameterVarDec(ParameterVarDec dec) {
        throw new UnsupportedOperationException(dec.getClass().getName()
                + " not yet supported");
    }

    // -----------------------------------------------------------
    //   Helper methods
    // -----------------------------------------------------------

    protected String getFacilitySpecification(String facilityName) {
        String result = "";
        try {
            FacilityEntry fe =
                    myScope
                            .queryForOne(
                                    new NameQuery(
                                            null,
                                            facilityName,
                                            MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                                            MathSymbolTable.FacilityStrategy.FACILITY_IGNORE,
                                            true)).toFacilityEntry(null);

            result =
                    fe.getFacility().getSpecification().getModuleIdentifier()
                            .toString();
        }
        catch (NoSuchSymbolException ex) {
            Logger.getLogger(CTranslator.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        catch (DuplicateSymbolException ex) {
            Logger.getLogger(CTranslator.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return result;
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

            String code = Formatter.formatCode(myBookkeeper.output());
            System.out.println(code);
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
