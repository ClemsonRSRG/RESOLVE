/**
 * MathSymbolQuery.java
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
package edu.clemson.cs.r2jt.typeandpopulate2.query;

import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.misc.Utils.Mapping;
import edu.clemson.cs.r2jt.typeandpopulate2.PossiblyQualifiedPath;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.MathSymbolEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.searchers.NameSearcher;
import org.antlr.v4.runtime.Token;

public class MathSymbolQuery
        extends
            ResultProcessingQuery<SymbolTableEntry, MathSymbolEntry> {

    public MathSymbolQuery(Token qualifier, Token name) {
        this(qualifier, name.getText(), name);
    }

    public MathSymbolQuery(Token qualifier, String name, Token nameLoc) {
        super(new BaseSymbolQuery<SymbolTableEntry>(new PossiblyQualifiedPath(
                qualifier, ImportStrategy.IMPORT_NAMED,
                FacilityStrategy.FACILITY_IGNORE, true), new NameSearcher(name,
                true)), new MapToMathSymbol(nameLoc));
    }

    private static class MapToMathSymbol
            implements
                Mapping<SymbolTableEntry, MathSymbolEntry> {

        private final Token myNameLocation;

        public MapToMathSymbol(Token t) {
            myNameLocation = t;
        }

        @Override
        public MathSymbolEntry map(SymbolTableEntry input) {
            return input.toMathSymbolEntry(myNameLocation);
        }
    }
}
