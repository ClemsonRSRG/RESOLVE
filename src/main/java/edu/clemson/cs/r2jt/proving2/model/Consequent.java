/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.model;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import java.util.Collections;

/**
 *
 * @author hamptos
 */
public class Consequent implements Conjunct {

    private PExp myExp;

    Consequent(PExp exp) {
        this.myExp = exp;
    }

    @Override
    public Site toSite(PerVCProverModel m) {
        return new Site(m, this, Collections.EMPTY_LIST, myExp);
    }

    @Override
    public PExp getExpression() {
        return myExp;
    }

    @Override
    public void setExpression(PExp newValue) {
        myExp = newValue;
    }

    @Override
    public String toString() {
        return "" + myExp;
    }

    @Override
    public boolean editable() {
        return true;
    }

    @Override
    public boolean libraryTheorem() {
        return false;
    }
}
