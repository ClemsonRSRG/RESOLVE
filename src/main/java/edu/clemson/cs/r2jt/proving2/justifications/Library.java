/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.justifications;

import edu.clemson.cs.r2jt.mathtype.TheoremEntry;

/**
 *
 * @author hamptos
 */
public class Library implements Justification {

    private final TheoremEntry myEntry;

    public Library(TheoremEntry e) {
        myEntry = e;
    }
}
