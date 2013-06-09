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
DECL|package|com.google.gerrit.server.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|config
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
name|Maps
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
name|ResourceConflictException
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
name|RestReadView
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

begin_comment
comment|/** List capabilities visible to the calling user. */
end_comment

begin_class
DECL|class|ListCapabilities
specifier|public
class|class
name|ListCapabilities
implements|implements
name|RestReadView
argument_list|<
name|ConfigResource
argument_list|>
block|{
annotation|@
name|Override
DECL|method|apply (ConfigResource resource)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|CapabilityInfo
argument_list|>
name|apply
parameter_list|(
name|ConfigResource
name|resource
parameter_list|)
throws|throws
name|AuthException
throws|,
name|BadRequestException
throws|,
name|ResourceConflictException
throws|,
name|IllegalArgumentException
throws|,
name|SecurityException
throws|,
name|IllegalAccessException
throws|,
name|NoSuchFieldException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|CapabilityInfo
argument_list|>
name|output
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|CapabilityConstants
argument_list|>
name|bundleClass
init|=
name|CapabilityConstants
operator|.
name|get
argument_list|()
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|CapabilityConstants
name|c
init|=
name|CapabilityConstants
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|GlobalCapability
operator|.
name|getAllNames
argument_list|()
control|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|bundleClass
operator|.
name|getField
argument_list|(
name|id
argument_list|)
operator|.
name|get
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|output
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|CapabilityInfo
argument_list|(
name|id
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|output
return|;
block|}
DECL|class|CapabilityInfo
specifier|public
specifier|static
class|class
name|CapabilityInfo
block|{
DECL|field|kind
specifier|final
name|String
name|kind
init|=
literal|"gerritcodereview#capability"
decl_stmt|;
DECL|field|id
specifier|public
name|String
name|id
decl_stmt|;
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
DECL|method|CapabilityInfo (String id, String name)
specifier|public
name|CapabilityInfo
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

