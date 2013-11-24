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
DECL|package|com.google.gerrit.common.data
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|data
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
name|client
operator|.
name|Account
import|;
end_import

begin_comment
comment|/** Summary information about an {@link Account}, for simple tabular displays. */
end_comment

begin_class
DECL|class|AccountInfo
specifier|public
class|class
name|AccountInfo
block|{
DECL|field|id
specifier|protected
name|Account
operator|.
name|Id
name|id
decl_stmt|;
DECL|field|fullName
specifier|protected
name|String
name|fullName
decl_stmt|;
DECL|field|preferredEmail
specifier|protected
name|String
name|preferredEmail
decl_stmt|;
DECL|method|AccountInfo ()
specifier|protected
name|AccountInfo
parameter_list|()
block|{   }
comment|/**    * Create an 'Anonymous Coward' account info, when only the id is known.    *<p>    * This constructor should only be a last-ditch effort, when the usual account    * lookup has failed and a stale account id has been discovered in the data    * store.    */
DECL|method|AccountInfo (final Account.Id id)
specifier|public
name|AccountInfo
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
comment|/**    * Create an account description from a real data store record.    *    * @param a the data store record holding the specific account details.    */
DECL|method|AccountInfo (final Account a)
specifier|public
name|AccountInfo
parameter_list|(
specifier|final
name|Account
name|a
parameter_list|)
block|{
name|id
operator|=
name|a
operator|.
name|getId
argument_list|()
expr_stmt|;
name|fullName
operator|=
name|a
operator|.
name|getFullName
argument_list|()
expr_stmt|;
name|preferredEmail
operator|=
name|a
operator|.
name|getPreferredEmail
argument_list|()
expr_stmt|;
block|}
comment|/** @return the unique local id of the account */
DECL|method|getId ()
specifier|public
name|Account
operator|.
name|Id
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/** @return the full name of the account holder; null if not supplied */
DECL|method|getFullName ()
specifier|public
name|String
name|getFullName
parameter_list|()
block|{
return|return
name|fullName
return|;
block|}
comment|/** @return the email address of the account holder; null if not supplied */
DECL|method|getPreferredEmail ()
specifier|public
name|String
name|getPreferredEmail
parameter_list|()
block|{
return|return
name|preferredEmail
return|;
block|}
DECL|method|setPreferredEmail (final String email)
specifier|public
name|void
name|setPreferredEmail
parameter_list|(
specifier|final
name|String
name|email
parameter_list|)
block|{
name|preferredEmail
operator|=
name|email
expr_stmt|;
block|}
comment|/**    * Formats an account name.    *<p>    * If the account has a full name, it returns only the full name. Otherwise it    * returns a longer form that includes the email address.    */
DECL|method|getName (String anonymousCowardName)
specifier|public
name|String
name|getName
parameter_list|(
name|String
name|anonymousCowardName
parameter_list|)
block|{
if|if
condition|(
name|getFullName
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|getFullName
argument_list|()
return|;
block|}
if|if
condition|(
name|getPreferredEmail
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|getPreferredEmail
argument_list|()
return|;
block|}
return|return
name|getNameEmail
argument_list|(
name|anonymousCowardName
argument_list|)
return|;
block|}
comment|/**    * Formats an account as an name and an email address.    *<p>    * Example output:    *<ul>    *<li>{@code A U. Thor&lt;author@example.com&gt;}: full populated</li>    *<li>{@code A U. Thor (12)}: missing email address</li>    *<li>{@code Anonymous Coward&lt;author@example.com&gt;}: missing name</li>    *<li>{@code Anonymous Coward (12)}: missing name and email address</li>    *</ul>    */
DECL|method|getNameEmail (String anonymousCowardName)
specifier|public
name|String
name|getNameEmail
parameter_list|(
name|String
name|anonymousCowardName
parameter_list|)
block|{
name|String
name|name
init|=
name|getFullName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|name
operator|=
name|anonymousCowardName
expr_stmt|;
block|}
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|getPreferredEmail
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"<"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|getPreferredEmail
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|getId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" ("
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

