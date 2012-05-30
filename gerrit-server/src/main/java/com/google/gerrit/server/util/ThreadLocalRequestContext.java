begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.util
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|util
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
name|Objects
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
name|inject
operator|.
name|AbstractModule
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
name|Module
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
name|Provides
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|name
operator|.
name|Named
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
name|name
operator|.
name|Names
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * ThreadLocalRequestContext manages the current RequestContext using a  * ThreadLocal. When the context is set, the fields exposed by the context  * are considered in scope. Otherwise, the FallbackRequestContext is used.  */
end_comment

begin_class
DECL|class|ThreadLocalRequestContext
specifier|public
class|class
name|ThreadLocalRequestContext
block|{
DECL|field|FALLBACK
specifier|private
specifier|static
specifier|final
name|String
name|FALLBACK
init|=
literal|"FALLBACK"
decl_stmt|;
DECL|method|module ()
specifier|public
specifier|static
name|Module
name|module
parameter_list|()
block|{
return|return
operator|new
name|AbstractModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|ThreadLocalRequestContext
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|RequestContext
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|Names
operator|.
name|named
argument_list|(
name|FALLBACK
argument_list|)
argument_list|)
operator|.
name|to
argument_list|(
name|FallbackRequestContext
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Provides
name|RequestContext
name|provideRequestContext
parameter_list|(
annotation|@
name|Named
argument_list|(
name|FALLBACK
argument_list|)
name|RequestContext
name|fallback
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|firstNonNull
argument_list|(
name|local
operator|.
name|get
argument_list|()
argument_list|,
name|fallback
argument_list|)
return|;
block|}
annotation|@
name|Provides
name|CurrentUser
name|provideCurrentUser
parameter_list|(
name|RequestContext
name|ctx
parameter_list|)
block|{
return|return
name|ctx
operator|.
name|getCurrentUser
argument_list|()
return|;
block|}
annotation|@
name|Provides
name|IdentifiedUser
name|provideCurrentUser
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
block|{
if|if
condition|(
name|user
operator|instanceof
name|IdentifiedUser
condition|)
block|{
return|return
operator|(
name|IdentifiedUser
operator|)
name|user
return|;
block|}
throw|throw
operator|new
name|ProvisionException
argument_list|(
name|NotSignedInException
operator|.
name|MESSAGE
argument_list|,
operator|new
name|NotSignedInException
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|;
block|}
DECL|field|local
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|RequestContext
argument_list|>
name|local
init|=
operator|new
name|ThreadLocal
argument_list|<
name|RequestContext
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|ThreadLocalRequestContext ()
name|ThreadLocalRequestContext
parameter_list|()
block|{   }
DECL|method|setContext (@ullable RequestContext ctx)
specifier|public
name|RequestContext
name|setContext
parameter_list|(
annotation|@
name|Nullable
name|RequestContext
name|ctx
parameter_list|)
block|{
name|RequestContext
name|old
init|=
name|getContext
argument_list|()
decl_stmt|;
name|local
operator|.
name|set
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
name|old
return|;
block|}
annotation|@
name|Nullable
DECL|method|getContext ()
specifier|public
name|RequestContext
name|getContext
parameter_list|()
block|{
return|return
name|local
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

