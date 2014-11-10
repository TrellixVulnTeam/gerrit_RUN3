begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.util
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|util
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
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|LabelVoteTest
specifier|public
class|class
name|LabelVoteTest
block|{
annotation|@
name|Test
DECL|method|parse ()
specifier|public
name|void
name|parse
parameter_list|()
block|{
name|LabelVote
name|l
decl_stmt|;
name|l
operator|=
name|LabelVote
operator|.
name|parse
argument_list|(
literal|"Code-Review-2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review"
argument_list|,
name|l
operator|.
name|label
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|2
argument_list|,
name|l
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
name|LabelVote
operator|.
name|parse
argument_list|(
literal|"Code-Review-1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review"
argument_list|,
name|l
operator|.
name|label
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|,
name|l
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
name|LabelVote
operator|.
name|parse
argument_list|(
literal|"-Code-Review"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review"
argument_list|,
name|l
operator|.
name|label
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|,
name|l
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
name|LabelVote
operator|.
name|parse
argument_list|(
literal|"Code-Review"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review"
argument_list|,
name|l
operator|.
name|label
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|,
name|l
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
name|LabelVote
operator|.
name|parse
argument_list|(
literal|"Code-Review+1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review"
argument_list|,
name|l
operator|.
name|label
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|,
name|l
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
name|LabelVote
operator|.
name|parse
argument_list|(
literal|"Code-Review+2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review"
argument_list|,
name|l
operator|.
name|label
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|2
argument_list|,
name|l
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|format ()
specifier|public
name|void
name|format
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"Code-Review-2"
argument_list|,
name|LabelVote
operator|.
name|parse
argument_list|(
literal|"Code-Review-2"
argument_list|)
operator|.
name|format
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review-1"
argument_list|,
name|LabelVote
operator|.
name|parse
argument_list|(
literal|"Code-Review-1"
argument_list|)
operator|.
name|format
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-Code-Review"
argument_list|,
name|LabelVote
operator|.
name|parse
argument_list|(
literal|"-Code-Review"
argument_list|)
operator|.
name|format
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review+1"
argument_list|,
name|LabelVote
operator|.
name|parse
argument_list|(
literal|"Code-Review+1"
argument_list|)
operator|.
name|format
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review+2"
argument_list|,
name|LabelVote
operator|.
name|parse
argument_list|(
literal|"Code-Review+2"
argument_list|)
operator|.
name|format
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|parseWithEquals ()
specifier|public
name|void
name|parseWithEquals
parameter_list|()
block|{
name|LabelVote
name|l
decl_stmt|;
name|l
operator|=
name|LabelVote
operator|.
name|parseWithEquals
argument_list|(
literal|"Code-Review=-2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review"
argument_list|,
name|l
operator|.
name|label
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|2
argument_list|,
name|l
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
name|LabelVote
operator|.
name|parseWithEquals
argument_list|(
literal|"Code-Review=-1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review"
argument_list|,
name|l
operator|.
name|label
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|,
name|l
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
name|LabelVote
operator|.
name|parseWithEquals
argument_list|(
literal|"Code-Review=0"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review"
argument_list|,
name|l
operator|.
name|label
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|,
name|l
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
name|LabelVote
operator|.
name|parseWithEquals
argument_list|(
literal|"Code-Review=1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review"
argument_list|,
name|l
operator|.
name|label
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|,
name|l
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
name|LabelVote
operator|.
name|parseWithEquals
argument_list|(
literal|"Code-Review=+1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review"
argument_list|,
name|l
operator|.
name|label
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|,
name|l
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
name|LabelVote
operator|.
name|parseWithEquals
argument_list|(
literal|"Code-Review=2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review"
argument_list|,
name|l
operator|.
name|label
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|2
argument_list|,
name|l
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|=
name|LabelVote
operator|.
name|parseWithEquals
argument_list|(
literal|"Code-Review=+2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review"
argument_list|,
name|l
operator|.
name|label
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
name|short
operator|)
literal|2
argument_list|,
name|l
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|formatWithEquals ()
specifier|public
name|void
name|formatWithEquals
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"Code-Review=-2"
argument_list|,
name|LabelVote
operator|.
name|parseWithEquals
argument_list|(
literal|"Code-Review=-2"
argument_list|)
operator|.
name|formatWithEquals
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review=-1"
argument_list|,
name|LabelVote
operator|.
name|parseWithEquals
argument_list|(
literal|"Code-Review=-1"
argument_list|)
operator|.
name|formatWithEquals
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review=0"
argument_list|,
name|LabelVote
operator|.
name|parseWithEquals
argument_list|(
literal|"Code-Review=0"
argument_list|)
operator|.
name|formatWithEquals
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review=+1"
argument_list|,
name|LabelVote
operator|.
name|parseWithEquals
argument_list|(
literal|"Code-Review=+1"
argument_list|)
operator|.
name|formatWithEquals
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Code-Review=+2"
argument_list|,
name|LabelVote
operator|.
name|parseWithEquals
argument_list|(
literal|"Code-Review=+2"
argument_list|)
operator|.
name|formatWithEquals
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

