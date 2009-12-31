begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.httpd.rpc.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|rpc
operator|.
name|account
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
name|httpd
operator|.
name|rpc
operator|.
name|Handler
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
name|AccountExternalId
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
name|ReviewDb
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
name|IdentifiedUser
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
name|account
operator|.
name|AccountByEmailCache
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
name|account
operator|.
name|AccountCache
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
name|client
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
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Map
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

begin_class
DECL|class|DeleteExternalIds
class|class
name|DeleteExternalIds
extends|extends
name|Handler
argument_list|<
name|Set
argument_list|<
name|AccountExternalId
operator|.
name|Key
argument_list|>
argument_list|>
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (Set<AccountExternalId.Key> keys)
name|DeleteExternalIds
name|create
parameter_list|(
name|Set
argument_list|<
name|AccountExternalId
operator|.
name|Key
argument_list|>
name|keys
parameter_list|)
function_decl|;
block|}
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|IdentifiedUser
name|user
decl_stmt|;
DECL|field|detailFactory
specifier|private
specifier|final
name|ExternalIdDetailFactory
name|detailFactory
decl_stmt|;
DECL|field|byEmailCache
specifier|private
specifier|final
name|AccountByEmailCache
name|byEmailCache
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|keys
specifier|private
specifier|final
name|Set
argument_list|<
name|AccountExternalId
operator|.
name|Key
argument_list|>
name|keys
decl_stmt|;
annotation|@
name|Inject
DECL|method|DeleteExternalIds (final ReviewDb db, final IdentifiedUser user, final ExternalIdDetailFactory detailFactory, final AccountByEmailCache byEmailCache, final AccountCache accountCache, @Assisted final Set<AccountExternalId.Key> keys)
name|DeleteExternalIds
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|IdentifiedUser
name|user
parameter_list|,
specifier|final
name|ExternalIdDetailFactory
name|detailFactory
parameter_list|,
specifier|final
name|AccountByEmailCache
name|byEmailCache
parameter_list|,
specifier|final
name|AccountCache
name|accountCache
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|Set
argument_list|<
name|AccountExternalId
operator|.
name|Key
argument_list|>
name|keys
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|detailFactory
operator|=
name|detailFactory
expr_stmt|;
name|this
operator|.
name|byEmailCache
operator|=
name|byEmailCache
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|keys
operator|=
name|keys
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Set
argument_list|<
name|AccountExternalId
operator|.
name|Key
argument_list|>
name|call
parameter_list|()
throws|throws
name|OrmException
block|{
specifier|final
name|Map
argument_list|<
name|AccountExternalId
operator|.
name|Key
argument_list|,
name|AccountExternalId
argument_list|>
name|have
init|=
name|have
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AccountExternalId
argument_list|>
name|toDelete
init|=
operator|new
name|ArrayList
argument_list|<
name|AccountExternalId
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountExternalId
operator|.
name|Key
name|k
range|:
name|keys
control|)
block|{
specifier|final
name|AccountExternalId
name|id
init|=
name|have
operator|.
name|get
argument_list|(
name|k
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
name|id
operator|.
name|canDelete
argument_list|()
condition|)
block|{
name|toDelete
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|toDelete
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|delete
argument_list|(
name|toDelete
argument_list|)
expr_stmt|;
name|accountCache
operator|.
name|evict
argument_list|(
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AccountExternalId
name|e
range|:
name|toDelete
control|)
block|{
name|byEmailCache
operator|.
name|evict
argument_list|(
name|e
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|toKeySet
argument_list|(
name|toDelete
argument_list|)
return|;
block|}
DECL|method|have ()
specifier|private
name|Map
argument_list|<
name|AccountExternalId
operator|.
name|Key
argument_list|,
name|AccountExternalId
argument_list|>
name|have
parameter_list|()
throws|throws
name|OrmException
block|{
name|Map
argument_list|<
name|AccountExternalId
operator|.
name|Key
argument_list|,
name|AccountExternalId
argument_list|>
name|r
decl_stmt|;
name|r
operator|=
operator|new
name|HashMap
argument_list|<
name|AccountExternalId
operator|.
name|Key
argument_list|,
name|AccountExternalId
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|AccountExternalId
name|i
range|:
name|detailFactory
operator|.
name|call
argument_list|()
control|)
block|{
name|r
operator|.
name|put
argument_list|(
name|i
operator|.
name|getKey
argument_list|()
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
DECL|method|toKeySet (List<AccountExternalId> toDelete)
specifier|private
name|Set
argument_list|<
name|AccountExternalId
operator|.
name|Key
argument_list|>
name|toKeySet
parameter_list|(
name|List
argument_list|<
name|AccountExternalId
argument_list|>
name|toDelete
parameter_list|)
block|{
name|Set
argument_list|<
name|AccountExternalId
operator|.
name|Key
argument_list|>
name|r
init|=
operator|new
name|HashSet
argument_list|<
name|AccountExternalId
operator|.
name|Key
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountExternalId
name|i
range|:
name|toDelete
control|)
block|{
name|r
operator|.
name|add
argument_list|(
name|i
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
block|}
end_class

end_unit

