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
DECL|package|com.google.gerrit.server.auth
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|auth
package|;
end_package

begin_comment
comment|/**  * An authentication exception that is thrown when the credentials are not  * present. This indicates that the AuthBackend has none of the needed  * information in the request to perform authentication. If parts of the  * authentication information is available to the backend, then a different  * AuthException should be used.  */
end_comment

begin_class
DECL|class|MissingCredentialsException
specifier|public
class|class
name|MissingCredentialsException
extends|extends
name|AuthException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|6499866977513508051L
decl_stmt|;
DECL|method|MissingCredentialsException ()
specifier|public
name|MissingCredentialsException
parameter_list|()
block|{   }
DECL|method|MissingCredentialsException (String msg)
specifier|public
name|MissingCredentialsException
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
DECL|method|MissingCredentialsException (Throwable ex)
specifier|public
name|MissingCredentialsException
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|super
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
DECL|method|MissingCredentialsException (String msg, Throwable ex)
specifier|public
name|MissingCredentialsException
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|ex
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

