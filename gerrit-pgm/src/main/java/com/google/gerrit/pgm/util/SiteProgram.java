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
DECL|package|com.google.gerrit.pgm.util
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|util
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Scopes
operator|.
name|SINGLETON
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Stage
operator|.
name|PRODUCTION
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
name|lifecycle
operator|.
name|LifecycleModule
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
name|GerritServerConfigModule
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
name|SitePath
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
name|schema
operator|.
name|DataSourceProvider
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
name|schema
operator|.
name|DatabaseModule
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
name|schema
operator|.
name|SchemaModule
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
name|inject
operator|.
name|AbstractModule
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
name|CreationException
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
name|Injector
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
name|Key
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
name|google
operator|.
name|inject
operator|.
name|name
operator|.
name|Names
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
name|spi
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
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
name|FileFilter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|sql
operator|.
name|SQLException
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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import

begin_class
DECL|class|SiteProgram
specifier|public
specifier|abstract
class|class
name|SiteProgram
extends|extends
name|AbstractProgram
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--site-path"
argument_list|,
name|aliases
operator|=
block|{
literal|"-d"
block|}
argument_list|,
name|usage
operator|=
literal|"Local directory containing site data"
argument_list|)
DECL|field|sitePath
specifier|private
name|File
name|sitePath
init|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
comment|/** @return the site path specified on the command line. */
DECL|method|getSitePath ()
specifier|protected
name|File
name|getSitePath
parameter_list|()
block|{
name|File
name|path
init|=
name|sitePath
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"."
operator|.
name|equals
argument_list|(
name|path
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
comment|/** Ensures we are running inside of a valid site, otherwise throws a Die. */
DECL|method|mustHaveValidSite ()
specifier|protected
name|void
name|mustHaveValidSite
parameter_list|()
throws|throws
name|Die
block|{
if|if
condition|(
operator|!
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|getSitePath
argument_list|()
argument_list|,
literal|"etc"
argument_list|)
argument_list|,
literal|"gerrit.config"
argument_list|)
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"not a Gerrit site: '"
operator|+
name|getSitePath
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"Perhaps you need to run init first?"
argument_list|)
throw|;
block|}
block|}
comment|/** Load extra JARs from {@code lib/} subdirectory of {@link #getSitePath()} */
DECL|method|loadSiteLib ()
specifier|protected
name|void
name|loadSiteLib
parameter_list|()
block|{
specifier|final
name|File
name|libdir
init|=
operator|new
name|File
argument_list|(
name|getSitePath
argument_list|()
argument_list|,
literal|"lib"
argument_list|)
decl_stmt|;
specifier|final
name|File
index|[]
name|list
init|=
name|libdir
operator|.
name|listFiles
argument_list|(
operator|new
name|FileFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|path
parameter_list|)
block|{
if|if
condition|(
operator|!
name|path
operator|.
name|isFile
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|path
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
comment|//
operator|||
name|path
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".zip"
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
operator|&&
literal|0
operator|<
name|list
operator|.
name|length
condition|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|list
argument_list|,
operator|new
name|Comparator
argument_list|<
name|File
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|File
name|a
parameter_list|,
name|File
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|addToClassLoader
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addToClassLoader (final File[] additionalLocations)
specifier|private
name|void
name|addToClassLoader
parameter_list|(
specifier|final
name|File
index|[]
name|additionalLocations
parameter_list|)
block|{
specifier|final
name|ClassLoader
name|cl
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|cl
operator|instanceof
name|URLClassLoader
operator|)
condition|)
block|{
throw|throw
name|noAddURL
argument_list|(
literal|"Not loaded by URLClassLoader"
argument_list|,
literal|null
argument_list|)
throw|;
block|}
specifier|final
name|URLClassLoader
name|ucl
init|=
operator|(
name|URLClassLoader
operator|)
name|cl
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|URL
argument_list|>
name|have
init|=
operator|new
name|HashSet
argument_list|<
name|URL
argument_list|>
argument_list|()
decl_stmt|;
name|have
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|ucl
operator|.
name|getURLs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Method
name|m
decl_stmt|;
try|try
block|{
name|m
operator|=
name|URLClassLoader
operator|.
name|class
operator|.
name|getDeclaredMethod
argument_list|(
literal|"addURL"
argument_list|,
name|URL
operator|.
name|class
argument_list|)
expr_stmt|;
name|m
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
throw|throw
name|noAddURL
argument_list|(
literal|"Method addURL not available"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
name|noAddURL
argument_list|(
literal|"Method addURL not available"
argument_list|,
name|e
argument_list|)
throw|;
block|}
for|for
control|(
specifier|final
name|File
name|path
range|:
name|additionalLocations
control|)
block|{
try|try
block|{
specifier|final
name|URL
name|url
init|=
name|path
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
decl_stmt|;
if|if
condition|(
name|have
operator|.
name|add
argument_list|(
name|url
argument_list|)
condition|)
block|{
name|m
operator|.
name|invoke
argument_list|(
name|cl
argument_list|,
name|url
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
name|noAddURL
argument_list|(
literal|"addURL "
operator|+
name|path
operator|+
literal|" failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
name|noAddURL
argument_list|(
literal|"addURL "
operator|+
name|path
operator|+
literal|" failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
name|noAddURL
argument_list|(
literal|"addURL "
operator|+
name|path
operator|+
literal|" failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
name|noAddURL
argument_list|(
literal|"addURL "
operator|+
name|path
operator|+
literal|" failed"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|noAddURL (String m, Throwable why)
specifier|private
specifier|static
name|UnsupportedOperationException
name|noAddURL
parameter_list|(
name|String
name|m
parameter_list|,
name|Throwable
name|why
parameter_list|)
block|{
specifier|final
name|String
name|prefix
init|=
literal|"Cannot extend classpath: "
decl_stmt|;
return|return
operator|new
name|UnsupportedOperationException
argument_list|(
name|prefix
operator|+
name|m
argument_list|,
name|why
argument_list|)
return|;
block|}
comment|/** @return provides database connectivity and site path. */
DECL|method|createDbInjector (final DataSourceProvider.Context context)
specifier|protected
name|Injector
name|createDbInjector
parameter_list|(
specifier|final
name|DataSourceProvider
operator|.
name|Context
name|context
parameter_list|)
block|{
name|loadSiteLib
argument_list|()
expr_stmt|;
specifier|final
name|File
name|sitePath
init|=
name|getSitePath
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Module
argument_list|>
name|modules
init|=
operator|new
name|ArrayList
argument_list|<
name|Module
argument_list|>
argument_list|()
decl_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|AbstractModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|File
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|SitePath
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|sitePath
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|LifecycleModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|DataSourceProvider
operator|.
name|Context
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|DataSource
operator|.
name|class
argument_list|,
name|Names
operator|.
name|named
argument_list|(
literal|"ReviewDb"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|toProvider
argument_list|(
name|DataSourceProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|DataSourceProvider
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|GerritServerConfigModule
argument_list|()
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|DatabaseModule
argument_list|()
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|SchemaModule
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|Guice
operator|.
name|createInjector
argument_list|(
name|PRODUCTION
argument_list|,
name|modules
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|CreationException
name|ce
parameter_list|)
block|{
specifier|final
name|Message
name|first
init|=
name|ce
operator|.
name|getErrorMessages
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Throwable
name|why
init|=
name|first
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|why
operator|instanceof
name|SQLException
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"Cannot connect to SQL database"
argument_list|,
name|why
argument_list|)
throw|;
block|}
if|if
condition|(
name|why
operator|instanceof
name|OrmException
operator|&&
name|why
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
operator|&&
literal|"Unable to determine driver URL"
operator|.
name|equals
argument_list|(
name|why
operator|.
name|getMessage
argument_list|()
argument_list|)
condition|)
block|{
name|why
operator|=
name|why
operator|.
name|getCause
argument_list|()
expr_stmt|;
if|if
condition|(
name|isCannotCreatePoolException
argument_list|(
name|why
argument_list|)
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"Cannot connect to SQL database"
argument_list|,
name|why
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
throw|throw
name|die
argument_list|(
literal|"Cannot connect to SQL database"
argument_list|,
name|why
argument_list|)
throw|;
block|}
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|why
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|why
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|why
operator|=
name|why
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
name|first
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|why
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"\n  caused by "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|why
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|why
operator|=
name|why
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
throw|throw
name|die
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|RuntimeException
argument_list|(
literal|"DbInjector failed"
argument_list|,
name|ce
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|isCannotCreatePoolException (Throwable why)
specifier|private
specifier|static
name|boolean
name|isCannotCreatePoolException
parameter_list|(
name|Throwable
name|why
parameter_list|)
block|{
return|return
name|why
operator|instanceof
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|dbcp
operator|.
name|SQLNestedException
operator|&&
name|why
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
operator|&&
name|why
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Cannot create PoolableConnectionFactory"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

