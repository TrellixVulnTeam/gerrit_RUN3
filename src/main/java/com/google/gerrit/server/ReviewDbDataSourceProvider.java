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
DECL|package|com.google.gerrit.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|jdbc
operator|.
name|SimpleDataSource
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
name|Provider
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
name|ProvisionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|InitialContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import

begin_comment
comment|/** Provides access to the {@code ReviewDb} DataSource. */
end_comment

begin_class
DECL|class|ReviewDbDataSourceProvider
specifier|final
class|class
name|ReviewDbDataSourceProvider
implements|implements
name|Provider
argument_list|<
name|DataSource
argument_list|>
block|{
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|DataSource
name|get
parameter_list|()
block|{
specifier|final
name|String
name|dsName
init|=
literal|"java:comp/env/jdbc/ReviewDb"
decl_stmt|;
try|try
block|{
return|return
operator|(
name|DataSource
operator|)
operator|new
name|InitialContext
argument_list|()
operator|.
name|lookup
argument_list|(
name|dsName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|namingErr
parameter_list|)
block|{
specifier|final
name|Properties
name|p
init|=
name|readGerritDataSource
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
literal|"Initialization error:\n"
operator|+
literal|"  * No DataSource "
operator|+
name|dsName
operator|+
literal|"\n"
operator|+
literal|"  * No -DGerritServer=GerritServer.properties"
operator|+
literal|" on Java command line"
argument_list|,
name|namingErr
argument_list|)
throw|;
block|}
try|try
block|{
return|return
operator|new
name|SimpleDataSource
argument_list|(
name|p
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|se
parameter_list|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
literal|"Database unavailable"
argument_list|,
name|se
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|readGerritDataSource ()
specifier|private
specifier|static
name|Properties
name|readGerritDataSource
parameter_list|()
throws|throws
name|ProvisionException
block|{
specifier|final
name|Properties
name|srvprop
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"GerritServer"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|name
operator|=
literal|"GerritServer.properties"
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|InputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|srvprop
operator|.
name|load
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
literal|"Cannot read "
operator|+
name|name
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|Properties
name|dbprop
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|srvprop
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|String
name|key
init|=
operator|(
name|String
operator|)
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"database."
argument_list|)
condition|)
block|{
name|dbprop
operator|.
name|put
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|"database."
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|dbprop
return|;
block|}
block|}
end_class

end_unit

