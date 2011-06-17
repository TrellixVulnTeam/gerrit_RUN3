begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
name|GlobalCapability
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
name|PermissionRange
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
name|PeerDaemonUser
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
name|QueueProvider
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
name|assistedinject
operator|.
name|Assisted
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
name|Set
import|;
end_import

begin_comment
comment|/** Access control management for server-wide capabilities. */
end_comment

begin_class
DECL|class|CapabilityControl
specifier|public
class|class
name|CapabilityControl
block|{
DECL|interface|Factory
specifier|public
specifier|static
interface|interface
name|Factory
block|{
DECL|method|create (CurrentUser user)
specifier|public
name|CapabilityControl
name|create
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
function_decl|;
block|}
DECL|field|state
specifier|private
specifier|final
name|ProjectState
name|state
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|CurrentUser
name|user
decl_stmt|;
DECL|field|permissions
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PermissionRule
argument_list|>
argument_list|>
name|permissions
decl_stmt|;
DECL|field|canAdministrateServer
specifier|private
name|Boolean
name|canAdministrateServer
decl_stmt|;
annotation|@
name|Inject
DECL|method|CapabilityControl (ProjectCache projectCache, @Assisted CurrentUser currentUser)
name|CapabilityControl
parameter_list|(
name|ProjectCache
name|projectCache
parameter_list|,
annotation|@
name|Assisted
name|CurrentUser
name|currentUser
parameter_list|)
block|{
name|state
operator|=
name|projectCache
operator|.
name|getAllProjects
argument_list|()
expr_stmt|;
name|user
operator|=
name|currentUser
expr_stmt|;
block|}
comment|/** Identity of the user the control will compute for. */
DECL|method|getCurrentUser ()
specifier|public
name|CurrentUser
name|getCurrentUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
comment|/** @return true if the user can administer this server. */
DECL|method|canAdministrateServer ()
specifier|public
name|boolean
name|canAdministrateServer
parameter_list|()
block|{
if|if
condition|(
name|canAdministrateServer
operator|==
literal|null
condition|)
block|{
name|canAdministrateServer
operator|=
name|user
operator|instanceof
name|PeerDaemonUser
operator|||
name|canPerform
argument_list|(
name|GlobalCapability
operator|.
name|ADMINISTRATE_SERVER
argument_list|)
expr_stmt|;
block|}
return|return
name|canAdministrateServer
return|;
block|}
comment|/** @return true if the user can create an account for another user. */
DECL|method|canCreateAccount ()
specifier|public
name|boolean
name|canCreateAccount
parameter_list|()
block|{
return|return
name|canPerform
argument_list|(
name|GlobalCapability
operator|.
name|CREATE_ACCOUNT
argument_list|)
operator|||
name|canAdministrateServer
argument_list|()
return|;
block|}
comment|/** @return true if the user can create a group. */
DECL|method|canCreateGroup ()
specifier|public
name|boolean
name|canCreateGroup
parameter_list|()
block|{
return|return
name|canPerform
argument_list|(
name|GlobalCapability
operator|.
name|CREATE_GROUP
argument_list|)
operator|||
name|canAdministrateServer
argument_list|()
return|;
block|}
comment|/** @return true if the user can create a group. */
DECL|method|canCreateProject ()
specifier|public
name|boolean
name|canCreateProject
parameter_list|()
block|{
return|return
name|canPerform
argument_list|(
name|GlobalCapability
operator|.
name|CREATE_PROJECT
argument_list|)
operator|||
name|canAdministrateServer
argument_list|()
return|;
block|}
comment|/** @return true if the user can kill any running task. */
DECL|method|canKillTask ()
specifier|public
name|boolean
name|canKillTask
parameter_list|()
block|{
return|return
name|canPerform
argument_list|(
name|GlobalCapability
operator|.
name|KILL_TASK
argument_list|)
operator|||
name|canAdministrateServer
argument_list|()
return|;
block|}
comment|/** @return true if the user can view the server caches. */
DECL|method|canViewCaches ()
specifier|public
name|boolean
name|canViewCaches
parameter_list|()
block|{
return|return
name|canPerform
argument_list|(
name|GlobalCapability
operator|.
name|VIEW_CACHES
argument_list|)
operator|||
name|canAdministrateServer
argument_list|()
return|;
block|}
comment|/** @return true if the user can flush the server's caches. */
DECL|method|canFlushCaches ()
specifier|public
name|boolean
name|canFlushCaches
parameter_list|()
block|{
return|return
name|canPerform
argument_list|(
name|GlobalCapability
operator|.
name|FLUSH_CACHES
argument_list|)
operator|||
name|canAdministrateServer
argument_list|()
return|;
block|}
comment|/** @return true if the user can view open connections. */
DECL|method|canViewConnections ()
specifier|public
name|boolean
name|canViewConnections
parameter_list|()
block|{
return|return
name|canPerform
argument_list|(
name|GlobalCapability
operator|.
name|VIEW_CONNECTIONS
argument_list|)
operator|||
name|canAdministrateServer
argument_list|()
return|;
block|}
comment|/** @return true if the user can view the entire queue. */
DECL|method|canViewQueue ()
specifier|public
name|boolean
name|canViewQueue
parameter_list|()
block|{
return|return
name|canPerform
argument_list|(
name|GlobalCapability
operator|.
name|VIEW_QUEUE
argument_list|)
operator|||
name|canAdministrateServer
argument_list|()
return|;
block|}
comment|/** @return true if the user can force replication to any configured destination. */
DECL|method|canStartReplication ()
specifier|public
name|boolean
name|canStartReplication
parameter_list|()
block|{
return|return
name|canPerform
argument_list|(
name|GlobalCapability
operator|.
name|START_REPLICATION
argument_list|)
operator|||
name|canAdministrateServer
argument_list|()
return|;
block|}
comment|/** @return which priority queue the user's tasks should be submitted to. */
DECL|method|getQueueType ()
specifier|public
name|QueueProvider
operator|.
name|QueueType
name|getQueueType
parameter_list|()
block|{
comment|// If a non-generic group (that is not Anonymous Users or Registered Users)
comment|// grants us INTERACTIVE permission, use the INTERACTIVE queue even if
comment|// BATCH was otherwise granted. This allows site administrators to grant
comment|// INTERACTIVE to Registered Users, and BATCH to 'CI Servers' and have
comment|// the 'CI Servers' actually use the BATCH queue while everyone else gets
comment|// to use the INTERACTIVE queue without additional grants.
comment|//
name|List
argument_list|<
name|PermissionRule
argument_list|>
name|rules
init|=
name|access
argument_list|(
name|GlobalCapability
operator|.
name|PRIORITY
argument_list|)
decl_stmt|;
name|boolean
name|batch
init|=
literal|false
decl_stmt|;
for|for
control|(
name|PermissionRule
name|r
range|:
name|rules
control|)
block|{
switch|switch
condition|(
name|r
operator|.
name|getAction
argument_list|()
condition|)
block|{
case|case
name|INTERACTIVE
case|:
if|if
condition|(
operator|!
name|isGenericGroup
argument_list|(
name|r
operator|.
name|getGroup
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|QueueProvider
operator|.
name|QueueType
operator|.
name|INTERACTIVE
return|;
block|}
break|break;
case|case
name|BATCH
case|:
name|batch
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|batch
condition|)
block|{
comment|// If any of our groups matched to the BATCH queue, use it.
return|return
name|QueueProvider
operator|.
name|QueueType
operator|.
name|BATCH
return|;
block|}
else|else
block|{
return|return
name|QueueProvider
operator|.
name|QueueType
operator|.
name|INTERACTIVE
return|;
block|}
block|}
DECL|method|isGenericGroup (GroupReference group)
specifier|private
specifier|static
name|boolean
name|isGenericGroup
parameter_list|(
name|GroupReference
name|group
parameter_list|)
block|{
return|return
name|AccountGroup
operator|.
name|ANONYMOUS_USERS
operator|.
name|equals
argument_list|(
name|group
operator|.
name|getUUID
argument_list|()
argument_list|)
operator|||
name|AccountGroup
operator|.
name|REGISTERED_USERS
operator|.
name|equals
argument_list|(
name|group
operator|.
name|getUUID
argument_list|()
argument_list|)
return|;
block|}
comment|/** True if the user has this permission. Works only for non labels. */
DECL|method|canPerform (String permissionName)
specifier|public
name|boolean
name|canPerform
parameter_list|(
name|String
name|permissionName
parameter_list|)
block|{
return|return
operator|!
name|access
argument_list|(
name|permissionName
argument_list|)
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/** The range of permitted values associated with a label permission. */
DECL|method|getRange (String permission)
specifier|public
name|PermissionRange
name|getRange
parameter_list|(
name|String
name|permission
parameter_list|)
block|{
if|if
condition|(
name|GlobalCapability
operator|.
name|hasRange
argument_list|(
name|permission
argument_list|)
condition|)
block|{
return|return
name|toRange
argument_list|(
name|permission
argument_list|,
name|access
argument_list|(
name|permission
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|toRange (String permissionName, List<PermissionRule> ruleList)
specifier|private
specifier|static
name|PermissionRange
name|toRange
parameter_list|(
name|String
name|permissionName
parameter_list|,
name|List
argument_list|<
name|PermissionRule
argument_list|>
name|ruleList
parameter_list|)
block|{
name|int
name|min
init|=
literal|0
decl_stmt|;
name|int
name|max
init|=
literal|0
decl_stmt|;
for|for
control|(
name|PermissionRule
name|rule
range|:
name|ruleList
control|)
block|{
name|min
operator|=
name|Math
operator|.
name|min
argument_list|(
name|min
argument_list|,
name|rule
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|rule
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|PermissionRange
argument_list|(
name|permissionName
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
return|;
block|}
comment|/** Rules for the given permission, or the empty list. */
DECL|method|access (String permissionName)
specifier|private
name|List
argument_list|<
name|PermissionRule
argument_list|>
name|access
parameter_list|(
name|String
name|permissionName
parameter_list|)
block|{
name|List
argument_list|<
name|PermissionRule
argument_list|>
name|r
init|=
name|permissions
argument_list|()
operator|.
name|get
argument_list|(
name|permissionName
argument_list|)
decl_stmt|;
return|return
name|r
operator|!=
literal|null
condition|?
name|r
else|:
name|Collections
operator|.
expr|<
name|PermissionRule
operator|>
name|emptyList
argument_list|()
return|;
block|}
comment|/** All rules that pertain to this user. */
DECL|method|permissions ()
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PermissionRule
argument_list|>
argument_list|>
name|permissions
parameter_list|()
block|{
if|if
condition|(
name|permissions
operator|==
literal|null
condition|)
block|{
name|permissions
operator|=
name|indexPermissions
argument_list|()
expr_stmt|;
block|}
return|return
name|permissions
return|;
block|}
DECL|method|indexPermissions ()
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PermissionRule
argument_list|>
argument_list|>
name|indexPermissions
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PermissionRule
argument_list|>
argument_list|>
name|res
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PermissionRule
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|AccessSection
name|section
init|=
name|state
operator|.
name|getConfig
argument_list|()
operator|.
name|getAccessSection
argument_list|(
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
argument_list|)
decl_stmt|;
if|if
condition|(
name|section
operator|==
literal|null
condition|)
block|{
name|section
operator|=
operator|new
name|AccessSection
argument_list|(
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Permission
name|permission
range|:
name|section
operator|.
name|getPermissions
argument_list|()
control|)
block|{
for|for
control|(
name|PermissionRule
name|rule
range|:
name|permission
operator|.
name|getRules
argument_list|()
control|)
block|{
if|if
condition|(
name|matchGroup
argument_list|(
name|rule
operator|.
name|getGroup
argument_list|()
operator|.
name|getUUID
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|rule
operator|.
name|getAction
argument_list|()
operator|!=
name|PermissionRule
operator|.
name|Action
operator|.
name|DENY
condition|)
block|{
name|List
argument_list|<
name|PermissionRule
argument_list|>
name|r
init|=
name|res
operator|.
name|get
argument_list|(
name|permission
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
name|r
operator|=
operator|new
name|ArrayList
argument_list|<
name|PermissionRule
argument_list|>
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|res
operator|.
name|put
argument_list|(
name|permission
operator|.
name|getName
argument_list|()
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|add
argument_list|(
name|rule
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|configureDefaults
argument_list|(
name|res
argument_list|,
name|section
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
DECL|method|matchGroup (AccountGroup.UUID uuid)
specifier|private
name|boolean
name|matchGroup
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|userGroups
init|=
name|getCurrentUser
argument_list|()
operator|.
name|getEffectiveGroups
argument_list|()
decl_stmt|;
return|return
name|userGroups
operator|.
name|contains
argument_list|(
name|uuid
argument_list|)
return|;
block|}
DECL|field|anonymous
specifier|private
specifier|static
specifier|final
name|GroupReference
name|anonymous
init|=
operator|new
name|GroupReference
argument_list|(
name|AccountGroup
operator|.
name|ANONYMOUS_USERS
argument_list|,
literal|"Anonymous Users"
argument_list|)
decl_stmt|;
DECL|method|configureDefaults ( Map<String, List<PermissionRule>> res, AccessSection section)
specifier|private
specifier|static
name|void
name|configureDefaults
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PermissionRule
argument_list|>
argument_list|>
name|res
parameter_list|,
name|AccessSection
name|section
parameter_list|)
block|{
name|configureDefault
argument_list|(
name|res
argument_list|,
name|section
argument_list|,
name|GlobalCapability
operator|.
name|QUERY_LIMIT
argument_list|,
name|anonymous
argument_list|)
expr_stmt|;
block|}
DECL|method|configureDefault (Map<String, List<PermissionRule>> res, AccessSection section, String capName, GroupReference group)
specifier|private
specifier|static
name|void
name|configureDefault
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|PermissionRule
argument_list|>
argument_list|>
name|res
parameter_list|,
name|AccessSection
name|section
parameter_list|,
name|String
name|capName
parameter_list|,
name|GroupReference
name|group
parameter_list|)
block|{
if|if
condition|(
name|section
operator|.
name|getPermission
argument_list|(
name|capName
argument_list|)
operator|==
literal|null
condition|)
block|{
name|PermissionRange
operator|.
name|WithDefaults
name|range
init|=
name|GlobalCapability
operator|.
name|getRange
argument_list|(
name|capName
argument_list|)
decl_stmt|;
if|if
condition|(
name|range
operator|!=
literal|null
condition|)
block|{
name|PermissionRule
name|rule
init|=
operator|new
name|PermissionRule
argument_list|(
name|group
argument_list|)
decl_stmt|;
name|rule
operator|.
name|setRange
argument_list|(
name|range
operator|.
name|getDefaultMin
argument_list|()
argument_list|,
name|range
operator|.
name|getDefaultMax
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|.
name|put
argument_list|(
name|capName
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|rule
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

