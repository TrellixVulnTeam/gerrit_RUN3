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
DECL|package|com.google.gerrit.server.rpc.patch
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|rpc
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
name|gerrit
operator|.
name|client
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
name|client
operator|.
name|data
operator|.
name|PatchScriptSettings
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
name|data
operator|.
name|PatchScriptSettings
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
name|client
operator|.
name|patches
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
name|client
operator|.
name|reviewdb
operator|.
name|AccountGeneralPreferences
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
name|reviewdb
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
name|client
operator|.
name|reviewdb
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
name|client
operator|.
name|reviewdb
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
name|client
operator|.
name|reviewdb
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
name|client
operator|.
name|reviewdb
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
name|client
operator|.
name|reviewdb
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
name|client
operator|.
name|rpc
operator|.
name|CorruptEntityException
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
name|FileTypeRegistry
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
name|GerritServer
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
name|config
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
name|server
operator|.
name|patch
operator|.
name|DiffCache
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
name|DiffCacheContent
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
name|DiffCacheKey
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
name|rpc
operator|.
name|Handler
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
name|client
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
name|org
operator|.
name|spearce
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
name|spearce
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
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
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
name|spearce
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|RevCommit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|RevWalk
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
DECL|class|PatchScriptFactory
class|class
name|PatchScriptFactory
extends|extends
name|Handler
argument_list|<
name|PatchScript
argument_list|>
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (Patch.Key patchKey, @Assisted(R) PatchSet.Id patchSetA, @Assisted(R) PatchSet.Id patchSetB, PatchScriptSettings settings)
name|PatchScriptFactory
name|create
parameter_list|(
name|Patch
operator|.
name|Key
name|patchKey
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
name|PatchScriptSettings
name|settings
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
DECL|field|server
specifier|private
specifier|final
name|GerritServer
name|server
decl_stmt|;
DECL|field|registry
specifier|private
specifier|final
name|FileTypeRegistry
name|registry
decl_stmt|;
DECL|field|diffCache
specifier|private
specifier|final
name|DiffCache
name|diffCache
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|changeControlFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
decl_stmt|;
DECL|field|patchKey
specifier|private
specifier|final
name|Patch
operator|.
name|Key
name|patchKey
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
DECL|field|settings
specifier|private
specifier|final
name|PatchScriptSettings
name|settings
decl_stmt|;
DECL|field|patchSetId
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
decl_stmt|;
DECL|field|changeId
specifier|private
specifier|final
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
DECL|field|change
specifier|private
name|Change
name|change
decl_stmt|;
DECL|field|patch
specifier|private
name|Patch
name|patch
decl_stmt|;
DECL|field|projectKey
specifier|private
name|Project
operator|.
name|NameKey
name|projectKey
decl_stmt|;
DECL|field|git
specifier|private
name|Repository
name|git
decl_stmt|;
DECL|field|control
specifier|private
name|ChangeControl
name|control
decl_stmt|;
annotation|@
name|Inject
DECL|method|PatchScriptFactory (final GerritServer gs, final FileTypeRegistry ftr, final DiffCache dc, final ReviewDb db, final ChangeControl.Factory changeControlFactory, @Assisted final Patch.Key patchKey, @Assisted(R) @Nullable final PatchSet.Id patchSetA, @Assisted(R) final PatchSet.Id patchSetB, @Assisted final PatchScriptSettings settings)
name|PatchScriptFactory
parameter_list|(
specifier|final
name|GerritServer
name|gs
parameter_list|,
specifier|final
name|FileTypeRegistry
name|ftr
parameter_list|,
specifier|final
name|DiffCache
name|dc
parameter_list|,
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
parameter_list|,
annotation|@
name|Assisted
specifier|final
name|Patch
operator|.
name|Key
name|patchKey
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
name|PatchScriptSettings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|server
operator|=
name|gs
expr_stmt|;
name|this
operator|.
name|registry
operator|=
name|ftr
expr_stmt|;
name|this
operator|.
name|diffCache
operator|=
name|dc
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|changeControlFactory
operator|=
name|changeControlFactory
expr_stmt|;
name|this
operator|.
name|patchKey
operator|=
name|patchKey
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
name|settings
operator|=
name|settings
expr_stmt|;
name|patchSetId
operator|=
name|patchKey
operator|.
name|getParentKey
argument_list|()
expr_stmt|;
name|changeId
operator|=
name|patchSetId
operator|.
name|getParentKey
argument_list|()
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
name|control
operator|=
name|changeControlFactory
operator|.
name|validateFor
argument_list|(
name|changeId
argument_list|)
expr_stmt|;
name|change
operator|=
name|control
operator|.
name|getChange
argument_list|()
expr_stmt|;
name|patch
operator|=
name|db
operator|.
name|patches
argument_list|()
operator|.
name|get
argument_list|(
name|patchKey
argument_list|)
expr_stmt|;
if|if
condition|(
name|patch
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
name|projectKey
operator|=
name|change
operator|.
name|getDest
argument_list|()
operator|.
name|getParentKey
argument_list|()
expr_stmt|;
try|try
block|{
name|git
operator|=
name|server
operator|.
name|openRepository
argument_list|(
name|projectKey
operator|.
name|get
argument_list|()
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
try|try
block|{
specifier|final
name|PatchScriptBuilder
name|b
init|=
name|newBuilder
argument_list|()
decl_stmt|;
specifier|final
name|ObjectId
name|bId
init|=
name|toObjectId
argument_list|(
name|db
argument_list|,
name|psb
argument_list|)
decl_stmt|;
specifier|final
name|ObjectId
name|aId
init|=
name|psa
operator|==
literal|null
condition|?
name|ancestor
argument_list|(
name|bId
argument_list|)
else|:
name|toObjectId
argument_list|(
name|db
argument_list|,
name|psa
argument_list|)
decl_stmt|;
specifier|final
name|DiffCacheKey
name|key
init|=
name|keyFor
argument_list|(
name|bId
argument_list|,
name|aId
argument_list|)
decl_stmt|;
specifier|final
name|DiffCacheContent
name|contentWS
init|=
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
specifier|final
name|CommentDetail
name|comments
init|=
name|allComments
argument_list|(
name|db
argument_list|)
decl_stmt|;
specifier|final
name|DiffCacheContent
name|contentActual
decl_stmt|;
if|if
condition|(
name|settings
operator|.
name|getWhitespace
argument_list|()
operator|!=
name|Whitespace
operator|.
name|IGNORE_NONE
condition|)
block|{
comment|// If we are ignoring whitespace in some form, we still need to know
comment|// where the post-image differs so we can ensure the post-image lines
comment|// are still packed for the client to display.
comment|//
specifier|final
name|PatchScriptSettings
name|s
init|=
operator|new
name|PatchScriptSettings
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|s
operator|.
name|setWhitespace
argument_list|(
name|Whitespace
operator|.
name|IGNORE_NONE
argument_list|)
expr_stmt|;
name|contentActual
operator|=
name|get
argument_list|(
operator|new
name|DiffCacheKey
argument_list|(
name|projectKey
argument_list|,
name|aId
argument_list|,
name|bId
argument_list|,
name|patch
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|contentActual
operator|=
name|contentWS
expr_stmt|;
block|}
try|try
block|{
return|return
name|b
operator|.
name|toPatchScript
argument_list|(
name|key
argument_list|,
name|contentWS
argument_list|,
name|comments
argument_list|,
name|contentActual
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|CorruptEntityException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"File content for "
operator|+
name|key
operator|+
literal|" unavailable"
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
DECL|method|keyFor (final ObjectId bId, final ObjectId aId)
specifier|private
name|DiffCacheKey
name|keyFor
parameter_list|(
specifier|final
name|ObjectId
name|bId
parameter_list|,
specifier|final
name|ObjectId
name|aId
parameter_list|)
block|{
return|return
operator|new
name|DiffCacheKey
argument_list|(
name|projectKey
argument_list|,
name|aId
argument_list|,
name|bId
argument_list|,
name|patch
argument_list|,
name|settings
argument_list|)
return|;
block|}
DECL|method|get (final DiffCacheKey key)
specifier|private
name|DiffCacheContent
name|get
parameter_list|(
specifier|final
name|DiffCacheKey
name|key
parameter_list|)
throws|throws
name|NoSuchChangeException
block|{
specifier|final
name|DiffCacheContent
name|r
init|=
name|diffCache
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cache get failed for "
operator|+
name|key
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|)
throw|;
block|}
return|return
name|r
return|;
block|}
DECL|method|newBuilder ()
specifier|private
name|PatchScriptBuilder
name|newBuilder
parameter_list|()
throws|throws
name|NoSuchChangeException
block|{
specifier|final
name|PatchScriptSettings
name|s
init|=
operator|new
name|PatchScriptSettings
argument_list|(
name|settings
argument_list|)
decl_stmt|;
specifier|final
name|int
name|ctx
init|=
name|settings
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|ctx
operator|==
name|AccountGeneralPreferences
operator|.
name|WHOLE_FILE_CONTEXT
condition|)
name|s
operator|.
name|setContext
argument_list|(
name|PatchScriptBuilder
operator|.
name|MAX_CONTEXT
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
literal|0
operator|<=
name|ctx
operator|&&
name|ctx
operator|<=
name|PatchScriptBuilder
operator|.
name|MAX_CONTEXT
condition|)
name|s
operator|.
name|setContext
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
else|else
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|)
throw|;
specifier|final
name|PatchScriptBuilder
name|b
init|=
operator|new
name|PatchScriptBuilder
argument_list|(
name|registry
argument_list|)
decl_stmt|;
name|b
operator|.
name|setRepository
argument_list|(
name|git
argument_list|)
expr_stmt|;
name|b
operator|.
name|setPatch
argument_list|(
name|patch
argument_list|)
expr_stmt|;
name|b
operator|.
name|setSettings
argument_list|(
name|s
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
DECL|method|ancestor (final ObjectId id)
specifier|private
name|ObjectId
name|ancestor
parameter_list|(
specifier|final
name|ObjectId
name|id
parameter_list|)
throws|throws
name|NoSuchChangeException
block|{
try|try
block|{
specifier|final
name|RevCommit
name|c
init|=
operator|new
name|RevWalk
argument_list|(
name|git
argument_list|)
operator|.
name|parseCommit
argument_list|(
name|id
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|c
operator|.
name|getParentCount
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
name|emptyTree
argument_list|()
return|;
case|case
literal|1
case|:
return|return
name|c
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
return|;
default|default:
return|return
literal|null
return|;
block|}
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
literal|"Commit information for "
operator|+
name|id
operator|.
name|name
argument_list|()
operator|+
literal|" unavailable"
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
block|}
DECL|method|emptyTree ()
specifier|private
name|ObjectId
name|emptyTree
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|ObjectWriter
argument_list|(
name|git
argument_list|)
operator|.
name|writeCanonicalTree
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
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
specifier|final
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
DECL|method|allComments (final ReviewDb db)
specifier|private
name|CommentDetail
name|allComments
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|CommentDetail
name|r
init|=
operator|new
name|CommentDetail
argument_list|(
name|psa
argument_list|,
name|psb
argument_list|)
decl_stmt|;
specifier|final
name|String
name|pn
init|=
name|patchKey
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchLineComment
name|p
range|:
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|published
argument_list|(
name|changeId
argument_list|,
name|pn
argument_list|)
control|)
block|{
name|r
operator|.
name|include
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|control
operator|.
name|getCurrentUser
argument_list|()
operator|instanceof
name|IdentifiedUser
condition|)
block|{
for|for
control|(
name|PatchLineComment
name|p
range|:
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|draft
argument_list|(
name|changeId
argument_list|,
name|pn
argument_list|,
operator|(
operator|(
name|IdentifiedUser
operator|)
name|control
operator|.
name|getCurrentUser
argument_list|()
operator|)
operator|.
name|getAccountId
argument_list|()
argument_list|)
control|)
block|{
name|r
operator|.
name|include
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|r
return|;
block|}
block|}
end_class

end_unit

