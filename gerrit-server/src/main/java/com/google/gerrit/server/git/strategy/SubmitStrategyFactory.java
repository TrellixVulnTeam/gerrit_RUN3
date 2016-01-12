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
name|Branch
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
name|IntegrationException
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectInserter
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
name|RevFlag
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
name|Set
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
name|SubmitStrategyFactory
operator|.
name|class
argument_list|)
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
DECL|method|create (SubmitType submitType, ReviewDb db, Repository repo, CodeReviewRevWalk rw, ObjectInserter inserter, RevFlag canMergeFlag, Set<RevCommit> alreadyAccepted, Branch.NameKey destBranch, IdentifiedUser caller, CommitStatus commits)
specifier|public
name|SubmitStrategy
name|create
parameter_list|(
name|SubmitType
name|submitType
parameter_list|,
name|ReviewDb
name|db
parameter_list|,
name|Repository
name|repo
parameter_list|,
name|CodeReviewRevWalk
name|rw
parameter_list|,
name|ObjectInserter
name|inserter
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
name|Branch
operator|.
name|NameKey
name|destBranch
parameter_list|,
name|IdentifiedUser
name|caller
parameter_list|,
name|CommitStatus
name|commits
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
name|destBranch
argument_list|,
name|commits
argument_list|,
name|rw
argument_list|,
name|caller
argument_list|,
name|inserter
argument_list|,
name|repo
argument_list|,
name|canMergeFlag
argument_list|,
name|db
argument_list|,
name|alreadyAccepted
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
default|default:
name|String
name|errorMsg
init|=
literal|"No submit strategy for: "
operator|+
name|submitType
decl_stmt|;
name|log
operator|.
name|error
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

