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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_interface
DECL|interface|Realm
specifier|public
interface|interface
name|Realm
block|{
comment|/** Can the end-user modify this field of their own account? */
DECL|method|allowsEdit (Account.FieldName field)
specifier|public
name|boolean
name|allowsEdit
parameter_list|(
name|Account
operator|.
name|FieldName
name|field
parameter_list|)
function_decl|;
DECL|method|authenticate (AuthRequest who)
specifier|public
name|AuthRequest
name|authenticate
parameter_list|(
name|AuthRequest
name|who
parameter_list|)
throws|throws
name|AccountException
function_decl|;
DECL|method|link (ReviewDb db, Account.Id to, AuthRequest who)
specifier|public
name|AuthRequest
name|link
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Account
operator|.
name|Id
name|to
parameter_list|,
name|AuthRequest
name|who
parameter_list|)
throws|throws
name|AccountException
function_decl|;
DECL|method|unlink (ReviewDb db, Account.Id to, AuthRequest who)
specifier|public
name|AuthRequest
name|unlink
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Account
operator|.
name|Id
name|to
parameter_list|,
name|AuthRequest
name|who
parameter_list|)
throws|throws
name|AccountException
function_decl|;
DECL|method|onCreateAccount (AuthRequest who, Account account)
specifier|public
name|void
name|onCreateAccount
parameter_list|(
name|AuthRequest
name|who
parameter_list|,
name|Account
name|account
parameter_list|)
function_decl|;
comment|/** @return true if the user has the given email address. */
DECL|method|hasEmailAddress (IdentifiedUser who, String email)
specifier|public
name|boolean
name|hasEmailAddress
parameter_list|(
name|IdentifiedUser
name|who
parameter_list|,
name|String
name|email
parameter_list|)
function_decl|;
comment|/** @return all known email addresses for the identified user. */
DECL|method|getEmailAddresses (IdentifiedUser who)
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getEmailAddresses
parameter_list|(
name|IdentifiedUser
name|who
parameter_list|)
function_decl|;
comment|/**    * Locate an account whose local username is the given account name.    *<p>    * Generally this only works for local realms, such as one backed by an LDAP    * directory, or where there is an {@link EmailExpander} configured that knows    * how to convert the accountName into an email address, and then locate the    * user by that email address.    */
DECL|method|lookup (String accountName)
specifier|public
name|Account
operator|.
name|Id
name|lookup
parameter_list|(
name|String
name|accountName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

