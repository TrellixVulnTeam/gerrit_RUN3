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
DECL|package|com.google.gerrit.common.data
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|data
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
name|audit
operator|.
name|Audit
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
name|auth
operator|.
name|SignInRequired
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
name|client
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
name|client
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
name|reviewdb
operator|.
name|client
operator|.
name|ContactInformation
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
name|AsyncCallback
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
name|RemoteJsonService
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
name|RpcImpl
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
name|RpcImpl
operator|.
name|Version
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
name|Set
import|;
end_import

begin_interface
annotation|@
name|RpcImpl
argument_list|(
name|version
operator|=
name|Version
operator|.
name|V2_0
argument_list|)
DECL|interface|AccountSecurity
specifier|public
interface|interface
name|AccountSecurity
extends|extends
name|RemoteJsonService
block|{
annotation|@
name|Audit
annotation|@
name|SignInRequired
DECL|method|addSshKey (String keyText, AsyncCallback<AccountSshKey> callback)
name|void
name|addSshKey
parameter_list|(
name|String
name|keyText
parameter_list|,
name|AsyncCallback
argument_list|<
name|AccountSshKey
argument_list|>
name|callback
parameter_list|)
function_decl|;
annotation|@
name|Audit
annotation|@
name|SignInRequired
DECL|method|deleteSshKeys (Set<AccountSshKey.Id> ids, AsyncCallback<VoidResult> callback)
name|void
name|deleteSshKeys
parameter_list|(
name|Set
argument_list|<
name|AccountSshKey
operator|.
name|Id
argument_list|>
name|ids
parameter_list|,
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|callback
parameter_list|)
function_decl|;
annotation|@
name|Audit
annotation|@
name|SignInRequired
DECL|method|changeUserName (String newName, AsyncCallback<VoidResult> callback)
name|void
name|changeUserName
parameter_list|(
name|String
name|newName
parameter_list|,
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|callback
parameter_list|)
function_decl|;
annotation|@
name|SignInRequired
DECL|method|myExternalIds (AsyncCallback<List<AccountExternalId>> callback)
name|void
name|myExternalIds
parameter_list|(
name|AsyncCallback
argument_list|<
name|List
argument_list|<
name|AccountExternalId
argument_list|>
argument_list|>
name|callback
parameter_list|)
function_decl|;
annotation|@
name|Audit
annotation|@
name|SignInRequired
DECL|method|deleteExternalIds (Set<AccountExternalId.Key> keys, AsyncCallback<Set<AccountExternalId.Key>> callback)
name|void
name|deleteExternalIds
parameter_list|(
name|Set
argument_list|<
name|AccountExternalId
operator|.
name|Key
argument_list|>
name|keys
parameter_list|,
name|AsyncCallback
argument_list|<
name|Set
argument_list|<
name|AccountExternalId
operator|.
name|Key
argument_list|>
argument_list|>
name|callback
parameter_list|)
function_decl|;
annotation|@
name|Audit
annotation|@
name|SignInRequired
DECL|method|updateContact (String fullName, String emailAddr, ContactInformation info, AsyncCallback<Account> callback)
name|void
name|updateContact
parameter_list|(
name|String
name|fullName
parameter_list|,
name|String
name|emailAddr
parameter_list|,
name|ContactInformation
name|info
parameter_list|,
name|AsyncCallback
argument_list|<
name|Account
argument_list|>
name|callback
parameter_list|)
function_decl|;
annotation|@
name|Audit
annotation|@
name|SignInRequired
DECL|method|enterAgreement (String agreementName, AsyncCallback<VoidResult> callback)
name|void
name|enterAgreement
parameter_list|(
name|String
name|agreementName
parameter_list|,
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|callback
parameter_list|)
function_decl|;
annotation|@
name|Audit
annotation|@
name|SignInRequired
DECL|method|validateEmail (String token, AsyncCallback<VoidResult> callback)
name|void
name|validateEmail
parameter_list|(
name|String
name|token
parameter_list|,
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|callback
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

