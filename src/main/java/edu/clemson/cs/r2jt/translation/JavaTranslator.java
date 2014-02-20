package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.typeandpopulate.*;
import edu.clemson.cs.r2jt.typeandpopulate.entry.*;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.*;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameAndEntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import org.stringtemplate.v4.*;

import java.util.*;

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
     * representing the actual parameters of a <code>FacilityDec</code> and
     * their formal <code>ModuleParameterDec</code>-bound counterparts.</p>
     */
    private Map<ModuleArgumentItem, ModuleParameterDec> myFacilityBindings =
            new HashMap<ModuleArgumentItem, ModuleParameterDec>();

    /**
     * <p>A <code>ModuleParameterization</code> corresponding to the
     * <code>EnhancementBodyItem</code> being walked.</p>
     */
    private ModuleParameterization myCurrentEnhancement = null;

    /**
     * <p>A pointer to Java's <em>outermost</em> class template. This houses
     * everythingfrom global the <code>class_declaration</code> template, to
     * those for <code>functions</code>, subclasses, and anything else that
     * might be found in a java class.</p>
     */
    private ST myOutermostJavaClass;
    private ST myBaseInstantiation, myBaseEnhancement;

    /**
     * <p>A <strong>temporary</strong> measure to get things rolling. This
     * will <em>hopefully</em> be gone in the near future.</p>
     */
    private final String[] myHardcodedStdFacs =
            {
                    "Boolean_Template Std_Boolean_Fac = new Std_Boolean_Realiz();",
                    "Integer_Template Std_Integer_Fac = new Std_Integer_Realiz();",
                    "Character_Template Std_Character_Fac = new Std_Character_Realiz();",
                    "Char_Str_Template Std_Char_Str_Fac = new Std_Char_Str_Realiz();" };

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

        ST declaration =
                myGroup.getInstanceOf("class_declaration").add("modifier",
                        "public").add("name", node.getName().getName()).add(
                        "kind", "class");

        myOutermostJavaClass = myGroup.getInstanceOf("facility_class");
        myOutermostJavaClass.add("declaration", declaration);

        myActiveTemplates.push(myOutermostJavaClass);
        myActiveTemplates.peek().add("STDFACS", myHardcodedStdFacs);
    }

    @Override
    public void postFacilityModuleDec(FacilityModuleDec node) {

        String invocationName = null;

        List<OperationEntry> locals =
                myScope.query(new EntryTypeQuery(OperationEntry.class,
                        ImportStrategy.IMPORT_NONE,
                        FacilityStrategy.FACILITY_IGNORE));

        for (OperationEntry o : locals) {
            if (o.getName() == "Main" || o.getName() == "main") {
                invocationName = o.getName();
            }
        }

        if (invocationName == null) {
            throw new NoSuchMethodError("Facility " + node.getName().getName()
                    + " cannot be executed. Specify a main!");
        }

        myOutermostJavaClass.add("invoker", invocationName);
    }

    @Override
    public void preConceptModuleDec(ConceptModuleDec node) {

        addPackageTemplate(node);

        ST extend =
                myGroup.getInstanceOf("class_extends").add("name",
                        "RESOLVE_INTERFACE");

        ST declaration =
                myGroup.getInstanceOf("class_declaration").add("modifier",
                        "public").add("name", node.getName().getName()).add(
                        "kind", "interface").add("extension", extend);

        myOutermostJavaClass = myGroup.getInstanceOf("class");
        myOutermostJavaClass.add("declaration", declaration);

        myActiveTemplates.push(myOutermostJavaClass);
    }

    @Override
    public void preConceptBodyModuleDec(ConceptBodyModuleDec node) {

        addPackageTemplate(node);

        ST extend =
                myGroup.getInstanceOf("class_extends").add("name",
                        "RESOLVE_BASE");
        ST implement =
                myGroup.getInstanceOf("class_implements").add("names",
                        node.getConceptName().getName());

        ST constructor =
                myGroup.getInstanceOf("constructor").add("name",
                        node.getName().getName()).add("modifier",
                        getFunctionModifier());

        ST declaration =
                myGroup.getInstanceOf("class_declaration").add("modifier",
                        "public").add("name", node.getName().getName()).add(
                        "kind", "class").add("extension", extend).add(
                        "implementations", implement);

        myOutermostJavaClass = myGroup.getInstanceOf("class");
        myOutermostJavaClass.add("declaration", declaration);

        myActiveTemplates.push(myOutermostJavaClass);
        myActiveTemplates.peek().add("constructors", constructor);

        List<ProgramParameterEntry> formals =
                getModuleFormalParameters(node.getConceptName());

        for (ProgramParameterEntry p : formals) {
            addParameterTemplate(p.getDeclaredType(), p.getName());
        }
        myActiveTemplates.peek().add("STDFACS", myHardcodedStdFacs);

    }

    @Override
    public void postConceptBodyModuleDec(ConceptBodyModuleDec node) {

        List<ProgramParameterEntry> formals =
                getModuleFormalParameters(node.getConceptName());

        for (ProgramParameterEntry p : formals) {
            ST returnStmt = myGroup.getInstanceOf("return_stmt");

            String prefix =
                    (p.getDeclaredType() instanceof PTElement) ? "getType"
                            : "get";

            ST paramFunction =
                    createOperationLikeTemplate(p.getDeclaredType(),
                            prefix + p.getName(), true).add("stmts",
                            returnStmt.add("name", p.getName()));

            myActiveTemplates.peek().add("functions", paramFunction);
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
        // Note : This seems pretty jank for some reason..
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

            if (!node.externallyRealized()) {
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

        LinkedList<Object> args =
                new LinkedList((List) myBaseInstantiation
                        .getAttribute("arguments"));

        List<ModuleParameterization> enhancements =
                myCurrentFacilityEntry.getEnhancements();

        boolean proxied = myCurrentFacilityEntry.getEnhancements().size() > 1;

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

        // This shouldn't be a problem once expressions are added.
        myActiveTemplates.peek().add("arguments", args);
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

        ST facilityVariable =
                myGroup.getInstanceOf("var_decl").add("type",
                        node.getConceptName().getName()).add("name",
                        node.getName().getName()).add("init",
                        myActiveTemplates.pop());

        myActiveTemplates.peek().add("variables", facilityVariable);

        myDynamicImports.add(myGroup.getInstanceOf("include").add(
                "directories", pathPieces).render());
    }

    @Override
    public void preModuleArgumentItem(ModuleArgumentItem node) {

        PTType type = node.getProgramTypeValue();

        if (type instanceof PTVoid) {
            ST argItem =
                    createOperationArgItemTemplate(
                            (OperationDec) myFacilityBindings.get(node)
                                    .getWrappedDec(), node.getQualifier(), node
                                    .getName());

            myActiveTemplates.peek().add("arguments", argItem);
        }
        else if (type instanceof PTGeneric) {
            myActiveTemplates.peek().add("arguments", node.getName());
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
                    createOperationLikeTemplate(ppe.getDeclaredType(),
                            "getType" + node.getName().getName(), false);

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

        ST getter =
                createOperationLikeTemplate(type, "get" + name, translatingBody);

        getter.add("stmts", myGroup.getInstanceOf("return_stmt").add("name",
                name));

        myActiveTemplates.peek().add("functions", getter);
    }

    @Override
    public void preModuleParameterDec(ModuleParameterDec node) {

        if (node.getWrappedDec() instanceof OperationDec) {

            ST parameter =
                    myGroup.getInstanceOf("parameter").add("type",
                            node.getName().getName()).add("name",
                            node.getName().getName() + "Param");

            ST operationInterface =
                    myGroup.getInstanceOf("class").add(
                            "declaration",
                            myGroup.getInstanceOf("class_declaration").add(
                                    "kind", "interface").add("name",
                                    node.getName().getName()));

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

        ST extend = myGroup.getInstanceOf("class_extends").add("name",
                "RType");

        ST declaration =
                myGroup.getInstanceOf("class_declaration").add("kind",
                        "interface").add("name", node.getName().getName()).add(
                        "extension", extend);

        myActiveTemplates.peek()
                .add(
                        "classes",
                        myGroup.getInstanceOf("class").add("declaration",
                                declaration));

        try {
            ProgramTypeDefinitionEntry ptde =
                    myScope.queryForOne(
                            new UnqualifiedNameQuery(node.getName().getName()))
                            .toProgramTypeDefinitionEntry(node.getLocation());

            ST typeDefinition =
                    createOperationLikeTemplate(ptde.getProgramType(), "create"
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
    public void preRepresentationDec(RepresentationDec node) {

        List<SymbolTableEntry> types =
                myScope.query(new NameQuery(null, node.getName()));

        PTType repType =
                types.get(0).toProgramTypeEntry(node.getLocation())
                        .getProgramType();

        ST recordDeclaration =
                myGroup.getInstanceOf("class_declaration").add("kind",
                        "class");

        recordDeclaration.add("name", node.getName()).add(
                "implementations",
                myGroup.getInstanceOf("class_implements").add("names",
                        getVariableTypeTemplate(repType)));

        myActiveTemplates.push(myGroup.getInstanceOf("record_class").add(
                "declaration", recordDeclaration));
    }

    @Override
    public void postRepresentationDec(RepresentationDec node) {

        ST instance =
                myGroup.getInstanceOf("facility_init").add("realization",
                        node.getName().getName());

        // First pop and insert the record built up from preRepresentationDec
        // to now.
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
                    createOperationLikeTemplate(pte.getProgramType(),
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

        if (completed != myOutermostJavaClass) {
            throw new RuntimeException(
                    "Wrong Template. Make sure intermediate templates are "
                            + "popped in their respective post methods!");
        }
        myActiveTemplates.peek().add("structures", completed);
    }

    //-------------------------------------------------------------------
    //   Helper methods
    //-------------------------------------------------------------------

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
            result.add("concept", concept);
        }
        return result;
    }

    @Override
    protected ST getOperationTypeTemplate(PTType type) {
        // Java function return types happen to look the same as variable
        // return types. So this should be easy.
        return getVariableTypeTemplate(type);
    }

    @Override
    protected ST getParameterTypeTemplate(PTType type) {
        // Ditto with parameters.
        return getVariableTypeTemplate(type);
    }

    @Override
    protected String getFunctionModifier() {
        String modifier = "public";

        if (myScope.getDefiningElement() instanceof FacilityModuleDec) {
            modifier = "public static";
        }

        return modifier;
    }

    /**
     * <p></p>
     * @param operation
     * @param qualifier
     * @param name
     * @return
     */
    private ST createOperationArgItemTemplate(OperationDec operation,
            PosSymbol qualifier, PosSymbol name) {

        int parameterNum = 0;
        ST result =
                myGroup.getInstanceOf("operation_argument_item").add(
                        "actualName", name.getName()).add("actualQualifier",
                        qualifier.getName());

        try {
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
                    createOperationLikeTemplate(returnType, operation.getName()
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
        return result;
    }

    /**
     * <p></p>
     * @param spec
     * @param realiz
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
     * template, as defined in <tt>Base.stg</tt>.
     *
     * @param node The <code>ModuleDec</code> currently being translated.
     */
    public void addPackageTemplate(ModuleDec node) {

        LinkedList<String> pkgDirectories =
                (LinkedList) getPathList(getFile(node, null));

        pkgDirectories.removeLast();
        ST pkg =
                myGroup.getInstanceOf("package").add("directories",
                        pkgDirectories);

        myActiveTemplates.peek().add("directives", pkg);
    }

    public static final void setUpFlags() {
        FlagDependencies.addRequires(JAVA_FLAG_TRANSLATE_CLEAN,
                JAVA_FLAG_TRANSLATE);
    }
}