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
DECL|package|com.google.gerrit.pgm.init
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
package|;
end_package

begin_import
import|import static
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
operator|.
name|InitUtil
operator|.
name|die
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
name|common
operator|.
name|FileUtil
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
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|Section
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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_class
DECL|class|DerbyInitializer
class|class
name|DerbyInitializer
implements|implements
name|DatabaseConfigInitializer
block|{
DECL|field|site
specifier|private
specifier|final
name|SitePaths
name|site
decl_stmt|;
annotation|@
name|Inject
DECL|method|DerbyInitializer (SitePaths site)
name|DerbyInitializer
parameter_list|(
name|SitePaths
name|site
parameter_list|)
block|{
name|this
operator|.
name|site
operator|=
name|site
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initConfig (Section databaseSection)
specifier|public
name|void
name|initConfig
parameter_list|(
name|Section
name|databaseSection
parameter_list|)
block|{
name|String
name|path
init|=
name|databaseSection
operator|.
name|get
argument_list|(
literal|"database"
argument_list|)
decl_stmt|;
name|Path
name|db
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|db
operator|=
name|site
operator|.
name|resolve
argument_list|(
literal|"db"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"ReviewDB"
argument_list|)
expr_stmt|;
name|databaseSection
operator|.
name|set
argument_list|(
literal|"database"
argument_list|,
name|db
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|db
operator|=
name|site
operator|.
name|resolve
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|db
operator|==
literal|null
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"database.database must be supplied for Derby"
argument_list|)
throw|;
block|}
name|db
operator|=
name|db
operator|.
name|getParent
argument_list|()
expr_stmt|;
name|FileUtil
operator|.
name|mkdirsOrDie
argument_list|(
name|db
argument_list|,
literal|"cannot create database.database"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

