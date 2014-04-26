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
DECL|package|com.google.gerrit.extensions.registration
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|registration
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Binding
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
name|Injector
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
name|Key
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
name|TypeLiteral
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

begin_class
DECL|class|DynamicItemProvider
class|class
name|DynamicItemProvider
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Provider
argument_list|<
name|DynamicItem
argument_list|<
name|T
argument_list|>
argument_list|>
block|{
DECL|field|type
specifier|private
specifier|final
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|type
decl_stmt|;
DECL|field|key
specifier|private
specifier|final
name|Key
argument_list|<
name|DynamicItem
argument_list|<
name|T
argument_list|>
argument_list|>
name|key
decl_stmt|;
annotation|@
name|Inject
DECL|field|injector
specifier|private
name|Injector
name|injector
decl_stmt|;
DECL|method|DynamicItemProvider (TypeLiteral<T> type, Key<DynamicItem<T>> key)
name|DynamicItemProvider
parameter_list|(
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|Key
argument_list|<
name|DynamicItem
argument_list|<
name|T
argument_list|>
argument_list|>
name|key
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
DECL|method|get ()
specifier|public
name|DynamicItem
argument_list|<
name|T
argument_list|>
name|get
parameter_list|()
block|{
return|return
operator|new
name|DynamicItem
argument_list|<>
argument_list|(
name|key
argument_list|,
name|find
argument_list|(
name|injector
argument_list|,
name|type
argument_list|)
argument_list|,
literal|"gerrit"
argument_list|)
return|;
block|}
DECL|method|find (Injector src, TypeLiteral<T> type)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Provider
argument_list|<
name|T
argument_list|>
name|find
parameter_list|(
name|Injector
name|src
parameter_list|,
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
name|List
argument_list|<
name|Binding
argument_list|<
name|T
argument_list|>
argument_list|>
name|bindings
init|=
name|src
operator|.
name|findBindingsByType
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|bindings
operator|!=
literal|null
operator|&&
name|bindings
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|bindings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getProvider
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|bindings
operator|!=
literal|null
operator|&&
name|bindings
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Multiple providers bound for DynamicItem<%s>\n"
operator|+
literal|"This is not allowed; check the server configuration."
argument_list|,
name|type
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

