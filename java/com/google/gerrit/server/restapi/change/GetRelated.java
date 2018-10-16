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
DECL|package|com.google.gerrit.server.restapi.change
package|package
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
name|change
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toSet
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
name|collect
operator|.
name|Lists
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
name|extensions
operator|.
name|api
operator|.
name|changes
operator|.
name|RelatedChangeAndCommitInfo
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
name|changes
operator|.
name|RelatedChangesInfo
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
name|CommitInfo
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
name|RestReadView
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
name|index
operator|.
name|IndexConfig
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
name|CommonConverters
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
name|change
operator|.
name|RevisionResource
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
name|NoSuchProjectException
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
name|InternalChangeQuery
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
name|Singleton
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
name|revwalk
operator|.
name|RevCommit
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|GetRelated
specifier|public
class|class
name|GetRelated
implements|implements
name|RestReadView
argument_list|<
name|RevisionResource
argument_list|>
block|{
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|queryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
decl_stmt|;
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
DECL|field|sorter
specifier|private
specifier|final
name|RelatedChangesSorter
name|sorter
decl_stmt|;
DECL|field|indexConfig
specifier|private
specifier|final
name|IndexConfig
name|indexConfig
decl_stmt|;
annotation|@
name|Inject
DECL|method|GetRelated ( Provider<ReviewDb> db, Provider<InternalChangeQuery> queryProvider, PatchSetUtil psUtil, RelatedChangesSorter sorter, IndexConfig indexConfig)
name|GetRelated
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
name|RelatedChangesSorter
name|sorter
parameter_list|,
name|IndexConfig
name|indexConfig
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|queryProvider
operator|=
name|queryProvider
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
name|this
operator|.
name|sorter
operator|=
name|sorter
expr_stmt|;
name|this
operator|.
name|indexConfig
operator|=
name|indexConfig
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (RevisionResource rsrc)
specifier|public
name|RelatedChangesInfo
name|apply
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|)
throws|throws
name|RepositoryNotFoundException
throws|,
name|IOException
throws|,
name|OrmException
throws|,
name|NoSuchProjectException
throws|,
name|PermissionBackendException
block|{
name|RelatedChangesInfo
name|relatedChangesInfo
init|=
operator|new
name|RelatedChangesInfo
argument_list|()
decl_stmt|;
name|relatedChangesInfo
operator|.
name|changes
operator|=
name|getRelated
argument_list|(
name|rsrc
argument_list|)
expr_stmt|;
return|return
name|relatedChangesInfo
return|;
block|}
DECL|method|getRelated (RevisionResource rsrc)
specifier|private
name|List
argument_list|<
name|RelatedChangeAndCommitInfo
argument_list|>
name|getRelated
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|PermissionBackendException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|groups
init|=
name|getAllGroups
argument_list|(
name|rsrc
operator|.
name|getNotes
argument_list|()
argument_list|,
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|psUtil
argument_list|)
decl_stmt|;
if|if
condition|(
name|groups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|ChangeData
argument_list|>
name|cds
init|=
name|InternalChangeQuery
operator|.
name|byProjectGroups
argument_list|(
name|queryProvider
argument_list|,
name|indexConfig
argument_list|,
name|rsrc
operator|.
name|getChange
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|,
name|groups
argument_list|)
decl_stmt|;
if|if
condition|(
name|cds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
if|if
condition|(
name|cds
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|cds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|rsrc
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|RelatedChangeAndCommitInfo
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|cds
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|isEdit
init|=
name|rsrc
operator|.
name|getEdit
argument_list|()
operator|.
name|isPresent
argument_list|()
decl_stmt|;
name|PatchSet
name|basePs
init|=
name|isEdit
condition|?
name|rsrc
operator|.
name|getEdit
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getBasePatchSet
argument_list|()
else|:
name|rsrc
operator|.
name|getPatchSet
argument_list|()
decl_stmt|;
name|reloadChangeIfStale
argument_list|(
name|cds
argument_list|,
name|basePs
argument_list|)
expr_stmt|;
for|for
control|(
name|RelatedChangesSorter
operator|.
name|PatchSetData
name|d
range|:
name|sorter
operator|.
name|sort
argument_list|(
name|cds
argument_list|,
name|basePs
argument_list|)
control|)
block|{
name|PatchSet
name|ps
init|=
name|d
operator|.
name|patchSet
argument_list|()
decl_stmt|;
name|RevCommit
name|commit
decl_stmt|;
if|if
condition|(
name|isEdit
operator|&&
name|ps
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|basePs
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
comment|// Replace base of an edit with the edit itself.
name|ps
operator|=
name|rsrc
operator|.
name|getPatchSet
argument_list|()
expr_stmt|;
name|commit
operator|=
name|rsrc
operator|.
name|getEdit
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getEditCommit
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|commit
operator|=
name|d
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|newChangeAndCommit
argument_list|(
name|rsrc
operator|.
name|getProject
argument_list|()
argument_list|,
name|d
operator|.
name|data
argument_list|()
operator|.
name|change
argument_list|()
argument_list|,
name|ps
argument_list|,
name|commit
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|RelatedChangeAndCommitInfo
name|r
init|=
name|result
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|commit
operator|!=
literal|null
operator|&&
name|r
operator|.
name|commit
operator|.
name|commit
operator|.
name|equals
argument_list|(
name|rsrc
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getAllGroups (ChangeNotes notes, ReviewDb db, PatchSetUtil psUtil)
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getAllGroups
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|ReviewDb
name|db
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|psUtil
operator|.
name|byChange
argument_list|(
name|db
argument_list|,
name|notes
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|flatMap
argument_list|(
name|ps
lambda|->
name|ps
operator|.
name|getGroups
argument_list|()
operator|.
name|stream
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|toSet
argument_list|()
argument_list|)
return|;
block|}
DECL|method|reloadChangeIfStale (List<ChangeData> cds, PatchSet wantedPs)
specifier|private
name|void
name|reloadChangeIfStale
parameter_list|(
name|List
argument_list|<
name|ChangeData
argument_list|>
name|cds
parameter_list|,
name|PatchSet
name|wantedPs
parameter_list|)
throws|throws
name|OrmException
block|{
for|for
control|(
name|ChangeData
name|cd
range|:
name|cds
control|)
block|{
if|if
condition|(
name|cd
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|wantedPs
operator|.
name|getId
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|cd
operator|.
name|patchSet
argument_list|(
name|wantedPs
operator|.
name|getId
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
name|cd
operator|.
name|reloadChange
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|newChangeAndCommit ( Project.NameKey project, @Nullable Change change, @Nullable PatchSet ps, RevCommit c)
specifier|static
name|RelatedChangeAndCommitInfo
name|newChangeAndCommit
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
annotation|@
name|Nullable
name|Change
name|change
parameter_list|,
annotation|@
name|Nullable
name|PatchSet
name|ps
parameter_list|,
name|RevCommit
name|c
parameter_list|)
block|{
name|RelatedChangeAndCommitInfo
name|info
init|=
operator|new
name|RelatedChangeAndCommitInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|project
operator|=
name|project
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|change
operator|!=
literal|null
condition|)
block|{
name|info
operator|.
name|changeId
operator|=
name|change
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|info
operator|.
name|_changeNumber
operator|=
name|change
operator|.
name|getChangeId
argument_list|()
expr_stmt|;
name|info
operator|.
name|_revisionNumber
operator|=
name|ps
operator|!=
literal|null
condition|?
name|ps
operator|.
name|getPatchSetId
argument_list|()
else|:
literal|null
expr_stmt|;
name|PatchSet
operator|.
name|Id
name|curr
init|=
name|change
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
name|info
operator|.
name|_currentRevisionNumber
operator|=
name|curr
operator|!=
literal|null
condition|?
name|curr
operator|.
name|get
argument_list|()
else|:
literal|null
expr_stmt|;
name|info
operator|.
name|status
operator|=
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|asChangeStatus
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|info
operator|.
name|commit
operator|=
operator|new
name|CommitInfo
argument_list|()
expr_stmt|;
name|info
operator|.
name|commit
operator|.
name|commit
operator|=
name|c
operator|.
name|name
argument_list|()
expr_stmt|;
name|info
operator|.
name|commit
operator|.
name|parents
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|c
operator|.
name|getParentCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|c
operator|.
name|getParentCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|CommitInfo
name|p
init|=
operator|new
name|CommitInfo
argument_list|()
decl_stmt|;
name|p
operator|.
name|commit
operator|=
name|c
operator|.
name|getParent
argument_list|(
name|i
argument_list|)
operator|.
name|name
argument_list|()
expr_stmt|;
name|info
operator|.
name|commit
operator|.
name|parents
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|commit
operator|.
name|author
operator|=
name|CommonConverters
operator|.
name|toGitPerson
argument_list|(
name|c
operator|.
name|getAuthorIdent
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|commit
operator|.
name|subject
operator|=
name|c
operator|.
name|getShortMessage
argument_list|()
expr_stmt|;
return|return
name|info
return|;
block|}
block|}
end_class

end_unit

