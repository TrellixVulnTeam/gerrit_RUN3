begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.reviewdb.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
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
name|assertThat
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
name|reviewdb
operator|.
name|client
operator|.
name|Account
operator|.
name|Id
operator|.
name|fromRef
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
name|reviewdb
operator|.
name|client
operator|.
name|Account
operator|.
name|Id
operator|.
name|fromRefPart
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
DECL|class|AccountTest
specifier|public
class|class
name|AccountTest
block|{
annotation|@
name|Test
DECL|method|parseRefName ()
specifier|public
name|void
name|parseRefName
parameter_list|()
block|{
name|assertThat
argument_list|(
name|fromRef
argument_list|(
literal|"refs/users/01/1"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|id
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fromRef
argument_list|(
literal|"refs/users/01/1-drafts"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|id
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fromRef
argument_list|(
literal|"refs/users/01/1-drafts/2"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|id
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fromRef
argument_list|(
literal|"refs/users/01/1/edit/2"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|id
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fromRef
argument_list|(
literal|null
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|fromRef
argument_list|(
literal|""
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
comment|// Invalid characters.
name|assertThat
argument_list|(
name|fromRef
argument_list|(
literal|"refs/users/01a/1"
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|fromRef
argument_list|(
literal|"refs/users/01/a1"
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
comment|// Mismatched shard.
name|assertThat
argument_list|(
name|fromRef
argument_list|(
literal|"refs/users/01/23"
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
comment|// Shard too short.
name|assertThat
argument_list|(
name|fromRef
argument_list|(
literal|"refs/users/1/1"
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|parseRefNameParts ()
specifier|public
name|void
name|parseRefNameParts
parameter_list|()
block|{
name|assertThat
argument_list|(
name|fromRefPart
argument_list|(
literal|"01/1"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|id
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fromRefPart
argument_list|(
literal|"ab/cd"
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
DECL|method|id (int n)
specifier|private
name|Account
operator|.
name|Id
name|id
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|n
argument_list|)
return|;
block|}
block|}
end_class

end_unit

