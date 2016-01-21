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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|MoreObjects
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
name|RelatedChangesSorter
operator|.
name|PatchSetData
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
name|HashSet
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
annotation|@
name|Inject
DECL|method|GetRelated (Provider<ReviewDb> db, Provider<InternalChangeQuery> queryProvider, PatchSetUtil psUtil, RelatedChangesSorter sorter)
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
block|}
annotation|@
name|Override
DECL|method|apply (RevisionResource rsrc)
specifier|public
name|RelatedInfo
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
block|{
name|RelatedInfo
name|relatedInfo
init|=
operator|new
name|RelatedInfo
argument_list|()
decl_stmt|;
name|relatedInfo
operator|.
name|changes
operator|=
name|getRelated
argument_list|(
name|rsrc
argument_list|)
expr_stmt|;
return|return
name|relatedInfo
return|;
block|}
DECL|method|getRelated (RevisionResource rsrc)
specifier|private
name|List
argument_list|<
name|ChangeAndCommit
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
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|enforceVisibility
argument_list|(
literal|true
argument_list|)
operator|.
name|byProjectGroups
argument_list|(
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
name|ChangeAndCommit
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
for|for
control|(
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
operator|new
name|ChangeAndCommit
argument_list|(
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
name|ChangeAndCommit
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
DECL|method|getAllGroups (ChangeNotes notes)
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getAllGroups
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|)
throws|throws
name|OrmException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSet
name|ps
range|:
name|psUtil
operator|.
name|byChange
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|notes
argument_list|)
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
name|ps
operator|.
name|getGroups
argument_list|()
decl_stmt|;
if|if
condition|(
name|groups
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|addAll
argument_list|(
name|groups
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|class|RelatedInfo
specifier|public
specifier|static
class|class
name|RelatedInfo
block|{
DECL|field|changes
specifier|public
name|List
argument_list|<
name|ChangeAndCommit
argument_list|>
name|changes
decl_stmt|;
block|}
DECL|class|ChangeAndCommit
specifier|public
specifier|static
class|class
name|ChangeAndCommit
block|{
DECL|field|changeId
specifier|public
name|String
name|changeId
decl_stmt|;
DECL|field|commit
specifier|public
name|CommitInfo
name|commit
decl_stmt|;
DECL|field|_changeNumber
specifier|public
name|Integer
name|_changeNumber
decl_stmt|;
DECL|field|_revisionNumber
specifier|public
name|Integer
name|_revisionNumber
decl_stmt|;
DECL|field|_currentRevisionNumber
specifier|public
name|Integer
name|_currentRevisionNumber
decl_stmt|;
DECL|field|status
specifier|public
name|String
name|status
decl_stmt|;
DECL|method|ChangeAndCommit ()
specifier|public
name|ChangeAndCommit
parameter_list|()
block|{     }
DECL|method|ChangeAndCommit (@ullable Change change, @Nullable PatchSet ps, RevCommit c)
name|ChangeAndCommit
parameter_list|(
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
if|if
condition|(
name|change
operator|!=
literal|null
condition|)
block|{
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
name|_changeNumber
operator|=
name|change
operator|.
name|getChangeId
argument_list|()
expr_stmt|;
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
name|commit
operator|=
operator|new
name|CommitInfo
argument_list|()
expr_stmt|;
name|commit
operator|.
name|commit
operator|=
name|c
operator|.
name|name
argument_list|()
expr_stmt|;
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
name|commit
operator|.
name|subject
operator|=
name|c
operator|.
name|getShortMessage
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|MoreObjects
operator|.
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|add
argument_list|(
literal|"changeId"
argument_list|,
name|changeId
argument_list|)
operator|.
name|add
argument_list|(
literal|"commit"
argument_list|,
name|toString
argument_list|(
name|commit
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"_changeNumber"
argument_list|,
name|_changeNumber
argument_list|)
operator|.
name|add
argument_list|(
literal|"_revisionNumber"
argument_list|,
name|_revisionNumber
argument_list|)
operator|.
name|add
argument_list|(
literal|"_currentRevisionNumber"
argument_list|,
name|_currentRevisionNumber
argument_list|)
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
name|status
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toString (CommitInfo commit)
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
name|CommitInfo
name|commit
parameter_list|)
block|{
return|return
name|MoreObjects
operator|.
name|toStringHelper
argument_list|(
name|commit
argument_list|)
operator|.
name|add
argument_list|(
literal|"commit"
argument_list|,
name|commit
operator|.
name|commit
argument_list|)
operator|.
name|add
argument_list|(
literal|"parent"
argument_list|,
name|commit
operator|.
name|parents
argument_list|)
operator|.
name|add
argument_list|(
literal|"author"
argument_list|,
name|commit
operator|.
name|author
argument_list|)
operator|.
name|add
argument_list|(
literal|"committer"
argument_list|,
name|commit
operator|.
name|committer
argument_list|)
operator|.
name|add
argument_list|(
literal|"subject"
argument_list|,
name|commit
operator|.
name|subject
argument_list|)
operator|.
name|add
argument_list|(
literal|"message"
argument_list|,
name|commit
operator|.
name|message
argument_list|)
operator|.
name|add
argument_list|(
literal|"webLinks"
argument_list|,
name|commit
operator|.
name|webLinks
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

