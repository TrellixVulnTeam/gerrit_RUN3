begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|project
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
name|LabelType
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
name|LabelTypes
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
name|common
operator|.
name|data
operator|.
name|RefConfigSection
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
name|query
operator|.
name|change
operator|.
name|ChangeData
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
name|assistedinject
operator|.
name|AssistedInject
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
name|Collection
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

begin_comment
comment|/** Access control management for a user accessing a single change. */
end_comment

begin_class
DECL|class|ChangeControl
specifier|public
class|class
name|ChangeControl
block|{
DECL|class|GenericFactory
specifier|public
specifier|static
class|class
name|GenericFactory
block|{
DECL|field|projectControl
specifier|private
specifier|final
name|ProjectControl
operator|.
name|GenericFactory
name|projectControl
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
annotation|@
name|Inject
DECL|method|GenericFactory (ProjectControl.GenericFactory p, Provider<ReviewDb> d)
name|GenericFactory
parameter_list|(
name|ProjectControl
operator|.
name|GenericFactory
name|p
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|d
parameter_list|)
block|{
name|projectControl
operator|=
name|p
expr_stmt|;
name|db
operator|=
name|d
expr_stmt|;
block|}
DECL|method|controlFor (Change change, CurrentUser user)
specifier|public
name|ChangeControl
name|controlFor
parameter_list|(
name|Change
name|change
parameter_list|,
name|CurrentUser
name|user
parameter_list|)
throws|throws
name|NoSuchChangeException
block|{
specifier|final
name|Project
operator|.
name|NameKey
name|projectKey
init|=
name|change
operator|.
name|getProject
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|projectControl
operator|.
name|controlFor
argument_list|(
name|projectKey
argument_list|,
name|user
argument_list|)
operator|.
name|controlFor
argument_list|(
name|change
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchProjectException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO: propagate this exception
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|validateFor (Change change, CurrentUser user)
specifier|public
name|ChangeControl
name|validateFor
parameter_list|(
name|Change
name|change
parameter_list|,
name|CurrentUser
name|user
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|OrmException
block|{
name|ChangeControl
name|c
init|=
name|controlFor
argument_list|(
name|change
argument_list|,
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|c
operator|.
name|isVisible
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|c
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|c
return|;
block|}
block|}
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
block|{
DECL|field|projectControl
specifier|private
specifier|final
name|ProjectControl
operator|.
name|Factory
name|projectControl
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
annotation|@
name|Inject
DECL|method|Factory (final ProjectControl.Factory p, final Provider<ReviewDb> d)
name|Factory
parameter_list|(
specifier|final
name|ProjectControl
operator|.
name|Factory
name|p
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|d
parameter_list|)
block|{
name|projectControl
operator|=
name|p
expr_stmt|;
name|db
operator|=
name|d
expr_stmt|;
block|}
DECL|method|controlFor (final Change.Id id)
specifier|public
name|ChangeControl
name|controlFor
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|id
parameter_list|)
throws|throws
name|NoSuchChangeException
block|{
specifier|final
name|Change
name|change
decl_stmt|;
try|try
block|{
name|change
operator|=
name|db
operator|.
name|get
argument_list|()
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|change
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|id
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|id
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|controlFor
argument_list|(
name|change
argument_list|)
return|;
block|}
DECL|method|controlFor (final Change change)
specifier|public
name|ChangeControl
name|controlFor
parameter_list|(
specifier|final
name|Change
name|change
parameter_list|)
throws|throws
name|NoSuchChangeException
block|{
try|try
block|{
specifier|final
name|Project
operator|.
name|NameKey
name|projectKey
init|=
name|change
operator|.
name|getProject
argument_list|()
decl_stmt|;
return|return
name|projectControl
operator|.
name|validateFor
argument_list|(
name|projectKey
argument_list|)
operator|.
name|controlFor
argument_list|(
name|change
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchProjectException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|validateFor (final Change.Id id)
specifier|public
name|ChangeControl
name|validateFor
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|id
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|OrmException
block|{
return|return
name|validate
argument_list|(
name|controlFor
argument_list|(
name|id
argument_list|)
argument_list|,
name|db
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|method|validateFor (final Change change)
specifier|public
name|ChangeControl
name|validateFor
parameter_list|(
specifier|final
name|Change
name|change
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|OrmException
block|{
return|return
name|validate
argument_list|(
name|controlFor
argument_list|(
name|change
argument_list|)
argument_list|,
name|db
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|method|validate (final ChangeControl c, final ReviewDb db)
specifier|private
specifier|static
name|ChangeControl
name|validate
parameter_list|(
specifier|final
name|ChangeControl
name|c
parameter_list|,
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|OrmException
block|{
if|if
condition|(
operator|!
name|c
operator|.
name|isVisible
argument_list|(
name|db
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|c
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|c
return|;
block|}
block|}
DECL|interface|AssistedFactory
specifier|public
interface|interface
name|AssistedFactory
block|{
DECL|method|create (RefControl refControl, Change change)
name|ChangeControl
name|create
parameter_list|(
name|RefControl
name|refControl
parameter_list|,
name|Change
name|change
parameter_list|)
function_decl|;
DECL|method|create (RefControl refControl, ChangeNotes notes)
name|ChangeControl
name|create
parameter_list|(
name|RefControl
name|refControl
parameter_list|,
name|ChangeNotes
name|notes
parameter_list|)
function_decl|;
block|}
DECL|field|changeDataFactory
specifier|private
specifier|final
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
decl_stmt|;
DECL|field|refControl
specifier|private
specifier|final
name|RefControl
name|refControl
decl_stmt|;
DECL|field|notes
specifier|private
specifier|final
name|ChangeNotes
name|notes
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|ChangeControl ( ChangeData.Factory changeDataFactory, ChangeNotes.Factory notesFactory, @Assisted RefControl refControl, @Assisted Change change)
name|ChangeControl
parameter_list|(
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|,
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
parameter_list|,
annotation|@
name|Assisted
name|RefControl
name|refControl
parameter_list|,
annotation|@
name|Assisted
name|Change
name|change
parameter_list|)
block|{
name|this
argument_list|(
name|changeDataFactory
argument_list|,
name|refControl
argument_list|,
name|notesFactory
operator|.
name|create
argument_list|(
name|change
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AssistedInject
DECL|method|ChangeControl ( ChangeData.Factory changeDataFactory, @Assisted RefControl refControl, @Assisted ChangeNotes notes)
name|ChangeControl
parameter_list|(
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|,
annotation|@
name|Assisted
name|RefControl
name|refControl
parameter_list|,
annotation|@
name|Assisted
name|ChangeNotes
name|notes
parameter_list|)
block|{
name|this
operator|.
name|changeDataFactory
operator|=
name|changeDataFactory
expr_stmt|;
name|this
operator|.
name|refControl
operator|=
name|refControl
expr_stmt|;
name|this
operator|.
name|notes
operator|=
name|notes
expr_stmt|;
block|}
DECL|method|forUser (final CurrentUser who)
specifier|public
name|ChangeControl
name|forUser
parameter_list|(
specifier|final
name|CurrentUser
name|who
parameter_list|)
block|{
if|if
condition|(
name|getCurrentUser
argument_list|()
operator|.
name|equals
argument_list|(
name|who
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|ChangeControl
argument_list|(
name|changeDataFactory
argument_list|,
name|getRefControl
argument_list|()
operator|.
name|forUser
argument_list|(
name|who
argument_list|)
argument_list|,
name|notes
argument_list|)
return|;
block|}
DECL|method|getRefControl ()
specifier|public
name|RefControl
name|getRefControl
parameter_list|()
block|{
return|return
name|refControl
return|;
block|}
DECL|method|getCurrentUser ()
specifier|public
name|CurrentUser
name|getCurrentUser
parameter_list|()
block|{
return|return
name|getRefControl
argument_list|()
operator|.
name|getCurrentUser
argument_list|()
return|;
block|}
DECL|method|getProjectControl ()
specifier|public
name|ProjectControl
name|getProjectControl
parameter_list|()
block|{
return|return
name|getRefControl
argument_list|()
operator|.
name|getProjectControl
argument_list|()
return|;
block|}
DECL|method|getProject ()
specifier|public
name|Project
name|getProject
parameter_list|()
block|{
return|return
name|getProjectControl
argument_list|()
operator|.
name|getProject
argument_list|()
return|;
block|}
DECL|method|getChange ()
specifier|public
name|Change
name|getChange
parameter_list|()
block|{
return|return
name|notes
operator|.
name|getChange
argument_list|()
return|;
block|}
DECL|method|getNotes ()
specifier|public
name|ChangeNotes
name|getNotes
parameter_list|()
block|{
return|return
name|notes
return|;
block|}
comment|/** Can this user see this change? */
DECL|method|isVisible (ReviewDb db)
specifier|public
name|boolean
name|isVisible
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|==
name|Change
operator|.
name|Status
operator|.
name|DRAFT
operator|&&
operator|!
name|isDraftVisible
argument_list|(
name|db
argument_list|,
literal|null
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|isRefVisible
argument_list|()
return|;
block|}
comment|/** Can the user see this change? Does not account for draft status */
DECL|method|isRefVisible ()
specifier|public
name|boolean
name|isRefVisible
parameter_list|()
block|{
return|return
name|getRefControl
argument_list|()
operator|.
name|isVisible
argument_list|()
return|;
block|}
comment|/** Can this user see the given patchset? */
DECL|method|isPatchVisible (PatchSet ps, ReviewDb db)
specifier|public
name|boolean
name|isPatchVisible
parameter_list|(
name|PatchSet
name|ps
parameter_list|,
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|ps
operator|!=
literal|null
operator|&&
name|ps
operator|.
name|isDraft
argument_list|()
operator|&&
operator|!
name|isDraftVisible
argument_list|(
name|db
argument_list|,
literal|null
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|isVisible
argument_list|(
name|db
argument_list|)
return|;
block|}
comment|/** Can this user abandon this change? */
DECL|method|canAbandon ()
specifier|public
name|boolean
name|canAbandon
parameter_list|()
block|{
return|return
name|isOwner
argument_list|()
comment|// owner (aka creator) of the change can abandon
operator|||
name|getRefControl
argument_list|()
operator|.
name|isOwner
argument_list|()
comment|// branch owner can abandon
operator|||
name|getProjectControl
argument_list|()
operator|.
name|isOwner
argument_list|()
comment|// project owner can abandon
operator|||
name|getCurrentUser
argument_list|()
operator|.
name|getCapabilities
argument_list|()
operator|.
name|canAdministrateServer
argument_list|()
comment|// site administers are god
operator|||
name|getRefControl
argument_list|()
operator|.
name|canAbandon
argument_list|()
comment|// user can abandon a specific ref
return|;
block|}
comment|/** Can this user publish this draft change or any draft patch set of this change? */
DECL|method|canPublish (final ReviewDb db)
specifier|public
name|boolean
name|canPublish
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
operator|(
name|isOwner
argument_list|()
operator|||
name|getRefControl
argument_list|()
operator|.
name|canPublishDrafts
argument_list|()
operator|)
operator|&&
name|isVisible
argument_list|(
name|db
argument_list|)
return|;
block|}
comment|/** Can this user delete this draft change or any draft patch set of this change? */
DECL|method|canDeleteDraft (final ReviewDb db)
specifier|public
name|boolean
name|canDeleteDraft
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
operator|(
name|isOwner
argument_list|()
operator|||
name|getRefControl
argument_list|()
operator|.
name|canDeleteDrafts
argument_list|()
operator|)
operator|&&
name|isVisible
argument_list|(
name|db
argument_list|)
return|;
block|}
comment|/** Can this user rebase this change? */
DECL|method|canRebase ()
specifier|public
name|boolean
name|canRebase
parameter_list|()
block|{
return|return
name|isOwner
argument_list|()
operator|||
name|getRefControl
argument_list|()
operator|.
name|canSubmit
argument_list|()
operator|||
name|getRefControl
argument_list|()
operator|.
name|canRebase
argument_list|()
return|;
block|}
comment|/** Can this user restore this change? */
DECL|method|canRestore ()
specifier|public
name|boolean
name|canRestore
parameter_list|()
block|{
return|return
name|canAbandon
argument_list|()
comment|// Anyone who can abandon the change can restore it back
operator|&&
name|getRefControl
argument_list|()
operator|.
name|canUpload
argument_list|()
return|;
comment|// as long as you can upload too
block|}
comment|/** All available label types for this change. */
DECL|method|getLabelTypes ()
specifier|public
name|LabelTypes
name|getLabelTypes
parameter_list|()
block|{
name|String
name|destBranch
init|=
name|getChange
argument_list|()
operator|.
name|getDest
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|LabelType
argument_list|>
name|all
init|=
name|getProjectControl
argument_list|()
operator|.
name|getLabelTypes
argument_list|()
operator|.
name|getLabelTypes
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|LabelType
argument_list|>
name|r
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|all
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|LabelType
name|l
range|:
name|all
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|refs
init|=
name|l
operator|.
name|getRefPatterns
argument_list|()
decl_stmt|;
if|if
condition|(
name|refs
operator|==
literal|null
condition|)
block|{
name|r
operator|.
name|add
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|refPattern
range|:
name|refs
control|)
block|{
if|if
condition|(
name|RefConfigSection
operator|.
name|isValid
argument_list|(
name|refPattern
argument_list|)
operator|&&
name|match
argument_list|(
name|destBranch
argument_list|,
name|refPattern
argument_list|)
condition|)
block|{
name|r
operator|.
name|add
argument_list|(
name|l
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
return|return
operator|new
name|LabelTypes
argument_list|(
name|r
argument_list|)
return|;
block|}
comment|/** All value ranges of any allowed label permission. */
DECL|method|getLabelRanges ()
specifier|public
name|List
argument_list|<
name|PermissionRange
argument_list|>
name|getLabelRanges
parameter_list|()
block|{
return|return
name|getRefControl
argument_list|()
operator|.
name|getLabelRanges
argument_list|(
name|isOwner
argument_list|()
argument_list|)
return|;
block|}
comment|/** The range of permitted values associated with a label permission. */
DECL|method|getRange (String permission)
specifier|public
name|PermissionRange
name|getRange
parameter_list|(
name|String
name|permission
parameter_list|)
block|{
return|return
name|getRefControl
argument_list|()
operator|.
name|getRange
argument_list|(
name|permission
argument_list|,
name|isOwner
argument_list|()
argument_list|)
return|;
block|}
comment|/** Can this user add a patch set to this change? */
DECL|method|canAddPatchSet ()
specifier|public
name|boolean
name|canAddPatchSet
parameter_list|()
block|{
return|return
name|getRefControl
argument_list|()
operator|.
name|canUpload
argument_list|()
return|;
block|}
comment|/** Is this user the owner of the change? */
DECL|method|isOwner ()
specifier|public
name|boolean
name|isOwner
parameter_list|()
block|{
if|if
condition|(
name|getCurrentUser
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
specifier|final
name|IdentifiedUser
name|i
init|=
operator|(
name|IdentifiedUser
operator|)
name|getCurrentUser
argument_list|()
decl_stmt|;
return|return
name|i
operator|.
name|getAccountId
argument_list|()
operator|.
name|equals
argument_list|(
name|getChange
argument_list|()
operator|.
name|getOwner
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** Is this user a reviewer for the change? */
DECL|method|isReviewer (ReviewDb db)
specifier|public
name|boolean
name|isReviewer
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|isReviewer
argument_list|(
name|db
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** Is this user a reviewer for the change? */
DECL|method|isReviewer (ReviewDb db, @Nullable ChangeData cd)
specifier|public
name|boolean
name|isReviewer
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
annotation|@
name|Nullable
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|getCurrentUser
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
name|Collection
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|results
init|=
name|changeData
argument_list|(
name|db
argument_list|,
name|cd
argument_list|)
operator|.
name|reviewers
argument_list|()
operator|.
name|values
argument_list|()
decl_stmt|;
name|IdentifiedUser
name|user
init|=
operator|(
name|IdentifiedUser
operator|)
name|getCurrentUser
argument_list|()
decl_stmt|;
return|return
name|results
operator|.
name|contains
argument_list|(
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** @return true if the user is allowed to remove this reviewer. */
DECL|method|canRemoveReviewer (PatchSetApproval approval)
specifier|public
name|boolean
name|canRemoveReviewer
parameter_list|(
name|PatchSetApproval
name|approval
parameter_list|)
block|{
return|return
name|canRemoveReviewer
argument_list|(
name|approval
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|approval
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|canRemoveReviewer (Account.Id reviewer, int value)
specifier|public
name|boolean
name|canRemoveReviewer
parameter_list|(
name|Account
operator|.
name|Id
name|reviewer
parameter_list|,
name|int
name|value
parameter_list|)
block|{
if|if
condition|(
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
comment|// A user can always remove themselves.
comment|//
if|if
condition|(
name|getCurrentUser
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
specifier|final
name|IdentifiedUser
name|i
init|=
operator|(
name|IdentifiedUser
operator|)
name|getCurrentUser
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|.
name|getAccountId
argument_list|()
operator|.
name|equals
argument_list|(
name|reviewer
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
comment|// can remove self
block|}
block|}
comment|// The change owner may remove any zero or positive score.
comment|//
if|if
condition|(
name|isOwner
argument_list|()
operator|&&
literal|0
operator|<=
name|value
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// Users with the remove reviewer permission, the branch owner, project
comment|// owner and site admin can remove anyone
if|if
condition|(
name|getRefControl
argument_list|()
operator|.
name|canRemoveReviewer
argument_list|()
comment|// has removal permissions
operator|||
name|getRefControl
argument_list|()
operator|.
name|isOwner
argument_list|()
comment|// branch owner
operator|||
name|getProjectControl
argument_list|()
operator|.
name|isOwner
argument_list|()
comment|// project owner
operator|||
name|getCurrentUser
argument_list|()
operator|.
name|getCapabilities
argument_list|()
operator|.
name|canAdministrateServer
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/** Can this user edit the topic name? */
DECL|method|canEditTopicName ()
specifier|public
name|boolean
name|canEditTopicName
parameter_list|()
block|{
if|if
condition|(
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
return|return
name|isOwner
argument_list|()
comment|// owner (aka creator) of the change can edit topic
operator|||
name|getRefControl
argument_list|()
operator|.
name|isOwner
argument_list|()
comment|// branch owner can edit topic
operator|||
name|getProjectControl
argument_list|()
operator|.
name|isOwner
argument_list|()
comment|// project owner can edit topic
operator|||
name|getCurrentUser
argument_list|()
operator|.
name|getCapabilities
argument_list|()
operator|.
name|canAdministrateServer
argument_list|()
comment|// site administers are god
operator|||
name|getRefControl
argument_list|()
operator|.
name|canEditTopicName
argument_list|()
comment|// user can edit topic on a specific ref
return|;
block|}
else|else
block|{
return|return
name|getRefControl
argument_list|()
operator|.
name|canForceEditTopicName
argument_list|()
return|;
block|}
block|}
comment|/** Can this user edit the hashtag name? */
DECL|method|canEditHashtags ()
specifier|public
name|boolean
name|canEditHashtags
parameter_list|()
block|{
return|return
name|isOwner
argument_list|()
comment|// owner (aka creator) of the change can edit hashtags
operator|||
name|getRefControl
argument_list|()
operator|.
name|isOwner
argument_list|()
comment|// branch owner can edit hashtags
operator|||
name|getProjectControl
argument_list|()
operator|.
name|isOwner
argument_list|()
comment|// project owner can edit hashtags
operator|||
name|getCurrentUser
argument_list|()
operator|.
name|getCapabilities
argument_list|()
operator|.
name|canAdministrateServer
argument_list|()
comment|// site administers are god
operator|||
name|getRefControl
argument_list|()
operator|.
name|canEditHashtags
argument_list|()
return|;
comment|// user can edit hashtag on a specific ref
block|}
DECL|method|canSubmit ()
specifier|public
name|boolean
name|canSubmit
parameter_list|()
block|{
return|return
name|getRefControl
argument_list|()
operator|.
name|canSubmit
argument_list|()
return|;
block|}
DECL|method|canSubmitAs ()
specifier|public
name|boolean
name|canSubmitAs
parameter_list|()
block|{
return|return
name|getRefControl
argument_list|()
operator|.
name|canSubmitAs
argument_list|()
return|;
block|}
DECL|method|match (String destBranch, String refPattern)
specifier|private
name|boolean
name|match
parameter_list|(
name|String
name|destBranch
parameter_list|,
name|String
name|refPattern
parameter_list|)
block|{
return|return
name|RefPatternMatcher
operator|.
name|getMatcher
argument_list|(
name|refPattern
argument_list|)
operator|.
name|match
argument_list|(
name|destBranch
argument_list|,
name|this
operator|.
name|getRefControl
argument_list|()
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|changeData (ReviewDb db, @Nullable ChangeData cd)
specifier|private
name|ChangeData
name|changeData
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
annotation|@
name|Nullable
name|ChangeData
name|cd
parameter_list|)
block|{
return|return
name|cd
operator|!=
literal|null
condition|?
name|cd
else|:
name|changeDataFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|this
argument_list|)
return|;
block|}
DECL|method|isDraftVisible (ReviewDb db, ChangeData cd)
specifier|public
name|boolean
name|isDraftVisible
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|OrmException
block|{
return|return
name|isOwner
argument_list|()
operator|||
name|isReviewer
argument_list|(
name|db
argument_list|,
name|cd
argument_list|)
operator|||
name|getRefControl
argument_list|()
operator|.
name|canViewDrafts
argument_list|()
return|;
block|}
block|}
end_class

end_unit

