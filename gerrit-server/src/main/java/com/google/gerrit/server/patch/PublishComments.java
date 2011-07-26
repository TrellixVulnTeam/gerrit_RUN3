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
DECL|package|com.google.gerrit.server.patch
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|patch
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
name|ApprovalType
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
name|ApprovalTypes
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
name|ApprovalCategory
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
name|ApprovalCategoryValue
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
name|PatchLineComment
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
name|mail
operator|.
name|CommentSender
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
name|EmailException
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
name|workflow
operator|.
name|FunctionState
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|client
operator|.
name|VoidResult
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
name|client
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
name|Collection
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
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_class
DECL|class|PublishComments
specifier|public
class|class
name|PublishComments
implements|implements
name|Callable
argument_list|<
name|VoidResult
argument_list|>
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
name|PublishComments
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (PatchSet.Id patchSetId, String messageText, Set<ApprovalCategoryValue.Id> approvals, boolean forceMessage)
name|PublishComments
name|create
parameter_list|(
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
name|String
name|messageText
parameter_list|,
name|Set
argument_list|<
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
name|approvals
parameter_list|,
name|boolean
name|forceMessage
parameter_list|)
function_decl|;
block|}
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|IdentifiedUser
name|user
decl_stmt|;
DECL|field|types
specifier|private
specifier|final
name|ApprovalTypes
name|types
decl_stmt|;
DECL|field|commentSenderFactory
specifier|private
specifier|final
name|CommentSender
operator|.
name|Factory
name|commentSenderFactory
decl_stmt|;
DECL|field|patchSetInfoFactory
specifier|private
specifier|final
name|PatchSetInfoFactory
name|patchSetInfoFactory
decl_stmt|;
DECL|field|changeControlFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
decl_stmt|;
DECL|field|functionStateFactory
specifier|private
specifier|final
name|FunctionState
operator|.
name|Factory
name|functionStateFactory
decl_stmt|;
DECL|field|hooks
specifier|private
specifier|final
name|ChangeHookRunner
name|hooks
decl_stmt|;
DECL|field|patchSetId
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
decl_stmt|;
DECL|field|messageText
specifier|private
specifier|final
name|String
name|messageText
decl_stmt|;
DECL|field|approvals
specifier|private
specifier|final
name|Set
argument_list|<
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
name|approvals
decl_stmt|;
DECL|field|forceMessage
specifier|private
specifier|final
name|boolean
name|forceMessage
decl_stmt|;
DECL|field|change
specifier|private
name|Change
name|change
decl_stmt|;
DECL|field|patchSet
specifier|private
name|PatchSet
name|patchSet
decl_stmt|;
DECL|field|message
specifier|private
name|ChangeMessage
name|message
decl_stmt|;
DECL|field|drafts
specifier|private
name|List
argument_list|<
name|PatchLineComment
argument_list|>
name|drafts
decl_stmt|;
annotation|@
name|Inject
DECL|method|PublishComments (final ReviewDb db, final IdentifiedUser user, final ApprovalTypes approvalTypes, final CommentSender.Factory commentSenderFactory, final PatchSetInfoFactory patchSetInfoFactory, final ChangeControl.Factory changeControlFactory, final FunctionState.Factory functionStateFactory, final ChangeHookRunner hooks, @Assisted final PatchSet.Id patchSetId, @Assisted final String messageText, @Assisted final Set<ApprovalCategoryValue.Id> approvals, @Assisted final boolean forceMessage)
name|PublishComments
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|IdentifiedUser
name|user
parameter_list|,
specifier|final
name|ApprovalTypes
name|approvalTypes
parameter_list|,
specifier|final
name|CommentSender
operator|.
name|Factory
name|commentSenderFactory
parameter_list|,
specifier|final
name|PatchSetInfoFactory
name|patchSetInfoFactory
parameter_list|,
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
parameter_list|,
specifier|final
name|FunctionState
operator|.
name|Factory
name|functionStateFactory
parameter_list|,
specifier|final
name|ChangeHookRunner
name|hooks
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|String
name|messageText
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|Set
argument_list|<
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
name|approvals
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|boolean
name|forceMessage
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|types
operator|=
name|approvalTypes
expr_stmt|;
name|this
operator|.
name|patchSetInfoFactory
operator|=
name|patchSetInfoFactory
expr_stmt|;
name|this
operator|.
name|commentSenderFactory
operator|=
name|commentSenderFactory
expr_stmt|;
name|this
operator|.
name|changeControlFactory
operator|=
name|changeControlFactory
expr_stmt|;
name|this
operator|.
name|functionStateFactory
operator|=
name|functionStateFactory
expr_stmt|;
name|this
operator|.
name|hooks
operator|=
name|hooks
expr_stmt|;
name|this
operator|.
name|patchSetId
operator|=
name|patchSetId
expr_stmt|;
name|this
operator|.
name|messageText
operator|=
name|messageText
expr_stmt|;
name|this
operator|.
name|approvals
operator|=
name|approvals
expr_stmt|;
name|this
operator|.
name|forceMessage
operator|=
name|forceMessage
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|VoidResult
name|call
parameter_list|()
throws|throws
name|NoSuchChangeException
throws|,
name|InvalidChangeOperationException
throws|,
name|OrmException
block|{
specifier|final
name|Change
operator|.
name|Id
name|changeId
init|=
name|patchSetId
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
specifier|final
name|ChangeControl
name|ctl
init|=
name|changeControlFactory
operator|.
name|validateFor
argument_list|(
name|changeId
argument_list|)
decl_stmt|;
name|change
operator|=
name|ctl
operator|.
name|getChange
argument_list|()
expr_stmt|;
name|patchSet
operator|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|patchSetId
argument_list|)
expr_stmt|;
if|if
condition|(
name|patchSet
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|)
throw|;
block|}
name|drafts
operator|=
name|drafts
argument_list|()
expr_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|beginTransaction
argument_list|(
name|changeId
argument_list|)
expr_stmt|;
try|try
block|{
name|publishDrafts
argument_list|()
expr_stmt|;
specifier|final
name|boolean
name|isCurrent
init|=
name|patchSetId
operator|.
name|equals
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|isCurrent
operator|&&
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|publishApprovals
argument_list|(
name|ctl
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|approvals
operator|.
name|isEmpty
argument_list|()
operator|||
name|forceMessage
condition|)
block|{
name|publishMessageOnly
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"Change is closed"
argument_list|)
throw|;
block|}
name|touchChange
argument_list|()
expr_stmt|;
name|db
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|db
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
name|email
argument_list|()
expr_stmt|;
name|fireHook
argument_list|()
expr_stmt|;
return|return
name|VoidResult
operator|.
name|INSTANCE
return|;
block|}
DECL|method|publishDrafts ()
specifier|private
name|void
name|publishDrafts
parameter_list|()
throws|throws
name|OrmException
block|{
for|for
control|(
specifier|final
name|PatchLineComment
name|c
range|:
name|drafts
control|)
block|{
name|c
operator|.
name|setStatus
argument_list|(
name|PatchLineComment
operator|.
name|Status
operator|.
name|PUBLISHED
argument_list|)
expr_stmt|;
name|c
operator|.
name|updated
argument_list|()
expr_stmt|;
block|}
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|update
argument_list|(
name|drafts
argument_list|)
expr_stmt|;
block|}
DECL|method|publishApprovals (ChangeControl ctl)
specifier|private
name|void
name|publishApprovals
parameter_list|(
name|ChangeControl
name|ctl
parameter_list|)
throws|throws
name|OrmException
block|{
name|ChangeUtil
operator|.
name|updated
argument_list|(
name|change
argument_list|)
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|>
name|dirty
init|=
operator|new
name|HashSet
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|ins
init|=
operator|new
name|ArrayList
argument_list|<
name|PatchSetApproval
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|upd
init|=
operator|new
name|ArrayList
argument_list|<
name|PatchSetApproval
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|all
init|=
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byPatchSet
argument_list|(
name|patchSetId
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|PatchSetApproval
argument_list|>
name|mine
init|=
name|mine
argument_list|(
name|all
argument_list|)
decl_stmt|;
comment|// Ensure any new approvals are stored properly.
comment|//
for|for
control|(
specifier|final
name|ApprovalCategoryValue
operator|.
name|Id
name|want
range|:
name|approvals
control|)
block|{
name|PatchSetApproval
name|a
init|=
name|mine
operator|.
name|get
argument_list|(
name|want
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|==
literal|null
condition|)
block|{
name|a
operator|=
operator|new
name|PatchSetApproval
argument_list|(
operator|new
name|PatchSetApproval
operator|.
name|Key
argument_list|(
comment|//
name|patchSetId
argument_list|,
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|want
operator|.
name|getParentKey
argument_list|()
argument_list|)
argument_list|,
name|want
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|.
name|cache
argument_list|(
name|change
argument_list|)
expr_stmt|;
name|ins
operator|.
name|add
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|all
operator|.
name|add
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|mine
operator|.
name|put
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|dirty
operator|.
name|add
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Normalize all of the items the user is changing.
comment|//
specifier|final
name|FunctionState
name|functionState
init|=
name|functionStateFactory
operator|.
name|create
argument_list|(
name|ctl
argument_list|,
name|patchSetId
argument_list|,
name|all
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|ApprovalCategoryValue
operator|.
name|Id
name|want
range|:
name|approvals
control|)
block|{
specifier|final
name|PatchSetApproval
name|a
init|=
name|mine
operator|.
name|get
argument_list|(
name|want
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|short
name|o
init|=
name|a
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|a
operator|.
name|setValue
argument_list|(
name|want
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|.
name|cache
argument_list|(
name|change
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|ApprovalCategory
operator|.
name|SUBMIT
operator|.
name|equals
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
argument_list|)
condition|)
block|{
name|functionState
operator|.
name|normalize
argument_list|(
name|types
operator|.
name|byId
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
argument_list|)
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|o
operator|!=
name|a
operator|.
name|getValue
argument_list|()
condition|)
block|{
comment|// Value changed, ensure we update the database.
comment|//
name|a
operator|.
name|setGranted
argument_list|()
expr_stmt|;
name|dirty
operator|.
name|add
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|ins
operator|.
name|contains
argument_list|(
name|a
argument_list|)
condition|)
block|{
name|upd
operator|.
name|add
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Format a message explaining the actions taken.
comment|//
specifier|final
name|StringBuilder
name|msgbuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|ApprovalType
name|at
range|:
name|types
operator|.
name|getApprovalTypes
argument_list|()
control|)
block|{
if|if
condition|(
name|dirty
operator|.
name|contains
argument_list|(
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|PatchSetApproval
name|a
init|=
name|mine
operator|.
name|get
argument_list|(
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|getValue
argument_list|()
operator|==
literal|0
operator|&&
name|ins
operator|.
name|contains
argument_list|(
name|a
argument_list|)
condition|)
block|{
comment|// Don't say "no score" for an initial entry.
continue|continue;
block|}
specifier|final
name|ApprovalCategoryValue
name|val
init|=
name|at
operator|.
name|getValue
argument_list|(
name|a
argument_list|)
decl_stmt|;
if|if
condition|(
name|msgbuf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|msgbuf
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|val
operator|!=
literal|null
operator|&&
name|val
operator|.
name|getName
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|val
operator|.
name|getName
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|msgbuf
operator|.
name|append
argument_list|(
name|val
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|msgbuf
operator|.
name|append
argument_list|(
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|msgbuf
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
if|if
condition|(
name|a
operator|.
name|getValue
argument_list|()
operator|>
literal|0
condition|)
name|msgbuf
operator|.
name|append
argument_list|(
literal|'+'
argument_list|)
expr_stmt|;
name|msgbuf
operator|.
name|append
argument_list|(
name|a
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Update dashboards for everyone else.
comment|//
for|for
control|(
name|PatchSetApproval
name|a
range|:
name|all
control|)
block|{
if|if
condition|(
operator|!
name|user
operator|.
name|getAccountId
argument_list|()
operator|.
name|equals
argument_list|(
name|a
operator|.
name|getAccountId
argument_list|()
argument_list|)
condition|)
block|{
name|a
operator|.
name|cache
argument_list|(
name|change
argument_list|)
expr_stmt|;
name|upd
operator|.
name|add
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
block|}
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|update
argument_list|(
name|upd
argument_list|)
expr_stmt|;
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|insert
argument_list|(
name|ins
argument_list|)
expr_stmt|;
name|summarizeInlineComments
argument_list|(
name|msgbuf
argument_list|)
expr_stmt|;
name|message
argument_list|(
name|msgbuf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|publishMessageOnly ()
specifier|private
name|void
name|publishMessageOnly
parameter_list|()
throws|throws
name|OrmException
block|{
name|StringBuilder
name|msgbuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|summarizeInlineComments
argument_list|(
name|msgbuf
argument_list|)
expr_stmt|;
name|message
argument_list|(
name|msgbuf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|message (String actions)
specifier|private
name|void
name|message
parameter_list|(
name|String
name|actions
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
operator|(
name|actions
operator|==
literal|null
operator|||
name|actions
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
operator|(
name|messageText
operator|==
literal|null
operator|||
name|messageText
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
comment|// They had nothing to say?
comment|//
return|return;
block|}
specifier|final
name|StringBuilder
name|msgbuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|msgbuf
operator|.
name|append
argument_list|(
literal|"Patch Set "
operator|+
name|patchSetId
operator|.
name|get
argument_list|()
operator|+
literal|":"
argument_list|)
expr_stmt|;
if|if
condition|(
name|actions
operator|!=
literal|null
operator|&&
operator|!
name|actions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|msgbuf
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|msgbuf
operator|.
name|append
argument_list|(
name|actions
argument_list|)
expr_stmt|;
block|}
name|msgbuf
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
name|msgbuf
operator|.
name|append
argument_list|(
name|messageText
operator|!=
literal|null
condition|?
name|messageText
else|:
literal|""
argument_list|)
expr_stmt|;
name|message
operator|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
comment|//
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
name|patchSetId
argument_list|)
expr_stmt|;
name|message
operator|.
name|setMessage
argument_list|(
name|msgbuf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|db
operator|.
name|changeMessages
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|mine ( Collection<PatchSetApproval> all)
specifier|private
name|Map
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|PatchSetApproval
argument_list|>
name|mine
parameter_list|(
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|all
parameter_list|)
block|{
name|Map
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|PatchSetApproval
argument_list|>
name|r
init|=
operator|new
name|HashMap
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|PatchSetApproval
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|a
range|:
name|all
control|)
block|{
if|if
condition|(
name|user
operator|.
name|getAccountId
argument_list|()
operator|.
name|equals
argument_list|(
name|a
operator|.
name|getAccountId
argument_list|()
argument_list|)
condition|)
block|{
name|r
operator|.
name|put
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|r
return|;
block|}
DECL|method|touchChange ()
specifier|private
name|void
name|touchChange
parameter_list|()
block|{
try|try
block|{
name|ChangeUtil
operator|.
name|touch
argument_list|(
name|change
argument_list|,
name|db
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{     }
block|}
DECL|method|drafts ()
specifier|private
name|List
argument_list|<
name|PatchLineComment
argument_list|>
name|drafts
parameter_list|()
throws|throws
name|OrmException
block|{
return|return
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|draftByPatchSetAuthor
argument_list|(
name|patchSetId
argument_list|,
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|toList
argument_list|()
return|;
block|}
DECL|method|email ()
specifier|private
name|void
name|email
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
specifier|final
name|CommentSender
name|cm
init|=
name|commentSenderFactory
operator|.
name|create
argument_list|(
name|change
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
name|patchSetInfoFactory
operator|.
name|get
argument_list|(
name|db
argument_list|,
name|patchSetId
argument_list|)
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setChangeMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setPatchLineComments
argument_list|(
name|drafts
argument_list|)
expr_stmt|;
name|cm
operator|.
name|send
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|EmailException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot send comments by email for patch set "
operator|+
name|patchSetId
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PatchSetInfoNotAvailableException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to obtain PatchSetInfo for patch set "
operator|+
name|patchSetId
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fireHook ()
specifier|private
name|void
name|fireHook
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
name|changed
init|=
operator|new
name|HashMap
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ApprovalCategoryValue
operator|.
name|Id
name|v
range|:
name|approvals
control|)
block|{
name|changed
operator|.
name|put
argument_list|(
name|v
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
name|hooks
operator|.
name|doCommentAddedHook
argument_list|(
name|change
argument_list|,
name|user
operator|.
name|getAccount
argument_list|()
argument_list|,
name|patchSet
argument_list|,
name|messageText
argument_list|,
name|changed
argument_list|)
expr_stmt|;
block|}
DECL|method|summarizeInlineComments (StringBuilder in)
specifier|private
name|void
name|summarizeInlineComments
parameter_list|(
name|StringBuilder
name|in
parameter_list|)
block|{
if|if
condition|(
operator|!
name|drafts
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|in
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|in
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|drafts
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|in
operator|.
name|append
argument_list|(
literal|"(1 inline comment)"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|in
operator|.
name|append
argument_list|(
literal|"("
operator|+
name|drafts
operator|.
name|size
argument_list|()
operator|+
literal|" inline comments)"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

