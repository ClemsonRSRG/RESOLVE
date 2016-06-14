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
        (conceptImplItems)?
        END closename=IDENTIFIER SEMICOLON EOF
    ;

conceptImplItems
    :   (conceptImplItem)+
    ;

conceptImplItem
    :   implItem
    |   sharedStateRepresentationDecl
    |   typeRepresentationDecl
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
    :   constraintClause
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
    :   performanceOperationDecl
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

programNamedType
    :   (qualifier=IDENTIFIER QUALIFIER)? name=IDENTIFIER
    ;

programArrayType
    :   ARRAY progExp RANGE progExp OF programNamedType
    ;

programRecordType
    :   RECORD (variableDeclGroup SEMICOLON)+ END
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
    :   TYPE name=IDENTIFIER (EQL | IS REPRESENTED BY) (programNamedType|programArrayType|programRecordType) SEMICOLON
        (conventionClause)?
        (correspondenceClause)?
        (representationInit)?
        (representationFinal)?
        END SEMICOLON
    ;

facilityTypeRepresentationDecl
    :   TYPE name=IDENTIFIER (EQL | IS REPRESENTED BY) (programNamedType|programArrayType|programRecordType) SEMICOLON
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
    :   SHAREDSTATE name=IDENTIFIER
        (moduleStateVariableDecl)+
        (constraintClause)?
        (specModelInit)?
        (specModelFinal)?
        END SEMICOLON
    ;

sharedStateRepresentationDecl
    :   SHAREDSTATE name=IDENTIFIER IS REALIZED BY
        (variableDecl)+
        (conventionClause)?
        (correspondenceClause)?
        (representationInit)?
        (representationFinal)?
        END SEMICOLON
    ;

facilitySharedStateRepresentationDecl
    :   SHAREDSTATE name=IDENTIFIER IS REALIZED BY
        (variableDecl)+
        (conventionClause)?
        (facilityRepresentationInit)?
        (facilityRepresentationFinal)?
        END SEMICOLON
    ;

// initialization, finalization rules

specModelInit
    :   INITIALIZATION
        (affectsClause)?
        (ensuresClause)?
    ;

specModelFinal
    :   FINALIZATION
        (affectsClause)?
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
        operationParameterList (COLON programNamedType)? SEMICOLON
        (affectsClause)?
        (facilityDecl)*
        (variableDecl)*
        (auxVariableDecl)*
        (stmt)*
        END closename=IDENTIFIER SEMICOLON
    ;

recursiveProcedureDecl
    :   RECURSIVE PROCEDURE name=IDENTIFIER
        operationParameterList (COLON programNamedType)? SEMICOLON
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
    :   OPERATION name=IDENTIFIER operationParameterList (COLON programNamedType)? SEMICOLON
        (affectsClause)?
        (requiresClause)?
        (ensuresClause)?
    ;

performanceOperationDecl
    :   OPERATION name=IDENTIFIER operationParameterList (COLON programNamedType)? SEMICOLON
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
        REALIZED BY impl=IDENTIFIER
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
    :   IDENTIFIER (COMMA IDENTIFIER)* COLON (programNamedType|programArrayType)
    ;

variableDecl
    :   VAR variableDeclGroup SEMICOLON
    ;

auxVariableDeclGroup
    :   IDENTIFIER (COMMA IDENTIFIER)* COLON programNamedType
    ;

auxVariableDecl
    :   AUX_VAR auxVariableDeclGroup SEMICOLON
    ;

// state variable declaration

moduleStateVariableDecl
    :   ABSTRACT_VAR mathVariableDeclGroup SEMICOLON
    ;

// statements

stmt
    :   assignStmt
    |   swapStmt
    |   callStmt
    |	presumeStmt
    |   confirmStmt
    |   memoryStmt
    |   ifStmt
    |   whileStmt
    ;

assignStmt
    :   left=progVariableExp ASSIGN_OP right=progExp SEMICOLON
    ;

swapStmt
    :   left=progVariableExp SWAP_OP right=progVariableExp SEMICOLON
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

memoryStmt
    :   (REMEMBER | FORGET) SEMICOLON
    ;

ifStmt
    :   IF progExp THEN (stmt)* (elsePart)? END SEMICOLON
    ;

elsePart
    :   ELSE stmt*
    ;

whileStmt
    :   WHILE progExp (changingClause)?
        (maintainingClause)? (decreasingClause) DO stmt* END SEMICOLON
    ;

// mathematical type theorems

mathTypeTheoremDecl
    :   TYPE THEOREM name=IDENTIFIER COLON
        (FORALL mathVariableDeclGroup COMMA)+ mathImpliesExp SEMICOLON
    ;

// mathematical theorems, corollaries, etc

mathAssertionDecl
    :   assertionType=(AXIOM | COROLLARY | LEMMA | PROPERTY |
            THEOREM | THEOREM_ASSOCIATIVE | THEOREM_COMMUTATIVE)
        name=mathTheoremIdent
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
        (IDENTIFIER | infixOp)
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
    :   op=(PLUS | MINUS | NOT | ABS | COMPLEMENT)
    ;

infixOp
    :   op=(IMPLIES | PLUS | CONCAT | MINUS | DIVIDE | MULTIPLY | EXP | MOD | REM | DIV |
         IMPLIES | IFF | RANGE | AND | OR | UNION | INTERSECT | IN | NOT_IN | GT | LT |
         GT_EQL | LT_EQL | EQL | NOT_EQL)
    ;

definitionParameterList
    :   LPAREN mathVariableDeclGroup (COMMA mathVariableDeclGroup)* RPAREN
    ;

// mathematical clauses

affectsClause
    :   AFFECTS mathVarNameExp (COMMA mathVarNameExp)* SEMICOLON
    ;

requiresClause
    :   REQUIRES mathExp (WHICH_ENTAILS mathExp)? SEMICOLON
    ;

ensuresClause
    :   ENSURES mathExp (WHICH_ENTAILS mathExp)? SEMICOLON
    ;

constraintClause
    :   CONSTRAINT mathExp (WHICH_ENTAILS mathExp)? SEMICOLON
    ;

changingClause
    :   CHANGING progVarNameExp (COMMA progVarNameExp)* SEMICOLON
    ;

maintainingClause
    :   MAINTAINING mathExp (WHICH_ENTAILS mathExp)? SEMICOLON
    ;

decreasingClause
    :   DECREASING mathAddingExp (WHICH_ENTAILS mathExp)? SEMICOLON
    ;

correspondenceClause
    :   CORR mathExp (WHICH_ENTAILS mathExp)? SEMICOLON
    ;

conventionClause
    :   CONVENTION mathExp (WHICH_ENTAILS mathExp)? SEMICOLON
    ;

durationClause
    :   DURATION mathAddingExp (WHICH_ENTAILS mathExp)? SEMICOLON
    ;

manipulationDispClause
    :   MAINP_DISP mathAddingExp (WHICH_ENTAILS mathExp)? SEMICOLON
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
        mathVariableDecl
        (mathWhereExp)?
        (COMMA | OF) LBRACE mathExp RBRACE
    ;

mathQuantifiedExp
    :   mathImpliesExp
    |   FORALL mathVariableDeclGroup (mathWhereExp)? (SUCHTHAT | COMMA)
        mathQuantifiedExp
    |   EXISTS_UNIQUE mathVariableDeclGroup (mathWhereExp)? (SUCHTHAT | COMMA)
        mathQuantifiedExp
    |   EXISTS mathVariableDeclGroup (mathWhereExp)? (SUCHTHAT | COMMA)
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
    :   mathAddingExp (FUNCARROW mathAddingExp)?
    ;

mathAddingExp
    :   mathMultiplyingExp
        (qualifier=IDENTIFIER QUALIFIER)? (op=(PLUS | MINUS | CONCAT | UNION | INTERSECT | WITHOUT | TILDE)
         mathMultiplyingExp)*
    ;

mathMultiplyingExp
    :   mathExponentialExp
        (qualifier=IDENTIFIER QUALIFIER)? (op=(MULTIPLY | DIVIDE | MOD | REM | DIV)
         mathExponentialExp)*
    ;

mathExponentialExp
    :   mathPrefixExp (EXP mathExponentialExp)?
    ;

mathPrefixExp
    :   (qualifier=IDENTIFIER QUALIFIER)? prefixOp mathPrimaryExp
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
    :   BOOLEAN_LITERAL                                     #mathBooleanExp
    |   (qualifier=IDENTIFIER QUALIFIER)? INTEGER_LITERAL   #mathIntegerExp
    |   REAL_LITERAL                                        #mathRealExp
    |   CHARACTER_LITERAL                                   #mathCharacterExp
    |   STRING_LITERAL                                      #mathStringExp
    ;

mathDotExp
    :   mathFunctionApplicationExp (DOT mathCleanFunctionExp)+
    |   mathFunctionApplicationExp
    ;

mathFunctionApplicationExp
    :   HASH mathCleanFunctionExp   #mathOldExp
    |   mathCleanFunctionExp        #mathFunctOrVarExp
    ;

mathCleanFunctionExp
    :   (qualifier=IDENTIFIER QUALIFIER)?
        name=IDENTIFIER (CARAT mathNestedExp)?
        LPAREN mathExp (COMMA mathExp)* RPAREN                  #mathFunctionExp
    |   mathVarNameExp                                          #mathVarExp
    |   OP (qualifier=IDENTIFIER QUALIFIER)?
        (infixOp | op=NOT | op=ABS | op=COMPLEMENT)             #mathOpNameExp
    ;

mathVarNameExp
    :   (qualifier=IDENTIFIER QUALIFIER)? name=IDENTIFIER
    ;

mathOutfixExp
    :   lop=LT mathInfixExp rop=GT
    |   lop=LL mathExp rop=GG
    |   lop=BAR mathExp rop=BAR
    |   lop=DBL_BAR mathExp rop=DBL_BAR
    ;

mathSetExp
    :   LBRACE mathVariableDecl (mathWhereExp)? BAR mathExp RBRACE       #mathSetBuilderExp
    |   LBRACE (mathExp (COMMA mathExp)*)? RBRACE                       #mathSetCollectionExp
    ;

mathTupleExp
    :   LPAREN mathExp COMMA mathExp RPAREN
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

mathWhereExp
    :   WHERE mathExp
    ;

// program expressions

progExp
    :   progExp
        op=(AND|OR|EQL|NOT_EQL|LT_EQL|GT_EQL|GT|LT|
        PLUS|MINUS|MULTIPLY|DIVIDE|MOD|REM|DIV)
        progExp                                     #progApplicationExp
    |   progExponential                             #progExponentialExp
    ;

progExponential
    :   progUnary (EXP progExponential)?
    ;

progUnary
    :   op=(NOT|MINUS) progExp                      #progUnaryExp
    |   progPrimary                                 #progPrimaryExp
    ;

progPrimary
    :   progLiteral             #progLiteralExp
    |   progParamExp            #progFunctionExp
    |   progVariableExp         #progVarExp
    |   LPAREN progExp RPAREN   #progNestedExp
    ;

// Real numbers are currently not supported. Should add this
// to the grammar when we do.
progLiteral
    :   INTEGER_LITERAL      #progIntegerExp
    |   CHARACTER_LITERAL    #progCharacterExp
    |   STRING_LITERAL       #progStringExp
    ;

progParamExp
    :   (qualifier=IDENTIFIER QUALIFIER)? name=IDENTIFIER
        LPAREN (progExp (COMMA progExp)*)? RPAREN
    ;

// This intermediate rule is really only needed to help make
// 'swapStmt' and 'assignStmt' rules a little more strict about
// what they accept.
progVariableExp
    :   progVarDotExp
    |   progVarDotArrayExp
    |   progVarArrayExp
    |   progVarNameExp
    ;

// This is a progamming dotted expression that contains only
// program variable names.
progVarDotExp
    :   progVarNameExp (DOT progVarNameExp)+
    ;

// This is a programming dotted expression that contains all
// program variable names except the last expression, which is a
// program array expression.
// Note: Arrays are simply syntactic sugar. We should convert these
// to the corresponding calls when building the RESOLVE AST.
progVarDotArrayExp
    :   progVarNameExp (DOT progVarNameExp)* (DOT progVarArrayExp)
    ;

// Note: Arrays are simply syntactic sugar. We should convert these
// to the corresponding calls when building the RESOLVE AST.
progVarArrayExp
    :   progVarNameExp LSQBRACK progExp RSQBRACK
    ;

progVarNameExp
    :   (qualifier=IDENTIFIER QUALIFIER)? name=IDENTIFIER
    ;