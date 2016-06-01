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
DECL|package|com.google.gerrit.server.query.change
package|package
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
name|Lists
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
name|strategy
operator|.
name|SubmitDryRun
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
name|OperatorPredicate
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
name|OrPredicate
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
name|Predicate
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
name|ChangeQueryBuilder
operator|.
name|Arguments
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
name|RevWalk
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
name|filter
operator|.
name|RevFilter
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
name|treewalk
operator|.
name|TreeWalk
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
name|treewalk
operator|.
name|filter
operator|.
name|TreeFilter
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
name|HashSet
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

begin_class
DECL|class|ConflictsPredicate
class|class
name|ConflictsPredicate
extends|extends
name|OrPredicate
argument_list|<
name|ChangeData
argument_list|>
block|{
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
DECL|method|ConflictsPredicate (Arguments args, String value, List<Change> changes)
name|ConflictsPredicate
parameter_list|(
name|Arguments
name|args
parameter_list|,
name|String
name|value
parameter_list|,
name|List
argument_list|<
name|Change
argument_list|>
name|changes
parameter_list|)
throws|throws
name|OrmException
block|{
name|super
argument_list|(
name|predicates
argument_list|(
name|args
argument_list|,
name|value
argument_list|,
name|changes
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|predicates (final Arguments args, String value, List<Change> changes)
specifier|private
specifier|static
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|predicates
parameter_list|(
specifier|final
name|Arguments
name|args
parameter_list|,
name|String
name|value
parameter_list|,
name|List
argument_list|<
name|Change
argument_list|>
name|changes
parameter_list|)
throws|throws
name|OrmException
block|{
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|changePredicates
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|changes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
init|=
name|args
operator|.
name|db
decl_stmt|;
for|for
control|(
specifier|final
name|Change
name|c
range|:
name|changes
control|)
block|{
specifier|final
name|ChangeDataCache
name|changeDataCache
init|=
operator|new
name|ChangeDataCache
argument_list|(
name|c
argument_list|,
name|db
argument_list|,
name|args
operator|.
name|changeDataFactory
argument_list|,
name|args
operator|.
name|projectCache
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|listFiles
argument_list|(
name|c
argument_list|,
name|args
argument_list|,
name|changeDataCache
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|filePredicates
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|files
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|filePredicates
operator|.
name|add
argument_list|(
operator|new
name|EqualsPathPredicate
argument_list|(
name|ChangeQueryBuilder
operator|.
name|FIELD_PATH
argument_list|,
name|file
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|predicatesForOneChange
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|predicatesForOneChange
operator|.
name|add
argument_list|(
name|not
argument_list|(
operator|new
name|LegacyChangeIdPredicate
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|predicatesForOneChange
operator|.
name|add
argument_list|(
operator|new
name|ProjectPredicate
argument_list|(
name|c
operator|.
name|getProject
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|predicatesForOneChange
operator|.
name|add
argument_list|(
operator|new
name|RefPredicate
argument_list|(
name|c
operator|.
name|getDest
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|predicatesForOneChange
operator|.
name|add
argument_list|(
name|or
argument_list|(
name|or
argument_list|(
name|filePredicates
argument_list|)
argument_list|,
operator|new
name|IsMergePredicate
argument_list|(
name|args
argument_list|,
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|predicatesForOneChange
operator|.
name|add
argument_list|(
operator|new
name|OperatorPredicate
argument_list|<
name|ChangeData
argument_list|>
argument_list|(
name|ChangeQueryBuilder
operator|.
name|FIELD_CONFLICTS
argument_list|,
name|value
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|match
parameter_list|(
name|ChangeData
name|object
parameter_list|)
throws|throws
name|OrmException
block|{
name|Change
name|otherChange
init|=
name|object
operator|.
name|change
argument_list|()
decl_stmt|;
if|if
condition|(
name|otherChange
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|otherChange
operator|.
name|getDest
argument_list|()
operator|.
name|equals
argument_list|(
name|c
operator|.
name|getDest
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SubmitTypeRecord
name|str
init|=
name|object
operator|.
name|submitTypeRecord
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|str
operator|.
name|isOk
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ObjectId
name|other
init|=
name|ObjectId
operator|.
name|fromString
argument_list|(
name|object
operator|.
name|currentPatchSet
argument_list|()
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|ConflictKey
name|conflictsKey
init|=
operator|new
name|ConflictKey
argument_list|(
name|changeDataCache
operator|.
name|getTestAgainst
argument_list|()
argument_list|,
name|other
argument_list|,
name|str
operator|.
name|type
argument_list|,
name|changeDataCache
operator|.
name|getProjectState
argument_list|()
operator|.
name|isUseContentMerge
argument_list|()
argument_list|)
decl_stmt|;
name|Boolean
name|conflicts
init|=
name|args
operator|.
name|conflictsCache
operator|.
name|getIfPresent
argument_list|(
name|conflictsKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|conflicts
operator|!=
literal|null
condition|)
block|{
return|return
name|conflicts
return|;
block|}
try|try
init|(
name|Repository
name|repo
init|=
name|args
operator|.
name|repoManager
operator|.
name|openRepository
argument_list|(
name|otherChange
operator|.
name|getProject
argument_list|()
argument_list|)
init|;
name|CodeReviewRevWalk
name|rw
operator|=
name|CodeReviewCommit
operator|.
name|newRevWalk
argument_list|(
name|repo
argument_list|)
init|)
block|{
name|conflicts
operator|=
operator|!
name|args
operator|.
name|submitDryRun
operator|.
name|run
argument_list|(
name|str
operator|.
name|type
argument_list|,
name|repo
argument_list|,
name|rw
argument_list|,
name|otherChange
operator|.
name|getDest
argument_list|()
argument_list|,
name|changeDataCache
operator|.
name|getTestAgainst
argument_list|()
argument_list|,
name|other
argument_list|,
name|getAlreadyAccepted
argument_list|(
name|repo
argument_list|,
name|rw
argument_list|)
argument_list|)
expr_stmt|;
name|args
operator|.
name|conflictsCache
operator|.
name|put
argument_list|(
name|conflictsKey
argument_list|,
name|conflicts
argument_list|)
expr_stmt|;
return|return
name|conflicts
return|;
block|}
catch|catch
parameter_list|(
name|IntegrationException
decl||
name|NoSuchProjectException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
literal|5
return|;
block|}
specifier|private
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
name|IntegrationException
block|{
try|try
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
name|SubmitDryRun
operator|.
name|addCommits
argument_list|(
name|changeDataCache
operator|.
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
name|ObjectId
name|tip
init|=
name|changeDataCache
operator|.
name|getTestAgainst
argument_list|()
decl_stmt|;
if|if
condition|(
name|tip
operator|!=
literal|null
condition|)
block|{
name|accepted
operator|.
name|add
argument_list|(
name|rw
operator|.
name|parseCommit
argument_list|(
name|tip
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|accepted
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IntegrationException
argument_list|(
literal|"Failed to determine already accepted commits."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|changePredicates
operator|.
name|add
argument_list|(
name|and
argument_list|(
name|predicatesForOneChange
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|changePredicates
return|;
block|}
DECL|method|listFiles (Change c, Arguments args, ChangeDataCache changeDataCache)
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|listFiles
parameter_list|(
name|Change
name|c
parameter_list|,
name|Arguments
name|args
parameter_list|,
name|ChangeDataCache
name|changeDataCache
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|args
operator|.
name|repoManager
operator|.
name|openRepository
argument_list|(
name|c
operator|.
name|getProject
argument_list|()
argument_list|)
init|;
name|RevWalk
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
init|)
block|{
name|RevCommit
name|ps
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|changeDataCache
operator|.
name|getTestAgainst
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|.
name|getParentCount
argument_list|()
operator|>
literal|1
condition|)
block|{
name|String
name|dest
init|=
name|c
operator|.
name|getDest
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|Ref
name|destBranch
init|=
name|repo
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|getRef
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|destBranch
operator|.
name|getObjectId
argument_list|()
expr_stmt|;
name|rw
operator|.
name|setRevFilter
argument_list|(
name|RevFilter
operator|.
name|MERGE_BASE
argument_list|)
expr_stmt|;
name|rw
operator|.
name|markStart
argument_list|(
name|rw
operator|.
name|parseCommit
argument_list|(
name|destBranch
operator|.
name|getObjectId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|rw
operator|.
name|markStart
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|RevCommit
name|base
init|=
name|rw
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// TODO(zivkov): handle the case with multiple merge bases
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|TreeWalk
name|tw
init|=
operator|new
name|TreeWalk
argument_list|(
name|repo
argument_list|)
init|)
block|{
if|if
condition|(
name|base
operator|!=
literal|null
condition|)
block|{
name|tw
operator|.
name|setFilter
argument_list|(
name|TreeFilter
operator|.
name|ANY_DIFF
argument_list|)
expr_stmt|;
name|tw
operator|.
name|addTree
argument_list|(
name|base
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tw
operator|.
name|addTree
argument_list|(
name|ps
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|tw
operator|.
name|setRecursive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
while|while
condition|(
name|tw
operator|.
name|next
argument_list|()
condition|)
block|{
name|files
operator|.
name|add
argument_list|(
name|tw
operator|.
name|getPathString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|files
return|;
block|}
return|return
name|args
operator|.
name|changeDataFactory
operator|.
name|create
argument_list|(
name|args
operator|.
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|c
argument_list|)
operator|.
name|currentFilePaths
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|ChangeQueryBuilder
operator|.
name|FIELD_CONFLICTS
operator|+
literal|":"
operator|+
name|value
return|;
block|}
DECL|class|ChangeDataCache
specifier|private
specifier|static
class|class
name|ChangeDataCache
block|{
DECL|field|change
specifier|private
specifier|final
name|Change
name|change
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
DECL|field|changeDataFactory
specifier|private
specifier|final
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|testAgainst
specifier|private
name|ObjectId
name|testAgainst
decl_stmt|;
DECL|field|projectState
specifier|private
name|ProjectState
name|projectState
decl_stmt|;
DECL|field|alreadyAccepted
specifier|private
name|Iterable
argument_list|<
name|ObjectId
argument_list|>
name|alreadyAccepted
decl_stmt|;
DECL|method|ChangeDataCache (Change change, Provider<ReviewDb> db, ChangeData.Factory changeDataFactory, ProjectCache projectCache)
name|ChangeDataCache
parameter_list|(
name|Change
name|change
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|)
block|{
name|this
operator|.
name|change
operator|=
name|change
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|changeDataFactory
operator|=
name|changeDataFactory
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
block|}
DECL|method|getTestAgainst ()
name|ObjectId
name|getTestAgainst
parameter_list|()
throws|throws
name|OrmException
block|{
if|if
condition|(
name|testAgainst
operator|==
literal|null
condition|)
block|{
name|testAgainst
operator|=
name|ObjectId
operator|.
name|fromString
argument_list|(
name|changeDataFactory
operator|.
name|create
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|change
argument_list|)
operator|.
name|currentPatchSet
argument_list|()
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|testAgainst
return|;
block|}
DECL|method|getProjectState ()
name|ProjectState
name|getProjectState
parameter_list|()
block|{
if|if
condition|(
name|projectState
operator|==
literal|null
condition|)
block|{
name|projectState
operator|=
name|projectCache
operator|.
name|get
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|projectState
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
operator|new
name|NoSuchProjectException
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
return|return
name|projectState
return|;
block|}
DECL|method|getAlreadyAccepted (Repository repo)
name|Iterable
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
if|if
condition|(
name|alreadyAccepted
operator|==
literal|null
condition|)
block|{
name|alreadyAccepted
operator|=
name|SubmitDryRun
operator|.
name|getAlreadyAccepted
argument_list|(
name|repo
argument_list|)
expr_stmt|;
block|}
return|return
name|alreadyAccepted
return|;
block|}
block|}
block|}
end_class

end_unit

