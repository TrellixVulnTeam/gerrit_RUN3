begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
import|import static
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
name|PostCaches
operator|.
name|Operation
operator|.
name|FLUSH
import|;
end_import

begin_import
import|import static
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
name|PostCaches
operator|.
name|Operation
operator|.
name|FLUSH_ALL
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
operator|.
name|CommandMetaData
operator|.
name|Mode
operator|.
name|MASTER_OR_SLAVE
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
name|data
operator|.
name|GlobalCapability
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
name|annotations
operator|.
name|RequiresCapability
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
name|RestApiException
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
name|ConfigResource
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
name|ListCaches
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
name|ListCaches
operator|.
name|OutputFormat
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
name|PostCaches
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

begin_comment
comment|/** Causes the caches to purge all entries and reload. */
end_comment

begin_class
annotation|@
name|RequiresCapability
argument_list|(
name|GlobalCapability
operator|.
name|FLUSH_CACHES
argument_list|)
annotation|@
name|CommandMetaData
argument_list|(
name|name
operator|=
literal|"flush-caches"
argument_list|,
name|description
operator|=
literal|"Flush some/all server caches from memory"
argument_list|,
name|runsAt
operator|=
name|MASTER_OR_SLAVE
argument_list|)
DECL|class|FlushCaches
specifier|final
class|class
name|FlushCaches
extends|extends
name|SshCommand
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--cache"
argument_list|,
name|usage
operator|=
literal|"flush named cache"
argument_list|,
name|metaVar
operator|=
literal|"NAME"
argument_list|)
DECL|field|caches
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|caches
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
literal|"--all"
argument_list|,
name|usage
operator|=
literal|"flush all caches"
argument_list|)
DECL|field|all
specifier|private
name|boolean
name|all
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--list"
argument_list|,
name|usage
operator|=
literal|"list available caches"
argument_list|)
DECL|field|list
specifier|private
name|boolean
name|list
decl_stmt|;
annotation|@
name|Inject
DECL|field|listCaches
specifier|private
name|Provider
argument_list|<
name|ListCaches
argument_list|>
name|listCaches
decl_stmt|;
annotation|@
name|Inject
DECL|field|postCaches
specifier|private
name|PostCaches
name|postCaches
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|protected
name|void
name|run
parameter_list|()
throws|throws
name|Failure
block|{
try|try
block|{
if|if
condition|(
name|list
condition|)
block|{
if|if
condition|(
name|all
operator|||
name|caches
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"error: cannot use --list with --all or --cache"
argument_list|)
throw|;
block|}
name|doList
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|all
operator|&&
name|caches
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
name|error
argument_list|(
literal|"error: cannot combine --all and --cache"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|all
operator|&&
name|caches
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|caches
operator|.
name|contains
argument_list|(
literal|"all"
argument_list|)
condition|)
block|{
name|caches
operator|.
name|clear
argument_list|()
expr_stmt|;
name|all
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|all
operator|&&
name|caches
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|all
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|all
condition|)
block|{
name|postCaches
operator|.
name|apply
argument_list|(
operator|new
name|ConfigResource
argument_list|()
argument_list|,
operator|new
name|PostCaches
operator|.
name|Input
argument_list|(
name|FLUSH_ALL
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|postCaches
operator|.
name|apply
argument_list|(
operator|new
name|ConfigResource
argument_list|()
argument_list|,
operator|new
name|PostCaches
operator|.
name|Input
argument_list|(
name|FLUSH
argument_list|,
name|caches
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RestApiException
name|e
parameter_list|)
block|{
throw|throw
name|die
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|doList ()
specifier|private
name|void
name|doList
parameter_list|()
block|{
for|for
control|(
name|String
name|name
range|:
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|listCaches
operator|.
name|get
argument_list|()
operator|.
name|setFormat
argument_list|(
name|OutputFormat
operator|.
name|LIST
argument_list|)
operator|.
name|apply
argument_list|(
operator|new
name|ConfigResource
argument_list|()
argument_list|)
control|)
block|{
name|stderr
operator|.
name|print
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|stderr
operator|.
name|print
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|stderr
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

