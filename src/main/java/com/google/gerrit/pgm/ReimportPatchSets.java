begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.pgm
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
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
name|client
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
name|client
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
name|client
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
name|client
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
name|client
operator|.
name|rpc
operator|.
name|Common
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
name|git
operator|.
name|InvalidRepositoryException
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
name|git
operator|.
name|PatchSetImporter
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
name|git
operator|.
name|WorkQueue
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
name|GerritServer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|server
operator|.
name|XsrfException
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
name|org
operator|.
name|spearce
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
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ProgressMonitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
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
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|TextProgressMonitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
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
name|spearce
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|RevWalk
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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

begin_comment
comment|/**  * Recreates PatchSet and Patch entities for the changes supplied.  *<p>  * Takes on input strings of the form<code>change_id|patch_set_id</code> or  *<code>change_id,patch_set_id</code>, such as might be created by the  * following PostgreSQL database dump:  *   *<pre>  *  psql reviewdb -tAc 'select change_id,patch_set_id from patch_sets'  *</pre>  *<p>  * For each supplied PatchSet the info and patch entities are completely updated  * based on the data stored in Git.  */
end_comment

begin_class
DECL|class|ReimportPatchSets
specifier|public
class|class
name|ReimportPatchSets
block|{
DECL|method|main (final String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
specifier|final
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|OrmException
throws|,
name|XsrfException
throws|,
name|IOException
block|{
try|try
block|{
name|mainImpl
argument_list|(
name|argv
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|WorkQueue
operator|.
name|terminate
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|mainImpl (final String[] argv)
specifier|private
specifier|static
name|void
name|mainImpl
parameter_list|(
specifier|final
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|OrmException
throws|,
name|XsrfException
throws|,
name|IOException
block|{
specifier|final
name|GerritServer
name|gs
init|=
name|GerritServer
operator|.
name|getInstance
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|>
name|todo
init|=
operator|new
name|ArrayList
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|todo
operator|.
name|add
argument_list|(
name|PatchSet
operator|.
name|Id
operator|.
name|parse
argument_list|(
name|line
operator|.
name|replace
argument_list|(
literal|'|'
argument_list|,
literal|','
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ReviewDb
name|db
init|=
name|Common
operator|.
name|getSchemaFactory
argument_list|()
operator|.
name|open
argument_list|()
decl_stmt|;
specifier|final
name|ProgressMonitor
name|pm
init|=
operator|new
name|TextProgressMonitor
argument_list|()
decl_stmt|;
try|try
block|{
name|pm
operator|.
name|start
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|pm
operator|.
name|beginTask
argument_list|(
literal|"Import patch sets"
argument_list|,
name|todo
operator|.
name|size
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
name|todo
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|PatchSet
operator|.
name|Id
name|psid
init|=
name|todo
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|PatchSet
name|ps
init|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|psid
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"NotFound "
operator|+
name|psid
argument_list|)
expr_stmt|;
continue|continue;
block|}
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
name|ps
operator|.
name|getId
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Orphan "
operator|+
name|psid
argument_list|)
expr_stmt|;
continue|continue;
block|}
specifier|final
name|Project
operator|.
name|NameKey
name|projectKey
init|=
name|c
operator|.
name|getDest
argument_list|()
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
specifier|final
name|String
name|projectName
init|=
name|projectKey
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|Repository
name|repo
decl_stmt|;
try|try
block|{
name|repo
operator|=
name|gs
operator|.
name|getRepositoryCache
argument_list|()
operator|.
name|get
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidRepositoryException
name|ie
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"NoProject "
operator|+
name|psid
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"NoProject "
operator|+
name|ie
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
specifier|final
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
decl_stmt|;
specifier|final
name|RevCommit
name|src
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
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
operator|new
name|PatchSetImporter
argument_list|(
name|gs
argument_list|,
name|db
argument_list|,
name|projectKey
argument_list|,
name|repo
argument_list|,
name|src
argument_list|,
name|ps
argument_list|,
literal|false
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
name|pm
operator|.
name|update
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|()
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|SQLException
condition|)
block|{
specifier|final
name|SQLException
name|e2
init|=
operator|(
name|SQLException
operator|)
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|e2
operator|.
name|getNextException
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|e2
operator|.
name|getNextException
argument_list|()
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|pm
operator|.
name|endTask
argument_list|()
expr_stmt|;
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

