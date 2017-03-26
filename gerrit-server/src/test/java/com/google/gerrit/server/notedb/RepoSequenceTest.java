begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.notedb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|notedb
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Constants
operator|.
name|OBJ_BLOB
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
name|fail
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|rholder
operator|.
name|retry
operator|.
name|BlockStrategy
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|rholder
operator|.
name|retry
operator|.
name|Retryer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|rholder
operator|.
name|retry
operator|.
name|RetryerBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|rholder
operator|.
name|retry
operator|.
name|StopStrategies
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
name|util
operator|.
name|concurrent
operator|.
name|Runnables
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
name|Project
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
name|RefNames
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
name|testutil
operator|.
name|InMemoryRepositoryManager
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
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|IncorrectObjectTypeException
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
name|ObjectInserter
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
name|lib
operator|.
name|Repository
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
DECL|class|RepoSequenceTest
specifier|public
class|class
name|RepoSequenceTest
block|{
DECL|field|RETRYER
specifier|private
specifier|static
specifier|final
name|Retryer
argument_list|<
name|RefUpdate
operator|.
name|Result
argument_list|>
name|RETRYER
init|=
name|RepoSequence
operator|.
name|retryerBuilder
argument_list|()
operator|.
name|withBlockStrategy
argument_list|(
operator|new
name|BlockStrategy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|block
parameter_list|(
name|long
name|sleepTime
parameter_list|)
block|{
comment|// Don't sleep in tests.
block|}
block|}
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|exception
annotation|@
name|Rule
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
DECL|field|repoManager
specifier|private
name|InMemoryRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|project
specifier|private
name|Project
operator|.
name|NameKey
name|project
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
name|repoManager
operator|=
operator|new
name|InMemoryRepositoryManager
argument_list|()
expr_stmt|;
name|project
operator|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"project"
argument_list|)
expr_stmt|;
name|repoManager
operator|.
name|createRepository
argument_list|(
name|project
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|oneCaller ()
specifier|public
name|void
name|oneCaller
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|max
init|=
literal|20
decl_stmt|;
for|for
control|(
name|int
name|batchSize
init|=
literal|1
init|;
name|batchSize
operator|<=
literal|10
condition|;
name|batchSize
operator|++
control|)
block|{
name|String
name|name
init|=
literal|"batch-size-"
operator|+
name|batchSize
decl_stmt|;
name|RepoSequence
name|s
init|=
name|newSequence
argument_list|(
name|name
argument_list|,
literal|1
argument_list|,
name|batchSize
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|max
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|named
argument_list|(
literal|"i="
operator|+
name|i
operator|+
literal|" for "
operator|+
name|name
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"failed batchSize="
operator|+
name|batchSize
operator|+
literal|", i="
operator|+
name|i
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|named
argument_list|(
literal|"acquireCount for "
operator|+
name|name
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|divCeil
argument_list|(
name|max
argument_list|,
name|batchSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|oneCallerNoLoop ()
specifier|public
name|void
name|oneCallerNoLoop
parameter_list|()
throws|throws
name|Exception
block|{
name|RepoSequence
name|s
init|=
name|newSequence
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|9
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|twoCallers ()
specifier|public
name|void
name|twoCallers
parameter_list|()
throws|throws
name|Exception
block|{
name|RepoSequence
name|s1
init|=
name|newSequence
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|RepoSequence
name|s2
init|=
name|newSequence
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
comment|// s1 acquires 1-3; s2 acquires 4-6.
name|assertThat
argument_list|(
name|s1
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s2
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s1
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s2
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s1
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s2
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|6
argument_list|)
expr_stmt|;
comment|// s2 acquires 7-9; s1 acquires 10-12.
name|assertThat
argument_list|(
name|s2
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s1
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s2
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s1
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|11
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s2
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|9
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s1
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|12
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|populateEmptyRefWithStartValue ()
specifier|public
name|void
name|populateEmptyRefWithStartValue
parameter_list|()
throws|throws
name|Exception
block|{
name|RepoSequence
name|s
init|=
name|newSequence
argument_list|(
literal|"id"
argument_list|,
literal|1234
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1234
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|readBlob
argument_list|(
literal|"id"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"1244"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|startIsIgnoredIfRefIsPresent ()
specifier|public
name|void
name|startIsIgnoredIfRefIsPresent
parameter_list|()
throws|throws
name|Exception
block|{
name|writeBlob
argument_list|(
literal|"id"
argument_list|,
literal|"1234"
argument_list|)
expr_stmt|;
name|RepoSequence
name|s
init|=
name|newSequence
argument_list|(
literal|"id"
argument_list|,
literal|3456
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1234
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|readBlob
argument_list|(
literal|"id"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"1244"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|retryOnLockFailure ()
specifier|public
name|void
name|retryOnLockFailure
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Seed existing ref value.
name|writeBlob
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|AtomicBoolean
name|doneBgUpdate
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Runnable
name|bgUpdate
init|=
parameter_list|()
lambda|->
block|{
if|if
condition|(
operator|!
name|doneBgUpdate
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
condition|)
block|{
name|writeBlob
argument_list|(
literal|"id"
argument_list|,
literal|"1234"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|RepoSequence
name|s
init|=
name|newSequence
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
name|bgUpdate
argument_list|,
name|RETRYER
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doneBgUpdate
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1234
argument_list|)
expr_stmt|;
comment|// Single acquire call that results in 2 ref reads.
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doneBgUpdate
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|failOnInvalidValue ()
specifier|public
name|void
name|failOnInvalidValue
parameter_list|()
throws|throws
name|Exception
block|{
name|ObjectId
name|id
init|=
name|writeBlob
argument_list|(
literal|"id"
argument_list|,
literal|"not a number"
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|OrmException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"invalid value in refs/sequences/id blob at "
operator|+
name|id
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|newSequence
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|failOnWrongType ()
specifier|public
name|void
name|failOnWrongType
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
init|)
block|{
name|TestRepository
argument_list|<
name|Repository
argument_list|>
name|tr
init|=
operator|new
name|TestRepository
argument_list|<>
argument_list|(
name|repo
argument_list|)
decl_stmt|;
name|tr
operator|.
name|branch
argument_list|(
name|RefNames
operator|.
name|REFS_SEQUENCES
operator|+
literal|"id"
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|create
argument_list|()
expr_stmt|;
try|try
block|{
name|newSequence
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
operator|.
name|next
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
operator|.
name|isInstanceOf
argument_list|(
name|ExecutionException
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
argument_list|)
operator|.
name|isInstanceOf
argument_list|(
name|IncorrectObjectTypeException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|failAfterRetryerGivesUp ()
specifier|public
name|void
name|failAfterRetryerGivesUp
parameter_list|()
throws|throws
name|Exception
block|{
name|AtomicInteger
name|bgCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1234
argument_list|)
decl_stmt|;
name|Runnable
name|bgUpdate
init|=
parameter_list|()
lambda|->
block|{
name|writeBlob
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|bgCounter
operator|.
name|getAndAdd
argument_list|(
literal|1000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
decl_stmt|;
name|RepoSequence
name|s
init|=
name|newSequence
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|,
name|bgUpdate
argument_list|,
name|RetryerBuilder
operator|.
expr|<
name|RefUpdate
operator|.
name|Result
operator|>
name|newBuilder
argument_list|()
operator|.
name|withStopStrategy
argument_list|(
name|StopStrategies
operator|.
name|stopAfterAttempt
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|OrmException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"failed to update refs/sequences/id: LOCK_FAILURE"
argument_list|)
expr_stmt|;
name|s
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|nextWithCountOneCaller ()
specifier|public
name|void
name|nextWithCountOneCaller
parameter_list|()
throws|throws
name|Exception
block|{
name|RepoSequence
name|s
init|=
name|newSequence
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|5
argument_list|,
literal|6
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|7
argument_list|,
literal|8
argument_list|,
literal|9
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|10
argument_list|,
literal|11
argument_list|,
literal|12
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|13
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|(
literal|7
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|16
argument_list|,
literal|17
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|,
literal|20
argument_list|,
literal|21
argument_list|,
literal|22
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|(
literal|7
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|23
argument_list|,
literal|24
argument_list|,
literal|25
argument_list|,
literal|26
argument_list|,
literal|27
argument_list|,
literal|28
argument_list|,
literal|29
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|next
argument_list|(
literal|7
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|30
argument_list|,
literal|31
argument_list|,
literal|32
argument_list|,
literal|33
argument_list|,
literal|34
argument_list|,
literal|35
argument_list|,
literal|36
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|s
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|8
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|nextWithCountMultipleCallers ()
specifier|public
name|void
name|nextWithCountMultipleCallers
parameter_list|()
throws|throws
name|Exception
block|{
name|RepoSequence
name|s1
init|=
name|newSequence
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|RepoSequence
name|s2
init|=
name|newSequence
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|s1
operator|.
name|next
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|s1
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// s1 hasn't exhausted its last batch.
name|assertThat
argument_list|(
name|s2
operator|.
name|next
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|4
argument_list|,
literal|5
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|s2
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// s1 acquires again to cover this request, plus a whole new batch.
name|assertThat
argument_list|(
name|s1
operator|.
name|next
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|3
argument_list|,
literal|8
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s1
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// s2 hasn't exhausted its last batch, do so now.
name|assertThat
argument_list|(
name|s2
operator|.
name|next
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|6
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|s2
operator|.
name|acquireCount
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|newSequence (String name, int start, int batchSize)
specifier|private
name|RepoSequence
name|newSequence
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|batchSize
parameter_list|)
block|{
return|return
name|newSequence
argument_list|(
name|name
argument_list|,
name|start
argument_list|,
name|batchSize
argument_list|,
name|Runnables
operator|.
name|doNothing
argument_list|()
argument_list|,
name|RETRYER
argument_list|)
return|;
block|}
DECL|method|newSequence ( String name, final int start, int batchSize, Runnable afterReadRef, Retryer<RefUpdate.Result> retryer)
specifier|private
name|RepoSequence
name|newSequence
parameter_list|(
name|String
name|name
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
name|int
name|batchSize
parameter_list|,
name|Runnable
name|afterReadRef
parameter_list|,
name|Retryer
argument_list|<
name|RefUpdate
operator|.
name|Result
argument_list|>
name|retryer
parameter_list|)
block|{
return|return
operator|new
name|RepoSequence
argument_list|(
name|repoManager
argument_list|,
name|project
argument_list|,
name|name
argument_list|,
operator|new
name|RepoSequence
operator|.
name|Seed
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|get
parameter_list|()
block|{
return|return
name|start
return|;
block|}
block|}
argument_list|,
name|batchSize
argument_list|,
name|afterReadRef
argument_list|,
name|retryer
argument_list|)
return|;
block|}
DECL|method|writeBlob (String sequenceName, String value)
specifier|private
name|ObjectId
name|writeBlob
parameter_list|(
name|String
name|sequenceName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|String
name|refName
init|=
name|RefNames
operator|.
name|REFS_SEQUENCES
operator|+
name|sequenceName
decl_stmt|;
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
init|;
name|ObjectInserter
name|ins
operator|=
name|repo
operator|.
name|newObjectInserter
argument_list|()
init|)
block|{
name|ObjectId
name|newId
init|=
name|ins
operator|.
name|insert
argument_list|(
name|OBJ_BLOB
argument_list|,
name|value
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|ins
operator|.
name|flush
argument_list|()
expr_stmt|;
name|RefUpdate
name|ru
init|=
name|repo
operator|.
name|updateRef
argument_list|(
name|refName
argument_list|)
decl_stmt|;
name|ru
operator|.
name|setNewObjectId
argument_list|(
name|newId
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ru
operator|.
name|forceUpdate
argument_list|()
argument_list|)
operator|.
name|isAnyOf
argument_list|(
name|RefUpdate
operator|.
name|Result
operator|.
name|NEW
argument_list|,
name|RefUpdate
operator|.
name|Result
operator|.
name|FORCED
argument_list|)
expr_stmt|;
return|return
name|newId
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|readBlob (String sequenceName)
specifier|private
name|String
name|readBlob
parameter_list|(
name|String
name|sequenceName
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|refName
init|=
name|RefNames
operator|.
name|REFS_SEQUENCES
operator|+
name|sequenceName
decl_stmt|;
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
init|;
name|RevWalk
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
init|)
block|{
name|ObjectId
name|id
init|=
name|repo
operator|.
name|exactRef
argument_list|(
name|refName
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|rw
operator|.
name|getObjectReader
argument_list|()
operator|.
name|open
argument_list|(
name|id
argument_list|)
operator|.
name|getCachedBytes
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
return|;
block|}
block|}
DECL|method|divCeil (float a, float b)
specifier|private
specifier|static
name|long
name|divCeil
parameter_list|(
name|float
name|a
parameter_list|,
name|float
name|b
parameter_list|)
block|{
return|return
name|Math
operator|.
name|round
argument_list|(
name|Math
operator|.
name|ceil
argument_list|(
name|a
operator|/
name|b
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

