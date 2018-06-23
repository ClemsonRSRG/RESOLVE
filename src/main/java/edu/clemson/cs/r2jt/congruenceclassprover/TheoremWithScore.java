/*
 * TheoremWithScore.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.absyn.StringExp;

/**
 * Created by nabilkabbani on 12/10/14.
 */
public class TheoremWithScore implements Comparable<TheoremWithScore> {

    TheoremCongruenceClosureImpl m_theorem;
    Integer m_score;

    public TheoremWithScore(TheoremCongruenceClosureImpl t) {
        m_theorem = t;
        m_score = 1;
    }

    @Override
    public int compareTo(TheoremWithScore o) {
        return m_score - o.m_score;
    }

}
