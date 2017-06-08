begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.git.validators
package|package
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
name|validators
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
name|Predicate
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
name|Iterables
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
name|registration
operator|.
name|DynamicSet
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
name|reviewdb
operator|.
name|client
operator|.
name|RefNames
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
name|config
operator|.
name|AllUsersName
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
name|events
operator|.
name|RefReceivedEvent
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
name|validators
operator|.
name|ValidationException
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
name|assistedinject
operator|.
name|Assisted
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|RefUpdate
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
name|transport
operator|.
name|ReceiveCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|RefOperationValidators
specifier|public
class|class
name|RefOperationValidators
block|{
DECL|field|GET_ERRORS
specifier|private
specifier|static
specifier|final
name|GetErrorMessages
name|GET_ERRORS
init|=
operator|new
name|GetErrorMessages
argument_list|()
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RefOperationValidators
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (Project project, IdentifiedUser user, ReceiveCommand cmd)
name|RefOperationValidators
name|create
parameter_list|(
name|Project
name|project
parameter_list|,
name|IdentifiedUser
name|user
parameter_list|,
name|ReceiveCommand
name|cmd
parameter_list|)
function_decl|;
block|}
DECL|method|getCommand (RefUpdate update, ReceiveCommand.Type type)
specifier|public
specifier|static
name|ReceiveCommand
name|getCommand
parameter_list|(
name|RefUpdate
name|update
parameter_list|,
name|ReceiveCommand
operator|.
name|Type
name|type
parameter_list|)
block|{
return|return
operator|new
name|ReceiveCommand
argument_list|(
name|update
operator|.
name|getExpectedOldObjectId
argument_list|()
argument_list|,
name|update
operator|.
name|getNewObjectId
argument_list|()
argument_list|,
name|update
operator|.
name|getName
argument_list|()
argument_list|,
name|type
argument_list|)
return|;
block|}
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|field|refOperationValidationListeners
specifier|private
specifier|final
name|DynamicSet
argument_list|<
name|RefOperationValidationListener
argument_list|>
name|refOperationValidationListeners
decl_stmt|;
DECL|field|event
specifier|private
specifier|final
name|RefReceivedEvent
name|event
decl_stmt|;
annotation|@
name|Inject
DECL|method|RefOperationValidators ( AllUsersName allUsersName, DynamicSet<RefOperationValidationListener> refOperationValidationListeners, @Assisted Project project, @Assisted IdentifiedUser user, @Assisted ReceiveCommand cmd)
name|RefOperationValidators
parameter_list|(
name|AllUsersName
name|allUsersName
parameter_list|,
name|DynamicSet
argument_list|<
name|RefOperationValidationListener
argument_list|>
name|refOperationValidationListeners
parameter_list|,
annotation|@
name|Assisted
name|Project
name|project
parameter_list|,
annotation|@
name|Assisted
name|IdentifiedUser
name|user
parameter_list|,
annotation|@
name|Assisted
name|ReceiveCommand
name|cmd
parameter_list|)
block|{
name|this
operator|.
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
name|this
operator|.
name|refOperationValidationListeners
operator|=
name|refOperationValidationListeners
expr_stmt|;
name|event
operator|=
operator|new
name|RefReceivedEvent
argument_list|()
expr_stmt|;
name|event
operator|.
name|command
operator|=
name|cmd
expr_stmt|;
name|event
operator|.
name|project
operator|=
name|project
expr_stmt|;
name|event
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
DECL|method|validateForRefOperation ()
specifier|public
name|List
argument_list|<
name|ValidationMessage
argument_list|>
name|validateForRefOperation
parameter_list|()
throws|throws
name|RefOperationValidationException
block|{
name|List
argument_list|<
name|ValidationMessage
argument_list|>
name|messages
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|boolean
name|withException
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|RefOperationValidationListener
argument_list|>
name|listeners
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|listeners
operator|.
name|add
argument_list|(
operator|new
name|DisallowCreationAndDeletionOfUserBranches
argument_list|(
name|allUsersName
argument_list|)
argument_list|)
expr_stmt|;
name|refOperationValidationListeners
operator|.
name|forEach
argument_list|(
name|l
lambda|->
name|listeners
operator|.
name|add
argument_list|(
name|l
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|RefOperationValidationListener
name|listener
range|:
name|listeners
control|)
block|{
name|messages
operator|.
name|addAll
argument_list|(
name|listener
operator|.
name|onRefOperation
argument_list|(
name|event
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ValidationException
name|e
parameter_list|)
block|{
name|messages
operator|.
name|add
argument_list|(
operator|new
name|ValidationMessage
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|withException
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|withException
condition|)
block|{
name|throwException
argument_list|(
name|messages
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
return|return
name|messages
return|;
block|}
DECL|method|throwException (Iterable<ValidationMessage> messages, RefReceivedEvent event)
specifier|private
name|void
name|throwException
parameter_list|(
name|Iterable
argument_list|<
name|ValidationMessage
argument_list|>
name|messages
parameter_list|,
name|RefReceivedEvent
name|event
parameter_list|)
throws|throws
name|RefOperationValidationException
block|{
name|Iterable
argument_list|<
name|ValidationMessage
argument_list|>
name|errors
init|=
name|Iterables
operator|.
name|filter
argument_list|(
name|messages
argument_list|,
name|GET_ERRORS
argument_list|)
decl_stmt|;
name|String
name|header
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Ref \"%s\" %S in project %s validation failed"
argument_list|,
name|event
operator|.
name|command
operator|.
name|getRefName
argument_list|()
argument_list|,
name|event
operator|.
name|command
operator|.
name|getType
argument_list|()
argument_list|,
name|event
operator|.
name|project
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|header
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RefOperationValidationException
argument_list|(
name|header
argument_list|,
name|errors
argument_list|)
throw|;
block|}
DECL|class|GetErrorMessages
specifier|private
specifier|static
class|class
name|GetErrorMessages
implements|implements
name|Predicate
argument_list|<
name|ValidationMessage
argument_list|>
block|{
annotation|@
name|Override
DECL|method|apply (ValidationMessage input)
specifier|public
name|boolean
name|apply
parameter_list|(
name|ValidationMessage
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|isError
argument_list|()
return|;
block|}
block|}
DECL|class|DisallowCreationAndDeletionOfUserBranches
specifier|private
specifier|static
class|class
name|DisallowCreationAndDeletionOfUserBranches
implements|implements
name|RefOperationValidationListener
block|{
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|method|DisallowCreationAndDeletionOfUserBranches (AllUsersName allUsersName)
name|DisallowCreationAndDeletionOfUserBranches
parameter_list|(
name|AllUsersName
name|allUsersName
parameter_list|)
block|{
name|this
operator|.
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onRefOperation (RefReceivedEvent refEvent)
specifier|public
name|List
argument_list|<
name|ValidationMessage
argument_list|>
name|onRefOperation
parameter_list|(
name|RefReceivedEvent
name|refEvent
parameter_list|)
throws|throws
name|ValidationException
block|{
if|if
condition|(
name|refEvent
operator|.
name|project
operator|.
name|getNameKey
argument_list|()
operator|.
name|equals
argument_list|(
name|allUsersName
argument_list|)
operator|&&
operator|(
name|refEvent
operator|.
name|command
operator|.
name|getRefName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|RefNames
operator|.
name|REFS_USERS
argument_list|)
operator|&&
operator|!
name|refEvent
operator|.
name|command
operator|.
name|getRefName
argument_list|()
operator|.
name|equals
argument_list|(
name|RefNames
operator|.
name|REFS_USERS_DEFAULT
argument_list|)
operator|)
condition|)
block|{
if|if
condition|(
name|refEvent
operator|.
name|command
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|ReceiveCommand
operator|.
name|Type
operator|.
name|CREATE
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|refEvent
operator|.
name|user
operator|.
name|getCapabilities
argument_list|()
operator|.
name|canAccessDatabase
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ValidationException
argument_list|(
literal|"Not allowed to create user branch."
argument_list|)
throw|;
block|}
if|if
condition|(
name|Account
operator|.
name|Id
operator|.
name|fromRef
argument_list|(
name|refEvent
operator|.
name|command
operator|.
name|getRefName
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ValidationException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Not allowed to create non-user branch under %s."
argument_list|,
name|RefNames
operator|.
name|REFS_USERS
argument_list|)
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|refEvent
operator|.
name|command
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|ReceiveCommand
operator|.
name|Type
operator|.
name|DELETE
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|refEvent
operator|.
name|user
operator|.
name|getCapabilities
argument_list|()
operator|.
name|canAccessDatabase
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ValidationException
argument_list|(
literal|"Not allowed to delete user branch."
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

