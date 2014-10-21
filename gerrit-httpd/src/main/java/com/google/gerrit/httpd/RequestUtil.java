begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_comment
comment|/** Utilities for manipulating HTTP request objects. */
end_comment

begin_class
DECL|class|RequestUtil
specifier|public
class|class
name|RequestUtil
block|{
comment|/** HTTP request attribute for storing the Throwable that caused an error condition. */
DECL|field|ATTRIBUTE_ERROR_TRACE
specifier|private
specifier|static
specifier|final
name|String
name|ATTRIBUTE_ERROR_TRACE
init|=
name|RequestUtil
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"/ErrorTraceThrowable"
decl_stmt|;
DECL|method|setErrorTraceAttribute (HttpServletRequest req, Throwable t)
specifier|public
specifier|static
name|void
name|setErrorTraceAttribute
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|req
operator|.
name|setAttribute
argument_list|(
name|ATTRIBUTE_ERROR_TRACE
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
DECL|method|getErrorTraceAttribute (HttpServletRequest req)
specifier|public
specifier|static
name|Throwable
name|getErrorTraceAttribute
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
return|return
operator|(
name|Throwable
operator|)
name|req
operator|.
name|getAttribute
argument_list|(
name|ATTRIBUTE_ERROR_TRACE
argument_list|)
return|;
block|}
comment|/**    * @return the same value as {@link HttpServletRequest#getPathInfo()}, but    *     without decoding URL-encoded characters.    */
DECL|method|getEncodedPathInfo (HttpServletRequest req)
specifier|public
specifier|static
name|String
name|getEncodedPathInfo
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
comment|// Based on com.google.guice.ServletDefinition$1#getPathInfo() from:
comment|// https://github.com/google/guice/blob/41c126f99d6309886a0ded2ac729033d755e1593/extensions/servlet/src/com/google/inject/servlet/ServletDefinition.java
name|String
name|servletPath
init|=
name|req
operator|.
name|getServletPath
argument_list|()
decl_stmt|;
name|int
name|servletPathLength
init|=
name|servletPath
operator|.
name|length
argument_list|()
decl_stmt|;
name|String
name|requestUri
init|=
name|req
operator|.
name|getRequestURI
argument_list|()
decl_stmt|;
name|String
name|pathInfo
init|=
name|requestUri
operator|.
name|substring
argument_list|(
name|req
operator|.
name|getContextPath
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"[/]{2,}"
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathInfo
operator|.
name|startsWith
argument_list|(
name|servletPath
argument_list|)
condition|)
block|{
name|pathInfo
operator|=
name|pathInfo
operator|.
name|substring
argument_list|(
name|servletPathLength
argument_list|)
expr_stmt|;
comment|// Corner case: when servlet path& request path match exactly (without
comment|// trailing '/'), then pathinfo is null.
if|if
condition|(
name|pathInfo
operator|.
name|isEmpty
argument_list|()
operator|&&
name|servletPathLength
operator|>
literal|0
condition|)
block|{
name|pathInfo
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|pathInfo
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|pathInfo
return|;
block|}
DECL|method|RequestUtil ()
specifier|private
name|RequestUtil
parameter_list|()
block|{   }
block|}
end_class

end_unit

