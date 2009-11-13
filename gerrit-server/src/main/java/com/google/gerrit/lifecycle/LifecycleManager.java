begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.lifecycle
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|lifecycle
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
name|TypeLiteral
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
comment|/** Tracks and executes registered {@link LifecycleListener}s. */
end_comment

begin_class
DECL|class|LifecycleManager
specifier|public
class|class
name|LifecycleManager
block|{
DECL|field|listeners
specifier|private
specifier|final
name|LinkedHashMap
argument_list|<
name|LifecycleListener
argument_list|,
name|Boolean
argument_list|>
name|listeners
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|LifecycleListener
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|started
specifier|private
name|boolean
name|started
decl_stmt|;
comment|/** Add a single listener. */
DECL|method|add (final LifecycleListener listener)
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|LifecycleListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|put
argument_list|(
name|listener
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Add all {@link LifecycleListener}s registered in the Injector. */
DECL|method|add (final Injector injector)
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|Injector
name|injector
parameter_list|)
block|{
if|if
condition|(
name|started
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Already started"
argument_list|)
throw|;
block|}
for|for
control|(
specifier|final
name|Binding
argument_list|<
name|LifecycleListener
argument_list|>
name|binding
range|:
name|get
argument_list|(
name|injector
argument_list|)
control|)
block|{
name|add
argument_list|(
name|binding
operator|.
name|getProvider
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Add all {@link LifecycleListener}s registered in the Injectors. */
DECL|method|add (final Injector... injectors)
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|Injector
modifier|...
name|injectors
parameter_list|)
block|{
for|for
control|(
specifier|final
name|Injector
name|i
range|:
name|injectors
control|)
block|{
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Start all listeners, in the order they were registered. */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
operator|!
name|started
condition|)
block|{
name|started
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|LifecycleListener
name|obj
range|:
name|listeners
operator|.
name|keySet
argument_list|()
control|)
block|{
name|obj
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** Stop all listeners, in the reverse order they were registered. */
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|started
condition|)
block|{
specifier|final
name|List
argument_list|<
name|LifecycleListener
argument_list|>
name|t
init|=
operator|new
name|ArrayList
argument_list|<
name|LifecycleListener
argument_list|>
argument_list|(
name|listeners
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|t
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
literal|0
operator|<=
name|i
condition|;
name|i
operator|--
control|)
block|{
specifier|final
name|LifecycleListener
name|obj
init|=
name|t
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
try|try
block|{
name|obj
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|err
parameter_list|)
block|{
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|obj
operator|.
name|getClass
argument_list|()
argument_list|)
operator|.
name|warn
argument_list|(
literal|"Failed to stop"
argument_list|,
name|err
argument_list|)
expr_stmt|;
block|}
block|}
name|started
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|get (Injector i)
specifier|private
specifier|static
name|List
argument_list|<
name|Binding
argument_list|<
name|LifecycleListener
argument_list|>
argument_list|>
name|get
parameter_list|(
name|Injector
name|i
parameter_list|)
block|{
return|return
name|i
operator|.
name|findBindingsByType
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|LifecycleListener
argument_list|>
argument_list|()
block|{}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

