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
DECL|package|com.google.gerrit.client.api
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|api
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
name|client
operator|.
name|GWT
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
name|client
operator|.
name|JavaScriptException
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
name|client
operator|.
name|JsArrayString
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
name|client
operator|.
name|impl
operator|.
name|StackTraceCreator
import|;
end_import

begin_comment
comment|/**  * Determines the name a plugin has been installed under.  *  * This implementation guesses the name a plugin runs under by looking at the  * JavaScript call stack and identifying the URL of the script file calling  * {@code Gerrit.install()}. The simple approach applied here is looking at  * the source URLs and extracting the name out of the string, e.g.:  * {@code "http://localhost:8080/plugins/{name}/static/foo.js"}.  */
end_comment

begin_class
DECL|class|PluginName
class|class
name|PluginName
block|{
DECL|field|UNKNOWN
specifier|private
specifier|static
specifier|final
name|String
name|UNKNOWN
init|=
literal|"<unknown>"
decl_stmt|;
DECL|method|baseUrl ()
specifier|private
specifier|static
name|String
name|baseUrl
parameter_list|()
block|{
return|return
name|GWT
operator|.
name|getHostPageBaseURL
argument_list|()
operator|+
literal|"plugins/"
return|;
block|}
DECL|method|getCallerUrl ()
specifier|static
name|String
name|getCallerUrl
parameter_list|()
block|{
return|return
name|GWT
operator|.
expr|<
name|PluginName
operator|>
name|create
argument_list|(
name|PluginName
operator|.
name|class
argument_list|)
operator|.
name|findCallerUrl
argument_list|()
return|;
block|}
DECL|method|fromUrl (String url)
specifier|static
name|String
name|fromUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|String
name|baseUrl
init|=
name|baseUrl
argument_list|()
decl_stmt|;
if|if
condition|(
name|url
operator|!=
literal|null
operator|&&
name|url
operator|.
name|startsWith
argument_list|(
name|baseUrl
argument_list|)
condition|)
block|{
name|int
name|s
init|=
name|url
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|,
name|baseUrl
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|>
literal|0
condition|)
block|{
return|return
name|url
operator|.
name|substring
argument_list|(
name|baseUrl
operator|.
name|length
argument_list|()
argument_list|,
name|s
argument_list|)
return|;
block|}
block|}
return|return
name|UNKNOWN
return|;
block|}
DECL|method|findCallerUrl ()
name|String
name|findCallerUrl
parameter_list|()
block|{
name|JavaScriptException
name|err
init|=
name|makeException
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasStack
argument_list|(
name|err
argument_list|)
condition|)
block|{
return|return
name|PluginNameMoz
operator|.
name|getUrl
argument_list|(
name|err
argument_list|)
return|;
block|}
name|String
name|baseUrl
init|=
name|baseUrl
argument_list|()
decl_stmt|;
name|StackTraceElement
index|[]
name|trace
init|=
name|getTrace
argument_list|(
name|err
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|trace
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|String
name|u
init|=
name|trace
index|[
name|i
index|]
operator|.
name|getFileName
argument_list|()
decl_stmt|;
if|if
condition|(
name|u
operator|!=
literal|null
operator|&&
name|u
operator|.
name|startsWith
argument_list|(
name|baseUrl
argument_list|)
condition|)
block|{
return|return
name|u
return|;
block|}
block|}
return|return
name|UNKNOWN
return|;
block|}
DECL|method|getTrace (JavaScriptException err)
specifier|private
specifier|static
name|StackTraceElement
index|[]
name|getTrace
parameter_list|(
name|JavaScriptException
name|err
parameter_list|)
block|{
name|StackTraceCreator
operator|.
name|fillInStackTrace
argument_list|(
name|err
argument_list|)
expr_stmt|;
return|return
name|err
operator|.
name|getStackTrace
argument_list|()
return|;
block|}
DECL|method|makeException ()
specifier|protected
specifier|static
specifier|final
specifier|native
name|JavaScriptException
name|makeException
parameter_list|()
comment|/*-{ try { null.a() } catch (e) { return e } }-*/
function_decl|;
DECL|method|hasStack (JavaScriptException e)
specifier|private
specifier|static
specifier|final
specifier|native
name|boolean
name|hasStack
parameter_list|(
name|JavaScriptException
name|e
parameter_list|)
comment|/*-{ return !!e.stack }-*/
function_decl|;
comment|/** Extracts URL from the stack frame. */
DECL|class|PluginNameMoz
specifier|static
class|class
name|PluginNameMoz
extends|extends
name|PluginName
block|{
DECL|method|findCallerUrl ()
name|String
name|findCallerUrl
parameter_list|()
block|{
return|return
name|getUrl
argument_list|(
name|makeException
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getUrl (JavaScriptException e)
specifier|private
specifier|static
name|String
name|getUrl
parameter_list|(
name|JavaScriptException
name|e
parameter_list|)
block|{
name|String
name|baseUrl
init|=
name|baseUrl
argument_list|()
decl_stmt|;
name|JsArrayString
name|stack
init|=
name|getStack
argument_list|(
name|e
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|stack
operator|.
name|length
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|String
name|frame
init|=
name|stack
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|at
init|=
name|frame
operator|.
name|indexOf
argument_list|(
name|baseUrl
argument_list|)
decl_stmt|;
if|if
condition|(
name|at
operator|>=
literal|0
condition|)
block|{
name|int
name|end
init|=
name|frame
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|,
name|at
operator|+
name|baseUrl
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|end
operator|<
literal|0
condition|)
block|{
name|end
operator|=
name|frame
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
return|return
name|frame
operator|.
name|substring
argument_list|(
name|at
argument_list|,
name|end
argument_list|)
return|;
block|}
block|}
return|return
name|UNKNOWN
return|;
block|}
DECL|method|getStack (JavaScriptException e)
specifier|private
specifier|static
specifier|final
specifier|native
name|JsArrayString
name|getStack
parameter_list|(
name|JavaScriptException
name|e
parameter_list|)
comment|/*-{ return e.stack ? e.stack.split('\n') : [] }-*/
function_decl|;
block|}
block|}
end_class

end_unit

