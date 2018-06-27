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
DECL|package|com.google.gerrit.server.index.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|index
operator|.
name|account
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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|collect
operator|.
name|ImmutableSet
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
name|index
operator|.
name|IndexConfig
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
name|index
operator|.
name|QueryOptions
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
name|index
operator|.
name|RefState
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
name|index
operator|.
name|query
operator|.
name|FieldBundle
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
name|client
operator|.
name|RefNames
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
name|externalids
operator|.
name|ExternalId
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
name|externalids
operator|.
name|ExternalIds
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
name|index
operator|.
name|IndexUtils
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
name|Ref
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

begin_comment
comment|/**  * Checks if documents in the account index are stale.  *  *<p>An index document is considered stale if the stored ref state differs from the SHA1 of the  * user branch or if the stored external ID states don't match with the external IDs of the account  * from the refs/meta/external-ids branch.  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|StalenessChecker
specifier|public
class|class
name|StalenessChecker
block|{
DECL|field|FIELDS
specifier|public
specifier|static
specifier|final
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|FIELDS
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|AccountField
operator|.
name|ID
operator|.
name|getName
argument_list|()
argument_list|,
name|AccountField
operator|.
name|REF_STATE
operator|.
name|getName
argument_list|()
argument_list|,
name|AccountField
operator|.
name|EXTERNAL_ID_STATE
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|indexes
specifier|private
specifier|final
name|AccountIndexCollection
name|indexes
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|field|externalIds
specifier|private
specifier|final
name|ExternalIds
name|externalIds
decl_stmt|;
DECL|field|indexConfig
specifier|private
specifier|final
name|IndexConfig
name|indexConfig
decl_stmt|;
annotation|@
name|Inject
DECL|method|StalenessChecker ( AccountIndexCollection indexes, GitRepositoryManager repoManager, AllUsersName allUsersName, ExternalIds externalIds, IndexConfig indexConfig)
name|StalenessChecker
parameter_list|(
name|AccountIndexCollection
name|indexes
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|AllUsersName
name|allUsersName
parameter_list|,
name|ExternalIds
name|externalIds
parameter_list|,
name|IndexConfig
name|indexConfig
parameter_list|)
block|{
name|this
operator|.
name|indexes
operator|=
name|indexes
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
name|this
operator|.
name|externalIds
operator|=
name|externalIds
expr_stmt|;
name|this
operator|.
name|indexConfig
operator|=
name|indexConfig
expr_stmt|;
block|}
DECL|method|isStale (Account.Id id)
specifier|public
name|boolean
name|isStale
parameter_list|(
name|Account
operator|.
name|Id
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|AccountIndex
name|i
init|=
name|indexes
operator|.
name|getSearchIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|null
condition|)
block|{
comment|// No index; caller couldn't do anything if it is stale.
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|i
operator|.
name|getSchema
argument_list|()
operator|.
name|hasField
argument_list|(
name|AccountField
operator|.
name|REF_STATE
argument_list|)
operator|||
operator|!
name|i
operator|.
name|getSchema
argument_list|()
operator|.
name|hasField
argument_list|(
name|AccountField
operator|.
name|EXTERNAL_ID_STATE
argument_list|)
condition|)
block|{
comment|// Index version not new enough for this check.
return|return
literal|false
return|;
block|}
name|Optional
argument_list|<
name|FieldBundle
argument_list|>
name|result
init|=
name|i
operator|.
name|getRaw
argument_list|(
name|id
argument_list|,
name|QueryOptions
operator|.
name|create
argument_list|(
name|indexConfig
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|IndexUtils
operator|.
name|accountFields
argument_list|(
name|FIELDS
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|result
operator|.
name|isPresent
argument_list|()
condition|)
block|{
comment|// The document is missing in the index.
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsersName
argument_list|)
init|)
block|{
name|Ref
name|ref
init|=
name|repo
operator|.
name|exactRef
argument_list|(
name|RefNames
operator|.
name|refsUsers
argument_list|(
name|id
argument_list|)
argument_list|)
decl_stmt|;
comment|// Stale if the account actually exists.
return|return
name|ref
operator|!=
literal|null
return|;
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|RefState
argument_list|>
name|e
range|:
name|RefState
operator|.
name|parseStates
argument_list|(
name|result
operator|.
name|get
argument_list|()
operator|.
name|getValue
argument_list|(
name|AccountField
operator|.
name|REF_STATE
argument_list|)
argument_list|)
operator|.
name|entries
argument_list|()
control|)
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
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
init|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|match
argument_list|(
name|repo
argument_list|)
condition|)
block|{
comment|// Ref was modified since the account was indexed.
return|return
literal|true
return|;
block|}
block|}
block|}
name|Set
argument_list|<
name|ExternalId
argument_list|>
name|extIds
init|=
name|externalIds
operator|.
name|byAccount
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|ListMultimap
argument_list|<
name|ObjectId
argument_list|,
name|ObjectId
argument_list|>
name|extIdStates
init|=
name|parseExternalIdStates
argument_list|(
name|result
operator|.
name|get
argument_list|()
operator|.
name|getValue
argument_list|(
name|AccountField
operator|.
name|EXTERNAL_ID_STATE
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|extIdStates
operator|.
name|size
argument_list|()
operator|!=
name|extIds
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// External IDs of the account were modified since the account was indexed.
return|return
literal|true
return|;
block|}
for|for
control|(
name|ExternalId
name|extId
range|:
name|extIds
control|)
block|{
if|if
condition|(
operator|!
name|extIdStates
operator|.
name|containsEntry
argument_list|(
name|extId
operator|.
name|key
argument_list|()
operator|.
name|sha1
argument_list|()
argument_list|,
name|extId
operator|.
name|blobId
argument_list|()
argument_list|)
condition|)
block|{
comment|// External IDs of the account were modified since the account was indexed.
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|parseExternalIdStates ( Iterable<byte[]> extIdStates)
specifier|public
specifier|static
name|ListMultimap
argument_list|<
name|ObjectId
argument_list|,
name|ObjectId
argument_list|>
name|parseExternalIdStates
parameter_list|(
name|Iterable
argument_list|<
name|byte
index|[]
argument_list|>
name|extIdStates
parameter_list|)
block|{
name|ListMultimap
argument_list|<
name|ObjectId
argument_list|,
name|ObjectId
argument_list|>
name|result
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
if|if
condition|(
name|extIdStates
operator|==
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
for|for
control|(
name|byte
index|[]
name|b
range|:
name|extIdStates
control|)
block|{
name|checkNotNull
argument_list|(
name|b
argument_list|,
literal|"invalid external ID state"
argument_list|)
expr_stmt|;
name|String
name|s
init|=
operator|new
name|String
argument_list|(
name|b
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
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
literal|':'
argument_list|)
operator|.
name|splitToList
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|parts
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|,
literal|"invalid external ID state: %s"
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
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
argument_list|,
name|ObjectId
operator|.
name|fromString
argument_list|(
name|parts
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
name|result
return|;
block|}
block|}
end_class

end_unit

