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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|LibraryDownload
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
name|Provider
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|lang
operator|.
name|reflect
operator|.
name|Modifier
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
name|Config
import|;
end_import

begin_comment
comment|/** Standard {@link LibraryDownloader} instances derived from configuration. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|Libraries
class|class
name|Libraries
block|{
DECL|field|RESOURCE_FILE
specifier|private
specifier|static
specifier|final
name|String
name|RESOURCE_FILE
init|=
literal|"com/google/gerrit/pgm/init/libraries.config"
decl_stmt|;
DECL|field|downloadProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|LibraryDownloader
argument_list|>
name|downloadProvider
decl_stmt|;
DECL|field|skippedDownloads
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|skippedDownloads
decl_stmt|;
DECL|field|skipAllDownloads
specifier|private
specifier|final
name|boolean
name|skipAllDownloads
decl_stmt|;
DECL|field|db2Driver
comment|/* final */
name|LibraryDownloader
name|db2Driver
decl_stmt|;
DECL|field|db2DriverLicense
comment|/* final */
name|LibraryDownloader
name|db2DriverLicense
decl_stmt|;
DECL|field|hanaDriver
comment|/* final */
name|LibraryDownloader
name|hanaDriver
decl_stmt|;
DECL|field|mysqlDriver
comment|/* final */
name|LibraryDownloader
name|mysqlDriver
decl_stmt|;
DECL|field|oracleDriver
comment|/* final */
name|LibraryDownloader
name|oracleDriver
decl_stmt|;
annotation|@
name|Inject
DECL|method|Libraries ( final Provider<LibraryDownloader> downloadProvider, @LibraryDownload List<String> skippedDownloads, @LibraryDownload Boolean skipAllDownloads)
name|Libraries
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|LibraryDownloader
argument_list|>
name|downloadProvider
parameter_list|,
annotation|@
name|LibraryDownload
name|List
argument_list|<
name|String
argument_list|>
name|skippedDownloads
parameter_list|,
annotation|@
name|LibraryDownload
name|Boolean
name|skipAllDownloads
parameter_list|)
block|{
name|this
operator|.
name|downloadProvider
operator|=
name|downloadProvider
expr_stmt|;
name|this
operator|.
name|skippedDownloads
operator|=
name|skippedDownloads
expr_stmt|;
name|this
operator|.
name|skipAllDownloads
operator|=
name|skipAllDownloads
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
DECL|method|init ()
specifier|private
name|void
name|init
parameter_list|()
block|{
specifier|final
name|Config
name|cfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
try|try
block|{
name|cfg
operator|.
name|fromText
argument_list|(
name|read
argument_list|(
name|RESOURCE_FILE
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|ConfigInvalidException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
for|for
control|(
name|Field
name|f
range|:
name|Libraries
operator|.
name|class
operator|.
name|getDeclaredFields
argument_list|()
control|)
block|{
if|if
condition|(
operator|(
name|f
operator|.
name|getModifiers
argument_list|()
operator|&
name|Modifier
operator|.
name|STATIC
operator|)
operator|==
literal|0
operator|&&
name|f
operator|.
name|getType
argument_list|()
operator|==
name|LibraryDownloader
operator|.
name|class
condition|)
block|{
try|try
block|{
name|f
operator|.
name|set
argument_list|(
name|this
argument_list|,
name|downloadProvider
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
decl||
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot initialize "
operator|+
name|f
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
for|for
control|(
name|Field
name|f
range|:
name|Libraries
operator|.
name|class
operator|.
name|getDeclaredFields
argument_list|()
control|)
block|{
if|if
condition|(
operator|(
name|f
operator|.
name|getModifiers
argument_list|()
operator|&
name|Modifier
operator|.
name|STATIC
operator|)
operator|==
literal|0
operator|&&
name|f
operator|.
name|getType
argument_list|()
operator|==
name|LibraryDownloader
operator|.
name|class
condition|)
block|{
try|try
block|{
name|init
argument_list|(
name|f
argument_list|,
name|cfg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
decl||
name|IllegalAccessException
decl||
name|NoSuchFieldException
decl||
name|SecurityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot configure "
operator|+
name|f
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|method|init (Field field, Config cfg)
specifier|private
name|void
name|init
parameter_list|(
name|Field
name|field
parameter_list|,
name|Config
name|cfg
parameter_list|)
throws|throws
name|IllegalArgumentException
throws|,
name|IllegalAccessException
throws|,
name|NoSuchFieldException
throws|,
name|SecurityException
block|{
name|String
name|n
init|=
name|field
operator|.
name|getName
argument_list|()
decl_stmt|;
name|LibraryDownloader
name|dl
init|=
operator|(
name|LibraryDownloader
operator|)
name|field
operator|.
name|get
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|dl
operator|.
name|setName
argument_list|(
name|get
argument_list|(
name|cfg
argument_list|,
name|n
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|dl
operator|.
name|setJarUrl
argument_list|(
name|get
argument_list|(
name|cfg
argument_list|,
name|n
argument_list|,
literal|"url"
argument_list|)
argument_list|)
expr_stmt|;
name|dl
operator|.
name|setSHA1
argument_list|(
name|getOptional
argument_list|(
name|cfg
argument_list|,
name|n
argument_list|,
literal|"sha1"
argument_list|)
argument_list|)
expr_stmt|;
name|dl
operator|.
name|setRemove
argument_list|(
name|get
argument_list|(
name|cfg
argument_list|,
name|n
argument_list|,
literal|"remove"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|d
range|:
name|cfg
operator|.
name|getStringList
argument_list|(
literal|"library"
argument_list|,
name|n
argument_list|,
literal|"needs"
argument_list|)
control|)
block|{
name|dl
operator|.
name|addNeeds
argument_list|(
operator|(
name|LibraryDownloader
operator|)
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
name|d
argument_list|)
operator|.
name|get
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|dl
operator|.
name|setSkipDownload
argument_list|(
name|skipAllDownloads
operator|||
name|skippedDownloads
operator|.
name|contains
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getOptional (Config cfg, String name, String key)
specifier|private
specifier|static
name|String
name|getOptional
parameter_list|(
name|Config
name|cfg
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|key
parameter_list|)
block|{
return|return
name|doGet
argument_list|(
name|cfg
argument_list|,
name|name
argument_list|,
name|key
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|get (Config cfg, String name, String key)
specifier|private
specifier|static
name|String
name|get
parameter_list|(
name|Config
name|cfg
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|key
parameter_list|)
block|{
return|return
name|doGet
argument_list|(
name|cfg
argument_list|,
name|name
argument_list|,
name|key
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|doGet (Config cfg, String name, String key, boolean required)
specifier|private
specifier|static
name|String
name|doGet
parameter_list|(
name|Config
name|cfg
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|key
parameter_list|,
name|boolean
name|required
parameter_list|)
block|{
name|String
name|val
init|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"library"
argument_list|,
name|name
argument_list|,
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|val
operator|==
literal|null
operator|||
name|val
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
name|required
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Variable library."
operator|+
name|name
operator|+
literal|"."
operator|+
name|key
operator|+
literal|" is required within "
operator|+
name|RESOURCE_FILE
argument_list|)
throw|;
block|}
return|return
name|val
return|;
block|}
DECL|method|read (final String p)
specifier|private
specifier|static
name|String
name|read
parameter_list|(
specifier|final
name|String
name|p
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|InputStream
name|in
init|=
name|Libraries
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|p
argument_list|)
init|)
block|{
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Cannot load resource "
operator|+
name|p
argument_list|)
throw|;
block|}
try|try
init|(
name|Reader
name|r
init|=
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
name|UTF_8
argument_list|)
init|)
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|char
index|[]
name|tmp
init|=
operator|new
name|char
index|[
literal|512
index|]
decl_stmt|;
name|int
name|n
decl_stmt|;
while|while
condition|(
literal|0
operator|<
operator|(
name|n
operator|=
name|r
operator|.
name|read
argument_list|(
name|tmp
argument_list|)
operator|)
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|tmp
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

