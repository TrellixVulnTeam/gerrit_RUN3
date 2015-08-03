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
DECL|package|com.google.gerrit.client.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|account
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

begin_class
DECL|class|GpgKeyInfo
specifier|public
class|class
name|GpgKeyInfo
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
DECL|method|fingerprint ()
specifier|public
specifier|final
specifier|native
name|String
name|fingerprint
parameter_list|()
comment|/*-{ return this.fingerprint; }-*/
function_decl|;
DECL|method|userIds ()
specifier|public
specifier|final
specifier|native
name|JsArrayString
name|userIds
parameter_list|()
comment|/*-{ return this.user_ids; }-*/
function_decl|;
DECL|method|key ()
specifier|public
specifier|final
specifier|native
name|String
name|key
parameter_list|()
comment|/*-{ return this.key; }-*/
function_decl|;
DECL|method|GpgKeyInfo ()
specifier|protected
name|GpgKeyInfo
parameter_list|()
block|{   }
block|}
end_class

end_unit

