# MAAP SQL Server Management Application

![our_log](https://github.com/2023-AB2-projects/ab2-project-nemleszkitolmasolni/blob/new_develop/client/src/main/java/images/logo_wide.png)

## About the project

![client_side_app](https://github.com/2023-AB2-projects/ab2-project-nemleszkitolmasolni/blob/new_develop/client/src/main/java/images/light_and_dark_mode.png)

**MAAP** is a **relational database managament system** (RDBMS) that supports a variety of SQL commands, with a friendly client side and helpful designers.

The backend and frontend was written fully in Java, using JSON for communication and catalog, binary file management and indexing with [B+ Trees](https://en.wikipedia.org/wiki/B%2B_tree).

---

## Build & run help

### How to run

After installing gradle, in the root project folder, you can build everything with `gradle build` command.

### Separately build serve and client

<b> Server: </b> <code>gradle server:build</code>

<b> Client: </b> <code>gradle client:build</code>

### Running the server and client
Open two terminals and run the follwing gradle scripts:
<code>gradle client:run</code> 
&& <code>gradle server:run</code> 

---

## Supported commands

<pre>
USE database_name
</pre>

### Data definition language (DDL)

<pre>
CREATE DATABASE database_name
</pre>

<pre>
DROP DATABASE database_name
</pre>

<pre>
CREATE TABLE table_name (
    column_name data_type [ [PRIMARY KEY] | [UNIQUE] | [FOREIGN KEY REFERENCES reference_table(reference_field)] ]
    [, ... n]
)
</pre>

<pre>
DROP TABLE table_name
</pre>

<pre>
CREATE INDEX [UNIQUE] index_name
ON table_name (column_name [, ... n])
</pre>

### Data Manipulation language (DML)

<pre>
INSERT INTO table_name
VALUES (field_value [, ... n])
[, (field_value [, ... n])]
</pre>

<pre>
DELETE FROM table_name
[ WHERE &lt;condition_schema&gt;  [AND ... n] ]
</pre>

### Data Query Language (DQL)

<pre>
SELECT table_name.field_name [, ... n] | *
FROM table_name
[ JOIN join_table ON &lt;condition_schema&gt; [... n] ]
[ WHERE &lt;condition_schema&gt; [AND ... n] ]
[ GROUP BY table_name.field_name [, ... n] ]
</pre>

<pre>
&lt;condition_schema&gt;:
table_name.field1 OP table_name.field2
table_name.field FUNC func_args

<b>Supported OP's:</b> <i>=, !=, <, >, <=, >=</i>
<b>Supported FUNC:</b> BETWEEN <i>lower_bound upper_bound</i>
</pre>

<pre>
<i>Note:</i> 
    Conditions of <b>table joins</b> must have the following format: 
        table_name1.field = table_name2.field
    Conditions on <b>selection</b> or <b>deletion</b> must have the following format:
        table_name.field OP/FUNC constant(s)
</pre>
