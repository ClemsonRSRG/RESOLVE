/**
 * Main2.java
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
package edu.clemson.cs.r2jt;

import edu.clemson.cs.r2jt.init2.ResolveCompiler;

/**
 * <p>The main class for the RESOLVE compiler when invoking
 * from the command line or using an IDE.</p>
 */
public class Main2 {

    public static void main(String[] args) {
        ResolveCompiler compiler = new ResolveCompiler(args);
        compiler.invokeCompiler();
    }

}