begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
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
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|net
operator|.
name|HttpHeaders
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
name|RawInput
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
name|OutputFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|Header
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|fluent
operator|.
name|Request
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|BufferedHttpEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|InputStreamEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|StringEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|message
operator|.
name|BasicHeader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|InputStream
import|;
end_import

begin_class
DECL|class|RestSession
specifier|public
class|class
name|RestSession
extends|extends
name|HttpSession
block|{
DECL|method|RestSession (GerritServer server, TestAccount account)
specifier|public
name|RestSession
parameter_list|(
name|GerritServer
name|server
parameter_list|,
name|TestAccount
name|account
parameter_list|)
block|{
name|super
argument_list|(
name|server
argument_list|,
name|account
argument_list|)
expr_stmt|;
block|}
DECL|method|get (String endPoint)
specifier|public
name|RestResponse
name|get
parameter_list|(
name|String
name|endPoint
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getWithHeader
argument_list|(
name|endPoint
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getJsonAccept (String endPoint)
specifier|public
name|RestResponse
name|getJsonAccept
parameter_list|(
name|String
name|endPoint
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getWithHeader
argument_list|(
name|endPoint
argument_list|,
operator|new
name|BasicHeader
argument_list|(
name|HttpHeaders
operator|.
name|ACCEPT
argument_list|,
literal|"application/json"
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getWithHeader (String endPoint, Header header)
specifier|private
name|RestResponse
name|getWithHeader
parameter_list|(
name|String
name|endPoint
parameter_list|,
name|Header
name|header
parameter_list|)
throws|throws
name|IOException
block|{
name|Request
name|get
init|=
name|Request
operator|.
name|Get
argument_list|(
name|url
operator|+
literal|"/a"
operator|+
name|endPoint
argument_list|)
decl_stmt|;
if|if
condition|(
name|header
operator|!=
literal|null
condition|)
block|{
name|get
operator|.
name|addHeader
argument_list|(
name|header
argument_list|)
expr_stmt|;
block|}
return|return
name|execute
argument_list|(
name|get
argument_list|)
return|;
block|}
DECL|method|put (String endPoint)
specifier|public
name|RestResponse
name|put
parameter_list|(
name|String
name|endPoint
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|put
argument_list|(
name|endPoint
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|put (String endPoint, Object content)
specifier|public
name|RestResponse
name|put
parameter_list|(
name|String
name|endPoint
parameter_list|,
name|Object
name|content
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|putWithHeader
argument_list|(
name|endPoint
argument_list|,
literal|null
argument_list|,
name|content
argument_list|)
return|;
block|}
DECL|method|putWithHeader (String endPoint, Header header)
specifier|public
name|RestResponse
name|putWithHeader
parameter_list|(
name|String
name|endPoint
parameter_list|,
name|Header
name|header
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|putWithHeader
argument_list|(
name|endPoint
argument_list|,
name|header
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|putWithHeader (String endPoint, Header header, Object content)
specifier|public
name|RestResponse
name|putWithHeader
parameter_list|(
name|String
name|endPoint
parameter_list|,
name|Header
name|header
parameter_list|,
name|Object
name|content
parameter_list|)
throws|throws
name|IOException
block|{
name|Request
name|put
init|=
name|Request
operator|.
name|Put
argument_list|(
name|url
operator|+
literal|"/a"
operator|+
name|endPoint
argument_list|)
decl_stmt|;
if|if
condition|(
name|header
operator|!=
literal|null
condition|)
block|{
name|put
operator|.
name|addHeader
argument_list|(
name|header
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
block|{
name|put
operator|.
name|addHeader
argument_list|(
operator|new
name|BasicHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/json"
argument_list|)
argument_list|)
expr_stmt|;
name|put
operator|.
name|body
argument_list|(
operator|new
name|StringEntity
argument_list|(
name|OutputFormat
operator|.
name|JSON_COMPACT
operator|.
name|newGson
argument_list|()
operator|.
name|toJson
argument_list|(
name|content
argument_list|)
argument_list|,
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|execute
argument_list|(
name|put
argument_list|)
return|;
block|}
DECL|method|putRaw (String endPoint, RawInput stream)
specifier|public
name|RestResponse
name|putRaw
parameter_list|(
name|String
name|endPoint
parameter_list|,
name|RawInput
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|Request
name|put
init|=
name|Request
operator|.
name|Put
argument_list|(
name|url
operator|+
literal|"/a"
operator|+
name|endPoint
argument_list|)
decl_stmt|;
name|put
operator|.
name|addHeader
argument_list|(
operator|new
name|BasicHeader
argument_list|(
literal|"Content-Type"
argument_list|,
name|stream
operator|.
name|getContentType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|put
operator|.
name|body
argument_list|(
operator|new
name|BufferedHttpEntity
argument_list|(
operator|new
name|InputStreamEntity
argument_list|(
name|stream
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|stream
operator|.
name|getContentLength
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|execute
argument_list|(
name|put
argument_list|)
return|;
block|}
DECL|method|post (String endPoint)
specifier|public
name|RestResponse
name|post
parameter_list|(
name|String
name|endPoint
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|post
argument_list|(
name|endPoint
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|post (String endPoint, Object content)
specifier|public
name|RestResponse
name|post
parameter_list|(
name|String
name|endPoint
parameter_list|,
name|Object
name|content
parameter_list|)
throws|throws
name|IOException
block|{
name|Request
name|post
init|=
name|Request
operator|.
name|Post
argument_list|(
name|url
operator|+
literal|"/a"
operator|+
name|endPoint
argument_list|)
decl_stmt|;
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
block|{
name|post
operator|.
name|addHeader
argument_list|(
operator|new
name|BasicHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/json"
argument_list|)
argument_list|)
expr_stmt|;
name|post
operator|.
name|body
argument_list|(
operator|new
name|StringEntity
argument_list|(
name|OutputFormat
operator|.
name|JSON_COMPACT
operator|.
name|newGson
argument_list|()
operator|.
name|toJson
argument_list|(
name|content
argument_list|)
argument_list|,
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|execute
argument_list|(
name|post
argument_list|)
return|;
block|}
DECL|method|delete (String endPoint)
specifier|public
name|RestResponse
name|delete
parameter_list|(
name|String
name|endPoint
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|execute
argument_list|(
name|Request
operator|.
name|Delete
argument_list|(
name|url
operator|+
literal|"/a"
operator|+
name|endPoint
argument_list|)
argument_list|)
return|;
block|}
DECL|method|newRawInput (String content)
specifier|public
specifier|static
name|RawInput
name|newRawInput
parameter_list|(
name|String
name|content
parameter_list|)
block|{
return|return
name|newRawInput
argument_list|(
name|content
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
return|;
block|}
DECL|method|newRawInput (final byte[] bytes)
specifier|public
specifier|static
name|RawInput
name|newRawInput
parameter_list|(
specifier|final
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|bytes
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
return|return
operator|new
name|RawInput
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
literal|"application/octet-stream"
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getContentLength
parameter_list|()
block|{
return|return
name|bytes
operator|.
name|length
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

