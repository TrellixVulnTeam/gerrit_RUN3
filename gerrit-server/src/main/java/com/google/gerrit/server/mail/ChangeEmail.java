begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.mail
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|mail
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
name|reviewdb
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
name|AccountGroup
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
name|AccountProjectWatch
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
name|PatchSetInfo
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
name|StarredChange
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
name|PatchListEntry
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
name|ProjectState
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
name|Predicate
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
name|QueryParseException
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
name|gerrit
operator|.
name|server
operator|.
name|query
operator|.
name|change
operator|.
name|ChangeQueryBuilder
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
name|client
operator|.
name|OrmException
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_comment
comment|/** Sends an email to one or more interested parties. */
end_comment

begin_class
DECL|class|ChangeEmail
specifier|public
specifier|abstract
class|class
name|ChangeEmail
extends|extends
name|OutgoingEmail
block|{
DECL|field|change
specifier|protected
specifier|final
name|Change
name|change
decl_stmt|;
DECL|field|projectName
specifier|protected
name|String
name|projectName
decl_stmt|;
DECL|field|patchSet
specifier|protected
name|PatchSet
name|patchSet
decl_stmt|;
DECL|field|patchSetInfo
specifier|protected
name|PatchSetInfo
name|patchSetInfo
decl_stmt|;
DECL|field|changeMessage
specifier|protected
name|ChangeMessage
name|changeMessage
decl_stmt|;
DECL|field|projectState
specifier|private
name|ProjectState
name|projectState
decl_stmt|;
DECL|field|changeData
specifier|protected
name|ChangeData
name|changeData
decl_stmt|;
DECL|method|ChangeEmail (EmailArguments ea, final Change c, final String mc)
specifier|protected
name|ChangeEmail
parameter_list|(
name|EmailArguments
name|ea
parameter_list|,
specifier|final
name|Change
name|c
parameter_list|,
specifier|final
name|String
name|mc
parameter_list|)
block|{
name|super
argument_list|(
name|ea
argument_list|,
name|mc
argument_list|)
expr_stmt|;
name|change
operator|=
name|c
expr_stmt|;
name|changeData
operator|=
name|change
operator|!=
literal|null
condition|?
operator|new
name|ChangeData
argument_list|(
name|change
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
DECL|method|setPatchSet (final PatchSet ps)
specifier|public
name|void
name|setPatchSet
parameter_list|(
specifier|final
name|PatchSet
name|ps
parameter_list|)
block|{
name|patchSet
operator|=
name|ps
expr_stmt|;
block|}
DECL|method|setPatchSet (final PatchSet ps, final PatchSetInfo psi)
specifier|public
name|void
name|setPatchSet
parameter_list|(
specifier|final
name|PatchSet
name|ps
parameter_list|,
specifier|final
name|PatchSetInfo
name|psi
parameter_list|)
block|{
name|patchSet
operator|=
name|ps
expr_stmt|;
name|patchSetInfo
operator|=
name|psi
expr_stmt|;
block|}
DECL|method|setChangeMessage (final ChangeMessage cm)
specifier|public
name|void
name|setChangeMessage
parameter_list|(
specifier|final
name|ChangeMessage
name|cm
parameter_list|)
block|{
name|changeMessage
operator|=
name|cm
expr_stmt|;
block|}
comment|/** Format the message body by calling {@link #appendText(String)}. */
DECL|method|format ()
specifier|protected
name|void
name|format
parameter_list|()
throws|throws
name|EmailException
block|{
name|formatChange
argument_list|()
expr_stmt|;
name|appendText
argument_list|(
name|velocifyFile
argument_list|(
literal|"ChangeFooter.vm"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|HashSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|reviewers
init|=
operator|new
name|HashSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|p
range|:
name|args
operator|.
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byChange
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|)
control|)
block|{
name|reviewers
operator|.
name|add
argument_list|(
name|p
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|TreeSet
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|who
range|:
name|reviewers
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|getNameEmailFor
argument_list|(
name|who
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|appendText
argument_list|(
literal|"Gerrit-Reviewer: "
operator|+
name|name
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{     }
block|}
comment|/** Format the message body by calling {@link #appendText(String)}. */
DECL|method|formatChange ()
specifier|protected
specifier|abstract
name|void
name|formatChange
parameter_list|()
throws|throws
name|EmailException
function_decl|;
comment|/** Setup the message headers and envelope (TO, CC, BCC). */
DECL|method|init ()
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|EmailException
block|{
if|if
condition|(
name|args
operator|.
name|projectCache
operator|!=
literal|null
condition|)
block|{
name|projectState
operator|=
name|args
operator|.
name|projectCache
operator|.
name|get
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
name|projectName
operator|=
name|projectState
operator|!=
literal|null
condition|?
name|projectState
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
else|:
literal|null
expr_stmt|;
block|}
else|else
block|{
name|projectState
operator|=
literal|null
expr_stmt|;
name|projectName
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|patchSet
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|patchSet
operator|=
name|args
operator|.
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|err
parameter_list|)
block|{
name|patchSet
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|patchSet
operator|!=
literal|null
operator|&&
name|patchSetInfo
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|patchSetInfo
operator|=
name|args
operator|.
name|patchSetInfoFactory
operator|.
name|get
argument_list|(
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PatchSetInfoNotAvailableException
name|err
parameter_list|)
block|{
name|patchSetInfo
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
if|if
condition|(
name|changeMessage
operator|!=
literal|null
operator|&&
name|changeMessage
operator|.
name|getWrittenOn
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|setHeader
argument_list|(
literal|"Date"
argument_list|,
operator|new
name|Date
argument_list|(
name|changeMessage
operator|.
name|getWrittenOn
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|setChangeSubjectHeader
argument_list|()
expr_stmt|;
name|setHeader
argument_list|(
literal|"X-Gerrit-Change-Id"
argument_list|,
literal|""
operator|+
name|change
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|setListIdHeader
argument_list|()
expr_stmt|;
name|setChangeUrlHeader
argument_list|()
expr_stmt|;
name|setCommitIdHeader
argument_list|()
expr_stmt|;
block|}
DECL|method|setListIdHeader ()
specifier|private
name|void
name|setListIdHeader
parameter_list|()
block|{
comment|// Set a reasonable list id so that filters can be used to sort messages
comment|//
specifier|final
name|StringBuilder
name|listid
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|listid
operator|.
name|append
argument_list|(
literal|"gerrit-"
argument_list|)
expr_stmt|;
name|listid
operator|.
name|append
argument_list|(
name|projectName
operator|.
name|replace
argument_list|(
literal|'/'
argument_list|,
literal|'-'
argument_list|)
argument_list|)
expr_stmt|;
name|listid
operator|.
name|append
argument_list|(
literal|"@"
argument_list|)
expr_stmt|;
name|listid
operator|.
name|append
argument_list|(
name|getGerritHost
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|listidStr
init|=
name|listid
operator|.
name|toString
argument_list|()
decl_stmt|;
name|setHeader
argument_list|(
literal|"Mailing-List"
argument_list|,
literal|"list "
operator|+
name|listidStr
argument_list|)
expr_stmt|;
name|setHeader
argument_list|(
literal|"List-Id"
argument_list|,
literal|"<"
operator|+
name|listidStr
operator|.
name|replace
argument_list|(
literal|'@'
argument_list|,
literal|'.'
argument_list|)
operator|+
literal|">"
argument_list|)
expr_stmt|;
if|if
condition|(
name|getSettingsUrl
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|setHeader
argument_list|(
literal|"List-Unsubscribe"
argument_list|,
literal|"<"
operator|+
name|getSettingsUrl
argument_list|()
operator|+
literal|">"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setChangeUrlHeader ()
specifier|private
name|void
name|setChangeUrlHeader
parameter_list|()
block|{
specifier|final
name|String
name|u
init|=
name|getChangeUrl
argument_list|()
decl_stmt|;
if|if
condition|(
name|u
operator|!=
literal|null
condition|)
block|{
name|setHeader
argument_list|(
literal|"X-Gerrit-ChangeURL"
argument_list|,
literal|"<"
operator|+
name|u
operator|+
literal|">"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setCommitIdHeader ()
specifier|private
name|void
name|setCommitIdHeader
parameter_list|()
block|{
if|if
condition|(
name|patchSet
operator|!=
literal|null
operator|&&
name|patchSet
operator|.
name|getRevision
argument_list|()
operator|!=
literal|null
operator|&&
name|patchSet
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
operator|!=
literal|null
operator|&&
name|patchSet
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|setHeader
argument_list|(
literal|"X-Gerrit-Commit"
argument_list|,
name|patchSet
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setChangeSubjectHeader ()
specifier|private
name|void
name|setChangeSubjectHeader
parameter_list|()
throws|throws
name|EmailException
block|{
name|setHeader
argument_list|(
literal|"Subject"
argument_list|,
name|velocifyFile
argument_list|(
literal|"ChangeSubject.vm"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Get a link to the change; null if the server doesn't know its own address. */
DECL|method|getChangeUrl ()
specifier|public
name|String
name|getChangeUrl
parameter_list|()
block|{
if|if
condition|(
name|change
operator|!=
literal|null
operator|&&
name|getGerritUrl
argument_list|()
operator|!=
literal|null
condition|)
block|{
specifier|final
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
name|getGerritUrl
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getChangeMessageThreadId ()
specifier|protected
name|String
name|getChangeMessageThreadId
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"gerrit"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|change
operator|.
name|getCreatedOn
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|change
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|'@'
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|getGerritHost
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Format the sender's "cover letter", {@link #getCoverLetter()}. */
DECL|method|formatCoverLetter ()
specifier|protected
name|void
name|formatCoverLetter
parameter_list|()
block|{
specifier|final
name|String
name|cover
init|=
name|getCoverLetter
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|cover
argument_list|)
condition|)
block|{
name|appendText
argument_list|(
name|cover
argument_list|)
expr_stmt|;
name|appendText
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Get the text of the "cover letter", from {@link ChangeMessage}. */
DECL|method|getCoverLetter ()
specifier|public
name|String
name|getCoverLetter
parameter_list|()
block|{
if|if
condition|(
name|changeMessage
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|txt
init|=
name|changeMessage
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|txt
operator|!=
literal|null
condition|)
block|{
return|return
name|txt
operator|.
name|trim
argument_list|()
return|;
block|}
block|}
return|return
literal|""
return|;
block|}
comment|/** Format the change message and the affected file list. */
DECL|method|formatChangeDetail ()
specifier|protected
name|void
name|formatChangeDetail
parameter_list|()
block|{
name|appendText
argument_list|(
name|getChangeDetail
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Create the change message and the affected file list. */
DECL|method|getChangeDetail ()
specifier|public
name|String
name|getChangeDetail
parameter_list|()
block|{
name|StringBuilder
name|detail
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|patchSetInfo
operator|!=
literal|null
condition|)
block|{
name|detail
operator|.
name|append
argument_list|(
name|patchSetInfo
operator|.
name|getMessage
argument_list|()
operator|.
name|trim
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|detail
operator|.
name|append
argument_list|(
name|change
operator|.
name|getSubject
argument_list|()
operator|.
name|trim
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|patchSet
operator|!=
literal|null
condition|)
block|{
name|detail
operator|.
name|append
argument_list|(
literal|"---\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|PatchListEntry
name|p
range|:
name|getPatchList
argument_list|()
operator|.
name|getPatches
argument_list|()
control|)
block|{
name|detail
operator|.
name|append
argument_list|(
name|p
operator|.
name|getChangeType
argument_list|()
operator|.
name|getCode
argument_list|()
operator|+
literal|" "
operator|+
name|p
operator|.
name|getNewName
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|detail
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|detail
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Get the patch list corresponding to this patch set. */
DECL|method|getPatchList ()
specifier|protected
name|PatchList
name|getPatchList
parameter_list|()
block|{
if|if
condition|(
name|patchSet
operator|!=
literal|null
condition|)
block|{
return|return
name|args
operator|.
name|patchListCache
operator|.
name|get
argument_list|(
name|change
argument_list|,
name|patchSet
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/** Get the project entity the change is in; null if its been deleted. */
DECL|method|getProjectState ()
specifier|protected
name|ProjectState
name|getProjectState
parameter_list|()
block|{
return|return
name|projectState
return|;
block|}
comment|/** Get the groups which own the project. */
DECL|method|getProjectOwners ()
specifier|protected
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|getProjectOwners
parameter_list|()
block|{
specifier|final
name|ProjectState
name|r
decl_stmt|;
name|r
operator|=
name|args
operator|.
name|projectCache
operator|.
name|get
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|r
operator|!=
literal|null
condition|?
name|r
operator|.
name|getOwners
argument_list|()
else|:
name|Collections
operator|.
expr|<
name|AccountGroup
operator|.
name|Id
operator|>
name|emptySet
argument_list|()
return|;
block|}
comment|/** TO or CC all vested parties (change owner, patch set uploader, author). */
DECL|method|rcptToAuthors (final RecipientType rt)
specifier|protected
name|void
name|rcptToAuthors
parameter_list|(
specifier|final
name|RecipientType
name|rt
parameter_list|)
block|{
name|add
argument_list|(
name|rt
argument_list|,
name|change
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|patchSet
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|rt
argument_list|,
name|patchSet
operator|.
name|getUploader
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|patchSetInfo
operator|!=
literal|null
condition|)
block|{
name|add
argument_list|(
name|rt
argument_list|,
name|patchSetInfo
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|rt
argument_list|,
name|patchSetInfo
operator|.
name|getCommitter
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** BCC any user who has starred this change. */
DECL|method|bccStarredBy ()
specifier|protected
name|void
name|bccStarredBy
parameter_list|()
block|{
try|try
block|{
comment|// BCC anyone who has starred this change.
comment|//
for|for
control|(
name|StarredChange
name|w
range|:
name|args
operator|.
name|db
operator|.
name|get
argument_list|()
operator|.
name|starredChanges
argument_list|()
operator|.
name|byChange
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|)
control|)
block|{
name|add
argument_list|(
name|RecipientType
operator|.
name|BCC
argument_list|,
name|w
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|err
parameter_list|)
block|{
comment|// Just don't BCC everyone. Better to send a partial message to those
comment|// we already have queued up then to fail deliver entirely to people
comment|// who have a lower interest in the change.
block|}
block|}
comment|/** BCC any user who has set "notify all comments" on this project. */
DECL|method|bccWatchesNotifyAllComments ()
specifier|protected
name|void
name|bccWatchesNotifyAllComments
parameter_list|()
block|{
try|try
block|{
comment|// BCC anyone else who has interest in this project's changes
comment|//
for|for
control|(
specifier|final
name|AccountProjectWatch
name|w
range|:
name|getWatches
argument_list|()
control|)
block|{
if|if
condition|(
name|w
operator|.
name|isNotifyAllComments
argument_list|()
condition|)
block|{
name|add
argument_list|(
name|RecipientType
operator|.
name|BCC
argument_list|,
name|w
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|err
parameter_list|)
block|{
comment|// Just don't CC everyone. Better to send a partial message to those
comment|// we already have queued up then to fail deliver entirely to people
comment|// who have a lower interest in the change.
block|}
block|}
comment|/** Returns all watches that are relevant */
DECL|method|getWatches ()
specifier|protected
specifier|final
name|List
argument_list|<
name|AccountProjectWatch
argument_list|>
name|getWatches
parameter_list|()
throws|throws
name|OrmException
block|{
if|if
condition|(
name|changeData
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|AccountProjectWatch
argument_list|>
name|matching
init|=
operator|new
name|ArrayList
argument_list|<
name|AccountProjectWatch
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|projectWatchers
init|=
operator|new
name|HashSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountProjectWatch
name|w
range|:
name|args
operator|.
name|db
operator|.
name|get
argument_list|()
operator|.
name|accountProjectWatches
argument_list|()
operator|.
name|byProject
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
control|)
block|{
name|projectWatchers
operator|.
name|add
argument_list|(
name|w
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|matching
argument_list|,
name|w
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|AccountProjectWatch
name|w
range|:
name|args
operator|.
name|db
operator|.
name|get
argument_list|()
operator|.
name|accountProjectWatches
argument_list|()
operator|.
name|byProject
argument_list|(
name|args
operator|.
name|wildProject
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|projectWatchers
operator|.
name|contains
argument_list|(
name|w
operator|.
name|getAccountId
argument_list|()
argument_list|)
condition|)
block|{
name|add
argument_list|(
name|matching
argument_list|,
name|w
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|matching
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|add (List<AccountProjectWatch> matching, AccountProjectWatch w)
specifier|private
name|void
name|add
parameter_list|(
name|List
argument_list|<
name|AccountProjectWatch
argument_list|>
name|matching
parameter_list|,
name|AccountProjectWatch
name|w
parameter_list|)
throws|throws
name|OrmException
block|{
name|IdentifiedUser
name|user
init|=
name|args
operator|.
name|identifiedUserFactory
operator|.
name|create
argument_list|(
name|args
operator|.
name|db
argument_list|,
name|w
operator|.
name|getAccountId
argument_list|()
argument_list|)
decl_stmt|;
name|ChangeQueryBuilder
name|qb
init|=
name|args
operator|.
name|queryBuilder
operator|.
name|create
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|Predicate
argument_list|<
name|ChangeData
argument_list|>
name|p
init|=
name|qb
operator|.
name|is_visible
argument_list|()
decl_stmt|;
if|if
condition|(
name|w
operator|.
name|getFilter
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|qb
operator|.
name|setAllowFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|p
operator|=
name|Predicate
operator|.
name|and
argument_list|(
name|qb
operator|.
name|parse
argument_list|(
name|w
operator|.
name|getFilter
argument_list|()
argument_list|)
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|p
operator|=
name|args
operator|.
name|queryRewriter
operator|.
name|get
argument_list|()
operator|.
name|rewrite
argument_list|(
name|p
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|match
argument_list|(
name|changeData
argument_list|)
condition|)
block|{
name|matching
operator|.
name|add
argument_list|(
name|w
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|QueryParseException
name|e
parameter_list|)
block|{
comment|// Ignore broken filter expressions.
block|}
block|}
elseif|else
if|if
condition|(
name|p
operator|.
name|match
argument_list|(
name|changeData
argument_list|)
condition|)
block|{
name|matching
operator|.
name|add
argument_list|(
name|w
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Any user who has published comments on this change. */
DECL|method|ccAllApprovals ()
specifier|protected
name|void
name|ccAllApprovals
parameter_list|()
block|{
name|ccApprovals
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Users who have non-zero approval codes on the change. */
DECL|method|ccExistingReviewers ()
specifier|protected
name|void
name|ccExistingReviewers
parameter_list|()
block|{
name|ccApprovals
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|ccApprovals (final boolean includeZero)
specifier|private
name|void
name|ccApprovals
parameter_list|(
specifier|final
name|boolean
name|includeZero
parameter_list|)
block|{
try|try
block|{
comment|// CC anyone else who has posted an approval mark on this change
comment|//
for|for
control|(
name|PatchSetApproval
name|ap
range|:
name|args
operator|.
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byChange
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|includeZero
operator|&&
name|ap
operator|.
name|getValue
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|add
argument_list|(
name|RecipientType
operator|.
name|CC
argument_list|,
name|ap
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|err
parameter_list|)
block|{     }
block|}
DECL|method|isVisibleTo (final Account.Id to)
specifier|protected
name|boolean
name|isVisibleTo
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|to
parameter_list|)
block|{
return|return
name|projectState
operator|==
literal|null
operator|||
name|change
operator|==
literal|null
operator|||
name|projectState
operator|.
name|controlFor
argument_list|(
name|args
operator|.
name|identifiedUserFactory
operator|.
name|create
argument_list|(
name|to
argument_list|)
argument_list|)
operator|.
name|controlFor
argument_list|(
name|change
argument_list|)
operator|.
name|isVisible
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setupVelocityContext ()
specifier|protected
name|void
name|setupVelocityContext
parameter_list|()
block|{
name|super
operator|.
name|setupVelocityContext
argument_list|()
expr_stmt|;
name|velocityContext
operator|.
name|put
argument_list|(
literal|"change"
argument_list|,
name|change
argument_list|)
expr_stmt|;
name|velocityContext
operator|.
name|put
argument_list|(
literal|"changeId"
argument_list|,
name|change
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|velocityContext
operator|.
name|put
argument_list|(
literal|"coverLetter"
argument_list|,
name|getCoverLetter
argument_list|()
argument_list|)
expr_stmt|;
name|velocityContext
operator|.
name|put
argument_list|(
literal|"branch"
argument_list|,
name|change
operator|.
name|getDest
argument_list|()
argument_list|)
expr_stmt|;
name|velocityContext
operator|.
name|put
argument_list|(
literal|"fromName"
argument_list|,
name|getNameFor
argument_list|(
name|fromId
argument_list|)
argument_list|)
expr_stmt|;
name|velocityContext
operator|.
name|put
argument_list|(
literal|"projectName"
argument_list|,
name|projectName
argument_list|)
expr_stmt|;
name|velocityContext
operator|.
name|put
argument_list|(
literal|"patchSet"
argument_list|,
name|patchSet
argument_list|)
expr_stmt|;
name|velocityContext
operator|.
name|put
argument_list|(
literal|"patchSetInfo"
argument_list|,
name|patchSetInfo
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

