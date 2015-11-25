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
DECL|package|com.google.gerrit.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|i18n
operator|.
name|client
operator|.
name|Constants
import|;
end_import

begin_interface
DECL|interface|GerritConstants
specifier|public
interface|interface
name|GerritConstants
extends|extends
name|Constants
block|{
DECL|method|menuSignIn ()
name|String
name|menuSignIn
parameter_list|()
function_decl|;
DECL|method|menuRegister ()
name|String
name|menuRegister
parameter_list|()
function_decl|;
DECL|method|reportBug ()
name|String
name|reportBug
parameter_list|()
function_decl|;
DECL|method|loadingPlugins ()
name|String
name|loadingPlugins
parameter_list|()
function_decl|;
DECL|method|signInDialogTitle ()
name|String
name|signInDialogTitle
parameter_list|()
function_decl|;
DECL|method|signInDialogGoAnonymous ()
name|String
name|signInDialogGoAnonymous
parameter_list|()
function_decl|;
DECL|method|linkIdentityDialogTitle ()
name|String
name|linkIdentityDialogTitle
parameter_list|()
function_decl|;
DECL|method|registerDialogTitle ()
name|String
name|registerDialogTitle
parameter_list|()
function_decl|;
DECL|method|loginTypeUnsupported ()
name|String
name|loginTypeUnsupported
parameter_list|()
function_decl|;
DECL|method|errorTitle ()
name|String
name|errorTitle
parameter_list|()
function_decl|;
DECL|method|errorDialogContinue ()
name|String
name|errorDialogContinue
parameter_list|()
function_decl|;
DECL|method|warnTitle ()
name|String
name|warnTitle
parameter_list|()
function_decl|;
DECL|method|confirmationDialogOk ()
name|String
name|confirmationDialogOk
parameter_list|()
function_decl|;
DECL|method|confirmationDialogCancel ()
name|String
name|confirmationDialogCancel
parameter_list|()
function_decl|;
DECL|method|branchCreationDialogTitle ()
name|String
name|branchCreationDialogTitle
parameter_list|()
function_decl|;
DECL|method|branchCreationConfirmationMessage ()
name|String
name|branchCreationConfirmationMessage
parameter_list|()
function_decl|;
DECL|method|branchDeletionDialogTitle ()
name|String
name|branchDeletionDialogTitle
parameter_list|()
function_decl|;
DECL|method|branchDeletionConfirmationMessage ()
name|String
name|branchDeletionConfirmationMessage
parameter_list|()
function_decl|;
DECL|method|notSignedInTitle ()
name|String
name|notSignedInTitle
parameter_list|()
function_decl|;
DECL|method|notSignedInBody ()
name|String
name|notSignedInBody
parameter_list|()
function_decl|;
DECL|method|notFoundTitle ()
name|String
name|notFoundTitle
parameter_list|()
function_decl|;
DECL|method|notFoundBody ()
name|String
name|notFoundBody
parameter_list|()
function_decl|;
DECL|method|noSuchAccountTitle ()
name|String
name|noSuchAccountTitle
parameter_list|()
function_decl|;
DECL|method|noSuchGroupTitle ()
name|String
name|noSuchGroupTitle
parameter_list|()
function_decl|;
DECL|method|inactiveAccountBody ()
name|String
name|inactiveAccountBody
parameter_list|()
function_decl|;
DECL|method|labelNotApplicable ()
name|String
name|labelNotApplicable
parameter_list|()
function_decl|;
DECL|method|menuAll ()
name|String
name|menuAll
parameter_list|()
function_decl|;
DECL|method|menuAllOpen ()
name|String
name|menuAllOpen
parameter_list|()
function_decl|;
DECL|method|menuAllMerged ()
name|String
name|menuAllMerged
parameter_list|()
function_decl|;
DECL|method|menuAllAbandoned ()
name|String
name|menuAllAbandoned
parameter_list|()
function_decl|;
DECL|method|menuMine ()
name|String
name|menuMine
parameter_list|()
function_decl|;
DECL|method|menuMyChanges ()
name|String
name|menuMyChanges
parameter_list|()
function_decl|;
DECL|method|menuMyDrafts ()
name|String
name|menuMyDrafts
parameter_list|()
function_decl|;
DECL|method|menuMyWatchedChanges ()
name|String
name|menuMyWatchedChanges
parameter_list|()
function_decl|;
DECL|method|menuMyStarredChanges ()
name|String
name|menuMyStarredChanges
parameter_list|()
function_decl|;
DECL|method|menuMyDraftComments ()
name|String
name|menuMyDraftComments
parameter_list|()
function_decl|;
DECL|method|menuDiff ()
name|String
name|menuDiff
parameter_list|()
function_decl|;
DECL|method|menuDiffCommit ()
name|String
name|menuDiffCommit
parameter_list|()
function_decl|;
DECL|method|menuDiffPreferences ()
name|String
name|menuDiffPreferences
parameter_list|()
function_decl|;
DECL|method|menuDiffPatchSets ()
name|String
name|menuDiffPatchSets
parameter_list|()
function_decl|;
DECL|method|menuDiffFiles ()
name|String
name|menuDiffFiles
parameter_list|()
function_decl|;
DECL|method|menuProjects ()
name|String
name|menuProjects
parameter_list|()
function_decl|;
DECL|method|menuProjectsList ()
name|String
name|menuProjectsList
parameter_list|()
function_decl|;
DECL|method|menuProjectsInfo ()
name|String
name|menuProjectsInfo
parameter_list|()
function_decl|;
DECL|method|menuProjectsBranches ()
name|String
name|menuProjectsBranches
parameter_list|()
function_decl|;
DECL|method|menuProjectsTags ()
name|String
name|menuProjectsTags
parameter_list|()
function_decl|;
DECL|method|menuProjectsAccess ()
name|String
name|menuProjectsAccess
parameter_list|()
function_decl|;
DECL|method|menuProjectsDashboards ()
name|String
name|menuProjectsDashboards
parameter_list|()
function_decl|;
DECL|method|menuProjectsCreate ()
name|String
name|menuProjectsCreate
parameter_list|()
function_decl|;
DECL|method|menuPeople ()
name|String
name|menuPeople
parameter_list|()
function_decl|;
DECL|method|menuPeopleGroupsList ()
name|String
name|menuPeopleGroupsList
parameter_list|()
function_decl|;
DECL|method|menuPeopleGroupsCreate ()
name|String
name|menuPeopleGroupsCreate
parameter_list|()
function_decl|;
DECL|method|menuPlugins ()
name|String
name|menuPlugins
parameter_list|()
function_decl|;
DECL|method|menuPluginsInstalled ()
name|String
name|menuPluginsInstalled
parameter_list|()
function_decl|;
DECL|method|menuDocumentation ()
name|String
name|menuDocumentation
parameter_list|()
function_decl|;
DECL|method|menuDocumentationTOC ()
name|String
name|menuDocumentationTOC
parameter_list|()
function_decl|;
DECL|method|menuDocumentationSearch ()
name|String
name|menuDocumentationSearch
parameter_list|()
function_decl|;
DECL|method|menuDocumentationUpload ()
name|String
name|menuDocumentationUpload
parameter_list|()
function_decl|;
DECL|method|menuDocumentationAccess ()
name|String
name|menuDocumentationAccess
parameter_list|()
function_decl|;
DECL|method|menuDocumentationAPI ()
name|String
name|menuDocumentationAPI
parameter_list|()
function_decl|;
DECL|method|menuDocumentationProjectOwnerGuide ()
name|String
name|menuDocumentationProjectOwnerGuide
parameter_list|()
function_decl|;
DECL|method|searchHint ()
name|String
name|searchHint
parameter_list|()
function_decl|;
DECL|method|searchButton ()
name|String
name|searchButton
parameter_list|()
function_decl|;
DECL|method|rpcStatusWorking ()
name|String
name|rpcStatusWorking
parameter_list|()
function_decl|;
DECL|method|sectionNavigation ()
name|String
name|sectionNavigation
parameter_list|()
function_decl|;
DECL|method|sectionActions ()
name|String
name|sectionActions
parameter_list|()
function_decl|;
DECL|method|keySearch ()
name|String
name|keySearch
parameter_list|()
function_decl|;
DECL|method|keyHelp ()
name|String
name|keyHelp
parameter_list|()
function_decl|;
DECL|method|sectionJumping ()
name|String
name|sectionJumping
parameter_list|()
function_decl|;
DECL|method|jumpAllOpen ()
name|String
name|jumpAllOpen
parameter_list|()
function_decl|;
DECL|method|jumpAllMerged ()
name|String
name|jumpAllMerged
parameter_list|()
function_decl|;
DECL|method|jumpAllAbandoned ()
name|String
name|jumpAllAbandoned
parameter_list|()
function_decl|;
DECL|method|jumpMine ()
name|String
name|jumpMine
parameter_list|()
function_decl|;
DECL|method|jumpMineDrafts ()
name|String
name|jumpMineDrafts
parameter_list|()
function_decl|;
DECL|method|jumpMineWatched ()
name|String
name|jumpMineWatched
parameter_list|()
function_decl|;
DECL|method|jumpMineStarred ()
name|String
name|jumpMineStarred
parameter_list|()
function_decl|;
DECL|method|jumpMineDraftComments ()
name|String
name|jumpMineDraftComments
parameter_list|()
function_decl|;
DECL|method|projectAccessError ()
name|String
name|projectAccessError
parameter_list|()
function_decl|;
DECL|method|projectAccessProposeForReviewHint ()
name|String
name|projectAccessProposeForReviewHint
parameter_list|()
function_decl|;
DECL|method|userCannotVoteToolTip ()
name|String
name|userCannotVoteToolTip
parameter_list|()
function_decl|;
DECL|method|stringListPanelAdd ()
name|String
name|stringListPanelAdd
parameter_list|()
function_decl|;
DECL|method|stringListPanelDelete ()
name|String
name|stringListPanelDelete
parameter_list|()
function_decl|;
DECL|method|stringListPanelUp ()
name|String
name|stringListPanelUp
parameter_list|()
function_decl|;
DECL|method|stringListPanelDown ()
name|String
name|stringListPanelDown
parameter_list|()
function_decl|;
DECL|method|searchDropdownChanges ()
name|String
name|searchDropdownChanges
parameter_list|()
function_decl|;
DECL|method|searchDropdownDoc ()
name|String
name|searchDropdownDoc
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

