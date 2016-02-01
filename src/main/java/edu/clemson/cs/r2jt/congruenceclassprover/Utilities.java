package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PSymbol;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

import java.util.ArrayList;

/**
 * Created by Mike on 2/1/2016.
 */
public class Utilities {
    final static boolean impliesToAndEq = true;

    public static PExp replacePExp(PExp p, TypeGraph g) {
        ArrayList<PExp> argList = new ArrayList<PExp>();
        for (PExp pa : p.getSubExpressions()) {
            argList.add(replacePExp(pa, g));
        }
        String pTop = p.getTopLevelOperation();
        if (pTop.equals("/=")) {
            PSymbol eqExp = new PSymbol(g.BOOLEAN, null, "=", argList);
            argList.clear();
            argList.add(eqExp);
            PSymbol notEqExp = new PSymbol(g.BOOLEAN, null, "not", argList);
            return notEqExp;
        }
        /*else if(pTop.equals("implies")){
            if(impliesToAndEq){
                PSymbol pAndq = new PSymbol(g.BOOLEAN, null, "and", argList);
                PSymbol antc = (PSymbol)argList.get(0);
                argList.clear();
                argList.add(pAndq);
                argList.add(antc);
                PSymbol pAndqeqP = new PSymbol(g.BOOLEAN, null, "=", argList);
                return pAndqeqP;

            }

        }*/
        return new PSymbol(p.getType(), null, p.getTopLevelOperation(), argList, ((PSymbol)p).quantification);
    }
}

        /*if (pTop.equals("implies")) {
            ArrayList<PExp> args = new ArrayList<PExp>();
            args.add(arg1);
            args.add(arg2);
            if(impliesToAndEq) {
                PSymbol pAndq = new PSymbol(g.BOOLEAN, null, "and", args);
                args.clear();
                args.add(pAndq);
                args.add(arg1);
                PSymbol pAndqeqP = new PSymbol(g.BOOLEAN, null, "=", args);
                return pAndqeqP;
            } else {
                PSymbol pOrQ = new PSymbol(g.BOOLEAN,null,"or", args);
                args.clear();
                args.add(pOrQ);
                args.add(arg2);
                PSymbol pOrQeqQ = new PSymbol(g.BOOLEAN,null,"=",args);
                return pOrQeqQ;
            }
*/