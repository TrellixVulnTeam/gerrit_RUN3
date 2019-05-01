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
name|PatchSetApproval
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
name|Objects
import|;
end_import

begin_enum
DECL|enum|PatchSetApprovalProtoConverter
specifier|public
enum|enum
name|PatchSetApprovalProtoConverter
implements|implements
name|ProtoConverter
argument_list|<
name|Entities
operator|.
name|PatchSetApproval
argument_list|,
name|PatchSetApproval
argument_list|>
block|{
DECL|enumConstant|INSTANCE
name|INSTANCE
block|;
specifier|private
specifier|final
name|ProtoConverter
argument_list|<
name|Entities
operator|.
name|PatchSetApproval_Key
argument_list|,
name|PatchSetApproval
operator|.
name|Key
argument_list|>
DECL|field|patchSetApprovalKeyProtoConverter
name|patchSetApprovalKeyProtoConverter
init|=
name|PatchSetApprovalKeyProtoConverter
operator|.
name|INSTANCE
decl_stmt|;
DECL|field|accountIdConverter
specifier|private
specifier|final
name|ProtoConverter
argument_list|<
name|Entities
operator|.
name|Account_Id
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
name|accountIdConverter
init|=
name|AccountIdProtoConverter
operator|.
name|INSTANCE
decl_stmt|;
annotation|@
name|Override
DECL|method|toProto (PatchSetApproval patchSetApproval)
specifier|public
name|Entities
operator|.
name|PatchSetApproval
name|toProto
parameter_list|(
name|PatchSetApproval
name|patchSetApproval
parameter_list|)
block|{
name|Entities
operator|.
name|PatchSetApproval
operator|.
name|Builder
name|builder
init|=
name|Entities
operator|.
name|PatchSetApproval
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKey
argument_list|(
name|patchSetApprovalKeyProtoConverter
operator|.
name|toProto
argument_list|(
name|patchSetApproval
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setValue
argument_list|(
name|patchSetApproval
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|setGranted
argument_list|(
name|patchSetApproval
operator|.
name|getGranted
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
operator|.
name|setPostSubmit
argument_list|(
name|patchSetApproval
operator|.
name|isPostSubmit
argument_list|()
argument_list|)
decl_stmt|;
name|patchSetApproval
operator|.
name|getTag
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|builder
operator|::
name|setTag
argument_list|)
expr_stmt|;
name|Account
operator|.
name|Id
name|realAccountId
init|=
name|patchSetApproval
operator|.
name|getRealAccountId
argument_list|()
decl_stmt|;
comment|// PatchSetApproval#getRealAccountId automatically delegates to PatchSetApproval#getAccountId if
comment|// the real author is not set. However, the previous protobuf representation kept
comment|// 'realAccountId' empty if it wasn't set. To ensure binary compatibility, simulate the previous
comment|// behavior.
if|if
condition|(
name|realAccountId
operator|!=
literal|null
operator|&&
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|realAccountId
argument_list|,
name|patchSetApproval
operator|.
name|getAccountId
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|setRealAccountId
argument_list|(
name|accountIdConverter
operator|.
name|toProto
argument_list|(
name|realAccountId
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fromProto (Entities.PatchSetApproval proto)
specifier|public
name|PatchSetApproval
name|fromProto
parameter_list|(
name|Entities
operator|.
name|PatchSetApproval
name|proto
parameter_list|)
block|{
name|PatchSetApproval
name|patchSetApproval
init|=
operator|new
name|PatchSetApproval
argument_list|(
name|patchSetApprovalKeyProtoConverter
operator|.
name|fromProto
argument_list|(
name|proto
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
operator|(
name|short
operator|)
name|proto
operator|.
name|getValue
argument_list|()
argument_list|,
operator|new
name|Timestamp
argument_list|(
name|proto
operator|.
name|getGranted
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|proto
operator|.
name|hasTag
argument_list|()
condition|)
block|{
name|patchSetApproval
operator|.
name|setTag
argument_list|(
name|proto
operator|.
name|getTag
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|proto
operator|.
name|hasRealAccountId
argument_list|()
condition|)
block|{
name|patchSetApproval
operator|.
name|setRealAccountId
argument_list|(
name|accountIdConverter
operator|.
name|fromProto
argument_list|(
name|proto
operator|.
name|getRealAccountId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|proto
operator|.
name|hasPostSubmit
argument_list|()
condition|)
block|{
name|patchSetApproval
operator|.
name|setPostSubmit
argument_list|(
name|proto
operator|.
name|getPostSubmit
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|patchSetApproval
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
name|PatchSetApproval
argument_list|>
name|getParser
parameter_list|()
block|{
return|return
name|Entities
operator|.
name|PatchSetApproval
operator|.
name|parser
argument_list|()
return|;
block|}
block|}
end_enum

end_unit

