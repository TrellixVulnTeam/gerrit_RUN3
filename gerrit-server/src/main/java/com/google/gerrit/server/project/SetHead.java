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
DECL|package|com.google.gerrit.server.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|gerrit
operator|.
name|extensions
operator|.
name|events
operator|.
name|HeadUpdatedListener
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
name|DefaultInput
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
name|extensions
operator|.
name|restapi
operator|.
name|RestModifyView
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
name|UnprocessableEntityException
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
name|extensions
operator|.
name|events
operator|.
name|AbstractNoNotifyEvent
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
name|git
operator|.
name|GitRepositoryManager
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
name|SetHead
operator|.
name|Input
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
name|Singleton
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
name|errors
operator|.
name|RepositoryNotFoundException
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
name|Constants
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
name|Ref
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
name|lib
operator|.
name|Repository
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
name|Map
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|SetHead
specifier|public
class|class
name|SetHead
implements|implements
name|RestModifyView
argument_list|<
name|ProjectResource
argument_list|,
name|Input
argument_list|>
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
name|SetHead
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|Input
specifier|public
specifier|static
class|class
name|Input
block|{
annotation|@
name|DefaultInput
DECL|field|ref
specifier|public
name|String
name|ref
decl_stmt|;
block|}
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|identifiedUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|identifiedUser
decl_stmt|;
DECL|field|headUpdatedListeners
specifier|private
specifier|final
name|DynamicSet
argument_list|<
name|HeadUpdatedListener
argument_list|>
name|headUpdatedListeners
decl_stmt|;
annotation|@
name|Inject
DECL|method|SetHead (GitRepositoryManager repoManager, Provider<IdentifiedUser> identifiedUser, DynamicSet<HeadUpdatedListener> headUpdatedListeners)
name|SetHead
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|identifiedUser
parameter_list|,
name|DynamicSet
argument_list|<
name|HeadUpdatedListener
argument_list|>
name|headUpdatedListeners
parameter_list|)
block|{
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|identifiedUser
operator|=
name|identifiedUser
expr_stmt|;
name|this
operator|.
name|headUpdatedListeners
operator|=
name|headUpdatedListeners
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (final ProjectResource rsrc, Input input)
specifier|public
name|String
name|apply
parameter_list|(
specifier|final
name|ProjectResource
name|rsrc
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|ResourceNotFoundException
throws|,
name|BadRequestException
throws|,
name|UnprocessableEntityException
throws|,
name|IOException
block|{
if|if
condition|(
operator|!
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|isOwner
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"restricted to project owner"
argument_list|)
throw|;
block|}
if|if
condition|(
name|input
operator|==
literal|null
operator|||
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|input
operator|.
name|ref
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"ref required"
argument_list|)
throw|;
block|}
name|String
name|ref
init|=
name|RefNames
operator|.
name|fullName
argument_list|(
name|input
operator|.
name|ref
argument_list|)
decl_stmt|;
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|)
init|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|cur
init|=
name|repo
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|exactRef
argument_list|(
name|Constants
operator|.
name|HEAD
argument_list|,
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|cur
operator|.
name|containsKey
argument_list|(
name|ref
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|UnprocessableEntityException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Ref Not Found: %s"
argument_list|,
name|ref
argument_list|)
argument_list|)
throw|;
block|}
specifier|final
name|String
name|oldHead
init|=
name|cur
operator|.
name|get
argument_list|(
name|Constants
operator|.
name|HEAD
argument_list|)
operator|.
name|getTarget
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|newHead
init|=
name|ref
decl_stmt|;
if|if
condition|(
operator|!
name|oldHead
operator|.
name|equals
argument_list|(
name|newHead
argument_list|)
condition|)
block|{
specifier|final
name|RefUpdate
name|u
init|=
name|repo
operator|.
name|updateRef
argument_list|(
name|Constants
operator|.
name|HEAD
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|u
operator|.
name|setRefLogIdent
argument_list|(
name|identifiedUser
operator|.
name|get
argument_list|()
operator|.
name|newRefLogIdent
argument_list|()
argument_list|)
expr_stmt|;
name|RefUpdate
operator|.
name|Result
name|res
init|=
name|u
operator|.
name|link
argument_list|(
name|newHead
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|res
condition|)
block|{
case|case
name|NO_CHANGE
case|:
case|case
name|RENAMED
case|:
case|case
name|FORCED
case|:
case|case
name|NEW
case|:
break|break;
case|case
name|FAST_FORWARD
case|:
case|case
name|IO_FAILURE
case|:
case|case
name|LOCK_FAILURE
case|:
case|case
name|NOT_ATTEMPTED
case|:
case|case
name|REJECTED
case|:
case|case
name|REJECTED_CURRENT_BRANCH
case|:
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Setting HEAD failed with "
operator|+
name|res
argument_list|)
throw|;
block|}
name|fire
argument_list|(
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|oldHead
argument_list|,
name|newHead
argument_list|)
expr_stmt|;
block|}
return|return
name|ref
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|rsrc
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|fire (Project.NameKey nameKey, String oldHead, String newHead)
specifier|private
name|void
name|fire
parameter_list|(
name|Project
operator|.
name|NameKey
name|nameKey
parameter_list|,
name|String
name|oldHead
parameter_list|,
name|String
name|newHead
parameter_list|)
block|{
if|if
condition|(
operator|!
name|headUpdatedListeners
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return;
block|}
name|Event
name|event
init|=
operator|new
name|Event
argument_list|(
name|nameKey
argument_list|,
name|oldHead
argument_list|,
name|newHead
argument_list|)
decl_stmt|;
for|for
control|(
name|HeadUpdatedListener
name|l
range|:
name|headUpdatedListeners
control|)
block|{
try|try
block|{
name|l
operator|.
name|onHeadUpdated
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failure in HeadUpdatedListener"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|Event
specifier|static
class|class
name|Event
extends|extends
name|AbstractNoNotifyEvent
implements|implements
name|HeadUpdatedListener
operator|.
name|Event
block|{
DECL|field|nameKey
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|nameKey
decl_stmt|;
DECL|field|oldHead
specifier|private
specifier|final
name|String
name|oldHead
decl_stmt|;
DECL|field|newHead
specifier|private
specifier|final
name|String
name|newHead
decl_stmt|;
DECL|method|Event (Project.NameKey nameKey, String oldHead, String newHead)
name|Event
parameter_list|(
name|Project
operator|.
name|NameKey
name|nameKey
parameter_list|,
name|String
name|oldHead
parameter_list|,
name|String
name|newHead
parameter_list|)
block|{
name|this
operator|.
name|nameKey
operator|=
name|nameKey
expr_stmt|;
name|this
operator|.
name|oldHead
operator|=
name|oldHead
expr_stmt|;
name|this
operator|.
name|newHead
operator|=
name|newHead
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProjectName ()
specifier|public
name|String
name|getProjectName
parameter_list|()
block|{
return|return
name|nameKey
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getOldHeadName ()
specifier|public
name|String
name|getOldHeadName
parameter_list|()
block|{
return|return
name|oldHead
return|;
block|}
annotation|@
name|Override
DECL|method|getNewHeadName ()
specifier|public
name|String
name|getNewHeadName
parameter_list|()
block|{
return|return
name|newHead
return|;
block|}
block|}
block|}
end_class

end_unit

