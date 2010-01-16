begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
name|gerrit
operator|.
name|common
operator|.
name|errors
operator|.
name|NoSuchEntityException
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandom
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
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_comment
comment|/** Operation to generate a password for an account. */
end_comment

begin_class
DECL|class|GeneratePassword
specifier|public
class|class
name|GeneratePassword
implements|implements
name|Callable
argument_list|<
name|AccountExternalId
argument_list|>
block|{
DECL|field|LEN
specifier|private
specifier|static
specifier|final
name|int
name|LEN
init|=
literal|12
decl_stmt|;
DECL|field|rng
specifier|private
specifier|static
specifier|final
name|SecureRandom
name|rng
decl_stmt|;
static|static
block|{
try|try
block|{
name|rng
operator|=
name|SecureRandom
operator|.
name|getInstance
argument_list|(
literal|"SHA1PRNG"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot create RNG for password generator"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (AccountExternalId.Key forUser)
name|GeneratePassword
name|create
parameter_list|(
name|AccountExternalId
operator|.
name|Key
name|forUser
parameter_list|)
function_decl|;
block|}
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
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
DECL|field|forUser
specifier|private
specifier|final
name|AccountExternalId
operator|.
name|Key
name|forUser
decl_stmt|;
annotation|@
name|Inject
DECL|method|GeneratePassword (final AccountCache accountCache, final ReviewDb db, final IdentifiedUser user, @Assisted AccountExternalId.Key forUser)
name|GeneratePassword
parameter_list|(
specifier|final
name|AccountCache
name|accountCache
parameter_list|,
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|IdentifiedUser
name|user
parameter_list|,
annotation|@
name|Assisted
name|AccountExternalId
operator|.
name|Key
name|forUser
parameter_list|)
block|{
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
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
name|forUser
operator|=
name|forUser
expr_stmt|;
block|}
DECL|method|call ()
specifier|public
name|AccountExternalId
name|call
parameter_list|()
throws|throws
name|OrmException
throws|,
name|NoSuchEntityException
block|{
name|AccountExternalId
name|id
init|=
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|get
argument_list|(
name|forUser
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
operator|||
operator|!
name|user
operator|.
name|getAccountId
argument_list|()
operator|.
name|equals
argument_list|(
name|id
operator|.
name|getAccountId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NoSuchEntityException
argument_list|()
throw|;
block|}
name|id
operator|.
name|setPassword
argument_list|(
name|generate
argument_list|()
argument_list|)
expr_stmt|;
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|id
argument_list|)
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
return|return
name|id
return|;
block|}
DECL|method|generate ()
specifier|private
name|String
name|generate
parameter_list|()
block|{
name|byte
index|[]
name|rand
init|=
operator|new
name|byte
index|[
name|LEN
index|]
decl_stmt|;
name|rng
operator|.
name|nextBytes
argument_list|(
name|rand
argument_list|)
expr_stmt|;
name|byte
index|[]
name|enc
init|=
name|Base64
operator|.
name|encodeBase64
argument_list|(
name|rand
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|(
name|LEN
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|LEN
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|enc
index|[
name|i
index|]
operator|==
literal|'='
condition|)
block|{
break|break;
block|}
name|r
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|enc
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

