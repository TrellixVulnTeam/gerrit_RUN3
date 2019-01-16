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
DECL|package|com.google.gerrit.gpg.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|gpg
operator|.
name|server
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
name|PublicKeyStore
operator|.
name|keyIdToString
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
name|externalids
operator|.
name|ExternalId
operator|.
name|SCHEME_GPGKEY
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|BaseEncoding
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
name|gpg
operator|.
name|PublicKeyStore
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
name|GerritPersonIdent
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
name|UserInitiated
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
name|AccountsUpdate
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
name|externalids
operator|.
name|ExternalId
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
name|externalids
operator|.
name|ExternalIds
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
name|Optional
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
name|bouncycastle
operator|.
name|openpgp
operator|.
name|PGPPublicKey
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
name|lib
operator|.
name|CommitBuilder
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
name|PersonIdent
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
name|RefUpdate
import|;
end_import

begin_class
DECL|class|DeleteGpgKey
specifier|public
class|class
name|DeleteGpgKey
implements|implements
name|RestModifyView
argument_list|<
name|GpgKey
argument_list|,
name|Input
argument_list|>
block|{
DECL|field|serverIdent
specifier|private
specifier|final
name|Provider
argument_list|<
name|PersonIdent
argument_list|>
name|serverIdent
decl_stmt|;
DECL|field|storeProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|PublicKeyStore
argument_list|>
name|storeProvider
decl_stmt|;
DECL|field|accountsUpdateProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|AccountsUpdate
argument_list|>
name|accountsUpdateProvider
decl_stmt|;
DECL|field|externalIds
specifier|private
specifier|final
name|ExternalIds
name|externalIds
decl_stmt|;
annotation|@
name|Inject
DECL|method|DeleteGpgKey ( @erritPersonIdent Provider<PersonIdent> serverIdent, Provider<PublicKeyStore> storeProvider, @UserInitiated Provider<AccountsUpdate> accountsUpdateProvider, ExternalIds externalIds)
name|DeleteGpgKey
parameter_list|(
annotation|@
name|GerritPersonIdent
name|Provider
argument_list|<
name|PersonIdent
argument_list|>
name|serverIdent
parameter_list|,
name|Provider
argument_list|<
name|PublicKeyStore
argument_list|>
name|storeProvider
parameter_list|,
annotation|@
name|UserInitiated
name|Provider
argument_list|<
name|AccountsUpdate
argument_list|>
name|accountsUpdateProvider
parameter_list|,
name|ExternalIds
name|externalIds
parameter_list|)
block|{
name|this
operator|.
name|serverIdent
operator|=
name|serverIdent
expr_stmt|;
name|this
operator|.
name|storeProvider
operator|=
name|storeProvider
expr_stmt|;
name|this
operator|.
name|accountsUpdateProvider
operator|=
name|accountsUpdateProvider
expr_stmt|;
name|this
operator|.
name|externalIds
operator|=
name|externalIds
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (GpgKey rsrc, Input input)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|apply
parameter_list|(
name|GpgKey
name|rsrc
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|PGPException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|PGPPublicKey
name|key
init|=
name|rsrc
operator|.
name|getKeyRing
argument_list|()
operator|.
name|getPublicKey
argument_list|()
decl_stmt|;
name|String
name|fingerprint
init|=
name|BaseEncoding
operator|.
name|base16
argument_list|()
operator|.
name|encode
argument_list|(
name|key
operator|.
name|getFingerprint
argument_list|()
argument_list|)
decl_stmt|;
name|Optional
argument_list|<
name|ExternalId
argument_list|>
name|extId
init|=
name|externalIds
operator|.
name|get
argument_list|(
name|ExternalId
operator|.
name|Key
operator|.
name|create
argument_list|(
name|SCHEME_GPGKEY
argument_list|,
name|fingerprint
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|extId
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|fingerprint
argument_list|)
throw|;
block|}
name|accountsUpdateProvider
operator|.
name|get
argument_list|()
operator|.
name|update
argument_list|(
literal|"Delete GPG Key via API"
argument_list|,
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|u
lambda|->
name|u
operator|.
name|deleteExternalId
argument_list|(
name|extId
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|PublicKeyStore
name|store
init|=
name|storeProvider
operator|.
name|get
argument_list|()
init|)
block|{
name|store
operator|.
name|remove
argument_list|(
name|rsrc
operator|.
name|getKeyRing
argument_list|()
operator|.
name|getPublicKey
argument_list|()
operator|.
name|getFingerprint
argument_list|()
argument_list|)
expr_stmt|;
name|CommitBuilder
name|cb
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
name|PersonIdent
name|committer
init|=
name|serverIdent
operator|.
name|get
argument_list|()
decl_stmt|;
name|cb
operator|.
name|setAuthor
argument_list|(
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|newCommitterIdent
argument_list|(
name|committer
operator|.
name|getWhen
argument_list|()
argument_list|,
name|committer
operator|.
name|getTimeZone
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setCommitter
argument_list|(
name|committer
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setMessage
argument_list|(
literal|"Delete public key "
operator|+
name|keyIdToString
argument_list|(
name|key
operator|.
name|getKeyID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|RefUpdate
operator|.
name|Result
name|saveResult
init|=
name|store
operator|.
name|save
argument_list|(
name|cb
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|saveResult
condition|)
block|{
case|case
name|NO_CHANGE
case|:
case|case
name|FAST_FORWARD
case|:
break|break;
case|case
name|FORCED
case|:
case|case
name|IO_FAILURE
case|:
case|case
name|LOCK_FAILURE
case|:
case|case
name|NEW
case|:
case|case
name|NOT_ATTEMPTED
case|:
case|case
name|REJECTED
case|:
case|case
name|REJECTED_CURRENT_BRANCH
case|:
case|case
name|RENAMED
case|:
case|case
name|REJECTED_MISSING_OBJECT
case|:
case|case
name|REJECTED_OTHER_REASON
case|:
default|default:
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Failed to delete public key: "
operator|+
name|saveResult
argument_list|)
throw|;
block|}
block|}
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

