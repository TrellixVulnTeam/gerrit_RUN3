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
name|resources
operator|.
name|client
operator|.
name|CssResource
import|;
end_import

begin_interface
DECL|interface|GerritCss
specifier|public
interface|interface
name|GerritCss
extends|extends
name|CssResource
block|{
DECL|method|accountDashboard ()
name|String
name|accountDashboard
parameter_list|()
function_decl|;
DECL|method|accountInfoBlock ()
name|String
name|accountInfoBlock
parameter_list|()
function_decl|;
DECL|method|accountLinkPanel ()
name|String
name|accountLinkPanel
parameter_list|()
function_decl|;
DECL|method|accountPassword ()
name|String
name|accountPassword
parameter_list|()
function_decl|;
DECL|method|accountUsername ()
name|String
name|accountUsername
parameter_list|()
function_decl|;
DECL|method|activeRow ()
name|String
name|activeRow
parameter_list|()
function_decl|;
DECL|method|addBranch ()
name|String
name|addBranch
parameter_list|()
function_decl|;
DECL|method|addMemberTextBox ()
name|String
name|addMemberTextBox
parameter_list|()
function_decl|;
DECL|method|addSshKeyPanel ()
name|String
name|addSshKeyPanel
parameter_list|()
function_decl|;
DECL|method|addWatchPanel ()
name|String
name|addWatchPanel
parameter_list|()
function_decl|;
DECL|method|avatarInfoPanel ()
name|String
name|avatarInfoPanel
parameter_list|()
function_decl|;
DECL|method|bottomheader ()
name|String
name|bottomheader
parameter_list|()
function_decl|;
DECL|method|branchTableDeleteButton ()
name|String
name|branchTableDeleteButton
parameter_list|()
function_decl|;
DECL|method|branchTablePrevNextLinks ()
name|String
name|branchTablePrevNextLinks
parameter_list|()
function_decl|;
DECL|method|cAPPROVAL ()
name|String
name|cAPPROVAL
parameter_list|()
function_decl|;
DECL|method|cLastUpdate ()
name|String
name|cLastUpdate
parameter_list|()
function_decl|;
DECL|method|cOWNER ()
name|String
name|cOWNER
parameter_list|()
function_decl|;
DECL|method|cSIZE ()
name|String
name|cSIZE
parameter_list|()
function_decl|;
DECL|method|cSUBJECT ()
name|String
name|cSUBJECT
parameter_list|()
function_decl|;
DECL|method|cSTATUS ()
name|String
name|cSTATUS
parameter_list|()
function_decl|;
DECL|method|cellsNextToFileComment ()
name|String
name|cellsNextToFileComment
parameter_list|()
function_decl|;
DECL|method|changeScreenDescription ()
name|String
name|changeScreenDescription
parameter_list|()
function_decl|;
DECL|method|changeScreenStarIcon ()
name|String
name|changeScreenStarIcon
parameter_list|()
function_decl|;
DECL|method|changeSize ()
name|String
name|changeSize
parameter_list|()
function_decl|;
DECL|method|changeTable ()
name|String
name|changeTable
parameter_list|()
function_decl|;
DECL|method|changeTablePrevNextLinks ()
name|String
name|changeTablePrevNextLinks
parameter_list|()
function_decl|;
DECL|method|changeTypeCell ()
name|String
name|changeTypeCell
parameter_list|()
function_decl|;
DECL|method|commentCell ()
name|String
name|commentCell
parameter_list|()
function_decl|;
DECL|method|commentEditorPanel ()
name|String
name|commentEditorPanel
parameter_list|()
function_decl|;
DECL|method|commentHolder ()
name|String
name|commentHolder
parameter_list|()
function_decl|;
DECL|method|commentHolderLeftmost ()
name|String
name|commentHolderLeftmost
parameter_list|()
function_decl|;
DECL|method|commentPanel ()
name|String
name|commentPanel
parameter_list|()
function_decl|;
DECL|method|commentPanelAuthorCell ()
name|String
name|commentPanelAuthorCell
parameter_list|()
function_decl|;
DECL|method|commentPanelButtons ()
name|String
name|commentPanelButtons
parameter_list|()
function_decl|;
DECL|method|commentPanelContent ()
name|String
name|commentPanelContent
parameter_list|()
function_decl|;
DECL|method|commentPanelDateCell ()
name|String
name|commentPanelDateCell
parameter_list|()
function_decl|;
DECL|method|commentPanelHeader ()
name|String
name|commentPanelHeader
parameter_list|()
function_decl|;
DECL|method|commentPanelLast ()
name|String
name|commentPanelLast
parameter_list|()
function_decl|;
DECL|method|commentPanelMessage ()
name|String
name|commentPanelMessage
parameter_list|()
function_decl|;
DECL|method|commentPanelSummary ()
name|String
name|commentPanelSummary
parameter_list|()
function_decl|;
DECL|method|commentPanelSummaryCell ()
name|String
name|commentPanelSummaryCell
parameter_list|()
function_decl|;
DECL|method|commentedActionDialog ()
name|String
name|commentedActionDialog
parameter_list|()
function_decl|;
DECL|method|commentedActionMessage ()
name|String
name|commentedActionMessage
parameter_list|()
function_decl|;
DECL|method|contributorAgreementAlreadySubmitted ()
name|String
name|contributorAgreementAlreadySubmitted
parameter_list|()
function_decl|;
DECL|method|contributorAgreementButton ()
name|String
name|contributorAgreementButton
parameter_list|()
function_decl|;
DECL|method|contributorAgreementLegal ()
name|String
name|contributorAgreementLegal
parameter_list|()
function_decl|;
DECL|method|contributorAgreementShortDescription ()
name|String
name|contributorAgreementShortDescription
parameter_list|()
function_decl|;
DECL|method|createProjectPanel ()
name|String
name|createProjectPanel
parameter_list|()
function_decl|;
DECL|method|dataCell ()
name|String
name|dataCell
parameter_list|()
function_decl|;
DECL|method|dataCellHidden ()
name|String
name|dataCellHidden
parameter_list|()
function_decl|;
DECL|method|dataHeader ()
name|String
name|dataHeader
parameter_list|()
function_decl|;
DECL|method|dataHeaderHidden ()
name|String
name|dataHeaderHidden
parameter_list|()
function_decl|;
DECL|method|diffLinkCell ()
name|String
name|diffLinkCell
parameter_list|()
function_decl|;
DECL|method|diffText ()
name|String
name|diffText
parameter_list|()
function_decl|;
DECL|method|diffTextCONTEXT ()
name|String
name|diffTextCONTEXT
parameter_list|()
function_decl|;
DECL|method|diffTextDELETE ()
name|String
name|diffTextDELETE
parameter_list|()
function_decl|;
DECL|method|diffTextFileHeader ()
name|String
name|diffTextFileHeader
parameter_list|()
function_decl|;
DECL|method|diffTextHunkHeader ()
name|String
name|diffTextHunkHeader
parameter_list|()
function_decl|;
DECL|method|diffTextINSERT ()
name|String
name|diffTextINSERT
parameter_list|()
function_decl|;
DECL|method|diffTextNoLF ()
name|String
name|diffTextNoLF
parameter_list|()
function_decl|;
DECL|method|downloadBox ()
name|String
name|downloadBox
parameter_list|()
function_decl|;
DECL|method|downloadBoxTable ()
name|String
name|downloadBoxTable
parameter_list|()
function_decl|;
DECL|method|downloadBoxTableCommandColumn ()
name|String
name|downloadBoxTableCommandColumn
parameter_list|()
function_decl|;
DECL|method|downloadBoxSpacer ()
name|String
name|downloadBoxSpacer
parameter_list|()
function_decl|;
DECL|method|downloadBoxScheme ()
name|String
name|downloadBoxScheme
parameter_list|()
function_decl|;
DECL|method|downloadBoxCopyLabel ()
name|String
name|downloadBoxCopyLabel
parameter_list|()
function_decl|;
DECL|method|downloadLink ()
name|String
name|downloadLink
parameter_list|()
function_decl|;
DECL|method|downloadLinkCopyLabel ()
name|String
name|downloadLinkCopyLabel
parameter_list|()
function_decl|;
DECL|method|downloadLinkHeader ()
name|String
name|downloadLinkHeader
parameter_list|()
function_decl|;
DECL|method|downloadLinkHeaderGap ()
name|String
name|downloadLinkHeaderGap
parameter_list|()
function_decl|;
DECL|method|downloadLinkList ()
name|String
name|downloadLinkList
parameter_list|()
function_decl|;
DECL|method|downloadLink_Active ()
name|String
name|downloadLink_Active
parameter_list|()
function_decl|;
DECL|method|drafts ()
name|String
name|drafts
parameter_list|()
function_decl|;
DECL|method|editHeadButton ()
name|String
name|editHeadButton
parameter_list|()
function_decl|;
DECL|method|emptySection ()
name|String
name|emptySection
parameter_list|()
function_decl|;
DECL|method|errorDialog ()
name|String
name|errorDialog
parameter_list|()
function_decl|;
DECL|method|errorDialogButtons ()
name|String
name|errorDialogButtons
parameter_list|()
function_decl|;
DECL|method|errorDialogErrorType ()
name|String
name|errorDialogErrorType
parameter_list|()
function_decl|;
DECL|method|errorDialogGlass ()
name|String
name|errorDialogGlass
parameter_list|()
function_decl|;
DECL|method|errorDialogTitle ()
name|String
name|errorDialogTitle
parameter_list|()
function_decl|;
DECL|method|extensionPanel ()
name|String
name|extensionPanel
parameter_list|()
function_decl|;
DECL|method|loadingPluginsDialog ()
name|String
name|loadingPluginsDialog
parameter_list|()
function_decl|;
DECL|method|fileColumnHeader ()
name|String
name|fileColumnHeader
parameter_list|()
function_decl|;
DECL|method|fileCommentBorder ()
name|String
name|fileCommentBorder
parameter_list|()
function_decl|;
DECL|method|fileLine ()
name|String
name|fileLine
parameter_list|()
function_decl|;
DECL|method|fileLineDELETE ()
name|String
name|fileLineDELETE
parameter_list|()
function_decl|;
DECL|method|fileLineINSERT ()
name|String
name|fileLineINSERT
parameter_list|()
function_decl|;
DECL|method|filePathCell ()
name|String
name|filePathCell
parameter_list|()
function_decl|;
DECL|method|gerritBody ()
name|String
name|gerritBody
parameter_list|()
function_decl|;
DECL|method|gerritTopMenu ()
name|String
name|gerritTopMenu
parameter_list|()
function_decl|;
DECL|method|greenCheckClass ()
name|String
name|greenCheckClass
parameter_list|()
function_decl|;
DECL|method|groupDescriptionPanel ()
name|String
name|groupDescriptionPanel
parameter_list|()
function_decl|;
DECL|method|groupIncludesTable ()
name|String
name|groupIncludesTable
parameter_list|()
function_decl|;
DECL|method|groupMembersTable ()
name|String
name|groupMembersTable
parameter_list|()
function_decl|;
DECL|method|groupName ()
name|String
name|groupName
parameter_list|()
function_decl|;
DECL|method|groupNamePanel ()
name|String
name|groupNamePanel
parameter_list|()
function_decl|;
DECL|method|groupNameTextBox ()
name|String
name|groupNameTextBox
parameter_list|()
function_decl|;
DECL|method|groupOptionsPanel ()
name|String
name|groupOptionsPanel
parameter_list|()
function_decl|;
DECL|method|groupOwnerPanel ()
name|String
name|groupOwnerPanel
parameter_list|()
function_decl|;
DECL|method|groupOwnerTextBox ()
name|String
name|groupOwnerTextBox
parameter_list|()
function_decl|;
DECL|method|groupUUIDPanel ()
name|String
name|groupUUIDPanel
parameter_list|()
function_decl|;
DECL|method|header ()
name|String
name|header
parameter_list|()
function_decl|;
DECL|method|iconCell ()
name|String
name|iconCell
parameter_list|()
function_decl|;
DECL|method|iconCellOfFileCommentRow ()
name|String
name|iconCellOfFileCommentRow
parameter_list|()
function_decl|;
DECL|method|iconHeader ()
name|String
name|iconHeader
parameter_list|()
function_decl|;
DECL|method|identityUntrustedExternalId ()
name|String
name|identityUntrustedExternalId
parameter_list|()
function_decl|;
DECL|method|infoBlock ()
name|String
name|infoBlock
parameter_list|()
function_decl|;
DECL|method|inputFieldTypeHint ()
name|String
name|inputFieldTypeHint
parameter_list|()
function_decl|;
DECL|method|labelNotApplicable ()
name|String
name|labelNotApplicable
parameter_list|()
function_decl|;
DECL|method|leftMostCell ()
name|String
name|leftMostCell
parameter_list|()
function_decl|;
DECL|method|lineNumber ()
name|String
name|lineNumber
parameter_list|()
function_decl|;
DECL|method|link ()
name|String
name|link
parameter_list|()
function_decl|;
DECL|method|linkMenuBar ()
name|String
name|linkMenuBar
parameter_list|()
function_decl|;
DECL|method|linkMenuItemNotLast ()
name|String
name|linkMenuItemNotLast
parameter_list|()
function_decl|;
DECL|method|linkPanel ()
name|String
name|linkPanel
parameter_list|()
function_decl|;
DECL|method|maxObjectSizeLimitEffectiveLabel ()
name|String
name|maxObjectSizeLimitEffectiveLabel
parameter_list|()
function_decl|;
DECL|method|menuBarUserName ()
name|String
name|menuBarUserName
parameter_list|()
function_decl|;
DECL|method|menuBarUserNameAvatar ()
name|String
name|menuBarUserNameAvatar
parameter_list|()
function_decl|;
DECL|method|menuBarUserNameFocusPanel ()
name|String
name|menuBarUserNameFocusPanel
parameter_list|()
function_decl|;
DECL|method|menuBarUserNamePanel ()
name|String
name|menuBarUserNamePanel
parameter_list|()
function_decl|;
DECL|method|menuItem ()
name|String
name|menuItem
parameter_list|()
function_decl|;
DECL|method|menuScreenMenuBar ()
name|String
name|menuScreenMenuBar
parameter_list|()
function_decl|;
DECL|method|needsReview ()
name|String
name|needsReview
parameter_list|()
function_decl|;
DECL|method|negscore ()
name|String
name|negscore
parameter_list|()
function_decl|;
DECL|method|noborder ()
name|String
name|noborder
parameter_list|()
function_decl|;
DECL|method|nowrap ()
name|String
name|nowrap
parameter_list|()
function_decl|;
DECL|method|pagingLink ()
name|String
name|pagingLink
parameter_list|()
function_decl|;
DECL|method|patchBrowserPopup ()
name|String
name|patchBrowserPopup
parameter_list|()
function_decl|;
DECL|method|patchBrowserPopupBody ()
name|String
name|patchBrowserPopupBody
parameter_list|()
function_decl|;
DECL|method|patchCellReverseDiff ()
name|String
name|patchCellReverseDiff
parameter_list|()
function_decl|;
DECL|method|patchContentTable ()
name|String
name|patchContentTable
parameter_list|()
function_decl|;
DECL|method|patchHistoryTable ()
name|String
name|patchHistoryTable
parameter_list|()
function_decl|;
DECL|method|patchHistoryTablePatchSetHeader ()
name|String
name|patchHistoryTablePatchSetHeader
parameter_list|()
function_decl|;
DECL|method|patchNoDifference ()
name|String
name|patchNoDifference
parameter_list|()
function_decl|;
DECL|method|patchSetActions ()
name|String
name|patchSetActions
parameter_list|()
function_decl|;
DECL|method|patchSizeCell ()
name|String
name|patchSizeCell
parameter_list|()
function_decl|;
DECL|method|pluginProjectConfigInheritedValue ()
name|String
name|pluginProjectConfigInheritedValue
parameter_list|()
function_decl|;
DECL|method|pluginsTable ()
name|String
name|pluginsTable
parameter_list|()
function_decl|;
DECL|method|posscore ()
name|String
name|posscore
parameter_list|()
function_decl|;
DECL|method|projectActions ()
name|String
name|projectActions
parameter_list|()
function_decl|;
DECL|method|projectFilterLabel ()
name|String
name|projectFilterLabel
parameter_list|()
function_decl|;
DECL|method|projectFilterPanel ()
name|String
name|projectFilterPanel
parameter_list|()
function_decl|;
DECL|method|projectNameColumn ()
name|String
name|projectNameColumn
parameter_list|()
function_decl|;
DECL|method|rebaseContentPanel ()
name|String
name|rebaseContentPanel
parameter_list|()
function_decl|;
DECL|method|rebaseSuggestBox ()
name|String
name|rebaseSuggestBox
parameter_list|()
function_decl|;
DECL|method|registerScreenExplain ()
name|String
name|registerScreenExplain
parameter_list|()
function_decl|;
DECL|method|registerScreenNextLinks ()
name|String
name|registerScreenNextLinks
parameter_list|()
function_decl|;
DECL|method|registerScreenSection ()
name|String
name|registerScreenSection
parameter_list|()
function_decl|;
DECL|method|reviewedPanelBottom ()
name|String
name|reviewedPanelBottom
parameter_list|()
function_decl|;
DECL|method|rightBorder ()
name|String
name|rightBorder
parameter_list|()
function_decl|;
DECL|method|rpcStatus ()
name|String
name|rpcStatus
parameter_list|()
function_decl|;
DECL|method|screen ()
name|String
name|screen
parameter_list|()
function_decl|;
DECL|method|screenHeader ()
name|String
name|screenHeader
parameter_list|()
function_decl|;
DECL|method|searchPanel ()
name|String
name|searchPanel
parameter_list|()
function_decl|;
DECL|method|suggestBoxPopup ()
name|String
name|suggestBoxPopup
parameter_list|()
function_decl|;
DECL|method|sectionHeader ()
name|String
name|sectionHeader
parameter_list|()
function_decl|;
DECL|method|sideBySideScreenLinkTable ()
name|String
name|sideBySideScreenLinkTable
parameter_list|()
function_decl|;
DECL|method|singleLine ()
name|String
name|singleLine
parameter_list|()
function_decl|;
DECL|method|smallHeading ()
name|String
name|smallHeading
parameter_list|()
function_decl|;
DECL|method|sourceFilePath ()
name|String
name|sourceFilePath
parameter_list|()
function_decl|;
DECL|method|specialBranchDataCell ()
name|String
name|specialBranchDataCell
parameter_list|()
function_decl|;
DECL|method|specialBranchIconCell ()
name|String
name|specialBranchIconCell
parameter_list|()
function_decl|;
DECL|method|sshHostKeyPanel ()
name|String
name|sshHostKeyPanel
parameter_list|()
function_decl|;
DECL|method|sshHostKeyPanelFingerprintData ()
name|String
name|sshHostKeyPanelFingerprintData
parameter_list|()
function_decl|;
DECL|method|sshHostKeyPanelHeading ()
name|String
name|sshHostKeyPanelHeading
parameter_list|()
function_decl|;
DECL|method|sshHostKeyPanelKnownHostEntry ()
name|String
name|sshHostKeyPanelKnownHostEntry
parameter_list|()
function_decl|;
DECL|method|sshKeyPanelEncodedKey ()
name|String
name|sshKeyPanelEncodedKey
parameter_list|()
function_decl|;
DECL|method|sshKeyPanelInvalid ()
name|String
name|sshKeyPanelInvalid
parameter_list|()
function_decl|;
DECL|method|sshKeyTable ()
name|String
name|sshKeyTable
parameter_list|()
function_decl|;
DECL|method|stringListPanelButtons ()
name|String
name|stringListPanelButtons
parameter_list|()
function_decl|;
DECL|method|topMostCell ()
name|String
name|topMostCell
parameter_list|()
function_decl|;
DECL|method|topmenu ()
name|String
name|topmenu
parameter_list|()
function_decl|;
DECL|method|topmenuMenuLeft ()
name|String
name|topmenuMenuLeft
parameter_list|()
function_decl|;
DECL|method|topmenuMenuRight ()
name|String
name|topmenuMenuRight
parameter_list|()
function_decl|;
DECL|method|topmenuTDglue ()
name|String
name|topmenuTDglue
parameter_list|()
function_decl|;
DECL|method|topmenuTDmenu ()
name|String
name|topmenuTDmenu
parameter_list|()
function_decl|;
DECL|method|topmost ()
name|String
name|topmost
parameter_list|()
function_decl|;
DECL|method|unifiedTable ()
name|String
name|unifiedTable
parameter_list|()
function_decl|;
DECL|method|unifiedTableHeader ()
name|String
name|unifiedTableHeader
parameter_list|()
function_decl|;
DECL|method|userInfoPopup ()
name|String
name|userInfoPopup
parameter_list|()
function_decl|;
DECL|method|usernameField ()
name|String
name|usernameField
parameter_list|()
function_decl|;
DECL|method|watchedProjectFilter ()
name|String
name|watchedProjectFilter
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

