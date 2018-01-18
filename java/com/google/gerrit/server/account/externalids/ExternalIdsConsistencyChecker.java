begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.account.externalids
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|account
operator|.
name|externalids
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
name|account
operator|.
name|externalids
operator|.
name|ExternalId
operator|.
name|SCHEME_USERNAME
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|joining
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
name|ListMultimap
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
name|MultimapBuilder
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
name|config
operator|.
name|ConsistencyCheckInfo
operator|.
name|ConsistencyProblemInfo
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
name|account
operator|.
name|AccountCache
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
name|account
operator|.
name|HashedPassword
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
name|AllUsersName
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
name|GitRepositoryManager
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
name|OutgoingEmailValidator
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
name|Singleton
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
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|DecoderException
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
name|errors
operator|.
name|ConfigInvalidException
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Repository
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
name|notes
operator|.
name|Note
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
name|notes
operator|.
name|NoteMap
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
name|RevWalk
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|ExternalIdsConsistencyChecker
specifier|public
class|class
name|ExternalIdsConsistencyChecker
block|{
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|allUsers
specifier|private
specifier|final
name|AllUsersName
name|allUsers
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|validator
specifier|private
specifier|final
name|OutgoingEmailValidator
name|validator
decl_stmt|;
annotation|@
name|Inject
DECL|method|ExternalIdsConsistencyChecker ( GitRepositoryManager repoManager, AllUsersName allUsers, AccountCache accountCache, OutgoingEmailValidator validator)
name|ExternalIdsConsistencyChecker
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|AllUsersName
name|allUsers
parameter_list|,
name|AccountCache
name|accountCache
parameter_list|,
name|OutgoingEmailValidator
name|validator
parameter_list|)
block|{
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|allUsers
operator|=
name|allUsers
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|validator
operator|=
name|validator
expr_stmt|;
block|}
DECL|method|check ()
specifier|public
name|List
argument_list|<
name|ConsistencyProblemInfo
argument_list|>
name|check
parameter_list|()
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
return|return
name|check
argument_list|(
name|ExternalIdNotes
operator|.
name|loadReadOnly
argument_list|(
name|repo
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|check (ObjectId rev)
specifier|public
name|List
argument_list|<
name|ConsistencyProblemInfo
argument_list|>
name|check
parameter_list|(
name|ObjectId
name|rev
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
return|return
name|check
argument_list|(
name|ExternalIdNotes
operator|.
name|loadReadOnly
argument_list|(
name|repo
argument_list|,
name|rev
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|check (ExternalIdNotes extIdNotes)
specifier|private
name|List
argument_list|<
name|ConsistencyProblemInfo
argument_list|>
name|check
parameter_list|(
name|ExternalIdNotes
name|extIdNotes
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|ConsistencyProblemInfo
argument_list|>
name|problems
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|ExternalId
operator|.
name|Key
argument_list|>
name|emails
init|=
name|MultimapBuilder
operator|.
name|hashKeys
argument_list|()
operator|.
name|arrayListValues
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
init|(
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|extIdNotes
operator|.
name|getRepository
argument_list|()
argument_list|)
init|)
block|{
name|NoteMap
name|noteMap
init|=
name|extIdNotes
operator|.
name|getNoteMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Note
name|note
range|:
name|noteMap
control|)
block|{
name|byte
index|[]
name|raw
init|=
name|ExternalIdNotes
operator|.
name|readNoteData
argument_list|(
name|rw
argument_list|,
name|note
operator|.
name|getData
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|ExternalId
name|extId
init|=
name|ExternalId
operator|.
name|parse
argument_list|(
name|note
operator|.
name|getName
argument_list|()
argument_list|,
name|raw
argument_list|,
name|note
operator|.
name|getData
argument_list|()
argument_list|)
decl_stmt|;
name|problems
operator|.
name|addAll
argument_list|(
name|validateExternalId
argument_list|(
name|extId
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|extId
operator|.
name|email
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|emails
operator|.
name|put
argument_list|(
name|extId
operator|.
name|email
argument_list|()
argument_list|,
name|extId
operator|.
name|key
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
name|e
parameter_list|)
block|{
name|addError
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|,
name|problems
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|emails
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|e
lambda|->
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|1
argument_list|)
operator|.
name|forEach
argument_list|(
name|e
lambda|->
name|addError
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Email '%s' is not unique, it's used by the following external IDs: %s"
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|k
lambda|->
literal|"'"
operator|+
name|k
operator|.
name|get
argument_list|()
operator|+
literal|"'"
argument_list|)
operator|.
name|sorted
argument_list|()
operator|.
name|collect
argument_list|(
name|joining
argument_list|(
literal|", "
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|problems
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|problems
return|;
block|}
DECL|method|validateExternalId (ExternalId extId)
specifier|private
name|List
argument_list|<
name|ConsistencyProblemInfo
argument_list|>
name|validateExternalId
parameter_list|(
name|ExternalId
name|extId
parameter_list|)
block|{
name|List
argument_list|<
name|ConsistencyProblemInfo
argument_list|>
name|problems
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|accountCache
operator|.
name|maybeGet
argument_list|(
name|extId
operator|.
name|accountId
argument_list|()
argument_list|)
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|addError
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"External ID '%s' belongs to account that doesn't exist: %s"
argument_list|,
name|extId
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|extId
operator|.
name|accountId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|problems
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|extId
operator|.
name|email
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|validator
operator|.
name|isValid
argument_list|(
name|extId
operator|.
name|email
argument_list|()
argument_list|)
condition|)
block|{
name|addError
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"External ID '%s' has an invalid email: %s"
argument_list|,
name|extId
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|extId
operator|.
name|email
argument_list|()
argument_list|)
argument_list|,
name|problems
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|extId
operator|.
name|password
argument_list|()
operator|!=
literal|null
operator|&&
name|extId
operator|.
name|isScheme
argument_list|(
name|SCHEME_USERNAME
argument_list|)
condition|)
block|{
try|try
block|{
name|HashedPassword
operator|.
name|decode
argument_list|(
name|extId
operator|.
name|password
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DecoderException
name|e
parameter_list|)
block|{
name|addError
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"External ID '%s' has an invalid password: %s"
argument_list|,
name|extId
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|,
name|problems
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|problems
return|;
block|}
DECL|method|addError (String error, List<ConsistencyProblemInfo> problems)
specifier|private
specifier|static
name|void
name|addError
parameter_list|(
name|String
name|error
parameter_list|,
name|List
argument_list|<
name|ConsistencyProblemInfo
argument_list|>
name|problems
parameter_list|)
block|{
name|problems
operator|.
name|add
argument_list|(
operator|new
name|ConsistencyProblemInfo
argument_list|(
name|ConsistencyProblemInfo
operator|.
name|Status
operator|.
name|ERROR
argument_list|,
name|error
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

