/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.treewalk;

import edu.clemson.cs.r2jt.absyn.VariableNameExp;

/**
 *
 * @author Blair
 */
public class TestVisitor extends TreeWalkerVisitor {

    @Override
    public void preVariableNameExp(VariableNameExp data) {
        System.out.println(data.asString(0, 0));
    }

}
