begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
DECL|package|com.google.gerrit.client.download
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|download
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
name|client
operator|.
name|Gerrit
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
name|AccountGeneralPreferences
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
name|AccountGeneralPreferences
operator|.
name|DownloadScheme
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
name|AuthType
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|aria
operator|.
name|client
operator|.
name|Roles
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|event
operator|.
name|dom
operator|.
name|client
operator|.
name|ClickEvent
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|event
operator|.
name|dom
operator|.
name|client
operator|.
name|ClickHandler
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|client
operator|.
name|GWT
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|Window
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|Anchor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|Widget
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|common
operator|.
name|AsyncCallback
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|common
operator|.
name|VoidResult
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
name|Set
import|;
end_import

begin_class
DECL|class|DownloadUrlLink
specifier|public
class|class
name|DownloadUrlLink
extends|extends
name|Anchor
implements|implements
name|ClickHandler
block|{
DECL|class|DownloadRefUrlLink
specifier|public
specifier|static
class|class
name|DownloadRefUrlLink
extends|extends
name|DownloadUrlLink
block|{
DECL|field|projectName
specifier|protected
name|String
name|projectName
decl_stmt|;
DECL|field|ref
specifier|protected
name|String
name|ref
decl_stmt|;
DECL|method|DownloadRefUrlLink (DownloadScheme urlType, String text, String project, String ref)
specifier|protected
name|DownloadRefUrlLink
parameter_list|(
name|DownloadScheme
name|urlType
parameter_list|,
name|String
name|text
parameter_list|,
name|String
name|project
parameter_list|,
name|String
name|ref
parameter_list|)
block|{
name|super
argument_list|(
name|urlType
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|this
operator|.
name|projectName
operator|=
name|project
expr_stmt|;
name|this
operator|.
name|ref
operator|=
name|ref
expr_stmt|;
block|}
DECL|method|appendRef (StringBuilder r)
specifier|protected
name|void
name|appendRef
parameter_list|(
name|StringBuilder
name|r
parameter_list|)
block|{
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|AnonGitLink
specifier|public
specifier|static
class|class
name|AnonGitLink
extends|extends
name|DownloadRefUrlLink
block|{
DECL|method|AnonGitLink (String project, String ref)
specifier|public
name|AnonGitLink
parameter_list|(
name|String
name|project
parameter_list|,
name|String
name|ref
parameter_list|)
block|{
name|super
argument_list|(
name|DownloadScheme
operator|.
name|ANON_GIT
argument_list|,
name|Util
operator|.
name|M
operator|.
name|anonymousDownload
argument_list|(
literal|"Git"
argument_list|)
argument_list|,
name|project
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUrlData ()
specifier|public
name|String
name|getUrlData
parameter_list|()
block|{
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getGitDaemonUrl
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|appendRef
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|class|AnonHttpLink
specifier|public
specifier|static
class|class
name|AnonHttpLink
extends|extends
name|DownloadRefUrlLink
block|{
DECL|method|AnonHttpLink (String project, String ref)
specifier|public
name|AnonHttpLink
parameter_list|(
name|String
name|project
parameter_list|,
name|String
name|ref
parameter_list|)
block|{
name|super
argument_list|(
name|DownloadScheme
operator|.
name|ANON_HTTP
argument_list|,
name|Util
operator|.
name|M
operator|.
name|anonymousDownload
argument_list|(
literal|"HTTP"
argument_list|)
argument_list|,
name|project
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUrlData ()
specifier|public
name|String
name|getUrlData
parameter_list|()
block|{
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getGitHttpUrl
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getGitHttpUrl
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|r
operator|.
name|append
argument_list|(
name|hostPageUrl
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|append
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|appendRef
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|class|SshLink
specifier|public
specifier|static
class|class
name|SshLink
extends|extends
name|DownloadRefUrlLink
block|{
DECL|method|SshLink (String project, String ref)
specifier|public
name|SshLink
parameter_list|(
name|String
name|project
parameter_list|,
name|String
name|ref
parameter_list|)
block|{
name|super
argument_list|(
name|DownloadScheme
operator|.
name|SSH
argument_list|,
literal|"SSH"
argument_list|,
name|project
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUrlData ()
specifier|public
name|String
name|getUrlData
parameter_list|()
block|{
name|String
name|sshAddr
init|=
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getSshdAddress
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"ssh://"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"@"
argument_list|)
expr_stmt|;
if|if
condition|(
name|sshAddr
operator|.
name|startsWith
argument_list|(
literal|"*:"
argument_list|)
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|sshAddr
argument_list|)
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
name|Window
operator|.
name|Location
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sshAddr
operator|.
name|startsWith
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|sshAddr
operator|=
name|sshAddr
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|append
argument_list|(
name|sshAddr
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|appendRef
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|class|HttpLink
specifier|public
specifier|static
class|class
name|HttpLink
extends|extends
name|DownloadRefUrlLink
block|{
DECL|field|anonymous
specifier|protected
name|boolean
name|anonymous
decl_stmt|;
DECL|method|HttpLink (String project, String ref, boolean anonymous)
specifier|public
name|HttpLink
parameter_list|(
name|String
name|project
parameter_list|,
name|String
name|ref
parameter_list|,
name|boolean
name|anonymous
parameter_list|)
block|{
name|super
argument_list|(
name|DownloadScheme
operator|.
name|HTTP
argument_list|,
literal|"HTTP"
argument_list|,
name|project
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|this
operator|.
name|anonymous
operator|=
name|anonymous
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUrlData ()
specifier|public
name|String
name|getUrlData
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getGitHttpUrl
argument_list|()
operator|!=
literal|null
operator|&&
operator|(
name|anonymous
operator|||
name|siteReliesOnHttp
argument_list|()
operator|)
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getGitHttpUrl
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|base
init|=
name|hostPageUrl
decl_stmt|;
name|int
name|p
init|=
name|base
operator|.
name|indexOf
argument_list|(
literal|"://"
argument_list|)
decl_stmt|;
name|int
name|s
init|=
name|base
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|,
name|p
operator|+
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|<
literal|0
condition|)
block|{
name|s
operator|=
name|base
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|String
name|host
init|=
name|base
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|3
argument_list|,
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|host
operator|.
name|contains
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
name|host
operator|=
name|host
operator|.
name|substring
argument_list|(
name|host
operator|.
name|indexOf
argument_list|(
literal|'@'
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|append
argument_list|(
name|base
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
operator|+
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|'@'
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|base
operator|.
name|substring
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|append
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|appendRef
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|method|siteReliesOnHttp ()
specifier|public
specifier|static
name|boolean
name|siteReliesOnHttp
parameter_list|()
block|{
return|return
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getGitHttpUrl
argument_list|()
operator|!=
literal|null
operator|&&
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getAuthType
argument_list|()
operator|==
name|AuthType
operator|.
name|CUSTOM_EXTENSION
operator|&&
operator|!
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|siteHasUsernames
argument_list|()
return|;
block|}
DECL|method|createDownloadUrlLinks (String project, String ref, boolean allowAnonymous)
specifier|public
specifier|static
name|List
argument_list|<
name|DownloadUrlLink
argument_list|>
name|createDownloadUrlLinks
parameter_list|(
name|String
name|project
parameter_list|,
name|String
name|ref
parameter_list|,
name|boolean
name|allowAnonymous
parameter_list|)
block|{
name|List
argument_list|<
name|DownloadUrlLink
argument_list|>
name|urls
init|=
operator|new
name|ArrayList
argument_list|<
name|DownloadUrlLink
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|DownloadScheme
argument_list|>
name|allowedSchemes
init|=
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getDownloadSchemes
argument_list|()
decl_stmt|;
if|if
condition|(
name|allowAnonymous
operator|&&
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getGitDaemonUrl
argument_list|()
operator|!=
literal|null
operator|&&
operator|(
name|allowedSchemes
operator|.
name|contains
argument_list|(
name|DownloadScheme
operator|.
name|ANON_GIT
argument_list|)
operator|||
name|allowedSchemes
operator|.
name|contains
argument_list|(
name|DownloadScheme
operator|.
name|DEFAULT_DOWNLOADS
argument_list|)
operator|)
condition|)
block|{
name|urls
operator|.
name|add
argument_list|(
operator|new
name|DownloadUrlLink
operator|.
name|AnonGitLink
argument_list|(
name|project
argument_list|,
name|ref
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allowAnonymous
operator|&&
operator|(
name|allowedSchemes
operator|.
name|contains
argument_list|(
name|DownloadScheme
operator|.
name|ANON_HTTP
argument_list|)
operator|||
name|allowedSchemes
operator|.
name|contains
argument_list|(
name|DownloadScheme
operator|.
name|DEFAULT_DOWNLOADS
argument_list|)
operator|)
condition|)
block|{
name|urls
operator|.
name|add
argument_list|(
operator|new
name|DownloadUrlLink
operator|.
name|AnonHttpLink
argument_list|(
name|project
argument_list|,
name|ref
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getSshdAddress
argument_list|()
operator|!=
literal|null
operator|&&
name|hasUserName
argument_list|()
operator|&&
operator|(
name|allowedSchemes
operator|.
name|contains
argument_list|(
name|DownloadScheme
operator|.
name|SSH
argument_list|)
operator|||
name|allowedSchemes
operator|.
name|contains
argument_list|(
name|DownloadScheme
operator|.
name|DEFAULT_DOWNLOADS
argument_list|)
operator|)
condition|)
block|{
name|urls
operator|.
name|add
argument_list|(
operator|new
name|DownloadUrlLink
operator|.
name|SshLink
argument_list|(
name|project
argument_list|,
name|ref
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|hasUserName
argument_list|()
operator|||
name|siteReliesOnHttp
argument_list|()
operator|)
operator|&&
operator|(
name|allowedSchemes
operator|.
name|contains
argument_list|(
name|DownloadScheme
operator|.
name|HTTP
argument_list|)
operator|||
name|allowedSchemes
operator|.
name|contains
argument_list|(
name|DownloadScheme
operator|.
name|DEFAULT_DOWNLOADS
argument_list|)
operator|)
condition|)
block|{
name|urls
operator|.
name|add
argument_list|(
operator|new
name|DownloadUrlLink
operator|.
name|HttpLink
argument_list|(
name|project
argument_list|,
name|ref
argument_list|,
name|allowAnonymous
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|urls
return|;
block|}
DECL|method|hasUserName ()
specifier|private
specifier|static
name|boolean
name|hasUserName
parameter_list|()
block|{
return|return
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
operator|&&
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getUserName
argument_list|()
operator|!=
literal|null
operator|&&
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getUserName
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
return|;
block|}
DECL|field|urlType
specifier|protected
name|DownloadScheme
name|urlType
decl_stmt|;
DECL|field|urlData
specifier|protected
name|String
name|urlData
decl_stmt|;
DECL|field|hostPageUrl
specifier|protected
name|String
name|hostPageUrl
init|=
name|GWT
operator|.
name|getHostPageBaseURL
argument_list|()
decl_stmt|;
DECL|method|DownloadUrlLink (DownloadScheme urlType, String text, String urlData)
specifier|public
name|DownloadUrlLink
parameter_list|(
name|DownloadScheme
name|urlType
parameter_list|,
name|String
name|text
parameter_list|,
name|String
name|urlData
parameter_list|)
block|{
name|this
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|this
operator|.
name|urlType
operator|=
name|urlType
expr_stmt|;
name|this
operator|.
name|urlData
operator|=
name|urlData
expr_stmt|;
block|}
DECL|method|DownloadUrlLink (DownloadScheme urlType, String text)
specifier|public
name|DownloadUrlLink
parameter_list|(
name|DownloadScheme
name|urlType
parameter_list|,
name|String
name|text
parameter_list|)
block|{
name|this
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|this
operator|.
name|urlType
operator|=
name|urlType
expr_stmt|;
block|}
DECL|method|DownloadUrlLink (String text)
specifier|public
name|DownloadUrlLink
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|super
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|setStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|downloadLink
argument_list|()
argument_list|)
expr_stmt|;
name|Roles
operator|.
name|getTabRole
argument_list|()
operator|.
name|set
argument_list|(
name|getElement
argument_list|()
argument_list|)
expr_stmt|;
name|addClickHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|hostPageUrl
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|hostPageUrl
operator|+=
literal|"/"
expr_stmt|;
block|}
block|}
DECL|method|getUrlData ()
specifier|public
name|String
name|getUrlData
parameter_list|()
block|{
return|return
name|urlData
return|;
block|}
annotation|@
name|Override
DECL|method|onClick (ClickEvent event)
specifier|public
name|void
name|onClick
parameter_list|(
name|ClickEvent
name|event
parameter_list|)
block|{
name|event
operator|.
name|preventDefault
argument_list|()
expr_stmt|;
name|event
operator|.
name|stopPropagation
argument_list|()
expr_stmt|;
name|select
argument_list|()
expr_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
comment|// If the user is signed-in, remember this choice for future panels.
comment|//
name|AccountGeneralPreferences
name|pref
init|=
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getGeneralPreferences
argument_list|()
decl_stmt|;
name|pref
operator|.
name|setDownloadUrl
argument_list|(
name|urlType
argument_list|)
expr_stmt|;
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|account
operator|.
name|Util
operator|.
name|ACCOUNT_SVC
operator|.
name|changePreferences
argument_list|(
name|pref
argument_list|,
operator|new
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|caught
parameter_list|)
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|VoidResult
name|result
parameter_list|)
block|{             }
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|select ()
name|void
name|select
parameter_list|()
block|{
name|DownloadUrlPanel
name|parent
init|=
operator|(
name|DownloadUrlPanel
operator|)
name|getParent
argument_list|()
decl_stmt|;
for|for
control|(
name|Widget
name|w
range|:
name|parent
control|)
block|{
if|if
condition|(
name|w
operator|!=
name|this
operator|&&
name|w
operator|instanceof
name|DownloadUrlLink
condition|)
block|{
name|w
operator|.
name|removeStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|downloadLink_Active
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|parent
operator|.
name|setCurrentUrl
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|addStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|downloadLink_Active
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

