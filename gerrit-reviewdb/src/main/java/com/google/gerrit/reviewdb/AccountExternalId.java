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
DECL|package|com.google.gerrit.reviewdb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
package|;
end_package

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
name|Column
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
name|StringKey
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

begin_comment
comment|/** Association of an external account identifier to a local {@link Account}. */
end_comment

begin_class
DECL|class|AccountExternalId
specifier|public
specifier|final
class|class
name|AccountExternalId
block|{
DECL|field|SCHEME_GERRIT
specifier|public
specifier|static
specifier|final
name|String
name|SCHEME_GERRIT
init|=
literal|"gerrit:"
decl_stmt|;
DECL|field|SCHEME_UUID
specifier|public
specifier|static
specifier|final
name|String
name|SCHEME_UUID
init|=
literal|"uuid:"
decl_stmt|;
DECL|field|SCHEME_MAILTO
specifier|public
specifier|static
specifier|final
name|String
name|SCHEME_MAILTO
init|=
literal|"mailto:"
decl_stmt|;
DECL|field|LEGACY_GAE
specifier|public
specifier|static
specifier|final
name|String
name|LEGACY_GAE
init|=
literal|"Google Account "
decl_stmt|;
DECL|class|Key
specifier|public
specifier|static
class|class
name|Key
extends|extends
name|StringKey
argument_list|<
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|Key
argument_list|<
name|?
argument_list|>
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Column
DECL|field|externalId
specifier|protected
name|String
name|externalId
decl_stmt|;
DECL|method|Key ()
specifier|protected
name|Key
parameter_list|()
block|{     }
DECL|method|Key (final String e)
specifier|public
name|Key
parameter_list|(
specifier|final
name|String
name|e
parameter_list|)
block|{
name|externalId
operator|=
name|e
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|String
name|get
parameter_list|()
block|{
return|return
name|externalId
return|;
block|}
annotation|@
name|Override
DECL|method|set (String newValue)
specifier|protected
name|void
name|set
parameter_list|(
name|String
name|newValue
parameter_list|)
block|{
name|externalId
operator|=
name|newValue
expr_stmt|;
block|}
block|}
annotation|@
name|Column
argument_list|(
name|name
operator|=
name|Column
operator|.
name|NONE
argument_list|)
DECL|field|key
specifier|protected
name|Key
name|key
decl_stmt|;
annotation|@
name|Column
DECL|field|accountId
specifier|protected
name|Account
operator|.
name|Id
name|accountId
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|notNull
operator|=
literal|false
argument_list|)
DECL|field|emailAddress
specifier|protected
name|String
name|emailAddress
decl_stmt|;
annotation|@
name|Column
argument_list|(
name|notNull
operator|=
literal|false
argument_list|)
DECL|field|lastUsedOn
specifier|protected
name|Timestamp
name|lastUsedOn
decl_stmt|;
comment|/**<i>computed value</i> is this identity trusted by the site administrator? */
DECL|field|trusted
specifier|protected
name|boolean
name|trusted
decl_stmt|;
comment|/**<i>computed value</i> can this identity be removed from the account? */
DECL|field|canDelete
specifier|protected
name|boolean
name|canDelete
decl_stmt|;
DECL|method|AccountExternalId ()
specifier|protected
name|AccountExternalId
parameter_list|()
block|{   }
comment|/**    * Create a new binding to an external identity.    *    * @param who the account this binds to.    * @param k the binding key.    */
DECL|method|AccountExternalId (final Account.Id who, final AccountExternalId.Key k)
specifier|public
name|AccountExternalId
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|who
parameter_list|,
specifier|final
name|AccountExternalId
operator|.
name|Key
name|k
parameter_list|)
block|{
name|accountId
operator|=
name|who
expr_stmt|;
name|key
operator|=
name|k
expr_stmt|;
block|}
DECL|method|getKey ()
specifier|public
name|AccountExternalId
operator|.
name|Key
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
comment|/** Get local id of this account, to link with in other entities */
DECL|method|getAccountId ()
specifier|public
name|Account
operator|.
name|Id
name|getAccountId
parameter_list|()
block|{
return|return
name|accountId
return|;
block|}
DECL|method|getExternalId ()
specifier|public
name|String
name|getExternalId
parameter_list|()
block|{
return|return
name|key
operator|.
name|externalId
return|;
block|}
DECL|method|getEmailAddress ()
specifier|public
name|String
name|getEmailAddress
parameter_list|()
block|{
return|return
name|emailAddress
return|;
block|}
DECL|method|setEmailAddress (final String e)
specifier|public
name|void
name|setEmailAddress
parameter_list|(
specifier|final
name|String
name|e
parameter_list|)
block|{
name|emailAddress
operator|=
name|e
expr_stmt|;
block|}
DECL|method|getLastUsedOn ()
specifier|public
name|Timestamp
name|getLastUsedOn
parameter_list|()
block|{
return|return
name|lastUsedOn
return|;
block|}
DECL|method|setLastUsedOn ()
specifier|public
name|void
name|setLastUsedOn
parameter_list|()
block|{
name|lastUsedOn
operator|=
operator|new
name|Timestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|isScheme (final String scheme)
specifier|public
name|boolean
name|isScheme
parameter_list|(
specifier|final
name|String
name|scheme
parameter_list|)
block|{
specifier|final
name|String
name|id
init|=
name|getExternalId
argument_list|()
decl_stmt|;
return|return
name|id
operator|!=
literal|null
operator|&&
name|id
operator|.
name|startsWith
argument_list|(
name|scheme
argument_list|)
return|;
block|}
DECL|method|getSchemeRest (final String scheme)
specifier|public
name|String
name|getSchemeRest
parameter_list|(
specifier|final
name|String
name|scheme
parameter_list|)
block|{
return|return
name|isScheme
argument_list|(
name|scheme
argument_list|)
condition|?
name|getExternalId
argument_list|()
operator|.
name|substring
argument_list|(
name|scheme
operator|.
name|length
argument_list|()
argument_list|)
else|:
literal|null
return|;
block|}
DECL|method|isTrusted ()
specifier|public
name|boolean
name|isTrusted
parameter_list|()
block|{
return|return
name|trusted
return|;
block|}
DECL|method|setTrusted (final boolean t)
specifier|public
name|void
name|setTrusted
parameter_list|(
specifier|final
name|boolean
name|t
parameter_list|)
block|{
name|trusted
operator|=
name|t
expr_stmt|;
block|}
DECL|method|canDelete ()
specifier|public
name|boolean
name|canDelete
parameter_list|()
block|{
return|return
name|canDelete
return|;
block|}
DECL|method|setCanDelete (final boolean t)
specifier|public
name|void
name|setCanDelete
parameter_list|(
specifier|final
name|boolean
name|t
parameter_list|)
block|{
name|canDelete
operator|=
name|t
expr_stmt|;
block|}
block|}
end_class

end_unit

