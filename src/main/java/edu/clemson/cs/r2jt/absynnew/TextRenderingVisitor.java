package edu.clemson.cs.r2jt.absynnew;

import edu.clemson.cs.r2jt.absynnew.decl.*;
import edu.clemson.cs.r2jt.absynnew.expr.MathSymbolAST;
import edu.clemson.cs.r2jt.absynnew.expr.ProgLiteralRefAST;
import edu.clemson.cs.r2jt.absynnew.expr.ProgNameRefAST;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Useful for printing out a text representation of arbitrarily complicated
 * asts.</p>
 * 
 * <p>This renderer makes no modifications to the tree. We merely walk an
 * existing tree, annotating {@link ResolveAST}s on the <tt>post</tt> traversal
 * with their corresponding, filled in {@link ST} representations. The
 * <tt>post</tt> traversal is important as it guarantees that all subtrees of
 * the current node will have already been visited and hence recieved their own
 * appropriately filled-in {@link ST}s prior to processing the current
 * <tt>post</tt> node.</p>
 *
 * <p>It's worth mentioning that the text produced by this class will
 * <strong>not</strong> be a one-to-one match with that contained in the
 * <code>AntlrTokenStream</code>. This class operates on asts, not parse-trees.
 * Thus, some information present in the original source is (rightly) not
 * reflected in <tt>RESOLVE</tt>'s ast.</p>
 *
 * <p>Clients can retrieve the collection of templates built by this class via a
 * call to {@link #getTemplates}.</p>
 */
public class TextRenderingVisitor extends TreeWalkerVisitor {

    private final Map<ResolveAST, ST> myVisited = new HashMap<ResolveAST, ST>();

    private final STGroup myTemplates = new STGroupFile(
            "edu/clemson/cs/r2jt/templates/Resolve.stg");

    @Override
    public void postFacilityAST(ModuleAST.FacilityAST e) {
        setST(e,
                getTemplate(e).add("block", getST(e.getBodyBlock()))
                        .add("uses", getST(e.getImportBlock()))
                        .add("facility", e)
                        .add("requires", getST(e.getRequires())));
    }

    @Override
    public void postImportBlockAST(ImportBlockAST e) {
        setST(e,
                getTemplate(e).add("explicits",
                        e.getImportsOfType(ImportBlockAST.ImportType.EXPLICIT)));
    }

    @Override
    public void postConceptAST(ModuleAST.ConceptAST e) {
        setST(e,
                getTemplate(e).add("parameters", collect(e.getParameters()))
                        .add("uses", getST(e.getImportBlock()))
                        .add("concept", e)
                        .add("block", getST(e.getBodyBlock())));
    }

    @Override
    public void postModuleBlockAST(ModuleBlockAST e) {
        setST(e, getTemplate(e).add("elements", collect(e.getElements())));
    }

    @Override
    public void postOperationSigAST(OperationSigAST e) {
        ST operation =
                getTemplate(e).add("parameters", collect(e.getParameters()))
                        .add("requires", getST(e.getRequires()))
                        .add("ensures", getST(e.getEnsures()))
                        .add("operation", e);
        setST(e, operation);
    }

    @Override
    public void postOperationImplAST(OperationImplAST e) {
        ST operation =
                getTemplate(e).add("parameters", collect(e.getParameters()))
                        .add("requires", e.getRequires()).add("operation", e)
                        .add("ensures", e.getEnsures())
                        .add("variables", e.getVariables());
        setST(e, operation);
    }

    @Override
    public void postModuleParameterAST(ModuleParameterAST e) {
        setST(e, getTemplate(e).add("parameter", e));
    }

    @Override
    public void postParameterAST(ParameterAST e) {
        ST parameter =
                getTemplate(e).add("type", getST(e.getType())).add("parameter",
                        e);
        setST(e, parameter);
    }

    @Override
    public void postTypeParameterAST(TypeParameterAST e) {
        setST(e, getTemplate(e).add("parameter", e));
    }

    @Override
    public void postTypeModelAST(TypeModelAST e) {
        ST family =
                getTemplate(e).add("model", getST(e.getModel()))
                        .add("constraint", getST(e.getConstraint()))
                        .add("init", getST(e.getInitialization()))
                        .add("final", getST(e.getFinalization()))
                        .add("type", e);
        setST(e, family);
    }

    @Override
    public void postTypeInitAST(InitFinalAST.TypeInitAST e) {
        setST(e,
                getTemplate(e).add("requires", getST(e.getRequires())).add(
                        "ensures", getST(e.getEnsures())));
    }

    @Override
    public void postTypeFinalAST(InitFinalAST.TypeFinalAST e) {
        setST(e,
                getTemplate(e).add("requires", getST(e.getRequires())).add(
                        "ensures", getST(e.getEnsures())));
    }

    @Override
    public void postMathSymbolAST(MathSymbolAST e) {
        setST(e, getTemplate(e).add("arguments", collect(e.getArguments()))
                .add("expr", e));
    }

    @Override
    public void postTypeAST(TypeAST e) {
        setST(e, getST(e));
    }

    @Override
    public void postNamedTypeAST(NamedTypeAST e) {
        setST(e, getTemplate(e).add("type", e));
    }

    @Override
    public void postMathTypeAST(MathTypeAST e) {
        ST type = getTemplate(e).add("expr", getST(e.getArbitraryTypeExpr()));
        setST(e, type);
    }

    /*
     * @Override public void postFacilityDeclAST(@Nonnull FacilityDeclAST e) {
     * ST facility =
     * getTemplate(e)
     * .add("facility", e)
     * .add("pair", e.getPairing())
     * .add("enhancements", collect(e.getPairedEnhancements()));
     * setST(e, facility);
     * }
     *
     * @Override public void postPairedEnhancementAST(
     *
     * @Nonnull FacilityDeclAST.PairedEnhancementAST e) {
     * setST(e,
     * getTemplate(e).add("enhancement", e).add("pair",
     * getST(e.getPairing())));
     * }
     *
     * @Override public void postSpecBodyPairAST(
     *
     * @Nonnull FacilityDeclAST.SpecBodyPairAST e) {
     * setST(e, getTemplate(e).add("pair", e).add("spec", getST(e.getSpec()))
     * .add("body", e.getBody()));
     * }
     *
     * @Override public void postModuleParameterizationAST(
     *
     * @Nonnull FacilityDeclAST.ModuleParameterizationAST e) {
     * setST(e,
     * getTemplate(e).add("parameterization", e).add("arguments",
     * collect(e.getArguments())));
     * }
     *
     * @Override public void postModuleArgAST(FacilityDeclAST.ModuleArgAST e) {
     * setST(e,
     * getTemplate(e).add("moduleArg", e).add("argument",
     * getST(e.getArgument())));
     * }
     */

    @Override
    public void postProgNameRefAST(ProgNameRefAST e) {
        setST(e, getTemplate(e).add("nameRef", e));
    }

    @Override
    public void postProgStringRefAST(ProgLiteralRefAST.ProgStringRefAST e) {
        setST(e, getTemplate(e).add("stringRef", e));
    }

    @Override
    public void postProgIntegerRefAST(ProgLiteralRefAST.ProgIntegerRefAST e) {
        setST(e, getTemplate(e).add("integerRef", e));
    }

    @Override
    public void
            postProgCharacterRefAST(ProgLiteralRefAST.ProgCharacterRefAST e) {
        setST(e, getTemplate(e).add("characterRef", e));
    }

    /*
     * @Override public void postProgCallRefAST(
     * ProgExprAST.ProgCallRefAST e) {
     * ST call = getTemplate(e).add("callRef", e)
     * .add("arguments", collect(e.getArguments()));
     * setST(e, call);
     * }
     */

    private void setST(ResolveAST ast, ST st) {
        myVisited.put(ast, st);
    }

    private ST getST(ResolveAST ast) {
        return myVisited.get(ast);
    }

    private ST getTemplate(ResolveAST ast) {
        Class<? extends ResolveAST> cl = ast.getClass();
        String templateName = cl.getSimpleName();

        if (!myTemplates.isDefined(templateName)) {
            return new ST("[" + templateName + " invalid]");
        }
        return myTemplates.getInstanceOf(templateName);
    }

    public Map<ResolveAST, ST> getTemplates() {
        return myVisited;
    }

    /**
     * <p>Collects and returns a list of all existing {@link ST}s for the ast
     * nodes present in <code>nodeChildren</code>.</p>
     *
     * @param nodeChildren  A list of <code>ResolveAST</code>s whose
     *      {@link ST}s we want.
     *
     * @return  All {@link ST}s corresponding to the nodes contained in
     *              <code>nodeChildren</code>.
     */
    private List<ST> collect(List<? extends ResolveAST> nodeChildren) {
        List<ST> result = new ArrayList<ST>();

        for (ResolveAST element : nodeChildren) {
            if (myVisited.get(element) != null) {
                result.add(myVisited.get(element));
            }
        }
        return result;
    }
}
