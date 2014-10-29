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
DECL|package|com.google.gerrit.extensions.restapi
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|restapi
package|;
end_package

begin_comment
comment|/** Special return value to mean specific HTTP status codes in a REST API. */
end_comment

begin_class
DECL|class|Response
specifier|public
specifier|abstract
class|class
name|Response
parameter_list|<
name|T
parameter_list|>
block|{
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|}
argument_list|)
DECL|field|NONE
specifier|private
specifier|static
specifier|final
name|Response
name|NONE
init|=
operator|new
name|None
argument_list|()
decl_stmt|;
comment|/** HTTP 200 OK: pointless wrapper for type safety. */
DECL|method|ok (T value)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Response
argument_list|<
name|T
argument_list|>
name|ok
parameter_list|(
name|T
name|value
parameter_list|)
block|{
return|return
operator|new
name|Impl
argument_list|<>
argument_list|(
literal|200
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/** HTTP 201 Created: typically used when a new resource is made. */
DECL|method|created (T value)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Response
argument_list|<
name|T
argument_list|>
name|created
parameter_list|(
name|T
name|value
parameter_list|)
block|{
return|return
operator|new
name|Impl
argument_list|<>
argument_list|(
literal|201
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/** HTTP 204 No Content: typically used when the resource is deleted. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|none ()
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Response
argument_list|<
name|T
argument_list|>
name|none
parameter_list|()
block|{
return|return
name|NONE
return|;
block|}
comment|/** HTTP 302 Found: temporary redirect to another URL. */
DECL|method|redirect (String location)
specifier|public
specifier|static
name|Redirect
name|redirect
parameter_list|(
name|String
name|location
parameter_list|)
block|{
return|return
operator|new
name|Redirect
argument_list|(
name|location
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|unwrap (T obj)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|T
name|obj
parameter_list|)
block|{
while|while
condition|(
name|obj
operator|instanceof
name|Response
condition|)
block|{
name|obj
operator|=
call|(
name|T
call|)
argument_list|(
operator|(
name|Response
operator|)
name|obj
argument_list|)
operator|.
name|value
argument_list|()
expr_stmt|;
block|}
return|return
name|obj
return|;
block|}
DECL|method|statusCode ()
specifier|public
specifier|abstract
name|int
name|statusCode
parameter_list|()
function_decl|;
DECL|method|value ()
specifier|public
specifier|abstract
name|T
name|value
parameter_list|()
function_decl|;
DECL|method|caching ()
specifier|public
specifier|abstract
name|CacheControl
name|caching
parameter_list|()
function_decl|;
DECL|method|caching (CacheControl c)
specifier|public
specifier|abstract
name|Response
argument_list|<
name|T
argument_list|>
name|caching
parameter_list|(
name|CacheControl
name|c
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
specifier|abstract
name|String
name|toString
parameter_list|()
function_decl|;
DECL|class|Impl
specifier|private
specifier|static
specifier|final
class|class
name|Impl
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Response
argument_list|<
name|T
argument_list|>
block|{
DECL|field|statusCode
specifier|private
specifier|final
name|int
name|statusCode
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|T
name|value
decl_stmt|;
DECL|field|caching
specifier|private
name|CacheControl
name|caching
init|=
name|CacheControl
operator|.
name|NONE
decl_stmt|;
DECL|method|Impl (int sc, T val)
specifier|private
name|Impl
parameter_list|(
name|int
name|sc
parameter_list|,
name|T
name|val
parameter_list|)
block|{
name|statusCode
operator|=
name|sc
expr_stmt|;
name|value
operator|=
name|val
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|statusCode ()
specifier|public
name|int
name|statusCode
parameter_list|()
block|{
return|return
name|statusCode
return|;
block|}
annotation|@
name|Override
DECL|method|value ()
specifier|public
name|T
name|value
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|caching ()
specifier|public
name|CacheControl
name|caching
parameter_list|()
block|{
return|return
name|caching
return|;
block|}
annotation|@
name|Override
DECL|method|caching (CacheControl c)
specifier|public
name|Response
argument_list|<
name|T
argument_list|>
name|caching
parameter_list|(
name|CacheControl
name|c
parameter_list|)
block|{
name|caching
operator|=
name|c
expr_stmt|;
return|return
name|this
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
literal|"["
operator|+
name|statusCode
argument_list|()
operator|+
literal|"] "
operator|+
name|value
argument_list|()
return|;
block|}
block|}
DECL|class|None
specifier|private
specifier|static
specifier|final
class|class
name|None
extends|extends
name|Response
argument_list|<
name|Object
argument_list|>
block|{
DECL|method|None ()
specifier|private
name|None
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|statusCode ()
specifier|public
name|int
name|statusCode
parameter_list|()
block|{
return|return
literal|204
return|;
block|}
annotation|@
name|Override
DECL|method|value ()
specifier|public
name|Object
name|value
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|caching ()
specifier|public
name|CacheControl
name|caching
parameter_list|()
block|{
return|return
name|CacheControl
operator|.
name|NONE
return|;
block|}
annotation|@
name|Override
DECL|method|caching (CacheControl c)
specifier|public
name|Response
argument_list|<
name|Object
argument_list|>
name|caching
parameter_list|(
name|CacheControl
name|c
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
literal|"[204 No Content] None"
return|;
block|}
block|}
comment|/** An HTTP redirect to another location. */
DECL|class|Redirect
specifier|public
specifier|static
specifier|final
class|class
name|Redirect
block|{
DECL|field|location
specifier|private
specifier|final
name|String
name|location
decl_stmt|;
DECL|method|Redirect (String url)
specifier|private
name|Redirect
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|this
operator|.
name|location
operator|=
name|url
expr_stmt|;
block|}
DECL|method|location ()
specifier|public
name|String
name|location
parameter_list|()
block|{
return|return
name|location
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
name|location
operator|.
name|hashCode
argument_list|()
return|;
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
return|return
name|o
operator|instanceof
name|Redirect
operator|&&
operator|(
operator|(
name|Redirect
operator|)
name|o
operator|)
operator|.
name|location
operator|.
name|equals
argument_list|(
name|location
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
name|String
operator|.
name|format
argument_list|(
literal|"[302 Redirect] %s"
argument_list|,
name|location
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

