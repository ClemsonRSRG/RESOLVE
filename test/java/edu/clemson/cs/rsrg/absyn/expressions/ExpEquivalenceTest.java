/*
 * ExpEquivalenceTest.java
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
package edu.clemson.cs.rsrg.absyn.expressions;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.init.ResolveCompiler;
import edu.clemson.cs.rsrg.init.file.ModuleType;
import edu.clemson.cs.rsrg.init.file.ResolveFile;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.SystemStdHandler;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import org.antlr.v4.runtime.UnbufferedCharStream;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <p>Unit test for making sure that all the {@link MathExp MathExps}
 * implement {@link MathExp#equivalent(Exp)}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ExpEquivalenceTest {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>A fake {@link Location} object to be used to create
     * {@link ResolveConceptualElement ResolveConceptualElements}.</p>
     */
    private final Location FAKE_LOCATION_1;

    /**
     * <p>Another fake {@link Location} object to be used to create
     * {@link ResolveConceptualElement ResolveConceptualElements}.</p>
     */
    private final Location FAKE_LOCATION_2;

    /**
     * <p>A fake {@link LocationDetailModel} object to be used to create
     * {@link ResolveConceptualElement ResolveConceptualElements}.</p>
     */
    private final LocationDetailModel FAKE_LOCATION_DETAIL_MODEL_1;

    /**
     * <p>Another fake {@link LocationDetailModel} object to be used to create
     * {@link ResolveConceptualElement ResolveConceptualElements}.</p>
     */
    private final LocationDetailModel FAKE_LOCATION_DETAIL_MODEL_2;

    /**
     * <p>A fake {@link TypeGraph} object that allows us to assign
     * types to expressions.</p>
     */
    private final TypeGraph FAKE_TYPEGRAPH;

    {
        try {
            FAKE_LOCATION_1 =
                    new Location(new ResolveFile("ExpEquivalenceTest",
                            ModuleType.THEORY, new UnbufferedCharStream(
                                    new StringReader("")), null,
                            new ArrayList<String>(), ""), 0, 0);

            FAKE_LOCATION_DETAIL_MODEL_1 =
                    new LocationDetailModel(FAKE_LOCATION_1.clone(),
                            FAKE_LOCATION_1.clone(), "Fake Location 1");

            FAKE_LOCATION_2 =
                    new Location(new ResolveFile("ExpEquivalenceTest",
                            ModuleType.THEORY, new UnbufferedCharStream(
                                    new StringReader("")), null,
                            new ArrayList<String>(), ""), 1, 0);

            FAKE_LOCATION_DETAIL_MODEL_2 =
                    new LocationDetailModel(FAKE_LOCATION_2.clone(),
                            FAKE_LOCATION_2.clone(), "Fake Location 2");

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

    // ===========================================================
    // Test Methods
    // ===========================================================

    /**
     * <p>This tests {@link AlternativeExp} and {@link AltItemExp}.</p>
     */
    @Test
    public final void testAlternativeExps() {
        // Build new AltItemExp
        AltItemExp altItemExp1 =
                new AltItemExp(FAKE_LOCATION_1.clone(), MathExp.getTrueVarExp(
                        FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH), new VarExp(
                        FAKE_LOCATION_1.clone(), null, new PosSymbol(
                                FAKE_LOCATION_1.clone(), "B")));
        altItemExp1
                .setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        AltItemExp altItemExp2 =
                new AltItemExp(FAKE_LOCATION_2.clone(), MathExp.getTrueVarExp(
                        FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH), new VarExp(
                        FAKE_LOCATION_2.clone(), null, new PosSymbol(
                                FAKE_LOCATION_2.clone(), "B")));
        altItemExp1
                .setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        // Equals and Equivalence test
        assertNotEquals(altItemExp1, altItemExp2);
        assertTrue(altItemExp1.equivalent(altItemExp2));

        // Build a new AlternativeExp
        AltItemExp otherwiseExp =
                new AltItemExp(FAKE_LOCATION_1.clone(), null, new VarExp(
                        FAKE_LOCATION_1.clone(), null, new PosSymbol(
                                FAKE_LOCATION_1.clone(), "C")));

        AlternativeExp alternativeExp1 =
                new AlternativeExp(FAKE_LOCATION_1.clone(), Arrays.asList(
                        altItemExp1, otherwiseExp));
        alternativeExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1
                .clone());

        AlternativeExp alternativeExp2 =
                new AlternativeExp(FAKE_LOCATION_2.clone(), Arrays.asList(
                        altItemExp1, otherwiseExp));
        alternativeExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2
                .clone());

        // Equals and Equivalence test
        assertNotEquals(alternativeExp1, alternativeExp2);
        assertTrue(alternativeExp1.equivalent(alternativeExp2));
    }

}