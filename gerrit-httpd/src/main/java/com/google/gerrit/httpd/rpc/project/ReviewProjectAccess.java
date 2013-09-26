begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.httpd.rpc.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|rpc
operator|.
name|project
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
name|AccessSection
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
name|reviewdb
operator|.
name|client
operator|.
name|Branch
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
name|PatchSetAncestor
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
name|client
operator|.
name|RevId
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
name|ChangeUtil
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
name|GroupBackend
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
name|ChangeResource
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
name|PostReviewers
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
name|MetaDataUpdate
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
name|ProjectConfig
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
name|index
operator|.
name|ChangeIndexer
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
name|ProjectControl
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
name|revwalk
operator|.
name|RevCommit
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

begin_class
DECL|class|ReviewProjectAccess
specifier|public
class|class
name|ReviewProjectAccess
extends|extends
name|ProjectAccessHandler
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (@ssisted Project.NameKey projectName, @Nullable @Assisted ObjectId base, @Assisted List<AccessSection> sectionList, @Nullable @Assisted String message)
name|ReviewProjectAccess
name|create
parameter_list|(
annotation|@
name|Assisted
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
annotation|@
name|Nullable
annotation|@
name|Assisted
name|ObjectId
name|base
parameter_list|,
annotation|@
name|Assisted
name|List
argument_list|<
name|AccessSection
argument_list|>
name|sectionList
parameter_list|,
annotation|@
name|Nullable
annotation|@
name|Assisted
name|String
name|message
parameter_list|)
function_decl|;
block|}
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|IdentifiedUser
name|user
decl_stmt|;
DECL|field|patchSetInfoFactory
specifier|private
specifier|final
name|PatchSetInfoFactory
name|patchSetInfoFactory
decl_stmt|;
DECL|field|reviewersProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|PostReviewers
argument_list|>
name|reviewersProvider
decl_stmt|;
DECL|field|changeFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|GenericFactory
name|changeFactory
decl_stmt|;
DECL|field|indexer
specifier|private
specifier|final
name|ChangeIndexer
name|indexer
decl_stmt|;
annotation|@
name|Inject
DECL|method|ReviewProjectAccess (final ProjectControl.Factory projectControlFactory, GroupBackend groupBackend, MetaDataUpdate.User metaDataUpdateFactory, ReviewDb db, IdentifiedUser user, PatchSetInfoFactory patchSetInfoFactory, Provider<PostReviewers> reviewersProvider, ChangeControl.GenericFactory changeFactory, ChangeIndexer indexer, @Assisted Project.NameKey projectName, @Nullable @Assisted ObjectId base, @Assisted List<AccessSection> sectionList, @Nullable @Assisted String message)
name|ReviewProjectAccess
parameter_list|(
specifier|final
name|ProjectControl
operator|.
name|Factory
name|projectControlFactory
parameter_list|,
name|GroupBackend
name|groupBackend
parameter_list|,
name|MetaDataUpdate
operator|.
name|User
name|metaDataUpdateFactory
parameter_list|,
name|ReviewDb
name|db
parameter_list|,
name|IdentifiedUser
name|user
parameter_list|,
name|PatchSetInfoFactory
name|patchSetInfoFactory
parameter_list|,
name|Provider
argument_list|<
name|PostReviewers
argument_list|>
name|reviewersProvider
parameter_list|,
name|ChangeControl
operator|.
name|GenericFactory
name|changeFactory
parameter_list|,
name|ChangeIndexer
name|indexer
parameter_list|,
annotation|@
name|Assisted
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
annotation|@
name|Nullable
annotation|@
name|Assisted
name|ObjectId
name|base
parameter_list|,
annotation|@
name|Assisted
name|List
argument_list|<
name|AccessSection
argument_list|>
name|sectionList
parameter_list|,
annotation|@
name|Nullable
annotation|@
name|Assisted
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|projectControlFactory
argument_list|,
name|groupBackend
argument_list|,
name|metaDataUpdateFactory
argument_list|,
name|projectName
argument_list|,
name|base
argument_list|,
name|sectionList
argument_list|,
name|message
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|patchSetInfoFactory
operator|=
name|patchSetInfoFactory
expr_stmt|;
name|this
operator|.
name|reviewersProvider
operator|=
name|reviewersProvider
expr_stmt|;
name|this
operator|.
name|changeFactory
operator|=
name|changeFactory
expr_stmt|;
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateProjectConfig (ProjectConfig config, MetaDataUpdate md)
specifier|protected
name|Change
operator|.
name|Id
name|updateProjectConfig
parameter_list|(
name|ProjectConfig
name|config
parameter_list|,
name|MetaDataUpdate
name|md
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|Change
operator|.
name|Id
name|changeId
init|=
operator|new
name|Change
operator|.
name|Id
argument_list|(
name|db
operator|.
name|nextChangeId
argument_list|()
argument_list|)
decl_stmt|;
name|PatchSet
name|ps
init|=
operator|new
name|PatchSet
argument_list|(
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|changeId
argument_list|,
name|Change
operator|.
name|INITIAL_PATCH_SET_ID
argument_list|)
argument_list|)
decl_stmt|;
name|RevCommit
name|commit
init|=
name|config
operator|.
name|commitToNewRef
argument_list|(
name|md
argument_list|,
name|ps
operator|.
name|getRefName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|commit
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|base
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Change
name|change
init|=
operator|new
name|Change
argument_list|(
operator|new
name|Change
operator|.
name|Key
argument_list|(
literal|"I"
operator|+
name|commit
operator|.
name|name
argument_list|()
argument_list|)
argument_list|,
name|changeId
argument_list|,
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|,
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|config
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|GitRepositoryManager
operator|.
name|REF_CONFIG
argument_list|)
argument_list|)
decl_stmt|;
name|ps
operator|.
name|setCreatedOn
argument_list|(
name|change
operator|.
name|getCreatedOn
argument_list|()
argument_list|)
expr_stmt|;
name|ps
operator|.
name|setUploader
argument_list|(
name|change
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|ps
operator|.
name|setRevision
argument_list|(
operator|new
name|RevId
argument_list|(
name|commit
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|PatchSetInfo
name|info
init|=
name|patchSetInfoFactory
operator|.
name|get
argument_list|(
name|commit
argument_list|,
name|ps
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|change
operator|.
name|setCurrentPatchSet
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|ChangeUtil
operator|.
name|updated
argument_list|(
name|change
argument_list|)
expr_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|beginTransaction
argument_list|(
name|changeId
argument_list|)
expr_stmt|;
try|try
block|{
name|insertAncestors
argument_list|(
name|ps
operator|.
name|getId
argument_list|()
argument_list|,
name|commit
argument_list|)
expr_stmt|;
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|ps
argument_list|)
argument_list|)
expr_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|change
argument_list|)
argument_list|)
expr_stmt|;
name|addProjectOwnersAsReviewers
argument_list|(
name|change
argument_list|)
expr_stmt|;
name|db
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|db
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
name|indexer
operator|.
name|index
argument_list|(
name|change
argument_list|)
expr_stmt|;
return|return
name|changeId
return|;
block|}
DECL|method|insertAncestors (PatchSet.Id id, RevCommit src)
specifier|private
name|void
name|insertAncestors
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|,
name|RevCommit
name|src
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|int
name|cnt
init|=
name|src
operator|.
name|getParentCount
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PatchSetAncestor
argument_list|>
name|toInsert
init|=
operator|new
name|ArrayList
argument_list|<
name|PatchSetAncestor
argument_list|>
argument_list|(
name|cnt
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|p
init|=
literal|0
init|;
name|p
operator|<
name|cnt
condition|;
name|p
operator|++
control|)
block|{
name|PatchSetAncestor
name|a
decl_stmt|;
name|a
operator|=
operator|new
name|PatchSetAncestor
argument_list|(
operator|new
name|PatchSetAncestor
operator|.
name|Id
argument_list|(
name|id
argument_list|,
name|p
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|a
operator|.
name|setAncestorRevision
argument_list|(
operator|new
name|RevId
argument_list|(
name|src
operator|.
name|getParent
argument_list|(
name|p
argument_list|)
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|toInsert
operator|.
name|add
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
name|db
operator|.
name|patchSetAncestors
argument_list|()
operator|.
name|insert
argument_list|(
name|toInsert
argument_list|)
expr_stmt|;
block|}
DECL|method|addProjectOwnersAsReviewers (final Change change)
specifier|private
name|void
name|addProjectOwnersAsReviewers
parameter_list|(
specifier|final
name|Change
name|change
parameter_list|)
block|{
specifier|final
name|String
name|projectOwners
init|=
name|groupBackend
operator|.
name|get
argument_list|(
name|AccountGroup
operator|.
name|PROJECT_OWNERS
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
try|try
block|{
name|ChangeResource
name|rsrc
init|=
operator|new
name|ChangeResource
argument_list|(
name|changeFactory
operator|.
name|controlFor
argument_list|(
name|change
argument_list|,
name|user
argument_list|)
argument_list|)
decl_stmt|;
name|PostReviewers
operator|.
name|Input
name|input
init|=
operator|new
name|PostReviewers
operator|.
name|Input
argument_list|()
decl_stmt|;
name|input
operator|.
name|reviewer
operator|=
name|projectOwners
expr_stmt|;
name|reviewersProvider
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// one of the owner groups is not visible to the user and this it why it
comment|// can't be added as reviewer
block|}
block|}
block|}
end_class

end_unit

