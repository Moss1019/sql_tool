<h1>SQL TOOL</h1>
<p>
A custom tool that takes in a definition for a DB table, or a POJO and generates
SQL queries, an entity POJO and a repo to call stored procedures
</p>

<h2>Running the program</h2>
<p>
Sql tool requires Java JDK and Python 3 be installed on the machine. The tool can download the Antlr jar file
needed to generate the Java classes from the grammar. 
</p>
<p>
The grammar used by Antlr is located in the ./parser folder as Definition.g4
</p>
<p>
<code>python .\__init__.py</code> on Windows
<br />
<code>python3 ./__init__.py</code> on Unix based machines
</p>