begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.extensions.api.changes
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
name|changes
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|StarsInput
specifier|public
class|class
name|StarsInput
block|{
DECL|field|add
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|add
decl_stmt|;
DECL|field|remove
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|remove
decl_stmt|;
DECL|method|StarsInput ()
specifier|public
name|StarsInput
parameter_list|()
block|{}
DECL|method|StarsInput (Set<String> add)
specifier|public
name|StarsInput
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|add
parameter_list|)
block|{
name|this
operator|.
name|add
operator|=
name|add
expr_stmt|;
block|}
DECL|method|StarsInput (Set<String> add, Set<String> remove)
specifier|public
name|StarsInput
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|add
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|remove
parameter_list|)
block|{
name|this
operator|.
name|add
operator|=
name|add
expr_stmt|;
name|this
operator|.
name|remove
operator|=
name|remove
expr_stmt|;
block|}
block|}
end_class

end_unit

