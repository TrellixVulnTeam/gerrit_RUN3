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
name|gerrit
operator|.
name|common
operator|.
name|data
operator|.
name|SubmitTypeRecord
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
name|extensions
operator|.
name|common
operator|.
name|MergeableInfo
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
name|ChangeUtil
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
name|project
operator|.
name|SubmitRuleEvaluator
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
name|ChangeData
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
name|Objects
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
DECL|field|mergeUtilFactory
specifier|private
specifier|final
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
decl_stmt|;
DECL|field|changeDataFactory
specifier|private
specifier|final
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
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
DECL|field|cache
specifier|private
specifier|final
name|MergeabilityCache
name|cache
decl_stmt|;
annotation|@
name|Inject
DECL|method|Mergeable (GitRepositoryManager gitManager, ProjectCache projectCache, MergeUtil.Factory mergeUtilFactory, ChangeData.Factory changeDataFactory, Provider<ReviewDb> db, ChangeIndexer indexer, MergeabilityCache cache)
name|Mergeable
parameter_list|(
name|GitRepositoryManager
name|gitManager
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
parameter_list|,
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|ChangeIndexer
name|indexer
parameter_list|,
name|MergeabilityCache
name|cache
parameter_list|)
block|{
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
name|mergeUtilFactory
operator|=
name|mergeUtilFactory
expr_stmt|;
name|this
operator|.
name|changeDataFactory
operator|=
name|changeDataFactory
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
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
block|}
DECL|method|setOtherBranches (boolean otherBranches)
specifier|public
name|void
name|setOtherBranches
parameter_list|(
name|boolean
name|otherBranches
parameter_list|)
block|{
name|this
operator|.
name|otherBranches
operator|=
name|otherBranches
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
name|ChangeData
name|cd
init|=
name|changeDataFactory
operator|.
name|create
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|resource
operator|.
name|getControl
argument_list|()
argument_list|)
decl_stmt|;
name|SubmitTypeRecord
name|rec
init|=
operator|new
name|SubmitRuleEvaluator
argument_list|(
name|cd
argument_list|)
operator|.
name|setPatchSet
argument_list|(
name|ps
argument_list|)
operator|.
name|getSubmitType
argument_list|()
decl_stmt|;
if|if
condition|(
name|rec
operator|.
name|status
operator|!=
name|SubmitTypeRecord
operator|.
name|Status
operator|.
name|OK
condition|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Submit type rule failed: "
operator|+
name|rec
argument_list|)
throw|;
block|}
name|result
operator|.
name|submitType
operator|=
name|rec
operator|.
name|type
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
name|ObjectId
name|commit
init|=
name|toId
argument_list|(
name|ps
argument_list|)
decl_stmt|;
if|if
condition|(
name|commit
operator|==
literal|null
condition|)
block|{
name|result
operator|.
name|mergeable
operator|=
literal|false
expr_stmt|;
return|return
name|result
return|;
block|}
name|Ref
name|ref
init|=
name|git
operator|.
name|getRef
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
name|ProjectState
name|projectState
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
decl_stmt|;
name|String
name|strategy
init|=
name|mergeUtilFactory
operator|.
name|create
argument_list|(
name|projectState
argument_list|)
operator|.
name|mergeStrategyName
argument_list|()
decl_stmt|;
name|Boolean
name|old
init|=
name|cache
operator|.
name|getIfPresent
argument_list|(
name|commit
argument_list|,
name|ref
argument_list|,
name|result
operator|.
name|submitType
argument_list|,
name|strategy
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|==
literal|null
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
name|commit
argument_list|,
name|ref
argument_list|,
name|result
operator|.
name|submitType
argument_list|,
name|strategy
argument_list|,
name|git
argument_list|,
name|old
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|mergeable
operator|=
name|old
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
name|projectState
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
name|git
operator|.
name|getRef
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
name|cache
operator|.
name|get
argument_list|(
name|commit
argument_list|,
name|other
argument_list|,
name|SubmitType
operator|.
name|CHERRY_PICK
argument_list|,
name|strategy
argument_list|,
name|change
operator|.
name|getDest
argument_list|()
argument_list|,
name|git
argument_list|,
name|db
operator|.
name|get
argument_list|()
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
DECL|method|toId (PatchSet ps)
specifier|private
specifier|static
name|ObjectId
name|toId
parameter_list|(
name|PatchSet
name|ps
parameter_list|)
block|{
try|try
block|{
return|return
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
return|;
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
literal|"Invalid revision on patch set "
operator|+
name|ps
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|refresh (final Change change, ObjectId commit, final Ref ref, SubmitType type, String strategy, Repository git, Boolean old)
specifier|private
name|boolean
name|refresh
parameter_list|(
specifier|final
name|Change
name|change
parameter_list|,
name|ObjectId
name|commit
parameter_list|,
specifier|final
name|Ref
name|ref
parameter_list|,
name|SubmitType
name|type
parameter_list|,
name|String
name|strategy
parameter_list|,
name|Repository
name|git
parameter_list|,
name|Boolean
name|old
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
block|{
specifier|final
name|boolean
name|mergeable
init|=
name|cache
operator|.
name|get
argument_list|(
name|commit
argument_list|,
name|ref
argument_list|,
name|type
argument_list|,
name|strategy
argument_list|,
name|change
operator|.
name|getDest
argument_list|()
argument_list|,
name|git
argument_list|,
name|db
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|mergeable
argument_list|,
name|old
argument_list|)
condition|)
block|{
comment|// TODO(dborowitz): Include cache info in ETag somehow instead.
name|ChangeUtil
operator|.
name|bumpRowVersionNotLastUpdatedOn
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|db
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|indexer
operator|.
name|index
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|change
argument_list|)
expr_stmt|;
block|}
return|return
name|mergeable
return|;
block|}
block|}
end_class

end_unit

