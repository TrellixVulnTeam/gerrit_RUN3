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
name|Change
operator|.
name|Status
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
name|strategy
operator|.
name|CommitMergeStatus
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

begin_class
DECL|class|RebaseSorter
specifier|public
class|class
name|RebaseSorter
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
name|RebaseSorter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rw
specifier|private
specifier|final
name|CodeReviewRevWalk
name|rw
decl_stmt|;
DECL|field|canMergeFlag
specifier|private
specifier|final
name|RevFlag
name|canMergeFlag
decl_stmt|;
DECL|field|initialTip
specifier|private
specifier|final
name|RevCommit
name|initialTip
decl_stmt|;
DECL|field|alreadyAccepted
specifier|private
specifier|final
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|alreadyAccepted
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
DECL|field|incoming
specifier|private
specifier|final
name|Set
argument_list|<
name|CodeReviewCommit
argument_list|>
name|incoming
decl_stmt|;
DECL|method|RebaseSorter ( CodeReviewRevWalk rw, RevCommit initialTip, Set<RevCommit> alreadyAccepted, RevFlag canMergeFlag, Provider<InternalChangeQuery> queryProvider, Set<CodeReviewCommit> incoming)
specifier|public
name|RebaseSorter
parameter_list|(
name|CodeReviewRevWalk
name|rw
parameter_list|,
name|RevCommit
name|initialTip
parameter_list|,
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|alreadyAccepted
parameter_list|,
name|RevFlag
name|canMergeFlag
parameter_list|,
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|,
name|Set
argument_list|<
name|CodeReviewCommit
argument_list|>
name|incoming
parameter_list|)
block|{
name|this
operator|.
name|rw
operator|=
name|rw
expr_stmt|;
name|this
operator|.
name|canMergeFlag
operator|=
name|canMergeFlag
expr_stmt|;
name|this
operator|.
name|initialTip
operator|=
name|initialTip
expr_stmt|;
name|this
operator|.
name|alreadyAccepted
operator|=
name|alreadyAccepted
expr_stmt|;
name|this
operator|.
name|queryProvider
operator|=
name|queryProvider
expr_stmt|;
name|this
operator|.
name|incoming
operator|=
name|incoming
expr_stmt|;
block|}
DECL|method|sort (Collection<CodeReviewCommit> toSort)
specifier|public
name|List
argument_list|<
name|CodeReviewCommit
argument_list|>
name|sort
parameter_list|(
name|Collection
argument_list|<
name|CodeReviewCommit
argument_list|>
name|toSort
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|CodeReviewCommit
argument_list|>
name|sorted
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|CodeReviewCommit
argument_list|>
name|sort
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|toSort
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|sort
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|CodeReviewCommit
name|n
init|=
name|removeOne
argument_list|(
name|sort
argument_list|)
decl_stmt|;
name|rw
operator|.
name|resetRetain
argument_list|(
name|canMergeFlag
argument_list|)
expr_stmt|;
name|rw
operator|.
name|markStart
argument_list|(
name|n
argument_list|)
expr_stmt|;
if|if
condition|(
name|initialTip
operator|!=
literal|null
condition|)
block|{
name|rw
operator|.
name|markUninteresting
argument_list|(
name|initialTip
argument_list|)
expr_stmt|;
block|}
name|CodeReviewCommit
name|c
decl_stmt|;
specifier|final
name|List
argument_list|<
name|CodeReviewCommit
argument_list|>
name|contents
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
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
if|if
condition|(
operator|!
name|c
operator|.
name|has
argument_list|(
name|canMergeFlag
argument_list|)
operator|||
operator|!
name|incoming
operator|.
name|contains
argument_list|(
name|c
argument_list|)
condition|)
block|{
if|if
condition|(
name|isAlreadyMerged
argument_list|(
name|c
argument_list|,
name|n
operator|.
name|change
argument_list|()
operator|.
name|getDest
argument_list|()
argument_list|)
condition|)
block|{
name|rw
operator|.
name|markUninteresting
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// We cannot merge n as it would bring something we
comment|// aren't permitted to merge at this time. Drop n.
comment|//
name|n
operator|.
name|setStatusCode
argument_list|(
name|CommitMergeStatus
operator|.
name|MISSING_DEPENDENCY
argument_list|)
expr_stmt|;
block|}
comment|// Stop RevWalk because c is either a merged commit or a missing
comment|// dependency. Not need to walk further.
break|break;
block|}
name|contents
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|n
operator|.
name|getStatusCode
argument_list|()
operator|==
name|CommitMergeStatus
operator|.
name|MISSING_DEPENDENCY
condition|)
block|{
continue|continue;
block|}
name|sort
operator|.
name|removeAll
argument_list|(
name|contents
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|reverse
argument_list|(
name|contents
argument_list|)
expr_stmt|;
name|sorted
operator|.
name|removeAll
argument_list|(
name|contents
argument_list|)
expr_stmt|;
name|sorted
operator|.
name|addAll
argument_list|(
name|contents
argument_list|)
expr_stmt|;
block|}
return|return
name|sorted
return|;
block|}
DECL|method|isAlreadyMerged (CodeReviewCommit commit, Branch.NameKey dest)
specifier|private
name|boolean
name|isAlreadyMerged
parameter_list|(
name|CodeReviewCommit
name|commit
parameter_list|,
name|Branch
operator|.
name|NameKey
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|CodeReviewRevWalk
name|mirw
init|=
name|CodeReviewCommit
operator|.
name|newRevWalk
argument_list|(
name|rw
operator|.
name|getObjectReader
argument_list|()
argument_list|)
init|)
block|{
name|mirw
operator|.
name|reset
argument_list|()
expr_stmt|;
name|mirw
operator|.
name|markStart
argument_list|(
name|commit
argument_list|)
expr_stmt|;
comment|// check if the commit is merged in other branches
for|for
control|(
name|RevCommit
name|accepted
range|:
name|alreadyAccepted
control|)
block|{
if|if
condition|(
name|mirw
operator|.
name|isMergedInto
argument_list|(
name|mirw
operator|.
name|parseCommit
argument_list|(
name|commit
argument_list|)
argument_list|,
name|mirw
operator|.
name|parseCommit
argument_list|(
name|accepted
argument_list|)
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Dependency {} merged into branch head {}."
argument_list|,
name|commit
operator|.
name|getName
argument_list|()
argument_list|,
name|accepted
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|// check if the commit associated change is merged in the same branch
name|List
argument_list|<
name|ChangeData
argument_list|>
name|changes
init|=
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|byCommit
argument_list|(
name|commit
argument_list|)
decl_stmt|;
for|for
control|(
name|ChangeData
name|change
range|:
name|changes
control|)
block|{
if|if
condition|(
name|change
operator|.
name|change
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|MERGED
operator|&&
name|change
operator|.
name|change
argument_list|()
operator|.
name|getDest
argument_list|()
operator|.
name|equals
argument_list|(
name|dest
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Dependency {} associated with merged change {}."
argument_list|,
name|commit
operator|.
name|getName
argument_list|()
argument_list|,
name|change
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
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
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|removeOne (Collection<T> c)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|removeOne
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|c
parameter_list|)
block|{
specifier|final
name|Iterator
argument_list|<
name|T
argument_list|>
name|i
init|=
name|c
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|final
name|T
name|r
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return
name|r
return|;
block|}
block|}
end_class

end_unit

