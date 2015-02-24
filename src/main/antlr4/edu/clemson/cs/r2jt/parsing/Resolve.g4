grammar Resolve;

module
    :   precisModule
    |   facilityModule
    |   conceptModule
    |   enhancementModule
    |   conceptImplModule
    ;

// precis module

precisModule
    :   'Precis' name=Identifier ';'
        (usesList)?
        (precisItems)?
        'end' closename=Identifier ';'
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
    :   'Facility' name=Identifier ';'
        (usesList)?
        (facilityItems)?
        'end' closename=Identifier ';'
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

// concept module

conceptModule
    :   'Concept' name=Identifier (moduleParameterList)? ';'
        (usesList)?
        (requiresClause)?
        (conceptItems)?
        'end' closename=Identifier ';' EOF
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
    :   'Realization' name=Identifier 'for' concept=Identifier
        ('enhanced' 'by' enhancement=Identifier)* ';'
        (usesList)?
        (requiresClause)?
        (implItems)?
        'end' closename=Identifier ';'
    ;

// enhancement module

enhancementModule
    :   'Enhancement' name=Identifier (moduleParameterList)?
        'for' concept=Identifier ';'
        (usesList)?
        (requiresClause)?
        (enhancementItems)?
        'end' closename=Identifier ';' EOF
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
    :   'Realization' name=Identifier (moduleParameterList)?
        'for' enhancement=Identifier 'of' concept=Identifier ';'
        (usesList)?
        (requiresClause)?
        (implItems)?
        'end' closename=Identifier ';'
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

// uses, imports

usesList
    :   'uses' Identifier (',' Identifier)* ';'
    ;

// parameter related rules

operationParameterList
    :   '(' (parameterDecl (';' parameterDecl)*)?  ')'
    ;

moduleParameterList
    :   '(' moduleParameterDecl (';' moduleParameterDecl)* ')'
    ;

moduleParameterDecl
    :   typeParameterDecl
    |   parameterDecl
    ;

typeParameterDecl
    :   'type' name=Identifier
    ;

parameterDecl
    :   parameterMode name=Identifier ':' type
    ;

parameterMode
    :   ( 'alters'
        | 'updates'
        | 'clears'
        | 'restores'
        | 'preserves'
        | 'replaces'
        | 'evaluates' )
    ;

// type and record related rules

type
    :   (qualifier=Identifier '::')? name=Identifier
    ;

record
    :   'Record' (recordVariableDeclGroup)+ 'end'
    ;

recordVariableDeclGroup
    :   Identifier (',' Identifier)* ':' type ';'
    ;

typeModelDecl
    :   'Type' 'Family' name=Identifier 'is' 'modeled' 'by' mathTypeExp ';'
        'exemplar' exemplar=Identifier ';'
        (constraintClause)?
        (typeModelInit)?
        (typeModelFinal)?
    ;

typeRepresentationDecl
    :   'Type' name=Identifier ('='|'is' 'represented' 'by') (record|type) ';'
        (conventionClause)?
        (correspondenceClause)?
        (typeRepresentationInit)?
        (typeRepresentationFinal)?
    ;

// initialization, finalization rules

typeModelInit
    :   'initialization' (requiresClause)? (ensuresClause)?
    ;

typeModelFinal
    :   'finalization' (requiresClause)? (ensuresClause)?
    ;

typeRepresentationInit
    :   'initialization' (variableDeclGroup)* //stmts
    ;

typeRepresentationFinal
    :   'finalization' (variableDeclGroup)* //stmts
    ;

//We use special rules for facility module init and final to allow requires
//and ensures clauses (which aren't allowed in normal impl modules)...
moduleFacilityInit
    :   'Facility_Initialization'
        (requiresClause)?
        (ensuresClause)?
        (variableDeclGroup)*
        //stmt block
    ;

moduleFacilityFinal
    :   'Facility_Finalization'
         (requiresClause)?
         (ensuresClause)?
         (variableDeclGroup)*
         //stmt block
    ;

moduleSpecInit
    :   'Facility_Initialization'
        (requiresClause)?
        (ensuresClause)?
    ;

moduleSpecFinal
    :   'Facility_Finalization'
        (requiresClause)?
        (ensuresClause)?
    ;

moduleImplInit
    :   'Facility_Initialization'
        (variableDeclGroup)*
        //Todo: stmts
    ;

moduleImplFinal
    :   'Facility_Finalization'
        (variableDeclGroup)*
        //Todo: stmts
    ;

// functions

procedureDecl
    :   (recursive='Recursive')? 'Procedure' name=Identifier
        operationParameterList (':' type)? ';'
        (variableDeclGroup)*
        //Todo: Stmts
        'end' closename=Identifier ';'
    ;

operationProcedureDecl
    :   (recursive='Recursive')? 'Operation'
        name=Identifier operationParameterList ';'
        (requiresClause)?
        (ensuresClause)?
        'Procedure'
        (variableDeclGroup)*
        //Todo: Stmts
        'end' closename=Identifier ';'
    ;

operationDecl
    :   'Operation' name=Identifier operationParameterList (':' type)? ';'
            (requiresClause)?
            (ensuresClause)?
    ;

// facility and enhancements

//Todo: This also needs enhancements realizable by the base concept.
facilityDecl
    :   'Facility' name=Identifier 'is' concept=Identifier
        (specArgs=moduleArgumentList)? (externally='externally')? 'realized'
        'by' impl=Identifier (implArgs=moduleArgumentList)?
        (enhancementPairDecl)* ';'
    ;

enhancementPairDecl
    :   'enhanced' 'by' spec=Identifier (specArgs=moduleArgumentList)?
        (externally='externally')? 'realized' 'by' impl=Identifier
        (implArgs=moduleArgumentList)?
    ;

moduleArgumentList
    :   '(' moduleArgument (',' moduleArgument)* ')'
    ;

moduleArgument
    :   progExp
    ;

// variable declarations

mathVariableDeclGroup
    :   Identifier (',' Identifier)* ':' mathTypeExp
    ;

mathVariableDecl
    :   Identifier ':' mathTypeExp
    ;

variableDeclGroup
    :   'Var' Identifier (',' Identifier)* ':' type ';'
    ;

// mathematical type theorems

mathTypeTheoremDecl
    :   'Type' 'Theorem' name=Identifier ':'
        ('For' 'all' mathVariableDeclGroup ',')+ mathExp ';'
    ;

// mathematical theorems, corollaries, etc

mathTheoremDecl
    :   ('Theorem'|'Lemma'|'Corollary') name=Identifier
        ':' mathAssertionExp ';'
    ;

// mathematical definitions

mathDefinitionDecl
    :   mathStandardDefinitionDecl
    |   mathInductiveDefinitionDecl
    ;

mathInductiveDefinitionDecl
    :   'Inductive' 'Definition' inductiveDefinitionSignature
        'is' '(i.)' mathAssertionExp ';' '(ii.)' mathAssertionExp ';'
    ;

mathStandardDefinitionDecl
    :   'Definition' definitionSignature ('is' mathAssertionExp)? ';'
    ;

inductiveDefinitionSignature
    :   inductivePrefixSignature
    |   inductiveInfixSignature
    ;

inductivePrefixSignature
    :   'on' mathVariableDecl 'of' prefixOp
        '(' (inductiveParameterList ',')? Identifier ')' ':' mathTypeExp
    ;

inductiveInfixSignature
    :   'on' mathVariableDecl 'of' '(' mathVariableDecl ')' infixOp
        '(' Identifier ')' ':' mathTypeExp
    ;

inductiveParameterList
    :   mathVariableDeclGroup (',' mathVariableDeclGroup)*
    ;

definitionSignature
    :   standardInfixSignature
    |   standardOutfixSignature
    |   standardPrefixSignature
    ;

standardInfixSignature
    :   '(' mathVariableDecl ')'
        infixOp
        '(' mathVariableDecl ')' ':' mathTypeExp
    ;

standardOutfixSignature
    :   ( lOp='|'  '(' mathVariableDecl ')' rOp='|'
    |     lOp='||' '(' mathVariableDecl ')' rOp='||'
    |     lOp='<'  '(' mathVariableDecl ')' rOp='>') ':' mathTypeExp
    ;

standardPrefixSignature
    :   prefixOp (definitionParameterList)? ':' mathTypeExp
    ;

prefixOp
    :   infixOp
    |   IntegerLiteral
    ;

infixOp
    :   ('implies'|'+'|'o'|'-'|'/'|'*'|'..'|'and'|'or')
    |   ('union'|'intersect'|'is_in'|'is_not_in'|'>'|'<'|'>='|'<=')
    |   Identifier
    ;

definitionParameterList
    :   '(' mathVariableDeclGroup (',' mathVariableDeclGroup)* ')'
    ;

// mathematical clauses

affectsClause
    :   parameterMode Identifier (',' Identifier)*
    ;

requiresClause
    :   'requires' mathAssertionExp ';'
    ;

ensuresClause
    :   'ensures' mathAssertionExp ';'
    ;

decreasingClause
    :   'decreasing' mathAssertionExp ';'
    ;

constraintClause
    :   ('constraint'|'Constraint') mathAssertionExp ';'
    ;

whereClause
    :   'where' mathAssertionExp
    ;

correspondenceClause
    :   'correspondence' mathAssertionExp ';'
    ;

conventionClause
    :   'convention' mathAssertionExp ';'
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
    :   'For' 'all' mathVariableDeclGroup (whereClause)? ','
         mathAssertionExp
    ;

mathExp
    :   mathPrimaryExp                                  #mathPrimeExp
    |   op=('+'|'-'|'~'|'not') mathExp                  #mathUnaryExp
    |   mathExp op=('*'|'/'|'~') mathExp                #mathInfixExp
    |   mathExp op=('+'|'-') mathExp                    #mathInfixExp
    |   mathExp op=('..'|'->') mathExp                  #mathInfixExp
    |   mathExp op=('o'|'union'|'intersect') mathExp    #mathInfixExp
    |   mathExp op=('is_in'|'is_not_in') mathExp        #mathInfixExp
    |   mathExp op=('<='|'>='|'>'|'<') mathExp          #mathInfixExp
    |   mathExp op=('='|'/=') mathExp                   #mathInfixExp
    |   mathExp op='implies' mathExp                    #mathInfixExp
    |   mathExp op=('and'|'or') mathExp                 #mathInfixExp
    |   mathExp (':') mathExp                           #mathTypeAssertExp
    |   '(' mathAssertionExp ')'                        #mathNestedExp
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
    :   BooleanLiteral      #mathBooleanExp
    |   IntegerLiteral      #mathIntegerExp
    ;

mathDotExp
    :   mathFunctionApplicationExp ('.' mathFunctionApplicationExp)+
    ;

mathFunctionApplicationExp
    :   '#' mathCleanFunctionExp
    |   mathCleanFunctionExp
    ;

mathCleanFunctionExp
    :   name=Identifier '(' mathExp (',' mathExp)* ')'  #mathFunctionExp
    |   (qualifier=Identifier '::')? name=Identifier    #mathVariableExp
    |   ('+'|'-'|'*'|'/')                               #mathOpExp
    ;

mathOutfixExp
    :   lop='<' mathExp rop='>'
    |   lop='|' mathExp rop='|'
    |   lop='||' mathExp rop='||'
    ;

mathSetExp
    :   '{' mathVariableDecl '|' mathAssertionExp '}'   #mathSetBuilderExp//Todo
    |   '{' (mathExp (',' mathExp)*)? '}'               #mathSetCollectionExp
    ;

mathTupleExp
    :   '(' mathExp (',' mathExp)+ ')'
    ;

//NOTE: Allows only very rudimentary lambda expressions.

mathLambdaExp
    :   'lambda' '(' mathVariableDeclGroup (',' mathVariableDeclGroup)* ')'
        '.' '(' mathAssertionExp ')'
    ;

// program expressions

progExp
    :   op=('not'|'-') progExp              #progApplicationExp
    |   progExp op=('*'|'/') progExp        #progApplicationExp
    |   progExp op=('+'|'-') progExp        #progApplicationExp
    |   '(' progExp ')'                     #progNestedExp
    |   progPrimary                         #progPrimaryExp
    ;

progPrimary
    :   progLiteralExp
    |   progParamExp
    |   progNamedExp
    |   progDotExp
    ;

progDotExp
    :   progNamedExp ('.' progNamedExp)+
    ;

progParamExp
    :   (qualifier=Identifier '::')? name=Identifier
        '(' (progExp (',' progExp)*)? ')'
    ;

progNamedExp
    :   (qualifier=Identifier '::')? name=Identifier
    ;

progLiteralExp
    :   IntegerLiteral      #progIntegerExp
    |   CharacterLiteral    #progCharacterExp
    |   StringLiteral       #progStringExp
    ;

// literal rules and fragments

BooleanLiteral
    :   'true'
    |   'false'
    |   'B'
    ;

IntegerLiteral
    :   DecimalIntegerLiteral
    ;

CharacterLiteral
    :   '\'' SingleCharacter '\''
    ;

StringLiteral
    :   '\"' StringCharacters? '\"'
    ;

fragment
StringCharacters
    :   StringCharacter+
    ;

fragment
StringCharacter
    :   ~["\\]
    ;

fragment
DecimalIntegerLiteral
    :   '0'
    |   NonZeroDigit (Digits)?
    ;

fragment
Digits
    :   Digit (Digit)*
    ;

fragment
Digit
    :   '0'
    |   NonZeroDigit
    ;

fragment
NonZeroDigit
    :   [1-9]
    ;

fragment
SingleCharacter
    :   ~['\\]
    ;

// whitespace, identifier rules, and comments

Identifier
    :   Letter LetterOrDigit*
    ;

Letter
    :   [a-zA-Z$_]
    ;

LetterOrDigit
    :   [a-zA-Z0-9$_]
    ;

SPACE
    :  [ \t\r\n\u000C]+ -> skip
    ;

COMMENT
    :   '(*' .*? '*)' -> skip
    ;

LINE_COMMENT
    :   '--' ~[\r\n]* -> skip
    ;