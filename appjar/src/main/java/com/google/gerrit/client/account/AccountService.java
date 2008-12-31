begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2008 Google Inc.
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
DECL|package|com.google.gerrit.client.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
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
name|AccountProjectWatch
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
name|ContributorAgreement
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
name|rpc
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
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|rpc
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
name|client
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
name|client
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
DECL|interface|AccountService
specifier|public
interface|interface
name|AccountService
extends|extends
name|RemoteJsonService
block|{
annotation|@
name|SignInRequired
DECL|method|myAccount (AsyncCallback<Account> callback)
name|void
name|myAccount
parameter_list|(
name|AsyncCallback
argument_list|<
name|Account
argument_list|>
name|callback
parameter_list|)
function_decl|;
annotation|@
name|SignInRequired
DECL|method|changeDefaultContext (short newSetting, AsyncCallback<VoidResult> callback)
name|void
name|changeDefaultContext
parameter_list|(
name|short
name|newSetting
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
DECL|method|myProjectWatch (AsyncCallback<List<AccountProjectWatchInfo>> callback)
name|void
name|myProjectWatch
parameter_list|(
name|AsyncCallback
argument_list|<
name|List
argument_list|<
name|AccountProjectWatchInfo
argument_list|>
argument_list|>
name|callback
parameter_list|)
function_decl|;
annotation|@
name|SignInRequired
DECL|method|addProjectWatch (String projectName, AsyncCallback<AccountProjectWatchInfo> callback)
name|void
name|addProjectWatch
parameter_list|(
name|String
name|projectName
parameter_list|,
name|AsyncCallback
argument_list|<
name|AccountProjectWatchInfo
argument_list|>
name|callback
parameter_list|)
function_decl|;
annotation|@
name|SignInRequired
DECL|method|deleteProjectWatches (Set<AccountProjectWatch.Key> keys, AsyncCallback<VoidResult> callback)
name|void
name|deleteProjectWatches
parameter_list|(
name|Set
argument_list|<
name|AccountProjectWatch
operator|.
name|Key
argument_list|>
name|keys
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
DECL|method|myAgreements (AsyncCallback<AgreementInfo> callback)
name|void
name|myAgreements
parameter_list|(
name|AsyncCallback
argument_list|<
name|AgreementInfo
argument_list|>
name|callback
parameter_list|)
function_decl|;
annotation|@
name|SignInRequired
DECL|method|enterAgreement (ContributorAgreement.Id id, AsyncCallback<VoidResult> callback)
name|void
name|enterAgreement
parameter_list|(
name|ContributorAgreement
operator|.
name|Id
name|id
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

