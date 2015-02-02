/*
 * [The "BSD license"]
 * Copyright (c) 2014 Clemson University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
grammar Resolve;

module
    :   conceptModule
    ;

// concept module

conceptModule
    :   'Concept' name=Identifier (moduleParameterList)? ';'
        (usesList)?
        (requiresClause)?
        (conceptItems)?
        'end' closename=Identifier ';'
    ;

conceptItems
    :   (conceptItem)+
    ;

conceptItem
    :   constraintClause
    |   operationDecl
    |   typeModelDecl
    ;

// uses, imports

usesList
    :   'uses' Identifier (',' Identifier)* ';'
    ;

// parameter related rules

moduleParameterList
    : '(' moduleParameterDecl (';' moduleParameterDecl)* ')'
    ;

moduleParameterDecl
    :   typeParameterDecl
    |   parameterDecl
    ;

typeParameterDecl
    : 'type' name=Identifier
    ;

parameterList
    :   parameterDecl (',' parameterDecl)*
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

// type related rules

type
    : (qualifier=Identifier '::')? name=Identifier
    ;

typeModelDecl
    :   'Type' 'Family' name=Identifier 'is' 'modeled' 'by' mathTypeExp ';'
        'exemplar' exemplar=Identifier ';'
        (constraintClause)?
        (specTypeInit)?
        (specTypeFinal)?
    ;

specTypeInit
    :   'initialization' (requiresClause)? (ensuresClause)?
    ;

specTypeFinal
    :   'finalization' (requiresClause)? (ensuresClause)?
    ;

moduleInit
    :   'facility' 'initialization'
    ;

moduleFinal
    :   'facility' 'finalization'
    ;

// functions

procedureDecl
    :   (recursive='Recursive')? 'Procedure' name=Identifier (':' type)
        operationParameterList ';'
        (variableDeclGroup)*

        'end' closename=Identifier
    ;

facilityOperationDecl
    :   (recursive='Recursive')? name=Identifier operationParameterList ';'
        (requiresClause)?
        (ensuresClause)?
        'Procedure'
        (variableDeclGroup)*

        'end' closename=Identifier ';'
    ;

operationDecl
    :   'Operation' name=Identifier operationParameterList (':' type)? ';'
            (requiresClause)?
            (ensuresClause)?
    ;

operationParameterList
    :   '(' (parameterDecl (';' parameterDecl)*)? ')'
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

// Mathematical clauses

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
    :   'constraint' mathAssertionExp ';'
    ;

whereClause
    :   'where' mathAssertionExp
    ;

// mathematical expressions

mathTypeExp
    :   mathExp
    ;

mathAssertionExp
    :   mathExp
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
    |   mathFunctionApplicationExp
    |   mathOutfixExp
    |   mathTupleExp
    ;

mathLiteralExp
    :   BooleanLiteral      #mathBooleanExp
    |   IntegerLiteral      #mathIntegerExp
    ;

mathFunctionApplicationExp
    :   '#' mathCleanFunctionExp
    |   mathCleanFunctionExp
    ;

mathCleanFunctionExp
    :   name=Identifier '(' mathExp (',' mathExp)* ')'  #mathFunctionExp
    |   (qualifier=Identifier '::')? name=Identifier    #mathVariableExp//Todo
    |   ('+'|'-'|'*'|'/')                               #mathOpExp
    ;

mathOutfixExp
    :   lop='<' mathExp rop='>'
    |   lop='|' mathExp rop='|'
    |   lop='||' mathExp rop='||'
    ;

mathTupleExp
    :   '(' mathExp (',' mathExp)+ ')'
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
    |   progVarExp
    ;

progParamExp
    :   (qualifier=Identifier '::')? name=Identifier
        '(' (progExp (',' progExp)*)? ')'
    ;

progVarExp
    :   progNamedVarExp ('.' progNamedVarExp)+      #progRecordDotExp
    |   progNamedVarExp                             #progNamedExp
    ;

progNamedVarExp
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