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
DECL|package|com.google.gerrit.server.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|git
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
name|collect
operator|.
name|ImmutableList
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
name|reviewdb
operator|.
name|client
operator|.
name|RefNames
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

begin_class
DECL|class|BranchOrderSection
specifier|public
class|class
name|BranchOrderSection
block|{
comment|/**    * Branch names ordered from least to the most stable.    *    * Typically the order will be like: master, stable-M.N, stable-M.N-1, ...    */
DECL|field|order
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|order
decl_stmt|;
DECL|method|BranchOrderSection (String[] order)
specifier|public
name|BranchOrderSection
parameter_list|(
name|String
index|[]
name|order
parameter_list|)
block|{
if|if
condition|(
name|order
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|order
operator|=
name|ImmutableList
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|String
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|b
range|:
name|order
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|RefNames
operator|.
name|fullName
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|order
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getMoreStable (String branch)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getMoreStable
parameter_list|(
name|String
name|branch
parameter_list|)
block|{
name|int
name|i
init|=
name|order
operator|.
name|indexOf
argument_list|(
name|RefNames
operator|.
name|fullName
argument_list|(
name|branch
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<=
name|i
condition|)
block|{
return|return
name|order
operator|.
name|subList
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|order
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

