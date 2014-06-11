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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|change
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|SetMultimap
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
name|util
operator|.
name|concurrent
operator|.
name|CheckedFuture
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
name|ChangeHooks
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
name|ChangeMessage
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
name|PatchSetInfo
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
name|RevId
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
name|ApprovalCopier
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
name|ChangeMessagesUtil
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
name|ChangeUtil
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
name|extensions
operator|.
name|events
operator|.
name|GitReferenceUpdated
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
name|mail
operator|.
name|ReplacePatchSetSender
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
name|ChangeUpdate
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
name|ReviewerState
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
name|patch
operator|.
name|PatchSetInfoFactory
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
name|ChangeControl
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
name|InvalidChangeOperationException
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
name|NoSuchChangeException
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
name|NoSshInfo
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
name|gerrit
operator|.
name|server
operator|.
name|util
operator|.
name|TimeUtil
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
name|AtomicUpdate
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
name|assistedinject
operator|.
name|Assisted
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
name|revwalk
operator|.
name|RevWalk
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Collections
import|;
end_import

begin_class
DECL|class|PatchSetInserter
specifier|public
class|class
name|PatchSetInserter
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PatchSetInserter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|interface|Factory
specifier|public
specifier|static
interface|interface
name|Factory
block|{
DECL|method|create (Repository git, RevWalk revWalk, ChangeControl ctl, RevCommit commit)
name|PatchSetInserter
name|create
parameter_list|(
name|Repository
name|git
parameter_list|,
name|RevWalk
name|revWalk
parameter_list|,
name|ChangeControl
name|ctl
parameter_list|,
name|RevCommit
name|commit
parameter_list|)
function_decl|;
block|}
comment|/**    * Whether to use {@link CommitValidators#validateForGerritCommits},    * {@link CommitValidators#validateForReceiveCommits}, or no commit    * validation.    */
DECL|enum|ValidatePolicy
specifier|public
specifier|static
enum|enum
name|ValidatePolicy
block|{
DECL|enumConstant|GERRIT
DECL|enumConstant|RECEIVE_COMMITS
DECL|enumConstant|NONE
name|GERRIT
block|,
name|RECEIVE_COMMITS
block|,
name|NONE
block|}
DECL|field|hooks
specifier|private
specifier|final
name|ChangeHooks
name|hooks
decl_stmt|;
DECL|field|patchSetInfoFactory
specifier|private
specifier|final
name|PatchSetInfoFactory
name|patchSetInfoFactory
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|updateFactory
specifier|private
specifier|final
name|ChangeUpdate
operator|.
name|Factory
name|updateFactory
decl_stmt|;
DECL|field|ctlFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|GenericFactory
name|ctlFactory
decl_stmt|;
DECL|field|gitRefUpdated
specifier|private
specifier|final
name|GitReferenceUpdated
name|gitRefUpdated
decl_stmt|;
DECL|field|commitValidatorsFactory
specifier|private
specifier|final
name|CommitValidators
operator|.
name|Factory
name|commitValidatorsFactory
decl_stmt|;
DECL|field|mergeabilityChecker
specifier|private
specifier|final
name|MergeabilityChecker
name|mergeabilityChecker
decl_stmt|;
DECL|field|replacePatchSetFactory
specifier|private
specifier|final
name|ReplacePatchSetSender
operator|.
name|Factory
name|replacePatchSetFactory
decl_stmt|;
DECL|field|approvalsUtil
specifier|private
specifier|final
name|ApprovalsUtil
name|approvalsUtil
decl_stmt|;
DECL|field|approvalCopier
specifier|private
specifier|final
name|ApprovalCopier
name|approvalCopier
decl_stmt|;
DECL|field|cmUtil
specifier|private
specifier|final
name|ChangeMessagesUtil
name|cmUtil
decl_stmt|;
DECL|field|git
specifier|private
specifier|final
name|Repository
name|git
decl_stmt|;
DECL|field|revWalk
specifier|private
specifier|final
name|RevWalk
name|revWalk
decl_stmt|;
DECL|field|commit
specifier|private
specifier|final
name|RevCommit
name|commit
decl_stmt|;
DECL|field|ctl
specifier|private
specifier|final
name|ChangeControl
name|ctl
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|IdentifiedUser
name|user
decl_stmt|;
DECL|field|patchSet
specifier|private
name|PatchSet
name|patchSet
decl_stmt|;
DECL|field|changeMessage
specifier|private
name|ChangeMessage
name|changeMessage
decl_stmt|;
DECL|field|copyLabels
specifier|private
name|boolean
name|copyLabels
decl_stmt|;
DECL|field|sshInfo
specifier|private
name|SshInfo
name|sshInfo
decl_stmt|;
DECL|field|validatePolicy
specifier|private
name|ValidatePolicy
name|validatePolicy
init|=
name|ValidatePolicy
operator|.
name|GERRIT
decl_stmt|;
DECL|field|draft
specifier|private
name|boolean
name|draft
decl_stmt|;
DECL|field|runHooks
specifier|private
name|boolean
name|runHooks
decl_stmt|;
DECL|field|sendMail
specifier|private
name|boolean
name|sendMail
decl_stmt|;
DECL|field|uploader
specifier|private
name|Account
operator|.
name|Id
name|uploader
decl_stmt|;
annotation|@
name|Inject
DECL|method|PatchSetInserter (ChangeHooks hooks, ReviewDb db, ChangeUpdate.Factory updateFactory, ChangeControl.GenericFactory ctlFactory, ApprovalsUtil approvalsUtil, ApprovalCopier approvalCopier, ChangeMessagesUtil cmUtil, PatchSetInfoFactory patchSetInfoFactory, GitReferenceUpdated gitRefUpdated, CommitValidators.Factory commitValidatorsFactory, MergeabilityChecker mergeabilityChecker, ReplacePatchSetSender.Factory replacePatchSetFactory, @Assisted Repository git, @Assisted RevWalk revWalk, @Assisted ChangeControl ctl, @Assisted RevCommit commit)
specifier|public
name|PatchSetInserter
parameter_list|(
name|ChangeHooks
name|hooks
parameter_list|,
name|ReviewDb
name|db
parameter_list|,
name|ChangeUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|ChangeControl
operator|.
name|GenericFactory
name|ctlFactory
parameter_list|,
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
name|ApprovalCopier
name|approvalCopier
parameter_list|,
name|ChangeMessagesUtil
name|cmUtil
parameter_list|,
name|PatchSetInfoFactory
name|patchSetInfoFactory
parameter_list|,
name|GitReferenceUpdated
name|gitRefUpdated
parameter_list|,
name|CommitValidators
operator|.
name|Factory
name|commitValidatorsFactory
parameter_list|,
name|MergeabilityChecker
name|mergeabilityChecker
parameter_list|,
name|ReplacePatchSetSender
operator|.
name|Factory
name|replacePatchSetFactory
parameter_list|,
annotation|@
name|Assisted
name|Repository
name|git
parameter_list|,
annotation|@
name|Assisted
name|RevWalk
name|revWalk
parameter_list|,
annotation|@
name|Assisted
name|ChangeControl
name|ctl
parameter_list|,
annotation|@
name|Assisted
name|RevCommit
name|commit
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|ctl
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
argument_list|,
literal|"only IdentifiedUser may create patch set on change %s"
argument_list|,
name|ctl
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|hooks
operator|=
name|hooks
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|updateFactory
operator|=
name|updateFactory
expr_stmt|;
name|this
operator|.
name|ctlFactory
operator|=
name|ctlFactory
expr_stmt|;
name|this
operator|.
name|approvalsUtil
operator|=
name|approvalsUtil
expr_stmt|;
name|this
operator|.
name|approvalCopier
operator|=
name|approvalCopier
expr_stmt|;
name|this
operator|.
name|cmUtil
operator|=
name|cmUtil
expr_stmt|;
name|this
operator|.
name|patchSetInfoFactory
operator|=
name|patchSetInfoFactory
expr_stmt|;
name|this
operator|.
name|gitRefUpdated
operator|=
name|gitRefUpdated
expr_stmt|;
name|this
operator|.
name|commitValidatorsFactory
operator|=
name|commitValidatorsFactory
expr_stmt|;
name|this
operator|.
name|mergeabilityChecker
operator|=
name|mergeabilityChecker
expr_stmt|;
name|this
operator|.
name|replacePatchSetFactory
operator|=
name|replacePatchSetFactory
expr_stmt|;
name|this
operator|.
name|git
operator|=
name|git
expr_stmt|;
name|this
operator|.
name|revWalk
operator|=
name|revWalk
expr_stmt|;
name|this
operator|.
name|commit
operator|=
name|commit
expr_stmt|;
name|this
operator|.
name|ctl
operator|=
name|ctl
expr_stmt|;
name|this
operator|.
name|user
operator|=
operator|(
name|IdentifiedUser
operator|)
name|ctl
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
name|this
operator|.
name|runHooks
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|sendMail
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|setPatchSet (PatchSet patchSet)
specifier|public
name|PatchSetInserter
name|setPatchSet
parameter_list|(
name|PatchSet
name|patchSet
parameter_list|)
block|{
name|Change
name|c
init|=
name|ctl
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|PatchSet
operator|.
name|Id
name|psid
init|=
name|patchSet
operator|.
name|getId
argument_list|()
decl_stmt|;
name|checkArgument
argument_list|(
name|psid
operator|.
name|getParentKey
argument_list|()
operator|.
name|equals
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
literal|"patch set %s not for change %s"
argument_list|,
name|psid
argument_list|,
name|c
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|psid
operator|.
name|get
argument_list|()
operator|>
name|c
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
literal|"new patch set ID %s is not greater than current patch set ID %s"
argument_list|,
name|psid
operator|.
name|get
argument_list|()
argument_list|,
name|c
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|patchSet
operator|=
name|patchSet
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getPatchSetId ()
specifier|public
name|PatchSet
operator|.
name|Id
name|getPatchSetId
parameter_list|()
throws|throws
name|IOException
block|{
name|init
argument_list|()
expr_stmt|;
return|return
name|patchSet
operator|.
name|getId
argument_list|()
return|;
block|}
DECL|method|setMessage (String message)
specifier|public
name|PatchSetInserter
name|setMessage
parameter_list|(
name|String
name|message
parameter_list|)
throws|throws
name|OrmException
block|{
name|changeMessage
operator|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
name|ctl
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|ChangeUtil
operator|.
name|messageUUID
argument_list|(
name|db
argument_list|)
argument_list|)
argument_list|,
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|,
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|changeMessage
operator|.
name|setMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setMessage (ChangeMessage changeMessage)
specifier|public
name|PatchSetInserter
name|setMessage
parameter_list|(
name|ChangeMessage
name|changeMessage
parameter_list|)
throws|throws
name|OrmException
block|{
name|this
operator|.
name|changeMessage
operator|=
name|changeMessage
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCopyLabels (boolean copyLabels)
specifier|public
name|PatchSetInserter
name|setCopyLabels
parameter_list|(
name|boolean
name|copyLabels
parameter_list|)
block|{
name|this
operator|.
name|copyLabels
operator|=
name|copyLabels
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSshInfo (SshInfo sshInfo)
specifier|public
name|PatchSetInserter
name|setSshInfo
parameter_list|(
name|SshInfo
name|sshInfo
parameter_list|)
block|{
name|this
operator|.
name|sshInfo
operator|=
name|sshInfo
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setValidatePolicy (ValidatePolicy validate)
specifier|public
name|PatchSetInserter
name|setValidatePolicy
parameter_list|(
name|ValidatePolicy
name|validate
parameter_list|)
block|{
name|this
operator|.
name|validatePolicy
operator|=
name|checkNotNull
argument_list|(
name|validate
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setDraft (boolean draft)
specifier|public
name|PatchSetInserter
name|setDraft
parameter_list|(
name|boolean
name|draft
parameter_list|)
block|{
name|this
operator|.
name|draft
operator|=
name|draft
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setRunHooks (boolean runHooks)
specifier|public
name|PatchSetInserter
name|setRunHooks
parameter_list|(
name|boolean
name|runHooks
parameter_list|)
block|{
name|this
operator|.
name|runHooks
operator|=
name|runHooks
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSendMail (boolean sendMail)
specifier|public
name|PatchSetInserter
name|setSendMail
parameter_list|(
name|boolean
name|sendMail
parameter_list|)
block|{
name|this
operator|.
name|sendMail
operator|=
name|sendMail
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setUploader (Account.Id uploader)
specifier|public
name|PatchSetInserter
name|setUploader
parameter_list|(
name|Account
operator|.
name|Id
name|uploader
parameter_list|)
block|{
name|this
operator|.
name|uploader
operator|=
name|uploader
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|insert ()
specifier|public
name|Change
name|insert
parameter_list|()
throws|throws
name|InvalidChangeOperationException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|NoSuchChangeException
block|{
name|init
argument_list|()
expr_stmt|;
name|validate
argument_list|()
expr_stmt|;
name|Change
name|c
init|=
name|ctl
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|Change
name|updatedChange
decl_stmt|;
name|RefUpdate
name|ru
init|=
name|git
operator|.
name|updateRef
argument_list|(
name|patchSet
operator|.
name|getRefName
argument_list|()
argument_list|)
decl_stmt|;
name|ru
operator|.
name|setExpectedOldObjectId
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setNewObjectId
argument_list|(
name|commit
argument_list|)
expr_stmt|;
name|ru
operator|.
name|disableRefLog
argument_list|()
expr_stmt|;
if|if
condition|(
name|ru
operator|.
name|update
argument_list|(
name|revWalk
argument_list|)
operator|!=
name|RefUpdate
operator|.
name|Result
operator|.
name|NEW
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to create ref %s in %s: %s"
argument_list|,
name|patchSet
operator|.
name|getRefName
argument_list|()
argument_list|,
name|c
operator|.
name|getDest
argument_list|()
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|ru
operator|.
name|getResult
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|gitRefUpdated
operator|.
name|fire
argument_list|(
name|c
operator|.
name|getProject
argument_list|()
argument_list|,
name|ru
argument_list|)
expr_stmt|;
specifier|final
name|PatchSet
operator|.
name|Id
name|currentPatchSetId
init|=
name|c
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
name|ChangeUpdate
name|update
init|=
name|updateFactory
operator|.
name|create
argument_list|(
name|ctl
argument_list|,
name|patchSet
operator|.
name|getCreatedOn
argument_list|()
argument_list|)
decl_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|beginTransaction
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Change %s is closed"
argument_list|,
name|c
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|ChangeUtil
operator|.
name|insertAncestors
argument_list|(
name|db
argument_list|,
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|,
name|commit
argument_list|)
expr_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|patchSet
argument_list|)
argument_list|)
expr_stmt|;
name|SetMultimap
argument_list|<
name|ReviewerState
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
name|oldReviewers
init|=
name|sendMail
condition|?
name|approvalsUtil
operator|.
name|getReviewers
argument_list|(
name|db
argument_list|,
name|ctl
operator|.
name|getNotes
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
name|updatedChange
operator|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|atomicUpdate
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|AtomicUpdate
argument_list|<
name|Change
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Change
name|update
parameter_list|(
name|Change
name|change
parameter_list|)
block|{
if|if
condition|(
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|isClosed
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|change
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|equals
argument_list|(
name|currentPatchSetId
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|change
operator|.
name|getStatus
argument_list|()
operator|!=
name|Change
operator|.
name|Status
operator|.
name|DRAFT
condition|)
block|{
name|change
operator|.
name|setStatus
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|NEW
argument_list|)
expr_stmt|;
block|}
name|change
operator|.
name|setLastSha1MergeTested
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|change
operator|.
name|setCurrentPatchSet
argument_list|(
name|patchSetInfoFactory
operator|.
name|get
argument_list|(
name|commit
argument_list|,
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ChangeUtil
operator|.
name|updated
argument_list|(
name|change
argument_list|)
expr_stmt|;
return|return
name|change
return|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|updatedChange
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ChangeModifiedException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Change %s was modified"
argument_list|,
name|c
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|messageIsForChange
argument_list|()
condition|)
block|{
name|cmUtil
operator|.
name|addChangeMessage
argument_list|(
name|db
argument_list|,
name|update
argument_list|,
name|changeMessage
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|copyLabels
condition|)
block|{
name|approvalCopier
operator|.
name|copy
argument_list|(
name|db
argument_list|,
name|ctl
argument_list|,
name|patchSet
argument_list|)
expr_stmt|;
block|}
name|db
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|messageIsForChange
argument_list|()
condition|)
block|{
name|update
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|messageIsForChange
argument_list|()
condition|)
block|{
name|commitMessageNotForChange
argument_list|(
name|updatedChange
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sendMail
condition|)
block|{
try|try
block|{
name|PatchSetInfo
name|info
init|=
name|patchSetInfoFactory
operator|.
name|get
argument_list|(
name|commit
argument_list|,
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|ReplacePatchSetSender
name|cm
init|=
name|replacePatchSetFactory
operator|.
name|create
argument_list|(
name|updatedChange
argument_list|)
decl_stmt|;
name|cm
operator|.
name|setFrom
argument_list|(
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setPatchSet
argument_list|(
name|patchSet
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setChangeMessage
argument_list|(
name|changeMessage
argument_list|)
expr_stmt|;
name|cm
operator|.
name|addReviewers
argument_list|(
name|oldReviewers
operator|.
name|get
argument_list|(
name|ReviewerState
operator|.
name|REVIEWER
argument_list|)
argument_list|)
expr_stmt|;
name|cm
operator|.
name|addExtraCC
argument_list|(
name|oldReviewers
operator|.
name|get
argument_list|(
name|ReviewerState
operator|.
name|CC
argument_list|)
argument_list|)
expr_stmt|;
name|cm
operator|.
name|send
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|err
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot send email for new patch set on change "
operator|+
name|updatedChange
operator|.
name|getId
argument_list|()
argument_list|,
name|err
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|db
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
name|CheckedFuture
argument_list|<
name|?
argument_list|,
name|IOException
argument_list|>
name|f
init|=
name|mergeabilityChecker
operator|.
name|newCheck
argument_list|()
operator|.
name|addChange
argument_list|(
name|updatedChange
argument_list|)
operator|.
name|reindex
argument_list|()
operator|.
name|runAsync
argument_list|()
decl_stmt|;
if|if
condition|(
name|runHooks
condition|)
block|{
name|hooks
operator|.
name|doPatchsetCreatedHook
argument_list|(
name|updatedChange
argument_list|,
name|patchSet
argument_list|,
name|db
argument_list|)
expr_stmt|;
block|}
name|f
operator|.
name|checkedGet
argument_list|()
expr_stmt|;
return|return
name|updatedChange
return|;
block|}
DECL|method|commitMessageNotForChange (Change updatedChange)
specifier|private
name|void
name|commitMessageNotForChange
parameter_list|(
name|Change
name|updatedChange
parameter_list|)
throws|throws
name|OrmException
throws|,
name|NoSuchChangeException
throws|,
name|IOException
block|{
if|if
condition|(
name|changeMessage
operator|!=
literal|null
condition|)
block|{
name|Change
name|otherChange
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|changeMessage
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
name|ChangeControl
name|otherControl
init|=
name|ctlFactory
operator|.
name|controlFor
argument_list|(
name|otherChange
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|ChangeUpdate
name|updateForOtherChange
init|=
name|updateFactory
operator|.
name|create
argument_list|(
name|otherControl
argument_list|,
name|updatedChange
operator|.
name|getLastUpdatedOn
argument_list|()
argument_list|)
decl_stmt|;
name|cmUtil
operator|.
name|addChangeMessage
argument_list|(
name|db
argument_list|,
name|updateForOtherChange
argument_list|,
name|changeMessage
argument_list|)
expr_stmt|;
name|updateForOtherChange
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|init ()
specifier|private
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|sshInfo
operator|==
literal|null
condition|)
block|{
name|sshInfo
operator|=
operator|new
name|NoSshInfo
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|patchSet
operator|==
literal|null
condition|)
block|{
name|patchSet
operator|=
operator|new
name|PatchSet
argument_list|(
name|ChangeUtil
operator|.
name|nextPatchSetId
argument_list|(
name|git
argument_list|,
name|ctl
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setCreatedOn
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setUploader
argument_list|(
name|ctl
operator|.
name|getChange
argument_list|()
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setRevision
argument_list|(
operator|new
name|RevId
argument_list|(
name|commit
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|patchSet
operator|.
name|setDraft
argument_list|(
name|draft
argument_list|)
expr_stmt|;
if|if
condition|(
name|uploader
operator|!=
literal|null
condition|)
block|{
name|patchSet
operator|.
name|setUploader
argument_list|(
name|uploader
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|validate ()
specifier|private
name|void
name|validate
parameter_list|()
throws|throws
name|InvalidChangeOperationException
block|{
name|CommitValidators
name|cv
init|=
name|commitValidatorsFactory
operator|.
name|create
argument_list|(
name|ctl
operator|.
name|getRefControl
argument_list|()
argument_list|,
name|sshInfo
argument_list|,
name|git
argument_list|)
decl_stmt|;
name|String
name|refName
init|=
name|patchSet
operator|.
name|getRefName
argument_list|()
decl_stmt|;
name|CommitReceivedEvent
name|event
init|=
operator|new
name|CommitReceivedEvent
argument_list|(
operator|new
name|ReceiveCommand
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|,
name|commit
operator|.
name|getId
argument_list|()
argument_list|,
name|refName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|refName
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
operator|+
literal|1
argument_list|)
operator|+
literal|"new"
argument_list|)
argument_list|,
name|ctl
operator|.
name|getProjectControl
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|,
name|ctl
operator|.
name|getRefControl
argument_list|()
operator|.
name|getRefName
argument_list|()
argument_list|,
name|commit
argument_list|,
name|user
argument_list|)
decl_stmt|;
try|try
block|{
switch|switch
condition|(
name|validatePolicy
condition|)
block|{
case|case
name|RECEIVE_COMMITS
case|:
name|cv
operator|.
name|validateForReceiveCommits
argument_list|(
name|event
argument_list|)
expr_stmt|;
break|break;
case|case
name|GERRIT
case|:
name|cv
operator|.
name|validateForGerritCommits
argument_list|(
name|event
argument_list|)
expr_stmt|;
break|break;
case|case
name|NONE
case|:
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|CommitValidationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|messageIsForChange ()
specifier|private
name|boolean
name|messageIsForChange
parameter_list|()
block|{
return|return
name|changeMessage
operator|!=
literal|null
operator|&&
name|changeMessage
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
operator|.
name|equals
argument_list|(
name|patchSet
operator|.
name|getId
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
return|;
block|}
DECL|class|ChangeModifiedException
specifier|public
class|class
name|ChangeModifiedException
extends|extends
name|InvalidChangeOperationException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|ChangeModifiedException (String msg)
specifier|public
name|ChangeModifiedException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

