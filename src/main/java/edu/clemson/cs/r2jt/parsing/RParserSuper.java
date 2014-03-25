/**
 * RParserSuper.java
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
package edu.clemson.cs.r2jt.parsing;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import edu.clemson.cs.r2jt.data.*;
import edu.clemson.cs.r2jt.errors.ErrorHandler;

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

    protected void checkOtherwiseItem(RuleReturnScope ast) {
        CommonTree astTree = (CommonTree) ast.getTree();
        if (otherwise) {
            String msg =
                    "Cannot add an alternative after "
                            + "an \"otherwise\" clause.";
            err.error(getPos(astTree), msg);
        }
    }

    protected void checkIndexedIdent(RuleReturnScope ast) {
        CommonTree astTree = (CommonTree) ast.getTree();
        if (!astTree.getText().equals("i") && !astTree.getText().equals("ii")) {
            String msg = "Expecting i or ii, found " + astTree.getText();
            err.error(getPos(astTree), msg);
        }
    }

    protected void checkTimesIdent(Token ast) {
        if (!ast.getText().equals("x")) {
            String msg = "Expecting x or times, found " + ast.getText();
            err.error(getPos(ast), msg);
        }
    }

    protected void checkIteratedIdent(RuleReturnScope ast) {
        CommonTree astTree = (CommonTree) ast.getTree();
        if (!astTree.getText().equals("Sum")
                && !astTree.getText().equals("Product")
                && !astTree.getText().equals("Concatenation")
                && !astTree.getText().equals("Intersection")
                && !astTree.getText().equals("Union")) {
            String msg =
                    "Expecting iteration identifier "
                            + "(Sum, Product, Concatenation, Intersection, Union),"
                            + "but found " + astTree.getText();
            err.error(getPos(astTree), msg);
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
        err.error(getPos(ex.token), getErrorMessage(ex,
                edu.clemson.cs.r2jt.parsing.RParser.tokenNames));
    }

    /** Delegate the warning handling to the error handler. */
    public void reportWarning(String s) {
        err.warning(s);
    }

    protected void matchModuleIdent(RuleReturnScope id2, RuleReturnScope id1) {
        CommonTree id1Tree = (CommonTree) id1.getTree();
        CommonTree id2Tree = (CommonTree) id2.getTree();
        if (!id1Tree.getText().equals(id2Tree.getText())) {
            String msg =
                    "End name " + id2Tree.getText()
                            + " does not match module name "
                            + id1Tree.getText();
            err.error(getPos(id2Tree), msg);
        }
    }

    protected void matchOperationIdent(RuleReturnScope id2, RuleReturnScope id1) {
        CommonTree id1Tree = (CommonTree) id1.getTree();
        CommonTree id2Tree = (CommonTree) id2.getTree();
        if (!id1Tree.getText().equals(id2Tree.getText())) {
            String msg =
                    "End name " + id2Tree.getText()
                            + " does not match operation name "
                            + id1Tree.getText();
            err.error(getPos(id2Tree), msg);
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

    protected boolean isDeductionToken(String testStr) {
        if (testStr.equals("deduction") || testStr.equals("Deduction")) {
            return true;
        }
        return false;
    }
}
