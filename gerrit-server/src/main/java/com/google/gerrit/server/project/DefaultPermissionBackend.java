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
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|gerrit
operator|.
name|server
operator|.
name|CurrentUser
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
name|permissions
operator|.
name|FailedPermissionBackend
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
name|permissions
operator|.
name|PermissionBackend
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
name|Singleton
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|DefaultPermissionBackend
class|class
name|DefaultPermissionBackend
extends|extends
name|PermissionBackend
block|{
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|DefaultPermissionBackend (ProjectCache projectCache)
name|DefaultPermissionBackend
parameter_list|(
name|ProjectCache
name|projectCache
parameter_list|)
block|{
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|user (CurrentUser user)
specifier|public
name|WithUser
name|user
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
block|{
return|return
operator|new
name|WithUserImpl
argument_list|(
name|checkNotNull
argument_list|(
name|user
argument_list|,
literal|"user"
argument_list|)
argument_list|)
return|;
block|}
DECL|class|WithUserImpl
class|class
name|WithUserImpl
extends|extends
name|WithUser
block|{
DECL|field|user
specifier|private
specifier|final
name|CurrentUser
name|user
decl_stmt|;
DECL|method|WithUserImpl (CurrentUser user)
name|WithUserImpl
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|checkNotNull
argument_list|(
name|user
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|project (Project.NameKey project)
specifier|public
name|ForProject
name|project
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
block|{
try|try
block|{
name|ProjectState
name|state
init|=
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|project
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
return|return
name|state
operator|.
name|controlFor
argument_list|(
name|user
argument_list|)
operator|.
name|asForProject
argument_list|()
operator|.
name|database
argument_list|(
name|db
argument_list|)
return|;
block|}
return|return
name|FailedPermissionBackend
operator|.
name|project
argument_list|(
literal|"not found"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
name|FailedPermissionBackend
operator|.
name|project
argument_list|(
literal|"unavailable"
argument_list|,
name|e
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

