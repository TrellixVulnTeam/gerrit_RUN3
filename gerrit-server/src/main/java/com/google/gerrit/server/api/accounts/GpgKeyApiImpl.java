begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.api.accounts
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
name|accounts
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
name|accounts
operator|.
name|GpgKeyApi
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
name|GpgKeyInfo
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
name|account
operator|.
name|AccountResource
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
name|account
operator|.
name|GpgKeys
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|AssistedInject
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
DECL|class|GpgKeyApiImpl
class|class
name|GpgKeyApiImpl
implements|implements
name|GpgKeyApi
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (AccountResource.GpgKey rsrc)
name|GpgKeyApiImpl
name|create
parameter_list|(
name|AccountResource
operator|.
name|GpgKey
name|rsrc
parameter_list|)
function_decl|;
block|}
DECL|field|get
specifier|private
specifier|final
name|GpgKeys
operator|.
name|Get
name|get
decl_stmt|;
DECL|field|rsrc
specifier|private
specifier|final
name|AccountResource
operator|.
name|GpgKey
name|rsrc
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|GpgKeyApiImpl ( GpgKeys.Get get, @Assisted AccountResource.GpgKey rsrc)
name|GpgKeyApiImpl
parameter_list|(
name|GpgKeys
operator|.
name|Get
name|get
parameter_list|,
annotation|@
name|Assisted
name|AccountResource
operator|.
name|GpgKey
name|rsrc
parameter_list|)
block|{
name|this
operator|.
name|get
operator|=
name|get
expr_stmt|;
name|this
operator|.
name|rsrc
operator|=
name|rsrc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|GpgKeyInfo
name|get
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|get
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot get GPG key"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

