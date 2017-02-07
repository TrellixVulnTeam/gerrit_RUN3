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
DECL|package|com.google.gerrit.client.dashboards
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|dashboards
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
name|JavaScriptObject
import|;
end_import

begin_class
DECL|class|DashboardInfo
specifier|public
class|class
name|DashboardInfo
extends|extends
name|JavaScriptObject
block|{
DECL|method|id ()
specifier|public
specifier|final
specifier|native
name|String
name|id
parameter_list|()
comment|/*-{ return this.id; }-*/
function_decl|;
DECL|method|title ()
specifier|public
specifier|final
specifier|native
name|String
name|title
parameter_list|()
comment|/*-{ return this.title; }-*/
function_decl|;
DECL|method|project ()
specifier|public
specifier|final
specifier|native
name|String
name|project
parameter_list|()
comment|/*-{ return this.project; }-*/
function_decl|;
DECL|method|definingProject ()
specifier|public
specifier|final
specifier|native
name|String
name|definingProject
parameter_list|()
comment|/*-{ return this.defining_project; }-*/
function_decl|;
DECL|method|ref ()
specifier|public
specifier|final
specifier|native
name|String
name|ref
parameter_list|()
comment|/*-{ return this.ref; }-*/
function_decl|;
DECL|method|path ()
specifier|public
specifier|final
specifier|native
name|String
name|path
parameter_list|()
comment|/*-{ return this.path; }-*/
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
DECL|method|foreach ()
specifier|public
specifier|final
specifier|native
name|String
name|foreach
parameter_list|()
comment|/*-{ return this.foreach; }-*/
function_decl|;
DECL|method|url ()
specifier|public
specifier|final
specifier|native
name|String
name|url
parameter_list|()
comment|/*-{ return this.url; }-*/
function_decl|;
DECL|method|isDefault ()
specifier|public
specifier|final
specifier|native
name|boolean
name|isDefault
parameter_list|()
comment|/*-{ return this['default'] ? true : false; }-*/
function_decl|;
DECL|method|DashboardInfo ()
specifier|protected
name|DashboardInfo
parameter_list|()
block|{}
block|}
end_class

end_unit

