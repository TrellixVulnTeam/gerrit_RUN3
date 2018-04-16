begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.account
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
package|;
end_package

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
name|Splitter
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/** An SSH key approved for use by an {@link Account}. */
end_comment

begin_class
annotation|@
name|AutoValue
DECL|class|AccountSshKey
specifier|public
specifier|abstract
class|class
name|AccountSshKey
block|{
DECL|method|create (Account.Id accountId, int seq, String sshPublicKey)
specifier|public
specifier|static
name|AccountSshKey
name|create
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|int
name|seq
parameter_list|,
name|String
name|sshPublicKey
parameter_list|)
block|{
return|return
name|create
argument_list|(
name|accountId
argument_list|,
name|seq
argument_list|,
name|sshPublicKey
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|createInvalid (Account.Id accountId, int seq, String sshPublicKey)
specifier|public
specifier|static
name|AccountSshKey
name|createInvalid
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|int
name|seq
parameter_list|,
name|String
name|sshPublicKey
parameter_list|)
block|{
return|return
name|create
argument_list|(
name|accountId
argument_list|,
name|seq
argument_list|,
name|sshPublicKey
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|createInvalid (AccountSshKey key)
specifier|public
specifier|static
name|AccountSshKey
name|createInvalid
parameter_list|(
name|AccountSshKey
name|key
parameter_list|)
block|{
return|return
name|create
argument_list|(
name|key
operator|.
name|accountId
argument_list|()
argument_list|,
name|key
operator|.
name|seq
argument_list|()
argument_list|,
name|key
operator|.
name|sshPublicKey
argument_list|()
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|create ( Account.Id accountId, int seq, String sshPublicKey, boolean valid)
specifier|public
specifier|static
name|AccountSshKey
name|create
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|int
name|seq
parameter_list|,
name|String
name|sshPublicKey
parameter_list|,
name|boolean
name|valid
parameter_list|)
block|{
return|return
operator|new
name|AutoValue_AccountSshKey
operator|.
name|Builder
argument_list|()
operator|.
name|setAccountId
argument_list|(
name|accountId
argument_list|)
operator|.
name|setSeq
argument_list|(
name|seq
argument_list|)
operator|.
name|setSshPublicKey
argument_list|(
name|stripOffNewLines
argument_list|(
name|sshPublicKey
argument_list|)
argument_list|)
operator|.
name|setValid
argument_list|(
name|valid
operator|&&
name|seq
operator|>
literal|0
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|stripOffNewLines (String s)
specifier|private
specifier|static
name|String
name|stripOffNewLines
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|replace
argument_list|(
literal|"\n"
argument_list|,
literal|""
argument_list|)
operator|.
name|replace
argument_list|(
literal|"\r"
argument_list|,
literal|""
argument_list|)
return|;
block|}
DECL|method|accountId ()
specifier|public
specifier|abstract
name|Account
operator|.
name|Id
name|accountId
parameter_list|()
function_decl|;
DECL|method|seq ()
specifier|public
specifier|abstract
name|int
name|seq
parameter_list|()
function_decl|;
DECL|method|sshPublicKey ()
specifier|public
specifier|abstract
name|String
name|sshPublicKey
parameter_list|()
function_decl|;
DECL|method|valid ()
specifier|public
specifier|abstract
name|boolean
name|valid
parameter_list|()
function_decl|;
DECL|method|publicKeyPart (int index, String defaultValue)
specifier|private
name|String
name|publicKeyPart
parameter_list|(
name|int
name|index
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|String
name|s
init|=
name|sshPublicKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
operator|&&
name|s
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|parts
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|' '
argument_list|)
operator|.
name|splitToList
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|size
argument_list|()
operator|>
name|index
condition|)
block|{
return|return
name|parts
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
block|}
return|return
name|defaultValue
return|;
block|}
DECL|method|algorithm ()
specifier|public
name|String
name|algorithm
parameter_list|()
block|{
return|return
name|publicKeyPart
argument_list|(
literal|0
argument_list|,
literal|"none"
argument_list|)
return|;
block|}
DECL|method|encodedKey ()
specifier|public
name|String
name|encodedKey
parameter_list|()
block|{
return|return
name|publicKeyPart
argument_list|(
literal|1
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|comment ()
specifier|public
name|String
name|comment
parameter_list|()
block|{
return|return
name|publicKeyPart
argument_list|(
literal|2
argument_list|,
literal|""
argument_list|)
return|;
block|}
annotation|@
name|AutoValue
operator|.
name|Builder
DECL|class|Builder
specifier|abstract
specifier|static
class|class
name|Builder
block|{
DECL|method|setAccountId (Account.Id accountId)
specifier|public
specifier|abstract
name|Builder
name|setAccountId
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
function_decl|;
DECL|method|setSeq (int seq)
specifier|public
specifier|abstract
name|Builder
name|setSeq
parameter_list|(
name|int
name|seq
parameter_list|)
function_decl|;
DECL|method|setSshPublicKey (String sshPublicKey)
specifier|public
specifier|abstract
name|Builder
name|setSshPublicKey
parameter_list|(
name|String
name|sshPublicKey
parameter_list|)
function_decl|;
DECL|method|setValid (boolean valid)
specifier|public
specifier|abstract
name|Builder
name|setValid
parameter_list|(
name|boolean
name|valid
parameter_list|)
function_decl|;
DECL|method|build ()
specifier|public
specifier|abstract
name|AccountSshKey
name|build
parameter_list|()
function_decl|;
block|}
block|}
end_class

end_unit

