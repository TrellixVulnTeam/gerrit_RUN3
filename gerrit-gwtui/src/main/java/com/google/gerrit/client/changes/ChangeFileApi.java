begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.client.changes
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|changes
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
name|VoidResult
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
name|NativeString
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
name|RestApi
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
name|gerrit
operator|.
name|reviewdb
operator|.
name|client
operator|.
name|PatchSet
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

begin_comment
comment|/**  * A collection of static methods which work on the Gerrit REST API for specific  * files in a change.  */
end_comment

begin_class
DECL|class|ChangeFileApi
specifier|public
class|class
name|ChangeFileApi
block|{
DECL|class|CallbackWrapper
specifier|static
specifier|abstract
class|class
name|CallbackWrapper
parameter_list|<
name|I
parameter_list|,
name|O
parameter_list|>
implements|implements
name|AsyncCallback
argument_list|<
name|I
argument_list|>
block|{
DECL|field|wrapped
specifier|protected
name|AsyncCallback
argument_list|<
name|O
argument_list|>
name|wrapped
decl_stmt|;
DECL|method|CallbackWrapper (AsyncCallback<O> callback)
specifier|public
name|CallbackWrapper
parameter_list|(
name|AsyncCallback
argument_list|<
name|O
argument_list|>
name|callback
parameter_list|)
block|{
name|wrapped
operator|=
name|callback
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onSuccess (I result)
specifier|public
specifier|abstract
name|void
name|onSuccess
parameter_list|(
name|I
name|result
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|onFailure (Throwable caught)
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|caught
parameter_list|)
block|{
name|wrapped
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Get the contents of a File in a PatchSet or cange edit. */
DECL|method|getContent (PatchSet.Id id, String filename, AsyncCallback<String> cb)
specifier|public
specifier|static
name|void
name|getContent
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|,
name|String
name|filename
parameter_list|,
name|AsyncCallback
argument_list|<
name|String
argument_list|>
name|cb
parameter_list|)
block|{
name|contentEditOrPs
argument_list|(
name|id
argument_list|,
name|filename
argument_list|)
operator|.
name|get
argument_list|(
operator|new
name|CallbackWrapper
argument_list|<
name|NativeString
argument_list|,
name|String
argument_list|>
argument_list|(
name|cb
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|NativeString
name|b64
parameter_list|)
block|{
name|wrapped
operator|.
name|onSuccess
argument_list|(
name|b64decode
argument_list|(
name|b64
operator|.
name|asString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Put contents into a File in a change edit. */
DECL|method|putContent (PatchSet.Id id, String filename, String content, AsyncCallback<VoidResult> result)
specifier|public
specifier|static
name|void
name|putContent
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|,
name|String
name|filename
parameter_list|,
name|String
name|content
parameter_list|,
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|result
parameter_list|)
block|{
name|contentEdit
argument_list|(
name|id
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|filename
argument_list|)
operator|.
name|put
argument_list|(
name|content
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|/** Restore contents of a File in a change edit. */
DECL|method|restoreContent (PatchSet.Id id, String filename, AsyncCallback<VoidResult> result)
specifier|public
specifier|static
name|void
name|restoreContent
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|,
name|String
name|filename
parameter_list|,
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|result
parameter_list|)
block|{
name|Input
name|in
init|=
name|Input
operator|.
name|create
argument_list|()
decl_stmt|;
name|in
operator|.
name|path
argument_list|(
name|filename
argument_list|)
expr_stmt|;
name|in
operator|.
name|restore
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ChangeApi
operator|.
name|edit
argument_list|(
name|id
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|post
argument_list|(
name|in
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|/** Delete a file from a change edit. */
DECL|method|deleteContent (PatchSet.Id id, String filename, AsyncCallback<VoidResult> result)
specifier|public
specifier|static
name|void
name|deleteContent
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|,
name|String
name|filename
parameter_list|,
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
name|result
parameter_list|)
block|{
name|contentEdit
argument_list|(
name|id
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|filename
argument_list|)
operator|.
name|delete
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|contentEditOrPs (PatchSet.Id id, String filename)
specifier|private
specifier|static
name|RestApi
name|contentEditOrPs
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|,
name|String
name|filename
parameter_list|)
block|{
return|return
name|id
operator|.
name|get
argument_list|()
operator|==
literal|0
condition|?
name|contentEdit
argument_list|(
name|id
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|filename
argument_list|)
else|:
name|ChangeApi
operator|.
name|revision
argument_list|(
name|id
argument_list|)
operator|.
name|view
argument_list|(
literal|"files"
argument_list|)
operator|.
name|id
argument_list|(
name|filename
argument_list|)
operator|.
name|view
argument_list|(
literal|"content"
argument_list|)
return|;
block|}
DECL|method|contentEdit (Change.Id id, String filename)
specifier|private
specifier|static
name|RestApi
name|contentEdit
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|,
name|String
name|filename
parameter_list|)
block|{
return|return
name|ChangeApi
operator|.
name|edit
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|id
argument_list|(
name|filename
argument_list|)
return|;
block|}
DECL|method|b64decode (String a)
specifier|private
specifier|static
specifier|native
name|String
name|b64decode
parameter_list|(
name|String
name|a
parameter_list|)
comment|/*-{ return window.atob(a); }-*/
function_decl|;
DECL|class|Input
specifier|private
specifier|static
class|class
name|Input
extends|extends
name|JavaScriptObject
block|{
DECL|method|path (String p)
specifier|final
specifier|native
name|void
name|path
parameter_list|(
name|String
name|p
parameter_list|)
comment|/*-{ if(p)this.path=p; }-*/
function_decl|;
DECL|method|restore (boolean r)
specifier|final
specifier|native
name|void
name|restore
parameter_list|(
name|boolean
name|r
parameter_list|)
comment|/*-{ if(r)this.restore=r; }-*/
function_decl|;
DECL|method|create ()
specifier|static
name|Input
name|create
parameter_list|()
block|{
return|return
operator|(
name|Input
operator|)
name|createObject
argument_list|()
return|;
block|}
DECL|method|Input ()
specifier|protected
name|Input
parameter_list|()
block|{     }
block|}
block|}
end_class

end_unit

