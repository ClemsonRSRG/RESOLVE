package edu.clemson.cs.r2jt.proving2;

import java.util.HashMap;
import java.util.Map;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.VarExp;
import edu.clemson.cs.r2jt.proving.absyn.BindingException;
import edu.clemson.cs.r2jt.proving.absyn.PExp;

public class Antecedent extends ImmutableConjuncts {

    public static final Antecedent EMPTY = new Antecedent();

    public Antecedent(Exp e) {
        super(e);
    }

    public Antecedent(PExp e) {
        super(e);
    }

    public Antecedent(Iterable<PExp> i) {
        super(i);
    }

    private Antecedent() {
        super();
    }
}
