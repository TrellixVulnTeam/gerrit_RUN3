begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.rules
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|rules
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertWithMessage
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|common
operator|.
name|TimeUtil
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
name|testing
operator|.
name|GerritBaseTests
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
name|Guice
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
name|Module
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
name|exceptions
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
name|lang
operator|.
name|BufferingPrologControl
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
name|lang
operator|.
name|JavaObjectTerm
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
name|lang
operator|.
name|Prolog
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
name|lang
operator|.
name|PrologClassLoader
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
name|lang
operator|.
name|PrologMachineCopy
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
name|lang
operator|.
name|StructureTerm
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
name|lang
operator|.
name|SymbolTerm
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
name|lang
operator|.
name|Term
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
name|lang
operator|.
name|VariableTerm
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
name|io
operator|.
name|PushbackReader
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
name|Arrays
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
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_comment
comment|/** Base class for any tests written in Prolog. */
end_comment

begin_class
annotation|@
name|Ignore
DECL|class|PrologTestCase
specifier|public
specifier|abstract
class|class
name|PrologTestCase
extends|extends
name|GerritBaseTests
block|{
DECL|field|test_1
specifier|private
specifier|static
specifier|final
name|SymbolTerm
name|test_1
init|=
name|SymbolTerm
operator|.
name|intern
argument_list|(
literal|"test"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
DECL|field|pkg
specifier|private
name|String
name|pkg
decl_stmt|;
DECL|field|hasSetup
specifier|private
name|boolean
name|hasSetup
decl_stmt|;
DECL|field|hasTeardown
specifier|private
name|boolean
name|hasTeardown
decl_stmt|;
DECL|field|tests
specifier|private
name|List
argument_list|<
name|Term
argument_list|>
name|tests
decl_stmt|;
DECL|field|machine
specifier|protected
name|PrologMachineCopy
name|machine
decl_stmt|;
DECL|field|envFactory
specifier|protected
name|PrologEnvironment
operator|.
name|Factory
name|envFactory
decl_stmt|;
DECL|method|load (String pkg, String prologResource, Module... modules)
specifier|protected
name|void
name|load
parameter_list|(
name|String
name|pkg
parameter_list|,
name|String
name|prologResource
parameter_list|,
name|Module
modifier|...
name|modules
parameter_list|)
throws|throws
name|CompileException
throws|,
name|IOException
block|{
name|ArrayList
argument_list|<
name|Module
argument_list|>
name|moduleList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|moduleList
operator|.
name|add
argument_list|(
operator|new
name|PrologModule
operator|.
name|EnvironmentModule
argument_list|()
argument_list|)
expr_stmt|;
name|moduleList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|modules
argument_list|)
argument_list|)
expr_stmt|;
name|envFactory
operator|=
name|Guice
operator|.
name|createInjector
argument_list|(
name|moduleList
argument_list|)
operator|.
name|getInstance
argument_list|(
name|PrologEnvironment
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|PrologEnvironment
name|env
init|=
name|envFactory
operator|.
name|create
argument_list|(
name|newMachine
argument_list|()
argument_list|)
decl_stmt|;
name|consult
argument_list|(
name|env
argument_list|,
name|getClass
argument_list|()
argument_list|,
name|prologResource
argument_list|)
expr_stmt|;
name|this
operator|.
name|pkg
operator|=
name|pkg
expr_stmt|;
name|hasSetup
operator|=
name|has
argument_list|(
name|env
argument_list|,
literal|"setup"
argument_list|)
expr_stmt|;
name|hasTeardown
operator|=
name|has
argument_list|(
name|env
argument_list|,
literal|"teardown"
argument_list|)
expr_stmt|;
name|StructureTerm
name|head
init|=
operator|new
name|StructureTerm
argument_list|(
literal|":"
argument_list|,
name|SymbolTerm
operator|.
name|intern
argument_list|(
name|pkg
argument_list|)
argument_list|,
operator|new
name|StructureTerm
argument_list|(
name|test_1
argument_list|,
operator|new
name|VariableTerm
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|tests
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|Term
index|[]
name|pair
range|:
name|env
operator|.
name|all
argument_list|(
name|Prolog
operator|.
name|BUILTIN
argument_list|,
literal|"clause"
argument_list|,
name|head
argument_list|,
operator|new
name|VariableTerm
argument_list|()
argument_list|)
control|)
block|{
name|tests
operator|.
name|add
argument_list|(
name|pair
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|tests
argument_list|)
operator|.
name|isNotEmpty
argument_list|()
expr_stmt|;
name|machine
operator|=
name|PrologMachineCopy
operator|.
name|save
argument_list|(
name|env
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set up the Prolog environment.    *    * @param env Prolog environment.    */
DECL|method|setUpEnvironment (PrologEnvironment env)
specifier|protected
name|void
name|setUpEnvironment
parameter_list|(
name|PrologEnvironment
name|env
parameter_list|)
throws|throws
name|Exception
block|{}
DECL|method|newMachine ()
specifier|private
name|PrologMachineCopy
name|newMachine
parameter_list|()
block|{
name|BufferingPrologControl
name|ctl
init|=
operator|new
name|BufferingPrologControl
argument_list|()
decl_stmt|;
name|ctl
operator|.
name|setMaxDatabaseSize
argument_list|(
literal|16
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|ctl
operator|.
name|setPrologClassLoader
argument_list|(
operator|new
name|PrologClassLoader
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|PrologMachineCopy
operator|.
name|save
argument_list|(
name|ctl
argument_list|)
return|;
block|}
DECL|method|consult (BufferingPrologControl env, Class<?> clazz, String prologResource)
specifier|protected
name|void
name|consult
parameter_list|(
name|BufferingPrologControl
name|env
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|String
name|prologResource
parameter_list|)
throws|throws
name|CompileException
throws|,
name|IOException
block|{
try|try
init|(
name|InputStream
name|in
init|=
name|clazz
operator|.
name|getResourceAsStream
argument_list|(
name|prologResource
argument_list|)
init|)
block|{
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
name|prologResource
argument_list|)
throw|;
block|}
name|SymbolTerm
name|pathTerm
init|=
name|SymbolTerm
operator|.
name|create
argument_list|(
name|prologResource
argument_list|)
decl_stmt|;
name|JavaObjectTerm
name|inTerm
init|=
operator|new
name|JavaObjectTerm
argument_list|(
operator|new
name|PushbackReader
argument_list|(
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
argument_list|,
name|Prolog
operator|.
name|PUSHBACK_SIZE
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|env
operator|.
name|execute
argument_list|(
name|Prolog
operator|.
name|BUILTIN
argument_list|,
literal|"consult_stream"
argument_list|,
name|pathTerm
argument_list|,
name|inTerm
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|CompileException
argument_list|(
literal|"Cannot consult "
operator|+
name|prologResource
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|has (BufferingPrologControl env, String name)
specifier|private
name|boolean
name|has
parameter_list|(
name|BufferingPrologControl
name|env
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|StructureTerm
name|head
init|=
name|SymbolTerm
operator|.
name|create
argument_list|(
name|pkg
argument_list|,
name|name
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
name|env
operator|.
name|execute
argument_list|(
name|Prolog
operator|.
name|BUILTIN
argument_list|,
literal|"clause"
argument_list|,
name|head
argument_list|,
operator|new
name|VariableTerm
argument_list|()
argument_list|)
return|;
block|}
DECL|method|runPrologBasedTests ()
specifier|public
name|void
name|runPrologBasedTests
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|errors
init|=
literal|0
decl_stmt|;
name|long
name|start
init|=
name|TimeUtil
operator|.
name|nowMs
argument_list|()
decl_stmt|;
for|for
control|(
name|Term
name|test
range|:
name|tests
control|)
block|{
name|PrologEnvironment
name|env
init|=
name|envFactory
operator|.
name|create
argument_list|(
name|machine
argument_list|)
decl_stmt|;
name|setUpEnvironment
argument_list|(
name|env
argument_list|)
expr_stmt|;
name|env
operator|.
name|setEnabled
argument_list|(
name|Prolog
operator|.
name|Feature
operator|.
name|IO
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|format
argument_list|(
literal|"Prolog %-60s ..."
argument_list|,
name|removePackage
argument_list|(
name|test
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasSetup
condition|)
block|{
name|call
argument_list|(
name|env
argument_list|,
literal|"setup"
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Term
argument_list|>
name|all
init|=
name|env
operator|.
name|all
argument_list|(
name|Prolog
operator|.
name|BUILTIN
argument_list|,
literal|"call"
argument_list|,
name|test
argument_list|)
decl_stmt|;
if|if
condition|(
name|hasTeardown
condition|)
block|{
name|call
argument_list|(
name|env
argument_list|,
literal|"teardown"
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|all
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
literal|"OK"
else|:
literal|"FAIL"
argument_list|)
expr_stmt|;
if|if
condition|(
name|all
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|test
operator|.
name|equals
argument_list|(
name|all
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
for|for
control|(
name|Term
name|t
range|:
name|all
control|)
block|{
name|Term
name|head
init|=
operator|(
operator|(
name|StructureTerm
operator|)
name|removePackage
argument_list|(
name|t
argument_list|)
operator|)
operator|.
name|args
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|Term
index|[]
name|args
init|=
operator|(
operator|(
name|StructureTerm
operator|)
name|head
operator|)
operator|.
name|args
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"  Result: "
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
literal|0
operator|<
name|i
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|all
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|errors
operator|++
expr_stmt|;
block|}
block|}
name|long
name|end
init|=
name|TimeUtil
operator|.
name|nowMs
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-------------------------------"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|format
argument_list|(
literal|"Prolog tests: %d, Failures: %d, Time elapsed %.3f sec"
argument_list|,
name|tests
operator|.
name|size
argument_list|()
argument_list|,
name|errors
argument_list|,
operator|(
name|end
operator|-
name|start
operator|)
operator|/
literal|1000.0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|errors
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|call (BufferingPrologControl env, String name)
specifier|private
name|void
name|call
parameter_list|(
name|BufferingPrologControl
name|env
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|StructureTerm
name|head
init|=
name|SymbolTerm
operator|.
name|create
argument_list|(
name|pkg
argument_list|,
name|name
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertWithMessage
argument_list|(
literal|"Cannot invoke "
operator|+
name|pkg
operator|+
literal|":"
operator|+
name|name
argument_list|)
operator|.
name|that
argument_list|(
name|env
operator|.
name|execute
argument_list|(
name|Prolog
operator|.
name|BUILTIN
argument_list|,
literal|"call"
argument_list|,
name|head
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
DECL|method|removePackage (Term test)
specifier|private
name|Term
name|removePackage
parameter_list|(
name|Term
name|test
parameter_list|)
block|{
name|Term
name|name
init|=
name|test
decl_stmt|;
if|if
condition|(
name|name
operator|instanceof
name|StructureTerm
operator|&&
literal|":"
operator|.
name|equals
argument_list|(
name|name
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|arg
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
block|}
end_class

end_unit

