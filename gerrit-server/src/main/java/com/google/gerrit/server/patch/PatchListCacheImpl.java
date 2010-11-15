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
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
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
name|EvictionPolicy
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
name|TypeLiteral
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
specifier|private
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
specifier|final
name|TypeLiteral
argument_list|<
name|Cache
argument_list|<
name|PatchListKey
argument_list|,
name|PatchList
argument_list|>
argument_list|>
name|fileType
init|=
operator|new
name|TypeLiteral
argument_list|<
name|Cache
argument_list|<
name|PatchListKey
argument_list|,
name|PatchList
argument_list|>
argument_list|>
argument_list|()
block|{}
decl_stmt|;
name|disk
argument_list|(
name|fileType
argument_list|,
name|FILE_NAME
argument_list|)
comment|//
operator|.
name|memoryLimit
argument_list|(
literal|128
argument_list|)
comment|// very large items, cache only a few
operator|.
name|evictionPolicy
argument_list|(
name|EvictionPolicy
operator|.
name|LRU
argument_list|)
comment|// prefer most recent
operator|.
name|populateWith
argument_list|(
name|PatchListLoader
operator|.
name|class
argument_list|)
comment|//
expr_stmt|;
specifier|final
name|TypeLiteral
argument_list|<
name|Cache
argument_list|<
name|IntraLineDiffKey
argument_list|,
name|IntraLineDiff
argument_list|>
argument_list|>
name|intraType
init|=
operator|new
name|TypeLiteral
argument_list|<
name|Cache
argument_list|<
name|IntraLineDiffKey
argument_list|,
name|IntraLineDiff
argument_list|>
argument_list|>
argument_list|()
block|{}
decl_stmt|;
name|disk
argument_list|(
name|intraType
argument_list|,
name|INTRA_NAME
argument_list|)
comment|//
operator|.
name|memoryLimit
argument_list|(
literal|128
argument_list|)
comment|// very large items, cache only a few
operator|.
name|evictionPolicy
argument_list|(
name|EvictionPolicy
operator|.
name|LRU
argument_list|)
comment|// prefer most recent
operator|.
name|populateWith
argument_list|(
name|IntraLineLoader
operator|.
name|class
argument_list|)
comment|//
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
DECL|field|computeIntraline
specifier|private
specifier|final
name|boolean
name|computeIntraline
decl_stmt|;
annotation|@
name|Inject
DECL|method|PatchListCacheImpl ( @amedFILE_NAME) final Cache<PatchListKey, PatchList> fileCache, @Named(INTRA_NAME) final Cache<IntraLineDiffKey, IntraLineDiff> intraCache, @GerritServerConfig Config cfg)
name|PatchListCacheImpl
parameter_list|(
annotation|@
name|Named
argument_list|(
name|FILE_NAME
argument_list|)
specifier|final
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
specifier|final
name|Cache
argument_list|<
name|IntraLineDiffKey
argument_list|,
name|IntraLineDiff
argument_list|>
name|intraCache
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
DECL|method|get (final PatchListKey key)
specifier|public
name|PatchList
name|get
parameter_list|(
specifier|final
name|PatchListKey
name|key
parameter_list|)
block|{
return|return
name|fileCache
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|get (final Change change, final PatchSet patchSet)
specifier|public
name|PatchList
name|get
parameter_list|(
specifier|final
name|Change
name|change
parameter_list|,
specifier|final
name|PatchSet
name|patchSet
parameter_list|)
block|{
specifier|final
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
specifier|final
name|ObjectId
name|a
init|=
literal|null
decl_stmt|;
specifier|final
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
specifier|final
name|Whitespace
name|ws
init|=
name|Whitespace
operator|.
name|IGNORE_NONE
decl_stmt|;
return|return
name|get
argument_list|(
operator|new
name|PatchListKey
argument_list|(
name|projectKey
argument_list|,
name|a
argument_list|,
name|b
argument_list|,
name|ws
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getIntraLineDiff (IntraLineDiffKey key)
specifier|public
name|IntraLineDiff
name|getIntraLineDiff
parameter_list|(
name|IntraLineDiffKey
name|key
parameter_list|)
block|{
if|if
condition|(
name|computeIntraline
condition|)
block|{
name|IntraLineDiff
name|d
init|=
name|intraCache
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|==
literal|null
condition|)
block|{
name|d
operator|=
operator|new
name|IntraLineDiff
argument_list|(
name|IntraLineDiff
operator|.
name|Status
operator|.
name|ERROR
argument_list|)
expr_stmt|;
block|}
return|return
name|d
return|;
block|}
else|else
block|{
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
block|}
block|}
end_class

end_unit

