package edu.clemson.rsrg.nProver;

import edu.clemson.rsrg.nProver.registry.CongruenceClassRegistry;
import edu.clemson.rsrg.nProver.registry.CongruenceCluster;

import java.util.*;

public class CCRegApp {

    /**
     * ROOT LABEL MAPPING
     * 1 - a | 2 - b | 3 - + | 4 - x | 5 - <= | 8 - 8|9 - 9 | 6 - d | 7 - c | 10 - \ |
     *
     *Bit set |A|S|U|
     *
     */
    private static int a, b, c, d, e, f, g, h, j, i, k, l, m, n, o, p, q, r, s, t;
    public static void main (String[] args){
        BitSet attb = new BitSet();


        boolean check = false;
        CongruenceClassRegistry<Integer, String, String, String> testRegistry = new CongruenceClassRegistry<>(100, 100, 100, 100);

        //sequentVC_3(testRegistry, attb);

        sequentVC_4(testRegistry, attb);


/*
        //printing cluster argument array
        System.out.println("Argument Array");
        for (int i = 1; i < 17; i++){//17
            System.out.print("|" + testRegistry.getClusterArgArray()[i].getNextClusterArg()+ "|");
            System.out.print("|" + testRegistry.getClusterArgArray()[i].getPrevClusterArg()+ "|");
            System.out.print("|" + testRegistry.getClusterArgArray()[i].getCcNumber()+ "|");
            System.out.print("|" + testRegistry.getClusterArgArray()[i].getClusterNumber()+ "|");
            System.out.print("|" + testRegistry.getClusterArgArray()[i].getNxtIndexWithSameCCNumberInLevel()+ "|");
            System.out.println("|" + testRegistry.getClusterArgArray()[i].getAlternativeArg()+ "|");
        }

        System.out.println("Cluster Array");
        for (int i = 1; i < 9; i++){
            System.out.print("|" + testRegistry.getClusterArray()[i].getTreeNodeLabel() + "|");
            System.out.print("|" + testRegistry.getClusterArray()[i].getIndexToArgList() + "|");
            System.out.print("|" + testRegistry.getClusterArray()[i].getIndexToCongruenceClass() + "|");
            System.out.print("|" + testRegistry.getClusterArray()[i].getNextPlantationCluster() + "|");
            System.out.print("|" + testRegistry.getClusterArray()[i].getPreviousPlantationCluster() + "|");
            System.out.print("|" + testRegistry.getClusterArray()[i].getDominantCluster() + "|");
            System.out.println("|" + testRegistry.getClusterArray()[i].getNextWithSameArg() + "|");
        }

        System.out.println("Plantation Array");

        for(int i = 1; i < 9; i++){
            System.out.print("|" + testRegistry.getPlantationArray()[i].getTreeNodeLabel() + "|");
            System.out.print("|" + testRegistry.getPlantationArray()[i].getFirstPlantationCluster() + "|");
            System.out.print("|" + testRegistry.getPlantationArray()[i].getPlantationTag() + "|");
            System.out.print("|" + testRegistry.getPlantationArray()[i].getNextCCPlantation() + "|");
            System.out.print("|" + testRegistry.getPlantationArray()[i].getNextVrtyPlantation() + "|");
            System.out.println("|" + testRegistry.getPlantationArray()[i].getPrvVrtyPlantation() + "|");
        }

        System.out.println("Class Array");
        for(int i = 1; i < 9 ; i++){
            System.out.print("|" + testRegistry.getCongruenceClassArray()[i].getFirstPlantation() + "|");
            System.out.print("|" + testRegistry.getCongruenceClassArray()[i].getClassTag() + "|");
            System.out.print("|" + testRegistry.getCongruenceClassArray()[i].getLastArgStringPosition() + "|");
            System.out.println("|" + testRegistry.getCongruenceClassArray()[i].getDominantCClass() + "|");
        }

        System.out.println();

*/

    }

    /**
     * num -- 4, min_int -- 2, max_int -- 3, 0 -- 0,  1 -- 1, <= -- 5
     * @param testRegistry
     * @param attb
     */


    private static void sequentVC_4(CongruenceClassRegistry<Integer, String, String, String> testRegistry, BitSet attb){
        // 6, 9, 9, 9 for printing
        //0
        a = testRegistry.registerCluster(0);

        //num
        b = testRegistry.registerCluster(4);

        BitSet attribute_01 = new BitSet();
        attribute_01.set(0); //antecedent
        attribute_01.set(2); //ultimate

        testRegistry.appendToClusterArgList(a);
        testRegistry.appendToClusterArgList(b);
        //0 <= num
        k = testRegistry.registerCluster(5);
        testRegistry.updateClassAttributes(k,attribute_01);

        //min_int
        d = testRegistry.registerCluster(2);

        BitSet attribute_02 = new BitSet();
        attribute_02.set(0);
        attribute_02.set(2);
        testRegistry.appendToClusterArgList(d);
        testRegistry.appendToClusterArgList(b);
        //min_int <= num
        e = testRegistry.registerCluster(5);
        testRegistry.updateClassAttributes(e, attribute_02);

        //max_int
        j = testRegistry.registerCluster(3);

        BitSet attribute_03 = new BitSet();
        attribute_03.set(0); //antecedent
        attribute_03.set(2); //ultimate

        testRegistry.appendToClusterArgList(b);
        testRegistry.appendToClusterArgList(j);
        //num <= max_int
        n = testRegistry.registerCluster(5);
        testRegistry.updateClassAttributes(n,attribute_03);

        BitSet attribute_04 = new BitSet();
        attribute_04.set(0);
        attribute_04.set(2);

        testRegistry.appendToClusterArgList(d);
        testRegistry.appendToClusterArgList(a);
        //min_int <= 0
        c = testRegistry.registerCluster(7);
        testRegistry.updateClassAttributes(c, attribute_04);

        //1
        i = testRegistry.registerCluster(1);

        BitSet attribute_05 = new BitSet();
        attribute_05.set(0);
        attribute_05.set(2);
        testRegistry.appendToClusterArgList(i);
        testRegistry.appendToClusterArgList(j);

        //1 <= max_int
        h = testRegistry.registerCluster(5);
        testRegistry.updateClassAttributes(h,attribute_05);



        testRegistry.appendToClusterArgList(b);
        testRegistry.appendToClusterArgList(j);
        // num <= max_int
        if(testRegistry.checkIfRegistered(5)){
            q = testRegistry.getAccessorFor(5);
        }else {
            testRegistry.addOperatorToSuccedentReflexiveOperatorSet(5);
            q = testRegistry.registerCluster(5);
        }
        BitSet attribute_06 = new BitSet();
        attribute_06.set(1);//succedent
        attribute_06.set(2);//ultimate
        testRegistry.updateClassAttributes(q,attribute_06);

        if(testRegistry.checkIfProved()){
            System.out.println("VC 4 Proved");
        }else{
            System.out.println("VC 4 Not Proved");
        }



    }

}
