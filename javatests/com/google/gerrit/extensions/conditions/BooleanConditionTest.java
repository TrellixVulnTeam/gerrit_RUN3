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
DECL|package|com.google.gerrit.extensions.conditions
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|conditions
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
name|extensions
operator|.
name|conditions
operator|.
name|BooleanCondition
operator|.
name|and
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
name|extensions
operator|.
name|conditions
operator|.
name|BooleanCondition
operator|.
name|not
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
name|extensions
operator|.
name|conditions
operator|.
name|BooleanCondition
operator|.
name|or
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
name|extensions
operator|.
name|conditions
operator|.
name|BooleanCondition
operator|.
name|valueOf
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
name|testing
operator|.
name|GerritBaseTests
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
DECL|class|BooleanConditionTest
specifier|public
class|class
name|BooleanConditionTest
extends|extends
name|GerritBaseTests
block|{
DECL|field|NO_TRIVIAL_EVALUATION
specifier|private
specifier|static
specifier|final
name|BooleanCondition
name|NO_TRIVIAL_EVALUATION
init|=
operator|new
name|BooleanCondition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|value
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"value() is not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Iterable
argument_list|<
name|T
argument_list|>
name|children
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"children(Class<T> type) is not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|BooleanCondition
name|reduce
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|evaluatesTrivially
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Test
DECL|method|reduceAnd_CutOffNonTrivialWhenPossible ()
specifier|public
name|void
name|reduceAnd_CutOffNonTrivialWhenPossible
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanCondition
name|nonReduced
init|=
name|and
argument_list|(
literal|false
argument_list|,
name|NO_TRIVIAL_EVALUATION
argument_list|)
decl_stmt|;
name|BooleanCondition
name|reduced
init|=
name|valueOf
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nonReduced
operator|.
name|reduce
argument_list|()
argument_list|,
name|reduced
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reduceAnd_CutOffNonTrivialWhenPossibleSwapped ()
specifier|public
name|void
name|reduceAnd_CutOffNonTrivialWhenPossibleSwapped
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanCondition
name|nonReduced
init|=
name|and
argument_list|(
name|NO_TRIVIAL_EVALUATION
argument_list|,
name|valueOf
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanCondition
name|reduced
init|=
name|valueOf
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nonReduced
operator|.
name|reduce
argument_list|()
argument_list|,
name|reduced
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reduceAnd_KeepNonTrivialWhenNoCutOffPossible ()
specifier|public
name|void
name|reduceAnd_KeepNonTrivialWhenNoCutOffPossible
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanCondition
name|nonReduced
init|=
name|and
argument_list|(
literal|true
argument_list|,
name|NO_TRIVIAL_EVALUATION
argument_list|)
decl_stmt|;
name|BooleanCondition
name|reduced
init|=
name|and
argument_list|(
literal|true
argument_list|,
name|NO_TRIVIAL_EVALUATION
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nonReduced
operator|.
name|reduce
argument_list|()
argument_list|,
name|reduced
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reduceAnd_KeepNonTrivialWhenNoCutOffPossibleSwapped ()
specifier|public
name|void
name|reduceAnd_KeepNonTrivialWhenNoCutOffPossibleSwapped
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanCondition
name|nonReduced
init|=
name|and
argument_list|(
name|NO_TRIVIAL_EVALUATION
argument_list|,
name|valueOf
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanCondition
name|reduced
init|=
name|and
argument_list|(
name|NO_TRIVIAL_EVALUATION
argument_list|,
name|valueOf
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nonReduced
operator|.
name|reduce
argument_list|()
argument_list|,
name|reduced
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reduceOr_CutOffNonTrivialWhenPossible ()
specifier|public
name|void
name|reduceOr_CutOffNonTrivialWhenPossible
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanCondition
name|nonReduced
init|=
name|or
argument_list|(
literal|true
argument_list|,
name|NO_TRIVIAL_EVALUATION
argument_list|)
decl_stmt|;
name|BooleanCondition
name|reduced
init|=
name|valueOf
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nonReduced
operator|.
name|reduce
argument_list|()
argument_list|,
name|reduced
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reduceOr_CutOffNonTrivialWhenPossibleSwapped ()
specifier|public
name|void
name|reduceOr_CutOffNonTrivialWhenPossibleSwapped
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanCondition
name|nonReduced
init|=
name|or
argument_list|(
name|NO_TRIVIAL_EVALUATION
argument_list|,
name|valueOf
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanCondition
name|reduced
init|=
name|valueOf
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nonReduced
operator|.
name|reduce
argument_list|()
argument_list|,
name|reduced
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reduceOr_KeepNonTrivialWhenNoCutOffPossible ()
specifier|public
name|void
name|reduceOr_KeepNonTrivialWhenNoCutOffPossible
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanCondition
name|nonReduced
init|=
name|or
argument_list|(
literal|false
argument_list|,
name|NO_TRIVIAL_EVALUATION
argument_list|)
decl_stmt|;
name|BooleanCondition
name|reduced
init|=
name|or
argument_list|(
literal|false
argument_list|,
name|NO_TRIVIAL_EVALUATION
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nonReduced
operator|.
name|reduce
argument_list|()
argument_list|,
name|reduced
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reduceOr_KeepNonTrivialWhenNoCutOffPossibleSwapped ()
specifier|public
name|void
name|reduceOr_KeepNonTrivialWhenNoCutOffPossibleSwapped
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanCondition
name|nonReduced
init|=
name|or
argument_list|(
name|NO_TRIVIAL_EVALUATION
argument_list|,
name|valueOf
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanCondition
name|reduced
init|=
name|or
argument_list|(
name|NO_TRIVIAL_EVALUATION
argument_list|,
name|valueOf
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nonReduced
operator|.
name|reduce
argument_list|()
argument_list|,
name|reduced
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reduceNot_ReduceIrrelevant ()
specifier|public
name|void
name|reduceNot_ReduceIrrelevant
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanCondition
name|nonReduced
init|=
name|not
argument_list|(
name|valueOf
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanCondition
name|reduced
init|=
name|valueOf
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nonReduced
operator|.
name|reduce
argument_list|()
argument_list|,
name|reduced
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reduceNot_ReduceIrrelevant2 ()
specifier|public
name|void
name|reduceNot_ReduceIrrelevant2
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanCondition
name|nonReduced
init|=
name|not
argument_list|(
name|valueOf
argument_list|(
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanCondition
name|reduced
init|=
name|valueOf
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nonReduced
operator|.
name|reduce
argument_list|()
argument_list|,
name|reduced
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reduceNot_KeepNonTrivialWhenNoCutOffPossible ()
specifier|public
name|void
name|reduceNot_KeepNonTrivialWhenNoCutOffPossible
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanCondition
name|nonReduced
init|=
name|not
argument_list|(
name|NO_TRIVIAL_EVALUATION
argument_list|)
decl_stmt|;
name|BooleanCondition
name|reduced
init|=
name|not
argument_list|(
name|NO_TRIVIAL_EVALUATION
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nonReduced
operator|.
name|reduce
argument_list|()
argument_list|,
name|reduced
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reduceComplexTreeToSingleValue ()
specifier|public
name|void
name|reduceComplexTreeToSingleValue
parameter_list|()
throws|throws
name|Exception
block|{
comment|//        AND
comment|//       /   \
comment|//      OR   NOT
comment|//     /  \    \
comment|//   NTE NTE  TRUE
name|BooleanCondition
name|nonReduced
init|=
name|and
argument_list|(
name|or
argument_list|(
name|NO_TRIVIAL_EVALUATION
argument_list|,
name|NO_TRIVIAL_EVALUATION
argument_list|)
argument_list|,
name|not
argument_list|(
name|valueOf
argument_list|(
literal|true
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanCondition
name|reduced
init|=
name|valueOf
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nonReduced
operator|.
name|reduce
argument_list|()
argument_list|,
name|reduced
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reduceComplexTreeToSmallerTree ()
specifier|public
name|void
name|reduceComplexTreeToSmallerTree
parameter_list|()
throws|throws
name|Exception
block|{
comment|//        AND
comment|//       /   \
comment|//      OR    OR
comment|//     /  \   / \
comment|//   NTE NTE  T  F
name|BooleanCondition
name|nonReduced
init|=
name|and
argument_list|(
name|or
argument_list|(
name|NO_TRIVIAL_EVALUATION
argument_list|,
name|NO_TRIVIAL_EVALUATION
argument_list|)
argument_list|,
name|or
argument_list|(
name|valueOf
argument_list|(
literal|true
argument_list|)
argument_list|,
name|valueOf
argument_list|(
literal|false
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanCondition
name|reduced
init|=
name|and
argument_list|(
name|or
argument_list|(
name|NO_TRIVIAL_EVALUATION
argument_list|,
name|NO_TRIVIAL_EVALUATION
argument_list|)
argument_list|,
name|valueOf
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nonReduced
operator|.
name|reduce
argument_list|()
argument_list|,
name|reduced
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

