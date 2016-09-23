begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.extensions.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|client
package|;
end_package

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

begin_comment
comment|/** Preferences about a single user. */
end_comment

begin_class
DECL|class|GeneralPreferencesInfo
specifier|public
class|class
name|GeneralPreferencesInfo
block|{
comment|/** Default number of items to display per page. */
DECL|field|DEFAULT_PAGESIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_PAGESIZE
init|=
literal|25
decl_stmt|;
comment|/** Valid choices for the page size. */
DECL|field|PAGESIZE_CHOICES
specifier|public
specifier|static
specifier|final
name|int
index|[]
name|PAGESIZE_CHOICES
init|=
block|{
literal|10
block|,
literal|25
block|,
literal|50
block|,
literal|100
block|}
decl_stmt|;
comment|/** Preferred method to download a change. */
DECL|enum|DownloadCommand
specifier|public
enum|enum
name|DownloadCommand
block|{
DECL|enumConstant|REPO_DOWNLOAD
DECL|enumConstant|PULL
DECL|enumConstant|CHECKOUT
DECL|enumConstant|CHERRY_PICK
DECL|enumConstant|FORMAT_PATCH
name|REPO_DOWNLOAD
block|,
name|PULL
block|,
name|CHECKOUT
block|,
name|CHERRY_PICK
block|,
name|FORMAT_PATCH
block|}
DECL|enum|DateFormat
specifier|public
enum|enum
name|DateFormat
block|{
comment|/** US style dates: Apr 27, Feb 14, 2010 */
DECL|enumConstant|STD
name|STD
argument_list|(
literal|"MMM d"
argument_list|,
literal|"MMM d, yyyy"
argument_list|)
block|,
comment|/** US style dates: 04/27, 02/14/10 */
DECL|enumConstant|US
name|US
argument_list|(
literal|"MM/dd"
argument_list|,
literal|"MM/dd/yy"
argument_list|)
block|,
comment|/** ISO style dates: 2010-02-14 */
DECL|enumConstant|ISO
name|ISO
argument_list|(
literal|"MM-dd"
argument_list|,
literal|"yyyy-MM-dd"
argument_list|)
block|,
comment|/** European style dates: 27. Apr, 27.04.2010 */
DECL|enumConstant|EURO
name|EURO
argument_list|(
literal|"d. MMM"
argument_list|,
literal|"dd.MM.yyyy"
argument_list|)
block|,
comment|/** UK style dates: 27/04, 27/04/2010 */
DECL|enumConstant|UK
name|UK
argument_list|(
literal|"dd/MM"
argument_list|,
literal|"dd/MM/yyyy"
argument_list|)
block|;
DECL|field|shortFormat
specifier|private
specifier|final
name|String
name|shortFormat
decl_stmt|;
DECL|field|longFormat
specifier|private
specifier|final
name|String
name|longFormat
decl_stmt|;
DECL|method|DateFormat (String shortFormat, String longFormat)
name|DateFormat
parameter_list|(
name|String
name|shortFormat
parameter_list|,
name|String
name|longFormat
parameter_list|)
block|{
name|this
operator|.
name|shortFormat
operator|=
name|shortFormat
expr_stmt|;
name|this
operator|.
name|longFormat
operator|=
name|longFormat
expr_stmt|;
block|}
DECL|method|getShortFormat ()
specifier|public
name|String
name|getShortFormat
parameter_list|()
block|{
return|return
name|shortFormat
return|;
block|}
DECL|method|getLongFormat ()
specifier|public
name|String
name|getLongFormat
parameter_list|()
block|{
return|return
name|longFormat
return|;
block|}
block|}
DECL|enum|ReviewCategoryStrategy
specifier|public
enum|enum
name|ReviewCategoryStrategy
block|{
DECL|enumConstant|NONE
name|NONE
block|,
DECL|enumConstant|NAME
name|NAME
block|,
DECL|enumConstant|EMAIL
name|EMAIL
block|,
DECL|enumConstant|USERNAME
name|USERNAME
block|,
DECL|enumConstant|ABBREV
name|ABBREV
block|}
DECL|enum|DiffView
specifier|public
enum|enum
name|DiffView
block|{
DECL|enumConstant|SIDE_BY_SIDE
name|SIDE_BY_SIDE
block|,
DECL|enumConstant|UNIFIED_DIFF
name|UNIFIED_DIFF
block|}
DECL|enum|EmailStrategy
specifier|public
enum|enum
name|EmailStrategy
block|{
DECL|enumConstant|ENABLED
name|ENABLED
block|,
DECL|enumConstant|CC_ON_OWN_COMMENTS
name|CC_ON_OWN_COMMENTS
block|,
DECL|enumConstant|DISABLED
name|DISABLED
block|}
DECL|enum|DefaultBase
specifier|public
enum|enum
name|DefaultBase
block|{
DECL|enumConstant|AUTO_MERGE
name|AUTO_MERGE
argument_list|(
literal|null
argument_list|)
block|,
DECL|enumConstant|FIRST_PARENT
name|FIRST_PARENT
argument_list|(
operator|-
literal|1
argument_list|)
block|;
DECL|field|base
specifier|private
specifier|final
name|String
name|base
decl_stmt|;
DECL|method|DefaultBase (String base)
name|DefaultBase
parameter_list|(
name|String
name|base
parameter_list|)
block|{
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
block|}
DECL|method|DefaultBase (int base)
name|DefaultBase
parameter_list|(
name|int
name|base
parameter_list|)
block|{
name|this
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|base
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getBase ()
specifier|public
name|String
name|getBase
parameter_list|()
block|{
return|return
name|base
return|;
block|}
block|}
DECL|enum|TimeFormat
specifier|public
enum|enum
name|TimeFormat
block|{
comment|/** 12-hour clock: 1:15 am, 2:13 pm */
DECL|enumConstant|HHMM_12
name|HHMM_12
argument_list|(
literal|"h:mm a"
argument_list|)
block|,
comment|/** 24-hour clock: 01:15, 14:13 */
DECL|enumConstant|HHMM_24
name|HHMM_24
argument_list|(
literal|"HH:mm"
argument_list|)
block|;
DECL|field|format
specifier|private
specifier|final
name|String
name|format
decl_stmt|;
DECL|method|TimeFormat (String format)
name|TimeFormat
parameter_list|(
name|String
name|format
parameter_list|)
block|{
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
block|}
DECL|method|getFormat ()
specifier|public
name|String
name|getFormat
parameter_list|()
block|{
return|return
name|format
return|;
block|}
block|}
comment|/** Number of changes to show in a screen. */
DECL|field|changesPerPage
specifier|public
name|Integer
name|changesPerPage
decl_stmt|;
comment|/** Should the site header be displayed when logged in ? */
DECL|field|showSiteHeader
specifier|public
name|Boolean
name|showSiteHeader
decl_stmt|;
comment|/** Should the Flash helper movie be used to copy text to the clipboard? */
DECL|field|useFlashClipboard
specifier|public
name|Boolean
name|useFlashClipboard
decl_stmt|;
comment|/** Type of download URL the user prefers to use. */
DECL|field|downloadScheme
specifier|public
name|String
name|downloadScheme
decl_stmt|;
comment|/** Type of download command the user prefers to use. */
DECL|field|downloadCommand
specifier|public
name|DownloadCommand
name|downloadCommand
decl_stmt|;
DECL|field|dateFormat
specifier|public
name|DateFormat
name|dateFormat
decl_stmt|;
DECL|field|timeFormat
specifier|public
name|TimeFormat
name|timeFormat
decl_stmt|;
DECL|field|highlightAssigneeInChangeTable
specifier|public
name|Boolean
name|highlightAssigneeInChangeTable
decl_stmt|;
DECL|field|relativeDateInChangeTable
specifier|public
name|Boolean
name|relativeDateInChangeTable
decl_stmt|;
DECL|field|diffView
specifier|public
name|DiffView
name|diffView
decl_stmt|;
DECL|field|sizeBarInChangeTable
specifier|public
name|Boolean
name|sizeBarInChangeTable
decl_stmt|;
DECL|field|legacycidInChangeTable
specifier|public
name|Boolean
name|legacycidInChangeTable
decl_stmt|;
DECL|field|reviewCategoryStrategy
specifier|public
name|ReviewCategoryStrategy
name|reviewCategoryStrategy
decl_stmt|;
DECL|field|muteCommonPathPrefixes
specifier|public
name|Boolean
name|muteCommonPathPrefixes
decl_stmt|;
DECL|field|signedOffBy
specifier|public
name|Boolean
name|signedOffBy
decl_stmt|;
DECL|field|my
specifier|public
name|List
argument_list|<
name|MenuItem
argument_list|>
name|my
decl_stmt|;
DECL|field|urlAliases
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|urlAliases
decl_stmt|;
DECL|field|emailStrategy
specifier|public
name|EmailStrategy
name|emailStrategy
decl_stmt|;
DECL|field|defaultBaseForMerges
specifier|public
name|DefaultBase
name|defaultBaseForMerges
decl_stmt|;
DECL|method|isShowInfoInReviewCategory ()
specifier|public
name|boolean
name|isShowInfoInReviewCategory
parameter_list|()
block|{
return|return
name|getReviewCategoryStrategy
argument_list|()
operator|!=
name|ReviewCategoryStrategy
operator|.
name|NONE
return|;
block|}
DECL|method|getDateFormat ()
specifier|public
name|DateFormat
name|getDateFormat
parameter_list|()
block|{
if|if
condition|(
name|dateFormat
operator|==
literal|null
condition|)
block|{
return|return
name|DateFormat
operator|.
name|STD
return|;
block|}
return|return
name|dateFormat
return|;
block|}
DECL|method|getTimeFormat ()
specifier|public
name|TimeFormat
name|getTimeFormat
parameter_list|()
block|{
if|if
condition|(
name|timeFormat
operator|==
literal|null
condition|)
block|{
return|return
name|TimeFormat
operator|.
name|HHMM_12
return|;
block|}
return|return
name|timeFormat
return|;
block|}
DECL|method|getReviewCategoryStrategy ()
specifier|public
name|ReviewCategoryStrategy
name|getReviewCategoryStrategy
parameter_list|()
block|{
if|if
condition|(
name|reviewCategoryStrategy
operator|==
literal|null
condition|)
block|{
return|return
name|ReviewCategoryStrategy
operator|.
name|NONE
return|;
block|}
return|return
name|reviewCategoryStrategy
return|;
block|}
DECL|method|getDiffView ()
specifier|public
name|DiffView
name|getDiffView
parameter_list|()
block|{
if|if
condition|(
name|diffView
operator|==
literal|null
condition|)
block|{
return|return
name|DiffView
operator|.
name|SIDE_BY_SIDE
return|;
block|}
return|return
name|diffView
return|;
block|}
DECL|method|getEmailStrategy ()
specifier|public
name|EmailStrategy
name|getEmailStrategy
parameter_list|()
block|{
if|if
condition|(
name|emailStrategy
operator|==
literal|null
condition|)
block|{
return|return
name|EmailStrategy
operator|.
name|ENABLED
return|;
block|}
return|return
name|emailStrategy
return|;
block|}
DECL|method|defaults ()
specifier|public
specifier|static
name|GeneralPreferencesInfo
name|defaults
parameter_list|()
block|{
name|GeneralPreferencesInfo
name|p
init|=
operator|new
name|GeneralPreferencesInfo
argument_list|()
decl_stmt|;
name|p
operator|.
name|changesPerPage
operator|=
name|DEFAULT_PAGESIZE
expr_stmt|;
name|p
operator|.
name|showSiteHeader
operator|=
literal|true
expr_stmt|;
name|p
operator|.
name|useFlashClipboard
operator|=
literal|true
expr_stmt|;
name|p
operator|.
name|emailStrategy
operator|=
name|EmailStrategy
operator|.
name|ENABLED
expr_stmt|;
name|p
operator|.
name|reviewCategoryStrategy
operator|=
name|ReviewCategoryStrategy
operator|.
name|NONE
expr_stmt|;
name|p
operator|.
name|downloadScheme
operator|=
literal|null
expr_stmt|;
name|p
operator|.
name|downloadCommand
operator|=
name|DownloadCommand
operator|.
name|CHECKOUT
expr_stmt|;
name|p
operator|.
name|dateFormat
operator|=
name|DateFormat
operator|.
name|STD
expr_stmt|;
name|p
operator|.
name|timeFormat
operator|=
name|TimeFormat
operator|.
name|HHMM_12
expr_stmt|;
name|p
operator|.
name|highlightAssigneeInChangeTable
operator|=
literal|true
expr_stmt|;
name|p
operator|.
name|relativeDateInChangeTable
operator|=
literal|false
expr_stmt|;
name|p
operator|.
name|diffView
operator|=
name|DiffView
operator|.
name|SIDE_BY_SIDE
expr_stmt|;
name|p
operator|.
name|sizeBarInChangeTable
operator|=
literal|true
expr_stmt|;
name|p
operator|.
name|legacycidInChangeTable
operator|=
literal|false
expr_stmt|;
name|p
operator|.
name|muteCommonPathPrefixes
operator|=
literal|true
expr_stmt|;
name|p
operator|.
name|signedOffBy
operator|=
literal|false
expr_stmt|;
name|p
operator|.
name|defaultBaseForMerges
operator|=
name|DefaultBase
operator|.
name|FIRST_PARENT
expr_stmt|;
return|return
name|p
return|;
block|}
block|}
end_class

end_unit

