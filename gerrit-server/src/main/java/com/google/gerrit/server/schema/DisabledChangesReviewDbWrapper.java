begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.schema
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|schema
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CheckedFuture
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
name|Account
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
name|ChangeMessage
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
name|client
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
name|server
operator|.
name|ChangeAccess
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
name|ChangeMessageAccess
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
name|PatchLineCommentAccess
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
name|PatchSetAccess
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
name|PatchSetApprovalAccess
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
name|reviewdb
operator|.
name|server
operator|.
name|ReviewDbWrapper
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
name|gwtorm
operator|.
name|server
operator|.
name|ResultSet
import|;
end_import

begin_class
DECL|class|DisabledChangesReviewDbWrapper
specifier|public
class|class
name|DisabledChangesReviewDbWrapper
extends|extends
name|ReviewDbWrapper
block|{
DECL|field|MSG
specifier|private
specifier|static
specifier|final
name|String
name|MSG
init|=
literal|"This table has been migrated to NoteDb"
decl_stmt|;
DECL|field|changes
specifier|private
specifier|final
name|DisabledChangeAccess
name|changes
decl_stmt|;
DECL|field|patchSetApprovals
specifier|private
specifier|final
name|DisabledPatchSetApprovalAccess
name|patchSetApprovals
decl_stmt|;
DECL|field|changeMessages
specifier|private
specifier|final
name|DisabledChangeMessageAccess
name|changeMessages
decl_stmt|;
DECL|field|patchSets
specifier|private
specifier|final
name|DisabledPatchSetAccess
name|patchSets
decl_stmt|;
DECL|field|patchComments
specifier|private
specifier|final
name|DisabledPatchLineCommentAccess
name|patchComments
decl_stmt|;
DECL|method|DisabledChangesReviewDbWrapper (ReviewDb db)
name|DisabledChangesReviewDbWrapper
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
block|{
name|super
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|changes
operator|=
operator|new
name|DisabledChangeAccess
argument_list|(
name|delegate
operator|.
name|changes
argument_list|()
argument_list|)
expr_stmt|;
name|patchSetApprovals
operator|=
operator|new
name|DisabledPatchSetApprovalAccess
argument_list|(
name|delegate
operator|.
name|patchSetApprovals
argument_list|()
argument_list|)
expr_stmt|;
name|changeMessages
operator|=
operator|new
name|DisabledChangeMessageAccess
argument_list|(
name|delegate
operator|.
name|changeMessages
argument_list|()
argument_list|)
expr_stmt|;
name|patchSets
operator|=
operator|new
name|DisabledPatchSetAccess
argument_list|(
name|delegate
operator|.
name|patchSets
argument_list|()
argument_list|)
expr_stmt|;
name|patchComments
operator|=
operator|new
name|DisabledPatchLineCommentAccess
argument_list|(
name|delegate
operator|.
name|patchComments
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|unsafeGetDelegate ()
specifier|public
name|ReviewDb
name|unsafeGetDelegate
parameter_list|()
block|{
return|return
name|delegate
return|;
block|}
annotation|@
name|Override
DECL|method|changes ()
specifier|public
name|ChangeAccess
name|changes
parameter_list|()
block|{
return|return
name|changes
return|;
block|}
annotation|@
name|Override
DECL|method|patchSetApprovals ()
specifier|public
name|PatchSetApprovalAccess
name|patchSetApprovals
parameter_list|()
block|{
return|return
name|patchSetApprovals
return|;
block|}
annotation|@
name|Override
DECL|method|changeMessages ()
specifier|public
name|ChangeMessageAccess
name|changeMessages
parameter_list|()
block|{
return|return
name|changeMessages
return|;
block|}
annotation|@
name|Override
DECL|method|patchSets ()
specifier|public
name|PatchSetAccess
name|patchSets
parameter_list|()
block|{
return|return
name|patchSets
return|;
block|}
annotation|@
name|Override
DECL|method|patchComments ()
specifier|public
name|PatchLineCommentAccess
name|patchComments
parameter_list|()
block|{
return|return
name|patchComments
return|;
block|}
DECL|class|DisabledChangeAccess
specifier|private
specifier|static
class|class
name|DisabledChangeAccess
extends|extends
name|ChangeAccessWrapper
block|{
DECL|method|DisabledChangeAccess (ChangeAccess delegate)
specifier|protected
name|DisabledChangeAccess
parameter_list|(
name|ChangeAccess
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterateAllEntities ()
specifier|public
name|ResultSet
argument_list|<
name|Change
argument_list|>
name|iterateAllEntities
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getAsync (Change.Id key)
specifier|public
name|CheckedFuture
argument_list|<
name|Change
argument_list|,
name|OrmException
argument_list|>
name|getAsync
parameter_list|(
name|Change
operator|.
name|Id
name|key
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|get (Iterable<Change.Id> keys)
specifier|public
name|ResultSet
argument_list|<
name|Change
argument_list|>
name|get
parameter_list|(
name|Iterable
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|keys
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|get (Change.Id id)
specifier|public
name|Change
name|get
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|)
throws|throws
name|OrmException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|all ()
specifier|public
name|ResultSet
argument_list|<
name|Change
argument_list|>
name|all
parameter_list|()
throws|throws
name|OrmException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|DisabledPatchSetApprovalAccess
specifier|private
specifier|static
class|class
name|DisabledPatchSetApprovalAccess
extends|extends
name|PatchSetApprovalAccessWrapper
block|{
DECL|method|DisabledPatchSetApprovalAccess (PatchSetApprovalAccess delegate)
name|DisabledPatchSetApprovalAccess
parameter_list|(
name|PatchSetApprovalAccess
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterateAllEntities ()
specifier|public
name|ResultSet
argument_list|<
name|PatchSetApproval
argument_list|>
name|iterateAllEntities
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getAsync ( PatchSetApproval.Key key)
specifier|public
name|CheckedFuture
argument_list|<
name|PatchSetApproval
argument_list|,
name|OrmException
argument_list|>
name|getAsync
parameter_list|(
name|PatchSetApproval
operator|.
name|Key
name|key
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|get ( Iterable<PatchSetApproval.Key> keys)
specifier|public
name|ResultSet
argument_list|<
name|PatchSetApproval
argument_list|>
name|get
parameter_list|(
name|Iterable
argument_list|<
name|PatchSetApproval
operator|.
name|Key
argument_list|>
name|keys
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|get (PatchSetApproval.Key key)
specifier|public
name|PatchSetApproval
name|get
parameter_list|(
name|PatchSetApproval
operator|.
name|Key
name|key
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|byChange (Change.Id id)
specifier|public
name|ResultSet
argument_list|<
name|PatchSetApproval
argument_list|>
name|byChange
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|byPatchSet (PatchSet.Id id)
specifier|public
name|ResultSet
argument_list|<
name|PatchSetApproval
argument_list|>
name|byPatchSet
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|DisabledChangeMessageAccess
specifier|private
specifier|static
class|class
name|DisabledChangeMessageAccess
extends|extends
name|ChangeMessageAccessWrapper
block|{
DECL|method|DisabledChangeMessageAccess (ChangeMessageAccess delegate)
name|DisabledChangeMessageAccess
parameter_list|(
name|ChangeMessageAccess
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterateAllEntities ()
specifier|public
name|ResultSet
argument_list|<
name|ChangeMessage
argument_list|>
name|iterateAllEntities
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getAsync ( ChangeMessage.Key key)
specifier|public
name|CheckedFuture
argument_list|<
name|ChangeMessage
argument_list|,
name|OrmException
argument_list|>
name|getAsync
parameter_list|(
name|ChangeMessage
operator|.
name|Key
name|key
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|get (Iterable<ChangeMessage.Key> keys)
specifier|public
name|ResultSet
argument_list|<
name|ChangeMessage
argument_list|>
name|get
parameter_list|(
name|Iterable
argument_list|<
name|ChangeMessage
operator|.
name|Key
argument_list|>
name|keys
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|get (ChangeMessage.Key id)
specifier|public
name|ChangeMessage
name|get
parameter_list|(
name|ChangeMessage
operator|.
name|Key
name|id
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|byChange (Change.Id id)
specifier|public
name|ResultSet
argument_list|<
name|ChangeMessage
argument_list|>
name|byChange
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|byPatchSet (PatchSet.Id id)
specifier|public
name|ResultSet
argument_list|<
name|ChangeMessage
argument_list|>
name|byPatchSet
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|all ()
specifier|public
name|ResultSet
argument_list|<
name|ChangeMessage
argument_list|>
name|all
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|DisabledPatchSetAccess
specifier|private
specifier|static
class|class
name|DisabledPatchSetAccess
extends|extends
name|PatchSetAccessWrapper
block|{
DECL|method|DisabledPatchSetAccess (PatchSetAccess delegate)
name|DisabledPatchSetAccess
parameter_list|(
name|PatchSetAccess
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterateAllEntities ()
specifier|public
name|ResultSet
argument_list|<
name|PatchSet
argument_list|>
name|iterateAllEntities
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getAsync (PatchSet.Id key)
specifier|public
name|CheckedFuture
argument_list|<
name|PatchSet
argument_list|,
name|OrmException
argument_list|>
name|getAsync
parameter_list|(
name|PatchSet
operator|.
name|Id
name|key
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|get (Iterable<PatchSet.Id> keys)
specifier|public
name|ResultSet
argument_list|<
name|PatchSet
argument_list|>
name|get
parameter_list|(
name|Iterable
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|>
name|keys
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|get (PatchSet.Id id)
specifier|public
name|PatchSet
name|get
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|byChange (Change.Id id)
specifier|public
name|ResultSet
argument_list|<
name|PatchSet
argument_list|>
name|byChange
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
block|}
DECL|class|DisabledPatchLineCommentAccess
specifier|private
specifier|static
class|class
name|DisabledPatchLineCommentAccess
extends|extends
name|PatchLineCommentAccessWrapper
block|{
DECL|method|DisabledPatchLineCommentAccess (PatchLineCommentAccess delegate)
name|DisabledPatchLineCommentAccess
parameter_list|(
name|PatchLineCommentAccess
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterateAllEntities ()
specifier|public
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|iterateAllEntities
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getAsync ( PatchLineComment.Key key)
specifier|public
name|CheckedFuture
argument_list|<
name|PatchLineComment
argument_list|,
name|OrmException
argument_list|>
name|getAsync
parameter_list|(
name|PatchLineComment
operator|.
name|Key
name|key
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|get ( Iterable<PatchLineComment.Key> keys)
specifier|public
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|get
parameter_list|(
name|Iterable
argument_list|<
name|PatchLineComment
operator|.
name|Key
argument_list|>
name|keys
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|get (PatchLineComment.Key id)
specifier|public
name|PatchLineComment
name|get
parameter_list|(
name|PatchLineComment
operator|.
name|Key
name|id
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|byChange (Change.Id id)
specifier|public
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|byChange
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|byPatchSet (PatchSet.Id id)
specifier|public
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|byPatchSet
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|publishedByChangeFile (Change.Id id, String file)
specifier|public
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|publishedByChangeFile
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|,
name|String
name|file
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|publishedByPatchSet ( PatchSet.Id patchset)
specifier|public
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|publishedByPatchSet
parameter_list|(
name|PatchSet
operator|.
name|Id
name|patchset
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|draftByPatchSetAuthor ( PatchSet.Id patchset, Account.Id author)
specifier|public
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|draftByPatchSetAuthor
parameter_list|(
name|PatchSet
operator|.
name|Id
name|patchset
parameter_list|,
name|Account
operator|.
name|Id
name|author
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|draftByChangeFileAuthor (Change.Id id, String file, Account.Id author)
specifier|public
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|draftByChangeFileAuthor
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|,
name|String
name|file
parameter_list|,
name|Account
operator|.
name|Id
name|author
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|draftByAuthor (Account.Id author)
specifier|public
name|ResultSet
argument_list|<
name|PatchLineComment
argument_list|>
name|draftByAuthor
parameter_list|(
name|Account
operator|.
name|Id
name|author
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|MSG
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

