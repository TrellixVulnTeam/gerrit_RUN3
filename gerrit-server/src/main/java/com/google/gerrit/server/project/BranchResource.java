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
name|RestView
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
name|Branch
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
name|TypeLiteral
import|;
end_import

begin_class
DECL|class|BranchResource
specifier|public
class|class
name|BranchResource
extends|extends
name|ProjectResource
block|{
DECL|field|BRANCH_KIND
specifier|public
specifier|static
specifier|final
name|TypeLiteral
argument_list|<
name|RestView
argument_list|<
name|BranchResource
argument_list|>
argument_list|>
name|BRANCH_KIND
init|=
operator|new
name|TypeLiteral
argument_list|<
name|RestView
argument_list|<
name|BranchResource
argument_list|>
argument_list|>
argument_list|()
block|{}
decl_stmt|;
DECL|field|branchInfo
specifier|private
specifier|final
name|BranchInfo
name|branchInfo
decl_stmt|;
DECL|method|BranchResource (ProjectControl control, BranchInfo branchInfo)
specifier|public
name|BranchResource
parameter_list|(
name|ProjectControl
name|control
parameter_list|,
name|BranchInfo
name|branchInfo
parameter_list|)
block|{
name|super
argument_list|(
name|control
argument_list|)
expr_stmt|;
name|this
operator|.
name|branchInfo
operator|=
name|branchInfo
expr_stmt|;
block|}
DECL|method|getBranchInfo ()
specifier|public
name|BranchInfo
name|getBranchInfo
parameter_list|()
block|{
return|return
name|branchInfo
return|;
block|}
DECL|method|getBranchKey ()
specifier|public
name|Branch
operator|.
name|NameKey
name|getBranchKey
parameter_list|()
block|{
return|return
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|getNameKey
argument_list|()
argument_list|,
name|branchInfo
operator|.
name|ref
argument_list|)
return|;
block|}
DECL|method|getRef ()
specifier|public
name|String
name|getRef
parameter_list|()
block|{
return|return
name|branchInfo
operator|.
name|ref
return|;
block|}
DECL|method|getRevision ()
specifier|public
name|String
name|getRevision
parameter_list|()
block|{
return|return
name|branchInfo
operator|.
name|revision
return|;
block|}
block|}
end_class

end_unit

