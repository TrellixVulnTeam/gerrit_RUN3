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
name|data
operator|.
name|AccountInfoCache
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
name|data
operator|.
name|AccountInfoCacheFactory
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
name|AccountAgreement
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_class
DECL|class|AgreementInfo
specifier|public
class|class
name|AgreementInfo
block|{
DECL|field|accounts
specifier|protected
name|AccountInfoCache
name|accounts
decl_stmt|;
DECL|field|accepted
specifier|protected
name|List
argument_list|<
name|AccountAgreement
argument_list|>
name|accepted
decl_stmt|;
DECL|field|agreements
specifier|protected
name|Map
argument_list|<
name|ContributorAgreement
operator|.
name|Id
argument_list|,
name|ContributorAgreement
argument_list|>
name|agreements
decl_stmt|;
DECL|method|AgreementInfo ()
specifier|public
name|AgreementInfo
parameter_list|()
block|{   }
DECL|method|load (final Account.Id me, final ReviewDb db)
specifier|public
name|void
name|load
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|me
parameter_list|,
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|AccountInfoCacheFactory
name|acc
init|=
operator|new
name|AccountInfoCacheFactory
argument_list|(
name|db
argument_list|)
decl_stmt|;
name|accepted
operator|=
name|db
operator|.
name|accountAgreements
argument_list|()
operator|.
name|byAccount
argument_list|(
name|me
argument_list|)
operator|.
name|toList
argument_list|()
expr_stmt|;
name|agreements
operator|=
operator|new
name|HashMap
argument_list|<
name|ContributorAgreement
operator|.
name|Id
argument_list|,
name|ContributorAgreement
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|AccountAgreement
name|a
range|:
name|accepted
control|)
block|{
name|acc
operator|.
name|want
argument_list|(
name|a
operator|.
name|getReviewedBy
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|agreements
operator|.
name|containsKey
argument_list|(
name|a
operator|.
name|getAgreementId
argument_list|()
argument_list|)
condition|)
block|{
name|agreements
operator|.
name|put
argument_list|(
name|a
operator|.
name|getAgreementId
argument_list|()
argument_list|,
name|db
operator|.
name|contributorAgreements
argument_list|()
operator|.
name|get
argument_list|(
name|a
operator|.
name|getAgreementId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|accounts
operator|=
name|acc
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

