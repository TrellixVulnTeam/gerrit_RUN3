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
DECL|package|com.google.gerrit.server.git
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
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
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
name|Maps
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
name|client
operator|.
name|Project
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|errors
operator|.
name|RepositoryNotFoundException
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
name|ObjectReader
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
name|RefUpdate
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
name|RevSort
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
name|sql
operator|.
name|Timestamp
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
name|Map
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

begin_comment
comment|/**  * This is a helper class for MergeOp and not intended for general use.  *  * Some database backends require to open a repository just once within  * a transaction of a submission, this caches open repositories to satisfy  * that requirement.  */
end_comment

begin_class
DECL|class|MergeOpRepoManager
specifier|public
class|class
name|MergeOpRepoManager
implements|implements
name|AutoCloseable
block|{
DECL|class|OpenRepo
specifier|public
class|class
name|OpenRepo
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
DECL|field|canMergeFlag
specifier|final
name|RevFlag
name|canMergeFlag
decl_stmt|;
DECL|field|ins
specifier|final
name|ObjectInserter
name|ins
decl_stmt|;
DECL|field|project
specifier|final
name|ProjectState
name|project
decl_stmt|;
DECL|field|update
name|BatchUpdate
name|update
decl_stmt|;
DECL|field|reader
specifier|private
specifier|final
name|ObjectReader
name|reader
decl_stmt|;
DECL|field|branches
specifier|private
specifier|final
name|Map
argument_list|<
name|Branch
operator|.
name|NameKey
argument_list|,
name|OpenBranch
argument_list|>
name|branches
decl_stmt|;
DECL|method|OpenRepo (Repository repo, ProjectState project)
specifier|private
name|OpenRepo
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|ProjectState
name|project
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
name|project
operator|=
name|project
expr_stmt|;
name|ins
operator|=
name|repo
operator|.
name|newObjectInserter
argument_list|()
expr_stmt|;
name|reader
operator|=
name|ins
operator|.
name|newReader
argument_list|()
expr_stmt|;
name|rw
operator|=
name|CodeReviewCommit
operator|.
name|newRevWalk
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|rw
operator|.
name|sort
argument_list|(
name|RevSort
operator|.
name|TOPO
argument_list|)
expr_stmt|;
name|rw
operator|.
name|sort
argument_list|(
name|RevSort
operator|.
name|COMMIT_TIME_DESC
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rw
operator|.
name|setRetainBody
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|canMergeFlag
operator|=
name|rw
operator|.
name|newFlag
argument_list|(
literal|"CAN_MERGE"
argument_list|)
expr_stmt|;
name|rw
operator|.
name|retainOnReset
argument_list|(
name|canMergeFlag
argument_list|)
expr_stmt|;
name|branches
operator|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|getBranch (Branch.NameKey branch)
name|OpenBranch
name|getBranch
parameter_list|(
name|Branch
operator|.
name|NameKey
name|branch
parameter_list|)
throws|throws
name|IntegrationException
block|{
name|OpenBranch
name|ob
init|=
name|branches
operator|.
name|get
argument_list|(
name|branch
argument_list|)
decl_stmt|;
if|if
condition|(
name|ob
operator|==
literal|null
condition|)
block|{
name|ob
operator|=
operator|new
name|OpenBranch
argument_list|(
name|this
argument_list|,
name|branch
argument_list|)
expr_stmt|;
name|branches
operator|.
name|put
argument_list|(
name|branch
argument_list|,
name|ob
argument_list|)
expr_stmt|;
block|}
return|return
name|ob
return|;
block|}
DECL|method|getProjectName ()
name|Project
operator|.
name|NameKey
name|getProjectName
parameter_list|()
block|{
return|return
name|project
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
return|;
block|}
DECL|method|getUpdate ()
name|BatchUpdate
name|getUpdate
parameter_list|()
block|{
name|checkState
argument_list|(
name|db
operator|!=
literal|null
argument_list|,
literal|"call setContext before getUpdate"
argument_list|)
expr_stmt|;
if|if
condition|(
name|update
operator|==
literal|null
condition|)
block|{
name|update
operator|=
name|batchUpdateFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|getProjectName
argument_list|()
argument_list|,
name|caller
argument_list|,
name|ts
argument_list|)
expr_stmt|;
name|update
operator|.
name|setRepository
argument_list|(
name|repo
argument_list|,
name|rw
argument_list|,
name|ins
argument_list|)
expr_stmt|;
block|}
return|return
name|update
return|;
block|}
DECL|method|close ()
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|update
operator|!=
literal|null
condition|)
block|{
name|update
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|rw
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|ins
operator|.
name|close
argument_list|()
expr_stmt|;
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|OpenBranch
specifier|public
specifier|static
class|class
name|OpenBranch
block|{
DECL|field|update
specifier|final
name|RefUpdate
name|update
decl_stmt|;
DECL|field|oldTip
specifier|final
name|CodeReviewCommit
name|oldTip
decl_stmt|;
DECL|field|mergeTip
name|MergeTip
name|mergeTip
decl_stmt|;
DECL|method|OpenBranch (OpenRepo or, Branch.NameKey name)
name|OpenBranch
parameter_list|(
name|OpenRepo
name|or
parameter_list|,
name|Branch
operator|.
name|NameKey
name|name
parameter_list|)
throws|throws
name|IntegrationException
block|{
try|try
block|{
name|update
operator|=
name|or
operator|.
name|repo
operator|.
name|updateRef
argument_list|(
name|name
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|update
operator|.
name|getOldObjectId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|oldTip
operator|=
name|or
operator|.
name|rw
operator|.
name|parseCommit
argument_list|(
name|update
operator|.
name|getOldObjectId
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Objects
operator|.
name|equals
argument_list|(
name|or
operator|.
name|repo
operator|.
name|getFullBranch
argument_list|()
argument_list|,
name|name
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
name|oldTip
operator|=
literal|null
expr_stmt|;
name|update
operator|.
name|setExpectedOldObjectId
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IntegrationException
argument_list|(
literal|"The destination branch "
operator|+
name|name
operator|+
literal|" does not exist anymore."
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
name|IntegrationException
argument_list|(
literal|"Cannot open branch "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|openRepos
specifier|private
specifier|final
name|Map
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|OpenRepo
argument_list|>
name|openRepos
decl_stmt|;
DECL|field|batchUpdateFactory
specifier|private
specifier|final
name|BatchUpdate
operator|.
name|Factory
name|batchUpdateFactory
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|db
specifier|private
name|ReviewDb
name|db
decl_stmt|;
DECL|field|ts
specifier|private
name|Timestamp
name|ts
decl_stmt|;
DECL|field|caller
specifier|private
name|IdentifiedUser
name|caller
decl_stmt|;
annotation|@
name|Inject
DECL|method|MergeOpRepoManager ( GitRepositoryManager repoManager, ProjectCache projectCache, BatchUpdate.Factory batchUpdateFactory)
name|MergeOpRepoManager
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|BatchUpdate
operator|.
name|Factory
name|batchUpdateFactory
parameter_list|)
block|{
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|batchUpdateFactory
operator|=
name|batchUpdateFactory
expr_stmt|;
name|openRepos
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|setContext (ReviewDb db, Timestamp ts, IdentifiedUser caller)
name|void
name|setContext
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Timestamp
name|ts
parameter_list|,
name|IdentifiedUser
name|caller
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|ts
operator|=
name|ts
expr_stmt|;
name|this
operator|.
name|caller
operator|=
name|caller
expr_stmt|;
block|}
DECL|method|getRepo (Project.NameKey project)
specifier|public
name|OpenRepo
name|getRepo
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
block|{
name|OpenRepo
name|or
init|=
name|openRepos
operator|.
name|get
argument_list|(
name|project
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|or
operator|!=
literal|null
argument_list|,
literal|"repo not yet opened: %s"
argument_list|,
name|project
argument_list|)
expr_stmt|;
return|return
name|or
return|;
block|}
DECL|method|openRepo (Project.NameKey project, boolean abortIfOpen)
specifier|public
name|void
name|openRepo
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|boolean
name|abortIfOpen
parameter_list|)
throws|throws
name|NoSuchProjectException
throws|,
name|IOException
block|{
if|if
condition|(
name|abortIfOpen
condition|)
block|{
name|checkState
argument_list|(
operator|!
name|openRepos
operator|.
name|containsKey
argument_list|(
name|project
argument_list|)
argument_list|,
literal|"repo already opened: %s"
argument_list|,
name|project
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|openRepos
operator|.
name|containsKey
argument_list|(
name|project
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
name|ProjectState
name|projectState
init|=
name|projectCache
operator|.
name|get
argument_list|(
name|project
argument_list|)
decl_stmt|;
if|if
condition|(
name|projectState
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchProjectException
argument_list|(
name|project
argument_list|)
throw|;
block|}
try|try
block|{
name|OpenRepo
name|or
init|=
operator|new
name|OpenRepo
argument_list|(
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
argument_list|,
name|projectState
argument_list|)
decl_stmt|;
name|openRepos
operator|.
name|put
argument_list|(
name|project
argument_list|,
name|or
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NoSuchProjectException
argument_list|(
name|project
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
for|for
control|(
name|OpenRepo
name|repo
range|:
name|openRepos
operator|.
name|values
argument_list|()
control|)
block|{
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

