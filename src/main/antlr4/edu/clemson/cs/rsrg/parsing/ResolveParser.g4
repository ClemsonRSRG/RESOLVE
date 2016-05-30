parser grammar ResolveParser;

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
    |   mathAssertionDecl
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
    |   facilitySharedStateRepresentationDecl
    |   facilityTypeRepresentationDecl
    |   recursiveOperationProcedureDecl
    |   operationProcedureDecl
    |   recursiveProcedureDecl
    |   mathDefinitionDecl
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
    |   operationDecl
    |   sharedStateDecl
    |   typeModelDecl
    |   mathDefinitionDecl
    |   mathDefinesDecl
    ;

// concept impl module

conceptImplModule
    :   REALIZATION name=IDENTIFIER
        (WITH_PROFILE profile=IDENTIFIER)?
        FOR concept=IDENTIFIER
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
    |   typeModelDecl
    |   mathDefinitionDecl
    |   mathDefinesDecl
    ;

// enhancement impl module

enhancementImplModule
    :   REALIZATION name=IDENTIFIER (moduleParameterList)?
        (WITH_PROFILE profile=IDENTIFIER)?
        FOR enhancement=IDENTIFIER
        OF concept=IDENTIFIER
        (ENHANCED BY cEnhancement=IDENTIFIER (moduleParameterList)?
         REALIZED BY cRealization=IDENTIFIER (WITH_PROFILE cProfile=IDENTIFIER)? (moduleParameterList)?)* SEMICOLON
        (usesList)?
        (requiresClause)?
        (implItems)?
        END closename=IDENTIFIER SEMICOLON
    ;

implItems
    :   (implItem)+
    ;

implItem
    :   facilityDecl
    |   recursiveOperationProcedureDecl
    |   operationProcedureDecl
    |   procedureDecl
    |   recursiveProcedureDecl
    |   mathDefinitionDecl
    |   sharedStateRepresentationDecl
    |   typeRepresentationDecl
    ;

// concept performance module

conceptPerformanceModule
    :   PROFILE name=IDENTIFIER (moduleParameterList)?
        SHORT_FOR fullName=IDENTIFIER FOR concept=IDENTIFIER SEMICOLON
        (usesList)?
        (requiresClause)?
        (conceptPerformanceItems)?
        END closename=IDENTIFIER SEMICOLON EOF
    ;

conceptPerformanceItems
    :   (conceptPerformanceItem)+
    ;

conceptPerformanceItem
    :   confirmMathTypeDecl
    |   constraintClause
    |   performanceOperationDecl
    |   performanceTypeModelDecl
    |   mathDefinitionDecl
    |   mathDefinesDecl
    ;

// enhancement performance module

enhancementPerformanceModule
    :   PROFILE name=IDENTIFIER (moduleParameterList)?
        SHORT_FOR fullName=IDENTIFIER FOR enhancement=IDENTIFIER
        WITH_PROFILE conceptProfile=IDENTIFIER SEMICOLON
        (usesList)?
        (requiresClause)?
        (enhancementPerformanceItems)?
        END closename=IDENTIFIER SEMICOLON EOF
    ;

enhancementPerformanceItems
    :   (enhancementPerformanceItem)+
    ;

enhancementPerformanceItem
    :   confirmMathTypeDecl
    |   performanceOperationDecl
    |   performanceTypeModelDecl
    |   mathDefinitionDecl
    |   mathDefinesDecl
    ;

// uses, imports

usesList
    :   USES usesItem (COMMA usesItem)* SEMICOLON
    ;

usesItem
    :   IDENTIFIER
    ;

// parameter related rules

operationParameterList
    :   LPAREN (parameterDecl (SEMICOLON parameterDecl)*)? RPAREN
    ;

moduleParameterList
    :   LPAREN moduleParameterDecl (SEMICOLON moduleParameterDecl)* RPAREN
    ;

moduleParameterDecl
    :   definitionParameterDecl
    |   typeParameterDecl
    |   constantParameterDecl
    |   operationParameterDecl
    |   conceptImplParameterDecl
    ;

definitionParameterDecl
    :   DEFINITION definitionSignature
    ;

typeParameterDecl
    :   TYPE name=IDENTIFIER
    ;

constantParameterDecl
    :   EVALUATES variableDeclGroup
    ;

operationParameterDecl
    :   operationDecl
    ;

conceptImplParameterDecl
    :   REALIZATION name=IDENTIFIER
        FOR (CONCEPT)? concept=IDENTIFIER
    ;

parameterDecl
    :   parameterMode variableDeclGroup
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
        (specModelInit)?
        (specModelFinal)?
        END SEMICOLON
    ;

typeRepresentationDecl
    :   TYPE name=IDENTIFIER (EQL | IS REPRESENTED BY) (record|type) SEMICOLON
        (conventionClause)?
        (correspondenceClause)?
        (representationInit)?
        (representationFinal)?
        END SEMICOLON
    ;

facilityTypeRepresentationDecl
    :   TYPE name=IDENTIFIER (EQL | IS REPRESENTED BY) (record|type) SEMICOLON
        (conventionClause)?
        (facilityRepresentationInit)?
        (facilityRepresentationFinal)?
        END SEMICOLON
    ;

performanceTypeModelDecl
    :   TYPE FAMILY IS MODELED BY mathTypeExp SEMICOLON
        (constraintClause)?
        (performanceSpecModelInit)?
        (performanceSpecModelFinal)?
        END SEMICOLON
    ;

// shared state rules

sharedStateDecl
    :   SHARED STATE name=IDENTIFIER
        (moduleStateVariableDecl)+
        (constraintClause)?
        (specModelInit)?
        (specModelFinal)?
        END SEMICOLON
    ;

sharedStateRepresentationDecl
    :   SHARED STATE name=IDENTIFIER IS REALIZED BY
        (stateVariableDecl)+
        (conventionClause)?
        (correspondenceClause)?
        (representationInit)?
        (representationFinal)?
        END SEMICOLON
    ;

facilitySharedStateRepresentationDecl
    :   SHARED STATE name=IDENTIFIER IS REALIZED BY
        (stateVariableDecl)+
        (conventionClause)?
        (facilityRepresentationInit)?
        (facilityRepresentationFinal)?
        END SEMICOLON
    ;

// initialization, finalization rules

specModelInit
    :   INITIALIZATION
        (affectsClause)?
        (requiresClause)?
        (ensuresClause)?
    ;

specModelFinal
    :   FINALIZATION
        (affectsClause)?
        (requiresClause)?
        (ensuresClause)?
    ;

representationInit
    :   INITIALIZATION
        (affectsClause)?
        (facilityDecl)*
        (variableDecl)*
        (auxVariableDecl)*
        (stmt)*
        END SEMICOLON
    ;

representationFinal
    :   FINALIZATION
        (affectsClause)?
        (facilityDecl)*
        (variableDecl)*
        (auxVariableDecl)*
        (stmt)*
        END SEMICOLON
    ;

//We use special rules for facility module init and final to allow requires
//and ensures clauses (which aren't allowed in normal impl modules)...
facilityRepresentationInit
    :   INITIALIZATION
        (affectsClause)?
        (requiresClause)?
        (ensuresClause)?
        (facilityDecl)*
        (variableDecl)*
        (auxVariableDecl)*
        (stmt)*
        END SEMICOLON
    ;

facilityRepresentationFinal
    :   FINALIZATION
        (affectsClause)?
        (requiresClause)?
        (ensuresClause)?
        (facilityDecl)*
        (variableDecl)*
        (auxVariableDecl)*
        (stmt)*
        END SEMICOLON
    ;

performanceSpecModelInit
    :   INITIALIZATION
        (durationClause)?
        (manipulationDispClause)?
    ;

performanceSpecModelFinal
    :   FINALIZATION
        (durationClause)?
        (manipulationDispClause)?
    ;

// functions

procedureDecl
    :   PROCEDURE name=IDENTIFIER
        operationParameterList (COLON type)? SEMICOLON
        (affectsClause)?
        (facilityDecl)*
        (variableDecl)*
        (auxVariableDecl)*
        (stmt)*
        END closename=IDENTIFIER SEMICOLON
    ;

recursiveProcedureDecl
    :   RECURSIVE PROCEDURE name=IDENTIFIER
        operationParameterList (COLON type)? SEMICOLON
        (affectsClause)?
        decreasingClause
        (facilityDecl)*
        (variableDecl)*
        (auxVariableDecl)*
        (stmt)*
        END closename=IDENTIFIER SEMICOLON
    ;

operationProcedureDecl
    :   OPERATION
        name=IDENTIFIER operationParameterList SEMICOLON
        (affectsClause)?
        (requiresClause)?
        (ensuresClause)?
        PROCEDURE
        (facilityDecl)*
        (variableDecl)*
        (auxVariableDecl)*
        (stmt)*
        END closename=IDENTIFIER SEMICOLON
    ;

recursiveOperationProcedureDecl
    :   OPERATION
        name=IDENTIFIER operationParameterList SEMICOLON
        (affectsClause)?
        (requiresClause)?
        (ensuresClause)?
        RECURSIVE PROCEDURE
        decreasingClause
        (facilityDecl)*
        (variableDecl)*
        (auxVariableDecl)*
        (stmt)*
        END closename=IDENTIFIER SEMICOLON
    ;

operationDecl
    :   OPERATION name=IDENTIFIER operationParameterList (COLON type)? SEMICOLON
        (affectsClause)?
        (requiresClause)?
        (ensuresClause)?
    ;

performanceOperationDecl
    :   OPERATION name=IDENTIFIER operationParameterList (COLON type)? SEMICOLON
        (affectsClause)?
        (requiresClause)?
        (ensuresClause)?
        (durationClause)?
        (manipulationDispClause)?
    ;

// facility and enhancements

facilityDecl
    :   FACILITY name=IDENTIFIER IS concept=IDENTIFIER
        (specArgs=moduleArgumentList)?
        (conceptEnhancementDecl)*
        (externally=EXTERNALLY)? REALIZED
        BY impl=IDENTIFIER (WITH_PROFILE profile=IDENTIFIER)? (implArgs=moduleArgumentList)?
        (enhancementPairDecl)* SEMICOLON
    ;

conceptEnhancementDecl
    :   ENHANCED BY spec=IDENTIFIER (specArgs=moduleArgumentList)?
    ;

enhancementPairDecl
    :   ENHANCED BY spec=IDENTIFIER (specArgs=moduleArgumentList)?
        (externally=EXTERNALLY)? REALIZED BY impl=IDENTIFIER
        (WITH_PROFILE profile=IDENTIFIER)?
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
    :   IDENTIFIER (COMMA IDENTIFIER)* COLON type
    ;

variableDecl
    :   VAR variableDeclGroup SEMICOLON
    ;

auxVariableDeclGroup
    :   IDENTIFIER (COMMA IDENTIFIER)* COLON type
    ;

auxVariableDecl
    :   AUX_VAR auxVariableDeclGroup SEMICOLON
    ;

// state variable declaration

moduleStateVariableDecl
    :   ABSTRACT_VAR mathVariableDeclGroup SEMICOLON
    ;

stateVariableDecl
    :   VAR variableDeclGroup SEMICOLON
    ;

// statements

stmt
    :   assignStmt
    |   swapStmt
    |   callStmt
    |	presumeStmt
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

presumeStmt
    :	PRESUME	mathExp SEMICOLON
    ;

confirmStmt
    :   CONFIRM mathExp SEMICOLON
    ;

ifStmt
    :   IF progExp THEN (stmt)* (elsePart)? END SEMICOLON
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
        (FOR ALL mathVariableDeclGroup COMMA)+ mathImpliesExp SEMICOLON
    ;

// mathematical theorems, corollaries, etc

mathAssertionDecl
    :   (AXIOM | COROLLARY | LEMMA | PROPERTY | THEOREM ) name=mathTheoremIdent
        COLON mathExp SEMICOLON
    ;

mathTheoremIdent
    :   IDENTIFIER
    |   INTEGER_LITERAL
    |   REAL_LITERAL
    ;

// mathematical definitions

mathDefinesDecl
    :   DEFINES definitionSignature SEMICOLON
    ;

mathDefinitionDecl
    :   mathImplicitDefinitionDecl
    |   mathStandardDefinitionDecl
    |   mathInductiveDefinitionDecl
    |   mathCategoricalDecl
    ;

mathCategoricalDecl
    :   CATEGORICAL DEFINITION name=IDENTIFIER INTRODUCES categoricalDefinitionSignature
        RELATED BY mathExp SEMICOLON
    ;

mathImplicitDefinitionDecl
    :   IMPLICIT DEFINITION definitionSignature
        IS mathExp SEMICOLON
    ;

mathInductiveDefinitionDecl
    :   INDUCTIVE DEFINITION definitionSignature
        IS INDUCTIVE_BASE_NUM mathExp SEMICOLON
        INDUCTIVE_HYP_NUM mathExp SEMICOLON
    ;

mathStandardDefinitionDecl
    :   DEFINITION definitionSignature (EQL mathExp)? SEMICOLON
    ;

categoricalDefinitionSignature
    :   definitionSignature (COMMA definitionSignature)*
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
    |     lOp=LT LPAREN mathVariableDecl RPAREN rOp=GT
    |     lOp=LL LPAREN mathVariableDecl RPAREN rOp=GG) COLON mathTypeExp
    ;

standardPrefixSignature
    :   (IDENTIFIER | prefixOp | INTEGER_LITERAL | REAL_LITERAL)
        (definitionParameterList)? COLON mathTypeExp
    ;

prefixOp
    :   (PLUS | MINUS | NOT | ABS | COMPLEMENT)
    ;

infixOp
    :   (IMPLIES | PLUS | CONCAT | MINUS | DIVIDE | MULTIPLY | EXP | MOD | REM | DIV |
         IMPLIES | IFF | RANGE | AND | OR | UNION | INTERSECT | IN | NOT_IN | GT | LT |
         GT_EQL | LT_EQL | EQL | NOT_EQL)
    ;

definitionParameterList
    :   LPAREN mathVariableDeclGroup (COMMA mathVariableDeclGroup)* RPAREN
    ;

// mathematical clauses

affectsClause
    :   AFFECTS progNamedExp (COMMA progNamedExp)* SEMICOLON
    ;

requiresClause
    :   REQUIRES mathExp SEMICOLON
    ;

ensuresClause
    :   ENSURES mathExp SEMICOLON
    ;

constraintClause
    :   CONSTRAINT mathExp SEMICOLON
    ;

changingClause
    :   CHANGING progVariableExp (COMMA progVariableExp)*
    ;

maintainingClause
    :   MAINTAINING mathExp SEMICOLON
    ;

decreasingClause
    :   DECREASING mathAddingExp SEMICOLON
    ;

whereClause
    :   WHERE mathExp
    ;

correspondenceClause
    :   CORR mathExp SEMICOLON
    ;

conventionClause
    :   CONVENTION mathExp SEMICOLON
    ;

durationClause
    :   DURATION mathAddingExp SEMICOLON
    ;

manipulationDispClause
    :   MAINP_DISP mathAddingExp SEMICOLON
    ;

// mathematical expressions

mathTypeExp
    :   mathInfixExp
    ;

mathExp
    :   mathIteratedExp
    |   mathQuantifiedExp
    ;

mathIteratedExp
    :   op=(BIG_CONCAT | BIG_INTERSECT | BIG_PRODUCT | BIG_SUM | BIG_UNION)
        IDENTIFIER COLON mathTypeExp
        (whereClause)?
        (COMMA | OF) LBRACE mathExp RBRACE
    ;

mathQuantifiedExp
    :   mathImpliesExp
    |   FOR ALL mathVariableDeclGroup (whereClause)? COMMA
        mathQuantifiedExp
    |   THERE EXISTS UNIQUE mathVariableDeclGroup (whereClause)? (SUCH THAT | COMMA)
        mathQuantifiedExp
    |   THERE EXISTS mathVariableDeclGroup (whereClause)? (SUCH THAT | COMMA)
        mathQuantifiedExp
    ;

mathImpliesExp
    :   mathLogicalExp (op=(IMPLIES | IFF) mathLogicalExp)?
    |   IF mathLogicalExp
        THEN mathLogicalExp
        (ELSE mathLogicalExp)?
    ;

mathLogicalExp
    :   mathRelationalExp (op=(AND | OR) mathRelationalExp)*
    ;

mathRelationalExp
    :   mathInfixExp
        (op1=(LT | LT_EQL))
        mathInfixExp
        (op2=(LT | LT_EQL))
        mathInfixExp
    |   mathInfixExp
        (op=(EQL | NOT_EQL | LT | LT_EQL | GT | GT_EQL | IN | NOT_IN |
             SUBSET | NOT_SUBSET | PROP_SUBSET | NOT_PROP_SUBSET | SUBSTR | NOT_SUBSTR)
         mathInfixExp)?
    ;

mathInfixExp
    :   mathTypeAssertionExp RANGE mathTypeAssertionExp
    |   mathTypeAssertionExp
    ;

mathTypeAssertionExp
    :   mathFunctionTypeExp (COLON mathTypeExp)?
    ;

mathFunctionTypeExp
    :   mathAddingExp (FUNCARROW mathAddingExp)*
    ;

mathAddingExp
    :   mathMultiplyingExp
        (op=(PLUS | MINUS | CONCAT | UNION | INTERSECT | WITHOUT | TILDE)
         mathMultiplyingExp)*
    ;

mathMultiplyingExp
    :   mathExponentialExp
        (op=(MULTIPLY | DIVIDE | MOD | REM | DIV)
         mathExponentialExp)*
    ;

mathExponentialExp
    :   mathPrefixExp (EXP mathExponentialExp)?
    ;

mathPrefixExp
    :   prefixOp mathPrimaryExp
    |   mathPrimaryExp
    ;

mathPrimaryExp
    :   mathAlternativeExp
    |   mathIteratedExp
    |   mathLiteralExp
    |   mathDotExp
    |   mathFunctionApplicationExp
    |   mathOutfixExp
    |   mathSetExp
    |   mathTupleExp
    |   mathLambdaExp
    |   mathTaggedCartProdTypeExp
    |   mathNestedExp
    ;

mathAlternativeExp
    :   DBL_LBRACE (mathAlternativeExpItem)+ DBL_RBRACE
    ;

mathAlternativeExpItem
    :   mathAddingExp
        (IF mathRelationalExp | OTHERWISE)
        SEMICOLON
    ;

mathLiteralExp
    :   BOOLEAN_LITERAL         #mathBooleanExp
    |   INTEGER_LITERAL         #mathIntegerExp
    |   REAL_LITERAL            #mathRealExp
    |   CHARACTER_LITERAL       #mathCharacterExp
    |   STRING_LITERAL          #mathStringExp
    ;

mathDotExp
    :   mathFunctionApplicationExp (DOT mathCleanFunctionExp)+
    |   mathFunctionApplicationExp
    ;

mathFunctionApplicationExp
    :   HASH mathCleanFunctionExp
    |   mathCleanFunctionExp
    ;

mathCleanFunctionExp
    :   name=IDENTIFIER (CARAT mathNestedExp)? LPAREN mathExp (COMMA mathExp)* RPAREN
    |   name=IDENTIFIER
    |   OP (infixOp | NOT | ABS | COMPLEMENT)
    ;


mathOutfixExp
    :   lop=LT mathInfixExp rop=GT
    |   lop=LL mathExp rop=GT
    |   lop=BAR mathExp rop=BAR
    |   lop=DBL_BAR mathExp rop=DBL_BAR
    ;

mathSetExp
    :   LBRACE mathVariableDecl BAR mathExp RBRACE          #mathSetBuilderExp
    |   LBRACE (mathExp (COMMA mathExp)*)? RBRACE           #mathSetCollectionExp
    ;

mathTupleExp
    :   LPAREN mathExp (COMMA mathExp)+ RPAREN
    ;

//NOTE: Allows only very rudimentary lambda expressions.

mathLambdaExp
    :   LAMBDA LPAREN mathVariableDeclGroup (COMMA mathVariableDeclGroup)* RPAREN
        DOT LPAREN mathExp RPAREN
    ;

mathTaggedCartProdTypeExp
    :   CARTPROD (mathVariableDeclGroup SEMICOLON)+ END
    ;

mathNestedExp
    :   LPAREN mathExp RPAREN
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
