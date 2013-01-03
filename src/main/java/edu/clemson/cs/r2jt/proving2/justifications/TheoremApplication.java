/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.justifications;

import edu.clemson.cs.r2jt.proving2.transformations.Transformation;

/**
 *
 * @author hamptos
 */
public class TheoremApplication implements Justification {

    private final Transformation myTransformation;

    public TheoremApplication(Transformation t) {
        myTransformation = t;
    }
}
