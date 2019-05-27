begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.git.receive
package|package
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
name|receive
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|git
operator|.
name|ObjectIds
operator|.
name|abbreviateName
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|transport
operator|.
name|ReceiveCommand
operator|.
name|Result
operator|.
name|REJECTED_OTHER_REASON
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
name|reviewdb
operator|.
name|client
operator|.
name|BranchNameKey
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
name|CommitReceivedEvent
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
name|validators
operator|.
name|CommitValidationException
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
name|validators
operator|.
name|CommitValidationMessage
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
name|validators
operator|.
name|CommitValidators
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
name|ssh
operator|.
name|SshInfo
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
name|io
operator|.
name|IOException
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
name|ObjectReader
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
name|notes
operator|.
name|NoteMap
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
name|revwalk
operator|.
name|RevCommit
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
name|transport
operator|.
name|ReceiveCommand
import|;
end_import

begin_comment
comment|/** Validates single commits for a branch. */
end_comment

begin_class
DECL|class|BranchCommitValidator
specifier|public
class|class
name|BranchCommitValidator
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
DECL|field|commitValidatorsFactory
specifier|private
specifier|final
name|CommitValidators
operator|.
name|Factory
name|commitValidatorsFactory
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|IdentifiedUser
name|user
decl_stmt|;
DECL|field|permissions
specifier|private
specifier|final
name|PermissionBackend
operator|.
name|ForProject
name|permissions
decl_stmt|;
DECL|field|project
specifier|private
specifier|final
name|Project
name|project
decl_stmt|;
DECL|field|branch
specifier|private
specifier|final
name|BranchNameKey
name|branch
decl_stmt|;
DECL|field|sshInfo
specifier|private
specifier|final
name|SshInfo
name|sshInfo
decl_stmt|;
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create ( ProjectState projectState, BranchNameKey branch, IdentifiedUser user)
name|BranchCommitValidator
name|create
parameter_list|(
name|ProjectState
name|projectState
parameter_list|,
name|BranchNameKey
name|branch
parameter_list|,
name|IdentifiedUser
name|user
parameter_list|)
function_decl|;
block|}
comment|/** A boolean validation status and a list of additional messages. */
annotation|@
name|AutoValue
DECL|class|Result
specifier|abstract
specifier|static
class|class
name|Result
block|{
DECL|method|create (boolean isValid, ImmutableList<CommitValidationMessage> messages)
specifier|static
name|Result
name|create
parameter_list|(
name|boolean
name|isValid
parameter_list|,
name|ImmutableList
argument_list|<
name|CommitValidationMessage
argument_list|>
name|messages
parameter_list|)
block|{
return|return
operator|new
name|AutoValue_BranchCommitValidator_Result
argument_list|(
name|isValid
argument_list|,
name|messages
argument_list|)
return|;
block|}
comment|/** Whether the commit is valid. */
DECL|method|isValid ()
specifier|abstract
name|boolean
name|isValid
parameter_list|()
function_decl|;
comment|/**      * A list of messages related to the validation. Messages may be present regardless of the      * {@link #isValid()} status.      */
DECL|method|messages ()
specifier|abstract
name|ImmutableList
argument_list|<
name|CommitValidationMessage
argument_list|>
name|messages
parameter_list|()
function_decl|;
block|}
annotation|@
name|Inject
DECL|method|BranchCommitValidator ( CommitValidators.Factory commitValidatorsFactory, PermissionBackend permissionBackend, SshInfo sshInfo, @Assisted ProjectState projectState, @Assisted BranchNameKey branch, @Assisted IdentifiedUser user)
name|BranchCommitValidator
parameter_list|(
name|CommitValidators
operator|.
name|Factory
name|commitValidatorsFactory
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|SshInfo
name|sshInfo
parameter_list|,
annotation|@
name|Assisted
name|ProjectState
name|projectState
parameter_list|,
annotation|@
name|Assisted
name|BranchNameKey
name|branch
parameter_list|,
annotation|@
name|Assisted
name|IdentifiedUser
name|user
parameter_list|)
block|{
name|this
operator|.
name|sshInfo
operator|=
name|sshInfo
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|branch
operator|=
name|branch
expr_stmt|;
name|this
operator|.
name|commitValidatorsFactory
operator|=
name|commitValidatorsFactory
expr_stmt|;
name|project
operator|=
name|projectState
operator|.
name|getProject
argument_list|()
expr_stmt|;
name|permissions
operator|=
name|permissionBackend
operator|.
name|user
argument_list|(
name|user
argument_list|)
operator|.
name|project
argument_list|(
name|project
operator|.
name|getNameKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validates a single commit. If the commit does not validate, the command is rejected.    *    * @param objectReader the object reader to use.    * @param cmd the ReceiveCommand executing the push.    * @param commit the commit being validated.    * @param isMerged whether this is a merge commit created by magicBranch --merge option    * @param change the change for which this is a new patchset.    * @return The validation {@link Result}.    */
DECL|method|validateCommit ( ObjectReader objectReader, ReceiveCommand cmd, RevCommit commit, boolean isMerged, NoteMap rejectCommits, @Nullable Change change)
name|Result
name|validateCommit
parameter_list|(
name|ObjectReader
name|objectReader
parameter_list|,
name|ReceiveCommand
name|cmd
parameter_list|,
name|RevCommit
name|commit
parameter_list|,
name|boolean
name|isMerged
parameter_list|,
name|NoteMap
name|rejectCommits
parameter_list|,
annotation|@
name|Nullable
name|Change
name|change
parameter_list|)
throws|throws
name|IOException
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|CommitValidationMessage
argument_list|>
name|messages
init|=
operator|new
name|ImmutableList
operator|.
name|Builder
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|CommitReceivedEvent
name|receiveEvent
init|=
operator|new
name|CommitReceivedEvent
argument_list|(
name|cmd
argument_list|,
name|project
argument_list|,
name|branch
operator|.
name|branch
argument_list|()
argument_list|,
name|objectReader
argument_list|,
name|commit
argument_list|,
name|user
argument_list|)
init|)
block|{
name|CommitValidators
name|validators
decl_stmt|;
if|if
condition|(
name|isMerged
condition|)
block|{
name|validators
operator|=
name|commitValidatorsFactory
operator|.
name|forMergedCommits
argument_list|(
name|permissions
argument_list|,
name|branch
argument_list|,
name|user
operator|.
name|asIdentifiedUser
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|validators
operator|=
name|commitValidatorsFactory
operator|.
name|forReceiveCommits
argument_list|(
name|permissions
argument_list|,
name|branch
argument_list|,
name|user
operator|.
name|asIdentifiedUser
argument_list|()
argument_list|,
name|sshInfo
argument_list|,
name|rejectCommits
argument_list|,
name|receiveEvent
operator|.
name|revWalk
argument_list|,
name|change
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|CommitValidationMessage
name|m
range|:
name|validators
operator|.
name|validate
argument_list|(
name|receiveEvent
argument_list|)
control|)
block|{
name|messages
operator|.
name|add
argument_list|(
operator|new
name|CommitValidationMessage
argument_list|(
name|messageForCommit
argument_list|(
name|commit
argument_list|,
name|m
operator|.
name|getMessage
argument_list|()
argument_list|,
name|objectReader
argument_list|)
argument_list|,
name|m
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|CommitValidationException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atFine
argument_list|()
operator|.
name|log
argument_list|(
literal|"Commit validation failed on %s"
argument_list|,
name|commit
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|CommitValidationMessage
name|m
range|:
name|e
operator|.
name|getMessages
argument_list|()
control|)
block|{
comment|// The non-error messages may contain background explanation for the
comment|// fatal error, so have to preserve all messages.
name|messages
operator|.
name|add
argument_list|(
operator|new
name|CommitValidationMessage
argument_list|(
name|messageForCommit
argument_list|(
name|commit
argument_list|,
name|m
operator|.
name|getMessage
argument_list|()
argument_list|,
name|objectReader
argument_list|)
argument_list|,
name|m
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|cmd
operator|.
name|setResult
argument_list|(
name|REJECTED_OTHER_REASON
argument_list|,
name|messageForCommit
argument_list|(
name|commit
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|objectReader
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Result
operator|.
name|create
argument_list|(
literal|false
argument_list|,
name|messages
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
return|return
name|Result
operator|.
name|create
argument_list|(
literal|true
argument_list|,
name|messages
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|messageForCommit (RevCommit c, String msg, ObjectReader objectReader)
specifier|private
name|String
name|messageForCommit
parameter_list|(
name|RevCommit
name|c
parameter_list|,
name|String
name|msg
parameter_list|,
name|ObjectReader
name|objectReader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"commit %s: %s"
argument_list|,
name|abbreviateName
argument_list|(
name|c
argument_list|,
name|objectReader
argument_list|)
argument_list|,
name|msg
argument_list|)
return|;
block|}
block|}
end_class

end_unit

