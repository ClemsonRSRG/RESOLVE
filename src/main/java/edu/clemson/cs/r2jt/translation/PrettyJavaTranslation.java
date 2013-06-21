/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.archiving.Archiver;
import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.data.*;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.scope.OldSymbolTable;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.r2jt.utilities.Flag;
import java.io.File;

/**
 *
 * @author Mark T
 */
public class PrettyJavaTranslation extends TreeWalkerStackVisitor {

    /*
     * Variable Declaration
     */

    PrettyJavaTranslationInfo cInfo;
    private final CompileEnvironment env;
    private ErrorHandler err;
    private String targetFileName;
    private OldSymbolTable table;
    private boolean isMath;

    //Flags
    private static final String FLAG_SECTION_NAME = "Pretty Java Translation";

    private static final String FLAG_DESC_TRANSLATE =
            "Translates into "
                    + "a \"Pretty\" Java version following the line numbers of the "
                    + "RESOLVE Facility.";
    public static final Flag FLAG_PRETTY_JAVA_TRANSLATE =
            new Flag(FLAG_SECTION_NAME, "prettyJavaTranslate",
                    FLAG_DESC_TRANSLATE);

    /*
     * End of Variable Declaration
     */

    public PrettyJavaTranslation(CompileEnvironment env, OldSymbolTable table,
            ModuleDec dec, ErrorHandler err) {
        this.err = err;
        this.env = env;
        this.table = table;
        targetFileName = dec.getName().getFile().getName();
        cInfo =
                new PrettyJavaTranslationInfo(dec.getName().getFile().getName());
        isMath = false;
    }

    /*
     * Visitor Methods
     */

    @Override
    public void preExp(Exp exp) {
        //ResolveConceptualElement par = getParent();
        ResolveConceptualElement par = getAncestor(1);
        if (par instanceof FacilityOperationDec) {
            FacilityOperationDec parFac = (FacilityOperationDec) par;
            if (exp.equals(parFac.getRequires())
                    || exp.equals(parFac.getEnsures())) {
                System.out.println("HERE");
            }
        }
    }

    /*@Override
    public boolean walkExp(Exp exp) {
        
    }*/

    @Override
    public void preModuleDec(ModuleDec dec) {
    //Stuff done before dec trees
    }

    @Override
    public void postModuleDec(ModuleDec dec) {
    //Stuff done after end of dec trees
    }

    @Override
    public void preFacilityModuleDec(FacilityModuleDec dec) {}

    @Override
    public void postFacilityModuleDec(FacilityModuleDec dec) {}

    @Override
    /**
     * 
     */
    public void preFacilityOperationDec(FacilityOperationDec dec) {
        PosSymbol retTy = null;
        if (dec.getReturnTy() != null) {
            retTy = ((NameTy) dec.getReturnTy()).getName();
        }
        cInfo.addFunction(dec.getName(), retTy);
    }

    // TODO : Fix this. Requires pre-post procedures for 'resolve verify' tests. 
    @Override
    public void midFacilityOperationDec(FacilityOperationDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {
        if (prevChild != null && nextChild != null) {
            if (!(prevChild.equals(node.getRequires()))) {
                if (nextChild.equals(node.getRequires())) {
                    Exp req = (Exp) nextChild;
                    cInfo.increaseLineStatementBuffer(req.getLocation()
                            .getPos().getLine());
                    cInfo.appendToStmt("/*requires");
                }
                else if (nextChild.equals(node.getEnsures())) {
                    Exp en = (Exp) nextChild;
                    cInfo.increaseLineStatementBuffer(en.getLocation().getPos()
                            .getLine());
                    cInfo.appendToStmt("/*ensures");
                }
            }
            else {
                if (nextChild.equals(node.getEnsures())) {
                    Exp en = (Exp) nextChild;
                    cInfo.increaseLineStatementBuffer(en.getLocation().getPos()
                            .getLine());
                    cInfo.appendToStmt("ensures");
                }
                else {
                    cInfo.appendToStmt("*/");
                }
            }
        }
    }

    public boolean walkInfixExp(InfixExp exp) {
        cInfo.appendToStmt(exp.toString());
        return true;
    }

    @Override
    public void preParameterVarDec(ParameterVarDec dec) {
        StringBuilder parmSet = new StringBuilder();
        if (dec.getTy() instanceof NameTy) {
            parmSet.append(cInfo.getCVarsWithLines(((NameTy) dec.getTy())
                    .getName(), null));
        }
        else {
            System.out.println("How did you reach here?");
        }
        parmSet.append(" ").append(cInfo.stringFromSym(dec.getName(), null));
        cInfo.addParamToFunc(parmSet.toString());
    }

    //Function Call
    @Override
    public void preCallStmt(CallStmt stmt) {
        if (stmt.getName().getName().contains("Write")) {
            String out = "System.out.println(";
            cInfo.increaseLineStatementBuffer(stmt.getName().getLocation()
                    .getPos().getLine());
            cInfo.appendToStmt(out);
        }
        else {
            String a = cInfo.stringFromSym(stmt.getName(), null);
            cInfo.appendToStmt(a + "(");
        }
    }

    @Override
    public void midCallStmtArguments(CallStmt node, ProgramExp previous,
            ProgramExp next) {
        if (next != null && previous != null) {
            cInfo.appendToStmt(", ");
        }
    }

    @Override
    public void postCallStmt(CallStmt stmt) {
        cInfo.appendToStmt(");");
    }

    @Override
    public void preVarDec(VarDec dec) {
        /* Prevents preprocessor variables from entering final translation */
        if (!dec.getName().getName().startsWith("_")) {
            NameTy ty = (NameTy) dec.getTy();
            String stTy = ty.getName().getName();
            String[] retTy = cInfo.getVarType(stTy);
            cInfo.appendToFuncVarInit(cInfo.stringFromSym(dec.getName(),
                    retTy[0])
                    + retTy[1]);
        }
    }

    @Override
    public void preFuncAssignStmt(FuncAssignStmt stmt) {}

    @Override
    public void midFuncAssignStmt(FuncAssignStmt stmt,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {
        if (prevChild != null && nextChild != null) {
            cInfo.appendToStmt(" = ");
        }
    }

    @Override
    public void postFuncAssignStmt(FuncAssignStmt stmt) {
        cInfo.appendToStmt(";");
    }

    @Override
    public void preVarExp(VarExp exp) {
        cInfo.appendToStmt(cInfo.stringFromSym(exp.getName(), null));
    }

    @Override
    public void preVariableNameExp(VariableNameExp var) {
        String name = cInfo.stringFromSym(var.getName(), null);
        cInfo.appendToStmt(name);
    }

    @Override
    public void preProgramIntegerExp(ProgramIntegerExp exp) {
        int lin = exp.getLocation().getPos().getLine();
        cInfo.increaseLineStatementBuffer(lin);
        cInfo.appendToStmt(Integer.toString(exp.getValue()));
    }

    @Override
    public void preProgramParamExp(ProgramParamExp exp) {
        //function with return
        cInfo.appendToStmt(cInfo.stringFromSym(exp.getName(), null));
        cInfo.appendToStmt("(");
    }

    // have a boolean for this saying when your in a parameter expression list...
    // preProgramParamExpArguments
    // disregard.
    @Override
    public void midProgramParamExpArguments(ProgramParamExp node,
            ProgramExp previous, ProgramExp next) {
        if (next != null && previous != null) {
            cInfo.appendToStmt(", ");
        }
    }

    @Override
    public void postProgramParamExp(ProgramParamExp exp) {
        cInfo.appendToStmt(")");
    }

    @Override
    public void midProgramParamExp(ProgramParamExp exp,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {
        //Used for debug breakpoints
        System.out.println();
    }

    @Override
    public void preProgramParamExpArguments(ProgramParamExp node) {
        //Used for debug breakpoints
        System.out.println();
    }

    /*
     * https://www.pivotaltracker.com/story/show/37258073
     * This will skip over all children of ProgramFunctionExp
     */
    //  @Override
    public boolean walkProgramFunctionExp(ProgramFunctionExp exp) {
        return true;
    }

    /*
     * Statement visits, uses a single (global) buffer in cInfo.currentFunc.
     */
    @Override
    public void preIfStmt(IfStmt stmt) {
        int lin = stmt.getTest().getLocation().getPos().getLine();
        cInfo.appendToStmt(getNewLines(lin));
        cInfo.appendToStmt("if(");
    }

    @Override
    public void midIfStmt(IfStmt stmt, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {

    }

    @Override
    public void preIfStmtThenclause(IfStmt node) {
        cInfo.appendToStmt("){");
    }

    @Override
    public void postIfStmtThenclause(IfStmt node) {
        cInfo.appendToStmt("}");
    }

    @Override
    public void postIfStmt(IfStmt stmt) {}

    @Override
    public void preIfStmtElseclause(IfStmt stmt) {
        cInfo.appendToStmt(" else{ ");
    }

    @Override
    public void postIfStmtElseclause(IfStmt stmt) {
        cInfo.appendToStmt("} ");
    }

    @Override
    public void preWhileStmt(WhileStmt stmt) {
        int lin = stmt.getTest().getLocation().getPos().getLine();
        cInfo.appendToStmt(getNewLines(lin));
        cInfo.appendToStmt("while(");
    }

    @Override
    public void preWhileStmtStatements(WhileStmt stmt) {
        if ((stmt.getChanging().size() < 1) || stmt.getDecreasing() != null
                || stmt.getMaintaining() != null) {
            cInfo.appendToStmt("){");
        }
        else {
            cInfo.appendToStmt(" */");
        }
    }

    @Override
    public void preWhileStmtChanging(WhileStmt stmt) {
        cInfo.appendToStmt("){");
        int lin = stmt.getChanging().get(0).getLocation().getPos().getLine();
        cInfo.appendToStmt(getNewLines(lin));
        cInfo.appendToStmt("/* changing ");
        cInfo.appendToStmt(stmt.getChanging().toString());
    }

    public boolean walkWhileStmtChanging(WhileStmt stmt) {
        return true;
    }

    @Override
    public void midWhileStmt(WhileStmt node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {
        if (node.getMaintaining().equals(nextChild)) {
            Exp main = (Exp) nextChild;
            int lin = main.getLocation().getPos().getLine();
            cInfo.appendToStmt(getNewLines(lin));
            cInfo.appendToStmt("maintaining ");
        }
        else if (node.getDecreasing().equals(nextChild)) {
            Exp dec = (Exp) nextChild;
            int lin = dec.getLocation().getPos().getLine();
            cInfo.appendToStmt(getNewLines(lin));
            cInfo.appendToStmt("decreasing ");
        }
    }

    @Override
    public void postWhileStmt(WhileStmt stmt) {
        cInfo.appendToStmt(" }");
    }

    /*
     * End of Visitor Methods
     */

    public String output() {
        return cInfo.toString();
    }

    public void outputCode(File outputFile) {
        //Assume files have already been translated
        if (!env.flags.isFlagSet(ResolveCompiler.FLAG_WEB)
                || env.flags.isFlagSet(Archiver.FLAG_ARCHIVE)) {
            //outputAsFile(targetFileName, getMainBuffer());
            //outputAsFile(outputFile.getAbsolutePath(), getMainBuffer());
            System.out.println(output());
        }
        else {
            outputToReport(output());
        }
        //outputAsFile(getMainFileName(), getMainBuffer());
    }

    private void outputToReport(String fileContents) {
        CompileReport report = env.getCompileReport();
        report.setTranslateSuccess();
        report.setOutput(fileContents);
    }

    @Override
    public void midProgramOpExp(ProgramOpExp exp,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {
        if (prevChild != null && nextChild != null) {
            System.out.println("here");

            switch (exp.getOperator()) {
            case ProgramOpExp.AND:
                cInfo.appendToStmt(" && ");
                break;
            case ProgramOpExp.OR:
                cInfo.appendToStmt(" || ");
                break;
            case ProgramOpExp.EQUAL:
                cInfo.appendToStmt(" == ");
                break;
            case ProgramOpExp.NOT_EQUAL:
                cInfo.appendToStmt(" != ");
                break;
            case ProgramOpExp.LT:
                cInfo.appendToStmt(" < ");
                break;
            case ProgramOpExp.LT_EQL:
                cInfo.appendToStmt(" <= ");
                break;
            case ProgramOpExp.GT:
                cInfo.appendToStmt(" > ");
                break;
            case ProgramOpExp.GT_EQL:
                cInfo.appendToStmt(" >= ");
                break;
            case ProgramOpExp.PLUS:
                cInfo.appendToStmt(" + ");
                break;
            case ProgramOpExp.MINUS:
                cInfo.appendToStmt(" - ");
                break;
            case ProgramOpExp.MULTIPLY:
                cInfo.appendToStmt(" * ");
                break;
            case ProgramOpExp.DIVIDE:
                cInfo.appendToStmt(" / ");
                break;
            case ProgramOpExp.REM:
                cInfo.appendToStmt(" % ");
                break;
            case ProgramOpExp.MOD:
                cInfo.appendToStmt(" % ");
                break;
            case ProgramOpExp.DIV:
                cInfo.appendToStmt(" / ");
                break;
            case ProgramOpExp.NOT:
                cInfo.appendToStmt(" == 0");
                break;
            case ProgramOpExp.UNARY_MINUS:
                cInfo.appendToStmt(" * -1 ");
                break;
            default:
                break;
            }
        }
    }

    private String getNewLines(int position) {
        StringBuilder retString = new StringBuilder();
        int n = position - cInfo.lineCount;
        int count = 0;
        while (count < n) {
            retString.append("\n");
            count++;
            cInfo.lineCount++;
        }
        return retString.toString();
    }

    /*public String outputCode() {
        return cInfo.toString();
    }*/

    public static final void setUpFlags() {

    }

}
