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
name|gpg
operator|.
name|testing
operator|.
name|TestKeys
operator|.
name|expiredKey
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
name|testing
operator|.
name|TestKeys
operator|.
name|keyRevokedByExpiredKeyAfterExpiration
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
name|testing
operator|.
name|TestKeys
operator|.
name|keyRevokedByExpiredKeyBeforeExpiration
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
name|testing
operator|.
name|TestKeys
operator|.
name|revokedCompromisedKey
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
name|testing
operator|.
name|TestKeys
operator|.
name|revokedNoLongerUsedKey
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
name|testing
operator|.
name|TestKeys
operator|.
name|selfRevokedKey
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
name|testing
operator|.
name|TestKeys
operator|.
name|validKeyWithExpiration
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
name|testing
operator|.
name|TestKeys
operator|.
name|validKeyWithoutExpiration
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
name|testing
operator|.
name|TestTrustKeys
operator|.
name|keyA
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
name|testing
operator|.
name|TestTrustKeys
operator|.
name|keyB
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
name|testing
operator|.
name|TestTrustKeys
operator|.
name|keyC
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
name|testing
operator|.
name|TestTrustKeys
operator|.
name|keyD
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
name|testing
operator|.
name|TestTrustKeys
operator|.
name|keyE
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
name|testing
operator|.
name|TestTrustKeys
operator|.
name|keyF
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
name|testing
operator|.
name|TestTrustKeys
operator|.
name|keyG
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
name|testing
operator|.
name|TestTrustKeys
operator|.
name|keyH
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
name|testing
operator|.
name|TestTrustKeys
operator|.
name|keyI
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
name|testing
operator|.
name|TestTrustKeys
operator|.
name|keyJ
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|bouncycastle
operator|.
name|bcpg
operator|.
name|SignatureSubpacketTags
operator|.
name|REVOCATION_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|bouncycastle
operator|.
name|openpgp
operator|.
name|PGPSignature
operator|.
name|DIRECT_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|testing
operator|.
name|TestKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|Arrays
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
name|Date
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
name|PGPSignature
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
name|internal
operator|.
name|storage
operator|.
name|dfs
operator|.
name|DfsRepositoryDescription
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
name|internal
operator|.
name|storage
operator|.
name|dfs
operator|.
name|InMemoryRepository
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
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
import|;
end_import

begin_class
DECL|class|PublicKeyCheckerTest
specifier|public
class|class
name|PublicKeyCheckerTest
block|{
DECL|field|thrown
annotation|@
name|Rule
specifier|public
name|ExpectedException
name|thrown
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
DECL|field|repo
specifier|private
name|InMemoryRepository
name|repo
decl_stmt|;
DECL|field|store
specifier|private
name|PublicKeyStore
name|store
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|repo
operator|=
operator|new
name|InMemoryRepository
argument_list|(
operator|new
name|DfsRepositoryDescription
argument_list|(
literal|"repo"
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|PublicKeyStore
argument_list|(
name|repo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|repo
operator|!=
literal|null
condition|)
block|{
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
name|repo
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|validKey ()
specifier|public
name|void
name|validKey
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNoProblems
argument_list|(
name|validKeyWithoutExpiration
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|keyExpiringInFuture ()
specifier|public
name|void
name|keyExpiringInFuture
parameter_list|()
throws|throws
name|Exception
block|{
name|TestKey
name|k
init|=
name|validKeyWithExpiration
argument_list|()
decl_stmt|;
name|PublicKeyChecker
name|checker
init|=
operator|new
name|PublicKeyChecker
argument_list|()
operator|.
name|setStore
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|assertNoProblems
argument_list|(
name|checker
argument_list|,
name|k
argument_list|)
expr_stmt|;
name|checker
operator|.
name|setEffectiveTime
argument_list|(
name|parseDate
argument_list|(
literal|"2015-07-10 12:00:00 -0400"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoProblems
argument_list|(
name|checker
argument_list|,
name|k
argument_list|)
expr_stmt|;
name|checker
operator|.
name|setEffectiveTime
argument_list|(
name|parseDate
argument_list|(
literal|"2075-07-10 12:00:00 -0400"
argument_list|)
argument_list|)
expr_stmt|;
name|assertProblems
argument_list|(
name|checker
argument_list|,
name|k
argument_list|,
literal|"Key is expired"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|expiredKeyIsExpired ()
specifier|public
name|void
name|expiredKeyIsExpired
parameter_list|()
throws|throws
name|Exception
block|{
name|assertProblems
argument_list|(
name|expiredKey
argument_list|()
argument_list|,
literal|"Key is expired"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|selfRevokedKeyIsRevoked ()
specifier|public
name|void
name|selfRevokedKeyIsRevoked
parameter_list|()
throws|throws
name|Exception
block|{
name|assertProblems
argument_list|(
name|selfRevokedKey
argument_list|()
argument_list|,
literal|"Key is revoked (key material has been compromised)"
argument_list|)
expr_stmt|;
block|}
comment|// Test keys specific to this test are at the bottom of this class. Each test
comment|// has a diagram of the trust network, where:
comment|//  - The notation M---N indicates N trusts M.
comment|//  - An 'x' indicates the key is expired.
annotation|@
name|Test
DECL|method|trustValidPathLength2 ()
specifier|public
name|void
name|trustValidPathLength2
parameter_list|()
throws|throws
name|Exception
block|{
comment|// A---Bx
comment|//  \
comment|//   \---C---D
comment|//        \
comment|//         \---Ex
comment|//
comment|// D and E trust C to be a valid introducer of depth 2.
name|TestKey
name|ka
init|=
name|add
argument_list|(
name|keyA
argument_list|()
argument_list|)
decl_stmt|;
name|TestKey
name|kb
init|=
name|add
argument_list|(
name|keyB
argument_list|()
argument_list|)
decl_stmt|;
name|TestKey
name|kc
init|=
name|add
argument_list|(
name|keyC
argument_list|()
argument_list|)
decl_stmt|;
name|TestKey
name|kd
init|=
name|add
argument_list|(
name|keyD
argument_list|()
argument_list|)
decl_stmt|;
name|TestKey
name|ke
init|=
name|add
argument_list|(
name|keyE
argument_list|()
argument_list|)
decl_stmt|;
name|save
argument_list|()
expr_stmt|;
name|PublicKeyChecker
name|checker
init|=
name|newChecker
argument_list|(
literal|2
argument_list|,
name|kb
argument_list|,
name|kd
argument_list|)
decl_stmt|;
name|assertNoProblems
argument_list|(
name|checker
argument_list|,
name|ka
argument_list|)
expr_stmt|;
name|assertProblems
argument_list|(
name|checker
argument_list|,
name|kb
argument_list|,
literal|"Key is expired"
argument_list|)
expr_stmt|;
name|assertNoProblems
argument_list|(
name|checker
argument_list|,
name|kc
argument_list|)
expr_stmt|;
name|assertNoProblems
argument_list|(
name|checker
argument_list|,
name|kd
argument_list|)
expr_stmt|;
name|assertProblems
argument_list|(
name|checker
argument_list|,
name|ke
argument_list|,
literal|"Key is expired"
argument_list|,
literal|"No path to a trusted key"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|trustValidPathLength1 ()
specifier|public
name|void
name|trustValidPathLength1
parameter_list|()
throws|throws
name|Exception
block|{
comment|// A---Bx
comment|//  \
comment|//   \---C---D
comment|//        \
comment|//         \---Ex
comment|//
comment|// D and E trust C to be a valid introducer of depth 2.
name|TestKey
name|ka
init|=
name|add
argument_list|(
name|keyA
argument_list|()
argument_list|)
decl_stmt|;
name|TestKey
name|kb
init|=
name|add
argument_list|(
name|keyB
argument_list|()
argument_list|)
decl_stmt|;
name|TestKey
name|kc
init|=
name|add
argument_list|(
name|keyC
argument_list|()
argument_list|)
decl_stmt|;
name|TestKey
name|kd
init|=
name|add
argument_list|(
name|keyD
argument_list|()
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|keyE
argument_list|()
argument_list|)
expr_stmt|;
name|save
argument_list|()
expr_stmt|;
name|PublicKeyChecker
name|checker
init|=
name|newChecker
argument_list|(
literal|1
argument_list|,
name|kd
argument_list|)
decl_stmt|;
name|assertProblems
argument_list|(
name|checker
argument_list|,
name|ka
argument_list|,
literal|"No path to a trusted key"
argument_list|,
name|notTrusted
argument_list|(
name|kb
argument_list|)
argument_list|,
name|notTrusted
argument_list|(
name|kc
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|trustCycle ()
specifier|public
name|void
name|trustCycle
parameter_list|()
throws|throws
name|Exception
block|{
comment|// F---G---F, in a cycle.
name|TestKey
name|kf
init|=
name|add
argument_list|(
name|keyF
argument_list|()
argument_list|)
decl_stmt|;
name|TestKey
name|kg
init|=
name|add
argument_list|(
name|keyG
argument_list|()
argument_list|)
decl_stmt|;
name|save
argument_list|()
expr_stmt|;
name|PublicKeyChecker
name|checker
init|=
name|newChecker
argument_list|(
literal|10
argument_list|,
name|keyA
argument_list|()
argument_list|)
decl_stmt|;
name|assertProblems
argument_list|(
name|checker
argument_list|,
name|kf
argument_list|,
literal|"No path to a trusted key"
argument_list|,
name|notTrusted
argument_list|(
name|kg
argument_list|)
argument_list|)
expr_stmt|;
name|assertProblems
argument_list|(
name|checker
argument_list|,
name|kg
argument_list|,
literal|"No path to a trusted key"
argument_list|,
name|notTrusted
argument_list|(
name|kf
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|trustInsufficientDepthInSignature ()
specifier|public
name|void
name|trustInsufficientDepthInSignature
parameter_list|()
throws|throws
name|Exception
block|{
comment|// H---I---J, but J is only trusted to length 1.
name|TestKey
name|kh
init|=
name|add
argument_list|(
name|keyH
argument_list|()
argument_list|)
decl_stmt|;
name|TestKey
name|ki
init|=
name|add
argument_list|(
name|keyI
argument_list|()
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|keyJ
argument_list|()
argument_list|)
expr_stmt|;
name|save
argument_list|()
expr_stmt|;
name|PublicKeyChecker
name|checker
init|=
name|newChecker
argument_list|(
literal|10
argument_list|,
name|keyJ
argument_list|()
argument_list|)
decl_stmt|;
comment|// J trusts I to a depth of 1, so I itself is valid, but I's certification
comment|// of K is not valid.
name|assertNoProblems
argument_list|(
name|checker
argument_list|,
name|ki
argument_list|)
expr_stmt|;
name|assertProblems
argument_list|(
name|checker
argument_list|,
name|kh
argument_list|,
literal|"No path to a trusted key"
argument_list|,
name|notTrusted
argument_list|(
name|ki
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|revokedKeyDueToCompromise ()
specifier|public
name|void
name|revokedKeyDueToCompromise
parameter_list|()
throws|throws
name|Exception
block|{
name|TestKey
name|k
init|=
name|add
argument_list|(
name|revokedCompromisedKey
argument_list|()
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|validKeyWithoutExpiration
argument_list|()
argument_list|)
expr_stmt|;
name|save
argument_list|()
expr_stmt|;
name|assertProblems
argument_list|(
name|k
argument_list|,
literal|"Key is revoked (key material has been compromised): test6 compromised"
argument_list|)
expr_stmt|;
name|PGPPublicKeyRing
name|kr
init|=
name|removeRevokers
argument_list|(
name|k
operator|.
name|getPublicKeyRing
argument_list|()
argument_list|)
decl_stmt|;
name|store
operator|.
name|add
argument_list|(
name|kr
argument_list|)
expr_stmt|;
name|save
argument_list|()
expr_stmt|;
comment|// Key no longer specified as revoker.
name|assertNoProblems
argument_list|(
name|kr
operator|.
name|getPublicKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|revokedKeyDueToCompromiseRevokesKeyRetroactively ()
specifier|public
name|void
name|revokedKeyDueToCompromiseRevokesKeyRetroactively
parameter_list|()
throws|throws
name|Exception
block|{
name|TestKey
name|k
init|=
name|add
argument_list|(
name|revokedCompromisedKey
argument_list|()
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|validKeyWithoutExpiration
argument_list|()
argument_list|)
expr_stmt|;
name|save
argument_list|()
expr_stmt|;
name|String
name|problem
init|=
literal|"Key is revoked (key material has been compromised): test6 compromised"
decl_stmt|;
name|assertProblems
argument_list|(
name|k
argument_list|,
name|problem
argument_list|)
expr_stmt|;
name|SimpleDateFormat
name|df
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm:ss"
argument_list|)
decl_stmt|;
name|PublicKeyChecker
name|checker
init|=
operator|new
name|PublicKeyChecker
argument_list|()
operator|.
name|setStore
argument_list|(
name|store
argument_list|)
operator|.
name|setEffectiveTime
argument_list|(
name|df
operator|.
name|parse
argument_list|(
literal|"2010-01-01 12:00:00"
argument_list|)
argument_list|)
decl_stmt|;
name|assertProblems
argument_list|(
name|checker
argument_list|,
name|k
argument_list|,
name|problem
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|revokedByKeyNotPresentInStore ()
specifier|public
name|void
name|revokedByKeyNotPresentInStore
parameter_list|()
throws|throws
name|Exception
block|{
name|TestKey
name|k
init|=
name|add
argument_list|(
name|revokedCompromisedKey
argument_list|()
argument_list|)
decl_stmt|;
name|save
argument_list|()
expr_stmt|;
name|assertProblems
argument_list|(
name|k
argument_list|,
literal|"Key is revoked (key material has been compromised): test6 compromised"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|revokedKeyDueToNoLongerBeingUsed ()
specifier|public
name|void
name|revokedKeyDueToNoLongerBeingUsed
parameter_list|()
throws|throws
name|Exception
block|{
name|TestKey
name|k
init|=
name|add
argument_list|(
name|revokedNoLongerUsedKey
argument_list|()
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|validKeyWithoutExpiration
argument_list|()
argument_list|)
expr_stmt|;
name|save
argument_list|()
expr_stmt|;
name|assertProblems
argument_list|(
name|k
argument_list|,
literal|"Key is revoked (retired and no longer valid): test7 not used"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|revokedKeyDueToNoLongerBeingUsedDoesNotRevokeKeyRetroactively ()
specifier|public
name|void
name|revokedKeyDueToNoLongerBeingUsedDoesNotRevokeKeyRetroactively
parameter_list|()
throws|throws
name|Exception
block|{
name|TestKey
name|k
init|=
name|add
argument_list|(
name|revokedNoLongerUsedKey
argument_list|()
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|validKeyWithoutExpiration
argument_list|()
argument_list|)
expr_stmt|;
name|save
argument_list|()
expr_stmt|;
name|assertProblems
argument_list|(
name|k
argument_list|,
literal|"Key is revoked (retired and no longer valid): test7 not used"
argument_list|)
expr_stmt|;
name|PublicKeyChecker
name|checker
init|=
operator|new
name|PublicKeyChecker
argument_list|()
operator|.
name|setStore
argument_list|(
name|store
argument_list|)
operator|.
name|setEffectiveTime
argument_list|(
name|parseDate
argument_list|(
literal|"2010-01-01 12:00:00 -0400"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNoProblems
argument_list|(
name|checker
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|keyRevokedByExpiredKeyAfterExpirationIsNotRevoked ()
specifier|public
name|void
name|keyRevokedByExpiredKeyAfterExpirationIsNotRevoked
parameter_list|()
throws|throws
name|Exception
block|{
name|TestKey
name|k
init|=
name|add
argument_list|(
name|keyRevokedByExpiredKeyAfterExpiration
argument_list|()
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|expiredKey
argument_list|()
argument_list|)
expr_stmt|;
name|save
argument_list|()
expr_stmt|;
name|PublicKeyChecker
name|checker
init|=
operator|new
name|PublicKeyChecker
argument_list|()
operator|.
name|setStore
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|assertNoProblems
argument_list|(
name|checker
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|keyRevokedByExpiredKeyBeforeExpirationIsRevoked ()
specifier|public
name|void
name|keyRevokedByExpiredKeyBeforeExpirationIsRevoked
parameter_list|()
throws|throws
name|Exception
block|{
name|TestKey
name|k
init|=
name|add
argument_list|(
name|keyRevokedByExpiredKeyBeforeExpiration
argument_list|()
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|expiredKey
argument_list|()
argument_list|)
expr_stmt|;
name|save
argument_list|()
expr_stmt|;
name|PublicKeyChecker
name|checker
init|=
operator|new
name|PublicKeyChecker
argument_list|()
operator|.
name|setStore
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|assertProblems
argument_list|(
name|checker
argument_list|,
name|k
argument_list|,
literal|"Key is revoked (retired and no longer valid): test9 not used"
argument_list|)
expr_stmt|;
comment|// Set time between key creation and revocation.
name|checker
operator|.
name|setEffectiveTime
argument_list|(
name|parseDate
argument_list|(
literal|"2005-08-01 13:00:00 -0400"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoProblems
argument_list|(
name|checker
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
DECL|method|removeRevokers (PGPPublicKeyRing kr)
specifier|private
name|PGPPublicKeyRing
name|removeRevokers
parameter_list|(
name|PGPPublicKeyRing
name|kr
parameter_list|)
block|{
name|PGPPublicKey
name|k
init|=
name|kr
operator|.
name|getPublicKey
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Iterator
argument_list|<
name|PGPSignature
argument_list|>
name|sigs
init|=
name|k
operator|.
name|getSignaturesOfType
argument_list|(
name|DIRECT_KEY
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
name|PGPSignature
name|sig
init|=
name|sigs
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|sig
operator|.
name|getHashedSubPackets
argument_list|()
operator|.
name|hasSubpacket
argument_list|(
name|REVOCATION_KEY
argument_list|)
condition|)
block|{
name|k
operator|=
name|PGPPublicKey
operator|.
name|removeCertification
argument_list|(
name|k
argument_list|,
name|sig
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|PGPPublicKeyRing
operator|.
name|insertPublicKey
argument_list|(
name|kr
argument_list|,
name|k
argument_list|)
return|;
block|}
DECL|method|newChecker (int maxTrustDepth, TestKey... trusted)
specifier|private
name|PublicKeyChecker
name|newChecker
parameter_list|(
name|int
name|maxTrustDepth
parameter_list|,
name|TestKey
modifier|...
name|trusted
parameter_list|)
block|{
name|Map
argument_list|<
name|Long
argument_list|,
name|Fingerprint
argument_list|>
name|fps
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|TestKey
name|k
range|:
name|trusted
control|)
block|{
name|Fingerprint
name|fp
init|=
operator|new
name|Fingerprint
argument_list|(
name|k
operator|.
name|getPublicKey
argument_list|()
operator|.
name|getFingerprint
argument_list|()
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
return|return
operator|new
name|PublicKeyChecker
argument_list|()
operator|.
name|enableTrust
argument_list|(
name|maxTrustDepth
argument_list|,
name|fps
argument_list|)
operator|.
name|setStore
argument_list|(
name|store
argument_list|)
return|;
block|}
DECL|method|add (TestKey k)
specifier|private
name|TestKey
name|add
parameter_list|(
name|TestKey
name|k
parameter_list|)
block|{
name|store
operator|.
name|add
argument_list|(
name|k
operator|.
name|getPublicKeyRing
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|k
return|;
block|}
DECL|method|save ()
specifier|private
name|void
name|save
parameter_list|()
throws|throws
name|Exception
block|{
name|PersonIdent
name|ident
init|=
operator|new
name|PersonIdent
argument_list|(
literal|"A U Thor"
argument_list|,
literal|"author@example.com"
argument_list|)
decl_stmt|;
name|CommitBuilder
name|cb
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
name|cb
operator|.
name|setAuthor
argument_list|(
name|ident
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setCommitter
argument_list|(
name|ident
argument_list|)
expr_stmt|;
name|RefUpdate
operator|.
name|Result
name|result
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
name|result
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
name|NO_CHANGE
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
name|AssertionError
argument_list|(
name|result
argument_list|)
throw|;
block|}
block|}
DECL|method|assertProblems (PublicKeyChecker checker, TestKey k, String first, String... rest)
specifier|private
name|void
name|assertProblems
parameter_list|(
name|PublicKeyChecker
name|checker
parameter_list|,
name|TestKey
name|k
parameter_list|,
name|String
name|first
parameter_list|,
name|String
modifier|...
name|rest
parameter_list|)
block|{
name|CheckResult
name|result
init|=
name|checker
operator|.
name|setStore
argument_list|(
name|store
argument_list|)
operator|.
name|check
argument_list|(
name|k
operator|.
name|getPublicKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|list
argument_list|(
name|first
argument_list|,
name|rest
argument_list|)
argument_list|,
name|result
operator|.
name|getProblems
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNoProblems (PublicKeyChecker checker, TestKey k)
specifier|private
name|void
name|assertNoProblems
parameter_list|(
name|PublicKeyChecker
name|checker
parameter_list|,
name|TestKey
name|k
parameter_list|)
block|{
name|CheckResult
name|result
init|=
name|checker
operator|.
name|setStore
argument_list|(
name|store
argument_list|)
operator|.
name|check
argument_list|(
name|k
operator|.
name|getPublicKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|result
operator|.
name|getProblems
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertProblems (TestKey tk, String first, String... rest)
specifier|private
name|void
name|assertProblems
parameter_list|(
name|TestKey
name|tk
parameter_list|,
name|String
name|first
parameter_list|,
name|String
modifier|...
name|rest
parameter_list|)
block|{
name|assertProblems
argument_list|(
name|tk
operator|.
name|getPublicKey
argument_list|()
argument_list|,
name|first
argument_list|,
name|rest
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNoProblems (TestKey tk)
specifier|private
name|void
name|assertNoProblems
parameter_list|(
name|TestKey
name|tk
parameter_list|)
block|{
name|assertNoProblems
argument_list|(
name|tk
operator|.
name|getPublicKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertProblems (PGPPublicKey k, String first, String... rest)
specifier|private
name|void
name|assertProblems
parameter_list|(
name|PGPPublicKey
name|k
parameter_list|,
name|String
name|first
parameter_list|,
name|String
modifier|...
name|rest
parameter_list|)
block|{
name|CheckResult
name|result
init|=
operator|new
name|PublicKeyChecker
argument_list|()
operator|.
name|setStore
argument_list|(
name|store
argument_list|)
operator|.
name|check
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|list
argument_list|(
name|first
argument_list|,
name|rest
argument_list|)
argument_list|,
name|result
operator|.
name|getProblems
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNoProblems (PGPPublicKey k)
specifier|private
name|void
name|assertNoProblems
parameter_list|(
name|PGPPublicKey
name|k
parameter_list|)
block|{
name|CheckResult
name|result
init|=
operator|new
name|PublicKeyChecker
argument_list|()
operator|.
name|setStore
argument_list|(
name|store
argument_list|)
operator|.
name|check
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|result
operator|.
name|getProblems
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|notTrusted (TestKey k)
specifier|private
specifier|static
name|String
name|notTrusted
parameter_list|(
name|TestKey
name|k
parameter_list|)
block|{
return|return
literal|"Certification by "
operator|+
name|keyToString
argument_list|(
name|k
operator|.
name|getPublicKey
argument_list|()
argument_list|)
operator|+
literal|" is valid, but key is not trusted"
return|;
block|}
DECL|method|parseDate (String str)
specifier|private
specifier|static
name|Date
name|parseDate
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm:ss Z"
argument_list|)
operator|.
name|parse
argument_list|(
name|str
argument_list|)
return|;
block|}
DECL|method|list (String first, String[] rest)
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|list
parameter_list|(
name|String
name|first
parameter_list|,
name|String
index|[]
name|rest
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|all
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|all
operator|.
name|add
argument_list|(
name|first
argument_list|)
expr_stmt|;
name|all
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|rest
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|all
return|;
block|}
block|}
end_class

end_unit

