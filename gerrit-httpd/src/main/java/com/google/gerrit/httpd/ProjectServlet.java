begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
name|reviewdb
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
name|AnonymousUser
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
name|IdentifiedUser
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
name|git
operator|.
name|ReceiveCommits
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
name|TransferConfig
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
name|VisibleRefFilter
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
name|NoSuchProjectException
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
name|ProjectControl
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
name|AbstractModule
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
name|eclipse
operator|.
name|jgit
operator|.
name|errors
operator|.
name|RepositoryNotFoundException
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
name|http
operator|.
name|server
operator|.
name|GitServlet
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
name|http
operator|.
name|server
operator|.
name|resolver
operator|.
name|AsIsFileService
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

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|storage
operator|.
name|pack
operator|.
name|PackConfig
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
name|transport
operator|.
name|ReceivePack
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
name|transport
operator|.
name|UploadPack
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
name|transport
operator|.
name|resolver
operator|.
name|ReceivePackFactory
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
name|transport
operator|.
name|resolver
operator|.
name|RepositoryResolver
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
name|transport
operator|.
name|resolver
operator|.
name|ServiceNotAuthorizedException
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
name|transport
operator|.
name|resolver
operator|.
name|ServiceNotEnabledException
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
name|transport
operator|.
name|resolver
operator|.
name|UploadPackFactory
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletConfig
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
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

begin_comment
comment|/** Serves Git repositories over HTTP. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|ProjectServlet
specifier|public
class|class
name|ProjectServlet
extends|extends
name|GitServlet
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
name|ProjectServlet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ATT_CONTROL
specifier|private
specifier|static
specifier|final
name|String
name|ATT_CONTROL
init|=
name|ProjectControl
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|class|Module
specifier|static
class|class
name|Module
extends|extends
name|AbstractModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|Resolver
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|Upload
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|Receive
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getProjectControl (HttpServletRequest req)
specifier|static
name|ProjectControl
name|getProjectControl
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
throws|throws
name|ServiceNotEnabledException
block|{
name|ProjectControl
name|pc
init|=
operator|(
name|ProjectControl
operator|)
name|req
operator|.
name|getAttribute
argument_list|(
name|ATT_CONTROL
argument_list|)
decl_stmt|;
if|if
condition|(
name|pc
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"No "
operator|+
name|ATT_CONTROL
operator|+
literal|" in request"
argument_list|,
operator|new
name|Exception
argument_list|(
literal|"here"
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServiceNotEnabledException
argument_list|()
throw|;
block|}
return|return
name|pc
return|;
block|}
DECL|field|urlProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|String
argument_list|>
name|urlProvider
decl_stmt|;
annotation|@
name|Inject
DECL|method|ProjectServlet (final Resolver resolver, final Upload upload, final Receive receive, @CanonicalWebUrl @Nullable Provider<String> urlProvider)
name|ProjectServlet
parameter_list|(
specifier|final
name|Resolver
name|resolver
parameter_list|,
specifier|final
name|Upload
name|upload
parameter_list|,
specifier|final
name|Receive
name|receive
parameter_list|,
annotation|@
name|CanonicalWebUrl
annotation|@
name|Nullable
name|Provider
argument_list|<
name|String
argument_list|>
name|urlProvider
parameter_list|)
block|{
name|this
operator|.
name|urlProvider
operator|=
name|urlProvider
expr_stmt|;
name|setRepositoryResolver
argument_list|(
name|resolver
argument_list|)
expr_stmt|;
name|setAsIsFileService
argument_list|(
name|AsIsFileService
operator|.
name|DISABLED
argument_list|)
expr_stmt|;
name|setUploadPackFactory
argument_list|(
name|upload
argument_list|)
expr_stmt|;
name|setReceivePackFactory
argument_list|(
name|receive
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (ServletConfig config)
specifier|public
name|void
name|init
parameter_list|(
name|ServletConfig
name|config
parameter_list|)
throws|throws
name|ServletException
block|{
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|serveRegex
argument_list|(
literal|"^/(.*?)/?$"
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|HttpServlet
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|ProjectControl
name|pc
decl_stmt|;
try|try
block|{
name|pc
operator|=
name|getProjectControl
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceNotEnabledException
name|e
parameter_list|)
block|{
name|rsp
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
return|return;
block|}
name|Project
operator|.
name|NameKey
name|dst
init|=
name|pc
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
decl_stmt|;
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
name|urlProvider
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|'#'
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|PageLinks
operator|.
name|toChangeQuery
argument_list|(
name|PageLinks
operator|.
name|projectQuery
argument_list|(
name|dst
argument_list|,
name|Change
operator|.
name|Status
operator|.
name|NEW
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|sendRedirect
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|class|Resolver
specifier|static
class|class
name|Resolver
implements|implements
name|RepositoryResolver
argument_list|<
name|HttpServletRequest
argument_list|>
block|{
DECL|field|manager
specifier|private
specifier|final
name|GitRepositoryManager
name|manager
decl_stmt|;
DECL|field|projectControlFactory
specifier|private
specifier|final
name|ProjectControl
operator|.
name|Factory
name|projectControlFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|Resolver (GitRepositoryManager manager, ProjectControl.Factory projectControlFactory)
name|Resolver
parameter_list|(
name|GitRepositoryManager
name|manager
parameter_list|,
name|ProjectControl
operator|.
name|Factory
name|projectControlFactory
parameter_list|)
block|{
name|this
operator|.
name|manager
operator|=
name|manager
expr_stmt|;
name|this
operator|.
name|projectControlFactory
operator|=
name|projectControlFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|open (HttpServletRequest req, String projectName)
specifier|public
name|Repository
name|open
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|String
name|projectName
parameter_list|)
throws|throws
name|RepositoryNotFoundException
throws|,
name|ServiceNotAuthorizedException
throws|,
name|ServiceNotEnabledException
block|{
if|if
condition|(
name|projectName
operator|.
name|endsWith
argument_list|(
literal|".git"
argument_list|)
condition|)
block|{
comment|// Be nice and drop the trailing ".git" suffix, which we never keep
comment|// in our database, but clients might mistakenly provide anyway.
comment|//
name|projectName
operator|=
name|projectName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|projectName
operator|.
name|length
argument_list|()
operator|-
literal|4
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|projectName
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
comment|// Be nice and drop the leading "/" if supplied by an absolute path.
comment|// We don't have a file system hierarchy, just a flat namespace in
comment|// the database's Project entities. We never encode these with a
comment|// leading '/' but users might accidentally include them in Git URLs.
comment|//
name|projectName
operator|=
name|projectName
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ProjectControl
name|pc
decl_stmt|;
try|try
block|{
specifier|final
name|Project
operator|.
name|NameKey
name|nameKey
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|projectName
argument_list|)
decl_stmt|;
name|pc
operator|=
name|projectControlFactory
operator|.
name|controlFor
argument_list|(
name|nameKey
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchProjectException
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryNotFoundException
argument_list|(
name|projectName
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|pc
operator|.
name|isVisible
argument_list|()
condition|)
block|{
if|if
condition|(
name|pc
operator|.
name|getCurrentUser
argument_list|()
operator|instanceof
name|AnonymousUser
condition|)
block|{
throw|throw
operator|new
name|ServiceNotAuthorizedException
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|ServiceNotEnabledException
argument_list|()
throw|;
block|}
block|}
name|req
operator|.
name|setAttribute
argument_list|(
name|ATT_CONTROL
argument_list|,
name|pc
argument_list|)
expr_stmt|;
return|return
name|manager
operator|.
name|openRepository
argument_list|(
name|pc
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|Upload
specifier|static
class|class
name|Upload
implements|implements
name|UploadPackFactory
argument_list|<
name|HttpServletRequest
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
DECL|field|packConfig
specifier|private
specifier|final
name|PackConfig
name|packConfig
decl_stmt|;
annotation|@
name|Inject
DECL|method|Upload (final Provider<ReviewDb> db, final TransferConfig tc)
name|Upload
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
specifier|final
name|TransferConfig
name|tc
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
name|packConfig
operator|=
name|tc
operator|.
name|getPackConfig
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create (HttpServletRequest req, Repository repo)
specifier|public
name|UploadPack
name|create
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|Repository
name|repo
parameter_list|)
throws|throws
name|ServiceNotEnabledException
throws|,
name|ServiceNotAuthorizedException
block|{
name|ProjectControl
name|pc
init|=
name|getProjectControl
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|pc
operator|.
name|canRunUploadPack
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ServiceNotAuthorizedException
argument_list|()
throw|;
block|}
comment|// The Resolver above already checked READ access for us.
comment|//
name|UploadPack
name|up
init|=
operator|new
name|UploadPack
argument_list|(
name|repo
argument_list|)
decl_stmt|;
name|up
operator|.
name|setPackConfig
argument_list|(
name|packConfig
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|pc
operator|.
name|allRefsAreVisible
argument_list|()
condition|)
block|{
name|up
operator|.
name|setRefFilter
argument_list|(
operator|new
name|VisibleRefFilter
argument_list|(
name|repo
argument_list|,
name|pc
argument_list|,
name|db
operator|.
name|get
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|up
return|;
block|}
block|}
DECL|class|Receive
specifier|static
class|class
name|Receive
implements|implements
name|ReceivePackFactory
argument_list|<
name|HttpServletRequest
argument_list|>
block|{
DECL|field|factory
specifier|private
specifier|final
name|ReceiveCommits
operator|.
name|Factory
name|factory
decl_stmt|;
annotation|@
name|Inject
DECL|method|Receive (final ReceiveCommits.Factory factory)
name|Receive
parameter_list|(
specifier|final
name|ReceiveCommits
operator|.
name|Factory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create (HttpServletRequest req, Repository db)
specifier|public
name|ReceivePack
name|create
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|Repository
name|db
parameter_list|)
throws|throws
name|ServiceNotEnabledException
throws|,
name|ServiceNotAuthorizedException
block|{
specifier|final
name|ProjectControl
name|pc
init|=
name|getProjectControl
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|pc
operator|.
name|canRunReceivePack
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ServiceNotAuthorizedException
argument_list|()
throw|;
block|}
if|if
condition|(
name|pc
operator|.
name|getCurrentUser
argument_list|()
operator|instanceof
name|IdentifiedUser
condition|)
block|{
specifier|final
name|IdentifiedUser
name|user
init|=
operator|(
name|IdentifiedUser
operator|)
name|pc
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
specifier|final
name|ReceiveCommits
name|rc
init|=
name|factory
operator|.
name|create
argument_list|(
name|pc
argument_list|,
name|db
argument_list|)
decl_stmt|;
specifier|final
name|ReceiveCommits
operator|.
name|Capable
name|s
init|=
name|rc
operator|.
name|canUpload
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|!=
name|ReceiveCommits
operator|.
name|Capable
operator|.
name|OK
condition|)
block|{
comment|// TODO We should alert the user to this message on the HTTP
comment|// response channel, assuming Git will even report it to them.
comment|//
specifier|final
name|String
name|who
init|=
name|user
operator|.
name|getUserName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|why
init|=
name|s
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"Rejected push from "
operator|+
name|who
operator|+
literal|": "
operator|+
name|why
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServiceNotEnabledException
argument_list|()
throw|;
block|}
name|rc
operator|.
name|getReceivePack
argument_list|()
operator|.
name|setRefLogIdent
argument_list|(
name|user
operator|.
name|newRefLogIdent
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rc
operator|.
name|getReceivePack
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ServiceNotAuthorizedException
argument_list|()
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

