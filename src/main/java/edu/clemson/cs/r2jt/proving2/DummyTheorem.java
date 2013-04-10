/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Collections;
import java.util.List;

/**
 * <p>Simply provides a way to construct a theorem that will return a particular
 * transformation.</p>
 */
public class DummyTheorem extends Theorem {
    
    private final Transformation myTransformation;
    
    public DummyTheorem(Transformation t) {
        super(null, null);
        
        myTransformation = t;
    }
    
    @Override
    public List<Transformation> getTransformations() {
        return Collections.singletonList(myTransformation);
    }
    
    @Override
    public String toString() {
        return "" + myTransformation;
    }
}
