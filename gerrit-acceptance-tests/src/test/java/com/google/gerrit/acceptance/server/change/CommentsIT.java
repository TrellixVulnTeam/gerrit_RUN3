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
DECL|package|com.google.gerrit.acceptance.server.change
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
name|change
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|Iterables
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
name|Comment
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
name|CommentInfo
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
name|RestApiException
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
name|testutil
operator|.
name|ConfigSuite
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|reflect
operator|.
name|TypeToken
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpStatus
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
name|api
operator|.
name|errors
operator|.
name|GitAPIException
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
name|junit
operator|.
name|Test
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
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_class
DECL|class|CommentsIT
specifier|public
class|class
name|CommentsIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|ConfigSuite
operator|.
name|Config
DECL|method|noteDbEnabled ()
specifier|public
specifier|static
name|Config
name|noteDbEnabled
parameter_list|()
block|{
name|Config
name|cfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|setBoolean
argument_list|(
literal|"notedb"
argument_list|,
literal|null
argument_list|,
literal|"write"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setBoolean
argument_list|(
literal|"notedb"
argument_list|,
literal|"comments"
argument_list|,
literal|"read"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|cfg
return|;
block|}
annotation|@
name|Test
DECL|method|createDraft ()
specifier|public
name|void
name|createDraft
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|IOException
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|String
name|changeId
init|=
name|r
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|String
name|revId
init|=
name|r
operator|.
name|getCommit
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|ReviewInput
operator|.
name|CommentInput
name|comment
init|=
name|newCommentInfo
argument_list|(
literal|"file1"
argument_list|,
name|Comment
operator|.
name|Side
operator|.
name|REVISION
argument_list|,
literal|1
argument_list|,
literal|"comment 1"
argument_list|)
decl_stmt|;
name|addDraft
argument_list|(
name|changeId
argument_list|,
name|revId
argument_list|,
name|comment
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|result
init|=
name|getDraftComments
argument_list|(
name|changeId
argument_list|,
name|revId
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|CommentInfo
name|actual
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|comment
operator|.
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertCommentInfo
argument_list|(
name|comment
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|postComment ()
specifier|public
name|void
name|postComment
parameter_list|()
throws|throws
name|RestApiException
throws|,
name|Exception
block|{
name|String
name|file
init|=
literal|"file"
decl_stmt|;
name|String
name|contents
init|=
literal|"contents"
decl_stmt|;
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
literal|"first subject"
argument_list|,
name|file
argument_list|,
name|contents
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
name|git
argument_list|,
literal|"refs/for/master"
argument_list|)
decl_stmt|;
name|String
name|changeId
init|=
name|r
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|String
name|revId
init|=
name|r
operator|.
name|getCommit
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|ReviewInput
name|input
init|=
operator|new
name|ReviewInput
argument_list|()
decl_stmt|;
name|ReviewInput
operator|.
name|CommentInput
name|comment
init|=
name|newCommentInfo
argument_list|(
name|file
argument_list|,
name|Comment
operator|.
name|Side
operator|.
name|REVISION
argument_list|,
literal|1
argument_list|,
literal|"comment 1"
argument_list|)
decl_stmt|;
name|input
operator|.
name|comments
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ReviewInput
operator|.
name|CommentInput
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|input
operator|.
name|comments
operator|.
name|put
argument_list|(
name|comment
operator|.
name|path
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|comment
argument_list|)
argument_list|)
expr_stmt|;
name|revision
argument_list|(
name|r
argument_list|)
operator|.
name|review
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|result
init|=
name|getPublishedComments
argument_list|(
name|changeId
argument_list|,
name|revId
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
operator|!
name|result
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|CommentInfo
name|actual
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|comment
operator|.
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertCommentInfo
argument_list|(
name|comment
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|putDraft ()
specifier|public
name|void
name|putDraft
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|IOException
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|String
name|changeId
init|=
name|r
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|String
name|revId
init|=
name|r
operator|.
name|getCommit
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|ReviewInput
operator|.
name|CommentInput
name|comment
init|=
name|newCommentInfo
argument_list|(
literal|"file1"
argument_list|,
name|Comment
operator|.
name|Side
operator|.
name|REVISION
argument_list|,
literal|1
argument_list|,
literal|"comment 1"
argument_list|)
decl_stmt|;
name|addDraft
argument_list|(
name|changeId
argument_list|,
name|revId
argument_list|,
name|comment
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|result
init|=
name|getDraftComments
argument_list|(
name|changeId
argument_list|,
name|revId
argument_list|)
decl_stmt|;
name|CommentInfo
name|actual
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|comment
operator|.
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|assertCommentInfo
argument_list|(
name|comment
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|String
name|uuid
init|=
name|actual
operator|.
name|id
decl_stmt|;
name|comment
operator|.
name|message
operator|=
literal|"updated comment 1"
expr_stmt|;
name|updateDraft
argument_list|(
name|changeId
argument_list|,
name|revId
argument_list|,
name|comment
argument_list|,
name|uuid
argument_list|)
expr_stmt|;
name|result
operator|=
name|getDraftComments
argument_list|(
name|changeId
argument_list|,
name|revId
argument_list|)
expr_stmt|;
name|actual
operator|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|comment
operator|.
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|assertCommentInfo
argument_list|(
name|comment
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getDraft ()
specifier|public
name|void
name|getDraft
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|IOException
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|String
name|changeId
init|=
name|r
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|String
name|revId
init|=
name|r
operator|.
name|getCommit
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|ReviewInput
operator|.
name|CommentInput
name|comment
init|=
name|newCommentInfo
argument_list|(
literal|"file1"
argument_list|,
name|Comment
operator|.
name|Side
operator|.
name|REVISION
argument_list|,
literal|1
argument_list|,
literal|"comment 1"
argument_list|)
decl_stmt|;
name|CommentInfo
name|returned
init|=
name|addDraft
argument_list|(
name|changeId
argument_list|,
name|revId
argument_list|,
name|comment
argument_list|)
decl_stmt|;
name|CommentInfo
name|actual
init|=
name|getDraftComment
argument_list|(
name|changeId
argument_list|,
name|revId
argument_list|,
name|returned
operator|.
name|id
argument_list|)
decl_stmt|;
name|assertCommentInfo
argument_list|(
name|comment
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteDraft ()
specifier|public
name|void
name|deleteDraft
parameter_list|()
throws|throws
name|IOException
throws|,
name|GitAPIException
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|String
name|changeId
init|=
name|r
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|String
name|revId
init|=
name|r
operator|.
name|getCommit
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|ReviewInput
operator|.
name|CommentInput
name|comment
init|=
name|newCommentInfo
argument_list|(
literal|"file1"
argument_list|,
name|Comment
operator|.
name|Side
operator|.
name|REVISION
argument_list|,
literal|1
argument_list|,
literal|"comment 1"
argument_list|)
decl_stmt|;
name|CommentInfo
name|returned
init|=
name|addDraft
argument_list|(
name|changeId
argument_list|,
name|revId
argument_list|,
name|comment
argument_list|)
decl_stmt|;
name|deleteDraft
argument_list|(
name|changeId
argument_list|,
name|revId
argument_list|,
name|returned
operator|.
name|id
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|drafts
init|=
name|getDraftComments
argument_list|(
name|changeId
argument_list|,
name|revId
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|drafts
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|addDraft (String changeId, String revId, ReviewInput.CommentInput c)
specifier|private
name|CommentInfo
name|addDraft
parameter_list|(
name|String
name|changeId
parameter_list|,
name|String
name|revId
parameter_list|,
name|ReviewInput
operator|.
name|CommentInput
name|c
parameter_list|)
throws|throws
name|IOException
block|{
name|RestResponse
name|r
init|=
name|userSession
operator|.
name|put
argument_list|(
literal|"/changes/"
operator|+
name|changeId
operator|+
literal|"/revisions/"
operator|+
name|revId
operator|+
literal|"/drafts"
argument_list|,
name|c
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_CREATED
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|newGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|r
operator|.
name|getReader
argument_list|()
argument_list|,
name|CommentInfo
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|updateDraft (String changeId, String revId, ReviewInput.CommentInput c, String uuid)
specifier|private
name|void
name|updateDraft
parameter_list|(
name|String
name|changeId
parameter_list|,
name|String
name|revId
parameter_list|,
name|ReviewInput
operator|.
name|CommentInput
name|c
parameter_list|,
name|String
name|uuid
parameter_list|)
throws|throws
name|IOException
block|{
name|RestResponse
name|r
init|=
name|userSession
operator|.
name|put
argument_list|(
literal|"/changes/"
operator|+
name|changeId
operator|+
literal|"/revisions/"
operator|+
name|revId
operator|+
literal|"/drafts/"
operator|+
name|uuid
argument_list|,
name|c
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteDraft (String changeId, String revId, String uuid)
specifier|private
name|void
name|deleteDraft
parameter_list|(
name|String
name|changeId
parameter_list|,
name|String
name|revId
parameter_list|,
name|String
name|uuid
parameter_list|)
throws|throws
name|IOException
block|{
name|RestResponse
name|r
init|=
name|userSession
operator|.
name|delete
argument_list|(
literal|"/changes/"
operator|+
name|changeId
operator|+
literal|"/revisions/"
operator|+
name|revId
operator|+
literal|"/drafts/"
operator|+
name|uuid
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_NO_CONTENT
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getPublishedComments (String changeId, String revId)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|getPublishedComments
parameter_list|(
name|String
name|changeId
parameter_list|,
name|String
name|revId
parameter_list|)
throws|throws
name|IOException
block|{
name|RestResponse
name|r
init|=
name|userSession
operator|.
name|get
argument_list|(
literal|"/changes/"
operator|+
name|changeId
operator|+
literal|"/revisions/"
operator|+
name|revId
operator|+
literal|"/comments/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|Type
name|mapType
init|=
operator|new
name|TypeToken
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
decl_stmt|;
return|return
name|newGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|r
operator|.
name|getReader
argument_list|()
argument_list|,
name|mapType
argument_list|)
return|;
block|}
DECL|method|getDraftComments (String changeId, String revId)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
name|getDraftComments
parameter_list|(
name|String
name|changeId
parameter_list|,
name|String
name|revId
parameter_list|)
throws|throws
name|IOException
block|{
name|RestResponse
name|r
init|=
name|userSession
operator|.
name|get
argument_list|(
literal|"/changes/"
operator|+
name|changeId
operator|+
literal|"/revisions/"
operator|+
name|revId
operator|+
literal|"/drafts/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|Type
name|mapType
init|=
operator|new
name|TypeToken
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CommentInfo
argument_list|>
argument_list|>
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
decl_stmt|;
return|return
name|newGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|r
operator|.
name|getReader
argument_list|()
argument_list|,
name|mapType
argument_list|)
return|;
block|}
DECL|method|getDraftComment (String changeId, String revId, String uuid)
specifier|private
name|CommentInfo
name|getDraftComment
parameter_list|(
name|String
name|changeId
parameter_list|,
name|String
name|revId
parameter_list|,
name|String
name|uuid
parameter_list|)
throws|throws
name|IOException
block|{
name|RestResponse
name|r
init|=
name|userSession
operator|.
name|get
argument_list|(
literal|"/changes/"
operator|+
name|changeId
operator|+
literal|"/revisions/"
operator|+
name|revId
operator|+
literal|"/drafts/"
operator|+
name|uuid
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|newGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|r
operator|.
name|getReader
argument_list|()
argument_list|,
name|CommentInfo
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|assertCommentInfo (ReviewInput.CommentInput expected, CommentInfo actual)
specifier|private
specifier|static
name|void
name|assertCommentInfo
parameter_list|(
name|ReviewInput
operator|.
name|CommentInput
name|expected
parameter_list|,
name|CommentInfo
name|actual
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|line
argument_list|,
name|actual
operator|.
name|line
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|message
argument_list|,
name|actual
operator|.
name|message
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|inReplyTo
argument_list|,
name|actual
operator|.
name|inReplyTo
argument_list|)
expr_stmt|;
if|if
condition|(
name|actual
operator|.
name|side
operator|==
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|expected
operator|.
name|side
argument_list|,
name|Comment
operator|.
name|Side
operator|.
name|REVISION
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|newCommentInfo (String path, Comment.Side side, int line, String message)
specifier|private
name|ReviewInput
operator|.
name|CommentInput
name|newCommentInfo
parameter_list|(
name|String
name|path
parameter_list|,
name|Comment
operator|.
name|Side
name|side
parameter_list|,
name|int
name|line
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|ReviewInput
operator|.
name|CommentInput
name|input
init|=
operator|new
name|ReviewInput
operator|.
name|CommentInput
argument_list|()
decl_stmt|;
name|input
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|input
operator|.
name|side
operator|=
name|side
expr_stmt|;
name|input
operator|.
name|line
operator|=
name|line
expr_stmt|;
name|input
operator|.
name|message
operator|=
name|message
expr_stmt|;
return|return
name|input
return|;
block|}
block|}
end_class

end_unit

