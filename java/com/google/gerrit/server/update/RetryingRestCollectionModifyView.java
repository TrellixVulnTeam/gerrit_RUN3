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
DECL|package|com.google.gerrit.server.update
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|update
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
name|BadRequestException
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
name|RestCollectionModifyView
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
name|RestResource
import|;
end_import

begin_class
DECL|class|RetryingRestCollectionModifyView
specifier|public
specifier|abstract
class|class
name|RetryingRestCollectionModifyView
parameter_list|<
name|P
extends|extends
name|RestResource
parameter_list|,
name|C
extends|extends
name|RestResource
parameter_list|,
name|I
parameter_list|,
name|O
parameter_list|>
implements|implements
name|RestCollectionModifyView
argument_list|<
name|P
argument_list|,
name|C
argument_list|,
name|I
argument_list|>
block|{
DECL|field|retryHelper
specifier|private
specifier|final
name|RetryHelper
name|retryHelper
decl_stmt|;
DECL|method|RetryingRestCollectionModifyView (RetryHelper retryHelper)
specifier|protected
name|RetryingRestCollectionModifyView
parameter_list|(
name|RetryHelper
name|retryHelper
parameter_list|)
block|{
name|this
operator|.
name|retryHelper
operator|=
name|retryHelper
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (P parentResource, I input)
specifier|public
specifier|final
name|O
name|apply
parameter_list|(
name|P
name|parentResource
parameter_list|,
name|I
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|BadRequestException
throws|,
name|ResourceConflictException
throws|,
name|Exception
block|{
return|return
name|retryHelper
operator|.
name|execute
argument_list|(
parameter_list|(
name|updateFactory
parameter_list|)
lambda|->
name|applyImpl
argument_list|(
name|updateFactory
argument_list|,
name|parentResource
argument_list|,
name|input
argument_list|)
argument_list|)
return|;
block|}
DECL|method|applyImpl (BatchUpdate.Factory updateFactory, P parentResource, I input)
specifier|protected
specifier|abstract
name|O
name|applyImpl
parameter_list|(
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|P
name|parentResource
parameter_list|,
name|I
name|input
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

