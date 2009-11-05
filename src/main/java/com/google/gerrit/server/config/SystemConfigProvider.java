begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|config
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
name|reviewdb
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
name|client
operator|.
name|reviewdb
operator|.
name|ApprovalCategory
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
name|reviewdb
operator|.
name|ApprovalCategoryValue
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
name|client
operator|.
name|reviewdb
operator|.
name|ProjectRight
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
name|reviewdb
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
name|client
operator|.
name|reviewdb
operator|.
name|SchemaVersion
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
name|reviewdb
operator|.
name|SystemConfig
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
name|workflow
operator|.
name|NoOpFunction
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
name|workflow
operator|.
name|SubmitFunction
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|server
operator|.
name|SignedToken
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
name|OrmException
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
name|SchemaFactory
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
name|Transaction
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|ProvisionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Collections
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

begin_comment
comment|/** Loads the {@link SystemConfig} from the database. */
end_comment

begin_class
DECL|class|SystemConfigProvider
class|class
name|SystemConfigProvider
implements|implements
name|Provider
argument_list|<
name|SystemConfig
argument_list|>
block|{
DECL|field|DEFAULT_WILD_NAME
specifier|private
specifier|static
specifier|final
name|Project
operator|.
name|NameKey
name|DEFAULT_WILD_NAME
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"-- All Projects --"
argument_list|)
decl_stmt|;
DECL|field|schema
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
annotation|@
name|Inject
DECL|method|SystemConfigProvider (final SchemaFactory<ReviewDb> sf)
name|SystemConfigProvider
parameter_list|(
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|sf
parameter_list|)
block|{
name|schema
operator|=
name|sf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|SystemConfig
name|get
parameter_list|()
block|{
try|try
block|{
specifier|final
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
name|SchemaVersion
name|sVer
init|=
name|getSchemaVersion
argument_list|(
name|db
argument_list|)
decl_stmt|;
if|if
condition|(
name|sVer
operator|==
literal|null
condition|)
block|{
comment|// Assume the schema is empty and try to populate it.
comment|//
name|sVer
operator|=
name|createSchema
argument_list|(
name|db
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|sVer
operator|.
name|versionNbr
condition|)
block|{
case|case
literal|2
case|:
name|initPushTagCategory
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|initPushUpdateBranchCategory
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|sVer
operator|.
name|versionNbr
operator|=
literal|3
expr_stmt|;
name|db
operator|.
name|schemaVersion
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|sVer
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|15
case|:
name|sVer
operator|.
name|versionNbr
operator|=
literal|16
expr_stmt|;
name|db
operator|.
name|schemaVersion
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|sVer
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|sVer
operator|.
name|versionNbr
operator|!=
name|ReviewDb
operator|.
name|VERSION
condition|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Unsupported schema version "
operator|+
name|sVer
operator|.
name|versionNbr
operator|+
literal|"; expected schema version "
operator|+
name|ReviewDb
operator|.
name|VERSION
argument_list|)
throw|;
block|}
specifier|final
name|List
argument_list|<
name|SystemConfig
argument_list|>
name|all
init|=
name|db
operator|.
name|systemConfig
argument_list|()
operator|.
name|all
argument_list|()
operator|.
name|toList
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|all
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|1
case|:
return|return
name|all
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
case|case
literal|0
case|:
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"system_config table is empty"
argument_list|)
throw|;
default|default:
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"system_config must have exactly 1 row;"
operator|+
literal|" found "
operator|+
name|all
operator|.
name|size
argument_list|()
operator|+
literal|" rows instead"
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
literal|"Cannot read system_config"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|createSchema (final ReviewDb db)
specifier|private
name|SchemaVersion
name|createSchema
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
name|db
operator|.
name|createSchema
argument_list|()
expr_stmt|;
specifier|final
name|SchemaVersion
name|sVer
init|=
name|SchemaVersion
operator|.
name|create
argument_list|()
decl_stmt|;
name|sVer
operator|.
name|versionNbr
operator|=
name|ReviewDb
operator|.
name|VERSION
expr_stmt|;
name|db
operator|.
name|schemaVersion
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|sVer
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|SystemConfig
name|sConfig
init|=
name|initSystemConfig
argument_list|(
name|db
argument_list|)
decl_stmt|;
name|initOwnerCategory
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|initReadCategory
argument_list|(
name|db
argument_list|,
name|sConfig
argument_list|)
expr_stmt|;
name|initVerifiedCategory
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|initCodeReviewCategory
argument_list|(
name|db
argument_list|,
name|sConfig
argument_list|)
expr_stmt|;
name|initSubmitCategory
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|initPushTagCategory
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|initPushUpdateBranchCategory
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|initWildCardProject
argument_list|(
name|db
argument_list|)
expr_stmt|;
return|return
name|sVer
return|;
block|}
DECL|method|getSchemaVersion (final ReviewDb db)
specifier|private
name|SchemaVersion
name|getSchemaVersion
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
block|{
try|try
block|{
return|return
name|db
operator|.
name|schemaVersion
argument_list|()
operator|.
name|get
argument_list|(
operator|new
name|SchemaVersion
operator|.
name|Key
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|initSystemConfig (final ReviewDb c)
specifier|private
name|SystemConfig
name|initSystemConfig
parameter_list|(
specifier|final
name|ReviewDb
name|c
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|AccountGroup
name|admin
init|=
operator|new
name|AccountGroup
argument_list|(
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
literal|"Administrators"
argument_list|)
argument_list|,
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
name|c
operator|.
name|nextAccountGroupId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|admin
operator|.
name|setDescription
argument_list|(
literal|"Gerrit Site Administrators"
argument_list|)
expr_stmt|;
name|admin
operator|.
name|setType
argument_list|(
name|AccountGroup
operator|.
name|Type
operator|.
name|INTERNAL
argument_list|)
expr_stmt|;
name|c
operator|.
name|accountGroups
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|admin
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|AccountGroup
name|anonymous
init|=
operator|new
name|AccountGroup
argument_list|(
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
literal|"Anonymous Users"
argument_list|)
argument_list|,
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
name|c
operator|.
name|nextAccountGroupId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|anonymous
operator|.
name|setDescription
argument_list|(
literal|"Any user, signed-in or not"
argument_list|)
expr_stmt|;
name|anonymous
operator|.
name|setOwnerGroupId
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|anonymous
operator|.
name|setType
argument_list|(
name|AccountGroup
operator|.
name|Type
operator|.
name|SYSTEM
argument_list|)
expr_stmt|;
name|c
operator|.
name|accountGroups
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|anonymous
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|AccountGroup
name|registered
init|=
operator|new
name|AccountGroup
argument_list|(
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
literal|"Registered Users"
argument_list|)
argument_list|,
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
name|c
operator|.
name|nextAccountGroupId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|registered
operator|.
name|setDescription
argument_list|(
literal|"Any signed-in user"
argument_list|)
expr_stmt|;
name|registered
operator|.
name|setOwnerGroupId
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|registered
operator|.
name|setType
argument_list|(
name|AccountGroup
operator|.
name|Type
operator|.
name|SYSTEM
argument_list|)
expr_stmt|;
name|c
operator|.
name|accountGroups
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|registered
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|sitePath
init|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"."
operator|.
name|equals
argument_list|(
name|sitePath
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|sitePath
operator|=
name|sitePath
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
specifier|final
name|SystemConfig
name|s
init|=
name|SystemConfig
operator|.
name|create
argument_list|()
decl_stmt|;
name|s
operator|.
name|registerEmailPrivateKey
operator|=
name|SignedToken
operator|.
name|generateRandomKey
argument_list|()
expr_stmt|;
name|s
operator|.
name|adminGroupId
operator|=
name|admin
operator|.
name|getId
argument_list|()
expr_stmt|;
name|s
operator|.
name|anonymousGroupId
operator|=
name|anonymous
operator|.
name|getId
argument_list|()
expr_stmt|;
name|s
operator|.
name|registeredGroupId
operator|=
name|registered
operator|.
name|getId
argument_list|()
expr_stmt|;
name|s
operator|.
name|sitePath
operator|=
name|sitePath
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|c
operator|.
name|systemConfig
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
DECL|method|initWildCardProject (final ReviewDb c)
specifier|private
name|void
name|initWildCardProject
parameter_list|(
specifier|final
name|ReviewDb
name|c
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|Project
name|p
decl_stmt|;
name|p
operator|=
operator|new
name|Project
argument_list|(
name|DEFAULT_WILD_NAME
argument_list|,
name|WildProjectNameProvider
operator|.
name|WILD_PROJECT_ID
argument_list|)
expr_stmt|;
name|p
operator|.
name|setDescription
argument_list|(
literal|"Rights inherited by all other projects"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setUseContributorAgreements
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|c
operator|.
name|projects
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|initVerifiedCategory (final ReviewDb c)
specifier|private
name|void
name|initVerifiedCategory
parameter_list|(
specifier|final
name|ReviewDb
name|c
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|Transaction
name|txn
init|=
name|c
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|ApprovalCategory
name|cat
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
name|vals
decl_stmt|;
name|cat
operator|=
operator|new
name|ApprovalCategory
argument_list|(
operator|new
name|ApprovalCategory
operator|.
name|Id
argument_list|(
literal|"VRIF"
argument_list|)
argument_list|,
literal|"Verified"
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setPosition
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setAbbreviatedName
argument_list|(
literal|"V"
argument_list|)
expr_stmt|;
name|vals
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
argument_list|()
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
literal|1
argument_list|,
literal|"Verified"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
literal|0
argument_list|,
literal|"No score"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
operator|-
literal|1
argument_list|,
literal|"Fails"
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategories
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|cat
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategoryValues
argument_list|()
operator|.
name|insert
argument_list|(
name|vals
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|initCodeReviewCategory (final ReviewDb c, final SystemConfig sConfig)
specifier|private
name|void
name|initCodeReviewCategory
parameter_list|(
specifier|final
name|ReviewDb
name|c
parameter_list|,
specifier|final
name|SystemConfig
name|sConfig
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|Transaction
name|txn
init|=
name|c
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|ApprovalCategory
name|cat
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
name|vals
decl_stmt|;
name|cat
operator|=
operator|new
name|ApprovalCategory
argument_list|(
operator|new
name|ApprovalCategory
operator|.
name|Id
argument_list|(
literal|"CRVW"
argument_list|)
argument_list|,
literal|"Code Review"
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setPosition
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setAbbreviatedName
argument_list|(
literal|"R"
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setCopyMinScore
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|vals
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
argument_list|()
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
literal|2
argument_list|,
literal|"Looks good to me, approved"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
literal|1
argument_list|,
literal|"Looks good to me, but someone else must approve"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
literal|0
argument_list|,
literal|"No score"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
operator|-
literal|1
argument_list|,
literal|"I would prefer that you didn't submit this"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
operator|-
literal|2
argument_list|,
literal|"Do not submit"
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategories
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|cat
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategoryValues
argument_list|()
operator|.
name|insert
argument_list|(
name|vals
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|ProjectRight
name|approve
init|=
operator|new
name|ProjectRight
argument_list|(
operator|new
name|ProjectRight
operator|.
name|Key
argument_list|(
name|DEFAULT_WILD_NAME
argument_list|,
name|cat
operator|.
name|getId
argument_list|()
argument_list|,
name|sConfig
operator|.
name|registeredGroupId
argument_list|)
argument_list|)
decl_stmt|;
name|approve
operator|.
name|setMaxValue
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|approve
operator|.
name|setMinValue
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
name|c
operator|.
name|projectRights
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|approve
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|initOwnerCategory (final ReviewDb c)
specifier|private
name|void
name|initOwnerCategory
parameter_list|(
specifier|final
name|ReviewDb
name|c
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|Transaction
name|txn
init|=
name|c
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|ApprovalCategory
name|cat
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
name|vals
decl_stmt|;
name|cat
operator|=
operator|new
name|ApprovalCategory
argument_list|(
name|ApprovalCategory
operator|.
name|OWN
argument_list|,
literal|"Owner"
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setPosition
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setFunctionName
argument_list|(
name|NoOpFunction
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|vals
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
argument_list|()
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
literal|1
argument_list|,
literal|"Administer All Settings"
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategories
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|cat
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategoryValues
argument_list|()
operator|.
name|insert
argument_list|(
name|vals
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|initReadCategory (final ReviewDb c, final SystemConfig sConfig)
specifier|private
name|void
name|initReadCategory
parameter_list|(
specifier|final
name|ReviewDb
name|c
parameter_list|,
specifier|final
name|SystemConfig
name|sConfig
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|Transaction
name|txn
init|=
name|c
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|ApprovalCategory
name|cat
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
name|vals
decl_stmt|;
name|cat
operator|=
operator|new
name|ApprovalCategory
argument_list|(
name|ApprovalCategory
operator|.
name|READ
argument_list|,
literal|"Read Access"
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setPosition
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setFunctionName
argument_list|(
name|NoOpFunction
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|vals
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
argument_list|()
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
literal|2
argument_list|,
literal|"Upload permission"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
literal|1
argument_list|,
literal|"Read access"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
operator|-
literal|1
argument_list|,
literal|"No access"
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategories
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|cat
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategoryValues
argument_list|()
operator|.
name|insert
argument_list|(
name|vals
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
block|{
specifier|final
name|ProjectRight
name|read
init|=
operator|new
name|ProjectRight
argument_list|(
operator|new
name|ProjectRight
operator|.
name|Key
argument_list|(
name|DEFAULT_WILD_NAME
argument_list|,
name|cat
operator|.
name|getId
argument_list|()
argument_list|,
name|sConfig
operator|.
name|anonymousGroupId
argument_list|)
argument_list|)
decl_stmt|;
name|read
operator|.
name|setMaxValue
argument_list|(
operator|(
name|short
operator|)
literal|2
argument_list|)
expr_stmt|;
name|read
operator|.
name|setMinValue
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|c
operator|.
name|projectRights
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|read
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
specifier|final
name|ProjectRight
name|read
init|=
operator|new
name|ProjectRight
argument_list|(
operator|new
name|ProjectRight
operator|.
name|Key
argument_list|(
name|DEFAULT_WILD_NAME
argument_list|,
name|cat
operator|.
name|getId
argument_list|()
argument_list|,
name|sConfig
operator|.
name|adminGroupId
argument_list|)
argument_list|)
decl_stmt|;
name|read
operator|.
name|setMaxValue
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|read
operator|.
name|setMinValue
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|c
operator|.
name|projectRights
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|read
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initSubmitCategory (final ReviewDb c)
specifier|private
name|void
name|initSubmitCategory
parameter_list|(
specifier|final
name|ReviewDb
name|c
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|Transaction
name|txn
init|=
name|c
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|ApprovalCategory
name|cat
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
name|vals
decl_stmt|;
name|cat
operator|=
operator|new
name|ApprovalCategory
argument_list|(
name|ApprovalCategory
operator|.
name|SUBMIT
argument_list|,
literal|"Submit"
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setPosition
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setFunctionName
argument_list|(
name|SubmitFunction
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|vals
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
argument_list|()
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
literal|1
argument_list|,
literal|"Submit"
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategories
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|cat
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategoryValues
argument_list|()
operator|.
name|insert
argument_list|(
name|vals
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|initPushTagCategory (final ReviewDb c)
specifier|private
name|void
name|initPushTagCategory
parameter_list|(
specifier|final
name|ReviewDb
name|c
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|Transaction
name|txn
init|=
name|c
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|ApprovalCategory
name|cat
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
name|vals
decl_stmt|;
name|cat
operator|=
operator|new
name|ApprovalCategory
argument_list|(
name|ApprovalCategory
operator|.
name|PUSH_TAG
argument_list|,
literal|"Push Annotated Tag"
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setPosition
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setFunctionName
argument_list|(
name|NoOpFunction
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|vals
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
argument_list|()
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
name|ApprovalCategory
operator|.
name|PUSH_TAG_SIGNED
argument_list|,
literal|"Create Signed Tag"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
name|ApprovalCategory
operator|.
name|PUSH_TAG_ANNOTATED
argument_list|,
literal|"Create Annotated Tag"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
name|ApprovalCategory
operator|.
name|PUSH_TAG_ANY
argument_list|,
literal|"Create Any Tag"
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategories
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|cat
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategoryValues
argument_list|()
operator|.
name|insert
argument_list|(
name|vals
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|initPushUpdateBranchCategory (final ReviewDb c)
specifier|private
name|void
name|initPushUpdateBranchCategory
parameter_list|(
specifier|final
name|ReviewDb
name|c
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|Transaction
name|txn
init|=
name|c
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|ApprovalCategory
name|cat
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
name|vals
decl_stmt|;
name|cat
operator|=
operator|new
name|ApprovalCategory
argument_list|(
name|ApprovalCategory
operator|.
name|PUSH_HEAD
argument_list|,
literal|"Push Branch"
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setPosition
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
name|cat
operator|.
name|setFunctionName
argument_list|(
name|NoOpFunction
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|vals
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
argument_list|()
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
name|ApprovalCategory
operator|.
name|PUSH_HEAD_UPDATE
argument_list|,
literal|"Update Branch"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
name|ApprovalCategory
operator|.
name|PUSH_HEAD_CREATE
argument_list|,
literal|"Create Branch"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
name|ApprovalCategory
operator|.
name|PUSH_HEAD_REPLACE
argument_list|,
literal|"Force Push Branch; Delete Branch"
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategories
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|cat
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategoryValues
argument_list|()
operator|.
name|insert
argument_list|(
name|vals
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|value (final ApprovalCategory cat, final int value, final String name)
specifier|private
specifier|static
name|ApprovalCategoryValue
name|value
parameter_list|(
specifier|final
name|ApprovalCategory
name|cat
parameter_list|,
specifier|final
name|int
name|value
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ApprovalCategoryValue
argument_list|(
operator|new
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|(
name|cat
operator|.
name|getId
argument_list|()
argument_list|,
operator|(
name|short
operator|)
name|value
argument_list|)
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

