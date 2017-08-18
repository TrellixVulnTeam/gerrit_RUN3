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
name|BranchInfo
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
name|permissions
operator|.
name|PermissionBackendException
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
DECL|class|GetBranch
specifier|public
class|class
name|GetBranch
implements|implements
name|RestReadView
argument_list|<
name|BranchResource
argument_list|>
block|{
DECL|field|list
specifier|private
specifier|final
name|Provider
argument_list|<
name|ListBranches
argument_list|>
name|list
decl_stmt|;
annotation|@
name|Inject
DECL|method|GetBranch (Provider<ListBranches> list)
name|GetBranch
parameter_list|(
name|Provider
argument_list|<
name|ListBranches
argument_list|>
name|list
parameter_list|)
block|{
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (BranchResource rsrc)
specifier|public
name|BranchInfo
name|apply
parameter_list|(
name|BranchResource
name|rsrc
parameter_list|)
throws|throws
name|ResourceNotFoundException
throws|,
name|IOException
throws|,
name|PermissionBackendException
block|{
return|return
name|list
operator|.
name|get
argument_list|()
operator|.
name|toBranchInfo
argument_list|(
name|rsrc
argument_list|)
return|;
block|}
block|}
end_class

end_unit

