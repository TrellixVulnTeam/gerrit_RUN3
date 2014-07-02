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
DECL|package|com.google.gerrit.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|Lists
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
name|registration
operator|.
name|DynamicSet
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
name|webui
operator|.
name|PatchSetWebLink
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
name|webui
operator|.
name|ProjectWebLink
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
name|Inject
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
DECL|class|WebLinks
specifier|public
class|class
name|WebLinks
block|{
DECL|field|patchSetLinks
specifier|private
specifier|final
name|DynamicSet
argument_list|<
name|PatchSetWebLink
argument_list|>
name|patchSetLinks
decl_stmt|;
DECL|field|projectLinks
specifier|private
specifier|final
name|DynamicSet
argument_list|<
name|ProjectWebLink
argument_list|>
name|projectLinks
decl_stmt|;
DECL|method|WebLinks (DynamicSet<PatchSetWebLink> patchSetLinks, DynamicSet<ProjectWebLink> projectLinks)
specifier|public
name|WebLinks
parameter_list|(
name|DynamicSet
argument_list|<
name|PatchSetWebLink
argument_list|>
name|patchSetLinks
parameter_list|,
name|DynamicSet
argument_list|<
name|ProjectWebLink
argument_list|>
name|projectLinks
parameter_list|)
block|{
name|this
operator|.
name|patchSetLinks
operator|=
name|patchSetLinks
expr_stmt|;
name|this
operator|.
name|projectLinks
operator|=
name|projectLinks
expr_stmt|;
block|}
DECL|method|getPatchSetLinks (String project, String commit)
specifier|public
name|Iterable
argument_list|<
name|Link
argument_list|>
name|getPatchSetLinks
parameter_list|(
name|String
name|project
parameter_list|,
name|String
name|commit
parameter_list|)
block|{
name|List
argument_list|<
name|Link
argument_list|>
name|links
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetWebLink
name|webLink
range|:
name|patchSetLinks
control|)
block|{
name|links
operator|.
name|add
argument_list|(
operator|new
name|Link
argument_list|(
name|webLink
operator|.
name|getLinkName
argument_list|()
argument_list|,
name|webLink
operator|.
name|getPatchSetUrl
argument_list|(
name|project
argument_list|,
name|commit
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|links
return|;
block|}
DECL|method|getProjectLinks (String project)
specifier|public
name|Iterable
argument_list|<
name|Link
argument_list|>
name|getProjectLinks
parameter_list|(
name|String
name|project
parameter_list|)
block|{
name|List
argument_list|<
name|Link
argument_list|>
name|links
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|ProjectWebLink
name|webLink
range|:
name|projectLinks
control|)
block|{
name|links
operator|.
name|add
argument_list|(
operator|new
name|Link
argument_list|(
name|webLink
operator|.
name|getLinkName
argument_list|()
argument_list|,
name|webLink
operator|.
name|getProjectUrl
argument_list|(
name|project
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|links
return|;
block|}
DECL|class|Link
specifier|public
class|class
name|Link
block|{
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
DECL|field|url
specifier|public
name|String
name|url
decl_stmt|;
DECL|method|Link (String name, String url)
specifier|public
name|Link
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|url
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

