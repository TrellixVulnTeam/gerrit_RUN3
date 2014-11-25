begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|config
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
name|extensions
operator|.
name|client
operator|.
name|SubmitType
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
name|reviewdb
operator|.
name|client
operator|.
name|Project
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
name|Singleton
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

begin_class
annotation|@
name|Singleton
DECL|class|RepositoryConfig
specifier|public
class|class
name|RepositoryConfig
block|{
DECL|field|SECTION_NAME
specifier|static
specifier|final
name|String
name|SECTION_NAME
init|=
literal|"repository"
decl_stmt|;
DECL|field|OWNER_GROUP_NAME
specifier|static
specifier|final
name|String
name|OWNER_GROUP_NAME
init|=
literal|"ownerGroup"
decl_stmt|;
DECL|field|DEFAULT_SUBMIT_TYPE_NAME
specifier|static
specifier|final
name|String
name|DEFAULT_SUBMIT_TYPE_NAME
init|=
literal|"defaultSubmitType"
decl_stmt|;
DECL|field|cfg
specifier|private
specifier|final
name|Config
name|cfg
decl_stmt|;
annotation|@
name|Inject
DECL|method|RepositoryConfig (@erritServerConfig Config cfg)
specifier|public
name|RepositoryConfig
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|this
operator|.
name|cfg
operator|=
name|cfg
expr_stmt|;
block|}
DECL|method|getDefaultSubmitType (Project.NameKey project)
specifier|public
name|SubmitType
name|getDefaultSubmitType
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
block|{
return|return
name|cfg
operator|.
name|getEnum
argument_list|(
name|SECTION_NAME
argument_list|,
name|findSubSection
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|DEFAULT_SUBMIT_TYPE_NAME
argument_list|,
name|SubmitType
operator|.
name|MERGE_IF_NECESSARY
argument_list|)
return|;
block|}
DECL|method|getOwnerGroups (Project.NameKey project)
specifier|public
name|String
index|[]
name|getOwnerGroups
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
block|{
return|return
name|cfg
operator|.
name|getStringList
argument_list|(
name|SECTION_NAME
argument_list|,
name|findSubSection
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|OWNER_GROUP_NAME
argument_list|)
return|;
block|}
comment|/**    * Find the subSection to get repository configuration from.    *<p>    * SubSection can use the * pattern so if project name matches more than one    * section, return the more precise one. E.g if the following subSections are    * defined:    *    *<pre>    * [repository "somePath/*"]    *   name = value    * [repository "somePath/somePath/*"]    *   name = value    *</pre>    *    * and this method is called with "somePath/somePath/someProject" as project    * name, it will return the subSection "somePath/somePath/*"    *    * @param project Name of the project    * @return the name of the subSection, null if none is found    */
DECL|method|findSubSection (String project)
specifier|private
name|String
name|findSubSection
parameter_list|(
name|String
name|project
parameter_list|)
block|{
name|String
name|subSectionFound
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|subSection
range|:
name|cfg
operator|.
name|getSubsections
argument_list|(
name|SECTION_NAME
argument_list|)
control|)
block|{
if|if
condition|(
name|isMatch
argument_list|(
name|subSection
argument_list|,
name|project
argument_list|)
operator|&&
operator|(
name|subSectionFound
operator|==
literal|null
operator|||
name|subSectionFound
operator|.
name|length
argument_list|()
operator|<
name|subSection
operator|.
name|length
argument_list|()
operator|)
condition|)
block|{
name|subSectionFound
operator|=
name|subSection
expr_stmt|;
block|}
block|}
return|return
name|subSectionFound
return|;
block|}
DECL|method|isMatch (String subSection, String project)
specifier|private
name|boolean
name|isMatch
parameter_list|(
name|String
name|subSection
parameter_list|,
name|String
name|project
parameter_list|)
block|{
return|return
name|project
operator|.
name|equals
argument_list|(
name|subSection
argument_list|)
operator|||
operator|(
name|subSection
operator|.
name|endsWith
argument_list|(
literal|"*"
argument_list|)
operator|&&
name|project
operator|.
name|startsWith
argument_list|(
name|subSection
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|subSection
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
operator|)
return|;
block|}
block|}
end_class

end_unit

