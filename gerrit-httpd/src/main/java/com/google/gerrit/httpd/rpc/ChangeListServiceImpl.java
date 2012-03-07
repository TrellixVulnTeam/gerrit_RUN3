begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.httpd.rpc
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|rpc
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
name|common
operator|.
name|data
operator|.
name|AccountDashboardInfo
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
name|data
operator|.
name|ChangeInfo
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
name|data
operator|.
name|ChangeListService
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
name|data
operator|.
name|GlobalCapability
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
name|data
operator|.
name|SingleListChangeInfo
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
name|data
operator|.
name|ToggleStarRequest
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
name|errors
operator|.
name|InvalidQueryException
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
name|errors
operator|.
name|NoSuchEntityException
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
name|PatchSetApproval
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
name|StarredChange
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
name|ChangeAccess
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
name|account
operator|.
name|AccountControl
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
name|AccountInfoCacheFactory
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
name|project
operator|.
name|NoSuchChangeException
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
name|server
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
name|ChangeDataSource
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
name|ChangeQueryRewriter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|rpc
operator|.
name|AsyncCallback
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|client
operator|.
name|VoidResult
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
name|ListResultSet
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
name|gwtorm
operator|.
name|server
operator|.
name|ResultSet
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
name|Comparator
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
name|Set
import|;
end_import

begin_class
DECL|class|ChangeListServiceImpl
specifier|public
class|class
name|ChangeListServiceImpl
extends|extends
name|BaseServiceImplementation
implements|implements
name|ChangeListService
block|{
DECL|field|ID_COMP
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|ChangeInfo
argument_list|>
name|ID_COMP
init|=
operator|new
name|Comparator
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|ChangeInfo
name|o1
parameter_list|,
specifier|final
name|ChangeInfo
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
operator|-
name|o2
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|SORT_KEY_COMP
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|ChangeInfo
argument_list|>
name|SORT_KEY_COMP
init|=
operator|new
name|Comparator
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|ChangeInfo
name|o1
parameter_list|,
specifier|final
name|ChangeInfo
name|o2
parameter_list|)
block|{
return|return
name|o2
operator|.
name|getSortKey
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o1
operator|.
name|getSortKey
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|QUERY_PREV
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|Change
argument_list|>
name|QUERY_PREV
init|=
operator|new
name|Comparator
argument_list|<
name|Change
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|Change
name|a
parameter_list|,
specifier|final
name|Change
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|getSortKey
argument_list|()
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|getSortKey
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|QUERY_NEXT
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|Change
argument_list|>
name|QUERY_NEXT
init|=
operator|new
name|Comparator
argument_list|<
name|Change
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|Change
name|a
parameter_list|,
specifier|final
name|Change
name|b
parameter_list|)
block|{
return|return
name|b
operator|.
name|getSortKey
argument_list|()
operator|.
name|compareTo
argument_list|(
name|a
operator|.
name|getSortKey
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|currentUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
decl_stmt|;
DECL|field|changeControlFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
decl_stmt|;
DECL|field|accountInfoCacheFactory
specifier|private
specifier|final
name|AccountInfoCacheFactory
operator|.
name|Factory
name|accountInfoCacheFactory
decl_stmt|;
DECL|field|accountControlFactory
specifier|private
specifier|final
name|AccountControl
operator|.
name|Factory
name|accountControlFactory
decl_stmt|;
DECL|field|queryBuilder
specifier|private
specifier|final
name|ChangeQueryBuilder
operator|.
name|Factory
name|queryBuilder
decl_stmt|;
DECL|field|queryRewriter
specifier|private
specifier|final
name|Provider
argument_list|<
name|ChangeQueryRewriter
argument_list|>
name|queryRewriter
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeListServiceImpl (final Provider<ReviewDb> schema, final Provider<CurrentUser> currentUser, final ChangeControl.Factory changeControlFactory, final AccountInfoCacheFactory.Factory accountInfoCacheFactory, final AccountControl.Factory accountControlFactory, final ChangeQueryBuilder.Factory queryBuilder, final Provider<ChangeQueryRewriter> queryRewriter)
name|ChangeListServiceImpl
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|schema
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
parameter_list|,
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
parameter_list|,
specifier|final
name|AccountInfoCacheFactory
operator|.
name|Factory
name|accountInfoCacheFactory
parameter_list|,
specifier|final
name|AccountControl
operator|.
name|Factory
name|accountControlFactory
parameter_list|,
specifier|final
name|ChangeQueryBuilder
operator|.
name|Factory
name|queryBuilder
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|ChangeQueryRewriter
argument_list|>
name|queryRewriter
parameter_list|)
block|{
name|super
argument_list|(
name|schema
argument_list|,
name|currentUser
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentUser
operator|=
name|currentUser
expr_stmt|;
name|this
operator|.
name|changeControlFactory
operator|=
name|changeControlFactory
expr_stmt|;
name|this
operator|.
name|accountInfoCacheFactory
operator|=
name|accountInfoCacheFactory
expr_stmt|;
name|this
operator|.
name|accountControlFactory
operator|=
name|accountControlFactory
expr_stmt|;
name|this
operator|.
name|queryBuilder
operator|=
name|queryBuilder
expr_stmt|;
name|this
operator|.
name|queryRewriter
operator|=
name|queryRewriter
expr_stmt|;
block|}
DECL|method|canRead (final Change c, final ReviewDb db)
specifier|private
name|boolean
name|canRead
parameter_list|(
specifier|final
name|Change
name|c
parameter_list|,
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
block|{
return|return
name|changeControlFactory
operator|.
name|controlFor
argument_list|(
name|c
argument_list|)
operator|.
name|isVisible
argument_list|(
name|db
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchChangeException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|allQueryPrev (final String query, final String pos, final int pageSize, final AsyncCallback<SingleListChangeInfo> callback)
specifier|public
name|void
name|allQueryPrev
parameter_list|(
specifier|final
name|String
name|query
parameter_list|,
specifier|final
name|String
name|pos
parameter_list|,
specifier|final
name|int
name|pageSize
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|SingleListChangeInfo
argument_list|>
name|callback
parameter_list|)
block|{
try|try
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|QueryPrev
argument_list|(
name|pageSize
argument_list|,
name|pos
argument_list|)
block|{
annotation|@
name|Override
name|ResultSet
argument_list|<
name|Change
argument_list|>
name|query
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|int
name|lim
parameter_list|,
name|String
name|key
parameter_list|)
throws|throws
name|OrmException
throws|,
name|InvalidQueryException
block|{
return|return
name|searchQuery
argument_list|(
name|db
argument_list|,
name|query
argument_list|,
name|lim
argument_list|,
name|key
argument_list|,
name|QUERY_PREV
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidQueryException
name|e
parameter_list|)
block|{
name|callback
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|allQueryNext (final String query, final String pos, final int pageSize, final AsyncCallback<SingleListChangeInfo> callback)
specifier|public
name|void
name|allQueryNext
parameter_list|(
specifier|final
name|String
name|query
parameter_list|,
specifier|final
name|String
name|pos
parameter_list|,
specifier|final
name|int
name|pageSize
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|SingleListChangeInfo
argument_list|>
name|callback
parameter_list|)
block|{
try|try
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|QueryNext
argument_list|(
name|pageSize
argument_list|,
name|pos
argument_list|)
block|{
annotation|@
name|Override
name|ResultSet
argument_list|<
name|Change
argument_list|>
name|query
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|int
name|lim
parameter_list|,
name|String
name|key
parameter_list|)
throws|throws
name|OrmException
throws|,
name|InvalidQueryException
block|{
return|return
name|searchQuery
argument_list|(
name|db
argument_list|,
name|query
argument_list|,
name|lim
argument_list|,
name|key
argument_list|,
name|QUERY_NEXT
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidQueryException
name|e
parameter_list|)
block|{
name|callback
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|searchQuery (final ReviewDb db, String query, final int limit, final String key, final Comparator<Change> cmp)
specifier|private
name|ResultSet
argument_list|<
name|Change
argument_list|>
name|searchQuery
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|,
name|String
name|query
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|,
specifier|final
name|String
name|key
parameter_list|,
specifier|final
name|Comparator
argument_list|<
name|Change
argument_list|>
name|cmp
parameter_list|)
throws|throws
name|OrmException
throws|,
name|InvalidQueryException
block|{
try|try
block|{
specifier|final
name|ChangeQueryBuilder
name|builder
init|=
name|queryBuilder
operator|.
name|create
argument_list|(
name|currentUser
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|visibleToMe
init|=
name|builder
operator|.
name|is_visible
argument_list|()
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|q
init|=
name|builder
operator|.
name|parse
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|q
operator|=
name|Predicate
operator|.
name|and
argument_list|(
name|q
argument_list|,
comment|//
name|cmp
operator|==
name|QUERY_PREV
comment|//
condition|?
name|builder
operator|.
name|sortkey_after
argument_list|(
name|key
argument_list|)
comment|//
else|:
name|builder
operator|.
name|sortkey_before
argument_list|(
name|key
argument_list|)
argument_list|,
comment|//
name|builder
operator|.
name|limit
argument_list|(
name|limit
argument_list|)
argument_list|,
comment|//
name|visibleToMe
comment|//
argument_list|)
expr_stmt|;
name|ChangeQueryRewriter
name|rewriter
init|=
name|queryRewriter
operator|.
name|get
argument_list|()
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|s
init|=
name|rewriter
operator|.
name|rewrite
argument_list|(
name|q
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|s
operator|instanceof
name|ChangeDataSource
operator|)
condition|)
block|{
name|s
operator|=
name|rewriter
operator|.
name|rewrite
argument_list|(
name|Predicate
operator|.
name|and
argument_list|(
name|builder
operator|.
name|status_open
argument_list|()
argument_list|,
name|q
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|s
operator|instanceof
name|ChangeDataSource
condition|)
block|{
name|ArrayList
argument_list|<
name|Change
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<
name|Change
argument_list|>
argument_list|()
decl_stmt|;
name|HashSet
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|want
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
name|ChangeData
name|d
range|:
operator|(
operator|(
name|ChangeDataSource
operator|)
name|s
operator|)
operator|.
name|read
argument_list|()
control|)
block|{
if|if
condition|(
name|d
operator|.
name|hasChange
argument_list|()
condition|)
block|{
comment|// Checking visibleToMe here should be unnecessary, the
comment|// query should have already performed it.  But we don't
comment|// want to trust the query rewriter that much yet.
comment|//
if|if
condition|(
name|visibleToMe
operator|.
name|match
argument_list|(
name|d
argument_list|)
condition|)
block|{
name|r
operator|.
name|add
argument_list|(
name|d
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|want
operator|.
name|add
argument_list|(
name|d
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Here we have to check canRead. Its impossible to
comment|// do that test without the change object, and it being
comment|// missing above means we have to compute it ourselves.
comment|//
if|if
condition|(
operator|!
name|want
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Change
name|c
range|:
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|want
argument_list|)
control|)
block|{
if|if
condition|(
name|canRead
argument_list|(
name|c
argument_list|,
name|db
argument_list|)
condition|)
block|{
name|r
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|r
argument_list|,
name|cmp
argument_list|)
expr_stmt|;
return|return
operator|new
name|ListResultSet
argument_list|<
name|Change
argument_list|>
argument_list|(
name|r
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|InvalidQueryException
argument_list|(
literal|"Not Supported"
argument_list|,
name|s
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|QueryParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InvalidQueryException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|query
argument_list|)
throw|;
block|}
block|}
DECL|method|forAccount (final Account.Id id, final AsyncCallback<AccountDashboardInfo> callback)
specifier|public
name|void
name|forAccount
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|id
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|AccountDashboardInfo
argument_list|>
name|callback
parameter_list|)
block|{
specifier|final
name|Account
operator|.
name|Id
name|me
init|=
name|getAccountId
argument_list|()
decl_stmt|;
specifier|final
name|Account
operator|.
name|Id
name|target
init|=
name|id
operator|!=
literal|null
condition|?
name|id
else|:
name|me
decl_stmt|;
if|if
condition|(
name|target
operator|==
literal|null
condition|)
block|{
name|callback
operator|.
name|onFailure
argument_list|(
operator|new
name|NoSuchEntityException
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|Action
argument_list|<
name|AccountDashboardInfo
argument_list|>
argument_list|()
block|{
specifier|public
name|AccountDashboardInfo
name|run
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
throws|,
name|Failure
block|{
specifier|final
name|AccountInfoCacheFactory
name|ac
init|=
name|accountInfoCacheFactory
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|final
name|Account
name|user
init|=
name|ac
operator|.
name|get
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
operator|||
operator|!
name|accountControlFactory
operator|.
name|get
argument_list|()
operator|.
name|canSee
argument_list|(
name|user
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
operator|new
name|NoSuchEntityException
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|stars
init|=
name|currentUser
operator|.
name|get
argument_list|()
operator|.
name|getStarredChanges
argument_list|()
decl_stmt|;
specifier|final
name|ChangeAccess
name|changes
init|=
name|db
operator|.
name|changes
argument_list|()
decl_stmt|;
specifier|final
name|AccountDashboardInfo
name|d
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|openReviews
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
specifier|final
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|closedReviews
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
specifier|final
name|PatchSetApproval
name|ca
range|:
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|openByUser
argument_list|(
name|id
argument_list|)
control|)
block|{
name|openReviews
operator|.
name|add
argument_list|(
name|ca
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|PatchSetApproval
name|ca
range|:
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|closedByUser
argument_list|(
name|id
argument_list|)
control|)
block|{
name|closedReviews
operator|.
name|add
argument_list|(
name|ca
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|d
operator|=
operator|new
name|AccountDashboardInfo
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|d
operator|.
name|setByOwner
argument_list|(
name|filter
argument_list|(
name|changes
operator|.
name|byOwnerOpen
argument_list|(
name|target
argument_list|)
argument_list|,
name|stars
argument_list|,
name|ac
argument_list|,
name|db
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|setClosed
argument_list|(
name|filter
argument_list|(
name|changes
operator|.
name|byOwnerClosed
argument_list|(
name|target
argument_list|)
argument_list|,
name|stars
argument_list|,
name|ac
argument_list|,
name|db
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|ChangeInfo
name|c
range|:
name|d
operator|.
name|getByOwner
argument_list|()
control|)
block|{
name|openReviews
operator|.
name|remove
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|d
operator|.
name|setForReview
argument_list|(
name|filter
argument_list|(
name|changes
operator|.
name|get
argument_list|(
name|openReviews
argument_list|)
argument_list|,
name|stars
argument_list|,
name|ac
argument_list|,
name|db
argument_list|)
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|d
operator|.
name|getForReview
argument_list|()
argument_list|,
name|ID_COMP
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|ChangeInfo
name|c
range|:
name|d
operator|.
name|getClosed
argument_list|()
control|)
block|{
name|closedReviews
operator|.
name|remove
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|closedReviews
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|d
operator|.
name|getClosed
argument_list|()
operator|.
name|addAll
argument_list|(
name|filter
argument_list|(
name|changes
operator|.
name|get
argument_list|(
name|closedReviews
argument_list|)
argument_list|,
name|stars
argument_list|,
name|ac
argument_list|,
name|db
argument_list|)
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|d
operator|.
name|getClosed
argument_list|()
argument_list|,
name|SORT_KEY_COMP
argument_list|)
expr_stmt|;
block|}
name|d
operator|.
name|setAccounts
argument_list|(
name|ac
operator|.
name|create
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|d
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|toggleStars (final ToggleStarRequest req, final AsyncCallback<VoidResult> callback)
specifier|public
name|void
name|toggleStars
parameter_list|(
specifier|final
name|ToggleStarRequest
name|req
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|callback
parameter_list|)
block|{
name|run
argument_list|(
name|callback
argument_list|,
operator|new
name|Action
argument_list|<
name|VoidResult
argument_list|>
argument_list|()
block|{
specifier|public
name|VoidResult
name|run
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|Account
operator|.
name|Id
name|me
init|=
name|getAccountId
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|existing
init|=
name|currentUser
operator|.
name|get
argument_list|()
operator|.
name|getStarredChanges
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|StarredChange
argument_list|>
name|add
init|=
operator|new
name|ArrayList
argument_list|<
name|StarredChange
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|StarredChange
operator|.
name|Key
argument_list|>
name|remove
init|=
operator|new
name|ArrayList
argument_list|<
name|StarredChange
operator|.
name|Key
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|req
operator|.
name|getAddSet
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Change
operator|.
name|Id
name|id
range|:
name|req
operator|.
name|getAddSet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|existing
operator|.
name|contains
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|add
operator|.
name|add
argument_list|(
operator|new
name|StarredChange
argument_list|(
operator|new
name|StarredChange
operator|.
name|Key
argument_list|(
name|me
argument_list|,
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|req
operator|.
name|getRemoveSet
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Change
operator|.
name|Id
name|id
range|:
name|req
operator|.
name|getRemoveSet
argument_list|()
control|)
block|{
name|remove
operator|.
name|add
argument_list|(
operator|new
name|StarredChange
operator|.
name|Key
argument_list|(
name|me
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|db
operator|.
name|starredChanges
argument_list|()
operator|.
name|insert
argument_list|(
name|add
argument_list|)
expr_stmt|;
name|db
operator|.
name|starredChanges
argument_list|()
operator|.
name|deleteKeys
argument_list|(
name|remove
argument_list|)
expr_stmt|;
return|return
name|VoidResult
operator|.
name|INSTANCE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|myStarredChangeIds (final AsyncCallback<Set<Change.Id>> callback)
specifier|public
name|void
name|myStarredChangeIds
parameter_list|(
specifier|final
name|AsyncCallback
argument_list|<
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
argument_list|>
name|callback
parameter_list|)
block|{
name|callback
operator|.
name|onSuccess
argument_list|(
name|currentUser
operator|.
name|get
argument_list|()
operator|.
name|getStarredChanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|safePageSize (final int pageSize)
specifier|private
name|int
name|safePageSize
parameter_list|(
specifier|final
name|int
name|pageSize
parameter_list|)
throws|throws
name|InvalidQueryException
block|{
name|int
name|maxLimit
init|=
name|currentUser
operator|.
name|get
argument_list|()
operator|.
name|getCapabilities
argument_list|()
operator|.
name|getRange
argument_list|(
name|GlobalCapability
operator|.
name|QUERY_LIMIT
argument_list|)
operator|.
name|getMax
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxLimit
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|InvalidQueryException
argument_list|(
literal|"Search Disabled"
argument_list|)
throw|;
block|}
return|return
literal|0
operator|<
name|pageSize
operator|&&
name|pageSize
operator|<=
name|maxLimit
condition|?
name|pageSize
else|:
name|maxLimit
return|;
block|}
DECL|method|filter (final ResultSet<Change> rs, final Set<Change.Id> starred, final AccountInfoCacheFactory accts, final ReviewDb db)
specifier|private
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|filter
parameter_list|(
specifier|final
name|ResultSet
argument_list|<
name|Change
argument_list|>
name|rs
parameter_list|,
specifier|final
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|starred
parameter_list|,
specifier|final
name|AccountInfoCacheFactory
name|accts
parameter_list|,
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|ArrayList
argument_list|<
name|ChangeInfo
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Change
name|c
range|:
name|rs
control|)
block|{
if|if
condition|(
name|canRead
argument_list|(
name|c
argument_list|,
name|db
argument_list|)
condition|)
block|{
specifier|final
name|ChangeInfo
name|ci
init|=
operator|new
name|ChangeInfo
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|accts
operator|.
name|want
argument_list|(
name|ci
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|ci
operator|.
name|setStarred
argument_list|(
name|starred
operator|.
name|contains
argument_list|(
name|ci
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|add
argument_list|(
name|ci
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|r
return|;
block|}
DECL|class|QueryNext
specifier|private
specifier|abstract
class|class
name|QueryNext
implements|implements
name|Action
argument_list|<
name|SingleListChangeInfo
argument_list|>
block|{
DECL|field|pos
specifier|protected
specifier|final
name|String
name|pos
decl_stmt|;
DECL|field|limit
specifier|protected
specifier|final
name|int
name|limit
decl_stmt|;
DECL|field|slim
specifier|protected
specifier|final
name|int
name|slim
decl_stmt|;
DECL|method|QueryNext (final int pageSize, final String pos)
name|QueryNext
parameter_list|(
specifier|final
name|int
name|pageSize
parameter_list|,
specifier|final
name|String
name|pos
parameter_list|)
throws|throws
name|InvalidQueryException
block|{
name|this
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|safePageSize
argument_list|(
name|pageSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|slim
operator|=
name|limit
operator|+
literal|1
expr_stmt|;
block|}
DECL|method|run (final ReviewDb db)
specifier|public
name|SingleListChangeInfo
name|run
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
throws|,
name|InvalidQueryException
block|{
specifier|final
name|AccountInfoCacheFactory
name|ac
init|=
name|accountInfoCacheFactory
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|final
name|SingleListChangeInfo
name|d
init|=
operator|new
name|SingleListChangeInfo
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|starred
init|=
name|currentUser
operator|.
name|get
argument_list|()
operator|.
name|getStarredChanges
argument_list|()
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ChangeInfo
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|ResultSet
argument_list|<
name|Change
argument_list|>
name|rs
init|=
name|query
argument_list|(
name|db
argument_list|,
name|slim
argument_list|,
name|pos
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Change
name|c
range|:
name|rs
control|)
block|{
if|if
condition|(
operator|!
name|canRead
argument_list|(
name|c
argument_list|,
name|db
argument_list|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|ChangeInfo
name|ci
init|=
operator|new
name|ChangeInfo
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|ac
operator|.
name|want
argument_list|(
name|ci
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|ci
operator|.
name|setStarred
argument_list|(
name|starred
operator|.
name|contains
argument_list|(
name|ci
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|ci
argument_list|)
expr_stmt|;
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|==
name|slim
condition|)
block|{
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
specifier|final
name|boolean
name|atEnd
init|=
name|finish
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|d
operator|.
name|setChanges
argument_list|(
name|list
argument_list|,
name|atEnd
argument_list|)
expr_stmt|;
name|d
operator|.
name|setAccounts
argument_list|(
name|ac
operator|.
name|create
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|d
return|;
block|}
DECL|method|finish (final ArrayList<ChangeInfo> list)
name|boolean
name|finish
parameter_list|(
specifier|final
name|ArrayList
argument_list|<
name|ChangeInfo
argument_list|>
name|list
parameter_list|)
block|{
specifier|final
name|boolean
name|atEnd
init|=
name|list
operator|.
name|size
argument_list|()
operator|<=
name|limit
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|==
name|slim
condition|)
block|{
name|list
operator|.
name|remove
argument_list|(
name|limit
argument_list|)
expr_stmt|;
block|}
return|return
name|atEnd
return|;
block|}
DECL|method|query (final ReviewDb db, final int slim, String sortKey)
specifier|abstract
name|ResultSet
argument_list|<
name|Change
argument_list|>
name|query
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|int
name|slim
parameter_list|,
name|String
name|sortKey
parameter_list|)
throws|throws
name|OrmException
throws|,
name|InvalidQueryException
function_decl|;
block|}
DECL|class|QueryPrev
specifier|private
specifier|abstract
class|class
name|QueryPrev
extends|extends
name|QueryNext
block|{
DECL|method|QueryPrev (int pageSize, String pos)
name|QueryPrev
parameter_list|(
name|int
name|pageSize
parameter_list|,
name|String
name|pos
parameter_list|)
throws|throws
name|InvalidQueryException
block|{
name|super
argument_list|(
name|pageSize
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish (final ArrayList<ChangeInfo> list)
name|boolean
name|finish
parameter_list|(
specifier|final
name|ArrayList
argument_list|<
name|ChangeInfo
argument_list|>
name|list
parameter_list|)
block|{
specifier|final
name|boolean
name|atEnd
init|=
name|super
operator|.
name|finish
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|reverse
argument_list|(
name|list
argument_list|)
expr_stmt|;
return|return
name|atEnd
return|;
block|}
block|}
block|}
end_class

end_unit

