<h1>SQL TOOL</h1>
<p>
A custom tool that takes in a definition for an entity and generats a series of files that contain the SQL
necessary to create the DB table and the stored procedures for the common CRUD operations. Other files are 
Java classes and these are @Entity class, @Repository class, @Service class and @Controller class.
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
Specifically the POJO that will be marked with @Entity and the bean that will be marked with @Repository.
The code written in these files are straight forward, and writing it by hand can take a lot of time.
This tool generates a lot of code from a simple definition, which can then be tweaked as if written by hand.
</p>
<h2>Example definition</h2>
<p>
currently the ./test.txt file contains the defintion. This will be changed soon to rather be an argument
</p>
<p>
User { <br/>
   id int primary_key auto_increment; <br/>
   first_name string; <br/>
   last_name string; <br/>
   is_active bool; <br/>
};
</p>
<p>
This project is being actively worked on, so there will be new things every now and then.
</p>
