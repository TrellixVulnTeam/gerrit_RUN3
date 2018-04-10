begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
name|checkState
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
name|server
operator|.
name|permissions
operator|.
name|LabelPermission
operator|.
name|ForUser
operator|.
name|ON_BEHALF_OF
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
name|Maps
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
name|common
operator|.
name|data
operator|.
name|LabelFunction
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
name|LabelType
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
name|PatchSetApproval
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
name|ApprovalsUtil
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
name|PatchSetUtil
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
name|Singleton
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
comment|/** Access control management for a user accessing a single change. */
end_comment

begin_class
DECL|class|ChangeControl
class|class
name|ChangeControl
block|{
annotation|@
name|Singleton
DECL|class|Factory
specifier|static
class|class
name|Factory
block|{
DECL|field|changeDataFactory
specifier|private
specifier|final
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
decl_stmt|;
DECL|field|notesFactory
specifier|private
specifier|final
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
decl_stmt|;
DECL|field|approvalsUtil
specifier|private
specifier|final
name|ApprovalsUtil
name|approvalsUtil
decl_stmt|;
DECL|field|patchSetUtil
specifier|private
specifier|final
name|PatchSetUtil
name|patchSetUtil
decl_stmt|;
annotation|@
name|Inject
DECL|method|Factory ( ChangeData.Factory changeDataFactory, ChangeNotes.Factory notesFactory, ApprovalsUtil approvalsUtil, PatchSetUtil patchSetUtil)
name|Factory
parameter_list|(
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|,
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
parameter_list|,
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
name|PatchSetUtil
name|patchSetUtil
parameter_list|)
block|{
name|this
operator|.
name|changeDataFactory
operator|=
name|changeDataFactory
expr_stmt|;
name|this
operator|.
name|notesFactory
operator|=
name|notesFactory
expr_stmt|;
name|this
operator|.
name|approvalsUtil
operator|=
name|approvalsUtil
expr_stmt|;
name|this
operator|.
name|patchSetUtil
operator|=
name|patchSetUtil
expr_stmt|;
block|}
DECL|method|create ( RefControl refControl, ReviewDb db, Project.NameKey project, Change.Id changeId)
name|ChangeControl
name|create
parameter_list|(
name|RefControl
name|refControl
parameter_list|,
name|ReviewDb
name|db
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Change
operator|.
name|Id
name|changeId
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|create
argument_list|(
name|refControl
argument_list|,
name|notesFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|project
argument_list|,
name|changeId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|create (RefControl refControl, ChangeNotes notes)
name|ChangeControl
name|create
parameter_list|(
name|RefControl
name|refControl
parameter_list|,
name|ChangeNotes
name|notes
parameter_list|)
block|{
return|return
operator|new
name|ChangeControl
argument_list|(
name|changeDataFactory
argument_list|,
name|approvalsUtil
argument_list|,
name|refControl
argument_list|,
name|notes
argument_list|,
name|patchSetUtil
argument_list|)
return|;
block|}
block|}
DECL|field|changeDataFactory
specifier|private
specifier|final
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
decl_stmt|;
DECL|field|approvalsUtil
specifier|private
specifier|final
name|ApprovalsUtil
name|approvalsUtil
decl_stmt|;
DECL|field|refControl
specifier|private
specifier|final
name|RefControl
name|refControl
decl_stmt|;
DECL|field|notes
specifier|private
specifier|final
name|ChangeNotes
name|notes
decl_stmt|;
DECL|field|patchSetUtil
specifier|private
specifier|final
name|PatchSetUtil
name|patchSetUtil
decl_stmt|;
DECL|method|ChangeControl ( ChangeData.Factory changeDataFactory, ApprovalsUtil approvalsUtil, RefControl refControl, ChangeNotes notes, PatchSetUtil patchSetUtil)
specifier|private
name|ChangeControl
parameter_list|(
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|,
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
name|RefControl
name|refControl
parameter_list|,
name|ChangeNotes
name|notes
parameter_list|,
name|PatchSetUtil
name|patchSetUtil
parameter_list|)
block|{
name|this
operator|.
name|changeDataFactory
operator|=
name|changeDataFactory
expr_stmt|;
name|this
operator|.
name|approvalsUtil
operator|=
name|approvalsUtil
expr_stmt|;
name|this
operator|.
name|refControl
operator|=
name|refControl
expr_stmt|;
name|this
operator|.
name|notes
operator|=
name|notes
expr_stmt|;
name|this
operator|.
name|patchSetUtil
operator|=
name|patchSetUtil
expr_stmt|;
block|}
DECL|method|asForChange (@ullable ChangeData cd, @Nullable Provider<ReviewDb> db)
name|ForChange
name|asForChange
parameter_list|(
annotation|@
name|Nullable
name|ChangeData
name|cd
parameter_list|,
annotation|@
name|Nullable
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
block|{
return|return
operator|new
name|ForChangeImpl
argument_list|(
name|cd
argument_list|,
name|db
argument_list|)
return|;
block|}
DECL|method|forUser (CurrentUser who)
specifier|private
name|ChangeControl
name|forUser
parameter_list|(
name|CurrentUser
name|who
parameter_list|)
block|{
if|if
condition|(
name|getUser
argument_list|()
operator|.
name|equals
argument_list|(
name|who
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|ChangeControl
argument_list|(
name|changeDataFactory
argument_list|,
name|approvalsUtil
argument_list|,
name|refControl
operator|.
name|forUser
argument_list|(
name|who
argument_list|)
argument_list|,
name|notes
argument_list|,
name|patchSetUtil
argument_list|)
return|;
block|}
DECL|method|getUser ()
specifier|private
name|CurrentUser
name|getUser
parameter_list|()
block|{
return|return
name|refControl
operator|.
name|getUser
argument_list|()
return|;
block|}
DECL|method|getProjectControl ()
specifier|private
name|ProjectControl
name|getProjectControl
parameter_list|()
block|{
return|return
name|refControl
operator|.
name|getProjectControl
argument_list|()
return|;
block|}
DECL|method|getChange ()
specifier|private
name|Change
name|getChange
parameter_list|()
block|{
return|return
name|notes
operator|.
name|getChange
argument_list|()
return|;
block|}
comment|/** Can this user see this change? */
DECL|method|isVisible (ReviewDb db, @Nullable ChangeData cd)
specifier|private
name|boolean
name|isVisible
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
annotation|@
name|Nullable
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|getChange
argument_list|()
operator|.
name|isPrivate
argument_list|()
operator|&&
operator|!
name|isPrivateVisible
argument_list|(
name|db
argument_list|,
name|cd
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|refControl
operator|.
name|isVisible
argument_list|()
return|;
block|}
comment|/** Can this user abandon this change? */
DECL|method|canAbandon (ReviewDb db)
specifier|private
name|boolean
name|canAbandon
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
operator|(
name|isOwner
argument_list|()
comment|// owner (aka creator) of the change can abandon
operator|||
name|refControl
operator|.
name|isOwner
argument_list|()
comment|// branch owner can abandon
operator|||
name|getProjectControl
argument_list|()
operator|.
name|isOwner
argument_list|()
comment|// project owner can abandon
operator|||
name|refControl
operator|.
name|canPerform
argument_list|(
name|Permission
operator|.
name|ABANDON
argument_list|)
comment|// user can abandon a specific ref
operator|||
name|getProjectControl
argument_list|()
operator|.
name|isAdmin
argument_list|()
operator|)
operator|&&
operator|!
name|isPatchSetLocked
argument_list|(
name|db
argument_list|)
return|;
block|}
comment|/** Can this user rebase this change? */
DECL|method|canRebase (ReviewDb db)
specifier|private
name|boolean
name|canRebase
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
operator|(
name|isOwner
argument_list|()
operator|||
name|refControl
operator|.
name|canSubmit
argument_list|(
name|isOwner
argument_list|()
argument_list|)
operator|||
name|refControl
operator|.
name|canRebase
argument_list|()
operator|)
operator|&&
name|refControl
operator|.
name|asForRef
argument_list|()
operator|.
name|testOrFalse
argument_list|(
name|RefPermission
operator|.
name|CREATE_CHANGE
argument_list|)
operator|&&
operator|!
name|isPatchSetLocked
argument_list|(
name|db
argument_list|)
return|;
block|}
comment|/** Can this user restore this change? */
DECL|method|canRestore (ReviewDb db)
specifier|private
name|boolean
name|canRestore
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
comment|// Anyone who can abandon the change can restore it, as long as they can create changes.
return|return
name|canAbandon
argument_list|(
name|db
argument_list|)
operator|&&
name|refControl
operator|.
name|asForRef
argument_list|()
operator|.
name|testOrFalse
argument_list|(
name|RefPermission
operator|.
name|CREATE_CHANGE
argument_list|)
return|;
block|}
comment|/** The range of permitted values associated with a label permission. */
DECL|method|getRange (String permission)
specifier|private
name|PermissionRange
name|getRange
parameter_list|(
name|String
name|permission
parameter_list|)
block|{
return|return
name|refControl
operator|.
name|getRange
argument_list|(
name|permission
argument_list|,
name|isOwner
argument_list|()
argument_list|)
return|;
block|}
comment|/** Can this user add a patch set to this change? */
DECL|method|canAddPatchSet (ReviewDb db)
specifier|private
name|boolean
name|canAddPatchSet
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
operator|!
operator|(
name|refControl
operator|.
name|asForRef
argument_list|()
operator|.
name|testOrFalse
argument_list|(
name|RefPermission
operator|.
name|CREATE_CHANGE
argument_list|)
operator|)
operator|||
name|isPatchSetLocked
argument_list|(
name|db
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|isOwner
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|refControl
operator|.
name|canAddPatchSet
argument_list|()
return|;
block|}
comment|/** Is the current patch set locked against state changes? */
DECL|method|isPatchSetLocked (ReviewDb db)
specifier|private
name|boolean
name|isPatchSetLocked
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|==
name|Change
operator|.
name|Status
operator|.
name|MERGED
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|PatchSetApproval
name|ap
range|:
name|approvalsUtil
operator|.
name|byPatchSet
argument_list|(
name|db
argument_list|,
name|notes
argument_list|,
name|getUser
argument_list|()
argument_list|,
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
control|)
block|{
name|LabelType
name|type
init|=
name|getProjectControl
argument_list|()
operator|.
name|getProjectState
argument_list|()
operator|.
name|getLabelTypes
argument_list|(
name|notes
argument_list|,
name|getUser
argument_list|()
argument_list|)
operator|.
name|byLabel
argument_list|(
name|ap
operator|.
name|getLabel
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
operator|&&
name|ap
operator|.
name|getValue
argument_list|()
operator|==
literal|1
operator|&&
name|type
operator|.
name|getFunction
argument_list|()
operator|==
name|LabelFunction
operator|.
name|PATCH_SET_LOCK
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
comment|/** Is this user the owner of the change? */
DECL|method|isOwner ()
specifier|private
name|boolean
name|isOwner
parameter_list|()
block|{
if|if
condition|(
name|getUser
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
name|Account
operator|.
name|Id
name|id
init|=
name|getUser
argument_list|()
operator|.
name|asIdentifiedUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
return|return
name|id
operator|.
name|equals
argument_list|(
name|getChange
argument_list|()
operator|.
name|getOwner
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** Is this user assigned to this change? */
DECL|method|isAssignee ()
specifier|private
name|boolean
name|isAssignee
parameter_list|()
block|{
name|Account
operator|.
name|Id
name|currentAssignee
init|=
name|notes
operator|.
name|getChange
argument_list|()
operator|.
name|getAssignee
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentAssignee
operator|!=
literal|null
operator|&&
name|getUser
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
name|Account
operator|.
name|Id
name|id
init|=
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
return|return
name|id
operator|.
name|equals
argument_list|(
name|currentAssignee
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** Is this user a reviewer for the change? */
DECL|method|isReviewer (ReviewDb db, @Nullable ChangeData cd)
specifier|private
name|boolean
name|isReviewer
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
annotation|@
name|Nullable
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|getUser
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
name|cd
operator|=
name|cd
operator|!=
literal|null
condition|?
name|cd
else|:
name|changeDataFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|notes
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|results
init|=
name|cd
operator|.
name|reviewers
argument_list|()
operator|.
name|all
argument_list|()
decl_stmt|;
return|return
name|results
operator|.
name|contains
argument_list|(
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** Can this user edit the topic name? */
DECL|method|canEditTopicName ()
specifier|private
name|boolean
name|canEditTopicName
parameter_list|()
block|{
if|if
condition|(
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
return|return
name|isOwner
argument_list|()
comment|// owner (aka creator) of the change can edit topic
operator|||
name|refControl
operator|.
name|isOwner
argument_list|()
comment|// branch owner can edit topic
operator|||
name|getProjectControl
argument_list|()
operator|.
name|isOwner
argument_list|()
comment|// project owner can edit topic
operator|||
name|refControl
operator|.
name|canPerform
argument_list|(
name|Permission
operator|.
name|EDIT_TOPIC_NAME
argument_list|)
comment|// user can edit topic on a specific ref
operator|||
name|getProjectControl
argument_list|()
operator|.
name|isAdmin
argument_list|()
return|;
block|}
return|return
name|refControl
operator|.
name|canForceEditTopicName
argument_list|()
return|;
block|}
comment|/** Can this user edit the description? */
DECL|method|canEditDescription ()
specifier|private
name|boolean
name|canEditDescription
parameter_list|()
block|{
if|if
condition|(
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
return|return
name|isOwner
argument_list|()
comment|// owner (aka creator) of the change can edit desc
operator|||
name|refControl
operator|.
name|isOwner
argument_list|()
comment|// branch owner can edit desc
operator|||
name|getProjectControl
argument_list|()
operator|.
name|isOwner
argument_list|()
comment|// project owner can edit desc
operator|||
name|getProjectControl
argument_list|()
operator|.
name|isAdmin
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|canEditAssignee ()
specifier|private
name|boolean
name|canEditAssignee
parameter_list|()
block|{
return|return
name|isOwner
argument_list|()
operator|||
name|getProjectControl
argument_list|()
operator|.
name|isOwner
argument_list|()
operator|||
name|refControl
operator|.
name|canPerform
argument_list|(
name|Permission
operator|.
name|EDIT_ASSIGNEE
argument_list|)
operator|||
name|isAssignee
argument_list|()
return|;
block|}
comment|/** Can this user edit the hashtag name? */
DECL|method|canEditHashtags ()
specifier|private
name|boolean
name|canEditHashtags
parameter_list|()
block|{
return|return
name|isOwner
argument_list|()
comment|// owner (aka creator) of the change can edit hashtags
operator|||
name|refControl
operator|.
name|isOwner
argument_list|()
comment|// branch owner can edit hashtags
operator|||
name|getProjectControl
argument_list|()
operator|.
name|isOwner
argument_list|()
comment|// project owner can edit hashtags
operator|||
name|refControl
operator|.
name|canPerform
argument_list|(
name|Permission
operator|.
name|EDIT_HASHTAGS
argument_list|)
comment|// user can edit hashtag on a specific ref
operator|||
name|getProjectControl
argument_list|()
operator|.
name|isAdmin
argument_list|()
return|;
block|}
DECL|method|isPrivateVisible (ReviewDb db, ChangeData cd)
specifier|private
name|boolean
name|isPrivateVisible
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|isOwner
argument_list|()
operator|||
name|isReviewer
argument_list|(
name|db
argument_list|,
name|cd
argument_list|)
operator|||
name|refControl
operator|.
name|canPerform
argument_list|(
name|Permission
operator|.
name|VIEW_PRIVATE_CHANGES
argument_list|)
operator|||
name|getUser
argument_list|()
operator|.
name|isInternalUser
argument_list|()
return|;
block|}
DECL|class|ForChangeImpl
specifier|private
class|class
name|ForChangeImpl
extends|extends
name|ForChange
block|{
DECL|field|cd
specifier|private
name|ChangeData
name|cd
decl_stmt|;
DECL|field|labels
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|PermissionRange
argument_list|>
name|labels
decl_stmt|;
DECL|field|resourcePath
specifier|private
name|String
name|resourcePath
decl_stmt|;
DECL|method|ForChangeImpl (@ullable ChangeData cd, @Nullable Provider<ReviewDb> db)
name|ForChangeImpl
parameter_list|(
annotation|@
name|Nullable
name|ChangeData
name|cd
parameter_list|,
annotation|@
name|Nullable
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
block|{
name|this
operator|.
name|cd
operator|=
name|cd
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
block|}
DECL|method|db ()
specifier|private
name|ReviewDb
name|db
parameter_list|()
block|{
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
block|{
return|return
name|db
operator|.
name|get
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|cd
operator|!=
literal|null
condition|)
block|{
return|return
name|cd
operator|.
name|db
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|changeData ()
specifier|private
name|ChangeData
name|changeData
parameter_list|()
block|{
if|if
condition|(
name|cd
operator|==
literal|null
condition|)
block|{
name|ReviewDb
name|reviewDb
init|=
name|db
argument_list|()
decl_stmt|;
name|checkState
argument_list|(
name|reviewDb
operator|!=
literal|null
argument_list|,
literal|"need ReviewDb"
argument_list|)
expr_stmt|;
name|cd
operator|=
name|changeDataFactory
operator|.
name|create
argument_list|(
name|reviewDb
argument_list|,
name|notes
argument_list|)
expr_stmt|;
block|}
return|return
name|cd
return|;
block|}
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
name|ForChange
name|user
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
block|{
return|return
name|user
argument_list|()
operator|.
name|equals
argument_list|(
name|user
argument_list|)
condition|?
name|this
else|:
name|forUser
argument_list|(
name|user
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
literal|"/projects/%s/+changes/%s"
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
name|changeData
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|resourcePath
return|;
block|}
annotation|@
name|Override
DECL|method|check (ChangePermissionOrLabel perm)
specifier|public
name|void
name|check
parameter_list|(
name|ChangePermissionOrLabel
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
literal|" not permitted"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|test (Collection<T> permSet)
specifier|public
parameter_list|<
name|T
extends|extends
name|ChangePermissionOrLabel
parameter_list|>
name|Set
argument_list|<
name|T
argument_list|>
name|test
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|permSet
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
name|Set
argument_list|<
name|T
argument_list|>
name|ok
init|=
name|newSet
argument_list|(
name|permSet
argument_list|)
decl_stmt|;
for|for
control|(
name|T
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
DECL|method|can (ChangePermissionOrLabel perm)
specifier|private
name|boolean
name|can
parameter_list|(
name|ChangePermissionOrLabel
name|perm
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
if|if
condition|(
name|perm
operator|instanceof
name|ChangePermission
condition|)
block|{
return|return
name|can
argument_list|(
operator|(
name|ChangePermission
operator|)
name|perm
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|perm
operator|instanceof
name|LabelPermission
condition|)
block|{
return|return
name|can
argument_list|(
operator|(
name|LabelPermission
operator|)
name|perm
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|perm
operator|instanceof
name|LabelPermission
operator|.
name|WithValue
condition|)
block|{
return|return
name|can
argument_list|(
operator|(
name|LabelPermission
operator|.
name|WithValue
operator|)
name|perm
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
DECL|method|can (ChangePermission perm)
specifier|private
name|boolean
name|can
parameter_list|(
name|ChangePermission
name|perm
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
try|try
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
argument_list|(
name|db
argument_list|()
argument_list|,
name|changeData
argument_list|()
argument_list|)
return|;
case|case
name|ABANDON
case|:
return|return
name|canAbandon
argument_list|(
name|db
argument_list|()
argument_list|)
return|;
case|case
name|DELETE
case|:
return|return
operator|(
name|isOwner
argument_list|()
operator|&&
name|refControl
operator|.
name|canPerform
argument_list|(
name|Permission
operator|.
name|DELETE_OWN_CHANGES
argument_list|)
operator|)
operator|||
name|getProjectControl
argument_list|()
operator|.
name|isAdmin
argument_list|()
return|;
case|case
name|ADD_PATCH_SET
case|:
return|return
name|canAddPatchSet
argument_list|(
name|db
argument_list|()
argument_list|)
return|;
case|case
name|EDIT_ASSIGNEE
case|:
return|return
name|canEditAssignee
argument_list|()
return|;
case|case
name|EDIT_DESCRIPTION
case|:
return|return
name|canEditDescription
argument_list|()
return|;
case|case
name|EDIT_HASHTAGS
case|:
return|return
name|canEditHashtags
argument_list|()
return|;
case|case
name|EDIT_TOPIC_NAME
case|:
return|return
name|canEditTopicName
argument_list|()
return|;
case|case
name|REBASE
case|:
return|return
name|canRebase
argument_list|(
name|db
argument_list|()
argument_list|)
return|;
case|case
name|RESTORE
case|:
return|return
name|canRestore
argument_list|(
name|db
argument_list|()
argument_list|)
return|;
case|case
name|SUBMIT
case|:
return|return
name|refControl
operator|.
name|canSubmit
argument_list|(
name|isOwner
argument_list|()
argument_list|)
return|;
case|case
name|REMOVE_REVIEWER
case|:
case|case
name|SUBMIT_AS
case|:
return|return
name|refControl
operator|.
name|canPerform
argument_list|(
name|perm
operator|.
name|permissionName
argument_list|()
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|PermissionBackendException
argument_list|(
literal|"unavailable"
argument_list|,
name|e
argument_list|)
throw|;
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
DECL|method|can (LabelPermission perm)
specifier|private
name|boolean
name|can
parameter_list|(
name|LabelPermission
name|perm
parameter_list|)
block|{
return|return
operator|!
name|label
argument_list|(
name|perm
operator|.
name|permissionName
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|can (LabelPermission.WithValue perm)
specifier|private
name|boolean
name|can
parameter_list|(
name|LabelPermission
operator|.
name|WithValue
name|perm
parameter_list|)
block|{
name|PermissionRange
name|r
init|=
name|label
argument_list|(
name|perm
operator|.
name|permissionName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|perm
operator|.
name|forUser
argument_list|()
operator|==
name|ON_BEHALF_OF
operator|&&
name|r
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|r
operator|.
name|contains
argument_list|(
name|perm
operator|.
name|value
argument_list|()
argument_list|)
return|;
block|}
DECL|method|label (String permission)
specifier|private
name|PermissionRange
name|label
parameter_list|(
name|String
name|permission
parameter_list|)
block|{
if|if
condition|(
name|labels
operator|==
literal|null
condition|)
block|{
name|labels
operator|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
name|PermissionRange
name|r
init|=
name|labels
operator|.
name|get
argument_list|(
name|permission
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
name|getRange
argument_list|(
name|permission
argument_list|)
expr_stmt|;
name|labels
operator|.
name|put
argument_list|(
name|permission
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
block|}
DECL|method|newSet (Collection<T> permSet)
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|ChangePermissionOrLabel
parameter_list|>
name|Set
argument_list|<
name|T
argument_list|>
name|newSet
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|permSet
parameter_list|)
block|{
if|if
condition|(
name|permSet
operator|instanceof
name|EnumSet
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
name|Set
argument_list|<
name|T
argument_list|>
name|s
init|=
operator|(
operator|(
name|EnumSet
operator|)
name|permSet
operator|)
operator|.
name|clone
argument_list|()
decl_stmt|;
name|s
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|s
return|;
block|}
return|return
name|Sets
operator|.
name|newHashSetWithExpectedSize
argument_list|(
name|permSet
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

