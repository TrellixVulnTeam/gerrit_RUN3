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
DECL|package|com.google.gerrit.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
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
name|client
operator|.
name|account
operator|.
name|AccountInfo
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
name|AccountGeneralPreferences
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|i18n
operator|.
name|client
operator|.
name|DateTimeFormat
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

begin_comment
comment|/** Misc. formatting functions. */
end_comment

begin_class
DECL|class|FormatUtil
specifier|public
class|class
name|FormatUtil
block|{
DECL|field|ONE_YEAR
specifier|private
specifier|static
specifier|final
name|long
name|ONE_YEAR
init|=
literal|182L
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
DECL|field|sTime
specifier|private
specifier|static
name|DateTimeFormat
name|sTime
decl_stmt|;
DECL|field|sDate
specifier|private
specifier|static
name|DateTimeFormat
name|sDate
decl_stmt|;
DECL|field|sdtFmt
specifier|private
specifier|static
name|DateTimeFormat
name|sdtFmt
decl_stmt|;
DECL|field|mDate
specifier|private
specifier|static
name|DateTimeFormat
name|mDate
decl_stmt|;
DECL|field|dtfmt
specifier|private
specifier|static
name|DateTimeFormat
name|dtfmt
decl_stmt|;
DECL|method|setPreferences (AccountGeneralPreferences pref)
specifier|public
specifier|static
name|void
name|setPreferences
parameter_list|(
name|AccountGeneralPreferences
name|pref
parameter_list|)
block|{
if|if
condition|(
name|pref
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
name|pref
operator|=
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getGeneralPreferences
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|pref
operator|=
operator|new
name|AccountGeneralPreferences
argument_list|()
expr_stmt|;
name|pref
operator|.
name|resetToDefaults
argument_list|()
expr_stmt|;
block|}
block|}
name|String
name|fmt_sTime
init|=
name|pref
operator|.
name|getTimeFormat
argument_list|()
operator|.
name|getFormat
argument_list|()
decl_stmt|;
name|String
name|fmt_sDate
init|=
name|pref
operator|.
name|getDateFormat
argument_list|()
operator|.
name|getShortFormat
argument_list|()
decl_stmt|;
name|String
name|fmt_mDate
init|=
name|pref
operator|.
name|getDateFormat
argument_list|()
operator|.
name|getLongFormat
argument_list|()
decl_stmt|;
name|sTime
operator|=
name|DateTimeFormat
operator|.
name|getFormat
argument_list|(
name|fmt_sTime
argument_list|)
expr_stmt|;
name|sDate
operator|=
name|DateTimeFormat
operator|.
name|getFormat
argument_list|(
name|fmt_sDate
argument_list|)
expr_stmt|;
name|sdtFmt
operator|=
name|DateTimeFormat
operator|.
name|getFormat
argument_list|(
name|fmt_sDate
operator|+
literal|" "
operator|+
name|fmt_sTime
argument_list|)
expr_stmt|;
name|mDate
operator|=
name|DateTimeFormat
operator|.
name|getFormat
argument_list|(
name|fmt_mDate
argument_list|)
expr_stmt|;
name|dtfmt
operator|=
name|DateTimeFormat
operator|.
name|getFormat
argument_list|(
name|fmt_mDate
operator|+
literal|" "
operator|+
name|fmt_sTime
argument_list|)
expr_stmt|;
block|}
comment|/** Format a date using a really short format. */
DECL|method|shortFormat (Date dt)
specifier|public
specifier|static
name|String
name|shortFormat
parameter_list|(
name|Date
name|dt
parameter_list|)
block|{
if|if
condition|(
name|dt
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
name|ensureInited
argument_list|()
expr_stmt|;
specifier|final
name|Date
name|now
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|dt
operator|=
operator|new
name|Date
argument_list|(
name|dt
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|mDate
operator|.
name|format
argument_list|(
name|now
argument_list|)
operator|.
name|equals
argument_list|(
name|mDate
operator|.
name|format
argument_list|(
name|dt
argument_list|)
argument_list|)
condition|)
block|{
comment|// Same day as today, report only the time.
comment|//
return|return
name|sTime
operator|.
name|format
argument_list|(
name|dt
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|now
operator|.
name|getTime
argument_list|()
operator|-
name|dt
operator|.
name|getTime
argument_list|()
argument_list|)
operator|<
name|ONE_YEAR
condition|)
block|{
comment|// Within the last year, show a shorter date.
comment|//
return|return
name|sDate
operator|.
name|format
argument_list|(
name|dt
argument_list|)
return|;
block|}
else|else
block|{
comment|// Report only date and year, its far away from now.
comment|//
return|return
name|mDate
operator|.
name|format
argument_list|(
name|dt
argument_list|)
return|;
block|}
block|}
comment|/** Format a date using a really short format. */
DECL|method|shortFormatDayTime (Date dt)
specifier|public
specifier|static
name|String
name|shortFormatDayTime
parameter_list|(
name|Date
name|dt
parameter_list|)
block|{
if|if
condition|(
name|dt
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
name|ensureInited
argument_list|()
expr_stmt|;
specifier|final
name|Date
name|now
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|dt
operator|=
operator|new
name|Date
argument_list|(
name|dt
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|mDate
operator|.
name|format
argument_list|(
name|now
argument_list|)
operator|.
name|equals
argument_list|(
name|mDate
operator|.
name|format
argument_list|(
name|dt
argument_list|)
argument_list|)
condition|)
block|{
comment|// Same day as today, report only the time.
comment|//
return|return
name|sTime
operator|.
name|format
argument_list|(
name|dt
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|now
operator|.
name|getTime
argument_list|()
operator|-
name|dt
operator|.
name|getTime
argument_list|()
argument_list|)
operator|<
name|ONE_YEAR
condition|)
block|{
comment|// Within the last year, show a shorter date.
comment|//
return|return
name|sdtFmt
operator|.
name|format
argument_list|(
name|dt
argument_list|)
return|;
block|}
else|else
block|{
comment|// Report only date and year, its far away from now.
comment|//
return|return
name|mDate
operator|.
name|format
argument_list|(
name|dt
argument_list|)
return|;
block|}
block|}
comment|/** Format a date using the locale's medium length format. */
DECL|method|mediumFormat (final Date dt)
specifier|public
specifier|static
name|String
name|mediumFormat
parameter_list|(
specifier|final
name|Date
name|dt
parameter_list|)
block|{
if|if
condition|(
name|dt
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
name|ensureInited
argument_list|()
expr_stmt|;
return|return
name|dtfmt
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|dt
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|ensureInited ()
specifier|private
specifier|static
name|void
name|ensureInited
parameter_list|()
block|{
if|if
condition|(
name|dtfmt
operator|==
literal|null
condition|)
block|{
name|setPreferences
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Format a date using git log's relative date format. */
DECL|method|relativeFormat (Date dt)
specifier|public
specifier|static
name|String
name|relativeFormat
parameter_list|(
name|Date
name|dt
parameter_list|)
block|{
return|return
name|RelativeDateFormatter
operator|.
name|format
argument_list|(
name|dt
argument_list|)
return|;
block|}
comment|/**    * Formats an account as a name and an email address.    *<p>    * Example output:    *<ul>    *<li>{@code A U. Thor&lt;author@example.com&gt;}: full populated</li>    *<li>{@code A U. Thor (12)}: missing email address</li>    *<li>{@code Anonymous Coward&lt;author@example.com&gt;}: missing name</li>    *<li>{@code Anonymous Coward (12)}: missing name and email address</li>    *</ul>    */
DECL|method|nameEmail (AccountInfo info)
specifier|public
specifier|static
name|String
name|nameEmail
parameter_list|(
name|AccountInfo
name|info
parameter_list|)
block|{
name|String
name|name
init|=
name|info
operator|.
name|name
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|name
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|name
operator|=
name|Gerrit
operator|.
name|info
argument_list|()
operator|.
name|user
argument_list|()
operator|.
name|anonymousCowardName
argument_list|()
expr_stmt|;
block|}
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|email
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
operator|.
name|append
argument_list|(
name|info
operator|.
name|email
argument_list|()
argument_list|)
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
name|info
operator|.
name|_accountId
argument_list|()
operator|>
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" ("
argument_list|)
operator|.
name|append
argument_list|(
name|info
operator|.
name|_accountId
argument_list|()
argument_list|)
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
comment|/**    * Formats an account name.    *<p>    * If the account has a full name, it returns only the full name. Otherwise it    * returns a longer form that includes the email address.    */
DECL|method|name (Account acct)
specifier|public
specifier|static
name|String
name|name
parameter_list|(
name|Account
name|acct
parameter_list|)
block|{
return|return
name|name
argument_list|(
name|asInfo
argument_list|(
name|acct
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Formats an account name.    *<p>    * If the account has a full name, it returns only the full name. Otherwise it    * returns a longer form that includes the email address.    */
DECL|method|name (AccountInfo ai)
specifier|public
specifier|static
name|String
name|name
parameter_list|(
name|AccountInfo
name|ai
parameter_list|)
block|{
if|if
condition|(
name|ai
operator|.
name|name
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|ai
operator|.
name|name
argument_list|()
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|ai
operator|.
name|name
argument_list|()
return|;
block|}
name|String
name|email
init|=
name|ai
operator|.
name|email
argument_list|()
decl_stmt|;
if|if
condition|(
name|email
operator|!=
literal|null
condition|)
block|{
name|int
name|at
init|=
name|email
operator|.
name|indexOf
argument_list|(
literal|'@'
argument_list|)
decl_stmt|;
return|return
literal|0
operator|<
name|at
condition|?
name|email
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|at
argument_list|)
else|:
name|email
return|;
block|}
return|return
name|nameEmail
argument_list|(
name|ai
argument_list|)
return|;
block|}
DECL|method|asInfo (Account acct)
specifier|public
specifier|static
name|AccountInfo
name|asInfo
parameter_list|(
name|Account
name|acct
parameter_list|)
block|{
if|if
condition|(
name|acct
operator|==
literal|null
condition|)
block|{
return|return
name|AccountInfo
operator|.
name|create
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
return|return
name|AccountInfo
operator|.
name|create
argument_list|(
name|acct
operator|.
name|getId
argument_list|()
operator|!=
literal|null
condition|?
name|acct
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
else|:
literal|0
argument_list|,
name|acct
operator|.
name|getFullName
argument_list|()
argument_list|,
name|acct
operator|.
name|getPreferredEmail
argument_list|()
argument_list|,
name|acct
operator|.
name|getUserName
argument_list|()
argument_list|)
return|;
block|}
DECL|method|asInfo (com.google.gerrit.common.data.AccountInfo acct)
specifier|public
specifier|static
name|AccountInfo
name|asInfo
parameter_list|(
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
name|AccountInfo
name|acct
parameter_list|)
block|{
if|if
condition|(
name|acct
operator|==
literal|null
condition|)
block|{
return|return
name|AccountInfo
operator|.
name|create
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
return|return
name|AccountInfo
operator|.
name|create
argument_list|(
name|acct
operator|.
name|getId
argument_list|()
operator|!=
literal|null
condition|?
name|acct
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
else|:
literal|0
argument_list|,
name|acct
operator|.
name|getFullName
argument_list|()
argument_list|,
name|acct
operator|.
name|getPreferredEmail
argument_list|()
argument_list|,
name|acct
operator|.
name|getUsername
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

