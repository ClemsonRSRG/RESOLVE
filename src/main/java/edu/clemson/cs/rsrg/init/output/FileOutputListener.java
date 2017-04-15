/*
 * FileOutputListener.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.init.output;

import edu.clemson.cs.r2jt.rewriteprover.Metrics;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.astoutput.GenerateGraphvizModel;
import edu.clemson.cs.rsrg.statushandling.StatusHandler;
import edu.clemson.cs.rsrg.vcgeneration.VCGenerator;
import edu.clemson.cs.rsrg.vcgeneration.vcs.AssertiveCodeBlock;
import java.io.*;
import java.util.List;

/**
 * <p>A listener that contains methods for retrieving compilation
 * results from the compiler and outputs them to different files.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class FileOutputListener implements OutputListener {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>This is the status handler for the RESOLVE compiler.</p> */
    private final StatusHandler myStatusHandler;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a output listener to different
     * {@link File Files}.</p>
     *
     * @param handler The status handler for the RESOLVE compiler.
     */
    public FileOutputListener(StatusHandler handler) {
        myStatusHandler = handler;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method outputs the provided {@code Graphviz} model generated
     * from the {@link GenerateGraphvizModel}.</p>
     *
     * @param dec The module declaration we are currently processing.
     * @param graphvizModel The inner {@code AST} represented in a {@code GraphViz}
     *                      file format.
     */
    @Override
    public final void astGraphvizModelResult(ModuleDec dec, String graphvizModel) {

    }

    /**
     * <p>This method outputs the provided the java translation results
     * from the {@code JavaTranslator}.</p>
     *
     * @param dec The module declaration we are currently processing.
     * @param javaTranslation The translated {@code Java} source code.
     */
    @Override
    public final void javaTranslationResult(ModuleDec dec, String javaTranslation) {
        throw new UnsupportedOperationException("Needs to be implemented!");
    }

    /**
     * <p>This method outputs the provided results
     * from the {@code CCProver}.</p>
     *
     * @param dec The module declaration we are currently processing.
     */
    @Override
    public final void proverResult(ModuleDec dec) {
        throw new UnsupportedOperationException("Needs to be implemented!");
    }

    /**
     * <p>This method outputs the provided {@link AssertiveCodeBlock AssertiveCodeBlocks}
     * from the {@link VCGenerator}.</p>
     *
     * @param dec The module declaration we are currently processing.
     * @param blocks A list of final {@link AssertiveCodeBlock AssertiveCodeBlocks}.
     */
    @Override
    public void vcGeneratorResult(ModuleDec dec, List<AssertiveCodeBlock> blocks) {
        throw new UnsupportedOperationException("Needs to be implemented!");
    }

    /**
     * <p>This method outputs the prover results for a given {@code VC}.</p>
     *
     * @param proved {@code true} if the {@code VC} was proved,
     *               {@code false} otherwise.
     * @param finalModel The prover representation for a {@code VC}.
     * @param m The prover generated metrics.
     */
    @Override
    public final void vcResult(boolean proved, PerVCProverModel finalModel,
            Metrics m) {
        throw new UnsupportedOperationException("Needs to be implemented!");
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Writes the content to the specified filename.</p>
     *
     * @param outputFileName Output filename.
     * @param outputString Contents to be written in file.
     */
    private void writeToFile(String outputFileName, String outputString) {
        try {
            // Write the contents to file
            Writer writer =
                    new BufferedWriter(new FileWriter(new File(outputFileName),
                            false));
            writer.write(outputString);
            writer.close();
        }
        catch (IOException ioe) {
            myStatusHandler.error(null, "Error while writing to file: "
                    + outputFileName);
        }
    }
}