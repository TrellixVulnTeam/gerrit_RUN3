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
DECL|package|com.google.gerrit.client.ui
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|ui
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
name|Dispatcher
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
name|client
operator|.
name|changes
operator|.
name|PatchTable
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
name|client
operator|.
name|patches
operator|.
name|PatchScreen
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
name|common
operator|.
name|data
operator|.
name|PatchSetDetail
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
name|Patch
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
name|PatchSet
import|;
end_import

begin_class
DECL|class|PatchLink
specifier|public
class|class
name|PatchLink
extends|extends
name|InlineHyperlink
block|{
DECL|field|base
specifier|protected
name|PatchSet
operator|.
name|Id
name|base
decl_stmt|;
DECL|field|patchKey
specifier|protected
name|Patch
operator|.
name|Key
name|patchKey
decl_stmt|;
DECL|field|patchIndex
specifier|protected
name|int
name|patchIndex
decl_stmt|;
DECL|field|patchSetDetail
specifier|protected
name|PatchSetDetail
name|patchSetDetail
decl_stmt|;
DECL|field|parentPatchTable
specifier|protected
name|PatchTable
name|parentPatchTable
decl_stmt|;
DECL|field|topView
specifier|protected
name|PatchScreen
operator|.
name|TopView
name|topView
decl_stmt|;
comment|/**    * @param text The text of this link    * @param base optional base to compare against.    * @param patchKey The key for this patch    * @param patchIndex The index of the current patch in the patch set    * @param historyToken The history token    * @param patchSetDetail Detailed information about the patch set.    * @param parentPatchTable The table used to display this link    */
DECL|method|PatchLink (String text, PatchSet.Id base, Patch.Key patchKey, int patchIndex, String historyToken, PatchSetDetail patchSetDetail, PatchTable parentPatchTable, PatchScreen.TopView topView)
specifier|protected
name|PatchLink
parameter_list|(
name|String
name|text
parameter_list|,
name|PatchSet
operator|.
name|Id
name|base
parameter_list|,
name|Patch
operator|.
name|Key
name|patchKey
parameter_list|,
name|int
name|patchIndex
parameter_list|,
name|String
name|historyToken
parameter_list|,
name|PatchSetDetail
name|patchSetDetail
parameter_list|,
name|PatchTable
name|parentPatchTable
parameter_list|,
name|PatchScreen
operator|.
name|TopView
name|topView
parameter_list|)
block|{
name|super
argument_list|(
name|text
argument_list|,
name|historyToken
argument_list|)
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|this
operator|.
name|patchKey
operator|=
name|patchKey
expr_stmt|;
name|this
operator|.
name|patchIndex
operator|=
name|patchIndex
expr_stmt|;
name|this
operator|.
name|patchSetDetail
operator|=
name|patchSetDetail
expr_stmt|;
name|this
operator|.
name|parentPatchTable
operator|=
name|parentPatchTable
expr_stmt|;
name|this
operator|.
name|parentPatchTable
operator|=
name|parentPatchTable
expr_stmt|;
name|this
operator|.
name|topView
operator|=
name|topView
expr_stmt|;
block|}
comment|/**    * @param text The text of this link    * @param type The type of the link to create (unified/side-by-side)    * @param patchScreen The patchScreen to grab contents to link to from    */
DECL|method|PatchLink (String text, PatchScreen.Type type, PatchScreen patchScreen)
specifier|public
name|PatchLink
parameter_list|(
name|String
name|text
parameter_list|,
name|PatchScreen
operator|.
name|Type
name|type
parameter_list|,
name|PatchScreen
name|patchScreen
parameter_list|)
block|{
name|this
argument_list|(
name|text
argument_list|,
comment|//
name|patchScreen
operator|.
name|getSideA
argument_list|()
argument_list|,
comment|//
name|patchScreen
operator|.
name|getPatchKey
argument_list|()
argument_list|,
comment|//
name|patchScreen
operator|.
name|getPatchIndex
argument_list|()
argument_list|,
comment|//
name|Dispatcher
operator|.
name|toPatch
argument_list|(
name|type
argument_list|,
name|patchScreen
operator|.
name|getPatchKey
argument_list|()
argument_list|)
argument_list|,
comment|//
name|patchScreen
operator|.
name|getPatchSetDetail
argument_list|()
argument_list|,
comment|//
name|patchScreen
operator|.
name|getFileList
argument_list|()
argument_list|,
comment|//
name|patchScreen
operator|.
name|getTopView
argument_list|()
comment|//
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|go ()
specifier|public
name|void
name|go
parameter_list|()
block|{
name|Dispatcher
operator|.
name|patch
argument_list|(
comment|//
name|getTargetHistoryToken
argument_list|()
argument_list|,
comment|//
name|base
argument_list|,
comment|//
name|patchKey
argument_list|,
comment|//
name|patchIndex
argument_list|,
comment|//
name|patchSetDetail
argument_list|,
comment|//
name|parentPatchTable
argument_list|,
name|topView
comment|//
argument_list|)
expr_stmt|;
block|}
DECL|class|SideBySide
specifier|public
specifier|static
class|class
name|SideBySide
extends|extends
name|PatchLink
block|{
DECL|method|SideBySide (String text, PatchSet.Id base, Patch.Key patchKey, int patchIndex, PatchSetDetail patchSetDetail, PatchTable parentPatchTable)
specifier|public
name|SideBySide
parameter_list|(
name|String
name|text
parameter_list|,
name|PatchSet
operator|.
name|Id
name|base
parameter_list|,
name|Patch
operator|.
name|Key
name|patchKey
parameter_list|,
name|int
name|patchIndex
parameter_list|,
name|PatchSetDetail
name|patchSetDetail
parameter_list|,
name|PatchTable
name|parentPatchTable
parameter_list|)
block|{
name|super
argument_list|(
name|text
argument_list|,
name|base
argument_list|,
name|patchKey
argument_list|,
name|patchIndex
argument_list|,
name|Dispatcher
operator|.
name|toPatchSideBySide
argument_list|(
name|base
argument_list|,
name|patchKey
argument_list|)
argument_list|,
name|patchSetDetail
argument_list|,
name|parentPatchTable
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Unified
specifier|public
specifier|static
class|class
name|Unified
extends|extends
name|PatchLink
block|{
DECL|method|Unified (String text, PatchSet.Id base, final Patch.Key patchKey, int patchIndex, PatchSetDetail patchSetDetail, PatchTable parentPatchTable)
specifier|public
name|Unified
parameter_list|(
name|String
name|text
parameter_list|,
name|PatchSet
operator|.
name|Id
name|base
parameter_list|,
specifier|final
name|Patch
operator|.
name|Key
name|patchKey
parameter_list|,
name|int
name|patchIndex
parameter_list|,
name|PatchSetDetail
name|patchSetDetail
parameter_list|,
name|PatchTable
name|parentPatchTable
parameter_list|)
block|{
name|super
argument_list|(
name|text
argument_list|,
name|base
argument_list|,
name|patchKey
argument_list|,
name|patchIndex
argument_list|,
name|Dispatcher
operator|.
name|toPatchUnified
argument_list|(
name|base
argument_list|,
name|patchKey
argument_list|)
argument_list|,
name|patchSetDetail
argument_list|,
name|parentPatchTable
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

