/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.vcgeneration;

/*
 * Libraries
 */
import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.proving.Conjuncts;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.Antecedent;
import edu.clemson.cs.r2jt.proving2.Consequent;
import edu.clemson.cs.r2jt.proving2.VC;
import edu.clemson.cs.r2jt.vcgeneration.vcs.VCCollector;
import edu.clemson.cs.r2jt.vcgeneration.vcs.VerificationCondition;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * TODO: Write a description of this module
 */
public class OutputVCs {

    // ===========================================================
    // Global Variables
    // ===========================================================

    /**
     * <p>The current compile environment used throughout
     * the compiler.</p>
     */
    private CompileEnvironment myInstanceEnvironment;

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
     * <p>A map that stores vc names. </p>
     */
    private Map<String, List<Location>> myVCDetails;

    /**
     * <p>This string buffer holds all the steps
     * the VC generator takes to generate VCs.</p>
     */
    private StringBuffer myVCSteps;

    // ===========================================================
    // Constructors
    // ===========================================================

    public OutputVCs(final CompileEnvironment env,
            Collection<AssertiveCode> assertiveCode, StringBuffer steps) {
        myInstanceEnvironment = env;
        myFinalAssertiveCode = assertiveCode;
        myVCDetails = new HashMap<String, List<Location>>();
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
            FileWriter w = new FileWriter(new File(filename));
            w.write(buildHeaderComment());
            w.write("\n=================================");
            w.write(" VC(s): ");
            w.write("=================================\n");
            w.write("\n");
            w.write(humanReadableVCs());
            w.write(myVCSteps.toString());
            w.flush();
            w.close();
        }
        catch (IOException ex) {
            System.err.println("File I/O error when writing: " + filename);
        }
    }

    public void outputToJSON() {
        JSONObject jsonObject = jsonVCs();
        CompileReport report = myInstanceEnvironment.getCompileReport();
        StringBuffer outBuffer = new StringBuffer();
        outBuffer.append("<vcFile>");
        outBuffer.append(jsonObject.toString());
        outBuffer.append("</vcFile>");
        report.setVcSuccess();
        report.setOutput(outBuffer.toString());
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Builds a comment header to identify VC files generated
     * by the compiler and from which RESOLVE source file the generated
     * file is derived.</p>
     */
    private String buildHeaderComment() {
        return "VCs for " + myInstanceEnvironment.getTargetFile().getName()
                + " generated " + new Date() + "\n";
    }

    /**
     * <p>Method to convert each VC to its immutable format.</p>
     *
     * @param vc The original VC.
     *
     * @return The immutable form of the VC.
     */
    private VC convertToImmutableVC(VerificationCondition vc) {
        List<PExp> newAntecedents = new LinkedList<PExp>();
        List<PExp> newConsequents = new LinkedList<PExp>();
        List<Location> locationList = new LinkedList<Location>();

        // Antecedents (Givens)
        Conjuncts oldAntecedents = vc.getAntecedents();
        for (Exp a : oldAntecedents) {
            newAntecedents.add(PExp.buildPExp(a));
        }
        Collections.reverse(newAntecedents);

        // Consequents (Goals)
        Conjuncts oldConsequents = vc.getConsequents();
        for (Exp c : oldConsequents) {
            newConsequents.add(PExp.buildPExp(c));
            locationList.add(c.getLocation());
        }

        // Immutable VC
        VC retval =
                new VC(vc.getName(), new Antecedent(newAntecedents),
                        new Consequent(newConsequents));

        // Stores the location details in our Map
        myVCDetails.put(retval.getName(), locationList);

        return retval;
    }

    /**
     * <p>This method takes all the VCs (in Immutable form) and outputs a human
     * readable String.</p>
     *
     * @return Human readable vcs as a String.
     */
    private String humanReadableVCs() {
        String finalVCs = "";

        // Convert to human readable format
        for (VC vc : myFinalImmutableVCs) {
            List<Location> locationList = myVCDetails.get(vc.getName());
            Antecedent antecedent = vc.getAntecedent();
            Consequent consequent = vc.getConsequent();

            // Add vc details to string
            finalVCs += ("VC " + vc.getName() + "\n\n");

            // Location details
            Location loc = locationList.get(0);
            finalVCs += (loc.getDetails() + ": " + loc.toString() + "\n\n");

            // Goals
            finalVCs +=
                    ("Goal(s):\n\n"
                            + reformatOutputString(consequent.toString()) + "\n");

            // Givens
            finalVCs += "Given(s):\n\n";
            int numAntecedent = antecedent.size();
            for (int i = 0; i < numAntecedent; i++) {
                finalVCs +=
                        ((i + 1)
                                + ". "
                                + reformatOutputString(antecedent.get(i)
                                        .toString()) + "\n");
            }
            finalVCs += "\n";
        }

        return finalVCs;
    }

    /**
     * <p>This method takes all the VCs (in Immutable form) and outputs in
     * jSON format for the WebIDE.</p>
     *
     * @return The created JSON VC Object.
     */
    private JSONObject jsonVCs() {
        JSONObject jsonOutput = new JSONObject();
        JSONArray vcArray = new JSONArray();

        for (VC vc : myFinalImmutableVCs) {
            JSONObject newVC = new JSONObject();
            List<Location> locationList = myVCDetails.get(vc.getName());
            Antecedent antecedent = vc.getAntecedent();
            Consequent consequent = vc.getConsequent();

            // Location details
            Location loc = locationList.get(0);
            newVC.put("lineNum", "" + loc.getPos().getLine());
            newVC.put("sourceFile", loc.getFilename());

            // VC Number
            newVC.put("vc", vc.getName());

            // Givens
            int numAntecedent = antecedent.size();
            String givens = "";
            for (int i = 0; i < numAntecedent; i++) {
                givens +=
                        ((i + 1)
                                + ": "
                                + reformatOutputString(antecedent.get(i)
                                        .toString()) + "\n");
            }
            newVC.put("vcGivens", ResolveCompiler.webEncode(givens));

            // Goal(s)
            newVC.put("vcGoal", ResolveCompiler
                    .webEncode(reformatOutputString(consequent.toString())));

            // VC Details
            newVC.put("vcInfo", ResolveCompiler.webEncode(loc.getDetails()
                    + ": " + loc.toString()));

            // Store this VC inside the array
            vcArray.put(newVC);
        }
        jsonOutput.put("vcs", vcArray);

        return jsonOutput;
    }

    /**
     * <p>This method converts all the question mark variables
     * into human readable prime variables and converts all the
     * "Conc_" variables to "Conc.".</p>
     *
     * @param str String form of <code>Antecedent</code> or
     *            <code>Consequent</code>.
     *
     * @return Properly converted text in string format.
     */
    private String reformatOutputString(String str) {
        // Return Value
        StringBuffer stringBuffer = new StringBuffer();

        // Replace all instances of Conc_ with Conc.
        String temp = str.replaceAll("Conc_", "Conc.");

        // Convert the string to a character array
        char[] tempArray = temp.toCharArray();

        // Loop through looking for question marks
        int i = 0;
        while (i < tempArray.length) {
            if (tempArray[i] != '?') {
                stringBuffer.append(tempArray[i]);
                i++;
            }
            else {
                int questionCount = 1;
                i++;
                while (i < tempArray.length && tempArray[i] == '?') {
                    questionCount++;
                    i++;
                }

                boolean stop = false;
                while (i < tempArray.length && !stop) {
                    if ((tempArray[i] > 47 && tempArray[i] < 58)
                            || (tempArray[i] > 64 && tempArray[i] < 91)
                            || (tempArray[i] > 96 && tempArray[i] < 123)
                            || (tempArray[i] == 95)) {
                        stringBuffer.append(tempArray[i]);
                        i++;
                    }
                    else {
                        stop = true;
                    }
                }

                for (int j = 0; j < questionCount; j++) {
                    stringBuffer.append('\'');
                }
            }
        }

        return stringBuffer.toString();
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
