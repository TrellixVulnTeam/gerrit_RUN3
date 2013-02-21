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
DECL|package|com.google.gerrit.server.mail
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|mail
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
name|common
operator|.
name|errors
operator|.
name|EmailException
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
name|AccountProjectWatch
operator|.
name|NotifyType
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
name|server
operator|.
name|mail
operator|.
name|ProjectWatch
operator|.
name|Watchers
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

begin_comment
comment|/** Notify interested parties of a brand new change. */
end_comment

begin_class
DECL|class|CreateChangeSender
specifier|public
class|class
name|CreateChangeSender
extends|extends
name|NewChangeSender
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
name|CreateChangeSender
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|interface|Factory
specifier|public
specifier|static
interface|interface
name|Factory
block|{
DECL|method|create (Change change)
specifier|public
name|CreateChangeSender
name|create
parameter_list|(
name|Change
name|change
parameter_list|)
function_decl|;
block|}
annotation|@
name|Inject
DECL|method|CreateChangeSender (EmailArguments ea, @Assisted Change c)
specifier|public
name|CreateChangeSender
parameter_list|(
name|EmailArguments
name|ea
parameter_list|,
annotation|@
name|Assisted
name|Change
name|c
parameter_list|)
block|{
name|super
argument_list|(
name|ea
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init ()
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|EmailException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
try|try
block|{
comment|// Try to mark interested owners with TO and CC or BCC line.
name|Watchers
name|matching
init|=
name|getWatchers
argument_list|(
name|NotifyType
operator|.
name|NEW_CHANGES
argument_list|)
decl_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|user
range|:
name|Iterables
operator|.
name|concat
argument_list|(
name|matching
operator|.
name|to
operator|.
name|accounts
argument_list|,
name|matching
operator|.
name|cc
operator|.
name|accounts
argument_list|,
name|matching
operator|.
name|bcc
operator|.
name|accounts
argument_list|)
control|)
block|{
if|if
condition|(
name|isOwnerOfProjectOrBranch
argument_list|(
name|user
argument_list|)
condition|)
block|{
name|add
argument_list|(
name|RecipientType
operator|.
name|TO
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Add everyone else. Owners added above will not be duplicated.
name|add
argument_list|(
name|RecipientType
operator|.
name|TO
argument_list|,
name|matching
operator|.
name|to
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|RecipientType
operator|.
name|CC
argument_list|,
name|matching
operator|.
name|cc
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|RecipientType
operator|.
name|BCC
argument_list|,
name|matching
operator|.
name|bcc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|err
parameter_list|)
block|{
comment|// Just don't CC everyone. Better to send a partial message to those
comment|// we already have queued up then to fail deliver entirely to people
comment|// who have a lower interest in the change.
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot notify watchers for new change"
argument_list|,
name|err
argument_list|)
expr_stmt|;
block|}
name|includeWatchers
argument_list|(
name|NotifyType
operator|.
name|NEW_PATCHSETS
argument_list|)
expr_stmt|;
block|}
DECL|method|isOwnerOfProjectOrBranch (Account.Id user)
specifier|private
name|boolean
name|isOwnerOfProjectOrBranch
parameter_list|(
name|Account
operator|.
name|Id
name|user
parameter_list|)
block|{
return|return
name|projectState
operator|!=
literal|null
operator|&&
name|projectState
operator|.
name|controlFor
argument_list|(
name|args
operator|.
name|identifiedUserFactory
operator|.
name|create
argument_list|(
name|user
argument_list|)
argument_list|)
operator|.
name|controlForRef
argument_list|(
name|change
operator|.
name|getDest
argument_list|()
argument_list|)
operator|.
name|isOwner
argument_list|()
return|;
block|}
block|}
end_class

end_unit

