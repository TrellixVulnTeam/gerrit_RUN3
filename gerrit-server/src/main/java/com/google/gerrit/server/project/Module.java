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
DECL|package|com.google.gerrit.server.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|gerrit
operator|.
name|server
operator|.
name|project
operator|.
name|DashboardResource
operator|.
name|DASHBOARD_KIND
import|;
end_import

begin_import
import|import static
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
name|ProjectResource
operator|.
name|PROJECT_KIND
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
name|DynamicMap
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
name|RestApiModule
import|;
end_import

begin_class
DECL|class|Module
specifier|public
class|class
name|Module
extends|extends
name|RestApiModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|ProjectsCollection
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|DashboardsCollection
operator|.
name|class
argument_list|)
expr_stmt|;
name|DynamicMap
operator|.
name|mapOf
argument_list|(
name|binder
argument_list|()
argument_list|,
name|PROJECT_KIND
argument_list|)
expr_stmt|;
name|DynamicMap
operator|.
name|mapOf
argument_list|(
name|binder
argument_list|()
argument_list|,
name|DASHBOARD_KIND
argument_list|)
expr_stmt|;
name|get
argument_list|(
name|PROJECT_KIND
argument_list|)
operator|.
name|to
argument_list|(
name|GetProject
operator|.
name|class
argument_list|)
expr_stmt|;
name|get
argument_list|(
name|PROJECT_KIND
argument_list|,
literal|"description"
argument_list|)
operator|.
name|to
argument_list|(
name|GetDescription
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|PROJECT_KIND
argument_list|,
literal|"description"
argument_list|)
operator|.
name|to
argument_list|(
name|PutDescription
operator|.
name|class
argument_list|)
expr_stmt|;
name|delete
argument_list|(
name|PROJECT_KIND
argument_list|,
literal|"description"
argument_list|)
operator|.
name|to
argument_list|(
name|PutDescription
operator|.
name|class
argument_list|)
expr_stmt|;
name|get
argument_list|(
name|PROJECT_KIND
argument_list|,
literal|"parent"
argument_list|)
operator|.
name|to
argument_list|(
name|GetParent
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|PROJECT_KIND
argument_list|,
literal|"parent"
argument_list|)
operator|.
name|to
argument_list|(
name|SetParent
operator|.
name|class
argument_list|)
expr_stmt|;
name|child
argument_list|(
name|PROJECT_KIND
argument_list|,
literal|"dashboards"
argument_list|)
operator|.
name|to
argument_list|(
name|DashboardsCollection
operator|.
name|class
argument_list|)
expr_stmt|;
name|get
argument_list|(
name|DASHBOARD_KIND
argument_list|)
operator|.
name|to
argument_list|(
name|GetDashboard
operator|.
name|class
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|DASHBOARD_KIND
argument_list|)
operator|.
name|to
argument_list|(
name|SetDashboard
operator|.
name|class
argument_list|)
expr_stmt|;
name|delete
argument_list|(
name|DASHBOARD_KIND
argument_list|)
operator|.
name|to
argument_list|(
name|DeleteDashboard
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

