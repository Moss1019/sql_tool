grammar Definition;

database: table+;

table
    : NAME '{' row* '}'
    ;

row
    : JOINED NAME
    | NAME DATA_TYPE option*;

option
    : 'primary'
    | 'auto_increment'
    | 'unique'
    | 'foreign'
    | 'foreign(' NAME ')'
    ;

JOINED: 'joined';

DATA_TYPE
    : 'int'
    | 'boolean'
    | 'string'
    | 'char'
    | 'date'
    ;

NAME: [a-zA-Z0-9_]+;

WS: [ \t\r\n]+ -> skip;
