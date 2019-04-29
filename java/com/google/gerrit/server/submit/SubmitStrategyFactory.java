begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.submit
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|submit
package|;
end_package

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
name|extensions
operator|.
name|api
operator|.
name|changes
operator|.
name|SubmitInput
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
name|CodeReviewCommit
operator|.
name|CodeReviewRevWalk
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
name|MergeTip
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
name|logging
operator|.
name|RequestId
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
name|submit
operator|.
name|MergeOp
operator|.
name|CommitStatus
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
name|Singleton
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
name|RevFlag
import|;
end_import

begin_comment
comment|/** Factory to create a {@link SubmitStrategy} for a {@link SubmitType}. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|SubmitStrategyFactory
specifier|public
class|class
name|SubmitStrategyFactory
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
DECL|field|argsFactory
specifier|private
specifier|final
name|SubmitStrategy
operator|.
name|Arguments
operator|.
name|Factory
name|argsFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|SubmitStrategyFactory (SubmitStrategy.Arguments.Factory argsFactory)
name|SubmitStrategyFactory
parameter_list|(
name|SubmitStrategy
operator|.
name|Arguments
operator|.
name|Factory
name|argsFactory
parameter_list|)
block|{
name|this
operator|.
name|argsFactory
operator|=
name|argsFactory
expr_stmt|;
block|}
DECL|method|create ( SubmitType submitType, CodeReviewRevWalk rw, RevFlag canMergeFlag, Set<RevCommit> alreadyAccepted, Set<CodeReviewCommit> incoming, BranchNameKey destBranch, IdentifiedUser caller, MergeTip mergeTip, CommitStatus commitStatus, RequestId submissionId, SubmitInput submitInput, SubmoduleOp submoduleOp, boolean dryrun)
specifier|public
name|SubmitStrategy
name|create
parameter_list|(
name|SubmitType
name|submitType
parameter_list|,
name|CodeReviewRevWalk
name|rw
parameter_list|,
name|RevFlag
name|canMergeFlag
parameter_list|,
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|alreadyAccepted
parameter_list|,
name|Set
argument_list|<
name|CodeReviewCommit
argument_list|>
name|incoming
parameter_list|,
name|BranchNameKey
name|destBranch
parameter_list|,
name|IdentifiedUser
name|caller
parameter_list|,
name|MergeTip
name|mergeTip
parameter_list|,
name|CommitStatus
name|commitStatus
parameter_list|,
name|RequestId
name|submissionId
parameter_list|,
name|SubmitInput
name|submitInput
parameter_list|,
name|SubmoduleOp
name|submoduleOp
parameter_list|,
name|boolean
name|dryrun
parameter_list|)
throws|throws
name|IntegrationException
block|{
name|SubmitStrategy
operator|.
name|Arguments
name|args
init|=
name|argsFactory
operator|.
name|create
argument_list|(
name|submitType
argument_list|,
name|destBranch
argument_list|,
name|commitStatus
argument_list|,
name|rw
argument_list|,
name|caller
argument_list|,
name|mergeTip
argument_list|,
name|canMergeFlag
argument_list|,
name|alreadyAccepted
argument_list|,
name|incoming
argument_list|,
name|submissionId
argument_list|,
name|submitInput
argument_list|,
name|submoduleOp
argument_list|,
name|dryrun
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|submitType
condition|)
block|{
case|case
name|CHERRY_PICK
case|:
return|return
operator|new
name|CherryPick
argument_list|(
name|args
argument_list|)
return|;
case|case
name|FAST_FORWARD_ONLY
case|:
return|return
operator|new
name|FastForwardOnly
argument_list|(
name|args
argument_list|)
return|;
case|case
name|MERGE_ALWAYS
case|:
return|return
operator|new
name|MergeAlways
argument_list|(
name|args
argument_list|)
return|;
case|case
name|MERGE_IF_NECESSARY
case|:
return|return
operator|new
name|MergeIfNecessary
argument_list|(
name|args
argument_list|)
return|;
case|case
name|REBASE_IF_NECESSARY
case|:
return|return
operator|new
name|RebaseIfNecessary
argument_list|(
name|args
argument_list|)
return|;
case|case
name|REBASE_ALWAYS
case|:
return|return
operator|new
name|RebaseAlways
argument_list|(
name|args
argument_list|)
return|;
case|case
name|INHERIT
case|:
default|default:
name|String
name|errorMsg
init|=
literal|"No submit strategy for: "
operator|+
name|submitType
decl_stmt|;
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|log
argument_list|(
name|errorMsg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IntegrationException
argument_list|(
name|errorMsg
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

