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
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertAbout
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
name|FailureStrategy
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
name|IntegerSubject
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
name|Subject
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
name|SubjectFactory
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
name|Truth
import|;
end_import

begin_class
DECL|class|RangeSubject
specifier|public
class|class
name|RangeSubject
extends|extends
name|Subject
argument_list|<
name|RangeSubject
argument_list|,
name|Comment
operator|.
name|Range
argument_list|>
block|{
DECL|field|RANGE_SUBJECT_FACTORY
specifier|private
specifier|static
specifier|final
name|SubjectFactory
argument_list|<
name|RangeSubject
argument_list|,
name|Comment
operator|.
name|Range
argument_list|>
name|RANGE_SUBJECT_FACTORY
init|=
operator|new
name|SubjectFactory
argument_list|<
name|RangeSubject
argument_list|,
name|Comment
operator|.
name|Range
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RangeSubject
name|getSubject
parameter_list|(
name|FailureStrategy
name|failureStrategy
parameter_list|,
name|Comment
operator|.
name|Range
name|range
parameter_list|)
block|{
return|return
operator|new
name|RangeSubject
argument_list|(
name|failureStrategy
argument_list|,
name|range
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|assertThat (Comment.Range range)
specifier|public
specifier|static
name|RangeSubject
name|assertThat
parameter_list|(
name|Comment
operator|.
name|Range
name|range
parameter_list|)
block|{
return|return
name|assertAbout
argument_list|(
name|RANGE_SUBJECT_FACTORY
argument_list|)
operator|.
name|that
argument_list|(
name|range
argument_list|)
return|;
block|}
DECL|method|RangeSubject (FailureStrategy failureStrategy, Comment.Range range)
specifier|private
name|RangeSubject
parameter_list|(
name|FailureStrategy
name|failureStrategy
parameter_list|,
name|Comment
operator|.
name|Range
name|range
parameter_list|)
block|{
name|super
argument_list|(
name|failureStrategy
argument_list|,
name|range
argument_list|)
expr_stmt|;
block|}
DECL|method|startLine ()
specifier|public
name|IntegerSubject
name|startLine
parameter_list|()
block|{
return|return
name|Truth
operator|.
name|assertThat
argument_list|(
name|actual
argument_list|()
operator|.
name|startLine
argument_list|)
operator|.
name|named
argument_list|(
literal|"startLine"
argument_list|)
return|;
block|}
DECL|method|startCharacter ()
specifier|public
name|IntegerSubject
name|startCharacter
parameter_list|()
block|{
return|return
name|Truth
operator|.
name|assertThat
argument_list|(
name|actual
argument_list|()
operator|.
name|startCharacter
argument_list|)
operator|.
name|named
argument_list|(
literal|"startCharacter"
argument_list|)
return|;
block|}
DECL|method|endLine ()
specifier|public
name|IntegerSubject
name|endLine
parameter_list|()
block|{
return|return
name|Truth
operator|.
name|assertThat
argument_list|(
name|actual
argument_list|()
operator|.
name|endLine
argument_list|)
operator|.
name|named
argument_list|(
literal|"endLine"
argument_list|)
return|;
block|}
DECL|method|endCharacter ()
specifier|public
name|IntegerSubject
name|endCharacter
parameter_list|()
block|{
return|return
name|Truth
operator|.
name|assertThat
argument_list|(
name|actual
argument_list|()
operator|.
name|endCharacter
argument_list|)
operator|.
name|named
argument_list|(
literal|"endCharacter"
argument_list|)
return|;
block|}
DECL|method|isValid ()
specifier|public
name|void
name|isValid
parameter_list|()
block|{
name|isNotNull
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|actual
argument_list|()
operator|.
name|isValid
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"is valid"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isInvalid ()
specifier|public
name|void
name|isInvalid
parameter_list|()
block|{
name|isNotNull
argument_list|()
expr_stmt|;
if|if
condition|(
name|actual
argument_list|()
operator|.
name|isValid
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"is invalid"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

