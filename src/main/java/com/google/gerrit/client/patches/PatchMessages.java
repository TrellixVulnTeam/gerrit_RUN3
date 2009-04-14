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
DECL|package|com.google.gerrit.client.patches
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|patches
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_interface
DECL|interface|PatchMessages
specifier|public
interface|interface
name|PatchMessages
extends|extends
name|Messages
block|{
DECL|method|patchWindowTitle (int id, String file)
name|String
name|patchWindowTitle
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|file
parameter_list|)
function_decl|;
DECL|method|patchPageTitle (int id, String path)
name|String
name|patchPageTitle
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|path
parameter_list|)
function_decl|;
DECL|method|patchHeaderAncestor (int id)
name|String
name|patchHeaderAncestor
parameter_list|(
name|int
name|id
parameter_list|)
function_decl|;
DECL|method|patchSkipRegion (@luralCount int lineCnt)
name|String
name|patchSkipRegion
parameter_list|(
annotation|@
name|PluralCount
name|int
name|lineCnt
parameter_list|)
function_decl|;
DECL|method|draftSaved (Date when)
name|String
name|draftSaved
parameter_list|(
name|Date
name|when
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

