begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.extensions.api.changes
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|api
operator|.
name|changes
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
name|extensions
operator|.
name|client
operator|.
name|ListChangesOption
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
name|common
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
name|extensions
operator|.
name|common
operator|.
name|ChangeInfo
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
name|common
operator|.
name|CommentInfo
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
name|common
operator|.
name|EditInfo
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
name|common
operator|.
name|MergePatchSetInput
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
name|common
operator|.
name|RobotCommentInfo
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
name|common
operator|.
name|SuggestedReviewerInfo
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
name|NotImplementedException
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
name|RestApiException
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

begin_interface
DECL|interface|ChangeApi
specifier|public
interface|interface
name|ChangeApi
block|{
DECL|method|id ()
name|String
name|id
parameter_list|()
function_decl|;
comment|/**    * Look up the current revision for the change.    *<p>    *<strong>Note:</strong> This method eagerly reads the revision. Methods that    * mutate the revision do not necessarily re-read the revision. Therefore,    * calling a getter method on an instance after calling a mutation method on    * that same instance is not guaranteed to reflect the mutation. It is not    * recommended to store references to {@code RevisionApi} instances.    *    * @return API for accessing the revision.    * @throws RestApiException if an error occurred.    */
DECL|method|current ()
name|RevisionApi
name|current
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Look up a revision of a change by number.    *    * @see #current()    */
DECL|method|revision (int id)
name|RevisionApi
name|revision
parameter_list|(
name|int
name|id
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Look up a revision of a change by commit SHA-1.    *    * @see #current()    */
DECL|method|revision (String id)
name|RevisionApi
name|revision
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Look up the reviewer of the change.    *<p>    * @param id ID of the account, can be a string of the format    *     "Full Name&lt;mail@example.com&gt;", just the email address, a full name    *     if it is unique, an account ID, a user name or 'self' for the    *     calling user.    * @return API for accessing the reviewer.    * @throws RestApiException if id is not account ID or is a user that isn't    *     known to be a reviewer for this change.    */
DECL|method|reviewer (String id)
name|ReviewerApi
name|reviewer
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|abandon ()
name|void
name|abandon
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|abandon (AbandonInput in)
name|void
name|abandon
parameter_list|(
name|AbandonInput
name|in
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|restore ()
name|void
name|restore
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|restore (RestoreInput in)
name|void
name|restore
parameter_list|(
name|RestoreInput
name|in
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|move (String destination)
name|void
name|move
parameter_list|(
name|String
name|destination
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|move (MoveInput in)
name|void
name|move
parameter_list|(
name|MoveInput
name|in
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Create a new change that reverts this change.    *    * @see Changes#id(int)    */
DECL|method|revert ()
name|ChangeApi
name|revert
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Create a new change that reverts this change.    *    * @see Changes#id(int)    */
DECL|method|revert (RevertInput in)
name|ChangeApi
name|revert
parameter_list|(
name|RevertInput
name|in
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
comment|/** Create a merge patch set for the change. */
DECL|method|createMergePatchSet (MergePatchSetInput in)
name|ChangeInfo
name|createMergePatchSet
parameter_list|(
name|MergePatchSetInput
name|in
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|submittedTogether ()
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|submittedTogether
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|submittedTogether ( EnumSet<SubmittedTogetherOption> options)
name|SubmittedTogetherInfo
name|submittedTogether
parameter_list|(
name|EnumSet
argument_list|<
name|SubmittedTogetherOption
argument_list|>
name|options
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|submittedTogether ( EnumSet<ListChangesOption> listOptions, EnumSet<SubmittedTogetherOption> submitOptions)
name|SubmittedTogetherInfo
name|submittedTogether
parameter_list|(
name|EnumSet
argument_list|<
name|ListChangesOption
argument_list|>
name|listOptions
parameter_list|,
name|EnumSet
argument_list|<
name|SubmittedTogetherOption
argument_list|>
name|submitOptions
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Publishes a draft change.    */
DECL|method|publish ()
name|void
name|publish
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Deletes a change.    */
DECL|method|delete ()
name|void
name|delete
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|topic ()
name|String
name|topic
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|topic (String topic)
name|void
name|topic
parameter_list|(
name|String
name|topic
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|addReviewer (AddReviewerInput in)
name|void
name|addReviewer
parameter_list|(
name|AddReviewerInput
name|in
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|addReviewer (String in)
name|void
name|addReviewer
parameter_list|(
name|String
name|in
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|suggestReviewers ()
name|SuggestedReviewersRequest
name|suggestReviewers
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|suggestReviewers (String query)
name|SuggestedReviewersRequest
name|suggestReviewers
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|get (EnumSet<ListChangesOption> options)
name|ChangeInfo
name|get
parameter_list|(
name|EnumSet
argument_list|<
name|ListChangesOption
argument_list|>
name|options
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
comment|/** {@code get} with {@link ListChangesOption} set to all except CHECK. */
DECL|method|get ()
name|ChangeInfo
name|get
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
comment|/** {@code get} with {@link ListChangesOption} set to none. */
DECL|method|info ()
name|ChangeInfo
name|info
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Retrieve change edit when exists.    *    * @deprecated Replaced by {@link ChangeApi#edit()} in combination with    * {@link ChangeEditApi#get()}.    */
annotation|@
name|Deprecated
DECL|method|getEdit ()
name|EditInfo
name|getEdit
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Provides access to an API regarding the change edit of this change.    *    * @return a {@code ChangeEditApi} for the change edit of this change    * @throws RestApiException if the API isn't accessible    */
DECL|method|edit ()
name|ChangeEditApi
name|edit
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Set hashtags on a change    **/
DECL|method|setHashtags (HashtagsInput input)
name|void
name|setHashtags
parameter_list|(
name|HashtagsInput
name|input
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Get hashtags on a change.    * @return hashtags    * @throws RestApiException    */
DECL|method|getHashtags ()
name|Set
argument_list|<
name|String
argument_list|>
name|getHashtags
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Set the assignee of a change.    */
DECL|method|setAssignee (AssigneeInput input)
name|AccountInfo
name|setAssignee
parameter_list|(
name|AssigneeInput
name|input
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Get the assignee of a change.    */
DECL|method|getAssignee ()
name|AccountInfo
name|getAssignee
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Get all past assignees.   */
DECL|method|getPastAssignees ()
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|getPastAssignees
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Delete the assignee of a change.    *    * @return the assignee that was deleted, or null if there was no assignee.    */
DECL|method|deleteAssignee ()
name|AccountInfo
name|deleteAssignee
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Get all published comments on a change.    *    * @return comments in a map keyed by path; comments have the {@code revision}    *     field set to indicate their patch set.    * @throws RestApiException    */
DECL|method|comments ()
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|comments
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Get all robot comments on a change.    *    * @return robot comments in a map keyed by path; robot comments have the    *     {@code revision} field set to indicate their patch set.    *    * @throws RestApiException    */
DECL|method|robotComments ()
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RobotCommentInfo
argument_list|>
argument_list|>
name|robotComments
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
comment|/**    * Get all draft comments for the current user on a change.    *    * @return drafts in a map keyed by path; comments have the {@code revision}    *     field set to indicate their patch set.    * @throws RestApiException    */
DECL|method|drafts ()
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|drafts
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|check ()
name|ChangeInfo
name|check
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|check (FixInput fix)
name|ChangeInfo
name|check
parameter_list|(
name|FixInput
name|fix
parameter_list|)
throws|throws
name|RestApiException
function_decl|;
DECL|method|index ()
name|void
name|index
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|class|SuggestedReviewersRequest
specifier|abstract
class|class
name|SuggestedReviewersRequest
block|{
DECL|field|query
specifier|private
name|String
name|query
decl_stmt|;
DECL|field|limit
specifier|private
name|int
name|limit
decl_stmt|;
DECL|method|get ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|SuggestedReviewerInfo
argument_list|>
name|get
parameter_list|()
throws|throws
name|RestApiException
function_decl|;
DECL|method|withQuery (String query)
specifier|public
name|SuggestedReviewersRequest
name|withQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withLimit (int limit)
specifier|public
name|SuggestedReviewersRequest
name|withLimit
parameter_list|(
name|int
name|limit
parameter_list|)
block|{
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getQuery ()
specifier|public
name|String
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
DECL|method|getLimit ()
specifier|public
name|int
name|getLimit
parameter_list|()
block|{
return|return
name|limit
return|;
block|}
block|}
comment|/**    * A default implementation which allows source compatibility    * when adding new methods to the interface.    **/
DECL|class|NotImplemented
class|class
name|NotImplemented
implements|implements
name|ChangeApi
block|{
annotation|@
name|Override
DECL|method|id ()
specifier|public
name|String
name|id
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|current ()
specifier|public
name|RevisionApi
name|current
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|revision (int id)
specifier|public
name|RevisionApi
name|revision
parameter_list|(
name|int
name|id
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|reviewer (String id)
specifier|public
name|ReviewerApi
name|reviewer
parameter_list|(
name|String
name|id
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|revision (String id)
specifier|public
name|RevisionApi
name|revision
parameter_list|(
name|String
name|id
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|abandon ()
specifier|public
name|void
name|abandon
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|abandon (AbandonInput in)
specifier|public
name|void
name|abandon
parameter_list|(
name|AbandonInput
name|in
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|restore ()
specifier|public
name|void
name|restore
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|restore (RestoreInput in)
specifier|public
name|void
name|restore
parameter_list|(
name|RestoreInput
name|in
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|move (String destination)
specifier|public
name|void
name|move
parameter_list|(
name|String
name|destination
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|move (MoveInput in)
specifier|public
name|void
name|move
parameter_list|(
name|MoveInput
name|in
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|revert ()
specifier|public
name|ChangeApi
name|revert
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|revert (RevertInput in)
specifier|public
name|ChangeApi
name|revert
parameter_list|(
name|RevertInput
name|in
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|publish ()
specifier|public
name|void
name|publish
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|delete ()
specifier|public
name|void
name|delete
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|topic ()
specifier|public
name|String
name|topic
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|topic (String topic)
specifier|public
name|void
name|topic
parameter_list|(
name|String
name|topic
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|addReviewer (AddReviewerInput in)
specifier|public
name|void
name|addReviewer
parameter_list|(
name|AddReviewerInput
name|in
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|addReviewer (String in)
specifier|public
name|void
name|addReviewer
parameter_list|(
name|String
name|in
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|suggestReviewers ()
specifier|public
name|SuggestedReviewersRequest
name|suggestReviewers
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|suggestReviewers (String query)
specifier|public
name|SuggestedReviewersRequest
name|suggestReviewers
parameter_list|(
name|String
name|query
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|get (EnumSet<ListChangesOption> options)
specifier|public
name|ChangeInfo
name|get
parameter_list|(
name|EnumSet
argument_list|<
name|ListChangesOption
argument_list|>
name|options
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|ChangeInfo
name|get
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|info ()
specifier|public
name|ChangeInfo
name|info
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getEdit ()
specifier|public
name|EditInfo
name|getEdit
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|edit ()
specifier|public
name|ChangeEditApi
name|edit
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setHashtags (HashtagsInput input)
specifier|public
name|void
name|setHashtags
parameter_list|(
name|HashtagsInput
name|input
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getHashtags ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getHashtags
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setAssignee (AssigneeInput input)
specifier|public
name|AccountInfo
name|setAssignee
parameter_list|(
name|AssigneeInput
name|input
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getAssignee ()
specifier|public
name|AccountInfo
name|getAssignee
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getPastAssignees ()
specifier|public
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|getPastAssignees
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|deleteAssignee ()
specifier|public
name|AccountInfo
name|deleteAssignee
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|comments ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|comments
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|robotComments ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RobotCommentInfo
argument_list|>
argument_list|>
name|robotComments
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|drafts ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|drafts
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|check ()
specifier|public
name|ChangeInfo
name|check
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|check (FixInput fix)
specifier|public
name|ChangeInfo
name|check
parameter_list|(
name|FixInput
name|fix
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|index ()
specifier|public
name|void
name|index
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|submittedTogether ()
specifier|public
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|submittedTogether
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|submittedTogether ( EnumSet<SubmittedTogetherOption> options)
specifier|public
name|SubmittedTogetherInfo
name|submittedTogether
parameter_list|(
name|EnumSet
argument_list|<
name|SubmittedTogetherOption
argument_list|>
name|options
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|submittedTogether ( EnumSet<ListChangesOption> a, EnumSet<SubmittedTogetherOption> b)
specifier|public
name|SubmittedTogetherInfo
name|submittedTogether
parameter_list|(
name|EnumSet
argument_list|<
name|ListChangesOption
argument_list|>
name|a
parameter_list|,
name|EnumSet
argument_list|<
name|SubmittedTogetherOption
argument_list|>
name|b
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|createMergePatchSet (MergePatchSetInput in)
specifier|public
name|ChangeInfo
name|createMergePatchSet
parameter_list|(
name|MergePatchSetInput
name|in
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
block|}
block|}
end_interface

end_unit

