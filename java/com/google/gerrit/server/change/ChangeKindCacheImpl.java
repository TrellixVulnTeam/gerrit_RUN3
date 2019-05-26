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
name|auto
operator|.
name|value
operator|.
name|AutoValue
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
name|cache
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
name|common
operator|.
name|cache
operator|.
name|Weigher
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
name|collect
operator|.
name|FluentIterable
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
name|entities
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
name|entities
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
name|entities
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
name|exceptions
operator|.
name|StorageException
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
name|ChangeKind
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
name|proto
operator|.
name|Protos
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
operator|.
name|ChangeKindKeyProto
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
name|CacheSerializer
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
name|EnumCacheSerializer
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
name|config
operator|.
name|GerritServerConfig
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
name|InMemoryInserter
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
name|MergeUtil
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
name|name
operator|.
name|Named
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
name|Arrays
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
name|Objects
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
name|errors
operator|.
name|LargeObjectException
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
name|AnyObjectId
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
name|Config
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

begin_class
DECL|class|ChangeKindCacheImpl
specifier|public
class|class
name|ChangeKindCacheImpl
implements|implements
name|ChangeKindCache
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
DECL|field|ID_CACHE
specifier|private
specifier|static
specifier|final
name|String
name|ID_CACHE
init|=
literal|"change_kind"
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
name|bind
argument_list|(
name|ChangeKindCache
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|ChangeKindCacheImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|persist
argument_list|(
name|ID_CACHE
argument_list|,
name|Key
operator|.
name|class
argument_list|,
name|ChangeKind
operator|.
name|class
argument_list|)
operator|.
name|maximumWeight
argument_list|(
literal|2
operator|<<
literal|20
argument_list|)
operator|.
name|weigher
argument_list|(
name|ChangeKindWeigher
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
name|Key
operator|.
name|Serializer
argument_list|()
argument_list|)
operator|.
name|valueSerializer
argument_list|(
operator|new
name|EnumCacheSerializer
argument_list|<>
argument_list|(
name|ChangeKind
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|class|NoCache
specifier|public
specifier|static
class|class
name|NoCache
implements|implements
name|ChangeKindCache
block|{
DECL|field|useRecursiveMerge
specifier|private
specifier|final
name|boolean
name|useRecursiveMerge
decl_stmt|;
DECL|field|changeDataFactory
specifier|private
specifier|final
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
annotation|@
name|Inject
DECL|method|NoCache ( @erritServerConfig Config serverConfig, ChangeData.Factory changeDataFactory, GitRepositoryManager repoManager)
name|NoCache
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|serverConfig
parameter_list|,
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|)
block|{
name|this
operator|.
name|useRecursiveMerge
operator|=
name|MergeUtil
operator|.
name|useRecursiveMerge
argument_list|(
name|serverConfig
argument_list|)
expr_stmt|;
name|this
operator|.
name|changeDataFactory
operator|=
name|changeDataFactory
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getChangeKind ( Project.NameKey project, @Nullable RevWalk rw, @Nullable Config repoConfig, ObjectId prior, ObjectId next)
specifier|public
name|ChangeKind
name|getChangeKind
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
annotation|@
name|Nullable
name|RevWalk
name|rw
parameter_list|,
annotation|@
name|Nullable
name|Config
name|repoConfig
parameter_list|,
name|ObjectId
name|prior
parameter_list|,
name|ObjectId
name|next
parameter_list|)
block|{
try|try
block|{
name|Key
name|key
init|=
name|Key
operator|.
name|create
argument_list|(
name|prior
argument_list|,
name|next
argument_list|,
name|useRecursiveMerge
argument_list|)
decl_stmt|;
return|return
operator|new
name|Loader
argument_list|(
name|key
argument_list|,
name|repoManager
argument_list|,
name|project
argument_list|,
name|rw
argument_list|,
name|repoConfig
argument_list|)
operator|.
name|call
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atWarning
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Cannot check trivial rebase of new patch set %s in %s"
argument_list|,
name|next
operator|.
name|name
argument_list|()
argument_list|,
name|project
argument_list|)
expr_stmt|;
return|return
name|ChangeKind
operator|.
name|REWORK
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getChangeKind (Change change, PatchSet patch)
specifier|public
name|ChangeKind
name|getChangeKind
parameter_list|(
name|Change
name|change
parameter_list|,
name|PatchSet
name|patch
parameter_list|)
block|{
return|return
name|getChangeKindInternal
argument_list|(
name|this
argument_list|,
name|change
argument_list|,
name|patch
argument_list|,
name|changeDataFactory
argument_list|,
name|repoManager
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getChangeKind ( @ullable RevWalk rw, @Nullable Config repoConfig, ChangeData cd, PatchSet patch)
specifier|public
name|ChangeKind
name|getChangeKind
parameter_list|(
annotation|@
name|Nullable
name|RevWalk
name|rw
parameter_list|,
annotation|@
name|Nullable
name|Config
name|repoConfig
parameter_list|,
name|ChangeData
name|cd
parameter_list|,
name|PatchSet
name|patch
parameter_list|)
block|{
return|return
name|getChangeKindInternal
argument_list|(
name|this
argument_list|,
name|rw
argument_list|,
name|repoConfig
argument_list|,
name|cd
argument_list|,
name|patch
argument_list|)
return|;
block|}
block|}
annotation|@
name|AutoValue
DECL|class|Key
specifier|public
specifier|abstract
specifier|static
class|class
name|Key
block|{
DECL|method|create (AnyObjectId prior, AnyObjectId next, String strategyName)
specifier|public
specifier|static
name|Key
name|create
parameter_list|(
name|AnyObjectId
name|prior
parameter_list|,
name|AnyObjectId
name|next
parameter_list|,
name|String
name|strategyName
parameter_list|)
block|{
return|return
operator|new
name|AutoValue_ChangeKindCacheImpl_Key
argument_list|(
name|prior
operator|.
name|copy
argument_list|()
argument_list|,
name|next
operator|.
name|copy
argument_list|()
argument_list|,
name|strategyName
argument_list|)
return|;
block|}
DECL|method|create (AnyObjectId prior, AnyObjectId next, boolean useRecursiveMerge)
specifier|private
specifier|static
name|Key
name|create
parameter_list|(
name|AnyObjectId
name|prior
parameter_list|,
name|AnyObjectId
name|next
parameter_list|,
name|boolean
name|useRecursiveMerge
parameter_list|)
block|{
return|return
name|create
argument_list|(
name|prior
argument_list|,
name|next
argument_list|,
name|MergeUtil
operator|.
name|mergeStrategyName
argument_list|(
literal|true
argument_list|,
name|useRecursiveMerge
argument_list|)
argument_list|)
return|;
block|}
DECL|method|prior ()
specifier|public
specifier|abstract
name|ObjectId
name|prior
parameter_list|()
function_decl|;
DECL|method|next ()
specifier|public
specifier|abstract
name|ObjectId
name|next
parameter_list|()
function_decl|;
DECL|method|strategyName ()
specifier|public
specifier|abstract
name|String
name|strategyName
parameter_list|()
function_decl|;
annotation|@
name|VisibleForTesting
DECL|class|Serializer
specifier|static
class|class
name|Serializer
implements|implements
name|CacheSerializer
argument_list|<
name|Key
argument_list|>
block|{
annotation|@
name|Override
DECL|method|serialize (Key object)
specifier|public
name|byte
index|[]
name|serialize
parameter_list|(
name|Key
name|object
parameter_list|)
block|{
name|ObjectIdConverter
name|idConverter
init|=
name|ObjectIdConverter
operator|.
name|create
argument_list|()
decl_stmt|;
return|return
name|Protos
operator|.
name|toByteArray
argument_list|(
name|ChangeKindKeyProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setPrior
argument_list|(
name|idConverter
operator|.
name|toByteString
argument_list|(
name|object
operator|.
name|prior
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setNext
argument_list|(
name|idConverter
operator|.
name|toByteString
argument_list|(
name|object
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setStrategyName
argument_list|(
name|object
operator|.
name|strategyName
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|deserialize (byte[] in)
specifier|public
name|Key
name|deserialize
parameter_list|(
name|byte
index|[]
name|in
parameter_list|)
block|{
name|ChangeKindKeyProto
name|proto
init|=
name|Protos
operator|.
name|parseUnchecked
argument_list|(
name|ChangeKindKeyProto
operator|.
name|parser
argument_list|()
argument_list|,
name|in
argument_list|)
decl_stmt|;
name|ObjectIdConverter
name|idConverter
init|=
name|ObjectIdConverter
operator|.
name|create
argument_list|()
decl_stmt|;
return|return
name|create
argument_list|(
name|idConverter
operator|.
name|fromByteString
argument_list|(
name|proto
operator|.
name|getPrior
argument_list|()
argument_list|)
argument_list|,
name|idConverter
operator|.
name|fromByteString
argument_list|(
name|proto
operator|.
name|getNext
argument_list|()
argument_list|)
argument_list|,
name|proto
operator|.
name|getStrategyName
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
DECL|class|Loader
specifier|private
specifier|static
class|class
name|Loader
implements|implements
name|Callable
argument_list|<
name|ChangeKind
argument_list|>
block|{
DECL|field|key
specifier|private
specifier|final
name|Key
name|key
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|projectName
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|projectName
decl_stmt|;
DECL|field|alreadyOpenRw
specifier|private
specifier|final
name|RevWalk
name|alreadyOpenRw
decl_stmt|;
DECL|field|repoConfig
specifier|private
specifier|final
name|Config
name|repoConfig
decl_stmt|;
DECL|method|Loader ( Key key, GitRepositoryManager repoManager, Project.NameKey projectName, @Nullable RevWalk rw, @Nullable Config repoConfig)
specifier|private
name|Loader
parameter_list|(
name|Key
name|key
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
annotation|@
name|Nullable
name|RevWalk
name|rw
parameter_list|,
annotation|@
name|Nullable
name|Config
name|repoConfig
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|(
name|rw
operator|==
literal|null
operator|&&
name|repoConfig
operator|==
literal|null
operator|)
operator|||
operator|(
name|rw
operator|!=
literal|null
operator|&&
name|repoConfig
operator|!=
literal|null
operator|)
argument_list|,
literal|"must either provide both revwalk/config, or neither; got %s/%s"
argument_list|,
name|rw
argument_list|,
name|repoConfig
argument_list|)
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|projectName
operator|=
name|projectName
expr_stmt|;
name|this
operator|.
name|alreadyOpenRw
operator|=
name|rw
expr_stmt|;
name|this
operator|.
name|repoConfig
operator|=
name|repoConfig
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
comment|// Resources are manually managed.
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|ChangeKind
name|call
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|Objects
operator|.
name|equals
argument_list|(
name|key
operator|.
name|prior
argument_list|()
argument_list|,
name|key
operator|.
name|next
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|ChangeKind
operator|.
name|NO_CODE_CHANGE
return|;
block|}
name|RevWalk
name|rw
init|=
name|alreadyOpenRw
decl_stmt|;
name|Config
name|config
init|=
name|repoConfig
decl_stmt|;
name|Repository
name|repo
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|alreadyOpenRw
operator|==
literal|null
condition|)
block|{
name|repo
operator|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
expr_stmt|;
name|config
operator|=
name|repo
operator|.
name|getConfig
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|RevCommit
name|prior
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|key
operator|.
name|prior
argument_list|()
argument_list|)
decl_stmt|;
name|rw
operator|.
name|parseBody
argument_list|(
name|prior
argument_list|)
expr_stmt|;
name|RevCommit
name|next
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|key
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|rw
operator|.
name|parseBody
argument_list|(
name|next
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|next
operator|.
name|getFullMessage
argument_list|()
operator|.
name|equals
argument_list|(
name|prior
operator|.
name|getFullMessage
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|isSameDeltaAndTree
argument_list|(
name|rw
argument_list|,
name|prior
argument_list|,
name|next
argument_list|)
condition|)
block|{
return|return
name|ChangeKind
operator|.
name|NO_CODE_CHANGE
return|;
block|}
return|return
name|ChangeKind
operator|.
name|REWORK
return|;
block|}
if|if
condition|(
name|isSameDeltaAndTree
argument_list|(
name|rw
argument_list|,
name|prior
argument_list|,
name|next
argument_list|)
condition|)
block|{
return|return
name|ChangeKind
operator|.
name|NO_CHANGE
return|;
block|}
if|if
condition|(
name|prior
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
operator|||
name|next
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// At this point we have considered all the kinds that could be applicable to root
comment|// commits; the remainder of the checks in this method all assume that both commits have
comment|// at least one parent.
return|return
name|ChangeKind
operator|.
name|REWORK
return|;
block|}
if|if
condition|(
operator|(
name|prior
operator|.
name|getParentCount
argument_list|()
operator|>
literal|1
operator|||
name|next
operator|.
name|getParentCount
argument_list|()
operator|>
literal|1
operator|)
operator|&&
operator|!
name|onlyFirstParentChanged
argument_list|(
name|prior
argument_list|,
name|next
argument_list|)
condition|)
block|{
comment|// Trivial rebases done by machine only work well on 1 parent.
return|return
name|ChangeKind
operator|.
name|REWORK
return|;
block|}
comment|// A trivial rebase can be detected by looking for the next commit
comment|// having the same tree as would exist when the prior commit is
comment|// cherry-picked onto the next commit's new first parent.
try|try
init|(
name|ObjectInserter
name|ins
init|=
operator|new
name|InMemoryInserter
argument_list|(
name|rw
operator|.
name|getObjectReader
argument_list|()
argument_list|)
init|)
block|{
name|ThreeWayMerger
name|merger
init|=
name|MergeUtil
operator|.
name|newThreeWayMerger
argument_list|(
name|ins
argument_list|,
name|config
argument_list|,
name|key
operator|.
name|strategyName
argument_list|()
argument_list|)
decl_stmt|;
name|merger
operator|.
name|setBase
argument_list|(
name|prior
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|merger
operator|.
name|merge
argument_list|(
name|next
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|,
name|prior
argument_list|)
operator|&&
name|merger
operator|.
name|getResultTreeId
argument_list|()
operator|.
name|equals
argument_list|(
name|next
operator|.
name|getTree
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|prior
operator|.
name|getParentCount
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|ChangeKind
operator|.
name|TRIVIAL_REBASE
return|;
block|}
return|return
name|ChangeKind
operator|.
name|MERGE_FIRST_PARENT_UPDATE
return|;
block|}
block|}
catch|catch
parameter_list|(
name|LargeObjectException
name|e
parameter_list|)
block|{
comment|// Some object is too large for the merge attempt to succeed. Assume
comment|// it was a rework.
block|}
return|return
name|ChangeKind
operator|.
name|REWORK
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|repo
operator|!=
literal|null
condition|)
block|{
name|rw
operator|.
name|close
argument_list|()
expr_stmt|;
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|onlyFirstParentChanged (RevCommit prior, RevCommit next)
specifier|public
specifier|static
name|boolean
name|onlyFirstParentChanged
parameter_list|(
name|RevCommit
name|prior
parameter_list|,
name|RevCommit
name|next
parameter_list|)
block|{
return|return
operator|!
name|sameFirstParents
argument_list|(
name|prior
argument_list|,
name|next
argument_list|)
operator|&&
name|sameRestOfParents
argument_list|(
name|prior
argument_list|,
name|next
argument_list|)
return|;
block|}
DECL|method|sameFirstParents (RevCommit prior, RevCommit next)
specifier|private
specifier|static
name|boolean
name|sameFirstParents
parameter_list|(
name|RevCommit
name|prior
parameter_list|,
name|RevCommit
name|next
parameter_list|)
block|{
if|if
condition|(
name|prior
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|next
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
return|;
block|}
return|return
name|prior
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
name|next
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
DECL|method|sameRestOfParents (RevCommit prior, RevCommit next)
specifier|private
specifier|static
name|boolean
name|sameRestOfParents
parameter_list|(
name|RevCommit
name|prior
parameter_list|,
name|RevCommit
name|next
parameter_list|)
block|{
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|priorRestParents
init|=
name|allExceptFirstParent
argument_list|(
name|prior
operator|.
name|getParents
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|nextRestParents
init|=
name|allExceptFirstParent
argument_list|(
name|next
operator|.
name|getParents
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|priorRestParents
operator|.
name|equals
argument_list|(
name|nextRestParents
argument_list|)
return|;
block|}
DECL|method|allExceptFirstParent (RevCommit[] parents)
specifier|private
specifier|static
name|Set
argument_list|<
name|RevCommit
argument_list|>
name|allExceptFirstParent
parameter_list|(
name|RevCommit
index|[]
name|parents
parameter_list|)
block|{
return|return
name|FluentIterable
operator|.
name|from
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|parents
argument_list|)
argument_list|)
operator|.
name|skip
argument_list|(
literal|1
argument_list|)
operator|.
name|toSet
argument_list|()
return|;
block|}
DECL|method|isSameDeltaAndTree (RevWalk rw, RevCommit prior, RevCommit next)
specifier|private
specifier|static
name|boolean
name|isSameDeltaAndTree
parameter_list|(
name|RevWalk
name|rw
parameter_list|,
name|RevCommit
name|prior
parameter_list|,
name|RevCommit
name|next
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|next
operator|.
name|getTree
argument_list|()
argument_list|,
name|prior
operator|.
name|getTree
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|prior
operator|.
name|getParentCount
argument_list|()
operator|!=
name|next
operator|.
name|getParentCount
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|prior
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// Make sure that the prior/next delta is the same - not just the tree.
comment|// This is done by making sure that the parent trees are equal.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|prior
operator|.
name|getParentCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// Parse parent commits so that their trees are available.
name|rw
operator|.
name|parseCommit
argument_list|(
name|prior
operator|.
name|getParent
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|rw
operator|.
name|parseCommit
argument_list|(
name|next
operator|.
name|getParent
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|next
operator|.
name|getParent
argument_list|(
name|i
argument_list|)
operator|.
name|getTree
argument_list|()
argument_list|,
name|prior
operator|.
name|getParent
argument_list|(
name|i
argument_list|)
operator|.
name|getTree
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
DECL|class|ChangeKindWeigher
specifier|public
specifier|static
class|class
name|ChangeKindWeigher
implements|implements
name|Weigher
argument_list|<
name|Key
argument_list|,
name|ChangeKind
argument_list|>
block|{
annotation|@
name|Override
DECL|method|weigh (Key key, ChangeKind changeKind)
specifier|public
name|int
name|weigh
parameter_list|(
name|Key
name|key
parameter_list|,
name|ChangeKind
name|changeKind
parameter_list|)
block|{
return|return
literal|16
operator|+
literal|2
operator|*
literal|36
operator|+
literal|2
operator|*
name|key
operator|.
name|strategyName
argument_list|()
operator|.
name|length
argument_list|()
comment|// Size of Key, 64 bit JVM
operator|+
literal|2
operator|*
name|changeKind
operator|.
name|name
argument_list|()
operator|.
name|length
argument_list|()
return|;
comment|// Size of ChangeKind, 64 bit JVM
block|}
block|}
DECL|field|cache
specifier|private
specifier|final
name|Cache
argument_list|<
name|Key
argument_list|,
name|ChangeKind
argument_list|>
name|cache
decl_stmt|;
DECL|field|useRecursiveMerge
specifier|private
specifier|final
name|boolean
name|useRecursiveMerge
decl_stmt|;
DECL|field|changeDataFactory
specifier|private
specifier|final
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeKindCacheImpl ( @erritServerConfig Config serverConfig, @Named(ID_CACHE) Cache<Key, ChangeKind> cache, ChangeData.Factory changeDataFactory, GitRepositoryManager repoManager)
name|ChangeKindCacheImpl
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|serverConfig
parameter_list|,
annotation|@
name|Named
argument_list|(
name|ID_CACHE
argument_list|)
name|Cache
argument_list|<
name|Key
argument_list|,
name|ChangeKind
argument_list|>
name|cache
parameter_list|,
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|,
name|GitRepositoryManager
name|repoManager
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
name|useRecursiveMerge
operator|=
name|MergeUtil
operator|.
name|useRecursiveMerge
argument_list|(
name|serverConfig
argument_list|)
expr_stmt|;
name|this
operator|.
name|changeDataFactory
operator|=
name|changeDataFactory
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getChangeKind ( Project.NameKey project, @Nullable RevWalk rw, @Nullable Config repoConfig, ObjectId prior, ObjectId next)
specifier|public
name|ChangeKind
name|getChangeKind
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
annotation|@
name|Nullable
name|RevWalk
name|rw
parameter_list|,
annotation|@
name|Nullable
name|Config
name|repoConfig
parameter_list|,
name|ObjectId
name|prior
parameter_list|,
name|ObjectId
name|next
parameter_list|)
block|{
try|try
block|{
name|Key
name|key
init|=
name|Key
operator|.
name|create
argument_list|(
name|prior
argument_list|,
name|next
argument_list|,
name|useRecursiveMerge
argument_list|)
decl_stmt|;
return|return
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|,
operator|new
name|Loader
argument_list|(
name|key
argument_list|,
name|repoManager
argument_list|,
name|project
argument_list|,
name|rw
argument_list|,
name|repoConfig
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
name|logger
operator|.
name|atWarning
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Cannot check trivial rebase of new patch set %s in %s"
argument_list|,
name|next
operator|.
name|name
argument_list|()
argument_list|,
name|project
argument_list|)
expr_stmt|;
return|return
name|ChangeKind
operator|.
name|REWORK
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getChangeKind (Change change, PatchSet patch)
specifier|public
name|ChangeKind
name|getChangeKind
parameter_list|(
name|Change
name|change
parameter_list|,
name|PatchSet
name|patch
parameter_list|)
block|{
return|return
name|getChangeKindInternal
argument_list|(
name|this
argument_list|,
name|change
argument_list|,
name|patch
argument_list|,
name|changeDataFactory
argument_list|,
name|repoManager
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getChangeKind ( @ullable RevWalk rw, @Nullable Config repoConfig, ChangeData cd, PatchSet patch)
specifier|public
name|ChangeKind
name|getChangeKind
parameter_list|(
annotation|@
name|Nullable
name|RevWalk
name|rw
parameter_list|,
annotation|@
name|Nullable
name|Config
name|repoConfig
parameter_list|,
name|ChangeData
name|cd
parameter_list|,
name|PatchSet
name|patch
parameter_list|)
block|{
return|return
name|getChangeKindInternal
argument_list|(
name|this
argument_list|,
name|rw
argument_list|,
name|repoConfig
argument_list|,
name|cd
argument_list|,
name|patch
argument_list|)
return|;
block|}
DECL|method|getChangeKindInternal ( ChangeKindCache cache, @Nullable RevWalk rw, @Nullable Config repoConfig, ChangeData change, PatchSet patch)
specifier|private
specifier|static
name|ChangeKind
name|getChangeKindInternal
parameter_list|(
name|ChangeKindCache
name|cache
parameter_list|,
annotation|@
name|Nullable
name|RevWalk
name|rw
parameter_list|,
annotation|@
name|Nullable
name|Config
name|repoConfig
parameter_list|,
name|ChangeData
name|change
parameter_list|,
name|PatchSet
name|patch
parameter_list|)
block|{
name|ChangeKind
name|kind
init|=
name|ChangeKind
operator|.
name|REWORK
decl_stmt|;
comment|// Trivial case: if we're on the first patch, we don't need to use
comment|// the repository.
if|if
condition|(
name|patch
operator|.
name|id
argument_list|()
operator|.
name|get
argument_list|()
operator|>
literal|1
condition|)
block|{
try|try
block|{
name|Collection
argument_list|<
name|PatchSet
argument_list|>
name|patchSetCollection
init|=
name|change
operator|.
name|patchSets
argument_list|()
decl_stmt|;
name|PatchSet
name|priorPs
init|=
name|patch
decl_stmt|;
for|for
control|(
name|PatchSet
name|ps
range|:
name|patchSetCollection
control|)
block|{
if|if
condition|(
name|ps
operator|.
name|id
argument_list|()
operator|.
name|get
argument_list|()
operator|<
name|patch
operator|.
name|id
argument_list|()
operator|.
name|get
argument_list|()
operator|&&
operator|(
name|ps
operator|.
name|id
argument_list|()
operator|.
name|get
argument_list|()
operator|>
name|priorPs
operator|.
name|id
argument_list|()
operator|.
name|get
argument_list|()
operator|||
name|priorPs
operator|==
name|patch
operator|)
condition|)
block|{
comment|// We only want the previous patch set, so walk until the last one
name|priorPs
operator|=
name|ps
expr_stmt|;
block|}
block|}
comment|// If we still think the previous patch is the current patch,
comment|// we only have one patch set.  Return the default.
comment|// This can happen if a user creates a draft, uploads a second patch,
comment|// and deletes the draft.
if|if
condition|(
name|priorPs
operator|!=
name|patch
condition|)
block|{
name|kind
operator|=
name|cache
operator|.
name|getChangeKind
argument_list|(
name|change
operator|.
name|project
argument_list|()
argument_list|,
name|rw
argument_list|,
name|repoConfig
argument_list|,
name|priorPs
operator|.
name|commitId
argument_list|()
argument_list|,
name|patch
operator|.
name|commitId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|StorageException
name|e
parameter_list|)
block|{
comment|// Do nothing; assume we have a complex change
name|logger
operator|.
name|atWarning
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Unable to get change kind for patchSet %s of change %s"
argument_list|,
name|patch
operator|.
name|number
argument_list|()
argument_list|,
name|change
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|kind
return|;
block|}
DECL|method|getChangeKindInternal ( ChangeKindCache cache, Change change, PatchSet patch, ChangeData.Factory changeDataFactory, GitRepositoryManager repoManager)
specifier|private
specifier|static
name|ChangeKind
name|getChangeKindInternal
parameter_list|(
name|ChangeKindCache
name|cache
parameter_list|,
name|Change
name|change
parameter_list|,
name|PatchSet
name|patch
parameter_list|,
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|)
block|{
comment|// TODO - dborowitz: add NEW_CHANGE type for default.
name|ChangeKind
name|kind
init|=
name|ChangeKind
operator|.
name|REWORK
decl_stmt|;
comment|// Trivial case: if we're on the first patch, we don't need to open
comment|// the repository.
if|if
condition|(
name|patch
operator|.
name|id
argument_list|()
operator|.
name|get
argument_list|()
operator|>
literal|1
condition|)
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
init|;           RevWalk rw = new RevWalk(repo)
block|)
block|{
name|kind
operator|=
name|getChangeKindInternal
argument_list|(
name|cache
argument_list|,
name|rw
argument_list|,
name|repo
operator|.
name|getConfig
argument_list|()
argument_list|,
name|changeDataFactory
operator|.
name|create
argument_list|(
name|change
argument_list|)
argument_list|,
name|patch
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Do nothing; assume we have a complex change
name|logger
operator|.
name|atWarning
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Unable to get change kind for patchSet %s of change %s"
argument_list|,
name|patch
operator|.
name|number
argument_list|()
argument_list|,
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|kind
return|;
block|}
end_class

unit|}
end_unit

