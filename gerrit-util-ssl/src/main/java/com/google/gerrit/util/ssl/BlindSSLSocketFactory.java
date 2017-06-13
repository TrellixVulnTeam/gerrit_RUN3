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
DECL|package|com.google.gerrit.util.ssl
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|util
operator|.
name|ssl
package|;
end_package

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
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|GeneralSecurityException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLSocketFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|TrustManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|X509TrustManager
import|;
end_import

begin_comment
comment|/** SSL socket factory that ignores SSL certificate validation. */
end_comment

begin_class
DECL|class|BlindSSLSocketFactory
specifier|public
class|class
name|BlindSSLSocketFactory
extends|extends
name|SSLSocketFactory
block|{
DECL|field|INSTANCE
specifier|private
specifier|static
specifier|final
name|BlindSSLSocketFactory
name|INSTANCE
decl_stmt|;
static|static
block|{
specifier|final
name|X509TrustManager
name|dummyTrustManager
init|=
operator|new
name|X509TrustManager
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|X509Certificate
index|[]
name|getAcceptedIssuers
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkClientTrusted
parameter_list|(
name|X509Certificate
index|[]
name|chain
parameter_list|,
name|String
name|authType
parameter_list|)
block|{}
annotation|@
name|Override
specifier|public
name|void
name|checkServerTrusted
parameter_list|(
name|X509Certificate
index|[]
name|chain
parameter_list|,
name|String
name|authType
parameter_list|)
block|{}
block|}
decl_stmt|;
try|try
block|{
specifier|final
name|SSLContext
name|context
init|=
name|SSLContext
operator|.
name|getInstance
argument_list|(
literal|"SSL"
argument_list|)
decl_stmt|;
specifier|final
name|TrustManager
index|[]
name|trustManagers
init|=
block|{
name|dummyTrustManager
block|}
decl_stmt|;
specifier|final
name|SecureRandom
name|rng
init|=
operator|new
name|SecureRandom
argument_list|()
decl_stmt|;
name|context
operator|.
name|init
argument_list|(
literal|null
argument_list|,
name|trustManagers
argument_list|,
name|rng
argument_list|)
expr_stmt|;
name|INSTANCE
operator|=
operator|new
name|BlindSSLSocketFactory
argument_list|(
name|context
operator|.
name|getSocketFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GeneralSecurityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot create BlindSslSocketFactory"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getDefault ()
specifier|public
specifier|static
name|SocketFactory
name|getDefault
parameter_list|()
block|{
return|return
name|INSTANCE
return|;
block|}
DECL|field|sslFactory
specifier|private
specifier|final
name|SSLSocketFactory
name|sslFactory
decl_stmt|;
DECL|method|BlindSSLSocketFactory (SSLSocketFactory sslFactory)
specifier|private
name|BlindSSLSocketFactory
parameter_list|(
name|SSLSocketFactory
name|sslFactory
parameter_list|)
block|{
name|this
operator|.
name|sslFactory
operator|=
name|sslFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createSocket (Socket s, String host, int port, boolean autoClose)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|Socket
name|s
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|boolean
name|autoClose
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|sslFactory
operator|.
name|createSocket
argument_list|(
name|s
argument_list|,
name|host
argument_list|,
name|port
argument_list|,
name|autoClose
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultCipherSuites ()
specifier|public
name|String
index|[]
name|getDefaultCipherSuites
parameter_list|()
block|{
return|return
name|sslFactory
operator|.
name|getDefaultCipherSuites
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSupportedCipherSuites ()
specifier|public
name|String
index|[]
name|getSupportedCipherSuites
parameter_list|()
block|{
return|return
name|sslFactory
operator|.
name|getSupportedCipherSuites
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket ()
specifier|public
name|Socket
name|createSocket
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|sslFactory
operator|.
name|createSocket
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (String host, int port)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnknownHostException
block|{
return|return
name|sslFactory
operator|.
name|createSocket
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (InetAddress host, int port)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|InetAddress
name|host
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|sslFactory
operator|.
name|createSocket
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (String host, int port, InetAddress localHost, int localPort)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|InetAddress
name|localHost
parameter_list|,
name|int
name|localPort
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnknownHostException
block|{
return|return
name|sslFactory
operator|.
name|createSocket
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|localHost
argument_list|,
name|localPort
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createSocket (InetAddress address, int port, InetAddress localAddress, int localPort)
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|InetAddress
name|address
parameter_list|,
name|int
name|port
parameter_list|,
name|InetAddress
name|localAddress
parameter_list|,
name|int
name|localPort
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|sslFactory
operator|.
name|createSocket
argument_list|(
name|address
argument_list|,
name|port
argument_list|,
name|localAddress
argument_list|,
name|localPort
argument_list|)
return|;
block|}
block|}
end_class

end_unit

