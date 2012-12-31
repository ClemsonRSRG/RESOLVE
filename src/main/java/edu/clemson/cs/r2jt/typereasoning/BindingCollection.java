package edu.clemson.cs.r2jt.typereasoning;

import java.util.HashMap;

import edu.clemson.cs.r2jt.absyn.VarExp;

public class BindingCollection {

    private HashMap<String, String> myBindings = new HashMap<String, String>();
    private int varCounter = 0;

    public void addBinding(String var1, String var2) {
        myBindings.put(var1, var2);
    }

    public String getBinding(String var) {
        String boundVar = myBindings.get(var);
        if (boundVar == null) {
            boundVar = var;
        }
        return boundVar;
    }
}
