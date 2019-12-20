grammar Definition;

database: table+;

table
    : NAME '{' row* '}'
    ;

row: NAME data_type option*;

data_type
    : 'int'
    | 'boolean'
    | 'string'
    | 'char'
    ;

option
    : 'primary'
    | 'auto_increment'
    | 'unique'
    | 'foreign'
    ;

NAME: [a-zA-Z0-9_]+;

WS: [ \t\r\n]+ -> skip;
