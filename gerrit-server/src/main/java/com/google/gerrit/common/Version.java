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
name|io
operator|.
name|BufferedReader
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

begin_class
DECL|class|Version
specifier|public
class|class
name|Version
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
name|Version
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|version
specifier|private
specifier|static
specifier|final
name|String
name|version
decl_stmt|;
DECL|method|getVersion ()
specifier|public
specifier|static
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
static|static
block|{
name|version
operator|=
name|loadVersion
argument_list|()
expr_stmt|;
block|}
DECL|method|loadVersion ()
specifier|private
specifier|static
name|String
name|loadVersion
parameter_list|()
block|{
try|try
init|(
name|InputStream
name|in
init|=
name|Version
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"Version"
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
return|return
literal|"(dev)"
return|;
block|}
try|try
init|(
name|BufferedReader
name|r
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
init|)
block|{
name|String
name|vs
init|=
name|r
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|vs
operator|!=
literal|null
operator|&&
name|vs
operator|.
name|startsWith
argument_list|(
literal|"v"
argument_list|)
condition|)
block|{
name|vs
operator|=
name|vs
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|vs
operator|!=
literal|null
operator|&&
name|vs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|vs
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|vs
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|"(unknown version)"
return|;
block|}
block|}
DECL|method|Version ()
specifier|private
name|Version
parameter_list|()
block|{   }
block|}
end_class

end_unit

