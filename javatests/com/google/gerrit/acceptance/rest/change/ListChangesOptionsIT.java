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
DECL|package|com.google.gerrit.acceptance.rest.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
operator|.
name|change
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
name|gerrit
operator|.
name|extensions
operator|.
name|client
operator|.
name|ListChangesOption
operator|.
name|ALL_REVISIONS
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
name|extensions
operator|.
name|client
operator|.
name|ListChangesOption
operator|.
name|CURRENT_REVISION
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
name|extensions
operator|.
name|client
operator|.
name|ListChangesOption
operator|.
name|MESSAGES
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
name|ImmutableSet
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
name|NoHttpd
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
name|extensions
operator|.
name|common
operator|.
name|ChangeInfo
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
name|junit
operator|.
name|Before
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
name|NoHttpd
DECL|class|ListChangesOptionsIT
specifier|public
class|class
name|ListChangesOptionsIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|changeId
specifier|private
name|String
name|changeId
decl_stmt|;
DECL|field|results
specifier|private
name|List
argument_list|<
name|PushOneCommit
operator|.
name|Result
argument_list|>
name|results
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|results
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|push
argument_list|(
literal|"file contents"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|changeId
operator|=
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getChangeId
argument_list|()
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|push
argument_list|(
literal|"new contents 1"
argument_list|,
name|changeId
argument_list|)
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|push
argument_list|(
literal|"new contents 2"
argument_list|,
name|changeId
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|push (String content, String baseChangeId)
specifier|private
name|PushOneCommit
operator|.
name|Result
name|push
parameter_list|(
name|String
name|content
parameter_list|,
name|String
name|baseChangeId
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|subject
init|=
literal|"Change subject"
decl_stmt|;
name|String
name|fileName
init|=
literal|"a.txt"
decl_stmt|;
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|,
name|subject
argument_list|,
name|fileName
argument_list|,
name|content
argument_list|,
name|baseChangeId
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|push
operator|.
name|to
argument_list|(
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
return|return
name|r
return|;
block|}
annotation|@
name|Test
DECL|method|noRevisionOptions ()
specifier|public
name|void
name|noRevisionOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeInfo
name|c
init|=
name|info
argument_list|(
name|changeId
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|currentRevision
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|revisions
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|currentRevision ()
specifier|public
name|void
name|currentRevision
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeInfo
name|c
init|=
name|get
argument_list|(
name|changeId
argument_list|,
name|CURRENT_REVISION
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|currentRevision
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|commitId
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|revisions
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|containsAllIn
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|commitId
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|revisions
operator|.
name|get
argument_list|(
name|commitId
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|_number
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|currentRevisionAndMessages ()
specifier|public
name|void
name|currentRevisionAndMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeInfo
name|c
init|=
name|get
argument_list|(
name|changeId
argument_list|,
name|CURRENT_REVISION
argument_list|,
name|MESSAGES
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|revisions
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|currentRevision
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|commitId
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|revisions
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|containsAllIn
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|commitId
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|revisions
operator|.
name|get
argument_list|(
name|commitId
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|_number
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|allRevisions ()
specifier|public
name|void
name|allRevisions
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeInfo
name|c
init|=
name|get
argument_list|(
name|changeId
argument_list|,
name|ALL_REVISIONS
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|currentRevision
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|commitId
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|revisions
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|containsAllIn
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|commitId
argument_list|(
literal|0
argument_list|)
argument_list|,
name|commitId
argument_list|(
literal|1
argument_list|)
argument_list|,
name|commitId
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|revisions
operator|.
name|get
argument_list|(
name|commitId
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|_number
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|revisions
operator|.
name|get
argument_list|(
name|commitId
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|_number
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|revisions
operator|.
name|get
argument_list|(
name|commitId
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|_number
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|commitId (int i)
specifier|private
name|String
name|commitId
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|results
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
return|;
block|}
block|}
end_class

end_unit

