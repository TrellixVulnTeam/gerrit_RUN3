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
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectId
import|;
end_import

begin_comment
comment|/** Caches external IDs of all accounts */
end_comment

begin_interface
DECL|interface|ExternalIdCache
interface|interface
name|ExternalIdCache
block|{
DECL|method|onCreate (ObjectId newNotesRev, Iterable<ExternalId> extId)
name|void
name|onCreate
parameter_list|(
name|ObjectId
name|newNotesRev
parameter_list|,
name|Iterable
argument_list|<
name|ExternalId
argument_list|>
name|extId
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|onUpdate (ObjectId newNotesRev, Iterable<ExternalId> extId)
name|void
name|onUpdate
parameter_list|(
name|ObjectId
name|newNotesRev
parameter_list|,
name|Iterable
argument_list|<
name|ExternalId
argument_list|>
name|extId
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|onReplace ( ObjectId newNotesRev, Account.Id accountId, Iterable<ExternalId> toRemove, Iterable<ExternalId> toAdd)
name|void
name|onReplace
parameter_list|(
name|ObjectId
name|newNotesRev
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|Iterable
argument_list|<
name|ExternalId
argument_list|>
name|toRemove
parameter_list|,
name|Iterable
argument_list|<
name|ExternalId
argument_list|>
name|toAdd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|onReplaceByKeys ( ObjectId newNotesRev, Account.Id accountId, Iterable<ExternalId.Key> toRemove, Iterable<ExternalId> toAdd)
name|void
name|onReplaceByKeys
parameter_list|(
name|ObjectId
name|newNotesRev
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|Iterable
argument_list|<
name|ExternalId
operator|.
name|Key
argument_list|>
name|toRemove
parameter_list|,
name|Iterable
argument_list|<
name|ExternalId
argument_list|>
name|toAdd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|onReplaceByKeys ( ObjectId newNotesRev, Iterable<ExternalId.Key> toRemove, Iterable<ExternalId> toAdd)
name|void
name|onReplaceByKeys
parameter_list|(
name|ObjectId
name|newNotesRev
parameter_list|,
name|Iterable
argument_list|<
name|ExternalId
operator|.
name|Key
argument_list|>
name|toRemove
parameter_list|,
name|Iterable
argument_list|<
name|ExternalId
argument_list|>
name|toAdd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|onReplace (ObjectId newNotesRev, Iterable<ExternalId> toRemove, Iterable<ExternalId> toAdd)
name|void
name|onReplace
parameter_list|(
name|ObjectId
name|newNotesRev
parameter_list|,
name|Iterable
argument_list|<
name|ExternalId
argument_list|>
name|toRemove
parameter_list|,
name|Iterable
argument_list|<
name|ExternalId
argument_list|>
name|toAdd
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|onRemove (ObjectId newNotesRev, Iterable<ExternalId> extId)
name|void
name|onRemove
parameter_list|(
name|ObjectId
name|newNotesRev
parameter_list|,
name|Iterable
argument_list|<
name|ExternalId
argument_list|>
name|extId
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|onRemoveByKeys ( ObjectId newNotesRev, Account.Id accountId, Iterable<ExternalId.Key> extIdKeys)
name|void
name|onRemoveByKeys
parameter_list|(
name|ObjectId
name|newNotesRev
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|Iterable
argument_list|<
name|ExternalId
operator|.
name|Key
argument_list|>
name|extIdKeys
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|onRemoveByKeys (ObjectId newNotesRev, Iterable<ExternalId.Key> extIdKeys)
name|void
name|onRemoveByKeys
parameter_list|(
name|ObjectId
name|newNotesRev
parameter_list|,
name|Iterable
argument_list|<
name|ExternalId
operator|.
name|Key
argument_list|>
name|extIdKeys
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|byAccount (Account.Id accountId)
name|Set
argument_list|<
name|ExternalId
argument_list|>
name|byAccount
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|onCreate (ObjectId newNotesRev, ExternalId extId)
specifier|default
name|void
name|onCreate
parameter_list|(
name|ObjectId
name|newNotesRev
parameter_list|,
name|ExternalId
name|extId
parameter_list|)
throws|throws
name|IOException
block|{
name|onCreate
argument_list|(
name|newNotesRev
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|extId
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|onRemove (ObjectId newNotesRev, ExternalId extId)
specifier|default
name|void
name|onRemove
parameter_list|(
name|ObjectId
name|newNotesRev
parameter_list|,
name|ExternalId
name|extId
parameter_list|)
throws|throws
name|IOException
block|{
name|onRemove
argument_list|(
name|newNotesRev
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|extId
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|onRemoveByKey (ObjectId newNotesRev, Account.Id accountId, ExternalId.Key extIdKey)
specifier|default
name|void
name|onRemoveByKey
parameter_list|(
name|ObjectId
name|newNotesRev
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|ExternalId
operator|.
name|Key
name|extIdKey
parameter_list|)
throws|throws
name|IOException
block|{
name|onRemoveByKeys
argument_list|(
name|newNotesRev
argument_list|,
name|accountId
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|extIdKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|onUpdate (ObjectId newNotesRev, ExternalId updatedExtId)
specifier|default
name|void
name|onUpdate
parameter_list|(
name|ObjectId
name|newNotesRev
parameter_list|,
name|ExternalId
name|updatedExtId
parameter_list|)
throws|throws
name|IOException
block|{
name|onUpdate
argument_list|(
name|newNotesRev
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|updatedExtId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_interface

end_unit

