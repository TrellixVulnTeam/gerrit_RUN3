begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2019 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
operator|.
name|toImmutableList
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
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
name|base
operator|.
name|MoreObjects
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
name|collect
operator|.
name|ImmutableListMultimap
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
name|Nullable
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
name|Exports
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
name|common
operator|.
name|ChangeInfo
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
name|common
operator|.
name|PluginDefinedInfo
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
name|DynamicOptions
operator|.
name|DynamicBean
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
name|change
operator|.
name|ChangeAttributeFactory
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
name|restapi
operator|.
name|change
operator|.
name|GetChange
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
name|restapi
operator|.
name|change
operator|.
name|QueryChanges
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
name|commands
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|Gson
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|reflect
operator|.
name|TypeToken
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
name|Module
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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

begin_class
DECL|class|AbstractPluginFieldsTest
specifier|public
class|class
name|AbstractPluginFieldsTest
extends|extends
name|AbstractDaemonTest
block|{
DECL|class|MyInfo
specifier|protected
specifier|static
class|class
name|MyInfo
extends|extends
name|PluginDefinedInfo
block|{
DECL|field|theAttribute
annotation|@
name|Nullable
name|String
name|theAttribute
decl_stmt|;
DECL|method|MyInfo (@ullable String theAttribute)
specifier|public
name|MyInfo
parameter_list|(
annotation|@
name|Nullable
name|String
name|theAttribute
parameter_list|)
block|{
name|this
operator|.
name|theAttribute
operator|=
name|theAttribute
expr_stmt|;
block|}
DECL|method|MyInfo (String name, @Nullable String theAttribute)
name|MyInfo
parameter_list|(
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
name|String
name|theAttribute
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|requireNonNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|theAttribute
operator|=
name|theAttribute
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|MyInfo
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|MyInfo
name|i
init|=
operator|(
name|MyInfo
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|name
argument_list|,
name|i
operator|.
name|name
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|theAttribute
argument_list|,
name|i
operator|.
name|theAttribute
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|name
argument_list|,
name|theAttribute
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|MoreObjects
operator|.
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
operator|.
name|add
argument_list|(
literal|"theAttribute"
argument_list|,
name|theAttribute
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|class|NullAttributeModule
specifier|protected
specifier|static
class|class
name|NullAttributeModule
extends|extends
name|AbstractModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|public
name|void
name|configure
parameter_list|()
block|{
name|DynamicSet
operator|.
name|bind
argument_list|(
name|binder
argument_list|()
argument_list|,
name|ChangeAttributeFactory
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
parameter_list|(
name|cd
parameter_list|,
name|bp
parameter_list|,
name|p
parameter_list|)
lambda|->
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SimpleAttributeModule
specifier|protected
specifier|static
class|class
name|SimpleAttributeModule
extends|extends
name|AbstractModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|public
name|void
name|configure
parameter_list|()
block|{
name|DynamicSet
operator|.
name|bind
argument_list|(
name|binder
argument_list|()
argument_list|,
name|ChangeAttributeFactory
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
parameter_list|(
name|cd
parameter_list|,
name|bp
parameter_list|,
name|p
parameter_list|)
lambda|->
operator|new
name|MyInfo
argument_list|(
literal|"change "
operator|+
name|cd
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MyOptions
specifier|private
specifier|static
class|class
name|MyOptions
implements|implements
name|DynamicBean
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--opt"
argument_list|)
DECL|field|opt
specifier|private
name|String
name|opt
decl_stmt|;
block|}
DECL|class|OptionAttributeModule
specifier|protected
specifier|static
class|class
name|OptionAttributeModule
extends|extends
name|AbstractModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|public
name|void
name|configure
parameter_list|()
block|{
name|DynamicSet
operator|.
name|bind
argument_list|(
name|binder
argument_list|()
argument_list|,
name|ChangeAttributeFactory
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
parameter_list|(
name|cd
parameter_list|,
name|bp
parameter_list|,
name|p
parameter_list|)
lambda|->
block|{
name|MyOptions
name|opts
init|=
operator|(
name|MyOptions
operator|)
name|bp
operator|.
name|getDynamicBean
argument_list|(
name|p
argument_list|)
decl_stmt|;
return|return
name|opts
operator|!=
literal|null
condition|?
operator|new
name|MyInfo
argument_list|(
literal|"opt "
operator|+
name|opts
operator|.
name|opt
argument_list|)
else|:
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|DynamicBean
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|Exports
operator|.
name|named
argument_list|(
name|Query
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|to
argument_list|(
name|MyOptions
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|DynamicBean
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|Exports
operator|.
name|named
argument_list|(
name|QueryChanges
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|to
argument_list|(
name|MyOptions
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|DynamicBean
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|Exports
operator|.
name|named
argument_list|(
name|GetChange
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|to
argument_list|(
name|MyOptions
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getChangeWithNullAttribute (PluginInfoGetter getter)
specifier|protected
name|void
name|getChangeWithNullAttribute
parameter_list|(
name|PluginInfoGetter
name|getter
parameter_list|)
throws|throws
name|Exception
block|{
name|Change
operator|.
name|Id
name|id
init|=
name|createChange
argument_list|()
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|getter
operator|.
name|call
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
try|try
init|(
name|AutoCloseable
name|ignored
init|=
name|installPlugin
argument_list|(
literal|"my-plugin"
argument_list|,
name|NullAttributeModule
operator|.
name|class
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|getter
operator|.
name|call
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|getter
operator|.
name|call
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
DECL|method|getChangeWithSimpleAttribute (PluginInfoGetter getter)
specifier|protected
name|void
name|getChangeWithSimpleAttribute
parameter_list|(
name|PluginInfoGetter
name|getter
parameter_list|)
throws|throws
name|Exception
block|{
name|getChangeWithSimpleAttribute
argument_list|(
name|getter
argument_list|,
name|SimpleAttributeModule
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|getChangeWithSimpleAttribute ( PluginInfoGetter getter, Class<? extends Module> moduleClass)
specifier|protected
name|void
name|getChangeWithSimpleAttribute
parameter_list|(
name|PluginInfoGetter
name|getter
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|moduleClass
parameter_list|)
throws|throws
name|Exception
block|{
name|Change
operator|.
name|Id
name|id
init|=
name|createChange
argument_list|()
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|getter
operator|.
name|call
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
try|try
init|(
name|AutoCloseable
name|ignored
init|=
name|installPlugin
argument_list|(
literal|"my-plugin"
argument_list|,
name|moduleClass
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|getter
operator|.
name|call
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
operator|new
name|MyInfo
argument_list|(
literal|"my-plugin"
argument_list|,
literal|"change "
operator|+
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|getter
operator|.
name|call
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
DECL|method|getChangeWithOption ( PluginInfoGetter getterWithoutOptions, PluginInfoGetterWithOptions getterWithOptions)
specifier|protected
name|void
name|getChangeWithOption
parameter_list|(
name|PluginInfoGetter
name|getterWithoutOptions
parameter_list|,
name|PluginInfoGetterWithOptions
name|getterWithOptions
parameter_list|)
throws|throws
name|Exception
block|{
name|Change
operator|.
name|Id
name|id
init|=
name|createChange
argument_list|()
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|getterWithoutOptions
operator|.
name|call
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
try|try
init|(
name|AutoCloseable
name|ignored
init|=
name|installPlugin
argument_list|(
literal|"my-plugin"
argument_list|,
name|OptionAttributeModule
operator|.
name|class
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|getterWithoutOptions
operator|.
name|call
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
operator|new
name|MyInfo
argument_list|(
literal|"my-plugin"
argument_list|,
literal|"opt null"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getterWithOptions
operator|.
name|call
argument_list|(
name|id
argument_list|,
name|ImmutableListMultimap
operator|.
name|of
argument_list|(
literal|"my-plugin--opt"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
operator|new
name|MyInfo
argument_list|(
literal|"my-plugin"
argument_list|,
literal|"opt foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|getterWithoutOptions
operator|.
name|call
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
DECL|method|pluginInfoFromSingletonList (List<ChangeInfo> changeInfos)
specifier|protected
specifier|static
name|List
argument_list|<
name|MyInfo
argument_list|>
name|pluginInfoFromSingletonList
parameter_list|(
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|changeInfos
parameter_list|)
block|{
name|assertThat
argument_list|(
name|changeInfos
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
name|pluginInfoFromChangeInfo
argument_list|(
name|changeInfos
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
DECL|method|pluginInfoFromChangeInfo (ChangeInfo changeInfo)
specifier|protected
specifier|static
name|List
argument_list|<
name|MyInfo
argument_list|>
name|pluginInfoFromChangeInfo
parameter_list|(
name|ChangeInfo
name|changeInfo
parameter_list|)
block|{
name|List
argument_list|<
name|PluginDefinedInfo
argument_list|>
name|pluginInfo
init|=
name|changeInfo
operator|.
name|plugins
decl_stmt|;
if|if
condition|(
name|pluginInfo
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|pluginInfo
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|MyInfo
operator|.
name|class
operator|::
name|cast
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Decode {@code MyInfo}s from a raw list of maps returned from Gson.    *    *<p>This method is used instead of decoding {@code ChangeInfo} or {@code ChangAttribute}, since    * Gson would decode the {@code plugins} field as a {@code List<PluginDefinedInfo>}, which would    * return the base type and silently ignore any fields that are defined only in the subclass.    * Instead, decode the enclosing {@code ChangeInfo} or {@code ChangeAttribute} as a raw {@code    * Map<String, Object>}, and pass the {@code "plugins"} value to this method.    *    * @param gson Gson converter.    * @param plugins list of {@code MyInfo} objects, each as a raw map returned from Gson.    * @return decoded list of {@code MyInfo}s.    */
DECL|method|decodeRawPluginsList (Gson gson, @Nullable Object plugins)
specifier|protected
specifier|static
name|List
argument_list|<
name|MyInfo
argument_list|>
name|decodeRawPluginsList
parameter_list|(
name|Gson
name|gson
parameter_list|,
annotation|@
name|Nullable
name|Object
name|plugins
parameter_list|)
block|{
if|if
condition|(
name|plugins
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|checkArgument
argument_list|(
name|plugins
operator|instanceof
name|List
argument_list|,
literal|"not a list: %s"
argument_list|,
name|plugins
argument_list|)
expr_stmt|;
return|return
name|gson
operator|.
name|fromJson
argument_list|(
name|gson
operator|.
name|toJson
argument_list|(
name|plugins
argument_list|)
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|List
argument_list|<
name|MyInfo
argument_list|>
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|FunctionalInterface
DECL|interface|PluginInfoGetter
specifier|protected
interface|interface
name|PluginInfoGetter
block|{
DECL|method|call (Change.Id id)
name|List
argument_list|<
name|MyInfo
argument_list|>
name|call
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
annotation|@
name|FunctionalInterface
DECL|interface|PluginInfoGetterWithOptions
specifier|protected
interface|interface
name|PluginInfoGetterWithOptions
block|{
DECL|method|call (Change.Id id, ImmutableListMultimap<String, String> pluginOptions)
name|List
argument_list|<
name|MyInfo
argument_list|>
name|call
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|,
name|ImmutableListMultimap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|pluginOptions
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
block|}
end_class

end_unit

