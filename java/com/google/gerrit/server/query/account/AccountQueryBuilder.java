begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.query.account
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
name|account
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
name|Splitter
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
name|flogger
operator|.
name|FluentLogger
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
name|exceptions
operator|.
name|NotSignedInException
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
name|exceptions
operator|.
name|StorageException
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
name|index
operator|.
name|Index
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
name|Schema
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
name|account
operator|.
name|AccountState
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
name|change
operator|.
name|ChangeFinder
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
name|account
operator|.
name|AccountField
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
name|account
operator|.
name|AccountIndexCollection
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
name|notedb
operator|.
name|ChangeNotes
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
name|permissions
operator|.
name|ChangePermission
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
name|permissions
operator|.
name|GlobalPermission
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
name|permissions
operator|.
name|PermissionBackend
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
name|permissions
operator|.
name|PermissionBackendException
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

begin_comment
comment|/** Parses a query string meant to be applied to account objects. */
end_comment

begin_class
DECL|class|AccountQueryBuilder
specifier|public
class|class
name|AccountQueryBuilder
extends|extends
name|QueryBuilder
argument_list|<
name|AccountState
argument_list|,
name|AccountQueryBuilder
argument_list|>
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|field|FIELD_ACCOUNT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_ACCOUNT
init|=
literal|"account"
decl_stmt|;
DECL|field|FIELD_CAN_SEE
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_CAN_SEE
init|=
literal|"cansee"
decl_stmt|;
DECL|field|FIELD_EMAIL
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_EMAIL
init|=
literal|"email"
decl_stmt|;
DECL|field|FIELD_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_LIMIT
init|=
literal|"limit"
decl_stmt|;
DECL|field|FIELD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_NAME
init|=
literal|"name"
decl_stmt|;
DECL|field|FIELD_PREFERRED_EMAIL
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_PREFERRED_EMAIL
init|=
literal|"preferredemail"
decl_stmt|;
DECL|field|FIELD_PREFERRED_EMAIL_EXACT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_PREFERRED_EMAIL_EXACT
init|=
literal|"preferredemail_exact"
decl_stmt|;
DECL|field|FIELD_USERNAME
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_USERNAME
init|=
literal|"username"
decl_stmt|;
DECL|field|FIELD_VISIBLETO
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_VISIBLETO
init|=
literal|"visibleto"
decl_stmt|;
DECL|field|mydef
specifier|private
specifier|static
specifier|final
name|QueryBuilder
operator|.
name|Definition
argument_list|<
name|AccountState
argument_list|,
name|AccountQueryBuilder
argument_list|>
name|mydef
init|=
operator|new
name|QueryBuilder
operator|.
name|Definition
argument_list|<>
argument_list|(
name|AccountQueryBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|Arguments
specifier|public
specifier|static
class|class
name|Arguments
block|{
DECL|field|changeFinder
specifier|final
name|ChangeFinder
name|changeFinder
decl_stmt|;
DECL|field|permissionBackend
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|self
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
decl_stmt|;
DECL|field|indexes
specifier|private
specifier|final
name|AccountIndexCollection
name|indexes
decl_stmt|;
annotation|@
name|Inject
DECL|method|Arguments ( Provider<CurrentUser> self, AccountIndexCollection indexes, ChangeFinder changeFinder, PermissionBackend permissionBackend)
specifier|public
name|Arguments
parameter_list|(
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
parameter_list|,
name|AccountIndexCollection
name|indexes
parameter_list|,
name|ChangeFinder
name|changeFinder
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|)
block|{
name|this
operator|.
name|self
operator|=
name|self
expr_stmt|;
name|this
operator|.
name|indexes
operator|=
name|indexes
expr_stmt|;
name|this
operator|.
name|changeFinder
operator|=
name|changeFinder
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
block|}
DECL|method|getIdentifiedUser ()
name|IdentifiedUser
name|getIdentifiedUser
parameter_list|()
throws|throws
name|QueryParseException
block|{
try|try
block|{
name|CurrentUser
name|u
init|=
name|getUser
argument_list|()
decl_stmt|;
if|if
condition|(
name|u
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
return|return
name|u
operator|.
name|asIdentifiedUser
argument_list|()
return|;
block|}
throw|throw
operator|new
name|QueryParseException
argument_list|(
name|NotSignedInException
operator|.
name|MESSAGE
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ProvisionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryParseException
argument_list|(
name|NotSignedInException
operator|.
name|MESSAGE
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getUser ()
name|CurrentUser
name|getUser
parameter_list|()
throws|throws
name|QueryParseException
block|{
try|try
block|{
return|return
name|self
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ProvisionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryParseException
argument_list|(
name|NotSignedInException
operator|.
name|MESSAGE
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|schema ()
name|Schema
argument_list|<
name|AccountState
argument_list|>
name|schema
parameter_list|()
block|{
name|Index
argument_list|<
name|?
argument_list|,
name|AccountState
argument_list|>
name|index
init|=
name|indexes
operator|!=
literal|null
condition|?
name|indexes
operator|.
name|getSearchIndex
argument_list|()
else|:
literal|null
decl_stmt|;
return|return
name|index
operator|!=
literal|null
condition|?
name|index
operator|.
name|getSchema
argument_list|()
else|:
literal|null
return|;
block|}
block|}
DECL|field|args
specifier|private
specifier|final
name|Arguments
name|args
decl_stmt|;
annotation|@
name|Inject
DECL|method|AccountQueryBuilder (Arguments args)
name|AccountQueryBuilder
parameter_list|(
name|Arguments
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|mydef
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
block|}
annotation|@
name|Operator
DECL|method|cansee (String change)
specifier|public
name|Predicate
argument_list|<
name|AccountState
argument_list|>
name|cansee
parameter_list|(
name|String
name|change
parameter_list|)
throws|throws
name|QueryParseException
throws|,
name|StorageException
throws|,
name|PermissionBackendException
block|{
name|ChangeNotes
name|changeNotes
init|=
name|args
operator|.
name|changeFinder
operator|.
name|findOne
argument_list|(
name|change
argument_list|)
decl_stmt|;
if|if
condition|(
name|changeNotes
operator|==
literal|null
condition|)
block|{
throw|throw
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"change %s not found"
argument_list|,
name|change
argument_list|)
argument_list|)
throw|;
block|}
try|try
block|{
name|args
operator|.
name|permissionBackend
operator|.
name|user
argument_list|(
name|args
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|change
argument_list|(
name|changeNotes
argument_list|)
operator|.
name|check
argument_list|(
name|ChangePermission
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|e
parameter_list|)
block|{
throw|throw
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"change %s not found"
argument_list|,
name|change
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|AccountPredicates
operator|.
name|cansee
argument_list|(
name|args
argument_list|,
name|changeNotes
argument_list|)
return|;
block|}
annotation|@
name|Operator
DECL|method|email (String email)
specifier|public
name|Predicate
argument_list|<
name|AccountState
argument_list|>
name|email
parameter_list|(
name|String
name|email
parameter_list|)
throws|throws
name|PermissionBackendException
throws|,
name|QueryParseException
block|{
if|if
condition|(
name|canSeeSecondaryEmails
argument_list|()
condition|)
block|{
return|return
name|AccountPredicates
operator|.
name|emailIncludingSecondaryEmails
argument_list|(
name|email
argument_list|)
return|;
block|}
if|if
condition|(
name|args
operator|.
name|schema
argument_list|()
operator|.
name|hasField
argument_list|(
name|AccountField
operator|.
name|PREFERRED_EMAIL
argument_list|)
condition|)
block|{
return|return
name|AccountPredicates
operator|.
name|preferredEmail
argument_list|(
name|email
argument_list|)
return|;
block|}
throw|throw
operator|new
name|QueryParseException
argument_list|(
literal|"'email' operator is not supported by account index version"
argument_list|)
throw|;
block|}
annotation|@
name|Operator
DECL|method|is (String value)
specifier|public
name|Predicate
argument_list|<
name|AccountState
argument_list|>
name|is
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|QueryParseException
block|{
if|if
condition|(
literal|"active"
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|AccountPredicates
operator|.
name|isActive
argument_list|()
return|;
block|}
if|if
condition|(
literal|"inactive"
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|AccountPredicates
operator|.
name|isNotActive
argument_list|()
return|;
block|}
throw|throw
name|error
argument_list|(
literal|"Invalid query"
argument_list|)
throw|;
block|}
annotation|@
name|Operator
DECL|method|limit (String query)
specifier|public
name|Predicate
argument_list|<
name|AccountState
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
annotation|@
name|Operator
DECL|method|name (String name)
specifier|public
name|Predicate
argument_list|<
name|AccountState
argument_list|>
name|name
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionBackendException
throws|,
name|QueryParseException
block|{
if|if
condition|(
name|canSeeSecondaryEmails
argument_list|()
condition|)
block|{
return|return
name|AccountPredicates
operator|.
name|equalsNameIncludingSecondaryEmails
argument_list|(
name|name
argument_list|)
return|;
block|}
if|if
condition|(
name|args
operator|.
name|schema
argument_list|()
operator|.
name|hasField
argument_list|(
name|AccountField
operator|.
name|NAME_PART_NO_SECONDARY_EMAIL
argument_list|)
condition|)
block|{
return|return
name|AccountPredicates
operator|.
name|equalsName
argument_list|(
name|name
argument_list|)
return|;
block|}
return|return
name|AccountPredicates
operator|.
name|fullName
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Operator
DECL|method|username (String username)
specifier|public
name|Predicate
argument_list|<
name|AccountState
argument_list|>
name|username
parameter_list|(
name|String
name|username
parameter_list|)
block|{
return|return
name|AccountPredicates
operator|.
name|username
argument_list|(
name|username
argument_list|)
return|;
block|}
DECL|method|defaultQuery (String query)
specifier|public
name|Predicate
argument_list|<
name|AccountState
argument_list|>
name|defaultQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
return|return
name|Predicate
operator|.
name|and
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|Splitter
operator|.
name|on
argument_list|(
literal|' '
argument_list|)
operator|.
name|omitEmptyStrings
argument_list|()
operator|.
name|splitToList
argument_list|(
name|query
argument_list|)
argument_list|,
name|this
operator|::
name|defaultField
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|defaultField (String query)
specifier|protected
name|Predicate
argument_list|<
name|AccountState
argument_list|>
name|defaultField
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|Predicate
argument_list|<
name|AccountState
argument_list|>
name|defaultPredicate
init|=
name|AccountPredicates
operator|.
name|defaultPredicate
argument_list|(
name|args
operator|.
name|schema
argument_list|()
argument_list|,
name|checkedCanSeeSecondaryEmails
argument_list|()
argument_list|,
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|.
name|startsWith
argument_list|(
literal|"cansee:"
argument_list|)
condition|)
block|{
try|try
block|{
return|return
name|cansee
argument_list|(
name|query
operator|.
name|substring
argument_list|(
literal|7
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|StorageException
decl||
name|QueryParseException
decl||
name|PermissionBackendException
name|e
parameter_list|)
block|{
comment|// Ignore, fall back to default query
block|}
block|}
if|if
condition|(
literal|"self"
operator|.
name|equalsIgnoreCase
argument_list|(
name|query
argument_list|)
operator|||
literal|"me"
operator|.
name|equalsIgnoreCase
argument_list|(
name|query
argument_list|)
condition|)
block|{
try|try
block|{
return|return
name|Predicate
operator|.
name|or
argument_list|(
name|defaultPredicate
argument_list|,
name|AccountPredicates
operator|.
name|id
argument_list|(
name|self
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|QueryParseException
name|e
parameter_list|)
block|{
comment|// Skip.
block|}
block|}
return|return
name|defaultPredicate
return|;
block|}
DECL|method|self ()
specifier|private
name|Account
operator|.
name|Id
name|self
parameter_list|()
throws|throws
name|QueryParseException
block|{
return|return
name|args
operator|.
name|getIdentifiedUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
return|;
block|}
DECL|method|canSeeSecondaryEmails ()
specifier|private
name|boolean
name|canSeeSecondaryEmails
parameter_list|()
throws|throws
name|PermissionBackendException
throws|,
name|QueryParseException
block|{
try|try
block|{
name|args
operator|.
name|permissionBackend
operator|.
name|user
argument_list|(
name|args
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|check
argument_list|(
name|GlobalPermission
operator|.
name|MODIFY_ACCOUNT
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|checkedCanSeeSecondaryEmails ()
specifier|private
name|boolean
name|checkedCanSeeSecondaryEmails
parameter_list|()
block|{
try|try
block|{
return|return
name|canSeeSecondaryEmails
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|PermissionBackendException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Permission check failed"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|QueryParseException
name|e
parameter_list|)
block|{
comment|// User is not signed in.
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

