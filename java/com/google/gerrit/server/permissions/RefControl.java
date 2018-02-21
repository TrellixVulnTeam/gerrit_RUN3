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
name|checkArgument
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
name|common
operator|.
name|data
operator|.
name|PermissionRule
operator|.
name|Action
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
name|BooleanProjectConfig
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
name|permissions
operator|.
name|PermissionBackend
operator|.
name|ForChange
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
name|ForRef
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
name|gerrit
operator|.
name|server
operator|.
name|util
operator|.
name|MagicBranch
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
name|util
operator|.
name|Providers
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

begin_comment
comment|/** Manages access control for Git references (aka branches, tags). */
end_comment

begin_class
DECL|class|RefControl
class|class
name|RefControl
block|{
DECL|field|projectControl
specifier|private
specifier|final
name|ProjectControl
name|projectControl
decl_stmt|;
DECL|field|refName
specifier|private
specifier|final
name|String
name|refName
decl_stmt|;
comment|/** All permissions that apply to this reference. */
DECL|field|relevant
specifier|private
specifier|final
name|PermissionCollection
name|relevant
decl_stmt|;
comment|// The next 4 members are cached canPerform() permissions.
DECL|field|owner
specifier|private
name|Boolean
name|owner
decl_stmt|;
DECL|field|canForgeAuthor
specifier|private
name|Boolean
name|canForgeAuthor
decl_stmt|;
DECL|field|canForgeCommitter
specifier|private
name|Boolean
name|canForgeCommitter
decl_stmt|;
DECL|field|isVisible
specifier|private
name|Boolean
name|isVisible
decl_stmt|;
DECL|method|RefControl (ProjectControl projectControl, String ref, PermissionCollection relevant)
name|RefControl
parameter_list|(
name|ProjectControl
name|projectControl
parameter_list|,
name|String
name|ref
parameter_list|,
name|PermissionCollection
name|relevant
parameter_list|)
block|{
name|this
operator|.
name|projectControl
operator|=
name|projectControl
expr_stmt|;
name|this
operator|.
name|refName
operator|=
name|ref
expr_stmt|;
name|this
operator|.
name|relevant
operator|=
name|relevant
expr_stmt|;
block|}
DECL|method|getProjectControl ()
name|ProjectControl
name|getProjectControl
parameter_list|()
block|{
return|return
name|projectControl
return|;
block|}
DECL|method|getUser ()
name|CurrentUser
name|getUser
parameter_list|()
block|{
return|return
name|projectControl
operator|.
name|getUser
argument_list|()
return|;
block|}
DECL|method|forUser (CurrentUser who)
name|RefControl
name|forUser
parameter_list|(
name|CurrentUser
name|who
parameter_list|)
block|{
name|ProjectControl
name|newCtl
init|=
name|projectControl
operator|.
name|forUser
argument_list|(
name|who
argument_list|)
decl_stmt|;
if|if
condition|(
name|relevant
operator|.
name|isUserSpecific
argument_list|()
condition|)
block|{
return|return
name|newCtl
operator|.
name|controlForRef
argument_list|(
name|refName
argument_list|)
return|;
block|}
return|return
operator|new
name|RefControl
argument_list|(
name|newCtl
argument_list|,
name|refName
argument_list|,
name|relevant
argument_list|)
return|;
block|}
comment|/** Is this user a ref owner? */
DECL|method|isOwner ()
name|boolean
name|isOwner
parameter_list|()
block|{
if|if
condition|(
name|owner
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|canPerform
argument_list|(
name|Permission
operator|.
name|OWNER
argument_list|)
condition|)
block|{
name|owner
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|owner
operator|=
name|projectControl
operator|.
name|isOwner
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|owner
return|;
block|}
comment|/** Can this user see this reference exists? */
DECL|method|isVisible ()
name|boolean
name|isVisible
parameter_list|()
block|{
if|if
condition|(
name|isVisible
operator|==
literal|null
condition|)
block|{
name|isVisible
operator|=
name|getUser
argument_list|()
operator|.
name|isInternalUser
argument_list|()
operator|||
name|canPerform
argument_list|(
name|Permission
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
return|return
name|isVisible
return|;
block|}
comment|/** @return true if this user can add a new patch set to this ref */
DECL|method|canAddPatchSet ()
name|boolean
name|canAddPatchSet
parameter_list|()
block|{
return|return
name|projectControl
operator|.
name|controlForRef
argument_list|(
name|MagicBranch
operator|.
name|NEW_CHANGE
operator|+
name|refName
argument_list|)
operator|.
name|canPerform
argument_list|(
name|Permission
operator|.
name|ADD_PATCH_SET
argument_list|)
return|;
block|}
comment|/** @return true if this user can rebase changes on this ref */
DECL|method|canRebase ()
name|boolean
name|canRebase
parameter_list|()
block|{
return|return
name|canPerform
argument_list|(
name|Permission
operator|.
name|REBASE
argument_list|)
return|;
block|}
comment|/** @return true if this user can submit patch sets to this ref */
DECL|method|canSubmit (boolean isChangeOwner)
name|boolean
name|canSubmit
parameter_list|(
name|boolean
name|isChangeOwner
parameter_list|)
block|{
if|if
condition|(
name|RefNames
operator|.
name|REFS_CONFIG
operator|.
name|equals
argument_list|(
name|refName
argument_list|)
condition|)
block|{
comment|// Always allow project owners to submit configuration changes.
comment|// Submitting configuration changes modifies the access control
comment|// rules. Allowing this to be done by a non-project-owner opens
comment|// a security hole enabling editing of access rules, and thus
comment|// granting of powers beyond submitting to the configuration.
return|return
name|projectControl
operator|.
name|isOwner
argument_list|()
return|;
block|}
return|return
name|canPerform
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|,
name|isChangeOwner
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** @return true if this user can force edit topic names. */
DECL|method|canForceEditTopicName ()
name|boolean
name|canForceEditTopicName
parameter_list|()
block|{
return|return
name|canPerform
argument_list|(
name|Permission
operator|.
name|EDIT_TOPIC_NAME
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** The range of permitted values associated with a label permission. */
DECL|method|getRange (String permission)
name|PermissionRange
name|getRange
parameter_list|(
name|String
name|permission
parameter_list|)
block|{
return|return
name|getRange
argument_list|(
name|permission
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/** The range of permitted values associated with a label permission. */
DECL|method|getRange (String permission, boolean isChangeOwner)
name|PermissionRange
name|getRange
parameter_list|(
name|String
name|permission
parameter_list|,
name|boolean
name|isChangeOwner
parameter_list|)
block|{
if|if
condition|(
name|Permission
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
name|isChangeOwner
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/** True if the user has this permission. Works only for non labels. */
DECL|method|canPerform (String permissionName)
name|boolean
name|canPerform
parameter_list|(
name|String
name|permissionName
parameter_list|)
block|{
return|return
name|canPerform
argument_list|(
name|permissionName
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|asForRef ()
name|ForRef
name|asForRef
parameter_list|()
block|{
return|return
operator|new
name|ForRefImpl
argument_list|()
return|;
block|}
DECL|method|canUpload ()
specifier|private
name|boolean
name|canUpload
parameter_list|()
block|{
return|return
name|projectControl
operator|.
name|controlForRef
argument_list|(
literal|"refs/for/"
operator|+
name|refName
argument_list|)
operator|.
name|canPerform
argument_list|(
name|Permission
operator|.
name|PUSH
argument_list|)
return|;
block|}
comment|/** @return true if this user can submit merge patch sets to this ref */
DECL|method|canUploadMerges ()
specifier|private
name|boolean
name|canUploadMerges
parameter_list|()
block|{
return|return
name|projectControl
operator|.
name|controlForRef
argument_list|(
literal|"refs/for/"
operator|+
name|refName
argument_list|)
operator|.
name|canPerform
argument_list|(
name|Permission
operator|.
name|PUSH_MERGE
argument_list|)
return|;
block|}
comment|/** @return true if the user can update the reference as a fast-forward. */
DECL|method|canUpdate ()
specifier|private
name|boolean
name|canUpdate
parameter_list|()
block|{
if|if
condition|(
name|RefNames
operator|.
name|REFS_CONFIG
operator|.
name|equals
argument_list|(
name|refName
argument_list|)
operator|&&
operator|!
name|projectControl
operator|.
name|isOwner
argument_list|()
condition|)
block|{
comment|// Pushing requires being at least project owner, in addition to push.
comment|// Pushing configuration changes modifies the access control
comment|// rules. Allowing this to be done by a non-project-owner opens
comment|// a security hole enabling editing of access rules, and thus
comment|// granting of powers beyond pushing to the configuration.
comment|// On the AllProjects project the owner access right cannot be assigned,
comment|// this why for the AllProjects project we allow administrators to push
comment|// configuration changes if they have push without being project owner.
if|if
condition|(
operator|!
operator|(
name|projectControl
operator|.
name|getProjectState
argument_list|()
operator|.
name|isAllProjects
argument_list|()
operator|&&
name|projectControl
operator|.
name|isAdmin
argument_list|()
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
name|canPerform
argument_list|(
name|Permission
operator|.
name|PUSH
argument_list|)
return|;
block|}
comment|/** @return true if the user can rewind (force push) the reference. */
DECL|method|canForceUpdate ()
specifier|private
name|boolean
name|canForceUpdate
parameter_list|()
block|{
if|if
condition|(
name|canPushWithForce
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
switch|switch
condition|(
name|getUser
argument_list|()
operator|.
name|getAccessPath
argument_list|()
condition|)
block|{
case|case
name|GIT
case|:
return|return
literal|false
return|;
case|case
name|JSON_RPC
case|:
case|case
name|REST_API
case|:
case|case
name|SSH_COMMAND
case|:
case|case
name|UNKNOWN
case|:
case|case
name|WEB_BROWSER
case|:
default|default:
return|return
operator|(
name|isOwner
argument_list|()
operator|&&
operator|!
name|isBlocked
argument_list|(
name|Permission
operator|.
name|PUSH
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
operator|)
operator|||
name|projectControl
operator|.
name|isAdmin
argument_list|()
return|;
block|}
block|}
DECL|method|canPushWithForce ()
specifier|private
name|boolean
name|canPushWithForce
parameter_list|()
block|{
if|if
condition|(
name|RefNames
operator|.
name|REFS_CONFIG
operator|.
name|equals
argument_list|(
name|refName
argument_list|)
operator|&&
operator|!
name|projectControl
operator|.
name|isOwner
argument_list|()
condition|)
block|{
comment|// Pushing requires being at least project owner, in addition to push.
comment|// Pushing configuration changes modifies the access control
comment|// rules. Allowing this to be done by a non-project-owner opens
comment|// a security hole enabling editing of access rules, and thus
comment|// granting of powers beyond pushing to the configuration.
return|return
literal|false
return|;
block|}
return|return
name|canPerform
argument_list|(
name|Permission
operator|.
name|PUSH
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Determines whether the user can delete the Git ref controlled by this object.    *    * @return {@code true} if the user specified can delete a Git ref.    */
DECL|method|canDelete ()
specifier|private
name|boolean
name|canDelete
parameter_list|()
block|{
if|if
condition|(
name|RefNames
operator|.
name|REFS_CONFIG
operator|.
name|equals
argument_list|(
name|refName
argument_list|)
condition|)
block|{
comment|// Never allow removal of the refs/meta/config branch.
comment|// Deleting the branch would destroy all Gerrit specific
comment|// metadata about the project, including its access rules.
comment|// If a project is to be removed from Gerrit, its repository
comment|// should be removed first.
return|return
literal|false
return|;
block|}
switch|switch
condition|(
name|getUser
argument_list|()
operator|.
name|getAccessPath
argument_list|()
condition|)
block|{
case|case
name|GIT
case|:
return|return
name|canPushWithForce
argument_list|()
operator|||
name|canPerform
argument_list|(
name|Permission
operator|.
name|DELETE
argument_list|)
return|;
case|case
name|JSON_RPC
case|:
case|case
name|REST_API
case|:
case|case
name|SSH_COMMAND
case|:
case|case
name|UNKNOWN
case|:
case|case
name|WEB_BROWSER
case|:
default|default:
return|return
comment|// We allow owner to delete refs even if they have no force-push rights. We forbid
comment|// it if force push is blocked, though. See commit 40bd5741026863c99bea13eb5384bd27855c5e1b
operator|(
name|isOwner
argument_list|()
operator|&&
operator|!
name|isBlocked
argument_list|(
name|Permission
operator|.
name|PUSH
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
operator|)
operator|||
name|canPushWithForce
argument_list|()
operator|||
name|canPerform
argument_list|(
name|Permission
operator|.
name|DELETE
argument_list|)
operator|||
name|projectControl
operator|.
name|isAdmin
argument_list|()
return|;
block|}
block|}
comment|/** @return true if this user can forge the author line in a commit. */
DECL|method|canForgeAuthor ()
specifier|private
name|boolean
name|canForgeAuthor
parameter_list|()
block|{
if|if
condition|(
name|canForgeAuthor
operator|==
literal|null
condition|)
block|{
name|canForgeAuthor
operator|=
name|canPerform
argument_list|(
name|Permission
operator|.
name|FORGE_AUTHOR
argument_list|)
expr_stmt|;
block|}
return|return
name|canForgeAuthor
return|;
block|}
comment|/** @return true if this user can forge the committer line in a commit. */
DECL|method|canForgeCommitter ()
specifier|private
name|boolean
name|canForgeCommitter
parameter_list|()
block|{
if|if
condition|(
name|canForgeCommitter
operator|==
literal|null
condition|)
block|{
name|canForgeCommitter
operator|=
name|canPerform
argument_list|(
name|Permission
operator|.
name|FORGE_COMMITTER
argument_list|)
expr_stmt|;
block|}
return|return
name|canForgeCommitter
return|;
block|}
comment|/** @return true if this user can forge the server on the committer line. */
DECL|method|canForgeGerritServerIdentity ()
specifier|private
name|boolean
name|canForgeGerritServerIdentity
parameter_list|()
block|{
return|return
name|canPerform
argument_list|(
name|Permission
operator|.
name|FORGE_SERVER
argument_list|)
return|;
block|}
DECL|method|isAllow (PermissionRule pr, boolean withForce)
specifier|private
specifier|static
name|boolean
name|isAllow
parameter_list|(
name|PermissionRule
name|pr
parameter_list|,
name|boolean
name|withForce
parameter_list|)
block|{
return|return
name|pr
operator|.
name|getAction
argument_list|()
operator|==
name|Action
operator|.
name|ALLOW
operator|&&
operator|(
name|pr
operator|.
name|getForce
argument_list|()
operator|||
operator|!
name|withForce
operator|)
return|;
block|}
DECL|method|isBlock (PermissionRule pr, boolean withForce)
specifier|private
specifier|static
name|boolean
name|isBlock
parameter_list|(
name|PermissionRule
name|pr
parameter_list|,
name|boolean
name|withForce
parameter_list|)
block|{
comment|// BLOCK with force specified is a weaker rule than without.
return|return
name|pr
operator|.
name|getAction
argument_list|()
operator|==
name|Action
operator|.
name|BLOCK
operator|&&
operator|(
operator|!
name|pr
operator|.
name|getForce
argument_list|()
operator|||
name|withForce
operator|)
return|;
block|}
DECL|method|toRange (String permissionName, boolean isChangeOwner)
specifier|private
name|PermissionRange
name|toRange
parameter_list|(
name|String
name|permissionName
parameter_list|,
name|boolean
name|isChangeOwner
parameter_list|)
block|{
name|int
name|blockAllowMin
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|,
name|blockAllowMax
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|projectLoop
label|:
for|for
control|(
name|List
argument_list|<
name|Permission
argument_list|>
name|ps
range|:
name|relevant
operator|.
name|getBlockRules
argument_list|(
name|permissionName
argument_list|)
control|)
block|{
name|boolean
name|blockFound
init|=
literal|false
decl_stmt|;
name|int
name|projectBlockAllowMin
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|,
name|projectBlockAllowMax
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|Permission
name|p
range|:
name|ps
control|)
block|{
if|if
condition|(
name|p
operator|.
name|getExclusiveGroup
argument_list|()
condition|)
block|{
for|for
control|(
name|PermissionRule
name|pr
range|:
name|p
operator|.
name|getRules
argument_list|()
control|)
block|{
if|if
condition|(
name|pr
operator|.
name|getAction
argument_list|()
operator|==
name|Action
operator|.
name|ALLOW
operator|&&
name|projectControl
operator|.
name|match
argument_list|(
name|pr
argument_list|,
name|isChangeOwner
argument_list|)
condition|)
block|{
comment|// exclusive override, usually for a more specific ref.
continue|continue
name|projectLoop
continue|;
block|}
block|}
block|}
for|for
control|(
name|PermissionRule
name|pr
range|:
name|p
operator|.
name|getRules
argument_list|()
control|)
block|{
if|if
condition|(
name|pr
operator|.
name|getAction
argument_list|()
operator|==
name|Action
operator|.
name|BLOCK
operator|&&
name|projectControl
operator|.
name|match
argument_list|(
name|pr
argument_list|,
name|isChangeOwner
argument_list|)
condition|)
block|{
name|projectBlockAllowMin
operator|=
name|pr
operator|.
name|getMin
argument_list|()
operator|+
literal|1
expr_stmt|;
name|projectBlockAllowMax
operator|=
name|pr
operator|.
name|getMax
argument_list|()
operator|-
literal|1
expr_stmt|;
name|blockFound
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|blockFound
condition|)
block|{
for|for
control|(
name|PermissionRule
name|pr
range|:
name|p
operator|.
name|getRules
argument_list|()
control|)
block|{
if|if
condition|(
name|pr
operator|.
name|getAction
argument_list|()
operator|==
name|Action
operator|.
name|ALLOW
operator|&&
name|projectControl
operator|.
name|match
argument_list|(
name|pr
argument_list|,
name|isChangeOwner
argument_list|)
condition|)
block|{
name|projectBlockAllowMin
operator|=
name|pr
operator|.
name|getMin
argument_list|()
expr_stmt|;
name|projectBlockAllowMax
operator|=
name|pr
operator|.
name|getMax
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
break|break;
block|}
block|}
name|blockAllowMin
operator|=
name|Math
operator|.
name|max
argument_list|(
name|projectBlockAllowMin
argument_list|,
name|blockAllowMin
argument_list|)
expr_stmt|;
name|blockAllowMax
operator|=
name|Math
operator|.
name|min
argument_list|(
name|projectBlockAllowMax
argument_list|,
name|blockAllowMax
argument_list|)
expr_stmt|;
block|}
name|int
name|voteMin
init|=
literal|0
decl_stmt|,
name|voteMax
init|=
literal|0
decl_stmt|;
for|for
control|(
name|PermissionRule
name|pr
range|:
name|relevant
operator|.
name|getAllowRules
argument_list|(
name|permissionName
argument_list|)
control|)
block|{
if|if
condition|(
name|pr
operator|.
name|getAction
argument_list|()
operator|==
name|PermissionRule
operator|.
name|Action
operator|.
name|ALLOW
operator|&&
name|projectControl
operator|.
name|match
argument_list|(
name|pr
argument_list|,
name|isChangeOwner
argument_list|)
condition|)
block|{
comment|// For votes, contrary to normal permissions, we aggregate all applicable rules.
name|voteMin
operator|=
name|Math
operator|.
name|min
argument_list|(
name|voteMin
argument_list|,
name|pr
operator|.
name|getMin
argument_list|()
argument_list|)
expr_stmt|;
name|voteMax
operator|=
name|Math
operator|.
name|max
argument_list|(
name|voteMax
argument_list|,
name|pr
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|PermissionRange
argument_list|(
name|permissionName
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|voteMin
argument_list|,
name|blockAllowMin
argument_list|)
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|voteMax
argument_list|,
name|blockAllowMax
argument_list|)
argument_list|)
return|;
block|}
DECL|method|isBlocked (String permissionName, boolean isChangeOwner, boolean withForce)
specifier|private
name|boolean
name|isBlocked
parameter_list|(
name|String
name|permissionName
parameter_list|,
name|boolean
name|isChangeOwner
parameter_list|,
name|boolean
name|withForce
parameter_list|)
block|{
comment|// Permissions are ordered by (more general project, more specific ref). Because Permission
comment|// does not have back pointers, we can't tell what ref-pattern or project each permission comes
comment|// from.
name|List
argument_list|<
name|List
argument_list|<
name|Permission
argument_list|>
argument_list|>
name|downwardPerProject
init|=
name|relevant
operator|.
name|getBlockRules
argument_list|(
name|permissionName
argument_list|)
decl_stmt|;
name|projectLoop
label|:
for|for
control|(
name|List
argument_list|<
name|Permission
argument_list|>
name|projectRules
range|:
name|downwardPerProject
control|)
block|{
name|boolean
name|overrideFound
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Permission
name|p
range|:
name|projectRules
control|)
block|{
comment|// If this is an exclusive ALLOW, then block rules from the same project are ignored.
if|if
condition|(
name|p
operator|.
name|getExclusiveGroup
argument_list|()
condition|)
block|{
for|for
control|(
name|PermissionRule
name|pr
range|:
name|p
operator|.
name|getRules
argument_list|()
control|)
block|{
if|if
condition|(
name|isAllow
argument_list|(
name|pr
argument_list|,
name|withForce
argument_list|)
operator|&&
name|projectControl
operator|.
name|match
argument_list|(
name|pr
argument_list|,
name|isChangeOwner
argument_list|)
condition|)
block|{
name|overrideFound
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|overrideFound
condition|)
block|{
comment|// Found an exclusive override, nothing further to do in this project.
continue|continue
name|projectLoop
continue|;
block|}
name|boolean
name|blocked
init|=
literal|false
decl_stmt|;
for|for
control|(
name|PermissionRule
name|pr
range|:
name|p
operator|.
name|getRules
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|withForce
operator|&&
name|pr
operator|.
name|getForce
argument_list|()
condition|)
block|{
comment|// force on block rule only applies to withForce permission.
continue|continue;
block|}
if|if
condition|(
name|isBlock
argument_list|(
name|pr
argument_list|,
name|withForce
argument_list|)
operator|&&
name|projectControl
operator|.
name|match
argument_list|(
name|pr
argument_list|,
name|isChangeOwner
argument_list|)
condition|)
block|{
name|blocked
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|blocked
condition|)
block|{
comment|// ALLOW in the same AccessSection (ie. in the same Permission) overrides the BLOCK.
for|for
control|(
name|PermissionRule
name|pr
range|:
name|p
operator|.
name|getRules
argument_list|()
control|)
block|{
if|if
condition|(
name|isAllow
argument_list|(
name|pr
argument_list|,
name|withForce
argument_list|)
operator|&&
name|projectControl
operator|.
name|match
argument_list|(
name|pr
argument_list|,
name|isChangeOwner
argument_list|)
condition|)
block|{
name|blocked
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|blocked
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/** True if the user has this permission. */
DECL|method|canPerform (String permissionName, boolean isChangeOwner, boolean withForce)
specifier|private
name|boolean
name|canPerform
parameter_list|(
name|String
name|permissionName
parameter_list|,
name|boolean
name|isChangeOwner
parameter_list|,
name|boolean
name|withForce
parameter_list|)
block|{
if|if
condition|(
name|isBlocked
argument_list|(
name|permissionName
argument_list|,
name|isChangeOwner
argument_list|,
name|withForce
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|PermissionRule
name|pr
range|:
name|relevant
operator|.
name|getAllowRules
argument_list|(
name|permissionName
argument_list|)
control|)
block|{
if|if
condition|(
name|isAllow
argument_list|(
name|pr
argument_list|,
name|withForce
argument_list|)
operator|&&
name|projectControl
operator|.
name|match
argument_list|(
name|pr
argument_list|,
name|isChangeOwner
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|class|ForRefImpl
specifier|private
class|class
name|ForRefImpl
extends|extends
name|ForRef
block|{
DECL|field|resourcePath
specifier|private
name|String
name|resourcePath
decl_stmt|;
annotation|@
name|Override
DECL|method|user ()
specifier|public
name|CurrentUser
name|user
parameter_list|()
block|{
return|return
name|getUser
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|user (CurrentUser user)
specifier|public
name|ForRef
name|user
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
block|{
return|return
name|forUser
argument_list|(
name|user
argument_list|)
operator|.
name|asForRef
argument_list|()
operator|.
name|database
argument_list|(
name|db
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|resourcePath ()
specifier|public
name|String
name|resourcePath
parameter_list|()
block|{
if|if
condition|(
name|resourcePath
operator|==
literal|null
condition|)
block|{
name|resourcePath
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"/projects/%s/+refs/%s"
argument_list|,
name|getProjectControl
argument_list|()
operator|.
name|getProjectState
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|refName
argument_list|)
expr_stmt|;
block|}
return|return
name|resourcePath
return|;
block|}
annotation|@
name|Override
DECL|method|change (ChangeData cd)
specifier|public
name|ForChange
name|change
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
block|{
try|try
block|{
comment|// TODO(hiesel) Force callers to call database() and use db instead of cd.db()
return|return
name|getProjectControl
argument_list|()
operator|.
name|controlFor
argument_list|(
name|cd
operator|.
name|db
argument_list|()
argument_list|,
name|cd
operator|.
name|change
argument_list|()
argument_list|)
operator|.
name|asForChange
argument_list|(
name|cd
argument_list|,
name|Providers
operator|.
name|of
argument_list|(
name|cd
operator|.
name|db
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
return|return
name|FailedPermissionBackend
operator|.
name|change
argument_list|(
literal|"unavailable"
argument_list|,
name|e
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|change (ChangeNotes notes)
specifier|public
name|ForChange
name|change
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|)
block|{
name|Project
operator|.
name|NameKey
name|project
init|=
name|getProjectControl
argument_list|()
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
decl_stmt|;
name|Change
name|change
init|=
name|notes
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|checkArgument
argument_list|(
name|project
operator|.
name|equals
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
argument_list|,
literal|"expected change in project %s, not %s"
argument_list|,
name|project
argument_list|,
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|getProjectControl
argument_list|()
operator|.
name|controlFor
argument_list|(
name|notes
argument_list|)
operator|.
name|asForChange
argument_list|(
literal|null
argument_list|,
name|db
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|indexedChange (ChangeData cd, ChangeNotes notes)
specifier|public
name|ForChange
name|indexedChange
parameter_list|(
name|ChangeData
name|cd
parameter_list|,
name|ChangeNotes
name|notes
parameter_list|)
block|{
return|return
name|getProjectControl
argument_list|()
operator|.
name|controlFor
argument_list|(
name|notes
argument_list|)
operator|.
name|asForChange
argument_list|(
name|cd
argument_list|,
name|db
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|check (RefPermission perm)
specifier|public
name|void
name|check
parameter_list|(
name|RefPermission
name|perm
parameter_list|)
throws|throws
name|AuthException
throws|,
name|PermissionBackendException
block|{
if|if
condition|(
operator|!
name|can
argument_list|(
name|perm
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
name|perm
operator|.
name|describeForException
argument_list|()
operator|+
literal|" not permitted for "
operator|+
name|refName
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|test (Collection<RefPermission> permSet)
specifier|public
name|Set
argument_list|<
name|RefPermission
argument_list|>
name|test
parameter_list|(
name|Collection
argument_list|<
name|RefPermission
argument_list|>
name|permSet
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
name|EnumSet
argument_list|<
name|RefPermission
argument_list|>
name|ok
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|RefPermission
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|RefPermission
name|perm
range|:
name|permSet
control|)
block|{
if|if
condition|(
name|can
argument_list|(
name|perm
argument_list|)
condition|)
block|{
name|ok
operator|.
name|add
argument_list|(
name|perm
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ok
return|;
block|}
DECL|method|can (RefPermission perm)
specifier|private
name|boolean
name|can
parameter_list|(
name|RefPermission
name|perm
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
switch|switch
condition|(
name|perm
condition|)
block|{
case|case
name|READ
case|:
return|return
name|isVisible
argument_list|()
return|;
case|case
name|CREATE
case|:
comment|// TODO This isn't an accurate test.
return|return
name|canPerform
argument_list|(
name|perm
operator|.
name|permissionName
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
return|;
case|case
name|DELETE
case|:
return|return
name|canDelete
argument_list|()
return|;
case|case
name|UPDATE
case|:
return|return
name|canUpdate
argument_list|()
return|;
case|case
name|FORCE_UPDATE
case|:
return|return
name|canForceUpdate
argument_list|()
return|;
case|case
name|SET_HEAD
case|:
return|return
name|projectControl
operator|.
name|isOwner
argument_list|()
return|;
case|case
name|FORGE_AUTHOR
case|:
return|return
name|canForgeAuthor
argument_list|()
return|;
case|case
name|FORGE_COMMITTER
case|:
return|return
name|canForgeCommitter
argument_list|()
return|;
case|case
name|FORGE_SERVER
case|:
return|return
name|canForgeGerritServerIdentity
argument_list|()
return|;
case|case
name|MERGE
case|:
return|return
name|canUploadMerges
argument_list|()
return|;
case|case
name|CREATE_CHANGE
case|:
return|return
name|canUpload
argument_list|()
return|;
case|case
name|CREATE_TAG
case|:
case|case
name|CREATE_SIGNED_TAG
case|:
return|return
name|canPerform
argument_list|(
name|perm
operator|.
name|permissionName
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
return|;
case|case
name|UPDATE_BY_SUBMIT
case|:
return|return
name|projectControl
operator|.
name|controlForRef
argument_list|(
name|MagicBranch
operator|.
name|NEW_CHANGE
operator|+
name|refName
argument_list|)
operator|.
name|canSubmit
argument_list|(
literal|true
argument_list|)
return|;
case|case
name|READ_PRIVATE_CHANGES
case|:
return|return
name|canPerform
argument_list|(
name|Permission
operator|.
name|VIEW_PRIVATE_CHANGES
argument_list|)
return|;
case|case
name|READ_CONFIG
case|:
return|return
name|projectControl
operator|.
name|controlForRef
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
operator|.
name|canPerform
argument_list|(
name|RefPermission
operator|.
name|READ
operator|.
name|name
argument_list|()
argument_list|)
return|;
case|case
name|WRITE_CONFIG
case|:
return|return
name|isOwner
argument_list|()
return|;
case|case
name|SKIP_VALIDATION
case|:
return|return
name|canForgeAuthor
argument_list|()
operator|&&
name|canForgeCommitter
argument_list|()
operator|&&
name|canForgeGerritServerIdentity
argument_list|()
operator|&&
name|canUploadMerges
argument_list|()
operator|&&
operator|!
name|projectControl
operator|.
name|getProjectState
argument_list|()
operator|.
name|is
argument_list|(
name|BooleanProjectConfig
operator|.
name|USE_SIGNED_OFF_BY
argument_list|)
return|;
block|}
throw|throw
operator|new
name|PermissionBackendException
argument_list|(
name|perm
operator|+
literal|" unsupported"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

