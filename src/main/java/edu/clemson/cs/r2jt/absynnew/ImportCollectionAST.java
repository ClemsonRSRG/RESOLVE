/**
 * ImportCollectionAST.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absynnew;

import edu.clemson.cs.r2jt.parsing.ResolveParser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

/**
 * An {@code ImportCollectionAST} classifies and maintains a complete
 * collection of module imports ranging from implicitly referenced/imported
 * modules, to those explicitly requested via the <tt>uses</tt> list.
 */
public class ImportCollectionAST extends ResolveAST {

    public static enum ImportType {
        EXPLICIT, IMPLICIT, EXTERNAL
    }

    protected final Map<ImportType, Set<Token>> myImports;

    private ImportCollectionAST(ImportCollectionBuilder builder) {
        super(builder.getStart(), builder.getStop());
        myImports = builder.usesItems;
    }

    /**
     * Retrieves a set of all imports except those of {@code type}.
     * 
     * @param type Any types we would like to filter/exclude.
     * @return A set of {@link Token} filtered by {@code type}.
     */
    public Set<Token> getImportsExcluding(ImportType... type) {
        Set<Token> result = new HashSet<Token>();
        List<ImportType> typesToExclude = Arrays.asList(type);

        for (ImportType s : myImports.keySet()) {
            if (!typesToExclude.contains(s)) {
                result.addAll(myImports.get(s));
            }
        }
        return result;
    }

    public Set<Token> getImportsOfType(ImportType type) {
        return myImports.get(type);
    }

    /**
     * Returns {@code true} iff the set of {@code type} imports contains
     * {@code t}; {@code false}.
     * otherwise.
     * @param type  A {@link ImportType}.
     * @param t     A name token.
     *
     * @return      true if t is in the set of type, false otherwise.
     */
    public boolean inCategory(ImportType type, Token t) {
        return myImports.get(type).contains(t);
    }

    /**
     * Returns all imports, regardless of their <code>ImportType</code>, in a
     * single set.
     * 
     * @return All imports.
     */
    public Set<Token> getImports() {
        Set<Token> aggregateImports = new HashSet<Token>();

        for (Set<Token> typeSet : myImports.values()) {
            aggregateImports.addAll(typeSet);
        }
        return aggregateImports;
    }

    /**
     * Useful for collecting all imports over the course of the
     * construction of a given {@link ModuleAST}.
     */
    public static class ImportCollectionBuilder
            extends
                AbstractNodeBuilder<ImportCollectionAST> {

        protected final Map<ImportType, Set<Token>> usesItems =
                new HashMap<ImportType, Set<Token>>();

        public ImportCollectionBuilder() {
            this(null, null);
        }

        public ImportCollectionBuilder(Token start, Token stop) {
            super(start, stop);

            //Initialize the uses/import map to empty sets
            for (int i = 0; i < ImportType.values().length; i++) {
                ImportType curType = ImportType.values()[i];
                if (usesItems.get(curType) == null) {
                    usesItems.put(curType, new HashSet<Token>());
                }
            }
        }

        public ImportCollectionBuilder imports(
                @NotNull ResolveParser.FacilityDeclContext ctx) {
            imports(ImportType.IMPLICIT, ctx.concept);
            ImportType specType =
                    (ctx.externally != null) ? ImportType.EXTERNAL
                            : ImportType.IMPLICIT;
            imports(specType, ctx.impl);
            //Todo: External keyword for enhancement pairs.
            for (ResolveParser.EnhancementPairDeclContext enhancement : ctx
                    .enhancementPairDecl()) {
                imports(ImportType.IMPLICIT, ctx.concept);
                imports(specType, ctx.impl);
            }
            return this;
        }

        public ImportCollectionBuilder imports(ImportType type, Token... t) {
            addTokenSet(type, Arrays.asList(t));
            return this;
        }

        public ImportCollectionBuilder imports(ImportType type,
                TerminalNode... t) {
            imports(type, Arrays.asList(t));
            return this;
        }

        public ImportCollectionBuilder imports(ImportType type,
                List<TerminalNode> terminals) {
            List<Token> convertedTerms = new ArrayList<Token>();
            for (TerminalNode t : terminals) {
                convertedTerms.add(t.getSymbol());
            }
            addTokenSet(type, convertedTerms);
            return this;
        }

        private void addTokenSet(ImportType type,
                Collection<? extends Token> newToks) {
            Set<Token> tokSet = usesItems.get(type);
            if (tokSet == null) {
                tokSet = new HashSet<Token>();
            }
            tokSet.addAll(newToks);
        }

        @Override
        public ImportCollectionAST build() {
            return new ImportCollectionAST(this);
        }
    }
}