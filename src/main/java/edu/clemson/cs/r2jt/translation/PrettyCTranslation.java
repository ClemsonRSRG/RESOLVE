/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.*;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.scope.SymbolTable;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.r2jt.utilities.Flag;

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
    public void postFacilityModuleDec(FacilityModuleDec dec) {
        System.out.println("Debug");
    }

    @Override
    /**
     * 
     */
    public void preFacilityOperationDec(FacilityOperationDec dec) {
        NameTy retTy = null;
        if (dec.getReturnTy() != null) {
            retTy = (NameTy) dec.getReturnTy();
        }
        cInfo.addFunction(dec.getName(), retTy.getName());
    }

    @Override
    public void preParameterVarDec(ParameterVarDec dec) {
        StringBuilder parmSet = new StringBuilder();
        if (dec.getTy() instanceof NameTy) {
            parmSet.append(cInfo.getCVarsWithLines(
                    ((NameTy) dec.getTy()).getName(), null));
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
            else{
                newTy = "<empty>";
                init = " = NULL";
            }
            String[] retTy = cInfo.getCVarType(stTy);
            cInfo.appendToFuncVarInit(cInfo.stringFromSym(dec.getName(), retTy[0])
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
        if (prevChild != null && nextChild != null){
            stmtBuf.append(" = ");
            cInfo.appendToStmt(" = ");
        }
    }

    @Override
    public void postFuncAssignStmt(FuncAssignStmt stmt) {
        //cInfo.addToStmts(stmtBuf.toString());
        cInfo.appendToStmt(";");
    }

    @Override
    public void preVariableNameExp(VariableNameExp var) {
        String name = cInfo.stringFromSym(var.getName(), null);
        stmtBuf.append(name);
        cInfo.appendToStmt(name);
    }

    @Override
    public void preProgramIntegerExp(ProgramIntegerExp exp) {
        stmtBuf.append(exp.getValue());
        cInfo.appendToStmt(Integer.toString(exp.getValue()));
    }

    @Override
    public void preProgramParamExp(ProgramParamExp exp) {
    //function with return
        System.out.println("out here");
    }

    
    @Override
    public void preIfStmt(IfStmt stmt){
        stmtBuf = new StringBuffer();
        stmtBuf.append("if(");
        cInfo.appendToStmt("if(");
    }
    
    @Override
    public void midIfStmt(IfStmt stmt, ResolveConceptualElement prevChild,
            ResolveConceptualElement nextChild){
        
    }
    
    @Override
    public void preIfStmtThenclause(IfStmt node){
        stmtBuf.append("){");
        cInfo.appendToStmt("){");
    }
    
    @Override
    public void postIfStmtThenclause(IfStmt node){
        stmtBuf.append("}");
        cInfo.appendToStmt("}");
    }
    
    @Override
    public void postIfStmt(IfStmt stmt){
        cInfo.addToStmts(stmtBuf.toString());
    }
    /*
     * End of Visitor Methods
     */

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
    
    public String outputCode(){
        return cInfo.toString();
    }

    public static final void setUpFlags() {

    }

}
