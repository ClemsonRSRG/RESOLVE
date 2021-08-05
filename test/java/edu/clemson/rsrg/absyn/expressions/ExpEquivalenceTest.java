/*
 * ExpEquivalenceTest.java
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
package edu.clemson.rsrg.absyn.expressions;

import edu.clemson.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.rsrg.init.CompileEnvironment;
import edu.clemson.rsrg.init.ResolveCompiler;
import edu.clemson.rsrg.init.file.ModuleType;
import edu.clemson.rsrg.init.file.ResolveFile;
import edu.clemson.rsrg.init.file.ResolveFileBasicInfo;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import edu.clemson.rsrg.statushandling.SystemStdHandler;
import edu.clemson.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import org.antlr.v4.runtime.UnbufferedCharStream;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <p>
 * Unit test for making sure that all the {@link MathExp MathExps} implement {@link MathExp#equivalent(Exp)}.
 * </p>
 *
 * @author Yu-Shan Sun
 * 
 * @version 1.0
 */
public class ExpEquivalenceTest {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A fake {@link Location} object to be used to create {@link ResolveConceptualElement ResolveConceptualElements}.
     * </p>
     */
    private final Location FAKE_LOCATION_1;

    /**
     * <p>
     * Another fake {@link Location} object to be used to create {@link ResolveConceptualElement
     * ResolveConceptualElements}.
     * </p>
     */
    private final Location FAKE_LOCATION_2;

    /**
     * <p>
     * A fake {@link LocationDetailModel} object to be used to create {@link ResolveConceptualElement
     * ResolveConceptualElements}.
     * </p>
     */
    private final LocationDetailModel FAKE_LOCATION_DETAIL_MODEL_1;

    /**
     * <p>
     * Another fake {@link LocationDetailModel} object to be used to create {@link ResolveConceptualElement
     * ResolveConceptualElements}.
     * </p>
     */
    private final LocationDetailModel FAKE_LOCATION_DETAIL_MODEL_2;

    /**
     * <p>
     * A fake {@link TypeGraph} object that allows us to assign types to expressions.
     * </p>
     */
    private final TypeGraph FAKE_TYPEGRAPH;

    {
        try {
            FAKE_LOCATION_1 = new Location(
                    new ResolveFile(new ResolveFileBasicInfo("ExpEquivalenceTest", ""), ModuleType.THEORY,
                            new UnbufferedCharStream(new StringReader("")), null, new ArrayList<String>(), ""),
                    0, 0);

            FAKE_LOCATION_DETAIL_MODEL_1 = new LocationDetailModel(FAKE_LOCATION_1.clone(), FAKE_LOCATION_1.clone(),
                    "Fake Location 1");

            FAKE_LOCATION_2 = new Location(
                    new ResolveFile(new ResolveFileBasicInfo("ExpEquivalenceTest", ""), ModuleType.THEORY,
                            new UnbufferedCharStream(new StringReader("")), null, new ArrayList<String>(), ""),
                    1, 0);

            FAKE_LOCATION_DETAIL_MODEL_2 = new LocationDetailModel(FAKE_LOCATION_2.clone(), FAKE_LOCATION_2.clone(),
                    "Fake Location 2");

            // Create a fake typegraph
            // YS: We need to create a ResolveCompiler instance to instantiate
            // the flag manager...
            new ResolveCompiler(new String[0]);
            FAKE_TYPEGRAPH = new TypeGraph(
                    new CompileEnvironment(new String[0], "TestCompiler", new SystemStdHandler()));
        } catch (IOException e) {
            throw new MiscErrorException("Error creating a fake location", e);
        }
    }

    // ===========================================================
    // Test Methods
    // ===========================================================

    /**
     * <p>
     * This tests {@link AlternativeExp#equals(Object)}, {@link AlternativeExp#equivalent(Exp)},
     * {@link AltItemExp#equals(Object)} and {@link AltItemExp#equivalent(Exp)}.
     * </p>
     */
    @Test
    public final void testAlternativeExpressions() {
        // AltItemExp
        AltItemExp altItemExp1 = new AltItemExp(FAKE_LOCATION_1.clone(),
                MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH),
                new VarExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "B")));
        altItemExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        AltItemExp altItemExp2 = new AltItemExp(FAKE_LOCATION_2.clone(),
                MathExp.getTrueVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH),
                new VarExp(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "B")));
        altItemExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(altItemExp1, altItemExp2);

        // AlternativeExp
        AltItemExp otherwiseExp = new AltItemExp(FAKE_LOCATION_1.clone(), null,
                new VarExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "C")));

        AlternativeExp alternativeExp1 = new AlternativeExp(FAKE_LOCATION_1.clone(),
                Arrays.asList(altItemExp1, otherwiseExp));
        alternativeExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        AlternativeExp alternativeExp2 = new AlternativeExp(FAKE_LOCATION_2.clone(),
                Arrays.asList(altItemExp1, otherwiseExp));
        alternativeExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(alternativeExp1, alternativeExp2);
    }

    /**
     * <p>
     * This tests {@link BetweenExp#equals(Object)}} and {@link BetweenExp#equivalent(Exp)}.
     * </p>
     */
    @Test
    public final void testBetweenExpressions() {
        // BetweenExp
        BetweenExp betweenExp1 = new BetweenExp(FAKE_LOCATION_1.clone(),
                Arrays.<Exp> asList(MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH),
                        MathExp.getFalseVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH)));
        betweenExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        BetweenExp betweenExp2 = new BetweenExp(FAKE_LOCATION_2.clone(),
                Arrays.<Exp> asList(MathExp.getTrueVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH),
                        MathExp.getFalseVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH)));
        betweenExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(betweenExp1, betweenExp2);
    }

    /**
     * <p>
     * This tests {@link DotExp#equals(Object)}} and {@link DotExp#equivalent(Exp)}.
     * </p>
     */
    @Test
    public final void testDotExpressions() {
        // DotExp
        DotExp dotExp1 = new DotExp(FAKE_LOCATION_1.clone(),
                Arrays.<Exp> asList(
                        new VarExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "C")),
                        new VarExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "D"))));
        dotExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        DotExp dotExp2 = new DotExp(FAKE_LOCATION_2.clone(),
                Arrays.<Exp> asList(
                        new VarExp(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "C")),
                        new VarExp(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "D"))));
        dotExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(dotExp1, dotExp2);
    }

    /**
     * <p>
     * This tests {@link FunctionExp#equals(Object)}} and {@link FunctionExp#equivalent(Exp)}.
     * </p>
     */
    @Test
    public final void testFunctionExpressions() {
        // FunctionExp
        FunctionExp functionExp1 = new FunctionExp(FAKE_LOCATION_1.clone(),
                new VarExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "C")), null,
                Arrays.<Exp> asList(MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH),
                        MathExp.getFalseVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH)));
        functionExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        FunctionExp functionExp2 = new FunctionExp(FAKE_LOCATION_2.clone(),
                new VarExp(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "C")), null,
                Arrays.<Exp> asList(MathExp.getTrueVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH),
                        MathExp.getFalseVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH)));
        functionExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(functionExp1, functionExp2);
    }

    /**
     * <p>
     * This tests {@link InfixExp#equals(Object)}}, {@link InfixExp#equivalent(Exp)}, {@link EqualsExp#equals(Object)}}
     * and {@link EqualsExp#equivalent(Exp)}.
     * </p>
     */
    @Test
    public final void testInfixExpressions() {
        // InfixExp
        InfixExp infixExp1 = new InfixExp(FAKE_LOCATION_1.clone(),
                MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH), null,
                new PosSymbol(FAKE_LOCATION_1.clone(), "<="),
                MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH));
        infixExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        InfixExp infixExp2 = new InfixExp(FAKE_LOCATION_1.clone(),
                MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH), null,
                new PosSymbol(FAKE_LOCATION_1.clone(), "<="),
                MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH));
        infixExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(infixExp1, infixExp2);

        // EqualsExp
        EqualsExp equalsExp1 = new EqualsExp(FAKE_LOCATION_1.clone(),
                MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH), null, EqualsExp.Operator.EQUAL,
                MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH));
        equalsExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        EqualsExp equalsExp2 = new EqualsExp(FAKE_LOCATION_2.clone(),
                MathExp.getTrueVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH), null, EqualsExp.Operator.EQUAL,
                MathExp.getTrueVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH));
        equalsExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(equalsExp1, equalsExp2);

        EqualsExp notEqualsExp1 = new EqualsExp(FAKE_LOCATION_1.clone(),
                MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH), null, EqualsExp.Operator.NOT_EQUAL,
                MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH));
        notEqualsExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        EqualsExp notEqualsExp2 = new EqualsExp(FAKE_LOCATION_2.clone(),
                MathExp.getTrueVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH), null, EqualsExp.Operator.NOT_EQUAL,
                MathExp.getTrueVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH));
        notEqualsExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(notEqualsExp1, notEqualsExp2);
    }

    /**
     * <p>
     * This tests {@link IterativeExp#equals(Object)}} and {@link IterativeExp#equivalent(Exp)}.
     * </p>
     */
    @Test
    public final void testIterativeExpressions() {
        // IterativeExp
        IterativeExp iterativeExp1 = new IterativeExp(FAKE_LOCATION_1.clone(), IterativeExp.Operator.CONCATENATION,
                new MathVarDec(new PosSymbol(FAKE_LOCATION_1.clone(), "a"),
                        new NameTy(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "Integer"))),
                null,
                new EqualsExp(FAKE_LOCATION_1.clone(), MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH),
                        null, EqualsExp.Operator.NOT_EQUAL,
                        MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH)));
        iterativeExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        IterativeExp iterativeExp2 = new IterativeExp(FAKE_LOCATION_2.clone(), IterativeExp.Operator.CONCATENATION,
                new MathVarDec(new PosSymbol(FAKE_LOCATION_2.clone(), "a"),
                        new NameTy(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "Integer"))),
                null,
                new EqualsExp(FAKE_LOCATION_2.clone(), MathExp.getTrueVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH),
                        null, EqualsExp.Operator.NOT_EQUAL,
                        MathExp.getTrueVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH)));
        iterativeExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(iterativeExp1, iterativeExp2);
    }

    /**
     * <p>
     * This tests {@link LambdaExp#equals(Object)}} and {@link LambdaExp#equivalent(Exp)}.
     * </p>
     */
    @Test
    public final void testLambdaExpressions() {
        // LambdaExp
        LambdaExp lambdaExp1 = new LambdaExp(FAKE_LOCATION_1.clone(),
                Collections.singletonList(new MathVarDec(new PosSymbol(FAKE_LOCATION_1.clone(), "a"),
                        new NameTy(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "Integer")))),
                new EqualsExp(FAKE_LOCATION_1.clone(), MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH),
                        null, EqualsExp.Operator.NOT_EQUAL,
                        MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH)));
        lambdaExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        LambdaExp lambdaExp2 = new LambdaExp(FAKE_LOCATION_2.clone(),
                Collections.singletonList(new MathVarDec(new PosSymbol(FAKE_LOCATION_2.clone(), "a"),
                        new NameTy(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "Integer")))),
                new EqualsExp(FAKE_LOCATION_2.clone(), MathExp.getTrueVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH),
                        null, EqualsExp.Operator.NOT_EQUAL,
                        MathExp.getTrueVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH)));
        lambdaExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(lambdaExp1, lambdaExp2);
    }

    /**
     * <p>
     * This tests {@link LiteralExp#equals(Object)}} and {@link LiteralExp#equivalent(Exp)}.
     * </p>
     */
    @Test
    public final void testLiteralExpressions() {
        // CharExp
        CharExp charExp1 = new CharExp(FAKE_LOCATION_1.clone(), 'c');
        charExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        CharExp charExp2 = new CharExp(FAKE_LOCATION_2.clone(), 'c');
        charExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(charExp1, charExp2);

        // DoubleExp
        DoubleExp doubleExp1 = new DoubleExp(FAKE_LOCATION_1.clone(), 2.0);
        doubleExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        DoubleExp doubleExp2 = new DoubleExp(FAKE_LOCATION_2.clone(), 2.0);
        doubleExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(doubleExp1, doubleExp2);

        // IntegerExp
        IntegerExp integerExp1 = new IntegerExp(FAKE_LOCATION_1.clone(), null, 5);
        integerExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        IntegerExp integerExp2 = new IntegerExp(FAKE_LOCATION_2.clone(), null, 5);
        integerExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(integerExp1, integerExp2);

        // StringExp
        StringExp stringExp1 = new StringExp(FAKE_LOCATION_1.clone(), "Hello");
        stringExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        StringExp stringExp2 = new StringExp(FAKE_LOCATION_2.clone(), "Hello");
        stringExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(stringExp1, stringExp2);
    }

    /**
     * <p>
     * This tests {@link OldExp#equals(Object)}} and {@link OldExp#equivalent(Exp)}.
     * </p>
     */
    @Test
    public final void testOldExpressions() {
        // OldExp
        OldExp oldExp1 = new OldExp(FAKE_LOCATION_1.clone(),
                new VarExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "C")));
        oldExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        OldExp oldExp2 = new OldExp(FAKE_LOCATION_2.clone(),
                new VarExp(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "C")));
        oldExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(oldExp1, oldExp2);
    }

    /**
     * <p>
     * This tests {@link OutfixExp#equals(Object)}} and {@link OutfixExp#equivalent(Exp)}.
     * </p>
     */
    @Test
    public final void testOutfixExpressions() {
        // OutfixExp
        OutfixExp outfixExp1 = new OutfixExp(FAKE_LOCATION_1.clone(), OutfixExp.Operator.DBL_ANGLE,
                new VarExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "C")));
        outfixExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        OutfixExp outfixExp2 = new OutfixExp(FAKE_LOCATION_2.clone(), OutfixExp.Operator.DBL_ANGLE,
                new VarExp(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "C")));
        outfixExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(outfixExp1, outfixExp2);
    }

    /**
     * <p>
     * This tests {@link PrefixExp#equals(Object)}} and {@link PrefixExp#equivalent(Exp)}.
     * </p>
     */
    @Test
    public final void testPrefixExpressions() {
        // PrefixExp
        PrefixExp prefixExp1 = new PrefixExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(null, "Operator"),
                new VarExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "C")));
        prefixExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        PrefixExp prefixExp2 = new PrefixExp(FAKE_LOCATION_2.clone(), null, new PosSymbol(null, "Operator"),
                new VarExp(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "C")));
        prefixExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(prefixExp1, prefixExp2);
    }

    /**
     * <p>
     * This tests {@link QuantExp#equals(Object)}} and {@link QuantExp#equivalent(Exp)}.
     * </p>
     */
    @Test
    public final void testQuantifiedExpressions() {
        // QuantExp
        QuantExp quantExp1 = new QuantExp(FAKE_LOCATION_1.clone(), SymbolTableEntry.Quantification.EXISTENTIAL,
                Collections.singletonList(new MathVarDec(new PosSymbol(FAKE_LOCATION_1.clone(), "a"),
                        new NameTy(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "Integer")))),
                null,
                new EqualsExp(FAKE_LOCATION_1.clone(), MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH),
                        null, EqualsExp.Operator.NOT_EQUAL,
                        MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH)));
        quantExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        QuantExp quantExp2 = new QuantExp(FAKE_LOCATION_2.clone(), SymbolTableEntry.Quantification.EXISTENTIAL,
                Collections.singletonList(new MathVarDec(new PosSymbol(FAKE_LOCATION_2.clone(), "a"),
                        new NameTy(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "Integer")))),
                null,
                new EqualsExp(FAKE_LOCATION_2.clone(), MathExp.getTrueVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH),
                        null, EqualsExp.Operator.NOT_EQUAL,
                        MathExp.getTrueVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH)));
        quantExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(quantExp1, quantExp2);
    }

    /**
     * <p>
     * This tests {@link RecpExp#equals(Object)}}, {@link RecpExp#equivalent(Exp)},
     * {@link TypeReceptaclesExp#equals(Object)} and {@link TypeReceptaclesExp#equivalent(Exp)}}.
     * </p>
     */
    @Test
    public final void testReceptaclesExpressions() {
        // RecpExp
        RecpExp recpExp1 = new RecpExp(FAKE_LOCATION_1.clone(),
                new VarExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "S")));
        recpExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        RecpExp recpExp2 = new RecpExp(FAKE_LOCATION_2.clone(),
                new VarExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "S")));
        recpExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(recpExp1, recpExp2);

        // TypeReceptaclesExp
        TypeReceptaclesExp typeReceptaclesExp1 = new TypeReceptaclesExp(FAKE_LOCATION_1.clone(),
                new VarExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "Integer")));
        typeReceptaclesExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        TypeReceptaclesExp typeReceptaclesExp2 = new TypeReceptaclesExp(FAKE_LOCATION_2.clone(),
                new VarExp(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "Integer")));
        typeReceptaclesExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(typeReceptaclesExp1, typeReceptaclesExp2);
    }

    /**
     * <p>
     * This tests {@link SetCollectionExp#equals(Object)}} and {@link SetCollectionExp#equivalent(Exp)}.
     * </p>
     */
    @Test
    public final void testSetCollectionExpressions() {
        // SetCollectionExp
        SetCollectionExp setCollectionExp1 = new SetCollectionExp(FAKE_LOCATION_1.clone(),
                new HashSet<>(Arrays.<MathExp> asList(
                        new VarExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "C")),
                        new VarExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "D")))));
        setCollectionExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        SetCollectionExp setCollectionExp2 = new SetCollectionExp(FAKE_LOCATION_2.clone(),
                new HashSet<>(Arrays.<MathExp> asList(
                        new VarExp(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "C")),
                        new VarExp(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "D")))));
        setCollectionExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        SetCollectionExp setCollectionExp3 = new SetCollectionExp(FAKE_LOCATION_2.clone(),
                new HashSet<>(Arrays.<MathExp> asList(
                        new VarExp(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "D")),
                        new VarExp(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "C")))));
        setCollectionExp3.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(setCollectionExp1, setCollectionExp2);
        assertionCheck(setCollectionExp1, setCollectionExp3);
    }

    /**
     * <p>
     * This tests {@link SetExp#equals(Object)}} and {@link SetExp#equivalent(Exp)}.
     * </p>
     */
    @Test
    public final void testSetExpressions() {
        // SetExp
        SetExp setExp1 = new SetExp(FAKE_LOCATION_1.clone(),
                new MathVarDec(new PosSymbol(FAKE_LOCATION_1.clone(), "a"),
                        new NameTy(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "Integer"))),
                null,
                new EqualsExp(FAKE_LOCATION_1.clone(), MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH),
                        null, EqualsExp.Operator.NOT_EQUAL,
                        MathExp.getTrueVarExp(FAKE_LOCATION_1.clone(), FAKE_TYPEGRAPH)));
        setExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        SetExp setExp2 = new SetExp(FAKE_LOCATION_2.clone(),
                new MathVarDec(new PosSymbol(FAKE_LOCATION_2.clone(), "a"),
                        new NameTy(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "Integer"))),
                null,
                new EqualsExp(FAKE_LOCATION_2.clone(), MathExp.getTrueVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH),
                        null, EqualsExp.Operator.NOT_EQUAL,
                        MathExp.getTrueVarExp(FAKE_LOCATION_2.clone(), FAKE_TYPEGRAPH)));
        setExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(setExp1, setExp2);
    }

    /**
     * <p>
     * This tests {@link TupleExp#equals(Object)}} and {@link TupleExp#equivalent(Exp)}.
     * </p>
     */
    @Test
    public final void testTupleExpressions() {
        // TupleExp
        TupleExp tupleExp1 = new TupleExp(FAKE_LOCATION_1.clone(),
                Arrays.<Exp> asList(
                        new VarExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "C")),
                        new VarExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "D"))));
        tupleExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        TupleExp tupleExp2 = new TupleExp(FAKE_LOCATION_2.clone(),
                Arrays.<Exp> asList(
                        new VarExp(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "C")),
                        new VarExp(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "D"))));
        tupleExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(tupleExp1, tupleExp2);
    }

    /**
     * <p>
     * This tests {@link UnaryMinusExp#equals(Object)}} and {@link UnaryMinusExp#equivalent(Exp)}.
     * </p>
     */
    @Test
    public final void testUnaryMinusExpressions() {
        // UnaryMinusExp
        UnaryMinusExp unaryMinusExp1 = new UnaryMinusExp(FAKE_LOCATION_1.clone(),
                new IntegerExp(FAKE_LOCATION_1.clone(), null, 1));
        unaryMinusExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        UnaryMinusExp unaryMinusExp2 = new UnaryMinusExp(FAKE_LOCATION_2.clone(),
                new IntegerExp(FAKE_LOCATION_2.clone(), null, 1));
        unaryMinusExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(unaryMinusExp1, unaryMinusExp2);
    }

    /**
     * <p>
     * This tests {@link VarExp#equals(Object)}}, {@link VarExp#equivalent(Exp)}, {@link VCVarExp#equals(Object)} and
     * {@link VCVarExp#equivalent(Exp)}}.
     * </p>
     */
    @Test
    public final void testVarExpressions() {
        // VarExp
        VarExp varExp1 = new VarExp(FAKE_LOCATION_1.clone(), null, new PosSymbol(FAKE_LOCATION_1.clone(), "C"));
        varExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        VarExp varExp2 = new VarExp(FAKE_LOCATION_2.clone(), null, new PosSymbol(FAKE_LOCATION_2.clone(), "C"));
        varExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(varExp1, varExp2);

        // VCVarExp
        VCVarExp vcVarExp1 = new VCVarExp(FAKE_LOCATION_1.clone(), varExp1.clone(), 1);
        vcVarExp1.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_1.clone());

        VCVarExp vcVarExp2 = new VCVarExp(FAKE_LOCATION_2.clone(), varExp2.clone(), 1);
        vcVarExp2.setLocationDetailModel(FAKE_LOCATION_DETAIL_MODEL_2.clone());

        assertionCheck(vcVarExp1, vcVarExp2);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This method performs the assertion tests on the passed in expressions.
     * </p>
     *
     * @param exp1
     *            A {@link Exp}.
     * @param exp2
     *            Another {@link Exp}.
     */
    private void assertionCheck(Exp exp1, Exp exp2) {
        // Equals and Equivalence test
        assertNotEquals(exp1, exp2);
        assertTrue(exp1.equivalent(exp2));
    }

}
