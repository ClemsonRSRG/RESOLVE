grammar Resolve;

options {
    tokenVocab=ResolveLexer;
}

module
    :   precisModule
    |   facilityModule
    |   shortFacilityModule
    |   conceptModule
    |   enhancementModule
    |   enhancementImplModule
    |   conceptImplModule
    |   conceptPerformanceModule
    |   enhancementPerformanceModule
    ;

// precis module

precisModule
    :   PRECIS name=IDENTIFIER SEMICOLON
        (usesList)?
        (precisItems)?
        END closename=IDENTIFIER SEMICOLON
    ;

precisItems
    :   (precisItem)+
    ;

precisItem
    :   mathTypeTheoremDecl
    |   mathDefinitionDecl
    |   mathTheoremDecl
    ;

// facility module

facilityModule
    :   FACILITY name=IDENTIFIER SEMICOLON
        (usesList)?
        (facilityItems)?
        END closename=IDENTIFIER SEMICOLON EOF
    ;

facilityItems
    :   (facilityItem)+
    ;

facilityItem
    :   facilityDecl
    |   operationProcedureDecl
    |   mathDefinitionDecl
    |   moduleFacilityInit
    |   moduleFacilityFinal
    ;

// short facility module

shortFacilityModule
    :   facilityDecl EOF
    ;

// concept module

conceptModule
    :   CONCEPT name=IDENTIFIER (moduleParameterList)? SEMICOLON
        (usesList)?
        (requiresClause)?
        (conceptItems)?
        END closename=IDENTIFIER SEMICOLON EOF
    ;

conceptItems
    :   (conceptItem)+
    ;

conceptItem
    :   constraintClause
    |   moduleSpecInit
    |   moduleSpecFinal
    |   operationDecl
    |   typeModelDecl
    |   mathDefinitionDecl
    ;

// concept impl module

conceptImplModule
    :   REALIZATION name=IDENTIFIER FOR concept=IDENTIFIER
        (ENHANCED BY enhancement=IDENTIFIER)* SEMICOLON
        (usesList)?
        (requiresClause)?
        (implItems)?
        END closename=IDENTIFIER SEMICOLON EOF
    ;

// enhancement module

enhancementModule
    :   ENHANCEMENT name=IDENTIFIER (moduleParameterList)?
        FOR concept=IDENTIFIER SEMICOLON
        (usesList)?
        (requiresClause)?
        (enhancementItems)?
        END closename=IDENTIFIER SEMICOLON EOF
    ;

enhancementItems
    :   (enhancementItem)+
    ;

enhancementItem
    :   operationDecl
    |   mathDefinitionDecl
    |   typeModelDecl
    ;

// enhancement impl module

enhancementImplModule
    :   REALIZATION name=IDENTIFIER (moduleParameterList)?
        FOR enhancement=IDENTIFIER OF concept=IDENTIFIER SEMICOLON
        (usesList)?
        (requiresClause)?
        (implItems)?
        END closename=IDENTIFIER SEMICOLON
    ;

implItems
    :   (implItem)+
    ;

implItem
    :   operationProcedureDecl
    |   facilityDecl
    |   procedureDecl
    |   mathDefinitionDecl
    |   typeRepresentationDecl
    |   conventionClause
    |   correspondenceClause
    |   moduleImplInit
    |   moduleImplFinal
    ;

// concept performance module

conceptPerformanceModule
    :   PROFILE name=IDENTIFIER (moduleParameterList)?
        SHORT_FOR fullName=IDENTIFIER FOR concept=IDENTIFIER SEMICOLON
        (usesList)?
        (requiresClause)?
        END closename=IDENTIFIER SEMICOLON EOF
    ;

// enhancement performance module

enhancementPerformanceModule
    :   PROFILE name=IDENTIFIER (moduleParameterList)?
        SHORT_FOR fullName=IDENTIFIER FOR enhancement=IDENTIFIER
        WITH_PROFILE conceptProfile=IDENTIFIER SEMICOLON
        (usesList)?
        (requiresClause)?
        END closename=IDENTIFIER SEMICOLON EOF
    ;

// uses, imports

usesList
    :   USES IDENTIFIER (COMMA IDENTIFIER)* SEMICOLON
    ;

// parameter related rules

operationParameterList
    :   LPAREN (parameterDecl (SEMICOLON parameterDecl)*)? RPAREN
    ;

moduleParameterList
    :   LPAREN moduleParameterDecl (SEMICOLON moduleParameterDecl)* RPAREN
    ;

moduleParameterDecl
    :   typeParameterDecl
    |   parameterDecl
    ;

typeParameterDecl
    :   TYPE name=IDENTIFIER
    ;

parameterDecl
    :   parameterMode name=IDENTIFIER COLON type
    ;

parameterMode
    :   ( ALTERS
        | UPDATES
        | CLEARS
        | RESTORES
        | PRESERVES
        | REPLACES
        | EVALUATES )
    ;

// type and record related rules

type
    :   (qualifier=IDENTIFIER QUALIFIER)? name=IDENTIFIER
    ;

record
    :   RECORD (recordVariableDeclGroup)+ END
    ;

recordVariableDeclGroup
    :   IDENTIFIER (COMMA IDENTIFIER)* COLON type SEMICOLON
    ;

typeModelDecl
    :   TYPE FAMILY name=IDENTIFIER IS MODELED BY mathTypeExp SEMICOLON
        EXEMPLAR exemplar=IDENTIFIER SEMICOLON
        (constraintClause)?
        (typeModelInit)?
        (typeModelFinal)?
    ;

typeRepresentationDecl
    :   TYPE name=IDENTIFIER (EQL | IS REPRESENTED BY) (record|type) SEMICOLON
        (conventionClause)?
        (correspondenceClause)?
        (typeRepresentationInit)?
        (typeRepresentationFinal)?
    ;

// initialization, finalization rules

typeModelInit
    :   INITIALIZATION (requiresClause)? (ensuresClause)?
    ;

typeModelFinal
    :   FINALIZATION (requiresClause)? (ensuresClause)?
    ;

typeRepresentationInit
    :   INITIALIZATION (variableDeclGroup)* //stmts
    ;

typeRepresentationFinal
    :   FINALIZATION (variableDeclGroup)* //stmts
    ;

//We use special rules for facility module init and final to allow requires
//and ensures clauses (which aren't allowed in normal impl modules)...
moduleFacilityInit
    :   FAC_INIT
        (requiresClause)?
        (ensuresClause)?
        (variableDeclGroup)*
        //stmt block
    ;

moduleFacilityFinal
    :   FAC_FINAL
         (requiresClause)?
         (ensuresClause)?
         (variableDeclGroup)*
         //stmt block
    ;

moduleSpecInit
    :   INITIALIZATION
        (requiresClause)?
        (ensuresClause)?
    ;

moduleSpecFinal
    :   FAC_FINAL
        (requiresClause)?
        (ensuresClause)?
    ;

moduleImplInit
    :   FAC_INIT
        (variableDeclGroup)*
        //Todo: stmts
    ;

moduleImplFinal
    :   FAC_FINAL
        (variableDeclGroup)*
        //Todo: stmts
    ;

// functions

procedureDecl
    :   (recursive=RECURSIVE)? PROCEDURE name=IDENTIFIER
        operationParameterList (COLON type)? SEMICOLON
        (variableDeclGroup)*
        (stmt)*
        END closename=IDENTIFIER SEMICOLON
    ;

operationProcedureDecl
    :   (recursive=RECURSIVE)? OPERATION
        name=IDENTIFIER operationParameterList SEMICOLON
        (requiresClause)?
        (ensuresClause)?
        PROCEDURE
        (variableDeclGroup)*
        (stmt)*
        END closename=IDENTIFIER SEMICOLON
    ;

operationDecl
    :   OPERATION name=IDENTIFIER operationParameterList (COLON type)? SEMICOLON
            (requiresClause)?
            (ensuresClause)?
    ;

// facility and enhancements

//Todo: This also needs enhancements realizable by the base concept.
facilityDecl
    :   FACILITY name=IDENTIFIER IS concept=IDENTIFIER
        (specArgs=moduleArgumentList)? (externally=EXTERNALLY)? REALIZED
        BY impl=IDENTIFIER (implArgs=moduleArgumentList)?
        (enhancementPairDecl)* SEMICOLON
    ;

enhancementPairDecl
    :   ENHANCED BY spec=IDENTIFIER (specArgs=moduleArgumentList)?
        (externally=EXTERNALLY)? REALIZED BY impl=IDENTIFIER
        (implArgs=moduleArgumentList)?
    ;

moduleArgumentList
    :   LPAREN moduleArgument (COMMA moduleArgument)* RPAREN
    ;

moduleArgument
    :   progExp
    ;

// variable declarations

mathVariableDeclGroup
    :   IDENTIFIER (COMMA IDENTIFIER)* COLON mathTypeExp
    ;

mathVariableDecl
    :   IDENTIFIER COLON mathTypeExp
    ;

variableDeclGroup
    :   VAR IDENTIFIER (COMMA IDENTIFIER)* COLON type SEMICOLON
    ;

// statements

stmt
    :   assignStmt
    |   swapStmt
    |   callStmt
    |   confirmStmt
    |   ifStmt
    |   whileStmt
    ;

assignStmt
    :   left=progExp ASSIGN_OP right=progExp SEMICOLON
    ;

swapStmt
    :   left=progExp SWAP_OP right=progExp SEMICOLON
    ;

callStmt
    :   progParamExp SEMICOLON
    ;

confirmStmt
    :   CONFIRM mathAssertionExp SEMICOLON
    ;

ifStmt
    :   IF progExp THEN (stmt)*  (elsePart)? END SEMICOLON
    ;

elsePart
    :   ELSE stmt*
    ;

whileStmt
    :   WHILE progExp (changingClause)?
        (maintainingClause)? (decreasingClause)? DO stmt* END SEMICOLON
    ;

// mathematical type theorems

mathTypeTheoremDecl
    :   TYPE THEOREM name=IDENTIFIER COLON
        (FOR ALL mathVariableDeclGroup COMMA)+ mathExp SEMICOLON
    ;

// mathematical theorems, corollaries, etc

mathTheoremDecl
    :   (THEOREM | LEMMA | COROLLARY) name=IDENTIFIER
        COLON mathAssertionExp SEMICOLON
    ;

// mathematical definitions

mathDefinitionDecl
    :   mathStandardDefinitionDecl
    |   mathInductiveDefinitionDecl
    |   mathCategoricalDecl
    ;

mathCategoricalDecl
    :   CATEGORICAL DEFINITION INTRODUCES categoricalDefinitionSignature
        RELATED BY mathAssertionExp SEMICOLON
    ;

mathInductiveDefinitionDecl
    :   INDUCTIVE DEFINITION inductiveDefinitionSignature
        IS INDUCTIVE_BASE_NUM mathAssertionExp SEMICOLON
        INDUCTIVE_HYP_NUM mathAssertionExp SEMICOLON
    ;

mathStandardDefinitionDecl
    :   DEFINITION definitionSignature (IS mathAssertionExp)? SEMICOLON
    ;

categoricalDefinitionSignature
    :   definitionSignature (COMMA definitionSignature)*
    ;

inductiveDefinitionSignature
    :   inductivePrefixSignature
    |   inductiveInfixSignature
    ;

inductivePrefixSignature
    :   ON mathVariableDecl OF prefixOp
        LPAREN (inductiveParameterList COMMA)? IDENTIFIER RPAREN COLON mathTypeExp
    ;

inductiveInfixSignature
    :   ON mathVariableDecl OF LPAREN mathVariableDecl RPAREN infixOp
        LPAREN IDENTIFIER RPAREN COLON mathTypeExp
    ;

inductiveParameterList
    :   mathVariableDeclGroup (COMMA mathVariableDeclGroup)*
    ;

definitionSignature
    :   standardInfixSignature
    |   standardOutfixSignature
    |   standardPrefixSignature
    ;

standardInfixSignature
    :   LPAREN mathVariableDecl RPAREN
        infixOp
        LPAREN mathVariableDecl RPAREN COLON mathTypeExp
    ;

standardOutfixSignature
    :   ( lOp=BAR LPAREN mathVariableDecl RPAREN rOp=BAR
    |     lOp=DBL_BAR LPAREN mathVariableDecl RPAREN rOp=DBL_BAR
    |     lOp=LT LPAREN mathVariableDecl RPAREN rOp=GT) COLON mathTypeExp
    ;

standardPrefixSignature
    :   prefixOp (definitionParameterList)? COLON mathTypeExp
    ;

prefixOp
    :   infixOp
    |   INTEGER_LITERAL
    ;

infixOp
    :   (IMPLIES | PLUS | CONCAT | MINUS | DIVIDE | MULTIPLY | RANGE | AND | OR)
    |   (UNION | INTERSECT | IN | NOT_IN | GT | LT | GT_EQL | LT_EQL)
    |   IDENTIFIER
    ;

definitionParameterList
    :   LPAREN mathVariableDeclGroup (COMMA mathVariableDeclGroup)* RPAREN
    ;

// mathematical clauses

affectsClause
    :   parameterMode IDENTIFIER (COMMA IDENTIFIER)*
    ;

requiresClause
    :   REQUIRES mathAssertionExp SEMICOLON
    ;

ensuresClause
    :   ENSURES mathAssertionExp SEMICOLON
    ;

constraintClause
    :   CONSTRAINT mathAssertionExp SEMICOLON
    ;

changingClause
    :   CHANGING progVariableExp (COMMA progVariableExp)*
    ;

maintainingClause
    :   MAINTAINING mathAssertionExp SEMICOLON
    ;

decreasingClause
    :   DECREASING mathAssertionExp SEMICOLON
    ;

whereClause
    :   WHERE mathAssertionExp
    ;

correspondenceClause
    :   CORR mathAssertionExp SEMICOLON
    ;

conventionClause
    :   CONVENTION mathAssertionExp SEMICOLON
    ;

// mathematical expressions

mathTypeExp
    :   mathExp
    ;

mathAssertionExp
    :   mathExp
    |   mathQuantifiedExp
    ;

mathQuantifiedExp
    :   FOR ALL mathVariableDeclGroup (whereClause)? COMMA
         mathAssertionExp
    ;

mathExp
    :   mathPrimaryExp                                  #mathPrimeExp
    |   op=(PLUS|MINUS|TILDE|NOT) mathExp               #mathUnaryExp
    |   mathExp op=(MULTIPLY|DIVIDE|TILDE) mathExp      #mathInfixExp
    |   mathExp op=(PLUS|MINUS) mathExp                 #mathInfixExp
    |   mathExp op=(RANGE|FUNCARROW) mathExp            #mathInfixExp
    |   mathExp op=(CONCAT|UNION|INTERSECT) mathExp     #mathInfixExp
    |   mathExp op=(IN|NOT_IN) mathExp                  #mathInfixExp
    |   mathExp op=(LT_EQL|GT_EQL|GT|LT) mathExp        #mathInfixExp
    |   mathExp op=(EQL|NOT_EQL) mathExp                #mathInfixExp
    |   mathExp op=IMPLIES mathExp                      #mathInfixExp
    |   mathExp op=(AND|OR) mathExp                     #mathInfixExp
    |   mathExp (COLON) mathExp                         #mathTypeAssertExp
    |   LPAREN mathAssertionExp RPAREN                  #mathNestedExp
    ;

mathPrimaryExp
    :   mathLiteralExp
    |   mathDotExp
    |   mathFunctionApplicationExp
    |   mathOutfixExp
    |   mathSetExp
    |   mathTupleExp
    |   mathLambdaExp
    ;

mathLiteralExp
    :   BOOLEAN_LITERAL      #mathBooleanExp
    |   INTEGER_LITERAL      #mathIntegerExp
    ;

mathDotExp
    :   mathFunctionApplicationExp (DOT mathFunctionApplicationExp)+
    ;

mathFunctionApplicationExp
    :   HASH mathCleanFunctionExp
    |   mathCleanFunctionExp
    ;

mathCleanFunctionExp
    :   name=IDENTIFIER LPAREN mathExp (COMMA mathExp)* RPAREN  #mathFunctionExp
    |   (qualifier=IDENTIFIER QUALIFIER)? name=IDENTIFIER       #mathVariableExp
    |   (PLUS|MINUS|MULTIPLY|DIVIDE)                            #mathOpExp
    ;

mathOutfixExp
    :   lop=LT mathExp rop=GT
    |   lop=BAR mathExp rop=BAR
    |   lop=DBL_BAR mathExp rop=DBL_BAR
    ;

mathSetExp
    :   LBRACE mathVariableDecl BAR mathAssertionExp RBRACE #mathSetBuilderExp//Todo
    |   LBRACE (mathExp (COMMA mathExp)*)? RBRACE           #mathSetCollectionExp
    ;

mathTupleExp
    :   LPAREN mathExp (COMMA mathExp)+ RPAREN
    ;

//NOTE: Allows only very rudimentary lambda expressions.

mathLambdaExp
    :   LAMBDA LPAREN mathVariableDeclGroup (COMMA mathVariableDeclGroup)* RPAREN
        DOT LPAREN mathAssertionExp RPAREN
    ;

// program expressions

progExp
    :   op=(NOT|MINUS) progExp                      #progApplicationExp
    |   progExp op=(MULTIPLY|DIVIDE) progExp        #progApplicationExp
    |   progExp op=(PLUS|MINUS) progExp             #progApplicationExp
    |   progExp op=(LT_EQL|GT_EQL|GT|LT) progExp    #progApplicationExp
    |   progExp op=(EQL|NOT_EQL) progExp            #progApplicationExp
    |   LPAREN progExp RPAREN                       #progNestedExp
    |   progPrimary                                 #progPrimaryExp
    ;

progPrimary
    :   progLiteralExp
    |   progVariableExp
    |   progParamExp
    ;

//This intermediate rule is really only needed to help make
//the 'changingClause' rule a little more strict about what it accepts.
//A root VariableExp class is no longer reflected in the ast.
progVariableExp
    :   progDotExp
    |   progNamedExp
    ;

progDotExp
    :   progNamedExp (DOT progNamedExp)+
    ;

progParamExp
    :   (qualifier=IDENTIFIER QUALIFIER)? name=IDENTIFIER
        LPAREN (progExp (COMMA progExp)*)? RPAREN
    ;

progNamedExp
    :   (qualifier=IDENTIFIER QUALIFIER)? name=IDENTIFIER
    ;

progLiteralExp
    :   INTEGER_LITERAL      #progIntegerExp
    |   CHARACTER_LITERAL    #progCharacterExp
    |   STRING_LITERAL       #progStringExp
    ;