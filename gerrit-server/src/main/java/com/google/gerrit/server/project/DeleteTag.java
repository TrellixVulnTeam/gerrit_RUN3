begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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

begin_class
annotation|@
name|Singleton
DECL|class|DeleteTag
specifier|public
class|class
name|DeleteTag
implements|implements
name|RestModifyView
argument_list|<
name|TagResource
argument_list|,
name|DeleteTag
operator|.
name|Input
argument_list|>
block|{
DECL|field|deleteRefFactory
specifier|private
specifier|final
name|DeleteRef
operator|.
name|Factory
name|deleteRefFactory
decl_stmt|;
DECL|class|Input
specifier|public
specifier|static
class|class
name|Input
block|{}
annotation|@
name|Inject
DECL|method|DeleteTag (DeleteRef.Factory deleteRefFactory)
name|DeleteTag
parameter_list|(
name|DeleteRef
operator|.
name|Factory
name|deleteRefFactory
parameter_list|)
block|{
name|this
operator|.
name|deleteRefFactory
operator|=
name|deleteRefFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (TagResource resource, Input input)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|TagResource
name|resource
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|OrmException
throws|,
name|RestApiException
throws|,
name|IOException
block|{
name|String
name|tag
init|=
name|RefUtil
operator|.
name|normalizeTagRef
argument_list|(
name|resource
operator|.
name|getTagInfo
argument_list|()
operator|.
name|ref
argument_list|)
decl_stmt|;
name|RefControl
name|refControl
init|=
name|resource
operator|.
name|getControl
argument_list|()
operator|.
name|controlForRef
argument_list|(
name|tag
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|refControl
operator|.
name|canDelete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Cannot delete tag"
argument_list|)
throw|;
block|}
name|deleteRefFactory
operator|.
name|create
argument_list|(
name|resource
argument_list|)
operator|.
name|ref
argument_list|(
name|tag
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
return|return
name|Response
operator|.
name|none
argument_list|()
return|;
block|}
block|}
end_class

end_unit

