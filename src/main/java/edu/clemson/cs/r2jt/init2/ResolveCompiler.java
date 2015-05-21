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
import edu.clemson.cs.r2jt.init2.file.ModuleType;
import edu.clemson.cs.r2jt.init2.file.ResolveFile;
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
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ANTLRInputStream;

/**
 * TODO: Description for this class
 */
public class ResolveCompiler {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private boolean myCompileAllFilesInDir = false;
    private final String[] myCompilerArgs;
    private final String myCompilerVersion = "Summer 2015";
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
     * <p>TODO: Add description.</p>
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
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /*
     * Attempts to compile all physical files specified by the argument list
     */
    private void compileRealFiles(List<String> fileArgList,
            CompileEnvironment compileEnvironment)
            throws FileNotFoundException,
                IllegalArgumentException {
        // Loop through the argument list to determine if it is a file or a directory
        for (String fileString : fileArgList) {
            // Convert to a file object
            File file = getAbsoluteFile(fileString);

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
                ModuleType moduleType = getModuleType(file.getName());

                // Print error message if it is not a valid RESOLVE file
                if (moduleType == null) {
                    System.err.println("The file " + file.getName()
                            + " is not a RESOLVE file.");
                }
                else {
                    // Convert to the internal representation of a RESOLVE file
                    String name = getFileName(file.getName(), moduleType);
                    String workspacePath =
                            compileEnvironment.getWorkspaceDir()
                                    .getAbsolutePath();
                    List<String> pkgList =
                            getPackageList(file.getAbsolutePath(),
                                    workspacePath);
                    ANTLRInputStream inputStream =
                            new ANTLRInputStream(file.getAbsolutePath());
                    ResolveFile f =
                            new ResolveFile(name, moduleType, inputStream,
                                    workspacePath, pkgList, file
                                            .getAbsolutePath());

                    // Invoke the compiler
                    compileMainFile(f, compileEnvironment);
                }
            }
        }
    }

    /*
     * This method finds all RESOLVE files in the directory and
     * adds those to files the compiler will compile/verify.
     */
    private void compileFilesInDir(File directory,
            CompileEnvironment compileEnvironment) {
        File[] filesInDir = directory.listFiles();
        List<String> fileList = new LinkedList<String>();

        // Obtain all RESOLVE files in the directory and add those as new files
        // we need to compile.
        for (File f : filesInDir) {
            if (getModuleType(f.getName()) != null) {
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
        catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    /*
     * This method will instantiate the controller and
     * begin the compilation process for the specified file.
     */
    private void compileMainFile(ResolveFile file,
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

    /*
     * Returns the file name without the extension.
     */
    private String getFileName(String fileName, ModuleType moduleType) {
        return fileName.substring(0, fileName.lastIndexOf(moduleType
                .getExtension()) - 1);
    }

    /*
     * The folder RESOLVE and it's sub folders are viewed by the compiler
     * as "packages", therefore we will need to store these for future use.
     */
    private List<String> getPackageList(String filePath, String workspacePath) {
        // Obtain the relative path using the workspace path and current file path.
        String relativePath = filePath.substring(workspacePath.length() + 1);

        // Add all package names using the Java Collections
        List<String> pkgList = new LinkedList<String>();
        Collections.addAll(pkgList, relativePath.split(Pattern
                .quote(File.separator)));

        return pkgList;
    }

    /*
     * Get the absolute path to the RESOLVE Workspace. This workspace
     * must have the same structure as the one hosted on Github.
     */
    private File getWorkspaceDir(String path) {
        File resolvePath = null;
        String resolveDirName = "RESOLVE";

        // Look in the specified path
        if (path != null) {
            resolvePath = new File(path);

            // Not a valid path
            if (!resolvePath.exists()) {
                System.err.println("Warning: Directory '" + resolveDirName
                        + "' not found, using current " + "directory.");

                resolvePath = null;
            }
        }

        // Attempt to locate the folder containing the folder "RESOLVE"
        if (resolvePath == null) {
            File currentDir = getAbsoluteFile("");

            // Check to see if our current path is the path that contains
            // the RESOLVE folder.
            if (currentDir.getName().equals(resolveDirName)) {
                resolvePath = currentDir;
            }

            // Attempt to locate the "RESOLVE" folder
            while ((resolvePath == null)
                    && (currentDir.getParentFile() != null)) {
                currentDir = currentDir.getParentFile();
                if (currentDir.getName().equals(resolveDirName)) {
                    // We store the path that contains the "RESOLVE" folder
                    resolvePath = currentDir.getParentFile();
                }
            }

            // Probably will crash because we can't find "RESOLVE"
            if (resolvePath == null) {
                System.err.println("Warning: Directory '" + resolveDirName
                        + "' not found, using current directory.");

                resolvePath = getAbsoluteFile("");
            }
        }

        return resolvePath;
    }

    /*
     * Method that handles the basic arguments.
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
                compileEnvironment
                        .setWorkspaceDir(getWorkspaceDir(workspaceDir));

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
     * Determines if the specified filename is a valid RESOLVE file type.
     */
    private ModuleType getModuleType(String filename) {
        ModuleType type = null;

        if (filename.endsWith(ModuleType.THEORY.getExtension())) {
            type = ModuleType.THEORY;
        }
        else if (filename.endsWith(ModuleType.CONCEPT.getExtension())) {
            type = ModuleType.CONCEPT;
        }
        else if (filename.endsWith(ModuleType.ENHANCEMENT.getExtension())) {
            type = ModuleType.ENHANCEMENT;
        }
        else if (filename.endsWith(ModuleType.REALIZATION.getExtension())) {
            type = ModuleType.REALIZATION;
        }
        else if (filename.endsWith(ModuleType.FACILITY.getExtension())) {
            type = ModuleType.FACILITY;
        }
        else if (filename.endsWith(ModuleType.PROFILE.getExtension())) {
            type = ModuleType.PROFILE;
        }

        return type;
    }

    /*
     * This prints the help message that prints out all the optional flags.
     */
    private void printHelpMessage(CompileEnvironment e) {
        System.out.println("Usage: java -jar RESOLVE.jar [options] <files>");
        System.out.println("where options include:");
        System.out.println();

        // General flags
        System.out.println("  -R             Recurse through directories.");

        // Prover flags

        // Translator flags
        System.out.println("  -translate     Translate to Java code.");

        // VC Generator flags
        System.out.println("  -PVCs           Generate verification "
                + "conditions for performance.");
        System.out.println("  -VCs           Generate verification "
                + "conditions.");

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

    /*
     * Add all the required and implied flags. Including those needed
     * by the WebIDE.
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