# SQL Tool
A tool used to generate MySQL code and Java spring classes that form the foundations of a web app

## Generated code
- SQL statements to create tables and stored procedures
- @Repository beans
- @Service beans
- @Controller beans
- @Entity POJOS

## Using the code
First, define the objects in the application using the following syntax <br />
<entity_name> { <br />
    \<col_name\> \<data_type\> \<options zero or more\> <br />
    ... <br />
    ...
<br />} <br />
For example <br />
user { <br />
user_id int primary auto_increment <br />
user_name string unique <br />
last_name string <br />
global_id int unique <br />
joined user_subject <br />
}

### Data types
- int
- string
- boolean
- char

### Options
- primary 
- auto_increment
- unique
- foreign (marks the column as a foreign key that links to another table)

NOTES <br />
foreign key uses the column name to generate the SQL used to set up a reference <br />
unique is used to generate select by statements

### Running the program
SQL_Tool requires a Python environment and JVM be installed on the system
- git clone https://github.com/Moss1019/sql_tool.git
- cd sql_tool
- python \_\_init\_\_.py

Use the commands presented by the Python script

- 1 - Download the antlr jar file
- 4 - Run the language app on the definition file

The other options are used when generating a new parser from another grammar (g4) file and to compile the Java code that forms the language app
