/**
 * Populator.java
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
package edu.clemson.cs.rsrg.typeandpopulate;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.ProcedureDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.init.ResolveCompiler;
import edu.clemson.cs.rsrg.init.flag.Flag;
import edu.clemson.cs.rsrg.init.flag.FlagDependencies;
import edu.clemson.cs.rsrg.misc.Utilities.Indirect;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.StatusHandler;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.treewalk.TreeWalker;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.rsrg.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.TypeFamilyEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.SymbolNotOfKindTypeException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.*;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.query.NameQuery;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScopeBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeComparison;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.*;

/**
 * <p>This class populates the symbol table and assigns mathematical types to the
 * provided RESOLVE abstract syntax tree. This visitor logic is implemented as a
 * a {@link TreeWalkerVisitor}.</p>
 *
 * @version 2.0
 */
public class Populator extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>A {@link TypeComparison} for to find exact domain match between a
     * {@link AbstractFunctionExp} and a {@link MTType}.</p>
     */
    private static final TypeComparison<AbstractFunctionExp, MTFunction> EXACT_DOMAIN_MATCH =
            new ExactDomainMatch();

    /** <p>An exact parameter {@link Comparator} for {@link MTType}.</p> */
    private static final Comparator<MTType> EXACT_PARAMETER_MATCH =
            new ExactParameterMatch();

    /**
     * <p>A {@link TypeComparison} for to find inexact domain match between a
     * {@link AbstractFunctionExp} and a {@link MTType}.</p>
     */
    private final TypeComparison<AbstractFunctionExp, MTFunction> INEXACT_DOMAIN_MATCH =
            new InexactDomainMatch();

    /**
     * <p>A {@link TypeComparison} for to find inexact parameter match between a
     * {@link Exp} and a {@link MTType}.</p>
     */
    private final TypeComparison<Exp, MTType> INEXACT_PARAMETER_MATCH =
            new InexactParameterMatch();

    /** <p>The symbol table we are currently building.</p> */
    private final MathSymbolTableBuilder myBuilder;

    /**
     * <p>The current job's compilation environment
     * that stores all necessary objects and flags.</p>
     */
    private final CompileEnvironment myCompileEnvironment;

    /** <p>The current scope for the module we are currently building.</p> */
    private ModuleScopeBuilder myCurModuleScope;

    /**
     * <p>A mapping from generic types that appear in the module to the math
     * types that bound their possible values.</p>
     */
    private final Map<String, MTType> myGenericTypes = new HashMap<>();

    /**
     * <p>This is the status handler for the RESOLVE compiler.</p>
     */
    private final StatusHandler myStatusHandler;

    /**
     * <p>When parsing a type realization declaration, this is set to the
     * entry corresponding to the conceptual declaration from the concept. When
     * not inside such a declaration, this will be null.</p>
     */
    private TypeFamilyEntry myTypeFamilyEntry;

    /**
     * <p>This is the math type graph that indicates relationship
     * between different math types.</p>
     */
    private final TypeGraph myTypeGraph;

    /**
     * <p>An helper value that helps evaluate mathematical type values.</p>
     */
    private int myTypeValueDepth = 0;

    // ===========================================================
    // Flag Strings
    // ===========================================================

    private static final String FLAG_POPULATOR_NAME = "Populator";
    private static final String FLAG_POPULATOR_DEBUG_INFO = "Populator Debug Flag";

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>Tells the compiler to print out Populator/Typegraph information messages.</p>
     */
    public static final Flag FLAG_POPULATOR_DEBUG =
            new Flag(FLAG_POPULATOR_NAME, "populatorDebug",
                    FLAG_POPULATOR_DEBUG_INFO);

    public static final void setUpFlags() {
        FlagDependencies.addImplies(FLAG_POPULATOR_DEBUG, ResolveCompiler.FLAG_DEBUG);
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * TODO: Refactor this and add JavaDoc.
     *
     * @param builder A scope builder for a symbol table.
     * @param compileEnvironment The current job's compilation environment
     *                           that stores all necessary objects and flags.
     */
    public Populator(MathSymbolTableBuilder builder, CompileEnvironment compileEnvironment) {
        //myActiveQuantifications.push(SymbolTableEntry.Quantification.NONE);
        myTypeGraph = builder.getTypeGraph();
        myBuilder = builder;
        myCompileEnvironment = compileEnvironment;
        myStatusHandler = myCompileEnvironment.getStatusHandler();
        //myFacilityQualifier = null;
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Module Declarations
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed before visiting a {@link ModuleDec}.</p>
     *
     * @param node A module declaration.
     */
    @Override
    public void preModuleDec(ModuleDec node) {
        emitDebug(node.getLocation(), "----------------------\nModule: "
                + node.getName().getName() + "\n----------------------");
        myCurModuleScope = myBuilder.startModuleScope(node);
    }

    /**
     * <p>Code that gets executed after visiting a {@link ModuleDec}.</p>
     *
     * @param node A module declaration.
     */
    @Override
    public void postModuleDec(ModuleDec node) {
        myBuilder.endScope();
        emitDebug(node.getLocation(), "END POPULATOR\n----------------------\n");
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>The type graph containing all the type relationships.</p>
     *
     * @return The type graph for the compiler.
     */
    public final TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // -----------------------------------------------------------
    // General
    // -----------------------------------------------------------

    /**
     * <p>An helper method to print debugging messages if the debug
     * flag is on.</p>
     *
     * @param l Location that generated the message.
     * @param message The message to be outputted.
     */
    private void emitDebug(Location l, String message) {
        if (myCompileEnvironment.flags.isFlagSet(FLAG_POPULATOR_DEBUG)) {
            myStatusHandler.info(l, message);
        }
    }

    // -----------------------------------------------------------
    // Math Type-Related
    // -----------------------------------------------------------

    /**
     * <p>Add a new binding for a {@link ResolveConceptualElement}.</p>
     *
     * @param name Name of the entry.
     * @param l Location where this element was found.
     * @param q A quantifier.
     * @param definingElement The object that is receiving the binding.
     * @param type The mathematical type associated with the object.
     * @param typeValue The mathematical type value associated with the object.
     * @param schematicTypes The schematic types associated with the object.
     *
     * @return A new {@link SymbolTableEntry} with the types bound to the object.
     */
    private SymbolTableEntry addBinding(String name, Location l, SymbolTableEntry.Quantification q,
                                        ResolveConceptualElement definingElement, MTType type, MTType typeValue,
                                        Map<String, MTType> schematicTypes) {
        if (type == null) {
            throw new NullPointerException();
        }
        else {
            try {
                return myBuilder.getInnermostActiveScope().addBinding(name, q, definingElement, type, typeValue,
                        schematicTypes, myGenericTypes);
            }
            catch (DuplicateSymbolException dse) {
                duplicateSymbol(name, l);
                throw new RuntimeException(); //This will never fire
            }
        }
    }

    /**
     * <p>Add a new binding for a {@link ResolveConceptualElement} with no quantification.</p>
     *
     * @param name Name of the entry.
     * @param l Location where this element was found.
     * @param definingElement The object that is receiving the binding.
     * @param type The mathematical type associated with the object.
     * @param typeValue The mathematical type value associated with the object.
     * @param schematicTypes The schematic types associated with the object.
     *
     * @return A new {@link SymbolTableEntry} with the types bound to the object.
     */
    private SymbolTableEntry addBinding(String name, Location l, ResolveConceptualElement definingElement,
                                        MTType type, MTType typeValue, Map<String, MTType> schematicTypes) {
        return addBinding(name, l, SymbolTableEntry.Quantification.NONE, definingElement, type,
                typeValue, schematicTypes);
    }

    /**
     * <p>Add a new binding for a {@link ResolveConceptualElement} with
     * no mathematical type value.</p>
     *
     * @param name Name of the entry.
     * @param l Location where this element was found.
     * @param q A quantifier.
     * @param definingElement The object that is receiving the binding.
     * @param type The mathematical type associated with the object.
     * @param schematicTypes The schematic types associated with the object.
     *
     * @return A new {@link SymbolTableEntry} with the types bound to the object.
     */
    private SymbolTableEntry addBinding(String name, Location l, SymbolTableEntry.Quantification q,
                                        ResolveConceptualElement definingElement, MTType type,
                                        Map<String, MTType> schematicTypes) {
        return addBinding(name, l, q, definingElement, type, null, schematicTypes);
    }

    /**
     * <p>Add a new binding for a {@link ResolveConceptualElement} with no quantification
     * and no mathematical type value.</p>
     *
     * @param name Name of the entry.
     * @param l Location where this element was found.
     * @param definingElement The object that is receiving the binding.
     * @param type The mathematical type associated with the object.
     * @param schematicTypes The schematic types associated with the object.
     *
     * @return A new {@link SymbolTableEntry} with the types bound to the object.
     */
    private SymbolTableEntry addBinding(String name, Location l, ResolveConceptualElement definingElement,
                                        MTType type, Map<String, MTType> schematicTypes) {
        return addBinding(name, l, SymbolTableEntry.Quantification.NONE, definingElement, type,
                null, schematicTypes);
    }

    /**
     * <p>Applies the provided mathematical type to a {@link FunctionExp}.</p>
     *
     * @param functionSegment A function expression.
     * @param type The type to be applied.
     *
     * @return The resulting mathematical type.
     */
    private MTType applyFunction(FunctionExp functionSegment, MTType type) {
        MTType result;

        try {
            MTFunction functionType = (MTFunction) type;

            //Ok, we need to type check our arguments before we can
            //continue
            for (Exp exp : functionSegment.getArguments()) {
                TreeWalker.visit(this, exp);
            }

            if (!INEXACT_DOMAIN_MATCH.compare(functionSegment, functionSegment
                            .getConservativePreApplicationType(myTypeGraph),
                    functionType)) {
                throw new SourceErrorException("Parameters do not "
                        + "match function range.\n\nExpected: "
                        + functionType.getDomain()
                        + "\nFound:    "
                        + functionSegment.getConservativePreApplicationType(
                        myTypeGraph).getDomain(), functionSegment
                        .getLocation());
            }

            result = functionType.getRange();
        }
        catch (ClassCastException cce) {
            throw new SourceErrorException("Not a function.", functionSegment
                    .getLocation());
        }

        return result;
    }

    /**
     * <p>An helper method that indicates we are beginning to evaluate a type value node.</p>
     */
    private void enteringTypeValueNode() {
        myTypeValueDepth++;
    }

    /**
     * <p>Returns the name component of a {@link VarExp} or {@link FunctionExp}.</p>
     *
     * @param e Expression to be evaluated.
     *
     * @return The expression name as a string.
     */
    private String getName(Exp e) {
        String result;

        if (e instanceof VarExp) {
            result = ((VarExp) e).getName().getName();
        }
        else if (e instanceof FunctionExp) {
            result = getName(((FunctionExp) e).getName());
        }
        else {
            throw new RuntimeException("Not a VarExp or FunctionExp:  " + e
                    + " (" + e.getClass() + ")");
        }

        return result;
    }

    /**
     * <p>This method has to do an annoying amount of work, so pay attention:
     * takes an iterator over segments as returned from DotExp.getSegments().
     * Either the first segment or first two segments will be advanced over
     * from the iterator, depending on whether this method determines the DotExp
     * refers to a local value (one segment), is a qualified name referring to
     * a value in another module (two segments), or is a Conc expression (two
     * segments).  The segments will receive appropriate types.  The data field
     * of lastGood will be set with the location of the last segment read.
     * Then, the <code>MathSymbolEntry</code> corresponding to the correct
     * top-level value will be returned.</p>
     *
     * @param segments An iterator for the various segments of an {@link Exp}.
     * @param lastGood An object that indirectly refer to the last good segment.
     */
    private MathSymbolEntry getTopLevelValue(Iterator<Exp> segments, Indirect<Exp> lastGood) {
        MathSymbolEntry result;

        Exp first = segments.next();

        PosSymbol firstName;
        if (first instanceof OldExp) {
            firstName = ((VarExp) ((OldExp) first).getExp()).getName();
        }
        else if (first instanceof VarExp) {
            firstName = ((VarExp) first).getName();
        }
        else {
            throw new RuntimeException("DotExp must start with VarExp or "
                    + "OldExp, found: " + first + " (" + first.getClass() + ")");
        }

        //First, we'll see if we're a Conc expression
        if (firstName.getName().equals("Conc")) {
            //Awesome.  We better be in a type definition and our second segment
            //better refer to the exemplar
            VarExp second = (VarExp) segments.next();

            if (!second.toString().equals(
                    myTypeFamilyEntry.getProgramType().getExemplarName())) {
                throw new RuntimeException("No idea what's going on here.");
            }

            //The Conc segment doesn't have a sensible type, but we'll set one
            //for completeness.
            first.setMathType(myTypeGraph.BOOLEAN);

            second.setMathType(myTypeFamilyEntry.getModelType());

            result = myTypeFamilyEntry.getExemplar();

            lastGood.data = second;
        }
        else {
            //Next, we'll see if there's a locally-accessible symbol with this
            //name
            try {
                result =
                        myBuilder
                                .getInnermostActiveScope()
                                .queryForOne(
                                        new NameQuery(
                                                null,
                                                firstName,
                                                ImportStrategy.IMPORT_NAMED,
                                                FacilityStrategy.FACILITY_IGNORE,
                                                true)).toMathSymbolEntry(
                                first.getLocation());

                //There is.  Cool.  We type it and we're done
                lastGood.data = first;
                first.setMathType(result.getType());
                try {
                    first.setMathTypeValue(result.getTypeValue());
                }
                catch (SymbolNotOfKindTypeException snokte) {

                }
            }
            catch (NoSuchSymbolException nsse) {
                //No such luck.  Maybe firstName identifies a module and the
                //second segment (which had better be a VarExp) is the name of
                //the value we want
                VarExp second = (VarExp) segments.next();

                try {
                    result =
                            myBuilder.getInnermostActiveScope().queryForOne(
                                    new NameQuery(firstName, second.getName(),
                                            ImportStrategy.IMPORT_NAMED,
                                            FacilityStrategy.FACILITY_IGNORE,
                                            true)).toMathSymbolEntry(
                                    first.getLocation());

                    //A qualifier doesn't have a sensible type, but we'll set one
                    //for completeness.
                    first.setMathType(myTypeGraph.BOOLEAN);

                    //Now the value itself
                    lastGood.data = second;
                    second.setMathType(result.getType());
                    try {
                        second.setMathTypeValue(result.getTypeValue());
                    }
                    catch (SymbolNotOfKindTypeException snokte) {

                    }
                }
                catch (NoSuchSymbolException nsse2) {
                    noSuchSymbol(firstName, second.getName());
                    throw new RuntimeException(); //This will never fire
                }
                catch (DuplicateSymbolException dse) {
                    //This shouldn't be possible--there can only be one symbol
                    //with the given name inside a particular module
                    throw new RuntimeException();
                }
            }
            catch (DuplicateSymbolException dse) {
                duplicateSymbol(firstName);
                throw new RuntimeException(); //This will never fire
            }
        }

        return result;
    }

    /**
     * <p>An helper method that indicates we are leaving a type value node.</p>
     */
    private void leavingTypeValueNode() {
        myTypeValueDepth--;
    }

    // -----------------------------------------------------------
    // Program Type-Related
    // -----------------------------------------------------------

    /**
     * <p>An helper method that returns the built-in <code>Character</code>
     * program type.</p>
     *
     * @param l A {@link Location} in file.
     *
     * @return A {@link PTType}.
     */
    private PTType getCharProgramType(Location l) {
        PTType result;

        try {
            ProgramTypeEntry type =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new NameQuery(null, "Character",
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_INSTANTIATE,
                                    false)).toProgramTypeEntry(l);

            result = type.getProgramType();
        }
        catch (NoSuchSymbolException nsse) {
            throw new RuntimeException("No program Character type in scope???");
        }
        catch (DuplicateSymbolException dse) {
            //Shouldn't be possible--NameQuery can't throw this
            throw new RuntimeException(dse);
        }

        return result;
    }

    /**
     * <p>An helper method that returns the built-in <code>Integer</code>
     * program type.</p>
     *
     * @param l A {@link Location} in file.
     *
     * @return A {@link PTType}.
     */
    private PTType getIntegerProgramType(Location l) {
        PTType result;

        try {
            ProgramTypeEntry type =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new NameQuery(null, "Integer",
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_INSTANTIATE,
                                    false)).toProgramTypeEntry(l);

            result = type.getProgramType();
        }
        catch (NoSuchSymbolException nsse) {
            throw new RuntimeException("No program Integer type in scope???");
        }
        catch (DuplicateSymbolException dse) {
            //Shouldn't be possible--NameQuery can't throw this
            throw new RuntimeException(dse);
        }

        return result;
    }

    /**
     * <p>An helper method that returns the built-in <code>Char_Str</code>
     * program type.</p>
     *
     * @param l A {@link Location} in file.
     *
     * @return A {@link PTType}.
     */
    private PTType getStringProgramType(Location l) {
        PTType result;

        try {
            ProgramTypeEntry type =
                    myBuilder.getInnermostActiveScope().queryForOne(
                            new NameQuery(null, "Char_Str",
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_INSTANTIATE,
                                    false)).toProgramTypeEntry(l);

            result = type.getProgramType();
        }
        catch (NoSuchSymbolException nsse) {
            throw new RuntimeException("No program String type in scope???");
        }
        catch (DuplicateSymbolException dse) {
            //Shouldn't be possible--NameQuery can't throw this
            throw new RuntimeException(dse);
        }

        return result;
    }

    // -----------------------------------------------------------
    // Operation-Related
    // -----------------------------------------------------------

    /**
     * <p>Obtains the list of all <code>OperationDec</code> and
     * <code>ProcedureDec</code>.</p>
     *
     * @param decs List of all declarations.
     *
     * @return List containing only operations.
     */
    private List<Dec> getOperationDecs(List<Dec> decs) {
        List<Dec> decList = new LinkedList<>();
        for (Dec d : decs) {
            if (d instanceof OperationDec || d instanceof ProcedureDec) {
                decList.add(d);
            }
        }
        return decList;
    }

    /**
     * <p>Checks to see if all operation specified by concept/enhancement
     * are implemented by the corresponding realization.</p>
     *
     * @param location The module that called this method.
     * @param specDecs List of decs of the Concept/Enhancement
     * @param realizationDecs List of decs of the realization.
     */
    private void implementAllOper(Location location, List<Dec> specDecs, List<Dec> realizationDecs) {
        List<Dec> opDecList1 = getOperationDecs(specDecs);
        List<Dec> opDecList2 = getOperationDecs(realizationDecs);

        for (Dec d1 : opDecList1) {
            boolean inRealization = false;
            for (Dec d2 : opDecList2) {
                if (d1.getName().equals(d2.getName())) {
                    inRealization = true;
                }
            }
            if (!inRealization) {
                throw new SourceErrorException("Operation " + d1.getName()
                        + " not implemented by the realization.", location);
            }
        }
    }

    // -----------------------------------------------------------
    // Error Handling
    // -----------------------------------------------------------

    /**
     * <p>An helper method that indicates we have an ambiguous symbol.</p>
     *
     * @param symbol The symbol represented as a {@link PosSymbol}.
     * @param candidates List of symbol entries that match {@code symbol}.
     * @param <T> A type that extends from {@link SymbolTableEntry}.
     */
    private <T extends SymbolTableEntry> void ambiguousSymbol(PosSymbol symbol, List<T> candidates) {
        ambiguousSymbol(symbol.getName(), symbol.getLocation(), candidates);
    }

    /**
     * <p>An helper method that indicates we have an ambiguous symbol.</p>
     *
     * @param symbolName The symbol represented as a {@link String}.
     * @param l The location where {@code symbolName} was found.
     * @param candidates List of symbol entries that match {@code symbolName}.
     * @param <T> A type that extends from {@link SymbolTableEntry}.
     */
    private <T extends SymbolTableEntry> void ambiguousSymbol(String symbolName, Location l, List<T> candidates) {
        String message = "Ambiguous symbol.  Candidates: ";

        boolean first = true;
        for (SymbolTableEntry candidate : candidates) {
            if (first) {
                first = false;
            }
            else {
                message += ", ";
            }

            message +=
                    candidate.getSourceModuleIdentifier()
                            .fullyQualifiedRepresentation(symbolName);
        }

        message += ".  Consider qualifying.";

        throw new SourceErrorException(message, l);
    }

    /**
     * <p>An helper method that indicates we have a duplicate symbol.</p>
     *
     * @param symbol The symbol represented as a {@link PosSymbol}.
     */
    private void duplicateSymbol(PosSymbol symbol) {
        duplicateSymbol(symbol.getName(), symbol.getLocation());
    }

    /**
     * <p>An helper method that indicates we have a duplicate symbol.</p>
     *
     * @param symbol The symbol represented as a {@link PosSymbol}.
     * @param l The location where {@code symbol} was found.
     */
    private void duplicateSymbol(String symbol, Location l) {
        throw new SourceErrorException("Duplicate symbol: " + symbol, l);
    }

    /**
     * <p>An helper method that indicates the expected type differs
     * from the one we found.</p>
     *
     * @param e Expression that is being evaluated.
     * @param expectedType The expected type for {@code e}.
     */
    private void expected(Exp e, MTType expectedType) {
        throw new SourceErrorException("Expected: " + expectedType
                + "\nFound: " + e.getMathType(), e.getLocation());
    }

    /**
     * <p>An helper method that indicates the type we found is not known
     * to be in the expected type.</p>
     *
     * @param e Expression that is being evaluated.
     * @param expectedType The expected type for {@code e}.
     */
    private void expectType(Exp e, MTType expectedType) {
        if (!myTypeGraph.isKnownToBeIn(e, expectedType)) {
            expected(e, expectedType);
        }
    }

    /**
     * <p>An helper method that indicates that the symbol table entry is not
     * known to be a type.</p>
     *
     * @param entry An entry in our symbol table.
     * @param l The location where this entry was found.
     */
    private void notAType(SymbolTableEntry entry, Location l) {
        throw new SourceErrorException(entry.getSourceModuleIdentifier()
                .fullyQualifiedRepresentation(entry.getName())
                + " is not known to be a type.", l);
    }

    /**
     * <p>An helper method that indicates that the evaluated expression is not
     * known to be a type.</p>
     *
     * @param e An expression that is being evaluated.
     */
    private void notAType(Exp e) {
        throw new SourceErrorException("Not known to be a type.", e.getLocation());
    }

    /**
     * <p>An helper method that indicates that a module with the specified name
     * cannot be found.</p>
     *
     * @param qualifier The name of a module.
     */
    private void noSuchModule(PosSymbol qualifier) {
        throw new SourceErrorException("Module does not exist or is not in scope.", qualifier);
    }

    /**
     * <p>An helper method that indicates that a symbol with the specified qualifier
     * and name cannot be found.</p>
     *
     * @param qualifier The module qualifier for the symbol.
     * @param symbol The name of the symbol represented as a {@link PosSymbol}.
     */
    private void noSuchSymbol(PosSymbol qualifier, PosSymbol symbol) {
        noSuchSymbol(qualifier, symbol.getName(), symbol.getLocation());
    }

    /**
     * <p>An helper method that indicates that a symbol with the specified qualifier
     * and name cannot be found.</p>
     *
     * @param qualifier The module qualifier for the symbol.
     * @param symbolName The name of the symbol represented as a {@link String}.
     * @param l The location where this symbol was found.
     */
    private void noSuchSymbol(PosSymbol qualifier, String symbolName, Location l) {
        String message;

        if (qualifier == null) {
            message = "No such symbol: " + symbolName;
        }
        else {
            message =
                    "No such symbol in module: " + qualifier.getName() + "."
                            + symbolName;
        }

        throw new SourceErrorException(message, l);
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>An helper class that indicates an exact domain match between an {@link AbstractFunctionExp}
     * and a {@link MTFunction}.</p>
     */
    private static class ExactDomainMatch
            implements
                TypeComparison<AbstractFunctionExp, MTFunction> {

        /**
         * <p>Takes an instance of {@link AbstractFunctionExp} and use the {@link MTType}
         * found in the expression and compare it with the expected {@link MTType}.</p>
         *
         * @param foundValue The expression to be compared.
         * @param foundType The type for the expression.
         * @param expectedType The expected type for the expression.
         *
         * @return {@code true} if {@code foundType = expectedType}, {@code false} otherwise.
         */
        @Override
        public final boolean compare(AbstractFunctionExp foundValue,
                MTFunction foundType, MTFunction expectedType) {
            return foundType.parameterTypesMatch(expectedType,
                    EXACT_PARAMETER_MATCH);
        }

        /**
         * <p>This method returns a string description for each type comparison.</p>
         *
         * @return A string.
         */
        @Override
        public final String description() {
            return "exact";
        }

    }

    /**
     * <p>An helper class that indicates an inexact domain match between an {@link AbstractFunctionExp}
     * and a {@link MTFunction}.</p>
     */
    private class InexactDomainMatch
            implements
                TypeComparison<AbstractFunctionExp, MTFunction> {

        /**
         * <p>Takes an instance of {@link AbstractFunctionExp} and use the {@link MTType}
         * found in the expression and compare it with the expected {@link MTType}.</p>
         *
         * @param foundValue The expression to be compared.
         * @param foundType The type for the expression.
         * @param expectedType The expected type for the expression.
         *
         * @return {@code true} if {@code foundType = expectedType}, {@code false} otherwise.
         */
        @Override
        public final boolean compare(AbstractFunctionExp foundValue,
                MTFunction foundType, MTFunction expectedType) {
            return expectedType.parametersMatch(foundValue.getParameters(),
                    INEXACT_PARAMETER_MATCH);
        }

        /**
         * <p>This method returns a string description for each type comparison.</p>
         *
         * @return A string.
         */
        @Override
        public final String description() {
            return "inexact";
        }

    }

    /**
     * <p>An helper class that indicates an exact parameter match between
     * two {@link MTType MTTypes}.</p>
     */
    private static class ExactParameterMatch implements Comparator<MTType> {

        /**
         * <p>Compares <code>o1</code> and <code>o2</code>.</p>
         *
         * @param o1 A mathematical type.
         * @param o2 Another mathematical type.
         *
         * @return Comparison results expressed as an integer.
         */
        @Override
        public final int compare(MTType o1, MTType o2) {
            int result;

            if (o1.equals(o2)) {
                result = 0;
            }
            else {
                result = 1;
            }

            return result;
        }

    }

    /**
     * <p>An helper class that indicates an inexact domain match between an {@link Exp}
     * and a {@link MTType}.</p>
     */
    private class InexactParameterMatch implements TypeComparison<Exp, MTType> {

        /**
         * <p>Takes an instance of {@link Exp} and use the {@link MTType}
         * found in the expression and compare it with the expected {@link MTType}.</p>
         *
         * @param foundValue The expression to be compared.
         * @param foundType The type for the expression.
         * @param expectedType The expected type for the expression.
         *
         * @return {@code true} if {@code foundType = expectedType}, {@code false} otherwise.
         */
        @Override
        public final boolean compare(Exp foundValue, MTType foundType,
                MTType expectedType) {

            boolean result =
                    myTypeGraph.isKnownToBeIn(foundValue, expectedType);

            if (!result && foundValue instanceof LambdaExp
                    && expectedType instanceof MTFunction) {
                LambdaExp foundValueAsLambda = (LambdaExp) foundValue;
                MTFunction expectedTypeAsFunction = (MTFunction) expectedType;
                MTFunction foundTypeAsFunction =
                        (MTFunction) foundValueAsLambda.getMathType();

                result =
                        myTypeGraph.isSubtype(foundTypeAsFunction.getDomain(),
                                expectedTypeAsFunction.getDomain())
                                && myTypeGraph.isKnownToBeIn(foundValueAsLambda
                                        .getBody(), expectedTypeAsFunction
                                        .getRange());
            }

            return result;
        }

        /**
         * <p>This method returns a string description for each type comparison.</p>
         *
         * @return A string.
         */
        @Override
        public final String description() {
            return "inexact";
        }

    }

}