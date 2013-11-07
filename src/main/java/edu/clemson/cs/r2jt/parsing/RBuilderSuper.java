/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.parsing;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.*;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.type.Type;
import edu.clemson.cs.r2jt.type.IsInType;
import edu.clemson.cs.r2jt.type.BooleanType;
import edu.clemson.cs.r2jt.collections.List;

/**
 *
 * @author Mark T
 */
public class RBuilderSuper extends TreeParser {

    public RBuilderSuper(TreeNodeStream input) {
        this(input, new RecognizerSharedState());
    }

    public RBuilderSuper(TreeNodeStream input, RecognizerSharedState state) {
        super(input, state);
    }

    /**
     * Variables to tell us what type of module we are
     * parsing.  Used for semantic predicates of rules or productions
     * which are only applicable to particular modules.
     */
    protected boolean proofModule = false;
    protected boolean theoryModule = false;
    protected boolean conceptModule = false;
    protected boolean headerModule = false;
    protected boolean bodyModule = false;
    protected boolean enhancementModule = false;
    protected boolean facilityModule = false;
    protected boolean enhancementBody = false;
    protected boolean performanceEModule = false;
    protected boolean performanceCModule = false;

    /* enhancementBody is a subclass of bodyModule.  It is only true
     * in the body of an enhancement module.  It is NOT true in a
     * "bundled implementation" module (a body that implements both a
     * concept and one or more enhancements at once). (BM)
     */

    /**
     * Reset the type of module we are parsing.
     */
    public void resetModuleType() {
        this.theoryModule = false;
        this.conceptModule = false;
        this.headerModule = false;
        this.bodyModule = false;
        this.enhancementModule = false;
        this.facilityModule = false;
        this.enhancementBody = false;
        this.performanceEModule = false;
        this.performanceCModule = false;
    }

    /** The error handler for this parser. */
    protected ErrorHandler err;

    //protected ErrorHandler err = ErrorHandler.getInstance();

    /** Delegate the error handling to the error handler. */
    public void reportError(RecognitionException ex) {
        System.out.println(getErrorMessage(ex, null));
        err.syntaxError(ex);
    }

    /** Delegate the warning handling to the error handler. */
    public void reportWarning(String s) {
        err.warning(s);
    }

    //      protected PosSymbol getQualifier(edu.clemson.cs.r2jt.collections.List<PosSymbol> psyms) {
    //          PosSymbol qual = null;
    //          switch (psyms.size()) {
    //          case 1: qual = null; break;
    //          case 2: qual = psyms.get(0); break;
    //          default: assert false : "qual is invalid";
    //          }
    //          return qual;
    //      }

    //      protected PosSymbol getName(edu.clemson.cs.r2jt.collections.List<PosSymbol> psyms) {
    //          PosSymbol name = null;
    //          switch (psyms.size()) {
    //          case 1: name = psyms.get(0); break;
    //          case 2: name = psyms.get(1); break;
    //          default: assert false : "psyms is invalid";
    //          }
    //          return name;
    //      }

    protected Pos getPos(ColsAST ast) {
        return new Pos(ast.getLine(), ast.getCharPositionInLine());
    }

    protected Location getLocation(ColsAST ast) {
        return new Location(err.getFile(), getPos(ast));
    }

    protected Location getLocation(Pos pos) {
        return new Location(err.getFile(), pos);
    }

    protected Symbol getSymbol(ColsAST ast) {
        return Symbol.symbol(ast.getText());
    }

    protected Pos getASTPos(ColsAST ast) {
        return new Pos(ast.getLine(), ast.getCharPositionInLine());
    }

    protected Symbol getASTSymbol(ColsAST ast) {
        return Symbol.symbol(ast.getText());
    }

    protected PosSymbol getOutfixPosSymbol(ColsAST ast) {
        Pos pos = new Pos(ast.getLine(), ast.getCharPositionInLine());
        Location loc = new Location(err.getFile(), pos);
        String str = ast.getText();
        Symbol name = null;
        if (str.equals("<")) {
            name = Symbol.symbol("<_>");
        }
        else if (str.equals("<<")) {
            name = Symbol.symbol("<<_>>");
        }
        else if (str.equals("|")) {
            name = Symbol.symbol("|_|");
        }
        else if (str.equals("||")) {
            name = Symbol.symbol("||_||");
        }
        else if (str.equals("[")) {
            name = Symbol.symbol("[_]");
        }
        else if (str.equals("[[")) {
            name = Symbol.symbol("[[_]]");
        }
        else {
            assert false : "invalid symbol: " + str;
        }
        return new PosSymbol(loc, name);
    }

    protected PosSymbol getPosSymbol(ColsAST ast) {
        Pos pos = new Pos(ast.getLine(), ast.getCharPositionInLine());
        Location loc = new Location(err.getFile(), pos);
        Symbol sym = Symbol.symbol(ast.getText());
        return new PosSymbol(loc, sym);
    }

    protected edu.clemson.cs.r2jt.collections.List<ParameterVarDec> getParamVarDecList(
            Mode mode, edu.clemson.cs.r2jt.collections.List<VarDec> vars) {
        edu.clemson.cs.r2jt.collections.List<ParameterVarDec> pVars =
                new edu.clemson.cs.r2jt.collections.List<ParameterVarDec>(
                        "ParameterVarDec");
        Iterator<VarDec> i = vars.iterator();
        while (i.hasNext()) {
            VarDec var = i.next();
            ParameterVarDec pVar =
                    new ParameterVarDec(mode, var.getName(), var.getTy());
            pVars.add(pVar);
        }
        return pVars;
    }

    protected InitItem getInitItem(Location loc, InitItem init) {
        return new InitItem(loc, init.getStateVars(), init.getRequires(), init
                .getEnsures(), init.getFacilities(), init.getVariables(), init
                .getAuxVariables(), init.getStatements());
    }

    protected FinalItem getFinalItem(Location loc, InitItem init) {
        return new FinalItem(loc, init.getStateVars(), init.getRequires(), init
                .getEnsures(), init.getFacilities(), init.getVariables(), init
                .getAuxVariables(), init.getStatements());
    }

    protected PerformanceInitItem getPerformanceInitItem(Location loc,
            PerformanceInitItem init) {
        return new PerformanceInitItem(loc, init.getStateVars(), init
                .getRequires(), init.getEnsures(), init.getDuration(), init
                .getMainp_disp(), init.getFacilities(), init.getVariables(),
                init.getAuxVariables(), init.getStatements());
    }

    protected PerformanceFinalItem getPerformanceFinalItem(Location loc,
            PerformanceFinalItem Final) {
        return new PerformanceFinalItem(loc, Final.getStateVars(), Final
                .getRequires(), Final.getEnsures(), Final.getDuration(), Final
                .getMainp_disp(), Final.getFacilities(), Final.getVariables(),
                Final.getAuxVariables(), Final.getStatements());
    }

    protected edu.clemson.cs.r2jt.collections.List<VarDec> getVarDecList(
            edu.clemson.cs.r2jt.collections.List<PosSymbol> psyms, Ty ty) {
        edu.clemson.cs.r2jt.collections.List<VarDec> vars =
                new edu.clemson.cs.r2jt.collections.List<VarDec>("VarDec");
        Iterator<PosSymbol> i = psyms.iterator();
        while (i.hasNext()) {
            PosSymbol ps = i.next();
            VarDec var = new VarDec(ps, ty);
            vars.add(var);
        }
        return vars;
    }

    protected edu.clemson.cs.r2jt.collections.List<AuxVarDec> getAuxVarDecList(
            edu.clemson.cs.r2jt.collections.List<PosSymbol> psyms, Ty ty) {
        edu.clemson.cs.r2jt.collections.List<AuxVarDec> vars =
                new edu.clemson.cs.r2jt.collections.List<AuxVarDec>("AuxVarDec");
        Iterator<PosSymbol> i = psyms.iterator();
        while (i.hasNext()) {
            PosSymbol ps = i.next();
            AuxVarDec var = new AuxVarDec(ps, ty);
            vars.add(var);
        }
        return vars;
    }

    protected edu.clemson.cs.r2jt.collections.List<MathVarDec> getMathVarDecList(
            edu.clemson.cs.r2jt.collections.List<PosSymbol> psyms, Ty ty) {
        edu.clemson.cs.r2jt.collections.List<MathVarDec> vars =
                new edu.clemson.cs.r2jt.collections.List<MathVarDec>(
                        "MathVarDec");
        Iterator<PosSymbol> i = psyms.iterator();
        while (i.hasNext()) {
            PosSymbol ps = i.next();
            MathVarDec var = new MathVarDec(ps, ty);
            vars.add(var);
        }
        return vars;
    }

    protected int getIterativeOp(PosSymbol ps) {
        Symbol sym = ps.getSymbol();
        int op = 0;
        if (sym == Symbol.symbol("Sum")) {
            op = IterativeExp.SUM;
        }
        else if (sym == Symbol.symbol("Product")) {
            op = IterativeExp.PRODUCT;
        }
        else if (sym == Symbol.symbol("Concatenation")) {
            op = IterativeExp.CONCATENATION;
        }
        else if (sym == Symbol.symbol("Intersection")) {
            op = IterativeExp.INTERSECTION;
        }
        else if (sym == Symbol.symbol("Union")) {
            op = IterativeExp.UNION;
        }
        else {
            assert false : "Invalid symbol: " + sym;
        }
        return op;
    }

    protected ProgramExp getProgramLiteral(Exp mlit) {
        if (mlit instanceof IntegerExp) {
            return new ProgramIntegerExp(((IntegerExp) mlit).getLocation(),
                    ((IntegerExp) mlit).getValue());
        }
        else if (mlit instanceof DoubleExp) {
            return new ProgramDoubleExp(((IntegerExp) mlit).getLocation(),
                    ((IntegerExp) mlit).getValue());
        }
        else if (mlit instanceof CharExp) {
            return new ProgramCharExp(((CharExp) mlit).getLocation(),
                    ((CharExp) mlit).getValue());
        }
        else if (mlit instanceof StringExp) {
            return new ProgramStringExp(((StringExp) mlit).getLocation(),
                    ((StringExp) mlit).getValue());
        }
        else {
            assert false : "Invalid expression type";
            return null;
        }
    }

    public String getErrorMessage(RecognitionException e, String[] tokenNames) {
        System.out.println("Builder Exception:");
        java.util.List stack =
                (java.util.List) getRuleInvocationStack(e, this.getClass()
                        .getName());
        String msg = null;
        if (e instanceof NoViableAltException) {
            NoViableAltException nvae = (NoViableAltException) e;
            msg =
                    " no viable alt; token=" + e.token + " (decision="
                            + nvae.decisionNumber + " state "
                            + nvae.stateNumber + ")" + " decision=<<"
                            + nvae.grammarDecisionDescription + ">>";
        }
        else {
            msg = super.getErrorMessage(e, RBuilder.tokenNames);
        }
        return stack + " " + msg + "\n" + e.token;
        //return msg;
    }

    public String getTokenErrorDisplay(Token t) {
        return t.toString();
    }
}
