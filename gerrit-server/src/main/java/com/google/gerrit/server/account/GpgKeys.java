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
DECL|package|com.google.gerrit.server.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|account
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
name|reviewdb
operator|.
name|client
operator|.
name|AccountExternalId
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|CharMatcher
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
name|Predicate
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
name|FluentIterable
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
name|RestReadView
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
name|client
operator|.
name|AccountExternalId
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
name|account
operator|.
name|AccountResource
operator|.
name|GpgKey
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
name|util
operator|.
name|BouncyCastleUtil
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
name|org
operator|.
name|bouncycastle
operator|.
name|bcpg
operator|.
name|ArmoredOutputStream
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
annotation|@
name|Singleton
DECL|class|GpgKeys
specifier|public
class|class
name|GpgKeys
implements|implements
name|ChildCollection
argument_list|<
name|AccountResource
argument_list|,
name|AccountResource
operator|.
name|GpgKey
argument_list|>
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|GpgKeys
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MIME_TYPE
specifier|public
specifier|static
name|String
name|MIME_TYPE
init|=
literal|"application/pgp-keys"
decl_stmt|;
DECL|field|views
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|AccountResource
operator|.
name|GpgKey
argument_list|>
argument_list|>
name|views
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
DECL|field|storeProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|PublicKeyStore
argument_list|>
name|storeProvider
decl_stmt|;
annotation|@
name|Inject
DECL|method|GpgKeys (DynamicMap<RestView<AccountResource.GpgKey>> views, Provider<ReviewDb> db, Provider<PublicKeyStore> storeProvider)
name|GpgKeys
parameter_list|(
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|AccountResource
operator|.
name|GpgKey
argument_list|>
argument_list|>
name|views
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|Provider
argument_list|<
name|PublicKeyStore
argument_list|>
name|storeProvider
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
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|storeProvider
operator|=
name|storeProvider
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|list ()
specifier|public
name|ListGpgKeys
name|list
parameter_list|()
throws|throws
name|ResourceNotFoundException
throws|,
name|AuthException
block|{
name|checkEnabled
argument_list|()
expr_stmt|;
return|return
operator|new
name|ListGpgKeys
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parse (AccountResource parent, IdString id)
specifier|public
name|GpgKey
name|parse
parameter_list|(
name|AccountResource
name|parent
parameter_list|,
name|IdString
name|id
parameter_list|)
throws|throws
name|ResourceNotFoundException
throws|,
name|PGPException
throws|,
name|OrmException
throws|,
name|IOException
block|{
name|checkEnabled
argument_list|()
expr_stmt|;
name|String
name|str
init|=
name|CharMatcher
operator|.
name|WHITESPACE
operator|.
name|removeFrom
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|toUpperCase
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|str
operator|.
name|length
argument_list|()
operator|!=
literal|8
operator|&&
name|str
operator|.
name|length
argument_list|()
operator|!=
literal|40
operator|)
operator|||
operator|!
name|CharMatcher
operator|.
name|anyOf
argument_list|(
literal|"0123456789ABCDEF"
argument_list|)
operator|.
name|matchesAllOf
argument_list|(
name|str
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
block|}
name|byte
index|[]
name|fp
init|=
literal|null
decl_stmt|;
for|for
control|(
name|AccountExternalId
name|extId
range|:
name|getGpgExtIds
argument_list|(
name|parent
argument_list|)
control|)
block|{
name|String
name|fpStr
init|=
name|extId
operator|.
name|getSchemeRest
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|fpStr
operator|.
name|endsWith
argument_list|(
name|str
argument_list|)
condition|)
block|{
continue|continue;
block|}
elseif|else
if|if
condition|(
name|fp
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
literal|"Multiple keys found for "
operator|+
name|id
argument_list|)
throw|;
block|}
name|fp
operator|=
name|BaseEncoding
operator|.
name|base16
argument_list|()
operator|.
name|decode
argument_list|(
name|fpStr
argument_list|)
expr_stmt|;
if|if
condition|(
name|str
operator|.
name|length
argument_list|()
operator|==
literal|40
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|fp
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
block|}
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
name|long
name|keyId
init|=
name|keyId
argument_list|(
name|fp
argument_list|)
decl_stmt|;
for|for
control|(
name|PGPPublicKeyRing
name|keyRing
range|:
name|store
operator|.
name|get
argument_list|(
name|keyId
argument_list|)
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
if|if
condition|(
name|Arrays
operator|.
name|equals
argument_list|(
name|key
operator|.
name|getFingerprint
argument_list|()
argument_list|,
name|fp
argument_list|)
condition|)
block|{
return|return
operator|new
name|AccountResource
operator|.
name|GpgKey
argument_list|(
name|parent
operator|.
name|getUser
argument_list|()
argument_list|,
name|keyRing
argument_list|)
return|;
block|}
block|}
block|}
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|views ()
specifier|public
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|GpgKey
argument_list|>
argument_list|>
name|views
parameter_list|()
block|{
return|return
name|views
return|;
block|}
DECL|class|ListGpgKeys
specifier|public
class|class
name|ListGpgKeys
implements|implements
name|RestReadView
argument_list|<
name|AccountResource
argument_list|>
block|{
annotation|@
name|Override
DECL|method|apply (AccountResource rsrc)
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
parameter_list|)
throws|throws
name|OrmException
throws|,
name|PGPException
throws|,
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|GpgKeyInfo
argument_list|>
name|keys
init|=
operator|new
name|HashMap
argument_list|<>
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
for|for
control|(
name|AccountExternalId
name|extId
range|:
name|getGpgExtIds
argument_list|(
name|rsrc
argument_list|)
control|)
block|{
name|String
name|fpStr
init|=
name|extId
operator|.
name|getSchemeRest
argument_list|()
decl_stmt|;
name|byte
index|[]
name|fp
init|=
name|BaseEncoding
operator|.
name|base16
argument_list|()
operator|.
name|decode
argument_list|(
name|fpStr
argument_list|)
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|PGPPublicKeyRing
name|keyRing
range|:
name|store
operator|.
name|get
argument_list|(
name|keyId
argument_list|(
name|fp
argument_list|)
argument_list|)
control|)
block|{
if|if
condition|(
name|Arrays
operator|.
name|equals
argument_list|(
name|keyRing
operator|.
name|getPublicKey
argument_list|()
operator|.
name|getFingerprint
argument_list|()
argument_list|,
name|fp
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
name|GpgKeyInfo
name|info
init|=
name|toJson
argument_list|(
name|keyRing
argument_list|)
decl_stmt|;
name|keys
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
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No public key stored for fingerprint {}"
argument_list|,
name|fp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|keys
return|;
block|}
block|}
annotation|@
name|Singleton
DECL|class|Get
specifier|public
specifier|static
class|class
name|Get
implements|implements
name|RestReadView
argument_list|<
name|AccountResource
operator|.
name|GpgKey
argument_list|>
block|{
annotation|@
name|Override
DECL|method|apply (GpgKey rsrc)
specifier|public
name|GpgKeyInfo
name|apply
parameter_list|(
name|GpgKey
name|rsrc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|toJson
argument_list|(
name|rsrc
operator|.
name|getKeyRing
argument_list|()
argument_list|)
return|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getGpgExtIds (ReviewDb db, Account.Id accountId)
specifier|public
specifier|static
name|Iterable
argument_list|<
name|AccountExternalId
argument_list|>
name|getGpgExtIds
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|FluentIterable
operator|.
name|from
argument_list|(
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|byAccount
argument_list|(
name|accountId
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
operator|new
name|Predicate
argument_list|<
name|AccountExternalId
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|AccountExternalId
name|in
parameter_list|)
block|{
return|return
name|in
operator|.
name|isScheme
argument_list|(
name|SCHEME_GPGKEY
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|getGpgExtIds (AccountResource rsrc)
specifier|private
name|Iterable
argument_list|<
name|AccountExternalId
argument_list|>
name|getGpgExtIds
parameter_list|(
name|AccountResource
name|rsrc
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
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
return|;
block|}
DECL|method|keyId (byte[] fp)
specifier|private
specifier|static
name|long
name|keyId
parameter_list|(
name|byte
index|[]
name|fp
parameter_list|)
block|{
return|return
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|fp
argument_list|)
operator|.
name|getLong
argument_list|(
name|fp
operator|.
name|length
operator|-
literal|8
argument_list|)
return|;
block|}
DECL|method|checkEnabled ()
specifier|static
name|void
name|checkEnabled
parameter_list|()
throws|throws
name|ResourceNotFoundException
block|{
if|if
condition|(
operator|!
name|BouncyCastleUtil
operator|.
name|havePGP
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
literal|"GPG not enabled"
argument_list|)
throw|;
block|}
block|}
DECL|method|toJson (PGPPublicKeyRing keyRing)
specifier|static
name|GpgKeyInfo
name|toJson
parameter_list|(
name|PGPPublicKeyRing
name|keyRing
parameter_list|)
throws|throws
name|IOException
block|{
name|PGPPublicKey
name|key
init|=
name|keyRing
operator|.
name|getPublicKey
argument_list|()
decl_stmt|;
name|GpgKeyInfo
name|info
init|=
operator|new
name|GpgKeyInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|id
operator|=
name|PublicKeyStore
operator|.
name|keyIdToString
argument_list|(
name|key
operator|.
name|getKeyID
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|fingerprint
operator|=
name|PublicKeyStore
operator|.
name|fingerprintToString
argument_list|(
name|key
operator|.
name|getFingerprint
argument_list|()
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Iterator
argument_list|<
name|String
argument_list|>
name|userIds
init|=
name|key
operator|.
name|getUserIDs
argument_list|()
decl_stmt|;
name|info
operator|.
name|userIds
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|userIds
argument_list|)
expr_stmt|;
try|try
init|(
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|4096
argument_list|)
init|;
name|ArmoredOutputStream
name|aout
operator|=
operator|new
name|ArmoredOutputStream
argument_list|(
name|out
argument_list|)
init|)
block|{
comment|// This is not exactly the key stored in the store, but is equivalent. In
comment|// particular, it will have a Bouncy Castle version string. The armored
comment|// stream reader in PublicKeyStore doesn't give us an easy way to extract
comment|// the original ASCII armor.
name|key
operator|.
name|encode
argument_list|(
name|aout
argument_list|)
expr_stmt|;
name|info
operator|.
name|key
operator|=
operator|new
name|String
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
expr_stmt|;
block|}
return|return
name|info
return|;
block|}
block|}
end_class

end_unit

