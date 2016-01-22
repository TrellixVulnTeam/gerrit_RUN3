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
name|change
operator|.
name|Resources
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
name|client
operator|.
name|info
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
name|client
operator|.
name|info
operator|.
name|GeneralPreferences
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
name|gwt
operator|.
name|i18n
operator|.
name|client
operator|.
name|NumberFormat
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
DECL|field|dateFormatter
specifier|private
specifier|static
name|DateFormatter
name|dateFormatter
decl_stmt|;
DECL|method|setPreferences (GeneralPreferences prefs)
specifier|public
specifier|static
name|void
name|setPreferences
parameter_list|(
name|GeneralPreferences
name|prefs
parameter_list|)
block|{
name|dateFormatter
operator|=
operator|new
name|DateFormatter
argument_list|(
name|prefs
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
name|ensureInited
argument_list|()
expr_stmt|;
return|return
name|dateFormatter
operator|.
name|shortFormat
argument_list|(
name|dt
argument_list|)
return|;
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
name|ensureInited
argument_list|()
expr_stmt|;
return|return
name|dateFormatter
operator|.
name|shortFormatDayTime
argument_list|(
name|dt
argument_list|)
return|;
block|}
comment|/** Format a date using the locale's medium length format. */
DECL|method|mediumFormat (Date dt)
specifier|public
specifier|static
name|String
name|mediumFormat
parameter_list|(
name|Date
name|dt
parameter_list|)
block|{
name|ensureInited
argument_list|()
expr_stmt|;
return|return
name|dateFormatter
operator|.
name|mediumFormat
argument_list|(
name|dt
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
name|dateFormatter
operator|==
literal|null
condition|)
block|{
name|setPreferences
argument_list|(
name|Gerrit
operator|.
name|getUserPreferences
argument_list|()
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
return|return
name|createAccountFormatter
argument_list|()
operator|.
name|nameEmail
argument_list|(
name|info
argument_list|)
return|;
block|}
comment|/**    * Formats an account name.    *<p>    * If the account has a full name, it returns only the full name. Otherwise it    * returns a longer form that includes the email address.    */
DECL|method|name (AccountInfo info)
specifier|public
specifier|static
name|String
name|name
parameter_list|(
name|AccountInfo
name|info
parameter_list|)
block|{
return|return
name|createAccountFormatter
argument_list|()
operator|.
name|name
argument_list|(
name|info
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
DECL|method|createAccountFormatter ()
specifier|private
specifier|static
name|AccountFormatter
name|createAccountFormatter
parameter_list|()
block|{
return|return
operator|new
name|AccountFormatter
argument_list|(
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
argument_list|)
return|;
block|}
comment|/** The returned format string doesn't contain any +/- sign. */
DECL|method|formatAbsBytes (long bytes)
specifier|public
specifier|static
name|String
name|formatAbsBytes
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
return|return
name|formatBytes
argument_list|(
name|bytes
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|formatBytes (long bytes)
specifier|public
specifier|static
name|String
name|formatBytes
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
return|return
name|formatBytes
argument_list|(
name|bytes
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|formatBytes (long bytes, boolean abs)
specifier|private
specifier|static
name|String
name|formatBytes
parameter_list|(
name|long
name|bytes
parameter_list|,
name|boolean
name|abs
parameter_list|)
block|{
name|bytes
operator|=
name|abs
condition|?
name|Math
operator|.
name|abs
argument_list|(
name|bytes
argument_list|)
else|:
name|bytes
expr_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|0
condition|)
block|{
return|return
name|abs
condition|?
literal|"0 B"
else|:
literal|"+/- 0 B"
return|;
block|}
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|bytes
argument_list|)
operator|<
literal|1024
condition|)
block|{
return|return
operator|(
name|bytes
operator|>
literal|0
operator|&&
operator|!
name|abs
condition|?
literal|"+"
else|:
literal|""
operator|)
operator|+
name|bytes
operator|+
literal|" B"
return|;
block|}
name|int
name|exp
init|=
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|bytes
argument_list|)
argument_list|)
operator|/
name|Math
operator|.
name|log
argument_list|(
literal|1024
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|(
name|bytes
operator|>
literal|0
operator|&&
operator|!
name|abs
condition|?
literal|"+"
else|:
literal|""
operator|)
operator|+
name|NumberFormat
operator|.
name|getFormat
argument_list|(
literal|"#.0"
argument_list|)
operator|.
name|format
argument_list|(
name|bytes
operator|/
name|Math
operator|.
name|pow
argument_list|(
literal|1024
argument_list|,
name|exp
argument_list|)
argument_list|)
operator|+
literal|" "
operator|+
literal|"KMGTPE"
operator|.
name|charAt
argument_list|(
name|exp
operator|-
literal|1
argument_list|)
operator|+
literal|"iB"
return|;
block|}
DECL|method|formatPercentage (long size, long delta)
specifier|public
specifier|static
name|String
name|formatPercentage
parameter_list|(
name|long
name|size
parameter_list|,
name|long
name|delta
parameter_list|)
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
name|Resources
operator|.
name|C
operator|.
name|notAvailable
argument_list|()
return|;
block|}
return|return
operator|(
name|delta
operator|>
literal|0
condition|?
literal|"+"
else|:
literal|"-"
operator|)
operator|+
name|formatAbsPercentage
argument_list|(
name|size
argument_list|,
name|delta
argument_list|)
return|;
block|}
DECL|method|formatAbsPercentage (long size, long delta)
specifier|public
specifier|static
name|String
name|formatAbsPercentage
parameter_list|(
name|long
name|size
parameter_list|,
name|long
name|delta
parameter_list|)
block|{
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
name|Resources
operator|.
name|C
operator|.
name|notAvailable
argument_list|()
return|;
block|}
name|int
name|p
init|=
name|Math
operator|.
name|abs
argument_list|(
name|Math
operator|.
name|round
argument_list|(
name|delta
operator|*
literal|100
operator|/
name|size
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|p
operator|+
literal|"%"
return|;
block|}
block|}
end_class

end_unit

