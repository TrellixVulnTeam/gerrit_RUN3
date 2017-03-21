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
DECL|package|com.google.gerrit.extensions.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|client
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
name|client
operator|.
name|RangeSubject
operator|.
name|assertThat
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
DECL|class|RangeTest
specifier|public
class|class
name|RangeTest
block|{
annotation|@
name|Test
DECL|method|rangeOverMultipleLinesWithSmallerEndCharacterIsValid ()
specifier|public
name|void
name|rangeOverMultipleLinesWithSmallerEndCharacterIsValid
parameter_list|()
block|{
name|Comment
operator|.
name|Range
name|range
init|=
name|createRange
argument_list|(
literal|13
argument_list|,
literal|31
argument_list|,
literal|19
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|range
argument_list|)
operator|.
name|isValid
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|rangeInOneLineIsValid ()
specifier|public
name|void
name|rangeInOneLineIsValid
parameter_list|()
block|{
name|Comment
operator|.
name|Range
name|range
init|=
name|createRange
argument_list|(
literal|13
argument_list|,
literal|2
argument_list|,
literal|13
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|range
argument_list|)
operator|.
name|isValid
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|startPositionEqualToEndPositionIsValidRange ()
specifier|public
name|void
name|startPositionEqualToEndPositionIsValidRange
parameter_list|()
block|{
name|Comment
operator|.
name|Range
name|range
init|=
name|createRange
argument_list|(
literal|13
argument_list|,
literal|11
argument_list|,
literal|13
argument_list|,
literal|11
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|range
argument_list|)
operator|.
name|isValid
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|negativeStartLineResultsInInvalidRange ()
specifier|public
name|void
name|negativeStartLineResultsInInvalidRange
parameter_list|()
block|{
name|Comment
operator|.
name|Range
name|range
init|=
name|createRange
argument_list|(
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|19
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|range
argument_list|)
operator|.
name|isInvalid
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|negativeEndLineResultsInInvalidRange ()
specifier|public
name|void
name|negativeEndLineResultsInInvalidRange
parameter_list|()
block|{
name|Comment
operator|.
name|Range
name|range
init|=
name|createRange
argument_list|(
literal|13
argument_list|,
literal|2
argument_list|,
operator|-
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|range
argument_list|)
operator|.
name|isInvalid
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|negativeStartCharacterResultsInInvalidRange ()
specifier|public
name|void
name|negativeStartCharacterResultsInInvalidRange
parameter_list|()
block|{
name|Comment
operator|.
name|Range
name|range
init|=
name|createRange
argument_list|(
literal|13
argument_list|,
operator|-
literal|1
argument_list|,
literal|19
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|range
argument_list|)
operator|.
name|isInvalid
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|negativeEndCharacterResultsInInvalidRange ()
specifier|public
name|void
name|negativeEndCharacterResultsInInvalidRange
parameter_list|()
block|{
name|Comment
operator|.
name|Range
name|range
init|=
name|createRange
argument_list|(
literal|13
argument_list|,
literal|2
argument_list|,
literal|19
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|range
argument_list|)
operator|.
name|isInvalid
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|zeroStartLineResultsInInvalidRange ()
specifier|public
name|void
name|zeroStartLineResultsInInvalidRange
parameter_list|()
block|{
name|Comment
operator|.
name|Range
name|range
init|=
name|createRange
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|,
literal|19
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|range
argument_list|)
operator|.
name|isInvalid
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|zeroEndLineResultsInInvalidRange ()
specifier|public
name|void
name|zeroEndLineResultsInInvalidRange
parameter_list|()
block|{
name|Comment
operator|.
name|Range
name|range
init|=
name|createRange
argument_list|(
literal|13
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|range
argument_list|)
operator|.
name|isInvalid
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|zeroStartCharacterResultsInValidRange ()
specifier|public
name|void
name|zeroStartCharacterResultsInValidRange
parameter_list|()
block|{
name|Comment
operator|.
name|Range
name|range
init|=
name|createRange
argument_list|(
literal|13
argument_list|,
literal|0
argument_list|,
literal|19
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|range
argument_list|)
operator|.
name|isValid
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|zeroEndCharacterResultsInValidRange ()
specifier|public
name|void
name|zeroEndCharacterResultsInValidRange
parameter_list|()
block|{
name|Comment
operator|.
name|Range
name|range
init|=
name|createRange
argument_list|(
literal|13
argument_list|,
literal|31
argument_list|,
literal|19
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|range
argument_list|)
operator|.
name|isValid
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|startLineGreaterThanEndLineResultsInInvalidRange ()
specifier|public
name|void
name|startLineGreaterThanEndLineResultsInInvalidRange
parameter_list|()
block|{
name|Comment
operator|.
name|Range
name|range
init|=
name|createRange
argument_list|(
literal|20
argument_list|,
literal|2
argument_list|,
literal|19
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|range
argument_list|)
operator|.
name|isInvalid
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|startCharGreaterThanEndCharForSameLineResultsInInvalidRange ()
specifier|public
name|void
name|startCharGreaterThanEndCharForSameLineResultsInInvalidRange
parameter_list|()
block|{
name|Comment
operator|.
name|Range
name|range
init|=
name|createRange
argument_list|(
literal|13
argument_list|,
literal|11
argument_list|,
literal|13
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|range
argument_list|)
operator|.
name|isInvalid
argument_list|()
expr_stmt|;
block|}
DECL|method|createRange ( int startLine, int startCharacter, int endLine, int endCharacter)
specifier|private
name|Comment
operator|.
name|Range
name|createRange
parameter_list|(
name|int
name|startLine
parameter_list|,
name|int
name|startCharacter
parameter_list|,
name|int
name|endLine
parameter_list|,
name|int
name|endCharacter
parameter_list|)
block|{
name|Comment
operator|.
name|Range
name|range
init|=
operator|new
name|Comment
operator|.
name|Range
argument_list|()
decl_stmt|;
name|range
operator|.
name|startLine
operator|=
name|startLine
expr_stmt|;
name|range
operator|.
name|startCharacter
operator|=
name|startCharacter
expr_stmt|;
name|range
operator|.
name|endLine
operator|=
name|endLine
expr_stmt|;
name|range
operator|.
name|endCharacter
operator|=
name|endCharacter
expr_stmt|;
return|return
name|range
return|;
block|}
block|}
end_class

end_unit

