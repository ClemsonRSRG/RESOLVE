/*
 * This software is released under the new BSD 2006 license.
 * 
 * Note the new BSD license is equivalent to the MIT License, except for the
 * no-endorsement final clause.
 * 
 * Copyright (c) 2007, Clemson University
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer. 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution. 
 *   * Neither the name of the Clemson University nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This sofware has been developed by past and present members of the
 * Reusable Sofware Research Group (RSRG) in the School of Computing at
 * Clemson University.  Contributors to the initial version are:
 * 
 *     Steven Atkinson
 *     Greg Kulczycki
 *     Kunal Chopra
 *     John Hunt
 *     Heather Keown
 *     Ben Markle
 *     Kim Roche
 *     Murali Sitaraman
 */

/*
 * ColsAST.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.parsing;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

//import antlr.*;
//import antlr.collections.AST;
//import antlr.ASTFactory;
//import antlr.StringUtils;
//import java.lang.Object;
/**
 * An extension to ANTLR's AST class to store column numbers with the
 * tree nodes.
 * 
 * @author Steven Atkinson
 */
public class ColsAST extends CommonTree{

    // ===========================================================
    // Variables
    // ===========================================================

    //private static String[] tokenNames = null;

    //private static boolean verboseStringConversion = false;

    //protected int ttype = Token.INVALID_TOKEN_TYPE;

    //protected String text;

    //protected int line;

    //protected int column;
    
    public ColsAST(){
    	super();
    }
    
    public ColsAST(Token tok){
    	super(tok);
    	//System.out.println(tok.toString());
    }
    
    public ColsAST(ColsAST tree){
    	super(tree);
    }
    
    public Tree dupNode(){
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
