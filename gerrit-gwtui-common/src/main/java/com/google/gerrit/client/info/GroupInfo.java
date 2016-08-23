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
DECL|package|com.google.gerrit.client.info
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|info
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
name|reviewdb
operator|.
name|client
operator|.
name|AccountGroup
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
name|http
operator|.
name|client
operator|.
name|URL
import|;
end_import

begin_class
DECL|class|GroupInfo
specifier|public
class|class
name|GroupInfo
extends|extends
name|GroupBaseInfo
block|{
DECL|method|getGroupId ()
specifier|public
specifier|final
name|AccountGroup
operator|.
name|Id
name|getGroupId
parameter_list|()
block|{
return|return
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
name|group_id
argument_list|()
argument_list|)
return|;
block|}
DECL|method|options ()
specifier|public
specifier|final
specifier|native
name|GroupOptionsInfo
name|options
parameter_list|()
comment|/*-{ return this.options; }-*/
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
DECL|method|url ()
specifier|public
specifier|final
specifier|native
name|String
name|url
parameter_list|()
comment|/*-{ return this.url; }-*/
function_decl|;
DECL|method|owner ()
specifier|public
specifier|final
specifier|native
name|String
name|owner
parameter_list|()
comment|/*-{ return this.owner; }-*/
function_decl|;
DECL|method|owner (String o)
specifier|public
specifier|final
specifier|native
name|void
name|owner
parameter_list|(
name|String
name|o
parameter_list|)
comment|/*-{ if(o)this.owner=o; }-*/
function_decl|;
DECL|method|members ()
specifier|public
specifier|final
specifier|native
name|JsArray
argument_list|<
name|AccountInfo
argument_list|>
name|members
parameter_list|()
comment|/*-{ return this.members; }-*/
function_decl|;
DECL|method|includes ()
specifier|public
specifier|final
specifier|native
name|JsArray
argument_list|<
name|GroupInfo
argument_list|>
name|includes
parameter_list|()
comment|/*-{ return this.includes; }-*/
function_decl|;
DECL|method|group_id ()
specifier|private
specifier|native
name|int
name|group_id
parameter_list|()
comment|/*-{ return this.group_id; }-*/
function_decl|;
DECL|method|owner_id ()
specifier|private
specifier|native
name|String
name|owner_id
parameter_list|()
comment|/*-{ return this.owner_id; }-*/
function_decl|;
DECL|method|owner_id (String o)
specifier|private
specifier|native
name|void
name|owner_id
parameter_list|(
name|String
name|o
parameter_list|)
comment|/*-{ if(o)this.owner_id=o; }-*/
function_decl|;
DECL|method|getOwnerUUID ()
specifier|public
specifier|final
name|AccountGroup
operator|.
name|UUID
name|getOwnerUUID
parameter_list|()
block|{
name|String
name|owner
init|=
name|owner_id
argument_list|()
decl_stmt|;
if|if
condition|(
name|owner
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|URL
operator|.
name|decodeQueryString
argument_list|(
name|owner
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|setOwnerUUID (AccountGroup.UUID uuid)
specifier|public
specifier|final
name|void
name|setOwnerUUID
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
name|owner_id
argument_list|(
name|URL
operator|.
name|encodeQueryString
argument_list|(
name|uuid
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|GroupInfo ()
specifier|protected
name|GroupInfo
parameter_list|()
block|{   }
DECL|class|GroupOptionsInfo
specifier|public
specifier|static
class|class
name|GroupOptionsInfo
extends|extends
name|JavaScriptObject
block|{
DECL|method|isVisibleToAll ()
specifier|public
specifier|final
specifier|native
name|boolean
name|isVisibleToAll
parameter_list|()
comment|/*-{ return this['visible_to_all'] ? true : false; }-*/
function_decl|;
DECL|method|GroupOptionsInfo ()
specifier|protected
name|GroupOptionsInfo
parameter_list|()
block|{     }
block|}
block|}
end_class

end_unit

