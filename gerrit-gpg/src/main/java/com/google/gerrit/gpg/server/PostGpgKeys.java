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
name|gpg
operator|.
name|PublicKeyStore
operator|.
name|keyToString
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
name|ExternalId
operator|.
name|SCHEME_GPGKEY
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
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
name|base
operator|.
name|Joiner
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
name|collect
operator|.
name|ImmutableList
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
name|collect
operator|.
name|ImmutableSet
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
name|collect
operator|.
name|Lists
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
name|collect
operator|.
name|Maps
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
name|collect
operator|.
name|Sets
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
name|common
operator|.
name|errors
operator|.
name|EmailException
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
name|CheckResult
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
name|Fingerprint
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
name|GerritPublicKeyChecker
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
name|PublicKeyChecker
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
name|gpg
operator|.
name|server
operator|.
name|PostGpgKeys
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
name|reviewdb
operator|.
name|client
operator|.
name|Account
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
name|CurrentUser
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
name|AccountState
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
name|ExternalIdsUpdate
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
name|mail
operator|.
name|send
operator|.
name|AddKeySender
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
name|query
operator|.
name|account
operator|.
name|InternalAccountQuery
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
name|ByteArrayInputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|bcpg
operator|.
name|ArmoredInputStream
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
name|bouncycastle
operator|.
name|openpgp
operator|.
name|PGPPublicKeyRing
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
name|PGPRuntimeOperationException
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
name|bc
operator|.
name|BcPGPObjectFactory
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|PostGpgKeys
specifier|public
class|class
name|PostGpgKeys
implements|implements
name|RestModifyView
argument_list|<
name|AccountResource
argument_list|,
name|Input
argument_list|>
block|{
DECL|class|Input
specifier|public
specifier|static
class|class
name|Input
block|{
DECL|field|add
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|add
decl_stmt|;
DECL|field|delete
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|delete
decl_stmt|;
block|}
DECL|field|log
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|serverIdent
specifier|private
specifier|final
name|Provider
argument_list|<
name|PersonIdent
argument_list|>
name|serverIdent
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
DECL|field|self
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
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
DECL|field|checkerFactory
specifier|private
specifier|final
name|GerritPublicKeyChecker
operator|.
name|Factory
name|checkerFactory
decl_stmt|;
DECL|field|addKeyFactory
specifier|private
specifier|final
name|AddKeySender
operator|.
name|Factory
name|addKeyFactory
decl_stmt|;
DECL|field|accountQueryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalAccountQuery
argument_list|>
name|accountQueryProvider
decl_stmt|;
DECL|field|externalIdsUpdateFactory
specifier|private
specifier|final
name|ExternalIdsUpdate
operator|.
name|User
name|externalIdsUpdateFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|PostGpgKeys ( @erritPersonIdent Provider<PersonIdent> serverIdent, Provider<ReviewDb> db, Provider<CurrentUser> self, Provider<PublicKeyStore> storeProvider, GerritPublicKeyChecker.Factory checkerFactory, AddKeySender.Factory addKeyFactory, Provider<InternalAccountQuery> accountQueryProvider, ExternalIdsUpdate.User externalIdsUpdateFactory)
name|PostGpgKeys
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
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
parameter_list|,
name|Provider
argument_list|<
name|PublicKeyStore
argument_list|>
name|storeProvider
parameter_list|,
name|GerritPublicKeyChecker
operator|.
name|Factory
name|checkerFactory
parameter_list|,
name|AddKeySender
operator|.
name|Factory
name|addKeyFactory
parameter_list|,
name|Provider
argument_list|<
name|InternalAccountQuery
argument_list|>
name|accountQueryProvider
parameter_list|,
name|ExternalIdsUpdate
operator|.
name|User
name|externalIdsUpdateFactory
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
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|self
operator|=
name|self
expr_stmt|;
name|this
operator|.
name|storeProvider
operator|=
name|storeProvider
expr_stmt|;
name|this
operator|.
name|checkerFactory
operator|=
name|checkerFactory
expr_stmt|;
name|this
operator|.
name|addKeyFactory
operator|=
name|addKeyFactory
expr_stmt|;
name|this
operator|.
name|accountQueryProvider
operator|=
name|accountQueryProvider
expr_stmt|;
name|this
operator|.
name|externalIdsUpdateFactory
operator|=
name|externalIdsUpdateFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (AccountResource rsrc, Input input)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|GpgKeyInfo
argument_list|>
name|apply
parameter_list|(
name|AccountResource
name|rsrc
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|ResourceNotFoundException
throws|,
name|BadRequestException
throws|,
name|ResourceConflictException
throws|,
name|PGPException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|GpgKeys
operator|.
name|checkVisible
argument_list|(
name|self
argument_list|,
name|rsrc
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|existingExtIds
init|=
name|GpgKeys
operator|.
name|getGpgExtIds
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
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
name|Set
argument_list|<
name|Fingerprint
argument_list|>
name|toRemove
init|=
name|readKeysToRemove
argument_list|(
name|input
argument_list|,
name|existingExtIds
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|PGPPublicKeyRing
argument_list|>
name|newKeys
init|=
name|readKeysToAdd
argument_list|(
name|input
argument_list|,
name|toRemove
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ExternalId
argument_list|>
name|newExtIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|existingExtIds
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|PGPPublicKeyRing
name|keyRing
range|:
name|newKeys
control|)
block|{
name|PGPPublicKey
name|key
init|=
name|keyRing
operator|.
name|getPublicKey
argument_list|()
decl_stmt|;
name|ExternalId
operator|.
name|Key
name|extIdKey
init|=
name|toExtIdKey
argument_list|(
name|key
operator|.
name|getFingerprint
argument_list|()
argument_list|)
decl_stmt|;
name|Account
name|account
init|=
name|getAccountByExternalId
argument_list|(
name|extIdKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|account
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"GPG key already associated with another account"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|newExtIds
operator|.
name|add
argument_list|(
name|ExternalId
operator|.
name|create
argument_list|(
name|extIdKey
argument_list|,
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|storeKeys
argument_list|(
name|rsrc
argument_list|,
name|newKeys
argument_list|,
name|toRemove
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ExternalId
operator|.
name|Key
argument_list|>
name|extIdKeysToRemove
init|=
name|toRemove
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|fp
lambda|->
name|toExtIdKey
argument_list|(
name|fp
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|externalIdsUpdateFactory
operator|.
name|create
argument_list|()
operator|.
name|replace
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|extIdKeysToRemove
argument_list|,
name|newExtIds
argument_list|)
expr_stmt|;
return|return
name|toJson
argument_list|(
name|newKeys
argument_list|,
name|toRemove
argument_list|,
name|store
argument_list|,
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|readKeysToRemove (Input input, Collection<ExternalId> existingExtIds)
specifier|private
name|Set
argument_list|<
name|Fingerprint
argument_list|>
name|readKeysToRemove
parameter_list|(
name|Input
name|input
parameter_list|,
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|existingExtIds
parameter_list|)
block|{
if|if
condition|(
name|input
operator|.
name|delete
operator|==
literal|null
operator|||
name|input
operator|.
name|delete
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
name|Set
argument_list|<
name|Fingerprint
argument_list|>
name|fingerprints
init|=
name|Sets
operator|.
name|newHashSetWithExpectedSize
argument_list|(
name|input
operator|.
name|delete
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|input
operator|.
name|delete
control|)
block|{
try|try
block|{
name|fingerprints
operator|.
name|add
argument_list|(
operator|new
name|Fingerprint
argument_list|(
name|GpgKeys
operator|.
name|parseFingerprint
argument_list|(
name|id
argument_list|,
name|existingExtIds
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceNotFoundException
name|e
parameter_list|)
block|{
comment|// Skip removal.
block|}
block|}
return|return
name|fingerprints
return|;
block|}
DECL|method|readKeysToAdd (Input input, Set<Fingerprint> toRemove)
specifier|private
name|List
argument_list|<
name|PGPPublicKeyRing
argument_list|>
name|readKeysToAdd
parameter_list|(
name|Input
name|input
parameter_list|,
name|Set
argument_list|<
name|Fingerprint
argument_list|>
name|toRemove
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|add
operator|==
literal|null
operator|||
name|input
operator|.
name|add
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
name|List
argument_list|<
name|PGPPublicKeyRing
argument_list|>
name|keyRings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|input
operator|.
name|add
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|armored
range|:
name|input
operator|.
name|add
control|)
block|{
try|try
init|(
name|InputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|armored
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
init|;
name|ArmoredInputStream
name|ain
operator|=
operator|new
name|ArmoredInputStream
argument_list|(
name|in
argument_list|)
init|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|Object
argument_list|>
name|objs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
operator|new
name|BcPGPObjectFactory
argument_list|(
name|ain
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|objs
operator|.
name|size
argument_list|()
operator|!=
literal|1
operator|||
operator|!
operator|(
name|objs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|PGPPublicKeyRing
operator|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Expected exactly one PUBLIC KEY BLOCK"
argument_list|)
throw|;
block|}
name|PGPPublicKeyRing
name|keyRing
init|=
operator|(
name|PGPPublicKeyRing
operator|)
name|objs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|toRemove
operator|.
name|contains
argument_list|(
operator|new
name|Fingerprint
argument_list|(
name|keyRing
operator|.
name|getPublicKey
argument_list|()
operator|.
name|getFingerprint
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Cannot both add and delete key: "
operator|+
name|keyToString
argument_list|(
name|keyRing
operator|.
name|getPublicKey
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|keyRings
operator|.
name|add
argument_list|(
name|keyRing
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PGPRuntimeOperationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Failed to parse GPG keys"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|keyRings
return|;
block|}
DECL|method|storeKeys ( AccountResource rsrc, List<PGPPublicKeyRing> keyRings, Set<Fingerprint> toRemove)
specifier|private
name|void
name|storeKeys
parameter_list|(
name|AccountResource
name|rsrc
parameter_list|,
name|List
argument_list|<
name|PGPPublicKeyRing
argument_list|>
name|keyRings
parameter_list|,
name|Set
argument_list|<
name|Fingerprint
argument_list|>
name|toRemove
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|ResourceConflictException
throws|,
name|PGPException
throws|,
name|IOException
block|{
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
name|List
argument_list|<
name|String
argument_list|>
name|addedKeys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|PGPPublicKeyRing
name|keyRing
range|:
name|keyRings
control|)
block|{
name|PGPPublicKey
name|key
init|=
name|keyRing
operator|.
name|getPublicKey
argument_list|()
decl_stmt|;
comment|// Don't check web of trust; admins can fill in certifications later.
name|CheckResult
name|result
init|=
name|checkerFactory
operator|.
name|create
argument_list|(
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|,
name|store
argument_list|)
operator|.
name|disableTrust
argument_list|()
operator|.
name|check
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|result
operator|.
name|isOk
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Problems with public key %s:\n%s"
argument_list|,
name|keyToString
argument_list|(
name|key
argument_list|)
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|join
argument_list|(
name|result
operator|.
name|getProblems
argument_list|()
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
name|addedKeys
operator|.
name|add
argument_list|(
name|PublicKeyStore
operator|.
name|keyToString
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|add
argument_list|(
name|keyRing
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Fingerprint
name|fp
range|:
name|toRemove
control|)
block|{
name|store
operator|.
name|remove
argument_list|(
name|fp
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|NEW
case|:
case|case
name|FAST_FORWARD
case|:
case|case
name|FORCED
case|:
try|try
block|{
name|addKeyFactory
operator|.
name|create
argument_list|(
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|,
name|addedKeys
argument_list|)
operator|.
name|send
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EmailException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot send GPG key added message to "
operator|+
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|getAccount
argument_list|()
operator|.
name|getPreferredEmail
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|NO_CHANGE
case|:
break|break;
case|case
name|IO_FAILURE
case|:
case|case
name|LOCK_FAILURE
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
default|default:
comment|// TODO(dborowitz): Backoff and retry on LOCK_FAILURE.
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Failed to save public keys: "
operator|+
name|saveResult
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|toExtIdKey (byte[] fp)
specifier|private
name|ExternalId
operator|.
name|Key
name|toExtIdKey
parameter_list|(
name|byte
index|[]
name|fp
parameter_list|)
block|{
return|return
name|ExternalId
operator|.
name|Key
operator|.
name|create
argument_list|(
name|SCHEME_GPGKEY
argument_list|,
name|BaseEncoding
operator|.
name|base16
argument_list|()
operator|.
name|encode
argument_list|(
name|fp
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getAccountByExternalId (ExternalId.Key extIdKey)
specifier|private
name|Account
name|getAccountByExternalId
parameter_list|(
name|ExternalId
operator|.
name|Key
name|extIdKey
parameter_list|)
throws|throws
name|OrmException
block|{
name|List
argument_list|<
name|AccountState
argument_list|>
name|accountStates
init|=
name|accountQueryProvider
operator|.
name|get
argument_list|()
operator|.
name|byExternalId
argument_list|(
name|extIdKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|accountStates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|accountStates
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|StringBuilder
name|msg
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|"GPG key "
argument_list|)
operator|.
name|append
argument_list|(
name|extIdKey
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" associated with multiple accounts: "
argument_list|)
expr_stmt|;
name|Joiner
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|appendTo
argument_list|(
name|msg
argument_list|,
name|Lists
operator|.
name|transform
argument_list|(
name|accountStates
argument_list|,
name|AccountState
operator|.
name|ACCOUNT_ID_FUNCTION
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|msg
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|accountStates
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAccount
argument_list|()
return|;
block|}
DECL|method|toJson ( Collection<PGPPublicKeyRing> keys, Set<Fingerprint> deleted, PublicKeyStore store, IdentifiedUser user)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|GpgKeyInfo
argument_list|>
name|toJson
parameter_list|(
name|Collection
argument_list|<
name|PGPPublicKeyRing
argument_list|>
name|keys
parameter_list|,
name|Set
argument_list|<
name|Fingerprint
argument_list|>
name|deleted
parameter_list|,
name|PublicKeyStore
name|store
parameter_list|,
name|IdentifiedUser
name|user
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Unlike when storing keys, include web-of-trust checks when producing
comment|// result JSON, so the user at least knows of any issues.
name|PublicKeyChecker
name|checker
init|=
name|checkerFactory
operator|.
name|create
argument_list|(
name|user
argument_list|,
name|store
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|GpgKeyInfo
argument_list|>
name|infos
init|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
name|keys
operator|.
name|size
argument_list|()
operator|+
name|deleted
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|PGPPublicKeyRing
name|keyRing
range|:
name|keys
control|)
block|{
name|PGPPublicKey
name|key
init|=
name|keyRing
operator|.
name|getPublicKey
argument_list|()
decl_stmt|;
name|CheckResult
name|result
init|=
name|checker
operator|.
name|check
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|GpgKeyInfo
name|info
init|=
name|GpgKeys
operator|.
name|toJson
argument_list|(
name|key
argument_list|,
name|result
argument_list|)
decl_stmt|;
name|infos
operator|.
name|put
argument_list|(
name|info
operator|.
name|id
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|info
operator|.
name|id
operator|=
literal|null
expr_stmt|;
block|}
for|for
control|(
name|Fingerprint
name|fp
range|:
name|deleted
control|)
block|{
name|infos
operator|.
name|put
argument_list|(
name|keyIdToString
argument_list|(
name|fp
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
operator|new
name|GpgKeyInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|infos
return|;
block|}
block|}
end_class

end_unit

