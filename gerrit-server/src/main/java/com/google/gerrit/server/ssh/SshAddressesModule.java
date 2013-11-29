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
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|GerritServerConfig
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
name|AbstractModule
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
name|Provides
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
name|lib
operator|.
name|Config
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
name|SocketAddress
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
name|List
import|;
end_import

begin_class
DECL|class|SshAddressesModule
specifier|public
class|class
name|SshAddressesModule
extends|extends
name|AbstractModule
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
name|SshAddressesModule
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_PORT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_PORT
init|=
literal|29418
decl_stmt|;
DECL|field|IANA_SSH_PORT
specifier|public
specifier|static
specifier|final
name|int
name|IANA_SSH_PORT
init|=
literal|22
decl_stmt|;
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{   }
annotation|@
name|Provides
annotation|@
name|Singleton
annotation|@
name|SshListenAddresses
DECL|method|getListenAddresses (@erritServerConfig Config cfg)
specifier|public
name|List
argument_list|<
name|SocketAddress
argument_list|>
name|getListenAddresses
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|List
argument_list|<
name|SocketAddress
argument_list|>
name|listen
init|=
name|Lists
operator|.
name|newArrayListWithExpectedSize
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|String
index|[]
name|want
init|=
name|cfg
operator|.
name|getStringList
argument_list|(
literal|"sshd"
argument_list|,
literal|null
argument_list|,
literal|"listenaddress"
argument_list|)
decl_stmt|;
if|if
condition|(
name|want
operator|==
literal|null
operator|||
name|want
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|listen
operator|.
name|add
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|DEFAULT_PORT
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|listen
return|;
block|}
if|if
condition|(
name|want
operator|.
name|length
operator|==
literal|1
operator|&&
name|isOff
argument_list|(
name|want
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
return|return
name|listen
return|;
block|}
for|for
control|(
specifier|final
name|String
name|desc
range|:
name|want
control|)
block|{
try|try
block|{
name|listen
operator|.
name|add
argument_list|(
name|SocketUtil
operator|.
name|resolve
argument_list|(
name|desc
argument_list|,
name|DEFAULT_PORT
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Bad sshd.listenaddress: "
operator|+
name|desc
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|listen
return|;
block|}
DECL|method|isOff (String listenHostname)
specifier|private
specifier|static
name|boolean
name|isOff
parameter_list|(
name|String
name|listenHostname
parameter_list|)
block|{
return|return
literal|"off"
operator|.
name|equalsIgnoreCase
argument_list|(
name|listenHostname
argument_list|)
operator|||
literal|"none"
operator|.
name|equalsIgnoreCase
argument_list|(
name|listenHostname
argument_list|)
operator|||
literal|"no"
operator|.
name|equalsIgnoreCase
argument_list|(
name|listenHostname
argument_list|)
return|;
block|}
annotation|@
name|Provides
annotation|@
name|Singleton
annotation|@
name|SshAdvertisedAddresses
DECL|method|getAdvertisedAddresses (@erritServerConfig Config cfg, @SshListenAddresses List<SocketAddress> listen)
name|List
argument_list|<
name|String
argument_list|>
name|getAdvertisedAddresses
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
annotation|@
name|SshListenAddresses
name|List
argument_list|<
name|SocketAddress
argument_list|>
name|listen
parameter_list|)
block|{
name|String
index|[]
name|want
init|=
name|cfg
operator|.
name|getStringList
argument_list|(
literal|"sshd"
argument_list|,
literal|null
argument_list|,
literal|"advertisedaddress"
argument_list|)
decl_stmt|;
if|if
condition|(
name|want
operator|.
name|length
operator|>
literal|0
condition|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|want
argument_list|)
return|;
block|}
name|List
argument_list|<
name|InetSocketAddress
argument_list|>
name|pub
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|InetSocketAddress
argument_list|>
name|local
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|SocketAddress
name|addr
range|:
name|listen
control|)
block|{
if|if
condition|(
name|addr
operator|instanceof
name|InetSocketAddress
condition|)
block|{
name|InetSocketAddress
name|inetAddr
init|=
operator|(
name|InetSocketAddress
operator|)
name|addr
decl_stmt|;
if|if
condition|(
name|inetAddr
operator|.
name|getAddress
argument_list|()
operator|.
name|isLoopbackAddress
argument_list|()
condition|)
block|{
name|local
operator|.
name|add
argument_list|(
name|inetAddr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pub
operator|.
name|add
argument_list|(
name|inetAddr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|pub
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|pub
operator|=
name|local
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|adv
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|pub
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|InetSocketAddress
name|addr
range|:
name|pub
control|)
block|{
name|adv
operator|.
name|add
argument_list|(
name|SocketUtil
operator|.
name|format
argument_list|(
name|addr
argument_list|,
name|IANA_SSH_PORT
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|adv
return|;
block|}
block|}
end_class

end_unit

