grammar Generator;

database: table+;

table : NAME '{' column* looped? joined?'}';

joined: 'joined(' NAME ')';

looped: 'looped(' NAME ')';

column : NAME DATA_TYPE option*;

option
  : 'primary'
  | 'secondary'
  | 'auto_increment'
  | 'unique'
  | 'foreign(' NAME ')'
  ;

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
