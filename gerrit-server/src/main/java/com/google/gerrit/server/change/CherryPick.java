begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|common
operator|.
name|errors
operator|.
name|EmailException
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
name|CherryPickInput
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
name|ChangeInfo
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
name|BadRequestException
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
name|ResourceConflictException
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
name|extensions
operator|.
name|restapi
operator|.
name|RestModifyView
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
name|webui
operator|.
name|UiAction
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
name|RefNames
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
name|server
operator|.
name|ReviewDb
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
name|git
operator|.
name|MergeException
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
name|git
operator|.
name|UpdateException
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
name|project
operator|.
name|ChangeControl
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
name|project
operator|.
name|InvalidChangeOperationException
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
name|project
operator|.
name|NoSuchChangeException
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
name|project
operator|.
name|RefControl
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
name|Provider
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
name|Singleton
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

begin_class
annotation|@
name|Singleton
DECL|class|CherryPick
specifier|public
class|class
name|CherryPick
implements|implements
name|RestModifyView
argument_list|<
name|RevisionResource
argument_list|,
name|CherryPickInput
argument_list|>
implements|,
name|UiAction
argument_list|<
name|RevisionResource
argument_list|>
block|{
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
decl_stmt|;
DECL|field|cherryPickChange
specifier|private
specifier|final
name|CherryPickChange
name|cherryPickChange
decl_stmt|;
DECL|field|json
specifier|private
specifier|final
name|ChangeJson
operator|.
name|Factory
name|json
decl_stmt|;
annotation|@
name|Inject
DECL|method|CherryPick (Provider<ReviewDb> dbProvider, CherryPickChange cherryPickChange, ChangeJson.Factory json)
name|CherryPick
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|CherryPickChange
name|cherryPickChange
parameter_list|,
name|ChangeJson
operator|.
name|Factory
name|json
parameter_list|)
block|{
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|cherryPickChange
operator|=
name|cherryPickChange
expr_stmt|;
name|this
operator|.
name|json
operator|=
name|json
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (RevisionResource revision, CherryPickInput input)
specifier|public
name|ChangeInfo
name|apply
parameter_list|(
name|RevisionResource
name|revision
parameter_list|,
name|CherryPickInput
name|input
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|EmailException
throws|,
name|UpdateException
throws|,
name|RestApiException
block|{
specifier|final
name|ChangeControl
name|control
init|=
name|revision
operator|.
name|getControl
argument_list|()
decl_stmt|;
if|if
condition|(
name|input
operator|.
name|message
operator|==
literal|null
operator|||
name|input
operator|.
name|message
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"message must be non-empty"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|input
operator|.
name|destination
operator|==
literal|null
operator|||
name|input
operator|.
name|destination
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"destination must be non-empty"
argument_list|)
throw|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
name|ReviewDb
name|db
init|=
name|dbProvider
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|control
operator|.
name|isVisible
argument_list|(
name|db
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Cherry pick not permitted"
argument_list|)
throw|;
block|}
name|String
name|refName
init|=
name|RefNames
operator|.
name|fullName
argument_list|(
name|input
operator|.
name|destination
argument_list|)
decl_stmt|;
name|RefControl
name|refControl
init|=
name|control
operator|.
name|getProjectControl
argument_list|()
operator|.
name|controlForRef
argument_list|(
name|refName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|refControl
operator|.
name|canUpload
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Not allowed to cherry pick "
operator|+
name|revision
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" to "
operator|+
name|input
operator|.
name|destination
argument_list|)
throw|;
block|}
try|try
block|{
name|Change
operator|.
name|Id
name|cherryPickedChangeId
init|=
name|cherryPickChange
operator|.
name|cherryPick
argument_list|(
name|revision
operator|.
name|getChange
argument_list|()
argument_list|,
name|revision
operator|.
name|getPatchSet
argument_list|()
argument_list|,
name|input
operator|.
name|message
argument_list|,
name|refName
argument_list|,
name|refControl
argument_list|)
decl_stmt|;
return|return
name|json
operator|.
name|create
argument_list|(
name|ChangeJson
operator|.
name|NO_OPTIONS
argument_list|)
operator|.
name|format
argument_list|(
name|cherryPickedChangeId
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidChangeOperationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|MergeException
decl||
name|NoSuchChangeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDescription (RevisionResource resource)
specifier|public
name|UiAction
operator|.
name|Description
name|getDescription
parameter_list|(
name|RevisionResource
name|resource
parameter_list|)
block|{
return|return
operator|new
name|UiAction
operator|.
name|Description
argument_list|()
operator|.
name|setLabel
argument_list|(
literal|"Cherry Pick"
argument_list|)
operator|.
name|setTitle
argument_list|(
literal|"Cherry pick change to a different branch"
argument_list|)
operator|.
name|setVisible
argument_list|(
name|resource
operator|.
name|getControl
argument_list|()
operator|.
name|getProjectControl
argument_list|()
operator|.
name|canUpload
argument_list|()
operator|&&
name|resource
operator|.
name|isCurrent
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

