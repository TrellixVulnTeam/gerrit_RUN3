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
name|username
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
name|internal
operator|.
name|storage
operator|.
name|file
operator|.
name|LockFile
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
name|FileInputStream
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_comment
comment|/** Initialize the {@code container} configuration section. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|InitContainer
class|class
name|InitContainer
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
DECL|field|container
specifier|private
specifier|final
name|Section
name|container
decl_stmt|;
annotation|@
name|Inject
DECL|method|InitContainer (final ConsoleUI ui, final SitePaths site, final Section.Factory sections)
name|InitContainer
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
name|container
operator|=
name|sections
operator|.
name|get
argument_list|(
literal|"container"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|FileNotFoundException
throws|,
name|IOException
block|{
name|ui
operator|.
name|header
argument_list|(
literal|"Container Process"
argument_list|)
expr_stmt|;
name|container
operator|.
name|string
argument_list|(
literal|"Run as"
argument_list|,
literal|"user"
argument_list|,
name|username
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|.
name|string
argument_list|(
literal|"Java runtime"
argument_list|,
literal|"javaHome"
argument_list|,
name|javaHome
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|myWar
decl_stmt|;
try|try
block|{
name|myWar
operator|=
name|GerritLauncher
operator|.
name|getDistributionArchive
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"warn: Cannot find distribution archive (e.g. gerrit.war)"
argument_list|)
expr_stmt|;
name|myWar
operator|=
literal|null
expr_stmt|;
block|}
name|String
name|path
init|=
name|container
operator|.
name|get
argument_list|(
literal|"war"
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|path
operator|=
name|container
operator|.
name|string
argument_list|(
literal|"Gerrit runtime"
argument_list|,
literal|"war"
argument_list|,
comment|//
name|myWar
operator|!=
literal|null
condition|?
name|myWar
operator|.
name|getAbsolutePath
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
operator|||
name|path
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"container.war is required"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|myWar
operator|!=
literal|null
condition|)
block|{
specifier|final
name|boolean
name|copy
decl_stmt|;
specifier|final
name|File
name|siteWar
init|=
name|site
operator|.
name|gerrit_war
decl_stmt|;
if|if
condition|(
name|siteWar
operator|.
name|exists
argument_list|()
condition|)
block|{
name|copy
operator|=
name|ui
operator|.
name|yesno
argument_list|(
literal|true
argument_list|,
literal|"Upgrade %s"
argument_list|,
name|siteWar
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|copy
operator|=
name|ui
operator|.
name|yesno
argument_list|(
literal|true
argument_list|,
literal|"Copy %s to %s"
argument_list|,
name|myWar
operator|.
name|getName
argument_list|()
argument_list|,
name|siteWar
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|copy
condition|)
block|{
name|container
operator|.
name|unset
argument_list|(
literal|"war"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|container
operator|.
name|set
argument_list|(
literal|"war"
argument_list|,
name|myWar
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|copy
condition|)
block|{
if|if
condition|(
operator|!
name|ui
operator|.
name|isBatch
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|format
argument_list|(
literal|"Copying %s to %s"
argument_list|,
name|myWar
operator|.
name|getName
argument_list|()
argument_list|,
name|siteWar
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|myWar
argument_list|)
decl_stmt|;
try|try
block|{
name|siteWar
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|LockFile
name|lf
init|=
operator|new
name|LockFile
argument_list|(
name|siteWar
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|lf
operator|.
name|lock
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot lock "
operator|+
name|siteWar
argument_list|)
throw|;
block|}
try|try
block|{
specifier|final
name|OutputStream
name|out
init|=
name|lf
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|byte
index|[]
name|tmp
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|n
init|=
name|in
operator|.
name|read
argument_list|(
name|tmp
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|<
literal|0
condition|)
block|{
break|break;
block|}
name|out
operator|.
name|write
argument_list|(
name|tmp
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|lf
operator|.
name|commit
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot commit "
operator|+
name|siteWar
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|lf
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
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
block|}
block|}
DECL|method|javaHome ()
specifier|private
specifier|static
name|String
name|javaHome
parameter_list|()
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.home"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

