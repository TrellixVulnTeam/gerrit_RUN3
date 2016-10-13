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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|auto
operator|.
name|value
operator|.
name|AutoValue
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
name|annotations
operator|.
name|VisibleForTesting
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
name|Enums
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
name|Joiner
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
name|Splitter
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
name|common
operator|.
name|collect
operator|.
name|ArrayListMultimap
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
name|ImmutableList
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
name|ImmutableSet
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
name|common
operator|.
name|Nullable
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
name|client
operator|.
name|RefNames
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
name|gerrit
operator|.
name|server
operator|.
name|git
operator|.
name|MetaDataUpdate
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
name|ValidationError
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
name|VersionedMetaData
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
name|CommitBuilder
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
name|Collection
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

begin_comment
comment|/**  * âwatch.configâ file in the user branch in the All-Users repository that  * contains the watch configuration of the user.  *<p>  * The 'watch.config' file is a git config file that has one 'project' section  * for all project watches of a project.  *<p>  * The project name is used as subsection name and the filters with the notify  * types that decide for which events email notifications should be sent are  * represented as 'notify' values in the subsection. A 'notify' value is  * formatted as {@code<filter> [<comma-separated-list-of-notify-types>]}:  *  *<pre>  *   [project "foo"]  *     notify = * [ALL_COMMENTS]  *     notify = branch:master [ALL_COMMENTS, NEW_PATCHSETS]  *     notify = branch:master owner:self [SUBMITTED_CHANGES]  *</pre>  *<p>  * If two notify values in the same subsection have the same filter they are  * merged on the next save, taking the union of the notify types.  *<p>  * For watch configurations that notify on no event the list of notify types is  * empty:  *  *<pre>  *   [project "foo"]  *     notify = branch:master []  *</pre>  *<p>  * Unknown notify types are ignored and removed on save.  */
end_comment

begin_class
DECL|class|WatchConfig
specifier|public
class|class
name|WatchConfig
extends|extends
name|VersionedMetaData
implements|implements
name|ValidationError
operator|.
name|Sink
block|{
annotation|@
name|Singleton
DECL|class|Accessor
specifier|public
specifier|static
class|class
name|Accessor
block|{
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|field|metaDataUpdateFactory
specifier|private
specifier|final
name|Provider
argument_list|<
name|MetaDataUpdate
operator|.
name|User
argument_list|>
name|metaDataUpdateFactory
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|Accessor ( GitRepositoryManager repoManager, AllUsersName allUsersName, Provider<MetaDataUpdate.User> metaDataUpdateFactory, IdentifiedUser.GenericFactory userFactory)
name|Accessor
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|AllUsersName
name|allUsersName
parameter_list|,
name|Provider
argument_list|<
name|MetaDataUpdate
operator|.
name|User
argument_list|>
name|metaDataUpdateFactory
parameter_list|,
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
parameter_list|)
block|{
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
name|this
operator|.
name|metaDataUpdateFactory
operator|=
name|metaDataUpdateFactory
expr_stmt|;
name|this
operator|.
name|userFactory
operator|=
name|userFactory
expr_stmt|;
block|}
DECL|method|getProjectWatches ( Account.Id accountId)
specifier|public
name|Map
argument_list|<
name|ProjectWatchKey
argument_list|,
name|Set
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|getProjectWatches
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
try|try
init|(
name|Repository
name|git
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsersName
argument_list|)
init|)
block|{
name|WatchConfig
name|watchConfig
init|=
operator|new
name|WatchConfig
argument_list|(
name|accountId
argument_list|)
decl_stmt|;
name|watchConfig
operator|.
name|load
argument_list|(
name|git
argument_list|)
expr_stmt|;
return|return
name|watchConfig
operator|.
name|getProjectWatches
argument_list|()
return|;
block|}
block|}
DECL|method|upsertProjectWatches (Account.Id accountId, Map<ProjectWatchKey, Set<NotifyType>> newProjectWatches)
specifier|public
specifier|synchronized
name|void
name|upsertProjectWatches
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|Map
argument_list|<
name|ProjectWatchKey
argument_list|,
name|Set
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|newProjectWatches
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|WatchConfig
name|watchConfig
init|=
name|read
argument_list|(
name|accountId
argument_list|)
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
name|watchConfig
operator|.
name|getProjectWatches
argument_list|()
decl_stmt|;
name|projectWatches
operator|.
name|putAll
argument_list|(
name|newProjectWatches
argument_list|)
expr_stmt|;
name|commit
argument_list|(
name|watchConfig
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteProjectWatches (Account.Id accountId, Collection<ProjectWatchKey> projectWatchKeys)
specifier|public
specifier|synchronized
name|void
name|deleteProjectWatches
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|Collection
argument_list|<
name|ProjectWatchKey
argument_list|>
name|projectWatchKeys
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|WatchConfig
name|watchConfig
init|=
name|read
argument_list|(
name|accountId
argument_list|)
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
name|watchConfig
operator|.
name|getProjectWatches
argument_list|()
decl_stmt|;
name|boolean
name|commit
init|=
literal|false
decl_stmt|;
for|for
control|(
name|ProjectWatchKey
name|key
range|:
name|projectWatchKeys
control|)
block|{
if|if
condition|(
name|projectWatches
operator|.
name|remove
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|commit
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|commit
condition|)
block|{
name|commit
argument_list|(
name|watchConfig
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|read (Account.Id accountId)
specifier|private
name|WatchConfig
name|read
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
try|try
init|(
name|Repository
name|git
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsersName
argument_list|)
init|)
block|{
name|WatchConfig
name|watchConfig
init|=
operator|new
name|WatchConfig
argument_list|(
name|accountId
argument_list|)
decl_stmt|;
name|watchConfig
operator|.
name|load
argument_list|(
name|git
argument_list|)
expr_stmt|;
return|return
name|watchConfig
return|;
block|}
block|}
DECL|method|commit (WatchConfig watchConfig)
specifier|private
name|void
name|commit
parameter_list|(
name|WatchConfig
name|watchConfig
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|MetaDataUpdate
name|md
init|=
name|metaDataUpdateFactory
operator|.
name|get
argument_list|()
operator|.
name|create
argument_list|(
name|allUsersName
argument_list|,
name|userFactory
operator|.
name|create
argument_list|(
name|watchConfig
operator|.
name|accountId
argument_list|)
argument_list|)
init|)
block|{
name|watchConfig
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|AutoValue
DECL|class|ProjectWatchKey
specifier|public
specifier|abstract
specifier|static
class|class
name|ProjectWatchKey
block|{
DECL|method|create (Project.NameKey project, @Nullable String filter)
specifier|public
specifier|static
name|ProjectWatchKey
name|create
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
annotation|@
name|Nullable
name|String
name|filter
parameter_list|)
block|{
return|return
operator|new
name|AutoValue_WatchConfig_ProjectWatchKey
argument_list|(
name|project
argument_list|,
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|filter
argument_list|)
argument_list|)
return|;
block|}
DECL|method|project ()
specifier|public
specifier|abstract
name|Project
operator|.
name|NameKey
name|project
parameter_list|()
function_decl|;
DECL|method|filter ()
specifier|public
specifier|abstract
annotation|@
name|Nullable
name|String
name|filter
parameter_list|()
function_decl|;
block|}
DECL|field|WATCH_CONFIG
specifier|public
specifier|static
specifier|final
name|String
name|WATCH_CONFIG
init|=
literal|"watch.config"
decl_stmt|;
DECL|field|PROJECT
specifier|public
specifier|static
specifier|final
name|String
name|PROJECT
init|=
literal|"project"
decl_stmt|;
DECL|field|KEY_NOTIFY
specifier|public
specifier|static
specifier|final
name|String
name|KEY_NOTIFY
init|=
literal|"notify"
decl_stmt|;
DECL|field|accountId
specifier|private
specifier|final
name|Account
operator|.
name|Id
name|accountId
decl_stmt|;
DECL|field|ref
specifier|private
specifier|final
name|String
name|ref
decl_stmt|;
DECL|field|projectWatches
specifier|private
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
decl_stmt|;
DECL|field|validationErrors
specifier|private
name|List
argument_list|<
name|ValidationError
argument_list|>
name|validationErrors
decl_stmt|;
DECL|method|WatchConfig (Account.Id accountId)
specifier|public
name|WatchConfig
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
name|this
operator|.
name|accountId
operator|=
name|accountId
expr_stmt|;
name|this
operator|.
name|ref
operator|=
name|RefNames
operator|.
name|refsUsers
argument_list|(
name|accountId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRefName ()
specifier|protected
name|String
name|getRefName
parameter_list|()
block|{
return|return
name|ref
return|;
block|}
annotation|@
name|Override
DECL|method|onLoad ()
specifier|protected
name|void
name|onLoad
parameter_list|()
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|Config
name|cfg
init|=
name|readConfig
argument_list|(
name|WATCH_CONFIG
argument_list|)
decl_stmt|;
name|projectWatches
operator|=
name|parse
argument_list|(
name|accountId
argument_list|,
name|cfg
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|parse ( Account.Id accountId, Config cfg, ValidationError.Sink validationErrorSink)
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
name|parse
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|Config
name|cfg
parameter_list|,
name|ValidationError
operator|.
name|Sink
name|validationErrorSink
parameter_list|)
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
name|String
name|projectName
range|:
name|cfg
operator|.
name|getSubsections
argument_list|(
name|PROJECT
argument_list|)
control|)
block|{
name|String
index|[]
name|notifyValues
init|=
name|cfg
operator|.
name|getStringList
argument_list|(
name|PROJECT
argument_list|,
name|projectName
argument_list|,
name|KEY_NOTIFY
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|nv
range|:
name|notifyValues
control|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|nv
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|NotifyValue
name|notifyValue
init|=
name|NotifyValue
operator|.
name|parse
argument_list|(
name|accountId
argument_list|,
name|projectName
argument_list|,
name|nv
argument_list|,
name|validationErrorSink
argument_list|)
decl_stmt|;
if|if
condition|(
name|notifyValue
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|ProjectWatchKey
name|key
init|=
name|ProjectWatchKey
operator|.
name|create
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|projectName
argument_list|)
argument_list|,
name|notifyValue
operator|.
name|filter
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|projectWatches
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|projectWatches
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|NotifyType
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|projectWatches
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|addAll
argument_list|(
name|notifyValue
operator|.
name|notifyTypes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|projectWatches
return|;
block|}
DECL|method|getProjectWatches ()
name|Map
argument_list|<
name|ProjectWatchKey
argument_list|,
name|Set
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|getProjectWatches
parameter_list|()
block|{
name|checkLoaded
argument_list|()
expr_stmt|;
return|return
name|projectWatches
return|;
block|}
annotation|@
name|Override
DECL|method|onSave (CommitBuilder commit)
specifier|protected
name|boolean
name|onSave
parameter_list|(
name|CommitBuilder
name|commit
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|checkLoaded
argument_list|()
expr_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|commit
operator|.
name|getMessage
argument_list|()
argument_list|)
condition|)
block|{
name|commit
operator|.
name|setMessage
argument_list|(
literal|"Updated watch configuration\n"
argument_list|)
expr_stmt|;
block|}
name|Config
name|cfg
init|=
name|readConfig
argument_list|(
name|WATCH_CONFIG
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|projectName
range|:
name|cfg
operator|.
name|getSubsections
argument_list|(
name|PROJECT
argument_list|)
control|)
block|{
name|cfg
operator|.
name|unset
argument_list|(
name|PROJECT
argument_list|,
name|projectName
argument_list|,
name|KEY_NOTIFY
argument_list|)
expr_stmt|;
block|}
name|Multimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|notifyValuesByProject
init|=
name|ArrayListMultimap
operator|.
name|create
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
name|NotifyValue
name|notifyValue
init|=
name|NotifyValue
operator|.
name|create
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|filter
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|notifyValuesByProject
operator|.
name|put
argument_list|(
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
argument_list|,
name|notifyValue
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|e
range|:
name|notifyValuesByProject
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|cfg
operator|.
name|setStringList
argument_list|(
name|PROJECT
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|KEY_NOTIFY
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|saveConfig
argument_list|(
name|WATCH_CONFIG
argument_list|,
name|cfg
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|checkLoaded ()
specifier|private
name|void
name|checkLoaded
parameter_list|()
block|{
name|checkState
argument_list|(
name|projectWatches
operator|!=
literal|null
argument_list|,
literal|"project watches not loaded yet"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|error (ValidationError error)
specifier|public
name|void
name|error
parameter_list|(
name|ValidationError
name|error
parameter_list|)
block|{
if|if
condition|(
name|validationErrors
operator|==
literal|null
condition|)
block|{
name|validationErrors
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
name|validationErrors
operator|.
name|add
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the validation errors, if any were discovered during load.    *    * @return list of errors; empty list if there are no errors.    */
DECL|method|getValidationErrors ()
specifier|public
name|List
argument_list|<
name|ValidationError
argument_list|>
name|getValidationErrors
parameter_list|()
block|{
if|if
condition|(
name|validationErrors
operator|!=
literal|null
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|validationErrors
argument_list|)
return|;
block|}
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
annotation|@
name|AutoValue
DECL|class|NotifyValue
specifier|public
specifier|abstract
specifier|static
class|class
name|NotifyValue
block|{
DECL|method|parse (Account.Id accountId, String project, String notifyValue, ValidationError.Sink validationErrorSink)
specifier|public
specifier|static
name|NotifyValue
name|parse
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|String
name|project
parameter_list|,
name|String
name|notifyValue
parameter_list|,
name|ValidationError
operator|.
name|Sink
name|validationErrorSink
parameter_list|)
block|{
name|notifyValue
operator|=
name|notifyValue
operator|.
name|trim
argument_list|()
expr_stmt|;
name|int
name|i
init|=
name|notifyValue
operator|.
name|lastIndexOf
argument_list|(
literal|'['
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
operator|||
name|notifyValue
operator|.
name|charAt
argument_list|(
name|notifyValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|!=
literal|']'
condition|)
block|{
name|validationErrorSink
operator|.
name|error
argument_list|(
operator|new
name|ValidationError
argument_list|(
name|WATCH_CONFIG
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"Invalid project watch of account %d for project %s: %s"
argument_list|,
name|accountId
operator|.
name|get
argument_list|()
argument_list|,
name|project
argument_list|,
name|notifyValue
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|String
name|filter
init|=
name|notifyValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|isEmpty
argument_list|()
operator|||
name|AccountProjectWatch
operator|.
name|FILTER_ALL
operator|.
name|equals
argument_list|(
name|filter
argument_list|)
condition|)
block|{
name|filter
operator|=
literal|null
expr_stmt|;
block|}
name|Set
argument_list|<
name|NotifyType
argument_list|>
name|notifyTypes
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
if|if
condition|(
name|i
operator|+
literal|1
operator|<
name|notifyValue
operator|.
name|length
argument_list|()
operator|-
literal|2
condition|)
block|{
for|for
control|(
name|String
name|nt
range|:
name|Splitter
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|trimResults
argument_list|()
operator|.
name|splitToList
argument_list|(
name|notifyValue
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|notifyValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
control|)
block|{
name|NotifyType
name|notifyType
init|=
name|Enums
operator|.
name|getIfPresent
argument_list|(
name|NotifyType
operator|.
name|class
argument_list|,
name|nt
argument_list|)
operator|.
name|orNull
argument_list|()
decl_stmt|;
if|if
condition|(
name|notifyType
operator|==
literal|null
condition|)
block|{
name|validationErrorSink
operator|.
name|error
argument_list|(
operator|new
name|ValidationError
argument_list|(
name|WATCH_CONFIG
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"Invalid notify type %s in project watch "
operator|+
literal|"of account %d for project %s: %s"
argument_list|,
name|nt
argument_list|,
name|accountId
operator|.
name|get
argument_list|()
argument_list|,
name|project
argument_list|,
name|notifyValue
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|notifyTypes
operator|.
name|add
argument_list|(
name|notifyType
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|create
argument_list|(
name|filter
argument_list|,
name|notifyTypes
argument_list|)
return|;
block|}
DECL|method|create (@ullable String filter, Set<NotifyType> notifyTypes)
specifier|public
specifier|static
name|NotifyValue
name|create
parameter_list|(
annotation|@
name|Nullable
name|String
name|filter
parameter_list|,
name|Set
argument_list|<
name|NotifyType
argument_list|>
name|notifyTypes
parameter_list|)
block|{
return|return
operator|new
name|AutoValue_WatchConfig_NotifyValue
argument_list|(
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|filter
argument_list|)
argument_list|,
name|Sets
operator|.
name|immutableEnumSet
argument_list|(
name|notifyTypes
argument_list|)
argument_list|)
return|;
block|}
DECL|method|filter ()
specifier|public
specifier|abstract
annotation|@
name|Nullable
name|String
name|filter
parameter_list|()
function_decl|;
DECL|method|notifyTypes ()
specifier|public
specifier|abstract
name|ImmutableSet
argument_list|<
name|NotifyType
argument_list|>
name|notifyTypes
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|List
argument_list|<
name|NotifyType
argument_list|>
name|notifyTypes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|notifyTypes
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuilder
name|notifyValue
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|notifyValue
operator|.
name|append
argument_list|(
name|firstNonNull
argument_list|(
name|filter
argument_list|()
argument_list|,
name|AccountProjectWatch
operator|.
name|FILTER_ALL
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|" ["
argument_list|)
expr_stmt|;
name|Joiner
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|appendTo
argument_list|(
name|notifyValue
argument_list|,
name|notifyTypes
argument_list|)
expr_stmt|;
name|notifyValue
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|notifyValue
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

