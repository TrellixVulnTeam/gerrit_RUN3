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
DECL|package|com.google.gerrit.acceptance.rest.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
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
name|acceptance
operator|.
name|rest
operator|.
name|account
operator|.
name|AccountAssert
operator|.
name|assertAccountInfo
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
name|acceptance
operator|.
name|AbstractDaemonTest
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
name|acceptance
operator|.
name|NoHttpd
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
name|acceptance
operator|.
name|TestAccount
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
name|restapi
operator|.
name|ResourceNotFoundException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
annotation|@
name|NoHttpd
DECL|class|GetAccountIT
specifier|public
class|class
name|GetAccountIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ResourceNotFoundException
operator|.
name|class
argument_list|)
DECL|method|getNonExistingAccount_NotFound ()
specifier|public
name|void
name|getNonExistingAccount_NotFound
parameter_list|()
throws|throws
name|Exception
block|{
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
literal|"non-existing"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getAccount ()
specifier|public
name|void
name|getAccount
parameter_list|()
throws|throws
name|Exception
block|{
comment|// by formatted string
name|testGetAccount
argument_list|(
name|admin
operator|.
name|fullName
operator|+
literal|"<"
operator|+
name|admin
operator|.
name|email
operator|+
literal|">"
argument_list|,
name|admin
argument_list|)
expr_stmt|;
comment|// by email
name|testGetAccount
argument_list|(
name|admin
operator|.
name|email
argument_list|,
name|admin
argument_list|)
expr_stmt|;
comment|// by full name
name|testGetAccount
argument_list|(
name|admin
operator|.
name|fullName
argument_list|,
name|admin
argument_list|)
expr_stmt|;
comment|// by account ID
name|testGetAccount
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|admin
operator|.
name|id
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|admin
argument_list|)
expr_stmt|;
comment|// by user name
name|testGetAccount
argument_list|(
name|admin
operator|.
name|username
argument_list|,
name|admin
argument_list|)
expr_stmt|;
comment|// by 'self'
name|testGetAccount
argument_list|(
literal|"self"
argument_list|,
name|admin
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetAccount (String id, TestAccount expectedAccount)
specifier|private
name|void
name|testGetAccount
parameter_list|(
name|String
name|id
parameter_list|,
name|TestAccount
name|expectedAccount
parameter_list|)
throws|throws
name|Exception
block|{
name|assertAccountInfo
argument_list|(
name|expectedAccount
argument_list|,
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|id
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

