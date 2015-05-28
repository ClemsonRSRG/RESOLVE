/**
 * ErrorHandler2.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.errors;

import edu.clemson.cs.r2jt.init2.ResolveCompiler;
import edu.clemson.cs.r2jt.init2.CompileEnvironment;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * TODO: Description for this class
 */
public class ErrorHandler2 {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private final CompileEnvironment myInstanceEnvironment;
    private final boolean myIsWebOutput;
    private final Writer myOutputWriter;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ErrorHandler2(CompileEnvironment e) {
        this(e, new PrintWriter(System.err, true));
    }

    public ErrorHandler2(CompileEnvironment e, Writer outWriter) {
        myInstanceEnvironment = e;
        myIsWebOutput = e.flags.isFlagSet(ResolveCompiler.FLAG_WEB);
        myOutputWriter = outWriter;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Message Output Methods
    // -----------------------------------------------------------

    /**
     * <p>Outputs an informational message, not an error or warning.</p>
     *
     * @param msg A compilation message.
     */
    public void message(String msg) {
        if (!myInstanceEnvironment.debugOff()) {
            try {
                myOutputWriter.write(msg);
                myOutputWriter.flush();
            }
            catch (IOException e) {
                System.err
                        .println("Error writing information to the specified output");
                e.printStackTrace();
            }
        }
    }

}