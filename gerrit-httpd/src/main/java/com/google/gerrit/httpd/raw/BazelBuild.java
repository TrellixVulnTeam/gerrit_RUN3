begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
name|common
operator|.
name|escape
operator|.
name|Escaper
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|html
operator|.
name|HtmlEscapers
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|ByteStreams
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
name|common
operator|.
name|TimeUtil
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtexpui
operator|.
name|server
operator|.
name|CacheHeaders
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
name|InterruptedIOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|HttpServletResponse
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
name|RawParseUtils
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

begin_class
DECL|class|BazelBuild
specifier|public
class|class
name|BazelBuild
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
name|BazelBuild
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|sourceRoot
specifier|private
specifier|final
name|Path
name|sourceRoot
decl_stmt|;
DECL|method|BazelBuild (Path sourceRoot)
specifier|public
name|BazelBuild
parameter_list|(
name|Path
name|sourceRoot
parameter_list|)
block|{
name|this
operator|.
name|sourceRoot
operator|=
name|sourceRoot
expr_stmt|;
block|}
comment|// builds the given label.
DECL|method|build (Label label)
specifier|public
name|void
name|build
parameter_list|(
name|Label
name|label
parameter_list|)
throws|throws
name|IOException
throws|,
name|BuildFailureException
block|{
name|ProcessBuilder
name|proc
init|=
name|newBuildProcess
argument_list|(
name|label
argument_list|)
decl_stmt|;
name|proc
operator|.
name|directory
argument_list|(
name|sourceRoot
operator|.
name|toFile
argument_list|()
argument_list|)
operator|.
name|redirectErrorStream
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"building "
operator|+
name|label
operator|.
name|fullName
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|TimeUtil
operator|.
name|nowMs
argument_list|()
decl_stmt|;
name|Process
name|rebuild
init|=
name|proc
operator|.
name|start
argument_list|()
decl_stmt|;
name|byte
index|[]
name|out
decl_stmt|;
try|try
init|(
name|InputStream
name|in
init|=
name|rebuild
operator|.
name|getInputStream
argument_list|()
init|)
block|{
name|out
operator|=
name|ByteStreams
operator|.
name|toByteArray
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|rebuild
operator|.
name|getOutputStream
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|int
name|status
decl_stmt|;
try|try
block|{
name|status
operator|=
name|rebuild
operator|.
name|waitFor
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
name|InterruptedIOException
argument_list|(
literal|"interrupted waiting for "
operator|+
name|proc
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|status
operator|!=
literal|0
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"build failed: "
operator|+
operator|new
name|String
argument_list|(
name|out
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|BuildFailureException
argument_list|(
name|out
argument_list|)
throw|;
block|}
name|long
name|time
init|=
name|TimeUtil
operator|.
name|nowMs
argument_list|()
operator|-
name|start
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"UPDATED    %s in %.3fs"
argument_list|,
name|label
operator|.
name|fullName
argument_list|()
argument_list|,
name|time
operator|/
literal|1000.0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Represents a label in bazel.
DECL|class|Label
specifier|static
class|class
name|Label
block|{
DECL|field|pkg
specifier|protected
specifier|final
name|String
name|pkg
decl_stmt|;
DECL|field|name
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|fullName ()
specifier|public
name|String
name|fullName
parameter_list|()
block|{
return|return
literal|"//"
operator|+
name|pkg
operator|+
literal|":"
operator|+
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|fullName
argument_list|()
return|;
block|}
comment|// Label in Bazel style.
DECL|method|Label (String pkg, String name)
name|Label
parameter_list|(
name|String
name|pkg
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|pkg
operator|=
name|pkg
expr_stmt|;
block|}
block|}
DECL|class|BuildFailureException
specifier|static
class|class
name|BuildFailureException
extends|extends
name|Exception
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
DECL|field|why
specifier|final
name|byte
index|[]
name|why
decl_stmt|;
DECL|method|BuildFailureException (byte[] why)
name|BuildFailureException
parameter_list|(
name|byte
index|[]
name|why
parameter_list|)
block|{
name|this
operator|.
name|why
operator|=
name|why
expr_stmt|;
block|}
DECL|method|display (String rule, HttpServletResponse res)
specifier|public
name|void
name|display
parameter_list|(
name|String
name|rule
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
throws|throws
name|IOException
block|{
name|res
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContentType
argument_list|(
literal|"text/html"
argument_list|)
expr_stmt|;
name|res
operator|.
name|setCharacterEncoding
argument_list|(
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|CacheHeaders
operator|.
name|setNotCacheable
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|Escaper
name|html
init|=
name|HtmlEscapers
operator|.
name|htmlEscaper
argument_list|()
decl_stmt|;
try|try
init|(
name|PrintWriter
name|w
init|=
name|res
operator|.
name|getWriter
argument_list|()
init|)
block|{
name|w
operator|.
name|write
argument_list|(
literal|"<html><title>BUILD FAILED</title><body>"
argument_list|)
expr_stmt|;
name|w
operator|.
name|format
argument_list|(
literal|"<h1>%s FAILED</h1>"
argument_list|,
name|html
operator|.
name|escape
argument_list|(
name|rule
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|"<pre>"
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|html
operator|.
name|escape
argument_list|(
name|RawParseUtils
operator|.
name|decode
argument_list|(
name|why
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|"</pre>"
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|"</body></html>"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|newBuildProcess (Label label)
specifier|private
name|ProcessBuilder
name|newBuildProcess
parameter_list|(
name|Label
name|label
parameter_list|)
block|{
return|return
operator|new
name|ProcessBuilder
argument_list|(
literal|"bazel"
argument_list|,
literal|"build"
argument_list|,
name|label
operator|.
name|fullName
argument_list|()
argument_list|)
return|;
block|}
comment|/** returns the root relative path to the artifact for the given label */
DECL|method|targetPath (Label l)
specifier|public
name|Path
name|targetPath
parameter_list|(
name|Label
name|l
parameter_list|)
block|{
return|return
name|sourceRoot
operator|.
name|resolve
argument_list|(
literal|"bazel-bin"
argument_list|)
operator|.
name|resolve
argument_list|(
name|l
operator|.
name|pkg
argument_list|)
operator|.
name|resolve
argument_list|(
name|l
operator|.
name|name
argument_list|)
return|;
block|}
comment|/** Label for the agent specific GWT zip. */
DECL|method|gwtZipLabel (String agent)
specifier|public
name|Label
name|gwtZipLabel
parameter_list|(
name|String
name|agent
parameter_list|)
block|{
return|return
operator|new
name|Label
argument_list|(
literal|"gerrit-gwtui"
argument_list|,
literal|"ui_"
operator|+
name|agent
operator|+
literal|".zip"
argument_list|)
return|;
block|}
comment|/** Label for the polygerrit component zip. */
DECL|method|polygerritComponents ()
specifier|public
name|Label
name|polygerritComponents
parameter_list|()
block|{
return|return
operator|new
name|Label
argument_list|(
literal|"polygerrit-ui"
argument_list|,
literal|"polygerrit_components.bower_components.zip"
argument_list|)
return|;
block|}
comment|/** Label for the fonts zip file. */
DECL|method|fontZipLabel ()
specifier|public
name|Label
name|fontZipLabel
parameter_list|()
block|{
return|return
operator|new
name|Label
argument_list|(
literal|"polygerrit-ui"
argument_list|,
literal|"fonts.zip"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

