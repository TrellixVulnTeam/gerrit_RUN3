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
DECL|package|com.google.gerrit.gpg
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|gpg
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
name|MoreObjects
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
name|ImmutableMap
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
name|Ordering
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
name|PageLinks
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
name|config
operator|.
name|CanonicalWebUrl
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
name|PGPSignature
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
name|operator
operator|.
name|bc
operator|.
name|BcPGPContentVerifierBuilderProvider
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
name|eclipse
operator|.
name|jgit
operator|.
name|transport
operator|.
name|PushCertificateIdent
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
comment|/**  * Checker for GPG public keys including Gerrit-specific checks.  *<p>  * For Gerrit, keys must contain a self-signed user ID certification matching a  * trusted external ID in the database, or an email address thereof.  */
end_comment

begin_class
DECL|class|GerritPublicKeyChecker
specifier|public
class|class
name|GerritPublicKeyChecker
extends|extends
name|PublicKeyChecker
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
name|GerritPublicKeyChecker
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Singleton
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
block|{
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|webUrl
specifier|private
specifier|final
name|String
name|webUrl
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
DECL|field|maxTrustDepth
specifier|private
specifier|final
name|int
name|maxTrustDepth
decl_stmt|;
DECL|field|trusted
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|Long
argument_list|,
name|Fingerprint
argument_list|>
name|trusted
decl_stmt|;
annotation|@
name|Inject
DECL|method|Factory (@erritServerConfig Config cfg, Provider<ReviewDb> db, IdentifiedUser.GenericFactory userFactory, @CanonicalWebUrl String webUrl)
name|Factory
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
parameter_list|,
annotation|@
name|CanonicalWebUrl
name|String
name|webUrl
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|webUrl
operator|=
name|webUrl
expr_stmt|;
name|this
operator|.
name|userFactory
operator|=
name|userFactory
expr_stmt|;
name|this
operator|.
name|maxTrustDepth
operator|=
name|cfg
operator|.
name|getInt
argument_list|(
literal|"receive"
argument_list|,
literal|null
argument_list|,
literal|"maxTrustDepth"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|String
index|[]
name|strs
init|=
name|cfg
operator|.
name|getStringList
argument_list|(
literal|"receive"
argument_list|,
literal|null
argument_list|,
literal|"trustedKey"
argument_list|)
decl_stmt|;
if|if
condition|(
name|strs
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|Map
argument_list|<
name|Long
argument_list|,
name|Fingerprint
argument_list|>
name|fps
init|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
name|strs
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|str
range|:
name|strs
control|)
block|{
name|str
operator|=
name|CharMatcher
operator|.
name|WHITESPACE
operator|.
name|removeFrom
argument_list|(
name|str
argument_list|)
operator|.
name|toUpperCase
argument_list|()
expr_stmt|;
name|Fingerprint
name|fp
init|=
operator|new
name|Fingerprint
argument_list|(
name|BaseEncoding
operator|.
name|base16
argument_list|()
operator|.
name|decode
argument_list|(
name|str
argument_list|)
argument_list|)
decl_stmt|;
name|fps
operator|.
name|put
argument_list|(
name|fp
operator|.
name|getId
argument_list|()
argument_list|,
name|fp
argument_list|)
expr_stmt|;
block|}
name|trusted
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|fps
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|trusted
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|create ()
specifier|public
name|GerritPublicKeyChecker
name|create
parameter_list|()
block|{
return|return
operator|new
name|GerritPublicKeyChecker
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|create (IdentifiedUser expectedUser, PublicKeyStore store)
specifier|public
name|GerritPublicKeyChecker
name|create
parameter_list|(
name|IdentifiedUser
name|expectedUser
parameter_list|,
name|PublicKeyStore
name|store
parameter_list|)
block|{
name|GerritPublicKeyChecker
name|checker
init|=
operator|new
name|GerritPublicKeyChecker
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|checker
operator|.
name|setExpectedUser
argument_list|(
name|expectedUser
argument_list|)
expr_stmt|;
name|checker
operator|.
name|setStore
argument_list|(
name|store
argument_list|)
expr_stmt|;
return|return
name|checker
return|;
block|}
block|}
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|webUrl
specifier|private
specifier|final
name|String
name|webUrl
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
DECL|field|expectedUser
specifier|private
name|IdentifiedUser
name|expectedUser
decl_stmt|;
DECL|method|GerritPublicKeyChecker (Factory factory)
specifier|private
name|GerritPublicKeyChecker
parameter_list|(
name|Factory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|factory
operator|.
name|db
expr_stmt|;
name|this
operator|.
name|webUrl
operator|=
name|factory
operator|.
name|webUrl
expr_stmt|;
name|this
operator|.
name|userFactory
operator|=
name|factory
operator|.
name|userFactory
expr_stmt|;
if|if
condition|(
name|factory
operator|.
name|trusted
operator|!=
literal|null
condition|)
block|{
name|enableTrust
argument_list|(
name|factory
operator|.
name|maxTrustDepth
argument_list|,
name|factory
operator|.
name|trusted
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * Set the expected user for this checker.     *<p>     * If set, the top-level key passed to {@link #check(PGPPublicKey)} must     * belong to the given user. (Other keys checked in the course of verifying     * the web of trust are checked against the set of identities in the database     * belonging to the same user as the key.)     */
DECL|method|setExpectedUser (IdentifiedUser expectedUser)
specifier|public
name|GerritPublicKeyChecker
name|setExpectedUser
parameter_list|(
name|IdentifiedUser
name|expectedUser
parameter_list|)
block|{
name|this
operator|.
name|expectedUser
operator|=
name|expectedUser
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|checkCustom (PGPPublicKey key, int depth)
specifier|public
name|CheckResult
name|checkCustom
parameter_list|(
name|PGPPublicKey
name|key
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|depth
operator|==
literal|0
operator|&&
name|expectedUser
operator|!=
literal|null
condition|)
block|{
return|return
name|checkIdsForExpectedUser
argument_list|(
name|key
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|checkIdsForArbitraryUser
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|PGPException
decl||
name|OrmException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Error checking user IDs for key"
decl_stmt|;
name|log
operator|.
name|warn
argument_list|(
name|msg
operator|+
literal|" "
operator|+
name|keyIdToString
argument_list|(
name|key
operator|.
name|getKeyID
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|CheckResult
operator|.
name|bad
argument_list|(
name|msg
argument_list|)
return|;
block|}
block|}
DECL|method|checkIdsForExpectedUser (PGPPublicKey key)
specifier|private
name|CheckResult
name|checkIdsForExpectedUser
parameter_list|(
name|PGPPublicKey
name|key
parameter_list|)
throws|throws
name|PGPException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|allowedUserIds
init|=
name|getAllowedUserIds
argument_list|(
name|expectedUser
argument_list|)
decl_stmt|;
if|if
condition|(
name|allowedUserIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|CheckResult
operator|.
name|bad
argument_list|(
literal|"No identities found for user; check "
operator|+
name|webUrl
operator|+
literal|"#"
operator|+
name|PageLinks
operator|.
name|SETTINGS_WEBIDENT
argument_list|)
return|;
block|}
if|if
condition|(
name|hasAllowedUserId
argument_list|(
name|key
argument_list|,
name|allowedUserIds
argument_list|)
condition|)
block|{
return|return
name|CheckResult
operator|.
name|trusted
argument_list|()
return|;
block|}
return|return
name|CheckResult
operator|.
name|bad
argument_list|(
name|missingUserIds
argument_list|(
name|allowedUserIds
argument_list|)
argument_list|)
return|;
block|}
DECL|method|checkIdsForArbitraryUser (PGPPublicKey key)
specifier|private
name|CheckResult
name|checkIdsForArbitraryUser
parameter_list|(
name|PGPPublicKey
name|key
parameter_list|)
throws|throws
name|PGPException
throws|,
name|OrmException
block|{
name|AccountExternalId
name|extId
init|=
name|db
operator|.
name|get
argument_list|()
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|get
argument_list|(
name|toExtIdKey
argument_list|(
name|key
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|extId
operator|==
literal|null
condition|)
block|{
return|return
name|CheckResult
operator|.
name|bad
argument_list|(
literal|"Key is not associated with any users"
argument_list|)
return|;
block|}
name|IdentifiedUser
name|user
init|=
name|userFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|extId
operator|.
name|getAccountId
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|allowedUserIds
init|=
name|getAllowedUserIds
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|allowedUserIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|CheckResult
operator|.
name|bad
argument_list|(
literal|"No identities found for user"
argument_list|)
return|;
block|}
if|if
condition|(
name|hasAllowedUserId
argument_list|(
name|key
argument_list|,
name|allowedUserIds
argument_list|)
condition|)
block|{
return|return
name|CheckResult
operator|.
name|trusted
argument_list|()
return|;
block|}
return|return
name|CheckResult
operator|.
name|bad
argument_list|(
literal|"Key does not contain any valid certifications for user's identities"
argument_list|)
return|;
block|}
DECL|method|hasAllowedUserId (PGPPublicKey key, Set<String> allowedUserIds)
specifier|private
name|boolean
name|hasAllowedUserId
parameter_list|(
name|PGPPublicKey
name|key
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|allowedUserIds
parameter_list|)
throws|throws
name|PGPException
block|{
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
while|while
condition|(
name|userIds
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|userId
init|=
name|userIds
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|isAllowed
argument_list|(
name|userId
argument_list|,
name|allowedUserIds
argument_list|)
condition|)
block|{
name|Iterator
argument_list|<
name|PGPSignature
argument_list|>
name|sigs
init|=
name|getSignaturesForId
argument_list|(
name|key
argument_list|,
name|userId
argument_list|)
decl_stmt|;
while|while
condition|(
name|sigs
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|isValidCertification
argument_list|(
name|key
argument_list|,
name|sigs
operator|.
name|next
argument_list|()
argument_list|,
name|userId
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getSignaturesForId (PGPPublicKey key, String userId)
specifier|private
name|Iterator
argument_list|<
name|PGPSignature
argument_list|>
name|getSignaturesForId
parameter_list|(
name|PGPPublicKey
name|key
parameter_list|,
name|String
name|userId
parameter_list|)
block|{
return|return
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|key
operator|.
name|getSignaturesForID
argument_list|(
name|userId
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptyIterator
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getAllowedUserIds (IdentifiedUser user)
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getAllowedUserIds
parameter_list|(
name|IdentifiedUser
name|user
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|user
operator|.
name|getEmailAddresses
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AccountExternalId
name|extId
range|:
name|user
operator|.
name|state
argument_list|()
operator|.
name|getExternalIds
argument_list|()
control|)
block|{
if|if
condition|(
name|extId
operator|.
name|isScheme
argument_list|(
name|SCHEME_GPGKEY
argument_list|)
condition|)
block|{
continue|continue;
comment|// Omit GPG keys.
block|}
name|result
operator|.
name|add
argument_list|(
name|extId
operator|.
name|getExternalId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|isAllowed (String userId, Set<String> allowedUserIds)
specifier|private
specifier|static
name|boolean
name|isAllowed
parameter_list|(
name|String
name|userId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|allowedUserIds
parameter_list|)
block|{
return|return
name|allowedUserIds
operator|.
name|contains
argument_list|(
name|userId
argument_list|)
operator|||
name|allowedUserIds
operator|.
name|contains
argument_list|(
name|PushCertificateIdent
operator|.
name|parse
argument_list|(
name|userId
argument_list|)
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
return|;
block|}
DECL|method|isValidCertification (PGPPublicKey key, PGPSignature sig, String userId)
specifier|private
specifier|static
name|boolean
name|isValidCertification
parameter_list|(
name|PGPPublicKey
name|key
parameter_list|,
name|PGPSignature
name|sig
parameter_list|,
name|String
name|userId
parameter_list|)
throws|throws
name|PGPException
block|{
if|if
condition|(
name|sig
operator|.
name|getSignatureType
argument_list|()
operator|!=
name|PGPSignature
operator|.
name|DEFAULT_CERTIFICATION
operator|&&
name|sig
operator|.
name|getSignatureType
argument_list|()
operator|!=
name|PGPSignature
operator|.
name|POSITIVE_CERTIFICATION
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|sig
operator|.
name|getKeyID
argument_list|()
operator|!=
name|key
operator|.
name|getKeyID
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// TODO(dborowitz): Handle certification revocations:
comment|// - Is there a revocation by either this key or another key trusted by the
comment|//   server?
comment|// - Does such a revocation postdate all other valid certifications?
name|sig
operator|.
name|init
argument_list|(
operator|new
name|BcPGPContentVerifierBuilderProvider
argument_list|()
argument_list|,
name|key
argument_list|)
expr_stmt|;
return|return
name|sig
operator|.
name|verifyCertification
argument_list|(
name|userId
argument_list|,
name|key
argument_list|)
return|;
block|}
DECL|method|missingUserIds (Set<String> allowedUserIds)
specifier|private
specifier|static
name|String
name|missingUserIds
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|allowedUserIds
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Key must contain a valid"
operator|+
literal|" certification for one of the following identities:\n"
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|sorted
init|=
name|FluentIterable
operator|.
name|from
argument_list|(
name|allowedUserIds
argument_list|)
operator|.
name|toSortedList
argument_list|(
name|Ordering
operator|.
name|natural
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|sorted
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
name|sorted
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|sorted
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toExtIdKey (PGPPublicKey key)
specifier|static
name|AccountExternalId
operator|.
name|Key
name|toExtIdKey
parameter_list|(
name|PGPPublicKey
name|key
parameter_list|)
block|{
return|return
operator|new
name|AccountExternalId
operator|.
name|Key
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
name|key
operator|.
name|getFingerprint
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

