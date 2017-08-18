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
name|gerrit
operator|.
name|index
operator|.
name|FieldDef
operator|.
name|exact
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
name|index
operator|.
name|FieldDef
operator|.
name|integer
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
name|index
operator|.
name|FieldDef
operator|.
name|prefix
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
name|index
operator|.
name|FieldDef
operator|.
name|storedOnly
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
name|index
operator|.
name|FieldDef
operator|.
name|timestamp
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
name|toSet
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
name|Predicates
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
name|FluentIterable
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
name|ImmutableList
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
name|gerrit
operator|.
name|index
operator|.
name|FieldDef
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
name|SchemaUtil
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
name|AccountState
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
name|index
operator|.
name|RefState
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
name|Locale
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

begin_comment
comment|/** Secondary index schemas for accounts. */
end_comment

begin_class
DECL|class|AccountField
specifier|public
class|class
name|AccountField
block|{
DECL|field|ID
specifier|public
specifier|static
specifier|final
name|FieldDef
argument_list|<
name|AccountState
argument_list|,
name|Integer
argument_list|>
name|ID
init|=
name|integer
argument_list|(
literal|"id"
argument_list|)
operator|.
name|stored
argument_list|()
operator|.
name|build
argument_list|(
name|a
lambda|->
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|EXTERNAL_ID
specifier|public
specifier|static
specifier|final
name|FieldDef
argument_list|<
name|AccountState
argument_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|EXTERNAL_ID
init|=
name|exact
argument_list|(
literal|"external_id"
argument_list|)
operator|.
name|buildRepeatable
argument_list|(
name|a
lambda|->
name|Iterables
operator|.
name|transform
argument_list|(
name|a
operator|.
name|getExternalIds
argument_list|()
argument_list|,
name|id
lambda|->
name|id
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|/** Fuzzy prefix match on name and email parts. */
DECL|field|NAME_PART
specifier|public
specifier|static
specifier|final
name|FieldDef
argument_list|<
name|AccountState
argument_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|NAME_PART
init|=
name|prefix
argument_list|(
literal|"name"
argument_list|)
operator|.
name|buildRepeatable
argument_list|(
name|a
lambda|->
block|{
name|String
name|fullName
init|=
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|getFullName
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|parts
init|=
name|SchemaUtil
operator|.
name|getNameParts
argument_list|(
name|fullName
argument_list|,
name|Iterables
operator|.
name|transform
argument_list|(
name|a
operator|.
name|getExternalIds
argument_list|()
argument_list|,
name|ExternalId
operator|::
name|email
argument_list|)
argument_list|)
decl_stmt|;
comment|// Additional values not currently added by getPersonParts.
comment|// TODO(dborowitz): Move to getPersonParts and remove this hack.
if|if
condition|(
name|fullName
operator|!=
literal|null
condition|)
block|{
name|parts
operator|.
name|add
argument_list|(
name|fullName
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|parts
return|;
block|}
argument_list|)
decl_stmt|;
DECL|field|FULL_NAME
specifier|public
specifier|static
specifier|final
name|FieldDef
argument_list|<
name|AccountState
argument_list|,
name|String
argument_list|>
name|FULL_NAME
init|=
name|exact
argument_list|(
literal|"full_name"
argument_list|)
operator|.
name|build
argument_list|(
name|a
lambda|->
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|getFullName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|ACTIVE
specifier|public
specifier|static
specifier|final
name|FieldDef
argument_list|<
name|AccountState
argument_list|,
name|String
argument_list|>
name|ACTIVE
init|=
name|exact
argument_list|(
literal|"inactive"
argument_list|)
operator|.
name|build
argument_list|(
name|a
lambda|->
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|isActive
argument_list|()
condition|?
literal|"1"
else|:
literal|"0"
argument_list|)
decl_stmt|;
DECL|field|EMAIL
specifier|public
specifier|static
specifier|final
name|FieldDef
argument_list|<
name|AccountState
argument_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|EMAIL
init|=
name|prefix
argument_list|(
literal|"email"
argument_list|)
operator|.
name|buildRepeatable
argument_list|(
name|a
lambda|->
name|FluentIterable
operator|.
name|from
argument_list|(
name|a
operator|.
name|getExternalIds
argument_list|()
argument_list|)
operator|.
name|transform
argument_list|(
name|ExternalId
operator|::
name|email
argument_list|)
operator|.
name|append
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|getPreferredEmail
argument_list|()
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|Predicates
operator|.
name|notNull
argument_list|()
argument_list|)
operator|.
name|transform
argument_list|(
name|String
operator|::
name|toLowerCase
argument_list|)
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|PREFERRED_EMAIL
specifier|public
specifier|static
specifier|final
name|FieldDef
argument_list|<
name|AccountState
argument_list|,
name|String
argument_list|>
name|PREFERRED_EMAIL
init|=
name|prefix
argument_list|(
literal|"preferredemail"
argument_list|)
operator|.
name|build
argument_list|(
name|a
lambda|->
block|{
name|String
name|preferredEmail
init|=
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|getPreferredEmail
argument_list|()
decl_stmt|;
return|return
name|preferredEmail
operator|!=
literal|null
condition|?
name|preferredEmail
operator|.
name|toLowerCase
argument_list|()
else|:
literal|null
return|;
block|}
argument_list|)
decl_stmt|;
DECL|field|PREFERRED_EMAIL_EXACT
specifier|public
specifier|static
specifier|final
name|FieldDef
argument_list|<
name|AccountState
argument_list|,
name|String
argument_list|>
name|PREFERRED_EMAIL_EXACT
init|=
name|exact
argument_list|(
literal|"preferredemail_exact"
argument_list|)
operator|.
name|build
argument_list|(
name|a
lambda|->
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|getPreferredEmail
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|REGISTERED
specifier|public
specifier|static
specifier|final
name|FieldDef
argument_list|<
name|AccountState
argument_list|,
name|Timestamp
argument_list|>
name|REGISTERED
init|=
name|timestamp
argument_list|(
literal|"registered"
argument_list|)
operator|.
name|build
argument_list|(
name|a
lambda|->
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|getRegisteredOn
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|USERNAME
specifier|public
specifier|static
specifier|final
name|FieldDef
argument_list|<
name|AccountState
argument_list|,
name|String
argument_list|>
name|USERNAME
init|=
name|exact
argument_list|(
literal|"username"
argument_list|)
operator|.
name|build
argument_list|(
name|a
lambda|->
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|a
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|toLowerCase
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|WATCHED_PROJECT
specifier|public
specifier|static
specifier|final
name|FieldDef
argument_list|<
name|AccountState
argument_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|>
name|WATCHED_PROJECT
init|=
name|exact
argument_list|(
literal|"watchedproject"
argument_list|)
operator|.
name|buildRepeatable
argument_list|(
name|a
lambda|->
name|FluentIterable
operator|.
name|from
argument_list|(
name|a
operator|.
name|getProjectWatches
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|transform
argument_list|(
name|k
lambda|->
name|k
operator|.
name|project
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * All values of all refs that were used in the course of indexing this document, except the    * refs/meta/external-ids notes branch which is handled specially (see {@link    * #EXTERNAL_ID_STATE}).    *    *<p>Emitted as UTF-8 encoded strings of the form {@code project:ref/name:[hex sha]}.    */
DECL|field|REF_STATE
specifier|public
specifier|static
specifier|final
name|FieldDef
argument_list|<
name|AccountState
argument_list|,
name|Iterable
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|>
name|REF_STATE
init|=
name|storedOnly
argument_list|(
literal|"ref_state"
argument_list|)
operator|.
name|buildRepeatable
argument_list|(
name|a
lambda|->
block|{
if|if
condition|(
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|getMetaId
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|RefState
operator|.
name|create
argument_list|(
name|RefNames
operator|.
name|refsUsers
argument_list|(
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|ObjectId
operator|.
name|fromString
argument_list|(
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|getMetaId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|toByteArray
argument_list|(
name|a
operator|.
name|getAllUsersNameForIndexing
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
argument_list|)
decl_stmt|;
comment|/**    * All note values of all external IDs that were used in the course of indexing this document.    *    *<p>Emitted as UTF-8 encoded strings of the form {@code [hex sha of external ID]:[hex sha of    * note blob]}, or with other words {@code [note ID]:[note data ID]}.    */
DECL|field|EXTERNAL_ID_STATE
specifier|public
specifier|static
specifier|final
name|FieldDef
argument_list|<
name|AccountState
argument_list|,
name|Iterable
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|>
name|EXTERNAL_ID_STATE
init|=
name|storedOnly
argument_list|(
literal|"external_id_state"
argument_list|)
operator|.
name|buildRepeatable
argument_list|(
name|a
lambda|->
name|a
operator|.
name|getExternalIds
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
name|blobId
argument_list|()
operator|!=
literal|null
argument_list|)
operator|.
name|map
argument_list|(
name|e
lambda|->
name|e
operator|.
name|toByteArray
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|toSet
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|AccountField ()
specifier|private
name|AccountField
parameter_list|()
block|{}
block|}
end_class

end_unit

