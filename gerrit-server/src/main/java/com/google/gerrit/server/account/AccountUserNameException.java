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
DECL|package|com.google.gerrit.server.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|account
package|;
end_package

begin_comment
comment|/**  * Thrown by {@link AccountManager} if the user name for a newly created account could not be set  * and the realm does not allow the user to set a user name manually.  */
end_comment

begin_class
DECL|class|AccountUserNameException
specifier|public
class|class
name|AccountUserNameException
extends|extends
name|AccountException
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
DECL|method|AccountUserNameException (final String message, final Throwable why)
specifier|public
name|AccountUserNameException
parameter_list|(
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|Throwable
name|why
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|why
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

