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
DECL|package|com.google.gerrit.server.permissions
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|permissions
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
name|Preconditions
operator|.
name|checkNotNull
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
name|reviewdb
operator|.
name|client
operator|.
name|RefNames
operator|.
name|REFS_CACHE_AUTOMERGE
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
name|reviewdb
operator|.
name|client
operator|.
name|RefNames
operator|.
name|REFS_CHANGES
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
name|reviewdb
operator|.
name|client
operator|.
name|RefNames
operator|.
name|REFS_CONFIG
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
name|reviewdb
operator|.
name|client
operator|.
name|RefNames
operator|.
name|REFS_USERS_SELF
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toMap
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
name|flogger
operator|.
name|FluentLogger
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
name|Branch
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
name|git
operator|.
name|SearchingChangeCacheImpl
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
name|TagCache
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
name|TagMatcher
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
name|group
operator|.
name|InternalGroup
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
name|notedb
operator|.
name|AbstractChangeNotes
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
name|notedb
operator|.
name|ChangeNotes
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
name|notedb
operator|.
name|ChangeNotes
operator|.
name|Factory
operator|.
name|ChangeNotesResult
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
name|permissions
operator|.
name|PermissionBackend
operator|.
name|RefFilterOptions
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
name|ProjectState
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
name|query
operator|.
name|change
operator|.
name|ChangeData
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
name|assistedinject
operator|.
name|Assisted
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
name|Collections
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
name|Objects
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Constants
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
name|Ref
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|SymbolicRef
import|;
end_import

begin_class
DECL|class|DefaultRefFilter
class|class
name|DefaultRefFilter
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (ProjectControl projectControl)
name|DefaultRefFilter
name|create
parameter_list|(
name|ProjectControl
name|projectControl
parameter_list|)
function_decl|;
block|}
DECL|field|tagCache
specifier|private
specifier|final
name|TagCache
name|tagCache
decl_stmt|;
DECL|field|changeNotesFactory
specifier|private
specifier|final
name|ChangeNotes
operator|.
name|Factory
name|changeNotesFactory
decl_stmt|;
DECL|field|changeCache
annotation|@
name|Nullable
specifier|private
specifier|final
name|SearchingChangeCacheImpl
name|changeCache
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
DECL|field|groupCache
specifier|private
specifier|final
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|projectControl
specifier|private
specifier|final
name|ProjectControl
name|projectControl
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|CurrentUser
name|user
decl_stmt|;
DECL|field|projectState
specifier|private
specifier|final
name|ProjectState
name|projectState
decl_stmt|;
DECL|field|permissionBackendForProject
specifier|private
specifier|final
name|PermissionBackend
operator|.
name|ForProject
name|permissionBackendForProject
decl_stmt|;
DECL|field|visibleChanges
specifier|private
name|Map
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|Branch
operator|.
name|NameKey
argument_list|>
name|visibleChanges
decl_stmt|;
annotation|@
name|Inject
DECL|method|DefaultRefFilter ( TagCache tagCache, ChangeNotes.Factory changeNotesFactory, @Nullable SearchingChangeCacheImpl changeCache, Provider<ReviewDb> db, GroupCache groupCache, PermissionBackend permissionBackend, @Assisted ProjectControl projectControl)
name|DefaultRefFilter
parameter_list|(
name|TagCache
name|tagCache
parameter_list|,
name|ChangeNotes
operator|.
name|Factory
name|changeNotesFactory
parameter_list|,
annotation|@
name|Nullable
name|SearchingChangeCacheImpl
name|changeCache
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|GroupCache
name|groupCache
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
annotation|@
name|Assisted
name|ProjectControl
name|projectControl
parameter_list|)
block|{
name|this
operator|.
name|tagCache
operator|=
name|tagCache
expr_stmt|;
name|this
operator|.
name|changeNotesFactory
operator|=
name|changeNotesFactory
expr_stmt|;
name|this
operator|.
name|changeCache
operator|=
name|changeCache
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|groupCache
operator|=
name|groupCache
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|projectControl
operator|=
name|projectControl
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|projectControl
operator|.
name|getUser
argument_list|()
expr_stmt|;
name|this
operator|.
name|projectState
operator|=
name|projectControl
operator|.
name|getProjectState
argument_list|()
expr_stmt|;
name|this
operator|.
name|permissionBackendForProject
operator|=
name|permissionBackend
operator|.
name|user
argument_list|(
name|user
argument_list|)
operator|.
name|database
argument_list|(
name|db
argument_list|)
operator|.
name|project
argument_list|(
name|projectState
operator|.
name|getNameKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|filter (Map<String, Ref> refs, Repository repo, RefFilterOptions opts)
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|filter
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|refs
parameter_list|,
name|Repository
name|repo
parameter_list|,
name|RefFilterOptions
name|opts
parameter_list|)
block|{
if|if
condition|(
name|projectState
operator|.
name|isAllUsers
argument_list|()
condition|)
block|{
name|refs
operator|=
name|addUsersSelfSymref
argument_list|(
name|refs
argument_list|)
expr_stmt|;
block|}
name|PermissionBackend
operator|.
name|WithUser
name|withUser
init|=
name|permissionBackend
operator|.
name|user
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|PermissionBackend
operator|.
name|ForProject
name|forProject
init|=
name|withUser
operator|.
name|project
argument_list|(
name|projectState
operator|.
name|getNameKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|projectState
operator|.
name|isAllUsers
argument_list|()
condition|)
block|{
if|if
condition|(
name|projectState
operator|.
name|statePermitsRead
argument_list|()
operator|&&
name|checkProjectPermission
argument_list|(
name|forProject
argument_list|,
name|ProjectPermission
operator|.
name|READ
argument_list|)
condition|)
block|{
return|return
name|refs
return|;
block|}
elseif|else
if|if
condition|(
name|projectControl
operator|.
name|allRefsAreVisible
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|fastHideRefsMetaConfig
argument_list|(
name|refs
argument_list|)
return|;
block|}
block|}
name|boolean
name|viewMetadata
decl_stmt|;
name|boolean
name|isAdmin
decl_stmt|;
name|Account
operator|.
name|Id
name|userId
decl_stmt|;
name|IdentifiedUser
name|identifiedUser
decl_stmt|;
if|if
condition|(
name|user
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
name|viewMetadata
operator|=
name|withUser
operator|.
name|testOrFalse
argument_list|(
name|GlobalPermission
operator|.
name|ACCESS_DATABASE
argument_list|)
expr_stmt|;
name|isAdmin
operator|=
name|withUser
operator|.
name|testOrFalse
argument_list|(
name|GlobalPermission
operator|.
name|ADMINISTRATE_SERVER
argument_list|)
expr_stmt|;
name|identifiedUser
operator|=
name|user
operator|.
name|asIdentifiedUser
argument_list|()
expr_stmt|;
name|userId
operator|=
name|identifiedUser
operator|.
name|getAccountId
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|viewMetadata
operator|=
literal|false
expr_stmt|;
name|isAdmin
operator|=
literal|false
expr_stmt|;
name|userId
operator|=
literal|null
expr_stmt|;
name|identifiedUser
operator|=
literal|null
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Ref
argument_list|>
name|deferredTags
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Ref
name|ref
range|:
name|refs
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|ref
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
name|Account
operator|.
name|Id
name|accountId
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|accountGroupUuid
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|REFS_CACHE_AUTOMERGE
argument_list|)
operator|||
operator|(
name|opts
operator|.
name|filterMeta
argument_list|()
operator|&&
name|isMetadata
argument_list|(
name|name
argument_list|)
operator|)
condition|)
block|{
continue|continue;
block|}
elseif|else
if|if
condition|(
name|RefNames
operator|.
name|isRefsEdit
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// Edits are visible only to the owning user, if change is visible.
if|if
condition|(
name|viewMetadata
operator|||
name|visibleEdit
argument_list|(
name|repo
argument_list|,
name|name
argument_list|)
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|(
name|changeId
operator|=
name|Change
operator|.
name|Id
operator|.
name|fromRef
argument_list|(
name|name
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// Change ref is visible only if the change is visible.
if|if
condition|(
name|viewMetadata
operator|||
name|visible
argument_list|(
name|repo
argument_list|,
name|changeId
argument_list|)
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|(
name|accountId
operator|=
name|Account
operator|.
name|Id
operator|.
name|fromRef
argument_list|(
name|name
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// Account ref is visible only to the corresponding account.
if|if
condition|(
name|viewMetadata
operator|||
operator|(
name|accountId
operator|.
name|equals
argument_list|(
name|userId
argument_list|)
operator|&&
name|canReadRef
argument_list|(
name|name
argument_list|)
operator|)
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|(
name|accountGroupUuid
operator|=
name|AccountGroup
operator|.
name|UUID
operator|.
name|fromRef
argument_list|(
name|name
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// Group ref is visible only to the corresponding owner group.
name|InternalGroup
name|group
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|accountGroupUuid
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|viewMetadata
operator|||
operator|(
name|group
operator|!=
literal|null
operator|&&
name|isGroupOwner
argument_list|(
name|group
argument_list|,
name|identifiedUser
argument_list|,
name|isAdmin
argument_list|)
operator|&&
name|canReadRef
argument_list|(
name|name
argument_list|)
operator|)
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|isTag
argument_list|(
name|ref
argument_list|)
condition|)
block|{
comment|// If its a tag, consider it later.
if|if
condition|(
name|ref
operator|.
name|getObjectId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|deferredTags
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|RefNames
operator|.
name|REFS_SEQUENCES
argument_list|)
condition|)
block|{
comment|// Sequences are internal database implementation details.
if|if
condition|(
name|viewMetadata
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|projectState
operator|.
name|isAllUsers
argument_list|()
operator|&&
operator|(
name|name
operator|.
name|equals
argument_list|(
name|RefNames
operator|.
name|REFS_EXTERNAL_IDS
argument_list|)
operator|||
name|name
operator|.
name|equals
argument_list|(
name|RefNames
operator|.
name|REFS_GROUPNAMES
argument_list|)
operator|)
condition|)
block|{
comment|// The notes branches with the external IDs / group names must not be exposed to normal
comment|// users.
if|if
condition|(
name|viewMetadata
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|canReadRef
argument_list|(
name|ref
operator|.
name|getLeaf
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// Use the leaf to lookup the control data. If the reference is
comment|// symbolic we want the control around the final target. If its
comment|// not symbolic then getLeaf() is a no-op returning ref itself.
name|result
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isRefsUsersSelf
argument_list|(
name|ref
argument_list|)
condition|)
block|{
comment|// viewMetadata allows to see all account refs, hence refs/users/self should be included as
comment|// well
if|if
condition|(
name|viewMetadata
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// If we have tags that were deferred, we need to do a revision walk
comment|// to identify what tags we can actually reach, and what we cannot.
comment|//
if|if
condition|(
operator|!
name|deferredTags
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|(
operator|!
name|result
operator|.
name|isEmpty
argument_list|()
operator|||
name|opts
operator|.
name|filterTagsSeparately
argument_list|()
operator|)
condition|)
block|{
name|TagMatcher
name|tags
init|=
name|tagCache
operator|.
name|get
argument_list|(
name|projectState
operator|.
name|getNameKey
argument_list|()
argument_list|)
operator|.
name|matcher
argument_list|(
name|tagCache
argument_list|,
name|repo
argument_list|,
name|opts
operator|.
name|filterTagsSeparately
argument_list|()
condition|?
name|filter
argument_list|(
name|repo
operator|.
name|getAllRefs
argument_list|()
argument_list|,
name|repo
argument_list|,
name|opts
operator|.
name|toBuilder
argument_list|()
operator|.
name|setFilterTagsSeparately
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|values
argument_list|()
else|:
name|result
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Ref
name|tag
range|:
name|deferredTags
control|)
block|{
if|if
condition|(
name|tags
operator|.
name|isReachable
argument_list|(
name|tag
argument_list|)
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|tag
operator|.
name|getName
argument_list|()
argument_list|,
name|tag
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|fastHideRefsMetaConfig (Map<String, Ref> refs)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|fastHideRefsMetaConfig
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|refs
parameter_list|)
block|{
if|if
condition|(
name|refs
operator|.
name|containsKey
argument_list|(
name|REFS_CONFIG
argument_list|)
operator|&&
operator|!
name|canReadRef
argument_list|(
name|REFS_CONFIG
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|r
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|refs
argument_list|)
decl_stmt|;
name|r
operator|.
name|remove
argument_list|(
name|REFS_CONFIG
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
return|return
name|refs
return|;
block|}
DECL|method|addUsersSelfSymref (Map<String, Ref> refs)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|addUsersSelfSymref
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|refs
parameter_list|)
block|{
if|if
condition|(
name|user
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
name|Ref
name|r
init|=
name|refs
operator|.
name|get
argument_list|(
name|RefNames
operator|.
name|refsUsers
argument_list|(
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|SymbolicRef
name|s
init|=
operator|new
name|SymbolicRef
argument_list|(
name|REFS_USERS_SELF
argument_list|,
name|r
argument_list|)
decl_stmt|;
name|refs
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|refs
argument_list|)
expr_stmt|;
name|refs
operator|.
name|put
argument_list|(
name|s
operator|.
name|getName
argument_list|()
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|refs
return|;
block|}
DECL|method|visible (Repository repo, Change.Id changeId)
specifier|private
name|boolean
name|visible
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|Change
operator|.
name|Id
name|changeId
parameter_list|)
block|{
if|if
condition|(
name|visibleChanges
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|changeCache
operator|==
literal|null
condition|)
block|{
name|visibleChanges
operator|=
name|visibleChangesByScan
argument_list|(
name|repo
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|visibleChanges
operator|=
name|visibleChangesBySearch
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|visibleChanges
operator|.
name|containsKey
argument_list|(
name|changeId
argument_list|)
return|;
block|}
DECL|method|visibleEdit (Repository repo, String name)
specifier|private
name|boolean
name|visibleEdit
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Change
operator|.
name|Id
name|id
init|=
name|Change
operator|.
name|Id
operator|.
name|fromEditRefPart
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// Initialize if it wasn't yet
if|if
condition|(
name|visibleChanges
operator|==
literal|null
condition|)
block|{
name|visible
argument_list|(
name|repo
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|user
operator|.
name|isIdentifiedUser
argument_list|()
operator|&&
name|name
operator|.
name|startsWith
argument_list|(
name|RefNames
operator|.
name|refsEditPrefix
argument_list|(
name|user
operator|.
name|asIdentifiedUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
argument_list|)
operator|&&
name|visible
argument_list|(
name|repo
argument_list|,
name|id
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|visibleChanges
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
condition|)
block|{
try|try
block|{
comment|// Default to READ_PRIVATE_CHANGES as there is no special permission for reading edits.
name|permissionBackendForProject
operator|.
name|ref
argument_list|(
name|visibleChanges
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|check
argument_list|(
name|RefPermission
operator|.
name|READ_PRIVATE_CHANGES
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|PermissionBackendException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Failed to check permission for %s in %s"
argument_list|,
name|id
argument_list|,
name|projectState
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|visibleChangesBySearch ()
specifier|private
name|Map
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|Branch
operator|.
name|NameKey
argument_list|>
name|visibleChangesBySearch
parameter_list|()
block|{
name|Project
operator|.
name|NameKey
name|project
init|=
name|projectState
operator|.
name|getNameKey
argument_list|()
decl_stmt|;
try|try
block|{
name|Map
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|Branch
operator|.
name|NameKey
argument_list|>
name|visibleChanges
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|changeCache
operator|.
name|getChangeData
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|project
argument_list|)
control|)
block|{
name|ChangeNotes
name|notes
init|=
name|changeNotesFactory
operator|.
name|createFromIndexedChange
argument_list|(
name|cd
operator|.
name|change
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|projectState
operator|.
name|statePermitsRead
argument_list|()
condition|)
block|{
continue|continue;
block|}
try|try
block|{
name|permissionBackendForProject
operator|.
name|indexedChange
argument_list|(
name|cd
argument_list|,
name|notes
argument_list|)
operator|.
name|check
argument_list|(
name|ChangePermission
operator|.
name|READ
argument_list|)
expr_stmt|;
name|visibleChanges
operator|.
name|put
argument_list|(
name|cd
operator|.
name|getId
argument_list|()
argument_list|,
name|cd
operator|.
name|change
argument_list|()
operator|.
name|getDest
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|e
parameter_list|)
block|{
comment|// Do nothing.
block|}
block|}
return|return
name|visibleChanges
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|PermissionBackendException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Cannot load changes for project %s, assuming no changes are visible"
argument_list|,
name|project
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
block|}
DECL|method|visibleChangesByScan (Repository repo)
specifier|private
name|Map
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|Branch
operator|.
name|NameKey
argument_list|>
name|visibleChangesByScan
parameter_list|(
name|Repository
name|repo
parameter_list|)
block|{
name|Project
operator|.
name|NameKey
name|p
init|=
name|projectState
operator|.
name|getNameKey
argument_list|()
decl_stmt|;
name|Stream
argument_list|<
name|ChangeNotesResult
argument_list|>
name|s
decl_stmt|;
try|try
block|{
name|s
operator|=
name|changeNotesFactory
operator|.
name|scan
argument_list|(
name|repo
argument_list|,
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Cannot load changes for project %s, assuming no changes are visible"
argument_list|,
name|p
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
return|return
name|s
operator|.
name|map
argument_list|(
name|this
operator|::
name|toNotes
argument_list|)
operator|.
name|filter
argument_list|(
name|Objects
operator|::
name|nonNull
argument_list|)
operator|.
name|collect
argument_list|(
name|toMap
argument_list|(
name|AbstractChangeNotes
operator|::
name|getChangeId
argument_list|,
name|n
lambda|->
name|n
operator|.
name|getChange
argument_list|()
operator|.
name|getDest
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Nullable
DECL|method|toNotes (ChangeNotesResult r)
specifier|private
name|ChangeNotes
name|toNotes
parameter_list|(
name|ChangeNotesResult
name|r
parameter_list|)
block|{
if|if
condition|(
name|r
operator|.
name|error
argument_list|()
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|logger
operator|.
name|atWarning
argument_list|()
operator|.
name|withCause
argument_list|(
name|r
operator|.
name|error
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|log
argument_list|(
literal|"Failed to load change %s in %s"
argument_list|,
name|r
operator|.
name|id
argument_list|()
argument_list|,
name|projectState
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|projectState
operator|.
name|statePermitsRead
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
name|permissionBackendForProject
operator|.
name|change
argument_list|(
name|r
operator|.
name|notes
argument_list|()
argument_list|)
operator|.
name|check
argument_list|(
name|ChangePermission
operator|.
name|READ
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|notes
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|e
parameter_list|)
block|{
comment|// Skip.
block|}
catch|catch
parameter_list|(
name|PermissionBackendException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Failed to check permission for %s in %s"
argument_list|,
name|r
operator|.
name|id
argument_list|()
argument_list|,
name|projectState
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|isMetadata (String name)
specifier|private
name|boolean
name|isMetadata
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|startsWith
argument_list|(
name|REFS_CHANGES
argument_list|)
operator|||
name|RefNames
operator|.
name|isRefsEdit
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|isTag (Ref ref)
specifier|private
specifier|static
name|boolean
name|isTag
parameter_list|(
name|Ref
name|ref
parameter_list|)
block|{
return|return
name|ref
operator|.
name|getLeaf
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|Constants
operator|.
name|R_TAGS
argument_list|)
return|;
block|}
DECL|method|isRefsUsersSelf (Ref ref)
specifier|private
specifier|static
name|boolean
name|isRefsUsersSelf
parameter_list|(
name|Ref
name|ref
parameter_list|)
block|{
return|return
name|ref
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|REFS_USERS_SELF
argument_list|)
return|;
block|}
DECL|method|canReadRef (String ref)
specifier|private
name|boolean
name|canReadRef
parameter_list|(
name|String
name|ref
parameter_list|)
block|{
try|try
block|{
name|permissionBackendForProject
operator|.
name|ref
argument_list|(
name|ref
argument_list|)
operator|.
name|check
argument_list|(
name|RefPermission
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|PermissionBackendException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"unable to check permissions"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
name|projectState
operator|.
name|statePermitsRead
argument_list|()
return|;
block|}
DECL|method|checkProjectPermission ( PermissionBackend.ForProject forProject, ProjectPermission perm)
specifier|private
name|boolean
name|checkProjectPermission
parameter_list|(
name|PermissionBackend
operator|.
name|ForProject
name|forProject
parameter_list|,
name|ProjectPermission
name|perm
parameter_list|)
block|{
try|try
block|{
name|forProject
operator|.
name|check
argument_list|(
name|perm
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|PermissionBackendException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Can't check permission for user %s on project %s"
argument_list|,
name|user
argument_list|,
name|projectState
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|isGroupOwner ( InternalGroup group, @Nullable IdentifiedUser user, boolean isAdmin)
specifier|private
name|boolean
name|isGroupOwner
parameter_list|(
name|InternalGroup
name|group
parameter_list|,
annotation|@
name|Nullable
name|IdentifiedUser
name|user
parameter_list|,
name|boolean
name|isAdmin
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|group
argument_list|)
expr_stmt|;
comment|// Keep this logic in sync with GroupControl#isOwner().
return|return
name|isAdmin
operator|||
operator|(
name|user
operator|!=
literal|null
operator|&&
name|user
operator|.
name|getEffectiveGroups
argument_list|()
operator|.
name|contains
argument_list|(
name|group
operator|.
name|getOwnerGroupUUID
argument_list|()
argument_list|)
operator|)
return|;
block|}
block|}
end_class

end_unit

