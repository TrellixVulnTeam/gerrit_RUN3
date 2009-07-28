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
DECL|package|com.google.gerrit.server.ssh
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|ssh
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
name|client
operator|.
name|reviewdb
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
name|client
operator|.
name|reviewdb
operator|.
name|AccountSshKey
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
name|client
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
name|gwtorm
operator|.
name|client
operator|.
name|SchemaFactory
import|;
end_import

begin_import
import|import
name|net
operator|.
name|sf
operator|.
name|ehcache
operator|.
name|constructs
operator|.
name|blocking
operator|.
name|CacheEntryFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Collections
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

begin_class
DECL|class|SshKeyCacheEntryFactory
specifier|public
class|class
name|SshKeyCacheEntryFactory
implements|implements
name|CacheEntryFactory
block|{
DECL|field|log
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|schema
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
DECL|method|SshKeyCacheEntryFactory (final SchemaFactory<ReviewDb> sf)
specifier|public
name|SshKeyCacheEntryFactory
parameter_list|(
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|sf
parameter_list|)
block|{
name|schema
operator|=
name|sf
expr_stmt|;
block|}
DECL|method|createEntry (final Object genericKey)
specifier|public
name|Object
name|createEntry
parameter_list|(
specifier|final
name|Object
name|genericKey
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|String
name|username
init|=
operator|(
name|String
operator|)
name|genericKey
decl_stmt|;
specifier|final
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|List
argument_list|<
name|Account
argument_list|>
name|matches
init|=
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|bySshUserName
argument_list|(
name|username
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
if|if
condition|(
name|matches
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
expr|<
name|SshKeyCacheEntry
operator|>
name|emptyList
argument_list|()
return|;
block|}
specifier|final
name|List
argument_list|<
name|SshKeyCacheEntry
argument_list|>
name|kl
init|=
operator|new
name|ArrayList
argument_list|<
name|SshKeyCacheEntry
argument_list|>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Account
name|a
range|:
name|matches
control|)
block|{
for|for
control|(
specifier|final
name|AccountSshKey
name|k
range|:
name|db
operator|.
name|accountSshKeys
argument_list|()
operator|.
name|valid
argument_list|(
name|a
operator|.
name|getId
argument_list|()
argument_list|)
control|)
block|{
name|add
argument_list|(
name|db
argument_list|,
name|kl
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|kl
argument_list|)
return|;
block|}
finally|finally
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|add (ReviewDb db, List<SshKeyCacheEntry> kl, AccountSshKey k)
specifier|private
name|void
name|add
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|List
argument_list|<
name|SshKeyCacheEntry
argument_list|>
name|kl
parameter_list|,
name|AccountSshKey
name|k
parameter_list|)
block|{
try|try
block|{
name|kl
operator|.
name|add
argument_list|(
operator|new
name|SshKeyCacheEntry
argument_list|(
name|k
operator|.
name|getKey
argument_list|()
argument_list|,
name|SshUtil
operator|.
name|parse
argument_list|(
name|k
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OutOfMemoryError
name|e
parameter_list|)
block|{
comment|// This is the only case where we assume the problem has nothing
comment|// to do with the key object, and instead we must abort this load.
comment|//
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|markInvalid
argument_list|(
name|db
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|markInvalid (final ReviewDb db, final AccountSshKey k)
specifier|private
name|void
name|markInvalid
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|AccountSshKey
name|k
parameter_list|)
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Flagging SSH key "
operator|+
name|k
operator|.
name|getKey
argument_list|()
operator|+
literal|" invalid"
argument_list|)
expr_stmt|;
name|k
operator|.
name|setInvalid
argument_list|()
expr_stmt|;
name|db
operator|.
name|accountSshKeys
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|k
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to mark SSH key"
operator|+
name|k
operator|.
name|getKey
argument_list|()
operator|+
literal|" invalid"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

