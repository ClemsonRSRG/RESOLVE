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
import edu.clemson.cs.rsrg.typeandpopulate.entry.TypeFamilyEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.*;
import edu.clemson.cs.rsrg.typeandpopulate.query.UnqualifiedNameQuery;
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

        try {
            TypeFamilyEntry typeFamilyEntry =
                    myCurrentModuleScope.queryForOne(
                            new UnqualifiedNameQuery(dec.getName().getName()))
                            .toTypeFamilyEntry(dec.getLocation());

            ST typeDefinition =
                    getOperationLikeTemplate(typeFamilyEntry.getProgramType(),
                            "create" + dec.getName().getName(), false);

            myActiveTemplates.peek().add("functions", typeDefinition);
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, dec.getName());
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse); // shouldn't fire.
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>This method returns the default function modifier specified
     * by the target language.</p>
     *
     * @return Function modifier as a string.
     */
    @Override
    protected final String getFunctionModifier() {
        return "public";
    }

    /**
     * <p>This method returns the operation type template for
     * the target language.</p>
     *
     * @param type A {@link PTType}.
     *
     * @return A {@link ST} associated with the given
     * program type.
     */
    @Override
    protected final ST getOperationTypeTemplate(PTType type) {
        return getVariableTypeTemplate(type);
    }

    /**
     * <p>This method returns the program parameter type template for
     * the target language.</p>
     *
     * @param type A {@link PTType}.
     *
     * @return A {@link ST} associated with the given
     * program parameter type.
     */
    @Override
    protected final ST getParameterTypeTemplate(PTType type) {
        return getVariableTypeTemplate(type);
    }

    /**
     * <p>This method returns the program variable type template for
     * the target language.</p>
     *
     * @param type A {@link PTType}.
     *
     * @return A {@link ST} associated with the given
     * program variable type.
     */
    protected final ST getVariableTypeTemplate(PTType type) {
        ST result;
        ModuleDec currentModule = myCurrentModuleScope.getDefiningElement();

        // Case 1: Generic types ("Entry", "Info", etc.)
        if (type instanceof PTGeneric || type instanceof PTElement) {
            result =
                    mySTGroup.getInstanceOf("unqualified_type").add("name",
                            "RType");
        }
        // Case 2: Program types implemented by a concept realization.
        else if (type instanceof PTRepresentation) {
            result =
                    mySTGroup.getInstanceOf("qualified_type").add("name",
                            getTypeName(type));

            result.add("concept", ((ConceptRealizModuleDec) currentModule)
                    .getConceptName().getName());
        }
        // Case 3: Program types declared and implemented in a facility module.
        else if (type instanceof PTFacilityRepresentation) {
            result =
                    mySTGroup.getInstanceOf("unqualified_type").add("name",
                            getTypeName(type));
        }
        // Case 4: Program types declared by the concept.
        else {
            String moduleName = currentModule.getName().getName();
            result =
                    mySTGroup.getInstanceOf("qualified_type").add("name",
                            getTypeName(type));

            // Case 4.1: This is an instantiated version of a concept type.
            if (getDefiningFacilityEntry(type) != null) {
                moduleName =
                        getDefiningFacilityEntry(type).getFacility()
                                .getSpecification().getModuleIdentifier()
                                .toString();
            }
            // Case 4.2: We are in an enhancement and we are using a type
            //           declared by the concept.
            else if (myCurrentModuleScope.getDefiningElement() instanceof EnhancementModuleDec) {
                moduleName =
                        ((EnhancementModuleDec) myCurrentModuleScope
                                .getDefiningElement()).getConceptName()
                                .getName();
            }
            // Case 4.3: We are in an enhancement realization and we are using a type
            //           declared by the concept.
            else if (myCurrentModuleScope.getDefiningElement() instanceof EnhancementRealizModuleDec) {
                moduleName =
                        ((EnhancementRealizModuleDec) myCurrentModuleScope
                                .getDefiningElement()).getConceptName()
                                .getName();
            }

            result.add("concept", moduleName);
        }

        return result;
    }

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