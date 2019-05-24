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
import|import
name|com
operator|.
name|google
operator|.
name|errorprone
operator|.
name|annotations
operator|.
name|Immutable
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
name|Entities
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
name|protobuf
operator|.
name|Parser
import|;
end_import

begin_enum
annotation|@
name|Immutable
DECL|enum|ChangeKeyProtoConverter
specifier|public
enum|enum
name|ChangeKeyProtoConverter
implements|implements
name|ProtoConverter
argument_list|<
name|Entities
operator|.
name|Change_Key
argument_list|,
name|Change
operator|.
name|Key
argument_list|>
block|{
DECL|enumConstant|INSTANCE
name|INSTANCE
block|;
annotation|@
name|Override
DECL|method|toProto (Change.Key key)
specifier|public
name|Entities
operator|.
name|Change_Key
name|toProto
parameter_list|(
name|Change
operator|.
name|Key
name|key
parameter_list|)
block|{
return|return
name|Entities
operator|.
name|Change_Key
operator|.
name|newBuilder
argument_list|()
operator|.
name|setId
argument_list|(
name|key
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fromProto (Entities.Change_Key proto)
specifier|public
name|Change
operator|.
name|Key
name|fromProto
parameter_list|(
name|Entities
operator|.
name|Change_Key
name|proto
parameter_list|)
block|{
return|return
name|Change
operator|.
name|key
argument_list|(
name|proto
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getParser ()
specifier|public
name|Parser
argument_list|<
name|Entities
operator|.
name|Change_Key
argument_list|>
name|getParser
parameter_list|()
block|{
return|return
name|Entities
operator|.
name|Change_Key
operator|.
name|parser
argument_list|()
return|;
block|}
block|}
end_enum

end_unit

