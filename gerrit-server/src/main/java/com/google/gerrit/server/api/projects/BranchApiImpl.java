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
DECL|package|com.google.gerrit.server.api.projects
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|api
operator|.
name|projects
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
name|api
operator|.
name|ApiUtil
operator|.
name|asRestApiException
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
name|BranchApi
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
name|api
operator|.
name|projects
operator|.
name|BranchInput
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
name|ReflogEntryInfo
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
name|common
operator|.
name|Input
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
name|BinaryResult
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
name|RestApiException
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
name|gerrit
operator|.
name|server
operator|.
name|project
operator|.
name|BranchResource
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
name|BranchesCollection
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
name|CreateBranch
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
name|DeleteBranch
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
name|FileResource
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
name|FilesCollection
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
name|GetBranch
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
name|GetContent
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
name|GetReflog
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
name|assistedinject
operator|.
name|Assisted
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
DECL|class|BranchApiImpl
specifier|public
class|class
name|BranchApiImpl
implements|implements
name|BranchApi
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (ProjectResource project, String ref)
name|BranchApiImpl
name|create
parameter_list|(
name|ProjectResource
name|project
parameter_list|,
name|String
name|ref
parameter_list|)
function_decl|;
block|}
DECL|field|branches
specifier|private
specifier|final
name|BranchesCollection
name|branches
decl_stmt|;
DECL|field|createBranchFactory
specifier|private
specifier|final
name|CreateBranch
operator|.
name|Factory
name|createBranchFactory
decl_stmt|;
DECL|field|deleteBranch
specifier|private
specifier|final
name|DeleteBranch
name|deleteBranch
decl_stmt|;
DECL|field|filesCollection
specifier|private
specifier|final
name|FilesCollection
name|filesCollection
decl_stmt|;
DECL|field|getBranch
specifier|private
specifier|final
name|GetBranch
name|getBranch
decl_stmt|;
DECL|field|getContent
specifier|private
specifier|final
name|GetContent
name|getContent
decl_stmt|;
DECL|field|getReflog
specifier|private
specifier|final
name|GetReflog
name|getReflog
decl_stmt|;
DECL|field|ref
specifier|private
specifier|final
name|String
name|ref
decl_stmt|;
DECL|field|project
specifier|private
specifier|final
name|ProjectResource
name|project
decl_stmt|;
annotation|@
name|Inject
DECL|method|BranchApiImpl ( BranchesCollection branches, CreateBranch.Factory createBranchFactory, DeleteBranch deleteBranch, FilesCollection filesCollection, GetBranch getBranch, GetContent getContent, GetReflog getReflog, @Assisted ProjectResource project, @Assisted String ref)
name|BranchApiImpl
parameter_list|(
name|BranchesCollection
name|branches
parameter_list|,
name|CreateBranch
operator|.
name|Factory
name|createBranchFactory
parameter_list|,
name|DeleteBranch
name|deleteBranch
parameter_list|,
name|FilesCollection
name|filesCollection
parameter_list|,
name|GetBranch
name|getBranch
parameter_list|,
name|GetContent
name|getContent
parameter_list|,
name|GetReflog
name|getReflog
parameter_list|,
annotation|@
name|Assisted
name|ProjectResource
name|project
parameter_list|,
annotation|@
name|Assisted
name|String
name|ref
parameter_list|)
block|{
name|this
operator|.
name|branches
operator|=
name|branches
expr_stmt|;
name|this
operator|.
name|createBranchFactory
operator|=
name|createBranchFactory
expr_stmt|;
name|this
operator|.
name|deleteBranch
operator|=
name|deleteBranch
expr_stmt|;
name|this
operator|.
name|filesCollection
operator|=
name|filesCollection
expr_stmt|;
name|this
operator|.
name|getBranch
operator|=
name|getBranch
expr_stmt|;
name|this
operator|.
name|getContent
operator|=
name|getContent
expr_stmt|;
name|this
operator|.
name|getReflog
operator|=
name|getReflog
expr_stmt|;
name|this
operator|.
name|project
operator|=
name|project
expr_stmt|;
name|this
operator|.
name|ref
operator|=
name|ref
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create (BranchInput input)
specifier|public
name|BranchApi
name|create
parameter_list|(
name|BranchInput
name|input
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|createBranchFactory
operator|.
name|create
argument_list|(
name|ref
argument_list|)
operator|.
name|apply
argument_list|(
name|project
argument_list|,
name|input
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot create branch"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|BranchInfo
name|get
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|getBranch
operator|.
name|apply
argument_list|(
name|resource
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot read branch"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|delete ()
specifier|public
name|void
name|delete
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
name|deleteBranch
operator|.
name|apply
argument_list|(
name|resource
argument_list|()
argument_list|,
operator|new
name|Input
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot delete branch"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|file (String path)
specifier|public
name|BinaryResult
name|file
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|FileResource
name|resource
init|=
name|filesCollection
operator|.
name|parse
argument_list|(
name|resource
argument_list|()
argument_list|,
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|getContent
operator|.
name|apply
argument_list|(
name|resource
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot retrieve file"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|reflog ()
specifier|public
name|List
argument_list|<
name|ReflogEntryInfo
argument_list|>
name|reflog
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|getReflog
operator|.
name|apply
argument_list|(
name|resource
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|PermissionBackendException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot retrieve reflog"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|resource ()
specifier|private
name|BranchResource
name|resource
parameter_list|()
throws|throws
name|RestApiException
throws|,
name|IOException
throws|,
name|PermissionBackendException
block|{
return|return
name|branches
operator|.
name|parse
argument_list|(
name|project
argument_list|,
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|ref
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

