/**
 * ColsAST.java
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

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 * An extension to ANTLR's AST class to store column numbers with the
 * tree nodes.
 * 
 * @author Steven Atkinson
 */
public class ColsAST extends CommonTree {

    // ===========================================================
    // Variables
    // ===========================================================

    //private static String[] tokenNames = null;

    //private static boolean verboseStringConversion = false;

    //protected int ttype = Token.INVALID_TOKEN_TYPE;

    //protected String text;

    //protected int line;

    //protected int column;

    public ColsAST() {
        super();
    }

    public ColsAST(Token tok) {
        super(tok);
        //System.out.println(tok.toString());
    }

    public ColsAST(ColsAST tree) {
        super(tree);
    }

    public Tree dupNode() {
        return new ColsAST(this);
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    /** Get the token text for this node */
    //public String getText() { return text; }

    /** Get the token type for this node */
    //public int getType() { return ttype; }

    /** Get the line for this node */
    /*public int getLine() { 
        if (line == 0) { 
            // search children depth first to find line information
            for (ColsAST cursor = (ColsAST)this.getFirstChild();
                 cursor != null && line == 0;
                 cursor = (ColsAST)cursor.getNextSibling()) {
                int result = cursor.getLine();
                if (result != 0) {
                    line = result;
                }
            }
        }       
        return line; 
    }*/

    /** Get the column for this node */
    /*public int getColumn() { 
        if (column == 0) { 
            // search children depth first to find column information
            for (ColsAST cursor = (ColsAST)this.getFirstChild();
                 cursor != null && column == 0;
                 cursor = (ColsAST) cursor.getNextSibling()) {
                int result = cursor.getColumn();
                if (result != 0) {
                    column = result;
                }
            }
        }       
        return column; 
    }*/

    /** Set the token text for this node */
    /*public void setText(String text_) { 
        text = text_; 
    }*/

    /** Set the token type for this node */
    /*public void setType(int ttype_) { 
        ttype = ttype_; 
    }*/

    /** Set the line for this node */
    /*public void setLine(int line_) {
        line = line_;
    }*/

    /** Set the column for this node */
    /*public void setColumn(int column_) {
        column = column_;
    }*/

    // ===========================================================
    // Public Methods
    // ===========================================================

    /*public void initialize(int t, String txt) {
        setType(t);
        setText(txt);
        setLine(0);
        setColumn(0);
    }*/

    /*public void initialize(AST t) {
        setText(t.getText());
        setType(t.getType());
        if (t instanceof ColsAST) {
            setLine(((ColsAST)t).getLine());
            setColumn(((ColsAST)t).getColumn());
        } else {
            setLine(0);
            setColumn(0);
        }
    }*/

    /*public void initialize(Token tok) {
        setText(tok.getText());
        setType(tok.getType());
        setLine(tok.getLine());
        //setColumn(tok.getColumn());
    }

    public static void setVerboseStringConversion (boolean verbose,
                                                   String[] names) {
        verboseStringConversion = verbose;
        tokenNames = names;
    }*/

    /**
     * Returns a deep copy of the tree with this node as the root.
     * The tree will not include the younger siblings of this node.
     */
    /*public ColsAST copy() {
        ASTFactory astFactory = new ASTFactory();
        astFactory.setASTNodeType("edu.clemson.cs.r2jt.parsing.ColsAST");
        ColsAST newAST = (ColsAST)astFactory.create();
        newAST.setType(this.getType());
        newAST.setText(this.getText());
        newAST.setLine(this.getLine());
        newAST.setColumn(this.getColumn());
        ColsAST cursor = (ColsAST)this.getFirstChild();
        while (cursor != null) {
            newAST.addChild(cursor.copy());
            cursor = (ColsAST)cursor.getNextSibling();
        }
        return newAST;
    }*/

    /**
     * Returns a deep copy of this node, its younger siblings, and
     * all of their dependents.
     */
    /*public ColsAST copyRight() {
        ASTFactory astFactory = new ASTFactory();
        astFactory.setASTNodeType("edu.clemson.cs.r2jt.parsing.ColsAST");
        ColsAST newAST = (ColsAST)astFactory.create();
        newAST.setType(this.getType());
        newAST.setText(this.getText());
        newAST.setLine(this.getLine());
        newAST.setColumn(this.getColumn());
        newAST.setNextSibling(((ColsAST)this.getNextSibling()).copyRight());
        newAST.addChild(((ColsAST)this.getFirstChild()).copyRight());
        return newAST;
    }*/

    /*public String toString() {
        StringBuffer b = new StringBuffer();
        // if verbose and type name not same as text (keyword probably)
        if ( verboseStringConversion &&
    !getText().equalsIgnoreCase(tokenNames[getType()]) &&
             !getText().equalsIgnoreCase(StringUtils.stripFrontBack(tokenNames[getType()],"\"","\"")) ) {
            b.append('[');
            b.append(getText());
            b.append(",<");
            b.append(tokenNames[getType()]);
            b.append(">");
            b.append("@(");
            b.append(getLine());
            b.append(",");
            b.append(getColumn());
            b.append(")>]");
            return b.toString();
        }
        return getText();
    }*/
}
