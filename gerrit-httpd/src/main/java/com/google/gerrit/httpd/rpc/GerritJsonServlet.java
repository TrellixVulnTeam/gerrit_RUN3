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
name|auth
operator|.
name|SignInRequired
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
name|NotSignedInException
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
name|httpd
operator|.
name|WebSession
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|GsonBuilder
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
name|RemoteJsonService
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
name|server
operator|.
name|ActiveCall
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
name|server
operator|.
name|JsonServlet
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
comment|/**  * Base JSON servlet to ensure the current user is not forged.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|GerritJsonServlet
specifier|final
class|class
name|GerritJsonServlet
extends|extends
name|JsonServlet
argument_list|<
name|GerritJsonServlet
operator|.
name|GerritCall
argument_list|>
block|{
DECL|field|session
specifier|private
specifier|final
name|Provider
argument_list|<
name|WebSession
argument_list|>
name|session
decl_stmt|;
DECL|field|service
specifier|private
specifier|final
name|RemoteJsonService
name|service
decl_stmt|;
annotation|@
name|Inject
DECL|method|GerritJsonServlet (final Provider<WebSession> w, final RemoteJsonService s)
name|GerritJsonServlet
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|WebSession
argument_list|>
name|w
parameter_list|,
specifier|final
name|RemoteJsonService
name|s
parameter_list|)
block|{
name|session
operator|=
name|w
expr_stmt|;
name|service
operator|=
name|s
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createActiveCall (final HttpServletRequest req, final HttpServletResponse rsp)
specifier|protected
name|GerritCall
name|createActiveCall
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|)
block|{
return|return
operator|new
name|GerritCall
argument_list|(
name|session
operator|.
name|get
argument_list|()
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createGsonBuilder ()
specifier|protected
name|GsonBuilder
name|createGsonBuilder
parameter_list|()
block|{
specifier|final
name|GsonBuilder
name|g
init|=
name|super
operator|.
name|createGsonBuilder
argument_list|()
decl_stmt|;
name|g
operator|.
name|registerTypeAdapter
argument_list|(
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|diff
operator|.
name|Edit
operator|.
name|class
argument_list|,
operator|new
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|diff
operator|.
name|EditDeserializer
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|g
return|;
block|}
annotation|@
name|Override
DECL|method|preInvoke (final GerritCall call)
specifier|protected
name|void
name|preInvoke
parameter_list|(
specifier|final
name|GerritCall
name|call
parameter_list|)
block|{
name|super
operator|.
name|preInvoke
argument_list|(
name|call
argument_list|)
expr_stmt|;
if|if
condition|(
name|call
operator|.
name|isComplete
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|call
operator|.
name|getMethod
argument_list|()
operator|.
name|getAnnotation
argument_list|(
name|SignInRequired
operator|.
name|class
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// If SignInRequired is set on this method we must have both a
comment|// valid XSRF token *and* have the user signed in. Doing these
comment|// checks also validates that they agree on the user identity.
comment|//
if|if
condition|(
operator|!
name|call
operator|.
name|requireXsrfValid
argument_list|()
operator|||
operator|!
name|session
operator|.
name|get
argument_list|()
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
name|call
operator|.
name|onFailure
argument_list|(
operator|new
name|NotSignedInException
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|createServiceHandle ()
specifier|protected
name|Object
name|createServiceHandle
parameter_list|()
block|{
return|return
name|service
return|;
block|}
DECL|class|GerritCall
specifier|static
class|class
name|GerritCall
extends|extends
name|ActiveCall
block|{
DECL|field|session
specifier|private
specifier|final
name|WebSession
name|session
decl_stmt|;
DECL|method|GerritCall (final WebSession session, final HttpServletRequest i, final HttpServletResponse o)
name|GerritCall
parameter_list|(
specifier|final
name|WebSession
name|session
parameter_list|,
specifier|final
name|HttpServletRequest
name|i
parameter_list|,
specifier|final
name|HttpServletResponse
name|o
parameter_list|)
block|{
name|super
argument_list|(
name|i
argument_list|,
name|o
argument_list|)
expr_stmt|;
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onFailure (final Throwable error)
specifier|public
name|void
name|onFailure
parameter_list|(
specifier|final
name|Throwable
name|error
parameter_list|)
block|{
if|if
condition|(
name|error
operator|instanceof
name|IllegalArgumentException
operator|||
name|error
operator|instanceof
name|IllegalStateException
condition|)
block|{
name|super
operator|.
name|onFailure
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|error
operator|instanceof
name|OrmException
operator|||
name|error
operator|instanceof
name|RuntimeException
condition|)
block|{
name|onInternalFailure
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|onFailure
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|xsrfValidate ()
specifier|public
name|boolean
name|xsrfValidate
parameter_list|()
block|{
specifier|final
name|String
name|keyIn
init|=
name|getXsrfKeyIn
argument_list|()
decl_stmt|;
if|if
condition|(
name|keyIn
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|keyIn
argument_list|)
condition|)
block|{
comment|// Anonymous requests don't need XSRF protection, they shouldn't
comment|// be able to cause critical state changes.
comment|//
return|return
operator|!
name|session
operator|.
name|isSignedIn
argument_list|()
return|;
block|}
else|else
block|{
comment|// The session must exist, and must be using this token.
comment|//
return|return
name|session
operator|.
name|isSignedIn
argument_list|()
operator|&&
name|session
operator|.
name|isTokenValid
argument_list|(
name|keyIn
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

