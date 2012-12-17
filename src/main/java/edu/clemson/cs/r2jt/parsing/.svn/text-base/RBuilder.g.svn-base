
tree grammar RBuilder;

options {
    k = 1;
    output = AST;      
    tokenVocab=RParser;             
    ASTLabelType = 'ColsAST';
    //importVocab=RESOLVE;
    }

@header {
    package edu.clemson.cs.r2jt.parsing;
    
    import org.antlr.*;
    import edu.clemson.cs.r2jt.absyn.*;
    import edu.clemson.cs.r2jt.data.*;
    import edu.clemson.cs.r2jt.errors.ErrorHandler;
    import edu.clemson.cs.r2jt.collections.Iterator;
    import edu.clemson.cs.r2jt.type.Type;
    import edu.clemson.cs.r2jt.type.IsInType;
    import edu.clemson.cs.r2jt.type.BooleanType;
    //import edu.clemson.cs.r2jt.collections.List;
}

// ===============================================================
// Java Declarations
// ===============================================================

@members{
    /**
     * Variables to tell us what type of module we are
     * parsing.  Used for semantic predicates of rules or productions
     * which are only applicable to particular modules.
     */
    boolean proofModule = false;
    boolean theoryModule = false;
    boolean conceptModule = false;
    boolean performanceModule = false;
    boolean headerModule = false;
    boolean bodyModule = false;
    boolean enhancementModule = false;
    boolean facilityModule = false;
    boolean enhancementBody = false;

    /* enhancementBody is a subclass of bodyModule.  It is only true
     * in the body of an enhancement module.  It is NOT true in a
     * "bundled implementation" module (a body that implements both a
     * concept and one or more enhancements at once). (BM)
     */

    /**
     * Reset the type of module we are parsing.
     */
    public void resetModuleType() {
        this.theoryModule = false;
        this.conceptModule = false;
        this.performanceModule = false;
        this.headerModule = false;
        this.bodyModule = false;
        this.enhancementModule = false;
        this.facilityModule = false;
        this.enhancementBody = false;
    }

    /** The error handler for this parser. */
    private ErrorHandler err;
    //private ErrorHandler err = ErrorHandler.getInstance();

    /** Delegate the error handling to the error handler. */
    public void reportError(RecognitionException ex) {
        System.out.println(getErrorMessage(ex, null));
        err.syntaxError(ex);
    }

    /** Delegate the warning handling to the error handler. */
    public void reportWarning(String s) {
        err.warning(s);
    }

//      private PosSymbol getQualifier(edu.clemson.cs.r2jt.collections.List<PosSymbol> psyms) {
//          PosSymbol qual = null;
//          switch (psyms.size()) {
//          case 1: qual = null; break;
//          case 2: qual = psyms.get(0); break;
//          default: assert false : "qual is invalid";
//          }
//          return qual;
//      }

//      private PosSymbol getName(edu.clemson.cs.r2jt.collections.List<PosSymbol> psyms) {
//          PosSymbol name = null;
//          switch (psyms.size()) {
//          case 1: name = psyms.get(0); break;
//          case 2: name = psyms.get(1); break;
//          default: assert false : "psyms is invalid";
//          }
//          return name;
//      }

    private Pos getPos(ColsAST ast) {
        return new Pos(ast.getLine(), ast.getCharPositionInLine());
    }

    private Location getLocation(ColsAST ast) {
        return new Location(err.getFile(), getPos(ast));
    }

    private Location getLocation(Pos pos) {
        return new Location(err.getFile(), pos);
    }

    private Symbol getSymbol(ColsAST ast) {
        return Symbol.symbol(ast.getText());
    }

    private Pos getASTPos(ColsAST ast) {
        return new Pos(ast.getLine(), ast.getCharPositionInLine());
    }
    
    private Symbol getASTSymbol(ColsAST ast) {
        return Symbol.symbol(ast.getText());
    }

    private PosSymbol getOutfixPosSymbol(ColsAST ast) {
        Pos pos = new Pos(ast.getLine(), ast.getCharPositionInLine());
        Location loc = new Location(err.getFile(), pos);
        String str = ast.getText();
        Symbol name = null;
        if (str.equals("<")) {
            name = Symbol.symbol("<_>");
        } else if (str.equals("<<")) {
            name = Symbol.symbol("<<_>>");
        } else if (str.equals("|")) {
            name = Symbol.symbol("|_|");
        } else if (str.equals("||")) {
            name = Symbol.symbol("||_||");
        } else if (str.equals("[")) {
          name = Symbol.symbol("[_]");
        }
        else if (str.equals("[[")) {
          name = Symbol.symbol("[[_]]");
        } else {
            assert false : "invalid symbol: " + str;
        }
        return new PosSymbol(loc, name);
    }

    private PosSymbol getPosSymbol(ColsAST ast) {
        Pos pos = new Pos(ast.getLine(), ast.getCharPositionInLine());
        Location loc = new Location(err.getFile(), pos);
        Symbol sym = Symbol.symbol(ast.getText());
        return new PosSymbol(loc, sym);
    }

    private edu.clemson.cs.r2jt.collections.List<ParameterVarDec> getParamVarDecList(Mode mode,
            edu.clemson.cs.r2jt.collections.List<VarDec> vars)
    {
        edu.clemson.cs.r2jt.collections.List<ParameterVarDec> pVars
            = new edu.clemson.cs.r2jt.collections.List<ParameterVarDec>("ParameterVarDec");
        Iterator<VarDec> i = vars.iterator();
        while (i.hasNext()) {
            VarDec var = i.next();
            ParameterVarDec pVar = new ParameterVarDec(mode,
                var.getName(), var.getTy());
            pVars.add(pVar);
        }
        return pVars;
    }

    private InitItem getInitItem(Location loc, InitItem init) {
        return new InitItem(
            loc,
            init.getStateVars(),
            init.getRequires(),
            init.getEnsures(),
            init.getFacilities(),
            init.getVariables(),
            init.getAuxVariables(),
            init.getStatements()
        );
    }

    private FinalItem getFinalItem(Location loc, InitItem init) {
        return new FinalItem(
            loc,
            init.getStateVars(),
            init.getRequires(),
            init.getEnsures(),
            init.getFacilities(),
            init.getVariables(),
            init.getAuxVariables(),
            init.getStatements()
        );
    }

    private PerformanceInitItem getPerformanceInitItem(Location loc, PerformanceInitItem init) {
        return new PerformanceInitItem(
            loc,
            init.getStateVars(),
            init.getRequires(),
            init.getEnsures(),
            init.getDuration(),
            init.getMainp_disp(),
            init.getFacilities(),
            init.getVariables(),
            init.getAuxVariables(),
            init.getStatements()
        );
    }

    private PerformanceFinalItem getPerformanceFinalItem (Location loc, PerformanceFinalItem Final) {
        return new PerformanceFinalItem (
            loc,
            Final.getStateVars(),
            Final.getRequires(),
            Final.getEnsures(),
            Final.getDuration(),
            Final.getMainp_disp(),
            Final.getFacilities(),
            Final.getVariables(),
            Final.getAuxVariables(),
            Final.getStatements()
        );
    }
    
    private edu.clemson.cs.r2jt.collections.List<VarDec> getVarDecList(edu.clemson.cs.r2jt.collections.List<PosSymbol> psyms, Ty ty) {
        edu.clemson.cs.r2jt.collections.List<VarDec> vars = new edu.clemson.cs.r2jt.collections.List<VarDec>("VarDec");
        Iterator<PosSymbol> i = psyms.iterator();
        while (i.hasNext()) {
            PosSymbol ps = i.next();
            VarDec var = new VarDec(ps, ty);
            vars.add(var);
        }
        return vars;
    }
    
  private edu.clemson.cs.r2jt.collections.List<AuxVarDec> getAuxVarDecList(edu.clemson.cs.r2jt.collections.List<PosSymbol> psyms, Ty ty) {
        edu.clemson.cs.r2jt.collections.List<AuxVarDec> vars = new edu.clemson.cs.r2jt.collections.List<AuxVarDec>("AuxVarDec");
        Iterator<PosSymbol> i = psyms.iterator();
        while (i.hasNext()) {
            PosSymbol ps = i.next();
            AuxVarDec var = new AuxVarDec(ps, ty);
            vars.add(var);
        }
        return vars;
    }

    private edu.clemson.cs.r2jt.collections.List<MathVarDec> getMathVarDecList(edu.clemson.cs.r2jt.collections.List<PosSymbol> psyms, Ty ty) {
        edu.clemson.cs.r2jt.collections.List<MathVarDec> vars = new edu.clemson.cs.r2jt.collections.List<MathVarDec>("MathVarDec");
        Iterator<PosSymbol> i = psyms.iterator();
        while (i.hasNext()) {
            PosSymbol ps = i.next();
            MathVarDec var = new MathVarDec(ps, ty);
            vars.add(var);
        }
        return vars;
    }

    private int getIterativeOp (PosSymbol ps) {
        Symbol sym = ps.getSymbol();
        int op = 0;
        if (sym == Symbol.symbol("Sum")) {
            op = IterativeExp.SUM;
        } else if (sym == Symbol.symbol("Product")) {
            op = IterativeExp.PRODUCT;
        } else if (sym == Symbol.symbol("Concatenation")) {
            op = IterativeExp.CONCATENATION;
        } else if (sym == Symbol.symbol("Intersection")) {
            op = IterativeExp.INTERSECTION;
        } else if (sym == Symbol.symbol("Union")) {
            op = IterativeExp.UNION;
        } else {
            assert false : "Invalid symbol: " + sym;
        }
        return op;
    }

    private ProgramExp getProgramLiteral(Exp mlit) {
        if (mlit instanceof IntegerExp) {
            return new ProgramIntegerExp(
                ((IntegerExp)mlit).getLocation(),
                ((IntegerExp)mlit).getValue());
        } else if (mlit instanceof DoubleExp) {
            return new ProgramDoubleExp(
                ((IntegerExp)mlit).getLocation(),
                ((IntegerExp)mlit).getValue());
        } else if (mlit instanceof CharExp) {
            return new ProgramCharExp(
                ((CharExp)mlit).getLocation(),
                ((CharExp)mlit).getValue());
        } else if (mlit instanceof StringExp) {
            return new ProgramStringExp(
                ((StringExp)mlit).getLocation(),
                ((StringExp)mlit).getValue());
        } else {
            assert false : "Invalid expression type";
            return null;
        }
    }
    
    public String getErrorMessage(RecognitionException e,
        String[] tokenNames)
    {
        System.out.println("Builder Exception:");
        List stack = (List)getRuleInvocationStack(e, this.getClass().getName());
        String msg = null;
        if ( e instanceof NoViableAltException ) {
          NoViableAltException nvae = (NoViableAltException)e;
          msg = " no viable alt; token="+e.token+
          " (decision="+nvae.decisionNumber+
          " state "+nvae.stateNumber+")"+
          " decision=<<"+nvae.grammarDecisionDescription+">>";
        }
        else {
          msg = super.getErrorMessage(e, RBuilder.tokenNames);
        }
        return stack+" "+msg+"\n"+e.token;
        //return msg;
    }
    public String getTokenErrorDisplay(Token t) {
        return t.toString();
    }

}

// ===============================================================
// Production rules for Resolve modules
// ===============================================================

module [ErrorHandler err] returns [ModuleDec dec = null]
@init{
this.err = err;
}
    :   (   { proofModule = true; }       dec1=proof_module
            { $dec = $dec1.pmd; }
        |   { theoryModule = true; }      dec2=theory_module
            { $dec = $dec2.dec; } 
        |   { conceptModule = true; }     dec3=conceptual_module
            { $dec = $dec3.dec; } 
        |   { bodyModule = true; }        dec4=realization_body_module
            { $dec = $dec4.dec; } 
        |   { enhancementModule = true; } dec5=enhancement_module 
            { $dec = $dec5.dec; }
        |   { facilityModule = true; }    dec6=facility_module
            { $dec = $dec6.dec; } 
        |   { performanceModule = true; }    dec7=performance_module
            { $dec = $dec7.dec; } 
        )
    ;

// ---------------------------------------------------------------
//  Theory Module
// ---------------------------------------------------------------

theory_module returns [MathModuleDec dec = null]
@init{
    edu.clemson.cs.r2jt.collections.List<ModuleParameterDec> pars = new edu.clemson.cs.r2jt.collections.List<ModuleParameterDec>("ModuleParameterDec");
    edu.clemson.cs.r2jt.collections.List<UsesItem> uses = new edu.clemson.cs.r2jt.collections.List<UsesItem>("UsesItem");
    edu.clemson.cs.r2jt.collections.List<Dec> decs = new edu.clemson.cs.r2jt.collections.List<Dec>("Dec");
}
    :   ^(  THEORY ps=ident
            (pars2=module_formal_param_section)?
            (uses2=uses_list)?
            (dec2=math_item_sequence)?
        )
        {   if ($dec2.dec != null) {
                decs = $dec2.dec.getDecs();
            }
            $dec = new MathModuleDec($ps.ps,
                                      $pars2.pars!=null?$pars2.pars:pars,
                                      $uses2.uses!=null?$uses2.uses:uses,
                                      decs);
        }
    ;

  //math_item_sequence
  //    :   (math_item)+
  //    ;

math_item_sequence returns [MathModuleDec dec = null]
@init{   
    PosSymbol ps = null; //dummy
    edu.clemson.cs.r2jt.collections.List<ModuleParameterDec> pars = null; //dummy
    edu.clemson.cs.r2jt.collections.List<UsesItem> uses = null; //dummy
    edu.clemson.cs.r2jt.collections.List<Dec> decs = new edu.clemson.cs.r2jt.collections.List<Dec>("Dec");
}
    :   (   dec4=definition_declaration { decs.add($dec4.dec); }
        |   dec5=categorical_definition_declaration { decs.add($dec5.dec); }
        |   dec6=math_assertion_declaration { decs.add($dec6.dec); }
        |   dec8=type_theorem_declaration { decs.add($dec8.dec); }
        )+
        { $dec = new MathModuleDec(ps, pars, uses, decs); }
    ;

// ---------------------------------------------------------------
// Concept Module
// ---------------------------------------------------------------

conceptual_module returns [ConceptModuleDec dec = null]
@init{
    edu.clemson.cs.r2jt.collections.List<Exp> cons = new edu.clemson.cs.r2jt.collections.List<Exp>("Exp");
    InitItem init = null;
    FinalItem fin = null;
    edu.clemson.cs.r2jt.collections.List<Dec> decs = new edu.clemson.cs.r2jt.collections.List<Dec>("Dec");
    edu.clemson.cs.r2jt.collections.List<ModuleParameterDec> pars = new edu.clemson.cs.r2jt.collections.List<ModuleParameterDec>("ModuleParameterDec");
    edu.clemson.cs.r2jt.collections.List<UsesItem> uses = new edu.clemson.cs.r2jt.collections.List<UsesItem>("UsesItem");
}
    :   ^(  MODULE_CONCEPT ps=ident
            (pars2=module_formal_param_section)?
            (uses2=uses_list)?
            (req=requires_clause)?
            (dec2=concept_item_sequence)?
        )
        {   if ($dec2.dec != null) {
                cons = $dec2.dec.getConstraints();
                init = $dec2.dec.getFacilityInit();
                fin = $dec2.dec.getFacilityFinal();
                decs = $dec2.dec.getDecs();
            }
            $dec = new ConceptModuleDec($ps.ps,
                                        $pars2.pars!=null?$pars2.pars:pars,
                                        $uses2.uses!=null?$uses2.uses:uses,
                                        $req.exp, cons,
                                        init, fin, decs);
        }
    ;

  //concept_item_sequence
  //    :   (concept_item)+
  //    ;

concept_item_sequence returns [ConceptModuleDec dec = null]
@init{
    PosSymbol ps = null; //dummy
    edu.clemson.cs.r2jt.collections.List<ModuleParameterDec> pars = null; //dummy
    edu.clemson.cs.r2jt.collections.List<UsesItem> uses = null; //dummy
    Exp req = null; //dummy
    edu.clemson.cs.r2jt.collections.List<Exp> cons = new edu.clemson.cs.r2jt.collections.List<Exp>("Exp");
    edu.clemson.cs.r2jt.collections.List<Dec> decs = new edu.clemson.cs.r2jt.collections.List<Dec>("Dec");
}
    :   (   dec2=confirm_math_type_declaration { decs.add($dec2.mvd1); }
        |   decs2=concept_state_variable_declaration { decs.addAll($decs2.decs); }
        |   exp2=constraint_clause { cons.add($exp2.exp); }
        |   init=module_concept_init_declaration
        |   fin=module_concept_final_declaration
        |   dec3=type_declaration { decs.add($dec3.dec); }
        |   dec4=operation_declaration { decs.add($dec4.dec); }
        |   dec5=definition_declaration { decs.add($dec5.dec); }
        |   dec6=defines_declaration { decs.add($dec6.dec); }
        )+
        {   $dec = new ConceptModuleDec(ps, pars, uses, req, cons,
                $init.item, $fin.item, decs);
        }
    ;

// ---------------------------------------------------------------
// Performance  Module
// ---------------------------------------------------------------

performance_module returns [PerformanceModuleDec dec = null]
@init{
    edu.clemson.cs.r2jt.collections.List<Exp> cons = new edu.clemson.cs.r2jt.collections.List<Exp>("Exp");
    InitItem init = null;
    FinalItem fin = null;
    PerformanceInitItem perfInit = null;
    PerformanceFinalItem perfFinal = null;
    edu.clemson.cs.r2jt.collections.List<Dec> decs = new edu.clemson.cs.r2jt.collections.List<Dec>("Dec");
    edu.clemson.cs.r2jt.collections.List<ModuleParameterDec> pars = new edu.clemson.cs.r2jt.collections.List<ModuleParameterDec>("ModuleParameterDec");
    edu.clemson.cs.r2jt.collections.List<UsesItem> uses = new edu.clemson.cs.r2jt.collections.List<UsesItem>("UsesItem");
}
    :   ^(  MODULE_PROFILE pn1=ident
            (pars2=module_formal_param_section)?
             pn2=ident
             ps1=ident
             (ps2=ident ps3=ident)?
            (uses2=uses_list)?
            (req=requires_clause)?
            (dec2=performance_item_sequence)?
        )
        {   if ($dec2.dec != null) {
                cons = $dec2.dec.getConstraints();
                perfInit = $dec2.dec.getPerfInit();
                perfFinal = $dec2.dec.getPerfFinal();                
                init = $dec2.dec.getFacilityInit();
                fin = $dec2.dec.getFacilityFinal();
                decs = $dec2.dec.getDecs();
            }
            $dec = new PerformanceModuleDec($pn1.ps,
                                        $pars2.pars!=null?$pars2.pars:pars,
                                        $pn2.ps,
                                        $ps1.ps,
                                        $uses2.uses!=null?$uses2.uses:uses,
                                        $req.exp, cons,
                                        perfInit, perfFinal, 
                                        init, fin, decs);
        }
    ;

 // performance_item_sequence
 //     :   (performance_item)+
 //     ;
 
performance_item_sequence returns [PerformanceModuleDec  dec = null]
@init{
    PosSymbol ps = null; //dummy
    PosSymbol pn1 = null; //dummy
    PosSymbol pn2 = null; //dummy
    PosSymbol ps3 = null; //dummy
    PosSymbol ps4 = null; //dummy

    edu.clemson.cs.r2jt.collections.List<ModuleParameterDec> pars = null; //dummy
    edu.clemson.cs.r2jt.collections.List<UsesItem> uses = null; //dummy
    Exp req = null; //dummy
    edu.clemson.cs.r2jt.collections.List<Exp> cons = new edu.clemson.cs.r2jt.collections.List<Exp>("Exp");
    edu.clemson.cs.r2jt.collections.List<Dec> decs = new edu.clemson.cs.r2jt.collections.List<Dec>("Dec");
}
    :   (   dec2=confirm_math_type_declaration { decs.add($dec2.mvd1); }
        |   decs2=concept_state_variable_declaration { decs.addAll($decs2.decs); }
        |   exp2=constraint_clause { cons.add($exp2.exp); }
        |   perfInit=performance_module_init_declaration
        |   perfFinal=performance_module_final_declaration
        |   init=module_concept_init_declaration
        |   fin=module_concept_final_declaration
        |   dec3=performance_type_declaration { decs.add($dec3.dec); }
        |   dec4=performance_operation_declaration { decs.add($dec4.dec); }
        |   dec5=definition_declaration { decs.add($dec5.dec); }
        |   dec6=defines_declaration { decs.add($dec6.dec); }
        )+
        {   $dec = new PerformanceModuleDec(pn1, pars, pn2, ps, uses, req, cons,
                                            $perfInit.item, $perfFinal.item, $init.item, $fin.item, decs); 
        }
    ;
    
    // ---------------------------------------------------------------
// Enhancement Module
// ---------------------------------------------------------------

enhancement_module returns [EnhancementModuleDec dec = null]
@init{   
    edu.clemson.cs.r2jt.collections.List<Dec> decs = new edu.clemson.cs.r2jt.collections.List<Dec>("Dec");
    edu.clemson.cs.r2jt.collections.List<ModuleParameterDec> pars = new edu.clemson.cs.r2jt.collections.List<ModuleParameterDec>("ModuleParameterDec");
    edu.clemson.cs.r2jt.collections.List<UsesItem> uses = new edu.clemson.cs.r2jt.collections.List<UsesItem>("UsesItem");
}
    :   ^(  MODULE_ENHANCEMENT ps=ident
            (pars2=module_formal_param_section)?
            cName=ident
            (uses2=uses_list)?
            (req=requires_clause)?
            (dec2=enhancement_item_sequence)?
        )
        {   if ($dec2.dec != null) {
                decs = $dec2.dec.getDecs();
            }
            $dec = new EnhancementModuleDec($ps.ps,
                                        $pars2.pars!=null?$pars2.pars:pars,
                                        $cName.ps,
                                        $uses2.uses!=null?$uses2.uses:uses,
                                        $req.exp, decs);
        }
    ;

  //enhancement_item_sequence
  //    :   (enhancement_item)+
  //    ;

enhancement_item_sequence returns [EnhancementModuleDec dec = null]
@init{   PosSymbol ps = null; //dummy
    edu.clemson.cs.r2jt.collections.List<ModuleParameterDec> pars = null; //dummy
    PosSymbol cSym = null; //dummy
    edu.clemson.cs.r2jt.collections.List<UsesItem> uses = null; //dummy
    Exp req = null; //dummy
    edu.clemson.cs.r2jt.collections.List<Dec> decs = new edu.clemson.cs.r2jt.collections.List<Dec>("Dec");
}
    :   (   decs2=concept_state_variable_declaration { decs.addAll($decs2.decs); }
        |   dec1=type_declaration { decs.add($dec1.dec); }
        |   dec2=operation_declaration { decs.add($dec2.dec); }
        |   dec3=definition_declaration { decs.add($dec3.dec); }
        |   dec4=defines_declaration { decs.add($dec4.dec); }
        )+
        { $dec = new EnhancementModuleDec(ps, pars, cSym, uses, req, decs); }
    ;

// ---------------------------------------------------------------
// Body Module
// ---------------------------------------------------------------

realization_body_module returns [ModuleDec dec = null]
@init{
    PosSymbol cName = null;
    edu.clemson.cs.r2jt.collections.List<PosSymbol> eNames = new edu.clemson.cs.r2jt.collections.List<PosSymbol>("PosSymbol");
    PosSymbol eName = null;
    edu.clemson.cs.r2jt.collections.List<EnhancementBodyItem> eItems
        = new edu.clemson.cs.r2jt.collections.List<EnhancementBodyItem>("EnhancementBodyItem");
    edu.clemson.cs.r2jt.collections.List<Exp> convs = new edu.clemson.cs.r2jt.collections.List<Exp>("Exp");
    edu.clemson.cs.r2jt.collections.List<Exp> corrs = new edu.clemson.cs.r2jt.collections.List<Exp>("Exp");
    InitItem init = null;
    FinalItem fin = null;
    edu.clemson.cs.r2jt.collections.List<Dec> decs = new edu.clemson.cs.r2jt.collections.List<Dec>("Dec");
    edu.clemson.cs.r2jt.collections.List<ModuleParameterDec> pars = new edu.clemson.cs.r2jt.collections.List<ModuleParameterDec>("ModuleParameterDec");
    edu.clemson.cs.r2jt.collections.List<UsesItem> uses = new edu.clemson.cs.r2jt.collections.List<UsesItem>("UsesItem");
}
    :   ^(  MODULE_REALIZATION ps=ident
            (WITH_PROFILE prof=ident)? (pars2=module_formal_param_section)?
            (   dec2=body_concept_section
            |   dec3=body_enhancement_section
            )
            (uses2=uses_list)?
            (req=requires_clause)?
            (dec4=body_item_sequence)?
        )
        {   if ($dec4.dec != null) {
                convs = $dec4.dec.getConventions();
                corrs = $dec4.dec.getCorrs();
                init = $dec4.dec.getFacilityInit();
                fin = $dec4.dec.getFacilityFinal();
                decs = $dec4.dec.getDecs();
            }
            if ($dec2.dec != null) {
                cName = $dec2.dec.getConceptName();
                eNames = $dec2.dec.getEnhancementNames();
                $dec = new ConceptBodyModuleDec($ps.ps, $prof.ps,
                    $pars2.pars!=null?$pars2.pars:pars, cName, eNames,
                    $uses2.uses!=null?$uses2.uses:uses, $req.exp, convs,
                    corrs, init, fin, decs);
            } else if ($dec3.dec != null) {
                eName = $dec3.dec.getEnhancementName();
                cName = $dec3.dec.getConceptName();
                eItems = $dec3.dec.getEnhancementBodies();
                $dec = new EnhancementBodyModuleDec($ps.ps, $prof.ps, $pars2.pars!=null?$pars2.pars:pars, eName,
                    cName, eItems,$uses2.uses!=null?$uses2.uses:uses, $req.exp, convs, corrs,
                    init, fin, decs);
            } else {
                assert false;
            }
        }
    ;

body_concept_section returns [ConceptBodyModuleDec dec = null]
@init{   PosSymbol ps = null; //dummy
    edu.clemson.cs.r2jt.collections.List<ModuleParameterDec> pars = null; //dummy
    edu.clemson.cs.r2jt.collections.List<PosSymbol> eNames = new edu.clemson.cs.r2jt.collections.List<PosSymbol>("PosSymbol");
    edu.clemson.cs.r2jt.collections.List<UsesItem> uses = new edu.clemson.cs.r2jt.collections.List<UsesItem>("UsesItem");
    Exp req = null; //dummy
    edu.clemson.cs.r2jt.collections.List<Exp> convs = null; //dummy
    edu.clemson.cs.r2jt.collections.List<Exp> corrs = null; //dummy
    InitItem init = null; //dummy
    FinalItem fin = null; //dummy
    edu.clemson.cs.r2jt.collections.List<Dec> decs = null; //dummy
}
    :   ^(CONCEPT cName=ident (ps2=ident { eNames.add($ps2.ps); })*
        )
        {   $dec = new ConceptBodyModuleDec(ps, null, pars, $cName.ps, eNames,
                uses, req, convs, corrs, init, fin, decs);
        }
    ;

body_enhancement_section returns [EnhancementBodyModuleDec dec = null]
@init{   PosSymbol ps = null; //dummy
    edu.clemson.cs.r2jt.collections.List<ModuleParameterDec> pars = null; //dummy
    edu.clemson.cs.r2jt.collections.List<EnhancementBodyItem> eItems
        = new edu.clemson.cs.r2jt.collections.List<EnhancementBodyItem>("EnhancementBodyItem");
    edu.clemson.cs.r2jt.collections.List<UsesItem> uses = new edu.clemson.cs.r2jt.collections.List<UsesItem>("UsesItem");
    Exp req = null; //dummy
    edu.clemson.cs.r2jt.collections.List<Exp> convs = null; //dummy
    edu.clemson.cs.r2jt.collections.List<Exp> corrs = null; //dummy
    InitItem init = null; //dummy
    FinalItem fin = null; //dummy
    edu.clemson.cs.r2jt.collections.List<Dec> decs = null; //dummy
}
    :   ^(  ENHANCEMENT eName=ident cName=ident
            (item2=added_enhancement_section { eItems.add($item2.item); })*
        )
        {   $dec = new EnhancementBodyModuleDec(ps, null, pars, $eName.ps, $cName.ps,
                eItems, uses, req, convs, corrs, init, fin, decs);
        }
    ;

added_enhancement_section returns [EnhancementBodyItem item = null]
@init{
  edu.clemson.cs.r2jt.collections.List<ModuleArgumentItem> args = new edu.clemson.cs.r2jt.collections.List<ModuleArgumentItem>("ModuleArgumentItem");
}
    :   ^(  ENHANCED ps=ident (args2=module_argument_section)?
            REALIZED bName=ident (WITH_PROFILE prof=ident)? (bArgs2=module_argument_section)?
        )
        { $item = new EnhancementBodyItem($ps.ps,
                                          $args2.args!=null?$args2.args:args,
                                          $bName.ps, $prof.ps,
                                          $bArgs2.args!=null?$bArgs2.args:args); }
    ;

  //body_item_sequence
  //    :   (body_item)+
  //    ;

body_item_sequence returns [ConceptBodyModuleDec dec = null]
@init{
    PosSymbol ps = null; //dummy
    edu.clemson.cs.r2jt.collections.List<ModuleParameterDec> pars = null; //dummy
    PosSymbol cSym = null; //dummy
    edu.clemson.cs.r2jt.collections.List<PosSymbol> eNames = null; //dummy
    edu.clemson.cs.r2jt.collections.List<UsesItem> uses = null; //dummy
    Exp req = null; //dummy
    edu.clemson.cs.r2jt.collections.List<Exp> convs = new edu.clemson.cs.r2jt.collections.List<Exp>("Exp");
    edu.clemson.cs.r2jt.collections.List<Exp> corrs = new edu.clemson.cs.r2jt.collections.List<Exp>("Exp");
    edu.clemson.cs.r2jt.collections.List<Dec> decs = new edu.clemson.cs.r2jt.collections.List<Dec>("Dec");
}
    :   (   decs2=state_variable_declaration { decs.addAll($decs2.decs); }
        |   exp1=convention_clause { convs.add($exp1.exp); }
        |   exp2=correspondence_clause { corrs.add($exp2.exp); }
        |   init=module_body_init_declaration
        |   fin=module_body_final_declaration
        |   dec1=type_representation_declaration { decs.add($dec1.dec); }
        |   dec3=operation_recursive_procedure_declaration { decs.add($dec3.dec); }
        |   dec2=operation_procedure_declaration { decs.add($dec2.dec); }
        |   dec4=aux_operation_declaration { decs.add($dec4.dec); }
        |   dec5=procedure_declaration { decs.add($dec5.dec); }
        |   dec6=recursive_procedure_declaration { decs.add($dec6.dec); }
        |   dec7=definition_declaration { decs.add($dec7.dec); }
        |   dec8=facility_declaration { decs.add($dec8.dec); }
        )+
        {   $dec = new ConceptBodyModuleDec(ps, null, pars, cSym,
                eNames, uses, req, convs, corrs, $init.item, $fin.item, decs);
        }
    ;

// ---------------------------------------------------------------
// Facility Module
// ---------------------------------------------------------------

facility_module returns [ModuleDec dec = null]
@init{
    InitItem init = null;
    FinalItem fin = null;
    edu.clemson.cs.r2jt.collections.List<Dec> decs = null;
    FacilityDec fDec = null;
}
    :   ^(  FACILITY ps=ident
            (   dec2=short_facility_section (uses=uses_list)?
            |   (uses=uses_list)? (dec3=facility_item_sequence)?
            )
        )
        {   if ($dec2.dec != null) {
                fDec = new FacilityDec($ps.ps,
                    $dec2.dec.getConceptName(),
                    $dec2.dec.getConceptParams(),
                    $dec2.dec.getEnhancements(),
                    $dec2.dec.getBodyName(),
                    null,
                    $dec2.dec.getBodyParams(),
                    $dec2.dec.getEnhancementBodies());
                $dec = new ShortFacilityModuleDec($ps.ps, fDec, $uses.uses);
            } else if ($dec3.dec != null) {
                init = $dec3.dec.getFacilityInit();
                fin = $dec3.dec.getFacilityFinal();
                decs = $dec3.dec.getDecs();
                $dec = new FacilityModuleDec($ps.ps,
                    $uses.uses, init, fin, decs);
            } else {
                assert false;
            }
        }
    ;

short_facility_section returns [FacilityDec dec = null]
@init{   PosSymbol ps = null; //dummy
    edu.clemson.cs.r2jt.collections.List<EnhancementItem> eItems
        = new edu.clemson.cs.r2jt.collections.List<EnhancementItem>("EnhancementItem");
    edu.clemson.cs.r2jt.collections.List<EnhancementBodyItem> ebItems
        = new edu.clemson.cs.r2jt.collections.List<EnhancementBodyItem>("EnhancementBodyItem");
    edu.clemson.cs.r2jt.collections.List<UsesItem> uses = new edu.clemson.cs.r2jt.collections.List<UsesItem>("UsesItem");
    edu.clemson.cs.r2jt.collections.List<ModuleArgumentItem> args = new edu.clemson.cs.r2jt.collections.List<ModuleArgumentItem>("ModuleArgumentItem");
} 
    :   cName=ident (cPars=module_argument_section)?
        (eItem=facility_enhancement { eItems.add($eItem.item); })*
        bName=ident (bPars=module_argument_section)?
        (ebItem=facility_body_enhancement { ebItems.add($ebItem.item); })*
        {   $dec = new FacilityDec(ps, $cName.ps,
                                    $cPars.args!=null?$cPars.args:args, eItems,
                                    $bName.ps, null,
                                    $bPars.args!=null?$bPars.args:args, ebItems);
        }
    ;

  //facility_item_sequence
  //    :   (facility_item)+
  //    ;

facility_item_sequence returns [FacilityModuleDec dec = null]
@init{   PosSymbol ps = null; //dummy
    edu.clemson.cs.r2jt.collections.List<UsesItem> uses = null; //dummy
    edu.clemson.cs.r2jt.collections.List<Dec> decs = new edu.clemson.cs.r2jt.collections.List<Dec>("Dec");
}
    :   (   decs2=state_variable_declaration { decs.addAll($decs2.decs); }
        |   init=module_facility_init_declaration
        |   fin=module_facility_final_declaration
        |   dec1=facility_type_declaration { decs.add($dec1.dec); }
        |   dec2=operation_recursive_procedure_declaration { decs.add($dec2.dec); }
        |   dec3=operation_procedure_declaration { decs.add($dec3.dec); }
        |   dec4=definition_declaration { decs.add($dec4.dec); }
        |   dec5=facility_declaration { decs.add($dec5.dec); }
        )+
        { $dec = new FacilityModuleDec(ps, uses, $init.item, $fin.item, decs); }
    ;

// ===============================================================
// Rules for module parameters and uses clause
// ===============================================================

// ---------------------------------------------------------------
// Module parameters
// ---------------------------------------------------------------

module_formal_param_section returns [edu.clemson.cs.r2jt.collections.List<ModuleParameterDec> pars
        = new edu.clemson.cs.r2jt.collections.List<ModuleParameterDec>("ModuleParameterDec")]
@init{
}
    :   ^(PARAMS (pars2=module_parameter { $pars.addAll($pars2.pars); })+)
    ;

module_parameter returns [edu.clemson.cs.r2jt.collections.List<ModuleParameterDec> pars
        = new edu.clemson.cs.r2jt.collections.List<ModuleParameterDec>("ModuleParameterDec")]
@init{
}
    :   par1=definition_parameter { $pars.add(new ModuleParameterDec($par1.dec)); }
    |   pars2=constant_parameter { $pars.addAll($pars2.pars); }
    |   par2=concept_type_parameter { $pars.add(new ModuleParameterDec($par2.dec)); }
    |   par3=operation_parameter { $pars.add(new ModuleParameterDec($par3.dec1)); }
    |   par4=concept_realization_parameter { $pars.add(new ModuleParameterDec($par4.dec)); }
    ;

definition_parameter returns [DefinitionDec dec = null]
@init{   boolean impl = false;
    PosSymbol ps = null;
    edu.clemson.cs.r2jt.collections.List<MathVarDec> pars = new edu.clemson.cs.r2jt.collections.List<MathVarDec>("VarDec");
    Ty ty = null;
    Exp base = null; //dummy
    Exp hyp = null; //dummy
    Exp def = null; //dummy
}
    :   ^(DEFINITION dec2=definition_signature)
        {   ps = $dec2.dec.getName();
            pars = $dec2.dec.getParameters();
            ty = $dec2.dec.getReturnTy();
            $dec = new DefinitionDec(impl, ps,
                pars, ty, base, hyp, def);
        }
    ;

constant_parameter returns [edu.clemson.cs.r2jt.collections.List<ModuleParameterDec> pars
        = new edu.clemson.cs.r2jt.collections.List<ModuleParameterDec>("ModuleParameterDec")]
@init{
}
    :   ^(EVALUATES vars=variable_declaration_group)
        {   Iterator<VarDec> i = $vars.decs.iterator();
            while (i.hasNext()) {
                VarDec var = i.next();
                ConstantParamDec con = new ConstantParamDec(
                    var.getName(),
                    var.getTy());
                $pars.add(new ModuleParameterDec(con));
            }
        }
    ;

concept_type_parameter returns [ConceptTypeParamDec dec = null]
@init{
}
    :   ^(TYPE ps=ident)
        { $dec = new ConceptTypeParamDec($ps.ps); }
    ;

operation_parameter returns [OperationDec dec1 = null]
    :   dec=operation_declaration
        { $dec1 = $dec.dec; }
    ;

concept_realization_parameter returns [RealizationParamDec dec = null]
@init{
}
    :   ^(REALIZATION ps=ident cName=ident)
        {   $dec = new RealizationParamDec($ps.ps, $cName.ps);
        }
    ;

// ---------------------------------------------------------------
//  Uses Declaration
// ---------------------------------------------------------------

uses_list returns [edu.clemson.cs.r2jt.collections.List<UsesItem> uses = new edu.clemson.cs.r2jt.collections.List<UsesItem>("UsesItem")]
@init{
}
    :   (uses2=uses_clause { $uses.addAll($uses2.uses); })+
    ;

uses_clause returns [edu.clemson.cs.r2jt.collections.List<UsesItem> uses = new edu.clemson.cs.r2jt.collections.List<UsesItem>("UsesItem")]
@init{
}
    :   ^(USES (ps=ident { $uses.add(new UsesItem($ps.ps)); })+)
    ;

// ===============================================================
// Rules for module level declarations and items
// ===============================================================

// ---------------------------------------------------------------
// Math Type Declarations
// ---------------------------------------------------------------
      
qualified_type returns [PosSymbol ps = null]
    :   ident DOT^ id=ident {$ps = $id.ps;}
    ;
    
confirm_math_type_declaration returns [MathVarDec mvd1 = null]
    :   ^( CONFIRM_TYPE mvd=math_variable_declaration
        )
        { $mvd.var.setConfirm(true); 
          $mvd1 = $mvd.var; }
    ;

// ---------------------------------------------------------------
// Math Assertions
// ---------------------------------------------------------------

math_assertion_declaration returns [MathAssertionDec dec = null]
@init{
    int kind = 0;
    MathAssertionDec.TheoremSubtype subtype = 
        MathAssertionDec.TheoremSubtype.NONE;
}
    :   (   ^(AXIOM (ps=math_theorem_ident)? exp=math_expression)
            { kind = MathAssertionDec.AXIOM; }
        |   ^(THEOREM (ps=math_theorem_ident)? exp=math_expression)
            { kind = MathAssertionDec.THEOREM; }
        |   ^(PROPERTY (ps=math_theorem_ident)? exp=math_expression)
            { kind = MathAssertionDec.PROPERTY; }
        |   ^(LEMMA (ps=math_theorem_ident)? exp=math_expression)
            { kind = MathAssertionDec.LEMMA; }
        |   ^(COROLLARY (ps=math_theorem_ident)? exp=math_expression)
            { kind = MathAssertionDec.COROLLARY; }
        |   COMMUTATIVITY (ps=math_theorem_ident)? exp=math_expression
            { kind = MathAssertionDec.THEOREM;
              subtype = MathAssertionDec.TheoremSubtype.COMMUTATIVITY; }
        )
        { if (kind == MathAssertionDec.THEOREM) {
              $dec = new MathAssertionDec($ps.ps, $exp.exp, subtype);
          }
          else {
              $dec = new MathAssertionDec($ps.ps, kind, $exp.exp);
          }
        }
    ;

constraint_clause returns [Exp exp = null]
    :   ^(CONSTRAINT exp1=math_expression)
        { $exp = $exp1.exp; }
    ;

correspondence_clause returns [Exp exp = null]
    :   ^(CORR exp1=math_expression)
        { $exp = $exp1.exp; }
    ;

convention_clause returns [Exp exp = null]
    :   ^(CONVENTION exp1=math_expression)
        { $exp = $exp1.exp; }
    ;
    
type_theorem_declaration returns [TypeTheoremDec dec = new TypeTheoremDec()]
    :   ^(TYPE (ps=math_theorem_ident)? COLON
         (FOR quant_vars=math_variable_declaration_group { $dec.addVarDecGroup($quant_vars.vars); })+
         assertion=implies_expression
         asserted_ty=math_type_expression
         )
        {
          $dec.setName($ps.ps);
          $dec.setAssertion($assertion.exp);
          $dec.setAssertedType($asserted_ty.ty);
        }
    ;

// ---------------------------------------------------------------
// State Variable Declarations
// ---------------------------------------------------------------

concept_state_variable_declaration returns [edu.clemson.cs.r2jt.collections.List<Dec> decs
        = new edu.clemson.cs.r2jt.collections.List<Dec>("MathVarDec")]
    :   ^(VAR decs2=math_variable_declaration_group)
        //NOTE: See gj tutorial for why this must be done.
        {   Iterator<MathVarDec> i = $decs2.vars.iterator();
            while (i.hasNext()) {
                MathVarDec dec = i.next();
                $decs.add(dec);
            }
        }
    ;

state_variable_declaration returns [edu.clemson.cs.r2jt.collections.List<Dec> decs
        = new edu.clemson.cs.r2jt.collections.List<Dec>("Dec")]
    :   ^(VAR decs2=variable_declaration_group)
        //NOTE: See gj tutorial for why this must be done.
        {   Iterator<VarDec> i = $decs2.decs.iterator();
            while (i.hasNext()) {
                VarDec dec = i.next();
                $decs.add(dec);
            }
        }
    ;
    
state_aux_variable_declaration
    :   AUX_VAR variable_declaration_group
    ;

// ---------------------------------------------------------------
//  Facility Declarations
// ---------------------------------------------------------------

facility_declaration returns [FacilityDec dec = null]
@init{
    edu.clemson.cs.r2jt.collections.List<EnhancementItem> eItems
        = new edu.clemson.cs.r2jt.collections.List<EnhancementItem>("EnhancementItem");
    edu.clemson.cs.r2jt.collections.List<EnhancementBodyItem> ebItems
        = new edu.clemson.cs.r2jt.collections.List<EnhancementBodyItem>("ModuleArgumentItem");
    edu.clemson.cs.r2jt.collections.List<ModuleArgumentItem> args = new edu.clemson.cs.r2jt.collections.List<ModuleArgumentItem>("ModuleArgumentItem");
}
    :   ^(  FACILITY ps=ident
            cName=ident (cPars=module_argument_section)?
            (eItem=facility_enhancement { eItems.add($eItem.item); })*
            REALIZED bName=ident (WITH_PROFILE prof=ident)? (bPars=module_argument_section)?
            (ebItem=facility_body_enhancement { ebItems.add($ebItem.item); })*
        )
        {   $dec = new FacilityDec($ps.ps, $cName.ps,
                                    $cPars.args!=null?$cPars.args:args,
                                    eItems, $bName.ps, $prof.ps,
                                    $bPars.args!=null?$bPars.args:args,
                                    ebItems);
        }
    ;

facility_enhancement returns [EnhancementItem item = null]
@init{
    edu.clemson.cs.r2jt.collections.List<ModuleArgumentItem> args = new edu.clemson.cs.r2jt.collections.List<ModuleArgumentItem>("ModuleArgumentItem");
}
    :   ^(ENHANCED ps=ident (args2=module_argument_section)?)
        { $item = new EnhancementItem($ps.ps, $args2.args!=null?$args2.args:args); }
    ;

facility_body_enhancement returns [EnhancementBodyItem item = null]
@init{
    edu.clemson.cs.r2jt.collections.List<ModuleArgumentItem> args = new edu.clemson.cs.r2jt.collections.List<ModuleArgumentItem>("ModuleArgumentItem");
}
    :   ^(  ENHANCED ps=ident (args2=module_argument_section)?
            REALIZED bName=ident (WITH_PROFILE prof=ident)? (bArgs=module_argument_section)?
        )
        { $item = new EnhancementBodyItem($ps.ps,
                                          $args2.args!=null?$args2.args:args,
                                          $bName.ps, $prof.ps,
                                          $bArgs.args!=null?$bArgs.args:args); }
    ;

module_argument_section returns [edu.clemson.cs.r2jt.collections.List<ModuleArgumentItem> args
        = new edu.clemson.cs.r2jt.collections.List<ModuleArgumentItem>("ModuleArgumentItem")]
@init{
}
    :   ^(PARAMS (arg=module_argument { $args.add($arg.arg); })+)
    ;

module_argument returns [ModuleArgumentItem arg = null]
@init{
    PosSymbol qual = null;
    PosSymbol name = null;
}
    :   (   qid=qualified_ident 
            {   qual = $qid.exp.getQualifier();
                name = $qid.exp.getName();
            }
        |   exp=program_expression
        )
        { $arg = new ModuleArgumentItem(qual, name, $exp.exp); }
    ;

// ===============================================================
// Definition Declarations
// ===============================================================

defines_declaration returns [DefinitionDec dec = null]
@init{
    boolean impl = false;
    PosSymbol ps = null; //dummy
    edu.clemson.cs.r2jt.collections.List<MathVarDec> pars = null; //dummy
    Ty ty = null; //dummy
    Exp base = null; //dummy
    Exp hyp = null; //dummy
    Exp exp = null;
}
    :   ^(DEFINES dec2=definition_signature)
        {   ps = $dec2.dec.getName();
            pars = $dec2.dec.getParameters();
            ty = $dec2.dec.getReturnTy();
            $dec = new DefinitionDec(impl, ps, pars, ty, base, hyp, exp);
        }   
    ;

definition_declaration returns [DefinitionDec dec = null]
    :   dec1=implicit_definition_declaration { $dec = $dec1.dec; }
    |   dec2=inductive_definition_declaration { $dec = $dec2.dec; }
    |   dec3=standard_definition_declaration { $dec = $dec3.dec; }
        
    ;

implicit_definition_declaration returns [DefinitionDec dec = null]
@init{   
    boolean impl = true;
    PosSymbol ps = null; //dummy
    edu.clemson.cs.r2jt.collections.List<MathVarDec> pars = null; //dummy
    Ty ty = null; //dummy
    Exp base = null; //dummy
    Exp hyp = null; //dummy
}
    :   ^(IMPLICIT_DEF dec2=definition_signature exp=math_expression)
        {   ps = $dec2.dec.getName();
            pars = $dec2.dec.getParameters();
            ty = $dec2.dec.getReturnTy();
            $dec = new DefinitionDec(impl, ps, pars, ty, base, hyp, $exp.exp);
        }   
    ;

inductive_definition_declaration returns [DefinitionDec dec = null]
@init{   
    boolean impl = true;
    PosSymbol ps = null; //dummy
    edu.clemson.cs.r2jt.collections.List<MathVarDec> pars = null; //dummy
    Ty ty = null; //dummy
    Exp exp = null; //dummy
}
    :   ^(  INDUCTIVE_DEF dec2=definition_signature
            base=indexed_expression hyp=indexed_expression
        )
        {   ps = $dec2.dec.getName();
            pars = $dec2.dec.getParameters();
            ty = $dec2.dec.getReturnTy();
            $dec = new DefinitionDec(impl, ps, pars, ty, $base.exp, $hyp.exp, exp);
        }   
    ;

standard_definition_declaration returns [DefinitionDec dec = null]
@init{
    boolean impl = false;
    PosSymbol ps = null; //dummy
    edu.clemson.cs.r2jt.collections.List<MathVarDec> pars = null; //dummy
    Ty ty = null; //dummy
    Exp base = null; //dummy
    Exp hyp = null; //dummy
}
    :   ^(DEFINITION dec2=definition_signature (exp=math_expression)?)
        {   ps = $dec2.dec.getName();
            pars = $dec2.dec.getParameters();
            ty = $dec2.dec.getReturnTy();
            $dec = new DefinitionDec(impl, ps, pars, ty, base, hyp, $exp.exp);
        }   
    ;
    
categorical_definition_declaration returns [CategoricalDefinitionDec dec = null]
    :   ^(CATEGORICAL_DEFINITION dec1=categorical_definition_construct ^(RELATED_BY exp=math_expression))
        {   
            $dec = new CategoricalDefinitionDec($dec1.defs, $exp.exp); 
        }
    ;

definition_signature returns [DefinitionDec dec = null]
@init{   
    boolean impl = false;
    PosSymbol ps = null; //dummy
    edu.clemson.cs.r2jt.collections.List<MathVarDec> pars = null; //dummy
    Exp base = null; //dummy
    Exp hyp = null; //dummy
    Exp exp = null; //dummy
}
    :   (   (   dec1=infix_definition_construct
                {
                  ps = $dec1.dec.getName();
                  pars = $dec1.dec.getParameters();
                }
            |   dec2=outfix_definition_construct
                {
                  ps = $dec2.dec.getName();
                  pars = $dec2.dec.getParameters();
                }
            |   dec3=standard_definition_construct
                {
                  ps = $dec3.dec.getName();
                  pars = $dec3.dec.getParameters();
                }
            )
            ty=math_type_expression
        )
        {   
            $dec = new DefinitionDec(impl, ps, pars, $ty.ty, base, hyp, exp);
        }   
    ;

infix_definition_construct returns [DefinitionDec dec = null]
@init{
    PosSymbol ps = null;  
    boolean impl = false;
    edu.clemson.cs.r2jt.collections.List<MathVarDec> pars = new edu.clemson.cs.r2jt.collections.List<MathVarDec>("MathVarDec");
    Ty ty = null; //dummy
    Exp base = null; //dummy
    Exp hyp = null; //dummy
    Exp exp = null; //dummy
}
    :   var2=singleton_definition_parameter { pars.add($var2.dec); }
        (ps1=ident { ps = $ps1.ps; }| ps2=infix_symbol { ps = $ps2.ps; })
        var3=singleton_definition_parameter { pars.add($var3.dec); }
        { $dec = new DefinitionDec(impl, ps, pars, ty, base, hyp, exp); }
    ;

outfix_definition_construct returns [DefinitionDec dec = null]
@init{   
    boolean impl = false;
    PosSymbol ps = null;
    edu.clemson.cs.r2jt.collections.List<MathVarDec> pars = new edu.clemson.cs.r2jt.collections.List<MathVarDec>("MathVarDec");
    Ty ty = null; //dummy
    Exp base = null; //dummy
    Exp hyp = null; //dummy
    Exp exp = null; //dummy
}
    :   (   BAR var2=singleton_definition_parameter
            { ps = getOutfixPosSymbol($BAR); pars.add($var2.dec); }
        |   DBL_BAR var2=singleton_definition_parameter
            { ps = getOutfixPosSymbol($DBL_BAR); pars.add($var2.dec); }
        |   LT var2=singleton_definition_parameter
            { ps = getOutfixPosSymbol($LT); pars.add($var2.dec); }
        |   LL var2=singleton_definition_parameter
            { ps = getOutfixPosSymbol($LL); pars.add($var2.dec); }
        )
        { $dec = new DefinitionDec(impl, ps, pars, ty, base, hyp, exp); }
    ;

standard_definition_construct returns [DefinitionDec dec = null]
@init{   
    PosSymbol ps = null;
    boolean impl = false;
    Ty ty = null; //dummy
    Exp base = null; //dummy
    Exp hyp = null; //dummy
    Exp exp = null; //dummy
    MathVarDec var2 = null;
}
    :   (   ps1=ident { ps = $ps1.ps; }
        |   ps2=prefix_symbol { ps = $ps2.ps; }
        |   NUMERIC_LITERAL { ps=getPosSymbol($NUMERIC_LITERAL); }
        )
        (pars=definition_formal_param_section)?
        { $dec = new DefinitionDec(impl, ps, $pars.decs, ty, base, hyp, exp); }
    ;

categorical_definition_construct returns [edu.clemson.cs.r2jt.collections.List<DefinitionDec> defs
        = new edu.clemson.cs.r2jt.collections.List<DefinitionDec>("DefinitionDec")]
    :   (^(DEFINITION dec=definition_signature) { $defs.add($dec.dec); })+
        //(DEFINITION dec1=definition_signature { $defs.add($dec1.dec); } )*
    ;

indexed_expression returns [Exp exp = null]
    :   exp1=math_expression
        { $exp = $exp1.exp; }
    ;

singleton_definition_parameter returns [MathVarDec dec = null]
    :   ^(PARAMS dec1=math_variable_declaration)
        { $dec = $dec1.var; }
    ;

definition_formal_param_section returns [edu.clemson.cs.r2jt.collections.List<MathVarDec> decs
        = new edu.clemson.cs.r2jt.collections.List<MathVarDec>("MathVarDec")]
    :   ^(  PARAMS
            (decs2=math_variable_declaration_group { $decs.addAll($decs2.vars); })+
        )
    ;

infix_symbol returns [PosSymbol ps = null]
    :   (   sym=EQL | sym=NOT_EQL | sym=LT | sym=GT | sym=LT_EQL | sym=GT_EQL
        | sym=PLUS | sym=MINUS | sym=MULTIPLY | sym=DIVIDE | sym=EXP
        | sym=MOD | sym=REM | sym=DIV | sym=IMPLIES | sym=IFF | sym=AND
        | sym=OR | sym=XOR | sym=ANDTHEN | sym=ORELSE | sym=COMPLEMENT | sym=IN
        | sym=NOT_IN | sym=RANGE | sym=UNION | sym=INTERSECT | sym=WITHOUT
        | sym=SUBSET | sym=PROP_SUBSET | sym=NOT_SUBSET | sym=NOT_PROP_SUBSET
        | sym=CAT | sym=SUBSTR | sym=NOT_SUBSTR
        )
        { $ps = getPosSymbol($sym); }
    ;

prefix_symbol returns [PosSymbol ps = null]
    : (sym=PLUS | sym=MINUS | sym=NOT | sym=ABS | sym=COMPLEMENT) { $ps = getPosSymbol($sym); }
    ;
    
quant_symbol
    : BIG_UNION | BIG_INTERSECT | BIG_SUM | BIG_PRODUCT | BIG_CONCAT
    ;

// ===============================================================
// Operation Declarations
// ===============================================================

operation_procedure_declaration returns [FacilityOperationDec dec = null]
@init{
    edu.clemson.cs.r2jt.collections.List<AffectsItem> items = new edu.clemson.cs.r2jt.collections.List<AffectsItem>("AffectsItem");
    edu.clemson.cs.r2jt.collections.List<FacilityDec> facs = new edu.clemson.cs.r2jt.collections.List<FacilityDec>("FacilityDec");
    edu.clemson.cs.r2jt.collections.List<VarDec> vars = new edu.clemson.cs.r2jt.collections.List<VarDec>("VarDec");
    edu.clemson.cs.r2jt.collections.List<AuxVarDec> aux_vars = new edu.clemson.cs.r2jt.collections.List<AuxVarDec>("AuxVarDec");
}
    :   ^(  OPERATION ps=ident
            pars=operation_formal_param_section
            (ty=program_type_expression)?
            (items2=affects_clause { items.addAll($items2.items); })*
            (req=requires_clause)?
            (ens=ensures_clause)?
            (decr=decreasing_clause)?
            (fac2=facility_declaration { facs.add($fac2.dec); })*
            (vars2=variable_declaration { vars.addAll($vars2.decs); })*
            (aux_vars2=aux_variable_declaration { aux_vars.addAll($aux_vars2.decs); })*
            (sts=statement_sequence)
        )
        {   $dec = new FacilityOperationDec($ps.ps, $pars.decs, $ty.ty, items,
                $req.exp, $ens.exp, $decr.exp, facs, vars, aux_vars, $sts.stmts);
        }
    ;
    
operation_recursive_procedure_declaration returns [FacilityOperationDec dec = null]
@init{
    edu.clemson.cs.r2jt.collections.List<AffectsItem> items = new edu.clemson.cs.r2jt.collections.List<AffectsItem>("AffectsItem");
    edu.clemson.cs.r2jt.collections.List<FacilityDec> facs = new edu.clemson.cs.r2jt.collections.List<FacilityDec>("FacilityDec");
    edu.clemson.cs.r2jt.collections.List<VarDec> vars = new edu.clemson.cs.r2jt.collections.List<VarDec>("VarDec");
    edu.clemson.cs.r2jt.collections.List<AuxVarDec> aux_vars = new edu.clemson.cs.r2jt.collections.List<AuxVarDec>("AuxVarDec");
}
    :   ^(  RECURSIVE_OPERATION_PROCEDURE ps=ident
            pars=operation_formal_param_section
            (ty=program_type_expression)?
            (items2=affects_clause { items.addAll($items2.items); })*
            (req=requires_clause)?
            (ens=ensures_clause)?
            decr=decreasing_clause
            (fac2=facility_declaration { facs.add($fac2.dec); })*
            (vars2=variable_declaration { vars.addAll($vars2.decs); })*
            (aux_vars2=aux_variable_declaration { aux_vars.addAll($aux_vars2.decs); })*
            (sts=statement_sequence)
        )
        {   $dec = new FacilityOperationDec($ps.ps, $pars.decs, $ty.ty, items,
                $req.exp, $ens.exp, $decr.exp, facs, vars, aux_vars, $sts.stmts, true);
        }
    ;

operation_declaration returns [OperationDec dec = null]
@init{
    edu.clemson.cs.r2jt.collections.List<AffectsItem> items = new edu.clemson.cs.r2jt.collections.List<AffectsItem>("AffectsItem");
}
    :   ^(  OPERATION ps=ident
            pars=operation_formal_param_section
            (ty=program_type_expression)?
            (items2=affects_clause { items.addAll($items2.items); })*
            (req=requires_clause)?
            (ens=ensures_clause)?
        )
        { $dec = new OperationDec($ps.ps, $pars.decs, $ty.ty, items, $req.exp, $ens.exp); }
    ;
    
performance_operation_declaration returns [PerformanceOperationDec  dec = null]
@init{
    edu.clemson.cs.r2jt.collections.List<AffectsItem> items = new edu.clemson.cs.r2jt.collections.List<AffectsItem>("AffectsItem");
}
    :   ^(  OPERATION ps=ident
            pars=operation_formal_param_section
            (ty=program_type_expression)?
            (items2=affects_clause { items.addAll($items2.items); })*
            (req=requires_clause)?
            (ens=ensures_clause)?
            (dur=duration_clause)?
            (msp=mainp_disp_clause)?
        )
        { $dec = new PerformanceOperationDec ($ps.ps, $pars.decs, $ty.ty, items, $req.exp, $ens.exp, $dur.exp, $msp.exp); }
    ;

aux_operation_declaration returns [OperationDec dec = null]
@init{
    edu.clemson.cs.r2jt.collections.List<AffectsItem> items = new edu.clemson.cs.r2jt.collections.List<AffectsItem>("AffectsItem");
}
    :   ^(  AUX_OPERATION ps=ident
            pars=operation_formal_param_section
            (ty=program_type_expression)?
            (items2=affects_clause { items.addAll($items2.items); })*
            (req=requires_clause)?
            (ens=ensures_clause)?
        )
        { $dec = new OperationDec($ps.ps, $pars.decs, $ty.ty, items, $req.exp, $ens.exp); }
    ;

procedure_declaration returns [ProcedureDec dec = null]
@init{
    edu.clemson.cs.r2jt.collections.List<AffectsItem> items = new edu.clemson.cs.r2jt.collections.List<AffectsItem>("AffectsItem");
    edu.clemson.cs.r2jt.collections.List<FacilityDec> facs = new edu.clemson.cs.r2jt.collections.List<FacilityDec>("FacilityDec");
    edu.clemson.cs.r2jt.collections.List<VarDec> vars = new edu.clemson.cs.r2jt.collections.List<VarDec>("VarDec");
    edu.clemson.cs.r2jt.collections.List<AuxVarDec> aux_vars = new edu.clemson.cs.r2jt.collections.List<AuxVarDec>("AuxVarDec");
}
    :   ^(  PROCEDURE ps=ident
            pars=operation_formal_param_section
            (ty=program_type_expression)?
            (items2=affects_clause { items.addAll($items2.items); })*
            (decr=decreasing_clause)?
            (fac2=facility_declaration { facs.add($fac2.dec); })*
            (vars2=variable_declaration { vars.addAll($vars2.decs); })*
            (aux_vars2=aux_variable_declaration { aux_vars.addAll($aux_vars2.decs); })*
            (sts=statement_sequence)
        )
        {   $dec = new ProcedureDec($ps.ps, $pars.decs, $ty.ty, items, $decr.exp,
                facs, vars, aux_vars, $sts.stmts);
        }
    ;
    
recursive_procedure_declaration returns [ProcedureDec dec = null]
@init{
    edu.clemson.cs.r2jt.collections.List<AffectsItem> items = new edu.clemson.cs.r2jt.collections.List<AffectsItem>("AffectsItem");
    edu.clemson.cs.r2jt.collections.List<FacilityDec> facs = new edu.clemson.cs.r2jt.collections.List<FacilityDec>("FacilityDec");
    edu.clemson.cs.r2jt.collections.List<VarDec> vars = new edu.clemson.cs.r2jt.collections.List<VarDec>("VarDec");
    edu.clemson.cs.r2jt.collections.List<AuxVarDec> aux_vars = new edu.clemson.cs.r2jt.collections.List<AuxVarDec>("AuxVarDec");
}
    :   ^(  RECURSIVE_PROCEDURE ps=ident
            pars=operation_formal_param_section
            (ty=program_type_expression)?
            (items2=affects_clause { items.addAll($items2.items); })*
            (decr=decreasing_clause)?
            (fac2=facility_declaration { facs.add($fac2.dec); })*
            (vars2=variable_declaration { vars.addAll($vars2.decs); })*
            (aux_vars2=aux_variable_declaration { aux_vars.addAll($aux_vars2.decs); })*
            (sts=statement_sequence)
        )
        {   $dec = new ProcedureDec($ps.ps, $pars.decs, $ty.ty, items, $decr.exp,
                facs, vars, aux_vars, $sts.stmts, true);
        }
    ;

operation_formal_param_section returns [edu.clemson.cs.r2jt.collections.List<ParameterVarDec> decs
        = new edu.clemson.cs.r2jt.collections.List<ParameterVarDec>("ParameterVarDec")]
    :   ^(PARAMS (vars=operation_formal_param_group { $decs.addAll($vars.decs); })*)
    ;

operation_formal_param_group returns [edu.clemson.cs.r2jt.collections.List<ParameterVarDec> decs
        = new edu.clemson.cs.r2jt.collections.List<ParameterVarDec>("ParameterVarDec")]
    :   ^(VAR mode=abstract_mode vars=variable_declaration_group)
        { $decs = getParamVarDecList($mode.mode, $vars.decs); }
    ;

variable_declaration returns [edu.clemson.cs.r2jt.collections.List<VarDec> decs = new edu.clemson.cs.r2jt.collections.List<VarDec>("VarDec")]
    :   ^(VAR decs1=variable_declaration_group)
        { $decs = $decs1.decs; }
    ;
    
aux_variable_declaration returns [edu.clemson.cs.r2jt.collections.List<AuxVarDec> decs = new edu.clemson.cs.r2jt.collections.List<AuxVarDec>("AuxVarDec")]
    :   ^(AUX_VAR decs1=aux_variable_declaration_group)
        { $decs = $decs1.decs; }
    ;

affects_clause returns [edu.clemson.cs.r2jt.collections.List<AffectsItem> items
        = new edu.clemson.cs.r2jt.collections.List<AffectsItem>("AffectsItem")]
@init{
    PosSymbol qual = null;
    PosSymbol name = null;
    AffectsItem item = null;
}
    :   ^(  AFFECTS mode=abstract_mode
            (   qid=qualified_ident
                {   qual = $qid.exp.getQualifier();
                    name = $qid.exp.getName();
                    item = new AffectsItem($mode.mode, qual, name);
                    $items.add(item);
                }
            )
            (   COMMA qid=qualified_ident
                {   qual = $qid.exp.getQualifier();
                    name = $qid.exp.getName();
                    item = new AffectsItem($mode.mode, qual, name);
                    $items.add(item);
                }
            )*
        )
    ;

abstract_mode returns [Mode mode = null]
    :   ALTERS { $mode = Mode.ALTERS; }
    |   CLEARS { $mode = Mode.CLEARS; }
    |   EVALUATES { $mode = Mode.EVALUATES; }
    |   PRESERVES { $mode = Mode.PRESERVES; }
    |   REPLACES { $mode = Mode.REPLACES; }
    |   RESTORES { $mode = Mode.RESTORES; }
    |   UPDATES { $mode = Mode.UPDATES; }
    |   REASSIGNS { $mode = Mode.REASSIGNS; }
    ;

requires_clause returns [Exp exp = null]
    :   ^(REQUIRES exp1=math_expression)
        { $exp = $exp1.exp; }
    ;

ensures_clause returns [Exp exp = null]
    :   ^(ENSURES exp1=math_expression)
        { $exp = $exp1.exp; }
    ;

duration_clause returns [Exp exp = null]
    :   ^(DURATION exp1=math_expression)
        { $exp = $exp1.exp; }
    ;

mainp_disp_clause returns [Exp exp = null]
    :   ^(MAINP_DISP exp1=math_expression)
        { $exp = $exp1.exp; }
    ;

// ===============================================================
// Type Declarations
// ===============================================================

type_declaration returns [TypeDec dec = null]
    :   ^(  TYPE_FAMILY ps=ident
            ty=math_type_expression
            exem=ident
            (cons=constraint_clause)?
            (init=type_concept_init_declaration)?
            (fin=type_concept_final_declaration)?
        )
        { $dec = new TypeDec($ps.ps, $ty.ty, $exem.ps, $cons.exp, $init.item, $fin.item); }
    ;

performance_type_declaration returns [PerformanceTypeDec dec = null]
    :   ^(  TYPE_FAMILY ps=ident
            ty=math_type_expression
            (cons=constraint_clause)?
            (perfInit=performance_type_init_declaration)?
            (perfFinal=performance_type_final_declaration)?
        )
        { $dec = new PerformanceTypeDec($ps.ps, $ty.ty, $cons.exp, $perfInit.item, $perfFinal.item); }
    ;

type_representation_declaration returns [RepresentationDec dec = null]
    :   ^(  TYPE ps=ident
            rep=structural_program_type_expression
            (conv=convention_clause)?
            (corr=correspondence_clause)?
            (init=type_body_init_declaration)?
            (fin=type_body_final_declaration)?
        )
        { $dec = new RepresentationDec($ps.ps, $rep.ty, $conv.exp, $corr.exp, $init.item, $fin.item); }
    ;

facility_type_declaration returns [FacilityTypeDec dec = null]
    :   ^(  TYPE ps=ident
            rep=structural_program_type_expression
            (conv=convention_clause)?
            (init=type_facility_init_declaration)? 
            (fin=type_facility_final_declaration)?
        )
        { $dec = new FacilityTypeDec($ps.ps, $rep.ty, $conv.exp, $init.item, $fin.item); }
    ;

// ---------------------------------------------------------------
// Initialization and finalization rules
// ---------------------------------------------------------------

// Module level init and final -----------------------------------

module_concept_init_declaration returns [InitItem item = null]
    :   ^(FAC_INIT item2=concept_init_final_section)
        { $item = getInitItem(getLocation($FAC_INIT), $item2.item); }
    ;

module_concept_final_declaration returns [FinalItem item = null]
    :   ^(FAC_FINAL item2=concept_init_final_section)
        { $item = getFinalItem(getLocation($FAC_FINAL), $item2.item); }
    ;

performance_module_init_declaration returns [PerformanceInitItem item = null]
    :   ^(PERF_INIT item2=performance_init_section)
        { $item = getPerformanceInitItem(getLocation($PERF_INIT), $item2.item); }
    ;

performance_module_final_declaration returns [PerformanceFinalItem item = null]
    :   ^(PERF_FINAL item2=performance_final_section)
        { $item = getPerformanceFinalItem(getLocation($PERF_FINAL), $item2.item); }
    ;

module_body_init_declaration returns [InitItem item = null]
    :   ^(FAC_INIT item2=body_init_final_section)
        { $item = getInitItem(getLocation($FAC_INIT), $item2.item); }
    ;

module_body_final_declaration returns [FinalItem item = null]
    :   ^(FAC_FINAL item2=body_init_final_section)
        { $item = getFinalItem(getLocation($FAC_FINAL), $item2.item); }
    ;

module_facility_init_declaration returns [InitItem item = null]
    :   ^(FAC_INIT item2=facility_init_final_section)
        { $item = getInitItem(getLocation($FAC_INIT), $item2.item); }
    ;

module_facility_final_declaration returns [FinalItem item = null]
    :   ^(FAC_FINAL item2=facility_init_final_section)
        { $item = getFinalItem(getLocation($FAC_FINAL), $item2.item); }
    ;

// Type level init and final -----------------------------------

type_concept_init_declaration returns [InitItem item = null]
    :   ^(INITIALIZATION item2=concept_init_final_section)
        { $item = getInitItem(getLocation($INITIALIZATION), $item2.item); }
    ;

type_concept_final_declaration returns [FinalItem item = null]
    :   ^(FINALIZATION item2=concept_init_final_section)
        { $item = getFinalItem(getLocation($FINALIZATION), $item2.item
        ); }
    ;
 
performance_type_init_declaration returns [PerformanceInitItem item = null]
    :   ^(INITIALIZATION item2=performance_init_section)
        { $item = getPerformanceInitItem(getLocation($INITIALIZATION), $item2.item); }
    ;

performance_type_final_declaration returns [PerformanceFinalItem item = null]
    :   ^(FINALIZATION  item2=performance_final_section)
        { $item = getPerformanceFinalItem(getLocation($FINALIZATION), $item2.item); }
    ;

type_body_init_declaration returns [InitItem item = null]
    :   ^(INITIALIZATION item2=body_init_final_section)
        { $item = getInitItem(getLocation($INITIALIZATION), $item2.item); }
    ;

type_body_final_declaration returns [FinalItem item = null]
    :   ^(FINALIZATION item2=body_init_final_section)
        { $item = getFinalItem(getLocation($FINALIZATION), $item2.item); }
    ;

type_facility_init_declaration returns [InitItem item = null]
    :   ^(INITIALIZATION item2=facility_init_final_section)
        { $item = getInitItem(getLocation($INITIALIZATION), $item2.item); }
    ;

type_facility_final_declaration returns [FinalItem item = null]
    :   ^(FINALIZATION item2=facility_init_final_section)
        { $item = getFinalItem(getLocation($FINALIZATION), $item2.item); }
    ;

// Init and final sections ---------------------------------------

concept_init_final_section returns [InitItem item = null]
@init{   Location loc = null; //dummy
    edu.clemson.cs.r2jt.collections.List<AffectsItem> items = new edu.clemson.cs.r2jt.collections.List<AffectsItem>("AffectsItem");
    edu.clemson.cs.r2jt.collections.List<FacilityDec> facs = new edu.clemson.cs.r2jt.collections.List<FacilityDec>(); //dummy
    edu.clemson.cs.r2jt.collections.List<VarDec> vars = new edu.clemson.cs.r2jt.collections.List<VarDec>(); //dummy
    edu.clemson.cs.r2jt.collections.List<AuxVarDec> aux_vars = new edu.clemson.cs.r2jt.collections.List<AuxVarDec>(); //dummy
    edu.clemson.cs.r2jt.collections.List<Statement> sts = new edu.clemson.cs.r2jt.collections.List<Statement>(); //dummy
}
    :   (items2=affects_clause { items.addAll($items2.items); })*
        (req=requires_clause)?
        (ens=ensures_clause)?
        { $item = new InitItem(loc, items, $req.exp, $ens.exp, facs, vars, aux_vars, sts); }
    ;

performance_init_section returns [PerformanceInitItem  item = null]
@init{   Location loc = null; //dummy
    edu.clemson.cs.r2jt.collections.List<AffectsItem> items = new edu.clemson.cs.r2jt.collections.List<AffectsItem>("AffectsItem");
    edu.clemson.cs.r2jt.collections.List<FacilityDec> facs = new edu.clemson.cs.r2jt.collections.List<FacilityDec>(); //dummy
    edu.clemson.cs.r2jt.collections.List<VarDec> vars = new edu.clemson.cs.r2jt.collections.List<VarDec>(); //dummy
    edu.clemson.cs.r2jt.collections.List<AuxVarDec> aux_vars = new edu.clemson.cs.r2jt.collections.List<AuxVarDec>(); //dummy
    edu.clemson.cs.r2jt.collections.List<Statement> sts = new edu.clemson.cs.r2jt.collections.List<Statement>(); //dummy
}
    :   (items2=affects_clause { items.addAll($items2.items); })*
        (req=requires_clause)?
        (ens=ensures_clause)?
        (dur=duration_clause)?
        (msp=mainp_disp_clause)?
        { $item = new PerformanceInitItem(loc, items, $req.exp, $ens.exp, $dur.exp, $msp.exp, facs, vars, aux_vars, sts); }
    ;

performance_final_section returns [PerformanceFinalItem  item = null]
@init{   Location loc = null; //dummy
    edu.clemson.cs.r2jt.collections.List<AffectsItem> items = new edu.clemson.cs.r2jt.collections.List<AffectsItem>("AffectsItem");
    edu.clemson.cs.r2jt.collections.List<FacilityDec> facs = new edu.clemson.cs.r2jt.collections.List<FacilityDec>(); //dummy
    edu.clemson.cs.r2jt.collections.List<VarDec> vars = new edu.clemson.cs.r2jt.collections.List<VarDec>(); //dummy
    edu.clemson.cs.r2jt.collections.List<AuxVarDec> aux_vars = new edu.clemson.cs.r2jt.collections.List<AuxVarDec>(); //dummy
    edu.clemson.cs.r2jt.collections.List<Statement> sts = new edu.clemson.cs.r2jt.collections.List<Statement>(); //dummy
}
    :   (items2=affects_clause { items.addAll($items2.items); })*
        (req=requires_clause)?
        (ens=ensures_clause)?
        (dur=duration_clause)?
        (msp=mainp_disp_clause)?
        { $item = new PerformanceFinalItem (loc, items, $req.exp, $ens.exp, $dur.exp, $msp.exp, facs, vars, aux_vars, sts); }
    ;

body_init_final_section returns [InitItem item = null]
@init{   Location loc = null; //dummy
    edu.clemson.cs.r2jt.collections.List<AffectsItem> items = new edu.clemson.cs.r2jt.collections.List<AffectsItem>("AffectsItem");
    Exp req = null; //dummy
    Exp ens = null; //dummy
    edu.clemson.cs.r2jt.collections.List<FacilityDec> facs = new edu.clemson.cs.r2jt.collections.List<FacilityDec>("FacilityDec");
    edu.clemson.cs.r2jt.collections.List<VarDec> vars = new edu.clemson.cs.r2jt.collections.List<VarDec>("VarDec");
    edu.clemson.cs.r2jt.collections.List<AuxVarDec> aux_vars = new edu.clemson.cs.r2jt.collections.List<AuxVarDec>("AuxVarDec");
}
    :   (items2=affects_clause { items.addAll($items2.items); })*
        (fac2=facility_declaration { facs.add($fac2.dec); })*
        (vars2=variable_declaration { vars.addAll($vars2.decs); })*
        (aux_vars2=aux_variable_declaration { aux_vars.addAll($aux_vars2.decs); })*
        (sts=statement_sequence)
        { $item = new InitItem(loc, items, req, ens, facs, vars, aux_vars, $sts.stmts); }
    ;

facility_init_final_section returns [InitItem item = null]
@init{   Location loc = null; //dummy
    edu.clemson.cs.r2jt.collections.List<AffectsItem> items = new edu.clemson.cs.r2jt.collections.List<AffectsItem>("AffectsItem");
    edu.clemson.cs.r2jt.collections.List<FacilityDec> facs = new edu.clemson.cs.r2jt.collections.List<FacilityDec>("FacilityDec");
    edu.clemson.cs.r2jt.collections.List<VarDec> vars = new edu.clemson.cs.r2jt.collections.List<VarDec>("VarDec");
    edu.clemson.cs.r2jt.collections.List<AuxVarDec> aux_vars = new edu.clemson.cs.r2jt.collections.List<AuxVarDec>("AuxVarDec");
}
    :   (items2=affects_clause { items.addAll($items2.items); })*
        (req=requires_clause)?
        (ens=ensures_clause)?
        (fac2=facility_declaration { facs.add($fac2.dec); })*
        (vars2=variable_declaration { vars.addAll($vars2.decs); })*
        (aux_vars2=aux_variable_declaration { aux_vars.addAll($aux_vars2.decs); })*
        (sts=statement_sequence)
        { $item = new InitItem(loc, items, $req.exp, $ens.exp, facs, vars, aux_vars, $sts.stmts); }
    ;

// ===============================================================
// Statements
// ===============================================================

statement returns [Statement stmt = null]
    :   ^(  STATEMENT
            (   stmt1=function_assignment { $stmt = $stmt1.stmt; }
            |   stmt2=forget_statement { $stmt = $stmt2.stmt; }
            |   stmt3=if_statement { $stmt = $stmt3.stmt; }
            |   stmt4=iterate_loop_statement { $stmt = $stmt4.stmt; }
            |   stmt5=procedure_operation_call { $stmt = $stmt5.stmt; }
            |   stmt6=remember_statement { $stmt = $stmt6.stmt; }
            |   stmt7=selection_statement { $stmt = $stmt7.stmt; }
            |   stmt8=swap_statement { $stmt = $stmt8.stmt; }
            |   stmt9=while_loop_statement { $stmt = $stmt9.stmt; }
            |   stmt10=confirm_statement { $stmt = $stmt10.stmt; }
            |   stmt11=assume_statement { $stmt = $stmt11.stmt; }
            |   stmt12=aux_code_statement { $stmt = $stmt12.stmt; }
            )
        )
    ;
    
in_aux_statement returns [Statement stmt = null]
    :   ^(  STATEMENT
            (   stmt1=function_assignment { $stmt = $stmt1.stmt; }
            |   stmt2=forget_statement { $stmt = $stmt2.stmt; }
            |   stmt3=if_statement { $stmt = $stmt3.stmt; }
            |   stmt4=iterate_loop_statement { $stmt = $stmt4.stmt; }
            //|   stmt5=procedure_operation_call { $stmt = $stmt5.stmt; }
            |   stmt6=remember_statement { $stmt = $stmt6.stmt; }
            |   stmt7=selection_statement { $stmt = $stmt7.stmt; }
            |   stmt8=swap_statement { $stmt = $stmt8.stmt; }
            |   stmt9=while_loop_statement { $stmt = $stmt9.stmt; }
            |   stmt10=confirm_statement { $stmt = $stmt10.stmt; }
            |   stmt11=assume_statement { $stmt = $stmt11.stmt; }
            |   stmt12=aux_code_statement { $stmt = $stmt12.stmt; }
            )
        )
    ;

statement_sequence returns [edu.clemson.cs.r2jt.collections.List<Statement> stmts
        = new edu.clemson.cs.r2jt.collections.List<Statement>("Statement")]
    :   ^(STATEMENT_SEQUENCE (stmt=statement { $stmts.add($stmt.stmt); })*)
    ;
    
in_aux_statement_sequence returns [edu.clemson.cs.r2jt.collections.List<Statement> stmts
        = new edu.clemson.cs.r2jt.collections.List<Statement>("Statement")]
    :   ^(STATEMENT_SEQUENCE (stmt=in_aux_statement { $stmts.add($stmt.stmt); })*)
    ;

// Aux Code statement --------------------------------------------------

aux_code_statement returns [AuxCodeStmt stmt = null]
    :   ^(  AUX_CODE statements=in_aux_statement_sequence
        )
        { $stmt = new AuxCodeStmt($statements.stmts); }
    ;


// Function assignment -------------------------------------------

function_assignment returns [FuncAssignStmt stmt = null]
    :   ^(ASSIGN_OP var=variable_expression exp=program_expression)
    //: ^(var=variable_expression ASSIGN_OP exp=program_expression)
        { $stmt = new FuncAssignStmt(getLocation($ASSIGN_OP), $var.exp, $exp.exp); }
    ;

// Forget and remember -------------------------------------------

forget_statement returns [MemoryStmt stmt = null]
    :   FORGET { $stmt = new MemoryStmt(getLocation($FORGET), false); }
    ;

remember_statement returns [MemoryStmt stmt = null]
    :   REMEMBER { $stmt = new MemoryStmt(getLocation($REMEMBER), true); }
    ;

    
// If statement --------------------------------------------------

if_statement returns [IfStmt stmt = null]
@init{
    edu.clemson.cs.r2jt.collections.List<ConditionItem> condItems = new edu.clemson.cs.r2jt.collections.List<ConditionItem>("ConditionItem");
}
    :   ^(  IF cond=condition thenStmts=statement_sequence
            (condItem=elsif_item { condItems.add($condItem.item); })*
            (elseStmts=else_part)?
        )
        { $stmt = new IfStmt($cond.exp, $thenStmts.stmts, condItems, $elseStmts.stmts); }
    ;

elsif_item returns [ConditionItem item = null]
    :   ^(ELSIF cond=condition thenStmts=statement_sequence)
        { $item = new ConditionItem($cond.exp, $thenStmts.stmts); }
    ;

else_part returns [edu.clemson.cs.r2jt.collections.List<Statement> stmts = new edu.clemson.cs.r2jt.collections.List<Statement>("Statement")]
    :   ^(ELSE stmts1=statement_sequence) { $stmts = $stmts1.stmts; }
    ;

condition returns [ProgramExp exp = null]
    :   exp1=program_expression { $exp = $exp1.exp; }
    ;

// Iterate statement ---------------------------------------------

iterate_loop_statement returns [IterateStmt stmt = null]
    :   ^(  ITERATE (chans=changing_clause)?
            main=maintaining_clause (decr=decreasing_clause)?
            stmts=iterate_item_sequence
        )
        { $stmt = new IterateStmt($chans.exps, $main.exp, $decr.exp, $stmts.stmts); }
    ;

iterate_item_sequence returns [edu.clemson.cs.r2jt.collections.List<Statement> stmts
        = new edu.clemson.cs.r2jt.collections.List<Statement>("Statement")]
    :   (stmt=iterate_item { $stmts.add($stmt.stmt); })+
    ;

iterate_item returns [Statement stmt = null]
    :   stmt1=statement { $stmt = $stmt1.stmt; }
    |   stmt2=iterate_exit_statement { $stmt = $stmt2.stmt; }
    ;

iterate_exit_statement returns [IterateExitStmt stmt = null]
    :   ^(ITERATE_EXIT test=condition stmts=statement_sequence)
        { $stmt = new IterateExitStmt($test.exp, $stmts.stmts); }
    ;     

// Procedure call ------------------------------------------------

procedure_operation_call returns [CallStmt stmt = null]
@init{
    PosSymbol qual = null;
    PosSymbol name = null;
    edu.clemson.cs.r2jt.collections.List<ProgramExp> args = new edu.clemson.cs.r2jt.collections.List<ProgramExp>("ProgramExp");
}
    :   ^(FUNCTION qid=qualified_ident args2=operation_argument_section)
        {   qual = $qid.exp.getQualifier();
            name = $qid.exp.getName();
            $stmt = new CallStmt(qual, name,
                                  $args2.exps!=null?$args2.exps:args);
        }
    ;

operation_argument_section returns [edu.clemson.cs.r2jt.collections.List<ProgramExp> exps
        = new edu.clemson.cs.r2jt.collections.List<ProgramExp>("ProgramExp")]
    :   ^(PARAMS (exp=program_expression { $exps.add($exp.exp); })*)
    ;

// Selection statement -------------------------------------------

selection_statement returns [SelectionStmt stmt = null]
    :   ^(  CASE exp=program_expression
            whens=selection_alternative_sequence
            (def=default_alternative)?
        )
        { $stmt = new SelectionStmt($exp.exp, $whens.items, $def.stmts); }
    ;

selection_alternative_sequence returns [edu.clemson.cs.r2jt.collections.List<ChoiceItem> items
        = new edu.clemson.cs.r2jt.collections.List<ChoiceItem>("ChoiceItem")]
    :   (item=selection_alternative { $items.add($item.item); })+
    ;

selection_alternative returns [ChoiceItem item = null]
    :   ^(WHEN test=choices thens=statement_sequence)
        { $item = new ChoiceItem($test.exps, $thens.stmts); }
    ;

default_alternative returns [edu.clemson.cs.r2jt.collections.List<Statement> stmts
        = new edu.clemson.cs.r2jt.collections.List<Statement>("Statement")]
    :   ^(DEFAULT stmts1=statement_sequence) { $stmts = $stmts1.stmts; }
    ;

choice returns [ProgramExp exp = null]
    :   exp1=program_expression { $exp = $exp1.exp; }
    ;

choices returns [edu.clemson.cs.r2jt.collections.List<ProgramExp> exps = new edu.clemson.cs.r2jt.collections.List<ProgramExp>("ProgramExp")]
    :   ^(CHOICES (exp=choice { $exps.add($exp.exp); })+)
    ;

// Swap statement ------------------------------------------------

swap_statement returns [SwapStmt stmt = null]
    :   ^(SWAP_OP exp2=variable_expression exp3=variable_expression)
    //:   exp2=variable_expression SWAP_OP^ exp3=variable_expression
        { $stmt = new SwapStmt(getLocation($SWAP_OP), $exp2.exp, $exp3.exp); }
    ;
    
// Confirm statement ------------------------------------------------

confirm_statement returns [ConfirmStmt stmt = null]
    :   ^(CONFIRM exp2=math_expression)
        { $stmt = new ConfirmStmt(getLocation($CONFIRM), $exp2.exp); }
    ;

// Assume statement ------------------------------------------------

assume_statement returns [AssumeStmt stmt = null]
    :   ^(ASSUME exp2=math_expression)
        { $stmt = new AssumeStmt(getLocation($ASSUME), $exp2.exp); }
    ;

// While loop ----------------------------------------------------

while_loop_statement returns [WhileStmt stmt = null]
    :   ^(  WHILE test=condition (chans=changing_clause)?    
            (main=maintaining_clause)? (decr=decreasing_clause)?
            (elasptime=elapsed_time_clause)?
            stmts=statement_sequence
        )
        { $stmt = new WhileStmt(getLocation($WHILE), $test.exp, $chans.exps, $main.exp, $decr.exp, $elasptime.exp, $stmts.stmts); }
    ;

maintaining_clause returns [Exp exp = null]
    :   ^(MAINTAINING exp1=math_expression) { $exp = $exp1.exp; }
    ;

decreasing_clause returns [Exp exp = null]
    :   ^(DECREASING exp1=adding_expression) { $exp = $exp1.exp; }
    ;

elapsed_time_clause returns [Exp exp = null]
    :   ^(ELAPSED_TIME exp1=math_expression) { $exp = $exp1.exp; }
    ;

changing_clause returns [edu.clemson.cs.r2jt.collections.List<VariableExp> exps
        = new edu.clemson.cs.r2jt.collections.List<VariableExp>("VariableExp")]
    :   ^(CHANGING (exp=variable_expression { $exps.add($exp.exp); })+)
    ;

// ===============================================================
// Program Type Expression Grammar
// ===============================================================

program_type_expression returns [Ty ty = null]
@init{   PosSymbol qual = null;
    PosSymbol name = null;
    ProgramExp lo = null;
    ProgramExp hi = null;
}
    :   ^(TYPEX qid=qualified_ident)
                {   qual = $qid.exp.getQualifier();
                    name = $qid.exp.getName();
                    $ty = new NameTy(qual, name);
                }
        |
        ^(ARRAY ran=array_range ent=program_type_expression)
                {   assert $ran.exps.size() == 2 : "ran.size() != 2";
                    lo = $ran.exps.get(0);
                    hi = $ran.exps.get(1);
                    $ty = new ArrayTy(getLocation($ARRAY), lo, hi, $ent.ty);
                }
    ;

structural_program_type_expression returns [Ty ty = null]
@init{   edu.clemson.cs.r2jt.collections.List<VarDec> decs = new edu.clemson.cs.r2jt.collections.List<VarDec>("VarDec");
}
    :   ^(  RECORD
            (decs2=record_variable_declaration_group { decs.addAll($decs2.decs); })+
        )
        { $ty = new RecordTy(getLocation($RECORD), decs); }
    |   ty1=program_type_expression { $ty = $ty1.ty; }
    ;

record_variable_declaration_group returns [edu.clemson.cs.r2jt.collections.List<VarDec> decs
        = new edu.clemson.cs.r2jt.collections.List<VarDec>("VarDec")]
    :   ^(VAR psyms=variable_id_list ty=program_type_expression)
        { $decs = getVarDecList($psyms.psyms, $ty.ty); }
    ;
    
record_aux_variable_declaration_group returns [edu.clemson.cs.r2jt.collections.List<AuxVarDec> decs
        = new edu.clemson.cs.r2jt.collections.List<AuxVarDec>("VarDec")]
    :   ^(AUX_VAR psyms=variable_id_list ty=program_type_expression)
        { $decs = getAuxVarDecList($psyms.psyms, $ty.ty); }
    ;

array_range returns [edu.clemson.cs.r2jt.collections.List<ProgramExp> exps
        = new edu.clemson.cs.r2jt.collections.List<ProgramExp>("ProgramExp")]
    :   ^(RANGE exp2=program_expression exp3=program_expression)
        { $exps.add($exp2.exp); $exps.add($exp3.exp); }
    ;

variable_declaration_group returns [edu.clemson.cs.r2jt.collections.List<VarDec> decs
        = new edu.clemson.cs.r2jt.collections.List<VarDec>("VarDec")]
    :   psyms=variable_id_list ty=program_type_expression
        { $decs = getVarDecList($psyms.psyms, $ty.ty); }
    ;
   
    
aux_variable_declaration_group returns [edu.clemson.cs.r2jt.collections.List<AuxVarDec> decs
        = new edu.clemson.cs.r2jt.collections.List<AuxVarDec>("AuxVarDec")]
    :   psyms=variable_id_list ty=program_type_expression
        { $decs = getAuxVarDecList($psyms.psyms, $ty.ty); }
    ;

variable_id_list returns [edu.clemson.cs.r2jt.collections.List<PosSymbol> psyms
        = new edu.clemson.cs.r2jt.collections.List<PosSymbol>("PosSymbol")]
    :   (ps=ident { $psyms.add($ps.ps); })+
    ;

// ===============================================================
// Math Type Expression Grammar
// ===============================================================

math_type_expression returns [ArbitraryExpTy ty = null]
    :   //^(TYPEX ty1=function_type_expression? { $ty = $ty1.ty; })
    //|   ^(BOOLEAN { $ty = new BooleanTy(); })
    i=infix_expression { $ty = new ArbitraryExpTy($i.exp); }
    ;

/*
function_type_expression returns [Ty ty = null]
    :   ty1=structural_math_type_expression { $ty = $ty1.ty; }
    |   ^(FUNCARROW ty2=structural_math_type_expression 
        ty3=structural_math_type_expression*)
        { $ty = new FunctionTy($ty2.ty, $ty3.ty); }
    ;
*/

/*
structural_math_type_expression returns [Ty ty = null]
@init{
   edu.clemson.cs.r2jt.collections.List<MathVarDec> vars = new edu.clemson.cs.r2jt.collections.List<MathVarDec>("MathVarDec");
}
    :   ^(  CARTPROD
            (   vars2=cartprod_variable_declaration_group
                { vars.addAll($vars2.vars); }
            )+
        )
        { $ty = new CartProdTy(getLocation($CARTPROD), vars); }
    |   ty1=product_type_expression { $ty = $ty1.ty; }
    ;


product_type_expression returns [Ty ty = null]
@init{   edu.clemson.cs.r2jt.collections.List<Ty> tys = new edu.clemson.cs.r2jt.collections.List<Ty>("Ty");
}
    :   ty1=primitive_type_expression { $ty = $ty1.ty; }
    |   ^(TIMES (ty2=primitive_type_expression { tys.add($ty2.ty); })+)
        { $ty = new TupleTy(getLocation($TIMES), tys); }
    ;

// Slightly different from RParser.g
primitive_type_expression returns [Ty ty = null]
@init{
    PosSymbol qual = null; //dummy
}
    :   BOOLEAN { $ty = new BooleanTy(qual, getPosSymbol($BOOLEAN)); }
    |   ty1=powerset_expression { $ty = $ty1.ty; }
    |   ty2=nested_type_expression { $ty = $ty2.ty; }
    |   qid=qualified_ident
        { $ty = new NameTy($qid.exp.getQualifier(), $qid.exp.getName()); }
    |   ^(FUNCTION qid=qualified_ident tys=type_expression_argument_list?)
        { $ty = new ConstructedTy($qid.exp.getQualifier(), $qid.exp.getName(), $tys.tys); }
    ;
    
powerset_expression returns [ConstructedTy ty = null]
@init{
  PosSymbol qual = null;
  PosSymbol name = null;
  edu.clemson.cs.r2jt.collections.List<Ty> args = new edu.clemson.cs.r2jt.collections.List<Ty>();
}
    :   ^(POWERSET arg=math_type_expression)
        { name=new PosSymbol(getLocation($POWERSET),Symbol.symbol("Powerset"));
          args.add($arg.ty);
          $ty=new ConstructedTy(qual, name, args); }
    ;

nested_type_expression returns [Ty ty = null]
    //:   ty1=implicit_type_parameter_group { $ty = $ty1.ty; }
    :   ty1=type_expression { $ty = $ty1.ty; }
    ;
    
type_expression returns [Ty ty = null]
    //:   (math_type_expression) => implicit_type_parameter_group
    :   ty2=math_type_expression { $ty = $ty2.ty; }
    ;
*/    

type_expression_argument_list returns [edu.clemson.cs.r2jt.collections.List<Ty> tys = new edu.clemson.cs.r2jt.collections.List<Ty>("Ty")]
    :   ^(PARAMS (ty2=math_type_expression { $tys.add($ty2.ty); })+)
    ;

cartprod_variable_declaration_group returns [edu.clemson.cs.r2jt.collections.List<MathVarDec> vars
        = new edu.clemson.cs.r2jt.collections.List<MathVarDec>("MathVarDec")]
    :   ^(VAR vars1=math_variable_declaration_group) { $vars = $vars1.vars; }
    ;
    
structural_math_variable_declaration_group
    :   variable_id_list COLON math_type_expression
    ;

math_variable_declaration_group returns [edu.clemson.cs.r2jt.collections.List<MathVarDec> vars
        = new edu.clemson.cs.r2jt.collections.List<MathVarDec>("MathVarDec")]
    :   psyms=variable_id_list COLON ty=math_type_expression
        { $vars = getMathVarDecList($psyms.psyms, $ty.ty); }
    ; 

math_variable_declaration returns [MathVarDec var = null]
    :   ps=ident COLON ty=math_type_expression
        { $var = new MathVarDec($ps.ps, $ty.ty); }
    ;
    
implicit_type_parameter_group returns [Ty ty = null]
@init{
  PosSymbol qual = null;
  edu.clemson.cs.r2jt.collections.List<Ty> args = new edu.clemson.cs.r2jt.collections.List<Ty>();
}
    :   ps=ident exp=math_expression
        //{
          //args.add($exp.ty);
          //$ty = new ConstructedTy(qual, $ps.ps, args);
        //}
    ;

// ===============================================================
// Resolve Expression/Math_Expression Grammar
// ===============================================================

math_expression returns [Exp exp = null]
    :   ^(  EXPR
            (   exp1=iterated_construct { $exp = $exp1.exp; }
            |   exp2=quantified_expression { $exp = $exp2.exp; }
            )
        )
    ;

quantified_expression returns [Exp exp = null]
@init{
    int op = 0;
}
    :   exp1=implies_expression { $exp = $exp1.exp; }
    |   (   ^(  id=FORALL vars=math_variable_declaration_group
                (where=where_clause)?
                body=quantified_expression)
            { op = QuantExp.FORALL; }
        |   ^(  id=EXISTS vars=math_variable_declaration_group
                (where=where_clause)?
                body=quantified_expression)
            { op = QuantExp.EXISTS; }
        |   ^(  id=EXISTS_UNIQUE vars=math_variable_declaration_group
                (where=where_clause)?
                body=quantified_expression)
            { op = QuantExp.UNIQUE; }
        )
        { $exp = new QuantExp(getLocation($id), op, $vars.vars, $where.exp, $body.exp); }
    ;

implies_expression returns [Exp exp = null]
    :   exp1=logical_expression { $exp = $exp1.exp; }
    |   (   ^(id=IMPLIES lf=logical_expression rt=logical_expression)
        |   ^(id=IFF lf=logical_expression rt=logical_expression)
        )
        {
            BooleanType b = BooleanType.INSTANCE;
            $exp = new InfixExp(getLocation($id), $lf.exp, getPosSymbol($id), $rt.exp, b);
        }
    |   ^(  IF test=logical_expression then=logical_expression
            (other=logical_expression)?
        )
        { $exp = new IfExp(getLocation($IF), $test.exp, $then.exp, $other.exp); }
    ;

logical_expression returns [Exp exp = null]
    :   exp1=relational_expression { $exp = $exp1.exp; }
    |   (   ^(id=AND lf=logical_expression rt=relational_expression)
        |   ^(id=OR lf=logical_expression rt=relational_expression)
        )
        {
            BooleanType b = BooleanType.INSTANCE;
            $exp = new InfixExp(getLocation($id), $lf.exp, getPosSymbol($id), $rt.exp, b);
        }
        //{ $exp = new BooleanExp(getLocation($id), $lf.exp, op, $rt.exp); }
    ; 

relational_expression returns [Exp exp = null]
@init{   //edu.clemson.cs.r2jt.collections.List<Exp> lss = new edu.clemson.cs.r2jt.collections.List<Exp>("Exp");
    int op = 0;
}
    :   exp1=infix_expression { $exp = $exp1.exp; }
    |   exp2=between_expression //^(BETWEEN_EXPR (ls=between_expression { lss.add($ls.exp); })+)
          { $exp = $exp2.exp; }
        //{ $exp = new BetweenExp(getLocation($BETWEEN_EXPR), lss.lss); }
    |   (   ^(id=EQL lf=infix_expression rt=infix_expression)
            { op = EqualsExp.EQUAL; }
        |   ^(id=NOT_EQL lf=infix_expression rt=infix_expression)
            { op = EqualsExp.NOT_EQUAL; }
        )
        { $exp = new EqualsExp(getLocation($id), $lf.exp, op, $rt.exp); } 
    |   (   ^(id=LT lf=infix_expression rt=infix_expression)
        |   ^(id=LT_EQL lf=infix_expression rt=infix_expression)
        |   ^(id=GT lf=infix_expression rt=infix_expression)
        |   ^(id=GT_EQL lf=infix_expression rt=infix_expression)
        //|   ^(id=IN lf=infix_expression rt=infix_expression)   
        //|   ^(id=NOT_IN lf=infix_expression rt=infix_expression)
        |   ^(id=SUBSET lf=infix_expression rt=infix_expression)
        |   ^(id=NOT_SUBSET lf=infix_expression rt=infix_expression)
        |   ^(id=PROP_SUBSET lf=infix_expression rt=infix_expression)
        |   ^(id=NOT_PROP_SUBSET lf=infix_expression rt=infix_expression)
        |   ^(id=SUBSTR lf=infix_expression rt=infix_expression)
        |   ^(id=NOT_SUBSTR lf=infix_expression rt=infix_expression)
        )
        { $exp = new InfixExp(getLocation($id), $lf.exp, getPosSymbol($id), $rt.exp); }
    |   (   ^(id=IN lf=infix_expression rt=infix_expression)
            { op = IsInExp.IS_IN; }
        |   ^(id=NOT_IN lf=infix_expression rt=infix_expression)
            { op = IsInExp.IS_IN; }
        )
        { 
          $exp = new IsInExp(getLocation($id), $lf.exp, op, $rt.exp);
          //Type t = new IsInType(getPosSymbol($id), null);
          //$exp = new InfixExp(getLocation($id), $lf.exp, getPosSymbol($id), $rt.exp, t);
        }
        
    ;

between_expression returns [Exp exp = null]
@init{
   edu.clemson.cs.r2jt.collections.List<Exp> lss = new edu.clemson.cs.r2jt.collections.List<Exp>("Exp");
   Exp lf_temp = null;
   Exp conjuncts = null;
   Exp conjunct;
}
    :   
          ^(BETWEEN_EXPR (lf=infix_expression { lf_temp = $lf.exp; }
            (
            (id=LT | id=LT_EQL)
            rt=infix_expression
            {
              conjunct = new InfixExp(getLocation($id), lf_temp, 
                      getPosSymbol($id), $rt.exp);  
              lss.add(conjunct);
              lf_temp = $rt.exp;
              
              if (conjuncts == null) {
                  conjuncts = conjunct;
              }
              else {
                  PosSymbol andPosSymbol = getPosSymbol($id);
                  andPosSymbol.setSymbol(Symbol.symbol("and"));
                  conjuncts = new InfixExp(getLocation($id), conjuncts,
                      andPosSymbol, conjunct);
              }
            } )+
            ))
            { $exp = conjuncts; }
    ;
        
    //:   (   ^(id=LT lf=infix_expression rt=infix_expression)
    //    |   ^(id=LT_EQL lf=infix_expression rt=infix_expression)
    //    )
    //    { $exp = new InfixExp(getLocation($id), $lf.exp, getPosSymbol($id), $rt.exp); }
    //;

infix_expression returns [Exp exp = null]
    :   
          ^(LOCALVAREXP locals=math_variable_declarations lf=math_expression { $exp = new QuantExp(getLocation($LOCALVAREXP), QuantExp.NONE, $locals.decs, null, $lf.exp); })
    |
        (exp1=function_type_expression { $exp = $exp1.exp; }
    |   (   ^(id=RANGE lf1=function_type_expression rt=function_type_expression)
        |   ^(id=FREE_OPERATOR lf1=function_type_expression rt=function_type_expression)
        )
        { $exp = new InfixExp(getLocation($id), $lf1.exp, getPosSymbol($id), $rt.exp); }
        )
    |   id=BOOLEAN { $exp = new VarExp(getLocation($id), null, getPosSymbol($id), BooleanType.INSTANCE); }
    ;

function_type_expression returns [Exp exp]
    :  left=adding_expression { $exp = $left.exp; }
    |  ^(id=FUNCARROW left=adding_expression right=function_expression) //Right associate
       { $exp = new InfixExp(getLocation($id), $left.exp, getPosSymbol($id), 
                             $right.exp); } 
    ;

adding_expression returns [Exp exp = null]
    :   exp1=multiplying_expression  { $exp = $exp1.exp; }
    |   (   ^(id=PLUS lf=adding_expression rt=multiplying_expression)
        |   ^(id=MINUS lf=adding_expression rt=multiplying_expression)
        |   ^(id=CAT lf=adding_expression rt=multiplying_expression)
        |   ^(id=UNION lf=adding_expression rt=multiplying_expression)
        |   ^(id=INTERSECT lf=adding_expression rt=multiplying_expression)
        |   ^(id=WITHOUT lf=adding_expression rt=multiplying_expression)
        )
        { $exp = new InfixExp(getLocation($id), $lf.exp, getPosSymbol($id), $rt.exp); }
    ;

multiplying_expression returns [Exp exp = null]
    :   exp1=exponential_expression { $exp = $exp1.exp; }
    |   (   ^(id=MULTIPLY lf=multiplying_expression rt=exponential_expression)
        |   ^(id=DIVIDE lf=multiplying_expression rt=exponential_expression)
        |   ^(id=MOD lf=multiplying_expression rt=exponential_expression)
        |   ^(id=REM lf=multiplying_expression rt=exponential_expression)
        |   ^(id=DIV lf=multiplying_expression rt=exponential_expression)
        )
        { $exp = new InfixExp(getLocation($id), $lf.exp, getPosSymbol($id), $rt.exp); }
    ;

exponential_expression returns [Exp exp = null]
    :   exp1=prefix_expression  { $exp = $exp1.exp; }
    |   ^(EXP lf=prefix_expression rt=exponential_expression)
        { $exp = new InfixExp(getLocation($EXP), $lf.exp, getPosSymbol($EXP), $rt.exp); }
    ;

prefix_expression returns [Exp exp = null]
    :   exp1=unary_expression { $exp = $exp1.exp; } ;

unary_expression returns [Exp exp = null]
    :   exp1=primitive_expression { $exp = $exp1.exp; }
    |   (   ^(id=NOT arg=unary_expression)
            { $exp = new PrefixExp(getLocation($id), getPosSymbol($id), $arg.exp, BooleanType.INSTANCE); }
        |   ^(id=COMPLEMENT arg=unary_expression)
            { $exp = new PrefixExp(getLocation($id), getPosSymbol($id), $arg.exp); }
        |   ^(id=UNARY_MINUS arg=unary_expression)
            { $exp = new PrefixExp(getLocation($id), getPosSymbol($id), $arg.exp); }
        )
        
    ;

primitive_expression returns [Exp exp = null]
@init{
  //VarExp ve = null;
  //PosSymbol name = null;
}
    :   exp1=alternative_expression { $exp = $exp1.exp; }
    |   exp2=dot_expression { $exp = $exp2.exp; }
    |   exp3=lambda_expression { $exp = $exp3.exp; }
    |   exp4=literal_expression { $exp = $exp4.exp; }
    |   exp5=outfix_expression { $exp = $exp5.exp; }
    |   exp6=set_constructor { $exp = $exp6.exp; }
    |   exp7=tuple_expression { $exp = $exp7.exp; }
    |   exp8=nested_expression { $exp = $exp8.exp; }
    |   exp9=iterated_construct { $exp = $exp9.exp; }
    |   exp10=tagged_cartesian_product_type_expression { $exp = $exp10.exp; }
    //|   name=ident { $exp=(ve=new VarExp(getLocation($name.ps),null,$name.ps)); }
    ;
    
tagged_cartesian_product_type_expression 
returns [CrossTypeExpression exp = null]
    :   id=CARTPROD^ { $exp = new CrossTypeExpression(getLocation($id)); } 
        (varList=cartprod_variable_declaration_group SEMICOLON!
            {
              for (MathVarDec d : $varList.vars) {
                $exp.addTaggedField(d.getName(), 
                    ((ArbitraryExpTy) d.getTy()).getArbitraryExp());
              }
            }
        )+
        END!;

// ---------------------------------------------------------------
// Articulated expression rules (expression with '.')
// ---------------------------------------------------------------

dot_expression returns [Exp exp = null]
@init{
    edu.clemson.cs.r2jt.collections.List<Exp> segs = new edu.clemson.cs.r2jt.collections.List<Exp>("Exp");
    Exp sem = null;
}
    :   exp1=function_expression { $exp = $exp1.exp; }
    |   ^(DOT (seg=function_expression { segs.add($seg.exp); })
        seg1=clean_function_expression* { segs.add($seg1.exp); })
        { $exp = new DotExp(getLocation($DOT), segs, sem); }
    ;

function_expression returns [Exp exp = null]
    :   exp1=clean_function_expression { $exp = $exp1.exp; }
    |   ^(HASH exp2=clean_function_expression)
        { $exp = new OldExp(getLocation($HASH), $exp2.exp); }
    ;

clean_function_expression returns [Exp exp = null]
@init{
    PosSymbol qual = null; //dummy
    edu.clemson.cs.r2jt.collections.List<FunctionArgList> aGrps = new edu.clemson.cs.r2jt.collections.List<FunctionArgList>("FunctionArgList");
}
    :   ps=ident
        { $exp = new VarExp(getLocation((ColsAST)ps.getTree()), qual, $ps.ps); }
    |   ^(FUNCTION ps=ident (hat=hat_expression)?
        (aGrp=function_argument_list { aGrps.add($aGrp.list); })+)
        { $exp = new FunctionExp(getLocation($FUNCTION), qual, $ps.ps, $hat.exp, aGrps); }
    ;

hat_expression returns [Exp exp = null]
    :   ^(CARAT (exp1=qualified_ident { $exp = $exp1.exp; }
    |   exp2=adding_expression) { $exp = $exp2.exp; })
    ;

function_argument_list returns [FunctionArgList list = null]
@init{
    edu.clemson.cs.r2jt.collections.List<Exp> args = new edu.clemson.cs.r2jt.collections.List<Exp>("Exp");
}
    :   ^(PARAMS (arg=math_expression { args.add($arg.exp); })+)
        { $list = new FunctionArgList(args); }
    ;

// ---------------------------------------------------------------
// Auxilliary expression rules
// ---------------------------------------------------------------

alternative_expression returns [AlternativeExp exp = null]
@init{
    edu.clemson.cs.r2jt.collections.List<AltItemExp> alts = new edu.clemson.cs.r2jt.collections.List<AltItemExp>("AltItemExp");
}
    :   ^(DBL_LBRACE (alt=alternative_expression_item { alts.add($alt.exp); })+)
        { $exp = new AlternativeExp(getLocation($DBL_LBRACE), alts); }
    ;

alternative_expression_item returns [AltItemExp exp = null]
    :   (   ^(id=IF assign=adding_expression test=relational_expression)
        |   ^(id=OTHERWISE assign=adding_expression)
        )
        { $exp = new AltItemExp(getLocation($id), $test.exp, $assign.exp); }
    ;

iterated_construct returns [IterativeExp exp = null]
@init{
    int op = 0;
    MathVarDec var = null;
}
    :   ^(  ITERATION inm=ident vnm=ident
            vty=math_type_expression 
            (where=where_clause)?
            body=math_expression
        )
        {   op = getIterativeOp($inm.ps);
            var = new MathVarDec($vnm.ps, $vty.ty);
            $exp = new IterativeExp(getLocation($ITERATION), op, var, $where.exp, $body.exp);
        }   
    ;

lambda_expression returns [LambdaExp exp = null]
@init{
    Ty ty = null;
    PosSymbol tql = null;
    PosSymbol tnm = null;
}
    :   ^(  LAMBDA ps=ident
            (   qid=certain_qualified_ident
                { tql = $qid.exp.getQualifier(); tnm = $qid.exp.getName(); }
            |   tnm1=ident { tnm = $tnm1.ps; }
            )
            body=math_expression
        )
        {   ty = new NameTy(tql, tnm);
            $exp = new LambdaExp(getLocation($LAMBDA), $ps.ps, ty, $body.exp);
        }
    ;

literal_expression returns [Exp exp = null]
@init{
    int ival = 0;
    double dval = 0.0;
    Character ch = null;
    String str = null;
}
    :   exp1=qualified_numeric_literal
    |   NUMERIC_LITERAL
        {   str = $NUMERIC_LITERAL.getText();
            if (str.indexOf('.') == -1) { // a dot does not appear
                try {
                    ival = (int)Integer.valueOf(str).intValue();
                } catch (Exception e) { ; } //FIX: add error here
                $exp = new IntegerExp(getLocation($NUMERIC_LITERAL), null, ival);
            } else {
                try {
                    dval = (double)Double.valueOf(str).doubleValue();
                } catch (Exception e) { ; } //FIX: add error here
                $exp = new DoubleExp(getLocation($NUMERIC_LITERAL), dval);
            }
        }
    |   CHARACTER_LITERAL
        {   str = $CHARACTER_LITERAL.getText();
            ch = new Character(str.charAt(1));
            $exp = new CharExp(getLocation($CHARACTER_LITERAL), ch);
        }
    |   STRING_LITERAL
        {   str = $STRING_LITERAL.getText();
            $exp = new StringExp(getLocation($STRING_LITERAL), str);
        }
    ;
    
numeric_lit returns [Exp exp = null]
@init{
  int ival = 0;
  double dval = 0.0;
  String str = null;
}
    :   NUMERIC_LITERAL
        {   str = $NUMERIC_LITERAL.getText();
            if (str.indexOf('.') == -1) { // a dot does not appear
                try {
                    ival = (int)Integer.valueOf(str).intValue();
                } catch (Exception e) { ; } //FIX: add error here
                $exp = new IntegerExp(getLocation($NUMERIC_LITERAL), null, ival);
            } else {
                try {
                    dval = (double)Double.valueOf(str).doubleValue();
                } catch (Exception e) { ; } //FIX: add error here
                $exp = new DoubleExp(getLocation($NUMERIC_LITERAL), dval);
            }
        }
    ;
    
qualified_numeric_literal returns [Exp exp = null]
    :   ^(QUALNUM qual=ident exp1=numeric_lit
        {
            if($exp1.exp instanceof IntegerExp) {
              $exp = $exp1.exp;
              ((IntegerExp)$exp).setQualifier($qual.ps);
            }
        }
        )
    ;

nested_expression returns [Exp exp = null]
    :   ^(NESTED exp1=math_expression)
        { $exp = $exp1.exp;  }
    ;

outfix_expression returns [OutfixExp exp = null]
@init{
    int op = 0;
    Exp arg = null;
}
    :   (   ^(id=ANGLE { op = OutfixExp.ANGLE; } arg1=infix_expression) { arg = $arg1.exp; }
        |   ^(id=DBL_ANGLE { op = OutfixExp.DBL_ANGLE; } arg2=math_expression) { arg = $arg2.exp; }
        |   ^(id=BAR { op = OutfixExp.BAR; } arg2=math_expression) { arg = $arg2.exp; }
        |   ^(id=DBL_BAR { op = OutfixExp.DBL_BAR; } arg2=math_expression) { arg = $arg2.exp; }
        )
        { $exp = new OutfixExp(getLocation($id), op, arg); }
    ;

parenthesized_expression returns [Exp exp = null]
    :   exp1=math_expression { $exp = $exp1.exp;  }
    ;

set_constructor returns [SetExp exp = null]
@init{
    MathVarDec var = null;
}
    :   ^(  LBRACE vnm=ident vty=math_type_expression
            (where=where_clause)? body=math_expression
        )
        {   var = new MathVarDec($vnm.ps, $vty.ty);
            $exp = new SetExp(getLocation($LBRACE), var, $where.exp, $body.exp);
        }
    ;

tuple_expression returns [TupleExp exp = null]
@init{
    edu.clemson.cs.r2jt.collections.List<Exp> flds = new edu.clemson.cs.r2jt.collections.List<Exp>("Exp");
}
    :   ^(TUPLE (fld=math_expression { flds.add($fld.exp); })+)
        { $exp = new TupleExp(getLocation($TUPLE), flds); }
    ;

where_clause returns [Exp exp = null]
    :   ^(WHERE exp1=math_expression) { $exp = $exp1.exp; }
    ;

// ===============================================================
// Programming expressions
// ===============================================================

program_expression returns [ProgramExp exp = null]
    :   ^(EXPR exp1=program_logical_expression) { $exp = $exp1.exp; }
    ;

program_logical_expression returns [ProgramExp exp = null]
@init{
    int op = 0;
}
    :   exp1=program_relational_expression { $exp = $exp1.exp; }
    |   (   ^(  id=AND { op = ProgramOpExp.AND; }
                lf=program_logical_expression
                rt=program_relational_expression)
        |   ^(  id=OR { op = ProgramOpExp.OR; }
                lf=program_logical_expression
                rt=program_relational_expression)
        )
        { $exp = new ProgramOpExp(getLocation($id), op, $lf.exp, $rt.exp); }
    ;

program_relational_expression returns [ProgramExp exp = null]
@init{
    int op = 0;
}
    :   exp1=program_adding_expression { $exp = $exp1.exp; }
    |   (   ^(  id=EQL { op = ProgramOpExp.EQUAL; }
                lf=program_relational_expression
                rt=program_adding_expression)
        |   ^(  id=NOT_EQL { op = ProgramOpExp.NOT_EQUAL; }
                lf=program_relational_expression
                rt=program_adding_expression)
        |   ^(  id=LT  { op = ProgramOpExp.LT; }
                lf=program_relational_expression
                rt=program_adding_expression)
        |   ^(  id=LT_EQL  { op = ProgramOpExp.LT_EQL; }
                lf=program_relational_expression
                rt=program_adding_expression)
        |   ^(  id=GT  { op = ProgramOpExp.GT; }
                lf=program_relational_expression
                rt=program_adding_expression)
        |   ^(  id=GT_EQL { op = ProgramOpExp.GT_EQL; }
                lf=program_relational_expression
                rt=program_adding_expression)
        )
        { $exp = new ProgramOpExp(getLocation($id), op, $lf.exp, $rt.exp); }
    ;

program_adding_expression returns [ProgramExp exp = null]
@init{
    int op = 0;
}
    :   exp1=program_multiplying_expression { $exp = $exp1.exp; }
    |   (   ^(  id=PLUS { op = ProgramOpExp.PLUS; }
                lf=program_adding_expression
                rt=program_multiplying_expression)
        |   ^(  id=MINUS { op = ProgramOpExp.MINUS; }
                lf=program_adding_expression
                rt=program_multiplying_expression)
        )
        { $exp = new ProgramOpExp(getLocation($id), op, $lf.exp, $rt.exp); }
    ;

program_multiplying_expression returns [ProgramExp exp = null]
@init{
    int op = 0;
}
    :   exp1=program_exponential_expression { $exp = $exp1.exp; }
    |   (   ^(  id=MULTIPLY { op = ProgramOpExp.MULTIPLY; }
                lf=program_multiplying_expression
                rt=program_exponential_expression)
        |   ^(  id=DIVIDE { op = ProgramOpExp.DIVIDE; }
                lf=program_multiplying_expression
                rt=program_exponential_expression)
        |   ^(  id=MOD { op = ProgramOpExp.MOD; }
                lf=program_multiplying_expression
                rt=program_exponential_expression)
        |   ^(  id=REM { op = ProgramOpExp.REM; }
                lf=program_multiplying_expression
                rt=program_exponential_expression)
        |   ^(  id=DIV { op = ProgramOpExp.DIV; }
                lf=program_multiplying_expression
                rt=program_exponential_expression)
        )
        { $exp = new ProgramOpExp(getLocation($id), op, $lf.exp, $rt.exp); }
    ;

program_exponential_expression returns [ProgramExp exp = null]
@init{
    int op = 0;
}
    :   exp1=program_unary_expression { $exp = $exp1.exp; }
    |   ^(  EXP { op = ProgramOpExp.EXP; }
            lf=program_unary_expression
            rt=program_exponential_expression)
        { $exp = new ProgramOpExp(getLocation($EXP), op, $lf.exp, $rt.exp); }
    ;

program_unary_expression returns [ProgramExp exp = null]
@init{
    int op = 0;
    ProgramExp extra = null; //dummy
}
    :   exp1=program_primitive_expression { $exp = $exp1.exp; }
    |   (   ^(  id=NOT { op = ProgramOpExp.NOT; }
                arg=program_unary_expression)
        |   ^(  id=UNARY_MINUS { op = ProgramOpExp.UNARY_MINUS; }
                arg=program_unary_expression)
        )
        { $exp = new ProgramOpExp(getLocation($id), op, $arg.exp, extra); }
    ;

program_primitive_expression returns [ProgramExp exp = null]
    :   exp1=program_literal_expression { $exp = $exp1.exp; }
    |   exp2=program_variable_expression { $exp = $exp2.exp; }
    |   exp3=program_nested_expression { $exp = $exp3.exp; }
    ;

program_variable_expression returns [ProgramExp exp = null]
    :   exp1=program_dot_expression { $exp = $exp1.exp; }
    |   exp2=variable_expression { $exp = $exp2.exp; }
    ;

program_dot_expression returns [ProgramExp exp = null]
@init{
    edu.clemson.cs.r2jt.collections.List<ProgramExp> segs = new edu.clemson.cs.r2jt.collections.List<ProgramExp>("ProgramExp");
}
    :   exp1=program_function_expression { $exp = $exp1.exp; }
    |   ^(PROGDOT sem1=ident { segs.add(new VariableNameExp(getLocation($sem1.tree), null, $sem1.ps)); }
            (seg=program_function_expression { segs.add($seg.exp); })+
          )
        { $exp = new ProgramDotExp(getLocation($PROGDOT), segs, null); }
    ;

program_function_expression returns [ProgramExp exp = null]
@init{
    PosSymbol qual = null; //dummy
    ProgramExp sem = null; //dummy
    edu.clemson.cs.r2jt.collections.List<ProgramExp> args = new edu.clemson.cs.r2jt.collections.List<ProgramExp>("ProgramExp");
}
    :    ^(FUNCTION name=ident args2=program_function_argument_list)
        { $exp = new ProgramParamExp(getLocation($FUNCTION), $name.ps,
                                      $args2.args!=null?$args2.args:args,
                                      null); }
    ;

program_function_argument_list returns [edu.clemson.cs.r2jt.collections.List<ProgramExp> args
        = new edu.clemson.cs.r2jt.collections.List<ProgramExp>("ProgramExp")]
    :   ^(PARAMS (arg=program_expression {$args.add($arg.exp); })*)
    ;

program_nested_expression returns [ProgramExp exp = null]
    :   ^(NESTED exp1=program_expression) { $exp = $exp1.exp; }
    ;

program_literal_expression returns [ProgramExp exp = null]
@init{
    int ival = 0;
    double dval = 0.0;
    Character ch = null;
    String str = null;
    Exp mexp = null;
}
    :   NUMERIC_LITERAL
        {   str = $NUMERIC_LITERAL.getText();
            if (str.indexOf('.') == -1) { // a dot does not appear
                try {
                    ival = (int)Integer.valueOf(str).intValue();
                } catch (Exception e) { ; } //FIX: add error here
                mexp = new IntegerExp(getLocation($NUMERIC_LITERAL), null, ival);
                $exp = getProgramLiteral(mexp);
            } else {
                try {
                    dval = (double)Double.valueOf(str).doubleValue();
                } catch (Exception e) { ; } //FIX: add error here
                mexp = new DoubleExp(getLocation($NUMERIC_LITERAL), dval);
                $exp = getProgramLiteral(mexp);
            }
        }
    |   CHARACTER_LITERAL
        {   str = $CHARACTER_LITERAL.getText();
            ch = new Character(str.charAt(1));
            mexp = new CharExp(getLocation($CHARACTER_LITERAL), ch);
            $exp = getProgramLiteral(mexp);
        }
    |   STRING_LITERAL
        {   str = $STRING_LITERAL.getText();
            mexp = new StringExp(getLocation($STRING_LITERAL), str);
            $exp = getProgramLiteral(mexp);
        }
    ;

// ===============================================================
// Variable Expressions
// ===============================================================

variable_expression returns [VariableExp exp = null]
@init{
    edu.clemson.cs.r2jt.collections.List<VariableExp> segs = new edu.clemson.cs.r2jt.collections.List<VariableExp>("VariableExp");
    VariableExp sem = null; //dummy
}
    :   exp1=variable_array_expression
        { $exp = $exp1.exp; }
    |   ^(VARDOT (seg=variable_array_expression { segs.add($seg.exp); })+)
        { $exp = new VariableDotExp(getLocation($VARDOT), segs, sem); }
    ;

variable_array_expression returns [VariableExp exp = null]
@init{
    PosSymbol qual = null; //dummy
}
    :   name=ident
        { $exp = new VariableNameExp(getLocation($name.tree), qual, $name.ps); }
    |   ^(ARRAYFUNCTION name=ident arg=variable_array_argument_list)
        { $exp = new VariableArrayExp(getLocation($ARRAYFUNCTION), qual, $name.ps, $arg.exp); }
    ;

variable_array_argument_list returns [ProgramExp exp = null]
    :   ^(PARAMS (exp1=program_expression { $exp = $exp1.exp; })?)
    ;

// ===============================================================
// Identifiers
// ===============================================================

certain_qualified_ident returns [VarExp exp = null]
    :   ^(IDENT ps1=ident ps2=ident)
        { $exp = new VarExp(getLocation($IDENT), $ps1.ps, $ps2.ps); }
    ;

qualified_ident returns [VarExp exp = null]
@init{
    boolean qualified = false;
}
    :   ^(IDENTIFIER ps1=ident (ps2=ident { qualified = true; })?)
        {   if (qualified) {
                $exp = new VarExp(getLocation($IDENTIFIER), $ps1.ps, $ps2.ps);
            } else {
                $exp = new VarExp(getLocation($IDENTIFIER), null, $ps1.ps);
            }
        }
    ;

ident returns [PosSymbol ps = null]
    :   id=IDENTIFIER { $ps = getPosSymbol($IDENTIFIER); }
    ;

math_theorem_ident returns [PosSymbol ps = null]
    :   ps1=ident { $ps = $ps1.ps; }
    |   ast=NUMERIC_LITERAL { $ps = getPosSymbol($NUMERIC_LITERAL); }
    ;
    
// =============================================================
// ADDED FOR PARSING MATH PROOF EXPRESSIONS
// =============================================================

proof_module returns [ProofModuleDec pmd = null]
    :   ^(PROOFS_FOR moduleName=ident
        (pars=module_formal_param_section)?
        (uses=uses_list)? (decs=proof_module_body)? ident)
        { $pmd = new ProofModuleDec($moduleName.ps, $pars.pars, $uses.uses, $decs.decs); }
    ;
    
proof_module_body returns [edu.clemson.cs.r2jt.collections.List<Dec> decs = new edu.clemson.cs.r2jt.collections.List<Dec>("Math Items")]
    :   ^(PROOFBODY
        (mmdTemp=math_item_sequence { $decs.addAllUnique($mmdTemp.dec.getDecs()); }
        | tempDec=proof { $decs.add($tempDec.pd); } )* )
    ;


proof returns [ProofDec pd = null]
@init{
   // Relies on unique identifiers for theorems, lemmas, etc. right now!
   edu.clemson.cs.r2jt.collections.List<Exp> statements = new edu.clemson.cs.r2jt.collections.List<Exp>("Statements");
   edu.clemson.cs.r2jt.collections.List<Exp> baseCase = new edu.clemson.cs.r2jt.collections.List<Exp>("Base Case");
   edu.clemson.cs.r2jt.collections.List<Exp> inductiveCase = new edu.clemson.cs.r2jt.collections.List<Exp>("Inductive Case");
   Exp tempExp = null;
   boolean isBaseCase = false;
   boolean isInductiveCase = false;
   PosSymbol lineNum = null;
}
:
   ^(PROOF pd1=math_item_reference { $pd = $pd1.pd; }
       (( (^(IDENTIFIER BASECASE)) => tempExp1=base_case_statement_head
         { tempExp = $tempExp1.exp; isBaseCase=true; isInductiveCase=false; }
       | (BASECASE) => tempExp2=base_case_statement_body
         { tempExp = $tempExp2.exp; isBaseCase=true; isInductiveCase=false; }
       | (^(IDENTIFIER INDUCTIVECASE)) => tempExp3=inductive_case_statement_head
         { tempExp = $tempExp3.exp; isInductiveCase=true; isBaseCase=false; }
       | (INDUCTIVECASE) => tempExp4=inductive_case_statement_body
         { tempExp = $tempExp4.exp; isInductiveCase=true; isBaseCase=false; }
       | (IDENTIFIER) => tempExp5=headed_proof_expression
         { tempExp = $tempExp5.exp; }
       | tempExp6=proof_expression
         { tempExp = $tempExp6.exp; }
       )
       { if(isBaseCase) baseCase.add(tempExp);
        else if(isInductiveCase) inductiveCase.add(tempExp);
        else statements.add(tempExp);
       }
   )*
   )
   { $pd.setStatements(statements);
     $pd.setBaseCase(baseCase);
     $pd.setInductiveCase(inductiveCase);
   }
;

base_case_statement_head returns [Exp exp = null]
@init{
  PosSymbol lineNum = null;
}
:
    ^(id=IDENTIFIER { lineNum=getPosSymbol($IDENTIFIER); }
    exp1=base_case_statement_body
    { if($exp1.exp instanceof SuppositionDeductionExp)
        { $exp = $exp1.exp; ((SuppositionDeductionExp)$exp).getSupposition().setLineNum(lineNum); }
      else if($exp1.exp instanceof GoalExp)
        { $exp = $exp1.exp; ((GoalExp)$exp).setLineNum(lineNum); }
      else if($exp1.exp instanceof ProofDefinitionExp)
        { $exp = $exp1.exp; ((ProofDefinitionExp)$exp).setLineNum(lineNum); }
      else
        { $exp = $exp1.exp; ((JustifiedExp)$exp).setLineNum(lineNum); }
    } )
;
    
inductive_case_statement_head returns [Exp exp = null]
@init{
  PosSymbol lineNum = null;
}
:
    ^(id=IDENTIFIER { lineNum=getPosSymbol($IDENTIFIER); }
    exp1=inductive_case_statement_body
    { if($exp1.exp instanceof SuppositionDeductionExp)
        { $exp = $exp1.exp; ((SuppositionDeductionExp)$exp).getSupposition().setLineNum(lineNum); }
      else if($exp1.exp instanceof GoalExp)
        { $exp = $exp1.exp; ((GoalExp)$exp).setLineNum(lineNum); }
      else if($exp1.exp instanceof ProofDefinitionExp)
        { $exp = $exp1.exp; ((ProofDefinitionExp)$exp).setLineNum(lineNum); }
      else
        { $exp = $exp1.exp; ((JustifiedExp)$exp).setLineNum(lineNum); }
    } )
;

base_case_statement_body returns [Exp exp = null]
    :   ^(BASECASE exp1=proof_expression) { $exp = $exp1.exp; }
    ;
  
inductive_case_statement_body returns [Exp exp = null]
    :   ^(INDUCTIVECASE exp1=proof_expression) { $exp = $exp1.exp; }
    ;

math_item_reference returns [ProofDec pd = null]
@init{
    int kind = 1;
    PosSymbol name = null;
}
:
    ^(MATHITEMREF
    (   name1=theorem_name { kind = ProofDec.THEOREM; name = $name1.name; }
    |   name2=lemma_name { kind = ProofDec.LEMMA; name = $name2.name; }
    |   name3=property_name { kind = ProofDec.PROPERTY; name = $name3.name; }
    |   name4=corollary_name { kind = ProofDec.COROLLARY; name = $name4.name; }
    ) )
    { $pd = new ProofDec(kind, name, null, null, null); }
;

theorem_name returns [PosSymbol name = null]
:   ^(THEOREM name1=ident) { $name = $name1.ps; }
;

lemma_name returns [PosSymbol name = null]
:   ^(LEMMA name1=ident) { $name = $name1.ps; }
;

property_name returns [PosSymbol name = null]
:   ^(PROPERTY name1=ident) { $name = $name1.ps; }
;

corollary_name returns [PosSymbol name = null]
:   ^(COROLLARY name1=math_theorem_ident) { $name = $name1.ps; }
;

proof_expression_list returns [edu.clemson.cs.r2jt.collections.List<Exp> list = new edu.clemson.cs.r2jt.collections.List<Exp>("Proof Expression edu.clemson.cs.r2jt.collections.List")]
:
    ^(PROOFEXPRLIST
    ( (IDENTIFIER) => temp=headed_proof_expression { $list.add($temp.exp); }
    | temp1=proof_expression { $list.add($temp1.exp); }
    )* )
;

headed_proof_expression returns [Exp exp = null]
@init{
  PosSymbol lineNum = null;
}
:
    ^(id=IDENTIFIER { lineNum=getPosSymbol($IDENTIFIER); } exp1=proof_expression
    { if($exp1.exp instanceof SuppositionDeductionExp)
        { $exp = $exp1.exp; ((SuppositionDeductionExp)$exp).getSupposition().setLineNum(lineNum); }
      else if($exp1.exp instanceof GoalExp)
        { $exp = $exp1.exp; ((GoalExp)$exp).setLineNum(lineNum); }
      else if($exp1.exp instanceof ProofDefinitionExp)
        { $exp = $exp1.exp; ((ProofDefinitionExp)$exp).setLineNum(lineNum); }
      else
        { $exp = $exp1.exp; ((JustifiedExp)$exp).setLineNum(lineNum); }
    } )
;

proof_expression returns [Exp exp = null]
:
    ( exp1=goal_declaration { $exp = $exp1.ge; } |
    tempDec=standard_definition_declaration { $exp = new ProofDefinitionExp(getLocation((ColsAST)tempDec.getTree()), null, (DefinitionDec)$tempDec.dec); } |
    exp2=supposition_deduction_pair { $exp = $exp2.sde; } |
    exp3=justification_declaration { $exp = $exp3.je; } )
;

goal_declaration returns [GoalExp ge = null]
:
   ^(GOAL mathExp=math_expression)
   { $ge = new GoalExp(getLocation($GOAL), null, $mathExp.exp); }
;

supposition_deduction_pair returns [SuppositionDeductionExp sde = null]
@init{
   PosSymbol endLineNum = null;
}
:
   ^(SUPDEDUC supposition=supposition_declaration
     body=proof_expression_list
     (lineId=IDENTIFIER { endLineNum = getPosSymbol($IDENTIFIER); })?
     deduction=deduction_declaration { $deduction.deduc.setLineNum(endLineNum); }
    )
    { $sde = new SuppositionDeductionExp(getLocation($SUPDEDUC), $supposition.sup, $body.list, $deduction.deduc); }
;

supposition_declaration returns [SuppositionExp sup = null]
:
   ^(SUPPOSITION
      (
        (e=math_expression (decs=math_variable_declarations)? )
        |
        (decs=math_variable_declarations (e=math_expression)? )
      )
      { $sup = new SuppositionExp(getLocation($SUPPOSITION), null, $e.exp, $decs.decs); }
    )
;

math_variable_declarations returns [edu.clemson.cs.r2jt.collections.List<MathVarDec> decs = null]
:
   ^(DECLARATIONS (tempList=math_variable_declaration_group { $decs.addAll($tempList.vars); } )+ )
;

deduction_declaration returns [DeductionExp deduc = null]
:
   ^(DEDUCTION e=math_expression) { $deduc = new DeductionExp(getLocation($DEDUCTION), null, $e.exp); }
;

justification_declaration returns [JustifiedExp je = null]
:
   ^(SIMPLIFICATION exp=math_expression by=justification)
   { $je = new JustifiedExp(getLocation($SIMPLIFICATION), null, $exp.exp, $by.je); }
;

justification returns [JustificationExp je = null]
:
  ^(BY
  ( (hyp_desig hyp_desig) => je1=double_hyp_rule_justification { $je = $je1.je; }
  | (hyp_desig rules_set_1) => je2=single_hyp_rule_justification { $je = $je2.je; }
  | (hyp_desig rules_set_2) => je2=single_hyp_rule_justification { $je = $je2.je; }
  | (hyp_desig DEFINITION) => je2=single_hyp_rule_justification { $je = $je2.je; }
  | (hyp_desig) => he=hyp_desig { $je = new JustificationExp(getLocation($BY), $he.hde, null, null, false); }
  | je3=simple_justification { $je = $je3.je; }
  | (DEFINITION) => je4=def_justification  { $je = $je4.je; }
  | (INDEXED_DEFINITION) => je5=indexed_def_justification { $je = $je5.je; }))
;

double_hyp_rule_justification returns [JustificationExp je = null]
:
  hypDesig1=hyp_desig hypDesig2=hyp_desig (rule=rules_set_1)?
  { $je = new JustificationExp(getLocation((ColsAST)hypDesig1.getTree()), $hypDesig1.hde, $hypDesig2.hde, $rule.rule, false); }
;

single_hyp_rule_justification returns [JustificationExp je = null]
:
  hypDesig1=hyp_desig
  ( (RULE1) => rule1=rules_set_1 { $je = new JustificationExp(getLocation((ColsAST)rule1.getTree()),$hypDesig1.hde, null, $rule1.rule, false); }
    | (RULE2) => rule2=rules_set_2 { $je = new JustificationExp(getLocation((ColsAST)rule2.getTree()),$hypDesig1.hde, null, $rule2.rule, false); }
  | je1=def_justification { $je = $je1.je; $je.setHypDesig1($hypDesig1.hde); } )
;

def_justification returns [JustificationExp je = null]
@init{
  PosSymbol rule = null;
}
:
  ^(DEFINITION
  ((UNIQUE) => (id=UNIQUE { rule = new PosSymbol(getLocation($DEFINITION), Symbol.symbol("ThereExistsUnique")); } )
  | (rule1=fn_name) { rule = $rule1.name; }) )
  { $je = new JustificationExp(null, null, null, rule, true); }
;

indexed_def_justification returns [JustificationExp je = null]
:
  ^(INDEXED_DEFINITION (index=ident) (definitionName=fn_name)
    (sourceModuleName=ident)?) 
  { $je = new JustificationExp(null, null, null, $definitionName.name, $index.ps, 
    $sourceModuleName.ps, true); }
;

simple_justification returns [JustificationExp je = null]
@init{
  PosSymbol rule = null;
}
:
  ( (RULE3) => rule1=rules_set_3 { rule = $rule1.rule; } | rule2=rules_set_2 ) { rule = $rule2.rule; }
  { $je = new JustificationExp(null, null, null, rule, false); }
;

rules_set_1 returns [PosSymbol rule = null]
@init{
  PosSymbol temp = null;
}
:
  ^(RULE1
  (id1=MODUS_PONENS { temp = new PosSymbol(getLocation(root_0), Symbol.symbol("ModusPonens")); }            |
  id2=AND_RULE { temp = new PosSymbol(getLocation(root_0), Symbol.symbol("AndRule")); }               |
  id3=CONTRADICTION { temp = new PosSymbol(getLocation(root_0), Symbol.symbol("Contradiction")); }          |
  id4=EQUALITY { temp = new PosSymbol(getLocation(root_0), Symbol.symbol("Equality")); }                |
  id5=ALTERNATIVE_ELIMINATION { temp = new PosSymbol(getLocation(root_0), Symbol.symbol("AlternativeElimination")); } |
  id6=COMMON_CONCLUSION { temp = new PosSymbol(getLocation(root_0), Symbol.symbol("CommonConclusion")); } ) )
  { $rule = temp; }
;

rules_set_2 returns [PosSymbol rule = null]
@init{
  PosSymbol temp = null;
}
:
  ^(RULE2
  (id1=REDUCTIO_AD_ABSURDUM { temp = new PosSymbol(getLocation(root_0), Symbol.symbol("ReductioAdAbsurdum")); }       |
  id2=UNIVERSAL_GENERALIZATION { temp = new PosSymbol(getLocation(root_0), Symbol.symbol("UniversalGeneralization")); }   |
  id3=EXISTENTIAL_GENERALIZATION { temp = new PosSymbol(getLocation(root_0), Symbol.symbol("ExistentialGeneralization")); } |
  id4=OR_RULE { temp = new PosSymbol(getLocation(root_0), Symbol.symbol("OrRule")); }                     |
  id5=CONJUNCT_ELIMINATION { temp = new PosSymbol(getLocation(root_0), Symbol.symbol("ConjunctElimination")); }       |
  id6=QUANTIFIER_DISTRIBUTION { temp = new PosSymbol(getLocation(root_0), Symbol.symbol("QuantifierDistribution")); }     |
  id7=UNIVERSAL_INSTANTIATION { temp = new PosSymbol(getLocation(root_0), Symbol.symbol("UniversalInstantiation")); }     |
  id8=EXISTENTIAL_INSTANTIATION { temp = new PosSymbol(getLocation(root_0), Symbol.symbol("ExistentialInstantiation")); } ) )
  { $rule = temp; }
;

rules_set_3 returns [PosSymbol rule = null]
:
    ^(RULE3 id=EXCLUDED_MIDDLE)
    { $rule = new PosSymbol(getLocation(root_0), Symbol.symbol("ExcludedMiddle")); }
;

hyp_desig returns [HypDesigExp hde = null]
@init{
    boolean self = false;
    MathRefExp mre = null;
}
:
  ^(HYPDESIG ( SELF { self=true; } |
    mre1=lemma_call { mre = $mre1.mre; } |
    ps=corollary_name       |
    mre2=theorem_call  { mre = $mre2.mre; }       |
    mre3=supposition_call  { mre = $mre3.mre; }     |
    mre4=definition_call { mre = $mre4.mre; }       |
    mre5=reference_marker_call  { mre = $mre5.mre; }  ) )
    { if(self)
        { mre = new MathRefExp(getLocation(root_0), MathRefExp.SELF, null); }
      else if($ps.name != null)
        { mre = new MathRefExp(getLocation(root_0), MathRefExp.COROLLARY, $ps.name); }
      $hde = new HypDesigExp(getLocation(root_0), mre); }
;

lemma_call returns [MathRefExp mre = null]
:
    ^(LEMMA id=ident)
    { $mre = new MathRefExp(getLocation(root_0), 4, $id.ps); }
;

theorem_call returns [MathRefExp mre = null]
:
    ^(THEOREM id=ident)
    { $mre = new MathRefExp(getLocation(root_0), 2, $id.ps); }
;

supposition_call returns [MathRefExp mre = null]
:
    SUPPOSITION
    { $mre = new MathRefExp(getLocation(root_0), 6, null); }
;

definition_call returns [MathRefExp mre = null]
:
    ^(DEFINITION (LPAREN index=ident)? id=fn_name vars=definition_params
      (sourceModule=ident)?)
    { $mre = new MathRefExp(getLocation(root_0), 8, $id.name, $index.ps, $sourceModule.ps, $vars.vars); }
;

definition_params returns [edu.clemson.cs.r2jt.collections.List<VarExp> vars = null]
:
    (temp=qualified_ident { $vars.add($temp.exp); })*
;

reference_marker_call returns [MathRefExp mre = null]
@init{
    PosSymbol id = null;
}
:
    ^(REFCALL id2=ident)
    { id = getPosSymbol((ColsAST)id2.getTree()); $mre = new MathRefExp(getLocation((ColsAST)id2.getTree()), 7, id); }
;

fn_name returns [PosSymbol name = null]
:
  id1=infix_symbol { $name=getPosSymbol((ColsAST)id1.getTree()); }
  | id2=prefix_symbol { $name=getPosSymbol((ColsAST)id2.getTree()); }
  //| name=ident
;
