begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
DECL|package|com.google.gerrit.truth
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|truth
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
name|truth
operator|.
name|Truth
operator|.
name|assertAbout
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
name|IterableSubject
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
name|function
operator|.
name|Function
import|;
end_import

begin_class
DECL|class|ListSubject
specifier|public
class|class
name|ListSubject
parameter_list|<
name|S
extends|extends
name|Subject
parameter_list|<
name|S
parameter_list|,
name|E
parameter_list|>
parameter_list|,
name|E
parameter_list|>
extends|extends
name|IterableSubject
block|{
DECL|field|elementAssertThatFunction
specifier|private
specifier|final
name|Function
argument_list|<
name|E
argument_list|,
name|S
argument_list|>
name|elementAssertThatFunction
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|assertThat ( List<E> list, Function<E, S> elementAssertThatFunction)
specifier|public
specifier|static
parameter_list|<
name|S
extends|extends
name|Subject
argument_list|<
name|S
argument_list|,
name|E
argument_list|>
parameter_list|,
name|E
parameter_list|>
name|ListSubject
argument_list|<
name|S
argument_list|,
name|E
argument_list|>
name|assertThat
parameter_list|(
name|List
argument_list|<
name|E
argument_list|>
name|list
parameter_list|,
name|Function
argument_list|<
name|E
argument_list|,
name|S
argument_list|>
name|elementAssertThatFunction
parameter_list|)
block|{
comment|// The ListSubjectFactory always returns ListSubjects. -> Casting is appropriate.
return|return
operator|(
name|ListSubject
argument_list|<
name|S
argument_list|,
name|E
argument_list|>
operator|)
name|assertAbout
argument_list|(
operator|new
name|ListSubjectFactory
argument_list|<>
argument_list|(
name|elementAssertThatFunction
argument_list|)
argument_list|)
operator|.
name|that
argument_list|(
name|list
argument_list|)
return|;
block|}
DECL|method|ListSubject ( FailureMetadata failureMetadata, List<E> list, Function<E, S> elementAssertThatFunction)
specifier|private
name|ListSubject
parameter_list|(
name|FailureMetadata
name|failureMetadata
parameter_list|,
name|List
argument_list|<
name|E
argument_list|>
name|list
parameter_list|,
name|Function
argument_list|<
name|E
argument_list|,
name|S
argument_list|>
name|elementAssertThatFunction
parameter_list|)
block|{
name|super
argument_list|(
name|failureMetadata
argument_list|,
name|list
argument_list|)
expr_stmt|;
name|this
operator|.
name|elementAssertThatFunction
operator|=
name|elementAssertThatFunction
expr_stmt|;
block|}
DECL|method|element (int index)
specifier|public
name|S
name|element
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|index
operator|>=
literal|0
argument_list|,
literal|"index(%s) must be>= 0"
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|isNotNull
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|E
argument_list|>
name|list
init|=
name|getActualList
argument_list|()
decl_stmt|;
if|if
condition|(
name|index
operator|>=
name|list
operator|.
name|size
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"has an element at index "
operator|+
name|index
argument_list|)
expr_stmt|;
block|}
return|return
name|elementAssertThatFunction
operator|.
name|apply
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
DECL|method|onlyElement ()
specifier|public
name|S
name|onlyElement
parameter_list|()
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
name|element
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|lastElement ()
specifier|public
name|S
name|lastElement
parameter_list|()
block|{
name|isNotNull
argument_list|()
expr_stmt|;
name|isNotEmpty
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|E
argument_list|>
name|list
init|=
name|getActualList
argument_list|()
decl_stmt|;
return|return
name|element
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getActualList ()
specifier|private
name|List
argument_list|<
name|E
argument_list|>
name|getActualList
parameter_list|()
block|{
comment|// The constructor only accepts lists. -> Casting is appropriate.
return|return
operator|(
name|List
argument_list|<
name|E
argument_list|>
operator|)
name|actual
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|named (String s, Object... objects)
specifier|public
name|ListSubject
argument_list|<
name|S
argument_list|,
name|E
argument_list|>
name|named
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
modifier|...
name|objects
parameter_list|)
block|{
comment|// This object is returned which is of type ListSubject. -> Casting is appropriate.
return|return
operator|(
name|ListSubject
argument_list|<
name|S
argument_list|,
name|E
argument_list|>
operator|)
name|super
operator|.
name|named
argument_list|(
name|s
argument_list|,
name|objects
argument_list|)
return|;
block|}
DECL|class|ListSubjectFactory
specifier|private
specifier|static
class|class
name|ListSubjectFactory
parameter_list|<
name|S
extends|extends
name|Subject
parameter_list|<
name|S
parameter_list|,
name|T
parameter_list|>
parameter_list|,
name|T
parameter_list|>
implements|implements
name|Subject
operator|.
name|Factory
argument_list|<
name|IterableSubject
argument_list|,
name|Iterable
argument_list|<
name|?
argument_list|>
argument_list|>
block|{
DECL|field|elementAssertThatFunction
specifier|private
name|Function
argument_list|<
name|T
argument_list|,
name|S
argument_list|>
name|elementAssertThatFunction
decl_stmt|;
DECL|method|ListSubjectFactory (Function<T, S> elementAssertThatFunction)
name|ListSubjectFactory
parameter_list|(
name|Function
argument_list|<
name|T
argument_list|,
name|S
argument_list|>
name|elementAssertThatFunction
parameter_list|)
block|{
name|this
operator|.
name|elementAssertThatFunction
operator|=
name|elementAssertThatFunction
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|createSubject (FailureMetadata failureMetadata, Iterable<?> objects)
specifier|public
name|ListSubject
argument_list|<
name|S
argument_list|,
name|T
argument_list|>
name|createSubject
parameter_list|(
name|FailureMetadata
name|failureMetadata
parameter_list|,
name|Iterable
argument_list|<
name|?
argument_list|>
name|objects
parameter_list|)
block|{
comment|// The constructor of ListSubject only accepts lists. -> Casting is appropriate.
return|return
operator|new
name|ListSubject
argument_list|<>
argument_list|(
name|failureMetadata
argument_list|,
operator|(
name|List
argument_list|<
name|T
argument_list|>
operator|)
name|objects
argument_list|,
name|elementAssertThatFunction
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

