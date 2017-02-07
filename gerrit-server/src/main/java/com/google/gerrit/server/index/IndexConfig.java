begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.index
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|index
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
name|auto
operator|.
name|value
operator|.
name|AutoValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Config
import|;
end_import

begin_comment
comment|/**  * Implementation-specific configuration for secondary indexes.  *  *<p>Contains configuration that is tied to a specific index implementation but is otherwise  * global, i.e. not tied to a specific {@link Index} and schema version.  */
end_comment

begin_class
annotation|@
name|AutoValue
DECL|class|IndexConfig
specifier|public
specifier|abstract
class|class
name|IndexConfig
block|{
DECL|field|DEFAULT_MAX_TERMS
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_TERMS
init|=
literal|1024
decl_stmt|;
DECL|method|createDefault ()
specifier|public
specifier|static
name|IndexConfig
name|createDefault
parameter_list|()
block|{
return|return
name|create
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|DEFAULT_MAX_TERMS
argument_list|)
return|;
block|}
DECL|method|fromConfig (Config cfg)
specifier|public
specifier|static
name|IndexConfig
name|fromConfig
parameter_list|(
name|Config
name|cfg
parameter_list|)
block|{
return|return
name|create
argument_list|(
name|cfg
operator|.
name|getInt
argument_list|(
literal|"index"
argument_list|,
literal|null
argument_list|,
literal|"maxLimit"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|cfg
operator|.
name|getInt
argument_list|(
literal|"index"
argument_list|,
literal|null
argument_list|,
literal|"maxPages"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|cfg
operator|.
name|getInt
argument_list|(
literal|"index"
argument_list|,
literal|null
argument_list|,
literal|"maxTerms"
argument_list|,
literal|0
argument_list|)
argument_list|)
return|;
block|}
DECL|method|create (int maxLimit, int maxPages, int maxTerms)
specifier|public
specifier|static
name|IndexConfig
name|create
parameter_list|(
name|int
name|maxLimit
parameter_list|,
name|int
name|maxPages
parameter_list|,
name|int
name|maxTerms
parameter_list|)
block|{
return|return
operator|new
name|AutoValue_IndexConfig
argument_list|(
name|checkLimit
argument_list|(
name|maxLimit
argument_list|,
literal|"maxLimit"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
name|checkLimit
argument_list|(
name|maxPages
argument_list|,
literal|"maxPages"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
name|checkLimit
argument_list|(
name|maxTerms
argument_list|,
literal|"maxTerms"
argument_list|,
name|DEFAULT_MAX_TERMS
argument_list|)
argument_list|)
return|;
block|}
DECL|method|checkLimit (int limit, String name, int defaultValue)
specifier|private
specifier|static
name|int
name|checkLimit
parameter_list|(
name|int
name|limit
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|limit
operator|==
literal|0
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
name|checkArgument
argument_list|(
name|limit
operator|>
literal|0
argument_list|,
literal|"%s must be positive: %s"
argument_list|,
name|name
argument_list|,
name|limit
argument_list|)
expr_stmt|;
return|return
name|limit
return|;
block|}
comment|/**    * @return maximum limit supported by the underlying index, or limited for performance reasons.    */
DECL|method|maxLimit ()
specifier|public
specifier|abstract
name|int
name|maxLimit
parameter_list|()
function_decl|;
comment|/**    * @return maximum number of pages (limit / start) supported by the underlying index, or limited    *     for performance reasons.    */
DECL|method|maxPages ()
specifier|public
specifier|abstract
name|int
name|maxPages
parameter_list|()
function_decl|;
comment|/**    * @return maximum number of total index query terms supported by the underlying index, or limited    *     for performance reasons.    */
DECL|method|maxTerms ()
specifier|public
specifier|abstract
name|int
name|maxTerms
parameter_list|()
function_decl|;
block|}
end_class

end_unit

