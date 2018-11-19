begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.logging
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|logging
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Expect
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
name|GerritBaseTests
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
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
name|ExecutorService
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
name|Executors
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

begin_class
DECL|class|LoggingContextAwareExecutorServiceTest
specifier|public
class|class
name|LoggingContextAwareExecutorServiceTest
extends|extends
name|GerritBaseTests
block|{
DECL|field|expect
annotation|@
name|Rule
specifier|public
specifier|final
name|Expect
name|expect
init|=
name|Expect
operator|.
name|create
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|loggingContextPropagationToBackgroundThread ()
specifier|public
name|void
name|loggingContextPropagationToBackgroundThread
parameter_list|()
throws|throws
name|Exception
block|{
name|assertThat
argument_list|(
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|getTags
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertForceLogging
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
init|(
name|TraceContext
name|traceContext
init|=
name|TraceContext
operator|.
name|open
argument_list|()
operator|.
name|forceLogging
argument_list|()
operator|.
name|addTag
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
init|)
block|{
name|SortedMap
argument_list|<
name|String
argument_list|,
name|SortedSet
argument_list|<
name|Object
argument_list|>
argument_list|>
name|tagMap
init|=
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|getTags
argument_list|()
operator|.
name|asMap
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|tagMap
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tagMap
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|assertForceLogging
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ExecutorService
name|executor
init|=
operator|new
name|LoggingContextAwareExecutorService
argument_list|(
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|executor
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
block|{
comment|// Verify that the tags and force logging flag have been propagated to the new
comment|// thread.
name|SortedMap
argument_list|<
name|String
argument_list|,
name|SortedSet
argument_list|<
name|Object
argument_list|>
argument_list|>
name|threadTagMap
init|=
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|getTags
argument_list|()
operator|.
name|asMap
argument_list|()
decl_stmt|;
name|expect
operator|.
name|that
argument_list|(
name|threadTagMap
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|expect
operator|.
name|that
argument_list|(
name|threadTagMap
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|expect
operator|.
name|that
argument_list|(
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|shouldForceLogging
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// Verify that tags and force logging flag in the outer thread are still set.
name|tagMap
operator|=
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|getTags
argument_list|()
operator|.
name|asMap
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tagMap
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tagMap
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|assertForceLogging
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|getTags
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertForceLogging
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|assertForceLogging (boolean expected)
specifier|private
name|void
name|assertForceLogging
parameter_list|(
name|boolean
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
name|LoggingContext
operator|.
name|getInstance
argument_list|()
operator|.
name|shouldForceLogging
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
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

