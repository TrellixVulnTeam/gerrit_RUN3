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
name|util
operator|.
name|Url
import|;
end_import

begin_class
DECL|class|GetGroup
class|class
name|GetGroup
implements|implements
name|RestReadView
argument_list|<
name|GroupResource
argument_list|>
block|{
annotation|@
name|Override
DECL|method|apply (GroupResource resource)
specifier|public
name|Object
name|apply
parameter_list|(
name|GroupResource
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
name|GroupDescription
operator|.
name|Basic
name|group
init|=
name|resource
operator|.
name|getControl
argument_list|()
operator|.
name|getGroup
argument_list|()
decl_stmt|;
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
name|resource
operator|.
name|getName
argument_list|()
expr_stmt|;
name|info
operator|.
name|uuid
operator|=
name|resource
operator|.
name|getGroupUUID
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|info
operator|.
name|isVisibleToAll
operator|=
name|group
operator|.
name|isVisibleToAll
argument_list|()
expr_stmt|;
if|if
condition|(
name|group
operator|instanceof
name|GroupDescription
operator|.
name|Internal
condition|)
block|{
specifier|final
name|AccountGroup
name|internalGroup
init|=
operator|(
operator|(
name|GroupDescription
operator|.
name|Internal
operator|)
name|group
operator|)
operator|.
name|getAccountGroup
argument_list|()
decl_stmt|;
name|info
operator|.
name|description
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|internalGroup
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|ownerUuid
operator|=
name|internalGroup
operator|.
name|getOwnerGroupUUID
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|info
operator|.
name|finish
argument_list|()
expr_stmt|;
return|return
name|info
return|;
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
DECL|field|id
name|String
name|id
decl_stmt|;
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|uuid
name|String
name|uuid
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
DECL|method|finish ()
name|void
name|finish
parameter_list|()
block|{
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

