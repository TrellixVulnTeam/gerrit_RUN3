begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.pgm.util
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|util
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Scopes
operator|.
name|SINGLETON
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
name|collect
operator|.
name|ImmutableSet
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
name|GroupReference
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
name|api
operator|.
name|projects
operator|.
name|CommentLinkInfo
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
name|common
operator|.
name|AccountVisibility
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
name|config
operator|.
name|FactoryModule
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
name|registration
operator|.
name|DynamicMap
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
name|registration
operator|.
name|DynamicSet
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
name|RestView
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
name|AccountGroup
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
name|AccountCacheImpl
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
name|AccountVisibilityProvider
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
name|CapabilityCollection
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
name|FakeRealm
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
name|GroupCacheImpl
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
name|GroupIncludeCacheImpl
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
name|Realm
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
name|externalids
operator|.
name|ExternalIdModule
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
name|CacheRemovalListener
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
name|h2
operator|.
name|DefaultCacheFactory
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
name|ChangeJson
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
name|ChangeKindCacheImpl
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
name|MergeabilityCacheImpl
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
name|PatchSetInserter
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
name|RebaseChangeOp
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
name|AdministrateServerGroups
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
name|CanonicalWebUrl
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
name|CanonicalWebUrlProvider
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
name|DisableReverseDnsLookup
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
name|DisableReverseDnsLookupProvider
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
name|config
operator|.
name|GitReceivePackGroups
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
name|GitUploadPackGroups
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
name|SysExecutorModule
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
name|extensions
operator|.
name|events
operator|.
name|EventUtil
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
name|extensions
operator|.
name|events
operator|.
name|GitReferenceUpdated
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
name|extensions
operator|.
name|events
operator|.
name|RevisionCreated
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
name|git
operator|.
name|SearchingChangeCacheImpl
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
name|TagCache
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
name|mail
operator|.
name|send
operator|.
name|ReplacePatchSetSender
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
name|NoteDbModule
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
name|DiffExecutorModule
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
name|PatchListCacheImpl
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
name|DefaultPermissionBackendModule
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
name|SectionSortCache
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
name|CommentLinkProvider
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
name|CommitResource
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
name|ProjectCacheImpl
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
name|project
operator|.
name|SubmitRuleEvaluator
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
name|gerrit
operator|.
name|server
operator|.
name|query
operator|.
name|change
operator|.
name|ChangeQueryProcessor
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
name|group
operator|.
name|GroupModule
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
name|rules
operator|.
name|DefaultSubmitRule
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
name|rules
operator|.
name|PrologModule
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
name|update
operator|.
name|BatchUpdate
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
name|util
operator|.
name|Providers
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Set
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

begin_comment
comment|/**  * Module for programs that perform batch operations on a site.  *  *<p>Any program that requires this module likely also requires using {@link ThreadLimiter} to  * limit the number of threads accessing the database concurrently.  */
end_comment

begin_class
DECL|class|BatchProgramModule
specifier|public
class|class
name|BatchProgramModule
extends|extends
name|FactoryModule
block|{
DECL|field|cfg
specifier|private
specifier|final
name|Config
name|cfg
decl_stmt|;
DECL|field|reviewDbModule
specifier|private
specifier|final
name|Module
name|reviewDbModule
decl_stmt|;
annotation|@
name|Inject
DECL|method|BatchProgramModule (@erritServerConfig Config cfg, PerThreadReviewDbModule reviewDbModule)
name|BatchProgramModule
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|PerThreadReviewDbModule
name|reviewDbModule
parameter_list|)
block|{
name|this
operator|.
name|cfg
operator|=
name|cfg
expr_stmt|;
name|this
operator|.
name|reviewDbModule
operator|=
name|reviewDbModule
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|install
argument_list|(
name|reviewDbModule
argument_list|)
expr_stmt|;
name|install
argument_list|(
operator|new
name|DiffExecutorModule
argument_list|()
argument_list|)
expr_stmt|;
name|install
argument_list|(
operator|new
name|SysExecutorModule
argument_list|()
argument_list|)
expr_stmt|;
name|install
argument_list|(
name|BatchUpdate
operator|.
name|module
argument_list|()
argument_list|)
expr_stmt|;
name|install
argument_list|(
name|PatchListCacheImpl
operator|.
name|module
argument_list|()
argument_list|)
expr_stmt|;
comment|// Plugins are not loaded and we're just running through each change
comment|// once, so don't worry about cache removal.
name|bind
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|DynamicSet
argument_list|<
name|CacheRemovalListener
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|toInstance
argument_list|(
name|DynamicSet
operator|.
expr|<
name|CacheRemovalListener
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|bind
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|DynamicMap
argument_list|<
name|Cache
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
argument_list|>
argument_list|()
block|{}
block|)
function|.toInstance
parameter_list|(
function|DynamicMap.<Cache<?
operator|,
function|?>>emptyMap
parameter_list|()
block|)
class|;
end_class

begin_expr_stmt
name|bind
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|List
argument_list|<
name|CommentLinkInfo
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|toProvider
argument_list|(
name|CommentLinkProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|bind
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|DynamicMap
argument_list|<
name|ChangeQueryProcessor
operator|.
name|ChangeAttributeFactory
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|toInstance
argument_list|(
name|DynamicMap
operator|.
expr|<
name|ChangeQueryProcessor
operator|.
name|ChangeAttributeFactory
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|bind
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|CommitResource
argument_list|>
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|toInstance
argument_list|(
name|DynamicMap
operator|.
expr|<
name|RestView
argument_list|<
name|CommitResource
argument_list|>
operator|>
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|bind
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|CanonicalWebUrl
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|CanonicalWebUrlProvider
operator|.
name|class
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|bind
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|DisableReverseDnsLookup
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|DisableReverseDnsLookupProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|bind
argument_list|(
name|Realm
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|FakeRealm
operator|.
name|class
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|bind
argument_list|(
name|IdentifiedUser
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|Providers
operator|.
expr|<
name|IdentifiedUser
operator|>
name|of
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|bind
argument_list|(
name|ReplacePatchSetSender
operator|.
name|Factory
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|Providers
operator|.
expr|<
name|ReplacePatchSetSender
operator|.
name|Factory
operator|>
name|of
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|bind
argument_list|(
name|CurrentUser
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|IdentifiedUser
operator|.
name|class
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|factory
argument_list|(
name|MergeUtil
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|factory
argument_list|(
name|PatchSetInserter
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|factory
argument_list|(
name|RebaseChangeOp
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// As Reindex is a batch program, don't assume the index is available for
end_comment

begin_comment
comment|// the change cache.
end_comment

begin_expr_stmt
name|bind
argument_list|(
name|SearchingChangeCacheImpl
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|Providers
operator|.
expr|<
name|SearchingChangeCacheImpl
operator|>
name|of
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|bind
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|ImmutableSet
argument_list|<
name|GroupReference
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|AdministrateServerGroups
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|ImmutableSet
operator|.
expr|<
name|GroupReference
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|bind
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|GitUploadPackGroups
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|Collections
operator|.
expr|<
name|AccountGroup
operator|.
name|UUID
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|bind
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|GitReceivePackGroups
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|Collections
operator|.
expr|<
name|AccountGroup
operator|.
name|UUID
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
operator|new
name|BatchGitModule
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
operator|new
name|DefaultPermissionBackendModule
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
operator|new
name|DefaultCacheFactory
operator|.
name|MemoryCacheModule
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
operator|new
name|DefaultCacheFactory
operator|.
name|PersistentCacheModule
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
operator|new
name|ExternalIdModule
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
operator|new
name|GroupModule
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
operator|new
name|NoteDbModule
argument_list|(
name|cfg
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
name|AccountCacheImpl
operator|.
name|module
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
name|GroupCacheImpl
operator|.
name|module
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
name|GroupIncludeCacheImpl
operator|.
name|module
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
name|ProjectCacheImpl
operator|.
name|module
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
name|SectionSortCache
operator|.
name|module
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
name|ChangeKindCacheImpl
operator|.
name|module
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
name|MergeabilityCacheImpl
operator|.
name|module
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
name|TagCache
operator|.
name|module
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|factory
argument_list|(
name|CapabilityCollection
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|factory
argument_list|(
name|ChangeData
operator|.
name|AssistedFactory
operator|.
name|class
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|factory
argument_list|(
name|ProjectState
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|// Submit rule evaluator
end_comment

begin_expr_stmt
name|factory
argument_list|(
name|SubmitRuleEvaluator
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
operator|new
name|PrologModule
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|install
argument_list|(
operator|new
name|DefaultSubmitRule
operator|.
name|Module
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|bind
argument_list|(
name|ChangeJson
operator|.
name|Factory
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|Providers
operator|.
expr|<
name|ChangeJson
operator|.
name|Factory
operator|>
name|of
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|bind
argument_list|(
name|EventUtil
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|Providers
operator|.
expr|<
name|EventUtil
operator|>
name|of
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|bind
argument_list|(
name|GitReferenceUpdated
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|GitReferenceUpdated
operator|.
name|DISABLED
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|bind
argument_list|(
name|RevisionCreated
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|RevisionCreated
operator|.
name|DISABLED
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|bind
argument_list|(
name|AccountVisibility
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|AccountVisibilityProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
end_expr_stmt

unit|} }
end_unit

