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
DECL|package|com.google.gerrit.common
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
package|;
end_package

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
name|Constants
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
name|IO
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
DECL|class|FileUtil
specifier|public
class|class
name|FileUtil
block|{
DECL|method|modified (FileBasedConfig cfg)
specifier|public
specifier|static
name|boolean
name|modified
parameter_list|(
name|FileBasedConfig
name|cfg
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|curVers
decl_stmt|;
try|try
block|{
name|curVers
operator|=
name|IO
operator|.
name|readFully
argument_list|(
name|cfg
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|notFound
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
name|byte
index|[]
name|newVers
init|=
name|Constants
operator|.
name|encode
argument_list|(
name|cfg
operator|.
name|toText
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|curVers
argument_list|,
name|newVers
argument_list|)
return|;
block|}
DECL|method|mkdir (final File path)
specifier|public
specifier|static
name|void
name|mkdir
parameter_list|(
specifier|final
name|File
name|path
parameter_list|)
block|{
if|if
condition|(
operator|!
name|path
operator|.
name|isDirectory
argument_list|()
operator|&&
operator|!
name|path
operator|.
name|mkdir
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|Die
argument_list|(
literal|"Cannot make directory "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
DECL|method|chmod (final int mode, final Path path)
specifier|public
specifier|static
name|void
name|chmod
parameter_list|(
specifier|final
name|int
name|mode
parameter_list|,
specifier|final
name|Path
name|path
parameter_list|)
block|{
comment|// TODO(dborowitz): Is there a portable way to do this with NIO?
name|chmod
argument_list|(
name|mode
argument_list|,
name|path
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|chmod (final int mode, final File path)
specifier|public
specifier|static
name|void
name|chmod
parameter_list|(
specifier|final
name|int
name|mode
parameter_list|,
specifier|final
name|File
name|path
parameter_list|)
block|{
name|path
operator|.
name|setReadable
argument_list|(
literal|false
argument_list|,
literal|false
comment|/* all */
argument_list|)
expr_stmt|;
name|path
operator|.
name|setWritable
argument_list|(
literal|false
argument_list|,
literal|false
comment|/* all */
argument_list|)
expr_stmt|;
name|path
operator|.
name|setExecutable
argument_list|(
literal|false
argument_list|,
literal|false
comment|/* all */
argument_list|)
expr_stmt|;
name|path
operator|.
name|setReadable
argument_list|(
operator|(
name|mode
operator|&
literal|0400
operator|)
operator|==
literal|0400
argument_list|,
literal|true
comment|/* owner only */
argument_list|)
expr_stmt|;
name|path
operator|.
name|setWritable
argument_list|(
operator|(
name|mode
operator|&
literal|0200
operator|)
operator|==
literal|0200
argument_list|,
literal|true
comment|/* owner only */
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|isDirectory
argument_list|()
operator|||
operator|(
name|mode
operator|&
literal|0100
operator|)
operator|==
literal|0100
condition|)
block|{
name|path
operator|.
name|setExecutable
argument_list|(
literal|true
argument_list|,
literal|true
comment|/* owner only */
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mode
operator|&
literal|0044
operator|)
operator|==
literal|0044
condition|)
block|{
name|path
operator|.
name|setReadable
argument_list|(
literal|true
argument_list|,
literal|false
comment|/* all */
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|mode
operator|&
literal|0011
operator|)
operator|==
literal|0011
condition|)
block|{
name|path
operator|.
name|setExecutable
argument_list|(
literal|true
argument_list|,
literal|false
comment|/* all */
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get the last modified time of a path.    *<p>    * Equivalent to {@code File#lastModified()}, returning 0 on errors, including    * file not found. Callers that prefer exceptions can use {@link    * Files#getLastModifiedTime(Path, java.nio.file.LinkOption...)}.    *    * @param p path.    * @return last modified time, in milliseconds since epoch.    */
DECL|method|lastModified (Path p)
specifier|public
specifier|static
name|long
name|lastModified
parameter_list|(
name|Path
name|p
parameter_list|)
block|{
try|try
block|{
return|return
name|Files
operator|.
name|getLastModifiedTime
argument_list|(
name|p
argument_list|)
operator|.
name|toMillis
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
literal|0
return|;
block|}
block|}
DECL|method|FileUtil ()
specifier|private
name|FileUtil
parameter_list|()
block|{   }
block|}
end_class

end_unit

