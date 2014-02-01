begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
name|gerrit
operator|.
name|acceptance
operator|.
name|GitUtil
operator|.
name|cloneProject
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
name|GitUtil
operator|.
name|createProject
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
name|assertNotNull
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
name|assertNull
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
name|AcceptanceTestRequestScope
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
name|SshSession
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
name|GerritApi
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
name|ChangeMessageInfo
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
name|reviewdb
operator|.
name|client
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
name|IdentifiedUser
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
name|util
operator|.
name|Providers
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
name|Git
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
name|junit
operator|.
name|After
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
name|Iterator
import|;
end_import

begin_class
DECL|class|ChangeMessagesIT
specifier|public
class|class
name|ChangeMessagesIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Inject
DECL|field|reviewDbProvider
specifier|private
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|reviewDbProvider
decl_stmt|;
annotation|@
name|Inject
DECL|field|gApi
specifier|private
name|GerritApi
name|gApi
decl_stmt|;
annotation|@
name|Inject
DECL|field|atrScope
specifier|private
name|AcceptanceTestRequestScope
name|atrScope
decl_stmt|;
annotation|@
name|Inject
DECL|field|identifiedUserFactory
specifier|private
name|IdentifiedUser
operator|.
name|GenericFactory
name|identifiedUserFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|pushFactory
specifier|private
name|PushOneCommit
operator|.
name|Factory
name|pushFactory
decl_stmt|;
DECL|field|git
specifier|private
name|Git
name|git
decl_stmt|;
DECL|field|db
specifier|private
name|ReviewDb
name|db
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
name|Project
operator|.
name|NameKey
name|project
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"p"
argument_list|)
decl_stmt|;
name|SshSession
name|sshSession
init|=
operator|new
name|SshSession
argument_list|(
name|server
argument_list|,
name|admin
argument_list|)
decl_stmt|;
name|createProject
argument_list|(
name|sshSession
argument_list|,
name|project
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|git
operator|=
name|cloneProject
argument_list|(
name|sshSession
operator|.
name|getUrl
argument_list|()
operator|+
literal|"/"
operator|+
name|project
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|sshSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|db
operator|=
name|reviewDbProvider
operator|.
name|open
argument_list|()
expr_stmt|;
name|atrScope
operator|.
name|set
argument_list|(
name|atrScope
operator|.
name|newContext
argument_list|(
name|reviewDbProvider
argument_list|,
name|sshSession
argument_list|,
name|identifiedUserFactory
operator|.
name|create
argument_list|(
name|Providers
operator|.
name|of
argument_list|(
name|db
argument_list|)
argument_list|,
name|admin
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|messagesNotReturnedByDefault ()
specifier|public
name|void
name|messagesNotReturnedByDefault
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|IOException
throws|,
name|RestApiException
block|{
name|String
name|changeId
init|=
name|createChange
argument_list|()
decl_stmt|;
name|postMessage
argument_list|(
name|changeId
argument_list|,
literal|"Some nits need to be fixed."
argument_list|)
expr_stmt|;
name|ChangeInfo
name|c
init|=
name|getChange
argument_list|(
literal|"p~master~"
operator|+
name|changeId
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|c
operator|.
name|messages
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|defaultMessage ()
specifier|public
name|void
name|defaultMessage
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|IOException
throws|,
name|RestApiException
block|{
name|String
name|changeId
init|=
name|createChange
argument_list|()
decl_stmt|;
name|ChangeInfo
name|c
init|=
name|getChangeAll
argument_list|(
literal|"p~master~"
operator|+
name|changeId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|c
operator|.
name|messages
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|c
operator|.
name|messages
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Uploaded patch set 1."
argument_list|,
name|c
operator|.
name|messages
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|messagesReturnedInChronologicalOrder ()
specifier|public
name|void
name|messagesReturnedInChronologicalOrder
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|IOException
throws|,
name|RestApiException
block|{
name|String
name|changeId
init|=
name|createChange
argument_list|()
decl_stmt|;
name|String
name|firstMessage
init|=
literal|"Some nits need to be fixed."
decl_stmt|;
name|postMessage
argument_list|(
name|changeId
argument_list|,
name|firstMessage
argument_list|)
expr_stmt|;
name|String
name|secondMessage
init|=
literal|"I like this feature."
decl_stmt|;
name|postMessage
argument_list|(
name|changeId
argument_list|,
name|secondMessage
argument_list|)
expr_stmt|;
name|ChangeInfo
name|c
init|=
name|getChangeAll
argument_list|(
literal|"p~master~"
operator|+
name|changeId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|c
operator|.
name|messages
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|c
operator|.
name|messages
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|ChangeMessageInfo
argument_list|>
name|it
init|=
name|c
operator|.
name|messages
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Uploaded patch set 1."
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|.
name|message
argument_list|)
expr_stmt|;
name|assertMessage
argument_list|(
name|firstMessage
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|.
name|message
argument_list|)
expr_stmt|;
name|assertMessage
argument_list|(
name|secondMessage
argument_list|,
name|it
operator|.
name|next
argument_list|()
operator|.
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|createChange ()
specifier|private
name|String
name|createChange
parameter_list|()
throws|throws
name|GitAPIException
throws|,
name|IOException
block|{
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
argument_list|)
decl_stmt|;
return|return
name|push
operator|.
name|to
argument_list|(
name|git
argument_list|,
literal|"refs/for/master"
argument_list|)
operator|.
name|getChangeId
argument_list|()
return|;
block|}
DECL|method|assertMessage (String expected, String actual)
specifier|private
name|void
name|assertMessage
parameter_list|(
name|String
name|expected
parameter_list|,
name|String
name|actual
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Patch Set 1:\n\n"
operator|+
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
DECL|method|postMessage (String changeId, String msg)
specifier|private
name|void
name|postMessage
parameter_list|(
name|String
name|changeId
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|IOException
block|{
name|ReviewInput
name|in
init|=
operator|new
name|ReviewInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|message
operator|=
name|msg
expr_stmt|;
name|adminSession
operator|.
name|post
argument_list|(
literal|"/changes/"
operator|+
name|changeId
operator|+
literal|"/revisions/1/review"
argument_list|,
name|in
argument_list|)
operator|.
name|consume
argument_list|()
expr_stmt|;
block|}
DECL|method|getChange (String triplet)
specifier|private
name|ChangeInfo
name|getChange
parameter_list|(
name|String
name|triplet
parameter_list|)
throws|throws
name|RestApiException
block|{
return|return
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|triplet
argument_list|)
operator|.
name|info
argument_list|()
return|;
block|}
DECL|method|getChangeAll (String triplet)
specifier|private
name|ChangeInfo
name|getChangeAll
parameter_list|(
name|String
name|triplet
parameter_list|)
throws|throws
name|RestApiException
block|{
return|return
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|triplet
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

