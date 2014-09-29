begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
DECL|package|com.google.gerrit.sshd.commands
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
operator|.
name|commands
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
name|extensions
operator|.
name|api
operator|.
name|changes
operator|.
name|AddReviewerInput
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
name|ResourceNotFoundException
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
name|Change
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
name|PatchSet
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
name|RevId
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
name|server
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
name|server
operator|.
name|change
operator|.
name|ChangeResource
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
name|ChangesCollection
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
name|DeleteReviewer
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
name|PostReviewers
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
name|ReviewerResource
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
name|ChangeControl
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
name|NoSuchChangeException
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
name|sshd
operator|.
name|CommandMetaData
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
name|sshd
operator|.
name|SshCommand
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
name|gwtorm
operator|.
name|server
operator|.
name|ResultSet
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
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Argument
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_class
annotation|@
name|CommandMetaData
argument_list|(
name|name
operator|=
literal|"set-reviewers"
argument_list|,
name|description
operator|=
literal|"Add or remove reviewers on a change"
argument_list|)
DECL|class|SetReviewersCommand
specifier|public
class|class
name|SetReviewersCommand
extends|extends
name|SshCommand
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SetReviewersCommand
operator|.
name|class
argument_list|)
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
literal|"-p"
argument_list|,
name|usage
operator|=
literal|"project containing the change"
argument_list|)
DECL|field|projectControl
specifier|private
name|ProjectControl
name|projectControl
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--add"
argument_list|,
name|aliases
operator|=
block|{
literal|"-a"
block|}
argument_list|,
name|metaVar
operator|=
literal|"REVIEWER"
argument_list|,
name|usage
operator|=
literal|"user or group that should be added as reviewer"
argument_list|)
DECL|field|toAdd
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|toAdd
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--remove"
argument_list|,
name|aliases
operator|=
block|{
literal|"-r"
block|}
argument_list|,
name|metaVar
operator|=
literal|"REVIEWER"
argument_list|,
name|usage
operator|=
literal|"user that should be removed from the reviewer list"
argument_list|)
DECL|method|optionRemove (Account.Id who)
name|void
name|optionRemove
parameter_list|(
name|Account
operator|.
name|Id
name|who
parameter_list|)
block|{
name|toRemove
operator|.
name|add
argument_list|(
name|who
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Argument
argument_list|(
name|index
operator|=
literal|0
argument_list|,
name|required
operator|=
literal|true
argument_list|,
name|multiValued
operator|=
literal|true
argument_list|,
name|metaVar
operator|=
literal|"COMMIT"
argument_list|,
name|usage
operator|=
literal|"changes to modify"
argument_list|)
DECL|method|addChange (String token)
name|void
name|addChange
parameter_list|(
name|String
name|token
parameter_list|)
block|{
try|try
block|{
name|changes
operator|.
name|addAll
argument_list|(
name|parseChangeId
argument_list|(
name|token
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnloggedFailure
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"database is down"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Inject
DECL|field|db
specifier|private
name|ReviewDb
name|db
decl_stmt|;
annotation|@
name|Inject
DECL|field|reviewerFactory
specifier|private
name|ReviewerResource
operator|.
name|Factory
name|reviewerFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|postReviewersProvider
specifier|private
name|Provider
argument_list|<
name|PostReviewers
argument_list|>
name|postReviewersProvider
decl_stmt|;
annotation|@
name|Inject
DECL|field|deleteReviewerProvider
specifier|private
name|Provider
argument_list|<
name|DeleteReviewer
argument_list|>
name|deleteReviewerProvider
decl_stmt|;
annotation|@
name|Inject
DECL|field|changeControlFactory
specifier|private
name|ChangeControl
operator|.
name|Factory
name|changeControlFactory
decl_stmt|;
annotation|@
name|Inject
DECL|field|changesCollection
specifier|private
name|ChangesCollection
name|changesCollection
decl_stmt|;
DECL|field|toRemove
specifier|private
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|toRemove
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|changes
specifier|private
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|changes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|protected
name|void
name|run
parameter_list|()
throws|throws
name|UnloggedFailure
block|{
name|boolean
name|ok
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Change
operator|.
name|Id
name|changeId
range|:
name|changes
control|)
block|{
try|try
block|{
name|ok
operator|&=
name|modifyOne
argument_list|(
name|changeId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|err
parameter_list|)
block|{
name|ok
operator|=
literal|false
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"Error updating reviewers on change "
operator|+
name|changeId
argument_list|,
name|err
argument_list|)
expr_stmt|;
name|writeError
argument_list|(
literal|"fatal"
argument_list|,
literal|"internal error while updating "
operator|+
name|changeId
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|ok
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"fatal: one or more updates failed; review output above"
argument_list|)
throw|;
block|}
block|}
DECL|method|modifyOne (Change.Id changeId)
specifier|private
name|boolean
name|modifyOne
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|)
throws|throws
name|Exception
block|{
name|ChangeResource
name|changeRsrc
init|=
name|changesCollection
operator|.
name|parse
argument_list|(
name|changeId
argument_list|)
decl_stmt|;
name|boolean
name|ok
init|=
literal|true
decl_stmt|;
comment|// Remove reviewers
comment|//
name|DeleteReviewer
name|delete
init|=
name|deleteReviewerProvider
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|reviewer
range|:
name|toRemove
control|)
block|{
name|ReviewerResource
name|rsrc
init|=
name|reviewerFactory
operator|.
name|create
argument_list|(
name|changeRsrc
argument_list|,
name|reviewer
argument_list|)
decl_stmt|;
name|String
name|error
init|=
literal|null
decl_stmt|;
try|try
block|{
name|delete
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|,
operator|new
name|DeleteReviewer
operator|.
name|Input
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceNotFoundException
name|e
parameter_list|)
block|{
name|error
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"could not remove %s: not found"
argument_list|,
name|reviewer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|error
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"could not remove %s: %s"
argument_list|,
name|reviewer
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|error
operator|!=
literal|null
condition|)
block|{
name|ok
operator|=
literal|false
expr_stmt|;
name|writeError
argument_list|(
literal|"error"
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Add reviewers
comment|//
name|PostReviewers
name|post
init|=
name|postReviewersProvider
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|reviewer
range|:
name|toAdd
control|)
block|{
name|AddReviewerInput
name|input
init|=
operator|new
name|AddReviewerInput
argument_list|()
decl_stmt|;
name|input
operator|.
name|reviewer
operator|=
name|reviewer
expr_stmt|;
name|input
operator|.
name|confirmed
operator|=
literal|true
expr_stmt|;
name|String
name|error
decl_stmt|;
try|try
block|{
name|error
operator|=
name|post
operator|.
name|apply
argument_list|(
name|changeRsrc
argument_list|,
name|input
argument_list|)
operator|.
name|error
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|error
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"could not add %s: %s"
argument_list|,
name|reviewer
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|error
operator|!=
literal|null
condition|)
block|{
name|ok
operator|=
literal|false
expr_stmt|;
name|writeError
argument_list|(
literal|"error"
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ok
return|;
block|}
DECL|method|parseChangeId (String idstr)
specifier|private
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|parseChangeId
parameter_list|(
name|String
name|idstr
parameter_list|)
throws|throws
name|UnloggedFailure
throws|,
name|OrmException
block|{
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|matched
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|boolean
name|isCommit
init|=
name|idstr
operator|.
name|matches
argument_list|(
literal|"^([0-9a-fA-F]{4,"
operator|+
name|RevId
operator|.
name|LEN
operator|+
literal|"})$"
argument_list|)
decl_stmt|;
comment|// By newer style changeKey?
comment|//
name|boolean
name|changeKeyParses
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|idstr
operator|.
name|matches
argument_list|(
literal|"^I[0-9a-fA-F]*$"
argument_list|)
condition|)
block|{
name|Change
operator|.
name|Key
name|key
decl_stmt|;
try|try
block|{
name|key
operator|=
name|Change
operator|.
name|Key
operator|.
name|parse
argument_list|(
name|idstr
argument_list|)
expr_stmt|;
name|changeKeyParses
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|key
operator|=
literal|null
expr_stmt|;
name|changeKeyParses
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|changeKeyParses
condition|)
block|{
for|for
control|(
name|Change
name|change
range|:
name|db
operator|.
name|changes
argument_list|()
operator|.
name|byKeyRange
argument_list|(
name|key
argument_list|,
name|key
operator|.
name|max
argument_list|()
argument_list|)
control|)
block|{
name|matchChange
argument_list|(
name|matched
argument_list|,
name|change
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// By commit?
comment|//
if|if
condition|(
name|isCommit
condition|)
block|{
name|RevId
name|id
init|=
operator|new
name|RevId
argument_list|(
name|idstr
argument_list|)
decl_stmt|;
name|ResultSet
argument_list|<
name|PatchSet
argument_list|>
name|patches
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|isComplete
argument_list|()
condition|)
block|{
name|patches
operator|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|byRevision
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|patches
operator|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|byRevisionRange
argument_list|(
name|id
argument_list|,
name|id
operator|.
name|max
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|PatchSet
name|ps
range|:
name|patches
control|)
block|{
name|matchChange
argument_list|(
name|matched
argument_list|,
name|ps
operator|.
name|getId
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// By older style changeId?
comment|//
name|boolean
name|changeIdParses
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|idstr
operator|.
name|matches
argument_list|(
literal|"^[1-9][0-9]*$"
argument_list|)
condition|)
block|{
name|Change
operator|.
name|Id
name|id
decl_stmt|;
try|try
block|{
name|id
operator|=
name|Change
operator|.
name|Id
operator|.
name|parse
argument_list|(
name|idstr
argument_list|)
expr_stmt|;
name|changeIdParses
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|id
operator|=
literal|null
expr_stmt|;
name|changeIdParses
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|changeIdParses
condition|)
block|{
name|matchChange
argument_list|(
name|matched
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|changeKeyParses
operator|&&
operator|!
name|isCommit
operator|&&
operator|!
name|changeIdParses
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"\""
operator|+
name|idstr
operator|+
literal|"\" is not a valid change"
argument_list|)
throw|;
block|}
switch|switch
condition|(
name|matched
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
throw|throw
name|error
argument_list|(
literal|"\""
operator|+
name|idstr
operator|+
literal|"\" no such change"
argument_list|)
throw|;
case|case
literal|1
case|:
return|return
name|matched
return|;
default|default:
throw|throw
name|error
argument_list|(
literal|"\""
operator|+
name|idstr
operator|+
literal|"\" matches multiple changes"
argument_list|)
throw|;
block|}
block|}
DECL|method|matchChange (Set<Change.Id> matched, Change.Id changeId)
specifier|private
name|void
name|matchChange
parameter_list|(
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|matched
parameter_list|,
name|Change
operator|.
name|Id
name|changeId
parameter_list|)
block|{
if|if
condition|(
name|changeId
operator|!=
literal|null
operator|&&
operator|!
name|matched
operator|.
name|contains
argument_list|(
name|changeId
argument_list|)
condition|)
block|{
try|try
block|{
name|matchChange
argument_list|(
name|matched
argument_list|,
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|changeId
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error reading change "
operator|+
name|changeId
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|matchChange (Set<Change.Id> matched, Change change)
specifier|private
name|void
name|matchChange
parameter_list|(
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|matched
parameter_list|,
name|Change
name|change
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|change
operator|!=
literal|null
operator|&&
name|inProject
argument_list|(
name|change
argument_list|)
operator|&&
name|changeControlFactory
operator|.
name|controlFor
argument_list|(
name|change
argument_list|)
operator|.
name|isVisible
argument_list|(
name|db
argument_list|)
condition|)
block|{
name|matched
operator|.
name|add
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchChangeException
name|e
parameter_list|)
block|{
comment|// Ignore this change.
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error reading change "
operator|+
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|inProject (Change change)
specifier|private
name|boolean
name|inProject
parameter_list|(
name|Change
name|change
parameter_list|)
block|{
if|if
condition|(
name|projectControl
operator|!=
literal|null
condition|)
block|{
return|return
name|projectControl
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
operator|.
name|equals
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
comment|// No --project option, so they want every project.
return|return
literal|true
return|;
block|}
block|}
DECL|method|writeError (String type, String msg)
specifier|private
name|void
name|writeError
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
try|try
block|{
name|err
operator|.
name|write
argument_list|(
operator|(
name|type
operator|+
literal|": "
operator|+
name|msg
operator|+
literal|"\n"
operator|)
operator|.
name|getBytes
argument_list|(
name|ENC
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{     }
block|}
DECL|method|error (String msg)
specifier|private
specifier|static
name|UnloggedFailure
name|error
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
return|return
operator|new
name|UnloggedFailure
argument_list|(
literal|1
argument_list|,
name|msg
argument_list|)
return|;
block|}
block|}
end_class

end_unit

