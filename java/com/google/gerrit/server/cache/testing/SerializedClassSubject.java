begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.cache.testing
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|cache
operator|.
name|testing
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
name|collect
operator|.
name|ImmutableMap
operator|.
name|toImmutableMap
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
name|assertAbout
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
name|assertWithMessage
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
name|truth
operator|.
name|FailureMetadata
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
name|truth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|reflect
operator|.
name|FieldUtils
import|;
end_import

begin_comment
comment|/**  * Subject about classes that are serialized into persistent caches.  *  *<p>Hand-written {@link com.google.gerrit.server.cache.serialize.CacheSerializer CacheSerializer}  * implementations depend on the exact representation of the data stored in a class, so it is  * important to verify any assumptions about the structure of the serialized classes. This class  * contains assertions about serialized classes, and should be used for every class that has a  * custom serializer implementation.  *  *<p>Changing fields of a serialized class (or abstract methods, in the case of {@code @AutoValue}  * classes) will likely require changes to the serializer implementation, and may require bumping  * the {@link com.google.gerrit.server.cache.PersistentCacheBinding#version(int) version} in the  * cache binding, in case the representation has changed in such a way that old serialized data  * becomes unreadable.  *  *<p>Changes to a serialized class such as adding or removing fields generally requires a change to  * the hand-written serializer. Usually, serializer implementations should be written in such a way  * that new fields are considered optional, and won't require bumping the version.  */
end_comment

begin_class
DECL|class|SerializedClassSubject
specifier|public
class|class
name|SerializedClassSubject
extends|extends
name|Subject
argument_list|<
name|SerializedClassSubject
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
block|{
DECL|method|assertThatSerializedClass (Class<?> actual)
specifier|public
specifier|static
name|SerializedClassSubject
name|assertThatSerializedClass
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|actual
parameter_list|)
block|{
comment|// This formulation fails in Eclipse 4.7.3a with "The type
comment|// SerializedClassSubject does not define SerializedClassSubject() that is
comment|// applicable here", due to
comment|// https://bugs.eclipse.org/bugs/show_bug.cgi?id=534694 or a similar bug:
comment|// return assertAbout(SerializedClassSubject::new).that(actual);
name|Subject
operator|.
name|Factory
argument_list|<
name|SerializedClassSubject
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|factory
init|=
parameter_list|(
name|m
parameter_list|,
name|a
parameter_list|)
lambda|->
operator|new
name|SerializedClassSubject
argument_list|(
name|m
argument_list|,
name|a
argument_list|)
decl_stmt|;
return|return
name|assertAbout
argument_list|(
name|factory
argument_list|)
operator|.
name|that
argument_list|(
name|actual
argument_list|)
return|;
block|}
DECL|method|SerializedClassSubject (FailureMetadata metadata, Class<?> actual)
specifier|private
name|SerializedClassSubject
parameter_list|(
name|FailureMetadata
name|metadata
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|actual
parameter_list|)
block|{
name|super
argument_list|(
name|metadata
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
DECL|method|isAbstract ()
specifier|public
name|void
name|isAbstract
parameter_list|()
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|assertWithMessage
argument_list|(
literal|"expected class %s to be abstract"
argument_list|,
name|actual
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|that
argument_list|(
name|Modifier
operator|.
name|isAbstract
argument_list|(
name|actual
argument_list|()
operator|.
name|getModifiers
argument_list|()
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
DECL|method|isConcrete ()
specifier|public
name|void
name|isConcrete
parameter_list|()
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|assertWithMessage
argument_list|(
literal|"expected class %s to be concrete"
argument_list|,
name|actual
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|that
argument_list|(
operator|!
name|Modifier
operator|.
name|isAbstract
argument_list|(
name|actual
argument_list|()
operator|.
name|getModifiers
argument_list|()
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
DECL|method|hasFields (Map<String, Type> expectedFields)
specifier|public
name|void
name|hasFields
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Type
argument_list|>
name|expectedFields
parameter_list|)
block|{
name|isConcrete
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|FieldUtils
operator|.
name|getAllFieldsList
argument_list|(
name|actual
argument_list|()
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|f
lambda|->
operator|!
name|Modifier
operator|.
name|isStatic
argument_list|(
name|f
operator|.
name|getModifiers
argument_list|()
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableMap
argument_list|(
name|Field
operator|::
name|getName
argument_list|,
name|Field
operator|::
name|getGenericType
argument_list|)
argument_list|)
argument_list|)
operator|.
name|containsExactlyEntriesIn
argument_list|(
name|expectedFields
argument_list|)
expr_stmt|;
block|}
DECL|method|hasAutoValueMethods (Map<String, Type> expectedMethods)
specifier|public
name|void
name|hasAutoValueMethods
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Type
argument_list|>
name|expectedMethods
parameter_list|)
block|{
comment|// Would be nice if we could check clazz is an @AutoValue, but the retention is not RUNTIME.
name|isAbstract
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|Arrays
operator|.
name|stream
argument_list|(
name|actual
argument_list|()
operator|.
name|getDeclaredMethods
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|m
lambda|->
operator|!
name|Modifier
operator|.
name|isStatic
argument_list|(
name|m
operator|.
name|getModifiers
argument_list|()
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|m
lambda|->
name|Modifier
operator|.
name|isAbstract
argument_list|(
name|m
operator|.
name|getModifiers
argument_list|()
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|m
lambda|->
name|m
operator|.
name|getParameters
argument_list|()
operator|.
name|length
operator|==
literal|0
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableMap
argument_list|(
name|Method
operator|::
name|getName
argument_list|,
name|Method
operator|::
name|getGenericReturnType
argument_list|)
argument_list|)
argument_list|)
operator|.
name|named
argument_list|(
literal|"no-argument abstract methods on %s"
argument_list|,
name|actual
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expectedMethods
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

