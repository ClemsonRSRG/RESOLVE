/**
 * VC.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.clemson.cs.r2jt.absyn.LambdaExp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExpVisitor;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PLambda;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

/**
 * <p>Represents an immutable <em>verification condition</em>, which takes the 
 * form of a mathematical implication.</p>
 */
public class VC {

    /**
     * <p>Name is a human-readable name for the VC used for debugging purposes.
     * </p>
     */
    private final String myName;

    /**
     * <p>myDerivedFlag is set to true to indicate that this VC is not the
     * original version of the VC with myName--rather it was derived from a
     * VC named myName (or derived from a VC derived from a VC named myName)</p>
     */
    private final boolean myDerivedFlag;

    private Antecedent myAntecedent;
    private Consequent myConsequent;

    private java.util.HashMap<PLambda,String> m_liftedLamdas;
    public java.util.List<PSymbol> m_liftedLambdaPredicates;
    private int m_lamdaTag = 0;
    private TypeGraph m_typegraph;
    private VC liftedCopy;

    public VC(String name, Antecedent antecedent, Consequent consequent) {
        this(name, antecedent, consequent, false);
    }

    public VC(String name, Antecedent antecedent, Consequent consequent,
            boolean derived) {

        myName = name;
        myAntecedent = antecedent;
        myConsequent = consequent;
        myDerivedFlag = derived;
        m_liftedLamdas = new HashMap<PLambda, String>();
        m_liftedLambdaPredicates = new ArrayList<PSymbol>();
    }

    public String getName() {
        String retval = myName;

        if (myDerivedFlag) {
            retval += " (modified)";
        }

        return retval;
    }
    public void liftLambdas(TypeGraph g){
        m_typegraph = g;
        ArrayList<PExp> newConjuncts = new ArrayList<PExp>();
        java.util.List<PExp> a_p = myAntecedent.getMutableCopy();
        for(PExp p : a_p){
            newConjuncts.add(recursiveLift(p));
        }
        myAntecedent = new Antecedent(newConjuncts);

        newConjuncts.clear();
        a_p = myConsequent.getMutableCopy();
        for(PExp p : a_p){
            newConjuncts.add(recursiveLift(p));
        }
        myConsequent = new Consequent(newConjuncts);
        for(PLambda p : m_liftedLamdas.keySet()){
            String name = m_liftedLamdas.get(p);
            PExp body = p.getBody();
            PSymbol lhs = new PSymbol(p.getType(),p.getTypeValue(),name,p.getParameters());
            ArrayList<PExp> args = new ArrayList<PExp>();
            args.add(lhs);
            args.add(body);
            m_liftedLambdaPredicates.add(new PSymbol(m_typegraph.BOOLEAN,null,"=",args));
        }
    }

    private PExp recursiveLift(PExp p){
        ArrayList<PExp> newArgList = new ArrayList<PExp>();
        for(PExp p_s : p.getSubExpressions()){
            newArgList.add(recursiveLift(p_s));
        }
        if(p instanceof PLambda){
            String lname = "";
            if(!m_liftedLamdas.containsKey(p)) {
                lname = "lambda" + m_lamdaTag++;
                m_liftedLamdas.put((PLambda)p,lname);
            }
            else{
                lname = m_liftedLamdas.get(p);
            }
            return new PSymbol(p.getType(),p.getTypeValue(),lname);

        }
        return new PSymbol(p.getType(),p.getTypeValue(),p.getTopLevelOperation(),newArgList);
    }
    public String getSourceName() {
        return myName;
    }

    public Antecedent getAntecedent() {
        return myAntecedent;
    }

    public Consequent getConsequent() {
        return myConsequent;
    }


    @Override
    public String toString() {

        String retval =
                "========== " + getName() + " ==========\n" + myAntecedent
                        + "  -->\n" + myConsequent;

        if(!m_liftedLambdaPredicates.isEmpty()){
            retval += "lifted lambda predicates:\n";
            for(PExp p: m_liftedLambdaPredicates){
                retval += p.toString() + "\n";
            }
        }
        return retval;
    }

    public void processStringRepresentation(PExpVisitor visitor, Appendable a) {

        try {
            a.append("========== " + getName() + " ==========\n");
            myAntecedent.processStringRepresentation(visitor, a);
            a.append("  -->\n");
            myConsequent.processStringRepresentation(visitor, a);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toSMTLIB() {
        String rString = "";
        Map<String, MTType> constantTypes = new HashMap<String, MTType>();
        return rString;
    }
}
