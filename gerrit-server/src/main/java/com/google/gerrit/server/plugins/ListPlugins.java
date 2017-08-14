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
name|java
operator|.
name|util
operator|.
name|Comparator
operator|.
name|comparing
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
name|Streams
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
name|data
operator|.
name|GlobalCapability
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
name|RequiresCapability
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
name|common
operator|.
name|PluginInfo
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
name|restapi
operator|.
name|BadRequestException
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
name|restapi
operator|.
name|RestReadView
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
name|restapi
operator|.
name|TopLevelResource
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
name|restapi
operator|.
name|Url
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
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
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

begin_comment
comment|/** List the installed plugins. */
end_comment

begin_class
annotation|@
name|RequiresCapability
argument_list|(
name|GlobalCapability
operator|.
name|VIEW_PLUGINS
argument_list|)
DECL|class|ListPlugins
specifier|public
class|class
name|ListPlugins
implements|implements
name|RestReadView
argument_list|<
name|TopLevelResource
argument_list|>
block|{
DECL|field|pluginLoader
specifier|private
specifier|final
name|PluginLoader
name|pluginLoader
decl_stmt|;
DECL|field|all
specifier|private
name|boolean
name|all
decl_stmt|;
DECL|field|limit
specifier|private
name|int
name|limit
decl_stmt|;
DECL|field|start
specifier|private
name|int
name|start
decl_stmt|;
DECL|field|matchPrefix
specifier|private
name|String
name|matchPrefix
decl_stmt|;
DECL|field|matchSubstring
specifier|private
name|String
name|matchSubstring
decl_stmt|;
DECL|field|matchRegex
specifier|private
name|String
name|matchRegex
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--all"
argument_list|,
name|aliases
operator|=
block|{
literal|"-a"
block|}
argument_list|,
name|usage
operator|=
literal|"List all plugins, including disabled plugins"
argument_list|)
DECL|method|setAll (boolean all)
specifier|public
name|void
name|setAll
parameter_list|(
name|boolean
name|all
parameter_list|)
block|{
name|this
operator|.
name|all
operator|=
name|all
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--limit"
argument_list|,
name|aliases
operator|=
block|{
literal|"-n"
block|}
argument_list|,
name|metaVar
operator|=
literal|"CNT"
argument_list|,
name|usage
operator|=
literal|"maximum number of plugins to list"
argument_list|)
DECL|method|setLimit (int limit)
specifier|public
name|void
name|setLimit
parameter_list|(
name|int
name|limit
parameter_list|)
block|{
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--start"
argument_list|,
name|aliases
operator|=
block|{
literal|"-S"
block|}
argument_list|,
name|metaVar
operator|=
literal|"CNT"
argument_list|,
name|usage
operator|=
literal|"number of plugins to skip"
argument_list|)
DECL|method|setStart (int start)
specifier|public
name|void
name|setStart
parameter_list|(
name|int
name|start
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--prefix"
argument_list|,
name|aliases
operator|=
block|{
literal|"-p"
block|}
argument_list|,
name|metaVar
operator|=
literal|"PREFIX"
argument_list|,
name|usage
operator|=
literal|"match plugin prefix"
argument_list|)
DECL|method|setMatchPrefix (String matchPrefix)
specifier|public
name|void
name|setMatchPrefix
parameter_list|(
name|String
name|matchPrefix
parameter_list|)
block|{
name|this
operator|.
name|matchPrefix
operator|=
name|matchPrefix
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--match"
argument_list|,
name|aliases
operator|=
block|{
literal|"-m"
block|}
argument_list|,
name|metaVar
operator|=
literal|"MATCH"
argument_list|,
name|usage
operator|=
literal|"match plugin substring"
argument_list|)
DECL|method|setMatchSubstring (String matchSubstring)
specifier|public
name|void
name|setMatchSubstring
parameter_list|(
name|String
name|matchSubstring
parameter_list|)
block|{
name|this
operator|.
name|matchSubstring
operator|=
name|matchSubstring
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"-r"
argument_list|,
name|metaVar
operator|=
literal|"REGEX"
argument_list|,
name|usage
operator|=
literal|"match plugin regex"
argument_list|)
DECL|method|setMatchRegex (String matchRegex)
specifier|public
name|void
name|setMatchRegex
parameter_list|(
name|String
name|matchRegex
parameter_list|)
block|{
name|this
operator|.
name|matchRegex
operator|=
name|matchRegex
expr_stmt|;
block|}
annotation|@
name|Inject
DECL|method|ListPlugins (PluginLoader pluginLoader)
specifier|protected
name|ListPlugins
parameter_list|(
name|PluginLoader
name|pluginLoader
parameter_list|)
block|{
name|this
operator|.
name|pluginLoader
operator|=
name|pluginLoader
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (TopLevelResource resource)
specifier|public
name|SortedMap
argument_list|<
name|String
argument_list|,
name|PluginInfo
argument_list|>
name|apply
parameter_list|(
name|TopLevelResource
name|resource
parameter_list|)
throws|throws
name|BadRequestException
block|{
name|Stream
argument_list|<
name|Plugin
argument_list|>
name|s
init|=
name|Streams
operator|.
name|stream
argument_list|(
name|pluginLoader
operator|.
name|getPlugins
argument_list|(
name|all
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchPrefix
operator|!=
literal|null
condition|)
block|{
name|checkMatchOptions
argument_list|(
name|matchSubstring
operator|==
literal|null
operator|&&
name|matchRegex
operator|==
literal|null
argument_list|)
expr_stmt|;
name|s
operator|=
name|s
operator|.
name|filter
argument_list|(
name|p
lambda|->
name|p
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|matchPrefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|matchSubstring
operator|!=
literal|null
condition|)
block|{
name|checkMatchOptions
argument_list|(
name|matchPrefix
operator|==
literal|null
operator|&&
name|matchRegex
operator|==
literal|null
argument_list|)
expr_stmt|;
name|String
name|substring
init|=
name|matchSubstring
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|s
operator|=
name|s
operator|.
name|filter
argument_list|(
name|p
lambda|->
name|p
operator|.
name|getName
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
operator|.
name|contains
argument_list|(
name|substring
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|matchRegex
operator|!=
literal|null
condition|)
block|{
name|checkMatchOptions
argument_list|(
name|matchPrefix
operator|==
literal|null
operator|&&
name|matchSubstring
operator|==
literal|null
argument_list|)
expr_stmt|;
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|matchRegex
argument_list|)
decl_stmt|;
name|s
operator|=
name|s
operator|.
name|filter
argument_list|(
name|p
lambda|->
name|pattern
operator|.
name|matcher
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|s
operator|=
name|s
operator|.
name|sorted
argument_list|(
name|comparing
argument_list|(
name|Plugin
operator|::
name|getName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|start
operator|>
literal|0
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|skip
argument_list|(
name|start
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|limit
operator|>
literal|0
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|limit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TreeMap
argument_list|<>
argument_list|(
name|s
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toMap
argument_list|(
name|p
lambda|->
name|p
operator|.
name|getName
argument_list|()
argument_list|,
name|p
lambda|->
name|toPluginInfo
argument_list|(
name|p
argument_list|)
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|checkMatchOptions (boolean cond)
specifier|private
name|void
name|checkMatchOptions
parameter_list|(
name|boolean
name|cond
parameter_list|)
throws|throws
name|BadRequestException
block|{
if|if
condition|(
operator|!
name|cond
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"specify exactly one of p/m/r"
argument_list|)
throw|;
block|}
block|}
DECL|method|toPluginInfo (Plugin p)
specifier|public
specifier|static
name|PluginInfo
name|toPluginInfo
parameter_list|(
name|Plugin
name|p
parameter_list|)
block|{
name|String
name|id
decl_stmt|;
name|String
name|version
decl_stmt|;
name|String
name|indexUrl
decl_stmt|;
name|String
name|filename
decl_stmt|;
name|Boolean
name|disabled
decl_stmt|;
name|id
operator|=
name|Url
operator|.
name|encode
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|version
operator|=
name|p
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|disabled
operator|=
name|p
operator|.
name|isDisabled
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|getSrcFile
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|indexUrl
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"plugins/%s/"
argument_list|,
name|p
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|filename
operator|=
name|p
operator|.
name|getSrcFile
argument_list|()
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|indexUrl
operator|=
literal|null
expr_stmt|;
name|filename
operator|=
literal|null
expr_stmt|;
block|}
return|return
operator|new
name|PluginInfo
argument_list|(
name|id
argument_list|,
name|version
argument_list|,
name|indexUrl
argument_list|,
name|filename
argument_list|,
name|disabled
argument_list|)
return|;
block|}
block|}
end_class

end_unit

