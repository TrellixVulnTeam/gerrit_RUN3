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
DECL|package|com.google.gerrit.server.restapi.change
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
name|change
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
name|AcceptsPost
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
name|AuthException
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
name|NotImplementedException
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
name|ResourceConflictException
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
name|extensions
operator|.
name|restapi
operator|.
name|RestModifyView
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
name|change
operator|.
name|ChangeEditResource
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
name|change
operator|.
name|ChangeResource
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
name|edit
operator|.
name|ChangeEditModifier
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
name|git
operator|.
name|GitRepositoryManager
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
name|InvalidChangeOperationException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|server
operator|.
name|OrmException
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
name|Repository
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|RebaseChangeEdit
specifier|public
class|class
name|RebaseChangeEdit
implements|implements
name|ChildCollection
argument_list|<
name|ChangeResource
argument_list|,
name|ChangeEditResource
operator|.
name|Rebase
argument_list|>
implements|,
name|AcceptsPost
argument_list|<
name|ChangeResource
argument_list|>
block|{
DECL|field|views
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|ChangeEditResource
operator|.
name|Rebase
argument_list|>
argument_list|>
name|views
decl_stmt|;
DECL|field|rebase
specifier|private
specifier|final
name|Rebase
name|rebase
decl_stmt|;
annotation|@
name|Inject
DECL|method|RebaseChangeEdit (DynamicMap<RestView<ChangeEditResource.Rebase>> views, Rebase rebase)
name|RebaseChangeEdit
parameter_list|(
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|ChangeEditResource
operator|.
name|Rebase
argument_list|>
argument_list|>
name|views
parameter_list|,
name|Rebase
name|rebase
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
name|rebase
operator|=
name|rebase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|views ()
specifier|public
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|ChangeEditResource
operator|.
name|Rebase
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
name|Override
DECL|method|list ()
specifier|public
name|RestView
argument_list|<
name|ChangeResource
argument_list|>
name|list
parameter_list|()
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|parse (ChangeResource parent, IdString id)
specifier|public
name|ChangeEditResource
operator|.
name|Rebase
name|parse
parameter_list|(
name|ChangeResource
name|parent
parameter_list|,
name|IdString
name|id
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|post (ChangeResource parent)
specifier|public
name|Rebase
name|post
parameter_list|(
name|ChangeResource
name|parent
parameter_list|)
throws|throws
name|RestApiException
block|{
return|return
name|rebase
return|;
block|}
annotation|@
name|Singleton
DECL|class|Rebase
specifier|public
specifier|static
class|class
name|Rebase
implements|implements
name|RestModifyView
argument_list|<
name|ChangeResource
argument_list|,
name|Input
argument_list|>
block|{
DECL|field|repositoryManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repositoryManager
decl_stmt|;
DECL|field|editModifier
specifier|private
specifier|final
name|ChangeEditModifier
name|editModifier
decl_stmt|;
annotation|@
name|Inject
DECL|method|Rebase (GitRepositoryManager repositoryManager, ChangeEditModifier editModifier)
name|Rebase
parameter_list|(
name|GitRepositoryManager
name|repositoryManager
parameter_list|,
name|ChangeEditModifier
name|editModifier
parameter_list|)
block|{
name|this
operator|.
name|repositoryManager
operator|=
name|repositoryManager
expr_stmt|;
name|this
operator|.
name|editModifier
operator|=
name|editModifier
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeResource rsrc, Input in)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|ChangeResource
name|rsrc
parameter_list|,
name|Input
name|in
parameter_list|)
throws|throws
name|AuthException
throws|,
name|ResourceConflictException
throws|,
name|IOException
throws|,
name|OrmException
throws|,
name|PermissionBackendException
block|{
name|Project
operator|.
name|NameKey
name|project
init|=
name|rsrc
operator|.
name|getProject
argument_list|()
decl_stmt|;
try|try
init|(
name|Repository
name|repository
init|=
name|repositoryManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
init|)
block|{
name|editModifier
operator|.
name|rebaseEdit
argument_list|(
name|repository
argument_list|,
name|rsrc
operator|.
name|getNotes
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidChangeOperationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|Response
operator|.
name|none
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

