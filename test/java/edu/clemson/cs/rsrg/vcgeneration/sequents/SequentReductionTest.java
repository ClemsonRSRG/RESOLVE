/*
 * SequentReductionTest.java
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
package edu.clemson.cs.rsrg.vcgeneration.sequents;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.init.ResolveCompiler;
import edu.clemson.cs.rsrg.init.file.ModuleType;
import edu.clemson.cs.rsrg.init.file.ResolveFile;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.SystemStdHandler;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

/**
 * <p>Unit test for testing the RESOLVE compiler's sequent reduction rules.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class SequentReductionTest {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>A fake {@link Location} object to be used to create
     * {@link ResolveConceptualElement ResolveConceptualElements}.</p>
     */
    private final Location FAKE_LOCATION;

    /**
     * <p>A fake {@link TypeGraph} object that allows us to assign
     * types to expressions.</p>
     */
    private final TypeGraph FAKE_TYPEGRAPH;

    {
        try {
            FAKE_LOCATION =
                    new Location(new ResolveFile("SequentReductionTest",
                            ModuleType.THEORY, new ANTLRInputStream(
                                    new StringReader("")),
                            new ArrayList<String>(), ""), 0, 0);

            // Create a fake typegraph
            // YS: We need to create a ResolveCompiler instance to instantiate
            // the flag manager...
            new ResolveCompiler(new String[0]);
            FAKE_TYPEGRAPH =
                    new TypeGraph(new CompileEnvironment(new String[0],
                            "TestCompiler", new SystemStdHandler()));
        }
        catch (IOException e) {
            throw new MiscErrorException("Error creating a fake location", e);
        }
    }

    /** <p>A rule for testing if we get a particular {@link Exception} object.</p> */
    @Rule
    public final ExpectedException EXCEPTION_TESTER = ExpectedException.none();

    // ===========================================================
    // Test Methods
    // ===========================================================

    /**
     * <p>This tests what happens when we call {@link SequentReduction#applyReduction()}
     * on an empty {@link Sequent}.</p>
     */
    @Test
    public final void testReductionOnEmptySequent() {
        // Create an empty sequent
        Sequent originalSequent =
                new Sequent(FAKE_LOCATION, new ArrayList<Exp>(),
                        new ArrayList<Exp>());

        // Reduce the sequent
        SequentReduction reduction = new SequentReduction(originalSequent);
        List<Sequent> resultSequents = reduction.applyReduction();

        // Check that we only have 1 sequent in resultSequents
        assertEquals(resultSequents.size(), 1);

        // Check to see if the element in resultSequents is
        // originalSequent.
        assertEquals(originalSequent, resultSequents.get(0));
    }

    /**
     * <p>This tests what happens when we call {@link SequentReduction#applyReduction()}
     * on a {@link Sequent} that only has atomic formulas.</p>
     */
    @Test
    public final void testReductionSequentWithAtomicFormula1() {
        // Create a sequent with atomic formulas
        List<Exp> antecedents = new ArrayList<>();
        antecedents.add(VarExp.getTrueVarExp(FAKE_LOCATION, FAKE_TYPEGRAPH));
        List<Exp> consequents = new ArrayList<>();
        Sequent originalSequent =
                new Sequent(FAKE_LOCATION, antecedents, consequents);

        // Reduce the sequent
        SequentReduction reduction = new SequentReduction(originalSequent);
        List<Sequent> resultSequents = reduction.applyReduction();

        // Check that we only have 1 sequent in resultSequents
        assertEquals(resultSequents.size(), 1);

        // Check to see if the element in resultSequents is
        // originalSequent.
        assertEquals(originalSequent, resultSequents.get(0));
    }

    /**
     * <p>This tests what happens when we call {@link SequentReduction#applyReduction()}
     * on a {@link Sequent} that only has atomic formulas.</p>
     */
    @Test
    public final void testReductionSequentWithAtomicFormula2() {
        // Create a sequent with atomic formulas
        List<Exp> antecedents = new ArrayList<>();
        List<Exp> consequents = new ArrayList<>();
        consequents.add(VarExp.getFalseVarExp(FAKE_LOCATION, FAKE_TYPEGRAPH));
        Sequent originalSequent =
                new Sequent(FAKE_LOCATION, antecedents, consequents);

        // Reduce the sequent
        SequentReduction reduction = new SequentReduction(originalSequent);
        List<Sequent> resultSequents = reduction.applyReduction();

        // Check that we only have 1 sequent in resultSequents
        assertEquals(resultSequents.size(), 1);

        // Check to see if the element in resultSequents is
        // originalSequent.
        assertEquals(originalSequent, resultSequents.get(0));
    }

    /**
     * <p>This tests what happens when we call {@link SequentReduction#applyReduction()}
     * on a {@link Sequent} that only has atomic formulas.</p>
     */
    @Test
    public final void testReductionSequentWithAtomicFormula3() {
        // Create a sequent with atomic formulas
        List<Exp> antecedents = new ArrayList<>();
        antecedents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "p"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        List<Exp> consequents = new ArrayList<>();
        consequents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "q"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        Sequent originalSequent =
                new Sequent(FAKE_LOCATION, antecedents, consequents);

        // Reduce the sequent
        SequentReduction reduction = new SequentReduction(originalSequent);
        List<Sequent> resultSequents = reduction.applyReduction();

        // Check that we only have 1 sequent in resultSequents
        assertEquals(resultSequents.size(), 1);

        // Check to see if the element in resultSequents is
        // originalSequent.
        assertEquals(originalSequent, resultSequents.get(0));
    }
}