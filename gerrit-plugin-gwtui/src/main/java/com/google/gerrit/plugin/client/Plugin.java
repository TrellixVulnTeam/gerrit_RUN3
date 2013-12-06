begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
name|gwt
operator|.
name|core
operator|.
name|client
operator|.
name|EntryPoint
import|;
end_import

begin_comment
comment|/**  * Base class for writing Gerrit Web UI plugins  *  * Writing a plugin:  *<ol>  *<li>Declare subtype of Plugin</li>  *<li>Bind WebUiPlugin to GwtPlugin implementation in Gerrit-Module</li>  *</ol>  */
end_comment

begin_class
DECL|class|Plugin
specifier|public
specifier|abstract
class|class
name|Plugin
implements|implements
name|EntryPoint
block|{
DECL|method|go (String t)
specifier|public
specifier|native
specifier|static
name|void
name|go
parameter_list|(
name|String
name|t
parameter_list|)
comment|/*-{ $wnd.Gerrit.go(t) }-*/
function_decl|;
DECL|method|refresh ()
specifier|public
specifier|native
specifier|static
name|void
name|refresh
parameter_list|()
comment|/*-{ $wnd.Gerrit.refresh() }-*/
function_decl|;
block|}
end_class

end_unit

