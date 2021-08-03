/*
 * FileOutputListener.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.init.output;

import edu.clemson.cs.rsrg.astoutput.GenerateGraphvizModel;
import edu.clemson.cs.rsrg.init.file.ResolveFile;
import edu.clemson.cs.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.cs.rsrg.prover.output.Metrics;
import edu.clemson.cs.rsrg.prover.output.PerVCProverModel;
import edu.clemson.cs.rsrg.statushandling.StatusHandler;
import edu.clemson.cs.rsrg.translation.targets.CTranslator;
import edu.clemson.cs.rsrg.translation.targets.JavaTranslator;
import edu.clemson.cs.rsrg.vcgeneration.VCGenerator;
import edu.clemson.cs.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationCondition;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 * <p>
 * A listener that contains methods for retrieving compilation results from the
 * compiler and outputs
 * them to different files.
 * </p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class FileOutputListener implements OutputListener {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * This is the status handler for the RESOLVE compiler.
     * </p>
     */
    private final StatusHandler myStatusHandler;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a output listener to different {@link File Files}.
     * </p>
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
     * <p>
     * This method outputs the provided {@code Graphviz} model generated from
     * the
     * {@link GenerateGraphvizModel}.
     * </p>
     *
     * @param outputFileName A name for the output file.
     * @param graphvizModel The inner {@code AST} represented in a
     *        {@code GraphViz} file format.
     */
    @Override
    public final void astGraphvizModelResult(String outputFileName,
            String graphvizModel) {
        writeToFile(outputFileName + "_ModuleDec.gv", graphvizModel);
    }

    /**
     * <p>
     * This method outputs the provided the {@code C} translation results from
     * the
     * {@link CTranslator}.
     * </p>
     *
     * @param inputFileName Name of the {@link ResolveFile} we are generating
     *        {@code C} translations.
     * @param outputFileName A name for the output file.
     * @param cTranslation The translated {@code C} source code.
     */
    @Override
    public final void cTranslationResult(String inputFileName,
            String outputFileName, String cTranslation) {
        throw new UnsupportedOperationException("Needs to be implemented!");
    }

    /**
     * <p>
     * This method outputs the provided the {@code Java} translation results
     * from the
     * {@link JavaTranslator}.
     * </p>
     *
     * @param inputFileName Name of the {@link ResolveFile} we are generating
     *        {@code Java}
     *        translations.
     * @param outputFileName A name for the output file.
     * @param javaTranslation The translated {@code Java} source code.
     */
    @Override
    public final void javaTranslationResult(String inputFileName,
            String outputFileName, String javaTranslation) {
        writeToFile(outputFileName + ".java", javaTranslation);
    }

    /**
     * <p>
     * This method outputs the provided results from the {@code CCProver}.
     * </p>
     *
     * @param inputFileName Name of the {@link ResolveFile} we are generating
     *        proofs.
     * @param outputFileName A name for the output file.
     */
    @Override
    public final void proverResult(String inputFileName,
            String outputFileName) {
        throw new UnsupportedOperationException("Needs to be implemented!");
    }

    /**
     * <p>
     * This method outputs the provided {@link AssertiveCodeBlock
     * AssertiveCodeBlocks} and/or raw
     * output result from the {@link VCGenerator}.
     * </p>
     *
     * @param inputFileName Name of the {@link ResolveFile} we are generating
     *        VCs for.
     * @param outputFileName A name for the output file.
     * @param blocks A list of final {@link AssertiveCodeBlock
     *        AssertiveCodeBlocks}.
     * @param verboseOutput The verbose output string generated by the
     *        {@link VCGenerator}.
     */
    @Override
    public final void vcGeneratorResult(String inputFileName,
            String outputFileName, List<AssertiveCodeBlock> blocks,
            String verboseOutput) {
        StringBuilder sb = new StringBuilder();

        // String template to hold the VC generation details
        STGroup group = new STGroupFile("templates/VCGenOutput.stg");
        ST model = group.getInstanceOf("outputVCGenFile")
                .add("fileName", inputFileName)
                .add("dateGenerated", new Date());

        // Add the VC output in human readable format
        for (AssertiveCodeBlock block : blocks) {
            // Obtain the final list of vcs
            List<VerificationCondition> vcs = block.getVCs();
            for (VerificationCondition vc : vcs) {
                // Create a model for adding all the details
                // associated with this VC.
                LocationDetailModel detailModel = vc.getLocationDetailModel();
                ST vcModel = group.getInstanceOf("outputVC");
                vcModel.add("vcNum", vc.getName());

                // Add additional detail if this VC has impacting reduction
                if (vc.getHasImpactingReductionFlag()) {
                    vcModel.add("hasImpactingReduction", true);
                }

                // Warn the user if are missing the LocationDetailModel
                if (detailModel != null) {
                    vcModel.add("location", detailModel.getDestinationLoc());
                    vcModel.add("locationDetail",
                            detailModel.getDetailMessage());
                }
                else {
                    myStatusHandler.warning(vc.getLocation(),
                            "[FileOutputListener] VC " + vc.getName()
                                    + " is missing information about how this VC got generated.");
                }

                // Output the associated sequent
                Sequent sequent = vc.getSequent();
                ST sequentModel = group.getInstanceOf("outputSequent");
                sequentModel.add("consequents", sequent.getConcequents());
                sequentModel.add("antecedents", sequent.getAntecedents());

                // Add this sequent to our vc model
                vcModel.add("sequent", sequentModel.render());

                // Add the VC to the model
                model.add("vcs", vcModel.render());
            }
        }

        // Append the generated VC details from the model
        sb.append(model.render());

        // Append VC details with any verbose output
        sb.append(verboseOutput);

        // Output the results to file
        writeToFile(outputFileName + ".asrt", sb.toString());
    }

    /**
     * <p>
     * This method outputs the prover results for a given {@code VC}.
     * </p>
     *
     * @param proved {@code true} if the {@code VC} was proved, {@code false}
     *        otherwise.
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
     * <p>
     * Writes the content to the specified filename.
     * </p>
     *
     * @param outputFileName Output filename.
     * @param outputString Contents to be written in file.
     */
    private void writeToFile(String outputFileName, String outputString) {
        try {
            Path outputFilePath = Paths.get(outputFileName);
            Charset charset = Charset.forName("UTF-8");

            // Write the contents to file
            Writer writer = Files.newBufferedWriter(outputFilePath, charset);
            writer.write(outputString);
            writer.close();
        }
        catch (IOException ioe) {
            myStatusHandler.error(null,
                    "[FileOutputListener] Error while writing to file: "
                            + outputFileName);
        }
    }
}
