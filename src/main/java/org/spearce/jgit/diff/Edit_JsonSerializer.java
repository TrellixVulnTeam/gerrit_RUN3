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
DECL|package|org.spearce.jgit.diff
package|package
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|diff
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|client
operator|.
name|JavaScriptObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|client
operator|.
name|JsonSerializer
import|;
end_import

begin_class
DECL|class|Edit_JsonSerializer
specifier|public
class|class
name|Edit_JsonSerializer
extends|extends
name|JsonSerializer
argument_list|<
name|Edit
argument_list|>
block|{
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|Edit_JsonSerializer
name|INSTANCE
init|=
operator|new
name|Edit_JsonSerializer
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|fromJson (Object jso)
specifier|public
name|Edit
name|fromJson
parameter_list|(
name|Object
name|jso
parameter_list|)
block|{
if|if
condition|(
name|jso
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|JavaScriptObject
name|o
init|=
operator|(
name|JavaScriptObject
operator|)
name|jso
decl_stmt|;
return|return
operator|new
name|Edit
argument_list|(
name|get
argument_list|(
name|o
argument_list|,
literal|0
argument_list|)
argument_list|,
name|get
argument_list|(
name|o
argument_list|,
literal|1
argument_list|)
argument_list|,
name|get
argument_list|(
name|o
argument_list|,
literal|2
argument_list|)
argument_list|,
name|get
argument_list|(
name|o
argument_list|,
literal|3
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|printJson (final StringBuilder sb, final Edit o)
specifier|public
name|void
name|printJson
parameter_list|(
specifier|final
name|StringBuilder
name|sb
parameter_list|,
specifier|final
name|Edit
name|o
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|o
operator|.
name|getBeginA
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|o
operator|.
name|getEndA
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|o
operator|.
name|getBeginB
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|o
operator|.
name|getEndB
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
DECL|method|get (JavaScriptObject jso, int idx)
specifier|private
specifier|static
specifier|native
name|int
name|get
parameter_list|(
name|JavaScriptObject
name|jso
parameter_list|,
name|int
name|idx
parameter_list|)
comment|/*-{ return jso[idx]; }-*/
function_decl|;
block|}
end_class

end_unit

