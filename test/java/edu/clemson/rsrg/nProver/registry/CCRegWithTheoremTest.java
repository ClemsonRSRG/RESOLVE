/*
 * CCRegWithTheoremTest.java
 * ---------------------------------
 * Copyright (c) 2023
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
 * Unit test for testing the RESOLVE compiler's congruence class registry that uses theorems.
 * </p>
 *
 * @author Nicodemus Msafiri J. M.
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class CCRegWithTheoremTest {

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
    // Arithmetic Equality Axioms (Associate / Commutative Theorems)
    // -----------------------------------------------------------

    /**
     * <p>
     * This tests checks that the registry proves the {@code VC} when we have an arithmetic and commutative equality
     * axiom as the succedent with no additional antecedents.
     * </p>
     * <p>
     * Sequent: {@code {} => {x + y = y + x}}
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
    public final void testSequentVCEqualityAxiomArithmeticCommutative() {
        // "x" (left) - > 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        // "y" (left) - > 4
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

        // "y" (right) - > 4
        int dNum;
        if (myRegistry.checkIfRegistered(4)) {
            dNum = myRegistry.getAccessorFor(4);
        } else {
            dNum = myRegistry.registerCluster(4);
        }

        // "x" (right) -> 3
        int cNum;
        if (myRegistry.checkIfRegistered(3)) {
            cNum = myRegistry.getAccessorFor(3);
        } else {
            cNum = myRegistry.registerCluster(3);
        }

        // "y + x" (right)
        int accessor2;
        myRegistry.appendToClusterArgList(dNum);
        myRegistry.appendToClusterArgList(cNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor2 = myRegistry.getAccessorFor(5);
        } else {
            accessor2 = myRegistry.registerCluster(5);
        }

        // "x + y = y + x"
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

    /**
     * <p>
     * This tests checks that the registry proves the {@code VC} when we have an arithmetic and associative equality
     * axiom as the succedent with no additional antecedents.
     * </p>
     * <p>
     * Sequent: {@code {} => {x + (y + z) = (x + y) + z}}
     * </p>
     *
     * <p>
     * The node labels are as follows:
     * <ul>
     * <li>"<=" -> 1</li>
     * <li>"=" -> 2</li>
     * <li>"y" -> 3</li>
     * <li>"z" -> 4</li>
     * <li>"+" -> 5</li>
     * <li>"x" -> 6</li>
     * </ul>
     * </p>
     */
    @Test
    public final void testSequentVCEqualityAxiomArithmeticAssociative() {
        // "y" (left) - > 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        // "z" (left) - > 4
        int bNum;
        if (myRegistry.checkIfRegistered(4)) {
            bNum = myRegistry.getAccessorFor(4);
        } else {
            bNum = myRegistry.registerCluster(4);
        }

        // "(y + z)" (left)
        int accessor1;
        myRegistry.appendToClusterArgList(aNum);
        myRegistry.appendToClusterArgList(bNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor1 = myRegistry.getAccessorFor(5);
        } else {
            accessor1 = myRegistry.registerCluster(5);
        }

        // "x" (left) - > 6
        int cNum;
        if (myRegistry.checkIfRegistered(6)) {
            cNum = myRegistry.getAccessorFor(6);
        } else {
            cNum = myRegistry.registerCluster(6);
        }

        // "x + (y + z)" (left)
        int accessor2;
        myRegistry.appendToClusterArgList(cNum);
        myRegistry.appendToClusterArgList(accessor1);
        if (myRegistry.checkIfRegistered(5)) {
            accessor2 = myRegistry.getAccessorFor(5);
        } else {
            accessor2 = myRegistry.registerCluster(5);
        }

        // "x" (right) - > 6
        int dNum;
        if (myRegistry.checkIfRegistered(6)) {
            dNum = myRegistry.getAccessorFor(6);
        } else {
            dNum = myRegistry.registerCluster(6);
        }

        // "y" (right) - > 4
        int eNum;
        if (myRegistry.checkIfRegistered(4)) {
            eNum = myRegistry.getAccessorFor(4);
        } else {
            eNum = myRegistry.registerCluster(4);
        }

        // "(x + y)" (right)
        int accessor3;
        myRegistry.appendToClusterArgList(dNum);
        myRegistry.appendToClusterArgList(eNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor3 = myRegistry.getAccessorFor(5);
        } else {
            accessor3 = myRegistry.registerCluster(5);
        }

        // "z" (right) -> 3
        int fNum;
        if (myRegistry.checkIfRegistered(3)) {
            fNum = myRegistry.getAccessorFor(3);
        } else {
            fNum = myRegistry.registerCluster(3);
        }

        // "(x + y) + z" (right)
        int accessor4;
        myRegistry.appendToClusterArgList(accessor3);
        myRegistry.appendToClusterArgList(fNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor4 = myRegistry.getAccessorFor(5);
        } else {
            accessor4 = myRegistry.registerCluster(5);
        }

        // "x + (y + z) = (x + y) + z"
        myRegistry.appendToClusterArgList(accessor2);
        myRegistry.appendToClusterArgList(accessor4);
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
     * This tests checks that the registry proves the {@code VC} when we have an arithmetic, associative and commutative
     * equality axiom as the succedent with no additional antecedents.
     * </p>
     * <p>
     * Sequent: {@code {} => {x + (y + z) = (y + x) + z}}
     * </p>
     *
     * <p>
     * The node labels are as follows:
     * <ul>
     * <li>"<=" -> 1</li>
     * <li>"=" -> 2</li>
     * <li>"y" -> 3</li>
     * <li>"z" -> 4</li>
     * <li>"+" -> 5</li>
     * <li>"x" -> 6</li>
     * </ul>
     * </p>
     */
    @Test
    public final void testSequentVCEqualityAxiomArithmeticAssociativeCommutative() {
        // "y" (left) - > 3
        int aNum;
        if (myRegistry.checkIfRegistered(3)) {
            aNum = myRegistry.getAccessorFor(3);
        } else {
            aNum = myRegistry.registerCluster(3);
        }

        // "z" (left) - > 4
        int bNum;
        if (myRegistry.checkIfRegistered(4)) {
            bNum = myRegistry.getAccessorFor(4);
        } else {
            bNum = myRegistry.registerCluster(4);
        }

        // "(y + z)" (left)
        int accessor1;
        myRegistry.appendToClusterArgList(aNum);
        myRegistry.appendToClusterArgList(bNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor1 = myRegistry.getAccessorFor(5);
        } else {
            accessor1 = myRegistry.registerCluster(5);
        }

        // "x" (left) - > 6
        int cNum;
        if (myRegistry.checkIfRegistered(6)) {
            cNum = myRegistry.getAccessorFor(6);
        } else {
            cNum = myRegistry.registerCluster(6);
        }

        // "x + (y + z)" (left)
        int accessor2;
        myRegistry.appendToClusterArgList(cNum);
        myRegistry.appendToClusterArgList(accessor1);
        if (myRegistry.checkIfRegistered(5)) {
            accessor2 = myRegistry.getAccessorFor(5);
        } else {
            accessor2 = myRegistry.registerCluster(5);
        }

        // "y" (right) - > 4
        int eNum;
        if (myRegistry.checkIfRegistered(4)) {
            eNum = myRegistry.getAccessorFor(4);
        } else {
            eNum = myRegistry.registerCluster(4);
        }

        // "x" (right) - > 6
        int dNum;
        if (myRegistry.checkIfRegistered(6)) {
            dNum = myRegistry.getAccessorFor(6);
        } else {
            dNum = myRegistry.registerCluster(6);
        }

        // "(y + x)" (right)
        int accessor3;
        myRegistry.appendToClusterArgList(eNum);
        myRegistry.appendToClusterArgList(dNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor3 = myRegistry.getAccessorFor(5);
        } else {
            accessor3 = myRegistry.registerCluster(5);
        }

        // "z" (right) -> 3
        int fNum;
        if (myRegistry.checkIfRegistered(3)) {
            fNum = myRegistry.getAccessorFor(3);
        } else {
            fNum = myRegistry.registerCluster(3);
        }

        // "(y + x) + z" (right)
        int accessor4;
        myRegistry.appendToClusterArgList(accessor3);
        myRegistry.appendToClusterArgList(fNum);
        if (myRegistry.checkIfRegistered(5)) {
            accessor4 = myRegistry.getAccessorFor(5);
        } else {
            accessor4 = myRegistry.registerCluster(5);
        }

        // "x + (y + z) = (y + x) + z"
        myRegistry.appendToClusterArgList(accessor2);
        myRegistry.appendToClusterArgList(accessor4);
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