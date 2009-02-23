begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2008 Google Inc.
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
DECL|package|com.google.gerrit.pgm
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
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
name|client
operator|.
name|reviewdb
operator|.
name|Account
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
name|client
operator|.
name|reviewdb
operator|.
name|ContactInformation
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
name|client
operator|.
name|reviewdb
operator|.
name|ReviewDb
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
name|client
operator|.
name|rpc
operator|.
name|Common
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
name|client
operator|.
name|rpc
operator|.
name|ContactInformationStoreException
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
name|git
operator|.
name|WorkQueue
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
name|EncryptedContactStore
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
name|GerritServer
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
name|server
operator|.
name|XsrfException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|client
operator|.
name|OrmException
import|;
end_import

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
name|JdbcSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|ProgressMonitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|TextProgressMonitor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
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
name|sql
operator|.
name|Statement
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

begin_comment
comment|/** Export old contact columns to the encrypted contact store. */
end_comment

begin_class
DECL|class|EncryptContactInfo
specifier|public
class|class
name|EncryptContactInfo
block|{
DECL|method|main (final String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
specifier|final
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|OrmException
throws|,
name|XsrfException
throws|,
name|ContactInformationStoreException
throws|,
name|SQLException
block|{
try|try
block|{
name|mainImpl
argument_list|(
name|argv
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|WorkQueue
operator|.
name|terminate
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|mainImpl (final String[] argv)
specifier|private
specifier|static
name|void
name|mainImpl
parameter_list|(
specifier|final
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|OrmException
throws|,
name|XsrfException
throws|,
name|ContactInformationStoreException
throws|,
name|SQLException
block|{
specifier|final
name|ProgressMonitor
name|pm
init|=
operator|new
name|TextProgressMonitor
argument_list|()
decl_stmt|;
name|GerritServer
operator|.
name|getInstance
argument_list|()
expr_stmt|;
specifier|final
name|ReviewDb
name|db
init|=
name|Common
operator|.
name|getSchemaFactory
argument_list|()
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
name|pm
operator|.
name|start
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|pm
operator|.
name|beginTask
argument_list|(
literal|"Enumerate accounts"
argument_list|,
name|ProgressMonitor
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
specifier|final
name|Connection
name|sql
init|=
operator|(
operator|(
name|JdbcSchema
operator|)
name|db
operator|)
operator|.
name|getConnection
argument_list|()
decl_stmt|;
specifier|final
name|Statement
name|stmt
init|=
name|sql
operator|.
name|createStatement
argument_list|()
decl_stmt|;
specifier|final
name|ResultSet
name|rs
init|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"SELECT"
operator|+
literal|" account_id"
operator|+
literal|",contact_address"
operator|+
literal|",contact_country"
operator|+
literal|",contact_phone_nbr"
operator|+
literal|",contact_fax_nbr"
operator|+
literal|" FROM accounts WHERE contact_filed_on IS NOT NULL"
operator|+
literal|" ORDER BY account_id"
argument_list|)
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ToDo
argument_list|>
name|todo
init|=
operator|new
name|ArrayList
argument_list|<
name|ToDo
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
specifier|final
name|ToDo
name|d
init|=
operator|new
name|ToDo
argument_list|()
decl_stmt|;
name|d
operator|.
name|id
operator|=
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|rs
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|info
operator|.
name|setAddress
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|info
operator|.
name|setCountry
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|info
operator|.
name|setPhoneNumber
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|info
operator|.
name|setFaxNumber
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|todo
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|pm
operator|.
name|update
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|pm
operator|.
name|endTask
argument_list|()
expr_stmt|;
name|pm
operator|.
name|start
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|pm
operator|.
name|beginTask
argument_list|(
literal|"Store contact"
argument_list|,
name|todo
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|ToDo
name|d
range|:
name|todo
control|)
block|{
specifier|final
name|Account
name|them
init|=
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|get
argument_list|(
name|d
operator|.
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|them
operator|.
name|isContactFiled
argument_list|()
operator|&&
name|ContactInformation
operator|.
name|hasData
argument_list|(
name|d
operator|.
name|info
argument_list|)
condition|)
block|{
name|EncryptedContactStore
operator|.
name|store
argument_list|(
name|them
argument_list|,
name|d
operator|.
name|info
argument_list|)
expr_stmt|;
block|}
name|pm
operator|.
name|update
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|pm
operator|.
name|endTask
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ToDo
specifier|static
class|class
name|ToDo
block|{
DECL|field|id
name|Account
operator|.
name|Id
name|id
decl_stmt|;
DECL|field|info
specifier|final
name|ContactInformation
name|info
init|=
operator|new
name|ContactInformation
argument_list|()
decl_stmt|;
block|}
block|}
end_class

end_unit

