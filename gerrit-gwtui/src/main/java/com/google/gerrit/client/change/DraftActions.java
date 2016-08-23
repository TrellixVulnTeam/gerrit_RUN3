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
name|client
operator|.
name|Gerrit
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
name|ChangeApi
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
name|rpc
operator|.
name|GerritCallback
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
name|PageLinks
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
name|user
operator|.
name|client
operator|.
name|rpc
operator|.
name|AsyncCallback
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
name|Button
import|;
end_import

begin_class
DECL|class|DraftActions
specifier|public
class|class
name|DraftActions
block|{
DECL|method|publish (Change.Id id, String revision, Button... draftButtons)
specifier|static
name|void
name|publish
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|,
name|String
name|revision
parameter_list|,
name|Button
modifier|...
name|draftButtons
parameter_list|)
block|{
name|ChangeApi
operator|.
name|publish
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|,
name|revision
argument_list|,
name|cs
argument_list|(
name|id
argument_list|,
name|draftButtons
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|delete (Change.Id id, String revision, Button... draftButtons)
specifier|static
name|void
name|delete
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|,
name|String
name|revision
parameter_list|,
name|Button
modifier|...
name|draftButtons
parameter_list|)
block|{
name|ChangeApi
operator|.
name|deleteRevision
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|,
name|revision
argument_list|,
name|cs
argument_list|(
name|id
argument_list|,
name|draftButtons
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|delete (Change.Id id, Button... draftButtons)
specifier|static
name|void
name|delete
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|,
name|Button
modifier|...
name|draftButtons
parameter_list|)
block|{
name|ChangeApi
operator|.
name|deleteChange
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|,
name|mine
argument_list|(
name|draftButtons
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|cs ( final Change.Id id, final Button... draftButtons)
specifier|public
specifier|static
name|GerritCallback
argument_list|<
name|JavaScriptObject
argument_list|>
name|cs
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|id
parameter_list|,
specifier|final
name|Button
modifier|...
name|draftButtons
parameter_list|)
block|{
name|setEnabled
argument_list|(
literal|false
argument_list|,
name|draftButtons
argument_list|)
expr_stmt|;
return|return
operator|new
name|GerritCallback
argument_list|<
name|JavaScriptObject
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|JavaScriptObject
name|result
parameter_list|)
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChange
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|err
parameter_list|)
block|{
name|setEnabled
argument_list|(
literal|true
argument_list|,
name|draftButtons
argument_list|)
expr_stmt|;
if|if
condition|(
name|SubmitFailureDialog
operator|.
name|isConflict
argument_list|(
name|err
argument_list|)
condition|)
block|{
operator|new
name|SubmitFailureDialog
argument_list|(
name|err
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|center
argument_list|()
expr_stmt|;
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChange
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|onFailure
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
DECL|method|mine ( final Button... draftButtons)
specifier|private
specifier|static
name|AsyncCallback
argument_list|<
name|JavaScriptObject
argument_list|>
name|mine
parameter_list|(
specifier|final
name|Button
modifier|...
name|draftButtons
parameter_list|)
block|{
name|setEnabled
argument_list|(
literal|false
argument_list|,
name|draftButtons
argument_list|)
expr_stmt|;
return|return
operator|new
name|GerritCallback
argument_list|<
name|JavaScriptObject
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|JavaScriptObject
name|result
parameter_list|)
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|MINE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|err
parameter_list|)
block|{
name|setEnabled
argument_list|(
literal|true
argument_list|,
name|draftButtons
argument_list|)
expr_stmt|;
if|if
condition|(
name|SubmitFailureDialog
operator|.
name|isConflict
argument_list|(
name|err
argument_list|)
condition|)
block|{
operator|new
name|SubmitFailureDialog
argument_list|(
name|err
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|center
argument_list|()
expr_stmt|;
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|MINE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|onFailure
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
DECL|method|setEnabled (boolean enabled, Button... draftButtons)
specifier|private
specifier|static
name|void
name|setEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|,
name|Button
modifier|...
name|draftButtons
parameter_list|)
block|{
if|if
condition|(
name|draftButtons
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Button
name|b
range|:
name|draftButtons
control|)
block|{
name|b
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

