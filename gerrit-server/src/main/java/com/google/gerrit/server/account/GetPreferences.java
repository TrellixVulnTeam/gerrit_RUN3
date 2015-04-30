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

begin_package
DECL|package|com.google.gerrit.server.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|account
package|;
end_package

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
name|extensions
operator|.
name|restapi
operator|.
name|AuthException
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
name|ResourceNotFoundException
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
name|webui
operator|.
name|TopMenu
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
name|Account
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
name|DateFormat
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
name|DiffView
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
name|DownloadCommand
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
name|AccountGeneralPreferences
operator|.
name|ReviewCategoryStrategy
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
name|TimeFormat
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
name|gerrit
operator|.
name|server
operator|.
name|CurrentUser
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
name|AllUsersName
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
name|git
operator|.
name|GitRepositoryManager
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
name|Provider
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
name|Singleton
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
name|ConfigInvalidException
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
name|Repository
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

begin_class
annotation|@
name|Singleton
DECL|class|GetPreferences
specifier|public
class|class
name|GetPreferences
implements|implements
name|RestReadView
argument_list|<
name|AccountResource
argument_list|>
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
name|GetPreferences
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MY
specifier|public
specifier|static
specifier|final
name|String
name|MY
init|=
literal|"my"
decl_stmt|;
DECL|field|KEY_URL
specifier|public
specifier|static
specifier|final
name|String
name|KEY_URL
init|=
literal|"url"
decl_stmt|;
DECL|field|KEY_TARGET
specifier|public
specifier|static
specifier|final
name|String
name|KEY_TARGET
init|=
literal|"target"
decl_stmt|;
DECL|field|KEY_ID
specifier|public
specifier|static
specifier|final
name|String
name|KEY_ID
init|=
literal|"id"
decl_stmt|;
DECL|field|self
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|field|gitMgr
specifier|private
specifier|final
name|GitRepositoryManager
name|gitMgr
decl_stmt|;
annotation|@
name|Inject
DECL|method|GetPreferences (Provider<CurrentUser> self, Provider<ReviewDb> db, AllUsersName allUsersName, GitRepositoryManager gitMgr)
name|GetPreferences
parameter_list|(
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|AllUsersName
name|allUsersName
parameter_list|,
name|GitRepositoryManager
name|gitMgr
parameter_list|)
block|{
name|this
operator|.
name|self
operator|=
name|self
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
name|this
operator|.
name|gitMgr
operator|=
name|gitMgr
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (AccountResource rsrc)
specifier|public
name|PreferenceInfo
name|apply
parameter_list|(
name|AccountResource
name|rsrc
parameter_list|)
throws|throws
name|AuthException
throws|,
name|ResourceNotFoundException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
if|if
condition|(
name|self
operator|.
name|get
argument_list|()
operator|!=
name|rsrc
operator|.
name|getUser
argument_list|()
operator|&&
operator|!
name|self
operator|.
name|get
argument_list|()
operator|.
name|getCapabilities
argument_list|()
operator|.
name|canAdministrateServer
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"restricted to administrator"
argument_list|)
throw|;
block|}
name|Account
name|a
init|=
name|db
operator|.
name|get
argument_list|()
operator|.
name|accounts
argument_list|()
operator|.
name|get
argument_list|(
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|()
throw|;
block|}
name|Repository
name|git
init|=
name|gitMgr
operator|.
name|openRepository
argument_list|(
name|allUsersName
argument_list|)
decl_stmt|;
try|try
block|{
name|VersionedAccountPreferences
name|p
init|=
name|VersionedAccountPreferences
operator|.
name|forUser
argument_list|(
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
decl_stmt|;
name|p
operator|.
name|load
argument_list|(
name|git
argument_list|)
expr_stmt|;
return|return
operator|new
name|PreferenceInfo
argument_list|(
name|a
operator|.
name|getGeneralPreferences
argument_list|()
argument_list|,
name|p
argument_list|,
name|git
argument_list|)
return|;
block|}
finally|finally
block|{
name|git
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|PreferenceInfo
specifier|public
specifier|static
class|class
name|PreferenceInfo
block|{
DECL|field|changesPerPage
name|Short
name|changesPerPage
decl_stmt|;
DECL|field|showSiteHeader
name|Boolean
name|showSiteHeader
decl_stmt|;
DECL|field|useFlashClipboard
name|Boolean
name|useFlashClipboard
decl_stmt|;
DECL|field|downloadScheme
name|DownloadScheme
name|downloadScheme
decl_stmt|;
DECL|field|downloadCommand
name|DownloadCommand
name|downloadCommand
decl_stmt|;
DECL|field|copySelfOnEmail
name|Boolean
name|copySelfOnEmail
decl_stmt|;
DECL|field|dateFormat
name|DateFormat
name|dateFormat
decl_stmt|;
DECL|field|timeFormat
name|TimeFormat
name|timeFormat
decl_stmt|;
DECL|field|relativeDateInChangeTable
name|Boolean
name|relativeDateInChangeTable
decl_stmt|;
DECL|field|sizeBarInChangeTable
name|Boolean
name|sizeBarInChangeTable
decl_stmt|;
DECL|field|legacycidInChangeTable
name|Boolean
name|legacycidInChangeTable
decl_stmt|;
DECL|field|muteCommonPathPrefixes
name|Boolean
name|muteCommonPathPrefixes
decl_stmt|;
DECL|field|reviewCategoryStrategy
name|ReviewCategoryStrategy
name|reviewCategoryStrategy
decl_stmt|;
DECL|field|diffView
name|DiffView
name|diffView
decl_stmt|;
DECL|field|my
name|List
argument_list|<
name|TopMenu
operator|.
name|MenuItem
argument_list|>
name|my
decl_stmt|;
DECL|method|PreferenceInfo (AccountGeneralPreferences p, VersionedAccountPreferences v, Repository allUsers)
specifier|public
name|PreferenceInfo
parameter_list|(
name|AccountGeneralPreferences
name|p
parameter_list|,
name|VersionedAccountPreferences
name|v
parameter_list|,
name|Repository
name|allUsers
parameter_list|)
block|{
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|changesPerPage
operator|=
name|p
operator|.
name|getMaximumPageSize
argument_list|()
expr_stmt|;
name|showSiteHeader
operator|=
name|p
operator|.
name|isShowSiteHeader
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|useFlashClipboard
operator|=
name|p
operator|.
name|isUseFlashClipboard
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|downloadScheme
operator|=
name|p
operator|.
name|getDownloadUrl
argument_list|()
expr_stmt|;
name|downloadCommand
operator|=
name|p
operator|.
name|getDownloadCommand
argument_list|()
expr_stmt|;
name|copySelfOnEmail
operator|=
name|p
operator|.
name|isCopySelfOnEmails
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|dateFormat
operator|=
name|p
operator|.
name|getDateFormat
argument_list|()
expr_stmt|;
name|timeFormat
operator|=
name|p
operator|.
name|getTimeFormat
argument_list|()
expr_stmt|;
name|relativeDateInChangeTable
operator|=
name|p
operator|.
name|isRelativeDateInChangeTable
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|sizeBarInChangeTable
operator|=
name|p
operator|.
name|isSizeBarInChangeTable
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|legacycidInChangeTable
operator|=
name|p
operator|.
name|isLegacycidInChangeTable
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|muteCommonPathPrefixes
operator|=
name|p
operator|.
name|isMuteCommonPathPrefixes
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|reviewCategoryStrategy
operator|=
name|p
operator|.
name|getReviewCategoryStrategy
argument_list|()
expr_stmt|;
name|diffView
operator|=
name|p
operator|.
name|getDiffView
argument_list|()
expr_stmt|;
block|}
name|my
operator|=
name|my
argument_list|(
name|v
argument_list|,
name|allUsers
argument_list|)
expr_stmt|;
block|}
DECL|method|my (VersionedAccountPreferences v, Repository allUsers)
specifier|private
name|List
argument_list|<
name|TopMenu
operator|.
name|MenuItem
argument_list|>
name|my
parameter_list|(
name|VersionedAccountPreferences
name|v
parameter_list|,
name|Repository
name|allUsers
parameter_list|)
block|{
name|List
argument_list|<
name|TopMenu
operator|.
name|MenuItem
argument_list|>
name|my
init|=
name|my
argument_list|(
name|v
argument_list|)
decl_stmt|;
if|if
condition|(
name|my
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|v
operator|.
name|isDefaults
argument_list|()
condition|)
block|{
try|try
block|{
name|VersionedAccountPreferences
name|d
init|=
name|VersionedAccountPreferences
operator|.
name|forDefault
argument_list|()
decl_stmt|;
name|d
operator|.
name|load
argument_list|(
name|allUsers
argument_list|)
expr_stmt|;
name|my
operator|=
name|my
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"cannot read default preferences"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|my
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|my
operator|.
name|add
argument_list|(
operator|new
name|TopMenu
operator|.
name|MenuItem
argument_list|(
literal|"Changes"
argument_list|,
literal|"#/dashboard/self"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|my
operator|.
name|add
argument_list|(
operator|new
name|TopMenu
operator|.
name|MenuItem
argument_list|(
literal|"Drafts"
argument_list|,
literal|"#/q/owner:self+is:draft"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|my
operator|.
name|add
argument_list|(
operator|new
name|TopMenu
operator|.
name|MenuItem
argument_list|(
literal|"Draft Comments"
argument_list|,
literal|"#/q/has:draft"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|my
operator|.
name|add
argument_list|(
operator|new
name|TopMenu
operator|.
name|MenuItem
argument_list|(
literal|"Edits"
argument_list|,
literal|"#/q/has:edit"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|my
operator|.
name|add
argument_list|(
operator|new
name|TopMenu
operator|.
name|MenuItem
argument_list|(
literal|"Watched Changes"
argument_list|,
literal|"#/q/is:watched+is:open"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|my
operator|.
name|add
argument_list|(
operator|new
name|TopMenu
operator|.
name|MenuItem
argument_list|(
literal|"Starred Changes"
argument_list|,
literal|"#/q/is:starred"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|my
operator|.
name|add
argument_list|(
operator|new
name|TopMenu
operator|.
name|MenuItem
argument_list|(
literal|"Groups"
argument_list|,
literal|"#/groups/self"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|my
return|;
block|}
DECL|method|my (VersionedAccountPreferences v)
specifier|private
name|List
argument_list|<
name|TopMenu
operator|.
name|MenuItem
argument_list|>
name|my
parameter_list|(
name|VersionedAccountPreferences
name|v
parameter_list|)
block|{
name|List
argument_list|<
name|TopMenu
operator|.
name|MenuItem
argument_list|>
name|my
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Config
name|cfg
init|=
name|v
operator|.
name|getConfig
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|subsection
range|:
name|cfg
operator|.
name|getSubsections
argument_list|(
name|MY
argument_list|)
control|)
block|{
name|String
name|url
init|=
name|my
argument_list|(
name|cfg
argument_list|,
name|subsection
argument_list|,
name|KEY_URL
argument_list|,
literal|"#/"
argument_list|)
decl_stmt|;
name|String
name|target
init|=
name|my
argument_list|(
name|cfg
argument_list|,
name|subsection
argument_list|,
name|KEY_TARGET
argument_list|,
name|url
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|?
literal|null
else|:
literal|"_blank"
argument_list|)
decl_stmt|;
name|my
operator|.
name|add
argument_list|(
operator|new
name|TopMenu
operator|.
name|MenuItem
argument_list|(
name|subsection
argument_list|,
name|url
argument_list|,
name|target
argument_list|,
name|my
argument_list|(
name|cfg
argument_list|,
name|subsection
argument_list|,
name|KEY_ID
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|my
return|;
block|}
DECL|method|my (Config cfg, String subsection, String key, String defaultValue)
specifier|private
specifier|static
name|String
name|my
parameter_list|(
name|Config
name|cfg
parameter_list|,
name|String
name|subsection
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|String
name|val
init|=
name|cfg
operator|.
name|getString
argument_list|(
name|MY
argument_list|,
name|subsection
argument_list|,
name|key
argument_list|)
decl_stmt|;
return|return
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|val
argument_list|)
condition|?
name|val
else|:
name|defaultValue
return|;
block|}
block|}
block|}
end_class

end_unit

