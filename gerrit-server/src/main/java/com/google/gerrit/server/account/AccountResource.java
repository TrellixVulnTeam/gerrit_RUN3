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
name|RestResource
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
name|RestView
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
name|inject
operator|.
name|TypeLiteral
import|;
end_import

begin_class
DECL|class|AccountResource
specifier|public
class|class
name|AccountResource
implements|implements
name|RestResource
block|{
DECL|field|ACCOUNT_KIND
specifier|public
specifier|static
specifier|final
name|TypeLiteral
argument_list|<
name|RestView
argument_list|<
name|AccountResource
argument_list|>
argument_list|>
name|ACCOUNT_KIND
init|=
operator|new
name|TypeLiteral
argument_list|<
name|RestView
argument_list|<
name|AccountResource
argument_list|>
argument_list|>
argument_list|()
block|{}
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|IdentifiedUser
name|user
decl_stmt|;
DECL|method|AccountResource (IdentifiedUser user)
name|AccountResource
parameter_list|(
name|IdentifiedUser
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
DECL|method|getUser ()
specifier|public
name|IdentifiedUser
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
block|}
end_class

end_unit

