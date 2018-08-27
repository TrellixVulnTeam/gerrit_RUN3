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
DECL|package|com.google.gerrit.server.account.externalids
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|account
operator|.
name|externalids
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
name|ImmutableSetMultimap
operator|.
name|toImmutableSetMultimap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|auto
operator|.
name|value
operator|.
name|AutoValue
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
name|base
operator|.
name|Strings
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
name|ImmutableSetMultimap
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
name|SetMultimap
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
name|server
operator|.
name|cache
operator|.
name|proto
operator|.
name|Cache
operator|.
name|AllExternalIdsProto
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
name|server
operator|.
name|cache
operator|.
name|proto
operator|.
name|Cache
operator|.
name|AllExternalIdsProto
operator|.
name|ExternalIdProto
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
name|server
operator|.
name|cache
operator|.
name|serialize
operator|.
name|CacheSerializer
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
name|server
operator|.
name|cache
operator|.
name|serialize
operator|.
name|ProtoCacheSerializers
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
name|server
operator|.
name|cache
operator|.
name|serialize
operator|.
name|ProtoCacheSerializers
operator|.
name|ObjectIdConverter
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

begin_comment
comment|/** Cache value containing all external IDs. */
end_comment

begin_class
annotation|@
name|AutoValue
DECL|class|AllExternalIds
specifier|public
specifier|abstract
class|class
name|AllExternalIds
block|{
DECL|method|create (SetMultimap<Account.Id, ExternalId> byAccount)
specifier|static
name|AllExternalIds
name|create
parameter_list|(
name|SetMultimap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ExternalId
argument_list|>
name|byAccount
parameter_list|)
block|{
return|return
operator|new
name|AutoValue_AllExternalIds
argument_list|(
name|ImmutableSetMultimap
operator|.
name|copyOf
argument_list|(
name|byAccount
argument_list|)
argument_list|,
name|byEmailCopy
argument_list|(
name|byAccount
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|create (Collection<ExternalId> externalIds)
specifier|static
name|AllExternalIds
name|create
parameter_list|(
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|externalIds
parameter_list|)
block|{
return|return
operator|new
name|AutoValue_AllExternalIds
argument_list|(
name|externalIds
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|toImmutableSetMultimap
argument_list|(
name|e
lambda|->
name|e
operator|.
name|accountId
argument_list|()
argument_list|,
name|e
lambda|->
name|e
argument_list|)
argument_list|)
argument_list|,
name|byEmailCopy
argument_list|(
name|externalIds
argument_list|)
argument_list|)
return|;
block|}
DECL|method|byEmailCopy ( Collection<ExternalId> externalIds)
specifier|private
specifier|static
name|ImmutableSetMultimap
argument_list|<
name|String
argument_list|,
name|ExternalId
argument_list|>
name|byEmailCopy
parameter_list|(
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|externalIds
parameter_list|)
block|{
return|return
name|externalIds
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|e
lambda|->
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|e
operator|.
name|email
argument_list|()
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableSetMultimap
argument_list|(
name|e
lambda|->
name|e
operator|.
name|email
argument_list|()
argument_list|,
name|e
lambda|->
name|e
argument_list|)
argument_list|)
return|;
block|}
DECL|method|byAccount ()
specifier|public
specifier|abstract
name|ImmutableSetMultimap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ExternalId
argument_list|>
name|byAccount
parameter_list|()
function_decl|;
DECL|method|byEmail ()
specifier|public
specifier|abstract
name|ImmutableSetMultimap
argument_list|<
name|String
argument_list|,
name|ExternalId
argument_list|>
name|byEmail
parameter_list|()
function_decl|;
DECL|enum|Serializer
enum|enum
name|Serializer
implements|implements
name|CacheSerializer
argument_list|<
name|AllExternalIds
argument_list|>
block|{
DECL|enumConstant|INSTANCE
name|INSTANCE
block|;
annotation|@
name|Override
DECL|method|serialize (AllExternalIds object)
specifier|public
name|byte
index|[]
name|serialize
parameter_list|(
name|AllExternalIds
name|object
parameter_list|)
block|{
name|ObjectIdConverter
name|idConverter
init|=
name|ObjectIdConverter
operator|.
name|create
argument_list|()
decl_stmt|;
name|AllExternalIdsProto
operator|.
name|Builder
name|allBuilder
init|=
name|AllExternalIdsProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|object
operator|.
name|byAccount
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|extId
lambda|->
name|toProto
argument_list|(
name|idConverter
argument_list|,
name|extId
argument_list|)
argument_list|)
operator|.
name|forEach
argument_list|(
name|allBuilder
operator|::
name|addExternalId
argument_list|)
expr_stmt|;
return|return
name|ProtoCacheSerializers
operator|.
name|toByteArray
argument_list|(
name|allBuilder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toProto (ObjectIdConverter idConverter, ExternalId externalId)
specifier|private
specifier|static
name|ExternalIdProto
name|toProto
parameter_list|(
name|ObjectIdConverter
name|idConverter
parameter_list|,
name|ExternalId
name|externalId
parameter_list|)
block|{
name|ExternalIdProto
operator|.
name|Builder
name|b
init|=
name|ExternalIdProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setKey
argument_list|(
name|externalId
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|setAccountId
argument_list|(
name|externalId
operator|.
name|accountId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|externalId
operator|.
name|email
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|setEmail
argument_list|(
name|externalId
operator|.
name|email
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|externalId
operator|.
name|password
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|setPassword
argument_list|(
name|externalId
operator|.
name|password
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|externalId
operator|.
name|blobId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|setBlobId
argument_list|(
name|idConverter
operator|.
name|toByteString
argument_list|(
name|externalId
operator|.
name|blobId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|deserialize (byte[] in)
specifier|public
name|AllExternalIds
name|deserialize
parameter_list|(
name|byte
index|[]
name|in
parameter_list|)
block|{
name|ObjectIdConverter
name|idConverter
init|=
name|ObjectIdConverter
operator|.
name|create
argument_list|()
decl_stmt|;
return|return
name|create
argument_list|(
name|ProtoCacheSerializers
operator|.
name|parseUnchecked
argument_list|(
name|AllExternalIdsProto
operator|.
name|parser
argument_list|()
argument_list|,
name|in
argument_list|)
operator|.
name|getExternalIdList
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|proto
lambda|->
name|toExternalId
argument_list|(
name|idConverter
argument_list|,
name|proto
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toExternalId (ObjectIdConverter idConverter, ExternalIdProto proto)
specifier|private
specifier|static
name|ExternalId
name|toExternalId
parameter_list|(
name|ObjectIdConverter
name|idConverter
parameter_list|,
name|ExternalIdProto
name|proto
parameter_list|)
block|{
return|return
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|Key
operator|.
name|parse
argument_list|(
name|proto
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|proto
operator|.
name|getAccountId
argument_list|()
argument_list|)
argument_list|,
comment|// ExternalId treats null and empty strings the same, so no need to distinguish here.
name|proto
operator|.
name|getEmail
argument_list|()
argument_list|,
name|proto
operator|.
name|getPassword
argument_list|()
argument_list|,
operator|!
name|proto
operator|.
name|getBlobId
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|?
name|idConverter
operator|.
name|fromByteString
argument_list|(
name|proto
operator|.
name|getBlobId
argument_list|()
argument_list|)
else|:
literal|null
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

