begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.httpd.rpc
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|rpc
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
name|errors
operator|.
name|CorruptEntityException
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
name|errors
operator|.
name|InvalidQueryException
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
name|errors
operator|.
name|NoSuchEntityException
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
name|errors
operator|.
name|NoSuchGroupException
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
name|reviewdb
operator|.
name|server
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
name|server
operator|.
name|CurrentUser
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
name|IdentifiedUser
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
name|project
operator|.
name|NoSuchChangeException
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
name|project
operator|.
name|NoSuchProjectException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|rpc
operator|.
name|AsyncCallback
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
name|server
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
name|inject
operator|.
name|Provider
import|;
end_import

begin_comment
comment|/** Support for services which require a {@link ReviewDb} instance. */
end_comment

begin_class
DECL|class|BaseServiceImplementation
specifier|public
class|class
name|BaseServiceImplementation
block|{
DECL|field|schema
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
DECL|field|currentUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|?
extends|extends
name|CurrentUser
argument_list|>
name|currentUser
decl_stmt|;
DECL|method|BaseServiceImplementation (final Provider<ReviewDb> schema, final Provider<? extends CurrentUser> currentUser)
specifier|protected
name|BaseServiceImplementation
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|schema
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|?
extends|extends
name|CurrentUser
argument_list|>
name|currentUser
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|currentUser
operator|=
name|currentUser
expr_stmt|;
block|}
DECL|method|getAccountId ()
specifier|protected
name|Account
operator|.
name|Id
name|getAccountId
parameter_list|()
block|{
name|CurrentUser
name|u
init|=
name|currentUser
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|u
operator|instanceof
name|IdentifiedUser
condition|)
block|{
return|return
operator|(
operator|(
name|IdentifiedUser
operator|)
name|u
operator|)
operator|.
name|getAccountId
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Executes<code>action.run</code> with an active ReviewDb connection.    *<p>    * A database handle is automatically opened and closed around the action's    * {@link Action#run(ReviewDb)} method. OrmExceptions are caught and passed    * into the onFailure method of the callback.    *    * @param<T> type of result the callback expects.    * @param callback the callback that will receive the result.    * @param action the action logic to perform.    */
DECL|method|run (final AsyncCallback<T> callback, final Action<T> action)
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|void
name|run
parameter_list|(
specifier|final
name|AsyncCallback
argument_list|<
name|T
argument_list|>
name|callback
parameter_list|,
specifier|final
name|Action
argument_list|<
name|T
argument_list|>
name|action
parameter_list|)
block|{
try|try
block|{
specifier|final
name|T
name|r
init|=
name|action
operator|.
name|run
argument_list|(
name|schema
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|callback
operator|.
name|onSuccess
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InvalidQueryException
name|e
parameter_list|)
block|{
name|callback
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchProjectException
name|e
parameter_list|)
block|{
name|callback
operator|.
name|onFailure
argument_list|(
operator|new
name|NoSuchEntityException
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchGroupException
name|e
parameter_list|)
block|{
name|callback
operator|.
name|onFailure
argument_list|(
operator|new
name|NoSuchEntityException
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
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|Failure
condition|)
block|{
name|callback
operator|.
name|onFailure
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|CorruptEntityException
condition|)
block|{
name|callback
operator|.
name|onFailure
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|NoSuchEntityException
condition|)
block|{
name|callback
operator|.
name|onFailure
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|callback
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Failure
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|NoSuchProjectException
operator|||
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|NoSuchChangeException
condition|)
block|{
name|callback
operator|.
name|onFailure
argument_list|(
operator|new
name|NoSuchEntityException
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|callback
operator|.
name|onFailure
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Exception whose cause is passed into onFailure. */
DECL|class|Failure
specifier|public
specifier|static
class|class
name|Failure
extends|extends
name|Exception
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|Failure (final Throwable why)
specifier|public
name|Failure
parameter_list|(
specifier|final
name|Throwable
name|why
parameter_list|)
block|{
name|super
argument_list|(
name|why
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Arbitrary action to run with a database connection. */
DECL|interface|Action
specifier|public
specifier|static
interface|interface
name|Action
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**      * Perform this action, returning the onSuccess value.      *      * @param db an open database handle to be used by this connection.      * @return he value to pass to {@link AsyncCallback#onSuccess(Object)}.      * @throws OrmException any schema based action failed.      * @throws Failure cause is given to      *         {@link AsyncCallback#onFailure(Throwable)}.      * @throws NoSuchProjectException      * @throws NoSuchGroupException      * @throws InvalidQueryException      */
DECL|method|run (ReviewDb db)
name|T
name|run
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
throws|,
name|Failure
throws|,
name|NoSuchProjectException
throws|,
name|NoSuchGroupException
throws|,
name|InvalidQueryException
function_decl|;
block|}
block|}
end_class

end_unit

