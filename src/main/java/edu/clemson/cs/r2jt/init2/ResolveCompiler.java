/**
 * ResolveCompiler.java
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
import edu.clemson.cs.r2jt.init2.file.ConcreteFile;
import edu.clemson.cs.r2jt.init2.file.FileInterface;
import edu.clemson.cs.r2jt.init2.misc.CompileEnvironment;
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
public class ResolveCompiler {

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

    public ResolveCompiler(String[] args) {
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
        CompileEnvironment compileEnvironment = handleCompileArgs();

        // Print Compiler Messages
        System.out.println("RESOLVE Compiler/Verifier - " + myCompilerVersion
                + " Version.");
        System.out.println("  Use -help flag for options.");

        // Compile files/directories listed in the argument list
        compileFiles(myFilesToCompile, compileEnvironment);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private void compileFiles(List<File> fileList,
            CompileEnvironment compileEnvironment) {
        // Compile all files
        for (File file : fileList) {
            // Recursively compile all RESOLVE files in the specified directory
            if (file.isDirectory()) {
                if (myCompileAllFilesInDir) {
                    compileFilesInDir(file, compileEnvironment);
                }
                else {
                    System.err.println("Skipping directory " + file.getName());
                }
            }
            // Print error message if it is not a valid RESOLVE file
            else if (!isResolveFile(file.getName())) {
                System.err.println("The file " + file.getName()
                        + " is not a RESOLVE file.");
            }
            // Error if we can't locate the file
            else if (!file.isFile()) {
                System.err.println("Cannot find the file " + file.getName()
                        + " in this directory.");
            }
            else {
                // Convert to the internal representation of a RESOLVE file
                FileInterface f = new ConcreteFile();

                // Invoke the compiler
                compileMainFile(f, compileEnvironment);
            }
        }
    }

    private void compileFilesInDir(File directory,
            CompileEnvironment compileEnvironment) {
        File[] filesInDir = directory.listFiles();
        List<File> fileList = new LinkedList<File>();

        // Obtain all RESOLVE files in the directory and add those as new files
        // we need to compile.
        for (File f : filesInDir) {
            if (isResolveFile(f.getName())) {
                fileList.add(f);
            }
        }

        // Compile these files first
        compileFiles(fileList, compileEnvironment);
    }

    private void compileMainFile(FileInterface file,
            CompileEnvironment compileEnvironment) {
        Controller controller = new Controller(compileEnvironment);
        controller.compileTargetFile(file);
    }

    /*
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

    private CompileEnvironment handleCompileArgs() {
        CompileEnvironment compileEnvironment = null;
        try {
            compileEnvironment = new CompileEnvironment(myCompilerArgs);

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
                        .isFlagSet(ResolveCompiler.FLAG_NO_DEBUG)) {
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

    /*
     * Determines if the specified filename is a valid RESOLVE filename.
     */
    private boolean isResolveFile(String filename) {
        return (filename.endsWith(".mt") || filename.endsWith(".co")
                || filename.endsWith(".en") || filename.endsWith(".rb")
                || filename.endsWith(".fa") || filename.endsWith(".pp"));
    }

    private void printHelpMessage(CompileEnvironment e) {
        System.out.println("Usage: java -jar RESOLVE.jar [options] <files>");
        System.out.println("where options include:");

        printOptions(e);
    }

    private void printOptions(CompileEnvironment e) {
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