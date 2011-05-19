begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
name|project
operator|.
name|ProjectControl
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
name|RevObject
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
name|RevTag
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
name|transport
operator|.
name|RefFilter
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
name|Collections
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
DECL|class|VisibleRefFilter
specifier|public
class|class
name|VisibleRefFilter
implements|implements
name|RefFilter
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
name|VisibleRefFilter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Repository
name|db
decl_stmt|;
DECL|field|projectCtl
specifier|private
specifier|final
name|ProjectControl
name|projectCtl
decl_stmt|;
DECL|field|reviewDb
specifier|private
specifier|final
name|ReviewDb
name|reviewDb
decl_stmt|;
DECL|field|showChanges
specifier|private
specifier|final
name|boolean
name|showChanges
decl_stmt|;
DECL|method|VisibleRefFilter (final Repository db, final ProjectControl projectControl, final ReviewDb reviewDb, final boolean showChanges)
specifier|public
name|VisibleRefFilter
parameter_list|(
specifier|final
name|Repository
name|db
parameter_list|,
specifier|final
name|ProjectControl
name|projectControl
parameter_list|,
specifier|final
name|ReviewDb
name|reviewDb
parameter_list|,
specifier|final
name|boolean
name|showChanges
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
name|projectCtl
operator|=
name|projectControl
expr_stmt|;
name|this
operator|.
name|reviewDb
operator|=
name|reviewDb
expr_stmt|;
name|this
operator|.
name|showChanges
operator|=
name|showChanges
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|filter (Map<String, Ref> refs)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|filter
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|refs
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|visibleChanges
init|=
name|visibleChanges
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Ref
argument_list|>
name|deferredTags
init|=
operator|new
name|ArrayList
argument_list|<
name|Ref
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Ref
name|ref
range|:
name|refs
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|PatchSet
operator|.
name|isRef
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|// Reference to a patch set is visible if the change is visible.
comment|//
if|if
condition|(
name|visibleChanges
operator|.
name|contains
argument_list|(
name|Change
operator|.
name|Id
operator|.
name|fromRef
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
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
comment|// If its a tag, consider it later.
comment|//
name|deferredTags
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|projectCtl
operator|.
name|controlForRef
argument_list|(
name|ref
operator|.
name|getLeaf
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isVisible
argument_list|()
condition|)
block|{
comment|// Use the leaf to lookup the control data. If the reference is
comment|// symbolic we want the control around the final target. If its
comment|// not symbolic then getLeaf() is a no-op returning ref itself.
comment|//
name|result
operator|.
name|put
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If we have tags that were deferred, we need to do a revision walk
comment|// to identify what tags we can actually reach, and what we cannot.
comment|//
if|if
condition|(
operator|!
name|deferredTags
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|result
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|addVisibleTags
argument_list|(
name|result
argument_list|,
name|deferredTags
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|visibleChanges ()
specifier|private
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|visibleChanges
parameter_list|()
block|{
if|if
condition|(
operator|!
name|showChanges
condition|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
specifier|final
name|Project
name|project
init|=
name|projectCtl
operator|.
name|getProject
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|visibleChanges
init|=
operator|new
name|HashSet
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Change
name|change
range|:
name|reviewDb
operator|.
name|changes
argument_list|()
operator|.
name|byProject
argument_list|(
name|project
operator|.
name|getNameKey
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
name|projectCtl
operator|.
name|controlFor
argument_list|(
name|change
argument_list|)
operator|.
name|isVisible
argument_list|()
condition|)
block|{
name|visibleChanges
operator|.
name|add
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|visibleChanges
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot load changes for project "
operator|+
name|project
operator|.
name|getName
argument_list|()
operator|+
literal|", assuming no changes are visible"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
block|}
DECL|method|addVisibleTags (final Map<String, Ref> result, final List<Ref> tags)
specifier|private
name|void
name|addVisibleTags
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|result
parameter_list|,
specifier|final
name|List
argument_list|<
name|Ref
argument_list|>
name|tags
parameter_list|)
block|{
specifier|final
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|db
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|RevFlag
name|VISIBLE
init|=
name|rw
operator|.
name|newFlag
argument_list|(
literal|"VISIBLE"
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RevCommit
argument_list|>
name|starts
decl_stmt|;
name|rw
operator|.
name|carry
argument_list|(
name|VISIBLE
argument_list|)
expr_stmt|;
name|starts
operator|=
name|lookupVisibleCommits
argument_list|(
name|result
argument_list|,
name|rw
argument_list|,
name|VISIBLE
argument_list|)
expr_stmt|;
for|for
control|(
name|Ref
name|tag
range|:
name|tags
control|)
block|{
if|if
condition|(
name|isTagVisible
argument_list|(
name|rw
argument_list|,
name|VISIBLE
argument_list|,
name|starts
argument_list|,
name|tag
argument_list|)
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|tag
operator|.
name|getName
argument_list|()
argument_list|,
name|tag
argument_list|)
expr_stmt|;
block|}
block|}
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
DECL|method|lookupVisibleCommits (final Map<String, Ref> result, final RevWalk rw, final RevFlag VISIBLE)
specifier|private
name|List
argument_list|<
name|RevCommit
argument_list|>
name|lookupVisibleCommits
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|result
parameter_list|,
specifier|final
name|RevWalk
name|rw
parameter_list|,
specifier|final
name|RevFlag
name|VISIBLE
parameter_list|)
block|{
comment|// Lookup and cache the roots of the graph that we know we can see.
comment|//
specifier|final
name|List
argument_list|<
name|RevCommit
argument_list|>
name|roots
init|=
operator|new
name|ArrayList
argument_list|<
name|RevCommit
argument_list|>
argument_list|(
name|result
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Ref
name|ref
range|:
name|result
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|RevObject
name|c
init|=
name|rw
operator|.
name|parseAny
argument_list|(
name|ref
operator|.
name|getObjectId
argument_list|()
argument_list|)
decl_stmt|;
name|c
operator|.
name|add
argument_list|(
name|VISIBLE
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|instanceof
name|RevCommit
condition|)
block|{
name|roots
operator|.
name|add
argument_list|(
operator|(
name|RevCommit
operator|)
name|c
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|instanceof
name|RevTag
condition|)
block|{
name|roots
operator|.
name|add
argument_list|(
name|rw
operator|.
name|parseCommit
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{       }
block|}
return|return
name|roots
return|;
block|}
DECL|method|isTagVisible (final RevWalk rw, final RevFlag VISIBLE, final List<RevCommit> starts, Ref tag)
specifier|private
name|boolean
name|isTagVisible
parameter_list|(
specifier|final
name|RevWalk
name|rw
parameter_list|,
specifier|final
name|RevFlag
name|VISIBLE
parameter_list|,
specifier|final
name|List
argument_list|<
name|RevCommit
argument_list|>
name|starts
parameter_list|,
name|Ref
name|tag
parameter_list|)
block|{
try|try
block|{
specifier|final
name|RevObject
name|obj
init|=
name|peelTag
argument_list|(
name|rw
argument_list|,
name|tag
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|.
name|has
argument_list|(
name|VISIBLE
argument_list|)
condition|)
block|{
comment|// If the target is immediately visible, continue on. This case
comment|// is quite common as tags are often sorted alphabetically by the
comment|// version number, so earlier tags usually compute the data needed
comment|// to answer later tags with no additional effort.
comment|//
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|instanceof
name|RevCommit
condition|)
block|{
comment|// Cast to a commit and traverse the history to determine if
comment|// the commit is reachable through one or more references.
comment|//
specifier|final
name|RevCommit
name|c
init|=
operator|(
name|RevCommit
operator|)
name|obj
decl_stmt|;
name|walk
argument_list|(
name|rw
argument_list|,
name|VISIBLE
argument_list|,
name|c
argument_list|,
name|starts
argument_list|)
expr_stmt|;
return|return
name|c
operator|.
name|has
argument_list|(
name|VISIBLE
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|peelTag (final RevWalk rw, final Ref tag)
specifier|private
name|RevObject
name|peelTag
parameter_list|(
specifier|final
name|RevWalk
name|rw
parameter_list|,
specifier|final
name|Ref
name|tag
parameter_list|)
throws|throws
name|MissingObjectException
throws|,
name|IOException
block|{
comment|// Try to use the peeled object identity, because it may be
comment|// able to save us from parsing the tag object itself.
comment|//
name|ObjectId
name|target
init|=
name|tag
operator|.
name|getPeeledObjectId
argument_list|()
decl_stmt|;
if|if
condition|(
name|target
operator|==
literal|null
condition|)
block|{
name|target
operator|=
name|tag
operator|.
name|getObjectId
argument_list|()
expr_stmt|;
block|}
name|RevObject
name|o
init|=
name|rw
operator|.
name|parseAny
argument_list|(
name|target
argument_list|)
decl_stmt|;
while|while
condition|(
name|o
operator|instanceof
name|RevTag
condition|)
block|{
name|o
operator|=
operator|(
operator|(
name|RevTag
operator|)
name|o
operator|)
operator|.
name|getObject
argument_list|()
expr_stmt|;
name|rw
operator|.
name|parseHeaders
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
return|return
name|o
return|;
block|}
DECL|method|walk (final RevWalk rw, final RevFlag VISIBLE, final RevCommit tagged, final List<RevCommit> starts)
specifier|private
name|void
name|walk
parameter_list|(
specifier|final
name|RevWalk
name|rw
parameter_list|,
specifier|final
name|RevFlag
name|VISIBLE
parameter_list|,
specifier|final
name|RevCommit
name|tagged
parameter_list|,
specifier|final
name|List
argument_list|<
name|RevCommit
argument_list|>
name|starts
parameter_list|)
throws|throws
name|MissingObjectException
throws|,
name|IncorrectObjectTypeException
throws|,
name|IOException
block|{
comment|// Reset the traversal, but keep VISIBLE flags live as they aren't
comment|// invalidated by the change in starting points.
comment|//
name|rw
operator|.
name|resetRetain
argument_list|(
name|VISIBLE
argument_list|)
expr_stmt|;
for|for
control|(
name|RevCommit
name|o
range|:
name|starts
control|)
block|{
try|try
block|{
name|rw
operator|.
name|markStart
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{       }
block|}
comment|// Traverse the history until the tag is found.
comment|//
name|rw
operator|.
name|markUninteresting
argument_list|(
name|tagged
argument_list|)
expr_stmt|;
while|while
condition|(
name|rw
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{     }
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
name|getLeaf
argument_list|()
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
block|}
end_class

end_unit

