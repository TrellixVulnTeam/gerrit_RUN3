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
DECL|package|com.google.gerrit.server.permissions
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|permissions
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
name|PermissionBackend
operator|.
name|ForChange
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
operator|.
name|ForProject
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
operator|.
name|ForRef
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
name|query
operator|.
name|change
operator|.
name|ChangeData
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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Helpers for {@link PermissionBackend} that must fail.  *  *<p>These helpers are useful to curry failure state identified inside a non-throwing factory  * method to the throwing {@code check} or {@code test} methods.  */
end_comment

begin_class
DECL|class|FailedPermissionBackend
specifier|public
class|class
name|FailedPermissionBackend
block|{
DECL|method|project (String message)
specifier|public
specifier|static
name|ForProject
name|project
parameter_list|(
name|String
name|message
parameter_list|)
block|{
return|return
name|project
argument_list|(
name|message
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|project (String message, Throwable cause)
specifier|public
specifier|static
name|ForProject
name|project
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
return|return
operator|new
name|FailedProject
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
return|;
block|}
DECL|method|ref (String message)
specifier|public
specifier|static
name|ForRef
name|ref
parameter_list|(
name|String
name|message
parameter_list|)
block|{
return|return
name|ref
argument_list|(
name|message
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|ref (String message, Throwable cause)
specifier|public
specifier|static
name|ForRef
name|ref
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
return|return
operator|new
name|FailedRef
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
return|;
block|}
DECL|method|change (String message)
specifier|public
specifier|static
name|ForChange
name|change
parameter_list|(
name|String
name|message
parameter_list|)
block|{
return|return
name|change
argument_list|(
name|message
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|change (String message, Throwable cause)
specifier|public
specifier|static
name|ForChange
name|change
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
return|return
operator|new
name|FailedChange
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
return|;
block|}
DECL|method|FailedPermissionBackend ()
specifier|private
name|FailedPermissionBackend
parameter_list|()
block|{}
DECL|class|FailedProject
specifier|private
specifier|static
class|class
name|FailedProject
extends|extends
name|ForProject
block|{
DECL|field|message
specifier|private
specifier|final
name|String
name|message
decl_stmt|;
DECL|field|cause
specifier|private
specifier|final
name|Throwable
name|cause
decl_stmt|;
DECL|method|FailedProject (String message, Throwable cause)
name|FailedProject
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|cause
operator|=
name|cause
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|database (Provider<ReviewDb> db)
specifier|public
name|ForProject
name|database
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|user (CurrentUser user)
specifier|public
name|ForProject
name|user
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|ref (String ref)
specifier|public
name|ForRef
name|ref
parameter_list|(
name|String
name|ref
parameter_list|)
block|{
return|return
operator|new
name|FailedRef
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|check (ProjectPermission perm)
specifier|public
name|void
name|check
parameter_list|(
name|ProjectPermission
name|perm
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
throw|throw
operator|new
name|PermissionBackendException
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|test (Collection<ProjectPermission> permSet)
specifier|public
name|Set
argument_list|<
name|ProjectPermission
argument_list|>
name|test
parameter_list|(
name|Collection
argument_list|<
name|ProjectPermission
argument_list|>
name|permSet
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
throw|throw
operator|new
name|PermissionBackendException
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
throw|;
block|}
block|}
DECL|class|FailedRef
specifier|private
specifier|static
class|class
name|FailedRef
extends|extends
name|ForRef
block|{
DECL|field|message
specifier|private
specifier|final
name|String
name|message
decl_stmt|;
DECL|field|cause
specifier|private
specifier|final
name|Throwable
name|cause
decl_stmt|;
DECL|method|FailedRef (String message, Throwable cause)
name|FailedRef
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|cause
operator|=
name|cause
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|database (Provider<ReviewDb> db)
specifier|public
name|ForRef
name|database
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|user (CurrentUser user)
specifier|public
name|ForRef
name|user
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|change (ChangeData cd)
specifier|public
name|ForChange
name|change
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
block|{
return|return
operator|new
name|FailedChange
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|change (ChangeNotes cd)
specifier|public
name|ForChange
name|change
parameter_list|(
name|ChangeNotes
name|cd
parameter_list|)
block|{
return|return
operator|new
name|FailedChange
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|check (RefPermission perm)
specifier|public
name|void
name|check
parameter_list|(
name|RefPermission
name|perm
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
throw|throw
operator|new
name|PermissionBackendException
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|test (Collection<RefPermission> permSet)
specifier|public
name|Set
argument_list|<
name|RefPermission
argument_list|>
name|test
parameter_list|(
name|Collection
argument_list|<
name|RefPermission
argument_list|>
name|permSet
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
throw|throw
operator|new
name|PermissionBackendException
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
throw|;
block|}
block|}
DECL|class|FailedChange
specifier|private
specifier|static
class|class
name|FailedChange
extends|extends
name|ForChange
block|{
DECL|field|message
specifier|private
specifier|final
name|String
name|message
decl_stmt|;
DECL|field|cause
specifier|private
specifier|final
name|Throwable
name|cause
decl_stmt|;
DECL|method|FailedChange (String message, Throwable cause)
name|FailedChange
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|cause
operator|=
name|cause
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|database (Provider<ReviewDb> db)
specifier|public
name|ForChange
name|database
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|user (CurrentUser user)
specifier|public
name|ForChange
name|user
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|check (ChangePermissionOrLabel perm)
specifier|public
name|void
name|check
parameter_list|(
name|ChangePermissionOrLabel
name|perm
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
throw|throw
operator|new
name|PermissionBackendException
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|test (Collection<T> permSet)
specifier|public
parameter_list|<
name|T
extends|extends
name|ChangePermissionOrLabel
parameter_list|>
name|Set
argument_list|<
name|T
argument_list|>
name|test
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|permSet
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
throw|throw
operator|new
name|PermissionBackendException
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

