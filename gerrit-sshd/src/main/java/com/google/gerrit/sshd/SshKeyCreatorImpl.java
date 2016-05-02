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
DECL|package|com.google.gerrit.sshd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
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
name|errors
operator|.
name|InvalidSshKeyException
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
name|server
operator|.
name|ssh
operator|.
name|SshKeyCreator
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
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchProviderException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|spec
operator|.
name|InvalidKeySpecException
import|;
end_import

begin_class
DECL|class|SshKeyCreatorImpl
specifier|public
class|class
name|SshKeyCreatorImpl
implements|implements
name|SshKeyCreator
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SshKeyCreatorImpl
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|create (AccountSshKey.Id id, String encoded)
specifier|public
name|AccountSshKey
name|create
parameter_list|(
name|AccountSshKey
operator|.
name|Id
name|id
parameter_list|,
name|String
name|encoded
parameter_list|)
throws|throws
name|InvalidSshKeyException
block|{
try|try
block|{
name|AccountSshKey
name|key
init|=
operator|new
name|AccountSshKey
argument_list|(
name|id
argument_list|,
name|SshUtil
operator|.
name|toOpenSshPublicKey
argument_list|(
name|encoded
argument_list|)
argument_list|)
decl_stmt|;
name|SshUtil
operator|.
name|parse
argument_list|(
name|key
argument_list|)
expr_stmt|;
return|return
name|key
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
decl||
name|InvalidKeySpecException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InvalidSshKeyException
argument_list|()
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchProviderException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot parse SSH key"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|InvalidSshKeyException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

