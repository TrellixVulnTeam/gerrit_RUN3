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
DECL|package|com.google.gerrit.client.ui
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|ui
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
name|client
operator|.
name|Gerrit
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
name|client
operator|.
name|admin
operator|.
name|Util
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
name|PageLinks
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
name|Project
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|DOM
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|Image
import|;
end_import

begin_class
DECL|class|ProjectSearchLink
specifier|public
class|class
name|ProjectSearchLink
extends|extends
name|InlineHyperlink
block|{
DECL|method|ProjectSearchLink (Project.NameKey projectName)
specifier|public
name|ProjectSearchLink
parameter_list|(
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|)
block|{
name|super
argument_list|(
literal|" "
argument_list|,
name|PageLinks
operator|.
name|toProjectDefaultDashboard
argument_list|(
name|projectName
argument_list|)
argument_list|)
expr_stmt|;
name|setTitle
argument_list|(
name|Util
operator|.
name|C
operator|.
name|projectListQueryLink
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Image
name|image
init|=
operator|new
name|Image
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|queryIcon
argument_list|()
argument_list|)
decl_stmt|;
name|image
operator|.
name|setStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|queryIcon
argument_list|()
argument_list|)
expr_stmt|;
name|DOM
operator|.
name|insertBefore
argument_list|(
name|getElement
argument_list|()
argument_list|,
name|image
operator|.
name|getElement
argument_list|()
argument_list|,
name|DOM
operator|.
name|getFirstChild
argument_list|(
name|getElement
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

