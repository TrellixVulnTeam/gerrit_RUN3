begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.git.receive
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
name|receive
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
name|quota
operator|.
name|QuotaGroupDefinitions
operator|.
name|REPOSITORY_SIZE_GROUP
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
name|plugincontext
operator|.
name|PluginSetContext
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
name|quota
operator|.
name|QuotaBackend
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
name|quota
operator|.
name|QuotaResponse
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
name|Collection
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
name|PostReceiveHook
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
name|eclipse
operator|.
name|jgit
operator|.
name|transport
operator|.
name|ReceivePack
import|;
end_import

begin_comment
comment|/**  * Class is responsible for calling all registered post-receive hooks. In addition, in case when  * repository size quota is defined, it requests tokens (pack size) that were received. This is the  * final step of enforcing repository size quota that deducts token from available tokens.  */
end_comment

begin_class
DECL|class|LazyPostReceiveHookChain
specifier|public
class|class
name|LazyPostReceiveHookChain
implements|implements
name|PostReceiveHook
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (CurrentUser user, Project.NameKey project)
name|LazyPostReceiveHookChain
name|create
parameter_list|(
name|CurrentUser
name|user
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
function_decl|;
block|}
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
DECL|field|hooks
specifier|private
specifier|final
name|PluginSetContext
argument_list|<
name|PostReceiveHook
argument_list|>
name|hooks
decl_stmt|;
DECL|field|quotaBackend
specifier|private
specifier|final
name|QuotaBackend
name|quotaBackend
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|CurrentUser
name|user
decl_stmt|;
DECL|field|project
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|project
decl_stmt|;
annotation|@
name|Inject
DECL|method|LazyPostReceiveHookChain ( PluginSetContext<PostReceiveHook> hooks, QuotaBackend quotaBackend, @Assisted CurrentUser user, @Assisted Project.NameKey project)
name|LazyPostReceiveHookChain
parameter_list|(
name|PluginSetContext
argument_list|<
name|PostReceiveHook
argument_list|>
name|hooks
parameter_list|,
name|QuotaBackend
name|quotaBackend
parameter_list|,
annotation|@
name|Assisted
name|CurrentUser
name|user
parameter_list|,
annotation|@
name|Assisted
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
block|{
name|this
operator|.
name|hooks
operator|=
name|hooks
expr_stmt|;
name|this
operator|.
name|quotaBackend
operator|=
name|quotaBackend
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|project
operator|=
name|project
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onPostReceive (ReceivePack rp, Collection<ReceiveCommand> commands)
specifier|public
name|void
name|onPostReceive
parameter_list|(
name|ReceivePack
name|rp
parameter_list|,
name|Collection
argument_list|<
name|ReceiveCommand
argument_list|>
name|commands
parameter_list|)
block|{
name|hooks
operator|.
name|runEach
argument_list|(
name|h
lambda|->
name|h
operator|.
name|onPostReceive
argument_list|(
name|rp
argument_list|,
name|commands
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|affectsSize
argument_list|(
name|rp
argument_list|,
name|commands
argument_list|)
condition|)
block|{
name|QuotaResponse
operator|.
name|Aggregated
name|a
init|=
name|quotaBackend
operator|.
name|user
argument_list|(
name|user
argument_list|)
operator|.
name|project
argument_list|(
name|project
argument_list|)
operator|.
name|requestTokens
argument_list|(
name|REPOSITORY_SIZE_GROUP
argument_list|,
name|rp
operator|.
name|getPackSize
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|hasError
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s request failed for project %s with [%s]"
argument_list|,
name|REPOSITORY_SIZE_GROUP
argument_list|,
name|project
argument_list|,
name|a
operator|.
name|errorMessage
argument_list|()
argument_list|)
decl_stmt|;
name|logger
operator|.
name|atWarning
argument_list|()
operator|.
name|log
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|affectsSize (ReceivePack rp, Collection<ReceiveCommand> commands)
specifier|public
specifier|static
name|boolean
name|affectsSize
parameter_list|(
name|ReceivePack
name|rp
parameter_list|,
name|Collection
argument_list|<
name|ReceiveCommand
argument_list|>
name|commands
parameter_list|)
block|{
if|if
condition|(
name|rp
operator|.
name|getPackSize
argument_list|()
operator|>
literal|0L
condition|)
block|{
for|for
control|(
name|ReceiveCommand
name|cmd
range|:
name|commands
control|)
block|{
if|if
condition|(
name|cmd
operator|.
name|getType
argument_list|()
operator|!=
name|ReceiveCommand
operator|.
name|Type
operator|.
name|DELETE
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

