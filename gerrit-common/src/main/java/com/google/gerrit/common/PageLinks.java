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
DECL|package|com.google.gerrit.common
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
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
name|common
operator|.
name|data
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
name|common
operator|.
name|data
operator|.
name|ChangeInfo
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
name|Change
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
name|Change
operator|.
name|Status
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
name|KeyUtil
import|;
end_import

begin_class
DECL|class|PageLinks
specifier|public
class|class
name|PageLinks
block|{
DECL|field|SETTINGS
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS
init|=
literal|"settings"
decl_stmt|;
DECL|field|SETTINGS_PREFERENCES
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS_PREFERENCES
init|=
literal|"settings,preferences"
decl_stmt|;
DECL|field|SETTINGS_SSHKEYS
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS_SSHKEYS
init|=
literal|"settings,ssh-keys"
decl_stmt|;
DECL|field|SETTINGS_HTTP_PASSWORD
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS_HTTP_PASSWORD
init|=
literal|"settings,http-password"
decl_stmt|;
DECL|field|SETTINGS_WEBIDENT
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS_WEBIDENT
init|=
literal|"settings,web-identities"
decl_stmt|;
DECL|field|SETTINGS_MYGROUPS
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS_MYGROUPS
init|=
literal|"settings,group-memberships"
decl_stmt|;
DECL|field|SETTINGS_AGREEMENTS
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS_AGREEMENTS
init|=
literal|"settings,agreements"
decl_stmt|;
DECL|field|SETTINGS_CONTACT
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS_CONTACT
init|=
literal|"settings,contact"
decl_stmt|;
DECL|field|SETTINGS_PROJECTS
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS_PROJECTS
init|=
literal|"settings,projects"
decl_stmt|;
DECL|field|SETTINGS_NEW_AGREEMENT
specifier|public
specifier|static
specifier|final
name|String
name|SETTINGS_NEW_AGREEMENT
init|=
literal|"settings,new-agreement"
decl_stmt|;
DECL|field|REGISTER
specifier|public
specifier|static
specifier|final
name|String
name|REGISTER
init|=
literal|"register"
decl_stmt|;
DECL|field|TOP
specifier|public
specifier|static
specifier|final
name|String
name|TOP
init|=
literal|"n,z"
decl_stmt|;
DECL|field|MINE
specifier|public
specifier|static
specifier|final
name|String
name|MINE
init|=
literal|"mine"
decl_stmt|;
DECL|field|MINE_STARRED
specifier|public
specifier|static
specifier|final
name|String
name|MINE_STARRED
init|=
literal|"mine,starred"
decl_stmt|;
DECL|field|MINE_DRAFTS
specifier|public
specifier|static
specifier|final
name|String
name|MINE_DRAFTS
init|=
literal|"mine,drafts"
decl_stmt|;
DECL|field|MINE_WATCHED
specifier|public
specifier|static
specifier|final
name|String
name|MINE_WATCHED
init|=
literal|"mine,watched,"
operator|+
name|TOP
decl_stmt|;
DECL|field|ALL_ABANDONED
specifier|public
specifier|static
specifier|final
name|String
name|ALL_ABANDONED
init|=
literal|"all,abandoned,"
operator|+
name|TOP
decl_stmt|;
DECL|field|ALL_MERGED
specifier|public
specifier|static
specifier|final
name|String
name|ALL_MERGED
init|=
literal|"all,merged,"
operator|+
name|TOP
decl_stmt|;
DECL|field|ALL_OPEN
specifier|public
specifier|static
specifier|final
name|String
name|ALL_OPEN
init|=
literal|"all,open,"
operator|+
name|TOP
decl_stmt|;
DECL|field|ADMIN_GROUPS
specifier|public
specifier|static
specifier|final
name|String
name|ADMIN_GROUPS
init|=
literal|"admin,groups"
decl_stmt|;
DECL|field|ADMIN_PROJECTS
specifier|public
specifier|static
specifier|final
name|String
name|ADMIN_PROJECTS
init|=
literal|"admin,projects"
decl_stmt|;
DECL|method|toChange (final ChangeInfo c)
specifier|public
specifier|static
name|String
name|toChange
parameter_list|(
specifier|final
name|ChangeInfo
name|c
parameter_list|)
block|{
return|return
name|toChange
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toChange (final Change.Id c)
specifier|public
specifier|static
name|String
name|toChange
parameter_list|(
specifier|final
name|Change
operator|.
name|Id
name|c
parameter_list|)
block|{
return|return
literal|"change,"
operator|+
name|c
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toAccountDashboard (final AccountInfo acct)
specifier|public
specifier|static
name|String
name|toAccountDashboard
parameter_list|(
specifier|final
name|AccountInfo
name|acct
parameter_list|)
block|{
return|return
name|toAccountDashboard
argument_list|(
name|acct
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toAccountDashboard (final Account.Id acct)
specifier|public
specifier|static
name|String
name|toAccountDashboard
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|acct
parameter_list|)
block|{
return|return
literal|"dashboard,"
operator|+
name|acct
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toChangeQuery (final String query)
specifier|public
specifier|static
name|String
name|toChangeQuery
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
block|{
return|return
literal|"q,"
operator|+
name|KeyUtil
operator|.
name|encode
argument_list|(
name|query
argument_list|)
operator|+
literal|","
operator|+
name|TOP
return|;
block|}
DECL|method|toProject (final Project.NameKey proj, Status status)
specifier|public
specifier|static
name|String
name|toProject
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|proj
parameter_list|,
name|Status
name|status
parameter_list|)
block|{
switch|switch
condition|(
name|status
condition|)
block|{
case|case
name|ABANDONED
case|:
return|return
literal|"project,abandoned,"
operator|+
name|proj
operator|.
name|toString
argument_list|()
operator|+
literal|",n,z"
return|;
case|case
name|MERGED
case|:
return|return
literal|"project,merged,"
operator|+
name|proj
operator|.
name|toString
argument_list|()
operator|+
literal|",n,z"
return|;
case|case
name|NEW
case|:
case|case
name|SUBMITTED
case|:
default|default:
return|return
literal|"project,open,"
operator|+
name|proj
operator|.
name|toString
argument_list|()
operator|+
literal|",n,z"
return|;
block|}
block|}
DECL|method|PageLinks ()
specifier|protected
name|PageLinks
parameter_list|()
block|{   }
block|}
end_class

end_unit

