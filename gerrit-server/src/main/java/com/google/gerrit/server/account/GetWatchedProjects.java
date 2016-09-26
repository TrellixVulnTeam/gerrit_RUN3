begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
name|common
operator|.
name|collect
operator|.
name|ComparisonChain
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
name|client
operator|.
name|ProjectWatchInfo
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
name|AccountProjectWatch
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
name|AccountProjectWatch
operator|.
name|NotifyType
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
name|WatchConfig
operator|.
name|ProjectWatchKey
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
name|Collections
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
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
annotation|@
name|Singleton
DECL|class|GetWatchedProjects
specifier|public
class|class
name|GetWatchedProjects
implements|implements
name|RestReadView
argument_list|<
name|AccountResource
argument_list|>
block|{
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
decl_stmt|;
DECL|field|self
specifier|private
specifier|final
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|self
decl_stmt|;
DECL|field|readFromGit
specifier|private
specifier|final
name|boolean
name|readFromGit
decl_stmt|;
DECL|field|watchConfig
specifier|private
specifier|final
name|WatchConfig
operator|.
name|Accessor
name|watchConfig
decl_stmt|;
annotation|@
name|Inject
DECL|method|GetWatchedProjects (Provider<ReviewDb> dbProvider, Provider<IdentifiedUser> self, @GerritServerConfig Config cfg, WatchConfig.Accessor watchConfig)
specifier|public
name|GetWatchedProjects
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|self
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|WatchConfig
operator|.
name|Accessor
name|watchConfig
parameter_list|)
block|{
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|self
operator|=
name|self
expr_stmt|;
name|this
operator|.
name|readFromGit
operator|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"user"
argument_list|,
literal|null
argument_list|,
literal|"readProjectWatchesFromGit"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|watchConfig
operator|=
name|watchConfig
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (AccountResource rsrc)
specifier|public
name|List
argument_list|<
name|ProjectWatchInfo
argument_list|>
name|apply
parameter_list|(
name|AccountResource
name|rsrc
parameter_list|)
throws|throws
name|OrmException
throws|,
name|AuthException
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
literal|"It is not allowed to list project watches "
operator|+
literal|"of other users"
argument_list|)
throw|;
block|}
name|Account
operator|.
name|Id
name|accountId
init|=
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|ProjectWatchKey
argument_list|,
name|Set
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|projectWatches
init|=
name|readFromGit
condition|?
name|watchConfig
operator|.
name|getProjectWatches
argument_list|(
name|accountId
argument_list|)
else|:
name|readProjectWatchesFromDb
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|,
name|accountId
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ProjectWatchInfo
argument_list|>
name|projectWatchInfos
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ProjectWatchKey
argument_list|,
name|Set
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|e
range|:
name|projectWatches
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ProjectWatchInfo
name|pwi
init|=
operator|new
name|ProjectWatchInfo
argument_list|()
decl_stmt|;
name|pwi
operator|.
name|filter
operator|=
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|filter
argument_list|()
expr_stmt|;
name|pwi
operator|.
name|project
operator|=
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|project
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|pwi
operator|.
name|notifyAbandonedChanges
operator|=
name|toBoolean
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
name|NotifyType
operator|.
name|ABANDONED_CHANGES
argument_list|)
argument_list|)
expr_stmt|;
name|pwi
operator|.
name|notifyNewChanges
operator|=
name|toBoolean
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
name|NotifyType
operator|.
name|NEW_CHANGES
argument_list|)
argument_list|)
expr_stmt|;
name|pwi
operator|.
name|notifyNewPatchSets
operator|=
name|toBoolean
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
name|NotifyType
operator|.
name|NEW_PATCHSETS
argument_list|)
argument_list|)
expr_stmt|;
name|pwi
operator|.
name|notifySubmittedChanges
operator|=
name|toBoolean
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
name|NotifyType
operator|.
name|SUBMITTED_CHANGES
argument_list|)
argument_list|)
expr_stmt|;
name|pwi
operator|.
name|notifyAllComments
operator|=
name|toBoolean
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
name|NotifyType
operator|.
name|ALL_COMMENTS
argument_list|)
argument_list|)
expr_stmt|;
name|projectWatchInfos
operator|.
name|add
argument_list|(
name|pwi
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|projectWatchInfos
argument_list|,
operator|new
name|Comparator
argument_list|<
name|ProjectWatchInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|ProjectWatchInfo
name|pwi1
parameter_list|,
name|ProjectWatchInfo
name|pwi2
parameter_list|)
block|{
return|return
name|ComparisonChain
operator|.
name|start
argument_list|()
operator|.
name|compare
argument_list|(
name|pwi1
operator|.
name|project
argument_list|,
name|pwi2
operator|.
name|project
argument_list|)
operator|.
name|compare
argument_list|(
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|pwi1
operator|.
name|filter
argument_list|)
argument_list|,
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|pwi2
operator|.
name|filter
argument_list|)
argument_list|)
operator|.
name|result
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|projectWatchInfos
return|;
block|}
DECL|method|toBoolean (boolean value)
specifier|private
specifier|static
name|Boolean
name|toBoolean
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
return|return
name|value
condition|?
literal|true
else|:
literal|null
return|;
block|}
DECL|method|readProjectWatchesFromDb ( ReviewDb db, Account.Id who)
specifier|public
specifier|static
name|Map
argument_list|<
name|ProjectWatchKey
argument_list|,
name|Set
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|readProjectWatchesFromDb
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Account
operator|.
name|Id
name|who
parameter_list|)
throws|throws
name|OrmException
block|{
name|Map
argument_list|<
name|ProjectWatchKey
argument_list|,
name|Set
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|projectWatches
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountProjectWatch
name|apw
range|:
name|db
operator|.
name|accountProjectWatches
argument_list|()
operator|.
name|byAccount
argument_list|(
name|who
argument_list|)
control|)
block|{
name|ProjectWatchKey
name|key
init|=
name|ProjectWatchKey
operator|.
name|create
argument_list|(
name|apw
operator|.
name|getProjectNameKey
argument_list|()
argument_list|,
name|apw
operator|.
name|getFilter
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|NotifyType
argument_list|>
name|notifyValues
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|NotifyType
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|NotifyType
name|notifyType
range|:
name|NotifyType
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|apw
operator|.
name|isNotify
argument_list|(
name|notifyType
argument_list|)
condition|)
block|{
name|notifyValues
operator|.
name|add
argument_list|(
name|notifyType
argument_list|)
expr_stmt|;
block|}
block|}
name|projectWatches
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|notifyValues
argument_list|)
expr_stmt|;
block|}
return|return
name|projectWatches
return|;
block|}
block|}
end_class

end_unit

