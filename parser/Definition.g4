grammar Definition;

database: table+;

table
    : NAME '{' row* '}'
    ;

row
    : 'joined' NAME
    | NAME DATA_TYPE OPTION*;

OPTION
    : 'primary'
    | 'auto_increment'
    | 'unique'
    | 'foreign'
    ;

DATA_TYPE
    : 'int'
    | 'boolean'
    | 'string'
    | 'char'
    | 'date'
    ;

NAME: [a-zA-Z0-9_]+;

WS: [ \t\r\n]+ -> skip;
