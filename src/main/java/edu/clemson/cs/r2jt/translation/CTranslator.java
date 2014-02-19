package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.typeandpopulate.*;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTElement;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameAndEntryTypeQuery;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;
import org.stringtemplate.v4.*;

import java.util.LinkedList;
import java.util.List;

public class CTranslator extends AbstractTranslator {

    private static final String FLAG_SECTION_NAME = "C Translation";

    private static final String FLAG_DESC_TRANSLATE =
            "Translate RESOLVE code to C.";

    private static final String FLAG_DESC_TRANSLATE_CLEAN =
            "Regenerates C code for all supporting RESOLVE files.";

    /**
     * <p>The main translator flag.  Tells the compiler convert
     * RESOLVE source code to Java source code.</p>
     */
    public static final Flag C_FLAG_TRANSLATE =
            new Flag(FLAG_SECTION_NAME, "cTranslate", FLAG_DESC_TRANSLATE);

    /**
     * <p>Tells the compiler to regenerate C code for all
     * supporting RESOLVE source files.</p>
     */
    public static final Flag C_FLAG_TRANSLATE_CLEAN =
            new Flag(FLAG_SECTION_NAME, "cTranslateClean",
                    FLAG_DESC_TRANSLATE_CLEAN);

    /**
     * <p>A list of templates corresponding to global facility instantiations.
     * These are intended to be added to the <code>variables</code> attribute
     * of each <code>function</code> template.</p>
     */
    List<ST> myFacilityInstantiations = new LinkedList<ST>();

    /**
     * <p>While we walk a <code>FacilityDec</code> and its children,
     * this maintains a pointer to its corresponding <code>ST</code> C-struct
     * representation.</p>
     */
    ST myCurrentStruct = null;

    public CTranslator(CompileEnvironment env, ScopeRepository repo) {
        super(env, repo);
        myGroup = new STGroupFile("templates/C.stg");
    }

    //-------------------------------------------------------------------
    //   Visitor methods
    //-------------------------------------------------------------------

    @Override
    public void preConceptModuleDec(ConceptModuleDec node) {

        addPreprocessorDirective(node.getName().getName());

        ST conceptStruct =
                myGroup.getInstanceOf("struct").add("name",
                        node.getName().getName());

        myActiveTemplates.push(conceptStruct);

        ST realizSpecific =
                myGroup.getInstanceOf("parameter").add("type", "void*").add(
                        "name", "realization_specific");

        myActiveTemplates.peek().add("params", realizSpecific);
    }

    @Override
    public void postConceptModuleDec(ConceptModuleDec node) {

        ST module = myActiveTemplates.pop();
        myActiveTemplates.peek().add("structures", module);
    }

    @Override
    public void preConceptBodyModuleDec(ConceptBodyModuleDec node) {

        String name = node.getName().getName();
        String conceptName = node.getConceptName().getName();

        List<ProgramParameterEntry> formals =
                getModuleFormalParameters(node.getConceptName());

        ST realizationHeader =
                myGroup.getInstanceOf("concept_realization_header").add(
                        "module", name).add("concept", conceptName);

        for (ProgramParameterEntry p : formals) {
            ST parameter =
                    myGroup.getInstanceOf("parameter").add("type",
                            getParameterTypeTemplate(p.getDeclaredType())).add(
                            "name", p.getName());

            realizationHeader.add("parameters", parameter);
        }

        myActiveTemplates.peek().add("functions", realizationHeader);
    }

    @Override
    public void preRepresentationDec(RepresentationDec node) {

        String conceptName =
                ((ConceptBodyModuleDec) myModuleScope.getDefiningElement())
                        .getConceptName().getName();

        ST initAndFinal =
                myGroup.getInstanceOf("init_and_final").add("typename",
                        node.getName().getName()).add("concept", conceptName);

        initAndFinal.add("facilities", myFacilityInstantiations);
        myActiveTemplates.push(initAndFinal);
    }

    @Override
    public void postRepresentationDec(RepresentationDec node) {

        ST initAndFinal = myActiveTemplates.pop();
        myActiveTemplates.peek().add("functions", initAndFinal);
    }

    @Override
    public void preFacilityDec(FacilityDec node) {

        String conceptName = node.getConceptName().getName();
        String realiz = node.getBodyName().getName() + "_for_" + conceptName;

        try {
            myCurrentFacilityEntry =
                    myModuleScope.queryForOne(
                            new NameAndEntryTypeQuery(null, node.getName(),
                                    FacilityEntry.class,
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_IGNORE, false))
                            .toFacilityEntry(node.getLocation());

            myCurrentStruct =
                    myGroup.getInstanceOf("struct").add("name",
                            node.getName().getName());

            ST field =
                    myGroup.getInstanceOf("struct_field").add("type",
                            conceptName).add("name", "core").add("realization",
                            realiz);

            myCurrentStruct.add("variables", field);
            myActiveTemplates.push(myCurrentStruct);
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
    public void preEnhancementBodyItem(EnhancementBodyItem node) {

        String enhancementType =
                node.getName().getName()
                        + "_for_"
                        + myCurrentFacilityEntry.getFacility()
                                .getSpecification().getModuleIdentifier()
                                .toString();
        String realiz =
                node.getBodyName().getName() + "_for_" + enhancementType;

        ST field =
                myGroup.getInstanceOf("struct_field").add("type",
                        enhancementType).add("name", node.getName().getName())
                        .add("realization", realiz);

        myCurrentStruct.add("variables", field);
        myActiveTemplates.push(field);
    }

    @Override
    public void postEnhancementBodyItem(EnhancementBodyItem node) {

        //        ST coreParameter =
        //                myGroup.getInstanceOf("qualified_identifier").add
        // ("qualifier",
        //                       myCurrentFacilityEntry.getName()).add("name",
        // "core");

        //        myActiveTemplates.peek().add("arguments", coreParameter);
        myActiveTemplates.pop();
    }

    /*@Override
    public void preOperationDec(OperationDec node) {
        String operationName = "(*" + node.getName().getName() + ")";
        ST operation =
                createOperationLikeTemplate((node.getReturnTy() != null) ? node
                        .getReturnTy().getProgramTypeValue() : null,
                        operationName, false);

        myActiveTemplates.push(operation);
    }*/

    /*@Override
    public void postOperationDec(OperationDec node) {
        ST operation = myActiveTemplates.pop();

        // TODO : Add Struct parameter to every function here...
        //    ST conceptStruct = myGroup.getInstanceOf("unqualified_type").add
        //            ("name", myModuleScope)
        if (myActiveTemplates.size() > 1) {
            // If we're translating a concept, then our stack should be two
            // deep since every variable ('operation') is added to a
            // type-def'ed struct.
            myActiveTemplates.peek().add("variables", operation);
        }
        else {
            myActiveTemplates.peek().add("functions", operation);
        }
    }*/

    @Override
    public void postFacilityDec(FacilityDec node) {

        ST assignment =
                myGroup.getInstanceOf("facility_assignment").add("struct",
                        myCurrentStruct);

        myFacilityInstantiations.add(assignment);

        myActiveTemplates.pop();
        myActiveTemplates.peek().add("structures", myCurrentStruct);
    }

    //-------------------------------------------------------------------
    //   Helper methods
    //-------------------------------------------------------------------

    @Override
    protected ST getVariableTypeTemplate(PTType type) {

        ST result = myGroup.getInstanceOf("unqualified_type");

        if (type instanceof PTElement) {
            result.add("name", "type_info*");
        }
        else {
            result.add("name", getTypeName(type));
        }
        return result;
    }

    @Override
    protected ST getOperationTypeTemplate(PTType type) {
        ST result = myGroup.getInstanceOf("unqualified_type");
        if (type instanceof PTElement) {
            result.add("name", "type_info*");
        }
        else {
            result.add("name", "r_type_ptr");
        }
        return result;
    }

    @Override
    protected ST getParameterTypeTemplate(PTType type) {
        return getOperationTypeTemplate(type);
    }

    @Override
    protected String getFunctionModifier() {

        String modifier = null;

        if (myModuleScope.getDefiningElement() instanceof ConceptBodyModuleDec) {
            modifier = "static";
        }

        return modifier;
    }

    /**
     * <p>Inserts preprocessor directives at the top-of (and end-of) the file
     * undergoing translation. Note that these directives only apply to
     * <strong>C</strong> header files.</p>
     *
     * @param moduleName Influences the name given to the directive. For ex:
     *                   <tt>Integer_Template</tt>-><tt>_INTEGER_TEMPLATE_H</tt>
     */
    private void addPreprocessorDirective(String moduleName) {

        moduleName = "__" + moduleName.toUpperCase() + "_H";

        ST openingDirective =
                myGroup.getInstanceOf("macro_define").add("name", moduleName);
        ST closingDirective = myGroup.getInstanceOf("macro_endif");

        if (myActiveTemplates.isEmpty()) {
            throw new IllegalStateException("Translation attempting to "
                    + " add macros to an empty template stack!");
        }

        myActiveTemplates.firstElement().add("directives", openingDirective)
                .add("eof", closingDirective);
    }

    public static final void setUpFlags() {
        FlagDependencies.addRequires(C_FLAG_TRANSLATE_CLEAN, C_FLAG_TRANSLATE);
    }
}