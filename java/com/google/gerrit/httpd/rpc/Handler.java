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
name|gerrit
operator|.
name|server
operator|.
name|project
operator|.
name|NoSuchRefException
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
name|common
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
name|gwtjsonrpc
operator|.
name|common
operator|.
name|VoidResult
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_comment
comment|/**  * Base class for RPC service implementations.  *  *<p>Typically an RPC service implementation will extend this class and use Guice injection to  * manage its state. For example:  *  *<pre>  *   class Foo extends Handler&lt;Result&gt; {  *     interface Factory {  *       Foo create(... args ...);  *     }  *&#064;Inject  *     Foo(state, @Assisted args) { ... }  *     Result get() throws Exception { ... }  *   }  *</pre>  *  * @param<T> type of result for {@link AsyncCallback#onSuccess(Object)} if the operation completed  *     successfully.  */
end_comment

begin_class
DECL|class|Handler
specifier|public
specifier|abstract
class|class
name|Handler
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Callable
argument_list|<
name|T
argument_list|>
block|{
DECL|method|wrap (Callable<T> r)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Handler
argument_list|<
name|T
argument_list|>
name|wrap
parameter_list|(
name|Callable
argument_list|<
name|T
argument_list|>
name|r
parameter_list|)
block|{
return|return
operator|new
name|Handler
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|T
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|r
operator|.
name|call
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/**    * Run the operation and pass the result to the callback.    *    * @param callback callback to receive the result of {@link #call()}.    */
DECL|method|to (AsyncCallback<T> callback)
specifier|public
specifier|final
name|void
name|to
parameter_list|(
name|AsyncCallback
argument_list|<
name|T
argument_list|>
name|callback
parameter_list|)
block|{
try|try
block|{
specifier|final
name|T
name|r
init|=
name|call
argument_list|()
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
name|NoSuchProjectException
decl||
name|NoSuchChangeException
decl||
name|NoSuchRefException
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
name|Exception
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
block|}
comment|/**    * Compute the operation result.    *    * @return the result of the operation. Return {@link VoidResult#INSTANCE} if there is no    *     meaningful return value for the operation.    * @throws Exception the operation failed. The caller will log the exception and the stack trace,    *     if it is worth logging on the server side.    */
annotation|@
name|Override
DECL|method|call ()
specifier|public
specifier|abstract
name|T
name|call
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

