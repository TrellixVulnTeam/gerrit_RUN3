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
DECL|package|com.google.gerrit.sshd.commands
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
operator|.
name|commands
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
name|common
operator|.
name|data
operator|.
name|ReviewResult
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
name|changedetail
operator|.
name|AbandonChange
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
name|changedetail
operator|.
name|DeleteDraftPatchSet
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
name|changedetail
operator|.
name|PublishDraft
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
name|changedetail
operator|.
name|RestoreChange
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
name|changedetail
operator|.
name|Submit
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
name|patch
operator|.
name|PublishComments
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
name|project
operator|.
name|ProjectControl
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
name|gerrit
operator|.
name|sshd
operator|.
name|BaseCommand
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
name|util
operator|.
name|cli
operator|.
name|CmdLineParser
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
name|gwtorm
operator|.
name|client
operator|.
name|ResultSet
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
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|Environment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Argument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
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
name|Set
import|;
end_import

begin_class
DECL|class|ReviewCommand
specifier|public
class|class
name|ReviewCommand
extends|extends
name|BaseCommand
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
name|ReviewCommand
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|newCmdLineParser ()
specifier|protected
specifier|final
name|CmdLineParser
name|newCmdLineParser
parameter_list|()
block|{
specifier|final
name|CmdLineParser
name|parser
init|=
name|super
operator|.
name|newCmdLineParser
argument_list|()
decl_stmt|;
for|for
control|(
name|ApproveOption
name|c
range|:
name|optionList
control|)
block|{
name|parser
operator|.
name|addOption
argument_list|(
name|c
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|parser
return|;
block|}
DECL|field|patchSetIds
specifier|private
specifier|final
name|Set
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|>
name|patchSetIds
init|=
operator|new
name|HashSet
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Argument
argument_list|(
name|index
operator|=
literal|0
argument_list|,
name|required
operator|=
literal|true
argument_list|,
name|multiValued
operator|=
literal|true
argument_list|,
name|metaVar
operator|=
literal|"{COMMIT | CHANGE,PATCHSET}"
argument_list|,
name|usage
operator|=
literal|"patch to review"
argument_list|)
DECL|method|addPatchSetId (final String token)
name|void
name|addPatchSetId
parameter_list|(
specifier|final
name|String
name|token
parameter_list|)
block|{
try|try
block|{
name|patchSetIds
operator|.
name|addAll
argument_list|(
name|parsePatchSetId
argument_list|(
name|token
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnloggedFailure
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"database error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--project"
argument_list|,
name|aliases
operator|=
literal|"-p"
argument_list|,
name|usage
operator|=
literal|"project containing the patch set"
argument_list|)
DECL|field|projectControl
specifier|private
name|ProjectControl
name|projectControl
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--message"
argument_list|,
name|aliases
operator|=
literal|"-m"
argument_list|,
name|usage
operator|=
literal|"cover message to publish on change"
argument_list|,
name|metaVar
operator|=
literal|"MESSAGE"
argument_list|)
DECL|field|changeComment
specifier|private
name|String
name|changeComment
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--abandon"
argument_list|,
name|usage
operator|=
literal|"abandon the patch set"
argument_list|)
DECL|field|abandonChange
specifier|private
name|boolean
name|abandonChange
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--restore"
argument_list|,
name|usage
operator|=
literal|"restore an abandoned the patch set"
argument_list|)
DECL|field|restoreChange
specifier|private
name|boolean
name|restoreChange
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--submit"
argument_list|,
name|aliases
operator|=
literal|"-s"
argument_list|,
name|usage
operator|=
literal|"submit the patch set"
argument_list|)
DECL|field|submitChange
specifier|private
name|boolean
name|submitChange
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--force-message"
argument_list|,
name|usage
operator|=
literal|"publish the message, "
operator|+
literal|"even if the label score cannot be applied due to change being closed"
argument_list|)
DECL|field|forceMessage
specifier|private
name|boolean
name|forceMessage
init|=
literal|false
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--publish"
argument_list|,
name|usage
operator|=
literal|"publish a draft patch set"
argument_list|)
DECL|field|publishPatchSet
specifier|private
name|boolean
name|publishPatchSet
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--delete"
argument_list|,
name|usage
operator|=
literal|"delete a draft patch set"
argument_list|)
DECL|field|deleteDraftPatchSet
specifier|private
name|boolean
name|deleteDraftPatchSet
decl_stmt|;
annotation|@
name|Inject
DECL|field|db
specifier|private
name|ReviewDb
name|db
decl_stmt|;
annotation|@
name|Inject
DECL|field|currentUser
specifier|private
name|IdentifiedUser
name|currentUser
decl_stmt|;
annotation|@
name|Inject
DECL|field|approvalTypes
specifier|private
name|ApprovalTypes
name|approvalTypes
decl_stmt|;
annotation|@
name|Inject
DECL|field|changeControlFactory
specifier|private
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|deleteDraftPatchSetFactory
specifier|private
name|DeleteDraftPatchSet
operator|.
name|Factory
name|deleteDraftPatchSetFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|abandonChangeFactory
specifier|private
name|AbandonChange
operator|.
name|Factory
name|abandonChangeFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|functionStateFactory
specifier|private
name|FunctionState
operator|.
name|Factory
name|functionStateFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|publishCommentsFactory
specifier|private
name|PublishComments
operator|.
name|Factory
name|publishCommentsFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|publishDraftFactory
specifier|private
name|PublishDraft
operator|.
name|Factory
name|publishDraftFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|restoreChangeFactory
specifier|private
name|RestoreChange
operator|.
name|Factory
name|restoreChangeFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|submitFactory
specifier|private
name|Submit
operator|.
name|Factory
name|submitFactory
decl_stmt|;
DECL|field|optionList
specifier|private
name|List
argument_list|<
name|ApproveOption
argument_list|>
name|optionList
decl_stmt|;
annotation|@
name|Override
DECL|method|start (final Environment env)
specifier|public
specifier|final
name|void
name|start
parameter_list|(
specifier|final
name|Environment
name|env
parameter_list|)
block|{
name|startThread
argument_list|(
operator|new
name|CommandRunnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Failure
block|{
name|initOptionList
argument_list|()
expr_stmt|;
name|parseCommandLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|abandonChange
condition|)
block|{
if|if
condition|(
name|restoreChange
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"abandon and restore actions are mutually exclusive"
argument_list|)
throw|;
block|}
if|if
condition|(
name|submitChange
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"abandon and submit actions are mutually exclusive"
argument_list|)
throw|;
block|}
if|if
condition|(
name|publishPatchSet
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"abandon and publish actions are mutually exclusive"
argument_list|)
throw|;
block|}
if|if
condition|(
name|deleteDraftPatchSet
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"abandon and delete actions are mutually exclusive"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|publishPatchSet
condition|)
block|{
if|if
condition|(
name|restoreChange
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"publish and restore actions are mutually exclusive"
argument_list|)
throw|;
block|}
if|if
condition|(
name|submitChange
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"publish and submit actions are mutually exclusive"
argument_list|)
throw|;
block|}
if|if
condition|(
name|deleteDraftPatchSet
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"publish and delete actions are mutually exclusive"
argument_list|)
throw|;
block|}
block|}
name|boolean
name|ok
init|=
literal|true
decl_stmt|;
for|for
control|(
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
range|:
name|patchSetIds
control|)
block|{
try|try
block|{
name|approveOne
argument_list|(
name|patchSetId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnloggedFailure
name|e
parameter_list|)
block|{
name|ok
operator|=
literal|false
expr_stmt|;
name|writeError
argument_list|(
literal|"error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|ok
operator|=
literal|false
expr_stmt|;
name|writeError
argument_list|(
literal|"fatal: internal server error while approving "
operator|+
name|patchSetId
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"internal error while approving "
operator|+
name|patchSetId
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|ok
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"one or more approvals failed;"
operator|+
literal|" review output above"
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|approveOne (final PatchSet.Id patchSetId)
specifier|private
name|void
name|approveOne
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|OrmException
throws|,
name|EmailException
throws|,
name|Failure
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
name|ChangeControl
name|changeControl
init|=
name|changeControlFactory
operator|.
name|validateFor
argument_list|(
name|changeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|changeComment
operator|==
literal|null
condition|)
block|{
name|changeComment
operator|=
literal|""
expr_stmt|;
block|}
name|Set
argument_list|<
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
name|aps
init|=
operator|new
name|HashSet
argument_list|<
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ApproveOption
name|ao
range|:
name|optionList
control|)
block|{
name|Short
name|v
init|=
name|ao
operator|.
name|value
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|assertScoreIsAllowed
argument_list|(
name|patchSetId
argument_list|,
name|changeControl
argument_list|,
name|ao
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|aps
operator|.
name|add
argument_list|(
operator|new
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|(
name|ao
operator|.
name|getCategoryId
argument_list|()
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|publishCommentsFactory
operator|.
name|create
argument_list|(
name|patchSetId
argument_list|,
name|changeComment
argument_list|,
name|aps
argument_list|,
name|forceMessage
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
if|if
condition|(
name|abandonChange
condition|)
block|{
specifier|final
name|ReviewResult
name|result
init|=
name|abandonChangeFactory
operator|.
name|create
argument_list|(
name|patchSetId
argument_list|,
name|changeComment
argument_list|)
operator|.
name|call
argument_list|()
decl_stmt|;
name|handleReviewResultErrors
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|restoreChange
condition|)
block|{
specifier|final
name|ReviewResult
name|result
init|=
name|restoreChangeFactory
operator|.
name|create
argument_list|(
name|patchSetId
argument_list|,
name|changeComment
argument_list|)
operator|.
name|call
argument_list|()
decl_stmt|;
name|handleReviewResultErrors
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|submitChange
condition|)
block|{
specifier|final
name|ReviewResult
name|result
init|=
name|submitFactory
operator|.
name|create
argument_list|(
name|patchSetId
argument_list|)
operator|.
name|call
argument_list|()
decl_stmt|;
name|handleReviewResultErrors
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InvalidChangeOperationException
name|e
parameter_list|)
block|{
throw|throw
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
throw|throw
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|publishPatchSet
condition|)
block|{
specifier|final
name|ReviewResult
name|result
init|=
name|publishDraftFactory
operator|.
name|create
argument_list|(
name|patchSetId
argument_list|)
operator|.
name|call
argument_list|()
decl_stmt|;
name|handleReviewResultErrors
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|deleteDraftPatchSet
condition|)
block|{
specifier|final
name|ReviewResult
name|result
init|=
name|deleteDraftPatchSetFactory
operator|.
name|create
argument_list|(
name|patchSetId
argument_list|)
operator|.
name|call
argument_list|()
decl_stmt|;
name|handleReviewResultErrors
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|handleReviewResultErrors (final ReviewResult result)
specifier|private
name|void
name|handleReviewResultErrors
parameter_list|(
specifier|final
name|ReviewResult
name|result
parameter_list|)
block|{
for|for
control|(
name|ReviewResult
operator|.
name|Error
name|resultError
range|:
name|result
operator|.
name|getErrors
argument_list|()
control|)
block|{
name|String
name|errMsg
init|=
literal|"error: (change "
operator|+
name|result
operator|.
name|getChangeId
argument_list|()
operator|+
literal|") "
decl_stmt|;
switch|switch
condition|(
name|resultError
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|ABANDON_NOT_PERMITTED
case|:
name|errMsg
operator|+=
literal|"not permitted to abandon change"
expr_stmt|;
break|break;
case|case
name|RESTORE_NOT_PERMITTED
case|:
name|errMsg
operator|+=
literal|"not permitted to restore change"
expr_stmt|;
break|break;
case|case
name|SUBMIT_NOT_PERMITTED
case|:
name|errMsg
operator|+=
literal|"not permitted to submit change"
expr_stmt|;
break|break;
case|case
name|SUBMIT_NOT_READY
case|:
name|errMsg
operator|+=
literal|"approvals or dependencies lacking"
expr_stmt|;
break|break;
case|case
name|CHANGE_IS_CLOSED
case|:
name|errMsg
operator|+=
literal|"change is closed"
expr_stmt|;
break|break;
case|case
name|PUBLISH_NOT_PERMITTED
case|:
name|errMsg
operator|+=
literal|"not permitted to publish change"
expr_stmt|;
break|break;
case|case
name|DELETE_NOT_PERMITTED
case|:
name|errMsg
operator|+=
literal|"not permitted to delete change/patch set"
expr_stmt|;
break|break;
case|case
name|RULE_ERROR
case|:
name|errMsg
operator|+=
literal|"rule error"
expr_stmt|;
break|break;
case|case
name|NOT_A_DRAFT
case|:
name|errMsg
operator|+=
literal|"change is not a draft"
expr_stmt|;
break|break;
case|case
name|GIT_ERROR
case|:
name|errMsg
operator|+=
literal|"error writing change to git repository"
expr_stmt|;
break|break;
default|default:
name|errMsg
operator|+=
literal|"failure in review"
expr_stmt|;
block|}
if|if
condition|(
name|resultError
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|errMsg
operator|+=
literal|": "
operator|+
name|resultError
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
name|writeError
argument_list|(
name|errMsg
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|parsePatchSetId (final String patchIdentity)
specifier|private
name|Set
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|>
name|parsePatchSetId
parameter_list|(
specifier|final
name|String
name|patchIdentity
parameter_list|)
throws|throws
name|UnloggedFailure
throws|,
name|OrmException
block|{
comment|// By commit?
comment|//
if|if
condition|(
name|patchIdentity
operator|.
name|matches
argument_list|(
literal|"^([0-9a-fA-F]{4,"
operator|+
name|RevId
operator|.
name|LEN
operator|+
literal|"})$"
argument_list|)
condition|)
block|{
specifier|final
name|RevId
name|id
init|=
operator|new
name|RevId
argument_list|(
name|patchIdentity
argument_list|)
decl_stmt|;
specifier|final
name|ResultSet
argument_list|<
name|PatchSet
argument_list|>
name|patches
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|isComplete
argument_list|()
condition|)
block|{
name|patches
operator|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|byRevision
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|patches
operator|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|byRevisionRange
argument_list|(
name|id
argument_list|,
name|id
operator|.
name|max
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Set
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|>
name|matches
init|=
operator|new
name|HashSet
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|PatchSet
name|ps
range|:
name|patches
control|)
block|{
specifier|final
name|Change
name|change
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|ps
operator|.
name|getId
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|inProject
argument_list|(
name|change
argument_list|)
condition|)
block|{
name|matches
operator|.
name|add
argument_list|(
name|ps
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
switch|switch
condition|(
name|matches
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|1
case|:
return|return
name|matches
return|;
case|case
literal|0
case|:
throw|throw
name|error
argument_list|(
literal|"\""
operator|+
name|patchIdentity
operator|+
literal|"\" no such patch set"
argument_list|)
throw|;
default|default:
throw|throw
name|error
argument_list|(
literal|"\""
operator|+
name|patchIdentity
operator|+
literal|"\" matches multiple patch sets"
argument_list|)
throw|;
block|}
block|}
comment|// By older style change,patchset?
comment|//
if|if
condition|(
name|patchIdentity
operator|.
name|matches
argument_list|(
literal|"^[1-9][0-9]*,[1-9][0-9]*$"
argument_list|)
condition|)
block|{
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
decl_stmt|;
try|try
block|{
name|patchSetId
operator|=
name|PatchSet
operator|.
name|Id
operator|.
name|parse
argument_list|(
name|patchIdentity
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
name|error
argument_list|(
literal|"\""
operator|+
name|patchIdentity
operator|+
literal|"\" is not a valid patch set"
argument_list|)
throw|;
block|}
if|if
condition|(
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|patchSetId
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"\""
operator|+
name|patchIdentity
operator|+
literal|"\" no such patch set"
argument_list|)
throw|;
block|}
if|if
condition|(
name|projectControl
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Change
name|change
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|patchSetId
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|inProject
argument_list|(
name|change
argument_list|)
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"change "
operator|+
name|change
operator|.
name|getId
argument_list|()
operator|+
literal|" not in project "
operator|+
name|projectControl
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|patchSetId
argument_list|)
return|;
block|}
throw|throw
name|error
argument_list|(
literal|"\""
operator|+
name|patchIdentity
operator|+
literal|"\" is not a valid patch set"
argument_list|)
throw|;
block|}
DECL|method|inProject (final Change change)
specifier|private
name|boolean
name|inProject
parameter_list|(
specifier|final
name|Change
name|change
parameter_list|)
block|{
if|if
condition|(
name|projectControl
operator|==
literal|null
condition|)
block|{
comment|// No --project option, so they want every project.
return|return
literal|true
return|;
block|}
return|return
name|projectControl
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
operator|.
name|equals
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
return|;
block|}
DECL|method|assertScoreIsAllowed (final PatchSet.Id patchSetId, final ChangeControl changeControl, ApproveOption ao, Short v)
specifier|private
name|void
name|assertScoreIsAllowed
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
specifier|final
name|ChangeControl
name|changeControl
parameter_list|,
name|ApproveOption
name|ao
parameter_list|,
name|Short
name|v
parameter_list|)
throws|throws
name|UnloggedFailure
block|{
specifier|final
name|PatchSetApproval
name|psa
init|=
operator|new
name|PatchSetApproval
argument_list|(
operator|new
name|PatchSetApproval
operator|.
name|Key
argument_list|(
name|patchSetId
argument_list|,
name|currentUser
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|ao
operator|.
name|getCategoryId
argument_list|()
argument_list|)
argument_list|,
name|v
argument_list|)
decl_stmt|;
specifier|final
name|FunctionState
name|fs
init|=
name|functionStateFactory
operator|.
name|create
argument_list|(
name|changeControl
argument_list|,
name|patchSetId
argument_list|,
name|Collections
operator|.
expr|<
name|PatchSetApproval
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|psa
operator|.
name|setValue
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|fs
operator|.
name|normalize
argument_list|(
name|approvalTypes
operator|.
name|byId
argument_list|(
name|psa
operator|.
name|getCategoryId
argument_list|()
argument_list|)
argument_list|,
name|psa
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|!=
name|psa
operator|.
name|getValue
argument_list|()
condition|)
block|{
throw|throw
name|error
argument_list|(
name|ao
operator|.
name|name
argument_list|()
operator|+
literal|"="
operator|+
name|ao
operator|.
name|value
argument_list|()
operator|+
literal|" not permitted"
argument_list|)
throw|;
block|}
block|}
DECL|method|initOptionList ()
specifier|private
name|void
name|initOptionList
parameter_list|()
block|{
name|optionList
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApproveOption
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ApprovalType
name|type
range|:
name|approvalTypes
operator|.
name|getApprovalTypes
argument_list|()
control|)
block|{
name|String
name|usage
init|=
literal|""
decl_stmt|;
specifier|final
name|ApprovalCategory
name|category
init|=
name|type
operator|.
name|getCategory
argument_list|()
decl_stmt|;
name|usage
operator|=
literal|"score for "
operator|+
name|category
operator|.
name|getName
argument_list|()
operator|+
literal|"\n"
expr_stmt|;
for|for
control|(
name|ApprovalCategoryValue
name|v
range|:
name|type
operator|.
name|getValues
argument_list|()
control|)
block|{
name|usage
operator|+=
name|v
operator|.
name|format
argument_list|()
operator|+
literal|"\n"
expr_stmt|;
block|}
specifier|final
name|String
name|name
init|=
literal|"--"
operator|+
name|category
operator|.
name|getName
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'-'
argument_list|)
decl_stmt|;
name|optionList
operator|.
name|add
argument_list|(
operator|new
name|ApproveOption
argument_list|(
name|name
argument_list|,
name|usage
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeError (final String msg)
specifier|private
name|void
name|writeError
parameter_list|(
specifier|final
name|String
name|msg
parameter_list|)
block|{
try|try
block|{
name|err
operator|.
name|write
argument_list|(
name|msg
operator|.
name|getBytes
argument_list|(
name|ENC
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{     }
block|}
DECL|method|error (final String msg)
specifier|private
specifier|static
name|UnloggedFailure
name|error
parameter_list|(
specifier|final
name|String
name|msg
parameter_list|)
block|{
return|return
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
name|msg
argument_list|)
return|;
block|}
block|}
end_class

end_unit

