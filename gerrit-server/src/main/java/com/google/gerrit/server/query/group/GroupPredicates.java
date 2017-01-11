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
DECL|package|com.google.gerrit.server.query.group
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|query
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
name|index
operator|.
name|FieldDef
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
name|index
operator|.
name|IndexPredicate
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
name|index
operator|.
name|group
operator|.
name|GroupField
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
name|query
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_class
DECL|class|GroupPredicates
specifier|public
class|class
name|GroupPredicates
block|{
DECL|method|uuid (AccountGroup.UUID uuid)
specifier|public
specifier|static
name|Predicate
argument_list|<
name|AccountGroup
argument_list|>
name|uuid
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
return|return
operator|new
name|GroupPredicate
argument_list|(
name|GroupField
operator|.
name|UUID
argument_list|,
name|GroupQueryBuilder
operator|.
name|FIELD_UUID
argument_list|,
name|uuid
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|method|inname (String name)
specifier|public
specifier|static
name|Predicate
argument_list|<
name|AccountGroup
argument_list|>
name|inname
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|GroupPredicate
argument_list|(
name|GroupField
operator|.
name|NAME_PART
argument_list|,
name|GroupQueryBuilder
operator|.
name|FIELD_INNAME
argument_list|,
name|name
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|)
return|;
block|}
DECL|method|name (String name)
specifier|public
specifier|static
name|Predicate
argument_list|<
name|AccountGroup
argument_list|>
name|name
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|GroupPredicate
argument_list|(
name|GroupField
operator|.
name|NAME
argument_list|,
name|GroupQueryBuilder
operator|.
name|FIELD_NAME
argument_list|,
name|name
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|)
return|;
block|}
DECL|class|GroupPredicate
specifier|static
class|class
name|GroupPredicate
extends|extends
name|IndexPredicate
argument_list|<
name|AccountGroup
argument_list|>
block|{
DECL|method|GroupPredicate (FieldDef<AccountGroup, ?> def, String value)
name|GroupPredicate
parameter_list|(
name|FieldDef
argument_list|<
name|AccountGroup
argument_list|,
name|?
argument_list|>
name|def
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|def
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|GroupPredicate (FieldDef<AccountGroup, ?> def, String name, String value)
name|GroupPredicate
parameter_list|(
name|FieldDef
argument_list|<
name|AccountGroup
argument_list|,
name|?
argument_list|>
name|def
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|def
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|GroupPredicates ()
specifier|private
name|GroupPredicates
parameter_list|()
block|{   }
block|}
end_class

end_unit

