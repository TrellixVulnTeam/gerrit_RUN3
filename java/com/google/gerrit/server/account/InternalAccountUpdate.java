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
comment|// limitations under the Licens
end_comment

begin_package
DECL|package|com.google.gerrit.server.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|account
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|auto
operator|.
name|value
operator|.
name|AutoValue
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
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|server
operator|.
name|account
operator|.
name|externalids
operator|.
name|DuplicateExternalIdKeyException
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
name|account
operator|.
name|externalids
operator|.
name|ExternalId
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
name|Optional
import|;
end_import

begin_comment
comment|/**  * Class to prepare updates to an account.  *  *<p>The getters in this class and the setters in the {@link Builder} correspond to fields in  * {@link Account}. The account ID and the registration date cannot be updated.  */
end_comment

begin_class
annotation|@
name|AutoValue
DECL|class|InternalAccountUpdate
specifier|public
specifier|abstract
class|class
name|InternalAccountUpdate
block|{
DECL|method|builder ()
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
operator|.
name|WrapperThatConvertsNullStringArgsToEmptyStrings
argument_list|(
operator|new
name|AutoValue_InternalAccountUpdate
operator|.
name|Builder
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns the new value for the full name.    *    * @return the new value for the full name, {@code Optional#empty()} if the full name is not being    *     updated, {@code Optional#of("")} if the full name is unset, the wrapped value is never    *     {@code null}    */
DECL|method|getFullName ()
specifier|public
specifier|abstract
name|Optional
argument_list|<
name|String
argument_list|>
name|getFullName
parameter_list|()
function_decl|;
comment|/**    * Returns the new value for the preferred email.    *    * @return the new value for the preferred email, {@code Optional#empty()} if the preferred email    *     is not being updated, {@code Optional#of("")} if the preferred email is unset, the wrapped    *     value is never {@code null}    */
DECL|method|getPreferredEmail ()
specifier|public
specifier|abstract
name|Optional
argument_list|<
name|String
argument_list|>
name|getPreferredEmail
parameter_list|()
function_decl|;
comment|/**    * Returns the new value for the active flag.    *    * @return the new value for the active flag, {@code Optional#empty()} if the active flag is not    *     being updated, the wrapped value is never {@code null}    */
DECL|method|getActive ()
specifier|public
specifier|abstract
name|Optional
argument_list|<
name|Boolean
argument_list|>
name|getActive
parameter_list|()
function_decl|;
comment|/**    * Returns the new value for the status.    *    * @return the new value for the status, {@code Optional#empty()} if the status is not being    *     updated, {@code Optional#of("")} if the status is unset, the wrapped value is never {@code    *     null}    */
DECL|method|getStatus ()
specifier|public
specifier|abstract
name|Optional
argument_list|<
name|String
argument_list|>
name|getStatus
parameter_list|()
function_decl|;
comment|/**    * Returns external IDs that should be newly created for the account.    *    * @return external IDs that should be newly created for the account    */
DECL|method|getCreatedExternalIds ()
specifier|public
specifier|abstract
name|ImmutableSet
argument_list|<
name|ExternalId
argument_list|>
name|getCreatedExternalIds
parameter_list|()
function_decl|;
comment|/**    * Returns external IDs that should be updated for the account.    *    * @return external IDs that should be updated for the account    */
DECL|method|getUpdatedExternalIds ()
specifier|public
specifier|abstract
name|ImmutableSet
argument_list|<
name|ExternalId
argument_list|>
name|getUpdatedExternalIds
parameter_list|()
function_decl|;
comment|/**    * Returns external IDs that should be deleted for the account.    *    * @return external IDs that should be deleted for the account    */
DECL|method|getDeletedExternalIds ()
specifier|public
specifier|abstract
name|ImmutableSet
argument_list|<
name|ExternalId
argument_list|>
name|getDeletedExternalIds
parameter_list|()
function_decl|;
comment|/**    * Class to build an account update.    *    *<p>Account data is only updated if the corresponding setter is invoked. If a setter is not    * invoked the corresponding data stays unchanged. To unset string values the setter can be    * invoked with either {@code null} or an empty string ({@code null} is converted to an empty    * string by using the {@link WrapperThatConvertsNullStringArgsToEmptyStrings} wrapper, see {@link    * InternalAccountUpdate#builder()}).    */
annotation|@
name|AutoValue
operator|.
name|Builder
DECL|class|Builder
specifier|public
specifier|abstract
specifier|static
class|class
name|Builder
block|{
comment|/**      * Sets a new full name for the account.      *      * @param fullName the new full name, if {@code null} or empty string the full name is unset      * @return the builder      */
DECL|method|setFullName (String fullName)
specifier|public
specifier|abstract
name|Builder
name|setFullName
parameter_list|(
name|String
name|fullName
parameter_list|)
function_decl|;
comment|/**      * Sets a new preferred email for the account.      *      * @param preferredEmail the new preferred email, if {@code null} or empty string the preferred      *     email is unset      * @return the builder      */
DECL|method|setPreferredEmail (String preferredEmail)
specifier|public
specifier|abstract
name|Builder
name|setPreferredEmail
parameter_list|(
name|String
name|preferredEmail
parameter_list|)
function_decl|;
comment|/**      * Sets the active flag for the account.      *      * @param active {@code true} if the account should be set to active, {@code false} if the      *     account should be set to inactive      * @return the builder      */
DECL|method|setActive (boolean active)
specifier|public
specifier|abstract
name|Builder
name|setActive
parameter_list|(
name|boolean
name|active
parameter_list|)
function_decl|;
comment|/**      * Sets a new status for the account.      *      * @param status the new status, if {@code null} or empty string the status is unset      * @return the builder      */
DECL|method|setStatus (String status)
specifier|public
specifier|abstract
name|Builder
name|setStatus
parameter_list|(
name|String
name|status
parameter_list|)
function_decl|;
comment|/**      * Returns a builder for the set of created external IDs.      *      * @return builder for the set of created external IDs.      */
DECL|method|createdExternalIdsBuilder ()
specifier|abstract
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|ExternalId
argument_list|>
name|createdExternalIdsBuilder
parameter_list|()
function_decl|;
comment|/**      * Adds a new external ID for the account.      *      *<p>The account ID of the external ID must match the account ID of the account that is      * updated.      *      *<p>If an external ID with the same ID already exists the account update will fail with {@link      * DuplicateExternalIdKeyException}.      *      * @param extId external ID that should be added      * @return the builder      */
DECL|method|addExternalId (ExternalId extId)
specifier|public
name|Builder
name|addExternalId
parameter_list|(
name|ExternalId
name|extId
parameter_list|)
block|{
return|return
name|addExternalIds
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|extId
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Adds new external IDs for the account.      *      *<p>The account IDs of the external IDs must match the account ID of the account that is      * updated.      *      *<p>If any of the external ID keys already exists, the insert fails with {@link      * DuplicateExternalIdKeyException}.      *      * @param extIds external IDs that should be added      * @return the builder      */
DECL|method|addExternalIds (Collection<ExternalId> extIds)
specifier|public
name|Builder
name|addExternalIds
parameter_list|(
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|extIds
parameter_list|)
block|{
name|createdExternalIdsBuilder
argument_list|()
operator|.
name|addAll
argument_list|(
name|extIds
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Returns a builder for the set of updated external IDs.      *      * @return builder for the set of updated external IDs.      */
DECL|method|updatedExternalIdsBuilder ()
specifier|abstract
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|ExternalId
argument_list|>
name|updatedExternalIdsBuilder
parameter_list|()
function_decl|;
comment|/**      * Updates an external ID for the account.      *      *<p>The account ID of the external ID must match the account ID of the account that is      * updated.      *      *<p>If no external ID with the ID exists the external ID is created.      *      * @param extId external ID that should be updated      * @return the builder      */
DECL|method|updateExternalId (ExternalId extId)
specifier|public
name|Builder
name|updateExternalId
parameter_list|(
name|ExternalId
name|extId
parameter_list|)
block|{
return|return
name|updateExternalIds
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|extId
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Updates external IDs for the account.      *      *<p>The account IDs of the external IDs must match the account ID of the account that is      * updated.      *      *<p>If any of the external IDs already exists, it is overwritten. New external IDs are      * inserted.      *      * @param extIds external IDs that should be updated      * @return the builder      */
DECL|method|updateExternalIds (Collection<ExternalId> extIds)
specifier|public
name|Builder
name|updateExternalIds
parameter_list|(
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|extIds
parameter_list|)
block|{
name|updatedExternalIdsBuilder
argument_list|()
operator|.
name|addAll
argument_list|(
name|extIds
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Returns a builder for the set of deleted external IDs.      *      * @return builder for the set of deleted external IDs.      */
DECL|method|deletedExternalIdsBuilder ()
specifier|abstract
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|ExternalId
argument_list|>
name|deletedExternalIdsBuilder
parameter_list|()
function_decl|;
comment|/**      * Deletes an external ID for the account.      *      *<p>The account ID of the external ID must match the account ID of the account that is      * updated.      *      *<p>If no external ID with the ID exists this is a no-op.      *      * @param extId external ID that should be deleted      * @return the builder      */
DECL|method|deleteExternalId (ExternalId extId)
specifier|public
name|Builder
name|deleteExternalId
parameter_list|(
name|ExternalId
name|extId
parameter_list|)
block|{
return|return
name|deleteExternalIds
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|extId
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Delete external IDs for the account.      *      *<p>The account IDs of the external IDs must match the account ID of the account that is      * updated.      *      *<p>For non-existing external IDs this is a no-op.      *      * @param extIds external IDs that should be deleted      * @return the builder      */
DECL|method|deleteExternalIds (Collection<ExternalId> extIds)
specifier|public
name|Builder
name|deleteExternalIds
parameter_list|(
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|extIds
parameter_list|)
block|{
name|deletedExternalIdsBuilder
argument_list|()
operator|.
name|addAll
argument_list|(
name|extIds
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Replaces an external ID.      *      * @param extIdToDelete external ID that should be deleted      * @param extIdToAdd external ID that should be added      * @return the builder      */
DECL|method|replaceExternalId (ExternalId extIdToDelete, ExternalId extIdToAdd)
specifier|public
name|Builder
name|replaceExternalId
parameter_list|(
name|ExternalId
name|extIdToDelete
parameter_list|,
name|ExternalId
name|extIdToAdd
parameter_list|)
block|{
return|return
name|replaceExternalIds
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|extIdToDelete
argument_list|)
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|extIdToAdd
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Replaces an external IDs.      *      * @param extIdsToDelete external IDs that should be deleted      * @param extIdsToAdd external IDs that should be added      * @return the builder      */
DECL|method|replaceExternalIds ( Collection<ExternalId> extIdsToDelete, Collection<ExternalId> extIdsToAdd)
specifier|public
name|Builder
name|replaceExternalIds
parameter_list|(
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|extIdsToDelete
parameter_list|,
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|extIdsToAdd
parameter_list|)
block|{
return|return
name|deleteExternalIds
argument_list|(
name|extIdsToDelete
argument_list|)
operator|.
name|addExternalIds
argument_list|(
name|extIdsToAdd
argument_list|)
return|;
block|}
comment|/**      * Builds the account update.      *      * @return the account update      */
DECL|method|build ()
specifier|public
specifier|abstract
name|InternalAccountUpdate
name|build
parameter_list|()
function_decl|;
comment|/**      * Wrapper for {@link Builder} that converts {@code null} string arguments to empty strings for      * all setter methods. This allows us to treat setter invocations with a {@code null} string      * argument as signal to unset the corresponding field. E.g. for a builder method {@code      * setX(String)} the following semantics apply:      *      *<ul>      *<li>Method is not invoked: X stays unchanged, X is stored as {@code Optional.empty()}.      *<li>Argument is a non-empty string Y: X is updated to the Y, X is stored as {@code      *       Optional.of(Y)}.      *<li>Argument is an empty string: X is unset, X is stored as {@code Optional.of("")}      *<li>Argument is {@code null}: X is unset, X is stored as {@code Optional.of("")} (since the      *       wrapper converts {@code null} to an empty string)      *</ul>      *      * Without the wrapper calling {@code setX(null)} would fail with a {@link      * NullPointerException}. Hence all callers would need to take care to call {@link      * Strings#nullToEmpty(String)} for all string arguments and likely it would be forgotten in      * some places.      *      *<p>This means the stored values are interpreted like this:      *      *<ul>      *<li>{@code Optional.empty()}: property stays unchanged      *<li>{@code Optional.of(<non-empty-string>)}: property is updated      *<li>{@code Optional.of("")}: property is unset      *</ul>      *      * This wrapper forwards all method invocations to the wrapped {@link Builder} instance that was      * created by AutoValue. For methods that return the AutoValue {@link Builder} instance the      * return value is replaced with the wrapper instance so that all chained calls go through the      * wrapper.      */
DECL|class|WrapperThatConvertsNullStringArgsToEmptyStrings
specifier|private
specifier|static
class|class
name|WrapperThatConvertsNullStringArgsToEmptyStrings
extends|extends
name|Builder
block|{
DECL|field|delegate
specifier|private
specifier|final
name|Builder
name|delegate
decl_stmt|;
DECL|method|WrapperThatConvertsNullStringArgsToEmptyStrings (Builder delegate)
specifier|private
name|WrapperThatConvertsNullStringArgsToEmptyStrings
parameter_list|(
name|Builder
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setFullName (String fullName)
specifier|public
name|Builder
name|setFullName
parameter_list|(
name|String
name|fullName
parameter_list|)
block|{
name|delegate
operator|.
name|setFullName
argument_list|(
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|fullName
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|setPreferredEmail (String preferredEmail)
specifier|public
name|Builder
name|setPreferredEmail
parameter_list|(
name|String
name|preferredEmail
parameter_list|)
block|{
name|delegate
operator|.
name|setPreferredEmail
argument_list|(
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|preferredEmail
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|setActive (boolean active)
specifier|public
name|Builder
name|setActive
parameter_list|(
name|boolean
name|active
parameter_list|)
block|{
name|delegate
operator|.
name|setActive
argument_list|(
name|active
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|setStatus (String status)
specifier|public
name|Builder
name|setStatus
parameter_list|(
name|String
name|status
parameter_list|)
block|{
name|delegate
operator|.
name|setStatus
argument_list|(
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|build ()
specifier|public
name|InternalAccountUpdate
name|build
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createdExternalIdsBuilder ()
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|ExternalId
argument_list|>
name|createdExternalIdsBuilder
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|createdExternalIdsBuilder
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|addExternalIds (Collection<ExternalId> extIds)
specifier|public
name|Builder
name|addExternalIds
parameter_list|(
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|extIds
parameter_list|)
block|{
name|delegate
operator|.
name|addExternalIds
argument_list|(
name|extIds
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|updatedExternalIdsBuilder ()
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|ExternalId
argument_list|>
name|updatedExternalIdsBuilder
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|updatedExternalIdsBuilder
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|updateExternalIds (Collection<ExternalId> extIds)
specifier|public
name|Builder
name|updateExternalIds
parameter_list|(
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|extIds
parameter_list|)
block|{
name|delegate
operator|.
name|updateExternalIds
argument_list|(
name|extIds
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|deletedExternalIdsBuilder ()
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|ExternalId
argument_list|>
name|deletedExternalIdsBuilder
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|deletedExternalIdsBuilder
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|deleteExternalIds (Collection<ExternalId> extIds)
specifier|public
name|Builder
name|deleteExternalIds
parameter_list|(
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|extIds
parameter_list|)
block|{
name|delegate
operator|.
name|deleteExternalIds
argument_list|(
name|extIds
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

