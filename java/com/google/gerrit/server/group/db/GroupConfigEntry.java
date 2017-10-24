begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.group.db
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
operator|.
name|db
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
name|group
operator|.
name|InternalGroup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Config
import|;
end_import

begin_comment
comment|// TODO(aliceks): Add Javadoc descriptions to this file. Mention that this class must only be used
end_comment

begin_comment
comment|// by GroupConfig and that other classes have to use InternalGroupUpdate!
end_comment

begin_enum
DECL|enum|GroupConfigEntry
enum|enum
name|GroupConfigEntry
block|{
DECL|enumConstant|ID
name|ID
argument_list|(
literal|"id"
argument_list|)
block|{
annotation|@
name|Override
name|void
name|readFromConfig
parameter_list|(
name|InternalGroup
operator|.
name|Builder
name|group
parameter_list|,
name|Config
name|config
parameter_list|)
block|{
name|AccountGroup
operator|.
name|Id
name|id
init|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
name|config
operator|.
name|getInt
argument_list|(
name|SECTION_NAME
argument_list|,
name|super
operator|.
name|keyName
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|group
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|updateConfigValue
parameter_list|(
name|Config
name|config
parameter_list|,
name|InternalGroupUpdate
name|groupUpdate
parameter_list|)
block|{
comment|// Updating the ID is not supported.
block|}
block|}
block|,
DECL|enumConstant|NAME
name|NAME
argument_list|(
literal|"name"
argument_list|)
block|{
annotation|@
name|Override
name|void
name|readFromConfig
parameter_list|(
name|InternalGroup
operator|.
name|Builder
name|group
parameter_list|,
name|Config
name|config
parameter_list|)
block|{
name|AccountGroup
operator|.
name|NameKey
name|name
init|=
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
name|config
operator|.
name|getString
argument_list|(
name|SECTION_NAME
argument_list|,
literal|null
argument_list|,
name|super
operator|.
name|keyName
argument_list|)
argument_list|)
decl_stmt|;
name|group
operator|.
name|setNameKey
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|updateConfigValue
parameter_list|(
name|Config
name|config
parameter_list|,
name|InternalGroupUpdate
name|groupUpdate
parameter_list|)
block|{
name|groupUpdate
operator|.
name|getName
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|name
lambda|->
name|config
operator|.
name|setString
argument_list|(
name|SECTION_NAME
argument_list|,
literal|null
argument_list|,
name|super
operator|.
name|keyName
argument_list|,
name|name
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|,
DECL|enumConstant|DESCRIPTION
name|DESCRIPTION
argument_list|(
literal|"description"
argument_list|)
block|{
annotation|@
name|Override
name|void
name|readFromConfig
parameter_list|(
name|InternalGroup
operator|.
name|Builder
name|group
parameter_list|,
name|Config
name|config
parameter_list|)
block|{
name|String
name|description
init|=
name|config
operator|.
name|getString
argument_list|(
name|SECTION_NAME
argument_list|,
literal|null
argument_list|,
name|super
operator|.
name|keyName
argument_list|)
decl_stmt|;
name|group
operator|.
name|setDescription
argument_list|(
name|description
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|updateConfigValue
parameter_list|(
name|Config
name|config
parameter_list|,
name|InternalGroupUpdate
name|groupUpdate
parameter_list|)
block|{
name|groupUpdate
operator|.
name|getDescription
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|description
lambda|->
name|config
operator|.
name|setString
argument_list|(
name|SECTION_NAME
argument_list|,
literal|null
argument_list|,
name|super
operator|.
name|keyName
argument_list|,
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|description
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|,
comment|// TODO(hiesel) or TODO(ekempin): Replace this property by a permission mechanism.
DECL|enumConstant|OWNER_GROUP_UUID
name|OWNER_GROUP_UUID
argument_list|(
literal|"ownerGroupUuid"
argument_list|)
block|{
annotation|@
name|Override
name|void
name|readFromConfig
parameter_list|(
name|InternalGroup
operator|.
name|Builder
name|group
parameter_list|,
name|Config
name|config
parameter_list|)
block|{
name|String
name|ownerGroupUuid
init|=
name|config
operator|.
name|getString
argument_list|(
name|SECTION_NAME
argument_list|,
literal|null
argument_list|,
name|super
operator|.
name|keyName
argument_list|)
decl_stmt|;
name|group
operator|.
name|setOwnerGroupUUID
argument_list|(
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|ownerGroupUuid
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|updateConfigValue
parameter_list|(
name|Config
name|config
parameter_list|,
name|InternalGroupUpdate
name|groupUpdate
parameter_list|)
block|{
name|groupUpdate
operator|.
name|getOwnerGroupUUID
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|ownerGroupUuid
lambda|->
name|config
operator|.
name|setString
argument_list|(
name|SECTION_NAME
argument_list|,
literal|null
argument_list|,
name|super
operator|.
name|keyName
argument_list|,
name|ownerGroupUuid
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|,
DECL|enumConstant|VISIBLE_TO_ALL
name|VISIBLE_TO_ALL
argument_list|(
literal|"visibleToAll"
argument_list|)
block|{
annotation|@
name|Override
name|void
name|readFromConfig
parameter_list|(
name|InternalGroup
operator|.
name|Builder
name|group
parameter_list|,
name|Config
name|config
parameter_list|)
block|{
name|boolean
name|visibleToAll
init|=
name|config
operator|.
name|getBoolean
argument_list|(
name|SECTION_NAME
argument_list|,
name|super
operator|.
name|keyName
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|group
operator|.
name|setVisibleToAll
argument_list|(
name|visibleToAll
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|updateConfigValue
parameter_list|(
name|Config
name|config
parameter_list|,
name|InternalGroupUpdate
name|groupUpdate
parameter_list|)
block|{
name|groupUpdate
operator|.
name|getVisibleToAll
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|visibleToAll
lambda|->
name|config
operator|.
name|setBoolean
argument_list|(
name|SECTION_NAME
argument_list|,
literal|null
argument_list|,
name|super
operator|.
name|keyName
argument_list|,
name|visibleToAll
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|;
DECL|field|SECTION_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SECTION_NAME
init|=
literal|"group"
decl_stmt|;
DECL|field|keyName
specifier|private
specifier|final
name|String
name|keyName
decl_stmt|;
DECL|method|GroupConfigEntry (String keyName)
name|GroupConfigEntry
parameter_list|(
name|String
name|keyName
parameter_list|)
block|{
name|this
operator|.
name|keyName
operator|=
name|keyName
expr_stmt|;
block|}
DECL|method|readFromConfig (InternalGroup.Builder group, Config config)
specifier|abstract
name|void
name|readFromConfig
parameter_list|(
name|InternalGroup
operator|.
name|Builder
name|group
parameter_list|,
name|Config
name|config
parameter_list|)
function_decl|;
DECL|method|updateConfigValue (Config config, InternalGroupUpdate groupUpdate)
specifier|abstract
name|void
name|updateConfigValue
parameter_list|(
name|Config
name|config
parameter_list|,
name|InternalGroupUpdate
name|groupUpdate
parameter_list|)
function_decl|;
block|}
end_enum

end_unit

