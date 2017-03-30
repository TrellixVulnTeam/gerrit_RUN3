begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.permissions
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|permissions
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
name|checkNotNull
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
name|extensions
operator|.
name|restapi
operator|.
name|AuthException
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
name|Branch
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
name|notedb
operator|.
name|ChangeNotes
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
name|query
operator|.
name|change
operator|.
name|ChangeData
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|util
operator|.
name|Providers
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Checks authorization to perform an action on project, ref, or change.  *  *<p>{@code PermissionBackend} should be a singleton for the server, acting as a factory for  * lightweight request instances.  *  *<p>{@code check} methods should be used during action handlers to verify the user is allowed to  * exercise the specified permission. For convenience in implementation {@code check} methods throw  * {@link AuthException} if the permission is denied.  *  *<p>{@code test} methods should be used when constructing replies to the client and the result  * object needs to include a true/false hint indicating the user's ability to exercise the  * permission. This is suitable for configuring UI button state, but should not be relied upon to  * guard handlers before making state changes.  *  *<p>Example use:  *  *<pre>  *   private final PermissionBackend permissions;  *   private final Provider<CurrentUser> user;  *  *   @Inject  *   Foo(PermissionBackend permissions, Provider<CurrentUser> user) {  *     this.permissions = permissions;  *     this.user = user;  *   }  *  *   public void apply(...) {  *     permissions.user(user).change(cd).check(ChangePermission.SUBMIT);  *   }  *  *   public UiAction.Description getDescription(ChangeResource rsrc) {  *     return new UiAction.Description()  *       .setLabel("Submit")  *       .setVisible(rsrc.permissions().testOrFalse(ChangePermission.SUBMIT));  * }  *</pre>  */
end_comment

begin_class
DECL|class|PermissionBackend
specifier|public
specifier|abstract
class|class
name|PermissionBackend
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PermissionBackend
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** @return lightweight factory scoped to answer for the specified user. */
DECL|method|user (CurrentUser user)
specifier|public
specifier|abstract
name|WithUser
name|user
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
function_decl|;
comment|/** @return lightweight factory scoped to answer for the specified user. */
DECL|method|user (Provider<CurrentUser> user)
specifier|public
name|WithUser
name|user
parameter_list|(
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
parameter_list|)
block|{
return|return
name|user
argument_list|(
name|checkNotNull
argument_list|(
name|user
argument_list|,
literal|"Provider<CurrentUser>"
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
comment|/** PermissionBackend with an optional per-request ReviewDb handle. */
DECL|class|AcceptsReviewDb
specifier|public
specifier|abstract
specifier|static
class|class
name|AcceptsReviewDb
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|db
specifier|protected
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|method|database (Provider<ReviewDb> db)
specifier|public
name|T
name|database
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
block|{
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
block|}
return|return
name|self
argument_list|()
return|;
block|}
DECL|method|database (ReviewDb db)
specifier|public
name|T
name|database
parameter_list|(
name|ReviewDb
name|db
parameter_list|)
block|{
return|return
name|database
argument_list|(
name|Providers
operator|.
name|of
argument_list|(
name|checkNotNull
argument_list|(
name|db
argument_list|,
literal|"ReviewDb"
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|self ()
specifier|private
name|T
name|self
parameter_list|()
block|{
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
block|}
comment|/** PermissionBackend scoped to a specific user. */
DECL|class|WithUser
specifier|public
specifier|abstract
specifier|static
class|class
name|WithUser
extends|extends
name|AcceptsReviewDb
argument_list|<
name|WithUser
argument_list|>
block|{
comment|/** @return instance scoped for the specified project. */
DECL|method|project (Project.NameKey project)
specifier|public
specifier|abstract
name|ForProject
name|project
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
function_decl|;
comment|/** @return instance scoped for the {@code ref}, and its parent project. */
DECL|method|ref (Branch.NameKey ref)
specifier|public
name|ForRef
name|ref
parameter_list|(
name|Branch
operator|.
name|NameKey
name|ref
parameter_list|)
block|{
return|return
name|project
argument_list|(
name|ref
operator|.
name|getParentKey
argument_list|()
argument_list|)
operator|.
name|ref
argument_list|(
name|ref
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|database
argument_list|(
name|db
argument_list|)
return|;
block|}
comment|/** @return instance scoped for the change, and its destination ref and project. */
DECL|method|change (ChangeData cd)
specifier|public
name|ForChange
name|change
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
block|{
try|try
block|{
return|return
name|ref
argument_list|(
name|cd
operator|.
name|change
argument_list|()
operator|.
name|getDest
argument_list|()
argument_list|)
operator|.
name|change
argument_list|(
name|cd
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
return|return
name|FailedPermissionBackend
operator|.
name|change
argument_list|(
literal|"unavailable"
argument_list|,
name|e
argument_list|)
return|;
block|}
block|}
comment|/** @return instance scoped for the change, and its destination ref and project. */
DECL|method|change (ChangeNotes notes)
specifier|public
name|ForChange
name|change
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|)
block|{
return|return
name|ref
argument_list|(
name|notes
operator|.
name|getChange
argument_list|()
operator|.
name|getDest
argument_list|()
argument_list|)
operator|.
name|change
argument_list|(
name|notes
argument_list|)
return|;
block|}
block|}
comment|/** PermissionBackend scoped to a user and project. */
DECL|class|ForProject
specifier|public
specifier|abstract
specifier|static
class|class
name|ForProject
extends|extends
name|AcceptsReviewDb
argument_list|<
name|ForProject
argument_list|>
block|{
comment|/** @return new instance rescoped to same project, but different {@code user}. */
DECL|method|user (CurrentUser user)
specifier|public
specifier|abstract
name|ForProject
name|user
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
function_decl|;
comment|/** @return instance scoped for {@code ref} in this project. */
DECL|method|ref (String ref)
specifier|public
specifier|abstract
name|ForRef
name|ref
parameter_list|(
name|String
name|ref
parameter_list|)
function_decl|;
comment|/** Verify scoped user can {@code perm}, throwing if denied. */
DECL|method|check (ProjectPermission perm)
specifier|public
specifier|abstract
name|void
name|check
parameter_list|(
name|ProjectPermission
name|perm
parameter_list|)
throws|throws
name|AuthException
throws|,
name|PermissionBackendException
function_decl|;
comment|/** Filter {@code permSet} to permissions scoped user might be able to perform. */
DECL|method|test (Collection<ProjectPermission> permSet)
specifier|public
specifier|abstract
name|Set
argument_list|<
name|ProjectPermission
argument_list|>
name|test
parameter_list|(
name|Collection
argument_list|<
name|ProjectPermission
argument_list|>
name|permSet
parameter_list|)
throws|throws
name|PermissionBackendException
function_decl|;
DECL|method|test (ProjectPermission perm)
specifier|public
name|boolean
name|test
parameter_list|(
name|ProjectPermission
name|perm
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
return|return
name|test
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|perm
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
name|perm
argument_list|)
return|;
block|}
block|}
comment|/** PermissionBackend scoped to a user, project and reference. */
DECL|class|ForRef
specifier|public
specifier|abstract
specifier|static
class|class
name|ForRef
extends|extends
name|AcceptsReviewDb
argument_list|<
name|ForRef
argument_list|>
block|{
comment|/** @return new instance rescoped to same reference, but different {@code user}. */
DECL|method|user (CurrentUser user)
specifier|public
specifier|abstract
name|ForRef
name|user
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
function_decl|;
comment|/** @return instance scoped to change. */
DECL|method|change (ChangeData cd)
specifier|public
specifier|abstract
name|ForChange
name|change
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
function_decl|;
comment|/** @return instance scoped to change. */
DECL|method|change (ChangeNotes notes)
specifier|public
specifier|abstract
name|ForChange
name|change
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|)
function_decl|;
comment|/** Verify scoped user can {@code perm}, throwing if denied. */
DECL|method|check (RefPermission perm)
specifier|public
specifier|abstract
name|void
name|check
parameter_list|(
name|RefPermission
name|perm
parameter_list|)
throws|throws
name|AuthException
throws|,
name|PermissionBackendException
function_decl|;
comment|/** Filter {@code permSet} to permissions scoped user might be able to perform. */
DECL|method|test (Collection<RefPermission> permSet)
specifier|public
specifier|abstract
name|Set
argument_list|<
name|RefPermission
argument_list|>
name|test
parameter_list|(
name|Collection
argument_list|<
name|RefPermission
argument_list|>
name|permSet
parameter_list|)
throws|throws
name|PermissionBackendException
function_decl|;
DECL|method|test (RefPermission perm)
specifier|public
name|boolean
name|test
parameter_list|(
name|RefPermission
name|perm
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
return|return
name|test
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|perm
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
name|perm
argument_list|)
return|;
block|}
block|}
comment|/** PermissionBackend scoped to a user, project, reference and change. */
DECL|class|ForChange
specifier|public
specifier|abstract
specifier|static
class|class
name|ForChange
extends|extends
name|AcceptsReviewDb
argument_list|<
name|ForChange
argument_list|>
block|{
comment|/** @return new instance rescoped to same change, but different {@code user}. */
DECL|method|user (CurrentUser user)
specifier|public
specifier|abstract
name|ForChange
name|user
parameter_list|(
name|CurrentUser
name|user
parameter_list|)
function_decl|;
comment|/** Verify scoped user can {@code perm}, throwing if denied. */
DECL|method|check (ChangePermissionOrLabel perm)
specifier|public
specifier|abstract
name|void
name|check
parameter_list|(
name|ChangePermissionOrLabel
name|perm
parameter_list|)
throws|throws
name|AuthException
throws|,
name|PermissionBackendException
function_decl|;
comment|/** Filter {@code permSet} to permissions scoped user might be able to perform. */
DECL|method|test (Collection<T> permSet)
specifier|public
specifier|abstract
parameter_list|<
name|T
extends|extends
name|ChangePermissionOrLabel
parameter_list|>
name|Set
argument_list|<
name|T
argument_list|>
name|test
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|permSet
parameter_list|)
throws|throws
name|PermissionBackendException
function_decl|;
DECL|method|test (ChangePermissionOrLabel perm)
specifier|public
name|boolean
name|test
parameter_list|(
name|ChangePermissionOrLabel
name|perm
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
return|return
name|test
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|perm
argument_list|)
argument_list|)
operator|.
name|contains
argument_list|(
name|perm
argument_list|)
return|;
block|}
comment|/**      * Test if user may be able to perform the permission.      *      *<p>Similar to {@link #test(ChangePermissionOrLabel)} except this method returns {@code false}      * instead of throwing an exception.      *      * @param perm the permission to test.      * @return true if the user might be able to perform the permission; false if the user may be      *     missing the necessary grants or state, or if the backend threw an exception.      */
DECL|method|testOrFalse (ChangePermissionOrLabel perm)
specifier|public
name|boolean
name|testOrFalse
parameter_list|(
name|ChangePermissionOrLabel
name|perm
parameter_list|)
block|{
try|try
block|{
return|return
name|test
argument_list|(
name|perm
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PermissionBackendException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Cannot test "
operator|+
name|perm
operator|+
literal|"; assuming false"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

