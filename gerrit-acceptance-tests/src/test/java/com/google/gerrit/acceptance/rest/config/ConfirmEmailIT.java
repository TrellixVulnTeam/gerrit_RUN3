begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.rest.config
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
name|config
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
name|server
operator|.
name|config
operator|.
name|ConfirmEmail
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
name|EmailTokenVerifier
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
name|testutil
operator|.
name|ConfigSuite
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
name|server
operator|.
name|SignedToken
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Config
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
DECL|class|ConfirmEmailIT
specifier|public
class|class
name|ConfirmEmailIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|ConfigSuite
operator|.
name|Default
DECL|method|defaultConfig ()
specifier|public
specifier|static
name|Config
name|defaultConfig
parameter_list|()
block|{
name|Config
name|cfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"auth"
argument_list|,
literal|null
argument_list|,
literal|"registerEmailPrivateKey"
argument_list|,
name|SignedToken
operator|.
name|generateRandomKey
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|cfg
return|;
block|}
annotation|@
name|Inject
DECL|field|emailTokenVerifier
specifier|private
name|EmailTokenVerifier
name|emailTokenVerifier
decl_stmt|;
annotation|@
name|Test
DECL|method|confirm ()
specifier|public
name|void
name|confirm
parameter_list|()
throws|throws
name|Exception
block|{
name|ConfirmEmail
operator|.
name|Input
name|in
init|=
operator|new
name|ConfirmEmail
operator|.
name|Input
argument_list|()
decl_stmt|;
name|in
operator|.
name|token
operator|=
name|emailTokenVerifier
operator|.
name|encode
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
argument_list|,
literal|"new.mail@example.com"
argument_list|)
expr_stmt|;
name|adminRestSession
operator|.
name|put
argument_list|(
literal|"/config/server/email.confirm"
argument_list|,
name|in
argument_list|)
operator|.
name|assertNoContent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|confirmForOtherUser_UnprocessableEntity ()
specifier|public
name|void
name|confirmForOtherUser_UnprocessableEntity
parameter_list|()
throws|throws
name|Exception
block|{
name|ConfirmEmail
operator|.
name|Input
name|in
init|=
operator|new
name|ConfirmEmail
operator|.
name|Input
argument_list|()
decl_stmt|;
name|in
operator|.
name|token
operator|=
name|emailTokenVerifier
operator|.
name|encode
argument_list|(
name|user
operator|.
name|getId
argument_list|()
argument_list|,
literal|"new.mail@example.com"
argument_list|)
expr_stmt|;
name|adminRestSession
operator|.
name|put
argument_list|(
literal|"/config/server/email.confirm"
argument_list|,
name|in
argument_list|)
operator|.
name|assertUnprocessableEntity
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|confirmInvalidToken_UnprocessableEntity ()
specifier|public
name|void
name|confirmInvalidToken_UnprocessableEntity
parameter_list|()
throws|throws
name|Exception
block|{
name|ConfirmEmail
operator|.
name|Input
name|in
init|=
operator|new
name|ConfirmEmail
operator|.
name|Input
argument_list|()
decl_stmt|;
name|in
operator|.
name|token
operator|=
literal|"invalidToken"
expr_stmt|;
name|adminRestSession
operator|.
name|put
argument_list|(
literal|"/config/server/email.confirm"
argument_list|,
name|in
argument_list|)
operator|.
name|assertUnprocessableEntity
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

