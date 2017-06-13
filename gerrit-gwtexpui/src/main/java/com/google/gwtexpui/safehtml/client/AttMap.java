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
DECL|package|com.google.gwtexpui.safehtml.client
package|package
name|com
operator|.
name|google
operator|.
name|gwtexpui
operator|.
name|safehtml
operator|.
name|client
package|;
end_package

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
name|HashMap
import|;
end_import

begin_comment
comment|/** Lightweight map of names/values for element attribute construction. */
end_comment

begin_class
DECL|class|AttMap
class|class
name|AttMap
block|{
DECL|field|ANY
specifier|private
specifier|static
specifier|final
name|Tag
name|ANY
init|=
operator|new
name|AnyTag
argument_list|()
decl_stmt|;
DECL|field|TAGS
specifier|private
specifier|static
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Tag
argument_list|>
name|TAGS
decl_stmt|;
static|static
block|{
specifier|final
name|Tag
name|src
init|=
operator|new
name|SrcTag
argument_list|()
decl_stmt|;
name|TAGS
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|TAGS
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
operator|new
name|AnchorTag
argument_list|()
argument_list|)
expr_stmt|;
name|TAGS
operator|.
name|put
argument_list|(
literal|"form"
argument_list|,
operator|new
name|FormTag
argument_list|()
argument_list|)
expr_stmt|;
name|TAGS
operator|.
name|put
argument_list|(
literal|"img"
argument_list|,
name|src
argument_list|)
expr_stmt|;
name|TAGS
operator|.
name|put
argument_list|(
literal|"script"
argument_list|,
name|src
argument_list|)
expr_stmt|;
name|TAGS
operator|.
name|put
argument_list|(
literal|"frame"
argument_list|,
name|src
argument_list|)
expr_stmt|;
block|}
DECL|field|names
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|tag
specifier|private
name|Tag
name|tag
init|=
name|ANY
decl_stmt|;
DECL|field|live
specifier|private
name|int
name|live
decl_stmt|;
DECL|method|reset (String tagName)
name|void
name|reset
parameter_list|(
name|String
name|tagName
parameter_list|)
block|{
name|tag
operator|=
name|TAGS
operator|.
name|get
argument_list|(
name|tagName
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|tag
operator|==
literal|null
condition|)
block|{
name|tag
operator|=
name|ANY
expr_stmt|;
block|}
name|live
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|onto (Buffer raw, SafeHtmlBuilder esc)
name|void
name|onto
parameter_list|(
name|Buffer
name|raw
parameter_list|,
name|SafeHtmlBuilder
name|esc
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|live
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|v
init|=
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|raw
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|raw
operator|.
name|append
argument_list|(
name|names
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|raw
operator|.
name|append
argument_list|(
literal|"=\""
argument_list|)
expr_stmt|;
name|esc
operator|.
name|append
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|raw
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|get (String name)
name|String
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|name
operator|=
name|name
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|live
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|names
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
block|}
return|return
literal|""
return|;
block|}
DECL|method|set (String name, String value)
name|void
name|set
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|name
operator|=
name|name
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
name|tag
operator|.
name|assertSafe
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|live
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|names
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|values
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
specifier|final
name|int
name|i
init|=
name|live
operator|++
decl_stmt|;
if|if
condition|(
name|names
operator|.
name|size
argument_list|()
operator|<
name|live
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|names
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|values
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertNotJavascriptUrl (String value)
specifier|private
specifier|static
name|void
name|assertNotJavascriptUrl
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
block|{
comment|// common in GWT, and safe, so bypass further checks
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|trim
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"javascript:"
argument_list|)
condition|)
block|{
comment|// possibly unsafe, we could have random user code here
comment|// we can't tell if its safe or not so we refuse to accept
comment|//
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"javascript unsafe in href: "
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
DECL|interface|Tag
specifier|private
interface|interface
name|Tag
block|{
DECL|method|assertSafe (String name, String value)
name|void
name|assertSafe
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
function_decl|;
block|}
DECL|class|AnyTag
specifier|private
specifier|static
class|class
name|AnyTag
implements|implements
name|Tag
block|{
annotation|@
name|Override
DECL|method|assertSafe (String name, String value)
specifier|public
name|void
name|assertSafe
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{}
block|}
DECL|class|AnchorTag
specifier|private
specifier|static
class|class
name|AnchorTag
implements|implements
name|Tag
block|{
annotation|@
name|Override
DECL|method|assertSafe (String name, String value)
specifier|public
name|void
name|assertSafe
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
literal|"href"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|assertNotJavascriptUrl
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|FormTag
specifier|private
specifier|static
class|class
name|FormTag
implements|implements
name|Tag
block|{
annotation|@
name|Override
DECL|method|assertSafe (String name, String value)
specifier|public
name|void
name|assertSafe
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
literal|"action"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|assertNotJavascriptUrl
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|SrcTag
specifier|private
specifier|static
class|class
name|SrcTag
implements|implements
name|Tag
block|{
annotation|@
name|Override
DECL|method|assertSafe (String name, String value)
specifier|public
name|void
name|assertSafe
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
literal|"src"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|assertNotJavascriptUrl
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

