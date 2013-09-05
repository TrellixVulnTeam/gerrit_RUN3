begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
name|common
operator|.
name|data
operator|.
name|PermissionRule
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
name|errors
operator|.
name|NoSuchGroupException
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
name|AccountGroup
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
name|gerrit
operator|.
name|server
operator|.
name|IdentifiedUser
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
name|git
operator|.
name|AccountsSection
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
name|project
operator|.
name|ProjectCache
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
name|Set
import|;
end_import

begin_comment
comment|/** Access control management for one account's access to other accounts. */
end_comment

begin_class
DECL|class|AccountControl
specifier|public
class|class
name|AccountControl
block|{
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
block|{
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|groupControlFactory
specifier|private
specifier|final
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
DECL|field|accountVisibility
specifier|private
specifier|final
name|AccountVisibility
name|accountVisibility
decl_stmt|;
annotation|@
name|Inject
DECL|method|Factory (final ProjectCache projectCache, final GroupControl.Factory groupControlFactory, final Provider<CurrentUser> user, final IdentifiedUser.GenericFactory userFactory, final AccountVisibility accountVisibility)
name|Factory
parameter_list|(
specifier|final
name|ProjectCache
name|projectCache
parameter_list|,
specifier|final
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
parameter_list|,
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
parameter_list|,
specifier|final
name|AccountVisibility
name|accountVisibility
parameter_list|)
block|{
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|groupControlFactory
operator|=
name|groupControlFactory
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|userFactory
operator|=
name|userFactory
expr_stmt|;
name|this
operator|.
name|accountVisibility
operator|=
name|accountVisibility
expr_stmt|;
block|}
DECL|method|get ()
specifier|public
name|AccountControl
name|get
parameter_list|()
block|{
return|return
operator|new
name|AccountControl
argument_list|(
name|projectCache
argument_list|,
name|groupControlFactory
argument_list|,
name|user
operator|.
name|get
argument_list|()
argument_list|,
name|userFactory
argument_list|,
name|accountVisibility
argument_list|)
return|;
block|}
block|}
DECL|field|accountsSection
specifier|private
specifier|final
name|AccountsSection
name|accountsSection
decl_stmt|;
DECL|field|groupControlFactory
specifier|private
specifier|final
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
decl_stmt|;
DECL|field|currentUser
specifier|private
specifier|final
name|CurrentUser
name|currentUser
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
DECL|field|accountVisibility
specifier|private
specifier|final
name|AccountVisibility
name|accountVisibility
decl_stmt|;
DECL|method|AccountControl (final ProjectCache projectCache, final GroupControl.Factory groupControlFactory, final CurrentUser currentUser, final IdentifiedUser.GenericFactory userFactory, final AccountVisibility accountVisibility)
name|AccountControl
parameter_list|(
specifier|final
name|ProjectCache
name|projectCache
parameter_list|,
specifier|final
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
parameter_list|,
specifier|final
name|CurrentUser
name|currentUser
parameter_list|,
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
parameter_list|,
specifier|final
name|AccountVisibility
name|accountVisibility
parameter_list|)
block|{
name|this
operator|.
name|accountsSection
operator|=
name|projectCache
operator|.
name|getAllProjects
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|getAccountsSection
argument_list|()
expr_stmt|;
name|this
operator|.
name|groupControlFactory
operator|=
name|groupControlFactory
expr_stmt|;
name|this
operator|.
name|currentUser
operator|=
name|currentUser
expr_stmt|;
name|this
operator|.
name|userFactory
operator|=
name|userFactory
expr_stmt|;
name|this
operator|.
name|accountVisibility
operator|=
name|accountVisibility
expr_stmt|;
block|}
comment|/**    * Returns true if the otherUser is allowed to see the current user, based    * on the account visibility policy. Depending on the group membership    * realms supported, this may not be able to determine SAME_GROUP or    * VISIBLE_GROUP correctly (defaulting to not being visible). This is because    * {@link GroupMembership#getKnownGroups()} may only return a subset of the    * effective groups.    */
DECL|method|canSee (final Account otherUser)
specifier|public
name|boolean
name|canSee
parameter_list|(
specifier|final
name|Account
name|otherUser
parameter_list|)
block|{
return|return
name|canSee
argument_list|(
name|otherUser
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns true if the otherUser is allowed to see the current user, based    * on the account visibility policy. Depending on the group membership    * realms supported, this may not be able to determine SAME_GROUP or    * VISIBLE_GROUP correctly (defaulting to not being visible). This is because    * {@link GroupMembership#getKnownGroups()} may only return a subset of the    * effective groups.    */
DECL|method|canSee (final Account.Id otherUser)
specifier|public
name|boolean
name|canSee
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|otherUser
parameter_list|)
block|{
comment|// Special case: I can always see myself.
if|if
condition|(
name|currentUser
operator|.
name|isIdentifiedUser
argument_list|()
operator|&&
operator|(
operator|(
name|IdentifiedUser
operator|)
name|currentUser
operator|)
operator|.
name|getAccountId
argument_list|()
operator|.
name|equals
argument_list|(
name|otherUser
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
switch|switch
condition|(
name|accountVisibility
condition|)
block|{
case|case
name|ALL
case|:
return|return
literal|true
return|;
case|case
name|SAME_GROUP
case|:
block|{
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|usersGroups
init|=
name|groupsOf
argument_list|(
name|otherUser
argument_list|)
decl_stmt|;
name|usersGroups
operator|.
name|remove
argument_list|(
name|AccountGroup
operator|.
name|ANONYMOUS_USERS
argument_list|)
expr_stmt|;
name|usersGroups
operator|.
name|remove
argument_list|(
name|AccountGroup
operator|.
name|REGISTERED_USERS
argument_list|)
expr_stmt|;
for|for
control|(
name|PermissionRule
name|rule
range|:
name|accountsSection
operator|.
name|getSameGroupVisibility
argument_list|()
control|)
block|{
if|if
condition|(
name|rule
operator|.
name|isBlock
argument_list|()
operator|||
name|rule
operator|.
name|isDeny
argument_list|()
condition|)
block|{
name|usersGroups
operator|.
name|remove
argument_list|(
name|rule
operator|.
name|getGroup
argument_list|()
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|currentUser
operator|.
name|getEffectiveGroups
argument_list|()
operator|.
name|containsAnyOf
argument_list|(
name|usersGroups
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
break|break;
block|}
case|case
name|VISIBLE_GROUP
case|:
block|{
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|usersGroups
init|=
name|groupsOf
argument_list|(
name|otherUser
argument_list|)
decl_stmt|;
name|usersGroups
operator|.
name|remove
argument_list|(
name|AccountGroup
operator|.
name|ANONYMOUS_USERS
argument_list|)
expr_stmt|;
name|usersGroups
operator|.
name|remove
argument_list|(
name|AccountGroup
operator|.
name|REGISTERED_USERS
argument_list|)
expr_stmt|;
for|for
control|(
name|AccountGroup
operator|.
name|UUID
name|usersGroup
range|:
name|usersGroups
control|)
block|{
try|try
block|{
if|if
condition|(
name|groupControlFactory
operator|.
name|controlFor
argument_list|(
name|usersGroup
argument_list|)
operator|.
name|isVisible
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchGroupException
name|e
parameter_list|)
block|{
continue|continue;
block|}
block|}
break|break;
block|}
case|case
name|NONE
case|:
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Bad AccountVisibility "
operator|+
name|accountVisibility
argument_list|)
throw|;
block|}
return|return
name|currentUser
operator|.
name|getCapabilities
argument_list|()
operator|.
name|canAdministrateServer
argument_list|()
return|;
block|}
DECL|method|groupsOf (Account.Id account)
specifier|private
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|groupsOf
parameter_list|(
name|Account
operator|.
name|Id
name|account
parameter_list|)
block|{
return|return
name|userFactory
operator|.
name|create
argument_list|(
name|account
argument_list|)
operator|.
name|getEffectiveGroups
argument_list|()
operator|.
name|getKnownGroups
argument_list|()
return|;
block|}
block|}
end_class

end_unit

