begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.httpd.raw
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|raw
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
name|Optional
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
name|edit
operator|.
name|ChangeEdit
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
name|edit
operator|.
name|ChangeEditUtil
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
name|io
operator|.
name|IOException
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
comment|/**  * Exports a single version of a patch as a normal file download.  *<p>  * This can be relatively unsafe with Microsoft Internet Explorer 6.0 as the  * browser will (rather incorrectly) treat an HTML or JavaScript file its  * supposed to download as though it was served by this site, and will execute  * it with the site's own protection domain. This opens a massive security hole  * so we package the content into a zip file.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
annotation|@
name|Singleton
DECL|class|CatServlet
specifier|public
class|class
name|CatServlet
extends|extends
name|HttpServlet
block|{
DECL|field|requestDb
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|requestDb
decl_stmt|;
DECL|field|userProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
decl_stmt|;
DECL|field|changeControl
specifier|private
specifier|final
name|ChangeControl
operator|.
name|GenericFactory
name|changeControl
decl_stmt|;
DECL|field|changeEditUtil
specifier|private
specifier|final
name|ChangeEditUtil
name|changeEditUtil
decl_stmt|;
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
annotation|@
name|Inject
DECL|method|CatServlet (Provider<ReviewDb> sf, ChangeControl.GenericFactory ccf, Provider<CurrentUser> usrprv, ChangeEditUtil ceu, PatchSetUtil psu)
name|CatServlet
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|sf
parameter_list|,
name|ChangeControl
operator|.
name|GenericFactory
name|ccf
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|usrprv
parameter_list|,
name|ChangeEditUtil
name|ceu
parameter_list|,
name|PatchSetUtil
name|psu
parameter_list|)
block|{
name|requestDb
operator|=
name|sf
expr_stmt|;
name|changeControl
operator|=
name|ccf
expr_stmt|;
name|userProvider
operator|=
name|usrprv
expr_stmt|;
name|changeEditUtil
operator|=
name|ceu
expr_stmt|;
name|psUtil
operator|=
name|psu
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
name|keyStr
init|=
name|req
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
comment|// We shouldn't have to do this extra decode pass, but somehow we
comment|// are now receiving our "^1" suffix as "%5E1", which confuses us
comment|// downstream. Other times we get our embedded "," as "%2C", which
comment|// is equally bad. And yet when these happen a "%2F" is left as-is,
comment|// rather than escaped as "%252F", which makes me feel really really
comment|// uncomfortable with a blind decode right here.
comment|//
name|keyStr
operator|=
name|Url
operator|.
name|decode
argument_list|(
name|keyStr
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|keyStr
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
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
name|keyStr
operator|=
name|keyStr
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|Patch
operator|.
name|Key
name|patchKey
decl_stmt|;
specifier|final
name|int
name|side
decl_stmt|;
block|{
specifier|final
name|int
name|c
init|=
name|keyStr
operator|.
name|lastIndexOf
argument_list|(
literal|'^'
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|0
condition|)
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
if|if
condition|(
name|c
operator|<
literal|0
condition|)
block|{
name|side
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|side
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|keyStr
operator|.
name|substring
argument_list|(
name|c
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|keyStr
operator|=
name|keyStr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
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
block|}
try|try
block|{
name|patchKey
operator|=
name|Patch
operator|.
name|Key
operator|.
name|parse
argument_list|(
name|keyStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
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
block|}
specifier|final
name|Change
operator|.
name|Id
name|changeId
init|=
name|patchKey
operator|.
name|getParentKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
name|String
name|revision
decl_stmt|;
try|try
block|{
specifier|final
name|ReviewDb
name|db
init|=
name|requestDb
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|ChangeControl
name|control
init|=
name|changeControl
operator|.
name|validateFor
argument_list|(
name|db
argument_list|,
name|changeId
argument_list|,
name|userProvider
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|patchKey
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// change edit
try|try
block|{
name|Optional
argument_list|<
name|ChangeEdit
argument_list|>
name|edit
init|=
name|changeEditUtil
operator|.
name|byChange
argument_list|(
name|control
operator|.
name|getChange
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|edit
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|revision
operator|=
name|edit
operator|.
name|get
argument_list|()
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
else|else
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
block|}
catch|catch
parameter_list|(
name|AuthException
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
block|}
else|else
block|{
name|PatchSet
name|patchSet
init|=
name|psUtil
operator|.
name|get
argument_list|(
name|db
argument_list|,
name|control
operator|.
name|getNotes
argument_list|()
argument_list|,
name|patchKey
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|patchSet
operator|==
literal|null
condition|)
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
name|revision
operator|=
name|patchSet
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchChangeException
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
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|getServletContext
argument_list|()
operator|.
name|log
argument_list|(
literal|"Cannot query database"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|path
init|=
name|patchKey
operator|.
name|getFileName
argument_list|()
decl_stmt|;
name|String
name|restUrl
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s/changes/%d/revisions/%s/files/%s/download?parent=%d"
argument_list|,
name|req
operator|.
name|getContextPath
argument_list|()
argument_list|,
name|changeId
operator|.
name|get
argument_list|()
argument_list|,
name|revision
argument_list|,
name|Url
operator|.
name|encode
argument_list|(
name|path
argument_list|)
argument_list|,
name|side
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|sendRedirect
argument_list|(
name|restUrl
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

