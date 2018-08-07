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
DECL|package|com.google.gerrit.proto
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|proto
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
name|extensions
operator|.
name|proto
operator|.
name|ProtoTruth
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
name|gerrit
operator|.
name|proto
operator|.
name|reviewdb
operator|.
name|Reviewdb
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
name|proto
operator|.
name|reviewdb
operator|.
name|Reviewdb
operator|.
name|Change_Id
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
DECL|class|ReviewDbProtoTest
specifier|public
class|class
name|ReviewDbProtoTest
block|{
annotation|@
name|Test
DECL|method|generatedProtoApi ()
specifier|public
name|void
name|generatedProtoApi
parameter_list|()
block|{
name|Change
name|c1
init|=
name|Change
operator|.
name|newBuilder
argument_list|()
operator|.
name|setChangeId
argument_list|(
name|Change_Id
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
literal|1234
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Change
name|c2
init|=
name|Change
operator|.
name|newBuilder
argument_list|()
operator|.
name|setChangeId
argument_list|(
name|Change_Id
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
literal|5678
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|c1
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c1
argument_list|)
operator|.
name|isNotEqualTo
argument_list|(
name|c2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

