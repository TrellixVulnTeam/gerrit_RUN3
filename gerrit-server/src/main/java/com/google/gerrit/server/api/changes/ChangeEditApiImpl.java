begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.api.changes
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|api
operator|.
name|changes
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|api
operator|.
name|ApiUtil
operator|.
name|asRestApiException
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
name|extensions
operator|.
name|api
operator|.
name|changes
operator|.
name|ChangeEditApi
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
name|extensions
operator|.
name|api
operator|.
name|changes
operator|.
name|PublishChangeEditInput
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
name|extensions
operator|.
name|common
operator|.
name|EditInfo
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
name|extensions
operator|.
name|common
operator|.
name|Input
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
name|extensions
operator|.
name|restapi
operator|.
name|AuthException
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
name|extensions
operator|.
name|restapi
operator|.
name|BinaryResult
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
name|extensions
operator|.
name|restapi
operator|.
name|IdString
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
name|extensions
operator|.
name|restapi
operator|.
name|RawInput
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
name|extensions
operator|.
name|restapi
operator|.
name|ResourceNotFoundException
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
name|extensions
operator|.
name|restapi
operator|.
name|Response
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
name|extensions
operator|.
name|restapi
operator|.
name|RestApiException
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
name|server
operator|.
name|change
operator|.
name|ChangeEditResource
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
name|server
operator|.
name|change
operator|.
name|ChangeEdits
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
name|server
operator|.
name|change
operator|.
name|ChangeResource
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
name|server
operator|.
name|change
operator|.
name|DeleteChangeEdit
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
name|server
operator|.
name|change
operator|.
name|PublishChangeEdit
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
name|server
operator|.
name|change
operator|.
name|RebaseChangeEdit
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|server
operator|.
name|OrmException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_class
DECL|class|ChangeEditApiImpl
specifier|public
class|class
name|ChangeEditApiImpl
implements|implements
name|ChangeEditApi
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (ChangeResource changeResource)
name|ChangeEditApiImpl
name|create
parameter_list|(
name|ChangeResource
name|changeResource
parameter_list|)
function_decl|;
block|}
DECL|field|editDetail
specifier|private
specifier|final
name|ChangeEdits
operator|.
name|Detail
name|editDetail
decl_stmt|;
DECL|field|changeEditsPost
specifier|private
specifier|final
name|ChangeEdits
operator|.
name|Post
name|changeEditsPost
decl_stmt|;
DECL|field|deleteChangeEdit
specifier|private
specifier|final
name|DeleteChangeEdit
name|deleteChangeEdit
decl_stmt|;
DECL|field|rebaseChangeEdit
specifier|private
specifier|final
name|RebaseChangeEdit
operator|.
name|Rebase
name|rebaseChangeEdit
decl_stmt|;
DECL|field|publishChangeEdit
specifier|private
specifier|final
name|PublishChangeEdit
operator|.
name|Publish
name|publishChangeEdit
decl_stmt|;
DECL|field|changeEditsGet
specifier|private
specifier|final
name|ChangeEdits
operator|.
name|Get
name|changeEditsGet
decl_stmt|;
DECL|field|changeEditsPut
specifier|private
specifier|final
name|ChangeEdits
operator|.
name|Put
name|changeEditsPut
decl_stmt|;
DECL|field|changeEditDeleteContent
specifier|private
specifier|final
name|ChangeEdits
operator|.
name|DeleteContent
name|changeEditDeleteContent
decl_stmt|;
DECL|field|getChangeEditCommitMessage
specifier|private
specifier|final
name|ChangeEdits
operator|.
name|GetMessage
name|getChangeEditCommitMessage
decl_stmt|;
DECL|field|modifyChangeEditCommitMessage
specifier|private
specifier|final
name|ChangeEdits
operator|.
name|EditMessage
name|modifyChangeEditCommitMessage
decl_stmt|;
DECL|field|changeEdits
specifier|private
specifier|final
name|ChangeEdits
name|changeEdits
decl_stmt|;
DECL|field|changeResource
specifier|private
specifier|final
name|ChangeResource
name|changeResource
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeEditApiImpl ( ChangeEdits.Detail editDetail, ChangeEdits.Post changeEditsPost, DeleteChangeEdit deleteChangeEdit, RebaseChangeEdit.Rebase rebaseChangeEdit, PublishChangeEdit.Publish publishChangeEdit, ChangeEdits.Get changeEditsGet, ChangeEdits.Put changeEditsPut, ChangeEdits.DeleteContent changeEditDeleteContent, ChangeEdits.GetMessage getChangeEditCommitMessage, ChangeEdits.EditMessage modifyChangeEditCommitMessage, ChangeEdits changeEdits, @Assisted ChangeResource changeResource)
specifier|public
name|ChangeEditApiImpl
parameter_list|(
name|ChangeEdits
operator|.
name|Detail
name|editDetail
parameter_list|,
name|ChangeEdits
operator|.
name|Post
name|changeEditsPost
parameter_list|,
name|DeleteChangeEdit
name|deleteChangeEdit
parameter_list|,
name|RebaseChangeEdit
operator|.
name|Rebase
name|rebaseChangeEdit
parameter_list|,
name|PublishChangeEdit
operator|.
name|Publish
name|publishChangeEdit
parameter_list|,
name|ChangeEdits
operator|.
name|Get
name|changeEditsGet
parameter_list|,
name|ChangeEdits
operator|.
name|Put
name|changeEditsPut
parameter_list|,
name|ChangeEdits
operator|.
name|DeleteContent
name|changeEditDeleteContent
parameter_list|,
name|ChangeEdits
operator|.
name|GetMessage
name|getChangeEditCommitMessage
parameter_list|,
name|ChangeEdits
operator|.
name|EditMessage
name|modifyChangeEditCommitMessage
parameter_list|,
name|ChangeEdits
name|changeEdits
parameter_list|,
annotation|@
name|Assisted
name|ChangeResource
name|changeResource
parameter_list|)
block|{
name|this
operator|.
name|editDetail
operator|=
name|editDetail
expr_stmt|;
name|this
operator|.
name|changeEditsPost
operator|=
name|changeEditsPost
expr_stmt|;
name|this
operator|.
name|deleteChangeEdit
operator|=
name|deleteChangeEdit
expr_stmt|;
name|this
operator|.
name|rebaseChangeEdit
operator|=
name|rebaseChangeEdit
expr_stmt|;
name|this
operator|.
name|publishChangeEdit
operator|=
name|publishChangeEdit
expr_stmt|;
name|this
operator|.
name|changeEditsGet
operator|=
name|changeEditsGet
expr_stmt|;
name|this
operator|.
name|changeEditsPut
operator|=
name|changeEditsPut
expr_stmt|;
name|this
operator|.
name|changeEditDeleteContent
operator|=
name|changeEditDeleteContent
expr_stmt|;
name|this
operator|.
name|getChangeEditCommitMessage
operator|=
name|getChangeEditCommitMessage
expr_stmt|;
name|this
operator|.
name|modifyChangeEditCommitMessage
operator|=
name|modifyChangeEditCommitMessage
expr_stmt|;
name|this
operator|.
name|changeEdits
operator|=
name|changeEdits
expr_stmt|;
name|this
operator|.
name|changeResource
operator|=
name|changeResource
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|Optional
argument_list|<
name|EditInfo
argument_list|>
name|get
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
name|Response
argument_list|<
name|EditInfo
argument_list|>
name|edit
init|=
name|editDetail
operator|.
name|apply
argument_list|(
name|changeResource
argument_list|)
decl_stmt|;
return|return
name|edit
operator|.
name|isNone
argument_list|()
condition|?
name|Optional
operator|.
name|empty
argument_list|()
else|:
name|Optional
operator|.
name|of
argument_list|(
name|edit
operator|.
name|value
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot retrieve change edit"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|create ()
specifier|public
name|void
name|create
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
name|changeEditsPost
operator|.
name|apply
argument_list|(
name|changeResource
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot create change edit"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|delete ()
specifier|public
name|void
name|delete
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
name|deleteChangeEdit
operator|.
name|apply
argument_list|(
name|changeResource
argument_list|,
operator|new
name|Input
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot delete change edit"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|rebase ()
specifier|public
name|void
name|rebase
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
name|rebaseChangeEdit
operator|.
name|apply
argument_list|(
name|changeResource
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot rebase change edit"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|publish ()
specifier|public
name|void
name|publish
parameter_list|()
throws|throws
name|RestApiException
block|{
name|publish
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|publish (PublishChangeEditInput publishChangeEditInput)
specifier|public
name|void
name|publish
parameter_list|(
name|PublishChangeEditInput
name|publishChangeEditInput
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|publishChangeEdit
operator|.
name|apply
argument_list|(
name|changeResource
argument_list|,
name|publishChangeEditInput
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot publish change edit"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFile (String filePath)
specifier|public
name|Optional
argument_list|<
name|BinaryResult
argument_list|>
name|getFile
parameter_list|(
name|String
name|filePath
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|ChangeEditResource
name|changeEditResource
init|=
name|getChangeEditResource
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
name|Response
argument_list|<
name|BinaryResult
argument_list|>
name|fileResponse
init|=
name|changeEditsGet
operator|.
name|apply
argument_list|(
name|changeEditResource
argument_list|)
decl_stmt|;
return|return
name|fileResponse
operator|.
name|isNone
argument_list|()
condition|?
name|Optional
operator|.
name|empty
argument_list|()
else|:
name|Optional
operator|.
name|of
argument_list|(
name|fileResponse
operator|.
name|value
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot retrieve file of change edit"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|renameFile (String oldFilePath, String newFilePath)
specifier|public
name|void
name|renameFile
parameter_list|(
name|String
name|oldFilePath
parameter_list|,
name|String
name|newFilePath
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|ChangeEdits
operator|.
name|Post
operator|.
name|Input
name|renameInput
init|=
operator|new
name|ChangeEdits
operator|.
name|Post
operator|.
name|Input
argument_list|()
decl_stmt|;
name|renameInput
operator|.
name|oldPath
operator|=
name|oldFilePath
expr_stmt|;
name|renameInput
operator|.
name|newPath
operator|=
name|newFilePath
expr_stmt|;
name|changeEditsPost
operator|.
name|apply
argument_list|(
name|changeResource
argument_list|,
name|renameInput
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot rename file of change edit"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|restoreFile (String filePath)
specifier|public
name|void
name|restoreFile
parameter_list|(
name|String
name|filePath
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|ChangeEdits
operator|.
name|Post
operator|.
name|Input
name|restoreInput
init|=
operator|new
name|ChangeEdits
operator|.
name|Post
operator|.
name|Input
argument_list|()
decl_stmt|;
name|restoreInput
operator|.
name|restorePath
operator|=
name|filePath
expr_stmt|;
name|changeEditsPost
operator|.
name|apply
argument_list|(
name|changeResource
argument_list|,
name|restoreInput
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot restore file of change edit"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|modifyFile (String filePath, RawInput newContent)
specifier|public
name|void
name|modifyFile
parameter_list|(
name|String
name|filePath
parameter_list|,
name|RawInput
name|newContent
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|changeEditsPut
operator|.
name|apply
argument_list|(
name|changeResource
argument_list|,
name|filePath
argument_list|,
name|newContent
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot modify file of change edit"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|deleteFile (String filePath)
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|filePath
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|changeEditDeleteContent
operator|.
name|apply
argument_list|(
name|changeResource
argument_list|,
name|filePath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot delete file of change edit"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getCommitMessage ()
specifier|public
name|String
name|getCommitMessage
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
try|try
init|(
name|BinaryResult
name|binaryResult
init|=
name|getChangeEditCommitMessage
operator|.
name|apply
argument_list|(
name|changeResource
argument_list|)
init|)
block|{
return|return
name|binaryResult
operator|.
name|asString
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot get commit message of change edit"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|modifyCommitMessage (String newCommitMessage)
specifier|public
name|void
name|modifyCommitMessage
parameter_list|(
name|String
name|newCommitMessage
parameter_list|)
throws|throws
name|RestApiException
block|{
name|ChangeEdits
operator|.
name|EditMessage
operator|.
name|Input
name|input
init|=
operator|new
name|ChangeEdits
operator|.
name|EditMessage
operator|.
name|Input
argument_list|()
decl_stmt|;
name|input
operator|.
name|message
operator|=
name|newCommitMessage
expr_stmt|;
try|try
block|{
name|modifyChangeEditCommitMessage
operator|.
name|apply
argument_list|(
name|changeResource
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|asRestApiException
argument_list|(
literal|"Cannot modify commit message of change edit"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getChangeEditResource (String filePath)
specifier|private
name|ChangeEditResource
name|getChangeEditResource
parameter_list|(
name|String
name|filePath
parameter_list|)
throws|throws
name|ResourceNotFoundException
throws|,
name|AuthException
throws|,
name|IOException
throws|,
name|OrmException
block|{
return|return
name|changeEdits
operator|.
name|parse
argument_list|(
name|changeResource
argument_list|,
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|filePath
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

