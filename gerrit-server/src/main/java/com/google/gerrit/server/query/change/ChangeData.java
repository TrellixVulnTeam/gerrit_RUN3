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

begin_package
DECL|package|com.google.gerrit.server.query.change
package|package
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
name|reviewdb
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
name|client
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
name|Provider
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
DECL|class|ChangeData
specifier|public
class|class
name|ChangeData
block|{
DECL|field|legacyId
specifier|private
specifier|final
name|Change
operator|.
name|Id
name|legacyId
decl_stmt|;
DECL|field|change
specifier|private
name|Change
name|change
decl_stmt|;
DECL|field|patches
specifier|private
name|Collection
argument_list|<
name|PatchSet
argument_list|>
name|patches
decl_stmt|;
DECL|field|approvals
specifier|private
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
decl_stmt|;
DECL|field|currentApprovals
specifier|private
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|currentApprovals
decl_stmt|;
DECL|field|currentFiles
specifier|private
name|Collection
argument_list|<
name|String
argument_list|>
name|currentFiles
decl_stmt|;
DECL|field|comments
specifier|private
name|Collection
argument_list|<
name|PatchLineComment
argument_list|>
name|comments
decl_stmt|;
DECL|field|trackingIds
specifier|private
name|Collection
argument_list|<
name|TrackingId
argument_list|>
name|trackingIds
decl_stmt|;
DECL|field|visibleTo
specifier|private
name|CurrentUser
name|visibleTo
decl_stmt|;
DECL|method|ChangeData (final Change.Id id)
specifier|public
name|ChangeData
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|id
parameter_list|)
block|{
name|legacyId
operator|=
name|id
expr_stmt|;
block|}
DECL|method|ChangeData (final Change c)
specifier|public
name|ChangeData
parameter_list|(
specifier|final
name|Change
name|c
parameter_list|)
block|{
name|legacyId
operator|=
name|c
operator|.
name|getId
argument_list|()
expr_stmt|;
name|change
operator|=
name|c
expr_stmt|;
block|}
DECL|method|setCurrentFilePaths (Collection<String> filePaths)
specifier|public
name|void
name|setCurrentFilePaths
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|filePaths
parameter_list|)
block|{
name|currentFiles
operator|=
name|filePaths
expr_stmt|;
block|}
DECL|method|currentFilePaths (Provider<ReviewDb> db, PatchListCache cache)
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|currentFilePaths
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|PatchListCache
name|cache
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|currentFiles
operator|==
literal|null
condition|)
block|{
name|Change
name|c
init|=
name|change
argument_list|(
name|db
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|PatchSet
name|ps
init|=
name|currentPatchSet
argument_list|(
name|db
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
literal|null
return|;
block|}
name|PatchList
name|p
init|=
name|cache
operator|.
name|get
argument_list|(
name|c
argument_list|,
name|ps
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|p
operator|.
name|getPatches
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|PatchListEntry
name|e
range|:
name|p
operator|.
name|getPatches
argument_list|()
control|)
block|{
switch|switch
condition|(
name|e
operator|.
name|getChangeType
argument_list|()
condition|)
block|{
case|case
name|ADDED
case|:
case|case
name|MODIFIED
case|:
case|case
name|DELETED
case|:
case|case
name|COPIED
case|:
name|r
operator|.
name|add
argument_list|(
name|e
operator|.
name|getNewName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|RENAMED
case|:
name|r
operator|.
name|add
argument_list|(
name|e
operator|.
name|getOldName
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|add
argument_list|(
name|e
operator|.
name|getNewName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|currentFiles
operator|=
name|r
expr_stmt|;
block|}
return|return
name|currentFiles
return|;
block|}
DECL|method|getId ()
specifier|public
name|Change
operator|.
name|Id
name|getId
parameter_list|()
block|{
return|return
name|legacyId
return|;
block|}
DECL|method|getChange ()
specifier|public
name|Change
name|getChange
parameter_list|()
block|{
return|return
name|change
return|;
block|}
DECL|method|hasChange ()
specifier|public
name|boolean
name|hasChange
parameter_list|()
block|{
return|return
name|change
operator|!=
literal|null
return|;
block|}
DECL|method|fastIsVisibleTo (CurrentUser user)
name|boolean
name|fastIsVisibleTo
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
block|{
return|return
name|visibleTo
operator|==
name|user
return|;
block|}
DECL|method|cacheVisibleTo (CurrentUser user)
name|void
name|cacheVisibleTo
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
block|{
name|visibleTo
operator|=
name|user
expr_stmt|;
block|}
DECL|method|change (Provider<ReviewDb> db)
specifier|public
name|Change
name|change
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|change
operator|==
literal|null
condition|)
block|{
name|change
operator|=
name|db
operator|.
name|get
argument_list|()
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|legacyId
argument_list|)
expr_stmt|;
block|}
return|return
name|change
return|;
block|}
DECL|method|currentPatchSet (Provider<ReviewDb> db)
specifier|public
name|PatchSet
name|currentPatchSet
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
name|Change
name|c
init|=
name|change
argument_list|(
name|db
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
for|for
control|(
name|PatchSet
name|p
range|:
name|patches
argument_list|(
name|db
argument_list|)
control|)
block|{
if|if
condition|(
name|p
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|p
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|currentApprovals (Provider<ReviewDb> db)
specifier|public
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|currentApprovals
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|currentApprovals
operator|==
literal|null
condition|)
block|{
name|Change
name|c
init|=
name|change
argument_list|(
name|db
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
name|currentApprovals
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|currentApprovals
operator|=
name|approvalsFor
argument_list|(
name|db
argument_list|,
name|c
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|currentApprovals
return|;
block|}
DECL|method|approvalsFor (Provider<ReviewDb> db, PatchSet.Id psId)
specifier|public
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvalsFor
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
throws|throws
name|OrmException
block|{
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<
name|PatchSetApproval
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|p
range|:
name|approvals
argument_list|(
name|db
argument_list|)
control|)
block|{
if|if
condition|(
name|p
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|equals
argument_list|(
name|psId
argument_list|)
condition|)
block|{
name|r
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|r
return|;
block|}
DECL|method|patches (Provider<ReviewDb> db)
specifier|public
name|Collection
argument_list|<
name|PatchSet
argument_list|>
name|patches
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|patches
operator|==
literal|null
condition|)
block|{
name|patches
operator|=
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|byChange
argument_list|(
name|legacyId
argument_list|)
operator|.
name|toList
argument_list|()
expr_stmt|;
block|}
return|return
name|patches
return|;
block|}
DECL|method|approvals (Provider<ReviewDb> db)
specifier|public
name|Collection
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|approvals
operator|==
literal|null
condition|)
block|{
name|approvals
operator|=
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byChange
argument_list|(
name|legacyId
argument_list|)
operator|.
name|toList
argument_list|()
expr_stmt|;
block|}
return|return
name|approvals
return|;
block|}
DECL|method|comments (Provider<ReviewDb> db)
specifier|public
name|Collection
argument_list|<
name|PatchLineComment
argument_list|>
name|comments
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|comments
operator|==
literal|null
condition|)
block|{
name|comments
operator|=
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchComments
argument_list|()
operator|.
name|byChange
argument_list|(
name|legacyId
argument_list|)
operator|.
name|toList
argument_list|()
expr_stmt|;
block|}
return|return
name|comments
return|;
block|}
DECL|method|trackingIds (Provider<ReviewDb> db)
specifier|public
name|Collection
argument_list|<
name|TrackingId
argument_list|>
name|trackingIds
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|trackingIds
operator|==
literal|null
condition|)
block|{
name|trackingIds
operator|=
name|db
operator|.
name|get
argument_list|()
operator|.
name|trackingIds
argument_list|()
operator|.
name|byChange
argument_list|(
name|legacyId
argument_list|)
operator|.
name|toList
argument_list|()
expr_stmt|;
block|}
return|return
name|trackingIds
return|;
block|}
block|}
end_class

end_unit

