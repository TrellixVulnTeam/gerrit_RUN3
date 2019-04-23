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
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toSet
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
name|ImmutableSet
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
name|Streams
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
name|common
operator|.
name|Nullable
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
name|server
operator|.
name|CurrentUser
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
name|gerrit
operator|.
name|server
operator|.
name|query
operator|.
name|change
operator|.
name|InternalChangeQuery
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
name|Collection
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
name|Objects
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
name|lib
operator|.
name|Constants
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Ref
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
name|eclipse
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|RevObject
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
name|RevTag
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
name|RevWalk
import|;
end_import

begin_comment
comment|/** Dry run of a submit strategy. */
end_comment

begin_class
DECL|class|SubmitDryRun
specifier|public
class|class
name|SubmitDryRun
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
DECL|class|Arguments
specifier|static
class|class
name|Arguments
block|{
DECL|field|repo
specifier|final
name|Repository
name|repo
decl_stmt|;
DECL|field|rw
specifier|final
name|CodeReviewRevWalk
name|rw
decl_stmt|;
DECL|field|mergeUtil
specifier|final
name|MergeUtil
name|mergeUtil
decl_stmt|;
DECL|field|mergeSorter
specifier|final
name|MergeSorter
name|mergeSorter
decl_stmt|;
DECL|method|Arguments (Repository repo, CodeReviewRevWalk rw, MergeUtil mergeUtil, MergeSorter mergeSorter)
name|Arguments
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|CodeReviewRevWalk
name|rw
parameter_list|,
name|MergeUtil
name|mergeUtil
parameter_list|,
name|MergeSorter
name|mergeSorter
parameter_list|)
block|{
name|this
operator|.
name|repo
operator|=
name|repo
expr_stmt|;
name|this
operator|.
name|rw
operator|=
name|rw
expr_stmt|;
name|this
operator|.
name|mergeUtil
operator|=
name|mergeUtil
expr_stmt|;
name|this
operator|.
name|mergeSorter
operator|=
name|mergeSorter
expr_stmt|;
block|}
block|}
DECL|method|getAlreadyAccepted (Repository repo)
specifier|public
specifier|static
name|Set
argument_list|<
name|ObjectId
argument_list|>
name|getAlreadyAccepted
parameter_list|(
name|Repository
name|repo
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Streams
operator|.
name|concat
argument_list|(
name|repo
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|getRefsByPrefix
argument_list|(
name|Constants
operator|.
name|R_HEADS
argument_list|)
operator|.
name|stream
argument_list|()
argument_list|,
name|repo
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|getRefsByPrefix
argument_list|(
name|Constants
operator|.
name|R_TAGS
argument_list|)
operator|.
name|stream
argument_list|()
argument_list|)
operator|.
name|map
argument_list|(
name|Ref
operator|::
name|getObjectId
argument_list|)
operator|.
name|filter
argument_list|(
name|Objects
operator|::
name|nonNull
argument_list|)
operator|.
name|collect
argument_list|(
name|toSet
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getAlreadyAccepted (Repository repo, RevWalk rw)
specifier|public
specifier|static
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|getAlreadyAccepted
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|RevWalk
name|rw
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|accepted
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|addCommits
argument_list|(
name|getAlreadyAccepted
argument_list|(
name|repo
argument_list|)
argument_list|,
name|rw
argument_list|,
name|accepted
argument_list|)
expr_stmt|;
return|return
name|accepted
return|;
block|}
DECL|method|addCommits (Iterable<ObjectId> ids, RevWalk rw, Collection<RevCommit> out)
specifier|public
specifier|static
name|void
name|addCommits
parameter_list|(
name|Iterable
argument_list|<
name|ObjectId
argument_list|>
name|ids
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|Collection
argument_list|<
name|RevCommit
argument_list|>
name|out
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|ObjectId
name|id
range|:
name|ids
control|)
block|{
name|RevObject
name|obj
init|=
name|rw
operator|.
name|parseAny
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|RevTag
condition|)
block|{
name|obj
operator|=
name|rw
operator|.
name|peel
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|obj
operator|instanceof
name|RevCommit
condition|)
block|{
name|out
operator|.
name|add
argument_list|(
operator|(
name|RevCommit
operator|)
name|obj
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|mergeUtilFactory
specifier|private
specifier|final
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
decl_stmt|;
DECL|field|queryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
decl_stmt|;
annotation|@
name|Inject
DECL|method|SubmitDryRun ( ProjectCache projectCache, MergeUtil.Factory mergeUtilFactory, Provider<InternalChangeQuery> queryProvider)
name|SubmitDryRun
parameter_list|(
name|ProjectCache
name|projectCache
parameter_list|,
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
parameter_list|,
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|)
block|{
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|mergeUtilFactory
operator|=
name|mergeUtilFactory
expr_stmt|;
name|this
operator|.
name|queryProvider
operator|=
name|queryProvider
expr_stmt|;
block|}
DECL|method|run ( @ullable CurrentUser caller, SubmitType submitType, Repository repo, CodeReviewRevWalk rw, Branch.NameKey destBranch, ObjectId tip, ObjectId toMerge, Set<RevCommit> alreadyAccepted)
specifier|public
name|boolean
name|run
parameter_list|(
annotation|@
name|Nullable
name|CurrentUser
name|caller
parameter_list|,
name|SubmitType
name|submitType
parameter_list|,
name|Repository
name|repo
parameter_list|,
name|CodeReviewRevWalk
name|rw
parameter_list|,
name|Branch
operator|.
name|NameKey
name|destBranch
parameter_list|,
name|ObjectId
name|tip
parameter_list|,
name|ObjectId
name|toMerge
parameter_list|,
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|alreadyAccepted
parameter_list|)
throws|throws
name|IntegrationException
throws|,
name|NoSuchProjectException
throws|,
name|IOException
block|{
name|CodeReviewCommit
name|tipCommit
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|tip
argument_list|)
decl_stmt|;
name|CodeReviewCommit
name|toMergeCommit
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|toMerge
argument_list|)
decl_stmt|;
name|RevFlag
name|canMerge
init|=
name|rw
operator|.
name|newFlag
argument_list|(
literal|"CAN_MERGE"
argument_list|)
decl_stmt|;
name|toMergeCommit
operator|.
name|add
argument_list|(
name|canMerge
argument_list|)
expr_stmt|;
name|Arguments
name|args
init|=
operator|new
name|Arguments
argument_list|(
name|repo
argument_list|,
name|rw
argument_list|,
name|mergeUtilFactory
operator|.
name|create
argument_list|(
name|getProject
argument_list|(
name|destBranch
argument_list|)
argument_list|)
argument_list|,
operator|new
name|MergeSorter
argument_list|(
name|caller
argument_list|,
name|rw
argument_list|,
name|alreadyAccepted
argument_list|,
name|canMerge
argument_list|,
name|queryProvider
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|toMergeCommit
argument_list|)
argument_list|)
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
name|CherryPick
operator|.
name|dryRun
argument_list|(
name|args
argument_list|,
name|tipCommit
argument_list|,
name|toMergeCommit
argument_list|)
return|;
case|case
name|FAST_FORWARD_ONLY
case|:
return|return
name|FastForwardOnly
operator|.
name|dryRun
argument_list|(
name|args
argument_list|,
name|tipCommit
argument_list|,
name|toMergeCommit
argument_list|)
return|;
case|case
name|MERGE_ALWAYS
case|:
return|return
name|MergeAlways
operator|.
name|dryRun
argument_list|(
name|args
argument_list|,
name|tipCommit
argument_list|,
name|toMergeCommit
argument_list|)
return|;
case|case
name|MERGE_IF_NECESSARY
case|:
return|return
name|MergeIfNecessary
operator|.
name|dryRun
argument_list|(
name|args
argument_list|,
name|tipCommit
argument_list|,
name|toMergeCommit
argument_list|)
return|;
case|case
name|REBASE_IF_NECESSARY
case|:
return|return
name|RebaseIfNecessary
operator|.
name|dryRun
argument_list|(
name|args
argument_list|,
name|repo
argument_list|,
name|tipCommit
argument_list|,
name|toMergeCommit
argument_list|)
return|;
case|case
name|REBASE_ALWAYS
case|:
return|return
name|RebaseAlways
operator|.
name|dryRun
argument_list|(
name|args
argument_list|,
name|repo
argument_list|,
name|tipCommit
argument_list|,
name|toMergeCommit
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
name|ProjectState
name|p
init|=
name|projectCache
operator|.
name|get
argument_list|(
name|branch
operator|.
name|project
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
name|project
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

