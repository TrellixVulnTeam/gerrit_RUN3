begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2009 Google Inc.
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
DECL|package|com.google.gerrit.client.openid
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|openid
package|;
end_package

begin_class
DECL|class|AllowFrameImpl
class|class
name|AllowFrameImpl
block|{
DECL|method|permit (final String url)
name|boolean
name|permit
parameter_list|(
specifier|final
name|String
name|url
parameter_list|)
block|{
if|if
condition|(
name|OpenIdUtil
operator|.
name|URL_GOOGLE
operator|.
name|equals
argument_list|(
name|url
argument_list|)
operator|||
name|url
operator|.
name|startsWith
argument_list|(
name|OpenIdUtil
operator|.
name|URL_GOOGLE
operator|+
literal|"?"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|is_claimID
argument_list|(
name|url
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|is_claimID (final String url)
specifier|protected
specifier|static
name|boolean
name|is_claimID
parameter_list|(
specifier|final
name|String
name|url
parameter_list|)
block|{
return|return
name|url
operator|.
name|contains
argument_list|(
literal|".claimid.com/"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

