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
DECL|package|com.google.gerrit.client.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
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
name|gerrit
operator|.
name|client
operator|.
name|ErrorDialog
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
name|Gerrit
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
name|Link
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
name|AccountAgreement
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
name|ContributorAgreement
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
name|rpc
operator|.
name|GerritCallback
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
name|ui
operator|.
name|AccountScreen
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
name|ui
operator|.
name|SmallHeading
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
name|ui
operator|.
name|TextSaveButtonListener
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
name|core
operator|.
name|client
operator|.
name|GWT
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
name|http
operator|.
name|client
operator|.
name|Request
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
name|http
operator|.
name|client
operator|.
name|RequestBuilder
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
name|http
operator|.
name|client
operator|.
name|RequestCallback
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
name|http
operator|.
name|client
operator|.
name|RequestException
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
name|http
operator|.
name|client
operator|.
name|Response
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
name|History
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
name|ui
operator|.
name|Button
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
name|ui
operator|.
name|ClickListener
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
name|ui
operator|.
name|FlowPanel
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
name|ui
operator|.
name|FormPanel
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
name|ui
operator|.
name|HTML
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
name|ui
operator|.
name|InlineLabel
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
name|ui
operator|.
name|Label
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
name|ui
operator|.
name|Panel
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
name|ui
operator|.
name|RadioButton
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
name|ui
operator|.
name|TextBox
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
name|ui
operator|.
name|VerticalPanel
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
name|ui
operator|.
name|Widget
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
name|client
operator|.
name|VoidResult
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_class
DECL|class|NewAgreementScreen
specifier|public
class|class
name|NewAgreementScreen
extends|extends
name|AccountScreen
block|{
DECL|field|mySigned
specifier|private
name|Set
argument_list|<
name|ContributorAgreement
operator|.
name|Id
argument_list|>
name|mySigned
decl_stmt|;
DECL|field|available
specifier|private
name|List
argument_list|<
name|ContributorAgreement
argument_list|>
name|available
decl_stmt|;
DECL|field|current
specifier|private
name|ContributorAgreement
name|current
decl_stmt|;
DECL|field|radios
specifier|private
name|VerticalPanel
name|radios
decl_stmt|;
DECL|field|agreementGroup
specifier|private
name|Panel
name|agreementGroup
decl_stmt|;
DECL|field|agreementHtml
specifier|private
name|HTML
name|agreementHtml
decl_stmt|;
DECL|field|contactGroup
specifier|private
name|Panel
name|contactGroup
decl_stmt|;
DECL|field|contactPanel
specifier|private
name|ContactPanel
name|contactPanel
decl_stmt|;
DECL|field|finalGroup
specifier|private
name|Panel
name|finalGroup
decl_stmt|;
DECL|field|yesIAgreeBox
specifier|private
name|TextBox
name|yesIAgreeBox
decl_stmt|;
DECL|field|submit
specifier|private
name|Button
name|submit
decl_stmt|;
DECL|method|NewAgreementScreen ()
specifier|public
name|NewAgreementScreen
parameter_list|()
block|{
name|super
argument_list|(
name|Util
operator|.
name|C
operator|.
name|newAgreement
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onLoad ()
specifier|public
name|void
name|onLoad
parameter_list|()
block|{
if|if
condition|(
name|radios
operator|==
literal|null
condition|)
block|{
name|initUI
argument_list|()
expr_stmt|;
block|}
name|mySigned
operator|=
literal|null
expr_stmt|;
name|available
operator|=
literal|null
expr_stmt|;
name|current
operator|=
literal|null
expr_stmt|;
name|agreementGroup
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|contactGroup
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|finalGroup
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|super
operator|.
name|onLoad
argument_list|()
expr_stmt|;
name|Util
operator|.
name|ACCOUNT_SVC
operator|.
name|myAgreements
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|AgreementInfo
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
name|AgreementInfo
name|result
parameter_list|)
block|{
if|if
condition|(
name|isAttached
argument_list|()
condition|)
block|{
name|mySigned
operator|=
operator|new
name|HashSet
argument_list|<
name|ContributorAgreement
operator|.
name|Id
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|AccountAgreement
name|a
range|:
name|result
operator|.
name|accepted
control|)
block|{
name|mySigned
operator|.
name|add
argument_list|(
name|a
operator|.
name|getAgreementId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|postRPC
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|Gerrit
operator|.
name|SYSTEM_SVC
operator|.
name|contributorAgreements
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|List
argument_list|<
name|ContributorAgreement
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|List
argument_list|<
name|ContributorAgreement
argument_list|>
name|result
parameter_list|)
block|{
if|if
condition|(
name|isAttached
argument_list|()
condition|)
block|{
name|available
operator|=
name|result
expr_stmt|;
name|postRPC
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|initUI ()
specifier|private
name|void
name|initUI
parameter_list|()
block|{
specifier|final
name|FlowPanel
name|formBody
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|radios
operator|=
operator|new
name|VerticalPanel
argument_list|()
expr_stmt|;
name|formBody
operator|.
name|add
argument_list|(
name|radios
argument_list|)
expr_stmt|;
name|agreementGroup
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
name|agreementGroup
operator|.
name|add
argument_list|(
operator|new
name|SmallHeading
argument_list|(
name|Util
operator|.
name|C
operator|.
name|newAgreementReviewLegalHeading
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|agreementHtml
operator|=
operator|new
name|HTML
argument_list|()
expr_stmt|;
name|agreementHtml
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-ContributorAgreement-Legal"
argument_list|)
expr_stmt|;
name|agreementGroup
operator|.
name|add
argument_list|(
name|agreementHtml
argument_list|)
expr_stmt|;
name|formBody
operator|.
name|add
argument_list|(
name|agreementGroup
argument_list|)
expr_stmt|;
name|contactGroup
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
name|contactGroup
operator|.
name|add
argument_list|(
operator|new
name|SmallHeading
argument_list|(
name|Util
operator|.
name|C
operator|.
name|newAgreementReviewContactHeading
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|formBody
operator|.
name|add
argument_list|(
name|contactGroup
argument_list|)
expr_stmt|;
name|finalGroup
operator|=
operator|new
name|VerticalPanel
argument_list|()
expr_stmt|;
name|finalGroup
operator|.
name|add
argument_list|(
operator|new
name|SmallHeading
argument_list|(
name|Util
operator|.
name|C
operator|.
name|newAgreementCompleteHeading
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|FlowPanel
name|fp
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|yesIAgreeBox
operator|=
operator|new
name|TextBox
argument_list|()
expr_stmt|;
name|yesIAgreeBox
operator|.
name|setVisibleLength
argument_list|(
name|Util
operator|.
name|C
operator|.
name|newAgreementIAGREE
argument_list|()
operator|.
name|length
argument_list|()
operator|+
literal|8
argument_list|)
expr_stmt|;
name|yesIAgreeBox
operator|.
name|setMaxLength
argument_list|(
name|Util
operator|.
name|C
operator|.
name|newAgreementIAGREE
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|yesIAgreeBox
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
operator|new
name|InlineLabel
argument_list|(
name|Util
operator|.
name|M
operator|.
name|enterIAGREE
argument_list|(
name|Util
operator|.
name|C
operator|.
name|newAgreementIAGREE
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|finalGroup
operator|.
name|add
argument_list|(
name|fp
argument_list|)
expr_stmt|;
name|submit
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonSubmitNewAgreement
argument_list|()
argument_list|)
expr_stmt|;
name|submit
operator|.
name|addClickListener
argument_list|(
operator|new
name|ClickListener
argument_list|()
block|{
specifier|public
name|void
name|onClick
parameter_list|(
specifier|final
name|Widget
name|sender
parameter_list|)
block|{
name|doSign
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|finalGroup
operator|.
name|add
argument_list|(
name|submit
argument_list|)
expr_stmt|;
name|formBody
operator|.
name|add
argument_list|(
name|finalGroup
argument_list|)
expr_stmt|;
operator|new
name|TextSaveButtonListener
argument_list|(
name|yesIAgreeBox
argument_list|,
name|submit
argument_list|)
expr_stmt|;
specifier|final
name|FormPanel
name|form
init|=
operator|new
name|FormPanel
argument_list|()
decl_stmt|;
name|form
operator|.
name|add
argument_list|(
name|formBody
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|form
argument_list|)
expr_stmt|;
block|}
DECL|method|postRPC ()
specifier|private
name|void
name|postRPC
parameter_list|()
block|{
if|if
condition|(
name|mySigned
operator|!=
literal|null
operator|&&
name|available
operator|!=
literal|null
condition|)
block|{
name|display
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|display ()
specifier|private
name|void
name|display
parameter_list|()
block|{
name|current
operator|=
literal|null
expr_stmt|;
name|agreementGroup
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|contactGroup
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|finalGroup
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|radios
operator|.
name|clear
argument_list|()
expr_stmt|;
specifier|final
name|SmallHeading
name|hdr
init|=
operator|new
name|SmallHeading
argument_list|()
decl_stmt|;
if|if
condition|(
name|available
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|hdr
operator|.
name|setText
argument_list|(
name|Util
operator|.
name|C
operator|.
name|newAgreementNoneAvailable
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hdr
operator|.
name|setText
argument_list|(
name|Util
operator|.
name|C
operator|.
name|newAgreementSelectTypeHeading
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|radios
operator|.
name|add
argument_list|(
name|hdr
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|ContributorAgreement
name|cla
range|:
name|available
control|)
block|{
specifier|final
name|RadioButton
name|r
init|=
operator|new
name|RadioButton
argument_list|(
literal|"cla_id"
argument_list|,
name|cla
operator|.
name|getShortName
argument_list|()
argument_list|)
decl_stmt|;
name|r
operator|.
name|addStyleName
argument_list|(
literal|"gerrit-ContributorAgreement-Button"
argument_list|)
expr_stmt|;
name|radios
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
name|mySigned
operator|.
name|contains
argument_list|(
name|cla
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|r
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|Label
name|l
init|=
operator|new
name|Label
argument_list|(
name|Util
operator|.
name|C
operator|.
name|newAgreementAlreadySubmitted
argument_list|()
argument_list|)
decl_stmt|;
name|l
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-ContributorAgreement-AlreadySubmitted"
argument_list|)
expr_stmt|;
name|radios
operator|.
name|add
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|r
operator|.
name|addClickListener
argument_list|(
operator|new
name|ClickListener
argument_list|()
block|{
specifier|public
name|void
name|onClick
parameter_list|(
specifier|final
name|Widget
name|sender
parameter_list|)
block|{
name|showCLA
argument_list|(
name|cla
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cla
operator|.
name|getShortDescription
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|cla
operator|.
name|getShortDescription
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
specifier|final
name|Label
name|l
init|=
operator|new
name|Label
argument_list|(
name|cla
operator|.
name|getShortDescription
argument_list|()
argument_list|)
decl_stmt|;
name|l
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-ContributorAgreement-ShortDescription"
argument_list|)
expr_stmt|;
name|radios
operator|.
name|add
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|doSign ()
specifier|private
name|void
name|doSign
parameter_list|()
block|{
name|submit
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|current
operator|==
literal|null
operator|||
operator|!
name|Util
operator|.
name|C
operator|.
name|newAgreementIAGREE
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|yesIAgreeBox
operator|.
name|getText
argument_list|()
argument_list|)
condition|)
block|{
name|yesIAgreeBox
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|yesIAgreeBox
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|contactGroup
operator|.
name|isVisible
argument_list|()
condition|)
block|{
name|contactPanel
operator|.
name|doSave
argument_list|()
expr_stmt|;
block|}
name|Util
operator|.
name|ACCOUNT_SEC
operator|.
name|enterAgreement
argument_list|(
name|current
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|VoidResult
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|VoidResult
name|result
parameter_list|)
block|{
name|History
operator|.
name|newItem
argument_list|(
name|Link
operator|.
name|SETTINGS_AGREEMENTS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
specifier|final
name|Throwable
name|caught
parameter_list|)
block|{
name|yesIAgreeBox
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|super
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|showCLA (final ContributorAgreement cla)
specifier|private
name|void
name|showCLA
parameter_list|(
specifier|final
name|ContributorAgreement
name|cla
parameter_list|)
block|{
name|current
operator|=
name|cla
expr_stmt|;
name|String
name|url
init|=
name|cla
operator|.
name|getAgreementUrl
argument_list|()
decl_stmt|;
if|if
condition|(
name|url
operator|!=
literal|null
operator|&&
name|url
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|agreementGroup
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|agreementHtml
operator|.
name|setText
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|rpcStatusLoading
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|url
operator|.
name|startsWith
argument_list|(
literal|"http:"
argument_list|)
operator|&&
operator|!
name|url
operator|.
name|startsWith
argument_list|(
literal|"https:"
argument_list|)
condition|)
block|{
name|url
operator|=
name|GWT
operator|.
name|getModuleBaseURL
argument_list|()
operator|+
name|url
expr_stmt|;
block|}
specifier|final
name|RequestBuilder
name|rb
init|=
operator|new
name|RequestBuilder
argument_list|(
name|RequestBuilder
operator|.
name|GET
argument_list|,
name|url
argument_list|)
decl_stmt|;
name|rb
operator|.
name|setCallback
argument_list|(
operator|new
name|RequestCallback
argument_list|()
block|{
specifier|public
name|void
name|onError
parameter_list|(
name|Request
name|request
parameter_list|,
name|Throwable
name|exception
parameter_list|)
block|{
operator|new
name|ErrorDialog
argument_list|(
name|exception
argument_list|)
operator|.
name|center
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|onResponseReceived
parameter_list|(
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
block|{
specifier|final
name|String
name|ct
init|=
name|response
operator|.
name|getHeader
argument_list|(
literal|"Content-Type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getStatusCode
argument_list|()
operator|==
literal|200
operator|&&
name|ct
operator|!=
literal|null
operator|&&
operator|(
name|ct
operator|.
name|equals
argument_list|(
literal|"text/html"
argument_list|)
operator|||
name|ct
operator|.
name|startsWith
argument_list|(
literal|"text/html;"
argument_list|)
operator|)
condition|)
block|{
name|agreementHtml
operator|.
name|setHTML
argument_list|(
name|response
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|new
name|ErrorDialog
argument_list|(
name|response
operator|.
name|getStatusText
argument_list|()
argument_list|)
operator|.
name|center
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|rb
operator|.
name|send
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RequestException
name|e
parameter_list|)
block|{
operator|new
name|ErrorDialog
argument_list|(
name|e
argument_list|)
operator|.
name|show
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|agreementGroup
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|contactPanel
operator|==
literal|null
operator|&&
name|cla
operator|.
name|isRequireContactInformation
argument_list|()
condition|)
block|{
name|contactPanel
operator|=
operator|new
name|ContactPanel
argument_list|()
expr_stmt|;
name|contactPanel
operator|.
name|hideSaveButton
argument_list|()
expr_stmt|;
name|contactGroup
operator|.
name|add
argument_list|(
name|contactPanel
argument_list|)
expr_stmt|;
block|}
name|contactGroup
operator|.
name|setVisible
argument_list|(
name|cla
operator|.
name|isRequireContactInformation
argument_list|()
argument_list|)
expr_stmt|;
name|finalGroup
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|yesIAgreeBox
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|submit
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

