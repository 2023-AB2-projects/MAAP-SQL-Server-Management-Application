# MAAP SQL Server Management Application
![](https://github.com/2023-AB2-projects/ab2-project-nemleszkitolmasolni/blob/new_develop/client/src/main/java/images/logo_wide.png)

## Build help

### How to run
After installing gradle, in the root project folder, you can build everything with `gradle build` command. <br>
### Separate build and run for client and server
<b> Client: </b> <code>gradle :client:build</code> && <code>gradle :client:run</code> <br>
<b> Server: </b> <code>gradle :server:build</code> && <code>gradle :server:run</code> <br> 

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
primary_key_field_value [, ... n]
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
    Conditions on <b>selection</b> must have the following format:
        table_name.field OP/FUNC constant(s)
</pre>
