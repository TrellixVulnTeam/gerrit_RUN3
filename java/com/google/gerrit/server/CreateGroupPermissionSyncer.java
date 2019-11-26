begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
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
name|toList
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
name|data
operator|.
name|AccessSection
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
name|Permission
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
name|PermissionRule
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
name|entities
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
name|extensions
operator|.
name|events
operator|.
name|ChangeMergedListener
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
name|AllProjectsName
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
name|meta
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
name|ProjectConfig
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
name|HashSet
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

begin_comment
comment|/**  * With groups in NoteDb, the capability of creating a group is expressed as a {@code CREATE}  * permission on {@code refs/groups/*} rather than a global capability in {@code All-Projects}.  *  *<p>During the transition phase, we have to keep these permissions in sync with the global  * capabilities that serve as the source of truth.  *  *<p>This class implements a one-way synchronization from the global {@code CREATE_GROUP}  * capability in {@code All-Projects} to a {@code CREATE} permission on {@code refs/groups/*} in  * {@code All-Users}.  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|CreateGroupPermissionSyncer
specifier|public
class|class
name|CreateGroupPermissionSyncer
implements|implements
name|ChangeMergedListener
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
DECL|field|allProjects
specifier|private
specifier|final
name|AllProjectsName
name|allProjects
decl_stmt|;
DECL|field|allUsers
specifier|private
specifier|final
name|AllUsersName
name|allUsers
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|metaDataUpdateFactory
specifier|private
specifier|final
name|Provider
argument_list|<
name|MetaDataUpdate
operator|.
name|Server
argument_list|>
name|metaDataUpdateFactory
decl_stmt|;
DECL|field|projectConfigFactory
specifier|private
specifier|final
name|ProjectConfig
operator|.
name|Factory
name|projectConfigFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|CreateGroupPermissionSyncer ( AllProjectsName allProjects, AllUsersName allUsers, ProjectCache projectCache, Provider<MetaDataUpdate.Server> metaDataUpdateFactory, ProjectConfig.Factory projectConfigFactory)
name|CreateGroupPermissionSyncer
parameter_list|(
name|AllProjectsName
name|allProjects
parameter_list|,
name|AllUsersName
name|allUsers
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|Provider
argument_list|<
name|MetaDataUpdate
operator|.
name|Server
argument_list|>
name|metaDataUpdateFactory
parameter_list|,
name|ProjectConfig
operator|.
name|Factory
name|projectConfigFactory
parameter_list|)
block|{
name|this
operator|.
name|allProjects
operator|=
name|allProjects
expr_stmt|;
name|this
operator|.
name|allUsers
operator|=
name|allUsers
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|metaDataUpdateFactory
operator|=
name|metaDataUpdateFactory
expr_stmt|;
name|this
operator|.
name|projectConfigFactory
operator|=
name|projectConfigFactory
expr_stmt|;
block|}
comment|/**    * Checks if {@code GlobalCapability.CREATE_GROUP} and {@code CREATE} permission on {@code    * refs/groups/*} have diverged and syncs them by applying the {@code CREATE} permission to {@code    * refs/groups/*}.    */
DECL|method|syncIfNeeded ()
specifier|public
name|void
name|syncIfNeeded
parameter_list|()
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|ProjectState
name|allProjectsState
init|=
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|allProjects
argument_list|)
decl_stmt|;
name|requireNonNull
argument_list|(
name|allProjectsState
argument_list|,
parameter_list|()
lambda|->
name|String
operator|.
name|format
argument_list|(
literal|"Can't obtain project state for %s"
argument_list|,
name|allProjects
argument_list|)
argument_list|)
expr_stmt|;
name|ProjectState
name|allUsersState
init|=
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|allUsers
argument_list|)
decl_stmt|;
name|requireNonNull
argument_list|(
name|allUsersState
argument_list|,
parameter_list|()
lambda|->
name|String
operator|.
name|format
argument_list|(
literal|"Can't obtain project state for %s"
argument_list|,
name|allUsers
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|PermissionRule
argument_list|>
name|createGroupsGlobal
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|allProjectsState
operator|.
name|getCapabilityCollection
argument_list|()
operator|.
name|createGroup
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|PermissionRule
argument_list|>
name|createGroupsRef
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|AccessSection
name|allUsersCreateGroupAccessSection
init|=
name|allUsersState
operator|.
name|getConfig
argument_list|()
operator|.
name|getAccessSection
argument_list|(
name|RefNames
operator|.
name|REFS_GROUPS
operator|+
literal|"*"
argument_list|)
decl_stmt|;
if|if
condition|(
name|allUsersCreateGroupAccessSection
operator|!=
literal|null
condition|)
block|{
name|Permission
name|create
init|=
name|allUsersCreateGroupAccessSection
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|CREATE
argument_list|)
decl_stmt|;
if|if
condition|(
name|create
operator|!=
literal|null
operator|&&
name|create
operator|.
name|getRules
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|createGroupsRef
operator|.
name|addAll
argument_list|(
name|create
operator|.
name|getRules
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|Sets
operator|.
name|symmetricDifference
argument_list|(
name|createGroupsGlobal
argument_list|,
name|createGroupsRef
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Nothing to sync
return|return;
block|}
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
name|allUsers
argument_list|)
init|)
block|{
name|ProjectConfig
name|config
init|=
name|projectConfigFactory
operator|.
name|read
argument_list|(
name|md
argument_list|)
decl_stmt|;
name|AccessSection
name|createGroupAccessSection
init|=
name|config
operator|.
name|getAccessSection
argument_list|(
name|RefNames
operator|.
name|REFS_GROUPS
operator|+
literal|"*"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|createGroupsGlobal
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|createGroupAccessSection
operator|.
name|setPermissions
argument_list|(
name|createGroupAccessSection
operator|.
name|getPermissions
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|p
lambda|->
operator|!
name|Permission
operator|.
name|CREATE
operator|.
name|equals
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|replace
argument_list|(
name|createGroupAccessSection
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// The create permission is managed by Gerrit at this point only so there is no concern of
comment|// overwriting user-defined permissions here.
name|Permission
name|createGroupPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|CREATE
argument_list|)
decl_stmt|;
name|createGroupAccessSection
operator|.
name|remove
argument_list|(
name|createGroupPermission
argument_list|)
expr_stmt|;
name|createGroupAccessSection
operator|.
name|addPermission
argument_list|(
name|createGroupPermission
argument_list|)
expr_stmt|;
name|createGroupsGlobal
operator|.
name|forEach
argument_list|(
name|createGroupPermission
operator|::
name|add
argument_list|)
expr_stmt|;
name|config
operator|.
name|replace
argument_list|(
name|createGroupAccessSection
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|projectCache
operator|.
name|evict
argument_list|(
name|config
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onChangeMerged (Event event)
specifier|public
name|void
name|onChangeMerged
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
if|if
condition|(
operator|!
name|allProjects
operator|.
name|get
argument_list|()
operator|.
name|equals
argument_list|(
name|event
operator|.
name|getChange
argument_list|()
operator|.
name|project
argument_list|)
operator|||
operator|!
name|RefNames
operator|.
name|REFS_CONFIG
operator|.
name|equals
argument_list|(
name|event
operator|.
name|getChange
argument_list|()
operator|.
name|branch
argument_list|)
condition|)
block|{
return|return;
block|}
try|try
block|{
name|syncIfNeeded
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|ConfigInvalidException
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
literal|"Can't sync create group permissions"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

