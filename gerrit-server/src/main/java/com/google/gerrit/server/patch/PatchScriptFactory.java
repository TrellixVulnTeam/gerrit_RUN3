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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Optional
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
name|AccountDiffPreference
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
name|AccountDiffPreference
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
name|PatchLineComment
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
name|PatchLineCommentsUtil
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
name|AccountInfoCacheFactory
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
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
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create ( ChangeControl control, String fileName, @Assisted(R) PatchSet.Id patchSetA, @Assisted(R) PatchSet.Id patchSetB, AccountDiffPreference diffPrefs)
name|PatchScriptFactory
name|create
parameter_list|(
name|ChangeControl
name|control
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
name|AccountDiffPreference
name|diffPrefs
parameter_list|)
function_decl|;
block|}
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PatchScriptFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
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
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|aicFactory
specifier|private
specifier|final
name|AccountInfoCacheFactory
operator|.
name|Factory
name|aicFactory
decl_stmt|;
DECL|field|plcUtil
specifier|private
specifier|final
name|PatchLineCommentsUtil
name|plcUtil
decl_stmt|;
DECL|field|fileName
specifier|private
specifier|final
name|String
name|fileName
decl_stmt|;
annotation|@
name|Nullable
DECL|field|psa
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|psa
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
name|AccountDiffPreference
name|diffPrefs
decl_stmt|;
DECL|field|editReader
specifier|private
specifier|final
name|ChangeEditUtil
name|editReader
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
DECL|field|change
specifier|private
name|Change
name|change
decl_stmt|;
DECL|field|projectKey
specifier|private
name|Project
operator|.
name|NameKey
name|projectKey
decl_stmt|;
DECL|field|control
specifier|private
name|ChangeControl
name|control
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
name|Inject
DECL|method|PatchScriptFactory (final GitRepositoryManager grm, Provider<PatchScriptBuilder> builderFactory, final PatchListCache patchListCache, final ReviewDb db, final AccountInfoCacheFactory.Factory aicFactory, PatchLineCommentsUtil plcUtil, ChangeEditUtil editReader, @Assisted ChangeControl control, @Assisted final String fileName, @Assisted(R) @Nullable final PatchSet.Id patchSetA, @Assisted(R) final PatchSet.Id patchSetB, @Assisted final AccountDiffPreference diffPrefs)
name|PatchScriptFactory
parameter_list|(
specifier|final
name|GitRepositoryManager
name|grm
parameter_list|,
name|Provider
argument_list|<
name|PatchScriptBuilder
argument_list|>
name|builderFactory
parameter_list|,
specifier|final
name|PatchListCache
name|patchListCache
parameter_list|,
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|AccountInfoCacheFactory
operator|.
name|Factory
name|aicFactory
parameter_list|,
name|PatchLineCommentsUtil
name|plcUtil
parameter_list|,
name|ChangeEditUtil
name|editReader
parameter_list|,
annotation|@
name|Assisted
name|ChangeControl
name|control
parameter_list|,
annotation|@
name|Assisted
specifier|final
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
specifier|final
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
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetB
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|AccountDiffPreference
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
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|control
operator|=
name|control
expr_stmt|;
name|this
operator|.
name|aicFactory
operator|=
name|aicFactory
expr_stmt|;
name|this
operator|.
name|plcUtil
operator|=
name|plcUtil
expr_stmt|;
name|this
operator|.
name|editReader
operator|=
name|editReader
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
name|getParentKey
argument_list|()
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
name|OrmException
throws|,
name|NoSuchChangeException
throws|,
name|LargeObjectException
throws|,
name|AuthException
throws|,
name|InvalidChangeOperationException
throws|,
name|IOException
block|{
name|validatePatchSetId
argument_list|(
name|psa
argument_list|)
expr_stmt|;
name|validatePatchSetId
argument_list|(
name|psb
argument_list|)
expr_stmt|;
name|change
operator|=
name|control
operator|.
name|getChange
argument_list|()
expr_stmt|;
name|projectKey
operator|=
name|change
operator|.
name|getProject
argument_list|()
expr_stmt|;
name|aId
operator|=
name|psa
operator|!=
literal|null
condition|?
name|toObjectId
argument_list|(
name|db
argument_list|,
name|psa
argument_list|)
else|:
literal|null
expr_stmt|;
name|bId
operator|=
name|toObjectId
argument_list|(
name|db
argument_list|,
name|psb
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|psa
operator|!=
literal|null
operator|&&
operator|!
name|control
operator|.
name|isPatchVisible
argument_list|(
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|psa
argument_list|)
argument_list|,
name|db
argument_list|)
operator|)
operator|||
operator|(
name|psb
operator|!=
literal|null
operator|&&
operator|!
name|control
operator|.
name|isPatchVisible
argument_list|(
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|psb
argument_list|)
argument_list|,
name|db
argument_list|)
operator|)
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
specifier|final
name|Repository
name|git
decl_stmt|;
try|try
block|{
name|git
operator|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|projectKey
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Repository "
operator|+
name|projectKey
operator|+
literal|" not found"
argument_list|,
name|e
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
name|log
operator|.
name|error
argument_list|(
literal|"Cannot open repository "
operator|+
name|projectKey
argument_list|,
name|e
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
name|getIgnoreWhitespace
argument_list|()
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
comment|//
name|content
operator|.
name|getOldName
argument_list|()
argument_list|,
comment|//
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
name|log
operator|.
name|error
argument_list|(
literal|"File content unavailable"
argument_list|,
name|e
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
finally|finally
block|{
name|git
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|keyFor (final Whitespace whitespace)
specifier|private
name|PatchListKey
name|keyFor
parameter_list|(
specifier|final
name|Whitespace
name|whitespace
parameter_list|)
block|{
return|return
operator|new
name|PatchListKey
argument_list|(
name|projectKey
argument_list|,
name|aId
argument_list|,
name|bId
argument_list|,
name|whitespace
argument_list|)
return|;
block|}
DECL|method|listFor (final PatchListKey key)
specifier|private
name|PatchList
name|listFor
parameter_list|(
specifier|final
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
argument_list|)
return|;
block|}
DECL|method|newBuilder (final PatchList list, Repository git)
specifier|private
name|PatchScriptBuilder
name|newBuilder
parameter_list|(
specifier|final
name|PatchList
name|list
parameter_list|,
name|Repository
name|git
parameter_list|)
block|{
specifier|final
name|AccountDiffPreference
name|dp
init|=
operator|new
name|AccountDiffPreference
argument_list|(
name|diffPrefs
argument_list|)
decl_stmt|;
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
name|projectKey
argument_list|)
expr_stmt|;
name|b
operator|.
name|setChange
argument_list|(
name|change
argument_list|)
expr_stmt|;
name|b
operator|.
name|setDiffPrefs
argument_list|(
name|dp
argument_list|)
expr_stmt|;
name|b
operator|.
name|setTrees
argument_list|(
name|list
operator|.
name|isAgainstParent
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
DECL|method|toObjectId (final ReviewDb db, final PatchSet.Id psId)
specifier|private
name|ObjectId
name|toObjectId
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
throws|throws
name|OrmException
throws|,
name|NoSuchChangeException
throws|,
name|AuthException
throws|,
name|InvalidChangeOperationException
throws|,
name|NoSuchChangeException
throws|,
name|IOException
block|{
if|if
condition|(
operator|!
name|changeId
operator|.
name|equals
argument_list|(
name|psId
operator|.
name|getParentKey
argument_list|()
argument_list|)
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
if|if
condition|(
name|psId
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
name|PatchSet
name|ps
init|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|psId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|==
literal|null
operator|||
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
name|log
operator|.
name|error
argument_list|(
literal|"Patch set "
operator|+
name|psId
operator|+
literal|" has invalid revision"
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
name|NoSuchChangeException
throws|,
name|IOException
throws|,
name|InvalidChangeOperationException
block|{
name|Optional
argument_list|<
name|ChangeEdit
argument_list|>
name|edit
init|=
name|editReader
operator|.
name|byChange
argument_list|(
name|change
argument_list|)
decl_stmt|;
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
name|getRef
argument_list|()
operator|.
name|getObjectId
argument_list|()
return|;
block|}
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|)
throw|;
block|}
DECL|method|validatePatchSetId (final PatchSet.Id psId)
specifier|private
name|void
name|validatePatchSetId
parameter_list|(
specifier|final
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
name|getParentKey
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
DECL|method|loadCommentsAndHistory (final ChangeType changeType, final String oldName, final String newName)
specifier|private
name|void
name|loadCommentsAndHistory
parameter_list|(
specifier|final
name|ChangeType
name|changeType
parameter_list|,
specifier|final
name|String
name|oldName
parameter_list|,
specifier|final
name|String
name|newName
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
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
specifier|final
name|PatchSet
name|ps
range|:
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|byChange
argument_list|(
name|changeId
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|control
operator|.
name|isPatchVisible
argument_list|(
name|ps
argument_list|,
name|db
argument_list|)
condition|)
block|{
continue|continue;
block|}
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
specifier|final
name|Patch
name|p
init|=
operator|new
name|Patch
argument_list|(
operator|new
name|Patch
operator|.
name|Key
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
block|}
if|if
condition|(
name|loadComments
condition|)
block|{
specifier|final
name|AccountInfoCacheFactory
name|aic
init|=
name|aicFactory
operator|.
name|create
argument_list|()
decl_stmt|;
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
name|aic
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
name|aic
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
name|aic
argument_list|,
name|oldName
argument_list|)
expr_stmt|;
block|}
name|loadPublished
argument_list|(
name|byKey
argument_list|,
name|aic
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
specifier|final
name|CurrentUser
name|user
init|=
name|control
operator|.
name|getCurrentUser
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
specifier|final
name|Account
operator|.
name|Id
name|me
init|=
operator|(
operator|(
name|IdentifiedUser
operator|)
name|user
operator|)
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
name|aic
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
name|aic
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
name|aic
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
name|aic
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
name|comments
operator|.
name|setAccountInfoCache
argument_list|(
name|aic
operator|.
name|create
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|loadPublished (final Map<Patch.Key, Patch> byKey, final AccountInfoCacheFactory aic, final String file)
specifier|private
name|void
name|loadPublished
parameter_list|(
specifier|final
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
specifier|final
name|AccountInfoCacheFactory
name|aic
parameter_list|,
specifier|final
name|String
name|file
parameter_list|)
throws|throws
name|OrmException
block|{
name|ChangeNotes
name|notes
init|=
name|control
operator|.
name|getNotes
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchLineComment
name|c
range|:
name|plcUtil
operator|.
name|publishedByChangeFile
argument_list|(
name|db
argument_list|,
name|notes
argument_list|,
name|changeId
argument_list|,
name|file
argument_list|)
control|)
block|{
if|if
condition|(
name|comments
operator|.
name|include
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|aic
operator|.
name|want
argument_list|(
name|c
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Patch
operator|.
name|Key
name|pKey
init|=
name|c
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
specifier|final
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
DECL|method|loadDrafts (final Map<Patch.Key, Patch> byKey, final AccountInfoCacheFactory aic, final Account.Id me, final String file)
specifier|private
name|void
name|loadDrafts
parameter_list|(
specifier|final
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
specifier|final
name|AccountInfoCacheFactory
name|aic
parameter_list|,
specifier|final
name|Account
operator|.
name|Id
name|me
parameter_list|,
specifier|final
name|String
name|file
parameter_list|)
throws|throws
name|OrmException
block|{
for|for
control|(
name|PatchLineComment
name|c
range|:
name|plcUtil
operator|.
name|draftByChangeFileAuthor
argument_list|(
name|db
argument_list|,
name|control
operator|.
name|getNotes
argument_list|()
argument_list|,
name|file
argument_list|,
name|me
argument_list|)
control|)
block|{
if|if
condition|(
name|comments
operator|.
name|include
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|aic
operator|.
name|want
argument_list|(
name|me
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Patch
operator|.
name|Key
name|pKey
init|=
name|c
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
specifier|final
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

