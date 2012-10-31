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
import edu.clemson.cs.r2jt.scope.SymbolTable;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.r2jt.utilities.Flag;
import java.io.File;

/**
 *
 * @author Mark T
 */
public class PrettyCTranslation extends TreeWalkerStackVisitor {

    /*
     * Variable Declaration
     */

    PrettyCTranslationInfo cInfo;
    private final CompileEnvironment env;
    private ErrorHandler err;
    private String targetFileName;
    private SymbolTable table;
    private boolean isMath;

    //Flags
    private static final String FLAG_SECTION_NAME = "Pretty C Translation";

    private static final String FLAG_DESC_TRANSLATE =
            "Translates into "
                    + "a \"Pretty\" C version following the line numbers of the "
                    + "RESOLVE Facility.";
    public static final Flag FLAG_PRETTY_C_TRANSLATE =
            new Flag(FLAG_SECTION_NAME, "prettyctranslate", FLAG_DESC_TRANSLATE);

    //Global stmt buf
    StringBuffer stmtBuf;

    /*
     * End of Variable Declaration
     */

    public PrettyCTranslation(CompileEnvironment env, SymbolTable table,
            ModuleDec dec, ErrorHandler err) {
        this.err = err;
        this.env = env;
        this.table = table;
        targetFileName = dec.getName().getFile().getName();
        cInfo = new PrettyCTranslationInfo(dec.getName().getFile().getName());
        stmtBuf = new StringBuffer();
        isMath = false;
    }

    /*
     * Visitor Methods
     */

    @Override
    public void preModuleDec(ModuleDec dec) {
    //Stuff done before start of dec trees
    }

    @Override
    public void postModuleDec(ModuleDec dec) {
    //Stuff done after end of dec trees
    }

    @Override
    public void preFacilityModuleDec(FacilityModuleDec dec) {
    //int a = 0;
    }

    @Override
    public void postFacilityModuleDec(FacilityModuleDec dec) {}

    @Override
    /**
     * 
     */
    public void preFacilityOperationDec(FacilityOperationDec dec) {
        NameTy retTy = null;
        if (dec.getReturnTy() != null) {
            retTy = (NameTy) dec.getReturnTy();
            cInfo.addFunction(dec.getName(), retTy.getName());
        }
        else {
            cInfo.addFunction(dec.getName(), null);
        }
    }

    // TODO : Fix this. Requires pre-post procedures for 'resolve verify' tests. 
    @Override
    public void midFacilityOperationDec(FacilityOperationDec node,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {
        if (prevChild != null && nextChild != null) {
            if (!(prevChild.equals(node.getEnsures()) || prevChild.equals(node
                    .getRequires()))) {
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
                if (nextChild.equals(node.getRequires())) {
                    Exp req = (Exp) nextChild;
                    cInfo.increaseLineStatementBuffer(req.getLocation()
                            .getPos().getLine());
                    cInfo.appendToStmt("requires");
                }
                else if (nextChild.equals(node.getEnsures())) {
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
    public void preCallStmt(CallStmt stmt) {
        String a = cInfo.stringFromSym(stmt.getName(), null);
        cInfo.addToStmts(a + "()");
        cInfo.appendToStmt(a + "();");
    }

    public void preVarDec(VarDec dec) {
        PosSymbol name = dec.getName();

        if (!name.getName().startsWith("_")) {
            NameTy ty = (NameTy) dec.getTy();
            String stTy = ty.getName().getName();
            String newTy, init;
            if (stTy.equals("Integer")) {
                newTy = "int";
                init = "= 0";
            }
            else if (stTy.equals("Char_Str")) {
                newTy = "char*";
                init = "";
            }
            else if (stTy.equals("Boolean")) {
                newTy = "int";
                init = "= 0";
            }
            else if (stTy.equals("Character")) {
                newTy = "char";
                init = "= /0";
            }
            else {
                newTy = "<empty>";
                init = " = NULL";
            }
            String[] retTy = cInfo.getCVarType(stTy);
            cInfo.appendToFuncVarInit(cInfo.stringFromSym(dec.getName(),
                    retTy[0])
                    + retTy[1]);
        }
    }

    @Override
    public void preFuncAssignStmt(FuncAssignStmt stmt) {
        stmtBuf = new StringBuffer();
    }

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
        //cInfo.addToStmts(stmtBuf.toString());
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

    @Override
    public void postProgramParamExp(ProgramParamExp exp) {
        cInfo.appendToStmt(")");
    }

    @Override
    public void midProgramParamExp(ProgramParamExp exp,
            ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild) {
        System.out.println();
    }

    @Override
    public void preProgramParamExpArguments(ProgramParamExp node) {
        System.out.println();
    }

    /*
     * https://www.pivotaltracker.com/story/show/37258073
     */
    @Override
    public void preProgramFunctionExp(ProgramFunctionExp exp) {
        cInfo.appendToStmt("/*");
    }

    @Override
    public void postProgramFunctionExp(ProgramFunctionExp exp) {
        cInfo.appendToStmt("*/");
    }

    /*
     * Statement walks
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
        if (stmt.getChanging().size() < 1) {
            cInfo.appendToStmt("){");
        }
        else
            cInfo.appendToStmt(" */");
    }

    public void preWhileStmtChanging(WhileStmt stmt) {
        cInfo.appendToStmt("){");
        int lin = stmt.getChanging().get(0).getLocation().getPos().getLine();
        cInfo.appendToStmt(getNewLines(lin));
        cInfo.appendToStmt("/* changing ");
    }

    public void postWhileStmtChanging(WhileStmt stmt) {}

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

    public String outputCodeOld() {
        return cInfo.toString();
    }

    public static final void setUpFlags() {

    }

}
