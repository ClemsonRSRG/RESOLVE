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
package edu.clemson.cs.rsrg.init;

import edu.clemson.cs.r2jt.archiving.Archiver;
import edu.clemson.cs.r2jt.congruenceclassprover.CongruenceClassProver;
import edu.clemson.cs.r2jt.congruenceclassprover.SMTProver;
import edu.clemson.cs.rsrg.errorhandling.exception.CompilerException;
import edu.clemson.cs.rsrg.errorhandling.exception.FlagDependencyException;
import edu.clemson.cs.rsrg.errorhandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.init.file.ModuleType;
import edu.clemson.cs.rsrg.init.file.ResolveFile;
import edu.clemson.cs.rsrg.init.flag.Flag;
import edu.clemson.cs.rsrg.init.flag.FlagDependencies;
import edu.clemson.cs.rsrg.misc.Utilities;
import edu.clemson.cs.r2jt.rewriteprover.AlgebraicProver;
import edu.clemson.cs.r2jt.rewriteprover.Prover;
import edu.clemson.cs.r2jt.rewriteprover.ProverListener;
import edu.clemson.cs.r2jt.translation.CTranslator;
import edu.clemson.cs.r2jt.translation.JavaTranslator;
import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTableBuilder;
import edu.clemson.cs.r2jt.vcgeneration.VCGenerator;
import edu.clemson.cs.rsrg.errorhandling.StdErrHandler;
import edu.clemson.cs.rsrg.errorhandling.ErrorHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

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
     * <p>This stores all the arguments received by the RESOLVE
     * compiler.</p>
     */
    private final String[] myCompilerArgs;

    /**
     * <p>This indicates the current compiler version.</p>
     */
    private final String myCompilerVersion = "Fall 2015";

    /**
     * <p>This stores all the file names specified in the argument
     * list.</p>
     */
    private final List<String> myArgumentFileList;

    // ===========================================================
    // Flag Strings
    // ===========================================================

    private static final String FLAG_DESC_DEBUG =
            "Print debugging statements from the compiler output.";
    private static final String FLAG_DESC_WORKSPACE_DIR =
            "Changes the workspace directory path.";
    private static final String FLAG_SECTION_GENERAL = "General";
    private static final String FLAG_SECTION_NAME = "Output";

    private static final String[] WORKSPACE_DIR_ARG_NAME = { "Path" };

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
     * <p>Tells the compiler to print debugging messages from the compiler
     * output.</p>
     */
    public static final Flag FLAG_DEBUG =
            new Flag(FLAG_SECTION_NAME, "debug", FLAG_DESC_DEBUG);

    /**
     * <p>Tells the compiler to print debugging messages from the compiler
     * output to a file.</p>
     */
    public static final Flag FLAG_DEBUG_FILE_OUT =
            new Flag(FLAG_SECTION_NAME, "debugOutToFile", FLAG_DESC_DEBUG);

    /**
     * <p>Tells the compiler to print compiler exception's stack traces.</p>
     */
    public static final Flag FLAG_DEBUG_STACK_TRACE =
            new Flag(FLAG_SECTION_NAME, "stacktrace", FLAG_DESC_DEBUG);

    /**
     * <p>Tells the compiler the RESOLVE workspace directory path.</p>
     */
    public static final Flag FLAG_WORKSPACE_DIR =
            new Flag(FLAG_SECTION_GENERAL, "workspaceDir",
                    FLAG_DESC_WORKSPACE_DIR, WORKSPACE_DIR_ARG_NAME);

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
        myArgumentFileList = new LinkedList<>();

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
        // Create a error handler
        ErrorHandler errorHandler = new StdErrHandler();

        // Handle all arguments to the compiler
        CompileEnvironment compileEnvironment = handleCompileArgs(errorHandler);

        // Compile files/directories listed in the argument list
        try {
            compileRealFiles(myArgumentFileList, compileEnvironment);
        }
        catch (CompilerException e) {
            // YS - The error handler object might have changed.
            errorHandler = compileEnvironment.getErrorHandler();
            errorHandler.error(null, e.getMessage());
            if (compileEnvironment.flags.isFlagSet(FLAG_DEBUG_STACK_TRACE)) {
                e.printStackTrace();
            }
            errorHandler.stopLogging();
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
            ErrorHandler errorHandler, ProverListener proverListener) {
        // Handle all arguments to the compiler
        CompileEnvironment compileEnvironment = handleCompileArgs(errorHandler);

        // Store the file map
        compileEnvironment.setFileMap(fileMap);

        // Store the listener required by all provers
        compileEnvironment.setProverListener(proverListener);

        // Compile files/directories listed in the argument list
        try {
            compileArbitraryFiles(myArgumentFileList, compileEnvironment);
        }
        catch (CompilerException e) {
            // YS - The error handler object might have changed.
            errorHandler = compileEnvironment.getErrorHandler();
            errorHandler.error(null, e.getMessage());
            if (compileEnvironment.flags.isFlagSet(FLAG_DEBUG_STACK_TRACE)
                    && errorHandler instanceof StdErrHandler) {
                e.printStackTrace();
            }
            errorHandler.stopLogging();
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
     * @throws CompilerException
     */
    private void compileArbitraryFiles(List<String> fileArgList,
            CompileEnvironment compileEnvironment) throws CompilerException {
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
                List<String> newFileList = new LinkedList<>();
                newFileList.add(fileString);

                compileRealFiles(newFileList, compileEnvironment);
            }
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
     * @throws CompilerException
     */
    private void compileRealFiles(List<String> fileArgList,
            CompileEnvironment compileEnvironment) throws CompilerException {
        // Loop through the argument list to determine if it is a file or a directory
        for (String fileString : fileArgList) {
            // Convert to a file object
            File file = Utilities.getAbsoluteFile(fileString);

            // Error if we can't locate the file
            if (!file.isFile()) {
                throw new MiscErrorException("Cannot find the file "
                        + file.getName() + " in this directory.",
                        new FileNotFoundException());
            }
            // Recursively compile all RESOLVE files in the specified directory
            else if (file.isDirectory()) {
                throw new MiscErrorException(
                        file.getName()
                                + " is an directory. Directories cannot be specified as an argument to the RESOLVE compiler.",
                        new IllegalArgumentException());
            }
            // Process this file
            else {
                ModuleType moduleType = Utilities.getModuleType(file.getName());

                // Print error message if it is not a valid RESOLVE file
                if (moduleType == null) {
                    throw new MiscErrorException("The file " + file.getName()
                            + " is not a RESOLVE file.",
                            new IllegalArgumentException());
                }
                else {
                    try {
                        String workspacePath =
                                compileEnvironment.getWorkspaceDir()
                                        .getAbsolutePath();
                        ResolveFile f =
                                Utilities.convertToResolveFile(file,
                                        moduleType, workspacePath);

                        // Invoke the compiler
                        compileMainFile(f, compileEnvironment);
                    }
                    catch (IOException ioe) {
                        throw new MiscErrorException(ioe.getMessage(), ioe
                                .getCause());
                    }
                }
            }
        }
    }

    /**
     * <p>Method that handles the basic arguments and returns a
     * <code>CompileEnvironment</code> that includes information
     * on the current compilation job.</p>
     *
     * @param errorHandler An error handler to display debug or error messages.
     *
     * @return A new <code>CompileEnvironment</code> for the current job.
     */
    private CompileEnvironment handleCompileArgs(ErrorHandler errorHandler) {
        CompileEnvironment compileEnvironment = null;
        try {
            // Instantiate a new compile environment that will store
            // all the necessary information needed throughout the compilation
            // process.
            compileEnvironment =
                    new CompileEnvironment(myCompilerArgs, myCompilerVersion,
                            errorHandler);

            if (compileEnvironment.flags.isFlagSet(FLAG_HELP)) {
                printHelpMessage(compileEnvironment);
            }
            else {
                // Handle remaining arguments
                String[] remainingArgs = compileEnvironment.getRemainingArgs();
                if (remainingArgs.length == 0) {
                    throw new FlagDependencyException(
                            "Need to specify a filename.");
                }
                else {
                    // The remaining arguments must be filenames, so we add those
                    // to our list of files to compile.
                    for (String arg : remainingArgs) {
                        myArgumentFileList.add(arg);
                    }
                }

                // Store the symbol table
                MathSymbolTableBuilder symbolTable =
                        new MathSymbolTableBuilder();
                compileEnvironment.setSymbolTable(symbolTable);
            }
        }
        catch (FlagDependencyException | IOException e) {
            // YS - The error handler object might have changed.
            errorHandler = compileEnvironment.getErrorHandler();
            errorHandler.error(null, e.getMessage());
            if (compileEnvironment.flags.isFlagSet(FLAG_DEBUG_STACK_TRACE)
                    && errorHandler instanceof StdErrHandler) {
                e.printStackTrace();
            }
            errorHandler.stopLogging();
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
        if (compileEnvironment.flags.isFlagSet(FLAG_DEBUG)) {
            ErrorHandler debugHandler = compileEnvironment.getErrorHandler();
            debugHandler.info(null,
                    "Usage: java -jar RESOLVE.jar [options] <files>");
            debugHandler.info(null, "where options include:");
            debugHandler.info(null, FlagDependencies
                    .getListingString(compileEnvironment.flags
                            .isFlagSet(FLAG_EXTENDED_HELP)));
        }
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

        // Debug out to file implies that the debug flag is also on.
        FlagDependencies.addImplies(FLAG_DEBUG_FILE_OUT, FLAG_DEBUG);

        // Stack traces require debug flag is on
        FlagDependencies.addRequires(FLAG_DEBUG_STACK_TRACE, FLAG_DEBUG);
    }
}