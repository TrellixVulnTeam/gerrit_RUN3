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
DECL|package|com.google.gerrit.plugin.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|plugin
operator|.
name|client
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|plugin
operator|.
name|client
operator|.
name|screen
operator|.
name|Screen
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
name|JavaScriptObject
import|;
end_import

begin_comment
comment|/**  * Wrapper around the plugin instance exposed by Gerrit.  *  * Listeners for events generated by the main UI must be registered  * through this instance.  */
end_comment

begin_class
DECL|class|Plugin
specifier|public
specifier|final
class|class
name|Plugin
extends|extends
name|JavaScriptObject
block|{
DECL|field|self
specifier|private
specifier|static
specifier|final
name|Plugin
name|self
init|=
name|install
argument_list|(
name|GWT
operator|.
name|getModuleBaseURL
argument_list|()
operator|+
name|GWT
operator|.
name|getModuleName
argument_list|()
operator|+
literal|".nocache.js"
argument_list|)
decl_stmt|;
comment|/** Obtain the plugin instance wrapper. */
DECL|method|get ()
specifier|public
specifier|static
name|Plugin
name|get
parameter_list|()
block|{
return|return
name|self
return|;
block|}
comment|/** Installed name of the plugin. */
DECL|method|getName ()
specifier|public
specifier|final
name|String
name|getName
parameter_list|()
block|{
return|return
name|getPluginName
argument_list|()
return|;
block|}
comment|/** Installed name of the plugin. */
DECL|method|getPluginName ()
specifier|public
specifier|final
specifier|native
name|String
name|getPluginName
parameter_list|()
comment|/*-{ return this.getPluginName() }-*/
function_decl|;
comment|/** Navigate the UI to the screen identified by the token. */
DECL|method|go (String token)
specifier|public
specifier|final
specifier|native
name|void
name|go
parameter_list|(
name|String
name|token
parameter_list|)
comment|/*-{ return this.go(token) }-*/
function_decl|;
comment|/** Refresh the current UI. */
DECL|method|refresh ()
specifier|public
specifier|final
specifier|native
name|void
name|refresh
parameter_list|()
comment|/*-{ return this.refresh() }-*/
function_decl|;
comment|/** Refresh Gerrit's menu bar. */
DECL|method|refreshMenuBar ()
specifier|public
specifier|final
specifier|native
name|void
name|refreshMenuBar
parameter_list|()
comment|/*-{ return this.refreshMenuBar() }-*/
function_decl|;
comment|/** Show message in Gerrit's ErrorDialog. */
DECL|method|showError (String message)
specifier|public
specifier|final
specifier|native
name|void
name|showError
parameter_list|(
name|String
name|message
parameter_list|)
comment|/*-{ return this.showError(message) }-*/
function_decl|;
comment|/**    * Register a screen displayed at {@code /#/x/plugin/token}.    *    * @param token literal anchor token appearing after the plugin name. For    *        regular expression matching use {@code screenRegex()} .    * @param entry callback function invoked to create the screen widgets.    */
DECL|method|screen (String token, Screen.EntryPoint entry)
specifier|public
specifier|final
name|void
name|screen
parameter_list|(
name|String
name|token
parameter_list|,
name|Screen
operator|.
name|EntryPoint
name|entry
parameter_list|)
block|{
name|screen
argument_list|(
name|token
argument_list|,
name|wrap
argument_list|(
name|entry
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|screen (String t, JavaScriptObject e)
specifier|private
specifier|final
specifier|native
name|void
name|screen
parameter_list|(
name|String
name|t
parameter_list|,
name|JavaScriptObject
name|e
parameter_list|)
comment|/*-{ this.screen(t, e) }-*/
function_decl|;
comment|/**    * Register a screen displayed at {@code /#/x/plugin/regex}.    *    * @param regex JavaScript {@code RegExp} expression to match the anchor token    *        after the plugin name. Matching groups are exposed through the    *        {@code Screen} object passed into the {@code Screen.EntryPoint}.    * @param entry callback function invoked to create the screen widgets.    */
DECL|method|screenRegex (String regex, Screen.EntryPoint entry)
specifier|public
specifier|final
name|void
name|screenRegex
parameter_list|(
name|String
name|regex
parameter_list|,
name|Screen
operator|.
name|EntryPoint
name|entry
parameter_list|)
block|{
name|screenRegex
argument_list|(
name|regex
argument_list|,
name|wrap
argument_list|(
name|entry
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|screenRegex (String p, JavaScriptObject e)
specifier|private
specifier|final
specifier|native
name|void
name|screenRegex
parameter_list|(
name|String
name|p
parameter_list|,
name|JavaScriptObject
name|e
parameter_list|)
comment|/*-{ this.screen(new $wnd.RegExp(p), e) }-*/
function_decl|;
DECL|method|Plugin ()
specifier|protected
name|Plugin
parameter_list|()
block|{   }
DECL|method|_initialized ()
specifier|native
name|void
name|_initialized
parameter_list|()
comment|/*-{ this._success = true }-*/
function_decl|;
DECL|method|_loaded ()
specifier|native
name|void
name|_loaded
parameter_list|()
comment|/*-{ this._loadedGwt() }-*/
function_decl|;
DECL|method|install (String u)
specifier|private
specifier|static
specifier|native
specifier|final
name|Plugin
name|install
parameter_list|(
name|String
name|u
parameter_list|)
comment|/*-{ return $wnd.Gerrit.installGwt(u) }-*/
function_decl|;
DECL|method|wrap (Screen.EntryPoint b)
specifier|private
specifier|static
specifier|final
specifier|native
name|JavaScriptObject
name|wrap
parameter_list|(
name|Screen
operator|.
name|EntryPoint
name|b
parameter_list|)
comment|/*-{     return $entry(function(c){       b.@com.google.gerrit.plugin.client.screen.Screen.EntryPoint::onLoad(Lcom/google/gerrit/plugin/client/screen/Screen;)(         @com.google.gerrit.plugin.client.screen.Screen::new(Lcom/google/gerrit/plugin/client/screen/Screen$Context;)(c));     });   }-*/
function_decl|;
block|}
end_class

end_unit

