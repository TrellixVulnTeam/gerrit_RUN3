begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|git
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
name|acceptance
operator|.
name|GitUtil
operator|.
name|pushHead
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
name|reviewdb
operator|.
name|client
operator|.
name|BooleanProjectConfig
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|ImplicitMergeCheckIT
specifier|public
class|class
name|ImplicitMergeCheckIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
DECL|method|implicitMergeViaFastForward ()
specifier|public
name|void
name|implicitMergeViaFastForward
parameter_list|()
throws|throws
name|Exception
block|{
name|setRejectImplicitMerges
argument_list|()
expr_stmt|;
name|pushHead
argument_list|(
name|testRepo
argument_list|,
literal|"refs/heads/stable"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|m
init|=
name|push
argument_list|(
literal|"refs/heads/master"
argument_list|,
literal|"0"
argument_list|,
literal|"file"
argument_list|,
literal|"0"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|c
init|=
name|push
argument_list|(
literal|"refs/for/stable"
argument_list|,
literal|"1"
argument_list|,
literal|"file"
argument_list|,
literal|"1"
argument_list|)
decl_stmt|;
name|c
operator|.
name|assertMessage
argument_list|(
name|implicitMergeOf
argument_list|(
name|m
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|assertErrorStatus
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|implicitMergeViaRealMerge ()
specifier|public
name|void
name|implicitMergeViaRealMerge
parameter_list|()
throws|throws
name|Exception
block|{
name|setRejectImplicitMerges
argument_list|()
expr_stmt|;
name|ObjectId
name|base
init|=
name|repo
argument_list|()
operator|.
name|exactRef
argument_list|(
literal|"HEAD"
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|push
argument_list|(
literal|"refs/heads/stable"
argument_list|,
literal|"0"
argument_list|,
literal|"f"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|testRepo
operator|.
name|reset
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|m
init|=
name|push
argument_list|(
literal|"refs/heads/master"
argument_list|,
literal|"1"
argument_list|,
literal|"f"
argument_list|,
literal|"1"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|c
init|=
name|push
argument_list|(
literal|"refs/for/stable"
argument_list|,
literal|"2"
argument_list|,
literal|"f"
argument_list|,
literal|"2"
argument_list|)
decl_stmt|;
name|c
operator|.
name|assertMessage
argument_list|(
name|implicitMergeOf
argument_list|(
name|m
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|assertErrorStatus
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|implicitMergeCheckOff ()
specifier|public
name|void
name|implicitMergeCheckOff
parameter_list|()
throws|throws
name|Exception
block|{
name|ObjectId
name|base
init|=
name|repo
argument_list|()
operator|.
name|exactRef
argument_list|(
literal|"HEAD"
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|push
argument_list|(
literal|"refs/heads/stable"
argument_list|,
literal|"0"
argument_list|,
literal|"f"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|testRepo
operator|.
name|reset
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|m
init|=
name|push
argument_list|(
literal|"refs/heads/master"
argument_list|,
literal|"1"
argument_list|,
literal|"f"
argument_list|,
literal|"1"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|c
init|=
name|push
argument_list|(
literal|"refs/for/stable"
argument_list|,
literal|"2"
argument_list|,
literal|"f"
argument_list|,
literal|"2"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getMessage
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
operator|.
name|doesNotContain
argument_list|(
name|implicitMergeOf
argument_list|(
name|m
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|notImplicitMerge_noWarning ()
specifier|public
name|void
name|notImplicitMerge_noWarning
parameter_list|()
throws|throws
name|Exception
block|{
name|setRejectImplicitMerges
argument_list|()
expr_stmt|;
name|ObjectId
name|base
init|=
name|repo
argument_list|()
operator|.
name|exactRef
argument_list|(
literal|"HEAD"
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|push
argument_list|(
literal|"refs/heads/stable"
argument_list|,
literal|"0"
argument_list|,
literal|"f"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|testRepo
operator|.
name|reset
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|m
init|=
name|push
argument_list|(
literal|"refs/heads/master"
argument_list|,
literal|"1"
argument_list|,
literal|"f"
argument_list|,
literal|"1"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|c
init|=
name|push
argument_list|(
literal|"refs/for/master"
argument_list|,
literal|"2"
argument_list|,
literal|"f"
argument_list|,
literal|"2"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getMessage
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
operator|.
name|doesNotContain
argument_list|(
name|implicitMergeOf
argument_list|(
name|m
operator|.
name|getCommit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|implicitMergeOf (ObjectId commit)
specifier|private
specifier|static
name|String
name|implicitMergeOf
parameter_list|(
name|ObjectId
name|commit
parameter_list|)
block|{
return|return
literal|"implicit merge of "
operator|+
name|commit
operator|.
name|abbreviate
argument_list|(
literal|7
argument_list|)
operator|.
name|name
argument_list|()
return|;
block|}
DECL|method|setRejectImplicitMerges ()
specifier|private
name|void
name|setRejectImplicitMerges
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ProjectConfigUpdate
name|u
init|=
name|updateProject
argument_list|(
name|project
argument_list|)
init|)
block|{
name|u
operator|.
name|getConfig
argument_list|()
operator|.
name|getProject
argument_list|()
operator|.
name|setBooleanConfig
argument_list|(
name|BooleanProjectConfig
operator|.
name|REJECT_IMPLICIT_MERGES
argument_list|,
name|InheritableBoolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|u
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|push (String ref, String subject, String fileName, String content)
specifier|private
name|PushOneCommit
operator|.
name|Result
name|push
parameter_list|(
name|String
name|ref
parameter_list|,
name|String
name|subject
parameter_list|,
name|String
name|fileName
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|Exception
block|{
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|admin
operator|.
name|newIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|,
name|subject
argument_list|,
name|fileName
argument_list|,
name|content
argument_list|)
decl_stmt|;
return|return
name|push
operator|.
name|to
argument_list|(
name|ref
argument_list|)
return|;
block|}
block|}
end_class

end_unit

