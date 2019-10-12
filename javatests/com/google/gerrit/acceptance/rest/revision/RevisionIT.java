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
DECL|package|com.google.gerrit.acceptance.rest.revision
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
name|revision
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
name|acceptance
operator|.
name|PushOneCommit
operator|.
name|FILE_CONTENT
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
name|acceptance
operator|.
name|PushOneCommit
operator|.
name|FILE_NAME
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|io
operator|.
name|BaseEncoding
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
name|RestResponse
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
name|ReviewInput
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|RevisionIT
specifier|public
class|class
name|RevisionIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
DECL|method|contentOfParent ()
specifier|public
name|void
name|contentOfParent
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|parentContent
init|=
literal|"parent content"
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|parent
init|=
name|createChange
argument_list|(
literal|"Parent change"
argument_list|,
name|FILE_NAME
argument_list|,
name|parentContent
argument_list|)
decl_stmt|;
name|parent
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|parent
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|parent
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|submit
argument_list|()
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|child
init|=
name|createChange
argument_list|(
literal|"Child change"
argument_list|,
name|FILE_NAME
argument_list|,
name|FILE_CONTENT
argument_list|)
decl_stmt|;
name|child
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|assertContent
argument_list|(
name|child
argument_list|,
name|FILE_NAME
argument_list|,
name|FILE_CONTENT
argument_list|)
expr_stmt|;
name|RestResponse
name|response
init|=
name|adminRestSession
operator|.
name|get
argument_list|(
literal|"/changes/"
operator|+
name|child
operator|.
name|getChangeId
argument_list|()
operator|+
literal|"/revisions/current/files/"
operator|+
name|FILE_NAME
operator|+
literal|"/content?parent=1"
argument_list|)
decl_stmt|;
name|response
operator|.
name|assertOK
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|String
argument_list|(
name|BaseEncoding
operator|.
name|base64
argument_list|()
operator|.
name|decode
argument_list|(
name|response
operator|.
name|getEntityContent
argument_list|()
argument_list|)
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|parentContent
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|contentOfInvalidParent ()
specifier|public
name|void
name|contentOfInvalidParent
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|parentContent
init|=
literal|"parent content"
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|parent
init|=
name|createChange
argument_list|(
literal|"Parent change"
argument_list|,
name|FILE_NAME
argument_list|,
name|parentContent
argument_list|)
decl_stmt|;
name|parent
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|parent
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|parent
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|submit
argument_list|()
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|child
init|=
name|createChange
argument_list|(
literal|"Child change"
argument_list|,
name|FILE_NAME
argument_list|,
name|FILE_CONTENT
argument_list|)
decl_stmt|;
name|child
operator|.
name|assertOkStatus
argument_list|()
expr_stmt|;
name|assertContent
argument_list|(
name|child
argument_list|,
name|FILE_NAME
argument_list|,
name|FILE_CONTENT
argument_list|)
expr_stmt|;
name|RestResponse
name|response
init|=
name|adminRestSession
operator|.
name|get
argument_list|(
literal|"/changes/"
operator|+
name|child
operator|.
name|getChangeId
argument_list|()
operator|+
literal|"/revisions/current/files/"
operator|+
name|FILE_NAME
operator|+
literal|"/content?parent=10"
argument_list|)
decl_stmt|;
name|response
operator|.
name|assertBadRequest
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getEntityContent
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"invalid parent"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getReview ()
specifier|public
name|void
name|getReview
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|ObjectId
name|ps1Commit
init|=
name|r
operator|.
name|getCommit
argument_list|()
decl_stmt|;
name|r
operator|=
name|amendChange
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|ObjectId
name|ps2Commit
init|=
name|r
operator|.
name|getCommit
argument_list|()
decl_stmt|;
name|ChangeInfo
name|info1
init|=
name|checkRevisionReview
argument_list|(
name|r
argument_list|,
literal|1
argument_list|,
name|ps1Commit
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|info1
operator|.
name|currentRevision
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|ChangeInfo
name|info2
init|=
name|checkRevisionReview
argument_list|(
name|r
argument_list|,
literal|2
argument_list|,
name|ps2Commit
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|info2
operator|.
name|currentRevision
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ps2Commit
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkRevisionReview ( PushOneCommit.Result r, int psNum, ObjectId expectedRevision)
specifier|private
name|ChangeInfo
name|checkRevisionReview
parameter_list|(
name|PushOneCommit
operator|.
name|Result
name|r
parameter_list|,
name|int
name|psNum
parameter_list|,
name|ObjectId
name|expectedRevision
parameter_list|)
throws|throws
name|Exception
block|{
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|r
operator|.
name|getChangeId
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
name|RestResponse
name|response
init|=
name|adminRestSession
operator|.
name|get
argument_list|(
literal|"/changes/"
operator|+
name|r
operator|.
name|getChangeId
argument_list|()
operator|+
literal|"/revisions/"
operator|+
name|psNum
operator|+
literal|"/review"
argument_list|)
decl_stmt|;
name|response
operator|.
name|assertOK
argument_list|()
expr_stmt|;
name|ChangeInfo
name|info
init|=
name|newGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|response
operator|.
name|getReader
argument_list|()
argument_list|,
name|ChangeInfo
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Check for DETAILED_ACCOUNTS, DETAILED_LABELS, and specified revision.
name|assertThat
argument_list|(
name|info
operator|.
name|owner
operator|.
name|name
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|labels
operator|.
name|get
argument_list|(
literal|"Code-Review"
argument_list|)
operator|.
name|all
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|revisions
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|expectedRevision
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
block|}
end_class

end_unit

