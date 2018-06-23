/*
 * Main.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg;

import edu.clemson.cs.rsrg.init.ResolveCompiler;

/**
 * <p>The main class for the RESOLVE compiler when invoking
 * from the command line or using an IDE.</p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 * @version 1.0
 */
public class Main {

    /**
     * <p>This method creates and invokes the {@link ResolveCompiler}
     * to perform the compilation job.</p>
     *
     * @param args Arguments required to perform the compile process.
     */
    public static void main(String[] args) {
        ResolveCompiler compiler = new ResolveCompiler(args);
        compiler.invokeCompiler();
    }

}