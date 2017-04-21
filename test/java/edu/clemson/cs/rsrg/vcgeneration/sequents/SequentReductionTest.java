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
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.MathExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.PrefixExp;
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
import edu.clemson.cs.rsrg.vcgeneration.sequents.reductionrules.leftrules.*;
import edu.clemson.cs.rsrg.vcgeneration.sequents.reductionrules.rightrules.*;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
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
     * on a {@link Sequent} that only needs the {@link LeftAndRule}.</p>
     */
    @Test
    public final void testLeftAndRule() {
        // Create sequent: "H, (A and B) |- G"
        VarExp A =
                Utilities.createVarExp(FAKE_LOCATION,
                        null, new PosSymbol(FAKE_LOCATION, "A"),
                        FAKE_TYPEGRAPH.BOOLEAN, null);
        VarExp B =
                Utilities.createVarExp(FAKE_LOCATION,
                        null, new PosSymbol(FAKE_LOCATION, "B"),
                        FAKE_TYPEGRAPH.BOOLEAN, null);
        Exp AandB = MathExp.formConjunct(FAKE_LOCATION, A, B);
        List<Exp> antecedents = new ArrayList<>();
        antecedents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "H"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        antecedents.add(AandB);
        List<Exp> consequents = new ArrayList<>();
        consequents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "G"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        Sequent originalSequent =
                new Sequent(FAKE_LOCATION, antecedents, consequents);

        // Reduce the sequent
        SequentReduction reduction = new SequentReduction(originalSequent);
        List<Sequent> resultSequents = reduction.applyReduction();

        // Check that we only have 1 sequent in resultSequents
        assertEquals(resultSequents.size(), 1);

        // 1) The sequents must be different.
        // 2) The result sequent must contain atomic formulas
        // 3) There must be paths in the reduction tree from the
        //    original sequent to the sequents in resultSequents.
        Sequent resultSequent = resultSequents.get(0);
        assertNotEquals(originalSequent, resultSequent);
        assertTrue(resultSequent.consistOfAtomicFormulas());
        assertTrue(pathsExist(reduction.getReductionTree(), originalSequent, resultSequents));

        // AandB cannot be in our sequent
        assertFalse(inSequent(resultSequent, AandB));

        // A and B must be in the antecedent
        assertTrue(inAntecedentExps(resultSequent, A));
        assertTrue(inAntecedentExps(resultSequent, B));
    }

    /**
     * <p>This tests what happens when we call {@link SequentReduction#applyReduction()}
     * on a {@link Sequent} that only needs the {@link LeftImpliesRule}.</p>
     */
    @Test
    public final void testLeftImpliesRule() {
        // Create sequent: "H, (A implies B) |- G"
        VarExp A =
                Utilities.createVarExp(FAKE_LOCATION,
                        null, new PosSymbol(FAKE_LOCATION, "A"),
                        FAKE_TYPEGRAPH.BOOLEAN, null);
        VarExp B =
                Utilities.createVarExp(FAKE_LOCATION,
                        null, new PosSymbol(FAKE_LOCATION, "B"),
                        FAKE_TYPEGRAPH.BOOLEAN, null);
        Exp AimpliesB = MathExp.formImplies(FAKE_LOCATION, A, B);
        List<Exp> antecedents = new ArrayList<>();
        antecedents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "H"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        antecedents.add(AimpliesB);
        List<Exp> consequents = new ArrayList<>();
        consequents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "G"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        Sequent originalSequent =
                new Sequent(FAKE_LOCATION, antecedents, consequents);

        // Reduce the sequent
        SequentReduction reduction = new SequentReduction(originalSequent);
        List<Sequent> resultSequents = reduction.applyReduction();

        // Check that we have 2 sequents in resultSequents
        assertEquals(resultSequents.size(), 2);

        // 1) The sequents must be different.
        // 2) The result sequents must contain atomic formulas
        // 3) There must be paths in the reduction tree from the
        //    original sequent to the sequents in resultSequents.
        Sequent resultSequent1 = resultSequents.get(0);
        Sequent resultSequent2 = resultSequents.get(1);
        assertNotEquals(originalSequent, resultSequent1);
        assertNotEquals(originalSequent, resultSequent2);
        assertTrue(resultSequent1.consistOfAtomicFormulas());
        assertTrue(resultSequent2.consistOfAtomicFormulas());
        assertTrue(pathsExist(reduction.getReductionTree(), originalSequent, resultSequents));

        // AimpliesB cannot be in our sequents
        assertFalse(inSequent(resultSequent1, AimpliesB));
        assertFalse(inSequent(resultSequent2, AimpliesB));

        // B must be in resultSequent1's antecedent,
        // but not in resultSequent2.
        assertTrue(inAntecedentExps(resultSequent1, B));
        assertFalse(inSequent(resultSequent2, B));

        // A must be in resultSequent2's consequent
        // but not in resultSequent1.
        assertTrue(inConsequentExps(resultSequent2, A));
        assertFalse(inSequent(resultSequent1, A));
    }

    /**
     * <p>This tests what happens when we call {@link SequentReduction#applyReduction()}
     * on a {@link Sequent} that only needs the {@link LeftNotRule}.</p>
     */
    @Test
    public final void testLeftNotRule() {
        // Create sequent: "H, not A |- G"
        VarExp A =
                Utilities.createVarExp(FAKE_LOCATION,
                        null, new PosSymbol(FAKE_LOCATION, "A"),
                        FAKE_TYPEGRAPH.BOOLEAN, null);
        Exp notA =
                new PrefixExp(FAKE_LOCATION, null,
                        new PosSymbol(FAKE_LOCATION, "not"), A);
        notA.setMathType(FAKE_TYPEGRAPH.BOOLEAN);
        List<Exp> antecedents = new ArrayList<>();
        antecedents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "H"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        antecedents.add(notA);
        List<Exp> consequents = new ArrayList<>();
        consequents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "G"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        Sequent originalSequent =
                new Sequent(FAKE_LOCATION, antecedents, consequents);

        // Reduce the sequent
        SequentReduction reduction = new SequentReduction(originalSequent);
        List<Sequent> resultSequents = reduction.applyReduction();

        // Check that we only have 1 sequent in resultSequents
        assertEquals(resultSequents.size(), 1);

        // 1) The sequents must be different.
        // 2) The result sequent must contain atomic formulas
        // 3) There must be paths in the reduction tree from the
        //    original sequent to the sequents in resultSequents.
        Sequent resultSequent = resultSequents.get(0);
        assertNotEquals(originalSequent, resultSequent);
        assertTrue(resultSequent.consistOfAtomicFormulas());
        assertTrue(pathsExist(reduction.getReductionTree(), originalSequent, resultSequents));

        // notA cannot be in our sequents
        assertFalse(inSequent(resultSequent, notA));

        // A can't be in resultSequent's antecedent
        assertFalse(inAntecedentExps(resultSequent, A));

        // A must be in resultSequent's consequent
        assertTrue(inConsequentExps(resultSequent, A));
    }

    /**
     * <p>This tests what happens when we call {@link SequentReduction#applyReduction()}
     * on a {@link Sequent} that only needs the {@link LeftOrRule}.</p>
     */
    @Test
    public final void testLeftOrRule() {
        // Create sequent: "H, (A or B) |- G"
        VarExp A =
                Utilities.createVarExp(FAKE_LOCATION,
                        null, new PosSymbol(FAKE_LOCATION, "A"),
                        FAKE_TYPEGRAPH.BOOLEAN, null);
        VarExp B =
                Utilities.createVarExp(FAKE_LOCATION,
                        null, new PosSymbol(FAKE_LOCATION, "B"),
                        FAKE_TYPEGRAPH.BOOLEAN, null);
        Exp AorB = MathExp.formDisjunct(FAKE_LOCATION, A, B);
        List<Exp> antecedents = new ArrayList<>();
        antecedents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "H"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        antecedents.add(AorB);
        List<Exp> consequents = new ArrayList<>();
        consequents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "G"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        Sequent originalSequent =
                new Sequent(FAKE_LOCATION, antecedents, consequents);

        // Reduce the sequent
        SequentReduction reduction = new SequentReduction(originalSequent);
        List<Sequent> resultSequents = reduction.applyReduction();

        // Check that we have 2 sequents in resultSequents
        assertEquals(resultSequents.size(), 2);

        // 1) The sequents must be different.
        // 2) The result sequents must contain atomic formulas
        // 3) There must be paths in the reduction tree from the
        //    original sequent to the sequents in resultSequents.
        Sequent resultSequent1 = resultSequents.get(0);
        Sequent resultSequent2 = resultSequents.get(1);
        assertNotEquals(originalSequent, resultSequent1);
        assertNotEquals(originalSequent, resultSequent2);
        assertTrue(resultSequent1.consistOfAtomicFormulas());
        assertTrue(resultSequent2.consistOfAtomicFormulas());
        assertTrue(pathsExist(reduction.getReductionTree(), originalSequent, resultSequents));

        // AorB cannot be in our sequents
        assertFalse(inSequent(resultSequent1, AorB));
        assertFalse(inSequent(resultSequent2, AorB));

        // B must be in resultSequent1's antecedent,
        // but not in resultSequent2.
        assertTrue(inAntecedentExps(resultSequent1, B));
        assertFalse(inSequent(resultSequent2, B));

        // A must be in resultSequent2's antecedent,
        // but not in resultSequent1.
        assertTrue(inAntecedentExps(resultSequent2, A));
        assertFalse(inSequent(resultSequent1, A));
    }

    /**
     * <p>This tests what happens when we call {@link SequentReduction#applyReduction()}
     * on a {@link Sequent} that only needs the {@link RightAndRule}.</p>
     */
    @Test
    public final void testRightAndRule() {
        // Create sequent: "H |- G, (A and B)"
        VarExp A =
                Utilities.createVarExp(FAKE_LOCATION,
                        null, new PosSymbol(FAKE_LOCATION, "A"),
                        FAKE_TYPEGRAPH.BOOLEAN, null);
        VarExp B =
                Utilities.createVarExp(FAKE_LOCATION,
                        null, new PosSymbol(FAKE_LOCATION, "B"),
                        FAKE_TYPEGRAPH.BOOLEAN, null);
        Exp AandB = MathExp.formConjunct(FAKE_LOCATION, A, B);
        List<Exp> antecedents = new ArrayList<>();
        antecedents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "H"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        List<Exp> consequents = new ArrayList<>();
        consequents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "G"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        consequents.add(AandB);
        Sequent originalSequent =
                new Sequent(FAKE_LOCATION, antecedents, consequents);

        // Reduce the sequent
        SequentReduction reduction = new SequentReduction(originalSequent);
        List<Sequent> resultSequents = reduction.applyReduction();

        // Check that we have 2 sequents in resultSequents
        assertEquals(resultSequents.size(), 2);

        // 1) The sequents must be different.
        // 2) The result sequents must contain atomic formulas
        // 3) There must be paths in the reduction tree from the
        //    original sequent to the sequents in resultSequents.
        Sequent resultSequent1 = resultSequents.get(0);
        Sequent resultSequent2 = resultSequents.get(1);
        assertNotEquals(originalSequent, resultSequent1);
        assertNotEquals(originalSequent, resultSequent2);
        assertTrue(resultSequent1.consistOfAtomicFormulas());
        assertTrue(resultSequent2.consistOfAtomicFormulas());
        assertTrue(pathsExist(reduction.getReductionTree(), originalSequent, resultSequents));

        // AandB cannot be in our sequents
        assertFalse(inSequent(resultSequent1, AandB));
        assertFalse(inSequent(resultSequent2, AandB));

        // A must be in resultSequent1's consequent,
        // but not in resultSequent2.
        assertTrue(inConsequentExps(resultSequent1, A));
        assertFalse(inSequent(resultSequent2, A));

        // B must be in resultSequent2's consequent,
        // but not in resultSequent1.
        assertTrue(inConsequentExps(resultSequent2, B));
        assertFalse(inSequent(resultSequent1, B));
    }

    /**
     * <p>This tests what happens when we call {@link SequentReduction#applyReduction()}
     * on a {@link Sequent} that only needs the {@link RightImpliesRule}.</p>
     */
    @Test
    public final void testRightImpliesRule() {
        // Create sequent: "H |- G, (A implies B)"
        VarExp A =
                Utilities.createVarExp(FAKE_LOCATION,
                        null, new PosSymbol(FAKE_LOCATION, "A"),
                        FAKE_TYPEGRAPH.BOOLEAN, null);
        VarExp B =
                Utilities.createVarExp(FAKE_LOCATION,
                        null, new PosSymbol(FAKE_LOCATION, "B"),
                        FAKE_TYPEGRAPH.BOOLEAN, null);
        Exp AimpliesB = MathExp.formImplies(FAKE_LOCATION, A, B);
        List<Exp> antecedents = new ArrayList<>();
        antecedents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "H"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        List<Exp> consequents = new ArrayList<>();
        consequents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "G"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        consequents.add(AimpliesB);
        Sequent originalSequent =
                new Sequent(FAKE_LOCATION, antecedents, consequents);

        // Reduce the sequent
        SequentReduction reduction = new SequentReduction(originalSequent);
        List<Sequent> resultSequents = reduction.applyReduction();

        // Check that we only have 1 sequent in resultSequents
        assertEquals(resultSequents.size(), 1);

        // 1) The sequents must be different.
        // 2) The result sequent must contain atomic formulas
        // 3) There must be paths in the reduction tree from the
        //    original sequent to the sequents in resultSequents.
        Sequent resultSequent = resultSequents.get(0);
        assertNotEquals(originalSequent, resultSequent);
        assertTrue(resultSequent.consistOfAtomicFormulas());
        assertTrue(pathsExist(reduction.getReductionTree(), originalSequent, resultSequents));

        // AimpliesB cannot be in our sequents
        assertFalse(inSequent(resultSequent, AimpliesB));

        // A must be in resultSequent's antecedent,
        // but not in it's consequent.
        assertTrue(inAntecedentExps(resultSequent, A));
        assertFalse(inConsequentExps(resultSequent, A));

        // B must be in resultSequent's consequent,
        // but not in it's antecedent.
        assertTrue(inConsequentExps(resultSequent, B));
        assertFalse(inAntecedentExps(resultSequent, B));
    }

    /**
     * <p>This tests what happens when we call {@link SequentReduction#applyReduction()}
     * on a {@link Sequent} that only needs the {@link RightNotRule}.</p>
     */
    @Test
    public final void testRightNotRule() {
        // Create sequent: "H |- G, not A"
        VarExp A =
                Utilities.createVarExp(FAKE_LOCATION,
                        null, new PosSymbol(FAKE_LOCATION, "A"),
                        FAKE_TYPEGRAPH.BOOLEAN, null);
        Exp notA =
                new PrefixExp(FAKE_LOCATION, null,
                        new PosSymbol(FAKE_LOCATION, "not"), A);
        notA.setMathType(FAKE_TYPEGRAPH.BOOLEAN);
        List<Exp> antecedents = new ArrayList<>();
        antecedents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "H"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        List<Exp> consequents = new ArrayList<>();
        consequents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "G"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        consequents.add(notA);
        Sequent originalSequent =
                new Sequent(FAKE_LOCATION, antecedents, consequents);

        // Reduce the sequent
        SequentReduction reduction = new SequentReduction(originalSequent);
        List<Sequent> resultSequents = reduction.applyReduction();

        // Check that we have 1 sequent in resultSequents
        assertEquals(resultSequents.size(), 1);

        // 1) The sequents must be different.
        // 2) The result sequent must contain atomic formulas
        // 3) There must be paths in the reduction tree from the
        //    original sequent to the sequents in resultSequents.
        Sequent resultSequent = resultSequents.get(0);
        assertNotEquals(originalSequent, resultSequent);
        assertTrue(resultSequent.consistOfAtomicFormulas());
        assertTrue(pathsExist(reduction.getReductionTree(), originalSequent, resultSequents));

        // notA cannot be in our sequents
        assertFalse(inSequent(resultSequent, notA));

        // A can't be in resultSequent's consequent
        assertFalse(inConsequentExps(resultSequent, A));

        // A must be in resultSequent's antecedent
        assertTrue(inAntecedentExps(resultSequent, A));
    }

    /**
     * <p>This tests what happens when we call {@link SequentReduction#applyReduction()}
     * on a {@link Sequent} that only needs the {@link RightOrRule}.</p>
     */
    @Test
    public final void testRightOrRule() {
        // Create sequent: "H |- G, (A or B)"
        VarExp A =
                Utilities.createVarExp(FAKE_LOCATION,
                        null, new PosSymbol(FAKE_LOCATION, "A"),
                        FAKE_TYPEGRAPH.BOOLEAN, null);
        VarExp B =
                Utilities.createVarExp(FAKE_LOCATION,
                        null, new PosSymbol(FAKE_LOCATION, "B"),
                        FAKE_TYPEGRAPH.BOOLEAN, null);
        Exp AorB = MathExp.formDisjunct(FAKE_LOCATION, A, B);
        List<Exp> antecedents = new ArrayList<>();
        antecedents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "H"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        List<Exp> consequents = new ArrayList<>();
        consequents.add(Utilities.createVarExp(FAKE_LOCATION,
                null, new PosSymbol(FAKE_LOCATION, "G"),
                FAKE_TYPEGRAPH.BOOLEAN, null));
        consequents.add(AorB);
        Sequent originalSequent =
                new Sequent(FAKE_LOCATION, antecedents, consequents);

        // Reduce the sequent
        SequentReduction reduction = new SequentReduction(originalSequent);
        List<Sequent> resultSequents = reduction.applyReduction();

        // Check that we have 1 sequent in resultSequents
        assertEquals(resultSequents.size(), 1);

        // 1) The sequents must be different.
        // 2) The result sequent must contain atomic formulas
        // 3) There must be paths in the reduction tree from the
        //    original sequent to the sequents in resultSequents.
        Sequent resultSequent = resultSequents.get(0);
        assertNotEquals(originalSequent, resultSequent);
        assertTrue(resultSequent.consistOfAtomicFormulas());
        assertTrue(pathsExist(reduction.getReductionTree(), originalSequent, resultSequents));

        // AorB cannot be in our sequent
        assertFalse(inSequent(resultSequent, AorB));

        // A, B must be in the resultSequent's consequent
        assertTrue(inConsequentExps(resultSequent, A));
        assertTrue(inConsequentExps(resultSequent, B));

        // A, B can't be in the resultSequent's antecedent
        assertFalse(inAntecedentExps(resultSequent, A));
        assertFalse(inAntecedentExps(resultSequent, B));
    }

    /**
     * <p>This tests what happens when we call {@link SequentReduction#applyReduction()}
     * on an empty {@link Sequent}.</p>
     */
    @Test
    public final void testSequentReductionOnEmptySequent() {
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
    public final void testSequentReductionWithAtomicFormula1() {
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
    public final void testSequentReductionWithAtomicFormula2() {
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
    public final void testSequentReductionWithAtomicFormula3() {
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

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This method checks to see if the given expression is
     * in the antecedent expression list.</p>
     *
     * @param sequent A {@link Sequent} to be checked.
     * @param exp The {@link Exp} we are searching for.
     *
     * @return {@code true} if it is in the {@code sequent}'s antecedent,
     * {@code false} otherwise.
     */
    private boolean inAntecedentExps(Sequent sequent, Exp exp) {
        return sequent.getAntecedents().contains(exp);
    }

    /**
     * <p>This method checks to see if the given expression is
     * in the consequent expression list.</p>
     *
     * @param sequent A {@link Sequent} to be checked.
     * @param exp The {@link Exp} we are searching for.
     *
     * @return {@code true} if it is in the {@code sequent}'s consequent,
     * {@code false} otherwise.
     */
    private boolean inConsequentExps(Sequent sequent, Exp exp) {
        return sequent.getConcequents().contains(exp);
    }

    /**
     * <p>This method checks to see if the given expression is
     * in the antecedent and/or consequent.</p>
     *
     * @param sequent A {@link Sequent} to be checked.
     * @param exp The {@link Exp} we are searching for.
     *
     * @return {@code true} if it is in the {@code sequent},
     * {@code false} otherwise.
     */
    private boolean inSequent(Sequent sequent, Exp exp) {
        return inAntecedentExps(sequent, exp) && inConsequentExps(sequent, exp);
    }

    /**
     * <p>This method is used to check for there are paths from
     * {@code originalSequent} to each sequent in {@code resultSequents}
     * in the reduction tree.</p>
     *
     * @param g The reduction tree.
     * @param originalSequent The original {@link Sequent}.
     * @param resultSequents The {@link Sequent} that resulted from the
     *                       sequent reduction applications.
     *
     * @return {@code true} if all the {@link Sequent} in {@code resultSequents}
     * have a path from {@code originalSequent} in the reduction tree,
     * {@code false} otherwise.
     */
    private boolean pathsExist(DirectedGraph<Sequent, DefaultEdge> g,
            Sequent originalSequent, List<Sequent> resultSequents) {
        boolean retVal = true;

        // Check to see if the originalSequent is in the tree.
        // YS: Should be in here, but just in case it isn't...
        if (!g.containsVertex(originalSequent)) {
            retVal = false;
        }

        // Check to see if there is a path from originalSequent to each
        // sequent in resultSequents.
        // YS: We choose to use Dijkstra's algorithm, but we could chose
        // other ones if needed.
        ShortestPathAlgorithm<Sequent, DefaultEdge> pathAlgorithm = new DijkstraShortestPath<>(g);
        Iterator<Sequent> iterator = resultSequents.iterator();
        while(iterator.hasNext() && retVal) {
            Sequent next = iterator.next();

            // Check to see if there is a path from
            if (pathAlgorithm.getPath(originalSequent, next) == null) {
                retVal = false;
            }
        }

        return retVal;
    }
}