begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
name|common
operator|.
name|errors
operator|.
name|NameAlreadyUsedException
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
name|account
operator|.
name|PerformCreateGroup
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
name|PerformCreateGroup
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
name|AdminCommand
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
name|BaseCommand
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
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|Environment
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
name|HashSet
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

begin_comment
comment|/**  * Creates a new group.  *<p>  * Optionally, puts an initial set of user in the newly created group.  */
end_comment

begin_class
annotation|@
name|AdminCommand
DECL|class|AdminCreateGroup
specifier|public
class|class
name|AdminCreateGroup
extends|extends
name|BaseCommand
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--owner"
argument_list|,
name|aliases
operator|=
block|{
literal|"-o"
block|}
argument_list|,
name|metaVar
operator|=
literal|"GROUP"
argument_list|,
name|usage
operator|=
literal|"owning group, if not specified the group will be self-owning"
argument_list|)
DECL|field|ownerGroupId
specifier|private
name|AccountGroup
operator|.
name|Id
name|ownerGroupId
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--description"
argument_list|,
name|aliases
operator|=
block|{
literal|"-d"
block|}
argument_list|,
name|metaVar
operator|=
literal|"DESC"
argument_list|,
name|usage
operator|=
literal|"description of group"
argument_list|)
DECL|field|groupDescription
specifier|private
name|String
name|groupDescription
init|=
literal|""
decl_stmt|;
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
name|metaVar
operator|=
literal|"GROUP"
argument_list|,
name|usage
operator|=
literal|"name of group to be created"
argument_list|)
DECL|field|groupName
specifier|private
name|String
name|groupName
decl_stmt|;
DECL|field|initialMembers
specifier|private
specifier|final
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|initialMembers
init|=
operator|new
name|HashSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--member"
argument_list|,
name|aliases
operator|=
block|{
literal|"-m"
block|}
argument_list|,
name|metaVar
operator|=
literal|"USERNAME"
argument_list|,
name|usage
operator|=
literal|"initial set of users to become members of the group"
argument_list|)
DECL|method|addMember (final Account.Id id)
name|void
name|addMember
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
name|initialMembers
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--visible-to-all"
argument_list|,
name|usage
operator|=
literal|"to make the group visible to all registered users"
argument_list|)
DECL|field|visibleToAll
specifier|private
name|boolean
name|visibleToAll
decl_stmt|;
DECL|field|initialGroups
specifier|private
specifier|final
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|initialGroups
init|=
operator|new
name|HashSet
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--group"
argument_list|,
name|aliases
operator|=
literal|"-g"
argument_list|,
name|metaVar
operator|=
literal|"GROUP"
argument_list|,
name|usage
operator|=
literal|"initial set of groups to be included in the group"
argument_list|)
DECL|method|addGroup (final AccountGroup.Id id)
name|void
name|addGroup
parameter_list|(
specifier|final
name|AccountGroup
operator|.
name|Id
name|id
parameter_list|)
block|{
name|initialGroups
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Inject
DECL|field|performCreateGroupFactory
specifier|private
name|PerformCreateGroup
operator|.
name|Factory
name|performCreateGroupFactory
decl_stmt|;
annotation|@
name|Override
DECL|method|start (Environment env)
specifier|public
name|void
name|start
parameter_list|(
name|Environment
name|env
parameter_list|)
throws|throws
name|IOException
block|{
name|startThread
argument_list|(
operator|new
name|CommandRunnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|parseCommandLine
argument_list|()
expr_stmt|;
name|createGroup
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|createGroup ()
specifier|private
name|void
name|createGroup
parameter_list|()
throws|throws
name|OrmException
throws|,
name|UnloggedFailure
block|{
specifier|final
name|PerformCreateGroup
name|performCreateGroup
init|=
name|performCreateGroupFactory
operator|.
name|create
argument_list|()
decl_stmt|;
try|try
block|{
name|performCreateGroup
operator|.
name|createGroup
argument_list|(
name|groupName
argument_list|,
name|groupDescription
argument_list|,
name|visibleToAll
argument_list|,
name|ownerGroupId
argument_list|,
name|initialMembers
argument_list|,
name|initialGroups
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NameAlreadyUsedException
name|e
parameter_list|)
block|{
throw|throw
name|die
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

