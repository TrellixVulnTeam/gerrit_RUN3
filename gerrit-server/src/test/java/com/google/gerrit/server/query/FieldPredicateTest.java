begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.query
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|query
package|;
end_package

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
name|assertSame
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
name|Collections
import|;
end_import

begin_class
DECL|class|FieldPredicateTest
specifier|public
class|class
name|FieldPredicateTest
extends|extends
name|PredicateTest
block|{
annotation|@
name|Test
DECL|method|testToString ()
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"author:bob"
argument_list|,
name|f
argument_list|(
literal|"author"
argument_list|,
literal|"bob"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"author:\"\""
argument_list|,
name|f
argument_list|(
literal|"author"
argument_list|,
literal|""
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"owner:\"A U Thor\""
argument_list|,
name|f
argument_list|(
literal|"owner"
argument_list|,
literal|"A U Thor"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEquals ()
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|f
argument_list|(
literal|"author"
argument_list|,
literal|"bob"
argument_list|)
operator|.
name|equals
argument_list|(
name|f
argument_list|(
literal|"author"
argument_list|,
literal|"bob"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|f
argument_list|(
literal|"author"
argument_list|,
literal|"bob"
argument_list|)
operator|.
name|equals
argument_list|(
name|f
argument_list|(
literal|"author"
argument_list|,
literal|"alice"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|f
argument_list|(
literal|"owner"
argument_list|,
literal|"bob"
argument_list|)
operator|.
name|equals
argument_list|(
name|f
argument_list|(
literal|"author"
argument_list|,
literal|"bob"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|f
argument_list|(
literal|"author"
argument_list|,
literal|"bob"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"author"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHashCode ()
specifier|public
name|void
name|testHashCode
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|f
argument_list|(
literal|"a"
argument_list|,
literal|"bob"
argument_list|)
operator|.
name|hashCode
argument_list|()
operator|==
name|f
argument_list|(
literal|"a"
argument_list|,
literal|"bob"
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|f
argument_list|(
literal|"a"
argument_list|,
literal|"bob"
argument_list|)
operator|.
name|hashCode
argument_list|()
operator|==
name|f
argument_list|(
literal|"a"
argument_list|,
literal|"alice"
argument_list|)
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNameValue ()
specifier|public
name|void
name|testNameValue
parameter_list|()
block|{
specifier|final
name|String
name|name
init|=
literal|"author"
decl_stmt|;
specifier|final
name|String
name|value
init|=
literal|"alice"
decl_stmt|;
specifier|final
name|OperatorPredicate
argument_list|<
name|String
argument_list|>
name|f
init|=
name|f
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|getOperator
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|value
argument_list|,
name|f
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|f
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCopy ()
specifier|public
name|void
name|testCopy
parameter_list|()
block|{
specifier|final
name|OperatorPredicate
argument_list|<
name|String
argument_list|>
name|f
init|=
name|f
argument_list|(
literal|"author"
argument_list|,
literal|"alice"
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|copy
argument_list|(
name|Collections
operator|.
expr|<
name|Predicate
argument_list|<
name|String
argument_list|>
operator|>
name|emptyList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|copy
argument_list|(
name|f
operator|.
name|getChildren
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"Expected 0 children"
argument_list|)
expr_stmt|;
name|f
operator|.
name|copy
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|f
argument_list|(
literal|"owner"
argument_list|,
literal|"bob"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

