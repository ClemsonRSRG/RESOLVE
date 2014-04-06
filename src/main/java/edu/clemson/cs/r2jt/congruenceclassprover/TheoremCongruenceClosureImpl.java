/**
 * Theorem.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.justifications.Justification;
import edu.clemson.cs.r2jt.proving2.justifications.Library;

/**
 * Created by mike on 4/3/2014.
 */
public class TheoremCongruenceClosureImpl {
    private final PExp m_lhs;
    private final PExp m_rhs;
    private final boolean isEquality;
    private Registry m_theoremRegistry;
    private ConjunctionOfNormalizedAtomicExpressions m_matchConj;
    private ConjunctionOfNormalizedAtomicExpressions m_replConj;

    public TheoremCongruenceClosureImpl(PExp p){
        m_lhs = p.getSubExpressions().get(0);
        m_rhs = p.getSubExpressions().get(1);
        isEquality = p.getTopLevelOperation().equals("=");
        m_theoremRegistry = new Registry();
        m_matchConj = new ConjunctionOfNormalizedAtomicExpressions(m_theoremRegistry);
        m_replConj = new ConjunctionOfNormalizedAtomicExpressions(m_theoremRegistry);
        
    }
    
    // this is mostly temporary
    public static boolean canProcess(PExp p){
        // 2 arguments
        // = or implies at top level
        if(p.getTopLevelOperation().equals("=") || p.getTopLevelOperation().equals("implies"))
            return true;
        return false;
    }
    /*
    These are apparently definitions. If blindly added they can greatly increase
    the memory usage of the VC.  If incorporated into the theorem, they will
    not be added to the VC unless a match is made in the larger context.
    Can't process (|(S o <E>)| > 0)
Can't process (|S| < |(<E> o S)|)
Can't process (|S| < |(S o <E>)|)
Can't process Is_Permutation(S, S)
Can't process Is_Permutation((S o T), (T o S))
Can't process Is_Universally_Related(Empty_String, S, f)
Can't process Is_Universally_Related(S, Empty_String, f)
Can't process (0 < 1)
Can't process (1 > 0)
Can't process ((i - 1) < i)
Can't process (i <= i)
    */
}
