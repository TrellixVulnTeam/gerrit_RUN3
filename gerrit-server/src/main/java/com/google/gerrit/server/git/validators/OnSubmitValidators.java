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
name|git
operator|.
name|IntegrationException
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
name|validators
operator|.
name|OnSubmitValidationListener
operator|.
name|Arguments
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ObjectReader
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|OnSubmitValidators
specifier|public
class|class
name|OnSubmitValidators
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create ()
name|OnSubmitValidators
name|create
parameter_list|()
function_decl|;
block|}
DECL|field|listeners
specifier|private
specifier|final
name|DynamicSet
argument_list|<
name|OnSubmitValidationListener
argument_list|>
name|listeners
decl_stmt|;
annotation|@
name|Inject
DECL|method|OnSubmitValidators (DynamicSet<OnSubmitValidationListener> listeners)
name|OnSubmitValidators
parameter_list|(
name|DynamicSet
argument_list|<
name|OnSubmitValidationListener
argument_list|>
name|listeners
parameter_list|)
block|{
name|this
operator|.
name|listeners
operator|=
name|listeners
expr_stmt|;
block|}
DECL|method|validate (Project.NameKey project, Repository repo, ObjectReader objectReader, Map<String, ReceiveCommand> commands)
specifier|public
name|void
name|validate
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Repository
name|repo
parameter_list|,
name|ObjectReader
name|objectReader
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ReceiveCommand
argument_list|>
name|commands
parameter_list|)
throws|throws
name|IntegrationException
block|{
try|try
block|{
for|for
control|(
name|OnSubmitValidationListener
name|listener
range|:
name|this
operator|.
name|listeners
control|)
block|{
name|listener
operator|.
name|preBranchUpdate
argument_list|(
operator|new
name|Arguments
argument_list|(
name|project
argument_list|,
name|repo
argument_list|,
name|objectReader
argument_list|,
name|commands
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
throw|throw
operator|new
name|IntegrationException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

