package edu.clemson.cs.r2jt.treewalk;

import edu.clemson.cs.r2jt.absyn.*;

public class VisitorPrintStructure extends TreeWalkerVisitor {

    private int indent = 0;
    private final boolean showIdentifiers = false;

    @Override
    public void preAny(ResolveConceptualElement data) {
        String className;
        if (data instanceof VirtualListNode) {
            className = ((VirtualListNode) data).getNodeName() + " [List]";
        }
        else {
            className = data.getClass().getSimpleName();
        }
        for (int i = 0; i < indent; ++i) {
            System.out.print("  ");
        }
        System.out.print(className);

        if (showIdentifiers) {
            if (data instanceof VarExp) {
                System.out.print(" (" + ((VarExp) data).getName().toString()
                        + ")");
            }
            else if (data instanceof InfixExp) {
                System.out.print(" ("
                        + ((InfixExp) data).getOpName().toString() + ")");
            }
            else if (data instanceof OutfixExp) {
                System.out.print(" ("
                        + ((OutfixExp) data).getOperatorAsString() + ")");
            }
        }

        System.out.println();
        ++indent;
    }

    @Override
    public void postAny(ResolveConceptualElement data) {
        --indent;
    }

	@Override
	public void midFacilityOperationDecStatements(FacilityOperationDec node, Statement previous, Statement next) {
		if (previous == null) {
			System.out.println("Beginning of statements.\nNext statement: " + next.toString(0));
		} else if (next == null) {
			System.out.println("Previous statement: " + previous.toString(0) + "\nEnd of statements.");
		} else {
			System.out.println("Previous statement: " + previous.toString(0) + "\nNext statement: " + next.toString(0) + ")");
		}
	}
	
	

    
}
