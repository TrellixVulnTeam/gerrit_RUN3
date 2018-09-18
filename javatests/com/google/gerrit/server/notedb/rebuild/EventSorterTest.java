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
DECL|package|com.google.gerrit.server.notedb.rebuild
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
operator|.
name|rebuild
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
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Collections2
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
name|Change
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
name|PatchSet
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
name|notedb
operator|.
name|ChangeUpdate
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
name|time
operator|.
name|TimeUtil
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
name|testing
operator|.
name|TestTimeUtil
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
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
name|List
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
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

begin_class
DECL|class|EventSorterTest
specifier|public
class|class
name|EventSorterTest
block|{
DECL|class|TestEvent
specifier|private
class|class
name|TestEvent
extends|extends
name|Event
block|{
DECL|method|TestEvent (Timestamp when)
specifier|protected
name|TestEvent
parameter_list|(
name|Timestamp
name|when
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|1000
argument_list|)
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|1000
argument_list|)
argument_list|,
name|when
argument_list|,
name|changeCreatedOn
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|uniquePerUpdate ()
name|boolean
name|uniquePerUpdate
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|apply (ChangeUpdate update)
name|void
name|apply
parameter_list|(
name|ChangeUpdate
name|update
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"E{"
operator|+
name|when
operator|.
name|getSeconds
argument_list|()
operator|+
literal|'}'
return|;
block|}
block|}
DECL|field|changeCreatedOn
specifier|private
name|Timestamp
name|changeCreatedOn
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|TestTimeUtil
operator|.
name|resetWithClockStep
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|changeCreatedOn
operator|=
name|TimeUtil
operator|.
name|nowTs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|naturalSort ()
specifier|public
name|void
name|naturalSort
parameter_list|()
block|{
name|Event
name|e1
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|Event
name|e2
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|Event
name|e3
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|Event
argument_list|>
name|events
range|:
name|Collections2
operator|.
name|permutations
argument_list|(
name|events
argument_list|(
name|e1
argument_list|,
name|e2
argument_list|,
name|e3
argument_list|)
argument_list|)
control|)
block|{
name|assertSorted
argument_list|(
name|events
argument_list|,
name|events
argument_list|(
name|e1
argument_list|,
name|e2
argument_list|,
name|e3
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|topoSortOneDep ()
specifier|public
name|void
name|topoSortOneDep
parameter_list|()
block|{
name|List
argument_list|<
name|Event
argument_list|>
name|es
decl_stmt|;
comment|// Input list is 0,1,2
comment|// 0 depends on 1 => 1,0,2
name|es
operator|=
name|threeEventsOneDep
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|es
argument_list|,
name|events
argument_list|(
name|es
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// 1 depends on 0 => 0,1,2
name|es
operator|=
name|threeEventsOneDep
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|es
argument_list|,
name|events
argument_list|(
name|es
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// 0 depends on 2 => 1,2,0
name|es
operator|=
name|threeEventsOneDep
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|es
argument_list|,
name|events
argument_list|(
name|es
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// 2 depends on 0 => 0,1,2
name|es
operator|=
name|threeEventsOneDep
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|es
argument_list|,
name|events
argument_list|(
name|es
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// 1 depends on 2 => 0,2,1
name|es
operator|=
name|threeEventsOneDep
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|es
argument_list|,
name|events
argument_list|(
name|es
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// 2 depends on 1 => 0,1,2
name|es
operator|=
name|threeEventsOneDep
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|es
argument_list|,
name|events
argument_list|(
name|es
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|threeEventsOneDep (int depFromIdx, int depOnIdx)
specifier|private
name|List
argument_list|<
name|Event
argument_list|>
name|threeEventsOneDep
parameter_list|(
name|int
name|depFromIdx
parameter_list|,
name|int
name|depOnIdx
parameter_list|)
block|{
name|List
argument_list|<
name|Event
argument_list|>
name|events
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
argument_list|,
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
argument_list|,
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|events
operator|.
name|get
argument_list|(
name|depFromIdx
argument_list|)
operator|.
name|addDep
argument_list|(
name|events
operator|.
name|get
argument_list|(
name|depOnIdx
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|events
return|;
block|}
annotation|@
name|Test
DECL|method|lastEventDependsOnFirstEvent ()
specifier|public
name|void
name|lastEventDependsOnFirstEvent
parameter_list|()
block|{
name|List
argument_list|<
name|Event
argument_list|>
name|events
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|events
operator|.
name|add
argument_list|(
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|events
operator|.
name|get
argument_list|(
name|events
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|addDep
argument_list|(
name|events
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|events
argument_list|,
name|events
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|firstEventDependsOnLastEvent ()
specifier|public
name|void
name|firstEventDependsOnLastEvent
parameter_list|()
block|{
name|List
argument_list|<
name|Event
argument_list|>
name|events
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|events
operator|.
name|add
argument_list|(
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|events
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|addDep
argument_list|(
name|events
operator|.
name|get
argument_list|(
name|events
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Event
argument_list|>
name|expected
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|expected
operator|.
name|addAll
argument_list|(
name|events
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|events
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
name|events
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|events
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|topoSortChainOfDeps ()
specifier|public
name|void
name|topoSortChainOfDeps
parameter_list|()
block|{
name|Event
name|e1
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|Event
name|e2
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|Event
name|e3
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|Event
name|e4
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|e1
operator|.
name|addDep
argument_list|(
name|e2
argument_list|)
expr_stmt|;
name|e2
operator|.
name|addDep
argument_list|(
name|e3
argument_list|)
expr_stmt|;
name|e3
operator|.
name|addDep
argument_list|(
name|e4
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|events
argument_list|(
name|e1
argument_list|,
name|e2
argument_list|,
name|e3
argument_list|,
name|e4
argument_list|)
argument_list|,
name|events
argument_list|(
name|e4
argument_list|,
name|e3
argument_list|,
name|e2
argument_list|,
name|e1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|topoSortMultipleDeps ()
specifier|public
name|void
name|topoSortMultipleDeps
parameter_list|()
block|{
name|Event
name|e1
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|Event
name|e2
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|Event
name|e3
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|Event
name|e4
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|e1
operator|.
name|addDep
argument_list|(
name|e2
argument_list|)
expr_stmt|;
name|e1
operator|.
name|addDep
argument_list|(
name|e4
argument_list|)
expr_stmt|;
name|e2
operator|.
name|addDep
argument_list|(
name|e3
argument_list|)
expr_stmt|;
comment|// Processing 3 pops 2, processing 4 pops 1.
name|assertSorted
argument_list|(
name|events
argument_list|(
name|e2
argument_list|,
name|e3
argument_list|,
name|e1
argument_list|,
name|e4
argument_list|)
argument_list|,
name|events
argument_list|(
name|e3
argument_list|,
name|e2
argument_list|,
name|e4
argument_list|,
name|e1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|topoSortMultipleDepsPreservesNaturalOrder ()
specifier|public
name|void
name|topoSortMultipleDepsPreservesNaturalOrder
parameter_list|()
block|{
name|Event
name|e1
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|Event
name|e2
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|Event
name|e3
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|Event
name|e4
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|e1
operator|.
name|addDep
argument_list|(
name|e4
argument_list|)
expr_stmt|;
name|e2
operator|.
name|addDep
argument_list|(
name|e4
argument_list|)
expr_stmt|;
name|e3
operator|.
name|addDep
argument_list|(
name|e4
argument_list|)
expr_stmt|;
comment|// Processing 4 pops 1, 2, 3 in natural order.
name|assertSorted
argument_list|(
name|events
argument_list|(
name|e4
argument_list|,
name|e3
argument_list|,
name|e2
argument_list|,
name|e1
argument_list|)
argument_list|,
name|events
argument_list|(
name|e4
argument_list|,
name|e1
argument_list|,
name|e2
argument_list|,
name|e3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|topoSortCycle ()
specifier|public
name|void
name|topoSortCycle
parameter_list|()
block|{
name|Event
name|e1
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|Event
name|e2
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
comment|// Implementation is not really defined, but infinite looping would be bad.
comment|// According to current implementation details, 2 pops 1, 1 pops 2 which was
comment|// already seen.
name|assertSorted
argument_list|(
name|events
argument_list|(
name|e2
argument_list|,
name|e1
argument_list|)
argument_list|,
name|events
argument_list|(
name|e1
argument_list|,
name|e2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|topoSortDepNotInInputList ()
specifier|public
name|void
name|topoSortDepNotInInputList
parameter_list|()
block|{
name|Event
name|e1
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|Event
name|e2
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|Event
name|e3
init|=
operator|new
name|TestEvent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|e1
operator|.
name|addDep
argument_list|(
name|e3
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Event
argument_list|>
name|events
init|=
name|events
argument_list|(
name|e2
argument_list|,
name|e1
argument_list|)
decl_stmt|;
try|try
block|{
operator|new
name|EventSorter
argument_list|(
name|events
argument_list|)
operator|.
name|sort
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// Expected.
block|}
block|}
DECL|method|events (Event... es)
specifier|private
specifier|static
name|List
argument_list|<
name|Event
argument_list|>
name|events
parameter_list|(
name|Event
modifier|...
name|es
parameter_list|)
block|{
return|return
name|Lists
operator|.
name|newArrayList
argument_list|(
name|es
argument_list|)
return|;
block|}
DECL|method|events (List<Event> in, Integer... indexes)
specifier|private
specifier|static
name|List
argument_list|<
name|Event
argument_list|>
name|events
parameter_list|(
name|List
argument_list|<
name|Event
argument_list|>
name|in
parameter_list|,
name|Integer
modifier|...
name|indexes
parameter_list|)
block|{
return|return
name|Stream
operator|.
name|of
argument_list|(
name|indexes
argument_list|)
operator|.
name|map
argument_list|(
name|in
operator|::
name|get
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
return|;
block|}
DECL|method|assertSorted (List<Event> unsorted, List<Event> expected)
specifier|private
specifier|static
name|void
name|assertSorted
parameter_list|(
name|List
argument_list|<
name|Event
argument_list|>
name|unsorted
parameter_list|,
name|List
argument_list|<
name|Event
argument_list|>
name|expected
parameter_list|)
block|{
name|List
argument_list|<
name|Event
argument_list|>
name|actual
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|unsorted
argument_list|)
decl_stmt|;
operator|new
name|EventSorter
argument_list|(
name|actual
argument_list|)
operator|.
name|sort
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|actual
argument_list|)
operator|.
name|named
argument_list|(
literal|"sorted"
operator|+
name|unsorted
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

