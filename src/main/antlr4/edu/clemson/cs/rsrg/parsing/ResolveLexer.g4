lexer grammar ResolveLexer;

ABS
    :   'abs'
    ;

ALL
    :   'all'
    ;

ALTERS
    :   'alt'
    |   'alters'
    ;

AND
    :   'and'
    ;

ARRAY
    :   'Array'
    ;

AUX_CODE
    :   'Aux_Code'
    ;

AUX_VAR
    :   'Aux_Var'
    ;

AUXILIARY
    :   'Aux'
    |   'Auxiliary'
    ;

AXIOM
    :   'Axiom'
    ;

BASECASE
    :   'Base_Case'
    ;

BIG_CONCAT
    :   'Concatenation'
    ;

BIG_INTERSECT
    :   'Intersection'
    ;

BIG_PRODUCT
    :   'Product'
    ;

BIG_SUM
    :   'Sum'
    ;

BIG_UNION
    :   'Union'
    ;

BY
    :   'by'
    ;

CARTPROD
    :   'Cart_Prod'
    ;

CASE
    :   'Case'
    ;

CATEGORICAL
    :   'Categorical'
    ;

CHANGING
    :   'changing'
    ;

CLEARS
    :   'clr'
    |   'clears'
    ;

COMPLEMENT
    :   'complement'
    ;

CONCEPT
    :   'Concept'
    ;

CONFIRM
    :   'Confirm'
    ;

CONSTRAINT
    :   'Constraint'
    |   'Constraints'
    |   'constraint'
    |   'constraints'
    ;

CONVENTION
    :   'Convention'
    |   'Conventions'
    |   'convention'
    |   'conventions'
    ;

COROLLARY
    :   'Corollary'
    ;

CORR
    :   'correspondence'
    ;

DECREASING
    :   'decreasing'
    ;

DEFINES
    :   'Defines'
    ;

DEFINITION
    :   'Definition'
    |   'Def'
    ;

DIV
    :   'div'
    ;

DO
    :   'do'
    ;

DURATION
    :   'duration'
    ;

ELAPSED_TIME
    :   'elapsed_time'
    ;

ELSE
    :   'Else'
    ;

END
    :   'end'
    ;

ENHANCED
    :   'enhanced'
    ;

ENHANCEMENT
    :   'Enhancement'
    ;

ENSURES
    :   'ensures'
    ;

EVALUATES
    :   'eval'
    |   'evaluates'
    ;

EXEMPLAR
    :   'exemplar'
    ;

EXISTS
    :   'exists'
    ;

EXTERNALLY
    :   'externally'
    ;

FACILITY
    :   'Facility'
    ;

FAC_FINAL
    :   'Facility_Finalization'
    ;

FAC_INIT
    :   'Facility_Initialization'
    ;

FAMILY
    :   'Family'
    ;

FINALIZATION
    :   'finalization'
    ;

FROM
    :   'from'
    ;

FOR
    :   'For'
    |   'for'
    ;

IF
    :   'If'
    ;

IFF
    :   'iff'
    ;

IMPLICIT
    :   'Implicit'
    ;

IMPLIES
    :   'implies'
    ;

IN
    :   'is_in'
    ;

INDUCTIVE
    :   'Inductive'
    ;

INDUCTIVE_BASE_NUM
    :   '(i.)'
    ;

INDUCTIVE_HYP_NUM
    :   '(ii.)'
    ;

INITIALIZATION
    :   'initialization'
    ;

INSTANTIATION
    :   'instantiation'
    ;

INTERSECT
    :   'intersect'
    ;

INTRODUCES
    :   'introduces'
    ;

IS
    :   'is'
    ;

IS_IN
    :   'is_in'
    ;

ITERATE
    :   'Iterate'
    ;

LAMBDA
    :   'lambda'
    ;

LEMMA
    :   'Lemma'
    ;

MAINP_DISP
    :   'mainp_disp'
    ;

MAINTAINING
    :   'maintaining'
    ;

MATH
    :   'Math'
    ;

MOD
    :   'mod'
    ;

MODELED
    :   'modeled'
    ;

MODUS
    :   'modus'
    ;

NOT
    :   'not'
    ;

NOT_IN
    :   'is_not_in'
    ;

NOT_PROP_SUBSET
    :   'is_not_proper_subset_of'
    ;

NOT_SUBSET
    :   'is_not_subset_of'
    ;

NOT_SUBSTR
    :   'is_not_substring_of'
    ;

OF
    :   'of'
    ;

ON
    :   'on'
    ;

OP
    :   'op'
    ;

OPERATION
    :   'Oper'
    |   'Operation'
    ;

OR
    :   'or'
    ;

OTHERWISE
    :   'otherwise'
    ;

PERF_FINAL
    :   'perf_finalization'
    ;

PERF_INIT
    :   'perf_initialization'
    ;

PONENS
    :   'ponens'
    ;

PRECIS
    :   'Precis'
    ;

PRESERVES
    :   'pres'
    |   'preserves'
    ;

PROFILE
    :   'Profile'
    ;

PROCEDURE
    :   'Proc'
    |   'Procedure'
    ;

PROPERTY
    :   'Property'
    ;

PROP_SUBSET
    :   'is_proper_subset_of'
    ;

REALIZATION
    :   'Realization'
    ;

REALIZED
    :   'realized'
    ;

RECORD
    :   'Record'
    ;

RECURSIVE
    :   'Recursive'
    ;

RELATED
    :   'related'
    ;

REM
    :   'rem'
    ;

REMEMBER
    :   'Remember'
    ;

REPLACES
    :   'rpl'
    |   'replaces'
    ;

REPRESENTED
    :   'represented'
    ;

REQUIRES
    :   'requires'
    ;

RESTORES
    :   'rest'
    |   'restores'
    ;

SHORT_FOR
    :   'short_for'
    ;

SUBSET
    :   'is_subset_of'
    ;

SUBSTR
    :   'is_substring_of'
    ;

SUCH
    :   'such'
    ;

THAT
    :   'that'
    ;

THEN
    :   'then'
    ;

THEOREM
    :   'Theorem'
    ;

THERE
    :   'There'
    |   'there'
    ;

TYPE
    :   'Type'
    |   'type'
    ;

UNION
    :   'union'
    ;

UNIQUE
    :   'Unique'
    |   'unique'
    ;

UPDATES
    :   'upd'
    |   'updates'
    ;

USES
    :   'uses'
    ;

VAR
    :   'Var'
    ;

WHERE
    :   'where'
    ;

WHILE
    :   'While'
    ;

WITHOUT
    :   'without'
    ;

WITH_PROFILE
    :   'with_profile'
    ;

// Additional Symbol Tokens

ASSIGN_OP
    :   ':='
    ;

BAR
    :   '|'
    ;

CARAT
    :   '^'
    ;

COLON
    :   ':'
    ;

COMMA
    :   ','
    ;

CONCAT
    :   'o'
    ;

DBL_BAR
    :   '||'
    ;

DBL_LBRACE
    :   '{{'
    ;

DBL_RBRACE
    :   '}}'
    ;

DIVIDE
    :   '/'
    ;

DOT
    :   '.'
    ;

EQL
    :   '='
    ;

EXP
    :   '**'
    ;

FUNCARROW
    :   '->'
    ;

GG
    :   '>>'
    ;

GT
    :   '>'
    ;

GT_EQL
    :   '>='
    ;

HASH
    :   '#'
    ;

LBRACE
    :   '{'
    ;

LL
    :   '<<'
    ;

LPAREN
    :   '('
    ;

LT
    :   '<'
    ;

LT_EQL
    :   '<='
    ;

MINUS
    :   '-'
    ;

MULTIPLY
    :   '*'
    ;

NOT_EQL
    :   '/='
    ;

PLUS
    :   '+'
    ;

QUALIFIER
    :   '::'
    ;

RANGE
    :   '..'
    ;

RBRACE
    :   '}'
    ;

RPAREN
    :   ')'
    ;

SEMICOLON
    :   ';'
    ;

SWAP_OP
    :   ':=:'
    ;

TILDE
    :   '~'
    ;

// literal rules and fragments

BOOLEAN_LITERAL
    :   'B'
    |   'false'
    |   'true'
    ;

INTEGER_LITERAL
    :   Digits
    ;

REAL_LITERAL
    :   Digits DOT Digits+
    ;

CHARACTER_LITERAL
    :   '\'' SingleCharacter '\''
    ;

STRING_LITERAL
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
Digits
    :   [0-9]+
    ;

fragment
Digit
    :   [0-9]
    ;

fragment
SingleCharacter
    :   ~['\\]
    ;

// whitespace, identifier rules, and comments

COMMENT
    :   '(*' .*? '*)' -> skip
    ;

IDENTIFIER
    :   LETTER LETTER_OR_DIGIT*
    ;

LETTER
    :   [a-zA-Z$_]
    ;

LETTER_OR_DIGIT
    :   [a-zA-Z0-9$_]
    ;

LINE_COMMENT
    :   '--' ~[\r\n]* -> skip
    ;

SPACE
    :  [ \t\r\n\u000C]+ -> skip
    ;