begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.mail.receive
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|mail
operator|.
name|receive
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
name|ArrayListMultimap
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
name|NotifyHandling
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
name|registration
operator|.
name|DynamicMap
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
name|ChangeMessage
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
name|ApprovalsUtil
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
name|ChangeMessagesUtil
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
name|account
operator|.
name|AccountByEmailCache
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
name|AccountCache
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
name|EmailReviewComments
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
name|CanonicalWebUrl
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
name|extensions
operator|.
name|events
operator|.
name|CommentAdded
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
name|mail
operator|.
name|MailFilter
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
name|project
operator|.
name|ChangeControl
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
name|Context
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
name|ManualRequestContext
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
name|OneOffRequestContext
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

begin_comment
comment|/** A service that can attach the comments from a {@link MailMessage} to a change. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|MailProcessor
specifier|public
class|class
name|MailProcessor
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
name|MailProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|accountByEmailCache
specifier|private
specifier|final
name|AccountByEmailCache
name|accountByEmailCache
decl_stmt|;
DECL|field|retryHelper
specifier|private
specifier|final
name|RetryHelper
name|retryHelper
decl_stmt|;
DECL|field|changeMessagesUtil
specifier|private
specifier|final
name|ChangeMessagesUtil
name|changeMessagesUtil
decl_stmt|;
DECL|field|commentsUtil
specifier|private
specifier|final
name|CommentsUtil
name|commentsUtil
decl_stmt|;
DECL|field|oneOffRequestContext
specifier|private
specifier|final
name|OneOffRequestContext
name|oneOffRequestContext
decl_stmt|;
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
DECL|field|queryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
decl_stmt|;
DECL|field|mailFilters
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|MailFilter
argument_list|>
name|mailFilters
decl_stmt|;
DECL|field|outgoingMailFactory
specifier|private
specifier|final
name|EmailReviewComments
operator|.
name|Factory
name|outgoingMailFactory
decl_stmt|;
DECL|field|commentAdded
specifier|private
specifier|final
name|CommentAdded
name|commentAdded
decl_stmt|;
DECL|field|approvalsUtil
specifier|private
specifier|final
name|ApprovalsUtil
name|approvalsUtil
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|canonicalUrl
specifier|private
specifier|final
name|Provider
argument_list|<
name|String
argument_list|>
name|canonicalUrl
decl_stmt|;
annotation|@
name|Inject
DECL|method|MailProcessor ( AccountByEmailCache accountByEmailCache, RetryHelper retryHelper, ChangeMessagesUtil changeMessagesUtil, CommentsUtil commentsUtil, OneOffRequestContext oneOffRequestContext, PatchListCache patchListCache, PatchSetUtil psUtil, Provider<InternalChangeQuery> queryProvider, DynamicMap<MailFilter> mailFilters, EmailReviewComments.Factory outgoingMailFactory, ApprovalsUtil approvalsUtil, CommentAdded commentAdded, AccountCache accountCache, @CanonicalWebUrl Provider<String> canonicalUrl)
specifier|public
name|MailProcessor
parameter_list|(
name|AccountByEmailCache
name|accountByEmailCache
parameter_list|,
name|RetryHelper
name|retryHelper
parameter_list|,
name|ChangeMessagesUtil
name|changeMessagesUtil
parameter_list|,
name|CommentsUtil
name|commentsUtil
parameter_list|,
name|OneOffRequestContext
name|oneOffRequestContext
parameter_list|,
name|PatchListCache
name|patchListCache
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|,
name|DynamicMap
argument_list|<
name|MailFilter
argument_list|>
name|mailFilters
parameter_list|,
name|EmailReviewComments
operator|.
name|Factory
name|outgoingMailFactory
parameter_list|,
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
name|CommentAdded
name|commentAdded
parameter_list|,
name|AccountCache
name|accountCache
parameter_list|,
annotation|@
name|CanonicalWebUrl
name|Provider
argument_list|<
name|String
argument_list|>
name|canonicalUrl
parameter_list|)
block|{
name|this
operator|.
name|accountByEmailCache
operator|=
name|accountByEmailCache
expr_stmt|;
name|this
operator|.
name|retryHelper
operator|=
name|retryHelper
expr_stmt|;
name|this
operator|.
name|changeMessagesUtil
operator|=
name|changeMessagesUtil
expr_stmt|;
name|this
operator|.
name|commentsUtil
operator|=
name|commentsUtil
expr_stmt|;
name|this
operator|.
name|oneOffRequestContext
operator|=
name|oneOffRequestContext
expr_stmt|;
name|this
operator|.
name|patchListCache
operator|=
name|patchListCache
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
name|this
operator|.
name|queryProvider
operator|=
name|queryProvider
expr_stmt|;
name|this
operator|.
name|mailFilters
operator|=
name|mailFilters
expr_stmt|;
name|this
operator|.
name|outgoingMailFactory
operator|=
name|outgoingMailFactory
expr_stmt|;
name|this
operator|.
name|commentAdded
operator|=
name|commentAdded
expr_stmt|;
name|this
operator|.
name|approvalsUtil
operator|=
name|approvalsUtil
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|canonicalUrl
operator|=
name|canonicalUrl
expr_stmt|;
block|}
comment|/**    * Parses comments from a {@link MailMessage} and persists them on the change.    *    * @param message {@link MailMessage} to process    */
DECL|method|process (MailMessage message)
specifier|public
name|void
name|process
parameter_list|(
name|MailMessage
name|message
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|UpdateException
block|{
name|retryHelper
operator|.
name|execute
argument_list|(
name|buf
lambda|->
block|{
name|processImpl
argument_list|(
name|buf
argument_list|,
name|message
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|processImpl (BatchUpdate.Factory buf, MailMessage message)
specifier|private
name|void
name|processImpl
parameter_list|(
name|BatchUpdate
operator|.
name|Factory
name|buf
parameter_list|,
name|MailMessage
name|message
parameter_list|)
throws|throws
name|OrmException
throws|,
name|UpdateException
throws|,
name|RestApiException
block|{
for|for
control|(
name|DynamicMap
operator|.
name|Entry
argument_list|<
name|MailFilter
argument_list|>
name|filter
range|:
name|mailFilters
control|)
block|{
if|if
condition|(
operator|!
name|filter
operator|.
name|getProvider
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|shouldProcessMessage
argument_list|(
name|message
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Message %s filtered by plugin %s %s. Will delete message."
argument_list|,
name|message
operator|.
name|id
argument_list|()
argument_list|,
name|filter
operator|.
name|getPluginName
argument_list|()
argument_list|,
name|filter
operator|.
name|getExportName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|MailMetadata
name|metadata
init|=
name|MetadataParser
operator|.
name|parse
argument_list|(
name|message
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|metadata
operator|.
name|hasRequiredFields
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Message %s is missing required metadata, have %s. Will delete message."
argument_list|,
name|message
operator|.
name|id
argument_list|()
argument_list|,
name|metadata
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|accounts
init|=
name|accountByEmailCache
operator|.
name|get
argument_list|(
name|metadata
operator|.
name|author
argument_list|)
decl_stmt|;
if|if
condition|(
name|accounts
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Address %s could not be matched to a unique account. It was matched to %s. Will delete message."
argument_list|,
name|metadata
operator|.
name|author
argument_list|,
name|accounts
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|Account
operator|.
name|Id
name|account
init|=
name|accounts
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|accountCache
operator|.
name|get
argument_list|(
name|account
argument_list|)
operator|.
name|getAccount
argument_list|()
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Mail: Account %s is inactive. Will delete message."
argument_list|,
name|account
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|persistComments
argument_list|(
name|buf
argument_list|,
name|message
argument_list|,
name|metadata
argument_list|,
name|account
argument_list|)
expr_stmt|;
block|}
DECL|method|persistComments ( BatchUpdate.Factory buf, MailMessage message, MailMetadata metadata, Account.Id sender)
specifier|private
name|void
name|persistComments
parameter_list|(
name|BatchUpdate
operator|.
name|Factory
name|buf
parameter_list|,
name|MailMessage
name|message
parameter_list|,
name|MailMetadata
name|metadata
parameter_list|,
name|Account
operator|.
name|Id
name|sender
parameter_list|)
throws|throws
name|OrmException
throws|,
name|UpdateException
throws|,
name|RestApiException
block|{
try|try
init|(
name|ManualRequestContext
name|ctx
init|=
name|oneOffRequestContext
operator|.
name|openAs
argument_list|(
name|sender
argument_list|)
init|)
block|{
name|List
argument_list|<
name|ChangeData
argument_list|>
name|changeDataList
init|=
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|byLegacyChangeId
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
name|metadata
operator|.
name|changeNumber
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|changeDataList
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Message %s references unique change %s, but there are %d matching changes in the index. Will delete message."
argument_list|,
name|message
operator|.
name|id
argument_list|()
argument_list|,
name|metadata
operator|.
name|changeNumber
argument_list|,
name|changeDataList
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|ChangeData
name|cd
init|=
name|changeDataList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingMessageIds
argument_list|(
name|cd
argument_list|)
operator|.
name|contains
argument_list|(
name|message
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Message %s was already processed. Will delete message."
argument_list|,
name|message
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Get all comments; filter and sort them to get the original list of
comment|// comments from the outbound email.
comment|// TODO(hiesel) Also filter by original comment author.
name|Collection
argument_list|<
name|Comment
argument_list|>
name|comments
init|=
name|cd
operator|.
name|publishedComments
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|c
lambda|->
operator|(
name|c
operator|.
name|writtenOn
operator|.
name|getTime
argument_list|()
operator|/
literal|1000
operator|)
operator|==
operator|(
name|metadata
operator|.
name|timestamp
operator|.
name|getTime
argument_list|()
operator|/
literal|1000
operator|)
argument_list|)
operator|.
name|sorted
argument_list|(
name|CommentsUtil
operator|.
name|COMMENT_ORDER
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|project
init|=
name|cd
operator|.
name|project
argument_list|()
decl_stmt|;
name|String
name|changeUrl
init|=
name|canonicalUrl
operator|.
name|get
argument_list|()
operator|+
literal|"#/c/"
operator|+
name|cd
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MailComment
argument_list|>
name|parsedComments
decl_stmt|;
if|if
condition|(
name|useHtmlParser
argument_list|(
name|message
argument_list|)
condition|)
block|{
name|parsedComments
operator|=
name|HtmlParser
operator|.
name|parse
argument_list|(
name|message
argument_list|,
name|comments
argument_list|,
name|changeUrl
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parsedComments
operator|=
name|TextParser
operator|.
name|parse
argument_list|(
name|message
argument_list|,
name|comments
argument_list|,
name|changeUrl
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parsedComments
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Could not parse any comments from %s. Will delete message."
argument_list|,
name|message
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|Op
name|o
init|=
operator|new
name|Op
argument_list|(
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|cd
operator|.
name|getId
argument_list|()
argument_list|,
name|metadata
operator|.
name|patchSet
argument_list|)
argument_list|,
name|parsedComments
argument_list|,
name|message
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
name|BatchUpdate
name|batchUpdate
init|=
name|buf
operator|.
name|create
argument_list|(
name|cd
operator|.
name|db
argument_list|()
argument_list|,
name|project
argument_list|,
name|ctx
operator|.
name|getUser
argument_list|()
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|batchUpdate
operator|.
name|addOp
argument_list|(
name|cd
operator|.
name|getId
argument_list|()
argument_list|,
name|o
argument_list|)
expr_stmt|;
name|batchUpdate
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Op
specifier|private
class|class
name|Op
implements|implements
name|BatchUpdateOp
block|{
DECL|field|psId
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|psId
decl_stmt|;
DECL|field|parsedComments
specifier|private
specifier|final
name|List
argument_list|<
name|MailComment
argument_list|>
name|parsedComments
decl_stmt|;
DECL|field|tag
specifier|private
specifier|final
name|String
name|tag
decl_stmt|;
DECL|field|changeMessage
specifier|private
name|ChangeMessage
name|changeMessage
decl_stmt|;
DECL|field|comments
specifier|private
name|List
argument_list|<
name|Comment
argument_list|>
name|comments
decl_stmt|;
DECL|field|patchSet
specifier|private
name|PatchSet
name|patchSet
decl_stmt|;
DECL|field|changeControl
specifier|private
name|ChangeControl
name|changeControl
decl_stmt|;
DECL|method|Op (PatchSet.Id psId, List<MailComment> parsedComments, String messageId)
specifier|private
name|Op
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|List
argument_list|<
name|MailComment
argument_list|>
name|parsedComments
parameter_list|,
name|String
name|messageId
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
name|parsedComments
operator|=
name|parsedComments
expr_stmt|;
name|this
operator|.
name|tag
operator|=
literal|"mailMessageId="
operator|+
name|messageId
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
name|UnprocessableEntityException
block|{
name|changeControl
operator|=
name|ctx
operator|.
name|getControl
argument_list|()
expr_stmt|;
name|patchSet
operator|=
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
expr_stmt|;
if|if
condition|(
name|patchSet
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"patch set not found: "
operator|+
name|psId
argument_list|)
throw|;
block|}
name|changeMessage
operator|=
name|generateChangeMessage
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|changeMessagesUtil
operator|.
name|addChangeMessage
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
name|changeMessage
argument_list|)
expr_stmt|;
name|comments
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|MailComment
name|c
range|:
name|parsedComments
control|)
block|{
if|if
condition|(
name|c
operator|.
name|type
operator|==
name|MailComment
operator|.
name|CommentType
operator|.
name|CHANGE_MESSAGE
condition|)
block|{
continue|continue;
block|}
name|comments
operator|.
name|add
argument_list|(
name|persistentCommentFromMailComment
argument_list|(
name|ctx
argument_list|,
name|c
argument_list|,
name|targetPatchSetForComment
argument_list|(
name|ctx
argument_list|,
name|c
argument_list|,
name|patchSet
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|ctx
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
argument_list|,
name|Status
operator|.
name|PUBLISHED
argument_list|,
name|comments
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|postUpdate (Context ctx)
specifier|public
name|void
name|postUpdate
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|patchSetComment
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|parsedComments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|type
operator|==
name|MailComment
operator|.
name|CommentType
operator|.
name|CHANGE_MESSAGE
condition|)
block|{
name|patchSetComment
operator|=
name|parsedComments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|message
expr_stmt|;
block|}
comment|// Send email notifications
name|outgoingMailFactory
operator|.
name|create
argument_list|(
name|NotifyHandling
operator|.
name|ALL
argument_list|,
name|ArrayListMultimap
operator|.
name|create
argument_list|()
argument_list|,
name|changeControl
operator|.
name|getNotes
argument_list|()
argument_list|,
name|patchSet
argument_list|,
name|ctx
operator|.
name|getUser
argument_list|()
operator|.
name|asIdentifiedUser
argument_list|()
argument_list|,
name|changeMessage
argument_list|,
name|comments
argument_list|,
name|patchSetComment
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
operator|.
name|sendAsync
argument_list|()
expr_stmt|;
comment|// Get previous approvals from this user
name|Map
argument_list|<
name|String
argument_list|,
name|Short
argument_list|>
name|approvals
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|approvalsUtil
operator|.
name|byPatchSetUser
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|,
name|changeControl
argument_list|,
name|psId
argument_list|,
name|ctx
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|ctx
operator|.
name|getRevWalk
argument_list|()
argument_list|,
name|ctx
operator|.
name|getRepoView
argument_list|()
operator|.
name|getConfig
argument_list|()
argument_list|)
operator|.
name|forEach
argument_list|(
name|a
lambda|->
name|approvals
operator|.
name|put
argument_list|(
name|a
operator|.
name|getLabel
argument_list|()
argument_list|,
name|a
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Fire Gerrit event. Note that approvals can't be granted via email, so old and new approvals
comment|// are always the same here.
name|commentAdded
operator|.
name|fire
argument_list|(
name|changeControl
operator|.
name|getChange
argument_list|()
argument_list|,
name|patchSet
argument_list|,
name|ctx
operator|.
name|getAccount
argument_list|()
argument_list|,
name|changeMessage
operator|.
name|getMessage
argument_list|()
argument_list|,
name|approvals
argument_list|,
name|approvals
argument_list|,
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|generateChangeMessage (ChangeContext ctx)
specifier|private
name|ChangeMessage
name|generateChangeMessage
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|)
block|{
name|String
name|changeMsg
init|=
literal|"Patch Set "
operator|+
name|psId
operator|.
name|get
argument_list|()
operator|+
literal|":"
decl_stmt|;
if|if
condition|(
name|parsedComments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|type
operator|==
name|MailComment
operator|.
name|CommentType
operator|.
name|CHANGE_MESSAGE
condition|)
block|{
comment|// Add a blank line after Patch Set to follow the default format
if|if
condition|(
name|parsedComments
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|changeMsg
operator|+=
literal|"\n\n"
operator|+
name|numComments
argument_list|(
name|parsedComments
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|changeMsg
operator|+=
literal|"\n\n"
operator|+
name|parsedComments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|message
expr_stmt|;
block|}
else|else
block|{
name|changeMsg
operator|+=
literal|"\n\n"
operator|+
name|numComments
argument_list|(
name|parsedComments
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ChangeMessagesUtil
operator|.
name|newMessage
argument_list|(
name|ctx
argument_list|,
name|changeMsg
argument_list|,
name|tag
argument_list|)
return|;
block|}
DECL|method|targetPatchSetForComment ( ChangeContext ctx, MailComment mailComment, PatchSet current)
specifier|private
name|PatchSet
name|targetPatchSetForComment
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|,
name|MailComment
name|mailComment
parameter_list|,
name|PatchSet
name|current
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|mailComment
operator|.
name|inReplyTo
operator|!=
literal|null
condition|)
block|{
return|return
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
name|mailComment
operator|.
name|inReplyTo
operator|.
name|key
operator|.
name|patchSetId
argument_list|)
argument_list|)
return|;
block|}
return|return
name|current
return|;
block|}
DECL|method|persistentCommentFromMailComment ( ChangeContext ctx, MailComment mailComment, PatchSet patchSetForComment)
specifier|private
name|Comment
name|persistentCommentFromMailComment
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|,
name|MailComment
name|mailComment
parameter_list|,
name|PatchSet
name|patchSetForComment
parameter_list|)
throws|throws
name|OrmException
throws|,
name|UnprocessableEntityException
block|{
name|String
name|fileName
decl_stmt|;
comment|// The patch set that this comment is based on is different if this
comment|// comment was sent in reply to a comment on a previous patch set.
name|Side
name|side
decl_stmt|;
if|if
condition|(
name|mailComment
operator|.
name|inReplyTo
operator|!=
literal|null
condition|)
block|{
name|fileName
operator|=
name|mailComment
operator|.
name|inReplyTo
operator|.
name|key
operator|.
name|filename
expr_stmt|;
name|side
operator|=
name|Side
operator|.
name|fromShort
argument_list|(
name|mailComment
operator|.
name|inReplyTo
operator|.
name|side
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fileName
operator|=
name|mailComment
operator|.
name|fileName
expr_stmt|;
name|side
operator|=
name|Side
operator|.
name|REVISION
expr_stmt|;
block|}
name|Comment
name|comment
init|=
name|commentsUtil
operator|.
name|newComment
argument_list|(
name|ctx
argument_list|,
name|fileName
argument_list|,
name|patchSetForComment
operator|.
name|getId
argument_list|()
argument_list|,
operator|(
name|short
operator|)
name|side
operator|.
name|ordinal
argument_list|()
argument_list|,
name|mailComment
operator|.
name|message
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|comment
operator|.
name|tag
operator|=
name|tag
expr_stmt|;
if|if
condition|(
name|mailComment
operator|.
name|inReplyTo
operator|!=
literal|null
condition|)
block|{
name|comment
operator|.
name|parentUuid
operator|=
name|mailComment
operator|.
name|inReplyTo
operator|.
name|key
operator|.
name|uuid
expr_stmt|;
name|comment
operator|.
name|lineNbr
operator|=
name|mailComment
operator|.
name|inReplyTo
operator|.
name|lineNbr
expr_stmt|;
name|comment
operator|.
name|range
operator|=
name|mailComment
operator|.
name|inReplyTo
operator|.
name|range
expr_stmt|;
name|comment
operator|.
name|unresolved
operator|=
name|mailComment
operator|.
name|inReplyTo
operator|.
name|unresolved
expr_stmt|;
block|}
name|CommentsUtil
operator|.
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
name|patchSetForComment
argument_list|)
expr_stmt|;
return|return
name|comment
return|;
block|}
block|}
DECL|method|useHtmlParser (MailMessage m)
specifier|private
specifier|static
name|boolean
name|useHtmlParser
parameter_list|(
name|MailMessage
name|m
parameter_list|)
block|{
return|return
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|m
operator|.
name|htmlContent
argument_list|()
argument_list|)
return|;
block|}
DECL|method|numComments (int numComments)
specifier|private
specifier|static
name|String
name|numComments
parameter_list|(
name|int
name|numComments
parameter_list|)
block|{
return|return
literal|"("
operator|+
name|numComments
operator|+
operator|(
name|numComments
operator|>
literal|1
condition|?
literal|" comments)"
else|:
literal|" comment)"
operator|)
return|;
block|}
DECL|method|existingMessageIds (ChangeData cd)
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|existingMessageIds
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|OrmException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|existingMessageIds
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|cd
operator|.
name|messages
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|forEach
argument_list|(
name|m
lambda|->
block|{
name|String
name|messageId
init|=
name|CommentsUtil
operator|.
name|extractMessageId
argument_list|(
name|m
operator|.
name|getTag
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|messageId
operator|!=
literal|null
condition|)
block|{
name|existingMessageIds
operator|.
name|add
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|cd
operator|.
name|publishedComments
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|forEach
argument_list|(
name|c
lambda|->
block|{
name|String
name|messageId
init|=
name|CommentsUtil
operator|.
name|extractMessageId
argument_list|(
name|c
operator|.
name|tag
argument_list|)
decl_stmt|;
if|if
condition|(
name|messageId
operator|!=
literal|null
condition|)
block|{
name|existingMessageIds
operator|.
name|add
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|existingMessageIds
return|;
block|}
block|}
end_class

end_unit

