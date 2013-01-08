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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|GroupList
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
name|BadRequestException
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
name|ResourceConflictException
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
name|extensions
operator|.
name|restapi
operator|.
name|TopLevelResource
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
name|OutputFormat
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
name|GroupCache
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
name|VisibleGroups
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
name|ioutil
operator|.
name|ColumnFormatter
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
name|ProjectControl
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
name|util
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
name|gson
operator|.
name|JsonElement
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|reflect
operator|.
name|TypeToken
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
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Map
import|;
end_import

begin_comment
comment|/** List groups visible to the calling user. */
end_comment

begin_class
DECL|class|ListGroups
specifier|public
class|class
name|ListGroups
implements|implements
name|RestReadView
argument_list|<
name|TopLevelResource
argument_list|>
block|{
DECL|field|groupCache
specifier|private
specifier|final
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|visibleGroupsFactory
specifier|private
specifier|final
name|VisibleGroups
operator|.
name|Factory
name|visibleGroupsFactory
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--project"
argument_list|,
name|aliases
operator|=
block|{
literal|"-p"
block|}
argument_list|,
name|usage
operator|=
literal|"projects for which the groups should be listed"
argument_list|)
DECL|field|projects
specifier|private
specifier|final
name|List
argument_list|<
name|ProjectControl
argument_list|>
name|projects
init|=
operator|new
name|ArrayList
argument_list|<
name|ProjectControl
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--visible-to-all"
argument_list|,
name|usage
operator|=
literal|"to list only groups that are visible to all registered users"
argument_list|)
DECL|field|visibleToAll
specifier|private
name|boolean
name|visibleToAll
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--type"
argument_list|,
name|usage
operator|=
literal|"type of group"
argument_list|)
DECL|field|groupType
specifier|private
name|AccountGroup
operator|.
name|Type
name|groupType
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--user"
argument_list|,
name|aliases
operator|=
block|{
literal|"-u"
block|}
argument_list|,
name|usage
operator|=
literal|"user for which the groups should be listed"
argument_list|)
DECL|field|user
specifier|private
name|Account
operator|.
name|Id
name|user
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--verbose"
argument_list|,
name|aliases
operator|=
block|{
literal|"-v"
block|}
argument_list|,
name|usage
operator|=
literal|"verbose output format with tab-separated columns for the "
operator|+
literal|"group name, UUID, description, type, owner group name, "
operator|+
literal|"owner group UUID, and whether the group is visible to all"
argument_list|)
DECL|field|verboseOutput
specifier|private
name|boolean
name|verboseOutput
decl_stmt|;
annotation|@
name|Inject
DECL|method|ListGroups (final GroupCache groupCache, final VisibleGroups.Factory visibleGroupsFactory, final IdentifiedUser.GenericFactory userFactory)
specifier|protected
name|ListGroups
parameter_list|(
specifier|final
name|GroupCache
name|groupCache
parameter_list|,
specifier|final
name|VisibleGroups
operator|.
name|Factory
name|visibleGroupsFactory
parameter_list|,
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
parameter_list|)
block|{
name|this
operator|.
name|groupCache
operator|=
name|groupCache
expr_stmt|;
name|this
operator|.
name|visibleGroupsFactory
operator|=
name|visibleGroupsFactory
expr_stmt|;
name|this
operator|.
name|userFactory
operator|=
name|userFactory
expr_stmt|;
block|}
DECL|method|getUser ()
specifier|public
name|Account
operator|.
name|Id
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
DECL|method|getProjects ()
specifier|public
name|List
argument_list|<
name|ProjectControl
argument_list|>
name|getProjects
parameter_list|()
block|{
return|return
name|projects
return|;
block|}
annotation|@
name|Override
DECL|method|apply (TopLevelResource resource)
specifier|public
name|Object
name|apply
parameter_list|(
name|TopLevelResource
name|resource
parameter_list|)
throws|throws
name|AuthException
throws|,
name|BadRequestException
throws|,
name|ResourceConflictException
throws|,
name|Exception
block|{
return|return
name|display
argument_list|(
literal|null
argument_list|)
return|;
block|}
DECL|method|display (OutputStream displayOutputStream)
specifier|public
name|JsonElement
name|display
parameter_list|(
name|OutputStream
name|displayOutputStream
parameter_list|)
throws|throws
name|NoSuchGroupException
block|{
name|PrintWriter
name|stdout
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|displayOutputStream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|stdout
operator|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|displayOutputStream
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"JVM lacks UTF-8 encoding"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
try|try
block|{
specifier|final
name|VisibleGroups
name|visibleGroups
init|=
name|visibleGroupsFactory
operator|.
name|create
argument_list|()
decl_stmt|;
name|visibleGroups
operator|.
name|setOnlyVisibleToAll
argument_list|(
name|visibleToAll
argument_list|)
expr_stmt|;
name|visibleGroups
operator|.
name|setGroupType
argument_list|(
name|groupType
argument_list|)
expr_stmt|;
specifier|final
name|GroupList
name|groupList
decl_stmt|;
if|if
condition|(
operator|!
name|projects
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|groupList
operator|=
name|visibleGroups
operator|.
name|get
argument_list|(
name|projects
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|groupList
operator|=
name|visibleGroups
operator|.
name|get
argument_list|(
name|userFactory
operator|.
name|create
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|groupList
operator|=
name|visibleGroups
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|stdout
operator|==
literal|null
condition|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|GroupInfo
argument_list|>
name|output
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|AccountGroup
name|g
range|:
name|groupList
operator|.
name|getGroups
argument_list|()
control|)
block|{
specifier|final
name|GroupInfo
name|info
init|=
operator|new
name|GroupInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|name
operator|=
name|g
operator|.
name|getName
argument_list|()
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
name|info
operator|.
name|setUuid
argument_list|(
name|g
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|description
operator|=
name|g
operator|.
name|getDescription
argument_list|()
expr_stmt|;
name|info
operator|.
name|isVisibleToAll
operator|=
name|g
operator|.
name|isVisibleToAll
argument_list|()
expr_stmt|;
name|info
operator|.
name|ownerUuid
operator|=
name|g
operator|.
name|getOwnerGroupUUID
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|output
operator|.
name|put
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
return|return
name|OutputFormat
operator|.
name|JSON
operator|.
name|newGson
argument_list|()
operator|.
name|toJsonTree
argument_list|(
name|output
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|GroupInfo
argument_list|>
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|ColumnFormatter
name|formatter
init|=
operator|new
name|ColumnFormatter
argument_list|(
name|stdout
argument_list|,
literal|'\t'
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|AccountGroup
name|g
range|:
name|groupList
operator|.
name|getGroups
argument_list|()
control|)
block|{
name|formatter
operator|.
name|addColumn
argument_list|(
name|g
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|verboseOutput
condition|)
block|{
name|formatter
operator|.
name|addColumn
argument_list|(
name|KeyUtil
operator|.
name|decode
argument_list|(
name|g
operator|.
name|getGroupUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|formatter
operator|.
name|addColumn
argument_list|(
name|g
operator|.
name|getDescription
argument_list|()
operator|!=
literal|null
condition|?
name|g
operator|.
name|getDescription
argument_list|()
else|:
literal|""
argument_list|)
expr_stmt|;
name|formatter
operator|.
name|addColumn
argument_list|(
name|g
operator|.
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|AccountGroup
name|owningGroup
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|g
operator|.
name|getOwnerGroupUUID
argument_list|()
argument_list|)
decl_stmt|;
name|formatter
operator|.
name|addColumn
argument_list|(
name|owningGroup
operator|!=
literal|null
condition|?
name|owningGroup
operator|.
name|getName
argument_list|()
else|:
literal|"n/a"
argument_list|)
expr_stmt|;
name|formatter
operator|.
name|addColumn
argument_list|(
name|KeyUtil
operator|.
name|decode
argument_list|(
name|g
operator|.
name|getOwnerGroupUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|formatter
operator|.
name|addColumn
argument_list|(
name|Boolean
operator|.
name|toString
argument_list|(
name|g
operator|.
name|isVisibleToAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|formatter
operator|.
name|nextLine
argument_list|()
expr_stmt|;
block|}
name|formatter
operator|.
name|finish
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|stdout
operator|!=
literal|null
condition|)
block|{
name|stdout
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|GroupInfo
specifier|static
class|class
name|GroupInfo
block|{
DECL|field|kind
specifier|final
name|String
name|kind
init|=
literal|"gerritcodereview#group"
decl_stmt|;
DECL|field|name
specifier|transient
name|String
name|name
decl_stmt|;
DECL|field|id
name|String
name|id
decl_stmt|;
DECL|field|uuid
name|String
name|uuid
decl_stmt|;
DECL|field|groupId
name|int
name|groupId
decl_stmt|;
DECL|field|description
name|String
name|description
decl_stmt|;
DECL|field|isVisibleToAll
name|boolean
name|isVisibleToAll
decl_stmt|;
DECL|field|ownerUuid
name|String
name|ownerUuid
decl_stmt|;
DECL|method|setUuid (AccountGroup.UUID u)
name|void
name|setUuid
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|u
parameter_list|)
block|{
name|uuid
operator|=
name|u
operator|.
name|get
argument_list|()
expr_stmt|;
name|id
operator|=
name|Url
operator|.
name|encode
argument_list|(
name|GroupsCollection
operator|.
name|UUID_PREFIX
operator|+
name|uuid
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

