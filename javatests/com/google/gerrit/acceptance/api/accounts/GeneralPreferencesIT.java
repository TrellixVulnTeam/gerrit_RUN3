begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.api.accounts
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|api
operator|.
name|accounts
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
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|AssertUtil
operator|.
name|assertPrefs
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
name|acceptance
operator|.
name|AbstractDaemonTest
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
name|acceptance
operator|.
name|NoHttpd
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
name|acceptance
operator|.
name|TestAccount
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
name|client
operator|.
name|GeneralPreferencesInfo
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
name|client
operator|.
name|GeneralPreferencesInfo
operator|.
name|DateFormat
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
name|client
operator|.
name|GeneralPreferencesInfo
operator|.
name|DefaultBase
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
name|client
operator|.
name|GeneralPreferencesInfo
operator|.
name|DiffView
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
name|client
operator|.
name|GeneralPreferencesInfo
operator|.
name|DownloadCommand
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
name|client
operator|.
name|GeneralPreferencesInfo
operator|.
name|EmailFormat
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
name|client
operator|.
name|GeneralPreferencesInfo
operator|.
name|EmailStrategy
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
name|client
operator|.
name|GeneralPreferencesInfo
operator|.
name|ReviewCategoryStrategy
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
name|client
operator|.
name|GeneralPreferencesInfo
operator|.
name|TimeFormat
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
name|client
operator|.
name|MenuItem
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
name|config
operator|.
name|DownloadScheme
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
name|registration
operator|.
name|DynamicMap
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
name|registration
operator|.
name|PrivateInternals_DynamicMapImpl
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
name|registration
operator|.
name|RegistrationHandle
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
name|BadRequestException
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
annotation|@
name|NoHttpd
DECL|class|GeneralPreferencesIT
specifier|public
class|class
name|GeneralPreferencesIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|downloadSchemes
annotation|@
name|Inject
specifier|private
name|DynamicMap
argument_list|<
name|DownloadScheme
argument_list|>
name|downloadSchemes
decl_stmt|;
DECL|field|user42
specifier|private
name|TestAccount
name|user42
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|name
init|=
name|name
argument_list|(
literal|"user42"
argument_list|)
decl_stmt|;
name|user42
operator|=
name|accountCreator
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|name
operator|+
literal|"@example.com"
argument_list|,
literal|"User 42"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getAndSetPreferences ()
specifier|public
name|void
name|getAndSetPreferences
parameter_list|()
throws|throws
name|Exception
block|{
name|GeneralPreferencesInfo
name|o
init|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|user42
operator|.
name|id
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getPreferences
argument_list|()
decl_stmt|;
name|assertPrefs
argument_list|(
name|o
argument_list|,
name|GeneralPreferencesInfo
operator|.
name|defaults
argument_list|()
argument_list|,
literal|"my"
argument_list|,
literal|"changeTable"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|my
argument_list|)
operator|.
name|containsExactly
argument_list|(
operator|new
name|MenuItem
argument_list|(
literal|"Changes"
argument_list|,
literal|"#/dashboard/self"
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|new
name|MenuItem
argument_list|(
literal|"Draft Comments"
argument_list|,
literal|"#/q/has:draft"
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|new
name|MenuItem
argument_list|(
literal|"Edits"
argument_list|,
literal|"#/q/has:edit"
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|new
name|MenuItem
argument_list|(
literal|"Watched Changes"
argument_list|,
literal|"#/q/is:watched+is:open"
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|new
name|MenuItem
argument_list|(
literal|"Starred Changes"
argument_list|,
literal|"#/q/is:starred"
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|new
name|MenuItem
argument_list|(
literal|"Groups"
argument_list|,
literal|"#/groups/self"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|changeTable
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|GeneralPreferencesInfo
name|i
init|=
name|GeneralPreferencesInfo
operator|.
name|defaults
argument_list|()
decl_stmt|;
comment|// change all default values
name|i
operator|.
name|changesPerPage
operator|*=
operator|-
literal|1
expr_stmt|;
name|i
operator|.
name|showSiteHeader
operator|^=
literal|true
expr_stmt|;
name|i
operator|.
name|useFlashClipboard
operator|^=
literal|true
expr_stmt|;
name|i
operator|.
name|downloadCommand
operator|=
name|DownloadCommand
operator|.
name|REPO_DOWNLOAD
expr_stmt|;
name|i
operator|.
name|dateFormat
operator|=
name|DateFormat
operator|.
name|US
expr_stmt|;
name|i
operator|.
name|timeFormat
operator|=
name|TimeFormat
operator|.
name|HHMM_24
expr_stmt|;
name|i
operator|.
name|emailStrategy
operator|=
name|EmailStrategy
operator|.
name|DISABLED
expr_stmt|;
name|i
operator|.
name|emailFormat
operator|=
name|EmailFormat
operator|.
name|PLAINTEXT
expr_stmt|;
name|i
operator|.
name|defaultBaseForMerges
operator|=
name|DefaultBase
operator|.
name|AUTO_MERGE
expr_stmt|;
name|i
operator|.
name|expandInlineDiffs
operator|^=
literal|true
expr_stmt|;
name|i
operator|.
name|highlightAssigneeInChangeTable
operator|^=
literal|true
expr_stmt|;
name|i
operator|.
name|relativeDateInChangeTable
operator|^=
literal|true
expr_stmt|;
name|i
operator|.
name|sizeBarInChangeTable
operator|^=
literal|true
expr_stmt|;
name|i
operator|.
name|legacycidInChangeTable
operator|^=
literal|true
expr_stmt|;
name|i
operator|.
name|muteCommonPathPrefixes
operator|^=
literal|true
expr_stmt|;
name|i
operator|.
name|signedOffBy
operator|^=
literal|true
expr_stmt|;
name|i
operator|.
name|reviewCategoryStrategy
operator|=
name|ReviewCategoryStrategy
operator|.
name|ABBREV
expr_stmt|;
name|i
operator|.
name|diffView
operator|=
name|DiffView
operator|.
name|UNIFIED_DIFF
expr_stmt|;
name|i
operator|.
name|my
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|i
operator|.
name|my
operator|.
name|add
argument_list|(
operator|new
name|MenuItem
argument_list|(
literal|"name"
argument_list|,
literal|"url"
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|.
name|changeTable
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|i
operator|.
name|changeTable
operator|.
name|add
argument_list|(
literal|"Status"
argument_list|)
expr_stmt|;
name|i
operator|.
name|urlAliases
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|i
operator|.
name|urlAliases
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|o
operator|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|user42
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setPreferences
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertPrefs
argument_list|(
name|o
argument_list|,
name|i
argument_list|,
literal|"my"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|my
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|i
operator|.
name|my
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|changeTable
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|i
operator|.
name|changeTable
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getPreferencesWithConfiguredDefaults ()
specifier|public
name|void
name|getPreferencesWithConfiguredDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|GeneralPreferencesInfo
name|d
init|=
name|GeneralPreferencesInfo
operator|.
name|defaults
argument_list|()
decl_stmt|;
name|int
name|newChangesPerPage
init|=
name|d
operator|.
name|changesPerPage
operator|*
literal|2
decl_stmt|;
name|GeneralPreferencesInfo
name|update
init|=
operator|new
name|GeneralPreferencesInfo
argument_list|()
decl_stmt|;
name|update
operator|.
name|changesPerPage
operator|=
name|newChangesPerPage
expr_stmt|;
name|gApi
operator|.
name|config
argument_list|()
operator|.
name|server
argument_list|()
operator|.
name|setDefaultPreferences
argument_list|(
name|update
argument_list|)
expr_stmt|;
name|GeneralPreferencesInfo
name|o
init|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|user42
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getPreferences
argument_list|()
decl_stmt|;
comment|// assert configured defaults
name|assertThat
argument_list|(
name|o
operator|.
name|changesPerPage
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|newChangesPerPage
argument_list|)
expr_stmt|;
comment|// assert hard-coded defaults
name|assertPrefs
argument_list|(
name|o
argument_list|,
name|d
argument_list|,
literal|"my"
argument_list|,
literal|"changeTable"
argument_list|,
literal|"changesPerPage"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|overwriteConfiguredDefaults ()
specifier|public
name|void
name|overwriteConfiguredDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|GeneralPreferencesInfo
name|d
init|=
name|GeneralPreferencesInfo
operator|.
name|defaults
argument_list|()
decl_stmt|;
name|int
name|configuredChangesPerPage
init|=
name|d
operator|.
name|changesPerPage
operator|*
literal|2
decl_stmt|;
name|GeneralPreferencesInfo
name|update
init|=
operator|new
name|GeneralPreferencesInfo
argument_list|()
decl_stmt|;
name|update
operator|.
name|changesPerPage
operator|=
name|configuredChangesPerPage
expr_stmt|;
name|gApi
operator|.
name|config
argument_list|()
operator|.
name|server
argument_list|()
operator|.
name|setDefaultPreferences
argument_list|(
name|update
argument_list|)
expr_stmt|;
name|GeneralPreferencesInfo
name|o
init|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getPreferences
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|changesPerPage
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|configuredChangesPerPage
argument_list|)
expr_stmt|;
name|assertPrefs
argument_list|(
name|o
argument_list|,
name|d
argument_list|,
literal|"my"
argument_list|,
literal|"changeTable"
argument_list|,
literal|"changesPerPage"
argument_list|)
expr_stmt|;
name|int
name|newChangesPerPage
init|=
name|configuredChangesPerPage
operator|*
literal|2
decl_stmt|;
name|GeneralPreferencesInfo
name|i
init|=
operator|new
name|GeneralPreferencesInfo
argument_list|()
decl_stmt|;
name|i
operator|.
name|changesPerPage
operator|=
name|newChangesPerPage
expr_stmt|;
name|GeneralPreferencesInfo
name|a
init|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setPreferences
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|a
operator|.
name|changesPerPage
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|newChangesPerPage
argument_list|)
expr_stmt|;
name|assertPrefs
argument_list|(
name|a
argument_list|,
name|d
argument_list|,
literal|"my"
argument_list|,
literal|"changeTable"
argument_list|,
literal|"changesPerPage"
argument_list|)
expr_stmt|;
name|a
operator|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getPreferences
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|a
operator|.
name|changesPerPage
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|newChangesPerPage
argument_list|)
expr_stmt|;
name|assertPrefs
argument_list|(
name|a
argument_list|,
name|d
argument_list|,
literal|"my"
argument_list|,
literal|"changeTable"
argument_list|,
literal|"changesPerPage"
argument_list|)
expr_stmt|;
comment|// overwrite the configured default with original hard-coded default
name|i
operator|=
operator|new
name|GeneralPreferencesInfo
argument_list|()
expr_stmt|;
name|i
operator|.
name|changesPerPage
operator|=
name|d
operator|.
name|changesPerPage
expr_stmt|;
name|a
operator|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setPreferences
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|a
operator|.
name|changesPerPage
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|d
operator|.
name|changesPerPage
argument_list|)
expr_stmt|;
name|assertPrefs
argument_list|(
name|a
argument_list|,
name|d
argument_list|,
literal|"my"
argument_list|,
literal|"changeTable"
argument_list|,
literal|"changesPerPage"
argument_list|)
expr_stmt|;
name|a
operator|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|admin
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getPreferences
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|a
operator|.
name|changesPerPage
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|d
operator|.
name|changesPerPage
argument_list|)
expr_stmt|;
name|assertPrefs
argument_list|(
name|a
argument_list|,
name|d
argument_list|,
literal|"my"
argument_list|,
literal|"changeTable"
argument_list|,
literal|"changesPerPage"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|rejectMyMenuWithoutName ()
specifier|public
name|void
name|rejectMyMenuWithoutName
parameter_list|()
throws|throws
name|Exception
block|{
name|GeneralPreferencesInfo
name|i
init|=
name|GeneralPreferencesInfo
operator|.
name|defaults
argument_list|()
decl_stmt|;
name|i
operator|.
name|my
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|i
operator|.
name|my
operator|.
name|add
argument_list|(
operator|new
name|MenuItem
argument_list|(
literal|null
argument_list|,
literal|"url"
argument_list|)
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"name for menu item is required"
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|user42
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setPreferences
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|rejectMyMenuWithoutUrl ()
specifier|public
name|void
name|rejectMyMenuWithoutUrl
parameter_list|()
throws|throws
name|Exception
block|{
name|GeneralPreferencesInfo
name|i
init|=
name|GeneralPreferencesInfo
operator|.
name|defaults
argument_list|()
decl_stmt|;
name|i
operator|.
name|my
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|i
operator|.
name|my
operator|.
name|add
argument_list|(
operator|new
name|MenuItem
argument_list|(
literal|"name"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"URL for menu item is required"
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|user42
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setPreferences
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|trimMyMenuInput ()
specifier|public
name|void
name|trimMyMenuInput
parameter_list|()
throws|throws
name|Exception
block|{
name|GeneralPreferencesInfo
name|i
init|=
name|GeneralPreferencesInfo
operator|.
name|defaults
argument_list|()
decl_stmt|;
name|i
operator|.
name|my
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|i
operator|.
name|my
operator|.
name|add
argument_list|(
operator|new
name|MenuItem
argument_list|(
literal|" name\t"
argument_list|,
literal|" url\t"
argument_list|,
literal|" _blank\t"
argument_list|,
literal|" id\t"
argument_list|)
argument_list|)
expr_stmt|;
name|GeneralPreferencesInfo
name|o
init|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|user42
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setPreferences
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|my
argument_list|)
operator|.
name|containsExactly
argument_list|(
operator|new
name|MenuItem
argument_list|(
literal|"name"
argument_list|,
literal|"url"
argument_list|,
literal|"_blank"
argument_list|,
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|rejectUnsupportedDownloadScheme ()
specifier|public
name|void
name|rejectUnsupportedDownloadScheme
parameter_list|()
throws|throws
name|Exception
block|{
name|GeneralPreferencesInfo
name|i
init|=
name|GeneralPreferencesInfo
operator|.
name|defaults
argument_list|()
decl_stmt|;
name|i
operator|.
name|downloadScheme
operator|=
literal|"foo"
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"Unsupported download scheme: "
operator|+
name|i
operator|.
name|downloadScheme
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|user42
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setPreferences
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|setDownloadScheme ()
specifier|public
name|void
name|setDownloadScheme
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|schemeName
init|=
literal|"foo"
decl_stmt|;
name|RegistrationHandle
name|registrationHandle
init|=
operator|(
operator|(
name|PrivateInternals_DynamicMapImpl
argument_list|<
name|DownloadScheme
argument_list|>
operator|)
name|downloadSchemes
operator|)
operator|.
name|put
argument_list|(
literal|"myPlugin"
argument_list|,
name|schemeName
argument_list|,
name|Providers
operator|.
name|of
argument_list|(
operator|new
name|TestDownloadScheme
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|GeneralPreferencesInfo
name|i
init|=
name|GeneralPreferencesInfo
operator|.
name|defaults
argument_list|()
decl_stmt|;
name|i
operator|.
name|downloadScheme
operator|=
name|schemeName
expr_stmt|;
name|GeneralPreferencesInfo
name|o
init|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|user42
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|setPreferences
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|downloadScheme
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|schemeName
argument_list|)
expr_stmt|;
name|o
operator|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|user42
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getPreferences
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|downloadScheme
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|schemeName
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|registrationHandle
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|unsupportedDownloadSchemeIsNotReturned ()
specifier|public
name|void
name|unsupportedDownloadSchemeIsNotReturned
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Set a download scheme and unregister the plugin that provides this download scheme so that it
comment|// becomes unsupported.
name|setDownloadScheme
argument_list|()
expr_stmt|;
name|GeneralPreferencesInfo
name|o
init|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|id
argument_list|(
name|user42
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getPreferences
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|downloadScheme
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
DECL|class|TestDownloadScheme
specifier|private
specifier|static
class|class
name|TestDownloadScheme
extends|extends
name|DownloadScheme
block|{
annotation|@
name|Override
DECL|method|getUrl (String project)
specifier|public
name|String
name|getUrl
parameter_list|(
name|String
name|project
parameter_list|)
block|{
return|return
literal|"http://foo/"
operator|+
name|project
return|;
block|}
annotation|@
name|Override
DECL|method|isAuthRequired ()
specifier|public
name|boolean
name|isAuthRequired
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|isAuthSupported ()
specifier|public
name|boolean
name|isAuthSupported
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|isEnabled ()
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

