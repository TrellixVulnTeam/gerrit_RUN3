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
name|extensions
operator|.
name|restapi
operator|.
name|AuthException
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
name|restapi
operator|.
name|RestReadView
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
name|AccountDiffPreference
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
name|AccountDiffPreference
operator|.
name|Whitespace
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
name|ReviewDb
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
name|CurrentUser
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
name|server
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

begin_class
DECL|class|GetDiffPreferences
specifier|public
class|class
name|GetDiffPreferences
implements|implements
name|RestReadView
argument_list|<
name|AccountResource
argument_list|>
block|{
DECL|field|self
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
annotation|@
name|Inject
DECL|method|GetDiffPreferences (Provider<CurrentUser> self, Provider<ReviewDb> db)
name|GetDiffPreferences
parameter_list|(
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
block|{
name|this
operator|.
name|self
operator|=
name|self
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (AccountResource rsrc)
specifier|public
name|DiffPreferencesInfo
name|apply
parameter_list|(
name|AccountResource
name|rsrc
parameter_list|)
throws|throws
name|AuthException
throws|,
name|OrmException
block|{
if|if
condition|(
name|self
operator|.
name|get
argument_list|()
operator|!=
name|rsrc
operator|.
name|getUser
argument_list|()
operator|&&
operator|!
name|self
operator|.
name|get
argument_list|()
operator|.
name|getCapabilities
argument_list|()
operator|.
name|canAdministrateServer
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"restricted to administrator"
argument_list|)
throw|;
block|}
name|Account
operator|.
name|Id
name|userId
init|=
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
name|AccountDiffPreference
name|a
init|=
name|db
operator|.
name|get
argument_list|()
operator|.
name|accountDiffPreferences
argument_list|()
operator|.
name|get
argument_list|(
name|userId
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|==
literal|null
condition|)
block|{
name|a
operator|=
operator|new
name|AccountDiffPreference
argument_list|(
name|userId
argument_list|)
expr_stmt|;
block|}
return|return
name|DiffPreferencesInfo
operator|.
name|parse
argument_list|(
name|a
argument_list|)
return|;
block|}
DECL|class|DiffPreferencesInfo
specifier|static
class|class
name|DiffPreferencesInfo
block|{
DECL|method|parse (AccountDiffPreference p)
specifier|static
name|DiffPreferencesInfo
name|parse
parameter_list|(
name|AccountDiffPreference
name|p
parameter_list|)
block|{
name|DiffPreferencesInfo
name|info
init|=
operator|new
name|DiffPreferencesInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|context
operator|=
name|p
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|info
operator|.
name|expandAllComments
operator|=
name|p
operator|.
name|isExpandAllComments
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|info
operator|.
name|ignoreWhitespace
operator|=
name|p
operator|.
name|getIgnoreWhitespace
argument_list|()
expr_stmt|;
name|info
operator|.
name|intralineDifference
operator|=
name|p
operator|.
name|isIntralineDifference
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|info
operator|.
name|lineLength
operator|=
name|p
operator|.
name|getLineLength
argument_list|()
expr_stmt|;
name|info
operator|.
name|manualReview
operator|=
name|p
operator|.
name|isManualReview
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|info
operator|.
name|retainHeader
operator|=
name|p
operator|.
name|isRetainHeader
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|info
operator|.
name|showLineEndings
operator|=
name|p
operator|.
name|isShowLineEndings
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|info
operator|.
name|showTabs
operator|=
name|p
operator|.
name|isShowTabs
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|info
operator|.
name|showWhitespaceErrors
operator|=
name|p
operator|.
name|isShowWhitespaceErrors
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|info
operator|.
name|skipDeleted
operator|=
name|p
operator|.
name|isSkipDeleted
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|info
operator|.
name|skipUncommented
operator|=
name|p
operator|.
name|isSkipUncommented
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|info
operator|.
name|hideTopMenu
operator|=
name|p
operator|.
name|isHideTopMenu
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|info
operator|.
name|syntaxHighlighting
operator|=
name|p
operator|.
name|isSyntaxHighlighting
argument_list|()
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|info
operator|.
name|tabSize
operator|=
name|p
operator|.
name|getTabSize
argument_list|()
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|field|context
name|short
name|context
decl_stmt|;
DECL|field|expandAllComments
name|Boolean
name|expandAllComments
decl_stmt|;
DECL|field|ignoreWhitespace
name|Whitespace
name|ignoreWhitespace
decl_stmt|;
DECL|field|intralineDifference
name|Boolean
name|intralineDifference
decl_stmt|;
DECL|field|lineLength
name|int
name|lineLength
decl_stmt|;
DECL|field|manualReview
name|Boolean
name|manualReview
decl_stmt|;
DECL|field|retainHeader
name|Boolean
name|retainHeader
decl_stmt|;
DECL|field|showLineEndings
name|Boolean
name|showLineEndings
decl_stmt|;
DECL|field|showTabs
name|Boolean
name|showTabs
decl_stmt|;
DECL|field|showWhitespaceErrors
name|Boolean
name|showWhitespaceErrors
decl_stmt|;
DECL|field|skipDeleted
name|Boolean
name|skipDeleted
decl_stmt|;
DECL|field|skipUncommented
name|Boolean
name|skipUncommented
decl_stmt|;
DECL|field|syntaxHighlighting
name|Boolean
name|syntaxHighlighting
decl_stmt|;
DECL|field|hideTopMenu
name|Boolean
name|hideTopMenu
decl_stmt|;
DECL|field|tabSize
name|int
name|tabSize
decl_stmt|;
block|}
block|}
end_class

end_unit

