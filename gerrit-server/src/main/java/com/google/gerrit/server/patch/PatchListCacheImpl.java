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

begin_comment
comment|//
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
name|gerrit
operator|.
name|server
operator|.
name|patch
operator|.
name|DiffSummaryLoader
operator|.
name|toDiffSummary
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
name|util
operator|.
name|concurrent
operator|.
name|UncheckedExecutionException
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

begin_comment
comment|/** Provides a cached list of {@link PatchListEntry}. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|PatchListCacheImpl
specifier|public
class|class
name|PatchListCacheImpl
implements|implements
name|PatchListCache
block|{
DECL|field|FILE_NAME
specifier|static
specifier|final
name|String
name|FILE_NAME
init|=
literal|"diff"
decl_stmt|;
DECL|field|INTRA_NAME
specifier|static
specifier|final
name|String
name|INTRA_NAME
init|=
literal|"diff_intraline"
decl_stmt|;
DECL|field|DIFF_SUMMARY
specifier|static
specifier|final
name|String
name|DIFF_SUMMARY
init|=
literal|"diff_summary"
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
name|factory
argument_list|(
name|PatchListLoader
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|persist
argument_list|(
name|FILE_NAME
argument_list|,
name|PatchListKey
operator|.
name|class
argument_list|,
name|PatchList
operator|.
name|class
argument_list|)
operator|.
name|maximumWeight
argument_list|(
literal|10
operator|<<
literal|20
argument_list|)
operator|.
name|weigher
argument_list|(
name|PatchListWeigher
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
argument_list|(
name|IntraLineLoader
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|persist
argument_list|(
name|INTRA_NAME
argument_list|,
name|IntraLineDiffKey
operator|.
name|class
argument_list|,
name|IntraLineDiff
operator|.
name|class
argument_list|)
operator|.
name|maximumWeight
argument_list|(
literal|10
operator|<<
literal|20
argument_list|)
operator|.
name|weigher
argument_list|(
name|IntraLineWeigher
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
argument_list|(
name|DiffSummaryLoader
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|persist
argument_list|(
name|DIFF_SUMMARY
argument_list|,
name|DiffSummaryKey
operator|.
name|class
argument_list|,
name|DiffSummary
operator|.
name|class
argument_list|)
operator|.
name|maximumWeight
argument_list|(
literal|10
operator|<<
literal|20
argument_list|)
operator|.
name|weigher
argument_list|(
name|DiffSummaryWeigher
operator|.
name|class
argument_list|)
operator|.
name|diskLimit
argument_list|(
literal|1
operator|<<
literal|30
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|PatchListCacheImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|PatchListCache
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|PatchListCacheImpl
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|field|fileCache
specifier|private
specifier|final
name|Cache
argument_list|<
name|PatchListKey
argument_list|,
name|PatchList
argument_list|>
name|fileCache
decl_stmt|;
DECL|field|intraCache
specifier|private
specifier|final
name|Cache
argument_list|<
name|IntraLineDiffKey
argument_list|,
name|IntraLineDiff
argument_list|>
name|intraCache
decl_stmt|;
DECL|field|diffSummaryCache
specifier|private
specifier|final
name|Cache
argument_list|<
name|DiffSummaryKey
argument_list|,
name|DiffSummary
argument_list|>
name|diffSummaryCache
decl_stmt|;
DECL|field|fileLoaderFactory
specifier|private
specifier|final
name|PatchListLoader
operator|.
name|Factory
name|fileLoaderFactory
decl_stmt|;
DECL|field|intraLoaderFactory
specifier|private
specifier|final
name|IntraLineLoader
operator|.
name|Factory
name|intraLoaderFactory
decl_stmt|;
DECL|field|diffSummaryLoaderFactory
specifier|private
specifier|final
name|DiffSummaryLoader
operator|.
name|Factory
name|diffSummaryLoaderFactory
decl_stmt|;
DECL|field|computeIntraline
specifier|private
specifier|final
name|boolean
name|computeIntraline
decl_stmt|;
annotation|@
name|Inject
DECL|method|PatchListCacheImpl ( @amedFILE_NAME) Cache<PatchListKey, PatchList> fileCache, @Named(INTRA_NAME) Cache<IntraLineDiffKey, IntraLineDiff> intraCache, @Named(DIFF_SUMMARY) Cache<DiffSummaryKey, DiffSummary> diffSummaryCache, PatchListLoader.Factory fileLoaderFactory, IntraLineLoader.Factory intraLoaderFactory, DiffSummaryLoader.Factory diffSummaryLoaderFactory, @GerritServerConfig Config cfg)
name|PatchListCacheImpl
parameter_list|(
annotation|@
name|Named
argument_list|(
name|FILE_NAME
argument_list|)
name|Cache
argument_list|<
name|PatchListKey
argument_list|,
name|PatchList
argument_list|>
name|fileCache
parameter_list|,
annotation|@
name|Named
argument_list|(
name|INTRA_NAME
argument_list|)
name|Cache
argument_list|<
name|IntraLineDiffKey
argument_list|,
name|IntraLineDiff
argument_list|>
name|intraCache
parameter_list|,
annotation|@
name|Named
argument_list|(
name|DIFF_SUMMARY
argument_list|)
name|Cache
argument_list|<
name|DiffSummaryKey
argument_list|,
name|DiffSummary
argument_list|>
name|diffSummaryCache
parameter_list|,
name|PatchListLoader
operator|.
name|Factory
name|fileLoaderFactory
parameter_list|,
name|IntraLineLoader
operator|.
name|Factory
name|intraLoaderFactory
parameter_list|,
name|DiffSummaryLoader
operator|.
name|Factory
name|diffSummaryLoaderFactory
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|this
operator|.
name|fileCache
operator|=
name|fileCache
expr_stmt|;
name|this
operator|.
name|intraCache
operator|=
name|intraCache
expr_stmt|;
name|this
operator|.
name|diffSummaryCache
operator|=
name|diffSummaryCache
expr_stmt|;
name|this
operator|.
name|fileLoaderFactory
operator|=
name|fileLoaderFactory
expr_stmt|;
name|this
operator|.
name|intraLoaderFactory
operator|=
name|intraLoaderFactory
expr_stmt|;
name|this
operator|.
name|diffSummaryLoaderFactory
operator|=
name|diffSummaryLoaderFactory
expr_stmt|;
name|this
operator|.
name|computeIntraline
operator|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"cache"
argument_list|,
name|INTRA_NAME
argument_list|,
literal|"enabled"
argument_list|,
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"cache"
argument_list|,
literal|"diff"
argument_list|,
literal|"intraline"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get (PatchListKey key, Project.NameKey project)
specifier|public
name|PatchList
name|get
parameter_list|(
name|PatchListKey
name|key
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
throws|throws
name|PatchListNotAvailableException
block|{
try|try
block|{
name|PatchList
name|pl
init|=
name|fileCache
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|fileLoaderFactory
operator|.
name|create
argument_list|(
name|key
argument_list|,
name|project
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|pl
operator|instanceof
name|LargeObjectTombstone
condition|)
block|{
throw|throw
operator|new
name|PatchListNotAvailableException
argument_list|(
literal|"Error computing "
operator|+
name|key
operator|+
literal|". Previous attempt failed with LargeObjectException"
argument_list|)
throw|;
block|}
if|if
condition|(
name|key
operator|.
name|getAlgorithm
argument_list|()
operator|==
name|PatchListKey
operator|.
name|Algorithm
operator|.
name|OPTIMIZED_DIFF
condition|)
block|{
name|diffSummaryCache
operator|.
name|put
argument_list|(
name|DiffSummaryKey
operator|.
name|fromPatchListKey
argument_list|(
name|key
argument_list|)
argument_list|,
name|toDiffSummary
argument_list|(
name|pl
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|pl
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|PatchListLoader
operator|.
name|log
operator|.
name|warn
argument_list|(
literal|"Error computing "
operator|+
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PatchListNotAvailableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|UncheckedExecutionException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|LargeObjectException
condition|)
block|{
comment|// Cache negative result so we don't need to redo expensive computations that would yield
comment|// the same result.
name|fileCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|new
name|LargeObjectTombstone
argument_list|()
argument_list|)
expr_stmt|;
name|PatchListLoader
operator|.
name|log
operator|.
name|warn
argument_list|(
literal|"Error computing "
operator|+
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PatchListNotAvailableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|get (Change change, PatchSet patchSet)
specifier|public
name|PatchList
name|get
parameter_list|(
name|Change
name|change
parameter_list|,
name|PatchSet
name|patchSet
parameter_list|)
throws|throws
name|PatchListNotAvailableException
block|{
return|return
name|get
argument_list|(
name|change
argument_list|,
name|patchSet
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getOldId (Change change, PatchSet patchSet, Integer parentNum)
specifier|public
name|ObjectId
name|getOldId
parameter_list|(
name|Change
name|change
parameter_list|,
name|PatchSet
name|patchSet
parameter_list|,
name|Integer
name|parentNum
parameter_list|)
throws|throws
name|PatchListNotAvailableException
block|{
return|return
name|get
argument_list|(
name|change
argument_list|,
name|patchSet
argument_list|,
name|parentNum
argument_list|)
operator|.
name|getOldId
argument_list|()
return|;
block|}
DECL|method|get (Change change, PatchSet patchSet, Integer parentNum)
specifier|private
name|PatchList
name|get
parameter_list|(
name|Change
name|change
parameter_list|,
name|PatchSet
name|patchSet
parameter_list|,
name|Integer
name|parentNum
parameter_list|)
throws|throws
name|PatchListNotAvailableException
block|{
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
if|if
condition|(
name|patchSet
operator|.
name|getRevision
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PatchListNotAvailableException
argument_list|(
literal|"revision is null for "
operator|+
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|)
throw|;
block|}
name|ObjectId
name|b
init|=
name|ObjectId
operator|.
name|fromString
argument_list|(
name|patchSet
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
if|if
condition|(
name|parentNum
operator|!=
literal|null
condition|)
block|{
return|return
name|get
argument_list|(
name|PatchListKey
operator|.
name|againstParentNum
argument_list|(
name|parentNum
argument_list|,
name|b
argument_list|,
name|ws
argument_list|)
argument_list|,
name|project
argument_list|)
return|;
block|}
return|return
name|get
argument_list|(
name|PatchListKey
operator|.
name|againstDefaultBase
argument_list|(
name|b
argument_list|,
name|ws
argument_list|)
argument_list|,
name|project
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getIntraLineDiff (IntraLineDiffKey key, IntraLineDiffArgs args)
specifier|public
name|IntraLineDiff
name|getIntraLineDiff
parameter_list|(
name|IntraLineDiffKey
name|key
parameter_list|,
name|IntraLineDiffArgs
name|args
parameter_list|)
block|{
if|if
condition|(
name|computeIntraline
condition|)
block|{
try|try
block|{
return|return
name|intraCache
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|intraLoaderFactory
operator|.
name|create
argument_list|(
name|key
argument_list|,
name|args
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
decl||
name|LargeObjectException
name|e
parameter_list|)
block|{
name|IntraLineLoader
operator|.
name|log
operator|.
name|warn
argument_list|(
literal|"Error computing "
operator|+
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
operator|new
name|IntraLineDiff
argument_list|(
name|IntraLineDiff
operator|.
name|Status
operator|.
name|ERROR
argument_list|)
return|;
block|}
block|}
return|return
operator|new
name|IntraLineDiff
argument_list|(
name|IntraLineDiff
operator|.
name|Status
operator|.
name|DISABLED
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDiffSummary (DiffSummaryKey key, Project.NameKey project)
specifier|public
name|DiffSummary
name|getDiffSummary
parameter_list|(
name|DiffSummaryKey
name|key
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
throws|throws
name|PatchListNotAvailableException
block|{
try|try
block|{
return|return
name|diffSummaryCache
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|diffSummaryLoaderFactory
operator|.
name|create
argument_list|(
name|key
argument_list|,
name|project
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
name|PatchListLoader
operator|.
name|log
operator|.
name|warn
argument_list|(
literal|"Error computing "
operator|+
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PatchListNotAvailableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|UncheckedExecutionException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|LargeObjectException
condition|)
block|{
name|PatchListLoader
operator|.
name|log
operator|.
name|warn
argument_list|(
literal|"Error computing "
operator|+
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PatchListNotAvailableException
argument_list|(
name|e
argument_list|)
throw|;
block|}
throw|throw
name|e
throw|;
block|}
block|}
comment|/** Used to cache negative results in {@code fileCache}. */
annotation|@
name|VisibleForTesting
DECL|class|LargeObjectTombstone
specifier|public
specifier|static
class|class
name|LargeObjectTombstone
extends|extends
name|PatchList
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|LargeObjectTombstone ()
specifier|public
name|LargeObjectTombstone
parameter_list|()
block|{
comment|// Initialize super class with valid values. We don't care about the inner state, but need to
comment|// pass valid values that don't break (de)serialization.
name|super
argument_list|(
literal|null
argument_list|,
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|,
literal|false
argument_list|,
name|ComparisonType
operator|.
name|againstAutoMerge
argument_list|()
argument_list|,
operator|new
name|PatchListEntry
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

