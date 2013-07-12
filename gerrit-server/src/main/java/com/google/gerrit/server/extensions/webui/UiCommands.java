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
DECL|package|com.google.gerrit.server.extensions.webui
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|extensions
operator|.
name|webui
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
name|Function
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
name|Predicate
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
name|Predicates
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
name|Iterables
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
name|Lists
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
name|Sets
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
name|UiCommandDetail
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
name|DynamicMap
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
name|ChildCollection
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
name|RestResource
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
name|RestView
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
name|webui
operator|.
name|UiCommand
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_class
DECL|class|UiCommands
specifier|public
class|class
name|UiCommands
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
name|UiCommands
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|enabled ()
specifier|public
specifier|static
name|Predicate
argument_list|<
name|UiCommandDetail
argument_list|>
name|enabled
parameter_list|()
block|{
return|return
operator|new
name|Predicate
argument_list|<
name|UiCommandDetail
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|UiCommandDetail
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|enabled
return|;
block|}
block|}
return|;
block|}
DECL|method|sorted (Iterable<UiCommandDetail> in)
specifier|public
specifier|static
name|List
argument_list|<
name|UiCommandDetail
argument_list|>
name|sorted
parameter_list|(
name|Iterable
argument_list|<
name|UiCommandDetail
argument_list|>
name|in
parameter_list|)
block|{
name|List
argument_list|<
name|UiCommandDetail
argument_list|>
name|s
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|s
argument_list|,
operator|new
name|Comparator
argument_list|<
name|UiCommandDetail
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|UiCommandDetail
name|a
parameter_list|,
name|UiCommandDetail
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|id
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|id
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
DECL|method|from ( ChildCollection<?, R> collection, R resource, EnumSet<UiCommand.Place> places)
specifier|public
specifier|static
parameter_list|<
name|R
extends|extends
name|RestResource
parameter_list|>
name|Iterable
argument_list|<
name|UiCommandDetail
argument_list|>
name|from
parameter_list|(
name|ChildCollection
argument_list|<
name|?
argument_list|,
name|R
argument_list|>
name|collection
parameter_list|,
name|R
name|resource
parameter_list|,
name|EnumSet
argument_list|<
name|UiCommand
operator|.
name|Place
argument_list|>
name|places
parameter_list|)
block|{
return|return
name|from
argument_list|(
name|collection
operator|.
name|views
argument_list|()
argument_list|,
name|resource
argument_list|,
name|places
argument_list|)
return|;
block|}
DECL|method|from ( DynamicMap<RestView<R>> views, final R resource, final EnumSet<UiCommand.Place> places)
specifier|public
specifier|static
parameter_list|<
name|R
extends|extends
name|RestResource
parameter_list|>
name|Iterable
argument_list|<
name|UiCommandDetail
argument_list|>
name|from
parameter_list|(
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|R
argument_list|>
argument_list|>
name|views
parameter_list|,
specifier|final
name|R
name|resource
parameter_list|,
specifier|final
name|EnumSet
argument_list|<
name|UiCommand
operator|.
name|Place
argument_list|>
name|places
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|views
argument_list|,
operator|new
name|Function
argument_list|<
name|DynamicMap
operator|.
name|Entry
argument_list|<
name|RestView
argument_list|<
name|R
argument_list|>
argument_list|>
argument_list|,
name|UiCommandDetail
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|UiCommandDetail
name|apply
parameter_list|(
name|DynamicMap
operator|.
name|Entry
argument_list|<
name|RestView
argument_list|<
name|R
argument_list|>
argument_list|>
name|e
parameter_list|)
block|{
name|int
name|d
init|=
name|e
operator|.
name|getExportName
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|<
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|method
init|=
name|e
operator|.
name|getExportName
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|d
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|e
operator|.
name|getExportName
argument_list|()
operator|.
name|substring
argument_list|(
name|d
operator|+
literal|1
argument_list|)
decl_stmt|;
name|RestView
argument_list|<
name|R
argument_list|>
name|view
decl_stmt|;
try|try
block|{
name|view
operator|=
name|e
operator|.
name|getProvider
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|err
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"error creating view %s.%s"
argument_list|,
name|e
operator|.
name|getPluginName
argument_list|()
argument_list|,
name|e
operator|.
name|getExportName
argument_list|()
argument_list|)
argument_list|,
name|err
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|view
operator|instanceof
name|UiCommand
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|UiCommand
argument_list|<
name|R
argument_list|>
name|cmd
init|=
operator|(
name|UiCommand
argument_list|<
name|R
argument_list|>
operator|)
name|view
decl_stmt|;
if|if
condition|(
name|Sets
operator|.
name|intersection
argument_list|(
name|cmd
operator|.
name|getPlaces
argument_list|()
argument_list|,
name|places
argument_list|)
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|cmd
operator|.
name|isVisible
argument_list|(
name|resource
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|UiCommandDetail
name|dsc
init|=
operator|new
name|UiCommandDetail
argument_list|()
decl_stmt|;
name|dsc
operator|.
name|id
operator|=
literal|"gerrit"
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getPluginName
argument_list|()
argument_list|)
condition|?
name|name
else|:
name|e
operator|.
name|getPluginName
argument_list|()
operator|+
literal|'~'
operator|+
name|name
expr_stmt|;
name|dsc
operator|.
name|method
operator|=
name|method
expr_stmt|;
name|dsc
operator|.
name|label
operator|=
name|cmd
operator|.
name|getLabel
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|dsc
operator|.
name|title
operator|=
name|cmd
operator|.
name|getTitle
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|dsc
operator|.
name|enabled
operator|=
name|cmd
operator|.
name|isEnabled
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|dsc
operator|.
name|confirmationMessage
operator|=
name|cmd
operator|.
name|getConfirmationMessage
argument_list|(
name|resource
argument_list|)
expr_stmt|;
return|return
name|dsc
return|;
block|}
block|}
argument_list|)
argument_list|,
name|Predicates
operator|.
name|notNull
argument_list|()
argument_list|)
return|;
block|}
DECL|method|UiCommands ()
specifier|private
name|UiCommands
parameter_list|()
block|{   }
block|}
end_class

end_unit

