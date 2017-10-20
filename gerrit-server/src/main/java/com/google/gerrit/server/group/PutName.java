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
DECL|package|com.google.gerrit.server.group
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|group
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|GroupDescription
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
name|errors
operator|.
name|NameAlreadyUsedException
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
name|errors
operator|.
name|NoSuchGroupException
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
name|NameInput
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
name|MethodNotAllowedException
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
name|group
operator|.
name|db
operator|.
name|GroupsUpdate
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
DECL|class|PutName
specifier|public
class|class
name|PutName
implements|implements
name|RestModifyView
argument_list|<
name|GroupResource
argument_list|,
name|NameInput
argument_list|>
block|{
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|groupsUpdateProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|GroupsUpdate
argument_list|>
name|groupsUpdateProvider
decl_stmt|;
annotation|@
name|Inject
DECL|method|PutName (Provider<ReviewDb> db, @UserInitiated Provider<GroupsUpdate> groupsUpdateProvider)
name|PutName
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
annotation|@
name|UserInitiated
name|Provider
argument_list|<
name|GroupsUpdate
argument_list|>
name|groupsUpdateProvider
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|groupsUpdateProvider
operator|=
name|groupsUpdateProvider
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (GroupResource rsrc, NameInput input)
specifier|public
name|String
name|apply
parameter_list|(
name|GroupResource
name|rsrc
parameter_list|,
name|NameInput
name|input
parameter_list|)
throws|throws
name|MethodNotAllowedException
throws|,
name|AuthException
throws|,
name|BadRequestException
throws|,
name|ResourceConflictException
throws|,
name|ResourceNotFoundException
throws|,
name|OrmException
throws|,
name|IOException
block|{
name|GroupDescription
operator|.
name|Internal
name|internalGroup
init|=
name|rsrc
operator|.
name|asInternalGroup
argument_list|()
operator|.
name|orElseThrow
argument_list|(
name|MethodNotAllowedException
operator|::
operator|new
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|isOwner
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Not group owner"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|input
operator|==
literal|null
operator|||
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|input
operator|.
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"name is required"
argument_list|)
throw|;
block|}
name|String
name|newName
init|=
name|input
operator|.
name|name
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|newName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"name is required"
argument_list|)
throw|;
block|}
if|if
condition|(
name|internalGroup
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|newName
argument_list|)
condition|)
block|{
return|return
name|newName
return|;
block|}
name|renameGroup
argument_list|(
name|internalGroup
argument_list|,
name|newName
argument_list|)
expr_stmt|;
return|return
name|newName
return|;
block|}
DECL|method|renameGroup (GroupDescription.Internal group, String newName)
specifier|private
name|void
name|renameGroup
parameter_list|(
name|GroupDescription
operator|.
name|Internal
name|group
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|ResourceConflictException
throws|,
name|ResourceNotFoundException
throws|,
name|OrmException
throws|,
name|IOException
block|{
name|AccountGroup
operator|.
name|UUID
name|groupUuid
init|=
name|group
operator|.
name|getGroupUUID
argument_list|()
decl_stmt|;
try|try
block|{
name|groupsUpdateProvider
operator|.
name|get
argument_list|()
operator|.
name|renameGroup
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|groupUuid
argument_list|,
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
name|newName
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchGroupException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Group %s not found"
argument_list|,
name|groupUuid
argument_list|)
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NameAlreadyUsedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"group with name "
operator|+
name|newName
operator|+
literal|" already exists"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

