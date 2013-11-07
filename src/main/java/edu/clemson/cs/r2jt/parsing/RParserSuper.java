/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.parsing;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import edu.clemson.cs.r2jt.data.*;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.collections.List;

/**
 *
 * @author Mark T
 */
public class RParserSuper extends Parser {

    public RParserSuper(TokenStream input) {
        this(input, new RecognizerSharedState());
    }

    public RParserSuper(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    /*
     * Variables to tell us what type of module we are
     * parsing.  Used for semantic predicates of rules or productions
     * which are only applicable to particular modules.
     */
    protected boolean theoryModule = false;
    protected boolean conceptModule = false;
    protected boolean bodyModule = false;
    protected boolean enhancementModule = false;
    protected boolean facilityModule = false;
    protected boolean performanceEModule = false;
    protected boolean performanceCModule = false;

    /**
     * Reset the type of module we are parsing.
     */
    public void resetModuleType() {
        this.theoryModule = false;
        this.conceptModule = false;
        this.performanceEModule = false;
        this.performanceCModule = false;
        this.bodyModule = false;
        this.enhancementModule = false;
        this.facilityModule = false;
    }

    //protected ErrorHandler err = ErrorHandler.getInstance();
    protected ErrorHandler err;

    protected boolean otherwise = false;

    protected void checkOtherwiseItem(Tree ast) {
        if (otherwise) {
            String msg =
                    "Cannot add an alternative after "
                            + "an \"otherwise\" clause.";
            err.error(getPos(ast), msg);
        }
    }

    protected void checkIndexedIdent(Tree ast) {
        if (!ast.getText().equals("i") && !ast.getText().equals("ii")) {
            String msg = "Expecting i or ii, found " + ast.getText();
            err.error(getPos(ast), msg);
        }
    }

    protected void checkTimesIdent(Token ast) {
        if (!ast.getText().equals("x")) {
            String msg = "Expecting x or times, found " + ast.getText();
            err.error(getPos(ast), msg);
        }
    }

    protected void checkIteratedIdent(Tree ast) {
        if (!ast.getText().equals("Sum") && !ast.getText().equals("Product")
                && !ast.getText().equals("Concatenation")
                && !ast.getText().equals("Intersection")
                && !ast.getText().equals("Union")) {
            String msg =
                    "Expecting iteration identifier "
                            + "(Sum, Product, Concatenation, Intersection, Union),"
                            + "but found " + ast.getText();
            err.error(getPos(ast), msg);
        }
    }

    protected boolean facInit = false;
    protected boolean facFinal = false;

    protected void checkFacInit(Tree ast) {
        if (facInit) {
            String msg = "Cannot redefine facility initialization.";
            err.error(getPos(ast), msg);
        }
        else {
            facInit = true;
        }
    }

    protected void checkFacFinal(Tree ast) {
        if (facFinal) {
            String msg = "Cannot redefine facility finalization.";
            err.error(getPos(ast), msg);
        }
        else {
            facFinal = true;
        }
    }

    public String getErrorMessage(RecognitionException e, String[] tokenNames) {
        java.util.List stack =
                (java.util.List) getRuleInvocationStack(e, this.getClass()
                        .getName());
        String msg = null;
        if (e instanceof NoViableAltException) {
            NoViableAltException nvae = (NoViableAltException) e;
            msg =
                    " no viable alt; token=" + e.token + " (decision="
                            + nvae.decisionNumber + " state "
                            + nvae.stateNumber + ")" + " input " + nvae.input
                            + ")" + " decision=<<"
                            + nvae.grammarDecisionDescription + ">>";
        }
        if (e instanceof MismatchedTokenException) {
            MismatchedTokenException mte = (MismatchedTokenException) e;
            String exp = null;
            if (mte.expecting == Token.EOF) {
                exp = "EOF";
            }
            else {
                exp = tokenNames[mte.expecting];
            }
            msg = "expecting " + exp + ", found '" + mte.token.getText() + "'";
        }
        else {
            msg = super.getErrorMessage(e, tokenNames);
        }

        // For debugging changes to the grammar change this to return
        // both the stack (lists the rules visited) and the msg
        return msg;
        //return "Parser: "+stack + " " + msg;
    }

    public String getTokenErrorDisplay(Token t) {
        return t.toString();
    }

    /** Delegate the error handling to the error handler. */
    public void reportError(RecognitionException ex) {
        err.error(getPos(ex.token), getErrorMessage(ex, RParser.tokenNames));
        //System.out.println(getErrorMessage(ex, RParser.tokenNames));
        //err.error(ex);
    }

    /** Delegate the warning handling to the error handler. */
    public void reportWarning(String s) {
        err.warning(s);
    }

    protected void matchModuleIdent(Tree id2, Tree id1) {
        if (!id1.getText().equals(id2.getText())) {
            String msg =
                    "End name " + id2.getText()
                            + " does not match module name " + id1.getText();
            err.error(getPos(id2), msg);
        }
    }

    /*protected void matchModuleIdent(ColsAST id2, ColsAST id1) {
        if (!id1.getText().equals(id2.getText())) { 
            String msg = "End name " + id2.getText() +
            " does not match module name " + id1.getText();
            err.error(getPos(id2), msg);
        }  
    }*/

    protected void matchOperationIdent(Tree id2, Tree id1) {
        if (!id1.getText().equals(id2.getText())) {
            String msg =
                    "End name " + id2.getText()
                            + " does not match operation name " + id1.getText();
            err.error(getPos(id2), msg);
        }
    }

    protected void matchMathItemIdent(Tree id2, Tree id1) {
        if (!id1.getText().equals(id2.getText())) {
            String msg =
                    "End name " + id2.getText() + " does not match proof name "
                            + id1.getText();
            err.error(getPos(id2), msg);
        }
    }

    protected Pos getPos(Tree ast) {
        //return new Pos(ast.getLine(), ast.getColumn());
        return new Pos(ast.getLine(), ast.getCharPositionInLine());
    }

    protected Pos getPos(Token ast) {
        //return new Pos(ast.getLine(), ast.getColumn());
        return new Pos(ast.getLine(), ast.getCharPositionInLine());
    }

    /*public void recover(RecognitionException ex, BitSet bs) throws TokenStreamException {
      try {
          consume();
          consumeUntil(bs);
      }
      catch (TokenStreamException tsex) {
            throw tsex;
      } 
    }*/

    protected boolean isDeductionToken(String testStr) {
        if (testStr.equals("deduction") || testStr.equals("Deduction")) {
            return true;
        }
        return false;
    }
}
