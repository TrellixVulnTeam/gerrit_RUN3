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
DECL|package|com.google.gerrit.server.notedb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|notedb
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|auto
operator|.
name|value
operator|.
name|AutoValue
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
name|annotations
operator|.
name|VisibleForTesting
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
name|server
operator|.
name|config
operator|.
name|GerritServerId
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
name|Optional
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
name|PersonIdent
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
name|revwalk
operator|.
name|FooterKey
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
name|revwalk
operator|.
name|RevCommit
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
name|util
operator|.
name|RawParseUtils
import|;
end_import

begin_class
DECL|class|ChangeNoteUtil
specifier|public
class|class
name|ChangeNoteUtil
block|{
DECL|field|FOOTER_ASSIGNEE
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_ASSIGNEE
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Assignee"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_BRANCH
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_BRANCH
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Branch"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_CHANGE_ID
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_CHANGE_ID
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Change-id"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_COMMIT
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_COMMIT
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Commit"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_CURRENT
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_CURRENT
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Current"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_GROUPS
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_GROUPS
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Groups"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_HASHTAGS
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_HASHTAGS
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Hashtags"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_LABEL
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_LABEL
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Label"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_PATCH_SET
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_PATCH_SET
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Patch-set"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_PATCH_SET_DESCRIPTION
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_PATCH_SET_DESCRIPTION
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Patch-set-description"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_PRIVATE
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_PRIVATE
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Private"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_REAL_USER
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_REAL_USER
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Real-user"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_STATUS
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_STATUS
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Status"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_SUBJECT
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_SUBJECT
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Subject"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_SUBMISSION_ID
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_SUBMISSION_ID
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Submission-id"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_SUBMITTED_WITH
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_SUBMITTED_WITH
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Submitted-with"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_TOPIC
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_TOPIC
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Topic"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_TAG
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_TAG
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Tag"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_WORK_IN_PROGRESS
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_WORK_IN_PROGRESS
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Work-in-progress"
argument_list|)
decl_stmt|;
DECL|field|FOOTER_REVERT_OF
specifier|public
specifier|static
specifier|final
name|FooterKey
name|FOOTER_REVERT_OF
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Revert-of"
argument_list|)
decl_stmt|;
DECL|field|AUTHOR
specifier|static
specifier|final
name|String
name|AUTHOR
init|=
literal|"Author"
decl_stmt|;
DECL|field|BASE_PATCH_SET
specifier|static
specifier|final
name|String
name|BASE_PATCH_SET
init|=
literal|"Base-for-patch-set"
decl_stmt|;
DECL|field|COMMENT_RANGE
specifier|static
specifier|final
name|String
name|COMMENT_RANGE
init|=
literal|"Comment-range"
decl_stmt|;
DECL|field|FILE
specifier|static
specifier|final
name|String
name|FILE
init|=
literal|"File"
decl_stmt|;
DECL|field|LENGTH
specifier|static
specifier|final
name|String
name|LENGTH
init|=
literal|"Bytes"
decl_stmt|;
DECL|field|PARENT
specifier|static
specifier|final
name|String
name|PARENT
init|=
literal|"Parent"
decl_stmt|;
DECL|field|PARENT_NUMBER
specifier|static
specifier|final
name|String
name|PARENT_NUMBER
init|=
literal|"Parent-number"
decl_stmt|;
DECL|field|PATCH_SET
specifier|static
specifier|final
name|String
name|PATCH_SET
init|=
literal|"Patch-set"
decl_stmt|;
DECL|field|REAL_AUTHOR
specifier|static
specifier|final
name|String
name|REAL_AUTHOR
init|=
literal|"Real-author"
decl_stmt|;
DECL|field|REVISION
specifier|static
specifier|final
name|String
name|REVISION
init|=
literal|"Revision"
decl_stmt|;
DECL|field|UUID
specifier|static
specifier|final
name|String
name|UUID
init|=
literal|"UUID"
decl_stmt|;
DECL|field|UNRESOLVED
specifier|static
specifier|final
name|String
name|UNRESOLVED
init|=
literal|"Unresolved"
decl_stmt|;
DECL|field|TAG
specifier|static
specifier|final
name|String
name|TAG
init|=
name|FOOTER_TAG
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|legacyChangeNoteRead
specifier|private
specifier|final
name|LegacyChangeNoteRead
name|legacyChangeNoteRead
decl_stmt|;
DECL|field|changeNoteJson
specifier|private
specifier|final
name|ChangeNoteJson
name|changeNoteJson
decl_stmt|;
DECL|field|serverId
specifier|private
specifier|final
name|String
name|serverId
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeNoteUtil ( ChangeNoteJson changeNoteJson, LegacyChangeNoteRead legacyChangeNoteRead, @GerritServerId String serverId)
specifier|public
name|ChangeNoteUtil
parameter_list|(
name|ChangeNoteJson
name|changeNoteJson
parameter_list|,
name|LegacyChangeNoteRead
name|legacyChangeNoteRead
parameter_list|,
annotation|@
name|GerritServerId
name|String
name|serverId
parameter_list|)
block|{
name|this
operator|.
name|serverId
operator|=
name|serverId
expr_stmt|;
name|this
operator|.
name|changeNoteJson
operator|=
name|changeNoteJson
expr_stmt|;
name|this
operator|.
name|legacyChangeNoteRead
operator|=
name|legacyChangeNoteRead
expr_stmt|;
block|}
DECL|method|getLegacyChangeNoteRead ()
specifier|public
name|LegacyChangeNoteRead
name|getLegacyChangeNoteRead
parameter_list|()
block|{
return|return
name|legacyChangeNoteRead
return|;
block|}
DECL|method|getChangeNoteJson ()
specifier|public
name|ChangeNoteJson
name|getChangeNoteJson
parameter_list|()
block|{
return|return
name|changeNoteJson
return|;
block|}
DECL|method|newIdent (Account.Id authorId, Date when, PersonIdent serverIdent)
specifier|public
name|PersonIdent
name|newIdent
parameter_list|(
name|Account
operator|.
name|Id
name|authorId
parameter_list|,
name|Date
name|when
parameter_list|,
name|PersonIdent
name|serverIdent
parameter_list|)
block|{
return|return
operator|new
name|PersonIdent
argument_list|(
literal|"Gerrit User "
operator|+
name|authorId
operator|.
name|toString
argument_list|()
argument_list|,
name|authorId
operator|.
name|get
argument_list|()
operator|+
literal|"@"
operator|+
name|serverId
argument_list|,
name|when
argument_list|,
name|serverIdent
operator|.
name|getTimeZone
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|newIdent (Account author, Date when, PersonIdent serverIdent)
specifier|public
name|PersonIdent
name|newIdent
parameter_list|(
name|Account
name|author
parameter_list|,
name|Date
name|when
parameter_list|,
name|PersonIdent
name|serverIdent
parameter_list|)
block|{
return|return
operator|new
name|PersonIdent
argument_list|(
literal|"Gerrit User "
operator|+
name|author
operator|.
name|getId
argument_list|()
argument_list|,
name|author
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|"@"
operator|+
name|serverId
argument_list|,
name|when
argument_list|,
name|serverIdent
operator|.
name|getTimeZone
argument_list|()
argument_list|)
return|;
block|}
DECL|method|parseCommitMessageRange (RevCommit commit)
specifier|public
specifier|static
name|Optional
argument_list|<
name|CommitMessageRange
argument_list|>
name|parseCommitMessageRange
parameter_list|(
name|RevCommit
name|commit
parameter_list|)
block|{
name|byte
index|[]
name|raw
init|=
name|commit
operator|.
name|getRawBuffer
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|raw
operator|.
name|length
decl_stmt|;
name|int
name|subjectStart
init|=
name|RawParseUtils
operator|.
name|commitMessage
argument_list|(
name|raw
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|subjectStart
operator|<
literal|0
operator|||
name|subjectStart
operator|>=
name|size
condition|)
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
name|int
name|subjectEnd
init|=
name|RawParseUtils
operator|.
name|endOfParagraph
argument_list|(
name|raw
argument_list|,
name|subjectStart
argument_list|)
decl_stmt|;
if|if
condition|(
name|subjectEnd
operator|==
name|size
condition|)
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
name|int
name|changeMessageStart
decl_stmt|;
if|if
condition|(
name|raw
index|[
name|subjectEnd
index|]
operator|==
literal|'\n'
condition|)
block|{
name|changeMessageStart
operator|=
name|subjectEnd
operator|+
literal|2
expr_stmt|;
comment|// \n\n ends paragraph
block|}
elseif|else
if|if
condition|(
name|raw
index|[
name|subjectEnd
index|]
operator|==
literal|'\r'
condition|)
block|{
name|changeMessageStart
operator|=
name|subjectEnd
operator|+
literal|4
expr_stmt|;
comment|// \r\n\r\n ends paragraph
block|}
else|else
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
name|int
name|ptr
init|=
name|size
operator|-
literal|1
decl_stmt|;
name|int
name|changeMessageEnd
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|ptr
operator|>
name|changeMessageStart
condition|)
block|{
name|ptr
operator|=
name|RawParseUtils
operator|.
name|prevLF
argument_list|(
name|raw
argument_list|,
name|ptr
argument_list|,
literal|'\r'
argument_list|)
expr_stmt|;
if|if
condition|(
name|ptr
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|raw
index|[
name|ptr
index|]
operator|==
literal|'\n'
condition|)
block|{
name|changeMessageEnd
operator|=
name|ptr
operator|-
literal|1
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|raw
index|[
name|ptr
index|]
operator|==
literal|'\r'
condition|)
block|{
name|changeMessageEnd
operator|=
name|ptr
operator|-
literal|3
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|ptr
operator|<=
name|changeMessageStart
condition|)
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
name|CommitMessageRange
name|range
init|=
name|CommitMessageRange
operator|.
name|builder
argument_list|()
operator|.
name|subjectStart
argument_list|(
name|subjectStart
argument_list|)
operator|.
name|subjectEnd
argument_list|(
name|subjectEnd
argument_list|)
operator|.
name|changeMessageStart
argument_list|(
name|changeMessageStart
argument_list|)
operator|.
name|changeMessageEnd
argument_list|(
name|changeMessageEnd
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|Optional
operator|.
name|of
argument_list|(
name|range
argument_list|)
return|;
block|}
annotation|@
name|AutoValue
DECL|class|CommitMessageRange
specifier|public
specifier|abstract
specifier|static
class|class
name|CommitMessageRange
block|{
DECL|method|subjectStart ()
specifier|public
specifier|abstract
name|int
name|subjectStart
parameter_list|()
function_decl|;
DECL|method|subjectEnd ()
specifier|public
specifier|abstract
name|int
name|subjectEnd
parameter_list|()
function_decl|;
DECL|method|changeMessageStart ()
specifier|public
specifier|abstract
name|int
name|changeMessageStart
parameter_list|()
function_decl|;
DECL|method|changeMessageEnd ()
specifier|public
specifier|abstract
name|int
name|changeMessageEnd
parameter_list|()
function_decl|;
DECL|method|builder ()
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|AutoValue_ChangeNoteUtil_CommitMessageRange
operator|.
name|Builder
argument_list|()
return|;
block|}
annotation|@
name|AutoValue
operator|.
name|Builder
DECL|class|Builder
specifier|public
specifier|abstract
specifier|static
class|class
name|Builder
block|{
DECL|method|subjectStart (int subjectStart)
specifier|abstract
name|Builder
name|subjectStart
parameter_list|(
name|int
name|subjectStart
parameter_list|)
function_decl|;
DECL|method|subjectEnd (int subjectEnd)
specifier|abstract
name|Builder
name|subjectEnd
parameter_list|(
name|int
name|subjectEnd
parameter_list|)
function_decl|;
DECL|method|changeMessageStart (int changeMessageStart)
specifier|abstract
name|Builder
name|changeMessageStart
parameter_list|(
name|int
name|changeMessageStart
parameter_list|)
function_decl|;
DECL|method|changeMessageEnd (int changeMessageEnd)
specifier|abstract
name|Builder
name|changeMessageEnd
parameter_list|(
name|int
name|changeMessageEnd
parameter_list|)
function_decl|;
DECL|method|build ()
specifier|abstract
name|CommitMessageRange
name|build
parameter_list|()
function_decl|;
block|}
block|}
block|}
end_class

end_unit

