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
 * ErrorHandler.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.errors;

import antlr.BaseAST;
import antlr.MismatchedTokenException;
import antlr.NoViableAltException;
import antlr.Token;
import antlr.collections.AST;
import java.io.*;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.MetaFile;
import edu.clemson.cs.r2jt.data.Pos;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.init.Environment;
import edu.clemson.cs.r2jt.parsing.ColsAST;
import edu.clemson.cs.r2jt.ResolveCompiler;

/**
 * A class through which all messages to the user pass. Contains
 * support for informational, error, warning and panic messages.
 *
 * @author Steven Atkinson
 */
public class ErrorHandler {
 
    // ===========================================================
    // Variables
    // ===========================================================

    //private static ErrorHandler instance = new ErrorHandler();
    
    private final CompileEnvironment myInstanceEnvironment;

    /* The "true" argument indicates that calls to out.println() will
     * flush the buffer. */
    private static PrintWriter err = new PrintWriter(System.err, true);
    
    private int errorCount = 0;

    private int warningCount = 0;

    /* The file being checked for errors (if any). Eventually, we want
       to make this a java.io.File type. */
    private String filename = new String("");

    private File myFile = null;

    private boolean ignoreErrors = false;
    
    private boolean webOutput;
    
    // ===========================================================
    // Constructors
    // ===========================================================

    //private ErrorHandler() { 
    	//myInstanceEnvironment = null; }
    
    public ErrorHandler(CompileEnvironment env) {
    	myInstanceEnvironment = env;
    	webOutput = env.flags.isFlagSet(ResolveCompiler.FLAG_WEB);
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    public void setIgnore(boolean ignore) {
    	ignoreErrors = ignore;
    }
    
    public boolean getIgnore() {
    	return ignoreErrors;
    }
    
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public File getFile() {
        return myFile;
    }

    public void setFile(File file) {
        this.myFile = file;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Returns the unique instance of this error handler. */
    //public static ErrorHandler getInstance() {
        //return instance;
    //}

    // -----------------------------------------------------------
    // Message Output Methods
    // -----------------------------------------------------------
    
    /** Outputs an informational message, not an error or warning. */
    public void message (String msg) {
    	if (!myInstanceEnvironment.debugOff()) {
    		err.println(msg);
    	}
    }

    private void doError(String msg) {
    	if (!ignoreErrors) {
	    	updateErrorCount();
    	}
    	
    	/*if (Environment.getInstance().errorsOnStdOut()) {
    		System.out.println(msg);
    	}
    	else {*/
		err.println(msg);
	    	//}
    	//}
    }
    
    /**
     * 
     * @param fileName
     * @param lineNum
     * @param msg
     * Outputs the error information to the compiler report (for web interface use)
     */
    private void doError(String fileName, int lineNum, String msg){
    	if (!ignoreErrors) {
	    	updateErrorCount();
    	}
    	
    	String json = "{";
    	json += "\"fn\":\"" + fileName + "\",";
    	json += "\"ln\":\"" + lineNum + "\",";
    	json += "\"msg\":\"" + ResolveCompiler.webEncode(msg) + "\"";
    	json += "}";
    	myInstanceEnvironment.getCompileReport().addError(json);
    }
    
    /** Outputs an error message. */
    public void error(String msg) {
        //doError("Error:" + filename + ":" + msg);
		if(webOutput){
    		
		}
		else{
			doError("Error:" + msg);
		}
    }
    
    /** Outputs a positioned error message. */
    public void error(Pos pos, String msg) {
		if(webOutput){
			String newMsg = msg + "\n" + printErrorLine(myFile, pos);
			doError(myFile.getName(), pos.getLine(), newMsg);
		}
		else{
			doError("Error: " + myFile.getName() + "(" + pos.getLine()
                    + "): " + msg + "\n" + printErrorLine(myFile, pos));
		}
    }

    /** Outputs a positioned error message. */
    public void error(Location location, String msg) {
    	if (location == null) {
    		error(msg);
    	}
    	else {
    		if(webOutput){
    			String newMsg = msg + "\n" +
				printErrorLine(location.getFile(), location.getPos());
		doError(location.getFilename(), location.getPos().getLine(), newMsg);
    		}
    		else{
    			doError("Error: " + location.toString() + ":\n" + msg + "\n" +
    	        		printErrorLine(location.getFile(), location.getPos()));
    		}
    	}
    }
    
    /** Outputs a positioned error message comparing two lines of code. */
    public void error(Location location1, Location location2, String msg) {
    	if (location1 == null) {
    		error(msg);
    	}
    	else {
    		if(webOutput){
    			String newMsg = msg + "\n" +
    					printErrorLine(location1.getFile(), location1.getPos()) + "\n" +
    					printErrorLine(location2.getFile(), location2.getPos());
    			doError(location1.getFilename(), location1.getPos().getLine(), newMsg);
    		}
    		else{
    			doError("Error: " + location1.toString() + ":\n" + msg + "\n" +
    	        		printErrorLine(location1.getFile(), location1.getPos()) + "\n" +
        		printErrorLine(location2.getFile(), location2.getPos()));
    		}
    	}
    }

    /** Outputs a warning message. */
    public void warning(String msg) {
        warningCount++;
        err.println("Warning:" + filename + ":" + msg);   
    }

    /** Outputs a positioned warning message. */
    public void warning(Pos pos, String msg) {
        warningCount++;
        err.println("Warning:" + filename + ":" + pos.getLine()
                    + "," + pos.getColumn() + ": " + msg);    
    }

    /** Outputs a positioned warning message. */
    public void warning(Location location, String msg) {
        warningCount++;
        err.println("Warning: " + location.toString() + ": " + msg);
    }

    /** Outputs a panic message as a bug report */
    public void panic(String msg) {
        BugReport bug = new BugReport(msg);
        err.println("PANIC: " + bug.getReport());
        throw new RuntimeException();
    }

    /** Outputs a positioned panic message as a bug report. */
    public void panic(Pos pos, String msg) {
        BugReport bug = new BugReport(msg);
        err.println("PANIC: "+ pos.getLine() + "," + pos.getColumn()
                    + ": " + bug.getReport());
        throw new RuntimeException();
    }

    /** Outputs a positioned panic message as a bug report. */
    public void panic(Location location, String msg) {
        BugReport bug = new BugReport(msg);
        err.println("PANIC: "+ location.toString() + ": " + bug.getReport());
        throw new RuntimeException();
    }

    // -----------------------------------------------------------
    // Syntax Error Methods
    // -----------------------------------------------------------

    /**
     * Called when a mismatched token exception occurs.
     * This routine investigates the info in the MMTE object
     * and constructs a nice error message.  This over-rides 
     * the default antlr.MismatchedTokenException.toString()
     * routine, and is therefore a little bit of a HACK.
     *
     * Note that MismatchedTokenExceptions arise during
     * parsing and tree parsing.  This routine handles both
     * situations, under the assumption that both the token
     * types and nodes contain line and column information.
     *
     * @see antlr.MismatchedTokenException
     */
    public void syntaxError(MismatchedTokenException ex) {
        assert ex != null : "ex is null";
        Token token = ex.token; 
        AST node = ex.node;
        String str = ex.toString();  
        if (token != null) { // we are parsing
            // grab the <msg> part of the exception's message 
            String msg = str.substring(3 + str.indexOf("), "), str.length()); 
            this.error(new Pos(token.getLine(), token.getColumn()), msg);
        } else { // we are walking
            if (node == null) { // the mismatched tree node is null
                this.error("Error at (empty_tree): " + str);
            } else if (node instanceof BaseAST) {
                ColsAST colsNode = (ColsAST) node;
                this.error(new Pos(colsNode.getLine(),
                        			colsNode.getCharPositionInLine()), str);
                                   //colsNode.getColumn()), str);
            } else { //antlr has some kind of antlr.ASTNULLType
                this.error("Error at (empty_subtree): " + str);
            }
        }
    }

    /**
     * Called when a noViableAlt exception occurs.
     * This routine investigates the info in the NVAE object
     * and constructs a nice error message.  This over-rides
     * the default antlr.NoViableAltException.toString()
     * routine, and is therefore a little bit of a HACK.
     *
     * Note that NoViableAltExceptions arise during both
     * parsing and tree parsing.  This routine handles both
     * situations, under the assumption that both the token
     * types and nodes contain line and column information.
     *
     * @see antlr.NoViableAltException
     */
    public void syntaxError(NoViableAltException ex) {
        assert ex != null : "ex is null";
        Token token = ex.token; 
        AST node = ex.node;
        String str = ex.toString();
        /* Grab the message part of the string. No matter what caused
         * the exception, this starts with the string "unexpected". */
        String msg = str.substring(str.indexOf("unexpected"), str.length());
        if (token != null) { // we are parsing
            this.error(new Pos(token.getLine(), token.getColumn()), msg);
        } else { // we are walking
            if (node == null) {
                this.error("Error at (empty_subtree): " + str);
            } else if (node instanceof BaseAST) {
                ColsAST colsNode = (ColsAST) node;
                this.error(new Pos(colsNode.getLine(),
            						colsNode.getCharPositionInLine()), str);
                                   //colsNode.getColumn()), str);
            } else { //antlr has some kind of antlr.ASTNULLType
                this.error("Error at (empty_subtree): " + str);
            }
        }
    }

    /**
     * Dispatch the parser exception to one of the two handlers. If
     * this cannot be done, issue a bug report.
     */
    public void syntaxError(java.lang.Exception ex) {
        if (ex instanceof MismatchedTokenException) {
            this.syntaxError((MismatchedTokenException)ex);
        } else if (ex instanceof NoViableAltException) {
            this.syntaxError((NoViableAltException)ex);
        } else {
            BugReport bug = new BugReport("Unknown parser exception "
                                          + ex);
            this.error(bug.getReport());
        }
    }

    // -----------------------------------------------------------
    // Error Count Methods
    // -----------------------------------------------------------

    //FIX: Figure out how to do the 100 error abort, and do something
    //     like this for warnings too.
    /**
     * Updates the error count. If this is the first error, a new
     * line is printed. If this is error 101, the program is aborted.
     */
    public void updateErrorCount() {
        if (errorCount == 0) {
            err.println();
        }
        if (errorCount == 100) {
            ; // somehow abort, but make sure 100 errors are shown
        }
        errorCount++;
    }

    /**
     * Returns the number of errors since this handler was created or
     * since the last time resetCounts() was called.
     */
    public int getErrorCount() {
        return errorCount;
    }
    
    /**
     * Returns the number of warnings since this handler was created or
     * since the last time resetCounts() was called.
     */
    public int getWarningCount() {
        return warningCount;
    }

    /**
     * Resets the warning and error counts to 0.
     */
    public void resetCounts() {
        errorCount = 0;
        warningCount = 0;
    }

    public boolean countExceeds(int n) {
        return (errorCount > n);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    public String printErrorLine(File file, Pos pos) {
        String lineOfCode = printLine(file, pos.getLine());
        int posColumn = pos.getColumn() + 1;
        
        StringBuffer sb = new StringBuffer(lineOfCode.length() + 1 + posColumn);
        
        sb.append(lineOfCode);
        sb.append("\n");
        
        //Print the aligning whitespace, taking into account tabs.
        //Note that we must convert from "columns", which are 1-indexed, to
        //a string index, which is 0-indexed
        sb.append(printAligningSpace(lineOfCode, posColumn - 1));
        
        //Print the caret
        sb.append("^\n");
        return sb.toString();
    }

    /*
     * This method returns a string consisting of the correct white-space to 
     * visually align (i.e., in the presence of the '\t' character) any 
     * immediately following character with the character in the text at the 
     * index provided.
     * 
     * @param text The text against which to align the character.
     * @param targetIndex The index in the text against which to align the
     *                    character.
     *                    
     * @return A <code>String</code> of whitespace that will align an 
     *         immediately following character with the given index of the 
     *         given text.
     */
    private String printAligningSpace(String text, int targetIndex) {
    	StringBuffer buffer = new StringBuffer(targetIndex);
    	
    	for (int curIndex = 0; curIndex < targetIndex; curIndex++) {
    		if (text.charAt(curIndex) == '\t') {
    			buffer.append('\t');
    		}
    		else {
    			buffer.append(' ');
    		}
    	}
    	
    	return buffer.toString();
    }
    
    private String printLine(File file, int n) {
        String str = "";
    	String fileName = file.getName();
    	fileName = fileName.substring(0, fileName.indexOf("."));
    	String pkg = file.getParentFile().getName();
    	//System.out.println("compilePosModule: "+targetFile.getName().getLocation());
    	String key = pkg + "." + fileName;
    	//System.out.println("Checking UserFileMap for: " + key + " (ErrorHandler(522)");
    	if(myInstanceEnvironment.isUserFile(key)){
    		//System.out.println("found: "+key);
			MetaFile inputFile = myInstanceEnvironment.getUserFileFromMap(key);
			String source = inputFile.getMyFileSource();
			StringReader sr = new StringReader(source);
			LineNumberReader reader = new LineNumberReader(sr);
            try{
            	for (int i = 1; i < n; i++) {
	                reader.readLine();
	            }
	            str = reader.readLine();
            } catch(IOException ioEx) {
	            System.err.println(ioEx);
	        }
    	}
    	else{
    		try {
	            FileReader fileReader = new FileReader(file);
	            LineNumberReader reader = new LineNumberReader(fileReader);
	            for (int i = 1; i < n; i++) {
	                reader.readLine();
	            }
	            str = reader.readLine();
	        } catch (FileNotFoundException fileEx) {
	            System.err.println(fileEx);
	        } catch (IOException ioEx) {
	            System.err.println(ioEx);
	        } 
    	}
        return str;
    }
}
