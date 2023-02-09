/*
 * CTranslator.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.translation.targets;

import edu.clemson.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.rsrg.absyn.declarations.moduledecl.ConceptModuleDec;
import edu.clemson.rsrg.absyn.declarations.moduledecl.ConceptRealizModuleDec;
import edu.clemson.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.rsrg.absyn.declarations.typedecl.TypeRepresentationDec;
import edu.clemson.rsrg.absyn.items.programitems.EnhancementSpecRealizItem;
import edu.clemson.rsrg.init.CompileEnvironment;
import edu.clemson.rsrg.init.ResolveCompiler;
import edu.clemson.rsrg.init.flag.Flag;
import edu.clemson.rsrg.init.flag.FlagDependencies;
import edu.clemson.rsrg.translation.AbstractTranslator;
import edu.clemson.rsrg.typeandpopulate.entry.FacilityEntry;
import edu.clemson.rsrg.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTElement;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.rsrg.typeandpopulate.query.NameAndEntryTypeQuery;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import java.util.LinkedList;
import java.util.List;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

/**
 * <p>
 * This class translates a {@code RESOLVE} source file to {@code C}.
 * </p>
 *
 * <p>
 * <em>Note:</em> This implementation isn't complete and needs a lot of work!
 * </p>
 *
 * @author Daniel Welch
 * @author Mark Todd
 * @author Yu-Shan Sun
 *
 * @version 2.0
 */
public class CTranslator extends AbstractTranslator {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * While we walk a {@link FacilityDec} and its children, this maintains a pointer to its corresponding {@link ST}
     * C-struct representation.
     * </p>
     */
    private ST myCurrentStruct = null;

    /**
     * <p>
     * A list of templates corresponding to global facility instantiations. These are intended to be added to the
     * <code>variables</code> attribute of each <code>function</code> template.
     * </p>
     */
    private final List<ST> myFacilityInstantiations;

    // ===========================================================
    // Flag Strings
    // ===========================================================

    /**
     * <p>
     * Description for {@code cTranslate} flag.
     * </p>
     */
    private static final String FLAG_DESC_TRANSLATE = "Translate RESOLVE code to C.";

    /**
     * <p>
     * Description for {@code cTranslateClean} flag.
     * </p>
     */
    private static final String FLAG_DESC_TRANSLATE_CLEAN = "Regenerates C code for all supporting RESOLVE files.";

    // ===========================================================
    // Flags
    // ===========================================================

    /**
     * <p>
     * The main translator flag. Tells the compiler convert {@code RESOLVE} source code to {@code C} source code.
     * </p>
     */
    private static final Flag C_FLAG_TRANSLATE = new Flag(FLAG_SECTION_NAME, "cTranslate", FLAG_DESC_TRANSLATE);

    /**
     * <p>
     * Tells the compiler to regenerate {@code C} code for all supporting {@code RESOLVE} source files.
     * </p>
     */
    private static final Flag C_FLAG_TRANSLATE_CLEAN = new Flag(FLAG_SECTION_NAME, "cTranslateClean",
            FLAG_DESC_TRANSLATE_CLEAN);

    /**
     * <p>
     * Add all the required and implied flags for the {@code CTranslator}.
     * </p>
     */
    public static void setUpFlags() {
        // Always need to set the auxiliary flag
        FlagDependencies.addImplies(C_FLAG_TRANSLATE, FLAG_TRANSLATE);
        FlagDependencies.addImplies(C_FLAG_TRANSLATE_CLEAN, FLAG_TRANSLATE);

        // Translate clean requires the regular translate flag
        FlagDependencies.addRequires(C_FLAG_TRANSLATE_CLEAN, C_FLAG_TRANSLATE);

        // Setup debugging for java translator
        FlagDependencies.addImplies(FLAG_TRANSLATE_DEBUG, ResolveCompiler.FLAG_DEBUG);
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an object that overrides methods to translate a {@link ModuleDec} into a {@code C} source file.
     * </p>
     *
     * @param builder
     *            A scope builder for a symbol table.
     * @param compileEnvironment
     *            The current job's compilation environment
     */
    public CTranslator(MathSymbolTableBuilder builder, CompileEnvironment compileEnvironment) {
        super(builder, compileEnvironment, new STGroupFile("templates/C.stg"));
        myFacilityInstantiations = new LinkedList<>();
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Concept Module
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link ConceptModuleDec}.
     * </p>
     *
     * @param dec
     *            A concept module declaration.
     */
    @Override
    public final void preConceptModuleDec(ConceptModuleDec dec) {
        addPreprocessorDirective(dec.getName().getName());

        ST conceptStruct = mySTGroup.getInstanceOf("struct").add("name", dec.getName().getName());

        myActiveTemplates.push(conceptStruct);

        ST realizSpecific = mySTGroup.getInstanceOf("parameter").add("type", "void*").add("name",
                "realization_specific");

        myActiveTemplates.peek().add("params", realizSpecific);
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link ConceptModuleDec}.
     * </p>
     *
     * @param dec
     *            A concept module declaration.
     */
    @Override
    public final void postConceptModuleDec(ConceptModuleDec dec) {
        ST module = myActiveTemplates.pop();
        myActiveTemplates.peek().add("structures", module);
    }

    // -----------------------------------------------------------
    // Concept Realization Module
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link ConceptRealizModuleDec}.
     * </p>
     *
     * @param dec
     *            A concept realization module declaration.
     */
    @Override
    public final void preConceptRealizModuleDec(ConceptRealizModuleDec dec) {
        String name = dec.getName().getName();
        String conceptName = dec.getConceptName().getName();

        List<ProgramParameterEntry> formals = getModuleFormalParameters(dec.getConceptName());

        ST realizationHeader = mySTGroup.getInstanceOf("concept_realization_header").add("module", name).add("concept",
                conceptName);

        for (ProgramParameterEntry p : formals) {
            ST parameter = mySTGroup.getInstanceOf("parameter")
                    .add("type", getParameterTypeTemplate(p.getDeclaredType())).add("name", p.getName());

            realizationHeader.add("parameters", parameter);
        }

        myActiveTemplates.peek().add("functions", realizationHeader);
    }

    // -----------------------------------------------------------
    // Facility Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link FacilityDec}.
     * </p>
     *
     * @param dec
     *            A facility declaration.
     */
    @Override
    public final void preFacilityDec(FacilityDec dec) {
        String conceptName = dec.getConceptName().getName();
        String realiz = dec.getConceptRealizName().getName() + "_for_" + conceptName;

        try {
            myCurrentFacilityEntry = myCurrentModuleScope
                    .queryForOne(new NameAndEntryTypeQuery<>(null, dec.getName(), FacilityEntry.class,
                            MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                            MathSymbolTable.FacilityStrategy.FACILITY_IGNORE, false))
                    .toFacilityEntry(dec.getLocation());

            myCurrentStruct = mySTGroup.getInstanceOf("struct").add("name", dec.getName().getName());

            ST field = mySTGroup.getInstanceOf("struct_field").add("type", conceptName).add("name", "core")
                    .add("realization", realiz);

            myCurrentStruct.add("variables", field);
            myActiveTemplates.push(myCurrentStruct);
        } catch (NoSuchSymbolException | DuplicateSymbolException nsse) {
            // Should've been caught way before now -- populators fault.
            throw new RuntimeException(nsse);
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link FacilityDec}.
     * </p>
     *
     * @param dec
     *            A facility declaration.
     */
    @Override
    public final void postFacilityDec(FacilityDec dec) {
        ST assignment = mySTGroup.getInstanceOf("facility_assignment").add("struct", myCurrentStruct);

        myFacilityInstantiations.add(assignment);

        myActiveTemplates.pop();
        myActiveTemplates.peek().add("structures", myCurrentStruct);
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link EnhancementSpecRealizItem}.
     * </p>
     *
     * @param item
     *            An {@code enhancement} and {@code realization} item pair.
     */
    @Override
    public final void preEnhancementSpecRealizItem(EnhancementSpecRealizItem item) {
        String enhancementType = item.getEnhancementName().getName() + "_for_"
                + myCurrentFacilityEntry.getFacility().getSpecification().getModuleIdentifier().toString();
        String realiz = item.getEnhancementRealizName().getName() + "_for_" + enhancementType;

        ST field = mySTGroup.getInstanceOf("struct_field").add("type", enhancementType)
                .add("name", item.getEnhancementName().getName()).add("realization", realiz);

        myCurrentStruct.add("variables", field);
        myActiveTemplates.push(field);
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link EnhancementSpecRealizItem}.
     * </p>
     *
     * @param item
     *            An {@code enhancement} and {@code realization} item pair.
     */
    @Override
    public final void postEnhancementSpecRealizItem(EnhancementSpecRealizItem item) {
        // ST coreParameter =
        // myGroup.getInstanceOf("qualified_identifier").add
        // ("qualifier",
        // myCurrentFacilityEntry.getName()).add("name",
        // "core");

        // myActiveTemplates.peek().add("arguments", coreParameter);
        myActiveTemplates.pop();
    }

    // -----------------------------------------------------------
    // Operation Declaration-Related
    // -----------------------------------------------------------

    /*
     * @Override public void preOperationDec(OperationDec node) { String operationName = "(*" + node.getName().getName()
     * + ")"; ST operation = getOperationLikeTemplate((node.getReturnTy() != null) ? node
     * .getReturnTy().getProgramTypeValue() : null, operationName, false);
     *
     * myActiveTemplates.push(operation); }
     */

    /*
     * @Override public void postOperationDec(OperationDec node) { ST operation = myActiveTemplates.pop();
     *
     * // TODO : Add Struct parameter to every function here... // ST conceptStruct =
     * myGroup.getInstanceOf("unqualified_type").add // ("name", myScope) if (myActiveTemplates.size() > 1) { // If
     * we're translating a concept, then our stack should be two // deep since every variable ('operation') is added to
     * a // type-def'ed struct. myActiveTemplates.peek().add("variables", operation); } else {
     * myActiveTemplates.peek().add("functions", operation); } }
     */

    // -----------------------------------------------------------
    // Type Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link TypeRepresentationDec}.
     * </p>
     *
     * @param dec
     *            A type representation declared in a {@code Concept Realization}.
     */
    @Override
    public final void preTypeRepresentationDec(TypeRepresentationDec dec) {
        String conceptName = ((ConceptRealizModuleDec) myCurrentModuleScope.getDefiningElement()).getConceptName()
                .getName();

        ST initAndFinal = mySTGroup.getInstanceOf("init_and_final").add("typename", dec.getName().getName())
                .add("concept", conceptName);

        initAndFinal.add("facilities", myFacilityInstantiations);
        myActiveTemplates.push(initAndFinal);
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link TypeRepresentationDec}.
     * </p>
     *
     * @param dec
     *            A type representation declared in a {@code Concept Realization}.
     */
    @Override
    public final void postTypeRepresentationDec(TypeRepresentationDec dec) {
        ST initAndFinal = myActiveTemplates.pop();
        myActiveTemplates.peek().add("functions", initAndFinal);
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>
     * This method returns the default function modifier specified by the target language.
     * </p>
     *
     * @return Function modifier as a string.
     */
    @Override
    protected final String getFunctionModifier() {
        String modifier = null;

        if (myCurrentModuleScope.getDefiningElement() instanceof ConceptRealizModuleDec) {
            modifier = "static";
        }

        return modifier;
    }

    /**
     * <p>
     * This method returns the operation type template for the target language.
     * </p>
     *
     * @param type
     *            A {@link PTType}.
     *
     * @return A {@link ST} associated with the given program type.
     */
    @Override
    protected final ST getOperationTypeTemplate(PTType type) {
        ST result = mySTGroup.getInstanceOf("unqualified_type");
        if (type instanceof PTElement) {
            result.add("name", "type_info*");
        } else {
            result.add("name", "r_type_ptr");
        }

        return result;
    }

    /**
     * <p>
     * This method returns the program parameter type template for the target language.
     * </p>
     *
     * @param type
     *            A {@link PTType}.
     *
     * @return A {@link ST} associated with the given program parameter type.
     */
    @Override
    protected final ST getParameterTypeTemplate(PTType type) {
        return getOperationTypeTemplate(type);
    }

    /**
     * <p>
     * This method returns the program variable type template for the target language.
     * </p>
     *
     * @param type
     *            A {@link PTType}.
     *
     * @return A {@link ST} associated with the given program variable type.
     */
    protected final ST getVariableTypeTemplate(PTType type) {
        ST result = mySTGroup.getInstanceOf("unqualified_type");

        if (type instanceof PTElement) {
            result.add("name", "type_info*");
        } else {
            result.add("name", getTypeName(type));
        }

        return result;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * Inserts preprocessor directives at the top-of (and end-of) the file undergoing translation. Note that these
     * directives only apply to <strong>C</strong> header files.
     * </p>
     *
     * @param moduleName
     *            Influences the name given to the directive. For ex:
     *            <tt>Integer_Template</tt>-><tt>_INTEGER_TEMPLATE_H</tt>
     */
    private void addPreprocessorDirective(String moduleName) {
        moduleName = "__" + moduleName.toUpperCase() + "_H";

        ST openingDirective = mySTGroup.getInstanceOf("macro_define").add("name", moduleName);
        ST closingDirective = mySTGroup.getInstanceOf("macro_endif");

        if (myActiveTemplates.isEmpty()) {
            throw new IllegalStateException("Translation attempting to " + " add macros to an empty template stack!");
        }

        myActiveTemplates.firstElement().add("directives", openingDirective).add("eof", closingDirective);
    }

}
