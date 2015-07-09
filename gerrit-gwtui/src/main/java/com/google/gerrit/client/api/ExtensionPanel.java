begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
name|gerrit
operator|.
name|client
operator|.
name|GerritUiExtensionPoint
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
name|client
operator|.
name|rpc
operator|.
name|Natives
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
name|JsArray
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
name|dom
operator|.
name|client
operator|.
name|Element
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
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|FlowPanel
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
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|SimplePanel
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
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_class
DECL|class|ExtensionPanel
specifier|public
class|class
name|ExtensionPanel
extends|extends
name|FlowPanel
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ExtensionPanel
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|extensionPoint
specifier|private
specifier|final
name|GerritUiExtensionPoint
name|extensionPoint
decl_stmt|;
DECL|field|contexts
specifier|private
specifier|final
name|List
argument_list|<
name|Context
argument_list|>
name|contexts
decl_stmt|;
DECL|method|ExtensionPanel (GerritUiExtensionPoint extensionPoint)
specifier|public
name|ExtensionPanel
parameter_list|(
name|GerritUiExtensionPoint
name|extensionPoint
parameter_list|)
block|{
name|this
operator|.
name|extensionPoint
operator|=
name|extensionPoint
expr_stmt|;
name|this
operator|.
name|contexts
operator|=
name|create
argument_list|()
expr_stmt|;
block|}
DECL|method|create ()
specifier|private
name|List
argument_list|<
name|Context
argument_list|>
name|create
parameter_list|()
block|{
name|List
argument_list|<
name|Context
argument_list|>
name|contexts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Definition
name|def
range|:
name|Natives
operator|.
name|asList
argument_list|(
name|Definition
operator|.
name|get
argument_list|(
name|extensionPoint
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
control|)
block|{
name|SimplePanel
name|p
init|=
operator|new
name|SimplePanel
argument_list|()
decl_stmt|;
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|contexts
operator|.
name|add
argument_list|(
name|Context
operator|.
name|create
argument_list|(
name|def
argument_list|,
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|contexts
return|;
block|}
DECL|method|put (GerritUiExtensionPoint.Key key, String value)
specifier|public
name|void
name|put
parameter_list|(
name|GerritUiExtensionPoint
operator|.
name|Key
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
for|for
control|(
name|Context
name|ctx
range|:
name|contexts
control|)
block|{
name|ctx
operator|.
name|put
argument_list|(
name|key
operator|.
name|name
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|putInt (GerritUiExtensionPoint.Key key, int value)
specifier|public
name|void
name|putInt
parameter_list|(
name|GerritUiExtensionPoint
operator|.
name|Key
name|key
parameter_list|,
name|int
name|value
parameter_list|)
block|{
for|for
control|(
name|Context
name|ctx
range|:
name|contexts
control|)
block|{
name|ctx
operator|.
name|putInt
argument_list|(
name|key
operator|.
name|name
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|putBoolean (GerritUiExtensionPoint.Key key, boolean value)
specifier|public
name|void
name|putBoolean
parameter_list|(
name|GerritUiExtensionPoint
operator|.
name|Key
name|key
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
for|for
control|(
name|Context
name|ctx
range|:
name|contexts
control|)
block|{
name|ctx
operator|.
name|putBoolean
argument_list|(
name|key
operator|.
name|name
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|putObject (GerritUiExtensionPoint.Key key, JavaScriptObject value)
specifier|public
name|void
name|putObject
parameter_list|(
name|GerritUiExtensionPoint
operator|.
name|Key
name|key
parameter_list|,
name|JavaScriptObject
name|value
parameter_list|)
block|{
for|for
control|(
name|Context
name|ctx
range|:
name|contexts
control|)
block|{
name|ctx
operator|.
name|putObject
argument_list|(
name|key
operator|.
name|name
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onLoad ()
specifier|protected
name|void
name|onLoad
parameter_list|()
block|{
name|super
operator|.
name|onLoad
argument_list|()
expr_stmt|;
for|for
control|(
name|Context
name|ctx
range|:
name|contexts
control|)
block|{
try|try
block|{
name|ctx
operator|.
name|onLoad
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|log
argument_list|(
name|Level
operator|.
name|SEVERE
argument_list|,
literal|"Failed to load extension panel for extension point "
operator|+
name|extensionPoint
operator|.
name|name
argument_list|()
operator|+
literal|" from plugin "
operator|+
name|ctx
operator|.
name|getPluginName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onUnload ()
specifier|protected
name|void
name|onUnload
parameter_list|()
block|{
name|super
operator|.
name|onUnload
argument_list|()
expr_stmt|;
for|for
control|(
name|Context
name|ctx
range|:
name|contexts
control|)
block|{
for|for
control|(
name|JavaScriptObject
name|u
range|:
name|Natives
operator|.
name|asList
argument_list|(
name|ctx
operator|.
name|unload
argument_list|()
argument_list|)
control|)
block|{
name|ApiGlue
operator|.
name|invoke
argument_list|(
name|u
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|Definition
specifier|static
class|class
name|Definition
extends|extends
name|JavaScriptObject
block|{
DECL|field|TYPE
specifier|static
specifier|final
name|JavaScriptObject
name|TYPE
init|=
name|init
argument_list|()
decl_stmt|;
DECL|method|init ()
specifier|private
specifier|static
specifier|native
name|JavaScriptObject
name|init
parameter_list|()
comment|/*-{       function PanelDefinition(n, c) {         this.pluginName = n;         this.onLoad = c;       };       return PanelDefinition;     }-*/
function_decl|;
DECL|method|get (String i)
specifier|static
specifier|native
name|JsArray
argument_list|<
name|Definition
argument_list|>
name|get
parameter_list|(
name|String
name|i
parameter_list|)
comment|/*-{ return $wnd.Gerrit.panels[i] || [] }-*/
function_decl|;
DECL|method|Definition ()
specifier|protected
name|Definition
parameter_list|()
block|{     }
block|}
DECL|class|Context
specifier|static
class|class
name|Context
extends|extends
name|JavaScriptObject
block|{
DECL|method|create ( Definition def, SimplePanel panel)
specifier|static
specifier|final
name|Context
name|create
parameter_list|(
name|Definition
name|def
parameter_list|,
name|SimplePanel
name|panel
parameter_list|)
block|{
return|return
name|create
argument_list|(
name|TYPE
argument_list|,
name|def
argument_list|,
name|panel
operator|.
name|getElement
argument_list|()
argument_list|)
return|;
block|}
DECL|method|onLoad ()
specifier|final
specifier|native
name|void
name|onLoad
parameter_list|()
comment|/*-{ this._d.onLoad(this) }-*/
function_decl|;
DECL|method|unload ()
specifier|final
specifier|native
name|JsArray
argument_list|<
name|JavaScriptObject
argument_list|>
name|unload
parameter_list|()
comment|/*-{ return this._u }-*/
function_decl|;
DECL|method|getPluginName ()
specifier|final
specifier|native
name|String
name|getPluginName
parameter_list|()
comment|/*-{ return this._d.pluginName; }-*/
function_decl|;
DECL|method|put (String k, String v)
specifier|final
specifier|native
name|void
name|put
parameter_list|(
name|String
name|k
parameter_list|,
name|String
name|v
parameter_list|)
comment|/*-{ this.p[k] = v; }-*/
function_decl|;
DECL|method|putInt (String k, int v)
specifier|final
specifier|native
name|void
name|putInt
parameter_list|(
name|String
name|k
parameter_list|,
name|int
name|v
parameter_list|)
comment|/*-{ this.p[k] = v; }-*/
function_decl|;
DECL|method|putBoolean (String k, boolean v)
specifier|final
specifier|native
name|void
name|putBoolean
parameter_list|(
name|String
name|k
parameter_list|,
name|boolean
name|v
parameter_list|)
comment|/*-{ this.p[k] = v; }-*/
function_decl|;
DECL|method|putObject (String k, JavaScriptObject v)
specifier|final
specifier|native
name|void
name|putObject
parameter_list|(
name|String
name|k
parameter_list|,
name|JavaScriptObject
name|v
parameter_list|)
comment|/*-{ this.p[k] = v; }-*/
function_decl|;
DECL|method|create ( JavaScriptObject T, Definition d, Element e)
specifier|private
specifier|static
specifier|final
specifier|native
name|Context
name|create
parameter_list|(
name|JavaScriptObject
name|T
parameter_list|,
name|Definition
name|d
parameter_list|,
name|Element
name|e
parameter_list|)
comment|/*-{ return new T(d,e) }-*/
function_decl|;
DECL|field|TYPE
specifier|private
specifier|static
specifier|final
name|JavaScriptObject
name|TYPE
init|=
name|init
argument_list|()
decl_stmt|;
DECL|method|init ()
specifier|private
specifier|static
specifier|final
specifier|native
name|JavaScriptObject
name|init
parameter_list|()
comment|/*-{       var T = function(d,e) {         this._d = d;         this._u = [];         this.body = e;         this.p = {};       };       T.prototype = {         onUnload: function(f){this._u.push(f)},       };       return T;     }-*/
function_decl|;
DECL|method|Context ()
specifier|protected
name|Context
parameter_list|()
block|{     }
block|}
block|}
end_class

end_unit

