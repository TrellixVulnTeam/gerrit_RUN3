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
DECL|package|com.google.gerrit.server.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|config
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
name|base
operator|.
name|MoreObjects
operator|.
name|firstNonNull
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
name|base
operator|.
name|Strings
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
name|GitWebType
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

begin_class
DECL|class|GitWebConfig
specifier|public
class|class
name|GitWebConfig
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
name|GitWebConfig
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|isDisabled (Config cfg)
specifier|public
specifier|static
name|boolean
name|isDisabled
parameter_list|(
name|Config
name|cfg
parameter_list|)
block|{
return|return
name|isEmptyString
argument_list|(
name|cfg
argument_list|,
literal|"gitweb"
argument_list|,
literal|null
argument_list|,
literal|"url"
argument_list|)
operator|||
name|isEmptyString
argument_list|(
name|cfg
argument_list|,
literal|"gitweb"
argument_list|,
literal|null
argument_list|,
literal|"cgi"
argument_list|)
return|;
block|}
DECL|method|isEmptyString (Config cfg, String section, String subsection, String name)
specifier|private
specifier|static
name|boolean
name|isEmptyString
parameter_list|(
name|Config
name|cfg
parameter_list|,
name|String
name|section
parameter_list|,
name|String
name|subsection
parameter_list|,
name|String
name|name
parameter_list|)
block|{
comment|// This is currently the only way to check for the empty string in a JGit
comment|// config. Fun!
name|String
index|[]
name|values
init|=
name|cfg
operator|.
name|getStringList
argument_list|(
name|section
argument_list|,
name|subsection
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|values
operator|.
name|length
operator|>
literal|0
operator|&&
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|values
index|[
literal|0
index|]
argument_list|)
return|;
block|}
DECL|field|url
specifier|private
specifier|final
name|String
name|url
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|GitWebType
name|type
decl_stmt|;
annotation|@
name|Inject
DECL|method|GitWebConfig (GitWebCgiConfig cgiConfig, @GerritServerConfig Config cfg)
name|GitWebConfig
parameter_list|(
name|GitWebCgiConfig
name|cgiConfig
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
if|if
condition|(
name|isDisabled
argument_list|(
name|cfg
argument_list|)
condition|)
block|{
name|type
operator|=
literal|null
expr_stmt|;
name|url
operator|=
literal|null
expr_stmt|;
return|return;
block|}
name|String
name|cfgUrl
init|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"gitweb"
argument_list|,
literal|null
argument_list|,
literal|"url"
argument_list|)
decl_stmt|;
name|GitWebType
name|type
init|=
name|GitWebType
operator|.
name|fromName
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"gitweb"
argument_list|,
literal|null
argument_list|,
literal|"type"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|type
operator|=
literal|null
expr_stmt|;
name|url
operator|=
literal|null
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|cgiConfig
operator|.
name|getGitwebCgi
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// Use an externally managed gitweb instance, and not an internal one.
name|url
operator|=
name|cfgUrl
expr_stmt|;
block|}
else|else
block|{
name|url
operator|=
name|firstNonNull
argument_list|(
name|cfgUrl
argument_list|,
literal|"gitweb"
argument_list|)
expr_stmt|;
block|}
name|type
operator|.
name|setLinkName
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"gitweb"
argument_list|,
literal|null
argument_list|,
literal|"linkname"
argument_list|)
argument_list|)
expr_stmt|;
name|type
operator|.
name|setBranch
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"gitweb"
argument_list|,
literal|null
argument_list|,
literal|"branch"
argument_list|)
argument_list|)
expr_stmt|;
name|type
operator|.
name|setProject
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"gitweb"
argument_list|,
literal|null
argument_list|,
literal|"project"
argument_list|)
argument_list|)
expr_stmt|;
name|type
operator|.
name|setRevision
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"gitweb"
argument_list|,
literal|null
argument_list|,
literal|"revision"
argument_list|)
argument_list|)
expr_stmt|;
name|type
operator|.
name|setRootTree
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"gitweb"
argument_list|,
literal|null
argument_list|,
literal|"roottree"
argument_list|)
argument_list|)
expr_stmt|;
name|type
operator|.
name|setFile
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"gitweb"
argument_list|,
literal|null
argument_list|,
literal|"file"
argument_list|)
argument_list|)
expr_stmt|;
name|type
operator|.
name|setFileHistory
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"gitweb"
argument_list|,
literal|null
argument_list|,
literal|"filehistory"
argument_list|)
argument_list|)
expr_stmt|;
name|type
operator|.
name|setLinkDrafts
argument_list|(
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"gitweb"
argument_list|,
literal|null
argument_list|,
literal|"linkdrafts"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|type
operator|.
name|setUrlEncode
argument_list|(
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"gitweb"
argument_list|,
literal|null
argument_list|,
literal|"urlencode"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|pathSeparator
init|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"gitweb"
argument_list|,
literal|null
argument_list|,
literal|"pathSeparator"
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathSeparator
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|pathSeparator
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
name|char
name|c
init|=
name|pathSeparator
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|isValidPathSeparator
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|type
operator|.
name|setPathSeparator
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Invalid value specified for gitweb.pathSeparator: "
operator|+
name|c
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Value specified for gitweb.pathSeparator is not a single character:"
operator|+
name|pathSeparator
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|type
operator|.
name|getBranch
argument_list|()
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No Pattern specified for gitweb.branch, disabling."
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|getProject
argument_list|()
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No Pattern specified for gitweb.project, disabling."
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|getRevision
argument_list|()
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No Pattern specified for gitweb.revision, disabling."
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|getRootTree
argument_list|()
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No Pattern specified for gitweb.roottree, disabling."
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|getFile
argument_list|()
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No Pattern specified for gitweb.file, disabling."
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|.
name|getFileHistory
argument_list|()
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No Pattern specified for gitweb.filehistory, disabling."
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
block|}
comment|/** @return GitWebType for gitweb viewer. */
DECL|method|getGitWebType ()
specifier|public
name|GitWebType
name|getGitWebType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**    * @return URL of the entry point into gitweb. This URL may be relative to our    *         context if gitweb is hosted by ourselves; or absolute if its hosted    *         elsewhere; or null if gitweb has not been configured.    */
DECL|method|getUrl ()
specifier|public
name|String
name|getUrl
parameter_list|()
block|{
return|return
name|url
return|;
block|}
comment|/**    * Determines if a given character can be used unencoded in an URL as a    * replacement for the path separator '/'.    *    * Reasoning: http://www.ietf.org/rfc/rfc1738.txt Â§ 2.2:    *    * ... only alphanumerics, the special characters "$-_.+!*'(),", and    *  reserved characters used for their reserved purposes may be used    * unencoded within a URL.    *    * The following characters might occur in file names, however:    *    * alphanumeric characters,    *    * "$-_.+!',"    */
DECL|method|isValidPathSeparator (char c)
specifier|static
name|boolean
name|isValidPathSeparator
parameter_list|(
name|char
name|c
parameter_list|)
block|{
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'*'
case|:
case|case
literal|'('
case|:
case|case
literal|')'
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

