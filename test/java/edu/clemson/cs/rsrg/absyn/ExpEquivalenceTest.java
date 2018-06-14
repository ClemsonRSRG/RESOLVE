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
package edu.clemson.cs.rsrg.absyn;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.init.ResolveCompiler;
import edu.clemson.cs.rsrg.init.file.ModuleType;
import edu.clemson.cs.rsrg.init.file.ResolveFile;
import edu.clemson.cs.rsrg.parsing.data.Location;
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
    private final Location FAKE_LOCATION;

    /**
     * <p>A fake {@link TypeGraph} object that allows us to assign
     * types to expressions.</p>
     */
    private final TypeGraph FAKE_TYPEGRAPH;

    {
        try {
            FAKE_LOCATION =
                    new Location(new ResolveFile("ExpEquivalenceTest",
                            ModuleType.THEORY, new UnbufferedCharStream(
                                    new StringReader("")), null,
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

    // ===========================================================
    // Test Methods
    // ===========================================================

    /**
     * <p>This tests {@link AlternativeExp} and {@link AltItemExp}.</p>
     */
    @Test
    public final void testAlternativeExps() {
        // AltItemExp
        AltItemExp altItemExp =
                new AltItemExp(FAKE_LOCATION.clone(), MathExp.getTrueVarExp(
                        FAKE_LOCATION.clone(), FAKE_TYPEGRAPH), new VarExp(
                        FAKE_LOCATION.clone(), null, new PosSymbol(
                                FAKE_LOCATION.clone(), "B")));
        assertTrue(altItemExp.equivalent(altItemExp.clone()));

        AltItemExp otherwiseExp =
                new AltItemExp(FAKE_LOCATION.clone(), null, new VarExp(
                        FAKE_LOCATION.clone(), null, new PosSymbol(
                                FAKE_LOCATION.clone(), "C")));
        assertTrue(otherwiseExp.equivalent(otherwiseExp.clone()));

        // Build a new AlternativeExp
        AlternativeExp alternativeExp1 =
                new AlternativeExp(FAKE_LOCATION.clone(), Arrays.asList(
                        altItemExp, otherwiseExp));
        assertTrue(alternativeExp1.equivalent(alternativeExp1.clone()));
    }

}