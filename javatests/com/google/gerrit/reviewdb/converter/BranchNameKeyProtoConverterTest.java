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
DECL|package|com.google.gerrit.reviewdb.converter
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|converter
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
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|proto
operator|.
name|testing
operator|.
name|SerializedClassSubject
operator|.
name|assertThatSerializedClass
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
name|ImmutableMap
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
name|testing
operator|.
name|SerializedClassSubject
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
name|Branch
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
name|Project
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|Parser
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
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
DECL|class|BranchNameKeyProtoConverterTest
specifier|public
class|class
name|BranchNameKeyProtoConverterTest
block|{
DECL|field|branchNameKeyProtoConverter
specifier|private
specifier|final
name|BranchNameKeyProtoConverter
name|branchNameKeyProtoConverter
init|=
name|BranchNameKeyProtoConverter
operator|.
name|INSTANCE
decl_stmt|;
annotation|@
name|Test
DECL|method|allValuesConvertedToProto ()
specifier|public
name|void
name|allValuesConvertedToProto
parameter_list|()
block|{
name|Branch
operator|.
name|NameKey
name|nameKey
init|=
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"project-13"
argument_list|)
argument_list|,
literal|"branch-72"
argument_list|)
decl_stmt|;
name|Reviewdb
operator|.
name|Branch_NameKey
name|proto
init|=
name|branchNameKeyProtoConverter
operator|.
name|toProto
argument_list|(
name|nameKey
argument_list|)
decl_stmt|;
name|Reviewdb
operator|.
name|Branch_NameKey
name|expectedProto
init|=
name|Reviewdb
operator|.
name|Branch_NameKey
operator|.
name|newBuilder
argument_list|()
operator|.
name|setProjectName
argument_list|(
name|Reviewdb
operator|.
name|Project_NameKey
operator|.
name|newBuilder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"project-13"
argument_list|)
argument_list|)
operator|.
name|setBranchName
argument_list|(
literal|"refs/heads/branch-72"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|proto
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expectedProto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|allValuesConvertedToProtoAndBackAgain ()
specifier|public
name|void
name|allValuesConvertedToProtoAndBackAgain
parameter_list|()
block|{
name|Branch
operator|.
name|NameKey
name|nameKey
init|=
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"project-52"
argument_list|)
argument_list|,
literal|"branch 14"
argument_list|)
decl_stmt|;
name|Branch
operator|.
name|NameKey
name|convertedNameKey
init|=
name|branchNameKeyProtoConverter
operator|.
name|fromProto
argument_list|(
name|branchNameKeyProtoConverter
operator|.
name|toProto
argument_list|(
name|nameKey
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|convertedNameKey
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|nameKey
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|protoCanBeParsedFromBytes ()
specifier|public
name|void
name|protoCanBeParsedFromBytes
parameter_list|()
throws|throws
name|Exception
block|{
name|Reviewdb
operator|.
name|Branch_NameKey
name|proto
init|=
name|Reviewdb
operator|.
name|Branch_NameKey
operator|.
name|newBuilder
argument_list|()
operator|.
name|setProjectName
argument_list|(
name|Reviewdb
operator|.
name|Project_NameKey
operator|.
name|newBuilder
argument_list|()
operator|.
name|setName
argument_list|(
literal|"project 1"
argument_list|)
argument_list|)
operator|.
name|setBranchName
argument_list|(
literal|"branch 36"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|proto
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|Parser
argument_list|<
name|Reviewdb
operator|.
name|Branch_NameKey
argument_list|>
name|parser
init|=
name|branchNameKeyProtoConverter
operator|.
name|getParser
argument_list|()
decl_stmt|;
name|Reviewdb
operator|.
name|Branch_NameKey
name|parsedProto
init|=
name|parser
operator|.
name|parseFrom
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parsedProto
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|proto
argument_list|)
expr_stmt|;
block|}
comment|/** See {@link SerializedClassSubject} for background and what to do if this test fails. */
annotation|@
name|Test
DECL|method|fieldsExistAsExpected ()
specifier|public
name|void
name|fieldsExistAsExpected
parameter_list|()
block|{
name|assertThatSerializedClass
argument_list|(
name|Branch
operator|.
name|NameKey
operator|.
name|class
argument_list|)
operator|.
name|hasFields
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Type
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"projectName"
argument_list|,
name|Project
operator|.
name|NameKey
operator|.
name|class
argument_list|)
operator|.
name|put
argument_list|(
literal|"branchName"
argument_list|,
name|String
operator|.
name|class
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

