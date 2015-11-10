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
DECL|package|com.google.gerrit.httpd.raw
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|raw
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
name|cache
operator|.
name|Cache
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
name|httpd
operator|.
name|raw
operator|.
name|ResourceServlet
operator|.
name|Resource
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
name|launcher
operator|.
name|GerritLauncher
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
name|cache
operator|.
name|CacheModule
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
name|Key
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
name|ProvisionException
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|name
operator|.
name|Named
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
name|name
operator|.
name|Names
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
name|servlet
operator|.
name|ServletModule
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
name|FileNotFoundException
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
name|nio
operator|.
name|file
operator|.
name|FileSystem
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServlet
import|;
end_import

begin_class
DECL|class|StaticModule
specifier|public
class|class
name|StaticModule
extends|extends
name|ServletModule
block|{
DECL|field|GWT_UI_SERVLET
specifier|private
specifier|static
specifier|final
name|String
name|GWT_UI_SERVLET
init|=
literal|"GwtUiServlet"
decl_stmt|;
DECL|field|CACHE
specifier|static
specifier|final
name|String
name|CACHE
init|=
literal|"static_content"
decl_stmt|;
DECL|field|warFs
specifier|private
specifier|final
name|FileSystem
name|warFs
decl_stmt|;
DECL|field|buckOut
specifier|private
specifier|final
name|Path
name|buckOut
decl_stmt|;
DECL|field|unpackedWar
specifier|private
specifier|final
name|Path
name|unpackedWar
decl_stmt|;
DECL|method|StaticModule ()
specifier|public
name|StaticModule
parameter_list|()
block|{
name|warFs
operator|=
name|getDistributionArchive
argument_list|()
expr_stmt|;
if|if
condition|(
name|warFs
operator|==
literal|null
condition|)
block|{
name|buckOut
operator|=
name|getDeveloperBuckOut
argument_list|()
expr_stmt|;
name|unpackedWar
operator|=
name|makeWarTempDir
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|buckOut
operator|=
literal|null
expr_stmt|;
name|unpackedWar
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|configureServlets ()
specifier|protected
name|void
name|configureServlets
parameter_list|()
block|{
name|serve
argument_list|(
literal|"/static/*"
argument_list|)
operator|.
name|with
argument_list|(
name|SiteStaticDirectoryServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|serveGwtUi
argument_list|()
expr_stmt|;
name|install
argument_list|(
operator|new
name|CacheModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|cache
argument_list|(
name|CACHE
argument_list|,
name|Path
operator|.
name|class
argument_list|,
name|Resource
operator|.
name|class
argument_list|)
operator|.
name|maximumWeight
argument_list|(
literal|1
operator|<<
literal|20
argument_list|)
operator|.
name|weigher
argument_list|(
name|ResourceServlet
operator|.
name|Weigher
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|serveGwtUi ()
specifier|private
name|void
name|serveGwtUi
parameter_list|()
block|{
name|serveRegex
argument_list|(
literal|"^/gerrit_ui/(?!rpc/)(.*)$"
argument_list|)
operator|.
name|with
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|HttpServlet
operator|.
name|class
argument_list|,
name|Names
operator|.
name|named
argument_list|(
name|GWT_UI_SERVLET
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|warFs
operator|==
literal|null
operator|&&
name|buckOut
operator|!=
literal|null
condition|)
block|{
name|filter
argument_list|(
literal|"/"
argument_list|)
operator|.
name|through
argument_list|(
operator|new
name|RecompileGwtUiFilter
argument_list|(
name|buckOut
argument_list|,
name|unpackedWar
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Provides
annotation|@
name|Singleton
annotation|@
name|Named
argument_list|(
name|GWT_UI_SERVLET
argument_list|)
DECL|method|getGwtUiServlet (@amedCACHE) Cache<Path, Resource> cache)
name|HttpServlet
name|getGwtUiServlet
parameter_list|(
annotation|@
name|Named
argument_list|(
name|CACHE
argument_list|)
name|Cache
argument_list|<
name|Path
argument_list|,
name|Resource
argument_list|>
name|cache
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|warFs
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|WarGwtUiServlet
argument_list|(
name|cache
argument_list|,
name|warFs
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|DeveloperGwtUiServlet
argument_list|(
name|cache
argument_list|,
name|unpackedWar
argument_list|)
return|;
block|}
block|}
DECL|method|getDistributionArchive ()
specifier|private
specifier|static
name|FileSystem
name|getDistributionArchive
parameter_list|()
block|{
try|try
block|{
return|return
name|GerritLauncher
operator|.
name|getDistributionArchiveFileSystem
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|(
name|e
operator|instanceof
name|FileNotFoundException
operator|)
operator|&&
name|GerritLauncher
operator|.
name|NOT_ARCHIVED
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|ProvisionException
name|pe
init|=
operator|new
name|ProvisionException
argument_list|(
literal|"Error reading gerrit.war"
argument_list|)
decl_stmt|;
name|pe
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|pe
throw|;
block|}
block|}
block|}
DECL|method|getDeveloperBuckOut ()
specifier|private
specifier|static
name|Path
name|getDeveloperBuckOut
parameter_list|()
block|{
try|try
block|{
return|return
name|GerritLauncher
operator|.
name|getDeveloperBuckOut
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|makeWarTempDir ()
specifier|private
specifier|static
name|Path
name|makeWarTempDir
parameter_list|()
block|{
comment|// Obtain our local temporary directory, but it comes back as a file
comment|// so we have to switch it to be a directory post creation.
comment|//
try|try
block|{
name|File
name|dstwar
init|=
name|GerritLauncher
operator|.
name|createTempFile
argument_list|(
literal|"gerrit_"
argument_list|,
literal|"war"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dstwar
operator|.
name|delete
argument_list|()
operator|||
operator|!
name|dstwar
operator|.
name|mkdir
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot mkdir "
operator|+
name|dstwar
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
comment|// Jetty normally refuses to serve out of a symlinked directory, as
comment|// a security feature. Try to resolve out any symlinks in the path.
comment|//
try|try
block|{
return|return
name|dstwar
operator|.
name|getCanonicalFile
argument_list|()
operator|.
name|toPath
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
name|dstwar
operator|.
name|getAbsoluteFile
argument_list|()
operator|.
name|toPath
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|ProvisionException
name|pe
init|=
operator|new
name|ProvisionException
argument_list|(
literal|"Cannot create war tempdir"
argument_list|)
decl_stmt|;
name|pe
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|pe
throw|;
block|}
block|}
block|}
end_class

end_unit

