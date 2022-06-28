/*
 * CCRegApp.java
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
package edu.clemson.rsrg.nProver;

import edu.clemson.rsrg.nProver.registry.CongruenceClassRegistry;
import edu.clemson.rsrg.nProver.registry.CongruenceCluster;

import java.util.*;

public class CCRegApp {

    /**
     * ROOT LABEL MAPPING 1 - a | 2 - b | 3 - + | 4 - x | 5 - <= | 8 - 8|9 - 9 | 6 - d | 7 - c | 10 - \ |
     *
     * Bit set |A|S|U|
     *
     */
    private static int a, b, c, d, e, f, g, h, j, i, k, l, m, n, o, p, q, r, s, t, aa, bb, kk, dd, ee, cc, ii, gg, ll,
            aaa, bbb, kkk, ccc, ooo, mmm, nnn, lll, ddd, iii, eee, hhh, fff, ppp;

    public static void main(String[] args) {
        BitSet attb = new BitSet();

        boolean check = false;
        CongruenceClassRegistry<Integer, String, String, String> testRegistry = new CongruenceClassRegistry<>(100, 100,
                100, 100);

        // sequentVC_3(testRegistry, attb);

        sequentVC_3(testRegistry, attb);

        /*
         * //printing cluster argument array System.out.println("Argument Array"); for (int i = 1; i < 17; i++){//17
         * System.out.print("|" + testRegistry.getClusterArgArray()[i].getNextClusterArg()+ "|"); System.out.print("|" +
         * testRegistry.getClusterArgArray()[i].getPrevClusterArg()+ "|"); System.out.print("|" +
         * testRegistry.getClusterArgArray()[i].getCcNumber()+ "|"); System.out.print("|" +
         * testRegistry.getClusterArgArray()[i].getClusterNumber()+ "|"); System.out.print("|" +
         * testRegistry.getClusterArgArray()[i].getNxtIndexWithSameCCNumberInLevel()+ "|"); System.out.println("|" +
         * testRegistry.getClusterArgArray()[i].getAlternativeArg()+ "|"); }
         *
         * System.out.println("Cluster Array"); for (int i = 1; i < 9; i++){ System.out.print("|" +
         * testRegistry.getClusterArray()[i].getTreeNodeLabel() + "|"); System.out.print("|" +
         * testRegistry.getClusterArray()[i].getIndexToArgList() + "|"); System.out.print("|" +
         * testRegistry.getClusterArray()[i].getIndexToCongruenceClass() + "|"); System.out.print("|" +
         * testRegistry.getClusterArray()[i].getNextPlantationCluster() + "|"); System.out.print("|" +
         * testRegistry.getClusterArray()[i].getPreviousPlantationCluster() + "|"); System.out.print("|" +
         * testRegistry.getClusterArray()[i].getDominantCluster() + "|"); System.out.println("|" +
         * testRegistry.getClusterArray()[i].getNextWithSameArg() + "|"); }
         *
         * System.out.println("Plantation Array");
         *
         * for(int i = 1; i < 9; i++){ System.out.print("|" + testRegistry.getPlantationArray()[i].getTreeNodeLabel() +
         * "|"); System.out.print("|" + testRegistry.getPlantationArray()[i].getFirstPlantationCluster() + "|");
         * System.out.print("|" + testRegistry.getPlantationArray()[i].getPlantationTag() + "|"); System.out.print("|" +
         * testRegistry.getPlantationArray()[i].getNextCCPlantation() + "|"); System.out.print("|" +
         * testRegistry.getPlantationArray()[i].getNextVrtyPlantation() + "|"); System.out.println("|" +
         * testRegistry.getPlantationArray()[i].getPrvVrtyPlantation() + "|"); }
         *
         * System.out.println("Class Array"); for(int i = 1; i < 9 ; i++){ System.out.print("|" +
         * testRegistry.getCongruenceClassArray()[i].getFirstPlantation() + "|"); System.out.print("|" +
         * testRegistry.getCongruenceClassArray()[i].getClassTag() + "|"); System.out.print("|" +
         * testRegistry.getCongruenceClassArray()[i].getLastArgStringPosition() + "|"); System.out.println("|" +
         * testRegistry.getCongruenceClassArray()[i].getDominantCClass() + "|"); }
         *
         * System.out.println();
         *
         */

    }

    /**
     * num -- 4, min_int -- 2, max_int -- 3, 0 -- 0, 1 -- 1, <= -- 5
     *
     * @param testRegistry
     * @param attb
     */

    private static void sequentVC_4(CongruenceClassRegistry<Integer, String, String, String> testRegistry,
            BitSet attb) {
        // 6, 9, 9, 9 for printing
        // 0
        a = testRegistry.registerCluster(0);

        // num
        b = testRegistry.registerCluster(4);

        BitSet attribute_01 = new BitSet();
        attribute_01.set(0); // antecedent
        attribute_01.set(2); // ultimate

        testRegistry.appendToClusterArgList(a);
        testRegistry.appendToClusterArgList(b);
        // 0 <= num
        k = testRegistry.registerCluster(5);
        testRegistry.updateClassAttributes(k, attribute_01);

        // min_int
        d = testRegistry.registerCluster(2);

        BitSet attribute_02 = new BitSet();
        attribute_02.set(0);
        attribute_02.set(2);
        testRegistry.appendToClusterArgList(d);
        testRegistry.appendToClusterArgList(b);
        // min_int <= num
        e = testRegistry.registerCluster(5);
        testRegistry.updateClassAttributes(e, attribute_02);

        // max_int
        j = testRegistry.registerCluster(3);

        BitSet attribute_03 = new BitSet();
        attribute_03.set(0); // antecedent
        attribute_03.set(2); // ultimate

        testRegistry.appendToClusterArgList(b);
        testRegistry.appendToClusterArgList(j);
        // num <= max_int
        n = testRegistry.registerCluster(5);
        testRegistry.updateClassAttributes(n, attribute_03);

        BitSet attribute_04 = new BitSet();
        attribute_04.set(0);
        attribute_04.set(2);

        testRegistry.appendToClusterArgList(d);
        testRegistry.appendToClusterArgList(a);
        // min_int <= 0
        c = testRegistry.registerCluster(7);
        testRegistry.updateClassAttributes(c, attribute_04);

        // 1
        i = testRegistry.registerCluster(1);

        BitSet attribute_05 = new BitSet();
        attribute_05.set(0);
        attribute_05.set(2);
        testRegistry.appendToClusterArgList(i);
        testRegistry.appendToClusterArgList(j);

        // 1 <= max_int
        h = testRegistry.registerCluster(5);
        testRegistry.updateClassAttributes(h, attribute_05);

        testRegistry.appendToClusterArgList(b);
        testRegistry.appendToClusterArgList(j);
        // num <= max_int
        if (testRegistry.checkIfRegistered(5)) {
            q = testRegistry.getAccessorFor(5);
        } else {
            testRegistry.addOperatorToSuccedentReflexiveOperatorSet(5);
            q = testRegistry.registerCluster(5);
        }
        BitSet attribute_06 = new BitSet();
        attribute_06.set(1);// succedent
        attribute_06.set(2);// ultimate
        testRegistry.updateClassAttributes(q, attribute_06);

        if (testRegistry.checkIfProved()) {
            System.out.println("VC 4 Proved");
        } else {
            System.out.println("VC 4 Not Proved");
        }

    }

    /**
     * ROOT LABEL MAPPING 1 - a | 2 - b | 3 - + | 4 - x | 5 - <= | 8 - 8|9 - 9 | 6 - d | 7 - c | 10 - \ | m - 11 |
     *
     *
     *
     */

    private static void sequentVC_5(CongruenceClassRegistry<Integer, String, String, String> testRegistry,
            BitSet attb) {

        // a
        aaa = testRegistry.registerCluster(1);

        // b
        bbb = testRegistry.registerCluster(2);

        // m
        kkk = testRegistry.registerCluster(11);

        // a + b
        testRegistry.appendToClusterArgList(aaa);
        testRegistry.appendToClusterArgList(bbb);
        ccc = testRegistry.registerCluster(3);

        // a + b <= m
        BitSet attb_1 = new BitSet();
        attb_1.set(0);// antecedent
        attb_1.set(2);// ultimate
        testRegistry.appendToClusterArgList(ccc);
        testRegistry.appendToClusterArgList(kkk);
        lll = testRegistry.registerCluster(5);
        testRegistry.updateClassAttributes(lll, attb_1);

        // 9
        ddd = testRegistry.registerCluster(9);// no changes to the array, doesn't have arguments

        // 9 x a
        testRegistry.appendToClusterArgList(aaa);
        testRegistry.appendToClusterArgList(ddd);
        eee = testRegistry.registerCluster(4);

        // m = 9 x a
        testRegistry.makeCongruent(eee, kkk);

        // 8
        iii = testRegistry.registerCluster(8);

        // c
        mmm = testRegistry.registerCluster(7);

        // 8 x c
        testRegistry.appendToClusterArgList(iii);
        testRegistry.appendToClusterArgList(mmm);
        nnn = testRegistry.registerCluster(4);

        // d
        hhh = testRegistry.registerCluster(6);

        // 9 x d
        testRegistry.appendToClusterArgList(ddd);
        testRegistry.appendToClusterArgList(hhh);
        ooo = testRegistry.registerCluster(4);

        // 8 x c + 9 x d
        testRegistry.appendToClusterArgList(nnn);
        testRegistry.appendToClusterArgList(ooo);
        fff = testRegistry.registerCluster(3);

        // a + b = 8 x c + 9 x d
        testRegistry.makeCongruent(ccc, fff);

        // 8 x c + 9 x d = 8
        testRegistry.makeCongruent(iii, fff);

        BitSet attb_2 = new BitSet();
        attb_2.set(1); // succedent
        attb_2.set(2); // ultimate
        // 8 <= 9 x a
        testRegistry.appendToClusterArgList(iii);
        testRegistry.appendToClusterArgList(eee);
        testRegistry.addOperatorToSuccedentReflexiveOperatorSet(5);
        if (testRegistry.checkIfRegistered(5)) {
            ppp = testRegistry.getAccessorFor(5);
        } else {
            ppp = testRegistry.registerCluster(5);
        }
        testRegistry.updateClassAttributes(ppp, attb_2);
        if (testRegistry.checkIfProved()) {
            System.out.println("VC 5 Proved");
        } else {
            System.out.println("VC 5 Not Proved");
        }
    }

    /**
     * ROOT LABEL MAPPING 1 - a | 2 - b | 3 - + | 4 - x | 5 - <= | 8 - 8|9 - 9 | 6 - d | 7 - c | 10 - \ | m - 11 | f -
     * 12 | e - 13 | == - 14|
     *
     *
     *
     */

    private static void sequentVC_6(CongruenceClassRegistry<Integer, String, String, String> testRegistry,
            BitSet attb) {
        // 6, 9, 9, 9 for printing
        // a
        a = testRegistry.registerCluster(1);

        // b
        b = testRegistry.registerCluster(2);

        testRegistry.appendToClusterArgList(a);
        testRegistry.appendToClusterArgList(b);
        // a + b
        k = testRegistry.registerCluster(3);

        // 9
        d = testRegistry.registerCluster(9);
        testRegistry.appendToClusterArgList(d);
        testRegistry.appendToClusterArgList(a);
        // 9 x a
        e = testRegistry.registerCluster(4);

        // a + b <= 9 x a
        testRegistry.appendToClusterArgList(k);
        testRegistry.appendToClusterArgList(e);
        n = testRegistry.registerCluster(5);
        BitSet attribute_2 = new BitSet();
        attribute_2.set(0); // antecedent
        attribute_2.set(2); // ultimate
        testRegistry.updateClassAttributes(n, attribute_2);

        // c
        c = testRegistry.registerCluster(7);

        // a = b
        testRegistry.makeCongruent(a, b);

        // e
        i = testRegistry.registerCluster(13);

        // f
        h = testRegistry.registerCluster(12);

        testRegistry.appendToClusterArgList(c);
        testRegistry.appendToClusterArgList(h);
        // c + f
        m = testRegistry.registerCluster(3);

        // e = c + f
        testRegistry.makeCongruent(m, i);

        // d
        o = testRegistry.registerCluster(6);

        testRegistry.appendToClusterArgList(o);
        testRegistry.appendToClusterArgList(c);
        // d x c
        p = testRegistry.registerCluster(4);

        testRegistry.appendToClusterArgList(p);
        testRegistry.appendToClusterArgList(e);
        // d x c <= 9 x a
        testRegistry.addOperatorToSuccedentReflexiveOperatorSet(5);
        q = testRegistry.registerCluster(5);
        BitSet attribute_3 = new BitSet();
        attribute_3.set(1);// succedent
        attribute_3.set(2);// ultimate
        testRegistry.updateClassAttributes(q, attribute_3);

        testRegistry.appendToClusterArgList(a);
        testRegistry.appendToClusterArgList(h);
        // a + f
        if (testRegistry.checkIfRegistered(3)) {
            r = testRegistry.getAccessorFor(3);
        } else {
            r = testRegistry.registerCluster(3);
        }
        testRegistry.appendToClusterArgList(r);
        testRegistry.appendToClusterArgList(i);
        testRegistry.addOperatorToSuccedentReflexiveOperatorSet(14);
        // a + f = e
        s = testRegistry.registerCluster(14);
        BitSet attribute_4 = new BitSet();
        attribute_4.set(1); // succedent
        attribute_4.set(2); // ultimate
        testRegistry.updateClassAttributes(s, attribute_4);

        // b = c
        testRegistry.makeCongruent(b, c);

        if (testRegistry.checkIfProved()) {
            System.out.println("VC 6 Proved");
        } else {
            System.out.println("VC 6 Not Proved");
        }

    }

    /**
     * ROOT LABEL MAPPING 1 - a | 2 - b | 3 - + | 4 - x | 5 - <= | 8 - 8|9 - 9 | 6 - d | 7 - c | 10 - \ | m - 11 | f -
     * 12 | e - 13 | == - 14|
     *
     *
     *
     */

    private static void sequentVC_7(CongruenceClassRegistry<Integer, String, String, String> testRegistry,
            BitSet attb) {
        // 6, 9, 9, 9 for printing
        // a
        // attb.set(0);//antecedent
        aa = testRegistry.registerCluster(1);

        // b
        bb = testRegistry.registerCluster(2);
        // a = b
        testRegistry.makeCongruent(aa, bb);

        // c
        kk = testRegistry.registerCluster(7);

        // b = c
        testRegistry.makeCongruent(bb, kk);

        // d
        dd = testRegistry.registerCluster(6);

        // d = c
        testRegistry.makeCongruent(dd, kk);

        // f
        ee = testRegistry.registerCluster(12);

        // d + f
        testRegistry.appendToClusterArgList(dd);
        testRegistry.appendToClusterArgList(ee);
        cc = testRegistry.registerCluster(3);

        // e
        ii = testRegistry.registerCluster(13);

        // e = d + f
        // testRegistry.makeCongruent(i, c);

        // a + f
        testRegistry.appendToClusterArgList(aa);
        testRegistry.appendToClusterArgList(ee);
        if (testRegistry.checkIfRegistered(3)) {
            gg = testRegistry.getAccessorFor(3);
        } else {
            gg = testRegistry.registerCluster(3);
        }

        BitSet attb_2 = new BitSet();
        attb_2.set(1); // succedent
        attb_2.set(2); // ultimate

        // a + f == e
        testRegistry.appendToClusterArgList(gg);
        testRegistry.appendToClusterArgList(ii);
        testRegistry.addOperatorToSuccedentReflexiveOperatorSet(14);
        ll = testRegistry.registerCluster(14);
        if (testRegistry.checkIfProved()) {
            System.out.println("VC Proved_7");
        } else {
            testRegistry.updateClassAttributes(ll, attb_2);
        }

        // e = d + f
        testRegistry.makeCongruent(ii, cc);

        if (testRegistry.checkIfProved()) {
            System.out.println("VC Proved_7");
        }

    }

    /**
     * ROOT LABEL MAPPING 1 - a | 2 - b | 3 - + | 4 - x | 5 - <= | 8 - 8|9 - 9 | 6 - d | 7 - c | 10 - \ | m - 11 | f -
     * 12 | e - 13 | == - 14|
     *
     *
     *
     */

    private static void sequentVC_8(CongruenceClassRegistry<Integer, String, String, String> testRegistry,
            BitSet attb) {
        // 6, 9, 9, 9 for printing
        // a
        a = testRegistry.registerCluster(1);

        // b
        b = testRegistry.registerCluster(2);

        testRegistry.appendToClusterArgList(a);
        testRegistry.appendToClusterArgList(b);
        // a + b
        k = testRegistry.registerCluster(3);

        // 9
        d = testRegistry.registerCluster(9);
        testRegistry.appendToClusterArgList(d);
        testRegistry.appendToClusterArgList(a);
        // 9 x a
        e = testRegistry.registerCluster(4);

        // a + b <= 9 x a
        testRegistry.appendToClusterArgList(k);
        testRegistry.appendToClusterArgList(e);
        n = testRegistry.registerCluster(5);
        BitSet attribute_2 = new BitSet();
        attribute_2.set(0); // antecedent
        attribute_2.set(2); // ultimate
        testRegistry.updateClassAttributes(n, attribute_2);

        // c
        c = testRegistry.registerCluster(7);

        // a = b
        testRegistry.makeCongruent(a, b);

        // e
        i = testRegistry.registerCluster(13);

        // f
        h = testRegistry.registerCluster(12);

        testRegistry.appendToClusterArgList(c);
        testRegistry.appendToClusterArgList(h);
        // c + f
        m = testRegistry.registerCluster(3);

        // e = c + f
        testRegistry.makeCongruent(m, i);

        // d
        o = testRegistry.registerCluster(6);

        testRegistry.appendToClusterArgList(o);
        testRegistry.appendToClusterArgList(c);
        // d x c
        p = testRegistry.registerCluster(4);

        testRegistry.appendToClusterArgList(p);
        testRegistry.appendToClusterArgList(e);
        // d x c <= 9 x a
        testRegistry.addOperatorToSuccedentReflexiveOperatorSet(5);
        q = testRegistry.registerCluster(5);
        BitSet attribute_3 = new BitSet();
        attribute_3.set(1);// succedent
        attribute_3.set(2);// ultimate
        testRegistry.updateClassAttributes(q, attribute_3);

        testRegistry.appendToClusterArgList(a);
        testRegistry.appendToClusterArgList(h);
        // a + f
        if (testRegistry.checkIfRegistered(3)) {
            r = testRegistry.getAccessorFor(3);
        } else {
            r = testRegistry.registerCluster(3);
        }
        testRegistry.appendToClusterArgList(r);
        testRegistry.appendToClusterArgList(i);
        testRegistry.addOperatorToSuccedentReflexiveOperatorSet(14);
        // a + f = e
        s = testRegistry.registerCluster(14);
        BitSet attribute_4 = new BitSet();
        attribute_4.set(1); // succedent
        attribute_4.set(2); // ultimate
        testRegistry.updateClassAttributes(s, attribute_4);

        // a + b = d x c
        testRegistry.makeCongruent(k, p);

        if (testRegistry.checkIfProved()) {
            System.out.println("VC 8 Proved");
        } else {
            System.out.println("VC 8 Not Proved");
        }

    }

    /**
     * ROOT LABEL MAPPING 1 - a | 2 - b | 3 - + | 4 - x | 5 - <= | 8 - 8|9 - 9 | 6 - d | 7 - c | 10 - \ | m - 11 | f -
     * 12 | e - 13 | == - 14| f - 15 | d - 16
     *
     *
     */
    private static void sequentVC_3(CongruenceClassRegistry<Integer, String, String, String> testRegistry,
            BitSet attb) {
        // b
        a = testRegistry.registerCluster(2);

        testRegistry.appendToClusterArgList(a);
        // f(b)
        f = testRegistry.registerCluster(15);
        // d
        d = testRegistry.registerCluster(16);
        // f(b) = d
        testRegistry.makeCongruent(f, d);

        testRegistry.appendToClusterArgList(d);
        // f(d)
        f = testRegistry.registerCluster(15);

        // a
        b = testRegistry.registerCluster(1);
        // f(d) = a
        testRegistry.makeCongruent(b, f);

        testRegistry.addOperatorToSuccedentReflexiveOperatorSet(14);
        testRegistry.makeCongruent(b, a);

        if (testRegistry.checkIfProved()) {
            System.out.println("VC 3 Proved");
        } else {
            System.out.println("VC 3 Not Proved");
        }

    }

    /**
     * ROOT LABEL MAPPING 1 - a | 2 - b | 3 - + | 4 - x | 5 - <= | 8 - 8|9 - 9 | 6 - d | 7 - c | 10 - \ | m - 11 |
     *
     *
     *
     */
    /*
     * private static void sequentVC_4 (CongruenceClassRegistry<Integer, String, String, String> testRegistry, BitSet
     * attb){
     *
     * //a a = testRegistry.registerCluster(1); //b b = testRegistry.registerCluster(2);
     *
     * //a + b testRegistry.appendToClusterArgList(a); testRegistry.appendToClusterArgList(b); c =
     * testRegistry.registerCluster(3);
     *
     * //9 d = testRegistry.registerCluster(9);
     *
     * //9 x a testRegistry.appendToClusterArgList(a); testRegistry.appendToClusterArgList(d); e =
     * testRegistry.registerCluster(4);
     *
     *
     * //a + b <= 9 x a BitSet attb_1 = new BitSet(); attb_1.set(0);//antecedent attb_1.set(2);//ultimate
     * testRegistry.appendToClusterArgList(c); testRegistry.appendToClusterArgList(e); f =
     * testRegistry.registerCluster(5); testRegistry.updateClassAttributes(f,attb_1);
     *
     * testRegistry.appendToClusterArgList(a); testRegistry.appendToClusterArgList(b);
     * if(testRegistry.checkIfRegistered(3)){ h = testRegistry.getAccessorFor(3); }
     *
     * //8 g = testRegistry.registerCluster(8);
     *
     * if(!testRegistry.areClassesCongruent(h, g)){ //a + b = 8 testRegistry.makeCongruent(h,g); }
     *
     * //8 testRegistry.appendToClusterArgList(g); testRegistry.appendToClusterArgList(e);
     * if(testRegistry.checkIfRegistered(5)){ j = testRegistry.getAccessorFor(5); } BitSet attb_2 = new BitSet();
     * attb_2.set(1); attb_2.set(2); testRegistry.updateClassAttributes(j, attb_2);
     *
     * if(testRegistry.checkIfProved()){ System.out.println("VC Proved"); }else{ System.out.println("VC Unproved"); } }
     */

}
