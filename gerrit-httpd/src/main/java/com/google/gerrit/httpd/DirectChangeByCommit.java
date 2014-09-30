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
name|extensions
operator|.
name|api
operator|.
name|changes
operator|.
name|Changes
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
name|List
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
DECL|field|changes
specifier|private
specifier|final
name|Changes
name|changes
decl_stmt|;
annotation|@
name|Inject
DECL|method|DirectChangeByCommit (Changes changes)
name|DirectChangeByCommit
parameter_list|(
name|Changes
name|changes
parameter_list|)
block|{
name|this
operator|.
name|changes
operator|=
name|changes
expr_stmt|;
block|}
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
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|results
decl_stmt|;
try|try
block|{
name|results
operator|=
name|changes
operator|.
name|query
argument_list|(
name|query
argument_list|)
operator|.
name|withLimit
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RestApiException
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
name|results
operator|=
name|ImmutableList
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
name|String
name|token
decl_stmt|;
if|if
condition|(
name|results
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
operator|new
name|Change
operator|.
name|Id
argument_list|(
name|results
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|_number
argument_list|)
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

