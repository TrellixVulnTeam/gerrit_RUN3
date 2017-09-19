begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.server.event
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|server
operator|.
name|event
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
name|truth
operator|.
name|Truth
operator|.
name|assertThat
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
name|extensions
operator|.
name|client
operator|.
name|ListChangesOption
operator|.
name|DETAILED_LABELS
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
name|group
operator|.
name|SystemGroupBackend
operator|.
name|ANONYMOUS_USERS
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
name|project
operator|.
name|testing
operator|.
name|Util
operator|.
name|category
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
name|project
operator|.
name|testing
operator|.
name|Util
operator|.
name|value
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
name|acceptance
operator|.
name|AbstractDaemonTest
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
name|acceptance
operator|.
name|NoHttpd
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
name|acceptance
operator|.
name|PushOneCommit
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
name|extensions
operator|.
name|api
operator|.
name|changes
operator|.
name|ReviewInput
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
name|ApprovalInfo
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
name|LabelInfo
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
name|events
operator|.
name|CommentAddedListener
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
name|registration
operator|.
name|DynamicSet
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
name|registration
operator|.
name|RegistrationHandle
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
name|git
operator|.
name|ProjectConfig
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
name|testing
operator|.
name|Util
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
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
annotation|@
name|NoHttpd
DECL|class|CommentAddedEventIT
specifier|public
class|class
name|CommentAddedEventIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|source
annotation|@
name|Inject
specifier|private
name|DynamicSet
argument_list|<
name|CommentAddedListener
argument_list|>
name|source
decl_stmt|;
DECL|field|label
specifier|private
specifier|final
name|LabelType
name|label
init|=
name|category
argument_list|(
literal|"CustomLabel"
argument_list|,
name|value
argument_list|(
literal|1
argument_list|,
literal|"Positive"
argument_list|)
argument_list|,
name|value
argument_list|(
literal|0
argument_list|,
literal|"No score"
argument_list|)
argument_list|,
name|value
argument_list|(
operator|-
literal|1
argument_list|,
literal|"Negative"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|pLabel
specifier|private
specifier|final
name|LabelType
name|pLabel
init|=
name|category
argument_list|(
literal|"CustomLabel2"
argument_list|,
name|value
argument_list|(
literal|1
argument_list|,
literal|"Positive"
argument_list|)
argument_list|,
name|value
argument_list|(
literal|0
argument_list|,
literal|"No score"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|eventListenerRegistration
specifier|private
name|RegistrationHandle
name|eventListenerRegistration
decl_stmt|;
DECL|field|lastCommentAddedEvent
specifier|private
name|CommentAddedListener
operator|.
name|Event
name|lastCommentAddedEvent
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|ProjectConfig
name|cfg
init|=
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|project
argument_list|)
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|anonymousUsers
init|=
name|systemGroupBackend
operator|.
name|getGroup
argument_list|(
name|ANONYMOUS_USERS
argument_list|)
operator|.
name|getUUID
argument_list|()
decl_stmt|;
name|Util
operator|.
name|allow
argument_list|(
name|cfg
argument_list|,
name|Permission
operator|.
name|forLabel
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
name|anonymousUsers
argument_list|,
literal|"refs/heads/*"
argument_list|)
expr_stmt|;
name|Util
operator|.
name|allow
argument_list|(
name|cfg
argument_list|,
name|Permission
operator|.
name|forLabel
argument_list|(
name|pLabel
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|anonymousUsers
argument_list|,
literal|"refs/heads/*"
argument_list|)
expr_stmt|;
name|saveProjectConfig
argument_list|(
name|project
argument_list|,
name|cfg
argument_list|)
expr_stmt|;
name|eventListenerRegistration
operator|=
name|source
operator|.
name|add
argument_list|(
operator|new
name|CommentAddedListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onCommentAdded
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
name|lastCommentAddedEvent
operator|=
name|event
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|eventListenerRegistration
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
DECL|method|saveLabelConfig ()
specifier|private
name|void
name|saveLabelConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|ProjectConfig
name|cfg
init|=
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|project
argument_list|)
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|getLabelSections
argument_list|()
operator|.
name|put
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|,
name|label
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|getLabelSections
argument_list|()
operator|.
name|put
argument_list|(
name|pLabel
operator|.
name|getName
argument_list|()
argument_list|,
name|pLabel
argument_list|)
expr_stmt|;
name|saveProjectConfig
argument_list|(
name|project
argument_list|,
name|cfg
argument_list|)
expr_stmt|;
block|}
comment|/* Need to lookup info for the label under test since there can be multiple    * labels defined.  By default Gerrit already has a Code-Review label.    */
DECL|method|getApprovalValues (LabelType label)
specifier|private
name|ApprovalValues
name|getApprovalValues
parameter_list|(
name|LabelType
name|label
parameter_list|)
block|{
name|ApprovalValues
name|res
init|=
operator|new
name|ApprovalValues
argument_list|()
decl_stmt|;
name|ApprovalInfo
name|info
init|=
name|lastCommentAddedEvent
operator|.
name|getApprovals
argument_list|()
operator|.
name|get
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|res
operator|.
name|value
operator|=
name|info
operator|.
name|value
expr_stmt|;
block|}
name|info
operator|=
name|lastCommentAddedEvent
operator|.
name|getOldApprovals
argument_list|()
operator|.
name|get
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|res
operator|.
name|oldValue
operator|=
name|info
operator|.
name|value
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
annotation|@
name|Test
DECL|method|newChangeWithVote ()
specifier|public
name|void
name|newChangeWithVote
parameter_list|()
throws|throws
name|Exception
block|{
name|saveLabelConfig
argument_list|()
expr_stmt|;
comment|// push a new change with -1 vote
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|ReviewInput
name|reviewInput
init|=
operator|new
name|ReviewInput
argument_list|()
operator|.
name|label
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|,
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|)
decl_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|reviewInput
argument_list|)
expr_stmt|;
name|ApprovalValues
name|attr
init|=
name|getApprovalValues
argument_list|(
name|label
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|attr
operator|.
name|oldValue
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|attr
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lastCommentAddedEvent
operator|.
name|getComment
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Patch Set 1: %s-1"
argument_list|,
name|label
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|newPatchSetWithVote ()
specifier|public
name|void
name|newPatchSetWithVote
parameter_list|()
throws|throws
name|Exception
block|{
name|saveLabelConfig
argument_list|()
expr_stmt|;
comment|// push a new change
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|ReviewInput
name|reviewInput
init|=
operator|new
name|ReviewInput
argument_list|()
operator|.
name|message
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|reviewInput
argument_list|)
expr_stmt|;
comment|// push a new revision with +1 vote
name|ChangeInfo
name|c
init|=
name|info
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
decl_stmt|;
name|r
operator|=
name|amendChange
argument_list|(
name|c
operator|.
name|changeId
argument_list|)
expr_stmt|;
name|reviewInput
operator|=
operator|new
name|ReviewInput
argument_list|()
operator|.
name|label
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|reviewInput
argument_list|)
expr_stmt|;
name|ApprovalValues
name|attr
init|=
name|getApprovalValues
argument_list|(
name|label
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|attr
operator|.
name|oldValue
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|attr
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lastCommentAddedEvent
operator|.
name|getComment
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Patch Set 2: %s+1"
argument_list|,
name|label
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reviewChange ()
specifier|public
name|void
name|reviewChange
parameter_list|()
throws|throws
name|Exception
block|{
name|saveLabelConfig
argument_list|()
expr_stmt|;
comment|// push a change
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
comment|// review with message only, do not apply votes
name|ReviewInput
name|reviewInput
init|=
operator|new
name|ReviewInput
argument_list|()
operator|.
name|message
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|reviewInput
argument_list|)
expr_stmt|;
comment|// reply message only so vote is shown as 0
name|ApprovalValues
name|attr
init|=
name|getApprovalValues
argument_list|(
name|label
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|attr
operator|.
name|oldValue
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|attr
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lastCommentAddedEvent
operator|.
name|getComment
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Patch Set 1:\n\n%s"
argument_list|,
name|label
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// transition from un-voted to -1 vote
name|reviewInput
operator|=
operator|new
name|ReviewInput
argument_list|()
operator|.
name|label
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|reviewInput
argument_list|)
expr_stmt|;
name|attr
operator|=
name|getApprovalValues
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|attr
operator|.
name|oldValue
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|attr
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lastCommentAddedEvent
operator|.
name|getComment
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Patch Set 1: %s-1"
argument_list|,
name|label
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// transition vote from -1 to 0
name|reviewInput
operator|=
operator|new
name|ReviewInput
argument_list|()
operator|.
name|label
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|reviewInput
argument_list|)
expr_stmt|;
name|attr
operator|=
name|getApprovalValues
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|attr
operator|.
name|oldValue
argument_list|)
operator|.
name|isEqualTo
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|attr
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lastCommentAddedEvent
operator|.
name|getComment
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Patch Set 1: -%s"
argument_list|,
name|label
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// transition vote from 0 to 1
name|reviewInput
operator|=
operator|new
name|ReviewInput
argument_list|()
operator|.
name|label
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|reviewInput
argument_list|)
expr_stmt|;
name|attr
operator|=
name|getApprovalValues
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|attr
operator|.
name|oldValue
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|attr
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lastCommentAddedEvent
operator|.
name|getComment
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Patch Set 1: %s+1"
argument_list|,
name|label
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// transition vote from 1 to -1
name|reviewInput
operator|=
operator|new
name|ReviewInput
argument_list|()
operator|.
name|label
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|reviewInput
argument_list|)
expr_stmt|;
name|attr
operator|=
name|getApprovalValues
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|attr
operator|.
name|oldValue
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|attr
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lastCommentAddedEvent
operator|.
name|getComment
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Patch Set 1: %s-1"
argument_list|,
name|label
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// review with message only, do not apply votes
name|reviewInput
operator|=
operator|new
name|ReviewInput
argument_list|()
operator|.
name|message
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|reviewInput
argument_list|)
expr_stmt|;
name|attr
operator|=
name|getApprovalValues
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|attr
operator|.
name|oldValue
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
comment|// no vote change so not included
name|assertThat
argument_list|(
name|attr
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lastCommentAddedEvent
operator|.
name|getComment
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Patch Set 1:\n\n%s"
argument_list|,
name|label
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reviewChange_MultipleVotes ()
specifier|public
name|void
name|reviewChange_MultipleVotes
parameter_list|()
throws|throws
name|Exception
block|{
name|saveLabelConfig
argument_list|()
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|ReviewInput
name|reviewInput
init|=
operator|new
name|ReviewInput
argument_list|()
operator|.
name|label
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|reviewInput
operator|.
name|message
operator|=
name|label
operator|.
name|getName
argument_list|()
expr_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|reviewInput
argument_list|)
expr_stmt|;
name|ChangeInfo
name|c
init|=
name|get
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|DETAILED_LABELS
argument_list|)
decl_stmt|;
name|LabelInfo
name|q
init|=
name|c
operator|.
name|labels
operator|.
name|get
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|q
operator|.
name|all
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ApprovalValues
name|labelAttr
init|=
name|getApprovalValues
argument_list|(
name|label
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|labelAttr
operator|.
name|oldValue
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|labelAttr
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lastCommentAddedEvent
operator|.
name|getComment
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Patch Set 1: %s-1\n\n%s"
argument_list|,
name|label
operator|.
name|getName
argument_list|()
argument_list|,
name|label
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// there should be 3 approval labels (label, pLabel, and CRVV)
name|assertThat
argument_list|(
name|lastCommentAddedEvent
operator|.
name|getApprovals
argument_list|()
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|// check the approvals that were not voted on
name|ApprovalValues
name|pLabelAttr
init|=
name|getApprovalValues
argument_list|(
name|pLabel
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|pLabelAttr
operator|.
name|oldValue
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|pLabelAttr
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|LabelType
name|crLabel
init|=
name|LabelType
operator|.
name|withDefaultValues
argument_list|(
literal|"Code-Review"
argument_list|)
decl_stmt|;
name|ApprovalValues
name|crlAttr
init|=
name|getApprovalValues
argument_list|(
name|crLabel
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|crlAttr
operator|.
name|oldValue
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|crlAttr
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// update pLabel approval
name|reviewInput
operator|=
operator|new
name|ReviewInput
argument_list|()
operator|.
name|label
argument_list|(
name|pLabel
operator|.
name|getName
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|reviewInput
operator|.
name|message
operator|=
name|pLabel
operator|.
name|getName
argument_list|()
expr_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|reviewInput
argument_list|)
expr_stmt|;
name|c
operator|=
name|get
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|DETAILED_LABELS
argument_list|)
expr_stmt|;
name|q
operator|=
name|c
operator|.
name|labels
operator|.
name|get
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|q
operator|.
name|all
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|pLabelAttr
operator|=
name|getApprovalValues
argument_list|(
name|pLabel
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|pLabelAttr
operator|.
name|oldValue
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|pLabelAttr
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lastCommentAddedEvent
operator|.
name|getComment
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Patch Set 1: %s+1\n\n%s"
argument_list|,
name|pLabel
operator|.
name|getName
argument_list|()
argument_list|,
name|pLabel
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// check the approvals that were not voted on
name|labelAttr
operator|=
name|getApprovalValues
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|labelAttr
operator|.
name|oldValue
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|labelAttr
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|crlAttr
operator|=
name|getApprovalValues
argument_list|(
name|crLabel
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|crlAttr
operator|.
name|oldValue
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|crlAttr
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|class|ApprovalValues
specifier|private
specifier|static
class|class
name|ApprovalValues
block|{
DECL|field|value
name|Integer
name|value
decl_stmt|;
DECL|field|oldValue
name|Integer
name|oldValue
decl_stmt|;
block|}
block|}
end_class

end_unit

