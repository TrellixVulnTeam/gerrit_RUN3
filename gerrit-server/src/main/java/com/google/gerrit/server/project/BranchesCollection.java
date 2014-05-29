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
name|AcceptsCreate
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
name|ChildCollection
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
name|server
operator|.
name|project
operator|.
name|ListBranches
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Constants
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
annotation|@
name|Singleton
DECL|class|BranchesCollection
specifier|public
class|class
name|BranchesCollection
implements|implements
name|ChildCollection
argument_list|<
name|ProjectResource
argument_list|,
name|BranchResource
argument_list|>
implements|,
name|AcceptsCreate
argument_list|<
name|ProjectResource
argument_list|>
block|{
DECL|field|views
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|BranchResource
argument_list|>
argument_list|>
name|views
decl_stmt|;
DECL|field|list
specifier|private
specifier|final
name|ListBranches
name|list
decl_stmt|;
DECL|field|createBranchFactory
specifier|private
specifier|final
name|CreateBranch
operator|.
name|Factory
name|createBranchFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|BranchesCollection (DynamicMap<RestView<BranchResource>> views, ListBranches list, CreateBranch.Factory createBranchFactory)
name|BranchesCollection
parameter_list|(
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|BranchResource
argument_list|>
argument_list|>
name|views
parameter_list|,
name|ListBranches
name|list
parameter_list|,
name|CreateBranch
operator|.
name|Factory
name|createBranchFactory
parameter_list|)
block|{
name|this
operator|.
name|views
operator|=
name|views
expr_stmt|;
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
name|this
operator|.
name|createBranchFactory
operator|=
name|createBranchFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|list ()
specifier|public
name|RestView
argument_list|<
name|ProjectResource
argument_list|>
name|list
parameter_list|()
block|{
return|return
name|list
return|;
block|}
annotation|@
name|Override
DECL|method|parse (ProjectResource parent, IdString id)
specifier|public
name|BranchResource
name|parse
parameter_list|(
name|ProjectResource
name|parent
parameter_list|,
name|IdString
name|id
parameter_list|)
throws|throws
name|ResourceNotFoundException
throws|,
name|IOException
block|{
name|String
name|branchName
init|=
name|id
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|branchName
operator|.
name|startsWith
argument_list|(
name|Constants
operator|.
name|R_REFS
argument_list|)
operator|&&
operator|!
name|branchName
operator|.
name|equals
argument_list|(
name|Constants
operator|.
name|HEAD
argument_list|)
condition|)
block|{
name|branchName
operator|=
name|Constants
operator|.
name|R_HEADS
operator|+
name|branchName
expr_stmt|;
block|}
name|List
argument_list|<
name|BranchInfo
argument_list|>
name|branches
init|=
name|list
operator|.
name|apply
argument_list|(
name|parent
argument_list|)
decl_stmt|;
for|for
control|(
name|BranchInfo
name|b
range|:
name|branches
control|)
block|{
if|if
condition|(
name|branchName
operator|.
name|equals
argument_list|(
name|b
operator|.
name|ref
argument_list|)
condition|)
block|{
return|return
operator|new
name|BranchResource
argument_list|(
name|parent
operator|.
name|getControl
argument_list|()
argument_list|,
name|b
argument_list|)
return|;
block|}
block|}
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|views ()
specifier|public
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|BranchResource
argument_list|>
argument_list|>
name|views
parameter_list|()
block|{
return|return
name|views
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|create (ProjectResource parent, IdString name)
specifier|public
name|CreateBranch
name|create
parameter_list|(
name|ProjectResource
name|parent
parameter_list|,
name|IdString
name|name
parameter_list|)
block|{
return|return
name|createBranchFactory
operator|.
name|create
argument_list|(
name|name
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

