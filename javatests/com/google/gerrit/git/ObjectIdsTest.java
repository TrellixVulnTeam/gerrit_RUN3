begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2019 The Android Open Source Project
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
DECL|package|com.google.gerrit.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|git
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assert_
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
name|git
operator|.
name|ObjectIds
operator|.
name|abbreviateName
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
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
name|AnyObjectId
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
name|ObjectId
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
name|revwalk
operator|.
name|RevBlob
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

begin_class
DECL|class|ObjectIdsTest
specifier|public
class|class
name|ObjectIdsTest
block|{
DECL|field|ID
specifier|private
specifier|static
specifier|final
name|ObjectId
name|ID
init|=
name|ObjectId
operator|.
name|fromString
argument_list|(
literal|"0000000000100000000000000000000000000000"
argument_list|)
decl_stmt|;
DECL|field|AMBIGUOUS_BLOB_ID
specifier|private
specifier|static
specifier|final
name|ObjectId
name|AMBIGUOUS_BLOB_ID
init|=
name|ObjectId
operator|.
name|fromString
argument_list|(
literal|"0000000000b36b6aa7ea4b75318ed078f55505c3"
argument_list|)
decl_stmt|;
DECL|field|AMBIGUOUS_TREE_ID
specifier|private
specifier|static
specifier|final
name|ObjectId
name|AMBIGUOUS_TREE_ID
init|=
name|ObjectId
operator|.
name|fromString
argument_list|(
literal|"0000000000cdcf04beb2fab69e65622616294984"
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|abbreviateNameDefaultLength ()
specifier|public
name|void
name|abbreviateNameDefaultLength
parameter_list|()
throws|throws
name|Exception
block|{
name|assertRuntimeException
argument_list|(
parameter_list|()
lambda|->
name|abbreviateName
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|abbreviateName
argument_list|(
name|ID
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"0000000"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|abbreviateName
argument_list|(
name|AMBIGUOUS_BLOB_ID
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|abbreviateName
argument_list|(
name|ID
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|abbreviateName
argument_list|(
name|AMBIGUOUS_TREE_ID
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|abbreviateName
argument_list|(
name|ID
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|abbreviateNameCustomLength ()
specifier|public
name|void
name|abbreviateNameCustomLength
parameter_list|()
throws|throws
name|Exception
block|{
name|assertRuntimeException
argument_list|(
parameter_list|()
lambda|->
name|abbreviateName
argument_list|(
literal|null
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertRuntimeException
argument_list|(
parameter_list|()
lambda|->
name|abbreviateName
argument_list|(
name|ID
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertRuntimeException
argument_list|(
parameter_list|()
lambda|->
name|abbreviateName
argument_list|(
name|ID
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertRuntimeException
argument_list|(
parameter_list|()
lambda|->
name|abbreviateName
argument_list|(
name|ID
argument_list|,
literal|41
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|abbreviateName
argument_list|(
name|ID
argument_list|,
literal|5
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"00000"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|abbreviateName
argument_list|(
name|ID
argument_list|,
literal|40
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ID
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|abbreviateNameDefaultLengthWithReader ()
specifier|public
name|void
name|abbreviateNameDefaultLengthWithReader
parameter_list|()
throws|throws
name|Exception
block|{
name|assertRuntimeException
argument_list|(
parameter_list|()
lambda|->
name|abbreviateName
argument_list|(
name|ID
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|ObjectReader
name|reader
init|=
name|newReaderWithAmbiguousIds
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|abbreviateName
argument_list|(
name|ID
argument_list|,
name|reader
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"00000000001"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|abbreviateNameCustomLengthWithReader ()
specifier|public
name|void
name|abbreviateNameCustomLengthWithReader
parameter_list|()
throws|throws
name|Exception
block|{
name|ObjectReader
name|reader
init|=
name|newReaderWithAmbiguousIds
argument_list|()
decl_stmt|;
name|assertRuntimeException
argument_list|(
parameter_list|()
lambda|->
name|abbreviateName
argument_list|(
name|ID
argument_list|,
operator|-
literal|1
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
name|assertRuntimeException
argument_list|(
parameter_list|()
lambda|->
name|abbreviateName
argument_list|(
name|ID
argument_list|,
literal|0
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
name|assertRuntimeException
argument_list|(
parameter_list|()
lambda|->
name|abbreviateName
argument_list|(
name|ID
argument_list|,
literal|41
argument_list|,
name|reader
argument_list|)
argument_list|)
expr_stmt|;
name|assertRuntimeException
argument_list|(
parameter_list|()
lambda|->
name|abbreviateName
argument_list|(
name|ID
argument_list|,
literal|5
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|shortest
init|=
literal|"00000000001"
decl_stmt|;
name|assertThat
argument_list|(
name|abbreviateName
argument_list|(
name|ID
argument_list|,
literal|1
argument_list|,
name|reader
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|shortest
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|abbreviateName
argument_list|(
name|ID
argument_list|,
literal|7
argument_list|,
name|reader
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|shortest
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|abbreviateName
argument_list|(
name|ID
argument_list|,
name|shortest
operator|.
name|length
argument_list|()
argument_list|,
name|reader
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|shortest
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|abbreviateName
argument_list|(
name|ID
argument_list|,
name|shortest
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|,
name|reader
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"000000000010"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|copyOrNull ()
specifier|public
name|void
name|copyOrNull
parameter_list|()
throws|throws
name|Exception
block|{
name|testCopy
argument_list|(
name|ObjectIds
operator|::
name|copyOrNull
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ObjectIds
operator|.
name|copyOrNull
argument_list|(
literal|null
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|copyOrZero ()
specifier|public
name|void
name|copyOrZero
parameter_list|()
throws|throws
name|Exception
block|{
name|testCopy
argument_list|(
name|ObjectIds
operator|::
name|copyOrZero
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ObjectIds
operator|.
name|copyOrZero
argument_list|(
literal|null
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCopy (Function<AnyObjectId, ObjectId> copyFunc)
specifier|private
name|void
name|testCopy
parameter_list|(
name|Function
argument_list|<
name|AnyObjectId
argument_list|,
name|ObjectId
argument_list|>
name|copyFunc
parameter_list|)
block|{
name|MyObjectId
name|myId
init|=
operator|new
name|MyObjectId
argument_list|(
name|ID
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|myId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ID
argument_list|)
expr_stmt|;
name|ObjectId
name|copy
init|=
name|copyFunc
operator|.
name|apply
argument_list|(
name|myId
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|copy
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|myId
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|copy
argument_list|)
operator|.
name|isNotSameInstanceAs
argument_list|(
name|myId
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|copy
operator|.
name|getClass
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ObjectId
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|FunctionalInterface
DECL|interface|Func
specifier|private
interface|interface
name|Func
block|{
DECL|method|call ()
name|void
name|call
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
DECL|method|assertRuntimeException (Func func)
specifier|private
specifier|static
name|void
name|assertRuntimeException
parameter_list|(
name|Func
name|func
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|func
operator|.
name|call
argument_list|()
expr_stmt|;
name|assert_
argument_list|()
operator|.
name|fail
argument_list|(
literal|"Expected RuntimeException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// Expected.
block|}
block|}
DECL|method|newReaderWithAmbiguousIds ()
specifier|private
specifier|static
name|ObjectReader
name|newReaderWithAmbiguousIds
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Recipe for creating ambiguous IDs courtesy of git core:
comment|// https://github.com/git/git/blob/df799f5d99ac51d4fc791d546de3f936088582fc/t/t1512-rev-parse-disambiguation.sh
name|TestRepository
argument_list|<
name|?
argument_list|>
name|tr
init|=
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
literal|"repo"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|blobData
init|=
literal|"0\n1\n2\n3\n4\n5\n6\n7\n8\n9\n\nb1rwzyc3\n"
decl_stmt|;
name|RevBlob
name|blob
init|=
name|tr
operator|.
name|blob
argument_list|(
name|blobData
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|blob
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|AMBIGUOUS_BLOB_ID
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tr
operator|.
name|tree
argument_list|(
name|tr
operator|.
name|file
argument_list|(
literal|"a0blgqsjc"
argument_list|,
name|blob
argument_list|)
argument_list|)
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|AMBIGUOUS_TREE_ID
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|tr
operator|.
name|getRevWalk
argument_list|()
operator|.
name|getObjectReader
argument_list|()
return|;
block|}
DECL|class|MyObjectId
specifier|private
specifier|static
class|class
name|MyObjectId
extends|extends
name|ObjectId
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|MyObjectId (AnyObjectId src)
name|MyObjectId
parameter_list|(
name|AnyObjectId
name|src
parameter_list|)
block|{
name|super
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

