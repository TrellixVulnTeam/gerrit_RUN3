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
import|import static
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
name|ExternalId
operator|.
name|SCHEME_USERNAME
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
name|toSet
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
name|errors
operator|.
name|NameAlreadyUsedException
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
name|server
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
name|ssh
operator|.
name|SshKeyCache
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|common
operator|.
name|VoidResult
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
name|OrmDuplicateKeyException
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
name|Collection
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|errors
operator|.
name|ConfigInvalidException
import|;
end_import

begin_comment
comment|/** Operation to change the username of an account. */
end_comment

begin_class
DECL|class|ChangeUserName
specifier|public
class|class
name|ChangeUserName
implements|implements
name|Callable
argument_list|<
name|VoidResult
argument_list|>
block|{
DECL|field|USERNAME_CANNOT_BE_CHANGED
specifier|public
specifier|static
specifier|final
name|String
name|USERNAME_CANNOT_BE_CHANGED
init|=
literal|"Username cannot be changed."
decl_stmt|;
DECL|field|USER_NAME_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|USER_NAME_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|Account
operator|.
name|USER_NAME_PATTERN
argument_list|)
decl_stmt|;
comment|/** Generic factory to change any user's username. */
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (ReviewDb db, IdentifiedUser user, String newUsername)
name|ChangeUserName
name|create
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|IdentifiedUser
name|user
parameter_list|,
name|String
name|newUsername
parameter_list|)
function_decl|;
block|}
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|sshKeyCache
specifier|private
specifier|final
name|SshKeyCache
name|sshKeyCache
decl_stmt|;
DECL|field|externalIdsUpdateFactory
specifier|private
specifier|final
name|ExternalIdsUpdate
operator|.
name|Server
name|externalIdsUpdateFactory
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
DECL|field|newUsername
specifier|private
specifier|final
name|String
name|newUsername
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeUserName ( AccountCache accountCache, SshKeyCache sshKeyCache, ExternalIdsUpdate.Server externalIdsUpdateFactory, @Assisted ReviewDb db, @Assisted IdentifiedUser user, @Nullable @Assisted String newUsername)
name|ChangeUserName
parameter_list|(
name|AccountCache
name|accountCache
parameter_list|,
name|SshKeyCache
name|sshKeyCache
parameter_list|,
name|ExternalIdsUpdate
operator|.
name|Server
name|externalIdsUpdateFactory
parameter_list|,
annotation|@
name|Assisted
name|ReviewDb
name|db
parameter_list|,
annotation|@
name|Assisted
name|IdentifiedUser
name|user
parameter_list|,
annotation|@
name|Nullable
annotation|@
name|Assisted
name|String
name|newUsername
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
name|sshKeyCache
operator|=
name|sshKeyCache
expr_stmt|;
name|this
operator|.
name|externalIdsUpdateFactory
operator|=
name|externalIdsUpdateFactory
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
name|newUsername
operator|=
name|newUsername
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|VoidResult
name|call
parameter_list|()
throws|throws
name|OrmException
throws|,
name|NameAlreadyUsedException
throws|,
name|InvalidUserNameException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|old
init|=
name|ExternalId
operator|.
name|from
argument_list|(
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|byAccount
argument_list|(
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|toList
argument_list|()
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|e
lambda|->
name|e
operator|.
name|isScheme
argument_list|(
name|SCHEME_USERNAME
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|old
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|USERNAME_CANNOT_BE_CHANGED
argument_list|)
throw|;
block|}
name|ExternalIdsUpdate
name|externalIdsUpdate
init|=
name|externalIdsUpdateFactory
operator|.
name|create
argument_list|()
decl_stmt|;
if|if
condition|(
name|newUsername
operator|!=
literal|null
operator|&&
operator|!
name|newUsername
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|USER_NAME_PATTERN
operator|.
name|matcher
argument_list|(
name|newUsername
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidUserNameException
argument_list|()
throw|;
block|}
name|ExternalId
operator|.
name|Key
name|key
init|=
name|ExternalId
operator|.
name|Key
operator|.
name|create
argument_list|(
name|SCHEME_USERNAME
argument_list|,
name|newUsername
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|password
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ExternalId
name|i
range|:
name|old
control|)
block|{
if|if
condition|(
name|i
operator|.
name|password
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|password
operator|=
name|i
operator|.
name|password
argument_list|()
expr_stmt|;
block|}
block|}
name|externalIdsUpdate
operator|.
name|insert
argument_list|(
name|db
argument_list|,
name|ExternalId
operator|.
name|create
argument_list|(
name|key
argument_list|,
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|,
literal|null
argument_list|,
name|password
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmDuplicateKeyException
name|dupeErr
parameter_list|)
block|{
comment|// If we are using this identity, don't report the exception.
comment|//
name|ExternalId
name|other
init|=
name|ExternalId
operator|.
name|from
argument_list|(
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|get
argument_list|(
name|key
operator|.
name|asAccountExternalIdKey
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|!=
literal|null
operator|&&
name|other
operator|.
name|accountId
argument_list|()
operator|.
name|equals
argument_list|(
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|VoidResult
operator|.
name|INSTANCE
return|;
block|}
comment|// Otherwise, someone else has this identity.
comment|//
throw|throw
operator|new
name|NameAlreadyUsedException
argument_list|(
name|newUsername
argument_list|)
throw|;
block|}
block|}
comment|// If we have any older user names, remove them.
comment|//
name|externalIdsUpdate
operator|.
name|delete
argument_list|(
name|db
argument_list|,
name|old
argument_list|)
expr_stmt|;
for|for
control|(
name|ExternalId
name|extId
range|:
name|old
control|)
block|{
name|sshKeyCache
operator|.
name|evict
argument_list|(
name|extId
operator|.
name|key
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|accountCache
operator|.
name|evictByUsername
argument_list|(
name|extId
operator|.
name|key
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|accountCache
operator|.
name|evictByUsername
argument_list|(
name|newUsername
argument_list|)
expr_stmt|;
name|sshKeyCache
operator|.
name|evict
argument_list|(
name|newUsername
argument_list|)
expr_stmt|;
return|return
name|VoidResult
operator|.
name|INSTANCE
return|;
block|}
block|}
end_class

end_unit

