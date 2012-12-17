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
 * BugReport.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.errors;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;

/**
 * This class is responsible for formatting a message to the user
 * when a request for a bug report is desired.  
 *
 * @author Steven Atkinson
 */
public class BugReport {

    // ===========================================================
    // Variables
    // ===========================================================

    private static String contactPerson = 
        "Please contact: Reusable Software Research Group\n";

    private static String emailAddress = 
        "murali@cs.clemson.edu\n";

    private static String institutionAddress1 = 
        "Developed at Department of Computer Science\n";

    private static String institutionAddress2 = 
        "Clemson University, Clemson SC USA 29634\n\n";
    
    private static String version = 
        "Version 2011.1.18\n\n";

    /* The string containing the full report. */
    private String report = null;
    
    private ErrorHandler err;

    // ===========================================================
    // Constructors
    // ===========================================================

    public BugReport (String report) {
        this.report = report + "\n\n" + 
            contactPerson + 
            emailAddress + 
            institutionAddress1 + 
            institutionAddress2 + 
            version;
    }
    
    // ===========================================================
    // Accessor Methods
    // ===========================================================

    /** Returns the entire bug report as a string. */
    public String getReport() { return report; }

    public static String abortProgram(Exception ex, CompileEnvironment env) {
    	StringBuilder sb = new StringBuilder();
    	sb.append("Currently compiling: "+ env.getCurrentTargetFileName() + "\n");
    	sb.append("Current compile environment: "+ env.toString() + "\n");
    	sb.append("Unexpected exception: " + ex + "\n");
    	String trace = getStackTraceString(ex);
        sb.append(trace);
        //ex.printStackTrace();
        BugReport report = new BugReport(ex.toString());
        sb.append(report.getReport());
        if(env.flags.isFlagSet(ResolveCompiler.FLAG_WEB)){
        	env.getCompileReport().addBugReport(sb.toString());
        }
        else{
        	System.err.println(sb.toString());
        }
        throw new RuntimeException();
    }
    
    private static String getStackTraceString(Throwable e){
    	/*StringBuilder sb = new StringBuilder();
    	for(StackTraceElement element : e.getStackTrace()){
    		sb.append(element.toString());
    		sb.append("\n");
    	}
    	return sb.toString();*/
    	Writer writer = new StringWriter();
    	PrintWriter pw = new PrintWriter(writer);
    	e.printStackTrace(pw);
    	return writer.toString();
    	
    }
}
	
