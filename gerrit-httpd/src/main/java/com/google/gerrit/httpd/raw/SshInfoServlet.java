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
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|ssh
operator|.
name|SshInfo
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtexpui
operator|.
name|server
operator|.
name|CacheHeaders
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
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|HostKey
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
name|io
operator|.
name|PrintWriter
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

begin_comment
comment|/**  * Servlet hosting an SSH daemon on another port. During a standard HTTP GET request the servlet  * returns the hostname and port number back to the client in the form<code>${host} ${port}</code>.  *  *<p>Use a Git URL such as<code>ssh://${email}@${host}:${port}/${path}</code>, e.g. {@code  * ssh://sop@google.com@gerrit.com:8010/tools/gerrit.git} to access the SSH daemon itself.  *  *<p>Versions of Git before 1.5.3 may require setting the username and port properties in the  * user's {@code ~/.ssh/config} file, and using a host alias through a URL such as {@code  * gerrit-alias:/tools/gerrit.git}:  *  *<pre>{@code  * Host gerrit-alias  *  User sop@google.com  *  Hostname gerrit.com  *  Port 8010  * }</pre>  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
annotation|@
name|Singleton
DECL|class|SshInfoServlet
specifier|public
class|class
name|SshInfoServlet
extends|extends
name|HttpServlet
block|{
DECL|field|sshd
specifier|private
specifier|final
name|SshInfo
name|sshd
decl_stmt|;
annotation|@
name|Inject
DECL|method|SshInfoServlet (SshInfo daemon)
name|SshInfoServlet
parameter_list|(
name|SshInfo
name|daemon
parameter_list|)
block|{
name|sshd
operator|=
name|daemon
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest req, HttpServletResponse rsp)
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
specifier|final
name|List
argument_list|<
name|HostKey
argument_list|>
name|hostKeys
init|=
name|sshd
operator|.
name|getHostKeys
argument_list|()
decl_stmt|;
specifier|final
name|String
name|out
decl_stmt|;
if|if
condition|(
operator|!
name|hostKeys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|host
init|=
name|hostKeys
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|String
name|port
init|=
literal|"22"
decl_stmt|;
if|if
condition|(
name|host
operator|.
name|contains
argument_list|(
literal|":"
argument_list|)
condition|)
block|{
specifier|final
name|int
name|p
init|=
name|host
operator|.
name|lastIndexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|port
operator|=
name|host
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
name|host
operator|=
name|host
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|host
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|host
operator|=
name|req
operator|.
name|getServerName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|host
operator|.
name|startsWith
argument_list|(
literal|"["
argument_list|)
operator|&&
name|host
operator|.
name|endsWith
argument_list|(
literal|"]"
argument_list|)
condition|)
block|{
name|host
operator|=
name|host
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|host
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|out
operator|=
name|host
operator|+
literal|" "
operator|+
name|port
expr_stmt|;
block|}
else|else
block|{
name|out
operator|=
literal|"NOT_AVAILABLE"
expr_stmt|;
block|}
name|CacheHeaders
operator|.
name|setNotCacheable
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setCharacterEncoding
argument_list|(
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
try|try
init|(
name|PrintWriter
name|w
init|=
name|rsp
operator|.
name|getWriter
argument_list|()
init|)
block|{
name|w
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

