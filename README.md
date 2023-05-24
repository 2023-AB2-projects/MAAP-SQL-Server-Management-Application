<style>
.sql {
    color: #ffc936;
}
h1 {
    color: #3395de;
}
h2 {
    color: #81bbe6;
}
</style>

# ab2-project-nemleszkitolmasolni
ab2-project-nemleszkitolmasolni created by GitHub Classroom

## Build help

### How to run
After installing gradle, in the root project folder, you can build everything with `gradle build` command. <br>
### Separate build and run for client and server
<b> Client: </b> <code>gradle :client:build</code> && <code>gradle :client:run</code> <br>
<b> Server: </b> <code>gradle :server:build</code> && <code>gradle :server:run</code> <br> 

## User help

## Commands

<pre>
<span class="sql">USE</span> database_name
</pre>

### Data definition language (DDL)

<pre>
<span class="sql">CREATE DATBASE</span> database_name
</pre>

<pre>
<span class="sql">DROP DATBASE</span> database_name
</pre>

<pre>
<span class="sql">CREATE TABLE</span> table_name (
    column_name data_type [ [<span class="sql">PRIMARY KEY</span>] | [<span class="sql">UNIQUE</span>] | [<span class="sql">FOREIGN KEY REFERENCES</span> reference_table(reference_field)] ]
    [, ... n]
)
</pre>

<pre>
<span class="sql">DROP TABLE</span> table_name
</pre>

<pre>
<span class="sql">CREATE INDEX</span> [<span class="sql">UNIQUE</span>] index_name
<span class="sql">ON</span> table_name (column_name [, ... n])
</pre>

### Data Manipulation language (DML)

<pre>
<span class="sql">INSERT INTO</span> table_name
<span class="sql">VALUES</span> (field_value [, ... n])
[, (field_value [, ... n])]
</pre>

<pre>
<span class="sql">DELETE FROM</span> table_name
primary_key_field_value [, ... n]
</pre>

### Data Query Language (DQL)

<pre>
<span class="sql">SELECT</span> [table_name.]field_name [, ... n] | *
<span class="sql">FROM</span> table_name
<span class="sql">WHERE</span> &lt;condition_schema&gt; [<span class="sql">AND</span> ... n]
</pre>

<pre>
&lt;condition_schema&gt;:
[table_name.]field1 OP [table_name.]field2
[table_name.]field FUNC func_args

<b>Supported OP's:</b> <i>=, !=, <, >, <=, >=</i>
<b>Supported FUNC:</b> <span style="color: #ffc936">BETWEEN</span> <i>lower_bound upper_bound</i>
</pre>
