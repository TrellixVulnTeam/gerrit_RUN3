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
name|Account
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
name|reviewdb
operator|.
name|client
operator|.
name|ChangeMessage
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
name|PatchSet
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
name|java
operator|.
name|sql
operator|.
name|Timestamp
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
DECL|class|ChangeMessageProtoConverterTest
specifier|public
class|class
name|ChangeMessageProtoConverterTest
block|{
DECL|field|changeMessageProtoConverter
specifier|private
specifier|final
name|ChangeMessageProtoConverter
name|changeMessageProtoConverter
init|=
name|ChangeMessageProtoConverter
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
name|ChangeMessage
name|changeMessage
init|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|543
argument_list|)
argument_list|,
literal|"change-message-21"
argument_list|)
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|63
argument_list|)
argument_list|,
operator|new
name|Timestamp
argument_list|(
literal|9876543
argument_list|)
argument_list|,
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|34
argument_list|)
argument_list|,
literal|13
argument_list|)
argument_list|)
decl_stmt|;
name|changeMessage
operator|.
name|setMessage
argument_list|(
literal|"This is a change message."
argument_list|)
expr_stmt|;
name|changeMessage
operator|.
name|setTag
argument_list|(
literal|"An arbitrary tag."
argument_list|)
expr_stmt|;
name|changeMessage
operator|.
name|setRealAuthor
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|10003
argument_list|)
argument_list|)
expr_stmt|;
name|Reviewdb
operator|.
name|ChangeMessage
name|proto
init|=
name|changeMessageProtoConverter
operator|.
name|toProto
argument_list|(
name|changeMessage
argument_list|)
decl_stmt|;
name|Reviewdb
operator|.
name|ChangeMessage
name|expectedProto
init|=
name|Reviewdb
operator|.
name|ChangeMessage
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKey
argument_list|(
name|Reviewdb
operator|.
name|ChangeMessage_Key
operator|.
name|newBuilder
argument_list|()
operator|.
name|setChangeId
argument_list|(
name|Reviewdb
operator|.
name|Change_Id
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
literal|543
argument_list|)
argument_list|)
operator|.
name|setUuid
argument_list|(
literal|"change-message-21"
argument_list|)
argument_list|)
operator|.
name|setAuthorId
argument_list|(
name|Reviewdb
operator|.
name|Account_Id
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
literal|63
argument_list|)
argument_list|)
operator|.
name|setWrittenOn
argument_list|(
literal|9876543
argument_list|)
operator|.
name|setMessage
argument_list|(
literal|"This is a change message."
argument_list|)
operator|.
name|setPatchset
argument_list|(
name|Reviewdb
operator|.
name|PatchSet_Id
operator|.
name|newBuilder
argument_list|()
operator|.
name|setChangeId
argument_list|(
name|Reviewdb
operator|.
name|Change_Id
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
literal|34
argument_list|)
argument_list|)
operator|.
name|setPatchSetId
argument_list|(
literal|13
argument_list|)
argument_list|)
operator|.
name|setTag
argument_list|(
literal|"An arbitrary tag."
argument_list|)
operator|.
name|setRealAuthor
argument_list|(
name|Reviewdb
operator|.
name|Account_Id
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
literal|10003
argument_list|)
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
DECL|method|mainValuesConvertedToProto ()
specifier|public
name|void
name|mainValuesConvertedToProto
parameter_list|()
block|{
name|ChangeMessage
name|changeMessage
init|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|543
argument_list|)
argument_list|,
literal|"change-message-21"
argument_list|)
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|63
argument_list|)
argument_list|,
operator|new
name|Timestamp
argument_list|(
literal|9876543
argument_list|)
argument_list|,
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|34
argument_list|)
argument_list|,
literal|13
argument_list|)
argument_list|)
decl_stmt|;
name|Reviewdb
operator|.
name|ChangeMessage
name|proto
init|=
name|changeMessageProtoConverter
operator|.
name|toProto
argument_list|(
name|changeMessage
argument_list|)
decl_stmt|;
name|Reviewdb
operator|.
name|ChangeMessage
name|expectedProto
init|=
name|Reviewdb
operator|.
name|ChangeMessage
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKey
argument_list|(
name|Reviewdb
operator|.
name|ChangeMessage_Key
operator|.
name|newBuilder
argument_list|()
operator|.
name|setChangeId
argument_list|(
name|Reviewdb
operator|.
name|Change_Id
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
literal|543
argument_list|)
argument_list|)
operator|.
name|setUuid
argument_list|(
literal|"change-message-21"
argument_list|)
argument_list|)
operator|.
name|setAuthorId
argument_list|(
name|Reviewdb
operator|.
name|Account_Id
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
literal|63
argument_list|)
argument_list|)
operator|.
name|setWrittenOn
argument_list|(
literal|9876543
argument_list|)
operator|.
name|setPatchset
argument_list|(
name|Reviewdb
operator|.
name|PatchSet_Id
operator|.
name|newBuilder
argument_list|()
operator|.
name|setChangeId
argument_list|(
name|Reviewdb
operator|.
name|Change_Id
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
literal|34
argument_list|)
argument_list|)
operator|.
name|setPatchSetId
argument_list|(
literal|13
argument_list|)
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
comment|// This test documents a special behavior which is necessary to ensure binary compatibility.
annotation|@
name|Test
DECL|method|realAuthorIsNotAutomaticallySetToAuthorWhenConvertedToProto ()
specifier|public
name|void
name|realAuthorIsNotAutomaticallySetToAuthorWhenConvertedToProto
parameter_list|()
block|{
name|ChangeMessage
name|changeMessage
init|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|543
argument_list|)
argument_list|,
literal|"change-message-21"
argument_list|)
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|63
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Reviewdb
operator|.
name|ChangeMessage
name|proto
init|=
name|changeMessageProtoConverter
operator|.
name|toProto
argument_list|(
name|changeMessage
argument_list|)
decl_stmt|;
name|Reviewdb
operator|.
name|ChangeMessage
name|expectedProto
init|=
name|Reviewdb
operator|.
name|ChangeMessage
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKey
argument_list|(
name|Reviewdb
operator|.
name|ChangeMessage_Key
operator|.
name|newBuilder
argument_list|()
operator|.
name|setChangeId
argument_list|(
name|Reviewdb
operator|.
name|Change_Id
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
literal|543
argument_list|)
argument_list|)
operator|.
name|setUuid
argument_list|(
literal|"change-message-21"
argument_list|)
argument_list|)
operator|.
name|setAuthorId
argument_list|(
name|Reviewdb
operator|.
name|Account_Id
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
literal|63
argument_list|)
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
DECL|method|mandatoryValuesConvertedToProto ()
specifier|public
name|void
name|mandatoryValuesConvertedToProto
parameter_list|()
block|{
comment|// writtenOn may not be null according to the column definition but it's optional for the
comment|// protobuf definition. -> assume as optional and hence test null
name|ChangeMessage
name|changeMessage
init|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|543
argument_list|)
argument_list|,
literal|"change-message-21"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Reviewdb
operator|.
name|ChangeMessage
name|proto
init|=
name|changeMessageProtoConverter
operator|.
name|toProto
argument_list|(
name|changeMessage
argument_list|)
decl_stmt|;
name|Reviewdb
operator|.
name|ChangeMessage
name|expectedProto
init|=
name|Reviewdb
operator|.
name|ChangeMessage
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKey
argument_list|(
name|Reviewdb
operator|.
name|ChangeMessage_Key
operator|.
name|newBuilder
argument_list|()
operator|.
name|setChangeId
argument_list|(
name|Reviewdb
operator|.
name|Change_Id
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
literal|543
argument_list|)
argument_list|)
operator|.
name|setUuid
argument_list|(
literal|"change-message-21"
argument_list|)
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
name|ChangeMessage
name|changeMessage
init|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|543
argument_list|)
argument_list|,
literal|"change-message-21"
argument_list|)
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|63
argument_list|)
argument_list|,
operator|new
name|Timestamp
argument_list|(
literal|9876543
argument_list|)
argument_list|,
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|34
argument_list|)
argument_list|,
literal|13
argument_list|)
argument_list|)
decl_stmt|;
name|changeMessage
operator|.
name|setMessage
argument_list|(
literal|"This is a change message."
argument_list|)
expr_stmt|;
name|changeMessage
operator|.
name|setTag
argument_list|(
literal|"An arbitrary tag."
argument_list|)
expr_stmt|;
name|changeMessage
operator|.
name|setRealAuthor
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|10003
argument_list|)
argument_list|)
expr_stmt|;
name|ChangeMessage
name|convertedChangeMessage
init|=
name|changeMessageProtoConverter
operator|.
name|fromProto
argument_list|(
name|changeMessageProtoConverter
operator|.
name|toProto
argument_list|(
name|changeMessage
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|convertedChangeMessage
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|changeMessage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|mainValuesConvertedToProtoAndBackAgain ()
specifier|public
name|void
name|mainValuesConvertedToProtoAndBackAgain
parameter_list|()
block|{
name|ChangeMessage
name|changeMessage
init|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|543
argument_list|)
argument_list|,
literal|"change-message-21"
argument_list|)
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|63
argument_list|)
argument_list|,
operator|new
name|Timestamp
argument_list|(
literal|9876543
argument_list|)
argument_list|,
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|34
argument_list|)
argument_list|,
literal|13
argument_list|)
argument_list|)
decl_stmt|;
name|ChangeMessage
name|convertedChangeMessage
init|=
name|changeMessageProtoConverter
operator|.
name|fromProto
argument_list|(
name|changeMessageProtoConverter
operator|.
name|toProto
argument_list|(
name|changeMessage
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|convertedChangeMessage
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|changeMessage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|mandatoryValuesConvertedToProtoAndBackAgain ()
specifier|public
name|void
name|mandatoryValuesConvertedToProtoAndBackAgain
parameter_list|()
block|{
name|ChangeMessage
name|changeMessage
init|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
literal|543
argument_list|)
argument_list|,
literal|"change-message-21"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ChangeMessage
name|convertedChangeMessage
init|=
name|changeMessageProtoConverter
operator|.
name|fromProto
argument_list|(
name|changeMessageProtoConverter
operator|.
name|toProto
argument_list|(
name|changeMessage
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|convertedChangeMessage
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|changeMessage
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
name|ChangeMessage
name|proto
init|=
name|Reviewdb
operator|.
name|ChangeMessage
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKey
argument_list|(
name|Reviewdb
operator|.
name|ChangeMessage_Key
operator|.
name|newBuilder
argument_list|()
operator|.
name|setChangeId
argument_list|(
name|Reviewdb
operator|.
name|Change_Id
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
literal|543
argument_list|)
argument_list|)
operator|.
name|setUuid
argument_list|(
literal|"change-message-21"
argument_list|)
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
name|ChangeMessage
argument_list|>
name|parser
init|=
name|changeMessageProtoConverter
operator|.
name|getParser
argument_list|()
decl_stmt|;
name|Reviewdb
operator|.
name|ChangeMessage
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
name|ChangeMessage
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
literal|"key"
argument_list|,
name|ChangeMessage
operator|.
name|Key
operator|.
name|class
argument_list|)
operator|.
name|put
argument_list|(
literal|"author"
argument_list|,
name|Account
operator|.
name|Id
operator|.
name|class
argument_list|)
operator|.
name|put
argument_list|(
literal|"writtenOn"
argument_list|,
name|Timestamp
operator|.
name|class
argument_list|)
operator|.
name|put
argument_list|(
literal|"message"
argument_list|,
name|String
operator|.
name|class
argument_list|)
operator|.
name|put
argument_list|(
literal|"patchset"
argument_list|,
name|PatchSet
operator|.
name|Id
operator|.
name|class
argument_list|)
operator|.
name|put
argument_list|(
literal|"tag"
argument_list|,
name|String
operator|.
name|class
argument_list|)
operator|.
name|put
argument_list|(
literal|"realAuthor"
argument_list|,
name|Account
operator|.
name|Id
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

