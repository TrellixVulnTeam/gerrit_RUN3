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
name|lib
operator|.
name|ObjectId
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
name|PreUploadHook
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
name|ServiceMayNotContinueException
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
name|UploadPack
import|;
end_import

begin_class
DECL|class|UploadValidators
specifier|public
class|class
name|UploadValidators
implements|implements
name|PreUploadHook
block|{
DECL|field|uploadValidationListeners
specifier|private
specifier|final
name|DynamicSet
argument_list|<
name|UploadValidationListener
argument_list|>
name|uploadValidationListeners
decl_stmt|;
DECL|field|project
specifier|private
specifier|final
name|Project
name|project
decl_stmt|;
DECL|field|repository
specifier|private
specifier|final
name|Repository
name|repository
decl_stmt|;
DECL|field|remoteHost
specifier|private
specifier|final
name|String
name|remoteHost
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (Project project, Repository repository, String remoteAddress)
name|UploadValidators
name|create
parameter_list|(
name|Project
name|project
parameter_list|,
name|Repository
name|repository
parameter_list|,
name|String
name|remoteAddress
parameter_list|)
function_decl|;
block|}
annotation|@
name|Inject
DECL|method|UploadValidators ( DynamicSet<UploadValidationListener> uploadValidationListeners, @Assisted Project project, @Assisted Repository repository, @Assisted String remoteHost)
name|UploadValidators
parameter_list|(
name|DynamicSet
argument_list|<
name|UploadValidationListener
argument_list|>
name|uploadValidationListeners
parameter_list|,
annotation|@
name|Assisted
name|Project
name|project
parameter_list|,
annotation|@
name|Assisted
name|Repository
name|repository
parameter_list|,
annotation|@
name|Assisted
name|String
name|remoteHost
parameter_list|)
block|{
name|this
operator|.
name|uploadValidationListeners
operator|=
name|uploadValidationListeners
expr_stmt|;
name|this
operator|.
name|project
operator|=
name|project
expr_stmt|;
name|this
operator|.
name|repository
operator|=
name|repository
expr_stmt|;
name|this
operator|.
name|remoteHost
operator|=
name|remoteHost
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onSendPack ( UploadPack up, Collection<? extends ObjectId> wants, Collection<? extends ObjectId> haves)
specifier|public
name|void
name|onSendPack
parameter_list|(
name|UploadPack
name|up
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|ObjectId
argument_list|>
name|wants
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|ObjectId
argument_list|>
name|haves
parameter_list|)
throws|throws
name|ServiceMayNotContinueException
block|{
for|for
control|(
name|UploadValidationListener
name|validator
range|:
name|uploadValidationListeners
control|)
block|{
try|try
block|{
name|validator
operator|.
name|onPreUpload
argument_list|(
name|repository
argument_list|,
name|project
argument_list|,
name|remoteHost
argument_list|,
name|up
argument_list|,
name|wants
argument_list|,
name|haves
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ValidationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UploadValidationException
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
annotation|@
name|Override
DECL|method|onBeginNegotiateRound ( UploadPack up, Collection<? extends ObjectId> wants, int cntOffered)
specifier|public
name|void
name|onBeginNegotiateRound
parameter_list|(
name|UploadPack
name|up
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|ObjectId
argument_list|>
name|wants
parameter_list|,
name|int
name|cntOffered
parameter_list|)
throws|throws
name|ServiceMayNotContinueException
block|{
for|for
control|(
name|UploadValidationListener
name|validator
range|:
name|uploadValidationListeners
control|)
block|{
try|try
block|{
name|validator
operator|.
name|onBeginNegotiate
argument_list|(
name|repository
argument_list|,
name|project
argument_list|,
name|remoteHost
argument_list|,
name|up
argument_list|,
name|wants
argument_list|,
name|cntOffered
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ValidationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UploadValidationException
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
annotation|@
name|Override
DECL|method|onEndNegotiateRound ( UploadPack up, Collection<? extends ObjectId> wants, int cntCommon, int cntNotFound, boolean ready)
specifier|public
name|void
name|onEndNegotiateRound
parameter_list|(
name|UploadPack
name|up
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|ObjectId
argument_list|>
name|wants
parameter_list|,
name|int
name|cntCommon
parameter_list|,
name|int
name|cntNotFound
parameter_list|,
name|boolean
name|ready
parameter_list|)
throws|throws
name|ServiceMayNotContinueException
block|{}
block|}
end_class

end_unit

