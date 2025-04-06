parser grammar ExpressionParser;

options { tokenVocab=ExpressionLexer; }

fullId
    : uid (DOT_ID | '.' uid)?
    ;

fullColumnName
    : uid (dottedId dottedId? )?
    | . dottedId dottedId?
    ;

collationName
    : uid | STRING_LITERAL;

uid
    : simpleId
    ;

simpleId
    : ID
    | functionNameBase
    ;

dottedId
    : DOT_ID
    | '.' uid
    ;

decimalLiteral
    : DECIMAL_LITERAL | ZERO_DECIMAL | ONE_DECIMAL | TWO_DECIMAL
    ;

stringLiteral
    : (
        STRING_CHARSET_NAME? STRING_LITERAL
        | START_NATIONAL_STRING_LITERAL
      ) STRING_LITERAL+
    | (
        STRING_CHARSET_NAME? STRING_LITERAL
        | START_NATIONAL_STRING_LITERAL
      ) (COLLATE collationName)?
    ;

booleanLiteral
    : TRUE | FALSE;

nullNotnull
    : NOT? NULL_LITERAL
    ;

constant
    : stringLiteral | decimalLiteral
    | '-' decimalLiteral
    | booleanLiteral
    | REAL_LITERAL | BIT_STRING
    | NOT? nullLiteral=(NULL_LITERAL | NULL_SPEC_LITERAL)
    ;

//    Common Lists
expressions
    : expression (',' expression)*
    ;

//    Functions
functionCall
    : specificFunction
    | fullId '(' functionArgs? ')'
    ;

specificFunction
    : CASE expression caseFuncAlternative+
      (ELSE elseArg=functionArg)? END
    | CASE caseFuncAlternative+
      (ELSE elseArg=functionArg)? END
    | SUBSTRING
      '('
        (
          sourceString=stringLiteral
          | sourceExpression=expression
        ) ','
        (
          fromDecimal=decimalLiteral
          | fromExpression=expression
        )
        (
          ','
          (
            forDecimal=decimalLiteral
            | forExpression=expression
          )
        )?
      ')'
    | TRIM
      '('
        (
          sourceString=stringLiteral
          | sourceExpression=expression
        )
        FROM
        (
          fromString=stringLiteral
          | fromExpression=expression
        )
      ')'
    ;

caseFuncAlternative
    : WHEN condition=functionArg
      THEN consequent=functionArg
    ;

functionArgs
    : (constant | fullColumnName | functionCall | expression)
    (
      ','
      (constant | fullColumnName | functionCall | expression)
    )*
    ;

functionArg
    : constant | fullColumnName | functionCall | expression
    ;


//    Expressions, predicates*****************************************************************************************************

// Simplified approach for expression
expression
    : notOperator=(NOT | '!') expression
    | expression logicalOperator expression
    | predicate
    ;

predicate
    : predicate NOT? IN '(' ( expressions) ')'
    | predicate IS nullNotnull
    | left=predicate comparisonOperator right=predicate
    | predicate NOT? BETWEEN predicate AND predicate
    | predicate NOT? LIKE predicate
    | expressionAtom
    ;


// Add in ASTVisitor nullNotnull in constant
expressionAtom
    : constant
    | fullColumnName
    | functionCall
    | unaryOperator expressionAtom
    | '(' expression (',' expression)* ')'
    | left=expressionAtom mathOperator right=expressionAtom
    ;

unaryOperator
    : '!' | '~' | '+' | '-' | NOT
    ;

comparisonOperator
    : '=' | '>' | '<' | '<' '=' | '>' '='
    | '<' '>' | '!' '=' | '<' '=' '>'
    ;

logicalOperator
    : AND | OR
    ;

mathOperator
    : '*' | '/' | DIV | MOD | '+' | '-'
    ;

functionNameBase
    : ABS | ACOS | ASIN | ATAN | COS | SIN | TAN | ATAN2 | LOG | LOG10 | LOG2
    | ROUND | SQRT
    | CONCAT | LOWER | UPPER
    | COALESCE
    ;

computedExpression:
    functionArg
    ;