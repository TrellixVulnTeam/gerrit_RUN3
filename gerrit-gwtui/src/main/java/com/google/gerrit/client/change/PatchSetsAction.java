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
DECL|package|com.google.gerrit.client.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|change
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
name|Change
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
name|UIObject
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
name|Widget
import|;
end_import

begin_class
DECL|class|PatchSetsAction
class|class
name|PatchSetsAction
extends|extends
name|RightSidePopdownAction
block|{
DECL|field|revisionBox
specifier|private
specifier|final
name|PatchSetsBox
name|revisionBox
decl_stmt|;
DECL|method|PatchSetsAction ( Change.Id changeId, String revision, ChangeScreen.Style style, UIObject relativeTo, Widget downloadButton)
name|PatchSetsAction
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|String
name|revision
parameter_list|,
name|ChangeScreen
operator|.
name|Style
name|style
parameter_list|,
name|UIObject
name|relativeTo
parameter_list|,
name|Widget
name|downloadButton
parameter_list|)
block|{
name|super
argument_list|(
name|style
argument_list|,
name|relativeTo
argument_list|,
name|downloadButton
argument_list|)
expr_stmt|;
name|this
operator|.
name|revisionBox
operator|=
operator|new
name|PatchSetsBox
argument_list|(
name|changeId
argument_list|,
name|revision
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWidget ()
name|Widget
name|getWidget
parameter_list|()
block|{
return|return
name|revisionBox
return|;
block|}
block|}
end_class

end_unit

