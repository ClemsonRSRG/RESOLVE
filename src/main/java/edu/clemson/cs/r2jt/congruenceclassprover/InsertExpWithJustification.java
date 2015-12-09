/**
 * InsertExpWithJustification.java
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
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;

/**
 *
 * @author mike
 */
public class InsertExpWithJustification {

    public PExp m_PExp;
    public String m_Justification;
    public int m_symCnt;

    public InsertExpWithJustification(PExp p, String j, int symCnt) {
        m_PExp = p;
        m_Justification = j;
        m_symCnt = symCnt;
    }

    public String toString() {
        return m_Justification + "\n\t" + m_PExp.toString();
    }
}
