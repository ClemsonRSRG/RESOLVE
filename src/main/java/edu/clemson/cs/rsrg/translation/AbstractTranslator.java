/*
 * AbstractTranslator.java
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
package edu.clemson.cs.rsrg.translation;

import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.init.flag.Flag;
import edu.clemson.cs.rsrg.init.flag.Flag.Type;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This is the abstract base class for all target language translators
 * using the RESOLVE abstract syntax tree. This visitor logic is implemented as
 * a {@link TreeWalkerStackVisitor}.</p>
 *
 * @author Daniel Welch
 * @author Mark Todd
 * @author Yu-Shan Sun
 * @version 2.0
 */
public abstract class AbstractTranslator extends TreeWalkerStackVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The symbol table we are currently building.</p> */
    private final MathSymbolTableBuilder myBuilder;

    /**
     * <p>The current job's compilation environment
     * that stores all necessary objects and flags.</p>
     */
    private final CompileEnvironment myCompileEnvironment;

    /**
     * <p>The module scope for the file we are generating
     * {@code VCs} for.</p>
     */
    private ModuleScope myCurrentModuleScope;

    /**
     * <p>This set keeps track of any additional <code>includes</code> or
     * <code>imports</code> needed to run the translated file. We call
     * it <em>dynamic</em> since only certain nodes trigger additions to this
     * set (i.e. <code>FacilityDec</code>s).</p>
     */
    protected final Set<String> myDynamicImports;

    /**
     * <p>These are special files that should already exist in
     * the current workspace and shouldn't be overwritten.</p>
     */
    private static final List<String> noTranslate =
            Arrays.asList("Std_Boolean_Fac.fa", "Std_Char_Str_Fac.fa",
                    "Std_Character_Fac.fa", "Std_Integer_Fac.fa",
                    "Std_Boolean_Realiz", "Integer_Template.co",
                    "Character_Template.co", "Char_Str_Template.co",
                    "Seq_Input_Template.co", "Seq_Output_Template.co",
                    "Print.co", "Std_Location_Linking_Realiz.rb");

    // -----------------------------------------------------------
    // Output-Related
    // -----------------------------------------------------------

    /**
     * <p>The top of this {@link Stack} maintains a reference to the
     * template actively being built or added to, and the bottom refers to
     * {@code shell} - the outermost enclosing template for all
     * currently supported target languages.</p>
     *
     * <p>Proper usage should generally involve: Pushing in <tt>pre</tt>,
     * modifying top arbitrarily with <tt>pre</tt>'s children, popping in the
     * corresponding <tt>post</tt>, then adding the popped template to the
     * appropriate enclosing template (i.e. the new/current top).</p>
     */
    protected final Stack<ST> myActiveTemplates;

    /**
     * <p>String template groups that houses all templates used by a
     * given target language.</p>
     */
    protected final STGroup mySTGroup;

    // ===========================================================
    // Flag Strings
    // ===========================================================

    /**
     * <p>This indicates that this section translates {@code RESOLVE}
     * source files to other target languages.</p>
     */
    protected static final String FLAG_SECTION_NAME = "Translation";

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>An auxiliary flag that indicates we are translating to
     * a target file.</p>
     */
    public static final Flag FLAG_TRANSLATE =
            new Flag(
                    FLAG_SECTION_NAME,
                    "translate",
                    "An auxiliary flag that indicates we are translating a source file",
                    Type.AUXILIARY);

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that creates and stores all the common
     * objects used by classes that inherit from this class.</p>
     *
     * @param builder A scope builder for a symbol table.
     * @param compileEnvironment The current job's compilation environment
     *                           that stores all necessary objects and flags.
     * @param group The string template group to be used by each
     *              of the implementing subclass.
     */
    protected AbstractTranslator(MathSymbolTableBuilder builder,
            CompileEnvironment compileEnvironment, STGroup group) {
        myActiveTemplates = new Stack<>();
        myBuilder = builder;
        myCompileEnvironment = compileEnvironment;
        myDynamicImports = new LinkedHashSet<>();
        mySTGroup = group;
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
     * @param dec A module declaration.
     */
    @Override
    public final void preModuleDec(ModuleDec dec) {
        try {
            myCurrentModuleScope =
                    myBuilder.getModuleScope(new ModuleIdentifier(dec));

            // Add to translation model
            ST outermostEnclosingTemplate = mySTGroup.getInstanceOf("module");
            outermostEnclosingTemplate.add("includes", mySTGroup.getInstanceOf(
                    "include").add("directories", "RESOLVE"));

            // Store this as our current outermost template
            myActiveTemplates.push(outermostEnclosingTemplate);
        }
        catch (NoSuchSymbolException nsse) {
            noSuchModule(dec.getLocation());
        }
    }

    /**
     * <p>Code that gets executed after visiting a {@link ModuleDec}.</p>
     *
     * @param dec A module declaration.
     */
    @Override
    public final void postModuleDec(ModuleDec dec) {
        if (!myDynamicImports.isEmpty()) {
            myActiveTemplates.firstElement().add("includes", myDynamicImports);
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method returns the translated source file
     * as a string.</p>
     *
     * @return A file content string.
     */
    public final String getOutputCode() {
        return myActiveTemplates.peek().render();
    }

    /**
     * <p>This method checks to see if we should translate
     * a file.</p>
     *
     * @param identifier A module identifier.
     *
     * @return {@code true} if it is a special file
     * that we shouldn't translate, {@code false} otherwise.
     */
    public static boolean onNoTranslateList(ModuleIdentifier identifier) {
        return noTranslate.contains(identifier.toString());
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>An helper method that throws the appropriate no module found
     * message.</p>
     *
     * @param loc Location where this module name was found.
     */
    protected final void noSuchModule(Location loc) {
        throw new SourceErrorException("[" + getClass().getCanonicalName()
                + "] " + "Module does not exist or is not in scope.", loc);
    }

    /**
     * <p>An helper method that throws the appropriate no symbol found
     * message.</p>
     *
     * @param qualifier The symbol's qualifier.
     * @param symbol The symbol not found.
     */
    protected final void noSuchSymbol(PosSymbol qualifier, PosSymbol symbol) {
        noSuchSymbol(qualifier, symbol.getName(), symbol.getLocation());
    }

    /**
     * <p>An helper method that throws the appropriate no symbol found
     * message.</p>
     *
     * @param qualifier The symbol's qualifier.
     * @param symbolName The symbol's name.
     * @param loc Location where this symbol was found.
     */
    protected final void noSuchSymbol(PosSymbol qualifier, String symbolName,
            Location loc) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(getClass().getCanonicalName());
        sb.append("] ");

        if (qualifier == null) {
            sb.append("Translation was unable to find symbol: ");
            sb.append(symbolName);
        }
        else {
            sb.append("No such symbol in module: ");
            sb.append(qualifier.getName());
            sb.append("::");
            sb.append(symbolName);
        }

        throw new SourceErrorException(sb.toString(), loc);
    }

}