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
name|Objects
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
name|extensions
operator|.
name|annotations
operator|.
name|RequiresCapability
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
name|account
operator|.
name|CapabilityControl
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
name|util
operator|.
name|cli
operator|.
name|CmdLineParser
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
name|common
operator|.
name|JsonConstants
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
name|RPCServletUtils
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
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|CmdLineException
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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

begin_class
DECL|class|RestApiServlet
specifier|public
specifier|abstract
class|class
name|RestApiServlet
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
name|RestApiServlet
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** MIME type used for a JSON response body. */
DECL|field|JSON_TYPE
specifier|protected
specifier|static
specifier|final
name|String
name|JSON_TYPE
init|=
name|JsonConstants
operator|.
name|JSON_TYPE
decl_stmt|;
comment|/**    * Garbage prefix inserted before JSON output to prevent XSSI.    *<p>    * This prefix is ")]}'\n" and is designed to prevent a web browser from    * executing the response body if the resource URI were to be referenced using    * a&lt;script src="...&gt; HTML tag from another web site. Clients using the    * HTTP interface will need to always strip the first line of response data to    * remove this magic header.    */
DECL|field|JSON_MAGIC
specifier|protected
specifier|static
specifier|final
name|byte
index|[]
name|JSON_MAGIC
decl_stmt|;
static|static
block|{
try|try
block|{
name|JSON_MAGIC
operator|=
literal|")]}'\n"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"UTF-8 not supported"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
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
DECL|method|RestApiServlet (final Provider<CurrentUser> currentUser)
specifier|protected
name|RestApiServlet
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
parameter_list|)
block|{
name|this
operator|.
name|currentUser
operator|=
name|currentUser
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|service (HttpServletRequest req, HttpServletResponse res)
specifier|protected
name|void
name|service
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|noCache
argument_list|(
name|res
argument_list|)
expr_stmt|;
try|try
block|{
name|checkRequiresCapability
argument_list|()
expr_stmt|;
name|super
operator|.
name|service
argument_list|(
name|req
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RequireCapabilityException
name|err
parameter_list|)
block|{
name|res
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|)
expr_stmt|;
name|noCache
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|sendText
argument_list|(
name|req
argument_list|,
name|res
argument_list|,
name|err
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Error
name|err
parameter_list|)
block|{
name|handleError
argument_list|(
name|err
argument_list|,
name|req
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|err
parameter_list|)
block|{
name|handleError
argument_list|(
name|err
argument_list|,
name|req
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkRequiresCapability ()
specifier|private
name|void
name|checkRequiresCapability
parameter_list|()
throws|throws
name|RequireCapabilityException
block|{
name|RequiresCapability
name|rc
init|=
name|getClass
argument_list|()
operator|.
name|getAnnotation
argument_list|(
name|RequiresCapability
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|!=
literal|null
condition|)
block|{
name|CurrentUser
name|user
init|=
name|currentUser
operator|.
name|get
argument_list|()
decl_stmt|;
name|CapabilityControl
name|ctl
init|=
name|user
operator|.
name|getCapabilities
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|ctl
operator|.
name|canPerform
argument_list|(
name|rc
operator|.
name|value
argument_list|()
argument_list|)
operator|&&
operator|!
name|ctl
operator|.
name|canAdministrateServer
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"fatal: %s does not have \"%s\" capability."
argument_list|,
name|Objects
operator|.
name|firstNonNull
argument_list|(
name|user
operator|.
name|getUserName
argument_list|()
argument_list|,
name|user
operator|instanceof
name|IdentifiedUser
condition|?
operator|(
operator|(
name|IdentifiedUser
operator|)
name|user
operator|)
operator|.
name|getNameEmail
argument_list|()
else|:
name|user
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|rc
operator|.
name|value
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|RequireCapabilityException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|noCache (HttpServletResponse res)
specifier|private
specifier|static
name|void
name|noCache
parameter_list|(
name|HttpServletResponse
name|res
parameter_list|)
block|{
name|res
operator|.
name|setHeader
argument_list|(
literal|"Expires"
argument_list|,
literal|"Fri, 01 Jan 1980 00:00:00 GMT"
argument_list|)
expr_stmt|;
name|res
operator|.
name|setHeader
argument_list|(
literal|"Pragma"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
name|res
operator|.
name|setHeader
argument_list|(
literal|"Cache-Control"
argument_list|,
literal|"no-cache, must-revalidate"
argument_list|)
expr_stmt|;
name|res
operator|.
name|setHeader
argument_list|(
literal|"Content-Disposition"
argument_list|,
literal|"attachment"
argument_list|)
expr_stmt|;
block|}
DECL|method|handleError ( Throwable err, HttpServletRequest req, HttpServletResponse res)
specifier|private
specifier|static
name|void
name|handleError
parameter_list|(
name|Throwable
name|err
parameter_list|,
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|uri
init|=
name|req
operator|.
name|getRequestURI
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|req
operator|.
name|getQueryString
argument_list|()
argument_list|)
condition|)
block|{
name|uri
operator|+=
literal|"?"
operator|+
name|req
operator|.
name|getQueryString
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Error in %s %s"
argument_list|,
name|req
operator|.
name|getMethod
argument_list|()
argument_list|,
name|uri
argument_list|)
argument_list|,
name|err
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|res
operator|.
name|isCommitted
argument_list|()
condition|)
block|{
name|res
operator|.
name|reset
argument_list|()
expr_stmt|;
name|res
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|)
expr_stmt|;
name|noCache
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|sendText
argument_list|(
name|req
argument_list|,
name|res
argument_list|,
literal|"Internal Server Error"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|acceptsJson (HttpServletRequest req)
specifier|protected
specifier|static
name|boolean
name|acceptsJson
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
name|String
name|accept
init|=
name|req
operator|.
name|getHeader
argument_list|(
literal|"Accept"
argument_list|)
decl_stmt|;
if|if
condition|(
name|accept
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|JSON_TYPE
operator|.
name|equals
argument_list|(
name|accept
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|accept
operator|.
name|startsWith
argument_list|(
name|JSON_TYPE
operator|+
literal|","
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
for|for
control|(
name|String
name|p
range|:
name|accept
operator|.
name|split
argument_list|(
literal|"[ ,;][ ,;]*"
argument_list|)
control|)
block|{
if|if
condition|(
name|JSON_TYPE
operator|.
name|equals
argument_list|(
name|p
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|sendText (HttpServletRequest req, HttpServletResponse res, String data)
specifier|protected
specifier|static
name|void
name|sendText
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|res
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|res
operator|.
name|setCharacterEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|send
argument_list|(
name|req
argument_list|,
name|res
argument_list|,
name|data
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|send (HttpServletRequest req, HttpServletResponse res, byte[] data)
specifier|protected
specifier|static
name|void
name|send
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|data
operator|.
name|length
operator|>
literal|256
operator|&&
name|RPCServletUtils
operator|.
name|acceptsGzipEncoding
argument_list|(
name|req
argument_list|)
condition|)
block|{
name|res
operator|.
name|setHeader
argument_list|(
literal|"Content-Encoding"
argument_list|,
literal|"gzip"
argument_list|)
expr_stmt|;
name|data
operator|=
name|HtmlDomUtil
operator|.
name|compress
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|res
operator|.
name|setContentLength
argument_list|(
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|OutputStream
name|out
init|=
name|res
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ParameterParser
specifier|public
specifier|static
class|class
name|ParameterParser
block|{
DECL|field|parserFactory
specifier|private
specifier|final
name|CmdLineParser
operator|.
name|Factory
name|parserFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|ParameterParser (CmdLineParser.Factory pf)
name|ParameterParser
parameter_list|(
name|CmdLineParser
operator|.
name|Factory
name|pf
parameter_list|)
block|{
name|this
operator|.
name|parserFactory
operator|=
name|pf
expr_stmt|;
block|}
DECL|method|parse (T param, HttpServletRequest req, HttpServletResponse res)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|boolean
name|parse
parameter_list|(
name|T
name|param
parameter_list|,
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|parse
argument_list|(
name|param
argument_list|,
name|req
argument_list|,
name|res
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
return|;
block|}
DECL|method|parse (T param, HttpServletRequest req, HttpServletResponse res, Set<String> argNames)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|boolean
name|parse
parameter_list|(
name|T
name|param
parameter_list|,
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|argNames
parameter_list|)
throws|throws
name|IOException
block|{
name|CmdLineParser
name|clp
init|=
name|parserFactory
operator|.
name|create
argument_list|(
name|param
argument_list|)
decl_stmt|;
try|try
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|parameterMap
init|=
name|req
operator|.
name|getParameterMap
argument_list|()
decl_stmt|;
name|clp
operator|.
name|parseOptionMap
argument_list|(
name|parameterMap
argument_list|,
name|argNames
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CmdLineException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|clp
operator|.
name|wasHelpRequestedByOption
argument_list|()
condition|)
block|{
name|res
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|)
expr_stmt|;
name|sendText
argument_list|(
name|req
argument_list|,
name|res
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|clp
operator|.
name|wasHelpRequestedByOption
argument_list|()
condition|)
block|{
name|StringWriter
name|msg
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|clp
operator|.
name|printQueryStringUsage
argument_list|(
name|req
operator|.
name|getRequestURI
argument_list|()
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|msg
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|clp
operator|.
name|printUsage
argument_list|(
name|msg
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|msg
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|sendText
argument_list|(
name|req
argument_list|,
name|res
argument_list|,
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
comment|// Never serialized or thrown out of this class.
DECL|class|RequireCapabilityException
specifier|private
specifier|static
class|class
name|RequireCapabilityException
extends|extends
name|Exception
block|{
DECL|method|RequireCapabilityException (String msg)
specifier|public
name|RequireCapabilityException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

