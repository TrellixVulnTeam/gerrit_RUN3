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
DECL|package|com.google.gerrit.client.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|config
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
DECL|class|ServerInfo
specifier|public
class|class
name|ServerInfo
extends|extends
name|JavaScriptObject
block|{
DECL|method|auth ()
specifier|public
specifier|final
specifier|native
name|AuthInfo
name|auth
parameter_list|()
comment|/*-{ return this.auth; }-*/
function_decl|;
DECL|method|contactStore ()
specifier|public
specifier|final
specifier|native
name|ContactStoreInfo
name|contactStore
parameter_list|()
comment|/*-{ return this.contact_store; }-*/
function_decl|;
DECL|method|gerrit ()
specifier|public
specifier|final
specifier|native
name|GerritInfo
name|gerrit
parameter_list|()
comment|/*-{ return this.gerrit; }-*/
function_decl|;
DECL|method|hasContactStore ()
specifier|public
specifier|final
name|boolean
name|hasContactStore
parameter_list|()
block|{
return|return
name|contactStore
argument_list|()
operator|!=
literal|null
return|;
block|}
DECL|method|ServerInfo ()
specifier|protected
name|ServerInfo
parameter_list|()
block|{   }
DECL|class|ContactStoreInfo
specifier|public
specifier|static
class|class
name|ContactStoreInfo
extends|extends
name|JavaScriptObject
block|{
DECL|method|url ()
specifier|public
specifier|final
specifier|native
name|String
name|url
parameter_list|()
comment|/*-{ return this.url; }-*/
function_decl|;
DECL|method|ContactStoreInfo ()
specifier|protected
name|ContactStoreInfo
parameter_list|()
block|{     }
block|}
block|}
end_class

end_unit

