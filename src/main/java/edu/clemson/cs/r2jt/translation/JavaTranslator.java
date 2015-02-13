/**
 * JavaTranslator.java
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
package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.proving2.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.typeandpopulate.*;
import edu.clemson.cs.r2jt.typeandpopulate.entry.*;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.*;
import edu.clemson.cs.r2jt.typeandpopulate.query.EntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameAndEntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import org.stringtemplate.v4.*;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaTranslator extends AbstractTranslator {

    private static final String FLAG_SECTION_NAME = "Java Translation";

    private static final String FLAG_DESC_TRANSLATE =
            "Translate RESOLVE code to Java.";

    private static final String FLAG_DESC_TRANSLATE_CLEAN =
            "Regenerates Java code for all supporting RESOLVE files.";

    /**
     * <p>The main translator flag.  Tells the compiler convert
     * RESOLVE source code to Java source code.</p>
     */
    public static final Flag JAVA_FLAG_TRANSLATE =
            new Flag(FLAG_SECTION_NAME, "javaTranslate", FLAG_DESC_TRANSLATE);

    /**
     * <p>Tells the compiler to regenerate Java code for all
     * supporting RESOLVE source files.</p>
     */
    public static final Flag JAVA_FLAG_TRANSLATE_CLEAN =
            new Flag(FLAG_SECTION_NAME, "javaTranslateClean",
                    FLAG_DESC_TRANSLATE_CLEAN);

    /**
     * <p>A mapping between the <code>ModuleArgumentItem</code>s
     * representing the actual arguments of a <code>FacilityDec</code> and
     * their formal <code>ModuleParameterDec</code> bound counterparts.</p>
     */
    //TODO: This needs to go.. but how?
    private Map<ModuleArgumentItem, ModuleParameterDec> myFacilityBindings =
            new HashMap<ModuleArgumentItem, ModuleParameterDec>();

    /**
     * <p>This set keeps track of the names of any <code>OperationDec</code>s
     * that parameterize the current module.</p>
     */
    private Set<String> myParameterOperationNames = new HashSet<String>();

    /**
     * <p>A <code>ModuleParameterization</code> corresponding to the
     * <code>EnhancementBodyItem</code> being walked.</p>
     */
    private ModuleParameterization myCurrentEnhancement = null;
    private ST myBaseInstantiation, myBaseEnhancement;

    public JavaTranslator(CompileEnvironment env, ScopeRepository repo) {
        super(env, repo);
        myGroup = new STGroupFile("templates/Java.stg");
    }

    //-------------------------------------------------------------------
    //   Visitor methods
    //-------------------------------------------------------------------

    @Override
    public void preFacilityModuleDec(FacilityModuleDec node) {

        addPackageTemplate(node);

        ST facility =
                myGroup.getInstanceOf("facility_class").add("name",
                        node.getName().getName());

        myActiveTemplates.push(facility);
    }

    @Override
    public void postFacilityModuleDec(FacilityModuleDec node) {

        String invocationName = null;

        boolean buildingJar =
                myInstanceEnvironment.flags.isFlagSet("createJar");

        List<OperationEntry> locals =
                myScope.query(new EntryTypeQuery(OperationEntry.class,
                        ImportStrategy.IMPORT_NONE,
                        FacilityStrategy.FACILITY_IGNORE));

        for (OperationEntry o : locals) {
            if (o.getName() == "Main" || o.getName() == "main") {
                invocationName = o.getName();
            }
        }

        if (invocationName == null && buildingJar) {
            throw new NoSuchMethodError("Facility " + node.getName().getName()
                    + " cannot be executed in Java. Specify a main!");
        }

        myActiveTemplates.peek().add("invoker", invocationName);
    }

    @Override
    public void preConceptModuleDec(ConceptModuleDec node) {

        addPackageTemplate(node);

        ST concept =
                myGroup.getInstanceOf("interface_class").add("name",
                        node.getName().getName()).add("extend",
                        "RESOLVE_INTERFACE");

        myActiveTemplates.push(concept);
    }

    @Override
    public void preEnhancementModuleDec(EnhancementModuleDec node) {

        addPackageTemplate(node);

        ST enhancement =
                myGroup.getInstanceOf("interface_class").add("name",
                        node.getName().getName()).add("extend",
                        node.getConceptName().getName());

        myActiveTemplates.push(enhancement);
    }

    @Override
    public void preEnhancementBodyModuleDec(EnhancementBodyModuleDec node) {

        addPackageTemplate(node);
        addReflectionImportTemplates();

        List<ProgramParameterEntry> formals =
                getModuleFormalParameters(node.getConceptName());

        ST enhancementBody =
                myGroup.getInstanceOf("enhancement_body_class").add("name",
                        node.getName().getName()).add("conceptname",
                        node.getConceptName().getName());

        enhancementBody.add("implement", node.getEnhancementName().getName());
        enhancementBody.add("implement", node.getConceptName().getName());
        enhancementBody.add("implement", "InvocationHandler");

        myActiveTemplates.push(enhancementBody);

        for (ProgramParameterEntry p : formals) {
            addParameterTemplate(p.getDeclaredType(), p.getName());
        }
    }

    private void addReflectionImportTemplates() {

        ST imp =
                myGroup.getInstanceOf("include").add("directories",
                        "java.lang.reflect");

        myActiveTemplates.firstElement().add("includes", imp);
    }

    @Override
    public void preFacilityTypeDec(FacilityTypeDec e) {
        ST record =
                myGroup.getInstanceOf("record_class").add("name",
                        e.getName().getName()).add("facility", true);

        myActiveTemplates.push(record);
    }

    @Override
    public void postFacilityTypeDec(FacilityTypeDec e) {
        ST result = myActiveTemplates.pop();

        myActiveTemplates.peek().add("records", result);
    }

    /**
     * <p>This is where we give the enhancement body all the functionality
     * defined in the base concept. This is done via a set of functions
     * that share the signatures of the functions defined in the base
     * concept, but whose bodies merely call the <em>real</em> method.</p>
     */
    @Override
    public void postEnhancementBodyModuleDec(EnhancementBodyModuleDec node) {

        try {
            ModuleScope conceptScope =
                    myBuilder.getModuleScope(new ModuleIdentifier(node
                            .getConceptName().getName()));

            List<OperationEntry> conceptOperations =
                    conceptScope.query(new EntryTypeQuery<OperationEntry>(
                            OperationEntry.class, ImportStrategy.IMPORT_NONE,
                            FacilityStrategy.FACILITY_IGNORE));

            List<ProgramTypeDefinitionEntry> conceptTypes =
                    conceptScope
                            .query(new EntryTypeQuery<ProgramTypeDefinitionEntry>(
                                    ProgramTypeDefinitionEntry.class,
                                    ImportStrategy.IMPORT_NONE,
                                    FacilityStrategy.FACILITY_IGNORE));

            for (OperationEntry o : conceptOperations) {
                PTType returnType =
                        (o.getReturnType() instanceof PTVoid) ? null : o
                                .getReturnType();

                addEnhancementConceptualFunction(returnType, o.getName(), o
                        .getParameters());
            }

            for (ProgramParameterEntry p : getModuleFormalParameters(node
                    .getConceptName())) {

                addEnhancementConceptualFunction(p.getDeclaredType(), (p
                        .getDeclaredType() instanceof PTElement) ? "getType"
                        + p.getName() : "get" + p.getName(), null);
            }

            for (ProgramTypeDefinitionEntry e : conceptTypes) {
                addEnhancementConceptualFunction(e.getProgramType(), "create"
                        + e.getName(), null);
            }
        }
        catch (NoSuchSymbolException nsse) {
            noSuchModule(node.getConceptName());
        }
    }

    @Override
    public void preConceptBodyModuleDec(ConceptBodyModuleDec node) {

        addPackageTemplate(node);

        ST conceptBody =
                myGroup.getInstanceOf("concept_body_class").add("name",
                        node.getName().getName()).add("implement",
                        node.getConceptName().getName());

        myActiveTemplates.push(conceptBody);

        List<ProgramParameterEntry> formals =
                getModuleFormalParameters(node.getConceptName());

        for (ProgramParameterEntry p : formals) {
            addParameterTemplate(p.getDeclaredType(), p.getName());
        }
    }

    @Override
    public void postConceptBodyModuleDec(ConceptBodyModuleDec node) {

        for (ProgramParameterEntry p : getModuleFormalParameters(node
                .getConceptName())) {

            String name =
                    (p.getDeclaredType() instanceof PTElement) ? "getType"
                            + p.getName() : "get" + p.getName();
            ST result =
                    getOperationLikeTemplate(p.getDeclaredType(), name, true);

            myActiveTemplates.peek().add(
                    "functions",
                    result.add("stmts", myGroup.getInstanceOf("return_stmt")
                            .add("name", p.getName())));
        }
    }

    @Override
    public void preFacilityDec(FacilityDec node) {

        myBaseInstantiation = myGroup.getInstanceOf("facility_init");
        myBaseInstantiation.add("realization", node.getBodyName().getName());

        myActiveTemplates.push(myBaseInstantiation);
        Scope scopeToSearch = myScope;

        // If we're within a function, get the appropriate scope so we
        // can find the SymbolTableEntry representing this FacilityDec.
        // Note : I don't really like this. I'd rather use the depth of the
        // stack I think...
        if (!myScope.equals(myBuilder.getScope(this.getAncestor(2)))) {
            scopeToSearch = myBuilder.getScope(this.getAncestor(2));
        }

        try {
            myCurrentFacilityEntry =
                    scopeToSearch.queryForOne(
                            new NameAndEntryTypeQuery(null, node.getName(),
                                    FacilityEntry.class,
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_IGNORE, false))
                            .toFacilityEntry(node.getLocation());

            ModuleParameterization spec =
                    myCurrentFacilityEntry.getFacility().getSpecification();

            ModuleParameterization realiz =
                    myCurrentFacilityEntry.getFacility().getRealization();

            if (!node.getExternallyRealizedFlag()) {
                constructFacilityArgBindings(spec, realiz);
            }
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, node.getName());
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }
        catch (NoneProvidedException npe) {
            noSuchModule(node.getBodyName());
        }
    }

    @Override
    public void preEnhancementBodyItem(EnhancementBodyItem node) {

        ST singleArg = null;
        LinkedList<Object> args = new LinkedList<Object>();

        List<ModuleParameterization> enhancements =
                myCurrentFacilityEntry.getEnhancements();

        boolean proxied = myCurrentFacilityEntry.getEnhancements().size() > 1;

        if (myBaseInstantiation.getAttribute("arguments") instanceof ST) {
            singleArg = ((ST) myBaseInstantiation.getAttribute("arguments"));
        }
        else if (myBaseInstantiation.getAttribute("arguments") != null) {
            args =
                    new LinkedList((List) myBaseInstantiation
                            .getAttribute("arguments"));
        }

        for (ModuleParameterization m : enhancements) {
            if (m.getModuleIdentifier().toString().equals(
                    node.getName().getName())) {
                constructFacilityArgBindings(m, myCurrentFacilityEntry
                        .getEnhancementRealization(m));

                myCurrentEnhancement = m;
            }
        }

        myActiveTemplates.push(myGroup.getInstanceOf("facility_init"));
        myActiveTemplates.peek().add("isProxied", proxied).add("realization",
                node.getBodyName().getName());

        if (myBaseInstantiation.getAttribute("arguments") instanceof ST) {
            myActiveTemplates.peek().add("arguments", singleArg);
        }
        else {
            myActiveTemplates.peek().add("arguments", args);
        }
    }

    @Override
    public void postEnhancementBodyItem(EnhancementBodyItem node) {

        String curName = node.getBodyName().getName();

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

    @Override
    public void postFacilityDec(FacilityDec node) {

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
                "directories", pathPieces).render());
    }

    @Override
    public void preModuleArgumentItem(ModuleArgumentItem node) {

        PTType type = node.getProgramTypeValue();

        // TODO: Please clean this logic! - YS
        if (type instanceof PTVoid) {
            Dec wrappedDec = myFacilityBindings.get(node).getWrappedDec();
            if (wrappedDec instanceof OperationDec) {
                ST argItem =
                        getOperationArgItemTemplate(
                                (OperationDec) myFacilityBindings.get(node)
                                        .getWrappedDec(), node.getQualifier(),
                                node.getName());

                myActiveTemplates.peek().add("arguments", argItem);
            }
            else if (wrappedDec instanceof ConstantParamDec) {
                myActiveTemplates.peek().add("arguments", wrappedDec.getName());
            }
        }
        else if (type instanceof PTGeneric) {
            myActiveTemplates.peek().add("arguments", node.getName());
        }
        else if (type instanceof PTFacilityRepresentation) {
            myActiveTemplates.peek().add("arguments",
                    "new " + getTypeName(type) + "()");
        }
        else if (node.getEvalExp() == null) {

            ST argItem =
                    myGroup.getInstanceOf("var_init").add("facility",
                            getDefiningFacilityEntry(type).getName()).add(
                            "type", getVariableTypeTemplate(type));

            myActiveTemplates.peek().add("arguments", argItem);
        }
    }

    @Override
    public void postConceptTypeParamDec(ConceptTypeParamDec node) {

        try {
            ProgramParameterEntry ppe =
                    myScope.queryForOne(
                            new NameAndEntryTypeQuery(null, node.getName(),
                                    ProgramParameterEntry.class,
                                    ImportStrategy.IMPORT_NONE,
                                    FacilityStrategy.FACILITY_IGNORE, true))
                            .toProgramParameterEntry(node.getLocation());

            ST getter =
                    getOperationLikeTemplate(ppe.getDeclaredType(), "getType"
                            + node.getName().getName(), false);

            myActiveTemplates.peek().add("functions", getter);
        }
        catch (NoSuchSymbolException nsse) {
            // Should've been caught way before now -- populators fault.
            throw new RuntimeException(nsse);
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }
    }

    @Override
    public void postConstantParamDec(ConstantParamDec node) {

        String name = node.getName().getName();
        PTType type = node.getTy().getProgramTypeValue();

        boolean translatingBody =
                myScope.getDefiningElement() instanceof ConceptBodyModuleDec;

        if (translatingBody) {
            addParameterTemplate(type, name);
        }

        // TODO : This is going to fail on enhancementbodymoduledecs. Fix it.
        ST getter =
                getOperationLikeTemplate(type, "get" + name, translatingBody);
        getter.add("stmts", myGroup.getInstanceOf("return_stmt").add("name",
                name));

        myActiveTemplates.peek().add("functions", getter);
    }

    @Override
    public void preModuleParameterDec(ModuleParameterDec node) {

        if (node.getWrappedDec() instanceof OperationDec) {

            myParameterOperationNames.add(node.getName().getName());

            ST parameter =
                    myGroup.getInstanceOf("parameter").add("type",
                            node.getName().getName()).add("name",
                            node.getName().getName() + "Param");

            ST operationInterface =
                    myGroup.getInstanceOf("interface_class").add("name",
                            node.getName().getName());

            myActiveTemplates.peek().add("parameters", parameter);
            myActiveTemplates.push(operationInterface);
        }
    }

    @Override
    public void postModuleParameterDec(ModuleParameterDec node) {

        if (node.getWrappedDec() instanceof OperationDec) {
            ST operationInterface = myActiveTemplates.pop();
            myActiveTemplates.peek().add("classes", operationInterface);
        }
    }

    @Override
    public void preTypeDec(TypeDec node) {

        ST typeDec =
                myGroup.getInstanceOf("interface_class").add("name",
                        node.getName().getName()).add("extend", "RType").add(
                        "public", false);

        myActiveTemplates.peek().add("classes", typeDec);

        try {
            ProgramTypeDefinitionEntry ptde =
                    myScope.queryForOne(
                            new UnqualifiedNameQuery(node.getName().getName()))
                            .toProgramTypeDefinitionEntry(node.getLocation());

            ST typeDefinition =
                    getOperationLikeTemplate(ptde.getProgramType(), "create"
                            + node.getName().getName(), false);

            myActiveTemplates.peek().add("functions", typeDefinition);
        }
        catch (NoSuchSymbolException nsse) {
            throw new RuntimeException(nsse);
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }
    }

    @Override
    public void preCallStmt(CallStmt node) {

        ST callStmt;
        String qualifier =
                getCallQualifier(node.getQualifier(), node.getName(), node
                        .getArguments());

        // If the call references an operation passed as a parameter, we need
        // to specially qualify it.
        if (myParameterOperationNames.contains(node.getName().getName())) {
            callStmt =
                    myGroup.getInstanceOf("qualified_call").add("name",
                            node.getName().getName()).add("qualifier",
                            node.getName().getName() + "Param");
        }
        else if (qualifier != null) {
            callStmt =
                    myGroup.getInstanceOf("qualified_call").add("name",
                            node.getName().getName()).add("qualifier",
                            qualifier);
        }
        else {
            callStmt =
                    myGroup.getInstanceOf("unqualified_call").add("name",
                            node.getName().getName());
        }

        myActiveTemplates.push(callStmt);
    }

    @Override
    public void preRepresentationDec(RepresentationDec node) {
        List<SymbolTableEntry> types =
                myScope.query(new NameQuery(null, node.getName()));

        PTType repType =
                types.get(0).toProgramTypeEntry(node.getLocation())
                        .getProgramType();

        ST record =
                myGroup.getInstanceOf("record_class").add("name",
                        node.getName().getName()).add("implement",
                        getVariableTypeTemplate(repType));

        myActiveTemplates.push(record);
    }

    @Override
    public void postRepresentationDec(RepresentationDec node) {
        ST instance =
                myGroup.getInstanceOf("facility_init").add("realization",
                        node.getName().getName());

        ST record = myActiveTemplates.pop();
        myActiveTemplates.peek().add("classes", record);

        // Now build the "create<TYPENAME>" method for that record.
        try {
            ProgramTypeEntry pte =
                    myScope.queryForOne(
                            new UnqualifiedNameQuery(node.getName().getName()))
                            .toProgramTypeEntry(node.getLocation());

            ST returnStmt =
                    myGroup.getInstanceOf("return_stmt").add("name", instance);

            ST createMethod =
                    getOperationLikeTemplate(pte.getProgramType(),
                            "create" + node.getName().getName(), true).add(
                            "stmts", returnStmt);

            myActiveTemplates.peek().add("functions", createMethod);
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, node.getName());
        }
        catch (DuplicateSymbolException dse) {
            // Populator's fault.
            throw new RuntimeException(dse);
        }
    }

    @Override
    public void preSwapStmt(SwapStmt node) {

        ST swapStmt;
        boolean translatingEnhancement =
                myScope.getDefiningElement() instanceof EnhancementBodyModuleDec;

        if (node.getLeft().getProgramType() instanceof PTGeneric
                || translatingEnhancement) {

            swapStmt =
                    myGroup.getInstanceOf("unqualified_call").add("name",
                            "swap");
        }
        else {
            FacilityEntry definingFacility =
                    getDefiningFacilityEntry(node.getLeft().getProgramType());

            swapStmt =
                    myGroup.getInstanceOf("qualified_call").add("qualifier",
                            definingFacility.getName()).add("name", "swap");
        }
        myActiveTemplates.push(swapStmt);
    }

    @Override
    public void preVariableNameExp(VariableNameExp node) {

        boolean nonLocal = false;
        ST nameExp = myGroup.getInstanceOf("name_exp");

        if (myScope.getDefiningElement() instanceof ConceptBodyModuleDec) {
            ConceptBodyModuleDec thisModule =
                    ((ConceptBodyModuleDec) myScope.getDefiningElement());

            List<ProgramParameterEntry> formals =
                    getModuleFormalParameters(thisModule.getConceptName());

            for (ProgramParameterEntry e : formals) {
                if (e.getName() == node.getName().getName()) {
                    nonLocal = true;
                }
            }
        }
        nameExp =
                (nonLocal) ? nameExp.add("name", "get"
                        + node.getName().getName() + "()") : nameExp.add(
                        "name", node.getName().getName());

        if (!myWhileStmtChangingClause) {
            myActiveTemplates.peek().add("arguments", nameExp);
        }
    }

    @Override
    public void postModuleDec(ModuleDec node) {
        super.postModuleDec(node);

        ST completed = myActiveTemplates.pop();

        if (myActiveTemplates.size() != 1) {
            throw new IllegalStateException("Translation template stack "
                    + "corrupted. Check your 'post' methods.");
        }
        myActiveTemplates.peek().add("structures", completed);
    }

    //-------------------------------------------------------------------
    //   Helper methods
    //-------------------------------------------------------------------

    // TODO : Refactor me. I don't even really even know anymore what these
    // conditional branches mean.
    @Override
    protected ST getVariableTypeTemplate(PTType type) {

        ST result;
        ModuleDec currentModule = myScope.getDefiningElement();
        String concept = currentModule.getName().getName();

        if (type instanceof PTGeneric || type instanceof PTElement) {
            result =
                    myGroup.getInstanceOf("unqualified_type").add("name",
                            "RType");
        }
        else if (type instanceof PTRepresentation) {

            result =
                    myGroup.getInstanceOf("qualified_type").add("name",
                            getTypeName(type));

            result.add("concept", ((ConceptBodyModuleDec) currentModule)
                    .getConceptName().getName());
        }
        else if (type instanceof PTFacilityRepresentation) {
            result =
                    myGroup.getInstanceOf("unqualified_type").add("name",
                            getTypeName(type));
        }
        else { // PTFamily, etc.
            result =
                    myGroup.getInstanceOf("qualified_type").add("name",
                            getTypeName(type));

            if (getDefiningFacilityEntry(type) != null) {
                concept =
                        getDefiningFacilityEntry(type).getFacility()
                                .getSpecification().getModuleIdentifier()
                                .toString();
            }
            else if (myScope.getDefiningElement() instanceof EnhancementModuleDec) {
                concept =
                        ((EnhancementModuleDec) myScope.getDefiningElement())
                                .getConceptName().getName();
            }
            else if (myScope.getDefiningElement() instanceof EnhancementBodyModuleDec) {
                concept =
                        ((EnhancementBodyModuleDec) myScope
                                .getDefiningElement()).getConceptName()
                                .getName();
            }
            result.add("concept", concept);
        }
        return result;
    }

    @Override
    protected ST getOperationTypeTemplate(PTType type) {
        return getVariableTypeTemplate(type);
    }

    @Override
    protected ST getParameterTypeTemplate(PTType type) {
        return getVariableTypeTemplate(type);
    }

    @Override
    protected String getFunctionModifier() {
        return "public";
    }

    // TODO : Try to refactor/rethink this method + op_arg_template. Too ugly.
    private ST getOperationArgItemTemplate(OperationDec operation,
            PosSymbol qualifier, PosSymbol name) {

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
                    realization);
        }
        catch (NoneProvidedException npe) {

        }
        catch (NoSuchSymbolException nsse) {
            throw new RuntimeException(nsse);
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }
        return result;
    }

    /**
     * <p>This method is only intended to be called when translating
     * <code>enhancementBodyModuleDec</code>s. It is used to construct and add
     * a 'dummy method' that simply uses 'con' to call the actual method.</p>
     *
     * <p>For example, given <code>type</code> = null,
     * <code>name</code> = 'Pop', and <code>parameters</code> = [R,
     * S]; is method returns :
     * <pre>
     *     public void Pop(RType R, Stack_Template.Stack S) {
     *         con.Pop(R, S);
     *     }
     * </pre>
     * </p>
     * @param type A <code>PTType</code> for the function's return type.
     * @param name The name.
     *
     * @param parameters A list of <code>ProgramParameterEntries</code>
     *                   representing the function's formal parameters.
     */
    private void addEnhancementConceptualFunction(PTType type, String name,
            ImmutableList<ProgramParameterEntry> parameters) {

        ST singleLine =
                myGroup.getInstanceOf("enhanced_stmt").add("returns", type)
                        .add("name", name);

        ST operation = getOperationLikeTemplate(type, name, true);
        myActiveTemplates.push(operation);

        if (parameters != null) {
            for (ProgramParameterEntry p : parameters) {

                addParameterTemplate(p.getDeclaredType(), p.getName());
                singleLine.add("arguments", p.getName());
            }
        }
        ST result = myActiveTemplates.pop().add("stmts", singleLine);
        myActiveTemplates.peek().add("conceptfunctions", result);
    }

    /**
     * <p>Binds <em>every</em> actual parameter of a <code>FacilityDec</code>
     * to its formal counterpart, as defined in a concept, enhancement,
     * or realization.</p>
     *
     * @param spec      A <code>ModuleParameterization</code> referencing a
     *                  concept or enhancement.
     *
     * @param realiz    A <code>ModuleParameterization</code> referencing a
     *                  realization (enhancementbody or conceptbody).
     */
    public void constructFacilityArgBindings(ModuleParameterization spec,
            ModuleParameterization realiz) {

        myFacilityBindings.clear();
        try {
            List<ModuleArgumentItem> joinedActuals =
                    new LinkedList<ModuleArgumentItem>(spec.getParameters());

            AbstractParameterizedModuleDec specModule =
                    (AbstractParameterizedModuleDec) myBuilder.getModuleScope(
                            spec.getModuleIdentifier()).getDefiningElement();

            AbstractParameterizedModuleDec realizModule =
                    (AbstractParameterizedModuleDec) myBuilder.getModuleScope(
                            realiz.getModuleIdentifier()).getDefiningElement();

            List<ModuleParameterDec> joinedFormals =
                    new LinkedList<ModuleParameterDec>(specModule
                            .getParameters());

            joinedActuals.addAll(realiz.getParameters());
            joinedFormals.addAll(realizModule.getParameters());

            for (int i = 0; i < joinedActuals.size(); i++) {
                myFacilityBindings.put(joinedActuals.get(i), joinedFormals
                        .get(i));
            }
        }
        catch (NoSuchSymbolException nsse) {
            throw new RuntimeException(nsse);
        }
    }

    /**
     * Creates and adds a formed java package template to the
     * <code>directives</code> attribute of the outermost <code>module</code>
     * template defined in <tt>Base.stg</tt>.
     *
     * @param node The <code>ModuleDec</code> currently being translated.
     */
    public void addPackageTemplate(ModuleDec node) {

        List<String> pkgDirectories = getPathList(getFile(node, null));

        ST pkg =
                myGroup.getInstanceOf("package").add("directories",
                        pkgDirectories);

        myActiveTemplates.peek().add("directives", pkg);
    }

    // TODO : See if there is a simpler, less verbose way of writing
    // the next three methods. And also try to get them into the abstract
    // translator.
    public boolean needToTranslate(File file) {
        boolean translate = false;
        String inFile = file.toString();
        String[] temp = inFile.split("\\.");
        String ext = temp[temp.length - 1];
        if (!onNoCompileList(file)) {
            if (ext.equals("co") || ext.equals("rb") || ext.equals("en")
                    || ext.equals("fa")) {
                String javaName = modifyString(inFile, "\\." + ext, ".java");
                File javaFile = new File(javaName);
                if (!javaFile.exists() || sourceNewerThan(file, javaFile)) {
                    translate = true;
                }
                else if (myInstanceEnvironment.flags
                        .isFlagSet(JAVA_FLAG_TRANSLATE_CLEAN)) {
                    translate = true;
                }
            }
        }
        return translate;
    }

    private String modifyString(String src, String find, String replace) {
        Pattern pattern = Pattern.compile(find);
        Matcher matcher = pattern.matcher(src);
        return matcher.replaceAll(replace);
    }

    private boolean sourceNewerThan(File a, File b) {
        if (a.lastModified() > b.lastModified()) {
            return true;
        }
        return false;
    }

    public static final void setUpFlags() {
        FlagDependencies.addRequires(JAVA_FLAG_TRANSLATE_CLEAN,
                JAVA_FLAG_TRANSLATE);
    }
}