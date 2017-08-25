begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.testutil
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|testutil
package|;
end_package

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
name|ImmutableSet
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
name|common
operator|.
name|Nullable
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
name|common
operator|.
name|TimeUtil
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
name|gerrit
operator|.
name|server
operator|.
name|account
operator|.
name|AccountState
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
name|config
operator|.
name|AllUsersName
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
name|config
operator|.
name|AllUsersNameProvider
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
name|Map
import|;
end_import

begin_comment
comment|/** Fake implementation of {@link AccountCache} for testing. */
end_comment

begin_class
DECL|class|FakeAccountCache
specifier|public
class|class
name|FakeAccountCache
implements|implements
name|AccountCache
block|{
DECL|field|byId
specifier|private
specifier|final
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|AccountState
argument_list|>
name|byId
decl_stmt|;
DECL|field|byUsername
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|AccountState
argument_list|>
name|byUsername
decl_stmt|;
DECL|method|FakeAccountCache ()
specifier|public
name|FakeAccountCache
parameter_list|()
block|{
name|byId
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|byUsername
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get (Account.Id accountId)
specifier|public
specifier|synchronized
name|AccountState
name|get
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
name|AccountState
name|state
init|=
name|byId
operator|.
name|get
argument_list|(
name|accountId
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
return|return
name|state
return|;
block|}
return|return
name|newState
argument_list|(
operator|new
name|Account
argument_list|(
name|accountId
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
DECL|method|getOrNull (Account.Id accountId)
specifier|public
specifier|synchronized
name|AccountState
name|getOrNull
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
return|return
name|byId
operator|.
name|get
argument_list|(
name|accountId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getByUsername (String username)
specifier|public
specifier|synchronized
name|AccountState
name|getByUsername
parameter_list|(
name|String
name|username
parameter_list|)
block|{
return|return
name|byUsername
operator|.
name|get
argument_list|(
name|username
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|evict (Account.Id accountId)
specifier|public
specifier|synchronized
name|void
name|evict
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
name|byId
operator|.
name|remove
argument_list|(
name|accountId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|evictAllNoReindex ()
specifier|public
specifier|synchronized
name|void
name|evictAllNoReindex
parameter_list|()
block|{
name|byId
operator|.
name|clear
argument_list|()
expr_stmt|;
name|byUsername
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|method|put (Account account)
specifier|public
specifier|synchronized
name|void
name|put
parameter_list|(
name|Account
name|account
parameter_list|)
block|{
name|AccountState
name|state
init|=
name|newState
argument_list|(
name|account
argument_list|)
decl_stmt|;
name|byId
operator|.
name|put
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|,
name|state
argument_list|)
expr_stmt|;
if|if
condition|(
name|account
operator|.
name|getUserName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|byUsername
operator|.
name|put
argument_list|(
name|account
operator|.
name|getUserName
argument_list|()
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|newState (Account account)
specifier|private
specifier|static
name|AccountState
name|newState
parameter_list|(
name|Account
name|account
parameter_list|)
block|{
return|return
operator|new
name|AccountState
argument_list|(
operator|new
name|AllUsersName
argument_list|(
name|AllUsersNameProvider
operator|.
name|DEFAULT
argument_list|)
argument_list|,
name|account
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

