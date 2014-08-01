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
name|server
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
name|AbstractAdvertiseRefsHook
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
name|ServiceMayNotContinueException
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
extends|extends
name|AbstractAdvertiseRefsHook
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
DECL|field|tagCache
specifier|private
specifier|final
name|TagCache
name|tagCache
decl_stmt|;
DECL|field|changeCache
specifier|private
specifier|final
name|ChangeCache
name|changeCache
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Repository
name|db
decl_stmt|;
DECL|field|projectName
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|projectName
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
DECL|method|VisibleRefFilter (final TagCache tagCache, final ChangeCache changeCache, final Repository db, final ProjectControl projectControl, final ReviewDb reviewDb, final boolean showChanges)
specifier|public
name|VisibleRefFilter
parameter_list|(
specifier|final
name|TagCache
name|tagCache
parameter_list|,
specifier|final
name|ChangeCache
name|changeCache
parameter_list|,
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
name|tagCache
operator|=
name|tagCache
expr_stmt|;
name|this
operator|.
name|changeCache
operator|=
name|changeCache
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|projectName
operator|=
name|projectControl
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
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
DECL|method|filter (Map<String, Ref> refs, boolean filterTagsSeperately)
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
parameter_list|,
name|boolean
name|filterTagsSeperately
parameter_list|)
block|{
if|if
condition|(
name|projectCtl
operator|.
name|allRefsAreVisible
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|r
init|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|refs
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|projectCtl
operator|.
name|controlForRef
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
operator|.
name|isVisible
argument_list|()
condition|)
block|{
name|r
operator|.
name|remove
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
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
argument_list|<>
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
argument_list|<>
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
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
if|if
condition|(
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
condition|)
block|{
continue|continue;
block|}
elseif|else
if|if
condition|(
operator|(
name|changeId
operator|=
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
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// Reference related to a change is visible if the change is visible.
comment|//
if|if
condition|(
name|showChanges
operator|&&
name|visibleChanges
operator|.
name|contains
argument_list|(
name|changeId
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
if|if
condition|(
name|ref
operator|.
name|getObjectId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|deferredTags
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
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
operator|(
operator|!
name|result
operator|.
name|isEmpty
argument_list|()
operator|||
name|filterTagsSeperately
operator|)
condition|)
block|{
name|TagMatcher
name|tags
init|=
name|tagCache
operator|.
name|get
argument_list|(
name|projectName
argument_list|)
operator|.
name|matcher
argument_list|(
name|tagCache
argument_list|,
name|db
argument_list|,
name|filterTagsSeperately
condition|?
name|filter
argument_list|(
name|refs
argument_list|)
operator|.
name|values
argument_list|()
else|:
name|result
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Ref
name|tag
range|:
name|deferredTags
control|)
block|{
if|if
condition|(
name|tags
operator|.
name|isReachable
argument_list|(
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
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getAdvertisedRefs (Repository repository, RevWalk revWalk)
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|getAdvertisedRefs
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|RevWalk
name|revWalk
parameter_list|)
throws|throws
name|ServiceMayNotContinueException
block|{
try|try
block|{
return|return
name|filter
argument_list|(
name|repository
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
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ServiceMayNotContinueException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|ServiceMayNotContinueException
name|ex
init|=
operator|new
name|ServiceMayNotContinueException
argument_list|()
decl_stmt|;
name|ex
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
block|}
DECL|method|filter (Map<String, Ref> refs)
specifier|private
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
return|return
name|filter
argument_list|(
name|refs
argument_list|,
literal|false
argument_list|)
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
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Change
name|change
range|:
name|changeCache
operator|.
name|get
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
argument_list|(
name|reviewDb
argument_list|)
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

