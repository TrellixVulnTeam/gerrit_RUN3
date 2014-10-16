begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.plugins
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|plugins
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|plugins
operator|.
name|AutoRegisterUtil
operator|.
name|calculateBindAnnotation
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|plugins
operator|.
name|PluginGuiceEnvironment
operator|.
name|is
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|LinkedListMultimap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Multimap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|extensions
operator|.
name|annotations
operator|.
name|Export
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
name|extensions
operator|.
name|annotations
operator|.
name|ExtensionPoint
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
name|extensions
operator|.
name|annotations
operator|.
name|Listen
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
name|extensions
operator|.
name|webui
operator|.
name|JavaScriptPlugin
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
name|plugins
operator|.
name|PluginContentScanner
operator|.
name|ExtensionMetaData
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
name|Scopes
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
name|TypeLiteral
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|lang
operator|.
name|annotation
operator|.
name|Annotation
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
name|ParameterizedType
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
name|Map
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

begin_class
DECL|class|AutoRegisterModules
class|class
name|AutoRegisterModules
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AutoRegisterModules
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|pluginName
specifier|private
specifier|final
name|String
name|pluginName
decl_stmt|;
DECL|field|env
specifier|private
specifier|final
name|PluginGuiceEnvironment
name|env
decl_stmt|;
DECL|field|scanner
specifier|private
specifier|final
name|PluginContentScanner
name|scanner
decl_stmt|;
DECL|field|classLoader
specifier|private
specifier|final
name|ClassLoader
name|classLoader
decl_stmt|;
DECL|field|sshGen
specifier|private
specifier|final
name|ModuleGenerator
name|sshGen
decl_stmt|;
DECL|field|httpGen
specifier|private
specifier|final
name|HttpModuleGenerator
name|httpGen
decl_stmt|;
DECL|field|sysSingletons
specifier|private
name|Set
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|sysSingletons
decl_stmt|;
DECL|field|sysListen
specifier|private
name|Multimap
argument_list|<
name|TypeLiteral
argument_list|<
name|?
argument_list|>
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|sysListen
decl_stmt|;
DECL|field|sysModule
name|Module
name|sysModule
decl_stmt|;
DECL|field|sshModule
name|Module
name|sshModule
decl_stmt|;
DECL|field|httpModule
name|Module
name|httpModule
decl_stmt|;
DECL|method|AutoRegisterModules (String pluginName, PluginGuiceEnvironment env, PluginContentScanner scanner, ClassLoader classLoader)
name|AutoRegisterModules
parameter_list|(
name|String
name|pluginName
parameter_list|,
name|PluginGuiceEnvironment
name|env
parameter_list|,
name|PluginContentScanner
name|scanner
parameter_list|,
name|ClassLoader
name|classLoader
parameter_list|)
block|{
name|this
operator|.
name|pluginName
operator|=
name|pluginName
expr_stmt|;
name|this
operator|.
name|env
operator|=
name|env
expr_stmt|;
name|this
operator|.
name|scanner
operator|=
name|scanner
expr_stmt|;
name|this
operator|.
name|classLoader
operator|=
name|classLoader
expr_stmt|;
name|this
operator|.
name|sshGen
operator|=
name|env
operator|.
name|hasSshModule
argument_list|()
condition|?
name|env
operator|.
name|newSshModuleGenerator
argument_list|()
else|:
name|ModuleGenerator
operator|.
name|NOP
expr_stmt|;
name|this
operator|.
name|httpGen
operator|=
name|env
operator|.
name|hasHttpModule
argument_list|()
condition|?
name|env
operator|.
name|newHttpModuleGenerator
argument_list|()
else|:
name|ModuleGenerator
operator|.
name|NOP
expr_stmt|;
block|}
DECL|method|discover ()
name|AutoRegisterModules
name|discover
parameter_list|()
throws|throws
name|InvalidPluginException
block|{
name|sysSingletons
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|()
expr_stmt|;
name|sysListen
operator|=
name|LinkedListMultimap
operator|.
name|create
argument_list|()
expr_stmt|;
name|sshGen
operator|.
name|setPluginName
argument_list|(
name|pluginName
argument_list|)
expr_stmt|;
name|httpGen
operator|.
name|setPluginName
argument_list|(
name|pluginName
argument_list|)
expr_stmt|;
name|scan
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|sysSingletons
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|sysListen
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|sysModule
operator|=
name|makeSystemModule
argument_list|()
expr_stmt|;
block|}
name|sshModule
operator|=
name|sshGen
operator|.
name|create
argument_list|()
expr_stmt|;
name|httpModule
operator|=
name|httpGen
operator|.
name|create
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|makeSystemModule ()
specifier|private
name|Module
name|makeSystemModule
parameter_list|()
block|{
return|return
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
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
range|:
name|sysSingletons
control|)
block|{
name|bind
argument_list|(
name|clazz
argument_list|)
operator|.
name|in
argument_list|(
name|Scopes
operator|.
name|SINGLETON
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|TypeLiteral
argument_list|<
name|?
argument_list|>
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|e
range|:
name|sysListen
operator|.
name|entries
argument_list|()
control|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|TypeLiteral
argument_list|<
name|Object
argument_list|>
name|type
init|=
operator|(
name|TypeLiteral
argument_list|<
name|Object
argument_list|>
operator|)
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Class
argument_list|<
name|Object
argument_list|>
name|impl
init|=
operator|(
name|Class
argument_list|<
name|Object
argument_list|>
operator|)
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Annotation
name|n
init|=
name|calculateBindAnnotation
argument_list|(
name|impl
argument_list|)
decl_stmt|;
name|bind
argument_list|(
name|type
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|n
argument_list|)
operator|.
name|to
argument_list|(
name|impl
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
DECL|method|scan ()
specifier|private
name|void
name|scan
parameter_list|()
throws|throws
name|InvalidPluginException
block|{
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
argument_list|,
name|Iterable
argument_list|<
name|ExtensionMetaData
argument_list|>
argument_list|>
name|extensions
init|=
name|scanner
operator|.
name|scan
argument_list|(
name|pluginName
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|Export
operator|.
name|class
argument_list|,
name|Listen
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|ExtensionMetaData
name|export
range|:
name|extensions
operator|.
name|get
argument_list|(
name|Export
operator|.
name|class
argument_list|)
control|)
block|{
name|export
argument_list|(
name|export
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ExtensionMetaData
name|listener
range|:
name|extensions
operator|.
name|get
argument_list|(
name|Listen
operator|.
name|class
argument_list|)
control|)
block|{
name|listen
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
name|exportInitJs
argument_list|()
expr_stmt|;
block|}
DECL|method|exportInitJs ()
specifier|private
name|void
name|exportInitJs
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|scanner
operator|.
name|getEntry
argument_list|(
name|JavaScriptPlugin
operator|.
name|STATIC_INIT_JS
argument_list|)
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|httpGen
operator|.
name|export
argument_list|(
name|JavaScriptPlugin
operator|.
name|INIT_JS
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot access %s from plugin %s: "
operator|+
literal|"JavaScript auto-discovered plugin will not be registered"
argument_list|,
name|JavaScriptPlugin
operator|.
name|STATIC_INIT_JS
argument_list|,
name|pluginName
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|export (ExtensionMetaData def)
specifier|private
name|void
name|export
parameter_list|(
name|ExtensionMetaData
name|def
parameter_list|)
throws|throws
name|InvalidPluginException
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
decl_stmt|;
try|try
block|{
name|clazz
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|def
operator|.
name|className
argument_list|,
literal|false
argument_list|,
name|classLoader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|InvalidPluginException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot load %s with @Export(\"%s\")"
argument_list|,
name|def
operator|.
name|className
argument_list|,
name|def
operator|.
name|annotationValue
argument_list|)
argument_list|,
name|err
argument_list|)
throw|;
block|}
name|Export
name|export
init|=
name|clazz
operator|.
name|getAnnotation
argument_list|(
name|Export
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|export
operator|==
literal|null
condition|)
block|{
name|PluginLoader
operator|.
name|log
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"In plugin %s asm incorrectly parsed %s with @Export(\"%s\")"
argument_list|,
name|pluginName
argument_list|,
name|clazz
operator|.
name|getName
argument_list|()
argument_list|,
name|def
operator|.
name|annotationValue
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|is
argument_list|(
literal|"org.apache.sshd.server.Command"
argument_list|,
name|clazz
argument_list|)
condition|)
block|{
name|sshGen
operator|.
name|export
argument_list|(
name|export
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|is
argument_list|(
literal|"javax.servlet.http.HttpServlet"
argument_list|,
name|clazz
argument_list|)
condition|)
block|{
name|httpGen
operator|.
name|export
argument_list|(
name|export
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
name|listen
argument_list|(
name|clazz
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|cnt
init|=
name|sysListen
operator|.
name|size
argument_list|()
decl_stmt|;
name|listen
argument_list|(
name|clazz
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
if|if
condition|(
name|cnt
operator|==
name|sysListen
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// If no bindings were recorded, the extension isn't recognized.
throw|throw
operator|new
name|InvalidPluginException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Class %s with @Export(\"%s\") not supported"
argument_list|,
name|clazz
operator|.
name|getName
argument_list|()
argument_list|,
name|export
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|listen (ExtensionMetaData def)
specifier|private
name|void
name|listen
parameter_list|(
name|ExtensionMetaData
name|def
parameter_list|)
throws|throws
name|InvalidPluginException
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
decl_stmt|;
try|try
block|{
name|clazz
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|def
operator|.
name|className
argument_list|,
literal|false
argument_list|,
name|classLoader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|InvalidPluginException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot load %s with @Listen"
argument_list|,
name|def
operator|.
name|className
argument_list|)
argument_list|,
name|err
argument_list|)
throw|;
block|}
name|Listen
name|listen
init|=
name|clazz
operator|.
name|getAnnotation
argument_list|(
name|Listen
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|listen
operator|!=
literal|null
condition|)
block|{
name|listen
argument_list|(
name|clazz
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|PluginLoader
operator|.
name|log
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"In plugin %s asm incorrectly parsed %s with @Listen"
argument_list|,
name|pluginName
argument_list|,
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|listen (java.lang.reflect.Type type, Class<?> clazz)
specifier|private
name|void
name|listen
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
name|type
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|InvalidPluginException
block|{
while|while
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|rawType
decl_stmt|;
if|if
condition|(
name|type
operator|instanceof
name|ParameterizedType
condition|)
block|{
name|rawType
operator|=
call|(
name|Class
argument_list|<
name|?
argument_list|>
call|)
argument_list|(
operator|(
name|ParameterizedType
operator|)
name|type
argument_list|)
operator|.
name|getRawType
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|instanceof
name|Class
condition|)
block|{
name|rawType
operator|=
operator|(
name|Class
argument_list|<
name|?
argument_list|>
operator|)
name|type
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
if|if
condition|(
name|rawType
operator|.
name|getAnnotation
argument_list|(
name|ExtensionPoint
operator|.
name|class
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|TypeLiteral
argument_list|<
name|?
argument_list|>
name|tl
init|=
name|TypeLiteral
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|env
operator|.
name|hasDynamicItem
argument_list|(
name|tl
argument_list|)
condition|)
block|{
name|sysSingletons
operator|.
name|add
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
name|sysListen
operator|.
name|put
argument_list|(
name|tl
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
name|httpGen
operator|.
name|listen
argument_list|(
name|tl
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
name|sshGen
operator|.
name|listen
argument_list|(
name|tl
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|env
operator|.
name|hasDynamicSet
argument_list|(
name|tl
argument_list|)
condition|)
block|{
name|sysSingletons
operator|.
name|add
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
name|sysListen
operator|.
name|put
argument_list|(
name|tl
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
name|httpGen
operator|.
name|listen
argument_list|(
name|tl
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
name|sshGen
operator|.
name|listen
argument_list|(
name|tl
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|env
operator|.
name|hasDynamicMap
argument_list|(
name|tl
argument_list|)
condition|)
block|{
if|if
condition|(
name|clazz
operator|.
name|getAnnotation
argument_list|(
name|Export
operator|.
name|class
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidPluginException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Class %s requires @Export(\"name\") annotation for %s"
argument_list|,
name|clazz
operator|.
name|getName
argument_list|()
argument_list|,
name|rawType
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|sysSingletons
operator|.
name|add
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
name|sysListen
operator|.
name|put
argument_list|(
name|tl
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
name|httpGen
operator|.
name|listen
argument_list|(
name|tl
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
name|sshGen
operator|.
name|listen
argument_list|(
name|tl
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|InvalidPluginException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot register %s, server does not accept %s"
argument_list|,
name|clazz
operator|.
name|getName
argument_list|()
argument_list|,
name|rawType
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
return|return;
block|}
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
index|[]
name|interfaces
init|=
name|rawType
operator|.
name|getGenericInterfaces
argument_list|()
decl_stmt|;
if|if
condition|(
name|interfaces
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
name|i
range|:
name|interfaces
control|)
block|{
name|listen
argument_list|(
name|i
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
block|}
name|type
operator|=
name|rawType
operator|.
name|getGenericSuperclass
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

