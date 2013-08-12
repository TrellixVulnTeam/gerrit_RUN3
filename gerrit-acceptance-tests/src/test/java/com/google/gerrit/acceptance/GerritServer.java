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
DECL|package|com.google.gerrit.acceptance
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
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
name|lifecycle
operator|.
name|LifecycleManager
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
name|Daemon
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
name|Init
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
name|FactoryModule
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
name|Injector
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
name|Module
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
name|lib
operator|.
name|RepositoryCache
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|util
operator|.
name|FS
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
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
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|BrokenBarrierException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CyclicBarrier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_class
DECL|class|GerritServer
class|class
name|GerritServer
block|{
comment|/** Returns fully started Gerrit server */
DECL|method|start ()
specifier|static
name|GerritServer
name|start
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|File
name|site
init|=
name|initSite
argument_list|()
decl_stmt|;
specifier|final
name|CyclicBarrier
name|serverStarted
init|=
operator|new
name|CyclicBarrier
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|Daemon
name|daemon
init|=
operator|new
name|Daemon
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|serverStarted
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|BrokenBarrierException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|ExecutorService
name|daemonService
init|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
decl_stmt|;
name|daemonService
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|rc
init|=
name|daemon
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-d"
block|,
name|site
operator|.
name|getPath
argument_list|()
block|,
literal|"--headless"
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|!=
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Failed to start Gerrit daemon. Check "
operator|+
name|site
operator|.
name|getPath
argument_list|()
operator|+
literal|"/logs/error_log"
argument_list|)
expr_stmt|;
name|serverStarted
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
empty_stmt|;
block|}
argument_list|)
expr_stmt|;
name|serverStarted
operator|.
name|await
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Gerrit Server Started"
argument_list|)
expr_stmt|;
name|Injector
name|i
init|=
name|createTestInjector
argument_list|(
name|daemon
argument_list|)
decl_stmt|;
return|return
operator|new
name|GerritServer
argument_list|(
name|site
argument_list|,
name|i
argument_list|,
name|daemon
argument_list|,
name|daemonService
argument_list|)
return|;
block|}
DECL|method|initSite ()
specifier|private
specifier|static
name|File
name|initSite
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|tmp
init|=
name|TempFileUtil
operator|.
name|createTempDirectory
argument_list|()
decl_stmt|;
name|Init
name|init
init|=
operator|new
name|Init
argument_list|()
decl_stmt|;
name|int
name|rc
init|=
name|init
operator|.
name|main
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-d"
block|,
name|tmp
operator|.
name|getPath
argument_list|()
block|,
literal|"--batch"
block|,
literal|"--no-auto-start"
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Couldn't initialize site"
argument_list|)
throw|;
block|}
name|InetSocketAddress
name|http
init|=
name|newPort
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|sshd
init|=
name|newPort
argument_list|()
decl_stmt|;
name|String
name|url
init|=
literal|"http://"
operator|+
name|format
argument_list|(
name|http
argument_list|)
operator|+
literal|"/"
decl_stmt|;
name|FileBasedConfig
name|cfg
init|=
operator|new
name|FileBasedConfig
argument_list|(
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|tmp
argument_list|,
literal|"etc"
argument_list|)
argument_list|,
literal|"gerrit.config"
argument_list|)
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
decl_stmt|;
name|cfg
operator|.
name|load
argument_list|()
expr_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"canonicalWebUrl"
argument_list|,
name|url
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"httpd"
argument_list|,
literal|null
argument_list|,
literal|"listenUrl"
argument_list|,
name|url
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"sshd"
argument_list|,
literal|null
argument_list|,
literal|"listenAddress"
argument_list|,
name|format
argument_list|(
name|sshd
argument_list|)
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"cache"
argument_list|,
literal|null
argument_list|,
literal|"directory"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setBoolean
argument_list|(
literal|"sendemail"
argument_list|,
literal|null
argument_list|,
literal|"enable"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setInt
argument_list|(
literal|"cache"
argument_list|,
literal|"projects"
argument_list|,
literal|"checkFrequency"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setInt
argument_list|(
literal|"plugins"
argument_list|,
literal|null
argument_list|,
literal|"checkFrequency"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|save
argument_list|()
expr_stmt|;
return|return
name|tmp
return|;
block|}
DECL|method|format (InetSocketAddress s)
specifier|private
specifier|static
name|String
name|format
parameter_list|(
name|InetSocketAddress
name|s
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s:%d"
argument_list|,
name|s
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|,
name|s
operator|.
name|getPort
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createTestInjector (Daemon daemon)
specifier|private
specifier|static
name|Injector
name|createTestInjector
parameter_list|(
name|Daemon
name|daemon
parameter_list|)
throws|throws
name|Exception
block|{
name|Injector
name|sysInjector
init|=
name|get
argument_list|(
name|daemon
argument_list|,
literal|"sysInjector"
argument_list|)
decl_stmt|;
name|Module
name|module
init|=
operator|new
name|FactoryModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|AccountCreator
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
return|return
name|sysInjector
operator|.
name|createChildInjector
argument_list|(
name|module
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|get (Object obj, String field)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|get
parameter_list|(
name|Object
name|obj
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|SecurityException
throws|,
name|NoSuchFieldException
throws|,
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|Field
name|f
init|=
name|obj
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|f
operator|.
name|get
argument_list|(
name|obj
argument_list|)
return|;
block|}
DECL|method|newPort ()
specifier|private
specifier|static
specifier|final
name|InetSocketAddress
name|newPort
parameter_list|()
throws|throws
name|IOException
block|{
name|ServerSocket
name|s
init|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|getLocalHost
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|(
name|InetSocketAddress
operator|)
name|s
operator|.
name|getLocalSocketAddress
argument_list|()
return|;
block|}
finally|finally
block|{
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getLocalHost ()
specifier|private
specifier|static
name|InetAddress
name|getLocalHost
parameter_list|()
throws|throws
name|UnknownHostException
block|{
try|try
block|{
return|return
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e1
parameter_list|)
block|{
try|try
block|{
return|return
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"localhost"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e2
parameter_list|)
block|{
return|return
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"127.0.0.1"
argument_list|)
return|;
block|}
block|}
block|}
DECL|field|sitePath
specifier|private
name|File
name|sitePath
decl_stmt|;
DECL|field|daemon
specifier|private
name|Daemon
name|daemon
decl_stmt|;
DECL|field|daemonService
specifier|private
name|ExecutorService
name|daemonService
decl_stmt|;
DECL|field|testInjector
specifier|private
name|Injector
name|testInjector
decl_stmt|;
DECL|field|url
specifier|private
name|String
name|url
decl_stmt|;
DECL|field|sshdAddress
specifier|private
name|InetSocketAddress
name|sshdAddress
decl_stmt|;
DECL|field|httpAddress
specifier|private
name|InetSocketAddress
name|httpAddress
decl_stmt|;
DECL|method|GerritServer (File sitePath, Injector testInjector, Daemon daemon, ExecutorService daemonService)
specifier|private
name|GerritServer
parameter_list|(
name|File
name|sitePath
parameter_list|,
name|Injector
name|testInjector
parameter_list|,
name|Daemon
name|daemon
parameter_list|,
name|ExecutorService
name|daemonService
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|this
operator|.
name|sitePath
operator|=
name|sitePath
expr_stmt|;
name|this
operator|.
name|testInjector
operator|=
name|testInjector
expr_stmt|;
name|this
operator|.
name|daemon
operator|=
name|daemon
expr_stmt|;
name|this
operator|.
name|daemonService
operator|=
name|daemonService
expr_stmt|;
name|FileBasedConfig
name|cfg
init|=
operator|new
name|FileBasedConfig
argument_list|(
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|sitePath
argument_list|,
literal|"etc"
argument_list|)
argument_list|,
literal|"gerrit.config"
argument_list|)
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
decl_stmt|;
name|cfg
operator|.
name|load
argument_list|()
expr_stmt|;
name|url
operator|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"canonicalWebUrl"
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|sshdAddress
operator|=
name|SocketUtil
operator|.
name|resolve
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"sshd"
argument_list|,
literal|null
argument_list|,
literal|"listenAddress"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|httpAddress
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|uri
operator|.
name|getHost
argument_list|()
argument_list|,
name|uri
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getUrl ()
name|String
name|getUrl
parameter_list|()
block|{
return|return
name|url
return|;
block|}
DECL|method|getSshdAddress ()
name|InetSocketAddress
name|getSshdAddress
parameter_list|()
block|{
return|return
name|sshdAddress
return|;
block|}
DECL|method|getHttpAddress ()
name|InetSocketAddress
name|getHttpAddress
parameter_list|()
block|{
return|return
name|httpAddress
return|;
block|}
DECL|method|getTestInjector ()
name|Injector
name|getTestInjector
parameter_list|()
block|{
return|return
name|testInjector
return|;
block|}
DECL|method|stop ()
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|LifecycleManager
name|manager
init|=
name|get
argument_list|(
name|daemon
argument_list|,
literal|"manager"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Gerrit Server Shutdown"
argument_list|)
expr_stmt|;
name|manager
operator|.
name|stop
argument_list|()
expr_stmt|;
name|daemonService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|daemonService
operator|.
name|awaitTermination
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|TempFileUtil
operator|.
name|recursivelyDelete
argument_list|(
name|sitePath
argument_list|)
expr_stmt|;
name|RepositoryCache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

