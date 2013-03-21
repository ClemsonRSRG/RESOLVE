/*
 * THIS CLASS IS DEPRECATED. PLEASE USE AN INSTANCE OF CompileInstance INSTEAD!
 */

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
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the Clemson University nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
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
 * Clemson University. Contributors to the initial version are:
 * 
 * Steven Atkinson
 * Greg Kulczycki
 * Kunal Chopra
 * John Hunt
 * Heather Keown
 * Ben Markle
 * Kim Roche
 * Murali Sitaraman
 */
/*
 * Environment.java
 * 
 * The Resolve Software Composition Workbench Project
 * 
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.init;

import java.io.File;
import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.collections.*;
import edu.clemson.cs.r2jt.data.*;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.scope.OldSymbolTable;
import edu.clemson.cs.r2jt.scope.ModuleScope;

/**
 * @deprecated This class uses global variables that cause trouble.  Please use
 *             an instance of CompileEnvironment instead. All of the functionality
 *             has now been mover top the CompileEnvironment class. We just keep
 *             a reference in here and forward the methods as necessary to remain
 *             backward compatible. This class is no longer static, and most references
 *             to it have been removed.
 */
@Deprecated
public class Environment {

    /*
    private final static Log         LOG      = LogFactory.getLog(
                                                        Environment.class);
     */
    //private static Environment INSTANCE = new Environment(null);
    private CompileEnvironment myOldEnvironment;

    /*private Map<ModuleID, ModuleRecord> map
        = new Map<ModuleID, ModuleRecord>();

    private Map<File, ModuleID> fmap    = new Map<File, ModuleID>();
    private List<File>      unparsables = new List<File>();
    private Stack<ModuleID> stack       = new Stack<ModuleID>();
    private File            mainDir     = null;
    private File            targetFile  = null;
    private ErrorHandler    err ;
    private List<String>	javaFiles	= new List<String>();
    private List<ModuleID>	modules		= new List<ModuleID>();

    // -----------------------------------------------------------
    // Compiler flags
    // -----------------------------------------------------------
    //private boolean compileBodies = false;
    //private boolean proofcheck    = false;
    private boolean showBuild     = false;
    private boolean showEnv       = false;
    private boolean showTable     = false;
    private boolean showBind      = false;
    //private boolean analyzeOnly   = false;
    private boolean showImports   = false;
    private boolean showIndirect  = false;
    //private boolean translate     = false;
    private boolean perf          = false;    
    //private boolean typecheck     = false;
    //private boolean sanitycheck   = false;
    //private boolean prove         = false;
    //private boolean alternative   = false;
    //private boolean proveDebug    = false;
    //private boolean quickProve    = false;
    //private boolean verboseProve  = false;
    //private boolean verify        = false;
    //private boolean finalAssrts   = false;
    //private boolean split		  = false;
    private boolean isabelle	  = false;    
    //private boolean simplifyVCs	  = false;
    //private boolean repeatedArg	  = false;    
    //private boolean noGUI		  = false;
    private boolean debugOff	  = false;
    //private boolean errorsOnStdOut= false;
    //private boolean webInterface  = false;
    //private boolean createJar	  = false;
    //private boolean verboseJar	  = false;
    //private boolean listVCs		  = false;
    
    //private String outputFile     = null;
    
    //private static boolean compileSuccess = false;*/

    /*@Deprecated
    public static void newInstance() {
    	INSTANCE = new Environment(null);
    }*/

    @Deprecated
    public void clearStopFlags() {
        myOldEnvironment.clearStopFlags();
        /*showBuild   = false;
        showEnv     = false;
        showTable   = false;
        showBind    = false;
        analyzeOnly = false;
        translate   = false;
        perf        = false;
        //proofcheck  = false;
        //typecheck   = false;
        verify      = false;
        finalAssrts = false;
        split       = false;
        repeatedArg = false;*/
    }

    public Environment(CompileEnvironment env) {
        myOldEnvironment = env;
        //INSTANCE = this;
    }

    /**
     * Returns the unique instance of this singleton class.
     */
    /*@Deprecated
    public static Environment getInstance() {
        return INSTANCE;
    }*/

    @Deprecated
    public void setErrorHandler(ErrorHandler err) {
        myOldEnvironment.setErrorHandler(err);
        //this.err = err;
    }

    /** Sets the main directory to the specified directory. */
    @Deprecated
    public void setMainDir(File mainDir) {
        myOldEnvironment.setMainDir(mainDir);
        //this.mainDir = mainDir;
    }

    /** Sets the target file to the specified file. */
    @Deprecated
    public void setTargetFile(File targetFile) {
        myOldEnvironment.setTargetFile(targetFile);
        //this.targetFile = targetFile;
    }

    /** Returns the main directory. */
    @Deprecated
    public File getMainDir() {
        return myOldEnvironment.getMainDir();
        //return mainDir;
    }

    /** Returns the target file. */
    @Deprecated
    public File getTargetFile() {
        return myOldEnvironment.getTargetFile();
        //return targetFile;
    }

    /** Name the output file. */
    @Deprecated
    public void setOutputFileName(String outputFile) {
        myOldEnvironment.setOutputFileName(outputFile);
        //this.outputFile = outputFile;
    }

    /** Sets the main directory to the specified directory. */
    @Deprecated
    public String getOutputFilename() {
        return myOldEnvironment.getOutputFilename();
        //return outputFile;
    }

    /*
     public boolean isStdRealiz(String name) {
    
     }
     */
    /*
     * Prints the pathname of the file relative to the default
     * resolve directory.
     */
    @Deprecated
    public String getResolveName(File file) {
        return myOldEnvironment.getResolveName(file);
        /*if (mainDir == null) {
            return this.toString();
        } else {
            File   par  = mainDir.getParentFile();
            String path = file.toString();
            String mask = par.toString();

            assert path.startsWith(mask) :
                "path does not start with mask: " + path;
            return path.substring(mask.length() + 1);
        }*/
    }

    /**
     * Indicates that the module dec should be displayed.
     */
    @Deprecated
    public void setShowBuildFlag() {
        myOldEnvironment.setShowBuildFlag();
        //        LOG.debug("setShowBuildFlag() called -- module decs will be displayed.");
        //clearStopFlags();
        //showBuild = true;
    }

    /**
     * Indicates that the compilation environment should be displayed.
     */
    @Deprecated
    public void setShowEnvFlag() {
        myOldEnvironment.setShowEnvFlag();
        //clearStopFlags();
        //showEnv = true;
    }

    /**
     * Indicates that the symbol table should be displayed (before
     * binding).
     */
    @Deprecated
    public void setShowTableFlag() {
        myOldEnvironment.setShowTableFlag();
        //clearStopFlags();
        //showTable = true;
    }

    /**
     * Indicates that the symbol table should be displayed after
     * binding.
     */
    @Deprecated
    public void setShowBindFlag() {
        myOldEnvironment.setShowBindFlag();
        //clearStopFlags();
        //showBind = true;
    }

    /**
     * Indicates that the compiler should not perform translation.
     */
    /*@Deprecated
    public void setAnalyzeOnlyFlag() {
    	myOldEnvironment.setAnalyzeOnlyFlag();
        //clearStopFlags();
        //analyzeOnly = true;
    }*/

    /**
     * Indicates that imported module tables should be displayed if
     * the main symbol table is displayed.
     */
    @Deprecated
    public void setShowImportsFlag() {
        myOldEnvironment.setShowImportsFlag();
        //showImports = true;
    }

    /**
     * Indicates that files should be translated after analysis.
     */
    /*@Deprecated
    public void setTranslateFlag() {
    	myOldEnvironment.setTranslateFlag();
    	//clearStopFlags();
        //translate = true;
    }*/

    /**
     * For performance.
     */
    @Deprecated
    public void setPerformanceFlag() {
        myOldEnvironment.setPerformanceFlag();
        //perf = true;
    }

    /**
     * Indicates that debug output should be turned off to the maximum amount
     * this is possible.
     */
    @Deprecated
    public void setDebugOff() {
        myOldEnvironment.setDebugOff();
        //debugOff = true;
    }

    /**
     * Indicates that files should be verified after analysis.
     */
    /*@Deprecated
    public void setVerifyFlag() {
        clearStopFlags();
        verify = true;
    }*/

    /**
     * Indicates that only final Assertion should be printed.
     */
    /*@Deprecated
    public void setFinalAssrtsFlag() {
        finalAssrts = true;
    }*/

    /**
     * Indicates that only final Assertion should be printed.
     */
    /*@Deprecated
    public void setIsabelleFlag() {
        isabelle = true;
    }*/

    /*@Deprecated
    public void setListVCsFlag(){
    	listVCs = true;
    }*/

    /**
     * Indicates that only final Assertion should be printed.
     */
    /*@Deprecated
    public void setSimplifyVCsFlag() {
        simplifyVCs = true;
    }*/

    /**
     * Indicates that only final Assertion should be printed.
     */
    /*@Deprecated
    public void setSplitFlag() {
        verify = true;    	
        split = true;
    }*/
    /**
     * Indicates that indirect types should display the scopes they
     * are bound to.
     */
    @Deprecated
    public void setShowIndirectFlag() {
        myOldEnvironment.setShowIndirectFlag();
        //showIndirect = true;
    }

    /*public void setSuccess(){
    	compileSuccess = true;
    }*/

    /*public void resetSuccess(){
    	compileSuccess = false;
    }*/

    /**
     * Returns true if the module dec will be displayed, false
     * otherwise.
     */
    @Deprecated
    public boolean showBuild() {
        return myOldEnvironment.showBuild();
        //return showBuild;
    }

    /**
     * Returns true if the environment will be displayed, false
     * otherwise.
     */
    @Deprecated
    public boolean showEnv() {
        return myOldEnvironment.showEnv();
        //return showEnv;
    }

    /**
     * Returns true if the symbol table (before binding) will be
     * displayed, false otherwise.
     */
    @Deprecated
    public boolean showTable() {
        return myOldEnvironment.showTable();
        //return showTable;
    }

    /**
     * Returns true if the symbol table (after binding) will be
     * displayed, false otherwise.
     */
    @Deprecated
    public boolean showBind() {
        return myOldEnvironment.showBind();
        //return showBind;
    }

    /**
     * Returns true if modules are not to be translated, false
     * otherwise.
     */
    /*@Deprecated
    public boolean analyzeOnly() {
        return analyzeOnly;
    }*/

    /**
     * Returns true if import tables should displayed when the main
     * table is displayed.
     */
    @Deprecated
    public boolean showImports() {
        return myOldEnvironment.showImports();
        //return showImports;
    }

    /**
     * Returns true if indirect types should display the scopes
     * they are bound to.
     */
    @Deprecated
    public boolean showIndirect() {
        return myOldEnvironment.showIndirect();
        //return showIndirect;
    }

    /**
     * Returns true if files should be listed.
     */
    /*@Deprecated
    public boolean listVCs() {
    	return listVCs;
    }*/

    /**
     * Returns true iff we should suppress debug output.
     */
    @Deprecated
    public boolean debugOff() {
        return myOldEnvironment.debugOff();
        //return debugOff;
    }

    /**
     * Returns true if files should be verified after analysis.
     */
    /*@Deprecated
    public boolean verify() {
        return verify;
    }*/

    /**
     * Returns true if if performance is checked.
     */
    @Deprecated
    public boolean perf() {
        return myOldEnvironment.perf();
        //return perf;
    }

    /**
     * Returns true if files should be verified after analysis.
     */
    /*@Deprecated
    public boolean finalAssrts() {
        return finalAssrts;
    }*/

    /**
     * Returns true if files should be printed pretty after verification.
     */
    /*@Deprecated
    public boolean split() {
        return split;
    }*/

    /**
     * Returns true if files should be printed pretty after verification.
     */
    @Deprecated
    public boolean isabelle() {
        return false;
        //return isabelle;
    }

    /**
     * Returns true if files should be printed pretty after verification.
     */
    /*@Deprecated
    public boolean simplifyVCs() {
        return simplifyVCs;
    }*/

    /*public boolean repeatedArg() {
        return repeatedArg;
    } */

    /**
     * Returns true if the specified file is present in the
     * compilation environment but could not be successfully parsed.
     */
    /*@Deprecated
    public boolean containsUnparsable(File file) {
        return unparsables.contains(file);
    }*/

    /**
     * Returns true if the specified file is present in the
     * compilation environment, has an associated id and a valid
     * module dec.
     */
    @Deprecated
    public boolean contains(File file) {
        return myOldEnvironment.contains(file);
        //return fmap.containsKey(file);
    }

    /**
     * Returns true if the specified module is present in the
     * compilation environment, has an associated file and a valid
     * module dec.
     */
    @Deprecated
    public boolean contains(ModuleID id) {
        return myOldEnvironment.contains(id);
        //return map.containsKey(id);
    }

    /**
     * Returns true if the specified file has already been
     * successfully compiled.
     */
    @Deprecated
    public boolean compileCompleted(File file) {
        return myOldEnvironment.compileCompleted(file);
        /*if (!fmap.containsKey(file)) {
            return false;
        } else {
            return map.get(fmap.get(file)).isComplete();
        }*/
    }

    /**
     * Returns true if compilation on the specified file has
     * begun, has not aborted, and has not completed.
     */
    @Deprecated
    public boolean compileIncomplete(File file) {
        return myOldEnvironment.compileIncomplete(file);
        /*if (!fmap.containsKey(file)) {
            return false;
        } else {
            return (!map.get(fmap.get(file)).isComplete() &&
                    !map.get(fmap.get(file)).containsErrors());
        }*/
    }

    /**
     * Returns true if a compile had been attempted on the specified
     * file and was aborted due to errors.
     */
    @Deprecated
    public boolean compileAborted(File file) {
        return myOldEnvironment.compileAborted(file);
        /*if (unparsables.contains(file)) { return true; }
        if (!fmap.containsKey(file)) {
            return false;
        } else {
            return map.get(fmap.get(file)).containsErrors();
        }*/
    }

    /**
     * Returns the module id associated with the specified file.
     */
    @Deprecated
    public ModuleID getModuleID(File file) {
        return myOldEnvironment.getModuleID(file);
        //return fmap.get(file);
    }

    /**
     * Returns the file associated with the specified module.
     */
    @Deprecated
    public File getFile(ModuleID id) {
        return myOldEnvironment.getFile(id);
        //return map.get(id).getFile();
    }

    /**
     * Returns the module dec associated with the specified module.
     */
    @Deprecated
    public ModuleDec getModuleDec(ModuleID id) {
        return myOldEnvironment.getModuleDec(id);
        //return map.get(id).getModuleDec();
    }

    /**
     * Returns a list of visible theories for the specified module.
     */
    @Deprecated
    public List<ModuleID> getTheories(ModuleID id) {
        return myOldEnvironment.getTheories(id);
        //return map.get(id).getTheories();
    }

    /**
     * Returns the symbol table associated with the specified module.
     */
    @Deprecated
    public OldSymbolTable getSymbolTable(ModuleID id) {
        return myOldEnvironment.getSymbolTable(id);
        //return map.get(id).getSymbolTable();
    }

    /**
     * Returns the map of symbol tables.
     */
    @Deprecated
    public Map<ModuleID, ModuleRecord> getMap() {
        return myOldEnvironment.getMap();
        //return map;
    }

    /**
     * Returns the module scope associated with the specified module.
     */
    @Deprecated
    public ModuleScope getModuleScope(ModuleID id) {
        return myOldEnvironment.getModuleScope(id);
        /*assert map.get(id).getSymbolTable() != null :
            "symbol table for id is null";
        return map.get(id).getSymbolTable().getModuleScope();*/
    }

    /**
     * Returns whether or not the file was compiled successfully
     */
    /*public boolean getSuccess(){
    	return compileSuccess;
    }*/

    /**
     * Constructs a record containing the module id, the file, and the
     * module dec, and places it in the module environment. Also
     * places the module into a stack that indicates compilation has
     * begun on this module but has not completed.
     */
    @Deprecated
    public void constructRecord(ModuleID id, File file, ModuleDec dec) {
        myOldEnvironment.constructRecord(id, file, dec);
        /*ModuleRecord record = new ModuleRecord(id, file);
        record.setModuleDec(dec);
        assert !map.containsKey(id) : "map already contains key";
        assert !fmap.containsKey(file) : "fmap already contains file";
        map.put(id, record);
        fmap.put(file, id);
        stack.push(id);
        
        if (!debugOff) {
        	err.message("Construct record: " + id.toString()); //DEBUG
        }*/
    }

    /**
     * Associates a list of visible theories with the specified
     * module. This method may only be called once during the life of
     * a module. The visible theories must be accessible to a module
     * before population begins.
     */
    @Deprecated
    public void setTheories(ModuleID id, List<ModuleID> theories) {
        myOldEnvironment.setTheories(id, theories);
        //ModuleRecord record = map.get(id);
        //record.setTheories(theories);
    }

    /**
     * Places the symbol table for an associated module into the
     * environment and pops the module from the compilation stack,
     * indicating that compilation has been completed for this module.
     */
    @Deprecated
    public void completeRecord(ModuleID id, OldSymbolTable table) {
        myOldEnvironment.completeRecord(id, table);
        /*ModuleRecord record = map.get(id);
        record.setSymbolTable(table);
        ModuleID id2 = stack.pop();
        assert id == id2 : "id != id2";
        
        if (!debugOff) {
        	err.message("Complete record: " + id.toString()); //DEBUG
        }*/
    }

    /**
     * Adds a file to the environment which failed to parse.
     */
    @Deprecated
    public void abortCompile(File file) {
        myOldEnvironment.abortCompile(file);
        /*if (fmap.containsKey(file)) {
            abortCompile(fmap.get(file));
        } else {
            unparsables.add(file);
            err.message("Add unparsable: " + file.getName()); //DEBUG
        }*/
    }

    /**
     * Aborts compilation of a module which parsed without errors, and
     * pops this module from the compilation stack.
     */
    @Deprecated
    public void abortCompile(ModuleID id) {
        myOldEnvironment.abortCompile(id);
        /*map.get(id).setErrorFlag();
        ModuleID id2 = stack.pop();
        assert id == id2 : "id != id2";
        err.message("Abort compile: " + id.toString()); //DEBUG
         */}

    /**
     * Returns a string representation of the compilation environment.
     */
    @Deprecated
    public String toString() {
        return myOldEnvironment.toString();
        /*StringBuffer sb = new StringBuffer();
        sb.append("=============================="
                  + "==============================\n");
        sb.append("Compilation environment for "
                  + targetFile.getName() + "\n");
        sb.append("=============================="
                  + "==============================\n");
        sb.append("Main directory: " + mainDir.getName() + "\n");
        sb.append("------------------------------"
                  + "------------------------------\n");
        sb.append("Unparsable files: " + getResolveNames(unparsables) + "\n");
        sb.append("Compile stack: " + stack.toString() + "\n");
        Iterator<ModuleID> i = map.keyIterator();
        while (i.hasNext()) {
            ModuleID id = i.next();
            ModuleRecord record = map.get(id);
            //sb.append(getResolveName(record.getFile()) + " ");
            sb.append(id.toString());
            if (record.isComplete()) {
                sb.append(" is complete");
            } else {
                sb.append(" is incomplete");
            }
            if (record.containsErrors()) {
                sb.append(" due to errors");
            }
            sb.append(".\n");
            sb.append("    Theories: " + record.getTheories().toString());
            sb.append("\n");
        }
        sb.append("------------------------------"
                  + "------------------------------\n");
        return sb.toString();*/
    }

    /*    private String getResolveNames(List<File> files) {
     StringBuffer sb = new StringBuffer();
     sb.append("( ");
     Iterator<File> i = files.iterator();
     while (i.hasNext()) {
     File file = i.next();
     sb.append(getResolveName(file));
     if (i.hasNext()) { sb.append(", "); }
     }
     sb.append(" )");
     return sb.toString();
     }*/

    /**
     * Returns a string of the modules in the compile stack, beginning
     * with the the specified module and ending with the module at the
     * top of the stack. The modules have arrows between them to
     * indicate dependencies.  This method is used when reporting a
     * circular module dependency error.
     */
    @Deprecated
    public String printStackPath(ModuleID id) {
        return myOldEnvironment.printStackPath(id);
        /*StringBuffer sb = new StringBuffer();
        Stack<ModuleID> stack2 = new Stack<ModuleID>();
        boolean printID = false;
        ModuleID id2 = null;
        while (!stack.isEmpty()) {
            id2 = stack.pop();
            stack2.push(id2);
        }
        sb.append("(");
        while (!stack2.isEmpty()) {
            id2 = stack2.pop();
            if (id2 == id) { printID = true; }
            if (printID) {
                sb.append(id2.toString());
                if (!stack2.isEmpty()) {
                    sb.append(" -> ");
                }
            }
            stack.push(id2);
        }
        sb.append(")");
        return sb.toString();*/
    }

    @Deprecated
    public void addModule(ModuleID mod) {
        myOldEnvironment.addModule(mod);
        //modules.addUnique(mod);
    }

    @Deprecated
    public void printModules() {
        myOldEnvironment.printModules();
        /*Iterator<ModuleID> it = modules.iterator();
        while(it.hasNext()){
        	System.out.println(it.next().getName().toString());
        }*/
    }
}
