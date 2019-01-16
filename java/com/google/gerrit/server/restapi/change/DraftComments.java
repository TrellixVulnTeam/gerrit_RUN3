begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.restapi.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|restapi
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
name|registration
operator|.
name|DynamicMap
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
name|AuthException
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
name|ChildCollection
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
name|IdString
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
name|ResourceNotFoundException
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
name|RestView
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
name|server
operator|.
name|CommentsUtil
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
name|change
operator|.
name|DraftCommentResource
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
name|RevisionResource
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Singleton
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|DraftComments
specifier|public
class|class
name|DraftComments
implements|implements
name|ChildCollection
argument_list|<
name|RevisionResource
argument_list|,
name|DraftCommentResource
argument_list|>
block|{
DECL|field|views
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|DraftCommentResource
argument_list|>
argument_list|>
name|views
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
decl_stmt|;
DECL|field|list
specifier|private
specifier|final
name|ListRevisionDrafts
name|list
decl_stmt|;
DECL|field|commentsUtil
specifier|private
specifier|final
name|CommentsUtil
name|commentsUtil
decl_stmt|;
annotation|@
name|Inject
DECL|method|DraftComments ( DynamicMap<RestView<DraftCommentResource>> views, Provider<CurrentUser> user, ListRevisionDrafts list, CommentsUtil commentsUtil)
name|DraftComments
parameter_list|(
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|DraftCommentResource
argument_list|>
argument_list|>
name|views
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
parameter_list|,
name|ListRevisionDrafts
name|list
parameter_list|,
name|CommentsUtil
name|commentsUtil
parameter_list|)
block|{
name|this
operator|.
name|views
operator|=
name|views
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
name|this
operator|.
name|commentsUtil
operator|=
name|commentsUtil
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|views ()
specifier|public
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|DraftCommentResource
argument_list|>
argument_list|>
name|views
parameter_list|()
block|{
return|return
name|views
return|;
block|}
annotation|@
name|Override
DECL|method|list ()
specifier|public
name|ListRevisionDrafts
name|list
parameter_list|()
throws|throws
name|AuthException
block|{
name|checkIdentifiedUser
argument_list|()
expr_stmt|;
return|return
name|list
return|;
block|}
annotation|@
name|Override
DECL|method|parse (RevisionResource rev, IdString id)
specifier|public
name|DraftCommentResource
name|parse
parameter_list|(
name|RevisionResource
name|rev
parameter_list|,
name|IdString
name|id
parameter_list|)
throws|throws
name|ResourceNotFoundException
throws|,
name|StorageException
throws|,
name|AuthException
block|{
name|checkIdentifiedUser
argument_list|()
expr_stmt|;
name|String
name|uuid
init|=
name|id
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|Comment
name|c
range|:
name|commentsUtil
operator|.
name|draftByPatchSetAuthor
argument_list|(
name|rev
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|rev
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|rev
operator|.
name|getNotes
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
name|uuid
operator|.
name|equals
argument_list|(
name|c
operator|.
name|key
operator|.
name|uuid
argument_list|)
condition|)
block|{
return|return
operator|new
name|DraftCommentResource
argument_list|(
name|rev
argument_list|,
name|c
argument_list|)
return|;
block|}
block|}
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
block|}
DECL|method|checkIdentifiedUser ()
specifier|private
name|void
name|checkIdentifiedUser
parameter_list|()
throws|throws
name|AuthException
block|{
if|if
condition|(
operator|!
operator|(
name|user
operator|.
name|get
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"drafts only available to authenticated users"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

