/*
 * This software is released under the new BSD 2006 license.
 * 
 * Note the new BSD license is equivalent to the MIT License, except for the
 * no-endorsement final clause.
 * 
 * Copyright (c) 2007, Clemson University
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer. 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution. 
 *   * Neither the name of the Clemson University nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This sofware has been developed by past and present members of the
 * Reusable Sofware Research Group (RSRG) in the School of Computing at
 * Clemson University.  Contributors to the initial version are:
 * 
 *     Steven Atkinson
 *     Greg Kulczycki
 *     Kunal Chopra
 *     John Hunt
 *     Heather Keown
 *     Ben Markle
 *     Kim Roche
 *     Murali Sitaraman
 */
/*
 * RParser.g
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

//class RParser extends Parser;
parser grammar RParser;

options {
    k = 1;    
    output = AST;                   
    ASTLabelType = 'ColsAST';
    tokenVocab=RLexer;
    //language=JavaScript;
    //backtrack=true;
    //exportVocab=RESOLVE;
}

tokens {
    AND_RULE;
    AFFECTS;
    ALTERNATIVE_ELIMINATION;
    ANGLE;
    AUX_OPERATION;
    ARRAYFUNCTION;
    BETWEEN_EXPR;
    CATEGORICAL_DEFINITION;
    CHOICES;
    COMMON_CONCLUSION;
    CONFIRM_TYPE;
    CONJUNCT_ELIMINATION;
    DECLARATIONS;
    DBL_ANGLE;
    ENHANCED_BY;
    EXCLUDED_MIDDLE;
    EXISTENTIAL_GENERALIZATION;
    EXISTENTIAL_INSTANTIATION;
    EXISTS_UNIQUE;
    EXPR;
    FORALL;
    FUNCTION;
    IDENT;
    IMPLICIT_DEF;
    INDEXED_DEFINITION;
    INDUCTIVE_DEF;
    ITERATE_EXIT;
    ITERATION;
    LOCAL_MATH_TYPE;
    MATH_TYPE;
    MODUS_PONENS;
    NESTED;
    OR_RULE;
    PARAMS;
    QUANTIFIER_DISTRIBUTION;
    RECURSIVE_OPERATION_PROCEDURE;
    RECURSIVE_PROCEDURE;
    REDUCTIO_AD_ABSURDUM;
    RELATED_BY;
    PROGDOT;
    SET_EXPR;
    STATEMENT;
    STATEMENT_SEQUENCE;
    TUPLE;
    TYPEX;
    UNARY_FREE_OP;
    UNARY_MINUS;
    UNIVERSAL_GENERALIZATION;
    UNIVERSAL_INSTANTIATION;
    VARDOT;
    PROOFBODY;
    MATHITEMREF;
    PROOFEXPR;
    SUPDEDUC;
    SIMPLIFICATION;
    RULE1;
    RULE2;
    RULE3;
    HYPDESIG;
    PROOFEXPRLIST;
    REFCALL;
    QUALNUM;
    LOCALVAREXP;
}

// comment this out if target is JavaScript
@header {
    package edu.clemson.cs.r2jt.parsing;
    
    import edu.clemson.cs.r2jt.data.*;
    import edu.clemson.cs.r2jt.errors.ErrorHandler;
    import org.antlr.*;
}

// ---------------------------------------------------------------
// Additional Java declarations
// ---------------------------------------------------------------

@members{
    /*
     * Variables to tell us what type of module we are
     * parsing.  Used for semantic predicates of rules or productions
     * which are only applicable to particular modules.
     */
    boolean theoryModule = false;
    boolean conceptModule = false;
    boolean performanceModule = false;
    boolean bodyModule = false;
    boolean enhancementModule = false;
    boolean facilityModule = false;

    /**
     * Reset the type of module we are parsing.
     */
    public void resetModuleType() {
        this.theoryModule = false;
        this.conceptModule = false;
        this.performanceModule = false;
        this.bodyModule = false;
        this.enhancementModule = false;
        this.facilityModule = false;
    }

    //private ErrorHandler err = ErrorHandler.getInstance();
    private ErrorHandler err;

    private boolean otherwise = false;

    private void checkOtherwiseItem(Tree ast) {
        if (otherwise) {
            String msg = "Cannot add an alternative after "
            + "an \"otherwise\" clause.";
            err.error(getPos(ast), msg);
        }
    }

    private void checkIndexedIdent(Tree ast) {
        if (!ast.getText().equals("i") &&
            !ast.getText().equals("ii"))
        {
            String msg = "Expecting i or ii, found " + ast.getText();
            err.error(getPos(ast), msg);
        }
    }    

    private void checkTimesIdent(Token ast) {
        if (!ast.getText().equals("x")) {
            String msg = "Expecting x or times, found " + ast.getText();
            err.error(getPos(ast), msg);
        }
    }    

    private void checkIteratedIdent(Tree ast) {
        if (!ast.getText().equals("Sum") &&
            !ast.getText().equals("Product") &&
            !ast.getText().equals("Concatenation") &&
            !ast.getText().equals("Intersection") &&
            !ast.getText().equals("Union"))
        {
            String msg = "Expecting iteration identifier "
                + "(Sum, Product, Concatenation, Intersection, Union),"
                + "but found " + ast.getText();
            err.error(getPos(ast), msg);
        }
    }    

    private boolean facInit = false;
    private boolean facFinal = false;

    private void checkFacInit(Tree ast) {
        if (facInit) {
            String msg = "Cannot redefine facility initialization.";
            err.error(getPos(ast), msg);
        } else {
            facInit = true;
        }
    } 

    private void checkFacFinal(Tree ast) {
        if (facFinal) {
            String msg = "Cannot redefine facility finalization.";
            err.error(getPos(ast), msg);
        } else {
            facFinal = true;
        }
    }
    public String getErrorMessage(RecognitionException e,
        String[] tokenNames)
        {
          List stack = (List)getRuleInvocationStack(e, this.getClass().getName());
          String msg = null;
          if ( e instanceof NoViableAltException ) {
            NoViableAltException nvae = (NoViableAltException)e;
            msg = " no viable alt; token="+e.token+
            " (decision="+nvae.decisionNumber+
            " state "+nvae.stateNumber+")"+
            " input "+nvae.input+")"+
            " decision=<<"+nvae.grammarDecisionDescription+">>";
          }
          if( e instanceof MismatchedTokenException ) {
            MismatchedTokenException mte = (MismatchedTokenException)e;
            String exp = null;
            if(mte.expecting == Token.EOF){
              exp = "EOF";
            }
            else{
              exp = tokenNames[mte.expecting];
            }
            msg = "expecting " + exp + ", found '" + mte.token.getText() + "'";
          }
          else {
            msg = super.getErrorMessage(e, tokenNames);
          }
          
          // For debugging changes to the grammar change this to return
          // both the stack (lists the rules visited) and the msg
          return msg;
          //return "Parser: "+stack + " " + msg;
    }
    public String getTokenErrorDisplay(Token t) {
        return t.toString();
    }

    /** Delegate the error handling to the error handler. */
    public void reportError(RecognitionException ex) {
        err.error(getPos(ex.token), getErrorMessage(ex, RParser.tokenNames));
        //System.out.println(getErrorMessage(ex, RParser.tokenNames));
        //err.error(ex);
    }

    /** Delegate the warning handling to the error handler. */
    public void reportWarning(String s) {
        err.warning(s);
    }
    
    private void matchModuleIdent(Tree id2, Tree id1) {
        if (!id1.getText().equals(id2.getText())) { 
            String msg = "End name " + id2.getText() +
            " does not match module name " + id1.getText();
            err.error(getPos(id2), msg);
        }  
    }

    /*private void matchModuleIdent(ColsAST id2, ColsAST id1) {
        if (!id1.getText().equals(id2.getText())) { 
            String msg = "End name " + id2.getText() +
            " does not match module name " + id1.getText();
            err.error(getPos(id2), msg);
        }  
    }*/

    private void matchOperationIdent(Tree id2, Tree id1) {
        if (!id1.getText().equals(id2.getText())) { 
            String msg = "End name " + id2.getText() +
            " does not match operation name " + id1.getText();
            err.error(getPos(id2), msg);
        }
    }
    
    private void matchMathItemIdent(Tree id2, Tree id1) {
        if (!id1.getText().equals(id2.getText())) { 
            String msg = "End name " + id2.getText() +
            " does not match proof name " + id1.getText();
            err.error(getPos(id2), msg);
        }
    }

    private Pos getPos(Tree ast) {
        //return new Pos(ast.getLine(), ast.getColumn());
        return new Pos(ast.getLine(), ast.getCharPositionInLine());
    }

    private Pos getPos(Token ast) {
       //return new Pos(ast.getLine(), ast.getColumn());
        return new Pos(ast.getLine(), ast.getCharPositionInLine());
    }
    
    /*public void recover(RecognitionException ex, BitSet bs) throws TokenStreamException {
      try {
          consume();
          consumeUntil(bs);
      }
      catch (TokenStreamException tsex) {
            throw tsex;
      } 
    }*/
    
	//XXX : This was not commented out during the Mar 2012 merge---we had no idea who changed it
    /*private boolean isDeductionToken(String testStr) {
      if(testStr.equals("deduction") || testStr.equals("Deduction")) {
        return true;
      }
      return false;
    }*/

}





// ===============================================================
// Production rules for Resolve modules
// ===============================================================

module [ErrorHandler err]
@init{
this.err = err;
}
    :   (   proof_module
        |   theory_module
        |   conceptual_module
        |   performance_module        
        |   realization_body_module
        |   enhancement_module
        |   facility_module
        )
        EOF!
    ;

// ---------------------------------------------------------------
//  Theory Module
// ---------------------------------------------------------------

theory_module
    :   THEORY^ id1=ident //{ theoryModule = true; }
        (module_formal_param_section)? SEMICOLON!
        (uses_list)?
        (math_item_sequence)?
        END! (id2=ident! { matchModuleIdent(id2.tree, id1.tree); })?
        SEMICOLON!
    ;


math_item_sequence
    :   (math_item)+
    ;

math_item
    :   definition_declaration
    |   math_assertion_declaration
    |   type_theorem_declaration
    ;

// ---------------------------------------------------------------
// Concept Module
// ---------------------------------------------------------------

conceptual_module
    :   MODULE_CONCEPT^ id1=ident { conceptModule = true; }
        (module_formal_param_section)? SEMICOLON!
        (uses_list)?
        (requires_clause)?
        (concept_item_sequence)?
        END! (id2=ident! { matchModuleIdent(id2.tree, id1.tree); })?
        SEMICOLON!
    ;

concept_item_sequence
    :   (concept_item)+
    ;

concept_item
    :
    confirm_math_type_declaration
    |   concept_state_variable_declaration
    |   constraint_clause
    |   module_concept_init_declaration
    |   module_concept_final_declaration
    |   type_declaration
    |   operation_declaration
    |   definition_declaration
    |   defines_declaration
    ;

// ---------------------------------------------------------------
// Performance Module
// ---------------------------------------------------------------

performance_module
    :  
        MODULE_PROFILE^ id1=ident
        { performanceModule = true; }
        (module_formal_param_section)? SHORT_FOR!
        ident FOR! ident (FOR! ident WITH_PROFILE! ident)? SEMICOLON!
        (uses_list)?
        (requires_clause)?
        (performance_item_sequence)?
        END! (id2=ident! { matchModuleIdent(id2.tree, id1.tree); })?
        SEMICOLON!
    ;

performance_item_sequence
    :   (performance_item)+
    ;

performance_item
    :
    confirm_math_type_declaration
    |   concept_state_variable_declaration
    |   constraint_clause
    |   performance_module_init_declaration
    |   performance_module_final_declaration
    |   performance_type_declaration
    |   performance_operation_declaration
    |   definition_declaration
    |   defines_declaration
    ;

// ---------------------------------------------------------------
// Enhancement Module
// ---------------------------------------------------------------

enhancement_module
    :   MODULE_ENHANCEMENT^ id1=ident { enhancementModule = true; }
        (module_formal_param_section)?
        FOR! (CONCEPT!)? ident SEMICOLON!
        (uses_list)?
        (requires_clause)?
        (enhancement_item_sequence)?
        END! (id2=ident! { matchModuleIdent(id2.tree, id1.tree); })?
        SEMICOLON!
    ;

enhancement_item_sequence
    :   (enhancement_item)+
    ;

enhancement_item
    :   concept_state_variable_declaration
    |   type_declaration
    |   operation_declaration
    |   definition_declaration
    |   defines_declaration
    ;

// ---------------------------------------------------------------
// Body Module
// ---------------------------------------------------------------

realization_body_module
    :   MODULE_REALIZATION^ id1=ident { bodyModule = true; }
        (WITH_PROFILE ident)? (module_formal_param_section)? FOR!
        (   (ident OF)=> body_enhancement_section
        |   body_concept_section
        ) SEMICOLON!
        (uses_list)?
        (requires_clause)?
        (body_item_sequence)?
        END! (id2=ident! { matchModuleIdent(id2.tree, id1.tree); })?
        SEMICOLON!
    ;

body_concept_section
    :   ident
        (ENHANCED BY ident)* -> ^(CONCEPT ident (ident)*)
    ;

body_enhancement_section
    :   ident
        OF ident
        (added_enhancement_section)* -> ^(ENHANCEMENT ident ident (added_enhancement_section)*)
    ;

added_enhancement_section
    :   (ENHANCED BY! ident
        module_argument_section) =>
        ENHANCED^ BY! ident
        module_argument_section
        REALIZED BY! ident
        (WITH_PROFILE ident)? module_argument_section? /*->
        ^(ENHANCED_BY
        ident
        module_argument_section
        ident
        module_argument_section?)*/
    |   ENHANCED^ BY! ident
        REALIZED BY! ident
        (WITH_PROFILE ident)? module_argument_section? /*->
        ^(ENHANCED_BY ident
        ident
        module_argument_section?)*/
    ;

body_item_sequence
    :   (body_item)+
    ;

body_item
    :   state_variable_declaration
    |   correspondence_clause
    |   convention_clause
    |   module_body_init_declaration
    |   module_body_final_declaration
    |   type_representation_declaration
    |   aux_operation_declaration
    |   (OPERATION ident
        operation_formal_param_section
        (COLON program_type_expression)? SEMICOLON
        affects_clause*
        requires_clause?
        ensures_clause?
        RECURSIVE) => operation_recursive_procedure_declaration
    |   operation_procedure_declaration
//    |   operation_recursive_procedure_declaration
    |   procedure_declaration
    |   recursive_procedure_declaration
    |   definition_declaration    
    |   facility_declaration
    ;

// ---------------------------------------------------------------
// Facility Module
// ---------------------------------------------------------------

facility_module
    :   FACILITY^ id1=ident //{ facilityModule = true; }
        (   short_facility_section (uses_list)?
        |   SEMICOLON! (uses_list)?
            (facility_item_sequence)?
            END! (id2=ident! { matchModuleIdent(id2.tree, id1.tree); })?
            SEMICOLON!
        )
    ;

short_facility_section
    :   IS! ident
        module_argument_section?
        facility_enhancement*
        REALIZED! BY! ident
        module_argument_section?
        facility_body_enhancement*
        SEMICOLON!
    ;

facility_item_sequence
    :   (facility_item)+
    ;

facility_item
    :   state_variable_declaration
    |   module_facility_init_declaration
    |   module_facility_final_declaration
    |   facility_type_declaration
    |   (OPERATION ident
        operation_formal_param_section
        (COLON program_type_expression)? SEMICOLON
        affects_clause*
        requires_clause?
        ensures_clause?
        RECURSIVE) => operation_recursive_procedure_declaration
    |   operation_procedure_declaration
    //|   operation_recursive_procedure_declaration
    |   definition_declaration 
    |   facility_declaration
    ;

// ===============================================================
// Rules for module parameters and uses clause
// ===============================================================

// ---------------------------------------------------------------
// Module parameters
// ---------------------------------------------------------------

module_formal_param_section
    :   LPAREN module_parameter
        (SEMICOLON module_parameter)* RPAREN
        -> ^(PARAMS module_parameter (module_parameter)*)
    ;

module_parameter
    :   definition_parameter
    |   { conceptModule || enhancementModule || bodyModule }?
        constant_parameter
    |   { conceptModule || enhancementModule }? concept_type_parameter
    |   { bodyModule }? operation_parameter
    |   { bodyModule }? concept_realization_parameter
    ;

definition_parameter
    :   DEFINITION^ definition_signature
    ;

constant_parameter
    :   EVALUATES^ variable_declaration_group
    ;

concept_type_parameter
    :   TYPE^ ident
    ;

operation_parameter
    :   operation_declaration
    ;

concept_realization_parameter
    :   REALIZATION^ ident
        FOR! (CONCEPT!)? ident
    ;

// ---------------------------------------------------------------
//  Uses Declaration
// ---------------------------------------------------------------

uses_list
    :   (uses_clause)+
    ;

uses_clause
    :   USES^ ident (COMMA! ident)* SEMICOLON!
    ;

// ===============================================================
// Rules for module level declarations and items
// ===============================================================

// ---------------------------------------------------------------
// Math Type Declarations
// ---------------------------------------------------------------

/*
formal_type_declaration
    :   LOCAL MATH TYPE ident SEMICOLON ->
    ^(LOCAL_MATH_TYPE ident)
    ;
*/

/*
subtype_declaration
    :   MATH_SUBTYPE^ ((ident DOT) => qualified_type | ident) COLON!
        ((ident DOT) => qualified_type | ident) SEMICOLON!
    ;
*/  

/*    
qualified_type
    :   ident DOT^ ident
    ;

math_type_declaration
    :   MATH TYPE ident
        COLON math_type_expression SEMICOLON ->
        ^(MATH_TYPE ident
        math_type_expression)
    ;
*/

//primitive_type_expression
confirm_math_type_declaration
    :   CONFIRM MATH TYPE math_variable_declaration SEMICOLON
        -> ^(CONFIRM_TYPE math_variable_declaration)
    ;

// ---------------------------------------------------------------
// Math Assertions
// ---------------------------------------------------------------

math_assertion_declaration
    :   (   AXIOM^
        |   THEOREM^
        |   PROPERTY^
        |   LEMMA^
        |   COROLLARY^
        |   (COMMUTATIVITY) THEOREM!
        )
        (math_theorem_ident)? COLON!
        math_expression SEMICOLON!
    ;

constraint_clause
    :   CONSTRAINT^ math_expression SEMICOLON!
    ;

correspondence_clause
    :   CORR^ math_expression SEMICOLON!
    ;

convention_clause
    :   CONVENTION^ math_expression SEMICOLON!
    ;
    
// ---------------------------------------------------------------
// Type Theorem Declarations
// ---------------------------------------------------------------

type_theorem_declaration
    :   TYPE^ THEOREM! (math_theorem_ident)? COLON
        (FOR ALL! math_variable_declaration_group COMMA!)+
        implies_expression COLON! math_type_expression SEMICOLON!
    ;
    


// ---------------------------------------------------------------
// State Variable Declarations
// ---------------------------------------------------------------

concept_state_variable_declaration
    :   VAR^ math_variable_declaration_group SEMICOLON!
    ;

state_variable_declaration
    :   VAR^ variable_declaration_group SEMICOLON!
    ;
    
state_aux_variable_declaration
    :   AUX_VAR^ variable_declaration_group SEMICOLON!
    ;

// ---------------------------------------------------------------
//  Facility Declarations
// ---------------------------------------------------------------

facility_declaration
    :   FACILITY^ ident
        IS! ident
        module_argument_section?
        facility_enhancement*
        REALIZED BY! ident
        (WITH_PROFILE ident)? module_argument_section?
        facility_body_enhancement*
        SEMICOLON!
    ;

facility_enhancement
    :   ENHANCED^ BY! ident
        module_argument_section? //->
        //^(ENHANCED_BY ident
        //module_argument_section?)
    ;

facility_body_enhancement
    :   (ENHANCED BY! ident
        module_argument_section) =>
        ENHANCED^ BY! ident
        module_argument_section
        REALIZED BY! ident
        (WITH_PROFILE ident)? module_argument_section? /*->
        ^(ENHANCED_BY ident
        module_argument_section
        REALIZED ident
        module_argument_section?)*/
    |   ENHANCED^ BY! ident
        REALIZED BY! ident
        (WITH_PROFILE ident)? module_argument_section? /*->
        ^(ENHANCED_BY ident
        REALIZED ident
        module_argument_section?)*/
    ;

module_argument_section
    :   LPAREN module_argument (COMMA module_argument)* RPAREN
        -> ^(PARAMS module_argument+)
    ;

module_argument
    :   (qualified_ident)=> qualified_ident
    |   program_expression
    ;

// ===============================================================
// Definition Declarations
// ===============================================================

defines_declaration
    :   DEFINES^ definition_signature SEMICOLON!
    ;

definition_declaration
    :   implicit_definition_declaration
    |   inductive_definition_declaration
    |   standard_definition_declaration
    |   categorical_definition_declaration
    ;

implicit_definition_declaration
    :   IMPLICIT DEFINITION definition_signature SEMICOLON?
        IS math_expression SEMICOLON ->
        ^(IMPLICIT_DEF definition_signature
        math_expression)
    ;

inductive_definition_declaration
    :   INDUCTIVE DEFINITION definition_signature SEMICOLON?
        IS indexed_expression SEMICOLON indexed_expression SEMICOLON ->
        ^(INDUCTIVE_DEF definition_signature
        indexed_expression indexed_expression)
    ;

standard_definition_declaration
    :   DEFINITION^ definition_signature
        (EQL! math_expression)? SEMICOLON!
    ;
    
categorical_definition_declaration
    :   CATEGORICAL DEFINITION INTRODUCES categorical_definition_construct
        RELATED BY math_expression SEMICOLON
        -> ^(CATEGORICAL_DEFINITION categorical_definition_construct ^(RELATED_BY math_expression))
    ;

definition_signature
    :   (   infix_definition_construct
        |   outfix_definition_construct
        |   standard_definition_construct
        )
        COLON! math_type_expression
    ;

infix_definition_construct
    :   singleton_definition_parameter
        (ident | infix_symbol)
        singleton_definition_parameter
    ;

outfix_definition_construct
    :   BAR singleton_definition_parameter BAR!
    |   DBL_BAR singleton_definition_parameter DBL_BAR!
    |   LT singleton_definition_parameter GT!
    |   LL singleton_definition_parameter GG!
    ;

standard_definition_construct
    :   (ident | prefix_symbol | quant_symbol | NUMERIC_LITERAL)
        (definition_formal_param_section)?
    ;

categorical_definition_construct
    :   definition_signature (COMMA definition_signature)*
        -> ^(DEFINITION definition_signature) ^(DEFINITION definition_signature)*
    ;

// FIX: IDENT getText() should be i or ii. Check for this and report
// an error if it is not the case.
indexed_expression
    :   LPAREN! id=ident! RPAREN! math_expression
        { checkIndexedIdent(id.tree); }
    ;

singleton_definition_parameter
    :   LPAREN math_variable_declaration RPAREN
        -> ^(PARAMS math_variable_declaration)
    ;

definition_formal_param_section
    :   LPAREN math_variable_declaration_group
        (COMMA math_variable_declaration_group)* RPAREN
        -> ^(PARAMS math_variable_declaration_group+)
    ;

infix_symbol
    : EQL | NOT_EQL | LT | GT | LT_EQL | GT_EQL | PLUS | MINUS | MULTIPLY | DIVIDE
    | EXP | MOD | REM | DIV | IMPLIES | IFF | AND | OR | XOR
    | ANDTHEN | ORELSE | COMPLEMENT | IN | NOT_IN | RANGE
    | UNION | INTERSECT | WITHOUT | SUBSET | PROP_SUBSET
    | NOT_SUBSET | NOT_PROP_SUBSET | CAT | SUBSTR | NOT_SUBSTR
    ;

prefix_symbol
    : PLUS | MINUS | NOT | ABS | COMPLEMENT
    ;

quant_symbol
    : BIG_UNION | BIG_INTERSECT | BIG_SUM | BIG_PRODUCT | BIG_CONCAT
    ;

// ===============================================================
// Operation Declarations
// ===============================================================

operation_procedure_declaration
    :   OPERATION^ id1=ident
        (operation_formal_param_section)
        (COLON! program_type_expression)? SEMICOLON!
        (affects_clause)*
        (requires_clause)?
        (ensures_clause)?
        PROCEDURE!
        (decreasing_clause)?
        (facility_declaration)*
        (variable_declaration)*
        (aux_variable_declaration)*
        statement_sequence
        END! (id2=ident! { matchOperationIdent(id2.tree, id1.tree); })?
        SEMICOLON!
    ;
    
operation_recursive_procedure_declaration
    :   OPERATION id1=ident
        operation_formal_param_section
        (COLON program_type_expression)? SEMICOLON
        affects_clause*
        requires_clause?
        ensures_clause?
        RECURSIVE PROCEDURE
        decreasing_clause
        facility_declaration*
        variable_declaration*
        aux_variable_declaration*
        statement_sequence
        END (id2=ident { matchOperationIdent(id2.tree, id1.tree); })?
        SEMICOLON ->
        ^(RECURSIVE_OPERATION_PROCEDURE ident
        operation_formal_param_section
        (program_type_expression)?
        affects_clause*
        requires_clause?
        ensures_clause?
        decreasing_clause
        facility_declaration*
        variable_declaration*
        aux_variable_declaration*
        statement_sequence)
    ;

operation_declaration
    :   OPERATION^ ident
        (operation_formal_param_section)
        (COLON! program_type_expression)? SEMICOLON!
        (affects_clause)*
        (requires_clause)?
        (ensures_clause)?
    ;
    
performance_operation_declaration
    :   OPERATION^ ident
        (operation_formal_param_section)
        (COLON! program_type_expression)? SEMICOLON!
        (ensures_clause)*
        (duration_clause)?
        (mainp_disp_clause)?
    ;

aux_operation_declaration
    :   AUXILIARY OPERATION ident
        operation_formal_param_section
        (COLON program_type_expression)? SEMICOLON
        affects_clause*
        requires_clause?
        ensures_clause? ->
        ^(AUX_OPERATION ident
        operation_formal_param_section
        program_type_expression?
        affects_clause*
        requires_clause?
        ensures_clause?)
    ;

procedure_declaration
    :   PROCEDURE^ id1=ident
        (operation_formal_param_section)
        (COLON! program_type_expression)? SEMICOLON!
        (affects_clause)*
        (decreasing_clause)?
        (facility_declaration)*
        (variable_declaration)*
        (aux_variable_declaration)*
        statement_sequence
        END! (id2=ident! { matchOperationIdent(id2.tree, id1.tree); })?
        SEMICOLON!
    ;
    
recursive_procedure_declaration
    :   RECURSIVE PROCEDURE id1=ident
        operation_formal_param_section
        (COLON program_type_expression)? SEMICOLON
        affects_clause*
        decreasing_clause?
        facility_declaration*
        variable_declaration*
        aux_variable_declaration*       
        statement_sequence
        END (id2=ident)? //{ matchOperationIdent(id2.tree, id1.tree); })?
        SEMICOLON ->
        ^(RECURSIVE_PROCEDURE ident
        operation_formal_param_section
        program_type_expression?
        affects_clause*
        decreasing_clause?
        facility_declaration*
        variable_declaration*
        aux_variable_declaration*       
        statement_sequence)
    ;

operation_formal_param_section
    :   LPAREN
        (   operation_formal_param_group
            (SEMICOLON operation_formal_param_group)*
        )? RPAREN
        -> ^(PARAMS operation_formal_param_group*)
    ;

operation_formal_param_group
    :   abstract_mode variable_declaration_group
        -> ^(VAR abstract_mode variable_declaration_group)
    ;

variable_declaration
    :   VAR^ variable_declaration_group SEMICOLON!
    ;
    
aux_variable_declaration
    :   AUX_VAR^ aux_variable_declaration_group SEMICOLON!
    ;
    

affects_clause
    :   abstract_mode qualified_ident (COMMA qualified_ident)* SEMICOLON
        -> ^(AFFECTS abstract_mode qualified_ident (COMMA qualified_ident)*)
    ;

abstract_mode
    :   ALTERS
    |   CLEARS
    |   EVALUATES
    |   PRESERVES
    |   REPLACES
    |   RESTORES
    |   UPDATES
    |   REASSIGNS    
    ;

requires_clause
    :   REQUIRES^ math_expression SEMICOLON!
    ;

ensures_clause
    :   ENSURES^ math_expression SEMICOLON!
    ;

duration_clause
    :   DURATION^ math_expression SEMICOLON!
    ;

mainp_disp_clause
    :   MAINP_DISP^ math_expression SEMICOLON!
    ;
    
// ===============================================================
// Type Declarations
// ===============================================================

type_declaration
    :   ((TYPE FAMILY) ident
        (SUBSET | (IS MODELED BY))
        math_type_expression SEMICOLON
        EXEMPLAR ident SEMICOLON
        constraint_clause?
        type_concept_init_declaration?
        type_concept_final_declaration?) ->
        ^(TYPE_FAMILY ident math_type_expression ident
        constraint_clause?
        type_concept_init_declaration?
        type_concept_final_declaration?)
    |   (FAMILY ident
        (SUBSET | (IS MODELED BY))
        math_type_expression SEMICOLON
        EXEMPLAR ident SEMICOLON
        constraint_clause?
        type_concept_init_declaration?
        type_concept_final_declaration?) ->
        ^(TYPE_FAMILY ident math_type_expression ident
        constraint_clause?
        type_concept_init_declaration?
        type_concept_final_declaration?)
    |   (TYPE_FAMILY^ ident
        (SUBSET! | (IS! MODELED! BY!))
        math_type_expression SEMICOLON!
        EXEMPLAR! ident SEMICOLON!
        constraint_clause?
        type_concept_init_declaration?
        type_concept_final_declaration?)
    ;

performance_type_declaration
    :   ((TYPE FAMILY) ident
        (SUBSET | (IS MODELED BY))
        math_type_expression SEMICOLON
        constraint_clause?
        performance_type_init_declaration?
        performance_type_final_declaration?) ->
        ^(TYPE_FAMILY ident math_type_expression
        constraint_clause?
        performance_type_init_declaration?
        performance_type_final_declaration?)
    |   (FAMILY ident
        (SUBSET | (IS MODELED BY))
        math_type_expression SEMICOLON
        constraint_clause?
        performance_type_init_declaration?
        performance_type_final_declaration?) ->
        ^(TYPE_FAMILY ident math_type_expression
        constraint_clause?
        performance_type_init_declaration?
        performance_type_final_declaration?)
    |   (TYPE_FAMILY^ ident
        (SUBSET! | IS! MODELED! BY!)
        math_type_expression SEMICOLON!
        constraint_clause?
        performance_type_init_declaration?
        performance_type_final_declaration?)
    ;

type_representation_declaration
    :   TYPE^ ident (EQL! | (IS! REPRESENTED! BY!))
        structural_program_type_expression SEMICOLON!
        (convention_clause)?
        (correspondence_clause)?
        (type_body_init_declaration)?
        (type_body_final_declaration)?
    ;

facility_type_declaration
    :   TYPE^ ident (EQL! | (IS! REPRESENTED! BY!))
        structural_program_type_expression SEMICOLON!
        (convention_clause)?
        (type_facility_init_declaration)? 
        (type_facility_final_declaration)?
    ;


// ---------------------------------------------------------------
// Initialization and finalization rules
// ---------------------------------------------------------------

// Module level init and final -----------------------------------

module_concept_init_declaration
    :   fac=FAC_INIT^ concept_init_final_section
        { checkFacInit(root_0); }
    ;

module_concept_final_declaration
    :   fac=FAC_FINAL^ concept_init_final_section
        { checkFacFinal(root_0); }
    ;

performance_module_init_declaration
    :   PERF_INIT^ performance_init_section
    ;

performance_module_final_declaration
    :   PERF_FINAL^ performance_final_section
    ;
 
module_body_init_declaration
    :   fac=FAC_INIT^ body_init_final_section
        { checkFacInit(root_0); }
    ;

module_body_final_declaration
    :   fac=FAC_FINAL^ body_init_final_section
        { checkFacFinal(root_0); }
    ;

module_facility_init_declaration
    :   fac=FAC_INIT^ facility_init_final_section
        { checkFacInit(root_0); }
    ;

module_facility_final_declaration
    :   fac=FAC_FINAL^ facility_init_final_section
        { checkFacFinal(root_0); }
    ;

// Type level init and final -----------------------------------

type_concept_init_declaration
    :   INITIALIZATION^ concept_init_final_section
    ;

type_concept_final_declaration
    :   FINALIZATION^ concept_init_final_section
    ;

performance_type_init_declaration
    :   INITIALIZATION ^ performance_init_section
    ;

performance_type_final_declaration
    :   FINALIZATION ^ performance_final_section
    ;
    
type_body_init_declaration
    :   INITIALIZATION^ body_init_final_section
    ;

type_body_final_declaration
    :   FINALIZATION^ body_init_final_section
    ;

type_facility_init_declaration
    :   INITIALIZATION^ facility_init_final_section
    ;

type_facility_final_declaration
    :   FINALIZATION^ facility_init_final_section
    ;

// Init and final sections ---------------------------------------

concept_init_final_section
    :   (affects_clause)*
        (requires_clause)?
        (ensures_clause)?
    ;

performance_init_section
    :   (duration_clause)?
        (mainp_disp_clause)?
    ;

performance_final_section
    :   (duration_clause)?
        (mainp_disp_clause)?
    ;

body_init_final_section
    :   (affects_clause)*
        (facility_declaration)*
        (variable_declaration)*
        (aux_variable_declaration)*
        statement_sequence
        END! SEMICOLON!
    ;

facility_init_final_section
    :   (affects_clause)*
        (requires_clause)?
        (ensures_clause)?
        (facility_declaration)*
        (variable_declaration)*
        (aux_variable_declaration)*
        statement_sequence
        END! SEMICOLON!
    ;

// ===============================================================
// Statements
// ===============================================================

statement
    :   (   if_statement -> ^(STATEMENT if_statement)
        |   selection_statement -> ^(STATEMENT selection_statement)
        |   while_loop_statement -> ^(STATEMENT while_loop_statement)
        |   iterate_loop_statement -> ^(STATEMENT iterate_loop_statement)
        |   (variable_expression SWAP_OP)=> swap_statement -> ^(STATEMENT swap_statement)
        |   (variable_expression ASSIGN_OP)=> function_assignment -> ^(STATEMENT function_assignment)
        |   (qualified_ident LPAREN)=> procedure_operation_call -> ^(STATEMENT procedure_operation_call)
        |   remember_statement -> ^(STATEMENT remember_statement)
        |   forget_statement -> ^(STATEMENT forget_statement)
        |   confirm_statement -> ^(STATEMENT confirm_statement)
        |   assume_statement -> ^(STATEMENT assume_statement)
        |   aux_code_statement -> ^(STATEMENT aux_code_statement)
        )
    ;

in_aux_statement
    :   (   if_statement -> ^(STATEMENT if_statement)
        |   selection_statement -> ^(STATEMENT selection_statement)
        |   while_loop_statement -> ^(STATEMENT while_loop_statement)
        |   iterate_loop_statement -> ^(STATEMENT iterate_loop_statement)
        |   (variable_expression SWAP_OP)=> swap_statement -> ^(STATEMENT swap_statement)
        |   (variable_expression ASSIGN_OP)=> function_assignment -> ^(STATEMENT function_assignment)
        |   (qualified_ident LPAREN)=> procedure_operation_call -> ^(STATEMENT procedure_operation_call)
        |   remember_statement -> ^(STATEMENT remember_statement)
        |   forget_statement -> ^(STATEMENT forget_statement)
        |   confirm_statement -> ^(STATEMENT confirm_statement)
        |   assume_statement -> ^(STATEMENT assume_statement)
        |   aux_code_statement -> ^(STATEMENT aux_code_statement)
        )
    ;


statement_sequence
    :   (statement SEMICOLON)* -> ^(STATEMENT_SEQUENCE statement*)
    ;
    
in_aux_statement_sequence
    :   (in_aux_statement SEMICOLON)* -> ^(STATEMENT_SEQUENCE in_aux_statement*)
    ;

// Function assignment -------------------------------------------

function_assignment
    :   variable_expression ASSIGN_OP^ program_expression
    ;

// Forget and remember -------------------------------------------

forget_statement
    :   FORGET
    ;

remember_statement
    :   REMEMBER
    ;

// If statement --------------------------------------------------

if_statement
    :   IF^ condition
        THEN! statement_sequence
        (elsif_item)*
        (else_part)? END!
    ;

elsif_item
    :   ELSIF^ condition
        THEN! statement_sequence
    ;

else_part
    :   ELSE^ statement_sequence
    ;

condition
    :   program_expression
    ;

// Aux Code statement --------------------------------------------
aux_code_statement
    :   AUX_CODE^
        in_aux_statement_sequence END!
    ;


// Iterate statement ---------------------------------------------

iterate_loop_statement
    :   ITERATE^
        (changing_clause)?    
        maintaining_clause
        (decreasing_clause)?
        iterate_item_sequence REPEAT!
    ;

iterate_item_sequence
    :   (iterate_item SEMICOLON!)+
    ;

iterate_item
    :   statement
    |   iterate_exit_statement
    ;

iterate_exit_statement
    :   WHEN^ { $WHEN.setType(ITERATE_EXIT); } condition
        DO! statement_sequence EXIT!
    ;     

// Procedure call ------------------------------------------------

procedure_operation_call
    :   qualified_ident operation_argument_section
        -> ^(FUNCTION qualified_ident operation_argument_section)
    ;

operation_argument_section
    :   LPAREN
        (   program_expression
            (COMMA program_expression)*
        )? RPAREN -> ^(PARAMS program_expression*)
    ;

// Selection statement -------------------------------------------

selection_statement
    :   CASE^ program_expression
        OF! selection_alternative_sequence
        (default_alternative)? END!
    ;

selection_alternative_sequence
    :   (selection_alternative)+
    ;

selection_alternative
    :   WHEN^ choices
        DO! statement_sequence
    ;

default_alternative
    :   DEFAULT^ statement_sequence
    ;

choice
    :   program_expression
    ;

choices
    :   choice
        (DBL_BAR choice)* -> ^(CHOICES choice+)
    ;

// Swap statement ------------------------------------------------

swap_statement
    :   variable_expression SWAP_OP^ variable_expression
    ;
    
// Confirm statement ------------------------------------------------

confirm_statement
    :   CONFIRM^ math_expression
    ;
    
// Confirm statement ------------------------------------------------

assume_statement
    :   ASSUME^ math_expression
    ;

// While loop ----------------------------------------------------

while_loop_statement
    :   WHILE^ condition
        changing_clause?    
        maintaining_clause?
        decreasing_clause?
        elapsed_time_clause?
        DO! statement_sequence END!
    ;

maintaining_clause
    :   MAINTAINING^ math_expression SEMICOLON!
    ;

decreasing_clause
    :   DECREASING^ adding_expression SEMICOLON!
    ;

elapsed_time_clause
    :   ELAPSED_TIME^ math_expression SEMICOLON!
    ;

changing_clause
    :   CHANGING^ variable_expression
        (COMMA! variable_expression)* SEMICOLON!
    ;

// ===============================================================
// Program Type Expression Grammar
// ===============================================================

program_type_expression
    :   (   (qualified_ident -> ^(TYPEX qualified_ident))
        |   (ARRAY array_range
            OF program_type_expression
            -> ^(ARRAY array_range program_type_expression))
        )
    ;

structural_program_type_expression
    :   RECORD^
        (record_variable_declaration_group SEMICOLON!)+
        END!
    |   program_type_expression
    ;

record_variable_declaration_group
    :   variable_id_list COLON program_type_expression
        -> ^(VAR variable_id_list program_type_expression)
    ;
    
record_aux_variable_declaration_group
    :   AUX_VAR^ variable_id_list program_type_expression
    ;

array_range
    :   program_expression RANGE^ program_expression
    ;

variable_declaration_group
    :   variable_id_list COLON! program_type_expression
    ;
    
aux_variable_declaration_group
    :   variable_id_list COLON! program_type_expression
    ;

variable_id_list
    :   ident (COMMA! ident)*
    ;

// ===============================================================
// Math Type Expression Grammar
// ===============================================================


math_type_expression
    :   //function_type_expression? -> ^(TYPEX function_type_expression?)
    //|   BOOLEAN
    infix_expression
    ;

/*    
function_type_expression
    :   structural_math_type_expression
        (FUNCARROW^ structural_math_type_expression)*
    ;
    
structural_math_type_expression
    :   CARTPROD^
        (cartprod_variable_declaration_group SEMICOLON!)+
        END!
    |   product_type_expression
    ;

product_type_expression
    :   (primitive_type_expression (IDENTIFIER|TIMES)) =>
        (   primitive_type_expression
            ((id=IDENTIFIER { checkTimesIdent(id); } | id=TIMES)
            primitive_type_expression
            )*
        ) -> ^(TIMES[$id] primitive_type_expression primitive_type_expression*)
    |   primitive_type_expression
    ;

primitive_type_expression
    :   (BOOLEAN) => BOOLEAN
    |   (POWERSET) => powerset_expression
    |   nested_type_expression
    |   (qualified_ident type_expression_argument_list)=> qualified_ident type_expression_argument_list
        -> ^(FUNCTION qualified_ident type_expression_argument_list)
    |   qualified_ident
    |   (qualified_ident -> ^(qualified_ident))
        (   type_expression_argument_list
        -> ^(FUNCTION qualified_ident type_expression_argument_list))?
    ;
    
powerset_expression
    :   POWERSET^ LPAREN! math_type_expression RPAREN!
    ;

nested_type_expression
    :   LPAREN! type_expression RPAREN!
    //| math_type_expression // Introduces Mutual left recursion errors
    ;
    
type_expression
    //:   (math_type_expression) => implicit_type_parameter_group
    :   math_type_expression
    ;
*/
type_expression_argument_list
    :   LPAREN math_type_expression
        (COMMA math_type_expression)* RPAREN -> ^(PARAMS math_type_expression math_type_expression*)
    ;

cartprod_variable_declaration_group
    :   math_variable_declaration_group -> ^(VAR math_variable_declaration_group)
    ;

structural_math_variable_declaration_group
    :   variable_id_list COLON math_type_expression
    ;

math_variable_declaration_group
    :   variable_id_list COLON math_type_expression
    ; 

math_variable_declaration
    :   ident COLON math_type_expression
    ;
    
implicit_type_parameter_group
    :   variable_id_list COLON math_expression
    ;

// ===============================================================
// Resolve Expression/Math_Expression Grammar
// ===============================================================

math_expression
    :   (   ((ident ident COLON) => iterated_construct) -> ^(EXPR iterated_construct)
        |   quantified_expression -> ^(EXPR quantified_expression)
        )
    ;

//  expression
//      :   quantified_expression
//      ;

quantified_expression
    :   implies_expression
    |   FOR ALL math_variable_declaration_group
        where_clause? COMMA quantified_expression ->
        ^(FORALL math_variable_declaration_group
        where_clause?  quantified_expression)
    |   (THERE EXISTS UNIQUE)=> THERE EXISTS UNIQUE math_variable_declaration_group
        where_clause? ((SUCH THAT) | COMMA) quantified_expression ->
        ^(EXISTS_UNIQUE math_variable_declaration_group
        where_clause? quantified_expression)
    |   THERE! EXISTS^ math_variable_declaration_group
        where_clause? ((SUCH! THAT!) | COMMA!) quantified_expression
    ;

implies_expression
    :   (   logical_expression 
            (   IMPLIES^ logical_expression
            |   IFF^ logical_expression 
            //|
            )?
        |   IF^ logical_expression
            THEN! logical_expression
            //(   // CONFLICT: The dangling-else problem.
                // ANTLR generates proper code by matching
                // as soon as possible. Turn off warning.
                //options {
                    //warnWhenFollowAmbig = false;
                //}
                //@rulecatch {}
            (ELSE! logical_expression)?
            //)?
        )
    ;

logical_expression
    :   relational_expression
        (   (   AND^
            |   OR^
            )
            relational_expression
        )*
    ;

relational_expression
    :   (infix_expression (LT|LT_EQL) infix_expression (LT|LT_EQL)) =>
            between_expression
    //between_expression+ -> ^(BETWEEN_EXPR between_expression+)
    |   infix_expression
        (   (   EQL^
            |   NOT_EQL^
            |   LT^
            |   LT_EQL^
            |   GT^
            |   GT_EQL^
            |   IN^
            |   NOT_IN^
            |   SUBSET^
            |   NOT_SUBSET^
            |   PROP_SUBSET^
            |   NOT_PROP_SUBSET^
            |   SUBSTR^
            |   NOT_SUBSTR^
            |   PROP_SUBSTR^
            |   NOT_PROP_SUBSTR^
            )
            infix_expression
        )?
    ;

between_expression
    :   infix_expression
            (   id1=LT
            |   id1=LT_EQL
            )
        infix_expression
            (   id2=LT
            |   id2=LT_EQL
            )
        infix_expression
        -> ^(BETWEEN_EXPR infix_expression $id1 infix_expression $id2 infix_expression)
    ;

infix_expression returns [ColsAST ast = null]
    :   //(math_variable_declarations AND) =>
        //(math_variable_declarations AND math_expression)
           // -> ^(LOCALVAREXP math_variable_declarations math_expression) |
        (function_type_expression
        (   (   RANGE^
            |   FREE_OPERATOR^
            )
            function_type_expression
        )?)
        | BOOLEAN
    ;
    
function_type_expression
    :  adding_expression (FUNCARROW^ adding_expression)*;

adding_expression
    :   multiplying_expression
        (   (   PLUS^
            |   MINUS^
            |   CAT^
            |   UNION^
            |   INTERSECT^
            |   WITHOUT^
            )
            multiplying_expression
        )*
    ;

multiplying_expression
    :   exponential_expression
        (   (   MULTIPLY^ 
            |   DIVIDE^
            |   MOD^
            |   REM^
            |   DIV^
            )
            exponential_expression
        )*
    ;

exponential_expression
    :   prefix_expression
        (EXP^ exponential_expression)?
    ;

prefix_expression
    :   unary_expression
    |   FREE_OPERATOR^ { $FREE_OPERATOR.setType(UNARY_FREE_OP); }
        prefix_expression
    ;

unary_expression
    :   primitive_expression
    |   NOT^ unary_expression
    |   COMPLEMENT^ unary_expression
    |   MINUS^ { $MINUS.setType(UNARY_MINUS); } unary_expression
    ;

primitive_expression
    :   alternative_expression
    |   (ident ident COLON) => iterated_construct
    |   (ident DOT NUMERIC_LITERAL) => qualified_numeric_literal
    |   dot_expression
    |   lambda_expression
    |   literal_expression
    |   outfix_expression
    |   set_constructor
    |   (LPAREN math_expression COMMA) => tuple_expression
    |   tagged_cartesian_product_type_expression
    |   nested_expression
    ;
    
tagged_cartesian_product_type_expression
    :   CARTPROD^
        (cartprod_variable_declaration_group SEMICOLON!)+
        END!;

// ---------------------------------------------------------------
// Articulated expression rules (expression with '.')
// ---------------------------------------------------------------

dot_expression
    :   (function_expression DOT) =>
        function_expression (DOT clean_function_expression)*
            -> ^(DOT function_expression clean_function_expression*)
    |   function_expression
    ;

function_expression
    :   HASH^ clean_function_expression
    |   clean_function_expression
    ;

clean_function_expression
    :   (ident hat_expression? function_argument_list+)=> ident hat_expression? function_argument_list+ ->
        ^(FUNCTION ident hat_expression? function_argument_list+)
    |   ident
    /*:   (ident -> ^(ident))
        (   (hat_expression)?
            (function_argument_list)+ -> ^(FUNCTION ident hat_expression? function_argument_list+)
        )?*/
        //-> ^(ident)
    ;

hat_expression
    :   CARAT^ (qualified_ident | nested_expression)
    ;

function_argument_list
    :   LPAREN math_expression (COMMA math_expression)* RPAREN -> ^(PARAMS math_expression+)
    ;

// ---------------------------------------------------------------
// Auxilliary expression rules
// ---------------------------------------------------------------

alternative_expression
    :   { otherwise = false; }
        DBL_LBRACE^
        //LBRACE
        //LPAREN^
        (alternative_expression_item)+
        DBL_RBRACE!
        //RBRACE!
        //RPAREN!
    ;

alternative_expression_item
    :   exp=adding_expression { checkOtherwiseItem((ColsAST)exp.getTree()); }
        (   IF^ relational_expression
        |   OTHERWISE^ { otherwise = true; }
        )
        SEMICOLON!
    ;

iterated_construct
    :   id=ident { checkIteratedIdent(id.tree); }
        ident
        COLON math_type_expression 
        (where_clause)?
        (COMMA | OF) LBRACE math_expression RBRACE
        -> ^(ITERATION ident ident math_type_expression (where_clause)? math_expression)
    ;

//NOTE: Allows only very rudimentary lambda expressions.
lambda_expression
    :   LAMBDA^ ident COLON!
        (   (ident DOT ident DOT) => certain_qualified_ident
        |   ident
        )
        DOT! LPAREN! math_expression RPAREN!
    ;

literal_expression
    //:   (ident DOT) => qualified_numeric_literal
    :   NUMERIC_LITERAL
    |   CHARACTER_LITERAL
    |   STRING_LITERAL
    ;
    
program_literal_expression
    :   NUMERIC_LITERAL
    |   CHARACTER_LITERAL
    |   STRING_LITERAL
    ;
    
qualified_numeric_literal
    :   ident DOT NUMERIC_LITERAL -> ^(QUALNUM ident NUMERIC_LITERAL)
    ;

nested_expression
    :   LPAREN math_expression RPAREN -> ^(NESTED math_expression)
    ;

outfix_expression
    :   (lt=LT infix_expression GT)-> ^(ANGLE[$lt] infix_expression)
    |   (ll=LL  math_expression GG) -> ^(DBL_ANGLE[$ll] math_expression)
    |   BAR^ math_expression BAR!
    |   DBL_BAR^ math_expression DBL_BAR!
    ;

parenthesized_expression
    :   LPAREN! math_expression RPAREN!
    ;

set_constructor
    :   LBRACE^ ident
        COLON! math_type_expression
        (where_clause)? BAR!
        math_expression RBRACE!
    ;

tuple_expression
    :   LPAREN math_expression (COMMA math_expression)* RPAREN -> ^(TUPLE math_expression+)
    ;

where_clause
    :   WHERE^ math_expression
    ;

// ===============================================================
// Programming expressions
// ===============================================================

program_expression
    :   program_logical_expression -> ^(EXPR program_logical_expression)
    ;

program_logical_expression
    :   program_relational_expression
        (   (   AND^
            |   OR^
            )
            program_relational_expression
        )*
    ;

program_relational_expression
    :   program_adding_expression
        (   (   EQL^
            |   NOT_EQL^
            |   LT^
            |   LT_EQL^
            |   GT^
            |   GT_EQL^
            )
            program_adding_expression
        )?
    ;

program_adding_expression
    :   program_multiplying_expression
        (   (   PLUS^
            |   MINUS^
            )
            program_multiplying_expression
        )*
    ;

program_multiplying_expression
    :   program_exponential_expression
        (   (   MULTIPLY^ 
            |   DIVIDE^
            |   MOD^
            |   REM^
            |   DIV^
            )
            program_exponential_expression
        )*
    ;

program_exponential_expression
    :   program_unary_expression
        (EXP^ program_exponential_expression)?
    ;

program_unary_expression
    :   program_primitive_expression
    |   NOT^ program_unary_expression
    |   m=MINUS program_unary_expression -> ^(UNARY_MINUS[$m] program_unary_expression)
    ;

program_primitive_expression
    :   program_literal_expression
    |   program_variable_expression
    |   program_nested_expression
    ;

program_variable_expression
    :   (ident (DOT ident)* LPAREN) => program_dot_expression
    |   variable_expression
    ;

program_dot_expression
    :   (ident DOT) =>
        ident
        (DOT program_function_expression)+ -> ^(PROGDOT ident program_function_expression+)
    |   program_function_expression
    ;

program_function_expression
    :   ( ident program_function_argument_list -> ^(FUNCTION ident program_function_argument_list)
        )
    ;

program_function_argument_list
    :   LPAREN (program_expression (COMMA program_expression)*)? RPAREN
        -> ^(PARAMS program_expression*)
    ;

program_nested_expression
    :   LPAREN program_expression RPAREN -> ^(NESTED program_expression)
    ;

// ===============================================================
// Variable Expressions
// ===============================================================

variable_expression
    :   (variable_array_expression DOT) =>
        variable_array_expression
        (DOT variable_array_expression)+ -> ^(VARDOT variable_array_expression+)
    |   variable_array_expression
    ;

variable_array_expression
    :   (ident -> ^(ident))
        (   variable_array_argument_list -> ^(ARRAYFUNCTION ident variable_array_argument_list)
        )?
    ;

variable_array_argument_list
    :   LSQBRACK (program_expression)? RSQBRACK -> ^(PARAMS program_expression?)
    ;

// ===============================================================
// Identifiers
// ===============================================================

certain_qualified_ident
    :   ident DOT ident -> ^(IDENTIFIER ident ident)
    ;

qualified_ident
    :   ident (DOT ident)? -> ^(IDENTIFIER ident ident?)
    ;

ident //returns [ColsAST ast = null]
    :   IDENTIFIER
    ;

math_theorem_ident
    :   ident
    |   NUMERIC_LITERAL
    ;
    
// =============================================================
// ADDED FOR PARSING MATH PROOF EXPRESSIONS
// =============================================================

proof_module
    :   PROOF UNIT id1=ident SEMICOLON
        module_formal_param_section?
        uses_list? //(proof_module_body)? END! id2=ident
        SEMICOLON ->
        ^(PROOFS_FOR ident
        module_formal_param_section?
        uses_list? //(proof_module_body)? END! id2=ident
        )
    |   PROOFS_FOR^ id1=ident SEMICOLON!
        module_formal_param_section?
        uses_list? //(proof_module_body)? END! id2=ident
        SEMICOLON!
    ;
    
proof_module_body
    :   (math_item_sequence -> ^(PROOFBODY math_item_sequence)
    |   proof -> ^(PROOFBODY proof))
    ;
    
proof
    :   PROOF^ OF!
        math_item_reference
        COLON!
        ( (LSQBRACK IDENTIFIER RSQBRACK LPAREN BASECASE) => base_case_statement_head
        | (LPAREN BASECASE) => base_case_statement_body
        | (LSQBRACK IDENTIFIER RSQBRACK LPAREN INDUCTIVECASE) => inductive_case_statement_head
        | (LPAREN INDUCTIVECASE) => inductive_case_statement_body
        | (LSQBRACK IDENTIFIER RSQBRACK) => headed_proof_expression
        | proof_expression )*
        QED!
    ;
    
base_case_statement_head
    :   LSQBRACK! IDENTIFIER^ RSQBRACK! base_case_statement_body
    ;
    
base_case_statement_body
    :   LPAREN! BASECASE^ RPAREN! proof_expression
    ;
    
inductive_case_statement_head
    :   LSQBRACK! IDENTIFIER^ RSQBRACK! inductive_case_statement_body
    ;
    
inductive_case_statement_body
    :   LPAREN! INDUCTIVECASE^ RPAREN! proof_expression
    ;

math_item_reference
    :
    (   theorem_name -> ^(MATHITEMREF theorem_name)
    |   lemma_name -> ^(MATHITEMREF lemma_name)
    |   property_name -> ^(MATHITEMREF property_name)
    |   corollary_name -> ^(MATHITEMREF corollary_name)
    )
    ;
    
theorem_name
    :   THEOREM^ ident
    ;
    
lemma_name
    :   LEMMA^ ident
    ;

property_name
    :   PROPERTY^ ident
    ;
    
corollary_name
    :   COROLLARY^ math_theorem_ident
    ;
    
proof_expression_list
    :   ( (LSQBRACK IDENTIFIER RSQBRACK DEDUCTION) => () { break; }
        | (DEDUCTION) => () { break; }
        | (LSQBRACK IDENTIFIER RSQBRACK) => headed_proof_expression -> ^(PROOFEXPRLIST headed_proof_expression)
        | proof_expression -> ^(PROOFEXPRLIST proof_expression)
        )*
    ;
    
headed_proof_expression
    :   LSQBRACK! IDENTIFIER^ RSQBRACK! proof_expression
    ;

proof_expression
    :
    (   goal_declaration
    |   standard_definition_declaration
    |   supposition_deduction_pair
    |   justification_declaration
    )
    ;
    
goal_declaration
    :   GOAL^ math_expression SEMICOLON!
    ;
    
supposition_deduction_pair
    :   supposition_declaration SEMICOLON
        proof_expression_list
        (LSQBRACK IDENTIFIER RSQBRACK)?
        deduction_declaration SEMICOLON
        -> ^(SUPDEDUC supposition_declaration proof_expression_list IDENTIFIER? deduction_declaration)
    ;
    
supposition_declaration
    :   SUPPOSITION^
        (
          ((ident COLON) => (math_variable_declarations (AND! math_expression)?))
          | ((ident COMMA ident) => (math_variable_declarations (AND! math_expression)?))
          //| (math_expression ((AND! math_variable_declarations)?))
        )
    ;
    
math_variable_declarations
    :   math_variable_declaration_group (COMMA math_variable_declaration_group)*
        -> ^(DECLARATIONS math_variable_declaration_group+)
    ;
    
deduction_declaration
    :   DEDUCTION^ math_expression
    ;
    
justification_declaration
    :   math_expression justification SEMICOLON -> ^(SIMPLIFICATION math_expression justification)
    ;
    
justification
    :   BY^
        ( (hyp_desig COMMA) => double_hyp_rule_justification
      | (hyp_desig AMPERSAND) => single_hyp_rule_justification
      | (hyp_desig) => hyp_desig
      | simple_justification
      | (DEFINITION) => def_justification )
      //| (LPAREN) => def_justification)  //XXX : Maybe an 'or' marker to combine these two?
    ;

double_hyp_rule_justification
    :   hyp_desig COMMA! hyp_desig
      (AMPERSAND! rules_set_1)?
    ;

single_hyp_rule_justification
    :   hyp_desig AMPERSAND! (rules_set_1 | rules_set_2 | def_justification)
    ;

def_justification
    :   
    ( DEFINITION ((UNIQUE) => (UNIQUE) | (fn_name))
    | LPAREN ident RPAREN OF DEFINITION fn_name (FROM ident)? -> ^(INDEXED_DEFINITION ident fn_name ident?)
    )
    ;

simple_justification
    :   rules_set_2 | rules_set_3
    ;

rules_set_1
    :
    ( MODUS PONENS -> ^(RULE1 MODUS_PONENS)
      | AND RULE -> ^(RULE1 AND_RULE)
      | CONTRADICTION -> ^(RULE1 CONTRADICTION)
      | EQUALITY -> ^(RULE1 EQUALITY)
      | ALTERNATIVE ELIMINATION -> ^(RULE1 ALTERNATIVE_ELIMINATION)
      | COMMON CONCLUSION -> ^(RULE1 COMMON_CONCLUSION)
      )
    ;

rules_set_2
    :   
    ( REDUCTIO AD ABSURDUM -> ^(RULE2 REDUCTIO_AD_ABSURDUM)
      | (UNIVERSAL GENERALIZATION)=> UNIVERSAL GENERALIZATION -> ^(RULE2 UNIVERSAL_GENERALIZATION)
      | UNIVERSAL INSTANTIATION -> ^(RULE2 UNIVERSAL_INSTANTIATION)
      | (EXISTENTIAL GENERALIZATION)=> EXISTENTIAL GENERALIZATION -> ^(RULE2 EXISTENTIAL_GENERALIZATION)
      | EXISTENTIAL INSTANTIATION  -> ^(RULE2 EXISTENTIAL_INSTANTIATION)
      | OR RULE  -> ^(RULE2 OR_RULE)
      | CONJUNCT ELIMINATION -> ^(RULE2 CONJUNCT_ELIMINATION)
      | QUANTIFIER DISTRIBUTION -> ^(RULE2 QUANTIFIER_DISTRIBUTION)
      )
    ;

rules_set_3
    :   EXCLUDED MIDDLE -> ^(RULE3 EXCLUDED_MIDDLE)
    ;

hyp_desig
    :   
    ( SELF -> ^(HYPDESIG SELF)
    | lemma_call -> ^(HYPDESIG lemma_call)
    | theorem_call -> ^(HYPDESIG theorem_call)
    | corollary_name -> ^(HYPDESIG corollary_name)
    | supposition_call -> ^(HYPDESIG supposition_call)
    | definition_call -> ^(HYPDESIG definition_call)
    | reference_marker_call  -> ^(HYPDESIG reference_marker_call)
    )
    ;

lemma_call
    :   LEMMA^ ident
    ;

theorem_call
    :   THEOREM^ ident
    ;

supposition_call
    :   SUPPOSITION
    ;

definition_call
    :   (LPAREN ident RPAREN! OF!)? 
      DEFINITION^ fn_name (LPAREN! qualified_ident (COMMA! ident) RPAREN!)?
      (FROM! ident)?
    ;
    
reference_marker_call
    :   ident -> ^(REFCALL ident)
    ;

fn_name
    :   infix_symbol | prefix_symbol | ident
    ;
    