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
DECL|package|com.google.gwtexpui.globalkey.client
package|package
name|com
operator|.
name|google
operator|.
name|gwtexpui
operator|.
name|globalkey
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
name|i18n
operator|.
name|client
operator|.
name|Constants
import|;
end_import

begin_interface
DECL|interface|KeyConstants
specifier|public
interface|interface
name|KeyConstants
extends|extends
name|Constants
block|{
DECL|field|I
specifier|public
specifier|static
specifier|final
name|KeyConstants
name|I
init|=
name|GWT
operator|.
name|create
argument_list|(
name|KeyConstants
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|applicationSection ()
name|String
name|applicationSection
parameter_list|()
function_decl|;
DECL|method|showHelp ()
name|String
name|showHelp
parameter_list|()
function_decl|;
DECL|method|closeCurrentDialog ()
name|String
name|closeCurrentDialog
parameter_list|()
function_decl|;
DECL|method|keyboardShortcuts ()
name|String
name|keyboardShortcuts
parameter_list|()
function_decl|;
DECL|method|closeButton ()
name|String
name|closeButton
parameter_list|()
function_decl|;
DECL|method|orOtherKey ()
name|String
name|orOtherKey
parameter_list|()
function_decl|;
DECL|method|thenOtherKey ()
name|String
name|thenOtherKey
parameter_list|()
function_decl|;
DECL|method|keyCtrl ()
name|String
name|keyCtrl
parameter_list|()
function_decl|;
DECL|method|keyAlt ()
name|String
name|keyAlt
parameter_list|()
function_decl|;
DECL|method|keyMeta ()
name|String
name|keyMeta
parameter_list|()
function_decl|;
DECL|method|keyShift ()
name|String
name|keyShift
parameter_list|()
function_decl|;
DECL|method|keyEnter ()
name|String
name|keyEnter
parameter_list|()
function_decl|;
DECL|method|keyEsc ()
name|String
name|keyEsc
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

