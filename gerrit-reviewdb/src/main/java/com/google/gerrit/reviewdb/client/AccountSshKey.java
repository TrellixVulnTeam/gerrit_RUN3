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
DECL|package|com.google.gerrit.reviewdb.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|client
package|;
end_package

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
name|Column
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
name|IntKey
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

begin_comment
comment|/** An SSH key approved for use by an {@link Account}. */
end_comment

begin_class
DECL|class|AccountSshKey
specifier|public
specifier|final
class|class
name|AccountSshKey
block|{
DECL|class|Id
specifier|public
specifier|static
class|class
name|Id
extends|extends
name|IntKey
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|1
argument_list|)
DECL|field|accountId
specifier|protected
name|Account
operator|.
name|Id
name|accountId
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|)
DECL|field|seq
specifier|protected
name|int
name|seq
decl_stmt|;
DECL|method|Id ()
specifier|protected
name|Id
parameter_list|()
block|{
name|accountId
operator|=
operator|new
name|Account
operator|.
name|Id
argument_list|()
expr_stmt|;
block|}
DECL|method|Id (final Account.Id a, final int s)
specifier|public
name|Id
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|a
parameter_list|,
specifier|final
name|int
name|s
parameter_list|)
block|{
name|accountId
operator|=
name|a
expr_stmt|;
name|seq
operator|=
name|s
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParentKey ()
specifier|public
name|Account
operator|.
name|Id
name|getParentKey
parameter_list|()
block|{
return|return
name|accountId
return|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|int
name|get
parameter_list|()
block|{
return|return
name|seq
return|;
block|}
annotation|@
name|Override
DECL|method|set (int newValue)
specifier|protected
name|void
name|set
parameter_list|(
name|int
name|newValue
parameter_list|)
block|{
name|seq
operator|=
name|newValue
expr_stmt|;
block|}
block|}
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|1
argument_list|,
name|name
operator|=
name|Column
operator|.
name|NONE
argument_list|)
DECL|field|id
specifier|protected
name|AccountSshKey
operator|.
name|Id
name|id
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|,
name|length
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
DECL|field|sshPublicKey
specifier|protected
name|String
name|sshPublicKey
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|3
argument_list|)
DECL|field|valid
specifier|protected
name|boolean
name|valid
decl_stmt|;
DECL|method|AccountSshKey ()
specifier|protected
name|AccountSshKey
parameter_list|()
block|{   }
DECL|method|AccountSshKey (final AccountSshKey.Id i, final String pub)
specifier|public
name|AccountSshKey
parameter_list|(
specifier|final
name|AccountSshKey
operator|.
name|Id
name|i
parameter_list|,
specifier|final
name|String
name|pub
parameter_list|)
block|{
name|id
operator|=
name|i
expr_stmt|;
name|sshPublicKey
operator|=
name|pub
expr_stmt|;
name|valid
operator|=
literal|true
expr_stmt|;
comment|// We can assume it is fine.
block|}
DECL|method|getAccount ()
specifier|public
name|Account
operator|.
name|Id
name|getAccount
parameter_list|()
block|{
return|return
name|id
operator|.
name|accountId
return|;
block|}
DECL|method|getKey ()
specifier|public
name|AccountSshKey
operator|.
name|Id
name|getKey
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|getSshPublicKey ()
specifier|public
name|String
name|getSshPublicKey
parameter_list|()
block|{
return|return
name|sshPublicKey
return|;
block|}
DECL|method|getAlgorithm ()
specifier|public
name|String
name|getAlgorithm
parameter_list|()
block|{
specifier|final
name|String
name|s
init|=
name|getSshPublicKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
operator|||
name|s
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|"none"
return|;
block|}
specifier|final
name|String
index|[]
name|parts
init|=
name|s
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|<
literal|1
condition|)
block|{
return|return
literal|"none"
return|;
block|}
return|return
name|parts
index|[
literal|0
index|]
return|;
block|}
DECL|method|getEncodedKey ()
specifier|public
name|String
name|getEncodedKey
parameter_list|()
block|{
specifier|final
name|String
name|s
init|=
name|getSshPublicKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
operator|||
name|s
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|String
index|[]
name|parts
init|=
name|s
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|<
literal|2
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|parts
index|[
literal|1
index|]
return|;
block|}
DECL|method|getComment ()
specifier|public
name|String
name|getComment
parameter_list|()
block|{
specifier|final
name|String
name|s
init|=
name|getSshPublicKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
operator|||
name|s
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|""
return|;
block|}
specifier|final
name|String
index|[]
name|parts
init|=
name|s
operator|.
name|split
argument_list|(
literal|" "
argument_list|,
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|<
literal|3
condition|)
block|{
return|return
literal|""
return|;
block|}
return|return
name|parts
index|[
literal|2
index|]
return|;
block|}
DECL|method|isValid ()
specifier|public
name|boolean
name|isValid
parameter_list|()
block|{
return|return
name|valid
return|;
block|}
DECL|method|setInvalid ()
specifier|public
name|void
name|setInvalid
parameter_list|()
block|{
name|valid
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|AccountSshKey
condition|)
block|{
name|AccountSshKey
name|other
init|=
operator|(
name|AccountSshKey
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|id
argument_list|,
name|other
operator|.
name|id
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|sshPublicKey
argument_list|,
name|other
operator|.
name|sshPublicKey
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|valid
argument_list|,
name|other
operator|.
name|valid
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|id
argument_list|,
name|sshPublicKey
argument_list|,
name|valid
argument_list|)
return|;
block|}
block|}
end_class

end_unit

