begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.server.mail
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
name|mail
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
name|server
operator|.
name|account
operator|.
name|WatchConfig
operator|.
name|NotifyType
operator|.
name|NEW_CHANGES
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
name|account
operator|.
name|WatchConfig
operator|.
name|NotifyType
operator|.
name|NEW_PATCHSETS
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
name|gerrit
operator|.
name|acceptance
operator|.
name|AbstractNotificationTest
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
DECL|class|CreateChangeSenderIT
specifier|public
class|class
name|CreateChangeSenderIT
extends|extends
name|AbstractNotificationTest
block|{
annotation|@
name|Test
DECL|method|createReviewableChange ()
specifier|public
name|void
name|createReviewableChange
parameter_list|()
throws|throws
name|Exception
block|{
name|StagedPreChange
name|spc
init|=
name|stagePreChange
argument_list|(
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|sender
argument_list|)
operator|.
name|sent
argument_list|(
literal|"newchange"
argument_list|,
name|spc
argument_list|)
operator|.
name|to
argument_list|(
name|spc
operator|.
name|watchingProjectOwner
argument_list|)
operator|.
name|bcc
argument_list|(
name|NEW_CHANGES
argument_list|,
name|NEW_PATCHSETS
argument_list|)
operator|.
name|noOneElse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|createWipChange ()
specifier|public
name|void
name|createWipChange
parameter_list|()
throws|throws
name|Exception
block|{
name|StagedPreChange
name|spc
init|=
name|stagePreChange
argument_list|(
literal|"refs/for/master%wip"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|sender
argument_list|)
operator|.
name|notSent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|createReviewableChangeWithNotifyOwnerReviewers ()
specifier|public
name|void
name|createReviewableChangeWithNotifyOwnerReviewers
parameter_list|()
throws|throws
name|Exception
block|{
name|stagePreChange
argument_list|(
literal|"refs/for/master%notify=OWNER_REVIEWERS"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sender
argument_list|)
operator|.
name|notSent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|createReviewableChangeWithNotifyOwner ()
specifier|public
name|void
name|createReviewableChangeWithNotifyOwner
parameter_list|()
throws|throws
name|Exception
block|{
name|stagePreChange
argument_list|(
literal|"refs/for/master%notify=OWNER"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sender
argument_list|)
operator|.
name|notSent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|createReviewableChangeWithNotifyNone ()
specifier|public
name|void
name|createReviewableChangeWithNotifyNone
parameter_list|()
throws|throws
name|Exception
block|{
name|stagePreChange
argument_list|(
literal|"refs/for/master%notify=OWNER"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sender
argument_list|)
operator|.
name|notSent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|createWipChangeWithNotifyAll ()
specifier|public
name|void
name|createWipChangeWithNotifyAll
parameter_list|()
throws|throws
name|Exception
block|{
name|StagedPreChange
name|spc
init|=
name|stagePreChange
argument_list|(
literal|"refs/for/master%wip,notify=ALL"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|sender
argument_list|)
operator|.
name|sent
argument_list|(
literal|"newchange"
argument_list|,
name|spc
argument_list|)
operator|.
name|to
argument_list|(
name|spc
operator|.
name|watchingProjectOwner
argument_list|)
operator|.
name|bcc
argument_list|(
name|NEW_CHANGES
argument_list|,
name|NEW_PATCHSETS
argument_list|)
operator|.
name|noOneElse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|createReviewableChangeWithReviewersAndCcs ()
specifier|public
name|void
name|createReviewableChangeWithReviewersAndCcs
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO(logan): Support reviewers/CCs-by-email via push option.
name|StagedPreChange
name|spc
init|=
name|stagePreChange
argument_list|(
literal|"refs/for/master"
argument_list|,
name|users
lambda|->
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"r="
operator|+
name|users
operator|.
name|reviewer
operator|.
name|username
argument_list|,
literal|"cc="
operator|+
name|users
operator|.
name|ccer
operator|.
name|username
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|sender
argument_list|)
operator|.
name|sent
argument_list|(
literal|"newchange"
argument_list|,
name|spc
argument_list|)
operator|.
name|to
argument_list|(
name|spc
operator|.
name|reviewer
argument_list|,
name|spc
operator|.
name|watchingProjectOwner
argument_list|)
operator|.
name|cc
argument_list|(
name|spc
operator|.
name|ccer
argument_list|)
operator|.
name|bcc
argument_list|(
name|NEW_CHANGES
argument_list|,
name|NEW_PATCHSETS
argument_list|)
operator|.
name|noOneElse
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

