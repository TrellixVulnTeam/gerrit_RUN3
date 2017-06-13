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
DECL|package|com.google.gwtexpui.css.rebind
package|package
name|com
operator|.
name|google
operator|.
name|gwtexpui
operator|.
name|css
operator|.
name|rebind
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|ext
operator|.
name|LinkerContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|ext
operator|.
name|TreeLogger
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|ext
operator|.
name|UnableToCompleteException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|ext
operator|.
name|linker
operator|.
name|AbstractLinker
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|ext
operator|.
name|linker
operator|.
name|Artifact
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|ext
operator|.
name|linker
operator|.
name|ArtifactSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|ext
operator|.
name|linker
operator|.
name|LinkerOrder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|ext
operator|.
name|linker
operator|.
name|PublicResource
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|ext
operator|.
name|linker
operator|.
name|impl
operator|.
name|StandardLinkerContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|ext
operator|.
name|linker
operator|.
name|impl
operator|.
name|StandardStylesheetReference
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|dev
operator|.
name|util
operator|.
name|Util
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|HashMap
import|;
end_import

begin_class
annotation|@
name|LinkerOrder
argument_list|(
name|LinkerOrder
operator|.
name|Order
operator|.
name|PRE
argument_list|)
DECL|class|CssLinker
specifier|public
class|class
name|CssLinker
extends|extends
name|AbstractLinker
block|{
annotation|@
name|Override
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"CssLinker"
return|;
block|}
annotation|@
name|Override
DECL|method|link (final TreeLogger logger, LinkerContext context, ArtifactSet artifacts)
specifier|public
name|ArtifactSet
name|link
parameter_list|(
specifier|final
name|TreeLogger
name|logger
parameter_list|,
name|LinkerContext
name|context
parameter_list|,
name|ArtifactSet
name|artifacts
parameter_list|)
throws|throws
name|UnableToCompleteException
block|{
specifier|final
name|ArtifactSet
name|returnTo
init|=
operator|new
name|ArtifactSet
argument_list|()
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|PublicResource
argument_list|>
name|css
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|StandardStylesheetReference
name|ssr
range|:
name|artifacts
operator|.
expr|<
name|StandardStylesheetReference
operator|>
name|find
argument_list|(
name|StandardStylesheetReference
operator|.
name|class
argument_list|)
control|)
block|{
name|css
operator|.
name|put
argument_list|(
name|ssr
operator|.
name|getSrc
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|PublicResource
name|pr
range|:
name|artifacts
operator|.
expr|<
name|PublicResource
operator|>
name|find
argument_list|(
name|PublicResource
operator|.
name|class
argument_list|)
control|)
block|{
if|if
condition|(
name|css
operator|.
name|containsKey
argument_list|(
name|pr
operator|.
name|getPartialPath
argument_list|()
argument_list|)
condition|)
block|{
name|css
operator|.
name|put
argument_list|(
name|pr
operator|.
name|getPartialPath
argument_list|()
argument_list|,
operator|new
name|CssPubRsrc
argument_list|(
name|name
argument_list|(
name|logger
argument_list|,
name|pr
argument_list|)
argument_list|,
name|pr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Artifact
argument_list|<
name|?
argument_list|>
name|a
range|:
name|artifacts
control|)
block|{
if|if
condition|(
name|a
operator|instanceof
name|PublicResource
condition|)
block|{
specifier|final
name|PublicResource
name|r
init|=
operator|(
name|PublicResource
operator|)
name|a
decl_stmt|;
if|if
condition|(
name|css
operator|.
name|containsKey
argument_list|(
name|r
operator|.
name|getPartialPath
argument_list|()
argument_list|)
condition|)
block|{
name|a
operator|=
name|css
operator|.
name|get
argument_list|(
name|r
operator|.
name|getPartialPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|a
operator|instanceof
name|StandardStylesheetReference
condition|)
block|{
specifier|final
name|StandardStylesheetReference
name|r
init|=
operator|(
name|StandardStylesheetReference
operator|)
name|a
decl_stmt|;
specifier|final
name|PublicResource
name|p
init|=
name|css
operator|.
name|get
argument_list|(
name|r
operator|.
name|getSrc
argument_list|()
argument_list|)
decl_stmt|;
name|a
operator|=
operator|new
name|StandardStylesheetReference
argument_list|(
name|p
operator|.
name|getPartialPath
argument_list|()
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
name|returnTo
operator|.
name|add
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
return|return
name|returnTo
return|;
block|}
DECL|method|name (TreeLogger logger, PublicResource r)
specifier|private
name|String
name|name
parameter_list|(
name|TreeLogger
name|logger
parameter_list|,
name|PublicResource
name|r
parameter_list|)
throws|throws
name|UnableToCompleteException
block|{
name|byte
index|[]
name|out
decl_stmt|;
try|try
init|(
name|ByteArrayOutputStream
name|tmp
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
init|;
name|InputStream
name|in
operator|=
name|r
operator|.
name|getContents
argument_list|(
name|logger
argument_list|)
init|)
block|{
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|2048
index|]
decl_stmt|;
name|int
name|n
decl_stmt|;
while|while
condition|(
operator|(
name|n
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|tmp
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
name|out
operator|=
name|tmp
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
specifier|final
name|UnableToCompleteException
name|ute
init|=
operator|new
name|UnableToCompleteException
argument_list|()
decl_stmt|;
name|ute
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|ute
throw|;
block|}
name|String
name|base
init|=
name|r
operator|.
name|getPartialPath
argument_list|()
decl_stmt|;
specifier|final
name|int
name|s
init|=
name|base
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|s
condition|)
block|{
name|base
operator|=
name|base
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|base
operator|=
literal|""
expr_stmt|;
block|}
return|return
name|base
operator|+
name|Util
operator|.
name|computeStrongName
argument_list|(
name|out
argument_list|)
operator|+
literal|".cache.css"
return|;
block|}
DECL|class|CssPubRsrc
specifier|private
specifier|static
class|class
name|CssPubRsrc
extends|extends
name|PublicResource
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|src
specifier|private
specifier|final
name|PublicResource
name|src
decl_stmt|;
DECL|method|CssPubRsrc (String partialPath, PublicResource r)
name|CssPubRsrc
parameter_list|(
name|String
name|partialPath
parameter_list|,
name|PublicResource
name|r
parameter_list|)
block|{
name|super
argument_list|(
name|StandardLinkerContext
operator|.
name|class
argument_list|,
name|partialPath
argument_list|)
expr_stmt|;
name|src
operator|=
name|r
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContents (TreeLogger logger)
specifier|public
name|InputStream
name|getContents
parameter_list|(
name|TreeLogger
name|logger
parameter_list|)
throws|throws
name|UnableToCompleteException
block|{
return|return
name|src
operator|.
name|getContents
argument_list|(
name|logger
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getLastModified ()
specifier|public
name|long
name|getLastModified
parameter_list|()
block|{
return|return
name|src
operator|.
name|getLastModified
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

