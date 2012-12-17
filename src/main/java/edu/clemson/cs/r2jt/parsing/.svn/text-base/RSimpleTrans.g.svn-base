tree grammar RSimpleTrans;

options {
  language = Java;
  output = template;
  //output = AST;
  tokenVocab = RParser;      
  ASTLabelType = CommonTree;
  //rewrite=true;
}


@header {
    package edu.clemson.cs.r2jt.parsing;
    
    import edu.clemson.cs.r2jt.errors.ErrorHandler;
    import edu.clemson.cs.r2jt.utilities.Flag;
		import java.io.FileOutputStream;
		import java.io.IOException;
		import java.io.OutputStream;
		import java.util.regex.*;
		import java.util.Arrays;
}

@members {
    private static final String FLAG_SECTION_NAME = "SimpleTranslation";
          
    private static final String FLAG_DESC_TRANSLATE = 
                  "Translate RESOLVE file to simple Java source file. (no imports)";
    public static final Flag FLAG_SIMPLE_TRANSLATE = 
                  new Flag(FLAG_SECTION_NAME, "translateSimple", FLAG_DESC_TRANSLATE);
                  
    public static final void setUpFlags(){
          }
          
    private ErrorHandler err;
    
    private int currLine = 1;
    private int indentLevel = 0;
    
    private String[] standardTypes = {
      "Boolean",
      "Integer",
      "Character",
      "Char_Str",
      "Stack",
      "Queue"
    };
    
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
    
    private String[] adjustLine(CommonTree token){
        String[] ret = {"",""};
        String nl = "";
        String indent = "";
        int line = token.getLine();
        //int pos = token.getCharPositionInLine();
        if(line > currLine) {
          //System.err.println("token: " + token + " CurrLine: "+currLine+" myLine: "+line);
          int i = 0;
	        for(i = currLine; i < line; i++){
	            nl += "\n";
	            currLine++;
	        }
          ret[1] = getIndent();
	        //int newPos = pos / 4 + 1;
	        //for(i = 0; i < pos; i++){
	            //indent += " ";
	        //}
          //System.err.println("token: " + token + " indent: "+i);
        }
        ret[0] = nl;
        //ret[1] = indent;
        return ret;
    }
    
    private void incIndent(){
      indentLevel = indentLevel + 1;
    }
    
    private void decIndent(){
      indentLevel = indentLevel - 1;
    }
    
    private void incLine(){
      currLine++;
    }
    
    private String getIndent(){
      String indent = "";
      for(int i = 0; i < indentLevel; i++){
        indent += "    ";
      }
      return indent;
    }
    
    public void outputAsFile(String fileName, String fileContents) {
					String[] temp = fileName.split("\\.");
					fileName = temp[0] + ".java";
					  if (fileContents != null && fileContents.length() > 0) {
					      try {   
					          byte buf[] = fileContents.getBytes();
					          OutputStream outFile = new FileOutputStream(fileName);
					          outFile.write(buf);
					          //System.out.println("Writing file: "+fileName);
					      } catch (IOException ex) {
					          //FIX: Something should be done with this exception
					          ;
					      }
					  } else {
					      //System.out.println("No translation available for " + fileName);
					  }
    }
    
    private String templatePrepend(StringTemplate st, String inject){
          String orig = st.toString();
          String trimmed = orig.trim();
          char c = trimmed.charAt(0);
          int index = orig.indexOf(c);
          String ret = orig.substring(0,index);
          ret += inject;
          ret += orig.substring(index,orig.length());
          //System.err.println(ret);
          return ret;
    }
        
    private String templateAppend(StringTemplate st, String inject){
          String orig = st.toString();
          String trimmed = orig.trim();
          char c = trimmed.charAt(trimmed.length());
          int index = orig.indexOf(c);
          String ret = orig.substring(0,index);
          ret += inject;
          ret += orig.substring(index,orig.length());
          //System.err.println(ret);
          return ret;
    }
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
        |   fm+=facility_module -> template(dec6={$fm})"<dec6>" 
        )
        
    ;

// ---------------------------------------------------------------
//  Theory Module
// ---------------------------------------------------------------

theory_module
    :   ^(THEORY ident
        (module_formal_param_section)?
        (uses_list)?
        (math_item_sequence)?)
        
    ;


math_item_sequence
    :   (math_item)+
    ;

math_item
    :   formal_type_declaration
    |   math_type_declaration
    |   definition_declaration
    |   categorical_definition_declaration
    |   math_assertion_declaration
    |   subtype_declaration
    ;

// ---------------------------------------------------------------
// Concept Module
// ---------------------------------------------------------------

conceptual_module
    :   ^(MODULE_CONCEPT id1=ident 
        (module_formal_param_section)? 
        (uses_list)?
        (requires_clause)?
        (concept_item_sequence)?)
        
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
        ^(MODULE_PROFILE id1=ident
        (module_formal_param_section)?
        ident ident (ident ident)?
        (uses_list)?
        (requires_clause)?
        (performance_item_sequence)?)
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
    :   ^(MODULE_ENHANCEMENT id1=ident 
        (module_formal_param_section)?
         ident 
        (uses_list)?
        (requires_clause)?
        (enhancement_item_sequence)?)
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
    :   ^(MODULE_REALIZATION id1=ident 
        (WITH_PROFILE ident)? (module_formal_param_section)? 
        (   (ident OF)=> body_enhancement_section
        |   body_concept_section
        ) 
        (uses_list)?
        (requires_clause)?
        (body_item_sequence)?)
    ;

body_concept_section
    :   ^(CONCEPT ident (ident)*)
    ;

body_enhancement_section
    :   ^(ENHANCEMENT ident ident (added_enhancement_section)*)
    ;

added_enhancement_section
    :   ^(ENHANCED ident
        module_argument_section
        REALIZED ident
        (WITH_PROFILE ident)? module_argument_section?)
    |   ^(ENHANCED ident
        REALIZED ident
        (WITH_PROFILE ident)? module_argument_section?)
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
    |   operation_procedure_declaration
    |   operation_recursive_procedure_declaration
    |   aux_operation_declaration
    |   procedure_declaration
    |   recursive_procedure_declaration
    |   definition_declaration    
    |   facility_declaration
    ;

// ---------------------------------------------------------------
// Facility Module
// ---------------------------------------------------------------

facility_module
scope{
boolean isFacility;
}
@init{
String[] adj = {"",""};
String preIndent = "";
String postIndent = "";
}
//@init {$facility_module::isFacility=true;}
    :   ^(FACILITY id=ident short_facility_section (uses_list)?)
    //|   ^(FACILITY {$facility_module::isFacility=true;} ident {$facility_module::isFacility=false;} (ul+=uses_list)? (fis+=facility_item_sequence)?)
    |   ^(FACILITY {adj = adjustLine($FACILITY); incIndent();} ident (ul+=uses_list)? (fis+=facility_item_sequence)? {decIndent();})
        -> template(name={$ident.text},
                    uses={$ul},
                    items={$fis},
                    ln={adj[0]},in={adj[1]})
                    "<ln><in>public class <name> {<uses> <items><\n>}"
    ;

short_facility_section
    :    ident
        module_argument_section?
        facility_enhancement*
          ident
        module_argument_section?
        facility_body_enhancement*
        
    ;

facility_item_sequence
    :   fi+=facility_item+
        -> template(item={$fi})"<item>"
    ;

facility_item
    :   state_variable_declaration
        -> template(svd={$state_variable_declaration.text})"<svd>"
    |   module_facility_init_declaration
        -> template(mfid={$module_facility_init_declaration.text})"<mfid>"
    |   module_facility_final_declaration
        -> template(mffd={$module_facility_final_declaration.text})"<mffd>"
    |   ftd+=facility_type_declaration
        //{adj = adjustLine($facility_type_declaration.token);}
        //-> {$parentLine == 0 || $parentLine == $facility_type_declaration.line}? template(ftd={$facility_type_declaration.text})"/*@ <ftd> */"
        -> template(ftd={$ftd})"<ftd>"
    |   operation_recursive_procedure_declaration
        -> template(orpd={$operation_recursive_procedure_declaration.text})"<orpd>"
    |   opd+=operation_procedure_declaration
        //{adj = adjustLine($operation_procedure_declaration.token);}
        -> template(opd={$opd})"<opd>"
    |   dd+=definition_declaration
        -> template(dd={$dd})"<dd>"
    |   fd+=facility_declaration
        -> template(fd={$fd})"<fd>"
    ;

// ===============================================================
// Rules for module parameters and uses clause
// ===============================================================

// ---------------------------------------------------------------
// Module parameters
// ---------------------------------------------------------------

module_formal_param_section
    :   ^(PARAMS module_parameter (module_parameter)*)
    ;

module_parameter
    :   definition_parameter
    |   
        constant_parameter
    |   concept_type_parameter
    |   operation_parameter
    |   concept_realization_parameter
    ;

definition_parameter
    :   ^(DEFINITION definition_signature)
    ;

constant_parameter
    :   ^(EVALUATES variable_declaration_group)
    ;

concept_type_parameter
    :   ^(TYPE ident)
    ;

operation_parameter
    :   operation_declaration
    ;

concept_realization_parameter
    :   ^(REALIZATION ident
        ident)
    ;

// ---------------------------------------------------------------
//  Uses Declaration
// ---------------------------------------------------------------

uses_list
@init{
//if(parentLine > currLine) currLine = parentLine;
String[] adj = {"",""};
}
    :   (uc+=uses_clause)+ //{adj = adjustLine($uses_clause.token);}
        //-> {$parentLine == 0 || $parentLine == $uses_clause.line}? template(clause={$uses_clause.text})"/*@ <clause> */"
        //->  template(clause={$uses_clause.text},ln={adj[0]},in={adj[1]})"<ln><in>/*@ <clause> */"
        -> template(uses={$uc})"<uses>"
    ;

uses_clause //returns [CommonTree token = null]
@init{
//if(parentLine > currLine) currLine = parentLine;
String[] adj = {"",""};
}
    :   ^(USES {adj = adjustLine($USES);} id+=ident+) //{$token = $USES;}
        -> template(u={$USES.text},uses={$id},ln={adj[0]},in={adj[1]})"<ln><in>/*@ <u> <uses; separator=\", \"> */"
        //-> template(uses={$id})"<uses>"
    ;

// ===============================================================
// Rules for module level declarations and items
// ===============================================================

// ---------------------------------------------------------------
// Math Type Declarations
// ---------------------------------------------------------------

formal_type_declaration
    :   ^(LOCAL_MATH_TYPE ident)
    ;
    
subtype_declaration // this is broken, but commented out in dev
    :   ^(MATH_SUBTYPE ((ident DOT) => qualified_type | ident) 
        ((ident DOT) => qualified_type | ident))
    ;
    
qualified_type
    //:   ident DOT^ ident
    :   ^(DOT ident ident)
    ;

math_type_declaration
    :   ^(MATH_TYPE ident
        math_type_expression)
    ;
//primitive_type_expression
confirm_math_type_declaration
    :   ^(CONFIRM_TYPE math_variable_declaration)
    ;

sset_type_expression
    :   ^(TYPEX sset_function_type_expression)
    ;

sset_function_type_expression
    //:   (sset_domain_expression FUNCARROW^) =>
    //    sset_domain_expression FUNCARROW^ SSET
    :   (FUNCARROW sset_domain_expression) =>
        FUNCARROW sset_domain_expression SSET
    |   SSET
    ;

sset_domain_expression
    :   ^(TIMES SSET+)
    |   SSET
    ;

// ---------------------------------------------------------------
// Math Assertions
// ---------------------------------------------------------------

math_assertion_declaration
    :   (   ^(AXIOM (ps=math_theorem_ident)? exp=math_expression)
        |   ^(THEOREM (ps=math_theorem_ident)? exp=math_expression)
        |   ^(PROPERTY (ps=math_theorem_ident)? exp=math_expression)
        |   ^(LEMMA (ps=math_theorem_ident)? exp=math_expression)
        |   ^(COROLLARY (ps=math_theorem_ident)? exp=math_expression)
        |   COMMUTATIVITY (ps=math_theorem_ident)? exp=math_expression
        )
    ;

constraint_clause
    :   ^(CONSTRAINT math_expression) 
    ;

correspondence_clause
    :   ^(CORR math_expression) 
    ;

convention_clause
    :   ^(CONVENTION math_expression) 
    ;

// ---------------------------------------------------------------
// State Variable Declarations
// ---------------------------------------------------------------

concept_state_variable_declaration
    :   ^(VAR math_variable_declaration_group)
    ;

state_variable_declaration
    :   ^(VAR variable_declaration_group) 
    ;
    
state_aux_variable_declaration
    :   ^(AUX_VAR variable_declaration_group) 
    ;

// ---------------------------------------------------------------
//  Facility Declarations
// ---------------------------------------------------------------

facility_declaration
@init{
String[] adj = {"",""};
String[] adj2 = {"",""};
String newRealiz = "";
String conceptEntryType = "";
String newConceptParams = "";
}
    :   ^(FACILITY {adj = adjustLine($FACILITY);incIndent();} varIdent+=ident
        conIdent+=ident
        con_mas+=module_argument_section?
        {
          if($con_mas != null){
	          String args = ((StringTemplate)$con_mas.get(0)).toString();
	          String[] params = args.split(",");
	          for(int i = 0; i < params.length; i++){
	           if(Arrays.asList(standardTypes).contains(params[i])){
               conceptEntryType = "<" + params[i] + ">";
               params[i] = null;
               break;
             }
	          }
            for(int i = 0; i < params.length; i++){
             if(params[i] != null){
               params[i] = params[i].trim();
               newConceptParams += params[i];
               if(i < params.length - 1){
                newConceptParams += ", ";
               }
             }
            }
          }
        }
        fe+=facility_enhancement*
        REALIZED {adj2 = adjustLine($REALIZED);}
        realizIdent+=ident
        {newRealiz = templatePrepend((StringTemplate)$realizIdent.get(0),"new ");}
        (WITH_PROFILE ident)? realiz_mas+=module_argument_section?
        fbe+=facility_body_enhancement*) {decIndent();}
        -> template(ln={adj[0]},in={adj[1]},
                    varIdent={$varIdent},
                    conIdent={$conIdent},
                    con_type={conceptEntryType},
                    con_mas={newConceptParams},
                    ln2={adj2[0]},in2={adj2[1]},
                    realizIdent={newRealiz},
                    realiz_mas={$realiz_mas},
                    fbe={$fbe})
            "<ln><in>private <conIdent><con_type> <varIdent> = <ln2><in2><realizIdent><con_type>(<con_mas>);<fbe>"       
    ;

facility_enhancement
@init{
String[] adj = {"",""};
}
    :   ^(ENHANCED {adj = adjustLine($ENHANCED);} ident
        module_argument_section?)
    ;

facility_body_enhancement
@init{
String[] adj = {"",""};
String[] adj2 = {"",""};
}
    :   ^(ENHANCED {adj = adjustLine($ENHANCED);} id+=ident
        mas+=module_argument_section
        REALIZED {adj2 = adjustLine($REALIZED);} id2+=ident
        (WITH_PROFILE ident)? mas2+=module_argument_section?)
        -> template(ln={adj[0]},in={adj[1]},
                    id={$id},mas={$mas},
                    ln2={adj2[0]},in2={adj2[1]},
                    id2={$id2},mas2={$mas2})
                    "<ln><in>/*@ enhanced by <id>(<mas>)<ln2><in2> realized by <id2>(<mas2>); */"
    |   ^(ENHANCED {adj = adjustLine($ENHANCED);} id+=ident
        REALIZED {adj2 = adjustLine($REALIZED);} id2+=ident
        (WITH_PROFILE ident)? mas2+=module_argument_section?)
        -> template(ln={adj[0]},in={adj[1]},
                    id={$id},
                    ln2={adj2[0]},in2={adj2[1]},
                    id2={$id2},mas2={$mas2})
                    "<ln><in>/*@ enhanced by <id><ln2><in2> realized by <id2>(<mas2>); */"
    ;

module_argument_section
@init{
String[] adj = {"",""};
}
    :   ^(PARAMS {adj = adjustLine($PARAMS);} ma+=module_argument+)
        -> template(ln={adj[0]},in={adj[1]},ma={$ma})"<ma; separator=\", \">"
    ;

module_argument
    :   (qualified_ident)=> qi+=qualified_ident -> template(qi={$qi})"<qi>"
    |   pe+=program_expression -> template(pe={$pe})"<pe>"
    ;

// ===============================================================
// Definition Declarations
// ===============================================================

defines_declaration
    :   ^(DEFINES definition_signature) 
    ;

definition_declaration
    :   implicit_definition_declaration
    |   inductive_definition_declaration
    |   sdd+=standard_definition_declaration -> template(sdd={$sdd})"<sdd>"
    //|   categorical_definition_declaration
    ;

implicit_definition_declaration
    :   ^(IMPLICIT_DEF definition_signature
        math_expression)
    ;

inductive_definition_declaration
    :   ^(INDUCTIVE_DEF definition_signature
        indexed_expression indexed_expression)
    ;

standard_definition_declaration
@init{
String[] adj = {"",""};
}
    :   ^(DEFINITION {adj = adjustLine($DEFINITION);} ds=definition_signature
        ( me=math_expression)?)
        -> template(ln={adj[0]},in={adj[1]},
                    def={$DEFINITION.text},
                    ds={$ds.text},me={$me.text})
        "<ln><in>/*@ <def> <ds> <me> */"
    ;
    
categorical_definition_declaration
    :   ^(CATEGORICAL_DEFINITION categorical_definition_construct ^(RELATED_BY math_expression))
    ;

definition_signature
    :   (   infix_definition_construct
        |   outfix_definition_construct
        |   sdc+=standard_definition_construct -> template(sdc={$sdc})"<sdc>"
        )
         math_type_expression
    ;

infix_definition_construct
    :   singleton_definition_parameter
        (ident | infix_symbol)
        singleton_definition_parameter
    ;

outfix_definition_construct
    :   BAR singleton_definition_parameter 
    |   DBL_BAR singleton_definition_parameter DBL_
    |   LT singleton_definition_parameter 
    |   LL singleton_definition_parameter 
    ;

standard_definition_construct
    :   (id+=ident | prefix_symbol | quant_symbol | NUMERIC_LITERAL)
        (dfps+=definition_formal_param_section)?
        -> template(id={$id},dfps={$dfps})"<id><dfps>"
    ;

categorical_definition_construct
    :   ^(DEFINITION definition_signature (DEFINITION definition_signature)*)
    ;

// FIX: IDENT getText() should be i or ii. Check for this and report
// an error if it is not the case.
indexed_expression
    :   math_expression
        
    ;

singleton_definition_parameter
    :   ^(PARAMS math_variable_declaration)
    ;

definition_formal_param_section
    :   ^(PARAMS math_variable_declaration_group+)
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

operation_procedure_declaration //returns [CommonTree token = null]
@init{
String[] adj = {"",""};
String modifier = "private";
String returnType = "void";
String sig = "";
String returnVarDec = "";
String returnStmt = "";
String statementSequence = "";
}
    :   ^(OPERATION {adj = adjustLine($OPERATION);incIndent();} id+=ident
        ofps+=operation_formal_param_section
        pte+=program_type_expression? 
        ac+=affects_clause*
        rc+=requires_clause?
        ec+=ensures_clause?
        
        dc+=decreasing_clause?
        fd+=facility_declaration*
        vd+=variable_declaration*
        avd+=aux_variable_declaration*
        ss+=statement_sequence) 
        {
          if(((StringTemplate)$id.get(0)).toString().trim().equals("main")){
            modifier = "public static";
          }
          if($pte != null){
            returnType = ((StringTemplate)$pte.get(0)).toString().trim();
            returnVarDec = returnType + " _ret;";
            returnStmt = "return _ret; ";
            String name = ((StringTemplate)$id.get(0)).toString().trim();
            for(Object s: $ss)
            {
              statementSequence += s.toString().replace(name,"_ret");
            }
          }
          else{
            for(Object s: $ss)
            {
              statementSequence += s.toString();
            }
          }
          sig = modifier + " " + returnType + " ";
          sig = templatePrepend((StringTemplate)$id.get(0),sig);
        }
        {decIndent();incLine();}
        //{$token = $OPERATION;})
        -> template(ln={adj[0]},in={adj[1]},
                    ident={sig},params={$ofps},
                    retDec={returnVarDec},
                    pte={$pte},ac={$ac},
                    ac={$ac},rc={$rc},
                    ec={$ec},dc={$dc},
                    fd={$fd},vd={$vd},
                    avd={$avd},ss={statementSequence},
                    retStmt={returnStmt})
                    "<ln><in><ident> (<params>) { <retDec> <ac> <rc> <ec> <dc> <fd> <vd> <avd> <ss><\n><in><retStmt>}"
        //-> template(op={$OPERATION},ident={$id.text},ln={adj[0]},in={adj[1]})"<ln><in> /@* <op> */  <ident>"
    ;
    
operation_recursive_procedure_declaration
    :   ^(RECURSIVE_OPERATION_PROCEDURE id1=ident
        operation_formal_param_section
        ( program_type_expression)? 
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
    :   ^(OPERATION ident
        (operation_formal_param_section)
        ( program_type_expression)? 
        (affects_clause)*
        (requires_clause)?
        (ensures_clause)?)
    ;
    
performance_operation_declaration
    :   ^(OPERATION ident
        (operation_formal_param_section)
        (program_type_expression)?
        (ensures_clause)*
        (duration_clause)?
        (mainp_disp_clause)?)
    ;
    
aux_operation_declaration
    :   ^(AUX_OPERATION ident
        operation_formal_param_section
        program_type_expression?
        affects_clause*
        requires_clause?
        ensures_clause?)
    ;

procedure_declaration
    :   ^(PROCEDURE id1=ident
        (operation_formal_param_section)
        ( program_type_expression)? 
        (affects_clause)*
        (decreasing_clause)?
        (facility_declaration)*
        (variable_declaration)*
        (aux_variable_declaration)*
        statement_sequence)
    ;
    
recursive_procedure_declaration
    :   ^(RECURSIVE_PROCEDURE ident
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
    :   ^(PARAMS ofpg+=operation_formal_param_group*) -> template(ofpg={$ofpg})"<ofpg; separator=\", \">"
    ;

operation_formal_param_group
@init{
String[] adj = {"",""};
}
    :   ^(VAR {adj = adjustLine($VAR);} abstract_mode vdg+=variable_declaration_group)
        ->template(ln={adj[0]},in={adj[1]},vdg={$vdg})
        "<ln><in><vdg>"
    ;

variable_declaration
@init{
String[] adj = {"",""};
}
    :   ^(VAR {adj = adjustLine($VAR);} vdg+=variable_declaration_group)
        ->template(ln={adj[0]},in={adj[1]},vdg={$vdg})"<ln><in><vdg>;" 
    ;
    
aux_variable_declaration
    :   ^(AUX_VAR aux_variable_declaration_group) 
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

affects_clause
@init{
String[] adj = {"",""};
}
    :   ^(AFFECTS {adj = adjustLine($AFFECTS);} am=abstract_mode qi1+=qualified_ident (COMMA qi2+=qualified_ident)*)
        -> template(ln={adj[0]},in={adj[1]}, clause={$AFFECTS.text},
                    am={$am.text},qi1={$qi1},qi2={$qi2})"<ln><in>/*@ <clause><am> <qi1>, <qi2> */"
    ;

requires_clause
@init{
String[] adj = {"",""};
}
    :   ^(REQUIRES {adj = adjustLine($REQUIRES);} me=math_expression)
        -> template(ln={adj[0]},in={adj[1]}, clause={$REQUIRES}, me={$me.text})"<ln><in>/*@ <clause><am> <me> */" 
    ;

ensures_clause
@init{
String[] adj = {"",""};
}
    :   ^(ENSURES {adj = adjustLine($ENSURES);} me=math_expression)
        -> template(ln={adj[0]},in={adj[1]}, clause={$ENSURES.text}, me={$me.text})"<ln><in>/*@ <clause><am> <me> */" 
    ;

duration_clause
    :   ^(DURATION math_expression)
    ;

mainp_disp_clause
    :   ^(MAINP_DISP math_expression)
    ;

// ===============================================================
// Type Declarations
// ===============================================================

type_declaration
    :   ^(  TYPE_FAMILY ident
            structural_math_type_expression
            ident
            (constraint_clause)?
            (type_concept_init_declaration)?
            (type_concept_final_declaration)?
        )
    ;
    
performance_type_declaration
		:   ^(TYPE_FAMILY ident math_type_expression
		        constraint_clause?
		        performance_type_init_declaration?
		        performance_type_final_declaration?)
		;

type_representation_declaration
    :   ^(TYPE ident
        structural_program_type_expression 
        (convention_clause)?
        (correspondence_clause)?
        (type_body_init_declaration)?
        (type_body_final_declaration)?)
    ;

facility_type_declaration// returns [CommonTree token = null]
@init{
String[] adj = {"",""};
}
    :   ^(TYPE {adj = adjustLine($TYPE);} id+=ident
        spte+=structural_program_type_expression 
        (cc=convention_clause)?
        (tfid=type_facility_init_declaration)? 
        (tffd=type_facility_final_declaration)?)
        //{$token = $TYPE;}
        -> template(ln={adj[0]},in={adj[1]},
                    id={$id},
                    spte={$spte},
                    cc={$cc.text},
                    tfid={$tfid.text},
                    tffd={$tffd.text})"<ln><in>private class <id> { <spte> <cc> <tfid> <tffd> }"
    ;


// ---------------------------------------------------------------
// Initialization and finalization rules
// ---------------------------------------------------------------

// Module level init and final -----------------------------------

module_concept_init_declaration
    :   ^(FAC_INIT concept_init_final_section)
        
    ;

module_concept_final_declaration
    :   ^(FAC_FINAL concept_init_final_section)
        
    ;

performance_module_init_declaration
    :   ^(PERF_INIT performance_init_section)
    ;

performance_module_final_declaration
    :   ^(PERF_FINAL performance_final_section)
    ;

module_body_init_declaration
    :   ^(FAC_INIT body_init_final_section)
        
    ;

module_body_final_declaration
    :   ^(FAC_FINAL body_init_final_section)
        
    ;

module_facility_init_declaration
    :   ^(FAC_INIT facility_init_final_section)
        
    ;

module_facility_final_declaration
    :   ^(FAC_FINAL facility_init_final_section)
        
    ;

// Type level init and final -----------------------------------

type_concept_init_declaration
    :   ^(INITIALIZATION concept_init_final_section)
    ;

type_concept_final_declaration
    :   ^(FINALIZATION concept_init_final_section)
    ;

performance_type_init_declaration
    :   ^(INITIALIZATION performance_init_section)
    ;

performance_type_final_declaration
    :   ^(FINALIZATION performance_final_section)
    ;

type_body_init_declaration
    :   ^(INITIALIZATION body_init_final_section)
    ;

type_body_final_declaration
    :   ^(FINALIZATION body_init_final_section)
    ;

type_facility_init_declaration
    :   ^(INITIALIZATION facility_init_final_section)
    ;

type_facility_final_declaration
    :   ^(FINALIZATION facility_init_final_section)
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
    ;

facility_init_final_section
    :   (affects_clause)*
        (requires_clause)?
        (ensures_clause)?
        (facility_declaration)*
        (variable_declaration)*
        (aux_variable_declaration)*
        statement_sequence
    ;

// ===============================================================
// Statements
// ===============================================================

statement
    :   (   ^(STATEMENT is+=if_statement)
            -> template(is={$is})"<is>"
        |   ^(STATEMENT selection_statement)
        |   ^(STATEMENT wls+=while_loop_statement)
            -> template(wls={$wls})"<wls>"
        |   ^(STATEMENT iterate_loop_statement)
        |   ^(STATEMENT ss+=swap_statement)
            -> template(ss={$ss})"<ss>"
        |   ^(STATEMENT fa+=function_assignment)
            -> template(fa={$fa})"<fa>"
        |   ^(STATEMENT poc+=procedure_operation_call)
            -> template(poc={$poc})"<poc>"
        |   ^(STATEMENT remember_statement)
        |   ^(STATEMENT forget_statement)
        |   ^(STATEMENT confirm_statement)
        |   ^(STATEMENT assume_statement)
        |   ^(STATEMENT aux_code_statement)
        )
    ;

in_aux_statement
    :   (   ^(STATEMENT if_statement)
        |   ^(STATEMENT selection_statement)
        |   ^(STATEMENT while_loop_statement)
        |   ^(STATEMENT iterate_loop_statement)
        |   ^(STATEMENT swap_statement)
        |   ^(STATEMENT function_assignment)
        |   ^(STATEMENT procedure_operation_call)
        |   ^(STATEMENT remember_statement)
        |   ^(STATEMENT forget_statement)
        |   ^(STATEMENT confirm_statement)
        |   ^(STATEMENT assume_statement)
        |   ^(STATEMENT aux_code_statement)
        )
    ;


statement_sequence
    :   ^(STATEMENT_SEQUENCE stmts+=statement*)
        -> template(stmts={$stmts})"<stmts>"
    ;
    
in_aux_statement_sequence
    :   ^(STATEMENT_SEQUENCE in_aux_statement*)
    ;

// Function assignment -------------------------------------------

function_assignment
    //:   variable_expression ASSIGN_OP^ program_expression
    :   ^(ASSIGN_OP lhs+=variable_expression rhs+=program_expression)
        -> template(lhs={$lhs},rhs={$rhs})"<lhs> = <rhs>;"
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
@init{
String[] adj = {"",""};
}
    :   ^(IF {adj = adjustLine($IF);} c+=condition {incIndent();}
         ss+=statement_sequence
        (ei+=elsif_item)*
        (ep+=else_part)? ) {decIndent();incLine();}
        -> template(ln={adj[0]},in={adj[1]},
                    //if={$IF.text},
                    c={$c},
                    ss={$ss},
                    ei={$ei},
                    ep={$ep})
        "<ln><in>if(<c>){<ss><ei><ep><\n><in>}"
    ;

elsif_item
    :   ^(ELSIF condition
         statement_sequence)
    ;

else_part
    :   ^(ELSE statement_sequence)
    ;

condition
    :   pe+=program_expression -> template(pe={$pe})"<pe>"
    ;

// Aux Code statement --------------------------------------------
aux_code_statement
    :   ^(AUX_CODE
        in_aux_statement_sequence) 
    ;


// Iterate statement ---------------------------------------------

iterate_loop_statement
    :   ^(ITERATE
        (changing_clause)?    
        maintaining_clause
        (decreasing_clause)?
        iterate_item_sequence) 
    ;

iterate_item_sequence
    :   (iterate_item )+
    ;

iterate_item
    :   statement
    |   iterate_exit_statement
    ;

iterate_exit_statement
    :   ^(WHEN  condition
         statement_sequence) 
    ;     

// Procedure call ------------------------------------------------

procedure_operation_call
@init{
String[] adj = {"",""};
}
    :   ^(FUNCTION {adj = adjustLine($FUNCTION);}qi+=qualified_ident oas+=operation_argument_section[$qi])
        -> template(qi={$qi},oas={$oas},ln={adj[0]},in={adj[1]})"<ln><in><oas>"
    ;

operation_argument_section [List opName]
@init{
String[] adj = {"",""};
String caller = "";
}
    :   ^(PARAMS {adj = adjustLine($PARAMS);} params+=program_expression*)
        {
          if($params != null){
            String name = opName.toString();
            if(!name.contains(".")){
              caller = (StringTemplate)$params.remove($params.size()-1)+".";
            }
          }
        }
        -> template(params={$params},
                    opName={$opName},
                    caller={caller},ln={adj[0]},in={adj[1]})
        "<caller><opName>(<ln><in><params; separator=\", \">);"
    ;

// Selection statement -------------------------------------------

selection_statement
    :   ^(CASE program_expression
         selection_alternative_sequence
        (default_alternative)?)
    ;

selection_alternative_sequence
    :   (selection_alternative)+
    ;

selection_alternative
    :   ^(WHEN choices
         statement_sequence)
    ;

default_alternative
    :   ^(DEFAULT statement_sequence)
    ;

choice
    :   program_expression
    ;

choices
    :   ^(CHOICES choice+)
    ;

// Swap statement ------------------------------------------------

swap_statement
@init{
String[] adj = {"",""};
String newLhs = "";
}
    :   ^(SWAP_OP lhs+=variable_expression rhs=variable_expression)
        {
          newLhs = templatePrepend((StringTemplate)$lhs.get(0), "Swap(");
        }
        -> template(lhs={newLhs},rhs={$rhs.text})"<lhs>, <rhs>);"
    ;
    
// Confirm statement ------------------------------------------------

confirm_statement
    :   ^(CONFIRM math_expression)
    ;
    
// Confirm statement ------------------------------------------------

assume_statement
    :   ^(ASSUME math_expression)
    ;

// While loop ----------------------------------------------------

while_loop_statement
@init{
String[] adj = {"",""};
}
    :   ^(WHILE {adj = adjustLine($WHILE);} c+=condition {incIndent();}
        cc+=changing_clause?    
        mc+=maintaining_clause?
        dc+=decreasing_clause?
        ss+=statement_sequence) {decIndent();incLine();}
         -> template(ln={adj[0]},in={adj[1]},
                     //w={$WHILE.text},
                     c={$c},
                     cc={$cc},
                     mc={$mc},
                     dc={$dc},
                     ss={$ss})
         "<ln><in>while(<c>){<cc><mc><dc><ss><\n><in>}"
    ;

maintaining_clause
@init{
String[] adj = {"",""};
}
    :   ^(MAINTAINING {adj = adjustLine($MAINTAINING);} me=math_expression)
        -> template(ln={adj[0]},in={adj[1]},
                    m={$MAINTAINING.text},
                    me={$me.text})
        "<ln><in>/*@ <m> <me> */"
        
    ;

decreasing_clause
@init{
String[] adj = {"",""};
}
    :   ^(DECREASING {adj = adjustLine($DECREASING);} ae=adding_expression)
        -> template(ln={adj[0]},in={adj[1]},
                    d={$DECREASING.text},
                    ae={$ae.text})
        "<ln><in>/*@ <d> <ae> */" 
    ;

changing_clause
@init{
String[] adj = {"",""};
}
    :   ^(CHANGING {adj = adjustLine($CHANGING);} ve+=variable_expression+)
        -> template(ln={adj[0]},in={adj[1]},
                    c={$CHANGING},
                    ve={$ve})
        "<ln><in>/*@ <c> <ve; separator=\", \"> */"
    ;

// ===============================================================
// Program Type Expression Grammar
// ===============================================================

program_type_expression
    :   (   ^(TYPEX qi+=qualified_ident))
            -> template(qi={$qi})"<qi>"
        |   ^(ARRAY array_range program_type_expression)
    ;

structural_program_type_expression
@init{
String[] adj = {"",""};
}
    :   ^(RECORD {adj = adjustLine($RECORD);incIndent();} 
        (rvdg+=record_variable_declaration_group )+) {decIndent();incLine();}
        -> template(ln={adj[0]},in={adj[1]},
                    rvdg={$rvdg})
        "<ln><in><rvdg><\n><in>"
    |   pte=program_type_expression -> template(pte={$pte.text})"<pte>"
    ;

record_variable_declaration_group
@init{
String[] adj = {"",""};
}
    :   ^(VAR {adj = adjustLine($VAR);} vil+=variable_id_list type+=program_type_expression)
        -> template(ln={adj[0]},in={adj[1]},vil={$vil},type={$type})
        "<ln><in> public <type> <vil>;"
    ;
    
record_aux_variable_declaration_group
    :   ^(AUX_VAR variable_id_list program_type_expression)
    ;

array_range
    :   ^(RANGE program_expression program_expression)
    ;

variable_declaration_group
@init{

}
    :   vil+=variable_id_list  type+=program_type_expression
        -> template(vil={$vil},type={$type})"<type> <vil>"
    ;
    
aux_variable_declaration_group
    :   vil+=variable_id_list  type+=program_type_expression
        -> template(vil={$vil},type={$type})"<type> <vil>"
    ;

variable_id_list
    :   id+=ident+
        -> template(id={$id})"<id; separator=\", \">"
    ;

// ===============================================================
// Math Type Expression Grammar
// ===============================================================

math_type_expression
    :   ^(TYPEX function_type_expression?)
    //|   BOOLEAN
    ;
    
function_type_expression
    :   structural_math_type_expression
    |   ^(FUNCARROW structural_math_type_expression (structural_math_type_expression)*)
    ;
    
structural_math_type_expression
    :   ^(CARTPROD
        (cartprod_variable_declaration_group )+)
    |   product_type_expression
    ;

product_type_expression
    :   ^(TIMES primitive_type_expression primitive_type_expression*)
    |   primitive_type_expression
    ;

primitive_type_expression
    :   (SSET) => SSET
    |   (BOOLEAN) => BOOLEAN
    |   (POWERSET) => powerset_expression
    |   nested_type_expression
    |   ^(FUNCTION qualified_ident type_expression_argument_list)
    |   qualified_ident
    /*|   (qualified_ident -> ^(qualified_ident))
        (   type_expression_argument_list
        -> ^(FUNCTION qualified_ident type_expression_argument_list))?*/
    ;
    
powerset_expression
    :   ^(POWERSET math_type_expression)
    ;

nested_type_expression
    :    type_expression 
    //| math_type_expression // Introduces Mutual left recursion errors
    ;
    
type_expression
    //:   (math_type_expression) => implicit_type_parameter_group
    :   math_type_expression
    ;

type_expression_argument_list
    :   ^(PARAMS math_type_expression math_type_expression*)
    ;

cartprod_variable_declaration_group
    :   ^(VAR math_variable_declaration_group)
    ;

structural_math_variable_declaration_group
    :   variable_id_list  structural_math_type_expression
    ;

math_variable_declaration_group
    :   variable_id_list  math_type_expression
    ; 

math_variable_declaration
    :   ident  math_type_expression
    ;
    
implicit_type_parameter_group
    :   variable_id_list  math_expression
    ;

// ===============================================================
// Resolve Expression/Math_Expression Grammar
// ===============================================================

math_expression
    :   (   ^(EXPR iterated_construct)
        |   ^(EXPR quantified_expression)
        )
    ;

//  expression
//      :   quantified_expression
//      ;

quantified_expression
    :   implies_expression
    |   ^(FORALL math_variable_declaration_group
        where_clause?  quantified_expression)
    |   ^(EXISTS_UNIQUE math_variable_declaration_group
        where_clause? quantified_expression)
    |    ^(EXISTS math_variable_declaration_group
        where_clause? quantified_expression)
    ;

implies_expression
    :   exp1=logical_expression
    |   (   ^(IMPLIES logical_expression logical_expression)
        |   ^(IFF logical_expression logical_expression)
        )
    |   ^(  IF logical_expression logical_expression
            (logical_expression)?
        )
    ;

logical_expression
    :   relational_expression
    |   (   ^(AND logical_expression relational_expression)
        |   ^(OR logical_expression relational_expression)
        )
    ;

relational_expression
    :   infix_expression
    |   between_expression
    |   ^(  (EQL | NOT_EQL) infix_expression infix_expression
        )
    |   ^(  (LT | LT_EQL | GT | GT_EQL |SUBSET | NOT_SUBSET | PROP_SUBSET | NOT_PROP_SUBSET | SUBSTR | NOT_SUBSTR)
            infix_expression infix_expression)
        /*|   ^(LT_EQL infix_expression infix_expression)
        |   ^(GT infix_expression infix_expression)
        |   ^(GT_EQL infix_expression infix_expression)
        //|   ^(id=IN lf=infix_expression rt=infix_expression)   
        //|   ^(id=NOT_IN lf=infix_expression rt=infix_expression)
        |   ^(SUBSET infix_expression infix_expression)
        |   ^(NOT_SUBSET infix_expression infix_expression)
        |   ^(PROP_SUBSET infix_expression infix_expression)
        |   ^(NOT_PROP_SUBSET infix_expression infix_expression)
        |   ^(SUBSTR infix_expression infix_expression)
        |   ^(NOT_SUBSTR infix_expression infix_expression)
        )*/
    |   ^(  (IN | NOT_IN) infix_expression infix_expression
        )
    ;

between_expression
    :   ^(BETWEEN_EXPR infix_expression (infix_expression)+)
        //{ retval.tree.addchild(op); }        
    ;

infix_expression
    :   ^(LOCALVAREXP math_variable_declarations math_expression)
    |
        (adding_expression
    |   (   ^(RANGE adding_expression adding_expression)
        |   ^(FREE_OPERATOR adding_expression adding_expression)
        )
        )
    |   BOOLEAN
    ;

adding_expression
    :   multiplying_expression
    |   ^(  (PLUS | MINUS | CAT | UNION | INTERSECT | WITHOUT)
            adding_expression multiplying_expression
        )
    /*|   (   ^(PLUS adding_expression multiplying_expression)
        |   ^(MINUS adding_expression multiplying_expression)
        |   ^(CAT adding_expression multiplying_expression)
        |   ^(UNION adding_expression multiplying_expression)
        |   ^(INTERSECT adding_expression multiplying_expression)
        |   ^(WITHOUT adding_expression multiplying_expression)
        )*/
    ;

multiplying_expression
    :   exponential_expression
    |   ^(  (MULTIPLY | DIVIDE | MOD | REM | DIV)
            multiplying_expression
            exponential_expression
        )
    ;

exponential_expression
    :   prefix_expression
    |   ^(EXP prefix_expression exponential_expression)
    ;

prefix_expression
    :   unary_expression
    |   ^(UNARY_FREE_OPERATOR prefix_expression)
    ;

unary_expression
    :   primitive_expression
    |   ^(NOT unary_expression)
    |   ^(COMPLEMENT unary_expression)
    |   ^(MINUS  unary_expression)
    ;

primitive_expression
    :   alternative_expression
    |   (ident DOT NUMERIC_LITERAL) => qualified_numeric_literal
    |   dot_expression
    |   lambda_expression
    |   literal_expression
    |   outfix_expression
    |   set_constructor
    |   (LPAREN math_expression COMMA) => tuple_expression
    |   nested_expression
    ;

// ---------------------------------------------------------------
// Articulated expression rules (expression with '.')
// ---------------------------------------------------------------

dot_expression
    :   ^(DOT function_expression clean_function_expression*)
    |   function_expression
    ;

function_expression
    :   ^(HASH clean_function_expression)
    |   clean_function_expression
    ;

clean_function_expression
    :   ^(FUNCTION ident hat_expression? function_argument_list+)
    |   ident
    /*:   (ident -> ^(ident))
        (   (hat_expression)?
            (function_argument_list)+ -> ^(FUNCTION ident hat_expression? function_argument_list+)
        )?*/
        //-> ^(ident)
    ;

hat_expression
    :   ^(CARAT (qualified_ident | nested_expression))
    ;

function_argument_list
    :   ^(PARAMS math_expression+)
    ;

// ---------------------------------------------------------------
// Auxilliary expression rules
// ---------------------------------------------------------------

alternative_expression
    :   
        ^(DBL_LBRACE
        //LBRACE
        //LPAREN^
        (alternative_expression_item)+
        //RBRACE!
        //RPAREN!
        )
    ;

alternative_expression_item
    :   (   ^(IF adding_expression relational_expression)
        |   ^(OTHERWISE adding_expression)
        )
        
    ;

iterated_construct
    :   ^(ITERATION ident ident math_type_expression (where_clause)? math_expression)
    ;

//NOTE: Allows only very rudimentary lambda expressions.
lambda_expression
    :   ^(LAMBDA ident 
        (   (ident DOT ident DOT) => certain_qualified_ident
        |   ident
        )
          math_expression) 
    ;

literal_expression
    //:   (ident DOT) => qualified_numeric_literal
    :   NUMERIC_LITERAL
    |   CHARACTER_LITERAL
    |   STRING_LITERAL
    ;
    
program_literal_expression
    :   NUMERIC_LITERAL -> template(nl={$NUMERIC_LITERAL})"<nl>"
    |   CHARACTER_LITERAL -> template(cl={$CHARACTER_LITERAL})"<cl>"
    |   STRING_LITERAL -> template(sl={$STRING_LITERAL})"<sl>"
    ;
    
qualified_numeric_literal
    :   ^(QUALNUM ident NUMERIC_LITERAL)
    ;

nested_expression
    :   ^(NESTED math_expression)
    ;

outfix_expression
    :   ^(ANGLE infix_expression)
    |   ^(DBL_ANGLE math_expression)
    |   ^(BAR math_expression) 
    |   ^(DBL_BAR math_expression)
    ;

parenthesized_expression
    :    math_expression 
    ;

set_constructor
    :   ^(LBRACE ident
         math_type_expression
        (where_clause)? 
        math_expression) 
    ;

tuple_expression
    :   ^(TUPLE math_expression+)
    ;

where_clause
    :   ^(WHERE math_expression)
    ;

// ===============================================================
// Programming expressions
// ===============================================================

program_expression
@init{
String[] adj = {"",""};
}
    :   ^(EXPR {adj = adjustLine($EXPR);} ple+=program_logical_expression)
        -> template(ple={$ple},ln={adj[0]},in={adj[1]})"<ln><in><ple>"
    ;

program_logical_expression
    :   pre+=program_relational_expression -> template(pre={$pre})"<pre>"
    |   ^(  (AND | OR)
            program_logical_expression
            program_relational_expression
        )
    /*|   (   ^(  AND
                program_logical_expression
                program_relational_expression)
        |   ^(  OR
                program_logical_expression
                program_relational_expression)
        )*/
    ;

program_relational_expression
@init{
String[] adj = {"",""};
}
    :   pae+=program_adding_expression -> template(pae={$pae})"<pae>"
    /*|   (   ^(  EQL
                program_relational_expression
                program_adding_expression)
        |   ^(  NOT_EQL
                program_relational_expression
                program_adding_expression)
        |   ^(  LT
                program_relational_expression
                program_adding_expression)
        |   ^(  LT_EQL
                program_relational_expression
                rt=program_adding_expression)
        |   ^(  GT
                program_relational_expression
                program_adding_expression)
        |   ^(  GT_EQL
                program_relational_expression
                program_adding_expression)
        )*/
    |   ^(  (op=EQL | op=NOT_EQL | op=LT | op=LT_EQL | op=GT | op=GT_EQL)
            {adj = adjustLine($op);}
            pre+=program_relational_expression
            pae+=program_adding_expression
        ) -> template(ln={adj[0]},in={adj[1]},
                      op={$op.text},
                      pre={$pre},
                      pae={$pae})
        "<ln><in><pre> <op> <pae>"
        
    ;

program_adding_expression
@init{
String[] adj = {"",""};
}
    :   pme+=program_multiplying_expression -> template(pme={$pme})"<pme>"
    /*|   (   ^(  PLUS
                program_adding_expression
                program_multiplying_expression)
        |   ^(  MINUS
                program_adding_expression
                program_multiplying_expression)
        )*/
    |   ^(  (op=PLUS | op=MINUS)
            {adj = adjustLine($op);}
            pae+=program_adding_expression
            pme+=program_multiplying_expression
        )
        -> template(ln={adj[0]},in={adj[1]},
                      op={$op.text},
                      pae={$pae},
                      pme={$pme})
        "<ln><in><pae> <op> <pme>"
    ;

program_multiplying_expression
    :   pee+=program_exponential_expression -> template(pee={$pee})"<pee>"
    |   ^(  (MULTIPLY | DIVIDE | MOD | REM | DIV)
            program_multiplying_expression
            program_exponential_expression
        )
    /*|   (   ^(  MULTIPLY 
                program_multiplying_expression
                program_exponential_expression)
        |   ^(  DIVIDE
                program_multiplying_expression
                program_exponential_expression)
        |   ^(  MOD
                program_multiplying_expression
                program_exponential_expression)
        |   ^(  REM
                program_multiplying_expression
                program_exponential_expression)
        |   ^(  DIV
                program_multiplying_expression
                program_exponential_expression)
        )*/
    ;

program_exponential_expression
    :   pue+=program_unary_expression -> template(pue={$pue})"<pue>"
    |   ^(  EXP
            program_unary_expression
            program_exponential_expression)
    ;

program_unary_expression
    :   ppe+=program_primitive_expression -> template(ppe={$ppe})"<ppe>"
    |   ^(NOT pue+=program_unary_expression) -> template(pue={$pue})"-<pue>"
    |   ^(UNARY_MINUS pue+=program_unary_expression) -> template(pue={$pue})"-<pue>"
    ;

program_primitive_expression
    :   ple=program_literal_expression -> template(ple={$ple.text})"<ple>"
    |   pve+=program_variable_expression -> template(pve={$pve})"<pve>"
    |   pne+=program_nested_expression -> template(pne={$pne})"<pne>"
    ;

program_variable_expression
    //:   (ident (DOT ident)* LPAREN) => program_dot_expression
    :   pde+=program_dot_expression -> template(pde={$pde})"<pde>"
    |   ve+=variable_expression -> template(ve={$ve})"<ve>"
    ;

program_dot_expression
@init{
String[] adj = {"",""};
}
    :   ^(PROGDOT {adj = adjustLine($PROGDOT);} id=ident pfe+=program_function_expression+)
        -> template(ln={adj[0]},in={adj[1]},
                    id={$id.text},pfe={$pfe})"<ln><in><id>.<pfe>"
    |   pfe+=program_function_expression -> template(pfe={$pfe})"<pfe>"
    ;

program_function_expression
@init{
String[] adj = {"",""};
}
    :   ^(FUNCTION {adj = adjustLine($FUNCTION);} id+=ident pfal+=program_function_argument_list[$id])
        -> template(ln={adj[0]},in={adj[1]},
                    pfal={$pfal})"<ln><in><pfal>"
    ;

program_function_argument_list [List opName]
@init{
String[] adj = {"",""};
String caller = "";
}
    :   ^(PARAMS params+=program_expression*)
        //{if($params != null){caller = (StringTemplate)$params.remove($params.size()-1)+".";}}
        -> template(params={$params},
                    opName={$opName},
                    caller={caller},ln={adj[0]},in={adj[1]})
        "<caller><opName>(<ln><in><params; separator=\", \">)"
    ;

program_nested_expression
    :   ^(NESTED pe+=program_expression) -> template(pe={$pe})"<pe>"
    ;

// ===============================================================
// Variable Expressions
// ===============================================================

variable_expression
@init{
String[] adj = {"",""};
}
    :   //(variable_array_expression DOT) =>
        //variable_array_expression
        vae+=variable_array_expression -> template(vae={$vae})"<vae>"
    |   ^(VARDOT {adj = adjustLine($VARDOT);} vae+=variable_array_expression+)
        -> template(ln={adj[0]},in={adj[1]},
                    vae={$vae})
        "<ln><in><vae; separator=\".\">"
    ;

variable_array_expression
    :   id+=ident -> template(id={$id})"<id>"
    |   ^(ARRAYFUNCTION id+=ident vaal+=variable_array_argument_list)
        -> template(id={$id},vaal={$vaal})"<id>[<vaal>]"
    ;

variable_array_argument_list
    :   ^(PARAMS pe+=program_expression?)
        -> template(pe={$pe})"<pe>"
    ;

// ===============================================================
// Identifiers
// ===============================================================

certain_qualified_ident
    :   ^(IDENTIFIER ident ident)
    ;

qualified_ident
    :   ^(IDENTIFIER id1+=ident id2+=ident?)
        -> template(id1={$id1},id2={$id2!=null?"."+$id2.get(0):$id2})"<id1><id2>"
    ;

ident //returns [CommonTree token = null]
@init{
String[] adj = {"",""};
}
    :   IDENTIFIER {adj = adjustLine($IDENTIFIER);}//{$token = $IDENTIFIER;}
        //-> {$facility_module::isFacility}? template(id={$IDENTIFIER.text})"class <id>"
        -> {!$IDENTIFIER.getText().equals("Main")}? template(id={$IDENTIFIER.text},ln={adj[0]},in={adj[1]})"<ln><in><id>"
        -> template(ln={adj[0]},in={adj[1]})"<ln><in>main"
    ;

math_theorem_ident
    :   ident
    |   NUMERIC_LITERAL
    ;
    
// =============================================================
// ADDED FOR PARSING MATH PROOF EXPRESSIONS
// =============================================================

proof_module
    :   ^(PROOFS_FOR id1=ident 
        module_formal_param_section?
        uses_list? //(proof_module_body)? END! id2=ident
        )
        
    ;
    
proof_module_body
    :   ^(PROOFBODY math_item_sequence)
    |   ^(PROOFBODY proof)
    ;
    
proof
    :   ^(PROOF 
        math_item_reference
        
        ( (LSQBRACK IDENTIFIER RSQBRACK LPAREN BASECASE) => base_case_statement_head
        | (LPAREN BASECASE) => base_case_statement_body
        | (LSQBRACK IDENTIFIER RSQBRACK LPAREN INDUCTIVECASE) => inductive_case_statement_head
        | (LPAREN INDUCTIVECASE) => inductive_case_statement_body
        | (LSQBRACK IDENTIFIER RSQBRACK) => headed_proof_expression
        | proof_expression )*)
        
    ;
    
base_case_statement_head
    :    ^(IDENTIFIER  base_case_statement_body)
    ;
    
base_case_statement_body
    :    ^(BASECASE  proof_expression)
    ;
    
inductive_case_statement_head
    :    ^(IDENTIFIER  inductive_case_statement_body)
    ;
    
inductive_case_statement_body
    :    ^(INDUCTIVECASE  proof_expression)
    ;

math_item_reference
    :
    (   ^(MATHITEMREF theorem_name)
    |   ^(MATHITEMREF lemma_name)
    |   ^(MATHITEMREF property_name)
    |   ^(MATHITEMREF corollary_name)
    )
    ;
    
theorem_name
    :   ^(THEOREM ident)
    ;
    
lemma_name
    :   ^(LEMMA ident)
    ;

property_name
    :   ^(PROPERTY ident)
    ;
    
corollary_name
    :   ^(COROLLARY math_theorem_ident)
    ;
    
proof_expression_list
    :   ^(PROOFEXPRLIST
    ( (IDENTIFIER) => headed_proof_expression
    | proof_expression
    )* )
    ;
    
headed_proof_expression
    :    ^(IDENTIFIER proof_expression)
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
    :   ^(GOAL math_expression) 
    ;
    
supposition_deduction_pair
    :   ^(SUPDEDUC supposition_declaration proof_expression_list IDENTIFIER? deduction_declaration)
    ;
    
supposition_declaration
    :   ^(SUPPOSITION
        (
          ((ident COLON) => (math_variable_declarations ( math_expression)?))
          | ((ident COMMA ident) => (math_variable_declarations ( math_expression)?))
          //| (math_expression ((AND! math_variable_declarations)?))
        ))
    ;
    
math_variable_declarations
    :   ^(DECLARATIONS math_variable_declaration_group+)
    ;
    
deduction_declaration
    :   ^(DEDUCTION math_expression)
    ;
    
justification_declaration
    :   ^(SIMPLIFICATION math_expression justification)
    ;
    
justification
    :   ^(BY
        ( (hyp_desig COMMA) => double_hyp_rule_justification
      | (hyp_desig AMPERSAND) => single_hyp_rule_justification
      | (hyp_desig) => hyp_desig
      | simple_justification
      | (DEFINITION) => def_justification )
      //| (LPAREN) => def_justification)  //XXX : Maybe an 'or' marker to combine these two?
      )
    ;

double_hyp_rule_justification
    :   hyp_desig  hyp_desig
      ( rules_set_1)?
    ;

single_hyp_rule_justification
    :   hyp_desig  (rules_set_1 | rules_set_2 | def_justification)
    ;

def_justification
    :   
    ( DEFINITION ((UNIQUE) => (UNIQUE) | (fn_name))
    | ^(INDEXED_DEFINITION ident fn_name ident?)
    )
    ;

simple_justification
    :   rules_set_2 | rules_set_3
    ;

rules_set_1
    :
    ( ^(RULE1 MODUS_PONENS)
      | ^(RULE1 AND_RULE)
      | ^(RULE1 CONTRADICTION)
      | ^(RULE1 EQUALITY)
      | ^(RULE1 ALTERNATIVE_ELIMINATION)
      | ^(RULE1 COMMON_CONCLUSION)
      )
    ;

rules_set_2
    :   
    ( ^(RULE2 REDUCTIO_AD_ABSURDUM)
      | ^(RULE2 UNIVERSAL_GENERALIZATION)
      | ^(RULE2 UNIVERSAL_INSTANTIATION)
      | ^(RULE2 EXISTENTIAL_GENERALIZATION)
      | ^(RULE2 EXISTENTIAL_INSTANTIATION)
      | ^(RULE2 OR_RULE)
      | ^(RULE2 CONJUNCT_ELIMINATION)
      | ^(RULE2 QUANTIFIER_DISTRIBUTION)
      )
    ;

rules_set_3
    :   ^(RULE3 EXCLUDED_MIDDLE)
    ;

hyp_desig
    :   
    ( ^(HYPDESIG SELF)
    | ^(HYPDESIG lemma_call)
    | ^(HYPDESIG theorem_call)
    | ^(HYPDESIG corollary_name)
    | ^(HYPDESIG supposition_call)
    | ^(HYPDESIG definition_call)
    | ^(HYPDESIG reference_marker_call)
    )
    ;

lemma_call
    :   ^(LEMMA ident)
    ;

theorem_call
    :   ^(THEOREM ident)
    ;

supposition_call
    :   SUPPOSITION
    ;

definition_call
    : ^(DEFINITION (LPAREN ident)? 
      fn_name ( qualified_ident ( ident) )?
      ( ident)?)
    ;
    
reference_marker_call
    :   ^(REFCALL ident)
    ;

fn_name
    :   infix_symbol | prefix_symbol | ident
    ;
    
