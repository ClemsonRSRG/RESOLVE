/*
 * BugReport.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.errors;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;

/**
 * This class is responsible for formatting a message to the user when a request
 * for a bug report is
 * desired.
 *
 * @author Steven Atkinson
 */
public class BugReport {

    // ===========================================================
    // Variables
    // ===========================================================

    private static String contactPerson =
            "Please contact: Reusable Software Research Group\n";

    private static String emailAddress = "murali@cs.clemson.edu\n";

    private static String institutionAddress1 =
            "Developed at Department of Computer Science\n";

    private static String institutionAddress2 =
            "Clemson University, Clemson SC USA 29634\n\n";

    private static String version = "Version 2011.1.18\n\n";

    /* The string containing the full report. */
    private String report = null;

    private ErrorHandler err;

    // ===========================================================
    // Constructors
    // ===========================================================

    public BugReport(String report) {
        this.report = report + "\n\n" + contactPerson + emailAddress
                + institutionAddress1 + institutionAddress2 + version;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    /** Returns the entire bug report as a string. */
    public String getReport() {
        return report;
    }

    public static String abortProgram(Exception ex, CompileEnvironment env) {
        StringBuilder sb = new StringBuilder();
        sb.append("Currently compiling: " + env.getCurrentTargetFileName()
                + "\n");
        sb.append("Current compile environment: " + env.toString() + "\n");
        sb.append("Unexpected exception: " + ex + "\n");
        String trace = getStackTraceString(ex);
        sb.append(trace);
        // ex.printStackTrace();
        BugReport report = new BugReport(ex.toString());
        sb.append(report.getReport());
        if (env.flags.isFlagSet(ResolveCompiler.FLAG_WEB)) {
            env.getCompileReport().addBugReport(sb.toString());
        }
        else {
            System.err.println(sb.toString());
        }
        throw new RuntimeException();
    }

    private static String getStackTraceString(Throwable e) {
        /*
         * StringBuilder sb = new StringBuilder(); for(StackTraceElement element
         * : e.getStackTrace()){
         * sb.append(element.toString()); sb.append("\n"); } return
         * sb.toString();
         */
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        e.printStackTrace(pw);
        return writer.toString();

    }
}
