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
DECL|package|com.google.gerrit.common
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
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
name|data
operator|.
name|AccessSection
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
name|common
operator|.
name|data
operator|.
name|Permission
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|Map
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
DECL|class|ProjectAccessUtil
specifier|public
class|class
name|ProjectAccessUtil
block|{
DECL|method|mergeSections (List<AccessSection> src)
specifier|public
specifier|static
name|List
argument_list|<
name|AccessSection
argument_list|>
name|mergeSections
parameter_list|(
name|List
argument_list|<
name|AccessSection
argument_list|>
name|src
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|AccessSection
argument_list|>
name|map
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccessSection
name|section
range|:
name|src
control|)
block|{
if|if
condition|(
name|section
operator|.
name|getPermissions
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
specifier|final
name|AccessSection
name|prior
init|=
name|map
operator|.
name|get
argument_list|(
name|section
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|prior
operator|!=
literal|null
condition|)
block|{
name|prior
operator|.
name|mergeFrom
argument_list|(
name|section
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|map
operator|.
name|put
argument_list|(
name|section
operator|.
name|getName
argument_list|()
argument_list|,
name|section
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|map
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
DECL|method|removeEmptyPermissionsAndSections ( final List<AccessSection> src)
specifier|public
specifier|static
name|List
argument_list|<
name|AccessSection
argument_list|>
name|removeEmptyPermissionsAndSections
parameter_list|(
specifier|final
name|List
argument_list|<
name|AccessSection
argument_list|>
name|src
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|AccessSection
argument_list|>
name|sectionsToRemove
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccessSection
name|section
range|:
name|src
control|)
block|{
specifier|final
name|Set
argument_list|<
name|Permission
argument_list|>
name|permissionsToRemove
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Permission
name|permission
range|:
name|section
operator|.
name|getPermissions
argument_list|()
control|)
block|{
if|if
condition|(
name|permission
operator|.
name|getRules
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|permissionsToRemove
operator|.
name|add
argument_list|(
name|permission
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Permission
name|permissionToRemove
range|:
name|permissionsToRemove
control|)
block|{
name|section
operator|.
name|remove
argument_list|(
name|permissionToRemove
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|section
operator|.
name|getPermissions
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|sectionsToRemove
operator|.
name|add
argument_list|(
name|section
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|AccessSection
name|sectionToRemove
range|:
name|sectionsToRemove
control|)
block|{
name|src
operator|.
name|remove
argument_list|(
name|sectionToRemove
argument_list|)
expr_stmt|;
block|}
return|return
name|src
return|;
block|}
block|}
end_class

end_unit

