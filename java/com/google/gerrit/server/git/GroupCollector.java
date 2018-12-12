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
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|RevFlag
operator|.
name|UNINTERESTING
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
name|ListMultimap
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
name|MultimapBuilder
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
name|Multimaps
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
name|SetMultimap
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
name|Sets
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
name|SortedSetMultimap
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
name|PatchSetUtil
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
name|RevisionResource
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
name|notedb
operator|.
name|ChangeNotes
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
name|java
operator|.
name|util
operator|.
name|ArrayDeque
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
name|Deque
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|revwalk
operator|.
name|RevCommit
import|;
end_import

begin_comment
comment|/**  * Helper for assigning groups to commits during {@code ReceiveCommits}.  *  *<p>For each commit encountered along a walk between the branch tip and the tip of the push, the  * group of a commit is defined as follows:  *  *<ul>  *<li>If the commit is an existing patch set of a change, the group is read from the group field  *       in the corresponding {@link PatchSet} record.  *<li>If all of a commit's parents are merged into the branch, then its group is its own SHA-1.  *<li>If the commit has a single parent that is not yet merged into the branch, then its group is  *       the same as the parent's group.  *<li>  *<li>For a merge commit, choose a parent and use that parent's group. If one of the parents has  *       a group from a patch set, use that group, otherwise, use the group from the first parent.  *       In addition to setting this merge commit's group, use the chosen group for all commits that  *       would otherwise use a group from the parents that were not chosen.  *<li>If a merge commit has multiple parents whose group comes from separate patch sets,  *       concatenate the groups from those parents together. This indicates two side branches were  *       pushed separately, followed by the merge.  *<li>  *</ul>  *  *<p>Callers must call {@link #visit(RevCommit)} on all commits between the current branch tip and  * the tip of a push, in reverse topo order (parents before children). Once all commits have been  * visited, call {@link #getGroups()} for the result.  */
end_comment

begin_class
DECL|class|GroupCollector
specifier|public
class|class
name|GroupCollector
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
DECL|method|getDefaultGroups (PatchSet ps)
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getDefaultGroups
parameter_list|(
name|PatchSet
name|ps
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
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
DECL|method|getDefaultGroups (ObjectId commit)
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getDefaultGroups
parameter_list|(
name|ObjectId
name|commit
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|commit
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getGroups (RevisionResource rsrc)
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getGroups
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|)
block|{
if|if
condition|(
name|rsrc
operator|.
name|getEdit
argument_list|()
operator|.
name|isPresent
argument_list|()
condition|)
block|{
comment|// Groups for an edit are just the base revision's groups, since they have
comment|// the same parent.
return|return
name|rsrc
operator|.
name|getEdit
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getBasePatchSet
argument_list|()
operator|.
name|getGroups
argument_list|()
return|;
block|}
return|return
name|rsrc
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getGroups
argument_list|()
return|;
block|}
DECL|interface|Lookup
specifier|private
interface|interface
name|Lookup
block|{
DECL|method|lookup (PatchSet.Id psId)
name|List
argument_list|<
name|String
argument_list|>
name|lookup
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
throws|throws
name|OrmException
function_decl|;
block|}
DECL|field|patchSetsBySha
specifier|private
specifier|final
name|ListMultimap
argument_list|<
name|ObjectId
argument_list|,
name|PatchSet
operator|.
name|Id
argument_list|>
name|patchSetsBySha
decl_stmt|;
DECL|field|groups
specifier|private
specifier|final
name|ListMultimap
argument_list|<
name|ObjectId
argument_list|,
name|String
argument_list|>
name|groups
decl_stmt|;
DECL|field|groupAliases
specifier|private
specifier|final
name|SetMultimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|groupAliases
decl_stmt|;
DECL|field|groupLookup
specifier|private
specifier|final
name|Lookup
name|groupLookup
decl_stmt|;
DECL|field|done
specifier|private
name|boolean
name|done
decl_stmt|;
DECL|method|create ( ListMultimap<ObjectId, Ref> changeRefsById, PatchSetUtil psUtil, ChangeNotes.Factory notesFactory, Project.NameKey project)
specifier|public
specifier|static
name|GroupCollector
name|create
parameter_list|(
name|ListMultimap
argument_list|<
name|ObjectId
argument_list|,
name|Ref
argument_list|>
name|changeRefsById
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
block|{
return|return
operator|new
name|GroupCollector
argument_list|(
name|transformRefs
argument_list|(
name|changeRefsById
argument_list|)
argument_list|,
operator|new
name|Lookup
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|lookup
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
throws|throws
name|OrmException
block|{
comment|// TODO(dborowitz): Reuse open repository from caller.
name|ChangeNotes
name|notes
init|=
name|notesFactory
operator|.
name|createChecked
argument_list|(
name|project
argument_list|,
name|psId
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
name|PatchSet
name|ps
init|=
name|psUtil
operator|.
name|get
argument_list|(
name|notes
argument_list|,
name|psId
argument_list|)
decl_stmt|;
return|return
name|ps
operator|!=
literal|null
condition|?
name|ps
operator|.
name|getGroups
argument_list|()
else|:
literal|null
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|createForSchemaUpgradeOnly ( ListMultimap<ObjectId, Ref> changeRefsById, ReviewDb db)
specifier|public
specifier|static
name|GroupCollector
name|createForSchemaUpgradeOnly
parameter_list|(
name|ListMultimap
argument_list|<
name|ObjectId
argument_list|,
name|Ref
argument_list|>
name|changeRefsById
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
block|{
return|return
operator|new
name|GroupCollector
argument_list|(
name|transformRefs
argument_list|(
name|changeRefsById
argument_list|)
argument_list|,
operator|new
name|Lookup
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|lookup
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
throws|throws
name|OrmException
block|{
name|PatchSet
name|ps
init|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|psId
argument_list|)
decl_stmt|;
return|return
name|ps
operator|!=
literal|null
condition|?
name|ps
operator|.
name|getGroups
argument_list|()
else|:
literal|null
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|GroupCollector (ListMultimap<ObjectId, PatchSet.Id> patchSetsBySha, Lookup groupLookup)
specifier|private
name|GroupCollector
parameter_list|(
name|ListMultimap
argument_list|<
name|ObjectId
argument_list|,
name|PatchSet
operator|.
name|Id
argument_list|>
name|patchSetsBySha
parameter_list|,
name|Lookup
name|groupLookup
parameter_list|)
block|{
name|this
operator|.
name|patchSetsBySha
operator|=
name|patchSetsBySha
expr_stmt|;
name|this
operator|.
name|groupLookup
operator|=
name|groupLookup
expr_stmt|;
name|groups
operator|=
name|MultimapBuilder
operator|.
name|hashKeys
argument_list|()
operator|.
name|arrayListValues
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
name|groupAliases
operator|=
name|MultimapBuilder
operator|.
name|hashKeys
argument_list|()
operator|.
name|hashSetValues
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|transformRefs ( ListMultimap<ObjectId, Ref> refs)
specifier|private
specifier|static
name|ListMultimap
argument_list|<
name|ObjectId
argument_list|,
name|PatchSet
operator|.
name|Id
argument_list|>
name|transformRefs
parameter_list|(
name|ListMultimap
argument_list|<
name|ObjectId
argument_list|,
name|Ref
argument_list|>
name|refs
parameter_list|)
block|{
return|return
name|Multimaps
operator|.
name|transformValues
argument_list|(
name|refs
argument_list|,
name|r
lambda|->
name|PatchSet
operator|.
name|Id
operator|.
name|fromRef
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|GroupCollector ( ListMultimap<ObjectId, PatchSet.Id> patchSetsBySha, ListMultimap<PatchSet.Id, String> groupLookup)
name|GroupCollector
parameter_list|(
name|ListMultimap
argument_list|<
name|ObjectId
argument_list|,
name|PatchSet
operator|.
name|Id
argument_list|>
name|patchSetsBySha
parameter_list|,
name|ListMultimap
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|,
name|String
argument_list|>
name|groupLookup
parameter_list|)
block|{
name|this
argument_list|(
name|patchSetsBySha
argument_list|,
operator|new
name|Lookup
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|lookup
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
name|groupLookup
operator|.
name|get
argument_list|(
name|psId
argument_list|)
decl_stmt|;
return|return
operator|!
name|groups
operator|.
name|isEmpty
argument_list|()
condition|?
name|groups
else|:
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|visit (RevCommit c)
specifier|public
name|void
name|visit
parameter_list|(
name|RevCommit
name|c
parameter_list|)
block|{
name|checkState
argument_list|(
operator|!
name|done
argument_list|,
literal|"visit() called after getGroups()"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|interestingParents
init|=
name|getInterestingParents
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|interestingParents
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// All parents are uninteresting: treat this commit as the root of a new
comment|// group of related changes.
name|groups
operator|.
name|put
argument_list|(
name|c
argument_list|,
name|c
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|interestingParents
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// Only one parent is new in this push. If it is the only parent, just use
comment|// that parent's group. If there are multiple parents, perhaps this commit
comment|// is a merge of a side branch. This commit belongs in that parent's group
comment|// in that case.
name|groups
operator|.
name|putAll
argument_list|(
name|c
argument_list|,
name|groups
operator|.
name|get
argument_list|(
name|interestingParents
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Multiple parents, merging at least two branches containing new commits in
comment|// this push.
name|Set
argument_list|<
name|String
argument_list|>
name|thisCommitGroups
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|parentGroupsNewInThisPush
init|=
name|Sets
operator|.
name|newLinkedHashSetWithExpectedSize
argument_list|(
name|interestingParents
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RevCommit
name|p
range|:
name|interestingParents
control|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|parentGroups
init|=
name|groups
operator|.
name|get
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentGroups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"no group assigned to parent %s of commit %s"
argument_list|,
name|p
operator|.
name|name
argument_list|()
argument_list|,
name|c
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
for|for
control|(
name|String
name|parentGroup
range|:
name|parentGroups
control|)
block|{
if|if
condition|(
name|isGroupFromExistingPatchSet
argument_list|(
name|p
argument_list|,
name|parentGroup
argument_list|)
condition|)
block|{
comment|// This parent's group is from an existing patch set, i.e. the parent
comment|// not new in this push. Use this group for the commit.
name|thisCommitGroups
operator|.
name|add
argument_list|(
name|parentGroup
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// This parent's group is new in this push.
name|parentGroupsNewInThisPush
operator|.
name|add
argument_list|(
name|parentGroup
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Iterable
argument_list|<
name|String
argument_list|>
name|toAlias
decl_stmt|;
if|if
condition|(
name|thisCommitGroups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// All parent groups were new in this push. Pick the first one and alias
comment|// other parents' groups to this first parent.
name|String
name|firstParentGroup
init|=
name|parentGroupsNewInThisPush
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|thisCommitGroups
operator|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|firstParentGroup
argument_list|)
expr_stmt|;
name|toAlias
operator|=
name|Iterables
operator|.
name|skip
argument_list|(
name|parentGroupsNewInThisPush
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// For each parent group that was new in this push, alias it to the actual
comment|// computed group(s) for this commit.
name|toAlias
operator|=
name|parentGroupsNewInThisPush
expr_stmt|;
block|}
name|groups
operator|.
name|putAll
argument_list|(
name|c
argument_list|,
name|thisCommitGroups
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|pg
range|:
name|toAlias
control|)
block|{
name|groupAliases
operator|.
name|putAll
argument_list|(
name|pg
argument_list|,
name|thisCommitGroups
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getGroups ()
specifier|public
name|SortedSetMultimap
argument_list|<
name|ObjectId
argument_list|,
name|String
argument_list|>
name|getGroups
parameter_list|()
throws|throws
name|OrmException
block|{
name|done
operator|=
literal|true
expr_stmt|;
name|SortedSetMultimap
argument_list|<
name|ObjectId
argument_list|,
name|String
argument_list|>
name|result
init|=
name|MultimapBuilder
operator|.
name|hashKeys
argument_list|(
name|groups
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|treeSetValues
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ObjectId
argument_list|,
name|Collection
argument_list|<
name|String
argument_list|>
argument_list|>
name|e
range|:
name|groups
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ObjectId
name|id
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|result
operator|.
name|putAll
argument_list|(
name|id
operator|.
name|copy
argument_list|()
argument_list|,
name|resolveGroups
argument_list|(
name|id
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|getInterestingParents (RevCommit commit)
specifier|private
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|getInterestingParents
parameter_list|(
name|RevCommit
name|commit
parameter_list|)
block|{
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|result
init|=
name|Sets
operator|.
name|newLinkedHashSetWithExpectedSize
argument_list|(
name|commit
operator|.
name|getParentCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RevCommit
name|p
range|:
name|commit
operator|.
name|getParents
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|p
operator|.
name|has
argument_list|(
name|UNINTERESTING
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|isGroupFromExistingPatchSet (RevCommit commit, String group)
specifier|private
name|boolean
name|isGroupFromExistingPatchSet
parameter_list|(
name|RevCommit
name|commit
parameter_list|,
name|String
name|group
parameter_list|)
block|{
name|ObjectId
name|id
init|=
name|parseGroup
argument_list|(
name|commit
argument_list|,
name|group
argument_list|)
decl_stmt|;
return|return
name|id
operator|!=
literal|null
operator|&&
name|patchSetsBySha
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
return|;
block|}
DECL|method|resolveGroups (ObjectId forCommit, Collection<String> candidates)
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|resolveGroups
parameter_list|(
name|ObjectId
name|forCommit
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|candidates
parameter_list|)
throws|throws
name|OrmException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|actual
init|=
name|Sets
operator|.
name|newTreeSet
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|done
init|=
name|Sets
operator|.
name|newHashSetWithExpectedSize
argument_list|(
name|candidates
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|seen
init|=
name|Sets
operator|.
name|newHashSetWithExpectedSize
argument_list|(
name|candidates
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Deque
argument_list|<
name|String
argument_list|>
name|todo
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|(
name|candidates
argument_list|)
decl_stmt|;
comment|// BFS through all aliases to find groups that are not aliased to anything
comment|// else.
while|while
condition|(
operator|!
name|todo
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|g
init|=
name|todo
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|seen
operator|.
name|add
argument_list|(
name|g
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|aliases
init|=
name|groupAliases
operator|.
name|get
argument_list|(
name|g
argument_list|)
decl_stmt|;
if|if
condition|(
name|aliases
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|done
operator|.
name|contains
argument_list|(
name|g
argument_list|)
condition|)
block|{
name|Iterables
operator|.
name|addAll
argument_list|(
name|actual
argument_list|,
name|resolveGroup
argument_list|(
name|forCommit
argument_list|,
name|g
argument_list|)
argument_list|)
expr_stmt|;
name|done
operator|.
name|add
argument_list|(
name|g
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|todo
operator|.
name|addAll
argument_list|(
name|aliases
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|actual
return|;
block|}
DECL|method|parseGroup (ObjectId forCommit, String group)
specifier|private
name|ObjectId
name|parseGroup
parameter_list|(
name|ObjectId
name|forCommit
parameter_list|,
name|String
name|group
parameter_list|)
block|{
try|try
block|{
return|return
name|ObjectId
operator|.
name|fromString
argument_list|(
name|group
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// Shouldn't happen; some sort of corruption or manual tinkering?
name|logger
operator|.
name|atWarning
argument_list|()
operator|.
name|log
argument_list|(
literal|"group for commit %s is not a SHA-1: %s"
argument_list|,
name|forCommit
operator|.
name|name
argument_list|()
argument_list|,
name|group
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|resolveGroup (ObjectId forCommit, String group)
specifier|private
name|Iterable
argument_list|<
name|String
argument_list|>
name|resolveGroup
parameter_list|(
name|ObjectId
name|forCommit
parameter_list|,
name|String
name|group
parameter_list|)
throws|throws
name|OrmException
block|{
name|ObjectId
name|id
init|=
name|parseGroup
argument_list|(
name|forCommit
argument_list|,
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|PatchSet
operator|.
name|Id
name|psId
init|=
name|Iterables
operator|.
name|getFirst
argument_list|(
name|patchSetsBySha
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|psId
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
name|groupLookup
operator|.
name|lookup
argument_list|(
name|psId
argument_list|)
decl_stmt|;
comment|// Group for existing patch set may be missing, e.g. if group has not
comment|// been migrated yet.
if|if
condition|(
name|groups
operator|!=
literal|null
operator|&&
operator|!
name|groups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|groups
return|;
block|}
block|}
block|}
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|group
argument_list|)
return|;
block|}
block|}
end_class

end_unit

