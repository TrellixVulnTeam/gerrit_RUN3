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
DECL|package|com.google.gwtexpui.clippy.client
package|package
name|com
operator|.
name|google
operator|.
name|gwtexpui
operator|.
name|clippy
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
DECL|interface|CopyableLabelText
interface|interface
name|CopyableLabelText
extends|extends
name|Constants
block|{
DECL|field|I
name|CopyableLabelText
name|I
init|=
name|GWT
operator|.
name|create
argument_list|(
name|CopyableLabelText
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|tooltip ()
name|String
name|tooltip
parameter_list|()
function_decl|;
DECL|method|copied ()
name|String
name|copied
parameter_list|()
function_decl|;
DECL|method|failed ()
name|String
name|failed
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

