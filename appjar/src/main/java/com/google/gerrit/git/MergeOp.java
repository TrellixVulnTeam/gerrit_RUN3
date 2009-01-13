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
DECL|package|com.google.gerrit.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|git
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
name|client
operator|.
name|reviewdb
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
name|client
operator|.
name|reviewdb
operator|.
name|Change
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
name|client
operator|.
name|reviewdb
operator|.
name|ChangeMessage
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
name|client
operator|.
name|reviewdb
operator|.
name|PatchSet
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
name|client
operator|.
name|reviewdb
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
name|client
operator|.
name|rpc
operator|.
name|Common
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
name|GerritServer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|OrmException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Transaction
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
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|util
operator|.
name|Base64
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
name|util
operator|.
name|NB
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
name|HashMap
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
name|Map
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
specifier|public
class|class
name|MergeOp
block|{
DECL|field|server
specifier|private
specifier|final
name|GerritServer
name|server
decl_stmt|;
DECL|field|myIdent
specifier|private
specifier|final
name|PersonIdent
name|myIdent
decl_stmt|;
DECL|field|destBranch
specifier|private
specifier|final
name|Branch
operator|.
name|NameKey
name|destBranch
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
DECL|field|submitted
specifier|private
name|List
argument_list|<
name|Change
argument_list|>
name|submitted
decl_stmt|;
DECL|field|status
specifier|private
specifier|final
name|Map
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|CommitMergeStatus
argument_list|>
name|status
decl_stmt|;
DECL|field|schema
specifier|private
name|ReviewDb
name|schema
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
DECL|field|branchUpdate
specifier|private
name|RefUpdate
name|branchUpdate
decl_stmt|;
DECL|method|MergeOp (final GerritServer gs, final Branch.NameKey branch)
specifier|public
name|MergeOp
parameter_list|(
specifier|final
name|GerritServer
name|gs
parameter_list|,
specifier|final
name|Branch
operator|.
name|NameKey
name|branch
parameter_list|)
block|{
name|server
operator|=
name|gs
expr_stmt|;
name|myIdent
operator|=
name|server
operator|.
name|newGerritPersonIdent
argument_list|()
expr_stmt|;
name|destBranch
operator|=
name|branch
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
name|status
operator|=
operator|new
name|HashMap
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|CommitMergeStatus
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|merge ()
specifier|public
name|void
name|merge
parameter_list|()
throws|throws
name|MergeException
block|{
try|try
block|{
name|schema
operator|=
name|Common
operator|.
name|getSchemaFactory
argument_list|()
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MergeException
argument_list|(
literal|"Cannot open database"
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|mergeImpl
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|schema
operator|.
name|close
argument_list|()
expr_stmt|;
name|schema
operator|=
literal|null
expr_stmt|;
block|}
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
name|listPendingSubmits
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
name|updateBranch
argument_list|()
expr_stmt|;
name|updateChangeStatus
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
name|destBranch
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
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
name|branchUpdate
operator|=
name|db
operator|.
name|updateRef
argument_list|(
name|destBranch
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|branchUpdate
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
name|branchUpdate
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
DECL|method|listPendingSubmits ()
specifier|private
name|void
name|listPendingSubmits
parameter_list|()
throws|throws
name|MergeException
block|{
try|try
block|{
name|submitted
operator|=
name|schema
operator|.
name|changes
argument_list|()
operator|.
name|submitted
argument_list|(
name|destBranch
argument_list|)
operator|.
name|toList
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MergeException
argument_list|(
literal|"Cannot query the database"
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
name|Change
name|chg
range|:
name|submitted
control|)
block|{
if|if
condition|(
name|chg
operator|.
name|currentPatchSetId
argument_list|()
operator|==
literal|null
condition|)
block|{
name|status
operator|.
name|put
argument_list|(
name|chg
operator|.
name|getId
argument_list|()
argument_list|,
name|CommitMergeStatus
operator|.
name|NO_PATCH_SET
argument_list|)
expr_stmt|;
continue|continue;
block|}
specifier|final
name|PatchSet
name|ps
decl_stmt|;
try|try
block|{
name|ps
operator|=
name|schema
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|chg
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MergeException
argument_list|(
literal|"Cannot query the database"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|ps
operator|==
literal|null
operator|||
name|ps
operator|.
name|getRevision
argument_list|()
operator|==
literal|null
operator|||
name|ps
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
name|status
operator|.
name|put
argument_list|(
name|chg
operator|.
name|getId
argument_list|()
argument_list|,
name|CommitMergeStatus
operator|.
name|NO_PATCH_SET
argument_list|)
expr_stmt|;
continue|continue;
block|}
specifier|final
name|String
name|idstr
init|=
name|ps
operator|.
name|getRevision
argument_list|()
operator|.
name|get
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
name|status
operator|.
name|put
argument_list|(
name|chg
operator|.
name|getId
argument_list|()
argument_list|,
name|CommitMergeStatus
operator|.
name|NO_PATCH_SET
argument_list|)
expr_stmt|;
continue|continue;
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
comment|//
name|status
operator|.
name|put
argument_list|(
name|chg
operator|.
name|getId
argument_list|()
argument_list|,
name|CommitMergeStatus
operator|.
name|REVISION_GONE
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
name|patchsetId
operator|=
name|ps
operator|.
name|getId
argument_list|()
expr_stmt|;
name|commit
operator|.
name|originalOrder
operator|=
name|commitOrder
operator|++
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
name|CommitMergeStatus
operator|.
name|ALREADY_MERGED
expr_stmt|;
name|status
operator|.
name|put
argument_list|(
name|chg
operator|.
name|getId
argument_list|()
argument_list|,
name|commit
operator|.
name|statusCode
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
name|status
operator|.
name|put
argument_list|(
name|c
operator|.
name|patchsetId
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|c
operator|.
name|statusCode
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
comment|// If this project only permits fast-forwards, abort everything else.
comment|//
if|if
condition|(
literal|"true"
operator|.
name|equals
argument_list|(
name|db
operator|.
name|getConfig
argument_list|()
operator|.
name|getString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"fastforwardonly"
argument_list|)
argument_list|)
condition|)
block|{
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
name|n
operator|.
name|statusCode
operator|=
name|CommitMergeStatus
operator|.
name|PATH_CONFLICT
expr_stmt|;
name|status
operator|.
name|put
argument_list|(
name|n
operator|.
name|patchsetId
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|n
operator|.
name|statusCode
argument_list|)
expr_stmt|;
block|}
return|return;
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
name|patchsetId
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|failed
operator|.
name|statusCode
operator|=
name|CommitMergeStatus
operator|.
name|PATH_CONFLICT
expr_stmt|;
name|status
operator|.
name|put
argument_list|(
name|failed
operator|.
name|patchsetId
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|failed
operator|.
name|statusCode
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
name|myIdent
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
name|patchsetId
operator|!=
literal|null
condition|)
block|{
name|c
operator|.
name|statusCode
operator|=
name|CommitMergeStatus
operator|.
name|CLEAN_MERGE
expr_stmt|;
name|status
operator|.
name|put
argument_list|(
name|c
operator|.
name|patchsetId
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|c
operator|.
name|statusCode
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
DECL|method|updateBranch ()
specifier|private
name|void
name|updateBranch
parameter_list|()
throws|throws
name|MergeException
block|{
if|if
condition|(
name|branchTip
operator|==
literal|null
operator|||
name|branchTip
operator|!=
name|mergeTip
condition|)
block|{
name|branchUpdate
operator|.
name|setForceUpdate
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|branchUpdate
operator|.
name|setNewObjectId
argument_list|(
name|mergeTip
argument_list|)
expr_stmt|;
name|branchUpdate
operator|.
name|setRefLogMessage
argument_list|(
literal|"merged"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
switch|switch
condition|(
name|branchUpdate
operator|.
name|update
argument_list|(
name|rw
argument_list|)
condition|)
block|{
case|case
name|NEW
case|:
case|case
name|FAST_FORWARD
case|:
break|break;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
name|branchUpdate
operator|.
name|getResult
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
throw|;
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
literal|"Cannot update "
operator|+
name|branchUpdate
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_function

begin_function
DECL|method|updateChangeStatus ()
specifier|private
name|void
name|updateChangeStatus
parameter_list|()
block|{
for|for
control|(
specifier|final
name|Change
name|c
range|:
name|submitted
control|)
block|{
specifier|final
name|CommitMergeStatus
name|s
init|=
name|status
operator|.
name|get
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
comment|// Shouldn't ever happen, but leave the change alone. We'll pick
comment|// it up on the next pass.
comment|//
continue|continue;
block|}
switch|switch
condition|(
name|s
condition|)
block|{
case|case
name|CLEAN_MERGE
case|:
block|{
specifier|final
name|String
name|txt
init|=
literal|"Change has been successfully merged into the git repository."
decl_stmt|;
name|setMerged
argument_list|(
name|c
argument_list|,
name|message
argument_list|(
name|c
argument_list|,
name|txt
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|ALREADY_MERGED
case|:
name|setMerged
argument_list|(
name|c
argument_list|,
literal|null
argument_list|)
expr_stmt|;
break|break;
case|case
name|PATH_CONFLICT
case|:
block|{
specifier|final
name|String
name|txt
init|=
literal|"Your change could not been merged due to a path conflict.\n"
operator|+
literal|"\n"
operator|+
literal|"Please merge (or rebase) the change locally and upload the resolution for review."
decl_stmt|;
name|setNew
argument_list|(
name|c
argument_list|,
name|message
argument_list|(
name|c
argument_list|,
name|txt
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|MISSING_DEPENDENCY
case|:
block|{
try|try
block|{
specifier|final
name|String
name|txt
init|=
literal|"Change could not be merged because of a missing dependency.  As soon as its dependencies are submitted, the change will be submitted."
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ChangeMessage
argument_list|>
name|msgList
init|=
name|schema
operator|.
name|changeMessages
argument_list|()
operator|.
name|byChange
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
if|if
condition|(
name|msgList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|ChangeMessage
name|last
init|=
name|msgList
operator|.
name|get
argument_list|(
name|msgList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|last
operator|.
name|getAuthor
argument_list|()
operator|==
literal|null
operator|&&
name|txt
operator|.
name|equals
argument_list|(
name|last
operator|.
name|getMessage
argument_list|()
argument_list|)
condition|)
block|{
comment|// The last message was written by us, and it said this
comment|// same message already. Its unlikely anything has changed
comment|// that would cause us to need to repeat ourselves.
comment|//
break|break;
block|}
block|}
name|schema
operator|.
name|changeMessages
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|message
argument_list|(
name|c
argument_list|,
name|txt
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{           }
break|break;
block|}
default|default:
name|setNew
argument_list|(
name|c
argument_list|,
name|message
argument_list|(
name|c
argument_list|,
literal|"Unspecified merge failure: "
operator|+
name|s
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
end_function

begin_function
DECL|method|message (final Change c, final String body)
specifier|private
name|ChangeMessage
name|message
parameter_list|(
specifier|final
name|Change
name|c
parameter_list|,
specifier|final
name|String
name|body
parameter_list|)
block|{
specifier|final
name|byte
index|[]
name|raw
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
name|NB
operator|.
name|encodeInt32
argument_list|(
name|raw
argument_list|,
literal|0
argument_list|,
name|schema
operator|.
name|nextChangeMessageId
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|uuid
init|=
name|Base64
operator|.
name|encodeBytes
argument_list|(
name|raw
argument_list|)
decl_stmt|;
specifier|final
name|ChangeMessage
name|m
init|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
name|uuid
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|m
operator|.
name|setMessage
argument_list|(
name|body
argument_list|)
expr_stmt|;
return|return
name|m
return|;
block|}
end_function

begin_function
DECL|method|setMerged (Change c, ChangeMessage msg)
specifier|private
name|void
name|setMerged
parameter_list|(
name|Change
name|c
parameter_list|,
name|ChangeMessage
name|msg
parameter_list|)
block|{
specifier|final
name|PatchSet
operator|.
name|Id
name|merged
init|=
name|c
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|attempts
init|=
literal|0
init|;
name|attempts
operator|<
literal|10
condition|;
name|attempts
operator|++
control|)
block|{
name|c
operator|.
name|setStatus
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|MERGED
argument_list|)
expr_stmt|;
name|c
operator|.
name|updated
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|Transaction
name|txn
init|=
name|schema
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|schema
operator|.
name|changes
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|schema
operator|.
name|changeMessages
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|msg
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
block|}
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
try|try
block|{
name|c
operator|=
name|schema
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|merged
operator|.
name|equals
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
condition|)
block|{
comment|// Uncool; the patch set changed after we merged it.
comment|// Go back to the patch set that was actually merged.
comment|//
name|c
operator|.
name|setCurrentPatchSet
argument_list|(
name|schema
operator|.
name|patchSetInfo
argument_list|()
operator|.
name|get
argument_list|(
name|merged
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|e2
parameter_list|)
block|{         }
block|}
block|}
block|}
end_function

begin_function
DECL|method|setNew (Change c, ChangeMessage msg)
specifier|private
name|void
name|setNew
parameter_list|(
name|Change
name|c
parameter_list|,
name|ChangeMessage
name|msg
parameter_list|)
block|{
for|for
control|(
name|int
name|attempts
init|=
literal|0
init|;
name|attempts
operator|<
literal|10
condition|;
name|attempts
operator|++
control|)
block|{
name|c
operator|.
name|setStatus
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|NEW
argument_list|)
expr_stmt|;
name|c
operator|.
name|updated
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|Transaction
name|txn
init|=
name|schema
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|schema
operator|.
name|changes
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|schema
operator|.
name|changeMessages
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|msg
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
try|try
block|{
name|c
operator|=
name|schema
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|.
name|getStatus
argument_list|()
operator|.
name|isClosed
argument_list|()
condition|)
block|{
comment|// Someone else marked it close while we noticed a failure.
comment|// That's fine, leave it closed.
comment|//
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|e2
parameter_list|)
block|{         }
block|}
block|}
block|}
end_function

unit|}
end_unit

