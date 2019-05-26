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
DECL|package|com.google.gerrit.acceptance.rest.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
operator|.
name|project
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertWithMessage
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
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|truth
operator|.
name|IterableSubject
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
name|entities
operator|.
name|AccountGroup
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
name|entities
operator|.
name|Project
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
name|common
operator|.
name|ProjectInfo
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
name|Url
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
name|project
operator|.
name|ProjectState
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
DECL|class|ProjectAssert
specifier|public
class|class
name|ProjectAssert
block|{
DECL|method|assertThatNameList (Iterable<ProjectInfo> actualIt)
specifier|public
specifier|static
name|IterableSubject
name|assertThatNameList
parameter_list|(
name|Iterable
argument_list|<
name|ProjectInfo
argument_list|>
name|actualIt
parameter_list|)
block|{
name|List
argument_list|<
name|ProjectInfo
argument_list|>
name|actual
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|actualIt
argument_list|)
decl_stmt|;
for|for
control|(
name|ProjectInfo
name|info
range|:
name|actual
control|)
block|{
name|assertWithMessage
argument_list|(
literal|"missing project name"
argument_list|)
operator|.
name|that
argument_list|(
name|info
operator|.
name|name
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertWithMessage
argument_list|(
literal|"project name does not match id"
argument_list|)
operator|.
name|that
argument_list|(
name|Url
operator|.
name|decode
argument_list|(
name|info
operator|.
name|id
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|info
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|assertThat
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|actual
argument_list|,
name|p
lambda|->
name|Project
operator|.
name|nameKey
argument_list|(
name|p
operator|.
name|name
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|assertProjectInfo (Project project, ProjectInfo info)
specifier|public
specifier|static
name|void
name|assertProjectInfo
parameter_list|(
name|Project
name|project
parameter_list|,
name|ProjectInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|info
operator|.
name|name
operator|!=
literal|null
condition|)
block|{
comment|// 'name' is not set if returned in a map
name|assertThat
argument_list|(
name|info
operator|.
name|name
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|project
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|Url
operator|.
name|decode
argument_list|(
name|info
operator|.
name|id
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|project
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Project
operator|.
name|NameKey
name|parentName
init|=
name|project
operator|.
name|getParent
argument_list|(
name|Project
operator|.
name|nameKey
argument_list|(
literal|"All-Projects"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentName
operator|!=
literal|null
condition|)
block|{
name|assertThat
argument_list|(
name|info
operator|.
name|parent
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|parentName
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|info
operator|.
name|parent
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|info
operator|.
name|description
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|project
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertProjectOwners ( Set<AccountGroup.UUID> expectedOwners, ProjectState state)
specifier|public
specifier|static
name|void
name|assertProjectOwners
parameter_list|(
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|expectedOwners
parameter_list|,
name|ProjectState
name|state
parameter_list|)
block|{
for|for
control|(
name|AccountGroup
operator|.
name|UUID
name|g
range|:
name|state
operator|.
name|getOwners
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|expectedOwners
operator|.
name|remove
argument_list|(
name|g
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|expectedOwners
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

