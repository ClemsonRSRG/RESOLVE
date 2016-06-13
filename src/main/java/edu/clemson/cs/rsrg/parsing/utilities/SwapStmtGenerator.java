/**
 * SwapStmtGenerator.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.parsing.utilities;

import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramVariableExp;
import edu.clemson.cs.rsrg.absyn.statements.SwapStmt;
import edu.clemson.cs.rsrg.parsing.TreeBuildingListener;

/**
 * <p>The main purpose of this class is to assist the {@link TreeBuildingListener}
 * in building the various different swap statements.</p>
 *
 * <p>Most of the time, we are simply building a regular {@link SwapStmt}. However,
 * we might have syntactic sugar that needs to be replaced with swap calls in
 * {@code Static_Array_Template}.</p>
 *
 * <p>This class will take care of the various different scenarios where we might
 * find an array expression and handle it appropriately.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class SwapStmtGenerator {

    // ===========================================================
    // Member Fields
    // ===========================================================

    //private final ProgramVariableExp myProgLeftExp;

    //private final ProgramVariableExp myProgRightExp;

}