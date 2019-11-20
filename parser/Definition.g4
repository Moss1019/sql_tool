grammar Definition;

definition: NAME '{' columnDef* (',' columnDef)* '}'';';

columnDef: NAME TYPE options+';';

options: (option)*;

option: NAME;

TYPE: 'string'
    | 'int'
    | 'char'
    | 'bool'
    | 'date'
    ;

NAME: [a-zA-Z_]+;

WS: [ \t\r\n]+ -> skip;
