/**
 * RecordTypeAST.java
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
package edu.clemson.cs.r2jt.absynnew;

import edu.clemson.cs.r2jt.absynnew.decl.VariableAST;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>A <code>RecordTypeAST</code> represents programmatic type designed
 * for grouping variables and other pieces of data together -- not unlike a
 * traditional <tt>C</tt> struct.</p>
 */
public class RecordTypeAST extends TypeAST {

    private final List<VariableAST> myFields;

    public RecordTypeAST(Token start, Token stop, List<VariableAST> fields) {
        super(start, stop);
        myFields = fields;
    }

    public List<VariableAST> getFields() {
        return myFields;
    }
}
