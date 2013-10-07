begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2011 Google Inc. All Rights Reserved.
end_comment

begin_package
DECL|package|com.google.gerrit.httpd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
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
name|gerrit
operator|.
name|common
operator|.
name|PageLinks
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
name|HashSet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServlet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|DirectChangeByCommit
class|class
name|DirectChangeByCommit
extends|extends
name|HttpServlet
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
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
name|DirectChangeByCommit
operator|.
name|class
argument_list|)
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
DECL|field|currentUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
decl_stmt|;
annotation|@
name|Inject
DECL|method|DirectChangeByCommit (ChangeQueryBuilder.Factory queryBuilder, Provider<ChangeQueryRewriter> queryRewriter, Provider<CurrentUser> currentUser)
name|DirectChangeByCommit
parameter_list|(
name|ChangeQueryBuilder
operator|.
name|Factory
name|queryBuilder
parameter_list|,
name|Provider
argument_list|<
name|ChangeQueryRewriter
argument_list|>
name|queryRewriter
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
parameter_list|)
block|{
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
name|this
operator|.
name|currentUser
operator|=
name|currentUser
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|doGet (final HttpServletRequest req, final HttpServletResponse rsp)
specifier|protected
name|void
name|doGet
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|query
init|=
name|CharMatcher
operator|.
name|is
argument_list|(
literal|'/'
argument_list|)
operator|.
name|trimTrailingFrom
argument_list|(
name|req
operator|.
name|getPathInfo
argument_list|()
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|ids
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
try|try
block|{
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
name|builder
operator|.
name|sortkey_before
argument_list|(
literal|"z"
argument_list|)
argument_list|,
name|builder
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
argument_list|,
name|visibleToMe
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
name|ids
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
block|}
catch|catch
parameter_list|(
name|QueryParseException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Received invalid query by URL: /r/"
operator|+
name|query
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot process query by URL: /r/"
operator|+
name|query
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|String
name|token
decl_stmt|;
if|if
condition|(
name|ids
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// If exactly one change matches, link to that change.
comment|// TODO Link to a specific patch set, if one matched.
name|token
operator|=
name|PageLinks
operator|.
name|toChange
argument_list|(
name|ids
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Otherwise, link to the query page.
name|token
operator|=
name|PageLinks
operator|.
name|toChangeQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
name|UrlModule
operator|.
name|toGerrit
argument_list|(
name|token
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

