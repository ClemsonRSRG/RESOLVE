/*
 * Utilities.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.vcgeneration;

import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.TypeRepresentationEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.SymbolNotOfKindTypeException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.query.NameQuery;
import edu.clemson.cs.rsrg.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerVisitor;
import java.util.List;

/**
 * <p>This class contains a bunch of utilities methods used by the {@link VCGenerator}
 * and all of its associated {@link TreeWalkerVisitor TreeWalkerVisitors}.</p>
 *
 * @author Yu-Shan Sun
 * @version 2.0
 */
public class Utilities {

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method returns a newly created {@link VarExp}
     * with the {@link PosSymbol} and math type provided.</p>
     *
     * @param loc New {@link VarExp VarExp's} {@link Location}.
     * @param qualifier New {@link VarExp VarExp's} qualifier.
     * @param name New {@link VarExp VarExp's} name.
     * @param type New {@link VarExp VarExp's} math type.
     * @param typeValue New {@link VarExp VarExp's} math type value.
     *
     * @return The new <code>VarExp</code>.
     */
    public static VarExp createVarExp(Location loc, PosSymbol qualifier,
            PosSymbol name, MTType type, MTType typeValue) {
        // Create the VarExp
        VarExp exp = new VarExp(loc, qualifier, name);
        exp.setMathType(type);
        exp.setMathTypeValue(typeValue);

        return exp;
    }

    /**
     * <p>An helper method that throws the appropriate message that
     * the symbol table entry that we found isn't a type.</p>
     *
     * @param entry A symbol table entry.
     * @param loc Location where this entry was found.
     */
    public static void notAType(SymbolTableEntry entry, Location loc) {
        throw new SourceErrorException("[VCGenerator] "
                + entry.getSourceModuleIdentifier()
                        .fullyQualifiedRepresentation(entry.getName())
                + " is not known to be a type.", loc);
    }

    /**
     * <p>An helper method that throws the appropriate no module found
     * message.</p>
     *
     * @param loc Location where this module name was found.
     */
    public static void noSuchModule(Location loc) {
        throw new SourceErrorException(
                "[VCGenerator] Module does not exist or is not in scope.", loc);
    }

    /**
     * <p>An helper method that throws the appropriate no symbol found
     * message.</p>
     *
     * @param qualifier The symbol's qualifier.
     * @param symbolName The symbol's name.
     * @param loc Location where this symbol was found.
     */
    public static void noSuchSymbol(PosSymbol qualifier, String symbolName,
            Location loc) {
        String message;

        if (qualifier == null) {
            message = "[VCGenerator] No such symbol: " + symbolName;
        }
        else {
            message =
                    "[VCGenerator] No such symbol in module: "
                            + qualifier.getName() + "." + symbolName;
        }

        throw new SourceErrorException(message, loc);
    }

    /**
     * <p>An helper method that throws the appropriate raw type
     * not handled message.</p>
     *
     * @param ty A raw type.
     * @param loc Location where this raw type was found.
     */
    public static void tyNotHandled(Ty ty, Location loc) {
        throw new SourceErrorException("[VCGenerator] Ty not handled: "
                + ty.toString(), loc);
    }

    // ===========================================================
    // Package Private Methods
    // ===========================================================

    /**
     * <p>Returns the math type for "Z".</p>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param scope The module scope to start our search.
     *
     *
     * @return The <code>MTType</code> for "Z".
     */
    static MTType getMathTypeZ(Location loc, ModuleScope scope) {
        // Locate "Z" (Integer)
        MathSymbolEntry mse = searchMathSymbol(loc, "Z", scope);
        MTType Z = null;
        try {
            Z = mse.getTypeValue();
        }
        catch (SymbolNotOfKindTypeException e) {
            notAType(mse, loc);
        }

        return Z;
    }

    /**
     * <p>Given a math symbol name, locate and return
     * the {@link MathSymbolEntry} stored in the
     * symbol table.</p>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param name The string name of the math symbol.
     * @param scope The module scope to start our search.
     *
     * @return A {@link MathSymbolEntry} from the
     *         symbol table.
     */
    static MathSymbolEntry searchMathSymbol(Location loc, String name,
            ModuleScope scope) {
        // Query for the corresponding math symbol
        MathSymbolEntry ms = null;
        try {
            ms =
                    scope.queryForOne(
                            new UnqualifiedNameQuery(name,
                                    ImportStrategy.IMPORT_RECURSIVE,
                                    FacilityStrategy.FACILITY_IGNORE, true,
                                    true)).toMathSymbolEntry(loc);
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, name, loc);
        }
        catch (DuplicateSymbolException dse) {
            //This should be caught earlier, when the duplicate symbol is
            //created
            throw new RuntimeException(dse);
        }

        return ms;
    }

    /**
     * <p>Given the name of the type locate and return
     * the {@link SymbolTableEntry} stored in the
     * symbol table.</p>
     *
     * @param loc The location in the AST that we are
     *            currently visiting.
     * @param qualifier The qualifier of the type.
     * @param name The name of the type.
     * @param scope The module scope to start our search.
     *
     * @return A {@link SymbolTableEntry} from the
     *         symbol table.
     */
    static SymbolTableEntry searchProgramType(Location loc,
            PosSymbol qualifier, PosSymbol name, ModuleScope scope) {
        SymbolTableEntry retEntry = null;

        List<SymbolTableEntry> entries =
                scope.query(new NameQuery(qualifier, name,
                        ImportStrategy.IMPORT_NAMED,
                        FacilityStrategy.FACILITY_INSTANTIATE, true));

        if (entries.size() == 0) {
            noSuchSymbol(qualifier, name.getName(), loc);
        }
        else if (entries.size() == 1) {
            retEntry = entries.get(0).toProgramTypeEntry(loc);
        }
        else {
            // When we have more than one, it means that we have a
            // type representation. In that case, we just need the
            // type representation.
            for (int i = 0; i < entries.size() && retEntry == null; i++) {
                SymbolTableEntry ste = entries.get(i);
                if (ste instanceof TypeRepresentationEntry) {
                    retEntry = ste.toTypeRepresentationEntry(loc);
                }
            }

            // Throw duplicate symbol error if we don't have a type
            // representation
            if (retEntry == null) {
                //This should be caught earlier, when the duplicate type is
                //created
                throw new RuntimeException();
            }
        }

        return retEntry;
    }

}
