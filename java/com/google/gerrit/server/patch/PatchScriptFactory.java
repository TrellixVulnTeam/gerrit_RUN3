begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.patch
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|patch
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|flogger
operator|.
name|FluentLogger
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
name|Nullable
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
name|CommentDetail
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
name|PatchScript
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
name|Comment
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
name|Patch
operator|.
name|ChangeType
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
name|server
operator|.
name|CommentsUtil
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
name|PatchSetUtil
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
name|edit
operator|.
name|ChangeEdit
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
name|edit
operator|.
name|ChangeEditUtil
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
name|git
operator|.
name|LargeObjectException
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
name|ProjectCache
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
name|assistedinject
operator|.
name|Assisted
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
name|AssistedInject
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
name|ArrayList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
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
name|errors
operator|.
name|RepositoryNotFoundException
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
DECL|class|PatchScriptFactory
specifier|public
class|class
name|PatchScriptFactory
implements|implements
name|Callable
argument_list|<
name|PatchScript
argument_list|>
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create ( ChangeNotes notes, String fileName, @Assisted(R) PatchSet.Id patchSetA, @Assisted(R) PatchSet.Id patchSetB, DiffPreferencesInfo diffPrefs)
name|PatchScriptFactory
name|create
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|String
name|fileName
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"patchSetA"
argument_list|)
name|PatchSet
operator|.
name|Id
name|patchSetA
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"patchSetB"
argument_list|)
name|PatchSet
operator|.
name|Id
name|patchSetB
parameter_list|,
name|DiffPreferencesInfo
name|diffPrefs
parameter_list|)
function_decl|;
DECL|method|create ( ChangeNotes notes, String fileName, int parentNum, PatchSet.Id patchSetB, DiffPreferencesInfo diffPrefs)
name|PatchScriptFactory
name|create
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|String
name|fileName
parameter_list|,
name|int
name|parentNum
parameter_list|,
name|PatchSet
operator|.
name|Id
name|patchSetB
parameter_list|,
name|DiffPreferencesInfo
name|diffPrefs
parameter_list|)
function_decl|;
block|}
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
DECL|field|builderFactory
specifier|private
specifier|final
name|Provider
argument_list|<
name|PatchScriptBuilder
argument_list|>
name|builderFactory
decl_stmt|;
DECL|field|patchListCache
specifier|private
specifier|final
name|PatchListCache
name|patchListCache
decl_stmt|;
DECL|field|commentsUtil
specifier|private
specifier|final
name|CommentsUtil
name|commentsUtil
decl_stmt|;
DECL|field|fileName
specifier|private
specifier|final
name|String
name|fileName
decl_stmt|;
DECL|field|psa
annotation|@
name|Nullable
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|psa
decl_stmt|;
DECL|field|parentNum
specifier|private
specifier|final
name|int
name|parentNum
decl_stmt|;
DECL|field|psb
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|psb
decl_stmt|;
DECL|field|diffPrefs
specifier|private
specifier|final
name|DiffPreferencesInfo
name|diffPrefs
decl_stmt|;
DECL|field|editReader
specifier|private
specifier|final
name|ChangeEditUtil
name|editReader
decl_stmt|;
DECL|field|userProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|edit
specifier|private
name|Optional
argument_list|<
name|ChangeEdit
argument_list|>
name|edit
decl_stmt|;
DECL|field|changeId
specifier|private
specifier|final
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
DECL|field|loadHistory
specifier|private
name|boolean
name|loadHistory
init|=
literal|true
decl_stmt|;
DECL|field|loadComments
specifier|private
name|boolean
name|loadComments
init|=
literal|true
decl_stmt|;
DECL|field|notes
specifier|private
name|ChangeNotes
name|notes
decl_stmt|;
DECL|field|aId
specifier|private
name|ObjectId
name|aId
decl_stmt|;
DECL|field|bId
specifier|private
name|ObjectId
name|bId
decl_stmt|;
DECL|field|history
specifier|private
name|List
argument_list|<
name|Patch
argument_list|>
name|history
decl_stmt|;
DECL|field|comments
specifier|private
name|CommentDetail
name|comments
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|PatchScriptFactory ( GitRepositoryManager grm, PatchSetUtil psUtil, Provider<PatchScriptBuilder> builderFactory, PatchListCache patchListCache, CommentsUtil commentsUtil, ChangeEditUtil editReader, Provider<CurrentUser> userProvider, PermissionBackend permissionBackend, ProjectCache projectCache, @Assisted ChangeNotes notes, @Assisted String fileName, @Assisted(R) @Nullable PatchSet.Id patchSetA, @Assisted(R) PatchSet.Id patchSetB, @Assisted DiffPreferencesInfo diffPrefs)
name|PatchScriptFactory
parameter_list|(
name|GitRepositoryManager
name|grm
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
name|Provider
argument_list|<
name|PatchScriptBuilder
argument_list|>
name|builderFactory
parameter_list|,
name|PatchListCache
name|patchListCache
parameter_list|,
name|CommentsUtil
name|commentsUtil
parameter_list|,
name|ChangeEditUtil
name|editReader
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
annotation|@
name|Assisted
name|ChangeNotes
name|notes
parameter_list|,
annotation|@
name|Assisted
name|String
name|fileName
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"patchSetA"
argument_list|)
annotation|@
name|Nullable
name|PatchSet
operator|.
name|Id
name|patchSetA
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"patchSetB"
argument_list|)
name|PatchSet
operator|.
name|Id
name|patchSetB
parameter_list|,
annotation|@
name|Assisted
name|DiffPreferencesInfo
name|diffPrefs
parameter_list|)
block|{
name|this
operator|.
name|repoManager
operator|=
name|grm
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
name|this
operator|.
name|builderFactory
operator|=
name|builderFactory
expr_stmt|;
name|this
operator|.
name|patchListCache
operator|=
name|patchListCache
expr_stmt|;
name|this
operator|.
name|notes
operator|=
name|notes
expr_stmt|;
name|this
operator|.
name|commentsUtil
operator|=
name|commentsUtil
expr_stmt|;
name|this
operator|.
name|editReader
operator|=
name|editReader
expr_stmt|;
name|this
operator|.
name|userProvider
operator|=
name|userProvider
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|fileName
operator|=
name|fileName
expr_stmt|;
name|this
operator|.
name|psa
operator|=
name|patchSetA
expr_stmt|;
name|this
operator|.
name|parentNum
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|psb
operator|=
name|patchSetB
expr_stmt|;
name|this
operator|.
name|diffPrefs
operator|=
name|diffPrefs
expr_stmt|;
name|changeId
operator|=
name|patchSetB
operator|.
name|changeId
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AssistedInject
DECL|method|PatchScriptFactory ( GitRepositoryManager grm, PatchSetUtil psUtil, Provider<PatchScriptBuilder> builderFactory, PatchListCache patchListCache, CommentsUtil commentsUtil, ChangeEditUtil editReader, Provider<CurrentUser> userProvider, PermissionBackend permissionBackend, ProjectCache projectCache, @Assisted ChangeNotes notes, @Assisted String fileName, @Assisted int parentNum, @Assisted PatchSet.Id patchSetB, @Assisted DiffPreferencesInfo diffPrefs)
name|PatchScriptFactory
parameter_list|(
name|GitRepositoryManager
name|grm
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
name|Provider
argument_list|<
name|PatchScriptBuilder
argument_list|>
name|builderFactory
parameter_list|,
name|PatchListCache
name|patchListCache
parameter_list|,
name|CommentsUtil
name|commentsUtil
parameter_list|,
name|ChangeEditUtil
name|editReader
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
annotation|@
name|Assisted
name|ChangeNotes
name|notes
parameter_list|,
annotation|@
name|Assisted
name|String
name|fileName
parameter_list|,
annotation|@
name|Assisted
name|int
name|parentNum
parameter_list|,
annotation|@
name|Assisted
name|PatchSet
operator|.
name|Id
name|patchSetB
parameter_list|,
annotation|@
name|Assisted
name|DiffPreferencesInfo
name|diffPrefs
parameter_list|)
block|{
name|this
operator|.
name|repoManager
operator|=
name|grm
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
name|this
operator|.
name|builderFactory
operator|=
name|builderFactory
expr_stmt|;
name|this
operator|.
name|patchListCache
operator|=
name|patchListCache
expr_stmt|;
name|this
operator|.
name|notes
operator|=
name|notes
expr_stmt|;
name|this
operator|.
name|commentsUtil
operator|=
name|commentsUtil
expr_stmt|;
name|this
operator|.
name|editReader
operator|=
name|editReader
expr_stmt|;
name|this
operator|.
name|userProvider
operator|=
name|userProvider
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|fileName
operator|=
name|fileName
expr_stmt|;
name|this
operator|.
name|psa
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|parentNum
operator|=
name|parentNum
expr_stmt|;
name|this
operator|.
name|psb
operator|=
name|patchSetB
expr_stmt|;
name|this
operator|.
name|diffPrefs
operator|=
name|diffPrefs
expr_stmt|;
name|changeId
operator|=
name|patchSetB
operator|.
name|changeId
argument_list|()
expr_stmt|;
name|checkArgument
argument_list|(
name|parentNum
operator|>=
literal|0
argument_list|,
literal|"parentNum must be>= 0"
argument_list|)
expr_stmt|;
block|}
DECL|method|setLoadHistory (boolean load)
specifier|public
name|void
name|setLoadHistory
parameter_list|(
name|boolean
name|load
parameter_list|)
block|{
name|loadHistory
operator|=
name|load
expr_stmt|;
block|}
DECL|method|setLoadComments (boolean load)
specifier|public
name|void
name|setLoadComments
parameter_list|(
name|boolean
name|load
parameter_list|)
block|{
name|loadComments
operator|=
name|load
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|PatchScript
name|call
parameter_list|()
throws|throws
name|LargeObjectException
throws|,
name|AuthException
throws|,
name|InvalidChangeOperationException
throws|,
name|IOException
throws|,
name|PermissionBackendException
block|{
if|if
condition|(
name|parentNum
operator|<
literal|0
condition|)
block|{
name|validatePatchSetId
argument_list|(
name|psa
argument_list|)
expr_stmt|;
block|}
name|validatePatchSetId
argument_list|(
name|psb
argument_list|)
expr_stmt|;
name|PatchSet
name|psEntityA
init|=
name|psa
operator|!=
literal|null
condition|?
name|psUtil
operator|.
name|get
argument_list|(
name|notes
argument_list|,
name|psa
argument_list|)
else|:
literal|null
decl_stmt|;
name|PatchSet
name|psEntityB
init|=
name|psb
operator|.
name|get
argument_list|()
operator|==
literal|0
condition|?
operator|new
name|PatchSet
argument_list|(
name|psb
argument_list|)
else|:
name|psUtil
operator|.
name|get
argument_list|(
name|notes
argument_list|,
name|psb
argument_list|)
decl_stmt|;
if|if
condition|(
name|psEntityA
operator|!=
literal|null
operator|||
name|psEntityB
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|permissionBackend
operator|.
name|currentUser
argument_list|()
operator|.
name|change
argument_list|(
name|notes
argument_list|)
operator|.
name|check
argument_list|(
name|ChangePermission
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|notes
operator|.
name|getProjectName
argument_list|()
argument_list|)
operator|.
name|statePermitsRead
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|)
throw|;
block|}
try|try
init|(
name|Repository
name|git
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|notes
operator|.
name|getProjectName
argument_list|()
argument_list|)
init|)
block|{
name|bId
operator|=
name|toObjectId
argument_list|(
name|psEntityB
argument_list|)
expr_stmt|;
if|if
condition|(
name|parentNum
operator|<
literal|0
condition|)
block|{
name|aId
operator|=
name|psEntityA
operator|!=
literal|null
condition|?
name|toObjectId
argument_list|(
name|psEntityA
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|PatchList
name|list
init|=
name|listFor
argument_list|(
name|keyFor
argument_list|(
name|diffPrefs
operator|.
name|ignoreWhitespace
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|PatchScriptBuilder
name|b
init|=
name|newBuilder
argument_list|(
name|list
argument_list|,
name|git
argument_list|)
decl_stmt|;
specifier|final
name|PatchListEntry
name|content
init|=
name|list
operator|.
name|get
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|loadCommentsAndHistory
argument_list|(
name|content
operator|.
name|getChangeType
argument_list|()
argument_list|,
name|content
operator|.
name|getOldName
argument_list|()
argument_list|,
name|content
operator|.
name|getNewName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toPatchScript
argument_list|(
name|content
argument_list|,
name|comments
argument_list|,
name|history
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PatchListNotAvailableException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"File content unavailable"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|errors
operator|.
name|LargeObjectException
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|LargeObjectException
argument_list|(
literal|"File content is too large"
argument_list|,
name|err
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Repository %s not found"
argument_list|,
name|notes
operator|.
name|getProjectName
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Cannot open repository %s"
argument_list|,
name|notes
operator|.
name|getProjectName
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|keyFor (Whitespace whitespace)
specifier|private
name|PatchListKey
name|keyFor
parameter_list|(
name|Whitespace
name|whitespace
parameter_list|)
block|{
if|if
condition|(
name|parentNum
operator|<
literal|0
condition|)
block|{
return|return
name|PatchListKey
operator|.
name|againstCommit
argument_list|(
name|aId
argument_list|,
name|bId
argument_list|,
name|whitespace
argument_list|)
return|;
block|}
return|return
name|PatchListKey
operator|.
name|againstParentNum
argument_list|(
name|parentNum
operator|+
literal|1
argument_list|,
name|bId
argument_list|,
name|whitespace
argument_list|)
return|;
block|}
DECL|method|listFor (PatchListKey key)
specifier|private
name|PatchList
name|listFor
parameter_list|(
name|PatchListKey
name|key
parameter_list|)
throws|throws
name|PatchListNotAvailableException
block|{
return|return
name|patchListCache
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|notes
operator|.
name|getProjectName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|newBuilder (PatchList list, Repository git)
specifier|private
name|PatchScriptBuilder
name|newBuilder
parameter_list|(
name|PatchList
name|list
parameter_list|,
name|Repository
name|git
parameter_list|)
block|{
specifier|final
name|PatchScriptBuilder
name|b
init|=
name|builderFactory
operator|.
name|get
argument_list|()
decl_stmt|;
name|b
operator|.
name|setRepository
argument_list|(
name|git
argument_list|,
name|notes
operator|.
name|getProjectName
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|setChange
argument_list|(
name|notes
operator|.
name|getChange
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|setDiffPrefs
argument_list|(
name|diffPrefs
argument_list|)
expr_stmt|;
name|b
operator|.
name|setTrees
argument_list|(
name|list
operator|.
name|getComparisonType
argument_list|()
argument_list|,
name|list
operator|.
name|getOldId
argument_list|()
argument_list|,
name|list
operator|.
name|getNewId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
DECL|method|toObjectId (PatchSet ps)
specifier|private
name|ObjectId
name|toObjectId
parameter_list|(
name|PatchSet
name|ps
parameter_list|)
throws|throws
name|AuthException
throws|,
name|IOException
block|{
if|if
condition|(
name|ps
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|getEditRev
argument_list|()
return|;
block|}
if|if
condition|(
name|ps
operator|.
name|getRevision
argument_list|()
operator|==
literal|null
operator|||
name|ps
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|)
throw|;
block|}
try|try
block|{
return|return
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
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|log
argument_list|(
literal|"Patch set %s has invalid revision"
argument_list|,
name|ps
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getEditRev ()
specifier|private
name|ObjectId
name|getEditRev
parameter_list|()
throws|throws
name|AuthException
throws|,
name|IOException
block|{
name|edit
operator|=
name|editReader
operator|.
name|byChange
argument_list|(
name|notes
argument_list|)
expr_stmt|;
if|if
condition|(
name|edit
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|edit
operator|.
name|get
argument_list|()
operator|.
name|getEditCommit
argument_list|()
return|;
block|}
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|notes
operator|.
name|getChangeId
argument_list|()
argument_list|)
throw|;
block|}
DECL|method|validatePatchSetId (PatchSet.Id psId)
specifier|private
name|void
name|validatePatchSetId
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
throws|throws
name|NoSuchChangeException
block|{
if|if
condition|(
name|psId
operator|==
literal|null
condition|)
block|{
comment|// OK, means use base;
block|}
elseif|else
if|if
condition|(
name|changeId
operator|.
name|equals
argument_list|(
name|psId
operator|.
name|changeId
argument_list|()
argument_list|)
condition|)
block|{
comment|// OK, same change;
block|}
else|else
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|)
throw|;
block|}
block|}
DECL|method|loadCommentsAndHistory (ChangeType changeType, String oldName, String newName)
specifier|private
name|void
name|loadCommentsAndHistory
parameter_list|(
name|ChangeType
name|changeType
parameter_list|,
name|String
name|oldName
parameter_list|,
name|String
name|newName
parameter_list|)
block|{
name|Map
argument_list|<
name|Patch
operator|.
name|Key
argument_list|,
name|Patch
argument_list|>
name|byKey
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|loadHistory
condition|)
block|{
comment|// This seems like a cheap trick. It doesn't properly account for a
comment|// file that gets renamed between patch set 1 and patch set 2. We
comment|// will wind up packing the wrong Patch object because we didn't do
comment|// proper rename detection between the patch sets.
comment|//
name|history
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|PatchSet
name|ps
range|:
name|psUtil
operator|.
name|byChange
argument_list|(
name|notes
argument_list|)
control|)
block|{
name|String
name|name
init|=
name|fileName
decl_stmt|;
if|if
condition|(
name|psa
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|changeType
condition|)
block|{
case|case
name|COPIED
case|:
case|case
name|RENAMED
case|:
if|if
condition|(
name|ps
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|psa
argument_list|)
condition|)
block|{
name|name
operator|=
name|oldName
expr_stmt|;
block|}
break|break;
case|case
name|MODIFIED
case|:
case|case
name|DELETED
case|:
case|case
name|ADDED
case|:
case|case
name|REWRITE
case|:
break|break;
block|}
block|}
name|Patch
name|p
init|=
operator|new
name|Patch
argument_list|(
name|Patch
operator|.
name|key
argument_list|(
name|ps
operator|.
name|getId
argument_list|()
argument_list|,
name|name
argument_list|)
argument_list|)
decl_stmt|;
name|history
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|byKey
operator|.
name|put
argument_list|(
name|p
operator|.
name|getKey
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|edit
operator|!=
literal|null
operator|&&
name|edit
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|Patch
name|p
init|=
operator|new
name|Patch
argument_list|(
name|Patch
operator|.
name|key
argument_list|(
name|PatchSet
operator|.
name|id
argument_list|(
name|psb
operator|.
name|changeId
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
name|fileName
argument_list|)
argument_list|)
decl_stmt|;
name|history
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|byKey
operator|.
name|put
argument_list|(
name|p
operator|.
name|getKey
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|loadComments
operator|&&
name|edit
operator|==
literal|null
condition|)
block|{
name|comments
operator|=
operator|new
name|CommentDetail
argument_list|(
name|psa
argument_list|,
name|psb
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|changeType
condition|)
block|{
case|case
name|ADDED
case|:
case|case
name|MODIFIED
case|:
name|loadPublished
argument_list|(
name|byKey
argument_list|,
name|newName
argument_list|)
expr_stmt|;
break|break;
case|case
name|DELETED
case|:
name|loadPublished
argument_list|(
name|byKey
argument_list|,
name|newName
argument_list|)
expr_stmt|;
break|break;
case|case
name|COPIED
case|:
case|case
name|RENAMED
case|:
if|if
condition|(
name|psa
operator|!=
literal|null
condition|)
block|{
name|loadPublished
argument_list|(
name|byKey
argument_list|,
name|oldName
argument_list|)
expr_stmt|;
block|}
name|loadPublished
argument_list|(
name|byKey
argument_list|,
name|newName
argument_list|)
expr_stmt|;
break|break;
case|case
name|REWRITE
case|:
break|break;
block|}
name|CurrentUser
name|user
init|=
name|userProvider
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|user
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
name|Account
operator|.
name|Id
name|me
init|=
name|user
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|changeType
condition|)
block|{
case|case
name|ADDED
case|:
case|case
name|MODIFIED
case|:
name|loadDrafts
argument_list|(
name|byKey
argument_list|,
name|me
argument_list|,
name|newName
argument_list|)
expr_stmt|;
break|break;
case|case
name|DELETED
case|:
name|loadDrafts
argument_list|(
name|byKey
argument_list|,
name|me
argument_list|,
name|newName
argument_list|)
expr_stmt|;
break|break;
case|case
name|COPIED
case|:
case|case
name|RENAMED
case|:
if|if
condition|(
name|psa
operator|!=
literal|null
condition|)
block|{
name|loadDrafts
argument_list|(
name|byKey
argument_list|,
name|me
argument_list|,
name|oldName
argument_list|)
expr_stmt|;
block|}
name|loadDrafts
argument_list|(
name|byKey
argument_list|,
name|me
argument_list|,
name|newName
argument_list|)
expr_stmt|;
break|break;
case|case
name|REWRITE
case|:
break|break;
block|}
block|}
block|}
block|}
DECL|method|loadPublished (Map<Patch.Key, Patch> byKey, String file)
specifier|private
name|void
name|loadPublished
parameter_list|(
name|Map
argument_list|<
name|Patch
operator|.
name|Key
argument_list|,
name|Patch
argument_list|>
name|byKey
parameter_list|,
name|String
name|file
parameter_list|)
block|{
for|for
control|(
name|Comment
name|c
range|:
name|commentsUtil
operator|.
name|publishedByChangeFile
argument_list|(
name|notes
argument_list|,
name|file
argument_list|)
control|)
block|{
name|comments
operator|.
name|include
argument_list|(
name|notes
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|PatchSet
operator|.
name|Id
name|psId
init|=
name|PatchSet
operator|.
name|id
argument_list|(
name|notes
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|c
operator|.
name|key
operator|.
name|patchSetId
argument_list|)
decl_stmt|;
name|Patch
operator|.
name|Key
name|pKey
init|=
name|Patch
operator|.
name|key
argument_list|(
name|psId
argument_list|,
name|c
operator|.
name|key
operator|.
name|filename
argument_list|)
decl_stmt|;
name|Patch
name|p
init|=
name|byKey
operator|.
name|get
argument_list|(
name|pKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|setCommentCount
argument_list|(
name|p
operator|.
name|getCommentCount
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|loadDrafts (Map<Patch.Key, Patch> byKey, Account.Id me, String file)
specifier|private
name|void
name|loadDrafts
parameter_list|(
name|Map
argument_list|<
name|Patch
operator|.
name|Key
argument_list|,
name|Patch
argument_list|>
name|byKey
parameter_list|,
name|Account
operator|.
name|Id
name|me
parameter_list|,
name|String
name|file
parameter_list|)
block|{
for|for
control|(
name|Comment
name|c
range|:
name|commentsUtil
operator|.
name|draftByChangeFileAuthor
argument_list|(
name|notes
argument_list|,
name|file
argument_list|,
name|me
argument_list|)
control|)
block|{
name|comments
operator|.
name|include
argument_list|(
name|notes
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|PatchSet
operator|.
name|Id
name|psId
init|=
name|PatchSet
operator|.
name|id
argument_list|(
name|notes
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|c
operator|.
name|key
operator|.
name|patchSetId
argument_list|)
decl_stmt|;
name|Patch
operator|.
name|Key
name|pKey
init|=
name|Patch
operator|.
name|key
argument_list|(
name|psId
argument_list|,
name|c
operator|.
name|key
operator|.
name|filename
argument_list|)
decl_stmt|;
name|Patch
name|p
init|=
name|byKey
operator|.
name|get
argument_list|(
name|pKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|setDraftCount
argument_list|(
name|p
operator|.
name|getDraftCount
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

