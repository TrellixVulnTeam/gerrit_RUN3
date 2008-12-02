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
name|ApprovalCategory
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
name|ApprovalCategoryValue
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
name|client
operator|.
name|Transaction
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
name|ArrayList
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
name|dsName
init|=
literal|"java:comp/env/jdbc/ReviewDb"
decl_stmt|;
specifier|final
name|String
name|pName
init|=
literal|"GerritServer.properties"
decl_stmt|;
name|DataSource
name|ds
decl_stmt|;
try|try
block|{
name|ds
operator|=
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
expr_stmt|;
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
argument_list|(
name|pName
argument_list|)
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
name|OrmException
argument_list|(
literal|"No DataSource "
operator|+
name|dsName
operator|+
literal|" and no "
operator|+
name|pName
operator|+
literal|" in CLASSPATH.  GerritServer requires either format."
argument_list|,
name|namingErr
argument_list|)
throw|;
block|}
try|try
block|{
name|ds
operator|=
operator|new
name|SimpleDataSource
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|se
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Database in "
operator|+
name|pName
operator|+
literal|" unavailable"
argument_list|,
name|se
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|Database
argument_list|<
name|ReviewDb
argument_list|>
argument_list|(
name|ds
argument_list|,
name|ReviewDb
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|readGerritDataSource (final String name)
specifier|private
name|Properties
name|readGerritDataSource
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|Properties
name|srvprop
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|final
name|InputStream
name|in
decl_stmt|;
name|in
operator|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
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
name|OrmException
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
DECL|method|initSystemConfig (final ReviewDb c)
specifier|private
name|void
name|initSystemConfig
parameter_list|(
specifier|final
name|ReviewDb
name|c
parameter_list|)
throws|throws
name|OrmException
block|{
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
DECL|method|initCodeReviewCategory (final ReviewDb c)
specifier|private
name|void
name|initCodeReviewCategory
parameter_list|(
specifier|final
name|ReviewDb
name|c
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|Transaction
name|txn
init|=
name|c
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|ApprovalCategory
name|cat
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
name|vals
decl_stmt|;
name|cat
operator|=
operator|new
name|ApprovalCategory
argument_list|(
operator|new
name|ApprovalCategory
operator|.
name|Id
argument_list|(
literal|"CRVW"
argument_list|)
argument_list|,
literal|"Code Review"
argument_list|)
expr_stmt|;
name|vals
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
argument_list|()
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
literal|2
argument_list|,
literal|"Looks good to me, approved"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
literal|1
argument_list|,
literal|"Looks good to me, but someone else must approve"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
literal|0
argument_list|,
literal|"No score"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
operator|-
literal|1
argument_list|,
literal|"I would prefer that you didn't submit this"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
operator|-
literal|2
argument_list|,
literal|"Do not submit"
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategories
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|cat
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategoryValues
argument_list|()
operator|.
name|insert
argument_list|(
name|vals
argument_list|)
expr_stmt|;
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|initVerifiedCategory (final ReviewDb c)
specifier|private
name|void
name|initVerifiedCategory
parameter_list|(
specifier|final
name|ReviewDb
name|c
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|Transaction
name|txn
init|=
name|c
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
specifier|final
name|ApprovalCategory
name|cat
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
name|vals
decl_stmt|;
name|cat
operator|=
operator|new
name|ApprovalCategory
argument_list|(
operator|new
name|ApprovalCategory
operator|.
name|Id
argument_list|(
literal|"VRIF"
argument_list|)
argument_list|,
literal|"Verified"
argument_list|)
expr_stmt|;
name|vals
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
argument_list|()
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
literal|1
argument_list|,
literal|"Verified"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
literal|0
argument_list|,
literal|"No score"
argument_list|)
argument_list|)
expr_stmt|;
name|vals
operator|.
name|add
argument_list|(
name|value
argument_list|(
name|cat
argument_list|,
operator|-
literal|1
argument_list|,
literal|"Fails"
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategories
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|cat
argument_list|)
argument_list|,
name|txn
argument_list|)
expr_stmt|;
name|c
operator|.
name|approvalCategoryValues
argument_list|()
operator|.
name|insert
argument_list|(
name|vals
argument_list|)
expr_stmt|;
name|txn
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|value (final ApprovalCategory cat, final int value, final String name)
specifier|private
specifier|static
name|ApprovalCategoryValue
name|value
parameter_list|(
specifier|final
name|ApprovalCategory
name|cat
parameter_list|,
specifier|final
name|int
name|value
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ApprovalCategoryValue
argument_list|(
operator|new
name|ApprovalCategoryValue
operator|.
name|Key
argument_list|(
name|cat
operator|.
name|getId
argument_list|()
argument_list|,
operator|(
name|short
operator|)
name|value
argument_list|)
argument_list|,
name|name
argument_list|)
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
name|SystemConfig
name|sysconf
decl_stmt|;
try|try
block|{
name|sysconf
operator|=
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
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
comment|// Assume the schema doesn't exist, and create it.
comment|// TODO Implement schema upgrades and/or exporting to a script file.
comment|//
name|sysconf
operator|=
literal|null
expr_stmt|;
name|c
operator|.
name|createSchema
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|sysconf
operator|==
literal|null
condition|)
block|{
comment|// Assume the schema is empty and populate it.
comment|//
name|initSystemConfig
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|initCodeReviewCategory
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|initVerifiedCategory
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|sysconf
operator|=
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
expr_stmt|;
block|}
return|return
name|sysconf
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
comment|/** Local filesystem location of header/footer/CSS configuration files. */
DECL|method|getSitePath ()
specifier|public
name|File
name|getSitePath
parameter_list|()
block|{
return|return
name|config
operator|.
name|sitePath
operator|!=
literal|null
condition|?
operator|new
name|File
argument_list|(
name|config
operator|.
name|sitePath
argument_list|)
else|:
literal|null
return|;
block|}
comment|/** Optional canonical URL for this application. */
DECL|method|getCanonicalURL ()
specifier|public
name|String
name|getCanonicalURL
parameter_list|()
block|{
name|String
name|u
init|=
name|config
operator|.
name|canonicalUrl
decl_stmt|;
if|if
condition|(
name|u
operator|!=
literal|null
operator|&&
operator|!
name|u
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|u
operator|+=
literal|"/"
expr_stmt|;
block|}
return|return
name|u
return|;
block|}
block|}
end_class

end_unit

