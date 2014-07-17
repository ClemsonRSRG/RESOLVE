/**
 * OutputVCs.java
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
package edu.clemson.cs.r2jt.vcgeneration;

/*
 * Libraries
 */
import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.proving.Conjuncts;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.Antecedent;
import edu.clemson.cs.r2jt.proving2.Consequent;
import edu.clemson.cs.r2jt.proving2.VC;
import edu.clemson.cs.r2jt.vcgeneration.vcs.VCCollector;
import edu.clemson.cs.r2jt.vcgeneration.vcs.VerificationCondition;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO: Write a description of this module
 */
public class OutputVCs {

    // ===========================================================
    // Global Variables
    // ===========================================================

    /**
     * <p>A list that will be built up with <code>AssertiveCode</code>
     * objects, each representing a VC or group of VCs that must be
     * satisfied to verify a parsed program.</p>
     */
    private Collection<AssertiveCode> myFinalAssertiveCode;

    /**
     * <p>A list of final immutable VCs for this module.</p>
     */
    private List<VC> myFinalImmutableVCs;

    /**
     * <p>This string buffer holds all the steps
     * the VC generator takes to generate VCs.</p>
     */
    private StringBuffer myVCSteps;

    // ===========================================================
    // Constructors
    // ===========================================================

    public OutputVCs(Collection<AssertiveCode> assertiveCode, StringBuffer steps) {
        myFinalAssertiveCode = assertiveCode;
        myVCSteps = steps;

        // Convert to each format
        proverVCs();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Returns the list of Immutable VCs for the in house provers.</p>
     *
     * @return List of Immutable VCs.
     */
    public List<VC> getProverOutput() {
        return myFinalImmutableVCs;
    }

    /**
     * <p>Outputs the results from the VC Generator to a file.</p>
     *
     * @param filename Name of the output file.
     */
    public void outputToFile(String filename) {
        try {
            FileWriter outFile = new FileWriter(filename);
            outFile.write("");
            outFile.append(myVCSteps);
            outFile.append(humanReadableVCs());
            outFile.flush();
        }
        catch (IOException ex) {
            System.err.println("File I/O error when writing: " + filename);
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Method to convert each VC to its immutable format.</p>
     *
     * @param vc The original VC.
     *
     * @return The immutable form of the VC.
     */
    private VC convertToImmutableVC(VerificationCondition vc) {

        java.util.List<PExp> newAntecedents = new LinkedList<PExp>();

        Conjuncts oldAntecedents = vc.getAntecedents();
        for (Exp a : oldAntecedents) {
            newAntecedents.add(PExp.buildPExp(a));
        }

        java.util.List<PExp> newConsequents = new LinkedList<PExp>();

        Conjuncts oldConsequents = vc.getConsequents();
        for (Exp c : oldConsequents) {
            newConsequents.add(PExp.buildPExp(c));
        }

        VC retval =
                new VC(vc.getName(), new Antecedent(newAntecedents),
                        new Consequent(newConsequents));

        return retval;
    }

    /**
     * <p>This method converts all <code>AssertiveCode</code> into
     * the format used by the output handler and our in house provers.</p>
     */
    private String humanReadableVCs() {
        String finalVCs = "";

        // TODO: Convert to human readable format

        return finalVCs;
    }

    /**
     * <p>This method converts all <code>AssertiveCode</code> into
     * the format used by the output handler and our in house provers.</p>
     */
    private void proverVCs() {
        // Convert to an iterable list of <code>VerificationCondition</code>
        Iterable<VerificationCondition> vcsToProve =
                new VCCollector(myFinalAssertiveCode);

        // Make the VCs immutable
        myFinalImmutableVCs = new LinkedList<VC>();
        for (VerificationCondition originalVC : vcsToProve) {
            myFinalImmutableVCs.add(convertToImmutableVC(originalVC));
        }
    }
}
