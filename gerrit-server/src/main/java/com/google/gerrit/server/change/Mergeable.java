begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|change
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
name|collect
operator|.
name|Sets
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
name|common
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
name|extensions
operator|.
name|restapi
operator|.
name|AuthException
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
name|restapi
operator|.
name|BadRequestException
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
name|restapi
operator|.
name|ResourceConflictException
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
name|restapi
operator|.
name|RestReadView
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
name|reviewdb
operator|.
name|client
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
name|reviewdb
operator|.
name|client
operator|.
name|RevId
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
name|git
operator|.
name|BranchOrderSection
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
name|GitRepositoryManager
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
name|MergeException
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
name|strategy
operator|.
name|SubmitStrategyFactory
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
name|gwtorm
operator|.
name|server
operator|.
name|AtomicUpdate
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
name|server
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
name|org
operator|.
name|eclipse
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
name|eclipse
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
name|eclipse
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
name|RefDatabase
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
name|RevWalk
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
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
name|Arrays
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

begin_class
DECL|class|Mergeable
specifier|public
class|class
name|Mergeable
implements|implements
name|RestReadView
argument_list|<
name|RevisionResource
argument_list|>
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
name|Mergeable
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|MergeableInfo
specifier|public
specifier|static
class|class
name|MergeableInfo
block|{
DECL|field|submitType
specifier|public
name|SubmitType
name|submitType
decl_stmt|;
DECL|field|mergeable
specifier|public
name|boolean
name|mergeable
decl_stmt|;
DECL|field|mergeableInto
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|mergeableInto
decl_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--other-branches"
argument_list|,
name|aliases
operator|=
block|{
literal|"-o"
block|}
argument_list|,
name|usage
operator|=
literal|"test mergeability for other branches too"
argument_list|)
DECL|field|otherBranches
specifier|private
name|boolean
name|otherBranches
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--force"
argument_list|,
name|aliases
operator|=
block|{
literal|"-f"
block|}
argument_list|,
name|usage
operator|=
literal|"force recheck of mergeable field"
argument_list|)
DECL|method|setForce (boolean force)
specifier|public
name|void
name|setForce
parameter_list|(
name|boolean
name|force
parameter_list|)
block|{
name|this
operator|.
name|force
operator|=
name|force
expr_stmt|;
block|}
DECL|field|submitType
specifier|private
specifier|final
name|TestSubmitType
operator|.
name|Get
name|submitType
decl_stmt|;
DECL|field|gitManager
specifier|private
specifier|final
name|GitRepositoryManager
name|gitManager
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|submitStrategyFactory
specifier|private
specifier|final
name|SubmitStrategyFactory
name|submitStrategyFactory
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|indexer
specifier|private
specifier|final
name|ChangeIndexer
name|indexer
decl_stmt|;
DECL|field|force
specifier|private
name|boolean
name|force
decl_stmt|;
annotation|@
name|Inject
DECL|method|Mergeable (TestSubmitType.Get submitType, GitRepositoryManager gitManager, ProjectCache projectCache, SubmitStrategyFactory submitStrategyFactory, Provider<ReviewDb> db, ChangeIndexer indexer)
name|Mergeable
parameter_list|(
name|TestSubmitType
operator|.
name|Get
name|submitType
parameter_list|,
name|GitRepositoryManager
name|gitManager
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|SubmitStrategyFactory
name|submitStrategyFactory
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|ChangeIndexer
name|indexer
parameter_list|)
block|{
name|this
operator|.
name|submitType
operator|=
name|submitType
expr_stmt|;
name|this
operator|.
name|gitManager
operator|=
name|gitManager
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|submitStrategyFactory
operator|=
name|submitStrategyFactory
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (RevisionResource resource)
specifier|public
name|MergeableInfo
name|apply
parameter_list|(
name|RevisionResource
name|resource
parameter_list|)
throws|throws
name|AuthException
throws|,
name|ResourceConflictException
throws|,
name|BadRequestException
throws|,
name|OrmException
throws|,
name|IOException
block|{
name|Change
name|change
init|=
name|resource
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|PatchSet
name|ps
init|=
name|resource
operator|.
name|getPatchSet
argument_list|()
decl_stmt|;
name|MergeableInfo
name|result
init|=
operator|new
name|MergeableInfo
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"change is "
operator|+
name|Submit
operator|.
name|status
argument_list|(
name|change
argument_list|)
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|ps
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
condition|)
block|{
comment|// Only the current revision is mergeable. Others always fail.
return|return
name|result
return|;
block|}
name|result
operator|.
name|submitType
operator|=
name|submitType
operator|.
name|apply
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|result
operator|.
name|mergeable
operator|=
name|change
operator|.
name|isMergeable
argument_list|()
expr_stmt|;
name|Repository
name|git
init|=
name|gitManager
operator|.
name|openRepository
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|refs
init|=
name|git
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|getRefs
argument_list|(
name|RefDatabase
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|Ref
name|ref
init|=
name|refs
operator|.
name|get
argument_list|(
name|change
operator|.
name|getDest
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|force
operator|||
name|isStale
argument_list|(
name|change
argument_list|,
name|ref
argument_list|)
condition|)
block|{
name|result
operator|.
name|mergeable
operator|=
name|refresh
argument_list|(
name|change
argument_list|,
name|ps
argument_list|,
name|result
operator|.
name|submitType
argument_list|,
name|git
argument_list|,
name|refs
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|otherBranches
condition|)
block|{
name|result
operator|.
name|mergeableInto
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|BranchOrderSection
name|branchOrder
init|=
name|projectCache
operator|.
name|get
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
operator|.
name|getBranchOrderSection
argument_list|()
decl_stmt|;
if|if
condition|(
name|branchOrder
operator|!=
literal|null
condition|)
block|{
name|int
name|prefixLen
init|=
name|Constants
operator|.
name|R_HEADS
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|n
range|:
name|branchOrder
operator|.
name|getMoreStable
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
control|)
block|{
name|Ref
name|other
init|=
name|refs
operator|.
name|get
argument_list|(
name|n
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|isMergeable
argument_list|(
name|change
argument_list|,
name|ps
argument_list|,
name|SubmitType
operator|.
name|CHERRY_PICK
argument_list|,
name|git
argument_list|,
name|refs
argument_list|,
name|other
argument_list|)
condition|)
block|{
name|result
operator|.
name|mergeableInto
operator|.
name|add
argument_list|(
name|other
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
name|prefixLen
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
finally|finally
block|{
name|git
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|isStale (Change change, Ref ref)
specifier|private
specifier|static
name|boolean
name|isStale
parameter_list|(
name|Change
name|change
parameter_list|,
name|Ref
name|ref
parameter_list|)
block|{
return|return
name|change
operator|.
name|getLastSha1MergeTested
argument_list|()
operator|==
literal|null
operator|||
operator|!
name|toRevId
argument_list|(
name|ref
argument_list|)
operator|.
name|equals
argument_list|(
name|change
operator|.
name|getLastSha1MergeTested
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toRevId (Ref ref)
specifier|private
specifier|static
name|RevId
name|toRevId
parameter_list|(
name|Ref
name|ref
parameter_list|)
block|{
return|return
operator|new
name|RevId
argument_list|(
name|ref
operator|!=
literal|null
operator|&&
name|ref
operator|.
name|getObjectId
argument_list|()
operator|!=
literal|null
condition|?
name|ref
operator|.
name|getObjectId
argument_list|()
operator|.
name|name
argument_list|()
else|:
literal|""
argument_list|)
return|;
block|}
DECL|method|refresh (Change change, final PatchSet ps, SubmitType type, Repository git, Map<String, Ref> refs, final Ref ref)
specifier|private
name|boolean
name|refresh
parameter_list|(
name|Change
name|change
parameter_list|,
specifier|final
name|PatchSet
name|ps
parameter_list|,
name|SubmitType
name|type
parameter_list|,
name|Repository
name|git
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|refs
parameter_list|,
specifier|final
name|Ref
name|ref
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
specifier|final
name|boolean
name|mergeable
init|=
name|isMergeable
argument_list|(
name|change
argument_list|,
name|ps
argument_list|,
name|type
argument_list|,
name|git
argument_list|,
name|refs
argument_list|,
name|ref
argument_list|)
decl_stmt|;
name|Change
name|c
init|=
name|db
operator|.
name|get
argument_list|()
operator|.
name|changes
argument_list|()
operator|.
name|atomicUpdate
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|AtomicUpdate
argument_list|<
name|Change
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Change
name|update
parameter_list|(
name|Change
name|c
parameter_list|)
block|{
if|if
condition|(
name|c
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
operator|&&
name|ps
operator|.
name|getId
argument_list|()
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
name|c
operator|.
name|setMergeable
argument_list|(
name|mergeable
argument_list|)
expr_stmt|;
name|c
operator|.
name|setLastSha1MergeTested
argument_list|(
name|toRevId
argument_list|(
name|ref
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|indexer
operator|.
name|index
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|mergeable
return|;
block|}
DECL|method|isMergeable (Change change, final PatchSet ps, SubmitType type, Repository git, Map<String, Ref> refs, final Ref ref)
specifier|private
name|boolean
name|isMergeable
parameter_list|(
name|Change
name|change
parameter_list|,
specifier|final
name|PatchSet
name|ps
parameter_list|,
name|SubmitType
name|type
parameter_list|,
name|Repository
name|git
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|refs
parameter_list|,
specifier|final
name|Ref
name|ref
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|git
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|CodeReviewCommit
name|createCommit
parameter_list|(
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
decl_stmt|;
try|try
block|{
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
name|ps
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Invalid revision on patch set %d of %d"
argument_list|,
name|ps
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|change
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
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
name|CodeReviewCommit
name|rev
init|=
name|parse
argument_list|(
name|rw
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|rev
operator|.
name|add
argument_list|(
name|canMerge
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|mergeable
decl_stmt|;
if|if
condition|(
name|ref
operator|==
literal|null
operator|||
name|ref
operator|.
name|getObjectId
argument_list|()
operator|==
literal|null
condition|)
block|{
name|mergeable
operator|=
literal|true
expr_stmt|;
comment|// Assume yes on new branch.
block|}
else|else
block|{
name|CodeReviewCommit
name|tip
init|=
name|parse
argument_list|(
name|rw
argument_list|,
name|ref
operator|.
name|getObjectId
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|accepted
init|=
name|alreadyAccepted
argument_list|(
name|rw
argument_list|,
name|refs
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|accepted
operator|.
name|add
argument_list|(
name|tip
argument_list|)
expr_stmt|;
name|accepted
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|rev
operator|.
name|getParents
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|mergeable
operator|=
name|submitStrategyFactory
operator|.
name|create
argument_list|(
name|type
argument_list|,
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|git
argument_list|,
name|rw
argument_list|,
literal|null
comment|/*inserter*/
argument_list|,
name|canMerge
argument_list|,
name|accepted
argument_list|,
name|change
operator|.
name|getDest
argument_list|()
argument_list|)
operator|.
name|dryRun
argument_list|(
name|tip
argument_list|,
name|rev
argument_list|)
expr_stmt|;
block|}
return|return
name|mergeable
return|;
block|}
catch|catch
parameter_list|(
name|MergeException
decl||
name|IOException
decl||
name|NoSuchProjectException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot merge test change %d"
argument_list|,
name|change
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
finally|finally
block|{
name|rw
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|alreadyAccepted (RevWalk rw, Collection<Ref> refs)
specifier|private
specifier|static
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|alreadyAccepted
parameter_list|(
name|RevWalk
name|rw
parameter_list|,
name|Collection
argument_list|<
name|Ref
argument_list|>
name|refs
parameter_list|)
throws|throws
name|MissingObjectException
throws|,
name|IOException
block|{
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|accepted
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Ref
name|r
range|:
name|refs
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
name|accepted
operator|.
name|add
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
name|nonCommit
parameter_list|)
block|{
comment|// Not a commit? Skip over it.
block|}
block|}
block|}
return|return
name|accepted
return|;
block|}
DECL|method|parse (RevWalk rw, ObjectId id)
specifier|private
specifier|static
name|CodeReviewCommit
name|parse
parameter_list|(
name|RevWalk
name|rw
parameter_list|,
name|ObjectId
name|id
parameter_list|)
throws|throws
name|MissingObjectException
throws|,
name|IncorrectObjectTypeException
throws|,
name|IOException
block|{
return|return
operator|(
name|CodeReviewCommit
operator|)
name|rw
operator|.
name|parseCommit
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
end_class

end_unit

