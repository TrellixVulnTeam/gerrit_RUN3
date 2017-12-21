begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.sshd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
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
name|Project
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
name|ChangeFinder
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
name|CurrentUser
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
name|notedb
operator|.
name|ChangeNotes
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
name|permissions
operator|.
name|ChangePermission
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
name|permissions
operator|.
name|GlobalPermission
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
name|permissions
operator|.
name|PermissionBackend
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
name|permissions
operator|.
name|PermissionBackendException
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
name|ProjectState
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
name|restapi
operator|.
name|change
operator|.
name|ChangesCollection
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
name|sshd
operator|.
name|BaseCommand
operator|.
name|UnloggedFailure
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|ChangeArgumentParser
specifier|public
class|class
name|ChangeArgumentParser
block|{
DECL|field|currentUser
specifier|private
specifier|final
name|CurrentUser
name|currentUser
decl_stmt|;
DECL|field|changesCollection
specifier|private
specifier|final
name|ChangesCollection
name|changesCollection
decl_stmt|;
DECL|field|changeFinder
specifier|private
specifier|final
name|ChangeFinder
name|changeFinder
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|changeNotesFactory
specifier|private
specifier|final
name|ChangeNotes
operator|.
name|Factory
name|changeNotesFactory
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeArgumentParser ( CurrentUser currentUser, ChangesCollection changesCollection, ChangeFinder changeFinder, ReviewDb db, ChangeNotes.Factory changeNotesFactory, PermissionBackend permissionBackend)
name|ChangeArgumentParser
parameter_list|(
name|CurrentUser
name|currentUser
parameter_list|,
name|ChangesCollection
name|changesCollection
parameter_list|,
name|ChangeFinder
name|changeFinder
parameter_list|,
name|ReviewDb
name|db
parameter_list|,
name|ChangeNotes
operator|.
name|Factory
name|changeNotesFactory
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|)
block|{
name|this
operator|.
name|currentUser
operator|=
name|currentUser
expr_stmt|;
name|this
operator|.
name|changesCollection
operator|=
name|changesCollection
expr_stmt|;
name|this
operator|.
name|changeFinder
operator|=
name|changeFinder
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|changeNotesFactory
operator|=
name|changeNotesFactory
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
block|}
DECL|method|addChange (String id, Map<Change.Id, ChangeResource> changes)
specifier|public
name|void
name|addChange
parameter_list|(
name|String
name|id
parameter_list|,
name|Map
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|ChangeResource
argument_list|>
name|changes
parameter_list|)
throws|throws
name|UnloggedFailure
throws|,
name|OrmException
throws|,
name|PermissionBackendException
block|{
name|addChange
argument_list|(
name|id
argument_list|,
name|changes
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|addChange ( String id, Map<Change.Id, ChangeResource> changes, ProjectState projectState)
specifier|public
name|void
name|addChange
parameter_list|(
name|String
name|id
parameter_list|,
name|Map
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|ChangeResource
argument_list|>
name|changes
parameter_list|,
name|ProjectState
name|projectState
parameter_list|)
throws|throws
name|UnloggedFailure
throws|,
name|OrmException
throws|,
name|PermissionBackendException
block|{
name|addChange
argument_list|(
name|id
argument_list|,
name|changes
argument_list|,
name|projectState
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|addChange ( String id, Map<Change.Id, ChangeResource> changes, ProjectState projectState, boolean useIndex)
specifier|public
name|void
name|addChange
parameter_list|(
name|String
name|id
parameter_list|,
name|Map
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|ChangeResource
argument_list|>
name|changes
parameter_list|,
name|ProjectState
name|projectState
parameter_list|,
name|boolean
name|useIndex
parameter_list|)
throws|throws
name|UnloggedFailure
throws|,
name|OrmException
throws|,
name|PermissionBackendException
block|{
name|List
argument_list|<
name|ChangeNotes
argument_list|>
name|matched
init|=
name|useIndex
condition|?
name|changeFinder
operator|.
name|find
argument_list|(
name|id
argument_list|)
else|:
name|changeFromNotesFactory
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ChangeNotes
argument_list|>
name|toAdd
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|changes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|canMaintainServer
decl_stmt|;
try|try
block|{
name|permissionBackend
operator|.
name|user
argument_list|(
name|currentUser
argument_list|)
operator|.
name|check
argument_list|(
name|GlobalPermission
operator|.
name|MAINTAIN_SERVER
argument_list|)
expr_stmt|;
name|canMaintainServer
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthException
decl||
name|PermissionBackendException
name|e
parameter_list|)
block|{
name|canMaintainServer
operator|=
literal|false
expr_stmt|;
block|}
for|for
control|(
name|ChangeNotes
name|notes
range|:
name|matched
control|)
block|{
if|if
condition|(
operator|!
name|changes
operator|.
name|containsKey
argument_list|(
name|notes
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|&&
name|inProject
argument_list|(
name|projectState
argument_list|,
name|notes
operator|.
name|getProjectName
argument_list|()
argument_list|)
operator|&&
operator|(
name|canMaintainServer
operator|||
name|permissionBackend
operator|.
name|user
argument_list|(
name|currentUser
argument_list|)
operator|.
name|change
argument_list|(
name|notes
argument_list|)
operator|.
name|database
argument_list|(
name|db
argument_list|)
operator|.
name|test
argument_list|(
name|ChangePermission
operator|.
name|READ
argument_list|)
operator|)
condition|)
block|{
name|toAdd
operator|.
name|add
argument_list|(
name|notes
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|toAdd
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"\""
operator|+
name|id
operator|+
literal|"\" no such change"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|toAdd
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"\""
operator|+
name|id
operator|+
literal|"\" matches multiple changes"
argument_list|)
throw|;
block|}
name|Change
operator|.
name|Id
name|cId
init|=
name|toAdd
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|ChangeResource
name|changeResource
decl_stmt|;
try|try
block|{
name|changeResource
operator|=
name|changesCollection
operator|.
name|parse
argument_list|(
name|cId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
literal|"\""
operator|+
name|id
operator|+
literal|"\" no such change"
argument_list|)
throw|;
block|}
name|changes
operator|.
name|put
argument_list|(
name|cId
argument_list|,
name|changeResource
argument_list|)
expr_stmt|;
block|}
DECL|method|changeFromNotesFactory (String id)
specifier|private
name|List
argument_list|<
name|ChangeNotes
argument_list|>
name|changeFromNotesFactory
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|OrmException
throws|,
name|UnloggedFailure
block|{
return|return
name|changeNotesFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|parseId
argument_list|(
name|id
argument_list|)
argument_list|)
return|;
block|}
DECL|method|parseId (String id)
specifier|private
name|List
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|parseId
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|UnloggedFailure
block|{
try|try
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Change
operator|.
name|Id
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UnloggedFailure
argument_list|(
literal|2
argument_list|,
literal|"Invalid change ID "
operator|+
name|id
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|inProject (ProjectState projectState, Project.NameKey project)
specifier|private
name|boolean
name|inProject
parameter_list|(
name|ProjectState
name|projectState
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
block|{
if|if
condition|(
name|projectState
operator|!=
literal|null
condition|)
block|{
return|return
name|projectState
operator|.
name|getNameKey
argument_list|()
operator|.
name|equals
argument_list|(
name|project
argument_list|)
return|;
block|}
comment|// No --project option, so they want every project.
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

