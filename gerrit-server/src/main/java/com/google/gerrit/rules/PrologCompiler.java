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
DECL|package|com.google.gerrit.rules
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|rules
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
name|client
operator|.
name|RefNames
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
name|server
operator|.
name|config
operator|.
name|GerritServerConfig
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
name|server
operator|.
name|config
operator|.
name|SitePaths
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
name|server
operator|.
name|util
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

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|errors
operator|.
name|MissingObjectException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Config
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Repository
import|;
end_import

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
name|FileNotFoundException
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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLClassLoader
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
name|concurrent
operator|.
name|Callable
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
name|Attributes
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
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|Manifest
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

begin_comment
comment|/**  * Helper class for Rulec: does the actual prolog -> java src -> class -> jar work  * Finds rules.pl in refs/meta/config branch  * Creates rules-(sha1 of rules.pl).jar in (site-path)/cache/rules  */
end_comment

begin_class
DECL|class|PrologCompiler
specifier|public
class|class
name|PrologCompiler
implements|implements
name|Callable
argument_list|<
name|PrologCompiler
operator|.
name|Status
argument_list|>
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (Repository git)
name|PrologCompiler
name|create
parameter_list|(
name|Repository
name|git
parameter_list|)
function_decl|;
block|}
DECL|enum|Status
specifier|public
specifier|static
enum|enum
name|Status
block|{
DECL|enumConstant|NO_RULES
DECL|enumConstant|COMPILED
name|NO_RULES
block|,
name|COMPILED
block|}
DECL|field|ruleDir
specifier|private
specifier|final
name|File
name|ruleDir
decl_stmt|;
DECL|field|git
specifier|private
specifier|final
name|Repository
name|git
decl_stmt|;
annotation|@
name|Inject
DECL|method|PrologCompiler (@erritServerConfig Config config, SitePaths site, @Assisted Repository gitRepository)
name|PrologCompiler
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|config
parameter_list|,
name|SitePaths
name|site
parameter_list|,
annotation|@
name|Assisted
name|Repository
name|gitRepository
parameter_list|)
block|{
name|File
name|cacheDir
init|=
name|site
operator|.
name|resolve
argument_list|(
name|config
operator|.
name|getString
argument_list|(
literal|"cache"
argument_list|,
literal|null
argument_list|,
literal|"directory"
argument_list|)
argument_list|)
decl_stmt|;
name|ruleDir
operator|=
name|cacheDir
operator|!=
literal|null
condition|?
operator|new
name|File
argument_list|(
name|cacheDir
argument_list|,
literal|"rules"
argument_list|)
else|:
literal|null
expr_stmt|;
name|git
operator|=
name|gitRepository
expr_stmt|;
block|}
DECL|method|call ()
specifier|public
name|Status
name|call
parameter_list|()
throws|throws
name|IOException
throws|,
name|CompileException
block|{
name|ObjectId
name|metaConfig
init|=
name|git
operator|.
name|resolve
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
decl_stmt|;
if|if
condition|(
name|metaConfig
operator|==
literal|null
condition|)
block|{
return|return
name|Status
operator|.
name|NO_RULES
return|;
block|}
name|ObjectId
name|rulesId
init|=
name|git
operator|.
name|resolve
argument_list|(
name|metaConfig
operator|.
name|name
argument_list|()
operator|+
literal|":rules.pl"
argument_list|)
decl_stmt|;
if|if
condition|(
name|rulesId
operator|==
literal|null
condition|)
block|{
return|return
name|Status
operator|.
name|NO_RULES
return|;
block|}
if|if
condition|(
name|ruleDir
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|CompileException
argument_list|(
literal|"Caching not enabled"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|ruleDir
operator|.
name|isDirectory
argument_list|()
operator|&&
operator|!
name|ruleDir
operator|.
name|mkdir
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot create "
operator|+
name|ruleDir
argument_list|)
throw|;
block|}
name|File
name|tempDir
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"GerritCodeReview_"
argument_list|,
literal|".rulec"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|tempDir
operator|.
name|delete
argument_list|()
operator|||
operator|!
name|tempDir
operator|.
name|mkdir
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot create "
operator|+
name|tempDir
argument_list|)
throw|;
block|}
try|try
block|{
comment|// Try to make the directory accessible only by this process.
comment|// This may help to prevent leaking rule data to outsiders.
name|tempDir
operator|.
name|setReadable
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tempDir
operator|.
name|setWritable
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tempDir
operator|.
name|setExecutable
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|compileProlog
argument_list|(
name|rulesId
argument_list|,
name|tempDir
argument_list|)
expr_stmt|;
name|compileJava
argument_list|(
name|tempDir
argument_list|)
expr_stmt|;
name|File
name|jarFile
init|=
operator|new
name|File
argument_list|(
name|ruleDir
argument_list|,
literal|"rules-"
operator|+
name|rulesId
operator|.
name|getName
argument_list|()
operator|+
literal|".jar"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|classFiles
init|=
name|getRelativePaths
argument_list|(
name|tempDir
argument_list|,
literal|".class"
argument_list|)
decl_stmt|;
name|createJar
argument_list|(
name|jarFile
argument_list|,
name|classFiles
argument_list|,
name|tempDir
argument_list|,
name|metaConfig
argument_list|,
name|rulesId
argument_list|)
expr_stmt|;
return|return
name|Status
operator|.
name|COMPILED
return|;
block|}
finally|finally
block|{
name|deleteAllFiles
argument_list|(
name|tempDir
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Creates a copy of rules.pl and compiles it into Java sources. */
DECL|method|compileProlog (ObjectId prolog, File tempDir)
specifier|private
name|void
name|compileProlog
parameter_list|(
name|ObjectId
name|prolog
parameter_list|,
name|File
name|tempDir
parameter_list|)
throws|throws
name|IOException
throws|,
name|CompileException
block|{
name|File
name|tempRules
init|=
name|copyToTempFile
argument_list|(
name|prolog
argument_list|,
name|tempDir
argument_list|)
decl_stmt|;
try|try
block|{
name|Compiler
name|comp
init|=
operator|new
name|Compiler
argument_list|()
decl_stmt|;
name|comp
operator|.
name|prologToJavaSource
argument_list|(
name|tempRules
operator|.
name|getPath
argument_list|()
argument_list|,
name|tempDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|tempRules
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|copyToTempFile (ObjectId blobId, File tempDir)
specifier|private
name|File
name|copyToTempFile
parameter_list|(
name|ObjectId
name|blobId
parameter_list|,
name|File
name|tempDir
parameter_list|)
throws|throws
name|IOException
throws|,
name|FileNotFoundException
throws|,
name|MissingObjectException
block|{
comment|// Any leak of tmp caused by this method failing will be cleaned
comment|// up by our caller when tempDir is recursively deleted.
name|File
name|tmp
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"rules"
argument_list|,
literal|".pl"
argument_list|,
name|tempDir
argument_list|)
decl_stmt|;
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tmp
argument_list|)
decl_stmt|;
try|try
block|{
name|git
operator|.
name|open
argument_list|(
name|blobId
argument_list|)
operator|.
name|copyTo
argument_list|(
name|out
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
return|return
name|tmp
return|;
block|}
comment|/** Compile java src into java .class files */
DECL|method|compileJava (File tempDir)
specifier|private
name|void
name|compileJava
parameter_list|(
name|File
name|tempDir
parameter_list|)
throws|throws
name|IOException
throws|,
name|CompileException
block|{
name|JavaCompiler
name|compiler
init|=
name|ToolProvider
operator|.
name|getSystemJavaCompiler
argument_list|()
decl_stmt|;
if|if
condition|(
name|compiler
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
name|diagnostics
init|=
operator|new
name|DiagnosticCollector
argument_list|<
name|JavaFileObject
argument_list|>
argument_list|()
decl_stmt|;
name|StandardJavaFileManager
name|fileManager
init|=
name|compiler
operator|.
name|getStandardFileManager
argument_list|(
name|diagnostics
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Iterable
argument_list|<
name|?
extends|extends
name|JavaFileObject
argument_list|>
name|compilationUnits
init|=
name|fileManager
operator|.
name|getJavaFileObjectsFromFiles
argument_list|(
name|getAllFiles
argument_list|(
name|tempDir
argument_list|,
literal|".java"
argument_list|)
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|options
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|classpath
init|=
name|getMyClasspath
argument_list|()
decl_stmt|;
if|if
condition|(
name|classpath
operator|!=
literal|null
condition|)
block|{
name|options
operator|.
name|add
argument_list|(
literal|"-classpath"
argument_list|)
expr_stmt|;
name|options
operator|.
name|add
argument_list|(
name|classpath
argument_list|)
expr_stmt|;
block|}
name|options
operator|.
name|add
argument_list|(
literal|"-d"
argument_list|)
expr_stmt|;
name|options
operator|.
name|add
argument_list|(
name|tempDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|JavaCompiler
operator|.
name|CompilationTask
name|task
init|=
name|compiler
operator|.
name|getTask
argument_list|(
literal|null
argument_list|,
name|fileManager
argument_list|,
name|diagnostics
argument_list|,
name|options
argument_list|,
literal|null
argument_list|,
name|compilationUnits
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|task
operator|.
name|call
argument_list|()
condition|)
block|{
name|Locale
name|myLocale
init|=
name|Locale
operator|.
name|getDefault
argument_list|()
decl_stmt|;
name|StringBuilder
name|msg
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|"Cannot compile to Java bytecode:"
argument_list|)
expr_stmt|;
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
name|diagnostics
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
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
name|err
operator|.
name|getKind
argument_list|()
argument_list|)
expr_stmt|;
name|msg
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
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
name|err
operator|.
name|getLineNumber
argument_list|()
argument_list|)
expr_stmt|;
name|msg
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
name|myLocale
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
name|fileManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getMyClasspath ()
specifier|private
name|String
name|getMyClasspath
parameter_list|()
block|{
name|StringBuilder
name|cp
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|appendClasspath
argument_list|(
name|cp
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|0
operator|<
name|cp
operator|.
name|length
argument_list|()
condition|?
name|cp
operator|.
name|toString
argument_list|()
else|:
literal|null
return|;
block|}
DECL|method|appendClasspath (StringBuilder cp, ClassLoader classLoader)
specifier|private
name|void
name|appendClasspath
parameter_list|(
name|StringBuilder
name|cp
parameter_list|,
name|ClassLoader
name|classLoader
parameter_list|)
block|{
if|if
condition|(
name|classLoader
operator|.
name|getParent
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|appendClasspath
argument_list|(
name|cp
argument_list|,
name|classLoader
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|classLoader
operator|instanceof
name|URLClassLoader
condition|)
block|{
for|for
control|(
name|URL
name|url
range|:
operator|(
operator|(
name|URLClassLoader
operator|)
name|classLoader
operator|)
operator|.
name|getURLs
argument_list|()
control|)
block|{
if|if
condition|(
literal|"file"
operator|.
name|equals
argument_list|(
name|url
operator|.
name|getProtocol
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
literal|0
operator|<
name|cp
operator|.
name|length
argument_list|()
condition|)
block|{
name|cp
operator|.
name|append
argument_list|(
name|File
operator|.
name|pathSeparatorChar
argument_list|)
expr_stmt|;
block|}
name|cp
operator|.
name|append
argument_list|(
name|url
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** Takes compiled prolog .class files, puts them into the jar file. */
DECL|method|createJar (File archiveFile, List<String> toBeJared, File tempDir, ObjectId metaConfig, ObjectId rulesId)
specifier|private
name|void
name|createJar
parameter_list|(
name|File
name|archiveFile
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|toBeJared
parameter_list|,
name|File
name|tempDir
parameter_list|,
name|ObjectId
name|metaConfig
parameter_list|,
name|ObjectId
name|rulesId
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|now
init|=
name|TimeUtil
operator|.
name|nowMs
argument_list|()
decl_stmt|;
name|File
name|tmpjar
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|".rulec_"
argument_list|,
literal|".jar"
argument_list|,
name|archiveFile
operator|.
name|getParentFile
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|Manifest
name|mf
init|=
operator|new
name|Manifest
argument_list|()
decl_stmt|;
name|mf
operator|.
name|getMainAttributes
argument_list|()
operator|.
name|put
argument_list|(
name|Attributes
operator|.
name|Name
operator|.
name|MANIFEST_VERSION
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|mf
operator|.
name|getMainAttributes
argument_list|()
operator|.
name|putValue
argument_list|(
literal|"Built-by"
argument_list|,
literal|"Gerrit Code Review "
operator|+
name|Version
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|git
operator|.
name|getDirectory
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|mf
operator|.
name|getMainAttributes
argument_list|()
operator|.
name|putValue
argument_list|(
literal|"Source-Repository"
argument_list|,
name|git
operator|.
name|getDirectory
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|mf
operator|.
name|getMainAttributes
argument_list|()
operator|.
name|putValue
argument_list|(
literal|"Source-Commit"
argument_list|,
name|metaConfig
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|mf
operator|.
name|getMainAttributes
argument_list|()
operator|.
name|putValue
argument_list|(
literal|"Source-Blob"
argument_list|,
name|rulesId
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|FileOutputStream
name|stream
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tmpjar
argument_list|)
init|;
name|JarOutputStream
name|out
operator|=
operator|new
name|JarOutputStream
argument_list|(
name|stream
argument_list|,
name|mf
argument_list|)
init|)
block|{
name|byte
name|buffer
index|[]
init|=
operator|new
name|byte
index|[
literal|10240
index|]
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|toBeJared
control|)
block|{
name|JarEntry
name|jarAdd
init|=
operator|new
name|JarEntry
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|jarAdd
operator|.
name|setTime
argument_list|(
name|now
argument_list|)
expr_stmt|;
name|out
operator|.
name|putNextEntry
argument_list|(
name|jarAdd
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|isFile
argument_list|()
condition|)
block|{
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
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|nRead
init|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|nRead
operator|<=
literal|0
condition|)
block|{
break|break;
block|}
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|nRead
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
block|}
block|}
name|out
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|tmpjar
operator|.
name|renameTo
argument_list|(
name|archiveFile
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot replace "
operator|+
name|archiveFile
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|tmpjar
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getAllFiles (File dir, String extension)
specifier|private
name|List
argument_list|<
name|File
argument_list|>
name|getAllFiles
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
name|fileList
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
name|getAllFiles
argument_list|(
name|dir
argument_list|,
name|extension
argument_list|,
name|fileList
argument_list|)
expr_stmt|;
return|return
name|fileList
return|;
block|}
DECL|method|getAllFiles (File dir, String extension, List<File> fileList)
specifier|private
name|void
name|getAllFiles
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|extension
parameter_list|,
name|List
argument_list|<
name|File
argument_list|>
name|fileList
parameter_list|)
block|{
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
name|fileList
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|getAllFiles
argument_list|(
name|f
argument_list|,
name|extension
argument_list|,
name|fileList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getRelativePaths (File dir, String extension)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getRelativePaths
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
name|String
argument_list|>
name|pathList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|getRelativePaths
argument_list|(
name|dir
argument_list|,
name|extension
argument_list|,
literal|""
argument_list|,
name|pathList
argument_list|)
expr_stmt|;
return|return
name|pathList
return|;
block|}
DECL|method|getRelativePaths (File dir, String extension, String path, List<String> pathList)
specifier|private
name|void
name|getRelativePaths
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|extension
parameter_list|,
name|String
name|path
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|pathList
parameter_list|)
block|{
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
name|pathList
operator|.
name|add
argument_list|(
name|path
operator|+
name|f
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|getRelativePaths
argument_list|(
name|f
argument_list|,
name|extension
argument_list|,
name|path
operator|+
name|f
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
argument_list|,
name|pathList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|deleteAllFiles (File dir)
specifier|private
name|void
name|deleteAllFiles
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
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
name|isDirectory
argument_list|()
condition|)
block|{
name|deleteAllFiles
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
name|dir
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

