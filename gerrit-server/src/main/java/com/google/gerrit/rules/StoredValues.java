begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
DECL|package|com.google.gerrit.rules
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|rules
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
name|rules
operator|.
name|StoredValue
operator|.
name|create
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
name|client
operator|.
name|DiffPreferencesInfo
operator|.
name|Whitespace
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
name|Account
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
name|gerrit
operator|.
name|reviewdb
operator|.
name|client
operator|.
name|PatchSetInfo
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
name|AnonymousUser
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
name|IdentifiedUser
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
name|account
operator|.
name|AccountCache
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
name|account
operator|.
name|Accounts
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
name|account
operator|.
name|Emails
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
name|GitRepositoryManager
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
name|patch
operator|.
name|PatchList
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
name|patch
operator|.
name|PatchListCache
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
name|patch
operator|.
name|PatchListKey
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
name|patch
operator|.
name|PatchListNotAvailableException
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
name|patch
operator|.
name|PatchSetInfoFactory
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
name|patch
operator|.
name|PatchSetInfoNotAvailableException
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
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|exceptions
operator|.
name|SystemException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|prolog_cafe
operator|.
name|lang
operator|.
name|Prolog
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
name|HashMap
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

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Repository
import|;
end_import

begin_class
DECL|class|StoredValues
specifier|public
specifier|final
class|class
name|StoredValues
block|{
DECL|field|ACCOUNTS
specifier|public
specifier|static
specifier|final
name|StoredValue
argument_list|<
name|Accounts
argument_list|>
name|ACCOUNTS
init|=
name|create
argument_list|(
name|Accounts
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ACCOUNT_CACHE
specifier|public
specifier|static
specifier|final
name|StoredValue
argument_list|<
name|AccountCache
argument_list|>
name|ACCOUNT_CACHE
init|=
name|create
argument_list|(
name|AccountCache
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|EMAILS
specifier|public
specifier|static
specifier|final
name|StoredValue
argument_list|<
name|Emails
argument_list|>
name|EMAILS
init|=
name|create
argument_list|(
name|Emails
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|REVIEW_DB
specifier|public
specifier|static
specifier|final
name|StoredValue
argument_list|<
name|ReviewDb
argument_list|>
name|REVIEW_DB
init|=
name|create
argument_list|(
name|ReviewDb
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CHANGE_DATA
specifier|public
specifier|static
specifier|final
name|StoredValue
argument_list|<
name|ChangeData
argument_list|>
name|CHANGE_DATA
init|=
name|create
argument_list|(
name|ChangeData
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Note: no guarantees are made about the user passed in the ChangeControl; do
comment|// not depend on this directly. Either use .forUser(otherUser) to get a
comment|// control for a specific known user, or use CURRENT_USER, which may be null
comment|// for rule types that may not depend on the current user.
DECL|field|CHANGE_CONTROL
specifier|public
specifier|static
specifier|final
name|StoredValue
argument_list|<
name|ChangeControl
argument_list|>
name|CHANGE_CONTROL
init|=
name|create
argument_list|(
name|ChangeControl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CURRENT_USER
specifier|public
specifier|static
specifier|final
name|StoredValue
argument_list|<
name|CurrentUser
argument_list|>
name|CURRENT_USER
init|=
name|create
argument_list|(
name|CurrentUser
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|getChange (Prolog engine)
specifier|public
specifier|static
name|Change
name|getChange
parameter_list|(
name|Prolog
name|engine
parameter_list|)
throws|throws
name|SystemException
block|{
name|ChangeData
name|cd
init|=
name|CHANGE_DATA
operator|.
name|get
argument_list|(
name|engine
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|cd
operator|.
name|change
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SystemException
argument_list|(
literal|"Cannot load change "
operator|+
name|cd
operator|.
name|getId
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|getPatchSet (Prolog engine)
specifier|public
specifier|static
name|PatchSet
name|getPatchSet
parameter_list|(
name|Prolog
name|engine
parameter_list|)
throws|throws
name|SystemException
block|{
name|ChangeData
name|cd
init|=
name|CHANGE_DATA
operator|.
name|get
argument_list|(
name|engine
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|cd
operator|.
name|currentPatchSet
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SystemException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|field|PATCH_SET_INFO
specifier|public
specifier|static
specifier|final
name|StoredValue
argument_list|<
name|PatchSetInfo
argument_list|>
name|PATCH_SET_INFO
init|=
operator|new
name|StoredValue
argument_list|<
name|PatchSetInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PatchSetInfo
name|createValue
parameter_list|(
name|Prolog
name|engine
parameter_list|)
block|{
name|Change
name|change
init|=
name|getChange
argument_list|(
name|engine
argument_list|)
decl_stmt|;
name|PatchSet
name|ps
init|=
name|getPatchSet
argument_list|(
name|engine
argument_list|)
decl_stmt|;
name|PrologEnvironment
name|env
init|=
operator|(
name|PrologEnvironment
operator|)
name|engine
operator|.
name|control
decl_stmt|;
name|PatchSetInfoFactory
name|patchInfoFactory
init|=
name|env
operator|.
name|getArgs
argument_list|()
operator|.
name|getPatchSetInfoFactory
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|patchInfoFactory
operator|.
name|get
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|,
name|ps
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PatchSetInfoNotAvailableException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SystemException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
DECL|field|PATCH_LIST
specifier|public
specifier|static
specifier|final
name|StoredValue
argument_list|<
name|PatchList
argument_list|>
name|PATCH_LIST
init|=
operator|new
name|StoredValue
argument_list|<
name|PatchList
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PatchList
name|createValue
parameter_list|(
name|Prolog
name|engine
parameter_list|)
block|{
name|PrologEnvironment
name|env
init|=
operator|(
name|PrologEnvironment
operator|)
name|engine
operator|.
name|control
decl_stmt|;
name|PatchSet
name|ps
init|=
name|getPatchSet
argument_list|(
name|engine
argument_list|)
decl_stmt|;
name|PatchListCache
name|plCache
init|=
name|env
operator|.
name|getArgs
argument_list|()
operator|.
name|getPatchListCache
argument_list|()
decl_stmt|;
name|Change
name|change
init|=
name|getChange
argument_list|(
name|engine
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|project
init|=
name|change
operator|.
name|getProject
argument_list|()
decl_stmt|;
name|ObjectId
name|b
init|=
name|ObjectId
operator|.
name|fromString
argument_list|(
name|ps
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|Whitespace
name|ws
init|=
name|Whitespace
operator|.
name|IGNORE_NONE
decl_stmt|;
name|PatchListKey
name|plKey
init|=
name|PatchListKey
operator|.
name|againstDefaultBase
argument_list|(
name|b
argument_list|,
name|ws
argument_list|)
decl_stmt|;
name|PatchList
name|patchList
decl_stmt|;
try|try
block|{
name|patchList
operator|=
name|plCache
operator|.
name|get
argument_list|(
name|plKey
argument_list|,
name|project
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PatchListNotAvailableException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SystemException
argument_list|(
literal|"Cannot create "
operator|+
name|plKey
argument_list|)
throw|;
block|}
return|return
name|patchList
return|;
block|}
block|}
decl_stmt|;
DECL|field|REPOSITORY
specifier|public
specifier|static
specifier|final
name|StoredValue
argument_list|<
name|Repository
argument_list|>
name|REPOSITORY
init|=
operator|new
name|StoredValue
argument_list|<
name|Repository
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Repository
name|createValue
parameter_list|(
name|Prolog
name|engine
parameter_list|)
block|{
name|PrologEnvironment
name|env
init|=
operator|(
name|PrologEnvironment
operator|)
name|engine
operator|.
name|control
decl_stmt|;
name|GitRepositoryManager
name|gitMgr
init|=
name|env
operator|.
name|getArgs
argument_list|()
operator|.
name|getGitRepositoryManager
argument_list|()
decl_stmt|;
name|Change
name|change
init|=
name|getChange
argument_list|(
name|engine
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|projectKey
init|=
name|change
operator|.
name|getProject
argument_list|()
decl_stmt|;
name|Repository
name|repo
decl_stmt|;
try|try
block|{
name|repo
operator|=
name|gitMgr
operator|.
name|openRepository
argument_list|(
name|projectKey
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SystemException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|env
operator|.
name|addToCleanup
argument_list|(
name|repo
operator|::
name|close
argument_list|)
expr_stmt|;
return|return
name|repo
return|;
block|}
block|}
decl_stmt|;
DECL|field|PERMISSION_BACKEND
specifier|public
specifier|static
specifier|final
name|StoredValue
argument_list|<
name|PermissionBackend
argument_list|>
name|PERMISSION_BACKEND
init|=
operator|new
name|StoredValue
argument_list|<
name|PermissionBackend
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|PermissionBackend
name|createValue
parameter_list|(
name|Prolog
name|engine
parameter_list|)
block|{
name|PrologEnvironment
name|env
init|=
operator|(
name|PrologEnvironment
operator|)
name|engine
operator|.
name|control
decl_stmt|;
return|return
name|env
operator|.
name|getArgs
argument_list|()
operator|.
name|getPermissionBackend
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|ANONYMOUS_USER
specifier|public
specifier|static
specifier|final
name|StoredValue
argument_list|<
name|AnonymousUser
argument_list|>
name|ANONYMOUS_USER
init|=
operator|new
name|StoredValue
argument_list|<
name|AnonymousUser
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|AnonymousUser
name|createValue
parameter_list|(
name|Prolog
name|engine
parameter_list|)
block|{
name|PrologEnvironment
name|env
init|=
operator|(
name|PrologEnvironment
operator|)
name|engine
operator|.
name|control
decl_stmt|;
return|return
name|env
operator|.
name|getArgs
argument_list|()
operator|.
name|getAnonymousUser
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|USERS
specifier|public
specifier|static
specifier|final
name|StoredValue
argument_list|<
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|IdentifiedUser
argument_list|>
argument_list|>
name|USERS
init|=
operator|new
name|StoredValue
argument_list|<
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|IdentifiedUser
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|IdentifiedUser
argument_list|>
name|createValue
parameter_list|(
name|Prolog
name|engine
parameter_list|)
block|{
return|return
operator|new
name|HashMap
argument_list|<>
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|method|StoredValues ()
specifier|private
name|StoredValues
parameter_list|()
block|{}
block|}
end_class

end_unit

