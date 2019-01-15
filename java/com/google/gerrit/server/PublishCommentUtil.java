begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|checkArgument
import|;
end_import

begin_import
import|import static
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
name|PatchLineComment
operator|.
name|Status
operator|.
name|PUBLISHED
import|;
end_import

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
name|toSet
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
name|reviewdb
operator|.
name|client
operator|.
name|Comment
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
name|gerrit
operator|.
name|server
operator|.
name|patch
operator|.
name|PatchListCache
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
name|patch
operator|.
name|PatchListNotAvailableException
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
name|update
operator|.
name|ChangeContext
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Singleton
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
name|Map
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|PublishCommentUtil
specifier|public
class|class
name|PublishCommentUtil
block|{
DECL|field|patchListCache
specifier|private
specifier|final
name|PatchListCache
name|patchListCache
decl_stmt|;
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
DECL|field|commentsUtil
specifier|private
specifier|final
name|CommentsUtil
name|commentsUtil
decl_stmt|;
annotation|@
name|Inject
DECL|method|PublishCommentUtil ( CommentsUtil commentsUtil, PatchListCache patchListCache, PatchSetUtil psUtil)
name|PublishCommentUtil
parameter_list|(
name|CommentsUtil
name|commentsUtil
parameter_list|,
name|PatchListCache
name|patchListCache
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|)
block|{
name|this
operator|.
name|commentsUtil
operator|=
name|commentsUtil
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
name|this
operator|.
name|patchListCache
operator|=
name|patchListCache
expr_stmt|;
block|}
DECL|method|publish ( ChangeContext ctx, PatchSet.Id psId, Collection<Comment> drafts, @Nullable String tag)
specifier|public
name|void
name|publish
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|Collection
argument_list|<
name|Comment
argument_list|>
name|drafts
parameter_list|,
annotation|@
name|Nullable
name|String
name|tag
parameter_list|)
throws|throws
name|OrmException
block|{
name|ChangeNotes
name|notes
init|=
name|ctx
operator|.
name|getNotes
argument_list|()
decl_stmt|;
name|checkArgument
argument_list|(
name|notes
operator|!=
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|drafts
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|Map
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|,
name|PatchSet
argument_list|>
name|patchSets
init|=
name|psUtil
operator|.
name|getAsMap
argument_list|(
name|notes
argument_list|,
name|drafts
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|d
lambda|->
name|psId
argument_list|(
name|notes
argument_list|,
name|d
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toSet
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Comment
name|d
range|:
name|drafts
control|)
block|{
name|PatchSet
name|ps
init|=
name|patchSets
operator|.
name|get
argument_list|(
name|psId
argument_list|(
name|notes
argument_list|,
name|d
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"patch set "
operator|+
name|ps
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
name|d
operator|.
name|writtenOn
operator|=
name|ctx
operator|.
name|getWhen
argument_list|()
expr_stmt|;
name|d
operator|.
name|tag
operator|=
name|tag
expr_stmt|;
comment|// Draft may have been created by a different real user; copy the current real user. (Only
comment|// applies to X-Gerrit-RunAs, since modifying drafts via on_behalf_of is not allowed.)
name|ctx
operator|.
name|getUser
argument_list|()
operator|.
name|updateRealAccountId
argument_list|(
name|d
operator|::
name|setRealAuthor
argument_list|)
expr_stmt|;
try|try
block|{
name|CommentsUtil
operator|.
name|setCommentRevId
argument_list|(
name|d
argument_list|,
name|patchListCache
argument_list|,
name|notes
operator|.
name|getChange
argument_list|()
argument_list|,
name|ps
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PatchListNotAvailableException
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
name|commentsUtil
operator|.
name|putComments
argument_list|(
name|ctx
operator|.
name|getUpdate
argument_list|(
name|psId
argument_list|)
argument_list|,
name|PUBLISHED
argument_list|,
name|drafts
argument_list|)
expr_stmt|;
block|}
DECL|method|psId (ChangeNotes notes, Comment c)
specifier|private
specifier|static
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|Comment
name|c
parameter_list|)
block|{
return|return
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|notes
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|c
operator|.
name|key
operator|.
name|patchSetId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

