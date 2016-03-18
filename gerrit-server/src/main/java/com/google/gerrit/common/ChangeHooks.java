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
comment|/** Invokes hooks on server actions. */
end_comment

begin_interface
DECL|interface|ChangeHooks
specifier|public
interface|interface
name|ChangeHooks
block|{
comment|/**    * Fire the Patchset Created Hook.    *    * @param change The change itself.    * @param patchSet The Patchset that was created.    * @param db The review database.    * @throws OrmException    */
DECL|method|doPatchsetCreatedHook (Change change, PatchSet patchSet, ReviewDb db)
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
comment|/**    * Fire the Draft Published Hook.    *    * @param change The change itself.    * @param patchSet The Patchset that was published.    * @param db The review database.    * @throws OrmException    */
DECL|method|doDraftPublishedHook (Change change, PatchSet patchSet, ReviewDb db)
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
comment|/**    * Fire the Comment Added Hook.    *    * @param change The change itself.    * @param account The gerrit user who added the comment.    * @param patchSet The patchset this comment is related to.    * @param comment The comment given.    * @param approvals Map of label IDs to scores    * @param oldApprovals Map of label IDs to old approval scores    * @param db The review database.    * @throws OrmException    */
DECL|method|doCommentAddedHook (Change change, Account account, PatchSet patchSet, String comment, Map<String, Short> approvals, Map<String, Short> oldApprovals, ReviewDb db)
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
name|Map
argument_list|<
name|String
argument_list|,
name|Short
argument_list|>
name|oldApprovals
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
function_decl|;
comment|/**    * Fire the Change Merged Hook.    *    * @param change The change itself.    * @param account The gerrit user who submitted the change.    * @param patchSet The patchset that was merged.    * @param db The review database.    * @param mergeResultRev The SHA-1 of the merge result revision.    * @throws OrmException    */
DECL|method|doChangeMergedHook (Change change, Account account, PatchSet patchSet, ReviewDb db, String mergeResultRev)
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
parameter_list|,
name|String
name|mergeResultRev
parameter_list|)
throws|throws
name|OrmException
function_decl|;
comment|/**    * Fire the Merge Failed Hook.    *    * @param change The change itself.    * @param account The gerrit user who attempted to submit the change.    * @param patchSet The patchset that failed to merge.    * @param reason The reason that the change failed to merge.    * @param db The review database.    * @throws OrmException    */
DECL|method|doMergeFailedHook (Change change, Account account, PatchSet patchSet, String reason, ReviewDb db)
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
comment|/**    * Fire the Change Abandoned Hook.    *    * @param change The change itself.    * @param account The gerrit user who abandoned the change.    * @param reason Reason for abandoning the change.    * @param db The review database.    * @throws OrmException    */
DECL|method|doChangeAbandonedHook (Change change, Account account, PatchSet patchSet, String reason, ReviewDb db)
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
comment|/**    * Fire the Change Restored Hook.    *    * @param change The change itself.    * @param account The gerrit user who restored the change.    * @param patchSet The patchset that was restored.    * @param reason Reason for restoring the change.    * @param db The review database.    * @throws OrmException    */
DECL|method|doChangeRestoredHook (Change change, Account account, PatchSet patchSet, String reason, ReviewDb db)
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
comment|/**    * Fire the Ref Updated Hook.    *    * @param refName The updated project and branch.    * @param refUpdate An actual RefUpdate object    * @param account The gerrit user who moved the ref    */
DECL|method|doRefUpdatedHook (Branch.NameKey refName, RefUpdate refUpdate, Account account)
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
comment|/**    * Fire the Ref Updated Hook.    *    * @param refName The Branch.NameKey of the ref that was updated.    * @param oldId The ref's old id.    * @param newId The ref's new id.    * @param account The gerrit user who moved the ref.    */
DECL|method|doRefUpdatedHook (Branch.NameKey refName, ObjectId oldId, ObjectId newId, Account account)
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
comment|/**    * Fire the Reviewer Added Hook.    *    * @param change The change itself.    * @param patchSet The patchset that the reviewer was added on.    * @param account The gerrit user who was added as reviewer.    * @param db The review database.    */
DECL|method|doReviewerAddedHook (Change change, Account account, PatchSet patchSet, ReviewDb db)
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
comment|/**    * Fire the Topic Changed Hook.    *    * @param change The change itself.    * @param account The gerrit user who changed the topic.    * @param oldTopic The old topic name.    * @param db The review database.    */
DECL|method|doTopicChangedHook (Change change, Account account, String oldTopic, ReviewDb db)
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
comment|/**    * Fire the contributor license agreement signup hook.    *    * @param account The gerrit user who signed the contributor license    *        agreement.    * @param claName The name of the contributor license agreement.    */
DECL|method|doClaSignupHook (Account account, String claName)
name|void
name|doClaSignupHook
parameter_list|(
name|Account
name|account
parameter_list|,
name|String
name|claName
parameter_list|)
function_decl|;
comment|/**    * Fire the Ref update Hook.    *    * @param project The target project.    * @param refName The Branch.NameKey of the ref provided by client.    * @param uploader The gerrit user running the command.    * @param oldId The ref's old id.    * @param newId The ref's new id.    */
DECL|method|doRefUpdateHook (Project project, String refName, Account uploader, ObjectId oldId, ObjectId newId)
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
comment|/**    * Fire the hashtags changed Hook.    *    * @param change The change itself.    * @param account The gerrit user changing the hashtags.    * @param added List of hashtags that were added to the change.    * @param removed List of hashtags that were removed from the change.    * @param hashtags List of hashtags on the change after adding or removing.    * @param db The review database.    * @throws OrmException    */
DECL|method|doHashtagsChangedHook (Change change, Account account, Set<String>added, Set<String> removed, Set<String> hashtags, ReviewDb db)
name|void
name|doHashtagsChangedHook
parameter_list|(
name|Change
name|change
parameter_list|,
name|Account
name|account
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|added
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|removed
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|hashtags
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
function_decl|;
comment|/**    * Fire the project created hook.    *    * @param project The project that was created.    * @param headName The head name of the created project.    */
DECL|method|doProjectCreatedHook (Project.NameKey project, String headName)
name|void
name|doProjectCreatedHook
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|String
name|headName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

