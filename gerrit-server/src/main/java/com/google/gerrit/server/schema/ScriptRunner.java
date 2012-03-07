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
DECL|package|com.google.gerrit.server.schema
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|schema
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
name|reviewdb
operator|.
name|server
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
name|gwtorm
operator|.
name|server
operator|.
name|OrmException
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
name|FileNotFoundException
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
comment|/** Parses a SQL script from a resource file and later runs it. */
end_comment

begin_class
DECL|class|ScriptRunner
class|class
name|ScriptRunner
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|commands
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|commands
decl_stmt|;
DECL|method|ScriptRunner (final String name)
name|ScriptRunner
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
try|try
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|commands
operator|=
name|parse
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot parse "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|run (final ReviewDb db)
name|void
name|run
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
block|{
specifier|final
name|Connection
name|c
init|=
operator|(
operator|(
name|JdbcSchema
operator|)
name|db
operator|)
operator|.
name|getConnection
argument_list|()
decl_stmt|;
specifier|final
name|Statement
name|stmt
init|=
name|c
operator|.
name|createStatement
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|String
name|sql
range|:
name|commands
control|)
block|{
try|try
block|{
name|stmt
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
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Error in "
operator|+
name|name
operator|+
literal|":\n"
operator|+
name|sql
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
finally|finally
block|{
name|stmt
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
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Cannot run statements for "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|parse (final String name)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|parse
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|in
init|=
name|ReviewDb
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"SQL script "
operator|+
name|name
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
name|BufferedReader
name|br
init|=
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
decl_stmt|;
try|try
block|{
name|String
name|delimiter
init|=
literal|";"
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|commands
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|line
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"--"
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|&&
name|line
operator|.
name|toLowerCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"delimiter "
argument_list|)
condition|)
block|{
name|delimiter
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|"delimiter "
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
continue|continue;
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
name|isDone
argument_list|(
name|delimiter
argument_list|,
name|line
argument_list|,
name|buffer
argument_list|)
condition|)
block|{
name|String
name|cmd
init|=
name|buffer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|commands
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|buffer
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
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
name|commands
operator|.
name|add
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|commands
return|;
block|}
finally|finally
block|{
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|isDone (String delimiter, String line, StringBuilder buffer)
specifier|private
name|boolean
name|isDone
parameter_list|(
name|String
name|delimiter
parameter_list|,
name|String
name|line
parameter_list|,
name|StringBuilder
name|buffer
parameter_list|)
block|{
if|if
condition|(
literal|";"
operator|.
name|equals
argument_list|(
name|delimiter
argument_list|)
condition|)
block|{
return|return
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
return|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|equals
argument_list|(
name|delimiter
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|setLength
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
name|delimiter
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

