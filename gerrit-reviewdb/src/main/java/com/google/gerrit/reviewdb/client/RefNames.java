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
DECL|package|com.google.gerrit.reviewdb.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|client
package|;
end_package

begin_comment
comment|/** Constants and utilities for Gerrit-specific ref names. */
end_comment

begin_class
DECL|class|RefNames
specifier|public
class|class
name|RefNames
block|{
DECL|field|REFS
specifier|public
specifier|static
specifier|final
name|String
name|REFS
init|=
literal|"refs/"
decl_stmt|;
DECL|field|REFS_HEADS
specifier|public
specifier|static
specifier|final
name|String
name|REFS_HEADS
init|=
literal|"refs/heads/"
decl_stmt|;
DECL|field|REFS_TAGS
specifier|public
specifier|static
specifier|final
name|String
name|REFS_TAGS
init|=
literal|"refs/tags/"
decl_stmt|;
DECL|field|REFS_CHANGES
specifier|public
specifier|static
specifier|final
name|String
name|REFS_CHANGES
init|=
literal|"refs/changes/"
decl_stmt|;
comment|/** Note tree listing commits we refuse {@code refs/meta/reject-commits} */
DECL|field|REFS_REJECT_COMMITS
specifier|public
specifier|static
specifier|final
name|String
name|REFS_REJECT_COMMITS
init|=
literal|"refs/meta/reject-commits"
decl_stmt|;
comment|/** Configuration settings for a project {@code refs/meta/config} */
DECL|field|REFS_CONFIG
specifier|public
specifier|static
specifier|final
name|String
name|REFS_CONFIG
init|=
literal|"refs/meta/config"
decl_stmt|;
comment|/** Preference settings for a user {@code refs/users} */
DECL|field|REFS_USERS
specifier|public
specifier|static
specifier|final
name|String
name|REFS_USERS
init|=
literal|"refs/users/"
decl_stmt|;
comment|/** Magic user branch in All-Users {@code refs/users/self} */
DECL|field|REFS_USERS_SELF
specifier|public
specifier|static
specifier|final
name|String
name|REFS_USERS_SELF
init|=
literal|"refs/users/self"
decl_stmt|;
comment|/** Default user preference settings */
DECL|field|REFS_USERS_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|REFS_USERS_DEFAULT
init|=
name|RefNames
operator|.
name|REFS_USERS
operator|+
literal|"default"
decl_stmt|;
comment|/** Configurations of project-specific dashboards (canned search queries). */
DECL|field|REFS_DASHBOARDS
specifier|public
specifier|static
specifier|final
name|String
name|REFS_DASHBOARDS
init|=
literal|"refs/meta/dashboards/"
decl_stmt|;
comment|/** Draft inline comments of a user on a change */
DECL|field|REFS_DRAFT_COMMENTS
specifier|public
specifier|static
specifier|final
name|String
name|REFS_DRAFT_COMMENTS
init|=
literal|"refs/draft-comments/"
decl_stmt|;
comment|/** A change starred by a user */
DECL|field|REFS_STARRED_CHANGES
specifier|public
specifier|static
specifier|final
name|String
name|REFS_STARRED_CHANGES
init|=
literal|"refs/starred-changes/"
decl_stmt|;
comment|/** Sequence counters in NoteDb. */
DECL|field|REFS_SEQUENCES
specifier|public
specifier|static
specifier|final
name|String
name|REFS_SEQUENCES
init|=
literal|"refs/sequences/"
decl_stmt|;
comment|/**    * Prefix applied to merge commit base nodes.    *<p>    * References in this directory should take the form    * {@code refs/cache-automerge/xx/yyyy...} where xx is    * the first two digits of the merge commit's object    * name, and yyyyy... is the remaining 38. The reference    * should point to a treeish that is the automatic merge    * result of the merge commit's parents.    */
DECL|field|REFS_CACHE_AUTOMERGE
specifier|public
specifier|static
specifier|final
name|String
name|REFS_CACHE_AUTOMERGE
init|=
literal|"refs/cache-automerge/"
decl_stmt|;
comment|/** Suffix of a meta ref in the NoteDb. */
DECL|field|META_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|META_SUFFIX
init|=
literal|"/meta"
decl_stmt|;
DECL|field|EDIT_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|EDIT_PREFIX
init|=
literal|"edit-"
decl_stmt|;
DECL|method|fullName (String ref)
specifier|public
specifier|static
name|String
name|fullName
parameter_list|(
name|String
name|ref
parameter_list|)
block|{
return|return
name|ref
operator|.
name|startsWith
argument_list|(
name|REFS
argument_list|)
condition|?
name|ref
else|:
name|REFS_HEADS
operator|+
name|ref
return|;
block|}
DECL|method|shortName (String ref)
specifier|public
specifier|static
specifier|final
name|String
name|shortName
parameter_list|(
name|String
name|ref
parameter_list|)
block|{
if|if
condition|(
name|ref
operator|.
name|startsWith
argument_list|(
name|REFS_HEADS
argument_list|)
condition|)
block|{
return|return
name|ref
operator|.
name|substring
argument_list|(
name|REFS_HEADS
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|ref
operator|.
name|startsWith
argument_list|(
name|REFS_TAGS
argument_list|)
condition|)
block|{
return|return
name|ref
operator|.
name|substring
argument_list|(
name|REFS_TAGS
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
return|return
name|ref
return|;
block|}
DECL|method|changeMetaRef (Change.Id id)
specifier|public
specifier|static
name|String
name|changeMetaRef
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|)
block|{
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
name|REFS_CHANGES
argument_list|)
expr_stmt|;
name|int
name|n
init|=
name|id
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|m
init|=
name|n
operator|%
literal|100
decl_stmt|;
if|if
condition|(
name|m
operator|<
literal|10
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|append
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|META_SUFFIX
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|refsUsers (Account.Id accountId)
specifier|public
specifier|static
name|String
name|refsUsers
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
name|REFS_USERS
argument_list|)
expr_stmt|;
name|int
name|account
init|=
name|accountId
operator|.
name|get
argument_list|()
decl_stmt|;
name|int
name|m
init|=
name|account
operator|%
literal|100
decl_stmt|;
if|if
condition|(
name|m
operator|<
literal|10
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|append
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|account
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|refsDraftComments (Change.Id changeId, Account.Id accountId)
specifier|public
specifier|static
name|String
name|refsDraftComments
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
name|StringBuilder
name|r
init|=
name|buildRefsPrefix
argument_list|(
name|REFS_DRAFT_COMMENTS
argument_list|,
name|changeId
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
name|accountId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|refsDraftCommentsPrefix (Change.Id changeId)
specifier|public
specifier|static
name|String
name|refsDraftCommentsPrefix
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|)
block|{
return|return
name|buildRefsPrefix
argument_list|(
name|REFS_DRAFT_COMMENTS
argument_list|,
name|changeId
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|refsStarredChanges (Change.Id changeId, Account.Id accountId)
specifier|public
specifier|static
name|String
name|refsStarredChanges
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
name|StringBuilder
name|r
init|=
name|buildRefsPrefix
argument_list|(
name|REFS_STARRED_CHANGES
argument_list|,
name|changeId
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
name|accountId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|refsStarredChangesPrefix (Change.Id changeId)
specifier|public
specifier|static
name|String
name|refsStarredChangesPrefix
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|)
block|{
return|return
name|buildRefsPrefix
argument_list|(
name|REFS_STARRED_CHANGES
argument_list|,
name|changeId
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|buildRefsPrefix (String prefix, int id)
specifier|private
specifier|static
name|StringBuilder
name|buildRefsPrefix
parameter_list|(
name|String
name|prefix
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|int
name|n
init|=
name|id
operator|%
literal|100
decl_stmt|;
if|if
condition|(
name|n
operator|<
literal|10
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|append
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
comment|/**    * Returns reference for this change edit with sharded user and change number:    * refs/users/UU/UUUU/edit-CCCC/P.    *    * @param accountId account id    * @param changeId change number    * @param psId patch set number    * @return reference for this change edit    */
DECL|method|refsEdit (Account.Id accountId, Change.Id changeId, PatchSet.Id psId)
specifier|public
specifier|static
name|String
name|refsEdit
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
block|{
return|return
name|refsEditPrefix
argument_list|(
name|accountId
argument_list|,
name|changeId
argument_list|)
operator|+
name|psId
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Returns reference prefix for this change edit with sharded user and    * change number: refs/users/UU/UUUU/edit-CCCC/.    *    * @param accountId account id    * @param changeId change number    * @return reference prefix for this change edit    */
DECL|method|refsEditPrefix (Account.Id accountId, Change.Id changeId)
specifier|public
specifier|static
name|String
name|refsEditPrefix
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|Change
operator|.
name|Id
name|changeId
parameter_list|)
block|{
return|return
operator|new
name|StringBuilder
argument_list|(
name|refsUsers
argument_list|(
name|accountId
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|EDIT_PREFIX
argument_list|)
operator|.
name|append
argument_list|(
name|changeId
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|parseShardedRefPart (String name)
specifier|static
name|Integer
name|parseShardedRefPart
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
index|[]
name|parts
init|=
name|name
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|int
name|n
init|=
name|parts
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|n
operator|<
literal|2
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Last 2 digits.
name|int
name|le
decl_stmt|;
for|for
control|(
name|le
operator|=
literal|0
init|;
name|le
operator|<
name|parts
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
condition|;
name|le
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|Character
operator|.
name|isDigit
argument_list|(
name|parts
index|[
literal|0
index|]
operator|.
name|charAt
argument_list|(
name|le
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
if|if
condition|(
name|le
operator|!=
literal|2
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Full ID.
name|int
name|ie
decl_stmt|;
for|for
control|(
name|ie
operator|=
literal|0
init|;
name|ie
operator|<
name|parts
index|[
literal|1
index|]
operator|.
name|length
argument_list|()
condition|;
name|ie
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|Character
operator|.
name|isDigit
argument_list|(
name|parts
index|[
literal|1
index|]
operator|.
name|charAt
argument_list|(
name|ie
argument_list|)
argument_list|)
condition|)
block|{
if|if
condition|(
name|ie
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
name|int
name|shard
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|int
name|id
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
literal|1
index|]
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|ie
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|%
literal|100
operator|!=
name|shard
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|id
return|;
block|}
DECL|method|parseRefSuffix (String name)
specifier|static
name|Integer
name|parseRefSuffix
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|i
init|=
name|name
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|char
name|c
init|=
name|name
operator|.
name|charAt
argument_list|(
name|i
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'/'
condition|)
block|{
break|break;
block|}
elseif|else
if|if
condition|(
operator|!
name|Character
operator|.
name|isDigit
argument_list|(
name|c
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|i
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|name
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|RefNames ()
specifier|private
name|RefNames
parameter_list|()
block|{   }
block|}
end_class

end_unit

