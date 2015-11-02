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
name|ApprovalsUtil
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
name|GerritPersonIdent
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
name|change
operator|.
name|RebaseChangeOp
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
name|BatchUpdate
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
name|MergeUtil
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
name|index
operator|.
name|ChangeIndexer
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
name|PatchSetInfoFactory
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
name|NoSuchProjectException
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
name|ProjectCache
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
name|ProjectState
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
name|Provider
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
name|PersonIdent
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
DECL|field|identifiedUserFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|identifiedUserFactory
decl_stmt|;
DECL|field|myIdent
specifier|private
specifier|final
name|Provider
argument_list|<
name|PersonIdent
argument_list|>
name|myIdent
decl_stmt|;
DECL|field|batchUpdateFactory
specifier|private
specifier|final
name|BatchUpdate
operator|.
name|Factory
name|batchUpdateFactory
decl_stmt|;
DECL|field|changeControlFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|GenericFactory
name|changeControlFactory
decl_stmt|;
DECL|field|patchSetInfoFactory
specifier|private
specifier|final
name|PatchSetInfoFactory
name|patchSetInfoFactory
decl_stmt|;
DECL|field|rebaseFactory
specifier|private
specifier|final
name|RebaseChangeOp
operator|.
name|Factory
name|rebaseFactory
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|approvalsUtil
specifier|private
specifier|final
name|ApprovalsUtil
name|approvalsUtil
decl_stmt|;
DECL|field|mergeUtilFactory
specifier|private
specifier|final
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
decl_stmt|;
DECL|field|indexer
specifier|private
specifier|final
name|ChangeIndexer
name|indexer
decl_stmt|;
annotation|@
name|Inject
DECL|method|SubmitStrategyFactory ( final IdentifiedUser.GenericFactory identifiedUserFactory, @GerritPersonIdent Provider<PersonIdent> myIdent, final BatchUpdate.Factory batchUpdateFactory, final ChangeControl.GenericFactory changeControlFactory, final PatchSetInfoFactory patchSetInfoFactory, final RebaseChangeOp.Factory rebaseFactory, final ProjectCache projectCache, final ApprovalsUtil approvalsUtil, final MergeUtil.Factory mergeUtilFactory, final ChangeIndexer indexer)
name|SubmitStrategyFactory
parameter_list|(
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|identifiedUserFactory
parameter_list|,
annotation|@
name|GerritPersonIdent
name|Provider
argument_list|<
name|PersonIdent
argument_list|>
name|myIdent
parameter_list|,
specifier|final
name|BatchUpdate
operator|.
name|Factory
name|batchUpdateFactory
parameter_list|,
specifier|final
name|ChangeControl
operator|.
name|GenericFactory
name|changeControlFactory
parameter_list|,
specifier|final
name|PatchSetInfoFactory
name|patchSetInfoFactory
parameter_list|,
specifier|final
name|RebaseChangeOp
operator|.
name|Factory
name|rebaseFactory
parameter_list|,
specifier|final
name|ProjectCache
name|projectCache
parameter_list|,
specifier|final
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
specifier|final
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
parameter_list|,
specifier|final
name|ChangeIndexer
name|indexer
parameter_list|)
block|{
name|this
operator|.
name|identifiedUserFactory
operator|=
name|identifiedUserFactory
expr_stmt|;
name|this
operator|.
name|myIdent
operator|=
name|myIdent
expr_stmt|;
name|this
operator|.
name|batchUpdateFactory
operator|=
name|batchUpdateFactory
expr_stmt|;
name|this
operator|.
name|changeControlFactory
operator|=
name|changeControlFactory
expr_stmt|;
name|this
operator|.
name|patchSetInfoFactory
operator|=
name|patchSetInfoFactory
expr_stmt|;
name|this
operator|.
name|rebaseFactory
operator|=
name|rebaseFactory
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|approvalsUtil
operator|=
name|approvalsUtil
expr_stmt|;
name|this
operator|.
name|mergeUtilFactory
operator|=
name|mergeUtilFactory
expr_stmt|;
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
block|}
DECL|method|create (SubmitType submitType, ReviewDb db, Repository repo, CodeReviewRevWalk rw, ObjectInserter inserter, RevFlag canMergeFlag, Set<RevCommit> alreadyAccepted, Branch.NameKey destBranch, IdentifiedUser caller)
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
parameter_list|)
throws|throws
name|IntegrationException
throws|,
name|NoSuchProjectException
block|{
name|ProjectState
name|project
init|=
name|getProject
argument_list|(
name|destBranch
argument_list|)
decl_stmt|;
name|SubmitStrategy
operator|.
name|Arguments
name|args
init|=
operator|new
name|SubmitStrategy
operator|.
name|Arguments
argument_list|(
name|identifiedUserFactory
argument_list|,
name|myIdent
argument_list|,
name|db
argument_list|,
name|batchUpdateFactory
argument_list|,
name|changeControlFactory
argument_list|,
name|repo
argument_list|,
name|rw
argument_list|,
name|inserter
argument_list|,
name|canMergeFlag
argument_list|,
name|alreadyAccepted
argument_list|,
name|destBranch
argument_list|,
name|approvalsUtil
argument_list|,
name|mergeUtilFactory
operator|.
name|create
argument_list|(
name|project
argument_list|)
argument_list|,
name|indexer
argument_list|,
name|caller
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
argument_list|,
name|patchSetInfoFactory
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
argument_list|,
name|patchSetInfoFactory
argument_list|,
name|rebaseFactory
argument_list|)
return|;
default|default:
specifier|final
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
DECL|method|getProject (Branch.NameKey branch)
specifier|private
name|ProjectState
name|getProject
parameter_list|(
name|Branch
operator|.
name|NameKey
name|branch
parameter_list|)
throws|throws
name|NoSuchProjectException
block|{
specifier|final
name|ProjectState
name|p
init|=
name|projectCache
operator|.
name|get
argument_list|(
name|branch
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchProjectException
argument_list|(
name|branch
operator|.
name|getParentKey
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|p
return|;
block|}
block|}
end_class

end_unit

