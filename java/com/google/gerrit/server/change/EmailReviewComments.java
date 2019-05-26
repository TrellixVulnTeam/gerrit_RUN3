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
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|CommentsUtil
operator|.
name|COMMENT_ORDER
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
name|flogger
operator|.
name|FluentLogger
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
name|entities
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
name|entities
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
name|entities
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
name|IdentifiedUser
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
name|config
operator|.
name|SendEmailExecutor
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
name|mail
operator|.
name|send
operator|.
name|CommentSender
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
name|patch
operator|.
name|PatchSetInfoFactory
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
name|util
operator|.
name|LabelVote
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
name|util
operator|.
name|RequestContext
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
name|util
operator|.
name|ThreadLocalRequestContext
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
name|assistedinject
operator|.
name|Assisted
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
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_class
DECL|class|EmailReviewComments
specifier|public
class|class
name|EmailReviewComments
implements|implements
name|Runnable
implements|,
name|RequestContext
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
comment|// TODO(dborowitz/wyatta): Rationalize these arguments so HTML and text templates are operating
comment|// on the same set of inputs.
comment|/**      * @param notify setting for handling notification.      * @param notes change notes.      * @param patchSet patch set corresponding to the top-level op      * @param user user the email should come from.      * @param message used by text template only: the full ChangeMessage that will go in the      *     database. The contents of this message typically include the "Patch set N" header and "(M      *     comments)".      * @param comments inline comments.      * @param patchSetComment used by HTML template only: some quasi-human-generated text. The      *     contents should *not* include a "Patch set N" header or "(M comments)" footer, as these      *     will be added automatically in soy in a structured way.      * @param labels labels applied as part of this review operation.      * @return handle for sending email.      */
DECL|method|create ( NotifyResolver.Result notify, ChangeNotes notes, PatchSet patchSet, IdentifiedUser user, ChangeMessage message, List<Comment> comments, String patchSetComment, List<LabelVote> labels)
name|EmailReviewComments
name|create
parameter_list|(
name|NotifyResolver
operator|.
name|Result
name|notify
parameter_list|,
name|ChangeNotes
name|notes
parameter_list|,
name|PatchSet
name|patchSet
parameter_list|,
name|IdentifiedUser
name|user
parameter_list|,
name|ChangeMessage
name|message
parameter_list|,
name|List
argument_list|<
name|Comment
argument_list|>
name|comments
parameter_list|,
name|String
name|patchSetComment
parameter_list|,
name|List
argument_list|<
name|LabelVote
argument_list|>
name|labels
parameter_list|)
function_decl|;
block|}
DECL|field|sendEmailsExecutor
specifier|private
specifier|final
name|ExecutorService
name|sendEmailsExecutor
decl_stmt|;
DECL|field|patchSetInfoFactory
specifier|private
specifier|final
name|PatchSetInfoFactory
name|patchSetInfoFactory
decl_stmt|;
DECL|field|commentSenderFactory
specifier|private
specifier|final
name|CommentSender
operator|.
name|Factory
name|commentSenderFactory
decl_stmt|;
DECL|field|requestContext
specifier|private
specifier|final
name|ThreadLocalRequestContext
name|requestContext
decl_stmt|;
DECL|field|notify
specifier|private
specifier|final
name|NotifyResolver
operator|.
name|Result
name|notify
decl_stmt|;
DECL|field|notes
specifier|private
specifier|final
name|ChangeNotes
name|notes
decl_stmt|;
DECL|field|patchSet
specifier|private
specifier|final
name|PatchSet
name|patchSet
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|IdentifiedUser
name|user
decl_stmt|;
DECL|field|message
specifier|private
specifier|final
name|ChangeMessage
name|message
decl_stmt|;
DECL|field|comments
specifier|private
specifier|final
name|List
argument_list|<
name|Comment
argument_list|>
name|comments
decl_stmt|;
DECL|field|patchSetComment
specifier|private
specifier|final
name|String
name|patchSetComment
decl_stmt|;
DECL|field|labels
specifier|private
specifier|final
name|List
argument_list|<
name|LabelVote
argument_list|>
name|labels
decl_stmt|;
annotation|@
name|Inject
DECL|method|EmailReviewComments ( @endEmailExecutor ExecutorService executor, PatchSetInfoFactory patchSetInfoFactory, CommentSender.Factory commentSenderFactory, ThreadLocalRequestContext requestContext, @Assisted NotifyResolver.Result notify, @Assisted ChangeNotes notes, @Assisted PatchSet patchSet, @Assisted IdentifiedUser user, @Assisted ChangeMessage message, @Assisted List<Comment> comments, @Nullable @Assisted String patchSetComment, @Assisted List<LabelVote> labels)
name|EmailReviewComments
parameter_list|(
annotation|@
name|SendEmailExecutor
name|ExecutorService
name|executor
parameter_list|,
name|PatchSetInfoFactory
name|patchSetInfoFactory
parameter_list|,
name|CommentSender
operator|.
name|Factory
name|commentSenderFactory
parameter_list|,
name|ThreadLocalRequestContext
name|requestContext
parameter_list|,
annotation|@
name|Assisted
name|NotifyResolver
operator|.
name|Result
name|notify
parameter_list|,
annotation|@
name|Assisted
name|ChangeNotes
name|notes
parameter_list|,
annotation|@
name|Assisted
name|PatchSet
name|patchSet
parameter_list|,
annotation|@
name|Assisted
name|IdentifiedUser
name|user
parameter_list|,
annotation|@
name|Assisted
name|ChangeMessage
name|message
parameter_list|,
annotation|@
name|Assisted
name|List
argument_list|<
name|Comment
argument_list|>
name|comments
parameter_list|,
annotation|@
name|Nullable
annotation|@
name|Assisted
name|String
name|patchSetComment
parameter_list|,
annotation|@
name|Assisted
name|List
argument_list|<
name|LabelVote
argument_list|>
name|labels
parameter_list|)
block|{
name|this
operator|.
name|sendEmailsExecutor
operator|=
name|executor
expr_stmt|;
name|this
operator|.
name|patchSetInfoFactory
operator|=
name|patchSetInfoFactory
expr_stmt|;
name|this
operator|.
name|commentSenderFactory
operator|=
name|commentSenderFactory
expr_stmt|;
name|this
operator|.
name|requestContext
operator|=
name|requestContext
expr_stmt|;
name|this
operator|.
name|notify
operator|=
name|notify
expr_stmt|;
name|this
operator|.
name|notes
operator|=
name|notes
expr_stmt|;
name|this
operator|.
name|patchSet
operator|=
name|patchSet
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|comments
operator|=
name|COMMENT_ORDER
operator|.
name|sortedCopy
argument_list|(
name|comments
argument_list|)
expr_stmt|;
name|this
operator|.
name|patchSetComment
operator|=
name|patchSetComment
expr_stmt|;
name|this
operator|.
name|labels
operator|=
name|labels
expr_stmt|;
block|}
DECL|method|sendAsync ()
specifier|public
name|void
name|sendAsync
parameter_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|Future
argument_list|<
name|?
argument_list|>
name|possiblyIgnoredError
init|=
name|sendEmailsExecutor
operator|.
name|submit
argument_list|(
name|this
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|RequestContext
name|old
init|=
name|requestContext
operator|.
name|setContext
argument_list|(
name|this
argument_list|)
decl_stmt|;
try|try
block|{
name|CommentSender
name|cm
init|=
name|commentSenderFactory
operator|.
name|create
argument_list|(
name|notes
operator|.
name|getProjectName
argument_list|()
argument_list|,
name|notes
operator|.
name|getChangeId
argument_list|()
argument_list|)
decl_stmt|;
name|cm
operator|.
name|setFrom
argument_list|(
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setPatchSet
argument_list|(
name|patchSet
argument_list|,
name|patchSetInfoFactory
operator|.
name|get
argument_list|(
name|notes
operator|.
name|getProjectName
argument_list|()
argument_list|,
name|patchSet
argument_list|)
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setChangeMessage
argument_list|(
name|message
operator|.
name|getMessage
argument_list|()
argument_list|,
name|message
operator|.
name|getWrittenOn
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setComments
argument_list|(
name|comments
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setPatchSetComment
argument_list|(
name|patchSetComment
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setLabels
argument_list|(
name|labels
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setNotify
argument_list|(
name|notify
argument_list|)
expr_stmt|;
name|cm
operator|.
name|send
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Cannot email comments for %s"
argument_list|,
name|patchSet
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|requestContext
operator|.
name|setContext
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"send-email comments"
return|;
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|CurrentUser
name|getUser
parameter_list|()
block|{
return|return
name|user
operator|.
name|getRealUser
argument_list|()
return|;
block|}
block|}
end_class

end_unit

