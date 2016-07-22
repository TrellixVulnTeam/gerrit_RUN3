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
DECL|package|com.google.gerrit.server.git.strategy
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
name|strategy
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
name|server
operator|.
name|git
operator|.
name|BatchUpdate
operator|.
name|RepoContext
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
name|CodeReviewCommit
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
name|IntegrationException
import|;
end_import

begin_class
DECL|class|FastForwardOp
class|class
name|FastForwardOp
extends|extends
name|SubmitStrategyOp
block|{
DECL|method|FastForwardOp (SubmitStrategy.Arguments args, CodeReviewCommit toMerge)
name|FastForwardOp
parameter_list|(
name|SubmitStrategy
operator|.
name|Arguments
name|args
parameter_list|,
name|CodeReviewCommit
name|toMerge
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|,
name|toMerge
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateRepoImpl (RepoContext ctx)
specifier|protected
name|void
name|updateRepoImpl
parameter_list|(
name|RepoContext
name|ctx
parameter_list|)
throws|throws
name|IntegrationException
block|{
name|args
operator|.
name|mergeTip
operator|.
name|moveTipTo
argument_list|(
name|amendGitlink
argument_list|(
name|toMerge
argument_list|)
argument_list|,
name|toMerge
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

