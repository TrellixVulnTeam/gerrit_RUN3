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
DECL|package|com.google.gerrit.server.account
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
name|AccountExternalId
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
name|ReviewDb
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
DECL|class|AccountResolver
specifier|public
class|class
name|AccountResolver
block|{
DECL|field|realm
specifier|private
specifier|final
name|Realm
name|realm
decl_stmt|;
DECL|field|byEmail
specifier|private
specifier|final
name|AccountByEmailCache
name|byEmail
decl_stmt|;
DECL|field|byId
specifier|private
specifier|final
name|AccountCache
name|byId
decl_stmt|;
DECL|field|schema
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
annotation|@
name|Inject
DECL|method|AccountResolver (final Realm realm, final AccountByEmailCache byEmail, final AccountCache byId, final Provider<ReviewDb> schema)
name|AccountResolver
parameter_list|(
specifier|final
name|Realm
name|realm
parameter_list|,
specifier|final
name|AccountByEmailCache
name|byEmail
parameter_list|,
specifier|final
name|AccountCache
name|byId
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|schema
parameter_list|)
block|{
name|this
operator|.
name|realm
operator|=
name|realm
expr_stmt|;
name|this
operator|.
name|byEmail
operator|=
name|byEmail
expr_stmt|;
name|this
operator|.
name|byId
operator|=
name|byId
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
block|}
comment|/**    * Locate exactly one account matching the name or name/email string.    *    * @param nameOrEmail a string of the format    *        "Full Name&lt;email@example&gt;", just the email address    *        ("email@example"), a full name ("Full Name"), an account id    *        ("18419") or an user name ("username").    * @return the single account that matches; null if no account matches or    *         there are multiple candidates.    */
DECL|method|find (final String nameOrEmail)
specifier|public
name|Account
name|find
parameter_list|(
specifier|final
name|String
name|nameOrEmail
parameter_list|)
throws|throws
name|OrmException
block|{
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|r
init|=
name|findAll
argument_list|(
name|nameOrEmail
argument_list|)
decl_stmt|;
return|return
name|r
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
name|byId
operator|.
name|get
argument_list|(
name|r
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|getAccount
argument_list|()
else|:
literal|null
return|;
block|}
comment|/**    * Locate exactly one account matching the name or name/email string.    *    * @param nameOrEmail a string of the format    *        "Full Name&lt;email@example&gt;", just the email address    *        ("email@example"), a full name ("Full Name"), an account id    *        ("18419") or an user name ("username").    * @return the accounts that match, empty collection if none.  Never null.    */
DECL|method|findAll (String nameOrEmail)
specifier|public
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|findAll
parameter_list|(
name|String
name|nameOrEmail
parameter_list|)
throws|throws
name|OrmException
block|{
name|Matcher
name|m
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^.* \\(([1-9][0-9]*)\\)$"
argument_list|)
operator|.
name|matcher
argument_list|(
name|nameOrEmail
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|Account
operator|.
name|Id
operator|.
name|parse
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|nameOrEmail
operator|.
name|matches
argument_list|(
literal|"^[1-9][0-9]*$"
argument_list|)
condition|)
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|Account
operator|.
name|Id
operator|.
name|parse
argument_list|(
name|nameOrEmail
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|nameOrEmail
operator|.
name|matches
argument_list|(
name|Account
operator|.
name|USER_NAME_PATTERN
argument_list|)
condition|)
block|{
name|AccountState
name|who
init|=
name|byId
operator|.
name|getByUsername
argument_list|(
name|nameOrEmail
argument_list|)
decl_stmt|;
if|if
condition|(
name|who
operator|!=
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|who
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
name|findAllByNameOrEmail
argument_list|(
name|nameOrEmail
argument_list|)
return|;
block|}
comment|/**    * Locate exactly one account matching the name or name/email string.    *    * @param nameOrEmail a string of the format    *        "Full Name&lt;email@example&gt;", just the email address    *        ("email@example"), a full name ("Full Name").    * @return the single account that matches; null if no account matches or    *         there are multiple candidates.    */
DECL|method|findByNameOrEmail (final String nameOrEmail)
specifier|public
name|Account
name|findByNameOrEmail
parameter_list|(
specifier|final
name|String
name|nameOrEmail
parameter_list|)
throws|throws
name|OrmException
block|{
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|r
init|=
name|findAllByNameOrEmail
argument_list|(
name|nameOrEmail
argument_list|)
decl_stmt|;
return|return
name|r
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
name|byId
operator|.
name|get
argument_list|(
name|r
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|getAccount
argument_list|()
else|:
literal|null
return|;
block|}
comment|/**    * Locate exactly one account matching the name or name/email string.    *    * @param nameOrEmail a string of the format    *        "Full Name&lt;email@example&gt;", just the email address    *        ("email@example"), a full name ("Full Name").    * @return the accounts that match, empty collection if none. Never null.    */
DECL|method|findAllByNameOrEmail (final String nameOrEmail)
specifier|public
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|findAllByNameOrEmail
parameter_list|(
specifier|final
name|String
name|nameOrEmail
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|int
name|lt
init|=
name|nameOrEmail
operator|.
name|indexOf
argument_list|(
literal|'<'
argument_list|)
decl_stmt|;
specifier|final
name|int
name|gt
init|=
name|nameOrEmail
operator|.
name|indexOf
argument_list|(
literal|'>'
argument_list|)
decl_stmt|;
if|if
condition|(
name|lt
operator|>=
literal|0
operator|&&
name|gt
operator|>
name|lt
operator|&&
name|nameOrEmail
operator|.
name|contains
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
return|return
name|byEmail
operator|.
name|get
argument_list|(
name|nameOrEmail
operator|.
name|substring
argument_list|(
name|lt
operator|+
literal|1
argument_list|,
name|gt
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|nameOrEmail
operator|.
name|contains
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
return|return
name|byEmail
operator|.
name|get
argument_list|(
name|nameOrEmail
argument_list|)
return|;
block|}
specifier|final
name|Account
operator|.
name|Id
name|id
init|=
name|realm
operator|.
name|lookup
argument_list|(
name|nameOrEmail
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|id
argument_list|)
return|;
block|}
name|List
argument_list|<
name|Account
argument_list|>
name|m
init|=
name|schema
operator|.
name|get
argument_list|()
operator|.
name|accounts
argument_list|()
operator|.
name|byFullName
argument_list|(
name|nameOrEmail
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
comment|// At this point we have no clue. Just perform a whole bunch of suggestions
comment|// and pray we come up with a reasonable result list.
comment|//
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|result
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
name|String
name|a
init|=
name|nameOrEmail
decl_stmt|;
name|String
name|b
init|=
name|nameOrEmail
operator|+
literal|"\u9fa5"
decl_stmt|;
for|for
control|(
name|Account
name|act
range|:
name|schema
operator|.
name|get
argument_list|()
operator|.
name|accounts
argument_list|()
operator|.
name|suggestByFullName
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
literal|10
argument_list|)
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|act
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|AccountExternalId
name|extId
range|:
name|schema
operator|.
name|get
argument_list|()
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|suggestByKey
argument_list|(
operator|new
name|AccountExternalId
operator|.
name|Key
argument_list|(
name|AccountExternalId
operator|.
name|SCHEME_USERNAME
argument_list|,
name|a
argument_list|)
argument_list|,
operator|new
name|AccountExternalId
operator|.
name|Key
argument_list|(
name|AccountExternalId
operator|.
name|SCHEME_USERNAME
argument_list|,
name|b
argument_list|)
argument_list|,
literal|10
argument_list|)
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|extId
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|AccountExternalId
name|extId
range|:
name|schema
operator|.
name|get
argument_list|()
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|suggestByEmailAddress
argument_list|(
name|a
argument_list|,
name|b
argument_list|,
literal|10
argument_list|)
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|extId
operator|.
name|getAccountId
argument_list|()
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

