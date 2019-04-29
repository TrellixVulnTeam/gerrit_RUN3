begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
name|MoreObjects
operator|.
name|firstNonNull
import|;
end_import

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
name|common
operator|.
name|collect
operator|.
name|ComparisonChain
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
name|FluentIterable
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
name|Lists
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
name|exceptions
operator|.
name|StorageException
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
name|extensions
operator|.
name|client
operator|.
name|Side
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
name|extensions
operator|.
name|common
operator|.
name|CommentInfo
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
name|extensions
operator|.
name|restapi
operator|.
name|UnprocessableEntityException
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
name|Account
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
name|Patch
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
name|PatchLineComment
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
name|client
operator|.
name|RobotComment
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
name|config
operator|.
name|AllUsersName
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
name|config
operator|.
name|GerritServerId
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
name|notedb
operator|.
name|ChangeUpdate
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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

begin_comment
comment|/** Utility functions to manipulate Comments. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|CommentsUtil
specifier|public
class|class
name|CommentsUtil
block|{
DECL|field|COMMENT_ORDER
specifier|public
specifier|static
specifier|final
name|Ordering
argument_list|<
name|Comment
argument_list|>
name|COMMENT_ORDER
init|=
operator|new
name|Ordering
argument_list|<
name|Comment
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Comment
name|c1
parameter_list|,
name|Comment
name|c2
parameter_list|)
block|{
return|return
name|ComparisonChain
operator|.
name|start
argument_list|()
operator|.
name|compare
argument_list|(
name|c1
operator|.
name|key
operator|.
name|filename
argument_list|,
name|c2
operator|.
name|key
operator|.
name|filename
argument_list|)
operator|.
name|compare
argument_list|(
name|c1
operator|.
name|key
operator|.
name|patchSetId
argument_list|,
name|c2
operator|.
name|key
operator|.
name|patchSetId
argument_list|)
operator|.
name|compare
argument_list|(
name|c1
operator|.
name|side
argument_list|,
name|c2
operator|.
name|side
argument_list|)
operator|.
name|compare
argument_list|(
name|c1
operator|.
name|lineNbr
argument_list|,
name|c2
operator|.
name|lineNbr
argument_list|)
operator|.
name|compare
argument_list|(
name|c1
operator|.
name|writtenOn
argument_list|,
name|c2
operator|.
name|writtenOn
argument_list|)
operator|.
name|result
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|COMMENT_INFO_ORDER
specifier|public
specifier|static
specifier|final
name|Ordering
argument_list|<
name|CommentInfo
argument_list|>
name|COMMENT_INFO_ORDER
init|=
operator|new
name|Ordering
argument_list|<
name|CommentInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|CommentInfo
name|a
parameter_list|,
name|CommentInfo
name|b
parameter_list|)
block|{
return|return
name|ComparisonChain
operator|.
name|start
argument_list|()
operator|.
name|compare
argument_list|(
name|a
operator|.
name|path
argument_list|,
name|b
operator|.
name|path
argument_list|,
name|NULLS_FIRST
argument_list|)
operator|.
name|compare
argument_list|(
name|a
operator|.
name|patchSet
argument_list|,
name|b
operator|.
name|patchSet
argument_list|,
name|NULLS_FIRST
argument_list|)
operator|.
name|compare
argument_list|(
name|side
argument_list|(
name|a
argument_list|)
argument_list|,
name|side
argument_list|(
name|b
argument_list|)
argument_list|)
operator|.
name|compare
argument_list|(
name|a
operator|.
name|line
argument_list|,
name|b
operator|.
name|line
argument_list|,
name|NULLS_FIRST
argument_list|)
operator|.
name|compare
argument_list|(
name|a
operator|.
name|inReplyTo
argument_list|,
name|b
operator|.
name|inReplyTo
argument_list|,
name|NULLS_FIRST
argument_list|)
operator|.
name|compare
argument_list|(
name|a
operator|.
name|message
argument_list|,
name|b
operator|.
name|message
argument_list|)
operator|.
name|compare
argument_list|(
name|a
operator|.
name|id
argument_list|,
name|b
operator|.
name|id
argument_list|)
operator|.
name|result
argument_list|()
return|;
block|}
specifier|private
name|int
name|side
parameter_list|(
name|CommentInfo
name|c
parameter_list|)
block|{
return|return
name|firstNonNull
argument_list|(
name|c
operator|.
name|side
argument_list|,
name|Side
operator|.
name|REVISION
argument_list|)
operator|.
name|ordinal
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|method|getCommentPsId (Change.Id changeId, Comment comment)
specifier|public
specifier|static
name|PatchSet
operator|.
name|Id
name|getCommentPsId
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|Comment
name|comment
parameter_list|)
block|{
return|return
name|PatchSet
operator|.
name|id
argument_list|(
name|changeId
argument_list|,
name|comment
operator|.
name|key
operator|.
name|patchSetId
argument_list|)
return|;
block|}
DECL|method|extractMessageId (@ullable String tag)
specifier|public
specifier|static
name|String
name|extractMessageId
parameter_list|(
annotation|@
name|Nullable
name|String
name|tag
parameter_list|)
block|{
if|if
condition|(
name|tag
operator|==
literal|null
operator|||
operator|!
name|tag
operator|.
name|startsWith
argument_list|(
literal|"mailMessageId="
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|tag
operator|.
name|substring
argument_list|(
literal|"mailMessageId="
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
DECL|field|NULLS_FIRST
specifier|private
specifier|static
specifier|final
name|Ordering
argument_list|<
name|Comparable
argument_list|<
name|?
argument_list|>
argument_list|>
name|NULLS_FIRST
init|=
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|nullsFirst
argument_list|()
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|allUsers
specifier|private
specifier|final
name|AllUsersName
name|allUsers
decl_stmt|;
DECL|field|serverId
specifier|private
specifier|final
name|String
name|serverId
decl_stmt|;
annotation|@
name|Inject
DECL|method|CommentsUtil ( GitRepositoryManager repoManager, AllUsersName allUsers, @GerritServerId String serverId)
name|CommentsUtil
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|AllUsersName
name|allUsers
parameter_list|,
annotation|@
name|GerritServerId
name|String
name|serverId
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
name|allUsers
operator|=
name|allUsers
expr_stmt|;
name|this
operator|.
name|serverId
operator|=
name|serverId
expr_stmt|;
block|}
DECL|method|newComment ( ChangeContext ctx, String path, PatchSet.Id psId, short side, String message, @Nullable Boolean unresolved, @Nullable String parentUuid)
specifier|public
name|Comment
name|newComment
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|,
name|String
name|path
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|short
name|side
parameter_list|,
name|String
name|message
parameter_list|,
annotation|@
name|Nullable
name|Boolean
name|unresolved
parameter_list|,
annotation|@
name|Nullable
name|String
name|parentUuid
parameter_list|)
throws|throws
name|UnprocessableEntityException
block|{
if|if
condition|(
name|unresolved
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|parentUuid
operator|==
literal|null
condition|)
block|{
comment|// Default to false if comment is not descended from another.
name|unresolved
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
comment|// Inherit unresolved value from inReplyTo comment if not specified.
name|Comment
operator|.
name|Key
name|key
init|=
operator|new
name|Comment
operator|.
name|Key
argument_list|(
name|parentUuid
argument_list|,
name|path
argument_list|,
name|psId
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|Optional
argument_list|<
name|Comment
argument_list|>
name|parent
init|=
name|getPublished
argument_list|(
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|,
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|parent
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnprocessableEntityException
argument_list|(
literal|"Invalid parentUuid supplied for comment"
argument_list|)
throw|;
block|}
name|unresolved
operator|=
name|parent
operator|.
name|get
argument_list|()
operator|.
name|unresolved
expr_stmt|;
block|}
block|}
name|Comment
name|c
init|=
operator|new
name|Comment
argument_list|(
operator|new
name|Comment
operator|.
name|Key
argument_list|(
name|ChangeUtil
operator|.
name|messageUuid
argument_list|()
argument_list|,
name|path
argument_list|,
name|psId
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|ctx
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|,
name|side
argument_list|,
name|message
argument_list|,
name|serverId
argument_list|,
name|unresolved
argument_list|)
decl_stmt|;
name|c
operator|.
name|parentUuid
operator|=
name|parentUuid
expr_stmt|;
name|ctx
operator|.
name|getUser
argument_list|()
operator|.
name|updateRealAccountId
argument_list|(
name|c
operator|::
name|setRealAuthor
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
DECL|method|newRobotComment ( ChangeContext ctx, String path, PatchSet.Id psId, short side, String message, String robotId, String robotRunId)
specifier|public
name|RobotComment
name|newRobotComment
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|,
name|String
name|path
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|short
name|side
parameter_list|,
name|String
name|message
parameter_list|,
name|String
name|robotId
parameter_list|,
name|String
name|robotRunId
parameter_list|)
block|{
name|RobotComment
name|c
init|=
operator|new
name|RobotComment
argument_list|(
operator|new
name|Comment
operator|.
name|Key
argument_list|(
name|ChangeUtil
operator|.
name|messageUuid
argument_list|()
argument_list|,
name|path
argument_list|,
name|psId
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|ctx
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|,
name|side
argument_list|,
name|message
argument_list|,
name|serverId
argument_list|,
name|robotId
argument_list|,
name|robotRunId
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|getUser
argument_list|()
operator|.
name|updateRealAccountId
argument_list|(
name|c
operator|::
name|setRealAuthor
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
DECL|method|getPublished (ChangeNotes notes, Comment.Key key)
specifier|public
name|Optional
argument_list|<
name|Comment
argument_list|>
name|getPublished
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|Comment
operator|.
name|Key
name|key
parameter_list|)
block|{
return|return
name|publishedByChange
argument_list|(
name|notes
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|c
lambda|->
name|key
operator|.
name|equals
argument_list|(
name|c
operator|.
name|key
argument_list|)
argument_list|)
operator|.
name|findFirst
argument_list|()
return|;
block|}
DECL|method|getDraft (ChangeNotes notes, IdentifiedUser user, Comment.Key key)
specifier|public
name|Optional
argument_list|<
name|Comment
argument_list|>
name|getDraft
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|IdentifiedUser
name|user
parameter_list|,
name|Comment
operator|.
name|Key
name|key
parameter_list|)
block|{
return|return
name|draftByChangeAuthor
argument_list|(
name|notes
argument_list|,
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|c
lambda|->
name|key
operator|.
name|equals
argument_list|(
name|c
operator|.
name|key
argument_list|)
argument_list|)
operator|.
name|findFirst
argument_list|()
return|;
block|}
DECL|method|publishedByChange (ChangeNotes notes)
specifier|public
name|List
argument_list|<
name|Comment
argument_list|>
name|publishedByChange
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|)
block|{
name|notes
operator|.
name|load
argument_list|()
expr_stmt|;
return|return
name|sort
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|notes
operator|.
name|getComments
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|robotCommentsByChange (ChangeNotes notes)
specifier|public
name|List
argument_list|<
name|RobotComment
argument_list|>
name|robotCommentsByChange
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|)
block|{
name|notes
operator|.
name|load
argument_list|()
expr_stmt|;
return|return
name|sort
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|notes
operator|.
name|getRobotComments
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|draftByChange (ChangeNotes notes)
specifier|public
name|List
argument_list|<
name|Comment
argument_list|>
name|draftByChange
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|)
block|{
name|List
argument_list|<
name|Comment
argument_list|>
name|comments
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
name|getDraftRefs
argument_list|(
name|notes
operator|.
name|getChangeId
argument_list|()
argument_list|)
control|)
block|{
name|Account
operator|.
name|Id
name|account
init|=
name|Account
operator|.
name|Id
operator|.
name|fromRefSuffix
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
operator|!=
literal|null
condition|)
block|{
name|comments
operator|.
name|addAll
argument_list|(
name|draftByChangeAuthor
argument_list|(
name|notes
argument_list|,
name|account
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sort
argument_list|(
name|comments
argument_list|)
return|;
block|}
DECL|method|byPatchSet (ChangeNotes notes, PatchSet.Id psId)
specifier|public
name|List
argument_list|<
name|Comment
argument_list|>
name|byPatchSet
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
block|{
name|List
argument_list|<
name|Comment
argument_list|>
name|comments
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|comments
operator|.
name|addAll
argument_list|(
name|publishedByPatchSet
argument_list|(
name|notes
argument_list|,
name|psId
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Ref
name|ref
range|:
name|getDraftRefs
argument_list|(
name|notes
operator|.
name|getChangeId
argument_list|()
argument_list|)
control|)
block|{
name|Account
operator|.
name|Id
name|account
init|=
name|Account
operator|.
name|Id
operator|.
name|fromRefSuffix
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
operator|!=
literal|null
condition|)
block|{
name|comments
operator|.
name|addAll
argument_list|(
name|draftByPatchSetAuthor
argument_list|(
name|psId
argument_list|,
name|account
argument_list|,
name|notes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sort
argument_list|(
name|comments
argument_list|)
return|;
block|}
DECL|method|publishedByChangeFile (ChangeNotes notes, String file)
specifier|public
name|List
argument_list|<
name|Comment
argument_list|>
name|publishedByChangeFile
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|String
name|file
parameter_list|)
block|{
return|return
name|commentsOnFile
argument_list|(
name|notes
operator|.
name|load
argument_list|()
operator|.
name|getComments
argument_list|()
operator|.
name|values
argument_list|()
argument_list|,
name|file
argument_list|)
return|;
block|}
DECL|method|publishedByPatchSet (ChangeNotes notes, PatchSet.Id psId)
specifier|public
name|List
argument_list|<
name|Comment
argument_list|>
name|publishedByPatchSet
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
block|{
return|return
name|removeCommentsOnAncestorOfCommitMessage
argument_list|(
name|commentsOnPatchSet
argument_list|(
name|notes
operator|.
name|load
argument_list|()
operator|.
name|getComments
argument_list|()
operator|.
name|values
argument_list|()
argument_list|,
name|psId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|robotCommentsByPatchSet (ChangeNotes notes, PatchSet.Id psId)
specifier|public
name|List
argument_list|<
name|RobotComment
argument_list|>
name|robotCommentsByPatchSet
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
block|{
return|return
name|commentsOnPatchSet
argument_list|(
name|notes
operator|.
name|load
argument_list|()
operator|.
name|getRobotComments
argument_list|()
operator|.
name|values
argument_list|()
argument_list|,
name|psId
argument_list|)
return|;
block|}
comment|/**    * For the commit message the A side in a diff view is always empty when a comparison against an    * ancestor is done, so there can't be any comments on this ancestor. However earlier we showed    * the auto-merge commit message on side A when for a merge commit a comparison against the    * auto-merge was done. From that time there may still be comments on the auto-merge commit    * message and those we want to filter out.    */
DECL|method|removeCommentsOnAncestorOfCommitMessage (List<Comment> list)
specifier|private
name|List
argument_list|<
name|Comment
argument_list|>
name|removeCommentsOnAncestorOfCommitMessage
parameter_list|(
name|List
argument_list|<
name|Comment
argument_list|>
name|list
parameter_list|)
block|{
return|return
name|list
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|c
lambda|->
name|c
operator|.
name|side
operator|!=
literal|0
operator|||
operator|!
name|Patch
operator|.
name|COMMIT_MSG
operator|.
name|equals
argument_list|(
name|c
operator|.
name|key
operator|.
name|filename
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
return|;
block|}
DECL|method|draftByPatchSetAuthor ( PatchSet.Id psId, Account.Id author, ChangeNotes notes)
specifier|public
name|List
argument_list|<
name|Comment
argument_list|>
name|draftByPatchSetAuthor
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|Account
operator|.
name|Id
name|author
parameter_list|,
name|ChangeNotes
name|notes
parameter_list|)
block|{
return|return
name|commentsOnPatchSet
argument_list|(
name|notes
operator|.
name|load
argument_list|()
operator|.
name|getDraftComments
argument_list|(
name|author
argument_list|)
operator|.
name|values
argument_list|()
argument_list|,
name|psId
argument_list|)
return|;
block|}
DECL|method|draftByChangeFileAuthor (ChangeNotes notes, String file, Account.Id author)
specifier|public
name|List
argument_list|<
name|Comment
argument_list|>
name|draftByChangeFileAuthor
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|String
name|file
parameter_list|,
name|Account
operator|.
name|Id
name|author
parameter_list|)
block|{
return|return
name|commentsOnFile
argument_list|(
name|notes
operator|.
name|load
argument_list|()
operator|.
name|getDraftComments
argument_list|(
name|author
argument_list|)
operator|.
name|values
argument_list|()
argument_list|,
name|file
argument_list|)
return|;
block|}
DECL|method|draftByChangeAuthor (ChangeNotes notes, Account.Id author)
specifier|public
name|List
argument_list|<
name|Comment
argument_list|>
name|draftByChangeAuthor
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|Account
operator|.
name|Id
name|author
parameter_list|)
block|{
name|List
argument_list|<
name|Comment
argument_list|>
name|comments
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|comments
operator|.
name|addAll
argument_list|(
name|notes
operator|.
name|getDraftComments
argument_list|(
name|author
argument_list|)
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sort
argument_list|(
name|comments
argument_list|)
return|;
block|}
DECL|method|putComments ( ChangeUpdate update, PatchLineComment.Status status, Iterable<Comment> comments)
specifier|public
name|void
name|putComments
parameter_list|(
name|ChangeUpdate
name|update
parameter_list|,
name|PatchLineComment
operator|.
name|Status
name|status
parameter_list|,
name|Iterable
argument_list|<
name|Comment
argument_list|>
name|comments
parameter_list|)
block|{
for|for
control|(
name|Comment
name|c
range|:
name|comments
control|)
block|{
name|update
operator|.
name|putComment
argument_list|(
name|status
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|putRobotComments (ChangeUpdate update, Iterable<RobotComment> comments)
specifier|public
name|void
name|putRobotComments
parameter_list|(
name|ChangeUpdate
name|update
parameter_list|,
name|Iterable
argument_list|<
name|RobotComment
argument_list|>
name|comments
parameter_list|)
block|{
for|for
control|(
name|RobotComment
name|c
range|:
name|comments
control|)
block|{
name|update
operator|.
name|putRobotComment
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|deleteComments (ChangeUpdate update, Iterable<Comment> comments)
specifier|public
name|void
name|deleteComments
parameter_list|(
name|ChangeUpdate
name|update
parameter_list|,
name|Iterable
argument_list|<
name|Comment
argument_list|>
name|comments
parameter_list|)
block|{
for|for
control|(
name|Comment
name|c
range|:
name|comments
control|)
block|{
name|update
operator|.
name|deleteComment
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|deleteCommentByRewritingHistory ( ChangeUpdate update, Comment.Key commentKey, String newMessage)
specifier|public
name|void
name|deleteCommentByRewritingHistory
parameter_list|(
name|ChangeUpdate
name|update
parameter_list|,
name|Comment
operator|.
name|Key
name|commentKey
parameter_list|,
name|String
name|newMessage
parameter_list|)
block|{
name|update
operator|.
name|deleteCommentByRewritingHistory
argument_list|(
name|commentKey
operator|.
name|uuid
argument_list|,
name|newMessage
argument_list|)
expr_stmt|;
block|}
DECL|method|commentsOnFile (Collection<Comment> allComments, String file)
specifier|private
specifier|static
name|List
argument_list|<
name|Comment
argument_list|>
name|commentsOnFile
parameter_list|(
name|Collection
argument_list|<
name|Comment
argument_list|>
name|allComments
parameter_list|,
name|String
name|file
parameter_list|)
block|{
name|List
argument_list|<
name|Comment
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|allComments
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Comment
name|c
range|:
name|allComments
control|)
block|{
name|String
name|currentFilename
init|=
name|c
operator|.
name|key
operator|.
name|filename
decl_stmt|;
if|if
condition|(
name|currentFilename
operator|.
name|equals
argument_list|(
name|file
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sort
argument_list|(
name|result
argument_list|)
return|;
block|}
DECL|method|commentsOnPatchSet ( Collection<T> allComments, PatchSet.Id psId)
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|Comment
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|commentsOnPatchSet
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|allComments
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
block|{
name|List
argument_list|<
name|T
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|allComments
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|T
name|c
range|:
name|allComments
control|)
block|{
if|if
condition|(
name|c
operator|.
name|key
operator|.
name|patchSetId
operator|==
name|psId
operator|.
name|get
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sort
argument_list|(
name|result
argument_list|)
return|;
block|}
DECL|method|setCommentCommitId (Comment c, PatchListCache cache, Change change, PatchSet ps)
specifier|public
specifier|static
name|void
name|setCommentCommitId
parameter_list|(
name|Comment
name|c
parameter_list|,
name|PatchListCache
name|cache
parameter_list|,
name|Change
name|change
parameter_list|,
name|PatchSet
name|ps
parameter_list|)
throws|throws
name|PatchListNotAvailableException
block|{
name|checkArgument
argument_list|(
name|c
operator|.
name|key
operator|.
name|patchSetId
operator|==
name|ps
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
literal|"cannot set commit ID for patch set %s on comment %s"
argument_list|,
name|ps
operator|.
name|getId
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|.
name|getCommitId
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|Side
operator|.
name|fromShort
argument_list|(
name|c
operator|.
name|side
argument_list|)
operator|==
name|Side
operator|.
name|PARENT
condition|)
block|{
if|if
condition|(
name|c
operator|.
name|side
operator|<
literal|0
condition|)
block|{
name|c
operator|.
name|setCommitId
argument_list|(
name|cache
operator|.
name|getOldId
argument_list|(
name|change
argument_list|,
name|ps
argument_list|,
operator|-
name|c
operator|.
name|side
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|c
operator|.
name|setCommitId
argument_list|(
name|cache
operator|.
name|getOldId
argument_list|(
name|change
argument_list|,
name|ps
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|c
operator|.
name|setCommitId
argument_list|(
name|ps
operator|.
name|getCommitId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Get NoteDb draft refs for a change.    *    *<p>Works if NoteDb is not enabled, but the results are not meaningful.    *    *<p>This is just a simple ref scan, so the results may potentially include refs for zombie draft    * comments. A zombie draft is one which has been published but the write to delete the draft ref    * from All-Users failed.    *    * @param changeId change ID.    * @return raw refs from All-Users repo.    */
DECL|method|getDraftRefs (Change.Id changeId)
specifier|public
name|Collection
argument_list|<
name|Ref
argument_list|>
name|getDraftRefs
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|)
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
name|allUsers
argument_list|)
init|)
block|{
return|return
name|getDraftRefs
argument_list|(
name|repo
argument_list|,
name|changeId
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|StorageException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getDraftRefs (Repository repo, Change.Id changeId)
specifier|private
name|Collection
argument_list|<
name|Ref
argument_list|>
name|getDraftRefs
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|Change
operator|.
name|Id
name|changeId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|repo
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|getRefsByPrefix
argument_list|(
name|RefNames
operator|.
name|refsDraftCommentsPrefix
argument_list|(
name|changeId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|sort (List<T> comments)
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|Comment
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|sort
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|comments
parameter_list|)
block|{
name|comments
operator|.
name|sort
argument_list|(
name|COMMENT_ORDER
argument_list|)
expr_stmt|;
return|return
name|comments
return|;
block|}
DECL|method|toPatchLineComments ( Change.Id changeId, PatchLineComment.Status status, Iterable<Comment> comments)
specifier|public
specifier|static
name|Iterable
argument_list|<
name|PatchLineComment
argument_list|>
name|toPatchLineComments
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|PatchLineComment
operator|.
name|Status
name|status
parameter_list|,
name|Iterable
argument_list|<
name|Comment
argument_list|>
name|comments
parameter_list|)
block|{
return|return
name|FluentIterable
operator|.
name|from
argument_list|(
name|comments
argument_list|)
operator|.
name|transform
argument_list|(
name|c
lambda|->
name|PatchLineComment
operator|.
name|from
argument_list|(
name|changeId
argument_list|,
name|status
argument_list|,
name|c
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toComments ( final String serverId, Iterable<PatchLineComment> comments)
specifier|public
specifier|static
name|List
argument_list|<
name|Comment
argument_list|>
name|toComments
parameter_list|(
specifier|final
name|String
name|serverId
parameter_list|,
name|Iterable
argument_list|<
name|PatchLineComment
argument_list|>
name|comments
parameter_list|)
block|{
return|return
name|COMMENT_ORDER
operator|.
name|sortedCopy
argument_list|(
name|FluentIterable
operator|.
name|from
argument_list|(
name|comments
argument_list|)
operator|.
name|transform
argument_list|(
name|plc
lambda|->
name|plc
operator|.
name|asComment
argument_list|(
name|serverId
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

