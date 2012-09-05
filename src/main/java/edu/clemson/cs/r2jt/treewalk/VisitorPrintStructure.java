package edu.clemson.cs.r2jt.treewalk;

import edu.clemson.cs.r2jt.absyn.*;

public class VisitorPrintStructure extends TreeWalkerVisitor {

    private int indent = 0;

    @Override
    public void preAny(ResolveConceptualElement data) {
        String className = data.getClass().getSimpleName();
        for (int i = 0; i < indent; ++i) {
            System.out.print("  ");
        }
        System.out.print(className);
        if (data instanceof VarExp) {
            System.out.print(" (" + ((VarExp) data).getName().toString() + ")");
        }
        else if (data instanceof InfixExp) {
            System.out.print(" (" + ((InfixExp) data).getOpName().toString()
                    + ")");
        }
        else if (data instanceof OutfixExp) {
            System.out.print(" (" + ((OutfixExp) data).getOperatorAsString()
                    + ")");
        }
        System.out.println();
        ++indent;
    }

    @Override
    public void postAny(ResolveConceptualElement data) {
        --indent;
    }
}
