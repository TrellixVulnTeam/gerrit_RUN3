begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
name|PureRevertInfo
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
name|BadRequestException
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
name|ResourceConflictException
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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|List
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
name|diff
operator|.
name|DiffEntry
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
name|diff
operator|.
name|DiffFormatter
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
name|InvalidObjectIdException
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
name|MissingObjectException
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
DECL|class|PureRevert
specifier|public
class|class
name|PureRevert
block|{
DECL|field|mergeUtilFactory
specifier|private
specifier|final
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|notesFactory
specifier|private
specifier|final
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
decl_stmt|;
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
decl_stmt|;
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
annotation|@
name|Inject
DECL|method|PureRevert ( MergeUtil.Factory mergeUtilFactory, GitRepositoryManager repoManager, ProjectCache projectCache, ChangeNotes.Factory notesFactory, Provider<ReviewDb> dbProvider, PatchSetUtil psUtil)
name|PureRevert
parameter_list|(
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|)
block|{
name|this
operator|.
name|mergeUtilFactory
operator|=
name|mergeUtilFactory
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|notesFactory
operator|=
name|notesFactory
expr_stmt|;
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
block|}
DECL|method|get (ChangeNotes notes, @Nullable String claimedOriginal)
specifier|public
name|PureRevertInfo
name|get
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
annotation|@
name|Nullable
name|String
name|claimedOriginal
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|BadRequestException
throws|,
name|ResourceConflictException
block|{
name|PatchSet
name|currentPatchSet
init|=
name|psUtil
operator|.
name|current
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|,
name|notes
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentPatchSet
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"current revision is missing"
argument_list|)
throw|;
block|}
if|if
condition|(
name|claimedOriginal
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|notes
operator|.
name|getChange
argument_list|()
operator|.
name|getRevertOf
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"no ID was provided and change isn't a revert"
argument_list|)
throw|;
block|}
name|PatchSet
name|ps
init|=
name|psUtil
operator|.
name|current
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|,
name|notesFactory
operator|.
name|createChecked
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|,
name|notes
operator|.
name|getProjectName
argument_list|()
argument_list|,
name|notes
operator|.
name|getChange
argument_list|()
operator|.
name|getRevertOf
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|claimedOriginal
operator|=
name|ps
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|notes
operator|.
name|getProjectName
argument_list|()
argument_list|)
init|;
name|ObjectInserter
name|oi
operator|=
name|repo
operator|.
name|newObjectInserter
argument_list|()
init|;
name|RevWalk
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
init|)
block|{
name|RevCommit
name|claimedOriginalCommit
decl_stmt|;
try|try
block|{
name|claimedOriginalCommit
operator|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
name|claimedOriginal
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidObjectIdException
decl||
name|MissingObjectException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"invalid object ID"
argument_list|)
throw|;
block|}
if|if
condition|(
name|claimedOriginalCommit
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"can't check against initial commit"
argument_list|)
throw|;
block|}
name|RevCommit
name|claimedRevertCommit
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
name|currentPatchSet
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|claimedRevertCommit
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"claimed revert has no parents"
argument_list|)
throw|;
block|}
comment|// Rebase claimed revert onto claimed original
name|ThreeWayMerger
name|merger
init|=
name|mergeUtilFactory
operator|.
name|create
argument_list|(
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|notes
operator|.
name|getProjectName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|newThreeWayMerger
argument_list|(
name|oi
argument_list|,
name|repo
operator|.
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|merger
operator|.
name|setBase
argument_list|(
name|claimedRevertCommit
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
name|merger
operator|.
name|merge
argument_list|(
name|claimedRevertCommit
argument_list|,
name|claimedOriginalCommit
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
operator|||
name|merger
operator|.
name|getResultTreeId
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// Merge conflict during rebase
return|return
operator|new
name|PureRevertInfo
argument_list|(
literal|false
argument_list|)
return|;
block|}
comment|// Any differences between claimed original's parent and the rebase result indicate that the
comment|// claimedRevert is not a pure revert but made content changes
try|try
init|(
name|DiffFormatter
name|df
init|=
operator|new
name|DiffFormatter
argument_list|(
operator|new
name|ByteArrayOutputStream
argument_list|()
argument_list|)
init|)
block|{
name|df
operator|.
name|setReader
argument_list|(
name|oi
operator|.
name|newReader
argument_list|()
argument_list|,
name|repo
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DiffEntry
argument_list|>
name|entries
init|=
name|df
operator|.
name|scan
argument_list|(
name|claimedOriginalCommit
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|,
name|merger
operator|.
name|getResultTreeId
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|PureRevertInfo
argument_list|(
name|entries
operator|.
name|isEmpty
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

