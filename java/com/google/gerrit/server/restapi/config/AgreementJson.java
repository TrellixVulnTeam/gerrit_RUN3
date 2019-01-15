begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.restapi.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|restapi
operator|.
name|config
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
name|flogger
operator|.
name|FluentLogger
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
name|data
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
name|common
operator|.
name|data
operator|.
name|GroupReference
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
name|exceptions
operator|.
name|NoSuchGroupException
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
name|common
operator|.
name|AgreementInfo
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
name|CurrentUser
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
name|GroupControl
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
name|group
operator|.
name|GroupResource
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
name|permissions
operator|.
name|PermissionBackendException
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
name|restapi
operator|.
name|group
operator|.
name|GroupJson
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
name|Provider
import|;
end_import

begin_class
DECL|class|AgreementJson
specifier|public
class|class
name|AgreementJson
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|field|self
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
decl_stmt|;
DECL|field|identifiedUserFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|identifiedUserFactory
decl_stmt|;
DECL|field|genericGroupControlFactory
specifier|private
specifier|final
name|GroupControl
operator|.
name|GenericFactory
name|genericGroupControlFactory
decl_stmt|;
DECL|field|groupJson
specifier|private
specifier|final
name|GroupJson
name|groupJson
decl_stmt|;
annotation|@
name|Inject
DECL|method|AgreementJson ( Provider<CurrentUser> self, IdentifiedUser.GenericFactory identifiedUserFactory, GroupControl.GenericFactory genericGroupControlFactory, GroupJson groupJson)
name|AgreementJson
parameter_list|(
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
parameter_list|,
name|IdentifiedUser
operator|.
name|GenericFactory
name|identifiedUserFactory
parameter_list|,
name|GroupControl
operator|.
name|GenericFactory
name|genericGroupControlFactory
parameter_list|,
name|GroupJson
name|groupJson
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
name|identifiedUserFactory
operator|=
name|identifiedUserFactory
expr_stmt|;
name|this
operator|.
name|genericGroupControlFactory
operator|=
name|genericGroupControlFactory
expr_stmt|;
name|this
operator|.
name|groupJson
operator|=
name|groupJson
expr_stmt|;
block|}
DECL|method|format (ContributorAgreement ca)
specifier|public
name|AgreementInfo
name|format
parameter_list|(
name|ContributorAgreement
name|ca
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
name|AgreementInfo
name|info
init|=
operator|new
name|AgreementInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|name
operator|=
name|ca
operator|.
name|getName
argument_list|()
expr_stmt|;
name|info
operator|.
name|description
operator|=
name|ca
operator|.
name|getDescription
argument_list|()
expr_stmt|;
name|info
operator|.
name|url
operator|=
name|ca
operator|.
name|getAgreementUrl
argument_list|()
expr_stmt|;
name|GroupReference
name|autoVerifyGroup
init|=
name|ca
operator|.
name|getAutoVerify
argument_list|()
decl_stmt|;
if|if
condition|(
name|autoVerifyGroup
operator|!=
literal|null
operator|&&
name|self
operator|.
name|get
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
name|IdentifiedUser
name|user
init|=
name|identifiedUserFactory
operator|.
name|create
argument_list|(
name|self
operator|.
name|get
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|GroupControl
name|gc
init|=
name|genericGroupControlFactory
operator|.
name|controlFor
argument_list|(
name|user
argument_list|,
name|autoVerifyGroup
operator|.
name|getUUID
argument_list|()
argument_list|)
decl_stmt|;
name|GroupResource
name|group
init|=
operator|new
name|GroupResource
argument_list|(
name|gc
argument_list|)
decl_stmt|;
name|info
operator|.
name|autoVerifyGroup
operator|=
name|groupJson
operator|.
name|format
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchGroupException
decl||
name|OrmException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atWarning
argument_list|()
operator|.
name|log
argument_list|(
literal|"autoverify group \"%s\" does not exist, referenced in CLA \"%s\""
argument_list|,
name|autoVerifyGroup
operator|.
name|getName
argument_list|()
argument_list|,
name|ca
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|info
return|;
block|}
block|}
end_class

end_unit

