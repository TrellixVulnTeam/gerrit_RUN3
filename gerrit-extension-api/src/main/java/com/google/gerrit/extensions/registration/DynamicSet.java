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
name|Binder
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
name|Scopes
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|binder
operator|.
name|LinkedBindingBuilder
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
name|internal
operator|.
name|UniqueAnnotations
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
name|util
operator|.
name|Providers
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
name|util
operator|.
name|Types
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_comment
comment|/**  * A set of members that can be modified as plugins reload.  *<p>  * DynamicSets are always mapped as singletons in Guice. Sets store Providers  * internally, and resolve the provider to an instance on demand. This enables  * registrations to decide between singleton and non-singleton members.  */
end_comment

begin_class
DECL|class|DynamicSet
specifier|public
class|class
name|DynamicSet
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Iterable
argument_list|<
name|T
argument_list|>
block|{
comment|/**    * Declare a singleton {@code DynamicSet<T>} with a binder.    *<p>    * Sets must be defined in a Guice module before they can be bound:    *<pre>    *   DynamicSet.setOf(binder(), Interface.class);    *   DynamicSet.bind(binder(), Interface.class).to(Impl.class);    *</pre>    *    * @param binder a new binder created in the module.    * @param member type of entry in the set.    */
DECL|method|setOf (Binder binder, Class<T> member)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|setOf
parameter_list|(
name|Binder
name|binder
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|member
parameter_list|)
block|{
name|setOf
argument_list|(
name|binder
argument_list|,
name|TypeLiteral
operator|.
name|get
argument_list|(
name|member
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Declare a singleton {@code DynamicSet<T>} with a binder.    *<p>    * Sets must be defined in a Guice module before they can be bound:    *<pre>    * {@code    *   DynamicSet.setOf(binder(), new TypeLiteral<Thing<Foo>>() {});    * }    *</pre>    *    * @param binder a new binder created in the module.    * @param member type of entry in the set.    */
DECL|method|setOf (Binder binder, TypeLiteral<T> member)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|setOf
parameter_list|(
name|Binder
name|binder
parameter_list|,
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|member
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Key
argument_list|<
name|DynamicSet
argument_list|<
name|T
argument_list|>
argument_list|>
name|key
init|=
operator|(
name|Key
argument_list|<
name|DynamicSet
argument_list|<
name|T
argument_list|>
argument_list|>
operator|)
name|Key
operator|.
name|get
argument_list|(
name|Types
operator|.
name|newParameterizedType
argument_list|(
name|DynamicSet
operator|.
name|class
argument_list|,
name|member
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|binder
operator|.
name|bind
argument_list|(
name|key
argument_list|)
operator|.
name|toProvider
argument_list|(
operator|new
name|DynamicSetProvider
argument_list|<>
argument_list|(
name|member
argument_list|)
argument_list|)
operator|.
name|in
argument_list|(
name|Scopes
operator|.
name|SINGLETON
argument_list|)
expr_stmt|;
block|}
comment|/**    * Bind one implementation into the set using a unique annotation.    *    * @param binder a new binder created in the module.    * @param type type of entries in the set.    * @return a binder to continue configuring the new set member.    */
DECL|method|bind (Binder binder, Class<T> type)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|LinkedBindingBuilder
argument_list|<
name|T
argument_list|>
name|bind
parameter_list|(
name|Binder
name|binder
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|bind
argument_list|(
name|binder
argument_list|,
name|TypeLiteral
operator|.
name|get
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Bind one implementation into the set using a unique annotation.    *    * @param binder a new binder created in the module.    * @param type type of entries in the set.    * @return a binder to continue configuring the new set member.    */
DECL|method|bind (Binder binder, TypeLiteral<T> type)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|LinkedBindingBuilder
argument_list|<
name|T
argument_list|>
name|bind
parameter_list|(
name|Binder
name|binder
parameter_list|,
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|binder
operator|.
name|bind
argument_list|(
name|type
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|UniqueAnnotations
operator|.
name|create
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Bind a named implementation into the set.    *    * @param binder a new binder created in the module.    * @param type type of entries in the set.    * @param name {@code @Named} annotation to apply instead of a unique    *        annotation.    * @return a binder to continue configuring the new set member.    */
DECL|method|bind (Binder binder, Class<T> type, Named name)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|LinkedBindingBuilder
argument_list|<
name|T
argument_list|>
name|bind
parameter_list|(
name|Binder
name|binder
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|Named
name|name
parameter_list|)
block|{
return|return
name|bind
argument_list|(
name|binder
argument_list|,
name|TypeLiteral
operator|.
name|get
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Bind a named implementation into the set.    *    * @param binder a new binder created in the module.    * @param type type of entries in the set.    * @param name {@code @Named} annotation to apply instead of a unique    *        annotation.    * @return a binder to continue configuring the new set member.    */
DECL|method|bind (Binder binder, TypeLiteral<T> type, Named name)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|LinkedBindingBuilder
argument_list|<
name|T
argument_list|>
name|bind
parameter_list|(
name|Binder
name|binder
parameter_list|,
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|Named
name|name
parameter_list|)
block|{
return|return
name|binder
operator|.
name|bind
argument_list|(
name|type
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|emptySet ()
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|DynamicSet
argument_list|<
name|T
argument_list|>
name|emptySet
parameter_list|()
block|{
return|return
operator|new
name|DynamicSet
argument_list|<>
argument_list|(
name|Collections
operator|.
expr|<
name|AtomicReference
argument_list|<
name|Provider
argument_list|<
name|T
argument_list|>
argument_list|>
operator|>
name|emptySet
argument_list|()
argument_list|)
return|;
block|}
DECL|field|items
specifier|private
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|AtomicReference
argument_list|<
name|Provider
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|>
name|items
decl_stmt|;
DECL|method|DynamicSet (Collection<AtomicReference<Provider<T>>> base)
name|DynamicSet
parameter_list|(
name|Collection
argument_list|<
name|AtomicReference
argument_list|<
name|Provider
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|>
name|base
parameter_list|)
block|{
name|items
operator|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|(
name|base
argument_list|)
expr_stmt|;
block|}
DECL|method|DynamicSet ()
specifier|public
name|DynamicSet
parameter_list|()
block|{
name|this
argument_list|(
name|Collections
operator|.
expr|<
name|AtomicReference
argument_list|<
name|Provider
argument_list|<
name|T
argument_list|>
argument_list|>
operator|>
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|AtomicReference
argument_list|<
name|Provider
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|>
name|itr
init|=
name|items
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|private
name|T
name|next
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
while|while
condition|(
name|next
operator|==
literal|null
operator|&&
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Provider
argument_list|<
name|T
argument_list|>
name|p
init|=
name|itr
operator|.
name|next
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|next
operator|=
name|p
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// TODO Log failed member of DynamicSet.
block|}
block|}
block|}
return|return
name|next
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|T
name|next
parameter_list|()
block|{
if|if
condition|(
name|hasNext
argument_list|()
condition|)
block|{
name|T
name|result
init|=
name|next
decl_stmt|;
name|next
operator|=
literal|null
expr_stmt|;
return|return
name|result
return|;
block|}
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
comment|/**    * Add one new element to the set.    *    * @param item the item to add to the collection. Must not be null.    * @return handle to remove the item at a later point in time.    */
DECL|method|add (final T item)
specifier|public
name|RegistrationHandle
name|add
parameter_list|(
specifier|final
name|T
name|item
parameter_list|)
block|{
return|return
name|add
argument_list|(
name|Providers
operator|.
name|of
argument_list|(
name|item
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Add one new element to the set.    *    * @param item the item to add to the collection. Must not be null.    * @return handle to remove the item at a later point in time.    */
DECL|method|add (final Provider<T> item)
specifier|public
name|RegistrationHandle
name|add
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|T
argument_list|>
name|item
parameter_list|)
block|{
specifier|final
name|AtomicReference
argument_list|<
name|Provider
argument_list|<
name|T
argument_list|>
argument_list|>
name|ref
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
name|item
argument_list|)
decl_stmt|;
name|items
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
return|return
operator|new
name|RegistrationHandle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
if|if
condition|(
name|ref
operator|.
name|compareAndSet
argument_list|(
name|item
argument_list|,
literal|null
argument_list|)
condition|)
block|{
name|items
operator|.
name|remove
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
comment|/**    * Add one new element that may be hot-replaceable in the future.    *    * @param key unique description from the item's Guice binding. This can be    *        later obtained from the registration handle to facilitate matching    *        with the new equivalent instance during a hot reload.    * @param item the item to add to the collection right now. Must not be null.    * @return a handle that can remove this item later, or hot-swap the item    *         without it ever leaving the collection.    */
DECL|method|add (Key<T> key, Provider<T> item)
specifier|public
name|ReloadableRegistrationHandle
argument_list|<
name|T
argument_list|>
name|add
parameter_list|(
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|,
name|Provider
argument_list|<
name|T
argument_list|>
name|item
parameter_list|)
block|{
name|AtomicReference
argument_list|<
name|Provider
argument_list|<
name|T
argument_list|>
argument_list|>
name|ref
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
name|item
argument_list|)
decl_stmt|;
name|items
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
return|return
operator|new
name|ReloadableHandle
argument_list|(
name|ref
argument_list|,
name|key
argument_list|,
name|item
argument_list|)
return|;
block|}
DECL|class|ReloadableHandle
specifier|private
class|class
name|ReloadableHandle
implements|implements
name|ReloadableRegistrationHandle
argument_list|<
name|T
argument_list|>
block|{
DECL|field|ref
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|Provider
argument_list|<
name|T
argument_list|>
argument_list|>
name|ref
decl_stmt|;
DECL|field|key
specifier|private
specifier|final
name|Key
argument_list|<
name|T
argument_list|>
name|key
decl_stmt|;
DECL|field|item
specifier|private
specifier|final
name|Provider
argument_list|<
name|T
argument_list|>
name|item
decl_stmt|;
DECL|method|ReloadableHandle (AtomicReference<Provider<T>> ref, Key<T> key, Provider<T> item)
name|ReloadableHandle
parameter_list|(
name|AtomicReference
argument_list|<
name|Provider
argument_list|<
name|T
argument_list|>
argument_list|>
name|ref
parameter_list|,
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|,
name|Provider
argument_list|<
name|T
argument_list|>
name|item
parameter_list|)
block|{
name|this
operator|.
name|ref
operator|=
name|ref
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|item
operator|=
name|item
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|remove ()
specifier|public
name|void
name|remove
parameter_list|()
block|{
if|if
condition|(
name|ref
operator|.
name|compareAndSet
argument_list|(
name|item
argument_list|,
literal|null
argument_list|)
condition|)
block|{
name|items
operator|.
name|remove
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getKey ()
specifier|public
name|Key
argument_list|<
name|T
argument_list|>
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
annotation|@
name|Override
DECL|method|replace (Key<T> newKey, Provider<T> newItem)
specifier|public
name|ReloadableHandle
name|replace
parameter_list|(
name|Key
argument_list|<
name|T
argument_list|>
name|newKey
parameter_list|,
name|Provider
argument_list|<
name|T
argument_list|>
name|newItem
parameter_list|)
block|{
if|if
condition|(
name|ref
operator|.
name|compareAndSet
argument_list|(
name|item
argument_list|,
name|newItem
argument_list|)
condition|)
block|{
return|return
operator|new
name|ReloadableHandle
argument_list|(
name|ref
argument_list|,
name|newKey
argument_list|,
name|newItem
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

