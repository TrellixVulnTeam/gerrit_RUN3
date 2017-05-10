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
DECL|package|com.google.gerrit.httpd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
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
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|UnzippedDistribution
class|class
name|UnzippedDistribution
implements|implements
name|PluginsDistribution
block|{
DECL|field|servletContext
specifier|private
name|ServletContext
name|servletContext
decl_stmt|;
DECL|field|pluginsDir
specifier|private
name|File
name|pluginsDir
decl_stmt|;
DECL|method|UnzippedDistribution (ServletContext servletContext)
name|UnzippedDistribution
parameter_list|(
name|ServletContext
name|servletContext
parameter_list|)
block|{
name|this
operator|.
name|servletContext
operator|=
name|servletContext
expr_stmt|;
block|}
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
index|[]
name|list
init|=
name|getPluginsDir
argument_list|()
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|p
range|:
name|list
control|)
block|{
name|String
name|pluginJarName
init|=
name|p
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
name|Files
operator|.
name|newInputStream
argument_list|(
name|p
operator|.
name|toPath
argument_list|()
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
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
index|[]
name|list
init|=
name|getPluginsDir
argument_list|()
operator|.
name|list
argument_list|()
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|pluginJarName
range|:
name|list
control|)
block|{
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
name|names
operator|.
name|add
argument_list|(
name|pluginName
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|names
return|;
block|}
DECL|method|getPluginsDir ()
specifier|private
name|File
name|getPluginsDir
parameter_list|()
block|{
if|if
condition|(
name|pluginsDir
operator|==
literal|null
condition|)
block|{
name|File
name|root
init|=
operator|new
name|File
argument_list|(
name|servletContext
operator|.
name|getRealPath
argument_list|(
literal|""
argument_list|)
argument_list|)
decl_stmt|;
name|pluginsDir
operator|=
operator|new
name|File
argument_list|(
name|root
argument_list|,
name|PLUGIN_DIR
argument_list|)
expr_stmt|;
block|}
return|return
name|pluginsDir
return|;
block|}
block|}
end_class

end_unit

