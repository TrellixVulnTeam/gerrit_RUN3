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
DECL|package|com.google.gerrit.acceptance.server.notedb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|server
operator|.
name|notedb
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
name|truth
operator|.
name|Truth
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|TruthJUnit
operator|.
name|assume
import|;
end_import

begin_import
import|import static
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
name|RefNames
operator|.
name|changeMetaRef
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
name|acceptance
operator|.
name|AbstractDaemonTest
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
name|acceptance
operator|.
name|PushOneCommit
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
name|acceptance
operator|.
name|UseLocalDisk
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
name|java
operator|.
name|io
operator|.
name|File
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
name|ReflogEntry
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
annotation|@
name|UseLocalDisk
DECL|class|ReflogIT
specifier|public
class|class
name|ReflogIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
DECL|method|guessRestApiInReflog ()
specifier|public
name|void
name|guessRestApiInReflog
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|notesMigration
operator|.
name|disableChangeReviewDb
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|Change
operator|.
name|Id
name|id
init|=
name|r
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|r
operator|.
name|getChange
argument_list|()
operator|.
name|project
argument_list|()
argument_list|)
init|)
block|{
name|File
name|log
init|=
operator|new
name|File
argument_list|(
name|repo
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|"logs/"
operator|+
name|changeMetaRef
argument_list|(
name|id
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|log
operator|.
name|exists
argument_list|()
condition|)
block|{
name|log
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|log
operator|.
name|createNewFile
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|topic
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|ReflogEntry
name|last
init|=
name|repo
operator|.
name|getReflogReader
argument_list|(
name|changeMetaRef
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|getLastEntry
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|last
argument_list|)
operator|.
name|named
argument_list|(
literal|"last RefLogEntry"
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|last
operator|.
name|getComment
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"change.PutTopic"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

