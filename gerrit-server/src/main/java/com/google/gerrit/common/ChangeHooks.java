begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.common
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
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
name|ChangeHookRunner
operator|.
name|HookResult
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
name|ContributorAgreement
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
name|events
operator|.
name|ChangeEvent
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectId
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
name|RefUpdate
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

begin_comment
comment|/** Invokes hooks on server actions. */
end_comment

begin_interface
DECL|interface|ChangeHooks
specifier|public
interface|interface
name|ChangeHooks
block|{
DECL|method|addChangeListener (ChangeListener listener, IdentifiedUser user)
specifier|public
name|void
name|addChangeListener
parameter_list|(
name|ChangeListener
name|listener
parameter_list|,
name|IdentifiedUser
name|user
parameter_list|)
function_decl|;
DECL|method|removeChangeListener (ChangeListener listener)
specifier|public
name|void
name|removeChangeListener
parameter_list|(
name|ChangeListener
name|listener
parameter_list|)
function_decl|;
comment|/**    * Fire the Patchset Created Hook.    *    * @param change The change itself.    * @param patchSet The Patchset that was created.    * @throws OrmException    */
DECL|method|doPatchsetCreatedHook (Change change, PatchSet patchSet, ReviewDb db)
specifier|public
name|void
name|doPatchsetCreatedHook
parameter_list|(
name|Change
name|change
parameter_list|,
name|PatchSet
name|patchSet
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
function_decl|;
comment|/**    * Fire the Draft Published Hook.    *    * @param change The change itself.    * @param patchSet The Patchset that was published.    * @throws OrmException    */
DECL|method|doDraftPublishedHook (Change change, PatchSet patchSet, ReviewDb db)
specifier|public
name|void
name|doDraftPublishedHook
parameter_list|(
name|Change
name|change
parameter_list|,
name|PatchSet
name|patchSet
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
function_decl|;
comment|/**    * Fire the Comment Added Hook.    *    * @param change The change itself.    * @param patchSet The patchset this comment is related to.    * @param account The gerrit user who added the comment.    * @param comment The comment given.    * @param approvals Map of label IDs to scores    * @throws OrmException    */
DECL|method|doCommentAddedHook (Change change, Account account, PatchSet patchSet, String comment, Map<String, Short> approvals, ReviewDb db)
specifier|public
name|void
name|doCommentAddedHook
parameter_list|(
name|Change
name|change
parameter_list|,
name|Account
name|account
parameter_list|,
name|PatchSet
name|patchSet
parameter_list|,
name|String
name|comment
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Short
argument_list|>
name|approvals
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
function_decl|;
comment|/**    * Fire the Change Merged Hook.    *    * @param change The change itself.    * @param account The gerrit user who submitted the change.    * @param patchSet The patchset that was merged.    * @throws OrmException    */
DECL|method|doChangeMergedHook (Change change, Account account, PatchSet patchSet, ReviewDb db)
specifier|public
name|void
name|doChangeMergedHook
parameter_list|(
name|Change
name|change
parameter_list|,
name|Account
name|account
parameter_list|,
name|PatchSet
name|patchSet
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
function_decl|;
comment|/**    * Fire the Merge Failed Hook.    *    * @param change The change itself.    * @param account The gerrit user who attempted to submit the change.    * @param patchSet The patchset that failed to merge.    * @param reason The reason that the change failed to merge.    * @throws OrmException    */
DECL|method|doMergeFailedHook (Change change, Account account, PatchSet patchSet, String reason, ReviewDb db)
specifier|public
name|void
name|doMergeFailedHook
parameter_list|(
name|Change
name|change
parameter_list|,
name|Account
name|account
parameter_list|,
name|PatchSet
name|patchSet
parameter_list|,
name|String
name|reason
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
function_decl|;
comment|/**    * Fire the Change Abandoned Hook.    *    * @param change The change itself.    * @param account The gerrit user who abandoned the change.    * @param reason Reason for abandoning the change.    * @throws OrmException    */
DECL|method|doChangeAbandonedHook (Change change, Account account, PatchSet patchSet, String reason, ReviewDb db)
specifier|public
name|void
name|doChangeAbandonedHook
parameter_list|(
name|Change
name|change
parameter_list|,
name|Account
name|account
parameter_list|,
name|PatchSet
name|patchSet
parameter_list|,
name|String
name|reason
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
function_decl|;
comment|/**    * Fire the Change Restored Hook.    *    * @param change The change itself.    * @param account The gerrit user who restored the change.    * @param reason Reason for restoring the change.    * @throws OrmException    */
DECL|method|doChangeRestoredHook (Change change, Account account, PatchSet patchSet, String reason, ReviewDb db)
specifier|public
name|void
name|doChangeRestoredHook
parameter_list|(
name|Change
name|change
parameter_list|,
name|Account
name|account
parameter_list|,
name|PatchSet
name|patchSet
parameter_list|,
name|String
name|reason
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
function_decl|;
comment|/**    * Fire the Ref Updated Hook    *    * @param refName The updated project and branch.    * @param refUpdate An actual RefUpdate object    * @param account The gerrit user who moved the ref    */
DECL|method|doRefUpdatedHook (Branch.NameKey refName, RefUpdate refUpdate, Account account)
specifier|public
name|void
name|doRefUpdatedHook
parameter_list|(
name|Branch
operator|.
name|NameKey
name|refName
parameter_list|,
name|RefUpdate
name|refUpdate
parameter_list|,
name|Account
name|account
parameter_list|)
function_decl|;
comment|/**    * Fire the Ref Updated Hook    *    * @param refName The Branch.NameKey of the ref that was updated    * @param oldId The ref's old id    * @param newId The ref's new id    * @param account The gerrit user who moved the ref    */
DECL|method|doRefUpdatedHook (Branch.NameKey refName, ObjectId oldId, ObjectId newId, Account account)
specifier|public
name|void
name|doRefUpdatedHook
parameter_list|(
name|Branch
operator|.
name|NameKey
name|refName
parameter_list|,
name|ObjectId
name|oldId
parameter_list|,
name|ObjectId
name|newId
parameter_list|,
name|Account
name|account
parameter_list|)
function_decl|;
comment|/**    * Fire the Reviewer Added Hook    *    * @param change The change itself.    * @param patchSet The patchset that the reviewer was added on.    * @param account The gerrit user who was added as reviewer.    */
DECL|method|doReviewerAddedHook (Change change, Account account, PatchSet patchSet, ReviewDb db)
specifier|public
name|void
name|doReviewerAddedHook
parameter_list|(
name|Change
name|change
parameter_list|,
name|Account
name|account
parameter_list|,
name|PatchSet
name|patchSet
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
function_decl|;
comment|/**    * Fire the Topic Changed Hook    *    * @param change The change itself.    * @param account The gerrit user who changed the topic.    * @param oldTopic The old topic name.    */
DECL|method|doTopicChangedHook (Change change, Account account, String oldTopic, ReviewDb db)
specifier|public
name|void
name|doTopicChangedHook
parameter_list|(
name|Change
name|change
parameter_list|,
name|Account
name|account
parameter_list|,
name|String
name|oldTopic
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
function_decl|;
DECL|method|doClaSignupHook (Account account, ContributorAgreement cla)
specifier|public
name|void
name|doClaSignupHook
parameter_list|(
name|Account
name|account
parameter_list|,
name|ContributorAgreement
name|cla
parameter_list|)
function_decl|;
comment|/**    * Fire the Ref update Hook    *    * @param project The target project    * @param refName The Branch.NameKey of the ref provided by client    * @param uploader The gerrit user running the command    * @param oldId The ref's old id    * @param newId The ref's new id    */
DECL|method|doRefUpdateHook (Project project, String refName, Account uploader, ObjectId oldId, ObjectId newId)
specifier|public
name|HookResult
name|doRefUpdateHook
parameter_list|(
name|Project
name|project
parameter_list|,
name|String
name|refName
parameter_list|,
name|Account
name|uploader
parameter_list|,
name|ObjectId
name|oldId
parameter_list|,
name|ObjectId
name|newId
parameter_list|)
function_decl|;
comment|/**    * Post a stream event that is related to a change    *    * @param change The change that the event is related to    * @param event The event to post    * @param db The database    * @throws OrmException    */
DECL|method|postEvent (Change change, ChangeEvent event, ReviewDb db)
specifier|public
name|void
name|postEvent
parameter_list|(
name|Change
name|change
parameter_list|,
name|ChangeEvent
name|event
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
function_decl|;
comment|/**    * Post a stream event that is related to a branch    *    * @param branchName The branch that the event is related to    * @param event The event to post    */
DECL|method|postEvent (Branch.NameKey branchName, ChangeEvent event)
specifier|public
name|void
name|postEvent
parameter_list|(
name|Branch
operator|.
name|NameKey
name|branchName
parameter_list|,
name|ChangeEvent
name|event
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

