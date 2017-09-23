/*
 * JavaTranslator.java
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
package edu.clemson.cs.rsrg.translation.targets;

import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.*;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.init.flag.Flag;
import edu.clemson.cs.rsrg.init.flag.FlagDependencies;
import edu.clemson.cs.rsrg.translation.AbstractTranslator;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import java.util.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

/**
 * <p>This class translates a {@code RESOLVE} source file
 * to {@code Java}.</p>
 *
 * @author Daniel Welch
 * @author Yu-Shan Sun
 * @version 2.0
 */
public class JavaTranslator extends AbstractTranslator {

    // ===========================================================
    // Member Fields
    // ===========================================================

    // ===========================================================
    // Flag Strings
    // ===========================================================

    /** <p>Description for {@code javaTranslate} flag.</p> */
    private static final String FLAG_DESC_TRANSLATE =
            "Translate RESOLVE code to Java.";

    /** <p>Description for {@code javaTranslateClean} flag.</p> */
    private static final String FLAG_DESC_TRANSLATE_CLEAN =
            "Regenerates Java code for all supporting RESOLVE files.";

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>The main translator flag. Tells the compiler convert
     * {@code RESOLVE} source code to {@code Java} source code.</p>
     */
    public static final Flag JAVA_FLAG_TRANSLATE =
            new Flag(FLAG_SECTION_NAME, "javaTranslate", FLAG_DESC_TRANSLATE);

    /**
     * <p>Tells the compiler to regenerate {@code Java} code for all
     * supporting {@code RESOLVE} source files.</p>
     */
    public static final Flag JAVA_FLAG_TRANSLATE_CLEAN =
            new Flag(FLAG_SECTION_NAME, "javaTranslateClean",
                    FLAG_DESC_TRANSLATE_CLEAN);

    /**
     * <p>Add all the required and implied flags for the {@code JavaTranslator}.</p>
     */
    public static void setUpFlags() {
        // Always need to set the auxiliary flag
        FlagDependencies.addImplies(JAVA_FLAG_TRANSLATE, FLAG_TRANSLATE);
        FlagDependencies.addImplies(JAVA_FLAG_TRANSLATE_CLEAN, FLAG_TRANSLATE);

        // Translate clean requires the regular translate flag
        FlagDependencies.addRequires(JAVA_FLAG_TRANSLATE_CLEAN,
                JAVA_FLAG_TRANSLATE);
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an object that overrides methods to translate
     * a {@link ModuleDec} into a {@code Java} source file.</p>
     *
     * @param builder            A scope builder for a symbol table.
     * @param compileEnvironment The current job's compilation environment
     */
    public JavaTranslator(MathSymbolTableBuilder builder,
            CompileEnvironment compileEnvironment) {
        super(builder, compileEnvironment,
                new STGroupFile("templates/Java.stg"));
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Module Declarations
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed after visiting a {@link ModuleDec}.</p>
     *
     * @param dec A module declaration.
     */
    @Override
    public final void postModuleDec(ModuleDec dec) {
        super.postModuleDec(dec);

        ST completed = myActiveTemplates.pop();

        if (myActiveTemplates.size() != 1) {
            throw new IllegalStateException("Translation template stack "
                    + "corrupted. Check your 'post' methods.");
        }

        myActiveTemplates.peek().add("structures", completed);
    }

    // -----------------------------------------------------------
    // Concept Module
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed before visiting a {@link ConceptModuleDec}.</p>
     *
     * @param dec A concept module declaration.
     */
    @Override
    public final void preConceptModuleDec(ConceptModuleDec dec) {
        addPackageTemplate(dec);

        ST concept =
                mySTGroup.getInstanceOf("interface_class").add("name",
                        dec.getName().getName()).add("extend",
                        "RESOLVE_INTERFACE");

        myActiveTemplates.push(concept);
    }

    // -----------------------------------------------------------
    // Type Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed before visiting a {@link TypeFamilyDec}.</p>
     *
     * @param dec A type family declared in a {@code Concept}.
     */
    @Override
    public final void preTypeFamilyDec(TypeFamilyDec dec) {
        ST typeDec =
                mySTGroup.getInstanceOf("interface_class").add("name",
                        dec.getName().getName()).add("extend", "RType").add(
                        "public", false);

        myActiveTemplates.peek().add("classes", typeDec);

        /*try {
            ProgramTypeDefinitionEntry ptde =
                    myScope.queryForOne(
                            new UnqualifiedNameQuery(dec.getName().getName()))
                            .toProgramTypeDefinitionEntry(dec.getLocation());

            ST typeDefinition =
                    getOperationLikeTemplate(ptde.getProgramType(), "create"
                            + dec.getName().getName(), false);

            myActiveTemplates.peek().add("functions", typeDefinition);
        }
        catch (NoSuchSymbolException nsse) {
            throw new RuntimeException(nsse);
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }*/
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Protected Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Creates and adds a formed java package template to the
     * {@code directives} attribute of the outermost {@code module}
     * template defined in <tt>Base.stg</tt>.</p>
     *
     * @param dec The {@link ModuleDec} currently being translated.
     */
    private void addPackageTemplate(ModuleDec dec) {
        List<String> pkgDirectories =
                getFile(dec.getName().getName()).getPkgList();
        ST pkg =
                mySTGroup.getInstanceOf("package").add("directories",
                        pkgDirectories);
        myActiveTemplates.peek().add("directives", pkg);
    }

}