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
DECL|package|com.google.gerrit.httpd.rpc.changedetail
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|rpc
operator|.
name|changedetail
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
name|Function
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
name|Optional
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
name|common
operator|.
name|data
operator|.
name|PatchSetDetail
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
name|UiCommandDetail
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
name|errors
operator|.
name|NoSuchEntityException
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
name|webui
operator|.
name|UiAction
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
name|httpd
operator|.
name|rpc
operator|.
name|Handler
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
name|AccountDiffPreference
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
name|AccountDiffPreference
operator|.
name|Whitespace
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
name|AccountPatchReview
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
name|PatchLineCommentsUtil
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
name|ChangesCollection
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
name|gerrit
operator|.
name|server
operator|.
name|change
operator|.
name|Revisions
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
name|edit
operator|.
name|ChangeEdit
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
name|edit
operator|.
name|ChangeEditUtil
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
name|extensions
operator|.
name|webui
operator|.
name|UiActions
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
name|PatchList
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
name|PatchListCache
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
name|PatchListKey
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
name|PatchListNotAvailableException
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
name|patch
operator|.
name|PatchSetInfoNotAvailableException
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
name|gerrit
operator|.
name|server
operator|.
name|project
operator|.
name|NoSuchChangeException
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
name|assistedinject
operator|.
name|Assisted
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
name|lib
operator|.
name|ObjectId
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

begin_comment
comment|/** Creates a {@link PatchSetDetail} from a {@link PatchSet}. */
end_comment

begin_class
DECL|class|PatchSetDetailFactory
class|class
name|PatchSetDetailFactory
extends|extends
name|Handler
argument_list|<
name|PatchSetDetail
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
name|PatchSetDetailFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create ( @ssistedR) @ullable PatchSet.Id psIdBase, @Assisted(R) PatchSet.Id psIdNew, @Nullable AccountDiffPreference diffPrefs)
name|PatchSetDetailFactory
name|create
parameter_list|(
annotation|@
name|Assisted
argument_list|(
literal|"psIdBase"
argument_list|)
annotation|@
name|Nullable
name|PatchSet
operator|.
name|Id
name|psIdBase
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"psIdNew"
argument_list|)
name|PatchSet
operator|.
name|Id
name|psIdNew
parameter_list|,
annotation|@
name|Nullable
name|AccountDiffPreference
name|diffPrefs
parameter_list|)
function_decl|;
block|}
DECL|field|infoFactory
specifier|private
specifier|final
name|PatchSetInfoFactory
name|infoFactory
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|patchListCache
specifier|private
specifier|final
name|PatchListCache
name|patchListCache
decl_stmt|;
DECL|field|userProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
decl_stmt|;
DECL|field|changeControlFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|GenericFactory
name|changeControlFactory
decl_stmt|;
DECL|field|changes
specifier|private
specifier|final
name|ChangesCollection
name|changes
decl_stmt|;
DECL|field|revisions
specifier|private
specifier|final
name|Revisions
name|revisions
decl_stmt|;
DECL|field|plcUtil
specifier|private
specifier|final
name|PatchLineCommentsUtil
name|plcUtil
decl_stmt|;
DECL|field|editUtil
specifier|private
specifier|final
name|ChangeEditUtil
name|editUtil
decl_stmt|;
DECL|field|project
specifier|private
name|Project
operator|.
name|NameKey
name|project
decl_stmt|;
DECL|field|psIdBase
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|psIdBase
decl_stmt|;
DECL|field|psIdNew
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|psIdNew
decl_stmt|;
DECL|field|diffPrefs
specifier|private
specifier|final
name|AccountDiffPreference
name|diffPrefs
decl_stmt|;
DECL|field|oldId
specifier|private
name|ObjectId
name|oldId
decl_stmt|;
DECL|field|newId
specifier|private
name|ObjectId
name|newId
decl_stmt|;
DECL|field|detail
specifier|private
name|PatchSetDetail
name|detail
decl_stmt|;
DECL|field|control
name|ChangeControl
name|control
decl_stmt|;
DECL|field|patchSet
name|PatchSet
name|patchSet
decl_stmt|;
annotation|@
name|Inject
DECL|method|PatchSetDetailFactory (final PatchSetInfoFactory psif, final ReviewDb db, final PatchListCache patchListCache, final Provider<CurrentUser> userProvider, final ChangeControl.GenericFactory changeControlFactory, final ChangesCollection changes, final Revisions revisions, final PatchLineCommentsUtil plcUtil, ChangeEditUtil editUtil, @Assisted(R) @Nullable final PatchSet.Id psIdBase, @Assisted(R) final PatchSet.Id psIdNew, @Assisted @Nullable final AccountDiffPreference diffPrefs)
name|PatchSetDetailFactory
parameter_list|(
specifier|final
name|PatchSetInfoFactory
name|psif
parameter_list|,
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|PatchListCache
name|patchListCache
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
parameter_list|,
specifier|final
name|ChangeControl
operator|.
name|GenericFactory
name|changeControlFactory
parameter_list|,
specifier|final
name|ChangesCollection
name|changes
parameter_list|,
specifier|final
name|Revisions
name|revisions
parameter_list|,
specifier|final
name|PatchLineCommentsUtil
name|plcUtil
parameter_list|,
name|ChangeEditUtil
name|editUtil
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"psIdBase"
argument_list|)
annotation|@
name|Nullable
specifier|final
name|PatchSet
operator|.
name|Id
name|psIdBase
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"psIdNew"
argument_list|)
specifier|final
name|PatchSet
operator|.
name|Id
name|psIdNew
parameter_list|,
annotation|@
name|Assisted
annotation|@
name|Nullable
specifier|final
name|AccountDiffPreference
name|diffPrefs
parameter_list|)
block|{
name|this
operator|.
name|infoFactory
operator|=
name|psif
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|patchListCache
operator|=
name|patchListCache
expr_stmt|;
name|this
operator|.
name|userProvider
operator|=
name|userProvider
expr_stmt|;
name|this
operator|.
name|changeControlFactory
operator|=
name|changeControlFactory
expr_stmt|;
name|this
operator|.
name|changes
operator|=
name|changes
expr_stmt|;
name|this
operator|.
name|revisions
operator|=
name|revisions
expr_stmt|;
name|this
operator|.
name|plcUtil
operator|=
name|plcUtil
expr_stmt|;
name|this
operator|.
name|editUtil
operator|=
name|editUtil
expr_stmt|;
name|this
operator|.
name|psIdBase
operator|=
name|psIdBase
expr_stmt|;
name|this
operator|.
name|psIdNew
operator|=
name|psIdNew
expr_stmt|;
name|this
operator|.
name|diffPrefs
operator|=
name|diffPrefs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|PatchSetDetail
name|call
parameter_list|()
throws|throws
name|OrmException
throws|,
name|NoSuchEntityException
throws|,
name|PatchSetInfoNotAvailableException
throws|,
name|NoSuchChangeException
throws|,
name|AuthException
throws|,
name|IOException
block|{
name|Optional
argument_list|<
name|ChangeEdit
argument_list|>
name|edit
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|control
operator|==
literal|null
operator|||
name|patchSet
operator|==
literal|null
condition|)
block|{
name|control
operator|=
name|changeControlFactory
operator|.
name|validateFor
argument_list|(
name|psIdNew
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|userProvider
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|psIdNew
operator|.
name|get
argument_list|()
operator|==
literal|0
condition|)
block|{
name|Change
name|change
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|psIdNew
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
name|edit
operator|=
name|editUtil
operator|.
name|byChange
argument_list|(
name|change
argument_list|)
expr_stmt|;
if|if
condition|(
name|edit
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|patchSet
operator|=
name|edit
operator|.
name|get
argument_list|()
operator|.
name|getBasePatchSet
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|patchSet
operator|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|psIdNew
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|patchSet
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchEntityException
argument_list|()
throw|;
block|}
block|}
name|project
operator|=
name|control
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
expr_stmt|;
specifier|final
name|PatchList
name|list
decl_stmt|;
try|try
block|{
if|if
condition|(
name|psIdBase
operator|!=
literal|null
condition|)
block|{
name|oldId
operator|=
name|toObjectId
argument_list|(
name|psIdBase
argument_list|)
expr_stmt|;
if|if
condition|(
name|edit
operator|!=
literal|null
operator|&&
name|edit
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|newId
operator|=
name|edit
operator|.
name|get
argument_list|()
operator|.
name|getEditCommit
argument_list|()
operator|.
name|toObjectId
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|newId
operator|=
name|toObjectId
argument_list|(
name|psIdNew
argument_list|)
expr_stmt|;
block|}
name|list
operator|=
name|listFor
argument_list|(
name|keyFor
argument_list|(
name|diffPrefs
operator|.
name|getIgnoreWhitespace
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// OK, means use base to compare
name|list
operator|=
name|patchListCache
operator|.
name|get
argument_list|(
name|control
operator|.
name|getChange
argument_list|()
argument_list|,
name|patchSet
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PatchListNotAvailableException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NoSuchEntityException
argument_list|()
throw|;
block|}
specifier|final
name|List
argument_list|<
name|Patch
argument_list|>
name|patches
init|=
name|list
operator|.
name|toPatchList
argument_list|(
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|Patch
operator|.
name|Key
argument_list|,
name|Patch
argument_list|>
name|byKey
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Patch
name|p
range|:
name|patches
control|)
block|{
name|byKey
operator|.
name|put
argument_list|(
name|p
operator|.
name|getKey
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
name|ChangeNotes
name|notes
init|=
name|control
operator|.
name|getNotes
argument_list|()
decl_stmt|;
if|if
condition|(
name|edit
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|PatchLineComment
name|c
range|:
name|plcUtil
operator|.
name|publishedByPatchSet
argument_list|(
name|db
argument_list|,
name|notes
argument_list|,
name|psIdNew
argument_list|)
control|)
block|{
specifier|final
name|Patch
name|p
init|=
name|byKey
operator|.
name|get
argument_list|(
name|c
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|setCommentCount
argument_list|(
name|p
operator|.
name|getCommentCount
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|detail
operator|=
operator|new
name|PatchSetDetail
argument_list|()
expr_stmt|;
name|detail
operator|.
name|setPatchSet
argument_list|(
name|patchSet
argument_list|)
expr_stmt|;
name|detail
operator|.
name|setProject
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|detail
operator|.
name|setInfo
argument_list|(
name|infoFactory
operator|.
name|get
argument_list|(
name|db
argument_list|,
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|detail
operator|.
name|setPatches
argument_list|(
name|patches
argument_list|)
expr_stmt|;
specifier|final
name|CurrentUser
name|user
init|=
name|control
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
if|if
condition|(
name|user
operator|.
name|isIdentifiedUser
argument_list|()
operator|&&
name|edit
operator|==
literal|null
condition|)
block|{
comment|// If we are signed in, compute the number of draft comments by the
comment|// current user on each of these patch files. This way they can more
comment|// quickly locate where they have pending drafts, and review them.
comment|//
specifier|final
name|Account
operator|.
name|Id
name|me
init|=
operator|(
operator|(
name|IdentifiedUser
operator|)
name|user
operator|)
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchLineComment
name|c
range|:
name|plcUtil
operator|.
name|draftByPatchSetAuthor
argument_list|(
name|db
argument_list|,
name|psIdNew
argument_list|,
name|me
argument_list|,
name|notes
argument_list|)
control|)
block|{
specifier|final
name|Patch
name|p
init|=
name|byKey
operator|.
name|get
argument_list|(
name|c
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|setDraftCount
argument_list|(
name|p
operator|.
name|getDraftCount
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|AccountPatchReview
name|r
range|:
name|db
operator|.
name|accountPatchReviews
argument_list|()
operator|.
name|byReviewer
argument_list|(
name|me
argument_list|,
name|psIdNew
argument_list|)
control|)
block|{
specifier|final
name|Patch
name|p
init|=
name|byKey
operator|.
name|get
argument_list|(
name|r
operator|.
name|getKey
argument_list|()
operator|.
name|getPatchKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|setReviewedByCurrentUser
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|detail
operator|.
name|setCommands
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|UiActions
operator|.
name|sorted
argument_list|(
name|UiActions
operator|.
name|plugins
argument_list|(
name|UiActions
operator|.
name|from
argument_list|(
name|revisions
argument_list|,
operator|new
name|RevisionResource
argument_list|(
name|changes
operator|.
name|parse
argument_list|(
name|control
argument_list|)
argument_list|,
name|patchSet
argument_list|)
argument_list|,
name|Providers
operator|.
name|of
argument_list|(
name|user
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Function
argument_list|<
name|UiAction
operator|.
name|Description
argument_list|,
name|UiCommandDetail
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|UiCommandDetail
name|apply
parameter_list|(
name|UiAction
operator|.
name|Description
name|in
parameter_list|)
block|{
name|UiCommandDetail
name|r
init|=
operator|new
name|UiCommandDetail
argument_list|()
decl_stmt|;
name|r
operator|.
name|method
operator|=
name|in
operator|.
name|getMethod
argument_list|()
expr_stmt|;
name|r
operator|.
name|id
operator|=
name|in
operator|.
name|getId
argument_list|()
expr_stmt|;
name|r
operator|.
name|label
operator|=
name|in
operator|.
name|getLabel
argument_list|()
expr_stmt|;
name|r
operator|.
name|title
operator|=
name|in
operator|.
name|getTitle
argument_list|()
expr_stmt|;
name|r
operator|.
name|enabled
operator|=
name|in
operator|.
name|isEnabled
argument_list|()
expr_stmt|;
return|return
name|r
return|;
block|}
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|detail
return|;
block|}
DECL|method|toObjectId (final PatchSet.Id psId)
specifier|private
name|ObjectId
name|toObjectId
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
throws|throws
name|OrmException
throws|,
name|NoSuchEntityException
block|{
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
name|psId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchEntityException
argument_list|()
throw|;
block|}
try|try
block|{
return|return
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
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Patch set "
operator|+
name|psId
operator|+
literal|" has invalid revision"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NoSuchEntityException
argument_list|()
throw|;
block|}
block|}
DECL|method|keyFor (Whitespace whitespace)
specifier|private
name|PatchListKey
name|keyFor
parameter_list|(
name|Whitespace
name|whitespace
parameter_list|)
block|{
return|return
operator|new
name|PatchListKey
argument_list|(
name|oldId
argument_list|,
name|newId
argument_list|,
name|whitespace
argument_list|)
return|;
block|}
DECL|method|listFor (PatchListKey key)
specifier|private
name|PatchList
name|listFor
parameter_list|(
name|PatchListKey
name|key
parameter_list|)
throws|throws
name|PatchListNotAvailableException
block|{
return|return
name|patchListCache
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|project
argument_list|)
return|;
block|}
block|}
end_class

end_unit

