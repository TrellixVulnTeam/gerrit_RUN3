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
DECL|package|com.google.gerrit.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
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
name|HashBasedTable
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
name|ListMultimap
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
name|Table
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
name|common
operator|.
name|data
operator|.
name|LabelType
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
name|server
operator|.
name|change
operator|.
name|ChangeKindCache
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
name|LabelNormalizer
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
name|Collection
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
name|TreeMap
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
name|revwalk
operator|.
name|RevWalk
import|;
end_import

begin_comment
comment|/**  * Copies approvals between patch sets.  *  *<p>The result of a copy may either be stored, as when stamping approvals in the database at  * submit time, or refreshed on demand, as when reading approvals from the NoteDb.  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|ApprovalCopier
specifier|public
class|class
name|ApprovalCopier
block|{
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|changeKindCache
specifier|private
specifier|final
name|ChangeKindCache
name|changeKindCache
decl_stmt|;
DECL|field|labelNormalizer
specifier|private
specifier|final
name|LabelNormalizer
name|labelNormalizer
decl_stmt|;
DECL|field|changeDataFactory
specifier|private
specifier|final
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
decl_stmt|;
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
annotation|@
name|Inject
DECL|method|ApprovalCopier ( ProjectCache projectCache, ChangeKindCache changeKindCache, LabelNormalizer labelNormalizer, ChangeData.Factory changeDataFactory, PatchSetUtil psUtil)
name|ApprovalCopier
parameter_list|(
name|ProjectCache
name|projectCache
parameter_list|,
name|ChangeKindCache
name|changeKindCache
parameter_list|,
name|LabelNormalizer
name|labelNormalizer
parameter_list|,
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|)
block|{
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|changeKindCache
operator|=
name|changeKindCache
expr_stmt|;
name|this
operator|.
name|labelNormalizer
operator|=
name|labelNormalizer
expr_stmt|;
name|this
operator|.
name|changeDataFactory
operator|=
name|changeDataFactory
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
block|}
DECL|method|getForPatchSet ( ChangeNotes notes, PatchSet.Id psId, @Nullable RevWalk rw, @Nullable Config repoConfig)
name|Iterable
argument_list|<
name|PatchSetApproval
argument_list|>
name|getForPatchSet
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
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
throws|throws
name|StorageException
block|{
return|return
name|getForPatchSet
argument_list|(
name|notes
argument_list|,
name|psId
argument_list|,
name|rw
argument_list|,
name|repoConfig
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getForPatchSet ( ChangeNotes notes, PatchSet.Id psId, @Nullable RevWalk rw, @Nullable Config repoConfig, Iterable<PatchSetApproval> dontCopy)
name|Iterable
argument_list|<
name|PatchSetApproval
argument_list|>
name|getForPatchSet
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
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
name|Iterable
argument_list|<
name|PatchSetApproval
argument_list|>
name|dontCopy
parameter_list|)
throws|throws
name|StorageException
block|{
name|PatchSet
name|ps
init|=
name|psUtil
operator|.
name|get
argument_list|(
name|notes
argument_list|,
name|psId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
return|return
name|getForPatchSet
argument_list|(
name|notes
argument_list|,
name|ps
argument_list|,
name|rw
argument_list|,
name|repoConfig
argument_list|,
name|dontCopy
argument_list|)
return|;
block|}
DECL|method|getForPatchSet ( ChangeNotes notes, PatchSet ps, @Nullable RevWalk rw, @Nullable Config repoConfig, Iterable<PatchSetApproval> dontCopy)
specifier|private
name|Iterable
argument_list|<
name|PatchSetApproval
argument_list|>
name|getForPatchSet
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|PatchSet
name|ps
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
name|Iterable
argument_list|<
name|PatchSetApproval
argument_list|>
name|dontCopy
parameter_list|)
throws|throws
name|StorageException
block|{
name|requireNonNull
argument_list|(
name|ps
argument_list|,
literal|"ps should not be null"
argument_list|)
expr_stmt|;
name|ChangeData
name|cd
init|=
name|changeDataFactory
operator|.
name|create
argument_list|(
name|notes
argument_list|)
decl_stmt|;
try|try
block|{
name|ProjectState
name|project
init|=
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|cd
operator|.
name|change
argument_list|()
operator|.
name|getDest
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
name|ListMultimap
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|,
name|PatchSetApproval
argument_list|>
name|all
init|=
name|cd
operator|.
name|approvals
argument_list|()
decl_stmt|;
name|requireNonNull
argument_list|(
name|all
argument_list|,
literal|"all should not be null"
argument_list|)
expr_stmt|;
name|Table
argument_list|<
name|String
argument_list|,
name|Account
operator|.
name|Id
argument_list|,
name|PatchSetApproval
argument_list|>
name|wontCopy
init|=
name|HashBasedTable
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|psa
range|:
name|dontCopy
control|)
block|{
name|wontCopy
operator|.
name|put
argument_list|(
name|psa
operator|.
name|getLabel
argument_list|()
argument_list|,
name|psa
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|psa
argument_list|)
expr_stmt|;
block|}
name|Table
argument_list|<
name|String
argument_list|,
name|Account
operator|.
name|Id
argument_list|,
name|PatchSetApproval
argument_list|>
name|byUser
init|=
name|HashBasedTable
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|psa
range|:
name|all
operator|.
name|get
argument_list|(
name|ps
operator|.
name|getId
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|wontCopy
operator|.
name|contains
argument_list|(
name|psa
operator|.
name|getLabel
argument_list|()
argument_list|,
name|psa
operator|.
name|getAccountId
argument_list|()
argument_list|)
condition|)
block|{
name|byUser
operator|.
name|put
argument_list|(
name|psa
operator|.
name|getLabel
argument_list|()
argument_list|,
name|psa
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|psa
argument_list|)
expr_stmt|;
block|}
block|}
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|PatchSet
argument_list|>
name|patchSets
init|=
name|getPatchSets
argument_list|(
name|cd
argument_list|)
decl_stmt|;
comment|// Walk patch sets strictly less than current in descending order.
name|Collection
argument_list|<
name|PatchSet
argument_list|>
name|allPrior
init|=
name|patchSets
operator|.
name|descendingMap
argument_list|()
operator|.
name|tailMap
argument_list|(
name|ps
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSet
name|priorPs
range|:
name|allPrior
control|)
block|{
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|priorApprovals
init|=
name|all
operator|.
name|get
argument_list|(
name|priorPs
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|priorApprovals
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|ChangeKind
name|kind
init|=
name|changeKindCache
operator|.
name|getChangeKind
argument_list|(
name|project
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|rw
argument_list|,
name|repoConfig
argument_list|,
name|ObjectId
operator|.
name|fromString
argument_list|(
name|priorPs
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
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
argument_list|)
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|psa
range|:
name|priorApprovals
control|)
block|{
if|if
condition|(
name|wontCopy
operator|.
name|contains
argument_list|(
name|psa
operator|.
name|getLabel
argument_list|()
argument_list|,
name|psa
operator|.
name|getAccountId
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|byUser
operator|.
name|contains
argument_list|(
name|psa
operator|.
name|getLabel
argument_list|()
argument_list|,
name|psa
operator|.
name|getAccountId
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|canCopy
argument_list|(
name|project
argument_list|,
name|psa
argument_list|,
name|ps
operator|.
name|getId
argument_list|()
argument_list|,
name|kind
argument_list|)
condition|)
block|{
name|wontCopy
operator|.
name|put
argument_list|(
name|psa
operator|.
name|getLabel
argument_list|()
argument_list|,
name|psa
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|psa
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|byUser
operator|.
name|put
argument_list|(
name|psa
operator|.
name|getLabel
argument_list|()
argument_list|,
name|psa
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|copy
argument_list|(
name|psa
argument_list|,
name|ps
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|labelNormalizer
operator|.
name|normalize
argument_list|(
name|notes
argument_list|,
name|byUser
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|getNormalized
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|StorageException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getPatchSets (ChangeData cd)
specifier|private
specifier|static
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|PatchSet
argument_list|>
name|getPatchSets
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|StorageException
block|{
name|Collection
argument_list|<
name|PatchSet
argument_list|>
name|patchSets
init|=
name|cd
operator|.
name|patchSets
argument_list|()
decl_stmt|;
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|PatchSet
argument_list|>
name|result
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSet
name|ps
range|:
name|patchSets
control|)
block|{
name|result
operator|.
name|put
argument_list|(
name|ps
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|ps
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|canCopy ( ProjectState project, PatchSetApproval psa, PatchSet.Id psId, ChangeKind kind)
specifier|private
specifier|static
name|boolean
name|canCopy
parameter_list|(
name|ProjectState
name|project
parameter_list|,
name|PatchSetApproval
name|psa
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|ChangeKind
name|kind
parameter_list|)
block|{
name|int
name|n
init|=
name|psa
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|checkArgument
argument_list|(
name|n
operator|!=
name|psId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|LabelType
name|type
init|=
name|project
operator|.
name|getLabelTypes
argument_list|()
operator|.
name|byLabel
argument_list|(
name|psa
operator|.
name|getLabelId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|type
operator|.
name|isCopyMinScore
argument_list|()
operator|&&
name|type
operator|.
name|isMaxNegative
argument_list|(
name|psa
argument_list|)
operator|)
operator|||
operator|(
name|type
operator|.
name|isCopyMaxScore
argument_list|()
operator|&&
name|type
operator|.
name|isMaxPositive
argument_list|(
name|psa
argument_list|)
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
switch|switch
condition|(
name|kind
condition|)
block|{
case|case
name|MERGE_FIRST_PARENT_UPDATE
case|:
return|return
name|type
operator|.
name|isCopyAllScoresOnMergeFirstParentUpdate
argument_list|()
return|;
case|case
name|NO_CODE_CHANGE
case|:
return|return
name|type
operator|.
name|isCopyAllScoresIfNoCodeChange
argument_list|()
return|;
case|case
name|TRIVIAL_REBASE
case|:
return|return
name|type
operator|.
name|isCopyAllScoresOnTrivialRebase
argument_list|()
return|;
case|case
name|NO_CHANGE
case|:
return|return
name|type
operator|.
name|isCopyAllScoresIfNoChange
argument_list|()
operator|||
name|type
operator|.
name|isCopyAllScoresOnTrivialRebase
argument_list|()
operator|||
name|type
operator|.
name|isCopyAllScoresOnMergeFirstParentUpdate
argument_list|()
operator|||
name|type
operator|.
name|isCopyAllScoresIfNoCodeChange
argument_list|()
return|;
case|case
name|REWORK
case|:
default|default:
return|return
literal|false
return|;
block|}
block|}
DECL|method|copy (PatchSetApproval src, PatchSet.Id psId)
specifier|private
specifier|static
name|PatchSetApproval
name|copy
parameter_list|(
name|PatchSetApproval
name|src
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
block|{
if|if
condition|(
name|src
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
operator|.
name|equals
argument_list|(
name|psId
argument_list|)
condition|)
block|{
return|return
name|src
return|;
block|}
return|return
operator|new
name|PatchSetApproval
argument_list|(
name|psId
argument_list|,
name|src
argument_list|)
return|;
block|}
block|}
end_class

end_unit

