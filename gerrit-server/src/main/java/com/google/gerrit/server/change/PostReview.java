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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
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
name|base
operator|.
name|Strings
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
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|ChangeHooks
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
name|data
operator|.
name|ApprovalType
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
name|data
operator|.
name|ApprovalTypes
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
name|data
operator|.
name|Permission
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
name|data
operator|.
name|PermissionRange
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
name|BadRequestException
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
name|DefaultInput
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
name|RestModifyView
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
name|ApprovalCategory
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
name|ApprovalCategoryValue
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
name|Patch
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
name|ChangeUtil
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
name|change
operator|.
name|PostReview
operator|.
name|Input
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
name|project
operator|.
name|ChangeControl
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
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
DECL|class|PostReview
class|class
name|PostReview
implements|implements
name|RestModifyView
argument_list|<
name|RevisionResource
argument_list|,
name|Input
argument_list|>
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PostReview
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|Input
specifier|static
class|class
name|Input
block|{
annotation|@
name|DefaultInput
DECL|field|message
name|String
name|message
decl_stmt|;
DECL|field|labels
name|Map
argument_list|<
name|String
argument_list|,
name|Short
argument_list|>
name|labels
decl_stmt|;
DECL|field|comments
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Comment
argument_list|>
argument_list|>
name|comments
decl_stmt|;
comment|/**      * If true require all labels to be within the user's permitted ranges based      * on access controls, attempting to use a label not granted to the user      * will fail the entire modify operation early. If false the operation will      * execute anyway, but the proposed labels given by the user will be      * modified to be the "best" value allowed by the access controls.      */
DECL|field|strictLabels
name|boolean
name|strictLabels
init|=
literal|true
decl_stmt|;
comment|/**      * How to process draft comments already in the database that were not also      * described in this input request.      */
DECL|field|drafts
name|DraftHandling
name|drafts
init|=
name|DraftHandling
operator|.
name|DELETE
decl_stmt|;
block|}
DECL|enum|DraftHandling
specifier|static
enum|enum
name|DraftHandling
block|{
DECL|enumConstant|DELETE
DECL|enumConstant|PUBLISH
DECL|enumConstant|KEEP
name|DELETE
block|,
name|PUBLISH
block|,
name|KEEP
block|;   }
DECL|enum|Side
specifier|static
enum|enum
name|Side
block|{
DECL|enumConstant|PARENT
DECL|enumConstant|REVISION
name|PARENT
block|,
name|REVISION
block|;   }
DECL|class|Comment
specifier|static
class|class
name|Comment
block|{
DECL|field|id
name|String
name|id
decl_stmt|;
DECL|field|side
name|Side
name|side
decl_stmt|;
DECL|field|line
name|int
name|line
decl_stmt|;
DECL|field|message
name|String
name|message
decl_stmt|;
block|}
DECL|class|Output
specifier|static
class|class
name|Output
block|{
DECL|field|labels
name|Map
argument_list|<
name|String
argument_list|,
name|Short
argument_list|>
name|labels
decl_stmt|;
block|}
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|approvalTypes
specifier|private
specifier|final
name|ApprovalTypes
name|approvalTypes
decl_stmt|;
DECL|field|email
specifier|private
specifier|final
name|EmailReviewComments
operator|.
name|Factory
name|email
decl_stmt|;
DECL|field|hooks
annotation|@
name|Deprecated
specifier|private
specifier|final
name|ChangeHooks
name|hooks
decl_stmt|;
DECL|field|change
specifier|private
name|Change
name|change
decl_stmt|;
DECL|field|message
specifier|private
name|ChangeMessage
name|message
decl_stmt|;
DECL|field|timestamp
specifier|private
name|Timestamp
name|timestamp
decl_stmt|;
DECL|field|comments
specifier|private
name|List
argument_list|<
name|PatchLineComment
argument_list|>
name|comments
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|labelDelta
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|labelDelta
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|categories
annotation|@
name|Deprecated
specifier|private
name|Map
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
name|categories
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|PostReview (ReviewDb db, ApprovalTypes approvalTypes, EmailReviewComments.Factory email, ChangeHooks hooks)
name|PostReview
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|ApprovalTypes
name|approvalTypes
parameter_list|,
name|EmailReviewComments
operator|.
name|Factory
name|email
parameter_list|,
name|ChangeHooks
name|hooks
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|approvalTypes
operator|=
name|approvalTypes
expr_stmt|;
name|this
operator|.
name|email
operator|=
name|email
expr_stmt|;
name|this
operator|.
name|hooks
operator|=
name|hooks
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|inputType ()
specifier|public
name|Class
argument_list|<
name|Input
argument_list|>
name|inputType
parameter_list|()
block|{
return|return
name|Input
operator|.
name|class
return|;
block|}
annotation|@
name|Override
DECL|method|apply (RevisionResource revision, Input input)
specifier|public
name|Object
name|apply
parameter_list|(
name|RevisionResource
name|revision
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|BadRequestException
throws|,
name|OrmException
block|{
if|if
condition|(
name|input
operator|.
name|labels
operator|!=
literal|null
condition|)
block|{
name|checkLabels
argument_list|(
name|revision
argument_list|,
name|input
operator|.
name|strictLabels
argument_list|,
name|input
operator|.
name|labels
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|comments
operator|!=
literal|null
condition|)
block|{
name|checkComments
argument_list|(
name|input
operator|.
name|comments
argument_list|)
expr_stmt|;
block|}
name|db
operator|.
name|changes
argument_list|()
operator|.
name|beginTransaction
argument_list|(
name|revision
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|change
operator|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|revision
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|ChangeUtil
operator|.
name|updated
argument_list|(
name|change
argument_list|)
expr_stmt|;
name|timestamp
operator|=
name|change
operator|.
name|getLastUpdatedOn
argument_list|()
expr_stmt|;
if|if
condition|(
name|input
operator|.
name|comments
operator|!=
literal|null
condition|)
block|{
name|insertComments
argument_list|(
name|revision
argument_list|,
name|input
operator|.
name|comments
argument_list|,
name|input
operator|.
name|drafts
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
operator|&&
name|input
operator|.
name|labels
operator|!=
literal|null
condition|)
block|{
comment|// TODO Allow updating some labels even when closed.
name|updateLabels
argument_list|(
name|revision
argument_list|,
name|input
operator|.
name|labels
argument_list|)
expr_stmt|;
block|}
name|insertMessage
argument_list|(
name|revision
argument_list|,
name|input
operator|.
name|message
argument_list|)
expr_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|change
argument_list|)
argument_list|)
expr_stmt|;
name|db
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|db
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
name|email
operator|.
name|create
argument_list|(
name|change
argument_list|,
name|revision
operator|.
name|getPatchSet
argument_list|()
argument_list|,
name|revision
operator|.
name|getAuthorId
argument_list|()
argument_list|,
name|message
argument_list|,
name|comments
argument_list|)
operator|.
name|sendAsync
argument_list|()
expr_stmt|;
name|fireCommentAddedHook
argument_list|(
name|revision
argument_list|)
expr_stmt|;
name|Output
name|output
init|=
operator|new
name|Output
argument_list|()
decl_stmt|;
name|output
operator|.
name|labels
operator|=
name|input
operator|.
name|labels
expr_stmt|;
return|return
name|output
return|;
block|}
DECL|method|checkLabels (RevisionResource revision, boolean strict, Map<String, Short> labels)
specifier|private
name|void
name|checkLabels
parameter_list|(
name|RevisionResource
name|revision
parameter_list|,
name|boolean
name|strict
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Short
argument_list|>
name|labels
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|AuthException
block|{
name|ChangeControl
name|ctl
init|=
name|revision
operator|.
name|getControl
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Short
argument_list|>
argument_list|>
name|itr
init|=
name|labels
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Short
argument_list|>
name|ent
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// TODO Support more generic label assignments.
name|ApprovalType
name|at
init|=
name|approvalTypes
operator|.
name|byLabel
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|at
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|strict
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"label \"%s\" is not a configured ApprovalCategory"
argument_list|,
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
name|itr
operator|.
name|remove
argument_list|()
expr_stmt|;
continue|continue;
block|}
block|}
if|if
condition|(
name|ent
operator|.
name|getValue
argument_list|()
operator|==
literal|null
operator|||
name|ent
operator|.
name|getValue
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// Always permit 0, even if it is not within range.
comment|// Later null/0 will be deleted and revoke the label.
continue|continue;
block|}
if|if
condition|(
operator|!
name|at
operator|.
name|getValuesAsList
argument_list|()
operator|.
name|contains
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|strict
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"label \"%s\": %d is not a valid value"
argument_list|,
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
name|itr
operator|.
name|remove
argument_list|()
expr_stmt|;
continue|continue;
block|}
block|}
name|String
name|name
init|=
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getLabelName
argument_list|()
decl_stmt|;
name|PermissionRange
name|range
init|=
name|ctl
operator|.
name|getRange
argument_list|(
name|Permission
operator|.
name|forLabel
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|range
operator|==
literal|null
operator|||
operator|!
name|range
operator|.
name|contains
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|strict
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Applying label \"%s\": %d is restricted"
argument_list|,
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|range
operator|==
literal|null
operator|||
name|range
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ent
operator|.
name|setValue
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ent
operator|.
name|setValue
argument_list|(
operator|(
name|short
operator|)
name|range
operator|.
name|squash
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|checkComments (Map<String, List<Comment>> in)
specifier|private
name|void
name|checkComments
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Comment
argument_list|>
argument_list|>
name|in
parameter_list|)
throws|throws
name|BadRequestException
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Comment
argument_list|>
argument_list|>
argument_list|>
name|mapItr
init|=
name|in
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|mapItr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Comment
argument_list|>
argument_list|>
name|ent
init|=
name|mapItr
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Comment
argument_list|>
name|list
init|=
name|ent
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|mapItr
operator|.
name|remove
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|Iterator
argument_list|<
name|Comment
argument_list|>
name|listItr
init|=
name|list
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|listItr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Comment
name|c
init|=
name|listItr
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|line
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"negative line number %d not allowed on %s"
argument_list|,
name|c
operator|.
name|line
argument_list|,
name|path
argument_list|)
argument_list|)
throw|;
block|}
name|c
operator|.
name|message
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|c
operator|.
name|message
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|c
operator|.
name|message
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|listItr
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|mapItr
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|insertComments (RevisionResource rsrc, Map<String, List<Comment>> in, DraftHandling draftsHandling)
specifier|private
name|void
name|insertComments
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Comment
argument_list|>
argument_list|>
name|in
parameter_list|,
name|DraftHandling
name|draftsHandling
parameter_list|)
throws|throws
name|OrmException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|PatchLineComment
argument_list|>
name|drafts
init|=
name|scanDraftComments
argument_list|(
name|rsrc
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|PatchLineComment
argument_list|>
name|del
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PatchLineComment
argument_list|>
name|ins
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PatchLineComment
argument_list|>
name|upd
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Comment
argument_list|>
argument_list|>
name|ent
range|:
name|in
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|path
init|=
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
for|for
control|(
name|Comment
name|c
range|:
name|ent
operator|.
name|getValue
argument_list|()
control|)
block|{
name|PatchLineComment
name|e
init|=
name|drafts
operator|.
name|remove
argument_list|(
name|c
operator|.
name|id
argument_list|)
decl_stmt|;
name|boolean
name|create
init|=
name|e
operator|==
literal|null
decl_stmt|;
if|if
condition|(
name|create
condition|)
block|{
name|e
operator|=
operator|new
name|PatchLineComment
argument_list|(
operator|new
name|PatchLineComment
operator|.
name|Key
argument_list|(
operator|new
name|Patch
operator|.
name|Key
argument_list|(
name|rsrc
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|path
argument_list|)
argument_list|,
name|ChangeUtil
operator|.
name|messageUUID
argument_list|(
name|db
argument_list|)
argument_list|)
argument_list|,
name|c
operator|.
name|line
argument_list|,
name|rsrc
operator|.
name|getAuthorId
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|e
operator|.
name|setStatus
argument_list|(
name|PatchLineComment
operator|.
name|Status
operator|.
name|PUBLISHED
argument_list|)
expr_stmt|;
name|e
operator|.
name|setWrittenOn
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
name|e
operator|.
name|setSide
argument_list|(
name|c
operator|.
name|side
operator|==
name|Side
operator|.
name|PARENT
condition|?
operator|(
name|short
operator|)
literal|0
else|:
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|e
operator|.
name|setMessage
argument_list|(
name|c
operator|.
name|message
argument_list|)
expr_stmt|;
operator|(
name|create
condition|?
name|ins
else|:
name|upd
operator|)
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
switch|switch
condition|(
name|Objects
operator|.
name|firstNonNull
argument_list|(
name|draftsHandling
argument_list|,
name|DraftHandling
operator|.
name|DELETE
argument_list|)
condition|)
block|{
case|case
name|KEEP
case|:
default|default:
break|break;
case|case
name|DELETE
case|:
name|del
operator|.
name|addAll
argument_list|(
name|drafts
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|PUBLISH
case|:
for|for
control|(
name|PatchLineComment
name|e
range|:
name|drafts
operator|.
name|values
argument_list|()
control|)
block|{
name|e
operator|.
name|setStatus
argument_list|(
name|PatchLineComment
operator|.
name|Status
operator|.
name|PUBLISHED
argument_list|)
expr_stmt|;
name|e
operator|.
name|setWrittenOn
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
name|upd
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|delete
argument_list|(
name|del
argument_list|)
expr_stmt|;
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|insert
argument_list|(
name|ins
argument_list|)
expr_stmt|;
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|update
argument_list|(
name|upd
argument_list|)
expr_stmt|;
name|comments
operator|.
name|addAll
argument_list|(
name|ins
argument_list|)
expr_stmt|;
name|comments
operator|.
name|addAll
argument_list|(
name|upd
argument_list|)
expr_stmt|;
block|}
DECL|method|scanDraftComments ( RevisionResource rsrc)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|PatchLineComment
argument_list|>
name|scanDraftComments
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|)
throws|throws
name|OrmException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|PatchLineComment
argument_list|>
name|drafts
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchLineComment
name|c
range|:
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|draftByPatchSetAuthor
argument_list|(
name|rsrc
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getAuthorId
argument_list|()
argument_list|)
control|)
block|{
name|drafts
operator|.
name|put
argument_list|(
name|c
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|drafts
return|;
block|}
DECL|method|updateLabels (RevisionResource rsrc, Map<String, Short> labels)
specifier|private
name|void
name|updateLabels
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Short
argument_list|>
name|labels
parameter_list|)
throws|throws
name|OrmException
block|{
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|del
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|ins
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|upd
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|PatchSetApproval
argument_list|>
name|current
init|=
name|scanLabels
argument_list|(
name|rsrc
argument_list|,
name|del
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Short
argument_list|>
name|ent
range|:
name|labels
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|// TODO Support arbitrary label names.
name|ApprovalType
name|at
init|=
name|approvalTypes
operator|.
name|byLabel
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getLabelName
argument_list|()
decl_stmt|;
name|PatchSetApproval
name|c
init|=
name|current
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|ent
operator|.
name|getValue
argument_list|()
operator|==
literal|null
operator|||
name|ent
operator|.
name|getValue
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// User requested delete of this label.
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|del
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|labelDelta
operator|.
name|add
argument_list|(
literal|"-"
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
if|if
condition|(
name|c
operator|!=
literal|null
operator|&&
name|c
operator|.
name|getValue
argument_list|()
operator|!=
name|ent
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|c
operator|.
name|setValue
argument_list|(
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|setGranted
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
name|c
operator|.
name|cache
argument_list|(
name|change
argument_list|)
expr_stmt|;
name|upd
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|labelDelta
operator|.
name|add
argument_list|(
name|format
argument_list|(
name|name
argument_list|,
name|c
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|categories
operator|.
name|put
argument_list|(
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|at
operator|.
name|getValue
argument_list|(
name|c
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
name|c
operator|=
operator|new
name|PatchSetApproval
argument_list|(
operator|new
name|PatchSetApproval
operator|.
name|Key
argument_list|(
name|rsrc
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getAuthorId
argument_list|()
argument_list|,
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|setGranted
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
name|c
operator|.
name|cache
argument_list|(
name|change
argument_list|)
expr_stmt|;
name|ins
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|labelDelta
operator|.
name|add
argument_list|(
name|format
argument_list|(
name|name
argument_list|,
name|c
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|categories
operator|.
name|put
argument_list|(
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|at
operator|.
name|getValue
argument_list|(
name|c
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|delete
argument_list|(
name|del
argument_list|)
expr_stmt|;
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|insert
argument_list|(
name|ins
argument_list|)
expr_stmt|;
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|update
argument_list|(
name|upd
argument_list|)
expr_stmt|;
block|}
DECL|method|scanLabels (RevisionResource rsrc, List<PatchSetApproval> del)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|PatchSetApproval
argument_list|>
name|scanLabels
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|,
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|del
parameter_list|)
throws|throws
name|OrmException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|PatchSetApproval
argument_list|>
name|current
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|a
range|:
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byPatchSetUser
argument_list|(
name|rsrc
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getAuthorId
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
name|ApprovalCategory
operator|.
name|SUBMIT
operator|.
name|equals
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|ApprovalType
name|at
init|=
name|approvalTypes
operator|.
name|byId
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|at
operator|!=
literal|null
condition|)
block|{
name|current
operator|.
name|put
argument_list|(
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getLabelName
argument_list|()
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|del
operator|.
name|add
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|current
return|;
block|}
DECL|method|format (String name, short value)
specifier|private
specifier|static
name|String
name|format
parameter_list|(
name|String
name|name
parameter_list|,
name|short
name|value
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|name
operator|.
name|length
argument_list|()
operator|+
literal|2
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|>=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'+'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|insertMessage (RevisionResource rsrc, String msg)
specifier|private
name|void
name|insertMessage
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|OrmException
block|{
name|msg
operator|=
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|msg
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Patch Set %d:"
argument_list|,
name|rsrc
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|d
range|:
name|labelDelta
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|comments
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"\n\n(1 inline comment)"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|comments
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"\n\n(%d inline comments)"
argument_list|,
name|comments
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|msg
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
operator|.
name|append
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|message
operator|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|ChangeUtil
operator|.
name|messageUUID
argument_list|(
name|db
argument_list|)
argument_list|)
argument_list|,
name|rsrc
operator|.
name|getAuthorId
argument_list|()
argument_list|,
name|timestamp
argument_list|,
name|rsrc
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|setMessage
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|db
operator|.
name|changeMessages
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|fireCommentAddedHook (RevisionResource rsrc)
specifier|private
name|void
name|fireCommentAddedHook
parameter_list|(
name|RevisionResource
name|rsrc
parameter_list|)
block|{
name|IdentifiedUser
name|user
init|=
operator|(
name|IdentifiedUser
operator|)
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
try|try
block|{
name|hooks
operator|.
name|doCommentAddedHook
argument_list|(
name|change
argument_list|,
name|user
operator|.
name|getAccount
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getPatchSet
argument_list|()
argument_list|,
name|message
operator|.
name|getMessage
argument_list|()
argument_list|,
name|categories
argument_list|,
name|db
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"ChangeHook.doCommentAddedHook delivery failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

