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
DECL|package|com.google.gerrit.pgm.init
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
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
name|common
operator|.
name|FileUtil
operator|.
name|chmod
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitUtil
operator|.
name|die
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitUtil
operator|.
name|domainOf
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitUtil
operator|.
name|isAnyAddress
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitUtil
operator|.
name|toURI
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
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|ConsoleUI
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
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitFlags
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
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitStep
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
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|Section
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
name|SitePaths
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Singleton
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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

begin_comment
comment|/** Initialize the {@code httpd} configuration section. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|InitHttpd
class|class
name|InitHttpd
implements|implements
name|InitStep
block|{
DECL|field|ui
specifier|private
specifier|final
name|ConsoleUI
name|ui
decl_stmt|;
DECL|field|site
specifier|private
specifier|final
name|SitePaths
name|site
decl_stmt|;
DECL|field|flags
specifier|private
specifier|final
name|InitFlags
name|flags
decl_stmt|;
DECL|field|httpd
specifier|private
specifier|final
name|Section
name|httpd
decl_stmt|;
DECL|field|gerrit
specifier|private
specifier|final
name|Section
name|gerrit
decl_stmt|;
annotation|@
name|Inject
DECL|method|InitHttpd (final ConsoleUI ui, final SitePaths site, final InitFlags flags, final Section.Factory sections)
name|InitHttpd
parameter_list|(
specifier|final
name|ConsoleUI
name|ui
parameter_list|,
specifier|final
name|SitePaths
name|site
parameter_list|,
specifier|final
name|InitFlags
name|flags
parameter_list|,
specifier|final
name|Section
operator|.
name|Factory
name|sections
parameter_list|)
block|{
name|this
operator|.
name|ui
operator|=
name|ui
expr_stmt|;
name|this
operator|.
name|site
operator|=
name|site
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
name|this
operator|.
name|httpd
operator|=
name|sections
operator|.
name|get
argument_list|(
literal|"httpd"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|gerrit
operator|=
name|sections
operator|.
name|get
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|ui
operator|.
name|header
argument_list|(
literal|"HTTP Daemon"
argument_list|)
expr_stmt|;
name|boolean
name|proxy
init|=
literal|false
decl_stmt|;
name|boolean
name|ssl
init|=
literal|false
decl_stmt|;
name|String
name|address
init|=
literal|"*"
decl_stmt|;
name|int
name|port
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|context
init|=
literal|"/"
decl_stmt|;
name|String
name|listenUrl
init|=
name|httpd
operator|.
name|get
argument_list|(
literal|"listenUrl"
argument_list|)
decl_stmt|;
if|if
condition|(
name|listenUrl
operator|!=
literal|null
operator|&&
operator|!
name|listenUrl
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
specifier|final
name|URI
name|uri
init|=
name|toURI
argument_list|(
name|listenUrl
argument_list|)
decl_stmt|;
name|proxy
operator|=
name|uri
operator|.
name|getScheme
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"proxy-"
argument_list|)
expr_stmt|;
name|ssl
operator|=
name|uri
operator|.
name|getScheme
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"https"
argument_list|)
expr_stmt|;
name|address
operator|=
name|isAnyAddress
argument_list|(
operator|new
name|URI
argument_list|(
name|listenUrl
argument_list|)
argument_list|)
condition|?
literal|"*"
else|:
name|uri
operator|.
name|getHost
argument_list|()
expr_stmt|;
name|port
operator|=
name|uri
operator|.
name|getPort
argument_list|()
expr_stmt|;
name|context
operator|=
name|uri
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"warning: invalid httpd.listenUrl "
operator|+
name|listenUrl
argument_list|)
expr_stmt|;
block|}
block|}
name|proxy
operator|=
name|ui
operator|.
name|yesno
argument_list|(
name|proxy
argument_list|,
literal|"Behind reverse proxy"
argument_list|)
expr_stmt|;
if|if
condition|(
name|proxy
condition|)
block|{
name|ssl
operator|=
name|ui
operator|.
name|yesno
argument_list|(
name|ssl
argument_list|,
literal|"Proxy uses SSL (https://)"
argument_list|)
expr_stmt|;
name|context
operator|=
name|ui
operator|.
name|readString
argument_list|(
name|context
argument_list|,
literal|"Subdirectory on proxy server"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ssl
operator|=
name|ui
operator|.
name|yesno
argument_list|(
name|ssl
argument_list|,
literal|"Use SSL (https://)"
argument_list|)
expr_stmt|;
name|context
operator|=
literal|"/"
expr_stmt|;
block|}
name|address
operator|=
name|ui
operator|.
name|readString
argument_list|(
name|address
argument_list|,
literal|"Listen on address"
argument_list|)
expr_stmt|;
if|if
condition|(
name|port
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|proxy
condition|)
block|{
name|port
operator|=
literal|8081
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ssl
condition|)
block|{
name|port
operator|=
literal|8443
expr_stmt|;
block|}
else|else
block|{
name|port
operator|=
literal|8080
expr_stmt|;
block|}
block|}
name|port
operator|=
name|ui
operator|.
name|readInt
argument_list|(
name|port
argument_list|,
literal|"Listen on port"
argument_list|)
expr_stmt|;
specifier|final
name|StringBuilder
name|urlbuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|urlbuf
operator|.
name|append
argument_list|(
name|proxy
condition|?
literal|"proxy-"
else|:
literal|""
argument_list|)
expr_stmt|;
name|urlbuf
operator|.
name|append
argument_list|(
name|ssl
condition|?
literal|"https"
else|:
literal|"http"
argument_list|)
expr_stmt|;
name|urlbuf
operator|.
name|append
argument_list|(
literal|"://"
argument_list|)
expr_stmt|;
name|urlbuf
operator|.
name|append
argument_list|(
name|address
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|<=
name|port
condition|)
block|{
name|urlbuf
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
name|urlbuf
operator|.
name|append
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
name|urlbuf
operator|.
name|append
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|httpd
operator|.
name|set
argument_list|(
literal|"listenUrl"
argument_list|,
name|urlbuf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|URI
name|uri
decl_stmt|;
try|try
block|{
name|uri
operator|=
name|toURI
argument_list|(
name|httpd
operator|.
name|get
argument_list|(
literal|"listenUrl"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|uri
operator|.
name|getScheme
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"proxy-"
argument_list|)
condition|)
block|{
comment|// If its a proxy URL, assume the reverse proxy is on our system
comment|// at the protocol standard ports (so omit the ports from the URL).
comment|//
name|String
name|s
init|=
name|uri
operator|.
name|getScheme
argument_list|()
operator|.
name|substring
argument_list|(
literal|"proxy-"
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|uri
operator|=
operator|new
name|URI
argument_list|(
name|s
operator|+
literal|"://"
operator|+
name|uri
operator|.
name|getHost
argument_list|()
operator|+
name|uri
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
name|die
argument_list|(
literal|"invalid httpd.listenUrl"
argument_list|)
throw|;
block|}
name|gerrit
operator|.
name|string
argument_list|(
literal|"Canonical URL"
argument_list|,
literal|"canonicalWebUrl"
argument_list|,
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|generateSslCertificate
argument_list|()
expr_stmt|;
block|}
DECL|method|generateSslCertificate ()
specifier|private
name|void
name|generateSslCertificate
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|String
name|listenUrl
init|=
name|httpd
operator|.
name|get
argument_list|(
literal|"listenUrl"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|listenUrl
operator|.
name|startsWith
argument_list|(
literal|"https://"
argument_list|)
condition|)
block|{
comment|// We aren't responsible for SSL processing.
comment|//
return|return;
block|}
name|String
name|hostname
decl_stmt|;
try|try
block|{
name|String
name|url
init|=
name|gerrit
operator|.
name|get
argument_list|(
literal|"canonicalWebUrl"
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
operator|||
name|url
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|url
operator|=
name|listenUrl
expr_stmt|;
block|}
name|hostname
operator|=
name|toURI
argument_list|(
name|url
argument_list|)
operator|.
name|getHost
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Invalid httpd.listenUrl, not checking certificate"
argument_list|)
expr_stmt|;
return|return;
block|}
name|Path
name|store
init|=
name|site
operator|.
name|ssl_keystore
decl_stmt|;
if|if
condition|(
operator|!
name|ui
operator|.
name|yesno
argument_list|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|store
argument_list|)
argument_list|,
literal|"Create new self-signed SSL certificate"
argument_list|)
condition|)
block|{
return|return;
block|}
name|String
name|ssl_pass
init|=
name|flags
operator|.
name|sec
operator|.
name|get
argument_list|(
literal|"http"
argument_list|,
literal|null
argument_list|,
literal|"sslKeyPassword"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ssl_pass
operator|==
literal|null
operator|||
name|ssl_pass
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ssl_pass
operator|=
name|SignedToken
operator|.
name|generateRandomKey
argument_list|()
expr_stmt|;
name|flags
operator|.
name|sec
operator|.
name|set
argument_list|(
literal|"httpd"
argument_list|,
literal|null
argument_list|,
literal|"sslKeyPassword"
argument_list|,
name|ssl_pass
argument_list|)
expr_stmt|;
block|}
name|hostname
operator|=
name|ui
operator|.
name|readString
argument_list|(
name|hostname
argument_list|,
literal|"Certificate server name"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|validity
init|=
name|ui
operator|.
name|readString
argument_list|(
literal|"365"
argument_list|,
literal|"Certificate expires in (days)"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|dname
init|=
literal|"CN="
operator|+
name|hostname
operator|+
literal|",OU=Gerrit Code Review,O="
operator|+
name|domainOf
argument_list|(
name|hostname
argument_list|)
decl_stmt|;
name|Path
name|tmpdir
init|=
name|site
operator|.
name|etc_dir
operator|.
name|resolve
argument_list|(
literal|"tmp.sslcertgen"
argument_list|)
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|createDirectory
argument_list|(
name|tmpdir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|die
argument_list|(
literal|"Cannot create directory "
operator|+
name|tmpdir
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|chmod
argument_list|(
literal|0600
argument_list|,
name|tmpdir
argument_list|)
expr_stmt|;
name|Path
name|tmpstore
init|=
name|tmpdir
operator|.
name|resolve
argument_list|(
literal|"keystore"
argument_list|)
decl_stmt|;
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"keytool"
block|,
comment|//
literal|"-keystore"
block|,
name|tmpstore
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
block|,
comment|//
literal|"-storepass"
block|,
name|ssl_pass
block|,
comment|//
literal|"-genkeypair"
block|,
comment|//
literal|"-alias"
block|,
name|hostname
block|,
comment|//
literal|"-keyalg"
block|,
literal|"RSA"
block|,
comment|//
literal|"-validity"
block|,
name|validity
block|,
comment|//
literal|"-dname"
block|,
name|dname
block|,
comment|//
literal|"-keypass"
block|,
name|ssl_pass
block|,
comment|//
block|}
argument_list|)
operator|.
name|waitFor
argument_list|()
expr_stmt|;
name|chmod
argument_list|(
literal|0600
argument_list|,
name|tmpstore
argument_list|)
expr_stmt|;
try|try
block|{
name|Files
operator|.
name|move
argument_list|(
name|tmpstore
argument_list|,
name|store
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|die
argument_list|(
literal|"Cannot rename "
operator|+
name|tmpstore
operator|+
literal|" to "
operator|+
name|store
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|Files
operator|.
name|delete
argument_list|(
name|tmpdir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|die
argument_list|(
literal|"Cannot delete "
operator|+
name|tmpdir
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|postRun ()
specifier|public
name|void
name|postRun
parameter_list|()
throws|throws
name|Exception
block|{   }
block|}
end_class

end_unit

