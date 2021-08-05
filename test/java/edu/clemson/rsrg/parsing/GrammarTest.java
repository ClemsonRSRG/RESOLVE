/*
 * GrammarTest.java
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
package edu.clemson.rsrg.parsing;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * <p>
 * Unit test for testing the RESOLVE compiler's lexer/parser on RESOLVE files.
 * </p>
 *
 * @author Yu-Shan Sun
 * 
 * @version 1.0
 */
public class GrammarTest {

    // ===========================================================
    // Test Methods
    // ===========================================================

    /**
     * <p>
     * This tests a sample concept file to see if we encounter any parsing errors.
     * </p>
     */
    @Test
    public final void testParsingConcept() {
        assertEquals(getNumberOfSyntaxErrors("/Stack_Template.co"), 0);
    }

    /**
     * <p>
     * This tests a sample concept realization file to see if we encounter any parsing errors.
     * </p>
     */
    @Test
    public final void testParsingConceptRealiz() {
        assertEquals(getNumberOfSyntaxErrors("/Array_Realiz.rb"), 0);
    }

    /**
     * <p>
     * This tests a sample enhancement file to see if we encounter any parsing errors.
     * </p>
     */
    @Test
    public final void testParsingEnhancement() {
        assertEquals(getNumberOfSyntaxErrors("/Reading_Capability.en"), 0);
    }

    /**
     * <p>
     * This tests a sample enhancement realization file to see if we encounter any parsing errors.
     * </p>
     */
    @Test
    public final void testParsingEnhancementRealiz() {
        assertEquals(getNumberOfSyntaxErrors("/Obvious_Reading_Realiz.rb"), 0);
    }

    /**
     * <p>
     * This tests a sample facility file to see if we encounter any parsing errors.
     * </p>
     */
    @Test
    public final void testParsingFacility() {
        assertEquals(getNumberOfSyntaxErrors("/RevStack.fa"), 0);
    }

    /**
     * <p>
     * This tests a sample precis file to see if we encounter any parsing errors.
     * </p>
     */
    @Test
    public final void testParsingPrecis() {
        assertEquals(getNumberOfSyntaxErrors("/Integer_Theory.mt"), 0);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This invokes the {@link ResolveLexer} and {@link ResolveParser} on the specified filename and checks to see if
     * the are any syntax errors.
     * </p>
     *
     * @param filename
     *            A {@code RESOLVE} filename.
     *
     * @return The number of syntax errors encountered.
     */
    private int getNumberOfSyntaxErrors(String filename) {
        CharStream input;
        try {
            File file = new File(this.getClass().getResource(filename).toURI());
            input = CharStreams.fromPath(file.toPath());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        // Create a RESOLVE language lexer
        ResolveLexer lexer = new ResolveLexer(input);

        // Create a RESOLVE language parser
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ResolveParser parser = new ResolveParser(tokens);
        parser.removeErrorListeners();

        // Check to see if there are any ambiguities in our language
        parser.addErrorListener(new DiagnosticErrorListener(true));
        parser.addErrorListener(new ConsoleErrorListener());
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
        parser.module();

        return parser.getNumberOfSyntaxErrors();
    }
}
