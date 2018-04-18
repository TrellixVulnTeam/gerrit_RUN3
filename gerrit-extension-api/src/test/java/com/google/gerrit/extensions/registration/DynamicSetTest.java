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
DECL|package|com.google.gerrit.extensions.registration
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|registration
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
name|inject
operator|.
name|Key
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
name|util
operator|.
name|Providers
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
DECL|class|DynamicSetTest
specifier|public
class|class
name|DynamicSetTest
block|{
comment|// In tests for {@link DynamicSet#contains(Object)}, be sure to avoid
comment|// {@code assertThat(ds).contains(...) @} and
comment|// {@code assertThat(ds).DoesNotContains(...) @} as (since
comment|// {@link DynamicSet@} is not a {@link Collection@}) those boil down to
comment|// iterating over the {@link DynamicSet@} and checking equality instead
comment|// of calling {@link DynamicSet#contains(Object)}.
comment|// To test for {@link DynamicSet#contains(Object)}, use
comment|// {@code assertThat(ds.contains(...)).isTrue() @} and
comment|// {@code assertThat(ds.contains(...)).isFalse() @} instead.
annotation|@
name|Test
DECL|method|containsWithEmpty ()
specifier|public
name|void
name|containsWithEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|DynamicSet
argument_list|<
name|Integer
argument_list|>
name|ds
init|=
operator|new
name|DynamicSet
argument_list|<>
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|ds
operator|.
name|contains
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
comment|// See above comment about ds.contains
block|}
annotation|@
name|Test
DECL|method|containsTrueWithSingleElement ()
specifier|public
name|void
name|containsTrueWithSingleElement
parameter_list|()
throws|throws
name|Exception
block|{
name|DynamicSet
argument_list|<
name|Integer
argument_list|>
name|ds
init|=
operator|new
name|DynamicSet
argument_list|<>
argument_list|()
decl_stmt|;
name|ds
operator|.
name|add
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ds
operator|.
name|contains
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
comment|// See above comment about ds.contains
block|}
annotation|@
name|Test
DECL|method|containsFalseWithSingleElement ()
specifier|public
name|void
name|containsFalseWithSingleElement
parameter_list|()
throws|throws
name|Exception
block|{
name|DynamicSet
argument_list|<
name|Integer
argument_list|>
name|ds
init|=
operator|new
name|DynamicSet
argument_list|<>
argument_list|()
decl_stmt|;
name|ds
operator|.
name|add
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ds
operator|.
name|contains
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
comment|// See above comment about ds.contains
block|}
annotation|@
name|Test
DECL|method|containsTrueWithTwoElements ()
specifier|public
name|void
name|containsTrueWithTwoElements
parameter_list|()
throws|throws
name|Exception
block|{
name|DynamicSet
argument_list|<
name|Integer
argument_list|>
name|ds
init|=
operator|new
name|DynamicSet
argument_list|<>
argument_list|()
decl_stmt|;
name|ds
operator|.
name|add
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|ds
operator|.
name|add
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ds
operator|.
name|contains
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
comment|// See above comment about ds.contains
block|}
annotation|@
name|Test
DECL|method|containsFalseWithTwoElements ()
specifier|public
name|void
name|containsFalseWithTwoElements
parameter_list|()
throws|throws
name|Exception
block|{
name|DynamicSet
argument_list|<
name|Integer
argument_list|>
name|ds
init|=
operator|new
name|DynamicSet
argument_list|<>
argument_list|()
decl_stmt|;
name|ds
operator|.
name|add
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|ds
operator|.
name|add
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ds
operator|.
name|contains
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
comment|// See above comment about ds.contains
block|}
annotation|@
name|Test
DECL|method|containsDynamic ()
specifier|public
name|void
name|containsDynamic
parameter_list|()
throws|throws
name|Exception
block|{
name|DynamicSet
argument_list|<
name|Integer
argument_list|>
name|ds
init|=
operator|new
name|DynamicSet
argument_list|<>
argument_list|()
decl_stmt|;
name|ds
operator|.
name|add
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Key
argument_list|<
name|Integer
argument_list|>
name|key
init|=
name|Key
operator|.
name|get
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
decl_stmt|;
name|ReloadableRegistrationHandle
argument_list|<
name|Integer
argument_list|>
name|handle
init|=
name|ds
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|Providers
operator|.
name|of
argument_list|(
literal|4
argument_list|)
argument_list|)
decl_stmt|;
name|ds
operator|.
name|add
argument_list|(
literal|6
argument_list|)
expr_stmt|;
comment|// At first, 4 is contained.
name|assertThat
argument_list|(
name|ds
operator|.
name|contains
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
comment|// See above comment about ds.contains
comment|// Then we remove 4.
name|handle
operator|.
name|remove
argument_list|()
expr_stmt|;
comment|// And now 4 should no longer be contained.
name|assertThat
argument_list|(
name|ds
operator|.
name|contains
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
comment|// See above comment about ds.contains
block|}
block|}
end_class

end_unit

