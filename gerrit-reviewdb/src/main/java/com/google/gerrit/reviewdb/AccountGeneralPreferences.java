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

begin_comment
comment|/** Preferences about a single user. */
end_comment

begin_class
DECL|class|AccountGeneralPreferences
specifier|public
specifier|final
class|class
name|AccountGeneralPreferences
block|{
comment|/** Default number of lines of context. */
DECL|field|DEFAULT_CONTEXT
specifier|public
specifier|static
specifier|final
name|short
name|DEFAULT_CONTEXT
init|=
literal|10
decl_stmt|;
comment|/** Context setting to display the entire file. */
DECL|field|WHOLE_FILE_CONTEXT
specifier|public
specifier|static
specifier|final
name|short
name|WHOLE_FILE_CONTEXT
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Typical valid choices for the default context setting. */
DECL|field|CONTEXT_CHOICES
specifier|public
specifier|static
specifier|final
name|short
index|[]
name|CONTEXT_CHOICES
init|=
block|{
literal|3
block|,
literal|10
block|,
literal|25
block|,
literal|50
block|,
literal|75
block|,
literal|100
block|,
name|WHOLE_FILE_CONTEXT
block|}
decl_stmt|;
comment|/** Default number of items to display per page. */
DECL|field|DEFAULT_PAGESIZE
specifier|public
specifier|static
specifier|final
name|short
name|DEFAULT_PAGESIZE
init|=
literal|25
decl_stmt|;
comment|/** Valid choices for the page size. */
DECL|field|PAGESIZE_CHOICES
specifier|public
specifier|static
specifier|final
name|short
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
comment|/** Preferred URL type to download a change. */
DECL|enum|DownloadUrl
specifier|public
specifier|static
enum|enum
name|DownloadUrl
block|{
DECL|enumConstant|ANON_GIT
DECL|enumConstant|ANON_HTTP
DECL|enumConstant|ANON_SSH
DECL|enumConstant|HTTP
DECL|enumConstant|SSH
name|ANON_GIT
block|,
name|ANON_HTTP
block|,
name|ANON_SSH
block|,
name|HTTP
block|,
name|SSH
block|;   }
comment|/** Preferred method to download a change. */
DECL|enum|DownloadCommand
specifier|public
specifier|static
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
block|;   }
comment|/** Default number of lines of context when viewing a patch. */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|1
argument_list|)
DECL|field|defaultContext
specifier|protected
name|short
name|defaultContext
decl_stmt|;
comment|/** Number of changes to show in a screen. */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|2
argument_list|)
DECL|field|maximumPageSize
specifier|protected
name|short
name|maximumPageSize
decl_stmt|;
comment|/** Should the site header be displayed when logged in ? */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|3
argument_list|)
DECL|field|showSiteHeader
specifier|protected
name|boolean
name|showSiteHeader
decl_stmt|;
comment|/** Should the Flash helper movie be used to copy text to the clipboard? */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|4
argument_list|)
DECL|field|useFlashClipboard
specifier|protected
name|boolean
name|useFlashClipboard
decl_stmt|;
comment|/** Type of download URL the user prefers to use. */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|5
argument_list|,
name|length
operator|=
literal|20
argument_list|,
name|notNull
operator|=
literal|false
argument_list|)
DECL|field|downloadUrl
specifier|protected
name|String
name|downloadUrl
decl_stmt|;
comment|/** Type of download command the user prefers to use. */
annotation|@
name|Column
argument_list|(
name|id
operator|=
literal|6
argument_list|,
name|length
operator|=
literal|20
argument_list|,
name|notNull
operator|=
literal|false
argument_list|)
DECL|field|downloadCommand
specifier|protected
name|String
name|downloadCommand
decl_stmt|;
DECL|method|AccountGeneralPreferences ()
specifier|public
name|AccountGeneralPreferences
parameter_list|()
block|{   }
comment|/** Get the default number of lines of context when viewing a patch. */
DECL|method|getDefaultContext ()
specifier|public
name|short
name|getDefaultContext
parameter_list|()
block|{
return|return
name|defaultContext
return|;
block|}
comment|/** Set the number of lines of context when viewing a patch. */
DECL|method|setDefaultContext (final short s)
specifier|public
name|void
name|setDefaultContext
parameter_list|(
specifier|final
name|short
name|s
parameter_list|)
block|{
name|defaultContext
operator|=
name|s
expr_stmt|;
block|}
DECL|method|getMaximumPageSize ()
specifier|public
name|short
name|getMaximumPageSize
parameter_list|()
block|{
return|return
name|maximumPageSize
return|;
block|}
DECL|method|setMaximumPageSize (final short s)
specifier|public
name|void
name|setMaximumPageSize
parameter_list|(
specifier|final
name|short
name|s
parameter_list|)
block|{
name|maximumPageSize
operator|=
name|s
expr_stmt|;
block|}
DECL|method|isShowSiteHeader ()
specifier|public
name|boolean
name|isShowSiteHeader
parameter_list|()
block|{
return|return
name|showSiteHeader
return|;
block|}
DECL|method|setShowSiteHeader (final boolean b)
specifier|public
name|void
name|setShowSiteHeader
parameter_list|(
specifier|final
name|boolean
name|b
parameter_list|)
block|{
name|showSiteHeader
operator|=
name|b
expr_stmt|;
block|}
DECL|method|isUseFlashClipboard ()
specifier|public
name|boolean
name|isUseFlashClipboard
parameter_list|()
block|{
return|return
name|useFlashClipboard
return|;
block|}
DECL|method|setUseFlashClipboard (final boolean b)
specifier|public
name|void
name|setUseFlashClipboard
parameter_list|(
specifier|final
name|boolean
name|b
parameter_list|)
block|{
name|useFlashClipboard
operator|=
name|b
expr_stmt|;
block|}
DECL|method|getDownloadUrl ()
specifier|public
name|DownloadUrl
name|getDownloadUrl
parameter_list|()
block|{
if|if
condition|(
name|downloadUrl
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|DownloadUrl
operator|.
name|valueOf
argument_list|(
name|downloadUrl
argument_list|)
return|;
block|}
DECL|method|setDownloadUrl (DownloadUrl url)
specifier|public
name|void
name|setDownloadUrl
parameter_list|(
name|DownloadUrl
name|url
parameter_list|)
block|{
if|if
condition|(
name|url
operator|!=
literal|null
condition|)
block|{
name|downloadUrl
operator|=
name|url
operator|.
name|name
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|downloadUrl
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getDownloadCommand ()
specifier|public
name|DownloadCommand
name|getDownloadCommand
parameter_list|()
block|{
if|if
condition|(
name|downloadCommand
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|DownloadCommand
operator|.
name|valueOf
argument_list|(
name|downloadCommand
argument_list|)
return|;
block|}
DECL|method|setDownloadCommand (DownloadCommand cmd)
specifier|public
name|void
name|setDownloadCommand
parameter_list|(
name|DownloadCommand
name|cmd
parameter_list|)
block|{
if|if
condition|(
name|cmd
operator|!=
literal|null
condition|)
block|{
name|downloadCommand
operator|=
name|cmd
operator|.
name|name
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|downloadCommand
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|resetToDefaults ()
specifier|public
name|void
name|resetToDefaults
parameter_list|()
block|{
name|defaultContext
operator|=
name|DEFAULT_CONTEXT
expr_stmt|;
name|maximumPageSize
operator|=
name|DEFAULT_PAGESIZE
expr_stmt|;
name|showSiteHeader
operator|=
literal|true
expr_stmt|;
name|useFlashClipboard
operator|=
literal|true
expr_stmt|;
name|downloadUrl
operator|=
literal|null
expr_stmt|;
name|downloadCommand
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

