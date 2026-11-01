/*
 * CoveringDispenserTest.java
 */
package edu.clemson.rsrg.nProver.utilities.theorems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CoveringDispenserTest {

    private CoveringDispenser dispenser;

    @BeforeEach
    void setUp() {
        dispenser = new CoveringDispenser();
    }

    // Example 1

    /** Runs the exact call sequence from Example 1. */
    private void runExample1() {
        dispenser.peremptoryAddSourceDesignator(1); // U
        dispenser.insertBlockerDesignator(1);       // Tm1 = (S o U)
        dispenser.insertSourceDesignator(1);        //   contains U
        dispenser.addNextBlocker();
        dispenser.insertBlockerDesignator(2);       // Tm2 = (T o U)
        dispenser.insertSourceDesignator(1);        //   contains U
        dispenser.addNextBlocker();
        dispenser.formCoveringSet();
    }

    @Test
    void example1_sourceSet() {
        runExample1();
        assertEquals(Set.of(1), dispenser.getSourceSet());
    }

    @Test
    void example1_blockerSet() {
        runExample1();
        // { <1, {1}>, <2, {1}> }
        assertEquals(Map.of(1, Set.of(1), 2, Set.of(1)), dispenser.getBlockerSet());
    }

    @Test
    void example1_coveringSets() {
        runExample1();
        // TSSA = {Tm1}, TSSB = {Tm2}, TSSC = {Tm1, Tm2}
        // MINIMAL? If only minimal covers: Set.of(Set.of(1), Set.of(2))
        Set<Set<Integer>> expected = Set.of(Set.of(1), Set.of(2), Set.of(1, 2));
        assertEquals(expected, dispenser.getCoveringSets());
    }

    // Example 2

    /** Runs the exact call sequence from Example 2. */
    private void runExample2() {
        dispenser.peremptoryAddSourceDesignator(1); // alpha
        dispenser.peremptoryAddSourceDesignator(2); // gamma
        dispenser.insertBlockerDesignator(1);       // Tm1 = (alpha o beta)
        dispenser.insertSourceDesignator(1);        //   contains alpha
        dispenser.addNextBlocker();
        dispenser.insertBlockerDesignator(2);       // Tm2 = (gamma o delta)
        dispenser.insertSourceDesignator(2);        //   contains gamma
        dispenser.addNextBlocker();
        dispenser.formCoveringSet();
    }

    @Test
    void example2_sourceSet() {
        runExample2();
        assertEquals(Set.of(1, 2), dispenser.getSourceSet());
    }

    @Test
    void example2_blockerSet() {
        runExample2();
        // { <1, {1}>, <2, {2}> }
        assertEquals(Map.of(1, Set.of(1), 2, Set.of(2)), dispenser.getBlockerSet());
    }

    @Test
    void example2_onlyTsscCovers() {
        runExample2();
        // TSSA = {Tm1} misses gamma, TSSB = {Tm2} misses alpha.
        // Only TSSC = {Tm1, Tm2} covers both. (Same answer whether or not "minimal".)
        assertEquals(Set.of(Set.of(1, 2)), dispenser.getCoveringSets());
    }

    // Example 3


    /** Runs the exact call sequence from Example 3. */
    private void runExample3() {
        dispenser.peremptoryAddSourceDesignator(1); // x2
        dispenser.peremptoryAddSourceDesignator(2); // x3
        dispenser.peremptoryAddSourceDesignator(3); // x4
        dispenser.insertBlockerDesignator(1);       // Tm1
        dispenser.insertSourceDesignator(1);        //   contains x2
        dispenser.insertSourceDesignator(2);        //   contains x3
        dispenser.addNextBlocker();
        dispenser.insertBlockerDesignator(2);       // Tm2
        dispenser.insertSourceDesignator(2);        //   contains x3
        dispenser.insertSourceDesignator(3);        //   contains x4
        dispenser.addNextBlocker();
        dispenser.insertBlockerDesignator(3);       // Tm3
        dispenser.insertSourceDesignator(1);        //   contains x2
        dispenser.insertSourceDesignator(3);        //   contains x4
        dispenser.addNextBlocker();
        dispenser.formCoveringSet();
    }

    @Test
    void example3_sourceSet() {
        runExample3();
        assertEquals(Set.of(1, 2, 3), dispenser.getSourceSet());
    }

    @Test
    void example3_blockerSet() {
        runExample3();
        // { <1, {1, 2}>, <2, {2, 3}>, <3, {1, 3}> }
        assertEquals(Map.of(1, Set.of(1, 2), 2, Set.of(2, 3), 3, Set.of(1, 3)),
                dispenser.getBlockerSet());
    }

    @Test
    void example3_noSingleBlockerCovers() {
        runExample3();
        // Each term alone misses one source, so no one-element set may appear.
        Set<Set<Integer>> result = dispenser.getCoveringSets();
        assertFalse(result.contains(Set.of(1)));
        assertFalse(result.contains(Set.of(2)));
        assertFalse(result.contains(Set.of(3)));
    }

    @Test
    void example3_coveringSets() {
        runExample3();
        // From the document: {Tm1,Tm2}, {Tm1,Tm3}, {Tm2,Tm3}, {Tm1,Tm2,Tm3}
        // MINIMAL? If only minimal covers, drop Set.of(1, 2, 3).
        Set<Set<Integer>> expected = Set.of(
                Set.of(1, 2),
                Set.of(1, 3),
                Set.of(2, 3),
                Set.of(1, 2, 3));
        assertEquals(expected, dispenser.getCoveringSets());
    }
}