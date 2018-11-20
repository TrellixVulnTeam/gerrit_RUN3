begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.common.data
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|data
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
name|truth
operator|.
name|Truth
operator|.
name|assertThat
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
name|ImmutableList
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
name|testing
operator|.
name|GerritBaseTests
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|AccessSectionTest
specifier|public
class|class
name|AccessSectionTest
extends|extends
name|GerritBaseTests
block|{
DECL|field|REF_PATTERN
specifier|private
specifier|static
specifier|final
name|String
name|REF_PATTERN
init|=
literal|"refs/heads/master"
decl_stmt|;
DECL|field|accessSection
specifier|private
name|AccessSection
name|accessSection
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|this
operator|.
name|accessSection
operator|=
operator|new
name|AccessSection
argument_list|(
name|REF_PATTERN
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getName ()
specifier|public
name|void
name|getName
parameter_list|()
block|{
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|REF_PATTERN
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getEmptyPermissions ()
specifier|public
name|void
name|getEmptyPermissions
parameter_list|()
block|{
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermissions
argument_list|()
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermissions
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|setAndGetPermissions ()
specifier|public
name|void
name|setAndGetPermissions
parameter_list|()
block|{
name|Permission
name|abandonPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|ABANDON
argument_list|)
decl_stmt|;
name|Permission
name|rebasePermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|REBASE
argument_list|)
decl_stmt|;
name|accessSection
operator|.
name|setPermissions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|abandonPermission
argument_list|,
name|rebasePermission
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermissions
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|abandonPermission
argument_list|,
name|rebasePermission
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|Permission
name|submitPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
decl_stmt|;
name|accessSection
operator|.
name|setPermissions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|submitPermission
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermissions
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|submitPermission
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|accessSection
operator|.
name|setPermissions
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|cannotSetDuplicatePermissions ()
specifier|public
name|void
name|cannotSetDuplicatePermissions
parameter_list|()
block|{
name|exception
operator|.
name|expect
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
name|accessSection
operator|.
name|setPermissions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|ABANDON
argument_list|)
argument_list|,
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|ABANDON
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|cannotSetPermissionsWithConflictingNames ()
specifier|public
name|void
name|cannotSetPermissionsWithConflictingNames
parameter_list|()
block|{
name|Permission
name|abandonPermissionLowerCase
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|ABANDON
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|)
decl_stmt|;
name|Permission
name|abandonPermissionUpperCase
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|ABANDON
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|)
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
name|accessSection
operator|.
name|setPermissions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|abandonPermissionLowerCase
argument_list|,
name|abandonPermissionUpperCase
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getNonExistingPermission ()
specifier|public
name|void
name|getNonExistingPermission
parameter_list|()
block|{
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
literal|"non-existing"
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
literal|"non-existing"
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getPermission ()
specifier|public
name|void
name|getPermission
parameter_list|()
block|{
name|Permission
name|submitPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
decl_stmt|;
name|accessSection
operator|.
name|setPermissions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|submitPermission
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|submitPermission
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|accessSection
operator|.
name|getPermission
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getPermissionWithOtherCase ()
specifier|public
name|void
name|getPermissionWithOtherCase
parameter_list|()
block|{
name|Permission
name|submitPermissionLowerCase
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|SUBMIT
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|)
decl_stmt|;
name|accessSection
operator|.
name|setPermissions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|submitPermissionLowerCase
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|submitPermissionLowerCase
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|createMissingPermissionOnGet ()
specifier|public
name|void
name|createMissingPermissionOnGet
parameter_list|()
block|{
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|accessSection
operator|.
name|getPermission
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addPermission ()
specifier|public
name|void
name|addPermission
parameter_list|()
block|{
name|Permission
name|abandonPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|ABANDON
argument_list|)
decl_stmt|;
name|Permission
name|rebasePermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|REBASE
argument_list|)
decl_stmt|;
name|accessSection
operator|.
name|setPermissions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|abandonPermission
argument_list|,
name|rebasePermission
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|Permission
name|submitPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
decl_stmt|;
name|accessSection
operator|.
name|addPermission
argument_list|(
name|submitPermission
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|submitPermission
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermissions
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|abandonPermission
argument_list|,
name|rebasePermission
argument_list|,
name|submitPermission
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|accessSection
operator|.
name|addPermission
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|cannotAddPermissionByModifyingListThatWasProvidedToAccessSection ()
specifier|public
name|void
name|cannotAddPermissionByModifyingListThatWasProvidedToAccessSection
parameter_list|()
block|{
name|Permission
name|abandonPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|ABANDON
argument_list|)
decl_stmt|;
name|Permission
name|rebasePermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|REBASE
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Permission
argument_list|>
name|permissions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|permissions
operator|.
name|add
argument_list|(
name|abandonPermission
argument_list|)
expr_stmt|;
name|permissions
operator|.
name|add
argument_list|(
name|rebasePermission
argument_list|)
expr_stmt|;
name|accessSection
operator|.
name|setPermissions
argument_list|(
name|permissions
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|Permission
name|submitPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
decl_stmt|;
name|permissions
operator|.
name|add
argument_list|(
name|submitPermission
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|cannotAddPermissionByModifyingListThatWasRetrievedFromAccessSection ()
specifier|public
name|void
name|cannotAddPermissionByModifyingListThatWasRetrievedFromAccessSection
parameter_list|()
block|{
name|Permission
name|submitPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
decl_stmt|;
name|accessSection
operator|.
name|getPermissions
argument_list|()
operator|.
name|add
argument_list|(
name|submitPermission
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Permission
argument_list|>
name|permissions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|permissions
operator|.
name|add
argument_list|(
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|ABANDON
argument_list|)
argument_list|)
expr_stmt|;
name|permissions
operator|.
name|add
argument_list|(
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|REBASE
argument_list|)
argument_list|)
expr_stmt|;
name|accessSection
operator|.
name|setPermissions
argument_list|(
name|permissions
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|accessSection
operator|.
name|getPermissions
argument_list|()
operator|.
name|add
argument_list|(
name|submitPermission
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|removePermission ()
specifier|public
name|void
name|removePermission
parameter_list|()
block|{
name|Permission
name|abandonPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|ABANDON
argument_list|)
decl_stmt|;
name|Permission
name|rebasePermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|REBASE
argument_list|)
decl_stmt|;
name|Permission
name|submitPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
decl_stmt|;
name|accessSection
operator|.
name|setPermissions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|abandonPermission
argument_list|,
name|rebasePermission
argument_list|,
name|submitPermission
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|accessSection
operator|.
name|remove
argument_list|(
name|submitPermission
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermissions
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|abandonPermission
argument_list|,
name|rebasePermission
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|accessSection
operator|.
name|remove
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|removePermissionByName ()
specifier|public
name|void
name|removePermissionByName
parameter_list|()
block|{
name|Permission
name|abandonPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|ABANDON
argument_list|)
decl_stmt|;
name|Permission
name|rebasePermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|REBASE
argument_list|)
decl_stmt|;
name|Permission
name|submitPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
decl_stmt|;
name|accessSection
operator|.
name|setPermissions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|abandonPermission
argument_list|,
name|rebasePermission
argument_list|,
name|submitPermission
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|accessSection
operator|.
name|removePermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermissions
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|abandonPermission
argument_list|,
name|rebasePermission
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|accessSection
operator|.
name|removePermission
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|removePermissionByNameOtherCase ()
specifier|public
name|void
name|removePermissionByNameOtherCase
parameter_list|()
block|{
name|Permission
name|abandonPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|ABANDON
argument_list|)
decl_stmt|;
name|Permission
name|rebasePermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|REBASE
argument_list|)
decl_stmt|;
name|String
name|submitLowerCase
init|=
name|Permission
operator|.
name|SUBMIT
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|String
name|submitUpperCase
init|=
name|Permission
operator|.
name|SUBMIT
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|Permission
name|submitPermissionLowerCase
init|=
operator|new
name|Permission
argument_list|(
name|submitLowerCase
argument_list|)
decl_stmt|;
name|accessSection
operator|.
name|setPermissions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|abandonPermission
argument_list|,
name|rebasePermission
argument_list|,
name|submitPermissionLowerCase
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|submitLowerCase
argument_list|)
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|submitUpperCase
argument_list|)
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|accessSection
operator|.
name|removePermission
argument_list|(
name|submitUpperCase
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|submitLowerCase
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermission
argument_list|(
name|submitUpperCase
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|getPermissions
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|abandonPermission
argument_list|,
name|rebasePermission
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|mergeAccessSections ()
specifier|public
name|void
name|mergeAccessSections
parameter_list|()
block|{
name|Permission
name|abandonPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|ABANDON
argument_list|)
decl_stmt|;
name|Permission
name|rebasePermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|REBASE
argument_list|)
decl_stmt|;
name|Permission
name|submitPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|SUBMIT
argument_list|)
decl_stmt|;
name|AccessSection
name|accessSection1
init|=
operator|new
name|AccessSection
argument_list|(
literal|"refs/heads/foo"
argument_list|)
decl_stmt|;
name|accessSection1
operator|.
name|setPermissions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|abandonPermission
argument_list|,
name|rebasePermission
argument_list|)
argument_list|)
expr_stmt|;
name|AccessSection
name|accessSection2
init|=
operator|new
name|AccessSection
argument_list|(
literal|"refs/heads/bar"
argument_list|)
decl_stmt|;
name|accessSection2
operator|.
name|setPermissions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|rebasePermission
argument_list|,
name|submitPermission
argument_list|)
argument_list|)
expr_stmt|;
name|accessSection1
operator|.
name|mergeFrom
argument_list|(
name|accessSection2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection1
operator|.
name|getPermissions
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|abandonPermission
argument_list|,
name|rebasePermission
argument_list|,
name|submitPermission
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|)
expr_stmt|;
name|accessSection
operator|.
name|mergeFrom
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEquals ()
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|Permission
name|abandonPermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|ABANDON
argument_list|)
decl_stmt|;
name|Permission
name|rebasePermission
init|=
operator|new
name|Permission
argument_list|(
name|Permission
operator|.
name|REBASE
argument_list|)
decl_stmt|;
name|accessSection
operator|.
name|setPermissions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|abandonPermission
argument_list|,
name|rebasePermission
argument_list|)
argument_list|)
expr_stmt|;
name|AccessSection
name|accessSectionSamePermissionsOtherRef
init|=
operator|new
name|AccessSection
argument_list|(
literal|"refs/heads/other"
argument_list|)
decl_stmt|;
name|accessSectionSamePermissionsOtherRef
operator|.
name|setPermissions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|abandonPermission
argument_list|,
name|rebasePermission
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|equals
argument_list|(
name|accessSectionSamePermissionsOtherRef
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|AccessSection
name|accessSectionOther
init|=
operator|new
name|AccessSection
argument_list|(
name|REF_PATTERN
argument_list|)
decl_stmt|;
name|accessSectionOther
operator|.
name|setPermissions
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|abandonPermission
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|equals
argument_list|(
name|accessSectionOther
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|accessSectionOther
operator|.
name|addPermission
argument_list|(
name|rebasePermission
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|accessSection
operator|.
name|equals
argument_list|(
name|accessSectionOther
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

