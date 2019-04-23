begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
name|MoreObjects
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
name|client
operator|.
name|RefNames
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
name|cache
operator|.
name|proto
operator|.
name|Cache
operator|.
name|TagSetHolderProto
operator|.
name|TagSetProto
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
name|cache
operator|.
name|proto
operator|.
name|Cache
operator|.
name|TagSetHolderProto
operator|.
name|TagSetProto
operator|.
name|CachedRefProto
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
name|cache
operator|.
name|proto
operator|.
name|Cache
operator|.
name|TagSetHolderProto
operator|.
name|TagSetProto
operator|.
name|TagProto
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
name|cache
operator|.
name|serialize
operator|.
name|ObjectIdConverter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ByteString
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
name|BitSet
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|ObjectIdOwnerMap
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

begin_class
DECL|class|TagSet
class|class
name|TagSet
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
DECL|field|projectName
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|projectName
decl_stmt|;
DECL|field|refs
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CachedRef
argument_list|>
name|refs
decl_stmt|;
DECL|field|tags
specifier|private
specifier|final
name|ObjectIdOwnerMap
argument_list|<
name|Tag
argument_list|>
name|tags
decl_stmt|;
DECL|method|TagSet (Project.NameKey projectName)
name|TagSet
parameter_list|(
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|)
block|{
name|this
argument_list|(
name|projectName
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
operator|new
name|ObjectIdOwnerMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|TagSet (Project.NameKey projectName, HashMap<String, CachedRef> refs, ObjectIdOwnerMap<Tag> tags)
name|TagSet
parameter_list|(
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|CachedRef
argument_list|>
name|refs
parameter_list|,
name|ObjectIdOwnerMap
argument_list|<
name|Tag
argument_list|>
name|tags
parameter_list|)
block|{
name|this
operator|.
name|projectName
operator|=
name|projectName
expr_stmt|;
name|this
operator|.
name|refs
operator|=
name|refs
expr_stmt|;
name|this
operator|.
name|tags
operator|=
name|tags
expr_stmt|;
block|}
DECL|method|getProjectName ()
name|Project
operator|.
name|NameKey
name|getProjectName
parameter_list|()
block|{
return|return
name|projectName
return|;
block|}
DECL|method|lookupTag (AnyObjectId id)
name|Tag
name|lookupTag
parameter_list|(
name|AnyObjectId
name|id
parameter_list|)
block|{
return|return
name|tags
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|// Test methods have obtuse names in addition to annotations, since they expose mutable state
comment|// which would be easy to corrupt.
annotation|@
name|VisibleForTesting
DECL|method|getRefsForTesting ()
name|Map
argument_list|<
name|String
argument_list|,
name|CachedRef
argument_list|>
name|getRefsForTesting
parameter_list|()
block|{
return|return
name|refs
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getTagsForTesting ()
name|ObjectIdOwnerMap
argument_list|<
name|Tag
argument_list|>
name|getTagsForTesting
parameter_list|()
block|{
return|return
name|tags
return|;
block|}
DECL|method|updateFastForward (String refName, ObjectId oldValue, ObjectId newValue)
name|boolean
name|updateFastForward
parameter_list|(
name|String
name|refName
parameter_list|,
name|ObjectId
name|oldValue
parameter_list|,
name|ObjectId
name|newValue
parameter_list|)
block|{
name|CachedRef
name|ref
init|=
name|refs
operator|.
name|get
argument_list|(
name|refName
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
comment|// compareAndSet works on reference equality, but this operation
comment|// wants to use object equality. Switch out oldValue with cur so the
comment|// compareAndSet will function correctly for this operation.
comment|//
name|ObjectId
name|cur
init|=
name|ref
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|cur
operator|.
name|equals
argument_list|(
name|oldValue
argument_list|)
condition|)
block|{
return|return
name|ref
operator|.
name|compareAndSet
argument_list|(
name|cur
argument_list|,
name|newValue
argument_list|)
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|prepare (TagMatcher m)
name|void
name|prepare
parameter_list|(
name|TagMatcher
name|m
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
name|RevWalk
name|rw
init|=
literal|null
decl_stmt|;
try|try
block|{
for|for
control|(
name|Ref
name|currentRef
range|:
name|m
operator|.
name|include
control|)
block|{
if|if
condition|(
name|currentRef
operator|.
name|isSymbolic
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|currentRef
operator|.
name|getObjectId
argument_list|()
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|CachedRef
name|savedRef
init|=
name|refs
operator|.
name|get
argument_list|(
name|currentRef
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|savedRef
operator|==
literal|null
condition|)
block|{
comment|// If the reference isn't known to the set, return null
comment|// and force the caller to rebuild the set in a new copy.
name|m
operator|.
name|newRefs
operator|.
name|add
argument_list|(
name|currentRef
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// The reference has not been moved. It can be used as-is.
name|ObjectId
name|savedObjectId
init|=
name|savedRef
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentRef
operator|.
name|getObjectId
argument_list|()
operator|.
name|equals
argument_list|(
name|savedObjectId
argument_list|)
condition|)
block|{
name|m
operator|.
name|mask
operator|.
name|set
argument_list|(
name|savedRef
operator|.
name|flag
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// Check on-the-fly to see if the branch still reaches the tag.
comment|// This is very likely for a branch that fast-forwarded.
try|try
block|{
if|if
condition|(
name|rw
operator|==
literal|null
condition|)
block|{
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|m
operator|.
name|db
argument_list|)
expr_stmt|;
name|rw
operator|.
name|setRetainBody
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|RevCommit
name|savedCommit
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|savedObjectId
argument_list|)
decl_stmt|;
name|RevCommit
name|currentCommit
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|currentRef
operator|.
name|getObjectId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rw
operator|.
name|isMergedInto
argument_list|(
name|savedCommit
argument_list|,
name|currentCommit
argument_list|)
condition|)
block|{
comment|// Fast-forward. Safely update the reference in-place.
name|savedRef
operator|.
name|compareAndSet
argument_list|(
name|savedObjectId
argument_list|,
name|currentRef
operator|.
name|getObjectId
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|mask
operator|.
name|set
argument_list|(
name|savedRef
operator|.
name|flag
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// The branch rewound. Walk the list of commits removed from
comment|// the reference. If any matches to a tag, this has to be removed.
name|boolean
name|err
init|=
literal|false
decl_stmt|;
name|rw
operator|.
name|reset
argument_list|()
expr_stmt|;
name|rw
operator|.
name|markStart
argument_list|(
name|savedCommit
argument_list|)
expr_stmt|;
name|rw
operator|.
name|markUninteresting
argument_list|(
name|currentCommit
argument_list|)
expr_stmt|;
name|rw
operator|.
name|sort
argument_list|(
name|RevSort
operator|.
name|TOPO
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|RevCommit
name|c
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
name|Tag
name|tag
init|=
name|tags
operator|.
name|get
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|tag
operator|!=
literal|null
operator|&&
name|tag
operator|.
name|refFlags
operator|.
name|get
argument_list|(
name|savedRef
operator|.
name|flag
argument_list|)
condition|)
block|{
name|m
operator|.
name|lostRefs
operator|.
name|add
argument_list|(
operator|new
name|TagMatcher
operator|.
name|LostRef
argument_list|(
name|tag
argument_list|,
name|savedRef
operator|.
name|flag
argument_list|)
argument_list|)
expr_stmt|;
name|err
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|err
condition|)
block|{
comment|// All of the tags are still reachable. Update in-place.
name|savedRef
operator|.
name|compareAndSet
argument_list|(
name|savedObjectId
argument_list|,
name|currentRef
operator|.
name|getObjectId
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|mask
operator|.
name|set
argument_list|(
name|savedRef
operator|.
name|flag
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|err
parameter_list|)
block|{
comment|// Defer a cache update until later. No conclusion can be made
comment|// based on an exception reading from the repository storage.
name|logger
operator|.
name|atWarning
argument_list|()
operator|.
name|withCause
argument_list|(
name|err
argument_list|)
operator|.
name|log
argument_list|(
literal|"Error checking tags of %s"
argument_list|,
name|projectName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|rw
operator|!=
literal|null
condition|)
block|{
name|rw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|build (Repository git, TagSet old, TagMatcher m)
name|void
name|build
parameter_list|(
name|Repository
name|git
parameter_list|,
name|TagSet
name|old
parameter_list|,
name|TagMatcher
name|m
parameter_list|)
block|{
if|if
condition|(
name|old
operator|!=
literal|null
operator|&&
name|m
operator|!=
literal|null
operator|&&
name|refresh
argument_list|(
name|old
argument_list|,
name|m
argument_list|)
condition|)
block|{
return|return;
block|}
try|try
init|(
name|TagWalk
name|rw
init|=
operator|new
name|TagWalk
argument_list|(
name|git
argument_list|)
init|)
block|{
name|rw
operator|.
name|setRetainBody
argument_list|(
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|Ref
name|ref
range|:
name|git
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|getRefs
argument_list|()
control|)
block|{
if|if
condition|(
name|skip
argument_list|(
name|ref
argument_list|)
condition|)
block|{
continue|continue;
block|}
elseif|else
if|if
condition|(
name|isTag
argument_list|(
name|ref
argument_list|)
condition|)
block|{
comment|// For a tag, remember where it points to.
try|try
block|{
name|addTag
argument_list|(
name|rw
argument_list|,
name|git
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|peel
argument_list|(
name|ref
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|addTag
argument_list|(
name|rw
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// New reference to include in the set.
name|addRef
argument_list|(
name|rw
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Traverse the complete history. Copy any flags from a commit to
comment|// all of its ancestors. This automatically updates any Tag object
comment|// as the TagCommit and the stored Tag object share the same
comment|// underlying bit set.
name|TagCommit
name|c
decl_stmt|;
while|while
condition|(
operator|(
name|c
operator|=
operator|(
name|TagCommit
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
name|BitSet
name|mine
init|=
name|c
operator|.
name|refFlags
decl_stmt|;
name|int
name|pCnt
init|=
name|c
operator|.
name|getParentCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|pIdx
init|=
literal|0
init|;
name|pIdx
operator|<
name|pCnt
condition|;
name|pIdx
operator|++
control|)
block|{
operator|(
operator|(
name|TagCommit
operator|)
name|c
operator|.
name|getParent
argument_list|(
name|pIdx
argument_list|)
operator|)
operator|.
name|refFlags
operator|.
name|or
argument_list|(
name|mine
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
name|logger
operator|.
name|atWarning
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Error building tags for repository %s"
argument_list|,
name|projectName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fromProto (TagSetProto proto)
specifier|static
name|TagSet
name|fromProto
parameter_list|(
name|TagSetProto
name|proto
parameter_list|)
block|{
name|ObjectIdConverter
name|idConverter
init|=
name|ObjectIdConverter
operator|.
name|create
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|CachedRef
argument_list|>
name|refs
init|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
name|proto
operator|.
name|getRefCount
argument_list|()
argument_list|)
decl_stmt|;
name|proto
operator|.
name|getRefMap
argument_list|()
operator|.
name|forEach
argument_list|(
parameter_list|(
name|n
parameter_list|,
name|cr
parameter_list|)
lambda|->
name|refs
operator|.
name|put
argument_list|(
name|n
argument_list|,
operator|new
name|CachedRef
argument_list|(
name|cr
operator|.
name|getFlag
argument_list|()
argument_list|,
name|idConverter
operator|.
name|fromByteString
argument_list|(
name|cr
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ObjectIdOwnerMap
argument_list|<
name|Tag
argument_list|>
name|tags
init|=
operator|new
name|ObjectIdOwnerMap
argument_list|<>
argument_list|()
decl_stmt|;
name|proto
operator|.
name|getTagList
argument_list|()
operator|.
name|forEach
argument_list|(
name|t
lambda|->
name|tags
operator|.
name|add
argument_list|(
operator|new
name|Tag
argument_list|(
name|idConverter
operator|.
name|fromByteString
argument_list|(
name|t
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|BitSet
operator|.
name|valueOf
argument_list|(
name|t
operator|.
name|getFlags
argument_list|()
operator|.
name|asReadOnlyByteBuffer
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|TagSet
argument_list|(
name|Project
operator|.
name|nameKey
argument_list|(
name|proto
operator|.
name|getProjectName
argument_list|()
argument_list|)
argument_list|,
name|refs
argument_list|,
name|tags
argument_list|)
return|;
block|}
DECL|method|toProto ()
name|TagSetProto
name|toProto
parameter_list|()
block|{
name|ObjectIdConverter
name|idConverter
init|=
name|ObjectIdConverter
operator|.
name|create
argument_list|()
decl_stmt|;
name|TagSetProto
operator|.
name|Builder
name|b
init|=
name|TagSetProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setProjectName
argument_list|(
name|projectName
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|refs
operator|.
name|forEach
argument_list|(
parameter_list|(
name|n
parameter_list|,
name|cr
parameter_list|)
lambda|->
name|b
operator|.
name|putRef
argument_list|(
name|n
argument_list|,
name|CachedRefProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
name|idConverter
operator|.
name|toByteString
argument_list|(
name|cr
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setFlag
argument_list|(
name|cr
operator|.
name|flag
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|tags
operator|.
name|forEach
argument_list|(
name|t
lambda|->
name|b
operator|.
name|addTag
argument_list|(
name|TagProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
name|idConverter
operator|.
name|toByteString
argument_list|(
name|t
argument_list|)
argument_list|)
operator|.
name|setFlags
argument_list|(
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|t
operator|.
name|refFlags
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|refresh (TagSet old, TagMatcher m)
specifier|private
name|boolean
name|refresh
parameter_list|(
name|TagSet
name|old
parameter_list|,
name|TagMatcher
name|m
parameter_list|)
block|{
if|if
condition|(
name|m
operator|.
name|newRefs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// No new references is a simple update. Copy from the old set.
name|copy
argument_list|(
name|old
argument_list|,
name|m
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// Only permit a refresh if all new references start from the tip of
comment|// an existing references. This happens some of the time within a
comment|// Gerrit Code Review server, perhaps about 50% of new references.
comment|// Since a complete rebuild is so costly, try this approach first.
name|Map
argument_list|<
name|ObjectId
argument_list|,
name|Integer
argument_list|>
name|byObj
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CachedRef
name|r
range|:
name|old
operator|.
name|refs
operator|.
name|values
argument_list|()
control|)
block|{
name|ObjectId
name|id
init|=
name|r
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|byObj
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|byObj
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|r
operator|.
name|flag
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Ref
name|newRef
range|:
name|m
operator|.
name|newRefs
control|)
block|{
name|ObjectId
name|id
init|=
name|newRef
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
operator|||
name|refs
operator|.
name|containsKey
argument_list|(
name|newRef
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
elseif|else
if|if
condition|(
operator|!
name|byObj
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
name|copy
argument_list|(
name|old
argument_list|,
name|m
argument_list|)
expr_stmt|;
for|for
control|(
name|Ref
name|newRef
range|:
name|m
operator|.
name|newRefs
control|)
block|{
name|ObjectId
name|id
init|=
name|newRef
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
operator|||
name|refs
operator|.
name|containsKey
argument_list|(
name|newRef
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|int
name|srcFlag
init|=
name|byObj
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|int
name|newFlag
init|=
name|refs
operator|.
name|size
argument_list|()
decl_stmt|;
name|refs
operator|.
name|put
argument_list|(
name|newRef
operator|.
name|getName
argument_list|()
argument_list|,
operator|new
name|CachedRef
argument_list|(
name|newRef
argument_list|,
name|newFlag
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Tag
name|tag
range|:
name|tags
control|)
block|{
if|if
condition|(
name|tag
operator|.
name|refFlags
operator|.
name|get
argument_list|(
name|srcFlag
argument_list|)
condition|)
block|{
name|tag
operator|.
name|refFlags
operator|.
name|set
argument_list|(
name|newFlag
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|copy (TagSet old, TagMatcher m)
specifier|private
name|void
name|copy
parameter_list|(
name|TagSet
name|old
parameter_list|,
name|TagMatcher
name|m
parameter_list|)
block|{
name|refs
operator|.
name|putAll
argument_list|(
name|old
operator|.
name|refs
argument_list|)
expr_stmt|;
for|for
control|(
name|Tag
name|srcTag
range|:
name|old
operator|.
name|tags
control|)
block|{
name|BitSet
name|mine
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
name|mine
operator|.
name|or
argument_list|(
name|srcTag
operator|.
name|refFlags
argument_list|)
expr_stmt|;
name|tags
operator|.
name|add
argument_list|(
operator|new
name|Tag
argument_list|(
name|srcTag
argument_list|,
name|mine
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|TagMatcher
operator|.
name|LostRef
name|lost
range|:
name|m
operator|.
name|lostRefs
control|)
block|{
name|Tag
name|mine
init|=
name|tags
operator|.
name|get
argument_list|(
name|lost
operator|.
name|tag
argument_list|)
decl_stmt|;
if|if
condition|(
name|mine
operator|!=
literal|null
condition|)
block|{
name|mine
operator|.
name|refFlags
operator|.
name|clear
argument_list|(
name|lost
operator|.
name|flag
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addTag (TagWalk rw, Ref ref)
specifier|private
name|void
name|addTag
parameter_list|(
name|TagWalk
name|rw
parameter_list|,
name|Ref
name|ref
parameter_list|)
block|{
name|ObjectId
name|id
init|=
name|ref
operator|.
name|getPeeledObjectId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
name|id
operator|=
name|ref
operator|.
name|getObjectId
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|tags
operator|.
name|contains
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|BitSet
name|flags
decl_stmt|;
try|try
block|{
name|flags
operator|=
operator|(
operator|(
name|TagCommit
operator|)
name|rw
operator|.
name|parseCommit
argument_list|(
name|id
argument_list|)
operator|)
operator|.
name|refFlags
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IncorrectObjectTypeException
name|notCommit
parameter_list|)
block|{
name|flags
operator|=
operator|new
name|BitSet
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atWarning
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Error on %s of %s"
argument_list|,
name|ref
operator|.
name|getName
argument_list|()
argument_list|,
name|projectName
argument_list|)
expr_stmt|;
name|flags
operator|=
operator|new
name|BitSet
argument_list|()
expr_stmt|;
block|}
name|tags
operator|.
name|add
argument_list|(
operator|new
name|Tag
argument_list|(
name|id
argument_list|,
name|flags
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addRef (TagWalk rw, Ref ref)
specifier|private
name|void
name|addRef
parameter_list|(
name|TagWalk
name|rw
parameter_list|,
name|Ref
name|ref
parameter_list|)
block|{
try|try
block|{
name|TagCommit
name|commit
init|=
operator|(
name|TagCommit
operator|)
name|rw
operator|.
name|parseCommit
argument_list|(
name|ref
operator|.
name|getObjectId
argument_list|()
argument_list|)
decl_stmt|;
name|rw
operator|.
name|markStart
argument_list|(
name|commit
argument_list|)
expr_stmt|;
name|int
name|flag
init|=
name|refs
operator|.
name|size
argument_list|()
decl_stmt|;
name|commit
operator|.
name|refFlags
operator|.
name|set
argument_list|(
name|flag
argument_list|)
expr_stmt|;
name|refs
operator|.
name|put
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|,
operator|new
name|CachedRef
argument_list|(
name|ref
argument_list|,
name|flag
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IncorrectObjectTypeException
name|notCommit
parameter_list|)
block|{
comment|// No need to spam the logs.
comment|// Quite many refs will point to non-commits.
comment|// For instance, refs from refs/cache-automerge
comment|// will often end up here.
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atWarning
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Error on %s of %s"
argument_list|,
name|ref
operator|.
name|getName
argument_list|()
argument_list|,
name|projectName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|skip (Ref ref)
specifier|static
name|boolean
name|skip
parameter_list|(
name|Ref
name|ref
parameter_list|)
block|{
return|return
name|ref
operator|.
name|isSymbolic
argument_list|()
operator|||
name|ref
operator|.
name|getObjectId
argument_list|()
operator|==
literal|null
operator|||
name|PatchSet
operator|.
name|isChangeRef
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
operator|||
name|RefNames
operator|.
name|isNoteDbMetaRef
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
operator|||
name|ref
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|RefNames
operator|.
name|REFS_CACHE_AUTOMERGE
argument_list|)
return|;
block|}
DECL|method|isTag (Ref ref)
specifier|private
specifier|static
name|boolean
name|isTag
parameter_list|(
name|Ref
name|ref
parameter_list|)
block|{
return|return
name|ref
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
return|;
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
name|MoreObjects
operator|.
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|add
argument_list|(
literal|"projectName"
argument_list|,
name|projectName
argument_list|)
operator|.
name|add
argument_list|(
literal|"refs"
argument_list|,
name|refs
argument_list|)
operator|.
name|add
argument_list|(
literal|"tags"
argument_list|,
name|tags
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|class|Tag
specifier|static
specifier|final
class|class
name|Tag
extends|extends
name|ObjectIdOwnerMap
operator|.
name|Entry
block|{
DECL|field|refFlags
annotation|@
name|VisibleForTesting
specifier|final
name|BitSet
name|refFlags
decl_stmt|;
DECL|method|Tag (AnyObjectId id, BitSet flags)
name|Tag
parameter_list|(
name|AnyObjectId
name|id
parameter_list|,
name|BitSet
name|flags
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|this
operator|.
name|refFlags
operator|=
name|flags
expr_stmt|;
block|}
DECL|method|has (BitSet mask)
name|boolean
name|has
parameter_list|(
name|BitSet
name|mask
parameter_list|)
block|{
return|return
name|refFlags
operator|.
name|intersects
argument_list|(
name|mask
argument_list|)
return|;
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
name|MoreObjects
operator|.
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|addValue
argument_list|(
name|name
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"refFlags"
argument_list|,
name|refFlags
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|class|CachedRef
specifier|static
specifier|final
class|class
name|CachedRef
extends|extends
name|AtomicReference
argument_list|<
name|ObjectId
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|flag
specifier|final
name|int
name|flag
decl_stmt|;
DECL|method|CachedRef (Ref ref, int flag)
name|CachedRef
parameter_list|(
name|Ref
name|ref
parameter_list|,
name|int
name|flag
parameter_list|)
block|{
name|this
argument_list|(
name|flag
argument_list|,
name|ref
operator|.
name|getObjectId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|CachedRef (int flag, ObjectId id)
name|CachedRef
parameter_list|(
name|int
name|flag
parameter_list|,
name|ObjectId
name|id
parameter_list|)
block|{
name|this
operator|.
name|flag
operator|=
name|flag
expr_stmt|;
name|set
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|ObjectId
name|id
init|=
name|get
argument_list|()
decl_stmt|;
return|return
name|MoreObjects
operator|.
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|addValue
argument_list|(
name|id
operator|!=
literal|null
condition|?
name|id
operator|.
name|name
argument_list|()
else|:
literal|"null"
argument_list|)
operator|.
name|add
argument_list|(
literal|"flag"
argument_list|,
name|flag
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|class|TagWalk
specifier|private
specifier|static
specifier|final
class|class
name|TagWalk
extends|extends
name|RevWalk
block|{
DECL|method|TagWalk (Repository git)
name|TagWalk
parameter_list|(
name|Repository
name|git
parameter_list|)
block|{
name|super
argument_list|(
name|git
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createCommit (AnyObjectId id)
specifier|protected
name|TagCommit
name|createCommit
parameter_list|(
name|AnyObjectId
name|id
parameter_list|)
block|{
return|return
operator|new
name|TagCommit
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
DECL|class|TagCommit
specifier|private
specifier|static
specifier|final
class|class
name|TagCommit
extends|extends
name|RevCommit
block|{
DECL|field|refFlags
specifier|final
name|BitSet
name|refFlags
decl_stmt|;
DECL|method|TagCommit (AnyObjectId id)
name|TagCommit
parameter_list|(
name|AnyObjectId
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|refFlags
operator|=
operator|new
name|BitSet
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

