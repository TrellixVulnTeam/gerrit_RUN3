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
DECL|package|com.google.gerrit.server.index
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|index
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
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
name|collect
operator|.
name|ImmutableList
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
name|collect
operator|.
name|ImmutableMap
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
name|collect
operator|.
name|Iterables
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
name|server
operator|.
name|query
operator|.
name|change
operator|.
name|ChangeData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|ParameterizedType
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
name|Map
import|;
end_import

begin_comment
comment|/** Secondary index schemas for changes. */
end_comment

begin_class
DECL|class|ChangeSchemas
specifier|public
class|class
name|ChangeSchemas
block|{
DECL|field|V25
specifier|static
specifier|final
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|V25
init|=
name|schema
argument_list|(
name|ChangeField
operator|.
name|LEGACY_ID2
argument_list|,
name|ChangeField
operator|.
name|ID
argument_list|,
name|ChangeField
operator|.
name|STATUS
argument_list|,
name|ChangeField
operator|.
name|PROJECT
argument_list|,
name|ChangeField
operator|.
name|PROJECTS
argument_list|,
name|ChangeField
operator|.
name|REF
argument_list|,
name|ChangeField
operator|.
name|EXACT_TOPIC
argument_list|,
name|ChangeField
operator|.
name|FUZZY_TOPIC
argument_list|,
name|ChangeField
operator|.
name|UPDATED
argument_list|,
name|ChangeField
operator|.
name|FILE_PART
argument_list|,
name|ChangeField
operator|.
name|PATH
argument_list|,
name|ChangeField
operator|.
name|OWNER
argument_list|,
name|ChangeField
operator|.
name|REVIEWER
argument_list|,
name|ChangeField
operator|.
name|COMMIT
argument_list|,
name|ChangeField
operator|.
name|TR
argument_list|,
name|ChangeField
operator|.
name|LABEL
argument_list|,
name|ChangeField
operator|.
name|COMMIT_MESSAGE
argument_list|,
name|ChangeField
operator|.
name|COMMENT
argument_list|,
name|ChangeField
operator|.
name|CHANGE
argument_list|,
name|ChangeField
operator|.
name|APPROVAL
argument_list|,
name|ChangeField
operator|.
name|MERGEABLE
argument_list|,
name|ChangeField
operator|.
name|ADDED
argument_list|,
name|ChangeField
operator|.
name|DELETED
argument_list|,
name|ChangeField
operator|.
name|DELTA
argument_list|,
name|ChangeField
operator|.
name|HASHTAG
argument_list|,
name|ChangeField
operator|.
name|COMMENTBY
argument_list|,
name|ChangeField
operator|.
name|PATCH_SET
argument_list|,
name|ChangeField
operator|.
name|GROUP
argument_list|,
name|ChangeField
operator|.
name|SUBMISSIONID
argument_list|,
name|ChangeField
operator|.
name|EDITBY
argument_list|,
name|ChangeField
operator|.
name|REVIEWEDBY
argument_list|,
name|ChangeField
operator|.
name|EXACT_COMMIT
argument_list|,
name|ChangeField
operator|.
name|AUTHOR
argument_list|,
name|ChangeField
operator|.
name|COMMITTER
argument_list|)
decl_stmt|;
DECL|method|schema (Collection<FieldDef<ChangeData, ?>> fields)
specifier|private
specifier|static
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|schema
parameter_list|(
name|Collection
argument_list|<
name|FieldDef
argument_list|<
name|ChangeData
argument_list|,
name|?
argument_list|>
argument_list|>
name|fields
parameter_list|)
block|{
return|return
operator|new
name|Schema
argument_list|<>
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|fields
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|SafeVarargs
DECL|method|schema (FieldDef<ChangeData, ?>.... fields)
specifier|private
specifier|static
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|schema
parameter_list|(
name|FieldDef
argument_list|<
name|ChangeData
argument_list|,
name|?
argument_list|>
modifier|...
name|fields
parameter_list|)
block|{
return|return
name|schema
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|fields
argument_list|)
argument_list|)
return|;
block|}
DECL|field|ALL
specifier|public
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|Integer
argument_list|,
name|Schema
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|ALL
decl_stmt|;
DECL|method|get (int version)
specifier|public
specifier|static
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|get
parameter_list|(
name|int
name|version
parameter_list|)
block|{
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|schema
init|=
name|ALL
operator|.
name|get
argument_list|(
name|version
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|schema
operator|!=
literal|null
argument_list|,
literal|"Unrecognized schema version: %s"
argument_list|,
name|version
argument_list|)
expr_stmt|;
return|return
name|schema
return|;
block|}
DECL|method|getLatest ()
specifier|public
specifier|static
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|getLatest
parameter_list|()
block|{
return|return
name|Iterables
operator|.
name|getLast
argument_list|(
name|ALL
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
static|static
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|Schema
argument_list|<
name|ChangeData
argument_list|>
argument_list|>
name|all
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|f
range|:
name|ChangeSchemas
operator|.
name|class
operator|.
name|getDeclaredFields
argument_list|()
control|)
block|{
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|f
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
name|Modifier
operator|.
name|isFinal
argument_list|(
name|f
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
name|Schema
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|f
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|ParameterizedType
name|t
init|=
operator|(
name|ParameterizedType
operator|)
name|f
operator|.
name|getGenericType
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|getActualTypeArguments
argument_list|()
index|[
literal|0
index|]
operator|==
name|ChangeData
operator|.
name|class
condition|)
block|{
try|try
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|schema
init|=
operator|(
name|Schema
argument_list|<
name|ChangeData
argument_list|>
operator|)
name|f
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|f
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"V"
argument_list|)
argument_list|)
expr_stmt|;
name|schema
operator|.
name|setVersion
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|f
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|all
operator|.
name|put
argument_list|(
name|schema
operator|.
name|getVersion
argument_list|()
argument_list|,
name|schema
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
decl||
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ExceptionInInitializerError
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ExceptionInInitializerError
argument_list|(
literal|"non-ChangeData schema: "
operator|+
name|f
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|all
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ExceptionInInitializerError
argument_list|(
literal|"no ChangeSchemas found"
argument_list|)
throw|;
block|}
name|ALL
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|all
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

