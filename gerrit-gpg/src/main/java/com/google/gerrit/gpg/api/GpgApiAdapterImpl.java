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
name|api
operator|.
name|accounts
operator|.
name|GpgKeysInput
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
name|gpg
operator|.
name|GerritPushCertificateChecker
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
name|PushCertificateChecker
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
name|GpgException
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
name|api
operator|.
name|accounts
operator|.
name|GpgApiAdapter
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|openpgp
operator|.
name|PGPException
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
name|errors
operator|.
name|ConfigInvalidException
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
name|transport
operator|.
name|PushCertificate
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
name|transport
operator|.
name|PushCertificateParser
import|;
end_import

begin_class
DECL|class|GpgApiAdapterImpl
specifier|public
class|class
name|GpgApiAdapterImpl
implements|implements
name|GpgApiAdapter
block|{
DECL|field|postGpgKeys
specifier|private
specifier|final
name|Provider
argument_list|<
name|PostGpgKeys
argument_list|>
name|postGpgKeys
decl_stmt|;
DECL|field|gpgKeys
specifier|private
specifier|final
name|Provider
argument_list|<
name|GpgKeys
argument_list|>
name|gpgKeys
decl_stmt|;
DECL|field|gpgKeyApiFactory
specifier|private
specifier|final
name|GpgKeyApiImpl
operator|.
name|Factory
name|gpgKeyApiFactory
decl_stmt|;
DECL|field|pushCertCheckerFactory
specifier|private
specifier|final
name|GerritPushCertificateChecker
operator|.
name|Factory
name|pushCertCheckerFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|GpgApiAdapterImpl ( Provider<PostGpgKeys> postGpgKeys, Provider<GpgKeys> gpgKeys, GpgKeyApiImpl.Factory gpgKeyApiFactory, GerritPushCertificateChecker.Factory pushCertCheckerFactory)
name|GpgApiAdapterImpl
parameter_list|(
name|Provider
argument_list|<
name|PostGpgKeys
argument_list|>
name|postGpgKeys
parameter_list|,
name|Provider
argument_list|<
name|GpgKeys
argument_list|>
name|gpgKeys
parameter_list|,
name|GpgKeyApiImpl
operator|.
name|Factory
name|gpgKeyApiFactory
parameter_list|,
name|GerritPushCertificateChecker
operator|.
name|Factory
name|pushCertCheckerFactory
parameter_list|)
block|{
name|this
operator|.
name|postGpgKeys
operator|=
name|postGpgKeys
expr_stmt|;
name|this
operator|.
name|gpgKeys
operator|=
name|gpgKeys
expr_stmt|;
name|this
operator|.
name|gpgKeyApiFactory
operator|=
name|gpgKeyApiFactory
expr_stmt|;
name|this
operator|.
name|pushCertCheckerFactory
operator|=
name|pushCertCheckerFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isEnabled ()
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
literal|true
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
throws|throws
name|RestApiException
throws|,
name|GpgException
block|{
try|try
block|{
return|return
name|gpgKeys
operator|.
name|get
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|apply
argument_list|(
name|account
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|PGPException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|GpgException
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
throws|throws
name|RestApiException
throws|,
name|GpgException
block|{
name|GpgKeysInput
name|in
init|=
operator|new
name|GpgKeysInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|add
operator|=
name|add
expr_stmt|;
name|in
operator|.
name|delete
operator|=
name|delete
expr_stmt|;
try|try
block|{
return|return
name|postGpgKeys
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|account
argument_list|,
name|in
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PGPException
decl||
name|OrmException
decl||
name|IOException
decl||
name|ConfigInvalidException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|GpgException
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
throws|throws
name|RestApiException
throws|,
name|GpgException
block|{
try|try
block|{
return|return
name|gpgKeyApiFactory
operator|.
name|create
argument_list|(
name|gpgKeys
operator|.
name|get
argument_list|()
operator|.
name|parse
argument_list|(
name|account
argument_list|,
name|idStr
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PGPException
decl||
name|OrmException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|GpgException
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
throws|throws
name|GpgException
block|{
try|try
block|{
name|PushCertificate
name|cert
init|=
name|PushCertificateParser
operator|.
name|fromString
argument_list|(
name|certStr
argument_list|)
decl_stmt|;
name|PushCertificateChecker
operator|.
name|Result
name|result
init|=
name|pushCertCheckerFactory
operator|.
name|create
argument_list|(
name|expectedUser
argument_list|)
operator|.
name|setCheckNonce
argument_list|(
literal|false
argument_list|)
operator|.
name|check
argument_list|(
name|cert
argument_list|)
decl_stmt|;
name|PushCertificateInfo
name|info
init|=
operator|new
name|PushCertificateInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|certificate
operator|=
name|certStr
expr_stmt|;
name|info
operator|.
name|key
operator|=
name|GpgKeys
operator|.
name|toJson
argument_list|(
name|result
operator|.
name|getPublicKey
argument_list|()
argument_list|,
name|result
operator|.
name|getCheckResult
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|info
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
name|GpgException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

