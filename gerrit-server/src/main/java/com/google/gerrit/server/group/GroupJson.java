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
DECL|package|com.google.gerrit.server.group
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|group
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|client
operator|.
name|ListGroupsOption
operator|.
name|INCLUDES
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|client
operator|.
name|ListGroupsOption
operator|.
name|MEMBERS
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|GroupDescription
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
name|GroupDescriptions
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
name|client
operator|.
name|ListGroupsOption
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
name|common
operator|.
name|GroupInfo
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
name|common
operator|.
name|GroupOptionsInfo
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
name|MethodNotAllowedException
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
name|Url
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
name|account
operator|.
name|GroupBackend
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
name|account
operator|.
name|GroupControl
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_class
DECL|class|GroupJson
specifier|public
class|class
name|GroupJson
block|{
DECL|method|createOptions (GroupDescription.Basic group)
specifier|public
specifier|static
name|GroupOptionsInfo
name|createOptions
parameter_list|(
name|GroupDescription
operator|.
name|Basic
name|group
parameter_list|)
block|{
name|GroupOptionsInfo
name|options
init|=
operator|new
name|GroupOptionsInfo
argument_list|()
decl_stmt|;
name|AccountGroup
name|ag
init|=
name|GroupDescriptions
operator|.
name|toAccountGroup
argument_list|(
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
name|ag
operator|!=
literal|null
operator|&&
name|ag
operator|.
name|isVisibleToAll
argument_list|()
condition|)
block|{
name|options
operator|.
name|visibleToAll
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|options
return|;
block|}
DECL|field|groupBackend
specifier|private
specifier|final
name|GroupBackend
name|groupBackend
decl_stmt|;
DECL|field|groupControlFactory
specifier|private
specifier|final
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
decl_stmt|;
DECL|field|listMembers
specifier|private
specifier|final
name|Provider
argument_list|<
name|ListMembers
argument_list|>
name|listMembers
decl_stmt|;
DECL|field|listIncludes
specifier|private
specifier|final
name|Provider
argument_list|<
name|ListIncludedGroups
argument_list|>
name|listIncludes
decl_stmt|;
DECL|field|options
specifier|private
name|EnumSet
argument_list|<
name|ListGroupsOption
argument_list|>
name|options
decl_stmt|;
annotation|@
name|Inject
DECL|method|GroupJson (GroupBackend groupBackend, GroupControl.Factory groupControlFactory, Provider<ListMembers> listMembers, Provider<ListIncludedGroups> listIncludes)
name|GroupJson
parameter_list|(
name|GroupBackend
name|groupBackend
parameter_list|,
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
parameter_list|,
name|Provider
argument_list|<
name|ListMembers
argument_list|>
name|listMembers
parameter_list|,
name|Provider
argument_list|<
name|ListIncludedGroups
argument_list|>
name|listIncludes
parameter_list|)
block|{
name|this
operator|.
name|groupBackend
operator|=
name|groupBackend
expr_stmt|;
name|this
operator|.
name|groupControlFactory
operator|=
name|groupControlFactory
expr_stmt|;
name|this
operator|.
name|listMembers
operator|=
name|listMembers
expr_stmt|;
name|this
operator|.
name|listIncludes
operator|=
name|listIncludes
expr_stmt|;
name|options
operator|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|ListGroupsOption
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|addOption (ListGroupsOption o)
specifier|public
name|GroupJson
name|addOption
parameter_list|(
name|ListGroupsOption
name|o
parameter_list|)
block|{
name|options
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addOptions (Collection<ListGroupsOption> o)
specifier|public
name|GroupJson
name|addOptions
parameter_list|(
name|Collection
argument_list|<
name|ListGroupsOption
argument_list|>
name|o
parameter_list|)
block|{
name|options
operator|.
name|addAll
argument_list|(
name|o
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|format (GroupResource rsrc)
specifier|public
name|GroupInfo
name|format
parameter_list|(
name|GroupResource
name|rsrc
parameter_list|)
throws|throws
name|OrmException
block|{
name|GroupInfo
name|info
init|=
name|init
argument_list|(
name|rsrc
operator|.
name|getGroup
argument_list|()
argument_list|)
decl_stmt|;
name|initMembersAndIncludes
argument_list|(
name|rsrc
argument_list|,
name|info
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|method|format (GroupDescription.Basic group)
specifier|public
name|GroupInfo
name|format
parameter_list|(
name|GroupDescription
operator|.
name|Basic
name|group
parameter_list|)
throws|throws
name|OrmException
block|{
name|GroupInfo
name|info
init|=
name|init
argument_list|(
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|contains
argument_list|(
name|MEMBERS
argument_list|)
operator|||
name|options
operator|.
name|contains
argument_list|(
name|INCLUDES
argument_list|)
condition|)
block|{
name|GroupResource
name|rsrc
init|=
operator|new
name|GroupResource
argument_list|(
name|groupControlFactory
operator|.
name|controlFor
argument_list|(
name|group
argument_list|)
argument_list|)
decl_stmt|;
name|initMembersAndIncludes
argument_list|(
name|rsrc
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
return|return
name|info
return|;
block|}
DECL|method|init (GroupDescription.Basic group)
specifier|private
name|GroupInfo
name|init
parameter_list|(
name|GroupDescription
operator|.
name|Basic
name|group
parameter_list|)
block|{
name|GroupInfo
name|info
init|=
operator|new
name|GroupInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|id
operator|=
name|Url
operator|.
name|encode
argument_list|(
name|group
operator|.
name|getGroupUUID
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|name
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|url
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|group
operator|.
name|getUrl
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|options
operator|=
name|createOptions
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|AccountGroup
name|g
init|=
name|GroupDescriptions
operator|.
name|toAccountGroup
argument_list|(
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
name|g
operator|!=
literal|null
condition|)
block|{
name|info
operator|.
name|description
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|g
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|groupId
operator|=
name|g
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|g
operator|.
name|getOwnerGroupUUID
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|info
operator|.
name|ownerId
operator|=
name|Url
operator|.
name|encode
argument_list|(
name|g
operator|.
name|getOwnerGroupUUID
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|GroupDescription
operator|.
name|Basic
name|o
init|=
name|groupBackend
operator|.
name|get
argument_list|(
name|g
operator|.
name|getOwnerGroupUUID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|info
operator|.
name|owner
operator|=
name|o
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|info
return|;
block|}
DECL|method|initMembersAndIncludes (GroupResource rsrc, GroupInfo info)
specifier|private
name|GroupInfo
name|initMembersAndIncludes
parameter_list|(
name|GroupResource
name|rsrc
parameter_list|,
name|GroupInfo
name|info
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|rsrc
operator|.
name|toAccountGroup
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|info
return|;
block|}
try|try
block|{
if|if
condition|(
name|options
operator|.
name|contains
argument_list|(
name|MEMBERS
argument_list|)
condition|)
block|{
name|info
operator|.
name|members
operator|=
name|listMembers
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|options
operator|.
name|contains
argument_list|(
name|INCLUDES
argument_list|)
condition|)
block|{
name|info
operator|.
name|includes
operator|=
name|listIncludes
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|)
expr_stmt|;
block|}
return|return
name|info
return|;
block|}
catch|catch
parameter_list|(
name|MethodNotAllowedException
name|e
parameter_list|)
block|{
comment|// should never happen, this exception is only thrown if we would try to
comment|// list members/includes of an external group, but in case of an external
comment|// group we return before
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

