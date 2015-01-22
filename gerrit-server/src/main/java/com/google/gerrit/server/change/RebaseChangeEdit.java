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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|common
operator|.
name|base
operator|.
name|Optional
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
name|PatchSet
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
name|server
operator|.
name|ReviewDb
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
name|ChangeEdit
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
name|edit
operator|.
name|ChangeEditUtil
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
argument_list|>
implements|,
name|AcceptsPost
argument_list|<
name|ChangeResource
argument_list|>
block|{
DECL|field|rebase
specifier|private
specifier|final
name|Rebase
name|rebase
decl_stmt|;
annotation|@
name|Inject
DECL|method|RebaseChangeEdit (Rebase rebase)
name|RebaseChangeEdit
parameter_list|(
name|Rebase
name|rebase
parameter_list|)
block|{
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
argument_list|>
argument_list|>
name|views
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"not yet implemented"
argument_list|)
throw|;
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
name|IllegalStateException
argument_list|(
literal|"not yet implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|parse (ChangeResource parent, IdString id)
specifier|public
name|ChangeEditResource
name|parse
parameter_list|(
name|ChangeResource
name|parent
parameter_list|,
name|IdString
name|id
parameter_list|)
throws|throws
name|ResourceNotFoundException
throws|,
name|Exception
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"not yet implemented"
argument_list|)
throw|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
name|PublishDraftPatchSet
operator|.
name|Input
argument_list|>
block|{
DECL|class|Input
specifier|public
specifier|static
class|class
name|Input
block|{     }
DECL|field|editModifier
specifier|private
specifier|final
name|ChangeEditModifier
name|editModifier
decl_stmt|;
DECL|field|editUtil
specifier|private
specifier|final
name|ChangeEditUtil
name|editUtil
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
annotation|@
name|Inject
DECL|method|Rebase (ChangeEditModifier editModifier, ChangeEditUtil editUtil, Provider<ReviewDb> db)
name|Rebase
parameter_list|(
name|ChangeEditModifier
name|editModifier
parameter_list|,
name|ChangeEditUtil
name|editUtil
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
block|{
name|this
operator|.
name|editModifier
operator|=
name|editModifier
expr_stmt|;
name|this
operator|.
name|editUtil
operator|=
name|editUtil
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeResource rsrc, PublishDraftPatchSet.Input in)
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
name|PublishDraftPatchSet
operator|.
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
name|InvalidChangeOperationException
throws|,
name|OrmException
block|{
name|Optional
argument_list|<
name|ChangeEdit
argument_list|>
name|edit
init|=
name|editUtil
operator|.
name|byChange
argument_list|(
name|rsrc
operator|.
name|getChange
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|edit
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"no edit exists for change %s"
argument_list|,
name|rsrc
operator|.
name|getChange
argument_list|()
operator|.
name|getChangeId
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|PatchSet
name|current
init|=
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|rsrc
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|current
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|edit
operator|.
name|get
argument_list|()
operator|.
name|getBasePatchSet
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"edit for change %s is already on latest patch set: %s"
argument_list|,
name|rsrc
operator|.
name|getChange
argument_list|()
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|current
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|editModifier
operator|.
name|rebaseEdit
argument_list|(
name|edit
operator|.
name|get
argument_list|()
argument_list|,
name|current
argument_list|)
expr_stmt|;
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

