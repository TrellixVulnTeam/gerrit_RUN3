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
DECL|package|com.google.gerrit.server.restapi.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|restapi
operator|.
name|change
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|CommentsUtil
operator|.
name|setCommentRevId
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
name|api
operator|.
name|changes
operator|.
name|DraftInput
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
name|BadRequestException
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
name|ResourceNotFoundException
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
name|Response
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
name|RestApiException
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
name|Url
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
name|PatchLineComment
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
name|CommentsUtil
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
name|DraftCommentResource
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
name|permissions
operator|.
name|PermissionBackendException
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
name|BatchUpdate
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
name|BatchUpdateOp
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
name|gerrit
operator|.
name|server
operator|.
name|update
operator|.
name|RetryHelper
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
name|RetryingRestModifyView
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
name|UpdateException
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
name|util
operator|.
name|time
operator|.
name|TimeUtil
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
name|Provider
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
name|sql
operator|.
name|Timestamp
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
name|Optional
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|PutDraftComment
specifier|public
class|class
name|PutDraftComment
extends|extends
name|RetryingRestModifyView
argument_list|<
name|DraftCommentResource
argument_list|,
name|DraftInput
argument_list|,
name|Response
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
block|{
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|delete
specifier|private
specifier|final
name|DeleteDraftComment
name|delete
decl_stmt|;
DECL|field|commentsUtil
specifier|private
specifier|final
name|CommentsUtil
name|commentsUtil
decl_stmt|;
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
DECL|field|commentJson
specifier|private
specifier|final
name|Provider
argument_list|<
name|CommentJson
argument_list|>
name|commentJson
decl_stmt|;
DECL|field|patchListCache
specifier|private
specifier|final
name|PatchListCache
name|patchListCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|PutDraftComment ( Provider<ReviewDb> db, DeleteDraftComment delete, CommentsUtil commentsUtil, PatchSetUtil psUtil, RetryHelper retryHelper, Provider<CommentJson> commentJson, PatchListCache patchListCache)
name|PutDraftComment
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|DeleteDraftComment
name|delete
parameter_list|,
name|CommentsUtil
name|commentsUtil
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
name|RetryHelper
name|retryHelper
parameter_list|,
name|Provider
argument_list|<
name|CommentJson
argument_list|>
name|commentJson
parameter_list|,
name|PatchListCache
name|patchListCache
parameter_list|)
block|{
name|super
argument_list|(
name|retryHelper
argument_list|)
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|delete
operator|=
name|delete
expr_stmt|;
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
name|commentJson
operator|=
name|commentJson
expr_stmt|;
name|this
operator|.
name|patchListCache
operator|=
name|patchListCache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|applyImpl ( BatchUpdate.Factory updateFactory, DraftCommentResource rsrc, DraftInput in)
specifier|protected
name|Response
argument_list|<
name|CommentInfo
argument_list|>
name|applyImpl
parameter_list|(
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|DraftCommentResource
name|rsrc
parameter_list|,
name|DraftInput
name|in
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|UpdateException
throws|,
name|OrmException
throws|,
name|PermissionBackendException
block|{
if|if
condition|(
name|in
operator|==
literal|null
operator|||
name|in
operator|.
name|message
operator|==
literal|null
operator|||
name|in
operator|.
name|message
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|delete
operator|.
name|applyImpl
argument_list|(
name|updateFactory
argument_list|,
name|rsrc
argument_list|,
literal|null
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|in
operator|.
name|id
operator|!=
literal|null
operator|&&
operator|!
name|rsrc
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|in
operator|.
name|id
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"id must match URL"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|in
operator|.
name|line
operator|!=
literal|null
operator|&&
name|in
operator|.
name|line
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"line must be>= 0"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|in
operator|.
name|line
operator|!=
literal|null
operator|&&
name|in
operator|.
name|range
operator|!=
literal|null
operator|&&
name|in
operator|.
name|line
operator|!=
name|in
operator|.
name|range
operator|.
name|endLine
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"range endLine must be on the same line as the comment"
argument_list|)
throw|;
block|}
try|try
init|(
name|BatchUpdate
name|bu
init|=
name|updateFactory
operator|.
name|create
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getChange
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
init|)
block|{
name|Op
name|op
init|=
operator|new
name|Op
argument_list|(
name|rsrc
operator|.
name|getComment
argument_list|()
operator|.
name|key
argument_list|,
name|in
argument_list|)
decl_stmt|;
name|bu
operator|.
name|addOp
argument_list|(
name|rsrc
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|bu
operator|.
name|execute
argument_list|()
expr_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|(
name|commentJson
operator|.
name|get
argument_list|()
operator|.
name|setFillAccounts
argument_list|(
literal|false
argument_list|)
operator|.
name|newCommentFormatter
argument_list|()
operator|.
name|format
argument_list|(
name|op
operator|.
name|comment
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|Op
specifier|private
class|class
name|Op
implements|implements
name|BatchUpdateOp
block|{
DECL|field|key
specifier|private
specifier|final
name|Comment
operator|.
name|Key
name|key
decl_stmt|;
DECL|field|in
specifier|private
specifier|final
name|DraftInput
name|in
decl_stmt|;
DECL|field|comment
specifier|private
name|Comment
name|comment
decl_stmt|;
DECL|method|Op (Comment.Key key, DraftInput in)
specifier|private
name|Op
parameter_list|(
name|Comment
operator|.
name|Key
name|key
parameter_list|,
name|DraftInput
name|in
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateChange (ChangeContext ctx)
specifier|public
name|boolean
name|updateChange
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|)
throws|throws
name|ResourceNotFoundException
throws|,
name|OrmException
throws|,
name|PatchListNotAvailableException
block|{
name|Optional
argument_list|<
name|Comment
argument_list|>
name|maybeComment
init|=
name|commentsUtil
operator|.
name|getDraft
argument_list|(
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|,
name|ctx
operator|.
name|getIdentifiedUser
argument_list|()
argument_list|,
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|maybeComment
operator|.
name|isPresent
argument_list|()
condition|)
block|{
comment|// Disappeared out from under us. Can't easily fall back to insert,
comment|// because the input might be missing required fields. Just give up.
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
literal|"comment not found: "
operator|+
name|key
argument_list|)
throw|;
block|}
name|Comment
name|origComment
init|=
name|maybeComment
operator|.
name|get
argument_list|()
decl_stmt|;
name|comment
operator|=
operator|new
name|Comment
argument_list|(
name|origComment
argument_list|)
expr_stmt|;
comment|// Copy constructor preserved old real author; replace with current real
comment|// user.
name|ctx
operator|.
name|getUser
argument_list|()
operator|.
name|updateRealAccountId
argument_list|(
name|comment
operator|::
name|setRealAuthor
argument_list|)
expr_stmt|;
name|PatchSet
operator|.
name|Id
name|psId
init|=
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|ctx
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|origComment
operator|.
name|key
operator|.
name|patchSetId
argument_list|)
decl_stmt|;
name|ChangeUpdate
name|update
init|=
name|ctx
operator|.
name|getUpdate
argument_list|(
name|psId
argument_list|)
decl_stmt|;
name|PatchSet
name|ps
init|=
name|psUtil
operator|.
name|get
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|,
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|,
name|psId
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
name|ResourceNotFoundException
argument_list|(
literal|"patch set not found: "
operator|+
name|psId
argument_list|)
throw|;
block|}
if|if
condition|(
name|in
operator|.
name|path
operator|!=
literal|null
operator|&&
operator|!
name|in
operator|.
name|path
operator|.
name|equals
argument_list|(
name|origComment
operator|.
name|key
operator|.
name|filename
argument_list|)
condition|)
block|{
comment|// Updating the path alters the primary key, which isn't possible.
comment|// Delete then recreate the comment instead of an update.
name|commentsUtil
operator|.
name|deleteComments
argument_list|(
name|update
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|origComment
argument_list|)
argument_list|)
expr_stmt|;
name|comment
operator|.
name|key
operator|.
name|filename
operator|=
name|in
operator|.
name|path
expr_stmt|;
block|}
name|setCommentRevId
argument_list|(
name|comment
argument_list|,
name|patchListCache
argument_list|,
name|ctx
operator|.
name|getChange
argument_list|()
argument_list|,
name|ps
argument_list|)
expr_stmt|;
name|commentsUtil
operator|.
name|putComments
argument_list|(
name|update
argument_list|,
name|Status
operator|.
name|DRAFT
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|update
argument_list|(
name|comment
argument_list|,
name|in
argument_list|,
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|dontBumpLastUpdatedOn
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
DECL|method|update (Comment e, DraftInput in, Timestamp when)
specifier|private
specifier|static
name|Comment
name|update
parameter_list|(
name|Comment
name|e
parameter_list|,
name|DraftInput
name|in
parameter_list|,
name|Timestamp
name|when
parameter_list|)
block|{
if|if
condition|(
name|in
operator|.
name|side
operator|!=
literal|null
condition|)
block|{
name|e
operator|.
name|side
operator|=
name|in
operator|.
name|side
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|inReplyTo
operator|!=
literal|null
condition|)
block|{
name|e
operator|.
name|parentUuid
operator|=
name|Url
operator|.
name|decode
argument_list|(
name|in
operator|.
name|inReplyTo
argument_list|)
expr_stmt|;
block|}
name|e
operator|.
name|setLineNbrAndRange
argument_list|(
name|in
operator|.
name|line
argument_list|,
name|in
operator|.
name|range
argument_list|)
expr_stmt|;
name|e
operator|.
name|message
operator|=
name|in
operator|.
name|message
operator|.
name|trim
argument_list|()
expr_stmt|;
name|e
operator|.
name|writtenOn
operator|=
name|when
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|tag
operator|!=
literal|null
condition|)
block|{
comment|// TODO(dborowitz): Can we support changing tags via PUT?
name|e
operator|.
name|tag
operator|=
name|in
operator|.
name|tag
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|unresolved
operator|!=
literal|null
condition|)
block|{
name|e
operator|.
name|unresolved
operator|=
name|in
operator|.
name|unresolved
expr_stmt|;
block|}
return|return
name|e
return|;
block|}
block|}
end_class

end_unit

