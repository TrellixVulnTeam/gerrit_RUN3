begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.extensions.api.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|api
operator|.
name|config
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
name|Nullable
import|;
end_import

begin_class
DECL|class|AccessCheckInput
specifier|public
class|class
name|AccessCheckInput
block|{
DECL|field|account
specifier|public
name|String
name|account
decl_stmt|;
DECL|field|project
specifier|public
name|String
name|project
decl_stmt|;
DECL|field|ref
annotation|@
name|Nullable
specifier|public
name|String
name|ref
decl_stmt|;
DECL|method|AccessCheckInput (String account, String project, @Nullable String ref)
specifier|public
name|AccessCheckInput
parameter_list|(
name|String
name|account
parameter_list|,
name|String
name|project
parameter_list|,
annotation|@
name|Nullable
name|String
name|ref
parameter_list|)
block|{
name|this
operator|.
name|account
operator|=
name|account
expr_stmt|;
name|this
operator|.
name|project
operator|=
name|project
expr_stmt|;
name|this
operator|.
name|ref
operator|=
name|ref
expr_stmt|;
block|}
DECL|method|AccessCheckInput ()
specifier|public
name|AccessCheckInput
parameter_list|()
block|{}
block|}
end_class

end_unit

