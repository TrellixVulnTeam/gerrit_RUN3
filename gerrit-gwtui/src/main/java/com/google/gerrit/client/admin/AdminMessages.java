begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.client.admin
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|admin
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
name|i18n
operator|.
name|client
operator|.
name|Messages
import|;
end_import

begin_interface
DECL|interface|AdminMessages
specifier|public
interface|interface
name|AdminMessages
extends|extends
name|Messages
block|{
DECL|method|group (String name)
name|String
name|group
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|label (String name)
name|String
name|label
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|labelAs (String name)
name|String
name|labelAs
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|project (String name)
name|String
name|project
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|deletedGroup (int id)
name|String
name|deletedGroup
parameter_list|(
name|int
name|id
parameter_list|)
function_decl|;
DECL|method|deletedReference (String name)
name|String
name|deletedReference
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|deletedSection (String name)
name|String
name|deletedSection
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|effectiveMaxObjectSizeLimit (String effectiveMaxObjectSizeLimit)
name|String
name|effectiveMaxObjectSizeLimit
parameter_list|(
name|String
name|effectiveMaxObjectSizeLimit
parameter_list|)
function_decl|;
DECL|method|globalMaxObjectSizeLimit (String globalMaxObjectSizeLimit)
name|String
name|globalMaxObjectSizeLimit
parameter_list|(
name|String
name|globalMaxObjectSizeLimit
parameter_list|)
function_decl|;
DECL|method|pluginProjectOptionsTitle (String pluginName)
name|String
name|pluginProjectOptionsTitle
parameter_list|(
name|String
name|pluginName
parameter_list|)
function_decl|;
DECL|method|pluginProjectInheritedValue (String value)
name|String
name|pluginProjectInheritedValue
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
DECL|method|pluginProjectInheritedListValue (String value)
name|String
name|pluginProjectInheritedListValue
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

