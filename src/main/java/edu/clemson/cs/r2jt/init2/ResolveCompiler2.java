/**
 * ResolveCompiler2.java
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
package edu.clemson.cs.r2jt.init2;

import edu.clemson.cs.r2jt.archiving.Archiver;
import edu.clemson.cs.r2jt.congruenceclassprover.CongruenceClassProver;
import edu.clemson.cs.r2jt.congruenceclassprover.SMTProver;
import edu.clemson.cs.r2jt.init2.model.CompileEnvironment2;
import edu.clemson.cs.r2jt.misc.Flag;
import edu.clemson.cs.r2jt.misc.FlagDependencies;
import edu.clemson.cs.r2jt.misc.FlagDependencyException;
import edu.clemson.cs.r2jt.rewriteprover.AlgebraicProver;
import edu.clemson.cs.r2jt.rewriteprover.Prover;
import edu.clemson.cs.r2jt.translation.CTranslator;
import edu.clemson.cs.r2jt.translation.JavaTranslator;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTableBuilder;
import edu.clemson.cs.r2jt.vcgeneration.VCGenerator;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO: Description for this class
 */
public class ResolveCompiler2 {

    private boolean myCompileAllFilesInDir = false;
    private String[] myCompilerArgs;
    private final String myCompilerVersion = "Summer 2015";
    private final List<File> myFilesToCompile;
    private String myMainDirName = "Main";

    // ===========================================================
    // Flag Strings
    // ===========================================================

    private static final String FLAG_DESC_NO_DEBUG =
            "Remove debugging statements from the compiler output.";
    private static final String FLAG_SECTION_GENERAL = "General";
    private static final String FLAG_SECTION_NAME = "Output";

    // ===========================================================
    // Flags
    // ===========================================================

    public static final Flag FLAG_HELP =
            new Flag(FLAG_SECTION_GENERAL, "help",
                    "Displays this help information.");

    public static final Flag FLAG_EXTENDED_HELP =
            new Flag(FLAG_SECTION_GENERAL, "xhelp",
                    "Displays all flags, including development flags and many others "
                            + "not relevant to most users.");

    /**
     * <p>Tells the compiler to remove debugging messages from the compiler
     * output.</p>
     */
    public static final Flag FLAG_NO_DEBUG =
            new Flag(FLAG_SECTION_NAME, "nodebug", FLAG_DESC_NO_DEBUG);

    // ===========================================================
    // Constructors
    // ===========================================================

    public ResolveCompiler2(String[] args) {
        myCompilerArgs = args;
        myFilesToCompile = new LinkedList<File>();

        // Make sure the flag dependencies are set
        setUpFlagDependencies();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public void invokeCompiler() {
        // Handle all arguments to the compiler
        CompileEnvironment2 compileEnvironment = handleCompileArgs();

        // Print Compiler Messages
        System.out.println("RESOLVE Compiler/Verifier - " + myCompilerVersion
                + " Version.");
        System.out.println("  Use -help flag for options.");

        /*
        // Compile all files
        for (File file : myFilesToCompile) {
            if (file.isDirectory()) {
                if (myCompileAllFilesInDir) {
                    compileFilesInDir(file, compileEnvironment);
                }
                else {
                    System.err.println("Skipping directory " + file.getName());
                }
            }
            else if (!isResolveFile(file.getName())) {
                System.err.println("The file " + file.getName()
                        + " is not a RESOLVE file.");
            }
            else if (!file.isFile()) {
                System.err.println("Cannot find the file " + file.getName()
                        + " in this directory.");
            }
            else {
                compileEnvironment.setTargetFile(file);
                compileMainFile(file, compileEnvironment, compileEnvironment.getSymbolTable());
            }
        }*/
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * Converts the specified pathname to a <code>File</code> representing
     * the absolute path to the pathname.
     */
    private File getAbsoluteFile(String pathname) {
        return new File(pathname).getAbsoluteFile();
    }

    private File getMainDir(String preferredMainDirectory) {
        File mainDir = null;

        if (preferredMainDirectory != null) {
            mainDir = new File(preferredMainDirectory);

            if (!mainDir.exists()) {
                System.err.println("Warning: Directory '"
                        + preferredMainDirectory
                        + "' not found, using current " + "directory.");

                mainDir = getAbsoluteFile("");
            }
        }
        else {
            File currentDir = getAbsoluteFile("");

            if (currentDir.getName().equals(myMainDirName)) {
                mainDir = currentDir;
            }

            while ((mainDir == null) && (currentDir.getParentFile() != null)) {
                currentDir = currentDir.getParentFile();
                if (currentDir.getName().equals(myMainDirName)) {
                    mainDir = currentDir;
                }
            }

            if (mainDir == null) {
                System.err.println("Warning: Directory '" + myMainDirName
                        + "' not found, using current directory.");

                mainDir = getAbsoluteFile("");
            }
        }

        return mainDir;
    }

    private CompileEnvironment2 handleCompileArgs() {
        CompileEnvironment2 compileEnvironment = null;
        try {
            compileEnvironment = new CompileEnvironment2(myCompilerArgs);

            // Change Main Directory
            String preferredMainDirectory = null;

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
                    else if (remainingArgs[i].equalsIgnoreCase("-maindir")) {
                        if (i + 1 < remainingArgs.length) {
                            i++;
                            preferredMainDirectory = remainingArgs[i];
                        }
                    }
                    else if (remainingArgs[i].equals("-D")) {
                        if (i + 1 < remainingArgs.length) {
                            i++;
                            myMainDirName = remainingArgs[i];
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
                        myFilesToCompile.add(getAbsoluteFile(remainingArgs[i]));
                    }
                }

                // Turn off debugging messages
                if (compileEnvironment.flags
                        .isFlagSet(ResolveCompiler2.FLAG_NO_DEBUG)) {
                    compileEnvironment.setDebugOff();
                }

                // Store the main directory to the compile environment
                compileEnvironment
                        .setMainDir(getMainDir(preferredMainDirectory));

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

    private void printHelpMessage(CompileEnvironment2 e) {
        System.out.println("Usage: java -jar RESOLVE.jar [options] <files>");
        System.out.println("where options include:");

        printOptions(e);
    }

    private void printOptions(CompileEnvironment2 e) {
        System.out.println("  -R             Recurse through directories.");
        System.out.println("  -D <dir>       Use <dir> as the main directory.");
        System.out.println("  -translate     Translate to Java code.");
        System.out.println("  -PVCs           Generate verification "
                + "conditions for performance.");
        System.out.println("  -VCs           Generate verification "
                + "conditions.");
        System.out.println("  -isabelle      Used with -VCs to generate "
                + "VCs for Isabelle.");

        System.out.println(FlagDependencies.getListingString(e.flags
                .isFlagSet(FLAG_EXTENDED_HELP)));
    }

    /*
     * This method sets up dependencies between compiler flags.  If you are
     * integrating your module into the compiler flag management system, this is
     * where to do it.
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

    private void setUpFlags() {
        FlagDependencies.addImplies(FLAG_EXTENDED_HELP, FLAG_HELP);
    }

}