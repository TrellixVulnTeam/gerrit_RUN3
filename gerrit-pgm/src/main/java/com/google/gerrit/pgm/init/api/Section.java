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
DECL|package|com.google.gerrit.pgm.init.api
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
operator|.
name|api
package|;
end_package

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
name|server
operator|.
name|config
operator|.
name|SitePaths
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
name|Inject
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
name|assistedinject
operator|.
name|Assisted
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/** Helper to edit a section of the configuration files. */
end_comment

begin_class
DECL|class|Section
specifier|public
class|class
name|Section
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|get (@ssistedR) String section, @Assisted(R) String subsection)
name|Section
name|get
parameter_list|(
annotation|@
name|Assisted
argument_list|(
literal|"section"
argument_list|)
name|String
name|section
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"subsection"
argument_list|)
name|String
name|subsection
parameter_list|)
function_decl|;
block|}
DECL|field|flags
specifier|private
specifier|final
name|InitFlags
name|flags
decl_stmt|;
DECL|field|site
specifier|private
specifier|final
name|SitePaths
name|site
decl_stmt|;
DECL|field|ui
specifier|private
specifier|final
name|ConsoleUI
name|ui
decl_stmt|;
DECL|field|section
specifier|private
specifier|final
name|String
name|section
decl_stmt|;
DECL|field|subsection
specifier|private
specifier|final
name|String
name|subsection
decl_stmt|;
annotation|@
name|Inject
DECL|method|Section (final InitFlags flags, final SitePaths site, final ConsoleUI ui, @Assisted(R) final String section, @Assisted(R) @Nullable final String subsection)
specifier|public
name|Section
parameter_list|(
specifier|final
name|InitFlags
name|flags
parameter_list|,
specifier|final
name|SitePaths
name|site
parameter_list|,
specifier|final
name|ConsoleUI
name|ui
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"section"
argument_list|)
specifier|final
name|String
name|section
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"subsection"
argument_list|)
annotation|@
name|Nullable
specifier|final
name|String
name|subsection
parameter_list|)
block|{
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
name|this
operator|.
name|site
operator|=
name|site
expr_stmt|;
name|this
operator|.
name|ui
operator|=
name|ui
expr_stmt|;
name|this
operator|.
name|section
operator|=
name|section
expr_stmt|;
name|this
operator|.
name|subsection
operator|=
name|subsection
expr_stmt|;
block|}
DECL|method|get (String name)
specifier|public
name|String
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|flags
operator|.
name|cfg
operator|.
name|getString
argument_list|(
name|section
argument_list|,
name|subsection
argument_list|,
name|name
argument_list|)
return|;
block|}
DECL|method|set (final String name, final String value)
specifier|public
name|void
name|set
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
block|{
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|all
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|all
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|flags
operator|.
name|cfg
operator|.
name|getStringList
argument_list|(
name|section
argument_list|,
name|subsection
argument_list|,
name|name
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|all
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|||
name|all
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|flags
operator|.
name|cfg
operator|.
name|setString
argument_list|(
name|section
argument_list|,
name|subsection
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|all
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|flags
operator|.
name|cfg
operator|.
name|setStringList
argument_list|(
name|section
argument_list|,
name|subsection
argument_list|,
name|name
argument_list|,
name|all
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|all
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{     }
elseif|else
if|if
condition|(
name|all
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|flags
operator|.
name|cfg
operator|.
name|unset
argument_list|(
name|section
argument_list|,
name|subsection
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|all
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|flags
operator|.
name|cfg
operator|.
name|setStringList
argument_list|(
name|section
argument_list|,
name|subsection
argument_list|,
name|name
argument_list|,
name|all
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|set (final String name, final T value)
specifier|public
parameter_list|<
name|T
extends|extends
name|Enum
argument_list|<
name|?
argument_list|>
parameter_list|>
name|void
name|set
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|T
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|set
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|unset
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|unset (String name)
specifier|public
name|void
name|unset
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|set
argument_list|(
name|name
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|string (final String title, final String name, final String dv)
specifier|public
name|String
name|string
parameter_list|(
specifier|final
name|String
name|title
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|dv
parameter_list|)
block|{
return|return
name|string
argument_list|(
name|title
argument_list|,
name|name
argument_list|,
name|dv
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|string (final String title, final String name, final String dv, final boolean nullIfDefault)
specifier|public
name|String
name|string
parameter_list|(
specifier|final
name|String
name|title
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|dv
parameter_list|,
specifier|final
name|boolean
name|nullIfDefault
parameter_list|)
block|{
specifier|final
name|String
name|ov
init|=
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|nv
init|=
name|ui
operator|.
name|readString
argument_list|(
name|ov
operator|!=
literal|null
condition|?
name|ov
else|:
name|dv
argument_list|,
literal|"%s"
argument_list|,
name|title
argument_list|)
decl_stmt|;
if|if
condition|(
name|nullIfDefault
operator|&&
name|nv
operator|.
name|equals
argument_list|(
name|dv
argument_list|)
condition|)
block|{
name|nv
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|eq
argument_list|(
name|ov
argument_list|,
name|nv
argument_list|)
condition|)
block|{
name|set
argument_list|(
name|name
argument_list|,
name|nv
argument_list|)
expr_stmt|;
block|}
return|return
name|nv
return|;
block|}
DECL|method|path (final String title, final String name, final String defValue)
specifier|public
name|File
name|path
parameter_list|(
specifier|final
name|String
name|title
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|defValue
parameter_list|)
block|{
return|return
name|site
operator|.
name|resolve
argument_list|(
name|string
argument_list|(
name|title
argument_list|,
name|name
argument_list|,
name|defValue
argument_list|)
argument_list|)
return|;
block|}
DECL|method|select (final String title, final String name, final T defValue)
specifier|public
parameter_list|<
name|T
extends|extends
name|Enum
argument_list|<
name|?
argument_list|>
parameter_list|>
name|T
name|select
parameter_list|(
specifier|final
name|String
name|title
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|T
name|defValue
parameter_list|)
block|{
return|return
name|select
argument_list|(
name|title
argument_list|,
name|name
argument_list|,
name|defValue
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|select (final String title, final String name, final T defValue, final boolean nullIfDefault)
specifier|public
parameter_list|<
name|T
extends|extends
name|Enum
argument_list|<
name|?
argument_list|>
parameter_list|>
name|T
name|select
parameter_list|(
specifier|final
name|String
name|title
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|T
name|defValue
parameter_list|,
specifier|final
name|boolean
name|nullIfDefault
parameter_list|)
block|{
specifier|final
name|boolean
name|set
init|=
name|get
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
decl_stmt|;
name|T
name|oldValue
init|=
name|flags
operator|.
name|cfg
operator|.
name|getEnum
argument_list|(
name|section
argument_list|,
name|subsection
argument_list|,
name|name
argument_list|,
name|defValue
argument_list|)
decl_stmt|;
name|T
name|newValue
init|=
name|ui
operator|.
name|readEnum
argument_list|(
name|oldValue
argument_list|,
literal|"%s"
argument_list|,
name|title
argument_list|)
decl_stmt|;
if|if
condition|(
name|nullIfDefault
operator|&&
name|newValue
operator|==
name|defValue
condition|)
block|{
name|newValue
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|set
operator|||
name|oldValue
operator|!=
name|newValue
condition|)
block|{
if|if
condition|(
name|newValue
operator|!=
literal|null
condition|)
block|{
name|set
argument_list|(
name|name
argument_list|,
name|newValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|unset
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|newValue
return|;
block|}
DECL|method|select (final String title, final String name, final String dv, Set<String> allowedValues)
specifier|public
name|String
name|select
parameter_list|(
specifier|final
name|String
name|title
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|dv
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|allowedValues
parameter_list|)
block|{
specifier|final
name|String
name|ov
init|=
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|nv
init|=
name|ui
operator|.
name|readString
argument_list|(
name|ov
operator|!=
literal|null
condition|?
name|ov
else|:
name|dv
argument_list|,
name|allowedValues
argument_list|,
literal|"%s"
argument_list|,
name|title
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|eq
argument_list|(
name|ov
argument_list|,
name|nv
argument_list|)
condition|)
block|{
name|set
argument_list|(
name|name
argument_list|,
name|nv
argument_list|)
expr_stmt|;
block|}
return|return
name|nv
return|;
block|}
DECL|method|password (final String username, final String password)
specifier|public
name|String
name|password
parameter_list|(
specifier|final
name|String
name|username
parameter_list|,
specifier|final
name|String
name|password
parameter_list|)
block|{
specifier|final
name|String
name|ov
init|=
name|getSecure
argument_list|(
name|password
argument_list|)
decl_stmt|;
name|String
name|user
init|=
name|flags
operator|.
name|sec
operator|.
name|getString
argument_list|(
name|section
argument_list|,
name|subsection
argument_list|,
name|username
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
name|user
operator|=
name|get
argument_list|(
name|username
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
name|flags
operator|.
name|sec
operator|.
name|unset
argument_list|(
name|section
argument_list|,
name|subsection
argument_list|,
name|password
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|ov
operator|!=
literal|null
condition|)
block|{
comment|// If the user already has a password stored, try to reuse it
comment|// rather than prompting for a whole new one.
comment|//
if|if
condition|(
name|ui
operator|.
name|isBatch
argument_list|()
operator|||
operator|!
name|ui
operator|.
name|yesno
argument_list|(
literal|false
argument_list|,
literal|"Change %s's password"
argument_list|,
name|user
argument_list|)
condition|)
block|{
return|return
name|ov
return|;
block|}
block|}
specifier|final
name|String
name|nv
init|=
name|ui
operator|.
name|password
argument_list|(
literal|"%s's password"
argument_list|,
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|eq
argument_list|(
name|ov
argument_list|,
name|nv
argument_list|)
condition|)
block|{
name|setSecure
argument_list|(
name|password
argument_list|,
name|nv
argument_list|)
expr_stmt|;
block|}
return|return
name|nv
return|;
block|}
DECL|method|passwordForKey (String key, String password)
specifier|public
name|String
name|passwordForKey
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|String
name|ov
init|=
name|getSecure
argument_list|(
name|password
argument_list|)
decl_stmt|;
if|if
condition|(
name|ov
operator|!=
literal|null
condition|)
block|{
comment|// If the password is already stored, try to reuse it
comment|// rather than prompting for a whole new one.
comment|//
if|if
condition|(
name|ui
operator|.
name|isBatch
argument_list|()
operator|||
operator|!
name|ui
operator|.
name|yesno
argument_list|(
literal|false
argument_list|,
literal|"Change %s"
argument_list|,
name|key
argument_list|)
condition|)
block|{
return|return
name|ov
return|;
block|}
block|}
specifier|final
name|String
name|nv
init|=
name|ui
operator|.
name|password
argument_list|(
literal|"%s"
argument_list|,
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|eq
argument_list|(
name|ov
argument_list|,
name|nv
argument_list|)
condition|)
block|{
name|setSecure
argument_list|(
name|password
argument_list|,
name|nv
argument_list|)
expr_stmt|;
block|}
return|return
name|nv
return|;
block|}
DECL|method|getSecure (String name)
specifier|public
name|String
name|getSecure
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|flags
operator|.
name|sec
operator|.
name|getString
argument_list|(
name|section
argument_list|,
name|subsection
argument_list|,
name|name
argument_list|)
return|;
block|}
DECL|method|setSecure (String name, String value)
specifier|public
name|void
name|setSecure
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
name|value
operator|!=
literal|null
condition|)
block|{
name|flags
operator|.
name|sec
operator|.
name|setString
argument_list|(
name|section
argument_list|,
name|subsection
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|flags
operator|.
name|sec
operator|.
name|unset
argument_list|(
name|section
argument_list|,
name|subsection
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getName ()
name|String
name|getName
parameter_list|()
block|{
return|return
name|section
return|;
block|}
DECL|method|eq (final String a, final String b)
specifier|private
specifier|static
name|boolean
name|eq
parameter_list|(
specifier|final
name|String
name|a
parameter_list|,
specifier|final
name|String
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
literal|null
operator|&&
name|b
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|a
operator|!=
literal|null
operator|&&
name|a
operator|.
name|equals
argument_list|(
name|b
argument_list|)
return|;
block|}
block|}
end_class

end_unit

