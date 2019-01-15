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
DECL|package|com.google.gerrit.server.restapi.account
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
name|account
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
name|collect
operator|.
name|ImmutableList
operator|.
name|toImmutableList
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
name|CharMatcher
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
name|extensions
operator|.
name|api
operator|.
name|accounts
operator|.
name|DeleteDraftCommentsInput
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
name|accounts
operator|.
name|DeletedDraftCommentInfo
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
name|ListChangesOption
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
name|AuthException
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
name|index
operator|.
name|query
operator|.
name|Predicate
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
name|index
operator|.
name|query
operator|.
name|QueryParseException
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
name|CurrentUser
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
name|account
operator|.
name|AccountResource
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
name|ChangeJson
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
name|query
operator|.
name|change
operator|.
name|ChangeData
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
name|query
operator|.
name|change
operator|.
name|ChangeQueryBuilder
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
name|query
operator|.
name|change
operator|.
name|HasDraftByPredicate
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
name|query
operator|.
name|change
operator|.
name|InternalChangeQuery
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
name|restapi
operator|.
name|change
operator|.
name|CommentJson
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
name|restapi
operator|.
name|change
operator|.
name|CommentJson
operator|.
name|CommentFormatter
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
name|BatchUpdate
operator|.
name|Factory
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
name|BatchUpdateListener
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
name|LinkedHashMap
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
name|Objects
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|DeleteDraftComments
specifier|public
class|class
name|DeleteDraftComments
implements|implements
name|RestModifyView
argument_list|<
name|AccountResource
argument_list|,
name|DeleteDraftCommentsInput
argument_list|>
block|{
DECL|field|userProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
decl_stmt|;
DECL|field|batchUpdateFactory
specifier|private
specifier|final
name|BatchUpdate
operator|.
name|Factory
name|batchUpdateFactory
decl_stmt|;
DECL|field|queryBuilderProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ChangeQueryBuilder
argument_list|>
name|queryBuilderProvider
decl_stmt|;
DECL|field|queryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
decl_stmt|;
DECL|field|changeDataFactory
specifier|private
specifier|final
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
decl_stmt|;
DECL|field|changeJsonFactory
specifier|private
specifier|final
name|ChangeJson
operator|.
name|Factory
name|changeJsonFactory
decl_stmt|;
DECL|field|commentJsonProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|CommentJson
argument_list|>
name|commentJsonProvider
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
annotation|@
name|Inject
DECL|method|DeleteDraftComments ( Provider<CurrentUser> userProvider, Factory batchUpdateFactory, Provider<ChangeQueryBuilder> queryBuilderProvider, Provider<InternalChangeQuery> queryProvider, ChangeData.Factory changeDataFactory, ChangeJson.Factory changeJsonFactory, Provider<CommentJson> commentJsonProvider, CommentsUtil commentsUtil, PatchSetUtil psUtil, PatchListCache patchListCache)
name|DeleteDraftComments
parameter_list|(
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
parameter_list|,
name|Factory
name|batchUpdateFactory
parameter_list|,
name|Provider
argument_list|<
name|ChangeQueryBuilder
argument_list|>
name|queryBuilderProvider
parameter_list|,
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|,
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|,
name|ChangeJson
operator|.
name|Factory
name|changeJsonFactory
parameter_list|,
name|Provider
argument_list|<
name|CommentJson
argument_list|>
name|commentJsonProvider
parameter_list|,
name|CommentsUtil
name|commentsUtil
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
name|PatchListCache
name|patchListCache
parameter_list|)
block|{
name|this
operator|.
name|userProvider
operator|=
name|userProvider
expr_stmt|;
name|this
operator|.
name|batchUpdateFactory
operator|=
name|batchUpdateFactory
expr_stmt|;
name|this
operator|.
name|queryBuilderProvider
operator|=
name|queryBuilderProvider
expr_stmt|;
name|this
operator|.
name|queryProvider
operator|=
name|queryProvider
expr_stmt|;
name|this
operator|.
name|changeDataFactory
operator|=
name|changeDataFactory
expr_stmt|;
name|this
operator|.
name|changeJsonFactory
operator|=
name|changeJsonFactory
expr_stmt|;
name|this
operator|.
name|commentJsonProvider
operator|=
name|commentJsonProvider
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
block|}
annotation|@
name|Override
DECL|method|apply ( AccountResource rsrc, DeleteDraftCommentsInput input)
specifier|public
name|ImmutableList
argument_list|<
name|DeletedDraftCommentInfo
argument_list|>
name|apply
parameter_list|(
name|AccountResource
name|rsrc
parameter_list|,
name|DeleteDraftCommentsInput
name|input
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|OrmException
throws|,
name|UpdateException
block|{
name|CurrentUser
name|user
init|=
name|userProvider
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|user
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Authentication required"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|hasSameAccountId
argument_list|(
name|user
argument_list|)
condition|)
block|{
comment|// Disallow even for admins or users with Modify Account. Drafts are not like preferences or
comment|// other account info; there is no way even for admins to read or delete another user's drafts
comment|// using the normal draft endpoints under the change resource, so disallow it here as well.
comment|// (Admins may still call this endpoint with impersonation, but in that case it would pass the
comment|// hasSameAccountId check.)
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Cannot delete drafts of other user"
argument_list|)
throw|;
block|}
name|CommentFormatter
name|commentFormatter
init|=
name|commentJsonProvider
operator|.
name|get
argument_list|()
operator|.
name|newCommentFormatter
argument_list|()
decl_stmt|;
name|Account
operator|.
name|Id
name|accountId
init|=
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
name|Timestamp
name|now
init|=
name|TimeUtil
operator|.
name|nowTs
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|BatchUpdate
argument_list|>
name|updates
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Op
argument_list|>
name|ops
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|queryProvider
operator|.
name|get
argument_list|()
comment|// Don't attempt to mutate any changes the user can't currently see.
operator|.
name|enforceVisibility
argument_list|(
literal|true
argument_list|)
operator|.
name|query
argument_list|(
name|predicate
argument_list|(
name|accountId
argument_list|,
name|input
argument_list|)
argument_list|)
control|)
block|{
name|BatchUpdate
name|update
init|=
name|updates
operator|.
name|computeIfAbsent
argument_list|(
name|cd
operator|.
name|project
argument_list|()
argument_list|,
name|p
lambda|->
name|batchUpdateFactory
operator|.
name|create
argument_list|(
name|p
argument_list|,
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|,
name|now
argument_list|)
argument_list|)
decl_stmt|;
name|Op
name|op
init|=
operator|new
name|Op
argument_list|(
name|commentFormatter
argument_list|,
name|accountId
argument_list|)
decl_stmt|;
name|update
operator|.
name|addOp
argument_list|(
name|cd
operator|.
name|getId
argument_list|()
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|ops
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
comment|// Currently there's no way to let some updates succeed even if others fail. Even if there were,
comment|// all updates from this operation only happen in All-Users and thus are fully atomic, so
comment|// allowing partial failure would have little value.
name|BatchUpdate
operator|.
name|execute
argument_list|(
name|updates
operator|.
name|values
argument_list|()
argument_list|,
name|BatchUpdateListener
operator|.
name|NONE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|ops
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Op
operator|::
name|getResult
argument_list|)
operator|.
name|filter
argument_list|(
name|Objects
operator|::
name|nonNull
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableList
argument_list|()
argument_list|)
return|;
block|}
DECL|method|predicate (Account.Id accountId, DeleteDraftCommentsInput input)
specifier|private
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|predicate
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|DeleteDraftCommentsInput
name|input
parameter_list|)
throws|throws
name|BadRequestException
block|{
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|hasDraft
init|=
operator|new
name|HasDraftByPredicate
argument_list|(
name|accountId
argument_list|)
decl_stmt|;
if|if
condition|(
name|CharMatcher
operator|.
name|whitespace
argument_list|()
operator|.
name|trimFrom
argument_list|(
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|input
operator|.
name|query
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|hasDraft
return|;
block|}
try|try
block|{
return|return
name|Predicate
operator|.
name|and
argument_list|(
name|hasDraft
argument_list|,
name|queryBuilderProvider
operator|.
name|get
argument_list|()
operator|.
name|parse
argument_list|(
name|input
operator|.
name|query
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|QueryParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Invalid query: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|class|Op
specifier|private
class|class
name|Op
implements|implements
name|BatchUpdateOp
block|{
DECL|field|commentFormatter
specifier|private
specifier|final
name|CommentFormatter
name|commentFormatter
decl_stmt|;
DECL|field|accountId
specifier|private
specifier|final
name|Account
operator|.
name|Id
name|accountId
decl_stmt|;
DECL|field|result
specifier|private
name|DeletedDraftCommentInfo
name|result
decl_stmt|;
DECL|method|Op (CommentFormatter commentFormatter, Account.Id accountId)
name|Op
parameter_list|(
name|CommentFormatter
name|commentFormatter
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
name|this
operator|.
name|commentFormatter
operator|=
name|commentFormatter
expr_stmt|;
name|this
operator|.
name|accountId
operator|=
name|accountId
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
name|OrmException
throws|,
name|PatchListNotAvailableException
throws|,
name|PermissionBackendException
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|CommentInfo
argument_list|>
name|comments
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
name|boolean
name|dirty
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Comment
name|c
range|:
name|commentsUtil
operator|.
name|draftByChangeAuthor
argument_list|(
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|,
name|accountId
argument_list|)
control|)
block|{
name|dirty
operator|=
literal|true
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
name|c
operator|.
name|key
operator|.
name|patchSetId
argument_list|)
decl_stmt|;
name|setCommentRevId
argument_list|(
name|c
argument_list|,
name|patchListCache
argument_list|,
name|ctx
operator|.
name|getChange
argument_list|()
argument_list|,
name|psUtil
operator|.
name|get
argument_list|(
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|,
name|psId
argument_list|)
argument_list|)
expr_stmt|;
name|commentsUtil
operator|.
name|deleteComments
argument_list|(
name|ctx
operator|.
name|getUpdate
argument_list|(
name|psId
argument_list|)
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|comments
operator|.
name|add
argument_list|(
name|commentFormatter
operator|.
name|format
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dirty
condition|)
block|{
name|result
operator|=
operator|new
name|DeletedDraftCommentInfo
argument_list|()
expr_stmt|;
name|result
operator|.
name|change
operator|=
name|changeJsonFactory
operator|.
name|create
argument_list|(
name|ListChangesOption
operator|.
name|SKIP_MERGEABLE
argument_list|)
operator|.
name|format
argument_list|(
name|changeDataFactory
operator|.
name|create
argument_list|(
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|deleted
operator|=
name|comments
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
return|return
name|dirty
return|;
block|}
annotation|@
name|Nullable
DECL|method|getResult ()
name|DeletedDraftCommentInfo
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
block|}
block|}
end_class

end_unit

