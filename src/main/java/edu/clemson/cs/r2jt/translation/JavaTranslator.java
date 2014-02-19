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
     * <p>A pointer to Java's outermost class template. This template
     * contains a template, <code>class_declaration</code>, intended to
     * be mutated in module-specific <code>Dec</code> methods.</p>
     */
    private ST myOutermostJavaClass;

    /**
     * <p>A direct pointer to the outermost class declaration template. We
     * don't simply pass around <code>myOutermostJavaClass</code> since
     * accessing its internal <code>decl</code> template would introduce
     * casting.</p>
     */
    private ST myOutermostClassDeclaration;
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

        addPackagePath(node);

        myOutermostClassDeclaration =
                myGroup.getInstanceOf("class_declaration").add("modifier",
                        "public").add("name", node.getName().getName()).add(
                        "kind", "class");

        myOutermostJavaClass = myGroup.getInstanceOf("static_class");
        myOutermostJavaClass.add("decl", myOutermostClassDeclaration);

        myActiveTemplates.push(myOutermostJavaClass);
    }

    @Override
    public void postFacilityModuleDec(FacilityModuleDec node) {

        String invocationName = null;

        List<OperationEntry> locals =
                myModuleScope.query(new EntryTypeQuery(OperationEntry.class,
                        ImportStrategy.IMPORT_NONE,
                        FacilityStrategy.FACILITY_IGNORE));

        for (OperationEntry o : locals) {
            if (o.getName() == "Main" || o.getName() == "main") {
                invocationName = o.getName();
            }
        }

        if (invocationName == null) {
            throw new IllegalStateException("Facility "
                    + node.getName().getName()
                    + " cannot be executed. Specify a main!");
        }

        myOutermostJavaClass.add("invoker", invocationName);
    }

    @Override
    public void preConceptModuleDec(ConceptModuleDec node) {

        ST extend =
                myGroup.getInstanceOf("class_extends").add("name",
                        "RESOLVE_INTERFACE");

        myOutermostClassDeclaration =
                myGroup.getInstanceOf("class_declaration").add("modifier",
                        "public").add("name", node.getName().getName()).add(
                        "kind", "interface").add("extension", extend);

        myOutermostJavaClass = myGroup.getInstanceOf("class");
        myOutermostJavaClass.add("decl", myOutermostClassDeclaration);

        myActiveTemplates.push(myOutermostJavaClass);
    }

    @Override
    public void preConceptBodyModuleDec(ConceptBodyModuleDec node) {

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

        myOutermostClassDeclaration =
                myGroup.getInstanceOf("class_declaration").add("modifier",
                        "public").add("name", node.getName().getName()).add(
                        "kind", "class").add("extension", extend).add(
                        "implementations", implement);

        myOutermostJavaClass = myGroup.getInstanceOf("class");
        myOutermostJavaClass.add("decl", myOutermostClassDeclaration);

        myActiveTemplates.push(myOutermostJavaClass);
        myActiveTemplates.peek().add("constructors", constructor);

        List<ProgramParameterEntry> formals =
                getModuleFormalParameters(node.getConceptName());

        for (ProgramParameterEntry p : formals) {
            addParameterTemplate(p.getDeclaredType(), p.getName());
        }
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
        Scope scopeToSearch = myModuleScope;

        // If we're within a function, get the appropriate scope so we
        // can find the SymbolTableEntry representing this FacilityDec.
        // Note : This seems pretty jank.
        if (!myModuleScope.equals(myBuilder.getScope(this.getAncestor(2)))) {
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
        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(null, node.getName());
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }
    }

    @Override
    public void preEnhancementBodyItem(EnhancementBodyItem node) {

        //    LinkedList<Object> args = new LinkedList((List) myBaseInstantiation
        //            .getAttribute("arguments"));

        boolean proxied = myCurrentFacilityEntry.getEnhancements().size() > 1;

        myActiveTemplates.push(myGroup.getInstanceOf("facility_init"));
        myActiveTemplates.peek().add("isProxied", proxied).add("realization",
                node.getBodyName().getName());

        // This shouldn't be a problem once expressions are added.
        //  myActiveTemplates.peek().add(args);
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
            /*ST operation =
                    myGroup.getInstanceOf("operationParameter").add(
                            "qualifier", node.getQualifier()).add("name",
                            node.getName().getName());*/

            //    myActiveTemplates.peek().add("arguments", "operation");

        }
        else if (type instanceof PTGeneric) {
            myActiveTemplates.peek().add("arguments", node.getName());
        }
        else if (node.getEvalExp() == null) {

            ST arg =
                    myGroup.getInstanceOf("var_init").add("facility",
                            getDefiningFacilityEntry(type).getName()).add(
                            "type", getVariableTypeTemplate(type));

            myActiveTemplates.peek().add("arguments", arg);
        }
    }

    @Override
    public void preTypeDec(TypeDec node) {

        ST extend = myGroup.getInstanceOf("class_extends").add("name", "RType");

        ST interfaceDec =
                myGroup.getInstanceOf("class_declaration").add("kind",
                        "interface").add("name", node.getName().getName()).add(
                        "extension", extend);

        myActiveTemplates.peek().add("classes",
                myGroup.getInstanceOf("class").add("decl", interfaceDec));

        try {
            ProgramTypeDefinitionEntry ptde =
                    myModuleScope.queryForOne(
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
                myModuleScope.query(new NameQuery(null, node.getName()));

        PTType repType =
                types.get(0).toProgramTypeEntry(node.getLocation())
                        .getProgramType();

        ST recordDeclaration =
                myGroup.getInstanceOf("class_declaration").add("kind", "class");

        recordDeclaration.add("name", node.getName()).add(
                "implementations",
                myGroup.getInstanceOf("class_implements").add("names",
                        getVariableTypeTemplate(repType)));

        myActiveTemplates.push(myGroup.getInstanceOf("record_class").add(
                "decl", recordDeclaration));
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
                    myModuleScope.queryForOne(
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

        if (myModuleScope.getDefiningElement() instanceof ConceptBodyModuleDec) {
            ConceptBodyModuleDec thisModule =
                    ((ConceptBodyModuleDec) myModuleScope.getDefiningElement());

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

        ST javaClass = myActiveTemplates.pop();

        if (!javaClass.equals(myOutermostJavaClass)) {
            throw new RuntimeException(
                    "Wrong Template. Make sure intermediate templates are "
                            + "popped in their respective post methods!");
        }
        myActiveTemplates.peek().add("structures", javaClass);
    }

    //-------------------------------------------------------------------
    //   Helper methods
    //-------------------------------------------------------------------

    @Override
    protected ST getVariableTypeTemplate(PTType type) {

        ST result;
        ModuleDec currentModule = myModuleScope.getDefiningElement();
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

        if (myModuleScope.getDefiningElement() instanceof FacilityModuleDec) {
            modifier = "public static";
        }

        return modifier;
    }

    public void addPackagePath(ModuleDec node) {
        LinkedList<String> pkgDirectories =
                (LinkedList) getPathList(getFile(myModuleScope
                        .getDefiningElement(), null));

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