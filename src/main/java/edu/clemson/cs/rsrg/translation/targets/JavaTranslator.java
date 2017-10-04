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
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ConceptTypeParamDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ConstantParamDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.init.ResolveCompiler;
import edu.clemson.cs.rsrg.init.flag.Flag;
import edu.clemson.cs.rsrg.init.flag.FlagDependencies;
import edu.clemson.cs.rsrg.translation.AbstractTranslator;
import edu.clemson.cs.rsrg.typeandpopulate.entry.*;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.*;
import edu.clemson.cs.rsrg.typeandpopulate.query.*;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable;
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

    /**
     * <p>This set keeps track of the names of any {@link OperationDec OperationDec(s)}
     * that parameterized the current module.</p>
     */
    private final Set<String> myParameterOperationNames;

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

        // Setup debugging for java translator
        FlagDependencies.addImplies(FLAG_TRANSLATE_DEBUG,
                ResolveCompiler.FLAG_DEBUG);
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
        myParameterOperationNames = new HashSet<>();
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
    // Concept Realization Module
    // -----------------------------------------------------------

    // -----------------------------------------------------------
    // Enhancement Module
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed before visiting an {@link EnhancementModuleDec}.</p>
     *
     * @param dec An enhancement module declaration.
     */
    @Override
    public final void preEnhancementModuleDec(EnhancementModuleDec dec) {
        addPackageTemplate(dec);

        ST enhancement =
                mySTGroup.getInstanceOf("interface_class").add("name",
                        dec.getName().getName()).add("extend",
                        dec.getConceptName().getName());

        myActiveTemplates.push(enhancement);
    }

    // -----------------------------------------------------------
    // Enhancement Realization Module
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed before visiting an {@link EnhancementRealizModuleDec}.</p>
     *
     * @param dec An enhancement realization module declaration.
     */
    @Override
    public final void preEnhancementRealizModuleDec(
            EnhancementRealizModuleDec dec) {
        addPackageTemplate(dec);
        addReflectionImportTemplates(dec);

        List<ProgramParameterEntry> formals =
                getModuleFormalParameters(dec.getConceptName());

        ST enhancementBody =
                mySTGroup.getInstanceOf("enhancement_body_class").add("name",
                        dec.getName().getName()).add("conceptname",
                        dec.getConceptName().getName());

        enhancementBody.add("implement", dec.getEnhancementName().getName());
        enhancementBody.add("implement", dec.getConceptName().getName());
        enhancementBody.add("implement", "InvocationHandler");

        myActiveTemplates.push(enhancementBody);

        for (ProgramParameterEntry p : formals) {
            addParameterTemplate(dec.getLocation(), p.getDeclaredType(), p
                    .getName());
        }
    }

    // -----------------------------------------------------------
    // Module parameter declarations
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed before visiting a {@link ModuleParameterDec}.</p>
     *
     * @param dec A module parameter declaration.
     */
    @Override
    public final void preModuleParameterDec(ModuleParameterDec dec) {
        if (dec.getWrappedDec() instanceof OperationDec) {
            myParameterOperationNames.add(dec.getName().getName());

            ST parameter =
                    mySTGroup.getInstanceOf("parameter").add("type",
                            dec.getName().getName()).add("name",
                            dec.getName().getName() + "Param");

            ST operationInterface =
                    mySTGroup.getInstanceOf("interface_class").add("name",
                            dec.getName().getName());

            myActiveTemplates.peek().add("parameters", parameter);
            myActiveTemplates.push(operationInterface);

            // Return value is the same name as the operation.
            myOperationParameterNames.add(dec.getName().getName());
        }
    }

    /**
     * <p>Code that gets executed after visiting a {@link ModuleParameterDec}.</p>
     *
     * @param dec A module parameter declaration.
     */
    @Override
    public final void postModuleParameterDec(ModuleParameterDec dec) {
        if (dec.getWrappedDec() instanceof OperationDec) {
            ST operationInterface = myActiveTemplates.pop();
            myActiveTemplates.peek().add("classes", operationInterface);

            emitDebug(dec.getLocation(), "Adding operation parameter: "
                    + dec.getName());
        }
    }

    /**
     * <p>Code that gets executed after visiting a {@link ConceptTypeParamDec}.</p>
     *
     * @param param A concept type parameter declaration.
     */
    @Override
    public final void postConceptTypeParamDec(ConceptTypeParamDec param) {
        try {
            ProgramParameterEntry ppe =
                    myCurrentModuleScope.queryForOne(
                            new NameAndEntryTypeQuery<>(null, param.getName(),
                                    ProgramParameterEntry.class,
                                    MathSymbolTable.ImportStrategy.IMPORT_NONE,
                                    MathSymbolTable.FacilityStrategy.FACILITY_IGNORE, true))
                            .toProgramParameterEntry(param.getLocation());

            ST getter =
                    getOperationLikeTemplate(ppe.getDeclaredType(), "getType"
                            + param.getName().getName(), false);

            myActiveTemplates.peek().add("functions", getter);

            emitDebug(param.getLocation(), "Adding concept type parameter: " + param.getName());
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, param.getName());
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse); // shouldn't fire.
        }
    }

    /**
     * <p>This method redefines how a {@link ConstantParamDec} should be walked.</p>
     *
     * @param dec A constant parameter declaration.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkConstantParamDec(ConstantParamDec dec) {
        preAny(dec);
        preDec(dec);

        // YS: We don't want to walker the inner ParameterVarDec,
        //     so we are redefining the walk and adding the logic here.
        String name = dec.getName().getName();
        PTType type = dec.getVarDec().getTy().getProgramType();

        boolean translatingBody =
                (myCurrentModuleScope.getDefiningElement() instanceof ConceptRealizModuleDec)
                        || (myCurrentModuleScope.getDefiningElement() instanceof EnhancementRealizModuleDec);

        if (translatingBody) {
            addParameterTemplate(dec.getLocation(), type, name);
        }

        ST getter =
                getOperationLikeTemplate(type, "get" + name, translatingBody);
        getter.add("stmts", mySTGroup.getInstanceOf("return_stmt").add("name",
                name));

        myActiveTemplates.peek().add("functions", getter);

        emitDebug(dec.getLocation(), "Adding constant parameter: "
                + dec.getName());

        postDec(dec);
        postAny(dec);

        return true;
    }

    // -----------------------------------------------------------
    // Variable Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed before visiting a {@link ParameterVarDec}.</p>
     *
     * @param dec A parameter declaration.
     */
    @Override
    public final void preParameterVarDec(ParameterVarDec dec) {
        PTType type = dec.getTy().getProgramType();

        ST parameter =
                mySTGroup.getInstanceOf("parameter").add("type",
                        getParameterTypeTemplate(type)).add("name",
                        dec.getName().getName());

        myActiveTemplates.peek().add("parameters", parameter);

        emitDebug(dec.getLocation(), "Adding parameter variable: "
                + dec.getName());
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

            emitDebug(dec.getLocation(), "Adding type family declaration: "
                    + dec.getName());
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

        emitDebug(dec.getLocation(), "Adding package template for module: "
                + dec.getName());
    }

    /**
     * <p>Creates and adds a formed java reflection package template to the
     * {@code includes} attribute of the outermost {@code module}
     * template defined in <tt>Base.stg</tt>.</p>
     *
     * @param dec The {@link EnhancementRealizModuleDec} currently being translated.
     */
    private void addReflectionImportTemplates(EnhancementRealizModuleDec dec) {
        ST imp =
                mySTGroup.getInstanceOf("include").add("directories",
                        "java.lang.reflect");

        myActiveTemplates.firstElement().add("includes", imp);

        emitDebug(dec.getLocation(), "Adding reflection imports for module: "
                + dec.getName());
    }

}