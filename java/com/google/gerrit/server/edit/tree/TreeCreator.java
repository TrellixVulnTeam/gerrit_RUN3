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
DECL|package|com.google.gerrit.server.edit.tree
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|edit
operator|.
name|tree
package|;
end_package

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
name|dircache
operator|.
name|DirCache
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
name|dircache
operator|.
name|DirCacheBuilder
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
name|dircache
operator|.
name|DirCacheEditor
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
name|dircache
operator|.
name|DirCacheEntry
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
name|ObjectReader
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
name|revwalk
operator|.
name|RevCommit
import|;
end_import

begin_comment
comment|/**  * A creator for a new Git tree. To create the new tree, the tree of another commit is taken as a  * basis and modified.  */
end_comment

begin_class
DECL|class|TreeCreator
specifier|public
class|class
name|TreeCreator
block|{
DECL|field|baseCommit
specifier|private
specifier|final
name|RevCommit
name|baseCommit
decl_stmt|;
DECL|field|treeModifications
specifier|private
specifier|final
name|List
argument_list|<
name|TreeModification
argument_list|>
name|treeModifications
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|TreeCreator (RevCommit baseCommit)
specifier|public
name|TreeCreator
parameter_list|(
name|RevCommit
name|baseCommit
parameter_list|)
block|{
name|this
operator|.
name|baseCommit
operator|=
name|requireNonNull
argument_list|(
name|baseCommit
argument_list|,
literal|"baseCommit is required"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Apply modifications to the tree which is taken as a basis. If this method is called multiple    * times, the modifications are applied subsequently in exactly the order they were provided.    *    * @param treeModifications modifications which should be applied to the base tree    */
DECL|method|addTreeModifications (List<TreeModification> treeModifications)
specifier|public
name|void
name|addTreeModifications
parameter_list|(
name|List
argument_list|<
name|TreeModification
argument_list|>
name|treeModifications
parameter_list|)
block|{
name|requireNonNull
argument_list|(
name|treeModifications
argument_list|,
literal|"treeModifications must not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|treeModifications
operator|.
name|addAll
argument_list|(
name|treeModifications
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates the new tree. When this method is called, the specified base tree is read from the    * repository, the specified modifications are applied, and the resulting tree is written to the    * object store of the repository.    *    * @param repository the affected Git repository    * @return the {@code ObjectId} of the created tree    * @throws IOException if problems arise when accessing the repository    */
DECL|method|createNewTreeAndGetId (Repository repository)
specifier|public
name|ObjectId
name|createNewTreeAndGetId
parameter_list|(
name|Repository
name|repository
parameter_list|)
throws|throws
name|IOException
block|{
name|DirCache
name|newTree
init|=
name|createNewTree
argument_list|(
name|repository
argument_list|)
decl_stmt|;
return|return
name|writeAndGetId
argument_list|(
name|repository
argument_list|,
name|newTree
argument_list|)
return|;
block|}
DECL|method|createNewTree (Repository repository)
specifier|private
name|DirCache
name|createNewTree
parameter_list|(
name|Repository
name|repository
parameter_list|)
throws|throws
name|IOException
block|{
name|DirCache
name|newTree
init|=
name|readBaseTree
argument_list|(
name|repository
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DirCacheEditor
operator|.
name|PathEdit
argument_list|>
name|pathEdits
init|=
name|getPathEdits
argument_list|(
name|repository
argument_list|)
decl_stmt|;
name|applyPathEdits
argument_list|(
name|newTree
argument_list|,
name|pathEdits
argument_list|)
expr_stmt|;
return|return
name|newTree
return|;
block|}
DECL|method|readBaseTree (Repository repository)
specifier|private
name|DirCache
name|readBaseTree
parameter_list|(
name|Repository
name|repository
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|ObjectReader
name|objectReader
init|=
name|repository
operator|.
name|newObjectReader
argument_list|()
init|)
block|{
name|DirCache
name|dirCache
init|=
name|DirCache
operator|.
name|newInCore
argument_list|()
decl_stmt|;
name|DirCacheBuilder
name|dirCacheBuilder
init|=
name|dirCache
operator|.
name|builder
argument_list|()
decl_stmt|;
name|dirCacheBuilder
operator|.
name|addTree
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|DirCacheEntry
operator|.
name|STAGE_0
argument_list|,
name|objectReader
argument_list|,
name|baseCommit
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|dirCacheBuilder
operator|.
name|finish
argument_list|()
expr_stmt|;
return|return
name|dirCache
return|;
block|}
block|}
DECL|method|getPathEdits (Repository repository)
specifier|private
name|List
argument_list|<
name|DirCacheEditor
operator|.
name|PathEdit
argument_list|>
name|getPathEdits
parameter_list|(
name|Repository
name|repository
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|DirCacheEditor
operator|.
name|PathEdit
argument_list|>
name|pathEdits
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|TreeModification
name|treeModification
range|:
name|treeModifications
control|)
block|{
name|pathEdits
operator|.
name|addAll
argument_list|(
name|treeModification
operator|.
name|getPathEdits
argument_list|(
name|repository
argument_list|,
name|baseCommit
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|pathEdits
return|;
block|}
DECL|method|applyPathEdits (DirCache tree, List<DirCacheEditor.PathEdit> pathEdits)
specifier|private
specifier|static
name|void
name|applyPathEdits
parameter_list|(
name|DirCache
name|tree
parameter_list|,
name|List
argument_list|<
name|DirCacheEditor
operator|.
name|PathEdit
argument_list|>
name|pathEdits
parameter_list|)
block|{
name|DirCacheEditor
name|dirCacheEditor
init|=
name|tree
operator|.
name|editor
argument_list|()
decl_stmt|;
name|pathEdits
operator|.
name|forEach
argument_list|(
name|dirCacheEditor
operator|::
name|add
argument_list|)
expr_stmt|;
name|dirCacheEditor
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
DECL|method|writeAndGetId (Repository repository, DirCache tree)
specifier|private
specifier|static
name|ObjectId
name|writeAndGetId
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|DirCache
name|tree
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|ObjectInserter
name|objectInserter
init|=
name|repository
operator|.
name|newObjectInserter
argument_list|()
init|)
block|{
name|ObjectId
name|treeId
init|=
name|tree
operator|.
name|writeTree
argument_list|(
name|objectInserter
argument_list|)
decl_stmt|;
name|objectInserter
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|treeId
return|;
block|}
block|}
block|}
end_class

end_unit

