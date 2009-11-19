begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Licensed under the Apache License, Version 2.0 (the "License");
end_comment

begin_comment
comment|// you may not use this file except in compliance with the License.
end_comment

begin_comment
comment|// You may obtain a copy of the License at
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// http://www.apache.org/licenses/LICENSE-2.0
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Unless required by applicable law or agreed to in writing, software
end_comment

begin_comment
comment|// distributed under the License is distributed on an "AS IS" BASIS,
end_comment

begin_comment
comment|// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
end_comment

begin_comment
comment|// See the License for the specific language governing permissions and
end_comment

begin_comment
comment|// limitations under the License.
end_comment

begin_package
DECL|package|com.google.gerrit.sshd.commands
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
operator|.
name|commands
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|ReviewDb
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|OrmException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|SchemaFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|jdbc
operator|.
name|JdbcSchema
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DatabaseMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSetMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/** Simple interactive SQL query tool. */
end_comment

begin_class
DECL|class|QueryShell
specifier|public
class|class
name|QueryShell
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (@ssisted InputStream in, @Assisted OutputStream out)
name|QueryShell
name|create
parameter_list|(
annotation|@
name|Assisted
name|InputStream
name|in
parameter_list|,
annotation|@
name|Assisted
name|OutputStream
name|out
parameter_list|)
function_decl|;
block|}
DECL|field|in
specifier|private
specifier|final
name|BufferedReader
name|in
decl_stmt|;
DECL|field|out
specifier|private
specifier|final
name|PrintWriter
name|out
decl_stmt|;
DECL|field|dbFactory
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|dbFactory
decl_stmt|;
DECL|field|db
specifier|private
name|ReviewDb
name|db
decl_stmt|;
DECL|field|connection
specifier|private
name|Connection
name|connection
decl_stmt|;
DECL|field|statement
specifier|private
name|Statement
name|statement
decl_stmt|;
annotation|@
name|Inject
DECL|method|QueryShell (final SchemaFactory<ReviewDb> dbFactory, @Assisted final InputStream in, @Assisted final OutputStream out)
name|QueryShell
parameter_list|(
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|dbFactory
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|InputStream
name|in
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|OutputStream
name|out
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
name|this
operator|.
name|dbFactory
operator|=
name|dbFactory
expr_stmt|;
name|this
operator|.
name|in
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|out
operator|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|db
operator|=
name|dbFactory
operator|.
name|open
argument_list|()
expr_stmt|;
try|try
block|{
name|connection
operator|=
operator|(
operator|(
name|JdbcSchema
operator|)
name|db
operator|)
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setAutoCommit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|statement
operator|=
name|connection
operator|.
name|createStatement
argument_list|()
expr_stmt|;
try|try
block|{
name|showBanner
argument_list|()
expr_stmt|;
name|readEvalPrintLoop
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
name|statement
operator|=
literal|null
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
name|db
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|err
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"fatal: Cannot open connection: "
operator|+
name|err
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|err
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"fatal: Cannot open connection: "
operator|+
name|err
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|readEvalPrintLoop ()
specifier|private
name|void
name|readEvalPrintLoop
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|executed
init|=
literal|false
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|print
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|executed
condition|?
literal|"gerrit> "
else|:
literal|"     -> "
argument_list|)
expr_stmt|;
name|String
name|line
init|=
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"\\"
argument_list|)
condition|)
block|{
comment|// Shell command, check the various cases we recognize
comment|//
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|equals
argument_list|(
literal|"h"
argument_list|)
operator|||
name|line
operator|.
name|equals
argument_list|(
literal|"?"
argument_list|)
condition|)
block|{
name|showHelp
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|equals
argument_list|(
literal|"q"
argument_list|)
condition|)
block|{
name|println
argument_list|(
literal|"Bye"
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|equals
argument_list|(
literal|"r"
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|executed
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|equals
argument_list|(
literal|"p"
argument_list|)
condition|)
block|{
name|println
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|equals
argument_list|(
literal|"g"
argument_list|)
condition|)
block|{
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|executeStatement
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|executed
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|equals
argument_list|(
literal|"d"
argument_list|)
condition|)
block|{
name|listTables
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"d "
argument_list|)
condition|)
block|{
name|showTable
argument_list|(
name|line
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|println
argument_list|(
literal|"ERROR: '\\"
operator|+
name|line
operator|+
literal|"' not supported"
argument_list|)
expr_stmt|;
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|showHelp
argument_list|()
expr_stmt|;
block|}
continue|continue;
block|}
if|if
condition|(
name|executed
condition|)
block|{
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|executed
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|buffer
operator|.
name|charAt
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|';'
condition|)
block|{
name|executeStatement
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|executed
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
DECL|method|listTables ()
specifier|private
name|void
name|listTables
parameter_list|()
block|{
specifier|final
name|DatabaseMetaData
name|meta
decl_stmt|;
try|try
block|{
name|meta
operator|=
name|connection
operator|.
name|getMetaData
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
specifier|final
name|String
index|[]
name|types
init|=
block|{
literal|"TABLE"
block|,
literal|"VIEW"
block|}
decl_stmt|;
name|ResultSet
name|rs
init|=
name|meta
operator|.
name|getTables
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|types
argument_list|)
decl_stmt|;
try|try
block|{
name|println
argument_list|(
literal|"                     List of relations"
argument_list|)
expr_stmt|;
name|showResultSet
argument_list|(
name|rs
argument_list|,
literal|"TABLE_SCHEM"
argument_list|,
literal|"TABLE_NAME"
argument_list|,
literal|"TABLE_TYPE"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|showTable (String tableName)
specifier|private
name|void
name|showTable
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
specifier|final
name|DatabaseMetaData
name|meta
decl_stmt|;
try|try
block|{
name|meta
operator|=
name|connection
operator|.
name|getMetaData
argument_list|()
expr_stmt|;
if|if
condition|(
name|meta
operator|.
name|storesUpperCaseIdentifiers
argument_list|()
condition|)
block|{
name|tableName
operator|=
name|tableName
operator|.
name|toUpperCase
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|meta
operator|.
name|storesLowerCaseIdentifiers
argument_list|()
condition|)
block|{
name|tableName
operator|=
name|tableName
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|ResultSet
name|rs
init|=
name|meta
operator|.
name|getColumns
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|tableName
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Table "
operator|+
name|tableName
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
name|println
argument_list|(
literal|"                     Table "
operator|+
name|tableName
argument_list|)
expr_stmt|;
name|showResultSet
argument_list|(
name|rs
argument_list|,
literal|"COLUMN_NAME"
argument_list|,
literal|"TYPE_NAME"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|ResultSet
name|rs
init|=
name|meta
operator|.
name|getIndexInfo
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|tableName
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|println
argument_list|(
literal|"Indexes on "
operator|+
name|tableName
operator|+
literal|":"
argument_list|)
expr_stmt|;
name|showResultSet
argument_list|(
name|rs
argument_list|,
literal|"INDEX_NAME"
argument_list|,
literal|"NON_UNIQUE"
argument_list|,
literal|"COLUMN_NAME"
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|executeStatement (final String sql)
specifier|private
name|void
name|executeStatement
parameter_list|(
specifier|final
name|String
name|sql
parameter_list|)
block|{
specifier|final
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|hasResultSet
decl_stmt|;
try|try
block|{
name|hasResultSet
operator|=
name|statement
operator|.
name|execute
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
if|if
condition|(
name|hasResultSet
condition|)
block|{
specifier|final
name|ResultSet
name|rs
init|=
name|statement
operator|.
name|getResultSet
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|int
name|rowCount
init|=
name|showResultSet
argument_list|(
name|rs
argument_list|)
decl_stmt|;
specifier|final
name|long
name|ms
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
name|println
argument_list|(
literal|"("
operator|+
name|rowCount
operator|+
operator|(
name|rowCount
operator|==
literal|1
condition|?
literal|" row"
else|:
literal|" rows"
operator|)
comment|//
operator|+
literal|"; "
operator|+
name|ms
operator|+
literal|" ms)"
argument_list|)
expr_stmt|;
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
specifier|final
name|int
name|updateCount
init|=
name|statement
operator|.
name|getUpdateCount
argument_list|()
decl_stmt|;
specifier|final
name|long
name|ms
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
name|println
argument_list|(
literal|"UPDATE "
operator|+
name|updateCount
operator|+
literal|"; "
operator|+
name|ms
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|showResultSet (final ResultSet rs, String... show)
specifier|private
name|int
name|showResultSet
parameter_list|(
specifier|final
name|ResultSet
name|rs
parameter_list|,
name|String
modifier|...
name|show
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
name|ResultSetMetaData
name|meta
init|=
name|rs
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|columnMap
decl_stmt|;
if|if
condition|(
name|show
operator|!=
literal|null
operator|&&
literal|0
operator|<
name|show
operator|.
name|length
condition|)
block|{
specifier|final
name|int
name|colCnt
init|=
name|meta
operator|.
name|getColumnCount
argument_list|()
decl_stmt|;
name|columnMap
operator|=
operator|new
name|int
index|[
name|show
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|show
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|columnMap
index|[
name|j
index|]
operator|=
name|rs
operator|.
name|findColumn
argument_list|(
name|show
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
specifier|final
name|int
name|colCnt
init|=
name|meta
operator|.
name|getColumnCount
argument_list|()
decl_stmt|;
name|columnMap
operator|=
operator|new
name|int
index|[
name|colCnt
index|]
expr_stmt|;
for|for
control|(
name|int
name|colId
init|=
literal|0
init|;
name|colId
operator|<
name|colCnt
condition|;
name|colId
operator|++
control|)
name|columnMap
index|[
name|colId
index|]
operator|=
name|colId
operator|+
literal|1
expr_stmt|;
block|}
specifier|final
name|int
name|colCnt
init|=
name|columnMap
operator|.
name|length
decl_stmt|;
specifier|final
name|String
index|[]
name|names
init|=
operator|new
name|String
index|[
name|colCnt
index|]
decl_stmt|;
specifier|final
name|int
index|[]
name|widths
init|=
operator|new
name|int
index|[
name|colCnt
index|]
decl_stmt|;
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|colCnt
condition|;
name|c
operator|++
control|)
block|{
specifier|final
name|int
name|colId
init|=
name|columnMap
index|[
name|c
index|]
decl_stmt|;
name|names
index|[
name|c
index|]
operator|=
name|meta
operator|.
name|getColumnLabel
argument_list|(
name|colId
argument_list|)
expr_stmt|;
name|widths
index|[
name|c
index|]
operator|=
name|names
index|[
name|c
index|]
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|String
index|[]
argument_list|>
name|rows
init|=
operator|new
name|ArrayList
argument_list|<
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
specifier|final
name|String
index|[]
name|row
init|=
operator|new
name|String
index|[
name|columnMap
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|colCnt
condition|;
name|c
operator|++
control|)
block|{
specifier|final
name|int
name|colId
init|=
name|columnMap
index|[
name|c
index|]
decl_stmt|;
name|String
name|val
init|=
name|rs
operator|.
name|getString
argument_list|(
name|colId
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|val
operator|=
literal|"NULL"
expr_stmt|;
block|}
name|row
index|[
name|c
index|]
operator|=
name|val
expr_stmt|;
name|widths
index|[
name|c
index|]
operator|=
name|Math
operator|.
name|max
argument_list|(
name|widths
index|[
name|c
index|]
argument_list|,
name|val
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rows
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|colCnt
condition|;
name|c
operator|++
control|)
block|{
specifier|final
name|int
name|colId
init|=
name|columnMap
index|[
name|c
index|]
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|c
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" | "
argument_list|)
expr_stmt|;
block|}
name|String
name|n
init|=
name|names
index|[
name|c
index|]
decl_stmt|;
if|if
condition|(
name|widths
index|[
name|c
index|]
operator|<
name|n
operator|.
name|length
argument_list|()
condition|)
block|{
name|n
operator|=
name|n
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|widths
index|[
name|c
index|]
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|n
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|<
name|colCnt
operator|-
literal|1
condition|)
block|{
for|for
control|(
name|int
name|pad
init|=
name|n
operator|.
name|length
argument_list|()
init|;
name|pad
operator|<
name|widths
index|[
name|c
index|]
condition|;
name|pad
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|println
argument_list|(
literal|" "
operator|+
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|colCnt
condition|;
name|c
operator|++
control|)
block|{
if|if
condition|(
literal|0
operator|<
name|c
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"-+-"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|pad
init|=
literal|0
init|;
name|pad
operator|<
name|widths
index|[
name|c
index|]
condition|;
name|pad
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
block|}
block|}
name|println
argument_list|(
literal|" "
operator|+
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|dataTruncated
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
index|[]
name|row
range|:
name|rows
control|)
block|{
name|b
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|colCnt
condition|;
name|c
operator|++
control|)
block|{
specifier|final
name|int
name|colId
init|=
name|columnMap
index|[
name|c
index|]
decl_stmt|;
specifier|final
name|int
name|max
init|=
name|widths
index|[
name|c
index|]
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|c
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" | "
argument_list|)
expr_stmt|;
block|}
name|String
name|s
init|=
name|row
index|[
name|c
index|]
decl_stmt|;
if|if
condition|(
literal|1
operator|<
name|colCnt
operator|&&
name|max
operator|<
name|s
operator|.
name|length
argument_list|()
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|max
argument_list|)
expr_stmt|;
name|dataTruncated
operator|=
literal|true
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|<
name|colCnt
operator|-
literal|1
condition|)
block|{
for|for
control|(
name|int
name|pad
init|=
name|s
operator|.
name|length
argument_list|()
init|;
name|pad
operator|<
name|max
condition|;
name|pad
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|println
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dataTruncated
condition|)
block|{
name|warning
argument_list|(
literal|"some column data was truncated"
argument_list|)
expr_stmt|;
block|}
return|return
name|rows
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|warning (final String msg)
specifier|private
name|void
name|warning
parameter_list|(
specifier|final
name|String
name|msg
parameter_list|)
block|{
name|println
argument_list|(
literal|"WARNING: "
operator|+
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|error (final SQLException err)
specifier|private
name|void
name|error
parameter_list|(
specifier|final
name|SQLException
name|err
parameter_list|)
block|{
name|println
argument_list|(
literal|"ERROR: "
operator|+
name|err
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|print (String s)
specifier|private
name|void
name|print
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|println (String s)
specifier|private
name|void
name|println
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|readLine ()
specifier|private
name|String
name|readLine
parameter_list|()
block|{
try|try
block|{
return|return
name|in
operator|.
name|readLine
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|showBanner ()
specifier|private
name|void
name|showBanner
parameter_list|()
block|{
name|println
argument_list|(
literal|"Welcome to Gerrit Code Review "
operator|+
name|Version
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|print
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|print
argument_list|(
name|connection
operator|.
name|getMetaData
argument_list|()
operator|.
name|getDatabaseProductName
argument_list|()
argument_list|)
expr_stmt|;
name|print
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|print
argument_list|(
name|connection
operator|.
name|getMetaData
argument_list|()
operator|.
name|getDatabaseProductVersion
argument_list|()
argument_list|)
expr_stmt|;
name|println
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|err
parameter_list|)
block|{
name|error
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|println
argument_list|(
literal|"Type '\\h' for help.  Type '\\r' to clear the buffer."
argument_list|)
expr_stmt|;
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|showHelp ()
specifier|private
name|void
name|showHelp
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|help
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|help
operator|.
name|append
argument_list|(
literal|"General\n"
argument_list|)
expr_stmt|;
name|help
operator|.
name|append
argument_list|(
literal|"  \\q        quit\n"
argument_list|)
expr_stmt|;
name|help
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|help
operator|.
name|append
argument_list|(
literal|"Query Buffer\n"
argument_list|)
expr_stmt|;
name|help
operator|.
name|append
argument_list|(
literal|"  \\g        execute the query buffer\n"
argument_list|)
expr_stmt|;
name|help
operator|.
name|append
argument_list|(
literal|"  \\p        display the current buffer\n"
argument_list|)
expr_stmt|;
name|help
operator|.
name|append
argument_list|(
literal|"  \\r        clear the query buffer\n"
argument_list|)
expr_stmt|;
name|help
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|help
operator|.
name|append
argument_list|(
literal|"Informational\n"
argument_list|)
expr_stmt|;
name|help
operator|.
name|append
argument_list|(
literal|"  \\d        list all tables\n"
argument_list|)
expr_stmt|;
name|help
operator|.
name|append
argument_list|(
literal|"  \\d NAME   describe table\n"
argument_list|)
expr_stmt|;
name|help
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|print
argument_list|(
name|help
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

