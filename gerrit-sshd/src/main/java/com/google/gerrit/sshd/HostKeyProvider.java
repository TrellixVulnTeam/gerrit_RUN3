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
name|server
operator|.
name|config
operator|.
name|SitePaths
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|ProvisionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|apache
operator|.
name|sshd
operator|.
name|common
operator|.
name|keyprovider
operator|.
name|FileKeyPairProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|common
operator|.
name|keyprovider
operator|.
name|KeyPairProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|common
operator|.
name|util
operator|.
name|security
operator|.
name|SecurityUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|keyprovider
operator|.
name|SimpleGeneratorHostKeyProvider
import|;
end_import

begin_class
DECL|class|HostKeyProvider
class|class
name|HostKeyProvider
implements|implements
name|Provider
argument_list|<
name|KeyPairProvider
argument_list|>
block|{
DECL|field|site
specifier|private
specifier|final
name|SitePaths
name|site
decl_stmt|;
annotation|@
name|Inject
DECL|method|HostKeyProvider (final SitePaths site)
name|HostKeyProvider
parameter_list|(
specifier|final
name|SitePaths
name|site
parameter_list|)
block|{
name|this
operator|.
name|site
operator|=
name|site
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|KeyPairProvider
name|get
parameter_list|()
block|{
name|Path
name|objKey
init|=
name|site
operator|.
name|ssh_key
decl_stmt|;
name|Path
name|rsaKey
init|=
name|site
operator|.
name|ssh_rsa
decl_stmt|;
name|Path
name|dsaKey
init|=
name|site
operator|.
name|ssh_dsa
decl_stmt|;
specifier|final
name|List
argument_list|<
name|File
argument_list|>
name|stdKeys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|rsaKey
argument_list|)
condition|)
block|{
name|stdKeys
operator|.
name|add
argument_list|(
name|rsaKey
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|dsaKey
argument_list|)
condition|)
block|{
name|stdKeys
operator|.
name|add
argument_list|(
name|dsaKey
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|objKey
argument_list|)
condition|)
block|{
if|if
condition|(
name|stdKeys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|SimpleGeneratorHostKeyProvider
name|p
init|=
operator|new
name|SimpleGeneratorHostKeyProvider
argument_list|()
decl_stmt|;
name|p
operator|.
name|setPath
argument_list|(
name|objKey
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
comment|// Both formats of host key exist, we don't know which format
comment|// should be authoritative. Complain and abort.
comment|//
name|stdKeys
operator|.
name|add
argument_list|(
name|objKey
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ProvisionException
argument_list|(
literal|"Multiple host keys exist: "
operator|+
name|stdKeys
argument_list|)
throw|;
block|}
if|if
condition|(
name|stdKeys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
literal|"No SSH keys under "
operator|+
name|site
operator|.
name|etc_dir
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|SecurityUtils
operator|.
name|isBouncyCastleRegistered
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
literal|"Bouncy Castle Crypto not installed;"
operator|+
literal|" needed to read server host keys: "
operator|+
name|stdKeys
operator|+
literal|""
argument_list|)
throw|;
block|}
name|FileKeyPairProvider
name|kp
init|=
operator|new
name|FileKeyPairProvider
argument_list|()
decl_stmt|;
name|kp
operator|.
name|setFiles
argument_list|(
name|stdKeys
argument_list|)
expr_stmt|;
return|return
name|kp
return|;
block|}
block|}
end_class

end_unit

