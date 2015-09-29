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
name|REFS_GPG_KEYS
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
name|keyObjectId
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
name|assertTrue
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
name|testutil
operator|.
name|TestKey
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
name|testutil
operator|.
name|TestKeys
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
name|PGPPublicKeyRingCollection
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
name|junit
operator|.
name|TestRepository
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
name|ObjectReader
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
name|eclipse
operator|.
name|jgit
operator|.
name|notes
operator|.
name|NoteMap
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
name|revwalk
operator|.
name|RevWalk
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
name|Test
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_class
DECL|class|PublicKeyStoreTest
specifier|public
class|class
name|PublicKeyStoreTest
block|{
DECL|field|tr
specifier|private
name|TestRepository
argument_list|<
name|?
argument_list|>
name|tr
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
throws|throws
name|Exception
block|{
name|tr
operator|=
operator|new
name|TestRepository
argument_list|<>
argument_list|(
operator|new
name|InMemoryRepository
argument_list|(
operator|new
name|DfsRepositoryDescription
argument_list|(
literal|"pubkeys"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|=
operator|new
name|PublicKeyStore
argument_list|(
name|tr
operator|.
name|getRepository
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testKeyIdToString ()
specifier|public
name|void
name|testKeyIdToString
parameter_list|()
throws|throws
name|Exception
block|{
name|PGPPublicKey
name|key
init|=
name|TestKeys
operator|.
name|key1
argument_list|()
operator|.
name|getPublicKey
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"46328A8C"
argument_list|,
name|keyIdToString
argument_list|(
name|key
operator|.
name|getKeyID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testKeyToString ()
specifier|public
name|void
name|testKeyToString
parameter_list|()
throws|throws
name|Exception
block|{
name|PGPPublicKey
name|key
init|=
name|TestKeys
operator|.
name|key1
argument_list|()
operator|.
name|getPublicKey
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"46328A8C Testuser One<test1@example.com>"
operator|+
literal|" (04AE A7ED 2F82 1133 E5B1  28D1 ED06 25DC 4632 8A8C)"
argument_list|,
name|keyToString
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testKeyObjectId ()
specifier|public
name|void
name|testKeyObjectId
parameter_list|()
throws|throws
name|Exception
block|{
name|PGPPublicKey
name|key
init|=
name|TestKeys
operator|.
name|key1
argument_list|()
operator|.
name|getPublicKey
argument_list|()
decl_stmt|;
name|String
name|objId
init|=
name|keyObjectId
argument_list|(
name|key
operator|.
name|getKeyID
argument_list|()
argument_list|)
operator|.
name|name
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"ed0625dc46328a8c000000000000000000000000"
argument_list|,
name|objId
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|keyIdToString
argument_list|(
name|key
operator|.
name|getKeyID
argument_list|()
argument_list|)
operator|.
name|toLowerCase
argument_list|()
argument_list|,
name|objId
operator|.
name|substring
argument_list|(
literal|8
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGet ()
specifier|public
name|void
name|testGet
parameter_list|()
throws|throws
name|Exception
block|{
name|TestKey
name|key1
init|=
name|TestKeys
operator|.
name|key1
argument_list|()
decl_stmt|;
name|tr
operator|.
name|branch
argument_list|(
name|REFS_GPG_KEYS
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|add
argument_list|(
name|keyObjectId
argument_list|(
name|key1
operator|.
name|getKeyId
argument_list|()
argument_list|)
operator|.
name|name
argument_list|()
argument_list|,
name|key1
operator|.
name|getPublicKeyArmored
argument_list|()
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|TestKey
name|key2
init|=
name|TestKeys
operator|.
name|key2
argument_list|()
decl_stmt|;
name|tr
operator|.
name|branch
argument_list|(
name|REFS_GPG_KEYS
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|add
argument_list|(
name|keyObjectId
argument_list|(
name|key2
operator|.
name|getKeyId
argument_list|()
argument_list|)
operator|.
name|name
argument_list|()
argument_list|,
name|key2
operator|.
name|getPublicKeyArmored
argument_list|()
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|assertKeys
argument_list|(
name|key1
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|key1
argument_list|)
expr_stmt|;
name|assertKeys
argument_list|(
name|key2
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|key2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetMultiple ()
specifier|public
name|void
name|testGetMultiple
parameter_list|()
throws|throws
name|Exception
block|{
name|TestKey
name|key1
init|=
name|TestKeys
operator|.
name|key1
argument_list|()
decl_stmt|;
name|TestKey
name|key2
init|=
name|TestKeys
operator|.
name|key2
argument_list|()
decl_stmt|;
name|tr
operator|.
name|branch
argument_list|(
name|REFS_GPG_KEYS
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|add
argument_list|(
name|keyObjectId
argument_list|(
name|key1
operator|.
name|getKeyId
argument_list|()
argument_list|)
operator|.
name|name
argument_list|()
argument_list|,
name|key1
operator|.
name|getPublicKeyArmored
argument_list|()
comment|// Mismatched for this key ID, but we can still read it out.
operator|+
name|key2
operator|.
name|getPublicKeyArmored
argument_list|()
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|assertKeys
argument_list|(
name|key1
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|key1
argument_list|,
name|key2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|save ()
specifier|public
name|void
name|save
parameter_list|()
throws|throws
name|Exception
block|{
name|TestKey
name|key1
init|=
name|TestKeys
operator|.
name|key1
argument_list|()
decl_stmt|;
name|TestKey
name|key2
init|=
name|TestKeys
operator|.
name|key2
argument_list|()
decl_stmt|;
name|store
operator|.
name|add
argument_list|(
name|key1
operator|.
name|getPublicKeyRing
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|add
argument_list|(
name|key2
operator|.
name|getPublicKeyRing
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RefUpdate
operator|.
name|Result
operator|.
name|NEW
argument_list|,
name|store
operator|.
name|save
argument_list|(
name|newCommitBuilder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertKeys
argument_list|(
name|key1
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|key1
argument_list|)
expr_stmt|;
name|assertKeys
argument_list|(
name|key2
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|key2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|saveAppendsToExistingList ()
specifier|public
name|void
name|saveAppendsToExistingList
parameter_list|()
throws|throws
name|Exception
block|{
name|TestKey
name|key1
init|=
name|TestKeys
operator|.
name|key1
argument_list|()
decl_stmt|;
name|TestKey
name|key2
init|=
name|TestKeys
operator|.
name|key2
argument_list|()
decl_stmt|;
name|tr
operator|.
name|branch
argument_list|(
name|REFS_GPG_KEYS
argument_list|)
operator|.
name|commit
argument_list|()
comment|// Mismatched for this key ID, but we can still read it out.
operator|.
name|add
argument_list|(
name|keyObjectId
argument_list|(
name|key1
operator|.
name|getKeyId
argument_list|()
argument_list|)
operator|.
name|name
argument_list|()
argument_list|,
name|key2
operator|.
name|getPublicKeyArmored
argument_list|()
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|store
operator|.
name|add
argument_list|(
name|key1
operator|.
name|getPublicKeyRing
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RefUpdate
operator|.
name|Result
operator|.
name|FAST_FORWARD
argument_list|,
name|store
operator|.
name|save
argument_list|(
name|newCommitBuilder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertKeys
argument_list|(
name|key1
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|key1
argument_list|,
name|key2
argument_list|)
expr_stmt|;
try|try
init|(
name|ObjectReader
name|reader
init|=
name|tr
operator|.
name|getRepository
argument_list|()
operator|.
name|newObjectReader
argument_list|()
init|;
name|RevWalk
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|reader
argument_list|)
init|)
block|{
name|NoteMap
name|notes
init|=
name|NoteMap
operator|.
name|read
argument_list|(
name|reader
argument_list|,
name|tr
operator|.
name|getRevWalk
argument_list|()
operator|.
name|parseCommit
argument_list|(
name|tr
operator|.
name|getRepository
argument_list|()
operator|.
name|getRef
argument_list|(
name|REFS_GPG_KEYS
argument_list|)
operator|.
name|getObjectId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|contents
init|=
operator|new
name|String
argument_list|(
name|reader
operator|.
name|open
argument_list|(
name|notes
operator|.
name|get
argument_list|(
name|keyObjectId
argument_list|(
name|key1
operator|.
name|getKeyId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getBytes
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|String
name|header
init|=
literal|"-----BEGIN PGP PUBLIC KEY BLOCK-----"
decl_stmt|;
name|int
name|i1
init|=
name|contents
operator|.
name|indexOf
argument_list|(
name|header
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|i1
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|int
name|i2
init|=
name|contents
operator|.
name|indexOf
argument_list|(
name|header
argument_list|,
name|i1
operator|+
name|header
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|i2
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|updateExisting ()
specifier|public
name|void
name|updateExisting
parameter_list|()
throws|throws
name|Exception
block|{
name|TestKey
name|key5
init|=
name|TestKeys
operator|.
name|key5
argument_list|()
decl_stmt|;
name|PGPPublicKeyRing
name|keyRing
init|=
name|key5
operator|.
name|getPublicKeyRing
argument_list|()
decl_stmt|;
name|PGPPublicKey
name|key
init|=
name|keyRing
operator|.
name|getPublicKey
argument_list|()
decl_stmt|;
name|store
operator|.
name|add
argument_list|(
name|keyRing
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RefUpdate
operator|.
name|Result
operator|.
name|NEW
argument_list|,
name|store
operator|.
name|save
argument_list|(
name|newCommitBuilder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertUserIds
argument_list|(
name|store
operator|.
name|get
argument_list|(
name|key5
operator|.
name|getKeyId
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|,
literal|"Testuser Five<test5@example.com>"
argument_list|,
literal|"foo:myId"
argument_list|)
expr_stmt|;
name|keyRing
operator|=
name|PGPPublicKeyRing
operator|.
name|removePublicKey
argument_list|(
name|keyRing
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|key
operator|=
name|PGPPublicKey
operator|.
name|removeCertification
argument_list|(
name|key
argument_list|,
literal|"foo:myId"
argument_list|)
expr_stmt|;
name|keyRing
operator|=
name|PGPPublicKeyRing
operator|.
name|insertPublicKey
argument_list|(
name|keyRing
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|store
operator|.
name|add
argument_list|(
name|keyRing
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RefUpdate
operator|.
name|Result
operator|.
name|FAST_FORWARD
argument_list|,
name|store
operator|.
name|save
argument_list|(
name|newCommitBuilder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|PGPPublicKeyRing
argument_list|>
name|keyRings
init|=
name|store
operator|.
name|get
argument_list|(
name|key
operator|.
name|getKeyID
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|keyRing
operator|=
name|keyRings
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|keyRings
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertUserIds
argument_list|(
name|keyRing
argument_list|,
literal|"Testuser Five<test5@example.com>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|remove ()
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|Exception
block|{
name|TestKey
name|key1
init|=
name|TestKeys
operator|.
name|key1
argument_list|()
decl_stmt|;
name|store
operator|.
name|add
argument_list|(
name|key1
operator|.
name|getPublicKeyRing
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RefUpdate
operator|.
name|Result
operator|.
name|NEW
argument_list|,
name|store
operator|.
name|save
argument_list|(
name|newCommitBuilder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertKeys
argument_list|(
name|key1
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|key1
argument_list|)
expr_stmt|;
name|store
operator|.
name|remove
argument_list|(
name|key1
operator|.
name|getPublicKey
argument_list|()
operator|.
name|getFingerprint
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RefUpdate
operator|.
name|Result
operator|.
name|FAST_FORWARD
argument_list|,
name|store
operator|.
name|save
argument_list|(
name|newCommitBuilder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertKeys
argument_list|(
name|key1
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|removeNonexisting ()
specifier|public
name|void
name|removeNonexisting
parameter_list|()
throws|throws
name|Exception
block|{
name|TestKey
name|key1
init|=
name|TestKeys
operator|.
name|key1
argument_list|()
decl_stmt|;
name|store
operator|.
name|add
argument_list|(
name|key1
operator|.
name|getPublicKeyRing
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RefUpdate
operator|.
name|Result
operator|.
name|NEW
argument_list|,
name|store
operator|.
name|save
argument_list|(
name|newCommitBuilder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|TestKey
name|key2
init|=
name|TestKeys
operator|.
name|key2
argument_list|()
decl_stmt|;
name|store
operator|.
name|remove
argument_list|(
name|key2
operator|.
name|getPublicKey
argument_list|()
operator|.
name|getFingerprint
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RefUpdate
operator|.
name|Result
operator|.
name|NO_CHANGE
argument_list|,
name|store
operator|.
name|save
argument_list|(
name|newCommitBuilder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertKeys
argument_list|(
name|key1
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|key1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addThenRemove ()
specifier|public
name|void
name|addThenRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|TestKey
name|key1
init|=
name|TestKeys
operator|.
name|key1
argument_list|()
decl_stmt|;
name|store
operator|.
name|add
argument_list|(
name|key1
operator|.
name|getPublicKeyRing
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|remove
argument_list|(
name|key1
operator|.
name|getPublicKey
argument_list|()
operator|.
name|getFingerprint
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RefUpdate
operator|.
name|Result
operator|.
name|NO_CHANGE
argument_list|,
name|store
operator|.
name|save
argument_list|(
name|newCommitBuilder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertKeys
argument_list|(
name|key1
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertKeys (long keyId, TestKey... expected)
specifier|private
name|void
name|assertKeys
parameter_list|(
name|long
name|keyId
parameter_list|,
name|TestKey
modifier|...
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|expectedStrings
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|TestKey
name|k
range|:
name|expected
control|)
block|{
name|expectedStrings
operator|.
name|add
argument_list|(
name|keyToString
argument_list|(
name|k
operator|.
name|getPublicKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|PGPPublicKeyRingCollection
name|actual
init|=
name|store
operator|.
name|get
argument_list|(
name|keyId
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|actualStrings
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|PGPPublicKeyRing
name|k
range|:
name|actual
control|)
block|{
name|actualStrings
operator|.
name|add
argument_list|(
name|keyToString
argument_list|(
name|k
operator|.
name|getPublicKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedStrings
argument_list|,
name|actualStrings
argument_list|)
expr_stmt|;
block|}
DECL|method|assertUserIds (PGPPublicKeyRing keyRing, String... expected)
specifier|private
name|void
name|assertUserIds
parameter_list|(
name|PGPPublicKeyRing
name|keyRing
parameter_list|,
name|String
modifier|...
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|actual
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
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
name|store
operator|.
name|get
argument_list|(
name|keyRing
operator|.
name|getPublicKey
argument_list|()
operator|.
name|getKeyID
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getPublicKey
argument_list|()
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
name|actual
operator|.
name|add
argument_list|(
name|userIds
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|actual
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|newCommitBuilder ()
specifier|private
name|CommitBuilder
name|newCommitBuilder
parameter_list|()
block|{
name|CommitBuilder
name|cb
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
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
return|return
name|cb
return|;
block|}
block|}
end_class

end_unit

