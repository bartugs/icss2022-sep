grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
stylesheet: variableAssignment* stylerule+ ;
stylerule : selector OPEN_BRACE (declaration+ | ifClause)* CLOSE_BRACE;
selector : tag_selector| class_selector | id_selector;
tag_selector   : LOWER_IDENT ;
class_selector : CLASS_IDENT ;
id_selector : ID_IDENT ;
declaration : property COLON expression SEMICOLON;
property : LOWER_IDENT;
ifClause: IF BOX_BRACKET_OPEN expression BOX_BRACKET_CLOSE OPEN_BRACE (declaration+ | ifClause)+ CLOSE_BRACE (elseClause)?;
elseClause: ELSE OPEN_BRACE (declaration+ | ifClause)+ CLOSE_BRACE;
expression
    : expression MUL expression       # MulExpr
    | expression PLUS expression      # AddExpr
    | expression MIN expression       # SubExpr
    | literal                         # LitExpr
    | variableReference               # VarRefExpr
    ;
literal
    : PIXELSIZE                        # PixelLiteral
    | PERCENTAGE                       # PercentageLiteral
    | SCALAR                           # ScalarLiteral
    | COLOR                            # ColorLiteral
    | TRUE                             # BoolLiteral
    | FALSE                            # BoolLiteral
    ;
variableAssignment: variableReference ASSIGNMENT_OPERATOR  expression SEMICOLON;
variableReference:CAPITAL_IDENT;
//EOF;