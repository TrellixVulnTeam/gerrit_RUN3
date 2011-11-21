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
DECL|package|com.google.gerrit.httpd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
DECL|field|url
specifier|private
specifier|final
name|String
name|url
decl_stmt|;
DECL|field|gitweb_cgi
specifier|private
specifier|final
name|File
name|gitweb_cgi
decl_stmt|;
DECL|field|gitweb_css
specifier|private
specifier|final
name|File
name|gitweb_css
decl_stmt|;
DECL|field|gitweb_js
specifier|private
specifier|final
name|File
name|gitweb_js
decl_stmt|;
DECL|field|git_logo_png
specifier|private
specifier|final
name|File
name|git_logo_png
decl_stmt|;
DECL|field|type
specifier|private
name|GitWebType
name|type
decl_stmt|;
annotation|@
name|Inject
DECL|method|GitWebConfig (final SitePaths sitePaths, @GerritServerConfig final Config cfg)
name|GitWebConfig
parameter_list|(
specifier|final
name|SitePaths
name|sitePaths
parameter_list|,
annotation|@
name|GerritServerConfig
specifier|final
name|Config
name|cfg
parameter_list|)
block|{
specifier|final
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
specifier|final
name|String
name|cfgCgi
init|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"gitweb"
argument_list|,
literal|null
argument_list|,
literal|"cgi"
argument_list|)
decl_stmt|;
name|type
operator|=
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
expr_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|url
operator|=
literal|null
expr_stmt|;
name|gitweb_cgi
operator|=
literal|null
expr_stmt|;
name|gitweb_css
operator|=
literal|null
expr_stmt|;
name|gitweb_js
operator|=
literal|null
expr_stmt|;
name|git_logo_png
operator|=
literal|null
expr_stmt|;
return|return;
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
name|type
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|cfgUrl
operator|!=
literal|null
operator|&&
name|cfgUrl
operator|.
name|isEmpty
argument_list|()
operator|)
operator|||
operator|(
name|cfgCgi
operator|!=
literal|null
operator|&&
name|cfgCgi
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
comment|// Either setting was explicitly set to the empty string disabling
comment|// gitweb for this server. Disable the configuration.
comment|//
name|url
operator|=
literal|null
expr_stmt|;
name|gitweb_cgi
operator|=
literal|null
expr_stmt|;
name|gitweb_css
operator|=
literal|null
expr_stmt|;
name|gitweb_js
operator|=
literal|null
expr_stmt|;
name|git_logo_png
operator|=
literal|null
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|(
name|cfgUrl
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|cfgCgi
operator|==
literal|null
operator|||
name|cfgCgi
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
comment|// Use an externally managed gitweb instance, and not an internal one.
comment|//
name|url
operator|=
name|cfgUrl
expr_stmt|;
name|gitweb_cgi
operator|=
literal|null
expr_stmt|;
name|gitweb_css
operator|=
literal|null
expr_stmt|;
name|gitweb_js
operator|=
literal|null
expr_stmt|;
name|git_logo_png
operator|=
literal|null
expr_stmt|;
return|return;
block|}
specifier|final
name|File
name|pkgCgi
init|=
operator|new
name|File
argument_list|(
literal|"/usr/lib/cgi-bin/gitweb.cgi"
argument_list|)
decl_stmt|;
name|String
index|[]
name|resourcePaths
init|=
block|{
literal|"/usr/share/gitweb/static"
block|,
literal|"/usr/share/gitweb"
block|,
literal|"/var/www/static"
block|,
literal|"/var/www"
block|}
decl_stmt|;
name|File
name|cgi
decl_stmt|;
if|if
condition|(
name|cfgCgi
operator|!=
literal|null
condition|)
block|{
comment|// Use the CGI script configured by the administrator, failing if it
comment|// cannot be used as specified.
comment|//
name|cgi
operator|=
name|sitePaths
operator|.
name|resolve
argument_list|(
name|cfgCgi
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|cgi
operator|.
name|isFile
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot find gitweb.cgi: "
operator|+
name|cgi
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|cgi
operator|.
name|canExecute
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot execute gitweb.cgi: "
operator|+
name|cgi
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|cgi
operator|.
name|equals
argument_list|(
name|pkgCgi
argument_list|)
condition|)
block|{
comment|// Assume the administrator pointed us to the distribution,
comment|// which also has the corresponding CSS and logo file.
comment|//
name|String
name|absPath
init|=
name|cgi
operator|.
name|getParentFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|resourcePaths
operator|=
operator|new
name|String
index|[]
block|{
name|absPath
operator|+
literal|"/static"
block|,
name|absPath
block|}
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|pkgCgi
operator|.
name|isFile
argument_list|()
operator|&&
name|pkgCgi
operator|.
name|canExecute
argument_list|()
condition|)
block|{
comment|// Use the OS packaged CGI.
comment|//
name|log
operator|.
name|debug
argument_list|(
literal|"Assuming gitweb at "
operator|+
name|pkgCgi
argument_list|)
expr_stmt|;
name|cgi
operator|=
name|pkgCgi
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"gitweb not installed (no "
operator|+
name|pkgCgi
operator|+
literal|" found)"
argument_list|)
expr_stmt|;
name|cgi
operator|=
literal|null
expr_stmt|;
name|resourcePaths
operator|=
operator|new
name|String
index|[]
block|{}
expr_stmt|;
block|}
name|File
name|css
init|=
literal|null
decl_stmt|,
name|js
init|=
literal|null
decl_stmt|,
name|logo
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|resourcePaths
control|)
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|css
operator|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"gitweb.css"
argument_list|)
expr_stmt|;
name|js
operator|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"gitweb.js"
argument_list|)
expr_stmt|;
name|logo
operator|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"git-logo.png"
argument_list|)
expr_stmt|;
if|if
condition|(
name|css
operator|.
name|isFile
argument_list|()
operator|&&
name|logo
operator|.
name|isFile
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|cfgUrl
operator|==
literal|null
operator|||
name|cfgUrl
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|url
operator|=
name|cgi
operator|!=
literal|null
condition|?
literal|"gitweb"
else|:
literal|null
expr_stmt|;
block|}
else|else
block|{
name|url
operator|=
name|cgi
operator|!=
literal|null
condition|?
name|cfgUrl
else|:
literal|null
expr_stmt|;
block|}
name|gitweb_cgi
operator|=
name|cgi
expr_stmt|;
name|gitweb_css
operator|=
name|css
expr_stmt|;
name|gitweb_js
operator|=
name|js
expr_stmt|;
name|git_logo_png
operator|=
name|logo
expr_stmt|;
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
comment|/** @return local path to the CGI executable; null if we shouldn't execute. */
DECL|method|getGitwebCGI ()
specifier|public
name|File
name|getGitwebCGI
parameter_list|()
block|{
return|return
name|gitweb_cgi
return|;
block|}
comment|/** @return local path of the {@code gitweb.css} matching the CGI. */
DECL|method|getGitwebCSS ()
specifier|public
name|File
name|getGitwebCSS
parameter_list|()
block|{
return|return
name|gitweb_css
return|;
block|}
comment|/** @return local path of the {@code gitweb.js} for the CGI. */
DECL|method|getGitwebJS ()
specifier|public
name|File
name|getGitwebJS
parameter_list|()
block|{
return|return
name|gitweb_js
return|;
block|}
comment|/** @return local path of the {@code git-logo.png} for the CGI. */
DECL|method|getGitLogoPNG ()
specifier|public
name|File
name|getGitLogoPNG
parameter_list|()
block|{
return|return
name|git_logo_png
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

