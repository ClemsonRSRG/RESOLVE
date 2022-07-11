/*
 * CCRegNoTheoremTest.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.nProver.registry;

import edu.clemson.rsrg.nProver.utilities.treewakers.AbstractRegisterSequent;
import java.util.BitSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * <p>
 * Unit test for testing the RESOLVE compiler's congruence class registry without using any theorems.
 * </p>
 *
 * @author Nicodemus Msafiri J. M.
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class CCRegNoTheoremTest {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A {@link CongruenceClassRegistry} object to store the antecedents and succedents.
     * </p>
     */
    private CongruenceClassRegistry<Integer, String, String, String> myRegistry;

    // ===========================================================
    // Set up Method
    // ===========================================================

    /**
     * <p>
     * This method sets up the congruence class registry before each test case is run.
     * </p>
     */
    @Before
    public final void setUp() {
        myRegistry = new CongruenceClassRegistry<>(100, 100, 100, 100);
    }

    // ===========================================================
    // Test Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Boolean Literals (No Theorems)
    // -----------------------------------------------------------

    /**
     * <p>
     * This tests checks that the registry proves the {@code VC} when we have {@code true} as the succedent with no
     * additional antecedents.
     * </p>
     * <p>
     * Sequent: {@code {} => {true}}
     * </p>
     *
     * <p>
     * The node labels are as follows:
     * <ul>
     * <li>"<=" -> 1</li>
     * <li>"=" -> 2</li>
     * <li>"true" -> 3</li>
     * </ul>
     * </p>
     */
    @Test
    public final void testSequentVCTrueLiteral() {
        // "true" -> 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        BitSet attribute01 = new BitSet();
        attribute01.set(1); // succedent
        attribute01.set(2); // ultimate
        myRegistry.updateClassAttributes(aNum, attribute01);

        // Check that this VC proves
        assertTrue(myRegistry.checkIfProved());
    }

    /**
     * <p>
     * This tests checks that the registry proves the {@code VC} when we have {@code false} as the succedent with no
     * additional antecedents.
     * </p>
     * <p>
     * Sequent: {@code {} => {false}}
     * </p>
     *
     * <p>
     * The node labels are as follows:
     * <ul>
     * <li>"<=" -> 1</li>
     * <li>"=" -> 2</li>
     * <li>"false" -> 3</li>
     * </ul>
     * </p>
     */
    @Test
    public final void testSequentVCFalseLiteral() {
        // "false" -> 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        BitSet attribute01 = new BitSet();
        attribute01.set(1); // succedent
        attribute01.set(2); // ultimate
        myRegistry.updateClassAttributes(aNum, attribute01);

        // Check that this VC proves
        assertFalse(myRegistry.checkIfProved());
    }

    // -----------------------------------------------------------
    // Arithmetic Equality Axioms (No Theorems)
    // -----------------------------------------------------------

    /**
     * <p>
     * This tests checks that the registry proves the {@code VC} when we have a simple equality axiom as the succedent
     * with no additional antecedents.
     * </p>
     * <p>
     * Sequent: {@code {} => {x = x}}
     * </p>
     *
     * <p>
     * The node labels are as follows:
     * <ul>
     * <li>"<=" -> 1</li>
     * <li>"=" -> 2</li>
     * <li>"x" -> 3</li>
     * </ul>
     * </p>
     */
    @Test
    public final void testSequentVCEqualityAxiomSimple() {
        // "x" (left) -> 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        // "x" (right) -> 3
        int bNum;
        if (myRegistry.checkIfRegistered(3)) {
            bNum = myRegistry.getAccessorFor(3);
        } else {
            bNum = myRegistry.registerCluster(3);
        }

        // "x = x"
        myRegistry.appendToClusterArgList(aNum);
        myRegistry.appendToClusterArgList(bNum);
        myRegistry.addOperatorToSuccedentReflexiveOperatorSet(AbstractRegisterSequent.OP_EQUALS);

        BitSet attribute01 = new BitSet();
        attribute01.set(1); // succedent
        attribute01.set(2); // ultimate
        int accessor = myRegistry.registerCluster(AbstractRegisterSequent.OP_EQUALS);
        if (!myRegistry.checkIfProved()) {
            myRegistry.updateClassAttributes(accessor, attribute01);
        }

        // Check that this VC proves
        assertTrue(myRegistry.checkIfProved());
    }

    /**
     * <p>
     * This tests checks that the registry proves the {@code VC} when we have an arithmetic equality axiom as the
     * succedent with no additional antecedents.
     * </p>
     * <p>
     * Sequent: {@code {} => {x + y = x + y}}
     * </p>
     *
     * <p>
     * The node labels are as follows:
     * <ul>
     * <li>"<=" -> 1</li>
     * <li>"=" -> 2</li>
     * <li>"x" -> 3</li>
     * <li>"y" -> 4</li>
     * <li>"+" -> 5</li>
     * </ul>
     * </p>
     */
    @Test
    public final void testSequentVCEqualityAxiomArithmetic() {
        // "x" (left) -> 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        // "y" (left) -> 4
        int bNum;
        if (myRegistry.checkIfRegistered(4)) {
            bNum = myRegistry.getAccessorFor(4);
        } else {
            bNum = myRegistry.registerCluster(4);
        }

        // "x + y" (left)
        int accessor1;
        myRegistry.appendToClusterArgList(aNum);
        myRegistry.appendToClusterArgList(bNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor1 = myRegistry.getAccessorFor(5);
        } else {
            accessor1 = myRegistry.registerCluster(5);
        }

        // "x" (right) -> 3
        int cNum;
        if (myRegistry.checkIfRegistered(3)) {
            cNum = myRegistry.getAccessorFor(3);
        } else {
            cNum = myRegistry.registerCluster(3);
        }

        // "y" (right) -> 4
        int dNum;
        if (myRegistry.checkIfRegistered(4)) {
            dNum = myRegistry.getAccessorFor(4);
        } else {
            dNum = myRegistry.registerCluster(4);
        }

        // "x + y" (right)
        int accessor2;
        myRegistry.appendToClusterArgList(cNum);
        myRegistry.appendToClusterArgList(dNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor2 = myRegistry.getAccessorFor(5);
        } else {
            accessor2 = myRegistry.registerCluster(5);
        }

        // "x + y = x + y"
        myRegistry.appendToClusterArgList(accessor1);
        myRegistry.appendToClusterArgList(accessor2);
        myRegistry.addOperatorToSuccedentReflexiveOperatorSet(AbstractRegisterSequent.OP_EQUALS);

        BitSet attribute01 = new BitSet();
        attribute01.set(1); // succedent
        attribute01.set(2); // ultimate
        int accessor = myRegistry.registerCluster(AbstractRegisterSequent.OP_EQUALS);
        if (!myRegistry.checkIfProved()) {
            myRegistry.updateClassAttributes(accessor, attribute01);
        }

        // Check that this VC proves
        assertTrue(myRegistry.checkIfProved());
    }

    // -----------------------------------------------------------
    // Antecedent Contains Succedent (No Theorems)
    // -----------------------------------------------------------

    /**
     * <p>
     * This tests checks that the registry proves the {@code VC} when we have a less than or equal antecedent that is
     * exactly the succedent.
     * </p>
     * <p>
     * Sequent: {@code {1 <= |S|} => {1 <= |S|}}
     * </p>
     *
     * <p>
     * The node labels are as follows:
     * <ul>
     * <li>"<=" -> 1</li>
     * <li>"=" -> 2</li>
     * <li>"1" -> 3</li>
     * <li>"S" -> 4</li>
     * <li>"|_|" -> 5</li>
     * </ul>
     * </p>
     */
    @Test
    public final void testSequentVCAntecedentContainsSuccedentLEQ() {
        // "1" (antecedent) -> 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        // "S" (antecedent) -> 4
        int bNum;
        if (myRegistry.checkIfRegistered(4)) {
            bNum = myRegistry.getAccessorFor(4);
        } else {
            bNum = myRegistry.registerCluster(4);
        }

        // "|S|" (antecedent) -> 5
        int accessor1;
        myRegistry.appendToClusterArgList(bNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor1 = myRegistry.getAccessorFor(5);
        } else {
            accessor1 = myRegistry.registerCluster(5);
        }

        // "1 <= |S|" (antecedent)
        int accessor2;
        myRegistry.appendToClusterArgList(aNum);
        myRegistry.appendToClusterArgList(accessor1);
        if (myRegistry.checkIfRegistered(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS)) {
            accessor2 = myRegistry.getAccessorFor(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);
        } else {
            accessor2 = myRegistry.registerCluster(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);
        }

        BitSet attribute01 = new BitSet();
        attribute01.set(0); // set the class antecedent
        attribute01.set(2); // set the class ultimate
        myRegistry.updateClassAttributes(accessor2, attribute01);

        // "1" (succedent) -> 3
        int cNum;
        if (myRegistry.checkIfRegistered(3)) {
            cNum = myRegistry.getAccessorFor(3);
        } else {
            cNum = myRegistry.registerCluster(3);
        }

        // "S" (succedent) -> 4
        int dNum;
        if (myRegistry.checkIfRegistered(4)) {
            dNum = myRegistry.getAccessorFor(4);
        } else {
            dNum = myRegistry.registerCluster(4);
        }

        // "|S|" (succedent) -> 5
        int accessor3;
        myRegistry.appendToClusterArgList(dNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor3 = myRegistry.getAccessorFor(5);
        } else {
            accessor3 = myRegistry.registerCluster(5);
        }

        // "1 <= |S|" (succedent)
        myRegistry.appendToClusterArgList(cNum);
        myRegistry.appendToClusterArgList(accessor3);
        myRegistry.addOperatorToSuccedentReflexiveOperatorSet(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);

        BitSet attribute02 = new BitSet();
        attribute02.set(1); // succedent
        attribute02.set(2); // ultimate
        if (myRegistry.checkIfRegistered(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS)) {
            myRegistry.updateClassAttributes(myRegistry.getAccessorFor(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS),
                    attribute02);
        } else {
            myRegistry.updateClassAttributes(myRegistry.registerCluster(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS),
                    attribute02);
        }

        // Check that this VC proves
        assertTrue(myRegistry.checkIfProved());
    }

    /**
     * <p>
     * This tests checks that the registry proves the {@code VC} when we have an equals expression in the antecedent
     * that is exactly in the succedent.
     * </p>
     * <p>
     * Sequent: {@code {x = y} => {x = y}}
     * </p>
     *
     * <p>
     * The node labels are as follows:
     * <ul>
     * <li>"<=" -> 1</li>
     * <li>"=" -> 2</li>
     * <li>"x" -> 3</li>
     * <li>"y" -> 4</li>
     * </ul>
     * </p>
     */
    @Test
    public final void testSequentVCAntecedentContainsSuccedentEQSimple() {
        // "x" (antecedent) -> 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        // "y" (antecedent) -> 4
        int bNum;
        if (myRegistry.checkIfRegistered(4)) {
            bNum = myRegistry.getAccessorFor(4);
        } else {
            bNum = myRegistry.registerCluster(4);
        }

        // "x = y" (antecedent)
        myRegistry.makeCongruent(aNum, bNum);

        // "x" (succedent) -> 3
        int cNum;
        if (myRegistry.checkIfRegistered(3)) {
            cNum = myRegistry.getAccessorFor(3);
        } else {
            cNum = myRegistry.registerCluster(3);
        }

        // "y" (succedent) -> 4
        int dNum;
        if (myRegistry.checkIfRegistered(4)) {
            dNum = myRegistry.getAccessorFor(4);
        } else {
            dNum = myRegistry.registerCluster(4);
        }

        // "x = y" (succedent)
        myRegistry.appendToClusterArgList(cNum);
        myRegistry.appendToClusterArgList(dNum);
        myRegistry.addOperatorToSuccedentReflexiveOperatorSet(AbstractRegisterSequent.OP_EQUALS);

        BitSet attribute01 = new BitSet();
        attribute01.set(1); // succedent
        attribute01.set(2); // ultimate
        int accessor = myRegistry.registerCluster(AbstractRegisterSequent.OP_EQUALS);
        if (!myRegistry.checkIfProved()) {
            myRegistry.updateClassAttributes(accessor, attribute01);
        }

        // Check that this VC proves
        assertTrue(myRegistry.checkIfProved());
    }

    /**
     * <p>
     * This tests checks that the registry proves the {@code VC} when we have an equals expression in the antecedent
     * that is flipped in the succedent.
     * </p>
     * <p>
     * Sequent: {@code {x = y} => {y = x}}
     * </p>
     *
     * <p>
     * The node labels are as follows:
     * <ul>
     * <li>"<=" -> 1</li>
     * <li>"=" -> 2</li>
     * <li>"x" -> 3</li>
     * <li>"y" -> 4</li>
     * </ul>
     * </p>
     */
    @Test
    public final void testSequentVCAntecedentContainsSuccedentEQSimpleFlipped() {
        // "x" (antecedent) -> 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        // "y" (antecedent) -> 4
        int bNum;
        if (myRegistry.checkIfRegistered(4)) {
            bNum = myRegistry.getAccessorFor(4);
        } else {
            bNum = myRegistry.registerCluster(4);
        }

        // "x = y" (antecedent)
        myRegistry.makeCongruent(aNum, bNum);

        // "y" (succedent) -> 4
        int cNum;
        if (myRegistry.checkIfRegistered(4)) {
            cNum = myRegistry.getAccessorFor(4);
        } else {
            cNum = myRegistry.registerCluster(4);
        }

        // "x" (succedent) -> 3
        int dNum;
        if (myRegistry.checkIfRegistered(3)) {
            dNum = myRegistry.getAccessorFor(3);
        } else {
            dNum = myRegistry.registerCluster(3);
        }

        // "y = x" (succedent)
        myRegistry.appendToClusterArgList(cNum);
        myRegistry.appendToClusterArgList(dNum);
        myRegistry.addOperatorToSuccedentReflexiveOperatorSet(AbstractRegisterSequent.OP_EQUALS);

        BitSet attribute01 = new BitSet();
        attribute01.set(1); // succedent
        attribute01.set(2); // ultimate
        int accessor = myRegistry.registerCluster(AbstractRegisterSequent.OP_EQUALS);
        if (!myRegistry.checkIfProved()) {
            myRegistry.updateClassAttributes(accessor, attribute01);
        }

        // Check that this VC proves
        assertTrue(myRegistry.checkIfProved());
    }

    /**
     * <p>
     * This tests checks that the registry proves the {@code VC} when we have an equals expression that contains nested
     * expressions in the antecedent that is exactly in the succedent.
     * </p>
     * <p>
     * Sequent: {@code {(S = (<Next_Entry''> o S'')), (1 <= |S|)} => {(<Next_Entry''> o S'') = S}}
     * </p>
     *
     * <p>
     * The node labels are as follows:
     * <ul>
     * <li>"<=" -> 1</li>
     * <li>"=" -> 2</li>
     * <li>"S" -> 3</li>
     * <li>"Next_Entry''" -> 4</li>
     * <li>"<_>" -> 5</li>
     * <li>"S''" -> 6</li>
     * <li>"o" -> 7</li>
     * <li>"1" -> 8</li>
     * <li>"|_|" -> 9</li>
     * </ul>
     * </p>
     */
    @Test
    public final void testSequentVCAntecedentContainsSuccedentEQNestedExp() {
        // "S" (antecedent) -> 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        // "Next_Entry''" (antecedent) -> 4
        int bNum;
        if (myRegistry.checkIfRegistered(4)) {
            bNum = myRegistry.getAccessorFor(4);
        } else {
            bNum = myRegistry.registerCluster(4);
        }

        // "<Next_Entry''>" (antecedent) -> 5
        int accessor1;
        myRegistry.appendToClusterArgList(bNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor1 = myRegistry.getAccessorFor(5);
        } else {
            accessor1 = myRegistry.registerCluster(5);
        }

        // "S''" (antecedent) -> 6
        int cNum;
        if (myRegistry.checkIfRegistered(6)) {
            cNum = myRegistry.getAccessorFor(6);
        } else {
            cNum = myRegistry.registerCluster(6);
        }

        // "<Next_Entry''> o S''" (antecedent)
        int accessor2;
        myRegistry.appendToClusterArgList(accessor1);
        myRegistry.appendToClusterArgList(cNum);
        if (myRegistry.checkIfRegistered(7)) {
            accessor2 = myRegistry.getAccessorFor(7);
        } else {
            accessor2 = myRegistry.registerCluster(7);
        }

        // "S = (<Next_Entry''> o S'')" (antecedent)
        myRegistry.makeCongruent(aNum, accessor2);

        // "1" (antecedent) -> 8
        int dNum;
        if (myRegistry.checkIfRegistered(8)) {
            dNum = myRegistry.getAccessorFor(8);
        } else {
            dNum = myRegistry.registerCluster(8);
        }

        // "S" (antecedent) -> 3
        int eNum;
        if (myRegistry.checkIfRegistered(3)) {
            eNum = myRegistry.getAccessorFor(3);
        } else {
            eNum = myRegistry.registerCluster(3);
        }

        // "|S|" (antecedent) -> 9
        int accessor3;
        myRegistry.appendToClusterArgList(eNum);
        if (myRegistry.checkIfRegistered(9)) {
            accessor3 = myRegistry.getAccessorFor(9);
        } else {
            accessor3 = myRegistry.registerCluster(9);
        }

        // "1 <= |S|" (antecedent)
        int accessor4;
        myRegistry.appendToClusterArgList(dNum);
        myRegistry.appendToClusterArgList(accessor3);
        if (myRegistry.checkIfRegistered(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS)) {
            accessor4 = myRegistry.getAccessorFor(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);
        } else {
            accessor4 = myRegistry.registerCluster(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);
        }

        BitSet attribute01 = new BitSet();
        attribute01.set(0); // set the class antecedent
        attribute01.set(2); // set the class ultimate
        myRegistry.updateClassAttributes(accessor4, attribute01);

        // "Next_Entry''" (succedent) -> 4
        int fNum;
        if (myRegistry.checkIfRegistered(4)) {
            fNum = myRegistry.getAccessorFor(4);
        } else {
            fNum = myRegistry.registerCluster(4);
        }

        // "<Next_Entry''>" (succedent) -> 5
        int accessor5;
        myRegistry.appendToClusterArgList(fNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor5 = myRegistry.getAccessorFor(5);
        } else {
            accessor5 = myRegistry.registerCluster(5);
        }

        // "S''" (succedent) -> 6
        int gNum;
        if (myRegistry.checkIfRegistered(6)) {
            gNum = myRegistry.getAccessorFor(6);
        } else {
            gNum = myRegistry.registerCluster(6);
        }

        // "<Next_Entry''> o S''" (succedent)
        int accessor6;
        myRegistry.appendToClusterArgList(accessor5);
        myRegistry.appendToClusterArgList(gNum);
        if (myRegistry.checkIfRegistered(7)) {
            accessor6 = myRegistry.getAccessorFor(7);
        } else {
            accessor6 = myRegistry.registerCluster(7);
        }

        // "S" (succedent) -> 3
        int hNum;
        if (myRegistry.checkIfRegistered(3)) {
            hNum = myRegistry.getAccessorFor(3);
        } else {
            hNum = myRegistry.registerCluster(3);
        }

        // "(<Next_Entry''> o S'') = S" (succedent)
        myRegistry.appendToClusterArgList(accessor6);
        myRegistry.appendToClusterArgList(hNum);
        myRegistry.addOperatorToSuccedentReflexiveOperatorSet(AbstractRegisterSequent.OP_EQUALS);

        BitSet attribute02 = new BitSet();
        attribute02.set(1); // succedent
        attribute02.set(2); // ultimate
        int accessor = myRegistry.registerCluster(AbstractRegisterSequent.OP_EQUALS);
        if (!myRegistry.checkIfProved()) {
            myRegistry.updateClassAttributes(accessor, attribute02);
        }

        // Check that this VC proves
        assertTrue(myRegistry.checkIfProved());
    }

    // -----------------------------------------------------------
    // Transitive Property (No Theorems)
    // -----------------------------------------------------------

    /**
     * <p>
     * This tests checks that the registry proves the {@code VC} when we have antecedents that contains the transitive
     * property that allows us to prove the succedent.
     * </p>
     * <p>
     * Sequent: {@code {a + b <= 9 * a, a + b = 8} => {8 <= 9 * a}}
     * </p>
     *
     * <p>
     * The node labels are as follows:
     * <ul>
     * <li>"<=" -> 1</li>
     * <li>"=" -> 2</li>
     * <li>"a" -> 3</li>
     * <li>"b" -> 4</li>
     * <li>"+" -> 5</li>
     * <li>"9" -> 6</li>
     * <li>"*" -> 7</li>
     * <li>"8" -> 8</li>
     * </ul>
     * </p>
     */
    @Test
    public final void testSequentVCTransitiveAntecedentEQLiteral() {
        // "a" (antecedent) -> 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        // "b" (antecedent) -> 4
        int bNum;
        if (myRegistry.checkIfRegistered(4)) {
            bNum = myRegistry.getAccessorFor(4);
        } else {
            bNum = myRegistry.registerCluster(4);
        }

        // "a + b" (antecedent)
        int accessor1;
        myRegistry.appendToClusterArgList(aNum);
        myRegistry.appendToClusterArgList(bNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor1 = myRegistry.getAccessorFor(5);
        } else {
            accessor1 = myRegistry.registerCluster(5);
        }

        // "9" (antecedent) -> 6
        int cNum;
        if (myRegistry.checkIfRegistered(6)) {
            cNum = myRegistry.getAccessorFor(6);
        } else {
            cNum = myRegistry.registerCluster(6);
        }

        // "a" (antecedent) -> 3
        int dNum;
        if (myRegistry.checkIfRegistered(3)) {
            dNum = myRegistry.getAccessorFor(3);
        } else {
            dNum = myRegistry.registerCluster(3);
        }

        // "9 * a" (antecedent)
        int accessor2;
        myRegistry.appendToClusterArgList(cNum);
        myRegistry.appendToClusterArgList(dNum);
        if (myRegistry.checkIfRegistered(7)) {
            accessor2 = myRegistry.getAccessorFor(7);
        } else {
            accessor2 = myRegistry.registerCluster(7);
        }

        // "a + b <= 9 * a" (antecedent)
        int accessor3;
        myRegistry.appendToClusterArgList(accessor1);
        myRegistry.appendToClusterArgList(accessor2);
        if (myRegistry.checkIfRegistered(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS)) {
            accessor3 = myRegistry.getAccessorFor(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);
        } else {
            accessor3 = myRegistry.registerCluster(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);
        }

        BitSet attribute01 = new BitSet();
        attribute01.set(0); // set the class antecedent
        attribute01.set(2); // set the class ultimate
        myRegistry.updateClassAttributes(accessor3, attribute01);

        // "a" (antecedent) -> 3
        int eNum;
        if (myRegistry.checkIfRegistered(3)) {
            eNum = myRegistry.getAccessorFor(3);
        } else {
            eNum = myRegistry.registerCluster(3);
        }

        // "b" (antecedent) -> 4
        int fNum;
        if (myRegistry.checkIfRegistered(4)) {
            fNum = myRegistry.getAccessorFor(4);
        } else {
            fNum = myRegistry.registerCluster(4);
        }

        // "a + b" (antecedent)
        int accessor4;
        myRegistry.appendToClusterArgList(eNum);
        myRegistry.appendToClusterArgList(fNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor4 = myRegistry.getAccessorFor(5);
        } else {
            accessor4 = myRegistry.registerCluster(5);
        }

        // "8" (antecedent) -> 8
        int gNum;
        if (myRegistry.checkIfRegistered(8)) {
            gNum = myRegistry.getAccessorFor(8);
        } else {
            gNum = myRegistry.registerCluster(8);
        }

        // "a + b = 8" (antecedent)
        myRegistry.makeCongruent(accessor4, gNum);

        // "8" (succedent) -> 8
        int hNum;
        if (myRegistry.checkIfRegistered(8)) {
            hNum = myRegistry.getAccessorFor(8);
        } else {
            hNum = myRegistry.registerCluster(8);
        }

        // "9" (succedent) -> 6
        int iNum;
        if (myRegistry.checkIfRegistered(6)) {
            iNum = myRegistry.getAccessorFor(6);
        } else {
            iNum = myRegistry.registerCluster(6);
        }

        // "a" (succedent) -> 3
        int jNum;
        if (myRegistry.checkIfRegistered(3)) {
            jNum = myRegistry.getAccessorFor(3);
        } else {
            jNum = myRegistry.registerCluster(3);
        }

        // "9 * a" (succedent)
        int accessor5;
        myRegistry.appendToClusterArgList(iNum);
        myRegistry.appendToClusterArgList(jNum);
        if (myRegistry.checkIfRegistered(7)) {
            accessor5 = myRegistry.getAccessorFor(7);
        } else {
            accessor5 = myRegistry.registerCluster(7);
        }

        // "8 <= 9 * a" (succedent)
        myRegistry.appendToClusterArgList(hNum);
        myRegistry.appendToClusterArgList(accessor5);
        myRegistry.addOperatorToSuccedentReflexiveOperatorSet(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);

        BitSet attribute02 = new BitSet();
        attribute02.set(1); // succedent
        attribute02.set(2); // ultimate
        if (myRegistry.checkIfRegistered(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS)) {
            myRegistry.updateClassAttributes(myRegistry.getAccessorFor(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS),
                    attribute02);
        } else {
            myRegistry.updateClassAttributes(myRegistry.registerCluster(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS),
                    attribute02);
        }

        // Check that this VC proves
        assertTrue(myRegistry.checkIfProved());
    }

    /**
     * <p>
     * This tests checks that the registry proves the {@code VC} when we have antecedents that contains the transitive
     * property that allows us to prove the succedent.
     * </p>
     * <p>
     * Sequent: {@code {a + b <= 9 * a, a + b = d + c} => {d + c <= 9 * a}}
     * </p>
     *
     * <p>
     * The node labels are as follows:
     * <ul>
     * <li>"<=" -> 1</li>
     * <li>"=" -> 2</li>
     * <li>"a" -> 3</li>
     * <li>"b" -> 4</li>
     * <li>"+" -> 5</li>
     * <li>"9" -> 6</li>
     * <li>"*" -> 7</li>
     * <li>"d" -> 8</li>
     * <li>"c" -> 9</li>
     * </ul>
     * </p>
     */
    @Test
    public final void testSequentVCTransitiveAntecedentEQAddition() {
        // "a" (antecedent) -> 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        // "b" (antecedent) -> 4
        int bNum;
        if (myRegistry.checkIfRegistered(4)) {
            bNum = myRegistry.getAccessorFor(4);
        } else {
            bNum = myRegistry.registerCluster(4);
        }

        // "a + b" (antecedent)
        int accessor1;
        myRegistry.appendToClusterArgList(aNum);
        myRegistry.appendToClusterArgList(bNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor1 = myRegistry.getAccessorFor(5);
        } else {
            accessor1 = myRegistry.registerCluster(5);
        }

        // "9" (antecedent) -> 6
        int cNum;
        if (myRegistry.checkIfRegistered(6)) {
            cNum = myRegistry.getAccessorFor(6);
        } else {
            cNum = myRegistry.registerCluster(6);
        }

        // "a" (antecedent) -> 3
        int dNum;
        if (myRegistry.checkIfRegistered(3)) {
            dNum = myRegistry.getAccessorFor(3);
        } else {
            dNum = myRegistry.registerCluster(3);
        }

        // "9 * a" (antecedent)
        int accessor2;
        myRegistry.appendToClusterArgList(cNum);
        myRegistry.appendToClusterArgList(dNum);
        if (myRegistry.checkIfRegistered(7)) {
            accessor2 = myRegistry.getAccessorFor(7);
        } else {
            accessor2 = myRegistry.registerCluster(7);
        }

        // "a + b <= 9 * a" (antecedent)
        int accessor3;
        myRegistry.appendToClusterArgList(accessor1);
        myRegistry.appendToClusterArgList(accessor2);
        if (myRegistry.checkIfRegistered(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS)) {
            accessor3 = myRegistry.getAccessorFor(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);
        } else {
            accessor3 = myRegistry.registerCluster(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);
        }

        BitSet attribute01 = new BitSet();
        attribute01.set(0); // set the class antecedent
        attribute01.set(2); // set the class ultimate
        myRegistry.updateClassAttributes(accessor3, attribute01);

        // "a" (antecedent) -> 3
        int eNum;
        if (myRegistry.checkIfRegistered(3)) {
            eNum = myRegistry.getAccessorFor(3);
        } else {
            eNum = myRegistry.registerCluster(3);
        }

        // "b" (antecedent) -> 4
        int fNum;
        if (myRegistry.checkIfRegistered(4)) {
            fNum = myRegistry.getAccessorFor(4);
        } else {
            fNum = myRegistry.registerCluster(4);
        }

        // "a + b" (antecedent)
        int accessor4;
        myRegistry.appendToClusterArgList(eNum);
        myRegistry.appendToClusterArgList(fNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor4 = myRegistry.getAccessorFor(5);
        } else {
            accessor4 = myRegistry.registerCluster(5);
        }

        // "d" (antecedent) -> 8
        int gNum;
        if (myRegistry.checkIfRegistered(8)) {
            gNum = myRegistry.getAccessorFor(8);
        } else {
            gNum = myRegistry.registerCluster(8);
        }

        // "c" (antecedent) -> 9
        int hNum;
        if (myRegistry.checkIfRegistered(9)) {
            hNum = myRegistry.getAccessorFor(9);
        } else {
            hNum = myRegistry.registerCluster(9);
        }

        // "d + c" (antecedent)
        int accessor5;
        myRegistry.appendToClusterArgList(gNum);
        myRegistry.appendToClusterArgList(hNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor5 = myRegistry.getAccessorFor(5);
        } else {
            accessor5 = myRegistry.registerCluster(5);
        }

        // "a + b = d + c" (antecedent)
        myRegistry.makeCongruent(accessor4, accessor5);

        // "d" (succedent) -> 8
        int iNum;
        if (myRegistry.checkIfRegistered(8)) {
            iNum = myRegistry.getAccessorFor(8);
        } else {
            iNum = myRegistry.registerCluster(8);
        }

        // "c" (succedent) -> 9
        int jNum;
        if (myRegistry.checkIfRegistered(9)) {
            jNum = myRegistry.getAccessorFor(9);
        } else {
            jNum = myRegistry.registerCluster(9);
        }

        // "d + c" (succedent)
        int accessor6;
        myRegistry.appendToClusterArgList(iNum);
        myRegistry.appendToClusterArgList(jNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor6 = myRegistry.getAccessorFor(5);
        } else {
            accessor6 = myRegistry.registerCluster(5);
        }

        // "9" (succedent) -> 6
        int kNum;
        if (myRegistry.checkIfRegistered(6)) {
            kNum = myRegistry.getAccessorFor(6);
        } else {
            kNum = myRegistry.registerCluster(6);
        }

        // "a" (succedent) -> 3
        int lNum;
        if (myRegistry.checkIfRegistered(3)) {
            lNum = myRegistry.getAccessorFor(3);
        } else {
            lNum = myRegistry.registerCluster(3);
        }

        // "9 * a" (succedent)
        int accessor7;
        myRegistry.appendToClusterArgList(kNum);
        myRegistry.appendToClusterArgList(lNum);
        if (myRegistry.checkIfRegistered(7)) {
            accessor7 = myRegistry.getAccessorFor(7);
        } else {
            accessor7 = myRegistry.registerCluster(7);
        }

        // "d + c <= 9 * a" (succedent)
        myRegistry.appendToClusterArgList(accessor6);
        myRegistry.appendToClusterArgList(accessor7);
        myRegistry.addOperatorToSuccedentReflexiveOperatorSet(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);

        BitSet attribute02 = new BitSet();
        attribute02.set(1); // succedent
        attribute02.set(2); // ultimate
        if (myRegistry.checkIfRegistered(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS)) {
            myRegistry.updateClassAttributes(myRegistry.getAccessorFor(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS),
                    attribute02);
        } else {
            myRegistry.updateClassAttributes(myRegistry.registerCluster(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS),
                    attribute02);
        }

        // Check that this VC proves
        assertTrue(myRegistry.checkIfProved());
    }

    /**
     * <p>
     * This tests checks that the registry proves the {@code VC} when we have antecedents that contains the transitive
     * property that allows us to prove the succedent.
     * </p>
     * <p>
     * Sequent: {@code {a + b <= 9 * a, a + b = d * c} => {d * c <= 9 * a}}
     * </p>
     *
     * <p>
     * The node labels are as follows:
     * <ul>
     * <li>"<=" -> 1</li>
     * <li>"=" -> 2</li>
     * <li>"a" -> 3</li>
     * <li>"b" -> 4</li>
     * <li>"+" -> 5</li>
     * <li>"9" -> 6</li>
     * <li>"*" -> 7</li>
     * <li>"d" -> 8</li>
     * <li>"c" -> 9</li>
     * </ul>
     * </p>
     */
    @Test
    public final void testSequentVCTransitiveAntecedentEQMultiplication() {
        // "a" (antecedent) -> 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        // "b" (antecedent) -> 4
        int bNum;
        if (myRegistry.checkIfRegistered(4)) {
            bNum = myRegistry.getAccessorFor(4);
        } else {
            bNum = myRegistry.registerCluster(4);
        }

        // "a + b" (antecedent)
        int accessor1;
        myRegistry.appendToClusterArgList(aNum);
        myRegistry.appendToClusterArgList(bNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor1 = myRegistry.getAccessorFor(5);
        } else {
            accessor1 = myRegistry.registerCluster(5);
        }

        // "9" (antecedent) -> 6
        int cNum;
        if (myRegistry.checkIfRegistered(6)) {
            cNum = myRegistry.getAccessorFor(6);
        } else {
            cNum = myRegistry.registerCluster(6);
        }

        // "a" (antecedent) -> 3
        int dNum;
        if (myRegistry.checkIfRegistered(3)) {
            dNum = myRegistry.getAccessorFor(3);
        } else {
            dNum = myRegistry.registerCluster(3);
        }

        // "9 * a" (antecedent)
        int accessor2;
        myRegistry.appendToClusterArgList(cNum);
        myRegistry.appendToClusterArgList(dNum);
        if (myRegistry.checkIfRegistered(7)) {
            accessor2 = myRegistry.getAccessorFor(7);
        } else {
            accessor2 = myRegistry.registerCluster(7);
        }

        // "a + b <= 9 * a" (antecedent)
        int accessor3;
        myRegistry.appendToClusterArgList(accessor1);
        myRegistry.appendToClusterArgList(accessor2);
        if (myRegistry.checkIfRegistered(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS)) {
            accessor3 = myRegistry.getAccessorFor(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);
        } else {
            accessor3 = myRegistry.registerCluster(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);
        }

        BitSet attribute01 = new BitSet();
        attribute01.set(0); // set the class antecedent
        attribute01.set(2); // set the class ultimate
        myRegistry.updateClassAttributes(accessor3, attribute01);

        // "a" (antecedent) -> 3
        int eNum;
        if (myRegistry.checkIfRegistered(3)) {
            eNum = myRegistry.getAccessorFor(3);
        } else {
            eNum = myRegistry.registerCluster(3);
        }

        // "b" (antecedent) -> 4
        int fNum;
        if (myRegistry.checkIfRegistered(4)) {
            fNum = myRegistry.getAccessorFor(4);
        } else {
            fNum = myRegistry.registerCluster(4);
        }

        // "a + b" (antecedent)
        int accessor4;
        myRegistry.appendToClusterArgList(eNum);
        myRegistry.appendToClusterArgList(fNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor4 = myRegistry.getAccessorFor(5);
        } else {
            accessor4 = myRegistry.registerCluster(5);
        }

        // "d" (antecedent) -> 8
        int gNum;
        if (myRegistry.checkIfRegistered(8)) {
            gNum = myRegistry.getAccessorFor(8);
        } else {
            gNum = myRegistry.registerCluster(8);
        }

        // "c" (antecedent) -> 9
        int hNum;
        if (myRegistry.checkIfRegistered(9)) {
            hNum = myRegistry.getAccessorFor(9);
        } else {
            hNum = myRegistry.registerCluster(9);
        }

        // "d * c" (antecedent)
        int accessor5;
        myRegistry.appendToClusterArgList(gNum);
        myRegistry.appendToClusterArgList(hNum);
        if (myRegistry.checkIfRegistered(7)) {
            accessor5 = myRegistry.getAccessorFor(7);
        } else {
            accessor5 = myRegistry.registerCluster(7);
        }

        // "a + b = d * c" (antecedent)
        myRegistry.makeCongruent(accessor4, accessor5);

        // "d" (succedent) -> 8
        int iNum;
        if (myRegistry.checkIfRegistered(8)) {
            iNum = myRegistry.getAccessorFor(8);
        } else {
            iNum = myRegistry.registerCluster(8);
        }

        // "c" (succedent) -> 9
        int jNum;
        if (myRegistry.checkIfRegistered(9)) {
            jNum = myRegistry.getAccessorFor(9);
        } else {
            jNum = myRegistry.registerCluster(9);
        }

        // "d * c" (succedent)
        int accessor6;
        myRegistry.appendToClusterArgList(iNum);
        myRegistry.appendToClusterArgList(jNum);
        if (myRegistry.checkIfRegistered(7)) {
            accessor6 = myRegistry.getAccessorFor(7);
        } else {
            accessor6 = myRegistry.registerCluster(7);
        }

        // "9" (succedent) -> 6
        int kNum;
        if (myRegistry.checkIfRegistered(6)) {
            kNum = myRegistry.getAccessorFor(6);
        } else {
            kNum = myRegistry.registerCluster(6);
        }

        // "a" (succedent) -> 3
        int lNum;
        if (myRegistry.checkIfRegistered(3)) {
            lNum = myRegistry.getAccessorFor(3);
        } else {
            lNum = myRegistry.registerCluster(3);
        }

        // "9 * a" (succedent)
        int accessor7;
        myRegistry.appendToClusterArgList(kNum);
        myRegistry.appendToClusterArgList(lNum);
        if (myRegistry.checkIfRegistered(7)) {
            accessor7 = myRegistry.getAccessorFor(7);
        } else {
            accessor7 = myRegistry.registerCluster(7);
        }

        // "d * c <= 9 * a" (succedent)
        myRegistry.appendToClusterArgList(accessor6);
        myRegistry.appendToClusterArgList(accessor7);
        myRegistry.addOperatorToSuccedentReflexiveOperatorSet(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);

        BitSet attribute02 = new BitSet();
        attribute02.set(1); // succedent
        attribute02.set(2); // ultimate
        if (myRegistry.checkIfRegistered(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS)) {
            myRegistry.updateClassAttributes(myRegistry.getAccessorFor(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS),
                    attribute02);
        } else {
            myRegistry.updateClassAttributes(myRegistry.registerCluster(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS),
                    attribute02);
        }

        // Check that this VC proves
        assertTrue(myRegistry.checkIfProved());
    }

    /**
     * <p>
     * This tests checks that the registry proves the {@code VC} when we have antecedents that contains the transitive
     * property that allows us to prove the succedent.
     * </p>
     * <p>
     * Sequent: {@code {a + b <= 9 * a, a + b = d * c, d * c = 8} => {8 <= 9 * a}}
     * </p>
     *
     * <p>
     * The node labels are as follows:
     * <ul>
     * <li>"<=" -> 1</li>
     * <li>"=" -> 2</li>
     * <li>"a" -> 3</li>
     * <li>"b" -> 4</li>
     * <li>"+" -> 5</li>
     * <li>"9" -> 6</li>
     * <li>"*" -> 7</li>
     * <li>"d" -> 8</li>
     * <li>"c" -> 9</li>
     * <li>"8" -> 10</li>
     * </ul>
     * </p>
     */
    @Test
    public final void testSequentVCTransitiveAntecedentTwoEQExps() {
        // "a" (antecedent) -> 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        // "b" (antecedent) -> 4
        int bNum;
        if (myRegistry.checkIfRegistered(4)) {
            bNum = myRegistry.getAccessorFor(4);
        } else {
            bNum = myRegistry.registerCluster(4);
        }

        // "a + b" (antecedent)
        int accessor1;
        myRegistry.appendToClusterArgList(aNum);
        myRegistry.appendToClusterArgList(bNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor1 = myRegistry.getAccessorFor(5);
        } else {
            accessor1 = myRegistry.registerCluster(5);
        }

        // "9" (antecedent) -> 6
        int cNum;
        if (myRegistry.checkIfRegistered(6)) {
            cNum = myRegistry.getAccessorFor(6);
        } else {
            cNum = myRegistry.registerCluster(6);
        }

        // "a" (antecedent) -> 3
        int dNum;
        if (myRegistry.checkIfRegistered(3)) {
            dNum = myRegistry.getAccessorFor(3);
        } else {
            dNum = myRegistry.registerCluster(3);
        }

        // "9 * a" (antecedent)
        int accessor2;
        myRegistry.appendToClusterArgList(cNum);
        myRegistry.appendToClusterArgList(dNum);
        if (myRegistry.checkIfRegistered(7)) {
            accessor2 = myRegistry.getAccessorFor(7);
        } else {
            accessor2 = myRegistry.registerCluster(7);
        }

        // "a + b <= 9 * a" (antecedent)
        int accessor3;
        myRegistry.appendToClusterArgList(accessor1);
        myRegistry.appendToClusterArgList(accessor2);
        if (myRegistry.checkIfRegistered(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS)) {
            accessor3 = myRegistry.getAccessorFor(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);
        } else {
            accessor3 = myRegistry.registerCluster(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);
        }

        BitSet attribute01 = new BitSet();
        attribute01.set(0); // set the class antecedent
        attribute01.set(2); // set the class ultimate
        myRegistry.updateClassAttributes(accessor3, attribute01);

        // "a" (antecedent) -> 3
        int eNum;
        if (myRegistry.checkIfRegistered(3)) {
            eNum = myRegistry.getAccessorFor(3);
        } else {
            eNum = myRegistry.registerCluster(3);
        }

        // "b" (antecedent) -> 4
        int fNum;
        if (myRegistry.checkIfRegistered(4)) {
            fNum = myRegistry.getAccessorFor(4);
        } else {
            fNum = myRegistry.registerCluster(4);
        }

        // "a + b" (antecedent)
        int accessor4;
        myRegistry.appendToClusterArgList(eNum);
        myRegistry.appendToClusterArgList(fNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor4 = myRegistry.getAccessorFor(5);
        } else {
            accessor4 = myRegistry.registerCluster(5);
        }

        // "d" (antecedent) -> 8
        int gNum;
        if (myRegistry.checkIfRegistered(8)) {
            gNum = myRegistry.getAccessorFor(8);
        } else {
            gNum = myRegistry.registerCluster(8);
        }

        // "c" (antecedent) -> 9
        int hNum;
        if (myRegistry.checkIfRegistered(9)) {
            hNum = myRegistry.getAccessorFor(9);
        } else {
            hNum = myRegistry.registerCluster(9);
        }

        // "d * c" (antecedent)
        int accessor5;
        myRegistry.appendToClusterArgList(gNum);
        myRegistry.appendToClusterArgList(hNum);
        if (myRegistry.checkIfRegistered(7)) {
            accessor5 = myRegistry.getAccessorFor(7);
        } else {
            accessor5 = myRegistry.registerCluster(7);
        }

        // "a + b = d * c" (antecedent)
        myRegistry.makeCongruent(accessor4, accessor5);

        // "d" (antecedent) -> 8
        int iNum;
        if (myRegistry.checkIfRegistered(8)) {
            iNum = myRegistry.getAccessorFor(8);
        } else {
            iNum = myRegistry.registerCluster(8);
        }

        // "c" (antecedent) -> 9
        int jNum;
        if (myRegistry.checkIfRegistered(9)) {
            jNum = myRegistry.getAccessorFor(9);
        } else {
            jNum = myRegistry.registerCluster(9);
        }

        // "d * c" (antecedent)
        int accessor6;
        myRegistry.appendToClusterArgList(iNum);
        myRegistry.appendToClusterArgList(jNum);
        if (myRegistry.checkIfRegistered(7)) {
            accessor6 = myRegistry.getAccessorFor(7);
        } else {
            accessor6 = myRegistry.registerCluster(7);
        }

        // "8" (antecedent) -> 10
        int kNum;
        if (myRegistry.checkIfRegistered(10)) {
            kNum = myRegistry.getAccessorFor(10);
        } else {
            kNum = myRegistry.registerCluster(10);
        }

        // "d * c = 8" (antecedent)
        myRegistry.makeCongruent(accessor6, kNum);

        // "8" (succedent) -> 10
        int lNum;
        if (myRegistry.checkIfRegistered(10)) {
            lNum = myRegistry.getAccessorFor(10);
        } else {
            lNum = myRegistry.registerCluster(10);
        }

        // "9" (succedent) -> 6
        int mNum;
        if (myRegistry.checkIfRegistered(6)) {
            mNum = myRegistry.getAccessorFor(6);
        } else {
            mNum = myRegistry.registerCluster(6);
        }

        // "a" (succedent) -> 3
        int nNum;
        if (myRegistry.checkIfRegistered(3)) {
            nNum = myRegistry.getAccessorFor(3);
        } else {
            nNum = myRegistry.registerCluster(3);
        }

        // "9 * a" (succedent)
        int accessor7;
        myRegistry.appendToClusterArgList(mNum);
        myRegistry.appendToClusterArgList(nNum);
        if (myRegistry.checkIfRegistered(7)) {
            accessor7 = myRegistry.getAccessorFor(7);
        } else {
            accessor7 = myRegistry.registerCluster(7);
        }

        // "8 <= 9 * a" (succedent)
        myRegistry.appendToClusterArgList(lNum);
        myRegistry.appendToClusterArgList(accessor7);
        myRegistry.addOperatorToSuccedentReflexiveOperatorSet(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);

        BitSet attribute02 = new BitSet();
        attribute02.set(1); // succedent
        attribute02.set(2); // ultimate
        if (myRegistry.checkIfRegistered(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS)) {
            myRegistry.updateClassAttributes(myRegistry.getAccessorFor(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS),
                    attribute02);
        } else {
            myRegistry.updateClassAttributes(myRegistry.registerCluster(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS),
                    attribute02);
        }

        // Check that this VC proves
        assertTrue(myRegistry.checkIfProved());
    }

    /**
     * <p>
     * This tests checks that the registry proves the {@code VC} when we have antecedents that contains the transitive
     * property that allows us to prove the succedent.
     * </p>
     * <p>
     * Sequent: {@code {a + b <= m, m = 9 * a, a + b = 8 * c + 9 * d, 8 * c + 9 * d = 8} => {8 <= 9 * a}}
     * </p>
     *
     * <p>
     * The node labels are as follows:
     * <ul>
     * <li>"<=" -> 1</li>
     * <li>"=" -> 2</li>
     * <li>"a" -> 3</li>
     * <li>"b" -> 4</li>
     * <li>"+" -> 5</li>
     * <li>"m" -> 6</li>
     * <li>"9" -> 7</li>
     * <li>"*" -> 8</li>
     * <li>"8" -> 9</li>
     * <li>"c" -> 10</li>
     * <li>"d" -> 11</li>
     * </ul>
     * </p>
     */
    @Test
    public final void testSequentVCTransitiveAntecedentMixedExps() {
        // "a" (antecedent) -> 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        // "b" (antecedent) -> 4
        int bNum;
        if (myRegistry.checkIfRegistered(4)) {
            bNum = myRegistry.getAccessorFor(4);
        } else {
            bNum = myRegistry.registerCluster(4);
        }

        // "a + b" (antecedent)
        int accessor1;
        myRegistry.appendToClusterArgList(aNum);
        myRegistry.appendToClusterArgList(bNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor1 = myRegistry.getAccessorFor(5);
        } else {
            accessor1 = myRegistry.registerCluster(5);
        }

        // "m" (antecedent) -> 6
        int cNum;
        if (myRegistry.checkIfRegistered(6)) {
            cNum = myRegistry.getAccessorFor(6);
        } else {
            cNum = myRegistry.registerCluster(6);
        }

        // "a + b <= m" (antecedent)
        int accessor2;
        myRegistry.appendToClusterArgList(accessor1);
        myRegistry.appendToClusterArgList(cNum);
        if (myRegistry.checkIfRegistered(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS)) {
            accessor2 = myRegistry.getAccessorFor(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);
        } else {
            accessor2 = myRegistry.registerCluster(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);
        }

        BitSet attribute01 = new BitSet();
        attribute01.set(0); // set the class antecedent
        attribute01.set(2); // set the class ultimate
        myRegistry.updateClassAttributes(accessor2, attribute01);

        // "m" (antecedent) -> 6
        int dNum;
        if (myRegistry.checkIfRegistered(6)) {
            dNum = myRegistry.getAccessorFor(6);
        } else {
            dNum = myRegistry.registerCluster(6);
        }

        // "9" (antecedent) -> 7
        int eNum;
        if (myRegistry.checkIfRegistered(7)) {
            eNum = myRegistry.getAccessorFor(7);
        } else {
            eNum = myRegistry.registerCluster(7);
        }

        // "a" (antecedent) -> 3
        int fNum;
        if (myRegistry.checkIfRegistered(3)) {
            fNum = myRegistry.getAccessorFor(3);
        } else {
            fNum = myRegistry.registerCluster(3);
        }

        // "9 * a" (antecedent)
        int accessor3;
        myRegistry.appendToClusterArgList(eNum);
        myRegistry.appendToClusterArgList(fNum);
        if (myRegistry.checkIfRegistered(8)) {
            accessor3 = myRegistry.getAccessorFor(8);
        } else {
            accessor3 = myRegistry.registerCluster(8);
        }

        // "m = 9 * a" (antecedent)
        myRegistry.makeCongruent(dNum, accessor3);

        // "a" (antecedent) -> 3
        int gNum;
        if (myRegistry.checkIfRegistered(3)) {
            gNum = myRegistry.getAccessorFor(3);
        } else {
            gNum = myRegistry.registerCluster(3);
        }

        // "b" (antecedent) -> 4
        int hNum;
        if (myRegistry.checkIfRegistered(4)) {
            hNum = myRegistry.getAccessorFor(4);
        } else {
            hNum = myRegistry.registerCluster(4);
        }

        // "a + b" (antecedent)
        int accessor4;
        myRegistry.appendToClusterArgList(gNum);
        myRegistry.appendToClusterArgList(hNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor4 = myRegistry.getAccessorFor(5);
        } else {
            accessor4 = myRegistry.registerCluster(5);
        }

        // "8" (antecedent) -> 9
        int iNum;
        if (myRegistry.checkIfRegistered(9)) {
            iNum = myRegistry.getAccessorFor(9);
        } else {
            iNum = myRegistry.registerCluster(9);
        }

        // "c" (antecedent) -> 10
        int jNum;
        if (myRegistry.checkIfRegistered(10)) {
            jNum = myRegistry.getAccessorFor(10);
        } else {
            jNum = myRegistry.registerCluster(10);
        }

        // "8 * c" (antecedent)
        int accessor5;
        myRegistry.appendToClusterArgList(iNum);
        myRegistry.appendToClusterArgList(jNum);
        if (myRegistry.checkIfRegistered(8)) {
            accessor5 = myRegistry.getAccessorFor(8);
        } else {
            accessor5 = myRegistry.registerCluster(8);
        }

        // "9" (antecedent) -> 7
        int kNum;
        if (myRegistry.checkIfRegistered(7)) {
            kNum = myRegistry.getAccessorFor(7);
        } else {
            kNum = myRegistry.registerCluster(7);
        }

        // "d" (antecedent) -> 11
        int lNum;
        if (myRegistry.checkIfRegistered(11)) {
            lNum = myRegistry.getAccessorFor(11);
        } else {
            lNum = myRegistry.registerCluster(11);
        }

        // "9 * d" (antecedent)
        int accessor6;
        myRegistry.appendToClusterArgList(kNum);
        myRegistry.appendToClusterArgList(lNum);
        if (myRegistry.checkIfRegistered(8)) {
            accessor6 = myRegistry.getAccessorFor(8);
        } else {
            accessor6 = myRegistry.registerCluster(8);
        }

        // "8 * c + 9 * d" (antecedent)
        int accessor7;
        myRegistry.appendToClusterArgList(accessor5);
        myRegistry.appendToClusterArgList(accessor6);
        if (myRegistry.checkIfRegistered(5)) {
            accessor7 = myRegistry.getAccessorFor(5);
        } else {
            accessor7 = myRegistry.registerCluster(5);
        }

        // "a + b = 8 * c + 9 * d" (antecedent)
        myRegistry.makeCongruent(accessor4, accessor7);

        // "8" (antecedent) -> 9
        int mNum;
        if (myRegistry.checkIfRegistered(9)) {
            mNum = myRegistry.getAccessorFor(9);
        } else {
            mNum = myRegistry.registerCluster(9);
        }

        // "c" (antecedent) -> 10
        int nNum;
        if (myRegistry.checkIfRegistered(10)) {
            nNum = myRegistry.getAccessorFor(10);
        } else {
            nNum = myRegistry.registerCluster(10);
        }

        // "8 * c" (antecedent)
        int accessor8;
        myRegistry.appendToClusterArgList(mNum);
        myRegistry.appendToClusterArgList(nNum);
        if (myRegistry.checkIfRegistered(8)) {
            accessor8 = myRegistry.getAccessorFor(8);
        } else {
            accessor8 = myRegistry.registerCluster(8);
        }

        // "9" (antecedent) -> 7
        int oNum;
        if (myRegistry.checkIfRegistered(7)) {
            oNum = myRegistry.getAccessorFor(7);
        } else {
            oNum = myRegistry.registerCluster(7);
        }

        // "d" (antecedent) -> 11
        int pNum;
        if (myRegistry.checkIfRegistered(11)) {
            pNum = myRegistry.getAccessorFor(11);
        } else {
            pNum = myRegistry.registerCluster(11);
        }

        // "9 * d" (antecedent)
        int accessor9;
        myRegistry.appendToClusterArgList(oNum);
        myRegistry.appendToClusterArgList(pNum);
        if (myRegistry.checkIfRegistered(8)) {
            accessor9 = myRegistry.getAccessorFor(8);
        } else {
            accessor9 = myRegistry.registerCluster(8);
        }

        // "8 * c + 9 * d" (antecedent)
        int accessor10;
        myRegistry.appendToClusterArgList(accessor8);
        myRegistry.appendToClusterArgList(accessor9);
        if (myRegistry.checkIfRegistered(5)) {
            accessor10 = myRegistry.getAccessorFor(5);
        } else {
            accessor10 = myRegistry.registerCluster(5);
        }

        // "8" (antecedent) -> 9
        int qNum;
        if (myRegistry.checkIfRegistered(9)) {
            qNum = myRegistry.getAccessorFor(9);
        } else {
            qNum = myRegistry.registerCluster(9);
        }

        // "8 * c + 9 * d = 8" (antecedent)
        myRegistry.makeCongruent(accessor10, qNum);

        // "8" (succedent) -> 9
        int rNum;
        if (myRegistry.checkIfRegistered(9)) {
            rNum = myRegistry.getAccessorFor(9);
        } else {
            rNum = myRegistry.registerCluster(9);
        }

        // "9" (succedent) -> 7
        int sNum;
        if (myRegistry.checkIfRegistered(7)) {
            sNum = myRegistry.getAccessorFor(7);
        } else {
            sNum = myRegistry.registerCluster(7);
        }

        // "a" (succedent) -> 3
        int tNum;
        if (myRegistry.checkIfRegistered(3)) {
            tNum = myRegistry.getAccessorFor(3);
        } else {
            tNum = myRegistry.registerCluster(3);
        }

        // "9 * a" (succedent)
        int accessor11;
        myRegistry.appendToClusterArgList(sNum);
        myRegistry.appendToClusterArgList(tNum);
        if (myRegistry.checkIfRegistered(8)) {
            accessor11 = myRegistry.getAccessorFor(8);
        } else {
            accessor11 = myRegistry.registerCluster(8);
        }

        // "8 <= 9 * a" (succedent)
        myRegistry.appendToClusterArgList(rNum);
        myRegistry.appendToClusterArgList(accessor11);
        myRegistry.addOperatorToSuccedentReflexiveOperatorSet(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS);

        BitSet attribute02 = new BitSet();
        attribute02.set(1); // succedent
        attribute02.set(2); // ultimate
        if (myRegistry.checkIfRegistered(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS)) {
            myRegistry.updateClassAttributes(myRegistry.getAccessorFor(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS),
                    attribute02);
        } else {
            myRegistry.updateClassAttributes(myRegistry.registerCluster(AbstractRegisterSequent.OP_LESS_THAN_OR_EQUALS),
                    attribute02);
        }

        // Check that this VC proves
        assertTrue(myRegistry.checkIfProved());
    }

    /**
     * <p>
     * This tests checks that the registry proves the {@code VC} when we have antecedents that contains the transitive
     * property that allows us to prove the succedent.
     * </p>
     * <p>
     * Sequent: {@code {a + b <= 9 * a, a + b = d * c, d * c = 8} => {8 <= 9 * a}}
     * </p>
     *
     * <p>
     * The node labels are as follows:
     * <ul>
     * <li>"<=" -> 1</li>
     * <li>"=" -> 2</li>
     * <li>"a" -> 3</li>
     * <li>"b" -> 4</li>
     * <li>"c" -> 5</li>
     * <li>"d" -> 6</li>
     * <li>"e" -> 7</li>
     * <li>"f" -> 8</li>
     * <li>"+" -> 9</li>
     * </ul>
     * </p>
     */
    @Test
    public final void testSequentVCTransitiveAntecedentMultipleEQExps() {
        // "a" (antecedent) -> 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        // "b" (antecedent) -> 4
        int bNum;
        if (myRegistry.checkIfRegistered(4)) {
            bNum = myRegistry.getAccessorFor(4);
        } else {
            bNum = myRegistry.registerCluster(4);
        }

        // "a = b" (antecedent)
        myRegistry.makeCongruent(aNum, bNum);

        // "b" (antecedent) -> 4
        int cNum;
        if (myRegistry.checkIfRegistered(4)) {
            cNum = myRegistry.getAccessorFor(4);
        } else {
            cNum = myRegistry.registerCluster(4);
        }

        // "c" (antecedent) -> 5
        int dNum;
        if (myRegistry.checkIfRegistered(5)) {
            dNum = myRegistry.getAccessorFor(5);
        } else {
            dNum = myRegistry.registerCluster(5);
        }

        // "b = c" (antecedent)
        myRegistry.makeCongruent(cNum, dNum);

        // "d" (antecedent) -> 6
        int eNum;
        if (myRegistry.checkIfRegistered(6)) {
            eNum = myRegistry.getAccessorFor(6);
        } else {
            eNum = myRegistry.registerCluster(6);
        }

        // "c" (antecedent) -> 5
        int fNum;
        if (myRegistry.checkIfRegistered(5)) {
            fNum = myRegistry.getAccessorFor(5);
        } else {
            fNum = myRegistry.registerCluster(5);
        }

        // "d = c" (antecedent)
        myRegistry.makeCongruent(eNum, fNum);

        // "e" (antecedent) -> 7
        int gNum;
        if (myRegistry.checkIfRegistered(7)) {
            gNum = myRegistry.getAccessorFor(7);
        } else {
            gNum = myRegistry.registerCluster(7);
        }

        // "d" (antecedent) -> 6
        int hNum;
        if (myRegistry.checkIfRegistered(6)) {
            hNum = myRegistry.getAccessorFor(6);
        } else {
            hNum = myRegistry.registerCluster(6);
        }

        // "f" (antecedent) -> 8
        int iNum;
        if (myRegistry.checkIfRegistered(8)) {
            iNum = myRegistry.getAccessorFor(8);
        } else {
            iNum = myRegistry.registerCluster(8);
        }

        // "d + f" (antecedent)
        int accessor1;
        myRegistry.appendToClusterArgList(hNum);
        myRegistry.appendToClusterArgList(iNum);
        if (myRegistry.checkIfRegistered(9)) {
            accessor1 = myRegistry.getAccessorFor(9);
        } else {
            accessor1 = myRegistry.registerCluster(9);
        }

        // "e = d + f" (antecedent)
        myRegistry.makeCongruent(gNum, accessor1);

        // "a" (succedent) -> 3
        int jNum;
        if (myRegistry.checkIfRegistered(3)) {
            jNum = myRegistry.getAccessorFor(3);
        } else {
            jNum = myRegistry.registerCluster(3);
        }

        // "f" (succedent) -> 8
        int kNum;
        if (myRegistry.checkIfRegistered(8)) {
            kNum = myRegistry.getAccessorFor(8);
        } else {
            kNum = myRegistry.registerCluster(8);
        }

        // "a + f" (succedent)
        int accessor2;
        myRegistry.appendToClusterArgList(jNum);
        myRegistry.appendToClusterArgList(kNum);
        if (myRegistry.checkIfRegistered(9)) {
            accessor2 = myRegistry.getAccessorFor(9);
        } else {
            accessor2 = myRegistry.registerCluster(9);
        }

        // "e" (succedent) -> 7
        int lNum;
        if (myRegistry.checkIfRegistered(7)) {
            lNum = myRegistry.getAccessorFor(7);
        } else {
            lNum = myRegistry.registerCluster(7);
        }

        // "a + f = e" (succedent)
        myRegistry.appendToClusterArgList(accessor2);
        myRegistry.appendToClusterArgList(lNum);
        myRegistry.addOperatorToSuccedentReflexiveOperatorSet(AbstractRegisterSequent.OP_EQUALS);

        BitSet attribute01 = new BitSet();
        attribute01.set(1); // succedent
        attribute01.set(2); // ultimate
        int accessor = myRegistry.registerCluster(AbstractRegisterSequent.OP_EQUALS);
        if (!myRegistry.checkIfProved()) {
            myRegistry.updateClassAttributes(accessor, attribute01);
        }

        // Check that this VC proves
        assertTrue(myRegistry.checkIfProved());
    }

}