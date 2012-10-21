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
name|pgm
operator|.
name|init
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
name|InitUtil
operator|.
name|savePublic
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
name|InitUtil
operator|.
name|saveSecure
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
name|util
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
name|gerrit
operator|.
name|server
operator|.
name|util
operator|.
name|SocketUtil
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|errors
operator|.
name|ConfigInvalidException
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
name|storage
operator|.
name|file
operator|.
name|FileBasedConfig
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
name|io
operator|.
name|FileInputStream
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLDecoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/** Upgrade from a 2.0.x site to a 2.1 site. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|UpgradeFrom2_0_x
class|class
name|UpgradeFrom2_0_x
implements|implements
name|InitStep
block|{
DECL|field|etcFiles
specifier|static
specifier|final
name|String
index|[]
name|etcFiles
init|=
block|{
literal|"gerrit.config"
block|,
comment|//
literal|"secure.config"
block|,
comment|//
literal|"replication.config"
block|,
comment|//
literal|"ssh_host_rsa_key"
block|,
comment|//
literal|"ssh_host_rsa_key.pub"
block|,
comment|//
literal|"ssh_host_dsa_key"
block|,
comment|//
literal|"ssh_host_dsa_key.pub"
block|,
comment|//
literal|"ssh_host_key"
block|,
comment|//
literal|"contact_information.pub"
block|,
comment|//
literal|"gitweb_config.perl"
block|,
comment|//
literal|"keystore"
block|,
comment|//
literal|"GerritSite.css"
block|,
comment|//
literal|"GerritSiteFooter.html"
block|,
comment|//
literal|"GerritSiteHeader.html"
block|,
comment|//
block|}
decl_stmt|;
DECL|field|ui
specifier|private
specifier|final
name|ConsoleUI
name|ui
decl_stmt|;
DECL|field|cfg
specifier|private
specifier|final
name|FileBasedConfig
name|cfg
decl_stmt|;
DECL|field|sec
specifier|private
specifier|final
name|FileBasedConfig
name|sec
decl_stmt|;
DECL|field|site_path
specifier|private
specifier|final
name|File
name|site_path
decl_stmt|;
DECL|field|etc_dir
specifier|private
specifier|final
name|File
name|etc_dir
decl_stmt|;
DECL|field|sections
specifier|private
specifier|final
name|Section
operator|.
name|Factory
name|sections
decl_stmt|;
annotation|@
name|Inject
DECL|method|UpgradeFrom2_0_x (final SitePaths site, final InitFlags flags, final ConsoleUI ui, final Section.Factory sections)
name|UpgradeFrom2_0_x
parameter_list|(
specifier|final
name|SitePaths
name|site
parameter_list|,
specifier|final
name|InitFlags
name|flags
parameter_list|,
specifier|final
name|ConsoleUI
name|ui
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
name|sections
operator|=
name|sections
expr_stmt|;
name|this
operator|.
name|cfg
operator|=
name|flags
operator|.
name|cfg
expr_stmt|;
name|this
operator|.
name|sec
operator|=
name|flags
operator|.
name|sec
expr_stmt|;
name|this
operator|.
name|site_path
operator|=
name|site
operator|.
name|site_path
expr_stmt|;
name|this
operator|.
name|etc_dir
operator|=
name|site
operator|.
name|etc_dir
expr_stmt|;
block|}
DECL|method|isNeedUpgrade ()
name|boolean
name|isNeedUpgrade
parameter_list|()
block|{
for|for
control|(
name|String
name|name
range|:
name|etcFiles
control|)
block|{
if|if
condition|(
operator|new
name|File
argument_list|(
name|site_path
argument_list|,
name|name
argument_list|)
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
if|if
condition|(
operator|!
name|isNeedUpgrade
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|ui
operator|.
name|yesno
argument_list|(
literal|true
argument_list|,
literal|"Upgrade '%s'"
argument_list|,
name|site_path
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"aborted by user"
argument_list|)
throw|;
block|}
for|for
control|(
name|String
name|name
range|:
name|etcFiles
control|)
block|{
specifier|final
name|File
name|src
init|=
operator|new
name|File
argument_list|(
name|site_path
argument_list|,
name|name
argument_list|)
decl_stmt|;
specifier|final
name|File
name|dst
init|=
operator|new
name|File
argument_list|(
name|etc_dir
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|src
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|dst
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"File "
operator|+
name|src
operator|+
literal|" would overwrite "
operator|+
name|dst
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|src
operator|.
name|renameTo
argument_list|(
name|dst
argument_list|)
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"Cannot rename "
operator|+
name|src
operator|+
literal|" to "
operator|+
name|dst
argument_list|)
throw|;
block|}
block|}
block|}
comment|// We have to reload the configuration after the rename as
comment|// the initial load pulled up an non-existent (and thus
comment|// believed to be empty) file.
comment|//
name|cfg
operator|.
name|load
argument_list|()
expr_stmt|;
name|sec
operator|.
name|load
argument_list|()
expr_stmt|;
specifier|final
name|Properties
name|oldprop
init|=
name|readGerritServerProperties
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldprop
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Section
name|database
init|=
name|sections
operator|.
name|get
argument_list|(
literal|"database"
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|oldprop
operator|.
name|getProperty
argument_list|(
literal|"url"
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|!=
literal|null
operator|&&
operator|!
name|convertUrl
argument_list|(
name|database
argument_list|,
name|url
argument_list|)
condition|)
block|{
name|database
operator|.
name|set
argument_list|(
literal|"type"
argument_list|,
literal|"jdbc"
argument_list|)
expr_stmt|;
name|database
operator|.
name|set
argument_list|(
literal|"driver"
argument_list|,
name|oldprop
operator|.
name|getProperty
argument_list|(
literal|"driver"
argument_list|)
argument_list|)
expr_stmt|;
name|database
operator|.
name|set
argument_list|(
literal|"url"
argument_list|,
name|url
argument_list|)
expr_stmt|;
block|}
name|String
name|username
init|=
name|oldprop
operator|.
name|getProperty
argument_list|(
literal|"user"
argument_list|)
decl_stmt|;
if|if
condition|(
name|username
operator|==
literal|null
operator|||
name|username
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|username
operator|=
name|oldprop
operator|.
name|getProperty
argument_list|(
literal|"username"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|username
operator|!=
literal|null
operator|&&
operator|!
name|username
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|cfg
operator|.
name|setString
argument_list|(
literal|"database"
argument_list|,
literal|null
argument_list|,
literal|"username"
argument_list|,
name|username
argument_list|)
expr_stmt|;
block|}
name|String
name|password
init|=
name|oldprop
operator|.
name|getProperty
argument_list|(
literal|"password"
argument_list|)
decl_stmt|;
if|if
condition|(
name|password
operator|!=
literal|null
operator|&&
operator|!
name|password
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|sec
operator|.
name|setString
argument_list|(
literal|"database"
argument_list|,
literal|null
argument_list|,
literal|"password"
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
block|}
name|String
index|[]
name|values
decl_stmt|;
name|values
operator|=
name|cfg
operator|.
name|getStringList
argument_list|(
literal|"ldap"
argument_list|,
literal|null
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|unset
argument_list|(
literal|"ldap"
argument_list|,
literal|null
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
name|sec
operator|.
name|setStringList
argument_list|(
literal|"ldap"
argument_list|,
literal|null
argument_list|,
literal|"password"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|=
name|cfg
operator|.
name|getStringList
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"smtpPass"
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|unset
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"smtpPass"
argument_list|)
expr_stmt|;
name|sec
operator|.
name|setStringList
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"smtpPass"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|saveSecure
argument_list|(
name|sec
argument_list|)
expr_stmt|;
name|savePublic
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
block|}
DECL|method|convertUrl (final Section database, String url)
specifier|private
name|boolean
name|convertUrl
parameter_list|(
specifier|final
name|Section
name|database
parameter_list|,
name|String
name|url
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
name|String
name|username
init|=
literal|null
decl_stmt|;
name|String
name|password
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|url
operator|.
name|contains
argument_list|(
literal|"?"
argument_list|)
condition|)
block|{
specifier|final
name|int
name|q
init|=
name|url
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|pair
range|:
name|url
operator|.
name|substring
argument_list|(
name|q
operator|+
literal|1
argument_list|)
operator|.
name|split
argument_list|(
literal|"&"
argument_list|)
control|)
block|{
specifier|final
name|int
name|eq
init|=
name|pair
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|eq
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|n
init|=
name|URLDecoder
operator|.
name|decode
argument_list|(
name|pair
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|eq
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|String
name|v
init|=
name|URLDecoder
operator|.
name|decode
argument_list|(
name|pair
operator|.
name|substring
argument_list|(
name|eq
operator|+
literal|1
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"user"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
operator|||
literal|"username"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|username
operator|=
name|v
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"password"
operator|.
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|password
operator|=
name|v
expr_stmt|;
block|}
else|else
block|{
comment|// There is a parameter setting we don't recognize, use the
comment|// JDBC URL format instead to preserve the configuration.
comment|//
return|return
literal|false
return|;
block|}
block|}
name|url
operator|=
name|url
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|q
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"jdbc:h2:file:"
argument_list|)
condition|)
block|{
name|url
operator|=
name|url
operator|.
name|substring
argument_list|(
literal|"jdbc:h2:file:"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|database
operator|.
name|set
argument_list|(
literal|"type"
argument_list|,
literal|"h2"
argument_list|)
expr_stmt|;
name|database
operator|.
name|set
argument_list|(
literal|"database"
argument_list|,
name|url
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"jdbc:postgresql://"
argument_list|)
condition|)
block|{
name|url
operator|=
name|url
operator|.
name|substring
argument_list|(
literal|"jdbc:postgresql://"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|sl
init|=
name|url
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|sl
operator|<
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|InetSocketAddress
name|addr
init|=
name|SocketUtil
operator|.
name|parse
argument_list|(
name|url
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sl
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|database
operator|.
name|set
argument_list|(
literal|"type"
argument_list|,
literal|"postgresql"
argument_list|)
expr_stmt|;
name|sethost
argument_list|(
name|database
argument_list|,
name|addr
argument_list|)
expr_stmt|;
name|database
operator|.
name|set
argument_list|(
literal|"database"
argument_list|,
name|url
operator|.
name|substring
argument_list|(
name|sl
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|setuser
argument_list|(
name|database
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"jdbc:postgresql:"
argument_list|)
condition|)
block|{
name|url
operator|=
name|url
operator|.
name|substring
argument_list|(
literal|"jdbc:postgresql:"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|database
operator|.
name|set
argument_list|(
literal|"type"
argument_list|,
literal|"postgresql"
argument_list|)
expr_stmt|;
name|database
operator|.
name|set
argument_list|(
literal|"hostname"
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
name|database
operator|.
name|set
argument_list|(
literal|"database"
argument_list|,
name|url
argument_list|)
expr_stmt|;
name|setuser
argument_list|(
name|database
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"jdbc:mysql://"
argument_list|)
condition|)
block|{
name|url
operator|=
name|url
operator|.
name|substring
argument_list|(
literal|"jdbc:mysql://"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|sl
init|=
name|url
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|sl
operator|<
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|InetSocketAddress
name|addr
init|=
name|SocketUtil
operator|.
name|parse
argument_list|(
name|url
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sl
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|database
operator|.
name|set
argument_list|(
literal|"type"
argument_list|,
literal|"mysql"
argument_list|)
expr_stmt|;
name|sethost
argument_list|(
name|database
argument_list|,
name|addr
argument_list|)
expr_stmt|;
name|database
operator|.
name|set
argument_list|(
literal|"database"
argument_list|,
name|url
operator|.
name|substring
argument_list|(
name|sl
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|setuser
argument_list|(
name|database
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|sethost (final Section database, final InetSocketAddress addr)
specifier|private
name|void
name|sethost
parameter_list|(
specifier|final
name|Section
name|database
parameter_list|,
specifier|final
name|InetSocketAddress
name|addr
parameter_list|)
block|{
name|database
operator|.
name|set
argument_list|(
literal|"hostname"
argument_list|,
name|SocketUtil
operator|.
name|hostname
argument_list|(
name|addr
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
literal|0
operator|<
name|addr
operator|.
name|getPort
argument_list|()
condition|)
block|{
name|database
operator|.
name|set
argument_list|(
literal|"port"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setuser (final Section database, String username, String password)
specifier|private
name|void
name|setuser
parameter_list|(
specifier|final
name|Section
name|database
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
block|{
if|if
condition|(
name|username
operator|!=
literal|null
operator|&&
operator|!
name|username
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|database
operator|.
name|set
argument_list|(
literal|"username"
argument_list|,
name|username
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|password
operator|!=
literal|null
operator|&&
operator|!
name|password
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|sec
operator|.
name|setString
argument_list|(
literal|"database"
argument_list|,
literal|null
argument_list|,
literal|"password"
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readGerritServerProperties ()
specifier|private
name|Properties
name|readGerritServerProperties
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Properties
name|srvprop
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"GerritServer"
argument_list|)
decl_stmt|;
name|File
name|path
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|path
operator|=
operator|new
name|File
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
operator|new
name|File
argument_list|(
name|site_path
argument_list|,
literal|"GerritServer.properties"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|path
operator|.
name|exists
argument_list|()
condition|)
block|{
name|path
operator|=
operator|new
name|File
argument_list|(
literal|"GerritServer.properties"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|path
operator|.
name|exists
argument_list|()
condition|)
block|{
try|try
block|{
specifier|final
name|InputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|path
argument_list|)
decl_stmt|;
try|try
block|{
name|srvprop
operator|.
name|load
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot read "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|Properties
name|dbprop
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|srvprop
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|String
name|key
init|=
operator|(
name|String
operator|)
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"database."
argument_list|)
condition|)
block|{
name|dbprop
operator|.
name|put
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|"database."
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|dbprop
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

