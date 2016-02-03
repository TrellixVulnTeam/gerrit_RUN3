begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.server.project
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
name|project
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
name|AddReviewerInput
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
name|group
operator|.
name|SystemGroupBackend
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
name|Util
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
DECL|class|CustomLabelIT
specifier|public
class|class
name|CustomLabelIT
extends|extends
name|AbstractDaemonTest
block|{
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
DECL|field|P
specifier|private
specifier|final
name|LabelType
name|P
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
name|SystemGroupBackend
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
name|P
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
block|}
annotation|@
name|Test
DECL|method|customLabelNoOp_NegativeVoteNotBlock ()
specifier|public
name|void
name|customLabelNoOp_NegativeVoteNotBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|label
operator|.
name|setFunctionName
argument_list|(
literal|"NoOp"
argument_list|)
expr_stmt|;
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
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
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
name|assertThat
argument_list|(
name|q
operator|.
name|rejected
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|q
operator|.
name|blocking
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|customLabelNoBlock_NegativeVoteNotBlock ()
specifier|public
name|void
name|customLabelNoBlock_NegativeVoteNotBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|label
operator|.
name|setFunctionName
argument_list|(
literal|"NoBlock"
argument_list|)
expr_stmt|;
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
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
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
name|assertThat
argument_list|(
name|q
operator|.
name|rejected
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|q
operator|.
name|blocking
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|customLabelMaxNoBlock_NegativeVoteNotBlock ()
specifier|public
name|void
name|customLabelMaxNoBlock_NegativeVoteNotBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|label
operator|.
name|setFunctionName
argument_list|(
literal|"MaxNoBlock"
argument_list|)
expr_stmt|;
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
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
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
name|assertThat
argument_list|(
name|q
operator|.
name|rejected
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|q
operator|.
name|blocking
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|customLabelAnyWithBlock_NegativeVoteBlock ()
specifier|public
name|void
name|customLabelAnyWithBlock_NegativeVoteBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|label
operator|.
name|setFunctionName
argument_list|(
literal|"AnyWithBlock"
argument_list|)
expr_stmt|;
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
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
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
name|assertThat
argument_list|(
name|q
operator|.
name|disliked
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|q
operator|.
name|rejected
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|q
operator|.
name|blocking
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|customLabelAnyWithBlock_Addreviewer_ZeroVote ()
specifier|public
name|void
name|customLabelAnyWithBlock_Addreviewer_ZeroVote
parameter_list|()
throws|throws
name|Exception
block|{
name|P
operator|.
name|setFunctionName
argument_list|(
literal|"AnyWithBlock"
argument_list|)
expr_stmt|;
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
name|AddReviewerInput
name|in
init|=
operator|new
name|AddReviewerInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|reviewer
operator|=
name|user
operator|.
name|email
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|addReviewer
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
operator|new
name|ReviewInput
argument_list|()
operator|.
name|label
argument_list|(
name|P
operator|.
name|getName
argument_list|()
argument_list|,
literal|0
argument_list|)
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
name|P
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
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|q
operator|.
name|disliked
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|q
operator|.
name|rejected
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|q
operator|.
name|blocking
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|customLabelMaxWithBlock_NegativeVoteBlock ()
specifier|public
name|void
name|customLabelMaxWithBlock_NegativeVoteBlock
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
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
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
name|assertThat
argument_list|(
name|q
operator|.
name|disliked
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|q
operator|.
name|rejected
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|q
operator|.
name|blocking
argument_list|)
operator|.
name|isTrue
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
name|P
operator|.
name|getName
argument_list|()
argument_list|,
name|P
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
block|}
end_class

end_unit

