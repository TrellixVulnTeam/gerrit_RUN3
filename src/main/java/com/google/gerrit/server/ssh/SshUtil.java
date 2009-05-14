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
DECL|package|com.google.gerrit.server.ssh
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|ssh
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
name|AccountSshKey
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
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
name|session
operator|.
name|AttributeKey
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
name|Buffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Constants
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
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
name|PublicKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|interfaces
operator|.
name|DSAPublicKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|interfaces
operator|.
name|RSAPublicKey
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/** Utilities to support SSH operations. */
end_comment

begin_class
DECL|class|SshUtil
specifier|public
class|class
name|SshUtil
block|{
comment|/** Server session attribute holding the {@link Account.Id}. */
DECL|field|CURRENT_ACCOUNT
specifier|static
specifier|final
name|AttributeKey
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|CURRENT_ACCOUNT
init|=
operator|new
name|AttributeKey
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Server session attribute holding the remote {@link SocketAddress}. */
DECL|field|REMOTE_PEER
specifier|static
specifier|final
name|AttributeKey
argument_list|<
name|SocketAddress
argument_list|>
name|REMOTE_PEER
init|=
operator|new
name|AttributeKey
argument_list|<
name|SocketAddress
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Server session attribute holding the current commands. */
DECL|field|ACTIVE
specifier|static
specifier|final
name|AttributeKey
argument_list|<
name|List
argument_list|<
name|AbstractCommand
argument_list|>
argument_list|>
name|ACTIVE
init|=
operator|new
name|AttributeKey
argument_list|<
name|List
argument_list|<
name|AbstractCommand
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Parse a public key into its Java type.    *     * @param key the account key to parse.    * @return the valid public key object.    * @throws InvalidKeySpecException the key supplied is not a valid SSH key.    * @throws NoSuchAlgorithmException the JVM is missing the key algorithm.    * @throws NoSuchProviderException the JVM is missing the provider.    */
DECL|method|parse (final AccountSshKey key)
specifier|public
specifier|static
name|PublicKey
name|parse
parameter_list|(
specifier|final
name|AccountSshKey
name|key
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|InvalidKeySpecException
throws|,
name|NoSuchProviderException
block|{
try|try
block|{
specifier|final
name|String
name|s
init|=
name|key
operator|.
name|getEncodedKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidKeySpecException
argument_list|(
literal|"No key string"
argument_list|)
throw|;
block|}
specifier|final
name|byte
index|[]
name|bin
init|=
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|Constants
operator|.
name|encodeASCII
argument_list|(
name|s
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Buffer
argument_list|(
name|bin
argument_list|)
operator|.
name|getPublicKey
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|re
parameter_list|)
block|{
throw|throw
operator|new
name|InvalidKeySpecException
argument_list|(
literal|"Cannot parse key"
argument_list|,
name|re
argument_list|)
throw|;
block|}
block|}
comment|/**    * Convert an RFC 4716 style key to an OpenSSH style key.    *     * @param keyStr the key string to convert.    * @return<code>keyStr</code> if conversion failed; otherwise the converted    *         key, in OpenSSH key format.    */
DECL|method|toOpenSshPublicKey (final String keyStr)
specifier|public
specifier|static
name|String
name|toOpenSshPublicKey
parameter_list|(
specifier|final
name|String
name|keyStr
parameter_list|)
block|{
try|try
block|{
specifier|final
name|StringBuilder
name|strBuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|keyStr
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|br
operator|.
name|readLine
argument_list|()
decl_stmt|;
comment|// BEGIN SSH2 line...
if|if
condition|(
operator|!
name|line
operator|.
name|equals
argument_list|(
literal|"---- BEGIN SSH2 PUBLIC KEY ----"
argument_list|)
condition|)
block|{
return|return
name|keyStr
return|;
block|}
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|line
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
name|strBuf
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"---- "
argument_list|)
condition|)
block|{
break|break;
block|}
name|strBuf
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
specifier|final
name|PublicKey
name|key
init|=
operator|new
name|Buffer
argument_list|(
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|Constants
operator|.
name|encodeASCII
argument_list|(
name|strBuf
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getPublicKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|instanceof
name|RSAPublicKey
condition|)
block|{
name|strBuf
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
name|KeyPairProvider
operator|.
name|SSH_RSA
operator|+
literal|" "
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|instanceof
name|DSAPublicKey
condition|)
block|{
name|strBuf
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
name|KeyPairProvider
operator|.
name|SSH_DSS
operator|+
literal|" "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
name|keyStr
return|;
block|}
name|strBuf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|strBuf
operator|.
name|append
argument_list|(
literal|"converted-key"
argument_list|)
expr_stmt|;
return|return
name|strBuf
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
name|keyStr
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|re
parameter_list|)
block|{
return|return
name|keyStr
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
return|return
name|keyStr
return|;
block|}
catch|catch
parameter_list|(
name|InvalidKeySpecException
name|e
parameter_list|)
block|{
return|return
name|keyStr
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchProviderException
name|e
parameter_list|)
block|{
return|return
name|keyStr
return|;
block|}
block|}
block|}
end_class

end_unit

