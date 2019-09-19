begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.restapi.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|restapi
operator|.
name|project
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
name|api
operator|.
name|projects
operator|.
name|DashboardInfo
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
name|api
operator|.
name|projects
operator|.
name|SetDashboardInput
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
name|IdString
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
name|ResourceNotFoundException
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
name|Response
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
name|RestCollectionCreateView
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
name|DashboardResource
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
name|ProjectResource
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Provider
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
name|Singleton
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|CreateDashboard
specifier|public
class|class
name|CreateDashboard
implements|implements
name|RestCollectionCreateView
argument_list|<
name|ProjectResource
argument_list|,
name|DashboardResource
argument_list|,
name|SetDashboardInput
argument_list|>
block|{
DECL|field|setDefault
specifier|private
specifier|final
name|Provider
argument_list|<
name|SetDefaultDashboard
argument_list|>
name|setDefault
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--inherited"
argument_list|,
name|usage
operator|=
literal|"set dashboard inherited by children"
argument_list|)
DECL|field|inherited
specifier|private
name|boolean
name|inherited
decl_stmt|;
annotation|@
name|Inject
DECL|method|CreateDashboard (Provider<SetDefaultDashboard> setDefault)
name|CreateDashboard
parameter_list|(
name|Provider
argument_list|<
name|SetDefaultDashboard
argument_list|>
name|setDefault
parameter_list|)
block|{
name|this
operator|.
name|setDefault
operator|=
name|setDefault
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ProjectResource parent, IdString id, SetDashboardInput input)
specifier|public
name|Response
argument_list|<
name|DashboardInfo
argument_list|>
name|apply
parameter_list|(
name|ProjectResource
name|parent
parameter_list|,
name|IdString
name|id
parameter_list|,
name|SetDashboardInput
name|input
parameter_list|)
throws|throws
name|Exception
block|{
name|parent
operator|.
name|getProjectState
argument_list|()
operator|.
name|checkStatePermitsWrite
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|DashboardsCollection
operator|.
name|isDefaultDashboard
argument_list|(
name|id
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
block|}
name|SetDefaultDashboard
name|set
init|=
name|setDefault
operator|.
name|get
argument_list|()
decl_stmt|;
name|set
operator|.
name|inherited
operator|=
name|inherited
expr_stmt|;
return|return
name|Response
operator|.
name|created
argument_list|(
name|set
operator|.
name|apply
argument_list|(
name|DashboardResource
operator|.
name|projectDefault
argument_list|(
name|parent
operator|.
name|getProjectState
argument_list|()
argument_list|,
name|parent
operator|.
name|getUser
argument_list|()
argument_list|)
argument_list|,
name|input
argument_list|)
operator|.
name|value
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

