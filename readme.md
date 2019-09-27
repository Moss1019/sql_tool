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
<p>
make use of the options provided to use the program. The antlr runtime will generate files in the ./parser directory. The 
language application will generate output in the ./output directory. The folders are generated.
</p>
<h2>Using the output</h2>
<p>
This tool is meant to speed up development of the classes needed for a java and spring boot application.
Specifically the model class and the repository. The code written in these files are straight forward, and writing
by hand can take a lot of time.
</p>
