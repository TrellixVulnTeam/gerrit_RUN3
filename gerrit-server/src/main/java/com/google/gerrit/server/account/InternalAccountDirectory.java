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
name|ArrayListMultimap
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
name|Lists
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
name|Multimap
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
name|extensions
operator|.
name|registration
operator|.
name|DynamicItem
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
name|account
operator|.
name|AccountInfo
operator|.
name|AvatarInfo
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
name|avatar
operator|.
name|AvatarProvider
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
name|AbstractModule
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
name|Provider
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
name|Singleton
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
annotation|@
name|Singleton
DECL|class|InternalAccountDirectory
specifier|public
class|class
name|InternalAccountDirectory
extends|extends
name|AccountDirectory
block|{
DECL|class|Module
specifier|public
specifier|static
class|class
name|Module
extends|extends
name|AbstractModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|AccountDirectory
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|InternalAccountDirectory
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|avatar
specifier|private
specifier|final
name|DynamicItem
argument_list|<
name|AvatarProvider
argument_list|>
name|avatar
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|InternalAccountDirectory (Provider<ReviewDb> db, AccountCache accountCache, DynamicItem<AvatarProvider> avatar, IdentifiedUser.GenericFactory userFactory)
name|InternalAccountDirectory
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|AccountCache
name|accountCache
parameter_list|,
name|DynamicItem
argument_list|<
name|AvatarProvider
argument_list|>
name|avatar
parameter_list|,
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
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
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|avatar
operator|=
name|avatar
expr_stmt|;
name|this
operator|.
name|userFactory
operator|=
name|userFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fillAccountInfo ( Iterable<? extends AccountInfo> in, Set<FillOptions> options)
specifier|public
name|void
name|fillAccountInfo
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|AccountInfo
argument_list|>
name|in
parameter_list|,
name|Set
argument_list|<
name|FillOptions
argument_list|>
name|options
parameter_list|)
throws|throws
name|DirectoryException
block|{
name|Multimap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|AccountInfo
argument_list|>
name|missing
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountInfo
name|info
range|:
name|in
control|)
block|{
name|AccountState
name|state
init|=
name|accountCache
operator|.
name|getIfPresent
argument_list|(
name|info
operator|.
name|_id
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
name|fill
argument_list|(
name|info
argument_list|,
name|state
operator|.
name|getAccount
argument_list|()
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|missing
operator|.
name|put
argument_list|(
name|info
operator|.
name|_id
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|missing
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
for|for
control|(
name|Account
name|account
range|:
name|db
operator|.
name|get
argument_list|()
operator|.
name|accounts
argument_list|()
operator|.
name|get
argument_list|(
name|missing
operator|.
name|keySet
argument_list|()
argument_list|)
control|)
block|{
for|for
control|(
name|AccountInfo
name|info
range|:
name|missing
operator|.
name|get
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|)
control|)
block|{
name|fill
argument_list|(
name|info
argument_list|,
name|account
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DirectoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|fill (AccountInfo info, Account account, Set<FillOptions> options)
specifier|private
name|void
name|fill
parameter_list|(
name|AccountInfo
name|info
parameter_list|,
name|Account
name|account
parameter_list|,
name|Set
argument_list|<
name|FillOptions
argument_list|>
name|options
parameter_list|)
block|{
if|if
condition|(
name|options
operator|.
name|contains
argument_list|(
name|FillOptions
operator|.
name|NAME
argument_list|)
condition|)
block|{
name|info
operator|.
name|name
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|account
operator|.
name|getFullName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|name
operator|==
literal|null
condition|)
block|{
name|info
operator|.
name|name
operator|=
name|account
operator|.
name|getUserName
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|options
operator|.
name|contains
argument_list|(
name|FillOptions
operator|.
name|EMAIL
argument_list|)
condition|)
block|{
name|info
operator|.
name|email
operator|=
name|account
operator|.
name|getPreferredEmail
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|options
operator|.
name|contains
argument_list|(
name|FillOptions
operator|.
name|USERNAME
argument_list|)
condition|)
block|{
name|info
operator|.
name|username
operator|=
name|account
operator|.
name|getUserName
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|options
operator|.
name|contains
argument_list|(
name|FillOptions
operator|.
name|AVATARS
argument_list|)
condition|)
block|{
name|AvatarProvider
name|ap
init|=
name|avatar
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|ap
operator|!=
literal|null
condition|)
block|{
name|info
operator|.
name|avatars
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|String
name|u
init|=
name|ap
operator|.
name|getUrl
argument_list|(
name|userFactory
operator|.
name|create
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|AvatarInfo
operator|.
name|DEFAULT_SIZE
argument_list|)
decl_stmt|;
if|if
condition|(
name|u
operator|!=
literal|null
condition|)
block|{
name|AvatarInfo
name|a
init|=
operator|new
name|AvatarInfo
argument_list|()
decl_stmt|;
name|a
operator|.
name|url
operator|=
name|u
expr_stmt|;
name|a
operator|.
name|height
operator|=
name|AvatarInfo
operator|.
name|DEFAULT_SIZE
expr_stmt|;
name|info
operator|.
name|avatars
operator|.
name|add
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

