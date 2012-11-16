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
name|git
operator|.
name|GitRepositoryManager
operator|.
name|REFS_DASHBOARDS
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
name|RestReadView
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
name|DashboardsCollection
operator|.
name|DashboardInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_class
DECL|class|GetDashboard
class|class
name|GetDashboard
implements|implements
name|RestReadView
argument_list|<
name|DashboardResource
argument_list|>
block|{
annotation|@
name|Override
DECL|method|apply (DashboardResource resource)
specifier|public
name|DashboardInfo
name|apply
parameter_list|(
name|DashboardResource
name|resource
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
return|return
name|DashboardsCollection
operator|.
name|parse
argument_list|(
name|resource
operator|.
name|getControl
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|,
name|resource
operator|.
name|getRefName
argument_list|()
operator|.
name|substring
argument_list|(
name|REFS_DASHBOARDS
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|resource
operator|.
name|getPathName
argument_list|()
argument_list|,
name|resource
operator|.
name|getConfig
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

