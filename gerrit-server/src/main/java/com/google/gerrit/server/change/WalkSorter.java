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
name|auto
operator|.
name|value
operator|.
name|AutoValue
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
name|annotations
operator|.
name|VisibleForTesting
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
name|base
operator|.
name|Function
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
name|ArrayListMultimap
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
name|ImmutableList
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
name|Iterables
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
name|Multimap
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
name|Ordering
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
name|RevSort
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
comment|/**  * Helper to sort {@link ChangeData}s based on {@link RevWalk} ordering.  *<p>  * Split changes by project, and map each change to a single commit based on the  * latest patch set. The set of patch sets considered may be limited by calling  * {@link #includePatchSets(Set)}. Perform a standard {@link RevWalk} on each  * project repository, and record the order in which each change's commit is  * seen.  *<p>  * Once an order within each project is determined, groups of changes are sorted  * based on the project name. This is slightly more stable than sorting on  * something like the commit or change timestamp, as it will not unexpectedly  * reorder large groups of changes on subsequent calls if one of the changes was  * updated.  */
end_comment

begin_class
DECL|class|WalkSorter
class|class
name|WalkSorter
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
name|WalkSorter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|PROJECT_LIST_SORTER
specifier|private
specifier|static
specifier|final
name|Ordering
argument_list|<
name|List
argument_list|<
name|PatchSetData
argument_list|>
argument_list|>
name|PROJECT_LIST_SORTER
init|=
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|nullsFirst
argument_list|()
operator|.
name|onResultOf
argument_list|(
operator|new
name|Function
argument_list|<
name|List
argument_list|<
name|PatchSetData
argument_list|>
argument_list|,
name|Project
operator|.
name|NameKey
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Project
operator|.
name|NameKey
name|apply
parameter_list|(
name|List
argument_list|<
name|PatchSetData
argument_list|>
name|in
parameter_list|)
block|{
if|if
condition|(
name|in
operator|==
literal|null
operator|||
name|in
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
return|return
name|in
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|data
argument_list|()
operator|.
name|change
argument_list|()
operator|.
name|getProject
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
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
block|}
argument_list|)
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|includePatchSets
specifier|private
specifier|final
name|Set
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|>
name|includePatchSets
decl_stmt|;
DECL|field|retainBody
specifier|private
name|boolean
name|retainBody
decl_stmt|;
annotation|@
name|Inject
DECL|method|WalkSorter (GitRepositoryManager repoManager)
name|WalkSorter
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|)
block|{
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|includePatchSets
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|includePatchSets (Iterable<PatchSet.Id> patchSets)
specifier|public
name|WalkSorter
name|includePatchSets
parameter_list|(
name|Iterable
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|>
name|patchSets
parameter_list|)
block|{
name|Iterables
operator|.
name|addAll
argument_list|(
name|includePatchSets
argument_list|,
name|patchSets
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setRetainBody (boolean retainBody)
specifier|public
name|WalkSorter
name|setRetainBody
parameter_list|(
name|boolean
name|retainBody
parameter_list|)
block|{
name|this
operator|.
name|retainBody
operator|=
name|retainBody
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|sort (Iterable<ChangeData> in)
specifier|public
name|Iterable
argument_list|<
name|PatchSetData
argument_list|>
name|sort
parameter_list|(
name|Iterable
argument_list|<
name|ChangeData
argument_list|>
name|in
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
block|{
name|Multimap
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|ChangeData
argument_list|>
name|byProject
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|in
control|)
block|{
name|byProject
operator|.
name|put
argument_list|(
name|cd
operator|.
name|change
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|,
name|cd
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|List
argument_list|<
name|PatchSetData
argument_list|>
argument_list|>
name|sortedByProject
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|byProject
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|Collection
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|e
range|:
name|byProject
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|sortedByProject
operator|.
name|add
argument_list|(
name|sortProject
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|sortedByProject
argument_list|,
name|PROJECT_LIST_SORTER
argument_list|)
expr_stmt|;
return|return
name|Iterables
operator|.
name|concat
argument_list|(
name|sortedByProject
argument_list|)
return|;
block|}
DECL|method|sortProject (Project.NameKey project, Collection<ChangeData> in)
specifier|private
name|List
argument_list|<
name|PatchSetData
argument_list|>
name|sortProject
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Collection
argument_list|<
name|ChangeData
argument_list|>
name|in
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
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
name|rw
operator|.
name|setRetainBody
argument_list|(
name|retainBody
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
name|Multimap
argument_list|<
name|RevCommit
argument_list|,
name|PatchSetData
argument_list|>
name|byCommit
init|=
name|byCommit
argument_list|(
name|rw
argument_list|,
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|byCommit
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
comment|// Walk from all patch set SHA-1s, and terminate as soon as we've found
comment|// everything we're looking for. This is equivalent to just sorting the
comment|// list of commits by the RevWalk's configured order.
for|for
control|(
name|RevCommit
name|c
range|:
name|byCommit
operator|.
name|keySet
argument_list|()
control|)
block|{
name|rw
operator|.
name|markStart
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|int
name|expected
init|=
name|byCommit
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|found
init|=
literal|0
decl_stmt|;
name|RevCommit
name|c
decl_stmt|;
name|List
argument_list|<
name|PatchSetData
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|expected
argument_list|)
decl_stmt|;
while|while
condition|(
name|found
operator|<
name|expected
operator|&&
operator|(
name|c
operator|=
name|rw
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|Collection
argument_list|<
name|PatchSetData
argument_list|>
name|psds
init|=
name|byCommit
operator|.
name|get
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|psds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|found
operator|++
expr_stmt|;
for|for
control|(
name|PatchSetData
name|psd
range|:
name|psds
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|psd
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
block|}
DECL|method|byCommit (RevWalk rw, Collection<ChangeData> in)
specifier|private
name|Multimap
argument_list|<
name|RevCommit
argument_list|,
name|PatchSetData
argument_list|>
name|byCommit
parameter_list|(
name|RevWalk
name|rw
parameter_list|,
name|Collection
argument_list|<
name|ChangeData
argument_list|>
name|in
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
block|{
name|Multimap
argument_list|<
name|RevCommit
argument_list|,
name|PatchSetData
argument_list|>
name|byCommit
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|(
name|in
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|in
control|)
block|{
name|PatchSet
name|maxPs
init|=
literal|null
decl_stmt|;
for|for
control|(
name|PatchSet
name|ps
range|:
name|cd
operator|.
name|patchSets
argument_list|()
control|)
block|{
if|if
condition|(
name|shouldInclude
argument_list|(
name|ps
argument_list|)
operator|&&
operator|(
name|maxPs
operator|==
literal|null
operator|||
name|ps
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
operator|>
name|maxPs
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
operator|)
condition|)
block|{
name|maxPs
operator|=
name|ps
expr_stmt|;
block|}
block|}
if|if
condition|(
name|maxPs
operator|==
literal|null
condition|)
block|{
continue|continue;
comment|// No patch sets matched.
block|}
name|ObjectId
name|id
init|=
name|ObjectId
operator|.
name|fromString
argument_list|(
name|maxPs
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|RevCommit
name|c
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|byCommit
operator|.
name|put
argument_list|(
name|c
argument_list|,
name|PatchSetData
operator|.
name|create
argument_list|(
name|cd
argument_list|,
name|maxPs
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MissingObjectException
decl||
name|IncorrectObjectTypeException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"missing commit "
operator|+
name|id
operator|.
name|name
argument_list|()
operator|+
literal|" for patch set "
operator|+
name|maxPs
operator|.
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|byCommit
return|;
block|}
DECL|method|shouldInclude (PatchSet ps)
specifier|private
name|boolean
name|shouldInclude
parameter_list|(
name|PatchSet
name|ps
parameter_list|)
block|{
return|return
name|includePatchSets
operator|.
name|isEmpty
argument_list|()
operator|||
name|includePatchSets
operator|.
name|contains
argument_list|(
name|ps
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|AutoValue
DECL|class|PatchSetData
specifier|static
specifier|abstract
class|class
name|PatchSetData
block|{
annotation|@
name|VisibleForTesting
DECL|method|create (ChangeData cd, PatchSet ps, RevCommit commit)
specifier|static
name|PatchSetData
name|create
parameter_list|(
name|ChangeData
name|cd
parameter_list|,
name|PatchSet
name|ps
parameter_list|,
name|RevCommit
name|commit
parameter_list|)
block|{
return|return
operator|new
name|AutoValue_WalkSorter_PatchSetData
argument_list|(
name|cd
argument_list|,
name|ps
argument_list|,
name|commit
argument_list|)
return|;
block|}
DECL|method|data ()
specifier|abstract
name|ChangeData
name|data
parameter_list|()
function_decl|;
DECL|method|patchSet ()
specifier|abstract
name|PatchSet
name|patchSet
parameter_list|()
function_decl|;
DECL|method|commit ()
specifier|abstract
name|RevCommit
name|commit
parameter_list|()
function_decl|;
block|}
block|}
end_class

end_unit

