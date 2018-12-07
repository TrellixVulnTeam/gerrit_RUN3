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
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
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
name|Nullable
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
name|proto
operator|.
name|Protos
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
name|cache
operator|.
name|proto
operator|.
name|Cache
operator|.
name|TagSetHolderProto
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
name|CacheSerializer
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

begin_class
DECL|class|TagSetHolder
specifier|public
class|class
name|TagSetHolder
block|{
DECL|field|buildLock
specifier|private
specifier|final
name|Object
name|buildLock
init|=
operator|new
name|Object
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
DECL|field|tags
annotation|@
name|Nullable
specifier|private
specifier|volatile
name|TagSet
name|tags
decl_stmt|;
DECL|method|TagSetHolder (Project.NameKey projectName)
name|TagSetHolder
parameter_list|(
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|)
block|{
name|this
operator|.
name|projectName
operator|=
name|projectName
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
DECL|method|getTagSet ()
name|TagSet
name|getTagSet
parameter_list|()
block|{
return|return
name|tags
return|;
block|}
DECL|method|setTagSet (TagSet tags)
name|void
name|setTagSet
parameter_list|(
name|TagSet
name|tags
parameter_list|)
block|{
name|this
operator|.
name|tags
operator|=
name|tags
expr_stmt|;
block|}
DECL|method|matcher (TagCache cache, Repository db, Collection<Ref> include)
specifier|public
name|TagMatcher
name|matcher
parameter_list|(
name|TagCache
name|cache
parameter_list|,
name|Repository
name|db
parameter_list|,
name|Collection
argument_list|<
name|Ref
argument_list|>
name|include
parameter_list|)
block|{
name|include
operator|=
name|include
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|r
lambda|->
operator|!
name|TagSet
operator|.
name|skip
argument_list|(
name|r
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
expr_stmt|;
name|TagSet
name|tags
init|=
name|this
operator|.
name|tags
decl_stmt|;
if|if
condition|(
name|tags
operator|==
literal|null
condition|)
block|{
name|tags
operator|=
name|build
argument_list|(
name|cache
argument_list|,
name|db
argument_list|)
expr_stmt|;
block|}
name|TagMatcher
name|m
init|=
operator|new
name|TagMatcher
argument_list|(
name|this
argument_list|,
name|cache
argument_list|,
name|db
argument_list|,
name|include
argument_list|,
name|tags
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|tags
operator|.
name|prepare
argument_list|(
name|m
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|newRefs
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|m
operator|.
name|lostRefs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|tags
operator|=
name|rebuild
argument_list|(
name|cache
argument_list|,
name|db
argument_list|,
name|tags
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|m
operator|=
operator|new
name|TagMatcher
argument_list|(
name|this
argument_list|,
name|cache
argument_list|,
name|db
argument_list|,
name|include
argument_list|,
name|tags
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tags
operator|.
name|prepare
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
return|return
name|m
return|;
block|}
DECL|method|rebuildForNewTags (TagCache cache, TagMatcher m)
name|void
name|rebuildForNewTags
parameter_list|(
name|TagCache
name|cache
parameter_list|,
name|TagMatcher
name|m
parameter_list|)
block|{
name|m
operator|.
name|tags
operator|=
name|rebuild
argument_list|(
name|cache
argument_list|,
name|m
operator|.
name|db
argument_list|,
name|m
operator|.
name|tags
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|m
operator|.
name|mask
operator|.
name|clear
argument_list|()
expr_stmt|;
name|m
operator|.
name|newRefs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|m
operator|.
name|lostRefs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|m
operator|.
name|tags
operator|.
name|prepare
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
DECL|method|build (TagCache cache, Repository db)
specifier|private
name|TagSet
name|build
parameter_list|(
name|TagCache
name|cache
parameter_list|,
name|Repository
name|db
parameter_list|)
block|{
synchronized|synchronized
init|(
name|buildLock
init|)
block|{
name|TagSet
name|tags
init|=
name|this
operator|.
name|tags
decl_stmt|;
if|if
condition|(
name|tags
operator|==
literal|null
condition|)
block|{
name|tags
operator|=
operator|new
name|TagSet
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|tags
operator|.
name|build
argument_list|(
name|db
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|tags
operator|=
name|tags
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|projectName
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|tags
return|;
block|}
block|}
DECL|method|rebuild (TagCache cache, Repository db, TagSet old, TagMatcher m)
specifier|private
name|TagSet
name|rebuild
parameter_list|(
name|TagCache
name|cache
parameter_list|,
name|Repository
name|db
parameter_list|,
name|TagSet
name|old
parameter_list|,
name|TagMatcher
name|m
parameter_list|)
block|{
synchronized|synchronized
init|(
name|buildLock
init|)
block|{
name|TagSet
name|cur
init|=
name|this
operator|.
name|tags
decl_stmt|;
if|if
condition|(
name|cur
operator|==
name|old
condition|)
block|{
name|cur
operator|=
operator|new
name|TagSet
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|cur
operator|.
name|build
argument_list|(
name|db
argument_list|,
name|old
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|this
operator|.
name|tags
operator|=
name|cur
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|projectName
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|cur
return|;
block|}
block|}
DECL|enum|Serializer
enum|enum
name|Serializer
implements|implements
name|CacheSerializer
argument_list|<
name|TagSetHolder
argument_list|>
block|{
DECL|enumConstant|INSTANCE
name|INSTANCE
block|;
annotation|@
name|Override
DECL|method|serialize (TagSetHolder object)
specifier|public
name|byte
index|[]
name|serialize
parameter_list|(
name|TagSetHolder
name|object
parameter_list|)
block|{
name|TagSetHolderProto
operator|.
name|Builder
name|b
init|=
name|TagSetHolderProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setProjectName
argument_list|(
name|object
operator|.
name|projectName
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|TagSet
name|tags
init|=
name|object
operator|.
name|tags
decl_stmt|;
if|if
condition|(
name|tags
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|setTags
argument_list|(
name|tags
operator|.
name|toProto
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|Protos
operator|.
name|toByteArray
argument_list|(
name|b
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|deserialize (byte[] in)
specifier|public
name|TagSetHolder
name|deserialize
parameter_list|(
name|byte
index|[]
name|in
parameter_list|)
block|{
name|TagSetHolderProto
name|proto
init|=
name|Protos
operator|.
name|parseUnchecked
argument_list|(
name|TagSetHolderProto
operator|.
name|parser
argument_list|()
argument_list|,
name|in
argument_list|)
decl_stmt|;
name|TagSetHolder
name|holder
init|=
operator|new
name|TagSetHolder
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|proto
operator|.
name|getProjectName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|proto
operator|.
name|hasTags
argument_list|()
condition|)
block|{
name|holder
operator|.
name|tags
operator|=
name|TagSet
operator|.
name|fromProto
argument_list|(
name|proto
operator|.
name|getTags
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|holder
return|;
block|}
block|}
block|}
end_class

end_unit

