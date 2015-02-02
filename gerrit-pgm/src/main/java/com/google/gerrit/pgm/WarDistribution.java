begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.pgm
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
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
name|InitPlugins
operator|.
name|JAR
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
name|InitPlugins
operator|.
name|PLUGIN_DIR
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
name|init
operator|.
name|PluginsDistribution
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipFile
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|WarDistribution
specifier|public
class|class
name|WarDistribution
implements|implements
name|PluginsDistribution
block|{
annotation|@
name|Override
DECL|method|foreach (Processor processor)
specifier|public
name|void
name|foreach
parameter_list|(
name|Processor
name|processor
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|IOException
block|{
name|File
name|myWar
init|=
name|GerritLauncher
operator|.
name|getDistributionArchive
argument_list|()
decl_stmt|;
if|if
condition|(
name|myWar
operator|.
name|isFile
argument_list|()
condition|)
block|{
try|try
init|(
name|ZipFile
name|zf
init|=
operator|new
name|ZipFile
argument_list|(
name|myWar
argument_list|)
init|)
block|{
name|Enumeration
argument_list|<
name|?
extends|extends
name|ZipEntry
argument_list|>
name|e
init|=
name|zf
operator|.
name|entries
argument_list|()
decl_stmt|;
while|while
condition|(
name|e
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|ZipEntry
name|ze
init|=
name|e
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|ze
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|ze
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|PLUGIN_DIR
argument_list|)
operator|&&
name|ze
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
name|JAR
argument_list|)
condition|)
block|{
name|String
name|pluginJarName
init|=
operator|new
name|File
argument_list|(
name|ze
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|pluginName
init|=
name|pluginJarName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pluginJarName
operator|.
name|length
argument_list|()
operator|-
name|JAR
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|in
init|=
name|zf
operator|.
name|getInputStream
argument_list|(
name|ze
argument_list|)
init|)
block|{
name|processor
operator|.
name|process
argument_list|(
name|pluginName
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|listPluginNames ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|listPluginNames
parameter_list|()
throws|throws
name|FileNotFoundException
block|{
comment|// not yet used
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

