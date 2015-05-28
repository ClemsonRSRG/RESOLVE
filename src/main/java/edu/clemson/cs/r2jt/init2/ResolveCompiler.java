/**
 * ResolveCompiler.java
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
package edu.clemson.cs.r2jt.init2;

import edu.clemson.cs.r2jt.archiving.Archiver;
import edu.clemson.cs.r2jt.congruenceclassprover.CongruenceClassProver;
import edu.clemson.cs.r2jt.congruenceclassprover.SMTProver;
import edu.clemson.cs.r2jt.init2.file.ModuleType;
import edu.clemson.cs.r2jt.init2.file.ResolveFile;
import edu.clemson.cs.r2jt.init2.file.Utilities;
import edu.clemson.cs.r2jt.misc.Flag;
import edu.clemson.cs.r2jt.misc.FlagDependencies;
import edu.clemson.cs.r2jt.misc.FlagDependencyException;
import edu.clemson.cs.r2jt.rewriteprover.AlgebraicProver;
import edu.clemson.cs.r2jt.rewriteprover.Prover;
import edu.clemson.cs.r2jt.rewriteprover.ProverListener;
import edu.clemson.cs.r2jt.translation.CTranslator;
import edu.clemson.cs.r2jt.translation.JavaTranslator;
import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTableBuilder;
import edu.clemson.cs.r2jt.vcgeneration.VCGenerator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>This class takes care of all argument processing and creates
 * a <code>CompileEnvironment</code> for the current job.</p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 * @version 1.0
 */
public class ResolveCompiler {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>Boolean flag that checks to see if we need to compile
     * all the files in the folder.</p>
     */
    private boolean myCompileAllFilesInDir = false;

    /**
     * <p>This stores all the arguments received by the RESOLVE
     * compiler.</p>
     */
    private final String[] myCompilerArgs;

    /**
     * <p>This indicates the current compiler version.</p>
     */
    private final String myCompilerVersion = "Summer 2015";

    /**
     * <p>This stores all the file names specified in the argument
     * list.</p>
     */
    private final List<String> myArgumentFileList;

    // ===========================================================
    // Flag Strings
    // ===========================================================

    private static final String FLAG_DESC_NO_DEBUG =
            "Remove debugging statements from the compiler output.";
    private static final String FLAG_DESC_ERRORS_ON_STD_OUT =
            "Change the output to be more web-friendly for the Web Interface.";
    private static final String FLAG_DESC_XML_OUT =
            "Changes the compiler output files to XML";
    private static final String FLAG_DESC_WEB =
            "Change the output to be more web-friendly for the Web Interface.";
    private static final String FLAG_SECTION_GENERAL = "General";
    private static final String FLAG_SECTION_NAME = "Output";

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>Tells the compiler to print out a general help message and
     * all the flags.</p>
     */
    public static final Flag FLAG_HELP =
            new Flag(FLAG_SECTION_GENERAL, "help",
                    "Displays this help information.");

    /**
     * <p>Tells the compiler to print out all the flags.</p>
     */
    public static final Flag FLAG_EXTENDED_HELP =
            new Flag(FLAG_SECTION_GENERAL, "xhelp",
                    "Displays all flags, including development flags and many others "
                            + "not relevant to most users.");

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
    public static final Flag FLAG_WEB =
            new Flag(FLAG_SECTION_NAME, "webinterface", FLAG_DESC_WEB,
                    Flag.Type.HIDDEN);

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a "handler" type object for RESOLVE
     * compiler arguments. This constructor takes care of
     * all possible flag dependencies and will work for both
     * invoking from the command line and from the WebIDE/WebAPI.</p>
     *
     * @param args The specified compiler arguments array.
     */
    public ResolveCompiler(String[] args) {
        myCompilerArgs = args;
        myArgumentFileList = new LinkedList<String>();

        // Make sure the flag dependencies are set
        setUpFlagDependencies();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This invokes the RESOLVE compiler. Usually this method
     * is called by running the compiler from the command line.</p>
     */
    public void invokeCompiler() {
        // Handle all arguments to the compiler
        CompileEnvironment compileEnvironment = handleCompileArgs();

        // Print Compiler Messages
        System.out.println("RESOLVE Compiler/Verifier - " + myCompilerVersion
                + " Version.");
        System.out.println("  Use -help flag for options.");

        // Compile files/directories listed in the argument list
        try {
            compileRealFiles(myArgumentFileList, compileEnvironment);
        }
        catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * <p>This invokes the RESOLVE compiler. Usually this method
     * is called by running the compiler from the WebAPI/WebIDE.</p>
     *
     * @param fileMap A map containing all the user modified files.
     * @param proverListener A listener object that needs to be
     *                       passed to the prover.
     */
    public void invokeCompiler(Map<String, ResolveFile> fileMap,
            ProverListener proverListener) {
        // Handle all arguments to the compiler
        CompileEnvironment compileEnvironment = handleCompileArgs();

        // Create a new compilation report needed by the WebAPI/WebIDE
        compileEnvironment.setCompileReport(new CompileReport());

        // Store the file map
        compileEnvironment.setFileMap(fileMap);

        // Store the listener required by all provers
        compileEnvironment.setProverListener(proverListener);

        // Compile files/directories listed in the argument list
        try {
            compileArbitraryFiles(myArgumentFileList, compileEnvironment);
        }
        catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Attempts to compile all "meta" files files specified by the
     * argument list. If the "meta" file is not supplied, attempt to
     * search for it as a physical file.</p>
     *
     * @param fileArgList List of strings representing the name of the file.
     * @param compileEnvironment The current job's compilation environment
     *                           that stores all necessary objects and flags.
     *
     * @throws IOException
     * @throws IllegalArgumentException
     */
    private void compileArbitraryFiles(List<String> fileArgList,
            CompileEnvironment compileEnvironment)
            throws IOException,
                IllegalArgumentException {
        // Loop through the argument list to determine if it is a file or a directory
        for (String fileString : fileArgList) {
            // First check if this is a "meta" file
            if (compileEnvironment.isMetaFile(fileString)) {
                // Invoke the compiler on this file
                compileMainFile(compileEnvironment
                        .getUserFileFromMap(fileString), compileEnvironment);
            }
            // If not, it must be a physical file. Use the compileRealFile method.
            else {
                List<String> newFileList = new LinkedList<String>();
                newFileList.add(fileString);

                compileRealFiles(newFileList, compileEnvironment);
            }
        }
    }

    /**
     * <p>This method finds all RESOLVE files in the directory and
     * adds those to files the compiler will compile/verify.</p>
     *
     * @param directory The directory we are searching for RESOLVE files
     * @param compileEnvironment The current job's compilation environment
     *                           that stores all necessary objects and flags.
     */
    private void compileFilesInDir(File directory,
            CompileEnvironment compileEnvironment) {
        File[] filesInDir = directory.listFiles();
        List<String> fileList = new LinkedList<String>();

        // Obtain all RESOLVE files in the directory and add those as new files
        // we need to compile.
        for (File f : filesInDir) {
            if (Utilities.getModuleType(f.getName()) != null) {
                fileList.add(f.getName());
            }
        }

        // Compile these files first
        try {
            compileRealFiles(fileList, compileEnvironment);
        }
        catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * <p>This method will instantiate the controller and
     * begin the compilation process for the specified file.</p>
     *
     * @param file The current <code>ResolveFile</code> specified by
     *             the argument list we wish to compile.
     * @param compileEnvironment The current job's compilation environment
     *                           that stores all necessary objects and flags.
     */
    private void compileMainFile(ResolveFile file,
            CompileEnvironment compileEnvironment) {
        Controller controller = new Controller(compileEnvironment);
        controller.compileTargetFile(file);
    }

    /**
     * <p>Attempts to compile all physical files specified by the
     * argument list.</p>
     *
     * @param fileArgList List of strings representing the name of the file.
     * @param compileEnvironment The current job's compilation environment
     *                           that stores all necessary objects and flags.
     *
     * @throws IOException
     * @throws IllegalArgumentException
     */
    private void compileRealFiles(List<String> fileArgList,
            CompileEnvironment compileEnvironment)
            throws IOException,
                IllegalArgumentException {
        // Loop through the argument list to determine if it is a file or a directory
        for (String fileString : fileArgList) {
            // Convert to a file object
            File file = Utilities.getAbsoluteFile(fileString);

            // Error if we can't locate the file
            if (!file.isFile()) {
                throw new FileNotFoundException("Cannot find the file "
                        + file.getName() + " in this directory.");
            }
            // Recursively compile all RESOLVE files in the specified directory
            else if (file.isDirectory()) {
                // If the option to compile all files in the directory is given
                if (myCompileAllFilesInDir) {
                    compileFilesInDir(file, compileEnvironment);
                }
                else {
                    throw new IllegalArgumentException(
                            "Option to compile all files in the directory not set. Skipping directory "
                                    + file.getName());
                }
            }
            // Process this file
            else {
                ModuleType moduleType = Utilities.getModuleType(file.getName());

                // Print error message if it is not a valid RESOLVE file
                if (moduleType == null) {
                    System.err.println("The file " + file.getName()
                            + " is not a RESOLVE file.");
                }
                else {
                    String workspacePath =
                            compileEnvironment.getWorkspaceDir()
                                    .getAbsolutePath();
                    ResolveFile f =
                            Utilities.convertToResolveFile(file, moduleType,
                                    workspacePath);

                    // Invoke the compiler
                    compileMainFile(f, compileEnvironment);
                }
            }
        }
    }

    /**
     * <p>Method that handles the basic arguments and returns a
     * <code>CompileEnvironment</code> that includes information
     * on the current compilation job.</p>
     *
     * @return A new <code>CompileEnvironment</code> for the current job.
     */
    private CompileEnvironment handleCompileArgs() {
        CompileEnvironment compileEnvironment = null;
        try {
            // Instantiate a new compile environment that will store
            // all the necessary information needed throughout the compilation
            // process.
            compileEnvironment = new CompileEnvironment(myCompilerArgs);

            // Workspace Directory
            String workspaceDir = null;

            // Handle remaining arguments
            String[] remainingArgs = compileEnvironment.getRemainingArgs();
            if (remainingArgs.length >= 1
                    && !compileEnvironment.flags.isFlagSet(FLAG_HELP)) {
                for (int i = 0; i < remainingArgs.length; i++) {
                    if (remainingArgs[i].equals("-R")) {
                        myCompileAllFilesInDir = true;
                    }
                    else if (remainingArgs[i].equals("-PVCs")) {
                        compileEnvironment.setPerformanceFlag();
                    }
                    else if (remainingArgs[i].equalsIgnoreCase("-workspaceDir")) {
                        if (i + 1 < remainingArgs.length) {
                            i++;
                            workspaceDir = remainingArgs[i];
                        }
                    }
                    else if (remainingArgs[i].equals("-o")) {
                        if (i + 1 < remainingArgs.length) {
                            String outputFile;
                            i++;
                            outputFile = remainingArgs[i];
                            compileEnvironment.setOutputFileName(outputFile);
                        }
                    }
                    else {
                        myArgumentFileList.add(remainingArgs[i]);
                    }
                }

                // Turn off debugging messages
                if (compileEnvironment.flags
                        .isFlagSet(ResolveCompiler.FLAG_NO_DEBUG)) {
                    compileEnvironment.setDebugOff();
                }

                // Store the workspace directory to the compile environment
                compileEnvironment.setWorkspaceDir(Utilities
                        .getWorkspaceDir(workspaceDir));

                // Store the symbol table
                MathSymbolTableBuilder symbolTable =
                        new MathSymbolTableBuilder();
                compileEnvironment.setSymbolTable(symbolTable);
            }
            else {
                printHelpMessage(compileEnvironment);
            }
        }
        catch (FlagDependencyException fde) {
            System.out.println("RESOLVE Compiler/Verifier - "
                    + myCompilerVersion + " Version.");
            System.out.println("  Use -help flag for options.");
            System.err.println(fde.getMessage());
        }

        return compileEnvironment;
    }

    /**
     * <p>This prints the help message that prints out all the optional flags.</p>
     *
     * @param compileEnvironment The current job's compilation environment
     *                           that stores all necessary objects and flags.
     */
    private void printHelpMessage(CompileEnvironment compileEnvironment) {
        System.out.println("Usage: java -jar RESOLVE.jar [options] <files>");
        System.out.println("where options include:");
        System.out.println();

        // General flags
        System.out.println("  -R             Recurse through directories.");

        // TODO: Each module should have a common method that specifies all the relevant flags

        // Prover flags

        // Translator flags
        System.out.println("  -translate     Translate to Java code.");

        // VC Generator flags
        System.out.println("  -PVCs           Generate verification "
                + "conditions for performance.");
        System.out.println("  -VCs           Generate verification "
                + "conditions.");

        System.out.println(FlagDependencies
                .getListingString(compileEnvironment.flags
                        .isFlagSet(FLAG_EXTENDED_HELP)));
    }

    /**
     * <p>This method sets up dependencies between compiler flags. If you are
     * integrating your module into the compiler flag management system, this is
     * where to do it.</p>
     */
    private synchronized void setUpFlagDependencies() {
        if (!FlagDependencies.isSealed()) {
            setUpFlags();
            Prover.setUpFlags();
            JavaTranslator.setUpFlags();
            CTranslator.setUpFlags();
            Archiver.setUpFlags();
            VCGenerator.setUpFlags();
            AlgebraicProver.setUpFlags();
            CongruenceClassProver.setUpFlags();
            SMTProver.setUpFlags();
            FlagDependencies.seal();
        }
    }

    /**
     * <p>Add all the required and implied flags. Including those needed
     * by the WebIDE.</p>
     */
    private void setUpFlags() {
        // Extended help implies that the general help is also on.
        FlagDependencies.addImplies(FLAG_EXTENDED_HELP, FLAG_HELP);

        // WebIDE
        FlagDependencies.addRequires(FLAG_ERRORS_ON_STD_OUT, FLAG_WEB);
        FlagDependencies.addImplies(FLAG_WEB, FLAG_ERRORS_ON_STD_OUT);
        FlagDependencies.addImplies(FLAG_WEB, FLAG_NO_DEBUG);
        FlagDependencies.addImplies(FLAG_WEB, FLAG_XML_OUT);
        FlagDependencies.addImplies(FLAG_WEB, Prover.FLAG_NOGUI);
    }

}