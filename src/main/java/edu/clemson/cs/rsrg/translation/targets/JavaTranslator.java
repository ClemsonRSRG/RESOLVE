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

import edu.clemson.cs.r2jt.rewriteprover.immutableadts.ImmutableList;
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.*;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ConceptTypeParamDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ConstantParamDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramVariableNameExp;
import edu.clemson.cs.rsrg.absyn.items.programitems.EnhancementSpecRealizItem;
import edu.clemson.cs.rsrg.absyn.items.programitems.ModuleArgumentItem;
import edu.clemson.cs.rsrg.init.CompileEnvironment;
import edu.clemson.cs.rsrg.init.ResolveCompiler;
import edu.clemson.cs.rsrg.init.flag.Flag;
import edu.clemson.cs.rsrg.init.flag.FlagDependencies;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.translation.AbstractTranslator;
import edu.clemson.cs.rsrg.typeandpopulate.entry.*;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoneProvidedException;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.*;
import edu.clemson.cs.rsrg.typeandpopulate.query.*;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleParameterization;
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
     * <p>These are string templates used to generate code for {@link FacilityDec FacilitiDec(s)}
     * and its associated enhancements.</p>
     */
    private ST myBaseInstantiation, myBaseEnhancement;

    /**
     * <p>A {@link ModuleParameterization} corresponding to the
     * {@link EnhancementSpecRealizItem} being walked.</p>
     */
    private ModuleParameterization myCurrentEnhancement;

    /**
     * <p>A mapping between the {@link ModuleArgumentItem ModuleArgumentItems}
     * representing the actual arguments of a {@link FacilityDec} and
     * their formal {@link ModuleParameterDec} bound counterparts.</p>
     */
    private final Map<ModuleArgumentItem, ModuleParameterDec> myFacilityBindings;

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
        myBaseEnhancement = null;
        myBaseInstantiation = null;
        myCurrentEnhancement = null;
        myFacilityBindings = new LinkedHashMap<>();
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

    /**
     * <p>Code that gets executed after visiting an {@link EnhancementRealizModuleDec}.</p>
     *
     * @param dec An enhancement realization module declaration.
     */
    @Override
    public final void postEnhancementRealizModuleDec(
            EnhancementRealizModuleDec dec) {
        /* This is where we give the enhancement body all the functionality
         * defined in the base concept. This is done via a set of functions
         * that share the signatures of the functions defined in the base
         * concept, but whose bodies merely call the <em>real</em> method.
         */
        try {
            ModuleScope conceptScope =
                    myBuilder.getModuleScope(new ModuleIdentifier(dec
                            .getConceptName().getName()));

            List<OperationEntry> conceptOperations =
                    conceptScope.query(new EntryTypeQuery<>(
                            OperationEntry.class,
                            MathSymbolTable.ImportStrategy.IMPORT_NONE,
                            MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));

            List<TypeFamilyEntry> conceptTypes =
                    conceptScope
                            .query(new EntryTypeQuery<>(
                                    TypeFamilyEntry.class,
                                    MathSymbolTable.ImportStrategy.IMPORT_NONE,
                                    MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));

            for (OperationEntry o : conceptOperations) {
                PTType returnType =
                        (o.getReturnType() instanceof PTVoid) ? null : o
                                .getReturnType();

                addEnhancementConceptualFunction(o.getDefiningElement().getLocation(),
                        returnType, o.getName(), o.getParameters());
            }

            for (ProgramParameterEntry p : getModuleFormalParameters(dec
                    .getConceptName())) {

                addEnhancementConceptualFunction(p.getDefiningElement().getLocation(),
                        p.getDeclaredType(), (p.getDeclaredType() instanceof PTElement) ? "getType"
                        + p.getName() : "get" + p.getName(), null);
            }

            for (TypeFamilyEntry e : conceptTypes) {
                addEnhancementConceptualFunction(e.getDefiningElement().getLocation(),
                        e.getProgramType(), "create" + e.getName(), null);
            }
        }
        catch (NoSuchSymbolException nsse) {
            noSuchModule(dec.getConceptName());
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
    // Facility Declaration-Related
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed before visiting a {@link FacilityDec}.</p>
     *
     * @param dec A facility declaration.
     */
    @Override
    public final void preFacilityDec(FacilityDec dec) {
        myBaseInstantiation = mySTGroup.getInstanceOf("facility_init");
        myBaseInstantiation.add("realization", dec.getConceptRealizName().getName());

        myActiveTemplates.push(myBaseInstantiation);
        Scope scopeToSearch = myCurrentModuleScope;

        // If we're within a function, get the appropriate scope so we
        // can find the SymbolTableEntry representing this FacilityDec.
        // Note : I don't really like this. I'd rather use the depth of the
        // stack I think...
        // YS: Might not need the following if.
        if (!myCurrentModuleScope.equals(myBuilder.getScope(this.getAncestor(2)))) {
            scopeToSearch = myBuilder.getScope(this.getAncestor(2));
        }

        try {
            myCurrentFacilityEntry =
                    scopeToSearch.queryForOne(
                            new NameAndEntryTypeQuery<>(null, dec.getName(),
                                    FacilityEntry.class,
                                    MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                                    MathSymbolTable.FacilityStrategy.FACILITY_IGNORE, false))
                            .toFacilityEntry(dec.getLocation());

            ModuleParameterization spec =
                    myCurrentFacilityEntry.getFacility().getSpecification();

            ModuleParameterization realiz =
                    myCurrentFacilityEntry.getFacility().getRealization();

            if (!dec.getExternallyRealizedFlag()) {
                constructFacilityArgBindings(dec.getLocation(), spec, realiz);
            }
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, dec.getName());
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }
        catch (NoneProvidedException npe) {
            noSuchModule(dec.getConceptRealizName());
        }
    }

    /**
     * <p>Code that gets executed after visiting a {@link FacilityDec}.</p>
     *
     * @param dec A facility declaration.
     */
    @Override
    public final void postFacilityDec(FacilityDec dec) {
    /* TODO: Might have to refactor module argument items first before dealing with this.
    String facilityType = node.getConceptName().getName();
    List<String> pathPieces =
            getPathList(getFile(null, node.getConceptName().getName()));

    // Basically: If we are an enhanced facility, clear the stack of only
    // the templates pushed for each EnhancementBodyItem plus the base
    // instantiation.. THEN push on the formed (enhanced) rhs.
    if (myActiveTemplates.peek() != myBaseInstantiation) {

        for (ModuleParameterization p : myCurrentFacilityEntry
                .getEnhancements()) {
            myActiveTemplates.pop();
        }
        myActiveTemplates.pop();
        myActiveTemplates.push(myBaseEnhancement);
    }

    // TODO : Figure out why the hell node.getEnhancements().size() is 0
    //        in BPStack.fa where there is clearly one enhancement!
    if (node.getEnhancementBodies().size() == 1) {
        facilityType =
                node.getEnhancementBodies().get(0).getName().getName();
    }

    ST facilityVariable =
            myGroup.getInstanceOf("var_decl").add("type", facilityType)
                    .add("name", node.getName().getName()).add("init",
                            myActiveTemplates.pop());

    myActiveTemplates.peek().add("variables", facilityVariable);

    myDynamicImports.add(myGroup.getInstanceOf("include").add(
            "directories", pathPieces).render()); */
    }

    /**
     * <p>Code that gets executed before visiting a {@link EnhancementSpecRealizItem}.</p>
     *
     * @param item An {@code enhancement} and {@code realization} item pair.
     */
    @Override
    public final void preEnhancementSpecRealizItem(EnhancementSpecRealizItem item) {
        ST singleArg = null;
        List<Object> args = new LinkedList<>();

        List<ModuleParameterization> enhancements =
                myCurrentFacilityEntry.getEnhancements();

        boolean proxied = myCurrentFacilityEntry.getEnhancements().size() > 1;

        if (myBaseInstantiation.getAttribute("arguments") instanceof ST) {
            singleArg = ((ST) myBaseInstantiation.getAttribute("arguments"));
        }
        else if (myBaseInstantiation.getAttribute("arguments") != null) {
            args =
                    new LinkedList<Object>((List) myBaseInstantiation
                            .getAttribute("arguments"));
        }

        for (ModuleParameterization m : enhancements) {
            if (m.getModuleIdentifier().toString().equals(
                    item.getEnhancementName().getName())) {
                constructFacilityArgBindings(item.getLocation(),
                        m, myCurrentFacilityEntry.getEnhancementRealization(m));
                myCurrentEnhancement = m;
            }
        }

        myActiveTemplates.push(mySTGroup.getInstanceOf("facility_init"));
        myActiveTemplates.peek().add("isProxied", proxied).add("realization",
                item.getEnhancementRealizName().getName());

        if (myBaseInstantiation.getAttribute("arguments") instanceof ST) {
            myActiveTemplates.peek().add("arguments", singleArg);
        }
        else {
            myActiveTemplates.peek().add("arguments", args);
        }
    }

    /**
     * <p>Code that gets executed after visiting a {@link EnhancementSpecRealizItem}.</p>
     *
     * @param item An {@code enhancement} and {@code realization} item pair.
     */
    @Override
    public final void postEnhancementSpecRealizItem(
            EnhancementSpecRealizItem item) {
        String curName = item.getEnhancementRealizName().getName();

        List<ModuleParameterization> enhancements =
                myCurrentFacilityEntry.getEnhancements();

        ModuleParameterization first = enhancements.get(0);
        ModuleParameterization last = enhancements.get(enhancements.size() - 1);

        String firstBodyName =
                myCurrentFacilityEntry.getEnhancementRealization(first)
                        .getModuleIdentifier().toString();

        String lastBodyName =
                myCurrentFacilityEntry.getEnhancementRealization(last)
                        .getModuleIdentifier().toString();

        if (curName.equals(lastBodyName)) {
            myActiveTemplates.peek().add("arguments",
                    myBaseInstantiation.render());
        }

        if (curName.equals(firstBodyName)) {
            myBaseEnhancement = myActiveTemplates.peek();
        }
        else {
            myBaseEnhancement.add("arguments", myActiveTemplates.peek());
        }
    }

    /**
     * <p>Code that gets executed before visiting a {@link ModuleArgumentItem}.</p>
     *
     * @param item A module argument used in instantiating a {@link FacilityDec}.
     */
    @Override
    public final void preModuleArgumentItem(ModuleArgumentItem item) {
        ProgramExp argumentExp = item.getArgumentExp();
        Dec wrappedDec = myFacilityBindings.get(item).getWrappedDec();

        // Case 1: The wrapped declaration is an operation name as argument.
        if (wrappedDec instanceof OperationDec) {
            ProgramVariableNameExp operationName =
                    (ProgramVariableNameExp) argumentExp;
            ST argItem =
                    getOperationArgItemTemplate(
                            (OperationDec) myFacilityBindings.get(item)
                                    .getWrappedDec(), operationName
                                    .getQualifier(), operationName.getName());

            myActiveTemplates.peek().add("arguments", argItem);
        }
        // Case 2: The wrapped declaration is some constant value.
        else if (wrappedDec instanceof ConstantParamDec) {
            //myActiveTemplates.peek().add("arguments", wrappedDec.getName());
        }
        // Case 3: The wrapped declaration is a concept type.
        else if (wrappedDec instanceof ConceptTypeParamDec) {
            PTType type = item.getProgramTypeValue();

            // Case 3.1: A generic type passed as module parameter
            if (type instanceof PTGeneric) {
                ProgramVariableNameExp typeName =
                        (ProgramVariableNameExp) item.getArgumentExp();
                myActiveTemplates.peek().add("arguments", typeName.getName());
            }
            // Case 3.2: A facility declared type that doesn't have a concept model
            else if (type instanceof PTFacilityRepresentation) {
                myActiveTemplates.peek().add("arguments",
                        "new " + getTypeName(type) + "()");
            }
            // Case 3.3: A facility instantiated type passed as module parameter
            else {
                ST argItem =
                        mySTGroup.getInstanceOf("var_init").add("facility",
                                getDefiningFacilityEntry(type).getName()).add(
                                "type", getVariableTypeTemplate(type));

                myActiveTemplates.peek().add("arguments", argItem);
            }
        }
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
     * <p>This method is only intended to be called when translating
     * {@link EnhancementRealizModuleDec EnhancementRealizModuleDecs}.
     * It is used to construct and add a 'dummy method' that simply uses
     * 'con' to call the actual method.</p>
     *
     * <p>For example, given {@code type} = null,
     * {@code name} = 'Pop', and {@code parameters} = [R, S]; is method returns :
     * <pre>
     *     public void Pop(RType R, Stack_Template.Stack S) {
     *         con.Pop(R, S);
     *     }
     * </pre>
     * </p>
     *
     * @param type A {@link PTType} for the function's return type.
     * @param name The name.
     * @param parameters A list of {@link ProgramParameterEntry}
     *                   representing the function's formal parameters.
     */
    private void addEnhancementConceptualFunction(Location loc, PTType type,
            String name, ImmutableList<ProgramParameterEntry> parameters) {
        ST singleLine =
                mySTGroup.getInstanceOf("enhanced_stmt").add("returns", type)
                        .add("name", name);

        ST operation = getOperationLikeTemplate(type, name, true);
        myActiveTemplates.push(operation);

        if (parameters != null) {
            for (ProgramParameterEntry p : parameters) {
                addParameterTemplate(loc, p.getDeclaredType(), p.getName());
                singleLine.add("arguments", p.getName());
            }
        }

        ST result = myActiveTemplates.pop().add("stmts", singleLine);
        myActiveTemplates.peek().add("conceptfunctions", result);

        emitDebug(loc, "Adding enhancement conceptual function for: " + name);
    }

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

    /**
     * <p>Binds <em>every</em> actual parameter of a {@link FacilityDec}
     * to its formal counterpart, as defined in a {@code concept}, {@code enhancement},
     * or {@code realization}.</p>
     *
     * @param loc The {@link Location} where we are trying to bind the facility arguments.
     * @param spec A {@link ModuleParameterization} referencing a
     *             {@code concept} or {@code enhancement}.
     * @param realiz A {@link ModuleParameterization} referencing a
     *               realization ({@code concept realization} or {@code enhancement realization}).
     */
    private void constructFacilityArgBindings(Location loc, ModuleParameterization spec, ModuleParameterization realiz) {
        myFacilityBindings.clear();

        try {
            List<ModuleArgumentItem> joinedActuals = new LinkedList<>(spec.getParameters());

            ModuleDec specModule = myBuilder.getModuleScope(spec.getModuleIdentifier()).getDefiningElement();
            ModuleDec realizModule = myBuilder.getModuleScope(realiz.getModuleIdentifier()).getDefiningElement();

            List<ModuleParameterDec> joinedFormals = new LinkedList<>(specModule.getParameterDecs());
            joinedActuals.addAll(realiz.getParameters());
            joinedFormals.addAll(realizModule.getParameterDecs());

            for (int i = 0; i < joinedActuals.size(); i++) {
                myFacilityBindings.put(joinedActuals.get(i), joinedFormals.get(i));
            }
        }
        catch (NoSuchSymbolException nsse) {
            throw new SourceErrorException("[" + getClass().getCanonicalName()
                    + "] " + "Error while trying to bind facility arguments.", loc);
        }
    }

    // TODO: Add javadoc.
    private ST getOperationArgItemTemplate(OperationDec operation,
            PosSymbol qualifier, PosSymbol name) {
        /* TODO : Try to refactor/rethink this method + op_arg_template. Too ugly.
        int parameterNum = 0;
        ST result =
                myGroup.getInstanceOf("operation_argument_item").add(
                        "actualName", name.getName());

        if (qualifier != null) {
            result.add("actualQualifier", qualifier.getName());
        }

        try {
            OperationEntry o =
                    myScope.queryForOne(
                            new NameQuery(qualifier, name,
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_INSTANTIATE,
                                    false))
                            .toOperationEntry(name.getLocation());

            for (ProgramParameterEntry p : o.getParameters()) {
                ST castedType = getVariableTypeTemplate(p.getDeclaredType());
                ST castedArg =
                        myGroup.getInstanceOf("parameter").add("type",
                                castedType).add("name", "p" + parameterNum);
                result.add("castedArguments", castedArg);
                parameterNum++;
            }
            parameterNum = 0;

            String realization;
            if (myCurrentEnhancement != null) {
                realization =
                        myCurrentFacilityEntry.getEnhancementRealization(
                                myCurrentEnhancement).getModuleIdentifier()
                                .toString();
            }
            else {
                realization =
                        myCurrentFacilityEntry.getFacility().getRealization()
                                .getModuleIdentifier().toString();
            }

            PTType returnType =
                    (operation.getReturnTy() != null) ? operation.getReturnTy()
                            .getProgramTypeValue() : null;

            ST interior =
                    getOperationLikeTemplate(returnType, operation.getName()
                            .getName(), true);

            myActiveTemplates.push(interior);

            for (ParameterVarDec p : operation.getParameters()) {
                addParameterTemplate(p.getTy().getProgramTypeValue(), "p"
                        + parameterNum);
                parameterNum++;
            }

            result.add("function", myActiveTemplates.pop()).add("realization",
                    realization).add("hasReturn", returnType != null);
        }
        catch (NoneProvidedException npe) {

        }
        catch (NoSuchSymbolException nsse) {
            throw new RuntimeException(nsse);
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }
        return result;*/
        return null; // TODO: Remove once we finish refactoring.
    }
}