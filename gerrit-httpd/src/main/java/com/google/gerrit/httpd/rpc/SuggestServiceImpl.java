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
DECL|package|com.google.gerrit.httpd.rpc
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|rpc
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
name|common
operator|.
name|data
operator|.
name|GroupReference
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
name|ReviewerInfo
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
name|SuggestService
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
name|errors
operator|.
name|NoSuchGroupException
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
name|AccountExternalId
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
name|reviewdb
operator|.
name|client
operator|.
name|AccountGroupName
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
name|reviewdb
operator|.
name|client
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
name|IdentifiedUser
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
name|account
operator|.
name|AccountCache
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
name|account
operator|.
name|AccountControl
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
name|account
operator|.
name|AccountVisibility
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
name|account
operator|.
name|GroupCache
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
name|account
operator|.
name|GroupControl
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
name|account
operator|.
name|GroupMembers
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
name|patch
operator|.
name|AddReviewer
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
name|project
operator|.
name|ChangeControl
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
name|project
operator|.
name|NoSuchChangeException
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
name|project
operator|.
name|NoSuchProjectException
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
name|project
operator|.
name|ProjectCache
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
name|project
operator|.
name|ProjectControl
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
DECL|class|SuggestServiceImpl
class|class
name|SuggestServiceImpl
extends|extends
name|BaseServiceImplementation
implements|implements
name|SuggestService
block|{
DECL|field|MAX_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|MAX_SUFFIX
init|=
literal|"\u9fa5"
decl_stmt|;
DECL|field|reviewDbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|reviewDbProvider
decl_stmt|;
DECL|field|projectControlFactory
specifier|private
specifier|final
name|ProjectControl
operator|.
name|Factory
name|projectControlFactory
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|groupControlFactory
specifier|private
specifier|final
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
decl_stmt|;
DECL|field|groupMembersFactory
specifier|private
specifier|final
name|GroupMembers
operator|.
name|Factory
name|groupMembersFactory
decl_stmt|;
DECL|field|identifiedUserFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|identifiedUserFactory
decl_stmt|;
DECL|field|accountControlFactory
specifier|private
specifier|final
name|AccountControl
operator|.
name|Factory
name|accountControlFactory
decl_stmt|;
DECL|field|changeControlFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
decl_stmt|;
DECL|field|cfg
specifier|private
specifier|final
name|Config
name|cfg
decl_stmt|;
DECL|field|groupCache
specifier|private
specifier|final
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|suggestAccounts
specifier|private
specifier|final
name|boolean
name|suggestAccounts
decl_stmt|;
annotation|@
name|Inject
DECL|method|SuggestServiceImpl (final Provider<ReviewDb> schema, final ProjectControl.Factory projectControlFactory, final ProjectCache projectCache, final AccountCache accountCache, final GroupControl.Factory groupControlFactory, final GroupMembers.Factory groupMembersFactory, final Provider<CurrentUser> currentUser, final IdentifiedUser.GenericFactory identifiedUserFactory, final AccountControl.Factory accountControlFactory, final ChangeControl.Factory changeControlFactory, @GerritServerConfig final Config cfg, final GroupCache groupCache)
name|SuggestServiceImpl
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|schema
parameter_list|,
specifier|final
name|ProjectControl
operator|.
name|Factory
name|projectControlFactory
parameter_list|,
specifier|final
name|ProjectCache
name|projectCache
parameter_list|,
specifier|final
name|AccountCache
name|accountCache
parameter_list|,
specifier|final
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
parameter_list|,
specifier|final
name|GroupMembers
operator|.
name|Factory
name|groupMembersFactory
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
parameter_list|,
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|identifiedUserFactory
parameter_list|,
specifier|final
name|AccountControl
operator|.
name|Factory
name|accountControlFactory
parameter_list|,
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
parameter_list|,
annotation|@
name|GerritServerConfig
specifier|final
name|Config
name|cfg
parameter_list|,
specifier|final
name|GroupCache
name|groupCache
parameter_list|)
block|{
name|super
argument_list|(
name|schema
argument_list|,
name|currentUser
argument_list|)
expr_stmt|;
name|this
operator|.
name|reviewDbProvider
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|projectControlFactory
operator|=
name|projectControlFactory
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|groupControlFactory
operator|=
name|groupControlFactory
expr_stmt|;
name|this
operator|.
name|groupMembersFactory
operator|=
name|groupMembersFactory
expr_stmt|;
name|this
operator|.
name|identifiedUserFactory
operator|=
name|identifiedUserFactory
expr_stmt|;
name|this
operator|.
name|accountControlFactory
operator|=
name|accountControlFactory
expr_stmt|;
name|this
operator|.
name|changeControlFactory
operator|=
name|changeControlFactory
expr_stmt|;
name|this
operator|.
name|cfg
operator|=
name|cfg
expr_stmt|;
name|this
operator|.
name|groupCache
operator|=
name|groupCache
expr_stmt|;
if|if
condition|(
literal|"OFF"
operator|.
name|equals
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"suggest"
argument_list|,
literal|null
argument_list|,
literal|"accounts"
argument_list|)
argument_list|)
condition|)
block|{
name|this
operator|.
name|suggestAccounts
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|suggestAccounts
decl_stmt|;
try|try
block|{
name|AccountVisibility
name|av
init|=
name|cfg
operator|.
name|getEnum
argument_list|(
literal|"suggest"
argument_list|,
literal|null
argument_list|,
literal|"accounts"
argument_list|,
name|AccountVisibility
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|suggestAccounts
operator|=
operator|(
name|av
operator|!=
name|AccountVisibility
operator|.
name|NONE
operator|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|err
parameter_list|)
block|{
name|suggestAccounts
operator|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"suggest"
argument_list|,
literal|null
argument_list|,
literal|"accounts"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|suggestAccounts
operator|=
name|suggestAccounts
expr_stmt|;
block|}
block|}
DECL|method|suggestProjectNameKey (final String query, final int limit, final AsyncCallback<List<Project.NameKey>> callback)
specifier|public
name|void
name|suggestProjectNameKey
parameter_list|(
specifier|final
name|String
name|query
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
argument_list|>
name|callback
parameter_list|)
block|{
specifier|final
name|int
name|max
init|=
literal|10
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|limit
operator|<=
literal|0
condition|?
name|max
else|:
name|Math
operator|.
name|min
argument_list|(
name|limit
argument_list|,
name|max
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
argument_list|(
name|n
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Project
operator|.
name|NameKey
name|nameKey
range|:
name|projectCache
operator|.
name|byName
argument_list|(
name|query
argument_list|)
control|)
block|{
specifier|final
name|ProjectControl
name|ctl
decl_stmt|;
try|try
block|{
name|ctl
operator|=
name|projectControlFactory
operator|.
name|validateFor
argument_list|(
name|nameKey
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchProjectException
name|e
parameter_list|)
block|{
continue|continue;
block|}
name|r
operator|.
name|add
argument_list|(
name|ctl
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|size
argument_list|()
operator|==
name|n
condition|)
block|{
break|break;
block|}
block|}
name|callback
operator|.
name|onSuccess
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
DECL|interface|VisibilityControl
specifier|private
interface|interface
name|VisibilityControl
block|{
DECL|method|isVisible (Account account)
name|boolean
name|isVisible
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|OrmException
function_decl|;
block|}
DECL|method|suggestAccount (final String query, final Boolean active, final int limit, final AsyncCallback<List<AccountInfo>> callback)
specifier|public
name|void
name|suggestAccount
parameter_list|(
specifier|final
name|String
name|query
parameter_list|,
specifier|final
name|Boolean
name|active
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|List
argument_list|<
name|AccountInfo
argument_list|>
argument_list|>
name|callback
parameter_list|)
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|Action
argument_list|<
name|List
argument_list|<
name|AccountInfo
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|run
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|suggestAccount
argument_list|(
name|db
argument_list|,
name|query
argument_list|,
name|active
argument_list|,
name|limit
argument_list|,
operator|new
name|VisibilityControl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isVisible
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|accountControlFactory
operator|.
name|get
argument_list|()
operator|.
name|canSee
argument_list|(
name|account
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|suggestAccount (final ReviewDb db, final String query, final Boolean active, final int limit, VisibilityControl visibilityControl)
specifier|private
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|suggestAccount
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|String
name|query
parameter_list|,
specifier|final
name|Boolean
name|active
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|,
name|VisibilityControl
name|visibilityControl
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
operator|!
name|suggestAccounts
condition|)
block|{
return|return
name|Collections
operator|.
expr|<
name|AccountInfo
operator|>
name|emptyList
argument_list|()
return|;
block|}
specifier|final
name|String
name|a
init|=
name|query
decl_stmt|;
specifier|final
name|String
name|b
init|=
name|a
operator|+
name|MAX_SUFFIX
decl_stmt|;
specifier|final
name|int
name|max
init|=
literal|10
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|limit
operator|<=
literal|0
condition|?
name|max
else|:
name|Math
operator|.
name|min
argument_list|(
name|limit
argument_list|,
name|max
argument_list|)
decl_stmt|;
specifier|final
name|LinkedHashMap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|AccountInfo
argument_list|>
name|r
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|AccountInfo
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Account
name|p
range|:
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|suggestByFullName
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
name|n
argument_list|)
control|)
block|{
name|addSuggestion
argument_list|(
name|r
argument_list|,
name|p
argument_list|,
operator|new
name|AccountInfo
argument_list|(
name|p
argument_list|)
argument_list|,
name|active
argument_list|,
name|visibilityControl
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|size
argument_list|()
operator|<
name|n
condition|)
block|{
for|for
control|(
specifier|final
name|Account
name|p
range|:
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|suggestByPreferredEmail
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
name|n
operator|-
name|r
operator|.
name|size
argument_list|()
argument_list|)
control|)
block|{
name|addSuggestion
argument_list|(
name|r
argument_list|,
name|p
argument_list|,
operator|new
name|AccountInfo
argument_list|(
name|p
argument_list|)
argument_list|,
name|active
argument_list|,
name|visibilityControl
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|r
operator|.
name|size
argument_list|()
operator|<
name|n
condition|)
block|{
for|for
control|(
specifier|final
name|AccountExternalId
name|e
range|:
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|suggestByEmailAddress
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
name|n
operator|-
name|r
operator|.
name|size
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|r
operator|.
name|containsKey
argument_list|(
name|e
operator|.
name|getAccountId
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|Account
name|p
init|=
name|accountCache
operator|.
name|get
argument_list|(
name|e
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|getAccount
argument_list|()
decl_stmt|;
specifier|final
name|AccountInfo
name|info
init|=
operator|new
name|AccountInfo
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|info
operator|.
name|setPreferredEmail
argument_list|(
name|e
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
name|addSuggestion
argument_list|(
name|r
argument_list|,
name|p
argument_list|,
name|info
argument_list|,
name|active
argument_list|,
name|visibilityControl
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|ArrayList
argument_list|<
name|AccountInfo
argument_list|>
argument_list|(
name|r
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
DECL|method|addSuggestion (Map<Account.Id, AccountInfo> map, Account account, AccountInfo info, Boolean active, VisibilityControl visibilityControl)
specifier|private
name|void
name|addSuggestion
parameter_list|(
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|AccountInfo
argument_list|>
name|map
parameter_list|,
name|Account
name|account
parameter_list|,
name|AccountInfo
name|info
parameter_list|,
name|Boolean
name|active
parameter_list|,
name|VisibilityControl
name|visibilityControl
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|active
operator|!=
literal|null
operator|&&
name|active
operator|!=
name|account
operator|.
name|isActive
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|visibilityControl
operator|.
name|isVisible
argument_list|(
name|account
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|suggestAccountGroup (final String query, final int limit, final AsyncCallback<List<GroupReference>> callback)
specifier|public
name|void
name|suggestAccountGroup
parameter_list|(
specifier|final
name|String
name|query
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|List
argument_list|<
name|GroupReference
argument_list|>
argument_list|>
name|callback
parameter_list|)
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|Action
argument_list|<
name|List
argument_list|<
name|GroupReference
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|List
argument_list|<
name|GroupReference
argument_list|>
name|run
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|suggestAccountGroup
argument_list|(
name|db
argument_list|,
name|query
argument_list|,
name|limit
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|suggestAccountGroup (final ReviewDb db, final String query, final int limit)
specifier|private
name|List
argument_list|<
name|GroupReference
argument_list|>
name|suggestAccountGroup
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|String
name|query
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|String
name|a
init|=
name|query
decl_stmt|;
specifier|final
name|String
name|b
init|=
name|a
operator|+
name|MAX_SUFFIX
decl_stmt|;
specifier|final
name|int
name|max
init|=
literal|10
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|limit
operator|<=
literal|0
condition|?
name|max
else|:
name|Math
operator|.
name|min
argument_list|(
name|limit
argument_list|,
name|max
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|GroupReference
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<
name|GroupReference
argument_list|>
argument_list|(
name|n
argument_list|)
decl_stmt|;
for|for
control|(
name|AccountGroupName
name|group
range|:
name|db
operator|.
name|accountGroupNames
argument_list|()
operator|.
name|suggestByName
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
name|n
argument_list|)
control|)
block|{
try|try
block|{
if|if
condition|(
name|groupControlFactory
operator|.
name|controlFor
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|isVisible
argument_list|()
condition|)
block|{
name|AccountGroup
name|g
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|group
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|g
operator|!=
literal|null
operator|&&
name|g
operator|.
name|getGroupUUID
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|add
argument_list|(
name|GroupReference
operator|.
name|forGroup
argument_list|(
name|g
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchGroupException
name|e
parameter_list|)
block|{
continue|continue;
block|}
block|}
return|return
name|r
return|;
block|}
annotation|@
name|Override
DECL|method|suggestReviewer (Project.NameKey project, String query, int limit, AsyncCallback<List<ReviewerInfo>> callback)
specifier|public
name|void
name|suggestReviewer
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|String
name|query
parameter_list|,
name|int
name|limit
parameter_list|,
name|AsyncCallback
argument_list|<
name|List
argument_list|<
name|ReviewerInfo
argument_list|>
argument_list|>
name|callback
parameter_list|)
block|{
comment|// The RPC is deprecated, but return an empty list for RPC API compatibility.
name|callback
operator|.
name|onSuccess
argument_list|(
name|Collections
operator|.
expr|<
name|ReviewerInfo
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|suggestChangeReviewer (final Change.Id change, final String query, final int limit, final AsyncCallback<List<ReviewerInfo>> callback)
specifier|public
name|void
name|suggestChangeReviewer
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|change
parameter_list|,
specifier|final
name|String
name|query
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|List
argument_list|<
name|ReviewerInfo
argument_list|>
argument_list|>
name|callback
parameter_list|)
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|Action
argument_list|<
name|List
argument_list|<
name|ReviewerInfo
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|List
argument_list|<
name|ReviewerInfo
argument_list|>
name|run
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|ChangeControl
name|changeControl
decl_stmt|;
try|try
block|{
name|changeControl
operator|=
name|changeControlFactory
operator|.
name|controlFor
argument_list|(
name|change
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchChangeException
name|e
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|VisibilityControl
name|visibilityControl
init|=
operator|new
name|VisibilityControl
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isVisible
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|OrmException
block|{
name|IdentifiedUser
name|who
init|=
name|identifiedUserFactory
operator|.
name|create
argument_list|(
name|reviewDbProvider
argument_list|,
name|account
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|changeControl
operator|.
name|forUser
argument_list|(
name|who
argument_list|)
operator|.
name|isVisible
argument_list|(
name|reviewDbProvider
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|suggestedAccounts
init|=
name|suggestAccount
argument_list|(
name|db
argument_list|,
name|query
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|,
name|limit
argument_list|,
name|visibilityControl
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ReviewerInfo
argument_list|>
name|reviewer
init|=
operator|new
name|ArrayList
argument_list|<
name|ReviewerInfo
argument_list|>
argument_list|(
name|suggestedAccounts
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|AccountInfo
name|a
range|:
name|suggestedAccounts
control|)
block|{
name|reviewer
operator|.
name|add
argument_list|(
operator|new
name|ReviewerInfo
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|GroupReference
argument_list|>
name|suggestedAccountGroups
init|=
name|suggestAccountGroup
argument_list|(
name|db
argument_list|,
name|query
argument_list|,
name|limit
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|GroupReference
name|g
range|:
name|suggestedAccountGroups
control|)
block|{
if|if
condition|(
name|suggestGroupAsReviewer
argument_list|(
name|changeControl
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|g
argument_list|)
condition|)
block|{
name|reviewer
operator|.
name|add
argument_list|(
operator|new
name|ReviewerInfo
argument_list|(
name|g
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|reviewer
argument_list|)
expr_stmt|;
if|if
condition|(
name|reviewer
operator|.
name|size
argument_list|()
operator|<=
name|limit
condition|)
block|{
return|return
name|reviewer
return|;
block|}
else|else
block|{
return|return
name|reviewer
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|limit
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|suggestGroupAsReviewer (final Project.NameKey project, final GroupReference group)
specifier|private
name|boolean
name|suggestGroupAsReviewer
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
specifier|final
name|GroupReference
name|group
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
operator|!
name|AddReviewer
operator|.
name|isLegalReviewerGroup
argument_list|(
name|group
operator|.
name|getUUID
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
try|try
block|{
specifier|final
name|Set
argument_list|<
name|Account
argument_list|>
name|members
init|=
name|groupMembersFactory
operator|.
name|create
argument_list|()
operator|.
name|listAccounts
argument_list|(
name|group
operator|.
name|getUUID
argument_list|()
argument_list|,
name|project
argument_list|)
decl_stmt|;
if|if
condition|(
name|members
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|int
name|maxAllowed
init|=
name|cfg
operator|.
name|getInt
argument_list|(
literal|"addreviewer"
argument_list|,
literal|"maxAllowed"
argument_list|,
name|AddReviewer
operator|.
name|DEFAULT_MAX_REVIEWERS
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxAllowed
operator|>
literal|0
operator|&&
name|members
operator|.
name|size
argument_list|()
operator|>
name|maxAllowed
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchGroupException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchProjectException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

