begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.events
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|events
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
name|data
operator|.
name|ApprovalType
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
name|ApprovalTypes
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
name|ChangeMessage
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
name|PatchSetApproval
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
name|client
operator|.
name|TrackingId
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
name|account
operator|.
name|AccountCache
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
name|patch
operator|.
name|PatchList
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
name|PatchListCache
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
name|PatchListEntry
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
name|gwtorm
operator|.
name|server
operator|.
name|SchemaFactory
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
name|lib
operator|.
name|ObjectId
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
name|Collection
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|EventFactory
specifier|public
class|class
name|EventFactory
block|{
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|urlProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|String
argument_list|>
name|urlProvider
decl_stmt|;
DECL|field|approvalTypes
specifier|private
specifier|final
name|ApprovalTypes
name|approvalTypes
decl_stmt|;
DECL|field|patchListCache
specifier|private
specifier|final
name|PatchListCache
name|patchListCache
decl_stmt|;
DECL|field|schema
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
annotation|@
name|Inject
DECL|method|EventFactory (AccountCache accountCache, @CanonicalWebUrl @Nullable Provider<String> urlProvider, ApprovalTypes approvalTypes, PatchListCache patchListCache, SchemaFactory<ReviewDb> schema)
name|EventFactory
parameter_list|(
name|AccountCache
name|accountCache
parameter_list|,
annotation|@
name|CanonicalWebUrl
annotation|@
name|Nullable
name|Provider
argument_list|<
name|String
argument_list|>
name|urlProvider
parameter_list|,
name|ApprovalTypes
name|approvalTypes
parameter_list|,
name|PatchListCache
name|patchListCache
parameter_list|,
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
parameter_list|)
block|{
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|urlProvider
operator|=
name|urlProvider
expr_stmt|;
name|this
operator|.
name|approvalTypes
operator|=
name|approvalTypes
expr_stmt|;
name|this
operator|.
name|patchListCache
operator|=
name|patchListCache
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
block|}
comment|/**    * Create a ChangeAttribute for the given change suitable for serialization to    * JSON.    *    * @param change    * @return object suitable for serialization to JSON    */
DECL|method|asChangeAttribute (final Change change)
specifier|public
name|ChangeAttribute
name|asChangeAttribute
parameter_list|(
specifier|final
name|Change
name|change
parameter_list|)
block|{
name|ChangeAttribute
name|a
init|=
operator|new
name|ChangeAttribute
argument_list|()
decl_stmt|;
name|a
operator|.
name|project
operator|=
name|change
operator|.
name|getProject
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|a
operator|.
name|branch
operator|=
name|change
operator|.
name|getDest
argument_list|()
operator|.
name|getShortName
argument_list|()
expr_stmt|;
name|a
operator|.
name|topic
operator|=
name|change
operator|.
name|getTopic
argument_list|()
expr_stmt|;
name|a
operator|.
name|id
operator|=
name|change
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|a
operator|.
name|number
operator|=
name|change
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|a
operator|.
name|subject
operator|=
name|change
operator|.
name|getSubject
argument_list|()
expr_stmt|;
name|a
operator|.
name|url
operator|=
name|getChangeUrl
argument_list|(
name|change
argument_list|)
expr_stmt|;
name|a
operator|.
name|owner
operator|=
name|asAccountAttribute
argument_list|(
name|change
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|a
return|;
block|}
comment|/**    * Create a RefUpdateAttribute for the given old ObjectId, new ObjectId, and    * branch that is suitable for serialization to JSON.    *    * @param refUpdate    * @param refName    * @return object suitable for serialization to JSON    */
DECL|method|asRefUpdateAttribute (final ObjectId oldId, final ObjectId newId, final Branch.NameKey refName)
specifier|public
name|RefUpdateAttribute
name|asRefUpdateAttribute
parameter_list|(
specifier|final
name|ObjectId
name|oldId
parameter_list|,
specifier|final
name|ObjectId
name|newId
parameter_list|,
specifier|final
name|Branch
operator|.
name|NameKey
name|refName
parameter_list|)
block|{
name|RefUpdateAttribute
name|ru
init|=
operator|new
name|RefUpdateAttribute
argument_list|()
decl_stmt|;
name|ru
operator|.
name|newRev
operator|=
name|newId
operator|!=
literal|null
condition|?
name|newId
operator|.
name|getName
argument_list|()
else|:
name|ObjectId
operator|.
name|zeroId
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
name|ru
operator|.
name|oldRev
operator|=
name|oldId
operator|!=
literal|null
condition|?
name|oldId
operator|.
name|getName
argument_list|()
else|:
name|ObjectId
operator|.
name|zeroId
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
name|ru
operator|.
name|project
operator|=
name|refName
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|ru
operator|.
name|refName
operator|=
name|refName
operator|.
name|getShortName
argument_list|()
expr_stmt|;
return|return
name|ru
return|;
block|}
comment|/**    * Extend the existing ChangeAttribute with additional fields.    *    * @param a    * @param change    */
DECL|method|extend (ChangeAttribute a, Change change)
specifier|public
name|void
name|extend
parameter_list|(
name|ChangeAttribute
name|a
parameter_list|,
name|Change
name|change
parameter_list|)
block|{
name|a
operator|.
name|createdOn
operator|=
name|change
operator|.
name|getCreatedOn
argument_list|()
operator|.
name|getTime
argument_list|()
operator|/
literal|1000L
expr_stmt|;
name|a
operator|.
name|lastUpdated
operator|=
name|change
operator|.
name|getLastUpdatedOn
argument_list|()
operator|.
name|getTime
argument_list|()
operator|/
literal|1000L
expr_stmt|;
name|a
operator|.
name|sortKey
operator|=
name|change
operator|.
name|getSortKey
argument_list|()
expr_stmt|;
name|a
operator|.
name|open
operator|=
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
expr_stmt|;
name|a
operator|.
name|status
operator|=
name|change
operator|.
name|getStatus
argument_list|()
expr_stmt|;
block|}
DECL|method|addDependencies (ChangeAttribute ca, Change change)
specifier|public
name|void
name|addDependencies
parameter_list|(
name|ChangeAttribute
name|ca
parameter_list|,
name|Change
name|change
parameter_list|)
block|{
name|ca
operator|.
name|dependsOn
operator|=
operator|new
name|ArrayList
argument_list|<
name|DependencyAttribute
argument_list|>
argument_list|()
expr_stmt|;
name|ca
operator|.
name|neededBy
operator|=
operator|new
name|ArrayList
argument_list|<
name|DependencyAttribute
argument_list|>
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|PatchSet
operator|.
name|Id
name|psId
init|=
name|change
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetAncestor
name|a
range|:
name|db
operator|.
name|patchSetAncestors
argument_list|()
operator|.
name|ancestorsOf
argument_list|(
name|psId
argument_list|)
control|)
block|{
for|for
control|(
name|PatchSet
name|p
range|:
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|byRevision
argument_list|(
name|a
operator|.
name|getAncestorRevision
argument_list|()
argument_list|)
control|)
block|{
name|Change
name|c
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|p
operator|.
name|getId
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
name|ca
operator|.
name|dependsOn
operator|.
name|add
argument_list|(
name|newDependsOn
argument_list|(
name|c
argument_list|,
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|RevId
name|revId
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
operator|.
name|getRevision
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetAncestor
name|a
range|:
name|db
operator|.
name|patchSetAncestors
argument_list|()
operator|.
name|descendantsOf
argument_list|(
name|revId
argument_list|)
control|)
block|{
specifier|final
name|PatchSet
name|p
init|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|a
operator|.
name|getPatchSet
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Change
name|c
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|p
operator|.
name|getId
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
name|ca
operator|.
name|neededBy
operator|.
name|add
argument_list|(
name|newNeededBy
argument_list|(
name|c
argument_list|,
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
comment|// Squash DB exceptions and leave dependency lists partially filled.
block|}
comment|// Remove empty lists so a confusing label won't be displayed in the output.
if|if
condition|(
name|ca
operator|.
name|dependsOn
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ca
operator|.
name|dependsOn
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|ca
operator|.
name|neededBy
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ca
operator|.
name|neededBy
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|newDependsOn (Change c, PatchSet ps)
specifier|private
name|DependencyAttribute
name|newDependsOn
parameter_list|(
name|Change
name|c
parameter_list|,
name|PatchSet
name|ps
parameter_list|)
block|{
name|DependencyAttribute
name|d
init|=
name|newDependencyAttribute
argument_list|(
name|c
argument_list|,
name|ps
argument_list|)
decl_stmt|;
name|d
operator|.
name|isCurrentPatchSet
operator|=
name|c
operator|.
name|currPatchSetId
argument_list|()
operator|.
name|equals
argument_list|(
name|ps
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|d
return|;
block|}
DECL|method|newNeededBy (Change c, PatchSet ps)
specifier|private
name|DependencyAttribute
name|newNeededBy
parameter_list|(
name|Change
name|c
parameter_list|,
name|PatchSet
name|ps
parameter_list|)
block|{
return|return
name|newDependencyAttribute
argument_list|(
name|c
argument_list|,
name|ps
argument_list|)
return|;
block|}
DECL|method|newDependencyAttribute (Change c, PatchSet ps)
specifier|private
name|DependencyAttribute
name|newDependencyAttribute
parameter_list|(
name|Change
name|c
parameter_list|,
name|PatchSet
name|ps
parameter_list|)
block|{
name|DependencyAttribute
name|d
init|=
operator|new
name|DependencyAttribute
argument_list|()
decl_stmt|;
name|d
operator|.
name|number
operator|=
name|c
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|d
operator|.
name|id
operator|=
name|c
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|d
operator|.
name|revision
operator|=
name|ps
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|d
operator|.
name|ref
operator|=
name|ps
operator|.
name|getRefName
argument_list|()
expr_stmt|;
return|return
name|d
return|;
block|}
DECL|method|addTrackingIds (ChangeAttribute a, Collection<TrackingId> ids)
specifier|public
name|void
name|addTrackingIds
parameter_list|(
name|ChangeAttribute
name|a
parameter_list|,
name|Collection
argument_list|<
name|TrackingId
argument_list|>
name|ids
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ids
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|a
operator|.
name|trackingIds
operator|=
operator|new
name|ArrayList
argument_list|<
name|TrackingIdAttribute
argument_list|>
argument_list|(
name|ids
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|TrackingId
name|t
range|:
name|ids
control|)
block|{
name|a
operator|.
name|trackingIds
operator|.
name|add
argument_list|(
name|asTrackingIdAttribute
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addCommitMessage (ChangeAttribute a, String commitMessage)
specifier|public
name|void
name|addCommitMessage
parameter_list|(
name|ChangeAttribute
name|a
parameter_list|,
name|String
name|commitMessage
parameter_list|)
block|{
name|a
operator|.
name|commitMessage
operator|=
name|commitMessage
expr_stmt|;
block|}
DECL|method|addPatchSets (ChangeAttribute a, Collection<PatchSet> ps)
specifier|public
name|void
name|addPatchSets
parameter_list|(
name|ChangeAttribute
name|a
parameter_list|,
name|Collection
argument_list|<
name|PatchSet
argument_list|>
name|ps
parameter_list|)
block|{
name|addPatchSets
argument_list|(
name|a
argument_list|,
name|ps
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|addPatchSets (ChangeAttribute ca, Collection<PatchSet> ps, Map<PatchSet.Id,Collection<PatchSetApproval>> approvals)
specifier|public
name|void
name|addPatchSets
parameter_list|(
name|ChangeAttribute
name|ca
parameter_list|,
name|Collection
argument_list|<
name|PatchSet
argument_list|>
name|ps
parameter_list|,
name|Map
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|,
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
argument_list|>
name|approvals
parameter_list|)
block|{
name|addPatchSets
argument_list|(
name|ca
argument_list|,
name|ps
argument_list|,
name|approvals
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|addPatchSets (ChangeAttribute ca, Collection<PatchSet> ps, Map<PatchSet.Id,Collection<PatchSetApproval>> approvals, boolean includeFiles, Change change)
specifier|public
name|void
name|addPatchSets
parameter_list|(
name|ChangeAttribute
name|ca
parameter_list|,
name|Collection
argument_list|<
name|PatchSet
argument_list|>
name|ps
parameter_list|,
name|Map
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|,
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
argument_list|>
name|approvals
parameter_list|,
name|boolean
name|includeFiles
parameter_list|,
name|Change
name|change
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ca
operator|.
name|patchSets
operator|=
operator|new
name|ArrayList
argument_list|<
name|PatchSetAttribute
argument_list|>
argument_list|(
name|ps
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|PatchSet
name|p
range|:
name|ps
control|)
block|{
name|PatchSetAttribute
name|psa
init|=
name|asPatchSetAttribute
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|approvals
operator|!=
literal|null
condition|)
block|{
name|addApprovals
argument_list|(
name|psa
argument_list|,
name|p
operator|.
name|getId
argument_list|()
argument_list|,
name|approvals
argument_list|)
expr_stmt|;
block|}
name|ca
operator|.
name|patchSets
operator|.
name|add
argument_list|(
name|psa
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeFiles
operator|&&
name|change
operator|!=
literal|null
condition|)
block|{
name|addPatchSetFileNames
argument_list|(
name|psa
argument_list|,
name|change
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|addPatchSetComments (PatchSetAttribute patchSetAttribute, Collection<PatchLineComment> patchLineComments)
specifier|public
name|void
name|addPatchSetComments
parameter_list|(
name|PatchSetAttribute
name|patchSetAttribute
parameter_list|,
name|Collection
argument_list|<
name|PatchLineComment
argument_list|>
name|patchLineComments
parameter_list|)
block|{
for|for
control|(
name|PatchLineComment
name|comment
range|:
name|patchLineComments
control|)
block|{
if|if
condition|(
name|comment
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
operator|==
name|Integer
operator|.
name|parseInt
argument_list|(
name|patchSetAttribute
operator|.
name|number
argument_list|)
condition|)
block|{
if|if
condition|(
name|patchSetAttribute
operator|.
name|comments
operator|==
literal|null
condition|)
block|{
name|patchSetAttribute
operator|.
name|comments
operator|=
operator|new
name|ArrayList
argument_list|<
name|PatchSetCommentAttribute
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|patchSetAttribute
operator|.
name|comments
operator|.
name|add
argument_list|(
name|asPatchSetLineAttribute
argument_list|(
name|comment
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addPatchSetFileNames (PatchSetAttribute patchSetAttribute, Change change, PatchSet patchSet)
specifier|public
name|void
name|addPatchSetFileNames
parameter_list|(
name|PatchSetAttribute
name|patchSetAttribute
parameter_list|,
name|Change
name|change
parameter_list|,
name|PatchSet
name|patchSet
parameter_list|)
block|{
name|PatchList
name|patchList
init|=
name|patchListCache
operator|.
name|get
argument_list|(
name|change
argument_list|,
name|patchSet
argument_list|)
decl_stmt|;
for|for
control|(
name|PatchListEntry
name|patch
range|:
name|patchList
operator|.
name|getPatches
argument_list|()
control|)
block|{
if|if
condition|(
name|patchSetAttribute
operator|.
name|files
operator|==
literal|null
condition|)
block|{
name|patchSetAttribute
operator|.
name|files
operator|=
operator|new
name|ArrayList
argument_list|<
name|PatchAttribute
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|PatchAttribute
name|p
init|=
operator|new
name|PatchAttribute
argument_list|()
decl_stmt|;
name|p
operator|.
name|file
operator|=
name|patch
operator|.
name|getNewName
argument_list|()
expr_stmt|;
name|p
operator|.
name|type
operator|=
name|patch
operator|.
name|getChangeType
argument_list|()
expr_stmt|;
name|patchSetAttribute
operator|.
name|files
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addComments (ChangeAttribute ca, Collection<ChangeMessage> messages)
specifier|public
name|void
name|addComments
parameter_list|(
name|ChangeAttribute
name|ca
parameter_list|,
name|Collection
argument_list|<
name|ChangeMessage
argument_list|>
name|messages
parameter_list|)
block|{
if|if
condition|(
operator|!
name|messages
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ca
operator|.
name|comments
operator|=
operator|new
name|ArrayList
argument_list|<
name|MessageAttribute
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ChangeMessage
name|message
range|:
name|messages
control|)
block|{
name|ca
operator|.
name|comments
operator|.
name|add
argument_list|(
name|asMessageAttribute
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|asTrackingIdAttribute (TrackingId id)
specifier|public
name|TrackingIdAttribute
name|asTrackingIdAttribute
parameter_list|(
name|TrackingId
name|id
parameter_list|)
block|{
name|TrackingIdAttribute
name|a
init|=
operator|new
name|TrackingIdAttribute
argument_list|()
decl_stmt|;
name|a
operator|.
name|system
operator|=
name|id
operator|.
name|getSystem
argument_list|()
expr_stmt|;
name|a
operator|.
name|id
operator|=
name|id
operator|.
name|getTrackingId
argument_list|()
expr_stmt|;
return|return
name|a
return|;
block|}
comment|/**    * Create a PatchSetAttribute for the given patchset suitable for    * serialization to JSON.    *    * @param patchSet    * @return object suitable for serialization to JSON    */
DECL|method|asPatchSetAttribute (final PatchSet patchSet)
specifier|public
name|PatchSetAttribute
name|asPatchSetAttribute
parameter_list|(
specifier|final
name|PatchSet
name|patchSet
parameter_list|)
block|{
name|PatchSetAttribute
name|p
init|=
operator|new
name|PatchSetAttribute
argument_list|()
decl_stmt|;
name|p
operator|.
name|revision
operator|=
name|patchSet
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|p
operator|.
name|number
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|patchSet
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|ref
operator|=
name|patchSet
operator|.
name|getRefName
argument_list|()
expr_stmt|;
name|p
operator|.
name|uploader
operator|=
name|asAccountAttribute
argument_list|(
name|patchSet
operator|.
name|getUploader
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|createdOn
operator|=
name|patchSet
operator|.
name|getCreatedOn
argument_list|()
operator|.
name|getTime
argument_list|()
operator|/
literal|1000L
expr_stmt|;
return|return
name|p
return|;
block|}
DECL|method|addApprovals (PatchSetAttribute p, PatchSet.Id id, Map<PatchSet.Id,Collection<PatchSetApproval>> all)
specifier|public
name|void
name|addApprovals
parameter_list|(
name|PatchSetAttribute
name|p
parameter_list|,
name|PatchSet
operator|.
name|Id
name|id
parameter_list|,
name|Map
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|,
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
argument_list|>
name|all
parameter_list|)
block|{
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|list
init|=
name|all
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
condition|)
block|{
name|addApprovals
argument_list|(
name|p
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addApprovals (PatchSetAttribute p, Collection<PatchSetApproval> list)
specifier|public
name|void
name|addApprovals
parameter_list|(
name|PatchSetAttribute
name|p
parameter_list|,
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|list
parameter_list|)
block|{
if|if
condition|(
operator|!
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|p
operator|.
name|approvals
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApprovalAttribute
argument_list|>
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|PatchSetApproval
name|a
range|:
name|list
control|)
block|{
if|if
condition|(
name|a
operator|.
name|getValue
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|p
operator|.
name|approvals
operator|.
name|add
argument_list|(
name|asApprovalAttribute
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|p
operator|.
name|approvals
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|p
operator|.
name|approvals
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Create an AuthorAttribute for the given account suitable for serialization    * to JSON.    *    * @param id    * @return object suitable for serialization to JSON    */
DECL|method|asAccountAttribute (Account.Id id)
specifier|public
name|AccountAttribute
name|asAccountAttribute
parameter_list|(
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
return|return
name|asAccountAttribute
argument_list|(
name|accountCache
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|.
name|getAccount
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Create an AuthorAttribute for the given account suitable for serialization    * to JSON.    *    * @param account    * @return object suitable for serialization to JSON    */
DECL|method|asAccountAttribute (final Account account)
specifier|public
name|AccountAttribute
name|asAccountAttribute
parameter_list|(
specifier|final
name|Account
name|account
parameter_list|)
block|{
name|AccountAttribute
name|who
init|=
operator|new
name|AccountAttribute
argument_list|()
decl_stmt|;
name|who
operator|.
name|name
operator|=
name|account
operator|.
name|getFullName
argument_list|()
expr_stmt|;
name|who
operator|.
name|email
operator|=
name|account
operator|.
name|getPreferredEmail
argument_list|()
expr_stmt|;
return|return
name|who
return|;
block|}
comment|/**    * Create an ApprovalAttribute for the given approval suitable for    * serialization to JSON.    *    * @param approval    * @return object suitable for serialization to JSON    */
DECL|method|asApprovalAttribute (PatchSetApproval approval)
specifier|public
name|ApprovalAttribute
name|asApprovalAttribute
parameter_list|(
name|PatchSetApproval
name|approval
parameter_list|)
block|{
name|ApprovalAttribute
name|a
init|=
operator|new
name|ApprovalAttribute
argument_list|()
decl_stmt|;
name|a
operator|.
name|type
operator|=
name|approval
operator|.
name|getCategoryId
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|a
operator|.
name|value
operator|=
name|Short
operator|.
name|toString
argument_list|(
name|approval
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|.
name|by
operator|=
name|asAccountAttribute
argument_list|(
name|approval
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|.
name|grantedOn
operator|=
name|approval
operator|.
name|getGranted
argument_list|()
operator|.
name|getTime
argument_list|()
operator|/
literal|1000L
expr_stmt|;
name|ApprovalType
name|at
init|=
name|approvalTypes
operator|.
name|byId
argument_list|(
name|approval
operator|.
name|getCategoryId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|at
operator|!=
literal|null
condition|)
block|{
name|a
operator|.
name|description
operator|=
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
DECL|method|asMessageAttribute (ChangeMessage message)
specifier|public
name|MessageAttribute
name|asMessageAttribute
parameter_list|(
name|ChangeMessage
name|message
parameter_list|)
block|{
name|MessageAttribute
name|a
init|=
operator|new
name|MessageAttribute
argument_list|()
decl_stmt|;
name|a
operator|.
name|timestamp
operator|=
name|message
operator|.
name|getWrittenOn
argument_list|()
operator|.
name|getTime
argument_list|()
operator|/
literal|1000L
expr_stmt|;
name|a
operator|.
name|reviewer
operator|=
name|asAccountAttribute
argument_list|(
name|message
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|.
name|message
operator|=
name|message
operator|.
name|getMessage
argument_list|()
expr_stmt|;
return|return
name|a
return|;
block|}
DECL|method|asPatchSetLineAttribute (PatchLineComment c)
specifier|public
name|PatchSetCommentAttribute
name|asPatchSetLineAttribute
parameter_list|(
name|PatchLineComment
name|c
parameter_list|)
block|{
name|PatchSetCommentAttribute
name|a
init|=
operator|new
name|PatchSetCommentAttribute
argument_list|()
decl_stmt|;
name|a
operator|.
name|reviewer
operator|=
name|asAccountAttribute
argument_list|(
name|c
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|.
name|file
operator|=
name|c
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|a
operator|.
name|line
operator|=
name|c
operator|.
name|getLine
argument_list|()
expr_stmt|;
name|a
operator|.
name|message
operator|=
name|c
operator|.
name|getMessage
argument_list|()
expr_stmt|;
return|return
name|a
return|;
block|}
comment|/** Get a link to the change; null if the server doesn't know its own address. */
DECL|method|getChangeUrl (final Change change)
specifier|private
name|String
name|getChangeUrl
parameter_list|(
specifier|final
name|Change
name|change
parameter_list|)
block|{
if|if
condition|(
name|change
operator|!=
literal|null
operator|&&
name|urlProvider
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
specifier|final
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
name|urlProvider
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

