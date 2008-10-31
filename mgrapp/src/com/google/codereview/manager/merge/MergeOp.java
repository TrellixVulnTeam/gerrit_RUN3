begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2008 Google Inc.
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
DECL|package|com.google.codereview.manager.merge
package|package
name|com
operator|.
name|google
operator|.
name|codereview
operator|.
name|manager
operator|.
name|merge
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|codereview
operator|.
name|internal
operator|.
name|PendingMerge
operator|.
name|PendingMergeItem
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|codereview
operator|.
name|internal
operator|.
name|PendingMerge
operator|.
name|PendingMergeResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|codereview
operator|.
name|internal
operator|.
name|PostMergeResult
operator|.
name|MergeResultItem
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|codereview
operator|.
name|internal
operator|.
name|PostMergeResult
operator|.
name|MissingDependencyItem
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|codereview
operator|.
name|internal
operator|.
name|PostMergeResult
operator|.
name|PostMergeResultRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|codereview
operator|.
name|manager
operator|.
name|Backend
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|codereview
operator|.
name|manager
operator|.
name|InvalidRepositoryException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|errors
operator|.
name|IncorrectObjectTypeException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|errors
operator|.
name|MissingObjectException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|AnyObjectId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Commit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
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
name|spearce
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
name|spearce
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
name|spearce
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
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|RefUpdate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
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
name|spearce
operator|.
name|jgit
operator|.
name|merge
operator|.
name|MergeStrategy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|merge
operator|.
name|Merger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
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
name|spearce
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|RevSort
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|RevWalk
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
name|ArrayList
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
comment|/**  * Merges changes in submission order into a single branch.  *<p>  * Branches are reduced to the minimum number of heads needed to merge  * everything. This allows commits to be entered into the queue in any order  * (such as ancestors before descendants) and only the most recent commit on any  * line of development will be merged. All unmerged commits along a line of  * development must be in the submission queue in order to merge the tip of that  * line.  *<p>  * Conflicts are handled by discarding the entire line of development and  * marking it as conflicting, even if an earlier commit along that same line can  * be merged cleanly.  */
end_comment

begin_class
DECL|class|MergeOp
class|class
name|MergeOp
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|MergeOp
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|mergePinName (final AnyObjectId id)
specifier|static
name|String
name|mergePinName
parameter_list|(
specifier|final
name|AnyObjectId
name|id
parameter_list|)
block|{
return|return
name|mergePinName
argument_list|(
name|id
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|mergePinName (final String idstr)
specifier|static
name|String
name|mergePinName
parameter_list|(
specifier|final
name|String
name|idstr
parameter_list|)
block|{
return|return
literal|"refs/merges/"
operator|+
name|idstr
return|;
block|}
DECL|field|server
specifier|private
specifier|final
name|Backend
name|server
decl_stmt|;
DECL|field|in
specifier|private
specifier|final
name|PendingMergeResponse
name|in
decl_stmt|;
DECL|field|mergeIdent
specifier|private
specifier|final
name|PersonIdent
name|mergeIdent
decl_stmt|;
DECL|field|updates
specifier|private
specifier|final
name|Collection
argument_list|<
name|MergeResultItem
argument_list|>
name|updates
decl_stmt|;
DECL|field|toMerge
specifier|private
specifier|final
name|List
argument_list|<
name|CodeReviewCommit
argument_list|>
name|toMerge
decl_stmt|;
DECL|field|db
specifier|private
name|Repository
name|db
decl_stmt|;
DECL|field|rw
specifier|private
name|RevWalk
name|rw
decl_stmt|;
DECL|field|branchTip
specifier|private
name|CodeReviewCommit
name|branchTip
decl_stmt|;
DECL|field|mergeTip
specifier|private
name|CodeReviewCommit
name|mergeTip
decl_stmt|;
DECL|field|newChanges
specifier|private
specifier|final
name|List
argument_list|<
name|CodeReviewCommit
argument_list|>
name|newChanges
decl_stmt|;
DECL|method|MergeOp (final Backend be, final PendingMergeResponse mergeInfo)
name|MergeOp
parameter_list|(
specifier|final
name|Backend
name|be
parameter_list|,
specifier|final
name|PendingMergeResponse
name|mergeInfo
parameter_list|)
block|{
name|server
operator|=
name|be
expr_stmt|;
name|in
operator|=
name|mergeInfo
expr_stmt|;
name|mergeIdent
operator|=
name|server
operator|.
name|newMergeIdentity
argument_list|()
expr_stmt|;
name|updates
operator|=
operator|new
name|ArrayList
argument_list|<
name|MergeResultItem
argument_list|>
argument_list|()
expr_stmt|;
name|toMerge
operator|=
operator|new
name|ArrayList
argument_list|<
name|CodeReviewCommit
argument_list|>
argument_list|()
expr_stmt|;
name|newChanges
operator|=
operator|new
name|ArrayList
argument_list|<
name|CodeReviewCommit
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|merge ()
name|PostMergeResultRequest
name|merge
parameter_list|()
block|{
specifier|final
name|String
name|loc
init|=
name|in
operator|.
name|getDestProjectName
argument_list|()
operator|+
literal|" "
operator|+
name|in
operator|.
name|getDestBranchName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Merging "
operator|+
name|loc
argument_list|)
expr_stmt|;
try|try
block|{
name|mergeImpl
argument_list|()
expr_stmt|;
specifier|final
name|PostMergeResultRequest
operator|.
name|Builder
name|update
decl_stmt|;
name|update
operator|=
name|PostMergeResultRequest
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
name|update
operator|.
name|setDestBranchKey
argument_list|(
name|in
operator|.
name|getDestBranchKey
argument_list|()
argument_list|)
expr_stmt|;
name|update
operator|.
name|addAllChange
argument_list|(
name|updates
argument_list|)
expr_stmt|;
return|return
name|update
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|MergeException
name|ee
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error merging "
operator|+
name|loc
argument_list|,
name|ee
argument_list|)
expr_stmt|;
name|mergeTip
operator|=
literal|null
expr_stmt|;
specifier|final
name|PostMergeResultRequest
operator|.
name|Builder
name|update
decl_stmt|;
name|update
operator|=
name|PostMergeResultRequest
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
name|update
operator|.
name|setDestBranchKey
argument_list|(
name|in
operator|.
name|getDestBranchKey
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|PendingMergeItem
name|pmi
range|:
name|in
operator|.
name|getChangeList
argument_list|()
control|)
block|{
name|update
operator|.
name|addChange
argument_list|(
name|suspend
argument_list|(
name|pmi
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|update
operator|.
name|build
argument_list|()
return|;
block|}
block|}
DECL|method|getMergeTip ()
name|CodeReviewCommit
name|getMergeTip
parameter_list|()
block|{
return|return
name|mergeTip
return|;
block|}
DECL|method|getNewChanges ()
name|Collection
argument_list|<
name|CodeReviewCommit
argument_list|>
name|getNewChanges
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableCollection
argument_list|(
name|newChanges
argument_list|)
return|;
block|}
DECL|method|mergeImpl ()
specifier|private
name|void
name|mergeImpl
parameter_list|()
throws|throws
name|MergeException
block|{
name|openRepository
argument_list|()
expr_stmt|;
name|openBranch
argument_list|()
expr_stmt|;
name|validateChangeList
argument_list|()
expr_stmt|;
name|reduceToMinimalMerge
argument_list|()
expr_stmt|;
name|mergeTopics
argument_list|()
expr_stmt|;
name|markCleanMerges
argument_list|()
expr_stmt|;
name|pinMergeCommit
argument_list|()
expr_stmt|;
block|}
DECL|method|openRepository ()
specifier|private
name|void
name|openRepository
parameter_list|()
throws|throws
name|MergeException
block|{
specifier|final
name|String
name|name
init|=
name|in
operator|.
name|getDestProjectName
argument_list|()
decl_stmt|;
try|try
block|{
name|db
operator|=
name|server
operator|.
name|getRepositoryCache
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidRepositoryException
name|notGit
parameter_list|)
block|{
specifier|final
name|String
name|m
init|=
literal|"Repository \""
operator|+
name|name
operator|+
literal|"\" unknown."
decl_stmt|;
throw|throw
operator|new
name|MergeException
argument_list|(
name|m
argument_list|,
name|notGit
argument_list|)
throw|;
block|}
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|db
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|RevCommit
name|createCommit
parameter_list|(
specifier|final
name|AnyObjectId
name|id
parameter_list|)
block|{
return|return
operator|new
name|CodeReviewCommit
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
DECL|method|openBranch ()
specifier|private
name|void
name|openBranch
parameter_list|()
throws|throws
name|MergeException
block|{
try|try
block|{
specifier|final
name|RefUpdate
name|ru
init|=
name|db
operator|.
name|updateRef
argument_list|(
name|in
operator|.
name|getDestBranchName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ru
operator|.
name|getOldObjectId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|branchTip
operator|=
operator|(
name|CodeReviewCommit
operator|)
name|rw
operator|.
name|parseCommit
argument_list|(
name|ru
operator|.
name|getOldObjectId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|branchTip
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MergeException
argument_list|(
literal|"Cannot open branch"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|validateChangeList ()
specifier|private
name|void
name|validateChangeList
parameter_list|()
throws|throws
name|MergeException
block|{
specifier|final
name|Set
argument_list|<
name|ObjectId
argument_list|>
name|tips
init|=
operator|new
name|HashSet
argument_list|<
name|ObjectId
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Ref
name|r
range|:
name|db
operator|.
name|getAllRefs
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|tips
operator|.
name|add
argument_list|(
name|r
operator|.
name|getObjectId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|commitOrder
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|PendingMergeItem
name|pmi
range|:
name|in
operator|.
name|getChangeList
argument_list|()
control|)
block|{
specifier|final
name|String
name|idstr
init|=
name|pmi
operator|.
name|getRevisionId
argument_list|()
decl_stmt|;
specifier|final
name|ObjectId
name|id
decl_stmt|;
try|try
block|{
name|id
operator|=
name|ObjectId
operator|.
name|fromString
argument_list|(
name|idstr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
throw|throw
operator|new
name|MergeException
argument_list|(
literal|"Invalid ObjectId: "
operator|+
name|idstr
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|tips
operator|.
name|contains
argument_list|(
name|id
argument_list|)
condition|)
block|{
comment|// TODO Technically the proper way to do this test is to use a
comment|// RevWalk on "$id --not --all" and test for an empty set. But
comment|// that is way slower than looking for a ref directly pointing
comment|// at the desired tip. We should always have a ref available.
comment|//
comment|// TODO this is actually an error, the branch is gone but we
comment|// want to merge the issue. We can't safely do that if the
comment|// tip is not reachable.
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot find branch head for "
operator|+
name|id
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|updates
operator|.
name|add
argument_list|(
name|suspend
argument_list|(
name|pmi
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
specifier|final
name|CodeReviewCommit
name|commit
decl_stmt|;
try|try
block|{
name|commit
operator|=
operator|(
name|CodeReviewCommit
operator|)
name|rw
operator|.
name|parseCommit
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MergeException
argument_list|(
literal|"Invalid issue commit "
operator|+
name|id
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|commit
operator|.
name|patchsetKey
operator|=
name|pmi
operator|.
name|getPatchsetKey
argument_list|()
expr_stmt|;
name|commit
operator|.
name|originalOrder
operator|=
name|commitOrder
operator|++
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Commit "
operator|+
name|commit
operator|.
name|name
argument_list|()
operator|+
literal|" is "
operator|+
name|commit
operator|.
name|patchsetKey
argument_list|)
expr_stmt|;
if|if
condition|(
name|branchTip
operator|!=
literal|null
condition|)
block|{
comment|// If this commit is already merged its a bug in the queuing code
comment|// that we got back here. Just mark it complete and move on. Its
comment|// merged and that is all that mattered to the requestor.
comment|//
try|try
block|{
if|if
condition|(
name|rw
operator|.
name|isMergedInto
argument_list|(
name|commit
argument_list|,
name|branchTip
argument_list|)
condition|)
block|{
name|commit
operator|.
name|statusCode
operator|=
name|MergeResultItem
operator|.
name|CodeType
operator|.
name|ALREADY_MERGED
expr_stmt|;
name|updates
operator|.
name|add
argument_list|(
name|toResult
argument_list|(
name|commit
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Already merged "
operator|+
name|commit
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|MergeException
argument_list|(
literal|"Cannot perform merge base test"
argument_list|,
name|err
argument_list|)
throw|;
block|}
block|}
name|toMerge
operator|.
name|add
argument_list|(
name|commit
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|reduceToMinimalMerge ()
specifier|private
name|void
name|reduceToMinimalMerge
parameter_list|()
throws|throws
name|MergeException
block|{
specifier|final
name|Collection
argument_list|<
name|CodeReviewCommit
argument_list|>
name|heads
decl_stmt|;
try|try
block|{
name|heads
operator|=
operator|new
name|MergeSorter
argument_list|(
name|rw
argument_list|,
name|branchTip
argument_list|)
operator|.
name|sort
argument_list|(
name|toMerge
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MergeException
argument_list|(
literal|"Branch head sorting failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
for|for
control|(
specifier|final
name|CodeReviewCommit
name|c
range|:
name|toMerge
control|)
block|{
if|if
condition|(
name|c
operator|.
name|statusCode
operator|!=
literal|null
condition|)
block|{
name|updates
operator|.
name|add
argument_list|(
name|toResult
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|toMerge
operator|.
name|clear
argument_list|()
expr_stmt|;
name|toMerge
operator|.
name|addAll
argument_list|(
name|heads
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|toMerge
argument_list|,
operator|new
name|Comparator
argument_list|<
name|CodeReviewCommit
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|CodeReviewCommit
name|a
parameter_list|,
specifier|final
name|CodeReviewCommit
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|originalOrder
operator|-
name|b
operator|.
name|originalOrder
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|mergeTopics ()
specifier|private
name|void
name|mergeTopics
parameter_list|()
throws|throws
name|MergeException
block|{
name|mergeTip
operator|=
name|branchTip
expr_stmt|;
comment|// Take the first fast-forward available, if any is available in the set.
comment|//
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|CodeReviewCommit
argument_list|>
name|i
init|=
name|toMerge
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
try|try
block|{
specifier|final
name|CodeReviewCommit
name|n
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|mergeTip
operator|==
literal|null
operator|||
name|rw
operator|.
name|isMergedInto
argument_list|(
name|mergeTip
argument_list|,
name|n
argument_list|)
condition|)
block|{
name|mergeTip
operator|=
name|n
expr_stmt|;
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Fast-forward to "
operator|+
name|n
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MergeException
argument_list|(
literal|"Cannot fast-forward test during merge"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// For every other commit do a pair-wise merge.
comment|//
while|while
condition|(
operator|!
name|toMerge
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|CodeReviewCommit
name|n
init|=
name|toMerge
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Merger
name|m
init|=
name|MergeStrategy
operator|.
name|SIMPLE_TWO_WAY_IN_CORE
operator|.
name|newMerger
argument_list|(
name|db
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|m
operator|.
name|merge
argument_list|(
operator|new
name|AnyObjectId
index|[]
block|{
name|mergeTip
operator|,
name|n
block|}
block|)
block|)
block|{
name|writeMergeCommit
argument_list|(
name|m
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Merged "
operator|+
name|n
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rw
operator|.
name|reset
argument_list|()
expr_stmt|;
name|rw
operator|.
name|markStart
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|rw
operator|.
name|markUninteresting
argument_list|(
name|mergeTip
argument_list|)
expr_stmt|;
name|CodeReviewCommit
name|failed
decl_stmt|;
while|while
condition|(
operator|(
name|failed
operator|=
operator|(
name|CodeReviewCommit
operator|)
name|rw
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|failed
operator|.
name|patchsetKey
operator|!=
literal|null
condition|)
block|{
name|failed
operator|.
name|statusCode
operator|=
name|MergeResultItem
operator|.
name|CodeType
operator|.
name|PATH_CONFLICT
expr_stmt|;
name|updates
operator|.
name|add
argument_list|(
name|toResult
argument_list|(
name|failed
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Rejected (path conflict) "
operator|+
name|n
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MergeException
argument_list|(
literal|"Cannot merge "
operator|+
name|n
operator|.
name|name
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
end_class

begin_function
unit|}    private
DECL|method|writeMergeCommit (final Merger m, final CodeReviewCommit n)
name|void
name|writeMergeCommit
parameter_list|(
specifier|final
name|Merger
name|m
parameter_list|,
specifier|final
name|CodeReviewCommit
name|n
parameter_list|)
throws|throws
name|IOException
throws|,
name|MissingObjectException
throws|,
name|IncorrectObjectTypeException
block|{
specifier|final
name|Commit
name|mergeCommit
init|=
operator|new
name|Commit
argument_list|(
name|db
argument_list|)
decl_stmt|;
name|mergeCommit
operator|.
name|setTreeId
argument_list|(
name|m
operator|.
name|getResultTreeId
argument_list|()
argument_list|)
expr_stmt|;
name|mergeCommit
operator|.
name|setParentIds
argument_list|(
operator|new
name|ObjectId
index|[]
block|{
name|mergeTip
block|,
name|n
block|}
argument_list|)
expr_stmt|;
name|mergeCommit
operator|.
name|setAuthor
argument_list|(
name|mergeIdent
argument_list|)
expr_stmt|;
name|mergeCommit
operator|.
name|setCommitter
argument_list|(
name|mergeCommit
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
name|mergeCommit
operator|.
name|setMessage
argument_list|(
literal|"Merge"
argument_list|)
expr_stmt|;
specifier|final
name|ObjectId
name|id
init|=
name|m
operator|.
name|getObjectWriter
argument_list|()
operator|.
name|writeCommit
argument_list|(
name|mergeCommit
argument_list|)
decl_stmt|;
name|mergeTip
operator|=
operator|(
name|CodeReviewCommit
operator|)
name|rw
operator|.
name|parseCommit
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|markCleanMerges ()
specifier|private
name|void
name|markCleanMerges
parameter_list|()
throws|throws
name|MergeException
block|{
try|try
block|{
name|rw
operator|.
name|reset
argument_list|()
expr_stmt|;
name|rw
operator|.
name|sort
argument_list|(
name|RevSort
operator|.
name|REVERSE
argument_list|)
expr_stmt|;
name|rw
operator|.
name|markStart
argument_list|(
name|mergeTip
argument_list|)
expr_stmt|;
if|if
condition|(
name|branchTip
operator|!=
literal|null
condition|)
block|{
name|rw
operator|.
name|markUninteresting
argument_list|(
name|branchTip
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
specifier|final
name|Ref
name|r
range|:
name|db
operator|.
name|getAllRefs
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|r
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|Constants
operator|.
name|R_HEADS
argument_list|)
operator|||
name|r
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|Constants
operator|.
name|R_TAGS
argument_list|)
condition|)
block|{
try|try
block|{
name|rw
operator|.
name|markUninteresting
argument_list|(
name|rw
operator|.
name|parseCommit
argument_list|(
name|r
operator|.
name|getObjectId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IncorrectObjectTypeException
name|iote
parameter_list|)
block|{
comment|// Not a commit? Skip over it.
block|}
block|}
block|}
block|}
name|CodeReviewCommit
name|c
decl_stmt|;
while|while
condition|(
operator|(
name|c
operator|=
operator|(
name|CodeReviewCommit
operator|)
name|rw
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|c
operator|.
name|patchsetKey
operator|!=
literal|null
condition|)
block|{
name|c
operator|.
name|statusCode
operator|=
name|MergeResultItem
operator|.
name|CodeType
operator|.
name|CLEAN_MERGE
expr_stmt|;
name|updates
operator|.
name|add
argument_list|(
name|toResult
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|newChanges
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MergeException
argument_list|(
literal|"Cannot mark clean merges"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
end_function

begin_function
DECL|method|pinMergeCommit ()
specifier|private
name|void
name|pinMergeCommit
parameter_list|()
throws|throws
name|MergeException
block|{
specifier|final
name|String
name|name
init|=
name|mergePinName
argument_list|(
name|mergeTip
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RefUpdate
operator|.
name|Result
name|r
decl_stmt|;
try|try
block|{
specifier|final
name|RefUpdate
name|u
init|=
name|db
operator|.
name|updateRef
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|u
operator|.
name|setNewObjectId
argument_list|(
name|mergeTip
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|u
operator|.
name|setRefLogMessage
argument_list|(
literal|"Merged submit queue"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|r
operator|=
name|u
operator|.
name|update
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|err
parameter_list|)
block|{
specifier|final
name|String
name|m
init|=
literal|"Failure creating "
operator|+
name|name
decl_stmt|;
throw|throw
operator|new
name|MergeException
argument_list|(
name|m
argument_list|,
name|err
argument_list|)
throw|;
block|}
if|if
condition|(
name|r
operator|==
name|RefUpdate
operator|.
name|Result
operator|.
name|NEW
condition|)
block|{     }
elseif|else
if|if
condition|(
name|r
operator|==
name|RefUpdate
operator|.
name|Result
operator|.
name|FAST_FORWARD
condition|)
block|{     }
elseif|else
if|if
condition|(
name|r
operator|==
name|RefUpdate
operator|.
name|Result
operator|.
name|FORCED
condition|)
block|{     }
elseif|else
if|if
condition|(
name|r
operator|==
name|RefUpdate
operator|.
name|Result
operator|.
name|NO_CHANGE
condition|)
block|{     }
else|else
block|{
specifier|final
name|String
name|m
init|=
literal|"Failure creating "
operator|+
name|name
operator|+
literal|": "
operator|+
name|r
operator|.
name|name
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|MergeException
argument_list|(
name|m
argument_list|)
throw|;
block|}
block|}
end_function

begin_function
DECL|method|suspend (final PendingMergeItem pmi)
specifier|private
specifier|static
name|MergeResultItem
name|suspend
parameter_list|(
specifier|final
name|PendingMergeItem
name|pmi
parameter_list|)
block|{
specifier|final
name|MergeResultItem
operator|.
name|Builder
name|delay
init|=
name|MergeResultItem
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|delay
operator|.
name|setStatusCode
argument_list|(
name|MergeResultItem
operator|.
name|CodeType
operator|.
name|MISSING_DEPENDENCY
argument_list|)
expr_stmt|;
name|delay
operator|.
name|setPatchsetKey
argument_list|(
name|pmi
operator|.
name|getPatchsetKey
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|delay
operator|.
name|build
argument_list|()
return|;
block|}
end_function

begin_function
DECL|method|toResult (final CodeReviewCommit c)
specifier|private
specifier|static
name|MergeResultItem
name|toResult
parameter_list|(
specifier|final
name|CodeReviewCommit
name|c
parameter_list|)
block|{
specifier|final
name|MergeResultItem
operator|.
name|Builder
name|delay
init|=
name|MergeResultItem
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|delay
operator|.
name|setStatusCode
argument_list|(
name|c
operator|.
name|statusCode
argument_list|)
expr_stmt|;
name|delay
operator|.
name|setPatchsetKey
argument_list|(
name|c
operator|.
name|patchsetKey
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|.
name|statusCode
operator|==
name|MergeResultItem
operator|.
name|CodeType
operator|.
name|MISSING_DEPENDENCY
condition|)
block|{
for|for
control|(
specifier|final
name|CodeReviewCommit
name|m
range|:
name|c
operator|.
name|missing
control|)
block|{
specifier|final
name|MissingDependencyItem
operator|.
name|Builder
name|d
decl_stmt|;
name|d
operator|=
name|MissingDependencyItem
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|m
operator|.
name|patchsetKey
operator|!=
literal|null
condition|)
block|{
name|d
operator|.
name|setPatchsetKey
argument_list|(
name|m
operator|.
name|patchsetKey
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|d
operator|.
name|setRevisionId
argument_list|(
name|m
operator|.
name|getId
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|delay
operator|.
name|addMissing
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|delay
operator|.
name|build
argument_list|()
return|;
block|}
end_function

unit|}
end_unit

