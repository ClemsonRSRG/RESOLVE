/*
 * ResolveCompiler.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.init;

import edu.clemson.rsrg.init.file.ModuleType;
import edu.clemson.rsrg.init.file.ResolveFile;
import edu.clemson.rsrg.init.file.ResolveFileBasicInfo;
import edu.clemson.rsrg.init.flag.Flag;
import edu.clemson.rsrg.init.flag.FlagDependencies;
import edu.clemson.rsrg.init.output.OutputListener;
import edu.clemson.rsrg.misc.Utilities;
import edu.clemson.rsrg.nProver.GeneralPurposeProver;
import edu.clemson.rsrg.prover.CongruenceClassProver;
import edu.clemson.rsrg.statushandling.Fault;
import edu.clemson.rsrg.statushandling.FaultType;
import edu.clemson.rsrg.statushandling.SystemStdHandler;
import edu.clemson.rsrg.statushandling.StatusHandler;
import edu.clemson.rsrg.statushandling.exception.CompilerException;
import edu.clemson.rsrg.statushandling.exception.FlagDependencyException;
import edu.clemson.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.rsrg.translation.targets.CTranslator;
import edu.clemson.rsrg.translation.targets.JavaTranslator;
import edu.clemson.rsrg.typeandpopulate.Populator;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.vcgeneration.VCGenerator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * This class takes care of all argument processing and creates a {@link CompileEnvironment} for the current job.
 * </p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 *
 * @version 1.0
 */
public class ResolveCompiler {

    // ===========================================================
    // Constant Fields
    // ===========================================================

    /**
     * <p>
     * This indicates the current compiler version.
     * </p>
     */
    public static final String COMPILER_VERSION = "Summer 2022";

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * This stores all the arguments received by the RESOLVE compiler.
     * </p>
     */
    private final String[] myCompilerArgs;

    /**
     * <p>
     * This stores all the file names specified in the argument list.
     * </p>
     */
    private final List<String> myArgumentFileList;

    // ===========================================================
    // Objects
    // ===========================================================

    /**
     * <p>
     * The list of files that we automatically import to any {@code Concept}, {@code Concept Realization},
     * {@code Enhancement}, {@code Enhancement Realizations}, {@code Facilities}. If you don't want this behavior to
     * happen to a specific file, add it to {@link #NO_AUTO_IMPORT_EXCEPTION_LIST}
     * </p>
     */
    public static final List<String> AUTO_IMPORT_FILES = Collections.unmodifiableList(
            Arrays.asList("Std_Boolean_Fac", "Std_Integer_Fac", "Std_Character_Fac", "Std_Char_Str_Fac"));

    /**
     * <p>
     * The list of files that we ignore the {@link #AUTO_IMPORT_FILES} list.
     * </p>
     */
    public static final List<String> NO_AUTO_IMPORT_EXCEPTION_LIST = Collections.unmodifiableList(
            Arrays.asList("Boolean_Template", "Integer_Template", "Character_Template", "Char_Str_Template"));

    // ===========================================================
    // Flag Strings
    // ===========================================================

    private static final String FLAG_DESC_DEBUG = "Print debugging statements from the compiler output.";
    private static final String FLAG_DESC_NO_FILE_OUTPUT = "Specifies that we do not want the default output to file behavior.";
    private static final String FLAG_DESC_PRINT_MODULE = "Print the modules we are compiling.";
    private static final String FLAG_DESC_EXPORT_AST = "Exports the AST for the target file as a .dot file that can be viewed in Graphviz";
    private static final String FLAG_DESC_WORKSPACE_DIR = "Changes the workspace directory path.";
    private static final String FLAG_SECTION_GENERAL = "General";
    private static final String FLAG_SECTION_DEBUG = "Debugging";

    private static final String[] WORKSPACE_DIR_ARG_NAME = { "Path" };

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>
     * Tells the compiler to print out a general help message and all the flags.
     * </p>
     */
    private static final Flag FLAG_HELP = new Flag(FLAG_SECTION_GENERAL, "help", "Displays this help information.");

    /**
     * <p>
     * Tells the compiler to print out all the flags.
     * </p>
     */
    private static final Flag FLAG_EXTENDED_HELP = new Flag(FLAG_SECTION_GENERAL, "xhelp",
            "Displays all flags, including development flags and many others " + "not relevant to most users.");

    /**
     * <p>
     * Tells the compiler to print debugging messages from the compiler output.
     * </p>
     */
    public static final Flag FLAG_DEBUG = new Flag(FLAG_SECTION_DEBUG, "debug", FLAG_DESC_DEBUG);

    /**
     * <p>
     * Tells the compiler to print debugging messages from the compiler output to a file.
     * </p>
     */
    static final Flag FLAG_DEBUG_FILE_OUT = new Flag(FLAG_SECTION_DEBUG, "debugOutToFile", FLAG_DESC_DEBUG);

    /**
     * <p>
     * Tells the compiler to print compiler exception's stack traces.
     * </p>
     */
    static final Flag FLAG_DEBUG_STACK_TRACE = new Flag(FLAG_SECTION_DEBUG, "stacktrace", FLAG_DESC_DEBUG,
            Flag.Type.HIDDEN);
    /**
     * <p>
     * Tells the compiler not to output anything to file.
     * </p>
     */
    static final Flag FLAG_NO_FILE_OUTPUT = new Flag(FLAG_SECTION_GENERAL, "noFileOutput", FLAG_DESC_NO_FILE_OUTPUT);

    /**
     * <p>
     * Tells the compiler to print the module we are compiling.
     * </p>
     */
    static final Flag FLAG_PRINT_MODULE = new Flag(FLAG_SECTION_DEBUG, "printModule", FLAG_DESC_PRINT_MODULE,
            Flag.Type.HIDDEN);

    /**
     * <p>
     * Tell the compiler to output a Graphviz model for our AST.
     * </p>
     */
    static final Flag FLAG_EXPORT_AST = new Flag(FLAG_SECTION_GENERAL, "exportAST", FLAG_DESC_EXPORT_AST,
            Flag.Type.HIDDEN);

    /**
     * <p>
     * Tells the compiler the RESOLVE workspace directory path.
     * </p>
     */
    static final Flag FLAG_WORKSPACE_DIR = new Flag(FLAG_SECTION_GENERAL, "workspaceDir", FLAG_DESC_WORKSPACE_DIR,
            WORKSPACE_DIR_ARG_NAME);

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a "handler" type object for RESOLVE compiler arguments. This constructor takes care of all possible
     * flag dependencies and will work for both invoking from the command line and from the WebIDE/WebAPI.
     * </p>
     *
     * @param args
     *            The specified compiler arguments array.
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
     * <p>
     * This invokes the RESOLVE compiler. Usually this method is called by running the compiler from the command line.
     * </p>
     */
    public void invokeCompiler() {
        // Create a status handler
        StatusHandler statusHandler = new SystemStdHandler();

        // Handle all arguments to the compiler
        CompileEnvironment compileEnvironment = handleCompileArgs(statusHandler);

        // Compile files/directories listed in the argument list
        try {
            compileRealFiles(myArgumentFileList, compileEnvironment);
        } catch (CompilerException e) {
            // YS - The status handler object might have changed.
            statusHandler = compileEnvironment.getStatusHandler();
            Fault fault = new Fault(FaultType.COMPILER_EXCEPTION, null, e.getMessage(), false);
            statusHandler.registerAndStreamFault(fault);
            if (compileEnvironment.flags.isFlagSet(FLAG_DEBUG_STACK_TRACE)) {
                statusHandler.printStackTrace(e);
            }
            statusHandler.stopLogging();
        }
    }

    /**
     * <p>
     * This invokes the RESOLVE compiler. Usually this method is called by running the compiler from the WebAPI/WebIDE.
     * </p>
     *
     * @param compilingFiles
     *            A map containing all the "meta" files we are going to compile.
     * @param userFilesMap
     *            A map containing all "meta" files that are provided by the user.
     * @param statusHandler
     *            A status handler to display debug or error messages.
     * @param listener
     *            An output listener object.
     */
    public void invokeCompiler(Map<String, ResolveFile> compilingFiles,
            Map<ResolveFileBasicInfo, ResolveFile> userFilesMap, StatusHandler statusHandler, OutputListener listener) {
        // Handle all arguments to the compiler
        CompileEnvironment compileEnvironment = handleCompileArgs(statusHandler);

        // Store the file map
        compileEnvironment.setFileMap(userFilesMap);

        // Store the new listener object
        compileEnvironment.addOutputListener(listener);

        // Compile files/directories listed in the argument list
        try {
            compileArbitraryFiles(myArgumentFileList, compilingFiles, compileEnvironment);
        } catch (CompilerException e) {
            // YS - The status handler object might have changed.
            statusHandler = compileEnvironment.getStatusHandler();
            Fault fault = new Fault(FaultType.COMPILER_EXCEPTION, null, e.getMessage(), false);
            statusHandler.registerAndStreamFault(fault);
            if (compileEnvironment.flags.isFlagSet(FLAG_DEBUG_STACK_TRACE)) {
                statusHandler.printStackTrace(e);
            }
            statusHandler.stopLogging();
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * Attempts to compile all "meta" files files specified by the argument list. If the "meta" file is not supplied,
     * attempt to search for it as a physical file.
     * </p>
     *
     * @param compilingFiles
     *            A map containing all the user "meta" files we are going to compile.
     * @param fileArgList
     *            List of strings representing the name of the file.
     * @param compileEnvironment
     *            The current job's compilation environment that stores all necessary objects and flags.
     *
     * @throws CompilerException
     *             This catches all sorts of exceptions thrown by the compiler.
     */
    private void compileArbitraryFiles(List<String> fileArgList, Map<String, ResolveFile> compilingFiles,
            CompileEnvironment compileEnvironment) throws CompilerException {
        // Loop through the argument list to determine if it is a file or a directory
        for (String fileString : fileArgList) {
            // First check if this is a "meta" file
            if (compilingFiles.containsKey(fileString)) {
                // Invoke the compiler on this file
                compileMainFile(compilingFiles.get(fileString), compileEnvironment);
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
     * <p>
     * This method will instantiate the controller and begin the compilation process for the specified file.
     * </p>
     *
     * @param file
     *            The current <code>ResolveFile</code> specified by the argument list we wish to compile.
     * @param compileEnvironment
     *            The current job's compilation environment that stores all necessary objects and flags.
     */
    private void compileMainFile(ResolveFile file, CompileEnvironment compileEnvironment) {
        Controller controller = new Controller(compileEnvironment);
        controller.compileTargetFile(file);
    }

    /**
     * <p>
     * Attempts to compile all physical files specified by the argument list.
     * </p>
     *
     * @param fileArgList
     *            List of strings representing the name of the file.
     * @param compileEnvironment
     *            The current job's compilation environment that stores all necessary objects and flags.
     *
     * @throws CompilerException
     *             This catches all sorts of exceptions thrown by the compiler.
     */
    private void compileRealFiles(List<String> fileArgList, CompileEnvironment compileEnvironment)
            throws CompilerException {
        // Loop through the argument list to determine if it is a file or a directory
        for (String fileString : fileArgList) {
            // Convert to a file object
            // 1) Find the file using any specified workspace directory.
            // 2) Find the file in the current directory.
            File file;
            if (compileEnvironment.flags.isFlagSet(FLAG_WORKSPACE_DIR)) {
                file = Utilities.getAbsoluteFile(compileEnvironment.getWorkspaceDir(), fileString);
            } else {
                file = Utilities.getAbsoluteFile(fileString);
            }

            // Error if we can't locate the file
            if (!file.isFile()) {
                throw new MiscErrorException("Cannot find the file " + file.getName() + " in this directory.",
                        new FileNotFoundException());
            }
            // Recursively compile all RESOLVE files in the specified directory
            else if (file.isDirectory()) {
                throw new MiscErrorException(file.getName()
                        + " is an directory. Directories cannot be specified as an argument to the RESOLVE compiler.",
                        new IllegalArgumentException());
            }
            // Process this file
            else {
                ModuleType moduleType = Utilities.getModuleType(file.getName());

                // Print error message if it is not a valid RESOLVE file
                if (moduleType == null) {
                    throw new MiscErrorException("The file " + file.getName() + " is not a RESOLVE file.",
                            new IllegalArgumentException());
                } else {
                    try {
                        String workspacePath = compileEnvironment.getWorkspaceDir().getAbsolutePath();
                        ResolveFile f = Utilities.convertToResolveFile(file, moduleType, workspacePath);

                        // Invoke the compiler
                        compileMainFile(f, compileEnvironment);
                    } catch (IOException ioe) {
                        throw new MiscErrorException(ioe.getMessage(), ioe.getCause());
                    }
                }
            }
        }
    }

    /**
     * <p>
     * Method that handles the basic arguments and returns a <code>CompileEnvironment</code> that includes information
     * on the current compilation job.
     * </p>
     *
     * @param statusHandler
     *            A status handler to display debug or error messages.
     *
     * @return A new {@link CompileEnvironment} for the current job.
     */
    private CompileEnvironment handleCompileArgs(StatusHandler statusHandler) {
        CompileEnvironment compileEnvironment = null;
        try {
            // Instantiate a new compile environment that will store
            // all the necessary information needed throughout the compilation
            // process.
            compileEnvironment = new CompileEnvironment(myCompilerArgs, COMPILER_VERSION, statusHandler);

            if (compileEnvironment.flags.isFlagSet(FLAG_HELP)) {
                printHelpMessage(compileEnvironment);
            } else {
                // Handle remaining arguments
                String[] remainingArgs = compileEnvironment.getRemainingArgs();
                if (remainingArgs.length == 0) {
                    throw new FlagDependencyException("Need to specify a filename.");
                } else {
                    // The remaining arguments must be filenames, so we add those
                    // to our list of files to compile.
                    Collections.addAll(myArgumentFileList, remainingArgs);
                }

                // Store the symbol table and type graph
                MathSymbolTableBuilder symbolTable = new MathSymbolTableBuilder(compileEnvironment);
                compileEnvironment.setSymbolTable(symbolTable);
                compileEnvironment.setTypeGraph(symbolTable.getTypeGraph());
            }
        } catch (FlagDependencyException fde) {
            // YS - Check to see if we have a status handler.
            if (compileEnvironment != null && compileEnvironment.getStatusHandler() != null) {
                statusHandler = compileEnvironment.getStatusHandler();
                Fault fault = new Fault(FaultType.FLAG_DEPENDENCY_EXCEPTION, null, fde.getMessage(), false);
                statusHandler.registerAndStreamFault(fault);
                if (compileEnvironment.flags.isFlagSet(FLAG_DEBUG_STACK_TRACE)) {
                    statusHandler.printStackTrace(fde);
                }
                statusHandler.stopLogging();
            } else {
                System.err.println(fde.getMessage());
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        return compileEnvironment;
    }

    /**
     * <p>
     * This prints the help message that prints out all the optional flags.
     * </p>
     *
     * @param compileEnvironment
     *            The current job's compilation environment that stores all necessary objects and flags.
     */
    private void printHelpMessage(CompileEnvironment compileEnvironment) {
        if (compileEnvironment.flags.isFlagSet(FLAG_DEBUG)) {
            StatusHandler debugHandler = compileEnvironment.getStatusHandler();
            debugHandler.info(null, "Usage: java -jar RESOLVE.jar [options] <files>");
            debugHandler.info(null, "where options include:");
            debugHandler.info(null,
                    FlagDependencies.getListingString(compileEnvironment.flags.isFlagSet(FLAG_EXTENDED_HELP)));
        }
    }

    /**
     * <p>
     * This method sets up dependencies between compiler flags. If you are integrating your module into the compiler
     * flag management system, this is where to do it.
     * </p>
     */
    private synchronized void setUpFlagDependencies() {
        if (!FlagDependencies.isSealed()) {
            setUpFlags();
            /*
             * Prover.setUpFlags(); Archiver.setUpFlags(); AlgebraicProver.setUpFlags();
             * CongruenceClassProver.setUpFlags();
             */
            CTranslator.setUpFlags();
            JavaTranslator.setUpFlags();
            Populator.setUpFlags();
            VCGenerator.setUpFlags();
            CongruenceClassProver.setUpFlags();
            GeneralPurposeProver.setUpFlags();
            FlagDependencies.seal();
        }
    }

    /**
     * <p>
     * Add all the required and implied flags. Including those needed by the WebIDE.
     * </p>
     */
    private void setUpFlags() {
        // Extended help implies that the general help is also on.
        FlagDependencies.addImplies(FLAG_EXTENDED_HELP, FLAG_HELP);

        // Debug out to file implies that the debug flag is also on.
        FlagDependencies.addImplies(FLAG_DEBUG_FILE_OUT, FLAG_DEBUG);

        // Stack traces implies debug flag is on
        FlagDependencies.addImplies(FLAG_DEBUG_STACK_TRACE, FLAG_DEBUG);

        // Print modules implies debug flag is on
        FlagDependencies.addImplies(FLAG_PRINT_MODULE, FLAG_DEBUG);
    }
}
