grammar Generator;

database: table+;

table : NAME '{' column* LOOPED? JOINED?'}';

column : NAME DATA_TYPE option*;

option
  : 'primary'
  | 'secondary'
  | 'auto_increment'
  | 'unique'
  | 'foreign(' NAME ')'
  ;

JOINED: 'joined';

LOOPED: 'looped';

DATA_TYPE
  : 'int'
  | 'boolean'
  | 'string'
  | 'char'
  | 'date'
  | 'guid'
  ;

NAME: [a-zA-Z0-9_]+;

WS: [ \t\r\n]+ -> skip;
