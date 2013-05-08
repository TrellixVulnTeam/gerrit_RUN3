begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarOutputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|tools
operator|.
name|Diagnostic
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|tools
operator|.
name|DiagnosticCollector
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|tools
operator|.
name|JavaCompiler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|tools
operator|.
name|JavaFileObject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|tools
operator|.
name|StandardJavaFileManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|tools
operator|.
name|ToolProvider
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|compiler
operator|.
name|CompileException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|compiler
operator|.
name|Compiler
import|;
end_import

begin_class
DECL|class|BuckPrologCompiler
specifier|public
class|class
name|BuckPrologCompiler
block|{
DECL|method|main (String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|IOException
throws|,
name|CompileException
block|{
name|List
argument_list|<
name|File
argument_list|>
name|srcs
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|jars
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|argv
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|argv
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
literal|".pl"
argument_list|)
condition|)
block|{
name|srcs
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
condition|)
block|{
name|jars
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|File
name|out
init|=
operator|new
name|File
argument_list|(
name|argv
index|[
name|argv
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
decl_stmt|;
name|File
name|java
init|=
name|tmpdir
argument_list|(
literal|"java"
argument_list|)
decl_stmt|;
name|File
name|classes
init|=
name|tmpdir
argument_list|(
literal|"classes"
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|src
range|:
name|srcs
control|)
block|{
operator|new
name|Compiler
argument_list|()
operator|.
name|prologToJavaSource
argument_list|(
name|src
operator|.
name|getPath
argument_list|()
argument_list|,
name|java
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|javac
argument_list|(
name|jars
argument_list|,
name|java
argument_list|,
name|classes
argument_list|)
expr_stmt|;
name|jar
argument_list|(
name|out
argument_list|,
name|classes
argument_list|)
expr_stmt|;
block|}
DECL|method|tmpdir (String name)
specifier|private
specifier|static
name|File
name|tmpdir
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|d
init|=
name|File
operator|.
name|createTempFile
argument_list|(
name|name
operator|+
literal|"_"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|d
operator|.
name|delete
argument_list|()
operator|||
operator|!
name|d
operator|.
name|mkdir
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot mkdir "
operator|+
name|d
argument_list|)
throw|;
block|}
return|return
name|d
return|;
block|}
DECL|method|javac (List<File> cp, File java, File classes)
specifier|private
specifier|static
name|void
name|javac
parameter_list|(
name|List
argument_list|<
name|File
argument_list|>
name|cp
parameter_list|,
name|File
name|java
parameter_list|,
name|File
name|classes
parameter_list|)
throws|throws
name|IOException
throws|,
name|CompileException
block|{
name|JavaCompiler
name|javac
init|=
name|ToolProvider
operator|.
name|getSystemJavaCompiler
argument_list|()
decl_stmt|;
if|if
condition|(
name|javac
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|CompileException
argument_list|(
literal|"JDK required (running inside of JRE)"
argument_list|)
throw|;
block|}
name|DiagnosticCollector
argument_list|<
name|JavaFileObject
argument_list|>
name|d
init|=
operator|new
name|DiagnosticCollector
argument_list|<
name|JavaFileObject
argument_list|>
argument_list|()
decl_stmt|;
name|StandardJavaFileManager
name|fm
init|=
name|javac
operator|.
name|getStandardFileManager
argument_list|(
name|d
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|StringBuilder
name|classpath
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|jar
range|:
name|cp
control|)
block|{
if|if
condition|(
name|classpath
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|classpath
operator|.
name|append
argument_list|(
name|File
operator|.
name|pathSeparatorChar
argument_list|)
expr_stmt|;
block|}
name|classpath
operator|.
name|append
argument_list|(
name|jar
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ArrayList
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-g:none"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-nowarn"
argument_list|)
expr_stmt|;
if|if
condition|(
name|classpath
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|args
operator|.
name|add
argument_list|(
literal|"-classpath"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|classpath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|args
operator|.
name|add
argument_list|(
literal|"-d"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|classes
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|javac
operator|.
name|getTask
argument_list|(
literal|null
argument_list|,
name|fm
argument_list|,
name|d
argument_list|,
name|args
argument_list|,
literal|null
argument_list|,
name|fm
operator|.
name|getJavaFileObjectsFromFiles
argument_list|(
name|find
argument_list|(
name|java
argument_list|,
literal|".java"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|call
argument_list|()
condition|)
block|{
name|StringBuilder
name|msg
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Diagnostic
argument_list|<
name|?
extends|extends
name|JavaFileObject
argument_list|>
name|err
range|:
name|d
operator|.
name|getDiagnostics
argument_list|()
control|)
block|{
name|msg
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|append
argument_list|(
name|err
operator|.
name|getKind
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
if|if
condition|(
name|err
operator|.
name|getSource
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|append
argument_list|(
name|err
operator|.
name|getSource
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|msg
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
operator|.
name|append
argument_list|(
name|err
operator|.
name|getLineNumber
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
name|err
operator|.
name|getMessage
argument_list|(
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|CompileException
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|fm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|jar (File jar, File classes)
specifier|private
specifier|static
name|void
name|jar
parameter_list|(
name|File
name|jar
parameter_list|,
name|File
name|classes
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|tmp
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"prolog"
argument_list|,
literal|".jar"
argument_list|,
name|jar
operator|.
name|getParentFile
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|JarOutputStream
name|out
init|=
operator|new
name|JarOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|tmp
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|out
operator|.
name|setLevel
argument_list|(
literal|9
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|out
argument_list|,
name|classes
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|tmp
operator|.
name|renameTo
argument_list|(
name|jar
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot create "
operator|+
name|jar
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|tmp
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|add (JarOutputStream out, File classes, String prefix)
specifier|private
specifier|static
name|void
name|add
parameter_list|(
name|JarOutputStream
name|out
parameter_list|,
name|File
name|classes
parameter_list|,
name|String
name|prefix
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|name
range|:
name|classes
operator|.
name|list
argument_list|()
control|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|classes
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|add
argument_list|(
name|out
argument_list|,
name|f
argument_list|,
name|prefix
operator|+
name|name
operator|+
literal|"/"
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|JarEntry
name|e
init|=
operator|new
name|JarEntry
argument_list|(
name|prefix
operator|+
name|name
argument_list|)
decl_stmt|;
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
try|try
block|{
name|e
operator|.
name|setTime
argument_list|(
name|f
operator|.
name|lastModified
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|putNextEntry
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|16
operator|<<
literal|10
index|]
decl_stmt|;
name|int
name|n
decl_stmt|;
while|while
condition|(
literal|0
operator|<
operator|(
name|n
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|find (File dir, String extension)
specifier|private
specifier|static
name|List
argument_list|<
name|File
argument_list|>
name|find
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|extension
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|File
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|f
range|:
name|dir
operator|.
name|listFiles
argument_list|()
control|)
block|{
if|if
condition|(
name|f
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
name|extension
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|find
argument_list|(
name|f
argument_list|,
name|extension
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
return|;
block|}
block|}
end_class

end_unit

