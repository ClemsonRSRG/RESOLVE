/**
 * SMTProver.java
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
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.absyn.IntegerExp;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.misc.Flag;
import edu.clemson.cs.r2jt.misc.FlagDependencies;
import edu.clemson.cs.r2jt.rewriteprover.Prover;
import edu.clemson.cs.r2jt.rewriteprover.ProverListener;
import edu.clemson.cs.r2jt.rewriteprover.VC;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.typeandpopulate.*;
import edu.clemson.cs.r2jt.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.TheoremEntry;
import edu.clemson.cs.r2jt.typeandpopulate.query.EntryTypeQuery;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.vcgeneration.VCGenerator;
import edu.clemson.cs.r2jt.vcgeneration.vcs.VerificationCondition;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.jar.Attributes;

/**
 * Created by nabilkabbani on 3/12/15.
 * example where this isn't working: http://rise4fun.com/Z3/sl8nv
 */
public class SMTProver {

    public static final Flag FLAG_PROVE =
            new Flag(Prover.FLAG_SECTION_NAME, "smtprove",
                    "creates SMTLIB 2.0 compliant file");
    private final CompileEnvironment m_environment;
    private final ModuleScope m_scope;
    private String m_smtlibScript = "";
    private final String[] m_perVCsmtLibScripts;
    private final TypeGraph m_typeGraph;
    private final boolean useSolvers = false;
    private final long DEFAULTTIMEOUT = 10000;
    private int numVCs;
    private Set<String> m_theorem_decls;
    public final static String TypeSort = "MType";
    public final static String NameSort = "Syms";
    public final static String ReserveString = "@!";
    public final static Set<String> NamesNotToBeChanged = new HashSet<String>();
    // only for webide ////////////////////////////////////
    private final PerVCProverModel[] myModels;
    private ProverListener myProverListener;
    private final long myTimeout;
    private long totalTime = 0;

    ///////////////////////////////////////////////////////
    public static void setUpFlags() {
        FlagDependencies.addImplies(SMTProver.FLAG_PROVE,
                VCGenerator.FLAG_ALTVERIFY_VC);
    }

    public SMTProver(TypeGraph g, List<VC> vcs, ModuleScope scope,
            CompileEnvironment environment, ProverListener listener) {
        totalTime = System.currentTimeMillis();
        numVCs = vcs.size();
        m_theorem_decls = new HashSet<String>();
        NamesNotToBeChanged.add("=");
        NamesNotToBeChanged.add("=>");
        NamesNotToBeChanged.add("and");
        NamesNotToBeChanged.add("or");
        NamesNotToBeChanged.add("not");
        NamesNotToBeChanged.add("true");
        NamesNotToBeChanged.add("false");
        // Only for web ide //////////////////////////////////////////
        myModels = new PerVCProverModel[vcs.size()];
        if (listener != null) {
            myProverListener = listener;
        }
        if (environment.flags.isFlagSet(Prover.FLAG_TIMEOUT)) {
            myTimeout =
                    Integer.parseInt(environment.flags.getFlagArgument(
                            Prover.FLAG_TIMEOUT, Prover.FLAG_TIMEOUT_ARG_NAME));
        }
        else {
            myTimeout = DEFAULTTIMEOUT;
        }
        ///////////////////////////////////////////////////////////////
        totalTime = System.currentTimeMillis();
        m_typeGraph = g;
        m_environment = environment;
        m_scope = scope;
        m_perVCsmtLibScripts = new String[vcs.size()];
        String vcDecls = "";
        String theoremsInSMTLib = getTheoremSMTStr(useSolvers);
        int i = 0;
        for (VC vc : vcs) {
            myModels[i] = (new PerVCProverModel(g, vc.getName(), vc, null));
            vcDecls += "(push)\n";
            String vcString = getVCSMTStr(vc);
            vcDecls += vcString;
            String vcWtheorems = theoremsInSMTLib + vcString;
            m_perVCsmtLibScripts[i] = vcWtheorems;
            if (i < vcs.size()) {
                vcDecls += "(pop)\n";
            }

            i += 1;
        }

    }

    String getVCSMTStr(VC vc) {
        HashMap<String, MTType> typeMap = new HashMap<String, MTType>();
        String declarations = ";VC: " + vc.getName() + "\n";
        String assertions = vc.getAntecedent().toSMTLIB(typeMap);
        assertions += "\n;goal\n" + vc.getConsequent().negateToSMT(typeMap);
        HashSet<String> namedSort = new HashSet<String>();
        for (Map.Entry<String, MTType> kv : typeMap.entrySet()) {
            MTType type = kv.getValue();
            String typeString = replaceReservedChars(type.toString());
            String s = kv.getKey();
            if (m_theorem_decls.contains(s))
                continue;
            if (type.getClass().getSimpleName().equals("MTNamed")
                    && !namedSort.contains(typeString)) {
                declarations +=
                        "(declare-const " + typeString + " " + TypeSort + ") "
                                + "\n";
                namedSort.add(typeString);

            }
            if (type.getClass().getSimpleName().equals("MTNamed")) {
                declarations +=
                        "(declare-const " + s + " " + NameSort + ") " + "\n";
                declarations +=
                        "(assert (EleOf " + s + " " + typeString + "))\n";

            }
            else if (type.getClass().getSimpleName().equals(
                    "MTFunctionApplication")) {
                MTFunctionApplication mtf = (MTFunctionApplication) type;
                String args = "";
                for (MTType m : mtf.getArguments()) {
                    String argTypeString = replaceReservedChars(m.toString());
                    if (m.getClass().getSimpleName().equals("MTNamed")
                            && !namedSort.contains(argTypeString)) {
                        declarations +=
                                "(declare-const " + argTypeString + " "
                                        + TypeSort + ") " + "\n";
                        namedSort.add(argTypeString);
                    }

                    args += argTypeString + " ";
                }
                declarations += "(declare-const " + s + " " + NameSort + " )\n";
                declarations +=
                        "(assert (EleOf " + s + "(" + ReserveString
                                + mtf.getName() + " " + args + ")))\n";
            }
            else {
                declarations += "(declare-const " + s + " " + NameSort + " )\n";
                declarations += "(assert (EleOf " + s + " " + type + "))\n";
            }

        }
        for (String s : namedSort) {
            s = s.replace("@", "");
            declarations +=
                    "(declare-fun " + ReserveString + s + ".Is!Initial ("
                            + NameSort + "  ) B)\n";

        }
        String script = declarations + assertions;
        script += "\n" + ("(echo \"" + vc.getName() + "\")(check-sat)\n");
        return script;

    }

    public static String replaceReservedChars(String name) {
        name = name.replace("_", "!");
        name = name.replace("|", "l");
        name = name.replace("'", "@");
        return name;
    }

    String getTheoremSMTStr(boolean useSolvers) {
        String rString = "";//"(set-option :smt.mbqi false)\n";
        Set<String> moduleIdExclusion = new HashSet<String>();
        //moduleIdExclusion.add("GLOBAL");
        //moduleIdExclusion.add("Natural_Number_Theory");
        moduleIdExclusion.add("Boolean_Theory");
        moduleIdExclusion.add("Set_Theory");
        if (useSolvers) {
            moduleIdExclusion.add("Integer_Theory");
        }

        List<MathSymbolEntry> mathSymbolEntries =
                m_scope.query(new EntryTypeQuery<MathSymbolEntry>(
                        MathSymbolEntry.class,
                        MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                        MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));
        String declString =
                "(declare-sort " + TypeSort + ")\n(declare-sort " + NameSort
                        + ")\n(define-sort B() Bool)\n";
        declString +=
                "(declare-fun EleOf(" + NameSort + " " + TypeSort + ") Bool)\n";

        //declString += "(declare-const Z " + TypeSort + " )\n";
        declString += "(declare-const N " + TypeSort + " )\n";
        //declString += "(declare-fun minus ( S ) S)\n";
        HashSet<String> declaredFuns = new HashSet<String>(); // for overloading
        for (MathSymbolEntry m : mathSymbolEntries) {
            String source = m.getSourceModuleIdentifier().toString();
            if (!(moduleIdExclusion.contains(source))) {
                MTType type = m.getType();
                String typeString = type.toString();
                String typeClass = type.getClass().getSimpleName();
                String name = replaceReservedChars(m.getName());

                if (typeString.equals("MType")) {
                    declString +=
                            "(declare-const " + name + " " + TypeSort + ") "
                                    + "\n";
                    m_theorem_decls.add(name);
                }
                // add spec chars if not a type
                name = ReserveString + name;
                m_theorem_decls.add(name);
                if (typeClass.equals("MTProper") && !typeString.equals("MType")) {
                    /*
                    (declare-const PVal S)
                    (assert (EleOf PVal N))
                     */
                    declString +=
                            "(declare-const " + name + " " + NameSort + " )\n";
                    declString +=
                            "(assert (EleOf " + name + " " + typeString
                                    + "))\n";
                }
                else {
                    if (typeClass.equals("MTFunction")) {
                        MTFunction mtf = (MTFunction) type;
                        String paramString = mtf.getParamStringForSMT();
                        String rangeString = "";
                        if (mtf.getRange().toString().equals("B")) {
                            rangeString = "Bool";
                        }
                        else if (mtf.getRange().toString().equals("MType")) {
                            rangeString = "MType";
                        }
                        else
                            rangeString = NameSort;

                        String funcDecl =
                                "(declare-fun " + name + " (" + paramString
                                        + ") " + rangeString + ")\n";
                        if (!declaredFuns.contains(funcDecl)) {
                            declString += funcDecl;
                            declaredFuns.add(funcDecl);
                        }

                    }
                }

            }
        }
        rString += declString;
        //rString += "(assert (forall ((n N )) (LTE zero n) ))\n";
        List<TheoremEntry> theoremEntries =
                m_scope.query(new EntryTypeQuery(TheoremEntry.class,
                        MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                        MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));
        String smtTheorems = "";
        for (TheoremEntry e : theoremEntries) {
            String source = e.getSourceModuleIdentifier().toString();
            if (!(moduleIdExclusion.contains(source))) {
                String thSmt = e.toSMTLIB(null, false); //assertion.toSMTLIB();
                if (!thSmt.isEmpty())
                    smtTheorems +=
                            ";" + e.getAssertion().toString() + "\n" + thSmt
                                    + "\n";
            }

        }
        // Manually entering type theorems
        smtTheorems +=
                "(assert (forall ((s " + NameSort
                        + ")) ( => (EleOf s N) (EleOf s Z))))\n";
        smtTheorems +=
                "(assert (forall ((t " + TypeSort + ")(s " + NameSort
                        + " )) (=> (EleOf s (" + ReserveString
                        + "Str t)) (EleOf s SStr))))\n";
        rString += smtTheorems;
        return rString;
    }

    public void start() throws IOException {

        String pfName = outputProofFile();
        /*        int hardTimeout = (numVCs * (int)myTimeout)/1000;

         ProcessBuilder pb = new ProcessBuilder("/Users/nabilkabbani/Tools/z3/build/z3", "-smt2", "-t:" + myTimeout, "-T:" + hardTimeout,pfName);
         pb.inheritIO();

         System.out.println(pfName);
         Process p = pb.start();
         try {
         p.waitFor();
         } catch (InterruptedException e) {
         e.printStackTrace();
         }


         */
        int count = 0;
        for (String vcS : m_perVCsmtLibScripts) {
            long perVCtime = System.currentTimeMillis();
            String fname = "temp" + count++ + ".smt";
            createTextFile(fname, vcS);

            ProcessBuilder pb =
                    new ProcessBuilder("/Users/nabilkabbani/Tools/z3/build/z3",
                            "-smt2", "-T:" + myTimeout / 1000, fname);
            pb.inheritIO();
            Process p = pb.start();
            try {
                p.waitFor();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Time: "
                    + (System.currentTimeMillis() - perVCtime) + " ms\n");
        }
        totalTime = System.currentTimeMillis() - totalTime;
        System.out.println("Elapsed time from construction: " + totalTime
                + " ms" + "\n");
    }

    private String proofFileName() {
        File file = m_environment.getTargetFile();
        ModuleID cid = m_environment.getModuleID(file);
        file = m_environment.getFile(cid);
        String filename = file.toString();
        int temp = filename.indexOf(".");
        String tempfile = filename.substring(0, temp);
        String mainFileName;
        mainFileName = tempfile + ".smt";
        return mainFileName;
    }

    private void createTextFile(String name, String text) throws IOException {
        FileWriter w = new FileWriter(new File(name));
        w.write(text);
        w.write("\n");
        w.flush();
        w.close();
    }

    private String outputProofFile() throws IOException {
        String pfName = proofFileName();
        FileWriter w = new FileWriter(new File(pfName));

        w.write(";SMTLIB 2.0 script " + m_scope.getModuleIdentifier()
                + " generated " + new Date() + "\n\n");

        w.write(m_smtlibScript);
        w.write("\n");
        w.flush();
        w.close();

        return pfName;
    }

    protected void createSMTScriptForProvingTheoremFile(String moduleId) {
        String rString = "";//"(set-option :smt.mbqi false)\n";

        List<MathSymbolEntry> mathSymbolEntries =
                m_scope.query(new EntryTypeQuery<MathSymbolEntry>(
                        MathSymbolEntry.class,
                        MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                        MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));
        String declString =
                "(declare-sort MType)\n(define-sort Entity() MType)\n(define-sort B() Bool)\n";
        declString += "(define-sort Z() Int)\n(define-sort N() Int)\n";

        for (MathSymbolEntry m : mathSymbolEntries) {
            String source = m.getSourceModuleIdentifier().toString();
            if (source == moduleId) {
                MTType type = m.getType();
                String typeString = type.toString();
                String typeClass = type.getClass().getSimpleName();
                String name = m.getName().replace("_", "");
                if (typeString.equals("MType")) {
                    declString +=
                            "(define-sort " + name + "() " + "MType) " + "\n";
                }
                else if (typeClass.equals("MTProper")) {
                    declString +=
                            "(declare-const " + name + " " + typeString + ")\n";
                }
                else {
                    if (typeClass.equals("MTFunction")) {
                        MTFunction mtf = (MTFunction) type;
                        String paramTypes = mtf.getParamString();
                        if (name.equals("Str")) {
                            declString +=
                                    "(define-sort " + name + " (" + paramTypes
                                            + ") " + mtf.getRange().toString()
                                            + ")\n";
                        }
                        else {
                            declString +=
                                    "(declare-fun " + name + " (" + paramTypes
                                            + ") " + mtf.getRange().toString()
                                            + ")\n";
                        }
                    }
                }

            }
        }
        rString += declString;
        //rString += "(assert (forall ((n N )) (LTE zero n) ))\n";
        List<TheoremEntry> theoremEntries =
                m_scope.query(new EntryTypeQuery(TheoremEntry.class,
                        MathSymbolTable.ImportStrategy.IMPORT_RECURSIVE,
                        MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));
        String smtTheorems = "";
        int i = 0;
        for (TheoremEntry e : theoremEntries) {
            String source = e.getSourceModuleIdentifier().toString();
            if (moduleId == source) {
                ++i;
                String thSmt = "(push)\n " + e.toSMTLIB(null, true); //assertion.toSMTLIB();
                thSmt += "(echo \"" + e.getAssertion().toString() + "\")\n";
                thSmt += "(check-sat)\n";
                if (i < theoremEntries.size())
                    thSmt += "(pop)\n";
                smtTheorems += thSmt + "\n";
            }

        }
        rString += smtTheorems;

        try {
            createTextFile("smtProofFileForTheorems.smt", rString);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

}
