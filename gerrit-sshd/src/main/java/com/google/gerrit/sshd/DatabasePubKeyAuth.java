begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.sshd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
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
name|reviewdb
operator|.
name|client
operator|.
name|AccountSshKey
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
name|AccessPath
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
name|PeerDaemonUser
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
name|config
operator|.
name|GerritServerConfig
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
name|config
operator|.
name|SitePaths
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
name|sshd
operator|.
name|SshScope
operator|.
name|Context
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
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|mina
operator|.
name|core
operator|.
name|future
operator|.
name|IoFuture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|mina
operator|.
name|core
operator|.
name|future
operator|.
name|IoFutureListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|common
operator|.
name|KeyPairProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|common
operator|.
name|SshException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|common
operator|.
name|util
operator|.
name|Buffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|PublickeyAuthenticator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|session
operator|.
name|ServerSession
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
name|Config
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
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
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
name|net
operator|.
name|SocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyPair
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PublicKey
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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

begin_comment
comment|/**  * Authenticates by public key through {@link AccountSshKey} entities.  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|DatabasePubKeyAuth
class|class
name|DatabasePubKeyAuth
implements|implements
name|PublickeyAuthenticator
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
name|DatabasePubKeyAuth
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|sshKeyCache
specifier|private
specifier|final
name|SshKeyCacheImpl
name|sshKeyCache
decl_stmt|;
DECL|field|sshLog
specifier|private
specifier|final
name|SshLog
name|sshLog
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
DECL|field|peerFactory
specifier|private
specifier|final
name|PeerDaemonUser
operator|.
name|Factory
name|peerFactory
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|Config
name|config
decl_stmt|;
DECL|field|myHostKeys
specifier|private
specifier|final
name|Set
argument_list|<
name|PublicKey
argument_list|>
name|myHostKeys
decl_stmt|;
DECL|field|peerKeyCache
specifier|private
specifier|volatile
name|PeerKeyCache
name|peerKeyCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|DatabasePubKeyAuth (final SshKeyCacheImpl skc, final SshLog l, final IdentifiedUser.GenericFactory uf, final PeerDaemonUser.Factory pf, final SitePaths site, final KeyPairProvider hostKeyProvider, final @GerritServerConfig Config cfg)
name|DatabasePubKeyAuth
parameter_list|(
specifier|final
name|SshKeyCacheImpl
name|skc
parameter_list|,
specifier|final
name|SshLog
name|l
parameter_list|,
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|uf
parameter_list|,
specifier|final
name|PeerDaemonUser
operator|.
name|Factory
name|pf
parameter_list|,
specifier|final
name|SitePaths
name|site
parameter_list|,
specifier|final
name|KeyPairProvider
name|hostKeyProvider
parameter_list|,
specifier|final
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|sshKeyCache
operator|=
name|skc
expr_stmt|;
name|sshLog
operator|=
name|l
expr_stmt|;
name|userFactory
operator|=
name|uf
expr_stmt|;
name|peerFactory
operator|=
name|pf
expr_stmt|;
name|config
operator|=
name|cfg
expr_stmt|;
name|myHostKeys
operator|=
name|myHostKeys
argument_list|(
name|hostKeyProvider
argument_list|)
expr_stmt|;
name|peerKeyCache
operator|=
operator|new
name|PeerKeyCache
argument_list|(
name|site
operator|.
name|peer_keys
argument_list|)
expr_stmt|;
block|}
DECL|method|myHostKeys (KeyPairProvider p)
specifier|private
specifier|static
name|Set
argument_list|<
name|PublicKey
argument_list|>
name|myHostKeys
parameter_list|(
name|KeyPairProvider
name|p
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|PublicKey
argument_list|>
name|keys
init|=
operator|new
name|HashSet
argument_list|<
name|PublicKey
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|addPublicKey
argument_list|(
name|keys
argument_list|,
name|p
argument_list|,
name|KeyPairProvider
operator|.
name|SSH_RSA
argument_list|)
expr_stmt|;
name|addPublicKey
argument_list|(
name|keys
argument_list|,
name|p
argument_list|,
name|KeyPairProvider
operator|.
name|SSH_DSS
argument_list|)
expr_stmt|;
return|return
name|keys
return|;
block|}
DECL|method|addPublicKey (final Collection<PublicKey> out, final KeyPairProvider p, final String type)
specifier|private
specifier|static
name|void
name|addPublicKey
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|PublicKey
argument_list|>
name|out
parameter_list|,
specifier|final
name|KeyPairProvider
name|p
parameter_list|,
specifier|final
name|String
name|type
parameter_list|)
block|{
specifier|final
name|KeyPair
name|pair
init|=
name|p
operator|.
name|loadKey
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|pair
operator|!=
literal|null
operator|&&
name|pair
operator|.
name|getPublic
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|add
argument_list|(
name|pair
operator|.
name|getPublic
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|authenticate (String username, final PublicKey suppliedKey, final ServerSession session)
specifier|public
name|boolean
name|authenticate
parameter_list|(
name|String
name|username
parameter_list|,
specifier|final
name|PublicKey
name|suppliedKey
parameter_list|,
specifier|final
name|ServerSession
name|session
parameter_list|)
block|{
specifier|final
name|SshSession
name|sd
init|=
name|session
operator|.
name|getAttribute
argument_list|(
name|SshSession
operator|.
name|KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|PeerDaemonUser
operator|.
name|USER_NAME
operator|.
name|equals
argument_list|(
name|username
argument_list|)
condition|)
block|{
if|if
condition|(
name|myHostKeys
operator|.
name|contains
argument_list|(
name|suppliedKey
argument_list|)
operator|||
name|getPeerKeys
argument_list|()
operator|.
name|contains
argument_list|(
name|suppliedKey
argument_list|)
condition|)
block|{
name|PeerDaemonUser
name|user
init|=
name|peerFactory
operator|.
name|create
argument_list|(
name|sd
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|success
argument_list|(
name|username
argument_list|,
name|session
argument_list|,
name|sd
argument_list|,
name|user
argument_list|)
return|;
block|}
else|else
block|{
name|sd
operator|.
name|authenticationError
argument_list|(
name|username
argument_list|,
literal|"no-matching-key"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|config
operator|.
name|getBoolean
argument_list|(
literal|"auth"
argument_list|,
literal|"userNameToLowerCase"
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|username
operator|=
name|username
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Iterable
argument_list|<
name|SshKeyCacheEntry
argument_list|>
name|keyList
init|=
name|sshKeyCache
operator|.
name|get
argument_list|(
name|username
argument_list|)
decl_stmt|;
specifier|final
name|SshKeyCacheEntry
name|key
init|=
name|find
argument_list|(
name|keyList
argument_list|,
name|suppliedKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
specifier|final
name|String
name|err
decl_stmt|;
if|if
condition|(
name|keyList
operator|==
name|SshKeyCacheImpl
operator|.
name|NO_SUCH_USER
condition|)
block|{
name|err
operator|=
literal|"user-not-found"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|keyList
operator|==
name|SshKeyCacheImpl
operator|.
name|NO_KEYS
condition|)
block|{
name|err
operator|=
literal|"key-list-empty"
expr_stmt|;
block|}
else|else
block|{
name|err
operator|=
literal|"no-matching-key"
expr_stmt|;
block|}
name|sd
operator|.
name|authenticationError
argument_list|(
name|username
argument_list|,
name|err
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// Double check that all of the keys are for the same user account.
comment|// This should have been true when the cache factory method loaded
comment|// the list into memory, but we want to be extra paranoid about our
comment|// security check to ensure there aren't two users sharing the same
comment|// user name on the server.
comment|//
for|for
control|(
specifier|final
name|SshKeyCacheEntry
name|otherKey
range|:
name|keyList
control|)
block|{
if|if
condition|(
operator|!
name|key
operator|.
name|getAccount
argument_list|()
operator|.
name|equals
argument_list|(
name|otherKey
operator|.
name|getAccount
argument_list|()
argument_list|)
condition|)
block|{
name|sd
operator|.
name|authenticationError
argument_list|(
name|username
argument_list|,
literal|"keys-cross-accounts"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
operator|!
name|createUser
argument_list|(
name|sd
argument_list|,
name|key
argument_list|)
operator|.
name|getAccount
argument_list|()
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|sd
operator|.
name|authenticationError
argument_list|(
name|username
argument_list|,
literal|"inactive-account"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
name|success
argument_list|(
name|username
argument_list|,
name|session
argument_list|,
name|sd
argument_list|,
name|createUser
argument_list|(
name|sd
argument_list|,
name|key
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getPeerKeys ()
specifier|private
name|Set
argument_list|<
name|PublicKey
argument_list|>
name|getPeerKeys
parameter_list|()
block|{
name|PeerKeyCache
name|p
init|=
name|peerKeyCache
decl_stmt|;
if|if
condition|(
operator|!
name|p
operator|.
name|isCurrent
argument_list|()
condition|)
block|{
name|p
operator|=
name|p
operator|.
name|reload
argument_list|()
expr_stmt|;
name|peerKeyCache
operator|=
name|p
expr_stmt|;
block|}
return|return
name|p
operator|.
name|keys
return|;
block|}
DECL|method|success (final String username, final ServerSession session, final SshSession sd, final CurrentUser user)
specifier|private
name|boolean
name|success
parameter_list|(
specifier|final
name|String
name|username
parameter_list|,
specifier|final
name|ServerSession
name|session
parameter_list|,
specifier|final
name|SshSession
name|sd
parameter_list|,
specifier|final
name|CurrentUser
name|user
parameter_list|)
block|{
if|if
condition|(
name|sd
operator|.
name|getCurrentUser
argument_list|()
operator|==
literal|null
condition|)
block|{
name|sd
operator|.
name|authenticationSuccess
argument_list|(
name|username
argument_list|,
name|user
argument_list|)
expr_stmt|;
comment|// If this is the first time we've authenticated this
comment|// session, record a login event in the log and add
comment|// a close listener to record a logout event.
comment|//
name|Context
name|ctx
init|=
operator|new
name|Context
argument_list|(
name|sd
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Context
name|old
init|=
name|SshScope
operator|.
name|set
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
try|try
block|{
name|sshLog
operator|.
name|onLogin
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|SshScope
operator|.
name|set
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|getIoSession
argument_list|()
operator|.
name|getCloseFuture
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|IoFutureListener
argument_list|<
name|IoFuture
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|operationComplete
parameter_list|(
name|IoFuture
name|future
parameter_list|)
block|{
specifier|final
name|Context
name|ctx
init|=
operator|new
name|Context
argument_list|(
name|sd
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|Context
name|old
init|=
name|SshScope
operator|.
name|set
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
try|try
block|{
name|sshLog
operator|.
name|onLogout
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|SshScope
operator|.
name|set
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|createUser (final SshSession sd, final SshKeyCacheEntry key)
specifier|private
name|IdentifiedUser
name|createUser
parameter_list|(
specifier|final
name|SshSession
name|sd
parameter_list|,
specifier|final
name|SshKeyCacheEntry
name|key
parameter_list|)
block|{
return|return
name|userFactory
operator|.
name|create
argument_list|(
name|AccessPath
operator|.
name|SSH_COMMAND
argument_list|,
operator|new
name|Provider
argument_list|<
name|SocketAddress
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SocketAddress
name|get
parameter_list|()
block|{
return|return
name|sd
operator|.
name|getRemoteAddress
argument_list|()
return|;
block|}
block|}
argument_list|,
name|key
operator|.
name|getAccount
argument_list|()
argument_list|)
return|;
block|}
DECL|method|find (final Iterable<SshKeyCacheEntry> keyList, final PublicKey suppliedKey)
specifier|private
name|SshKeyCacheEntry
name|find
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|SshKeyCacheEntry
argument_list|>
name|keyList
parameter_list|,
specifier|final
name|PublicKey
name|suppliedKey
parameter_list|)
block|{
for|for
control|(
specifier|final
name|SshKeyCacheEntry
name|k
range|:
name|keyList
control|)
block|{
if|if
condition|(
name|k
operator|.
name|match
argument_list|(
name|suppliedKey
argument_list|)
condition|)
block|{
return|return
name|k
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|class|PeerKeyCache
specifier|private
specifier|static
class|class
name|PeerKeyCache
block|{
DECL|field|path
specifier|private
specifier|final
name|File
name|path
decl_stmt|;
DECL|field|modified
specifier|private
specifier|final
name|long
name|modified
decl_stmt|;
DECL|field|keys
specifier|final
name|Set
argument_list|<
name|PublicKey
argument_list|>
name|keys
decl_stmt|;
DECL|method|PeerKeyCache (final File path)
name|PeerKeyCache
parameter_list|(
specifier|final
name|File
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|modified
operator|=
name|path
operator|.
name|lastModified
argument_list|()
expr_stmt|;
name|this
operator|.
name|keys
operator|=
name|read
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|read (File path)
specifier|private
specifier|static
name|Set
argument_list|<
name|PublicKey
argument_list|>
name|read
parameter_list|(
name|File
name|path
parameter_list|)
block|{
try|try
block|{
specifier|final
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Set
argument_list|<
name|PublicKey
argument_list|>
name|keys
init|=
operator|new
name|HashSet
argument_list|<
name|PublicKey
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
operator|||
name|line
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
try|try
block|{
name|byte
index|[]
name|bin
init|=
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|line
operator|.
name|getBytes
argument_list|(
literal|"ISO-8859-1"
argument_list|)
argument_list|)
decl_stmt|;
name|keys
operator|.
name|add
argument_list|(
operator|new
name|Buffer
argument_list|(
name|bin
argument_list|)
operator|.
name|getRawPublicKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|logBadKey
argument_list|(
name|path
argument_list|,
name|line
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SshException
name|e
parameter_list|)
block|{
name|logBadKey
argument_list|(
name|path
argument_list|,
name|line
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|keys
argument_list|)
return|;
block|}
finally|finally
block|{
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|noFile
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|err
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot read "
operator|+
name|path
argument_list|,
name|err
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
block|}
DECL|method|logBadKey (File path, String line, Exception e)
specifier|private
specifier|static
name|void
name|logBadKey
parameter_list|(
name|File
name|path
parameter_list|,
name|String
name|line
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Invalid key in "
operator|+
name|path
operator|+
literal|":\n  "
operator|+
name|line
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
DECL|method|isCurrent ()
name|boolean
name|isCurrent
parameter_list|()
block|{
return|return
name|path
operator|.
name|lastModified
argument_list|()
operator|==
name|modified
return|;
block|}
DECL|method|reload ()
name|PeerKeyCache
name|reload
parameter_list|()
block|{
return|return
operator|new
name|PeerKeyCache
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

