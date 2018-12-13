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
name|collect
operator|.
name|ImmutableList
operator|.
name|toImmutableList
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
name|collect
operator|.
name|Iterables
operator|.
name|getOnlyElement
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
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|Lists
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
name|Protos
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
name|PatchSet
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
name|RevId
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|protobuf
operator|.
name|CodecFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|protobuf
operator|.
name|ProtobufCodec
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|server
operator|.
name|OrmException
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
name|ByteString
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
name|CodedOutputStream
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
name|MessageLite
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_comment
comment|// TODO(aliceks): Delete after proving binary compatibility.
end_comment

begin_class
DECL|class|PatchSetConverterCompatibilityTest
specifier|public
class|class
name|PatchSetConverterCompatibilityTest
block|{
DECL|field|patchSetCodec
specifier|private
specifier|final
name|ProtobufCodec
argument_list|<
name|PatchSet
argument_list|>
name|patchSetCodec
init|=
name|CodecFactory
operator|.
name|encoder
argument_list|(
name|PatchSet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|patchSetProtoConverter
specifier|private
specifier|final
name|PatchSetProtoConverter
name|patchSetProtoConverter
init|=
name|PatchSetProtoConverter
operator|.
name|INSTANCE
decl_stmt|;
annotation|@
name|Test
DECL|method|changeIndexFieldWithAllValuesIsBinaryCompatible ()
specifier|public
name|void
name|changeIndexFieldWithAllValuesIsBinaryCompatible
parameter_list|()
throws|throws
name|Exception
block|{
name|PatchSet
name|patchSet
init|=
operator|new
name|PatchSet
argument_list|(
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
literal|103
argument_list|)
argument_list|,
literal|73
argument_list|)
argument_list|)
decl_stmt|;
name|patchSet
operator|.
name|setRevision
argument_list|(
operator|new
name|RevId
argument_list|(
literal|"aabbccddeeff"
argument_list|)
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setUploader
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|452
argument_list|)
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setCreatedOn
argument_list|(
operator|new
name|Timestamp
argument_list|(
literal|930349320L
argument_list|)
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setGroups
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"group1, group2"
argument_list|)
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setPushCertificate
argument_list|(
literal|"my push certificate"
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setDescription
argument_list|(
literal|"This is a patch set description."
argument_list|)
expr_stmt|;
name|ImmutableList
argument_list|<
name|PatchSet
argument_list|>
name|patchSets
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|patchSet
argument_list|)
decl_stmt|;
name|byte
index|[]
name|resultOfOldConverter
init|=
name|getOnlyElement
argument_list|(
name|convertToProtos_old
argument_list|(
name|patchSetCodec
argument_list|,
name|patchSets
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|resultOfNewConverter
init|=
name|getOnlyElement
argument_list|(
name|convertToProtos_new
argument_list|(
name|patchSetProtoConverter
argument_list|,
name|patchSets
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultOfNewConverter
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|resultOfOldConverter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|changeIndexFieldWithMandatoryValuesIsBinaryCompatible ()
specifier|public
name|void
name|changeIndexFieldWithMandatoryValuesIsBinaryCompatible
parameter_list|()
throws|throws
name|Exception
block|{
name|PatchSet
name|patchSet
init|=
operator|new
name|PatchSet
argument_list|(
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
literal|103
argument_list|)
argument_list|,
literal|73
argument_list|)
argument_list|)
decl_stmt|;
name|ImmutableList
argument_list|<
name|PatchSet
argument_list|>
name|patchSets
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|patchSet
argument_list|)
decl_stmt|;
name|byte
index|[]
name|resultOfOldConverter
init|=
name|getOnlyElement
argument_list|(
name|convertToProtos_old
argument_list|(
name|patchSetCodec
argument_list|,
name|patchSets
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|resultOfNewConverter
init|=
name|getOnlyElement
argument_list|(
name|convertToProtos_new
argument_list|(
name|patchSetProtoConverter
argument_list|,
name|patchSets
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultOfNewConverter
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|resultOfOldConverter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|changeNotesFieldWithAllValuesIsBinaryCompatible ()
specifier|public
name|void
name|changeNotesFieldWithAllValuesIsBinaryCompatible
parameter_list|()
block|{
name|PatchSet
name|patchSet
init|=
operator|new
name|PatchSet
argument_list|(
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
literal|103
argument_list|)
argument_list|,
literal|73
argument_list|)
argument_list|)
decl_stmt|;
name|patchSet
operator|.
name|setRevision
argument_list|(
operator|new
name|RevId
argument_list|(
literal|"aabbccddeeff"
argument_list|)
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setUploader
argument_list|(
operator|new
name|Account
operator|.
name|Id
argument_list|(
literal|452
argument_list|)
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setCreatedOn
argument_list|(
operator|new
name|Timestamp
argument_list|(
literal|930349320L
argument_list|)
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setGroups
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"group1, group2"
argument_list|)
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setPushCertificate
argument_list|(
literal|"my push certificate"
argument_list|)
expr_stmt|;
name|patchSet
operator|.
name|setDescription
argument_list|(
literal|"This is a patch set description."
argument_list|)
expr_stmt|;
name|ByteString
name|resultOfOldConverter
init|=
name|Protos
operator|.
name|toByteString
argument_list|(
name|patchSet
argument_list|,
name|patchSetCodec
argument_list|)
decl_stmt|;
name|ByteString
name|resultOfNewConverter
init|=
name|toByteString
argument_list|(
name|patchSet
argument_list|,
name|patchSetProtoConverter
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultOfNewConverter
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|resultOfOldConverter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|changeNotesFieldWithMandatoryValuesIsBinaryCompatible ()
specifier|public
name|void
name|changeNotesFieldWithMandatoryValuesIsBinaryCompatible
parameter_list|()
block|{
name|PatchSet
name|patchSet
init|=
operator|new
name|PatchSet
argument_list|(
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
literal|103
argument_list|)
argument_list|,
literal|73
argument_list|)
argument_list|)
decl_stmt|;
name|ByteString
name|resultOfOldConverter
init|=
name|Protos
operator|.
name|toByteString
argument_list|(
name|patchSet
argument_list|,
name|patchSetCodec
argument_list|)
decl_stmt|;
name|ByteString
name|resultOfNewConverter
init|=
name|toByteString
argument_list|(
name|patchSet
argument_list|,
name|patchSetProtoConverter
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultOfNewConverter
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|resultOfOldConverter
argument_list|)
expr_stmt|;
block|}
comment|// Copied from ChangeField.
DECL|method|convertToProtos_old (ProtobufCodec<T> codec, Collection<T> objs)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|convertToProtos_old
parameter_list|(
name|ProtobufCodec
argument_list|<
name|T
argument_list|>
name|codec
parameter_list|,
name|Collection
argument_list|<
name|T
argument_list|>
name|objs
parameter_list|)
throws|throws
name|OrmException
block|{
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|result
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|objs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|256
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|T
name|obj
range|:
name|objs
control|)
block|{
name|out
operator|.
name|reset
argument_list|()
expr_stmt|;
name|CodedOutputStream
name|cos
init|=
name|CodedOutputStream
operator|.
name|newInstance
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|codec
operator|.
name|encode
argument_list|(
name|obj
argument_list|,
name|cos
argument_list|)
expr_stmt|;
name|cos
operator|.
name|flush
argument_list|()
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
comment|// Copied from ChangeField.
DECL|method|convertToProtos_new ( ProtoConverter<?, T> converter, Collection<T> objects)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|convertToProtos_new
parameter_list|(
name|ProtoConverter
argument_list|<
name|?
argument_list|,
name|T
argument_list|>
name|converter
parameter_list|,
name|Collection
argument_list|<
name|T
argument_list|>
name|objects
parameter_list|)
block|{
return|return
name|objects
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|converter
operator|::
name|toProto
argument_list|)
operator|.
name|map
argument_list|(
name|Protos
operator|::
name|toByteArray
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableList
argument_list|()
argument_list|)
return|;
block|}
comment|// Copied from ChangeNotesState.Serializer.
DECL|method|toByteString (T object, ProtoConverter<?, T> converter)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|ByteString
name|toByteString
parameter_list|(
name|T
name|object
parameter_list|,
name|ProtoConverter
argument_list|<
name|?
argument_list|,
name|T
argument_list|>
name|converter
parameter_list|)
block|{
name|MessageLite
name|message
init|=
name|converter
operator|.
name|toProto
argument_list|(
name|object
argument_list|)
decl_stmt|;
return|return
name|Protos
operator|.
name|toByteString
argument_list|(
name|message
argument_list|)
return|;
block|}
block|}
end_class

end_unit

