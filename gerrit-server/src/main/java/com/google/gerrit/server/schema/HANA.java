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
DECL|package|com.google.gerrit.server.schema
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|schema
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
name|server
operator|.
name|schema
operator|.
name|JdbcUtil
operator|.
name|hostname
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|schema
operator|.
name|JdbcUtil
operator|.
name|port
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|server
operator|.
name|config
operator|.
name|ConfigSection
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
name|GerritServerConfig
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

begin_class
DECL|class|HANA
class|class
name|HANA
extends|extends
name|BaseDataSourceType
block|{
DECL|field|cfg
specifier|private
name|Config
name|cfg
decl_stmt|;
annotation|@
name|Inject
DECL|method|HANA (@erritServerConfig Config cfg)
name|HANA
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|super
argument_list|(
literal|"com.sap.db.jdbc.Driver"
argument_list|)
expr_stmt|;
name|this
operator|.
name|cfg
operator|=
name|cfg
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUrl ()
specifier|public
name|String
name|getUrl
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|ConfigSection
name|dbs
init|=
operator|new
name|ConfigSection
argument_list|(
name|cfg
argument_list|,
literal|"database"
argument_list|)
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"jdbc:sap://"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|hostname
argument_list|(
name|dbs
operator|.
name|required
argument_list|(
literal|"hostname"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|port
argument_list|(
name|dbs
operator|.
name|optional
argument_list|(
literal|"port"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|database
init|=
name|dbs
operator|.
name|optional
argument_list|(
literal|"database"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|database
argument_list|)
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"?databaseName="
argument_list|)
operator|.
name|append
argument_list|(
name|database
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getIndexScript ()
specifier|public
name|ScriptRunner
name|getIndexScript
parameter_list|()
throws|throws
name|IOException
block|{
comment|// HANA uses column tables and should not require additional indices
return|return
name|ScriptRunner
operator|.
name|NOOP
return|;
block|}
block|}
end_class

end_unit

