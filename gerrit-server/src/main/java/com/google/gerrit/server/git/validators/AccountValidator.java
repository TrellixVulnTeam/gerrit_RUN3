begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.git.validators
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|git
operator|.
name|validators
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
name|ImmutableList
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
name|AccountConfig
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
name|mail
operator|.
name|send
operator|.
name|OutgoingEmailValidator
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
name|ArrayList
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

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|RevWalk
import|;
end_import

begin_class
DECL|class|AccountValidator
specifier|public
class|class
name|AccountValidator
block|{
DECL|field|self
specifier|private
specifier|final
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|self
decl_stmt|;
DECL|field|emailValidator
specifier|private
specifier|final
name|OutgoingEmailValidator
name|emailValidator
decl_stmt|;
annotation|@
name|Inject
DECL|method|AccountValidator (Provider<IdentifiedUser> self, OutgoingEmailValidator emailValidator)
specifier|public
name|AccountValidator
parameter_list|(
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|self
parameter_list|,
name|OutgoingEmailValidator
name|emailValidator
parameter_list|)
block|{
name|this
operator|.
name|self
operator|=
name|self
expr_stmt|;
name|this
operator|.
name|emailValidator
operator|=
name|emailValidator
expr_stmt|;
block|}
DECL|method|validate ( Account.Id accountId, RevWalk rw, @Nullable ObjectId oldId, ObjectId newId)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|validate
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
annotation|@
name|Nullable
name|ObjectId
name|oldId
parameter_list|,
name|ObjectId
name|newId
parameter_list|)
throws|throws
name|IOException
block|{
name|Account
name|oldAccount
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|oldId
operator|!=
literal|null
operator|&&
operator|!
name|ObjectId
operator|.
name|zeroId
argument_list|()
operator|.
name|equals
argument_list|(
name|oldId
argument_list|)
condition|)
block|{
try|try
block|{
name|oldAccount
operator|=
name|loadAccount
argument_list|(
name|accountId
argument_list|,
name|rw
argument_list|,
name|oldId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
name|e
parameter_list|)
block|{
comment|// ignore, maybe the new commit is repairing it now
block|}
block|}
name|Account
name|newAccount
decl_stmt|;
try|try
block|{
name|newAccount
operator|=
name|loadAccount
argument_list|(
name|accountId
argument_list|,
name|rw
argument_list|,
name|newId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
name|e
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"commit '%s' has an invalid '%s' file for account '%s': %s"
argument_list|,
name|newId
operator|.
name|name
argument_list|()
argument_list|,
name|AccountConfig
operator|.
name|ACCOUNT_CONFIG
argument_list|,
name|accountId
operator|.
name|get
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|messages
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|accountId
operator|.
name|equals
argument_list|(
name|self
operator|.
name|get
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|&&
operator|!
name|newAccount
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|messages
operator|.
name|add
argument_list|(
literal|"cannot deactivate own account"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|newAccount
operator|.
name|getPreferredEmail
argument_list|()
operator|!=
literal|null
operator|&&
operator|(
name|oldAccount
operator|==
literal|null
operator|||
operator|!
name|newAccount
operator|.
name|getPreferredEmail
argument_list|()
operator|.
name|equals
argument_list|(
name|oldAccount
operator|.
name|getPreferredEmail
argument_list|()
argument_list|)
operator|)
condition|)
block|{
if|if
condition|(
operator|!
name|emailValidator
operator|.
name|isValid
argument_list|(
name|newAccount
operator|.
name|getPreferredEmail
argument_list|()
argument_list|)
condition|)
block|{
name|messages
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"invalid preferred email '%s' for account '%s'"
argument_list|,
name|newAccount
operator|.
name|getPreferredEmail
argument_list|()
argument_list|,
name|accountId
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|messages
argument_list|)
return|;
block|}
DECL|method|loadAccount (Account.Id accountId, RevWalk rw, ObjectId commit)
specifier|private
name|Account
name|loadAccount
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|ObjectId
name|commit
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|rw
operator|.
name|reset
argument_list|()
expr_stmt|;
name|AccountConfig
name|accountConfig
init|=
operator|new
name|AccountConfig
argument_list|(
literal|null
argument_list|,
name|accountId
argument_list|)
decl_stmt|;
name|accountConfig
operator|.
name|load
argument_list|(
name|rw
argument_list|,
name|commit
argument_list|)
expr_stmt|;
return|return
name|accountConfig
operator|.
name|getAccount
argument_list|()
return|;
block|}
block|}
end_class

end_unit

