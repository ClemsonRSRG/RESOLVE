/*
 * FileOutputListener.java
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
package edu.clemson.rsrg.init.output;

import edu.clemson.rsrg.astoutput.GenerateGraphvizModel;
import edu.clemson.rsrg.init.file.ResolveFile;
import edu.clemson.rsrg.nProver.GeneralPurposeProver;
import edu.clemson.rsrg.nProver.output.VCProverResult;
import edu.clemson.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.rsrg.prover.output.Metrics;
import edu.clemson.rsrg.prover.output.PerVCProverModel;
import edu.clemson.rsrg.statushandling.StatusHandler;
import edu.clemson.rsrg.statushandling.Fault;
import edu.clemson.rsrg.statushandling.FaultType;
import edu.clemson.rsrg.translation.targets.CTranslator;
import edu.clemson.rsrg.translation.targets.JavaTranslator;
import edu.clemson.rsrg.vcgeneration.VCGenerator;
import edu.clemson.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationCondition;
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
import org.stringtemplate.v4.StringRenderer;

/**
 * <p>
 * A listener that contains methods for retrieving compilation results from the compiler and outputs them to different
 * files.
 * </p>
 *
 * @author Yu-Shan Sun
 *
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
     * @param handler
     *            The status handler for the RESOLVE compiler.
     */
    public FileOutputListener(StatusHandler handler) {
        myStatusHandler = handler;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method outputs the provided {@code Graphviz} model generated from the {@link GenerateGraphvizModel}.
     * </p>
     *
     * @param outputFileName
     *            A name for the output file.
     * @param graphvizModel
     *            The inner {@code AST} represented in a {@code GraphViz} file format.
     */
    @Override
    public final void astGraphvizModelResult(String outputFileName, String graphvizModel) {
        writeToFile(outputFileName + "_ModuleDec.gv", graphvizModel);
    }

    /**
     * <p>
     * This method outputs the provided the {@code C} translation results from the {@link CTranslator}.
     * </p>
     *
     * @param inputFileName
     *            Name of the {@link ResolveFile} we are generating {@code C} translations.
     * @param outputFileName
     *            A name for the output file.
     * @param cTranslation
     *            The translated {@code C} source code.
     */
    @Override
    public final void cTranslationResult(String inputFileName, String outputFileName, String cTranslation) {
        throw new UnsupportedOperationException("Needs to be implemented!");
    }

    /**
     * <p>
     * This method outputs the provided the {@code Java} translation results from the {@link JavaTranslator}.
     * </p>
     *
     * @param inputFileName
     *            Name of the {@link ResolveFile} we are generating {@code Java} translations.
     * @param outputFileName
     *            A name for the output file.
     * @param javaTranslation
     *            The translated {@code Java} source code.
     */
    @Override
    public final void javaTranslationResult(String inputFileName, String outputFileName, String javaTranslation) {
        writeToFile(outputFileName + ".java", javaTranslation);
    }

    /**
     * <p>
     * This method outputs the provided {@link VerificationCondition VerificationConditions} and/or raw output result
     * from the {@link GeneralPurposeProver}.
     * </p>
     *
     * @param inputFileName
     *            Name of the {@link ResolveFile} we are proving VCs for.
     * @param outputFileName
     *            A name for the output file.
     * @param timeOut
     *            The prover setting for the amount of time that can be spent on each VC.
     * @param numTries
     *            The prover setting for how many unproved VCs we can have before halting.
     * @param results
     *            A list containing the prover results for each VC.
     * @param totalTime
     *            Total time spent on proving this file.
     * @param verboseOutput
     *            The verbose output string generated by the {@link GeneralPurposeProver}.
     */
    @Override
    public final void nProverResult(String inputFileName, String outputFileName, long timeOut, int numTries,
            List<VCProverResult> results, long totalTime, String verboseOutput) {
        StringBuilder sb = new StringBuilder();

        // String template to hold the VC generation details
        STGroup group = new STGroupFile("templates/proverOutput.stg");
        group.registerRenderer(String.class, new StringRenderer());
        ST model = group.getInstanceOf("outputProofFile").add("fileName", inputFileName)
                .add("dateGenerated", new Date()).add("proverName", "General Purpose Prover").add("timeOut", timeOut)
                .add("numTries", numTries).add("totalTime", totalTime);

        // Store the results for each VC
        int numProved = 0;
        int numUnproved = 0;
        for (VCProverResult result : results) {
            // Create a model for adding all the details
            // associated with this VC.
            ST vcProofModel = group.getInstanceOf("outputVCProofResult");
            vcProofModel.add("vcNum", result.getVerificationCondition().getName());
            vcProofModel.add("isProved", result.isProved());
            vcProofModel.add("wasSkipped", result.getWasSkippedFlag());
            vcProofModel.add("timedOut", result.getTimedOutFlag());
            vcProofModel.add("time", result.getProofTime());

            // Increment count
            if (result.isProved()) {
                numProved++;
            } else {
                numUnproved++;
            }

            // Add the VC to the model
            model.add("vcs", vcProofModel.render());
        }

        // Store the number of proved and unproved VCs
        model.add("numProved", numProved);
        model.add("numUnproved", numUnproved);

        // Append the VC proof details from the model
        sb.append(model.render());

        // Append VC details with any verbose output
        sb.append(verboseOutput);

        // Output the results to file
        writeToFile(outputFileName + ".gp.proof", sb.toString());
    }

    /**
     * <p>
     * This method outputs the provided results from the {@code CCProver}.
     * </p>
     *
     * @param inputFileName
     *            Name of the {@link ResolveFile} we are generating proofs.
     * @param outputFileName
     *            A name for the output file.
     */
    @Override
    public final void proverResult(String inputFileName, String outputFileName) {
        throw new UnsupportedOperationException("Needs to be implemented!");
    }

    /**
     * <p>
     * This method outputs the provided {@link AssertiveCodeBlock AssertiveCodeBlocks} and/or raw output result from the
     * {@link VCGenerator}.
     * </p>
     *
     * @param inputFileName
     *            Name of the {@link ResolveFile} we are generating VCs for.
     * @param outputFileName
     *            A name for the output file.
     * @param blocks
     *            A list of final {@link AssertiveCodeBlock AssertiveCodeBlocks}.
     * @param verboseOutput
     *            The verbose output string generated by the {@link VCGenerator}.
     */
    @Override
    public final void vcGeneratorResult(String inputFileName, String outputFileName, List<AssertiveCodeBlock> blocks,
            String verboseOutput) {
        StringBuilder sb = new StringBuilder();

        // String template to hold the VC generation details
        STGroup group = new STGroupFile("templates/VCGenOutput.stg");
        ST model = group.getInstanceOf("outputVCGenFile").add("fileName", inputFileName).add("dateGenerated",
                new Date());

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
                    vcModel.add("locationDetail", detailModel.getDetailMessage());
                } else {
                    Fault vcGenMissingInfo = new Fault(FaultType.MISSING_INFO_VC_GEN, vc.getLocation(),
                            "[FileOutputListener] VC " + vc.getName() + " is missing information about "
                                    + "how this VC got generated.",
                            false);
                    myStatusHandler.registerAndStreamFault(vcGenMissingInfo);
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
     * @param proved
     *            {@code true} if the {@code VC} was proved, {@code false} otherwise.
     * @param finalModel
     *            The prover representation for a {@code VC}.
     * @param m
     *            The prover generated metrics.
     */
    @Override
    public final void vcResult(boolean proved, PerVCProverModel finalModel, Metrics m) {
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
     * @param outputFileName
     *            Output filename.
     * @param outputString
     *            Contents to be written in file.
     */
    private void writeToFile(String outputFileName, String outputString) {
        try {
            Path outputFilePath = Paths.get(outputFileName);
            Charset charset = Charset.forName("UTF-8");

            // Write the contents to file
            Writer writer = Files.newBufferedWriter(outputFilePath, charset);
            writer.write(outputString);
            writer.close();
        } catch (IOException ioe) {
            throw new RuntimeException("[FileOutputListener] Error while writing to file: " + outputFileName);
        }
    }
}
