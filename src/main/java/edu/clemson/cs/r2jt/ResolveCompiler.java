/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.data.MetaFile;
import edu.clemson.cs.r2jt.data.ModuleKind;
import edu.clemson.cs.r2jt.proving.Prover;
import edu.clemson.cs.r2jt.proving2.ProverListener;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;

public class ResolveCompiler {

    private static final String FLAG_SECTION_NAME = "Output";

    private static final String FLAG_DESC_WEB =
            "Change the output to be more web-friendly for the Web Interface.";

    private static final String FLAG_DESC_ERRORS_ON_STD_OUT =
            "Change the output to be more web-friendly for the Web Interface.";

    private static final String FLAG_DESC_NO_DEBUG =
            "Remove debugging statements from the compiler output.";

    private static final String FLAG_DESC_XML_OUT =
            "Changes the compiler output files to XML";

    private static final String FLAG_DESC_EXPORT_AST =
            "exports the AST for the target file as a .dot file that can be viewed in Graphviz";

    /**
     * <p>The main web interface flag.  Tells the compiler to modify
     * some of the output to be more user-friendly for the web.</p>
     */
    public static final Flag FLAG_WEB =
            new Flag(FLAG_SECTION_NAME, "webinterface", FLAG_DESC_WEB,
                    Flag.Type.HIDDEN);

    /**
     * <p>Tells the compiler to send error messages to std_out instead
     * of std_err.</p>
     */
    public static final Flag FLAG_ERRORS_ON_STD_OUT =
            new Flag(FLAG_SECTION_NAME, "errorsOnStdOut",
                    FLAG_DESC_ERRORS_ON_STD_OUT, Flag.Type.HIDDEN);

    /**
     * <p>Tells the compiler to remove debugging messages from the compiler
     * output.</p>
     */
    public static final Flag FLAG_NO_DEBUG =
            new Flag(FLAG_SECTION_NAME, "nodebug", FLAG_DESC_NO_DEBUG);

    /**
     * <p>Tells the compiler to remove debugging messages from the compiler
     * output.</p>
     */
    public static final Flag FLAG_XML_OUT =
            new Flag(FLAG_SECTION_NAME, "XMLout", FLAG_DESC_XML_OUT);

    /**
     * <p>The main web interface flag.  Tells the compiler to modify
     * some of the output to be more user-friendly for the web.</p>
     */
    public static final Flag FLAG_EXPORT_AST =
            new Flag(FLAG_SECTION_NAME, "exportAST", FLAG_DESC_EXPORT_AST,
                    Flag.Type.HIDDEN);

    //private String myTargetSource = null;
    //private String myTargetFileName = null;
    private HashMap<String, MetaFile> myUserFileMap;
    private CompileReport myCompileReport;
    private MetaFile myInputFile;

    public ResolveCompiler(String[] args, MetaFile inputFile,
            String customFacilityName, HashMap<String, MetaFile> userFileMap) {
        myCompileReport = new CompileReport();
        myCompileReport.setFacilityName(customFacilityName);
        myInputFile = inputFile;
        myUserFileMap = userFileMap;
        //myTargetFileName = fileName;
        //System.out.println(fileName);
        //Main.main(args);
    }

    public ResolveCompiler(String[] args, MetaFile inputFile,
            HashMap<String, MetaFile> userFileMap) {
        myCompileReport = new CompileReport();
        myCompileReport.setFacilityName(inputFile.getMyFileName());
        //myTargetFileName = fileArray[0];
        //myTargetSource = fileArray[3];
        myInputFile = inputFile;
        myUserFileMap = userFileMap;
        //System.out.println(fileName);
        //Main.main(args);
    }

    public ResolveCompiler(String[] args) {
        myCompileReport = new CompileReport();
        //myCompileReport.setFacilityName(inputFile.getMyFileName());
        //myTargetFileName = fileArray[0];
        //myTargetSource = fileArray[3];
        myUserFileMap = new HashMap<String, MetaFile>();
        //System.out.println(fileName);
        //Main.main(args);
    }

    public ResolveCompiler(String[] args, HashMap<String, MetaFile> userFileMap) {
        myCompileReport = new CompileReport();
        //myCompileReport.setFacilityName(inputFile.getMyFileName());
        //myTargetFileName = fileArray[0];
        //myTargetSource = fileArray[3];
        //myUserFileMap = userFileMap;
        myUserFileMap = new HashMap<String, MetaFile>();
        //System.out.println(fileName);
        //Main.main(args);
    }

    public void createMeta(String fileName, String assocConcept, String pkg,
            String fileSource, String modKind) {
        //String fileName, String assocConcept, String pkg, String fileSource, ModuleKind kind
        ModuleKind kind = null;
        if (modKind.equals("CONCEPT"))
            kind = ModuleKind.CONCEPT;
        else if (modKind.equals("ENHANCEMENT"))
            kind = ModuleKind.ENHANCEMENT;
        else if (modKind.equals("FACILITY"))
            kind = ModuleKind.FACILITY;
        else if (modKind.equals("REALIZATION"))
            kind = ModuleKind.REALIZATION;
        else if (modKind.equals("THEORY"))
            kind = ModuleKind.THEORY;
        else
            kind = ModuleKind.UNDEFINED;

        myInputFile =
                new MetaFile(fileName, assocConcept, pkg, fileSource, kind);
        String key = pkg + "." + fileName;
        myUserFileMap.put(key, myInputFile);
    }

    public void compile(String[] args) {
        Main.runMain(args, myCompileReport, myInputFile, myUserFileMap);
    }

    public void compile(String[] args, ProverListener listener) {
        Main.runMain(args, myCompileReport, myInputFile, myUserFileMap,
                listener);
    }

    /*public void wsCompile(String[] args, WebSocketWriter writer){
            myWsWriter = writer;
            //myCompileReport.setWsWriter(writer);
    	Main.runMain(args, myCompileReport, myInputFile, myUserFileMap);
    }*/

    /*public void setFacilityName(String facName){
    	myReport.setFacilityName(facName);
    }*/

    public CompileReport getReport() {
        return myCompileReport;
    }

    public void resetReport() {
        myCompileReport.resetReport();
    }

    public boolean proved() {
        //boolean proved = Prover.allProved;
        boolean proved = myCompileReport.proveSuccess();
        //Prover.allProved = false;
        return proved;
    }

    public boolean archived() {
        //CompileReport myReport = CompileReport.getInstance();
        boolean archived = myCompileReport.jarSuccess();
        return archived;
    }

    public boolean hasError() {
        //CompileReport myReport = CompileReport.getInstance();
        boolean error = myCompileReport.hasError();
        return error;
    }

    public static String webEncode(String s) {
        String encoded = null;
        try {
            // Replace all instances of escaped quotes with the HTML equivalent
            s = s.replaceAll("\"", "&quot;");

            // Replace all spaces with the HTML equivalent
            s = s.replaceAll(" ", "%20");

            // Encode the string for WebIDE
            encoded = URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {

        }
        return encoded;
    }

    public static final void setUpFlags() {
        FlagDependencies.addRequires(FLAG_ERRORS_ON_STD_OUT, FLAG_WEB);
        FlagDependencies.addImplies(FLAG_WEB, FLAG_ERRORS_ON_STD_OUT);
        FlagDependencies.addImplies(FLAG_WEB, FLAG_NO_DEBUG);
        FlagDependencies.addImplies(FLAG_WEB, FLAG_XML_OUT);
        FlagDependencies.addImplies(FLAG_WEB, Prover.FLAG_NOGUI);
    }
}
