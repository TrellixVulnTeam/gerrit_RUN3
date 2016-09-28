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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|common
operator|.
name|base
operator|.
name|Strings
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
name|TimeUtil
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
name|RestModifyView
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
name|ChangeUtil
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
name|git
operator|.
name|BatchUpdate
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
name|git
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
name|util
operator|.
name|Collections
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|CreateDraftComment
specifier|public
class|class
name|CreateDraftComment
implements|implements
name|RestModifyView
argument_list|<
name|RevisionResource
argument_list|,
name|DraftInput
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
DECL|field|updateFactory
specifier|private
specifier|final
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
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
DECL|field|patchListCache
specifier|private
specifier|final
name|PatchListCache
name|patchListCache
decl_stmt|;
DECL|field|serverId
specifier|private
specifier|final
name|String
name|serverId
decl_stmt|;
annotation|@
name|Inject
DECL|method|CreateDraftComment (Provider<ReviewDb> db, BatchUpdate.Factory updateFactory, Provider<CommentJson> commentJson, CommentsUtil commentsUtil, PatchSetUtil psUtil, PatchListCache patchListCache, @GerritServerId String serverId)
name|CreateDraftComment
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|Provider
argument_list|<
name|CommentJson
argument_list|>
name|commentJson
parameter_list|,
name|CommentsUtil
name|commentsUtil
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
name|PatchListCache
name|patchListCache
parameter_list|,
annotation|@
name|GerritServerId
name|String
name|serverId
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
name|updateFactory
operator|=
name|updateFactory
expr_stmt|;
name|this
operator|.
name|commentJson
operator|=
name|commentJson
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
name|patchListCache
operator|=
name|patchListCache
expr_stmt|;
name|this
operator|.
name|serverId
operator|=
name|serverId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (RevisionResource rsrc, DraftInput in)
specifier|public
name|Response
argument_list|<
name|CommentInfo
argument_list|>
name|apply
parameter_list|(
name|RevisionResource
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
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|in
operator|.
name|path
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"path must be non-empty"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
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
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"message must be non-empty"
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
name|getPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
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
name|created
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
extends|extends
name|BatchUpdate
operator|.
name|Op
block|{
DECL|field|psId
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|psId
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
DECL|method|Op (PatchSet.Id psId, DraftInput in)
specifier|private
name|Op
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|DraftInput
name|in
parameter_list|)
block|{
name|this
operator|.
name|psId
operator|=
name|psId
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
block|{
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
name|comment
operator|=
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
name|messageUUID
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|)
argument_list|,
name|in
operator|.
name|path
argument_list|,
name|ps
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
argument_list|,
name|ctx
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|,
name|in
operator|.
name|side
argument_list|()
argument_list|,
name|in
operator|.
name|message
operator|.
name|trim
argument_list|()
argument_list|,
name|serverId
argument_list|)
expr_stmt|;
name|comment
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
name|comment
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
name|comment
operator|.
name|tag
operator|=
name|in
operator|.
name|tag
expr_stmt|;
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
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|,
name|ctx
operator|.
name|getUpdate
argument_list|(
name|psId
argument_list|)
argument_list|,
name|Status
operator|.
name|DRAFT
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|comment
argument_list|)
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|bumpLastUpdatedOn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

