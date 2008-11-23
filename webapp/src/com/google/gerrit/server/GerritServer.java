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
name|reviewdb
operator|.
name|SystemConfig
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
name|SignedToken
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
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
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
name|Collections
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

begin_comment
comment|/** Global server-side state for Gerrit. */
end_comment

begin_class
DECL|class|GerritServer
specifier|public
class|class
name|GerritServer
block|{
DECL|field|impl
specifier|private
specifier|static
name|GerritServer
name|impl
decl_stmt|;
comment|/**    * Obtain the singleton server instance for this web application.    *     * @return the server instance. Never null.    * @throws OrmException the database could not be configured. There is    *         something wrong with the schema configuration in {@link ReviewDb}    *         that must be addressed by a developer.    * @throws XsrfException the XSRF support could not be correctly configured to    *         protect the application against cross-site request forgery. The JVM    *         is most likely lacking critical security algorithms.    */
DECL|method|getInstance ()
specifier|public
specifier|static
specifier|synchronized
name|GerritServer
name|getInstance
parameter_list|()
throws|throws
name|OrmException
throws|,
name|XsrfException
block|{
if|if
condition|(
name|impl
operator|==
literal|null
condition|)
block|{
name|impl
operator|=
operator|new
name|GerritServer
argument_list|()
expr_stmt|;
block|}
return|return
name|impl
return|;
block|}
DECL|field|db
specifier|private
specifier|final
name|Database
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|SystemConfig
name|config
decl_stmt|;
DECL|field|xsrf
specifier|private
specifier|final
name|SignedToken
name|xsrf
decl_stmt|;
DECL|field|account
specifier|private
specifier|final
name|SignedToken
name|account
decl_stmt|;
DECL|method|GerritServer ()
specifier|private
name|GerritServer
parameter_list|()
throws|throws
name|OrmException
throws|,
name|XsrfException
block|{
name|db
operator|=
name|createDatabase
argument_list|()
expr_stmt|;
name|config
operator|=
name|readSystemConfig
argument_list|()
expr_stmt|;
if|if
condition|(
name|config
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"No "
operator|+
name|SystemConfig
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" found"
argument_list|)
throw|;
block|}
name|xsrf
operator|=
operator|new
name|SignedToken
argument_list|(
name|config
operator|.
name|maxSessionAge
argument_list|,
name|config
operator|.
name|xsrfPrivateKey
argument_list|)
expr_stmt|;
name|account
operator|=
operator|new
name|SignedToken
argument_list|(
name|config
operator|.
name|maxSessionAge
argument_list|,
name|config
operator|.
name|accountPrivateKey
argument_list|)
expr_stmt|;
block|}
DECL|method|createDatabase ()
specifier|private
name|Database
argument_list|<
name|ReviewDb
argument_list|>
name|createDatabase
parameter_list|()
throws|throws
name|OrmException
block|{
specifier|final
name|String
name|dbpath
init|=
operator|new
name|File
argument_list|(
literal|"ReviewDb"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|isnew
init|=
operator|!
operator|new
name|File
argument_list|(
name|dbpath
operator|+
literal|".data.db"
argument_list|)
operator|.
name|exists
argument_list|()
decl_stmt|;
specifier|final
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"driver"
argument_list|,
literal|"org.h2.Driver"
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"url"
argument_list|,
literal|"jdbc:h2:file:"
operator|+
name|dbpath
argument_list|)
expr_stmt|;
specifier|final
name|Database
argument_list|<
name|ReviewDb
argument_list|>
name|db
init|=
operator|new
name|Database
argument_list|<
name|ReviewDb
argument_list|>
argument_list|(
name|p
argument_list|,
name|ReviewDb
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|isnew
condition|)
block|{
specifier|final
name|ReviewDb
name|c
init|=
name|db
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
name|c
operator|.
name|createSchema
argument_list|()
expr_stmt|;
specifier|final
name|SystemConfig
name|s
init|=
name|SystemConfig
operator|.
name|create
argument_list|()
decl_stmt|;
name|s
operator|.
name|xsrfPrivateKey
operator|=
name|SignedToken
operator|.
name|generateRandomKey
argument_list|()
expr_stmt|;
name|s
operator|.
name|accountPrivateKey
operator|=
name|SignedToken
operator|.
name|generateRandomKey
argument_list|()
expr_stmt|;
name|c
operator|.
name|systemConfig
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|db
return|;
block|}
DECL|method|readSystemConfig ()
specifier|private
name|SystemConfig
name|readSystemConfig
parameter_list|()
throws|throws
name|OrmException
block|{
specifier|final
name|ReviewDb
name|c
init|=
name|db
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|c
operator|.
name|systemConfig
argument_list|()
operator|.
name|get
argument_list|(
operator|new
name|SystemConfig
operator|.
name|Key
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Get the {@link ReviewDb} schema factory for the server. */
DECL|method|getDatabase ()
specifier|public
name|Database
argument_list|<
name|ReviewDb
argument_list|>
name|getDatabase
parameter_list|()
block|{
return|return
name|db
return|;
block|}
comment|/** Time (in seconds) that user sessions stay "signed in". */
DECL|method|getSessionAge ()
specifier|public
name|int
name|getSessionAge
parameter_list|()
block|{
return|return
name|config
operator|.
name|maxSessionAge
return|;
block|}
comment|/** Get the signature support used to protect against XSRF attacks. */
DECL|method|getXsrfToken ()
specifier|public
name|SignedToken
name|getXsrfToken
parameter_list|()
block|{
return|return
name|xsrf
return|;
block|}
comment|/** Get the signature support used to protect user identity cookies. */
DECL|method|getAccountToken ()
specifier|public
name|SignedToken
name|getAccountToken
parameter_list|()
block|{
return|return
name|account
return|;
block|}
comment|/** A binary string key to encrypt cookies related to account data. */
DECL|method|getAccountCookieKey ()
specifier|public
name|String
name|getAccountCookieKey
parameter_list|()
block|{
name|byte
index|[]
name|r
init|=
operator|new
name|byte
index|[
name|config
operator|.
name|accountPrivateKey
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
name|r
operator|.
name|length
operator|-
literal|1
init|;
name|k
operator|>=
literal|0
condition|;
name|k
operator|--
control|)
block|{
name|r
index|[
name|k
index|]
operator|=
operator|(
name|byte
operator|)
name|config
operator|.
name|accountPrivateKey
operator|.
name|charAt
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
name|r
operator|=
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|r
argument_list|)
expr_stmt|;
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|r
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|r
index|[
name|i
index|]
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
block|}
end_class

end_unit

