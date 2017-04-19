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
package edu.clemson.cs.rsrg.vcgeneration.vcs;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.init.file.ModuleType;
import edu.clemson.cs.rsrg.init.file.ResolveFile;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
    {
        try {
            FAKE_LOCATION =
                    new Location(new ResolveFile("SequentReductionTest",
                            ModuleType.THEORY, new ANTLRInputStream(
                                    new StringReader("")),
                            new ArrayList<String>(), ""), 0, 0);
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
     * <p>This tests what happens when we call {@link SequentReduction#getResultingSequents()}
     * without calling {@link SequentReduction#applyReduction()} first.</p>
     */
    @Test
    public final void testNoApplyReductionCall() {
        // We are expecting a MiscErrorException with the following
        // message.
        EXCEPTION_TESTER.expect(MiscErrorException.class);
        EXCEPTION_TESTER
                .expectMessage("Did you forget to call applyReduction?");

        SequentReduction reduction =
                new SequentReduction(new Sequent(FAKE_LOCATION,
                        new ArrayList<Exp>(), new ArrayList<Exp>()));
        reduction.getResultingSequents();
    }

}