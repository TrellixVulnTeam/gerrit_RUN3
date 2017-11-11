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
DECL|package|com.google.gerrit.gpg.api
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|gpg
operator|.
name|api
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
name|gpg
operator|.
name|server
operator|.
name|GpgKey
operator|.
name|GPG_KEY_KIND
import|;
end_import

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
name|account
operator|.
name|AccountResource
operator|.
name|ACCOUNT_KIND
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
name|common
operator|.
name|PushCertificateInfo
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
name|RestApiModule
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
name|gpg
operator|.
name|server
operator|.
name|DeleteGpgKey
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
name|gpg
operator|.
name|server
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
name|gerrit
operator|.
name|gpg
operator|.
name|server
operator|.
name|PostGpgKeys
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
name|IdentifiedUser
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
name|GpgApiAdapter
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|GpgApiModule
specifier|public
class|class
name|GpgApiModule
extends|extends
name|RestApiModule
block|{
DECL|field|enabled
specifier|private
specifier|final
name|boolean
name|enabled
decl_stmt|;
DECL|method|GpgApiModule (boolean enabled)
specifier|public
name|GpgApiModule
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
name|bind
argument_list|(
name|GpgApiAdapter
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|NoGpgApi
operator|.
name|class
argument_list|)
expr_stmt|;
return|return;
block|}
name|bind
argument_list|(
name|GpgApiAdapter
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|GpgApiAdapterImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
argument_list|(
name|GpgKeyApiImpl
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|DynamicMap
operator|.
name|mapOf
argument_list|(
name|binder
argument_list|()
argument_list|,
name|GPG_KEY_KIND
argument_list|)
expr_stmt|;
name|child
argument_list|(
name|ACCOUNT_KIND
argument_list|,
literal|"gpgkeys"
argument_list|)
operator|.
name|to
argument_list|(
name|GpgKeys
operator|.
name|class
argument_list|)
expr_stmt|;
name|post
argument_list|(
name|ACCOUNT_KIND
argument_list|,
literal|"gpgkeys"
argument_list|)
operator|.
name|to
argument_list|(
name|PostGpgKeys
operator|.
name|class
argument_list|)
expr_stmt|;
name|get
argument_list|(
name|GPG_KEY_KIND
argument_list|)
operator|.
name|to
argument_list|(
name|GpgKeys
operator|.
name|Get
operator|.
name|class
argument_list|)
expr_stmt|;
name|delete
argument_list|(
name|GPG_KEY_KIND
argument_list|)
operator|.
name|to
argument_list|(
name|DeleteGpgKey
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|class|NoGpgApi
specifier|private
specifier|static
class|class
name|NoGpgApi
implements|implements
name|GpgApiAdapter
block|{
DECL|field|MSG
specifier|private
specifier|static
specifier|final
name|String
name|MSG
init|=
literal|"GPG key APIs disabled"
decl_stmt|;
annotation|@
name|Override
DECL|method|isEnabled ()
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|listGpgKeys (AccountResource account)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|GpgKeyInfo
argument_list|>
name|listGpgKeys
parameter_list|(
name|AccountResource
name|account
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|putGpgKeys ( AccountResource account, List<String> add, List<String> delete)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|GpgKeyInfo
argument_list|>
name|putGpgKeys
parameter_list|(
name|AccountResource
name|account
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|add
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|delete
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|gpgKey (AccountResource account, IdString idStr)
specifier|public
name|GpgKeyApi
name|gpgKey
parameter_list|(
name|AccountResource
name|account
parameter_list|,
name|IdString
name|idStr
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|checkPushCertificate (String certStr, IdentifiedUser expectedUser)
specifier|public
name|PushCertificateInfo
name|checkPushCertificate
parameter_list|(
name|String
name|certStr
parameter_list|,
name|IdentifiedUser
name|expectedUser
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

