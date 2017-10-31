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
DECL|package|com.google.gerrit.server.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|project
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|api
operator|.
name|projects
operator|.
name|CommentLinkInfo
import|;
end_import

begin_comment
comment|/** Info about a single commentlink section in a config. */
end_comment

begin_class
DECL|class|CommentLinkInfoImpl
specifier|public
class|class
name|CommentLinkInfoImpl
extends|extends
name|CommentLinkInfo
block|{
DECL|class|Enabled
specifier|public
specifier|static
class|class
name|Enabled
extends|extends
name|CommentLinkInfoImpl
block|{
DECL|method|Enabled (String name)
specifier|public
name|Enabled
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isOverrideOnly ()
name|boolean
name|isOverrideOnly
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|class|Disabled
specifier|public
specifier|static
class|class
name|Disabled
extends|extends
name|CommentLinkInfoImpl
block|{
DECL|method|Disabled (String name)
specifier|public
name|Disabled
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isOverrideOnly ()
name|boolean
name|isOverrideOnly
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|method|CommentLinkInfoImpl (String name, String match, String link, String html, Boolean enabled)
specifier|public
name|CommentLinkInfoImpl
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|match
parameter_list|,
name|String
name|link
parameter_list|,
name|String
name|html
parameter_list|,
name|Boolean
name|enabled
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|name
operator|!=
literal|null
argument_list|,
literal|"invalid commentlink.name"
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|match
argument_list|)
argument_list|,
literal|"invalid commentlink.%s.match"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|link
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|link
argument_list|)
expr_stmt|;
name|html
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
operator|(
name|link
operator|!=
literal|null
operator|&&
name|html
operator|==
literal|null
operator|)
operator|||
operator|(
name|link
operator|==
literal|null
operator|&&
name|html
operator|!=
literal|null
operator|)
argument_list|,
literal|"commentlink.%s must have either link or html"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|match
operator|=
name|match
expr_stmt|;
name|this
operator|.
name|link
operator|=
name|link
expr_stmt|;
name|this
operator|.
name|html
operator|=
name|html
expr_stmt|;
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
DECL|method|CommentLinkInfoImpl (CommentLinkInfo src, boolean enabled)
specifier|private
name|CommentLinkInfoImpl
parameter_list|(
name|CommentLinkInfo
name|src
parameter_list|,
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|src
operator|.
name|name
expr_stmt|;
name|this
operator|.
name|match
operator|=
name|src
operator|.
name|match
expr_stmt|;
name|this
operator|.
name|link
operator|=
name|src
operator|.
name|link
expr_stmt|;
name|this
operator|.
name|html
operator|=
name|src
operator|.
name|html
expr_stmt|;
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
DECL|method|CommentLinkInfoImpl (String name, boolean enabled)
specifier|private
name|CommentLinkInfoImpl
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|match
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|link
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|html
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
DECL|method|isOverrideOnly ()
name|boolean
name|isOverrideOnly
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|inherit (CommentLinkInfo src)
name|CommentLinkInfo
name|inherit
parameter_list|(
name|CommentLinkInfo
name|src
parameter_list|)
block|{
return|return
operator|new
name|CommentLinkInfoImpl
argument_list|(
name|src
argument_list|,
name|enabled
argument_list|)
return|;
block|}
block|}
end_class

end_unit

