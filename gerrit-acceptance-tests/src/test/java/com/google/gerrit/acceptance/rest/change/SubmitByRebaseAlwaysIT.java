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
DECL|package|com.google.gerrit.acceptance.rest.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
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
name|truth
operator|.
name|Truth
operator|.
name|assertThat
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
name|acceptance
operator|.
name|TestProjectInput
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
name|client
operator|.
name|InheritableBoolean
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
name|client
operator|.
name|SubmitType
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|SubmitByRebaseAlwaysIT
specifier|public
class|class
name|SubmitByRebaseAlwaysIT
extends|extends
name|AbstractSubmitByRebase
block|{
annotation|@
name|Override
DECL|method|getSubmitType ()
specifier|protected
name|SubmitType
name|getSubmitType
parameter_list|()
block|{
return|return
name|SubmitType
operator|.
name|REBASE_ALWAYS
return|;
block|}
annotation|@
name|Test
annotation|@
name|TestProjectInput
argument_list|(
name|useContentMerge
operator|=
name|InheritableBoolean
operator|.
name|TRUE
argument_list|)
DECL|method|submitWithPossibleFastForward ()
specifier|public
name|void
name|submitWithPossibleFastForward
parameter_list|()
throws|throws
name|Exception
block|{
name|RevCommit
name|oldHead
init|=
name|getRemoteHead
argument_list|()
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change
init|=
name|createChange
argument_list|()
decl_stmt|;
name|submit
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|RevCommit
name|head
init|=
name|getRemoteHead
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|head
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|isNotEqualTo
argument_list|(
name|change
operator|.
name|getCommit
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|head
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|oldHead
argument_list|)
expr_stmt|;
name|assertApproved
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertCurrentRevision
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|2
argument_list|,
name|head
argument_list|)
expr_stmt|;
name|assertSubmitter
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertSubmitter
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertPersonEquals
argument_list|(
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|head
operator|.
name|getAuthorIdent
argument_list|()
argument_list|)
expr_stmt|;
name|assertPersonEquals
argument_list|(
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|head
operator|.
name|getCommitterIdent
argument_list|()
argument_list|)
expr_stmt|;
name|assertRefUpdatedEvents
argument_list|(
name|oldHead
argument_list|,
name|head
argument_list|)
expr_stmt|;
name|assertChangeMergedEvents
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|head
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

