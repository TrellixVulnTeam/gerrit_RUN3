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
DECL|package|com.google.gerrit.server.query.project
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
name|project
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
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|primitives
operator|.
name|Ints
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
name|ProjectState
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
name|index
operator|.
name|project
operator|.
name|ProjectData
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
name|index
operator|.
name|query
operator|.
name|LimitPredicate
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
name|index
operator|.
name|query
operator|.
name|Predicate
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
name|index
operator|.
name|query
operator|.
name|QueryBuilder
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
name|index
operator|.
name|query
operator|.
name|QueryParseException
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
name|Project
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/** Parses a query string meant to be applied to project objects. */
end_comment

begin_class
DECL|class|ProjectQueryBuilder
specifier|public
class|class
name|ProjectQueryBuilder
extends|extends
name|QueryBuilder
argument_list|<
name|ProjectData
argument_list|>
block|{
DECL|field|FIELD_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_LIMIT
init|=
literal|"limit"
decl_stmt|;
DECL|field|mydef
specifier|private
specifier|static
specifier|final
name|QueryBuilder
operator|.
name|Definition
argument_list|<
name|ProjectData
argument_list|,
name|ProjectQueryBuilder
argument_list|>
name|mydef
init|=
operator|new
name|QueryBuilder
operator|.
name|Definition
argument_list|<>
argument_list|(
name|ProjectQueryBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Inject
DECL|method|ProjectQueryBuilder ()
name|ProjectQueryBuilder
parameter_list|()
block|{
name|super
argument_list|(
name|mydef
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Operator
DECL|method|name (String name)
specifier|public
name|Predicate
argument_list|<
name|ProjectData
argument_list|>
name|name
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|ProjectPredicates
operator|.
name|name
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Operator
DECL|method|parent (String parentName)
specifier|public
name|Predicate
argument_list|<
name|ProjectData
argument_list|>
name|parent
parameter_list|(
name|String
name|parentName
parameter_list|)
block|{
return|return
name|ProjectPredicates
operator|.
name|parent
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|parentName
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Operator
DECL|method|inname (String namePart)
specifier|public
name|Predicate
argument_list|<
name|ProjectData
argument_list|>
name|inname
parameter_list|(
name|String
name|namePart
parameter_list|)
block|{
if|if
condition|(
name|namePart
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|name
argument_list|(
name|namePart
argument_list|)
return|;
block|}
return|return
name|ProjectPredicates
operator|.
name|inname
argument_list|(
name|namePart
argument_list|)
return|;
block|}
annotation|@
name|Operator
DECL|method|description (String description)
specifier|public
name|Predicate
argument_list|<
name|ProjectData
argument_list|>
name|description
parameter_list|(
name|String
name|description
parameter_list|)
throws|throws
name|QueryParseException
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|description
argument_list|)
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"description operator requires a value"
argument_list|)
throw|;
block|}
return|return
name|ProjectPredicates
operator|.
name|description
argument_list|(
name|description
argument_list|)
return|;
block|}
annotation|@
name|Operator
DECL|method|state (String state)
specifier|public
name|Predicate
argument_list|<
name|ProjectData
argument_list|>
name|state
parameter_list|(
name|String
name|state
parameter_list|)
throws|throws
name|QueryParseException
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|state
argument_list|)
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"state operator requires a value"
argument_list|)
throw|;
block|}
name|ProjectState
name|parsedState
decl_stmt|;
try|try
block|{
name|parsedState
operator|=
name|ProjectState
operator|.
name|valueOf
argument_list|(
name|state
operator|.
name|replace
argument_list|(
literal|'-'
argument_list|,
literal|'_'
argument_list|)
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
name|error
argument_list|(
literal|"state operator must be either 'active' or 'read-only'"
argument_list|)
throw|;
block|}
if|if
condition|(
name|parsedState
operator|==
name|ProjectState
operator|.
name|HIDDEN
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"state operator must be either 'active' or 'read-only'"
argument_list|)
throw|;
block|}
return|return
name|ProjectPredicates
operator|.
name|state
argument_list|(
name|parsedState
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|defaultField (String query)
specifier|protected
name|Predicate
argument_list|<
name|ProjectData
argument_list|>
name|defaultField
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|QueryParseException
block|{
comment|// Adapt the capacity of this list when adding more default predicates.
name|List
argument_list|<
name|Predicate
argument_list|<
name|ProjectData
argument_list|>
argument_list|>
name|preds
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|preds
operator|.
name|add
argument_list|(
name|name
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
name|preds
operator|.
name|add
argument_list|(
name|inname
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|query
argument_list|)
condition|)
block|{
name|preds
operator|.
name|add
argument_list|(
name|description
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Predicate
operator|.
name|or
argument_list|(
name|preds
argument_list|)
return|;
block|}
annotation|@
name|Operator
DECL|method|limit (String query)
specifier|public
name|Predicate
argument_list|<
name|ProjectData
argument_list|>
name|limit
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|QueryParseException
block|{
name|Integer
name|limit
init|=
name|Ints
operator|.
name|tryParse
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|limit
operator|==
literal|null
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"Invalid limit: "
operator|+
name|query
argument_list|)
throw|;
block|}
return|return
operator|new
name|LimitPredicate
argument_list|<>
argument_list|(
name|FIELD_LIMIT
argument_list|,
name|limit
argument_list|)
return|;
block|}
block|}
end_class

end_unit

