begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2019 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|git
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
name|annotations
operator|.
name|VisibleForTesting
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
name|base
operator|.
name|Throwables
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
name|cache
operator|.
name|CacheLoader
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
name|cache
operator|.
name|LoadingCache
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
name|server
operator|.
name|cache
operator|.
name|CacheModule
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
name|cache
operator|.
name|proto
operator|.
name|Cache
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
name|cache
operator|.
name|proto
operator|.
name|Cache
operator|.
name|PureRevertKeyProto
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
name|cache
operator|.
name|serialize
operator|.
name|BooleanCacheSerializer
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
name|cache
operator|.
name|serialize
operator|.
name|ObjectIdConverter
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
name|cache
operator|.
name|serialize
operator|.
name|ProtobufSerializer
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
name|logging
operator|.
name|TraceContext
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
name|Module
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|name
operator|.
name|Named
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ByteString
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|List
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
name|ExecutionException
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
name|diff
operator|.
name|DiffEntry
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
name|diff
operator|.
name|DiffFormatter
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
name|InvalidObjectIdException
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
name|MissingObjectException
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
name|ObjectInserter
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
name|eclipse
operator|.
name|jgit
operator|.
name|merge
operator|.
name|ThreeWayMerger
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
name|revwalk
operator|.
name|RevCommit
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
name|revwalk
operator|.
name|RevWalk
import|;
end_import

begin_comment
comment|/** Computes and caches if a change is a pure revert of another change. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|PureRevertCache
specifier|public
class|class
name|PureRevertCache
block|{
DECL|field|ID_CACHE
specifier|private
specifier|static
specifier|final
name|String
name|ID_CACHE
init|=
literal|"pure_revert"
decl_stmt|;
DECL|method|module ()
specifier|public
specifier|static
name|Module
name|module
parameter_list|()
block|{
return|return
operator|new
name|CacheModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|persist
argument_list|(
name|ID_CACHE
argument_list|,
name|Cache
operator|.
name|PureRevertKeyProto
operator|.
name|class
argument_list|,
name|Boolean
operator|.
name|class
argument_list|)
operator|.
name|maximumWeight
argument_list|(
literal|100
argument_list|)
operator|.
name|loader
argument_list|(
name|Loader
operator|.
name|class
argument_list|)
operator|.
name|version
argument_list|(
literal|1
argument_list|)
operator|.
name|keySerializer
argument_list|(
operator|new
name|ProtobufSerializer
argument_list|<>
argument_list|(
name|Cache
operator|.
name|PureRevertKeyProto
operator|.
name|parser
argument_list|()
argument_list|)
argument_list|)
operator|.
name|valueSerializer
argument_list|(
name|BooleanCacheSerializer
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|field|cache
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|PureRevertKeyProto
argument_list|,
name|Boolean
argument_list|>
name|cache
decl_stmt|;
DECL|field|notesFactory
specifier|private
specifier|final
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|PureRevertCache ( @amedID_CACHE) LoadingCache<PureRevertKeyProto, Boolean> cache, ChangeNotes.Factory notesFactory)
name|PureRevertCache
parameter_list|(
annotation|@
name|Named
argument_list|(
name|ID_CACHE
argument_list|)
name|LoadingCache
argument_list|<
name|PureRevertKeyProto
argument_list|,
name|Boolean
argument_list|>
name|cache
parameter_list|,
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
name|this
operator|.
name|notesFactory
operator|=
name|notesFactory
expr_stmt|;
block|}
comment|/**    * Returns {@code true} if {@code claimedRevert} is a pure (clean) revert of the change that is    * referenced in {@link Change#getRevertOf()}.    *    * @return {@code true} if {@code claimedRevert} is a pure (clean) revert.    * @throws IOException if there was a problem with the storage layer    * @throws BadRequestException if there is a problem with the provided {@link ChangeNotes}    */
DECL|method|isPureRevert (ChangeNotes claimedRevert)
specifier|public
name|boolean
name|isPureRevert
parameter_list|(
name|ChangeNotes
name|claimedRevert
parameter_list|)
throws|throws
name|IOException
throws|,
name|BadRequestException
block|{
if|if
condition|(
name|claimedRevert
operator|.
name|getChange
argument_list|()
operator|.
name|getRevertOf
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"revertOf not set"
argument_list|)
throw|;
block|}
name|ChangeNotes
name|claimedOriginal
init|=
name|notesFactory
operator|.
name|createChecked
argument_list|(
name|claimedRevert
operator|.
name|getProjectName
argument_list|()
argument_list|,
name|claimedRevert
operator|.
name|getChange
argument_list|()
operator|.
name|getRevertOf
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|isPureRevert
argument_list|(
name|claimedRevert
operator|.
name|getProjectName
argument_list|()
argument_list|,
name|claimedRevert
operator|.
name|getCurrentPatchSet
argument_list|()
operator|.
name|getCommitId
argument_list|()
argument_list|,
name|claimedOriginal
operator|.
name|getCurrentPatchSet
argument_list|()
operator|.
name|getCommitId
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns {@code true} if {@code claimedRevert} is a pure (clean) revert of {@code    * claimedOriginal}.    *    * @return {@code true} if {@code claimedRevert} is a pure (clean) revert of {@code    *     claimedOriginal}.    * @throws IOException if there was a problem with the storage layer    * @throws BadRequestException if there is a problem with the provided {@link ObjectId}s    */
DECL|method|isPureRevert ( Project.NameKey project, ObjectId claimedRevert, ObjectId claimedOriginal)
specifier|public
name|boolean
name|isPureRevert
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|ObjectId
name|claimedRevert
parameter_list|,
name|ObjectId
name|claimedOriginal
parameter_list|)
throws|throws
name|IOException
throws|,
name|BadRequestException
block|{
try|try
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|(
name|project
argument_list|,
name|claimedRevert
argument_list|,
name|claimedOriginal
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|Throwables
operator|.
name|throwIfInstanceOf
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|,
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|key ( Project.NameKey project, ObjectId claimedRevert, ObjectId claimedOriginal)
specifier|static
name|PureRevertKeyProto
name|key
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|ObjectId
name|claimedRevert
parameter_list|,
name|ObjectId
name|claimedOriginal
parameter_list|)
block|{
name|ByteString
name|original
init|=
name|ObjectIdConverter
operator|.
name|create
argument_list|()
operator|.
name|toByteString
argument_list|(
name|claimedOriginal
argument_list|)
decl_stmt|;
name|ByteString
name|revert
init|=
name|ObjectIdConverter
operator|.
name|create
argument_list|()
operator|.
name|toByteString
argument_list|(
name|claimedRevert
argument_list|)
decl_stmt|;
return|return
name|PureRevertKeyProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setProject
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|setClaimedOriginal
argument_list|(
name|original
argument_list|)
operator|.
name|setClaimedRevert
argument_list|(
name|revert
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|class|Loader
specifier|static
class|class
name|Loader
extends|extends
name|CacheLoader
argument_list|<
name|PureRevertKeyProto
argument_list|,
name|Boolean
argument_list|>
block|{
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|mergeUtilFactory
specifier|private
specifier|final
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|Loader ( GitRepositoryManager repoManager, MergeUtil.Factory mergeUtilFactory, ProjectCache projectCache)
name|Loader
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|)
block|{
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|mergeUtilFactory
operator|=
name|mergeUtilFactory
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load (PureRevertKeyProto key)
specifier|public
name|Boolean
name|load
parameter_list|(
name|PureRevertKeyProto
name|key
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|IOException
block|{
try|try
init|(
name|TraceContext
operator|.
name|TraceTimer
name|ignored
init|=
name|TraceContext
operator|.
name|newTimer
argument_list|(
literal|"Loading pure revert for %s"
argument_list|,
name|key
argument_list|)
init|)
block|{
name|ObjectId
name|original
init|=
name|ObjectIdConverter
operator|.
name|create
argument_list|()
operator|.
name|fromByteString
argument_list|(
name|key
operator|.
name|getClaimedOriginal
argument_list|()
argument_list|)
decl_stmt|;
name|ObjectId
name|revert
init|=
name|ObjectIdConverter
operator|.
name|create
argument_list|()
operator|.
name|fromByteString
argument_list|(
name|key
operator|.
name|getClaimedRevert
argument_list|()
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|project
init|=
name|Project
operator|.
name|nameKey
argument_list|(
name|key
operator|.
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
init|;
name|ObjectInserter
name|oi
operator|=
name|repo
operator|.
name|newObjectInserter
argument_list|()
init|;
name|RevWalk
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
init|)
block|{
name|RevCommit
name|claimedOriginalCommit
decl_stmt|;
try|try
block|{
name|claimedOriginalCommit
operator|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|original
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidObjectIdException
decl||
name|MissingObjectException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"invalid object ID"
argument_list|)
throw|;
block|}
if|if
condition|(
name|claimedOriginalCommit
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"can't check against initial commit"
argument_list|)
throw|;
block|}
name|RevCommit
name|claimedRevertCommit
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|revert
argument_list|)
decl_stmt|;
if|if
condition|(
name|claimedRevertCommit
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Rebase claimed revert onto claimed original
name|ThreeWayMerger
name|merger
init|=
name|mergeUtilFactory
operator|.
name|create
argument_list|(
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|project
argument_list|)
argument_list|)
operator|.
name|newThreeWayMerger
argument_list|(
name|oi
argument_list|,
name|repo
operator|.
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|merger
operator|.
name|setBase
argument_list|(
name|claimedRevertCommit
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
name|merger
operator|.
name|merge
argument_list|(
name|claimedRevertCommit
argument_list|,
name|claimedOriginalCommit
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
operator|||
name|merger
operator|.
name|getResultTreeId
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// Merge conflict during rebase
return|return
literal|false
return|;
block|}
comment|// Any differences between claimed original's parent and the rebase result indicate that
comment|// the
comment|// claimedRevert is not a pure revert but made content changes
try|try
init|(
name|DiffFormatter
name|df
init|=
operator|new
name|DiffFormatter
argument_list|(
operator|new
name|ByteArrayOutputStream
argument_list|()
argument_list|)
init|)
block|{
name|df
operator|.
name|setReader
argument_list|(
name|oi
operator|.
name|newReader
argument_list|()
argument_list|,
name|repo
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DiffEntry
argument_list|>
name|entries
init|=
name|df
operator|.
name|scan
argument_list|(
name|claimedOriginalCommit
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|,
name|merger
operator|.
name|getResultTreeId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|entries
operator|.
name|isEmpty
argument_list|()
return|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

