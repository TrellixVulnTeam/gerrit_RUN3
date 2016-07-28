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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
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
name|reviewdb
operator|.
name|client
operator|.
name|RefNames
operator|.
name|changeMetaRef
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
name|reviewdb
operator|.
name|client
operator|.
name|RefNames
operator|.
name|refsDraftComments
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
name|server
operator|.
name|notedb
operator|.
name|NoteDbChangeState
operator|.
name|PrimaryStorage
operator|.
name|NOTE_DB
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
name|server
operator|.
name|notedb
operator|.
name|NoteDbChangeState
operator|.
name|PrimaryStorage
operator|.
name|REVIEW_DB
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectId
operator|.
name|zeroId
import|;
end_import

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
name|common
operator|.
name|base
operator|.
name|Splitter
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
name|ImmutableMap
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
name|server
operator|.
name|ReviewDbUtil
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
name|git
operator|.
name|RefCache
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_comment
comment|/**  * The state of all relevant NoteDb refs across all repos corresponding to a  * given Change entity.  *<p>  * Stored serialized in the {@code Change#noteDbState} field, and used to  * determine whether the state in NoteDb is out of date.  *<p>  * Serialized in one of the forms:  *<ul>  *<li>[meta-sha],[account1]=[drafts-sha],[account2]=[drafts-sha]...  *<li>R[meta-sha],[account1]=[drafts-sha],[account2]=[drafts-sha]...  *<li>N  *</ul>  *  * in numeric account ID order, with hex SHA-1s for human readability.  */
end_comment

begin_class
DECL|class|NoteDbChangeState
specifier|public
class|class
name|NoteDbChangeState
block|{
DECL|field|NOTE_DB_PRIMARY_STATE
specifier|public
specifier|static
specifier|final
name|String
name|NOTE_DB_PRIMARY_STATE
init|=
literal|"N"
decl_stmt|;
DECL|enum|PrimaryStorage
specifier|public
enum|enum
name|PrimaryStorage
block|{
DECL|enumConstant|REVIEW_DB
name|REVIEW_DB
argument_list|(
literal|'R'
argument_list|)
block|,
DECL|enumConstant|NOTE_DB
name|NOTE_DB
argument_list|(
literal|'N'
argument_list|)
block|;
DECL|field|code
specifier|private
specifier|final
name|char
name|code
decl_stmt|;
DECL|method|PrimaryStorage (char code)
specifier|private
name|PrimaryStorage
parameter_list|(
name|char
name|code
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
block|}
DECL|method|of (Change c)
specifier|public
specifier|static
name|PrimaryStorage
name|of
parameter_list|(
name|Change
name|c
parameter_list|)
block|{
return|return
name|of
argument_list|(
name|NoteDbChangeState
operator|.
name|parse
argument_list|(
name|c
argument_list|)
argument_list|)
return|;
block|}
DECL|method|of (NoteDbChangeState s)
specifier|public
specifier|static
name|PrimaryStorage
name|of
parameter_list|(
name|NoteDbChangeState
name|s
parameter_list|)
block|{
return|return
name|s
operator|!=
literal|null
condition|?
name|s
operator|.
name|getPrimaryStorage
argument_list|()
else|:
name|REVIEW_DB
return|;
block|}
block|}
annotation|@
name|AutoValue
DECL|class|Delta
specifier|public
specifier|abstract
specifier|static
class|class
name|Delta
block|{
DECL|method|create (Change.Id changeId, Optional<ObjectId> newChangeMetaId, Map<Account.Id, ObjectId> newDraftIds)
specifier|static
name|Delta
name|create
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|Optional
argument_list|<
name|ObjectId
argument_list|>
name|newChangeMetaId
parameter_list|,
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ObjectId
argument_list|>
name|newDraftIds
parameter_list|)
block|{
if|if
condition|(
name|newDraftIds
operator|==
literal|null
condition|)
block|{
name|newDraftIds
operator|=
name|ImmutableMap
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|AutoValue_NoteDbChangeState_Delta
argument_list|(
name|changeId
argument_list|,
name|newChangeMetaId
argument_list|,
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|newDraftIds
argument_list|)
argument_list|)
return|;
block|}
DECL|method|changeId ()
specifier|abstract
name|Change
operator|.
name|Id
name|changeId
parameter_list|()
function_decl|;
DECL|method|newChangeMetaId ()
specifier|abstract
name|Optional
argument_list|<
name|ObjectId
argument_list|>
name|newChangeMetaId
parameter_list|()
function_decl|;
DECL|method|newDraftIds ()
specifier|abstract
name|ImmutableMap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ObjectId
argument_list|>
name|newDraftIds
parameter_list|()
function_decl|;
block|}
annotation|@
name|AutoValue
DECL|class|RefState
specifier|public
specifier|abstract
specifier|static
class|class
name|RefState
block|{
annotation|@
name|VisibleForTesting
DECL|method|create (ObjectId changeMetaId, Map<Account.Id, ObjectId> draftIds)
specifier|public
specifier|static
name|RefState
name|create
parameter_list|(
name|ObjectId
name|changeMetaId
parameter_list|,
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ObjectId
argument_list|>
name|draftIds
parameter_list|)
block|{
return|return
operator|new
name|AutoValue_NoteDbChangeState_RefState
argument_list|(
name|changeMetaId
operator|.
name|copy
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|Maps
operator|.
name|filterValues
argument_list|(
name|draftIds
argument_list|,
name|id
lambda|->
operator|!
name|zeroId
argument_list|()
operator|.
name|equals
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|parse (Change.Id changeId, List<String> parts)
specifier|private
specifier|static
name|Optional
argument_list|<
name|RefState
argument_list|>
name|parse
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|parts
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|parts
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"missing state string for change %s"
argument_list|,
name|changeId
argument_list|)
expr_stmt|;
name|ObjectId
name|changeMetaId
init|=
name|ObjectId
operator|.
name|fromString
argument_list|(
name|parts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ObjectId
argument_list|>
name|draftIds
init|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
name|parts
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Splitter
name|s
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|'='
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|parts
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|p
init|=
name|parts
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|draftParts
init|=
name|s
operator|.
name|splitToList
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|draftParts
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|,
literal|"invalid draft state part for change %s: %s"
argument_list|,
name|changeId
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|draftIds
operator|.
name|put
argument_list|(
name|Account
operator|.
name|Id
operator|.
name|parse
argument_list|(
name|draftParts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
name|ObjectId
operator|.
name|fromString
argument_list|(
name|draftParts
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Optional
operator|.
name|of
argument_list|(
name|create
argument_list|(
name|changeMetaId
argument_list|,
name|draftIds
argument_list|)
argument_list|)
return|;
block|}
DECL|method|changeMetaId ()
specifier|abstract
name|ObjectId
name|changeMetaId
parameter_list|()
function_decl|;
DECL|method|draftIds ()
specifier|abstract
name|ImmutableMap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ObjectId
argument_list|>
name|draftIds
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|appendTo
argument_list|(
operator|new
name|StringBuilder
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|appendTo (StringBuilder sb)
name|StringBuilder
name|appendTo
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|changeMetaId
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|id
range|:
name|ReviewDbUtil
operator|.
name|intKeyOrdering
argument_list|()
operator|.
name|sortedCopy
argument_list|(
name|draftIds
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
operator|.
name|append
argument_list|(
name|draftIds
argument_list|()
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
return|;
block|}
block|}
DECL|method|parse (Change c)
specifier|public
specifier|static
name|NoteDbChangeState
name|parse
parameter_list|(
name|Change
name|c
parameter_list|)
block|{
return|return
name|c
operator|!=
literal|null
condition|?
name|parse
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
name|c
operator|.
name|getNoteDbState
argument_list|()
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|parse (Change.Id id, String str)
specifier|public
specifier|static
name|NoteDbChangeState
name|parse
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|,
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|str
argument_list|)
condition|)
block|{
comment|// Return null rather than Optional as this is what goes in the field in
comment|// ReviewDb.
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|parts
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|splitToList
argument_list|(
name|str
argument_list|)
decl_stmt|;
comment|// Only valid NOTE_DB state is "N".
name|String
name|first
init|=
name|parts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|first
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
name|NOTE_DB
operator|.
name|code
condition|)
block|{
return|return
operator|new
name|NoteDbChangeState
argument_list|(
name|id
argument_list|,
name|NOTE_DB
argument_list|,
name|Optional
operator|.
name|empty
argument_list|()
argument_list|)
return|;
block|}
comment|// Otherwise it must be REVIEW_DB, either "R,<RefState>" or just
comment|// "<RefState>". Allow length> 0 for forward compatibility.
if|if
condition|(
name|first
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Optional
argument_list|<
name|RefState
argument_list|>
name|refState
decl_stmt|;
if|if
condition|(
name|first
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
name|REVIEW_DB
operator|.
name|code
condition|)
block|{
name|refState
operator|=
name|RefState
operator|.
name|parse
argument_list|(
name|id
argument_list|,
name|parts
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|parts
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|refState
operator|=
name|RefState
operator|.
name|parse
argument_list|(
name|id
argument_list|,
name|parts
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|NoteDbChangeState
argument_list|(
name|id
argument_list|,
name|REVIEW_DB
argument_list|,
name|refState
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid state string for change "
operator|+
name|id
operator|+
literal|": "
operator|+
name|str
argument_list|)
throw|;
block|}
DECL|method|applyDelta (Change change, Delta delta)
specifier|public
specifier|static
name|NoteDbChangeState
name|applyDelta
parameter_list|(
name|Change
name|change
parameter_list|,
name|Delta
name|delta
parameter_list|)
block|{
if|if
condition|(
name|delta
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|oldStr
init|=
name|change
operator|.
name|getNoteDbState
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldStr
operator|==
literal|null
operator|&&
operator|!
name|delta
operator|.
name|newChangeMetaId
argument_list|()
operator|.
name|isPresent
argument_list|()
condition|)
block|{
comment|// Neither an old nor a new meta ID was present, most likely because we
comment|// aren't writing a NoteDb graph at all for this change at this point. No
comment|// point in proceeding.
return|return
literal|null
return|;
block|}
name|NoteDbChangeState
name|oldState
init|=
name|parse
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|oldStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldState
operator|!=
literal|null
operator|&&
name|oldState
operator|.
name|getPrimaryStorage
argument_list|()
operator|==
name|NOTE_DB
condition|)
block|{
comment|// NOTE_DB state doesn't include RefState, so applying a delta is a no-op.
return|return
name|oldState
return|;
block|}
name|ObjectId
name|changeMetaId
decl_stmt|;
if|if
condition|(
name|delta
operator|.
name|newChangeMetaId
argument_list|()
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|changeMetaId
operator|=
name|delta
operator|.
name|newChangeMetaId
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|changeMetaId
operator|.
name|equals
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
condition|)
block|{
name|change
operator|.
name|setNoteDbState
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
name|changeMetaId
operator|=
name|oldState
operator|.
name|getChangeMetaId
argument_list|()
expr_stmt|;
block|}
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ObjectId
argument_list|>
name|draftIds
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldState
operator|!=
literal|null
condition|)
block|{
name|draftIds
operator|.
name|putAll
argument_list|(
name|oldState
operator|.
name|getDraftIds
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ObjectId
argument_list|>
name|e
range|:
name|delta
operator|.
name|newDraftIds
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
condition|)
block|{
name|draftIds
operator|.
name|remove
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|draftIds
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|NoteDbChangeState
name|state
init|=
operator|new
name|NoteDbChangeState
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|oldState
operator|!=
literal|null
condition|?
name|oldState
operator|.
name|getPrimaryStorage
argument_list|()
else|:
name|REVIEW_DB
argument_list|,
name|Optional
operator|.
name|of
argument_list|(
name|RefState
operator|.
name|create
argument_list|(
name|changeMetaId
argument_list|,
name|draftIds
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|change
operator|.
name|setNoteDbState
argument_list|(
name|state
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|state
return|;
block|}
DECL|method|isChangeUpToDate (@ullable NoteDbChangeState state, RefCache changeRepoRefs, Change.Id changeId)
specifier|public
specifier|static
name|boolean
name|isChangeUpToDate
parameter_list|(
annotation|@
name|Nullable
name|NoteDbChangeState
name|state
parameter_list|,
name|RefCache
name|changeRepoRefs
parameter_list|,
name|Change
operator|.
name|Id
name|changeId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|PrimaryStorage
operator|.
name|of
argument_list|(
name|state
argument_list|)
operator|==
name|NOTE_DB
condition|)
block|{
return|return
literal|true
return|;
comment|// Primary storage is NoteDb, up to date by definition.
block|}
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
return|return
operator|!
name|changeRepoRefs
operator|.
name|get
argument_list|(
name|changeMetaRef
argument_list|(
name|changeId
argument_list|)
argument_list|)
operator|.
name|isPresent
argument_list|()
return|;
block|}
return|return
name|state
operator|.
name|isChangeUpToDate
argument_list|(
name|changeRepoRefs
argument_list|)
return|;
block|}
DECL|method|areDraftsUpToDate (@ullable NoteDbChangeState state, RefCache draftsRepoRefs, Change.Id changeId, Account.Id accountId)
specifier|public
specifier|static
name|boolean
name|areDraftsUpToDate
parameter_list|(
annotation|@
name|Nullable
name|NoteDbChangeState
name|state
parameter_list|,
name|RefCache
name|draftsRepoRefs
parameter_list|,
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|PrimaryStorage
operator|.
name|of
argument_list|(
name|state
argument_list|)
operator|==
name|NOTE_DB
condition|)
block|{
return|return
literal|true
return|;
comment|// Primary storage is NoteDb, up to date by definition.
block|}
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
return|return
operator|!
name|draftsRepoRefs
operator|.
name|get
argument_list|(
name|refsDraftComments
argument_list|(
name|changeId
argument_list|,
name|accountId
argument_list|)
argument_list|)
operator|.
name|isPresent
argument_list|()
return|;
block|}
return|return
name|state
operator|.
name|areDraftsUpToDate
argument_list|(
name|draftsRepoRefs
argument_list|,
name|accountId
argument_list|)
return|;
block|}
DECL|field|changeId
specifier|private
specifier|final
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
DECL|field|primaryStorage
specifier|private
specifier|final
name|PrimaryStorage
name|primaryStorage
decl_stmt|;
DECL|field|refState
specifier|private
specifier|final
name|Optional
argument_list|<
name|RefState
argument_list|>
name|refState
decl_stmt|;
DECL|method|NoteDbChangeState ( Change.Id changeId, PrimaryStorage primaryStorage, Optional<RefState> refState)
specifier|public
name|NoteDbChangeState
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|PrimaryStorage
name|primaryStorage
parameter_list|,
name|Optional
argument_list|<
name|RefState
argument_list|>
name|refState
parameter_list|)
block|{
name|this
operator|.
name|changeId
operator|=
name|checkNotNull
argument_list|(
name|changeId
argument_list|)
expr_stmt|;
name|this
operator|.
name|primaryStorage
operator|=
name|checkNotNull
argument_list|(
name|primaryStorage
argument_list|)
expr_stmt|;
name|this
operator|.
name|refState
operator|=
name|refState
expr_stmt|;
switch|switch
condition|(
name|primaryStorage
condition|)
block|{
case|case
name|REVIEW_DB
case|:
name|checkArgument
argument_list|(
name|refState
operator|.
name|isPresent
argument_list|()
argument_list|,
literal|"expected RefState for change %s with primary storage %s"
argument_list|,
name|changeId
argument_list|,
name|primaryStorage
argument_list|)
expr_stmt|;
break|break;
case|case
name|NOTE_DB
case|:
name|checkArgument
argument_list|(
operator|!
name|refState
operator|.
name|isPresent
argument_list|()
argument_list|,
literal|"expected no RefState for change %s with primary storage %s"
argument_list|,
name|changeId
argument_list|,
name|primaryStorage
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"invalid PrimaryStorage: "
operator|+
name|primaryStorage
argument_list|)
throw|;
block|}
block|}
DECL|method|getPrimaryStorage ()
specifier|public
name|PrimaryStorage
name|getPrimaryStorage
parameter_list|()
block|{
return|return
name|primaryStorage
return|;
block|}
DECL|method|isChangeUpToDate (RefCache changeRepoRefs)
specifier|public
name|boolean
name|isChangeUpToDate
parameter_list|(
name|RefCache
name|changeRepoRefs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|primaryStorage
operator|==
name|NOTE_DB
condition|)
block|{
return|return
literal|true
return|;
comment|// Primary storage is NoteDb, up to date by definition.
block|}
name|Optional
argument_list|<
name|ObjectId
argument_list|>
name|id
init|=
name|changeRepoRefs
operator|.
name|get
argument_list|(
name|changeMetaRef
argument_list|(
name|changeId
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|id
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|getChangeMetaId
argument_list|()
operator|.
name|equals
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
return|;
block|}
return|return
name|id
operator|.
name|get
argument_list|()
operator|.
name|equals
argument_list|(
name|getChangeMetaId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|areDraftsUpToDate (RefCache draftsRepoRefs, Account.Id accountId)
specifier|public
name|boolean
name|areDraftsUpToDate
parameter_list|(
name|RefCache
name|draftsRepoRefs
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|primaryStorage
operator|==
name|NOTE_DB
condition|)
block|{
return|return
literal|true
return|;
comment|// Primary storage is NoteDb, up to date by definition.
block|}
name|Optional
argument_list|<
name|ObjectId
argument_list|>
name|id
init|=
name|draftsRepoRefs
operator|.
name|get
argument_list|(
name|refsDraftComments
argument_list|(
name|changeId
argument_list|,
name|accountId
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|id
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
operator|!
name|getDraftIds
argument_list|()
operator|.
name|containsKey
argument_list|(
name|accountId
argument_list|)
return|;
block|}
return|return
name|id
operator|.
name|get
argument_list|()
operator|.
name|equals
argument_list|(
name|getDraftIds
argument_list|()
operator|.
name|get
argument_list|(
name|accountId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|isUpToDate (RefCache changeRepoRefs, RefCache draftsRepoRefs)
specifier|public
name|boolean
name|isUpToDate
parameter_list|(
name|RefCache
name|changeRepoRefs
parameter_list|,
name|RefCache
name|draftsRepoRefs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|primaryStorage
operator|==
name|NOTE_DB
condition|)
block|{
return|return
literal|true
return|;
comment|// Primary storage is NoteDb, up to date by definition.
block|}
if|if
condition|(
operator|!
name|isChangeUpToDate
argument_list|(
name|changeRepoRefs
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|Account
operator|.
name|Id
name|accountId
range|:
name|getDraftIds
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|areDraftsUpToDate
argument_list|(
name|draftsRepoRefs
argument_list|,
name|accountId
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getChangeId ()
name|Change
operator|.
name|Id
name|getChangeId
parameter_list|()
block|{
return|return
name|changeId
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getChangeMetaId ()
specifier|public
name|ObjectId
name|getChangeMetaId
parameter_list|()
block|{
return|return
name|refState
argument_list|()
operator|.
name|changeMetaId
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getDraftIds ()
name|ImmutableMap
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ObjectId
argument_list|>
name|getDraftIds
parameter_list|()
block|{
return|return
name|refState
argument_list|()
operator|.
name|draftIds
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getRefState ()
name|Optional
argument_list|<
name|RefState
argument_list|>
name|getRefState
parameter_list|()
block|{
return|return
name|refState
return|;
block|}
DECL|method|refState ()
specifier|private
name|RefState
name|refState
parameter_list|()
block|{
name|checkState
argument_list|(
name|refState
operator|.
name|isPresent
argument_list|()
argument_list|,
literal|"state for %s has no RefState: %s"
argument_list|,
name|changeId
argument_list|,
name|this
argument_list|)
expr_stmt|;
return|return
name|refState
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
switch|switch
condition|(
name|primaryStorage
condition|)
block|{
case|case
name|REVIEW_DB
case|:
comment|// Don't include enum field, just IDs (though parse would accept it).
return|return
name|refState
argument_list|()
operator|.
name|toString
argument_list|()
return|;
case|case
name|NOTE_DB
case|:
return|return
name|NOTE_DB_PRIMARY_STATE
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported PrimaryStorage: "
operator|+
name|primaryStorage
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

