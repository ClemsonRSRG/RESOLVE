/**
 * PopulatingVisitor.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate2;

import edu.clemson.cs.r2jt.absynnew.ImportCollectionAST;
import edu.clemson.cs.r2jt.absynnew.ModuleAST;
import edu.clemson.cs.r2jt.absynnew.TreeWalkerVisitor;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;
import org.antlr.v4.runtime.Token;

import java.util.Deque;
import java.util.LinkedList;

public class PopulatingVisitor extends TreeWalkerVisitor {

    private static final boolean PRINT_DEBUG = false;

    private MathSymbolTableBuilder myBuilder;
    private ModuleScopeBuilder myCurModuleScope;

    /**
     * <p>Any quantification-introducing syntactic node (like, e.g., a
     * QuantExp), introduces a level to this stack to reflect the quantification
     * that should be applied to named variables as they are encountered.  Note
     * that this may change as the children of the node are processed--for
     * example, MathVarDecs found in the declaration portion of a QuantExp
     * should have quantification (universal or existential) applied, while
     * those found in the body of the QuantExp should have no quantification
     * (unless there is an embedded QuantExp).  In this case, QuantExp should
     * <em>not</em> remove its layer, but rather change it to
     * MathSymbolTableEntry.None.</p>
     *
     * <p>This stack is never empty, but rather the bottom layer is always
     * MathSymbolTableEntry.None.</p>
     */
    private Deque<SymbolTableEntry.Quantification> myActiveQuantifications =
            new LinkedList<SymbolTableEntry.Quantification>();

    private final TypeGraph myTypeGraph;

    public PopulatingVisitor(MathSymbolTableBuilder builder) {
        myActiveQuantifications.push(SymbolTableEntry.Quantification.NONE);

        myTypeGraph = builder.getTypeGraph();
        myBuilder = builder;
    }

    public TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    //-------------------------------------------------------------------
    //   Visitor methods
    //-------------------------------------------------------------------

    @Override
    public void preModuleAST(ModuleAST e) {
        PopulatingVisitor.emitDebug("----------------------\nModule: "
                + e.getName().getText() + "\n----------------------");
        myCurModuleScope = myBuilder.startModuleScope(e);
    }

    @Override
    public void postImportCollectionAST(ImportCollectionAST e) {
        for (Token importRequest : e
                .getImportsExcluding(ImportCollectionAST.ImportType.EXTERNAL)) {
            myCurModuleScope.addImport(new ModuleIdentifier(importRequest));
        }
    }

    @Override
    public void postModuleAST(ModuleAST e) {
        myBuilder.endScope();
        PopulatingVisitor
                .emitDebug("END MATH POPULATOR\n----------------------\n");
    }

    public static void emitDebug(String msg) {
        if (PRINT_DEBUG) {
            System.out.println(msg);
        }
    }
}
