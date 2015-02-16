/**
 * ScopeBuilder.java
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

import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.MathSymbolEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.ProgramVariableEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>A <code>ScopeBuilder</code> is a working, mutable realization of 
 * <code>Scope</code>.</p>
 * 
 * <p>Note that <code>ScopeBuilder</code> has no public constructor.  
 * <code>ScopeBuilders</code>s are acquired through calls to some of the methods
 * of {@link edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTableBuilder MathSymbolTableBuilder}.</p>
 */
public class ScopeBuilder extends SyntacticScope {

    protected final List<ScopeBuilder> myChildren =
            new LinkedList<ScopeBuilder>();

    private final TypeGraph myTypeGraph;

    ScopeBuilder(MathSymbolTableBuilder b, TypeGraph g,
            ResolveAST definingElement, Scope parent,
            ModuleIdentifier enclosingModule) {

        super(b, definingElement, parent, enclosingModule,
                new BaseSymbolTable());

        myTypeGraph = g;
    }

    void setParent(Scope parent) {
        myParent = parent;
    }

    void addChild(ScopeBuilder b) {
        myChildren.add(b);
    }

    List<ScopeBuilder> children() {
        return new LinkedList<ScopeBuilder>(myChildren);
    }

    FinalizedScope seal(MathSymbolTable finalTable) {
        return new FinalizedScope(finalTable, myDefiningElement, myRootModule,
                myParent, myBindings);
    }

    public ProgramVariableEntry addProgramVariable(String name,
            ResolveAST definingElement, PTType type)
            throws DuplicateSymbolException {

        sanityCheckBindArguments(name, definingElement, type);

        ProgramVariableEntry entry =
                new ProgramVariableEntry(name, definingElement, myRootModule,
                        type);

        myBindings.put(name, entry);

        return entry;
    }

    /**
     * <p>Modifies the current working scope to add a new binding for a
     * symbol with an unqualified name, <code>name</code>, defined by the AST
     * node <code>definingElement</code> and of type <code>type</code>.</p>
     *
     * @param name The unqualified name of the symbol.
     * @param definingElement The AST Node that introduced the symbol.
     * @param type The declared type of the symbol.
     * @param typeValue The type assigned to the symbol (can be null).
     *
     * @throws DuplicateSymbolException If such a symbol is already defined
     *             directly in the scope represented by this
     *             <code>ScopeBuilder</code>.  Note that this exception is not
     *             thrown if the symbol is defined in a parent scope or an
     *             imported module.
     */
    public MathSymbolEntry addBinding(String name,
            SymbolTableEntry.Quantification q, ResolveAST definingElement,
            MTType type, MTType typeValue, Map<String, MTType> schematicTypes,
            Map<String, MTType> genericsInDefiningContext)
            throws DuplicateSymbolException {

        sanityCheckBindArguments(name, definingElement, type);

        MathSymbolEntry entry =
                new MathSymbolEntry(myTypeGraph, name, q, definingElement,
                        type, typeValue, schematicTypes,
                        genericsInDefiningContext, myRootModule);

        myBindings.put(name, entry);

        return entry;
    }

    public MathSymbolEntry addBinding(String name,
            SymbolTableEntry.Quantification q, ResolveAST definingElement,
            MTType type) throws DuplicateSymbolException {
        return addBinding(name, q, definingElement, type, null, null, null);
    }

    public MathSymbolEntry addBinding(String name, ResolveAST definingElement,
            MTType type, MTType typeValue) throws DuplicateSymbolException {

        return addBinding(name, SymbolTableEntry.Quantification.NONE,
                definingElement, type, typeValue, null, null);
    }

    public MathSymbolEntry addBinding(String name, ResolveAST definingElement,
            MTType type) throws DuplicateSymbolException {

        return addBinding(name, SymbolTableEntry.Quantification.NONE,
                definingElement, type);
    }

    private void sanityCheckBindArguments(String name,
            ResolveAST definingElement, Object type)
            throws DuplicateSymbolException {

        SymbolTableEntry curLocalEntry = myBindings.get(name);
        if (curLocalEntry != null) {
            throw new DuplicateSymbolException(curLocalEntry);
        }

        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("symbol table entry name must "
                    + "be non-null and contain at least one character");
        }

        if (type == null) {
            throw new IllegalArgumentException("symbol table entry type must "
                    + "be non-null.");
        }
    }
}
