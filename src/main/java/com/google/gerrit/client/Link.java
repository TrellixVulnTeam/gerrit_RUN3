begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
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
name|account
operator|.
name|AccountSettings
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
name|client
operator|.
name|account
operator|.
name|NewAgreementScreen
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
name|client
operator|.
name|account
operator|.
name|RegisterScreen
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
name|client
operator|.
name|account
operator|.
name|ValidateEmailScreen
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
name|client
operator|.
name|admin
operator|.
name|AccountGroupScreen
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
name|client
operator|.
name|admin
operator|.
name|GroupListScreen
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
name|client
operator|.
name|admin
operator|.
name|ProjectAdminScreen
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
name|client
operator|.
name|admin
operator|.
name|ProjectListScreen
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
name|client
operator|.
name|auth
operator|.
name|openid
operator|.
name|OpenIdSignInDialog
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
name|client
operator|.
name|auth
operator|.
name|userpass
operator|.
name|UserPassSignInDialog
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
name|client
operator|.
name|changes
operator|.
name|AccountDashboardScreen
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
name|client
operator|.
name|changes
operator|.
name|AllAbandonedChangesScreen
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
name|client
operator|.
name|changes
operator|.
name|AllMergedChangesScreen
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
name|client
operator|.
name|changes
operator|.
name|AllOpenChangesScreen
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
name|client
operator|.
name|changes
operator|.
name|ByProjectOpenChangesScreen
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
name|client
operator|.
name|changes
operator|.
name|ChangeQueryResultsScreen
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
name|client
operator|.
name|changes
operator|.
name|ChangeScreen
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
name|client
operator|.
name|changes
operator|.
name|MineDraftsScreen
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
name|client
operator|.
name|changes
operator|.
name|MineStarredScreen
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
name|client
operator|.
name|changes
operator|.
name|PublishCommentScreen
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
name|client
operator|.
name|data
operator|.
name|AccountInfo
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
name|client
operator|.
name|data
operator|.
name|ChangeInfo
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
name|client
operator|.
name|patches
operator|.
name|PatchScreen
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
name|client
operator|.
name|reviewdb
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
name|client
operator|.
name|reviewdb
operator|.
name|AccountGroup
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
name|client
operator|.
name|reviewdb
operator|.
name|Change
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
name|client
operator|.
name|reviewdb
operator|.
name|Patch
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
name|client
operator|.
name|reviewdb
operator|.
name|PatchSet
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
name|client
operator|.
name|reviewdb
operator|.
name|Project
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
name|client
operator|.
name|ui
operator|.
name|Screen
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
name|event
operator|.
name|logical
operator|.
name|shared
operator|.
name|ValueChangeEvent
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
name|logical
operator|.
name|shared
operator|.
name|ValueChangeHandler
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
name|KeyUtil
import|;
end_import

begin_class
DECL|class|Link
specifier|public
class|class
name|Link
implements|implements
name|ValueChangeHandler
argument_list|<
name|String
argument_list|>
block|{
DECL|field|SETTINGS
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS
init|=
literal|"settings"
decl_stmt|;
DECL|field|SETTINGS_SSHKEYS
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS_SSHKEYS
init|=
literal|"settings,ssh-keys"
decl_stmt|;
DECL|field|SETTINGS_WEBIDENT
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS_WEBIDENT
init|=
literal|"settings,web-identities"
decl_stmt|;
DECL|field|SETTINGS_MYGROUPS
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS_MYGROUPS
init|=
literal|"settings,group-memberships"
decl_stmt|;
DECL|field|SETTINGS_AGREEMENTS
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS_AGREEMENTS
init|=
literal|"settings,agreements"
decl_stmt|;
DECL|field|SETTINGS_CONTACT
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS_CONTACT
init|=
literal|"settings,contact"
decl_stmt|;
DECL|field|SETTINGS_PROJECTS
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS_PROJECTS
init|=
literal|"settings,projects"
decl_stmt|;
DECL|field|SETTINGS_NEW_AGREEMENT
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS_NEW_AGREEMENT
init|=
literal|"settings,new-agreement"
decl_stmt|;
DECL|field|REGISTER
specifier|public
specifier|static
specifier|final
name|String
name|REGISTER
init|=
literal|"register"
decl_stmt|;
DECL|field|MINE
specifier|public
specifier|static
specifier|final
name|String
name|MINE
init|=
literal|"mine"
decl_stmt|;
DECL|field|MINE_STARRED
specifier|public
specifier|static
specifier|final
name|String
name|MINE_STARRED
init|=
literal|"mine,starred"
decl_stmt|;
DECL|field|MINE_DRAFTS
specifier|public
specifier|static
specifier|final
name|String
name|MINE_DRAFTS
init|=
literal|"mine,drafts"
decl_stmt|;
DECL|field|ALL_ABANDONED
specifier|public
specifier|static
specifier|final
name|String
name|ALL_ABANDONED
init|=
literal|"all,abandoned,n,z"
decl_stmt|;
DECL|field|ALL_MERGED
specifier|public
specifier|static
specifier|final
name|String
name|ALL_MERGED
init|=
literal|"all,merged,n,z"
decl_stmt|;
DECL|field|ALL_OPEN
specifier|public
specifier|static
specifier|final
name|String
name|ALL_OPEN
init|=
literal|"all,open,n,z"
decl_stmt|;
DECL|field|ADMIN_PEOPLE
specifier|public
specifier|static
specifier|final
name|String
name|ADMIN_PEOPLE
init|=
literal|"admin,people"
decl_stmt|;
DECL|field|ADMIN_GROUPS
specifier|public
specifier|static
specifier|final
name|String
name|ADMIN_GROUPS
init|=
literal|"admin,groups"
decl_stmt|;
DECL|field|ADMIN_PROJECTS
specifier|public
specifier|static
specifier|final
name|String
name|ADMIN_PROJECTS
init|=
literal|"admin,projects"
decl_stmt|;
DECL|method|toChange (final ChangeInfo c)
specifier|public
specifier|static
name|String
name|toChange
parameter_list|(
specifier|final
name|ChangeInfo
name|c
parameter_list|)
block|{
return|return
name|toChange
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toChange (final Change.Id c)
specifier|public
specifier|static
name|String
name|toChange
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|c
parameter_list|)
block|{
return|return
literal|"change,"
operator|+
name|c
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toAccountDashboard (final AccountInfo acct)
specifier|public
specifier|static
name|String
name|toAccountDashboard
parameter_list|(
specifier|final
name|AccountInfo
name|acct
parameter_list|)
block|{
return|return
name|toAccountDashboard
argument_list|(
name|acct
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toAccountDashboard (final Account.Id acct)
specifier|public
specifier|static
name|String
name|toAccountDashboard
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|acct
parameter_list|)
block|{
return|return
literal|"dashboard,"
operator|+
name|acct
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toPatchSideBySide (final Patch.Key id)
specifier|public
specifier|static
name|String
name|toPatchSideBySide
parameter_list|(
specifier|final
name|Patch
operator|.
name|Key
name|id
parameter_list|)
block|{
return|return
name|toPatch
argument_list|(
literal|"sidebyside"
argument_list|,
name|id
argument_list|)
return|;
block|}
DECL|method|toPatchUnified (final Patch.Key id)
specifier|public
specifier|static
name|String
name|toPatchUnified
parameter_list|(
specifier|final
name|Patch
operator|.
name|Key
name|id
parameter_list|)
block|{
return|return
name|toPatch
argument_list|(
literal|"unified"
argument_list|,
name|id
argument_list|)
return|;
block|}
DECL|method|toPatch (final String type, final Patch.Key id)
specifier|public
specifier|static
name|String
name|toPatch
parameter_list|(
specifier|final
name|String
name|type
parameter_list|,
specifier|final
name|Patch
operator|.
name|Key
name|id
parameter_list|)
block|{
return|return
literal|"patch,"
operator|+
name|type
operator|+
literal|","
operator|+
name|id
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toAccountGroup (final AccountGroup.Id id)
specifier|public
specifier|static
name|String
name|toAccountGroup
parameter_list|(
specifier|final
name|AccountGroup
operator|.
name|Id
name|id
parameter_list|)
block|{
return|return
literal|"admin,group,"
operator|+
name|id
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toProjectAdmin (final Project.NameKey n, final String tab)
specifier|public
specifier|static
name|String
name|toProjectAdmin
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|n
parameter_list|,
specifier|final
name|String
name|tab
parameter_list|)
block|{
return|return
literal|"admin,project,"
operator|+
name|n
operator|.
name|toString
argument_list|()
operator|+
literal|","
operator|+
name|tab
return|;
block|}
DECL|method|toProjectOpen (final Project.NameKey proj)
specifier|public
specifier|static
name|String
name|toProjectOpen
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|proj
parameter_list|)
block|{
return|return
literal|"project,open,"
operator|+
name|proj
operator|.
name|toString
argument_list|()
operator|+
literal|",n,z"
return|;
block|}
DECL|method|toChangeQuery (final String query)
specifier|public
specifier|static
name|String
name|toChangeQuery
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
block|{
return|return
literal|"q,"
operator|+
name|KeyUtil
operator|.
name|encode
argument_list|(
name|query
argument_list|)
operator|+
literal|",n,z"
return|;
block|}
annotation|@
name|Override
DECL|method|onValueChange (final ValueChangeEvent<String> event)
specifier|public
name|void
name|onValueChange
parameter_list|(
specifier|final
name|ValueChangeEvent
argument_list|<
name|String
argument_list|>
name|event
parameter_list|)
block|{
specifier|final
name|String
name|token
init|=
name|event
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Screen
name|s
decl_stmt|;
try|try
block|{
name|s
operator|=
name|select
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|err
parameter_list|)
block|{
name|GWT
operator|.
name|log
argument_list|(
literal|"Error parsing history token: "
operator|+
name|token
argument_list|,
name|err
argument_list|)
expr_stmt|;
name|s
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Gerrit
operator|.
name|display
argument_list|(
operator|new
name|NotFoundScreen
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|select (final String token)
specifier|private
name|Screen
name|select
parameter_list|(
specifier|final
name|String
name|token
parameter_list|)
block|{
name|String
name|p
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|SETTINGS
operator|.
name|equals
argument_list|(
name|token
argument_list|)
operator|||
name|token
operator|.
name|startsWith
argument_list|(
literal|"settings,"
argument_list|)
condition|)
block|{
if|if
condition|(
name|SETTINGS_NEW_AGREEMENT
operator|.
name|equals
argument_list|(
name|token
argument_list|)
condition|)
block|{
return|return
operator|new
name|NewAgreementScreen
argument_list|()
return|;
block|}
name|p
operator|=
name|SETTINGS_NEW_AGREEMENT
operator|+
literal|","
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
block|{
return|return
operator|new
name|NewAgreementScreen
argument_list|(
name|skip
argument_list|(
name|p
argument_list|,
name|token
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|new
name|AccountSettings
argument_list|(
name|token
argument_list|)
return|;
block|}
if|if
condition|(
name|MINE
operator|.
name|equals
argument_list|(
name|token
argument_list|)
condition|)
block|{
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
return|return
operator|new
name|AccountDashboardScreen
argument_list|(
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|Screen
name|r
init|=
operator|new
name|AccountDashboardScreen
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|r
operator|.
name|setRequiresSignIn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
block|}
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
literal|"mine,"
argument_list|)
condition|)
block|{
if|if
condition|(
name|MINE_STARRED
operator|.
name|equals
argument_list|(
name|token
argument_list|)
condition|)
block|{
return|return
operator|new
name|MineStarredScreen
argument_list|()
return|;
block|}
if|if
condition|(
name|MINE_DRAFTS
operator|.
name|equals
argument_list|(
name|token
argument_list|)
condition|)
block|{
return|return
operator|new
name|MineDraftsScreen
argument_list|()
return|;
block|}
block|}
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
literal|"all,"
argument_list|)
condition|)
block|{
name|p
operator|=
literal|"all,abandoned,"
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
block|{
return|return
operator|new
name|AllAbandonedChangesScreen
argument_list|(
name|skip
argument_list|(
name|p
argument_list|,
name|token
argument_list|)
argument_list|)
return|;
block|}
name|p
operator|=
literal|"all,merged,"
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
block|{
return|return
operator|new
name|AllMergedChangesScreen
argument_list|(
name|skip
argument_list|(
name|p
argument_list|,
name|token
argument_list|)
argument_list|)
return|;
block|}
name|p
operator|=
literal|"all,open,"
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
block|{
return|return
operator|new
name|AllOpenChangesScreen
argument_list|(
name|skip
argument_list|(
name|p
argument_list|,
name|token
argument_list|)
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
literal|"project,"
argument_list|)
condition|)
block|{
name|p
operator|=
literal|"project,open,"
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
block|{
specifier|final
name|String
name|s
init|=
name|skip
argument_list|(
name|p
argument_list|,
name|token
argument_list|)
decl_stmt|;
specifier|final
name|int
name|c
init|=
name|s
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
decl_stmt|;
return|return
operator|new
name|ByProjectOpenChangesScreen
argument_list|(
name|Project
operator|.
name|NameKey
operator|.
name|parse
argument_list|(
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|c
argument_list|)
argument_list|)
argument_list|,
name|s
operator|.
name|substring
argument_list|(
name|c
operator|+
literal|1
argument_list|)
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
literal|"patch,"
argument_list|)
condition|)
block|{
name|p
operator|=
literal|"patch,sidebyside,"
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
return|return
operator|new
name|PatchScreen
operator|.
name|SideBySide
argument_list|(
name|Patch
operator|.
name|Key
operator|.
name|parse
argument_list|(
name|skip
argument_list|(
name|p
argument_list|,
name|token
argument_list|)
argument_list|)
argument_list|,
literal|0
comment|/* patchIndex */
argument_list|,
literal|null
comment|/* patchTable */
argument_list|)
return|;
name|p
operator|=
literal|"patch,unified,"
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
return|return
operator|new
name|PatchScreen
operator|.
name|Unified
argument_list|(
name|Patch
operator|.
name|Key
operator|.
name|parse
argument_list|(
name|skip
argument_list|(
name|p
argument_list|,
name|token
argument_list|)
argument_list|)
argument_list|,
literal|0
comment|/* patchIndex */
argument_list|,
literal|null
comment|/* patchTable */
argument_list|)
return|;
block|}
name|p
operator|=
literal|"change,publish,"
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
return|return
operator|new
name|PublishCommentScreen
argument_list|(
name|PatchSet
operator|.
name|Id
operator|.
name|parse
argument_list|(
name|skip
argument_list|(
name|p
argument_list|,
name|token
argument_list|)
argument_list|)
argument_list|)
return|;
name|p
operator|=
literal|"change,"
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
return|return
operator|new
name|ChangeScreen
argument_list|(
name|Change
operator|.
name|Id
operator|.
name|parse
argument_list|(
name|skip
argument_list|(
name|p
argument_list|,
name|token
argument_list|)
argument_list|)
argument_list|)
return|;
name|p
operator|=
literal|"dashboard,"
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
return|return
operator|new
name|AccountDashboardScreen
argument_list|(
name|Account
operator|.
name|Id
operator|.
name|parse
argument_list|(
name|skip
argument_list|(
name|p
argument_list|,
name|token
argument_list|)
argument_list|)
argument_list|)
return|;
name|p
operator|=
literal|"q,"
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
block|{
specifier|final
name|String
name|s
init|=
name|skip
argument_list|(
name|p
argument_list|,
name|token
argument_list|)
decl_stmt|;
specifier|final
name|int
name|c
init|=
name|s
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
decl_stmt|;
return|return
operator|new
name|ChangeQueryResultsScreen
argument_list|(
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|c
argument_list|)
argument_list|,
name|s
operator|.
name|substring
argument_list|(
name|c
operator|+
literal|1
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
literal|"admin,"
argument_list|)
condition|)
block|{
name|p
operator|=
literal|"admin,group,"
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
return|return
operator|new
name|AccountGroupScreen
argument_list|(
name|AccountGroup
operator|.
name|Id
operator|.
name|parse
argument_list|(
name|skip
argument_list|(
name|p
argument_list|,
name|token
argument_list|)
argument_list|)
argument_list|)
return|;
name|p
operator|=
literal|"admin,project,"
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|p
operator|=
name|skip
argument_list|(
name|p
argument_list|,
name|token
argument_list|)
expr_stmt|;
specifier|final
name|int
name|c
init|=
name|p
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
decl_stmt|;
specifier|final
name|String
name|idstr
init|=
name|p
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|c
argument_list|)
decl_stmt|;
return|return
operator|new
name|ProjectAdminScreen
argument_list|(
name|Project
operator|.
name|NameKey
operator|.
name|parse
argument_list|(
name|idstr
argument_list|)
argument_list|,
name|token
argument_list|)
return|;
block|}
if|if
condition|(
name|ADMIN_GROUPS
operator|.
name|equals
argument_list|(
name|token
argument_list|)
condition|)
block|{
return|return
operator|new
name|GroupListScreen
argument_list|()
return|;
block|}
if|if
condition|(
name|ADMIN_PROJECTS
operator|.
name|equals
argument_list|(
name|token
argument_list|)
condition|)
block|{
return|return
operator|new
name|ProjectListScreen
argument_list|()
return|;
block|}
block|}
name|p
operator|=
name|REGISTER
operator|+
literal|","
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
block|{
return|return
operator|new
name|RegisterScreen
argument_list|(
name|skip
argument_list|(
name|p
argument_list|,
name|token
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|REGISTER
operator|.
name|equals
argument_list|(
name|token
argument_list|)
condition|)
block|{
return|return
operator|new
name|RegisterScreen
argument_list|(
name|MINE
argument_list|)
return|;
block|}
name|p
operator|=
literal|"VE,"
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
block|{
return|return
operator|new
name|ValidateEmailScreen
argument_list|(
name|skip
argument_list|(
name|p
argument_list|,
name|token
argument_list|)
argument_list|)
return|;
block|}
name|p
operator|=
literal|"SignInFailure,"
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
block|{
specifier|final
name|String
index|[]
name|args
init|=
name|skip
argument_list|(
name|p
argument_list|,
name|token
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
specifier|final
name|SignInDialog
operator|.
name|Mode
name|mode
init|=
name|SignInDialog
operator|.
name|Mode
operator|.
name|valueOf
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
specifier|final
name|String
name|msg
init|=
name|KeyUtil
operator|.
name|decode
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getAuthType
argument_list|()
condition|)
block|{
case|case
name|OPENID
case|:
operator|new
name|OpenIdSignInDialog
argument_list|(
name|mode
argument_list|,
name|msg
argument_list|)
operator|.
name|center
argument_list|()
expr_stmt|;
break|break;
case|case
name|LDAP
case|:
operator|new
name|UserPassSignInDialog
argument_list|(
name|msg
argument_list|)
operator|.
name|center
argument_list|()
expr_stmt|;
break|break;
default|default:
return|return
literal|null
return|;
block|}
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|SIGN_IN
case|:
return|return
name|select
argument_list|(
name|ALL_OPEN
argument_list|)
return|;
case|case
name|LINK_IDENTIY
case|:
return|return
operator|new
name|AccountSettings
argument_list|(
name|SETTINGS_WEBIDENT
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|skip (final String prefix, final String in)
specifier|private
specifier|static
name|String
name|skip
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|String
name|in
parameter_list|)
block|{
return|return
name|in
operator|.
name|substring
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

