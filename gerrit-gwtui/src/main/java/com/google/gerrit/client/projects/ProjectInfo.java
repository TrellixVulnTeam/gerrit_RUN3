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
DECL|package|com.google.gerrit.client.projects
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|projects
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
name|WebLinkInfo
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
name|extensions
operator|.
name|client
operator|.
name|ProjectState
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
name|reviewdb
operator|.
name|client
operator|.
name|Project
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
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|SuggestOracle
import|;
end_import

begin_class
DECL|class|ProjectInfo
specifier|public
class|class
name|ProjectInfo
extends|extends
name|JavaScriptObject
implements|implements
name|SuggestOracle
operator|.
name|Suggestion
block|{
DECL|method|name_key ()
specifier|public
specifier|final
name|Project
operator|.
name|NameKey
name|name_key
parameter_list|()
block|{
return|return
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|name ()
specifier|public
specifier|final
specifier|native
name|String
name|name
parameter_list|()
comment|/*-{ return this.name; }-*/
function_decl|;
DECL|method|description ()
specifier|public
specifier|final
specifier|native
name|String
name|description
parameter_list|()
comment|/*-{ return this.description; }-*/
function_decl|;
DECL|method|webLinks ()
specifier|public
specifier|final
specifier|native
name|JsArray
argument_list|<
name|WebLinkInfo
argument_list|>
name|webLinks
parameter_list|()
comment|/*-{ return this.web_links; }-*/
function_decl|;
DECL|method|state ()
specifier|public
specifier|final
name|ProjectState
name|state
parameter_list|()
block|{
return|return
name|ProjectState
operator|.
name|valueOf
argument_list|(
name|getStringState
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getStringState ()
specifier|private
specifier|final
specifier|native
name|String
name|getStringState
parameter_list|()
comment|/*-{ return this.state; }-*/
function_decl|;
annotation|@
name|Override
DECL|method|getDisplayString ()
specifier|public
specifier|final
name|String
name|getDisplayString
parameter_list|()
block|{
if|if
condition|(
name|description
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|name
argument_list|()
operator|+
literal|" ("
operator|+
name|description
argument_list|()
operator|+
literal|")"
return|;
block|}
return|return
name|name
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getReplacementString ()
specifier|public
specifier|final
name|String
name|getReplacementString
parameter_list|()
block|{
return|return
name|name
argument_list|()
return|;
block|}
DECL|method|ProjectInfo ()
specifier|protected
name|ProjectInfo
parameter_list|()
block|{   }
block|}
end_class

end_unit

